package com.bitmovers.maui.engine.logmanager;

import java.io.*;


// ========================================================================
// CLASS: WarningString                          (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>WarningString</code> represents a warning-level log message
  * (e.g. "Duplicate class found. Ignoring."). I should be used in 
  * situations where an error can be caught and handled safely. If an
  * error occurs, but cannot be handled safely, you should log a 
  * <code>ErrorString</code> instead.
  * 
  * The <code>WarningString</code> appears second in the order of log 
  * message levels:<p>
  * 
  * <code>ErrorString</code>, <code>WarningString</code>,
  * <code>InfoString</code>, <code>DebugString</code><p>
  * 
  * <code>WarningString</code>s will always be reported unless logging is
  * to "error" level.
  * 
  */

public class WarningString extends LogManagerString
{
	
	
	public static final float LOG_LEVEL = 0.4f;
	public static final String LOG_LEVEL_LABEL = "warning";
	private static final String PREFIX = "<W>";
	
	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /** @param  string  The message to be reported to the maui log.
    *
    */
    
  public WarningString(String string)
  {
    super(string);
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