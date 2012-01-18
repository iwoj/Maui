// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.io.Serializable;

/**
* I_CacheDomain <p>
* This interface describes the base functionality required for the cache domain
* objects.
*
* <-- Suggestion --> Allow the use of JNDI for naming cache objects, plus associating
* default attributes with them.
*
*/
public interface I_CacheDomain
	extends I_Named,
			I_CacheDomainEventSource,
			I_LoadFactorable,
			I_LoadFactorEventSource,
			//I_HasProperties,
			I_OfflineStorage,
			I_CanDestroy
{
	/**
	* Get the Cache Repository for this Cache Domain
	*
	* @return The I_CacheManager
	*/
	public I_CacheManager getCacheManager ();
	
	/**
	* Create an I_CacheObject in this I_CacheDomain.  If it already exists, then a
	* reference to the existing one will be returned.
	*
	* @param aAddress An address for the object
	* @param aUserObject The user object (this can be null, and filled in at a later time)
	*
	* @return An I_CacheObject representation of the user object
	*/
	public I_CacheObject create (Object aAddress,
								 Object aUserObject);
						
	/**
	* Create an I_CacheObject in this I_CacheDomain.  If it already exists, then a
	* reference to the existing one will be returned.
	*
	* @param aAddress An address for the object
	* @param aProperties An array of property identifiers for attributes to be associated
	*					 with this object
	* @param aUserObject The user object (this can be null, and filled in at a later time)
	*
	* @return An I_CacheObject representation of the user object
	*/
	public I_CacheObject create (Object aAddress,
								 Object [] aProperties,
								 Object aUserObject);
						
	/**
	* Create a user object and an I_CacheObject in this I_CacheDomain.  Prior to this a
	* class profile definition must be introduced via the "putCacheObjectProfile" method.
	* When the user object is created it will first be initialized from a composite of any
	* configuration parameters discovered through the Server Configuration Manager and the
	* profile object presented to "putCacheObjectProfile".
	*
	* @param aAddress An address for the object
	* @param aObjectClass The class of the object to be create
	*
	* @return An I_CacheObject representation of the user object
	*/
	public I_CacheObject create (Object aAddress,
								 Class aObjectClass);
								 
	/**
	* Destroy this Cache Domain
	*
	*/
	public void destroy ();
						
	/**
	* Remove an I_CacheObject from this I_CacheDomain.
	*
	* @param aAddress An address for the object
	*
	* @return The deleted I_CacheObject, or null if it wasn't found
	*/
	public I_CacheObject remove (Object aAddress);
	
	
	/**
	* Locate an I_CacheObject
	*
	* @param aAddress An address for the object
	*
	* @return The locaed I_CacheObject, or null if it wasn't found
	*/
	public I_CacheObject locate (Object aAddress);
	
	/**
	* Put a profile for a cache object.  This is used to bind initial properties and values
	* with cache object when they are created.  If the profile has already been added, it
	* be replaced.
	*
	* @param aObjectClass The Class for the object
	* @param aProfile An arbitrary object which is used for initializing the I_CacheObject
	*
	* @return The replaced profile object, or the profile passed in if it is a new profile
	*/
	public Object putCacheObjectProfile (Class aObjectClass,
									     Object aProfile);
	
	/**
	* Remove a profile for a cache object.  This is used to bind initial properties and values
	* with cache object when they are created.
	*
	* @param aObjectClass The Class for the object
	*
	* @return The removed profile object
	*/
	public Object removeCacheObjectProfile (Class aObjectClass);
						
}
