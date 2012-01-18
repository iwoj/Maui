// =============================================================================
// CHANGELOG:
//++ 286 MW 2001.08.07
// Called a method to convert multiple spaces in a string of html into nbsp;
// spaces, with encodeWhitespace(). This method only converts every other
// space into an nbsp; so line breaks will still work in the appropriate place.
//++ 352 MW 2001.08.15
// Added a tab character (as two non-breaking spaces) to the encodeWML method.
// =============================================================================

package com.bitmovers.maui.engine.wmlcompositor;

import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
//++ 286 MW 2001.08.07
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
//-- 286
import com.bitmovers.utilities.*;


// =============================================================================
// SINGLETON CLASS: WMLCompositor                     (c) 2001 Bitmovers Systems
// =============================================================================

/** This object is used for handling the event processing and composition of all 
  * WML-based clients.
  *
  * @invisible
  *
  */
  
public class WMLCompositor implements Compositor
{
	
  
	public final static String servletURL = "/";
	
	private static WMLCompositor theInstance = new WMLCompositor();  
  
	
	// ---------------------------------------------------------------------------
	// STATIC METHOD: encodeWML
	// ---------------------------------------------------------------------------
	
	/** This method takes a regular Java unicode string and returns an WML-safe
	  * string. This is particularly useful in Maui WML renderers.
	  * 
	  */
	  
	public static String encodeWML(final String javaString)
	{
		StringBuffer encodedString = new StringBuffer();
		
		for (int i = 0; i < javaString.length(); i++)
		{
			int thisCharValue = javaString.charAt(i);
			
			switch (thisCharValue)
			{
				//++ 352 MW 2001.08.15
				// Tab (\t)
				case 9:
					encodedString.append ("&nbsp;&nbsp;");
					break;
				//-- 352
					
				// Line feed (\n)
				case 10:
					encodedString.append("<br/>");
					break;
					
				// Quotation Mark (")
				case 34:
					encodedString.append("&quot;");
					break;
					
				// Dollar sign ($)
				case 36:
					encodedString.append("$$");
					break;
					
				// Ampersand (&)
				case 38:
					encodedString.append("&amp;");
					break;
					
				// Semicolon (;)
				case 59:
					encodedString.append("&#59;");
					break;
					
				// Less-than (<)
				case 60:
					encodedString.append("&lt;");
					break;
					
				// Greater-than (>)
				case 62:
					encodedString.append("&gt;");
					break;
					
				//
				// Macintosh ASCII
				//
				
				// Curly open quote (Ò)
				// Curly close quote (Ó)
				case 210:
				case 211:
					encodedString.append("\"");
					break;
					
				// Curly open apostrophe (Ô)
				// Curly close apostrophe (Õ)
				case 212:
				case 213:
					encodedString.append("'");
					break;
					
				default:
					// Handle extended ASCII and Unicode characters.
					if (thisCharValue >= 160)
					{
						encodedString.append("&#");
						encodedString.append(thisCharValue);
						encodedString.append(";");
					}
					// Otherwise, this is probably a safe ASCII character.
					else
					{
						encodedString.append(javaString.charAt(i));
					}
					break;
			}
		}
		
		//++ 286 MW 2001.08.07
		return HTMLCompositor.encodeWhitespace(encodedString.toString());
		//-- 286
	}
	
	
	private boolean initDone = false;
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: WMLCompositor
	// ---------------------------------------------------------------------------
	
	/** This is a private constructor to enforce the Singleton pattern.
	  *
	  */
	  
	private WMLCompositor()
	{
		System.err.println(new DebugString("[WMLCompositor] - Started."));
	}


	// ---------------------------------------------------------------------------
	// METHOD: initialize
	// ---------------------------------------------------------------------------
	/**
	* Perform once only initialization for the WMLCompositor
	*/
	public void initialize()
	{
		if (!initDone)
		{
			initDone = true;
			
			/* The servletURL is in the process of being removed entirely. I will keep
			   this here commented out until it is 100% ready to be removed.
			
			//ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
			//servletURL = theSCM.getProperty (ServerConfigurationManager.MAUI_WML_SERVLET_URL) + ":" +
			//						 theSCM.getProperty (ServerConfigurationManager.MAUI_PORT + "/");
			*/
		}
	}


	// ---------------------------------------------------------------------------
	// CLASS METHOD: getInstance
	// ---------------------------------------------------------------------------
	/**
	* Get an instance of the WMLCompositor.  Since this is a singleton, it will always be the
	* same object.
	*/
	public static WMLCompositor getInstance()
	{
	  return theInstance;
	}


	// ---------------------------------------------------------------------------
	// METHOD: doComposition
	// ---------------------------------------------------------------------------
	
	// NOTE: This method is *very* temporary and is meant only to integrate the
	//       previous codebase of the Maui 0.5 rendering system in to this 
	//       reference implementation of Maui.  Some issues need to be discussed
	//       here -- notably the current coupling of this compositor to 
	//       the 'httpserver' package (ie. the HTTPRequest and the HTTPResponse).
	
	/**
	* This is the method which performs the event processing and HTTPResponse construction for
	* a WML client.
	*
	* @param parameters A Hashtable of information used for processing the request
	*/
	public void doComposition(Hashtable parameters)
	{
		MauiApplication application = (MauiApplication)parameters.get("application");
		HTTPRequest request = (HTTPRequest)parameters.get("request");
		HTTPResponse response = (HTTPResponse)parameters.get("response");
   
		Hashtable paramHash = createParamHash(request);
		Vector eventVector = createEventVector(request);
				
		this.processEvent(application, eventVector, paramHash, request, response);
	}


	// ---------------------------------------------------------------------------
	// METHOD: processEvent
	// ---------------------------------------------------------------------------
	
  // NOTE: This is remanants of the Maui 0.5 'event system'.  Even though the
  //       comments in the code might refer to 'events', they're not talking
  //       about MauiEvents in the sense of Maui 0.8.

	/** This method takes care of some special generic Maui events. It should
	  * be called before the Maui application handles the event.
    *
    * @param mauiApp Reference to the MauiApplication associated with the events
    * @param eventVector The same event vector that is passed to the Maui app.
    * @param paramHash Hashtable of HTTP parameters
    * @param response The HTTPResponse object which will be sent back to the client
    */
	
	private void processEvent(MauiApplication mauiApp, Vector eventVector, Hashtable paramHash, HTTPRequest request, HTTPResponse response)
	{
		boolean passEventToMauiApp = true;
		
		try
		{
			if (eventVector != null && !eventVector.isEmpty())
			{
				// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
				// Component events
				// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
				
				String componentID = (String)eventVector.lastElement();
				
				// If there are parentheses...
				if (componentID.startsWith("(") && componentID.endsWith(")"))
				{
					// ... strip them off!
					componentID = componentID.substring(1, componentID.length() - 1);
				}
				
				//
				//	Strip off prefix - some wml browsers don't like variables that begin with a digit
				//
				if (componentID.startsWith ("IDz"))
				{
					componentID = componentID.substring (3);
				}
				{
					System.err.println("componentID: " + componentID);
				}
				
				
				// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
				// Pass events to Maui application
				// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
				
        // Check for a content-type override
				String contentType = null;
				{
  				if ((contentType = request.getQueryValue("contentType")) != null)
					{
				    response.setContentType(contentType);
					}
					else
					{
						response.setContentType("x-wap.wml");
					}
				}
				response.setContent(mauiApp.render().getBytes());
			}
			else
			{
				response.setContentType("text/vnd.wap.wml");
				response.setContent(mauiApp.render().getBytes());
			}
		}
		catch (Exception e)
		{
			response.setContentType(getBaseContentType ());
			response.setContent((generateExceptionMessage (e)).getBytes());
			e.printStackTrace(System.err);
		}
	}


	// ---------------------------------------------------------------------------
	// METHOD: createEventVector
	// ---------------------------------------------------------------------------

  // NOTE: This is remanants of the Maui 0.5 'event system'.  Even though the
  //       comments in the code might refer to 'events', they're not talking
  //       about MauiEvents in the sense of Maui 0.8.
	
	/** This method creates an event vector based on a given request object.
    * 
    * @param request The HttpRequest object.
    *
    * @return A Vector containing the events
    */
	
	private Vector createEventVector(HTTPRequest request)
	{
		Vector eventVector = new Vector();
		if (request.getQueryValue("mauiEvent") != null)
		{
			eventVector = StringUtilities.split(".", request.getQueryValue("mauiEvent"));
		}
		return eventVector;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: createParamHash
	// ---------------------------------------------------------------------------
	
  // NOTE: This is remanants of the Maui 0.5 'event system'.  Even though the
  //       comments in the code might refer to 'events', they're not talking
  //       about MauiEvents in the sense of Maui 0.8.

	/** This method creates a parameter hashtables based on a given request object.
    * 
    * @param request The HttpServletRequest object.
    *
    * @return A Hashtable containing the HTTP parameter key/value pairs
    */
	
	private Hashtable createParamHash(HTTPRequest request)
	{
		return request.getQueries();
	}
			

  /**
  * Get the base content type for this compositor
  *
  * @return A String description of the base content type
  */
  public String getBaseContentType ()
  {
  	return "text/vnd.wap.wml";
  }
  
  /**
  * Generate an exception message
  *
  * @param aException The Exception object
  *
  * @return A representation of the exception which is appropriate for the client
  * 			  type
  */
  public String generateExceptionMessage (Exception aException)
  {
		StringBuffer retVal = new StringBuffer ("<?xml version=\"1.0\"?>\n");
		retVal.append("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">\n");
		retVal.append("<wml>\n");
 		retVal.append("<card><p>\n");
 		retVal.append("[HTTPEventTranslator Error (");
 		retVal.append(aException.getClass().toString());
 		retVal.append(")]\n");
   	retVal.append("</p></card>\n");
   	retVal.append("</wml>");
  	return retVal.toString ();
  }
	
	// ---------------------------------------------------------------------------

}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof