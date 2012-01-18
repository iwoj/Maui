// =============================================================================
// com.bitmovers.maui.engine.render.A_Renderer
// =============================================================================

// =============================================================================
// CHANGELOG:
//++ 363 SL 2001.08.13
// If MTable is created by string rather than MComponents, then check to see
// if theContainers[i] != null before getting the value for thePhase to catch
// the nullpointer error for WML
// =============================================================================
package com.bitmovers.maui.engine.render;

import java.util.Vector;
import java.util.HashMap;
import java.util.Stack;

import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.HasLabel;
import com.bitmovers.maui.components.foundation.Settable;
import com.bitmovers.maui.components.foundation.MSettable;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.foundation.MFrame;
import com.bitmovers.maui.components.foundation.HasPostValue;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.engine.httpserver.HTTPSession;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.events.MActionEvent;


// ========================================================================
// CLASS: A_Renderer
// ========================================================================
// 
// I think that the rendering system passes around too many String
// objects. Perhaps we should make use of streams? - Ian [2001.08.28]
// 

/** This object provides a lot of common or basic functionality for many 
  * renderers.  It also implements some of the general render patterns.
  * 
  */
  
public abstract class A_Renderer implements I_Renderer
{
	
	
	protected String [] renderTemplate = new String [10];
	protected int renderTemplateCount = 0;
	protected String [] renderPhaseNames = null;
	protected HashMap renderPhaseMap = null;
	protected static int uniqueReference = (int) (System.currentTimeMillis () & 0x7fffffff);
	
	protected boolean html = true;
	protected HasPostValue [] valuePosters = null;
	protected String [] clientClassification;
	protected I_Renderable renderable;
	protected I_Renderable representativeRenderable;
	protected I_Renderable containingFrame = null;
	protected static int mainCounter = 0;
	protected static int postCardCounter = 0;
	protected static int currentMain = 0;
	protected static int currentPostCard = 0;
	protected StringBuffer generatedPseudo = null;
	protected boolean isUp = false;
	protected boolean isNokia = false;
	protected boolean useBackKey = true;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	protected A_Renderer ()
	{
		// Do nothing.
	}
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** Simple constructor.
	  *
	  * @param  aRenderable  The I_Renderable object (typically an 
	  *                      MComponent, or subclass, or a layout manager).
	  * 
	  * @param  aClientClassification  A String array describing the client.
	  * 
	  */
	  
	protected A_Renderer (I_Renderable aRenderable, String [] aClientClassification)
	{
		initialize (aRenderable, null, aClientClassification);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: initialize
	// ----------------------------------------------------------------------
	
	/** Initialize the renderer.
	  *
	  * @param  aRenderable  The I_Renderable object (typically an 
	  *                      MComponent, or subclass, or a layout manager).
	  * @param  aComponent  The MComponent associated with I_Renderable (if 
	  *                     the I_Renderable is different).
	  * @param  aClientClassification  A String array describing the client.
	  * 
	  */
	  
	public void initialize (I_Renderable aRenderable, MComponent aComponent, String [] aClientClassification)
	{
		renderable = aRenderable;
		clientClassification = aClientClassification;
		
		reloadTemplates();
		
		html = (aClientClassification [0].equals ("html"));
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: reloadTemplates
	// ----------------------------------------------------------------------
	//
	// This method does not currently attempt to load superclass templates.
	// 
	// e.g. If my FunkyButton renderer in not accompanied by a 
	//      FunkyButton.html template, this method should attempt to load all
	//      appropriate superclass templates, such as MButton.html.
	//
	// - Ian [2001.08.29]
	//
	
	/** This method loads the <code>renderTemplate<code> String array with 
	  * all templates applicable to the specified renderable object. The 
	  * appropriate templates are loaded based on the contents of the 
	  * <code>clientClassification</code> String array.
	  * 
	  */
	  
	protected void reloadTemplates()
	{
		//
		//	Look for resources for this renderable object
		//
		ResourceManager theResourceManager = ResourceManager.getInstance ();
		String theNameBase;
		boolean theUseNameBase = true;
		
		if (this instanceof I_Generator)
		{
			//
			//	Use the generator template rather than the renderable template
			//
			theNameBase = getGeneratorClassName ();
			theUseNameBase = false;
		}
		else
		{
			//
			//	If the object is a MauiApplication then use the MauiApplication class name rather than the
			//	I_Renderable's class name
			//
			theNameBase = (renderable instanceof MauiApplication ?
									"com.bitmovers.maui.MauiApplication" :
									getResourceClassName (renderable));
		}
		
		//
		//	Fix up the name, and look in the resource file for resources with the appropriate name
		//
		int thePeriod = theNameBase.lastIndexOf (".");
		String thePackageBase = theNameBase.substring (0, thePeriod).replace ('.', '/');
		theNameBase = theNameBase.substring (theNameBase.lastIndexOf (".") + 1);
		String [] theTemplateTypes = getTemplateTypes ();
		
		//
		// Iterate through template types (e.g. "off", "selected", "")
		//
		TEMPLATE_TYPE_LOOP: for (int i = 0; i < theTemplateTypes.length; i++)
		{
			//
			// Walk through the client classification array and look for templates starting at the most 
			// detailed descriptions, (e.g. "MButton.off.netscape.4.html") and descending to the least 
			// detailed (e.g. "MButton.off.html").
			// 
			CLASSIFICATION_LOOP: for (int j = clientClassification.length - 1; j >= 0; j--)
			{
				StringBuffer resourceName;
				byte [] theRenderTemplate;
				
				if (theTemplateTypes[i].equals(""))
				{
					resourceName = new StringBuffer(thePackageBase + "/" +
														(theUseNameBase ? theNameBase + "/" : "") +
														theNameBase + ".");
				}
				else
				{
					resourceName = new StringBuffer(thePackageBase + "/" +
														(theUseNameBase ? theNameBase + "/" : "") +
														theNameBase + "." + theTemplateTypes [i] + ".");
				}
				
				try
				{
					
					//
					// Append the client classification string in the format
					// [<client_type>[.<version>[.<other_stuff>]]].<data_format>
					// 
					// e.g. "up.3.wml" or "html"
					//
					for (int k = 1; k <= j; k++)
					{
						if (clientClassification[k] == null)
						{
							continue CLASSIFICATION_LOOP;
						}
						resourceName.append(clientClassification[k]);
						resourceName.append(".");
					} 
					resourceName.append(clientClassification[0]);
					
					//
					// Cross your fingers here.
					//
					theRenderTemplate = theResourceManager.getResource (resourceName.toString());
					System.out.println(new DebugString("[A_Renderer] Loaded: " + resourceName.toString()));
					
					renderTemplate [renderTemplateCount++] = (theRenderTemplate != null ?
					                                          new String (theRenderTemplate) :
					                                          new String ());
					
					//
					// At this point, we have sucessfully loaded the most appropriate template
					// for this type. So move on to the next template type.
					//
					continue TEMPLATE_TYPE_LOOP;
				}
				//
				// Don't worry, just keep looking.
				//
				catch (ResourceNotFoundException e)
				{
					System.out.println(new DebugString("[A_Renderer] Skipped: " + resourceName.toString() + " (Does not exist.)"));
				}
			}
			
			// Uh oh.
			if (renderTemplate.length == 0)
			{
				System.out.println(new WarningString("Could not load any templates for " + theNameBase));
			}
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setHtml
	// ----------------------------------------------------------------------
	
	/** Set the boolean indicating if this html or not
	  *
	  * @param aHtml The html boolean
	  * 
	  */
	  
	public void setHtml(boolean aHtml)
	{
		html = aHtml;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getTemplateTypes
	// ----------------------------------------------------------------------
	
	/** Get the template types.  This is used for determining the naming 
	  * scheme to use in searching for the resources.
	  *
	  * @return The String array of template types
	  * 
	  */
	  
	protected String[] getTemplateTypes()
	{
		return new String [] {""};
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getResourceClassName
	// ----------------------------------------------------------------------
	
	protected String getResourceClassName(I_Renderable aRenderable)
	{
		Class theRenderableClass = aRenderable.getClass ();
		String retVal;
		while (!(retVal = theRenderableClass.getName ()).startsWith ("com.bitmovers.maui.components") &&
				!retVal.startsWith ("com.bitmovers.maui.layouts"))
		{
			theRenderableClass = theRenderableClass.getSuperclass ();
		}
		return retVal;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getGeneratorClassName
	// ----------------------------------------------------------------------
	
	protected String getGeneratorClassName()
	{
		return getClass ().getName ();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: doRenderComponent
	// ----------------------------------------------------------------------
	
	protected String doRenderComponent (MComponent aComponent)
	{
		/* This code implemented backwards compatibility with the first
		   version of the rendering system. Legacy rendering as been 
		   removed from the components, so this code is no longer necessary. 
		   -- ian (2001.05.14) */

		/*
		return (aComponent instanceof I_Renderable ?
					((I_Renderable) aComponent).render () :
					(html ? aComponent.renderHTML () :
							 aComponent.renderWML ()));
		*/

			return aComponent.render();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: renderComponents
	// ----------------------------------------------------------------------
	
	protected String renderComponents (MContainer aParent, String aSeparator)
	{
		StringBuffer retVal = new StringBuffer();
		
		MComponent[] theComponents = getComponents (aParent);
		I_Renderer theRenderer = null;

	  	if (theComponents.length == 1 &&
			theComponents [0] instanceof MContainer &&
			(theRenderer = theComponents [0].getRenderer ()) instanceof I_HasDepth)
		{
			retVal.append (((I_HasDepth) theRenderer).depthBasedRender ((I_Renderable) theComponents [0],
																					null,
																					""));
		}
	  	else if (theComponents.length > 0) 
	  	{
	    	for (int i = 0; i < theComponents.length - 1; i++)
	    	{
	    		retVal.append(renderSeparatedComponent(theComponents[i], aSeparator));
	    	}
	    	retVal.append(doRenderComponent(theComponents[theComponents.length - 1]));
	  	}
	  	
	  	return retVal.toString();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: renderSeparatedComponent
	// ----------------------------------------------------------------------
	
	protected String renderSeparatedComponent(MComponent aComponent, String aSeparator)
	{
		String retVal = doRenderComponent (aComponent);
		return (retVal.trim ().length () == 0 ?
					retVal :
					retVal + aSeparator);
	}
	
	protected HasPostValue [] getValuePosters (I_Renderable aRenderable)
	{
		if (valuePosters == null)
		{
			Vector thePosters = new Vector ();
			MComponent.getContainedHasPostValueComponents(thePosters, getMauiApplication (aRenderable));
			valuePosters = new HasPostValue [thePosters.size ()];
			for (int i = 0; i < valuePosters.length; i++)
			{
				valuePosters [i] = (HasPostValue) thePosters.elementAt (i);
			}
		}
		return valuePosters;
	}
	
	protected String setVar (I_Renderable aRenderable)
	{
		return setVar (aRenderable, getValue (aRenderable));
	}
	
	protected String setVar (I_Renderable aRenderable, String aValue)
	{
		return setVar (((MComponent) aRenderable).getWMLSafeComponentID (), aValue);
	}
	
	protected String setVar (String aId, String aValue)
	{
		StringBuffer retVal = new StringBuffer ("<setvar name=\"");
		retVal.append (aId);
		retVal.append ("\" value=\"");
		retVal.append (aValue);
		retVal.append ("\"/>\n");
		return retVal.toString ();
	}
	
	private void addInterfaces (MComponent aComponent, HashMap aPhaseMap, String [] aInterfaces)
	{
		for (int i = 0; i < aInterfaces.length; i++)
		{
			if (aInterfaces [i] != null)
			{
				HashMap theComponentPhase = (HashMap) aPhaseMap.get (aInterfaces [i]);
				if (theComponentPhase == null)
				{
					theComponentPhase = new HashMap ();
					aPhaseMap.put (aInterfaces [i], theComponentPhase);
				}
				
				theComponentPhase.put (aComponent.getComponentID (), aComponent);
			}
		}
	}
	
	private void addComponentToPhases (MComponent aComponent, HashMap aPhaseMap)
	{
		if (aComponent != null)
		{
	  		I_Renderer theRenderer = aComponent.getRenderer ();
	  		String [] theInterfaces;
	  		if (theRenderer != null)
	  		{
				theInterfaces = theRenderer.getRenderPhases ();
				addInterfaces (aComponent, aPhaseMap, theInterfaces);
			}
			theInterfaces = A_Renderer.getRenderPhases (aComponent.getClass ());
			addInterfaces (aComponent, aPhaseMap, theInterfaces);
		}
	}
	
	protected MComponent [] getComponentsForPhase (String aPhaseClass, HashMap aPhaseMap)
	{
		MComponent [] retVal = null;
		
		HashMap theComponentPhase = (HashMap) aPhaseMap.get (aPhaseClass);
		if (theComponentPhase != null)
		{
			Object [] theComponents = theComponentPhase.values ().toArray ();
			retVal = new MComponent [theComponents.length];
			for (int i = 0; i < retVal.length; i++)
			{
				retVal [i] = (MComponent) theComponents [i];
			}
		}
		return (retVal == null ? new MComponent [0] : retVal);
	}
  
  
	protected void clean (MContainer aContainer)
	{
		HashMap theRenderPhaseMap = null;
		
		boolean theDirty = aContainer.isDirty ();
		if (theDirty)
		{
			theRenderPhaseMap = new HashMap ();
			renderPhaseMap.put (aContainer, theRenderPhaseMap);
		}
		else
		{
			theRenderPhaseMap = (HashMap) renderPhaseMap.get (aContainer);
		}
		
		I_Renderer theRenderer = aContainer.getRenderer ();
		if (theDirty && aContainer instanceof MFrame)
		{
			addComponentToPhases (aContainer, theRenderPhaseMap);
		}
		MComponent [] theComponents = (theRenderer instanceof A_Renderer ?
											((A_Renderer) theRenderer).getComponents (aContainer) :
											getComponents (aContainer));
		for (int i = 0; i < theComponents.length; i++)
		{
			if (theDirty)
			{
				addComponentToPhases (theComponents [i], theRenderPhaseMap);
			}
			
			if (theComponents [i] instanceof MContainer)
			{
				clean ((MContainer) theComponents [i]);
			}
		}
		aContainer.setDirty (false);
	}
	
	public MContainer getContainer (MComponent aComponent)
	{
		return aComponent.getParent ();
	} 
	
	/**
	* Get all of the components that should be included as part of this simple post
	* card
	*
	* @param aRenderable The current component (current point in deep navigation)
	*
	* @return The I_Renderable array
	*/
	public I_Renderable [] getSimplePostCardComponents (I_Renderable aRenderable)
	{
		return new I_Renderable [] {aRenderable};
	}
	
	protected MComponent [] getComponents (MContainer aContainer)
	{
		return aContainer.getComponents ();
	}
	
	protected String generatePhase (String aPhase, I_PhaseGenerationCallback aCallback, I_Renderable aContainer)
	{
		StringBuffer retVal = new StringBuffer ();
		Object [] theContainers = (aContainer == null ?
										renderPhaseMap.values ().toArray () :
										new Object [] {renderPhaseMap.get (aContainer)});
		//Object [] theContainers = renderPhaseMap.values ().toArray ();
		if (aContainer == null || aContainer instanceof MContainer)
		{
			I_Renderer theRenderer = (aContainer != null ? aContainer.getRenderer () : null);
			if (theRenderer == null ||
				theRenderer.generatePhaseOkay (aContainer, aPhase))
			{
				/*if (! (aContainer instanceof MFrame))
				{
					//
					//	The MFrame always has to generate some prologue stuff
					//
					MComponent theComponent = ((MComponent) aContainer).getParent ();
					while (! (theComponent instanceof MFrame) && theComponent != null)
					{
						theComponent = theComponent.getParent ();
					}
					
					if (theComponent instanceof MFrame)
					{
						retVal.append (aCallback.generatePhase (aPhase, new Object [] {theComponent}));
					}
				}*/
				
				for (int i = 0; i < theContainers.length; i++)
				{
					//++ SL 2001.08.13
					//
					// Solves the nullpointer error if MTable is built with strings  
					// rather than with MComponents					
					if (theContainers[i] != null)
					{
						HashMap thePhase = (HashMap) ((HashMap) theContainers [i]).get (aPhase);
						if (thePhase != null)
						{
							Object [] theComponents = thePhase.values ().toArray ();
							retVal.append (aCallback.generatePhase (aPhase, theComponents));
						}
					}
					//-- SL 2001.08.10
				}
			}
		}
		return retVal.toString ();
	}
	
	protected void generate (I_Renderable aRenderable, StringParser aParser)
	{
		if (aRenderable instanceof MContainer)
		{
			if (renderPhaseMap == null)
			{
				renderPhaseMap = new HashMap (10);
			}
			clean ((MContainer) aRenderable);
		}
		aParser.setVariable ("prologue", ((I_HasPrologue) this).generatePrologue (aRenderable));
		aParser.setVariable ("content", ((I_HasContent) this).generateContent (aRenderable));
		aParser.setVariable ("epilogue", ((I_HasEpilogue) this).generateEpilogue (aRenderable));
	}


	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** This is the default rendering method for renderers. This methods
	  * looks for templates according to the following naming scheme, and 
	  * substitutes the <code>^^variables^^</code> with values declared in 
	  * the component's <code>fillParserValues()</code> method.<p>
	  * 
	  * Naming scheme:<p>
	  * 
	  * <pre>
	  * ComponentType.[browser.[version.]]{html|wml}
	  * </pre>
	  * 
	  * e.g.<p>
	  * 
	  * <pre>
	  * MLabel.up.2.wml
	  * </pre>
	  * 
	  * or<p>
	  * 
	  * <pre>
	  * MButton.wca.html
	  * </pre>
	  * 
	  * If your component requires more complex rendering procedures for a 
	  * particular client environment, simply subclass 
	  * <code>A_Renderer</code> and override this method.
	  * 
	  */
	  
	public synchronized String render(I_Renderable aRenderable)
	{
		String retVal = null;
		
		if (renderTemplate != null)
		{
			aRenderable.fillParserValues();
			StringParser theStringParser = aRenderable.getParser();
			
			if (theStringParser == null)
			{
				theStringParser = new StringParser ();
			}
			if (this instanceof I_Generator)
			{
				generate (aRenderable, theStringParser);
			}
			else
			{
				doRender (theStringParser, aRenderable);
			}
			
			try
			{
				retVal = theStringParser.parseString (getRenderTemplate (aRenderable));
			}
			catch(NullPointerException e)
			{
				System.out.println(new ErrorString("[A_Renderer.render()] No templates available for " + aRenderable.getClass().getName()));
			}
		}
		return (retVal == null ? "" : retVal);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getRenderTemplate
	// ----------------------------------------------------------------------
	
	protected String getRenderTemplate (I_Renderable aRenderable)
	{
		return renderTemplate [0];
	}


	// ----------------------------------------------------------------------
	// METHOD: generatePostList
	// ----------------------------------------------------------------------
	
	/** Generate a protocol independent based post list.
	  *
	  * @param aRenderable The I_Renderable object
	  *
	  * @return The WML String representing the post list
	  *
	  */
	
	public String generatePostList (I_Renderable aRenderable)
	{
		return generatePostList (aRenderable, null);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateDoneButton
	// ----------------------------------------------------------------------
	
	protected String generateDoneButton (I_Renderable aRenderable)
	{
		I_Renderable theFrame = getContainingFrame (aRenderable);
		return generateSimpleAnchor (theFrame, "pop", "Done");
	}
	

	// ----------------------------------------------------------------------
	// METHOD: zapGremlins
	// ----------------------------------------------------------------------
	
	protected String zapGremlins (String aString)
	{
		return aString.replace ('/', '_');
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateCard
	// ----------------------------------------------------------------------
	
	/** Generate a card.
	  *
	  * @param aCardID   The id to use for the card.
	  * 
	  * @param aTitle    The title to use for the card.
	  * 
	  * @param aPrefix   Any event information.
	  * 
	  * @param aContent  The content to be placed in the card.
	  * 
	  * @param aIncludeP A boolean indicating whether or not to include a
	  *                  <code>&lt;p&gt;</code> tag.
	  * 
	  */
	  
	public String generateCard (String aCardID,
								String aTitle,
								String aPrefix,
								String aContent,
								boolean aIncludeP)
	{
		StringBuffer retVal = new StringBuffer ("\n\n<card id=\"");
		retVal.append (zapGremlins (aCardID));
		retVal.append ("\" title=\"");
		retVal.append (zapGremlins (aTitle));
		retVal.append ("\">\n");
		if (aPrefix != null && aPrefix.length () > 0)
		{
			retVal.append (aPrefix);
		}
		if (aIncludeP)
		{
			retVal.append ("<p>");
		}
		retVal.append ("\n");
		retVal.append (aContent);
		retVal.append ("\n");
		if (aIncludeP)
		{
			retVal.append ("</p>");
		}
		retVal.append ("</card>");
		return retVal.toString ();
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateCard
	// ----------------------------------------------------------------------
	
	/** Generate a card.
	  *
	  * @param aCardID  The id to use for the card.
	  * 
	  * @param aTitle   The title to use for the card.
	  * 
	  * @param aPrefix  Any event information.
	  * 
	  * @param aContent The content to be placed in the card.
	  * 
	  */
	  
	public String generateCard (String aCardID, String aTitle, String aPrefix, String aContent)
	{
		return generateCard (aCardID, aTitle, aPrefix, aContent, true);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateCard
	// ----------------------------------------------------------------------
	
	/** Generate a card.
	  *
	  * @param aCardID  The id to use for the card.
	  * 
	  * @param aTitle   The title to use for the card.
	  * 
	  * @param aContent The content to be placed in the card.
	  * 
	  */
	  
	public String generateCard(String aCardID, String aTitle, String aContent)
	{
		return generateCard (aCardID, aTitle, null, aContent);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getContainingFrame
	// ----------------------------------------------------------------------
	
	public I_Renderable getContainingFrame(I_Renderable aRenderable)
	{
		if (containingFrame == null)
		{
			MComponent theComponent = (MComponent) aRenderable;
			while (theComponent != null && !(theComponent instanceof MFrame))
			{
				theComponent = theComponent.getParent ();
			}
			containingFrame = (I_Renderable) theComponent;
		}
		return containingFrame;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getMauiApplication
	// ----------------------------------------------------------------------
	
	/** Get the MauiApplication object from the renderable.
	  *
	  * @param aRenderable The I_Renderable object
	  *
	  * @return The MauiApplication object
	  * 
	  */
	  
	protected MauiApplication getMauiApplication(I_Renderable aRenderable)
	{
		return (MauiApplication) ((MComponent) aRenderable).getRootParent ();
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePostList
	// ----------------------------------------------------------------------
	
	/** Generate a post list
	  *
	  * @param aRenderable The I_Renderable object
	  * 
	  * @param aRenderableValue A value to put into the post list for this I_Renderable object
	  *
	  * @return The WML String representing the post list
	  * 
	  */
	  
	public String generatePostList(I_Renderable aRenderable, String aRenderableValue)
	{
		MauiApplication theApplication = getMauiApplication (aRenderable);
		
		StringBuffer retVal = new StringBuffer ("<go href=\"");
		if (isUp && !theApplication.getRuntimeName ().equals (""))
		{
			retVal.append (theApplication.getServletAndApplication ());
			generatePseudo (getContainingFrame (aRenderable), "peek");
			retVal.append (getGeneratedPseudo ());
		}
		retVal.append ("#postCard_");
		retVal.append (Integer.toString (currentPostCard));
		retVal.append ("\">\n");
		if (aRenderable != null && aRenderableValue != null)
		{
			if (aRenderable instanceof HasPostValue)
			{
				retVal.append ("<setvar name=\"");
				retVal.append (((MComponent) aRenderable).getWMLSafeComponentID ());
				retVal.append ("\" value=\"");
				retVal.append (aRenderableValue);
				retVal.append ("\" />\n");
			}
			else
			{
				retVal.append ("<setvar name=\"mauiOneEventID\" value=\"");
				retVal.append (((MComponent) aRenderable).getWMLSafeComponentID ());
				retVal.append ("\" />\n");
				retVal.append ("<setvar name=\"mauiOneEventValue\" value=\"");
				retVal.append (aRenderableValue);
				retVal.append ("\" />\n");
				I_Renderer theFrameRenderer = (I_Renderer) getContainingFrame (aRenderable).getRenderer ();
				if (theFrameRenderer instanceof I_FrameRenderer)
				{
					((I_FrameRenderer) theFrameRenderer).setUseMauiOneEvent (true);
				}
			}
		}
		retVal.append ("</go>\n");
		/*StringBuffer retVal = generatePostHeader ();
		StringBuffer theEventQueue = new StringBuffer ();
		String theComponentID;
		HasPostValue [] thePostValues = getValuePosters (aRenderable);
		
		for (int i = 0; i < thePostValues.length; i++)
		{
			MComponent theComponent = (MComponent) thePostValues [i];
			theComponentID = (theComponent instanceof MauiApplication ?
								"sessionID" :
								theComponent.getWMLSafeComponentID ());
			retVal.append (generatePostStatement (theComponentID,
												  thePostValues [i].getPostValue ()));

			if (! (theComponent instanceof MauiApplication))
			{
				theEventQueue.append (theComponentID);
				theEventQueue.append (",");
			}
		}
		
		if (aRenderableValue != null)
		{
			theComponentID = ((MComponent) aRenderable).getWMLSafeComponentID ();
			retVal.append (generatePostStatement (theComponentID, aRenderableValue));
			theEventQueue.append (theComponentID);
		}
		else if (theEventQueue.length () > 0)
		{
			theEventQueue.setLength (theEventQueue.length () - 1);	// Strip off trailing comma
		}
		
		retVal.append (generatePostStatement ("mauiEventQueue", theEventQueue.toString ()));
		retVal.append (generatePostFooter ());*/
		return retVal.toString ();
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePostHeader
	// ----------------------------------------------------------------------
	
	protected StringBuffer generatePostHeader()
	{
		StringBuffer retVal = new StringBuffer ("<go href=\"#postCard_");
		retVal.append (Integer.toString (currentPostCard));
		retVal.append ("\"");
		return retVal; 
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePostFooter
	// ----------------------------------------------------------------------
	
	protected StringBuffer generatePostFooter()
	{
		return new StringBuffer ();
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePostStatement
	// ----------------------------------------------------------------------
	
	protected StringBuffer generatePostStatement(String aName, String aValue)
	{
		StringBuffer retVal = new StringBuffer ("<postfield name=\"");
		retVal.append (aName);
		retVal.append ("\" value=\"");
		retVal.append (aValue);
		retVal.append ("\"/>\n");
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePseudoHref
	// ----------------------------------------------------------------------
	
	protected StringBuffer generatePseudoHref(String aPseudoCommand, String aSuffix, boolean aUsePreviousApp)
	{
		StringBuffer retVal = new StringBuffer ("<go href=\"/");
		MauiApplication theApplication = getMauiApplication (renderable);
		
		if (aUsePreviousApp)
		{
			HTTPSession theSession = theApplication.getSession ();
			Object thePreviousApp = theSession.getShared ("previousApp");
			if (thePreviousApp != null)
			{
				theApplication = (MauiApplication) thePreviousApp;
			}
		}
		//if (!theApplication.getRuntimeName ().equals (""))
		//{
		//	retVal.append (theApplication.getRuntimeName ());
		//}
		retVal.append (theApplication.getServletAndApplication ().substring (1));
		
		if (retVal.charAt (retVal.length () - 1) != '/')
		{
			retVal.append ("/");
		}
		if (aPseudoCommand != null)
		{
			generatePseudo (renderable, aPseudoCommand);
		}
		doGeneratePseudo ();
		String theGeneratedPseudo = getGeneratedPseudo ();
		if (theGeneratedPseudo.length () > 0)
		{
			retVal.append (theGeneratedPseudo);
			retVal.append (generateUniqueReference ());
		}
		else
		{
			generatePseudo ("uniqueRef", generateShortUniqueReference ());
			retVal.append (getGeneratedPseudo ());
		}
		retVal.append ("\" ");
		retVal.append (aSuffix != null ? aSuffix : "/");
		retVal.append (">\n");
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: doGeneratePseudo
	// ----------------------------------------------------------------------
	
	protected void doGeneratePseudo()
	{
		// Do nothing.
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePostGoStatement
	// ----------------------------------------------------------------------
	
	protected StringBuffer generatePostGoStatement(I_Renderable aRenderable)
	{
		return generatePostGoStatement (aRenderable, "push");
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePostGoStatement
	// ----------------------------------------------------------------------
	
	protected StringBuffer generatePostGoStatement(I_Renderable aRenderable, String aSuffix)
	{
		return generatePseudoHref (aSuffix, " method=\"post\"", false);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateSimpleAnchor
	// ----------------------------------------------------------------------
	
	protected String generateSimpleAnchor(I_Renderable aRenderable, 
	                                      String aEventValue, 
	                                      String aLabel)
	{
		MComponent theComponent = (MComponent)aRenderable;
		StringBuffer retVal = new StringBuffer("<anchor id=\"");
		retVal.append(theComponent.getWMLSafeComponentID());
		retVal.append("\" title=\"Link\">");
		retVal.append(WMLCompositor.encodeWML(aLabel));
		retVal.append("\n");
		retVal.append(generatePostList (aRenderable, aEventValue));
		retVal.append("</anchor>\n");
		return retVal.toString();
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateSimpleAnchor
	// ----------------------------------------------------------------------
	
	protected String generateSimpleAnchor(I_Renderable aRenderable, 
	                                      String aEventValue)
	{
		return generateSimpleAnchor(aRenderable, aEventValue, getLabel(aRenderable));
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getLabel
	// ----------------------------------------------------------------------
	
	/** This method returns a short string which can be used to describe the
	  * given <code>I_Renderable</code> object.
	  * 
	  */
	
	protected String getLabel(I_Renderable aRenderable)
	{
		return (aRenderable instanceof HasLabel ? ((HasLabel)aRenderable).getLabel() :
		                                          ((MComponent)aRenderable).getName());
	}
		

	// ----------------------------------------------------------------------
	// METHOD: doRender
	// ----------------------------------------------------------------------
	
	protected void doRender(StringParser aParser, I_Renderable aRenderable)
	{
		// Do nothing.
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: generateComponentID
	// ----------------------------------------------------------------------
	
	/** Generate a unique component ID that is safe on all devices.
	  *
	  */
	
	public String generateComponentID(MComponent aComponent)
	{
		return "IDz" + aComponent.getComponentID();
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateForwardPrologue
	// ----------------------------------------------------------------------
	
	public String generateForwardPrologue(I_Renderable aRenderable)
	{
		String theValue = getValue (aRenderable);
		return (theValue == null ?
						"" :
						setVar (generateComponentID ((MComponent) aRenderable),
								theValue));
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getValue
	// ----------------------------------------------------------------------
	
	public String getValue(I_Renderable aRenderable)
	{
		Settable theSettable = (Settable) aRenderable;
		return theSettable.getValue ().toString ();
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateBackwardPrologue
	// ----------------------------------------------------------------------
	
	public String generateBackwardPrologue(I_Renderable aRenderable)
	{
		MComponent theComponent = (MComponent) aRenderable;
		return setVar (generateComponentID (theComponent) + "_Label", "$(" + generateComponentID (theComponent) + ")");//":e)");
	}
	

	// ----------------------------------------------------------------------
	// METHOD: filter
	// ----------------------------------------------------------------------
	
	private static String [] filter(Class [] aInterfaces)
	{
		Vector theList = new Vector ();
		String theInterfaceName;
		
		for (int i = 0; i < aInterfaces.length; i++)
		{
			theInterfaceName = aInterfaces [i].getName ();
			if (theInterfaceName.startsWith ("com.bitmovers.maui."))
			{
				theList.addElement (theInterfaceName);
			}
		}
		
		Object [] theValues = theList.toArray ();
		String [] retVal = new String [theValues.length];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = (String) theValues [i];
		}
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getRenderPhases
	// ----------------------------------------------------------------------
	
	public static String[] getRenderPhases(Class aClass)
	{
		String [] retVal = null;
		String [] theClasses = filter (aClass.getInterfaces ());
		if (aClass.getSuperclass () != Object.class)
		{
			String [] theSuperClasses = getRenderPhases (aClass.getSuperclass ());
			if (theSuperClasses.length == 0)
			{
				retVal = theClasses;
			}
			else
			{
				retVal = new String [theSuperClasses.length + theClasses.length];
				System.arraycopy (theSuperClasses, 0, retVal, 0, theSuperClasses.length);
				System.arraycopy (theClasses, 0, retVal, theSuperClasses.length - 1, theClasses.length);
			}		
		}
		else
		{
			retVal = theClasses;
		}
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getRenderPhases
	// ----------------------------------------------------------------------
	
	public String[] getRenderPhases()
	{
		if (renderPhaseNames == null)
		{
			renderPhaseNames = getRenderPhases (getClass ());
		}
		return renderPhaseNames;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateUniqueReference
	// ----------------------------------------------------------------------
	
	public static synchronized String generateUniqueReference ()
	{
		return "?uniqueRef=" + uniqueReference++;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateShortUniqueReference
	// ----------------------------------------------------------------------
	
	public static synchronized String generateShortUniqueReference()
	{
		return Integer.toString (uniqueReference++);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generatePseudo
	// ----------------------------------------------------------------------
	
	public void generatePseudo(String aReference, String aValue)
	{
		if (generatedPseudo == null)
		{
			generatedPseudo = new StringBuffer ("pseudo");
		}
		generatedPseudo.append ("/");
		generatedPseudo.append (aReference);
		generatedPseudo.append ("_");
		generatedPseudo.append (aValue);
	}
		

	// ----------------------------------------------------------------------
	// METHOD: generatePseudo
	// ----------------------------------------------------------------------
	
	public void generatePseudo(I_Renderable aRenderable, String aValue)
	{
		generatePseudo (((MComponent) aRenderable).getWMLSafeComponentID (), aValue);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getGeneratedPseudo
	// ----------------------------------------------------------------------
	
	public String getGeneratedPseudo()
	{
		String retVal = (generatedPseudo == null ? "" : generatedPseudo.toString ());
		generatedPseudo = null;
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: generateBackKey
	// ----------------------------------------------------------------------
	
	protected boolean generateBackKey()
	{
		return useBackKey;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getDepthBasedAlignment
	// ----------------------------------------------------------------------
	
	public String getDepthBasedAlignment()
	{
		return null;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getRepresentativeRenderable
	// ----------------------------------------------------------------------
	
	public I_Renderable getRepresentativeRenderable(I_Renderable aRenderable)
	{
		return (representativeRenderable == null ? aRenderable : representativeRenderable);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getEventSource
	// ----------------------------------------------------------------------
	
	/** Get the event source for this renderer... In some cases this may not 
	  * be the same as the actual its associated <code>I_Renderable</code>.
	  * 
	  */
	  
	public I_Renderable getEventSource(I_Renderable aRenderable)
	{
		return (renderable == null ? aRenderable : renderable);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: autoPop
	// ----------------------------------------------------------------------
	
	public boolean autoPop(MActionEvent aActionEvent, Stack aStack)
	{
		return false;
	}
		

	// ----------------------------------------------------------------------
	// METHOD: generatePhaseOkay
	// ----------------------------------------------------------------------
	
	/** Is it okay to generate a phase for this renderer?
	  *
	  * @param aRenderable The target renderable.
	  * 
	  * @param aPhase      The String describing the phase being generated.
	  *
	  * @return            Boolean indicating if phase generation should be 
	  *                    done for this component.
	  * 
	  */
	  
	public boolean generatePhaseOkay(I_Renderable aRenderable, String aPhase)
	{
		return true;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: convertToRenderable
	// ----------------------------------------------------------------------
	
	protected I_Renderable[] convertToRenderable(MComponent [] aComponents)
	{
		I_Renderable [] retVal = new I_Renderable [aComponents.length];
		
		int j = 0;
		for (int i = 0; i < retVal.length; i++)
		{
			if (aComponents [i] instanceof I_Renderable)
			{
				retVal [j++] = (I_Renderable) aComponents [i];
			}
		}
		
		if (j != retVal.length)
		{
			I_Renderable [] theTemp = new I_Renderable [j];
			System.arraycopy (retVal, 0, theTemp, 0, j);
			retVal = theTemp;
		}
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: finish
	// ----------------------------------------------------------------------
	
	public void finish()
	{
		renderTemplate = null;
		renderPhaseNames = null;
		renderPhaseMap = null;
		valuePosters = null;
	}
}