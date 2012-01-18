// =============================================================================
// com.bitmovers.maui.HTTPEventTranslator
// =============================================================================

package com.bitmovers.maui.engine.httpserver;

import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.profiler.Profiler;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.engine.render.I_HasMultiple;
import com.bitmovers.maui.engine.render.I_Renderer;
//import com.bitmovers.maui.components.complex.*;
import com.bitmovers.utilities.*;


// =============================================================================
// <<SINGLETON>> CLASS: HTTPEventTranslator
// =============================================================================
/**
* HTTPEventTranslator SINGLETON <p>
* @invisible
* This is the main event translator for HTTP requests.  It is responsible for determining what to do with
* with the parsed HTTP Request.  It is a bit of a workhorse object, functioning much as a switch between the
* HTTP request and the MauiApplication environments.  it arbitrates the creation and initialization of MauiApplications,
* switching between MauiApplications, event dispatching to components within MauiApplications, and resource retrieval.
*/
public class HTTPEventTranslator
{
	// ---------------------------------------------------------------------------
	
  private static HTTPEventTranslator theInstance = new HTTPEventTranslator();
  
  private static final String [] REQUESTHEADERS  = new String [] {"ua-cpu",
			  													  "ua-os",
			  													  "user-agent",
			  													  "accept",
			  													  "x-up-devcap-screenpixels"};	// For UP browsers

	private Hashtable mPIDLookupTable;
	
	private long startTime;
	
	private boolean initDone = false;
	private ApplicationManager am;
	private ComponentManager cm;
	private MDesktop desktop;
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: HTTPEventTranslator
	// ---------------------------------------------------------------------------
	
	/**
	* Since the HTTPEventTranslator is a singleton, the constructor is private, thus preventing
	* any explicit construction
	*/
	private HTTPEventTranslator()
	{
    	this.mPIDLookupTable = new Hashtable();    
		
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: initialize
	// ---------------------------------------------------------------------------
	
	/**
	* Initialize the HTTPEventTranslator.  This involves getting references to a few required singleton Maui objects.
	* This is done only once, at startup time.
	*/
	public void initialize()
	{
		if (!initDone)
		{
			ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance();
			am = ApplicationManager.getInstance();
			cm = ComponentManager.getInstance ();
			desktop = MDesktop.getInstance ();
			
			initDone = true;
			System.err.println(new DebugString("[HTTPEventTranslator] - Started."));
		}
	}


	// ---------------------------------------------------------------------------
	// CLASS METHOD: getInstance
	// ---------------------------------------------------------------------------
	
	/**
	* Return an instance of the HTTPEventTranslator.
	*
	* @return The HTTPEventTranslator object
	*/
	public static HTTPEventTranslator getInstance()
	{
	  return theInstance;
	}
		
	
	/**
	* Build a description of the request header.  This is used during the creation of a MauiApplication.  Some information
	* from the HTTPRequest is required by the ApplicationManager to determine characteristics of the MauiApplication being
	* created.
	*
	* @param aRequest The HTTPRequest object
	*
	* @return A Map containing the required header values from the HTTPRequest
	*/
	private Map buildRequestHeaderDescription (HTTPRequest aRequest)
	{
		Map retVal = new HashMap ();
		
		String theValue = null;
		for (int i = 0; i < REQUESTHEADERS.length; i++)
		{
			theValue = aRequest.getHeaderValue (REQUESTHEADERS [i]);
			if (theValue != null)
			{
				retVal.put (REQUESTHEADERS [i], theValue);
			}
		}
		return retVal;
	}
		
	// ---------------------------------------------------------------------------
	// METHOD: translateRequest
	// ---------------------------------------------------------------------------
	
	/**
	* This is primary interface to HTTPRequest.  It determines what to do with the HTTPRequest
	*
	* @param request The HTTPRequest
	*
	* @return The HTTPResponse object, which contains the full response buffer
	*/
	public HTTPResponse translateRequest (HTTPRequest request)
		throws SessionMaximumException
	{
	  HTTPResponse response = request.createResponseObject ();
	  Compositor theCompositor = null;
	  MauiApplication application = null;
      boolean theOutOfDate = false;
	  String theApplicationName = null;
	  int theReference = 0;
	  boolean theCreate = false;
	
		try
		{
			// return a GIF
			if (request.getQueryValue("getImage") != null)
			{
				try
				{
					ResourceDescription description = new ResourceDescription(request.getQueries());
					
					byte[] gifImage = ResourceManager.getInstance().getResourceBytes(description);
					response.setContentType("image/gif");
					response.setContent(gifImage);
					gifImage = null;
				}
				catch (ResourceNotFoundException exception)
				{
				  System.out.println(new ErrorString(exception, "HTTPEventTranslator.translateRequest()"));
				}
			}
			// return a Style Sheet
			else if (request.getQueryValue("getCSS") != null)
			{
				try
				{
					//String css = new String(MauiApplication.getJarResources ().getResource(request.getQueryValue("getCSS")));
					byte[] css = ResourceManager.getInstance().getResourceBytes(request.getQueryValue("getCSS"));
					response.setContentType("text/css");
					response.setContent(css);
					css = null;
				}
				catch (ResourceNotFoundException exception)
				{
				  System.out.println(new ErrorString(exception, "HTTPEventTranslator.translateRequest()"));
				}
			}
			// return a HTML/WML/etc. page
			else
			{
				HTTPSession theSession = null;
				synchronized (this)
				{
			        // Lookup the mPID (Maui Application PID) based on the request's 
			        // session ID.  If the session is new, then construct a new Maui
			        // application and enter it into the pool.  Do the same if the 
			        // mPID lookup up is no longer valid.
			        theSession = request.getSession ();
				}
			  
				Object theSynchBlock = theSession.getSynchBlock ();
				synchronized (theSynchBlock)
				{
			        String sessionID = theSession.getID();
			        String mPID = (String)this.mPIDLookupTable.get(sessionID);
			        
			        theApplicationName = request.getApplicationName ();
			        if (theApplicationName.endsWith ("/"))
			        {
			        	theApplicationName = theApplicationName.substring (0, theApplicationName.length () - 1);
			        }
			        
			        theReference = Profiler.start (MauiRuntimeEngine.SOURCE_APPLICATION,
			        							   MauiRuntimeEngine.ACTION_CREATE);
			        theCreate = true;
		            boolean theDoLoad = false;
			        
			        if ((mPID == null) || !(ProcessManager.getInstance().processExists(mPID)))
			        {
			        	//
			        	//	This is a new session, so an application has to be loaded for it.
			        	//	The file portion of the URL indicates what application is expected.
			        	//	If there is no file portion, then use the default application.
			        	//
			        	application = am.createMauiApplication (buildRequestHeaderDescription (request),
			        											request.getCookies (),
			        											theSession,
			        											theApplicationName,
			        											true);
			        											
		        		if (application.isDefaultApplication () &&
		        			!am.isDefaultApplicationClassName (theApplicationName))
		        		{
		        			theApplicationName = am.getDefaultApplicationClassName ();
		        		}
			  			this.mPIDLookupTable.put(sessionID, application.getProcessID());
			        }
			        else
			        {
			          //
			          //	The process id was found, which means that the application exists.
			          //
			          application = ProcessManager.getInstance().getProcess (mPID);
			          String theApplicationAddress = null;
			          
			          //
			          //	Check if the URL indicates that a different application should be run (ie. application chaining)
			          //
			          
			          if (theApplicationName == null ||
			          	  theApplicationName.trim().length () == 0)
			          {
			          	if (!application.isDefaultApplication ())
			          	{
			          		//
			          		//	The default application has been requested but the current application isn't the default.
			          		//	So load the default application
			          		//
			          		theDoLoad = true;
			          	}
			          }
			          else if (!application.getApplicationAddress ().equals (
			          					(theApplicationAddress =am.getMauiApplicationAddress (theApplicationName))))
			          {
			          	//
			          	//	The URL indicates that a different application has been requested (probably due to application
			          	//	chaining, or a new application name explicitly being entered by the user).  So load the new application.
			          	//
			          	theDoLoad = true;
			          }
			          
			          if (theDoLoad)
			          {
			          	//
			          	//	The application in the URL is different from the current one.  So, chain to the new one
			          	//
			          	if ((application = theSession.getCachedMauiApplication (theApplicationName)) == null)
			          	{
			        		application = am.createMauiApplication (buildRequestHeaderDescription (request),
			        												request.getCookies (),
			        												theSession,
			        												theApplicationName,
			        												true);
			        		if (application.isDefaultApplication () &&
			        			!am.isDefaultApplicationClassName (theApplicationName))
			        		{
			        			theApplicationName = am.getDefaultApplicationClassName ();
			        		}
				        }
				  		mPIDLookupTable.put(sessionID, application.getProcessID());
			          }
			          else
			          {
			          	theCreate = false;
			          	theOutOfDate = am.isOutOfDate (application);
			          }
			        }
				}

				theSynchBlock = application.getSynchBlock ();
				synchronized (theSynchBlock)
				{
			        
					application.setRuntimeName ((theApplicationName == null ? "" : theApplicationName));
					application.setInboundCookies (request.getCookies ());
					application.setPath (request.getApplicationName ());
			        theSession.setApplication (application);
			        desktop.setApplication (application);
			        cm.setApplication (application);

					Profiler.finish (theReference,
									 MauiRuntimeEngine.SOURCE_APPLICATION,
									 (theCreate ? MauiRuntimeEngine.ACTION_CREATE :
												   MauiRuntimeEngine.ACTION_CACHE_HIT),
								    application.getRuntimeName ());
					
					boolean theDoChain = false;
			        String theChainApplicationName = null;
					if (!theOutOfDate)
					{
						HTTPSession.activateApplication (application);
			        	this.translateAndDispatchEvents(request);
						HTTPSession.deactivateApplication (application);
			        
				        theChainApplicationName = application.getChainApplicationName ();
				        if (theChainApplicationName != null &&
				        	! (application.getRuntimeName ().equals (theChainApplicationName)))
				        {
				        	//
				        	//	There is a request to switch to another MauiApplication.
				        	//	So, initiate application chaining.  HTTPResponse will take care of the
				        	//	HTTP redirect
				        	//
				        	theDoChain = true;
				        	String theFolderName = am.getFolderName (theChainApplicationName);
				        	if (theFolderName.length () > 0)
				        	{
				        		theFolderName = theFolderName + "/";
				        	}
				        	theChainApplicationName = theFolderName + am.getSimpleShortName (theChainApplicationName);
				        	String theServletURL = application.getServletURL ();
				        	if (theServletURL.length () > 1)
				        	{
				        		theChainApplicationName = theServletURL + theChainApplicationName;
				        	}
				        	application.setChainApplicationName (null);
				        	
				        	//
				        	//	Invalidate the application's window so that it will be completed re-rendered whenever
				        	//	the client returns to it.  It's possible that the application state will be stale.
				        	//
				        	application.invalidate ();
				        	//
				        	//	Chaining to another application
				        	//
				        	/*application = am.createMauiApplication (buildRequestHeaderDescription (request),
				        											sessionID,
				        											theApplicationName,
				        											true);
							application.setRuntimeName ((theApplicationName == null ? "" : theApplicationName));
				        	theSession.setApplication (application);
				        	desktop.setApplication (application);*/
				        }
				    }
				    
				    if (application.isExit () ||
			        	application.isExitAll ())
			        {
			        	//
			        	//	The application, or applications are exiting.  So remove the application references from the session,
			        	//	and set the redirection to the default application.
			        	//
			        	String theRedirection;
			        	if (!theOutOfDate)
			        	{
			        		theSession.removeApplication ((application.isExitAll () ? null : application));
			        		if ((theRedirection = application.getURLString ()) == null)
			        		{
			        			theRedirection = (theDoChain ? theChainApplicationName : "");
			        		}
			        	}
			        	else
			        	{
			        		theRedirection = application.getChainApplicationName ();
			        	}
			        	response.setRedirection (theRedirection);
			        }
			        else if (theDoChain)
			        {
			        	//
			        	//	Application chaining is occuring so there is no need to do any rendering for the current request
			        	//
			        	response.setRedirection (theChainApplicationName);
			        	response.setPseudoEvents (application.getPseudoEvents ());
			        	application.resetPseudoEvents ();
			        }
			        else
			        {
				        // Now obtain a compositor for the device in questions.  To do this,
				        // we must first assemble a Vector of relevant platform information 
				        // in order for the Composition Manager to give us what we need.

				        theCompositor = application.getCompositor ();	//	Compositor can be determined at application creation time

				        // Now we must do the composition.  The HTMLCompositor requires the
				        // MauiApplication in use, as well as HTTPRequest and HTTPResponse
				        // objects.  Obviously this isn't a good coupling for compositors in
				        // general (although the WMLCompositor accepts the same thing) ...
				        // this binding/interface will be re-visited, but this is the 
				        // mechanism in use for now.
				        
				        Hashtable parameters = new Hashtable();
			        	parameters.put("application", application);
				        parameters.put("request", request);
				        parameters.put("response", response);
				        
				        theCompositor.doComposition(parameters);
				        
				        //
				        //	If redirection was set, pass the new URL onto the HTTPResponse object
				        //
				        response.setRedirection (application.getURLString ());
				        application.setURLString (null);
					}
				}
			}
		}
		catch (Exception exception)
		{
			if (exception instanceof SessionMaximumException)
			{
				throw (SessionMaximumException) exception;
			}
			
			if (application != null)
			{
				HTTPSession.deactivateApplication (application);
			}
			//
			//	Something went wrong during the request processing.  Try to send some kind of informative error message to the
			//	client.
			//
			if (theCompositor == null)
			{
				if (application != null)
				{
					theCompositor = application.getCompositor ();
				}
			}
			
			if (theCompositor != null)
			{
				response.setContentType (theCompositor.getBaseContentType ());
				response.setContent ((theCompositor.generateExceptionMessage (exception)).getBytes ());
			}
			exception.printStackTrace(System.err);
		}
		
		return response;
	}


	// ---------------------------------------------------------------------------
	// METHOD: translateAndDispatchEvents
	// ----------------------------------------------------------------------
	
	/** This method is responsible for extracting and parsing the components id's in the MauiEventQueue,
	  * getting their respective posted values, creating MActionEvent objects, and publishing them
	  *
	  * @param request The HTTPRequest (All of the posted information is available through this object)
	  * 
	  */
	  
	private void translateAndDispatchEvents(HTTPRequest request)
	{
		Vector queue = new Vector();
		Vector theMultipleEvents = new Vector ();
		boolean theAllowDuplicateEvents = true;

		//
		//	Pseudo events are events which are constructed from information contained within the base URL (ie. not parameterized, and
		//	prior to the "?" in the URL string.  This is to compensate for caching strategies in some WML browsers (eg. Nokia)
		//	which cause bad URL's.
		//
		String [] thePseudoEvents = request.getPseudoEvents ();
		for (int i = 0; i < thePseudoEvents.length; i++)
		{
			processOneEvent (request, thePseudoEvents [i], theMultipleEvents);
		}
		
		String queueString = request.getQueryValue("mauiEventQueue");
		
		//
		// Look for lonely non-MSettable componentIDs in the query string and tack them on to the end of 
		// the event queue. This is necessary for clients which do not support client-side scripting (like 
		// Lynx and Palm Web Clippings). [ian - 2001.06.25]
		//		
		Enumeration queryKeys = request.getQueries().keys();
		while (queryKeys.hasMoreElements())
		{
			String key = (String)queryKeys.nextElement();
			if (cm.getComponent(key) != null && !(cm.getComponent(key) instanceof MSettable))
			{
				if (queueString != null && !queueString.equals(""))
				{
					queueString += "," + key;
				}
				else
				{
					queueString = key;
				}
			}
		}

		if (queueString != null)
		{
			//
			//	There is data for the mauiEventQueue, so it probably means that there are some events
			//	to process.
			//
			StringTokenizer queueTokens = new StringTokenizer(queueString, ",");
			int theSequenceNumber = -1;
			boolean theIsDuplicateEvent = false;
			while (queueTokens.hasMoreTokens() && theAllowDuplicateEvents)
			{
				//
				//	Construct a Vector of the events.  This step is taken to prevent duplication of
				//	componentID's within the queue
				//
				String componentID = queueTokens.nextToken();
				// Ensure that each component appears only once in the queue.
				if (!queue.contains(componentID))
				{
					MComponent theComponent = getComponent (componentID);
					if (theComponent != null)
					{
						if (theSequenceNumber == -1)
						{
							MComponent theRootParent = theComponent.getRootParent ();
							if (theRootParent instanceof MauiApplication)
							{
								MauiApplication theApplication = (MauiApplication) theRootParent;
								String theSequenceString = request.getQueryValue ("sequenceNumber");
								if (theSequenceString != null)
								{
									theSequenceNumber = Integer.parseInt (theSequenceString);
									theIsDuplicateEvent = theApplication.isDuplicateEvent (theSequenceNumber);
									if (!theApplication.getAllowDuplicateEvents ())
									{
										theAllowDuplicateEvents = !theIsDuplicateEvent;
									}
								}
								else
								{
									theSequenceNumber = -2;
								}
                				
							}
						}
                	
						if (theSequenceNumber != -2 &&
						    theAllowDuplicateEvents &&
						    !theComponent.getAllowDuplicateEvents ())
						{
							theAllowDuplicateEvents = !theIsDuplicateEvent;
						}
                		
					}
					
					//++ 405 JL 2001.09.21
					// Warning!!! HACK Alert. This is a hack! The HTTPEventTranslator should know nothing about 
					// MRadioButtons but lo and behold, component specific code. 
					if (theComponent instanceof MRadioButton)
					{
					    //  if the component is an MRadioButton, add if it appears in the Query string
					    //  (ie. it was the selected member of its group) add it to the event queue
                        
                        if (request.getQueries().values().contains(componentID))
					    {
                            queue.addElement(componentID);
                        }
					}
					else
					{
        			    queue.addElement(componentID);
					}
					//--
					
				}
			}

			if (theAllowDuplicateEvents)
			{
	
				Enumeration queueElements = queue.elements();

				//
				//	Process an event for each component id in the event Vector
				//
				while (queueElements.hasMoreElements())
				{
					String componentID = (String)queueElements.nextElement();					
					processOneEvent (request, componentID, theMultipleEvents);
				}
			}
		}
       
		if (theAllowDuplicateEvents)
		{
			//
			//	Because of scripting limitations, WML also has a couple of special variables which describe a single
			//	event rather than a complete array of events.
			//
			String oneEvent = request.getQueryValue ("mauiOneEvent");
			if (oneEvent != null)
			{
				if (!queue.contains (oneEvent))
				{
					processOneEvent (request, oneEvent, theMultipleEvents);
				}
			}
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getComponent
	// ----------------------------------------------------------------------
	
	/** Get a component
	  *
	  * @param aComponentID The componentID to use for lookup
	  *
	  * @return The corresponding component
	  * 
	  */
	  
	private MComponent getComponent (String aComponentID)
	{
		int theUnderScore = aComponentID.indexOf ("z");
		String theLookupComponentID = (theUnderScore != -1 ?
	  									aComponentID.substring (theUnderScore + 1) :
	  									aComponentID);

	  // NOTE ** This code must be audited for security breeches and stuff like
	  //         that.  Spoofing of other people's events may become an issue...

	  return ComponentManager.getInstance().getComponent (theLookupComponentID);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: processOneEvent
	// ----------------------------------------------------------------------
	
	/**
	* Process a single event.
	*
	* @param request The HTTPRequest
	* @param componentID The target component id for the event.  This is used to retrieve the event value from the
	* @param aMultipleEvents Vector containing a list of components accumulated for a multiple event component.
	*
	*/			 
    private void processOneEvent (HTTPRequest request, String componentID, Vector aMultipleEvents)
    {
    	MComponent component = getComponent (componentID);
    	
        if (component != null && component.isEnabled())
        {
            String stateData = request.getQueryValue(componentID);
            if (stateData != null || request.isEmptySignificant ())
            {
	        	I_Renderer theRenderer = component.getRenderer ();
	        	if (theRenderer instanceof I_HasMultiple)
	        	{
	        		String [] theComponentIDs = ((I_HasMultiple) theRenderer).getComponentIDs (componentID, stateData);
	        		for (int i = 0; i < theComponentIDs.length; i++)
	        		{
	        			if (!aMultipleEvents.contains (theComponentIDs [i]))
	        			{
	        				aMultipleEvents.addElement (theComponentIDs [i]);
		        			component = getComponent (theComponentIDs [i]);
		        			processOneActionEvent (component,
		        								   ((I_HasMultiple) theRenderer).
		        								   				getStateData (theComponentIDs [i],
		        								   							   request.getQueryValue (theComponentIDs [i])));
		        		}
	        		}
	        	}
	        	else
	        	{
	        		processOneActionEvent (component, stateData);
	        	}
			}
        }
    }
    
    /**
    * Process a single action event
    *
    * @param aComponent The component to process
    * @param aStateData The state data for the component
    */
    private void processOneActionEvent (MComponent aComponent,
    									String aStateData)
    {
    	if (aComponent != null && aComponent.isEnabled ())
    	{
			MauiEvent theEvent = aComponent.createEvent (aStateData);

			//if (!theEvent.isConsumed())
			//{
				if (theEvent instanceof MActionEvent)
				{
					aComponent.dispatchActionEvent ((MActionEvent) theEvent);
				}
			//}
		}
	}
	
	// ---------------------------------------------------------------------------
}

// =============================================================================
// Copyright (c) 1999 Bitmovers Communications Inc.                          eof