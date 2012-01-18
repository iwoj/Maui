// =============================================================================
// com.bitmovers.maui.engine.render.I_HasContent
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_HasContent <p>
* This is the interface for objects which are capable of generating renderer content
* information.
*/
public interface I_HasContent
{
	/**
	*
	* Render the object to the client device
	*
	* @param aRenderable An object which can be rendered (MComponent or Layout)
	*
	* @return The rendered String.
	*/
	public String generateContent (I_Renderable aRenderable);
}