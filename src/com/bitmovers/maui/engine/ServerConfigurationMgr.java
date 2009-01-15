// =============================================================================
// CHANGELOG:
//++ 181 MW 2001.08.09
// Added a properties variable for determining whether a Maui URL needs to be
// case sensitive. This variable "maui.case.sensitive" can either be
// true or false (true by default).
//++ 335 MW 2001.08.13
// Made a default value for the String Truncation length of 30. Added the
// string truncation property to the configuration properties hashtable.
// =============================================================================


package com.bitmovers.maui.engine;

import java.util.Properties;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.File;
import java.net.InetAddress;

import com.bitmovers.utilities.PropertiesLoader;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.I_SiteInitializer;
import com.bitmovers.maui.engine.logmanager.*;


// ========================================================================
// SINGLETON CLASS: ServerConfigurationManager   (c) 2001 Bitmovers Systems
// ========================================================================

/** This is used for loading configuration information for the maui
  * environment, and, optionally, for any application which requires
  * configuration information.<p>
  *
  * When this class is instantiated, it will look for a file called
  * maui.properties in the current working directory, and will load the
  * key/value pairs defined within.<p>
  *
  * Also, whenver an application is loaded it can request Properties to
  * be loaded on its behalf.  Given a location reference (ie. a folder
  * name), it will look in the folder for a file named
  * [folder name].properties.  If it is found it will load the properties
  * defined in this file.  If the file is not found, it will do nothing.
  *
  */

public class ServerConfigurationManager
{
  
  
  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Property Names	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	/** The path to the folder where the Maui applications reside. This folder
	  * will be scanned recursively for Maui application JAR files.
	  *
	  * default: [Maui Install Location]/Applications
	  *
	  */
	  
	public static final String MAUI_APPLICATION_LOCATION = "maui.application.location";
	
	public static final String MAUI_APPLICATION_WORK_SPACE_LOCATION = "maui.application.work.space.location";
	
	public static final String MAUI_APPLICATION_PREAMBLE = "maui.application.preamble";
	
	public static final String MAUI_APPLICATION_POSTAMBLE = "maui.application.postamble";
	
	public static final String MAUI_APPLICATION_STRING_TRUNCATION = "maui.application.string.truncation";
	
	public static final String MAUI_EXTENSION_LOCATION = "maui.extension.location";
	
	
	/** Sets the logging threshold level. Any log messages with a level greater 
	  * than this threshold will be ignored. The log threshold can be specified as
	  * either a string or a float between 0.0 and 1.0:
	  * 
	  * <pre>
	  * Class            String     Float
	  * ---------------------------------
	  * ErrorString      error      0.1
	  * WarningString    warning    0.4
	  * InfoString       info       0.6
	  * DebugString      debug      0.9
	  * </pre>
	  * 
	  * default: info
	  * 
	  */
	  
	public static final String MAUI_LOG_THRESHOLD = "maui.log.threshold";
	
	
	/** This is the default application which will be loaded if no application
	  * or an invalid application is specified in the URL. (For example, either
	  * http://mymauiserver.com:8080/ or http://mymauiserver.com:8080/blah will
	  * go to the maui.default.application.)
	  *
	  * default: com.bitmovers.maui.applications.WelcomeToMaui
	  *
	  */
	  
	public static final String MAUI_DEFAULT_APPLICATION = "maui.default.application";
	
	public static final String MAUI_DEFAULT_FOLDER = "maui.default.folder";
	
	public static final String MAUI_INTERNAL_APPLICATION = "maui.internal.application";
	
	public static final String MAUI_LOCATION = "maui.location";
	
	/** This is a boolean indicating if maui should check for reloading jar
	  * files.  This is done whenever an application for a particular jar
	  * file receives events.  If the jar file is out of date, then it will
	  * be reloaded, and all of the applications which are from it will be
	  * restarted.  Since this operation can be a bit expensive, it should be
	  * used only during development.
	  * 
	  * default: true
	  *
	  */
	  
