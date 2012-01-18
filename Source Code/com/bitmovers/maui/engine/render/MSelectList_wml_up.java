// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Stack;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.Settable;
import com.bitmovers.maui.components.foundation.HasSelectList;
import com.bitmovers.maui.events.MActionEvent;

public class MSelectList_wml_up extends A_MSelectList_wml_up
	implements //I_HasPrologue,
			   //I_HasEpilogue,
			   //I_HasBackwardPrologue,
			   I_GenerateBackKeyCode,
			   I_HasDepth,
			   I_SimplePostCard
{
	protected boolean inDepthBasedRender = false;
	protected I_OnPickGenerator onPickGenerator = null;
	
	
	public String render (I_Renderable aRenderable)
	{
		inDepthBasedRender = false;
		/*MComponent theSelectList = (MComponent) aRenderable;
		
		return "<a href=\"#" + generateComponentID (theSelectList) + "_Card\"> $(" +
							   generateComponentID (theSelectList) + ") </a>";*/
		return generateSimpleAnchor (aRenderable, MActionEvent.ACTION_PUSH);
	}
	
	protected String getLabel (I_Renderable aRenderable)
	{
		Settable theSelectList = (Settable) aRenderable;
		return (String) theSelectList.getValue ();
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
		inDepthBasedRender = true;
		MComponent theComponent = (MComponent) aRenderable;
		Vector theSelectionItems = ((HasSelectList) aRenderable).getSelectListOptions ();
		
		onPick = new String [theSelectionItems.size ()];
		String [] theOnPick = (String []) onPick;
		Enumeration theSelectionList = theSelectionItems.elements ();
		int i = 0;
		while (theSelectionList.hasMoreElements ())
		{
			Object theOption = theSelectionList.nextElement ();
			if (theOption instanceof String)
			{
				theOnPick [i++] = (String) theOption;
			}
			else if (theOption instanceof String [])
			{
				theOnPick [i++] = ((String []) theOption) [1];
			}
			else
			{
				theOnPick [i++] = getLabel ((I_Renderable) theOption);
			}
		}
		onPickGenerator = getOnPickGenerator (aRenderable);
		String retVal = MSelectList_wml.generateSelectList (getDepthBasedRenderer (aRenderable),
															generateComponentID (theComponent),
															((HasSelectList) aRenderable).getSelectListOptions (),
															getValue (aRenderable),
															"  ",
															onPick,
															false,
															onPickGenerator);
		return retVal;
	}
	
	protected A_Renderer getDepthBasedRenderer (I_Renderable aRenderable)
	{
		return (A_Renderer) aRenderable.getRenderer ();
	}
	
	protected I_OnPickGenerator getOnPickGenerator (I_Renderable aRenderable)
	{
		return new DefaultOnPickGenerator ((A_Renderer) aRenderable.getRenderer ());
	}
							 	   	
	/**
	* Notify the renderer that it is being backed out.  This is so it can do
	* whatever cleanup is necessary
	*
	* @param aRenderable The I_Renderable object
	*/
	public void backout (I_Renderable aRenderable)
	{
		inDepthBasedRender = false;
	}
	
	/**
	* Get the back key code.
	*
	* @return The back key code.  If this is null, then allow the MFrame to generate
	* its default code
	*/
	public String generateBackKeyCode ()
	{
		return "";
	}
	/**
	* Test if the event indicates that deep navigation is occuring or not.
	*
	* @param aActionEvent The MActionEvent describing the component's event
	*
	* @return Boolean indicating if this is deep navigation or not
	*/
	public boolean isDeepNavigating (MActionEvent aEvent, Stack aStack)
	{
		return (aEvent.getActionCommand ().equals (MActionEvent.ACTION_PUSH));
	}
	
	public boolean autoPop (MActionEvent aActionEvent, Stack aStack)
	{
		return true;
	}	
}