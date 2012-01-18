package com.bitmovers.maui;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.render.I_Renderer;
import com.bitmovers.maui.engine.wmlcompositor.WMLCompositor;
import com.bitmovers.maui.engine.resourcemanager.ResourceManager;
import com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.httpserver.HTTPSession;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.*;


// ========================================================================
// CLASS: MauiApplication
// ========================================================================

/** This is the super class used for all Maui applications. In order for an
  * application to be recognized and loaded by Maui, it must subclass this
  * class like this:<p>
  * 
  * <pre>
  *  class MyFirstMauiApp extends MauiApplication
  *  {
  *    public MyFirstMauiApp(Object anInitializer)
	*    {
	*      super (anInitializer, "My First Maui App!");
	*      // ...
  *    }
  * }
  * </pre>
  * 
  * <code>MauiApplication</code> provides information on the operating
  * environment to its subclasses. For example, a reference to the session
  * object may be obtained through <code>getSession()</code>. It also has 
  * properties which allow the subclass to alter the behaviour of the 
  * application (e.g. application chaining is done by simply calling 
  * <code>setChainApplication()</code>.
  *
  */

public abstract class MauiApplication extends MContainer
	implements Serializable,
			   HasPostValue,
			   MActionListener
{
	
	
	/** <code>maui.user.dir</code> is the name of the working folder for this application.  If
	  * it doesn't already exist, it is created by Maui when the application is created.
	  */
	public static final String MAUI_USER_DIR = "maui.user.dir";
	
	/** <code>maui.application.invisible</code> is a property that can be set within the application's
	  * property file.  It indicates if the application is visible when browsing through
	  * all of the applications via the <code>ApplicationManager</code>
	  */
	public static final String MAUI_APPLICATION_INVISIBLE = "maui.application.invisible";
	
	/** The full name for the working folder name for the <code>MauiApplication<code>.
		*
	  */
	protected String mauiWorkingLocation;
	
	/** Hashtable of event listeners which are used to set chain application names
		* @invisible
	  */
	private Hashtable chainListeners = new Hashtable (10);
	
	/** This contains all of the components created within this Maui application
		*
		*/
	public Hashtable componentMap = new Hashtable();

	/** This is the PID for this MauiApplication().  The PID is a unique number (derived
		* from the hashcode for the MauiApplication object).
		* @invisible
		*/
  private String mPID = ProcessManager.getInstance().registerProcess(this);
  
  /** Reference to the ServerConfigurationManager().
  	* @invisible
  	*/
  private static ServerConfigurationManager scm = null;
  
  /** Boolean indicating if static once-only initialization for all Maui applications
  	* has been completed yet.
  	* @invisible
  	*/
  private static boolean initDone = false;
  
  /** Reference to the appropriate compositor for this application.
  	* @invisible
  	*/
  private Compositor compositor;
  
  /** Reference to the MDesktop object.  There is only one MDesktop per client session.
    * @invisible
    */
  private MDesktop desktop;
  
  /** The header values (generally from HTTP) from the last request.
  	* @invisible
  	*/
  private Map requestHeaderValues = null;
  
  /** The suite name (i.e. the name of the jar file containing this application).
  	* @invisible
  	*/
  private String applicationSuiteName = null;
  
  /** The folder name
    * @invisible
    */
  private String folderName;
  
  /** The name for this application (this can be different than the full class name).
  	* @invisible
  	*/
  private String applicationName = null;
  
  /** The full class name for the application.
  	* @invisible
  	*/
  private final String applicationClassName;
  
  /** The name used to invoke the application.
  	* @invisible
  	*/
  private String runtimeName = null;
  
  /** The shortest, unambiguous name for this application.
  	* @invisible
  	*/
  private String shortName = null;
  
  /** The application folder.
  	* @invisible
  	*/
  private String folder = "";
  
  /** The target chain application name. If this is null, then no chaining occurs.
  	* Non-null indicates that application chaining will occur.
  	*/
  private String chainApplication = null;	// Application to chain to.
  
  /** Boolean indicating if the application should be restarted or not.
  	* @invisible
  	*/
  private boolean forceApplicationStart = false;	// Force the application to start (or restart).
  
  /** The unique ID for the HTTPSession that this application belongs to.
  	* @invisible
  	*/
  private String sessionID = null;
  
  /** Reference to the actual HTTPSession to which this application belongs.
  	* @invisible
  	*/
  private HTTPSession session = null;
  
  /**	A URL string which is used for redirecting the client browser outside of the Maui 
  	* environment (ie. an external link)
  	* @invisible
  	*/
  private String urlString = null;
  
  /** Property object containing all of the runtime properties associated with this
 		* application.  These are an accumulation of some maui.properties, plus properties
 		* found in any files or resources which correspond to the application name.
 		* @invisible
 		*/
  private Properties properties = null;
  
  /** Boolean indicating if this is the default Maui application or not.
  	* @invisible
  	*/
  private boolean defaultApplication = false;
  
  /** The HTMLTemplate to pass to the StringParser as the first step in rendering the
  	* application.
  	* @invisible
  	*/
  private String HTMLTemplate;
  
  /** The WMLTemplate to pass to the StringParser as the first step in rendering the
  	* application.
  	* @invisible
  	*/
	private String WMLTemplate;
	
	/** The actual rendered HTML stored as a String.
		* @invisible
		*/
	private String renderedHTML;
	
	/** The actual rendered HTML stored as a String.
		* @invisible
		*/
	private String renderedWML;
	
	/** The pseudo events used in building up the HTTPResponse().  Thse are typically only
		* used if a client browser doesn't support cookies, or a procedure can't be represented
		* through the maui event queue.
		* @invisible
		*/
	private StringBuffer pseudoEvents = null;
	
	/** Boolean indicating if this application is marked for exit or not.
	  * @invisible
	  */
	private boolean exit = false;
	
	/** Boolean indicating if all applications in this session should exit.
		* @invisible
	  */
	private boolean exitAll = false;
	
	/** The title to use for this application.  At render time this is set in the MFrame.
		* @invisible
		*/
  protected String title;
  
  /** The preamble
	  * @invisible
	  */
  private String [] preamble = null;
  
  /** The postamble
	  * @invisible
	  */
  private String [] postamble = null;
  
  /**
  * The application background color
  * @invisible
  */
  private String backgroundColor = null;
  
  /**
  * The resource name for the background image
  * @invisible
  */
  private String backgroundImage = null;
  
  private int sequenceNumber = 0;
  
  private Hashtable cookies = new Hashtable (5);
  private Hashtable inboundCookies;
  private Object synchBlock = new Object ();
  protected String applicationPath;
  /** Each application can be handled through either a servlet or directly through Maui
    * This indicates the prefix to use for creating responses.
    */
  private String servletURL = "/";
  private AuthorizationManager am = AuthorizationManager.getInstance ();
  
  
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR: MauiApplication
	// ----------------------------------------------------------------------
	
	/** Standard constructor.
		* 
		* @param anInitializer  an object which is used to initialize the 
		*                       <code>MauiApplication</code>. It is a callback 
		*                       to the <code>ApplicationManager</code>, which 
		*                       in turn completes the initialization of the 
		*                       <code>MauiApplication</code>.
		* 
		* @param aTitle         the title of this application, to be used in 
		*                       the heading of the main MFrame.
		* 
		*/
		
	public MauiApplication (Object anInitializer, String aTitle)
	{
		((I_ApplicationInitializer)anInitializer).initializeApplication(this);
		applicationClassName = getClass ().getName ();
		
		try
		{
	    // Handle the possibility of a null title
	    if (aTitle == null)
	    {
	      title = "Untitled Window";
	    }
	    else
	    {
			  title = aTitle;
			}
			
			setLayout(new MFlowLayout(this));
			
			try
			{
			  HTMLTemplate = new String(ResourceManager.getInstance().getResourceString("com/bitmovers/maui/MauiApplication/MauiApplication.html"));
		  }
		  catch (ResourceNotFoundException e)
		  {
		  	System.err.println(new ErrorString("[MauiApplication] - " + e.getMessage() + ": MauiApplication.html"));
		  }
		  
			try
			{
				WMLTemplate = new String(ResourceManager.getInstance().getResourceString("com/bitmovers/maui/MauiApplication/MauiApplication.wml"));
		  }
		  catch (ResourceNotFoundException e)
		  {
		  	System.err.println(new ErrorString("[MauiApplication] - " + e.getMessage() + ": MauiApplication.wml"));
		  }
		}
		catch(NullPointerException e)
		{
			System.err.println("[MauiApplication] NullPointer during construction!");
		}
	}
	
  
	// ----------------------------------------------------------------------
	// STATIC METHOD: staticInitialize
	// ----------------------------------------------------------------------
	
  /** Perform once-only initialization for the <code>MauiApplication</code> 
    * class.  This sets up some static variables.
  	* 
  	* @invisible
  	* 
  	*/
  	
  public static void staticInitialize()
  {
  	if (!initDone)
  	{
  		scm = ServerConfigurationManager.getInstance ();
  		initDone = true;
  	}
  }
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: getJarResources
	// ----------------------------------------------------------------------

	/** This method provides backwards compatibility by connecting the old 
	  * getJarResources() method to the new ResourceManager.
	  * 
	  * @deprecated
	  * 
	  * @invisible
	  * 
	  */
	  
  public static ResourceManager getJarResources()
  {
  	staticInitialize ();
		return ResourceManager.getInstance();
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: initialize
	// ----------------------------------------------------------------------
	
  /** Perform per application initialization. This is a final method, so it 
    * will never be overridden.
  	*
  	* @param aRequestHeaderValues  the request header values contained in 
  	*                              the HTTP request when this application 
  	*                              was created.
  	* 
  	* @invisible
  	* 
  	*/
  	
  public final void initialize (Map aRequestHeaderValues)
  {
  	requestHeaderValues = aRequestHeaderValues;
  	compositor = CompositionManager.getInstance ().getCompositor (aRequestHeaderValues);
  }
  
  
  // ----------------------------------------------------------------------
	// METHOD: finish
	// ----------------------------------------------------------------------
	
  /** Performs any cleanup required by the application if it is exiting.
    *
    */
    
  public final void finish ()
  {
  	doFinish ();
  }
  
  
  // ----------------------------------------------------------------------
	// METHOD: doFinish
	// ----------------------------------------------------------------------
	
  /** Application Developer may override the method to perform exit cleanup.
	  * 
	  */
	  
  public void doFinish ()
  {
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getCompositor
	// ----------------------------------------------------------------------
	
  /** Returns the compositor for this application (e.g. an HTML or WML 
    * compositor).
  	*
  	* @return the compositor for this application.
  	* 
  	* @invisible
  	* 
  	*/
  	
  public Compositor getCompositor ()
  {
  	return compositor;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
  /** This is the default value to post for a Maui application.  It is composed 
    * of an "MA_" prefix plus the session id associated with application.
  	*
  	* @return the post value for the application
  	*
  	* @invisible
  	* 
  	*/
  	
  public String getPostValue ()
  {
		return "MA_" + sessionID;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getRequestHeaderValues
	// ----------------------------------------------------------------------
	
	/** Returns the request header values used when the application was created.
		*
		* @return a Map of the request header values
		* 
		* @invisible
		* 
		*/
		
  public Map getRequestHeaderValues ()
  {
  	return requestHeaderValues;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: setApplicationName
	// ----------------------------------------------------------------------
	
  /** Sets the name of the application.
  	*
  	* @param aApplicationName  the new application name.
  	* 
  	* @invisible
  	* 
  	*/
  	
  public void setApplicationName (String aApplicationName)
  {
  	applicationName = aApplicationName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: setBackgroundColor
	// ----------------------------------------------------------------------
	
	/** Sets the background colour for this application.
	  *
	  * @param aColor  the color String (as a hex RGB combination). If this is null, 
	  * 								then the <code>Desktop</code> background color will be used.
	  */
	  
	public void setBackgroundColor(String aColor)
	{
		backgroundColor = aColor;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: getBackgroundColor
	// ----------------------------------------------------------------------
	
	/** Returns the background colour for this application.
	  *
	  * @return the background color (as a hex RGB string). Null means use the 
	  *	<code>Desktop</code> background color.
	  *
	  */
	  
	public String getBackgroundColor ()
	{
		return backgroundColor;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: setBackgroundImage
	// ----------------------------------------------------------------------
	
	/** Sets the background image for this application.
	  *
	  * @param aBackgroundImage  the name of the resource for the background image. 
	  *														Null means use the <code>Desktop</code> background image.
	  *
	  */
	  
	public void setBackgroundImage(String aBackgroundImage)
	{
		backgroundImage = aBackgroundImage;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: getBackgroundImage
	// ----------------------------------------------------------------------
	
	/** Returns the background image for this application.
	  *
	  * @return the background image for this application. Null means use the 
	  *	<code>Desktop</code> background image.
	  *
	  */
	  
	public String getBackgroundImage ()
	{
		return backgroundImage;
	}

	// ----------------------------------------------------------------------
	// METHOD: setDesktop
	// ----------------------------------------------------------------------
	
	/** Sets the MDesktop for the application.
		*
		* @param  aDesktop  a reference to the new <code>MDesktop</code> object.
		* 
		*/
		
  public void setDesktop (MDesktop aDesktop)
  {
  	desktop = aDesktop;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getDesktop
	// ----------------------------------------------------------------------
	
  /** Returns the <code>MDesktop</code> for the application
  	*
  	* @return the <code>MDesktop</code>
  	* 
  	*/
  	
  public MDesktop getDesktop ()
  {
  	return desktop;
  }
  
	// ----------------------------------------------------------------------
	// METHOD: addComponent
	// ----------------------------------------------------------------------
	
	/**	Override <code>MContainer</code>'s <code>addComponent()</code> 
	  * method to ensure that added components always appear at the center of 
	  * the <code>MauiApplication</code>.
	  * 
	  * @param  comp         the MComponent being added.
	  * 
	  * @param  constraints  layoutManager constraints.
	  * 
	  * @param  index        not used.
	  *
		*/
	
	protected void addComponent(MComponent comp, Object constraints, int index)
	{
		super.remove(0);
		super.addComponent(comp, MBorderLayout.CENTER, 0);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setApplicationSuiteName
	// ---------------------------------------------------------------------------
	
	/** Sets the name of the application suite
	  * 
		* @param aApplicationSuiteName  the name of the application suite.
		*
		* @invisible
		* 
		*/
		
	public void setApplicationSuiteName (String aApplicationSuiteName)
  {
  	applicationSuiteName = aApplicationSuiteName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: setSession
	// ----------------------------------------------------------------------
	
  /** Sets a reference to the HTTPSession for this application.
    * 
  	* @param aSession  the <code>HTTPSession</code>reference.
  	* 
  	* @invisible
  	* 
  	*/
  	
  public void setSession (HTTPSession aSession)
  {
  	session = aSession;
  	sessionID = session.getSessionID ();
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getSessionID
	// ----------------------------------------------------------------------
	
  /** Returns the unique session ID of the session associated with this 
    * <code>MauiApplication</code>.
  	* 
  	* @return The session ID.
  	* 
  	* @invisible
  	* 
  	*/
  	
  public String getSessionID ()
  {
  	return sessionID;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getSession
	// ----------------------------------------------------------------------
	
  /** Returns the <code>HTTPSession</code> associated with this 
    * <code>MauiApplication</code>.
  	* 
  	* @return The session object.
  	* 
  	*/
  	
  public HTTPSession getSession ()
  {
  	return session;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getApplicationSuiteName
	// ----------------------------------------------------------------------
	
	/** Returns the name of the application suite (i.e. the name of the jar file 
	  * that contains this MauiApplication.
		*
		* @return The application suite name
		*
		* @invisible
		* 
		*/
		
  protected String getApplicationSuiteName ()
  {
  	return applicationSuiteName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getSynchBlock
	// ----------------------------------------------------------------------
	
  /** Returns the synchronization object.
	  *
	  * @return The synchronization object
	  * 
	  */
	  
  public Object getSynchBlock()
  {
  	return synchBlock;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: setFolderName
	// ----------------------------------------------------------------------
	
  /** Sets the application folder name.
    *
    * @param aFolderName  the name of the folder
    * 
    * @invisible
    * 
    */
	
  public void setFolderName(String aFolderName)
  {
  	folderName = aFolderName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getFolderName
	// ----------------------------------------------------------------------
	
  /** Returns the application folder.
    *
    * @return The application folder.
    *
    * @invisible
    * 
    */
    
  public String getFolderName ()
  {
  	return folderName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: setShortName
	// ----------------------------------------------------------------------
	
	/** Sets the "short" name for this application.
		*
		* @param aShortName  the short name.
		*
		* @invisible
		* 
		*/
		
  public void setShortName (String aShortName)
  {
  	shortName = aShortName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getShortName
	// ----------------------------------------------------------------------
	
	/** Returns the short name for the application.
		* 
		* @return the short name.
		* 
		* @invisible
		* 
		*/
		
  public String getShortName ()
  {
  	return shortName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getApplicationName
	// ----------------------------------------------------------------------
	
	/** Returns the application name.
		*
		* @return The application name.
		* 
		*/
		
  public String getApplicationName ()
  {
  	return applicationName;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: getRuntimeName
	// ---------------------------------------------------------------------------
	
	/** Returns the name that was used to invoke this application.
		*
		* @return the application name.
		* 
		*/
		
  public String getRuntimeName ()
  {
  	return runtimeName;
  }
  
	// ---------------------------------------------------------------------------
	// METHOD: setRuntimeName
	// ---------------------------------------------------------------------------
	
	/** Sets the runtime name for this application. This is the name that was used
		* to invoke the application.
		*
		* @param aRuntimeName  the name of the application.
		* 
		* @invisible
		* 
		*/
		
  public void setRuntimeName (String aRuntimeName)
  {
  	runtimeName = aRuntimeName;
  	int theSlash = runtimeName.lastIndexOf ("/");
  	folder = (theSlash == -1 ? "" : runtimeName.substring (0, theSlash));
  	chainApplication = aRuntimeName;
  	mauiWorkingLocation = "maui." + runtimeName + ".working.folder";
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getFolder
	// ----------------------------------------------------------------------
	
  /** Returns the folder name in which this application resides.
    *
    * @return The folder in which this application resides.
    * 
    */
    
  public String getFolder ()
  {
  	return folder;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getApplicationAddress
	// ----------------------------------------------------------------------
	
  /** Returns the full address of this application.
    *
    * @return The full address of the application.
    * 
    */
    
  public String getApplicationAddress ()
  {
  	return (folder.length () == 0 ? "" : folder + "/") + applicationClassName;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: setURLString
	// ----------------------------------------------------------------------
	
  /** Sets an external URL for redirecting the browser upon the next user 
    * action.
    * 
    * @param aURL  the external URL.
    * 
    */
    
  public void setURLString (String aUrlString)
  {
  	urlString = aUrlString;
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getURLString
	// ----------------------------------------------------------------------
	
  /** Returns the URL (if any) for redirecting the browser.
    *
    * @return The redirection URL.
    * 
    */
    
  public String getURLString ()
  {
  	return urlString;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setChainApplicationName
	// ---------------------------------------------------------------------------
	
	/** Sets the name of application to chain to on the next user action.
		*
		* @param  aChainApplication  the target application name.
		* 
		*/
		
  public void setChainApplicationName (String aChainApplication)
  {
  	/*String theServletURL = getServletURL ();
  	if (theServletURL != null && aChainApplication != null)
  	{
	  	StringBuffer theChainApplication = new StringBuffer (theServletURL);
	  	int theOffset = theChainApplication.length () - 1;
	  	theOffset = (theChainApplication.charAt (theOffset) == '/' && aChainApplication.charAt (0) == '/' ? 1 : 0);
	  	theChainApplication.append (aChainApplication.substring (theOffset));
	  	chainApplication = theChainApplication.toString ();
	  }
	  else
	  {*/
	  	chainApplication = aChainApplication;
	  //}
  	if (sessionID != null && !session.getHasCookie ())
  	{
  		appendPseudoEvent ("sessionID", sessionID);
  	}
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: appendPseudoEvent
	// ----------------------------------------------------------------------
	
  /** Appends pseudo events to the pseudo events string.
    *
    * @param aComponentID the component ID for the pseudo event.
    * 
    * @param aValue 			the value for the pseudo event.
    * 
    * @invisible
    * 
    */
    
  public void appendPseudoEvent (String aComponentID, String aValue)
  {
  	if (pseudoEvents == null)
  	{
  		pseudoEvents = new StringBuffer ("/pseudo");
  	}
  	pseudoEvents.append ("/");
  	pseudoEvents.append (aComponentID);
  	pseudoEvents.append ("_");
  	pseudoEvents.append (aValue);
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: getPseudoEvents
	// ----------------------------------------------------------------------
	
  /** Returns the pseudo event string.
    *
    * @return The pseudo event string.
    * 
    * @invisible
    * 
    */
    
  public String getPseudoEvents ()
  {
  	return (pseudoEvents == null ? null : pseudoEvents.toString ());
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: resetPseudoEvents
	// ----------------------------------------------------------------------
	
  /** Resets the pseudo event string.
    * 
    * @invisible
    * 
    */
    
  public void resetPseudoEvents ()
  {
  	pseudoEvents = null;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setForceApplicationStart
	// ---------------------------------------------------------------------------
	
	/** Forces an application to restart.
		*
		* @param  aForceApplicationStart  a boolean indicating if the application
		*	should restart or not.
		* 
		*/
		
  public void setForceApplicationStart (boolean aForceApplicationStart)
  {
  	forceApplicationStart = aForceApplicationStart;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: getForceApplicationStart
	// ---------------------------------------------------------------------------
	
	/** Returns the boolean indicating whether an application should be restarted or not.
		*
		* @return Boolean indicating restart or not.
		* 
		*/
		
  public boolean getForceApplicationStart ()
  {
  	return forceApplicationStart;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: getChainApplicationName
	// ---------------------------------------------------------------------------

	/** Returns the name of the application to chain to.  If this is null, or is the same
		* as the name of the current application, then no chaining will occur.
		*
		* @return The chain application name.
		* 
		*/
  public String getChainApplicationName ()
  {
  	return chainApplication;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setProperties
	// ---------------------------------------------------------------------------
	
	/** Sets the properties for the application.  This is done when the application is
		* initialized.
		*
		* @param aProperties  the Properties built up by the <code>ApplicationManager</code>
		* 
		* @invisible
		* 
		*/
		
  public void setProperties (Properties aProperties)
  {
  	properties = aProperties;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setAmble
	// ---------------------------------------------------------------------------
	
	/** Sets the amble for this application using the location from this application's
	  * properties file.
		*
	 	* @invisible	 
		*/
		
  private String [] setAmble (String aAmble)
  {
  	String [] retVal = null;
  	
  	if (aAmble != null)
  	{
  		String theLocation = getProperty (MAUI_USER_DIR);
  		retVal = MDesktop.setAmble (aAmble, null, theLocation);
  	}
  	return retVal;
  }
  	
  
	// ---------------------------------------------------------------------------
	// METHOD: setPreamble
	// ---------------------------------------------------------------------------
	
	/** Sets the preamble for the application.
		*
		* @param aPreambleFile   the reference to the file or resource which contains 
		* 												the preamble.
		* 
		* @invisible
		* 
		*/
		
  public void setPreamble (String aPreambleFile)
  {
  	preamble = setAmble (aPreambleFile);
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setPostamble
	// ---------------------------------------------------------------------------
	
	/** Sets the postamble for the application.
		*
		* @param aPostambleFile  the reference to the file or resource which contains 
		* 												the preamble.
		* 
		* @invisible
		* 
		*/
		
  public void setPostamble (String aPostambleFile)
  {
		postamble = setAmble (aPostambleFile);
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setPath
	// ---------------------------------------------------------------------------
	
	/** Sets the application path correctly.
		* @invisible
		* 
		*/
		
  public void setPath (String aApplicationPath)
  {
 		applicationPath = (aApplicationPath.startsWith ("/") ?
 										aApplicationPath :
 										"/" + aApplicationPath);
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: generateCookies
	// ---------------------------------------------------------------------------
	
  /** Generates all cookies for this application (if there are any).
    *
    * @param aDomain the default domain to use.
    *
    * @return The Cookie string.
    * 
    * @invisible
    * 
    */
    
  public synchronized String generateCookies (String aDomain)
  {
  	StringBuffer retVal = null;
  	Enumeration theCookies = cookies.elements ();
  	while (theCookies.hasMoreElements ())
  	{
  		if (retVal == null)
  		{
  			retVal = new StringBuffer ();
  		}
  		retVal.append (((MauiCookie) theCookies.nextElement ()).generateCookie (aDomain));
  		retVal.append ("\r\n");
  	}
  	cookies.clear ();
  	return (retVal == null ? "" : retVal.toString ());
  }
  
	// ---------------------------------------------------------------------------
	// METHOD: getAndClearCookies
	// ---------------------------------------------------------------------------
	
	/** Returns all cookies as an array, and clear the vector.
	  *
	  * @param  aClear  a boolean indicating if the vector should be cleared or 
	  *                 not.
	  *
	  * @return         The cookie array.
	  * 
	  * @invisible
	  * 
	  */
	public MauiCookie [] getAndClearCookies (boolean aClear)
	{
		MauiCookie [] retVal = null;
  		Enumeration theCookies = cookies.elements ();
		int i = 0;
  		while (theCookies.hasMoreElements ())
  		{
  			if (retVal == null)
  			{
  				retVal = new MauiCookie [cookies.size ()];
  			}
			retVal [i++] = (MauiCookie) theCookies.nextElement ();
  		}
  	
  		if (aClear)
  		{
  			cookies.clear ();
  		}
		return (retVal == null ? new MauiCookie [0] : retVal);
	}		
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setCookie
	// ---------------------------------------------------------------------------
	
  /** Sets a cookie value.
    * 
    * @param  aKey      the key value for the cookie.
    * 
    * @param  aValue    the value for the cookie.
    * 
    * @param  aExpires  the expiry date in the form <code>Wdy, DD-Mon-YYYY HH:MM:SS GMT</code>. 
    *										 A null value means that the cookie will expire at the end of the 
    										 session.
    * 
    * @param  aPath     the path within the domain where the cookie may be read.
    * 
    * @param  aDomain   the domain where the cookie may be read.
    * 
    * @invisible
    * 
    */
    
  public void setCookie (String aKey,
						 String aValue,
						 String aExpires,
						 String aPath,
						 String aDomain)
  {
  	MauiCookie theCookie = new MauiCookie (aKey,
										   aValue,
										   aExpires,
										   (aPath == null ? applicationPath : aPath),
										   aDomain);
  	cookies.put (aKey, theCookie);
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setCookie
	// ---------------------------------------------------------------------------
	
  /** Sets a cookie value.
    *
    * @param  aKey      the key value for the cookie.
    * 
    * @param  aValue    the value for the cookie.
    * 
    * @param  aExpires  the expiry date in the form <code>Wdy, DD-Mon-YYYY HH:MM:SS GMT</code>. 
    *										 A null value means that the cookie will expire at the end of 
    *										 the session.
    * 
    * @invisible
    * 
    */
    
  public void setCookie (String akey,
  											 String aValue,
  											 String aExpires)
  {
  	setCookie (akey, aValue, aExpires, null, null);
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setCookie
	// ---------------------------------------------------------------------------
	
  /** Sets a cookie value (default expiry "Session").
    *
    * @param aKey   the key value for the cookie
    * 
    * @param aValue  the value for the cookie
    * 
    * @invisible
    * 
    */
    
  public void setCookie (String akey,
  											 String aValue)
  {
  	setCookie (akey, aValue, null, null, null);
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: setInboundCookies
	// ---------------------------------------------------------------------------
	
  /** Fills in the inbound cookie values
    * 
    * @param aInboundCookies  the Hashtable of inbound cookie values.
    * 
    * @invisible
    * 
    */
    
  public void setInboundCookies (Hashtable aInboundCookies)
  {
  	inboundCookies = aInboundCookies;
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: getCookie
	// ---------------------------------------------------------------------------
	
  /** Returns a cookie.
    *
    * @param aKey  the key of access for the cookie.
    *
    * @return The cookie value, or null if not found.
    * 
    * @invisible
    * 
    */
    
  public String getCookie (String aKey)
  {
  	return (String) inboundCookies.get (aKey);
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: getProperty
	// ---------------------------------------------------------------------------
	
	/** Returns a property.
		*
		* @param aKey  the key of access into the property hashtable.
		*
		* @return The property value, or null if there is no corresponding value.
		* 
		*/
		
  public String getProperty (String aKey)
  {
  	return (properties == null ? null : properties.getProperty (aKey));
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: getProperties
	// ---------------------------------------------------------------------------
	
  /** Returns a copy of the properties list.
    *
    * @return A copy of the properties list.
    *
    */
    
  public Properties getProperties ()
  {
  	return (Properties) properties.clone ();
  }
  
  
	// ---------------------------------------------------------------------------
	// METHOD: propertyNames
	// ---------------------------------------------------------------------------
	
	/** Returns an Enumeration of the property names.
		*
		*	@return The Enumeration of property names.
		* 
		*/ 
		
  public Enumeration propertyNames ()
  {
  	return properties.propertyNames ();
  }

	
	// ---------------------------------------------------------------------------
	// METHOD: setDefaultApplication
	// ---------------------------------------------------------------------------
	
	/** Sets the boolean indicating whether this is the default application or not.
	  *
	  * @param aIsDefault  boolean indicating whether this is the default application or not.
	  * 
	  * @deprecated  Default application settings should be stored at the runtime 
	  *              engine-level, not application-level. There can be only one
	  *              default app. A fix for this should be developed. 
	  *              (ian@bitmovers.com)
	  * 
	  * @invisible
	  * 
	  */
	  
	public void setDefaultApplication (boolean aDefaultApplication)
	{
		defaultApplication = aDefaultApplication;
	}
	
  
	// ---------------------------------------------------------------------------
	// METHOD: isDefaultApplication
	// ---------------------------------------------------------------------------
	
	/** Returns boolean indicating if this is the default application or not.
	  *
	  * @return Boolean indicating if this is the default or not.
	  *
	  * @deprecated  Default application settings should be stored at the runtime 
	  *              engine-level, not application-level. There can be only one
	  *              default app. A fix for this should be developed. 
	  *              (ian@bitmovers.com)
	  *
	  * @invisible
	  * 
	  */
	  
	public boolean isDefaultApplication ()
	{
		return defaultApplication;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getProcessID
	// ---------------------------------------------------------------------------
	
	/** Returns the processID for this application instance. This is a unique number
		* which is determined when the application is created. It is derived from the
		* the Object hashcode 
		*
		* @return The process id.
		* 
		* @invisible
		* 
		*/
		
	public final String getProcessID()
	{
	  return mPID;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getName
	// ----------------------------------------------------------------------
	
	/** Returns the name of this <code>MauiApplication</code>.  The actual application can 
	  * override this method to provide another name. The default name is 
	  * <code>[application suite].[class name]</code>.
	  *
	  * @return The <code>MauiApplication</code> name.
	  * 
	  */
	  
	public String getName ()
	{
		return (applicationSuiteName == null ? "" : applicationSuiteName + ".") + getClass ().getName ();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** Fills all parser values. This is a callback from the renderer layer.
		* Since the component is the best place to fill in information about itself
		* this method is used as a callback (defined in <code>I_Renderable</code>).  Note that the
		* parser values filled in should not contain any embedded tags (e.g. no HTML, or WML).
		*
		* @param aRenderable  the reference to the renderable object. This will typically be
		*											this component.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void fillParserValues()
	{
		boolean theWMLFlag = compositor instanceof WMLCompositor;
		desktop.fillParserValues(parser, theWMLFlag);
		super.fillParserValues();
		
		if (am.isAuthorized (am.AUTHORIZATION_CLIENTBRANDING))
		{
			if (preamble != null)
			{
				parser.setVariable ("applicationPreamble", preamble [(theWMLFlag ? 0 : 1)]);
			}
			if (postamble != null)
			{
				parser.setVariable ("applicationPostamble", postamble [(theWMLFlag ? 0 : 1)]);
			}
		}
		parser.setVariable("title", title);
		StringBuffer theName = new StringBuffer (getServletURL ());
		theName.append (getRuntimeName ());
		String theNameString = theName.toString ();
		int theOffset = theNameString.indexOf ("//");
		if (theOffset != -1)
		{
			theNameString = theNameString.substring (0, theOffset) + theNameString.substring (theOffset + 1);
		}
		parser.setVariable("applicationName", theNameString);
		//parser.setVariable("applicationName", getRuntimeName ());
		
		int refreshSeconds = Integer.parseInt(ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_SESSION_TIMEOUT)) * 60;
		
		// If session keepalive is on
		if (Boolean.valueOf(ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_SESSION_KEEPALIVE)).booleanValue())
		{
			// Set refresh time to 30 seconds less than session timeout time.
			// (i.e. the refresh will keep the session alive)
			refreshSeconds -= 30;
		}
		else
		{
			// Set refresh time to 30 seconds more than session timeout time.
			// (i.e. the refresh will cause a session timeout screen to appear)
			refreshSeconds += 30;
		}
		
		// 10 seconds is the minimum refresh time.
		if (refreshSeconds < 10)
		{
			refreshSeconds = 10;
		}
		
		parser.setVariable("refreshSeconds", Integer.toString(refreshSeconds));
		parser.setVariable("refreshMilliseconds", Integer.toString(refreshSeconds * 1000));
		
		//
		//	DL:
		//	Since WML doesn't allow nested tables, and since time is running short...
		//	it's time for a kludge...
		//	If the compositor is WML and the contained component is a container,
		//	then skip past the layout manager for the MauiApplication, and start
		//	the layout with the container (since it will have a layout manager).
		//
		//	A better solution for wml would be for each layout manager to determine if
		//	there is a current layout manager, and close it before starting.
		//	And when completed, "reopen" the previously closed layout manager.
		//
		//	But, alas, not today.
		//
		
		if (compositor instanceof WMLCompositor &&
			getComponent (0) instanceof MContainer)
		{
			parser.setVariable ("layoutManager", getComponent (0).render ());
		}
		else
		{
			MLayout theLayout = getLayout();
			parser.setVariable("layoutManager", theLayout.render ());
		}
		
		sequenceNumber++;
		parser.setVariable ("sequenceNumber", Integer.toString (sequenceNumber));
		
		//
		// Create prepopulated list of all MSettable components.
		//
		Vector msettableVector = new Vector();
		Vector msettableComponentIDVector = new Vector();
		//++ 405 JL 2001.09.21
		getContainedSettableComponents(msettableVector, this);
		//--
		for (int i = 0; i < msettableVector.size(); i++)
		{
			msettableComponentIDVector.addElement(((MComponent)msettableVector.elementAt(i)).getComponentID());
		}
		String prepopulatedEventQueue = StringUtilities.join(",", msettableComponentIDVector);
		parser.setVariable ("prepopulatedEventQueue", prepopulatedEventQueue);
	}
	
	
// ----------------------------------------------------------------------
// METHOD: isDuplicateEvent
// ----------------------------------------------------------------------
	
/**
	* Checks whether this a duplicate event or not.
	*
	* @param aSequenceNumber  the sequence number for the event.
	*
	* @return Boolean indicating if this is a duplicate event or not.
	*/
	public boolean isDuplicateEvent (int aSequenceNumber)
	{
		return (aSequenceNumber < sequenceNumber);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getTitle
	// ----------------------------------------------------------------------
	
	/** Returns the application title.
	  * 
	  * @return The application title.
	  * 
	  */
	  
	public String getTitle()
	{
		return title;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: actionPerformed
	// ----------------------------------------------------------------------
	
	/** <code>MauiApplication</code> is an action listener for some events. This method is used to
		* republish events to any registered event listeners within <code>MauiApplication</code>.
		*
		* @param aEvent  the event being fired.
		*
		* @invisible
		* 
		*/
  
	public void actionPerformed (MActionEvent aEvent)
	{
		dispatchActionEvent (aEvent);
	}
  
	
	// ----------------------------------------------------------------------
	// METHOD: removeChainApplicationSource
	// ----------------------------------------------------------------------
	
  /** Removes a component (typically an <code>MButton</code>) as an object which can be used
  	* as a trigger for application chaining.
  	*
  	* @param aEventSource  the <code>MComponent</code> which triggers an application chain.
  	* 
  	*/
  
	public void removeChainApplicationSource (MComponent aEventSource)
	{
		ChainListener theListener = (ChainListener) chainListeners.get (aEventSource);
		if (theListener != null)
		{
			aEventSource.removeActionListener (theListener);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: addChainApplicationSource
	// ----------------------------------------------------------------------
	
	/** Adds a component (typically an <code>MButton</code>) as an object which can be used as a trigger
		* for application chaining.
		*
		* @param aEventSource 				  the <code>MComponent</code> to use as a trigger. Any event from this 
		*																 component will be treated as a trigger.
		* 
		* @param aChainApplicationName  the name of the application to chain to.
		* 
		*/
		
	public void addChainApplicationSource (MComponent aEventSource, String aChainApplicationName)
	{
		removeChainApplicationSource (aEventSource);
		
		ChainListener theListener = new ChainListener (aChainApplicationName);
		chainListeners.put (aEventSource, theListener);
		aEventSource.addActionListener (theListener);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getGlobalProperty
	// ----------------------------------------------------------------------
	
	/** Returns a global property.
		*
	  * @param aKey  the key of access.
	  *
	  * @return The associated value, or null if nothing found.
	  * 
	  */
	  
	public Object getGlobalProperty (Object aKey)
	{
		return scm.getGlobalProperty (aKey);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: putGlobalProperty
	// ----------------------------------------------------------------------
	
	/** Stores a global property.
	  *
	  * @param aKey   the key of access.
	  * 
	  * @param aValue  the associated value.
	  * 
	  */
	  
	public void putGlobalProperty (Object aKey, Object aValue)
	{
		scm.putGlobalProperty (aKey, aValue);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSessionProperty
	// ----------------------------------------------------------------------
	
	/** Returns a session property.
	  *
	  * @param aKey  the key of access.
	  *
	  * @return The associated value, or null if nothing found.
	  * 
	  */
	  
	  public Object getSessionProperty (Object aKey)
	  {
	  	return session.getShared (aKey);
	  }
	
	
	// ----------------------------------------------------------------------
	// METHOD: putSessionProperty
	// ----------------------------------------------------------------------
	
	/** Stores a session property.
	  *
	  * @param aKey    the key of access.
	  * @param aValue  the associated value.
	  *
	  */
	  public void putSessionProperty (Object aKey, Object aValue)
	  {
	  	session.putShared (aKey, aValue);
	  }
	  
	
	// ----------------------------------------------------------------------
	// METHOD: exit
	// ----------------------------------------------------------------------
	
	/** Marks this application for exit.
	  * 
	  */
	  
	  public void exit ()
	  {
	  	exit = true;
	  }
	  
	
	// ----------------------------------------------------------------------
	// METHOD: exitAll
	// ----------------------------------------------------------------------
	
	/** Marks all applications in this session for exit.
	  *
	  */
	  
	// Should this method really be an instance method? Should it be static? Or 
	// should it exist in ApplicationManager?
	
	  public void exitAll ()
	  {
	  	exitAll = true;
	  }
	  
	
	// ----------------------------------------------------------------------
	// METHOD: isExit
	// ----------------------------------------------------------------------
	
	/** Returns the exit boolean.
	  *
	  * @return The exit boolean.
	  *
	  * @invisible
	  * 
	  */
	  
	  public boolean isExit ()
	  {
	  	return exit;
	  }
	  
	
	// ----------------------------------------------------------------------
	// METHOD: isExitAll
	// ----------------------------------------------------------------------
	
	/** Returns the exitAll boolean.
	  *
	  * @return The exitAll boolean.
	  *
	  * @invisible
	  * 
	  */
	  
	  public boolean isExitAll ()
	  {
	  	return exitAll;
	  }

	
	
	  
	// ----------------------------------------------------------------------
	// METHOD: getServletURL
	// ----------------------------------------------------------------------
	
	/** Returns the servlet URL
	  *
	  * @return The Servlet URL string.
	  */
	  public String getServletURL ()
	  {
	  	return (session == null ? "/" : session.getServletURL ());
	  }
	  
	// ----------------------------------------------------------------------
	// METHOD: getServletURL
	// ----------------------------------------------------------------------
	
	/** Returns the servlet and application name
	  *
	  * @return The Servlet URL string.
	  */
	  public String getServletAndApplication ()
	  {
	  	StringBuffer retVal = new StringBuffer (getServletURL ());
	  	retVal.append (getRuntimeName ());
	  	return retVal.toString ();
	  }
	  
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: ChainListener
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
  /** This is a listener which is "wired" to any MComponent.  If it receives an
    * event, it will set the chain application name to the one speceified in it's
    * constructor.
    *
    * @invisible
    * 
    */
    
	class ChainListener implements MActionListener
	{
		/** The name of the application to chain to if an event is received.
			* @invisible
			*/
		private final String localChainApplication;
		
		/** Simple constructor
			* @param aChainApplication  the name of the application to chain to when an event
			* 													 is received.
			*/
		protected ChainListener (String aChainApplication)
		{
			localChainApplication = aChainApplication;
		}
		
		/** Event notification method.  Invoking this method will cause the chain application
			*	in MauiApplication to be set to whatever was specified in the ChainListener
			* constructor.
			*
			* @param aActionEvent  the MActionEvent being published.
			*/
		public void actionPerformed (MActionEvent aActionEvent)
		{
			chainApplication = localChainApplication;
		}
	}
	// ---------------------------------------------------------------------------
}

// =============================================================================
// Copyright (c) 1999-2000 Bitmovers Communications Inc.                     eof