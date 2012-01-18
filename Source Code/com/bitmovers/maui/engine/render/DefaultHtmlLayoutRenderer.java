// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.StringParser;

public class DefaultHtmlLayoutRenderer extends A_Layout
{
	protected DefaultHtmlLayoutRenderer ()
	{
		super ();
	}
	
	public DefaultHtmlLayoutRenderer (I_Renderable aRenderable,
									  MContainer aParent,
									  String [] aClientClassification)
	{
		super (aRenderable, aParent, aClientClassification, true);
	}
	
	public void initialize (I_Renderable aRenderable,
							MComponent aParent,
							String [] aClientClassification)
	{
		html = true;
		super.initialize (aRenderable, aParent, aClientClassification);
	}
	
	public void setupAlignment (I_Renderable aRenderable,
								StringParser aStringParser)
	{
		MLayout theLayout = (MLayout) aRenderable;

		aStringParser.setVariable("align", theLayout.getAlignment().toString());
	}
}