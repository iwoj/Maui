// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.foundation.MTabbedPane;

public class MTabbedPane_wml_up extends A_MSelectList_wml_up
	implements I_SimplePhase,
			   I_SimplePostCard
{
	public MTabbedPane_wml_up ()
	{
		super ();
		needsEpilogue = true;
	}
	
	protected String generateLocalHref (I_Renderable aRenderable)
	{
		MTabbedPane theComponent = (MTabbedPane) aRenderable;
		return "<anchor> <go href=\"#" + generateComponentID (theComponent) + "_Card\"/> " +
							   theComponent.getTabName (theComponent.getSelectedTab ()) + "</anchor>";
	}
	
	public String render (I_Renderable aRenderable)
	{
		onPick = generateComponentID ((MComponent) aRenderable) + "_TabCard";
		StringBuffer retVal = new StringBuffer (generateLocalHref (aRenderable));
		MTabbedPane theTabbedPane = (MTabbedPane) aRenderable;
		retVal.append ("<br/>");
		retVal.append (doRenderComponent (theTabbedPane.getSelectedComponent ()));
		return retVal.toString ();
	}
	
	public String generateEpilogue (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer (super.generateEpilogue (aRenderable));
		retVal.append ("\n");
		retVal.append (generateGoCard ((String) onPick,
									   aRenderable,
									   "$(" + generateComponentID ((MComponent) aRenderable) + ")"));//":e)"));
		return retVal.toString ();
	}
	
	protected MComponent [] getComponents (MContainer aContainer)
	{
		return new MComponent [] {((MTabbedPane) aContainer).getSelectedComponent ()};
	}
	
	protected String getLabel (I_Renderable aRenderable)
	{
		MTabbedPane theTabbedPane = (MTabbedPane) aRenderable;
		return theTabbedPane.getTabName (theTabbedPane.getSelectedTab ());
	}
	/**
	* Get all of the components that should be included as part of this simple post
	* card
	*
	* @param aRenderable The current component (current point in deep navigation)
	* @param aPhase The phase being generated
	*
	* @return The I_Renderable array
	*/
	public I_Renderable [] getSimplePhaseComponents (I_Renderable aRenderable,
													 String aPhase)
	{
		MTabbedPane theTabbedPane = (MTabbedPane) aRenderable;
		return convertToRenderable (
				new MComponent [] {theTabbedPane.getSelectedComponent ()});
	}
}