// =============================================================================
// com.bitmovers.maui.client.Queue
// =============================================================================

package com.bitmovers.maui.engine;

/**
* Queue <p>
* This is a simply doubly linked queue.
*/
public class Queue
{
	/**
	* The queue head
	*/
	protected QueueEntry queueHead = new QueueEntry ();
	protected int queueSize = 0;
	
	/**
	* A queue of available queue entries.  At the expense of some memory consumption
	* this reduces the Garbage Collection activity.
	*
	* At some point, probably a capacity should be set for the free list size to
	* prevent it from gobbling too much memory
	*/
	protected QueueEntry freeList = new QueueEntry ();
	
	/**
	* An entry within the queue
	*/
	class QueueEntry
	{
		/**
		* The forward link
		*/
		private QueueEntry flink;
		
		/**
		* The backward link
		*/
		private QueueEntry blink;
				
		/**
		* The payload
		*/
		private Object payload = null;
		
		/**
		* Simple constructor
		*/
		protected QueueEntry ()
		{
			flink = this;
			blink = this;
		}
		
		/**
		* Get the payload object
		*
		* @return The payload
		*/
		protected Object getPayload ()
		{
			return payload;
		}
		
		/**
		* Add this queue entry to the queue tail
		*
		* @param aPayload The payload for the queue entry
		* @param aQueueHead The target queue head
		*/
		protected void add (Object aPayload, QueueEntry aQueueHead)
		{
			if (flink != this)
			{
				remove ();
			}
			payload = aPayload;
			blink = aQueueHead.blink;
			flink = aQueueHead;
			aQueueHead.blink.flink = this;
			aQueueHead.blink = this;
		}
		
		/**
		* Remove this queue entry
		*/
		protected QueueEntry remove ()
		{
			flink.blink = blink;
			blink.flink = flink;
			flink = this;
			blink = this;

			return this;
		}
		
		protected QueueEntry removeNext ()
		{
			return flink.remove ();
		}
		
		protected QueueEntry removePrevious ()
		{
			return blink.remove ();
		}
	}
	
	/**
	* Simple constructor
	*/
	public Queue ()
	{
	}
	
	/**
	* Add an object to the queue tail
	*
	* @param aPayload The object to enqueue
	*/
	public synchronized void add (Object aPayload)
	{
		QueueEntry theQueueEntry = (freeList.flink != freeList ?
										freeList.removeNext () :
										new QueueEntry ());
		theQueueEntry.add (aPayload, queueHead);
		queueSize++;
	}
	
	/**
	* Get an object from the queue head without removing it
	*
	* @return The enqueued object
	*/
	public Object get ()
	{
		return queueHead.flink.getPayload ();
	}
	
	/**
	* Get the object from the queue tail without removing it
	*
	* @return The enqueued object
	*/
	public Object getTail ()
	{
		return queueHead.blink.getPayload ();
	}
	
	/**
	* Remove an object from the queue tail
	*
	* @return The enqueued object
	*/
	public synchronized Object removeTail ()
	{
		Object retVal = null;
		if (!isEmpty ())
		{
			QueueEntry theEntry = queueHead.removePrevious ();
			retVal = theEntry.getPayload ();
			theEntry.add (null, freeList);
			queueSize--;
		}
		return retVal;
	}
	
	
	/**
	* Remove an object from the queue head
	*
	* @return The enqueued object
	*/
	public synchronized Object remove ()
	{
		Object retVal = null;
		if (!isEmpty ())
		{
			QueueEntry theEntry = queueHead.removeNext ();
			retVal = theEntry.getPayload ();
			theEntry.add (null, freeList);
			queueSize--;
		}
		return retVal;
	}
	
	/**
	* Get the queue size
	*
	* @return The queue size
	*/
	public int getQueueSize ()
	{
		return queueSize;
	}
	
	/**
	* Get the queue size
	*
	* @return The queue size
	*/
	public int size ()
	{
		return queueSize;
	}
	
	
	/**
	* Test if the queue is empty or not
	*
	* @return Boolean indicating if it is empty or not
	*/
	public synchronized boolean isEmpty ()
	{
		return (queueHead.flink == queueHead);
	}
	
	/**
	* Get all of the payloads in the queue
	*
	* @return Array of payloads
	*/
	public synchronized Object [] getPayloads ()
	{
		Object [] retVal = new Object [queueSize];
		QueueEntry theEntry = queueHead.flink;
		
		int i = 0;
		while (theEntry != queueHead)
		{
			retVal [i++] = theEntry.getPayload ();
			theEntry = theEntry.flink;
		}
		return retVal;
	}
	
	public synchronized void filteredRemoval (I_QueueFilter aFilter)
	{
		QueueEntry theEntry = queueHead;
		
		while (theEntry.flink != queueHead)
		{
			if (aFilter.filter (theEntry.flink.getPayload ()))
			{
				theEntry.flink.remove ();
				queueSize--;
			}
			theEntry = theEntry.flink;
		}
	}
	
	/**
	* Remove an entry using a payload as a reference
	*
	* @param aPayload The payload to remove
	*
	* @return boolean indicating found/not found
	*/
	public boolean remove (Object aPayload)
	{
		PayloadRemoval aRemoval = new PayloadRemoval (aPayload);
		filteredRemoval (aRemoval);
		return aRemoval.isRemoved ();
	}
	
}

class PayloadRemoval implements I_QueueFilter
{
	private final Object payload;
	private boolean removed = false;
	
	protected PayloadRemoval (Object aPayload)
	{
		payload = aPayload;
	}
	
	public boolean filter (Object aPayload)
	{
		boolean retVal = (aPayload.equals (payload));
		if (retVal)
		{
			removed = true;
		}
		return retVal;
	}
	
	protected boolean isRemoved ()
	{
		return removed;
	}
}