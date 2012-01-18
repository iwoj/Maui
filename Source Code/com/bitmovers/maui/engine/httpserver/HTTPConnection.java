package com.bitmovers.maui.engine.httpserver;

import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.engine.messagedispatcher.*;
import com.bitmovers.maui.profiler.Profiler;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;


// ========================================================================
// CLASS: HTTPConnection 
// ========================================================================

/** This class manages any HTTP client connection.  Connection pooling and 
  * thread pooling is also managed in this class.
  * 
  * @invisible
  * 
  */

public class HTTPConnection
{
	private final int NEWCONNECTION = 0;
	private final int CONNECTIONCLOSED = 1;
	private final int REQUESTSTARTED = 2;
	private final int REQUESTCOMPLETED = 3;
	
	private int connectionState = -1;
	
	private Socket socket;

	private String infoClientHostname;
	private long infoTimer = System.currentTimeMillis();
	private String infoQuery;
	private String queryString = "";
	private String infoSession;
	private String method = "none";

	private DataInputStream requestStream;
	private InputStream inputStream;
	private OutputStream responseStream;

	private Thread inputThread;
	private boolean lastKeepAlive = false;
	private boolean secure = false;
	private static PrintStream errorStream = null;
	private static String currentLogLevel = null;
	private int localPort = -1;
	private HTTPServer server;
	
	protected static Object synchObject = new Object ();
	
	private static Vector connectionListeners = new Vector ();
	private static I_ConnectionListener [] listenersArray = new I_ConnectionListener [0];
	
	private static int threadNumber = 0;
	
	private static int connectionCount = 0;
	
	private static com.bitmovers.maui.engine.Queue connectionQueue = new com.bitmovers.maui.engine.Queue ();
	private static int minimum;
	
	private boolean isConnectionClosed = false;
	
	private byte [] buffer = new byte [0];
	private StringBuffer queries = new StringBuffer ();
	
	private static boolean firstTime = true;
	private static boolean keepAlive = false;
	private static boolean threadPooling = false;
	private static int bufferSize;
	private static int ageLimit;
	private byte [] streamBuffer;
	private byte [] inputBuffer;
	private long touchTime = 0;
	private boolean sessionKeepAlive = true;
	
	//
	// This is the dispatcher for Thread pooling
	//
	private static MessageDispatcher messageDispatcher =
		new MessageDispatcher (new MessageThreadFactory ()
		{
			public A_MessageThread createMessageThread ()
			{
				return new A_MessageThread ()
					{
						public void processMessage (Object aMessage)
						{
							HTTPConnection theHTTPConnection =
								(HTTPConnection) aMessage;
							theHTTPConnection.runInputThread ();
						}
					};
			}
		});
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR: HTTPConnection
	// ----------------------------------------------------------------------
	
	/** Simple constuctor.
	  *
	  * @param socket   The socket created by the server socket
	  * @param aSecure  A boolean indicating if this is a secure connection or not
	  * 
	  */
	  
	public HTTPConnection ()
	{
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		connectionCount++;
		if (firstTime)
		{
			firstTime = false;
			keepAlive = theSCM.getProperty (theSCM.MAUI_CONNECTION_KEEPALIVE).equalsIgnoreCase ("true");
			threadPooling = theSCM.getProperty (theSCM.MAUI_THREAD_POOLING).equalsIgnoreCase ("true");
			int theValue;
			try
			{
				theValue = Integer.parseInt (theSCM.getProperty (theSCM.MAUI_CONNECTION_BUFFER_SIZE));
			}
			catch (NumberFormatException e)
			{
				theValue = 2000;
			}
			bufferSize = theValue;
			
			try
			{
				theValue = Integer.parseInt (theSCM.getProperty (theSCM.MAUI_CONNECTION_POOL_MINIMUM));
			}
			catch (NumberFormatException e)
			{
				theValue = 10;
			}
			minimum = theValue;
			
			try
			{
				theValue = Integer.parseInt (theSCM.getProperty (theSCM.MAUI_CONNECTION_AGE_LIMIT));
			}
			catch (NumberFormatException e)
			{
				theValue = 10000;
			}
			ageLimit = theValue;
						
			new Thread (new Runnable ()
				{
					public void run ()
					{
						houseKeeping ();
					}
				}).start ();
		}
		streamBuffer = new byte [bufferSize];
		inputBuffer = new byte [bufferSize];
	}
		
	protected void startConnection (Socket aSocket, boolean aSecure)
	{
		isConnectionClosed = false;
	    socket = aSocket;
	    try
	    {
	    	socket.setReceiveBufferSize (20000);
	    	socket.setSendBufferSize (20000);
	   	}
	   	catch (IOException e)
	   	{
	   	}
	   	
		secure = aSecure;
	    infoClientHostname = this.socket.getInetAddress().getHostAddress();
	    localPort = socket.getLocalPort ();
		notifyListeners (NEWCONNECTION);
		startNewRequest ();
	}
	
	private void startNewRequest ()
	{
		queryString = "";
		method = "none";
		if (threadPooling)
		{
		  messageDispatcher.postMessage (this);
		}
		else
		{
		  this.inputThread = new Thread(new Runnable() { public void run() { runInputThread();}},
		  								"HTTPConnection-InputThread " + threadNumber++);
		  this.inputThread.setPriority(Thread.NORM_PRIORITY);
		  this.inputThread.start();
		}
		lastKeepAlive = keepAlive;
	}
	
	public static void addThreadListener (I_ThreadListener aListener)
	{
		messageDispatcher.addThreadListener (aListener);
	}
	
	public static void removeThreadListner (I_ThreadListener aListener)
	{
		messageDispatcher.removeThreadListener (aListener);
	}
		
	public static HTTPConnection getConnection ()
	{
		HTTPConnection retVal = null;
		synchronized (connectionQueue)
		{
			retVal = (HTTPConnection) connectionQueue.removeTail ();
			if (retVal == null)
			{
				retVal = new HTTPConnection ();
			}
			retVal.touch ();
		}
		return retVal;
	}
	
	public static void releaseConnection (HTTPConnection aConnection)
	{
		synchronized (connectionQueue)
		{
			connectionQueue.add (aConnection);
		}
	}
	
	private void touch ()
	{
		touchTime = System.currentTimeMillis ();
	}
	
