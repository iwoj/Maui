package com.bitmovers.maui.components.foundation;

import java.util.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException;

// ========================================================================
// CLASS: TabbedPane                             (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is a very useful container that can have several overlapping 
  * panels. It is similar to the tabbed panes used by most desktop operating 
  * systems.
  * 
  */

//++ 405 JL 2001.09.21
// removed Settable interface
public class MTabbedPane extends MContainer
		implements HasSelectList,
							 HasPostValue,
							 Settable
//--
{
	
    
	int selectedTab = 0;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new tabbed pane object with the specified tabbed pane.
    *
    */
    
	public MTabbedPane()
	{
		generateUniqueName ();
		setLayout(new MBoxLayout());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getTabIndex
	// ----------------------------------------------------------------------
	
	/** Returns the index int of the tab whose tab label matches the given string. 
	  * -1 is returned if there is no match.
    *
    */
    
	public int getTabIndex(String aTabLabel)
	{
		int retVal = -1;
		
		MComponent [] theComponents = getComponents ();
		for (int i = 0; i < theComponents.length && retVal == -1; i++)
		{
			if (theComponents [i].getName ().equals (aTabLabel))
			{
				retVal = i;
			}
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setSelectedTab
	// ----------------------------------------------------------------------
	
	/** Brings the specified pane to the foreground.
	  * 
    * @param focusedPaneI the index of the pane to be moved to the foreground
    * 
    */
    
	public void setSelectedTab(int aSelectedTab) throws ArrayIndexOutOfBoundsException
	{
		if (aSelectedTab >= 0 && aSelectedTab < super.getComponentCount())
		{
			if (aSelectedTab != selectedTab)
			{
				setDirty (true);
			}
			getTabbedComponent(selectedTab).setSelected(false);
			selectedTab = aSelectedTab;
			getTabbedComponent(selectedTab).setSelected(true);
			dispatchActionEvent(new MActionEvent(this, MActionEvent.ACTION_TABSELECTED));
		}
		else
		{
			throw( new ArrayIndexOutOfBoundsException( "Pane " + aSelectedTab + " does not exist" ) );
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: add
	// ----------------------------------------------------------------------
	
	/** Add a component, using the component's name for the tab name.
	  *
	  * @param aMComponent The component to add
	  *
	  */
	  
	public MComponent add(MComponent aMComponent)
	{
		return add (aMComponent, aMComponent.getName());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: add
	// ----------------------------------------------------------------------
	
	/** Add a component, using the given string for the tab name. Note that
	  * <code>MWindow</code>s may not be added to tabbed panes. Doing so will
	  * cause an <code>IllegalArgumentException</code> to be thrown.
	  *
	  * @param aMComponent The component to add
	  *
	  */
	  
	public MComponent add(MComponent aMComponent, String aName)
	{
		if (aMComponent instanceof MWindow)
		{
			throw new IllegalArgumentException("MWindows cannot be added to TabbedPanes");
		}
		MComponent returnValue = aMComponent;
		MTabbedComponent theTabbedComponent = new MTabbedComponent (aName, aMComponent, getComponentCount ());
		super.add(theTabbedComponent);
		if (getComponentCount() == 1)
		{
			setSelectedTab(0);
		}
		else
		{
			theTabbedComponent.setSelected(false);
		}
		
		return returnValue;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectedTab
	// ----------------------------------------------------------------------
	
	/** Returns the index of the currently focused tab.
	  *
    */
	
	public int getSelectedTab()
	{
		return selectedTab;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getTabName
	// ----------------------------------------------------------------------
	
	/** Returns the name of the tab at the specified index.
	  *
    */
	
	public String getTabName(int aTabIndex)
	{
		if (aTabIndex < 0 || aTabIndex >= super.getComponentCount())
		{
			throw new ArrayIndexOutOfBoundsException (Integer.toString (aTabIndex));
		}
		
		MTabbedComponent theComponent = getTabbedComponent (aTabIndex);
		return theComponent.getName ();
	}			
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectedComponent
	// ----------------------------------------------------------------------
	
	/** Returns the component contained in the foreground pane.
	  *
    */
	
	public MComponent getSelectedComponent()
	{
		return (super.getComponentCount() > 0 ? ((MTabbedComponent)super.getComponent(selectedTab)).getComponent () : null);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectedTabButton
	// ----------------------------------------------------------------------
	
	/** Returns the <code>MTabbedPaneButton</code> of the foreground pane.
	  * 
	  * @invisible
	  *
    */
	
	public MTabbedPaneButton getSelectedTabButton()
	{
		return (super.getComponentCount() > 0 ? ((MTabbedComponent)super.getComponent(selectedTab)).getTab () : null);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getComponent
	// ----------------------------------------------------------------------
	
	/** Returns the component contained at the given index.
	  *
    */
	
	public MComponent getComponent(int aIndex)
	{
		MTabbedComponent theTabbedComponent = (MTabbedComponent) super.getComponent (aIndex);
		MComponent retVal = null;
		if (theTabbedComponent != null)
		{
			theTabbedComponent.fixupParent ();
			retVal = theTabbedComponent.getComponent ();
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectListOptions
	// ----------------------------------------------------------------------
	
	/** Returns a vector of all the select list options, as are intended for use by 
	  * the MTabbedPane's WML renderer.
	  *
    */
	
	public Vector getSelectListOptions()
	{
		Vector retVal = new Vector ();
		MComponent [] theComponents = getComponents ();
		for (int i = 0; i < theComponents.length; i++)
		{
			retVal.addElement (((MTabbedComponent) theComponents [i]).getName ());
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getTabButton
	// ----------------------------------------------------------------------
	
	/** Returns the <code>MTabbedPaneButton</code> of the pane at the given index.
	  * 
	  * @invisible
	  * 
    */
	
	public MTabbedPaneButton getTabButton(int aIndex)
	{
		MTabbedComponent theTabbedComponent = (MTabbedComponent) super.getComponent (aIndex);
		MTabbedPaneButton retVal = null;
		if (theTabbedComponent != null)
		{
			theTabbedComponent.fixupParent ();
			retVal = theTabbedComponent.getTab ();
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getTabbedComponent
	// ----------------------------------------------------------------------
	
	private MTabbedComponent getTabbedComponent(int index)
	{
		return (index >= 0 ? (MTabbedComponent) super.getComponent(index) : null);
	}		
	
	
	// ----------------------------------------------------------------------
	// METHOD: setEnabled
	// ----------------------------------------------------------------------
	
	/** Overrides Component.setEnabled() so all contained components may be 
    * enabled or disabled at once.
    * 
    * @param newEnabled true or false (enabled or disabled)
    * 
    */
    
	public void setEnabled (boolean newEnabled)
	{
		super.setEnabled (newEnabled);
		int theComponentCount = getComponentCount ();
		for (int i = 0; i < theComponentCount; i++)
		{
			super.getComponent (i).setEnabled(newEnabled);
		}
	}
	
	/** Enable or disable a tabbed pane, rather than the entire panel
	  *
	  * @param aEnabled The enable/disable boolean
	  * @param aIndex The index of the tab to enable/disable
	  */
	public void setEnabled (boolean aEnabled,
													int aIndex)
	{
		if (aIndex >= 0 && aIndex < getComponentCount ())
		{
			getTabbedComponent (aIndex).setEnabled (aEnabled);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** Fills-in parser values for the renderers to use. Parser values should not
	  * contain any device-specific formatting.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void fillParserValues()
	{
		super.fillParserValues();
		parser.setVariable ("numOfTabs", Integer.toString (getComponentCount () + 3));
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setValue
	// ----------------------------------------------------------------------
	
	/** Set the value for this component.
	  *
	  * @param aValue A value Object
	  * 
	  * @invisible
	  * 
	  */
	  
	public void setValue(Object aValue)
	{
		if (aValue instanceof Integer)
		{
			setSelectedTab (((Integer) aValue).intValue ());
		}
		else
		{
			int theTab = getTabIndex (aValue.toString ());
			if (theTab != -1)
			{
				setSelectedTab (theTab);
			}
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setValue
	// ----------------------------------------------------------------------
	
	/** Get the value for this component
	  * 
	  * @return The value Object for the component
	  * 
	  * @invisible
	  * 
	  */
	
	public Object getValue ()
	{
		return getTabbedComponent (getSelectedTab ()).getName ();
	}
	
	
		public String getPostValue()
		{
		  	return "$(" + getWMLSafeComponentID () + ")";//":e)";
		}
	
		
	// ----------------------------------------------------------------------
	// METHOD: setValue
	// ----------------------------------------------------------------------
	
	/** Create an event for this component
	  * 
	  * @param aStringData The data to use for creating the event
	  * 
	  * @return A MauiEvent
	  * 
	  * @invisible
	  * 
	  */
	  
	public MauiEvent createEvent (String aStringData)
	{
		MauiEvent retVal = null;
		int theIndex = (aStringData == null || aStringData.equals (MActionEvent.ACTION_SELECTED) ?
												getSelectedTab () :
												getTabIndex (aStringData));
		MTabbedComponent theComponent = getTabbedComponent (theIndex);
		if (theComponent == null)
		{
			retVal = new MActionEvent (this, aStringData);
			retVal.consume ();
		}
		else
		{
			retVal = theComponent.createEvent (aStringData);
		}
		return retVal;
	}
	
	/** This method causes the given MActionEvent to be published to all 
	  * MActionEventListeners.
	  *
	  * @param aEvent The MActionEvent to propagate
	  *
	  */
	
	public void dispatchActionEvent (MActionEvent aEvent)
	{
		super.dispatchActionEvent (aEvent);
		Object theComponent = aEvent.getSource ();
		if (theComponent instanceof MTabbedPane)
		{
			MButton theButton = getTabButton (getSelectedTab ());
			theButton.dispatchActionEvent (aEvent);
		}
	}
	
	// ----------------------------------------------------------------------
	// INNER CLASS: MTabbedComponent
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	class MTabbedComponent extends MContainer
	{
		
		
		MComponent component;
		MTabbedPaneButton tab;
		int tabIndex;
		
		
		// --------------------------------------------------------------------
		// CONSTRUCTOR
		// --------------------------------------------------------------------
		
		MTabbedComponent (String aName, MComponent aComponent, int aTabIndex)
		{
			name = aName;
			component = aComponent;
			tabIndex = aTabIndex;
			this.add (component);
			tab = new MTabbedPaneButton (name);
			this.add (tab);
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getComponent
		// --------------------------------------------------------------------
		
		public MComponent getComponent()
		{
			return component;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getHTMLParsed
		// --------------------------------------------------------------------
		
		public String getHTMLParsed()
		{
			return "";
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: getTab
		// --------------------------------------------------------------------
		
		public MTabbedPaneButton getTab()
		{
			return tab;
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: invalidate
		// --------------------------------------------------------------------
		
		public void invalidate()
		{
			super.invalidate();
			MComponent [] theComponents = getComponents();
			for (int i = 0; i < theComponents.length; i++)
			{
				theComponents [i].invalidate ();
			}
		}
				
		
		// --------------------------------------------------------------------
		// METHOD: setEnabled
		// --------------------------------------------------------------------
		
		public void setEnabled(boolean aEnabled)
		{
			super.setEnabled(aEnabled);
			tab.setEnabled(aEnabled);
			component.setEnabled(aEnabled);
			invalidate();
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: setSelected
		// --------------------------------------------------------------------
		
		public void setSelected(boolean aSelected)
		{
			component.setVisible(aSelected);
			tab.setSelected(aSelected);
			invalidate();
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: fixupParent
		// --------------------------------------------------------------------
		
		protected void fixupParent()
		{
			if (component.getParent () != this)
			{
				//
				//	The component must remain as part of the tabbed panel
				//
				this.add (component, name);
			}
		}
		
		
		// --------------------------------------------------------------------
		// METHOD: createEvent
		// --------------------------------------------------------------------
		
		public MauiEvent createEvent(String aStateData)
		{
			MActionEvent retVal = new MActionEvent (MTabbedPane.this, null);
			if (selectedTab != tabIndex)
			{
				
				setSelectedTab(tabIndex);
				MTabbedPane.this.invalidate ();
			}
			else
			{
				retVal.consume ();
			}
			return retVal;
		}
	
		
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF