// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;

import java.util.HashMap;
import com.bitmovers.maui.engine.cachemanager.events.I_LoadFactorListener;
import com.bitmovers.maui.engine.cachemanager.events.LoadFactorEvent;
import com.bitmovers.maui.engine.cachemanager.events.I_CacheListener;

/**
* LoadFactorable <p>
* This manages the LoadFactorable entities
*
*/
public class LoadFactorable extends CacheEventSource
	implements I_LoadFactorable,
			   I_LoadFactorEventSource
{
	protected final HashMap factors = new HashMap (10);
	

	protected LoadFactorable ()
		throws NoSuchMethodException
	{
		super ();
		setEventClass (LoadFactorEvent.class);
	}
	
	private void notifyListeners (int aEventType, Object aArbitrary)
	{
		try
		{
			notifyListeners (this,
							 I_CacheListener.EVENTCODE_LOADFACTOR,
							 aEventType,
							 aArbitrary);
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	* Get a list of the names of the different load factors which are being managed
	* by this object
	*
	* @return The list of load factor names
	*/
	public String [] getLoadFactorNames ()
	{
		Object [] theKeys = factors.keySet ().toArray ();
		String [] retVal = new String [theKeys.length];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = (String) theKeys [i];
		}
		return retVal;
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
		Object retVal = factors.get (aName);
		return (retVal == null ? null : retVal.getClass ());
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
		return factors.get (aName);
	}
	
	/**
	* Set a value for a load factor
	*
	* @param aFactorName The name of the load factor
	* @param aValue The object representing the new value
	*/
	public void setLoadFactor (String aName, Object aValue)
	{
		factors.put (aName, aValue);
		notifyListeners (I_CacheListener.EVENTTYPE_CHANGE,
						 aName);
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
		//
		//	This should be more sophisticated,  but for now this will do
		//
		factors.put (aName, aValue);
		notifyListeners (I_CacheListener.EVENTTYPE_ADJUST,
						 aName);
	}
	
	/**
	* Add an I_LoadFactorListener object
	*
	* @param aRepositoryListener The I_LoadFactorListener to add
	*/
	public void addLoadFactorListener (I_LoadFactorListener aLoadFactorListener)
	{
		addCacheListener (aLoadFactorListener);
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
		return (I_LoadFactorListener) removeCacheListener (aLoadFactorListener);
	}
	
}