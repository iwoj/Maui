package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.components.foundation.MTabbedPane;

// ========================================================================
// CLASS: MTabbedPane_html_wca
// ========================================================================

/** MTabbedPane renderer for Palm Web Clipping clients. Currently makes use
  * of the MTabbedPane.wca.html template.
  * 
  */

public class MTabbedPane_html_wca extends A_Renderer
                               implements I_RendererInitialize
{
	public MTabbedPane_html_wca()
	{
		super();
	}
	
	protected void doRender (StringParser aParser, I_Renderable aRenderable)
	{
		MTabbedPane theTabbedPane = (MTabbedPane)aRenderable;
		StringBuffer theTabs = new StringBuffer();
		
		for (int i = 0; i < theTabbedPane.getComponentCount(); i++)
		{
			theTabs.append(doRenderComponent(theTabbedPane.getTabButton(i)));
			theTabs.append("<br>");
		}
		
		aParser.setVariable ("tabs", theTabs.toString ());
		aParser.setVariable ("focusedPane", doRenderComponent (theTabbedPane.getSelectedComponent ()));
	}
	
}
