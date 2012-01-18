package com.bitmovers.maui.events;

import java.io.*;
import java.net.*;
import java.util.*;


// ========================================================================
// CLASS: MauiEvent
// ========================================================================

/** This is the abstract superclass for all Maui event types. Currently, 
  * <code>MActionEvent</code> is its only subclass.
  *
  */
  
public abstract class MauiEvent extends EventObject
{
	
	
	private boolean consumed = false;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR: MauiEvent
	// ----------------------------------------------------------------------
	
	/** @param  source  The object upon which the event occured. This 
	  *                 reference is typically used for callbacks to check 
	  *                 the new state of the object.
	  *
	  */
	  
	public MauiEvent(Object source)
	{
	  super(source);
	}


	// ----------------------------------------------------------------------
	// METHOD: consume
	// ----------------------------------------------------------------------

	/** Consumes the event. This is used to stop propagation of the event.
	  * 
	  */
	  
	public void consume()
	{
	  this.consumed = true;
	}


	// ----------------------------------------------------------------------
	// METHOD: isConsumed
	// ----------------------------------------------------------------------

	/** @return  <code>true</code> if the event has been consumed, 
	  *          <code>false</code> if the event has not been consumed. 
	  *          Consumed events should be neither propagated nor accessed.
	  *
	  */
	  
	public boolean isConsumed()
	{
	  return this.consumed;
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF