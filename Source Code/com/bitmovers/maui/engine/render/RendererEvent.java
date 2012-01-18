// =============================================================================
// com.bitmovers.maui.engine.render.I_Generator
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.EventObject;

import com.bitmovers.maui.components.MComponent;

/**
* RendererEvent EVENTOBJECT <p>
* This EventObject describes the creation of a renderer
*/
public class RendererEvent extends EventObject
{
	private final I_Renderer renderer;
	private final MComponent component;
	private final String [] clientClassification;
	
	public RendererEvent (Object aEventSource,
						  I_Renderer aRenderer,
						  MComponent aComponent,
						  String [] aClientClassification)
	{
		super (aEventSource);
		renderer = aRenderer;
		component = aComponent;
		clientClassification = aClientClassification;
	}

	public I_Renderer getRenderer ()
	{
		return renderer;
	}
	
	public MComponent getComponent ()
	{
		return component;
	}
	
	public String [] getClientClassification ()
	{
		return clientClassification;
	}
}