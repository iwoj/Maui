// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;

/**
* I_CacheStrategy <p>
* This interface is for an object which implements the caching strategy to use for
* the repository and/or the cache domains.
*
*/
public interface I_CacheStrategy
	extends I_Named,
			Runnable
{
	/**
	* Initialize the strategy
	*
	* @param aInternalCacheDomain The I_InternalCacheDomain object (internal representation of
	*							  I_CacheDomain)
	* @param aCacheManager The I_CacheManager object
	*/
	public void initialize (I_InternalCacheDomain aInternalCacheDomain,
							I_CacheManager aCacheManager);
}
