// =============================================================================
// CHANGELOG:
//++ 335 MW 2001.08.08
// Made the use of the truncatedStringLength value more prominant in the code.
// Rather than having a static occurance of the truncated length, we now can use
// the value specified by our maui.application.string.truncation property
// properly.
// =============================================================================




// =============================================================================
// com.bitmovers.maui.engine.render.MLabel_wml
// =============================================================================


package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import java.util.Stack;
import java.awt.Color;
import java.awt.Dimension;

import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.foundation.MLabel;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.engine.ServerConfigurationManager;
import com.bitmovers.maui.engine.httpserver.HTTPSession;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.engine.logmanager.InfoString;
import com.bitmovers.utilities.StringParser;


// =============================================================================
// CLASS: MLabel_wml
// =============================================================================

/** Generic WML renderer for MLabel component.
  *
  */

public class MLabel_wml extends A_Renderer
	implements I_HasDepth,
			   I_RendererInitialize,
			   MActionListener
{
	 Dimension dimension;
	 protected boolean clicked = false;
	 protected boolean inDepthBasedRender = false;
	 protected boolean linking = false;
	 //protected int stringLength = 0;
	 //++ 335 MW 2001.08.07
	 protected int truncatedStringLength = 30; // default length of 30
	 //--

	// ---------------------------------------------------------------------------	
	
	/** Initialize the I_Renderer object
	  *
	  * @param aRenderable An object which can be rendered (MComponent or Layout)
	  * @param aComponent The reference component (required for Layout objects)
	  * @param aClientClassification The client classification string array
	  *
	  */
	  
	public void initialize (I_Renderable aRenderable,
							MComponent aComponent,
							String [] aClientClassification)
	{
		dimension = ((MauiApplication) aComponent.getRootParent ()).
															getSession ().
															getClientDimension ();

		MauiApplication theApplication = (MauiApplication) ((MComponent) aRenderable).getRootParent ();
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance();
		String truncatedStringLengthString = theApplication.getProperty(theSCM.MAUI_APPLICATION_STRING_TRUNCATION);
		if (truncatedStringLengthString == null)
		{
			truncatedStringLengthString = theSCM.getProperty(theSCM.MAUI_APPLICATION_STRING_TRUNCATION);
		}
		
		try
		{
			truncatedStringLength = Integer.parseInt(truncatedStringLengthString);
		}
		catch (NumberFormatException e)
		{
			System.out.println(new InfoString("[MLabel_wml] defaulting to string length of 30."));
			truncatedStringLength = 30;
		}

	}
	
	/** Setup for redirection, and render
	  *
	  * @param aRenderable The MLabel being rendered
	  */
	private String redirect (I_Renderable aRenderable)
	{
		MLabel theLabel = (MLabel) aRenderable;
		
		if (theLabel.getLink () != null)
		{
			linking = true;
			//
			//	Setup for redirection
			//
			MauiApplication theApplication = (MauiApplication) theLabel.getRootParent ();
			theApplication.setURLString (theLabel.getLink ());
		}
		
		//
		//	And do the render, in case the "back" button is used
		//
		return renderLabel (aRenderable);
	}
	
	private String renderLabel (I_Renderable aRenderable)
	{
		String retVal;
		if (clicked)
		{
			retVal = render (aRenderable);
		}
		else
		{
			MLabel theLabel = (MLabel) aRenderable;
			//String theText = WMLCompositor.encodeWML (theLabel.getText ());
			String theLink = theLabel.getLink ();
			//++ 335 MW 2001.08.07
			// modified this statement to account for situations where a Label has no external url, but just an empty link. For example, a functional link.
			retVal = (theLink != null ?
							generateSimpleAnchor (aRenderable, theLink) :
							WMLCompositor.encodeWML (theLabel.getText ()));
			//-- 335
		}
		return retVal;
	}
		
	
	// ---------------------------------------------------------------------------
	// METHOD: render
	// ---------------------------------------------------------------------------
	
	/**
	* Render the MLabel.  The size of the text in the MLabel is compared to
	* the available screen space.  If the text is too long then it an anchor is created
	* to view all of the details.
	*
	* @param aRenderable The MLabel
	*
	* @return The WML code
	*/
	public String render (I_Renderable aRenderable)
	{
		String retVal = null;
		renderable = aRenderable;
		if (clicked)
		{
			clicked = false;
			redirect (aRenderable);
			retVal = render (aRenderable);
		}
		else
		{
			MLabel theLabel = (MLabel) aRenderable;
			String theLink = theLabel.getLink ();
			if (theLink != null && theLink.indexOf ("://") != -1)
			{
				theLabel.addActionListener (this);
			}
			else
			{
				theLabel.removeActionListener (this);
			}
		
			String theText = theLabel.getText ();
			/*if (stringLength == 0)
			{
				//++ 335 MW 2001.08.07
				// made a global reference to the ServerConfigurationManager, so the MAUI_APPLICATION_STRING_TRUNCATION
				// string variable could be accessed in more than one place.
				ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
				String theStringLength = "" + truncatedStringLength;
				//-- 335
				if (theStringLength != null ||
					((theStringLength = theSCM.getProperty (theSCM.MAUI_APPLICATION_STRING_TRUNCATION)) != null))
				{
					try
					{
						//stringLength = Integer.parseInt (theStringLength);
						stringLength = truncatedStringLength;
					}
					catch (NumberFormatException e)
					{
						System.err.println ("[MLabel_wml] defaulting to string length of 30 because: " + e);
						stringLength = 30;
					}
				}
				else
				{
					stringLength = 30;
				}
			}*/

			//++ 335 MW 2001.08.13
			retVal = (//stringLength != -1 &&
					  theText.length () > truncatedStringLength ?
											generateSimpleAnchor (aRenderable, MActionEvent.ACTION_PUSH) :
											renderLabel (aRenderable));
			//-- 335
											
		}
		return retVal;
	}
	
	/**
	* This will be called as part of generating a simple anchor
	*
	* @param aRenderable The MLabel
	*
	* @return The "short" version of the label
	*/
	protected String getLabel (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer (((MLabel) aRenderable).getText ());
		//++ 335 MW 2001.08.07
		// used the global truncatedStringLength variable
		if (!inDepthBasedRender && retVal.length () > truncatedStringLength)
		{
			retVal.setLength (truncatedStringLength);
			int theSpace = retVal.toString ().lastIndexOf (" ");
			if (theSpace != -1)
			{
				retVal.setLength (theSpace + 1);
			}
			retVal.append ("...");
		}
		
		// This no longer encodesHTML, it simply return the text of the label.
		return retVal.toString ();
		//-- 335
	}
		
	
	/**
	* Notify the component to do some "drill down"
	*
	* @param aRenderable The renderable MComponent
	* @param aStack The Stack which represents the current navigation depth
	* @param aBackout A command to be included as part of the backout
	*
	* @return The rendered string
	*/
	public String depthBasedRender (I_Renderable aRenderable,
							 	   	Stack aStack,
							 	   	String aBackout)
	{
		inDepthBasedRender = true;
		return renderLabel (aRenderable);
	}
							 	   	
	/**
	* Notify the renderer that it is being backed out.  This is so it can do
	* whatever cleanup is necessary
	*
	* @param aRenderable The I_Renderable object
	*/
	public void backout (I_Renderable aRenderable)
	{
		inDepthBasedRender = false;
		linking = false;
	}
	
	// ----------------------------------------------------------------------
	// METHOD: autoPop
	// ----------------------------------------------------------------------
	
	public boolean autoPop(MActionEvent aActionEvent, Stack aStack)
	{
		String theLink = ((MLabel) renderable).getLink ();
		return (theLink == null ? false : theLink.equals (aActionEvent.getActionCommand ()));
	}
		
	/**
	* Test if the event indicates that deep navigation is occuring or not.
	*
	* @param aActionEvent The MActionEvent describing the component's event
	*
	* @return Boolean indicating if this is deep navigation or not
	*/
	public boolean isDeepNavigating (MActionEvent aEvent, Stack aStack)
	{
		return (aEvent.getActionCommand ().equals (MActionEvent.ACTION_PUSH));
	}
	
	/**
	* Get the representative renderer for this renderer (which may not be the same
	* as the actual renderer
	*
	* @return The representative renderer
	*/
	public I_Renderable getRepresentativeRenderable (I_Renderable aRenderable)
	{
		return (((MLabel) aRenderable).getLink () == null ? null : aRenderable);
	}
	
	/**
	* Event listener for MLabel "click".  This means that their is an URL, and that the MLabel has been clicked
	*
	* @param aEvent The MActionEvent describing the event
	*/
	public void actionPerformed (MActionEvent aEvent)
	{
		String theActionCommand = aEvent.getActionCommand ();
		if (!linking && theActionCommand != null && theActionCommand.indexOf ("://") != -1)
		{
			clicked = true;
		}
	}
	
	// ---------------------------------------------------------------------------
}