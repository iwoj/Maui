// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.io.Serializable;

/**
* I_CacheProcesing <p>
* If a user object implements this interface, then the "load" and "unload" methods will
* be called whenever the object is cached-in or cached-out.
*
*/
public interface I_CacheProcessing
{
	/**
	* This method is called whenever the object is cached-in.  This is useful for
	* reconstituting an object
	*
	* @param aCacheObject The I_CacheObject associated with this object
	*/
	public void load (I_CacheObject aCacheObject);
	
	/**
	* This method is called whenever the object is cached-out.  This is useful for
	* releasing resources which are being used by an object
	*
	* @param aCacheObject The I_CacheObject associated with this object
	*/
	public void unload (I_CacheObject aCacheObject);
}
	
	
