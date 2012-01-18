// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import com.bitmovers.maui.engine.cachemanager.events.I_OfflineListener;

/**
* I_OfflineEventSource <p>
* This interface describes the methods required for an object which can fire
* OfflineEvents.
*
*/
public interface I_OfflineEventSource extends I_CacheEventSource
{
	/**
	* Add an I_OfflineListener object
	*
	* @param aRepositoryListener The I_OfflineListener to add
	*/
	public void addOfflineListener (I_OfflineListener aOfflineListener);
	
	/**
	* Remove an I_OfflineListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_OfflineListener, or null if it wasn't a listener
	*/
	public I_OfflineListener removeOfflineListener (I_OfflineListener aOfflineListener);
}