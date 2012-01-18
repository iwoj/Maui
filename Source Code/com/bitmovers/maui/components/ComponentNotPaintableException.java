package com.bitmovers.maui.components;

import com.bitmovers.maui.MauiException;


// ========================================================================
// CLASS: ComponentNotPaintableException         (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>ComponentNotPaintableException</code> is thrown when a component class
  * is requested to paint a graphical representation of itself, but does
  * not implement the Paintable interface.
  *
  */

public class ComponentNotPaintableException extends MauiException
{
	
	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR: ComponentNotPaintableException
  // ----------------------------------------------------------------------
  
	/** Constructs a new <code>ComponentNotPaintableException</code> with the default 
	  * error message.
	  *
	  */
	  
  public ComponentNotPaintableException()
  {
    super("Component does not implement the Paintable interface.");
  }


  // ----------------------------------------------------------------------
  // CONSTRUCTOR: ComponentNotPaintableException
  // ----------------------------------------------------------------------
  
	/** Constructs a new <code>ComponentNotPaintableException</code> with the given 
	  * error message.
	  *
	  */
	
  public ComponentNotPaintableException(String message)
  {
    super(message);
  }
  
  
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF