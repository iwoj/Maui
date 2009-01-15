// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;

/**
* I_CacheObject <p>
* This is for the object which holds and maintains the "cachable" user object.
*
*/
public interface I_CacheObject
	extends I_Named,
			I_CacheObjectEventSource,
			I_OfflineStorage,
			I_CanDestroy
{
	/**
	* Is this object taken?
	*
	* @return Boolean indicating if the object is taken or not
	*/
	public boolean isTaken ();
	
	/**
	* Take the user object
	*
	* @return The user object
	*/
	public Object take ();
	
	/**
	* Get the user object (ie. don't mark it as taken
	*
	* @return The "untaken" userobject
	*/
	public Object get ();
	
	/**
	* Get the address of this object
	*
	* @return The address
	*/
	public Object getAddress ();
	
	/**
	* Get the Cache Domain for this Cache Object
	*
	* @return The I_CacheDomain object
	*/
	public I_CacheDomain getCacheDomain ();
	
	/**
	* Put a new user object (replacing the previous one)
	*
	* @param aObject The user object
	*/
	public void put (Object aUserObject);
	
	/**
	* Release the user object.  This is done after a get, to indicate to the
	* cache manager that the user object is a candidate for caching out.
	*/
	public void release ();
	
	/**
	* Is the object cached out?
	*
	* @return Boolean indicating if the object is cached out or not
	*/
	public boolean isCachedOut ();
}