	public static final String MAUI_AUTO_RELOAD = "maui.auto.reload";

	/** If auto reloading is enabled, then this is the interval (in seconds)
	  * between scans of the Applications folder area for new folders and jar
	  * files.  It also checks for the removal of folders and jar files, and unloads
	  * them from the ApplicationManager.
	  * 
	  * default: 10
	  *
	  */
	
	public static final String MAUI_APPLICATION_SCAN_TIME = "maui.application.scan.time";
	
	
	/** The path to the log file which Maui will use for all output. On Unix
	  * systems, this file must be writable by the user who starts the Maui
	  * Engine.
	  *
	  * default: [Maui Install Location]/mauilog.txt
	  *
	  */
	  
	public static final String MAUI_LOGFILE = "maui.logfile";
	
	
	/** The default port on which the Maui runtime answers requests. On Unix
	  * systems, if you select a port less than or equal to 1024, you will need
	  * to be root to start things up.
	  *
	  * default: 8080
	  *
	  */
	  
	public static final String MAUI_PORT = "maui.port";
	
	public static final String MAUI_SECURE_PORT = "maui.secure.port";
	
	public static final String MAUI_IP_ADDRESS = "maui.ip.address";
	
	public static final String MAUI_CERTIFICATE_FILE = "maui.certificate.file";
	
	public static final String MAUI_PASS_PHRASE = "maui.pass.phrase";
	
	public static final String MAUI_FAST_RANDOM = "maui.fast.random";
	
	public static final String MAUI_PROPERTIES = "maui.properties";
	
	/** The path to the Maui runtime JAR file which contains all of the Maui
	  * resource files (such as images, HTML/WML files, etc.).
	  *
	  * default: [Maui Install Location]/Maui.jar
	  *
	  */
	  
	public static final String MAUI_RESOURCES = "maui.resources";
	
	/** The number of minutes in which a session will timeout and be destroyed
	  * Larger timeout values will use more memory, as each time a session
	  * expires, memory is liberated. The more active sessions which have not
	  * expired, the more memory will be used.
	  *
	  * default: 10 (minutes)
	  *
	  */
	  
	public static final String MAUI_SESSION_TIMEOUT = "maui.session.timeout";
	
	/** This feature, if turned on, automatically performs a new client 
	  * connection to the Maui server just before the session times-out, 
	  * thereby extending the life of the session without requiring user 
	  * action. This feature may not be available on all devices.<p>
	  * 
	  * default: true
	  *
	  */
	  
	public static final String MAUI_SESSION_KEEPALIVE = "maui.session.keepalive";
	
	public static final String MAUI_SHARED_CLASSLOADER = "maui.shared.classloader";
	
	public static final String MAUI_DESKTOP_CLASS = "maui.desktop.class";
	
	public static final String MAUI_DESKTOP_BACKGROUND_COLOR = "maui.desktop.background.color";
	
	public static final String MAUI_APPLICATION_BACKGROUND_COLOR = "maui.application.background.color";
	
	public static final String MAUI_DESKTOP_TEXT_COLOR = "maui.desktop.text.color";
	
	public static final String MAUI_DESKTOP_LINK_COLOR = "maui.desktop.link.color";
	
	public static final String MAUI_TITLE_BAR_COLOR = "maui.title.bar.color";
	
	public static final String MAUI_WINDOW_COLOR = "maui.window.color";
	
	public static final String MAUI_TITLE_BAR_TEXT_COLOR = "maui.title.bar.text.color";
	
	public static final String MAUI_DESKTOP_PREAMBLE = "maui.desktop.preamble";
	
	public static final String MAUI_DESKTOP_POSTAMBLE = "maui.desktop.postamble";
	
	public static final String MAUI_PROFILE_OUTPUT = "maui.profile.output";
	
	public static final String MAUI_PROFILE_ENABLE = "maui.profile.enable";
	
	public static final String MAUI_THREAD_POOLING = "maui.thread.pooling";
	
	public static final String MAUI_THREAD_POOL_MAXIMUM = "maui.thread.pool.maximum";
	
	public static final String MAUI_THREAD_POOL_MINIMUM = "maui.thread.pool.minimum";
	
	public static final String MAUI_THREAD_AGE_LIMIT = "maui.thead.age.limit";
	
	public static final String MAUI_CONNECTION_KEEPALIVE = "maui.connection.keepalive";
	
	public static final String MAUI_DESKTOP_BACKGROUND_IMAGE = "maui.desktop.background.image";
	
	public static final String MAUI_APPLICATION_BACKGROUND_IMAGE = "maui.application.background.image";
	
	public static final String MAUI_SITE_INITIALIZER = "maui.site.initializer";
	
	public static final String MAUI_MONITOR_PORT = "maui.monitor.port";
	
	public static final String MAUI_SERVLET_PORT = "maui.servlet.port";
	
	public static final String MAUI_SERVLET_CLASS = "maui.servlet.class";
	
	public static final String MAUI_CONNECTION_BUFFER_SIZE = "maui.connection.buffer.size";
	
	public static final String MAUI_CONNECTION_AGE_LIMIT = "maui.connection.age.limit";
	
	public static final String MAUI_CONNECTION_POOL_MINIMUM = "maui.connection.pool.minimum";
	
	public static final String MAUI_SESSION_MAXIMUM = "maui.session.maximum";
	
	// Planned, but not used
	public static final String MAUI_PROCESS_HOUSEKEEPING_FREQUENCY = "maui.process.housekeeping.frequency";
	
	//++ 181 MW 2001.08.09
	public static final String MAUI_CASE_SENSITIVE = "maui.case.sensitive";
	//-- 181

	//++ 67 JL 2001.08.08
	/** The password needed to shut down the Maui engine via the MauiAdministrator application
	  */
	public static final String MAUI_ADMIN_PASSWORD = "maui.admin.password";
	//-- 67
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Default Property Values
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static final String MAUI_LOG_THRESHOLD_VALUE = new Float(LogManagerString.DEFAULT_LOG_LEVEL).toString();
	
	public static final String MAUI_DEFAULT_APPLICATION_VALUE = "com.bitmovers.maui.applications.WelcomeToMaui";
	
	public static final String MAUI_DEFAULT_FOLDER_VALUE = "";
	
	public static final String MAUI_INTERNAL_APPLICATION_VALUE = "com.bitmovers.maui.applications.WelcomeToMaui";
	
	public static final String MAUI_LOGFILE_VALUE = "mauilog.txt";
	
	public static final String MAUI_PORT_VALUE = "8080";
	
	public static final String MAUI_CERTIFICATE_FILE_VALUE = "maui.cert";
	
	public static final String MAUI_FAST_RANDOM_VALUE = "false";
	
	public static final String MAUI_SECURE_PORT_VALUE = "-1";
	
	public static final String MAUI_PROPERTIES_VALUE = "maui.properties";
	
	public static final String MAUI_RESOURCES_JAR = "Maui.jar";
	
	public static final String MAUI_SESSION_TIMEOUT_VALUE = "5";
	
	public static final String MAUI_SESSION_KEEPALIVE_VALUE = "true";
	
	public static final String MAUI_SHARED_CLASSLOADER_VALUE= "true";
	
	public static final String MAUI_DESKTOP_CLASS_VALUE = "com.bitmovers.maui.components.MDesktop";
	
	public static final String MAUI_DESKTOP_BACKGROUND_COLOR_VALUE = "#003366";
	
	public static final String MAUI_TITLE_BAR_COLOR_VALUE = "#336699";
	
	public static final String MAUI_WINDOW_COLOR_VALUE = "#CCCCCC";

	public static final String MAUI_TITLE_BAR_TEXT_COLOR_VALUE = "#FFFFFF";
	
	public static final String MAUI_DESKTOP_TEXT_COLOR_VALUE = "#000000";
	
	public static final String MAUI_DESKTOP_LINK_COLOR_VALUE = "#003333";
	
	public static final String MAUI_DESKTOP_PREAMBLE_VALUE = "preamble";
	
	public static final String MAUI_DESKTOP_POSTAMBLE_VALUE = "postamble";
	
	public static final String MAUI_PROFILE_OUTPUT_VALUE = "profile.txt";
	
	public static final String MAUI_PROFILE_ENABLE_VALUE = "false";
	
	public static final String MAUI_THREAD_POOLING_VALUE = "true";
	
	public static final String MAUI_THREAD_POOL_MAXIMUM_VALUE = "100";
	
	public static final String MAUI_THREAD_POOL_MINIMUM_VALUE = "10";
	
	public static final String MAUI_THREAD_AGE_LIMIT_VALUE = "20000";
	
	public static final String MAUI_CONNECTION_KEEPALIVE_VALUE = "true";
	
	public static final String MAUI_APPLICATION_FOLDER_VALUE = "Applications";
	
	public static final String MAUI_EXTENSION_FOLDER_VALUE = "Extensions";

	public static final String MAUI_APPLICATION_WORK_SPACE_FOLDER_VALUE = ".ApplicationsWorkSpace";
	
	public static final String MAUI_AUTO_RELOAD_VALUE = "true";
	
	public static final String MAUI_APPLICATION_SCAN_TIME_VALUE = "10";
	
	public static final String MAUI_SERVLET_PORT_VALUE = "-1";
	
	public static final String MAUI_SERVLET_CLASS_VALUE = "com.bitmovers.maui.engine.servlet.MauiRemoteServlet";
		
	public static final String MAUI_CONNECTION_BUFFER_SIZE_VALUE = "20000";
	
	public static final String MAUI_CONNECTION_AGE_LIMIT_VALUE = "20000";
	
	public static final String MAUI_CONNECTION_POOL_MINIMUM_VALUE = "10";

	public static final String MAUI_SESSION_MAXIMUM_VALUE = "100";

	// Planned, but not used
	public static final String MAUI_PROCESS_HOUSEKEEPING_FREQUENCY_VALUE = "20000";

	//++ 181 MW 2001.08.09
	public static final String MAUI_CASE_SENSITIVE_VALUE = "false";
	//-- 181
	
	//++ 335 MW 2001.08.10
	public static final String MAUI_APPLICATION_STRING_TRUNCATION_VALUE = "30";
	//-- 335
	
	//++ 67 JL 2001.08.08
	public static final String MAUI_ADMIN_PASSWORD_VALUE = "admin";
	//-- 67
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	
	private static final Properties configurationProperties = new Properties ();
	private static final HashMap globalProperties = new HashMap ();
	private static final ServerConfigurationManager scm = new ServerConfigurationManager ();
	
	private static String mauiLocationValue;
	private static String mauiApplicationLocationValue;
	private static String mauiExtensionLocationValue;
	private static String mauiWorkLocationValue;
	private static String mauiResourcesValue;
	
	private final PropertiesLoader propertiesLoader;
	
	private boolean initDone = false;
	
	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

	/** This constructor is private to enforce the ServerConfigurationManager 
	  * as a Singleton object.
	  *
	  */
	  
	private ServerConfigurationManager ()
	{
		mauiLocationValue = System.getProperty ("user.dir");
		mauiApplicationLocationValue = mauiLocationValue + File.separator + MAUI_APPLICATION_FOLDER_VALUE;
		mauiWorkLocationValue = mauiLocationValue + File.separator + MAUI_APPLICATION_WORK_SPACE_FOLDER_VALUE;
		mauiExtensionLocationValue = mauiLocationValue + File.separator + MAUI_EXTENSION_FOLDER_VALUE;
		
		mauiResourcesValue = System.getProperty ("java.class.path");
		int theMauiJarIndex = mauiResourcesValue.indexOf ("maui");

		if (theMauiJarIndex != -1)
		{
			theMauiJarIndex = mauiResourcesValue.indexOf (".jar", theMauiJarIndex) + 4;
			mauiResourcesValue = mauiResourcesValue.substring (0, theMauiJarIndex);
			int theSeparator = mauiResourcesValue.lastIndexOf (File.pathSeparator);

			if (theSeparator != -1)
			{
				mauiResourcesValue = mauiResourcesValue.substring (theSeparator + 1);
			}
		}
		else
		{
			mauiResourcesValue = mauiLocationValue + File.separator + MAUI_RESOURCES_JAR;
		}
		
		//	Load default values.
		configurationProperties.put (MAUI_APPLICATION_LOCATION, mauiApplicationLocationValue);
		configurationProperties.put (MAUI_APPLICATION_WORK_SPACE_LOCATION, mauiWorkLocationValue);
		configurationProperties.put (MAUI_EXTENSION_LOCATION, mauiExtensionLocationValue);
		configurationProperties.put (MAUI_LOG_THRESHOLD, MAUI_LOG_THRESHOLD_VALUE);
		configurationProperties.put (MAUI_DEFAULT_APPLICATION, MAUI_DEFAULT_APPLICATION_VALUE);
		configurationProperties.put (MAUI_DEFAULT_FOLDER, MAUI_DEFAULT_FOLDER_VALUE);
		configurationProperties.put (MAUI_INTERNAL_APPLICATION, MAUI_INTERNAL_APPLICATION_VALUE);
		configurationProperties.put (MAUI_LOCATION, mauiLocationValue);
		configurationProperties.put (MAUI_LOGFILE, MAUI_LOGFILE_VALUE);
		configurationProperties.put (MAUI_PORT, MAUI_PORT_VALUE);
		configurationProperties.put (MAUI_SECURE_PORT, MAUI_SECURE_PORT_VALUE);
		configurationProperties.put (MAUI_CERTIFICATE_FILE, MAUI_CERTIFICATE_FILE_VALUE);
		configurationProperties.put (MAUI_FAST_RANDOM, MAUI_FAST_RANDOM_VALUE);
		configurationProperties.put (MAUI_PROCESS_HOUSEKEEPING_FREQUENCY, MAUI_PROCESS_HOUSEKEEPING_FREQUENCY_VALUE);
		//++ 181 MW 2001.08.09
		configurationProperties.put (MAUI_CASE_SENSITIVE, MAUI_CASE_SENSITIVE_VALUE);
		//-- 181
		//++ 335 MW 2001.08.10
		configurationProperties.put (MAUI_APPLICATION_STRING_TRUNCATION, MAUI_APPLICATION_STRING_TRUNCATION_VALUE);
		//-- 335
		configurationProperties.put (MAUI_PROPERTIES, MAUI_PROPERTIES_VALUE);
		configurationProperties.put (MAUI_SESSION_TIMEOUT, MAUI_SESSION_TIMEOUT_VALUE);
		configurationProperties.put (MAUI_SESSION_KEEPALIVE, MAUI_SESSION_KEEPALIVE_VALUE);
		configurationProperties.put (MAUI_RESOURCES, mauiResourcesValue);
		configurationProperties.put (MAUI_SHARED_CLASSLOADER, MAUI_SHARED_CLASSLOADER_VALUE);
		configurationProperties.put (MAUI_DESKTOP_CLASS, MAUI_DESKTOP_CLASS_VALUE);
		configurationProperties.put (MAUI_DESKTOP_BACKGROUND_COLOR, MAUI_DESKTOP_BACKGROUND_COLOR_VALUE);
		configurationProperties.put (MAUI_TITLE_BAR_COLOR, MAUI_TITLE_BAR_COLOR_VALUE);
		configurationProperties.put (MAUI_TITLE_BAR_TEXT_COLOR, MAUI_TITLE_BAR_TEXT_COLOR_VALUE);
		configurationProperties.put (MAUI_WINDOW_COLOR, MAUI_WINDOW_COLOR_VALUE);
		configurationProperties.put (MAUI_DESKTOP_TEXT_COLOR, MAUI_DESKTOP_TEXT_COLOR_VALUE);
		configurationProperties.put (MAUI_DESKTOP_LINK_COLOR, MAUI_DESKTOP_LINK_COLOR_VALUE);
		configurationProperties.put (MAUI_DESKTOP_PREAMBLE, MAUI_DESKTOP_PREAMBLE_VALUE);
		configurationProperties.put (MAUI_DESKTOP_POSTAMBLE, MAUI_DESKTOP_POSTAMBLE_VALUE);
		configurationProperties.put (MAUI_PROFILE_OUTPUT, MAUI_PROFILE_OUTPUT_VALUE);
		configurationProperties.put (MAUI_PROFILE_ENABLE, MAUI_PROFILE_ENABLE_VALUE);
		configurationProperties.put (MAUI_THREAD_POOLING, MAUI_THREAD_POOLING_VALUE);
		configurationProperties.put (MAUI_CONNECTION_KEEPALIVE, MAUI_CONNECTION_KEEPALIVE_VALUE);
		configurationProperties.put (MAUI_AUTO_RELOAD, MAUI_AUTO_RELOAD_VALUE);
		configurationProperties.put (MAUI_APPLICATION_SCAN_TIME, MAUI_APPLICATION_SCAN_TIME_VALUE);
		configurationProperties.put (MAUI_SERVLET_PORT, MAUI_SERVLET_PORT_VALUE);
		configurationProperties.put (MAUI_SERVLET_CLASS, MAUI_SERVLET_CLASS_VALUE);
		configurationProperties.put (MAUI_CONNECTION_AGE_LIMIT, MAUI_CONNECTION_AGE_LIMIT_VALUE);
		configurationProperties.put (MAUI_CONNECTION_POOL_MINIMUM, MAUI_CONNECTION_POOL_MINIMUM_VALUE);
		configurationProperties.put (MAUI_SESSION_MAXIMUM, MAUI_SESSION_MAXIMUM_VALUE);
		configurationProperties.put (MAUI_THREAD_POOL_MAXIMUM, MAUI_THREAD_POOL_MAXIMUM_VALUE);
		configurationProperties.put (MAUI_THREAD_POOL_MINIMUM, MAUI_THREAD_POOL_MINIMUM_VALUE);
		configurationProperties.put (MAUI_THREAD_AGE_LIMIT, MAUI_THREAD_AGE_LIMIT_VALUE);
	

		//++ 67 JL 2001.08.08
		configurationProperties.put (MAUI_ADMIN_PASSWORD, MAUI_ADMIN_PASSWORD_VALUE);
		//-- 67
		configurationProperties.put (MAUI_CONNECTION_BUFFER_SIZE, MAUI_CONNECTION_BUFFER_SIZE_VALUE);
	
		
		propertiesLoader = PropertiesLoader.getInstance ();

		// DEPRECATED BEYOND BELIEF
		// configurationProperties.put(MAUI_HTML_SERVLET_URL, MAUI_HTML_SERVLET_URL_VALUE);
		// configurationProperties.put(MAUI_WML_SERVLET_URL, MAUI_WML_SERVLET_URL_VALUE);
	}
	

  // ----------------------------------------------------------------------
  // METHOD: getInstance
  // ----------------------------------------------------------------------

	/** Gets an instance of the ServerConfigurationManager.  Since the 
	  * ServerConfigurationManager is a singleton, this will always be the 
	  * same object.
	  *
	  * @return  An instance of the ServerConfigurationManager.
	  *
	  */
	  
	public static ServerConfigurationManager getInstance ()
	{
		return scm;
	}


  // ----------------------------------------------------------------------
  // METHOD: initialize
  // ----------------------------------------------------------------------
	
  // ----------------------------------------------------------------------
  // METHOD: initialize
  // ----------------------------------------------------------------------
	
	/** Initialize the ServerConfigurationManager
	  * 
	  * @param aArgs Command line parameters
	  *
	  */
	  
	public void initialize (final String[] aArgs)
	{
		if (!initDone)
		{
			//
			//	First load up the properties from the default maui properties file.
			//
			loadPropertiesFromFolder("maui", "", configurationProperties);
			
			//
			//	Now load up the properties with the arguments passed in
			//
			propertiesLoader.loadProperties(aArgs, configurationProperties);
			
			initDone = true;
			
		}
	}
	
	/** List the properties to System.out
	  *
	  */
	public void listProperties ()
	{
		configurationProperties.list (System.out);
	}
	
	

  // ----------------------------------------------------------------------
  // METHOD: getJarResources
  // ----------------------------------------------------------------------
	
	/** Get the Jar Resources reference
	  *
	  * @return The Jar Resources
	  *
	  */

	public ResourceManager getJarResources()
	{
		return ResourceManager.getInstance();
	}
	

  // ----------------------------------------------------------------------
  // METHOD: loadPropertiesFromFolder
  // ----------------------------------------------------------------------
	
	/** Load properties from a folder.
	  *
	  * @param aLocation A reference to a location.  This serves as the
	  *        base for a property name, or the actual folder name to look
	  *        for the properties file
	  *
	  * @param aSubFolder This is used to indicate a subfolder location
	  *
	  * @param aProperties The Properties object to use.  If this is null,
	  *        a new object will be created
	  *
	  * @return A Properties object.
	  *
	  */

	public Properties loadPropertiesFromFolder (final String aLocation,
	                                            final String aSubFolder,
	                                            final Properties aProperties)
	{
		String theLocation = null;
		String thePropertiesFile = null;
		Properties retVal = aProperties;
		
		if (aProperties != null)
		{
			theLocation = getProperty (aLocation + ".location");
			thePropertiesFile = getProperty (aLocation + ".properties");
		}

		if (thePropertiesFile == null)
		{
			thePropertiesFile = aLocation + ".properties";
		}
		
		if (theLocation == null)
		{
			theLocation = mauiApplicationLocationValue;
			if (aSubFolder != null && aSubFolder.length () > 0)
			{
				theLocation += File.separator + aSubFolder;
			}
		}
		
		File theFile = new File (theLocation, thePropertiesFile);
		if (theFile.exists())
		{
			retVal = propertiesLoader.loadProperties(new String[] { "@" + theFile }, aProperties);
		}

		return (retVal == null ? new Properties() : retVal);
	}
	

  // ----------------------------------------------------------------------
  // METHOD: getProperty
  // ----------------------------------------------------------------------

	/** Get a property value
	  *
	  * @param aKey The key of access
	  *
	  * @return The property value, or null if it wasn't found
	  *
	  */
	  
	public String getProperty(String aKey)
	{
		return configurationProperties.getProperty(aKey);
	}
	

  // ----------------------------------------------------------------------
  // METHOD: getProperty
  // ----------------------------------------------------------------------

	/** Put a property value
	  *
	  * @param akey The key of access
	  * 
	  * @param aValue The value for the property
	  *
	  */

	public void setProperty(String aKey, String aValue)
	{	
		configurationProperties.put(aKey, aValue);
	}
	
	/** Get the global Properties
	  *
	  * @return The global properties
	  */
	public HashMap getGlobalProperties ()
	{
		return globalProperties;
	}

  // ----------------------------------------------------------------------
  // METHOD: getProperties
  // ----------------------------------------------------------------------

	/** Get the Properties object
	  *
	  * @return The SCM Properties object
	  *
	  */
	  
	public Properties getProperties()
	{
		return configurationProperties;
	}
	
	//
	//	Get a global value
	//
	
	/** Get a global value
	  *
	  * @param aKey The key of access to the HashMap
	  *
	  * @return The value assoicated with the key, or null if not found
	  */
	public Object getGlobalProperty (Object aKey)
	{
		return globalProperties.get (aKey);
	}
	
	//
	//	Put a global value
	//
	
	/** Put a global value
	  *
	  * @param aKey The key of access to the HashMap
	  * @param aValue The value assoicated with the key
	  */
	public void putGlobalProperty (Object aKey, Object aValue)
	{
		globalProperties.put (aKey, aValue);
	}
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF