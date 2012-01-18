// ========================================================================
// com.bitmovers.maui.engine.render.MExpandPane_html_wca
// ========================================================================

package com.bitmovers.maui.engine.render;

import java.util.Stack;
import java.util.Vector;
import java.util.Enumeration;

import com.bitmovers.maui.components.foundation.MExpandPane;
import com.bitmovers.maui.components.foundation.MButton;
import com.bitmovers.maui.components.MComponent;

// ========================================================================
// CLASS: MExpandPane_html_wca
// ========================================================================

public class MExpandPane_html_wca extends A_Renderer
                               implements I_RendererInitialize
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	public String render(I_Renderable aRenderable)
	{
		StringBuffer returnValue = new StringBuffer();
		
		MExpandPane expandPane = (MExpandPane)aRenderable;
		
		appendStartComment(returnValue);
		
		returnValue.append(expandPane.getButton().render());
		returnValue.append("\n");
		
		if (expandPane.isOpen())
		{
			returnValue.append("<br>\n");
			returnValue.append(expandPane.getLayout().render());
			returnValue.append("\n");
		}
		
		appendEndComment(returnValue);
		
		return returnValue.toString();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: appendStartComment
	// ----------------------------------------------------------------------
	
	protected void appendStartComment(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("<!-- MExpandPane ");
		aStringBuffer.append(((MComponent)renderable).getComponentID());
		aStringBuffer.append(" (start) -->\n");
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: appendEndComment
	// ----------------------------------------------------------------------
	
	protected void appendEndComment(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("<!-- MExpandPane ");
		aStringBuffer.append(((MComponent)renderable).getComponentID());
		aStringBuffer.append(" (end) -->\n");
	}
	
	
}