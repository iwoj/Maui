package com.bitmovers.maui.monitor;

import java.net.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Date;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class MonitorClient
	implements Runnable
{
	private static final int MONITOR_AVERAGERESPONSETIME = 1;
	private static final int MONITOR_AVERAGEAPPLICATIONTIME = 2;
	private static final int MONITOR_CONNECTIONCOUNT = 3;
	private static final int MONITOR_SESSIONCOUNT = 4;
	private static final int MONITOR_APPLICATIONCOUNT = 5;
	private static final int MONITOR_THREADCOUNT = 6;
	private static final int MONITOR_CUSTOMMESSAGE = 7;
	
	private static final int MONITOR_SESSIONCREATED = 1;
	private static final int MONITOR_SESSIONDELETED = 2;
	private static final int MONITOR_APPLICATIONADDED = 3;
	private static final int MONITOR_APPLICATIONREMOVED = 4;
	private static final int MONITOR_APPLICATIONACTIVATED = 5;
	private static final int MONITOR_APPLICATIONDEACTIVATED = 6;
	private static final int MONITOR_STATS = 7;
	private static final int MONITOR_ERROR = 8;
	private static final int MONITOR_DETAILS = 9;
	
	public static final int MONITOR_SESSIONS = 100;
	public static final int MONITOR_APPLICATIONS = 101;
	
	private static final int REQUEST_SESSIONS = 10000;
	private static final int REQUEST_APPLICATIONS = 10001;
	private static final int REQUEST_STATS = 10002;
	private static final int REQUEST_SD = 1003;
	private static final int REQUEST_AD = 1004;
	private static final int REQUEST_KS = 1005;
	private static final int REQUEST_KA = 1006;
	private static final int REQUEST_LL = 1007;
	private static final int REQUEST_PING = 1008;
	private static final int REQUEST_KILL = 1009;
	
	private static final int MONITOR_LOGMESSAGE = 1000;
	
	private static final String CLI_HELP = "?";			// Help
	private static final String CLI_STATS = "stats";	// Get stats
	private static final String CLI_LS = "ls";			// List sessions
	private static final String CLI_LA = "la";			// List applications
	private static final String CLI_SD = "sd";			// Session details
	private static final String CLI_AD = "ad";			// Application details
	private static final String CLI_KS = "ks";			// Kill a session
	private static final String CLI_KA = "ka";			// Kill an application
	private static final String CLI_LL = "ll";			// Log level
	private static final String CLI_PING = "ping";		// Port ping
	private static final String CLI_KILL = "kill";		// Kill the maui server
	private static final String CLI_EXIT = "exit";
	
	private static final int TOKEN_HELP = 0;
	private static final int TOKEN_STATS = 1;
	private static final int TOKEN_LS = 2;
	private static final int TOKEN_LA = 3;
	private static final int TOKEN_SD = 4;
	private static final int TOKEN_AD = 5;
	private static final int TOKEN_KS = 6;
	private static final int TOKEN_KA = 7;
	private static final int TOKEN_LL = 8;
	private static final int TOKEN_PING = 9;
	private static final int TOKEN_KILL = 10;
	private static final int TOKEN_EXIT = 11;
	
	private static final int TOKEN_INVALID = -1;
	
	private static final String HELP_HELP = 	"help";
	private static final String HELP_STATS = 	"stats <on|off>                        - Turn stats on or off";
	private static final String HELP_LS = 		"ls                                    - List sessions";
	private static final String HELP_LA =		"la <sessionID>                        - List applications";
	private static final String HELP_SD = 		"sd <sessionID>                        - Session details";
	private static final String HELP_AD = 		"ad <applicationID>                    - Application details";
	private static final String HELP_KS = 		"ks <sessionID>                        - Kill session";
	private static final String HELP_KA = 		"ka <applicationID>                    - Kill application";
	private static final String HELP_LL = 		"ll <on|off>                           - Log level debug or default";	
	private static final String HELP_PING =     "ping <port> <tmo> \n" +
												"     [wait [email [repeat [kill]]]]   - Ping port with timeout";
	private static final String HELP_KILL = 	"kill                                  - Kill the maui server";
	private static final String HELP_EXIT = 	"exit                                  - Exit from this applicaiton";
	
	private static final String ACTION_HELP = "helpAction";
	private static final String ACTION_STATS = "statsAction";
	private static final String ACTION_LS = "lsAction";
	private static final String ACTION_LA = "laAction";
	private static final String ACTION_SD = "sdAction";
	private static final String ACTION_AD = "adAction";
	private static final String ACTION_KS = "ksAction";
	private static final String ACTION_KA = "kaAction";
	private static final String ACTION_LL = "llAction";
	private static final String ACTION_PING = "pingAction";
	private static final String ACTION_KILL = "killAction";
	private static final String ACTION_EXIT = "exitAction";
	
	private DataInputStream input;
	private DataOutputStream output;
	private DataInputStream cli;
	private Socket socket;
	private byte [] data = new byte [0];
	
	private Properties properties = new Properties ();
	private String smtpHost = null;
	private Session mailSession = null;
	
	private int waitCode = -2;
	
	private String server = null;
	private int port = 0;
	
	private Thread pingThread = null;
	private boolean pingThreadRunning = false;
	
	class Token
	{
		private final String cli;
		private final int token;
		private final String help;
		private Method cliAction = null;
		private String [] arguments;
		
		protected Token (String aCli,
						 int aToken,
						 String aHelp,
						 String aAction)
		{
			cli = aCli;
			token = aToken;
			help = aHelp;
			try
			{
				cliAction = MonitorClient.class.getDeclaredMethod (aAction,
																   new Class [] {Token.class,
																   				 String [].class});
			}
			catch (Exception e)
			{
				System.out.println (e);
			}
		}
		
		protected String getCli ()
		{
			return cli;
		}
		
		protected int getToken ()
		{
			return token;
		}
		
		protected void showHelp ()
		{
			System.out.println (help);
		}
		
		protected void setArguments (String [] aArguments)
		{
			arguments = aArguments;
		}
		
		protected String [] getArguments ()
		{
			return arguments;
		}
		
		protected boolean match (String aCommand)
		{
			return aCommand.equals (cli);
		}
		
		protected void action ()
		{
			if (cliAction != null)
			{
				try
				{
					cliAction.invoke (MonitorClient.this, new Object [] {this, arguments});
				}
				catch (Throwable e)
				{
					if (e instanceof InvocationTargetException)
					{
						e = ((InvocationTargetException) e).getTargetException ();
					}
					System.out.println (e);
				}
			}
		}
	}
	
	private Token invalidToken = new Token (null, TOKEN_INVALID, null, ACTION_HELP);
	
	private Token [] tokens = new Token [] {
								new Token (CLI_HELP, TOKEN_HELP, HELP_HELP, ACTION_HELP),
								new Token (CLI_STATS, TOKEN_STATS, HELP_STATS, ACTION_STATS),
								new Token (CLI_LL, TOKEN_LL, HELP_LL, ACTION_LL),
								new Token (CLI_LS, TOKEN_LS, HELP_LS, ACTION_LS),
								new Token (CLI_LA, TOKEN_LA, HELP_LA, ACTION_LA),
								new Token (CLI_SD, TOKEN_SD, HELP_SD, ACTION_SD),
								new Token (CLI_AD, TOKEN_AD, HELP_AD, ACTION_AD),
								new Token (CLI_KS, TOKEN_KS, HELP_KS, ACTION_KS),
								new Token (CLI_KA, TOKEN_KA, HELP_KA, ACTION_KA),
								new Token (CLI_PING, TOKEN_PING, HELP_PING, ACTION_PING),
								new Token (CLI_KILL, TOKEN_KILL, HELP_KILL, ACTION_KILL),
								new Token (CLI_EXIT, TOKEN_EXIT, HELP_EXIT, ACTION_EXIT)};
	
	public static void main (String [] aArgs)
	{
		if (aArgs.length > 1)
		{
			System.out.println ("Connecting to maui monitor at : " +
								aArgs [0] + ":" + aArgs [1]);
			try
			{
				new MonitorClient (aArgs);
			}
			catch (Exception e)
			{
				System.out.println (e.toString ());
				System.exit (2);
			}
		}
		else
		{
			System.out.println ("MonitorClient <ip address> <port>");
		}
	}
	
	private MonitorClient (String [] aArgs)
		throws IOException, NumberFormatException
	{
		server = aArgs [0];
		port = Integer.parseInt (aArgs [1]);
		socket = new Socket (server, Integer.parseInt (aArgs [1]));
		input = new DataInputStream (socket.getInputStream ());
		output = new DataOutputStream (socket.getOutputStream ());
		try
		{
			properties.load (new FileInputStream ("monitor.properties"));
		}
		catch (IOException e)
		{
		}
		smtpHost = properties.getProperty ("mail.smtp.host");
		if (smtpHost == null)
		{
			smtpHost = "mail.bopjet.net";
		}
		mailSession = Session.getDefaultInstance (properties, null);
		
		new Thread (this).start ();
		try
		{
			cli = new DataInputStream (new FileInputStream ("Init.txt"));
			cliInput (true);
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (cli != null)
			{
				cli.close ();
			}
		}
		cli = new DataInputStream (System.in);
		
		new Thread (new Runnable ()
			{
				public void run ()
				{
					cliInput (false);
				}
			}).start ();
	}
	
	private Token tokenize (String [] aArguments)
	{
		Token retVal = invalidToken;
		
		for (int i = 0;  i < tokens.length; i++)
		{
			if (tokens [i].match (aArguments [0]))
			{
				retVal = tokens [i];
				retVal.setArguments (aArguments);
				break;
			}
		}
		return retVal;
	}
	
	private void handleCommand (String [] aCommand)
	{
		Token theToken = tokenize (aCommand);
		theToken.action ();
	}
	
	private String [] parseLine (String aCommand)
	{
		Vector theItems = new Vector ();
		
		byte [] theCommand = aCommand.getBytes ();
		
		int theStart = 0;
		int i = 0;
		
		for (; i < theCommand.length; i++)
		{
			if (theCommand [i] == ' ')
			{
				theItems.addElement (new String (theCommand, theStart, i - theStart));
				theStart = ++i;
			}
		}
		theItems.addElement (new String (theCommand, theStart, i - theStart));
		Enumeration theCommands = theItems.elements ();
		String [] retVal = new String [theItems.size ()];
		i = 0;
		while (theCommands.hasMoreElements ())
		{
			retVal [i++] = (String) theCommands.nextElement ();
		}
		
		return retVal;
	}
	
	private String readLine ()
		throws IOException
	{
		StringBuffer theInput = new StringBuffer ();
		
		int theChar;
		
		while ((theChar = cli.read ()) != -1 &&
			    theChar != '\r' &&
			    theChar != '\n')
		{
			theInput.append ((char) theChar);
		}
		
		if (theChar == -1 &&
			theInput.length () == 0)
		{
			throw new EOFException ();
		}
		
		return theInput.toString ();
	}
	
	public void cliInput (boolean aIndirect)
	{
		boolean theRunning = true;
		while (theRunning)
		{
			try
			{
				System.out.print ("> ");
				String theCommand = readLine ();
				boolean theComment = false;
				
				if (aIndirect)
				{
					System.out.println (theCommand);
				}
				if (theCommand.trim ().length () > 0 &&
					!(theComment = theCommand.startsWith (";")))
				{
					handleCommand (parseLine (theCommand));
				}
				else if (aIndirect && !theComment)
				{
					break;
				}
			}
			catch (IOException e)
			{
				if (! (e instanceof EOFException))
				{
					System.err.println (e.toString ());
				}
				theRunning = !aIndirect;
				System.out.println ();
			}
		}
	}
	
	private int sendStatCode (byte [] aData, int aMessageLength)
	{
		int retVal;
		for (retVal = 0; retVal < aData.length; retVal++)
		{
			if ((char) aData [retVal] == ',')
			{
				int theCode = Integer.parseInt (new String (aData, 0, retVal++));
				
				switch (theCode)
				{
					case (MONITOR_AVERAGERESPONSETIME) :
						System.out.print ("Stat: Average Response Time ");
						break;
						
					case (MONITOR_AVERAGEAPPLICATIONTIME) :
						System.out.print ("Stat: Average Application Time ");
						break;
						
					case (MONITOR_CONNECTIONCOUNT) :
						System.out.print ("Stat: Connection Count ");
						break;
						
					case (MONITOR_SESSIONCOUNT) :
						System.out.print ("Stat: Session Count ");
						break;
						
					case (MONITOR_APPLICATIONCOUNT) :
						System.out.print ("Stat: Application Count ");
						break;
						
					case (MONITOR_THREADCOUNT) :
						System.out.print ("Stat: Thread Count ");
						break;
						
					case (MONITOR_CUSTOMMESSAGE) :
						System.out.print ("Custom: ");
						break;
				}
				
				break;
			}
		}
		return retVal;
	}
	
	private void decodeMessage (int aMessageCode, int aMessageLength)
		throws IOException
	{
		boolean theError = false;
		int theTrim = 0;
			
		if (aMessageLength > data.length)
		{
			data = new byte [aMessageLength];
		}
		
		if (aMessageLength > 0)
		{
			input.read (data, 0, aMessageLength);
		}
		
		switch (aMessageCode)
		{
			case (MONITOR_SESSIONCREATED) :
				System.out.print ("Session created: ");
				break;
				
			case (MONITOR_SESSIONDELETED) :
				System.out.print ("Session deleted: ");
				break;
				
			case (MONITOR_APPLICATIONADDED) :
				System.out.print ("Application added: ");
				break;
				
			case (MONITOR_APPLICATIONREMOVED) :
				System.out.print ("Application removed: ");
				break;
				
			case (MONITOR_LOGMESSAGE) :
				System.out.print ("Log message: ");
				break;
				
			case (MONITOR_APPLICATIONACTIVATED) :
				System.out.print ("Activation: ");
				break;
				
			case (MONITOR_APPLICATIONDEACTIVATED) :
				System.out.print ("Deactivation: ");
				break;
				
			case (MONITOR_SESSIONS) :	
				System.out.print ("Sessions: " );
				break;
				
			case (MONITOR_APPLICATIONS) :
				System.out.print ("Applications: " );
				break;
				
			case (MONITOR_STATS) :
				//System.out.print ("Stat: ");
				theTrim = sendStatCode (data, aMessageLength);
				break;
				
			case (MONITOR_DETAILS) :
				System.out.print ("Detail: " );
				break;
				
			case (MONITOR_ERROR) :
				System.out.print ("Error: ");
				theError = true;
				break;
		}
		
		if (aMessageLength - theTrim > 0)
		{
			System.out.write (data, theTrim, aMessageLength - theTrim);
		}
		System.out.write ('\n');
		System.out.flush ();
		
		synchronized (this)
		{
			if ((waitCode != -2 && theError) || waitCode == aMessageCode)
			{
				waitCode = -2;
				notify ();
			}
		}
	}
	
	public void run ()
	{
		try
		{
			while (true)
			{
				decodeMessage (input.readInt (), input.readInt ());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}
	
	private void sendRequestWithException (int aMessageCode, String [] aCommands)
		throws IOException
	{
		sendRequestWithException (aMessageCode, aMessageCode, aCommands);
	}
	
	private void sendRequestWithException (int aMessageCode, int aCheckCode, String [] aCommands)
		throws IOException
	{
		synchronized (this)
		{
			output.writeInt (aMessageCode);
			output.writeInt (aCommands.length - 1);
			for (int i = 1; i < aCommands.length; i++)
			{
				output.writeInt (aCommands [i].length ());
				output.writeBytes (aCommands [i]);
			}
			output.flush ();
			if (aCheckCode != -2)
			{
				waitCode = aCheckCode;
				try
				{
					wait ();
				}
				catch (InterruptedException e)
				{
					throw new InterruptedIOException ();
				}
			}
		}
	}
	
	
	private void sendRequest (int aMessageCode, String [] aCommands)
	{
		sendRequest (aMessageCode, aMessageCode, aCommands);
	}
	
	private void sendRequest (int aMessageCode, int aCheckCode, String [] aCommands)
	{
		try
		{
			sendRequestWithException (aMessageCode, aCheckCode, aCommands);
		}
		catch (IOException e)
		{
			System.out.println (e.toString ());
		}
	}
	
	protected void helpAction (Token aToken, String [] aCommands)
	{
		for (int i = 0; i < tokens.length; i++)
		{
			tokens [i].showHelp ();
		}
	}
	
	private void waitForInput ()
	{
		try
		{
			cli.read ();
			System.out.println ();
		}
		catch (IOException e)
		{
		}
	}
	
	protected void statsAction (Token aToken, String [] aCommands)
	{
		sendRequest (REQUEST_STATS, -2, aCommands);
	}
	
	protected void lsAction (Token aToken, String [] aCommands)
	{
		sendRequest (REQUEST_SESSIONS, MONITOR_SESSIONS, aCommands);
	}
	
	protected void llAction (Token aToken, String [] aCommands)
	{
		sendRequest (REQUEST_LL, -2, aCommands);
	}
	
	protected void killAction ()
	{
		try
		{
			final Thread theThread = Thread.currentThread ();
			new Thread (new Runnable ()
				{
					public void run ()
					{
						try
						{
							Thread.sleep (5000);
						}
						catch (InterruptedException e)
						{
						}
						theThread.interrupt ();
					}
				}).start ();
			sendRequestWithException (REQUEST_KILL, new String [] {""});
			System.out.println ("Normal exit");
			System.exit (0);
		}
		catch (IOException e)
		{
			System.out.println ("Exceptional exit");
			System.exit (1);
		}
	}
	
	protected void killAction (Token aToken, String [] aCommands)
	{
		killAction ();
	}
	
	protected void laAction (Token aToken, String [] aCommands)
	{
		if (aCommands.length < 2)
		{
			System.out.println ("Usage: la <sessionID>");
		}
		else
		{
			sendRequest (REQUEST_APPLICATIONS, MONITOR_APPLICATIONS, aCommands);
		}
	}
	
	protected void sdAction (Token aToken, String [] aCommands)
	{
		if (aCommands.length < 2)
		{
			System.out.println ("Usage: sd <sessionID>");
		}
		else
		{
			sendRequest (REQUEST_SD, MONITOR_STATS, aCommands);
		}
	}
	
	protected void adAction (Token aToken, String [] aCommands)
	{
		if (aCommands.length < 2)
		{
			System.out.println ("Usage: ad <applicationID>");
		}
		else
		{
			sendRequest (REQUEST_AD, MONITOR_STATS, aCommands);
		}
	}
	
	protected void ksAction (Token aToken, String [] aCommands)
	{
		if (aCommands.length < 2)
		{
			System.out.println ("Usage: ks <sessionID>");
		}
		else
		{
			sendRequest (REQUEST_KS, MONITOR_SESSIONDELETED, aCommands);
		}
	}
	
	protected void kaAction (Token aToken, String [] aCommands)
	{
		if (aCommands.length < 2)
		{
			System.out.println ("Usage: ka <applicationID>");
		}
		else
		{
			sendRequest (REQUEST_KA, MONITOR_APPLICATIONREMOVED, aCommands);
		}
	}
	
	private void handlePing (Token aToken, String [] aCommands)
	{
		stopPingThread ();
		try
		{
			final int thePort = Integer.parseInt (aCommands [1]);
			final int theTimeout = Integer.parseInt (aCommands [2]);
			final int theWaitTime = (aCommands.length > 3 ? Integer.parseInt (aCommands [3]) : 5);
			final String theEmail = (aCommands.length > 4 ? aCommands [4] : null);
			final boolean theRepeat = (aCommands.length > 5 &&
									   aCommands [5].equalsIgnoreCase ("false") ? false : true);
			final boolean theKill = (aCommands.length > 6 &&
									 aCommands [6].equalsIgnoreCase ("true") ? true : false);
				
			pingThreadRunning = true;
			Runnable theRunnable = new Runnable ()
									{
										public void run ()
										{
											pingRunnable (thePort,
														  theTimeout,
														  theWaitTime,
														  theEmail,
														  theRepeat,
														  theKill);
										}
									};
			if (theRepeat)
			{
				pingThread = new Thread (theRunnable);
				pingThread.start ();
			}
			else
			{
				theRunnable.run ();
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace ();
		}
	}
	
	private void sendEmail (String aEmail, int aTimeout)
	{
		try
		{
			Message theMessage = new MimeMessage (mailSession);
			theMessage.setFrom (new InternetAddress ("maui@" + server));
			theMessage.setRecipients (Message.RecipientType.TO, new InternetAddress [] {new InternetAddress (aEmail)});
			theMessage.setSubject ("No ping response at " + server + " for " + aTimeout + " milliseconds.");
			theMessage.setSentDate (new Date ());
			theMessage.setText ("Check the subject");
			Transport.send (theMessage);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
	
	protected void pingRunnable (final int aPort,
								 final int aTimeout,
								 final int aWaitTime,
								 final String aEmail,
								 final boolean aRepeat,
								 final boolean aKill)
	{
		try
		{
			Socket theSocket = new Socket (server, aPort);
			theSocket.setSoTimeout (aTimeout);
			InputStream theInput = theSocket.getInputStream ();
			OutputStream theOutput = theSocket.getOutputStream ();
			byte [] theMessage = "ping monitor\n".getBytes ();
			byte [] theResponse = new byte [7];
			boolean theTimeoutOccurred = false;
			pingThreadRunning = aRepeat;
			System.out.println ("The ping of death on " + server +
								" [timeout=" + aTimeout + "] [waittime=" + aWaitTime +
								"] [email=" + aEmail + "] [repeat=" + aRepeat +
								"] [kill=" + aKill + "]");
			do
			{
				try
				{
					theOutput.write (theMessage, 0, theMessage.length);
					theOutput.flush ();
					theInput.read ();
					theTimeoutOccurred = false;
				}
				catch (InterruptedIOException e)
				{
					System.out.println ("Ping timeout");
					if (aEmail != null && !theTimeoutOccurred)
					{
						theTimeoutOccurred = true;
						sendEmail (aEmail, aTimeout);
						if (aKill)
						{
							killAction ();
						}
					}
				}
				catch (Exception e)
				{
					pingThreadRunning = false;
					e.printStackTrace ();
				}
				
				if (pingThreadRunning)
				{
					try
					{
						Thread.sleep ((aWaitTime > 0 ? aWaitTime * 1000 : 5000));
					}
					catch (InterruptedException e)
					{
					}
				}
			}
			while (pingThreadRunning);
			try
			{
				theInput.close ();
			}
			catch (Exception e)
			{
			}
			
			try
			{
				theOutput.close ();
			}
			catch (Exception e)
			{
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
			pingThreadRunning = false;
			pingThread = null;
		}
	}
	
	private void stopPingThread ()
	{
		if (pingThread != null)
		{
			pingThreadRunning = false;
			pingThread.stop ();
			pingThread = null;
		}
	}
	
	protected void pingAction (Token aToken, String [] aCommands)
	{
		if (aCommands.length == 2 &&
			aCommands [1].equalsIgnoreCase ("stop"))
		{
			stopPingThread ();
		}
		else if (aCommands.length < 3)
		{
			System.out.println ("Usage: ping <port> <timeout> [email]");
		}
		else
		{
			handlePing (aToken, aCommands);
		}
	}
	
	protected void exitAction (Token aToken, String [] aCommands)
	{
		System.exit (0);
	}
}
