// =============================================================================
// com.bitmovers.maui.engine.render.MFrame_wml_up
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* MFrame_wml_up <p>
* @invisible
* This is the wml renderer for MFrame objects.  It also handles "deep navigation", which is accomplished by listening
* to events published from objects which implement the "I_HasDepth" interface.  When such a component publishes an event
* this class sets the initial render point to be the component which published the event... a bit like zooming in on that
* component.
*/
public class MFrame_wml_up extends MFrame_wml
{
	protected String createContent (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ("<onevent type=\"onenterforward\">");
		retVal.append (generatePostList (aRenderable, null));
		retVal.append ("</onevent>");
		return retVal.toString ();
	}
	
	protected String doStandardEpilogueMark (I_Renderable aRenderable)
	{
		return generateCard ("Mark",
							 "Mark",
							 null,
							 createContent (aRenderable),
							 false);
	}
	
	protected String getGeneratorClassName ()
	{
		return MFrame_wml.class.getName ();
	}
	
}