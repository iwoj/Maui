// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.components.foundation.MTabbedPane;

public class MTabbedPane_html extends A_Renderer
	implements I_RendererInitialize
{
	public MTabbedPane_html ()
	{
		super ();
	}
	
	protected void doRender (StringParser aParser, I_Renderable aRenderable)
	{
		MTabbedPane theTabbedPane = (MTabbedPane) aRenderable;
		StringBuffer theTabs = new StringBuffer ();
		
		for (int i = 0; i < theTabbedPane.getComponentCount (); i++)
		{
			theTabs.append ("<td rowspan=\"3\" height=\"22\">");
			theTabs.append (doRenderComponent (theTabbedPane.getTabButton (i)));
			theTabs.append ("</td>\n");
		}
		
		aParser.setVariable ("tabs", theTabs.toString ());
		aParser.setVariable ("focusedPane", doRenderComponent (theTabbedPane.getSelectedComponent ()));
	}
}
