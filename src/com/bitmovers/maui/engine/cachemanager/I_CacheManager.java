// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;

/**
* I_CacheManager <p>
* This interface describes the base functionality required for the cache repository
* object.
*
* <-- Suggestion --> Use JNDI for naming cache domains, plus associating default
* attributes with them.
*
*/
public interface I_CacheManager
	extends I_Named,
			I_LoadFactorable,
			I_LoadFactorEventSource,
			I_RepositoryEventSource,
			I_OfflineStorage
{
	/**
	* Create a cache domain.  If a cache domain by the same name already exists, then a reference
	* to it will be returned.  Otherwise, a new domain will be created.
	*
	* @param aAddress The address of the cache domain.
	*
	* @return A new I_CacheDomain, or a reference to an already existing one.
	*/
	public I_CacheDomain create (Object aAddress);

	/**
	* Create a cache domain.  If a cache domain by the same name already exists, then a reference
	* to it will be returned.  Otherwise, a new domain will be created.
	*
	* @param aAddress The address of the cache domain.
	* @param aConfigurationAddress A starting reference address to the Server Configuration Manager.
	*							   If this is null, then no Server Configuration access will be done.
	* @param aProperties A list of property names to use for default initialization of any
						 I_CacheObjects created within this domain.
	*
	* @return A new I_CacheDomain, or a reference to an already existing one.
	*/
	public I_CacheDomain create (Object aAddress,
								 Object aConfigurationAddress,
								 Object [] aProperties);
	
	/**
	* Locate a cache domain.
	*
	* @param aAddress The address of the cache domain.
	*
	* @return The I_CacheDomain object, or null if it isn't found
	*/
	public I_CacheDomain locate (Object aAddress);
	
	/**
	* Destroy a cache domain.
	*
	* @param aAddress The address of the cache domain.
	*
	* @return The I_CacheDomain object just destroyed, or null if it doesn't exist
	*/
	public I_CacheDomain destroyCacheDomain (Object aAddress);
	
}