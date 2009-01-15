// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.bitmovers.maui.engine.cachemanager.events.I_CacheListener;
import com.bitmovers.maui.engine.cachemanager.events.A_CacheEvent;

/**
* CacheEventSource <p>
* This abstract class provides functionality which is common to all
* Cache EventSources.
*
*/
public class CacheEventSource implements I_CacheEventSource
{
	protected ArrayList listeners = new ArrayList (5);
	protected I_CacheListener [] listenerArray = null;
	protected boolean dirty = true;
	protected Constructor eventConstructor = null;
	
	protected CacheEventSource ()
	{
	}
	
	protected CacheEventSource (Class aEventClass)
		throws NoSuchMethodException, SecurityException
	{
		setEventClass (aEventClass);
	}
	
	protected void setEventClass (Class aEventClass)
		throws NoSuchMethodException, SecurityException
	{
		Class [] theParams = {Object.class,		// Event source
							  int.class,	// Event code
							  int.class,	// Event type
							  Object.class};	// Arbitrary information
							  
		eventConstructor = aEventClass.getDeclaredConstructor (theParams);
	}
	
	/**
	* Add a cache listener.
	*
	* @param aCacheListener An I_CacheListener object to add.
	*/
	public synchronized void addCacheListener (I_CacheListener aCacheListener)
	{
		if (!listeners.contains (aCacheListener))
		{
			listeners.add (aCacheListener);
			dirty = true;
		}
	}
	
	/**
	* Remove a cache listener
	*
	* @param aCacheListener The I_CacheListener object to remove.
	*/
	public synchronized I_CacheListener removeCacheListener (I_CacheListener aCacheListener)
	{
		int theIndex = listeners.indexOf (aCacheListener);
		if (theIndex != -1)
		{
			listeners.remove (theIndex);
			dirty = true;
		}
		
		return (theIndex == -1 ? null : aCacheListener);
	}
	
	/**
	* Rebuild the listener list
	*/
	private void rebuildListenerArray ()
	{
		if (dirty)
		{
			Object [] theListeners = listeners.toArray ();
			listenerArray = new I_CacheListener [theListeners.length];
			for (int i = 0; i < listenerArray.length; i++)
			{
				listenerArray [i] = (I_CacheListener) theListeners [i];
			}
			dirty = false;
		}
	}
	
	protected synchronized void notifyListeners (Object aEventSource,
												 int aEventCode,
												 int aEventType,
												 Object aArbitraryInformation)
		throws InstantiationException,
			   IllegalAccessException,
			   InvocationTargetException,
			   NoSuchMethodException
	{
		if (eventConstructor != null)
		{
			A_CacheEvent theEvent =
				(A_CacheEvent)
					eventConstructor.newInstance (new Object [] {aEventSource,
																 new Integer (aEventCode),
																 new Integer (aEventType),
																 aArbitraryInformation});
			notifyListeners (theEvent);
		}
		else
		{
			throw new NoSuchMethodException ("Event Constructor");
		}
	}
	
	/**
	* Notify the listeners of an event
	*
	* @param aCacheEvent The A_CacheEvent to pass onto all listeners
	*/
	protected synchronized void notifyListeners (A_CacheEvent aCacheEvent)
	{
		if (dirty)
		{
			rebuildListenerArray ();
		}
		
		for (int i = 0; i < listenerArray.length; i++)
		{
			listenerArray [i].notify (aCacheEvent);
		}
	}
}