	private void houseKeeping ()
	{
		while (true)
		{
			try
			{
				Thread.sleep (ageLimit);
				synchronized (connectionQueue)
				{
					int theConnectionCount = connectionCount;
					if (connectionQueue.size () > minimum)
					{
						connectionQueue.filteredRemoval (new I_QueueFilter ()
							{
								public boolean filter (Object aPayload)
								{
									boolean retVal = (connectionQueue.getQueueSize () > minimum &&
														(aPayload == null ||
														((HTTPConnection) aPayload).isExpired (ageLimit)));
									if (retVal)
									{
										--connectionCount;
									}
									return retVal;
								}
							});
					}
					else
					{
						for (int i = connectionQueue.size (); i < minimum; i++)
						{
							new HTTPConnection ().finalize ();
						}
					}
					
					if (theConnectionCount != connectionCount)
					{
						notifyListeners (CONNECTIONCLOSED);
					}
				}
				
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	protected int getState ()
	{
		return connectionState;
	}
	
	protected String getQueryString ()
	{
		return queryString;
	}
	
	protected String getMethod ()
	{
		return method;
	}
	
	private boolean isExpired (long aExpiryTime)
	{
		boolean retVal = (System.currentTimeMillis () - touchTime > aExpiryTime);
		return (System.currentTimeMillis () - touchTime > aExpiryTime);
	}
	
	private static final int CODE_LOGLEVEL = 1;
	private static final int CODE_PING = 2;
	
	private static final int CODE_NONE = -1;
	
	private int getCommandCode (String aString)
	{
		int retVal = CODE_NONE;
		
		String theString = aString.toLowerCase ();
		if (theString.startsWith ("loglevel="))
		{
			retVal = CODE_LOGLEVEL;
		}
		else if (theString.startsWith ("ping"))
		{
			retVal = CODE_PING;
		}
		return retVal;
	}
	
	private boolean checkInternalMessage (String aString)
	{
		int theCode = CODE_NONE;
		boolean retVal = (aString == null ?
							false :
							(theCode = getCommandCode (aString)) != CODE_NONE);
		
		if (retVal)
		{
			switch (theCode)
			{
				case (CODE_LOGLEVEL) : 
					//
					//	Get the new log level
					//
					String theNewLogLevel = aString.toLowerCase ().substring (9);
					if (theNewLogLevel.endsWith ("\r"))
					{
						theNewLogLevel = theNewLogLevel.substring (0, theNewLogLevel.length () -1);
					}
					ServerConfigurationManager.getInstance ().setProperty (ServerConfigurationManager.MAUI_LOG_THRESHOLD,
																		   theNewLogLevel);
					LogManager.resetLogLevel ();
					String theNewLogLevelMessage = "Log level changed to " + theNewLogLevel + "\n\r";
					try
					{
						((MyBufferedOutputStream) responseStream).write (theNewLogLevelMessage.getBytes (), 0, theNewLogLevelMessage.length ());
						responseStream.flush ();
					}
					catch (IOException e)
					{
						e.printStackTrace ();
					}
					break;
					
				case (CODE_PING) :
					String theResponseString = (aString.length () > 5 ? aString.substring (5) : "ping");
					try
					{
						((MyBufferedOutputStream) responseStream).write (theResponseString.getBytes (), 0, theResponseString.length ());
						responseStream.flush ();
					}
					catch (IOException e)
					{
						e.printStackTrace ();
					}
					break;
			}		
		}
		return retVal;
	}
			

	// ----------------------------------------------------------------------
	// METHOD: readHeader
	// ----------------------------------------------------------------------
	
	/** Read an HTTP header.
	  *
	  * @return A String representation of the HTTP header record.
	  *
	  */
	  
	private String readHeader()
	{
		String retVal = null;
		boolean theLogLevelMessage = true;
		StringBuffer theReadBuffer = new StringBuffer (512);
		
		while (theLogLevelMessage)
		{
		
			int theBuffSize = 0;
			int theChar;
			boolean theIsEmpty = false;
			
			try
			{
				theReadBuffer.setLength (0);
				while ((theChar = inputStream.read ()) != '\n')
				{
					if (theChar == -1)
					{
						lastKeepAlive = false;
						theIsEmpty = true;
						break;
					}
					theReadBuffer.append ((char) theChar);
					theBuffSize++;
				}
			}
			catch (IOException e)
			{
				lastKeepAlive = false;
				theIsEmpty = true;
			}
			
			//
			//	For debugging.  A special message can be sent:
			//	LOGLEVEL=STRING
			//	This causes the following to happen:
			//
			//  - The new log level is set in the server configuration manager
			//  - The log manager is reset
			//	- A simple message "Log level set to <LOGLEVEL> is sent back to the issuer
			//
			retVal = (theIsEmpty ? null : theReadBuffer.toString ());
			
			theLogLevelMessage = checkInternalMessage (retVal);
		}
		
		return retVal;
	}
	

	// ----------------------------------------------------------------------
	// METHOD: runInputThread
	// ----------------------------------------------------------------------
	
	/** This is the the method which performs the I/O with the socket.
	  *
	  */
	  
	private void runInputThread()
	{
	  boolean theKeepAlive = true;

	  method = "";
	  queryString = "";
	  connectionState = 100;
	  int accessCount = 0;
	  //Thread.currentThread ().setName("HTTPConnection-InputThread " + threadNumber++);
	  int theReference = Profiler.start (MauiRuntimeEngine.SOURCE_REQUEST,
	  									 MauiRuntimeEngine.ACTION_CREATE);
	  HTTPRequest request = null;
	  try
	  {
		responseStream = new MyBufferedOutputStream (socket.getOutputStream (), streamBuffer);
		MyBufferedInputStream theInput = new MyBufferedInputStream (socket.getInputStream (), inputBuffer);
	    this.requestStream = new DataInputStream(theInput);
	  	//inputStream = theInput;
	  	inputStream = requestStream;
	    
	    boolean theLastKeepAlive = false;
	    byte [] theReadBytes = new byte [256];

      // Read first request line, obtain the method and queryString...

		while (theKeepAlive)
		{
	      boolean reading = true;
		  String readData = "";
		  long theEventTime = System.currentTimeMillis ();
      	  String readLine = readHeader ();
      	  
      	  if (readLine != null)
      	  {
		  		notifyListeners (REQUESTSTARTED, theEventTime);
			      //String readLine = this.requestStream.readLine();

		        StringTokenizer tokens = new StringTokenizer(readLine);
		        accessCount++;
		        method = tokens.nextToken().toString();
		        queryString = tokens.nextToken().toString();
		        System.out.println(new DebugString("[HTTPConnection] method = " + method + " queryString = " + queryString));
		   }
		   else
		   {
		   		reading = false;
		   		theKeepAlive = false;
		   		lastKeepAlive = false;
		   }

	      // Read through the rest of the lines...
	      
	      String theURL = null;
	      Hashtable theRawHeaders = new Hashtable ();
	      String theKey;
	      String theValue;
	      int theContentLength = 0;
		  while (reading)
		  {
		      readLine = this.requestStream.readLine();
		      readData = readLine;// + "\n";
		      if (readLine == null)
		      {
		      	reading = false;
		      }
			  else if (!readLine.equals (""))
			  {
			  	int theColon = readLine.indexOf (":");
			  	theKey = readLine.substring (0, theColon).toLowerCase ();
			  	theValue = readLine.substring (theColon + 2);
			  	if (theKey.equals ("content-length"))
			  	{
			  		try
			  		{
			  			theContentLength = Integer.parseInt (theValue);
			  		}
			  		catch (NumberFormatException exception)
			  		{
			  			//
			  			// PG 2001.08.01
			  			// Some browsers have a space after their content length
			  			// header value. This should take care of it.
			  			//
			  			theContentLength = Integer.parseInt (theValue.trim());
			  		}
			  	}
			  	else if (theKey.equals ("connection") &&
			  			 keepAlive)
			  	{
			  		lastKeepAlive = (theValue.equalsIgnoreCase ("Keep-Alive"));
			  	}
			  	theRawHeaders.put (theKey, theValue);
			  }
		      else
		      {
		        if (method.equalsIgnoreCase("POST"))
		        {
		        	if (theURL == null)
		        	{
		        		theURL = queryString;
		        	}
    	
					// NOTE: If this is a POST, then we have to obtain the 'queries'
					//       from the passed data that is following the headers.
						        
					queries.ensureCapacity (theContentLength + 11);
					queries.setLength (0);
					queries.append ("x-queries: ");

					//BufferedReader reader = new BufferedReader(new InputStreamReader(this.requestStream));
					int theCharacter = 0;
					
					if (buffer.length < theContentLength)
					{
						buffer = new byte [theContentLength];
					}
					
					requestStream.read (buffer, 0, theContentLength);
					
					int theAvailable;
					while ((theAvailable = requestStream.available ()) != 0)
					{
						//
						//	Netscape Navigator 4.08 sends an extra \r\n after the data (in excess of the Content-length).
						//	Ignoring these characters cause Navigator to barf.  So, just skip over them, which should
						//	clear out the socket input buffer.
						//
						requestStream.skip ((long) theAvailable);
					}

					for (int i = 0; i < theContentLength; i++)
					{
						queries.append ((char) buffer [i]);
					}
					readData += queries.toString (); //("x-queries: " + queries);
		        }
		        else if (method.equalsIgnoreCase("GET"))
		        {
		          // NOTE: If this is a GET, then we have to obtain both the
		          //			 URL and the 'queries' contained with the passed
		          //       URL (the query string itself).
		          
		          	int theQuestion = queryString.indexOf ("?");
		          	if (theURL == null)
		          	{
		          		theURL = queryString.substring (0, (theQuestion == -1 ? queryString.length () : theQuestion));
		          	}
		          	if (theQuestion != -1)
		          	{
		            	String queries = queryString.substring (theQuestion + 1, queryString.length());
			          	readData += ("x-queries: " + queries);
			        }
		        }
		        
		        long theTime = System.currentTimeMillis ();
	      
	      	    request = new HTTPRequest (infoClientHostname, theURL, readData, theRawHeaders);
      	  
	      	    // ** This is where the http server calls the interim RR translator;
	      	    //    the translator will, in the future, be a class within this 
	      	    //    package that extends a more generic (ie. abstract) rr class in
	      	    //    the engine itself.  That's why it's 'interim'.
	      	    
		      	HTTPResponse response = HTTPEventTranslator.getInstance().translateRequest(request);

		        // Let the response know what session it it associated with.
		      
		        response.setSession (request.getSession ());
		      	  
		      	//this.responseStream = socket.getOutputStream ();//new PrintStream(this.socket.getOutputStream());
		      	//lastKeepAlive = false;
		      	lastKeepAlive = response.sendResponse(this.responseStream, request.getHeaderValue ("host"), localPort, lastKeepAlive, secure);
		      	
		      	//System.err.println ("Thread = " + Thread.currentThread ().getName () + " URL = " + queryString + " Bytes written " + response.getContentLength () +
		      	//					" Time = " + (System.currentTimeMillis () - theTime));
		      	  
		      	reading = false;
		      	  
		      	// Record some information to output.
		      	  
		      	this.infoQuery = request.getHeaderValue("x-queries");
		      	this.infoSession = request.getSession().getID();
	      	  }
	        }
      	    notifyListeners (REQUESTCOMPLETED);
			theKeepAlive = lastKeepAlive;
	      }
		}
		catch (SessionMaximumException e)
		{
			HTTPResponse theResponse = request.createResponseObject ();
			theResponse.setErrorCode (responseStream, HTTPResponse.STATUS_SERVICEUNAVAILABLE);
		}
		catch (Throwable exception)
		{
			if (! (exception instanceof IOException))
			{
				/*if (errorStream == null)
				{
					try
					{
						errorStream = new PrintStream (new FileOutputStream ("error.log"));
						System.setErr (errorStream);
					}
					catch (Exception e)
					{
					}
				}*/
			    System.out.println(new WarningString("[HTTPConnection] " + infoClientHostname + " - Request Stream Abnormally Ended."));
				exception.printStackTrace ();
			    if (exception instanceof Exception)
			    {
			    	MauiRuntimeEngine.printException ((Exception) exception);
			    }
			    
			    if (exception instanceof OutOfMemoryError)
			    {
			    	//
			    	//	Ugly kludge... This is to allow Maui to restart when it runs out of memory...
			    	//	which is always happening.
			    	//
			    	System.exit (5);
			    }
			}
		}
	  
	  Profiler.finish (theReference, method + queryString);
	  this.endConnection();
	}

	
	// ----------------------------------------------------------------------
	// METHOD: endConnection
	// ----------------------------------------------------------------------
	
	/** Shutdown a socket connection.
	  * 
	  */
	  
	private synchronized void endConnection()
	{
	  if (!isConnectionClosed)
	  {
	  	isConnectionClosed = true;
		try
		{
			if (requestStream != null)
			{
				this.requestStream.close();
			}
			if (this.responseStream != null)
			{
			  this.responseStream.close();
			}
			if (socket != null)
			{
				this.socket.close();
		        this.printInformation();
			}
			//HTTPServer.getInstance().registerConnection(this);
	 
		  }
		  catch (IOException exception)
		  {
			  System.err.println("[HTTPConnection] - WARNING: Connection not ended with '" + this.infoClientHostname + "'.");
	      	  this.printInformation();
		      MauiRuntimeEngine.printException(exception);
		  }
		  finally
		  {
		    //--connectionCount;
		    notifyListeners (REQUESTCOMPLETED);
			notifyListeners (CONNECTIONCLOSED);
			//if (connectionQueue.getQueueSize () <= minimum ||
			//	!isExpired (ageLimit))
			//{
				connectionQueue.add (this);
			//}
			//else
			//{
			//	--connectionCountl
			//}
		  }
		}
	}
	
	public void finalize ()
	{
		endConnection ();
	}
	
	public static int getConnectionCount ()
	{
		return connectionCount;
	}
	
	public static int getFreeConnectionCount ()
	{
		return connectionQueue.size ();
	}


	// ----------------------------------------------------------------------
	// PRIVATE METHOD: printInformation
	// ----------------------------------------------------------------------
  
  /** Print some information about the client connection.  This is for 
    * debugging.
    *
    */
    
  private void printInformation()
  {
	  System.out.println(new DebugString("[HTTPConnection] - Request-response complete (client: " + this.infoClientHostname + ", session: " + this.infoSession + ", time elapsed: " + (System.currentTimeMillis() - this.infoTimer) + ", query: '" + this.infoQuery + "')."));
  }
  
  	protected void notifyListeners (int aAction)
  	{
  		notifyListeners (aAction, System.currentTimeMillis ());
  	}
  		
  	protected void notifyListeners (int aAction, long aEventTime)
  	{
  		if (listenersArray.length > 0)
  		{
	  		ConnectionEvent theEvent = new ConnectionEvent (this, aEventTime);
	  		synchronized (connectionListeners)
	  		{
	  			for (int i = 0; i < listenersArray.length; i++)
	  			{
	  				switch (aAction)
	  				{
	  					case (NEWCONNECTION) :
	  						listenersArray [i].newConnection (theEvent);
	  						break;
	  						
	  					case (CONNECTIONCLOSED) :
	  						listenersArray [i].connectionClosed (theEvent);
	  						break;
	  						
	  					case (REQUESTSTARTED) :
	  						listenersArray [i].requestStarted (theEvent);
	  						break;
	  						
	  					case (REQUESTCOMPLETED) :
	  						listenersArray [i].requestCompleted (theEvent);
	  						break;
	  				}
	  			}
	  		}
	  	}
  	}
  	
	private static void rebuildListenerArray ()
	{
		listenersArray = new I_ConnectionListener [connectionListeners.size ()];
		Object [] theListenersArray = connectionListeners.toArray ();
		for (int i = 0; i < theListenersArray.length; i++)
		{
			listenersArray [i] = (I_ConnectionListener) theListenersArray [i];
		}
	}
	
  	public static void addConnectionListener (I_ConnectionListener aConnectionListener)
  	{
  		synchronized (connectionListeners)
  		{
			if (!connectionListeners.contains (aConnectionListener))
			{
				connectionListeners.addElement (aConnectionListener);
				rebuildListenerArray ();
			}
		}
	}
	
	public synchronized static void removeConnectionListener (I_ConnectionListener aConnectionListener)
	{
		synchronized (connectionListeners)
		{
			if (connectionListeners.contains (aConnectionListener))
			{
				connectionListeners.removeElement (aConnectionListener);
				rebuildListenerArray ();
			}
		}
	}
	
}

class MyBufferedOutputStream extends FilterOutputStream
{
    /**
     * The internal buffer where data is stored. 
     */
    protected byte buf[];

    /**
     * The number of valid bytes in the buffer. This value is always 
     * in the range <tt>0</tt> through <tt>buf.length</tt>; elements 
     * <tt>buf[0]</tt> through <tt>buf[count-1]</tt> contain valid 
     * byte data.
     */
    protected int count;
    

    public MyBufferedOutputStream(OutputStream out, byte [] aBuffer) {
	super(out);
    buf = aBuffer;
    }

    /** Flush the internal buffer */
    private void flushBuffer() throws IOException {
        if (count > 0) {
	    out.write(buf, 0, count);
	    count = 0;
        }
    }

    /**
     * Writes the specified byte to this buffered output stream. 
     *
     * @param      b   the byte to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void write(int b) throws IOException {
	if (count >= buf.length) {
	    flushBuffer();
	}
	buf[count++] = (byte)b;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to this buffered output stream.
     *
     * <p> Ordinarily this method stores bytes from the given array into this
     * stream's buffer, flushing the buffer to the underlying output stream as
     * needed.  If the requested length is at least as large as this stream's
     * buffer, however, then this method will flush the buffer and write the
     * bytes directly to the underlying output stream.  Thus redundant
     * <code>BufferedOutputStream</code>s will not copy data unnecessarily.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void write(byte b[], int off, int len) throws IOException {
	if (len >= buf.length) {
	    /* If the request length exceeds the size of the output buffer,
    	       flush the output buffer and then write the data directly.
    	       In this way buffered streams will cascade harmlessly. */
	    flushBuffer();
	    out.write(b, off, len);
	    return;
	}
	if (len > buf.length - count) {
	    flushBuffer();
	}
	System.arraycopy(b, off, buf, count, len);
	count += len;
    }

    /**
     * Flushes this buffered output stream. This forces any buffered 
     * output bytes to be written out to the underlying output stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     */
    public synchronized void flush() throws IOException {
        flushBuffer();
	out.flush();
    }
}
class MyBufferedInputStream extends FilterInputStream {

    private static int defaultBufferSize = 2048;

    /**
     * The internal buffer array where the data is stored. When necessary,
     * it may be replaced by another array of
     * a different size.
     */
    protected byte buf[];

    /**
     * The index one greater than the index of the last valid byte in 
     * the buffer. 
     * This value is always
     * in the range <code>0</code> through <code>buf.length</code>;
     * elements <code>buf[0]</code>  through <code>buf[count-1]
     * </code>contain buffered input data obtained
     * from the underlying  input stream.
     */
    protected int count;

    /**
     * The current position in the buffer. This is the index of the next 
     * character to be read from the <code>buf</code> array. 
     * <p>
     * This value is always in the range <code>0</code>
     * through <code>count</code>. If it is less
     * than <code>count</code>, then  <code>buf[pos]</code>
     * is the next byte to be supplied as input;
     * if it is equal to <code>count</code>, then
     * the  next <code>read</code> or <code>skip</code>
     * operation will require more bytes to be
     * read from the contained  input stream.
     *
     * @see     java.io.BufferedInputStream#buf
     */
    protected int pos;
    
    /**
     * The value of the <code>pos</code> field at the time the last 
     * <code>mark</code> method was called.
     * <p>
     * This value is always
     * in the range <code>-1</code> through <code>pos</code>.
     * If there is no marked position in  the input
     * stream, this field is <code>-1</code>. If
     * there is a marked position in the input
     * stream,  then <code>buf[markpos]</code>
     * is the first byte to be supplied as input
     * after a <code>reset</code> operation. If
     * <code>markpos</code> is not <code>-1</code>,
     * then all bytes from positions <code>buf[markpos]</code>
     * through  <code>buf[pos-1]</code> must remain
     * in the buffer array (though they may be
     * moved to  another place in the buffer array,
     * with suitable adjustments to the values
     * of <code>count</code>,  <code>pos</code>,
     * and <code>markpos</code>); they may not
     * be discarded unless and until the difference
     * between <code>pos</code> and <code>markpos</code>
     * exceeds <code>marklimit</code>.
     *
     * @see     java.io.BufferedInputStream#mark(int)
     * @see     java.io.BufferedInputStream#pos
     */
    protected int markpos = -1;

    /**
     * The maximum read ahead allowed after a call to the 
     * <code>mark</code> method before subsequent calls to the 
     * <code>reset</code> method fail. 
     * Whenever the difference between <code>pos</code>
     * and <code>markpos</code> exceeds <code>marklimit</code>,
     * then the  mark may be dropped by setting
     * <code>markpos</code> to <code>-1</code>.
     *
     * @see     java.io.BufferedInputStream#mark(int)
     * @see     java.io.BufferedInputStream#reset()
     */
    protected int marklimit;

    /**
     * Check to make sure that this stream has not been closed
     */
    private void ensureOpen() throws IOException {
	if (in == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Creates a <code>BufferedInputStream</code>
     * and saves its  argument, the input stream
     * <code>in</code>, for later use. An internal
     * buffer array is created and  stored in <code>buf</code>.
     *
     * @param   in   the underlying input stream.
     */
    public MyBufferedInputStream(InputStream in) {
	this(in, new byte [defaultBufferSize]);
    }

    /**
     * Creates a <code>BufferedInputStream</code>
     * with the specified buffer size,
     * and saves its  argument, the input stream
     * <code>in</code>, for later use.  An internal
     * buffer array of length  <code>size</code>
     * is created and stored in <code>buf</code>.
     *
     * @param   in     the underlying input stream.
     * @param   buffer the buffer to use
     * @exception IllegalArgumentException if size <= 0.
     */
    public MyBufferedInputStream(InputStream in, byte [] aBuffer) {
	super(in);
	buf = aBuffer;
    }

    /**
     * Fills the buffer with more data, taking into account
     * shuffling and other tricks for dealing with marks.
     * Assumes that it is being called by a synchronized method.
     * This method also assumes that all data has already been read in,
     * hence pos > count.
     */
    private void fill() throws IOException {
	if (markpos < 0)
	    pos = 0;		/* no mark: throw away the buffer */
	else if (pos >= buf.length)	/* no room left in buffer */
	    if (markpos > 0) {	/* can throw away early part of the buffer */
		int sz = pos - markpos;
		System.arraycopy(buf, markpos, buf, 0, sz);
		pos = sz;
		markpos = 0;
	    } else if (buf.length >= marklimit) {
		markpos = -1;	/* buffer got too big, invalidate mark */
		pos = 0;	/* drop buffer contents */
	    } else {		/* grow buffer */
		int nsz = pos * 2;
		if (nsz > marklimit)
		    nsz = marklimit;
		byte nbuf[] = new byte[nsz];
		System.arraycopy(buf, 0, nbuf, 0, pos);
		buf = nbuf;
	    }
        count = pos;
	int n = in.read(buf, pos, buf.length - pos);
        if (n > 0)
            count = n + pos;
    }

    /**
     * See
     * the general contract of the <code>read</code>
     * method of <code>InputStream</code>.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public synchronized int read() throws IOException {
        ensureOpen();
	if (pos >= count) {
	    fill();
	    if (pos >= count)
		return -1;
	}
	return buf[pos++] & 0xff;
    }

    /**
     * Read characters into a portion of an array, reading from the underlying
     * stream at most once if necessary.
     */
    private int read1(byte[] b, int off, int len) throws IOException {
	int avail = count - pos;
	if (avail <= 0) {
	    /* If the requested length is at least as large as the buffer, and
	       if there is no mark/reset activity, do not bother to copy the
	       bytes into the local buffer.  In this way buffered streams will
	       cascade harmlessly. */
	    if (len >= buf.length && markpos < 0) {
		return in.read(b, off, len);
	    }
	    fill();
	    avail = count - pos;
	    if (avail <= 0) return -1;
	}
	int cnt = (avail < len) ? avail : len;
	System.arraycopy(buf, pos, b, off, cnt);
	pos += cnt;
	return cnt;
    }

    /**
     * Reads bytes from this byte-input stream into the specified byte array,
     * starting at the given offset.
     *
     * <p> This method implements the general contract of the corresponding
     * <code>{@link InputStream#read(byte[], int, int) read}</code> method of
     * the <code>{@link InputStream}</code> class.  As an additional
     * convenience, it attempts to read as many bytes as possible by repeatedly
     * invoking the <code>read</code> method of the underlying stream.  This
     * iterated <code>read</code> continues until one of the following
     * conditions becomes true: <ul>
     *
     *   <li> The specified number of bytes have been read,
     *
     *   <li> The <code>read</code> method of the underlying stream returns
     *   <code>-1</code>, indicating end-of-file, or
     *
     *   <li> The <code>available</code> method of the underlying stream
     *   returns zero, indicating that further input requests would block.
     *
     * </ul> If the first <code>read</code> on the underlying stream returns
     * <code>-1</code> to indicate end-of-file then this method returns
     * <code>-1</code>.  Otherwise this method returns the number of bytes
     * actually read.
     *
     * <p> Subclasses of this class are encouraged, but not required, to
     * attempt to read as many bytes as possible in the same fashion.
     *
     * @param      b     destination buffer.
     * @param      off   offset at which to start storing bytes.
     * @param      len   maximum number of bytes to read.
     * @return     the number of bytes read, or <code>-1</code> if the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized int read(byte b[], int off, int len)
	throws IOException
    {
        ensureOpen();
	if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) > b.length) || ((off + len) < 0)) {
	    throw new IndexOutOfBoundsException();
	} else if (len == 0) {
	    return 0;
	}

	int n = read1(b, off, len);
	if (n <= 0) return n;
	while ((n < len) && (in.available() > 0)) {
	    int n1 = read1(b, off + n, len - n);
	    if (n1 <= 0) break;
	    n += n1;
	}
	return n;
    }

    /**
     * See the general contract of the <code>skip</code>
     * method of <code>InputStream</code>.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized long skip(long n) throws IOException {
        ensureOpen();
	if (n <= 0) {
	    return 0;
	}
	long avail = count - pos;
     
        if (avail <= 0) {
            // If no mark position set then don't keep in buffer
            if (markpos <0) 
                return in.skip(n);
            
            // Fill in buffer to save bytes for reset
            fill();
            avail = count - pos;
            if (avail <= 0)
                return 0;
        }
        
        long skipped = (avail < n) ? avail : n;
        pos += skipped;
        return skipped;
    }

    /**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. 
     * <p>
     * The <code>available</code> method of 
     * <code>BufferedInputStream</code> returns the sum of the the number 
     * of bytes remaining to be read in the buffer 
     * (<code>count&nbsp;- pos</code>) 
     * and the result of calling the <code>available</code> method of the 
     * underlying input stream. 
     *
     * @return     the number of bytes that can be read from this input
     *             stream without blocking.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public synchronized int available() throws IOException {
        ensureOpen();
	return (count - pos) + in.available();
    }

    /** 
     * See the general contract of the <code>mark</code>
     * method of <code>InputStream</code>.
     *
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.BufferedInputStream#reset()
     */
    public synchronized void mark(int readlimit) {
	marklimit = readlimit;
	markpos = pos;
    }

    /**
     * See the general contract of the <code>reset</code>
     * method of <code>InputStream</code>.
     * <p>
     * If <code>markpos</code> is <code>-1</code>
     * (no mark has been set or the mark has been
     * invalidated), an <code>IOException</code>
     * is thrown. Otherwise, <code>pos</code> is
     * set equal to <code>markpos</code>.
     *
     * @exception  IOException  if this stream has not been marked or
     *               if the mark has been invalidated.
     * @see        java.io.BufferedInputStream#mark(int)
     */
    public synchronized void reset() throws IOException {
        ensureOpen();
	if (markpos < 0)
	    throw new IOException("Resetting to invalid mark");
	pos = markpos;
    }

    /**
     * Tests if this input stream supports the <code>mark</code> 
     * and <code>reset</code> methods. The <code>markSupported</code> 
     * method of <code>BufferedInputStream</code> returns 
     * <code>true</code>. 
     *
     * @return  a <code>boolean</code> indicating if this stream type supports
     *          the <code>mark</code> and <code>reset</code> methods.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     */
    public boolean markSupported() {
	return true;
    }

    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void close() throws IOException {
        if (in == null)
            return;
        in.close();
        in = null;
        buf = null;
    }
}



// ========================================================================
// Copyright © 2001 Bitmovers Software Inc.                             eof