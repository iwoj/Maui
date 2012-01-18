package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.StringParser;


// ========================================================================
// CLASS: MBorderLayout_html                     (c) 2001 Bitmovers Systems
// ========================================================================

/** MBorderLayout_html is the HTML renderer for the MBoxLayout layout 
  * manager.
  * 
  */

public class MBorderLayout_html extends DefaultHtmlLayoutRenderer implements I_RendererInitialize
{
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	public String render(I_Renderable layout)
	{
		MContainer parent = ((MLayout)layout).getParentContainer();
		
		return this.renderComponents(parent, null);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: renderComponents
	// ----------------------------------------------------------------------
	
	public String renderComponents(MContainer parent, String separator)
	{
		try
		{
			StringParser parser = new StringParser();

			MComponent[] components = parent.getComponents();

	  	if (components.length > 0) 
	  	{
	    	for (int i = 0; i < components.length; i++)
	    	{
	    		MBorderLayout.CompassDirection position = null;
	    		MLayout.Alignment alignment = null;

	    		try
	    		{
		    		Object constraint = parent.getConstraints(components[i]);
		    		
		    		// If the constraint is an Object[], we have more than one constraint
		    		// to work with.
		    		if (constraint instanceof Object[])
		    		{
							Object[] constraints = (Object[])constraint;
							
		    			position = (MBorderLayout.CompassDirection)constraints[0];
		    			alignment = (MLayout.Alignment)constraints[1];
		    		}
		    		else if (constraint instanceof MBorderLayout.CompassDirection)
		    		{
		    			position = (MBorderLayout.CompassDirection)constraint;
		    			alignment = ((MBorderLayout)parent.getLayout()).getAlignment();
		    		}
		    		else
		    		{
							throw new ClassCastException();
		    		}
		    	}
		    	catch (ClassCastException exception)
		    	{
	    			System.err.println(new WarningString("An invalid constraint was passed to BorderLayout. Use BorderLayout.NORTH, SOUTH, EAST, WEST, or CENTER. Using default position of CENTER."));
	    			position = MBorderLayout.CENTER;
	    			alignment = ((MBorderLayout)parent.getLayout()).getAlignment();
		    	}

	  			parser.setVariable(position.toString(), doRenderComponent (components[i]));
	  			parser.setVariable(position.toString() + "Align", alignment.toString());
	    	}
	  	}
	  	
	  	return parser.parseString(super.getRenderTemplate((I_Renderable)parent.getLayout()));
	  }
	  catch (Exception exception)
	  {
	  	System.err.println(new ErrorString(exception, "MBorderLayout_html.renderComponents()"));
	  	
	  	return null;
	  }
	}
	
}


// ========================================================================
//                                               (c) 2001 Bitmovers Systems