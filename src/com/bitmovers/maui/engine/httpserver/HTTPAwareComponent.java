// =============================================================================
// com.bitmovers.maui.httpserver.HTTPAwareComponent
// =============================================================================

package com.bitmovers.maui.engine.httpserver;


// =============================================================================
// INTERFACE: HTTPAwareComponent
// =============================================================================

/**
* This interface provides an abstraction for objects which, among other things,
* are capable of processing HTTP messages
*/
public interface HTTPAwareComponent
{
	// ---------------------------------------------------------------------------
	
	/**
	* Process an actual HTTPEvent.
	*
	* @param httpEventDetails The String containing the HTTP data
	*/
  public void processHTTPEvent(String httpEventDetails);
	
	// ---------------------------------------------------------------------------
}


// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof