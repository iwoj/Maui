// =============================================================================
// CHANGELOG:
//++ 286 MW 2001.08.03
// Added a method to convert multiple spaces in a string of html into nbsp;
// spaces, with encodeWhitespace(). This method only converts every other
// space into an nbsp; so line breaks will still work in the appropriate place.
// =============================================================================


package com.bitmovers.maui.engine.htmlcompositor;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.utilities.*;


// ========================================================================
// SINGLETON CLASS: HTMLCompositor               (c) 2001 Bitmovers Systems
// ========================================================================

public class HTMLCompositor implements Compositor
{
	
		
  private static HTMLCompositor theInstance = new HTMLCompositor();  
	
	
	// ---------------------------------------------------------------------------
	// STATIC METHOD: zeroFill
	// ---------------------------------------------------------------------------
	
	/** Zero fill a value up to the length specified. For example, calling
	  * <code>zeroFill("1234", 10)</code> would return <code>"0000001234"</code>.
	  *
	  * @param aValue The String representation of the value to zero fill.
	  *
	  * @param aSize The size to zero fill to
	  *
	  * @return The zerofilled value
	  *
	  */
	  
	public static String zeroFill (String aValue, int aSize)
	{
		StringBuffer retVal = new StringBuffer (aValue);
		int theSizeToFill = aSize - retVal.length () ;
		if (theSizeToFill > 0)
		{
			StringBuffer theZeros = new StringBuffer ("000000000000000".substring (0, theSizeToFill));
			theZeros.append (retVal);
			retVal = theZeros;
		}
		return retVal.toString ();
	}
	
	
	// ---------------------------------------------------------------------------
	// STATIC METHOD: colorToRGBHexString
	// ---------------------------------------------------------------------------
	
	/** This method converts a Java AWT Color object into an RGB Hex String in the 
	  * form <code>"RRGGBB"</code> (e.g. <code>"ff0000"</code> is 100% red).
	  * 
	  */
	  
	public static String colorToRGBHexString(Color color)
	{
		StringBuffer hexString = new StringBuffer();
		
		hexString.append(zeroFill (Integer.toHexString(color.getRed()), 2));
		hexString.append(zeroFill (Integer.toHexString(color.getGreen()), 2));
		hexString.append(zeroFill (Integer.toHexString(color.getBlue()), 2));
		
		return hexString.toString();
	}
	
	
	// ---------------------------------------------------------------------------
	// STATIC METHOD: encodeHTML
	// ---------------------------------------------------------------------------
	
	/** This method takes a regular Java unicode string and returns an HTML-safe
	  * string. This is particularly useful in Maui HTML renderers.
	  * 
	  */
	  
