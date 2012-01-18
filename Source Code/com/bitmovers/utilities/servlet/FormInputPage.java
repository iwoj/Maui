// =============================================================================// com.bitmovers.utilities.servlet.FormInputPage// =============================================================================package com.bitmovers.utilities.servlet;import java.io.*;import java.util.*;import javax.servlet.*;import javax.servlet.http.*;import com.bitmovers.utilities.*;// =============================================================================// CLASS: FormInputPage// =============================================================================/** The FormInputPage class is a tool for developing HTTP Servlets that consist  * of one or more form input pages.  FormInputPage implements the generic code  * that handles form input and field verification from a HttpServlet.  * <p>  *  * FormInputPage is an abstract class.  The developer implementing a Servlet  * that requires form input is intended to extend this class.  Particularily,  * the developer is intended to implement the 'verifyField()' method which  * defines the verification rules for required form fields.  The implementing  * developer must also (likely in the constructor) make calls to the   * 'setFieldRequirements()' method in order to tell FormInputPage class which  * fields are required fields.  * <p>  *  * The class automatically handles HTML output through the 'outputHTML()'   * method.  When a 'formIsComplete()', the developer may also   * 'getFormFieldData()' to do something with the data that has been collected.  * <p>  *  * See the method descriptions for particular details.  * <p>  *  * @version 2000.02.27-A  * @author Chris Knight (chris.knight@bitmovers.com)  */public abstract class FormInputPage extends HTMLPage{  // ---------------------------------------------------------------------------    private final Hashtable requiredFields = new Hashtable();  // ---------------------------------------------------------------------------  // METHOD: handleAdditionalVariables  // ---------------------------------------------------------------------------  /** This 'handleAdditonalVariables()' method is responsible for handling    * any additional variables which need to be processed. This method can    * be extended to handle variables in any way seen fit. The default    * behaviour of this method is to not do anything special to the passed    * variables.    *    */  public Hashtable handleAdditionalVariables(Hashtable additionalVariables)  {    // (1) Verify the contents of all required fields and then construct and    //     add the error variables to the parsedVariables hashtable.    {    	    Enumeration requiredKeys = this.requiredFields.keys();	    while (requiredKeys.hasMoreElements())	    {	      String fieldName = requiredKeys.nextElement().toString();	      String fieldValue;	      	      try	      {	        fieldValue = super.getRequestData().get(fieldName).toString();	      }	      catch (NullPointerException exception)	      {	        fieldValue = "";	      }	      	      if (this.verifyField(fieldName, fieldValue))	      {	        // remove the errorColour field if it's there	        if (additionalVariables.containsKey(fieldName + ".errorColour"))	        {	          additionalVariables.remove(fieldName + ".errorColour");	        }	      }	      else	      {	        additionalVariables.put(fieldName + ".errorColour", "red");	      }	    }	  }	  	  return additionalVariables;  }    // ---------------------------------------------------------------------------  // METHOD: isComplete  // ---------------------------------------------------------------------------  /** If all required fields (specified through the 'setRequiredField()'     * method) all pass the tests implemented in the 'verifyField()' method,     * then this method returns 'true'.  Otherwise this method returns false.    */    public final boolean isComplete()  {    Enumeration requiredKeys = requiredFields.keys();    int errorCounter = 0;        while (requiredKeys.hasMoreElements())    {      String fieldName = requiredKeys.nextElement().toString();            String fieldValue;            try      {        fieldValue = super.getRequestData().get(fieldName).toString();      }      catch (NullPointerException exception)      {        fieldValue = "";      }            if (!verifyField(fieldName, fieldValue))      {        errorCounter++;      }    }      if (errorCounter > 0)    {      return false;    }    else    {      return true;    }  }    // ---------------------------------------------------------------------------  // METHOD: verifyField  // ---------------------------------------------------------------------------    /** The FormInputPage subclass developer must implement this method in order    * to handle field verification.  For every 'required field' (specified     * through the 'setRequiredField()' method), this method must accept the    * field's name and return 'true' if the method is sucessfully populated    * or return 'false' if not.  An example implementation might look like     * the following:    * <p><pre>    *   public boolean verifyField(String fieldName, String fieldValue)    *   {    *     if (fieldName.equals("FIRST_NAME"))    *     {    *       if (fieldValue.equals(""))    *       {    *         return false;    *       }    *       else    *       {    *         return true;    *       }    *     }    *     else    *     {    *       return false;    *     }    *   }    * </pre>    * <p>    */    public abstract boolean verifyField(String fieldName, String fieldValue);    // ---------------------------------------------------------------------------  // METHOD: setRequiredField  // ---------------------------------------------------------------------------    /** The 'setRequiredField()' method indicates which form fields are    * required.  To specify a required form field, pass the field's name    * to this method.  Only required fields are verified through the     * 'verifyField()' method during runtime.    */    public final void setRequiredField(String fieldName)  {    this.requiredFields.put(fieldName, "required");  }  // ---------------------------------------------------------------------------}// =============================================================================// Copyright 2000 Bitmovers Communications, Inc.                             eof