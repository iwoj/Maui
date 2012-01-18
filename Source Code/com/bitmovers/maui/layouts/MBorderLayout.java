package com.bitmovers.maui.layouts;

import java.awt.Dimension;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.htmlcompositor.*;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MBorderLayout                          (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>MBorderLayout</code> class is similar to AWT's 
  * <code>BorderLayout</code> layout manager. It arranges components in the 
  * following manner:
  *
  * <pre>
  *  +--------------------------------------------------------------------+
  *  |                                                                    |
  *  |                               NORTH                                |
  *  |                                                                    |
  *  +---------------------+-------------------------+--------------------+
  *  |                     |                         |                    |
  *  |        WEST         |         CENTER          |        EAST        |
  *  |                     |                         |                    |
  *  +---------------------+-------------------------+--------------------+
  *  |                                                                    |
  *  |                               SOUTH                                |
  *  |                                                                    |
  *  +--------------------------------------------------------------------+
  * </pre>
  *
  * When <code>MBorderLayout</code> is used as your layout manager for your 
  * container, items are added in the following manner:
  * 
  * <pre>
  *  MLabel north = new MLabel("North");
  *  MLabel south = new MLabel("South");
  *  MLabel center = new MLabel("Center");
  *  MLabel west = new MLabel("West");
  *  MLabel east = new MLabel("East");
  *
  *  container.add(north, MBorderLayout.NORTH);
  *  container.add(south, MBorderLayout.SOUTH);
  *  container.add(center, MBorderLayout.CENTER);
  *  container.add(east, MBorderLayout.WEST);
  *  container.add(west, MBorderLayout.EAST);
  * </pre>
  * 
  * You can set the default alignment within the cells for all components by
  * using the <code>setDefaultAlignment()</code> method (defaults to 
  * <code>MLayout.CENTER</code>). (See the alignment finals in 
  * <code>MLayout</code>.)
  * 
  * <pre>
  *  ((MBorderLayout)container.getLayout()).setDefaultAlignment(MLayout.LEFT);
  * </pre>
  * 
  * You can set the alignment on a cell-by-cell basis as well, which will
  * override the default alignment:
  * 
  * <pre>
  *  MLabel north = new MLabel("North");
  *  MLabel south = new MLabel("South");
  *  MLabel center = new MLabel("Center");
  *  MLabel west = new MLabel("West");
  *  MLabel east = new MLabel("East");
  *
  *  container.add(north, new Object[] { MBorderLayout.NORTH, MLayout.LEFT });
  *  container.add(south, new Object[] { MBorderLayout.SOUTH, MLayout.RIGHT });
  *  container.add(center, new Object[] { MBorderLayout.CENTER, MLayout.CENTER });
  *  container.add(east, new Object[] { MBorderLayout.WEST, MLayout.LEFT });
  *  container.add(west, new Object[] { MBorderLayout.EAST, MLayout.RIGHT });
  * </pre>
  * 
  * As with all layout managers, this behaviour may not necessarily apply
  * to all client devices. Some devices may alter this layout's behaviour 
  * to suit the limitations of their particular environment (e.g. it may 
  * not make sense to arrange components horizontally on devices with very
  * narrow screens).
  *
  */

public class MBorderLayout extends MLayout
{
	
	
	/* This constraint object represents the top-most compartment in a border layout. */
	public static final CompassNorth NORTH = new MBorderLayout.CompassNorth();
	
	/* This constraint object represents the bottom-most compartment in a border layout. */
	public static final CompassSouth SOUTH = new MBorderLayout.CompassSouth();
	
	/* This constraint object represents the center compartment in a border layout. */
	public static final CompassCenter CENTER = new MBorderLayout.CompassCenter();
	
	/* This constraint object represents the right-most compartment in a border layout. */
	public static final CompassEast EAST = new MBorderLayout.CompassEast();
	
	/* This constraint object represents the left-most compartment in a border layout. */
	public static final CompassWest WEST = new MBorderLayout.CompassWest();
	
	private Alignment defaultAlignment = null;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MBorderLayout</code> using the default alignment 
	  * for all cells (i.e. <code>MLayout.CENTER</code>).
	  * 
	  * @invisible
	  * 
	  */

	public MBorderLayout()
	{
		this(MLayout.CENTER);
	}
	

	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MBorderLayout</code> using the alignment specified 
	  * as the default for cells which do not define their own alignment.
	  *
	  * @param  defaultAlignment  the new default alignment.
	  * 
	  * @invisible
	  * 
	  */

	public MBorderLayout(MLayout.Alignment defaultAlignment)
	{
		this.defaultAlignment = defaultAlignment;
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MBorderLayout</code> using the default alignment 
	  * for all cells (i.e. <code>MLayout.CENTER</code>).
	  * 
	  * @param  parent  the container to which this layout will be applied.
	  *
	  * @deprecated     No longer necessary to specify parent at 
	  *                 construction-time.
	  * 
	  */

	public MBorderLayout(MContainer parent)
	{
		this(parent, MLayout.CENTER);
	}
	

	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a <code>MBorderLayout</code> using the alignment specified 
	  * as the default for cells which do not define their own alignment.
	  *
	  * @param  parent            the container to which this layout will be 
	  *                           applied.
	  * 
	  * @param  defaultAlignment  the new default alignment.
	  * 
	  * @deprecated     No longer necessary to specify parent at 
	  *                 construction-time.
	  * 
	  */

	public MBorderLayout(MContainer parent, MLayout.Alignment defaultAlignment)
	{
		super.parent = parent;
		this.defaultAlignment = defaultAlignment;
	}
	

	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: CompassDirection
	//              Should this class be moved into MLayout?
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** <code>CompassDirection</code> is the superclass of the type-safe
	  * constraint constants used the <code>MBorderLayout</code> layout 
	  * manager.
	  *
	  */
	
	public static class CompassDirection
	{
		
		
		protected static final String NORTH = "north";
		protected static final String SOUTH = "south";
		protected static final String CENTER = "center";
		protected static final String EAST = "east";
		protected static final String WEST = "west";

		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------
		
		/** @return  a text representation of this object's value, which equals 
		  *          "center".
		  * 
		  */
		  
		public String toString()
		{
			// Default to center-placement
			return CompassDirection.CENTER;
		}
		
		
	}


	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: CompassNorth
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This is the constraint class which represents 
	  * the top-most compartment in a border layout.
	  *
	  */
	
	public static class CompassNorth extends CompassDirection
	{
		
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------
		
		/** @return  a text representation of this object's value, which equals 
		  *          "north".
		  * 
		  */
		  
		public String toString()
		{
			return CompassDirection.NORTH;
		}
		
		
	}

	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: CompassSouth
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This is the constraint class which represents 
	  * the bottom-most compartment in a border layout.
	  *
	  */
	
	public static class CompassSouth extends CompassDirection
	{
		
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------

		/** @return  a text representation of this object's value, which equals 
		  *          "south".
		  * 
		  */
		  
		public String toString()
		{
			return CompassDirection.SOUTH;
		}
		
		
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: CompassWest
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This is the constraint class which represents 
	  * the left-most compartment in a border layout.
	  *
	  */
	
	public static class CompassWest extends CompassDirection
	{
		
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------

		/** @return  a text representation of this object's value, which equals 
		  *          "west".
		  * 
		  */
		  
		public String toString()
		{
			return CompassDirection.WEST;
		}
		
		
	}


	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: CompassEast
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This is the constraint class which represents 
	  * the right-most compartment in a border layout.
	  *
	  */
	
	public static class CompassEast extends CompassDirection
	{
		
		
		// --------------------------------------------------------------------
		// METHOD: toString
		// --------------------------------------------------------------------

		/** @return  a text representation of this object's value, which equals 
		  *          "east".
		  * 
		  */
		  
		public String toString()
		{
			return CompassDirection.EAST;
		}
		
		
	}


	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	// INNER CLASS: CompassCenter
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	
	/** This is the constraint class which represents 
	  * the center compartment in a border layout.
	  *
	  */
	
	public static class CompassCenter extends CompassDirection
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
			return CompassDirection.CENTER;
		}
		
		
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF