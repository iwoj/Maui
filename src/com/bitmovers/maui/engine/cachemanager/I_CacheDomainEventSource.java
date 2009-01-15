// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheDomainListener;

/**
* I_CacheDomainEventSource <p>
* This interface describes the methods required for an object which can fire
* CacheDomainEvents.
*
*/
public interface I_CacheDomainEventSource extends I_CacheEventSource
{
	/**
	* Add an I_CacheDomainListener object
	*
	* @param aRepositoryListener The I_CacheDomainListener to add
	*/
	public void addCacheDomainListener (I_CacheDomainListener aCacheDomainListener);
	
	/**
	* Remove an I_CacheDomainListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_CacheDomainListener, or null if it wasn't a listener
	*/
	public I_CacheDomainListener removeCacheDomainListener (I_CacheDomainListener aCacheDomainListener);
	
}