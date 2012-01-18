// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheListener;

/**
* I_CacheEventSource <p>
* This abstract interface is used to group all event sources together
*
*/
public abstract interface I_CacheEventSource
{
	/**
	* Add a cache listener.
	*
	* @param aCacheListener An I_CacheListener object
	*/
	public void addCacheListener (I_CacheListener aCacheListener);
	
	public I_CacheListener removeCacheListener (I_CacheListener aCacheListener);
}