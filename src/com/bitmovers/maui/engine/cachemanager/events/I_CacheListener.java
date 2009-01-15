// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager.events;
import java.util.EventListener;

/**
* I_CacheListener <p>
* This interface describes a repository event listener
*
*/
public abstract interface I_CacheListener extends EventListener
{
	/**
	* Event codes - This is used to categorize an event
	*/
	public static final int EVENTCODE_CACHEDOMAIN = 1;
	public static final int EVENTCODE_CACHEOBJECT = 2;
	public static final int EVENTCODE_LOADFACTOR = 3;
	public static final int EVENTCODE_OFFLINE = 4;
	public static final int EVENTCODE_PROPERTY = 5;
	public static final int EVENTCODE_PROPERTYCHANGE = 6;
	public static final int EVENTCODE_REPOSITORY = 7;
	
	/**
	* Event types - This describes what the event actually is
	*/
	public static final int EVENTTYPE_ADD = 1;
	public static final int EVENTTYPE_REMOVE = 2;
	public static final int EVENTTYPE_CHANGE = 3;
	public static final int EVENTTYPE_CREATE = 4;
	public static final int EVENTTYPE_DESTROY = 5;
	public static final int EVENTTYPE_TAKEN = 6;
	public static final int EVENTTYPE_RELEASED = 7;
	public static final int EVENTTYPE_CACHEOUT = 8;
	public static final int EVENTTYPE_CACHEIN = 9;
	public static final int EVENTTYPE_ADJUST = 10;
	
	/**
	* Notify the listener of a change in some component of the cache manager
	*
	* @param aCacheEvent A CacheEvent object which describes the event
	*/
	public void notify (A_CacheEvent aCacheEvent);
}