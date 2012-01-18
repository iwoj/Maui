// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.MComponent;

// =============================================================================
// INTERFACE: I_RendererInitialize
// =============================================================================

/** This interface is for renderers which require to be initialized after they 
  * are created. Note that the A_Renderer class will automatically load the 
  * appropriate template resource for any renderer which implements this 
  * interface.
  *
  */
  
public interface I_RendererInitialize
{
	/** Initialize the I_Renderer object
	  *
	  * @param aRenderable An object which can be rendered (MComponent or Layout)
	  * @param aComponent The reference component (required for Layout objects)
	  * @param aClientClassification The client classification string array
	  *
	  * @return The rendered String.
	  * 
	  */
	  
	public void initialize (I_Renderable aRenderable,
							MComponent aComponent,
							String [] aClientClassification);
}