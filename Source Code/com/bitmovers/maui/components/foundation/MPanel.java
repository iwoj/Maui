// =============================================================================
// com.bitmovers.maui.components.foundation.MPanel
// =============================================================================

// =============================================================================
// CHANGELOG:
//++ 340  SL 2001.08.13
// MPanel construction with MLayout
// =============================================================================

package com.bitmovers.maui.components.foundation;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MPanel                                 (c) 2001 Bitmovers Systems
// ========================================================================

/** <code>MPanel</code> is the simplest of all containers. It has no visual 
  * representation, but is useful for grouping and aligning components. The
  * default layout used when none is specified is <code>MBoxLayout</code>.
  * 
  */
  
public class MPanel extends MContainer
{	
  private boolean firstTime = true;
  private boolean html = false;
  private MLayout.Alignment defaultAlignment = MLayout.CENTER;
  
	

	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Creates a new panel using the default layout 
	  * (<code>MBoxLayout</code>).
	  *
	  */
	  
	public MPanel()
	{
		this("");
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Creates a new panel using the specified name and the default layout 
	  * (<code>MBoxLayout</code>). The given name should be a human-readable
	  * string that indicates the title or purpose of this container.
	  *
	  * @param name A string representing the name of the panel.
	  */
	  
	public MPanel(String name)
	{
		this(name,null);
	}
	
	//++ SL 2001.08.14
	//
	// Add Layout info at construction time. This avoids calling setLayout  
	// for each MPanel at construction time.	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Creates a new panel using the specified name and <code>MBoxLayout</code> such as
	  * <code>new MMBoxLayout(MBoxLayout.X_AXIS)</code>. The given name should be a 
	  * human-readable string that indicates the title or purpose of this container.
	  *
	  * @param name A string representing the name of the panel.
	  *
	  * @param layout <code>MLayout</code> object to indicate the type of layout.
	  *
	  */
	  
	public MPanel(String name, MLayout layout)
	{
		if (name == null || name.equals(""));
		{
			name = generateUniqueName(this);
		}
		this.name = name;
		if (layout == null)
		{
			layout = new MBoxLayout(this);
		}
    setLayout(layout);		
	}
	//-- SL 2001.08.14	
	
	
	//++ SL 2001.08.14
	//
	// Add Layout info at construction time. This avoids calling setLayout  
	// for each MPanel at construction time.	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Creates a new panel using the specified layout. For Example:
		* <pre>
	  * <code>new MBoxLayout(MBoxLayout.X_AXIS)</code>
	  * </pre>
	  * only. 
	  *
	  * @param layout <code>MLayout</code> object to indicate the type of layout.	   
		*/
	  
	public MPanel(MLayout layout)
	{
		this(null,layout);
	}
	//-- SL 2001.08.14	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF