// =============================================================================
// com.bitmovers.maui.engine.render.I_HasBackwardPrologue
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_GenerateBackKeyCode <p>
* This is the interface for objects which can generate the content of the back key.
*/
public interface I_GenerateBackKeyCode
{
	/**
	* Get the back key code.
	*
	* @return The back key code.  If this is null, then allow the MFrame to generate
	* its default code
	*/
	public String generateBackKeyCode ();
}