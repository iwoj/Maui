// =============================================================================
// com.bitmovers.maui.components.NoSuchComponentException
// =============================================================================

package com.bitmovers.maui.components;

import com.bitmovers.maui.MauiException;


// =============================================================================
// CLASS: NoSuchComponentException
// =============================================================================

/** This exception is thrown when referring to a non-existant component.
  *
  * @author Ian Wojtowicz (ian@bitmovers.com)
  * @version 1999.11.01
  */
  
public class NoSuchComponentException extends MauiException
{
  // ---------------------------------------------------------------------------
	// CONSTRUCTOR
  // ---------------------------------------------------------------------------
	
  public NoSuchComponentException()
  {
  	super();
  }


  // ---------------------------------------------------------------------------
	// CONSTRUCTOR
  // ---------------------------------------------------------------------------
	
  public NoSuchComponentException(String message)
  {
  	super(message);
  }
	
	
  // ---------------------------------------------------------------------------
}
