// ========================================================================
// com.bitmovers.maui.engine.render.MSelectList_html_wca
// ========================================================================

package com.bitmovers.maui.engine.render;

import java.util.Enumeration;

import com.bitmovers.maui.components.foundation.MSelectList;

// ========================================================================
// CLASS: MSelectList_html_wca
// ========================================================================

/** This renderer overrides the default <code>MSelectList</code> renderer
  * to provide Palm-specific functionality.
  * 
  */
  
public class MSelectList_html_wca extends MSelectList_html
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** This method renders select lists identically to 
	  * <code>MSelectList_html.render()</code>, but appends a "Refresh" 
	  * button if auto-refresh is turned on. This is because Palm Web 
	  * Clipping apps do not support JavaScript, and auto-refresh select 
	  * lists need to publish their events as soon as possible. The "Refresh"
	  * button provides a way for the user to initiate a screen update to
	  * reflect any changes brought about by the select list's state change.
	  * 
	  */
	  
	public String render(I_Renderable aRenderable)
	{
		MSelectList selectList = (MSelectList)aRenderable;
		StringBuffer returnValue = new StringBuffer(super.render(aRenderable));
		
		// If auto refresh is turned on, append a "Refresh" button.
		if (selectList.getAutoRefresh())
		{
			returnValue.append("<input type=\"submit\" value=\"Refresh\">\n");
		}
		
		return returnValue.toString();
	}
	
	
}