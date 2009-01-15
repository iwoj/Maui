package com.bitmovers.maui.engine.messagedispatcher;
import java.util.Vector;
import java.util.Enumeration;

import com.bitmovers.maui.engine.Queue;
import com.bitmovers.maui.engine.I_QueueFilter;
import com.bitmovers.maui.engine.ServerConfigurationManager;

/**
* MessageDispatcher <p>
* This handles brokering messages to message processing threads contained
* within the MessageDispatcher.
*
* @invisible
*/
public class MessageDispatcher
	implements Runnable
{
	protected Queue messages = new Queue ();
	protected Queue threads = new Queue ();
	protected MessageThreadFactory messageThreadFactory;
	
	protected int threadCount = 0;
	protected int threadCapacity = 20;
	protected int threadMinimum = 4;
	private int ageLimit;
	private int count = 0;
	
	private I_ThreadListener [] threadListeners = new I_ThreadListener [0];
	private Vector listenersVector = new Vector ();
	private boolean dirty = true;

	class DispatchMessage
	{
		protected final int sequenceNumber;
		protected final Object object;
		protected final boolean deferred;
		protected DispatchMessage (int aSequenceNumber,
								   Object aObject,
								   boolean aDeferred)
		{
			sequenceNumber = aSequenceNumber;
			object = aObject;
			deferred = aDeferred;
		}
	}
	
	/**
	* Simple constructor.
	*
	* @param aMessageThreadFactory Reference to an object which can create instances
	*                              of the concrete MessageThread objects
	*/
	public MessageDispatcher (MessageThreadFactory aMessageThreadFactory)
	{
		messageThreadFactory = aMessageThreadFactory;
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		ageLimit = getValue (theSCM,
							 theSCM.MAUI_THREAD_AGE_LIMIT,
							 Integer.parseInt (theSCM.MAUI_THREAD_AGE_LIMIT_VALUE));
		threadCapacity = getValue (theSCM,
								   theSCM.MAUI_THREAD_POOL_MAXIMUM,
								   Integer.parseInt (theSCM.MAUI_THREAD_POOL_MAXIMUM_VALUE));
		threadMinimum = getValue (theSCM,
								  theSCM.MAUI_THREAD_POOL_MINIMUM,
								  Integer.parseInt (theSCM.MAUI_THREAD_POOL_MINIMUM_VALUE));		
		new Thread (this).start ();
	}
	
	
	private int getValue (ServerConfigurationManager aSCM,
						  String aKey,
						  int aDefault)
	{
		int retVal = aDefault;
		
		try
		{
			retVal = Integer.parseInt (aSCM.getProperty (aKey));
		}
		catch (Exception e)
		{
			retVal = aDefault;
		}
		return retVal;
	}
	/**
	* Housekeeping thread... Trim back on threads if there are too many outstanding
	*/
	public void run ()
	{
		while (true)
		{
			try
			{
				Thread.sleep (ageLimit);
			}
			catch (InterruptedException e)
			{
			}
			
			synchronized (this)
			{
				if (threads.size () > threadMinimum)
				{
					int theThreadCount = threads.size ();
					threads.filteredRemoval (new I_QueueFilter ()
						{
							public boolean filter (Object aPayload)
							{
								boolean retVal = false;
								if (threadCount > threadMinimum &&
									aPayload != null)
								{
									A_MessageThread theThread = (A_MessageThread) aPayload;
									retVal = theThread.tryThreadShutdown (ageLimit);
								}
								return retVal;
							}
						});
					if (theThreadCount != threads.size ())
					{
						notifyThreadListeners (threadCount);
					}
				}
			}
		}
	}
	
	/**
	* Empty constructor (for debugging)
	*
	* This will use a simple MessageThreadFactory.  The MessageThreads created from this
	* factory will only print out the ".toString ()" result of the message object
	*/
	public MessageDispatcher ()
	{
		this (new MessageThreadFactory ());
	}
	
	/**
	* Post a message object.  The MessageDispatcher will check if a thread is available
	* to handle the message immediately.  If no thread is available, and the thread capacity
	* isn't reached yet, it will create a MessageThread object to handle the message.  If the
	* capacity has been reached, then the message will be dropped into an outstanding message
	* queue.
	*
	* @param aMessage The message bing posted
	*/
	public synchronized void postMessage (Object aMessage)
	{
		if (threads.size () == 0)
		{
			//
			//	No free threads
			//
			if (threadCapacity == -1 ||
				threadCount < threadCapacity)
			{
				notifyThreadListeners (++threadCount);
				//System.err.println ("Launching thread " + threadCount);
				messageThreadFactory.createMessageThread ().initialize (this, aMessage);
			}
			else
			{
				System.err.println ("Deferring message");
				//messages.add (new DispatchMessage (count++, aMessage, true));
				messages.add (aMessage);
			}
		}
		else
		{
			//postMessageToThread (new DispatchMessage (count++, aMessage, false));
			postMessageToThread (aMessage);
		}
	}
	
	/**
	* Post a message to an available thread
	*
	* @param aMessage The message to be posted to an available MessageThread
	*
	*/
	private void postMessageToThread (Object aMessage)
	{
		//System.err.println ("Posting message to thread");
		if (aMessage instanceof DispatchMessage)
		{
			System.err.println ("Handling message # " + ((DispatchMessage) aMessage).sequenceNumber);
			if (((DispatchMessage) aMessage).deferred)
			{
				System.err.println ("Deferred message");
			}
			aMessage = ((DispatchMessage) aMessage).object;
			
		}
		A_MessageThread theTargetThread = (A_MessageThread) threads.removeTail ();
		theTargetThread.setQueued (false);
		theTargetThread.handleMessage (aMessage);
	}
	
	/**
	* Shutdown a thread
	*
	* @param aThread The thread to shutdown
	*/
	protected synchronized boolean shutdownThread (A_MessageThread aThread)
	{
		boolean retVal = false;
		if (!aThread.isHandlingMessage ())
		{
			aThread.setQueued (false);
			aThread.setActive (false);
			aThread.handleMessage (null);
			threadCount--;
			//System.err.println ("Removed thread " + threadCount);
			retVal = true;
		}
		return retVal;
	}
	
	/**
	* The callback method from a MessageThread indicating that it is available to process
	* another message
	*
	* @param aMessageThread The MessageThread which has become avaialble
	*/
	public synchronized void available (A_MessageThread aMessageThread)
	{
		if (!aMessageThread.isQueued ())
		{
			threads.add (aMessageThread);
			aMessageThread.setQueued (true);
		}
		
		if (messages.size () > 0)
		{
			postMessageToThread (messages.remove ());
		}
	}
	private final void checkDirty ()
	{
		synchronized (listenersVector)
		{
			if (dirty)
			{
				threadListeners = new I_ThreadListener [listenersVector.size ()];
				Enumeration theListeners = listenersVector.elements ();
				int i = 0;
				while (theListeners.hasMoreElements ())
				{
					threadListeners [i++] = (I_ThreadListener) theListeners.nextElement ();
				}
				dirty = false;
			}
		}
	}
	
	private final void notifyThreadListeners (int aThreadCount)
	{
		checkDirty ();
		if (threadListeners.length > 0)
		{
			ThreadEvent theEvent = new ThreadEvent (this, aThreadCount);
			for (int i = 0; i < threadListeners.length; i++)
			{
				threadListeners [i].threadEvent (theEvent);
			}
		}
	}
	
	public void addThreadListener (I_ThreadListener aListener)
	{
		synchronized (listenersVector)
		{
			if (!listenersVector.contains (aListener))
			{
				listenersVector.addElement (aListener);
				dirty = true;
			}
		}
	}
	
	public void removeThreadListener (I_ThreadListener aListener)
	{
		synchronized (listenersVector)
		{
			if (listenersVector.contains (aListener))
			{
				listenersVector.removeElement (aListener);
				dirty = true;
			}
		}
	}
}
