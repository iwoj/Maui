// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;

/**
* I_LoadFactorable <p>
* This interface describes objects which are capable of setting, reporting on and responding
* to arbitrary "system load" events.  The definition of a system load is fairly loose
* (eg. system resources, memory, i/o demand, etc.).  The interpretation of load is the
* responsibility of the various I_CacheStrategy objects.
*
*/
public interface I_LoadFactorable
{
	/**
	* The name of a load factor indicating collection size capacity, plus its
	* datatype.
	*/
	public static final String LOADFACTOR_CAPACITY = "Capacity";
	public static final Class DATATYPE_CAPACITY = Integer.TYPE;
	
	/**
	* Get a list of the names of the different load factors which are being managed
	* by this object
	*
	* @return The list of load factor names
	*/
	public String [] getLoadFactorNames ();
	
	/**
	* Get the datatype for the load factor.
	*
	* @param aFactorname The name of the load factor
	*
	* @return Class representing the datatype for the load factor (or null, if it wasn't found)
	*/
	public Class getDataType (String aName);
	
	/**
	* Get the load factor value.  Since the meaning of a factor is open, the primitive
	* data type for the load factor could be anything (or even an Object).
	*
	* @param aFactorName The name of the load factor
	*
	* @return Object representing the load factor (or null, if the factor wasn't found)
	*/
	public Object getLoadFactor (String aName);
	
	/**
	* Set a value for a load factor
	*
	* @param aFactorName The name of the load factor
	* @param aValue The object representing the new value
	*/
	public void setLoadFactor (String aName, Object aValue);
	
	/**
	* Adjust a load factor.  Rather than setting an absolute value for a load factor,
	* this method allows a load factor to be changed relative to its current value.  If
	* the load factor isn't found, then this is taken as an absolute value.
	*
	* @param aFactorname The name of the load factor
	* @param aValue The object representing the new value
	*/
	public void adjustLoadFactor (String aName, Object aValue);
	
	
}