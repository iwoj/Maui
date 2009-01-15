// =============================================================================
// com.bitmovers.maui.engine.ApplicationManager
// =============================================================================

package com.bitmovers.maui.engine;
import com.bitmovers.maui.MauiApplication;

/**
* I_ApplicationInitializer INTERFACE <p>
* This is used as a callback to the ApplicationManager to perform initialization on
* the MauiApplication whenever it is instantiated.
*
* @invisible
*/
public interface I_ApplicationInitializer
{
	/**
	*  Callback to initialize the application
	*
	* @param aMauiApplication The MauiApplication to initialize
	*/
	public void initializeApplication (MauiApplication aApplication);
}	