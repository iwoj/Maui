// =============================================================================
// CLASS: com.bitmovers.maui.engine.render.MMenuBar_html
// =============================================================================

package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import java.awt.Color;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.utilities.StringParser;

/** Generic HTML renderer for MMenuBar component.
  */

public class MMenuBar_html extends A_Renderer implements I_RendererInitialize
{
	// ---------------------------------------------------------------------------
	
	
	// ---------------------------------------------------------------------------
	// METHOD: render
	// ---------------------------------------------------------------------------
	
	public String render(I_Renderable aRenderable)
	{
		return this.render(aRenderable, 1);
	}
	
	public String render(I_Renderable aRenderable, int recursionCounter)
	{
		float oddEvenIndicator = recursionCounter % 2;
		StringParser parser = new StringParser();
		MMenu openMenu = null;
		
		MContainer menuContainer = (MContainer)aRenderable;
		StringBuffer menuLabels = new StringBuffer();
		
		// Iterate through all my menu items
		for (int i = 0; i < menuContainer.getComponentCount(); i++)
		{
			MMenuItem menu = (MMenuItem)menuContainer.getComponent(i);
			
			// Set the menu item to the appropriate color.
			if (oddEvenIndicator == 0)
			{
				menu.setColor(MMenuBar.SECONDARY_COLOR);
			}
			else
			{
				menu.setColor(MMenuBar.PRIMARY_COLOR);
			}
			
			// Check for open menus
			if (menu instanceof MMenu)
			{
				if (((MMenu)menu).isOpen())
				{
					openMenu = (MMenu)menu;
				}
			}
			
			// Render MMenuItem
			menuLabels.append(menu.getButton().render());
		}
		
		// Add the appropriate color to the template.
		if (oddEvenIndicator == 0)
		{
			// even
			parser.setVariable("barColor", "#" + Integer.toHexString(MMenuBar.SECONDARY_COLOR.getRed()) + 
			                               Integer.toHexString(MMenuBar.SECONDARY_COLOR.getGreen()) + 
			                               Integer.toHexString(MMenuBar.SECONDARY_COLOR.getBlue()));
		}
		else
		{
			// odd
			parser.setVariable("barColor", "#" + Integer.toHexString(MMenuBar.PRIMARY_COLOR.getRed()) + 
			                               Integer.toHexString(MMenuBar.PRIMARY_COLOR.getGreen()) + 
			                               Integer.toHexString(MMenuBar.PRIMARY_COLOR.getBlue()));
		}
		
		// Add menu labels to the template.
		parser.setVariable("menuLabels", menuLabels.toString());
		
		// Recurse if necessary.
		if (openMenu != null)
		{
			return parser.parseString(this.renderTemplate [0]) + "\n" + render(openMenu, ++recursionCounter);
		}
		else
		{
			return parser.parseString(this.renderTemplate [0]);
		}
	}
	
	
	// ---------------------------------------------------------------------------
}