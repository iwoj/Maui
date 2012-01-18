package com.bitmovers.maui.engine.logmanager;

import java.io.*;


// ========================================================================
// CLASS: InfoString                             (c) 2001 Bitmovers Systems
// ========================================================================

/** The <code>InfoString</code> represents an info-level log message
  * (e.g. "LogManager loaded."). It should be used to report normal,
  * public information about the status of your code. Messages regarding
  * unexpected behaviour should use either the <code>ErrorString</code> 
  * or the <code>WarningString</code>. Messages about lower-level 
  * activities, not of interest to the average maui administrator, should
  * use the <code>DebugString</code>.<p>
  * 
  * The <code>InfoString</code> appears third in the order of log message 
  * levels:<p>
  * 
  * <code>ErrorString</code>, <code>WarningString</code>,
  * <code>InfoString</code>, <code>DebugString</code><p>
  * 
  * The <code>InfoString</code> if the default log-level threshold. It will
  * always be reported unless the log level is set to "warning" or "error".
  * 
  */

public class InfoString extends LogManagerString
{
	
	
	public static final float LOG_LEVEL = 0.6f;
	public static final String LOG_LEVEL_LABEL = "info";
	private static final String PREFIX = "<I>";
	
	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /** @param  string  The message to be reported to the maui log.
    *
    */
    
  public InfoString(String string)
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