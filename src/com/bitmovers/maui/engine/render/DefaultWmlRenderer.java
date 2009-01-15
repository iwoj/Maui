// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.foundation.HasPostValue;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.MauiApplication;

public class DefaultWmlRenderer extends A_Renderer
{
	public DefaultWmlRenderer (I_Renderable aRenderable,
							   String [] aClientClassification)
	{
		super (aRenderable, aClientClassification);
	}
	
	public DefaultWmlRenderer ()
	{
		super ();
	}
	
	protected StringBuffer generateGoCard (String aCardName,
										   I_Renderable aRenderable,
										   String aValue)
	{
		StringBuffer retVal = new StringBuffer ("\n\n<card id=\"");
		if (aCardName == null)
		{
			System.out.println ("Uh oh");
		}
		retVal.append (aCardName);
		retVal.append ("\" title=\"goCard\">\n");
		retVal.append ("<onevent type=\"onenterforward\">\n");
		retVal.append (generatePostList (aRenderable, aValue));
		retVal.append ("</onevent>\n");
		retVal.append ("</card>\n");
		return retVal;
	}
	
	protected HasPostValue [] getValuePosters (I_Renderable aRenderable)
	{
		HasPostValue [] theValues = super.getValuePosters (aRenderable);
		HasPostValue [] retVal = null;
		MComponent theTop = ((MComponent) aRenderable).getRootParent ();
		if (theTop instanceof MauiApplication)
		{
			retVal = new HasPostValue [theValues.length + 1];
			System.arraycopy (theValues, 0, retVal, 1, theValues.length);
			retVal [0] = (HasPostValue) theTop;
		}
		
		return (retVal == null ? theValues : retVal);
	}
	
	/*protected StringBuffer generatePostHeader ()
	{
		return new StringBuffer ("<go href=\"\" method=\"post\">\n");
	}*/
	
	protected StringBuffer generatePostFooter ()
	{
		return new StringBuffer ("</go>\n");
	}
}