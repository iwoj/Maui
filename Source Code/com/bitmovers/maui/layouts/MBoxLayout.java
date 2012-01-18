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
// CLASS: MBoxLayout                             (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>MBoxLayout</code> class is based on AWT's 
  * <code>BoxLayout</code> layout manager. It arranges components in an 
  * unbroken line, either vertically or horizontally, depending on the axis 
  * constant  used during construction 
  * (<code>X_AXIS</code> or <code>Y_AXIS</code>).<p>
  * 
  * As with all layout managers, this behaviour may not necessarily apply
  * to all client devices. Some devices may alter this layout's behaviour 
  * to suit the limitations of their particular environment (e.g. it may 
  * not make sense to arrange components horizontally on devices with very
  * narrow screens).
  * 
  */
  
public class MBoxLayout extends MLayout
{
	
	/* This constant may be used during construction to indicate the 
	   orientation of the layout (horizontal). */
	public static final byte X_AXIS = 0;
	
	/* This constant may be used during construction to indicate the 
	   orientation of the layout (vertical). */
	public static final byte Y_AXIS = 1;
	
	String HTMLTemplate;
	String HTMLParsed;
	
	String WMLTemplate;
	String WMLParsed;
	    
	MComponent[] components;
	byte axis;
    
    
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs new <code>MBoxLayout</code>.
	  * 
	  */
	  
	public MBoxLayout()
	{
		this(Y_AXIS);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs new <code>MBoxLayout</code>.
	  * 
	  * @param  axis    the axis upon which this layout should be based 
	  *                 (either <code>MBoxLayout.X_AXIS</code> or 
	  *                 <code>MBoxLayout.Y_AXIS</code>).
	  * 
	  */
	  
	public MBoxLayout(byte axis)
	{
		this(axis, MBoxLayout.LEFT);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs new <code>MBoxLayout</code>.
	  * 
	  * @param  axis    the axis upon which this layout should be based 
	  *                 (either <code>MBoxLayout.X_AXIS</code> or 
	  *                 <code>MBoxLayout.Y_AXIS</code>).
	  * 
	  * @param  align   as <code>Alignment</code> object to indicate the 
	  *                 layout's default alignment.
	  * 
	  */
	  
	public MBoxLayout(byte axis, Alignment align)
	{
		super.align = align;
		
		if (axis == Y_AXIS || axis == X_AXIS)
		{
			this.axis = axis;
		}
		else
		{
			throw new IllegalArgumentException("Cannot instantiate layout manager: unknown axis: " + axis);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs new <code>MBoxLayout</code>.
	  * 
	  * @param  parent  the container to which this layout will be applied.
	  * 
	  * @deprecated     No longer necessary to specify parent at 
	  *                 construction-time.
	  * 
	  */
	  
	public MBoxLayout(MContainer parent)
	{
		this(parent, Y_AXIS);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs new <code>MBoxLayout</code>.
	  * 
	  * @param  parent  the container to which this layout will be applied.
	  * 
	  * @param  axis    the axis upon which this layout should be based 
	  *                 (either <code>MBoxLayout.X_AXIS</code> or 
	  *                 <code>MBoxLayout.Y_AXIS</code>).
	  * 
	  * @deprecated     No longer necessary to specify parent at 
	  *                 construction-time.
	  * 
	  */
	  
	public MBoxLayout(MContainer parent, byte axis)
	{
		this(parent, axis, MBoxLayout.LEFT);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs new <code>MBoxLayout</code>.
	  * 
	  * @param  parent  the container to which this layout will be applied.
	  * 
	  * @param  axis    the axis upon which this layout should be based 
	  *                 (either <code>MBoxLayout.X_AXIS</code> or 
	  *                 <code>MBoxLayout.Y_AXIS</code>).
	  * 
	  * @deprecated     No longer necessary to specify parent at 
	  *                 construction-time.
	  * 
	  */
	  
	public MBoxLayout(MContainer parent, byte axis, Alignment align)
	{
		super.align = align;
		super.parent = parent;
		
		if (axis == Y_AXIS || axis == X_AXIS)
		{
			this.axis = axis;
		}
		else
		{
			throw new IllegalArgumentException("Cannot instantiate layout manager: unknown axis: " + axis);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getAxis
	// ----------------------------------------------------------------------
	
  /** Gets the axis.
    * 
    * @return the axis
    *
    */

	public byte getAxis()
	{
		return axis;
	}
    
    
	// ----------------------------------------------------------------------
	// METHOD: renderWML
	// ----------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	  
	// This method was required by the now deprecated WMLRenderable interface. The
	// following code should be removed once this component's WML rendering is 
	// fully tested. - ian (2001.05.14)
	
	/*
	public String renderWML()
	{
		if (this instanceof I_Renderable)
		{
			return render ();
		}
		else
		{
			String seperator = " ";
			this.WMLParsed = "";
			
			switch(axis)
			{
				case X_AXIS:
					seperator = " ";
					break;
				case Y_AXIS:
					seperator = "<br/>\n";
					break;
			}
			
			if (parent.getComponents().length > 0) 
			{
				for (int i = 0; i < this.parent.getComponents().length - 1; i++)
				{
					this.WMLParsed += super.doRenderComponent(parent.getComponent (i), false) + seperator;
				}
				this.WMLParsed += super.doRenderComponent(parent.getComponent (parent.getComponents().length - 1), false);
			}
			
			return this.WMLParsed;
		}
	}
	*/
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF