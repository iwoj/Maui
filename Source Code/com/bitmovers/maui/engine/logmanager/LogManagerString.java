package com.bitmovers.maui.engine.logmanager;

import java.io.*;
import java.util.*;


// ========================================================================
// CLASS: LogManagerString                       (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>LogManagerString</code> represents an abstract message which 
  * is intended to be extended to provide a particular context for log
  * messages. For example, one might extend this class to indicate that the 
  * message is some sort of warning.
  *
  */

public abstract class LogManagerString
{
	
	
	public static final float DEFAULT_LOG_LEVEL = InfoString.LOG_LEVEL;
	
	private static Hashtable thresholdLabelMap; 
	
  private String messageString = null;
	
	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR: LogManagerString
  // ----------------------------------------------------------------------
	
	/** @param  string  A string containing the log message.
	  * 
	  */
	
  public LogManagerString(final String messageString)
  {
  	this.messageString = messageString;
  }
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: initialise
	// ----------------------------------------------------------------------
	
	/** This method initialises the <code>LogManagerString</code>.
	  * 
	  * @invisible
	  * 
	  */
	  
	public static void initialise()
	{
		// Populate the thresholdLabelMap
  	thresholdLabelMap = new Hashtable();
  	thresholdLabelMap.put(ErrorString.LOG_LEVEL_LABEL, new Float(ErrorString.LOG_LEVEL));
	  thresholdLabelMap.put(WarningString.LOG_LEVEL_LABEL, new Float(WarningString.LOG_LEVEL));
	  thresholdLabelMap.put(InfoString.LOG_LEVEL_LABEL, new Float(InfoString.LOG_LEVEL));
	  thresholdLabelMap.put(DebugString.LOG_LEVEL_LABEL, new Float(DebugString.LOG_LEVEL));
	}
	
	
	// ----------------------------------------------------------------------
  // STATIC METHOD: convertStringToLogLevel
  // ----------------------------------------------------------------------
  
  /** This is a general-purpose convenience method to convert from a string
    * representing a log level (as a float or label) to its corresponding 
    * float value.
    * 
    * @return     A float value matching the given log level string value. 
    *             For example, if <code>"debug"</code> is passed, 
    *             <code>0.9</code> will be returned. If a string 
    *             representing a float (e.g. <code>"0.1f"</code>) or a 
    *             double (e.g. <code>"0.1"</code>) is passed, it will be 
    *             converted to a float and returned.
    * 
    * @exception  An <code>IllegalArgumentException</code> will be thrown 
    *             if the given <code>String</code> cannot be converted.
    * 
    */
    
  public static float convertStringToLogLevel(final String logLevelLabel) throws IllegalArgumentException
	{
		float logLevel = DEFAULT_LOG_LEVEL;
		
		// Try converting from a log level label (e.g. "debug") to a float (e.g. 0.9f)
		try
		{
			logLevel = ((Float)thresholdLabelMap.get(logLevelLabel)).floatValue();
		}
		// Try converting from a string float (e.g. "0.9f") to a proper float (e.g. 0.9f)
		catch (NullPointerException npe)
		{
			try
			{
				logLevel = (new Float(logLevelLabel)).floatValue();
			}
			catch (NumberFormatException nfe) {}
		}
		
		return logLevel;
	}
	
	
  // ----------------------------------------------------------------------
  // METHOD: toString
  // ----------------------------------------------------------------------
  
  /** @return  A string containing the log message.
    * 
    */
    
  public String toString()
  {
    return getPrefix() + " " + getMessage();
  }
	
	
  // ----------------------------------------------------------------------
  // METHOD: getMessageString
  // ----------------------------------------------------------------------
  
  public String getMessage()
  {
    return messageString;
  }
	
	
  // ----------------------------------------------------------------------
  // PROTECTED METHOD: setMessage
  // ----------------------------------------------------------------------
  
  protected void setMessage(String messageString)
  {
    this.messageString = messageString;
  }
	
	
  // ----------------------------------------------------------------------
  // ABSTRACT STATIC METHOD: getLogLevel
  // ----------------------------------------------------------------------
  
  /** Returns a float representing this <code>LogManagerString</code>'s
    * log level for threshold use.<p>
    * 
    * This method must be overridden by subclasses.
    * 
    */
    
  public abstract float getLogLevel();
	
	
  // ----------------------------------------------------------------------
  // ABSTRACT STATIC METHOD: getLogLevelLabel
  // ----------------------------------------------------------------------
  
  /** Returns a label representing this <code>LogManagerString</code>'s
    * log level for threshold use.<p>
    * 
    * This method must be overridden by subclasses.
    * 
    */
    
  public abstract String getLogLevelLabel();
	
	
  // ----------------------------------------------------------------------
  // ABSTRACT STATIC METHOD: getPrefix
  // ----------------------------------------------------------------------
  
  /** This method must be overridden by subclasses.
    * 
    */
    
  public abstract String getPrefix();
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF