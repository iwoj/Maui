// =============================================================================
// com.bitmovers.maui.components.foundation.MRadioButton
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.*;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.HasSelectList;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.render.StubRenderer;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MauiEvent;


// ========================================================================
// CLASS: MRadioButton                           (c) 2001 Bitmovers Systems
// ========================================================================

/** This is a radio button wrapper. It holds information about its 
  * <code>MRadioButtonGroup</code>.
  *
  */
  
public class MRadioButton extends MContainer
	implements Settable,
             HasSelectList,
             HasLabel,
             HasPostValue
{
	
	
	protected final MRadioButtonGroup radioButtonGroup;
	protected int buttonIndex = 0;
	protected boolean selected = false;
	
	protected MLabel label;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Since <code>MRadioButtons</code> can only be contained with 
		* <code>MRadioButtonGroup</code> objects, they cannot be instantiated outside 
		* of this package.
		*
	  * @param aRadioButtonGroup <code>MRadioButtonGroup</code> object for which to
	  *														construct a radio button for.
	  *
	  * @param aName A label associated with the radio button.
	  */
	  
	protected MRadioButton (MRadioButtonGroup aRadioButtonGroup, String aName)
	{
		radioButtonGroup = aRadioButtonGroup;
		name = aName;
		
		label = new MLabel(name);
		add(label);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getLabel
	// ----------------------------------------------------------------------
	
	/** Returns the radio button's label.
	  * 
	  * @return The associated label of the radio button.
	  */
	  
	public String getLabel()
	{
		return label.getText();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMLabel
	// ----------------------------------------------------------------------
	
	/** Returns the radio button's MLabel.
	  * 
	  * @return The associated text of the MLabel of the radio button.
	  */
	  
	public MLabel getMLabel()
	{
		return label;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRadioButtonGroup
	// ----------------------------------------------------------------------
	
	/** Returns the group to which this radio button belongs.
	  * 
	  * @return <code>MRadioButtonGroup</code>.
	  */
	  
	public MRadioButtonGroup getRadioButtonGroup()
	{
		return radioButtonGroup;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setButtonIndex
	// ----------------------------------------------------------------------
	
	/** Sets the radio button index.
	  *
	  * @param aIndex The index for this button.
	  *
	  */
	  
	protected void setButtonIndex(int aButtonIndex)
	{
		buttonIndex = aButtonIndex;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getButtonIndex
	// ----------------------------------------------------------------------
	
	/** Returns the index of this radio button within its group.
	  *
	  * @return The index.
	  *
	  */
	  
	public int getButtonIndex()
	{
		return buttonIndex;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setValue
	// ----------------------------------------------------------------------
	
	/** Overrides <code>Settable.setValue()</code>. Takes either "true" or
	  * "false" as a string.
	  * 
	  * @param aSelected Value "true" to set the radio button as selected, "false" otherwise.
	  */
	
	public void setValue(Object aSelected)
	{
		String theSelected = (String) aSelected;
		if (theSelected == null ||
			theSelected.length () == 0 ||
			theSelected.equalsIgnoreCase ("false"))
		{
			setSelected (false);
		}
		else
		{
			setSelected (true);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getValue
	// ----------------------------------------------------------------------
	
	/** Overrides <code>Settable.getValue()</code>. Returns either "true" or
	  * "false" as a string, depending on the state of the button.
	  * 
	  * @return A string "true" if the radio button is selected, "false" otherwise.
	  */
	
	public Object getValue()
	{
		return (selected ? "true" : "false");
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
	/** Returns WML safe string.
	  * 
	  * @return WML safe value.
	  * @invisible
	  * 
	  */
	  
	public String getPostValue ()
	{
		return "$(" +  getWMLSafeComponentID() + ")";//":e)";
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setSelected
	// ----------------------------------------------------------------------
	
	/** Sets the radio button as selected or not. The second parameter is to notify
		* the event listener to fire an event depending on the boolean value. <code>true</code>
		* fires an event to the event listener, <code>false</code> indicates that an event
		* should <b>NOT</b> be fired.
	  *
	  * @param aSelected <code>true</code> sets radio button as selected, <code>false</code> 
	  * 								 otherwise.
	  * @param aNotify Boolean indicating if notification should be done or not.
	  *
	  */
	  
	protected void setSelected (boolean aSelected, boolean aNotify)
	{
		if (selected != aSelected)
		{
			selected = aSelected;
			if (aNotify)
			{
				dispatchActionEvent (new MActionEvent (this,
														(aSelected ? "true" : "false")));
			}
		}
	}
	/** Sets the button as selected or not.
	  *
	  * @param aSelected <code>true</code> sets the radio button as selected, <code>false</code>
	  *										de-selects the radio button. In both cases, the event is fired to the
	  *										listener.
	  *	
	  */
	  
	protected void setSelected (boolean aSelected)
	{
		setSelected (aSelected, true);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: isSelected
	// ----------------------------------------------------------------------
	
	/** Checks the state of the radio button.
		* 
		*	@returns <code>true</code> if the radio button is selected, <code>false</code> otherwise.
	  *
	  */
	  
	public boolean isSelected()
	{
		return selected;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: createEvent
	// ----------------------------------------------------------------------
	
	/** Returns a <code>MauiEvent</code>, depending on the state of the radio button.	
	  * @invisible
	  * 
	  */
	  
	public MauiEvent createEvent (String aStateData)
	{
 		/*boolean theState = (aStateData == null ||
							aStateData.length () == 0 ||
							aStateData.equals ("false") ?
								false :
								true);*/
		MActionEvent retVal = null;
		if (aStateData == null)
		{
			retVal = new MActionEvent (this, "true");
		}					
		else if (aStateData.equals (MActionEvent.ACTION_PUSH))
		{
			retVal = new MActionEvent (this, MActionEvent.ACTION_PUSH);
		}
		else
		{
			MRadioButtonGroup theGroup = getRadioButtonGroup ();
			MRadioButton theRadioButton = theGroup.getRadioButton (aStateData);
			retVal = new MActionEvent (theRadioButton, aStateData);
			if (theRadioButton.isSelected ())
			{
				retVal.consume ();
			}
			theGroup.doSetSelectedButtonIndex (theRadioButton.getButtonIndex (), true);
		}

		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** Overriddes <code>MComponent</code>'s render method to register the radio button
		* with the maui application as an event listener. This is done at render time to 
	  * ensure that the radio buttons's root parent exists and is a maui application.
	  *
	  * @invisible
	  * 
	  */
	  
	public String render()
	{
		if (renderer == null)
		{
			//
			//	If the previous component in the container is an MRadioButton, then
			//	just use a stub renderer for this MRadioButton, coz the previous one
			//	will render all consecutive MRadioButtons as rows of a single table.
			//
			int theComponentIdx = parent.getComponentIndex (this);
			
			renderer = getRenderer();

			/* It's not always desirable to do this because what if you want
			   to put something beside a particular MRadioButton? If the first
			   MRadioButton's renderer renders all buttons in the group, a
			   developer would never be able to put anything beside say the
			   third button.
			
			renderer = (theComponentIdx > 0 &&
						(parent.getComponent (theComponentIdx -1)) instanceof MRadioButton ?
							new StubRenderer () :
							getRenderer ());
			*/
		}
		return super.render ();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectListOptions
	// ----------------------------------------------------------------------
	
	/** For the purposes of WML, this is a select list.
	  * 
	  * @invisible
	  * 
	  */
	  
	public Vector getSelectListOptions()
	{
		Vector retVal = new Vector ();
		retVal.addElement (getName ());
		return retVal;
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF