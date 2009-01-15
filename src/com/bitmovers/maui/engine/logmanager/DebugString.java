package com.bitmovers.maui.engine.logmanager;

import java.io.*;


// ========================================================================
// CLASS: DebugString                            (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>DebugString</code> represents a debug-level log message
  * (e.g. "User clicked a button."). It is the most commonly used log 
  * output level, but least commonly reported level.<p>
  * 
  * It appears last in the order of log message levels:<p>
  * 
  * <code>ErrorString</code>, <code>WarningString</code>,
  * <code>InfoString</code>, <code>DebugString</code><p>
  * 
  * If the maui runtime is configured to output debug messages, then all 
  * other message types will also be reported. To prevent debug messages 
  * from being logged, simply set the log level to "info" or lower.
  * 
  */

public class DebugString extends LogManagerString
{
	
	
	public static final float LOG_LEVEL = 0.9f;
	public static final String LOG_LEVEL_LABEL = "debug";
	private static final String PREFIX = "<D>";
	
	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /** @param  string  The message to be reported to the maui log.
    * 
    */
  
  public DebugString(String string)
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