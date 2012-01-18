// =============================================================================
// com.bitmovers.maui.engine.render.I_FrameRenderer
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_FrameRenderer <p>
* This is the interface for frame renderers
*/
public interface I_FrameRenderer extends I_Renderer
{
	/**
	* Set the boolean indicating if the MauiOneEvent variable is being used
	*
	* @param aUseMauiOneEvent MauiOneEvent being used or not
	*/
	public void setUseMauiOneEvent (boolean aMauiOneEvent);
}
