package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import com.bitmovers.maui.components.foundation.MExpandPaneButton;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.maui.MauiApplication;


// ========================================================================
// CLASS: MExpandPaneButton_html_wca             (c) 2001 Bitmovers Systems
// ========================================================================

public class MExpandPaneButton_html_wca extends A_Renderer
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	public String render(I_Renderable aRenderable)
	{
		MExpandPaneButton button = (MExpandPaneButton)aRenderable;
		StringBuffer returnValue = new StringBuffer();
		
		if (button.isOpen())
		{
			// Open
			returnValue.append("<!-- MExpandPaneButton: Open,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (start) -->\n");
			
			returnValue.append("- <input type=\"submit\" name=\"");
			returnValue.append(button.getComponentID());
			returnValue.append("\" value=\"");
			returnValue.append(HTMLCompositor.encodeHTML(button.getLabel()));
			returnValue.append("\">\n");
			
			returnValue.append("<!-- MExpandPaneButton: Open,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (end) -->\n");
		}
		else
		{
			// Closed
			returnValue.append("<!-- MExpandPaneButton: Closed,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (start) -->\n");
			
			returnValue.append("+ <input type=\"submit\" name=\"");
			returnValue.append(button.getComponentID());
			returnValue.append("\" value=\"");
			returnValue.append(HTMLCompositor.encodeHTML(button.getLabel()));
			returnValue.append("\">\n");
			
			returnValue.append("<!-- MExpandPaneButton: Closed,");
			returnValue.append(button.getComponentID());
			returnValue.append(" (end) -->\n");
		}

		return returnValue.toString();
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF