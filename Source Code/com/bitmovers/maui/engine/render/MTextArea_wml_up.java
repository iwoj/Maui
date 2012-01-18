// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.components.MComponent;

public class MTextArea_wml_up extends A_Renderer
	implements I_HasForwardPrologue,
			   I_RendererInitialize
			   //I_HasEpilogue
{
	/*public String render (I_Renderable aRenderable)
	{
		MComponent theSelectList = (MComponent) aRenderable;
		
		return "<a href=\"#" + generateComponentID (theSelectList) + "_Card\"> $(" +
							   generateComponentID (theSelectList) + ") </a>";
	}*/
	
	public String generateEpilogue (I_Renderable aRenderable)
	{
		aRenderable.fillParserValues();
		StringParser theParser = aRenderable.getParser();
		return generateCard (generateComponentID ((MComponent) aRenderable),
							 "TextArea",
							 theParser.parseString (getRenderTemplate (aRenderable)));
		
	}
}