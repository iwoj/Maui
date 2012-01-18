// =============================================================================
// com.bitmovers.maui.engine.render.MTextField_wml
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import com.bitmovers.maui.components.foundation.MTabbedPaneButton;

public class MTabbedPaneButton_html extends MButton_html
{
	// ---------------------------------------------------------------------------
	// METHOD: getTemplateTypes
	// ---------------------------------------------------------------------------

	protected String [] getTemplateTypes()
	{
		return new String [] {"", "off", "selected"};
	}


	// ---------------------------------------------------------------------------
	// METHOD: getRenderTemplate
	// ---------------------------------------------------------------------------
	
	protected String getRenderTemplate(I_Renderable aRenderable)
	{
		MTabbedPaneButton theButton = (MTabbedPaneButton)aRenderable;
		return renderTemplate [theButton.isSelected () ? 2 :
									theButton.isEnabled () ? 0 : 1];
	}
	
}