package com.bitmovers.maui;

import java.util.Date;
import com.bitmovers.maui.profiler.Profiler;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.engine.servlet.ServletManager;


// ========================================================================
// SINGLETON CLASS: MauiRuntimeEngine            (c) 2001 Bitmovers Systems
// ========================================================================

/** This is the entry point for Maui.  It handles all manager 
  * initialization and starts up all necessary services.
  * 
  */

public class MauiRuntimeEngine
{
	
	
	/** These are used for application profiling.  They refer to the source 
	  * of a profile operation, and the action being performed by the 
	  * source.
	  *
	  * @invisible
	  * 
	  */
	  
	public static final int SOURCE_RUNTIME_ENGINE = 0;
	public static final int SOURCE_SESSION = 1;
	public static final int SOURCE_APPLICATION = 2;
	public static final int SOURCE_RESOURCE = 3;
	public static final int SOURCE_APPLICATION_MANAGER = 4;
	public static final int SOURCE_REQUEST = 5;
	
	public static final int ACTION_INITIALIZE = 0;
	public static final int ACTION_CREATE = 1;
	public static final int ACTION_DESTROY = 2;
	public static final int ACTION_CACHE_HIT = 3;
	public static final int ACTION_GET = 4;
	
	public static boolean windowingEnvironmentAvailable = false;
	
	// Version numbers
	
	private static final int majorVersion = 1;
	private static final int minorVersion = 2;
	private static final int microVersion = 2;
	
	/** This field should be incremented during testing.
	  * 
	  * @invisible
	  * 
	  */
	  
	private static final int buildNumber = 1;
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	
	// ========================================================================
	// CLASS: MauiRuntimeEngine
	// ========================================================================

	/** Since the MauiRuntimeEngine is a Singleton, this is the reference to 
	  * the only instance of it.
	  *
	  * @invisible
	  *
	  */
	  
	private static MauiRuntimeEngine theInstance = new MauiRuntimeEngine();
	
	
	// ========================================================================
	// CLASS: expireMe
	// ========================================================================

	/** Turns on or off expiry. Intended for beta and free versions.
	  * 
	  * @invisible
	  * 
	  */
	  
	private static final boolean expireMe = false;
	
	
	// ========================================================================
	// CLASS: releaseDate
	// ========================================================================

	/** Release date of this version of Maui. Used to expire old versions.
	  *
	  * @invisible
	  * 
	  */
	  
	private static final Date releaseDate = new Date(101, 6, 15, 0, 0); // July 15, 2001
	
	
	// ========================================================================
	// CLASS: usableTime
	// ========================================================================

	/** The amount of usable time past the release date. This is used to 
	  * expire old versions.
	  *
	  * @invisible
	  * 
	  */
	  
	private static final long usableTime = (long)90 * (long)86400000; // 90 days
  	
  	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR: MauiRuntimeEngine
	// ----------------------------------------------------------------------
	
	private MauiRuntimeEngine()
	{
	}


	// ----------------------------------------------------------------------
	// METHOD: getInformation
	// ----------------------------------------------------------------------
	
	/** Returns information about Maui such as its version and copyright information.
	  *
	  */
	  
	public static final String getInformation()
	{
	  StringBuffer string = new StringBuffer();
	  
	  string.append("Bitmovers Maui Runtime Engine ");
	  string.append(getVersion());
	  string.append(" (Build ");
	  string.append(getBuildNumber());
	  string.append(")\n");
	  string.append("Copyright (c) 1999-2001 Bitmovers Systems Inc.\n");
	  string.append("All Rights Reserved.\n");
	  string.append("\n");
	  
	  return string.toString();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getVersion
	// ----------------------------------------------------------------------
	
	/** Returns a string containing Maui's version as String.
	  *
	  */
	  
	public static final String getVersion()
	{
		// Major version
		StringBuffer returnValue = new StringBuffer(Integer.toString(majorVersion));
		
		// Minor version
		if (minorVersion > 0 || microVersion > 0)
		{
			returnValue.append(".");
			returnValue.append(Integer.toString(minorVersion));
		}
		
		// Micro version
		if (microVersion > 0)
		{
			returnValue.append(".");
			returnValue.append(Integer.toString(microVersion));
		}
		
		return returnValue.toString();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMajorVersion
	// ----------------------------------------------------------------------
	
	/** Returns Maui's major version number. For example, if Maui's version
	  * is "1.3.22", this method will return <code>1</code>.
	  *
	  */
	  
	public static final int getMajorVersion()
	{
		return majorVersion;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMinorVersion
	// ----------------------------------------------------------------------
	
	/** Returns Maui's minor version number. For example, if Maui's version
	  * is "1.3.22", this method will return <code>3</code>.
	  *
	  */
	  
	public static final int getMinorVersion()
	{
		return minorVersion;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getMicroVersion
	// ----------------------------------------------------------------------
	
	/** Returns Maui's micro version number. For example, if Maui's version
	  * is "1.3.22", this method will return <code>22</code>.
	  *
	  */
	  
	public static final int getMicroVersion()
	{
		return microVersion;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getBuildNumber
	// ----------------------------------------------------------------------
	
	/** Returns Maui's build number. This is intended for pre-release version
	  * tracking only.
	  *
	  * @return Build number as integer.
	  */
	  
	public static final int getBuildNumber()
	{
		return buildNumber;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: isLicensed
	// ----------------------------------------------------------------------
	
	/** Returns a boolean indicating whether or not this installation of Maui
	  * is licensed.
	  * 
	  * @invisible
	  * 
	  */
	  
	public final boolean isLicensed()
	{
		return AuthorizationManager.getInstance().isAuthorized(AuthorizationManager.AUTHORIZATION_SERVLET);
	}
	
	
	// ----------------------------------------------------------------------
	// STATIC METHOD: getInstance
	// ----------------------------------------------------------------------

	/** Returns the instance of the <code>MauiRuntimeEngine</code>.
	  *
	  * @return The instance of the <code>MauiRuntimeEngine</code>.
	  * 
	  */

	public static MauiRuntimeEngine getInstance()
	{
		return theInstance;
	}


	// ----------------------------------------------------------------------
	// METHOD: printException
	// ----------------------------------------------------------------------
	// NOTE: This method should be moved to a more generic 'print/display'
	//       library class (such as a printstream replacement, etc.).	
	
	/** Prints an exception message, plus the traceback.
	  *
	  * @param exception the <code>Exception</code> to print out.
	  * 
	  */
	  
	public static void printException(Exception exception)
	{
		System.err.println("An " + exception.getClass().toString() + " has been thrown (" + exception.getMessage() + "):");
		exception.printStackTrace(System.err);
	}


	// ----------------------------------------------------------------------
	// METHOD: main
	// ----------------------------------------------------------------------

	/** The main method.
	  *
	  * @param  arguments the arguments must be key/value pairs, or a reference 
	  *                     to a file containing key/value pairs (@). If a file is 
	  *                     used then <code>java.util.Properties</code> load is used.
	  * 
	  */
	  
	public static void main (String arguments[])
	{
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		theSCM.initialize (arguments);
		
		try
		{
			String [] theSources = new String [] {"RuntimeEngine",
												  "Session",
												  "Application",
												  "Resource",
												  "Application Manager",
												  "Request"};
																						
			String [] theActions = new String [] {"Initialize",
												  "Create",
												  "Destroy",
												  "Cache Hit",
												  "Get"};
			Profiler.initialize (ServerConfigurationManager.getInstance ().getProperty ("maui.profile"),
													 true, 
													 theSources,
													 theActions);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		
		//	Other components can initialize themselves after the ServerConfiguration-
		//  Manager is intialized.
		//
    //  NOTE: The following code represents the *master* startup point for 
    //        all of the 'managers' and other persistent portions of the 
    //        Maui Runtime Engine.

		int theProfilerReference = Profiler.start (SOURCE_RUNTIME_ENGINE, ACTION_INITIALIZE);
		
		com.bitmovers.maui.engine.logmanager.LogManager.initialise();
		
		// Try to access native graphics with a Maui log window.
		try
		{
			LogManager.getInstance().addLogListener(MauiRuntimeWindow.getInstance());
			// Even though we have access to native graphics, we'll use PJA to
			// avoid certain rendering problems on certain platforms.
			ImageFactory.nativeGraphics = false;
			MauiRuntimeEngine.windowingEnvironmentAvailable = true;
		}
		catch (Throwable exception)
		{
			// No native graphics system found...
			ImageFactory.nativeGraphics = false;
			MauiRuntimeEngine.windowingEnvironmentAvailable = false;
		}
		
		// Check expiry.
		MauiRuntimeEngine.checkExpiry();
			
		ApplicationManager theAM = com.bitmovers.maui.engine.ApplicationManager.getInstance();
	
		theAM.initialize();
		com.bitmovers.maui.engine.httpserver.HTTPEventTranslator.getInstance().initialize();
		com.bitmovers.maui.engine.wmlcompositor.WMLCompositor.getInstance().initialize();
		com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor.getInstance().initialize();
		com.bitmovers.maui.MauiApplication.staticInitialize();
		com.bitmovers.maui.engine.httpserver.HTTPServer.getInstance().initialize();
		if (!ServerConfigurationManager.getInstance ().getProperty (ServerConfigurationManager.MAUI_SECURE_PORT).equals ("-1"))
		{
			com.bitmovers.maui.engine.httpserver.HTTPSecureServer.getInstance ().initialize ();
		}
		com.bitmovers.maui.engine.httpserver.HTTPSession.initialise();
		com.bitmovers.maui.engine.resourcemanager.ResourceManager.getInstance().initialise();
		com.bitmovers.maui.engine.cachemanager.CacheManager.getInstance();
		com.bitmovers.maui.engine.ProcessManager.getInstance().initialize ();
		com.bitmovers.maui.engine.ApplicationManager.getInstance();
		com.bitmovers.maui.engine.EventTranslationManager.getInstance();	
		com.bitmovers.maui.engine.ComponentManager.getInstance().initialize ();
		com.bitmovers.maui.engine.CompositionManager.getInstance();
		//com.bitmovers.maui.engine.ServerConfigurationManager.getInstance();
		com.bitmovers.maui.engine.htmlcompositor.HTMLCompositor.getInstance();
		com.bitmovers.maui.engine.wmlcompositor.WMLCompositor.getInstance();
		com.bitmovers.maui.components.MDesktop.getInstance();
		com.bitmovers.maui.engine.httpserver.HTTPServer.getInstance();
		com.bitmovers.maui.monitor.MonitorManager.getInstance().initialize();
		com.bitmovers.maui.engine.AuthorizationManager.getInstance().initialize();
    
		ServletManager.getInstance().initialize();
		System.out.println(new InfoString("Bitmovers Maui " + MauiRuntimeEngine.getVersion() + " (Build " + getBuildNumber() + ") started."));

		Profiler.finish(theProfilerReference, "Startup"); // This will include everything involved with startup
	}
		
	
	// ----------------------------------------------------------------------
	// METHOD: checkExpiry
	// ----------------------------------------------------------------------
	
	
	/** Checks the number of days (as a positive or negative float) until 
	  * Maui expires.
	  *
	  * @invisible
	  * 
	  */
	  
	private static final void checkExpiry()
	{
		// Check expiry.
		if (expireMe)
		{
			float daysUntilExpiry = MauiRuntimeEngine.daysUntilExpiry();
			StringBuffer expiryMessage = new StringBuffer();
			
			// This beta has expired.
			if (daysUntilExpiry <= 0)
			{
				System.out.println(new ErrorString("This version of Maui has expired. Please contact Bitmovers (http://bitmovers.com) for the latest version."));
			}
			// This beta has not yet expired.
			else
			{
				expiryMessage.append("This version of Maui will expire in ");
				
				// Less than a day left...
				if (daysUntilExpiry > 0 && daysUntilExpiry < 1)
				{
					expiryMessage.append("under a day. Please contact Bitmovers (http://bitmovers.com) for the latest version.");
				}
				// One day left...
				else if (daysUntilExpiry > 1 && daysUntilExpiry < 2)
				{
					expiryMessage.append("a day. Please contact Bitmovers (http://bitmovers.com) for the latest version.");
				}
				else
				{
					expiryMessage.append(Integer.toString((int)Math.floor(daysUntilExpiry)));
					expiryMessage.append(" days. ");
					
					// Less than 15 days left...
					if (daysUntilExpiry < 15)
					{
						expiryMessage.append("Please contact Bitmovers (http://bitmovers.com) for the latest version.");
					}
				}
				
				System.out.println(new WarningString(expiryMessage.toString()));
			}
		}
	}
	
		
	// ----------------------------------------------------------------------
	// METHOD: daysUntilExpiry
	// ----------------------------------------------------------------------
	
	/** Returns the number of days (as a positive or negative float) until 
	  * Maui expires.
	  *
	  * @return The number of days to Maui expiry.
	  * @invisible
	  * 
	  */
	  
	private static final float daysUntilExpiry()
	{
		long expiryTime = releaseDate.getTime() + usableTime;
		long now = (new Date()).getTime();
		
		// How much time is left? Will we ever know?
		long remainingTime = expiryTime - now;
		float remainingDays = (float)(remainingTime / 1000 / 60 / 60 / 24);
		
		return remainingDays;
	}
	
	
}


// ========================================================================
//                                               (c) 2001 Bitmovers Systems