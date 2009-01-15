package com.bitmovers.maui.components.foundation;

import java.util.*;
import java.awt.*;
import java.net.URLEncoder;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// =============================================================================
// CLASS: MTabbedPaneButton                           (c) 2001 Bitmovers Systems
// =============================================================================

/** The MTabbedPaneButton class extends the MButton class to provide buttons
  * which are appropriate for the MTabbedPane. These buttons have four states:
  * foreground (non-clickable), background (mouse out), background (mouse over),
  * and background (mouse down).
  *
  * @invisible
  * 
  */

public class MTabbedPaneButton extends MButton
	implements SelectedPaintable
{
	
	
	private static final String base = "MTabbedPaneButton";
	private static int nameCounter = 0;
	private int myID;
	private boolean dispatching = false;
	private boolean selected = false;

	// Dynamic image generation helpers
	private static final int MINIMUM_BUTTON_WIDTH = 74;
	private static final int MINIMUM_MARGIN_WIDTH = 16;
	private static final int BUTTON_HEIGHT = 22;
	private static final int FONT_HEIGHT = 14;
	private static final int SPACE_WIDTH = 4;
	private static final int TEXT_Y_POSITION = 4;

	private static final Color grey1 = new Color(204, 204, 204);
	private static final Color grey2 = new Color(170, 170, 170);
	private static final Color grey3 = new Color(153, 153, 153);


	// ---------------------------------------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------------------------------------

	/** Constructs a new MButton object with the default label "OK".
    *
    */

	public MTabbedPaneButton()
	{
		super("Untitled Tab");
	}
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------------------------------------

	/** Constructs a new MButton object with the specified label.
    * 
    */

	public MTabbedPaneButton(String label)
	{
		super(label);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: paint
	// ---------------------------------------------------------------------------

	/** This method paints the default state of the button, which in this case is
	  * the tabbed pane button being in the foreground.
	  * 
	  * @invisible
	  * 
	  */

	public void paint(Graphics graphics)
	{
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();

			byte[] westEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/this.west.gif");
			byte[] eastEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/this.east.gif");
			
			Image westEndImage = imageFactory.getImage(westEnd);
			Image eastEndImage = imageFactory.getImage(eastEnd);
			
			Dimension size = this.getSize();

			graphics.drawImage(westEndImage, 0, 0, MComponent.DUMMY_COMPONENT);
			graphics.drawImage(eastEndImage, (size.width - 16), 0, MComponent.DUMMY_COMPONENT);
			
			// Top line and highlight
			graphics.setColor(Color.black);
			graphics.drawLine(16, 1, (size.width - 17), 1);
			graphics.setColor(Color.white);
			graphics.drawLine(16, 2, (size.width - 17), 2);
		}
		catch (ResourceNotFoundException exception)
		{
			System.out.println(new ErrorString(exception, "MTabbedPaneButton.paint()"));
		}

		super.paintLabelOnButton(graphics, 0, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintMouseOut
	// ---------------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void paintMouseOut(Graphics graphics)
	{
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();

			byte[] westEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/out.west.gif");
			byte[] eastEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/out.east.gif");
			
			Image westEndImage = imageFactory.getImage(westEnd);
			Image eastEndImage = imageFactory.getImage(eastEnd);
			
			Dimension size = this.getSize();

			graphics.drawImage(westEndImage, 0, 0, MComponent.DUMMY_COMPONENT);
			graphics.drawImage(eastEndImage, (size.width - 16), 0, MComponent.DUMMY_COMPONENT);
			
			// Top line and highlight
			graphics.setColor(Color.black);
			graphics.drawLine(16, 1, (size.width - 17), 1);
			graphics.drawLine(16, 17, (size.width - 17), 17);
			graphics.setColor(MTabbedPaneButton.grey1);
			graphics.drawLine(16, 2, (size.width - 17), 2);
			graphics.setColor(Color.white);
			graphics.drawLine(0, 18, size.width, 18);
			
			// Fill
			graphics.setColor(MTabbedPaneButton.grey2);
			graphics.fillRect(10, 3, (size.width - 21), 14);
		}
		catch (ResourceNotFoundException exception)
		{
			System.out.println(new ErrorString(exception, "MTabbedPaneButton.paint()"));
		}

		super.paintLabelOnButton(graphics, 0, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintSelected
	// ---------------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void paintSelected(Graphics graphics)
	{
	  this.paint(graphics);
	}


	// ----------------------------------------------------------------------
	// METHOD: paintDisabled
	// ----------------------------------------------------------------------
	
	/** This method paints the button's disabled state into the given graphics
	  * context.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void paintDisabled(Graphics graphics)
	{
	  super.paintDisabled(graphics, 6);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: paintMouseOver
	// ---------------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void paintMouseOver(Graphics graphics)
	{
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();

			byte[] westEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/over.west.gif");
			byte[] eastEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/over.east.gif");
			
			Image westEndImage = imageFactory.getImage(westEnd);
			Image eastEndImage = imageFactory.getImage(eastEnd);
			
			Dimension size = this.getSize();

			graphics.drawImage(westEndImage, 0, 0, MComponent.DUMMY_COMPONENT);
			graphics.drawImage(eastEndImage, (size.width - 16), 0, MComponent.DUMMY_COMPONENT);
			
			// Top line and highlight
			graphics.setColor(Color.black);
			graphics.drawLine(16, 1, (size.width - 17), 1);
			graphics.drawLine(16, 17, (size.width - 17), 17);
			graphics.setColor(super.yellow1);
			graphics.drawLine(16, 2, (size.width - 17), 2);
			graphics.setColor(Color.white);
			graphics.drawLine(0, 18, size.width, 18);
			
			// Fill
			graphics.setColor(super.yellow2);
			graphics.fillRect(10, 3, (size.width - 21), 14);
		}
		catch (ResourceNotFoundException exception)
		{
			System.out.println(new ErrorString(exception, "MTabbedPaneButton.paint()"));
		}

		super.paintLabelOnButton(graphics, 0, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintMouseDown
	// ---------------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void paintMouseDown(Graphics graphics)
	{
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();

			byte[] westEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/down.west.gif");
			byte[] eastEnd = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MTabbedPaneButton/down.east.gif");
			
			Image westEndImage = imageFactory.getImage(westEnd);
			Image eastEndImage = imageFactory.getImage(eastEnd);
			
			Dimension size = this.getSize();

			graphics.drawImage(westEndImage, 0, 0, MComponent.DUMMY_COMPONENT);
			graphics.drawImage(eastEndImage, (size.width - 16), 0, MComponent.DUMMY_COMPONENT);
			
			// Top line and highlight
			graphics.setColor(Color.black);
			graphics.drawLine(16, 1, (size.width - 17), 1);
			graphics.drawLine(16, 17, (size.width - 17), 17);
			graphics.setColor(super.yellow4);
			graphics.drawLine(16, 2, (size.width - 17), 2);
			graphics.setColor(Color.white);
			graphics.drawLine(0, 18, size.width, 18);
			
			// Fill
			graphics.setColor(super.yellow2);
			graphics.fillRect(10, 3, (size.width - 21), 14);
		}
		catch (ResourceNotFoundException exception)
		{
			System.out.println(new ErrorString(exception, "MTabbedPaneButton.paint()"));
		}

		super.paintLabelOnButton(graphics, 1, 1);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getTextYPosition
	// ---------------------------------------------------------------------------
	
	/** getTextYPosition() returns the co-ordinate on the y-axis of the upper-left
	  * hand corner where text is painted.
	  * 
	  * @invisible
	  *
	  */
	
	protected int getTextYPosition()
	{
		return this.TEXT_Y_POSITION;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getSize
	// ---------------------------------------------------------------------------
	
	/** getSize() returns the dimensions of the button. Subclasses may need to
	  * override this method to perform their own calculations.
	  *
	  */
	
	public Dimension getSize()
	{
		int width;
		
		if ((width = (super.getLabelWidth() + (this.MINIMUM_MARGIN_WIDTH * 2))) < this.MINIMUM_BUTTON_WIDTH)
		{
			return new Dimension(this.MINIMUM_BUTTON_WIDTH, this.BUTTON_HEIGHT);
		}
		else
		{
			return new Dimension(width, this.BUTTON_HEIGHT);
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: createEvent
	// ---------------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public MauiEvent createEvent(String aEventData)
	{
		return parent.createEvent(aEventData);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: dispatchActionEvent
	// ---------------------------------------------------------------------------

	/** This method causes the given MActionEvent to be published to all 
	  * MActionEventListeners.
	  *
	  * @param aEvent The MActionEvent to propagate
	  *
	  */
	
	public void dispatchActionEvent (MActionEvent aEvent)
	{
		if (!dispatching)
		{
			dispatching = true;
			super.dispatchActionEvent (aEvent);
			dispatching = false;
		}
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: setSelected
	// ---------------------------------------------------------------------------

	protected void setSelected (boolean aSelected)
	{
		selected = aSelected;
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: isSelected
	// ---------------------------------------------------------------------------

	public boolean isSelected ()
	{
		return selected;
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: getLabelID
	// ---------------------------------------------------------------------------

	protected String getLabelID ()
	{
		return getComponentID ();
	}
	  

	// ---------------------------------------------------------------------------
	// METHOD: getLabelID
	// ---------------------------------------------------------------------------

  /** Get the boolean indicating if duplicate events are allowed or not.
    *
    * @return The boolean indicating a duplicate event
    * 
    */

  public boolean getAllowDuplicateEvents ()
  {
  	boolean retVal = super.getAllowDuplicateEvents ();
  	
  	if (retVal)
  	{
  		//
  		//	Check the MTabbedPane if duplicates are allowed
  		//
  		if (parent instanceof MTabbedPane.MTabbedComponent)
  		{
  			retVal = parent.getParent ().getAllowDuplicateEvents ();
  		}
  	}
  	return retVal;
  }


	// ---------------------------------------------------------------------------
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF