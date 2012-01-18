// =============================================================================
// com.bitmovers.maui.httpserver.HTTPServer
// =============================================================================

package com.bitmovers.maui.engine.httpserver;

import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;


// =============================================================================
// <<SINGLETON>> CLASS: HTTPServer 
// =============================================================================

/** 
  * HTTPServer SINGLETON <p>
  * This is the object which creates the ServerSocket, and establishes connections with
  * clients via HTTP.  It is designed to perform only the Socket connection activity so that
  * it can easily be replaced without affecting any other part of Maui
  *
  * @invisible
  */

public class HTTPServer
{
	// ---------------------------------------------------------------------------
	
	private static final int kDefaultPort = 4296;
	private int serverPort;
	protected InetAddress inetAddress = null;
	
	private static HTTPServer instance = new HTTPServer();
	
	private Thread serverSocketThread;
	private static Thread monitorThread;
	
	private boolean listening = false;
	
	private boolean initDone = false;
	
	private Vector registeredConnections = new Vector ();
	
	private String className;
	
	protected boolean secure = false;
	
	protected String portPropertyName = ServerConfigurationManager.MAUI_PORT;
	
	protected String realClassName = "HTTPServer";
	
	private com.bitmovers.maui.engine.Queue connectionQueue = new com.bitmovers.maui.engine.Queue ();
	
	private static boolean globalInitDone = false;
	
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: HTTPServer
	// ---------------------------------------------------------------------------
	
	/**
	* Simple constructor.  
	*/
	protected HTTPServer()
	{
	  // Set up the registered connections Vector...
	}
	
	/**
	* This is part of the Maui once-only initialization.  This method does some setup for the HTTPServer.
	* The reason there is an initialize method rather than doing doing initialization within the constructor is that
	* other Maui services may not be ready when the HTTPServer constructor is invoked.  Instead this method is explicitly invoked
	* as part of the Maui startup
	*/
	public void initialize ()
	{
		if (!initDone)
		{
		  //
		  //	Find out what port Maui will be running on
		  //
		  ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		  String thePortValue = theSCM.getProperty (portPropertyName);
		  
		  try
		  {
		    serverPort = Integer.parseInt (thePortValue);
		  }
		  catch (NumberFormatException e)
		  {
		    System.out.println(new WarningString("[HTTPServer] Bad port number: maui.port=" + thePortValue + ". Using default port " + kDefaultPort));
		    serverPort = kDefaultPort;
		  }
		  
		  if (serverPort > 0)
		  {
			  String theIpAddress = theSCM.getProperty (theSCM.MAUI_IP_ADDRESS);
			  if (theIpAddress != null)
			  {
			  	try
			  	{
			  		inetAddress = InetAddress.getByName (theIpAddress);
			  	}
			  	catch (UnknownHostException e)
			  	{
			  		System.err.println ("[HTTPServer] " + e);
			  	}
			  }
		  
			  // Let us know that we're listening...
			
			  this.listening = true;

		      // Start the server socket thread...
			
			  className = getClass ().getName ();
			  className = className.substring (className.lastIndexOf ('.') + 1);
			  this.serverSocketThread = new Thread(new Runnable() { public void run() { runServerSocketThread(); } },
			  									   className + "-ServerSocketThread");
		      this.serverSocketThread.setName(className + "-ServerSocketThread");
			  this.serverSocketThread.setPriority(Thread.MAX_PRIORITY);
			  this.serverSocketThread.start();

		      // Start the monitor thread...
		      
		      if (monitorThread != null)
		      {
				
				  this.monitorThread = new Thread(new Runnable() { public void run() { runMonitorThread(); } },
				  								  className + "-MonitorThread");
			      this.monitorThread.setName(className + "-MonitorThread");
				  this.monitorThread.setPriority(Thread.MIN_PRIORITY);
				  this.monitorThread.start();
			  }
		  }
		  initDone = true;
		}
		
		synchronized (this)
		{
			if (!globalInitDone)
			{
				globalInitDone = true;
				
				int theMinimum = 10;
				try
				{
					theMinimum = Integer.parseInt (ServerConfigurationManager.getInstance ().getProperty (
														ServerConfigurationManager.MAUI_CONNECTION_POOL_MINIMUM));
				}
				catch (NumberFormatException e)
				{
					theMinimum = 10;
				}
				
				for (int i = 0; i < theMinimum; i++)
				{
					new HTTPConnection ().finalize ();
				}
			}
		}
	}
	

	// ---------------------------------------------------------------------------
	// METHOD: runServerSocketThread
	// ---------------------------------------------------------------------------
	
	/**
	* This is the HTTPServer thread loop
	*/
	private void runServerSocketThread()
	{
	  System.out.println (new InfoString ("[HTTPServer] server thread"));
	  try
	   {
	   	ServerSocket serverSocket = null;
	  	try
	  	{
	    	serverSocket = createServerSocket (serverPort, inetAddress);
	   	}
	   	catch (IOException e)
	   	{
	   		System.out.println (new InfoString ("Port bind exception (" + e + ") on port " +
	   							serverPort + ".  Retrying in 2 seconds"));
	   		try
	   		{
	   			Thread.sleep (2000);
	   		}
	   		catch (Exception e2)
	   		{
	   		}
	   		serverSocket = createServerSocket (serverPort, inetAddress);
	   	}
	    
	    System.out.println(new InfoString("[" + realClassName + "] - Operational on port " + serverPort + "."));
	    Socket theSocket;
	    HTTPConnection theConnection;
	    while (this.listening)
	    {
	    	theSocket = serverSocket.accept ();
	    	theConnection = HTTPConnection.getConnection ();
	    	theConnection.startConnection (theSocket, secure);
	    }
	  }
	  catch (IOException exception)
	  {
	    System.out.println(new ErrorString("[" + realClassName + "] - Could not create a socket (IOException: " + exception.getMessage() + "). Port " + serverPort + " may already be in use. You can either free up this port (by killing the process tied to port " + serverPort + ") or try running Maui on a different port."));
		try
		{
			Thread.sleep (2000);
		}
		catch (InterruptedException e)
		{
		}
		System.exit (1);
	  }
	}
	
	protected void connectionDone (HTTPConnection aConnection)
	{
		connectionQueue.add (aConnection);
	}
	
	protected ServerSocket createServerSocket (int aServerPort, InetAddress aInetAddress)
		throws IOException
	{
		return (aInetAddress == null ?
					new ServerSocket (aServerPort) :
					new ServerSocket (aServerPort, 100, aInetAddress));
	}
	
	
	// ---------------------------------------------------------------------------
	// METHOD: runMonitorThread
	// ---------------------------------------------------------------------------
	
	/**
	* Not used
	*/
	private void runMonitorThread()
	{
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: registerConnection
	// ---------------------------------------------------------------------------
	
	/** registerConnection() is called by a HTTPConnection when it is 
	  * constructed.
	  *
	  * @param connection The HTTPConnection object
	  */
	
	protected void registerConnection(HTTPConnection connection)
	{
	  //this.registeredConnections.addElement(connection);
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: unregisterConnection
	// ---------------------------------------------------------------------------
	
	/** registerConnection() is called by a HTTPConnection when it is 
	  * closed (not destructed or GC'd, but closed).
	  *
	  * @param connection The HTTPConnection object
	  */
	
	protected void unregisterConnection(HTTPConnection connection)
	{
	  //this.registeredConnections.removeElement(connection);
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: getInstance
	// ---------------------------------------------------------------------------
	
	/**
	* Get a reference to the HTTPServer singleton.  This method enforces the Singleton pattern
	*
	* @return A reference to the HTTPServer
	*/
	public static HTTPServer getInstance()
	{
	  return instance;
	}

	
	// ---------------------------------------------------------------------------
}


// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof