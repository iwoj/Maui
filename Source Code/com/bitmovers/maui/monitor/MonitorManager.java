package com.bitmovers.maui.monitor;

import java.net.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.messagedispatcher.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.MauiApplication;

/**
* This is used as an interface to monitor Maui activities.  Hopefully, this will
* help to identify when Maui is in trouble.
*
* This is quick and dirty
*
* @invisible
*/
public class MonitorManager
	implements Runnable,
			   I_ConnectionListener,
			   I_ApplicationListener,
			   I_SessionListener,
			   I_ThreadListener
{
	private ServerSocket monitorSocket;
	
	private static MonitorManager monitorManager = new MonitorManager ();
	private boolean running = true;
	
	//
	//	Session stuff
	//
	private int sessionCount;
	private int applicationCount;
	
	//
	//	Application stuff
	//
	private long totalTime = 0;
	private long appMessages = 0;
	private double averageAppTime = 0;
		
	//
	//	Connection stuff
	//
	private long totalResponseTime = 0l;
	private long totalMessages = 0l;
	private double averageResponseTime = 0;
	
	private Hashtable connections = new Hashtable (10);
	private Hashtable applications = new Hashtable (10);
	
	//
	//	The "live" hashtable
	//
	protected NotifyHashtable stats = new NotifyHashtable ();
	
	private MonitorManager ()
	{
		stats.put (G_Monitor.MONITOR_AVERAGERESPONSETIME, new Double (0));
		stats.setAlwaysNotify (G_Monitor.MONITOR_AVERAGERESPONSETIME, true);
		stats.put (G_Monitor.MONITOR_AVERAGEAPPLICATIONTIME, new Double (0));
		stats.setAlwaysNotify (G_Monitor.MONITOR_AVERAGEAPPLICATIONTIME, true);
		stats.put (G_Monitor.MONITOR_CONNECTIONCOUNT, new Integer (0));
		stats.put (G_Monitor.MONITOR_SESSIONCOUNT, new Integer (0));
		stats.put (G_Monitor.MONITOR_APPLICATIONCOUNT, new Integer (0));
		stats.put (G_Monitor.MONITOR_THREADCOUNT, new Integer (0));
		stats.put (G_Monitor.MONITOR_CUSTOMMESSAGE, new String (""));
	}
	
	public void initialize ()
	{
		//System.out.println (new InfoString ("[MonitorManager] - starting"));
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		String theMonitorPort = theSCM.getProperty (theSCM.MAUI_MONITOR_PORT);
		
		if (theMonitorPort != null)
		{
			try
			{
				stats.put (G_Monitor.MONITOR_CONNECTIONCOUNT, new Integer (HTTPConnection.getConnectionCount ()));
				monitorSocket = new ServerSocket (Integer.parseInt (theMonitorPort));
				HTTPConnection.addConnectionListener (this);
				HTTPConnection.addThreadListener (this);
				HTTPSession.addApplicationListener (this);
				HTTPSession.addSessionListener (this);
				new Thread (this).start ();
			}
			catch (IOException e)
			{
				System.err.println ("[MonitorManager] " + e);
			}
			catch (NumberFormatException e)
			{
				System.err.println ("[MonitorManager] " + e);
			}				
		}
		/*else
		{
			System.out.println (new InfoString ("[MonitorManager] - no port"));
		}*/
	}
	
	public static MonitorManager getInstance ()
	{
		return monitorManager;
	}
	
	public void stop ()
	{
		running = false;
	}
	
	public void run ()
	{
		//System.out.println (new InfoString ("[MonitorManager] - running"));
		while (running)
		{
			try
			{
				Socket theSocket = monitorSocket.accept ();
				if (!running)
				{
					break;
				}
				
				//
				//	Create the monitor listener
				//
				new MonitorListener (this, theSocket);
			}
			catch (IOException e)
			{
				System.err.println ("[MonitorManager thread] " + e);
			}
		}
	}
	
	public synchronized void sendCustomMessage (String aCustomMessage)
	{
		stats.put (G_Monitor.MONITOR_CUSTOMMESSAGE, aCustomMessage);
	}
	
	/**
	* Notification of a new connection
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public synchronized void newConnection (ConnectionEvent aConnectionEvent)
	{
		stats.put (G_Monitor.MONITOR_CONNECTIONCOUNT, new Integer (HTTPConnection.getConnectionCount ()));
		//System.out.println (new InfoString ("New: Connection count = " + HTTPConnection.getConnectionCount ()));
	}
	
	/**
	* Notification of a connection closing
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public synchronized void connectionClosed (ConnectionEvent aConnectionEvent)
	{
		stats.put (G_Monitor.MONITOR_CONNECTIONCOUNT, new Integer (HTTPConnection.getConnectionCount ()));
		//System.out.println (new InfoString ("Closed: Connection count = " + HTTPConnection.getConnectionCount ()));
	}
	
	/**
	* Notification of the start of a request
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public synchronized void requestStarted (ConnectionEvent aConnectionEvent)
	{
		//System.out.println (new InfoString ("Request started"));
		connections.put (aConnectionEvent.getHTTPConnection (), new Long (aConnectionEvent.getEventTime ()));
	}
	
	/**
	* Notification of the end of a request
	*
	* @param aConnectionEvent The ConnectionEvent
	*/
	public synchronized void requestCompleted (ConnectionEvent aConnectionEvent)
	{
		Long theStartTime = (Long) connections.remove (aConnectionEvent.getHTTPConnection ());
		//System.out.println (new InfoString ("Request completed: " + theStartTime));
		if (theStartTime != null)
		{
			totalResponseTime += (aConnectionEvent.getEventTime () - theStartTime.longValue ());
			averageResponseTime = (double) totalResponseTime / (double) ++totalMessages;
			stats.put (G_Monitor.MONITOR_AVERAGERESPONSETIME, new Double (averageResponseTime));
			//System.out.println (new InfoString ("Average response time = " + totalResponseTime + " / " +
			//					totalMessages + " = " + averageResponseTime));
		}
	}
	
	/**
	* Notification of session creation
	*
	* @param aSessionEvent The event object describing the session 
	*/
	public void sessionCreated (SessionEvent aSessionEvent)
	{
		stats.put (G_Monitor.MONITOR_SESSIONCOUNT, new Integer (++sessionCount));
	}
	
	/**
	* Notification of session deletion
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void sessionDeleted (SessionEvent aSessionEvent)
	{
		stats.put (G_Monitor.MONITOR_SESSIONCOUNT, new Integer (--sessionCount));
	}
	
	/**
	* Notification of the addition of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationAdded (SessionEvent aSessionEvent)
	{
		stats.put (G_Monitor.MONITOR_APPLICATIONCOUNT, new Integer (++applicationCount));
	}
	
	/**
	* Notification of the removal of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationRemoved (SessionEvent aSessionEvent)
	{
		stats.put (G_Monitor.MONITOR_APPLICATIONCOUNT, new Integer (--applicationCount));
	}
		
	/**
	* Notification of an application being activated
	*
	* @param aApplicationEvent The event object describing the application
	*/
	public void applicationActivated (ApplicationEvent aApplicationEvent)
	{
		applications.put (aApplicationEvent.getMauiApplication (),
						  new Long (aApplicationEvent.getEventTime ()));
	}
	
	/**
	* Notification of an application being deactivated
	*
	* @param aApplicationEvent The event object describing the application
	*/
	public void applicationDeactivated (ApplicationEvent aApplicationEvent)
	{
		Long theTime = (Long) applications.remove (aApplicationEvent.getMauiApplication ());
		if (theTime != null)
		{
			totalTime += (aApplicationEvent.getEventTime () - theTime.longValue ());
			averageAppTime = (double) totalTime / (double) ++appMessages;
			stats.put (G_Monitor.MONITOR_AVERAGEAPPLICATIONTIME,
					   new Double (averageAppTime));
			//System.out.println (new InfoString ("Average application time = " +
			//					totalTime + " / " +
			//					appMessages + " = " + averageAppTime));
		}
		/*else
		{
			System.out.println (new InfoString ("Application not found: " +
												aApplicationEvent.getMauiApplication ().getName ()));
		}*/
	}

	public void threadEvent (ThreadEvent aEvent)
	{
		stats.put (G_Monitor.MONITOR_THREADCOUNT,
				   new Integer (aEvent.getThreadCount ()));
	}
	
}

