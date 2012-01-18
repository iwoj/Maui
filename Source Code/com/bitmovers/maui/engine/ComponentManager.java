// ======================================================================
// com.bitmovers.maui.engine.ComponentManager
// ======================================================================

package com.bitmovers.maui.engine;

import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.utilities.*;


// ======================================================================
// <<SINGLETON>> CLASS: ComponentManager
// ======================================================================

public class ComponentManager
	implements I_SessionListener
{
	// --------------------------------------------------------------------

  private static ComponentManager theInstance = new ComponentManager();
  private static InheritableThreadLocal applications = new InheritableThreadLocal ();  
  
  private Hashtable componentTable;
  private Hashtable applicationXRef;
  private Hashtable componentXRef;
  
  private boolean initDone = false;

	
	// --------------------------------------------------------------------
	// CONSTRUCTOR: ComponentManager
	// --------------------------------------------------------------------
	
	private ComponentManager()
	{
		this.componentTable = new Hashtable();
		applicationXRef = new Hashtable ();
		componentXRef = new Hashtable ();

		System.err.println(new DebugString("[ComponentManager] - Started."));
	}
	
	public void initialize ()
	{
		if (!initDone)
		{
			HTTPSession.addSessionListener (this);
			initDone = true;
		}
	}

	public static void setApplication (MauiApplication aMauiApplication)
	{
		applications.set (aMauiApplication);
	}

	// --------------------------------------------------------------------
	// CLASS METHOD: getInstance
	// --------------------------------------------------------------------
	
	public static ComponentManager getInstance()
	{
	  return theInstance;
	}


	// --------------------------------------------------------------------
	// METHOD: registerComponent
	// --------------------------------------------------------------------
	
	/** Registers a Component with the component manager and returns the 
	  * componentID assigned to the Component.  This method is normally 
	  * called automatically by the Component itself.  Returns a 'null'
	  * componentID if the component passed is itself 'null'.
	  */
	
	public String registerComponent(MComponent component)
	{
	  String componentID = null;
	  
	  try
	  {
	    componentID = this.calculateComponentID(component);
  	    this.componentTable.put(componentID, component);
  	    MauiApplication theApplication = (MauiApplication) applications.get ();
  	    if (theApplication != null)
  	    {
  	    	bindComponentToApplication (component, theApplication);
  	    }
  	}
  	catch (NullPointerException exception) { }
  	
  	return componentID;
	}


	// --------------------------------------------------------------------
	// METHOD: deRegisterComponent
	// --------------------------------------------------------------------
	
	/** De-registers a Component from the component manager.  This method
	  * is normally called automatically by the Component itself.
	  */
	
	public void deRegisterComponent(MComponent component)
	{
	  if (this.componentTable.contains(component))
	  {
	    this.componentTable.remove(this.calculateComponentID(component));
	  }
	}


	// --------------------------------------------------------------------
	// METHOD: getComponentID
	// --------------------------------------------------------------------
	
	/** Returns the componentID String for the specified Component.  If the
	  * component specified is not 
    */
	
	public String getComponentID(MComponent component)
	{
	  if (this.componentTable.contains(component))
	  {
	    return this.calculateComponentID(component);
	  }
	  else
	  {
	    return null;
	  }
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: isAComponent
	// --------------------------------------------------------------------
	
	/** Returns true or false depending on whether or not the given 
	  * componentID matches a valid component.
	  * 
	  */
	
	public boolean isAComponent(String componentID)
	{
	  if (componentTable.containsKey(componentID))
	  {
	    return true;
	  }
	  else
	  {
	    return false;
	  }
	}


	// --------------------------------------------------------------------
	// METHOD: getComponent
	// --------------------------------------------------------------------
	
	/** Returns a Component based on it's componentID.
	  */
	
	public MComponent getComponent(String componentID)
	{
	  if (this.componentTable.containsKey(componentID))
	  {
	    return (MComponent)this.componentTable.get(componentID);
	  }
	  else
	  {
	    return null;
	  }
	}
	
	
	// --------------------------------------------------------------------
	// METHOD: getComponentClassName
	// --------------------------------------------------------------------
	
	/** getComponentClassName() returns the name of the Class of the
	  * component which possesses the given componentID. Returns null if
	  * the component does not exist.
	  *
	  */
	
	public String getComponentClassName(String componentID)
	{
		MComponent component = null;
		
		if ((component = this.getComponent(componentID)) == null)
		{
			return null;
		}
		else
		{
			return component.getClass().getName();
		}
	}


	// --------------------------------------------------------------------
	// METHOD: calculateComponentID
	// --------------------------------------------------------------------
	
	private String calculateComponentID(MComponent component)
	{
	  // ** Note: this method may want to be updated to provide a different
	  //          form of hashCode in the future.  Perhaps a completely 
	  //          random number or some other calculation.

	  // ** Note: the present implementation of this class assumes that the
	  //          componentID can be caluculated from a component at any time
	  //          (ie. it's not random, but derivable from the Component's
	  //          hashCode or some other device).
	  
	  return Integer.toHexString(component.hashCode());
	}
	
	private void unbindComponentFromApplication (MComponent aComponent)
	{
		Vector theApplicationVector = (Vector) componentXRef.get (aComponent);
		if (theApplicationVector != null)
		{
			theApplicationVector.removeElement (aComponent);
		}
	}
	
	/** Bind a component to a MauiApplication.  This is used for removing
	* components when from the ComponentManager when the application exits
	*
	* @param aComponent The component to bind
	* @param aMauiApplication The Maui application to bind to
	*/
	public void bindComponentToApplication (MComponent aComponent,
											MauiApplication aMauiApplication)
	{
		unbindComponentFromApplication (aComponent);
		Vector theComponents = (Vector) applicationXRef.get (aMauiApplication);
		if (theComponents == null)
		{
			theComponents = new Vector ();
			applicationXRef.put (aMauiApplication, theComponents);
		}
		theComponents.addElement (aComponent);
		componentXRef.put (aComponent, theComponents);
	}

	/**
	* Notification of session creation
	*
	* @param aSessionEvent The event object describing the session 
	*/
	public void sessionCreated (SessionEvent aSessionEvent)
	{
	}
	
	/**
	* Notification of session deletion
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void sessionDeleted (SessionEvent aSessionEvent)
	{
	}
	
	/**
	* Notification of the addition of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationAdded (SessionEvent aSessionEvent)
	{
		if (applicationXRef.get (aSessionEvent.getMauiApplication ()) == null)
		{
			applicationXRef.put (aSessionEvent.getMauiApplication (),
													 new Vector ());
		}
	}
	
	private void removeComponents (MauiApplication theApplication)
	{
		Vector theComponentsVector = (Vector) applicationXRef.get (theApplication);
		if (theComponentsVector != null)
		{
			Object [] theComponents = theComponentsVector.toArray ();
			MComponent theComponent;
			for (int i = 0; i < theComponents.length; i++)
			{
				theComponent = (MComponent) theComponents [i];
				theComponent.finish ();
				componentXRef.remove (theComponent);
			}
		}
		theApplication.finish ();
		/*MComponent [] theComponents = aContainer.getComponents ();
		for (int i = 0; i < theComponents.length; i++)
		{
			if (theComponents [i] instanceof MContainer)
			{
				removeComponents ((MContainer) theComponents [i]);
			}
			aContainer.remove (theComponents [i]);
			theComponents [i].finish ();
		}*/
	}
	
	/**
	* Notification of the removal of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationRemoved (SessionEvent aSessionEvent)
	{
		//
		//	Remove all of the MComponents for this MauiApplication
		//
		MauiApplication theApplication = aSessionEvent.getMauiApplication ();
		removeComponents (theApplication);
		applicationXRef.remove (theApplication);
	}
	// --------------------------------------------------------------------

}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF