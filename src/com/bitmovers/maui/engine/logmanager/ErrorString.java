package com.bitmovers.maui.engine.logmanager;

import java.io.*;


// ========================================================================
// CLASS: ErrorString                            (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>ErrorString</code> represents a error-level log message
  * (e.g. "NullPointerException. Unable to continue."). It should only 
  * be used for reporting critical situations. Errors which can be caught 
  * and handled safely should be reported with the 
  * <code>WarningString</code>.<p>
  * 
  * The <code>ErrorString</code> is the least commonly used log output 
  * level, but most commonly reported level. It appears first in the order 
  * of log message levels:<p>
  * 
  * <code>ErrorString</code>, <code>WarningString</code>,
  * <code>InfoString</code>, <code>DebugString</code><p>
  * 
  * <code>ErrorString</code>s will always be reported unless logging is
  * turned off entirely.
  * 
  */

public class ErrorString extends LogManagerString
{
	
	
	public static final float LOG_LEVEL = 0.1f;
	public static final String LOG_LEVEL_LABEL = "error";
	private static final String PREFIX = "<E>";
	
	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /** @param  string  The message to be reported to the maui log.
	  * 
	  */
	  
  public ErrorString(String string)
  {
    super(string);
  }


  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /** @param  exception  The exception to be reported to the maui log.
	  * 
	  */
	  
  public ErrorString(Throwable exception)
  {
    this(exception, null);
  }


  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /** @param  exception  The exception to be reported to the maui log.
    * 
    * @param  string     An additional string to be reported to the maui 
    *                    log, along with the exception message.
	  * 
	  */
	  
  public ErrorString(Throwable exception, String string)
  {
    super(string);
    
    // Terse-ify the exception class name
    String exceptionClass = exception.getClass().getName();
    {
      int lastIndex = -1;
      if ((lastIndex = exceptionClass.lastIndexOf(".")) != -1)
      {
        exceptionClass = exceptionClass.substring(lastIndex + 1, exceptionClass.length());
      }
    }

    String message = exceptionClass + " - " + exception.getMessage();
    
    if (string != null)
    {
      message += " (" + string + ")";
    }
    
    setMessage(message);
  }
	
	
  // ----------------------------------------------------------------------
  // METHOD: getLogLevel
  // ----------------------------------------------------------------------
  
  /** Returns a float representing this <code>LogManagerString</code>'s
    * log level for threshold use.
    * 
    * @return  Should always return <code>0.9f</code>
    * 
    */
    
  public float getLogLevel()
  {
  	return LOG_LEVEL;
  }
	
	
  // ----------------------------------------------------------------------
  // STATIC METHOD: getLogLevelLabel
  // ----------------------------------------------------------------------
  
  /** Returns a label representing this <code>LogManagerString</code>'s
    * log level for threshold use.
    * 
    * @return  Should always return <code>"debug"</code>
    * 
    */
    
  public String getLogLevelLabel()
  {
  	return LOG_LEVEL_LABEL;
  }
	
	
  // ----------------------------------------------------------------------
  // STATIC METHOD: getPrefix
  // ----------------------------------------------------------------------
  
  /** 
    * 
    */
    
  public String getPrefix()
  {
  	return PREFIX;
  }
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF