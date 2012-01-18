// =============================================================================
// com.bitmovers.maui.engine.render.I_HasDepth
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MContainer;

/**
* I_SimplePhase <p>
* This interface is used by MFrame_wml to determine if a full or an abbreviated phase list
* should be generated.
*
* @invisible
*/
public interface I_SimplePhase
{
	/**
	* Get all of the components that should be included as part of this simple post
	* card
	*
	* @param aRenderable The current component (current point in deep navigation)
	* @param aPhase The phase being generated
	*
	* @return The I_Renderable array
	*/
	public I_Renderable [] getSimplePhaseComponents (I_Renderable aRenderable,
													 String aPhase);
}
