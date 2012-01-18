// =============================================================================
// com.bitmovers.maui.engine.render.MSelectList_html
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import com.bitmovers.maui.components.foundation.MSelectList;

public class MSelectList_html
	implements I_Renderer
{
	public String render (I_Renderable aRenderable)
	{
		String retVal = null;
		MSelectList theSelectList = (MSelectList) aRenderable;
		Enumeration enumerator = theSelectList.getSelectListOptions ().elements();
		boolean defaultValue = false;
		
		if (theSelectList.getAutoSubmit ())
		{
			retVal = theSelectList.getIndentation () +
					  "<select name=\"" + theSelectList.getComponentID () +
					  "\" onChange=\"registerEvent('" + theSelectList.getComponentID()
					  + "'); doSubmit()\">\n";
		}
		else
		{	
			retVal = theSelectList.getIndentation () +
					 "<select name=\"" + theSelectList.getComponentID () +
					 "\" onChange=\"registerEvent('" +
					 theSelectList.getComponentID() + "')\">\n";
		}
		
		while (enumerator.hasMoreElements())
		{
			String[] element = (String[])enumerator.nextElement();
			String value = element[0];
			String label = element[1];
			
			if (value.equals(theSelectList.getValue ().toString()))
			{
				retVal += theSelectList.getIndentation () +
						   " <option value=\"" + value +
						   "\" selected>" + label + "</option>\n";
			}
			else
			{
				retVal += theSelectList.getIndentation () +
							" <option value=\"" + value +
							"\">" + label + "</option>\n";
			}
		}
		
		retVal += theSelectList.getIndentation () + "</select>\n";
		return retVal;
	}
	
	public String [] getRenderPhases ()
	{
		return A_Renderer.getRenderPhases (MSelectList_html.class);
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