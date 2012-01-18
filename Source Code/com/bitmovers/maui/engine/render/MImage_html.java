// =============================================================================
// com.bitmovers.maui.engine.render.MImage_html
// =============================================================================

package com.bitmovers.maui.engine.render;

import java.awt.Dimension;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.httpserver.HTTPSession;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.*;

// =============================================================================
// CLASS: MImage_html
// =============================================================================

/** The MImage_html class is the default HTML renderer for the MImage class.
  *
  */

public class MImage_html implements I_Renderer
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
			// [1] Cast our Renderable object to an MImage. We will need this to
			//     get the info we need from the component.
			MImage image = (MImage)renderable;

			// [2] Construct the HTML tag we need to return.
			StringBuffer buffer = new StringBuffer();
			String imagePath = image.getImagePath();
			
			Dimension size = image.getSize(); 

			// Try to get the client's dimensions so we can scale the image down
			// if necessary...
			HTTPSession session = ((MauiApplication)((MComponent)renderable).getRootParent()).getSession();
			{
				Dimension clientSize = session.getClientDimension();
				
				if (clientSize != null)
				{
					if (size.width > clientSize.width)
					{
						size.height = (int)((float)size.height / (float)size.width * (float)clientSize.width);
						size.width = clientSize.width;
					}
				}
			}
			
			buffer.append("<!-- start MIMAGE -->");
			buffer.append("<img src=\"");
			buffer.append(image.getServletURL ());
			buffer.append("?getImage=true&path=");
			buffer.append(imagePath);
			buffer.append("\" width=\"");
			buffer.append(size.width);
			buffer.append("\" height=\"");
			buffer.append(size.height);
			buffer.append("\" border=\"0\" alt=\"");
			buffer.append(image.getDescription());
			buffer.append("\">");
			buffer.append("<!-- end MIMAGE -->");
			
			return buffer.toString();
		}
		catch (ClassCastException exception)
		{
			System.err.println(new WarningString("A component was passed to the MImage_html rendered which was not an MImage."));
			return "";
		}
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: getRenderPhases()
	// ---------------------------------------------------------------------------

	public String[] getRenderPhases()
	{
		return A_Renderer.getRenderPhases (MImage_html.class);
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
