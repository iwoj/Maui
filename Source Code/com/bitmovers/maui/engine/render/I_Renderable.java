package com.bitmovers.maui.engine.render;

import com.bitmovers.utilities.StringParser;


// ========================================================================
// INTERFACE: I_Renderable                       (c) 2001 Bitmovers Systems
// ========================================================================

/** This interface is for objects which are renderable (e.g. MComponents 
  * and MLayouts).
  * 
  */
  
public interface I_Renderable
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** Fill in the parser with values based on the current state of the 
	  * object. Parser values should not contain any platform-specific 
	  * formatting, but rather more generic data such as numbers and plain 
	  * text strings.
	  * 
	  */
	  
	public void fillParserValues();
	
	
	// ----------------------------------------------------------------------
	// METHOD: getParser
	// ----------------------------------------------------------------------
	
	/** @return The StringParser from the renderable object
	  * 
	  */
	  
	public StringParser getParser();
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** The render method.  This is just a hand-off to the I_Renderer object.
	  *
	  */
	  
	public String render();
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRenderer
	// ----------------------------------------------------------------------
	
	/** Get the renderer for this I_Renderable.
	  *
	  * @return The I_Renderer
	  * 
	  */
	  
	public I_Renderer getRenderer();
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF