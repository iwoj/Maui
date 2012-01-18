// ========================================================================
// com.bitmovers.maui.engine.render.I_HasDepth
// ========================================================================

package com.bitmovers.maui.engine.render;

import java.util.Stack;
import com.bitmovers.maui.events.MActionEvent;


// ========================================================================
// CLASS: I_HasDepth
// ========================================================================

/** This is the interface for renderers which use "deep navigation". Deep
  * navigation is a technique where a component's state is spread across
  * multiple screens, in a tree structure. This enables a component to use
  * minimal screen real estate, while still being fully functional.<p>
  * 
  * Deep navigation is currently only used on devices with small screens,
  * such as WAP devices.
  * 
  */
  
public interface I_HasDepth
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: depthBasedRender
	// ----------------------------------------------------------------------
		
	/** Notify the component to do some "drill down".
	  *
	  * @param aRenderable The renderable MComponent
	  * 
	  * @param aStack The Stack which represents the current navigation depth
	  * 
	  * @param aBackout A command to be included as part of the backout
	  *
	  * @return The rendered string
	  * 
	  */
	  
	public String depthBasedRender(I_Renderable aRenderable,
	                               Stack aStack,
	                               String aBackout);
	
	
	// ----------------------------------------------------------------------
	// METHOD: backout
	// ----------------------------------------------------------------------
	
	/** Notify the renderer that it is being backed out.  This is so it can 
	  * do whatever cleanup is necessary.
	  *
	  * @param aRenderable The I_Renderable object
	  * 
	  */
	  
	public void backout(I_Renderable aRenderable);
	
	
	// ----------------------------------------------------------------------
	// METHOD: getDepthBasedAlignment
	// ----------------------------------------------------------------------
	
	/** When depth based rendering is being done, then the target component
	  * is allowed to specify the alignment for the page
	  *
	  * @return The alignment.  If this is null, then no alignment is assumed
	  * 
	  */
	  
	public String getDepthBasedAlignment();
	
	
	// ----------------------------------------------------------------------
	// METHOD: isDeepNavigating
	// ----------------------------------------------------------------------
	
	/** Test if the event indicates that deep navigation is occuring or not.
	  *
	  * @param aActionEvent The MActionEvent describing the component's event
	  * 
	  * @param aStack The renderables stack
	  *
	  * @return Boolean indicating if this is deep navigation or not
	  * 
	  */
	  
	public boolean isDeepNavigating(MActionEvent aActionEvent,
	                                Stack aStack);
	
	
	// ----------------------------------------------------------------------
	// METHOD: autoPop
	// ----------------------------------------------------------------------
	
	/** Test if an auto pop should be done or not.
	  *
	  * @param aActionEvent The MActionEvent describing the component's 
	  *                     event.
	  * 
	  * @param aStack       The renderables stack.
	  *
	  * @return             A boolean indicating if autopop should be done or 
	  *                     not.
	  * 
	  */
	
	public boolean autoPop(MActionEvent aEvent, 
	                       Stack aStack);
	
	
}


// ========================================================================