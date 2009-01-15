package com.bitmovers.maui.engine.logmanager;


// ========================================================================
// INTERFACE: LogListener                        (c) 2001 Bitmovers Systems
// ========================================================================

/** The LogListener interface allows implementing classes to listen to
  * LogManager messages.	
  * 
  */

public interface LogListener
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: processLogMessage
	// ----------------------------------------------------------------------
	
	/** This method is called on all registered <code>LogListener</code>s 
	  * when a new log message is reported.
	  * 
	  * @param  message  The message being reported to the maui log.
	  *
	  */
	  
	public void processLogMessage(String message);
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF