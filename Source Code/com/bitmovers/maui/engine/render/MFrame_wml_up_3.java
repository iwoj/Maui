// =============================================================================
// com.bitmovers.maui.engine.render.MFrame_wml_up_3
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* MFrame_wml_up_3 <p>
* @invisible
* This is the primary render for up 3.x.  Since the browser gets upset with 10
* consecutive deck loads where the first card contains "onenterforward" or "onenterbackward"
* the cycle must be broken.
*/
public class MFrame_wml_up_3 extends MFrame_wml_up
{
	protected int counter = 0;
	protected String doGeneratePrologueOpening (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ();
		//if (counter++ >= 5)
		//{
			retVal.append ("<card id=\"timer\" ontimer=\"#Main_");
			//MauiApplication theApplication =
			//	(MauiApplication) ((MComponent) aRenderable).getRootParent ();
			//retVal.append (theApplication.getChainApplicationName ());
			//retVal.append ("/pseudo/timer_reload");
			retVal.append (Integer.toString (currentMain));
			retVal.append ("\">\n<timer value=\"1\"/>\n</card>\n");
			//counter = 0;
		//}
		retVal.append (super.doGeneratePrologueOpening (aRenderable));
		return retVal.toString ();
	}
}