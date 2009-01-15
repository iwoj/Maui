// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import com.bitmovers.maui.engine.cachemanager.events.I_RepositoryListener;

/**
* I_RepositoryEventSource <p>
* This interface describes the methods required for an object which can fire
* RepositoryEvents.
*
*/
public interface I_RepositoryEventSource extends I_CacheEventSource
{
	/**
	* Add an I_RepositoryListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to add
	*/
	public void addRepositoryListener (I_RepositoryListener aRepositoryListener);
	
	/**
	* Remove an I_RepositoryListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_RepositoryListener, or null if it wasn't a listener
	*/
	public I_RepositoryListener removeRepositoryListener (I_RepositoryListener aRepositoryListener);
}