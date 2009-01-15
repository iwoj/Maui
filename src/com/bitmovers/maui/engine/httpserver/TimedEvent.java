// =============================================================================
// com.bitmovers.maui.httpserver.TimedEvent
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import java.util.EventObject;
import com.bitmovers.maui.MauiApplication;

/**
* TimedEvent <p>
* This EventObject keeps a time for the event
*
* @invisible
*/
public class TimedEvent extends EventObject
{
	private final long eventTime;
	
	public TimedEvent (Object aSource)
	{
		super (aSource);
		eventTime = System.currentTimeMillis ();
	}
	
	public TimedEvent (Object aSource, long aEventTime)
	{
		super (aSource);
		eventTime = aEventTime;
	}
	
	public long getEventTime ()
	{
		return eventTime;
	}
}
