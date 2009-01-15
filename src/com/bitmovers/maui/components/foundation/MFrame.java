// ========================================================================
// com.bitmovers.maui.components.foundation.MFrame
// ========================================================================

package com.bitmovers.maui.components.foundation;

import java.awt.Color;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MauiEvent;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MFrame                                 (c) 2001 Bitmovers Systems
// ========================================================================

/** This class represents the main container into which components are 
  * added. An <code>MFrame</code> is conceptually similar to a 
  * <code>java.awt.Frame</code> object.
  * 
  */
  
public class MFrame extends MWindow implements HasPostValue
{
	private static final String base = "MFrame";
	private static int nameCounter = 0;
	
	private static final int DEFAULT_WIDTH = 0;
	private static final String DEFAULT_TITLE = "";
	private static final Color DEFAULT_TITLEBAR_COLOR = MDesktop.getColorFromHexString(ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_TITLE_BAR_COLOR));
	private static final Color DEFAULT_WINDOW_COLOR = MDesktop.getColorFromHexString(ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_WINDOW_COLOR));
	private static final String BITMOVERS_BRANDING = "<p align=\"right\"><font color=\"#999999\">Powered by <a href=\"http://bitmovers.com/maui.html\">Bitmovers Maui</a></font></p>";


	private MMenuBar menubar;
	private int width = 0;
	private boolean firstTime = true;
	private boolean html = false;
	private String titlebarBaseColor;
	private String titlebarShadowColor;
	private String titlebarHighlightColor;
	private String titlebarTextColor;
	private String eventValue = "";

	private String windowBaseColor;
	private String windowShadowColor;
	private String windowHighlightColor;
	private AuthorizationManager am = null;
	private boolean authorized = false;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new instance of <code>Frame</code> with no title.
	  *
	  */
	
	public MFrame()
	{
		this("");
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new instance of <code>Frame</code> with no title, but with
	  * specified color for the title bar.
	  *
	  * @param titlebarColour <code>Color</code> object to set the color of the titlebar.
	  */
	
	public MFrame(Color titlebarColour)
	{
		this(MFrame.DEFAULT_TITLE, titlebarColour);
	}
	
		
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new instance of <code>MFrame</code> with the given title.
	  *
	  * @param title A string used as the title that appear on the titlebar.
	  */
	
	public MFrame(String title)
	{
		this(title, MFrame.DEFAULT_WIDTH);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MFrame</code> object with the given title and 
	  * width (in pixels). In Maui, width measurements should be considered
	  * as suggestions rather than reliable values, as the size of components
	  * will vary depending on the client device.
	  * 
	  * @param title A string as the title of the <code>MFrame</code>.
	  *
	  * @param width sets the default with of the <code>MFrame</code>.
	  */
	 
	public MFrame(String title, int width)
	{
		this(title, width, MFrame.DEFAULT_TITLEBAR_COLOR);
	}


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MFrame</code> object with the given title and 
	  * a base colour for the titlebar.
	  * 
	  * @param title A string used as the title for the <code>MFrame</code>.
	  *
	  * @param titlebarColor <code>Color</code> object to set the base title color.
	  */
	 
	public MFrame(String title, Color titlebarColor)
	{
		this(title, MFrame.DEFAULT_WIDTH, titlebarColor);
	}


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MFrame</code> object with the given title,  
	  * width (in pixels) and Color object for the base color of the title bar. 
	  *	In Maui, width measurements should be considered
	  * as suggestions rather than reliable values, as the size of components
	  * will vary depending on the client device.
	  * 
	  * @param title A string used as the title for the <code>MFrame</code>.
	  *
	  * @param width Number of pixels to set the width of the frame.
		*
	  * @param titlebarColor <code>Color</code> object to set the base title color.
		*/
	 
	public MFrame(String title, int width, Color titlebarColor)
	{
		this.name = base + nameCounter++;
		this.title = title;
		this.width = width;
		setLayout(new MBoxLayout(this));
		
		String titlebarColorString = MDesktop.getHexStringFromColor(titlebarColor);
		String titlebarDefaultColor = MDesktop.getHexStringFromColor(MFrame.DEFAULT_TITLEBAR_COLOR);
		
		titlebarBaseColor = MDesktop.testColor(titlebarColorString, titlebarDefaultColor);
		titlebarShadowColor = "#" + MDesktop.adjustColor(titlebarBaseColor, -51);
		titlebarHighlightColor = "#" + MDesktop.adjustColor(titlebarBaseColor, +51);
		titlebarTextColor = ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_TITLE_BAR_TEXT_COLOR);
		
		windowBaseColor = MDesktop.getHexStringFromColor(MFrame.DEFAULT_WINDOW_COLOR);
		windowHighlightColor = "#" + MDesktop.adjustColor(windowBaseColor, +51);
		windowShadowColor = "#" + MDesktop.adjustColor(windowBaseColor, -51);
		am = AuthorizationManager.getInstance ();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getWidth
	// ----------------------------------------------------------------------
	
	/** Returns the suggested width of this frame (in pixels).
	  * 
	  * @return the suggested width of this frame (in pixels).
	  * 
	  */
	 
	public int getWidth()
	{
		return width;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setWidth
	// ----------------------------------------------------------------------
	
	/** Sets the suggested width for this frame to the specified size (in 
	  * pixels).
	  * 
	  * @param width The suggested width of this frame (in pixels).
	  *
	  */
	 
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getTitle
	// ----------------------------------------------------------------------
	
	/** Returns the title of this frame.
	  * 
	  * @return The title of this frame, or <code>null</code> 
	  *         if this frame doesn't have a title.
	  *
	  */
	
	public String getTitle()
	{
		return title;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setTitle
	// ----------------------------------------------------------------------
	
	/** Sets the title of this frame to the specified string.
	  * 
	  * @param title The desired title of this frame.
	  * 
	  */
	
	public void setTitle(String title)
	{
		this.title = title;
	}
  
  
	// ----------------------------------------------------------------------
	// METHOD: setTitlebarColor
	// ----------------------------------------------------------------------
	
	/** Sets the base color of the titlebar.  
	  * 
	  * 
	  * @param titlebarColor The desired base color of this frame's title bar.
	  * 
		*/
	
	public void setTitlebarColor(Color titlebarColor)
	{
		this.titlebarBaseColor = MDesktop.getHexStringFromColor(titlebarColor);
		
		titlebarShadowColor = "#" + MDesktop.adjustColor(titlebarBaseColor, -51);
		titlebarHighlightColor = "#" + MDesktop.adjustColor(titlebarBaseColor, +51);
		
		invalidate();
	}


	// ----------------------------------------------------------------------
	// METHOD: setTitlebarTextColor
	// ----------------------------------------------------------------------
	
	/** Sets the color of the title text in the titlebar.  
	  * 
	  * 
	  * @param textColor The desired base color of this frame's titlebar text.
	  * 
		*/
	
	public void setTitlebarTextColor(Color textColor)
	{
		this.titlebarTextColor = MDesktop.getHexStringFromColor(textColor);

		invalidate();
	}
  
  
	// ----------------------------------------------------------------------
	// METHOD: setWindowColor
	// ----------------------------------------------------------------------
	
	/** Sets the base color of the window (where all the components go). Note
		* the default is grey.
	  *
	  */
	
	public void setWindowColor(Color windowColor)
	{
	  this.windowBaseColor = MDesktop.getHexStringFromColor(windowColor);
	  
		windowShadowColor = "#" + MDesktop.adjustColor(windowBaseColor, -51);
		windowHighlightColor = "#" + MDesktop.adjustColor(windowBaseColor, +51);
	  
		invalidate();
	}


	// ----------------------------------------------------------------------
	// METHOD: getWindowColor
	// ----------------------------------------------------------------------
	
	/** Returns the base color of the window (where all the components go).
	  *
	  * @return <code>Color</code> object stating the color of this frame's window.
	  */
	
	public Color getWindowColor()
	{
	  return MDesktop.getColorFromHexString(this.windowBaseColor);
	}


	// ----------------------------------------------------------------------
	// METHOD: createEvent
	// ----------------------------------------------------------------------
	
	/** Creates an event. This is used for wml deep navigation. When backing
	  * out from the navigation stack, an event is delivered to the frame renderer
	  * so it can manage the navigation.
	  *
	  * @param aStateData The data associated with the event.
	  *
	  * @invisible
	  * 
	  */
	  
	public MauiEvent createEvent(String aStateData)
	{
		return (aStateData != null && aStateData.trim ().length () > 0 ?
					doCreateEvent (aStateData) :
					super.createEvent (aStateData));
		//return doCreateEvent (aStateData);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: doCreateEvent
	// ----------------------------------------------------------------------
	
	/** Utility method for <code>createEvent()</code>.
	  * 
	  */
	  
	protected MauiEvent doCreateEvent(String aStateData)
	{
		eventValue = aStateData;
		return new MActionEvent(this, aStateData);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** Performs all layout tasks necessary to set this frame's 
	  * parser values.
	  * 
	  * @invisible
	  * 
	  */
	
	public void fillParserValues()
	{
		super.fillParserValues();
		
		if (firstTime)
		{
			firstTime = false;
			MauiApplication theApplication = (MauiApplication) getRootParent ();
		}
		
		parser.setVariable("title", title);
		parser.setVariable("layoutManager", ((I_Renderable) getLayout()).render ());
		parser.setVariable("titlebarBaseColor", titlebarBaseColor);
		parser.setVariable("titlebarShadowColor", titlebarShadowColor);
		parser.setVariable("titlebarHighlightColor", titlebarHighlightColor);
		parser.setVariable("titlebarTextColor", titlebarTextColor);
		parser.setVariable("windowBaseColor", windowBaseColor);
		parser.setVariable("windowShadowColor", windowShadowColor);
		parser.setVariable("windowHighlightColor", windowHighlightColor);
	 	
		if (width != 0)
		{
			parser.setVariable("contentWidth", "width=\"" + Integer.toString(this.width - 6) + "\"");
			//super.parser.setVariable( "titlebarWidth", "width=\"" + Integer.toString(this.width - 46) + "\"" );
		}
		
		if (am.isAuthorized (am.AUTHORIZATION_BITMOVERSBRANDING))
		{
			parser.setVariable ("branding", BITMOVERS_BRANDING);
		}
		else if (!authorized)
		{
			authorized = true;
			parser.setVariable ("branding", "");
		}
		
		if (menubar != null)
		{
			parser.setVariable("menubar", getMenuBar().render());
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMenuBar
	// ----------------------------------------------------------------------
	
	/** Returns this frame's menu bar, or <code>null</code> if none exists.
	  * 
	  */
	
	public MMenuBar getMenuBar()
	{
		return menubar;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setMenuBar
	// ----------------------------------------------------------------------
	
	/** Sets this frame's menu bar to the given menu bar object. This method
	  * also represents the given menu bar with a reference to this frame.
	  * 
	  * @param menubar The given <code>MMenuBar</code> object to set menu bar to.
	  */
	
	public void setMenuBar(MMenuBar menubar)
	{
		if (this.menubar != menubar)
		{
			this.menubar = menubar;
			this.menubar.setParent(this);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: removeMenuBar
	// ----------------------------------------------------------------------
	
	/** Removes the menubar from this frame, if one exists.
	  * 
	  */
	
	public void removeMenuBar()
	{
		if (menubar != null)
		{
			menubar.setParent(null);
			menubar = null;
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
	/** Returns the post value for use in WML.
	  * 
	  * @return The value with WML safe string.
	  * @invisible
	  * 
	  */
	
  public String getPostValue()
  {
  	return "$(" + getWMLSafeComponentID () + ")";//":e)";
  }
  
  
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF