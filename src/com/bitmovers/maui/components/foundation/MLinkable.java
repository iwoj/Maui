package com.bitmovers.maui.components.foundation;

import java.net.*;


// ========================================================================
// INTERFACE: MLinkable                          (c) 2001 Bitmovers Systems
// ========================================================================

/** This interface defines methods that all classes which link to external
  * network resources must implement.
  * 
  */
  
public interface MLinkable
{
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setLink
	// ---------------------------------------------------------------------------
	
	/** Set a link object which component may use to link to other content. To 
	  * remove an existing link, pass <code>null</code> here.
	  * 
	  */
	  
	public void setLink (String aLink);
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getLink
	// ---------------------------------------------------------------------------
	
	/** Returns a link object which components may use to direct the user to 
	  * external content.
	  *
	  */
	  
	public String getLink ();
		
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF