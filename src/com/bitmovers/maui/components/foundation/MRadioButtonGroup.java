// =============================================================================
// com.bitmovers.maui.components.foundation.MRadioButtonGroup
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.Vector;
import java.util.Enumeration;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.render.I_Renderer;
import com.bitmovers.maui.events.*;
import com.bitmovers.utilities.StringParser;


// ========================================================================
// CLASS: MRadioButtonGroup                      (c) 2001 Bitmovers Systems
// ========================================================================

/** This defines a group of radio buttons. Since it doesn't really make sense for a
  * radio button to exist outside of a group, buttons are created through this class.
  *
  * For example:<p>
	* 
	* <pre>
	* MRadioButtonGroup iPreferRadioGroup = new MRadioButtonGroup("prefer to use");
	*
	* MCheckBox macOsRadioButton = iPreferRadioGroup.addRadioButton("Mac OS");
	* MCheckBox unixRadioButton = iPreferRadioGroup.addRadioButton("Unix/Linux");
	* MCheckBox windowsRadioButton = iPreferRadioGroup.addRadioButton("Windows");  
  * </pre>
  * <p>
  * The resulting values and labels would look like this:<p>
  * 
  * <pre>
  * prefer to use:
  * (.) Mac OS 
 	* ( ) Unix/Linux 
 	* ( ) Windows   
  * </pre>
  */

public class MRadioButtonGroup extends MComponent
	implements MActionListener,
			   HasSelectList,
			   HasPostValue
{
	
	
	private final String groupName;
	private MRadioButton [] radioButtons = new MRadioButton [0];
	private int radioButtonCount = 0;
	private int selectedButtonIndex = 0;
	private static int counter = 0;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a radio button group.
	  *
	  * @param aGroupName The name of the radio group.
	  * 
	  */
	  
	public MRadioButtonGroup()
	{
    super();
    
    groupName = getComponentID();
	}

	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a radio button group with the given name.
	  *
	  * @param aGroupName The name of the radio group.
	  * 
	  * @deprecated Developers should not have to be responsible for setting
	  *             the name of the group. If they don't properly set the
	  *             name, and have more than one <code>MRadioButtonGroup</code> on the
	  *             same window, all the buttons in all of the groups will
	  *             be part of the same group.
	  *
	  */
	  
	public MRadioButtonGroup(String aGroupName)
	{
    this();
	}

	
	// ----------------------------------------------------------------------
	// METHOD: getGroupName
	// ----------------------------------------------------------------------
	
	/** Returns the group name.
	  * 
	  * @return The name associated with <code>MRadioButtonGroup</code>.
	  */
	  
	public String getGroupName()
	{
		return groupName;
	}

	
	// ----------------------------------------------------------------------
	// METHOD: addToList
	// ----------------------------------------------------------------------
	
	/** Appends the given radio button to the group.
	  * 
	  * @param aRadioButton <code>MRadioButton</code> that will be added to the 
	  *											radio button group at the end.
	  * 
	  */
	  
	private void addToList(MRadioButton aRadioButton)
	{
		if (radioButtonCount >= radioButtons.length)
		{
			MRadioButton [] theNewList = new MRadioButton [radioButtons.length + 4];
			System.arraycopy (radioButtons, 0, theNewList, 0, radioButtons.length);
			radioButtons = theNewList;
		}
		radioButtons [radioButtonCount++] = aRadioButton;
		aRadioButton.setButtonIndex (radioButtonCount - 1);
	}

	
	// ----------------------------------------------------------------------
	// METHOD: removeFromList
	// ----------------------------------------------------------------------
	
	/** Removes the given radio button from the group.
	  * 
	  * @param aRadioButton <code>MRadioButton</code> that will be removed from the 
	  *											radio button group.
	  * 
	  */
	  
	private void removeFromList (MRadioButton aRadioButton)
	{
		int i = 0;
		for (i = 0; i < radioButtonCount; i++)
		{
			if (radioButtons [i] == aRadioButton)
			{
				break;
			}
		}
		
		if (i < radioButtonCount)
		{
			if (i < radioButtonCount - 1)	// Don't remove the last entry
			{
				System.arraycopy (radioButtons,
								  i + 1,
								  radioButtons,
								  i,
								  radioButtonCount - i);
				for (; i < radioButtons.length; i++)
				{
					radioButtons [i].setButtonIndex (i);
				}
			}
			radioButtonCount--;
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: addRadioButton
	// ----------------------------------------------------------------------
	
	/** Adds a new radio button with the given name. Since 
	  * <code>MRadioButton</code>s can only exist within a group, the 
	  * constructor is protected. The <code>MRadioButton</code> is created 
	  * here.
	  *
	  * @param aName The name of the new radio button.
	  * 
	  * @return a reference to the newly created and added
	  *         <code>MRadioButton</code>.
	  * 
	  */
	  
	public MRadioButton addRadioButton(String aName)
	{
		MRadioButton retVal = new MRadioButton (this, aName);
		addToList (retVal);
		retVal.addActionListener (this);
		if (radioButtons [0] == retVal)
		{
			retVal.setSelected (true);
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: removeRadioButton
	// ----------------------------------------------------------------------
	
	/** Removes a radio button.
	  *
	  * @param aRadioButton The <code>MRadioButton</code> to remove.
	  *
	  */
	  
	public void removeRadioButton(MRadioButton aRadioButton)
	{
		removeFromList(aRadioButton);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectedButton
	// ----------------------------------------------------------------------
	
	/** Returns the currently selected <code>MRadioButton</code>.
	  *
	  * @return <code>MRadioButton</code> that is selected.
	  * 
	  */
	  
	public MRadioButton getSelectedButton()
	{
		 return radioButtons[selectedButtonIndex];
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectedButtonIndex
	// ----------------------------------------------------------------------
	
	/** Returns the index of the currently selected <code>MRadioButton</code>.
	  *
	  * @return The index of the currently selected <code>MRadioButton</code>.
	  * 
	  */
	  
	public int getSelectedButtonIndex()
	{
		return selectedButtonIndex;
	}

	
	// ----------------------------------------------------------------------
	// METHOD: doSetSelectedButtonIndex
	// ----------------------------------------------------------------------
	
	protected boolean doSetSelectedButtonIndex (int aIndex, boolean aNotify)
	{
		boolean retVal = (aIndex != selectedButtonIndex);
		if (retVal)
		{
			radioButtons [selectedButtonIndex].setSelected (false, aNotify);
			selectedButtonIndex = aIndex;
			radioButtons [selectedButtonIndex].setSelected (true, aNotify);
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setSelectedButtonIndex
	// ----------------------------------------------------------------------
	
	/** Sets the selected radio button to the button at the given index.
	  *
	  * @param aButtonIndex The index of the radio button to select.
	  *
	  */
	
	public void setSelectedButtonIndex(int aIndex)
	{
		if (aIndex < 0 || aIndex >= radioButtonCount)
		{
			throw new ArrayIndexOutOfBoundsException (Integer.toString (aIndex));
		}
		else if (doSetSelectedButtonIndex (aIndex, true))
		{
			notifyListeners (new MActionEvent (this, MActionEvent.ACTION_CHECKED));
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRadioButtons
	// ----------------------------------------------------------------------
	
	/** Returns an array of all radio buttons.
	  *
	  * @return An array of <code>MRadioButton</code> objects.
	  * 
	  */
	  
	public MRadioButton[] getRadioButtons()
	{
		MRadioButton [] retVal = new MRadioButton [radioButtonCount];
		System.arraycopy (radioButtons, 0, retVal, 0, radioButtonCount);
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRadioButtons
	// ----------------------------------------------------------------------
	
	/** Returns a <code>MRadioButton</code> at a certain index.
	  *
	  * @return The <code>MRadioButton</code>, or <code>null</code> if it does not exist.
	  * 
	  */
	  
	public MRadioButton getRadioButton (int aIndex)
	{
		return (aIndex >= 0 && aIndex < radioButtons.length ?
					radioButtons [aIndex] :
					null);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRadioButton
	// ----------------------------------------------------------------------
	
	/** Returns a radio button based on it's label.
	  *
	  * @param aLabel The label to look for.
	  *
	  * @return The radio button, or <code>null</code> if not found.
	  * 
	  */
	  
	public MRadioButton getRadioButton (String aLabel)
	{
		MRadioButton retVal = null;
		
		for (int i = 0; i < radioButtons.length && retVal == null; i++)
		{
			if (radioButtons [i].getLabel ().equals (aLabel))
			{
				retVal = radioButtons [i];
			}
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRadioButtonCount
	// ----------------------------------------------------------------------
	
	/** Returns the number of radio buttons.
	  *
	  * @return The radio button count.
	  * 
	  */
	  
	public int getRadioButtonCount ()
	{
		return radioButtonCount;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: actionPerformed
	// ----------------------------------------------------------------------
	
	/** Event handler for switching the selected button.
	  *
	  * @invisible
	  * 
	  */
	  
	public void actionPerformed(MActionEvent aEvent)
	{
		setSelectedButtonIndex (((MRadioButton) aEvent.getSource ()).getButtonIndex ());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: notifyListeners
	// ----------------------------------------------------------------------
	
	private void notifyListeners(MActionEvent aEvent)
	{
		Enumeration theEnumeration = actionListeners.elements ();
		while (theEnumeration.hasMoreElements ())
		{
			((MActionListener) theEnumeration.nextElement ()).
				actionPerformed (aEvent);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectListOptions
	// ----------------------------------------------------------------------
	
	/** Returns a vector of strings containing the labels of each list 
	  * item.
	  *
	  * @return A vector of strings containing the labels of each list 
	  *            item.
	  * @invisible
	  * 
	  */
	  
	public Vector getSelectListOptions()
	{
		Vector retVal = new Vector ();
		for (int i = 0; i < radioButtonCount; i++)
		{
			retVal.addElement (radioButtons [i].getName ());
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
	/** Returns WML safe string.
		* @invisible
	  * 
	  */
	  
	public String getPostValue ()
	{

	  	return "$(IDz" + getComponentID () + ")";//":e)";
	}

  
	// ----------------------------------------------------------------------
	// METHOD: createEvent
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public MauiEvent createEvent (String aStateData)
	{
		MActionEvent retVal = null;
		if (aStateData != null && aStateData.startsWith ("selected_"))
		{
			int theIndex = Integer.parseInt (aStateData.substring (9));
			doSetSelectedButtonIndex (theIndex, true);
			retVal = (MActionEvent) getSelectedButton ().createEvent ("true");
		}
		else
		{
			retVal = new MActionEvent (this, MActionEvent.ACTION_CHECKED);
		}
		
		return retVal;
	}
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF