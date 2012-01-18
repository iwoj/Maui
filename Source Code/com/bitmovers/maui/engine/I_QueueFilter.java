// =============================================================================
// com.bitmovers.maui.engine.I_QueueFilter
// =============================================================================

package com.bitmovers.maui.engine;

/**
* Interface for traversing the queue, and removing entries
*/
public interface I_QueueFilter
{
	/**
	* Test for this entry to be removed
	*
	* @param aPayload The payload
	*
	* @return True to remove.  False to leave
	*/
	public boolean filter (Object aPayload);
}
