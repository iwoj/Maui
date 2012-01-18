package com.bitmovers.maui.components.foundation;

import java.awt.*;
import java.util.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;

// ========================================================================
// CLASS: MMenuButton                            (c) 2001 Bitmovers Systems
// ========================================================================

/** The MMenuButton class extends the MButton class to provide buttons
  * which are appropriate for the MMenu. These buttons have nine states:
  * foreground (open), background (mouse out), background (mouse over),
  * and background (mouse down).
  *
  * There are currently two "levels", or drawing styles of menus supported.
  * LEVEL1 is used for the main menubar, LEVEL2 is used for the menu items
  * which have a parent. LEVEL2 items can also be parents too, however.
  *
  * The colour of the buttons can be selected for MMenuBarButtons. A single
  * light colour should be picked and used for all "levels". A default colour
  * is defined which goes with the standard Maui interface.
  *
  * If a MMenuBarButton is to be used as a submenu, the method setChildren(true)
  * needs to be called so that it knows to draw itself using the triangles.
  *
  * @invisible
  * 
  */

public class MMenuButton extends MButton
{
	
	
	public static final String ACTION_CLICKED = "clicked";
	public static final String ACTION_OPENED = "opened";
	public static final String ACTION_CLOSED = "closed";
	
	public static final byte LEVEL1 = 1;
	public static final byte LEVEL2 = 2;
	
	private static final String base = "MMenuButton";
	private static int nameCounter = 0;
	private int myID;

	// Dynamic image generation helpers
	private static final int MINIMUM_BUTTON_WIDTH = 40;
	private static final int MINIMUM_MARGIN_WIDTH = 10;
	private static final int BUTTON_HEIGHT = 19;
	private static final int FONT_HEIGHT = 14;
	private static final int SPACE_WIDTH = 4;
	private static final int TEXT_Y_POSITION = 3;
	private static final int TRIANGLE_WIDTH = 7;

	private Color color1;
	private Color color2;
	private Color color3;
	private Color color4;
	private Color color5;
	private Color color6;
	
	private byte level = 1;
	private boolean hasChildren = false;
	private boolean open = false;
	
	//private MMenuItem parent;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	/** Constructs a new MMenuButton object with the specified label.
    * 
    */

	protected MMenuButton(MMenuItem parent, String label)
	{
		this(parent, label, MMenuBar.PRIMARY_COLOR);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new MMenuButton object with the specified label, using the
	  * specified Color as the base color.
    * 
    */

	protected MMenuButton(MMenuItem parent, String label, byte level)
	{
		this(parent, label, MMenuBar.PRIMARY_COLOR, level);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new MMenuButton object with the specified label, using the
	  * specified Color as the base color.
    * 
    */

	protected MMenuButton(MMenuItem parent, String label, Color color)
	{
		this(parent, label, color, MMenuButton.LEVEL1);
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Constructs a new MMenuButton object with the specified label, using the
	  * specified Color as the base color, and the specified level as the level
	  * of menu.
    * 
    */

	protected MMenuButton(MMenuItem parent, String label, Color color, byte level)
	{
		super(label);
		this.parent = parent;
		this.setLevel(level);
		this.setColor(color);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: createEvent
	// ---------------------------------------------------------------------------
	
	public MauiEvent createEvent (String stateData)
	{
		MActionEvent event;
		
		event = new MActionEvent(this, MMenuButton.ACTION_CLICKED);
		
		return event;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setLevel
	// ---------------------------------------------------------------------------

	/** setLevel() sets the button's level.
	  *
	  */
	
	protected void setLevel(byte level)
	{
		switch (level)
		{
			case MMenuButton.LEVEL2:
			{
				this.level = MMenuButton.LEVEL2;
				break;
			}
			default:
			{
				this.level = MMenuButton.LEVEL1;
				break;
			}
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setColor
	// ---------------------------------------------------------------------------

	/** setColor() sets the button's color.
	  *
	  */
	
	protected void setColor(Color color)
	{
		// Since color3 is what the other colors are based on, we need to
		// initialise it first
		this.color3 = color;
		this.color1 = this.getColorOffset(102);
		this.color2 = this.getColorOffset(51);
		this.color4 = this.getColorOffset(-51);
		this.color5 = this.getColorOffset(-102);
		this.color6 = this.getColorOffset(-153);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getColor
	// ---------------------------------------------------------------------------

	/** Returns a clone of the button's current color.
	  *
	  */
	
	public Color getColor()
	{
		// Don't return the actual reference. The colour must be set through the
		// setColor() accessor method.
		return new Color(this.color3.getRGB());
	}
	
		
	// ---------------------------------------------------------------------------
	// METHOD: setChildren
	// ---------------------------------------------------------------------------

	/** setChildren() sets a flag with this MMenuButton indicating that it has
	  * children. MMenuButtons with children have a triangle which rotates to
	  * indicate if it is displaying its children or not.
	  *
	  */

	protected void setChildren(boolean hasChildren)
	{
		this.hasChildren = hasChildren;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setChildren
	// ---------------------------------------------------------------------------
	
	public I_Renderer getRenderer()
	{
		return getRenderer(this.parent);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: hasChildren
	// ---------------------------------------------------------------------------

	/** hasChildren() returns the flag indicating whether or not this MMenuButton
	  * has children.
	  *
	  */

	public boolean hasChildren()
	{
		return this.hasChildren;
	}


	// ---------------------------------------------------------------------------
	// METHOD: isOpen
	// ---------------------------------------------------------------------------
	
	/** isOpen() returns a boolean representing this MMenuButton's openness.
	  *
	  */
	
	public boolean isOpen()
	{
		return this.open;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: open
	// ---------------------------------------------------------------------------
	
	/** Opens the MMenuButton.
	  *
	  */
	
	protected void open()
	{
		if (!this.open)
		{
			this.open = true;
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: close
	// ---------------------------------------------------------------------------
	
	/** Closes the MMenuButton.
	  *
	  */
	
	protected void close()
	{
		if (this.open)
		{
			this.open = false;
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: toggle
	// ---------------------------------------------------------------------------
	
	/** toggle() toggles the state of the MMenuButton (open or closed).
	  *
	  */
	
	protected void toggle()
	{
		if (this.open)
		{
			this.open = false;
		}
		else
		{
			this.open = true;
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: paint
	// ---------------------------------------------------------------------------

	/** This method paints the default state of the button, which in this case is
	  * a mouse out, menu closed.
	  *
	  */
	
	public void paint(Graphics graphics)
	{
		this.paintMouseOut(graphics);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: paintMouseOut
	// ---------------------------------------------------------------------------
	
	/** This method paints the out state of the MMenuButton, with the menu closed.
	  *
	  */
	
	public void paintMouseOut(Graphics graphics)
	{
		// If the menu is open, we want to paint the appropriate state
		if (this.hasChildren() && this.isOpen())
		{
			this.paintOpenMouseOut(graphics);
			return;
		}

		switch (this.level)
		{
			// Top menu level (Added directly to the MMenuBar)
			case (MMenuButton.LEVEL1):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color3);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Highlight
				graphics.setColor(this.color2);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);

				// Shadow
				graphics.setColor(this.color4);
				graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
				graphics.drawLine(0, (size.height - 2), (size.width - 1), (size.height - 2));
				
				// Button divider
				graphics.setColor(this.color6);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));				

				break;
			}

			// Sub-menu level (MMenuItem added to a MMenu)
			case (MMenuButton.LEVEL2):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color3);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Highlight
				graphics.setColor(this.color2);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);

				// Shadow
				graphics.setColor(this.color4);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				graphics.drawLine(0, (size.height - 2), (size.width - 1), (size.height - 2));
				
				// Button divider
				if (this.isLighterColor(this.color3))
				{
					graphics.setColor(this.color5);
				}
				else
				{
					graphics.setColor(this.color6);
				}
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

				break;
			}
		}

		this.paintLabelOnButton(graphics, 0, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintMouseOver
	// ---------------------------------------------------------------------------
	
	public void paintMouseOver(Graphics graphics)
	{
		// If the menu is open, we want to paint the appropriate state
		if (this.hasChildren() && this.isOpen())
		{
			this.paintOpenMouseOver(graphics);
			return;
		}

		switch (this.level)
		{
			// Top menu level (Added directly to the MMenuBar)
			case (MMenuButton.LEVEL1):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color2);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Highlight
				graphics.setColor(this.color1);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);

				// Shadow
				graphics.setColor(this.color3);
				graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
				graphics.drawLine(0, (size.height - 2), (size.width - 1), (size.height - 2));
				
				// Button divider
				graphics.setColor(this.color6);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

				break;
			}

			// Sub-menu level (MMenuItem added to a MMenu)
			case (MMenuButton.LEVEL2):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color2);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Highlight
				graphics.setColor(this.color1);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);

				// Shadow
				graphics.setColor(this.color3);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				graphics.drawLine(0, (size.height - 2), (size.width - 1), (size.height - 2));
				
				// Button divider
				if (this.isLighterColor(this.color3))
				{
					graphics.setColor(this.color5);
				}
				else
				{
					graphics.setColor(this.color6);
				}
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

				
				break;
			}
		}

		this.paintLabelOnButton(graphics, 0, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintMouseDown
	// ---------------------------------------------------------------------------
	
	public void paintMouseDown(Graphics graphics)
	{
		// If the menu is open, we want to paint the appropriate state
		if (this.hasChildren() && this.isOpen())
		{
			this.paintOpenMouseDown(graphics);
			return;
		}

		switch (this.level)
		{
			// Top menu level (Added directly to the MMenuBar)
			case (MMenuButton.LEVEL1):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color4);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Shadow
				graphics.setColor(this.color5);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);

				// Button divider
				graphics.setColor(this.color6);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

				break;
			}

			// Sub-menu level (MMenuItem added to a MMenu)
			case (MMenuButton.LEVEL2):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color4);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Shadow
				graphics.setColor(this.color5);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);

				// Button divider
				if (this.isLighterColor(this.color3))
				{
					graphics.setColor(this.color5);
				}
				else
				{
					graphics.setColor(this.color6);
				}
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

				break;
			}
		}

		this.paintLabelOnButton(graphics, 1, 1);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintOpenMouseOut
	// ---------------------------------------------------------------------------
	
	/** This method paints the MMenuButton to be in an open state, mouse out.
	  *
	  */

	public void paintOpenMouseOut(Graphics graphics)
	{
		switch (this.level)
		{
			// Top menu level (Added directly to the MMenuBar)
			case (MMenuButton.LEVEL1):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color4);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Highlight
				graphics.setColor(this.color3);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);
				
				// Shadow
				graphics.setColor(this.color5);
				graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));
				
				// Button divider
				graphics.setColor(this.color6);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				
				break;
			}

			// Sub-menu level (MMenuItem added to a MMenu)
			case (MMenuButton.LEVEL2):
			{
				Dimension size = this.getSize();

				if (this.isLighterColor(this.color3))
				{
					// Fill
					graphics.setColor(this.color4);
					graphics.fillRect(0, 0, size.width, size.height);
					
					// Highlight
					graphics.setColor(this.color3);
					graphics.drawLine(0, 0, size.width, 0);
					graphics.drawLine(1, 0, 1, size.height);

					// Shadow
					graphics.setColor(this.color5);
					graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
					graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

					// Button divider
					graphics.setColor(this.color6);
					graphics.drawLine(0, 0, 0, size.height);
					graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				}
				else
				{
					// Fill
					graphics.setColor(this.color2);
					graphics.fillRect(0, 0, size.width, size.height);
					
					// Highlight
					graphics.setColor(this.color1);
					graphics.drawLine(0, 0, size.width, 0);
					graphics.drawLine(1, 0, 1, size.height);

					// Shadow
					graphics.setColor(this.color3);
					graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
					graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

					// Button divider
					graphics.setColor(this.color6);
					graphics.drawLine(0, 0, 0, size.height);
					graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				}
				
				break;
			}
		}

		this.paintLabelOnButton(graphics, 0, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintOpenMouseOver
	// ---------------------------------------------------------------------------
	
	/** This method paints the MMenuButton to be in an open state, mouse over.
	  *
	  */

	public void paintOpenMouseOver(Graphics graphics)
	{
		switch (this.level)
		{
			// Top menu level (Added directly to the MMenuBar)
			case (MMenuButton.LEVEL1):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color3);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Highlight
				graphics.setColor(this.color2);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);
				
				// Shadow
				graphics.setColor(this.color4);
				graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
				graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));
				
				// Button divider
				graphics.setColor(this.color6);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				
				break;
			}

			// Sub-menu level (MMenuItem added to a MMenu)
			case (MMenuButton.LEVEL2):
			{
				Dimension size = this.getSize();

				if (this.isLighterColor(this.color3))
				{
					// Fill
					graphics.setColor(this.color3);
					graphics.fillRect(0, 0, size.width, size.height);
					
					// Highlight
					graphics.setColor(this.color2);
					graphics.drawLine(0, 0, size.width, 0);
					graphics.drawLine(1, 0, 1, size.height);

					// Shadow
					graphics.setColor(this.color4);
					graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
					graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

					// Button divider
					graphics.setColor(this.color5);
					graphics.drawLine(0, 0, 0, size.height);
					graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				}
				else
				{
					// Fill
					graphics.setColor(this.color2);
					graphics.fillRect(0, 0, size.width, size.height);
					
					// Highlight
					graphics.setColor(this.color1);
					graphics.drawLine(0, 0, size.width, 0);
					graphics.drawLine(1, 0, 1, size.height);

					// Shadow
					graphics.setColor(this.color3);
					graphics.drawLine((size.width - 2), 0, (size.width - 2), size.height);
					graphics.drawLine(0, (size.height - 1), (size.width - 1), (size.height - 1));

					// Button divider
					graphics.setColor(this.color6);
					graphics.drawLine(0, 0, 0, size.height);
					graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				}

				break;
			}
		}

		this.paintLabelOnButton(graphics, 0, 0);
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintOpenMouseDown
	// ---------------------------------------------------------------------------
	
	/** This method paints the MMenuButton to be in an open state, mouse down.
	  *
	  */

	public void paintOpenMouseDown(Graphics graphics)
	{
		switch (this.level)
		{
			// Top menu level (Added directly to the MMenuBar)
			case (MMenuButton.LEVEL1):
			{
				Dimension size = this.getSize();

				// Fill
				graphics.setColor(this.color4);
				graphics.fillRect(0, 0, size.width, size.height);
				
				// Shadow
				graphics.setColor(this.color5);
				graphics.drawLine(0, 0, size.width, 0);
				graphics.drawLine(0, 0, 0, size.height);
				
				// Button divider
				graphics.setColor(this.color6);
				graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				
				break;
			}

			// Sub-menu level (MMenuItem added to a MMenu)
			case (MMenuButton.LEVEL2):
			{
				Dimension size = this.getSize();

				if (this.isLighterColor(this.color3))
				{
					// Fill
					graphics.setColor(this.color2);
					graphics.fillRect(0, 0, size.width, size.height);
					
					// Shaadow
					graphics.setColor(this.color3);
					graphics.drawLine(0, 0, size.width, 0);
					graphics.drawLine(1, 0, 1, size.height);

					// Button divider
					graphics.setColor(this.color5);
					graphics.drawLine(0, 0, 0, size.height);
					graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				}
				else
				{
					// Fill
					graphics.setColor(this.color3);
					graphics.fillRect(0, 0, size.width, size.height);
					
					// Shadow
					graphics.setColor(this.color4);
					graphics.drawLine(0, 0, size.width, 0);
					graphics.drawLine(1, 0, 1, size.height);

					// Button divider
					graphics.setColor(this.color6);
					graphics.drawLine(0, 0, 0, size.height);
					graphics.drawLine((size.width - 1), 0, (size.width - 1), size.height);
				}

				break;
			}
		}

		this.paintLabelOnButton(graphics, 1, 1);
	}


	// ---------------------------------------------------------------------------
	// METHOD: getTextYPosition
	// ---------------------------------------------------------------------------
	
	/** getTextYPosition() returns the co-ordinate on the y-axis of the upper-left
	  * hand corner where text is painted.
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
	// METHOD: getLabelWidth
	// ---------------------------------------------------------------------------
	
	/** getLabelWidth() overrides the method of the same name in the super class.
	  * This method needs to compensate for the width of the triangle which
	  * indicates that the MMenuButton has children.
	  *
	  */
	
	protected int getLabelWidth()
	{
		if (this.hasChildren)
		{
			return (super.getLabelWidth() + this.TRIANGLE_WIDTH);
		}
		else
		{
			return super.getLabelWidth();
		}
	}


	// ---------------------------------------------------------------------------
	// METHOD: paintLabelOnButton
	// ---------------------------------------------------------------------------
	
	/** paintLabelOnButton() overrides the method of the same name in the super
	  * class. This method needs to draw the triangle which indicates that the
	  * MMenuButton has children (if necessary).
	  *
	  */

	protected void paintLabelOnButton(Graphics graphics, int offsetX, int offsetY)
	{
		super.paintLabelOnButton(graphics, offsetX, offsetY);
		
		try
		{
			if (this.hasChildren)
			{
				String trianglePath = null;
				Dimension size = this.getSize();
				
				if (this.isOpen())
				{
					trianglePath = "com/bitmovers/maui/components/foundation/MMenuBar/triangle.opened.gif";
				}
				else
				{
					trianglePath = "com/bitmovers/maui/components/foundation/MMenuBar/triangle.closed.gif";
				}
			
				byte[] triangleBytes = ResourceManager.getInstance().getResourceBytes(trianglePath);
					
				Image triangleImage = ImageFactory.getInstance().getImage(triangleBytes);
				
				int x = ((size.width - super.getLabelWidth()) / 2) + super.getLabelWidth() + offsetX - 2;
				int y = this.getTextYPosition() + offsetY - 1;

				graphics.drawImage(triangleImage, x, y, MComponent.DUMMY_COMPONENT);
			}
		}
		catch (ResourceNotFoundException exception)
		{
			System.err.println(new ErrorString(exception, "MMenuButton.paintLabelOnButton()"));
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getColorOffset
	// ---------------------------------------------------------------------------
	
	/** getColorOffset() creates a Color object based on the baseColour of this
	  * object and the offset passed to this method.
	  *
	  */

	private Color getColorOffset(int offset)
	{
		int red = this.color3.getRed();
		int green = this.color3.getGreen();
		int blue = this.color3.getBlue();
		
		// Add the offset to each color
		red += offset;
		green += offset;
		blue += offset;
		
		if (!this.isColorInRange(red))
		{
			red = this.getClosestValidColor(red);
		}

		if (!this.isColorInRange(green))
		{
			green = this.getClosestValidColor(green);
		}

		if (!this.isColorInRange(blue))
		{
			blue = this.getClosestValidColor(blue);
		}

		return new Color(red, green, blue);
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: isColorInRange
	// ---------------------------------------------------------------------------
	
	/** isColorInRange() reports if a value of a color is in the 8-bit range of
	  * 0-255.
	  *
	  */
	
	private boolean isColorInRange(int color)
	{
		if ((color >= 0) && (color <= 255))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: getClosestValidColor
	// ---------------------------------------------------------------------------
	
	/** getClosestValidColor() returns the closest valid color value should a
	  * given color fall outside the 0-255 range.
	  *
	  */
	
	private int getClosestValidColor(int color)
	{
		if (color > 255)
		{
			return 255;
		}
		else if (color < 0)
		{
			return 0;
		}
		else
		{
			return color;
		}
	}


	// ---------------------------------------------------------------------------
	// METHOD: isLighterColor
	// ---------------------------------------------------------------------------
	
	/** isLighterColor() checks to see if a passed color is on the light side or
	  * not.
	  *
	  */
	
	private boolean isLighterColor(Color color)
	{
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		
		int colorSum = red + green + blue;
		
		if (colorSum > 480)
		{
			return true;
		}
		else
		{
			return false;
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
		super.fillParserValues();
		
		if (this.hasChildren())
		{
			if (this.isOpen())
			{
				parser.setVariable("extra", "opened");
			}
			else
			{
				parser.setVariable("extra", "closed");
			}
		}
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF