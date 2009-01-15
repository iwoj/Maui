// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_Renderer <p>
* This is the interface for objects which are capable of rendering code to
* client devices and browsers.
*/
public interface I_Renderer
{
	/**
	*
	* Render the object to the client device
	*
	* @param aRenderable An object which can be rendered (MComponent or Layout)
	*
	* @return The rendered String.
	*/
	public String render (I_Renderable aRenderable);
	
	/**
	* Get the rendering phases that this renderer uses
	*
	* @return A String array of the rendering phases for this renderer
	*/
	public String [] getRenderPhases ();
	
	/**
	* Get the representative renderer for this renderer (which may not be the same
	* as the actual renderer
	*
	* @return The representative renderer
	*/
	public I_Renderable getRepresentativeRenderable (I_Renderable aRenderable);
	
	/**
	* Get the event source for this renderer... In some cases this may not be the
	* same as the actual its associated I_Renderable
	*/
	public I_Renderable getEventSource (I_Renderable aRenderable);
	
	/**
	* Is it okay to generate a phase for this renderer?
	*
	* @param aRenderable The target renderable
	* @param aPhase The String describing the phase being generated
	*
	* @return Boolean indicating if phase generation should be done for this component
	*/
	public boolean generatePhaseOkay (I_Renderable aRenderable, String aPhase);
	
	/**
	* Perform renderer cleanup
	*/
	public void finish ();
}