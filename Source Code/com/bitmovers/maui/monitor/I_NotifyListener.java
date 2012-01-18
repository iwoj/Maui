package com.bitmovers.maui.monitor;
import java.util.EventListener;

/**
* I_NotifyListener <p>
*
* The event listener for the NotifyHashtable
*/
public interface I_NotifyListener extends EventListener
{
	/**
	* Notification of a parameter change
	*
	* @param aEvent The NotifyEvent
	*/ 
	public void notify (NotifyEvent aEvent);
}