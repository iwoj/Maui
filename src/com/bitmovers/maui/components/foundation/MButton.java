// =============================================================================
// com.bitmovers.maui.components.foundation.MButton
// =============================================================================

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
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MButton                                (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>MButton</code> is one of the most commonly used Maui components. It represents 
  * a simple, clickable button. For <code>HTML</code> clients, the <code>MButton</code> class  
  * renders gif images to represent its various states (normal, mouse over, 
  * mouse down, disabled). For <code>WML</code> clients, the <code>MButton</code> component is just a 
  * text link wrapped in square brackets (e.g. [Cancel]).<p>
  *
  * When clicked, a button will publish a <code>MActionEvent</code> with 
  * the <code>MActionEvent.ACTION_CLICKED</code> event command string.
  * 
  */

public class MButton extends MComponent
	implements MouseOutPaintable,
						 MouseOverPaintable,
						 MouseDownPaintable,
						 DisabledPaintable,
						 HasLabel,
						 MLinkable
{
	// ----------------------------------------------------------------------
	
	// (!) IMPORTANT (!)
	// This grey1 colour should actually be set in the MFrame class, as it
	// is the background colour of the MFrame.
	//
	protected static final Color grey1 = new Color(204, 204, 204);
	private static final Color grey = new Color(153, 153, 153);

	protected static final Color yellow1 = new Color(255, 255, 153);
	protected static final Color yellow2 = new Color(255, 226, 102);
	protected static final Color yellow3 = new Color(255, 204, 0);
	protected static final Color yellow4 = new Color(204, 153, 0);
	protected static final Color orange1 = new Color(255, 102, 0);
	protected static final Color orange2 = new Color(204, 51, 0);
	
	private static final String base = "MButton";
	private static int nameCounter = 0;
	
	// Dynamic image generation helpers
	private static final int MINIMUM_BUTTON_WIDTH = 78;
	private static final int MINIMUM_MARGIN_WIDTH = 12;
	private static final int BUTTON_HEIGHT = 27;
	private static final int FONT_HEIGHT = 14;
	private static final int SPACE_WIDTH = 4;
	private static final int TEXT_Y_POSITION = 7;
	private static final int[] CHAR_WIDTHS =
	{
	// first 32 ASCII characters are non-visible
	  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // (arial)

	//   !  "  #  $  %  &  '  (  )  *  +  ,  -  .  /  0  1  2  3  4  5  6  7  8  9  :  ;  <  =  >  ?  @
	  4, 1, 3, 5, 5, 7, 7, 1, 3, 3, 3, 5, 1, 2, 1, 3, 5, 3, 5, 5, 6, 5, 5, 5, 5, 5, 1, 1, 4, 5, 4, 6, 9, // (arial)

	//A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R  S  T  U  V  W   X  Y  Z
		7, 6, 6, 6, 5, 5, 7, 6, 1, 4, 6, 5, 7, 6, 7, 5, 7, 6, 6, 5, 6, 7, 11, 6, 7, 6, // (arial)

	//[  \  ]  ^  _  `
	  2, 3, 2, 5, 6, 2, // (arial)

	//a  b  c  d  e  f  g  h  i  j  k  l  m   n  o  p  q  r  s  t  u  v  w   x  y  z
		5, 5, 5, 5, 5, 3, 5, 5, 1, 2, 4, 1, 7,  5, 5, 5, 5, 3, 5, 3, 5, 5, 9,  5, 5, 5, // (arial)

	//{  |  }  ~  
	  3, 1, 3, 5, 4 // (arial)
	};


	// ----------------------------------------------------------------------
  // Instance variables
	String label;
	
	private int myID;
	private Image basicButtonImage = null;
	private Image labelImage = null;
	private String link = null;
  private boolean isProminent = false;
    private StringBuffer encodeBuffer = new StringBuffer ();	


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	/** Constructs a new <code>MButton</code> object with the default label ("OK").
    *
    */

	public MButton()
	{
		this("OK");
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	/** Constructs a new <code>MButton</code> object with the specified label.
	  * 
	  * @param label A string to appear on the rendered button.
    * 
    */

	public MButton(String label)
	{
		this.myID = nameCounter++;
		this.name = base + myID;
		setLabel (label);
		//visible = false;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setName
	// ----------------------------------------------------------------------

	/** Overrides <code>Component.setName()</code>. This method should not be used by 
	  * application developers.
	  *
	  * @param newName The new button name.
	  * @invisible
	  * 
	  */

	public void setName(String newName)
	{
		this.invalidate();
		super.setName(newName + ".(" + base + myID + ")");
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getLabel
	// ----------------------------------------------------------------------
	
	/** Returns a string representing the text label shown on the button.
	  * 
	  */
	
	public String getLabel()
	{
		return this.label;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setLabel
	// ----------------------------------------------------------------------

	/** Sets the text label shown on the button.
	  * 
	  * @param newLabel The new button label.
	  * 
	  */

	public void setLabel(String newLabel)
	{
		this.invalidate();
		this.labelImage = null;
		this.basicButtonImage = null;
		ResourceManager theRM = ResourceManager.getInstance ();
		this.label = newLabel;
		theRM.addCrossReference (newLabel, this);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: doExiting
	// ----------------------------------------------------------------------

	/** Application is exiting.
	  * 
	  * @invisible
	  */

	public void doExiting ()
	{
		ResourceManager theRM = ResourceManager.getInstance ();
		theRM.removeCrossReference (this);
	}
  
  	private char getNibble (byte theChar, boolean aUpper)
  	{
  		char retVal;
  		
  		if (aUpper)
  		{
  			retVal = (char) ((theChar >> 4) & 0xff);
  		}
  		else
  		{
  			retVal = (char) (theChar & 0xff);
  		}
  		
  		return (char) (retVal < 10 ? '0' + retVal : retVal - 10 + 'A');
  	}
  		
	// ----------------------------------------------------------------------
	// METHOD: encode
	// ----------------------------------------------------------------------
	
	/** Encode the label. This replaces <code>URLEncoder</code> because it is so slow.
	  *
	  * @param aText The <code>String</code> to encode.
	  *
	  * @return The encoded text.
	  */
	private final String encode (String aText)
	{
		StringBuffer retVal = encodeBuffer;
		retVal.setLength (0);
		
		byte [] theBytes = aText.getBytes ();
		for (int i = 0; i < theBytes.length; i++)
		{
			if (theBytes [i] == (byte) ' ')
			{
				retVal.append ('+');
			}
			else if (theBytes [i] < '0' ||
					 (theBytes [i] > '9' && theBytes [i] < 'A') ||
					 (theBytes [i] > 'Z' && theBytes [i] < 'a') ||
					 theBytes [i] > 'z')
			{
				retVal.append ('%');
				retVal.append (getNibble (theBytes [i], false));
				retVal.append (getNibble (theBytes [i], true));
			}
		}
		return retVal.toString ();
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
		parser.setVariable("label", label);
		parser.setVariable("encodedLabel", URLEncoder.encode(label));
		parser.setVariable("labelID", getLabelID ());
		parser.setVariable("componentID", getComponentID());
		
		// Image Dimensions
		{
			Dimension size = getSize();
			parser.setVariable("width", Integer.toString(size.width));
			parser.setVariable("height", Integer.toString(size.height));
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getLabelID
	// ----------------------------------------------------------------------
	
	/** Returns <code>MButton</code>'s label string with all the spaces replaced by dash.
	  *
	  * @return Label text.
	  * @invisible
	  * 
	  */
	  
	protected String getLabelID ()
	{
		return label.replace (' ', '_');
	}
	
	
  // ----------------------------------------------------------------------
  // METHOD: createEvent
  // ----------------------------------------------------------------------
  
  /** Overrides Component.createEvent(). No state saving is necessary for the 
    * <code>MButton</code> component.
    *
    * @return A new <code>MActionEvent</code> with the <code>MActionEvent.ACTION_CLICKED</code>
    *         command string.
    * 
    * @invisible
    * 
    */

  public MauiEvent createEvent(String stateData)
  {
    return new MActionEvent(this, MActionEvent.ACTION_CLICKED);
  }


	// ----------------------------------------------------------------------
	// METHOD: getSize
	// ----------------------------------------------------------------------
	
	/** Returns the dimensions of the button. Subclasses may need to
	  * override this method to perform their own calculations.
	  *
	  * @return <code>Dimension</code> of the button width and height.
	  */
	
	public Dimension getSize()
	{
		int width;
		
		if ((width = (this.getLabelWidth() + (this.MINIMUM_MARGIN_WIDTH * 2))) < this.MINIMUM_BUTTON_WIDTH)
		{
			return new Dimension(this.MINIMUM_BUTTON_WIDTH, this.BUTTON_HEIGHT);
		}
		else
		{
			return new Dimension(width, this.BUTTON_HEIGHT);
		}
	}


	// ----------------------------------------------------------------------
	// METHOD: getLabelWidth
	// ----------------------------------------------------------------------
	
	/** Returns the number of pixels that the label itself will
	  * take up (width-wise). This includes a one-pixel gap between each character.
	  *
	  * @return int value of the button width.
	  */

	protected int getLabelWidth()
	{
		if (this.label.length() == 0)
		{
			return 0;
		}
		
		char[] characters = this.label.toCharArray();
		int width = 0;
		
		for (int i = 0; i < characters.length; i++)
		{
			width += this.getCharacterWidth(characters[i]);
			
			// Compensate for having one pixel between each letter
			if (!Character.isSpace(characters[i]))
			{
				width++;
			}
		}

		return width;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getCharacterWidth
	// ----------------------------------------------------------------------
	
	/** Returns the width of a given character. It makes use of
	  * some class tables which store these values.
	  *
	  * @return int value of a given character width.
	  */
	
	private int getCharacterWidth(char character)
	{
		try
		{
			return MButton.CHAR_WIDTHS[(int)character];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("MButton.getCharacterWidth(): Tried to get width for an unknown character. Only visible 7-bit ASCII characters are currently supported.");
			return 0;
		}
	}


	// ----------------------------------------------------------------------
	// METHOD: paint
	// ----------------------------------------------------------------------
	
	/** Implementation <code>Paintable</code> interface's <code>paint()</code> 
	  * method. It performs all the actions necessary to represent this button 
	  * visually in the given <code>Graphics</code> object.
	  *
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
		* @invisible
		* 
		*/
	
	public void paint(Graphics graphics)
	{
		this.paintMouseOut(graphics);	
	}


	// ----------------------------------------------------------------------
	// METHOD: paintBasicButton
	// ----------------------------------------------------------------------
	
	/** Paints a basic button <b>without</b> any text label.
		*
		* @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
		*/
	
	private synchronized void paintBasicButton(Graphics graphics)
	{
		if (this.basicButtonImage == null)
		{
			try
			{
				ResourceManager resourceManager = ResourceManager.getInstance();
				ImageFactory imageFactory = ImageFactory.getInstance();
				this.basicButtonImage = imageFactory.getImage(this.getSize());
				Graphics bbGraphics = this.basicButtonImage.getGraphics();

				byte[] cornerNW = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MButton/corner.nw.gif");
				byte[] cornerNE = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MButton/corner.ne.gif");
				byte[] cornerSW = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MButton/corner.sw.gif");
				byte[] cornerSE = resourceManager.getResourceBytes("com/bitmovers/maui/components/foundation/MButton/corner.se.gif");
				
				Image cornerNWImage = imageFactory.getImage(cornerNW);
				Image cornerNEImage = imageFactory.getImage(cornerNE);
				Image cornerSWImage = imageFactory.getImage(cornerSW);
				Image cornerSEImage = imageFactory.getImage(cornerSE);
				
				Dimension size = this.getSize();

				bbGraphics.drawImage(cornerNWImage, 2, 2, MComponent.DUMMY_COMPONENT);
				bbGraphics.drawImage(cornerNEImage, (size.width - 5), 2, MComponent.DUMMY_COMPONENT);
				bbGraphics.drawImage(cornerSWImage, 2, (size.height - 5), MComponent.DUMMY_COMPONENT);
				bbGraphics.drawImage(cornerSEImage, (size.width - 5), (size.height - 5), MComponent.DUMMY_COMPONENT);
				
				// Outer Border
				bbGraphics.setColor(MButton.grey);
				bbGraphics.drawLine(5, 2, (size.width - 6), 2);
				bbGraphics.drawLine(2, 5, 2, (size.height - 6));
				bbGraphics.setColor(Color.white);
				bbGraphics.drawLine(5, (size.height - 3), (size.width - 6), (size.height - 3));
				bbGraphics.drawLine((size.width - 3), 5, (size.width - 3), (size.height - 6));

				// Inner border
				bbGraphics.setColor(Color.black);
				bbGraphics.drawLine(5, 3, (size.width - 6), 3);
				bbGraphics.drawLine(3, 5, 3, (size.height - 6));
				bbGraphics.drawLine(5, (size.height - 4), (size.width - 6), (size.height - 4));
				bbGraphics.drawLine((size.width - 4), 5, (size.width - 4), (size.height - 6));
				bbGraphics.setColor(MButton.yellow1);
				bbGraphics.drawLine(4, 5, 4, (size.height - 6));
				bbGraphics.drawLine(5, 4, (size.width - 6), 4);
				bbGraphics.setColor(MButton.yellow4);
				bbGraphics.drawLine((size.width - 5), 5, (size.width - 5), (size.height - 6));
				bbGraphics.drawLine(5, (size.height - 5), (size.width - 6), (size.height - 5));

				// Fill
				bbGraphics.setColor(MButton.yellow3);
				bbGraphics.fillRect(5, 5, (size.width - 10), (size.height - 10));
				
				// Prominent border
				if (isProminent)
				{
				  bbGraphics.setColor(Color.black);
				  
          // Draw the border
				  Polygon poly = new Polygon();
				  {
				    poly.addPoint(3, 1);
				    poly.addPoint(1, 3);
				    poly.addPoint(1, (size.height - 4));
				    poly.addPoint(3, (size.height - 2));
				    poly.addPoint((size.width - 4), (size.height - 2));
				    poly.addPoint((size.width - 2), (size.height - 4));
				    poly.addPoint((size.width - 2), 3);
				    poly.addPoint((size.width - 4), 1);
				  }
				  bbGraphics.drawPolygon(poly);

          // Fill in the holes left by the above border
				  bbGraphics.drawLine(3, 2, 2, 3);
				  bbGraphics.drawLine(2, (size.height - 4), 3, (size.height - 3));
				  bbGraphics.drawLine((size.width - 4), (size.height - 3), (size.width - 3), (size.height - 4));
				  bbGraphics.drawLine((size.width - 3), 3, (size.width - 4), 2);
				  
          // Fill in the highlight border
				  bbGraphics.setColor(MButton.orange1);
				  bbGraphics.drawLine(5, 2, (size.width - 5), 2);
				  bbGraphics.drawLine(2, 4, 2, (size.height - 5));
				  bbGraphics.drawLine(2, 4, 4, 2);

          // Fill in the shadow border
				  bbGraphics.setColor(MButton.orange2);
				  bbGraphics.drawLine(4, (size.height - 3), (size.width - 6), (size.height - 3));
				  bbGraphics.drawLine((size.width - 3), 4, (size.width - 3), (size.height - 6));
				  bbGraphics.drawLine((size.width - 4), 3, (size.width - 3), 4);
				  bbGraphics.drawLine(3, (size.height - 4), 3, (size.height - 4));
				  bbGraphics.drawLine((size.width - 3), (size.height - 5), (size.width - 5), (size.height - 3));
				  
				  // Draw the outer highlight and shadow
				  bbGraphics.setColor(MButton.grey);
				  bbGraphics.drawLine(3, 0, (size.width - 4), 0);
				  bbGraphics.drawLine(3, 0, 0, 3);
				  bbGraphics.drawLine(0, 3, 0, (size.height - 4));
				  bbGraphics.drawLine((size.width - 1), 3, (size.width - 3), 1);
				  bbGraphics.drawLine(1, (size.height - 3), 3, (size.height - 1));
				  
				  bbGraphics.setColor(Color.white);
				  bbGraphics.drawLine(3, (size.height - 1), (size.width - 4), (size.height - 1));
				  bbGraphics.drawLine((size.width - 4), (size.height - 1), (size.width - 1), (size.height - 4));
				  bbGraphics.drawLine((size.width - 1), (size.height - 4), (size.width - 1), 3);
				}
			}
			catch (ResourceNotFoundException exception)
			{
				System.out.println(new ErrorString(exception, "MButton.paintBasicButton()"));
			}
		}

		graphics.drawImage(this.basicButtonImage, 0, 0, MComponent.DUMMY_COMPONENT);
	}


	// ----------------------------------------------------------------------
	// METHOD: paintLabelOnButton
	// ----------------------------------------------------------------------
	
	/** Paints the current label onto the button, using the
		* offsets specified.
		*
		* @param graphics A <code>Graphics</code> object into which this label will paint 
	  *                 itself.
	  * 
	  * @param offsetX X offset to paint the label on the button.
	  *
	  * @param offsetY Y offset to paint the label on the button.
	  */
	
	protected synchronized void paintLabelOnButton(Graphics graphics, int offsetX, int offsetY)
	{
		try
		{
			int labelWidth = this.getLabelWidth();
			Dimension size = this.getSize();

			switch (labelWidth)
			{
				case 0:
				{
					throw new Exception("An empty label was provided.");
				}
			}

			if (this.labelImage == null)
			{
				this.labelImage = ImageFactory.getInstance().getImage(new Dimension(labelWidth, MButton.FONT_HEIGHT));
				Graphics labelGraphics = this.labelImage.getGraphics();
				char[] characters = this.label.toCharArray();
				
				int x = 0;
				int y = 0;
				
				for (int i = 0; i < characters.length; i++)
				{
					if (!Character.isSpace(characters[i]))
					{
						try
						{
							byte[] characterBytes = ResourceManager.getInstance().getResourceBytes("com/bitmovers/maui/components/foundation/MButton/characters/" + (int)characters[i] + ".gif");
							Image characterImage = ImageFactory.getInstance().getInstance().getImage(characterBytes);
							
							labelGraphics.drawImage(characterImage, x, y, MComponent.DUMMY_COMPONENT);
							
							if (i + 1 != characters.length)
							{
								x += (this.getCharacterWidth(characters[i]) + 1);
							}
						}
						catch (ResourceNotFoundException exception)
						{
							System.out.println(new ErrorString(exception, "MButton.paintLabelOnButton()"));
						}
					}
					else
					{
						if (i + 1 != characters.length)
						{
							x += MButton.SPACE_WIDTH;
						}
					}
				}
			}

			// Paint the labelImage onto the passed Graphics object
			int x = ((size.width - this.getLabelWidth()) / 2) + offsetX;
			int y = this.getTextYPosition() + offsetY;
			
			graphics.drawImage(this.labelImage, x, y, MComponent.DUMMY_COMPONENT);
		}
		catch (Exception exception)
		{
			// This is most likely caused by a zero-length label... In such a case,
			// we just won't draw anything...
			System.err.println(new WarningString("MButton.paintLabelOnButton(): " + exception.getMessage()));
		}
	}


	// ----------------------------------------------------------------------
	// METHOD: paintMouseOut
	// ----------------------------------------------------------------------
	
	/** Paints the button's mouse out state into the given graphics
	  * context.
	  * 
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
	  * @invisible
	  * 
	  */
	  
	public void paintMouseOut(Graphics graphics)
	{
		this.paintBasicButton(graphics);
		this.paintLabelOnButton(graphics, 0, 0);
	}


	// ----------------------------------------------------------------------
	// METHOD: paintDisabled
	// ----------------------------------------------------------------------
	
	/** Paints the button's disabled state into the given graphics
	  * context.
	  * 
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
	  * @invisible
	  * 
	  */
	  
	public void paintDisabled(Graphics graphics)
	{
		this.paintMouseOut(graphics);
		Dimension size = this.getSize();
		
		for (int i = 1; i <= size.height; i++)
		{
		  for (int j = 1; j <= size.width; j += 2)
		  {
		    graphics.setColor(this.grey1);

        if ((i % 2) == 0)
        {
		      graphics.drawLine(j + 1, i, j + 1, i);
		    }
		    else
		    {
		      graphics.drawLine(j, i, j, i);
		    }
		  }
		}
	}


	// ----------------------------------------------------------------------
	// METHOD: paintDisabled
	// ----------------------------------------------------------------------
	
	/** Paints the button's disabled state into the given graphics
	  * context.
	  * 
	  * @param minusNumberOfRows The number of rows from the bottom to
	  *                          subtract from painting. 
	  *
	  * @invisible
	  * 
	  */
	  
	public void paintDisabled(Graphics graphics, int minusNumberOfRows)
	{
		paintMouseOut(graphics);
		Dimension size = this.getSize();
		
		for (int i = 1; i <= size.height - minusNumberOfRows; i++)
		{
		  for (int j = 1; j <= size.width; j += 2)
		  {
		    graphics.setColor(this.grey1);

        if ((i % 2) == 0)
        {
		      graphics.drawLine(j + 1, i, j + 1, i);
		    }
		    else
		    {
		      graphics.drawLine(j, i, j, i);
		    }
		  }
		}
	}


	// ----------------------------------------------------------------------
	// METHOD: paintMouseOver
	// ----------------------------------------------------------------------

	/** Paints the button's mouse over state into the given graphics
	  * context.
	  * 
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
	  * @invisible
	  * 
	  */
	
	public void paintMouseOver(Graphics graphics)
	{
		this.paintBasicButton(graphics);
		Dimension size = this.getSize();
		graphics.setColor(MButton.yellow2);
		graphics.fillRect(5, 5, (size.width - 10), (size.height - 10));
		this.paintLabelOnButton(graphics, 0, 0);
	}


	// ----------------------------------------------------------------------
	// METHOD: paintMouseDown
	// ----------------------------------------------------------------------

	/** Paints the button's mouse down state into the given graphics
	  * context.
	  * 
	  * @param graphics A <code>Graphics</code> object into which this button will paint 
	  *                 itself.
	  * @invisible
	  * 
	  */
	
	public void paintMouseDown(Graphics graphics)
	{
		this.paintBasicButton(graphics);
		this.paintLabelOnButton(graphics, 1, 1);

		Dimension size = this.getSize();
		graphics.setColor(MButton.yellow4);
		graphics.drawLine(4, 5, 4, (size.height - 6));
		graphics.drawLine(5, 4, (size.width - 6), 4);
		graphics.setColor(MButton.yellow3);
		graphics.drawLine((size.width - 5), 5, (size.width - 5), (size.height - 6));
		graphics.drawLine(5, (size.height - 5), (size.width - 6), (size.height - 5));
	}


	// ----------------------------------------------------------------------
	// METHOD: getTextYPosition
	// ----------------------------------------------------------------------
	
	/** Returns the co-ordinate on the y-axis of the upper-left
	  * hand corner where text is painted.
	  *
	  */
	
	protected int getTextYPosition()
	{
		return this.TEXT_Y_POSITION;
	}


	// ----------------------------------------------------------------------
	// METHOD: setLink
	// ----------------------------------------------------------------------
	
	/** Sets a link for this button.
	  *
	  * @param aLink The link to set. Value <code>null</code> means clear the link.
	  */

	public void setLink (String aLink)
	{
	  link = aLink;
	}


	// ----------------------------------------------------------------------
	// METHOD: getLink
	// ----------------------------------------------------------------------

	/** Gets the link for this button.
		*
		* @return The link value for this button.
		*/

	public String getLink ()
	{
		return link;
	}


	// ----------------------------------------------------------------------
	// METHOD: setProminent
	// ----------------------------------------------------------------------

	/** Sets whether or not this button is prominent. A prominent button is
	  * drawn with an extra border around it so that it stands out. This is
	  * the equivalent to the "default" button in most operating systems --
	  * the one that will be activated if you hit "Enter", it will be used.
	  *
	  * Note that this method will be deprecated.
	  * 
	  * @invisible
	  *
	  */

	public void setProminent(boolean prominent)
	{
		this.isProminent = prominent;
	}
	

}

// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF