// ==========================================================================
// com.bitmovers.utilities.SelectList
// ==========================================================================

package com.bitmovers.utilities;

import java.util.*;
import java.sql.*;
import java.io.Serializable;

// ==========================================================================
// CLASS: SelectList
// ==========================================================================

/** This class generates a &lt;select&gt; list.
  *
  * @version 1999.08.26
  * @author Patrick Gibson (patrick@bitmovers.com)
  */

public class SelectList implements Serializable
{
  // -----------------------------------------------------------------------
  // CLASS VARIABLES
  // -----------------------------------------------------------------------

	String selectListName;
	Vector selectListOptions = new Vector();
	String selected = "";
	String defaultOption = "";
	String indentation = "   ";
	String onChange = "";


  // -----------------------------------------------------------------------
  // CONSTRUCTOR: SelectList(Vector)
  // -----------------------------------------------------------------------

	/** This method creates a &lt;select&gt; list from a given Vector. Each element
		* in the Vector must be a String[]. If there are two elements in the String[],
		* the first element will be used for the value of the &lt;option&gt; tag and
		* the second element will be the label; if there is only one element, the first
		* element will be used for both the value and label.
		*
		* @param vector The Vector object to use
		*
		*/

	public SelectList(Vector vector)
	{
    Enumeration elements = vector.elements();
    
    while (elements.hasMoreElements())
    {
    	String[] element = (String[])elements.nextElement();
    	String value = "";
    	String label = "";
    	
			try
			{
				value = element[0];
				label = element[1];
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				label = value;
			}
			
	    String[] newElement = { value, label };
	    this.selectListOptions.addElement(newElement);
    }
	}


  // -----------------------------------------------------------------------
  // CONSTRUCTOR: SelectList(String[], boolean)
  // -----------------------------------------------------------------------

	/** This method creates a &lt;select&gt; list from a given String[] array.<p>
		* @param stringArray The array of Strings to use
		* @param numericalValue If true, each &lt;option&gt; tag will have a numerical value starting at 1
		*
		*/

	public SelectList(String[] stringArray, boolean numericalValue)
	{
		int length = stringArray.length;

		try
		{
			if (numericalValue)
			{
				for (int i=0; i<length; i++)
				{
					String value = i+1 + "";
					String label = stringArray[i];
					
					String[] newElement = { value, label };
					
					this.selectListOptions.addElement(newElement);
				}
			}
			else
			{
				for (int i=0; i<length; i++)
				{
					String value = stringArray[i];
					String label = stringArray[i];
					
					String[] newElement = { value, label };

					this.selectListOptions.addElement(newElement);
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// end of array
		}
	}


  // -----------------------------------------------------------------------
  // CONSTRUCTOR: SelectList(int, int)
  // -----------------------------------------------------------------------

	/** This method creates a numbered &lt;select&gt; list from a given number to a given number<p>
		* @param fromNumber The starting number
		* @param toNumber The ending number
		*
		*/

	public SelectList(int start, int end)
	{
		for (int i=start; i<=end; i++)
		{
			String value = i + "";
			String label = i + "";
			
			String[] newElement = { value, label };

			this.selectListOptions.addElement(newElement);
		}
	}


  // -----------------------------------------------------------------------
  // CONSTRUCTOR: SelectList(ResultSet, String)
  // -----------------------------------------------------------------------

	/** This method creates a &lt;select&gt; list from a given ResultSet object.<p>
		* @param resultset The ResultSet object to use
		* @param labelColumn The column name in the ResultSet to retrieve the &lt;option&gt;'s label
		*
		*/

	public SelectList(ResultSet resultset, String labelColumn)
	{
		this(resultset, labelColumn, "");
	}


  // -----------------------------------------------------------------------
  // CONSTRUCTOR: SelectList(ResultSet, String, String)
  // -----------------------------------------------------------------------

	/** This method creates a &lt;select&gt; list from a given ResultSet object.<p>
		* @param resultset The ResultSet object to use
		* @param labelColumn The column name in the ResultSet to retrieve the &lt;option&gt;'s label
		* @param nameColumn The column name in the ResultSet to retrieve the &lt;option&gt;'s value (if different from the label)
		*
		*/

	public SelectList(ResultSet resultset, String labelColumn, String valueColumn)
	{
		Vector newSelectList = new Vector();

		try
		{
			// If there was no valueColumn specified, then value will be the same as label
			if (valueColumn.length() == 0)
			{
				while (resultset.next())
				{
					String label = resultset.getString(labelColumn);
					String value = label;
					
					String[] newElement = { value, label };

					newSelectList.addElement(newElement);
				}
			}
			else
			{
				while (resultset.next())
				{
					String label = resultset.getString(labelColumn);
					String value = resultset.getString(valueColumn);

					String[] newElement = { value, label };

					newSelectList.addElement(newElement);
				}
			}

			this.selectListOptions = newSelectList;
		}
		catch (SQLException e)
		{
			// Leave things as they are for now...
		}
	}


  // -----------------------------------------------------------------------
  // CONSTRUCTOR: SelectList(Hashtable)
  // -----------------------------------------------------------------------

	/** This method creates a &lt;select&gt; list from a given Hashtable, where the key is
		* the value
		* and the value is the name.<p>
		* @param hashtable The Hashtable object to use
		*
		*/

	public SelectList(Hashtable hashtable)
	{
    Enumeration keys = hashtable.keys();
    
    while (keys.hasMoreElements())
    {
    	String value = (String)keys.nextElement();
    	String label = (String)hashtable.get(value);
    	
    	String[] newElement = { value, label };
    	
    	this.selectListOptions.addElement(newElement);
    }
	}


  // -----------------------------------------------------------------------
  // METHOD: setIndentation()
  // -----------------------------------------------------------------------

	/** This method sets the default indentation in spaces.
		*
		*/
  
  public void setIndentation(int spaces)
  {
		if (spaces > 0)
		{
			this.indentation = "";

			for (int i = 0; i < spaces; i++)
			{
				this.indentation += " ";
			}
		}
  }


  // -----------------------------------------------------------------------
  // METHOD: getIndentation()
  // -----------------------------------------------------------------------

	/** This method gets the default indentation in spaces.
		*
		*/
  
  public String getIndentation()
  {
		return this.indentation;
  }


  // -----------------------------------------------------------------------
  // METHOD: setDefaultOption()
  // -----------------------------------------------------------------------

	/** This method sets the label or default, or top, &lt;option&gt;.
		*
		*/
  
  public void setDefaultOption(String option)
  {
  	this.defaultOption = option;
  }


  // -----------------------------------------------------------------------
  // METHOD: getDefaultOption()
  // -----------------------------------------------------------------------

	/** This method gets the label or default, or top, &lt;option&gt;.
		*
		*/
  
  public String getDefaultOption()
  {
  	return this.defaultOption;
  }


  // -----------------------------------------------------------------------
  // METHOD: setName()
  // -----------------------------------------------------------------------

	/** This method sets the name of the &lt;select&gt; list.
		*
		*/
  
  public void setName(String name)
  {
  	this.selectListName = name;
  }


  // -----------------------------------------------------------------------
  // METHOD: getName()
  // -----------------------------------------------------------------------

	/** This method gets the name of the &lt;select&gt; list.
		*
		* @return the name of the select list, or null of uninitialized.
		*
		*/
  
  public String getName()
  {
  	return this.selectListName;
  }


  // -----------------------------------------------------------------------
  // METHOD: setSelected
  // -----------------------------------------------------------------------

	/** This method marks an &lt;option&gt; tag to be selected, or the default
		* option to display.
		*
		* @param value The value to match.
		*
		*/

  public void setSelected(String value)
  {
		// Make sure our value is valid for starters
		try
		{
			if (value.length() != 0)
			{
				this.selected = value;
	    }
	  }
	  catch (Exception e)
	  {
	  	// Oh well...
	  }
  }


  // -----------------------------------------------------------------------
  // METHOD: removeSelected
  // -----------------------------------------------------------------------

  public void removeSelected()
  {
		this.selected = "";
  }

	
  // -----------------------------------------------------------------------
  // METHOD: getSelectList()
  // -----------------------------------------------------------------------
  
	/** This method returns the ready-to-use HTML for the &lt;select&gt; list.
		* Although toString() does the same thing, use this method instead.
		*
		*/

  public String getSelectList()
  {
		String selectList = "";
		Enumeration enumerator = this.selectListOptions.elements();
		boolean defaultValue = false;
		
		selectList += getIndentation() + "<select name=\"" + getName() + "\"" + this.getOnChange() + ">\n";
		selectList += getIndentation() + " " + "<option value=\"\">" + getDefaultOption() + "\n";
		
		if (this.selected.length() != 0)
		{
			defaultValue = true;
		}

		while (enumerator.hasMoreElements())
		{
			String[] element = (String[])enumerator.nextElement();
			String value = element[0];
			String label = element[1];

			if ((defaultValue) && (value.equalsIgnoreCase(this.selected)))
			{
				selectList += getIndentation() + " " + "<option value=\"" + value + "\" selected>" + label + "\n";
			}
			else
			{
				selectList += getIndentation() + " " + "<option value=\"" + value + "\">" + label + "\n";
			}
		}

		selectList += getIndentation() + "</select>\n";
		
		return selectList;
  }
	
  // -----------------------------------------------------------------------
  // METHOD: toString()
  // -----------------------------------------------------------------------
	
	/** This method is here in case someone concatenates a String with a
		* SelectList.
		*
		*/
	
	public String toString()
	{
		return getSelectList();
	}

	
  // -----------------------------------------------------------------------
  // METHOD: getSelectListOptions()
  // -----------------------------------------------------------------------
	
	/** This method is the accessor method for the select list's vector of 
	  * string arrays.
		*/
	
	public Vector getSelectListOptions()
	{
		Vector returnVector = (Vector)selectListOptions.clone();
		
		String[] firstOption = new String[2];
		firstOption[0] = "";
		firstOption[1] = getDefaultOption();
		
		returnVector.insertElementAt(firstOption, 0);
		
		return returnVector;
	}
	
	
  // -----------------------------------------------------------------------
  // METHOD: getSelected()
  // -----------------------------------------------------------------------
	
	/** This method returns the value of the currently selected element.
		*/
	
	public String getSelected()
	{
		return selected;
	}


  // -----------------------------------------------------------------------
  // METHOD: getOnChange()
  // -----------------------------------------------------------------------
	
	/** This method is the accessor method for the select list onChange()
	  * handler.
	  *
		*/
	
	public String getOnChange()
	{
    if (this.onChange.equals(""))
    {
      return this.onChange;
    }
    else
    {
      return " onChange=\"" + this.onChange + "\"";
    }
	}
	
	
  // -----------------------------------------------------------------------
  // METHOD: setOnChange()
  // -----------------------------------------------------------------------

	/** This method sets the onChange() handler of the select list.
		*
		*/
  
  public void setOnChange(String onChange)
  {
  	this.onChange = onChange;
  }

	
  // -----------------------------------------------------------------------
}
