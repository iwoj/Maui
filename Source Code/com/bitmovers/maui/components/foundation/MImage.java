// =============================================================================
// com.bitmovers.maui.components.foundation.MImage
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.*;
import java.awt.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;


// ========================================================================
// CLASS: MImage                                 (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is a renderable image component. It can display a 
  * stored image from a given resource location. The image resource may 
  * exist in any application Jar archive, but be aware that the internal 
  * directory structures of all Jar resources are merged into a single 
  * tree. Use a package naming style to place your images in a location 
  * which is unlikely to conflict with other applications
  * (e.g. "/com/yourcompany/imagelibrary/my_photo.gif"). This limitation
  * will be solved in a future version of Maui.
  *
  */
  
public class MImage extends MComponent
{
	
	
	private static final String base = "MImage";
	
	private Image image = null;
	private Dimension size = null;
	private String imagePath = null;
	private String description = null;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	/** Constructs an <code>MImage</code> object which will contain an image specified by the
	  * passed path (e.g.. /icons/Email.gif). The path must point to a resource
	  * contained within one of the application JAR files (see the <code>ResourceManager</code>
	  * for more information).
    * 
    * @param imagePath The path of the image within the jar file.
    *
    * @param width 		 The width of the image in pixels.
    *
    * @param height 	 The height of the image in pixels.
    *
    * @exception ResourceNotFoundException Gets thrown if the image is not found. Check the
    *            path of the image, and make sure the image is contained within the jar file.
    *
    */

	public MImage(String imagePath, int width, int height) throws ResourceNotFoundException
	{
		this(imagePath, width, height, null);
	}


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	/** Constructs an <code>MImage</code> object which will contain an image specified by the
	  * passed path (e.g. /icons/Email.gif). The path must point to a resource
	  * contained within one of the application JAR files (see the <code>ResourceManager</code>
	  * for more information). The description passed will be used to describe the
	  * image wherever possible (in <code>HTML</code>, it will be used for the alt tag; in 
	  * <code>WML</code>, this description will be displayed instead of the image).
    * 
    */

	public MImage(String imagePath, int width, int height, String description) throws ResourceNotFoundException
	{
		this.imagePath = imagePath;
		this.description = description;
		this.size = new Dimension(width, height);
		
		try
		{
			ResourceManager resourceManager = ResourceManager.getInstance();
			ImageFactory imageFactory = ImageFactory.getInstance();
		
			byte[] imageBytes = resourceManager.getResourceBytes(this.imagePath);
				
			this.image = imageFactory.getImage(imageBytes);

			/* Doesn't seem to work reliably with some images
			int width = this.image.getWidth(MComponent.DUMMY_COMPONENT);
			int height = this.image.getHeight(MComponent.DUMMY_COMPONENT);

			this.size = new Dimension(width, height);
			*/
		}
		catch (ResourceNotFoundException exception)
		{
			throw exception;
		}
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getDescription
	// ----------------------------------------------------------------------
	
	/** Returns the description of the MImage if it exists, else a <code>null</code>
		* is returned.
		*
		* @return The image tag string.
	  *
	  */
	
	public String getDescription()
	{
		return ((this.description != null) ? this.description : "");
	}


	// ----------------------------------------------------------------------
	// METHOD: setDescription
	// ----------------------------------------------------------------------
	
	/** Sets the description of the MImage.
		*
		* @param description The string to set as the image tag.
	  *
	  */
	
	public void setDescription(String description)
	{
		this.description = description;
	}

	
	// ----------------------------------------------------------------------
	// METHOD: getSize
	// ----------------------------------------------------------------------
	
	/** Returns the dimensions of the button. Subclasses may need to
	  * override this method to perform their own calculations.
	  *
	  * @return The width and height of the image.
	  */
	
	public Dimension getSize()
	{
		return this.size;
	}


	// ----------------------------------------------------------------------
	// METHOD: getImagePath
	// ----------------------------------------------------------------------
	
	/** Returns the path to the image specified upon construction.
	  *
	  * @return The string representing the path of the image.
	  */
	
	public String getImagePath()
	{
		return this.imagePath;
	}


	// ----------------------------------------------------------------------
	// METHOD: getImage
	// ----------------------------------------------------------------------
	
	/** Returns an <code>Image</code> object which represents this <code>MImage</code>. 
		* (Intended for use by other components which can accept an <code>MImage</code>, 
		* e.g. <code>MButton</code>.)
	  *
	  */
	
	protected Image getImage()
	{
		return this.getImage();
	}

	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF