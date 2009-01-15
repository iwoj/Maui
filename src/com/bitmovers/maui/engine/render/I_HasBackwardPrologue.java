// =============================================================================
// com.bitmovers.maui.engine.render.I_HasBackwardPrologue
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_HasBackwardPrologue <p>
* This is the interface for objects which are capable of generating renderer prologue
* information.
*/
public interface I_HasBackwardPrologue
{
	/**
	*
	* Render the object to the client device
	*
	* @param aRenderable An object which can be rendered (MComponent or Layout)
	*
	* @return The rendered String.
	*/
	public String generateBackwardPrologue (I_Renderable aRenderable);
}