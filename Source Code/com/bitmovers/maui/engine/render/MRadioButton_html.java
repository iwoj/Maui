// =============================================================================
// com.bitmovers.maui.engine.render.MRadioButton_html
// =============================================================================

package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.components.foundation.MRadioButton;
import com.bitmovers.maui.components.foundation.MRadioButtonGroup;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.MComponent;

/**
* MRadioButton_html <p>
* This is the renderer for MRadioButtons
*/
public class MRadioButton_html implements I_Renderer
{
	public String render (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ();
		MRadioButton theRadioButton = (MRadioButton) aRenderable;
		MContainer theContainer = theRadioButton.getParent ();
		
		retVal.append("<!-- start MRADIOBUTTON --><table border=\"0\" cellpadding=\"0\" cellspacing=\"3\">");
		MComponent theComponent;
		MRadioButtonGroup theGroup = theRadioButton.getRadioButtonGroup ();
		String theGroupName = theGroup.getGroupName ();
		int theComponentIndex = theContainer.getComponentIndex (theRadioButton);
		int theComponentCount = theContainer.getComponentCount ();
		
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
			retVal.append ("<tr><td valign=\"top\"><input type=\"radio\" name=\"");
			retVal.append (theGroupName);
			retVal.append ("\" value=\"");
			//++ 405 JL 2001.09.21
			//retVal.append ( (String) theRadioButton.getValue ());
			retVal.append (theComponentID);
			//--
			retVal.append ("\" onClick=\"registerEvent('");
			retVal.append (theComponentID);
			
			retVal.append("')\"");
			if (theRadioButton.isSelected ())
			{
				retVal.append (" checked");
			}
			retVal.append ("></td><td>");
			//retVal.append (theRadioButton.getLabel ());
			retVal.append (theRadioButton.getMLabel().render());
			retVal.append ("</td></tr>");
		/*
		}
		*/
		retVal.append ("</table><!-- end MRADIOBUTTON -->");
		
		return retVal.toString ();
	}
	
	public String [] getRenderPhases ()
	{
		return A_Renderer.getRenderPhases (MRadioButton_html.class);
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
