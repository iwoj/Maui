// =============================================================================
// com.bitmovers.maui.httpserver.I_ApplicationListener
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import java.util.EventListener;

/**
* I_ApplicationListener INTERFACE <p>
* This interface is used to listen the application level events
*
* @invisible
*/
public interface I_ApplicationListener extends EventListener
{
	/**
	* Notification of an application being activated
	*
	* @param aApplicationEvent The event object describing the application
	*/
	public void applicationActivated (ApplicationEvent aApplicationEvent);
	
	/**
	* Notification of an application being deactivated
	*
	* @param aApplicationEvent The event object describing the application
	*/
	public void applicationDeactivated (ApplicationEvent aApplicationEvent);
}
	
