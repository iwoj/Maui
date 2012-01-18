// ========================================================================
// com.bitmovers.maui.engine.render.MLabel_html_wca
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
// CLASS: MLabel_html_wca
// ========================================================================

/** Palm Web Clipping HTML renderer for <code>MLabel</code> components.
  * 
  */

public class MLabel_html_wca extends MLabel_html
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: getAnchoredText
	// ----------------------------------------------------------------------

	protected String getAnchoredText(I_Renderable aRenderable)
	{
		MLabel theLabel = (MLabel)aRenderable;
		StringBuffer returnValue = null;
		String theText = theLabel.getText();
		String theComponentID = theLabel.getComponentID();
		
		if (theLabel.getLink() != null)
		{
			theLabel.addActionListener (this);
			
			int theStartingOffset = -1;
			int theEndingOffset = -1;
			String theSubText = theLabel.getSubText();
			
			if (theSubText != null)
			{
				theStartingOffset = theText.indexOf(theSubText);
				theEndingOffset = theStartingOffset + theSubText.length();
			}
			
			String thePrefix = (theStartingOffset == -1 ? "" : theText.substring(0, theStartingOffset));
			String theLinkedText = (theStartingOffset == -1 ? theText : theSubText);
			String theSuffix = (theStartingOffset == -1 ? "" : theText.substring(theEndingOffset));
			
			returnValue = new StringBuffer(HTMLCompositor.encodeHTML(thePrefix));
			
			returnValue.append("<input type=\"submit\" name=\"");
			returnValue.append(theComponentID);
			returnValue.append("\" value=\"");
			returnValue.append(HTMLCompositor.encodeHTML(theLinkedText));
			returnValue.append("\">\n");
			
			returnValue.append(HTMLCompositor.encodeHTML(theSuffix));
		}
		else
		{
			theLabel.removeActionListener(this);
		}
		
		return (returnValue == null ? HTMLCompositor.encodeHTML(theText) : returnValue.toString());
	}
	
	
}