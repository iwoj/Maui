// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import com.bitmovers.maui.engine.cachemanager.events.I_PropertyListener;


/**
* I_PropertyEventSource <p>
* This interface describes the methods required for an object which can fire
* PropertyEvents.
*
*/
public interface I_PropertyEventSource extends I_CacheEventSource
{
	/**
	* Add an I_PropertyListener object
	*
	* @param aRepositoryListener The I_PropertyListener to add
	*/
	public void addPropertyListener (I_PropertyListener aPropertyListener);
	
	/**
	* Remove an I_PropertyListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_PropertyListener, or null if it wasn't a listener
	*/
	public I_PropertyListener removePropertyListener (I_PropertyListener aPropertyListener);
}