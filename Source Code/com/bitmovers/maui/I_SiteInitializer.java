// =============================================================================
// com.bitmovers.maui.I_SiteInitializer
// =============================================================================

package com.bitmovers.maui;
import java.util.HashMap;

/**
* I_SiteInitializer INTERFACE <p>
* This is the interface that is used for site specific initialization object
*
* @invisible
*/
public interface I_SiteInitializer
{
	/**
	*  Perform site specific initialization
	*
	* @param aGlobalProperties VM wide HashMap, for storing global values
	*/
	public void initializeSite (HashMap aGlobalProperties);
}	