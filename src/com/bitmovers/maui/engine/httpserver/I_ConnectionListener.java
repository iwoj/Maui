// =============================================================================
// com.bitmovers.maui.httpserver.I_ConnectionListener
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import java.util.EventListener;

/**
* I_ConnectionListener INTERFACE <p>
* This interface is used to notify of HTTPConnection activity.
*
* @invisible
*/
public interface I_ConnectionListener
{
	/**
	* Notification of a new connection
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public void newConnection (ConnectionEvent aConnectionEvent);
	
	/**
	* Notification of a connection closing
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public void connectionClosed (ConnectionEvent aConnectionEvent);
	
	/**
	* Notification of the start of a request
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public void requestStarted (ConnectionEvent aConnectionEvent);
	
	/**
	* Notification of the end of a request
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public void requestCompleted (ConnectionEvent aConnectionEvent);
}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof