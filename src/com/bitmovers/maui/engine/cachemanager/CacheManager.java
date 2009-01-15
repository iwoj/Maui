// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.util.HashMap;

import com.bitmovers.maui.engine.cachemanager.events.I_CacheListener;
import com.bitmovers.maui.engine.cachemanager.events.I_RepositoryListener;
import com.bitmovers.maui.engine.cachemanager.events.I_OfflineListener;
import com.bitmovers.maui.engine.cachemanager.events.I_LoadFactorListener;
import com.bitmovers.maui.engine.cachemanager.events.LoadFactorEvent;
import com.bitmovers.maui.engine.cachemanager.events.OfflineEvent;
import com.bitmovers.maui.engine.cachemanager.events.RepositoryEvent;
import com.bitmovers.maui.engine.logmanager.*;


/**
* CacheManager <p>
* This class implements the base functionality required for the cache repository
* object.
*
* <-- Suggestion --> Use JNDI for naming cache domains, plus associating default
* attributes with them.
*
*/
public class CacheManager extends CacheEventSource implements I_CacheManager
{
	private static final I_CacheManager CacheManager = new CacheManager ();
	private HashMap cacheDomains = new HashMap (5);
	private CacheEventSource cacheOutListeners;
	private LoadFactorable loadFactorable;
		
	private CacheManager ()
	{
		super ();
		try
		{
			loadFactorable = new LoadFactorable ();
			setEventClass (RepositoryEvent.class);
			cacheOutListeners = new CacheEventSource (OfflineEvent.class);
			System.err.println (new DebugString("[CacheManager] - Started."));
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
	
	public static I_CacheManager getInstance ()
	{
		return CacheManager;
	}
	
	private void notifyListeners (int aEventType,
								  Object aObject)
	{
		try
		{
			notifyListeners (this,
							 I_CacheListener.EVENTCODE_REPOSITORY,
							 aEventType,
							 aObject);
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
		return "Maui Cache Repository";
	}
	
	/**
	* Create a cache domain.  If a cache domain by the same name already exists, then a reference
	* to it will be returned.  Otherwise, a new domain will be created.
	*
	* @param aAddress The address of the cache domain.
	*
	* @return A new I_CacheDomain, or a reference to an already existing one.
	*/
	public I_CacheDomain create (Object aAddress)
	{
		I_CacheDomain retVal = (I_CacheDomain) cacheDomains.get (aAddress);
		if (retVal == null)
		{
			retVal = new CacheDomain (this, aAddress.toString ());
			cacheDomains.put (aAddress, retVal);
			notifyListeners (I_CacheListener.EVENTTYPE_CREATE,
							 retVal);
		}
		return retVal;
	}

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
								 Object [] aProperties)
	{
		return create (aAddress);
	}
	
	/**
	* Locate a cache domain.
	*
	* @param aAddress The address of the cache domain.
	*
	* @return The I_CacheDomain object, or null if it isn't found
	*/
	public I_CacheDomain locate (Object aAddress)
	{
		return (I_CacheDomain) cacheDomains.get (aAddress);
	}
	
	/**
	* Destroy a cache domain.
	*
	* @param aAddress The address of the cache domain.
	*
	* @return The I_CacheDomain object just destroyed, or null if it doesn't exist
	*/
	public I_CacheDomain destroyCacheDomain (Object aAddress)
	{
		I_CacheDomain retVal = locate (aAddress);
		if (retVal != null)
		{
			retVal.destroy ();
			notifyListeners (I_CacheListener.EVENTTYPE_DESTROY,
							 retVal);
		}
		return retVal;
	}
	
	/**
	* Add an I_RepositoryListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to add
	*/
	public void addRepositoryListener (I_RepositoryListener aRepositoryListener)
	{
		addCacheListener (aRepositoryListener);
	}
	
	/**
	* Remove an I_RepositoryListener object
	*
	* @param aRepositoryListener The I_RepositoryListener to remove
	*
	* @return The removed I_RepositoryListener, or null if it wasn't a listener
	*/
	public I_RepositoryListener removeRepositoryListener (I_RepositoryListener aRepositoryListener)
	{
		return (I_RepositoryListener) removeCacheListener (aRepositoryListener);
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
			I_CacheDomain theCacheDomain = (I_CacheDomain) aObject;
			theCacheDomain.cacheOut (theCacheDomain);
			notifyCacheListeners (I_CacheListener.EVENTTYPE_CACHEOUT,
								  theCacheDomain);
		}
	}
	
	/**
	* Reload an object in offline storage to the cache
	*
	* @param aCacheObject The I_Cacheobject representing the objected to be cached-in.
	*
	* @return The removed I_CacheDomainListener, or null if it wasn't a listener
	*/
	public synchronized void cacheIn (Object aObject)
	{
		if (aObject != null)
		{
			
			I_CacheDomain theCacheDomain = locate (((I_Named) aObject).getName ());
			if (theCacheDomain != null)
			{
				theCacheDomain.cacheIn (theCacheDomain);
				notifyCacheListeners (I_CacheListener.EVENTTYPE_CACHEIN,
									  theCacheDomain);
			}
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
	
}