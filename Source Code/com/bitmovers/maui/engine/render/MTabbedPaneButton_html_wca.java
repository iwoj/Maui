package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import com.bitmovers.maui.components.foundation.MTabbedPaneButton;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.maui.MauiApplication;


// ========================================================================
// CLASS: MTabbedPaneButton_html_wca             (c) 2001 Bitmovers Systems
// ========================================================================

public class MTabbedPaneButton_html_wca extends A_Renderer
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	public String render(I_Renderable aRenderable)
	{
		MTabbedPaneButton button = (MTabbedPaneButton)aRenderable;
		StringBuffer returnValue = new StringBuffer();
		
		if (button.isSelected() && button.isEnabled())
		{
			// Selected
			returnValue.append("<!-- MTabbedPaneButton: Selected,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (start) -->\n");
			
			returnValue.append("<b>&gt; ");
			returnValue.append(HTMLCompositor.encodeHTML(button.getLabel()));
			returnValue.append("</b>\n");
			
			returnValue.append("<!-- MTabbedPaneButton: Selected,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (end) -->\n");
		}
		else if (button.isEnabled())
		{
			// Selected
			returnValue.append("<!-- MTabbedPaneButton: ");
			returnValue.append(button.getComponentID());
			returnValue.append(" (start) -->\n");
			
			returnValue.append("&gt; <input type=\"submit\" name=\"");
			returnValue.append(button.getComponentID());
			returnValue.append("\" value=\"");
			returnValue.append(HTMLCompositor.encodeHTML(button.getLabel()));
			returnValue.append("\">\n");
			
			returnValue.append("<!-- MTabbedPaneButton: ");
			returnValue.append(button.getComponentID());
			returnValue.append(" (end) -->\n");
		}
		else
		{
			// Disabled
			returnValue.append("<!-- MTabbedPaneButton: Disabled,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (start) -->\n");
			
			returnValue.append("&gt; <font color=\"#C0C0C0\">(");
			returnValue.append(HTMLCompositor.encodeHTML(button.getLabel()));
			returnValue.append(")</font>");
			
			returnValue.append("<!-- MTabbedPaneButton: Disabled,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (end) -->\n");
		}

		return returnValue.toString();
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF