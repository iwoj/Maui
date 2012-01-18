// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.foundation.MFrame;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.StringParser;


// =============================================================================
// CLASS: DefaultWmlLayoutRenderer
// =============================================================================

/** DefaultWmlLayoutRenderer is the default renderer to use for all WML Layout
  * Managers.<p>
  * 
  * NOTE: This class (and LayoutManager in general) are not currently used for 
  * WML rendering.
  * 
  */

public class DefaultWmlLayoutRenderer extends A_Layout
{
	// ---------------------------------------------------------------------------
	public static final String SEPARATOR = "<br/>\n";


	// ---------------------------------------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------------------------------------

	public DefaultWmlLayoutRenderer(I_Renderable aRenderable, MContainer aParent, String[] aClientClassification)
	{
		super(aRenderable, aParent, aClientClassification, false);
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: initialize
	// ---------------------------------------------------------------------------

	public void initialize(I_Renderable aRenderable, MContainer aParent, String[] aClientClassification)
	{
		super.html = false;
		super.initialize(aRenderable, aParent, aClientClassification);
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: setupAlignment
	// ---------------------------------------------------------------------------

	public void setupAlignment(I_Renderable aRenderable, StringParser aStringParser)
	{
		super.separator = this.SEPARATOR;
	}


	// ----------------------------------------------------------------------
	// METHOD: generateDirectionalPrologue
	// ----------------------------------------------------------------------
	
	protected StringBuffer generateDirectionalPrologue(MComponent [] aComponents, boolean aForward)
	{
		StringBuffer retVal = new StringBuffer();
		I_Renderer theRenderer = null;
		boolean theHasOnTag = false;
		
		for (int i = 0; i < aComponents.length; i++)
		{
			theRenderer = aComponents [i].getRenderer();
			if (theRenderer != null &&
				(aForward ? theRenderer instanceof I_HasForwardPrologue :
							theRenderer instanceof I_HasBackwardPrologue) &&
				theRenderer != this)
			{
				if (!theHasOnTag)
				{
					retVal.append("<onevent type=");
					retVal.append((aForward ? "\"onenterforward\">\n<refresh>\n" :
											   "\"onenterbackward\">\n<refresh>\n"));
					theHasOnTag = true;
				}
				
				retVal.append((aForward ?
									((I_HasForwardPrologue) theRenderer).generateForwardPrologue((I_Renderable) aComponents [i]) :
									((I_HasBackwardPrologue) theRenderer).generateBackwardPrologue((I_Renderable) aComponents [i])));
			}
		}
		
		if (theHasOnTag)
		{
			retVal.append("</refresh>\n</onevent>\n");
		}
		return retVal;
	}
		

	// ---------------------------------------------------------------------------
	// METHOD: generatePrologue
	// ---------------------------------------------------------------------------

	public String generatePrologue(I_Renderable aRenderable)
	{
		StringBuffer retVal = null;//"<card><p>");		// This will always be here
		boolean theHasOnEvent = false;
		
		MComponent [] theComponents = parent.getComponents();
		
		retVal = generateDirectionalPrologue(theComponents, true);
		retVal.append(generateDirectionalPrologue(theComponents, false));
		/*I_Renderer theRenderer = null;
		for (int i = 0; i < theComponents.length; i++)
		{
			theRenderer = theComponents [i].getRenderer();
			if (theRenderer != null &&
				theRenderer instanceof I_HasPrologue &&
				theRenderer != this)
			{
				if (!theHasOnEvent)
				{
					//
					//	Create an "onEvent" tag
					//
					retVal.append("<onevent type=\"onenterbackward\">\n<refresh>\n");
					theHasOnEvent = true;
				}
				
				retVal.append(((I_HasPrologue) theRenderer).
									generatePrologue((I_Renderable) theComponents [i]));
			}					
		}
		
		if (theHasOnEvent)
		{
			retVal.append("</refresh>\n</onevent>\n");
		}*/
		retVal.append("<p>");
		return retVal.toString();
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: generateContent
	// ---------------------------------------------------------------------------

	public String generateContent(I_Renderable aRenderable)
	{
		return renderComponents(aRenderable);
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: generateEpilogue
	// ---------------------------------------------------------------------------

	public String generateEpilogue(I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer("</p>\n</card>");
		
		//
		//	Search for components which have epilogues
		//
		MComponent [] theComponents = parent.getComponents();
		I_Renderer theRenderer = null;
		for (int i = 0; i < theComponents.length; i++)
		{
			theRenderer = theComponents [i].getRenderer();
			if (theRenderer != null &&
				theRenderer instanceof I_HasEpilogue &&
				theRenderer != this)
			{
				retVal.append(((I_HasEpilogue) theRenderer).
									generateEpilogue((I_Renderable) theComponents [i]));
			}
		}
		return retVal.toString();
	}


	// ---------------------------------------------------------------------------
	// METHOD: generate
	// ---------------------------------------------------------------------------

	protected void generate(I_Renderable aRenderable, StringParser aParser)
	{
		super.generate(aRenderable, aParser);
		//
		//	Get the title of the containing Frame
		//
		MComponent theComponent = parent;

		while(theComponent != null &&
			   !(theComponent instanceof MFrame))
		{
			theComponent = theComponent.getParent();
		}

		if (theComponent instanceof MFrame)
		{
			aParser.setVariable("title", "<b>" +
								((MFrame) theComponent).getTitle() + "</b><br/>");
		}
	}


	// ---------------------------------------------------------------------------
}

// =============================================================================
//                                                                           EOF