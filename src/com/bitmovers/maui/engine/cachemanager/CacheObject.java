// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.io.Serializable;
import com.bitmovers.maui.engine.cachemanager.events.I_OfflineListener;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheListener;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheObjectListener;
import com.bitmovers.maui.engine.cachemanager.events.OfflineEvent;
import com.bitmovers.maui.engine.cachemanager.events.CacheObjectEvent;
/**
* I_CacheObject <p>
* This is for the object which holds and maintains the "cachable" user object.
*
*/
public class CacheObject extends CacheEventSource implements I_CacheObject
{
	private final I_CacheDomain cacheDomain;
	private final Object address;
	private Object userObject;
	private boolean taken = false;
	private boolean cachedOut = false;
	private final CacheEventSource offlineEventSource;
	
	protected CacheObject (I_CacheDomain aCacheDomain,
						   Object aAddress,
						   Object aUserObject)
		throws NoSuchMethodException
	{
		offlineEventSource = new CacheEventSource (OfflineEvent.class);
		setEventClass (CacheObjectEvent.class);
		cacheDomain = aCacheDomain;
		address = aAddress;
		userObject = aUserObject;
	}
	
	
	/**
	* Get the object's name
	*
	* @return The name
	*/
	public String getName ()
	{
		return cacheDomain.getName () + "." + address;
	}
	
	/**
	* Is this object taken?
	*
	* @return Boolean indicating if the object is taken or not
	*/
	public synchronized boolean isTaken ()
	{
		return taken;
	}
	
	/**
	* Is the object cached out?
	*
	* @return Boolean indicating if the object is cached out or not
	*/
	public boolean isCachedOut ()
	{
		return cachedOut;
	}
	
	private void notifyListeners (int aEventType, boolean aIsCacheObject)
	{
		try
		{
			if (aIsCacheObject)
			{
				notifyListeners (this,
								 I_CacheListener.EVENTCODE_CACHEOBJECT,
								 aEventType,
								 userObject);
			}
			else
			{
				offlineEventSource.notifyListeners (this,
												    I_CacheListener.EVENTCODE_CACHEOBJECT,
												    aEventType,
												    userObject);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
		
	/**
	* Destroy this I_CacheObject
	*
	*/
	public synchronized void destroy ()
	{
		cacheDomain.remove (address);
		notifyListeners (I_CacheListener.EVENTTYPE_DESTROY, true);
		userObject = null;
	}
	
	/**
	* Take the user object
	*
	* @return The user object
	*/
	public Object get ()
	{
		return userObject;
	}
	
	/**
	* Get the user object
	*
	* @return The user object
	*/
	public synchronized Object take ()
	{
		taken = true;
		notifyListeners (I_CacheListener.EVENTTYPE_TAKEN, true);
		return userObject;
	}
	
	/**
	* Get the address of this object
	*
	* @return The address
	*/
	public Object getAddress ()
	{
		return address;
	}
	
	/**
	* Get the Cache Domain for this Cache Object
	*
	* @return The I_CacheDomain object
	*/
	public I_CacheDomain getCacheDomain ()
	{
		return cacheDomain;
	}
	
	/**
	* Put a new user object (replacing the previous one)
	*
	* @param aObject The user object
	*/
	public synchronized void put (Object aUserObject)
	{
		taken = false;
		userObject = aUserObject;
		notifyListeners (I_CacheListener.EVENTTYPE_CHANGE, true);
	}
	
	/**
	* Release the user object.  This is done after a get, to indicate to the
	* cache manager that the user object is a candidate for caching out.
	*/
	public synchronized void release ()
	{
		taken = false;
		notifyListeners (I_CacheListener.EVENTTYPE_RELEASED, true);
	}
	
	/**
	* Add an I_CacheObjectListener object
	*
	* @param aCacheObjectListener The I_CacheObjectListener to add
	*/
	public void addCacheObjectListener (I_CacheObjectListener aCacheObjectListener)
	{
		addCacheListener (aCacheObjectListener);
	}
	
	/**
	* Remove an I_CacheObjectListener object
	*
	* @param aCacheobjectListener The I_CacheObjectListener to remove
	*
	* @return The removed I_CacheObjectListener, or null if it wasn't a listener
	*/
	public I_CacheObjectListener removeCacheObjectListener (I_CacheObjectListener aCacheObjectListener)
	{
		return (I_CacheObjectListener) removeCacheListener (aCacheObjectListener);
	}
	
	/**
	* Add an I_OfflineListener object
	*
	* @param aRepositoryListener The I_OfflineListener to add
	*/
	public void addOfflineListener (I_OfflineListener aOfflineListener)
	{
		offlineEventSource.addCacheListener (aOfflineListener);
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
		return (I_OfflineListener) offlineEventSource.removeCacheListener (aOfflineListener);
	}
	
	/**
	* Move an object out of the cache onto offline storage
	*
	* @param aCacheObject The I_CacheObject representing the object to be cached-out.
	*/
	public synchronized void cacheOut (Object aCacheObject)
	{
		if (!cachedOut)
		{
			if (userObject instanceof I_OfflineStorage)
			{
				((I_OfflineStorage) userObject).cacheOut (this);
			}
			notifyListeners (I_CacheListener.EVENTTYPE_CACHEOUT, false);
			userObject = null;
			cachedOut = true;
		}
	}
	
	/**
	* Reload an object in offline storage to the cache
	*
	* @param aCacheObject The I_Cacheobject representing the objected to be cached-in.
	*
	* @return The removed I_CacheDomainListener, or null if it wasn't a listener
	*/
	public synchronized void cacheIn (Object aCacheObject)
	{
		if (cachedOut)
		{
			userObject = ((I_CacheObject) aCacheObject).get ();
			if (userObject instanceof I_OfflineStorage)
			{
				((I_OfflineStorage) userObject).cacheIn (this);
			}
			cachedOut = false;
			notifyListeners (I_CacheListener.EVENTTYPE_CACHEIN, false);
		}
	}
}
