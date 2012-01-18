// =============================================================================
// com.bitmovers.maui.components.foundation.MMenuBar
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.awt.Color;
import java.util.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MMenuBar                               (c) 2001 Bitmovers Systems
// ========================================================================

/** This class represents a menu bar, similar to menu bars in desktop graphical 
  * user interfaces, which holds expandable hierarchies of menu items. The menu
  * bar is the root level of this system.<p>
  * 
  * <code>MMenuBar</code>s may be added to <code>MFrame</code>s using the 
  * <code>setMenuBar()</code> method.
  * 
  */

public class MMenuBar extends MContainer implements MMenuItemContainer, MActionListener
{
	
		
	/** This is the color of the odd-numbered rows in the menu bar. */
	public static final Color PRIMARY_COLOR = new Color(204, 204, 153);
	
	/** This is the color of the even-numbered rows in the menu bar. */
	public static final Color SECONDARY_COLOR = new Color(153, 153, 102);
	
	private static final String base = "MMenuBar";
  private static int nameCounter = 0;
	
	private final int myCounterValue;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new menu bar.
	  *
	  */
	  
	public MMenuBar()
	{
		this.myCounterValue = nameCounter++;
		this.name = base + myCounterValue;
		this.setLayout(new MMenuBarLayout(this));
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: actionPerformed
	// ----------------------------------------------------------------------
	
	/** Implements autocollapse functionality. It listens to all events published 
		* by the maui application and closes all menus if an event external to the 
		*	menu bar occurs.
	  *
	  * @invisible
	  * 
	  */
	
	public void actionPerformed(MActionEvent event)
	{
		if (!(event.getSource() instanceof MMenuItem) &&
			  !(event.getSource() instanceof MMenuButton))
		{
			this.closeChildren();
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMenu
	// ----------------------------------------------------------------------
	
	/** Returns the contained menu at the given index.
	  * 
	  * @param index The position of the menu contained.
	  *
	  *	@return the <code>MMenu</code> at position <code>index</code>.
	  */
	  
	public MMenu getMenu(int index)
	{
		return (MMenu)this.getComponent(index);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMenuCount
	// ----------------------------------------------------------------------
	
	/** Returns the number of menus contained within this menu bar.
	  * 
	  * @return The int representing the number of menus on the menu bar.
	  */
	  
	public int getMenuItemCount()
	{
		return this.getComponentCount();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: closeChildren
	// ----------------------------------------------------------------------
	
	/** Closes all child menus.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void closeChildren()
	{
		MComponent[] menus = this.getComponents();
		
		for (int i = 0; i < menus.length; i++)
		{
			((MMenu)menus[i]).close();
		}
		this.invalidate();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** Overriddes <code>MComponent</code>'s render method to register the menu bar with the 
	  * maui application as an event listener. This is done at render time to 
	  * ensure that the menu bar's root parent exists and is a maui application.
	  *
	  * @invisible
	  * 
	  */
	
	public String render()
	{
		String renderedStuff = super.render();
		
		if (this.getRootParent() instanceof MauiApplication)
  	{
  		((MauiApplication)this.getRootParent()).addActionListener(this);
  	}
  	
  	return renderedStuff;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: addComponent
	// ----------------------------------------------------------------------
	
	/** Overriddes <code>MContainer</code>'s addComponent method to ensure that only menus can  
	  * be added to menu bars.
	  *
	  * @param comp Menu bar component to add to. 
	  *
	  * @param constraints Menus to add to the menu bar.
	  *
	  * @param index The position to add the menus at.
	  */
	
	protected void addComponent(MComponent comp, Object constraints, int index) 
	{
		if (comp instanceof MMenu)
		{
			super.addComponent(comp, constraints, index);
			((MMenu)comp).setMenuBar(this);
			((MMenu)comp).button.setLevel(MMenuButton.LEVEL1);
			((MMenu)comp).button.setChildren(true);
		}
		else
		{
			throw new IllegalArgumentException("Only MMenu objects may be added to MMenuBar.");
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// INNER CLASS: MMenuBarLayout
	// ---------------------------------------------------------------------------
	
	/** This is a special layout class for exclusive use by the <code>MMenuBar</code> class.
		*
	  *
	  */
	
	private class MMenuBarLayout extends MLayout
	{
		public MMenuBarLayout(MMenuBar parent)
		{
			this.parent = parent;
		}
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF