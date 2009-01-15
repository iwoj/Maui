package com.bitmovers.maui.engine.messagedispatcher;

import java.util.EventObject;

/**
* This describes a thread event
*
* @invisible
*/
public class ThreadEvent extends EventObject
{
	private final int threadCount;
	
	public ThreadEvent (MessageDispatcher aMessageDispatcher, int aThreadCount)
	{
		super (aMessageDispatcher);
		threadCount = aThreadCount;
	}
	
	public int getThreadCount ()
	{
		return threadCount;
	}
}
