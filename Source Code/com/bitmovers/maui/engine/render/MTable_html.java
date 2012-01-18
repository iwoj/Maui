package com.bitmovers.maui.engine.render;

import java.util.Enumeration;
import java.awt.Color;
import java.util.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor;
import com.bitmovers.utilities.StringParser;

// ========================================================================
// CLASS: MTable_html
// ========================================================================

/** HTML renderer for the MTable component.
  *
  */

public class MTable_html extends A_Renderer
{
	
	
	private int cellSpacing = 1;
	private int cellPadding = 5;
	private int borderSize = 0;
	
	
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
			html.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td bgcolor=\"#" + HTMLCompositor.colorToRGBHexString(table.getBorderColor()) + "\">\n");
			html.append("<table width=\"100%\" cellpadding=\"" + this.cellPadding + "\" cellspacing=\"" + this.cellSpacing + "\" border=\"" + this.borderSize + "\" bordercolor=\"#" + HTMLCompositor.colorToRGBHexString(table.getBorderColor()) + "\">\n");
			int columnCount = table.getColumnCount();
			
			// Column headers row
			if (table.showHeaders())
			{
				html.append(" <tr bgcolor=\"#" + HTMLCompositor.colorToRGBHexString(table.getHeaderRowColor()) + "\">\n");
				
				for (int h = 0; h < columnCount; h++)
				{
					html.append("  <td align=\"");
					html.append(defaultAlignment);
					html.append("\" valign=\"middle\"><font color=\"#");
					html.append(HTMLCompositor.colorToRGBHexString(table.getHeaderTextColor()));
					html.append("\">");
					
					String columnLabel = table.getColumnName(h);
					
					if (columnLabel.equals(""))
					{
						html.append("&nbsp;");
					}
					else
					{
						html.append("<b>" + columnLabel + "</b>");
					}
					
					html.append("</font></td>\n");
				}
				
				html.append(" </tr>\n");
			}
			
			// Iterate through rows.
			for (int i = table.getFirstDisplayableRowIndex(); i <= table.getLastDisplayableRowIndex(); i++)
			{
				// Alternate row color.
				if (i % 2 == 0)
				{
					html.append(" <tr bgcolor=\"#" + HTMLCompositor.colorToRGBHexString(table.getOddRowColor()) + "\">\n");
				}
				else
				{
					html.append(" <tr bgcolor=\"#" + HTMLCompositor.colorToRGBHexString(table.getEvenRowColor()) + "\">\n");
				}
				
				Vector rowData = table.getRow(i);
				
				// Iterate through fields.
				for (int j = 0; j < rowData.size(); j++)
				{
					html.append("  <td align=\"");
					html.append(defaultAlignment);
					html.append("\" valign=\"middle\">");
					
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
					  	html.append(((I_Renderable)rowData.elementAt(j)).render());
					  }
					}
					// If all else fails, output the String representation of the
					// field object.
					else
					{
						html.append("<font color=\"#");
						html.append(HTMLCompositor.colorToRGBHexString(table.getTextColor()));
						html.append("\">");
						html.append(HTMLCompositor.encodeHTML(rowData.elementAt(j).toString()));
						html.append("</font>");
					}
					
					html.append("</td>\n");
				}
				
				html.append(" </tr>\n");
			}
			
			html.append("</table>\n");
			html.append("</td></tr></table>\n");
		}
		catch (NullPointerException exception)
		{
		  System.err.println("MTable_html.render(): NullPointerException caught.");
		  
		  exception.printStackTrace(System.err);
		}
		
		return html.toString();
	}
	
	
	// ---------------------------------------------------------------------------
}