// =============================================================================
// com.bitmovers.maui.engine.render.I_Generator
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.EventListener;

/*
* I_RendererListener INTERFACE <p>
* This event listener is used for renderer creation events
*/
public interface I_RendererListener extends EventListener
{
	/**
	* This method is used to notify the listener that a renderer has been created
	*
	* @param aRendererEvent The EventObject which describes the event
	*/
	public void rendererCreated (RendererEvent aRendererEvent);
}