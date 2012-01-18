package com.bitmovers.maui.engine.render;

import java.awt.Dimension;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.*;


// ========================================================================
// CLASS: MImage_wml
// ========================================================================

/** The MImage_wml class is the default WML renderer for the MImage class.
  * 
  * @invisible
  * 
  */

public class MImage_wml implements I_Renderer
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** Renders the WML for the MImage component, which is presently an empty
	  * string.
	  *
	  */
	
	public String render(I_Renderable renderable)
	{
		return "";
		
		/*
		try
		{
			// [1] Cast our Renderable object to an MImage. We will need this to
			//     get the info we need from the component.
			MImage image = (MImage)renderable;

			// [2] Construct the HTML tag we need to return.
			StringBuffer buffer = new StringBuffer();
			String description = image.getDescription();
			Dimension size = image.getSize(); 

			buffer.append("[IMAGE");

			if (description.length() > 0)
			{
				buffer.append(": ");
				buffer.append(description);
			}
			
			buffer.append("]");
			
			return buffer.toString();
		}
		catch (ClassCastException exception)
		{
			System.err.println(new WarningString("A component was passed to the MImage_wml rendered which was not an MImage."));
			return "";
		}
		*/
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getRenderPhases()
	// ----------------------------------------------------------------------
	
	public String[] getRenderPhases()
	{
		return A_Renderer.getRenderPhases (MImage_wml.class);
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
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF