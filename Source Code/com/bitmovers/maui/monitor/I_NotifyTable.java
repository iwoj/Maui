package com.bitmovers.maui.monitor;

/**
* I_NotifyTable <p>
*
* Interface to a table which provides parameter change notification
*/
public interface I_NotifyTable
{
	/**
	* Add a notify listener
	*
	* @param aKey The key of access
	* @param aNotifyListener The I_NotifyListener object
	*/
	public void addNotifyListener (Object aKey, I_NotifyListener aListener);
	
	/**
	* Remove a notify listener
	*
	* @param aKey The key of access
	* @param aNotifyListener The I_NotifyListener object
	*/
	public I_NotifyListener removeNotifyListener (Object aKey, I_NotifyListener aListener);

	/**
	* Get a value from the table
	*
	* @param aKey The key of access
	* 
	* @return The value 
	*/
	public Object get (Object aKey);
	
	/**
	* Get a value from the table
	*
	* @param aKey The key of access
	* @param aIndex The index of the object (for arrayed items)
	* 
	* @return The value 
	*/
	public Object get (Object aKey, int aIndex);
	
	/**
	* Get the array size of the item
	*
	* @param aObject The key of access
	*
	* @return The array size of the item
	*/
	public int getArraySize (Object aKey);
	
	/**
	* Put a value to the table
	*
	* @param aKey The key of access
	* @param aIndex The index of the object (for arrayed items)
	* @param aValue The value to put
	*
	* @return The previous value
	*/
	public Object put (Object aKey, int aIndex, Object aValue);

	/**
	* Force republication to all listeners of the value for this key
	*
	* @param aKey The key to republish
	*/
	public void republish (Object aKey);
	
	/**
	* Republish the entire table
	*/
	public void republishAll ();
}