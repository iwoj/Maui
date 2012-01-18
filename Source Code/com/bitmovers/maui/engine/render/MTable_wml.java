// ========================================================================
// CLASS: MTable_wml
// ========================================================================
// CHANGELOG:
//
//++ 201 IW 2001.08.14
// Added "..." suffix to main MTable anchor to indicate to the user to 
// expect deep navigation.+
//

package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Stack;

import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.HasPostValue;
import com.bitmovers.maui.components.foundation.MTable;
import com.bitmovers.maui.components.foundation.Settable;
import com.bitmovers.maui.components.foundation.HasLabel;
import com.bitmovers.maui.components.foundation.MSelectList;
import com.bitmovers.maui.components.foundation.MCheckBox;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.utilities.StringParser;


// ========================================================================
// CLASS: MTable_wml
// ========================================================================

/** WML renderer for the <code>MTable</code> component.
  *
  */

public class MTable_wml extends A_Renderer
                     implements I_HasDepth,
                                I_RendererInitialize,
                                MActionListener,
                                I_SimplePostCard,
                                I_ListGenerator
{
	//protected String [] onPickRef = null;
	protected boolean isDepthBasedRender = false;
	protected int selectedRow = -1;
	protected boolean inBackout = false;
	
	public void initialize (I_Renderable aRenderable, MComponent aComponent, String [] aClientClassification)
	{
		aComponent.addActionListener (this);
		renderable = aRenderable;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: render
	// ---------------------------------------------------------------------------
	
	private String getTableSelectName (MTable aTable)
	{
		StringBuffer retVal = new StringBuffer (generateComponentID (aTable));
		retVal.append ("_TabSelect");
		return retVal.toString ();
	}
	
	private String getTableValue (Vector aRow, int aIndex, boolean aRender)
	{
		String retVal;

		Object theValue = aRow.elementAt (aIndex);
		if (theValue == null)
		{
			retVal = "";
		}
		else if (theValue instanceof I_Renderable && aRender)
		{
			/*I_Renderable theRenderable = (I_Renderable) theValue;
			I_Renderer theRenderer = theRenderable.getRenderer ();
			retVal = (theRenderer instanceof I_UsesListGenerator &&
					  !(theRenderer instanceof I_HasDepth) ?
							((I_UsesListGenerator) theRenderer).render (renderable, this) :
							 theRenderer.render (theRenderable));*/
			retVal = ((I_Renderable) theValue).getRenderer ().render ((I_Renderable) theValue);
		}
		else if (theValue instanceof Settable)
		{
			retVal = ((Settable) theValue).getValue ().toString ();
		}
		else if (theValue instanceof HasLabel)
		{
			retVal = ((HasLabel) theValue).getLabel ();
		}
		else if (theValue instanceof MComponent)
		{
			retVal = ((MComponent) theValue).getName ();
		}
		else
		{
			retVal = theValue.toString ();
		}
		return retVal;
	}
	
	/*public String render (I_Renderable aRenderable)
	{
		//
		//	Simply cause a depth change for this component
		//
	}*/
	
	public I_Renderable [] generateList (I_Renderable aRenderable,
										 Class aListClass)
	{
		return rowToRenderables (aRenderable, aListClass);
		/*int j = 0;
		
		for (int i = 0; i < retVal.length; i++)
		{
			if (aListClass.isInstance (retVal [i]))
			{
				retVal [j++] = retVal [i];
			}
		}
		
		if (j < retVal.length)
		{
			I_Renderable [] theTemp = new I_Renderable [j];
			if (j > 0)
			{
				System.arraycopy (retVal, 0, theTemp, 0, j);
			}
			retVal = theTemp;
		}
		return retVal;*/
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	/** This is where all MTable WML rendering begins.
	  * 
	  */
	  
	public String render (I_Renderable aRenderable)
	{
		MTable theTable = (MTable) aRenderable;
		
		//
		//	Generate a reference to the select list for browsing the table
		//
		String retVal = null;
		if (theTable.getRowCount () > 0)
		{
			retVal = generateSimpleAnchor(aRenderable, MActionEvent.ACTION_SELECTED);
		}
		return (retVal == null ? "" : retVal);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getLabel
	// ----------------------------------------------------------------------
	
	/** This method returns the table's name followed by "..." to indicate 
	  * that deep navigation will be used.
	  * 
	  */
	
	protected String getLabel(I_Renderable aRenderable)
	{
		//++ 201 IW 2001.08.14
		return ((MComponent)aRenderable).getName() + "...";
		//-- 201
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: depthBasedRender
	// ----------------------------------------------------------------------
	
	public String depthBasedRender(I_Renderable aRenderable,
	                               Stack aStack,
	                               String aBackout)
	{
		isDepthBasedRender = true;
		if (inBackout)
		{
			selectedRow = -1;
		}
		inBackout = false;
		String retVal = (selectedRow == -1 ?
								generateTableSelectList (aRenderable).toString () :
								generateTableDetail (aRenderable));
		isDepthBasedRender = false;
		return retVal;
	}
	
	/**
	* Notify the renderer that it is being backed out.  This is so it can do
	* whatever cleanup is necessary
	*
	* @param aRenderable The I_Renderable object
	*/
	public void backout (I_Renderable aRenderable)
	{
		inBackout = true;
	}

	private StringBuffer generateDetail (MTable aTable, Vector aRow, int aIndex)
	{
		String theShortColumnName = aTable.getShortColumnName (aIndex);
		StringBuffer retVal = new StringBuffer (theShortColumnName == null ?
													"" :
													theShortColumnName);
		if (theShortColumnName != null)
		{
			retVal.append (": ");
		}
		retVal.append (getTableValue (aRow, aIndex, true));
		retVal.append (DefaultWmlLayoutRenderer.SEPARATOR);
		return retVal;
	}
	
	private StringBuffer generateTableSelectList (I_Renderable aRenderable)
	{
		MTable theTable = (MTable) aRenderable;
		StringBuffer retVal = new StringBuffer ();
		if (theTable.getRowCount () > 0)
		{
			int theColumnToUse = -1;
			int theColumnCount = theTable.getColumnCount ();
			Vector theRow = theTable.getRow (0);
			
			for (int i = 0; i < theColumnCount && theColumnToUse == -1; i++)
			{
				Object theValue = theRow.elementAt (i);
				if (theValue != null)
				{
					if (theValue instanceof Settable)
					{
						if (! (theValue instanceof MSelectList ||
							   theValue instanceof MCheckBox))
						{
							theColumnToUse = i;
						}
					}
					else if (! (theValue instanceof I_Renderable))
					{
						theColumnToUse = i;
					}
				}
			}
			
			if (theColumnToUse == -1)
			{
				//
				//	None of the columns are good candidates for labeling the rows... so
				//	Just use the first column, but use it's label only.
				//
				theColumnToUse = 0;
			}
			
			if (theColumnToUse != -1)
			{
				//
				//	Create the href's and labels for each select list item.
				//	Use 5 rows.
				//
				int theFirstRow = theTable.getFirstDisplayableRowIndex ();
				int theRowCount = theTable.getRowCount () - theFirstRow;
				/*if (theRowCount > 5)
				{
					theRowCount = 5;
				}*/
				
				String [] theOnPickRef = new String [theRowCount];
				String theValue;
				Vector theData = new Vector ();
				String theComponentID = generateComponentID (theTable);
				String theSelection = null;
				for (int i = 0; i < theRowCount; i++)
				{
					theRow = theTable.getRow (theFirstRow + i);
					theValue = getTableValue (theRow, theColumnToUse, false);
					if (i == 0)
					{
						theSelection = theValue;
					}
					theOnPickRef [i] = "selected_" + i;
					theData.addElement (theValue);
				}
				/*retVal.append (generateCard (getTableSelectName (theTable),
										     theTable.getName (),
										     MSelectList_wml.generateSelectList (theComponentID,
										   									     theData,
										   									     theSelection,
										   									     " ",
										   									     onPickRef)));*/
				retVal.append (MSelectList_wml.generateSelectList (this,
																   theComponentID,
																   theData,
																   theSelection,
																   " ",
																   theOnPickRef,
																   false,
																   null));
				//retVal.append ("\n</p>\n</card>\n");
				
					
			}
		}
		return retVal;
	}
	
	private I_Renderable [] rowToRenderables (I_Renderable aRenderable, Class aFilter)
	{
		MTable theTable = (aRenderable instanceof MTable ?
										(MTable) aRenderable :
										(MTable) ((MComponent) aRenderable).getParent ());
		Vector theRow = theTable.getRow (selectedRow);
		I_Renderable [] retVal = new I_Renderable [theRow.size ()];
		Enumeration theEnumeration = theRow.elements ();
		int i = 0;
		while (theEnumeration.hasMoreElements ())
		{
			Object theComponent = theEnumeration.nextElement ();
			if (theComponent instanceof I_Renderable &&
				(aFilter == null || aFilter.isInstance (theComponent)))
			{
				retVal [i++] = (I_Renderable) theComponent;
			}
		}
		
		if (i < theRow.size ())
		{
			I_Renderable [] theTemp = new I_Renderable [i];
			if (i > 0)
			{
				System.arraycopy (retVal, 0, theTemp, 0, i);
			}
			retVal = theTemp;
		}
		return retVal;
	}
	
	private String generateTableDetail (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ();
		//
		//	Now generate the cards which contain the data
		//
		MTable theTable = (MTable) aRenderable;
		
		int theColumnCount = theTable.getColumnCount ();
		Vector theRow = theTable.getRow (selectedRow);
		for (int i = 0; i < theColumnCount; i++)
		{
			retVal.append (generateDetail (theTable, theRow, i));
		}
		return retVal.toString ();
	}
	
	public void actionPerformed (MActionEvent aMauiEvent)
	{
		MTable theTable = (MTable) aMauiEvent.getSource ();
		selectedRow = theTable.getSelectedRow ();
		theTable.invalidate ();
	}
	
	/**
	* Test if the event indicates that deep navigation is occuring or not.
	*
	* @param aActionEvent The MActionEvent describing the component's event
	*
	* @return Boolean indicating if this is deep navigation or not
	*/
	public boolean isDeepNavigating (MActionEvent aEvent, Stack aStack)
	{
		return (aEvent.getActionCommand ().startsWith ("selected"));
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
		return (selectedRow != -1 ? rowToRenderables (aRenderable, HasPostValue.class) : new I_Renderable [] {aRenderable});
	}
	
	// ---------------------------------------------------------------------------
}