// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.ResourceDescription
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import java.util.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;


// ======================================================================
// CLASS: ResourceDescription
// ======================================================================

/** The ResourceDescription class encapsulates an HTTP request for a
  * resource (ie. an image). It provides the ResourceManager with enough
  * information to render an image for the requesting client.
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class ResourceDescription
{
  // --------------------------------------------------------------------
	private static final String CLASS_PROPERTY = "class";
	private static final String STATE_PROPERTY = "state";
	private static final String LABEL_PROPERTY = "label";
	private static final String EXTRA_PROPERTY = "extra";
	private static final String PATH_PROPERTY = "path";
	
	private String classValue = null;
	private String stateValue = null;
	private String labelValue = null;
	private String extraValue = null;
	private String pathValue = null;
	
	private String fullStringValue = null;
	private int localHashCode = 0;

  Hashtable properties = null;
  

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public ResourceDescription()
  {
    this.properties = new Hashtable(5);
  }


	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------

	public ResourceDescription(Hashtable properties)
	{
		this.properties = properties;
    
		// If there is a componentID, get and store the class of the
		// component. This will be used to (help) compare ResourceDescription
		// objects using the equals() method.
		{
			String componentID = null;

			if ((componentID = this.getProperty("id")) != null)
			{
		    	classValue = ComponentManager.getInstance().getComponentClassName(componentID);    	
			}
			
			stateValue = getProperty (STATE_PROPERTY);
			labelValue = getProperty (LABEL_PROPERTY);
			extraValue = getProperty (EXTRA_PROPERTY);
			pathValue = getProperty (PATH_PROPERTY);
		}
	}


  // --------------------------------------------------------------------
  // METHOD: setProperty
  // --------------------------------------------------------------------
  
  /** setProperty() sets a property for the ResourceDescription.
    *
    */
  
  public void setProperty(String name, String value)
  {
    try
    {
      this.properties.put(name, value);
      if (name.equals (CLASS_PROPERTY))
      {
      	classValue = value;
      }
      else if (name.equals (STATE_PROPERTY))
      {
      	stateValue = value;
      }
      else if (name.equals (LABEL_PROPERTY))
      {
      	labelValue = value;
      }
      else if (name.equals (EXTRA_PROPERTY))
      {
      	extraValue = value;
      }
      else if (name.equals (PATH_PROPERTY))
      {
      	pathValue = value;
      }
    }
    catch (NullPointerException exception)
    {
      System.out.println(new WarningString("ResourceDescription.setProperty(): a property with the name '" + name + "' had a value of '" + value + "'"));
    }
  }


  // --------------------------------------------------------------------
  // METHOD: getProperty
  // --------------------------------------------------------------------
  
  /** getProperty() gets a property for the ResourceDescription. It will
    * return null if the requested property does not exist.
    *
    */
  
  public String getProperty(String name)
  {
    return (String)this.properties.get(name);
  }


  // --------------------------------------------------------------------
  // METHOD: getPropertyNames
  // --------------------------------------------------------------------
  
  /** getPropertyNames() returns an Enumeration of Strings containing all
    * of the property names currently stored.
    *
    */
  
  public Enumeration getPropertyNames()
  {
    return this.properties.keys();
  }


	// --------------------------------------------------------------------
	// METHOD: equals
	// --------------------------------------------------------------------
  
	/** Overrides java.lang.Object.equals(). It has been overridden in such
	  * a way that it will return true to other ResourceDescription objects
	  * which are close, but not exactly the same. The keys which it will
	  * pay attention to are: class, label, state, and extra.
	  *
	  */
	
	public boolean equals(Object object)
	{
		boolean classCheck = false;
		boolean labelCheck = false;
		boolean stateCheck = false;
		boolean extraCheck = false;

		// We are only interested in ResourceDescription objects...
		try
		{
			ResourceDescription comparee = (ResourceDescription)object;

			// [1] Check to see if the class is the same
			{
				String classA = this.getProperty(ResourceDescription.CLASS_PROPERTY);
				String classB = comparee.getProperty(ResourceDescription.CLASS_PROPERTY);
				
				if (classA == null || classB == null)
				{
					return false;
				}
				else
				{
					if (classA.equals(classB))
					{
						classCheck = true;
					}
				}
			}
			
			// [2] Check to see if the label is the same
			{
				String labelA = this.getProperty(ResourceDescription.LABEL_PROPERTY);
				String labelB = comparee.getProperty(ResourceDescription.LABEL_PROPERTY);
			
				if (labelA == null || labelB == null)
				{
					return false;
				}
				else
				{
					if (labelA.equals(labelB))
					{
						labelCheck = true;
					}
				}
			}

			// [3] Check to see if the state is the same
			{
				String stateA = this.getProperty(ResourceDescription.STATE_PROPERTY);
				String stateB = comparee.getProperty(ResourceDescription.STATE_PROPERTY);
			
				if (stateA == null || stateB == null)
				{
					return false;
				}
				else
				{
					if (stateA.equals(stateB))
					{
						stateCheck = true;
					}
				}
			}
			
			// [4] Check to see if the extra is the same
			{
				String extraA = this.getProperty(ResourceDescription.EXTRA_PROPERTY);
				String extraB = comparee.getProperty(ResourceDescription.EXTRA_PROPERTY);
			
				if (extraA == null || extraB == null)
				{
					return false;
				}
				else
				{
					if (extraA.equals(extraB))
					{
						extraCheck = true;
					}
				}
			}
			
			// [4] If the necessary comparisons were true, the passed
			//     ResourceDescription object is for all intents and
			//     purposes, the same.
			if (classCheck && labelCheck && stateCheck && extraCheck)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (ClassCastException exception)
		{
			return false;
		}
	}

	private String getPrivateProperty (String aKey, String aValue)
	{
		return (aValue == null ? getProperty (aKey) : aValue);
	}
	
	private void getPrivateProperties ()
	{
		classValue = getPrivateProperty(ResourceDescription.CLASS_PROPERTY, classValue);
		labelValue = getPrivateProperty(ResourceDescription.LABEL_PROPERTY, labelValue);
		stateValue = getPrivateProperty(ResourceDescription.STATE_PROPERTY, stateValue);
		extraValue = getPrivateProperty(ResourceDescription.EXTRA_PROPERTY, extraValue);
		pathValue = getPrivateProperty(ResourceDescription.PATH_PROPERTY, pathValue);
	}
		

	// --------------------------------------------------------------------
	// METHOD: hashCode
	// --------------------------------------------------------------------
	
	/** Overrides java.lang.Object.hashCode() because we want the hashcode
	  * to be calculated based on a set criteria.
	  *
	  */
	
	public int hashCode()
	{
		if (localHashCode == 0)
		{
			getPrivateProperties ();
			StringBuffer theString = new StringBuffer (classValue);
			theString.append (labelValue);
			theString.append (stateValue);
			localHashCode = theString.toString ().hashCode ();
		}
		return localHashCode;
	}
	
	private int appendFullName (char [] aBuffer, int aOffset, String aValue, boolean aAddColon)
	{
		int retVal = 0;
		String theValue = (aValue == null ? "null" : aValue);
		retVal = theValue.length ();
		for (int i = 0; i < retVal; i++)
		{
			aBuffer [i + aOffset] = theValue.charAt (i);
		}
		if (aAddColon)
		{
			aBuffer [retVal++ + aOffset] = ':';
		}
		return retVal + aOffset;
	}
	
	public String toString ()
	{
		if (fullStringValue == null)
		{
			getPrivateProperties ();
			char [] theChar = new char [(classValue != null ? classValue.length () + 1 : 5) +
										(stateValue != null ? stateValue.length () + 1 : 5) +
										(labelValue != null ? labelValue.length () + 1 : 5) +
										(extraValue != null ? extraValue.length () + 1 : 5) +
										(pathValue != null ? pathValue.length () : 4)];
										
			int theOffset = appendFullName (theChar, 0, classValue, true);
			theOffset = appendFullName (theChar, theOffset, stateValue, true);
			theOffset = appendFullName (theChar, theOffset, labelValue, true);
			theOffset = appendFullName (theChar, theOffset, extraValue, true);
			theOffset = appendFullName (theChar, theOffset, pathValue,false);
			fullStringValue = new String (theChar);
		}
		return fullStringValue;
	}

  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF