// =============================================================================
// com.bitmovers.maui.cache
// =============================================================================
package com.bitmovers.maui.engine.cachemanager;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
* I_PropertyChangeEventSource <p>
* This interface describes the methods required for an object which can fire
* PropertyChangeEvents.
*
*/
public interface I_PropertyChangeEventSource extends I_CacheEventSource
{
	/**
	* Add a PropertyChangeListener object
	*
	* @param aPropertyChangeListener The PropertyChangeListener to add
	*/
	public void addPropertyChangeListener (PropertyChangeListener aPropertyChangeListener);
	
	/**
	* Remove a PropertyChangeListener object
	*
	* @param PropertyChangeListener The PropertyChangeListener to remove
	*
	* @return The removed PropertyChangeListener, or null if it wasn't a listener
	*/
	public PropertyChangeListener removePropertyChangeListener (PropertyChangeListener aPropertyChangeListener);
	
	/**
	* Notify all listeners of a property change event
	*
	* @param aPropertyName The property name
	* @param aOldValue The old boolean value
	* @param aNewvalue The new boolean value
	*/
	public void firePropertyChange (String aPropertyName,
									boolean aOldValue,
									boolean aNewValue);

	/**
	* Notify all listeners of a property change event
	*
	* @param aPropertyName The property name
	* @param aOldValue The old int value
	* @param aNewvalue The new int value
	*/
	public void firePropertyChange (String aPropertyName,
									int aOldValue,
									int aNewValue);

	/**
	* Notify all listeners of a property change event
	*
	* @param aPropertyName The property name
	* @param aOldValue The old Object value
	* @param aNewvalue The new Object value
	*/
	public void firePropertyChange (String aPropertyName,
									Object aOldValue,
									Object aNewValue);

	/**
	* Notify all listeners of a property change event
	*
	* @param aPropertyName The property name
	* @param aPropertyChangeEvent The property change event
	*/
	public void firePropertyChange (String aPropertyName,
									PropertyChangeEvent aPropertyChangeEvent);
	

	/**
	* Check if a property has any listeners
	*
	* @param aPropertyName The property name
	*
	* @return boolean indicating if the property has any listeners or not
	*/
	public boolean hasListeners (String aPropertyName);
}