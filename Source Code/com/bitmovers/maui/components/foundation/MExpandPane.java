// =============================================================================
// com.bitmovers.maui.components.foundation.MExpandPane
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MauiEvent;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException;


// ========================================================================
// CLASS: MExpandPane                            (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is similar to the triangular widgets found in the list view 
  * of the Macintosh finder. Clicking on this component causes it to expand
  * and display previously hidden components. This is a very useful 
  * component for information that does not need to be constantly visible,
  * but requires a presence in the user interface of an application.
  *
  */

public class MExpandPane extends MContainer implements MActionListener, HasLabel
{
	
	
	protected String label;
	
	private MExpandPaneButton expandPaneButton;
	private boolean open = false;
	private boolean labelIsVisible = true;
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------------------------------------
	
	/** Constructs a new expand pane object with no label.
	  *
	  */
	
	public MExpandPane()
	{
		this(null);
	}
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------------------------------------
	
	/** Constructs a new expand pane object with the given label.
	  *
	  */

	public MExpandPane (String label)
	{
		this.label = label;
		this.expandPaneButton = (this.label == null ? new MExpandPaneButton (this) :
		                                              new MExpandPaneButton (this.label, this));
		
		this.expandPaneButton.addActionListener(this);
		this.generateUniqueName();
		this.setLayout(new MBoxLayout(this));
		this.labelIsVisible = (this.label != null);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getLabelVisibility
	// ---------------------------------------------------------------------------
	
	/** Returns the label's visibility value.
	  *
	  * @return Value <code>true</code> if the label is visible, <code>false<code> otherwise.
	  */
      
	public boolean getLabelVisibility()
	{
		return this.labelIsVisible;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setLabelVisibility
	// ---------------------------------------------------------------------------
	
	/** Sets the label's visibility value.
	  * 
	  * @param labelIsVisible <code>true</code> if label needs to be visible, 
	  *  <code>false</code> otherwise.
	  *
	  */
      
	public void setLabelVisibility(boolean labelIsVisible)
	{
		this.labelIsVisible = labelIsVisible;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: toggleOpen
	// ---------------------------------------------------------------------------
	
	/** Reverses the current state of the expand pane (e.g. if the pane is open, 
	  * this method will close it; if it is closed, this method will open it).
	  *
	  */
      
	public void toggleOpen()
	{
		setOpen (!open);
	}
	
	
	//++ 403 JL 2001.09.26
	// ---------------------------------------------------------------------------
	// METHOD: add
	// ---------------------------------------------------------------------------
	
	/** This overrides <code>MContainer</code>'s add method and sets the added
	  * component to the appropriate visibility
	  */
      
	public void add(MComponent aComponent, Object aConstraint)
	{
	    add(aComponent, aConstraint, -1);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: add
	// ---------------------------------------------------------------------------
	
	/** This overrides <code>MContainer</code>'s add method and sets the added
	  * component to the appropriate visibility
	  */
      
	public MComponent add(MComponent aComponent)
	{
	    add(aComponent, null, -1);
	    return aComponent;
   	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: add
	// ---------------------------------------------------------------------------
	
	/** This overrides <code>MContainer</code>'s add method and sets the added
	  * component to the appropriate visibility
	  */
      
	public void add(MComponent aComponent, Object aConstraint, int index)
	{
	    aComponent.setVisible(isOpen());
	    super.add(aComponent, aConstraint, index);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: add
	// ---------------------------------------------------------------------------
	
	/** This overrides <code>MContainer</code>'s add method and sets the added
	  * component to the appropriate visibility
	  */
      
	public MComponent add(MComponent aComponent, int index)
	{
	   add(aComponent, null, index);
	   return aComponent;
	}
    
    
	// ----------------------------------------------------------------------
	// METHOD: setChildrenVisible
	// ----------------------------------------------------------------------
	  
	/** Sets the visibility of all child components to true only if the pane
	  * is opened
	  *
	  * @param b <code>true</code> to set child components as visible, <code>false</code> otherwise.
	  */
  
	public void setChildrenVisible(boolean b) 
	{
	    if ((!b) || (b && isOpen()))
	    {
	        super.setChildrenVisible(b);
	    }
	}
	//--
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setOpen
	// ---------------------------------------------------------------------------
	
	/** Sets the expand pane to the given state.
	  *
	  * @param aOpenState <code>true</code> if state of the expand pane is open, 
	  *             <code>false</code> otherwise.
	  */
  
	public void setOpen(boolean aOpenState)
	{
		if (open != aOpenState)
		{
			open = aOpenState;
			setDirty(true);
			setChildrenVisible(aOpenState);
			invalidate();
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: open
	// ---------------------------------------------------------------------------
	
	/** @deprecated  Use <code>setOpen(true)</code> instead.
	  * 
	  */
    
	public void open()
	{
		setOpen(true);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: close
	// ---------------------------------------------------------------------------
	
	/** @deprecated  Use <code>setOpen(false)</code> instead.
	  *
	  */
      
	public void close()
	{
		setOpen(false);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: isOpen
	// ---------------------------------------------------------------------------
	
	/** Checks the current state of the expand pane.
	  *
	  * @return <code>true</code> if expand pane is open, <code>false</code> otherwise.
	  */
    
	public boolean isOpen()
	{
		return open;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: actionPerformed
	// ---------------------------------------------------------------------------
	
	/** Returns the current state of the expand pane.
	  *
	  * @invisible
	  * 
	  */
    
	public void actionPerformed(MActionEvent aEvent)
	{
		this.setOpen(aEvent.getActionCommand().equals(MActionEvent.ACTION_OPENED));
		this.dispatchActionEvent(new MActionEvent(this, aEvent.getActionCommand()));
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: fillParserValues
	// ---------------------------------------------------------------------------
	
	/** This method is responsible for setting parser values for the component's 
	  * renderer.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void fillParserValues()
	{
		super.fillParserValues();
		
		if (labelIsVisible)
		{
			parser.setVariable("label", label);
		}
		
		parser.setVariable ("expandButton", expandPaneButton.render());
		
		if (open)
		{
			parser.setVariable ("expandedPane", getLayout().render());
		}
		else
		{
			parser.setVariable("expandedPane", "");
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getLabel
	// ---------------------------------------------------------------------------
	
	/** Returns a string representing the expand pane's label.
	  * 
	  * @return The expand pane's label text.
	  */
	  
	public String getLabel()
	{
		return label;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setLabel
	// ---------------------------------------------------------------------------
	
	/** Sets a string to represent the expand pane's label.
	  * 
	  * @param aLabel A string text to set as expand pane's label.
	  */
	  
	public void setLabel(String aLabel)
	{
		label = aLabel;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getButton
	// ---------------------------------------------------------------------------
	
	/** Returns this expand pane's internal button.
	  * 
	  * @return The button contained in the expand pane.
	  * @invisible
	  * 
	  */
	  
	public MExpandPaneButton getButton()
	{
		return expandPaneButton;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: createEvent
	// ----------------------------------------------------------------------

	/** Overrides Component.createEvent(). No state saving is necessary for the 
	  * <code>MButton</code> component.
	  *
	  * @return a new <code>MActionEvent</code> with the <code>MActionEvent.ACTION_OPENED</code> 
	  *         command if the expand pane has just opened, or 
	  *         <code>MActionEvent.ACTION_CLOSED</code> if it has just closed.
	  * @invisible
	  * 
	  */
	
	public MauiEvent createEvent(String aStateData)
	{
		this.toggleOpen();
		return new MActionEvent(this, (this.open ? MActionEvent.ACTION_OPENED :
		                                           MActionEvent.ACTION_CLOSED));
	}
    
    
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF