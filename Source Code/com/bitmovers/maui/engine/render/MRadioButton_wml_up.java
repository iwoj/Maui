// =============================================================================
// com.bitmovers.maui.engine.render.MRadioButton_wml
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Stack;

import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MRadioButton;
import com.bitmovers.maui.components.foundation.MRadioButtonGroup;
import com.bitmovers.maui.events.MActionEvent;

public class MRadioButton_wml_up extends MSelectList_wml_up
	//implements I_HasEpilogue
	//		   I_HasForwardPrologue
{
	public MRadioButton_wml_up ()
	{
		super ();
	}
	
	private MRadioButton getRadioButton (I_Renderable aRenderable)
	{
		MRadioButtonGroup theGroup = (aRenderable instanceof MRadioButtonGroup ?
										(MRadioButtonGroup) aRenderable :
										((MRadioButton) aRenderable).getRadioButtonGroup ());
		return theGroup.getSelectedButton ();
	}
	
	public String render (I_Renderable aRenderable)
	{
		String retVal = null;
		MRadioButton theRadioButton = (MRadioButton) aRenderable;
		renderable = theRadioButton;
		if (theRadioButton.getButtonIndex () == 0)
		{
			MRadioButtonGroup theRadioButtonGroup = theRadioButton.getRadioButtonGroup ();
			representativeRenderable = theRadioButtonGroup;
			//onPick = generateComponentID ((MComponent) aRenderable) + "_RadioCard";
			//allowMultiples = true;
			retVal = super.render (renderable);
		}
		return (retVal == null ? "" : retVal);
	}
	
	public boolean isDeepNavigating (MActionEvent aActionEvent, Stack aStack)
	{
		return (aActionEvent.getActionCommand ().equals (MActionEvent.ACTION_PUSH));
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
		/*MRadioButtonGroup theGroup = (MRadioButtonGroup) representativeRenderable;
		onPick = new String [theGroup.getRadioButtonCount ()];
		String [] theOnPick = (String []) onPick;
		for (int i = 0; i < theOnPick.length; i++)
		{
			theOnPick [i] = theGroup.getRadioButton (i).getLabel ();
		}
		onPickGenerator = new DefaultOnPickGenerator (this);*/
		return super.depthBasedRender (representativeRenderable,
									   aStack,
									   aBackout);
	}
	
	protected I_OnPickGenerator getOnPickGenerator (I_Renderable aRenderable)
	{
		return new DefaultOnPickGenerator (this);
	}
	
	protected A_Renderer getDepthBasedRenderer (I_Renderable aRenderable)
	{
		return this;
	}
							 	   	
	protected String getLabel (I_Renderable aRenderable)
	{
		return getRadioButton (aRenderable).getLabel ();
	}
	
	public String getValue (I_Renderable aRenderable)
	{
		String retVal = null;
		
		if (representativeRenderable != null)
		{
			retVal = ((MRadioButtonGroup) representativeRenderable).
							getSelectedButton ().getLabel ();
		}
		else if (((MRadioButton) aRenderable).getButtonIndex () == 0)
		{
			retVal = ((MRadioButton) aRenderable).getLabel ();
		}
		return retVal;
	}
	
	protected MauiApplication getMauiApplication (I_Renderable aRenderable)
	{
		return (MauiApplication) getRadioButton (aRenderable).getRootParent ();
	}

	/**
	* Get the representative renderer for this renderer (which may not be the same
	* as the actual renderer
	*
	* @return The representative renderer
	*/
	public I_Renderable getRepresentativeRenderable (I_Renderable aRenderable)
	{
		return (representativeRenderable == null ? null : renderable);
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
		return super.getSimplePostCardComponents (aRenderable);
	}
	
	public String generateComponentID(MComponent aComponent)
	{
		return super.generateComponentID ((aComponent instanceof MRadioButtonGroup ? (MComponent) renderable : 
		                                                                             aComponent));
	}
	
	public boolean autoPop (MActionEvent aActionEvent, Stack aStack)
	{
		return (inDepthBasedRender && !aActionEvent.getActionCommand ().equals (MActionEvent.ACTION_PUSH));
	}
	
}