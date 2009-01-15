package com.bitmovers.maui.components.foundation;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.events.MauiEvent;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.engine.ServerConfigurationManager;
import java.util.*;


// ========================================================================
// CLASS: MSettable                              (c) 2001 Bitmovers Systems
// ========================================================================

/** This abstract class handles common activities for components which are 
  * user settable.
  *
  */
  
public abstract class MSettable extends MComponent
                             implements Settable
{
	
	
	private Object value;
	private final String actionEvent;
	
  
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	protected MSettable (String aActionEvent)
	{
		actionEvent = aActionEvent;
	}
	
  
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	protected MSettable ()
	{
		actionEvent = null;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: setValue
	// ----------------------------------------------------------------------
	
	/** Set a value
	  *
	  * @param aValue The value to set
	  * 
	  */
	
	public void setValue (Object aValue)
	{
		//++ ?? MW 2001.08.15
		// check if the aValue value is null before proceeding with the setValue operation.
		if (aValue != null)
		{
			if (!doEqualityCheck (value, aValue))
			{
				doSetValue (aValue);
				MauiEvent theEvent = doCreateEvent (aValue.toString ());
				if (theEvent instanceof MActionEvent)
				{
					dispatchActionEvent((MActionEvent) theEvent);
				}
			}
		}
		//-- ??
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: doSetValue
	// ----------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	  
	protected void doSetValue (Object aValue)
	{
		invalidate ();
		value = aValue;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: getValue
	// ----------------------------------------------------------------------
	
	/** Get the value
	  *
	  * @return The  value
	  * 
	  */
	  
	public Object getValue ()
	{
		return value;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: createEvent
	// ----------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	
	public MauiEvent createEvent (String aStateData)
	{
		return (doEqualityCheck (value, aStateData) ?
					super.createEvent (aStateData) :
					doCreateEvent (aStateData));
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: doCreateEvent
	// ----------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	  
	protected MauiEvent doCreateEvent (String aStateData)
	{
		doSetValue (aStateData);
		MActionEvent retVal = new MActionEvent (this, (actionEvent == null ? MActionEvent.VALUE_CHANGED : actionEvent));
		//if (aStateData == null)
		//{
		//	retVal.consume ();
		//}
		return retVal;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: doEqualityCheck
	// ----------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	  
	protected boolean doEqualityCheck (Object aValue, Object aStateData)
	{
		boolean retVal = false;
		
		if (aValue == null)
		{
			retVal = (aStateData == null);
		}
		else if (aStateData != null)
		{
			retVal = aValue.equals (aStateData);
		}
		return retVal;
	}
  
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	  
	public void fillParserValues()
	{
		super.fillParserValues();
		parser.setVariable("value", (value == null ? "" : value.toString ()));
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF