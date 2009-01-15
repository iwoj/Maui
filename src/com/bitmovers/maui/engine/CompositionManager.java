package com.bitmovers.maui.engine;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.Dimension;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.htmlcompositor.*;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.engine.httpserver.HTTPSession;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.utilities.*;


// ========================================================================
// <<SINGLETON>> CLASS: CompositionManager
// ========================================================================

public class CompositionManager
{
	
	
  private static CompositionManager theInstance = new CompositionManager();
  
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR: CompositionManager
	// ----------------------------------------------------------------------
	
	private CompositionManager()
	{
		System.err.println(new DebugString("[CompositionManager] - Started."));
	}


	// ----------------------------------------------------------------------
	// CLASS METHOD: getInstance
	// ----------------------------------------------------------------------
	
	public static CompositionManager getInstance()
	{
	  return theInstance;
	}


	// ----------------------------------------------------------------------
	// METHOD: getCompositor
	// ----------------------------------------------------------------------
	
	/** getCompositor returns a reference to a particular Compositor for a 
	  * desired device.  The method determines which Compositor is best for
	  * a particular device by virtue of the passed 'deviceInformation'.  This
	  * deviceInformation is a Vector of Strings -- the Strings contain 
	  * relevant information (such as OS type, OS version, platform type,
	  * platform version, browser type, version, etc., etc.).  This information
	  * is provided in a nonstandard form (different devices present such details
	  * in different forms), so this method employs various techniques to sort
	  * things out and provide the best Compositor for the job...
	  * 
	  */
	