	public static String encodeHTML(final String javaString)
	{
		StringBuffer encodedString = new StringBuffer();
		
		for (int i = 0; i < javaString.length(); i++)
		{
			char thisCharValue = javaString.charAt(i);
			
			switch (thisCharValue)
			{
				// Tab
				// case 9:
				case '\t':
					encodedString.append ("&nbsp;&nbsp;&nbsp;&nbsp;");
					break;
					
				// Line feed (\n)
				//case 10:
				case '\n':
					encodedString.append("<br>");
					break;
					
				// Quotation Mark (")
				//case 34:
				case '"':
					encodedString.append("&quot;");
					break;
					
				// Ampersand (&)
				//case 38:
				case '&':
					encodedString.append("&amp;");
					break;
					
				// Semicolon (;)
				//case 59:
				case ';':
					encodedString.append("&#59;");
					break;
					
				// Less-than (<)
				//case 60:
				case '<':
					encodedString.append("&lt;");
					break;
					
				// Greater-than (>)
				//case 62:
				case '>':
					encodedString.append("&gt;");
					break;
					
				case '©':
					encodedString.append("&copy;");
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
		
		//++ 286 MW 2001.08.03
		return encodeWhitespace(encodedString.toString());
		//-- 286
	}
	


	// ---------------------------------------------------------------------------
	// STATIC METHOD: encodeWhitespace
	// ---------------------------------------------------------------------------
	
	/** This method converts every other space into an &nbsp; character. For
	  * instance if there are 3 spaces there will be 1 &nbsp; char, if there are 5
	  * spaces, then there will be 3 &nbsp; chars.<p>
	  * 
	  * NOTE: does not yet account for tabs, or other types of whitespace.
	  * 
	  */
	  
	public static String encodeWhitespace(String javaString)
	{
		String tempString = javaString;
		
		int indexOfSpaces;
		
		while((indexOfSpaces = tempString.indexOf("  ")) != -1)
		{
			tempString = com.bitmovers.utilities.StringUtilities.replaceString(tempString, "  ", " &nbsp;");
		}

		return tempString;
	}

	
	// ---------------------------------------------------------------------------
	
	
	private boolean initDone = false;
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: HTMLCompositor
	// ---------------------------------------------------------------------------
	
	private HTMLCompositor()
	{
		System.err.println(new DebugString("[HTMLCompositor] - Started."));
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: initialize
	// ---------------------------------------------------------------------------

	public void initialize()
	{
		if (!initDone)
		{
			initDone = true;

			/* The servletURL is in the process of being removed entirely. I will keep
			   this here commented out until it is 100% ready to be removed.
			
			ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
			servletURL = "/";//theSCM.getProperty (ServerConfigurationManager.MAUI_HTML_SERVLET_URL) + ":" +
									 //theSCM.getProperty (ServerConfigurationManager.MAUI_PORT) + "/";
			*/

		}
	}


	// ---------------------------------------------------------------------------
	// CLASS METHOD: getInstance
	// ---------------------------------------------------------------------------
	
	public static HTMLCompositor getInstance()
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
	
	/** NOTE: The following are remnants of the Maui 0.5 'event system'.  Even though the
    *      comments in the code might refer to 'events', they're not talking
    *      about MauiEvents in the sense of Maui 0.8.
	  *
    */
	
	private void processEvent(MauiApplication mauiApp, Vector eventVector, Hashtable paramHash, HTTPRequest request, HTTPResponse response)
	{
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
				
				// if (componentID.startsWith("ToolbarButton"))
				// {
					// try
					// {
						// Get the button from the component map and select it.
						// ToolbarButton button = (ToolbarButton)mauiApp.componentMap.get(componentID);
						// button.setSelected(true);
					// }
					// catch (NullPointerException e) { }
				// }
				// else
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
						response.setContentType("text/html");
					}
				}
				response.setContent(mauiApp.render().getBytes());
			}
			else
			{
				response.setContentType("text/html");
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
    * @param request The HttpServletRequest object.
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
  	return "text/html";
  }
  
  
	// ----------------------------------------------------------------------
	// METHOD: generateExceptionMessage
	// ----------------------------------------------------------------------
	
  /** Generate an exception message.
    * 
    * @param aException The Exception object
    * 
    * @return A representation of the exception which is appropriate for the client
    *         type.
    * 
    */
    
  public String generateExceptionMessage(Exception aException)
  {
  	String retVal = null;
  	if (LogManager.isDebug())
  	{
  		PrintStream thePrintStream = System.err;
  		ByteArrayOutputStream theOutput = new ByteArrayOutputStream ();
  		PrintStream theNewPrintStream = new PrintStream (theOutput);
  		theNewPrintStream.println ("<pre>");
  		theNewPrintStream.println ("[HTTPEventTranslator Error (" + aException.getClass().toString() + ")]");
  		System.setErr (theNewPrintStream);
  		aException.printStackTrace ();
  		theNewPrintStream.println ("</pre>");
  		retVal = new String (theOutput.toString ());
  		System.setErr (thePrintStream);
  	}
  	return (retVal == null ?
  							"[HTTPEventTranslator Error (" + aException.getClass().toString() + ")]" :
  							retVal);
  }
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF