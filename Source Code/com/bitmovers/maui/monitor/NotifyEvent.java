package com.bitmovers.maui.monitor;
import java.util.EventObject;
import java.util.Hashtable;

/**
* NotifyEvent <p>
*
* The event object describing the NotifyHashtable change
*/
public class NotifyEvent extends EventObject
{
	protected Object key;
	protected int index;
	protected Object oldValue;
	protected Object newValue;
	protected transient Hashtable sourceTable;
	
	public NotifyEvent (Hashtable aSource,
						Object aKey,
						int aIndex,
						Object aOldValue,
						Object aNewValue)
	{
		super ("Empty");
		sourceTable = aSource;
		key = aKey;
		index = aIndex;
		oldValue = aOldValue;
		newValue = aNewValue;
	}
	
	public Object getKey ()
	{
		return key;
	}
	
	public int getIndex ()
	{
		return index;
	}
	
	public Object getOldValue ()
	{
		return oldValue;
	}
	
	public Object getNewValue ()
	{
		return newValue;
	}
}