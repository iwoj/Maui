// ========================================================================
// com.bitmovers.maui.layouts.MGridLayout
// ========================================================================

package com.bitmovers.maui.layouts;

import java.awt.Color;

import com.bitmovers.maui.engine.ServerConfigurationManager;
import com.bitmovers.maui.components.MDesktop;
import com.bitmovers.maui.components.foundation.MFrame;
import com.bitmovers.maui.layouts.MLayout;


// ========================================================================
// CLASS: MGridLayout                            (c) 2001 Bitmovers Systems
// ========================================================================

/** This layout lays things out in a grid, with a set number of columns
  * and rows. Components are added left to right, top to bottom, much like
  * the java.awt.GridLayout layout manager.<p>
  *
  * You can pass an Alignment object when you add a component to control
  * how things are aligned. By default, components will be center-aligned.
  * <p>
  *
  * Example:<p>
  *
  * <pre>
  *  MGridLayout gridLayout = new MGridLayout(2, 2);
  *  MPanel panel = new MPanel(gridLayout);
  *
  *  panel.add(new MLabel("One"), MLayout.LEFT);
  *  panel.add(new MLabel("Two"), MLayout.RIGHT);
  *  panel.add(new MLabel("Three"));
  *  panel.add(new MLabel("Four"));
  * </pre>
  *
  * When rendered, this MPanel would look like:
  *
  * <pre>
  *  +-------------------+-------------------+
  *  | One               |               Two |
  *  +-------------------+-------------------+
  *  |      Three        |       Four        |
  *  +-------------------+-------------------+
  * </pre>
  * 
  * This layout manager will by default only be as wide as the sum of
  * the widest components contained within. If you would like it span the
  * maximum width it can, simply call setSpanMaximumWidth(true).<p>
  *
  * 
  */
  
public class MGridLayout extends MLayout
{
	// ----------------------------------------------------------------------
  public static final MLayout.Alignment DEFAULT_ALIGNMENT = MLayout.CENTER;

	private int columns;
	private int rows;
  private boolean gridDividerLines = false;
  private int gridDividerLineWidth = 1;
  private Color gridDividerLineColor = Color.black;
  private Color cellColor = MDesktop.getColorFromHexString(ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_WINDOW_COLOR));
  private int cellPadding = 2;
  private boolean spanMaximumWidth = false;


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** @param columns The number of columns to use.
	  * @param rows The number of rows to use.
	  *
	  */
	
	public MGridLayout(int aColumns, int aRows)
	{
		setColumns(aColumns);
		setRows(aRows);
		setDefaultAlignment(MGridLayout.DEFAULT_ALIGNMENT);
	}


	// ----------------------------------------------------------------------
	// METHOD: setColumns
	// ----------------------------------------------------------------------
	
	public void setColumns(int aColumns)
	{
		columns = Math.abs(aColumns);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getColumns
	// ----------------------------------------------------------------------
	
	public int getColumns()
	{
		return columns;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setRows
	// ----------------------------------------------------------------------
	
	public void setRows(int aRows)
	{
		rows = Math.abs(aRows);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRows
	// ----------------------------------------------------------------------
	
	public int getRows()
	{
		return rows;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setGridDividerLines
	// ----------------------------------------------------------------------
	
	/** Setting this method to true will cause this layout manager to put
	  * lines between each cell (on platforms which support it).
	  *
	  */
	
	public void setGridDividerLines(boolean aGridDividerLines)
	{
		gridDividerLines = aGridDividerLines;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: isGridDividerLines
	// ----------------------------------------------------------------------
	
	public boolean isGridDividerLines()
	{
		return gridDividerLines;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setGridDividerLineColor
	// ----------------------------------------------------------------------
	
	public void setGridDividerLineColor(Color aDividerLineColor)
	{
		gridDividerLineColor = aDividerLineColor;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getGridDividerLineColor
	// ----------------------------------------------------------------------
	
	public Color getGridDividerLineColor()
	{
		return gridDividerLineColor;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setGridDividerLineWidth
	// ----------------------------------------------------------------------
	
	public void setGridDividerLineWidth(int aWidth)
	{
		gridDividerLineWidth = Math.abs(aWidth);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getGridDividerLineWidth
	// ----------------------------------------------------------------------
	
	public int getGridDividerLineWidth()
	{
		return gridDividerLineWidth;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setCellColor
	// ----------------------------------------------------------------------
	
	public void setCellColor(Color aCellColor)
	{
		cellColor = aCellColor;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getCellColor
	// ----------------------------------------------------------------------
	
	public Color getCellColor()
	{
		return cellColor;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setCellPadding
	// ----------------------------------------------------------------------
	
	/** On platforms which support it, this layout manager will pad the cells
	  * of the grid with this value.
	  *
	  * @param aPadding The number of pixels with which to pad the cells.
	  *
	  */
	
	public void setCellPadding(int aPadding)
	{
		cellPadding = Math.abs(aPadding);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getCellPadding
	// ----------------------------------------------------------------------
	
	public int getCellPadding()
	{
		return cellPadding;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setSpanMaximumWidth
	// ----------------------------------------------------------------------
	
	/** On platforms which support it, this layout manager will span the
	  * maximum width possible.
	  *
	  */
	
	public void setSpanMaximumWidth(boolean aSpan)
	{
		spanMaximumWidth = aSpan;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: isSpanMaximumWidth
	// ----------------------------------------------------------------------
	
	public boolean isSpanMaximumWidth()
	{
		return spanMaximumWidth;
	}
	
	
	// ----------------------------------------------------------------------
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF