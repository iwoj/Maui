// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import com.bitmovers.maui.engine.cachemanager.events.I_LoadFactorListener;

/**
* I_LoadFactorEventSource <p>
* This interface describes the methods required for an object which can fire
* LoadFactorEvents.
*
*/
public interface I_LoadFactorEventSource extends I_CacheEventSource
{
	/**
	* Add an I_LoadFactorListener object
	*
	* @param aRepositoryListener The I_LoadFactorListener to add
	*/
	public void addLoadFactorListener (I_LoadFactorListener aLoadFactorListener);
	
	/**
	* Remove an I_LoadFactorListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_LoadFactorListener, or null if it wasn't a listener
	*/
	public I_LoadFactorListener removeLoadFactorListener (I_LoadFactorListener aLoadFactorListener);
	
}