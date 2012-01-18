// =============================================================================
// CHANGELOG:
//++ 181 MW 2001.08.09
// Added the getAllApplicationNames() which returns a vector of all the names
// of the applications in the current Maui Environment.
// =============================================================================


package com.bitmovers.maui.engine;

import java.util.Properties;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipFile;
import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import sun.misc.URLClassPath;
import sun.misc.Resource;
import com.bitmovers.maui.profiler.Profiler;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.I_SiteInitializer;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.MauiRuntimeEngine;
import com.bitmovers.maui.components.MDesktop;
import com.bitmovers.maui.engine.httpserver.HTTPSession;
import com.bitmovers.maui.engine.httpserver.I_SessionListener;
import com.bitmovers.maui.engine.httpserver.SessionEvent;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.utilities.PropertiesLoader;


// ======================================================================
// CLASS: ApplicationManager                   (c) 2001 Bitmovers Systems
// ======================================================================

/** This object has two primary functions.  Firstly, during 
  * initialization, this manager will scan jar files within the Maui 
  * applications folder for <code>MauiApplication</code> classes.  It 
  * then creates objects which represent each of these applications.
  * 
  * During runtime, the ApplicationManager creates MauiApplication 
  * objects on demand.
  *
  */
  
public class ApplicationManager
	implements I_SessionListener,
			   Runnable
{
	
	
	//
	//	If the ClassLoader is shared, then a common will be created here, and used
	//	by all of the ApplicationSuites.
	//
	protected MauiClassLoader extensionsClassLoader = new MauiClassLoader ();
	protected ServerConfigurationManager scm;
	protected String applicationLocation;
	protected String workLocation;
	protected String extensionLocation;
	protected FolderInformation folderInformation = null;
	protected boolean autoReload = false;
	protected Vector extensionsLoaded = new Vector ();
	//protected Hashtable applications = new Hashtable (20);
	//protected Hashtable applicationNames = new Hashtable (20);
	
	private static final ApplicationManager am = new ApplicationManager ();
	
	private String defaultApplicationClassName;
	private String defaultFolder;
	private FolderInformation defaultFolderInformation;
	private String internalApplicationClassName;
	private boolean initDone = false;
	private Map folders = new TreeMap ();
	private Hashtable sessions = new Hashtable ();
	private Hashtable initializers = new Hashtable (5);
	private int applicationScanTime;
	private boolean firstTime = true;
	
	private Vector listeners = new Vector ();
	private Hashtable suiteReferences = new Hashtable (10);
	private Hashtable lowerCase = new Hashtable (50);
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: SessionInformation
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This inner class contains information about application/session cross 
	  * references.
	  * 
	  */
	  
	class SessionInformation
	{
		
		
		protected final Vector sessions = new Vector ();
		protected final MauiApplication mauiApplication;
		
		
		// --------------------------------------------------------------------
		// CONSTRUCTOR
		// --------------------------------------------------------------------
		
		SessionInformation (MauiApplication aMauiApplication)
		{
			mauiApplication = aMauiApplication;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getMauiApplication
		// --------------------------------------------------------------------
		
		protected MauiApplication getMauiApplication ()
		{
			return mauiApplication;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getSessions
		// --------------------------------------------------------------------
		
		protected Vector getSessions ()
		{
			return sessions;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: toArray
		// --------------------------------------------------------------------
		
		protected HTTPSession [] toArray ()
		{
			HTTPSession [] retVal = new HTTPSession [sessions.size ()];
			Object [] theSessions = sessions.toArray ();
			for (int i = 0; i < theSessions.length; i++)
			{
				retVal [i] = (HTTPSession) theSessions [i];
			}
			return retVal;
		}
		
		/**
		* Return a count of the number of sessions in the vector
		*
		* @return The number of sessions running this application
		*/
		public int size ()
		{
			return sessions.size ();
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: isEmpty
		// --------------------------------------------------------------------
		
		protected boolean isEmpty ()
		{
			return (sessions.size () == 0);
		}
			
		
		// --------------------------------------------------------------------
		// METHOD: addSession
		// --------------------------------------------------------------------
		
		protected void addSession (HTTPSession aSession)
		{
			if (!sessions.contains (aSession))
			{
				sessions.addElement (aSession);
			}
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: removeSession
		// --------------------------------------------------------------------
		
		protected void removeSession (HTTPSession aSession)
		{
			if (sessions.contains (aSession))
			{
				sessions.removeElement (aSession);
			}
		}
		
		
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: FolderInformation
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This inner class is used for grouping application suites by folder.
	  * ApplicationSuites can be grouped under folders
	  *
	  */
	  
	class FolderInformation
	{
		protected final String folder;
		protected final TreeMap suites = new TreeMap ();
		protected final TreeMap applications = new TreeMap ();
		protected final Map applicationNames = new TreeMap ();
		
		
		// --------------------------------------------------------------------
		// CONSTRUCTOR
		// --------------------------------------------------------------------
		
		protected FolderInformation (String aFolder)
		{
			folder = aFolder;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: addApplicationSuite
		// --------------------------------------------------------------------
		
		protected void addApplicationSuite (ApplicationSuite aApplicationSuite)
		{
			suites.put (aApplicationSuite.getName (), aApplicationSuite);
		}
		
		protected void removeApplicationSuite (ApplicationSuite aApplicationSuite)
		{
			suites.remove (aApplicationSuite.getName ());
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getSuite
		// --------------------------------------------------------------------
		
		/** Given an ApplicationSuite name get the ApplicationSuite name
		  *
		  * @param aSuiteName The application suite name
		  *
		  * @return The ApplicationSuite
		  *
		  */
		  
		protected ApplicationSuite getSuite (String aSuiteName)
		{
			int theIndex = aSuiteName.indexOf (".jar");
			String theLookupKey = (theIndex == -1 ? aSuiteName: aSuiteName.substring (0, theIndex));
			TreeMap theSuites = suites;
			Object retVal = theSuites.get (theLookupKey);
			return (ApplicationSuite) retVal;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getFolderName
		// --------------------------------------------------------------------
		
		protected String getFolderName ()
		{
			return folder;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getAllSuites
		// --------------------------------------------------------------------
		
		/** Get a sorted array of all of the ApplicationSuites within the folder
		  *
		  * @return An array of the ApplicationSuites contained within the folder
		  *
		  */
		  
		protected ApplicationSuite [] getAllSuites ()
		{
			ApplicationSuite [] retVal = new ApplicationSuite [suites.size ()];
			Iterator theValues = suites.values ().iterator ();
			int i = 0;
			while (theValues.hasNext ())
			{
				Object theValue = theValues.next ();
				retVal [i++] = (ApplicationSuite) theValue;
			}
			return retVal;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getApplicationSuiteNames
		// --------------------------------------------------------------------
		
		/** Get a sorted list of all of the application suites
		  *
		  * @return The sorted String array
		  *
		  */
		  
		protected String [] getApplicationSuiteNames ()
		{
			ApplicationSuite [] theSuites = getAllSuites ();
			String [] retVal = new String [theSuites.length];
			for (int i = 0; i < retVal.length; i++)
			{
				retVal [i] = theSuites [i].getName ();
			}
			return retVal;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: addApplication
		// --------------------------------------------------------------------
		
		protected void addApplication (MauiApplication aMauiApplication)
		{
			applications.put (aMauiApplication.getName(), aMauiApplication);
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getApplicationName
		// --------------------------------------------------------------------
		
		protected String getApplicationName (String aClassName)
		{
			return (String) applicationNames.get (getClassName (aClassName));
		}
	
		
		// --------------------------------------------------------------------
		// METHOD: getAllApplicationNames
		// --------------------------------------------------------------------
		
		protected String [] getAllApplicationNames ()
		{
			String [] retVal = new String [applications.size ()];
			Iterator theValues = applicationNames.values ().iterator ();
			int i = 0;
			while (theValues.hasNext ())
			{
				retVal [i++] = (String) theValues.next ();
			}
			return retVal;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getWrapper
		// --------------------------------------------------------------------
		
		protected ApplicationClassWrapper getWrapper (String aClassName)
		{
			return (ApplicationClassWrapper) applications.get (getClassName (aClassName));
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getClassName
		// --------------------------------------------------------------------
		
		protected String getClassName (String aClassName)
		{
			int theSlash = aClassName.lastIndexOf ("/");
			return (theSlash == -1 ? aClassName : aClassName.substring (theSlash + 1));
		}
		
		
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: ApplicationClassWrapper
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** Inner class for maintaining cross-reference between a MauiApplication Class
	  * and the ApplicationSuite it belongs to.
	  *
	  */
	  
	class ApplicationClassWrapper
	{
		
		
		protected Class applicationClass;
		protected ApplicationSuite applicationSuite;
		protected Properties properties = null;
		
		
		// --------------------------------------------------------------------
		// CONSTRUCTOR
		// --------------------------------------------------------------------
		
		ApplicationClassWrapper (Class aApplicationClass,
								 ApplicationSuite aApplicationSuite,
								 Properties aProperties)
		{
			applicationClass = aApplicationClass;
			applicationSuite = aApplicationSuite;
			properties = aProperties;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getApplicationClass
		// --------------------------------------------------------------------
		
		protected Class getApplicationClass ()
		{
			return applicationClass;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getApplicationSuite
		// --------------------------------------------------------------------
		
		protected ApplicationSuite getApplicationSuite ()
		{
			return applicationSuite;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getProperties
		// --------------------------------------------------------------------
		
		protected Properties getProperties ()
		{
			return properties;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: createFolder
		// --------------------------------------------------------------------
		
		private void createFolder (String aLocation)
		{
			File theFile = new File (aLocation);
			if (!theFile.exists ())
			{
				theFile.mkdirs ();
			}
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: instantiateApplication
		// --------------------------------------------------------------------
		
		/** Instantiate the application
		  *
		  * @param aInitializer The I_ApplicationInitializer callback object
		  *
		  * @return The instantiated application
		  *
		  */
		  
		protected MauiApplication instantiateApplication (I_ApplicationInitializer aInitializer)
			throws NoSuchMethodException,
				   SecurityException,
				   InstantiationException,
				   InvocationTargetException,
				   IllegalAccessException
		{
			Constructor theConstructor = applicationClass.getConstructor (new Class [] {Object.class});
			return (MauiApplication) theConstructor.newInstance (new Object [] {aInitializer});
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: createWorkSpace
		// --------------------------------------------------------------------
		
		/** Create the work space for this application
		  *
		  * @param aApplicationClassWrapper The ApplicationClassWrapper which describes the application
		  *
		  */
		  
		protected void createWorkSpace ()
		{
			createFolder (properties.getProperty (MauiApplication.MAUI_USER_DIR));
		}
		
		
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: ApplicationSuite
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This class is a representation of a single jar file.  Since it is possible for
	  * several MauiApplication classes to be contained within a single jar file, this
	  * functions largely as a placeholder to group these classes together.
	  *
	  */
	  
	class ApplicationSuite
	{
		
		
		protected MauiClassLoader classLoader;
		protected String name;
		protected String location;
		protected String fullyQualifiedLocation;
		protected TreeMap suiteApplications = new TreeMap ();
		protected Properties suiteProperties = null;
		private long lastModified;
		private String jarFileName;
		private FolderInformation folderInformation;
		private boolean firstLoad = true;
		
		
		// --------------------------------------------------------------------
		// CONSTRUCTOR
		// --------------------------------------------------------------------
		
		ApplicationSuite (final String aLocation,
						  final String aJarFileName,
						  final FolderInformation aFolderInformation)
			throws IOException
		{
			location = aLocation;
			int thePeriod = aJarFileName.indexOf (".jar");
			name = aJarFileName;
			jarFileName = aJarFileName;
			folderInformation = aFolderInformation;
			if (thePeriod != -1)
			{
				name = name.substring (0, thePeriod);	// Remove ".jar"
				getFullyQualifiedLocation ();
				load ();
			}
		}
		
		protected void reload ()
			throws IOException
		{
			unload ();
			load ();
		}
		
		protected void unload ()
		{
			suiteProperties = null;
			Object [] theSuiteApplications = suiteApplications.values ().toArray ();
			for (int i = 0; i < theSuiteApplications.length; i++)
			{
				mauiApplicationAction ((Class) theSuiteApplications [i], false);
			}
			ResourceManager.getInstance ().removeResourceFile (fullyQualifiedLocation + File.separator + name + ".jar");
			/*try
			{
				classLoader.removeJarFile (new File (getFullyQualifiedLocation (), jarFileName));
			}
			catch (IOException e)
			{
				System.err.println ("[ApplicationSuite] unload exception: " + e);
			}*/
		}
		
		protected void load ()
			throws IOException
		{
			//
			//	Use either a private ClassLoader, or the shared one
			//
			classLoader = new MauiClassLoader (extensionsClassLoader);
			
			//
			// Look for suite level properties.  These are properties to be applied to all
			//	applications objects which originate from this application suite.
			//
			suiteProperties = scm.loadPropertiesFromFolder (getName (),
															location,
															null);
			
			//
			//	Add the jar file to the ClassLoader.  If this is shared then it means that this jar file
			//	will be added to a list of files.
			//
			File theJarFile = new File (fullyQualifiedLocation, jarFileName);
			lastModified = theJarFile.lastModified ();
			firstLoad = true;
			if (!firstTime)
			{
				try
				{
					ResourceManager.getInstance ().addResourceFile (new File (fullyQualifiedLocation + File.separator + name + ".jar"));
				}
				catch (Exception e)
				{
				}
			}
			classLoader.addJarFile (new File (fullyQualifiedLocation, jarFileName),
									new I_JarCallback ()
									{
										public void mauiAppFound (Class aMauiApplication)
										{
											addMauiApplication (aMauiApplication);
										}
										
										public void initializerFound (Class aInitializer)
										{
											addInitializer (aInitializer);
										}
									});
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getApplicationNames
		// --------------------------------------------------------------------
		
		protected String [] getApplicationNames ()
		{
			String [] retVal = new String [suiteApplications.size ()];
			Iterator theSuiteApplications = suiteApplications.values ().iterator ();
			int i = 0;
			while (theSuiteApplications.hasNext ())
			{
				retVal [i++] = ((Class) theSuiteApplications.next ()).getName ();
			}
			return retVal;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getClassLoader
		// --------------------------------------------------------------------
		
		protected MauiClassLoader getClassLoader ()
		{
			return classLoader;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getProperties
		// --------------------------------------------------------------------
		
		protected Properties getProperties ()
		{
			return suiteProperties;
		}
		
		/**
		* Get the last modified date for jar file
		*
		* @return The last modified date
		*/
		protected long getLastModified ()
		{
			return lastModified;
		}
		
		/**
		* Check if the latest jar file in the folder is out of date
		*
		* @return Boolean indicating if it is out of date or not
		*/
		protected boolean isOutOfDate ()
		{
			File theFile = new File (fullyQualifiedLocation, jarFileName);
			return (lastModified < theFile.lastModified ());
		}
		
		protected void setOutOfDate ()
		{
			lastModified = (long) 0;
		}
		
		// --------------------------------------------------------------------
		// METHOD: loadPropertiesFromResource
		// --------------------------------------------------------------------
		
		private boolean loadPropertiesFromResource (String aResourceName, Properties aProperties)
			throws IOException
		{
			InputStream thePropertiesStream = getClassLoader ().findResourceStream (aResourceName + ".properties");
			if (thePropertiesStream != null)
			{
				aProperties.load (thePropertiesStream);
				try
				{
					thePropertiesStream.close ();
				}
				catch (IOException e)
				{
				}
			}
			return (thePropertiesStream == null ? false : true);
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getApplicationProperties
		// --------------------------------------------------------------------
		
		private Properties getApplicationProperties (final String aAppName, final String aClassName)
		{
			Properties retVal = (Properties) getProperties ().clone ();
			int thePeriod = aAppName.indexOf (".");
			if (thePeriod != -1)
			{
				scm.loadPropertiesFromFolder (aAppName.substring (0, thePeriod), location, retVal);
			}
			scm.loadPropertiesFromFolder (aAppName, location, retVal);
			scm.loadPropertiesFromFolder (aClassName, location, retVal);
			thePeriod = aClassName.lastIndexOf (".");
			scm.loadPropertiesFromFolder ((thePeriod != -1 ?
			                                    aClassName.substring (thePeriod + 1) :
			                                    aClassName),
			                              location,
			                              retVal);
			try
			{
				if (!loadPropertiesFromResource (aAppName, retVal))
				{
					try
					{
						loadPropertiesFromResource (aClassName, retVal);
					}
					catch (IOException e)
					{
					}
				}
			}
			catch (IOException e)
			{
				try
				{
					loadPropertiesFromResource (aClassName, retVal);
				}
				catch (IOException e2)
				{
				}
			}
			return retVal;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: addMauiApplication
		// --------------------------------------------------------------------
		
		/** Add a MauiApplication to the ApplicationSuite.  This method also 
		  * takes responsible for cataloguing the MauiApplication. This 
		  * includes determining the shortest, unambiguous name for each 
		  * application.
		  *
		  * @param aMauiApplication The MauiApplication Class
		  *
		  */
		  
		protected void addMauiApplication (final Class aMauiApplication)
		{
			mauiApplicationAction (aMauiApplication, true);
		}
		
		protected void mauiApplicationAction (final Class aMauiApplication, boolean aAdd)
		{
			String theAppClassName = aMauiApplication.getName ();
			String theFullClassName = theAppClassName;
			
			int thePeriod = theAppClassName.lastIndexOf (".");
			if (thePeriod != -1)
			{
				theAppClassName = theAppClassName.substring (thePeriod + 1);
			}
			
			String theAppName = getName () + "." + theAppClassName;
			ApplicationClassWrapper theWrapper;
			
			if (firstLoad)
			{
				try
				{
					loadPropertiesFromResource (getName (), suiteProperties);
				}
				catch (IOException e)
				{
				}
				firstLoad = false;
			}
			
			if (aAdd)
			{
				suiteApplications.put (theAppClassName,
									   aMauiApplication);
				theWrapper = new ApplicationClassWrapper (aMauiApplication,
						  							      ApplicationSuite.this,
						  							      getApplicationProperties (theAppName,
						  							      							theFullClassName));
				StringBuffer theWorkLocation = new StringBuffer (workLocation);
				if (!location.equals (""))
				{
					theWorkLocation.append (File.separator);
					theWorkLocation.append (location);
				}
				theWorkLocation.append (File.separator);
				String theClassName = theWrapper.getApplicationClass ().getName ();
				theWorkLocation.append (theClassName.replace ('.', File.separatorChar));
				Properties theProperties = theWrapper.getProperties ();
				String theWorkFolderName = theWorkLocation.toString ();
				theProperties.put (MauiApplication.MAUI_USER_DIR, theWorkFolderName);
				int theDot = theClassName.lastIndexOf ('.');
				theClassName = (theDot == -1 ? theClassName : theClassName.substring (theDot + 1));
				int theSlash = theClassName.lastIndexOf (File.separatorChar);
				theClassName = (theSlash == -1 ? theClassName : theClassName.substring (theSlash + 1));
				theProperties.put ("maui." + theClassName + ".working.folder", theWorkFolderName);
				if (folderInformation.applications.get (theAppClassName) != null)
				{
					System.err.println ("[Application Manager] MauiApplication " + theFullClassName +
										" was already defined in " + theWrapper.getApplicationSuite ().getName ());
				}
				else
				{
					folderInformation.applications.put (theAppClassName, theWrapper);
					folderInformation.applicationNames.put (theAppClassName, theFullClassName);
				}
				
				//Properties theProperties = (Properties) getProperties ().clone ();
				folderInformation.applications.put (theAppName, theWrapper);
				folderInformation.applicationNames.put (theAppName, theFullClassName);
				folderInformation.applications.put (theFullClassName, theWrapper);
				
				if (theFullClassName.equals (defaultApplicationClassName))
				{
					String theDefaultName = "default." + theAppClassName;
					folderInformation.applications.put (theDefaultName, theWrapper);
					folderInformation.applicationNames.put (theDefaultName, theFullClassName); 
				}
				
			}
			else
			{
				suiteApplications.remove (theAppClassName);
				theWrapper = getApplicationClassWrapper (location, theAppClassName);
				folderInformation.applications.remove (theAppClassName);
				folderInformation.applicationNames.remove (theAppClassName);
				folderInformation.applications.remove (theAppName);
				folderInformation.applicationNames.remove (theAppName);
				folderInformation.applications.remove (theFullClassName);
				
				if (theFullClassName.equals (defaultApplicationClassName))
				{
					String theDefaultName = "default." + theAppClassName;
					folderInformation.applications.remove (theDefaultName);
					folderInformation.applicationNames.remove (theDefaultName); 
				}
				
			}
			
								  
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: addInitializer
		// --------------------------------------------------------------------
		
		protected void addInitializer (Class aInitializer)
		{
			initializers.put (aInitializer.getName (), aInitializer);
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getName
		// --------------------------------------------------------------------
		
		protected String getName ()
		{
			return name;
		}
		
		public String getJarFileName ()
		{
			return jarFileName;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getLocation
		// --------------------------------------------------------------------
		
		protected String getLocation ()
		{
			return location;
		}
		
		/**
		* Get the fully qualified location
		*
		* @return The fully qualified location
		*/
		protected void getFullyQualifiedLocation ()
		{
			StringBuffer theLocation = new StringBuffer (applicationLocation);
			if (location != null && location.length () > 0)
			{
				theLocation.append (File.separator);
				theLocation.append (location);
			}
			fullyQualifiedLocation =  theLocation.toString ();
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getFullName
		// --------------------------------------------------------------------
		
		protected String getFullName ()
		{
			return (location.length () > 0 ? location + "/" : "") + name;
		}
	}

	// ----------------------------------------------------------------------
	// STATIC METHOD: getInstance
	// ----------------------------------------------------------------------
	
	public static ApplicationManager getInstance ()
	{
		return am;
	}
	
	// --------------------------------------------------------------------
	// METHOD: addApplicationSuite
	// --------------------------------------------------------------------
	public ClassLoader getExtensionsClassLoader ()
	{
		return extensionsClassLoader;
	}
	
	
	
	/*
	// --------------------------------------------------------------------
	// METHOD: addApplicationSuite
	// --------------------------------------------------------------------
	
	private void addApplicationSuite (String aSuiteName, String aClassName)
	{
		try
		{
			ApplicationSuite theSuite = new ApplicationSuite (aSuiteName);	//	Load up the default.
			theSuite.addMauiApplication (Class.forName (aClassName));
			suites.put (theSuite.getName (), theSuite);
		}
		catch (Exception e)
		{
		}
	}*/
	
	private String [] getJarFiles (File aLocation)
	{
		String [] theJarFiles =
			aLocation.list (new FilenameFilter ()
								{
									public boolean accept (File aDir, String aName)
									{
										return aName.toLowerCase ().endsWith (".jar");
									}
								});
		return theJarFiles;
	}
	
	public void addApplicationManagerListener (ApplicationManagerListener aListener)
	{
		if (!listeners.contains (aListener))
		{
			listeners.addElement (aListener);
		}
	}
	
	public void removeApplicationManagerListener (ApplicationManagerListener aListener)
	{
		if (listeners.contains (aListener))
		{
			listeners.removeElement (aListener);
		}
	}
	
	protected void notifyManagerListeners (Object aObjectSource, String aName, int aAction)
	{
		if (listeners.size () > 0)
		{
			ApplicationManagerEvent theEvent = 
				new ApplicationManagerEvent (aObjectSource, aName);
			Enumeration theListeners = listeners.elements ();
			while (theListeners.hasMoreElements ())
			{
				ApplicationManagerListener theListener =
					(ApplicationManagerListener) theListeners.nextElement ();
				switch (aAction)
				{
					case (ApplicationManagerListener.LOADED) :
						theListener.applicationSuiteLoaded (theEvent);
						break;
						
					case (ApplicationManagerListener.UNLOADED) :
						theListener.applicationSuiteUnloaded (theEvent);
						break;

					case (ApplicationManagerListener.RELOADED) :
						theListener.applicationSuiteReloaded (theEvent);
						break;

					case (ApplicationManagerListener.EXTENSIONS) :
						theListener.extensionsLoaded (theEvent);
						break;
				}

			}
		}
	}
		
	/**
	* Scan the extensions folder, and add any extensions that haven't already been defined
	*/
	private void scanForExtensions ()
	{
		extensionsClassLoader.setLoadAllProperties (true);
		File theExtensionsFolder = new File (extensionLocation);
		if (!theExtensionsFolder.exists ())
		{
			theExtensionsFolder.mkdir ();
		}
		if (theExtensionsFolder.exists () &&
			theExtensionsFolder.isDirectory ())
		{
			boolean theLoadAllExtensions = false;
			String [] theJarFiles = getJarFiles (theExtensionsFolder);
			if (theJarFiles.length > 0)
			{
				for (int i = 0; i < theJarFiles.length; i++)
				{
					if (!extensionsLoaded.contains (theJarFiles [i]))
					{
						try
						{
							extensionsClassLoader.addJarFile (new File (theExtensionsFolder, theJarFiles [i]), null);
							theLoadAllExtensions = true;
						}
						catch (IOException e)
						{
						}
						extensionsLoaded.addElement (theJarFiles [i]);
					}
				}
				if (autoReload && theLoadAllExtensions)
				{
					extensionsClassLoader.loadAllExtensions ();
				}
			}
		}
		
		notifyManagerListeners (this,
								"Extensions",
								ApplicationManagerListener.EXTENSIONS);
	}
	
	/**
	* Go through the list of ApplicationSuite objects and prune out any that no longer
	* physically exist.
	*
	* @param aFolder The FolderInformation object
	* @param aJarFiles Up-to-date list of jar file names found within the folder
	*/
	private void scanForDeletedSuites (FolderInformation aFolder, String [] aJarFiles)
	{
		Vector theJarFiles = new Vector (aJarFiles.length);
		for (int i = 0; i < aJarFiles.length; i++)
		{
			int thePeriod = aJarFiles [i].toLowerCase ().indexOf (".jar");
			if (thePeriod != -1)
			{
				theJarFiles.addElement (aJarFiles [i].substring (0, thePeriod));
			}
		}
		
		Object [] theSuites = aFolder.suites.values ().toArray ();
		for (int i = 0; i < theSuites.length; i++)
		{
			ApplicationSuite theSuite = (ApplicationSuite) theSuites [i];
			if (!theJarFiles.contains (theSuite.getName ()))
			{
				theSuite.setOutOfDate ();
				try
				{
					isOutOfDate (theSuite, true);
				}
				catch (IOException e)
				{
				}
				aFolder.removeApplicationSuite (theSuite);
				notifyManagerListeners (theSuite, theSuite.getName (), ApplicationManagerListener.UNLOADED);
			}
		}
	}
	
	/**
	* Go through the list of FolderInformation objects and prune out any that
	* no longer physically exist.
	*
	* @param aLocation The folder being scanned
	* @param aFolders Up-to-date list of folders for this location
	*/
	private void scanForDeletedFolders (File aLocation, File [] aFolders)
	{
		FolderInformation [] theFolders = getFolders ();
		HashMap theFoldersMap = new HashMap (theFolders.length);
		String theLocationName = aLocation.getName ();
		if (theLocationName.startsWith (applicationLocation))
		{
			theLocationName = theLocationName.substring (applicationLocation.length ());
		}
		for (int i = 0; i < theFolders.length; i++)
		{
			String theFolderName = theFolders [i].getFolderName ();
			if (theFolderName.startsWith (theLocationName))
			{
				theFolderName = theFolderName.substring (theLocationName.length ());
				if (theFolderName.length () > 0)
				{
					theFoldersMap.put (theFolderName, theFolders [i]);
				}
			}
		}
		
		for (int i = 0; i < aFolders.length; i++)
		{
			theFoldersMap.remove (aFolders [i].getName ());
		}
		
		Object [] theRemainder = theFoldersMap.values ().toArray ();
		for (int i = 0; i < theRemainder.length; i++)
		{
			scanForDeletedSuites ((FolderInformation) theRemainder [i], new String [0]);
			folders.remove (((FolderInformation) theRemainder [i]).getFolderName ());
		}
		
	}
	
	// ----------------------------------------------------------------------
	// METHOD: buildApplicationSuites
	// ----------------------------------------------------------------------
	
	/** This method takes responsibility for loading all of the ApplicationSuites (ie. jar files) within a particular
	  * folder.  This includes locating all jar files within the folder, scanning each jar file for MauiApplications (ie.
	  * classes which extend the MauiApplicaition class), creating descriptions for them (ApplicationClassWrappers), and
	  * cataloguing them.
	  *
	  * @param aLocation The folder to scan
	  *
	  */
	  
	private void buildApplicationSuites (final File aLocation)
	{
		//addApplicationSuite ("default", defaultApplicationClassName);
		//addApplicationSuite ("maui", internalApplicationClassName);
		
		if (aLocation.exists ())
		{
			String theUseLocation = aLocation.toString ();
			int theIndex = theUseLocation.indexOf (applicationLocation) + applicationLocation.length ();
			theUseLocation = theUseLocation.substring (theIndex);
			if (theUseLocation.startsWith (File.separator))
			{
				theUseLocation = theUseLocation.substring (1);
			}
			
			String [] theJarFiles = getJarFiles (aLocation);
			String theFolderLocation = theUseLocation.replace (File.separatorChar, '/');
			folderInformation = (FolderInformation) folders.get (theFolderLocation);
			if (folderInformation == null)
			{
				folderInformation = new FolderInformation (theUseLocation);
				folders.put (theFolderLocation, folderInformation);
			}
			
			String theJarFile;
			if (theJarFiles.length > 0)
			{
				TreeMap theSuites = folderInformation.suites;
				
				for (int i = 0; i < theJarFiles.length; i++)
				{
					try
					{
						int thePeriod = theJarFiles [i].indexOf (".jar");
						if (thePeriod != -1)
						{
							String theSuiteName = theJarFiles [i].substring (0, thePeriod);
							ApplicationSuite theSuite;
							if ((theSuite = (ApplicationSuite) theSuites.get (theSuiteName)) == null)
							{
								theSuite = new ApplicationSuite (theUseLocation,
															     theJarFiles [i],
															     folderInformation);
								System.out.println (new InfoString ("Loading " + theSuite.getFullName () + "."));
								theSuites.put (theSuite.getName (), theSuite);
								notifyManagerListeners (theSuite, theSuite.getFullName (), ApplicationManagerListener.LOADED);
							}
							else if (theSuite.isOutOfDate ());
							{
								isOutOfDate (theSuite, false);
							}
						}
					}
					catch (IOException e)
					{
						System.err.println (new ErrorString ("[ApplicationManager] Exception adding jar file " + theJarFiles [i] + " : " + e));
					}
				}

				scanForDeletedSuites (folderInformation, theJarFiles);				
			}
			
			File [] theFolders =
				aLocation.listFiles (new FileFilter ()
										{
											public boolean accept (File aFile)
											{
												return (aFile.isDirectory () &&
														!aFile.getName ().startsWith (".") &&
														!aFile.getName ().toLowerCase ().endsWith ("resource.frk"));
											}
										});
			scanForDeletedFolders (aLocation, theFolders);
			for (int i = 0; i < theFolders.length; i++)
			{
				buildApplicationSuites (theFolders [i]);
			}
		}		
	}
		
	
	// ----------------------------------------------------------------------
	// METHOD: siteInitialization
	// ----------------------------------------------------------------------
	
	/** Perform site specific initialization
	  * 
	  * @param aConfigurationProperties The properties Hashtable
	  *
	  */
	  
	private void siteInitialization (final Properties aConfigurationProperties)
	{
		TreeMap theSortedMap = new TreeMap ();
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		
		Enumeration theKeys = aConfigurationProperties.keys ();
		while (theKeys.hasMoreElements ())
		{
			String theKey = (String) theKeys.nextElement ();
			if (theKey.startsWith (theSCM.MAUI_SITE_INITIALIZER))
			{
				theSortedMap.put (theKey, aConfigurationProperties.getProperty (theKey));
			}
		}
		
		if (theSortedMap.size () > 0)
		{
			Iterator theValues = theSortedMap.values ().iterator ();
			int i = 0;
			while (theValues.hasNext ())
			{
				String theClass = (String) theValues.next ();
				try
				{
					I_SiteInitializer theInitializer = getSiteInitializer (theClass);
					if (theInitializer != null)
					{
						theInitializer.initializeSite (theSCM.getGlobalProperties ());
					}
					else
					{
						System.err.println ("[ServerConfigurationManager] - Expected site specific initializer class not found : " +
											theClass);
					}
				}
				catch (Exception e)
				{
					System.err.println ("[ServerConfigurationManager] - Site specific initializer exception: (class: " +
										theClass + ") (exception: " + e + ")");
				}
			}
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: initialize
	// ----------------------------------------------------------------------
	
	/** Initialize the ApplicationManager.  Get some properties that the ApplicationManager uses: 
	  * <pre>
	  * - the location of the application folder
	  * - a boolean indicating if a shared ClassLoader should be used or not
	  * - the default MauiApplication name
	  * </pre>
	  *
	  */
	  
	public void initialize ()
	{
		if (!initDone)
		{
			int theReference = Profiler.start (MauiRuntimeEngine.SOURCE_APPLICATION_MANAGER,
											   MauiRuntimeEngine.ACTION_INITIALIZE);
			//
			//	Do some initialization
			//
			scm = ServerConfigurationManager.getInstance ();
			applicationLocation = scm.getProperty (scm.MAUI_APPLICATION_LOCATION);
			workLocation = scm.getProperty (scm.MAUI_APPLICATION_WORK_SPACE_LOCATION);
			extensionLocation = scm.getProperty (scm.MAUI_EXTENSION_LOCATION);
			autoReload = scm.getProperty (scm.MAUI_AUTO_RELOAD).equalsIgnoreCase ("true");
					
			defaultFolder = scm.getProperty (scm.MAUI_DEFAULT_FOLDER);
			defaultFolderInformation = new FolderInformation (defaultFolder);
			folders.put ("", defaultFolderInformation);
			folders.put (defaultFolder, defaultFolderInformation);
			defaultApplicationClassName = getMauiApplicationClassName (scm.getProperty (scm.MAUI_DEFAULT_APPLICATION));
			internalApplicationClassName = getMauiApplicationClassName (scm.getProperty (scm.MAUI_INTERNAL_APPLICATION));
			//if (isSharedClassLoader)
			//{
				//sharedClassLoader = new MauiClassLoader ();
			//}
			
			System.out.println (new DebugString("[ApplicationManager] - Starting..."));
			defaultFolderInformation.applicationNames.put ("default", defaultApplicationClassName);
			if (autoReload)
			{
				try
				{
					applicationScanTime = Integer.parseInt (scm.getProperty (scm.MAUI_APPLICATION_SCAN_TIME)) * 1000;
				}
				catch (NumberFormatException e)
				{
					applicationScanTime = 20000;
				}
				
			}
			
			addApplicationManagerListener (new ApplicationManagerListener ()
				{
					public void extensionsLoaded (ApplicationManagerEvent aEvent)
					{
					}
					
					public void applicationSuiteLoaded (ApplicationManagerEvent aEvent)
					{
						catalogueSuite ((ApplicationSuite) aEvent.getSource ());
					}
					
					public void applicationSuiteUnloaded (ApplicationManagerEvent aEvent)
					{
						uncatalogueSuite ((ApplicationSuite) aEvent.getSource ());
					}
					
					public void applicationSuiteReloaded (ApplicationManagerEvent aEvent)
					{
						ApplicationSuite theSuite = (ApplicationSuite) aEvent.getSource ();
						uncatalogueSuite (theSuite);
						catalogueSuite (theSuite);
					}
				});
			new Thread (this, "AppMgr - AutoLoad").start ();
			/*synchronized (this)
			{
				try
				{
					wait ();
				}
				catch (InterruptedException e)
				{
				}
			}*/
			/*if (extensionLocation != null)
			{
				scanForExtensions ();
			}
			
			if (applicationLocation != null)
			{
				buildApplicationSuites (new File (applicationLocation));
			}
			
			siteInitialization (scm.getProperties ());*/
			
			//
			//	Listen for session level events (creation, deletion, application addition)
			//
			HTTPSession.addSessionListener (this);
			initDone = true;
			
			System.out.println (new DebugString("[ApplicationManager] - Started."));
			Profiler.finish (theReference, null);
		}
	}
	
	// ----------------------------------------------------------------------
	// METHOD: catalogueSuite
	// ----------------------------------------------------------------------
	
	private void addName (String aName, Vector aTargetVector)
	{
		lowerCase.put (aName, aName);
		lowerCase.put (aName.toLowerCase (), aName);
		aTargetVector.addElement (aName);
		aTargetVector.addElement(aName.toLowerCase ());
	}
	
	/** Do lowercase cataloguing of applications withing an ApplicationSuite 
	  *
	  * @param aApplicationSuite The ApplicationSuite
	  */
	public void catalogueSuite (ApplicationSuite aApplicationSuite)
	{
		uncatalogueSuite (aApplicationSuite);
		
		Vector theLowerCaseNames = new Vector (20);
		suiteReferences.put (aApplicationSuite.getFullName (), theLowerCaseNames);
		String theLocation = aApplicationSuite.getLocation ();
		String [] theApplicationNames = aApplicationSuite.getApplicationNames ();
		StringBuffer theName = new StringBuffer ();
		for (int i = 0; i < theApplicationNames.length; i++)
		{
			if (theLocation != null &&
				theLocation.length () > 0)
			{
				theName.append (theLocation);
				theName.append ("/");
			}
			int theLength = theName.length ();
			if (theLength != 0)
			{
				theName.append (theApplicationNames [i]);
				addName (theName.toString (), theLowerCaseNames);
			}
			addName (theApplicationNames [i], theLowerCaseNames);
			int theIndex = theApplicationNames [i].lastIndexOf (".");
			if (theIndex != -1)
			{
				String theShortName = theApplicationNames [i].substring (theIndex + 1);
				addName (theShortName, theLowerCaseNames);
				addName (aApplicationSuite.getName () + "." + theShortName, theLowerCaseNames);
				if (theLength != 0)
				{
					theName.setLength (theLength);
					theName.append (theShortName);
					addName (theName.toString (), theLowerCaseNames);
				}
			}
			else
			{
				addName (aApplicationSuite.getName () + "." + theApplicationNames [i], theLowerCaseNames);
			}
			theName.setLength (0);
		}	
	}
	
	// ----------------------------------------------------------------------
	// METHOD: catalogueSuite
	// ----------------------------------------------------------------------
	
	/** Uncatalogue all of the applications within the ApplicationSuite 
	  *
	  * @param aApplicationSuite The ApplicationSuite
	  */
	public void uncatalogueSuite (ApplicationSuite aApplicationSuite)
	{
		Vector theNames = (Vector) suiteReferences.remove (aApplicationSuite.getFullName ());
		if (theNames != null)
		{
			Enumeration theNamesEnumeration = theNames.elements ();
			String theName;
			while (theNamesEnumeration.hasMoreElements ())
			{
				theName = (String) theNamesEnumeration.nextElement ();
				lowerCase.remove (theName);
				lowerCase.remove (theName.toLowerCase ());
			}
		}
	}

	// ----------------------------------------------------------------------
	// METHOD: getFolders
	// ----------------------------------------------------------------------
	
	/** Get an array of all of the FolderInformation objects
	  *
	  *
	  * @return The array of FolderInformation objects
	  *
	  */
	  
	protected FolderInformation [] getFolders ()
	{
		Object [] theFolders = folders.values ().toArray ();
		FolderInformation [] retVal = new FolderInformation [theFolders.length];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = (FolderInformation) theFolders [i];
		}
		return retVal;
	}
		

	// ----------------------------------------------------------------------
	// METHOD: getFolder
	// ----------------------------------------------------------------------
	
	/** Get a FolderInformation object.  This object contains information on the ApplicationSuites it contains.  This method
	  * uses a class name of a MauiApplication, and retrieves the containing FolderInformation object
	  *
	  * @param aFolderName The name of the folder, or null to use the default
	  *
	  * @return The associated FolderInformation object
	  *
	  */
	  
	protected FolderInformation getFolder (String aFolderName)
	{
		String theFolderName = new String (aFolderName);
		Object retVal = folders.get ((theFolderName == null ? "" : theFolderName));
		if (retVal == null)
		{
			int theSlash = theFolderName.lastIndexOf ("/");
			if (theSlash != -1)
			{
				theFolderName = theFolderName.substring (0, theSlash);
				retVal = folders.get (theFolderName);
			}
		}
		
		if (retVal != null && theFolderName.length () > 0)
		{
			//
			//	Make sure that a folder and file name aren't the same
			//
			File theFolder = new File (applicationLocation, theFolderName);
			if (theFolder.isDirectory ())
			{
				final String theFinalName = theFolderName;
				//
				//	This might be a directory, or it might be a file name
				//
				theFolder = theFolder.getParentFile ();
				if (theFolder != null)
				{
					String [] theFiles =
						theFolder.list (new FilenameFilter ()
											{
												public boolean accept (File aDir, String aName)
												{
													boolean retVal2 = false;
													if (aName.startsWith (theFinalName))
													{
														retVal2 = !new File (aDir, aName).isDirectory ();
													}
													return retVal2;
												}
											});
					if (theFiles.length != 0)
					{
						retVal = null;
					}
				}
			}
		}
		return (FolderInformation) (retVal == null ? folders.get ("") : retVal);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getFolderName
	// ----------------------------------------------------------------------
	
	/** Get the folder name
	  *
	  * @param aClassName The name of the class
	  *
	  * @return The associated folder name
	  *
	  */
	  
	public String getFolderName (String aClassName)
	{
		FolderInformation theFolder = getFolder (aClassName);
		return theFolder.getFolderName ();
	}
	
	/**
	  * Get the ApplicationSuite for an application
	  *
	  * @param aMauiApplication The MauiApplication to check
	  */
	protected ApplicationSuite getApplicationSuite (MauiApplication aMauiApplication)
	{
		String theApplicationName = aMauiApplication.getApplicationName ();
		String theFolderName = aMauiApplication.getFolderName ();
		ApplicationClassWrapper theWrapper = getApplicationClassWrapper (theFolderName, theApplicationName);
		return theWrapper.getApplicationSuite ();
	}
	
	
	public void setOutOfDate (MauiApplication aMauiApplication)
	{
		ApplicationSuite theSuite = getApplicationSuite (aMauiApplication);
		theSuite.setOutOfDate ();
	}
	
	/** Check for the application suite being out of date
	  *
	  * @return Boolean indicating if the application suite is out of date
	  */
	public boolean isOutOfDate (MauiApplication aMauiApplication)
		throws IOException
	{
		boolean retVal = false;
		
		if (autoReload)
		{
			ApplicationSuite theSuite = getApplicationSuite (aMauiApplication);
			retVal = isOutOfDate (theSuite, false);
		}
		
		return retVal;
		
	}
	
	/**
	* Check for an ApplicationSuite being out of date
	*
	* @param aApplicationSuite The ApplicationSuite to check
	* @param aUnloadOnly Boolean indicating if this is unload only
	*
	* @param Boolean indicating if the suite is out of date or not
	*/
	protected boolean isOutOfDate (ApplicationSuite	aApplicationSuite,
								   boolean aUnloadOnly)
		throws IOException
	{
		boolean retVal = false;
		if (aUnloadOnly || aApplicationSuite.isOutOfDate ())
		{
			retVal = true;
			String theFolderName = new String (aApplicationSuite.getLocation ());
			if (theFolderName.length () > 0)
			{
				theFolderName += "/";
			}
			
			MauiApplication [] theApplications = getRunningApplications ();
			for (int i = 0; i < theApplications.length; i++)
			{
				if (getApplicationSuite (theApplications [i]) == aApplicationSuite)
				{
					HTTPSession [] theSessions = getSessions (theApplications [i]);
					String theApplicationClass = theFolderName + theApplications [i].getClass ().getName ();
					for (int j = 0; j < theSessions.length; j++)
					{
						MauiApplication theApplication = theSessions [j].getCachedMauiApplication (theApplicationClass);
						if (theApplication != null)
						{
							theApplication.exit ();
							theSessions [j].removeApplication (theApplication);
							theApplication.setChainApplicationName (theFolderName + theApplication.getName ());
						}
					}
				}
			}
			StringBuffer theStatusMessage = new StringBuffer ((aUnloadOnly ? "Unloading " : "Reloading "));
			theStatusMessage.append (aApplicationSuite.getFullName ());
			theStatusMessage.append (".");
			System.out.println (new InfoString (theStatusMessage.toString ()));
			if (aUnloadOnly)
			{
				aApplicationSuite.unload ();
				notifyManagerListeners (aApplicationSuite,
										aApplicationSuite.getFullName (),
										ApplicationManagerListener.UNLOADED);
			}
			else
			{
				aApplicationSuite.reload ();
				notifyManagerListeners (aApplicationSuite,
										aApplicationSuite.getFullName (),
										ApplicationManagerListener.RELOADED);
			}
		}
		return retVal;
	}
	
	// ----------------------------------------------------------------------
	// METHOD: doAuthorizationCheck
	// ----------------------------------------------------------------------
	
	/** Check authorizations.  If any fail, then an appropriate
	  * MauiApplication will be created.
	  *
	  * @param aSession The HTTPSession
	  * @param aInitializer The I_ApplicationInitializer object
	  */
	private MauiApplication doAuthorizationCheck (HTTPSession aSession,
												  I_ApplicationInitializer aInitializer)
	{
		MauiApplication retVal = null;
		AuthorizationManager theAuth = AuthorizationManager.getInstance ();
		if (!theAuth.isAuthorized (null, theAuth.AUTHORIZATION_SESSIONS))
		{
			retVal = new com.bitmovers.maui.engine.httpserver.SessionLimitExceeded (aInitializer);
		}
		else if (aSession.isServletBased () && !theAuth.isAuthorized (null, theAuth.AUTHORIZATION_SERVLET))
		{
			retVal = new com.bitmovers.maui.engine.servlet.NoServletSupport (aInitializer);
		}
		return retVal;
	}
	
	// ----------------------------------------------------------------------
	// METHOD: createMauiApplication
	// ----------------------------------------------------------------------
	
	/** Create a MauiApplication object.
	  *
	  * @param aRequestHeader Information from the request header
	  * @param aCheckpoint A hashtable of checkpoint values set on the client side
	  * @param aSession The session associated with the application being created
	  * @param aAppName The class name of the MauiApplication to create 
	  * @param aCreateDefault Boolean indicating if a default application should be created.
	  *
	  * @return The initialized MauiApplication object
	  *
	  * @exception ClassNotFoundException This could happen if an invalid class name is passed in
	  *
	  */
	  
	public MauiApplication createMauiApplication (final Map aRequestHeader,
												  final Hashtable aCheckpoint,
												  final HTTPSession aSession,
												  final String aClassName,
												  final boolean aCreateDefault)
		throws ClassNotFoundException,
			   InstantiationException,
			   IllegalAccessException,
			   SecurityException,
			   NoSuchMethodException,
			   InvocationTargetException,
			   IllegalAccessException
	{
		MauiApplication retVal = null;
		if (aClassName != null &&
			aClassName.trim ().length () > 0)
		{
			
			//FolderInformation theFolderInformation = getFolder (aClassName);
			//String theClassName = theFolderInformation.getClassName (aClassName);
			//theClassName = getMauiApplicationClassName (theClassName);
			//ApplicationClassWrapper theWrapper = (ApplicationClassWrapper) theFolderInformation.applications.get (theClassName);
			final ApplicationClassWrapper theWrapper = getApplicationClassWrapper (aClassName);
			if (theWrapper == null)
			{
				if (aCreateDefault)
				{
					String theLocation = (defaultFolder.trim ().length () == 0 ?
												"" :
												defaultFolder + "/");		
					retVal = createMauiApplication (aRequestHeader, aCheckpoint, aSession, theLocation + defaultApplicationClassName, false);
				}
				else
				{
					FolderInformation theFolderInformation = getFolder (aClassName);
					String theClassName = theFolderInformation.getClassName (aClassName);
					theClassName = getMauiApplicationClassName (theClassName);
					throw new ClassNotFoundException (theClassName);
				}
			}
			else
			{
				I_ApplicationInitializer theInitializer = new I_ApplicationInitializer ()
					{
						public void initializeApplication (MauiApplication aMauiApplication)
						{
							
							theWrapper.createWorkSpace ();
							ComponentManager.getInstance ().setApplication (aMauiApplication);
							ApplicationSuite theApplicationSuite = theWrapper.getApplicationSuite ();
							aMauiApplication.setApplicationSuiteName (theApplicationSuite.getName ());
							aMauiApplication.setApplicationName (aClassName);
							aMauiApplication.setChainApplicationName (aClassName);
							aMauiApplication.setShortName (getShortName (aClassName));
							aMauiApplication.setSession (aSession);
							aMauiApplication.setFolderName (theApplicationSuite.getLocation ());
							aMauiApplication.setInboundCookies (aCheckpoint);
							Properties theProperties = (Properties) theWrapper.getProperties ().clone ();
							aMauiApplication.setProperties (theProperties);
							String theProperty = theProperties.getProperty (scm.MAUI_APPLICATION_PREAMBLE);
							if (theProperty != null)
							{
								aMauiApplication.setPreamble (theProperty);
							}
							
							theProperty = theProperties.getProperty (scm.MAUI_APPLICATION_POSTAMBLE);
							if (theProperty != null)
							{
								aMauiApplication.setPostamble (theProperty);
							}
							
							theProperty = theProperties.getProperty (scm.MAUI_APPLICATION_BACKGROUND_COLOR);
							if (theProperty != null)
							{
								aMauiApplication.setBackgroundColor (theProperty);
							}
							
							theProperty = theProperties.getProperty (scm.MAUI_APPLICATION_BACKGROUND_IMAGE);
							if (theProperty != null)
							{
								aMauiApplication.setBackgroundImage (theProperty);
							}
							
							aMauiApplication.setDesktop (MDesktop.getInstance ());
							aMauiApplication.initialize (aRequestHeader);
						}
					};
				if ((retVal = doAuthorizationCheck (aSession, theInitializer)) == null)
				{
					retVal = theWrapper.instantiateApplication (theInitializer);
				}
				try
				{
					if (isOutOfDate (retVal))
					{
						retVal = createMauiApplication (aRequestHeader,
														aCheckpoint,
														aSession,
														aClassName,
														aCreateDefault);
					}
				}
				catch (IOException e)
				{
				}
			}
		}
		else if (aCreateDefault)
		{
			//
			//	Create a default application
			//
			retVal = createMauiApplication (aRequestHeader, aCheckpoint, aSession, defaultApplicationClassName, false);
		}
		else
		{
			throw new ClassNotFoundException ((aClassName == null ? defaultApplicationClassName : aClassName));
		}
		
		if (retVal != null)
		{
			retVal.staticInitialize ();
			if (aClassName != null && aClassName.equals (defaultApplicationClassName))
			{
				retVal.setDefaultApplication (true);
			}
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getDefaultApplicationClassName
	// ----------------------------------------------------------------------
	
	/** Get the name of the default application
	  *
	  * @return The name of the default application
	  *
	  */
	  
	public String getDefaultApplicationClassName ()
	{
		return defaultApplicationClassName;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: isDefaultApplicationClassName
	// ----------------------------------------------------------------------
	
	/** Test if the class name corresponds to the default application class name
	  *
	  * @return Boolean indicating if this is the default application class name
	  *
	  */
	  
	public boolean isDefaultApplicationClassName (String aClassName)
	{
		return getMauiApplicationClassName (aClassName).equals (defaultApplicationClassName);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getShortName
	// ----------------------------------------------------------------------
	
	/** Return the short name for the application
	  *
	  * @param aFolder The folder which contains the class
	  * @param aClassName The target class
	  *
	  * @return The short name for the class
	  *
	  */
	  
	public String getShortName (String aFolderName, String aClassName)
	{
		return getShortName (aFolderName + "/" + aClassName);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getShortName
	// ----------------------------------------------------------------------
	
	/** Return the short name for the application
	  *
	  * @param aName An application name
	  *
	  * @return The short name for the application
	  *
	  */
	  
	public String getShortName (String aClassName)
	{
		FolderInformation theFolder = getFolder (aClassName);
		String retVal = getMauiApplicationClassName (aClassName);
		ApplicationClassWrapper theWrapper = theFolder.getWrapper (retVal);
		int thePeriod = retVal.lastIndexOf (".");
		if (thePeriod != -1)
		{
			retVal = retVal.substring (thePeriod + 1);
		}
		retVal = theWrapper.getApplicationSuite ().getName () + "." + retVal;
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSimpleShortName
	// ----------------------------------------------------------------------
	
	/** Return the short name for the class
	  *
	  * @param aFolder The folder which contains the class
	  *
	  * @param aName An application name
	  *
	  * @return The short name for the application without the suite name attached
	  *
	  */
	  
	public String getSimpleShortName (String aFolder, String aClassName)
	{
		return getSimpleShortName (aFolder + "/" + aClassName);
	}	
	
	
	/** Return the short name for the application without the suite name attached.
	  *
	  * @param aName An application name
	  *
	  * @return The short name for the application without the suite name attached
	  *
	  */
	  
	public String getSimpleShortName (String aClassName)
	{
		FolderInformation theFolder = getFolder (aClassName);
		String retVal = getMauiApplicationClassName (aClassName);
		ApplicationClassWrapper theWrapper = theFolder.getWrapper (retVal);
		int thePeriod = retVal.lastIndexOf (".");
		if (thePeriod != -1)
		{
			retVal = retVal.substring (thePeriod + 1);
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getApplicationClassWrapper
	// ----------------------------------------------------------------------
	
	/** Get the ApplicationClassWrapper object
	  *
	  * @param aApplicationName The name of the application
	  *
	  * @return The ApplicationClassWrapper, or null if not found
	  *
	  */
	  
	private ApplicationClassWrapper getApplicationClassWrapper (String aApplicationName)
	{
		ApplicationClassWrapper retVal = null;
		String theApplicationName = new String (aApplicationName);
		FolderInformation theFolderInformation = getFolder (theApplicationName);
		if (theFolderInformation != null)
		{
			String theFolderName = theFolderInformation.getFolderName ();
			if (theFolderName.length () > 0 &&
				theApplicationName.startsWith (theFolderName))
			{
				theApplicationName = theApplicationName.substring (theFolderName.length () - 1);
				if (theApplicationName.startsWith ("/"))
				{
					theApplicationName = theApplicationName.substring (1);
				}
			}
			String theClassName = theFolderInformation.getClassName (theApplicationName);
			retVal = (ApplicationClassWrapper) theFolderInformation.applications.get (theClassName);
		}
		return retVal;
	}
	
	
	/** Get the ApplicationClassWrapper object
	  *
	  * @param aFolderName The name of the folder containing the application (or null for the default)
	  * @param aApplicationName The name of the application
	  *
	  * @return The ApplicationClassWrapper, or null if not found
	  *
	  */
	  
	private ApplicationClassWrapper getApplicationClassWrapper (String aFolderName, String aApplicationName)
	{
		ApplicationClassWrapper retVal = null;
		FolderInformation theFolderInformation = getFolder (aFolderName);
		if (theFolderInformation != null)
		{
			String theClassName = theFolderInformation.getClassName (aApplicationName);
			retVal = (ApplicationClassWrapper) theFolderInformation.applications.get (theClassName);
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getApplicationProperties
	// ----------------------------------------------------------------------
	
	/** Get all of the properties for an application (without the application having to be loaded)
	  *
	  * @param aFolderName The folder which contains the application (or null to use the default)
	  * @param aFullApplicationName The fully qualified application name
	  *
	  * @return A Properties object containing the properties for the application, or null if not found
	  *
	  */
	  
	public Properties getApplicationProperties (String aFolderName, String aFullApplicationName)
	{
		ApplicationClassWrapper theWrapper = getApplicationClassWrapper (aFolderName, aFullApplicationName);
		return (theWrapper == null ? null : (Properties) theWrapper.getProperties ().clone ());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSiteInitializer
	// ----------------------------------------------------------------------
	
	/** Get a site initializer
	  *
	  * @param aName The name of the site initializer
	  *
	  * @return An I_SiteInitializer object
	  *
	  * @exception IllegalAccessException, InstantiationException
	  *
	  */
	  
	public I_SiteInitializer getSiteInitializer (String aName)
		throws IllegalAccessException,
			   InstantiationException
	{
		I_SiteInitializer retVal = null;
		Class theClass = (Class) initializers.get (aName);
		if (theClass != null)
		{
			retVal = (I_SiteInitializer) theClass.newInstance ();
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getApplicationFolders
	// ----------------------------------------------------------------------
	
	/** Get the application folders
	  *
	  * @return The application folders
	  *
	  */
	  
	public String [] getApplicationFolders ()
	{
		String [] retVal = new String [folders.size ()];
		Iterator theFolders = folders.keySet ().iterator ();
		int i = 0;
		while (theFolders.hasNext ())
		{
			retVal [i++] = (String) theFolders.next ();
		}
		return retVal;
	}	
	
	
	// ----------------------------------------------------------------------
	// METHOD: getApplicationSuiteNames
	// ----------------------------------------------------------------------
	
	/** Get the application suite names
	  *
	  * @param aFolder The folder to look for the application suites
	  *
	  * @return A String array of the application suite names
	  *
	  */
	  
	public String [] getApplicationSuiteNames (String aFolder)
	{
		FolderInformation theFolderInformation = (FolderInformation) folders.get ((aFolder == null ? "" : aFolder));
		return (theFolderInformation == null ? new String [0] : theFolderInformation.getApplicationSuiteNames ());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getApplicationNames
	// ----------------------------------------------------------------------
	
	/** Get the application names
	  *
	  * @param  aFolderName            The name of the folder containing the 
	  *                                application suite.
	  *
	  * @param  aApplicationSuiteName  The name of the application suite to 
	  *                                use as a filter. If null, then all 
	  *                                Maui Application names will be 
	  *                                returned.
	  *
	  * @return  A String array of application names.
	  *
	  */
	  
	public String [] getApplicationNames (String aFolderName, String aApplicationSuiteName)
	{
		String [] retVal = null;
		
		FolderInformation theFolder = (FolderInformation) folders.get (aFolderName);
		
		if (aApplicationSuiteName == null)
		{
			retVal = theFolder.getAllApplicationNames ();
		}
		else
		{
			ApplicationSuite theSuite = theFolder.getSuite (aApplicationSuiteName);
			retVal = theSuite.getApplicationNames ();
			int theLastIndex = 0;
			for (int i = 0; i < retVal.length; i++)
			{
				Properties theProperties = getApplicationProperties (aFolderName, retVal [i]);
				String theVisibility = theProperties.getProperty (MauiApplication.MAUI_APPLICATION_INVISIBLE);
				if (theVisibility == null || theVisibility.equalsIgnoreCase ("false"))
				{
					if (theLastIndex++ < i)
					{
						retVal [theLastIndex - 1] = retVal [i];
					}
				}
			}
			
			if (theLastIndex < retVal.length)
			{
				String [] theNewApps = new String [theLastIndex];
				System.arraycopy (retVal, 0, theNewApps, 0, theLastIndex);
				retVal = theNewApps;
			}
		}
		return retVal;
	}
	
	
	
	//++ 181 MW 2001.08.09
	// ----------------------------------------------------------------------
	// METHOD: getAllApplicationNames
	// ----------------------------------------------------------------------
	
	/** Get all the application names
	  *
	  * @return  A Vector of all the application names.
	  *
	  */
	  
	public Hashtable getAllApplicationNames ()
	{
		return lowerCase;
	}
	//-- 181
	
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMauiApplicationClassName
	// ----------------------------------------------------------------------
	
	/** Get the class name for a MauiApplication
	  *
	  * @param  aFolderName  The folder which contains the application
	  * 
	  * @param  aName        The class name
	  *
	  */
	  
	public String getMauiApplicationClassName (String aFolder, String aName)
	{
		String retVal = null;
		
		if (aName == null ||
			aName.trim ().length () == 0)
		{
			retVal = defaultApplicationClassName;
		}
		else
		{
			FolderInformation theFolder = getFolder (aFolder);
			retVal = (String) theFolder.getApplicationName (aName);
			if (retVal == null)
			{
				retVal = theFolder.getClassName (aName);
			}
		}
		return retVal;
	}
	
	
	/** Get the class name for a MauiApplication.
	  *
	  * @param  aName  The name of the MauiApplication
	  *
	  * @return  The class name
	  *
	  */
	  
	public String getMauiApplicationClassName (String aName)
	{
		int theSlash = aName.lastIndexOf ("/");
		String theFolder = (theSlash == -1 ? "" : aName.substring (0, theSlash));
		String theName = (theSlash == -1 ? aName : aName.substring (theSlash + 1));
		return getMauiApplicationClassName (theFolder, theName);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMauiApplicationAddress
	// ----------------------------------------------------------------------
	
	/** Get the application address (this includes the folder reference
	  *
	  * @param  aName  The name of the application
	  *
	  * @return  The full address, including the folder and class name
	  *
	  */
	  
	public String getMauiApplicationAddress(String aName)
	{
		String retVal = null;
		if (aName == null ||
			aName.trim ().length () == 0)
		{
			retVal = (defaultFolder.length () == 0 ? "" : defaultFolder + "/") + defaultApplicationClassName;
		}
		else
		{
			int theQuestion = aName.indexOf ("?");
			String theName = (theQuestion == -1 ? aName : aName.substring (0, theQuestion));
			String theFolder = getFolder (theName).getFolderName ();
			retVal = (theFolder.length () == 0 ? "" : theFolder + "/") + getMauiApplicationClassName (theName);
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSessions
	// ----------------------------------------------------------------------
	
	/** Get all of the sessions running a particular application
	  *
	  * @param  aApplicationName  The name of the application to check
	  *
	  */
	  
	public HTTPSession[] getSessions(String anApplicationName)
	{
		HTTPSession [] retVal = null;
		SessionInformation theSessionInformation = (SessionInformation)sessions.get(getMauiApplicationClassName(anApplicationName));
		
		if (theSessionInformation != null)
		{
			retVal = theSessionInformation.toArray();
		}
		
		return (retVal == null ? new HTTPSession [0] : retVal);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSessionCount
	// ----------------------------------------------------------------------
	
	/** Get all of the sessions running a particular application
	  *
	  * @param  aApplication  The application to check
	  *
	  */
	  
	public HTTPSession[] getSessions(MauiApplication aApplication)
	{
		return getSessions(aApplication.getApplicationName());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSessionCount
	// ----------------------------------------------------------------------
	
	/** Get a count of the number of sessions currently running an application
	  *
	  * @param  aApplication  A Maui application.
	  *
	  * @return               The number of sessions currently running.
	  * 
	  */
	  
	public int getSessionCount(MauiApplication aApplication)
	{
		return getSessions(aApplication).length;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSessionCount
	// ----------------------------------------------------------------------
	
	/** Get a count of all of the sessions currently running
	  *
	  * @return Count of all of the sessions currently running
	  * 
	  */
	  
	public int getSessionCount()
	{
		return HTTPSession.getSessionCount();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSessionApplications
	// ----------------------------------------------------------------------
	
	/** Get all of the applications for an HTTPSession, or for all applications
	  *
	  * @param  aSession  The session to get applications for
	  *
	  */
	  
	public MauiApplication[] getSessionApplications(HTTPSession aSession)
	{
		return (aSession == null ? getRunningApplications() : aSession.getCachedMauiApplications());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSessionApplications
	// ----------------------------------------------------------------------
	
	/** Get all of the applications for an HTTPSession id, or for all applications
	  *
	  * @param  aSessionID  The session id
	  *
	  */
	  
	public MauiApplication[] getSessionApplications(String aSessionID)
	{
		MauiApplication [] retVal;
		
		if (aSessionID == null)
		{
			retVal = getRunningApplications ();
		}
		else
		{
			HTTPSession theSession = HTTPSession.getSession (aSessionID);
			retVal = getSessionApplications (theSession);
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRunningApplications
	// ----------------------------------------------------------------------
	
	/** Get the list of all running applications
	  *
	  * @return  An array of all of the running applications
	  *
	  */
	  
	public MauiApplication[] getRunningApplications ()
	{
		Enumeration theSessionInformations = sessions.elements ();
		MauiApplication [] retVal = new MauiApplication [sessions.size ()];
		int i = 0;
		while (theSessionInformations.hasMoreElements ())
		{
			retVal [i++] = ((SessionInformation) theSessionInformations.nextElement ()).getMauiApplication ();
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: sessionCreated
	// ----------------------------------------------------------------------
	
	/** Notification of session creation
	  *
	  * @param  aSessionEvent  The event object describing the session 
	  *
	  */
	  
	public void sessionCreated (SessionEvent aSessionEvent)
	{
		System.out.println ("Session created: " + ((HTTPSession) aSessionEvent.getSource ()).getSessionID ());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: sessionDeleted
	// ----------------------------------------------------------------------
	
	/** Notification of session deletion.
	  *
	  * @param  aSessionEvent  The event object describing the session
	  *
	  */
	  
	public void sessionDeleted (SessionEvent aSessionEvent)
	{
		HTTPSession theSession = (HTTPSession) aSessionEvent.getSource ();
		System.out.println ("Session removed: " + theSession.getSessionID ());
		
		//
		//	Purge this session from the sessions to applications cross-reference table
		MauiApplication [] theApplications = theSession.getCachedMauiApplications ();
		for (int i = 0; i < theApplications.length; i++)
		{
			String theName = getMauiApplicationClassName (theApplications [i].getApplicationName ());
			SessionInformation theSessionInformation = (SessionInformation) sessions.get (theName);
			if (theSessionInformation != null)
			{
				theSessionInformation.removeSession (theSession);
				if (theSessionInformation.isEmpty ())
				{
					sessions.remove (theName);
				}
			}
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: applicationAdded
	// ----------------------------------------------------------------------
	
	/** Notification of the addition of an application.
	  *
	  * @param  aSessionEvent  The event object describing the session
	  *
	  */
	
	public void applicationAdded (SessionEvent aSessionEvent)
	{
		String theApplicationName = getMauiApplicationClassName (aSessionEvent.getMauiApplication ().getApplicationName ());
		System.out.println ("Application " + theApplicationName + 
							" added to session: " + ((HTTPSession) aSessionEvent.getSource ()).getSessionID ());
							
		SessionInformation theSessionInformation = (SessionInformation) sessions.get (theApplicationName);
		if (theSessionInformation == null)
		{
			theSessionInformation = new SessionInformation (aSessionEvent.getMauiApplication ());
			sessions.put (theApplicationName, theSessionInformation);
		}
		theSessionInformation.addSession ((HTTPSession) aSessionEvent.getSource ());
	}
	
	/** Notification of the addition of an application.
	  *
	  * @param  aSessionEvent  The event object describing the session
	  *
	  */
	
	public void applicationRemoved (SessionEvent aSessionEvent)
	{
		HTTPSession theSession = (HTTPSession) aSessionEvent.getSource ();
		String theApplicationName = getMauiApplicationClassName (aSessionEvent.getMauiApplication ().getApplicationName ());
		System.out.println ("Application " + theApplicationName + 
							" removed from session: " + theSession.getSessionID ());
							
		SessionInformation theSessionInformation = (SessionInformation) sessions.get (theApplicationName);
		if (theSessionInformation != null)
		{
			theSessionInformation.removeSession (theSession);
			if (theSessionInformation.isEmpty ())
			{
				sessions.remove (theApplicationName);
			}
		}
	}
	
	public void run ()
	{
		Thread.currentThread ().setName ("AppMgr - AutoLoad");
		do
		{
			if (extensionLocation != null)
			{
				scanForExtensions ();
			}
			
			if (applicationLocation != null)
			{
				buildApplicationSuites (new File (applicationLocation));
			}
			
			if (firstTime)
			{
				siteInitialization (scm.getProperties ());
				//synchronized (this)
				//{
					firstTime = false;
				//	notify ();
				//}
			}
			
			if (autoReload)
			{
				try
				{
					Thread.sleep (applicationScanTime);
				}
				catch (InterruptedException e)
				{
				}
			}
		} while (autoReload);
	}
}


// ========================================================================
// INTERFACE: I_JarCallback
// ========================================================================

interface I_JarCallback
{
	public void mauiAppFound (Class aMauiAppClass);
	public void initializerFound (Class aInitializer);
}


// ========================================================================
// CLASS: MauiClassLoader
// ========================================================================

/** This is the MauiClassLoader object.  It is a simple ClassLoader which is used to load classes from the jar
  * files contained with the Applications folder hierarchy.
  *
  */
  
class MauiClassLoader extends ClassLoader
{
	
	
	private Hashtable classEntries = new Hashtable (30);
	private Hashtable resourceEntries = new Hashtable (10);
	private Hashtable resourceJars = new Hashtable (10);
	private static final Class mauiApplicationClass = MauiApplication.class;
	protected boolean loadAll = false;
	protected boolean loadAllProperties = false;
	protected Vector newClasses = new Vector (100);
	private boolean applicationsLoader;
	private MauiClassLoader parent;


	// --------------------------------------------------------------------
	// CONSTRUCTORS
	// --------------------------------------------------------------------

	protected MauiClassLoader (ClassLoader aParent)
	{
		//super (aParent);
		super ();
		parent = (MauiClassLoader) aParent;
		applicationsLoader = true;
		initialize ();
	}
	
	
	protected MauiClassLoader ()
	{
		super ();
		applicationsLoader = false;
		initialize ();
	}
	
	private void initialize ()
	{
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		loadAll = theSCM.getProperty (theSCM.MAUI_AUTO_RELOAD).equals ("true");
	}
	
	private byte [] readZipEntry (InputStream aInput, int aSize)
		throws IOException
	{
		final int theTotalLength = aSize;
		byte [] retVal = new byte [theTotalLength];
		int theBufferOffset = 0;
		while (theBufferOffset < theTotalLength)
		{
			int theBytesRead = aInput.read (retVal, theBufferOffset, theTotalLength - theBufferOffset);
			theBufferOffset += theBytesRead;
		}
		return retVal;
	}
	
	protected void setLoadAllProperties (boolean aLoadAllProperties)
	{
		loadAllProperties = aLoadAllProperties;
	}

	// --------------------------------------------------------------------
	// METHOD: findObject
	// --------------------------------------------------------------------

	/** Find an object within the jar file.  This can be a class or 
	  * resource.  For each access to the jar file, an entry is
	  * added to a "caching" Hashtable, for faster subsequent accesses.
	  *
	  * @param  aUseName     The name to use for throwing exceptions (ie. 
	  *                      the actual class or resource name, rather than 
	  *                      the key of access).
	  * 
	  * @param  aTable       The Hashtable to use for retrieving cached 
	  *                      objects.
	  *
	  * @param  aNeedClass   Boolean indicating if this is a request for a 
	  *                      class or resource.
	  *
	  * @return              The retrieved Object (Class or Resource).
	  *
	  * @exception  ClassNotFoundException
	  *                      Thrown if the Class isn't found.
	  *
	  */
	  
	protected Object findObject (String aUseName,
								 Hashtable aTable,
								 boolean aNeedClass)
		throws ClassNotFoundException
	{
		Object retVal = aTable.get (aUseName);
		
		if (retVal != null && retVal instanceof byte [])
		{
			if (aNeedClass)
			{
				retVal = defineClass (aUseName, (byte []) retVal, 0, ((byte []) retVal).length);
				aTable.put (aUseName, retVal);
			}
			else
			{
				retVal = new ByteArrayInputStream ((byte []) retVal);
			}
		}
		return retVal;
	}
	/*	if (retVal instanceof ZipFile)
		{
			String theSearchName = new String (aUseName);
			if (aNeedClass)
			{
				theSearchName = theSearchName.replace ('.', '/') + ".class";
			}
			ZipEntry theEntry = ((ZipFile) retVal).getEntry (theSearchName);
			if (theEntry == null)
			{
				throw new ClassNotFoundException (aUseName);
			}
			

			InputStream theInput = null;
			try
			{
				theInput = ((ZipFile) retVal).getInputStream (theEntry);
				if (aNeedClass)
				{
					//
					//	This is a request for a class load
					//
					//definePackage (theEntry);
					int theSize = (int) theEntry.getSize ();
					byte [] theClassDefinition = readZipEntry (theInput, theSize);
					retVal = defineClass (aUseName, theClassDefinition, 0, theSize);
					aTable.put (aUseName, retVal);	// Replace the entry with the class definition
				}
				else
				{
					//
					//	Trying for resource loading
					//
					//	Return an InputStream for the resource, which can be wrapped up as an URL later
					//
					if (loadAll)
					{
						byte [] theResource = readZipEntry (theInput, (int) theEntry.getSize ());
						aTable.put (aUseName, theResource);
						retVal = new ByteArrayInputStream (theResource);
					}
					else
					{
						retVal = theInput;
					}
				}
			}
			catch (IOException e)
			{
				throw new ClassNotFoundException (aUseName);
			}
			finally
			{
				if (theInput != null)
				{
					try
					{
						theInput.close ();
					}
					catch (IOException e1)
					{
					}
				}
			}
		}
		else if (!aNeedClass)
		{
			if (retVal instanceof byte [])
			{
				retVal = new ByteArrayInputStream ((byte []) retVal);
			}
		}
		return retVal;
	}*/
	
	
	// --------------------------------------------------------------------
	// METHOD: loadClass
	// --------------------------------------------------------------------
	
	/** This extends the ClassLoader's loadClass method.  It is for 
	  * debugging purposes only. If the logging threshold is at the debug 
	  * level, then this will add a message to the log indicating what 
	  * class is being loaded.
	  *
	  * @param  aName     The name of the Class to load.
	  * 
	  * @param  aResolve  Boolean indicating if the Class should be 
	  *                   resolved or not.
	  * 
	  */
	  
	protected Class loadClass (String aName, boolean aResolve)
		throws ClassNotFoundException
	{
		Class retVal = null;
		System.out.println (new DebugString("Loading class " + aName));
		if (!aName.equals ("com.bitmovers.maui.MauiRuntimeEngine"))
		{
			try
			{
				retVal = (Class) findObject (aName,
											 classEntries,
											 true);
			}
			catch (ClassNotFoundException e)
			{
			}

			if (retVal == null && applicationsLoader)
			{
				try
				{
					retVal = parent.loadClass (aName, aResolve);
					classEntries.put (aName, retVal);
				}
				catch (ClassNotFoundException e)
				{
				}
				
			}
		}	
				
		if (retVal == null)
		{
			retVal = findSystemClass (aName);
			classEntries.put (aName, retVal);
		}

		if (retVal != null && aResolve)
		{
			resolveClass (retVal);
		}
		return retVal;
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: findClass
	// --------------------------------------------------------------------
	
	/** If the default ClassLoader can't find the Class, then this 
	  * ClassLoader will try to find it.
	  *
	  * @param      aName  The name of the Class to load.
	  *
	  * @return            The loaded Class
	  *
	  * @exception  ClassNotFoundException  
	  *                    Thrown if the Class isn't found.
	  *
	  */
	  
	/*protected Class findClass (String aName)
		throws ClassNotFoundException
	{
		return (Class) findObject (aName.replace ('.', '/') + ".class",
								   aName,
								   classEntries,
								   true);
	}*/
	
	
	// --------------------------------------------------------------------
	// METHOD: findResourceStream
	// --------------------------------------------------------------------
	
	/** Find a resource, and return an input stream to it
	  *
	  * @param   aName        The name of the resource.
	  *
	  * @return               An InputStream to the resource.
	  *
	  * @throws  IOException  If the resource isn't found, or something 
	  *                       else goes wrong.
	  *
	  */
	  
	protected InputStream findResourceStream (String aName)
		throws IOException
	{
		InputStream retVal = null;
		try
		{
			StringBuffer theResourceName = new StringBuffer (aName);
			int theNameLength = theResourceName.length ();
			boolean theSuffix = aName.endsWith (".properties");
			if (theSuffix)
			{
				theNameLength -= 11;
			}
			else
			{
				theResourceName.append (".properties");
			}
			
			for (int i = 0; i < theNameLength; i++)
			{
				if (theResourceName.charAt (i) == '.')
				{
					theResourceName.setCharAt (i, '/');
				}
			}
			String theName = theResourceName.toString ();
			retVal = (InputStream) findObject (theName, resourceEntries, false);
		}
		catch (ClassNotFoundException e)
		{
		}
		return retVal;
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: findResourceStream
	// --------------------------------------------------------------------
	
	/** WARNING: NOT IMPLEMENTED YET <p>
	  *
	  * Find a resource, and return a URL to it
	  *
	  * @param aName The name of resource
	  *
	  * @return The URL for the resource
	  * 
	  * @invisible
	  *
	  */
	  
	/*protected URL findResource (String aName)
	{
		URL retVal = null;
		if (resourceEntries.get (aName) != null)
		{
			String theJarName = (String) resourceJars.get (aName);
			try
			{
				retVal = new URL ("jar:file:" + theJarName + "!/" + aName);
			}
			catch (IOException e)
			{
			}
		}
		return retVal;
	}*/
	
	public InputStream getResourceAsStream (String aName)
	{
		InputStream retVal = null;
		
		byte [] theResourceArray = (byte []) resourceEntries.get (aName);
		if (theResourceArray == null)
		{
			if (parent != null)
			{
				retVal = parent.getResourceAsStream (aName);
			}
		}
		else
		{
			retVal = new ByteArrayInputStream (theResourceArray);
		}
		return retVal;
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: findResources
	// --------------------------------------------------------------------
	
	/** WARNING: NOT IMPLEMENTED YET <p>
	  * Find the resources which match the name
	  *
	  */
	  
	protected Enumeration findResources (String aName)
		throws IOException
	{
		return null;
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: scanJarFile
	// --------------------------------------------------------------------
	
	/** Scan a jar file for Classes and Resources, and catalogue them.
	  *
	  * @param aJarFile The Jar file to scan
	  * @param aCallback A callback object which is used for cataloguing a MauiApplication
	  * @param aLoad Boolean indicating if this is loading or unloading
	  *
	  * @exception IOException If the jar file scan fails
	  *
	  */
	  
	protected void scanJarFile (final ZipFile aJarFile,
								final I_JarCallback aCallback)
		throws IOException
	{
		//
		//	Create two lists of entries one for class files, and one for resources (eg. everything else)
		//	For now, ignore the manifest
		//
		ZipEntry theEntry;
		String theEntryName = null;
		Enumeration theEntries = aJarFile.entries ();
		while (theEntries.hasMoreElements ())
		{
			theEntry = (ZipEntry) theEntries.nextElement ();
			if (!theEntry.isDirectory ())
			{
				theEntryName = theEntry.getName ();
				InputStream theInput = aJarFile.getInputStream (theEntry);
				byte [] theData = readZipEntry (theInput, (int) theEntry.getSize ());
				if (theEntryName.endsWith (".class"))
				{
					theEntryName = theEntryName.substring (0, theEntryName.length () - 6).replace ('/', '.');
					classEntries.put (theEntryName, theData);
					newClasses.addElement (theEntryName);
				}
				else if (loadAllProperties || theEntryName.toLowerCase ().endsWith (".properties"))
				{
					resourceEntries.put (theEntryName, theData);
				}
				theInput.close ();
				
					
					/*if (loadAll)
					{
						try
						{
							InputStream theInputStream = (InputStream) findObject (theEntryName,
																				   resourceEntries,
																				   false);
							try
							{
								theInputStream.close ();
							}
							catch (IOException e)
							{
							}
						}
						catch (Exception e)
						{
						}
					}
					else
					{
						resourceEntries.remove (theEntryName);
					}
				}*/
				
			}
		}
		
		if (applicationsLoader)
		{
			if (aCallback != null)
			{
				loadAllEntries (aCallback);
			}
			else
			{
				newClasses.removeAllElements ();
			}
		}
	}
	
	protected void loadAllEntries (I_JarCallback aCallback)
	{
		String theEntryName;
		Enumeration theClassEntries = newClasses.elements ();
		Class theInitializer = I_SiteInitializer.class;
		while (theClassEntries.hasMoreElements ())
		{
			theEntryName = (String) theClassEntries.nextElement ();
			try
			{
				Class theClass = Class.forName (theEntryName, true, this);
				if (aCallback != null)
				{
					if (mauiApplicationClass.isAssignableFrom (theClass) &&
						!Modifier.isAbstract (theClass.getModifiers ()) &&
						!theClass.equals (mauiApplicationClass))
					{
						aCallback.mauiAppFound (theClass);
					}
					else if (theInitializer.isAssignableFrom (theClass))
					{
						aCallback.initializerFound (theClass);
					}
				}
			}
			catch (ClassNotFoundException e)
			{
				System.err.println ("[MauiClassLoader] unexpected exception on " + theEntryName + " : " + e);
				e.printStackTrace ();
			}
			catch (NoClassDefFoundError e)
			{
				System.err.println ("[MauiClassLoader] unexpected exception on " + theEntryName + " : " + e);
				e.printStackTrace ();
			}
		}
		newClasses.removeAllElements ();
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: addJarFile
	// --------------------------------------------------------------------
	
	/** Added a jar file to the Maui applications database
	  *
	  * @param aJarFile The jar file to add
	  *
	  * @param aCallback The callback object which is used for cataloguing a MauiApplication
	  *
	  * @exception IOException If the jar file read fails
	  *
	  */
	
	protected void addJarFile (final File aJarFile,
							   final I_JarCallback aCallback)
		throws IOException
	{
		ZipFile theZipFile = new ZipFile (aJarFile);
		//
		//	Now scan through, and preload the ZipEntry objects for faster class loading
		//
		scanJarFile (theZipFile, aCallback);
		theZipFile.close ();
			/*if (theIndex == currentCapacity)
			{
				currentCapacity++;
			}
		}
		catch (Exception e)
		{
			if (e instanceof IOException)
			{
				throw (IOException) e;
			}
			e.printStackTrace ();
		}
		finally
		{
			if (loadAll && applicationsLoader && jarFiles [theIndex] != null)
			{
				try
				{
					jarFiles [theIndex].close ();
					new FileInputStream (aJarFile).close ();
				}
				catch (IOException e)
				{
				}
				jarFiles [theIndex] = null;
			}
		}*/
	}
	
	protected void loadAllExtensions ()
	{
		loadAllEntries (null);
		/*for (int i = 0; i < currentCapacity; i++)
		{
			if (jarFiles [i] != null)
			{
				try
				{
					jarFiles [i].close ();
				}
				catch (IOException e)
				{
				}
				jarFiles [i] = null;
			}
		}
		currentCapacity = 0;*/
	}
}


// ========================================================================
//                                               (c) 2001 Bitmovers Systems