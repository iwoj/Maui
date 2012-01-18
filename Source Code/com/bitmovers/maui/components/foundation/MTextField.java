package com.bitmovers.maui.components.foundation;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.resourcemanager.*;

// ========================================================================
// CLASS: MTextField                             (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is a basic, one line editable text field.
  * 
  */

public class MTextField extends MTextComponent implements HasPostValue
{
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new TextField object of the default size.
    *
    */
  
	public MTextField ()
	{
		this (DEFAULT_SIZE, "", false);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new TextField object of the specified size.
    *
    */
    
	public MTextField (int size)
	{
		this (size, "", false);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new TextField object of the specified size with
    * the given value as the default text to appear within the field.
    *
    */
    
	public MTextField (int size, String value)
	{
		this (size, value, false);
	}
	
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new TextField object of the specified size with
    * the given mask value (true means the field is read-only).
    *
    */
    
	public MTextField (int size, boolean masked)
	{
		this (size, "", masked);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new TextField object of the specified size with
    * the given value as the default text appearing within the field.
    * The isMasked parameter allows you to mask the characters in the 
    * field -- often useful for password fields.
    *
    */
    
	public MTextField(int size, String value, boolean masked)
	{
		this (size, value, masked, -1);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new TextField object of the specified size with
    * the given value as the default text appearing within the field.
    * The isMasked parameter allows you to mask the characters in the 
    * field -- often useful for password fields, and the maximumLength
    * parameter limits the contents of the field to this many characters 
    * (a negative value signifies no maximum limit).
    *
    */
    
	public MTextField (int size, String value, boolean masked, int maximumLength)
	{
		super ();
		setFieldSize (size);
		setMaximumLength (maximumLength);
		setValue (value);
		setMasked (masked);
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
		
		parser.setVariable ("size", Integer.toString (getFieldSize ()));
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
	
  	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF