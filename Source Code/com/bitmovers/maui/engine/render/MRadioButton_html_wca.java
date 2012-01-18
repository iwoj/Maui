// =============================================================================
// com.bitmovers.maui.engine.render.MRadioButton_html_wca
// =============================================================================

package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.components.foundation.MRadioButton;
import com.bitmovers.maui.components.foundation.MRadioButtonGroup;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.MComponent;

/**
* MRadioButton_html_wca <p>
* This is the renderer for MRadioButtons
*/
public class MRadioButton_html_wca implements I_Renderer
{
	public String render (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ();
		MRadioButton theRadioButton = (MRadioButton) aRenderable;
		//MContainer theContainer = theRadioButton.getParent ();
		
		retVal.append("<!-- start MRADIOBUTTON -->");
		MComponent theComponent;
		MRadioButtonGroup theGroup = theRadioButton.getRadioButtonGroup ();
		String theGroupName = theGroup.getGroupName ();
		//int theComponentIndex = theContainer.getComponentIndex (theRadioButton);
		//int theComponentCount = theContainer.getComponentCount ();
		int theRadioButtonIndex = theRadioButton.getButtonIndex ();
		
		/*
		//
		//	Render all consecutive MRadioButtons into a single table
		//
		for (int i = theContainer.getComponentIndex (theRadioButton);
				i < theComponentCount &&
				((theComponent = theContainer.getComponent (i)) instanceof MRadioButton);
				i++)
		{
			theRadioButton = (MRadioButton) theComponent;
	    */
			String theComponentID = theRadioButton.getComponentID ();
			
			retVal.append ("<input type=\"radio\" name=\"");
			retVal.append (theGroupName);
			retVal.append ("\" value=\"");
			//retVal.append ("selected_");
			//retVal.append (theRadioButtonIndex);
			retVal.append (theComponentID);
			retVal.append ("\"");
			if (theRadioButton.isSelected ())
			{
				retVal.append (" checked");
			}
			retVal.append (">");
			//retVal.append (theRadioButton.getLabel ());
			retVal.append (theRadioButton.getMLabel().render());
		/*
		}
		*/
		retVal.append ("<!-- end MRADIOBUTTON -->");
		
		return retVal.toString ();
	}
	
	public String [] getRenderPhases ()
	{
		return A_Renderer.getRenderPhases (MRadioButton_html_wca.class);
	}

	/**
	* Get the representative renderer for this renderer (which may not be the same
	* as the actual renderer
	*
	* @return The representative renderer
	*/
	public I_Renderable getRepresentativeRenderable (I_Renderable aRenderable)
	{
		return aRenderable;
	}


	/**
	* Get the event source for this renderer... In some cases this may not be the
	* same as the actual its associated I_Renderable
	*/
	public I_Renderable getEventSource (I_Renderable aRenderable)
	{
		return aRenderable;
	}
	/**
	* Is it okay to generate a phase for this renderer?
	*
	* @param aRenderable The target renderable
	* @param aPhase The String describing the phase being generated
	*
	* @return Boolean indicating if phase generation should be done for this component
	*/
	public boolean generatePhaseOkay (I_Renderable aRenderable, String aPhase)
	{
		return true;
	}
	
	public void finish ()
	{
	}
	// ---------------------------------------------------------------------------
}
