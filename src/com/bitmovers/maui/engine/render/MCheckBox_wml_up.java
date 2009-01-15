// =============================================================================
// com.bitmovers.maui.engine.render.MCheckBox_wml
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.StringTokenizer;
import java.util.Stack;

import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MCheckBox;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.events.MActionEvent;

// =============================================================================
// CLASS: MCheckBox_wml_up
// =============================================================================

/** The MCheckBox_wml_up is the special renderer to use for the up browser.
  *
  */

public class MCheckBox_wml_up extends MCheckBox_wml
	implements I_RendererInitialize,
			   I_GenerateBackKeyCode,
			   MActionListener,
			   I_SimplePostCard,
			   I_HasPseudoCommand,
			   I_HasDepth
{
	protected boolean isPush = false;
	protected boolean inDepthBasedRender = false;
	protected I_ListGenerator listGenerator = null;
	
	protected void recheckCheckBoxes (I_Renderable aRenderable)
	{
		MCheckBox theCheckBox = (MCheckBox) aRenderable;
		container = theCheckBox.getParent ();
		
		listGenerator = (container.getRenderer () instanceof I_ListGenerator ?
							((I_ListGenerator) container.getRenderer ()) :
							this);
		
		int theIndex = container.getComponentIndex (theCheckBox);
		if (theIndex == 0 ||
			! (container.getComponent (theIndex - 1) instanceof MCheckBox))
		{
			renderable = theCheckBox;
			representativeRenderable = theCheckBox;
			firstIndex = theIndex;
			theCheckBox.addActionListener (this);
		}
		else
		{
			theCheckBox.removeActionListener (this);
		}
		checkboxes = getCheckBoxes (aRenderable);
	}
	
	protected String getLabel (I_Renderable aRenderable)
	{
		return (inDepthBasedRender ? super.getLabel (aRenderable) :
									 getSummaryLabel (aRenderable, ",", false));
	}
	
	public I_Renderable getRepresentativeRenderable (I_Renderable aRenderable)
	{
		return renderable;
	}
	
	
	private String doGenerateBackKeyCode ()
	{
		StringBuffer retVal = new StringBuffer ("<template>\n<do type=\"OK\" label=\"OK\">\n");
		retVal.append (generatePostList (renderable));
		retVal.append ("</do>\n</template>\n");
		return retVal.toString ();
	}
		
							 	   	
	/**
	* Get the back key code.
	*
	* @return The back key code.  If this is null, then allow the MFrame to generate
	* its default code
	*/
	public String generateBackKeyCode ()
	{
		//return (inDepthBasedRender ?
		//			doGenerateBackKeyCode () :
		//			null);
		return "";//generatePostGoStatement (renderable).toString ();
	}
	// ---------------------------------------------------------------------------
	// METHOD: render
	// ---------------------------------------------------------------------------
	
	public String render (I_Renderable aRenderable)
	{
		String retVal = null;
		
		recheckCheckBoxes (aRenderable);
		if (aRenderable == renderable || listGenerator != this)	
		{
			renderable = aRenderable;
			retVal = generateSimpleAnchor (aRenderable, MActionEvent.ACTION_PUSH);
		}
		return (retVal == null ? "" : retVal);
	}
	
	public String depthBasedRender (I_Renderable aRenderable,
									Stack aStack,
									String aBackout)
	{
		StringBuffer retVal = new StringBuffer (super.render (aRenderable, listGenerator));
		retVal.append (generateDoneButton (aRenderable));
		return retVal.toString ();
	}
		
	/**
	* Get the pseudo command target
	*
	* @return The pseudo command target
	*/
	public String getPseudoCommandTarget ()
	{
		return (inDepthBasedRender ? "emptyIsSignificant" : null);
	}
	
	/**
	* Get the pseudo command value
	*
	* @return The pseudo command value
	*/
	public String getPseudoCommandValue ()
	{
		return (inDepthBasedRender ? "true" : null);
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
		return checkboxes;
	}
	
	/**
	* Test if the event indicates that deep navigation is occuring or not.
	*
	* @param aActionEvent The MActionEvent describing the component's event
	*
	* @return Boolean indicating if this is deep navigation or not
	*/
	public boolean isDeepNavigating (MActionEvent aEvent, Stack aStack)
	{
		return (aEvent.getActionCommand ().equals (MActionEvent.ACTION_PUSH));
	}
	
	/**
	* Notify the renderer that it is being backed out.  This is so it can do
	* whatever cleanup is necessary
	*
	* @param aRenderable The I_Renderable object
	*/
	public void backout (I_Renderable aRenderable)
	{
		inDepthBasedRender = false;
	}
	
	/**
	* Get the component id's from the request string
	*
	* @param aComponentID	The component ID
	* @param aRequestValue The request value to parse
	*
	* @return The list of component ids
	*/
	public String [] getComponentIDs (String aComponentID, String aRequestValue)
	{
		String [] retVal = null;
		
		isPush = aRequestValue != null && aRequestValue.equals (MActionEvent.ACTION_PUSH);
		
		if (!isPush)
		{
			retVal = super.getComponentIDs (aComponentID, aRequestValue);
		}
		return (retVal == null ? new String [] {aComponentID} :
								 retVal);
	}
	
	/**
	* Get the state data for a given component
	*
	* @param aComponent The component
	* @param aStateData		Any state data that can be gathered
	*
	* @return The state data to associate with the component
	*/
	public String getStateData (String aComponentID, String aStateData)
	{
		return (isPush ? MActionEvent.ACTION_PUSH :
						 super.getStateData (aComponentID, aStateData));
	}

	public void actionPerformed (MActionEvent aActionEvent)
	{
		String theActionCommand = aActionEvent.getActionCommand ();
		inDepthBasedRender = (theActionCommand != null &&
							  theActionCommand.equals (MActionEvent.ACTION_PUSH));
	}	
	
}