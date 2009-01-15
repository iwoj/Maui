package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import com.bitmovers.maui.components.foundation.MButton;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.maui.MauiApplication;


// ========================================================================
// CLASS: MButton_html_wca                       (c) 2001 Bitmovers Systems
// ========================================================================

public class MButton_html_wca extends MButton_html
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: doRender
	// ----------------------------------------------------------------------
	
	protected String doRender(I_Renderable aRenderable)
	{
		super.doRender(aRenderable);
		MButton button = (MButton)aRenderable;
		StringBuffer returnValue = new StringBuffer();
		
		if (button.isEnabled())
		{
			// Enabled
			returnValue.append("<!-- MButton: ");
			returnValue.append(button.getComponentID());
			returnValue.append(" (start) -->\n");
			
			returnValue.append("<input type=\"submit\" name=\"");
			returnValue.append(button.getComponentID());
			returnValue.append("\" value=\"");
			returnValue.append(HTMLCompositor.encodeHTML(button.getLabel()));
			returnValue.append("\">\n");
			
			returnValue.append("<!-- MButton: ");
			returnValue.append(button.getComponentID());
			returnValue.append(" (end) -->\n");
		}
		else
		{
			// Disabled
			returnValue.append("<!-- MButton: Disabled,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (start) -->\n");
			
			returnValue.append("<font color=\"#C0C0C0\">(");
			returnValue.append(HTMLCompositor.encodeHTML(button.getLabel()));
			returnValue.append(")</font>");
			
			returnValue.append("<!-- MButton: Disabled,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (end) -->\n");
		}

		return returnValue.toString();
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF