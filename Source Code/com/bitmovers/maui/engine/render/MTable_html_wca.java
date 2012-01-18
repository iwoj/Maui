package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import java.util.*;

import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.utilities.StringParser;

// ========================================================================
// CLASS: MTable_html_wca
// ========================================================================

/** HTML renderer for the MTable component.
  *
  */

public class MTable_html_wca extends A_Renderer
{
	
		
	// ----------------------------------------------------------------------
	// METHOD: render
	// ----------------------------------------------------------------------
	
	public String render(I_Renderable aRenderable)
	{
		MTable table = (MTable)aRenderable;
		
		String defaultAlignment = ((MLayout.Alignment)table.getDefaultAlignment()).toString();
		
		StringBuffer html = new StringBuffer();
		
    try
    {
			int columnCount = table.getColumnCount();
			
			html.append("<hr size=\"2\">\n");
			
			/*
			// Column headers row
			if (table.showHeaders())
			{
				columnHeaders = new String[columnCount];
				
				for (int h = 0; h < columnCount; h++)
				{ 
					columnHeaders[h] = 
				  String columnLabel = table.getColumnName(h);
					
					if (columnLabel.equals(""))
					{
						html.append("&nbsp;");
					}
					else
					{
						html.append("<b>" + columnLabel + "</b>");
					}
					
					if (h < (columnCount - 1))
					{
					  html.append("\n<br>\n");
					}
					else
					{
					  html.append("\n<hr size=\"1\">\n");
					}
				}
			}
			*/
			
			
			// Iterate through rows.
			for (int i = table.getFirstDisplayableRowIndex(); i <= table.getLastDisplayableRowIndex(); i++)
			{
				Vector rowData = table.getRow(i);
				
				// Iterate through fields.
				for (int j = 0; j < rowData.size(); j++)
				{
					// Output column name
					if (table.showHeaders())
					{
						html.append("<b>");
						html.append(HTMLCompositor.encodeHTML(table.getColumnName(j)));
						html.append(":</b> ");
					}
					
					// Handle null field.
					if (rowData.elementAt(j) == null)
					{
				  	html.append("&nbsp;");
					}
					// Handle empty String field.
					else if (rowData.elementAt(j).toString().equals(""))
					{
				  	html.append("&nbsp;");
					}
					// Handle renderable field.
					else if (rowData.elementAt(j) instanceof I_Renderable)
					{
						// If the field contains an empty MLabel, output a non-breaking space
						// to ensure that table cell renders its background color.
						if (rowData.elementAt(j) instanceof MLabel && ((MLabel)rowData.elementAt(j)).getText().equals(""))
						{
							html.append("&nbsp;");
						}
						else
						{
						  //Palm does not support nested Tables so if the field to render is another MTable
						  //we must output an error message and an appropriate String for the field value
						  
						  Object tableElement = rowData.elementAt(j);
						  String componentName = tableElement.getClass().getName();
						  	
						  if (componentName.indexOf("MTable") == -1)
						  {
						    html.append(((I_Renderable)tableElement).render()); 
						  }
						  else
						  {
					  	  html.append("Can't render a nested MTable!");
					  	}
					  }
					}
					// If all else fails, output the String representation of the
					// field object.
					else
					{
						html.append(HTMLCompositor.encodeHTML(rowData.elementAt(j).toString()));
				  }
					
					if (j < (rowData.size() - 1))
					{
					  html.append("\n<br>\n");
					}
					else if (i == table.getLastDisplayableRowIndex())
					{
					  //we are at the last row of the table, so we add a thick line to denote that fact
					  html.append("\n<hr size=\"2\">\n");
					}
					else
					{
					  html.append("\n<hr size=\"1\">\n");
					}
				}				
			}
		}
		catch (NullPointerException exception)
		{
		  System.err.println("HTMLResultSetTable.getHTML(): NullPointerException caught.");
		}
				
		return html.toString();
	}
	
	
	// ---------------------------------------------------------------------------
}