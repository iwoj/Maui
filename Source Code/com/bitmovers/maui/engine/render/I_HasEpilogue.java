// =============================================================================
// com.bitmovers.maui.engine.render.I_HasEpilogue
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_HasEpilogue <p>
* This is the interface for objects which are capable of generating renderer prologue
* information.
*/
public interface I_HasEpilogue
{
	/**
	*
	* Render the object to the client device
	*
	* @param aRenderable An object which can be rendered (MComponent or Layout)
	*
	* @return The rendered String.
	*/
	public String generateEpilogue (I_Renderable aRenderable);
}