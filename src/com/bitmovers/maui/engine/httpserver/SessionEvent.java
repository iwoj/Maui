// =============================================================================
// com.bitmovers.maui.httpserver.HTTPSession
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import java.util.EventObject;
import com.bitmovers.maui.MauiApplication;

/**
* SessionEvent <p>
* This EventObject describes an HTTPSession event
*
* @invisible
*/
public class SessionEvent extends TimedEvent
{

	final private MauiApplication mauiApplication;
	
	/**
	* Simple constructor
	*
	* @param aSource The HTTPSession object
	* @param aMauiApplication The associated MauiApplication object (or null)
	*/
	public SessionEvent (Object aSource,
						 MauiApplication aMauiApplication)
	{
		super (aSource);
		mauiApplication = aMauiApplication;
	}

	/**
	* Simple constructor
	*
	* @param aSource The HTTPSession object
	* @param aEventTime The time of the event
	* @param aMauiApplication The associated MauiApplication object (or null)
	*/
	public SessionEvent (Object aSource,
						 long aEventTime,
						 MauiApplication aMauiApplication)
	{
		super (aSource, aEventTime);
		mauiApplication = aMauiApplication;
	}

	/**
	* Get the MauiApplication
	*
	* @return The MauiApplication object
	*/
	public MauiApplication getMauiApplication ()
	{
		return mauiApplication;
	}
}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof