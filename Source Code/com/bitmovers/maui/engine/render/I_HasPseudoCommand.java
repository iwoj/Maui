// =============================================================================
// com.bitmovers.maui.engine.render.I_HasEpilogue
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_HasPseudoCommand <p>
* This interface is used for depth based renderers which require an alternative
* pseudo command for the MFrame in the go card
*/
public interface I_HasPseudoCommand
{
	/**
	* Get the pseudo command target
	*
	* @return The pseudo command target
	*/
	public String getPseudoCommandTarget ();
	
	/**
	* Get the pseudo command value
	*
	* @return The pseudo command value
	*/
	public String getPseudoCommandValue ();
}