// =============================================================================
// com.bitmovers.maui.engine.servlet.MauiServletProxy
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.Cookie;

import com.bitmovers.maui.MauiCookie;
import com.bitmovers.maui.engine.Queue;
import com.bitmovers.maui.engine.I_QueueFilter;

/** This is the servlet that functions as a maui proxy.  It manages
  * communications with the Maui engine.
  */
public class MauiServletProxy extends HttpServlet
{
	private ServerSocket serverSocket;
	private boolean running = true;
	private static boolean initDone = false;
	private int port;
	private int timeout = DEFAULT_TIMEOUT;
	private String host;
	protected Queue proxies = new Queue ();
	protected int total = 0;
	protected static boolean debug = false;
	
	private static final String PORT = "maui.servlet.port";
	private static final String HOST = "maui.servlet.host";
	private static final String DEBUG = "maui.servlet.debug";
	private static final String TIMEOUT = "maui.timeout";
	private static final int DEFAULT_PORT = 8090;
	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_TIMEOUT = 20000;
	
	public MauiServletProxy ()
	{
	}
	
	public void init ()
	{
		if (!initDone)
		{
			System.out.println ("------------ Maui servlet proxy start ------------");
			String theServletPort = getInitParameter (PORT);
			if (theServletPort == null)
			{
				port = DEFAULT_PORT;
			}
			else
			{
				try
				{
					port = Integer.parseInt (theServletPort);
				}
				catch (NumberFormatException e)
				{
					System.out.println (e.toString ());
					port = DEFAULT_PORT;
				}
			}
			host = getInitParameter (HOST);
			if (host == null || host == "")
			{
				host = DEFAULT_HOST;
			}
			
			String theDebug = getInitParameter (DEBUG);
			if (theDebug != null && theDebug.equals ("true"))
			{
				debug = true;
			}
			
			String theTimeout = getInitParameter (TIMEOUT);
			if (theTimeout != null)
			{
				try
				{
					timeout = Integer.parseInt (theTimeout);
				}
				catch (NumberFormatException e)
				{
					timeout = DEFAULT_TIMEOUT;
				}
			}
			
			System.out.println (PORT + " = " + theServletPort);
			System.out.println (HOST + " = " + host);
			System.out.println (DEBUG + " = " + debug);
			System.out.println (TIMEOUT + " = " + timeout);
			System.out.println ("------------ Maui servlet proxy loaded ------------");
			
			new Thread (new Runnable ()
				{
					public void run ()
					{
						houseKeeping ();
					}
				}).start ();
		}
	}
	
	protected void houseKeeping ()
	{
		while (true)
		{
			try
			{
				Thread.sleep (timeout);
			}
			catch (InterruptedException e)
			{
			}
			
			proxies.filteredRemoval (new I_QueueFilter ()
				{
					public boolean filter (Object aPayload)
					{
						return ((MauiProxy) aPayload).isExpired (timeout);
					}
				});
		}
	}
	
	protected void freeProxy (MauiProxy aMauiProxy)
	{
		synchronized (proxies)
		{
			proxies.add (aMauiProxy);
		}
	}
		
	protected MauiProxy getMauiProxy ()
		throws IOException
	{	
		MauiProxy retVal = null;
		synchronized (proxies)
		{
			while (retVal == null)
			{
				if (proxies.isEmpty ())
				{
					retVal = new MauiProxy (host, port, this);
					break;
				}
				else
				{
					retVal = (MauiProxy) proxies.remove ();
				}
			}
		}
		/*if (mauiProxy == null)
		{
			mauiProxy = new MauiProxy (host, port, this);
		}*/
		return retVal;
	}
	
	protected void performAction (HttpServletRequest aRequest, HttpServletResponse aResponse)
	{
		try
		{
			MauiProxy theProxy = getMauiProxy ();
			if (theProxy  == null)
			{
				aResponse.getOutputStream ().write ("Remote connection to Maui not established... cannot continue\n".getBytes ());
			}
			else
			{
				try
				{
					theProxy.performAction (aRequest, aResponse);
				}
				catch (IOException e)
				{
					performAction (aRequest, aResponse);
				}
			}
		}
		catch (IOException e)
		{
			System.out.println (e.toString ());
		}
		catch (ClassNotFoundException e)
		{
			System.out.println (e.toString ());
		}
	}
	
	protected void doGet (HttpServletRequest aRequest, HttpServletResponse aResponse)
	{
		performAction (aRequest, aResponse);
	}
	
	protected void doPost (HttpServletRequest aRequest, HttpServletResponse aResponse)
	{
		performAction (aRequest, aResponse);
	}
}

class MauiProxy
{
	private final MauiServletProxy servlet;
	private final Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	protected final int proxyRef;
	private long touchTime = 0;
	
	private static int proxyCounter = 0;
	
	protected MauiProxy (String aHost, int aPort, MauiServletProxy aServlet)
		throws IOException
	{
		servlet = aServlet;
		socket = new Socket (aHost, aPort);
		output = new ObjectOutputStream (socket.getOutputStream ());
		input = new ObjectInputStream (socket.getInputStream ());
		synchronized (MauiProxy.class)
		{
			proxyRef = ++proxyCounter;
		}
	}
	
	protected void handleCookies (HttpServletResponse aResponse, MauiCookie [] aCookies)
	{
		for (int i = 0; i < aCookies.length; i++)
		{
			Cookie theCookie = new Cookie (aCookies [i].getKey (),
										   aCookies [i].getValue ());
			String theString = null;
			
			if ((theString = aCookies [i].getPath ()) != null)
			{
				theCookie.setPath (theString);
			}
			
			if ((theString = aCookies [i].getDomain ()) != null)
			{
				theCookie.setDomain (theString);
			}
			
			aResponse.addCookie (theCookie);
		}
	}	
	protected void performAction (HttpServletRequest aRequest, HttpServletResponse aResponse)
		throws IOException, ClassNotFoundException
	{
		touchTime = System.currentTimeMillis ();
		//
		//	Send the message
		//
		output.writeObject (new RemoteRequestObject (aRequest));
		output.flush ();
		output.reset ();
		
		RemoteResponseObject theObject = (RemoteResponseObject) input.readObject ();
		if (theObject.getErrorCode () != -1)
		{
			aResponse.sendError (theObject.getErrorCode ());
		}
		else
		{
			handleCookies (aResponse, new MauiCookie [] {theObject.getSessionCookie ()});
			handleCookies (aResponse, theObject.getApplicationCookies ());
			String theRedirection = theObject.getRedirection ();
			if (theRedirection != null)
			{
				if (!theRedirection.startsWith ("mailto:") &&
					theRedirection.indexOf ("://") == -1)
				{
					//
					//	Fill full redirection URL
					//
					boolean theSecure = aRequest.isSecure ();
					StringBuffer theURL = new StringBuffer ((theSecure ? "https://" : "http://"));
					theURL.append (aRequest.getServerName ());
					int thePort = aRequest.getServerPort ();
					if (thePort != (theSecure ? 443 : 80))
					{
						theURL.append (":");
						theURL.append (Integer.toString (thePort));
					}
					if (!theRedirection.startsWith ("/"))
					{
						theURL.append ("/");
					}
					theURL.append (theRedirection);
					theRedirection = theURL.toString ();
				}
				//System.out.println ("Redirection = " + theRedirection);
				aResponse.sendRedirect (theRedirection);
			}
			else
			{
				aResponse.setContentType (theObject.getContentType ());
				byte [] theContent = theObject.getContent ();
				aResponse.setContentLength (theContent.length);
				OutputStream theOutput = aResponse.getOutputStream ();
				theOutput.write (theContent, 0, theContent.length);
				theOutput.flush ();
			}
		}
		
		if (MauiServletProxy.debug)
		{
			System.out.println ("Request time on proxy " + proxyRef + " = " +
							Long.toString (System.currentTimeMillis () - touchTime));
		}
		servlet.freeProxy (this);
		
	}
	
	protected boolean isExpired (int aTimeout)
	{
		boolean retVal = false;
		if ((System.currentTimeMillis () - touchTime) > aTimeout)
		{
			try
			{
				output.close ();
			}
			catch (Exception e)
			{
			}
			try
			{
				input.close ();
			}
			catch (Exception e)
			{
			}
			
			try
			{
				socket.close ();
			}
			catch (Exception e)
			{
			}
			
			retVal = true;
		}
		return retVal;
	}
				
}
