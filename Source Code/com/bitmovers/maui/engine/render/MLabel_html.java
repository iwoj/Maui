// ========================================================================
// com.bitmovers.maui.engine.render.MLabel_html
// ========================================================================

package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import java.awt.Color;
import java.net.URL;

import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;


// ========================================================================
// CLASS: MLabel_html                            (c) 2001 Bitmovers Systems
// ========================================================================

/** Generic HTML renderer for the <code>MLabel</code> component.
  * 
  */

public class MLabel_html extends A_Renderer
                      implements MActionListener
{
	
	
	// --------------------------------------------------------------------
	// METHOD: render
	// --------------------------------------------------------------------
	
	/** Perform an HTML rendering of the MLabel, or redirect to an URL if the MLabel was clicked, and it is actually an
	  * an external reference.  
	  *
	  * @param aRenderable The MLabel being rendered
	  * 
	  */
	  
	public String render(I_Renderable aRenderable)
	{
		return renderLabel (aRenderable);
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: getAnchoredText
	// --------------------------------------------------------------------
	
	protected String getAnchoredText(I_Renderable aRenderable)
	{
		MLabel theLabel = (MLabel) aRenderable;
		StringBuffer retVal = null;
		String theText = theLabel.getText ();
		
		if (theLabel.getLink () != null)
		{
			theLabel.addActionListener (this);
			int theStartingOffset = -1;
			int theEndingOffset = -1;
			String theSubText = theLabel.getSubText ();
			if (theSubText != null)
			{
				theStartingOffset = theText.indexOf (theSubText);
				theEndingOffset = theStartingOffset + theSubText.length ();
			}
			String thePrefix = (theStartingOffset == -1 ? "" : theText.substring (0, theStartingOffset));
			String theAnchor = (theStartingOffset == -1 ? theText : theSubText);
			String theSuffix = (theStartingOffset == -1 ? "" : theText.substring (theEndingOffset));
			
			retVal = new StringBuffer (HTMLCompositor.encodeHTML (thePrefix));
			
			retVal.append ("<a href=\"javascript:doSubmit ()\" onClick=\"registerEvent (\'");
			retVal.append (theLabel.getComponentID ());
			retVal.append ("\')\">");
			retVal.append (HTMLCompositor.encodeHTML(theAnchor));
			retVal.append ("</a>");
			retVal.append (HTMLCompositor.encodeHTML (theSuffix));
		}
		else
		{
			theLabel.removeActionListener (this);
		}
		
		return (retVal == null ? HTMLCompositor.encodeHTML(theText) : retVal.toString ());
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: renderLabel
	// --------------------------------------------------------------------
	
	/** Render the label.  If it has an URL associated with it, then setup a link for it.
	  * 
	  * @param aRenderable The I_Renderable object
	  * 
	  */
	  
	protected String renderLabel(I_Renderable aRenderable)
	{
		MLabel label = (MLabel) aRenderable;
		
		StringBuffer renderedHTML = new StringBuffer();
		
		renderedHTML.append("<font color=\"#");
		renderedHTML.append(HTMLCompositor.colorToRGBHexString(label.getColor()));
		renderedHTML.append("\">");
		
		if (label.isBold ())
		{
			renderedHTML.append ("<b>");
		}
		
		if (label.isItalic ())
		{
			renderedHTML.append ("<i>");
		}
		
		if (label.isStrikethrough ())
		{
			renderedHTML.append ("<s>");
		}
		
		if (label.isUnderline ())
		{
			renderedHTML.append ("<u>");
		}
		
		renderedHTML.append(getAnchoredText (aRenderable));
		
		if (label.isUnderline ())
		{
			renderedHTML.append ("</u>");
		}
		
		if (label.isStrikethrough ())
		{
			renderedHTML.append ("</s>");
		}
		
		if (label.isItalic ())
		{
			renderedHTML.append ("</i>");
		}
		
		if (label.isBold ())
		{
			renderedHTML.append ("</b>");
		}
		
		if (label.getLink () != null)
		{
			renderedHTML.append("</a>");
		}
		
		renderedHTML.append("</font>");
		
		return renderedHTML.toString();
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: actionPerformed
	// --------------------------------------------------------------------
	
	/** Event listener for MLabel "click".  This means that their is an URL, and that the MLabel has been clicked
	  *
	  * @param aEvent The MActionEvent describing the event
	  * 
	  */
	  
	public void actionPerformed(MActionEvent aEvent)
	{
		MLabel theLabel = (MLabel) aEvent.getSource ();
		//
		//	If this is an external URL, then setup redirection to the link.  Since this should take precedence, we must
		//	guarantee that this occurs, rather than any other of kind of redirection (like application chaining).
		//
		
		String theURL = theLabel.getLink ();
		if (theURL != null &&
			(theURL.indexOf ("://") != -1 || theURL.indexOf ("mailto:") != -1))
		{
			//
			//	Setup for redirection
			//
			MauiApplication theApplication = (MauiApplication) theLabel.getRootParent ();
			theApplication.setURLString (theLabel.getLink ());
		}
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF