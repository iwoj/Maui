// =============================================================================
// com.bitmoveres.maui.components.MComponent
// =============================================================================

package com.bitmovers.maui.components;

import java.io.*;
import java.util.*;
import java.awt.Dimension;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.htmlcompositor.*;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.logmanager.WarningString;


// =============================================================================
// CLASS: MComponent
// =============================================================================

/** This class is the class which all Maui components must extend.
  * 
  */
  
public abstract class MComponent
           implements I_Renderable, 
                      Serializable
{
	
	
	/** The resource file which all components use to render themselves.
	  * 
	  * @invisible
	  */
	public ResourceManager jarResources = ResourceManager.getInstance();
	
	protected static final DummyComponent DUMMY_COMPONENT = new MComponent.DummyComponent();
	
	protected I_Renderer renderer = null;
	
	/** @invisible
	  */
	public String HTMLTemplate;
	
	/** @invisible
	  */
	public String HTMLParsed;
	
	/** @invisible
	  */
	public String WMLTemplate;
	
	/** @invisible
	  */
	public String WMLParsed;
	
	protected String sessionID = null;
	
	/** The StringParser is used to create the <code>HTMLParsed<code> string from 
	  * the <code>HTMLTemplate</code>.
	  * 
	  * @invisible
	  */
	public StringParser parser = new StringParser();
	
	/** The parent of the object. It may be null for top-level components.
	  */
	protected MContainer parent;
	
	/** The width of the component.
	  */
	protected int width;
	
	/** The height of the component.
	  */
	protected int height;
	
	/** True when the object is visible. An object that is not
	  * visible is not drawn on the screen.
	  */
	protected boolean visible = true;
	
	/** True when the object is enabled. An object that is not
	  * enabled does not interact with the user.
	  */
	protected boolean enabled = true;
	
	/** When locked is set to true, the component may not be modified in 
	  * any way except to set the lock to false.
	  */
	protected boolean locked = false;
	
	/** True when the object is valid. An invalid object needs to
	  * be layed out.
	  */
	protected boolean valid = false;
	
	protected String name;
	
	/** A Vector containing references to all registered MActionListeners.
	  */
	protected Vector actionListeners = new Vector();
	
	private final String componentID = ComponentManager.getInstance().registerComponent(this);
	
	/** A boolean indicating if the application can receive duplicate events or not.
	  */
	private boolean allowDuplicateEvents = true;
	
	private static int uniqueNumber = 0;
	private static final Object synchObject = new Object ();
	protected MComponent rootParent = null;
	private MComponent oldParent;
	private String servletURL = null;
	
	
	// ---------------------------------------------------------------------------
  // METHOD: generateUniqueName
	// ---------------------------------------------------------------------------
	
	/** Returns unique name for a component. A convenience method to generate a unique 
		* name for a component.
	  * 
	  * @invisible
    * 
	  * @returns Unique string for a component name.
	  */
	  
	protected static String generateUniqueName (Object aObject)
	{
		String retVal = null;
		String theClassName = aObject.getClass().getName ();
		int theIndex = theClassName.lastIndexOf (".");
		theClassName = (theIndex == -1 ? theClassName : theClassName.substring (theIndex + 1));
		synchronized (synchObject)
		{
			retVal = theClassName + "_" + uniqueNumber++;
		}
		return retVal;
	}
  
  // ---------------------------------------------------------------------------
  // METHOD: CONSTRUCTOR
	// ---------------------------------------------------------------------------
	
	/**	Empty constructor for convenience.
		*/
	public MComponent ()
	{
		//servletURL = ServerConfigurationManager.getInstance ().getProperty (ServerConfigurationManager.MAUI_SERVLET_URL);
	}
	
	
	// ---------------------------------------------------------------------------
  // METHOD: generateUniqueName
	// ---------------------------------------------------------------------------
	
	/** Generates a unique name.
		* @invisible
    * 
	  */
	  
	protected void generateUniqueName ()
	{
		name = generateUniqueName (this);
	}
  
  
	// ---------------------------------------------------------------------------
  // METHOD: setAllowDuplicateEvents
	// ---------------------------------------------------------------------------
	
  /** Sets the boolean indicating whether this application can receive duplicate
    * events or not.
    *
    * @param aAllowDuplicateEvents boolean indicating duplicates allowed or not.
    * 
    */
    
  public void setAllowDuplicateEvents (boolean aAllowDuplicateEvents)
  {
  	allowDuplicateEvents = aAllowDuplicateEvents;
  }
  
  
	// ---------------------------------------------------------------------------
  // METHOD: getAllowDuplicateEvents
	// ---------------------------------------------------------------------------
	
  /** Returns <code>true</code> indicating duplicate events are allowed, 
  	* <code>false</code> otherwise.
    *
    * @return The boolean indicating a duplicate event.
    * 
    */
    
  public boolean getAllowDuplicateEvents ()
  {
  	return allowDuplicateEvents;
  }
  
  
	// ---------------------------------------------------------------------------
  // METHOD: getComponentID
	// ---------------------------------------------------------------------------
	
	/** Returns a componentID.
		* @invisible    
	  */
	  
	public final String getComponentID()
	{
	  return this.componentID;
	}
  
	
	// ---------------------------------------------------------------------------
	// METHOD: getWMLSafeComponentID
	// ---------------------------------------------------------------------------
	
	/** Returns a componentID as a <code>String</code>, prefixed with "IDz" for WML.
		* @invisible
    * 
	  */
	  
	public final String getWMLSafeComponentID ()
	{
		return "IDz" + componentID;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: getServletURL
	// ---------------------------------------------------------------------------
	
	/** Returns <code>URL</code> of a <code>Servlet</code> as a <code>String</code>, or
		* a <code>null</code> value.
    * 
	  */
	
	public String getServletURL ()
	{
		if (servletURL == null)
		{
			MComponent theComponent = getRootParent ();
			if (theComponent instanceof MauiApplication)
			{
				servletURL = theComponent.getServletURL ();
			}
		}
		return (servletURL == null ? "/" : servletURL);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: resetServletURL
	// ---------------------------------------------------------------------------
	
	/** Sets the <code>Servlet URL</code> as <code>null</code> value to enable reset.
    * 
	  */
	
	public void resetServletURL ()
	{
		servletURL = null;
	}
  
	
	// ---------------------------------------------------------------------------
	// METHOD: getRootParent
	// ---------------------------------------------------------------------------
	
	/** Returns root container. This is a utility method and with a properly 
	  * constructed component hierarchy, this should be a <code>MauiApplication</code>. 
	  * Finds the component that has no parent and returns its <code>sourceComponent()</code>.
	  *
	  *
	  * @param sourceComponent the <code>MComponent</code> that contains the root parent.
	  * @return The root parent.
	  */
	
	public static MComponent getRootParent(MComponent sourceComponent)
	{
		if (sourceComponent.getParent() == null)
		{
			return sourceComponent;
		}
		else
		{
			return MComponent.getRootParent(sourceComponent.getParent());
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getRootParent
	// ---------------------------------------------------------------------------

	/** Returns the root parent in this component's component tree.  This is a a 
	  * non-static version of <code>getRootParent()</code>.
	  *
	  * @return The root parent.
    * 
	  */
	  
	public MComponent getRootParent ()
	{
		if (rootParent == null ||
			! (rootParent instanceof MauiApplication))
		{
			rootParent = getRootParent (this);
		}
		return rootParent;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: resetRootParent
	// ---------------------------------------------------------------------------

	/** Sets the value to <code>null</code> for reset.
		* @invisible
    * 
	  */
	  
	public void resetRootParent ()
	{
		rootParent = null;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getName
	// ---------------------------------------------------------------------------

	/** Returns the name of the component.
	  * 
	  * @return This component's name.
	  * 
	  */
	  
  public String getName() 
  {
      return name;
  }
  
  
  // ---------------------------------------------------------------------------
	// METHOD: setName
	// ---------------------------------------------------------------------------
	
  /** Sets the name of the component to the specified string.
    * 
    * @param <code>name</code> the string that is to be this 
    * component's name.
    * 
    */
    
  public void setName(String name) 
  {
  	if(!locked)
		{
      this.name = name;
		}
  }
  
  
  // ---------------------------------------------------------------------------
	// METHOD: getParent
	// ---------------------------------------------------------------------------
	
	/** Returns the parent of this component. Returns <code>null</code> if none 
	  * exists.
	  * 
	  * @return The parent container of this component.
	  * 
	  */
	  
	public MContainer getParent() 
	{
		return parent;
	}
  
  
  // ---------------------------------------------------------------------------
	// METHOD: setParent
	// ---------------------------------------------------------------------------
	
	/** Sets the parent of this component.
	  * 
	  * @param newParent the new parent container of this component.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void setParent(MContainer newParent) 
	{
		parent = newParent;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: isValid
	// ---------------------------------------------------------------------------

	/** Determines whether this component is valid. Components are 
	  * invalidated when they are first shown on the screen.
	  * 
	  * @return <code>true</code> if the component is valid; <code>false</code> 
	  * otherwise.
	  * 
	  * @invisible
    * 
	  */
	
	public boolean isValid() 
	{
		return valid;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: isVisible
	// ---------------------------------------------------------------------------
	
	/** Determines whether this component is visible. Components are 
	  * initially visible, with the exception of top level components such 
	  * as <code>Frame</code> objects.
	  * 
	  * @return <code>true</code> if the component is visible; 
	  * <code>false</code> otherwise.
	  * 
	  */
	  
	public boolean isVisible() 
	{
		boolean parentIsVisible = true;
		
		if (this.parent != null)
		{
			parentIsVisible = this.parent.isVisible();
		}
		
		if (this.visible == true && parentIsVisible == false)
		{
			this.visible = false;
		}
		
		return this.visible;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: isShowing
	// ---------------------------------------------------------------------------
	
	/** Determines whether this component is showing on screen. This means 
	  * that the component must be visible, and it must be in a container 
	  * that is visible and showing.
	  * 
	  * @return <code>true</code> if the component is showing; 
	  * <code>false</code> otherwise.
	  * 
	  */
	  
	public boolean isShowing()
	{
		if (visible) 
		{
			MContainer parent = this.parent;
			return (parent == null) || parent.isShowing();
		}
		return false;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: isEnabled
	// ---------------------------------------------------------------------------
	
	/** Determines whether this component is enabled. An enabled component 
	  * can respond to user input and generate events. Components are 
	  * enabled initially by default. A component may be enabled or disabled by 
	  * calling its <code>setEnabled</code> method.
	  * 
	  * @return <code>true</code> if the component is enabled; 
	  * <code>false</code> otherwise.
	  * 
	  */
	  
	public boolean isEnabled() 
	{
		return enabled;
	}
	
	
  // ---------------------------------------------------------------------------
	// METHOD: setEnabled
	// ---------------------------------------------------------------------------
	
	/** Enables or disables this component, depending on the value of the 
	  * parameter <code>b</code>. An enabled component can respond to user 
	  * input and generate events. Components are enabled initially by default.
	  * 
	  * @param     <code>b</code> if <code>true</code>, this component is 
	  *            enabled; otherwise this component is disabled.
	  * 
	  */
	  
	public void setEnabled(boolean b) 
	{
		if(!locked)
		{
	  	if(enabled != b) 
	  	{
				enabled = b;
			}
		}
	}


	// ---------------------------------------------------------------------------
	// METHOD: isLocked
	// ---------------------------------------------------------------------------
	
	/** Determines whether this component is locked. An locked component 
	  * may not be modified in any way until its <code>setLocked</code> 
	  * method is called with <code>false</code>.
	  * 
	  * @return <code>true</code> if the component is locked; 
	  * <code>false</code> otherwise.
	  * 
	  */
	 
	public boolean isLocked()
	{
		return locked;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setLocked
	// ---------------------------------------------------------------------------
	
	/** Locks or unlocks this component, depending on the value of the 
	  * parameter <code>b</code>. A locked component cannot be modified in any way.
	  * Components are not locked by default.
	  * 
	  * @param     <code>b</code> if <code>true</code>, this component is 
	  *            locked; otherwise this component is not locked.
	  * 
	  */
	  
	public void setLocked(boolean b) 
	{
		if(locked != b) 
		{
			locked = b;
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: setVisible
	// ---------------------------------------------------------------------------
	
    /** Shows or hides this component depending on the value of parameter 
      * <code>b</code>.
      *
      * @param <code>b</code> if <code>true</code>, shows this component; 
      * otherwise, hides this component.
      *
      */
      
    public void setVisible(boolean b) 
    {
    	if(!locked)
      {
  			if (visible != b)
  			{
  				visible = b;
  				invalidate();
  			}
		  }
    }
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getSize
	// ---------------------------------------------------------------------------
	
	/** Returns the size of this component in the form of a 
	  * <code>Dimension</code> object. The <code>height</code> 
	  * field of the <code>Dimension</code> object contains 
	  * this component's height, and the <code>width</code> 
	  * field of the <code>Dimension</code> object contains 
	  * this component's width.
	  * 
	  * @return A <code>Dimension</code> object that indicates the 
	  * size of this component.
	  * 
	  */

	public Dimension getSize()
	{
		return new Dimension(width, height);
	}


	// ---------------------------------------------------------------------------
	// METHOD: setSize
	// ---------------------------------------------------------------------------
	
	/** Resizes this component so that it has width <code>d.width</code> 
	  * and height <code>d.height</code>.
	  * 
	  * @param <code>d</code> the <code>Dimension</code> specifying the new size 
	  * of this component.
	  * 
	  */
	
	public void setSize(Dimension d) 
	{
		if(!locked)
		{
			boolean resized = (this.width != d.width) || (this.height != d.height);
			
			if(resized) 
			{
				this.width = d.width;
				this.height = d.height;
				
				if(resized)
				{
					invalidate();
				}
			}
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: validate
	// ---------------------------------------------------------------------------
	
	/** Ensures that this component has a valid layout.  This method is
	  * primarily intended to operate on instances of <code>Container</code>.
	  * 
	  * @invisible
    * 
	  */
	  
	public void validate()
	{
		if(!valid)
		{
			//doLayout();
			valid = true;
		}
	}
	
	
  // ---------------------------------------------------------------------------
	// METHOD: invalidate
	// ---------------------------------------------------------------------------
	
  /** Invalidates this component. This component and all parents
    * above it are marked as needing to be laid out. Method is called often, therefore
    * needs to be execute quickly.
    * 
	  * @invisible
    * 
    */
    
  public void invalidate()
  {
	  // For efficiency, propagate invalidate() upwards only if
	  // some other component hasn't already done so first.
		valid = false;
		if(parent != null && parent.isValid()) 
		{
			parent.invalidate();
		}
  }
    
	// ---------------------------------------------------------------------------
	// METHOD: paramString
	// ---------------------------------------------------------------------------
	
	/** Returns the parameter string representing the state of this 
	  * component. This string is useful for debugging. 
	  *
	  * @return    The parameter string of this component.
	  *
	  */
	
	protected String paramString() 
	{
		String str = (name != null? name : "") + "," + width + "x" + height;
		if (!valid) 
		{
			str += ",invalid";
		}
		if (!visible) 
		{
		    str += ",hidden";
		}
		if (!enabled) 
		{
		    str += ",disabled";
		}
		return str;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: toString
	// ---------------------------------------------------------------------------
	
	/** Returns a string representation of this component and its values.
	  *
	  * @return    A string representation of this component.
	  *
	  */
	  
	public String toString() 
	{
		return getClass().getName() + "[" + paramString() + "]";
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: addActionListener
	// ---------------------------------------------------------------------------

	/** This method is used for registering <code>MActionListeners</code>.
	  * 
	  */

	public void addActionListener(MActionListener listener)
	{
		if (listener != null && !this.actionListeners.contains(listener))
		{
			this.actionListeners.addElement(listener);
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: removeActionListener
	// ---------------------------------------------------------------------------
	
	/** This method is used for deregistering <code>MActionListeners</code>.
	  * 
	  */
	
	public void removeActionListener(MActionListener listener)
	{
		this.actionListeners.removeElement(listener);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: dispatchActionEvent
	// ---------------------------------------------------------------------------
	
	/** Causes the given <code>MActionEvent</code> to be published to all 
	  * <code>MActionEventListeners</code>.
	  * 
	  * @invisible
    * 
	  */
	
	public void dispatchActionEvent(MActionEvent event)
	{
		Enumeration listeners = this.actionListeners.elements();
		
		while (listeners.hasMoreElements())
		{
			MActionListener listener = (MActionListener)listeners.nextElement();
			if (!event.isConsumed () || listener instanceof AlwaysNotify)
			{
				listener.actionPerformed(event);
			}
	  	}
	  	
	  	// Dispatch this event to the maui application, unless these are maui application
	  	// events (we are prejudiced against infinite loops).
	  	if (getRootParent() instanceof MauiApplication && !(this instanceof MauiApplication))
	  	{
	  		((MauiApplication)rootParent).dispatchActionEvent (event);
	  		//((MauiApplication)rootParent).actionPerformed(event);
	  	}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: createEvent
	// ---------------------------------------------------------------------------
	
	/** This method should be overridden by subclasses to update the component's 
	  * state (if necessary) and create the appropriate corresponding event. If
	  * no event is dispatched as a result of the new state data, the consume() method 
	  *	should be called on the <code>MauiEvent</code> object before returning.
	  *
	  * @param stateData intended for overriding in subclasses.
	  * @invisible
    * 
	  */

	public MauiEvent createEvent(String stateData)
	{
		MActionEvent event = new MActionEvent(this, "");
		event.consume();
		return event;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: fillParserValues
	// ----------------------------------------------------------------------
	
	/** Provides all components with a basic set of variables for use in their 
	  * <code>fillParserValues()</code> methods. This method should be called at the start 
	  * of all components' <code>fillParserValues()</code> methods.
	  * 
	  * @invisible
    * 
	  */
	  
	public void fillParserValues()
	{
		if (sessionID == null)
		{
			MComponent theParent = getRootParent ();
			sessionID = (theParent instanceof MauiApplication ?
								((MauiApplication) theParent).getSessionID () :
								"Unknown");
		}
		oldParent = rootParent;
		
		parser.setVariable("componentID", getComponentID());
		parser.setVariable ("safeComponentID", getWMLSafeComponentID ());
		parser.setVariable("servletURL", getServletURL ());
		parser.setVariable ("sessionID", sessionID);
		parser.setVariable("name", getName());
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getParser
	// ----------------------------------------------------------------------
	
	/** Returns the string parser for this object.
	  * 
	  * @invisible
    * 
	  */
	  
	public StringParser getParser()
	{
		return parser;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getRenderer
	// ---------------------------------------------------------------------------
	
	/** Returns the <code>I_Renderer</code> for this component (if the component is renderable).
	  * Otherwise, returns <code>null</code> value.
	  *
	  * @return The <code>I_Renderer</code> object.
	  * 
	  * @invisible
    * 
	  */
	  
	public I_Renderer getRenderer ()
	{
		return getRenderer (this);
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getRenderer
	// ---------------------------------------------------------------------------
	
	/** Allows components to specify an alternative component path for getting the renderer.
	  * This seems like an odd construct, but for some of the specialized panes
	  * (e.g. TabbedPane, ExpandPane) the navigation buttons aren't strictly part of the component
	  * hierarchy. Rather their containing panes should be used as the reference.
	  *
	  * @param aComponent the component to pass for locating the root.
	  * 
	  * @invisible
    * 
	  */
	  
	public I_Renderer getRenderer (MComponent aComponent)
	{
		if (this instanceof I_Renderable)
		{
			if (renderer == null)
			{
				//
				//	The renderer hasn't been initialized yet.  So initialize it, and then
				//	start rendering
				//
				CompositionManager theCompositionManager = CompositionManager.getInstance ();
				renderer = theCompositionManager.getRenderer ( (I_Renderable) this,
																aComponent);
			}
		}
		return renderer;
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: render
	// ---------------------------------------------------------------------------
	
	/** Called during rendering.
	  * 
	  * @invisible
	  * 
	  */
	  
	public String render ()
	{
		if (renderer == null)
		{
			getRenderer ();
		}
		return renderer.render ((I_Renderable) this);
	}
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	// INNER CLASS: DummyComponent
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	
	/** @invisible
	  * 
	  */
	  
	protected static class DummyComponent extends java.awt.Component
	{
		
		
		// -------------------------------------------------------------------------
		// CONSTRUCTOR
		// -------------------------------------------------------------------------

	  public DummyComponent()
	  {
	    super();
	  }
	  
	  
	}

	
	
	// ---------------------------------------------------------------------------
	// METHOD: getContainedMSettableComponents
	// ---------------------------------------------------------------------------
	
	/** Returns a vector of all the visible, enabled <code>MSettable</code> components in the 
	  * given container (and all its subcontainers).
	  *
	  * @param componentVector the target return vector.
	  * 
	  * @param rootContainer the starting container to search.
	  * 
	  * @invisible
	  * 
	  */
	  
	public static void getContainedMSettableComponents(Vector componentVector, MContainer rootContainer)
	{
		MComponent[] components = rootContainer.getComponents();
		for (int i = 0; i < components.length; i++)
		{
			if (components[i] instanceof MSettable && components[i].isVisible() && components[i].isEnabled())
			{
				componentVector.addElement(components[i]);
			}
			if (components[i] instanceof MContainer)
			{
				MSettable.getContainedMSettableComponents(componentVector, (MContainer)components[i]);
			}
		}
	}
	
	
	//++ 405 JL 2001.09.21
	// ---------------------------------------------------------------------------
	// METHOD: getContainedSettableComponents
	// ---------------------------------------------------------------------------
	
	/** Returns a vector of all the visible, enabled <code>Settable</code> components in the 
	  * given container (and all its subcontainers).
	  *
	  * @param componentVector the target return vector.
	  * 
	  * @param rootContainer the starting container to search.
	  * 
	  * @invisible
	  * 
	  */
	  
	public static void getContainedSettableComponents(Vector componentVector, MContainer rootContainer)
	{
		MComponent[] components = rootContainer.getComponents();
		for (int i = 0; i < components.length; i++)
		{
			if (components[i] instanceof Settable && components[i].isVisible() && components[i].isEnabled())
			{
				componentVector.addElement(components[i]);
			}
			if (components[i] instanceof MContainer)
			{
				getContainedSettableComponents(componentVector, (MContainer)components[i]);
			}
		}
	}
	//--
	
	
	// ---------------------------------------------------------------------------
	// METHOD: getContainedHasPostValueComponents
	// ---------------------------------------------------------------------------
	
	/** Returns a vector of all the visible, enabled <code>HasPostValue</code> components in the 
	  * given container (and all its subcontainers).
	  *
	  * @param componentVector target return vector.
	  * 
	  * @param rootContainer the starting container to search.
	  * 
	  * @invisible
	  * 
	  */
	  
	public static void getContainedHasPostValueComponents(Vector componentVector, MContainer rootContainer)
	{
		MComponent[] components = rootContainer.getComponents();
		for (int i = 0; i < components.length; i++)
		{
			if (components[i] instanceof HasPostValue && components[i].isVisible() && components[i].isEnabled())
			{
				componentVector.addElement(components[i]);
			}
			if (components[i] instanceof MContainer)
			{
				MSettable.getContainedMSettableComponents(componentVector, (MContainer)components[i]);
			}
		}
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: exiting
	// ---------------------------------------------------------------------------
	
	/** Notifys the <code>MComponent</code> that it is exiting.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void exiting ()
	{
		doExiting ();
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: doExiting
	// ---------------------------------------------------------------------------
	
	/** Intended for overriding in subclasses.
	  * 
	  * @invisible
	  * 
	  */
	  
	public void doExiting () {}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: finish
	// ---------------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	public void finish ()
	{
		finalize ();
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: finalize
	// ---------------------------------------------------------------------------
	
	/** @invisible
	  * 
	  */
	  
	protected void finalize ()
	{
		System.out.println ("Removing component " + componentID + " from ComponentManager");
		ComponentManager.getInstance ().deRegisterComponent (this);
		ResourceManager.getInstance ().removeCrossReference (this);
		HTMLTemplate = null;
		HTMLParsed = null;
		WMLTemplate = null;
		WMLParsed = null;
		if (renderer != null)
		{
			renderer.finish ();
			renderer = null;
		}
	}
	// ---------------------------------------------------------------------------
}

// =============================================================================
// Copyright (c) 1999 Bitmovers Communications Inc.                          eof