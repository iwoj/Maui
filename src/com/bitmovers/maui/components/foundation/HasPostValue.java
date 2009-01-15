package com.bitmovers.maui.components.foundation;


// ========================================================================
// INTERFACE: HasPostValue                       (c) 2001 Bitmovers Systems
// ========================================================================

/** This is an interface to identify which components require posts from 
  * the browser. This is currently specific to WML.
  * 
  * @invisible
  * 
  */
  
public interface HasPostValue
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
	/** Get the value to be posted along with the component id.
	  *
	  * @return The value to be posted.  If null, then the value will be 
	  *         treated as an empty string ("").
	  * @invisible
	  * 
	  */
	  
	public String getPostValue();
	
	
} 


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF