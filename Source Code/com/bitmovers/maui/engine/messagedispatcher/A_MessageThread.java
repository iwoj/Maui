package com.bitmovers.maui.engine.messagedispatcher;

/**
* A_MessageThread ABSTRACT <p>
* This is the abstract class which represents a Thread within the MessageDispatcher
* thread pool.  All MessageThread objects must extend this class
*
* @invisible
*/
public abstract class A_MessageThread extends Thread
{
	private Object currentMessage;
	private MessageDispatcher messageDispatcher;
	private boolean active = true;
	private boolean queued = false;
	private boolean waiting = false;
	private static int threadNumber = 0;
	private long touchTime = 0;
	private boolean handlingMessage = false;
	
	/**
	* Empty constructor for convenience
	*/
	public A_MessageThread ()
	{
		setName ("Message Thread " + threadNumber++);
	}
	
	/**
	* Initialize the MessageThread.  This is a final method because it must be called
	* and cannot be overridden.  To perform specific initilization, override the
	* doInitialize method.
	*
	* @param aMessageDispatcher The MessageDispatcher object, which arbitrates the threads
	* @param aCurrentMessage The object representing the initial message for the thread (ie.
	*						 the reason why the thread was created).
	*/
	protected final void initialize (MessageDispatcher aMessageDispatcher,
									 Object aCurrentMessage)
	{
		messageDispatcher = aMessageDispatcher;
		doInitialize ();
		handleMessage (aCurrentMessage);
		start ();
	}
	
	/**
	* A "call-out" method for the MessageThread subclass to perform some initialization
	*/
	protected void doInitialize ()
	{
	}
	
	/**
	* Set a boolean indicating if this MessageThread is queued, and waiting for another message
	*
	* @param aQueued Boolean indicating if queued or not
	*/
	public void setQueued (boolean aQueued)
	{
		queued = aQueued;
	}
	
	/**
	* Test for this MessageThread being queued or not
	*
	* @return The queued boolean
	*/
	public boolean isQueued ()
	{	
		return queued;
	}
	
	/**
	* Test if this Thread can be shutdown
	*
	*/
	public synchronized boolean tryThreadShutdown (int aAgeLimit)
	{
		boolean retVal = false;
		if (!handlingMessage &&
			System.currentTimeMillis () - touchTime > aAgeLimit)
		{
			retVal = messageDispatcher.shutdownThread (this);
		}
		return retVal;
	}
	
	/**
	* Is the thread handling a message
	*
	*/
	protected boolean isHandlingMessage ()
	{
		return handlingMessage;
	}
	
	/**
	* Set a boolean indicating if this MessageThread is still active or not.  If it
	* isn't active, then the thread will exit.
	*
	* @param aActive The boolean indicating activity
	*/
	protected void setActive (boolean aActive)
	{	
		active = aActive;
	}
	
	private void touch ()
	{
		touchTime = System.currentTimeMillis ();
	}
	
	protected boolean isExpired (int aAgeLimit)
	{
		return (System.currentTimeMillis () - touchTime > aAgeLimit);
	}
	
	/**
	* Get the next message from the MessageDispatcher.  The MessageThread will block
	* if there are no outstanding messages.  Once a message appears the first available
	* MessageThread will be activated with the message.
	*/
	public synchronized void getMessage ()
	{
		handlingMessage = false;
		messageDispatcher.available (this);
		if (currentMessage == null)
		{
			try
			{
				waiting = true;
				wait ();
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	/**
	* Handle a message.  This is the other side of the "getMessage" method.
	* The MessageDispatcher notifies the MessageThread of a new message through this
	* method.  If the MessageThread is waiting, then it will be "notified".
	*
	* @param aMessage The message object
	*
	* @invisible
	*/
	public synchronized void handleMessage (Object aMessage)
	{
		touch ();
		handlingMessage = true;
		currentMessage = aMessage;
		if (waiting)
		{
			notify ();
			waiting = false;
		}
	}
	
	/**
	* The run method for the thread.  This just gets messages and processes them
	*/
	public void run ()
	{
		while (active)
		{
			processMessage (currentMessage);
			currentMessage = null;
			getMessage ();
			System.out.println ("Got message");
		}
	}
	
	
	/**
	* This abstract method is where the message is actually handled.  It must
	* implemented by a concreate subclass
	*
	* @param aMessage The message to process
	*/
	public abstract void processMessage (Object aMessage);
}
