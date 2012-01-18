// =============================================================================
// com.bitmovers.maui.engine.render.MTextField_wml_up_3
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Stack;
import com.bitmovers.maui.components.foundation.MSettable;
import com.bitmovers.maui.events.MActionEvent;

public class MTextField_wml_up_3 extends MTextField_wml
	implements I_HasDepth,
			   I_SimplePostCard,
			   I_HasForwardPrologue
{
	public synchronized String render (I_Renderable aRenderable)
	{
		return generateSimpleAnchor (aRenderable,
									 MActionEvent.ACTION_PUSH,
									 (String) ((MSettable) aRenderable).getValue ());
	}
	
	/**
	* Notify the component to do some "drill down"
	*
	* @param aRenderable The renderable MComponent
	* @param aStack The Stack which represents the current navigation depth
	* @param aBackout A command to be included as part of the backout
	*
	* @return The rendered string
	*/
	public String depthBasedRender (I_Renderable aRenderable,
							 	   	Stack aStack,
							 	   	String aBackout)
	{
		StringBuffer retVal = new StringBuffer ("input: ");
		retVal.append (super.render (aRenderable));
		retVal.append (generateDoneButton (aRenderable));
		return retVal.toString ();
	}
							 	   	
	/**
	* Notify the renderer that it is being backed out.  This is so it can do
	* whatever cleanup is necessary
	*
	* @param aRenderable The I_Renderable object
	*/
	public void backout (I_Renderable aRenderable)
	{
	}
	
	/**
	* Test if the event indicates that deep navigation is occuring or not.
	*
	* @param aActionEvent The MActionEvent describing the component's event
	* @param aStack The renderables stack
	*
	* @return Boolean indicating if this is deep navigation or not
	*/
	public boolean isDeepNavigating (MActionEvent aActionEvent, Stack aStack)
	{
		return (aActionEvent.getActionCommand ().equals (MActionEvent.ACTION_PUSH));
	}
}