	public Compositor getCompositor(Map deviceInformation)
	{
	  // NOTE: This is a temporary algorithim ... to be replaced with
	  //       something a little more intelligent later on!
	
    String usefulElements = new String();
	
	  Iterator elements = deviceInformation.entrySet().iterator ();
	  
	  while (elements.hasNext ())
	  {
	    String element = elements.next().toString();
	    usefulElements += element;
	    System.out.println(new DebugString ("[CompositionManager] Useful: " + element));
	  }

    usefulElements = usefulElements.toLowerCase();
    
    // 1) Match user agent if possible.
    
    // Netscape and browsers that are almost compatible like Internet Explorer 
    // and Elaine (the Palm web clipping browser).
    if (usefulElements.indexOf("mozilla") != -1)
    {
	    System.out.println(new DebugString ("[CompositionManager] Using HTMLCompositor!"));
  	  return HTMLCompositor.getInstance();
    }
    // UP.Browser
    else if (usefulElements.indexOf("up.browser") != -1)
    {
	    System.out.println(new DebugString ("[CompositionManager] Using WMLCompositor!"));
  	  return WMLCompositor.getInstance();
    }
    else if (usefulElements.indexOf("klondike/1.1") != -1)
    {
	    System.out.println(new DebugString ("[CompositionManager] Using WMLCompositor!"));
  	  return WMLCompositor.getInstance();
    }
    else if (usefulElements.indexOf ("wapsody") != -1)
    {
    	System.out.println (new DebugString ("[CompositionManager] Using WMLCompositor!"));
    	return WMLCompositor.getInstance ();
    }
    else if (usefulElements.indexOf ("nokia") != -1)
    {
    	System.out.println (new DebugString ("[CompositionManager] Using WMLCompositor!"));
    	return WMLCompositor.getInstance ();
    }
    
    // 2) If user agent is unknown, guess compositor based on acceptable 
    // MIME types. Start from simplest display platforms and work towards
    // more complex ones (e.g. WML, then HTML).
    
    else if (usefulElements.indexOf ("text/vnd.wap.wml") != -1)
    {
    	System.out.println (new DebugString ("[CompositionManager] Using WMLCompositor!"));
    	return WMLCompositor.getInstance ();
    }
    
    else if (usefulElements.indexOf ("text/html") != -1)
    {
	    System.out.println(new DebugString ("[CompositionManager] Using Default Compositor (HTML)."));
  	  return HTMLCompositor.getInstance();
    }
    
    // 3) If all else fails, use HTMLCompositor.
    
    else
    {
	    System.out.println(new DebugString ("[CompositionManager] Using Default Compositor (HTML)."));
  	  return HTMLCompositor.getInstance();
  	}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRenderer
	// ----------------------------------------------------------------------
	
	/** Get an I_Renderer object given an I_Renderable object
	  *
	  * @param aRenderable The I_Renderable object
	  *
	  * @return The I_Renderer object
	  * 
	  */
	  
	public I_Renderer getRenderer (I_Renderable aRenderable)
	{
		return getRenderer (aRenderable, (MComponent) aRenderable);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRenderer
	// ----------------------------------------------------------------------
	
	/** Get the client classification.  This is just a string which uniquely 
	  * identifies the client. The first item is the data format (e.g. HTML, 
	  * WML, etc.). The second item describes the client (e.g. "netscape", 
	  * "ie", "nokia"). The third item is a version string (e.g. "4") for 
	  * cases when it is important to differentiate between versions of the
	  * same client.<p>
	  * 
	  * Client classification strings are used for loading renderer classes
	  * and component templates.
		*
		* @return The client classification array
		* 
		*/
		
	private String [] getClientClassification (MauiApplication aMauiApplication)
	{
		String [] retVal = new String [3];
		HTTPSession theSession = aMauiApplication.getSession ();
		retVal [0] = (aMauiApplication.getCompositor () instanceof WMLCompositor ? "wml" : "html");
		if (retVal [0].equals ("wml"))
		{
			theSession.setKeepAlive (false);
		}
		
		Map theHeaderValues = aMauiApplication.getRequestHeaderValues ();
		String theUserAgent = (String) theHeaderValues.get ("user-agent");
		if (theUserAgent != null)
		{
			theUserAgent = theUserAgent.toLowerCase ();
			int theIndex;
			
			// Internet Explorer
			// e.g. "Mozilla/4.0 (compatible; MSIE 5.0; Mac_PowerPC)"
			if ((theIndex = theUserAgent.indexOf ("msie")) != -1)
			{
				retVal [1] = "explorer";
				theUserAgent = theUserAgent.substring (theIndex + 5);
				theIndex = theUserAgent.indexOf (";");
				retVal [2] = theUserAgent.substring (0, theIndex);
			}
			else if ((theIndex = theUserAgent.indexOf ("opera")) != -1)
			{
				retVal [1] = "opera";
				theIndex = theUserAgent.indexOf ("/");
				theUserAgent = theUserAgent.substring (theIndex);
				retVal [2] = theUserAgent.substring (0, theUserAgent.indexOf (" "));
				
				//
				//	Can't use keep alive
				//
				theSession.setKeepAlive (false);
			}
			
			// Palm Web Clipping Application (a.k.a. PQA)
			// e.g. "Mozilla/2.0 (compatible; Elaine/2.0)"
			else if (theUserAgent.indexOf ("elaine") != -1)
			{
				retVal [1] = "wca";
				theIndex = theUserAgent.indexOf ("/");
				theUserAgent = theUserAgent.substring (theIndex + 1);
				theIndex = theUserAgent.indexOf (")");
				retVal [2] = theUserAgent.substring (0, theIndex);
				
				// If we are dealing with a RIM device, set the dimension to 132x64
				if ((theUserAgent.indexOf("rim") != -1) && (theUserAgent.indexOf("957") == -1))
				{
					theSession.setClientDimension(new Dimension(132, 64));
				}
				// All Palms have 160x160 screens.
				else
				{
					theSession.setClientDimension(new Dimension(160, 160));
				}
			}
			
			// Netscape
			// e.g. "Mozilla/4.76 (Macintosh; U; PPC)"
			else if (theUserAgent.indexOf ("mozilla") != -1)
			{
				retVal [1] = "netscape";
				theIndex = theUserAgent.indexOf ("/");
				theUserAgent = theUserAgent.substring (theIndex + 1);
				theIndex = theUserAgent.indexOf (" ");
				retVal [2] = theUserAgent.substring (0, theIndex);
			}
			
			// Lynx
			// e.g. "Lynx/2.8.3rel.1 libwww-FM/2.14"
			else if (theUserAgent.indexOf ("lynx") == 0)
			{
				retVal [1] = "lynx";
				theIndex = theUserAgent.indexOf ("/");
				theUserAgent = theUserAgent.substring (theIndex + 1);
				theIndex = theUserAgent.indexOf (" ");
				retVal [2] = theUserAgent.substring (0, theIndex);
			}
			
			// UP.Browser
			else if ((theIndex = theUserAgent.indexOf ("up.browser")) != -1)
			{
				retVal [1] = "up";
				theIndex = theUserAgent.indexOf ("/");
				theUserAgent = theUserAgent.substring (theIndex + 1);
				theIndex = theUserAgent.indexOf (" ");
				int theOther = theUserAgent.indexOf ("-");
				if (theOther != -1 && theOther < theIndex)
				{
					theIndex = theOther;
				}
				retVal [2] = theUserAgent.substring (0, theIndex);
				String theDimension = (String) theHeaderValues.get ("x-up-devcap-screenpixels");
				if (theDimension != null)
				{
					int theComma = theDimension.indexOf (',');
					theSession.setClientDimension (new Dimension (Integer.parseInt (theDimension.substring (0, theComma)),
																  Integer.parseInt (theDimension.substring (theComma + 1))));
				}
			}
			
			// Klondike
			else if (theUserAgent.indexOf ("klondike") != -1)
			{
				retVal [1] = "klondike";
				retVal [2] = null;
			}
			
			// Wapsody
			else if (theUserAgent.indexOf ("wapsody") != -1)
			{
				retVal [1] = "wapsody";
				retVal [2] = null;
			}
			
			// Nokia
			else if (theUserAgent.indexOf ("nokia") != -1)
			{
				retVal [1] = "nokia";
				//
				//	7110 is (96 x 65)
				//
				if ((theIndex = theUserAgent.indexOf ("7110")) != -1)
				{
					theSession.setClientDimension (new Dimension (96, 65));
					theUserAgent = theUserAgent.substring (theIndex + 5);
				}
				
				theIndex = theUserAgent.indexOf ("/");
				if (theIndex != -1)
				{
					theUserAgent = theUserAgent.substring (theIndex + 1);
				}
				theIndex = theUserAgent.indexOf (" ");
				retVal [2] = (theIndex == -1 ? theUserAgent : theUserAgent.substring (0, theIndex));
				
			}
			else
			{
				retVal [1] = "unknown";
				retVal [2] = null;
			}
			
			if (retVal [2] != null)
			{
				retVal [2] = retVal[2].replace ('.', '_');
			}
		}
		else
		{
			retVal [1] = "unknown";
		}
		return retVal;
	}
	
	private String buildCheckName (String aBaseName, String [] aClientClassification, int aSize)
	{
		String retVal = "com.bitmovers.maui.engine.render." + aBaseName;
		for (int i = 0; i < aSize; i++)
		{
			retVal += "_" + aClientClassification [i];
		}
		return retVal;
	}
	
	private I_Renderer testFileName (I_Renderable aRenderable, MComponent aComponent, String aBaseName, String [] aClientClassification, int aIndex)
	{
		I_Renderer retVal = null;
		String theCheckName = buildCheckName (aBaseName, aClientClassification, aIndex);
		try
		{
			retVal = (I_Renderer) Class.forName (theCheckName).newInstance ();
			if (retVal instanceof I_RendererInitialize)
			{
				((I_RendererInitialize) retVal).initialize (aRenderable, aComponent, aClientClassification);
			}
		}
		catch (Exception e)
		{
		}
		return retVal;
	}
	
	private I_Renderer locateRenderer (I_Renderable aRenderable, String [] aClientClassification, MComponent aComponent)
	{
		I_Renderer retVal = null;
		Class theCheckClass = (aRenderable instanceof MauiApplication ?
									MauiApplication.class :
									aRenderable.getClass ());
		String theBaseName = theCheckClass.getName ();
		boolean theIsMauiPackage;
		do
		{
			theIsMauiPackage = (theBaseName.startsWith ("com.bitmovers.maui.components") ||
								theBaseName.startsWith ("com.bitmovers.maui.layouts") ||
								theCheckClass == MauiApplication.class);
			theBaseName = theBaseName.substring (theBaseName.lastIndexOf (".") + 1);
			
			for (int i = aClientClassification.length; i >= 0 && retVal == null; i--)
			{
				if (i == 0 || aClientClassification [i - 1] != null)
				{
					String theCheckName;
					if (i == aClientClassification.length)
					{
						String theFullVersion = aClientClassification [i - 1];
						String theSubVersion = new String (theFullVersion);
						int theUnderScore = theSubVersion.length ();
						do
						{
							theSubVersion = theSubVersion.substring (0, theUnderScore);
							aClientClassification [i - 1] = theSubVersion;
							retVal = testFileName (aRenderable, aComponent, theBaseName, aClientClassification, i);
						}
						while (retVal == null && (theUnderScore = theSubVersion.lastIndexOf ("_")) != -1);
					}
					else
					{
						retVal = testFileName (aRenderable, aComponent, theBaseName, aClientClassification, i);
					}	
				}
			}
			if (!theIsMauiPackage && retVal == null)
			{
				theCheckClass = theCheckClass.getSuperclass ();
				theBaseName = theCheckClass.getName ();
			}
		}
		while (retVal == null && !theIsMauiPackage);
		//
		//	If no renderer was found, then just use the default renderer for the protocol
		//
		if (retVal == null)
		{
			if (aRenderable instanceof MLayout)
			{
				retVal = (aClientClassification [0].equals ("html") ?
						(I_Renderer) new DefaultHtmlLayoutRenderer (aRenderable, (MContainer) aComponent, aClientClassification) :
						(I_Renderer) new DefaultWmlLayoutRenderer (aRenderable, (MContainer) aComponent, aClientClassification));
			}
			else
			{
				retVal = (aClientClassification [0].equals ("html") ?
						(I_Renderer) new DefaultHtmlRenderer (aRenderable, aClientClassification) :
						(I_Renderer) new DefaultWmlRenderer (aRenderable, aClientClassification));
			}
		}
		
		if (retVal != null)
		{
			if (retVal instanceof A_Renderer)
			{
				((A_Renderer) retVal).setHtml (aClientClassification [0].equals ("html"));
			}
			
			//
			//	Notify the session that a renderer has been created
			//
			MComponent theRootParent = aComponent.getRootParent ();
			if (theRootParent instanceof MauiApplication)
			{
				HTTPSession theSession = ((MauiApplication) theRootParent).getSession ();
				if (theSession != null)
				{
					theSession.rendererCreated (new RendererEvent (this, retVal, aComponent, aClientClassification));
				}
			}
		}
		return retVal;
	}
	
	/**
	* Get an I_Renderer object
	*
	* @param aRenderable The I_Renderable object
	* @param aComponent A component in the MauiApplication hierarchy
	*
	* @return The I_Render object
	*/
	public I_Renderer getRenderer (I_Renderable aRenderable, MComponent aComponent)
	{
		I_Renderer retVal = null;
		
		//
		//	Get the header values and determine a client "classification" to use
		//	for trying to locate a renderer.
		//
		MComponent theRoot = aComponent.getRootParent ();
		if (theRoot instanceof MauiApplication)
		{
			String [] theClientClassification = getClientClassification ((MauiApplication) theRoot);
			//
			//	With this classification array, try to locate the appropriate renderer.
			//
			retVal = locateRenderer (aRenderable, theClientClassification, aComponent);
		}
		else
		{
			System.err.println (new ErrorString ("[CompositionManager] - Root pane for " + aComponent.getName () +
																					 " isn't a MauiApplication.  Cannot determine renderer."));
		}
		return (retVal != null ? retVal : new StubRenderer ());
	}
		
	
	// ---------------------------------------------------------------------------

}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof