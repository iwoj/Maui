// =============================================================================
// com.bitmovers.maui.components.foundation.MMenuItemContainer
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.Vector;


// ========================================================================
// INTERFACE: MMenuItemContainer                 (c) 2001 Bitmovers Systems
// ========================================================================

/** Interface for components which can contain <code>MMenuItem</code> 
	* objects such as <code>MMenuBar</code>, <code>MMenu</code>.
  *
  */
  
public interface MMenuItemContainer
{
	
	
	public int getMenuItemCount();
	public void closeChildren();
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF