package com.bitmovers.maui.engine.logmanager;


// ========================================================================
// INTERFACE: DetailLogListener                  (c) 2001 Bitmovers Systems
// ========================================================================

/** The DetailLogListener interface allows implementing classes to listen to
  * all LogManager messages.	
  * 
  */

public interface DetailLogListener
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
	  
	public void processDetailLogMessage(String message);
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF