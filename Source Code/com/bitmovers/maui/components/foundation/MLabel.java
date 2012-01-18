// ========================================================================
// CHANGELOG:
//
//++ 377 MW 2001.08.16
// Consumed Push events for WML so that MLabels with action events attached
// wouldn't be executed until they were cliked in the deep navigation screen.
// ========================================================================

// =============================================================================
// com.bitmovers.maui.components.foundation.MLabel
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.awt.Color;
import java.net.URL;
import java.net.MalformedURLException;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.htmlcompositor.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.events.MauiEvent;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MLabel                                 (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is a basic text component, useful for creating user interface
  * labels or general text messages. This component can optionally be given a 
  * URL to turn it into a test link.
  *
  */

public class MLabel extends MComponent implements MLinkable
{
	
	
	private String text;
	private Color colour;
	private String link;
	private boolean strikethrough = false;
	private boolean underline = false;
	private boolean bold = false;
	private boolean italic = false;
	private String subText = null;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new, empty <code>MLabel</code> object with the default color (black).
	  *
	  */

	public MLabel()
	{
		this("");
	}


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MLabel</code> object with the given string and the default 
	  * color (black).
	  *
	  * @param text A string that will become the text of the label.
	  */
    
	public MLabel(String text)
	{
		this(text, Color.black);
	}


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MLabel</code> object with the given string and the given 
	  * color.
	  * 
	  * @param text The text of the label.
	  *
	  * @param colour The <code>Color</code> object to set the desired color of the text.
	  */
    
	public MLabel(String text, Color colour)
	{
		setText(text);
		
		//++ IW 2001.08.10
		//
		// MLabels now use their text value as their initial name. So 
		// (new MLabel("Username")).getName() returns "Username".
		// 
		// This means no more annoying appearances of "MLabel32" in WAP interfaces.
		//
		setName(text);
		//-- IW 2001.08.10
		
		setColor(colour);
	}


	// ----------------------------------------------------------------------
	// METHOD: setText
	// ----------------------------------------------------------------------
	
	/** Sets the text of this label to the given string.
	  *
	  * @param aText The string to set as the text label.
	  */
    
	public void setText (String aText)
	{
		text = aText;
		invalidate ();
	}


	// ----------------------------------------------------------------------
	// METHOD: append
	// ----------------------------------------------------------------------
	
	/** Appends the given string to the text of this label.
	  *
	  * @param aText A string to append to the label text.
	  */
    
	public void append(String aText)
	{
		text += aText;
		invalidate ();
	}


	// ---------------------------------------------------------------------------
	// METHOD: appendLine
	// ---------------------------------------------------------------------------
	
	/** Appends the given string to the next line in the text of this label. 
	  * Equivalent to <code>append("\n" + aText)</code>.
	  *
	  * @param aText A string that adds text to the label at a new line.
	  */
    
	public void appendLine(String aText)
	{
		this.append("\n" + aText);
		this.invalidate();
	}


	// ---------------------------------------------------------------------------
	// METHOD: getText
	// ---------------------------------------------------------------------------
	
	/** Returns the text of this label. 
	  *
	  * @return The string text of the label.
	  */
    
	public String getText()
	{
		return this.text;
	}


	// ---------------------------------------------------------------------------
	// METHOD: setColor
	// ---------------------------------------------------------------------------
	
	/** Sets the color of this label to the colour represented by the given 
	  * <code>java.awt.Color</code> object. 
	  *
	  * @param colour <code>Color</code> object representing the color of the text.
	  */
    
	public void setColor(Color colour)
	{
		this.colour = colour;
	}


	// ---------------------------------------------------------------------------
	// METHOD: getColor
	// ---------------------------------------------------------------------------
	
	/** Returns the <code>java.awt.Color</code> object representing the color of 
	  * this label. 
	  *
	  * @return <code>Color</code> object representing the color of the text.
	  */
    
	public Color getColor()
	{
		return colour;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setBold
	// ---------------------------------------------------------------------------
	
	/** Allows bolding for clients which support it.
	  *
	  * @param aBold <code>true</code> to set bold, <code>false</code> otherwise.
	  */
    
	public void setBold (boolean aBold)
	{
		bold = aBold;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: isBold
	// ---------------------------------------------------------------------------
	
	/** Checks if this <code>MLabel</code> has bold true or false.
	  * 
	  * @return <code>true</code> if bold, <code>false</code> otherwise.
		*/
	public boolean isBold ()
	{
		return bold;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: setItalics
	// ---------------------------------------------------------------------------
	
	/** Allows italics for clients which support it, allow italics.
	  * 
	  * @deprecated
	  * 
	  */
    
	public void setItalics (boolean aItalics)
	{
		setItalic(aItalics);
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: isItalics
	// ---------------------------------------------------------------------------
	
	/** Checks the italics for this <code>MLabel</code>.
	  * 
	  * @deprecated
	  * 
		*/
	public boolean isItalics ()
	{
		return isItalic();
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setItalic
	// ---------------------------------------------------------------------------
	
	/** Allows italic text for clients which support it.
	  * 
	  * @param aItalic <code>true</code> to set italic, <code>false</code> otherwise.
	  *  
	  */
    
	public void setItalic (boolean aItalic)
	{
		italic = aItalic;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: isItalic
	// ---------------------------------------------------------------------------
	
	/** Returns a boolean indicating whether or not this <code>MLabel</code> is italicised.
	  * 
	  * @return <code>true</code> if italic, <code>false</code> otherwise.
		*/
	public boolean isItalic ()
	{
		return italic;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: setUnderline
	// ---------------------------------------------------------------------------
	
	/** Allows underline for clients which support it.
	  *
	  * @param aUnderline <code>true</code> to set underline, <code>false</code> otherwise.
	  */
    
	public void setUnderline (boolean aUnderline)
	{
		underline = aUnderline;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: isUnderline
	// ---------------------------------------------------------------------------
	
	/** Checks the underlining for this <code>MLabel</code>.
	  * 
		* @return <code>true</code> if underlined, <code>false</code> otherwise.
	  */
	public boolean isUnderline ()
	{
		return underline;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: setStrikethrough
	// ---------------------------------------------------------------------------
	
	/** Allows strikethrough for clients which support it.
	  *
	  * @param aStrikethrough <code>true</code> to set strikethrough, <code>false</code> otherwise.
	  */
    
	public void setStrikethrough (boolean aStrikethrough)
	{
		strikethrough = aStrikethrough;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: isStrikethrough
	// ---------------------------------------------------------------------------
	
	/** Checks if for this <code>MLabel</code> is set to strikethrough.
	  * 
		* @return <code>true</code> if set to strikethrough, <code>false</code> otherwise.
	  */
		
	public boolean isStrikethrough ()
	{
		return strikethrough;
	}
	
			
	// ---------------------------------------------------------------------------
	// METHOD: setLink
	// ---------------------------------------------------------------------------
	
	/** Sets a link object which the component may use to link to other content. To 
	  * remove an existing link, pass <code>null</code> here.
	  * 
	  * @param aLink The string representing the link.
	  */
	  
	public void setLink (String aLink)
	{
		link = aLink;
	}
	
			
	// ---------------------------------------------------------------------------
	// METHOD: setLink
	// ---------------------------------------------------------------------------
	
	/** Associates part of the <code>MLabel</code> text with the link. If the client browser 
		* can support links, (e.g. <code>HTML</code> is okay), then only the indicated text 
		* will be linkable.
	  *
	  *
	  * @param aLink The link.
	  * 
	  * @param aSubText The substring to associate with the link.
	  * 
	  */
	  
	public void setLink (String aLink, String aSubText)
	{
		link = aLink;
		subText = aSubText;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getSubText
	// ---------------------------------------------------------------------------
	
	/** Returns the subtext associated with a link. <code>null</code> means no subtext.
	  *
	  * @return The link's subtext.
	  * 
	  */
	  
	public String getSubText ()
	{
		return subText;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getLink
	// ---------------------------------------------------------------------------
	
	/** Returns a link object which components may use to direct the user to 
	  * external content.
	  *
	  * @return The link text.
	  */
	  
	public String getLink ()
	{
		return link;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: createEvent
	// ---------------------------------------------------------------------------
	
	/** Creates an event. This is used for <code>WML</code> deep navigation
		* and for links.
	  *
	  * @param aStateData The data associated with the event.
	  *
	  * @invisible
	  * 
	  */
	  
	public MauiEvent createEvent (String aStateData)
	{
	
		//++ 377 MW 2001.08.16
		MActionEvent returnValue = new MActionEvent (this, aStateData);
		
		// This will consume the ACTION_PUSH event, so that deep navigation clicks
		// don't cause an action event for MLabels (linkable with actionEvents)
		//
		// NOTE: This pattern could possibly be generalized in the HTTPEVentTranslator
		// so that is effects other types of components.
		// Other alternative is make Deep Navigation clicks not action events but
		// a different type of event, so it can be filtered by their event type.
		
		if (aStateData != null && aStateData.equals(MActionEvent.ACTION_PUSH))
		{
			returnValue.consume();
		}
		
		return returnValue;
		//-- 377
	}
  
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF