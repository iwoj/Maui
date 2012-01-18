package com.bitmovers.maui.monitor;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
* NotifyHashtable <p>
* Class for event notification of Hashtable changes
*
* @version 		A1	07/08/99
*/
public class NotifyHashtable extends Hashtable
	implements I_NotifyTable
{
	protected Hashtable listeners = new Hashtable ();
	protected DB_ListenerList allListeners = new DB_ListenerList (null);
	protected Hashtable alwaysNotify = new Hashtable (10);
	protected boolean isAlwaysNotify = false;
	protected boolean notifyOnNull = false;
	
	class DB_ListenerList
	{
		protected boolean dirty = true;
		protected I_NotifyListener [] listenerArray = null;
		protected Vector listenerList = new Vector ();
		protected Object key;
		
		protected DB_ListenerList (Object aKey)
		{
			key = aKey;
		}
		
		protected synchronized I_NotifyListener [] getListeners ()
		{
			if (dirty)
			{
				listenerArray = new I_NotifyListener [listenerList.size ()];
				Enumeration theListeners = listenerList.elements ();
				int i = 0;
				while (theListeners.hasMoreElements ())
				{
					listenerArray [i++] = (I_NotifyListener) theListeners.nextElement ();
				}
				dirty = false;
			}
			return listenerArray;
		}
		
		protected synchronized void addNotifyListener (I_NotifyListener aNotifyListener)
		{
			listenerList.removeElement (aNotifyListener);
			listenerList.addElement (aNotifyListener);
			dirty = true;
		}
		
		protected synchronized I_NotifyListener removeNotifyListener (I_NotifyListener aNotifyListener)
		{
			dirty = true;
			I_NotifyListener retVal = null;
			if (listenerList.removeElement (aNotifyListener))
			{
				retVal = aNotifyListener;
			}
			
			return retVal;
		}
		
		protected void notifyListeners (NotifyEvent aEvent)
		{
			I_NotifyListener [] theListeners = getListeners ();
			for (int i = 0; i < theListeners.length; i++)
			{
				theListeners [i].notify (aEvent);
			}
		}
	}
	
	public void setNotifyOnNull (boolean aNotifyOnNull)
	{
		notifyOnNull = aNotifyOnNull;
	}
	
	public boolean getNotifyOnNull ()
	{
		return notifyOnNull;
	}
	
	/**
	* Get the array size of the item
	*
	* @param aObject The key of access
	*
	* @return The array size of the item
	*/
	public int getArraySize (Object aKey)
	{
		Vector theVector = (Vector) super.get (aKey);
		return (theVector == null ? 0 : theVector.size ());
	}
	
	public void setAlwaysNotify (Object aKey, boolean aAlwaysNotify)
	{
		if (aKey == null)
		{
			isAlwaysNotify = aAlwaysNotify;
		}
		else
		{
			alwaysNotify.put (aKey, new Boolean (aAlwaysNotify));
		}
	}
	
	public boolean getAlwaysNotify (Object aKey)
	{
		boolean retVal = false;
		if (aKey == null)
		{
			retVal = isAlwaysNotify;
		}
		else
		{
			Boolean theAlwaysNotify = (Boolean) alwaysNotify.get (aKey);
			if (theAlwaysNotify != null)
			{
				retVal = theAlwaysNotify.booleanValue ();
			}
		}
		return retVal;
	}
	
	public Object put (Object aKey, Object aValue)
	{
		return put (aKey, 0, aValue);
	}
	
	/**
	* Put a value to the table
	*
	* @param aKey The key of access
	* @param aIndex The index of the object (for arrayed items)
	* @param aValue The value to put
	*
	* @return The previous value
	*/
	public Object put (Object aKey, int aIndex, Object aValue)
	{
		if (aKey instanceof String &&
			((String) aKey).equals ("11"))
		{
			System.out.println ("Wait");
		}
		Vector theArray = (Vector) super.get (aKey);
		if (theArray == null)
		{
			theArray = new Vector ();
			super.put (aKey, theArray);
		}
		Object theValue = null;
		if (theArray.size () < aIndex + 1)
		{
			theArray.setSize (aIndex + 1);
		}
		else
		{
			theValue = theArray.elementAt (aIndex);
		}
		
		boolean doFire = (isAlwaysNotify ||
						  getAlwaysNotify (aKey) ||
						  (theValue == null ? notifyOnNull : !theValue.equals (aValue)));
		theArray.setElementAt (aValue, aIndex);
		if (doFire)
		{
			notifyListeners (aKey, aIndex, theValue);
		}
		return aValue;
	}

	/**
	* Get a value from the table at a given index
	*
	* @param aKey The key of access
	* @param aIndex The index of the object (for arrayed items)
	* 
	* @return The value 
	*/
	public Object get (Object aKey, int aIndex)
	{
		Object retVal = null;
		Vector theVector = (Vector) super.get (aKey);
		
		if (theVector != null &&
			aIndex < theVector.size ())
		{
			retVal = theVector.elementAt (aIndex);
		}
		return retVal;
	}
	
	/**
	* Get a value from the table
	*
	* @param aKey The key of access
	*/
	public Object get (Object aKey)
	{
		return get (aKey, 0);
	}
	
	/**
	* Force republication to all listeners of the value for this key
	*
	* @param aKey The key to republish
	*/
	public void republish (Object aKey)
	{
		int theSize = getArraySize (aKey);
		Vector theArray = (Vector) super.get (aKey);
		if (theArray != null)
		{
			for (int i = 0; i < theSize; i++)
			{
				Object theValue = theArray.elementAt (i);
				if (theValue != null)
				{
					notifyListeners (aKey, i, theValue);
				}
			}
		}
	}
	
	public Enumeration elements ()
	{
		Vector theTopParameters = new Vector ();
		Enumeration theElements = super.elements ();
		while (theElements.hasMoreElements ())
		{
			theTopParameters.addElement (((Vector) theElements.nextElement ()).elementAt (0));
		}
		return theTopParameters.elements ();
	}
	
	/**
	* Republish the entire table
	*/
	public void republishAll ()
	{
		Enumeration theKeys = keys ();
		while (theKeys.hasMoreElements ())
		{
			republish (theKeys.nextElement ());
		}
	}
	
	public void addNotifyListener (Object aKey, I_NotifyListener aListener)
	{
		if (aKey == null)
		{
			allListeners.addNotifyListener (aListener);
		}
		else
		{
			DB_ListenerList theListeners = (DB_ListenerList) listeners.get (aKey);
			if (theListeners == null)
			{
				theListeners = new DB_ListenerList (aKey);
				listeners.put (aKey, theListeners);
			}
			
			theListeners.addNotifyListener (aListener);
		}
	}
	
	public I_NotifyListener removeNotifyListener (Object aKey, I_NotifyListener aListener)
	{
		I_NotifyListener retVal = null;
		if (aKey == null)
		{
			retVal = allListeners.removeNotifyListener (aListener);
		}
		else
		{
			DB_ListenerList theListeners = (DB_ListenerList) listeners.get (aKey);
			if (theListeners != null)
			{
				retVal = (I_NotifyListener) theListeners.removeNotifyListener (aListener);
			}
		}
		return retVal;
	}
	
	/*private void doNotify (Hashtable aTable, NotifyEvent aNotifyEvent)
	{
		synchronized (aTable)
		{
			Enumeration theEnumeration = aTable.elements ();
			while (theEnumeration.hasMoreElements ())
			{
				I_NotifyListener theListener = 
					(I_NotifyListener) theEnumeration.nextElement();
				if (G_Common.debug)
				{
					System.out.println ("Notifying " + theListener);
				}
				theListener.notify (aNotifyEvent);
			}
		}
	}*/	
	
	public void notifyListeners (Object aKey, int aIndex, Object aOldValue)
	{
		NotifyEvent theNotifyEvent =
			new NotifyEvent (this, aKey, aIndex, aOldValue, get (aKey, aIndex));
		allListeners.notifyListeners (theNotifyEvent);
		//doNotify (allListeners, theNotifyEvent);
		DB_ListenerList theListeners = (DB_ListenerList) listeners.get (aKey);
		if (theListeners != null)
		{
			theListeners.notifyListeners (theNotifyEvent);
			//doNotify (theListeners, theNotifyEvent);
		}
	}
}