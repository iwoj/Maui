// =============================================================================
// com.bitmovers.maui.engine.render.I_Renderer
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.foundation.MTabbedPane;

public class MTabbedPane_wml extends MSelectList_wml
	implements I_HasEpilogue,
			   I_SimplePhase,
			   I_SimplePostCard
{
	public MTabbedPane_wml ()
	{
		super ();
	}
	
	public String render (I_Renderable aRenderable)
	{
		onPick = generateComponentID ((MComponent) aRenderable) + "_TabCard";
		StringBuffer retVal = new StringBuffer (super.render (aRenderable,
															  new SimpleOnPickGenerator ()));
		MTabbedPane theTabbedPane = (MTabbedPane) aRenderable;
		retVal.append ("<br/>");
		retVal.append (doRenderComponent (theTabbedPane.getSelectedComponent ()));
		return retVal.toString ();
	}
	
	public String generateEpilogue (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ("\n");
		retVal.append (generateGoCard (onPick,
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
	/**
	* Get all of the components that should be included as part of this simple post
	* card
	*
	* @param aRenderable The current component (current point in deep navigation)
	*
	* @return The I_Renderable array
	*/
	public I_Renderable [] getSimplePostCardComponents (I_Renderable aRenderable)
	{
		MTabbedPane theTabbedPane = (MTabbedPane) aRenderable;
		return convertToRenderable (
				new MComponent [] {theTabbedPane.getSelectedComponent ()});
	}
}