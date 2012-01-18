// ========================================================================
// com.bitmovers.maui.engine.render.MFrame_wml
// ========================================================================

package com.bitmovers.maui.engine.render;
import java.util.Stack;
import java.util.Vector;
import java.util.StringTokenizer;

import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.foundation.HasPostValue;
import com.bitmovers.maui.components.foundation.MMenuBar;
import com.bitmovers.maui.components.foundation.MFrame;
import com.bitmovers.maui.components.foundation.MSettable;
import com.bitmovers.maui.components.foundation.MRadioButton;
import com.bitmovers.maui.events.AlwaysNotify;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.engine.httpserver.HTTPSession;

// ========================================================================
// CLASS: MFrame_wml
// ========================================================================

/** This is the wml renderer for MFrame objects.  It also handles "deep 
  * navigation", which is accomplished by listening to events published 
  * from objects which implement the "I_HasDepth" interface.  When such a 
  * component publishes an event this class sets the initial render point 
  * to be the component which published the event... a bit like zooming in 
  * on that component.
  * 
  * @invisible
  * 
  */

public class MFrame_wml extends A_Renderer
                     implements I_Generator,
                                I_RendererInitialize,
                                I_RendererListener,
                                I_FrameRenderer,
                                AlwaysNotify,
                                MActionListener
{
	private static final int PHASE_FORWARD = 0;
	private static final int PHASE_BACKWARD = 1;
	private static final int PHASE_POST = 2;
	
	private MauiApplication mauiApplication = null;
	private HTTPSession session = null;
	private HasPostValue [] valuePosters = null;
	private Stack depthBasedRenderables = new Stack ();
	private I_Renderable currentRenderable = null;
	private I_Renderable lastRenderable = null;
	private boolean needsBackout = false;
	private boolean generatingPseudo = false;
	private boolean mFrameGenerated = false;
	private int uniqueReference = -1;
	private boolean uniqueCheckDone = false;
	private boolean useMauiOneEvent = false;
	private boolean popDone = false;
	
	// ----------------------------------------------------------------------
	// METHOD: initialize
	// ----------------------------------------------------------------------
	
	/** Initializes the renderer. This is called once only, when the renderer 
	  * is instantiated.
	  *
	  * @param aRenderable           The <code>I_Renderable</code> component 
	  *                              (ie. an <code>MFrame</code> object).
	  * 
	  * @param aParent               The parent component of the 
	  *                              <code>I_Renderable</code>. This is used 
	  *                              for some embedded components (eg. expand 
	  *                              pane buttons).
	  * 
	  * @param aClientClassification A string array which describes the 
	  *                              client environment (eg. HTML/WML, 
	  *                              browser type, platform type).
	  * 
	  */
	  
	public void initialize(I_Renderable aRenderable,
	                       MComponent aParent,
                           String [] aClientClassification)
	{
		super.initialize (aRenderable, aParent, aClientClassification);
		mauiApplication = (MauiApplication) aParent.getRootParent ();
		session = mauiApplication.getSession ();
		
		//
		//	The frame renderers must listen for the creation of other renderers,
		//	and check each renderer for implementing "I_HasDepth".  If it does have
		//	depth, then the frame must listen for MActionEvent publications from its
		//	corresponding MComponent object, and manipulate the depth accordingly.
		//
		
		mauiApplication.getSession ().addRendererListener (mauiApplication, this);
		isUp = (aClientClassification [1].equals ("up"));
		isNokia = (aClientClassification [1].startsWith ("nokia"));
	}
	
	/**
	* Get the naming patterns for the parser templates.
	* 
	* @return A String array of the naming patterns for the template resources.
	*/
	public String [] getTemplateTypes ()
	{
		return new String []{"", "layout"};
	}
	
	private String getPhase (int aPhaseCode)
	{
		String retVal = null;
		
		switch (aPhaseCode)
		{
			case (PHASE_FORWARD) :
				retVal = "com.bitmovers.maui.engine.render.I_HasForwardPrologue";
				break;
				
			case (PHASE_BACKWARD) :
				retVal = "com.bitmovers.maui.engine.render.I_HasBackwardPrologue";
				break;
			
			case (PHASE_POST) :
				retVal = "com.bitmovers.maui.components.foundation.HasPostValue";
				break;
		}
		return retVal;
	}
	
	private I_Renderable [] getPhaseComponents (final int aPhaseCode,
												I_Renderable aRenderable,
												I_Renderer aRenderer)
	{
		I_Renderable [] retVal = null;
		
		if (aRenderer != null)
		{
			if ((aPhaseCode == PHASE_FORWARD ||
				aPhaseCode == PHASE_BACKWARD) &&
				aRenderer instanceof I_SimplePhase)
			{
				retVal = ((I_SimplePhase) aRenderer).getSimplePhaseComponents (aRenderable, getPhase (aPhaseCode));
			}
			else if (aPhaseCode == PHASE_POST &&
				aRenderer instanceof I_SimplePostCard)
			{
				retVal = ((I_SimplePostCard) aRenderer).getSimplePostCardComponents (aRenderable);
			}
			else
			{
				retVal = convertToRenderable (((MContainer) aRenderable).getComponents ());
			}
		}
		return retVal;
	}
												
	
	private StringBuffer recurseGeneration (final int aPhaseCode,
							   				final I_Renderable aRenderable,
							   				final I_PhaseGenerationCallback aCallback)
	{
		StringBuffer retVal = new StringBuffer ();
		String thePhase = getPhase (aPhaseCode);
		retVal.append (generatePhase (thePhase,
									  aCallback,
									  aRenderable));
		if (aRenderable instanceof MContainer)
		{
			MContainer theContainer = (MContainer) aRenderable;
			I_Renderer theRenderer = theContainer.getRenderer ();
			I_Renderable [] theComponents = getPhaseComponents (aPhaseCode, aRenderable, theRenderer);
			//(theRenderer != null && theRenderer instanceof I_SimplePhase ?
			//									((I_SimplePhase) theRenderer).getSimplePhaseComponents (aRenderable,
			//																				 			thePhase) :
			//									convertToRenderable (theContainer.getComponents ()));
			if (theComponents != null)
			{
				for (int i = 0; i < theComponents.length; i++)
				{
					if (theComponents [i] instanceof I_Renderable &&
						theComponents [i] instanceof MContainer &&
						theComponents [i] != aRenderable)
					{
						theRenderer = theComponents [i].getRenderer ();
						if (theRenderer == null ||
							theRenderer.generatePhaseOkay ((I_Renderable) theComponents [i], thePhase))
						{
							retVal.append (recurseGeneration (aPhaseCode,
															  (I_Renderable) theComponents [i],
															  aCallback));
						}
					}
				}
			}
		}
		return retVal;
	}
	
	/**
	* Generate a card prologue "phrase".  This generates prologues for
	* "onenterforward" and "onenterbackward" card events
	*
	* @param aForward Boolean indicating the direction of the prologue "phrase"
	*/
	protected StringBuffer generateDirectionalPrologue (final boolean aForward,
														final I_Renderable aRenderable)
	{
		hasOnTag = false;
		inPrologue = true;
		mFrameGenerated = false;
		
		//
		//	The callback for generating the prologue "phrase".
		//	The actual work is done in doGeneratePhase
		//
		I_PhaseGenerationCallback theCallback = new I_PhaseGenerationCallback ()
			{
				public StringBuffer generatePhase (String aPhase, Object [] aComponents)
				{
					return doGeneratePhase (aForward, aComponents);
				}
			};
		
		//
		//	Generate a prologue "phrase" - onenterforward or onenterbackward
		//
		
		StringBuffer retVal = /*new StringBuffer (generatePhase ((aForward ?
							  										"com.bitmovers.maui.engine.render.I_HasForwardPrologue" :
							  										"com.bitmovers.maui.engine.render.I_HasBackwardPrologue"),
												theCallback,
												aRenderable));*/
							  recurseGeneration ((aForward ? PHASE_FORWARD : PHASE_BACKWARD), 
							  					 aRenderable,
							  					 theCallback);
							  					 
		if (currentRenderable == aRenderable &&
			! (aRenderable instanceof MContainer))
		{
			I_Renderer theRenderer = aRenderable.getRenderer ();
			if ((aForward ? theRenderer instanceof I_HasForwardPrologue :
							theRenderer instanceof I_HasBackwardPrologue))
			{
				retVal.append (doGeneratePhase (aForward,
												new Object [] {aRenderable}));
			}
		}				 
		if (!mFrameGenerated)
		{
			retVal.append (doGeneratePhase (aForward,
											new Object [] {renderable}));
		}
		
			
		if (hasOnTag)
		{
			retVal.append ("</refresh>\n</onevent>\n");
		}
		inPrologue = false;
		return retVal;			
	}
	
	public String getValue (I_Renderable aRenderable)
	{
		return (inPrologue ? "push" : super.getValue (aRenderable));
	}
	
	public String generateBackwardPrologue (I_Renderable aRenderable)
	{
		return generateForwardPrologue (aRenderable);
	}
	
	protected boolean inPrologue = false;
	protected boolean hasOnTag = false;
	
	private StringBuffer generateOnTag (boolean aForward)
	{
		StringBuffer retVal = null;
		if (!hasOnTag)
		{
			hasOnTag = true;
			retVal = new StringBuffer ("<onevent type=");
			retVal.append ((aForward ? "\"onenterforward\">\n<refresh>\n" :
						   			   "\"onenterbackward\">\n<refresh>\n"));
		}
		else
		{
			retVal = new StringBuffer ();
		}
		return retVal;
	}
		
	/**
	* This method actually generates one line of a prologue "phrase".  If it is the first line then it also
	* generates the "onevent" tag
	*
	* @param aForward Boolean indicating "onenterforward" or "onenterbackward"
	* @param aComponents The MComponents to be inserted into this prologue "phrase"
	*
	* @return StringBuffer The WML for the prologue
	*/
	protected StringBuffer doGeneratePhase (boolean aForward, Object [] aComponents)
	{
		I_Renderer theRenderer = null;
		StringBuffer retVal = new StringBuffer ();
		String thePrologue;
		
		for (int i = 0; i < aComponents.length; i++)
		{
			theRenderer = ((MComponent) aComponents [i]).getRenderer ();
			if (theRenderer != null &&
				(aForward ? theRenderer instanceof I_HasForwardPrologue :
							theRenderer instanceof I_HasBackwardPrologue))
			{
				thePrologue = (aForward ?
									((I_HasForwardPrologue) theRenderer).generateForwardPrologue ((I_Renderable) aComponents [i]) :
									((I_HasBackwardPrologue) theRenderer).generateBackwardPrologue ((I_Renderable) aComponents [i]));
				if (thePrologue != null && thePrologue.length () > 0)
				{
					retVal.append (generateOnTag (aForward));
						
					//
					//	Let the I_Renderer for the component generate its own prologue line
					//
					if (theRenderer == this)
					{
						mFrameGenerated = true;
					}
					retVal.append (thePrologue);
					
				}
			}
		}
		
		return retVal;
	}
	
	/*private String generatePrefix (I_Renderable aRenderable)
	{
		StringBuffer theHref = new StringBuffer (mauiApplication.getRuntimeName ());
		if (needsBackout)
		{
			int theSize = depthBasedRenderables.size ();
			generatePseudo (aRenderable, "pop" + (push ? theSize : (theSize > 0 ? theSize - 1 : 0)));
			theHref.append (getGeneratedPseudo ());
		}
		theHref.append (generateUniqueReference ());
		return setVar ("goHref", theHref.toString ());
	}
	
	public String generateForwardPrologue (I_Renderable aRenderable)
	{
		if (currentRenderable != null && push)
		{
			depthBasedRenderables.push (currentRenderable);
		}
		return generatePrefix (aRenderable);
	}
	
	public String generateBackwardPrologue (I_Renderable aRenderable)
	{
		return generatePrefix (aRenderable);
	}*/
	
	/**
	* Generate a statement which will cause a "backout" from deep navigation.
	*
	* @return The WML code for backing out from deep navigation
	*/
	protected String generateBackoutPost ()
	{
		StringBuffer retVal = generatePostGoStatement (renderable);
		retVal.append (generatePostStatement ("mauiOneEvent",
											  ((MComponent) renderable).getWMLSafeComponentID ()));
		retVal.append (generatePostStatement (((MComponent) renderable).getWMLSafeComponentID (),
											  MActionEvent.ACTION_POP));
		retVal.append ("</go>");
		return retVal.toString ();
	}
	
	/*private String generateBackout ()
	{
		StringBuffer retVal = new StringBuffer ("<onevent type=\"onenterbackward\">\n");
		retVal.append (generateBackoutPost ());
		retVal.append ("</onevent>\n");
		return retVal.toString ();
	}*/
	
	private StringBuffer generateBackSoftElement (String aType)
	{
		StringBuffer retVal = new StringBuffer ("<do type=\"");
		retVal.append (aType);
		retVal.append ("\" label=\"Back\">\n");
		retVal.append (doGenerateBackSoftKey ());
		retVal.append ("</do>\n");
		return retVal;
	}
	
	/**
	* Generate a "Back" soft key for the deep navigation backout
	*
	* @return The WML code for the "Back" soft key
	*/
	protected String generateBackSoftKey ()
	{
		StringBuffer retVal = null;
		if (currentRenderable != null &&
			currentRenderable.getRenderer () instanceof I_GenerateBackKeyCode)
		{
			String theCode = ((I_GenerateBackKeyCode) currentRenderable.getRenderer ()).generateBackKeyCode ();
			if (theCode != null)
			{
				retVal = new StringBuffer (theCode);
			}
		}
		
		if (retVal == null && depthBasedRenderables.size () > 0)
		{
			retVal = new StringBuffer ("<template>\n");
			if (isUp)
			{
				//retVal.append ("<onevent type=\"onpick\"><noop/></onevent>\n");
				retVal.append (generateBackSoftElement ("options"));
			}
			retVal.append (generateBackSoftElement ("prev"));
			//StringBuffer retVal = new StringBuffer ("<do type=\"options\" label=\"Backout\">\n<prev/></do>\n");
			//retVal.append (generateBackoutPost ());
			//retVal.append (doGenerateBackSoftKey ());//(isUp ? doGenerateBackSoftKey () : generatePostList (renderable, MActionEvent.ACTION_POP)));
			retVal.append ("</template>\n");
		}
		return (retVal == null ? "" : retVal.toString ());
	}
	
	/**
	* The WML code to be invoked when the soft key is pressed.  This method is separate from generateBackSoftKey to
	* allow method overriding by subclasses
	*
	* @return The WML code for the action to perform when the Back soft key is pressed
	*/
	protected String doGenerateBackSoftKey ()
	{
		if (isNokia)
		{
			generatingPseudo = true;
		}
		//StringBuffer retVal = generatePseudoHref ("pop", (isNokia ? " method=\"post\"" : null), true);
		//StringBuffer retVal = generatePseudoHref ("pop", null, true);
		StringBuffer retVal = generatePostHeader ();
		retVal.append (">\n");
		retVal.append (setVar (renderable, MActionEvent.ACTION_POP));
		retVal.append ("</go>\n");
		/*if (isNokia)
		{
			MauiApplication theApplication = (MauiApplication) ((MComponent) renderable).getRootParent ();
			retVal.append (generatePostStatement ("sessionID", theApplication.getPostValue ()));
			retVal.append ("</go>\n");
		}*/
		generatingPseudo = false;
		return retVal.toString ();
	}
	
	protected void doGeneratePseudo ()
	{
		if (generatingPseudo)
		{
			MauiApplication theApplication = (MauiApplication) ((MComponent) renderable).getRootParent ();
			generatePseudo ("sessionID", theApplication.getPostValue ());
			
			
		}
		
		if (currentRenderable != null)
		{
			I_Renderer theRenderer = currentRenderable.getRenderer ();
			if (theRenderer != null && theRenderer instanceof I_HasPseudoCommand)
			{
				String theTarget = ((I_HasPseudoCommand) theRenderer).getPseudoCommandTarget ();
				if (theTarget != null)
				{
					generatePseudo (theTarget,
									((I_HasPseudoCommand) theRenderer).getPseudoCommandValue ());
				}
			}
		}
	}
	
	protected String doGeneratePrologueOpening (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ("<card id=\"Main_");
		retVal.append (Integer.toString (currentMain));
		retVal.append ("\" title=\"");
		retVal.append (((MFrame) aRenderable).getTitle ());
		retVal.append ("\" newcontext=\"true\">\n");
		return retVal.toString ();
	}
	
	/**
	* Generate the prologue for a standard MFrame (ie. if deep navigation is not happening)
	*
	* @param aRenderable The MComponent being rendered (ie. an MFrame)
	*
	* @return The WML code for the prologue
	*/
	private String standardPrologue (I_Renderable aRenderable)
	{
		MFrame theFrame = (MFrame) aRenderable;
		StringBuffer retVal = new StringBuffer (generateBackSoftKey ());
		MMenuBar theMenuBar = theFrame.getMenuBar ();
		if (theMenuBar != null)
		{
			retVal.append (theMenuBar.render ());
		}
		retVal.append (doGeneratePrologueOpening (aRenderable));
		boolean theHasOnEvent = false;
				
		/*if (needsBackout)
		{
			//retVal.append ("<template>\n");
			//retVal.append (generateBackout ());
			//retVal.append ("</template>");
			retVal.append (generateDepthValue (aRenderable));
		}*/
		retVal.append (generateDirectionalPrologue (true, aRenderable));
		retVal.append (generateDirectionalPrologue (false, aRenderable));
		/*I_Renderer theRenderer = null;
		for (int i = 0; i < theComponents.length; i++)
		{
			theRenderer = theComponents [i].getRenderer ();
			if (theRenderer != null &&
				theRenderer instanceof I_HasPrologue &&
				theRenderer != this)
			{
				if (!theHasOnEvent)
				{
					//
					//	Create an "onEvent" tag
					//
					retVal.append ("<onevent type=\"onenterbackward\">\n<refresh>\n");
					theHasOnEvent = true;
				}
				
				retVal.append (((I_HasPrologue) theRenderer).
									generatePrologue ((I_Renderable) theComponents [i]));
			}					
		}
		
		if (theHasOnEvent)
		{
			retVal.append ("</refresh>\n</onevent>\n");
		}*/
		
		
		/* Removed because title was already being reported
		 * in the <card> tag. - ian (2001.06.13)
		 *
		retVal.append ("<p align=\"center\">\n");
		retVal.append (theFrame.getTitle ());
		retVal.append ("</p><p>");
		 */
		retVal.append ("<p>");
		
		return retVal.toString ();
	}
	
	/**
	* Generate the prologue if deep navigation is in effect
	*
	* @param aRenderable The renderable MComponent (ie. MFrame)
	*
	* @return The WML code for the deep navigation prologue
	*/
	private String depthBasedPrologue (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer (generateBackSoftKey ());
		MFrame theFrame = (MFrame) aRenderable;
		retVal.append (doGeneratePrologueOpening (aRenderable));
		retVal.append (generateDirectionalPrologue (true, currentRenderable));
		retVal.append (generateDirectionalPrologue (false, currentRenderable));
		String theAlignment = ((I_HasDepth) currentRenderable.getRenderer ()).getDepthBasedAlignment ();
		retVal.append ((theAlignment == null ?
							"<p>\n" :
							"<p align=\"" + theAlignment + "\">\n"));
		return retVal.toString ();
		
	}
	
	/**
	* Part of the the I_Generator interface.  Generate a card prologue
	*
	* @param aRenderable The renderable MComponent (ie. MFrame)
	*
	* @return The WML code for the prologue.  This will be inserted into the ^^prologue^^ tag by the StringParser
	*/
	public String generatePrologue (I_Renderable aRenderable)
	{
		uniqueCheckDone = false;
		useMauiOneEvent = false;
		popDone = false;
		currentMain = mainCounter++;
		currentPostCard = postCardCounter++;
		StringBuffer retVal = new StringBuffer ();
		//if (isUp)
		//{
			//retVal.append ("<head>\n<meta forua=\"true\" http-equiv=\"Cache-Control\" content=\"no-cache,no-store,no-transform\"/>\n</head>");
			retVal.append ("<head>\n<meta forua=\"true\" http-equiv=\"Cache-Control\" content=\"max-age=0\"/>\n</head>\n\n");
		//}
		retVal.append ((currentRenderable == null ?
						standardPrologue (aRenderable) :
						depthBasedPrologue (aRenderable)));
		return retVal.toString ();
	}
	
	private String standardContent (I_Renderable aRenderable)
	{
		aRenderable.fillParserValues();
		StringParser theParser = aRenderable.getParser();
		MFrame theFrame = (MFrame) aRenderable;
		theParser.setVariable ("layoutManager", renderComponents ((MContainer) aRenderable, DefaultWmlLayoutRenderer.SEPARATOR));
		return theParser.parseString (renderTemplate [1]);
	}
	
	private String generateDepthValue (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ("<onevent type=\"onenterforward\">\n<refresh>\n<setvar name=\"");
		retVal.append (((MComponent) aRenderable).getWMLSafeComponentID ());
		retVal.append ("\" value=\"backout_");
		int theSize = depthBasedRenderables.size () - 1;
		retVal.append (Integer.toString ((theSize < 0 ? 0 : theSize)));
		retVal.append ("\" />\n</refresh>\n</onevent>\n");
		return retVal.toString ();
	}
	
	protected String doRenderComponent (MComponent aComponent)
	{
		String retVal;
		if (currentRenderable != null)
		{
			I_HasDepth theRenderer = (I_HasDepth) currentRenderable.getRenderer ();
			retVal = theRenderer.depthBasedRender (currentRenderable, depthBasedRenderables, null);
		}
		else
		{
			retVal = super.doRenderComponent (aComponent);
		}
		return retVal;
	}
	
	private String depthBasedContent (I_Renderable aRenderable)
	{
		//
		//	Some deep navigation is going on, so pass it off to the component which will handle it
		//
		depthBasedRenderables.push (currentRenderable);
		I_HasDepth theRenderer = (I_HasDepth) currentRenderable.getRenderer ();
		//StringBuffer retVal = new StringBuffer (generateBackSoftKey ());
		//StringBuffer retVal = new StringBuffer (generateDepthValue (aRenderable));
		StringBuffer retVal = new StringBuffer (theRenderer.depthBasedRender (currentRenderable, depthBasedRenderables, null));
		//retVal.append (generatePostCard (aRenderable));
		return retVal.toString ();
	}	
	
	public String generateContent (I_Renderable aRenderable)
	{
		//return (currentRenderable == null ?
		//			standardContent (aRenderable) :
		//			depthBasedContent (aRenderable));
		return standardContent (aRenderable);
	}
	
	/*protected StringBuffer generatePostStatement (String aName, String aValue)
	{
		StringBuffer retVal = new StringBuffer ("<postfield name=\"");
		retVal.append (aName);
		retVal.append ("\" value=\"");
		retVal.append (aValue);
		retVal.append ("\"/>\n");
		return retVal;
	}*/
	
	protected String generatePostCard (I_Renderable aRenderable)
	{
		MComponent theRenderableComponent = (MComponent) aRenderable;
		final StringBuffer retVal = new StringBuffer ("<card id=\"postCard_");
		retVal.append (Integer.toString (currentPostCard));
		retVal.append ("\" title=\"Please Wait\">\n");
		/*retVal.append ("<onevent type=\"onenterbackward\">\n<go href=\"/");
		MauiApplication theApplication = (MauiApplication) theFrame.getRootParent ();
		retVal.append (theApplication.getRuntimeName ());
		retVal.append ("#Main_");
		retVal.append (Integer.toString (currentMain));
		retVal.append (generateUniqueReference ());
		retVal.append ("\"/></onevent>\n");*/
		/*retVal.append ("<onevent type=\"onenterbackward\">\n<go href=\"");
		MauiApplication theApplication = (MauiApplication) theRenderableComponent.getRootParent ();
		String theApplicationName = theApplication.getRuntimeName ();
		retVal.append ((theApplicationName.length () == 0 ? "" : "/"));
		retVal.append (theApplicationName);
		//if (needsBackout)
		//{
			int theSize = depthBasedRenderables.size ();
			generatePseudo (renderable, "pop");//depthBasedRenderables.size ());
			retVal.append (getGeneratedPseudo ());
		//}
		retVal.append (generateUniqueReference ());
		retVal.append ("\"/>\n</onevent>\n");*/
		retVal.append ("<onevent type=\"onenterforward\">\n");
		/*String thePseudoCommand = null;*/
		I_Renderer theRenderer = (currentRenderable != null ? currentRenderable.getRenderer () : null);
		/*if (theRenderer instanceof I_HasPseudoCommand)
		{
			thePseudoCommand = ((I_HasPseudoCommand) theRenderer).getPseudoCommand ();
		}
		if (thePseudoCommand == null)
		{
			thePseudoCommand = MActionEvent.ACTION_PUSH;
		}*/
		//generatingPseudo = true;
		retVal.append (generatePostGoStatement (aRenderable, null));
		//generatingPseudo = false;
		//retVal.append ("<go href=\"\" method=\"post\">\n");
		final StringBuffer theEventQueue = new StringBuffer ();
		mFrameGenerated = false;
		final Vector theProcessed = new Vector ();
		if (valuePosters == null)
		{
			I_PhaseGenerationCallback theCallback = new I_PhaseGenerationCallback ()
				{
					public StringBuffer generatePhase (String aPhase, Object [] aComponents)
					{
						String theComponentID;
						MComponent theComponent;
						//Vector theProcessed = new Vector ();
						for (int i = 0; i < aComponents.length; i++)
						{
							theComponent = (MComponent) aComponents [i];
							if (theComponent != null)
							{
								I_Renderer theLocalRenderer = (I_Renderer) theComponent.getRenderer ();
								theComponent = (theLocalRenderer != null ?
																(MComponent) theLocalRenderer.getRepresentativeRenderable ((I_Renderable) theComponent) :
																theComponent);
								if (theComponent != null &&
									!theProcessed.contains (theComponent))
								{
									theProcessed.addElement (theComponent);
									if (theLocalRenderer == MFrame_wml.this)
									{
										mFrameGenerated = true;
									}
									theComponentID = (theComponent instanceof MauiApplication ?
														"sessionID" :
														theComponent.getWMLSafeComponentID ());
									retVal.append (generatePostStatement (theComponentID,
																		  ((HasPostValue) theComponent).getPostValue ()));
									if (! (theComponent instanceof MauiApplication))
									{
										theEventQueue.append (theComponentID);
										theEventQueue.append (",");
									}
								}
							}
						}
						return retVal;
					}
				};
				
			//
			//	This is a horrible kludge to compensate with problem with deep navigation...
			//	In some cases, the card being rendered for deep navigation will be laid out differently.
			//	And sometimes not... This is just a check to see if the deep navigation component will use
			//	something other than a stock post card.
			//
			
			if (theRenderer instanceof I_SimplePostCard)
			{
				I_Renderable [] theRenderables = ((I_SimplePostCard) theRenderer).getSimplePostCardComponents (currentRenderable);
				if (theRenderables == null)
				{
					generatePhase ("com.bitmovers.maui.components.foundation.HasPostValue",
								   theCallback,
								   currentRenderable);
				}
				else
				{
					theCallback.generatePhase ("com.bitmovers.maui.components.foundation.HasPostValue",
											   theRenderables);
				}
			}
			else
			{
				if (currentRenderable != null)
				{
					System.out.println ("Wait");
				}
				recurseGeneration (PHASE_POST,
								   (currentRenderable == null ? renderable : currentRenderable),
								   theCallback);
								   
				//generatePhase ("com.bitmovers.maui.components.foundation.HasPostValue",
				//			   theCallback,
				//			   currentRenderable);
			}
			
			if (!mFrameGenerated)
			{
				theCallback.generatePhase ("com.bitmovers.maui.components.foundation.HasPostValue",
										   new Object [] {renderable});
			}
			/*if (needsBackout)
			{
				//
				//	Generate Frame level backout information
				//
				retVal.append (generatePostStatement (((MComponent) renderable).getWMLSafeComponentID (), "backout_" + depthBasedRenderables.size ()));
				theEventQueue.append (((MComponent) renderable).getWMLSafeComponentID ());
				theEventQueue.append (",");
			}*/
			
			if (useMauiOneEvent)
			{
				retVal.append (generatePostStatement ("$(mauiOneEventID)", "$(mauiOneEventValue)"));
			}
			if (theEventQueue.length () > 0)
			{
				theEventQueue.setLength (theEventQueue.length () - 1);	// Remove trailing ","
			}
			
			retVal.append (generatePostStatement ("mauiEventQueue", theEventQueue.toString ()));
			if (useMauiOneEvent)
			{
				retVal.append (generatePostStatement ("mauiOneEvent", "$(mauiOneEventID)"));
			}
			
			if (isNokia)
			{
				retVal.append (generatePostStatement ("sessionID", ((MauiApplication) theRenderableComponent.getRootParent ()).getSessionID ()));
			}
			retVal.append ("</go></onevent>\n</card>");
		}
		return retVal.toString ();
	}
	
	private String standardEpilogue (I_Renderable aRenderable)
	{
		final StringBuffer retVal = new StringBuffer ("</p>\n</card>\n");
		
		//retVal.append (doStandardEpilogueMark (aRenderable));
	
		//
		//	Search for components which have epilogues
		//
		I_PhaseGenerationCallback theCallback = new I_PhaseGenerationCallback ()
			{
				public StringBuffer generatePhase (String aPhase, Object [] aComponents)
				{
					for (int i = 0; i < aComponents.length; i++)
					{
						I_Renderer theRenderer = ((MComponent) aComponents [i]).getRenderer ();
						if (theRenderer != null &&
							theRenderer instanceof I_HasEpilogue &&
							theRenderer != MFrame_wml.this)
						{
							retVal.append (((I_HasEpilogue) theRenderer).
												generateEpilogue ((I_Renderable) aComponents [i]));
						}
					}
					return retVal;
				}
			};
		
		generatePhase ("com.bitmovers.maui.engine.render.I_HasEpilogue", theCallback, aRenderable);
		
		//
		//	And finally generate the card for posting data
		//
		retVal.append (generatePostCard (aRenderable));
		return retVal.toString ();
	}
	
	protected String doStandardEpilogueMark (I_Renderable aRenderable)
	{
		return "";
	}
	
	private String depthBasedEpilogue (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer (standardEpilogue (currentRenderable));
		I_Renderer theRenderer = currentRenderable.getRenderer ();
		if (theRenderer instanceof I_HasEpilogue)
		{
			retVal.append (((I_HasEpilogue) theRenderer).generateEpilogue (currentRenderable));
		}
		
		//currentRenderable = null;
		return retVal.toString ();
	}
	
	public String generateEpilogue (I_Renderable aRenderable)
	{
		String retVal = (currentRenderable == null ?
							standardEpilogue (aRenderable) :
							depthBasedEpilogue (aRenderable));
		MauiApplication theApplication = (MauiApplication) ((MComponent) aRenderable).getRootParent ();
		HTTPSession theSession = theApplication.getSession ();
		theSession.putShared ("previousApp", theApplication);
		return retVal;
		//return standardEpilogue (aRenderable);
	}
	
	protected MComponent [] getComponents (MContainer aContainer)
	{
		MComponent [] retVal = null;
		
		if (currentRenderable == null)
		{
			retVal = new MComponent [aContainer.getComponentCount ()];
			MComponent [] theComponents = aContainer.getComponents ();
			System.arraycopy (theComponents, 0, retVal, 0, theComponents.length);
			MMenuBar theMenuBar = ((MFrame) aContainer).getMenuBar ();
			if (theMenuBar != null)
			{
				MComponent [] theCopy = new MComponent [retVal.length + 1];
				System.arraycopy (retVal, 0, theCopy, 0, retVal.length);
				retVal = theCopy;
				retVal [theComponents.length] = theMenuBar;
			}
		}
		else
		{
			I_Renderer theRenderer = currentRenderable.getRenderer ();
			MComponent theParent = ((MComponent) currentRenderable).getParent ();
			I_Renderer theParentRenderer = theParent.getRenderer ();
			if (theRenderer instanceof I_UsesListGenerator &&
				theParentRenderer instanceof I_ListGenerator)
			{
				I_Renderable [] theRenderables =
					((I_ListGenerator) theParentRenderer).generateList ((I_Renderable) theParent,
																		((I_UsesListGenerator) theRenderer).getFilterClass ());
				retVal = new MComponent [theRenderables.length];
				for (int i = 0; i < retVal.length; i++)
				{
					retVal [i] = (MComponent) theRenderables [i];
				}
			}
			else
			{
				retVal = new MComponent [] {(MComponent) currentRenderable};
			}
		}
		return retVal;
		/*return (((MExpandPane) aContainer).isOpen () ?
					aContainer.getComponents () :
					new MComponent [0]);*/
	}
	
	/**
	* A renderer was created.  Check to see if it has depth
	*
	* @param aRendererEvent The RendererEvent object
	*/
	public void rendererCreated (RendererEvent aRendererEvent)
	{
		I_Renderer theRenderer = aRendererEvent.getRenderer ();
		if (theRenderer instanceof I_HasDepth ||
			theRenderer == this)
		{
			MComponent theComponent = aRendererEvent.getComponent ();
			theComponent.addActionListener (this);
			
			needsBackout = (theRenderer instanceof I_HasDepth);
		}
	}
	
	/**
	* Pop and backout a renderable
	*
	* @return The popped renderable
	*/
	private I_Renderable popAndBackout ()
	{
		I_Renderable retVal = (I_Renderable) depthBasedRenderables.pop ();
		((I_HasDepth) retVal.getRenderer ()).backout (retVal);
		return retVal;
	}
	
	private void popStack ()
	{
		if (!popDone)
		{
			popDone = true;
			//
			//	Popping the stack
			//
			//int theDepth = Integer.parseInt (theToken.substring (3));
			//int theSize = depthBasedRenderables.size ();
			if (depthBasedRenderables.size () > 0)
			{
				lastRenderable = popAndBackout ();
				Object theNextRenderable;
				while (depthBasedRenderables.size () != 0 &&
						(theNextRenderable = depthBasedRenderables.peek ()) instanceof MContainer &&
						((MContainer) theNextRenderable).getComponentCount () == 1)
				{
					//
					//	While the component being popped off the stack is an MContainer
					//	and contains only one component, keep popping
					//
					lastRenderable = popAndBackout ();
				}
			}
		}
	}
	
	private void propagateInvalidate (MComponent aComponent)
	{
		MComponent theComponent = aComponent;
		do
		{
			if (theComponent.isValid ())
			{
				theComponent.invalidate ();
			}
		}
		while ((theComponent = theComponent.getParent ()) != null);
	}
	
	private void peekStack (MComponent aComponent)
	{
		currentRenderable = (depthBasedRenderables.size () != 0 ?
									(I_Renderable) depthBasedRenderables.peek () :
									null);
		if (currentRenderable != null)
		{
			propagateInvalidate ((MComponent) currentRenderable);
		}
		else
		{
			propagateInvalidate (aComponent);
		}
	}
	
	public MComponent pushStack (MComponent aRenderable)
	{
		MComponent retVal = aRenderable;
		while (retVal instanceof MContainer &&
			   ((MContainer) retVal).getComponentCount () == 1)
		{
			depthBasedRenderables.push (retVal);
			retVal = ((MContainer) retVal).getComponent (0);
		}
		depthBasedRenderables.push (retVal);
		currentRenderable = (I_Renderable) retVal;
		propagateInvalidate (retVal);
		return retVal;
	}
	
	protected MFrame getContainingFrame (MComponent aComponent)
	{
		MComponent retVal = aComponent;
		
		while (!((retVal = retVal.getParent ()) instanceof MFrame )&&
				 retVal != null);
				 
		return (MFrame) retVal;
	}
			
	
	/**
	* An event was delivered to an MComponent whose I_Renderer has depth
	*
	* @param aActionEvent The MActionEvent
	*/
	public void actionPerformed (MActionEvent aActionEvent)
	{
		int theUniqueReference = session.getUniqueReference ();
		if (uniqueCheckDone ||
			theUniqueReference == -1 ||
			theUniqueReference > uniqueReference)
		{
			if (theUniqueReference != -1)
			{
				uniqueReference = theUniqueReference;
			}
			uniqueCheckDone = true;
			MComponent theComponent = (MComponent) aActionEvent.getSource ();
			if (theComponent == renderable)
			{
				String theActionCommand = aActionEvent.getActionCommand ();
				StringTokenizer theTokenizer = new StringTokenizer (theActionCommand, "_");
				String theToken;
				
				while (theTokenizer.hasMoreTokens ())
				{
					theToken = theTokenizer.nextToken ();
					if (theToken.equals ("pop"))
					{
						popStack ();
						peekStack (theComponent);
					}
					else if (theToken.equals ("peek"))
					{
						peekStack (theComponent);
					}
					else
					{
						lastRenderable = null;
					}
				}
			}
			else if (getContainingFrame (theComponent) == renderable)
			{
				MComponent theCurrentRenderable = theComponent;
				//if (theCurrentRenderable != lastRenderable)
				//{
					I_HasDepth theRenderer = (I_HasDepth) theCurrentRenderable.getRenderer ();
					if (theRenderer.isDeepNavigating (aActionEvent, depthBasedRenderables))
					{
						theCurrentRenderable = pushStack (theCurrentRenderable);
					}
					else if (currentRenderable == theCurrentRenderable &&
							 theRenderer.autoPop (aActionEvent, depthBasedRenderables))
					{
						popStack ();
						peekStack (theCurrentRenderable);
					}
				//}
			}
		}
	} 
	
	/**
	* Set the boolean indicating if the MauiOneEvent variable is being used
	*
	* @param aUseMauiOneEvent MauiOneEvent being used or not
	*/
	public void setUseMauiOneEvent (boolean aMauiOneEvent)
	{
		useMauiOneEvent = aMauiOneEvent;
	}
}