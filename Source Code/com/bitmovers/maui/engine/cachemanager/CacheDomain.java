// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;

import java.util.HashMap;
import com.bitmovers.maui.engine.cachemanager.events.I_OfflineListener;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheListener;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheDomainListener;
import com.bitmovers.maui.engine.cachemanager.events.I_LoadFactorListener;
import com.bitmovers.maui.engine.cachemanager.events.OfflineEvent;
import com.bitmovers.maui.engine.cachemanager.events.CacheDomainEvent;

/**
* CacheDomain <p>
* This class describes the base functionality required for the cache domain
* objects.
*
* <-- Suggestion --> Allow the use of JNDI for naming cache objects, plus associating
* default attributes with them.
*
*/
public class CacheDomain extends CacheEventSource implements I_InternalCacheDomain
{
	private String name;
	
	protected HashMap cacheObjects = new HashMap (10);
	
	private LoadFactorable loadFactorable;
	private CacheEventSource cacheOutListeners;
	private I_CacheStrategy cacheStrategy = null;
	private I_CacheManager CacheManager;
	
	protected CacheDomain (I_CacheManager aCacheManager,
						   String aName)
	{
		super ();
		name = aName;
		CacheManager = aCacheManager;
		try
		{
			cacheOutListeners = new CacheEventSource (OfflineEvent.class);
			setEventClass (CacheDomainEvent.class);
			loadFactorable = new LoadFactorable ();
		}
		catch (Exception e)
		{
			loadFactorable = null;
			cacheOutListeners = null;
		}
	}
	
	protected CacheDomain (I_CacheManager aCacheManager,
						   String aName,
						   Object aConfigurationAddress,
						   Object [] aProperties)
	{
		this (aCacheManager, aName);
		
		//
		//	Forget about the configuration address and properties
		//
	}
	
	private void notifyListeners (int aEventType, Object aArbitraryObject)
	{
		try
		{
			notifyListeners (this,
							 I_CacheListener.EVENTCODE_CACHEDOMAIN,
							 aEventType,
							 aArbitraryObject);
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	* Get the object's name
	*
	* @return The name
	*/
	public String getName ()
	{
		return name;
	}
	
	/**
	* Create an I_CacheObject in this I_CacheDomain.  If it already exists, then a
	* reference to the existing one will be returned.
	*
	* @param aAddress An address for the object
	* @param aUserObject The user object (this can be null, and filled in at a later time)
	*
	* @return An I_CacheObject representation of the user object
	*/
	public synchronized I_CacheObject create (Object aAddress,
											  Object aUserObject)
	{
		I_CacheObject retVal = (I_CacheObject) cacheObjects.get (aAddress);
		if (retVal == null)
		{
			try
			{
				retVal = new CacheObject (this, aAddress, aUserObject);
				cacheObjects.put (aAddress, retVal);
				notifyListeners (I_CacheListener.EVENTTYPE_CREATE,
								 retVal);
			}
			catch (NoSuchMethodException e)
			{
			}
		}
		return retVal;
	}
	
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
								 Object aUserObject)
	{
		return create (aAddress, aUserObject);
	}
												
	/**
	* Create a user object and an I_CacheObject in this I_CacheDomain.
	*
	* @param aAddress An address for the object
	* @param aObjectClass The class of the object to be create
	*
	* @return An I_CacheObject representation of the user object
	*/
	public I_CacheObject create (Object aAddress,
								 Class aObjectClass)
	{
		return null;	// Not being used for now
	}
						
								 
	/**
	* Destroy this Cache Domain
	*
	*/
	public synchronized void destroy ()
	{
		Object [] theCacheObjects = cacheObjects.values ().toArray ();
		for (int i = 0; i < theCacheObjects.length; i++)
		{
			((I_CanDestroy) theCacheObjects [i]).destroy ();
			notifyListeners (I_CacheListener.EVENTTYPE_DESTROY,
							 theCacheObjects [i]);
		}
	}
						
	/**
	* Remove an I_CacheObject from this I_CacheDomain.
	*
	* @param aAddress An address for the object
	*
	* @return The deleted I_CacheObject, or null if it wasn't found
	*/
	public synchronized I_CacheObject remove (Object aAddress)
	{
		I_CacheObject retVal = (I_CacheObject) cacheObjects.remove (aAddress);
		if (retVal != null)
		{
			notifyListeners (I_CacheListener.EVENTTYPE_REMOVE,
							 retVal);
		}
		return retVal;
	}
	
	
	/**
	* Locate an I_CacheObject
	*
	* @param aAddress An address for the object
	*
	* @return The locaed I_CacheObject, or null if it wasn't found
	*/
	public I_CacheObject locate (Object aAddress)
	{
		return (I_CacheObject) cacheObjects.get (aAddress);
	}
	
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
									     Object aProfile)
	{
		return null;	// Not being used
	}
	
	/**
	* Remove a profile for a cache object.  This is used to bind initial properties and values
	* with cache object when they are created.
	*
	* @param aObjectClass The Class for the object
	*
	* @return The removed profile object
	*/
	public Object removeCacheObjectProfile (Class aObjectClass)
	{
		return null;	// Not being used
	}
						
	/**
	* Get the Cache Repository for this Cache Domain
	*
	* @return The I_CacheManager
	*/
	public I_CacheManager getCacheManager ()
	{
		return CacheManager;
	}
	
	/**
	* Add an I_CacheDomainListener object
	*
	* @param aRepositoryListener The I_CacheDomainListener to add
	*/
	public void addCacheDomainListener (I_CacheDomainListener aCacheDomainListener)
	{
		addCacheListener (aCacheDomainListener);
	}
	
	/**
	* Remove an I_CacheDomainListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_CacheDomainListener, or null if it wasn't a listener
	*/
	public I_CacheDomainListener removeCacheDomainListener (I_CacheDomainListener aCacheDomainListener)
	{
		return (I_CacheDomainListener) removeCacheListener (aCacheDomainListener);
	}
	
	/**
	* Get a list of the names of the different load factors which are being managed
	* by this object
	*
	* @return The list of load factor names
	*/
	public String [] getLoadFactorNames ()
	{
		return loadFactorable.getLoadFactorNames ();
	}
	
	/**
	* Get the datatype for the load factor.
	*
	* @param aFactorname The name of the load factor
	*
	* @return Class representing the datatype for the load factor (or null, if it wasn't found)
	*/
	public Class getDataType (String aName)
	{
		return loadFactorable.getDataType (aName);
	}
	
	/**
	* Get the load factor value.  Since the meaning of a factor is open, the primitive
	* data type for the load factor could be anything (or even an Object).
	*
	* @param aFactorName The name of the load factor
	*
	* @return Object representing the load factor (or null, if the factor wasn't found)
	*/
	public Object getLoadFactor (String aName)
	{
		return loadFactorable.getLoadFactor (aName);
	}
	
	/**
	* Set a value for a load factor
	*
	* @param aFactorName The name of the load factor
	* @param aValue The object representing the new value
	*/
	public void setLoadFactor (String aName, Object aValue)
	{
		loadFactorable.setLoadFactor (aName, aValue);
	}
	
	/**
	* Adjust a load factor.  Rather than setting an absolute value for a load factor,
	* this method allows a load factor to be changed relative to its current value.  If
	* the load factor isn't found, then this is taken as an absolute value.
	*
	* @param aFactorname The name of the load factor
	* @param aValue The object representing the new value
	*/
	public void adjustLoadFactor (String aName, Object aValue)
	{
		loadFactorable.adjustLoadFactor (aName, aValue);
	}
	
	/**
	* Add an I_LoadFactorListener object
	*
	* @param aRepositoryListener The I_LoadFactorListener to add
	*/
	public void addLoadFactorListener (I_LoadFactorListener aLoadFactorListener)
	{
		loadFactorable.addLoadFactorListener (aLoadFactorListener);
	}
	
	/**
	* Remove an I_LoadFactorListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_LoadFactorListener, or null if it wasn't a listener
	*/
	public I_LoadFactorListener removeLoadFactorListener (I_LoadFactorListener aLoadFactorListener)
	{
		return loadFactorable.removeLoadFactorListener (aLoadFactorListener);
	}
	
	private void notifyCacheListeners (int aEventType,
									   Object aObject)
	{
		try
		{
			cacheOutListeners.notifyListeners (this,
											   I_CacheListener.EVENTCODE_CACHEDOMAIN,
											   aEventType,
											   aObject);
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	* Move an object out of the cache onto offline storage
	*
	* @param aCacheObject The I_CacheObject representing the object to be cached-out.
	*/
	public synchronized void cacheOut (Object aObject)
	{
		if (aObject != null)
		{
			I_CacheObject theCacheObject = (I_CacheObject) aObject;
			if (!theCacheObject.isCachedOut ())
			{
				theCacheObject.cacheOut (aObject);
				notifyCacheListeners (I_CacheListener.EVENTTYPE_CACHEOUT,
									  aObject);
			}
		}
	}
	
	/**
	* Reload an object in offline storage to the cache
	*
	* @param aObject The Object to be cached in.
	*
	* @return The removed I_CacheDomainListener, or null if it wasn't a listener
	*/
	public synchronized void cacheIn (Object aObject)
	{
		if (aObject != null)
		{
			I_CacheObject theCacheObject = locate (((I_CacheObject) aObject).getAddress ());
			if (theCacheObject != null && theCacheObject.isCachedOut ())
			{
				theCacheObject.cacheIn (theCacheObject);
				notifyCacheListeners (I_CacheListener.EVENTTYPE_CACHEIN,
									  theCacheObject);
			}
		}
		else
		{
			//
			//	Do caching sweep
		}
	}
	
	/**
	* Add an I_OfflineListener object
	*
	* @param aRepositoryListener The I_OfflineListener to add
	*/
	public void addOfflineListener (I_OfflineListener aOfflineListener)
	{
		cacheOutListeners.addCacheListener (aOfflineListener);
	}
	
	/**
	* Remove an I_OfflineListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_OfflineListener, or null if it wasn't a listener
	*/
	public I_OfflineListener removeOfflineListener (I_OfflineListener aOfflineListener)
	{
		return (I_OfflineListener) cacheOutListeners.removeCacheListener (aOfflineListener);
	}
	
	/**
	* Get the caching strategy for this object
	*
	* @return The I_CacheStrategy
	*/
	public I_CacheStrategy getCacheStrategy ()
	{
		return cacheStrategy;
	}
	
	/**
	* Set the cache strategy
	*
	* @param aCacheStrategy The I_CacheStrategy to set
	*/
	public void setCacheStrategy (I_CacheStrategy aCacheStrategy)
	{
		cacheStrategy = aCacheStrategy;
	}

}
