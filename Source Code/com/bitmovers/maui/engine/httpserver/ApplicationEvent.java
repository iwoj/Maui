// =============================================================================
// com.bitmovers.maui.httpserver.ApplicationEvent
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import java.util.EventObject;
import com.bitmovers.maui.MauiApplication;

/**
* ApplicationEvent <p>
* This EventObject describes an Application event
*
* @invisible
*/
public class ApplicationEvent extends TimedEvent
{	
	/**
	* Simple constructor
	*
	* @param aSource The HTTPSession object
	*/
	public ApplicationEvent (Object aSource)
	{
		super (aSource);
	}

	/**
	* Simple constructor
	*
	* @param aSource The HTTPSession object
	* @param aEventTime The time of the event
	*/
	public ApplicationEvent (Object aSource,
							 long aEventTime)
	{
		super (aSource, aEventTime);
	}

	/**
	* Get the MauiApplication
	*
	* @return The MauiApplication object
	*/
	public MauiApplication getMauiApplication ()
	{
		return (MauiApplication) getSource ();
	}
}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof