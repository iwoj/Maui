// =============================================================================
// com.bitmovers.maui.engine.render.MCheckBox_html
// =============================================================================

package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.*;


// =============================================================================
// CLASS: MCheckBox_html
// =============================================================================

/** The MCheckBox_html class is the default HTML renderer for the MCheckBox
  * class.
  *
  */

public class MCheckBox_html implements I_Renderer
{
	// ---------------------------------------------------------------------------
	// METHOD: render
	// ---------------------------------------------------------------------------
	
	/** Renders the HTML for the MCheckBox component.
	  *
	  */
	
	public String render(I_Renderable renderable)
	{
		try
		{
			// [1] Cast our Renderable object to an MCheckBox. We will need this to
			//     get the info we need from the component.
			MCheckBox checkbox = (MCheckBox)renderable;

			// [2] Construct the HTML tag we need to return.
			StringBuffer buffer = new StringBuffer();
			String componentID = checkbox.getComponentID();
			
			// <!-- start MCHECKBOX -->
			buffer.append("<!-- start MCHECKBOX -->");
			
			if (checkbox.getLabel() != null && !checkbox.getLabel().equals(""))
			{
				buffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"3\"><tr><td valign=\"top\">");
			}
			
			buffer.append("<input type=\"checkbox\" name=\"");
			buffer.append(componentID);
			buffer.append("\" value=\"");
			buffer.append((String)checkbox.getValue());
			buffer.append("\" onClick=\"registerEvent('");
			buffer.append(componentID);
			buffer.append("')\"");
			
			if (checkbox.isChecked())
			{
				buffer.append(" checked");
			}
			
			buffer.append(">");
			
			if (checkbox.getLabel() != null && !checkbox.getLabel().equals(""))
			{
				buffer.append("</td><td>");
				buffer.append(checkbox.getLabel());
				buffer.append("</td></tr></table>");
			}
			
			// <!-- end MCHECKBOX -->
			buffer.append("<!-- end MCHECKBOX -->");
			
			return buffer.toString();
		}
		catch (ClassCastException exception)
		{
			System.err.println(new WarningString("A component was passed to the MCheckBox_html rendered which was not an MCheckBox."));
			return "";
		}
	}
	
	public String [] getRenderPhases ()
	{
		return A_Renderer.getRenderPhases (MCheckBox_html.class);
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
