// =============================================================================
// CLASS: com.bitmovers.maui.engine.render.MMenuBar_html
// =============================================================================

package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.utilities.StringParser;

/**
* MMenuBar_wml <p>
* This is a generic (and quick and dirty) wml implementation of the menu components
*/
public class MMenuBar_wml extends A_Renderer
	//implements I_HasEpilogue
{
	protected StringBuffer generateSelectHeader (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ("<select title=\"menu\" name=\"");
		retVal.append (generateComponentID ((MComponent) aRenderable));
		retVal.append ("\">\n");
		return retVal;
	}
	
	protected StringBuffer generateSelectFooter (I_Renderable aRenderable)
	{
		return new StringBuffer ("</select>\n");
	}
	
	public String render (I_Renderable aRenderable)
	{
		/*StringBuffer retVal =
			new StringBuffer ("<template><do type=\"options\" label=\"Menu\">\n");
		retVal.append ("<go href=\"#MenuCard\"/>\n");
		retVal.append ("</do></template>\n");
		return retVal.toString ();*/
		return "";
	}
		
	
	public String generateEpilogue (I_Renderable aRenderable)
	{
		StringBuffer retVal = generateSelectHeader (aRenderable);
		retVal.append (doRender (aRenderable));
		retVal.append (generateSelectFooter (aRenderable));
		return generateCard ("MenuCard", "MenuCard", retVal.toString ());
	}
	
	protected String doRender (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ();
		MContainer theContainer = (MContainer) aRenderable;
		MComponent [] theComponents = theContainer.getComponents ();
		for (int i = 0; i < theComponents.length; i++)
		{
			if (theComponents [i] instanceof MMenu)
			{
				retVal.append (generateOptgroup (theComponents [i]));
			}
			else
			{
				retVal.append (generateOption (theComponents [i]));
			}
		}
		return retVal.toString ();
	}
	
	protected String generateOptgroupHeader (MComponent aComponent)
	{
		MMenu theMenu = (MMenu) aComponent;
		StringBuffer retVal = new StringBuffer ("<optgroup title=\"");
		retVal.append (theMenu.getLabel ());
		retVal.append ("\">\n");
		return retVal.toString ();
	}
	
	protected String generateOptgroupFooter (MComponent aComponent)
	{
		return "</optgroup>\n";
	} 
	
	protected StringBuffer generateOptgroup (MComponent aComponent)
	{
		//
		//	Generation another menu hierarchy
		//
		StringBuffer retVal = new StringBuffer (generateOptgroupHeader (aComponent));
		retVal.append (doRender ((I_Renderable) aComponent));
		retVal.append (generateOptgroupFooter (aComponent));
		return retVal;
	}
	
	protected StringBuffer generateOptionHeader (MComponent aComponent)
	{
		MMenuItem theItem = (MMenuItem) aComponent;
		StringBuffer retVal = new StringBuffer ("<option value=\"");
		retVal.append (generateComponentID (theItem.getButton ()));
		retVal.append ("\">\n");
		return retVal;
	}
	
	protected String generateOptionFooter (MComponent aComponent)
	{
		return "</option>\n";
	}
	
	protected StringBuffer generateOptionDetail (MComponent aComponent)
	{
		MMenuItem theMenu = (MMenuItem) aComponent;
		StringBuffer retVal = new StringBuffer (theMenu.getLabel ());
		retVal.append ("<onevent type=\"onpick\">\n");
		retVal.append (generatePostList (theMenu.getButton (), "picked"));
		retVal.append ("</onevent>");
		return retVal;
	}
	
	protected String generateOption (MComponent aComponent)
	{
		StringBuffer retVal = generateOptionHeader (aComponent);
		retVal.append (generateOptionDetail (aComponent));
		retVal.append (generateOptionFooter (aComponent));
		return retVal.toString ();
	}
		
}											