package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.StringParser;


// ========================================================================
// CLASS: MFlowLayout_html                       (c) 2001 Bitmovers Systems
// ========================================================================

/** MFlowLayout_html is the HTML renderer for the FlowLayout layout 
  * manager.
  *
  */

public class MFlowLayout_html extends DefaultHtmlLayoutRenderer implements I_RendererInitialize
{
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	public MFlowLayout_html()
	{
		super();
	}


	// ----------------------------------------------------------------------
	// METHOD: renderSeparatedComponent
	// ----------------------------------------------------------------------
	
	/** renderSeparatedComponent() overrides A_Renderer.renderSeparatedComponent()
	  * because in BoxLayout, the separator between each component may be
	  * different, as alignment will differ between each component.
	  *
	  */

	protected String renderSeparatedComponent(MComponent component, String separator)
	{
		StringBuffer rendered = new StringBuffer();
		
		try
		{
			MContainer container = component.getParent();
			Object constraints = container.getConstraints(component);

			if (constraints != null)
			{
				System.out.println(new DebugString("FlowLayout does not support any constraints. Ignoring..."));
			}

			MFlowLayout layout = (MFlowLayout)container.getLayout();

			final int lastComponent = (container.getComponentCount() - 1);
			final int componentIndex = container.getComponentIndex(component); 

			rendered.append(super.doRenderComponent(component));

			if (componentIndex != lastComponent)
			{
				rendered.append(" ");
			}
		}
		catch (Exception exception)
		{
	  	System.err.println(new WarningString("MFlowLayout_html.renderSeparatedComponent(): " + exception.getMessage()));
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