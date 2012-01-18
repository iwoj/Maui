// =============================================================================
// com.bitmovers.maui.engine.servlet.MauiRemoteServlet
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Enumeration;

import com.bitmovers.maui.engine.ServerConfigurationManager;
import com.bitmovers.maui.engine.httpserver.HTTPEventTranslator;
import com.bitmovers.maui.engine.httpserver.SessionMaximumException;
import com.bitmovers.maui.MauiRuntimeEngine;
import com.bitmovers.maui.MauiCookie;
import com.bitmovers.maui.engine.logmanager.InfoString;

/** This is one of the servlet adapters.  It uses sockets to communicate with
  * the actual servlet object.  The reason for this is to allow Maui to startup
  * separately from the servlet engine.  There are two reasons for this <p>
  * 1) Maui can be running on another node from the servlet engine
  * 2) It's easier to debug Maui if it is a separate application.
  */
public class MauiRemoteServlet
	implements Runnable
{
	private ServerSocket serverSocket;
	private boolean running = true;
	private static boolean initDone = false;
	
	public MauiRemoteServlet ()
		throws IOException
	{
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		int thePort = Integer.parseInt (theSCM.getProperty (theSCM.MAUI_SERVLET_PORT));
		if (thePort != -1)
		{
			serverSocket = new ServerSocket (thePort);
			new Thread (this).start ();
		}
	}
	
	public void run ()
	{
		while (running)
		{
			try
			{
				new MauiRemoteServletConnection (serverSocket.accept ());
			}
			catch (IOException e)
			{
			}
		}
	}
	
}

class MauiRemoteServletConnection extends MauiServletConnection
	implements Runnable
{
	private final Socket socket;
	private final ObjectInputStream input;
	private final ObjectOutputStream output;
	private final HTTPEventTranslator et;
	private boolean running = true;
	private final String hostName;
	
	protected MauiRemoteServletConnection (Socket aSocket)
		throws IOException
	{
		super ();
		socket = aSocket;
		hostName = socket.getInetAddress ().getHostAddress ();
		input = new ObjectInputStream (aSocket.getInputStream ());
		output = new ObjectOutputStream (aSocket.getOutputStream ());
		et = HTTPEventTranslator.getInstance ();
		new Thread (this).start ();
		System.out.println (new InfoString ("Created MauiRemoteServletConnection thread"));
	}
	
	public void run ()
	{
		try
		{
			while (running)
			{
				RemoteRequestObject theRequest = (RemoteRequestObject) input.readObject ();
				//
				//	Got the request.  Wrap it up for Maui, and send it the event translator
				//
				MauiServletResponse theResponse = null;
				MauiRemoteServletRequest theRequestObject = new MauiRemoteServletRequest (theRequest, hostName);
				try
				{
					theResponse =
						(MauiServletResponse) et.translateRequest (theRequestObject);
					theResponse.prepareResponse ();
				}
				catch (SessionMaximumException e)
				{
					theResponse = (MauiServletResponse) theRequestObject.createResponseObject ();
					theResponse.setErrorCode (null, MauiServletResponse.STATUS_SERVICEUNAVAILABLE);
				}
				//
				//	Send the response back to the remote servlet object
				//
				RemoteResponseObject theResponseObject = (theResponse.getErrorCode () != -1 ?
										new RemoteResponseObject (theResponse.getErrorCode ()) :
										new RemoteResponseObject (theResponse.getContent (),
																  theResponse.getContentType (),
																  theResponse.getSessionCookie (),
																  theResponse.getApplicationCookies (),
																  theResponse.getRedirection (),
																  theResponse.isLimitExceeded ()));
				output.writeObject (theResponseObject);
				output.flush ();
				output.reset ();
			}
		}
		catch (Exception e)
		{
			System.err.println ("[MauiRemoteServletConnection] " + e.toString ());
			//e.printStackTrace ();
		}
		finally
		{
			try
			{
				input.close ();
			}
			catch (IOException e1)
			{
			}
			
			try
			{
				output.close ();
			}
			catch (IOException e1)
			{
			}
		}
	}
	
}