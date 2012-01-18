// =============================================================================
// com.bitmovers.maui.engine.render.I_HasDepth
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MContainer;

/**
* I_SimplePostCard <p>
* This interface is used by MFrame_wml to determine if a full or an abbreviated post card
* should be generated.
*
* @invisible
*/
public interface I_SimplePostCard
{
	/**
	* Get the container for the object being rendered.  This is used when generating
	* a simple post card
	*
	* @param aComponent The component to get the container from
	*
	* @return The object's container
	*/
	public MContainer getContainer (MComponent aComponent);
	
	/**
	* Get all of the components that should be included as part of this simple post
	* card
	*
	* @param aRenderable The current component (current point in deep navigation)
	*
	* @return The I_Renderable array
	*/
	public I_Renderable [] getSimplePostCardComponents (I_Renderable aRenderable);
}
