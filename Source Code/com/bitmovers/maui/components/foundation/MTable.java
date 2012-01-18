package com.bitmovers.maui.components.foundation;

import java.awt.Color;
import java.util.*;
import java.sql.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.engine.render.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MauiEvent;


// ========================================================================
// CLASS: MTable                                 (c) 2001 Bitmovers Systems
// ========================================================================

/** <code>MTable</code> is a special container for tabular data. It can be
  * constructed with a two-dimensional array (either an 
  * <code>Object[][]</code> or a <code>Vector</code> of 
  * <code>Vector</code>s) with the table data. If an element in the table
  * is a Maui component, it will be rendered normally, otherwise the 
  * element will be displayed with a <code>toString()</code> call.
  * 
  */
  
public class MTable extends MContainer implements HasPostValue
{
    private static final String base = "MTable";
    private static int nameCounter = 0;

    /** The default border color is Color(0, 0, 0), also known as black.
      */
    private Color borderColor = new Color(0, 0, 0);

    /** The default odd row color is Color(170,170,170).
      */
    private Color oddRowColor = new Color(170, 170, 170);

    /** The default even row color is Color(204,204,204).
      */
    private Color evenRowColor = new Color(204, 204, 204);

    /** The default header row color is Color(102,102,102).
      */
    private Color headerRowColor = new Color(102, 102, 102);

    /** The default header text color is Color(255,255,255), also known as white.
      */
    private Color headerTextColor = new Color(255, 255, 255);

    /** The Default text color is Color(0,0,0), also known as black.
      */
    private Color textColor = new Color(0, 0, 0);
    
    private Vector tableData;
    private Vector columnNames;

    private Hashtable metaData;
    private int selectedRow = -1;
    private String sortField;
    private int sortFieldIndex = -1;
    private boolean sortAscending = true;
    private boolean showHeaders = true;
    private int firstDisplayableRowIndex = 0;
    private MLayout.Alignment defaultAlignment = MLayout.CENTER;

    /** The default value, -1, indicates that all rows should be displayed. */
    private int numberOfDisplayableRows = -1;


    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------
    
    /** Constructs a new <code>MTable</code> using a <code>ResultSet</code> 
      * object. The results from the set are used for the table rows and the 
      * database field names are used for the column headers.
      *
      * @param resultSet  The result set to pass
      */
    
    public MTable(ResultSet resultSet) throws SQLException
    {
      name = base + nameCounter++;
        
      Hashtable tableHash = MTable.hashifyResultSet(resultSet);

      Vector tableData = (Vector)tableHash.get("TableData");
      Vector columnNames = (Vector)((Hashtable)tableHash.get("MetaData")).get("columnName");

      initialise(tableData, columnNames);
    }
    
    
    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    
    /** Constructs a new <code>MTable</code> with the given number of blank 
      * rows and columns excluding any column headers.
      * 
      * @param numberOfRows     This is the number of blank rows that the 
      *                         table should be constructed with.
      * 
      * @param numberOfColumns  This is the number of blank columns that 
      *                         the table should be constructed with.
      */
    
    public MTable(int numberOfRows, int numberOfColumns)
    {
        this(new Object[numberOfRows][numberOfColumns], null);
    }
    
    
    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    
    /** Constructs an <code>MTable</code> with the given column headers  
      * and a given number of blank rows.
      * 
      * @param columnNames  The titles for each column. This is ususally an 
      *                     array of <code>String</code> objects.
      * 
      * @param numberOfRows This is the number of blank rows that the table 
      *                     should be constructed with.
      * 
      */
    
    public MTable(Object[] columnNames, int numberOfRows)
    {
      this(new Object[numberOfRows][columnNames.length], columnNames);
    }
    
    
    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    
    /** Constructs the <code>MTable</code> rows excluding column headers.
      *
      * @param tableData  A two dimensional <code>Object</code> array 
      *                   describing the content of the table rows.
      */
    
    public MTable(Object[][] tableData)
    {
      // what about empty column name object arrays?
      this(tableData, null);
    }
    
    
    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    
    /** Constructs the <code>MTable</code> rows with the given column headers.
      * 
      * @param tableData    A two dimensional <code>Object</code> array 
      *                     describing the content of the table rows.
      * 
      * @param columnNames  An <code>Object</code> array with elements
      *                     representing the column headers. If null is
      *                     received, no column headers will be used.
      */
    
    public MTable(Object[][] tableData, Object[] columnNames)
    {
      name = base + nameCounter++;

      initialise(tableData, columnNames);
    }
    
    
    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    
    /** Constructs the MTable rows excluding column headers.
      *
      * @param tableData    A <code>Vector</code> of row <code>Vector</code>s
      *                     describing the content of the table rows.
      */
    
    public MTable(Vector tableData)
    {
      name = base + nameCounter++;

      // what about empty column name vectors?
      initialise(tableData, null);
    }
    
    
    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    
    /** Constructs the <code>MTable</code> rows including column headers.
      *
      * @param tableData    A <code>Vector</code> of row <code>Vector</code>s
      *                     describing the content of the table rows.
      *
      * @param columnNames  An <code>Vector</code> with elements representing 
      *                     the column headers. If null is received, no
      *                     column headers will be used.
      */
    
    public MTable(Vector tableData, Vector columnNames)
    {
      name = base + nameCounter++;

      initialise(tableData, columnNames);
    }
    
    
    // ----------------------------------------------------------------------
    // METHOD: initialise
    // ----------------------------------------------------------------------
    
    /** Refreshes the table with row data and column headers.
      *
      * @param tableData    A two dimensional <code>Object</code> array 
      *                     describing the content of the table rows.
      * 
      * @param columnNames  An <code>Object</code> array with elements
      *                     representing the column headers. If null is
      *                     received, no column headers will be used.
      */
    
    public void initialise(Object[][] tableData, Object[] columnNames)
    {
      Vector tableDataVector = new Vector();
      Vector columnNamesVector;

      // Construct Vector with table data
      for (int i = 0; i < tableData.length; i++)
      {
        Vector rowData = new Vector();

        for (int j = 0; j < tableData[i].length; j++)
        {
          rowData.addElement(tableData[i][j]);
        }

        tableDataVector.addElement(rowData);
      }

      if (columnNames != null)
      {
        columnNamesVector = new Vector();

        // Construct Vector with column names
        for (int i = 0; i < columnNames.length; i++)
        {
         columnNamesVector.addElement(columnNames[i]);
        }

        initialise(tableDataVector, columnNamesVector);
      }
      else
      {
        initialise(tableDataVector, null);
      }

      firstDisplayableRowIndex = 0;
    }
    
    
    // ----------------------------------------------------------------------
    // METHOD: initialise
    // ----------------------------------------------------------------------
    
    /** Refreshes the table with given row data and columnNames.
      *
      * @param tableData    A <code>Vector</code> of row <code>Vector</code>s
      *                     describing the content of the table rows 
      *
      * @param columnNames  An <code>Vector</code> with elements representing 
      *                     the column headers. If null is received, no 
      *                     column headers will be used.
      */
    
    public void initialise(Vector tableData, Vector columnNames)
    {
      // Look through table data.
      for (int i = 0; i < tableData.size(); i++)
      {
        // Catch bad row data.
        try
        {
          Vector thisRow = (Vector)tableData.elementAt(i);

          for (int j = 0; j < thisRow.size(); j++)
          {
            Object fieldObject = thisRow.elementAt(j);

            // If an object is a maui component...
            if (fieldObject instanceof MComponent)
            {
            // Add it.
            add((MComponent)fieldObject);
            }
          }
        }
        catch (ClassCastException e)
        {
          System.out.println(new WarningString("[MTable] - Bad row data (row object should be a Vector, not a " + e.getMessage() + "). Skipping row."));
          //throw new InvalidArgumentException("[MTable] - Bad row data (row object should be a Vector, not a " + e.getMessage() + "). Skipping row.");
          continue;
        }
      }

      this.tableData = tableData;

      if (columnNames == null)
      {
        setShowHeaders(false);
      }
      else
      {
        this.columnNames = columnNames;
      }

      firstDisplayableRowIndex = 0;
    }
    
    
    // ----------------------------------------------------------------------
    // STATIC METHOD: hashifyResultSet
    // ----------------------------------------------------------------------
    
    /** Converts the given <code>ResultSet</code> into a hashtable with two 
      * elements named "TableData" (which contains a Vector of column field 
      * data) and "MetaData".
      * 
      * @param resultSet  The result set to pass
      */
      
    public static Hashtable hashifyResultSet(ResultSet resultSet) throws SQLException
    {
      Hashtable resultSetHash = new Hashtable();
      Vector tableData = new Vector();
      ResultSetMetaData metaData = resultSet.getMetaData();

      // Get Table Data
      try
      {
        while (resultSet.next())
        {
          Vector rowData = new Vector();
          for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++)
          {
            rowData.addElement(resultSet.getObject(columnIndex));
          }
          tableData.addElement(rowData);
        }
      }
      catch(SQLException e) {}

      // Form the wrapper hashtable.
      resultSetHash.put("MetaData", MTable.hashifyResultSetMetaData(metaData));
      resultSetHash.put("TableData", tableData);

      return resultSetHash;
    }
    
    
    // ----------------------------------------------------------------------
    // STATIC METHOD: hashifyResultSetMetaData
    // ----------------------------------------------------------------------
  
    /** Converts the given <code>ResultSetMetaData</code> object into a 
      * hashtable filled with the result set's meta data. The hashtable keys,
      * listed below, relate directly to the JDBC MetaData accessor methods. 
      * The hashtable values are Vector objects (with the exception of 
      * "columnCount", which is an <code>Integer</code>) have the same number
      * of elements as there are columns in the data to which the meta data 
      * refers.<p>
      *
      * <ul>
      * <li><code>columnCount</code>
      * <li><code>catalogName</code>
      * <li><code>columnDisplaySize</code>
      * <li><code>columnLabel</code>
      * <li><code>columnName</code>
      * <li><code>columnType</code>
      * <li><code>columnTypeName</code>
      * <li><code>precision</code>
      * <li><code>scale</code>
      * <li><code>schemaName</code>
      * <li><code>tableName</code>
      * <li><code>autoIncrement</code>
      * <li><code>caseSensitive</code>
      * <li><code>currency</code>
      * <li><code>definitelyWritable</code>
      * <li><code>nullable</code>
      * <li><code>readOnly</code>
      * <li><code>signed</code>
      * <li><code>writable</code>
      * </ul>
      *
      */
      
    public static Hashtable hashifyResultSetMetaData(ResultSetMetaData metaData)
    {
      Hashtable metaDataHash = new Hashtable();

      try
      {
        // columnCount
        Integer columnCount = new Integer(metaData.getColumnCount());
        metaDataHash.put("columnCount", columnCount);

        // catalogName
        Vector catalogName = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          catalogName.addElement(metaData.getCatalogName(i));
        }
        metaDataHash.put("catalogName", catalogName);

        // columnDisplaySize
        Vector columnDisplaySize = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          columnDisplaySize.addElement(new Integer(metaData.getColumnDisplaySize(i)));
        }
        metaDataHash.put("columnDisplaySize", columnDisplaySize);

        // columnLabel
        Vector columnLabel = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          columnLabel.addElement(metaData.getColumnLabel(i));
        }
        metaDataHash.put("columnLabel", columnLabel);

        // columnName
        Vector columnName = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          columnName.addElement(metaData.getColumnName(i));
        }
        metaDataHash.put("columnName", columnName);

        // columnType
        Vector columnType = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          columnType.addElement(new Integer(metaData.getColumnType(i)));
        }
        metaDataHash.put("columnType", columnType);

        // columnTypeName
        Vector columnTypeName = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          columnTypeName.addElement(metaData.getColumnTypeName(i));
        }
        metaDataHash.put("columnTypeName", columnTypeName);

        // precision
        Vector precision = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          precision.addElement(new Integer(metaData.getPrecision(i)));
        }
        metaDataHash.put("precision", precision);

        // scale
        Vector scale = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          scale.addElement(new Integer(metaData.getScale(i)));
        }
        metaDataHash.put("scale", scale);

        // schemaName
        Vector schemaName = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          schemaName.addElement(metaData.getSchemaName(i));
        }
        metaDataHash.put("schemaName", schemaName);

        // tableName
        Vector tableName = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          tableName.addElement(metaData.getTableName(i));
        }
        metaDataHash.put("tableName", tableName);

        // autoIncrement
        Vector autoIncrement = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          autoIncrement.addElement(new Boolean(metaData.isAutoIncrement(i)));
        }
        metaDataHash.put("autoIncrement", autoIncrement);

        // caseSensitive
        Vector caseSensitive = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          caseSensitive.addElement(new Boolean(metaData.isCaseSensitive(i)));
        }
        metaDataHash.put("caseSensitive", caseSensitive);

        // currency
        Vector currency = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          currency.addElement(new Boolean(metaData.isCurrency(i)));
        }
        metaDataHash.put("currency", currency);

        // definitelyWritable
        Vector definitelyWritable = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          definitelyWritable.addElement(new Boolean(metaData.isDefinitelyWritable(i)));
        }
        metaDataHash.put("definitelyWritable", definitelyWritable);

        // nullable
        Vector nullable = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          nullable.addElement(new Integer(metaData.isNullable(i)));
        }
        metaDataHash.put("nullable", nullable);

        // readOnly
        Vector readOnly = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          readOnly.addElement(new Boolean(metaData.isReadOnly(i)));
        }
        metaDataHash.put("readOnly", readOnly);

        // searchable
        Vector searchable = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          searchable.addElement(new Boolean(metaData.isSearchable(i)));
        }
        metaDataHash.put("searchable", searchable);

        // signed
        Vector signed = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          signed.addElement(new Boolean(metaData.isSigned(i)));
        }
        metaDataHash.put("signed", signed);

        // writable
        Vector writable = new Vector();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
        {
          writable.addElement(new Boolean(metaData.isWritable(i)));
        }
        metaDataHash.put("writable", writable);
      }
      catch(SQLException e)
      {
        System.err.println("Could not hashify ResultSetMetaData.");
      }

      return metaDataHash;
    }
    
    
    // ----------------------------------------------------------------------
    // METHOD: getColumnCount
    // ----------------------------------------------------------------------
    
    /** Returns the number of columns in the table.
      *
      */
    
    public int getColumnCount()
    {
      return (tableData != null && tableData.size () > 0 ?
             ((Vector) tableData.elementAt (0)).size () :
             0);
    }
    
    
    // ----------------------------------------------------------------------
    // METHOD: getRowCount
    // ----------------------------------------------------------------------
    
    /** Returns the number of rows in the table.
      *
      */
    
    public int getRowCount()
    {
      return (tableData != null ?
              tableData.size () :
              0);
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: showHeaders
    // ---------------------------------------------------------------------------
    
    /** Returns whether or not table headers are set to be shown.
      *
      */
    
    public boolean showHeaders()
    {
      return showHeaders;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setShowHeaders
    // ---------------------------------------------------------------------------
    
    /** Sets whether or not table headers are to be shown.
      *
      * @param showHeaders  The <code>boolean</code> value which sets whether or
      *                     not the show the table headers
      */
    
    public void setShowHeaders(boolean showHeaders)
    {
      this.showHeaders = showHeaders;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getSortFieldIndex
    // ---------------------------------------------------------------------------
    
    /** Returns an <code>int</code> indicating the index of the column to sort by.
      *
      */
    
    public int getSortFieldIndex()
    {
      return sortFieldIndex;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getFirstDisplayableRowIndex
    // ---------------------------------------------------------------------------
    
    /** Returns an <code>int</code> indicating the index of the first displayable 
      * row.
      *
      */
      
    public int getFirstDisplayableRowIndex()
    {
      return firstDisplayableRowIndex;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getLastDisplayableRowIndex
    // ---------------------------------------------------------------------------
    
    /** Returns an <code>int</code> indicating the index of the last displayable 
      * row.
      *
      */
      
    public int getLastDisplayableRowIndex()
    {
      if (getNumberOfDisplayableRows() < 0 || getFirstDisplayableRowIndex() + getNumberOfDisplayableRows() > (getRowCount() - 1))
      {
        return getRowCount() - 1;
      }
      else
      {
        return getFirstDisplayableRowIndex() + getNumberOfDisplayableRows() - 1;
      }
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setFirstDisplayableRowIndex
    // ---------------------------------------------------------------------------
    
    /** Sets an <code>int</code> indicating the index of the first displayable row.
      * 
      * @param index  The index to set
      */
      
    public void setFirstDisplayableRowIndex(int index)
    {
      firstDisplayableRowIndex = index;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getNumberOfDisplayableRows
    // ---------------------------------------------------------------------------
    
    /** Returns an <code>int</code> indicating the suggested number of displayable
      * rows.
      * 
      */
      
    public int getNumberOfDisplayableRows()
    {
      if (numberOfDisplayableRows < 0)
      {
        // Display all.
        return getRowCount();
      }
      else
      {
        return numberOfDisplayableRows;
      }
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setNumberOfDisplayableRows
    // ---------------------------------------------------------------------------
    
    /** Set an <code>int</code> indicating the suggested number of displayable 
      * rows. Passing -1 indicates that all rows should be displayed.
      * 
      * @param numberOfDisplayableRows  The number of displayable rows to set.
      */
      
    public void setNumberOfDisplayableRows(int numberOfDisplayableRows)
    {
      // Maximum displayable rows is the size of the entire table.
      if (numberOfDisplayableRows > getRowCount())
      {
        this.numberOfDisplayableRows = getRowCount();
      }
      // Keep negative numbers at -1
      else if (numberOfDisplayableRows < -1)
      {
        this.numberOfDisplayableRows = -1;
      }
      else
      {
        this.numberOfDisplayableRows = numberOfDisplayableRows;
      }
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: determineColumnName
    // ---------------------------------------------------------------------------
    
    /** Determine the actual column name
      *
      * @param aObject  The <code>Object</code> to examine.
      *
      * @return         The <code>String</code> value for the column name.
      */
    private String determineColumnName (Object aObject)
    {
      String retVal = null;

      if (aObject instanceof MComponent)
      {
        retVal = (aObject instanceof HasLabel ?
               ((HasLabel) aObject).getLabel () :
               ((MComponent) aObject).getName ());
      }
      return (retVal == null ? aObject.toString () : retVal);    
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getColumnName
    // ---------------------------------------------------------------------------
    
    /** Returns a <code>String</code> indicating the name of the column at the 
      * specified index.
      * 
      */
      
    public String getColumnName(int index)
    {
      return (columnNames == null ||
              columnNames.size () <= index ||
              columnNames.elementAt (index) == null ?
              "Column_" + index :
              determineColumnName (columnNames.elementAt(index)));
    }


    // ---------------------------------------------------------------------------
    // METHOD: determineShortColumnName
    // ---------------------------------------------------------------------------
    
    /** Determine the actual column name
      *
      * @param aObject  The <code>Object</code> to examine
      *
      * @return         The <code>String</code> value for the column name
      */
      
    private String determineShortColumnName (Object aObject)
    {
      String retVal = null;

      if (aObject instanceof MComponent)
      {
        retVal = (aObject instanceof HasLabel ?
                 ((HasLabel) aObject).getLabel () :
                 ((MComponent) aObject).getName ());
      }
      return (retVal == null ? aObject.toString () : retVal);    
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getShortColumnName
    // ---------------------------------------------------------------------------

    /** Returns an abbreviated name for a column.
      *
      * @param aIndex   The 0 based column index
      *
      * @return         The column name.  If the column has no name, one will be 
      *                 generated
      */

    public String getShortColumnName (int aIndex)
    {
      return (columnNames == null ||
              columnNames.size () <= aIndex ||
              columnNames.elementAt (aIndex) == null ?
               null :
               determineShortColumnName (columnNames.elementAt (aIndex)));
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getRow
    // ---------------------------------------------------------------------------
    
    /** Returns a <code>Vector</code> containing column element from the row at 
      * the specified row index.
      * 
      */
      
    public Vector getRow(int index)
    {
      return (Vector)tableData.elementAt(index);
    }
    

    // ---------------------------------------------------------------------------
    // METHOD: getSelectedRow
    // ---------------------------------------------------------------------------
    
    /** Returns the currently selected row.
      * <pre>
      * WARNING: So far this has been implemented for WML devices only
      * </pre>
      *
      * @invisible
      * @return   The currently selected row, or -1 if no row is selected
      */
    public int getSelectedRow ()
    {
      return selectedRow;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getOddRowColor
    // ---------------------------------------------------------------------------
    
    /** Returns an AWT <code>Color</code> object describing the desired color for 
      * odd-numbered rows in the table.
      * 
      */
      
    public Color getOddRowColor()
    {
      return oddRowColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getEvenRowColor
    // ---------------------------------------------------------------------------
    
    /** Returns an AWT <code>Color</code> object describing the desired color for
      * even-numbered rows in the table.
      * 
      */
      
    public Color getEvenRowColor()
    {
      return evenRowColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getHeaderRowColor
    // ---------------------------------------------------------------------------
    
    /** Returns an AWT <code>Color</code> object describing the desired color for 
      * the header row.
      * 
      */
      
    public Color getHeaderRowColor()
    {
      return headerRowColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getHeaderTextColor
    // ---------------------------------------------------------------------------
    
    /** Returns an AWT <code>Color</code> object describing the desired color for
      * the header row text.
      * 
      */
      
    public Color getHeaderTextColor()
    {
      return headerTextColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getTextColor
    // ---------------------------------------------------------------------------
    
    /** Returns an AWT <code>Color</code> object describing the desired default
      * color for the table's text.
      * 
      */
      
    public Color getTextColor()
    {
      return textColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getBorderColor
    // ---------------------------------------------------------------------------
    
    /** Returns an AWT <code>Color</code> object describing the desired default 
      * color for the table's border.
      * 
      */
      
    public Color getBorderColor()
    {
      return borderColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getDefaultAlignment
    // ---------------------------------------------------------------------------
    
    /** Returns the table's default alignment for columns with no explicit 
      * alignment preferences.
      * 
      */
      
    public MLayout.Alignment getDefaultAlignment()
    {
      return defaultAlignment;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setDefaultAlignment
    // ---------------------------------------------------------------------------
    
    /** Sets the default alignment for columns with no explicit alignment 
      * preferences.
      * 
      * @param defaultAlignment   The default alignment to set
      */
      
    public void setDefaultAlignment(MLayout.Alignment defaultAlignment)
    {
      this.defaultAlignment = defaultAlignment;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: getPostValue
    // ---------------------------------------------------------------------------
    
    /** Returns a WML post value string for use in rendering.
      *
      * @invisible
      * 
      */
    
    public String getPostValue()
    {
      return "$(" + getWMLSafeComponentID () + ")";//":e)";
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setBorderColor
    // ---------------------------------------------------------------------------
    
    /** Change the table's border color
      *
      * @param newColor   The <code>Color</code> to set
      */
    
    public void setBorderColor(Color newColor)
    {
      borderColor = newColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setOddRowColor
    // ---------------------------------------------------------------------------
    
    /** Change the odd table row's color
      *
      * @param newColor   The <code>Color</code> to set
      */
    
    public void setOddRowColor(Color newColor)
    {
      oddRowColor = newColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setEvenRowColor
    // ---------------------------------------------------------------------------
    
    /** Change the even table row's color
      *
      * @param newColor   The <code>Color</code> to set
      */
    
    public void setEvenRowColor(Color newColor)
    {
      evenRowColor = newColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setHeaderRowColor
    // ---------------------------------------------------------------------------
    
    /** Change the table header's row color
      *
      * @param newColor   The <code>Color</code> to set
      */
    
    public void setHeaderRowColor(Color newColor)
    {
      headerRowColor = newColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setHeaderTextColor
    // ---------------------------------------------------------------------------
    
    /** Change the table header's text color
      *
      * @param newColor   The <code>Color</code> to set
      */
    
    public void setHeaderTextColor(Color newColor)
    {
      headerTextColor = newColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: setTextColor
    // ---------------------------------------------------------------------------
    
    /** Change the table row's text color
      *
      * @param newColor   The <code>Color</code> to set
      */
    
    public void setTextColor(Color newColor)
    {
      textColor = newColor;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: createTableEvent
    // ---------------------------------------------------------------------------

    /** Create a table event and return the <code>MActionEvent</code> object. 
      * 
      * @param aStateData  The data associated with the event.
      */
    
    private MActionEvent createTableEvent (String aStateData)
    {
      MActionEvent retVal = new MActionEvent (this, aStateData);
      int theUnderscore = aStateData.indexOf ("_");
      selectedRow = ((theUnderscore != -1) ?
                    Integer.parseInt (aStateData.substring (theUnderscore + 1)) :
                    -1);
      return retVal;
    }
    
    
    // ---------------------------------------------------------------------------
    // METHOD: createEvent
    // ---------------------------------------------------------------------------
        
    /** Create an event.  This is used for wml deep navigation.
      *
      * @param aStateData   The data associated with the event
      *
      * @return             A <code>MauiEvent</code>
      *
      * @invisible
      * 
      */
      
    public MauiEvent createEvent (String aStateData)
    {
      return (aStateData != null && aStateData.trim ().length () > 0 ?
            createTableEvent (aStateData) :
            super.createEvent (aStateData));
      //return doCreateEvent (aStateData);
    }
}

// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF
