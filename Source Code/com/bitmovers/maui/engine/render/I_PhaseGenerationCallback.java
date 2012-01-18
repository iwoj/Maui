// =============================================================================
// com.bitmovers.maui.engine.render.I_HasPrologue
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.MComponent;

/**
* I_PhaseGenerationCallback <p>
* A callback to the I_Generator object to generate the tags for components
* of a particular phase
* <p>
* This is kind of awkward, and should be reworked.
*/
public interface I_PhaseGenerationCallback
{
	/**
	*
	* Render the object to the client device
	*
	* @param aPhase The callback phase name
	* @param aComponents The components for the callback phase
	*
	* @return The rendered StringBuffer.
	*/
	public StringBuffer generatePhase (String aPhase,
									   Object [] aComponents);
}