class MonitorListener
	implements Runnable,
			   I_SessionListener,
			   I_ApplicationListener,
			   I_NotifyListener,
			   DetailLogListener
{	
	private Socket socket;
	private String hostName;
	private MonitorManager monitor;
	
	private DataOutputStream output;
	
	private DataInputStream input;
	
	private boolean running = true;
	private boolean exitDone = false;
	
	private Thread thread;
	
	class Command
	{
		private final int commandCode;
		private Method action;
		private String [] arguments;
		
		protected Command (int aCommandCode, String aAction)
		{
			commandCode = aCommandCode;
			try
			{
				action = MonitorListener.class.getDeclaredMethod (aAction,
																  new Class []
																 	{String [].class});
			}
			catch (Exception e)
			{
				System.err.println (e.toString ());
				action = null;
			}
		}
		
		protected int getCommandCode ()
		{
			return commandCode;
		}
		
		protected void setArguments (String [] aArguments)
		{
			arguments = aArguments;
		}
		
		protected void performAction ()
		{
			try
			{
				action.invoke (MonitorListener.this, new Object [] {arguments});
			}
			catch (Throwable e)
			{
				if (e instanceof InvocationTargetException)
				{
					e = ((InvocationTargetException) e).getTargetException ();
				}
				System.err.println (e);
			}
		}
	}
	
	protected Command [] commands = new Command [] {new Command (G_Monitor.REQUEST_SESSIONS,
																 G_Monitor.METHOD_SESSIONS),
													new Command (G_Monitor.REQUEST_APPLICATIONS,
																 G_Monitor.METHOD_APPLICATIONS),
													new Command (G_Monitor.REQUEST_LOGLEVEL,
																 G_Monitor.METHOD_LOGLEVEL),
													new Command (G_Monitor.REQUEST_SD,
																 G_Monitor.METHOD_SD),
													new Command (G_Monitor.REQUEST_AD,
																 G_Monitor.METHOD_AD),
													new Command (G_Monitor.REQUEST_KS,
																 G_Monitor.METHOD_KS),
													new Command (G_Monitor.REQUEST_KA,
																 G_Monitor.METHOD_KA),
													new Command (G_Monitor.REQUEST_STATS,
																 G_Monitor.METHOD_STATS),
													new Command (G_Monitor.REQUEST_KILL,
																 G_Monitor.METHOD_KILL)};
	protected MonitorListener (MonitorManager aMonitor, Socket aSocket)
		throws IOException
	{
		socket = aSocket;
		hostName = socket.getInetAddress ().getHostName ();
		System.out.println (new InfoString ("Monitor connection established from " +
											hostName));
		output = new DataOutputStream (socket.getOutputStream ());
		input = new DataInputStream (socket.getInputStream ());
		HTTPSession.addSessionListener (this);
		HTTPSession.addApplicationListener (this);
		//LogManager.getInstance ().addDetailLogListener (this);
		monitor = aMonitor;
		monitor.stats.addNotifyListener (G_Monitor.MONITOR_CUSTOMMESSAGE, this);
		(thread = new Thread (this)).start ();
	}
	
	public void run ()
	{
		System.out.println (new InfoString ("[MonitorManager] thread creation"));
		try
		{
			while (running)
			{
				processCommand ();
			}
		}
		catch (IOException e)
		{
			handleExit (e);
		}
	}
	
	private synchronized void handleExit (Exception aException)
	{
		if (!exitDone)
		{
			exitDone = true;
			//LogManager.getInstance ().removeDetailLogListener (this);
			HTTPSession.removeSessionListener (this);
			HTTPSession.removeApplicationListener (this);
			StringBuffer theMessage = new StringBuffer ("Monitor from ");
			theMessage.append (hostName);
			theMessage.append (" exiting");
			
			if (aException != null)
			{
				theMessage.append (" because: ");
				theMessage.append (aException.toString ());
			}
			System.out.println (new InfoString (theMessage.toString ()));
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
			
			running = false;
			thread.stop ();
		}
	}
	
	private synchronized void sendMessage (int aMessageCode, String aMessage)
	{
		try
		{
			output.writeInt (aMessageCode);
			output.writeInt (aMessage.length ());
			output.writeBytes (aMessage);
			output.flush ();
			//output.println (message);
		}
		catch (IOException e)
		{
			handleExit (e);
			System.err.println (e.toString ());
		}
	}
		
	private String getSessionInfo (SessionEvent aSessionEvent)
	{
		HTTPSession theSession = (HTTPSession) aSessionEvent.getSource ();
		MauiApplication theApplication = aSessionEvent.getMauiApplication ();
		
		StringBuffer retVal = new StringBuffer (Long.toString (aSessionEvent.getEventTime ()));
		retVal.append (",");
		retVal.append (theSession.getSessionID ());
		if (theApplication != null)
		{
			retVal.append (",");
			retVal.append (theApplication.getComponentID ());
		}
		return retVal.toString ();
	}
	
	/**
	* Notification of session creation
	*
	* @param aSessionEvent The event object describing the session 
	*/
	public void sessionCreated (SessionEvent aSessionEvent)
	{
		sendMessage (G_Monitor.MONITOR_SESSIONCREATED, getSessionInfo (aSessionEvent));
	}
	
	/**
	* Notification of session deletion
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void sessionDeleted (SessionEvent aSessionEvent)
	{
		sendMessage (G_Monitor.MONITOR_SESSIONDELETED, getSessionInfo (aSessionEvent));
	}
	
	/**
	* Notification of the addition of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationAdded (SessionEvent aSessionEvent)
	{
		sendMessage (G_Monitor.MONITOR_APPLICATIONADDED, getSessionInfo (aSessionEvent));
	}
	
	/**
	* Notification of the removal of an application
	*
	* @param aSessionEvent The event object describing the session
	*/
	public void applicationRemoved (SessionEvent aSessionEvent)
	{
		sendMessage (G_Monitor.MONITOR_APPLICATIONREMOVED, getSessionInfo (aSessionEvent));
	}

	/** This method is called on all registered <code>LogListener</code>s 
	  * when a new log message is reported.
	  * 
	  * @param  message  The message being reported to the maui log.
	  *
	  */	  
	public synchronized void processDetailLogMessage(String message)
	{
		sendMessage (G_Monitor.MONITOR_LOGMESSAGE, message);
	}
	
	private String getApplicationMessage (ApplicationEvent aApplicationEvent)
	{
		MauiApplication theApplication = aApplicationEvent.getMauiApplication ();
		StringBuffer retVal = new StringBuffer (Long.toString (aApplicationEvent.getEventTime ()));
		retVal.append (",");
		retVal.append (theApplication.getSessionID ());
		retVal.append (",");
		retVal.append (theApplication.getComponentID ());
		return retVal.toString ();
	}
	
	/**
	* Notification of an application being activated
	*
	* @param aApplicationEvent The event object describing the application
	*/
	public void applicationActivated (ApplicationEvent aApplicationEvent)
	{
		sendMessage (G_Monitor.MONITOR_APPLICATIONACTIVATED, getApplicationMessage (aApplicationEvent));
	}
	
	/**
	* Notification of an application being deactivated
	*
	* @param aApplicationEvent The event object describing the application
	*/
	public void applicationDeactivated (ApplicationEvent aApplicationEvent)
	{
		MauiApplication theApplication = aApplicationEvent.getMauiApplication ();
		sendMessage (G_Monitor.MONITOR_APPLICATIONDEACTIVATED, getApplicationMessage (aApplicationEvent));
	}
	
	protected Command readCommand ()
		throws IOException
	{
		Command retVal = null;
		int theCommandCode = input.readInt ();
		int theArgumentCount = input.readInt ();
		String [] theArguments = new String [theArgumentCount];
		for (int i = 0; i < theArgumentCount; i++)
		{
			int theStringLength = input.readInt ();
			byte [] theBytes = new byte [theStringLength];
			input.read (theBytes);
			theArguments [i] = new String (theBytes);
		}
		
		for (int i = 0; i < commands.length; i++)
		{
			if (commands [i].getCommandCode () == theCommandCode)
			{
				retVal = commands [i];
				retVal.setArguments (theArguments);
				break;
			}
		}
		return retVal;
	}
	
	protected void processCommand ()
		throws IOException
	{
		Command theCommand = readCommand ();
		if (theCommand != null)
		{
			theCommand.performAction ();
		}
	}
	
	protected void requestSessions (String [] aArguments)
	{
		HTTPSession [] theSessions = HTTPSession.getAllSessions ();
		StringBuffer theMessage = new StringBuffer ();
		for (int i = 0; i < theSessions.length; i++)
		{
			theMessage.append (theSessions [i].getSessionID ());
			theMessage.append (" [ip:");
			theMessage.append (theSessions [i].getClientName ());
			theMessage.append ("],");
		}
		
		if (theMessage.length () > 0)
		{
			theMessage.setLength (theMessage.length () - 1);
		}
		sendMessage (G_Monitor.MONITOR_SESSIONS, theMessage.toString ());
	}
	
	protected void requestApplications (String [] aArguments)
	{
		HTTPSession theSession = HTTPSession.getSession (aArguments [0]);
		if (theSession != null)
		{
			MauiApplication [] theApplications = theSession.getCachedMauiApplications ();
			StringBuffer theMessage = new StringBuffer (aArguments [0]);
			theMessage.append (",");
			for (int i = 0; i < theApplications.length; i++)
			{
				theMessage.append (theApplications [i].getComponentID ());
				theMessage.append (",");
			}
			theMessage.setLength (theMessage.length () - 1);
			sendMessage (G_Monitor.MONITOR_APPLICATIONS, theMessage.toString ());
		}
		else
		{
			sendMessage (G_Monitor.MONITOR_ERROR, "No such session " + aArguments [0]);
		}
	}
	
	private void sendStats ()
	{
		Enumeration theKeys = monitor.stats.keys ();
		while (theKeys.hasMoreElements ())
		{
			Object theKey = theKeys.nextElement ();
			sendMessage (G_Monitor.MONITOR_STATS, buildMonitorMessage (theKey, monitor.stats.get (theKey)));
		}
	}
	
	protected void requestStats (String [] aArgument)
	{
		if (aArgument [0].equals ("on"))
		{
			//
			//	Listen on stats
			//
			monitor.stats.addNotifyListener (null, this);
			sendStats ();
		}
		else
		{
			monitor.stats.removeNotifyListener (null, this);
		}
	}
	
	protected void requestKill (String [] aArgument)
	{
		System.out.println (new WarningString ("Monitor Client requested server kill"));
		//
		//	Summarily send this puppy to sleep
		//
		sendMessage (G_Monitor.REQUEST_KILL, "");
		try
		{
			Thread.sleep (1000);
		}
		catch (InterruptedException e)
		{
		}
		System.exit (1);
	}
	
	protected void requestLogLevel (String [] aArgument)
	{
		if (aArgument [0].equals ("on"))
		{
			LogManager.getInstance ().addDetailLogListener (this);
		}
		else
		{
			LogManager.getInstance ().removeDetailLogListener (this);
		}	
	}
	
	protected void requestSD (String [] aArguments)
	{
		HTTPSession theSession = HTTPSession.getSession (aArguments [0]);
		if (theSession != null)
		{
			sendMessage (G_Monitor.MONITOR_DETAILS,
						 "session," +
						 theSession.getSessionID () +
						 "," + 
						 theSession.getClientName ());
		}
		else
		{
			sendMessage (G_Monitor.MONITOR_ERROR, "No such session " + aArguments [0]);
		}
	}
	
	protected void requestAD (String [] aArguments)
	{
		Object theObject = ComponentManager.getInstance ().getComponent (aArguments [0]);
		if (theObject instanceof MauiApplication)
		{
			MauiApplication theApplication = (MauiApplication) theObject;
			sendMessage (G_Monitor.MONITOR_DETAILS,
						 "application," +
						 theApplication.getComponentID () + "," +
						 theApplication.getApplicationAddress ());
		}
		else
		{
			sendMessage (G_Monitor.MONITOR_ERROR, "No such application " + aArguments [0]);
		}
	}
	
	protected void requestKS (String [] aArguments)
	{
		HTTPSession theSession = HTTPSession.getSession (aArguments [0]);
		if (theSession != null)
		{
			theSession.removeSession (theSession);
		}
		else
		{
			sendMessage (G_Monitor.MONITOR_ERROR, "No such session " + aArguments [0]);
		}
	}
	
	protected void requestKA (String [] aArguments)
	{
		Object theObject = ComponentManager.getInstance ().getComponent (aArguments [0]);
		if (theObject instanceof MauiApplication)
		{
			MauiApplication theApplication = (MauiApplication) theObject;
			theApplication.getSession ().removeApplication (theApplication);
		}
		else
		{
			sendMessage (G_Monitor.MONITOR_ERROR, "No such application " + aArguments [0]);
		}
	}
	
	private String buildMonitorMessage (Object aCode, Object aValue)
	{
		StringBuffer retVal = new StringBuffer (aCode.toString ());
		retVal.append (",");
		retVal.append (aValue.toString ());
		return retVal.toString ();
	}
	
	private String buildMonitorMessage (NotifyEvent aEvent)
	{
		return buildMonitorMessage (aEvent.getKey (),
									aEvent.getNewValue ());
	}
	
	/**
	* Notification of a parameter change
	*
	* @param aEvent The NotifyEvent
	*/ 
	public void notify (NotifyEvent aEvent)
	{
		sendMessage (G_Monitor.MONITOR_STATS, buildMonitorMessage (aEvent));
	}
}
	
	
	