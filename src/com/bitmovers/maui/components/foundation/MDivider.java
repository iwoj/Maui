// =============================================================================
// com.bitmovers.maui.components.foundation.MDivider
// =============================================================================

package com.bitmovers.maui.components.foundation;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException;


// ========================================================================
// CLASS: MDivider                               (c) 2001 Bitmovers Systems
// ========================================================================

/** <code>MDivider</code> is a horizontal line component. The divider is useful for 
  * organizing the user interface into logically grouped areas.
  * 
  */

public class MDivider extends MComponent
{
	
	
	private int widthPercent = 100;
	
	
	//----------------------------------------------------------------------------
	// CONSTRUCTOR
	//----------------------------------------------------------------------------
	
	/** Constructs a new divider with the default width (100%).
    *
    */

	public MDivider()
	{
		generateUniqueName();
	}
	
	
	//----------------------------------------------------------------------------
	// CONSTRUCTOR
	//----------------------------------------------------------------------------
	
	/** Constructs a new divider with the given width percent.
    *
    * @param widthPercent	An integer value ranging from 0 to 100.
    * 
    */
    
	public MDivider (int widthPercent)
	{
		generateUniqueName();
		this.widthPercent = widthPercent;
	}
	
	
	//----------------------------------------------------------------------------
	// METHOD: fillParserValues
	//----------------------------------------------------------------------------
	
	/** Fills in parser values for the renderers to use. Parser values should not
	  * contain any device-specific formatting.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void fillParserValues()
	{
		super.fillParserValues();
		parser.setVariable("width", Integer.toString(widthPercent));
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF