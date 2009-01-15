// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager.events;
import java.util.EventObject;

/**
* CacheDomainEvent <p>
* Event Object class which describes a load factor event
*
*/
public class CacheDomainEvent extends A_CacheEvent
{
	/**
	* Simple constructor
	*
	* @param aEventSource The event source
	* @param aEventCode The event code
	* @param aEventType The event type
	* @param aArbitrary The arbitrary Object associated with the event.
	*/
	public CacheDomainEvent (Object aSource,
						    int aEventCode,
						    int aEventType,
						    Object aArbitrary)
	{
		super (aSource, aEventCode, aEventType, aArbitrary);
	}
}