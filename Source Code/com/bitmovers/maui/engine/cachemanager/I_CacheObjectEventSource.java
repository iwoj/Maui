// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheObjectListener;

/**
* I_CacheObjectEventSource <p>
* This interface describes the methods required for an object which can fire
* CacheObjectEvents.
*
*/
public interface I_CacheObjectEventSource extends I_CacheEventSource
{
	/**
	* Add an I_CacheObjectListener object
	*
	* @param aCacheObjectListener The I_CacheObjectListener to add
	*/
	public void addCacheObjectListener (I_CacheObjectListener aCacheObjectListener);
	
	/**
	* Remove an I_CacheObjectListener object
	*
	* @param aCacheobjectListener The I_CacheObjectListener to remove
	*
	* @return The removed I_CacheObjectListener, or null if it wasn't a listener
	*/
	public I_CacheObjectListener removeCacheObjectListener (I_CacheObjectListener aCacheObjectListener);
}