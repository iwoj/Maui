package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import com.bitmovers.maui.components.foundation.MButton;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.MauiApplication;


// ========================================================================
// CLASS: MButton_html                           (c) 2001 Bitmovers Systems
// ========================================================================

public class MButton_html extends A_Renderer
	implements I_RendererInitialize,
						 MActionListener
{
	// ----------------------------------------------------------------------
	// METHOD: getResourceClassName
	// ----------------------------------------------------------------------

	protected String getResourceClassName(I_Renderable aRenderable)
	{
		return MButton.class.getName ();
	}

	
	// ----------------------------------------------------------------------
	// METHOD: getTemplateTypes
	// ----------------------------------------------------------------------

	protected String [] getTemplateTypes()
	{
		return new String [] {"", "off"};
	}


	// ----------------------------------------------------------------------
	// METHOD: getRenderTemplate
	// ----------------------------------------------------------------------
	
	protected String getRenderTemplate(I_Renderable aRenderable)
	{
		MButton theButton = (MButton)aRenderable;
		return renderTemplate [theButton.isEnabled () ? 0 : 1];
	}


	// ----------------------------------------------------------------------
	// METHOD: doRender
	// ----------------------------------------------------------------------
	
	protected String doRender(I_Renderable aRenderable)
	{
		MButton theButton = (MButton) aRenderable;
		
		if (theButton.getLink () != null)
		{
			theButton.addActionListener (this);
		}
		else
		{
			theButton.removeActionListener (this);
		}
		return super.render (aRenderable);
	}


	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	public String render (I_Renderable aRenderable)
	{
		return doRender (aRenderable);
	}


	// ----------------------------------------------------------------------
	// METHOD: actionPerformed
	// ----------------------------------------------------------------------
	
	public void actionPerformed (MActionEvent aActionEvent)
	{
		MButton theButton = (MButton) aActionEvent.getSource ();
		//
		//	If this is an external URL, then setup redirection to the link.  Since this should take precedence, we must
		//	guarantee that this occurs, rather than any other of kind of redirection (like application chaining).
		//
		
		String theURL = theButton.getLink ();
		if (theURL != null && theURL.indexOf ("://") != -1)
		{
			MauiApplication theApplication = (MauiApplication) theButton.getRootParent ();
			theApplication.setURLString (theButton.getLink ());
		}
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF