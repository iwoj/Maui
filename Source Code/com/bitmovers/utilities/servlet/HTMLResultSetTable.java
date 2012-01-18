// =============================================================================// com.bitmovers.utilities.servlet.HTMLResultSetTable// =============================================================================package com.bitmovers.utilities.servlet;import java.io.*;import java.util.*;import java.sql.*;import com.bitmovers.utilities.*;// =============================================================================// CLASS: HTMLResultSetTable// =============================================================================/**   *  * @version 2000.05.10  * @author Ian Wojtowicz (ian@bitmovers.com)  */public class HTMLResultSetTable implements Serializable{  // ---------------------------------------------------------------------------      // ---------------------------------------------------------------------------	// STATIC METHOD: getResultSetAsHashtable  // ---------------------------------------------------------------------------		public static Hashtable getResultSetAsHashtable(ResultSet resultSet) throws SQLException	{		Hashtable resultSetHash = new Hashtable();		ResultSetMetaData metaData;		Vector tableData = new Vector();				metaData = resultSet.getMetaData();				// Get Table Data		try		{			while (resultSet.next())			{				Vector rowData = new Vector();				for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++)				{					rowData.addElement(resultSet.getObject(columnIndex));				}				tableData.addElement(rowData);			}		}		catch(SQLException e) {}				// Form the wrapper hashtable.		resultSetHash.put("MetaData", HTMLResultSetTable.getResultSetMetaDataAsHashtable(metaData));		resultSetHash.put("TableData", tableData);				return resultSetHash;	}			// ---------------------------------------------------------------------------	// STATIC METHOD: getResultSetMetaDataAsHashtable  // ---------------------------------------------------------------------------  	public static Hashtable getResultSetMetaDataAsHashtable(ResultSetMetaData metaData)	{		Hashtable metaDataHash = new Hashtable();				try		{			// columnCount			Integer columnCount = new Integer(metaData.getColumnCount());			metaDataHash.put("columnCount", columnCount);						// catalogName			Vector catalogName = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				catalogName.addElement(metaData.getCatalogName(i));			}			metaDataHash.put("catalogName", catalogName);						// columnDisplaySize			Vector columnDisplaySize = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				columnDisplaySize.addElement(new Integer(metaData.getColumnDisplaySize(i)));			}			metaDataHash.put("columnDisplaySize", columnDisplaySize);						// columnLabel			Vector columnLabel = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				columnLabel.addElement(metaData.getColumnLabel(i));			}			metaDataHash.put("columnLabel", columnLabel);						// columnName			Vector columnName = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				columnName.addElement(metaData.getColumnName(i));			}			metaDataHash.put("columnName", columnName);						// columnType			Vector columnType = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				columnType.addElement(new Integer(metaData.getColumnType(i)));			}			metaDataHash.put("columnType", columnType);						// columnTypeName			Vector columnTypeName = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				columnTypeName.addElement(metaData.getColumnTypeName(i));			}			metaDataHash.put("columnTypeName", columnTypeName);						// precision			Vector precision = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				precision.addElement(new Integer(metaData.getPrecision(i)));			}			metaDataHash.put("precision", precision);						// scale			Vector scale = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				scale.addElement(new Integer(metaData.getScale(i)));			}			metaDataHash.put("scale", scale);						// schemaName			Vector schemaName = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				schemaName.addElement(metaData.getSchemaName(i));			}			metaDataHash.put("schemaName", schemaName);						// tableName			Vector tableName = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				tableName.addElement(metaData.getTableName(i));			}			metaDataHash.put("tableName", tableName);						// autoIncrement			Vector autoIncrement = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				autoIncrement.addElement(new Boolean(metaData.isAutoIncrement(i)));			}			metaDataHash.put("autoIncrement", autoIncrement);						// caseSensitive			Vector caseSensitive = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				caseSensitive.addElement(new Boolean(metaData.isCaseSensitive(i)));			}			metaDataHash.put("caseSensitive", caseSensitive);						// currency			Vector currency = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				currency.addElement(new Boolean(metaData.isCurrency(i)));			}			metaDataHash.put("currency", currency);						// definitelyWritable			Vector definitelyWritable = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				definitelyWritable.addElement(new Boolean(metaData.isDefinitelyWritable(i)));			}			metaDataHash.put("definitelyWritable", definitelyWritable);						// nullable			Vector nullable = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				nullable.addElement(new Integer(metaData.isNullable(i)));			}			metaDataHash.put("nullable", nullable);						// readOnly			Vector readOnly = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				readOnly.addElement(new Boolean(metaData.isReadOnly(i)));			}			metaDataHash.put("readOnly", readOnly);						// searchable			Vector searchable = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				searchable.addElement(new Boolean(metaData.isSearchable(i)));			}			metaDataHash.put("searchable", searchable);						// signed			Vector signed = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				signed.addElement(new Boolean(metaData.isSigned(i)));			}			metaDataHash.put("signed", signed);						// writable			Vector writable = new Vector();			for (int i = 1; i <= metaData.getColumnCount(); i++)			{				writable.addElement(new Boolean(metaData.isWritable(i)));			}			metaDataHash.put("writable", writable);		}		catch(SQLException e)		{			System.err.println("Could not hashify ResultSetMetaData.");		}				return metaDataHash;	}		  // ---------------------------------------------------------------------------		  private Hashtable metaData = new Hashtable();	private Vector tableData = new Vector();	private String sortField = "";	private int sortFieldIndex = -1;		private boolean showHeaders = true;	private int cellSpacing = 1;	private int cellPadding = 5;	private int border = 0;	private int tableWidth = 0;	private String borderColor = "000000";	private String oddRowColor = "cccccc";	private String evenRowColor = "aaaaaa";	private String headerRowColor = "666666";	private String headerTextColor = "FFFFFF";	private String textColor = "000000";		  // ---------------------------------------------------------------------------	// CONSTRUCTOR: HTMLResultSetTable  // ---------------------------------------------------------------------------		public HTMLResultSetTable(ResultSet resultSet) throws SQLException	{		this(HTMLResultSetTable.getResultSetAsHashtable(resultSet), null);	}		public HTMLResultSetTable(ResultSet resultSet, String sortField) throws SQLException	{		this(HTMLResultSetTable.getResultSetAsHashtable(resultSet), sortField);	}		public HTMLResultSetTable(Hashtable tableHash)	{		this(tableHash, null);	}		public HTMLResultSetTable(Hashtable tableHash, String sortField)	{		this.sortField = sortField;				try		{			this.tableData = (Vector)tableHash.get("TableData");			this.metaData = (Hashtable)tableHash.get("MetaData");		}		catch(Exception e)		{			System.err.println("Malformed Hashtable passed to HTMLResultSetTable constructor.");			e.printStackTrace(System.err);		}	}		  // ---------------------------------------------------------------------------	// METHOD: getCellPadding  // ---------------------------------------------------------------------------  	public int getCellPadding()	{		return this.cellPadding;	}		  // ---------------------------------------------------------------------------	// METHOD: setCellPadding  // ---------------------------------------------------------------------------  	public void setCellPadding(int cellPadding)	{		this.cellPadding = cellPadding;	}			// ---------------------------------------------------------------------------	// METHOD: getBorderWidth  // ---------------------------------------------------------------------------  	public int getBorderWidth()	{		return this.border;	}		  // ---------------------------------------------------------------------------	// METHOD: setBorderWidth  // ---------------------------------------------------------------------------  	public void setBorderWidth(int border)	{		this.border = border;	}		// ---------------------------------------------------------------------------	// METHOD: getTableWidth  // ---------------------------------------------------------------------------  	public int getTableWidth()	{		return this.tableWidth;	}		  // ---------------------------------------------------------------------------	// METHOD: setTableWidth  // ---------------------------------------------------------------------------  	public void setTableWidth(int tableWidth)	{		this.tableWidth = tableWidth;	}	  // ---------------------------------------------------------------------------	// METHOD: getHTML  // ---------------------------------------------------------------------------  	public String getHTML()	{		return this.getHTML(1, this.tableData.size());	}		public String getHTML(int startRow, int endRow)	{		StringBuffer html = new StringBuffer();    try    {			html.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"");			if (this.tableWidth > 0)			{			  html.append(" width=\"" + this.tableWidth + "\"");			}						html.append("><tr><td bgcolor=\"#000000\">\n");			html.append("<table width=\"100%\" cellpadding=\"" + this.cellPadding + "\" cellspacing=\"" + this.cellSpacing + "\" border=\"" + this.border + "\" bordercolor=\"" + this.borderColor + "\">\n");			int columnCount = ((Integer)this.metaData.get("columnCount")).intValue();						// Column headers row			if (this.showHeaders)			{				html.append(" <tr bgcolor=\"#" + this.headerRowColor + "\">\n");								for (int h = 0; h < columnCount; h++)				{					html.append("  <td align=\"center\" valign=\"middle\"><font color=\"#" + headerTextColor + "\">");					String columnLabel = (String)((Vector)this.metaData.get("columnLabel")).elementAt(h);													if (columnLabel.equals(""))					{						html.append("&nbsp;");					}					else if (columnLabel.equalsIgnoreCase(sortField))					{						html.append("<b>" + columnLabel + "</b>");						this.sortFieldIndex = h;					}					else					{						html.append("<b>" + columnLabel + "</b>");					}										html.append("</font></td>\n");				}								html.append(" </tr>\n");			}			// Table data rows			for (int i = startRow - 1; i < endRow; i++)			{				if (i % 2 == 0)				{					html.append(" <tr bgcolor=\"" + oddRowColor + "\">\n");				}				else				{					html.append(" <tr bgcolor=\"" + evenRowColor + "\">\n");				}									Vector rowData = (Vector)tableData.elementAt(i);				for (int j = 0; j < rowData.size(); j++)				{					html.append("  <td align=\"center\" valign=\"middle\"><font color=\"#" + textColor + "\">");										if (rowData.elementAt(j) == null)					{				  	html.append("&nbsp;");					}					else if (rowData.elementAt(j).toString().equals(""))					{				  	html.append("&nbsp;");					}					else if (j == (this.sortFieldIndex - 1))					{						html.append("<b>" + rowData.elementAt(j).toString() + "</b>");					}					else					{						html.append(rowData.elementAt(j).toString());					}					html.append("</font></td>\n");				}				html.append(" </tr>\n");			}						html.append("</table>\n");			html.append("</td></tr></table>\n");		}		catch (NullPointerException exception)		{		  System.err.println("HTMLResultSetTable.getHTML(): NullPointerException caught.");		}				return html.toString();	}		  // ---------------------------------------------------------------------------}// =============================================================================// Copyright 2000 Bitmovers Communications, Inc.                             eof