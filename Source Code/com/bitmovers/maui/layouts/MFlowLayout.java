package com.bitmovers.maui.layouts;

import java.awt.Dimension;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.htmlcompositor.*;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MFlowLayout                            (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>MFlowLayout</code> class is based on AWT's 
  * <code>FlowLayout</code> layout manager. It arranges components like 
  * text in a paragraph (i.e. sequentially, left to right, top to bottom).<p>
  * 
  * As with all layout managers, this behaviour may not necessarily apply
  * to all client devices. Some devices may alter this layout's behaviour 
  * to suit the limitations of their particular environment (e.g. it may 
  * not make sense to arrange components horizontally on devices with very
  * narrow screens).
  * 
  */
  
public class MFlowLayout extends MLayout
{
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MFlowLayout</code> using the default alignment 
	  * for all cells (i.e. <code>MLayout.CENTER</code>).
	  * 
	  * @param  parent  the container to which this layout will be applied.
	  *	
	  */
	
	public MFlowLayout()
	{
		this(CENTER);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MFlowLayout</code> using the alignment specified 
	  * as the default for cells which do not define their own alignment.
	  *
	  * @param  parent  the container to which this layout will be applied.
	  * 
	  * @param  align   the new default alignment.
	  * 
	  */
	
	public MFlowLayout(Alignment align)
	{
		this.align = align;
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MFlowLayout</code> using the default alignment 
	  * for all cells (i.e. <code>MLayout.CENTER</code>).
	  * 
	  * @param  parent  the container to which this layout will be applied.
	  *	
	  * @deprecated     No longer necessary to specify parent at 
	  *                 construction-time.
	  * 
	  */
	
	public MFlowLayout(MContainer parent)
	{
		this(parent, CENTER);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MFlowLayout</code> using the alignment specified 
	  * as the default for cells which do not define their own alignment.
	  *
	  * @param  parent  the container to which this layout will be applied.
	  * 
	  * @param  align   the new default alignment.
	  * 
	  * @deprecated     No longer necessary to specify parent at 
	  *                 construction-time.
	  * 
	  */
	
	public MFlowLayout(MContainer parent, Alignment align)
	{
		this.parent = parent;
		this.align = align;
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF