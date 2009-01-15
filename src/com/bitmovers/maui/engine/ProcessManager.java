// =============================================================================
// com.bitmovers.maui.ProcessManager
// =============================================================================

package com.bitmovers.maui.engine;

import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.utilities.*;


// =============================================================================
// <<SINGLETON>> CLASS: ProcessManager
// =============================================================================

public class ProcessManager
	implements I_SessionListener
{
	// ---------------------------------------------------------------------------

  private static ProcessManager theInstance = new ProcessManager();  
  
  private Hashtable processHashtable;
  private boolean initDone = false;
  private long housekeepingFrequency = 20000;

	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: ProcessManager
	// ---------------------------------------------------------------------------
	
	private ProcessManager()
	{
		this.processHashtable = new Hashtable();

		System.err.println(new DebugString("[ProcessManager] - Started."));
	}

	// ---------------------------------------------------------------------------
	// METHOD: initialize
	// ---------------------------------------------------------------------------
	
	public void initialize ()
	{
		if (!initDone)
		{
			ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
			HTTPSession.addSessionListener (this);
			String theFrequency = theSCM.getProperty (theSCM.MAUI_PROCESS_HOUSEKEEPING_FREQUENCY);
			try
			{
				housekeepingFrequency = Long.parseLong (theFrequency);
			}
			catch (NumberFormatException e)
			{
				System.err.println ("[ProcessManager] Bad number for process housekeeping frequency : " +
													  theFrequency + " Using default frequency of 20 seconds.");
				housekeepingFrequency = 20;
			}
			initDone = true;
		}
	}

	// ---------------------------------------------------------------------------
	// CLASS METHOD: getInstance
	// ---------------------------------------------------------------------------
	
	public static ProcessManager getInstance()
	{
	  return theInstance;
	}
		
	
	// ---------------------------------------------------------------------------
	// METHOD: registerProcess
	// ---------------------------------------------------------------------------
	
	/** MauiApplications register themselves with the process manager when they
	  * are constructed.  There is no need to call the method explicitly.
	  */
	
	public String registerProcess(MauiApplication application)
	{
	  String mPID = Integer.toHexString(application.hashCode());
	  ProcessInformation mProcessInformation = new ProcessInformation(application);
	  
	  this.processHashtable.put(mPID, mProcessInformation);

	  System.out.println(new DebugString("[ProcessManager] - Registered Process '" + mPID + "'."));
	  
	  return mPID;
	}
	
	public void removeProcess (MauiApplication aApplication)
	{
		String thePID = Integer.toHexString (aApplication.hashCode ());
		processHashtable.remove (thePID);
	}


	// ---------------------------------------------------------------------------
	// METHOD: processExists
	// ---------------------------------------------------------------------------
	
	public boolean processExists(String mPID)
	{
	  try
	  {
	    if (this.processHashtable.containsKey(mPID))
	    {
	      // Just to be extra-picky, we will check to ensure that the
	      // process hasn't gone bad with this simple test.  If it fails,
	      // a NullPointerException should be thrown...
	      
	      ((ProcessInformation)(this.processHashtable.get(mPID))).application.toString();
	      return true;
	    }
	    else
	    {
	      return false;
	    }
	  }
	  catch (NullPointerException exception)
	  {
	    return false;
	  }
	}


	/**
	* Notification of session creation
	*
	* @param aSessionEvent The event object describing the session 
	*/
	public void sessionCreated (SessionEvent aSessionEvent)
	{
	}
	
	/**
	* Notification of session deletion
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void sessionDeleted (SessionEvent aSessionEvent)
	{
	}
	
	/**
	* Notification of the addition of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationAdded (SessionEvent aSessionEvent)
	{
	}
	
	/**
	* Notification of the removal of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationRemoved (SessionEvent aSessionEvent)
	{
	  String mPID = Integer.toHexString (aSessionEvent.getMauiApplication ().hashCode());
		processHashtable.remove (mPID);
	}
			
	// ---------------------------------------------------------------------------
	// METHOD: getProcess
	// ---------------------------------------------------------------------------
	
	public MauiApplication getProcess(String mPID)
	{
		ProcessInformation theInformation = (ProcessInformation) processHashtable.get (mPID);
		return (theInformation == null ? null : theInformation.application);
	}

	// ---------------------------------------------------------------------------
	// METHOD: getProcessID
	// ---------------------------------------------------------------------------

	// ---------------------------------------------------------------------------
	// METHOD: getProcessUptime
	// ---------------------------------------------------------------------------

	// ---------------------------------------------------------------------------
	// METHOD: endProcess
	// ---------------------------------------------------------------------------


	// ---------------------------------------------------------------------------
	// METHOD: runProcessManager
	// ---------------------------------------------------------------------------
	
	/** runProcessManager is a thread that monitors all processes registered 
	  * with the ProcessManager (ie. all 'running' MauiApplications).  The
	  * responsibility of the ProcessManager run thread are as follows (in 
	  * no particular order):
	  *
	  *  1. Removal of 'old' (closed or killed) process from the manager.
	  *      (...) 
	  *  2. ?? Ensure that processes aren't duplicated... ??
	  *
	  */


	// ===========================================================================
	// INNER CLASS: ProcessInformation
	// ===========================================================================
	
	private class ProcessInformation
	{
	  // -------------------------------------------------------------------------
	  
	  MauiApplication application;
	  long startTime;
	  
		ProcessInformation(MauiApplication application)
	  {
	    this.application = application;
	    this.startTime = System.currentTimeMillis();
	  }

	  // -------------------------------------------------------------------------
	}

	// ===========================================================================

}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof