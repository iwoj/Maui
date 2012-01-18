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

public abstract class A_MSelectList_wml_up extends DefaultWmlRenderer
	implements I_HasEpilogue,
			   I_HasForwardPrologue
{
	protected Object onPick = null;
	protected boolean needsEpilogue = false;
	
	public String generateEpilogue (I_Renderable aRenderable)
	{
		String retVal = null;
		
		if (needsEpilogue)
		{
			Settable theSelectList = (Settable) aRenderable;
			String theComponentID = generateComponentID ((MComponent) aRenderable);
			String theSelection =
				MSelectList_wml.generateSelectList (this,
													theComponentID,
													((HasSelectList) theSelectList).getSelectListOptions (),
													theSelectList.getValue ().toString (),
													"  ",
													onPick,
													false,
													new SimpleOnPickGenerator ());
			retVal = generateCard (theComponentID + "_Card",
										 "select",
										 theSelection);
		}
		return (retVal == null ? "" : retVal.toString ());
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
		return new I_Renderable [] {aRenderable};
	}
}
