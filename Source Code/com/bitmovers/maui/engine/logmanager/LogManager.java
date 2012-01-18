package com.bitmovers.maui.engine.logmanager;

import java.io.*;
import java.util.*;
import com.bitmovers.maui.engine.*;


// ========================================================================
// <<SINGLETON>> CLASS: LogManager               (c) 2001 Bitmovers Systems
// ========================================================================

/** The LogManager provides logging facilities to Maui. It allows for
  * different types of logging messages, giving developers the
  * ability to handle or display certain types of logging messages
  * differently from others.  
  * 
  */

public class LogManager
{
	
	
	// Use a static initialiser to start up this class
	private static LogManager thisInstance = new LogManager();
	private static LogManagerPrintStream outStream;
	private static LogManagerPrintStream errStream;
	private static PrintStream originalOut = System.out;
	private static PrintStream originalErr = System.err;
	private static float logThreshold = -1.0f;
	
	private Vector listeners = new Vector();
	protected static Vector detailListeners = new Vector ();
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR: LogManager
	// ----------------------------------------------------------------------
	
	/** This constructor is private and will never be called by anyone
	  * but the static initialiser.
	  *
	  */
	
	private LogManager() {}


	// ----------------------------------------------------------------------
	// STATIC METHOD: initialise
	// ----------------------------------------------------------------------
	
	/** This method initialises the <code>LogManager</code>.
	  * 
	  * @invisible
	  * 
	  */
	  
	public static void initialise()
	{
		String mauiLogFilePath = null;
		boolean couldCreateLogFile = false;
		
		LogManagerString.initialise();
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		
		try
		{
			mauiLogFilePath = theSCM.getProperty(ServerConfigurationManager.MAUI_LOGFILE);
			FileOutputStream out = new FileOutputStream(mauiLogFilePath, true);
			
			LogManager.outStream = new LogManager.LogManagerPrintStream(out, false);
			LogManager.errStream = new LogManager.LogManagerPrintStream(out, true);
			
			couldCreateLogFile = true;
		}
		catch (Exception exception)
		{
		  LogManager.outStream = new LogManager.LogManagerPrintStream(System.out, false);
		  LogManager.errStream = new LogManager.LogManagerPrintStream(System.err, true);
		}
		
		System.setOut(LogManager.outStream);
		System.setErr(LogManager.errStream);
		
		if (isDebug ())
		{
			theSCM.listProperties ();
		}
		
		System.out.println(new DebugString("[LogManager] - Started."));
		
		if (!couldCreateLogFile)
		{
			System.out.println(new WarningString("LogManager could not open " + mauiLogFilePath + " for writing. All output will go to System.out instead."));
		}
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: isWithinThreshold
	// ----------------------------------------------------------------------
	
	/** This method determines if a particular <code>LogManagerString</code>
	  * type is within the system log threshold. If a log message has a value 
	  * within the threshold, it should be recorded, otherwise, ignored.
	  * 
	  * @return  returns <code>true</code> if the given 
	  *          <code>LogManagerString</code> is within the current log 
	  *          threshold value, <code>false</code> otherwise.
	  * 
	  * @param   logManagerString  the <code>LogManagerString</code> to be 
	  *                            tested against the current threshold.
	  * 
	  */
	
	public static boolean isWithinThreshold(LogManagerString logManagerString)
	{
		return isWithinThreshold(logManagerString.getLogLevel());
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: isWithinThreshold
	// ----------------------------------------------------------------------
	
	/** This method determines if a particular float value is within the system log 
	  * threshold. If a log message has a value within the threshold, it 
	  * should be recorded, otherwise, ignored.
	  *
	  * @return  returns <code>true</code> if the given value is less than or
	  *          equal to the current log threshold value, <code>false</code>
	  *          otherwise.
	  * 
	  * @param   testValue  the float value to be tested against the current 
	  *                     threshold.
	  * 
	  */
	
	public static boolean isWithinThreshold(float testValue)
	{
		float testThreshold = logThreshold;
		
		// If the log threshold has not yet been initialised, load it.
		if (testThreshold < 0.0f)
		{
			loadLogThreshold();
		}
		
		testThreshold = logThreshold;
		
		if (testValue <= testThreshold)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: isDebug
	// ----------------------------------------------------------------------
	
	/** This is a convenience method to determine if the current threshold 
	  * accepts debug messages. In other words, you may use this method to
	  * test if Maui is currently in debug mode.
	  * 
	  * @return  returns <code>true</code> if the current log threshold 
	  *          value is equal to, or greater than debug level. Returns
	  *          <code>false</code> otherwise.
	  * 
	  */
	
	public static boolean isDebug()
	{
		return LogManager.isWithinThreshold(new DebugString("").getLogLevel());
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: getInstance
	// ----------------------------------------------------------------------
	
	/** @return  An instance of the <code>LogManager</code> singleton.
	  * 
	  */
	  
	public static LogManager getInstance()
	{
		return LogManager.thisInstance;
	}


	// ----------------------------------------------------------------------
	// STATIC METHOD: resetSystemStreams
	// ----------------------------------------------------------------------
	
	/** This method sets the <code>System.out</code> and 
	  * <code>System.err</code> back to the default java PrintStreams.
	  * 
	  * @invisible
	  * 
	  */
	
	public static void resetSystemStreams()
	{
		System.setOut(LogManager.originalOut);
		System.setErr(LogManager.originalErr);
	}
	
	/** Reset the log level for the log manager
	*
	* @invisible
	*/
	public static void resetLogLevel ()
	{
		logThreshold = -1.0f;
	}

	
	// ----------------------------------------------------------------------
	// STATIC METHOD: getOriginalOut
	// ----------------------------------------------------------------------
	
	/**
	  * Get the original System.out
	  *
	  * @return The original System.otu
	  *
	  * @invisible
	  * 
	  */
	  
	public static PrintStream getOriginalOut ()
	{
		return originalOut;
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: getOriginalErr
	// ----------------------------------------------------------------------
	
	/**
	  * Get the original System.out
	  *
	  * @return The original System.out
	  *
	  * @invisible
	  * 
	  */
	  
	public static PrintStream getOriginalErr ()
	{
		return originalErr;
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: loadLogThreshold
	// ----------------------------------------------------------------------
	
	/** Sets the log threshold value to the value specified by the 
	  * <code>ServerConfigurationManager</code>, i.e. the default value or
	  * the value specified in the Maui properties file.
	  *
	  */
	  
	public static void loadLogThreshold()
	{
		try
		{
			setLogThreshold(LogManagerString.convertStringToLogLevel(ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_LOG_THRESHOLD)));
		}
		catch (Exception exception)
		{
			resetSystemStreams();
			System.err.println("An exception occured while trying to load the log threshold: " + exception.getMessage());
		}
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: setLogThreshold
	// ----------------------------------------------------------------------
	
	/** Sets the log threshold to the specified value.
	  * 
	  * @param  newThreshold  a float value between <code>0.0</code> and 
	  *                       <code>1.0</code> (inclusive).
	  *
	  */
	
	public static void setLogThreshold(final float newThreshold)
	{
		if (newThreshold < 0 || newThreshold > 1)
		{
			throw new IllegalArgumentException("Log threshold values must be between 0.0 and 1.0. You specified " + newThreshold + ".");
		}
		else
		{
			logThreshold = newThreshold;
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: addLogListener
	// ----------------------------------------------------------------------
	
	/** This method registers a <code>LogListener</code> with the 
	  * <code>LogManager</code>. All <code>LogListener</code>s will be 
	  * notified when a new log message is reported. Application developers
	  * may choose to use the <code>LogListener</code> interface and this
	  * registration method to create supplemental log-reporting services.
	  * 
	  */
	
	public void addLogListener(LogListener listener)
	{
		if (listener != null)
		{
			this.listeners.addElement(listener);
		}
	}


	// ----------------------------------------------------------------------
	// METHOD: removeLogListener
	// ----------------------------------------------------------------------
	
	/** This method deregisters a <code>LogListener</code> from the 
	  * <code>LogManager</code>.
	  *
	  */
	
	public void removeLogListener(LogListener listener)
	{
		if (listener != null)
		{
			this.listeners.removeElement(listener);
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: addLogListener
	// ----------------------------------------------------------------------
	
	/** This method registers a <code>LogListener</code> with the 
	  * <code>LogManager</code>. All <code>LogListener</code>s will be 
	  * notified when a new log message is reported. Application developers
	  * may choose to use the <code>LogListener</code> interface and this
	  * registration method to create supplemental log-reporting services.
	  * 
	  */
	
	public void addDetailLogListener(DetailLogListener listener)
	{
		if (listener != null &&
			  !detailListeners.contains (listener))
		{
			detailListeners.addElement(listener);
		}
	}



	// ----------------------------------------------------------------------
	// METHOD: removeLogListener
	// ----------------------------------------------------------------------
	
	/** This method deregisters a <code>LogListener</code> from the 
	  * <code>LogManager</code>.
	  *
	  */
	
	public void removeDetailLogListener(DetailLogListener listener)
	{
		if (listener != null &&
			  detailListeners.contains (listener))
		{
			detailListeners.removeElement(listener);
		}
	}
	
	
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // INNER CLASS: LogManagerPrintStream
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  
  /** @invisible
    * 
    */
    
  private static class LogManagerPrintStream extends PrintStream implements Runnable
  {
  	
  	
	  private static final String NEWLINE = "\r\n";
	  private static final String NOTHING = "";
		
    private Vector logVector = new Vector();
    private boolean isErrorStream;


    // ------------------------------------------------------------------
    // CONSTRUCTOR: LogManagerPrintStream
    // ------------------------------------------------------------------
    
    public LogManagerPrintStream(OutputStream outputStream, boolean isErrorStream)
    {
      super(outputStream);
      
      this.isErrorStream = isErrorStream;
      
      Thread logger = new Thread(this, (isErrorStream ? "LogManager Error Stream" : "LogManager Print Stream"));
      logger.setPriority(Thread.MIN_PRIORITY);
      logger.start();
    }


		private void notifyDetailListeners (Object aPrintable)
		{
			Enumeration theListeners = detailListeners.elements ();
			while (theListeners.hasMoreElements ())
			{
				((DetailLogListener) theListeners.nextElement ()).processDetailLogMessage (aPrintable.toString ());
			}
		}
		
    // ------------------------------------------------------------------
    // METHOD: reallyPrint
    // ------------------------------------------------------------------
    
    /** reallyPrint() does the actual work of printing everything. This
      * method is necessary to handle a println() call with an Object
      * argument (versus a String) because if you concatenate an Object
      * with a newline character, you will get a String and thus will
      * lose the original Object (which in most cases will probably not
      * be Strings).
      *
      */
    
    private void reallyPrint(Object printableObject, boolean newline)
    {
    	// If the printableObject is not a LogManagerString, it should be converted
    	// into one.
    	notifyDetailListeners (printableObject);
    	if (!(printableObject instanceof LogManagerString))
    	{
    		// If this is an error stream, set the printableObject to a ErrorString.
    		if (isErrorStream)
	      {
					printableObject = new ErrorString(printableObject.toString());
	    	}
	    	// Otherwise, set the printableObject to a DebugString.
	    	else
	    	{
	    		printableObject = new DebugString(printableObject.toString());
	    	}
    	}
    	
    	// At this point we should definitely be dealing with LogManagerStrings
    	LogManagerString logManagerString = (LogManagerString)printableObject;
    	
      // Test for threshold, and output string if allowed.
      if (isWithinThreshold(logManagerString))
      {
        this.logVector.addElement(new LogManagerDate() + " " + logManagerString + (newline ? NEWLINE : NOTHING));
      }
    }
		
		
    // ------------------------------------------------------------------
    // METHOD: print
    // ------------------------------------------------------------------
    
    public synchronized void print(Object string)
    {
      reallyPrint(string, false);
    }


    // ------------------------------------------------------------------
    // METHOD: println
    // ------------------------------------------------------------------
    
    public synchronized void println(Object string)
    {
      reallyPrint(string, true);
    }


    // ------------------------------------------------------------------
    // METHOD: print
    // ------------------------------------------------------------------
    
    public synchronized void print(String string)
    {
      print((Object)string);
    }


    // ------------------------------------------------------------------
    // METHOD: println
    // ------------------------------------------------------------------
    
    public synchronized void println(String string)
    {
      print(string + "\n");
    }


    // ------------------------------------------------------------------
    // METHOD: run
    // ------------------------------------------------------------------
    
    /** The Thread's run method which looks for new messages to output.
      *
      */
    
    public void run()
    {
			while (true)
			{
				try
				{
					// Check to see of the log vector has anything to print. If it does,
					// print it.
					if (!this.logVector.isEmpty())
					{
						for (int i = 0; i < this.logVector.size(); i++)
						{
							super.print(this.logVector.elementAt(0));
							
							for (int l = 0; l < LogManager.getInstance().listeners.size(); l++)
							{
								LogListener listener = null;
								
								try
								{
									listener = (LogListener)LogManager.getInstance().listeners.elementAt(l);
									listener.processLogMessage((String)this.logVector.elementAt(0));
								}
								catch (Exception exception)
								{
									// Remove the problematic LogListener
									LogManager.getInstance().removeLogListener(listener);
								}
							}
							
							this.logVector.removeElementAt(0);
						}
					}
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{
					this.print("Warning: LogManager thread was interrupted.");
				}
			}
    }
  
    
  }
  
  
}


// ======================================================================
// (c) 2001 Bitmovers Systems                                         EOF