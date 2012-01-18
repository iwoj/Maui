// =============================================================================
// com.bitmovers.maui.components.foundation.MCheckBox
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MCheckBox                              (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>MCheckBox</code> class represents a checkbox, that is, an element which allows
  * one to toggle between two states. An example of where one might be used
  * would be:
  *
  * <pre>
  *  [ ] Check this box if you wish to receive updates.
  * </pre>
  *
  * When clicked, a checkbox will publish a <code>MActionEvent</code> with 
  * the <code>MCheckBox.ACTION_CHECKED</code> event command string.
  * 
  */

public class MCheckBox extends MSettable implements HasPostValue, HasLabel
{
	
	// The value for MCheckBox's should either be the value of TRUE or FALSE. In
	// the case of HTML, the value is only sent when the box is checked, so the
	// value needs to be TRUE. In WML, the MCheckBox may be a select list, in
	// which case both values (TRUE and FALSE) would be used, as either could be
	// sent back.

	public static final String TRUE = "Yes";
	public static final String FALSE = "No";

	private String label = null;
	private String value = null;
	private boolean checked = false;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a basic <code>MCheckBox</code> with no label.
	  *
	  */
	
	public MCheckBox()
	{
		this("");
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs <code>MCheckBox</code> using the specified label.
	  *
	  * @param label A string to appear on the rendered checkbox.
	  */
	
	public MCheckBox(String label)
	{
		this(label, false);
	}


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs an <code>MCheckBox</code> using the specified label, value, and default
	  * checked state. The boolean value <code>true</code> indicates the state of the 
	  * checkbox as checked, <code>false</code> otherwise.
	  *
	  * @param label A string to appear on the rendered checkbox.
	  *
		* @param checked A boolean value to indicate the state of the checkbox.
	  */
	
	public MCheckBox(String label, boolean checked)
	{
		super.generateUniqueName();
		
		this.setLabel(label);
		super.setValue(MCheckBox.TRUE);
		this.setChecked(checked);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setLabel
	// ----------------------------------------------------------------------
	
	/** Sets the label of the checkbox.
	  *
	  * @param label A string indicating the text associated with the checkbox.
	  */
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getLabel
	// ----------------------------------------------------------------------
	
	/** Returns the label of the checkbox.
	  *
	  * @return The text associated with the checkbox.
	  */
	
	public String getLabel()
	{
		return this.label;
	}


	// ----------------------------------------------------------------------
	// METHOD: setChecked
	// ----------------------------------------------------------------------
	
	/** Sets the state of the <code>MCheckBox</code>. Passing <code>true</code> will check the
	  * <code>MCheckBox</code>, and <code>false</code> will uncheck it.
	  *
	  * @param checked A boolean value of either <code>true</code> or <code>false</code>, depending whether to check or
	  *								 uncheck the <code>MCheckBox</code>. 	
	  */
	
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}


	// ----------------------------------------------------------------------
	// METHOD: isChecked
	// ----------------------------------------------------------------------
	
	/** Checks the state of the <code>MCheckBox</code>.  
	  *
	  * @return <code>true</code> if checkbox is checked, <code>false</code> otherwise.
	  */
	
	public boolean isChecked()
	{
		return this.checked;
	}
	


	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
	/** Returns a <code>WML</code> post value string for use in rendering 
		* (<code>WMLSafeComponentID</code>).
	  *
	  * @return A <code>WML</code> safe string as post value.
	  * @invisible
	  * 
	  */
	
	public String getPostValue()
	{
	  	return "$(" + getWMLSafeComponentID () + ")";//":e)";
	}
	
	// ----------------------------------------------------------------------
	// METHOD: createEvent
	// ----------------------------------------------------------------------
	
	/** Overrides <code>MComponent.createEvent()</code> by updating the state of the checkbox
	  * instance with the given state data. A valid event is returned if a change 
	  * occured. Otherwise, a consumed event is returned.
	  *
	  * @param aStateData A string indicating the state change of the checkbox. <code>null</code>
	  *                   indicates the checkbox was unchecked.
	  *
	  * @return A <code>MauiEvent</code> indicating state change.
	  * @invisible
	  *
	  */
	  
	public MauiEvent createEvent(String aStateData)
	{
		// Some technologies like HTML only submit the value of a checkbox if it is
		// checked. If it unchecked no data is sent. If an event was registered by
		// the client but no stateData was sent (null), we can assume that the user
		// unchecked the box.
		MActionEvent retVal = null;
		
		if (aStateData != null && aStateData.equals (MActionEvent.ACTION_PUSH))
		{
			//
			//	This is a deep navigation event (from a small form factor device)
			//
			retVal = new MActionEvent (this, aStateData);
		}
		else
		{
			boolean theState = (aStateData == null ||
								aStateData.length () == 0 ||
								aStateData.equals (FALSE) ?
									false :
									true);
									
			retVal = new MActionEvent(this, (theState ? MActionEvent.ACTION_CHECKED :
														MActionEvent.ACTION_UNCHECKED));
			
			
			if (//aStateData != null &&
				theState != isChecked())
			{
				this.setChecked(theState);
			}
			else
			{
				retVal.consume();
			}
		}
		return retVal;
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF