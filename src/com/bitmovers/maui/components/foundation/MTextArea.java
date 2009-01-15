package com.bitmovers.maui.components.foundation;

import java.awt.Dimension;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.render.I_Renderer;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException;


// ========================================================================
// CLASS: TextArea                               (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is a multiple line text field component.
  * 
  */

public class MTextArea extends MTextComponent implements HasPostValue
{
	
	
	int rows;
	int cols;
	
	/** Constructs a new TextField object of the default size.
    */
	public MTextArea ()
	{
		this( "", new Dimension (30, 5));
	}
	
	/** Constructs a new TextField object of the specified size.
    */
    
	public MTextArea( String value )
	{
		this( value, new Dimension( 30, 5 ) );
	}
	
	/** Constructs a new TextField object of the specified size.
    */
    
	public MTextArea( Dimension dimension )
	{
		this( "", dimension );
	}
	
	/** Constructs a new TextField object of the specified size.
    */
    
	public MTextArea( String value, Dimension dimension )
	{
		super ();
		setValue (value);
		setDimension (dimension);
		enabled = true;
	}
  
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** Fills-in parser values for the renderers to use. Parser values should 
	  * not contain any device-specific formatting.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void fillParserValues()
	{
		super.fillParserValues();
		
		// This should be moved into the HTML renderer - ian (2001.05.14)
  	if( !enabled )
  	{
  		parser.setVariable( "disabledCode", "onFocus=\"mauiForm.testtext.blur()\"" );
  	}
  	
  	parser.setVariable( "rows", Integer.toString( rows ) );
  	parser.setVariable( "cols", Integer.toString( cols ) );
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public String getPostValue ()
	{
		return "$(" +  getWMLSafeComponentID() + ")";//":e)";
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: setDimension
	// ----------------------------------------------------------------------
	
	public void setDimension (Dimension aDimension)
	{
		cols = aDimension.width;
		rows = aDimension.height;
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: getDimension
	// ----------------------------------------------------------------------
	
	public Dimension getDimension ()
	{
		return new Dimension (rows, cols);
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public String render ()
	{
		return super.render ();
	}
  
  	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF