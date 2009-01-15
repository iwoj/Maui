// =============================================================================
// com.bitmovers.maui.httpserver.HTTPSession
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import java.util.EventListener;

/**
* I_SessionListener INTERFACE <p>
* This interface is used to listen the HTTPSession level events
*
* @invisible
*/
public interface I_SessionListener
{
	/**
	* Notification of session creation
	*
	* @param aSessionEvent The event object describing the session 
	*/
	public void sessionCreated (SessionEvent aSessionEvent);
	
	/**
	* Notification of session deletion
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void sessionDeleted (SessionEvent aSessionEvent);
	
	/**
	* Notification of the addition of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationAdded (SessionEvent aSessionEvent);
	
	/**
	* Notification of the removal of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationRemoved (SessionEvent aSessionEvent);
		
}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof