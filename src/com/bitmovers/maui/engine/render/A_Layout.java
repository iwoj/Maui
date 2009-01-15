package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.utilities.StringParser;

// ========================================================================
// CLASS: A_Layout
// ========================================================================

/** This class handles some of the general rendering functions of layout 
  * managers.
  * 
  */
  
public abstract class A_Layout extends A_Renderer
{
	protected MContainer parent;
	protected String separator = "";
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	protected A_Layout ()
	{
		super ();
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Simple constructor.
	  *
	  * @param aRenderable           Reference to the I_Renderable component 
	  *                              (This will be the layout manager).
	  * 
	  * @param aParent               The MContainer which is utilizing the 
	  *                              layout manager.
	  * 
	  * @param aClientClassification String array which describes the client.
	  *                              This is used to locate a renderer.
	  * 
	  * @param aHtml                 Boolean indicating if html or wml is 
	  *                              being used (in case the client 
	  *                              classification isn't complete)
	  *						         This is a bit of a kludge, and should 
	  *                              be removed at some point.
	  * 
	  */
	  
	protected A_Layout (I_Renderable aRenderable,
						MContainer aParent,
						String [] aClientClassification,
						boolean aHtml)
	{
		super (aRenderable, aClientClassification);
		parent = aParent;
		html = aHtml;
	}
	
	/**
	* Render all of the components contained within the parent MContainer object
	*
	* @param aRenderable The layout manager
	*
	* @return The rendered string
	*/
	protected String renderComponents (I_Renderable aRenderable)
	{
		return renderComponents (parent, separator);
	}
	
	/**
	* Start the render operation.
	*
	* @param aRenderable The I_Renderable object (the layout manager)
	*
	* @return The fully rendered String for the parent container
	*/
	public String render (I_Renderable aRenderable)
	{
		String retVal = null;
		if (renderTemplate != null)
		{		
			aRenderable.fillParserValues();
			StringParser theParser = aRenderable.getParser();
			setupAlignment (aRenderable, theParser);
			if (this instanceof I_Generator)
			{
				generate (aRenderable, theParser);
			}
			else
			{
				theParser.setVariable ("components",
									   theParser.parseString (renderComponents (aRenderable)));
				
			}
			retVal = theParser.parseString (renderTemplate [0]);
		}
		return (retVal == null ? "" : retVal);
	}
	
	/**
	* Once-only initialization when the renderer is created
	*
	* @param aRenderable the I_Renderable object (layout manager)
	* @param aParent The container object
	* @param aClientClassification The String array describing the client
	*/
	public void initialize (I_Renderable aRenderable,
							MComponent aParent,
							String [] aClientClassification)
	{
		parent = (MContainer) aParent;
		super.initialize (aRenderable, aParent, aClientClassification);
	}
	
	/**
	* Setup the alignment.  This is an abstract method, and must be implemented by a subclass.
	*
	* @param aRenderable The renderable object (layout manager)
	* @param aStringParser The StringParser for for this layout manager
	*/
	protected abstract void setupAlignment (I_Renderable aRenderable,
											StringParser aStringParser);
	
}