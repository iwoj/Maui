package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.StringParser;


// ========================================================================
// CLASS: MBoxLayout_html_wca                    (c) 2001 Bitmovers Systems
// ========================================================================

/** MBoxLayout_html_wca is the Palm WebClipping renderer for the MBoxLayout 
  * layout manager.
  * 
  */

public class MBoxLayout_html_wca extends DefaultHtmlLayoutRenderer implements I_RendererInitialize
{
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	public MBoxLayout_html_wca()
	{
		super();
	}


	// ----------------------------------------------------------------------
	// METHOD: setupAlignment
	// ----------------------------------------------------------------------

	public void setupAlignment(I_Renderable aRenderable, StringParser aStringParser)
	{
		super.setupAlignment(aRenderable, aStringParser);

		MBoxLayout theLayout = (MBoxLayout)aRenderable;

  	switch (theLayout.getAxis())
  	{
  		//
  		// X-axis layout does not pay attention to alignment because nested tables
  		// are not allowed in Palm Web Clippings. [ian - 2001.06.25]
  		//
  		case MBoxLayout.X_AXIS:
  		{
  			separator = " ";
  			break;
  		}
  		case MBoxLayout.Y_AXIS:
  		{
  			separator = "</p>\n<p align=\"^^align^^\">";
  			break;
  		}
  	}
	}


	// ---------------------------------------------------------------------------
	// METHOD: renderSeparatedComponent
	// ---------------------------------------------------------------------------
	
	/** renderSeparatedComponent() overrides A_Renderer.renderSeparatedComponent()
	  * because in BoxLayout, the separator between each component may be
	  * different, as alignment will differ between each component.
	  *
	  */

	protected String renderSeparatedComponent(MComponent component, String separator)
	{
		StringBuffer rendered = new StringBuffer();
		MContainer container = component.getParent();
		
		try
		{
			Object constraints = container.getConstraints(component);
			
			// Handle improper contraint objects.
			if (!(constraints instanceof MLayout.Alignment))
			{
				if (constraints != null)
				{
					constraints = container.getLayout().getAlignment();
					System.out.println(new DebugString("MBoxLayout only supports only constraint objects of type Alignment. Use BoxLayout.LEFT, .CENTER, and .RIGHT for constraints with this LayoutManager. Defaulting to " + (MLayout.Alignment)constraints) + ".");
				}
				else
				{
					constraints = container.getLayout().getAlignment();
				}
			}

			MBoxLayout layout = (MBoxLayout)container.getLayout();

			final int lastComponent = (container.getComponentCount() - 1);
			final int componentIndex = container.getComponentIndex(component); 

			switch (componentIndex)
			{
				// First component
				case 0:
				{
					// If there is only one component...
					if (componentIndex == lastComponent)
					{
						rendered.append("<p align=\"" + constraints + "\">");
						rendered.append(super.doRenderComponent(component));
						rendered.append("</p>");
					}
					else
					{
						rendered.append("<p align=\"" + constraints + "\">");
						rendered.append(super.doRenderComponent(component));
					}
					
					break;
				}
				
				// All others
				default:
				{
					// If this is the last component...
					if (componentIndex == lastComponent)
					{
						rendered.append("<p align=\"" + constraints + "\">");
						rendered.append(super.doRenderComponent(component));
						rendered.append("</p>");
					}
					else
					{
						rendered.append("<p align=\"" + constraints + "\">");
						rendered.append(super.doRenderComponent(component));
					}

					break;
				}
			}
		}
		catch (Exception exception)
		{
	  	System.err.println(new WarningString("MBoxLayout_html_wca.renderSeparatedComponent(): " +
	  																			 "(Container: " + container + ")" +
	  																			 "(Component: " + component + ")" +
	  																			 " (" + exception.toString () + ")"));
	  	exception.printStackTrace ();
		}

		return rendered.toString();
	}


	// ----------------------------------------------------------------------
	// METHOD: renderComponents
	// ----------------------------------------------------------------------
	
	/** renderComponents() overrides A_Renderer.renderComponents() because we need
	  * to take a slightly different approach to rendering all the components.
	  * Since renderSeparatedComponent() prepends the separator instead of
	  * appending it, we want to call it for every component, not just some.
	  *
	  */

	protected String renderComponents(MContainer parent, String separator)
	{
		StringBuffer buffer = new StringBuffer();

		MComponent[] components = parent.getComponents();

  	if (components.length > 0) 
  	{
    	for (int i = 0; i < components.length; i++)
    	{
    		buffer.append(this.renderSeparatedComponent(components[i], separator));
    	}
  	}
  	
  	return buffer.toString();
	}
	
	
}


// ========================================================================
//                                               (c) 2001 Bitmovers Systems