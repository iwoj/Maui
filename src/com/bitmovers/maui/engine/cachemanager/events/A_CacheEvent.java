// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager.events;
import java.util.EventObject;

/**
* A_CacheEvent <p>
* Event Object class which describes any event within the cache manager
*
*/
public abstract class A_CacheEvent extends EventObject
{
	/**
	* A code which describes the event
	*/
	protected final int eventCode;
	
	/**
	* The event type (eg. add, remove, change, etc.)
	*/
	protected final int eventType;
	
	/**
	* Arbitrary information associated with the event
	*/
	protected final Object arbitrary;
	
	/**
	* Simple constructor
	*
	* @param aEventSource The event source
	* @param aEventCode The event code
	* @param aEventType The event type
	* @param aArbitrary The arbitrary Object associated with the event.
	*/
	public A_CacheEvent (Object aSource,
						 int aEventCode,
						 int aEventType,
						 Object aArbitrary)
	{
		super (aSource);
		eventCode = aEventCode;
		eventType = aEventType;
		arbitrary = aArbitrary;
	}
	
	/**
	* Get the event code
	*
	* @return The event code
	*/
	public int getEventCode ()
	{
		return eventCode;
	}
	
	/**
	* Get the event type
	*
	* @return The event type
	*/
	public int getEventType ()
	{
		return eventType;
	}
	
	/**
	*
	* Get the aribtrary Object associated with the event
	*
	* @return The arbitrary object
	*/
	public Object getArbitraryObject ()
	{
		return arbitrary;
	}
}