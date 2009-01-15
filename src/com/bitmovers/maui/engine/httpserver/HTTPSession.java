// =============================================================================
// com.bitmovers.maui.httpserver.HTTPSession
// =============================================================================

package com.bitmovers.maui.engine.httpserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.Dimension;
import com.bitmovers.maui.*;
import com.bitmovers.maui.profiler.Profiler;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.InfoString;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.engine.render.I_RendererListener;
import com.bitmovers.maui.engine.render.RendererEvent;
import com.bitmovers.maui.components.MComponent;


// =============================================================================
// CLASS: HTTPSession 
// =============================================================================

/**
* HTTPSession <p>
* This object represents a "session" with a single client.  It helps maintain client state.
* It also holds references to all of the MauiApplications running within a single client session.
* It is also a "switch" for forwarding I_Renderer creation events
*
* @invisible
*
*/
public class HTTPSession
	implements I_RendererListener
{
	// ---------------------------------------------------------------------------
	private static boolean rebuildSessionListeners = true;
	private static boolean rebuildApplicationListeners = true;
	private static I_SessionListener [] sessionListenerArray = null;
	private static Vector sessionListeners = new Vector ();
	private static Hashtable sessionHashtable = new Hashtable();
	private static final byte [] SESSION_COOKIE = "Set-Cookie: Maui.HTTPSession=".getBytes ();
	private static final byte [] SESSION_OLDCOOKIE = "Set-Cookie: Maui.HTTPOldSession=".getBytes ();
	private static final byte [] SESSION_PATH = "; Path=/\r\n".getBytes ();
	private static final byte [] SESSION_EXIT = "Set-Cookie: Maui.HTTPSession=exit; Path=/\r\n".getBytes ();
	
	private static I_ApplicationListener []  applicationListenerArray = new I_ApplicationListener [0];
	private static Vector applicationListeners = new Vector ();
	
	private Hashtable shared = new Hashtable (10);
	private Hashtable applications = new Hashtable (5);
	private Hashtable rendererListeners = new Hashtable (5);
	private Hashtable cookies = new Hashtable (5);
	private static Hashtable crossReferences = new Hashtable ();
	
	private static long kDefaultSessionTimeout = 300000;
	private static int sessionMaximum;
	private Thread thread;
	private String sessionID;
	private long lastAccessTime;
	private MauiApplication application;
	private Dimension dimension = null;
	private boolean hasCookie = false;
	private int uniqueReference = -1;
	private String clientName;
	private boolean exit = false;
	private static Object synchBlock = new Object ();
	private static int number = 0;
	private String servletURL = "/";
	private boolean servletBased = false;
	private boolean keepAlive = true;
	private String crossReference = null;
		
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: HTTPSession
	// ---------------------------------------------------------------------------
	/**
	* This is a private constructor because the HTTPSession cannot be explicitly created.  Instead
	* it is created through the static method retrieveSession, and only if a matching HTTPSession can't
	* be found.
	*
	* @invisible
	*/
	private HTTPSession ()
	{
		this (null, false, null);
	}
	
	private HTTPSession (String aClientName,
						 boolean aServletBased,
						 String aServletURL)
	{
		clientName = aClientName;
		servletBased = aServletBased;
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		servletURL = (servletBased ? aServletURL : "/");
		// Set up the session ID...
		this.sessionID = Integer.toHexString(this.hashCode());
	
		// Start the thread...
		this.thread = new Thread(new Runnable() { public void run() { runThread(); } }, "HTTPSessionThread");
		this.thread.setPriority(Thread.NORM_PRIORITY);
		this.thread.start();
	}
	
	/**
	* Get an array of I_SessionListeners
	*
	* @return The array of session listeners
	*/
	private static I_SessionListener [] getSessionListeners ()
	{
		if (rebuildSessionListeners)
		{
			Object [] theListeners = sessionListeners.toArray ();
			sessionListenerArray = new I_SessionListener [theListeners.length];
			for (int i = 0; i < theListeners.length; i++)
			{
				sessionListenerArray [i] = (I_SessionListener) theListeners [i];
			}
			rebuildSessionListeners = false;
		}
		return sessionListenerArray;
	}
	
	/**
	* Add a session listener
	*
	* @param aSessionListener The I_SessionListener to add
	*/
	public static void addSessionListener (I_SessionListener aSessionListener)
	{
		if (!sessionListeners.contains (aSessionListener))
		{
			sessionListeners.addElement (aSessionListener);
			rebuildSessionListeners = true;
		}
	}
	
	
	private static I_ApplicationListener [] getApplicationListeners ()
	{
		if (rebuildApplicationListeners)
		{
			Object [] theListeners = applicationListeners.toArray ();
			applicationListenerArray = new I_ApplicationListener [theListeners.length];
			for (int i = 0; i < theListeners.length; i++)
			{
				applicationListenerArray [i] = (I_ApplicationListener) theListeners [i];
			}
			rebuildApplicationListeners = false;
		}
		return applicationListenerArray;
	}
	
	/**
	* Remove an application listener
	*
	* @param aApplicationListener The I_ApplicationListener to remove
	*/
	public static void removeApplicationListener (I_ApplicationListener aApplicationListener)
	{
		if (applicationListeners.contains (aApplicationListener))
		{
			applicationListeners.remove (aApplicationListener);
			rebuildApplicationListeners = true;
		}
	}
	
	/**
	* Add a session listener
	*
	* @param aSessionListener The I_SessionListener to add
	*/
	public static void addApplicationListener (I_ApplicationListener aApplicationListener)
	{
		if (!applicationListeners.contains (aApplicationListener))
		{
			applicationListeners.addElement (aApplicationListener);
			rebuildApplicationListeners = true;
		}
	}
	
	
	/**
	* Remove a session listener
	*
	* @param aSessionListener The I_SessionListener to remove
	*/
	public static void removeSessionListener (I_SessionListener aSessionListener)
	{
		if (sessionListeners.contains (aSessionListener))

		{
			sessionListeners.remove (aSessionListener);
			rebuildSessionListeners = true;
		}
	}
	
	/**
	* Notify any listeners that a session has been added or removed, or that an
	* application has been created.
	*
	* @param aSession Target session
	* @param aMauiApplication The associated MauiApplication (or null, if not relevant)
	* @param aCreated Boolean indicating if the session was added or removed
	*/
	protected static void notifySessionListeners (HTTPSession aSession,
												  MauiApplication aMauiApplication,
												  boolean aCreated)
	{
		I_SessionListener [] theSessionListeners = getSessionListeners ();
		if (theSessionListeners.length > 0)
		{
			SessionEvent theEvent = new SessionEvent (aSession,
													  aMauiApplication);
			for (int i = 0; i < theSessionListeners.length; i++)
			{
				if (aMauiApplication != null)
				{
					if (aCreated)
					{
						theSessionListeners [i].applicationAdded (theEvent);
					}
					else
					{
						theSessionListeners [i].applicationRemoved (theEvent);
					}
				}
				else if (aCreated)
				{
					theSessionListeners [i].sessionCreated (theEvent);
				}
				else
				{
					theSessionListeners [i].sessionDeleted (theEvent);
				}
			}
		}
	}
	
	/**
	* add a session to the static session table
	*
	* @param aSession The HTTPSession to add
	*/
	protected static void addSession (HTTPSession aSession)
	{
		String theSessionID = aSession.getSessionID ();
		if (!sessionHashtable.containsKey (theSessionID))
		{
			sessionHashtable.put (theSessionID, aSession);
			notifySessionListeners (aSession, null, true);
		}
	}
	
	/**
	* Remove a session from the static session table
	*
	* @param aSession The session to remove
	*/
	public static void removeSession (HTTPSession aSession)
	{
		String theSessionID = aSession.getSessionID ();
		if (sessionHashtable.containsKey (theSessionID))
		{
			HTTPSession theSession = getSession (theSessionID);
			sessionHashtable.remove (theSessionID);
			if (theSession.getCachedMauiApplications ().length > 0)
			{
				theSession.removeApplication (null);
			}
			theSession.removeCrossReference ();
			theSession.thread.interrupt ();
			notifySessionListeners (aSession, null, false);
		}
	}
	
	/**
	* Get a session from the session table
	*
	* @param aSessionID The session id for retrieval
	*
	* @return The HTTPSession, or null if not found
	*/
	public static HTTPSession getSession (String aSessionID)
	{
		return (HTTPSession) sessionHashtable.get (aSessionID);
	}
	
	/**
	* Get all of the sessions
	*
	* @return An array containing all of the HTTPSession objects
	*/
	public static HTTPSession [] getAllSessions ()
	{
		Object [] theSessions = sessionHashtable.values ().toArray ();
		HTTPSession [] retVal = new HTTPSession [theSessions.length];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = (HTTPSession) theSessions [i];
		}
		return retVal;
	}
	
	public static int getSessionCount ()
	{
		return sessionHashtable.size ();
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: initialise
	// ---------------------------------------------------------------------------
	/**
	* Perform once-only initialisation.  This gets configuration information like the session timeout.
	*
	* @invisible
	*/
	public static void initialise()
	{
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance();
		String timeoutMinutes = theSCM.getProperty(theSCM.MAUI_SESSION_TIMEOUT);
		
		try
		{
			long minutes = Long.parseLong(timeoutMinutes);
			
			HTTPSession.kDefaultSessionTimeout = minutes * 60 * 1000;
		}
		catch (NumberFormatException e)
		{
			System.err.println("[HTTPSession] Bad value for session timeout : " + timeoutMinutes + " Defaulting to 5 minutes.");
		}
		
		try
		{
			sessionMaximum = Integer.parseInt (theSCM.getProperty (theSCM.MAUI_SESSION_MAXIMUM));
		}
		catch (NumberFormatException e)
		{
			sessionMaximum = Integer.parseInt (theSCM.MAUI_SESSION_MAXIMUM_VALUE);
		}
	}


	// ---------------------------------------------------------------------------
	// METHOD: getID
	// ---------------------------------------------------------------------------
	/**
	*  Get the session id for this session object.  This is just a hashcode for the object
	*
	* @return A string representation of the HTTPSession object hashcode
	*/
	public String getID()
	{
	  return this.sessionID;
	}
	
	/**
	* Get the unique reference for the latest request.
	*
	* @return The unique reference
	*/
	public int getUniqueReference ()
	{
		return uniqueReference;
	}
	
	/**
	* Set the unique reference for the latest request
	*
	* @int aUniqueReference The unique reference
	*/
	public void setUniqueReference (int aUniqueReference)
	{
		uniqueReference = aUniqueReference;
	}
	
	/**
	* Get the synchronization block for this session
	*
	* @return The synchronization block for this session
	*/
	public Object getSynchBlock ()
	{
		return synchBlock;
	}
	
	/**
	*  Get the session id for this session object.  This is just a hashcode for the object.
	*
	* @return A string representation of the HTTPSession object hashcode
	*/
	public String getSessionID ()
	{
		return sessionID;
	}
	
	/**
	* Get the client name
	*
	* @return The client name
	*/
	public String getClientName ()
	{
		return clientName;
	}
	
	/**
	  * Is this a servlet based session
	  */
	public boolean isServletBased ()
	{
		return servletBased;
	}
	
	/** Get the servlet URL
	  */
	public String getServletURL ()
	{
		return servletURL;
	}
	
	/** Set the servlet URL
	  * @invisible
	  */
	public void setServletURL (String aServletURL)
	{
		servletURL = aServletURL;
	}
	
	/**
	* Set the current application.
	*
	* @param aApplication The current maui application
	*/
	protected void setApplication (MauiApplication aApplication)
	{
		if (application != aApplication)
		{
			application = aApplication;
			String theApplicationAddress = aApplication.getApplicationAddress ();
			if (!applications.contains (theApplicationAddress))
			{
				applications.put (theApplicationAddress, aApplication);	// Cache it
				notifySessionListeners (this, aApplication, true);
			}
		}
	}
	
	private static void notifyApplicationListeners (ApplicationEvent aApplicationEvent,
											 boolean aActivated)
	{
		I_ApplicationListener [] theListeners = getApplicationListeners ();
		for (int i = 0; i < theListeners.length; i++)
		{
			if (aActivated)
			{
				theListeners [i].applicationActivated (aApplicationEvent);
			}
			else
			{
				theListeners [i].applicationDeactivated (aApplicationEvent);
			}
		}
	}
	
	
	/**
	* Activate an application
	*
	* @param aApplication The application being activated
	*/
	public static void activateApplication (MauiApplication aApplication)
	{
		I_ApplicationListener [] theListeners = getApplicationListeners ();
		if (theListeners.length > 0)
		{
			notifyApplicationListeners (new ApplicationEvent (aApplication), true);
		}
	}
	
	/**
	* Deactivate an application
	*
	* @param aApplication The application being activated
	*/
	public static void deactivateApplication (MauiApplication aApplication)
	{
		I_ApplicationListener [] theListeners = getApplicationListeners ();
		if (theListeners.length > 0)
		{
			notifyApplicationListeners (new ApplicationEvent (aApplication), false);
		}
	}
	
	
	/**
	* Get the current application
	*
	* @return The current application
	*/
	public MauiApplication getApplication ()
	{
		return application;
	}
	
	/**
	* Get the cached application
	*
	* @param aClassName The name of the class to look for
	*
	* @return The cached MauiApplication, or null if not found.
	*/
	public MauiApplication getCachedMauiApplication (String aClassName)
	{
		return (MauiApplication) applications.get (
				ApplicationManager.getInstance ().getMauiApplicationAddress (aClassName));
	}
	
	/**
	* Delete an application from this session
	*
	* @param aApplication The application to remove.  Null means remove all applications.
	*/
	public void removeApplication (MauiApplication aApplication)
	{
		if (aApplication == null)
		{
			MauiApplication [] theApplications = getCachedMauiApplications ();
			for (int i = 0; i < theApplications.length; i++)
			{
				removeApplication (theApplications [i]);
			}
		}
		else
		{
			ProcessManager thePM = ProcessManager.getInstance ();
			ApplicationManager theAM = ApplicationManager.getInstance ();
			String theApplicationAddress = aApplication.getApplicationAddress ();
			if (applications.containsKey (theApplicationAddress))
			{
				applications.remove (theApplicationAddress);
				notifySessionListeners (this, aApplication, false);
				aApplication.finish ();
				aApplication.exiting ();
				if (applications.isEmpty ())
				{
					removeSession (this);
					exit = true;
				}
			}
		}
	}
	
	/**
	* Get all of the cached applications
	*
	* @return An array of all of the cached applications
	*/
	public MauiApplication [] getCachedMauiApplications ()
	{
		MauiApplication [] retVal = new MauiApplication [applications.size ()];
		Enumeration theApplications = applications.elements ();
		int i = 0;
		while (theApplications.hasMoreElements ())
		{
			retVal [i++] = (MauiApplication) theApplications.nextElement ();
		}
		return retVal;
	}
	private byte [] localGetBytes (String aString)
	{
		byte [] retVal = new byte [aString.length ()];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = (byte) aString.charAt (i);	
		}
		return retVal;
	}

	// ---------------------------------------------------------------------------
	// METHOD: writeCookieHeader
	// ---------------------------------------------------------------------------
	
	/**
	* Write out the cookie header for this session to the output stream
	*
	* @param responseStream The OutputStream to send the output
	* @param aHost The client host name
	*
	* @throws IOException This exception may be thrown if the socket is no longer valid (eg. if
	* the client unexpectedly disconnects)
	*
	* @invisible
	*/
	protected void writeCookieHeader(OutputStream responseStream, String aHost) throws IOException
	{
    	if (!exit)
    	{
			responseStream.write (SESSION_OLDCOOKIE);
			responseStream.write (localGetBytes (sessionID));
			responseStream.write (SESSION_PATH);
    		responseStream.write (SESSION_COOKIE);
    		responseStream.write (localGetBytes (sessionID));
    		responseStream.write (SESSION_PATH);
    	}
    	else
    	{
    		responseStream.write (SESSION_EXIT);
    	}
    	
    	if (application != null)
    	{
    		responseStream.write (localGetBytes (application.generateCookies (aHost)));
    	}
	}
	
	/** Get the Session cookie (without writing it out)
	  *
	  * @return The Session cookie
	  */
	public MauiCookie getSessionCookie ()
	{
		return new MauiCookie ("Maui.HTTPSession",
							   sessionID,
							   null,
							   "/",
							   null);
	}


	// ---------------------------------------------------------------------------
	// METHOD: touchTimeoutCounter
	// ---------------------------------------------------------------------------
	
	/**
	* This is used in conjunction with the session timeout.  Whenever an HTTP request appears which is
	* with respect to this session, then the session's last access time will be updated to the current
	* system time.
	*
	* @invisible
	*/
	protected void touchTimeoutCounter()
	{
	  this.lastAccessTime = System.currentTimeMillis();
	  thread.interrupt ();
	}


	// ---------------------------------------------------------------------------
	// METHOD: runThread
	// ---------------------------------------------------------------------------
	
	/**
	* This is the timeout thread.  It uses the session timeout property to determine if a session
	* has timed out.  If it has timed out, then it is removed from the session table.
	*
	* @invisible
	*/
	private void runThread()
	{
	  boolean looping = true;
	  System.out.println ("[HTTPSession] thread created");
	  synchronized (synchBlock)
	  {
	  	Thread.currentThread ().setName ("HTTPSession - Timeout Scan " + number++);
	  }
	  
	  while (looping && !exit)
	  {
	    try
	    {
	      this.thread.sleep (kDefaultSessionTimeout + 500);
	      
	      if (System.currentTimeMillis() > this.lastAccessTime + kDefaultSessionTimeout)
	      {
	        looping = false;
	        removeSession (this);
	        System.out.println("[HTTPSession] - Removing session '" + this.sessionID + "' due to timeout.");
	      }
	    }
	    catch (InterruptedException exception)  { }
	  }
	  this.thread.stop();
	}

	// ---------------------------------------------------------------------------
	// CLASS METHOD: retrieveSession
	// ---------------------------------------------------------------------------
	
	/** Retrieves a HTTPSession for a particular HTTPRequest.  If the session
	  * specified by the request is invalid or non-existant, a new session is
	  * created and returned.  This is a static method which access a static Hashtable
	  * of HTTPSession objects.
	  *
	  * @param request The HTTPRequest object
	  *
	  * @return An HTTPSession object (either retrieved or created)
	  *
	  * @invisible
	  */
	
	protected synchronized static HTTPSession retrieveSession (HTTPRequest request)
		throws SessionMaximumException
	{
		
	  HTTPSession session = null;
	  int theReference = Profiler.start (MauiRuntimeEngine.SOURCE_SESSION,
	  									 MauiRuntimeEngine.ACTION_GET);
	  boolean theHasCookie = false;
	  boolean theGet = true;
	  String sessionID = HTTPSession.getSessionIDFromRequest (request);
	  if (sessionID != null && sessionID.startsWith ("MA_"))
	  {
	  	sessionID = sessionID.substring (3);
	  }

	  String theSessionCookie = request.cookieMonster ();
	  if ((theHasCookie = (theSessionCookie != null)))
	  {
	  	sessionID = theSessionCookie;
	  }
	  
	  if ((session = retrieveSessionFromTable (sessionID)) == null)
	  {
	  	  if (sessionMaximum != -1 &&
	  	  	  getSessionCount () >= sessionMaximum)
	  	  {
	  	  	throw new SessionMaximumException (sessionMaximum);
	  	  }
	  	  
	      session = new HTTPSession( request.getClientName (),
	      							 request.isServletBased (),
	      							 request.getServletURL ());
	      if (sessionID != null)
	      {
	      	crossReferences.put (sessionID, session);
	      	session.setCrossReference (sessionID);
	      }
	      theGet = false;
	      addSession (session);
	      // System.out.println("[HTTPSession] - Created new HTTPSession with ID '" + session.sessionID + "'.");
	  }

 	  session.touchTimeoutCounter();
 	  session.setHasCookie (theHasCookie);
      Profiler.finish (theReference,
    				   MauiRuntimeEngine.SOURCE_SESSION,
    				   (theGet ? MauiRuntimeEngine.ACTION_GET :
    					 		 MauiRuntimeEngine.ACTION_CREATE),
    				   "Session: " + sessionID);
	  return session;
	}
	
	/** Set the cross reference
	  * 
	  * @param aCrossReference The cross reference
	  */
	private void setCrossReference (String aCrossReference)
	{
		crossReference = aCrossReference;
	}
	
	/** Get the cross referenced HTTPSession (or null, if doesn't exist)
	  *
	  * @return The HTTPSession, or nuul
	  */
	public static HTTPSession getCrossReference (String aSessionID)
	{
		return (HTTPSession) crossReferences.get (aSessionID);
	}
	
	/** Remove the cross reference
	  *
	  */
	private void removeCrossReference ()
	{
		if (crossReference != null)
		{
			crossReferences.remove (crossReference);
		}
	}

	/**
	* Try to access the HTTPSession object from the static session table
	*
	* @param aSessionID The String representation of the session object's hashcode
	*
	* @return An HTTPSession object, or null if wasn't found
	*
	* @invisible
	*/
	private static HTTPSession retrieveSessionFromTable (String aSessionID)
	{
		HTTPSession retVal = (aSessionID == null ?
								null :
								(HTTPSession) sessionHashtable.get (aSessionID));
		if (retVal == null && aSessionID != null)
		{
			retVal = getCrossReference (aSessionID);
		}
		return retVal;
	}

	/**
	* Look in the HTTPRequest for the sessionID.  For devices which don't support cookies, the
	* SessionID can be found as one of the POST key/value pairs.
	*
	* @param aRequest The HTTPRequest object
	*
	* @return The session object's hashcode, or null if it wasn't found in the request
	*
	* @invisible
	*/
	private static String getSessionIDFromRequest (HTTPRequest aRequest)
	{
		return aRequest.getQueryValue ("sessionID");
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: cookieMonster
	// ---------------------------------------------------------------------------
	
	/** cookieMonster is a helper method for 'getSession' that takes a raw
	  * HTTPRequest object and returns the passed Maui session ID.
	  *
	  * @param request The HTTPRequest
	  *
	  * @return The session object's hashcode, or null if it wasn't found
	  *
	  * @invisible
	  */
	  
	private static void cookieMonster (HTTPRequest request)
	{
	  /*if (!request.isCookiesParsed ())
	  {
	  	  retVal = request.cookieMonster ();
	  }*/
	  
	}
	
	//
	//	Renderer creation event notification methods
	//
	
	/**
	* Notify any I_RendererListeners of the creation of an I_Renderer object.
	*
	* @param aMauiApplicationName The name of the MauiApplication
	* @param aRendererEvent An EventObject describing the I_Renderer creation
	*
	* @invisible
	*/
	private void notifyListeners (String aMauiApplicationName, RendererEvent aRendererEvent)
	{
		Vector theListeners = (Vector) rendererListeners.get (aMauiApplicationName);
		
		if (theListeners != null)
		{
			Object [] theListenerArray = theListeners.toArray ();
		
			for (int i = 0; i < theListenerArray.length; i++)
			{
				((I_RendererListener) theListenerArray [i]).rendererCreated (aRendererEvent);
			}
		}
	}
	
	/**
	* The I_RendererListener event.  The HTTPSession isn't actually registered as an event listener
	* anywhere.  Instead the CompositionManager explicitly notifies it whenever a renderer is created.
	* HTTPSession is notified because the CompositionManager, which creates the renderer, is environment
	* but the renderer is specific to the HTTPSession.
	*
	* @param aRendererEvent The EventObject which describes the event
	*/
	public synchronized void rendererCreated (RendererEvent aRendererEvent)
	{
		MComponent theComponent = aRendererEvent.getComponent ();
		MauiApplication theApplication = (MauiApplication) theComponent.getRootParent ();
		notifyListeners (theApplication.getApplicationAddress (), aRendererEvent);
		notifyListeners ("null", aRendererEvent);
	}
	
	/**
	* Add a renderer listener
	*
	* @param aMauiApplication The MauiApplication to use as a filter.  If this is null
	*						  then all renderer creation events will be delivered to the listener
	* @param aRendererListener The I_RendererListener
	*/
	public synchronized void addRendererListener (MauiApplication aMauiApplication,
												  I_RendererListener aRendererListener)
	{
		String theApplicationName = (aMauiApplication == null ? "null" :
																aMauiApplication.getApplicationAddress ());
		Vector theListeners = (Vector) rendererListeners.get (theApplicationName);
		if (theListeners == null)
		{
			theListeners = new Vector (5);
			rendererListeners.put (theApplicationName, theListeners);
		}
		
		if (!theListeners.contains (aRendererListener))
		{
			theListeners.add (aRendererListener);
		}
	}
	
	/**
	* Remove a renderer listener
	*
	* @param aMauiApplication The MauiAppication that this listener is associated with.
	* @param aRendererListener The I_RendererListener
	*/
	public synchronized void removeRendererListener (MauiApplication aMauiApplication,
													 I_RendererListener aRendererListener)
	{
		String theApplicationName = (aMauiApplication == null ? "null" :
																aMauiApplication.getApplicationAddress ());
		Vector theListeners = (Vector) rendererListeners.get (theApplicationName);
		if (theListeners != null)
		{	
			theListeners.remove (aRendererListener);
			if (theListeners.size () == 0)
			{
				rendererListeners.remove (theApplicationName);
			}
		}
	}
	
	/**
	* General purpose service for any MauiApplications running within this session.
	* This provides a Hashtable which can be shared by all MauiApplications within the session
	*
	* @param aKey The key to use
	* @param aValue The value to use
	*/
	public void putShared (Object aKey, Object aValue)
	{
		shared.put (aKey, aValue);
	}
	
	/**
	* General purpose service for any MauiApplications running within this session.
	* This provides a Hashtable which can be shared by all MauiApplications within the session.
	*
	* @param akey The key of access
	*
	* @return The value, or null if it isn't found
	*/
	public Object getShared (Object aKey)
	{
		return shared.get (aKey);
	}
	
	/**
	* Set the client display area dimension (in pixels)
	*
	* @param aDimension The Dimension object
	*/
	public void setClientDimension (Dimension aDimension)
	{
		dimension = aDimension;
	}
	
	/**
	* Get the client display area dimension (in pixels)
	*
	* @return A Dimension object with the pixel width and height of the the client, or null if it's not known
	*/
	public Dimension getClientDimension ()
	{
		return dimension;
	}
	
	/**
	* Get the boolean indicating if this client has cookies or not
	*
	* @return Has or doesn't have cookies
	*/
	public boolean getHasCookie ()
	{
		return hasCookie;
	}
	
	/**
	* Set the boolean indication if the client has cookies or not
	*
	* @boolean aHasCookie Boolean indicating if cookies present or not
	*/
	protected void setHasCookie (boolean aHasCookie)
	{
		hasCookie = aHasCookie;
	}
	
	/**
	* Test if the session is exiting or not
	*
	* @return Boolean indicating exit or not
	*/
	protected boolean isExiting ()
	{
		return exit;
	}
	
	/** Set a boolean indicating if communications with this session can be pooled
	  *
	  * @param aPooled Pooling boolean
	  */
	public void setKeepAlive (boolean aKeepAlive)
	{
		keepAlive = aKeepAlive;
	}
	
	/** Get the pooling boolean
	  */
	public boolean getKeepAlive ()
	{
		return keepAlive;
	}
	
	// ---------------------------------------------------------------------------
}


// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof