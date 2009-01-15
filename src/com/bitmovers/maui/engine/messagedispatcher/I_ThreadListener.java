package com.bitmovers.maui.engine.messagedispatcher;

import java.util.EventListener;

/**
* I_ThreadListener INTERFACE <p>
* This listens to thread total changes
*
* @invisible
*/
public interface I_ThreadListener extends EventListener
{
	/**
	* The event
	*
	* @param aThreadEvent the event
	*/
	public void threadEvent (ThreadEvent aEvent);
}
