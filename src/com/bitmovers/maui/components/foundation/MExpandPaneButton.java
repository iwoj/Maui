// =============================================================================
// com.bitmovers.maui.components.foundation.MExpandPaneButton
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.*;
import java.awt.*;
import java.net.URLEncoder;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.render.I_Renderer;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MExpandPaneButton                      (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>MExpandPaneButton</code> class extends the <code>MButton</code> class to provide buttons
  * which are appropriate for the <code>MExpandPane</code>. These buttons have six states:
  * opened-mouse out, opened-mouse over, opened-mouse down, closed-mouse out,
  * closed-mouse over, and closed-mouse down.
  *
  * @invisible
  * 
  */

public class MExpandPaneButton extends MButton
{
	
	private static final String base = "MExpandPaneButton";
	private static final int BUTTON_HEIGHT = 18;
	private static final int FONT_HEIGHT = 14;
	private static final int SPACE_WIDTH = 4;
	private static final int TEXT_Y_POSITION = 2;
	private static final int TRIANGLE_WIDTH = 15;
	
	private static int nameCounter = 0;
	
	private int myID;
	
	private final MExpandPane expandPane;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MButton</code> object with the default label ("OK").
    *
    */
	
	protected MExpandPaneButton(MExpandPane aExpandPane)
	{
		super ("Untitled Pane");
		expandPane = aExpandPane;
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MButton</code> object with the specified label.
    * 
    */
	
	protected MExpandPaneButton (String label, MExpandPane aExpandPane)
	{
		super(label);
		expandPane = aExpandPane;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: isOpen
	// ----------------------------------------------------------------------
	
	/** Checks whether the expand pane is open.
		*
		* @return <code>true</code> is the expand pane is open, <code>false</code> otherwise.
	  *
	  */
	
	public boolean isOpen()
	{
		return expandPane.isOpen ();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: toggle
	// ----------------------------------------------------------------------
	
	/** Toggles the state of the <code>MExandPaneButton</code> (e.g. if the pane is open, 
	  * this method will close it; if it is closed, this method will open it)..
	  *
	  */
	
	protected void toggle()
	{
		expandPane.setOpen (!isOpen ());
	}
	
	// ----------------------------------------------------------------------
	// METHOD: paint
	// ----------------------------------------------------------------------

	/** Paints the default state of the button, which in this case is
	  * the a closed, mouse-out <code>MExpandPaneButton</code>.
	  *
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
	  */

	public void paint(Graphics graphics)
	{
		this.paintMouseOut(graphics);
	}


	// ----------------------------------------------------------------------
	// METHOD: paintMouseOut
	// ----------------------------------------------------------------------
	
	/** Paints the mouse out state of the button.
	  *
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
		*/
	
	public void paintMouseOut(Graphics graphics)
	{
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();

			String resourcePath = null;
			
			if (this.isOpen())
			{
				resourcePath = "com/bitmovers/maui/components/foundation/MExpandPaneButton/triangle.opened.out.gif";
			}
			else
			{
				resourcePath = "com/bitmovers/maui/components/foundation/MExpandPaneButton/triangle.closed.out.gif";
			}
			
			byte[] triangle = resourceManager.getResourceBytes(resourcePath);
			
			Image triangleImage = imageFactory.getImage(triangle);
			
			Dimension size = this.getSize();

			graphics.drawImage(triangleImage, 0, 2, MComponent.DUMMY_COMPONENT);
		}
		catch (ResourceNotFoundException exception)
		{
			System.out.println(new ErrorString(exception, "MExpandPaneButton.paintMouseOut()"));
		}

		super.paintLabelOnButton(graphics, 9, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintMouseOver
	// ---------------------------------------------------------------------------
	
	/** Paints the mouse over state of the button.
	  *
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
		*/
	
	public void paintMouseOver(Graphics graphics)
	{
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();

			String resourcePath = null;
			
			if (this.isOpen())
			{
				resourcePath = "com/bitmovers/maui/components/foundation/MExpandPaneButton/triangle.opened.over.gif";
			}
			else
			{
				resourcePath = "com/bitmovers/maui/components/foundation/MExpandPaneButton/triangle.closed.over.gif";
			}
			
			byte[] triangle = resourceManager.getResourceBytes(resourcePath);
			
			Image triangleImage = imageFactory.getImage(triangle);
			
			Dimension size = this.getSize();

			graphics.drawImage(triangleImage, 0, 2, MComponent.DUMMY_COMPONENT);
		}
		catch (ResourceNotFoundException exception)
		{
			System.out.println(new ErrorString(exception, "MExpandPaneButton.paintMouseOver()"));
		}

		super.paintLabelOnButton(graphics, 9, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintMouseDown
	// ---------------------------------------------------------------------------
	
	/** Paints the mouse down state of the button.
	  *
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
		*/
	
	public void paintMouseDown(Graphics graphics)
	{
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();

			String resourcePath = null;
			
			if (this.isOpen())
			{
				resourcePath = "com/bitmovers/maui/components/foundation/MExpandPaneButton/triangle.opened.down.gif";
			}
			else
			{
				resourcePath = "com/bitmovers/maui/components/foundation/MExpandPaneButton/triangle.closed.down.gif";
			}
			
			byte[] triangle = resourceManager.getResourceBytes(resourcePath);
			
			Image triangleImage = imageFactory.getImage(triangle);
			
			Dimension size = this.getSize();

			graphics.drawImage(triangleImage, 0, 2, MComponent.DUMMY_COMPONENT);
		}
		catch (ResourceNotFoundException exception)
		{
			System.out.println(new ErrorString(exception, "MExpandPaneButton.paintMouseDown()"));
		}

		super.paintLabelOnButton(graphics, 9, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: getTextYPosition
	// ---------------------------------------------------------------------------
	
	/** Returns the co-ordinate on the y-axis of the upper-left
	  * hand corner where text is painted.
	  *
	  * @return Y position int value indicating upper-left coner where text is painted.
	  *
	  */
	
	protected int getTextYPosition()
	{
		return this.TEXT_Y_POSITION;
	}


	// ---------------------------------------------------------------------------
	// METHOD: getSize
	// ---------------------------------------------------------------------------
	
	/** Returns the dimensions of the button. Subclasses may need to
	  * override this method to perform their own calculations.
	  *
	  * @return The <code>Dimension</code> with the button's width and height.
	  */
	
	public Dimension getSize()
	{
		int width = super.getLabelWidth() + this.SPACE_WIDTH + this.TRIANGLE_WIDTH;

		return new Dimension(width, this.BUTTON_HEIGHT);
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: createEvent
	// ---------------------------------------------------------------------------
	
  /** Overrides <code>Component.createEvent()</code> to toggle the triangle position and 
    * returns the appropriate event.
    *
    * @return a new <code>MActionEvent</code> with the <code>MActionEvent.ACTION_OPENED</code> 
    *         command if the expand pane has just opened, or 
    *         <code>MActionEvent.ACTION_CLOSED</code> if it has just closed.
    *
    * @invisible
    * 
    */
	
	public MauiEvent createEvent(String aEventData)
	{
		toggle();
		
		return new MActionEvent (this, (isOpen () ? MActionEvent.ACTION_OPENED :
													MActionEvent.ACTION_CLOSED));
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getRenderer
	// ---------------------------------------------------------------------------
	
  /** Returns the renderer for this object.
    *
    * @return render for the expand pane.
    */
	
	public I_Renderer getRenderer()
	{
		return getRenderer(expandPane);
	}


	// ---------------------------------------------------------------------------
	// METHOD: fillParserValues
	// ---------------------------------------------------------------------------
	
	/** Sets parser values for the component's renderer.
	  * 
	  */
	  
	public void fillParserValues()
	{
		super.fillParserValues();
		
		if (isOpen())
		{
			parser.setVariable("extra", "opened");
		}
		else
		{
			parser.setVariable("extra", "closed");
		}
	}


	// ---------------------------------------------------------------------------
	// METHOD: getLabelID
	// ---------------------------------------------------------------------------
	
	/** Returns expand pane's label text.
		*
		* @return A string value of the expand pane label.
	  * 
	  */
	  
	protected String getLabelID ()
	{
		return getComponentID ();
	}
	
	public MComponent getRootParent ()
	{
		rootParent = expandPane.getRootParent ();
		return rootParent;
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF