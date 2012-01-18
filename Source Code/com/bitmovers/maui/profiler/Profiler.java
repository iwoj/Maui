// =============================================================================
// com.bitmovers.maui.profiler.Profiler
// =============================================================================

package com.bitmovers.maui.profiler;
import java.io.*;
import com.bitmovers.maui.engine.ServerConfigurationManager;

/**
* Profiler STATIC class <p>
* This class provides simple resource profiling for any application.
* It doesn't run as a background profiler (as others do).  Instead it provides a 
* simple API for applications to use in tracking the resource utilization of various
* operations performed.
*
* @invisible
*/
public class Profiler
{
	private static ProfileEntry [] profileEntries = new ProfileEntry [50];
	private static int lastUsedEntry = 0;
	private static String [] sourceNames;
	private static String [] actionCodeNames;
	private static PrintStream logStream;
	private static Runtime runtime;
	private static int sequenceNumber = 0;
	private static boolean enabled = false;
	
	/**
	* This static inner class contains a "snap shot" taken by the profiler.
	*/
	protected static class ProfileEntry
	{
		protected boolean available = true;
		protected int source;
		protected int action;
		protected long startTime;
		protected long startMemory;
		protected long freeMemory;
		protected long totalMemory;
		protected StringBuffer logMessage = new StringBuffer (256);
	}
	
	/**
	* Initialize profiling.  If profiling is enabled, then an output file will be created (or opened with append).
	* Also, the ProfileEntry array will be populated with ProfileEntry objects.
	*
	* @param aLogFile The name of the target log file
	* @param aSourceNames String array which maps to the source codes used in the profiling
	* @param aActionCodeNames String array which maps to the action codes used in the profiling
	* @param aAppend Boolean indicating if this is append or overwrite
	*
	* @exception IOException If the profile output file creation fails
	*/
	public static void initialize (String aFileName,
								   boolean aAppend,
								   String [] aSourceNames,
								   String [] aActionCodeNames)
		throws IOException
	{
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		enabled = theSCM.getProperty (ServerConfigurationManager.MAUI_PROFILE_ENABLE).equals ("true");
		
		if (enabled)
		{
		
			logStream = new PrintStream (new FileOutputStream (aFileName, aAppend));
			
			sourceNames = (String []) aSourceNames.clone ();
			actionCodeNames = (String []) aActionCodeNames.clone ();
			runtime = Runtime.getRuntime ();
			
			for (int i = 0; i < profileEntries.length; i++)
			{
				profileEntries [i] = new ProfileEntry ();
			}
			
			logStream.println ("Sequence,Source,Action,Millis,Mem Change,In Use, Comment");
		}
	}
	
	/**
	* Start a profile record.  This takes a snapshot of some useful information in the VM (memory, etc).
	*
	* @param aSource The int representing the source of the action
	* @param aAction The int representing the action
	*
	* @return A reference value which is used to close the profile operation
	*/
	public static int start (int aSource, int aAction)
	{
		int retVal = -1;
		
		if (logStream != null)
		{
			for (int i = 0; i < 50 && retVal == -1; i++)
			{
				if (profileEntries [i].available)
				{
					retVal = i;
				}
			}
			
			if (retVal != -1)
			{
				profileEntries [retVal].available = false;
				profileEntries [retVal].source = aSource;
				profileEntries [retVal].action = aAction;
				profileEntries [retVal].startTime = System.currentTimeMillis ();
				profileEntries [retVal].freeMemory = runtime.freeMemory ();
				profileEntries [retVal].totalMemory = runtime.totalMemory ();
				profileEntries [retVal].startMemory = profileEntries [retVal].totalMemory -
													  profileEntries [retVal].freeMemory;
			}
		}
		
		return retVal;
	}
	
	/**
	* Post a profile record.  This is the closing part of a profile.  A corresponding ProfileEntry must
	* already exist in order for a profile record to be written out.  Delta information is calculated (time, memory),
	* plus some identifier information.
	*
	* @param aReference The reference returned from the profile start
	* @param aSource An alternative source to associate with the request
	* @param aAction An alternative action to associate with the request
	* @param aText Option text to include with the output
	*
	*/
	public static void finish (int aReference, int aSource, int aAction, String aText)
	{
		if (logStream != null &&
			aReference != -1 &&
			!profileEntries [aReference].available)
		{
			long theElapsedTime = System.currentTimeMillis () -
								  profileEntries [aReference].startTime;
			long theTotalMemory = runtime.totalMemory ();
			long theFreeMemory = runtime.freeMemory ();
			long theMemoryChange = theTotalMemory - profileEntries [aReference].totalMemory;
			theMemoryChange += profileEntries [aReference].freeMemory - theFreeMemory;
		
			profileEntries [aReference].logMessage.delete (0, 255);
			profileEntries [aReference].logMessage.append (sequenceNumber++);
			profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (sourceNames [aSource]);
			profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (actionCodeNames [aAction]);
			profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (theElapsedTime);
			profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (theMemoryChange);
			profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (theTotalMemory - theFreeMemory);
			/*profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (profileEntries [aReference].totalMemory);
			profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (theFreeMemory);
			profileEntries [aReference].logMessage.append (",");
			profileEntries [aReference].logMessage.append (profileEntries [aReference].freeMemory);*/
			profileEntries [aReference].logMessage.append (",");
			if (aText != null)
			{
				profileEntries [aReference].logMessage.append (aText);
			}
			
			logStream.println (profileEntries [aReference].logMessage.toString ());
			logStream.flush ();
			profileEntries [aReference].available = true;
		}
	}
	
	/**
	* Post a profile record.  This is a simpler version of the "finish" method, and assumes that the informational
	* information that was used when creating the ProfileEntry is still valid.
	*
	* @param aReference The reference returned from the profile start
	* @param aText Option text to include with the output
	*/
	public static void finish (int aReference, String aText)
	{
		if (logStream != null &&
			aReference != -1 &&
			!profileEntries [aReference].available)
		{
			finish (aReference,
					profileEntries [aReference].source,
					profileEntries [aReference].action,
					aText);
		}
	}
}