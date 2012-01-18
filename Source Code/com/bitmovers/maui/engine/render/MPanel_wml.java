// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import com.bitmovers.maui.components.foundation.MContainer;

public class MPanel_wml extends A_Renderer
{
	public String render (I_Renderable aRenderable)
	{
		return renderComponents ((MContainer) aRenderable, "<br/><!-- MPanel_wml -->\n");
	}
}