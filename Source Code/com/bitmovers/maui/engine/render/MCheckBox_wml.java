// =============================================================================
// com.bitmovers.maui.engine.render.MCheckBox_wml
// =============================================================================

package com.bitmovers.maui.engine.render;

import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;

import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.*;


// =============================================================================
// CLASS: MCheckBox_wml
// =============================================================================

/** The MCheckBox_wml class is the WML renderer for the MCheckBox
  * class.
  *
  */

public class MCheckBox_wml extends A_Renderer
	implements I_HasMultiple,
			   I_HasForwardPrologue,
			   I_ListGenerator,
			   I_UsesListGenerator
			   
{
	private Hashtable checked = new Hashtable (5);
	protected boolean includeComma = false;
	protected int firstIndex;
	protected MContainer container;
	protected I_Renderable [] checkboxes;
	
	/**
	* Initialize the renderer
	*
	* @param aRenderable The I_Renderable object (typically an MComponent, or subclass, or a layout manager)
	* @param aComponent The MComponent associated with I_Renderable (if the I_Renderable is different)
	* @param aClientClassification A String array describing the client 
	*/
	public void initialize (I_Renderable aRenderable, MComponent aComponent, String [] aClientClassification)
	{
		recheckCheckBoxes (aRenderable);
	}
	
	protected void recheckCheckBoxes (I_Renderable aRenderable)
	{
		MCheckBox theCheckBox = (MCheckBox) aRenderable;
		container = theCheckBox.getParent ();
		
		int theIndex = container.getComponentIndex (theCheckBox);
		if (theIndex == 0 ||
			! (container.getComponent (theIndex - 1) instanceof MCheckBox))
		{
			renderable = theCheckBox;
			firstIndex = theIndex;
		}
		checkboxes = getCheckBoxes (aRenderable);
	}
	
	protected I_Renderable [] getCheckBoxes (I_Renderable aRenderable)
	{
		MContainer theContainer = getContainer ((MComponent) aRenderable);
		int theComponentCount = theContainer.getComponentCount ();
		I_Renderable [] theCheckBoxes = new I_Renderable [theComponentCount];
		int theCount = 0;
		MComponent theComponent;
		for (int i = firstIndex;
			 i < theComponentCount &&
			 (theComponent = container.getComponent (i)) instanceof MCheckBox; i++)
		{
			theCheckBoxes [theCount++] = (I_Renderable) theComponent;
		}
		
		I_Renderable [] retVal = new I_Renderable [theCount];
		System.arraycopy (theCheckBoxes, 0, retVal, 0, theCount);
		return retVal;
	}
	
	public I_Renderable [] generateList (I_Renderable aRenderable,
										 Class aListClass)
	{
		recheckCheckBoxes (aRenderable);
		return checkboxes;
	}
	
	protected String getCheckBoxLabel (MCheckBox aCheckBox, String aSeparator, boolean aID)
	{
		StringBuffer retVal = new StringBuffer ();
		
		if (aCheckBox.isChecked ())
		{
			if (includeComma)
			{
				retVal.append (aSeparator);
			}
			else
			{
				includeComma = true;
			}
			
			retVal.append ((aID ? aCheckBox.getWMLSafeComponentID () :
								  aCheckBox.getLabel ()));
		}
		
		return retVal.toString ();
	}
	
	protected String getSummaryLabel (I_Renderable aRenderable, String aSeparator, boolean aID)
	{
		StringBuffer retVal = new StringBuffer ();
		includeComma = false;
		
		for (int i = 0; i < checkboxes.length; i++)
		{
			retVal.append (getCheckBoxLabel ((MCheckBox) checkboxes [i], aSeparator, aID));
		}
		return retVal.toString ();
	}
	
	
	public String render (I_Renderable aRenderable,
						  I_ListGenerator aGenerator)
	{
		StringBuffer retVal = null;
		I_Renderable [] theCheckBoxArray = aGenerator.generateList (aRenderable,
																	MCheckBox.class);
		if (theCheckBoxArray != null &&
			theCheckBoxArray.length > 0)
		{
			MCheckBox theCheckBox = (MCheckBox) theCheckBoxArray [0];
			representativeRenderable = theCheckBox;
			
			retVal = new StringBuffer (" <select name=\"");
			retVal.append (theCheckBox.getWMLSafeComponentID());
			retVal.append ("\" multiple=\"true\" value=\"");
			String theSelected = null;
			for (int i = 0; i < theCheckBoxArray.length; i++)
			{
				if ((theSelected = getSelected ((I_Renderable) theCheckBoxArray [i])) != null)
				{
					retVal.append (theSelected);//getSelected ((I_Renderable) theCheckBoxArray [i]));
					if (i + 1 < theCheckBoxArray.length)
					{
						retVal.append (";");
					}
				}
			}
			retVal.append ("\" >\n");
			
			for (int i = 0; i < theCheckBoxArray.length; i++)
			{
				retVal.append ("  <option value=\"");
				retVal.append (((MCheckBox) theCheckBoxArray [i]).getWMLSafeComponentID ());
				retVal.append ("\">");
				retVal.append (getLabel ((I_Renderable) theCheckBoxArray [i]));
				retVal.append ("</option>\n");
			}
			retVal.append ("</select>\n");				
		}
		return (retVal == null ? "" : retVal.toString ());
	}
	
						  
	/**
	* Get the filter class name for the list generator to use
	*
	* @return The filter list class
	*/
	public Class getFilterClass ()
	{
		return MCheckBox.class;
	}
	
	/** Renders the WML for the MCheckBox component.
	  *
	  */
	
	public String render (I_Renderable aRenderable)
	{
		return render (aRenderable, this);
	}

	protected String getLabel (I_Renderable aRenderable)
	{
		MCheckBox theCheckBox = (MCheckBox) aRenderable;
		String retVal = theCheckBox.getLabel ();
		if  (retVal == null ||
			 retVal.trim ().length () == 0)
		{
			retVal = "Select";
		}
		return retVal;
	}
	
	public String [] getRenderPhases ()
	{
		return A_Renderer.getRenderPhases (MCheckBox_wml.class);
	}
	
	public String getSelected (I_Renderable aRenderable)
	{
		String retVal = null;
		MCheckBox aCheckbox = (MCheckBox) aRenderable;
		if (aCheckbox.isChecked ())
		{
			retVal = aCheckbox.getWMLSafeComponentID ();
		}
		return retVal;
	}

	/**
	* Get the component id's from the request string
	*
	* @param aComponentID	The component ID
	* @param aRequestValue The request value to parse
	*
	* @return The list of component ids
	*/
	public String [] getComponentIDs (String aComponentID, String aRequestValue)
	{
		String [] retVal = new String [checkboxes.length];
		
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = ((MComponent) checkboxes [i]).getWMLSafeComponentID ();
			checked.put (retVal [i], "No");
		}
		
		if (aRequestValue != null)
		{
			StringTokenizer theTokenizer = new StringTokenizer (aRequestValue, ";");
			while (theTokenizer.hasMoreTokens ())
			{
				checked.put (theTokenizer.nextToken (), "Yes");
			}
		}
			
		return retVal;
	}
	/*	headerTokenPresent = false;
		headerToken = aComponentID;
		if (aRequestValue != null)
		{
			
			if (!headerTokenPresent)
			{
				String [] theTemp = new String [retVal.length + 1];
				System.arraycopy (retVal, 0, theTemp, 0, retVal.length);
				theTemp [retVal.length] = aComponentID;
				retVal = theTemp;
			}
		}
		else
		{
			checked = false;
		}
		return (retVal == null ? new String [] {aComponentID} : retVal);
	}*/
	
	/**
	* Get the state data for a given component
	*
	* @param aComponent The component
	* @param aStateData		Any state data that can be gathered
	*
	* @return The state data to associate with the component
	*/
	public String getStateData (String aComponentID, String aStateData)
	{
		return (String) checked.get (aComponentID);
	}
	
	public I_Renderable getRepresentativeRenderable (I_Renderable aRenderable)
	{
		return representativeRenderable;
	}
	
	/**
	*
	* Render the object to the client device
	*
	* @param aRenderable An object which can be rendered (MComponent or Layout)
	*
	* @return The rendered String.
	*/
	public String generateForwardPrologue (I_Renderable aRenderable)
	{
		String retVal = null;
		recheckCheckBoxes (aRenderable);
		if (renderable != null)
		{
			retVal = getSummaryLabel (aRenderable, ";", true);
			if (!retVal.equals (""))
			{
				retVal = setVar (aRenderable, retVal);
			}
		}
		return (retVal == null ? "" : retVal);
	}
	// ---------------------------------------------------------------------------
}
