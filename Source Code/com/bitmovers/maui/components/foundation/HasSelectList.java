package com.bitmovers.maui.components.foundation;

import java.util.Vector;


// ========================================================================
// INTERFACE: HasSelectList                      (c) 2001 Bitmovers Systems
// ========================================================================

/** Interface for a component which can generate a select list (eg. 
  * MSelectList, MTabbedPane). This is used for WML rendering where 
  * several different components have identical appearances.
  *
  * @invisible
  * 
  */
  
public interface HasSelectList
{
	
	
	// ----------------------------------------------------------------------
	// METHOD: getSelectListOptions
	// ----------------------------------------------------------------------
	
	/** Returns a vector of strings containing the labels of each list 
	  * item.
	  *
	  * @return    A vector of strings containing the labels of each list 
	  *            item.
	  * @invisible
	  * 
	  */
	  
	public Vector getSelectListOptions();
	
	
}


// ========================================================================
//                                               (c) 2001 Bitmovers Systems