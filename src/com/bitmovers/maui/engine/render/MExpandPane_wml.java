// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Stack;
import java.util.Vector;
import java.util.Enumeration;

import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.foundation.MExpandPane;
import com.bitmovers.maui.components.foundation.HasPostValue;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.events.MActionEvent;

public class MExpandPane_wml extends MButton_wml
	implements I_HasDepth,
			   I_SimplePostCard,
			   I_SimplePhase
{
	protected boolean inDepthBasedRender = false;
	protected MauiApplication mauiApplication = null;
	public MExpandPane_wml ()
	{
		super ();
	}
	
	public void initialize (I_Renderable aRenderable, MComponent aComponent, String [] aClientClassification)
	{
		super.initialize (aRenderable, aComponent, aClientClassification);
		mauiApplication = (MauiApplication) aComponent.getRootParent ();
	}
		
	/*public String render (I_Renderable aRenderable)
	{
		onPick = generateComponentID ((MComponent) aRenderable) + "_ExpandCard";
		StringBuffer retVal = new StringBuffer (super.render (aRenderable));
		MTabbedPane theTabbedPane = (MTabbedPane) aRenderable;
		retVal.append ("<br/>");
		retVal.append (doRenderComponent (theTabbedPane.getSelectedComponent ()));
		return retVal.toString ();
	}*/
	
	public String render (I_Renderable aRenderable)
	{
		MExpandPane theExpandPane = (MExpandPane) aRenderable;
		theExpandPane.setOpen (inDepthBasedRender);
		StringBuffer retVal = new StringBuffer ((inDepthBasedRender ?
													"[" + theExpandPane.getLabel () + "]":
													super.render (aRenderable)));
		retVal.append (DefaultWmlLayoutRenderer.SEPARATOR);
		if (((MExpandPane) aRenderable).isOpen ())
		{
			retVal.append (renderComponents ((MContainer) aRenderable,
											 DefaultWmlLayoutRenderer.SEPARATOR));
		}
		return retVal.toString ();
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
		MExpandPane theExpandPane = (MExpandPane) aRenderable;
		inDepthBasedRender = true;
		//String retVal = generateCard ("depth_" + aStack.size (),
		//							  theExpandPane.getLabel (),
		//							  aBackout,
		//							  render (aRenderable));
		String retVal = render (aRenderable);
		inDepthBasedRender = false;
		return retVal;
	}
	
	/**
	* Notify the renderer that it is being backed out.  This is so it can do
	* whatever cleanup is necessary
	*
	* @param aRenderable The I_Renderable object
	*/
	public void backout (I_Renderable aRenderable)
	{
		((MExpandPane) aRenderable).setOpen (false);
	}
	
	
	/*public String generateEpilogue (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ("\n");
		retVal.append (generateGoCard (onPick,
									   aRenderable,
									   "$(" + generateComponentID ((MComponent) aRenderable) + ":e)"));
		return retVal.toString ();
	}*/
	
	protected MComponent [] getComponents (MContainer aContainer)
	{
		return (((MExpandPane) aContainer).isOpen () ?
					aContainer.getComponents () :
					new MComponent [0]);
	}
	
	public String getLabel (I_Renderable aRenderable)
	{
		return ((MExpandPane) aRenderable).getLabel ();
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
		return true;
	}
	
	/**
	* Get all of the components that should be included as part of this simple post
	* card
	*
	* @param aRenderable The current component (current point in deep navigation)
	*
	* @return The I_Renderable array
	*/
	public I_Renderable [] getSimplePostCardComponents (I_Renderable aRenderable)
	{
		Object [] theRenderables = ((MExpandPane) aRenderable).getComponents ();
		I_Renderable [] theTemp = new I_Renderable [theRenderables.length];
		int j = 0;
		for (int i = 0; i < theTemp.length; i++)
		{
			if (theRenderables [i] instanceof HasPostValue)
			{
				theTemp [j++] = (I_Renderable) theRenderables [i];
			}
		}
		
		I_Renderable [] retVal = new I_Renderable [j];
		System.arraycopy (theTemp, 0, retVal, 0, j);
		return retVal;
	}

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
													 String aPhase)
	{
		MExpandPane theExpandPane = (MExpandPane) aRenderable;
		return (theExpandPane.isOpen () ?
					convertToRenderable (theExpandPane.getComponents ()) :
					null);
	}
}