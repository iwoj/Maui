// =============================================================================
// com.bitmovers.maui.engine.servlet.MauiRemoteServlet
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import com.bitmovers.maui.engine.ServerConfigurationManager;
import com.bitmovers.maui.engine.ApplicationManager;
import com.bitmovers.maui.engine.logmanager.InfoString;

/** The servlet manager checks if Maui is running standalone, using a socket
  * for communications with a servlet engine
  */
public class ServletManager
{
	private static ServletManager servletManager = new ServletManager ();
	private boolean initDone = false;
	
	private ServletManager ()
	{
	}
	
	public static ServletManager getInstance ()
	{
		return servletManager;
	}
	
	public void initialize ()
	{
		if (!initDone)
		{
			initDone = true;
			ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
			int thePort = Integer.parseInt (theSCM.getProperty (theSCM.MAUI_SERVLET_PORT));
			if (thePort != -1)
			{
				try
				{
					String theServletClassName = theSCM.getProperty (theSCM.MAUI_SERVLET_CLASS);
					Class.forName (theServletClassName).newInstance ();
					System.out.println (new InfoString ("[ServletManager] Servlet connector established on port " + thePort));
					System.out.println ("[ServletManager] (servlet class=" + theServletClassName + ")");
				}
				catch (Throwable e)
				{
					e.printStackTrace ();
				}
			}
		}
	}
}