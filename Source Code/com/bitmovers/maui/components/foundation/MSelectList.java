// =============================================================================
// com.bitmovers.maui.components.foundation.MSelectList
// =============================================================================

package com.bitmovers.maui.components.foundation;

import java.util.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.render.I_Renderable;
import com.bitmovers.maui.events.*;


// ========================================================================
// CLASS: MSelectList                            (c) 2001 Bitmovers Systems
// ========================================================================

/** This class represents a pull-down select list type component, or the 
  * closest approximation on any supported device.
  * 
  */

public class MSelectList extends MSettable
                      implements HasPostValue,
                                 HasSelectList
{
	
	
	public static final String ACTION_NEW_SELECTION = "new selection";
	
	private static final String base = "MSelectList";
	
	private static int nameCounter = 0;
	
	private final int myCounterValue = MSelectList.nameCounter++;
	
	private boolean isSavable = true;
	private String indentation = "   ";
	private boolean autoRefresh = false;
	private String selectListName;
	private Vector selectListOptions = new Vector();
	private Hashtable selectListTable = new Hashtable ();
	private String defaultOption = "";
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	/** This method creates a select list from a given 
	  * <code>com.bitmovers.utilities.SelectList</code> object.
	  *
	  * @param com.bitmovers.utilities.SelectList The SelectList object to use.
	  * 
	  * @invisible
	  * 
	  * @deprecated
	  * 
	  */
	
	public MSelectList(com.bitmovers.utilities.SelectList otherSelectList)
	{
		name = MSelectList.base + myCounterValue;
		
		selectListName = otherSelectList.getName();
		defaultOption = otherSelectList.getDefaultOption();
		setValue(otherSelectList.getSelected());
		selectListOptions = otherSelectList.getSelectListOptions();
		
		Object [] theEntries = selectListOptions.toArray ();
		for (int i = 0; i < theEntries.length; i++)
		{
			String [] theEntry = (String []) theEntries [i];
			selectListTable.put (theEntry [0], theEntry);
		}
	}
  	
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------
	
	/** This method creates a select list from a given object array.
	  * Each object element's <code>toString()</code> value is used as both 
	  * the value and the label of the corresponding select list item.
	  * 
	  * @param items The array of objects to use as the select list items.
	  * 
	  * @deprecated
	  * 
	  */
	
	public MSelectList(Object[] items)
	{
		this(items, false);
	}
	
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------
	
	/** This method creates a select list from a given vector. Each element
	  * in the vector may be either an object or a two-element object 
	  * array. If a vector element contains an <code>Object</code>, 
	  * its <code>toString()</code> value will be used for the 
	  * corresponding select list item value and label.<p>
	  * 
	  * If a vector element contains an <code>Object[]</code>, the 
	  * <code>toString()</code> value of the first element in the object 
	  * array will be used for the corresponding select list item's 
	  * value. The second object array element will be used 
	  * for the label. If there is only one element, the first element will 
	  * be used for both the value and label.
	  * 
	  * For example:<p>
	  * 
	  * <pre>
	  * Vector favouriteMuppets = new Vector();
	  * favouriteMuppets.addElement(new String[] {"Frog", "Kermit"});
	  * favouriteMuppets.addElement(new String[] {"Pig", "Miss Piggy"});
	  * favouriteMuppets.addElement(new String[] {"Bear", "Fozzie"});
	  * favouriteMuppets.addElement("Chef");
	  * 
	  * MSelectList mySelectList = new MSelectList(favouriteMuppets);
	  * </pre>
	  * 
	  * The resulting values and labels within <code>mySelectList</code>
	  * would look like this:<p>
	  * 
	  * <pre>
	  * Value     Label
	  * --------------------
	  * Frog      Kermit
	  * Pig       Piggy
	  * Bear      Fozzie
	  * Chef      Chef
	  * </pre>
	  * 
	  * @param vector The <code>Vector</code> object to use.
	  *
	  */
	
	public MSelectList(Vector items)
	{
		name = MSelectList.base + myCounterValue;
		
		Enumeration elements = items.elements();
		
		while (elements.hasMoreElements())
		{
			Object element = elements.nextElement();
	  	String theValue = "";
			String label = "";
			
			// Try to handle this element as an object array.
			try
			{
				Object[] elementArray = (Object[])element;
				
				theValue = elementArray[0].toString();
				label = elementArray[1].toString();
		  }
		  // This object array only has one element.
			catch (ArrayIndexOutOfBoundsException e)
			{
				label = theValue;
			}
		  // This element is not an object array.
		  catch (ClassCastException e)
		  {
				theValue = element.toString();
				label = theValue;
		  }
			
		  addElement (theValue, label);
		}
		
		String[] firstItem = (String[])selectListOptions.elementAt(0);
		setValue(firstItem[0]);
	}
	
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------
	
	/** This method creates a select list from a given object array, 
	  * optionally setting the values of items to an automatic numerical 
	  * range (1 to <code>stringArray.length</code>). <p>
	  * 
	  * For example:<p>
	  * 
	  * <pre>
	  * String[] favouriteMuppets = {"Kermit", "Piggy", "Fozzie", "Chef"};
	  * MSelectList mySelectList = new MSelectList(favouriteMuppets, true);
	  * </pre>
	  * 
	  * The resulting values and labels within <code>mySelectList</code>
	  * would look like this:<p>
	  * 
	  * <pre>
	  * Value     Label
	  * --------------------
	  * 1         Kermit
	  * 2         Piggy
	  * 3         Fozzie
	  * 4         Chef
	  * </pre>
	  * 
	  * @param items The array of objects to use as the select list items.
	  * 
	  *
	  */
	
	public MSelectList(String[] items)
	{
		this (items, false);
	}
	
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------
	
	/** This method creates a select list from a given string array, 
	  * optionally setting the values of items to an automatic numerical 
	  * range (1 to <code>stringArray.length</code>). <p>
	  * 
	  * For example:<p>
	  * 
	  * <pre>
	  * String[] favouriteMuppets = {"Kermit", "Piggy", "Fozzie", "Chef"};
	  * MSelectList mySelectList = new MSelectList(favouriteMuppets, true);
	  * </pre>
	  * 
	  * The resulting values and labels within <code>mySelectList</code>
	  * would look like this:<p>
	  * 
	  * <pre>
	  * Value     Label
	  * --------------------
	  * 1         Kermit
	  * 2         Piggy
	  * 3         Fozzie
	  * 4         Chef
	  * </pre>
	  * 
	  * @param items The array of objects to use as the select list items.
	  * 
	  * @param generateNumericalValues If true, each value will have a 
	  *                                numerical value equal to its index  + 1.
	  *
	  */
	
	public MSelectList(String[] items, boolean generateNumericalValues)
	{
		name = MSelectList.base + myCounterValue;
		
		if (generateNumericalValues)
		{
			String[][] generatedItems = new String[items.length][2];
			
			for (int i = 0; i < items.length; i++)
			{
				generatedItems[i][0] = i + 1 + "";
				generatedItems[i][1] = items[i];
			}
			
			setItems(generatedItems);
		}
		else
		{
			setItems(items);
		}
	}
	
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------
	
	 /* OLD JAVADOCS
	  * 
	  * This method creates a select list from a given object array, 
	  * optionally setting the values of items to an automatic numerical 
	  * range (1 to <code>stringArray.length</code>). <p>
	  * 
	  * For example:<p>
	  * 
	  * <pre>
	  * String[] favouriteMuppets = {"Kermit", "Piggy", "Fozzie", "Chef"};
	  * MSelectList mySelectList = new MSelectList(favouriteMuppets, true);
	  * </pre>
	  * 
	  * The resulting values and labels within <code>mySelectList</code>
	  * would look like this:<p>
	  * 
	  * <pre>
	  * Value     Label
	  * --------------------
	  * 1         Kermit
	  * 2         Piggy
	  * 3         Fozzie
	  * 4         Chef
	  * </pre>
	  * 
	  * @param items The array of objects to use as the select list items.
	  * 
	  * @param  generateNumericalValues  If true, each value will have a 
	  *                                  numerical value equal to its index 
	  *                                  + 1.
	  *
	  */
	  
	  
	/** Provided for backwards compatibility with the old "embedded" 
	  * multi-dimensional object array constructors.
	  * 
	  * @deprecated  Use one of the Vector, String[] or String[][] 
	  *              constructors instead.
	  *
	  */
	  
	public MSelectList(Object[] items, boolean generateNumericalValues)
	{
		name = MSelectList.base + myCounterValue;
		
		if (generateNumericalValues)
		{
			String[][] generatedItems = new String[items.length][2];
			
			for (int i = 0; i < items.length; i++)
			{
				generatedItems[i][0] = i + 1 + "";
				generatedItems[i][1] = ((Object[])items[i])[1].toString();
			}
			
			setItems(generatedItems);
		}
		else
		{
			String[][] multiArrayItems = new String[items.length][2];
			
			for (int i = 0; i < items.length; i++)
			{
				multiArrayItems[i][0] = ((Object[])items[i])[0].toString();
				multiArrayItems[i][1] = ((Object[])items[i])[1].toString();
			}
			
			setItems(multiArrayItems);
		}
	}
	
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------
	
	/** This constructor creates a numbered select list using the given
	  * numerical range. Item values and labels will be identical.
	  * 
	  * @param start The starting number.
	  *
	  * @param end The ending number.
	  *
	  */
	
	public MSelectList(int start, int end)
	{
		name = MSelectList.base + myCounterValue;
		
		for (int i=start; i<=end; i++)
		{
			String theValue = i + "";
			String label = i + "";
			
			addElement (theValue, label);
		}
		
		String[] firstItem = (String[])selectListOptions.elementAt(0);
		setValue(firstItem[0]);
	}
	
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR
	// --------------------------------------------------------------------
	
	/** This method creates a select list from a given 
	  * <code>Hashtable</code>, where a hashtable element's key becomes a 
	  * select list item value and that hashtable element's value becomes
	  * the select list item label.<p>
	  * 
	  * Note that constructing an <code>MSelectList</code> in this manner
	  * (i.e. with a hashtable) precludes you from specifying the order in 
	  * which items appear. This constructor is made available as a 
	  * convenience to application developers, although it should be noted
	  * that for long select lists, end users will find ordered
	  * lists (alphabetical or otherwise) significantly more usable.
	  * 
	  * @param hashtable The <code>Hashtable</code> object to use.
	  *
	  */
	
	public MSelectList(Hashtable hashtable)
	{
		name = MSelectList.base + myCounterValue;
		Enumeration keys = hashtable.keys();
		
		while (keys.hasMoreElements())
		{
			String theValue = (String)keys.nextElement();
			String label = (String)hashtable.get(theValue);
			
			addElement (theValue, label);
		}
		
		String[] firstItem = (String[])selectListOptions.elementAt(0);
		setValue(firstItem[0]);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: setItems
	// ----------------------------------------------------------------------
	
	/** Sets the select list items with a string array. This assumes
	  * that each select list item has identical values and labels.
	  * 
	  * @param items String array object from which to create a select list.
	  */
  
	public void setItems(String[] items)
	{
		selectListOptions = new Vector();
		selectListTable = new Hashtable ();
		
		int length = items.length;
		
		try
		{
			for (int i=0; i < length; i++)
			{
				addElement(items [i]);
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// end of array
		}
		
		String[] firstItem = (String[])selectListOptions.elementAt(0);		
		setValue(firstItem[0]);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: setItems
	// ----------------------------------------------------------------------
	
	/** Sets the value/label pair data in the select list. The first
	  * dimension of this multidimensional array represents rows of items. The 
	  * second dimension has exactly two elements: [0] is the given item's value and 
	  * [1] is its label.
	  * 
	  * @param items A two dimensioned string array from which to create a select list.
	  */
  
	public void setItems(String[][] items)
	{
		selectListOptions = new Vector();
		selectListTable = new Hashtable ();
		
		int length = items.length;
		
		try
		{
			for (int i=0; i < length; i++)
			{				
				addElement(items [i][0], items [i][1]);
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// end of array
		}
		
		String[] firstItem = (String[])selectListOptions.elementAt(0);
		setValue(firstItem[0]);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: createEntry
	// ----------------------------------------------------------------------
	
	private String [] createEntry (String aLabel, String aValue)
	{
		return new String [] {aLabel, (aValue == null ? aLabel : aValue)};
	}
	

	// ----------------------------------------------------------------------
	// METHOD: addElement
	// ----------------------------------------------------------------------
	
	/** Adds an element to the select list.
	  *
	  * @param aLabel The lable and value to use.
	  * 
	  */
	  
	public void addElement (String aLabel)
	{
		addElement (aLabel, null);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: addElement
	// ----------------------------------------------------------------------
	
	/** Add an element to the select list.
	  *
	  * @param aLabel The label for the select list item.
	  * 
	  * @param aValue The value to associate with the label.
	  * 
	  */
	  
	public void addElement (String aLabel, String aValue)
	{
		String [] theEntry = createEntry (aLabel, aValue);
		removeElement (aLabel);
		selectListTable.put (aLabel, theEntry);
		selectListOptions.addElement (theEntry);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: insertElementAt
	// ----------------------------------------------------------------------
	
	/** Insert an element at the specified location.
	  *
	  * @param aIndex The index at which to place the element.
	  * 
	  * @param aLabel The label for the select list item.
	  * 
	  * @param aValue The value to associate with the label.
	  * 
	  */
	  
	public void insertElementAt (int aIndex, String aLabel, String aValue)
	{
		removeElement (aLabel);
		String [] theEntry = createEntry (aLabel, aValue);
		selectListTable.put (aLabel, theEntry);
		selectListOptions.insertElementAt (theEntry, aIndex);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: removeElementAt
	// ----------------------------------------------------------------------
	
	/** Remove an element from the select list.
	  *
	  * @param aIndex The index of the element to remove.
	  * 
	  */
	  
	public void removeElementAt (int aIndex)
	{
		selectListOptions.removeElementAt (aIndex);
	}
	

	// ----------------------------------------------------------------------
	// METHOD: removeElement
	// ----------------------------------------------------------------------
	
	/** Remove an element from the select list corresponding to the given label.
	  *
	  * @param aLabel The label for the element to be removed.
	  * 
	  */
	  
	public void removeElement (String aLabel)
	{
		String [] theEntry = (String []) selectListTable.get (aLabel);
		if (theEntry != null)
		{
			selectListTable.remove (aLabel);
			selectListOptions.removeElement (theEntry);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getAutoRefresh
	// ----------------------------------------------------------------------
	
	/** Returns <code>true</code> if selecting this select list should cause 
		* and automatic screen refresh, <code>false</code> if not.  
		*  
		* @return <code>true</code> if automatic screen refresh is on, <code>false</code> otherwise.
	  * 
	  */
  
	public boolean getAutoRefresh()
	{
		return autoRefresh;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setAutoRefresh
	// ----------------------------------------------------------------------
	
	/** This method sets the select list screen refresh behaviour. This can be
		* useful if the selection of the item should cause a similar event as a
		* button press.
	  * 
	  * @param autoRefresh <code>true</code> if the list is to automatically 
	  *                    refresh the screen on user action, 
	  *                    <code>false</code> if not.
	  * 
	  */
  
	public void setAutoRefresh(boolean anAutoRefresh)
	{
		invalidate();
		autoRefresh = anAutoRefresh;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setIndentation
	// ----------------------------------------------------------------------
	
	/** This method sets the default indentation in spaces.
	  * 
	  * @invisible
	  * 
	  */
  
	public void setIndentation(int spaces)
	{
		this.invalidate();
		if (spaces > 0)
		{
			this.indentation = "";

			for (int i = 0; i < spaces; i++)
			{
				this.indentation += " ";
			}
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getIndentation
	// ----------------------------------------------------------------------
	
	/** This method gets the default indentation in spaces.
	  * 
	  * @invisible
	  * 
		*/
  
  public String getIndentation()
  {
		return this.indentation;
  }
  	
  	
	// ----------------------------------------------------------------------
	// METHOD: getSelectListItems
	// ----------------------------------------------------------------------
	
	/** Returns a <code>Vector</code> containing all the select list items.
	  * 
	  * @return <code>Vector</code> object containing all the items from the select list.
	  */
	  
	public Vector getSelectListItems()
	{
		return getSelectListOptions();
	}
  	
  	
	// ----------------------------------------------------------------------
	// METHOD: getSelectListOptions
	// ----------------------------------------------------------------------
	
	/** @deprecated  Replaced by <code>getSelectListItems()</code>
	  * 
	  */
	  
	public Vector getSelectListOptions()
	{
		return (Vector) selectListOptions.clone ();
	}
  	
  	
	// ----------------------------------------------------------------------
	// METHOD: getSelectedIndex
	// ----------------------------------------------------------------------
	
	/** @deprecated  Replaced by <code>getSelectionIndex()</code>
	  * 
	  */
	  
	public int getSelectedIndex()
	{
		return getSelectionIndex();
	}
  	
  	
	// ----------------------------------------------------------------------
	// METHOD: getSelectionIndex
	// ----------------------------------------------------------------------
	
	/** Returns the index value of the selected item.
	  * 
	  * @return The position of the selected item.
	  */
	  
	public int getSelectionIndex()
	{
		return (getValue () == null ? -1 : getSelectionIndex (getValue ().toString ()));
	}
  	
  	
	// ----------------------------------------------------------------------
	// METHOD: getSelectionIndex
	// ----------------------------------------------------------------------
	
	/** Returns the index value of a given item.
	  * 
	  * @param value The value of which to get the index number of.
	  *
	  * @return The index value of a given item.
	  */
	  
	public int getSelectionIndex(String value)
	{
		int retVal = -1;
		Enumeration theElements = selectListOptions.elements ();
		String [] theElement = null;
		int theIdx = 0;
		while (theElements.hasMoreElements () && retVal == -1)
		{
			theElement = (String []) theElements.nextElement ();
			if (theElement [0].equals(value))
			{
				retVal = theIdx;
			}
			theIdx++;
		}
		return retVal;
	}
  	
  	
	// ----------------------------------------------------------------------
	// METHOD: setSelectionIndex
	// ----------------------------------------------------------------------
	
	/** Sets the item associated with the given index value as selected.
	  * 
	  * @param selectionIndex The index of which to set the associated item as selected.
	  *
	  */
	  
	public void setSelectionIndex(int selectionIndex)
	{
		try
		{
			setValue(((Object[])getSelectListOptions().elementAt(selectionIndex))[0].toString());
		}
		catch (Exception e)
		{
			System.out.println(new DebugString("MSelectList.setSelection(): an exception occured."));
			e.printStackTrace(System.out);
		}
	}  
	
		
	// ----------------------------------------------------------------------
	// METHOD: deselect
	// ----------------------------------------------------------------------
	
	/** De-selects the item from the select list.
	  * 
	  */	  
	
	public void deselect()
	{
		invalidate();
		setValue("");
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setName
	// ----------------------------------------------------------------------
	
	/** Sets the name of the select list. Overrides <code>Component.setName()</code>.
	  * 
	  * @param newName The string associated with the name of the select list.
	  */
	  
	public void setName(String newName)
	{
		invalidate();
		super.setName(newName);
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: getPostValue
	// ----------------------------------------------------------------------
	
	/** Note that this method causes a tighter coupling between the component's 
	  * model and view (WML). Should it eventually be moved into the renderer 
	  * classes? - ian (2001.05.14)
	  * 
	  * @invisible
	  * 
	  */
	  
  public String getPostValue ()
  {
  	return "$(" + getWMLSafeComponentID () + ")";//":e)";
  }
	
  
	// ----------------------------------------------------------------------
	// METHOD: doCreateEvent
	// ----------------------------------------------------------------------
	
	/** Creates and event depending on the state. 
	  * 
	  * @param aStateData The string associated with the state of the select list.
	  */
	  
	protected MauiEvent doCreateEvent (String aStateData)
	{
		return (aStateData != null && aStateData.equals (MActionEvent.ACTION_PUSH) ?
							new MActionEvent (this, aStateData) :
							super.doCreateEvent (aStateData));
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: convertArray
	// ----------------------------------------------------------------------
	
	/** Converts an optinally multidimential object array to a string array.
	  *
	  */
	
	private static String[] convertArray(Object[] aItems)
	{
		String[] retVal = new String[aItems.length];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal[i] = aItems[i].toString();
		}
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: getAutoSubmit
	// ----------------------------------------------------------------------
	
	/** @deprecated  Use <code>getAutoRefresh()</code> instead.
	  * 
	  * @return      <code>true</code> if selecting this select list should 
	  *              cause and automatic screen refresh, <code>false</code> 
	  *              if not.
	  * 
	  */
  
	public boolean getAutoSubmit()
	{
		return getAutoRefresh();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: setAutoSubmit
	// ----------------------------------------------------------------------
	
	/** @deprecated       Use <code>setAutoRefresh()</code> instead.
	  * 
	  * @param autoSubmit <code>true</code> if the list is to automatically 
	  *                   refresh the screen on user action, 
	  *                   <code>false</code> if not.
	  * 
	  */
  
	public void setAutoSubmit(boolean autoSubmit)
	{
		setAutoRefresh(autoSubmit);
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF