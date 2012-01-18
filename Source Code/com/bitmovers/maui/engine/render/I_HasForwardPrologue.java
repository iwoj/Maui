// =============================================================================
// com.bitmovers.maui.engine.render.I_HasForwardPrologue
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_HasForwardPrologue <p>
* This is the interface for objects which are capable of generating renderer prologue
* information.
*/
public interface I_HasForwardPrologue
{
	/**
	*
	* Render the object to the client device
	*
	* @param aRenderable An object which can be rendered (MComponent or Layout)
	*
	* @return The rendered String.
	*/
	public String generateForwardPrologue (I_Renderable aRenderable);
}