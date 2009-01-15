// =============================================================================
// com.bitmovers.maui.components.foundation.MMenu
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.*;
import java.awt.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MMenu                                  (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is the menu component, which represents a list of clickable menu 
  * items. The <code>MMenu</code> class enables menu hierarchies by virtue of 
  * the fact that it extends <code>MMenuItem</code>.
  *
  */

public class MMenu extends MMenuItem implements MMenuItemContainer
{
	
		
	private static final String base = "MMenu";
  private static int nameCounter = 0;
	
	private final int myCounterValue;
	
	private Vector menuItems = new Vector();
	private boolean isOpen = false;
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------------
	
	/** Constructs a menu with the given label.
	  *
	  */
	
	public MMenu(String label)
	{
		super(label);
		this.myCounterValue = nameCounter++;
		this.name = base + myCounterValue;
		super.button.setChildren(true);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: actionPerformed
	// ---------------------------------------------------------------------------
	
	/** Republish <code>MMenuButton</code> click event to all <code>MMenuItem</code> listeners.
	  * 
	  * @invisible
	  * 
	  */
	
	public void actionPerformed(MActionEvent event)
	{
		if (this.isOpen())
		{
			this.close();
		}
		else
		{
			((MMenuItemContainer)super.parent).closeChildren();
			this.open();
		}
		this.dispatchActionEvent(new MActionEvent(this, event.getActionCommand()));
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getMenuItem
	// ---------------------------------------------------------------------------
	
	/** This is a convenience method to access a contained <code>MMenuItem</code> at the 
	  * specified index.
	  *
	  * @param index The index of the contained menu item.
	  *
	  *	@return menu item at position <code>index</code>.
	  *
	  */
	  
	public MMenuItem getMenuItem(int index)
	{
		return (MMenuItem)this.getComponent(index);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getMenu
	// ---------------------------------------------------------------------------
	
	/** This is a convenience method to access a contained <code>MMenu</code>. Be sure that the 
	  * component at the given index is a <code>MMenu</code> object (not a 
	  * <code>MMenuItem</code>) or you will have to deal with <code>ClassCastExceptions</code>.
	  *
	  * @param index The position at which to find the <code>MMenu</code> object.
	  *
	  * @return <code>MMenu</code> object at position <code>index</code>.
	  *
	  */
	  
	public MMenu getMenu(int index)
	{
		return (MMenu)this.getComponent(index);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getMenuItemCount
	// ---------------------------------------------------------------------------
	
	/** Returns the number of items in a <code>MMenu</code>. It implements the defined method 
		* from <code>MMenuItemContainer</code> interface.
	  *
	  * @return Number if items in <code>MMenu</code> object.	  
	  */
	  
	public int getMenuItemCount()
	{
		return this.getComponentCount();
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: isOpen
	// ---------------------------------------------------------------------------
	
	/** Checks this <code>MMenu</code> if it is open or closed.
	  *
	  * @return <code>true</code> if this <code>MMenu</code> is open, <code>false</code> 
	  * if it is closed.
	  */
	  
	public boolean isOpen()
	{
		return this.isOpen;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: open
	// ---------------------------------------------------------------------------
	
	/** Opens this </code>MMenu<code>.
	  *
	  */
	  
	protected void open()
	{
		if (!this.isOpen)
		{
			this.isOpen = true;
			super.button.open();
			this.invalidate();
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: addComponent
	// ---------------------------------------------------------------------------
	
	/** This method is overridden to ensure that only <code>MMenuItems</code> are added.
	  *
	  * @param comp The MMenu component to add menu item to.
	  *
	  * @param constraints The <code>MMenuItems</code> to be added.
	  *
	  * @param index The position at which to add the menu item.
	  */
	  
	protected void addComponent(MComponent comp, Object constraints, int index) 
	{
		if (comp instanceof MMenuItem)
		{
			super.addComponent(comp, constraints, index);
			((MMenuItem)comp).setMenuBar(this.menubar);
		}
		else
		{
			throw new IllegalArgumentException("Only MMenuItems may be added to MMenu.");
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: close
	// ---------------------------------------------------------------------------
	
	/** Closes this <code>MMenu</code> and all children <code>MMenus</code>. 
		* Implements the defined method from <code>MMenuItemContainer</code> interface.
	  *
	  */
	
	public void close()
	{
		if (this.isOpen)
		{
			// Close myself
			this.isOpen = false;
			super.button.close();
			
			// Close my children
			this.closeChildren();
			
			this.invalidate();
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: closeChildren
	// ---------------------------------------------------------------------------
	
	/** Closes this <code>MMenu</code>'s children <code>MMenus</code>. 
		* Implements the defined method from <code>MMenuItemContainer</code> interface.
	  * 
	  * @invisible
	  * 
	  */
	
	public void closeChildren()
	{
		MComponent[] menus = this.getComponents();
		
		for (int i = 0; i < menus.length; i++)
		{
			try
			{
				((MMenu)menus[i]).close();
			}
			// If the current MMenuItem isn't a MMenu, do nothing.
			catch (ClassCastException e) {}
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// INNER CLASS: MMenuLayout
	// ---------------------------------------------------------------------------
	
	/** A special layout class for exclusive use by the <code>MMenuBar</code> class.
	  *
	  */
	
	private class MMenuLayout extends MLayout
	{
		public MMenuLayout(MMenu parent)
		{
			this.parent = parent;
		}
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF