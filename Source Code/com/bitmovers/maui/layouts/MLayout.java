package com.bitmovers.maui.layouts;

import java.io.*;
import java.awt.Dimension;
import com.bitmovers.utilities.*;

import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderer;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.htmlcompositor.*;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.*;


// ========================================================================
// CLASS: MLayout                                (c) 2001 Bitmovers Systems
// ========================================================================

/** This is the superclass for all Maui layout managers. Layout managers
  * work in conjunction with containers to arrange contained components
  * according to a specific strategy.<p>
  *
  * It is important to note that these strategies may not necessarily apply
  * to all client devices. Some devices may alter a layout's behaviour 
  * to suit the limitations of their particular environment (e.g. it may 
  * not make sense to arrange components horizontally on devices with very
  * narrow screens).<p>
  *
  * <code>MLayout</code> is conceptually similar to the 
  * <code>LayoutManager</code> and <code>LayoutManager2</code> classes in 
  * AWT.
  * 
  */
  
public abstract class MLayout
           implements I_Renderable, 
                      Serializable
{
	
	/* This value indicates that each row of components
	   should be left-justified. */
	public static final AlignmentLeft LEFT = new MLayout.AlignmentLeft();
	
	/* This value indicates that each row of components
	   should be centered. */
	public static final AlignmentCenter CENTER = new MLayout.AlignmentCenter();
	
	/* This value indicates that each row of components
	   should be right-justified. */
	public static final AlignmentRight RIGHT = new MLayout.AlignmentRight();
	
	static ResourceManager jarResources = ResourceManager.getInstance();
	protected I_Renderer renderer = null;
	protected Alignment align = this.CENTER;
	
	/** This field should be protected, but Maui's current packaging scheme
	  * interferes with MContainer accessing this field unless it is public.
	  * 
	  * @invisible
	  * 
	  */
	
	public MContainer parent;
	
	private boolean isValid = false;
	private StringParser parser = new StringParser();
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** For now, this will only be called if a subclass implements the 
	  * I_Renderable interface. Otherwise, it is ignored.
	  * 
	  * @invisible
	  * 
	  */
	
	public String render()
	{
		if (renderer == null)
		{
			//
			//	The renderer hasn't been initialized yet.  So initialize it, and then
			//	start rendering
			//
			CompositionManager theCompositionManager = CompositionManager.getInstance();
			renderer = theCompositionManager.getRenderer((I_Renderable)this, getParentContainer());
		}
		return renderer.render((I_Renderable)this);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getParentContainer
	// ----------------------------------------------------------------------
	
	/** @return  this layout's parent container.
	  * 
	  */
	  
	public MContainer getParentContainer()
	{
		return parent;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setParentContainer
	// ----------------------------------------------------------------------
	
	/** Sets this layout's parent. For use by <code>MContainer</code>.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void setParentContainer(MContainer newParent)
	{
		parent = newParent;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setDefaultAlignment
	// ----------------------------------------------------------------------
	
	/** Sets the default alignment to use during component addition if no
	  * constraints are specified.
	  * 
	  * @param  alignment  the new default alignment.
	  *
	  */
	  
	public void setDefaultAlignment(Alignment alignment)
	{
		align = alignment;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getDefaultAlignment
	// ----------------------------------------------------------------------
	
	/** Gets the default alignment to use during component addition if no
	  * constraints are specified.
	  * 
	  * @return  the default alignment.
	  *
	  */
	  
	public Alignment getDefaultAlignment()
	{
		return align;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: validate
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void validate()
	{
		if (!isValid)
		{
			isValid = true;
		}
	}
	
		
	// ----------------------------------------------------------------------
	// METHOD: invalidate
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void invalidate()
	{
		if (parent != null && parent.isValid())
		{
			parent.invalidate();
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void fillParserValues()
	{
		//parser.setVariable("servletURL", HTMLCompositor.servletURL);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getParser
	// ----------------------------------------------------------------------
	
	/** Returns the string parser for this object.
	  * 
	  * @invisible
    * 
	  */
	  
	public StringParser getParser()
	{
		return parser;
	}


	// ----------------------------------------------------------------------
	// METHOD: doRenderComponent
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	protected String doRenderComponent(MComponent component, boolean html)
	{
		/* This code implemented backwards compatibility with the first
		   version of the rendering system. Legacy rendering as been 
		   removed from the components, so this code is no longer necessary. 
		   -- ian (2001.05.14) */
		   
/*
		if (component instanceof I_Renderable)
		{
			return component.render();
		}
		else
		{
			if (html)
			{
				return component.renderHTML();
			}
			else
			{
				return component.renderWML();
			}
		}
*/

		return component.render();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRenderer
	// ----------------------------------------------------------------------
	
	/** Get the renderer for this I_Renderable.
	  * 
	  * @return The I_Renderer
	  * 
	  * @invisible
	  * 
	  */
	  
	public I_Renderer getRenderer()
	{
		return renderer;
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: Alignment
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** <code>Alignment</code> is the superclass of the type-safe alignment 
	  * constants used by layout managers.
	  *
	  */
	
	public static class Alignment
	{
		
		protected static final String LEFT = "left";
		protected static final String CENTER = "center";
		protected static final String RIGHT = "right";
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------
		
		/** @return  a text representation of this object's value, which equals 
		  *          "center".
		  * 
		  */
		  
		public String toString()
		{
			// Default to center-alignment
			return Alignment.CENTER;
		}
		
	}


	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: AlignmentLeft
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This class represents left alignment. It is used by layout managers
	  * to help position components.
	  *
	  */
	
	public static class AlignmentLeft extends Alignment
	{
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------
		
		/** @return  a text representation of this object's value, which equals 
		  *          "left".
		  * 
		  */
		  
		public String toString()
		{
			return super.LEFT;
		}
		
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: AlignmentCenter
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This class represents center alignment. It is used by layout managers
	  * to help position components.
	  *
	  */
	
	public static class AlignmentCenter extends Alignment
	{
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------
		
		/** @return  a text representation of this object's value, which equals 
		  *          "center".
		  * 
		  */
		  
		public String toString()
		{
			return super.CENTER;
		}
		
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: AlignmentRight
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This class represents right alignment. It is used by layout managers
	  * to help position components.
	  *
	  */
	
	public static class AlignmentRight extends Alignment
	{
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------
		
		/** @return  a text representation of this object's value, which equals 
		  *          "right".
		  * 
		  */
		  
		public String toString()
		{
			return super.RIGHT;
		}
		
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setAlignment
	// ----------------------------------------------------------------------
	
	/** @deprecated  Use <code>setDefaultAlignment()</code> instead.
	  *
	  */
	  
	public void setAlignment(Alignment alignment)
	{
		setDefaultAlignment(alignment);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getAlignment
	// ----------------------------------------------------------------------
	
	/** @deprecated  Use <code>getDefaultAlignment()</code> instead.
	  *
	  */
	  
	public Alignment getAlignment()
	{
		return getDefaultAlignment();
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF