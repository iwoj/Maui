package com.bitmovers.maui.components.foundation;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MauiEvent;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.engine.ServerConfigurationManager;


// ========================================================================
// CLASS: MTextComponent                         (c) 2001 Bitmovers Systems
// ========================================================================

/** This abstract class handles common activities for text based 
  * components.
  * 
  */

public abstract class MTextComponent extends MSettable
{
	public static final int DEFAULT_SIZE = 20;
	public static final int IGNORE_SIZE = -1;
	
	private int size = -1;  					// The size of the display field
	private int maxlength = -1;					// The maximum input length
	private boolean masked = false;				// Using password or text type
	private boolean readonly = false;			// Indicates if field is readonly or not


	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------

	protected MTextComponent ()
	{
		generateUniqueName ();
	}
	

	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------

	protected MTextComponent (String aText)
	{
		this ();
		setValue (aText);
	}
	

	// --------------------------------------------------------------------
	// METHOD: setText
	// --------------------------------------------------------------------

	/** Set a text value.
	  *
	  * @param aText The text value
	  * 
	  */

	public void setText (String aText)
	{
		setValue (aText);
	}
	

	// --------------------------------------------------------------------
	// METHOD: getText
	// --------------------------------------------------------------------

	/** Get the text value.
	  *
	  * @return The text value
	  * 
	  */

	public String getText ()
	{
		return (String) getValue ();
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: append
	// --------------------------------------------------------------------
	
	public void append (String aText)
	{
		setValue((String)getValue() + aText);
		invalidate ();
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: setFieldSize
	// --------------------------------------------------------------------

	/** Set the size of the display area of the field.
	  *
	  * @param aSize The size
	  * 
	  */

	public void setFieldSize (int aSize)
	{
		size = aSize;
	}
	

	// --------------------------------------------------------------------
	// METHOD: getFieldSize
	// --------------------------------------------------------------------

	/** Get the size of the display area of the field.
	  *
	  * @return The size of the display area
	  * 
	  */

	public int getFieldSize ()
	{
		return size;
	}
	

	// --------------------------------------------------------------------
	// METHOD: getFieldSize
	// --------------------------------------------------------------------

	/** Set the maximum input length for the field.
	  *
	  * @param aSize The size
	  * 
	  */
	  
	public void setMaximumLength (int aMaxlength)
	{
		maxlength = aMaxlength;
	}
	

	// --------------------------------------------------------------------
	// METHOD: getMaximumLength
	// --------------------------------------------------------------------

	/** Get the size of the display area of the field.
	  *
	  * @return The size of the display area
	  * 
	  */

	public int getMaximumLength ()
	{
		return maxlength;
	}
	

	// --------------------------------------------------------------------
	// METHOD: setReadonly
	// --------------------------------------------------------------------

	/** Set the readonly boolean.
	  *
	  * @param aReadonly The readonly boolean value
	  * 
	  */
	  
	public void setReadonly (boolean aReadonly)
	{
		readonly = aReadonly;
	}
	

	// --------------------------------------------------------------------
	// METHOD: isReadonly
	// --------------------------------------------------------------------

	/** Get the readonly boolean.
	  *
	  * @return The readonly boolean
	  * 
	  */
	
	public boolean isReadonly ()
	{
		return readonly;
	}
	

	// --------------------------------------------------------------------
	// METHOD: setMasked
	// --------------------------------------------------------------------

	/** Set the masked boolean.
	  *
	  * @param aMasked The masked boolean
	  * 
	  */

	public void setMasked (boolean aMasked)
	{
	  masked = aMasked;
	}
	

	// --------------------------------------------------------------------
	// METHOD: isMasked
	// --------------------------------------------------------------------

	/** Get the masked boolean.
	  *
	  * @return The masked boolean
	  * 
	  */
	  
	public boolean isMasked ()
	{
		return masked;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** Fills-in parser values for the renderers to use. Parser values should not
	  * contain any device-specific formatting.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void fillParserValues()
	{
		super.fillParserValues();
		
		// This is HTML-specific! (Ian - 2001.06.11)
		if (maxlength != IGNORE_SIZE)
		{
			parser.setVariable ("maximumLength", "maxlength=\"" + maxlength + "\"");
		}
		
		// This is HTML-specific! (Ian - 2001.06.11)
		parser.setVariable ("type", (isMasked () ? "password" : "text"));
		
		// This is HTML-specific! (Ian - 2001.06.11)
		if (size != IGNORE_SIZE)
		{
			parser.setVariable ("size", "size=\"" + size + "\"");
		}
	}


	// --------------------------------------------------------------------
	// METHOD: createEvent
	// --------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	
	public MauiEvent createEvent (String aStateData)
	{
		return (aStateData != null && aStateData.equals (MActionEvent.ACTION_PUSH) ?
					new MActionEvent (this, aStateData) :
					super.createEvent (aStateData));
	}


	// --------------------------------------------------------------------
	// METHOD: doSetValue
	// --------------------------------------------------------------------
	
	/** @invisible
	  *
	  */
	
	public void doSetValue (Object aValue)
	{
		super.doSetValue (aValue == null ? "" : aValue);
	}
	
	
}


// ========================================================================
//                                                                      EOF