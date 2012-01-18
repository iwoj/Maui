package com.bitmovers.maui.engine.resourcemanager;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import Acme.JPM.Encoders.*;


// ========================================================================
// SINGLETON CLASS: ImageFactory
// ========================================================================

/** The Image Factory "receives", "ships", and "packages" Image objects.
  * As specified in the document, Maui 0.8: Image Rendering Component,
  * blank Image canvases are provided by the "shipping" portion of the
  * Image Factory, and rendered Images are given to the "receiving"
  * portion. These rendered Images are taken down the assembly line to
  * the "packaging" portion. The packager outputs an Image to a
  * specified format (Maui currently only supports GIF87a).
  *
  */

public class ImageFactory
{
	
	
  public static final byte GIF = 0;
  public static final byte JPEG = 1;
	
	public static boolean nativeGraphics = false;
	private static Toolkit toolkit = null;
	
  // Use a static initialiser to start up this class
  private static ImageFactory thisInstance = new ImageFactory();
  
  private final String NO_AWT_ERROR = "No native graphics system was detected. Please install the Pure Java AWT Toolkit found at <http://www.eteks.com/pja/en/#Download>. Add it to your CLASSPATH and restart Maui. Maui will exit now...";
  private EmptyImageCanvasFacade canvasFacade = new EmptyImageCanvasFacade();
  

  // ----------------------------------------------------------------------
  // CONSTRUCTOR: ImageFactory
  // ----------------------------------------------------------------------
	
  /** This constructor is private and shall never be called by anyone
    * but the static initialiser.
    *
    */

  private ImageFactory()
  {
		
  }


  // --------------------------------------------------------------------
  // METHOD: getInstance
  // --------------------------------------------------------------------
  
  /** getInstance() gets an instance of the ImageFactory.
    *
    */
  
  public static ImageFactory getInstance()
  {
    return ImageFactory.thisInstance;
  }


  // --------------------------------------------------------------------
  // METHOD: getImage
  // --------------------------------------------------------------------
  
  /** getImage() gets an Image object of the specified Dimension. If an
    * invalid Dimension is passed (ie. null, negative, or 0), a null
    * will be returned.
    *
    */
  
  public Image getImage(Dimension dimension)
  {
    // [1] Verify the validity of the dimensions
    if (dimension == null)
    {
      return null;
    }
    else if ((dimension.width <= 0) || (dimension.height <= 0))
    {
      return null;
    }
    
    // [2] Get an Image of the specified size from the faade
    return this.canvasFacade.getImage(dimension);
  }


	// --------------------------------------------------------------------
	// METHOD: getImage
	// --------------------------------------------------------------------
	
	/** getImage() returns an Image object based on a byte[]
	  *
	  */
  
	public Image getImage(byte[] bytes)
	{
		Image image = null;
		
		// [1] First try and use the default toolkit to get an Image
		if (ImageFactory.nativeGraphics)
		{
			try
			{
				Toolkit theToolkit = java.awt.Toolkit.getDefaultToolkit();
				image = theToolkit.createImage(bytes);
				final Object theSynchObject = new Object();
				synchronized (theSynchObject)
				{
					theToolkit.prepareImage(image, -1, -1, new ImageObserver()
					{
						public boolean imageUpdate (Image aImage, int aInfoFlags, int aX, int aY, int aWidth, int aHeight)
						{
							synchronized (theSynchObject)
							{
								if ((aInfoFlags & ImageObserver.ALLBITS) != 0)
								{
									theSynchObject.notify();
									return false;
								}
								return true;
							}
						}
					});
					theSynchObject.wait();
				}
			}
			catch (Throwable exception)
			{
				if (!(exception instanceof ClassNotFoundException) && !(exception instanceof NoClassDefFoundError))
				{
					System.err.println(new ErrorString(exception, "ImageFactory.getImage()"));
				}
			}
		}
		// [2] Else, try to use the Pure Java AWT Toolkit (http://www.eteks.com/)
		else
		{
			if (toolkit == null)
			{
				try
				{
					toolkit = (com.eteks.awt.PJAToolkit) Class.forName ("com.eteks.awt.PJAToolkit").newInstance ();
					//Class pjaToolkitClass = Class.forName("com.eteks.awt.PJAToolkit");
					
					//image = ((com.eteks.awt.PJAToolkit)pjaToolkitClass.getConstructor(new Class[] { }).newInstance(new Object[] { })).createImage(bytes, 0, bytes.length);
				}
				catch (Throwable exception)
				{
					if ((exception instanceof ClassNotFoundException) || (exception instanceof NoClassDefFoundError))
					{
						// Output the error both to the log file and to the standard System.err
						System.err.println(this.NO_AWT_ERROR);
						LogManager.resetSystemStreams();
						System.err.println(this.NO_AWT_ERROR);

						// Sleep for a second to make sure things get output before quitting
						try
						{
							Thread.sleep(2000);
						}
						catch (InterruptedException iException) { }

						// Quit
						System.exit(1);
					}
					else
					{
						System.err.println(new ErrorString(exception, "ImageFactory.getImage(byte[])"));
					}
				}
			}
			image = toolkit.createImage (bytes, 0, bytes.length);
		}
		
		return image;
	}

  
  // --------------------------------------------------------------------
  // METHOD: getOutputImage
  // --------------------------------------------------------------------
  
  public byte[] getOutputImage(Image image, byte imageType)
  {
    byte[] bytes = null;

    try
    {
      ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
      
      // [1] Load an encoded image into a ByteArrayOutputStream
      new GifEncoder(image, bytesOut, true).encode();
      
      // [2] Return a BufferedInputStream based on the encoded data
      bytes = bytesOut.toByteArray();
    }
    catch (Exception exception)
    {
      System.out.println(new ErrorString(exception, "ImageFactory.getOutputImage()"));
    }
    
    return bytes;
  }


	// ====================================================================
	// INNER CLASS: EmptyImageCanvasFacade
	// ====================================================================

	/** The EmptyImageCanvasFacade acts as a faade between the
	  * ImageFactory class and the mechanism which provides Image objects.
	  *
	  * @author Patrick Gibson (patrick@bitmovers.com)
	  *
	  */

	private class EmptyImageCanvasFacade
	{
	  // ------------------------------------------------------------------
	  // CONSTRUCTOR: EmptyImageCanvasFacade
	  // ------------------------------------------------------------------

	  private EmptyImageCanvasFacade()
	  {
      // Nothing to do just yet...
	  }


		// ------------------------------------------------------------------
		// METHOD: getImage
		// ------------------------------------------------------------------

		/** getImage() gets an Image object of the specified Dimension.
		  *
		  */

		public Image getImage(Dimension dimension)
		{
			Image image = null;
			
			// [1] First try and use the default toolkit to get an Image
			if (ImageFactory.nativeGraphics)
			{
				try
				{
					image = MauiRuntimeWindow.getInstance().window.createImage(dimension.width, dimension.height);
				}
				catch (Throwable exception)
				{
					System.err.println("ImageFactory.nativeGraphics: " + ImageFactory.nativeGraphics);
				}
			}
			// [2] Else, try to use the Pure Java AWT Toolkit (http://www.eteks.com/)
			else
			{
				try
				{
					Class pjaImageClass = Class.forName("com.eteks.awt.PJAImage");
					
					image = (Image)pjaImageClass.getConstructor(new Class[] { int.class, int.class }).newInstance(new Object[] { new Integer(dimension.width), new Integer(dimension.height) });
				}
				catch (Throwable exception)
				{
					if (exception instanceof ClassNotFoundException)
					{
						// Output the error both to the log file and to the standard System.err
						System.err.println(ImageFactory.this.NO_AWT_ERROR);
						LogManager.resetSystemStreams();
						System.err.println(ImageFactory.this.NO_AWT_ERROR);

						// Sleep for a second to make sure things get output before quitting
						try
						{
							Thread.sleep(2000);
						}
						catch (InterruptedException iException) { }

						// Quit
						System.exit(1);
					}
					else
					{
						System.err.println(new ErrorString(exception, "ImageFactory.getImage(Dimension)"));
					}
				}
			}
			
			return image;
		}
	}
	
	
}


// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF