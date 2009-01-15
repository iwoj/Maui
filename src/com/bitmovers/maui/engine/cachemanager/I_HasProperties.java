// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.util.EventObject;

/**
* I_HasProperties <p>
* Interface for any object which supports "properties"
*
*/
public interface I_HasProperties
	extends I_PropertyEventSource,
			I_PropertyChangeEventSource
{
	/**
	* Add a property.
	*
	* @param aName The property name
	* @param aType The datatype for the property
	* @param aDefaultValue A default value for the property
	*/
	public I_Property addProperty (String aName,
								   Class aType,
								   Object aDefaultValue);
								  
	/**
	* Get an I_Property wrapper
	*
	* @param aName The name of the property
	*
	* @return The I_Property object, or null if isn't found
	*/
	public I_Property getProperty (String aName);
								   
	/**
	* Set a property value
	*
	* @param aName The name of the property
	* @param aValue the value of the property
	*/
	public void setPropertyValue (String aName,
								  Object aValue);
								  
	/**
	* Get a property value
	*
	* @param aName the name of the property
	*
	* @return The value of the property, or null if itsn't found.
	*/
	public Object getPropertyValue (String aName);
}