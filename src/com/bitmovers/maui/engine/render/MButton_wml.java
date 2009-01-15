// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MButton;
import com.bitmovers.maui.components.foundation.MLinkable;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;

public class MButton_wml extends DefaultWmlRenderer
	implements I_RendererInitialize,
			   MActionListener
{
	private boolean clicked = false;
	
	public MButton_wml ()
	{
		super ();
	}
	
	public String render (I_Renderable aRenderable)
	{
		return (clicked ? redirect (aRenderable) :
						  doRender (aRenderable));
	}
	private String redirect (I_Renderable aRenderable)
	{
		MButton theButton = (MButton) aRenderable;
		
		if (theButton.getLink () != null)
		{
			MauiApplication theApplication = (MauiApplication) theButton.getRootParent ();
			theApplication.setURLString (theButton.getLink ());
		}
		clicked = false;
		return super.render (aRenderable);
	}
	
	private String doRender (I_Renderable aRenderable)
	{
		String retVal = null;
		
		if (((MComponent) aRenderable).isEnabled ())
		{
			if (aRenderable instanceof MLinkable)
			{
				MLinkable theLinkable = (MLinkable) aRenderable;
			
				if (theLinkable.getLink () != null)
				{
					((MComponent) theLinkable).addActionListener (this);
				}
				else
				{
					((MComponent) theLinkable).removeActionListener (this);
				}
			}
			retVal = generateSimpleAnchor (aRenderable, MActionEvent.ACTION_CLICKED);
		}
		else
		{
			retVal = getLabel (aRenderable);
		}
		return retVal;
	}
	
	protected String getLabel (I_Renderable aRenderable)
	{
		return ((MButton) aRenderable).getLabel ();
	}
	
	
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
			clicked = true;
		}
	}

}
