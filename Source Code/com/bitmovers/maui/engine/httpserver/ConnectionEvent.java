// =============================================================================
// com.bitmovers.maui.httpserver.ConnectionEvent
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import java.util.EventObject;

/**
* ConnectionEvent <p>
* This EventObject describes a ConnectionEvent
*
* @invisible
*/
public class ConnectionEvent extends TimedEvent
{
	/**
	* Simple constructor
	*
	* @param aSource The HTTPSession object
	*/
	public ConnectionEvent (HTTPConnection aSource,
							long aEventTime)
	{
		super (aSource, aEventTime);
	}

	/**
	* Simple constructor
	*
	* @param aSource The HTTPSession object
	*/
	public ConnectionEvent (HTTPConnection aSource)
	{
		super (aSource);
	}
	
	/**
	* Get the HTTPConnection
	*
	* @return The HTTPConnection object
	*/
	public HTTPConnection getHTTPConnection ()
	{
		return (HTTPConnection) getSource ();
	}
}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof