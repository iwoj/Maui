// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.util.EventObject;

/**
* I_Property <p>
* A convenience wrapper for an object property
*
*/
public interface I_Property extends I_Named
{
	/**
	* Get the datatype for the property
	*
	* @return The property datatype, or null if it's not known
	*/
	public Class getType ();
	
	/**
	* Get the value of the property
	*
	* @return The property value
	*/
	public Object getPropertyValue ();
	
	/**
	* Set the property value
	*
	* @param aPropertyValue The new property value
	*/
	public void setPropertyValue (Object aValue);

}