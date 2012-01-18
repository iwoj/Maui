package com.bitmovers.maui.components.foundation;

import com.bitmovers.maui.events.MauiEvent;


// ========================================================================
// INTERFACE: Settable                           (c) 2001 Bitmovers Systems
// ========================================================================

/** Interface for settable components.
  * 
  */
  
public interface Settable
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: setValue
	// ----------------------------------------------------------------------
	
	/** Set the value for this component
	  *
	  * @param aValue A value Object
	  * 
	  */
	  
	public void setValue (Object aValue);
	
	
	// ----------------------------------------------------------------------
	// METHOD: getValue
	// ----------------------------------------------------------------------
	
	/** Get the value for this component
	  *
	  * @return The value Object for the component
	  * 
	  */
	  
	public Object getValue ();
	
	
	// ----------------------------------------------------------------------
	// METHOD: createEvent
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
	  
	public MauiEvent createEvent (String aStringData);
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF