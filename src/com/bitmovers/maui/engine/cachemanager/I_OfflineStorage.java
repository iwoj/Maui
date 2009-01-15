// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;

/**
* I_OfflineStorage <p>
* This interface describes the methods required for an object which can provide the
* offline storage capabilites of the cache manager.
*
*/
interface I_OfflineStorage
	extends I_OfflineEventSource
{
	/**
	* Move an object out of the cache onto offline storage
	*
	* @param aObject The Object to be cached-out.
	*/
	void cacheOut (Object aObject);
	
	/**
	* Reload an object in offline storage to the cache
	*
	* @param aObject The Object to be cached in
	*
	* @return The removed I_CacheDomainListener, or null if it wasn't a listener
	*/
	void cacheIn (Object aObject);
}