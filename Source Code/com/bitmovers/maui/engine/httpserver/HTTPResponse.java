// =============================================================================
// com.bitmovers.maui.httpserver.HTTPResponse
// =============================================================================

package com.bitmovers.maui.engine.httpserver;

import java.io.*;
import java.net.*;
import java.util.Hashtable;

import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;


// =============================================================================
// CLASS: HTTPResponse 
// =============================================================================

/**
* HTTPResponse <p>
* This class represents a response to an HTTP request.  There is one HTTPResponse object
* per HTTP response message
* @invisible
*/
public class HTTPResponse
{
	// ---------------------------------------------------------------------------

  private boolean responseSent;
  private String contentType;
  private byte[] content;
  private HTTPSession session;
  private String redirection = null;
  private String pseudoEvents = null;
  private static boolean firstResponse = true;
  private boolean limitExceeded = false;
  private static byte [] mauiVersion = null;
  private int length = 0;
  
  private static final byte [] CRLF = "\r\n".getBytes ();
  public static final byte [] HTTP_503 = "HTTP/1.1 503 Service Unavailable\r\n".getBytes ();
  public static final byte [] HTTP_302 = "HTTP/1.1 302 Found\r\n".getBytes ();
  public static final byte [] HTTP_200 = "HTTP/1.1 200 OK\r\n".getBytes();
  private static final byte [] CACHE_CONTROL_1 = "Cache-Control: public\r\n".getBytes ();
  private static final byte [] CACHE_CONTROL_2 = "Cache-Control: no-store,no-transform,no-cache,max-age=0\r\n".getBytes ();
  private static final byte [] CONTENT_TYPE = "Content-Type: ".getBytes ();
  private static final byte [] CONTENT_LENGTH = "Content-Length: ".getBytes ();
  private static final byte [] CONTENT_LENGTH_0 = "Content-Length: 0\r\n".getBytes ();
  private static final byte [] CONNECTION_KEEP_ALIVE = "Connection: Keep-Alive\r\n".getBytes ();
  private static final byte [] CONNECTION_CLOSE = "Connection: Close\r\n".getBytes ();
  private static final byte [] LOCATION = "Location: ".getBytes ();
  private static final byte [] CONTENTTYPE_TEXT = "Content-Type: text/plain\r\n".getBytes ();
  
  public static final int STATUS_SERVICEUNAVAILABLE = 503;
  
  private static Hashtable errorText = new Hashtable (10);
  
  protected int errorCode = -1;

	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: HTTPResponse
	// ---------------------------------------------------------------------------
	
	/**
	* Simple constructor
	*/
	public HTTPResponse()
	{
	  this.responseSent = false;
	  this.contentType = "text/plain";
	  this.content = new byte[0];
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: setContentType
	// ---------------------------------------------------------------------------
	
	/**
	* Set the content type of the response object
	*
	* @param contentType The content type string
	*/
	public void setContentType(String contentType)
	{
	  if (!this.responseSent)
	  {
	    if (contentType != null)
	    {
	      this.contentType = contentType;
	    }
	  }
	}


	// ---------------------------------------------------------------------------
	// METHOD: setContent
	// ---------------------------------------------------------------------------
	/**
	* Set the actual content of the response
	*
	* @param content A byte array of the message response content
	*/
	public void setContent(byte[] content)
	{
	  if (!this.responseSent)
	  {
		  if (content != null)
		  {
	  	  	this.content = content;
	  	  }
	  }
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: getContent
	// ---------------------------------------------------------------------------
	/**
	* Get the actual content of the response
	*
	* @return The actual content
	*/
	public byte [] getContent ()
	{
		return content;
	}
	
	public int getContentLength ()
	{
		return content.length;
	}

	// ---------------------------------------------------------------------------
	// METHOD: getContentType
	// ---------------------------------------------------------------------------
	/**
	* Get the content type of the response
	*
	* @return The actual content
	*/
	public String getContentType ()
	{
		return contentType;
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: setSession
	// ---------------------------------------------------------------------------
	
	/**
	* Set the session object for associated with this response
	*
	* @param session The HTTPSession object
	*/
	protected void setSession(HTTPSession session)
	{
	  this.session = session;
	}

	// ---------------------------------------------------------------------------
	// METHOD: setLimitedExceeded
	// ---------------------------------------------------------------------------
	
	/** The authorized connection limit has been exceeded.  So, send back a nasty
	  * message.
	  *
	  * @invisible
	  */
	protected void setLimitExceeded (boolean aLimitExceeded)
	{
		if (aLimitExceeded)
		{
			System.out.println ("Wait");
		}
		limitExceeded = aLimitExceeded;
	}
	
	public boolean isLimitExceeded ()
	{
		return limitExceeded;
	}
	
	/**
	* Set the redirection URL.  This is used for switching from one Maui application
	* to another, or for an external linke
	*
	* @param aRedirection The redirection address
	*/
	public void setRedirection (String aRedirection)
	{
		redirection = aRedirection;
	}
	
	/**
	* Set the pseudo events
	*
	* @param aPseudoEvents The pseudo events string
	*/
	public void setPseudoEvents (String aPseudoEvents)
	{
		pseudoEvents = aPseudoEvents;
	}

	/**
	* Get the redirection URL
	*
	* @return The redirection URL
	*/
	public String getRedirection ()
	{
		return redirection;
	}
	
	private byte [] getBytesLocal (String aString)
	{
		byte [] retVal = new byte [aString.length ()];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = (byte) aString.charAt (i);
		}
		return retVal;
	}
	
	private byte [] getErrorText (int aErrorCode)
	{
		String theErrorCode = Integer.toString (aErrorCode);
		byte [] retVal = (byte []) errorText.get (theErrorCode);
		if (retVal == null)
		{
			//
			//	Look for an error description file in the Errors folder
			//
			InputStream theInput = null;
			try
			{
				File theFile = new File ("Errors" + File.separatorChar + theErrorCode);
				theInput = new FileInputStream (theFile);
				retVal = new byte [(int) theFile.length ()];
				int theTotalBytesRead = 0;
				int theBytesRead = 0;
				while (theTotalBytesRead < retVal.length &&
					   (theBytesRead = theInput.read (retVal, theTotalBytesRead, retVal.length - theTotalBytesRead)) != -1)
				{
					theTotalBytesRead += theBytesRead;
				}
				
				errorText.put (theErrorCode, retVal);
				theInput.close ();
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (theInput != null)
				{
					try
					{
						theInput.close ();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
		
		if (retVal == null)
		{
			switch (aErrorCode)
			{
				case (STATUS_SERVICEUNAVAILABLE) :
					retVal = HTTP_503;
					break;
			}
			errorText.put (theErrorCode, retVal);
		}
		return retVal;
	}
				
	
	protected void setErrorCode (OutputStream aOutputStream, int aErrorCode)
	{
		errorCode = aErrorCode;
		try
		{
			byte [] theErrorMessage = getErrorText (aErrorCode);
			
			byte [] theErrorStatus = null;
			
			switch (aErrorCode)
			{
				case (STATUS_SERVICEUNAVAILABLE) :
					theErrorStatus = HTTP_503;
					break;
			}
			/*aOutputStream.write (mauiVersion);
			aOutputStream.write (CONTENTTYPE_TEXT);

			aOutputStream.write (CONTENT_LENGTH);
			aOutputStream.write (getBytesLocal (Integer.toString (theErrorMessage.length)));
			aOutputStream.write (CRLF);
	 		aOutputStream.write (CONNECTION_CLOSE);*/
	 		aOutputStream.write (theErrorStatus);
	 		aOutputStream.write (CRLF);
	 		aOutputStream.write (theErrorMessage);
			aOutputStream.flush();
		}
		catch (IOException e)
		{
			System.err.println ("[HTTPResponse - setErrorCode] " + e);
		}
	}
	
	public int getErrorCode ()
	{
		return errorCode;
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: sendResponse
	// ---------------------------------------------------------------------------

	/**
	* Send the response stream to the client
	*
	* @param responseStream The OutputStream to use for sending the response
	* @param aHost The name of the host
	* @param aLocalPort The local port in this connection
	* @param aKeepAlive Boolean if this connection should be left open, or closed
	* @param aSecure Secure or unsecure socket connection
	*
	* @return Boolean indicating if the connection should be closed or not
	*/
	public boolean sendResponse(OutputStream responseStream, final String aHost, int aLocalPort, boolean aKeepAlive, boolean aSecure)
	{
	  boolean retVal = (aKeepAlive && session.getKeepAlive ());
	  
	  if (mauiVersion == null)
	  {
	  	StringBuffer theVersion = new StringBuffer ("Server: Maui HTTPConnection Maui Runtime Engine ");
	  	theVersion.append (MauiRuntimeEngine.getVersion ());
	  	theVersion.append ("\r\n");
	  	mauiVersion = new byte [theVersion.length ()];
	  	for (int i = 0; i < mauiVersion.length; i++)
	  	{
	  		mauiVersion [i] = (byte) theVersion.charAt (i);
	  	}
	  }
	  if (firstResponse)
	  {
	  	synchronized (this)
	  	{
	  	  firstResponse = false;
	  	  if (MauiRuntimeEngine.windowingEnvironmentAvailable)
	  	  {
		  	  new Thread (new Runnable ()
		  	  	{
			  	  	public void run ()
			  	  	{
			  		  firstResponse = false;
				  	  MauiRuntimeWindow theWindow = MauiRuntimeWindow.getInstance ();
				  	  StringBuffer theHost = new StringBuffer (aHost);
				  	  int theIndex = aHost.indexOf (":");
				  	  if (theIndex != -1)
				  	  {
				  		  theHost.setLength (theIndex);
				  	  }
				  	  theWindow.setHostName (theHost.toString ());
				  	}
				  }, "NodeNameSet").start ();
			}
	    }
	  }
		
	  	
	  try
	  {
	  	if (isLimitExceeded ())
	  	{
	  		//responseStream.write ("HTTP/1.1 503 Service Unavailable\r\n".getBytes ());
	  		responseStream.write (HTTP_503);
	  	}
	  	else if (redirection == null)
	  	{
			//responseStream.write("HTTP/1.1 200 OK\r\n".getBytes());
			responseStream.write (HTTP_200);
			// Send the response headers...

			//responseStream.write(("Server: Maui HTTPConnection (Maui Runtime Engine " + MauiRuntimeEngine.getVersion() + ")\r\n").getBytes());
			responseStream.write (mauiVersion);
			
			// Client-side caching should be encouraged for images and CSS files.
			if (!this.contentType.equals("image/gif") && !this.contentType.equals("text/css"))
			{
				//responseStream.write (("Cache-Control: public\r\n").getBytes ());
				responseStream.write (CACHE_CONTROL_1);
			}
			// Client-side caching should be forbidden for all others.
			else
			{
				//responseStream.write (("Cache-Control: no-store,no-transform,no-cache,max-age=0\r\n").getBytes ());
				responseStream.write (CACHE_CONTROL_2);
			}
			
			responseStream.write (CONTENT_TYPE);
			responseStream.write (getBytesLocal (contentType));
			responseStream.write (CRLF);
			//responseStream.write(("Content-Type: " + this.contentType + "\r\n").getBytes());
			responseStream.write (CONTENT_LENGTH);
			responseStream.write (getBytesLocal (Integer.toString (content.length)));
			responseStream.write (CRLF);
			//responseStream.write(("Content-Length: " + this.content.length + "\r\n").getBytes());
			//responseStream.write (("Connection: ").getBytes ());
			//responseStream.write ((aKeepAlive ? "Keep-Alive\r\n" : "Close\r\n").getBytes ());
			//responseStream.write (aKeepAlive ? CONNECTION_KEEP_ALIVE : CONNECTION_CLOSE);
 		    responseStream.write (retVal ? CONNECTION_KEEP_ALIVE : CONNECTION_CLOSE);
			if (session != null)
			{
				session.writeCookieHeader (responseStream, aHost);
			}
			//responseStream.write("\r\n".getBytes());
			responseStream.write (CRLF);

	      // Send the response...

		  /*int theTotalWritten = 0;
		  int theBufferSize = 0;
		  
		  while (theTotalWritten < content.length)
		  {
		  	theBufferSize = content.length - theTotalWritten;
		  	if (theBufferSize > 1000)
		  	{
		  		theBufferSize = 1000;
		  	}
		  	responseStream.write (content, theTotalWritten, theBufferSize);
		  	responseStream.flush ();
		  	theTotalWritten += theBufferSize;
		  }*/
		  responseStream.write(this.content);
		  //responseStream.write ("\r\n".getBytes ());
		  responseStream.write (CRLF);
		  responseStream.write (CRLF);
		  responseStream.flush ();
		  
		  
		}
		else
		{
		  retVal = retVal && !redirection.startsWith ("mailto:");
 		  //
 		  //	This is redirection to an external URL or application chaining... So redirect to a new location
 		  //
          //responseStream.write("HTTP/1.1 302 Found\r\n".getBytes());
          responseStream.write (HTTP_302);
		  responseStream.write (mauiVersion);
          //responseStream.write(("Server: Maui HTTPConnection (Maui Runtime Engine " + MauiRuntimeEngine.getVersion() + ")\r\n").getBytes());
 		  //responseStream.write (("Content-Length: 0\r\n").getBytes ());
 		  responseStream.write (CONTENT_LENGTH_0);
 		  responseStream.write (retVal ? CONNECTION_KEEP_ALIVE : CONNECTION_CLOSE);
		  //responseStream.write (("Connection: ").getBytes ());
		  //responseStream.write ((aKeepAlive ? "Keep-Alive\r\n" : "Close\r\n").getBytes ());
 		  if (session != null && session.isExiting ())
 		  {
 		  	session.writeCookieHeader (responseStream, aHost);
 		  }
 		  //responseStream.write ("Location: ".getBytes ());
 		  responseStream.write (LOCATION);
 		  StringBuffer theRedirection = new StringBuffer ();
 		  if (!redirection.startsWith ("mailto:") &&
 		  	  redirection.indexOf ("://") == -1)
 		  {
 		  	  theRedirection.append ((aSecure ? "https://" : "http://"));
 		  	  theRedirection.append (aHost);
	 		  //responseStream.write ((aSecure ? "https://" : "http://").getBytes ());
	 		  //responseStream.write (aHost.getBytes ());
	 		  if (aHost.indexOf (":") == -1 && aLocalPort != (aSecure ? 443 : 80))
	 		  {
	 		  	theRedirection.append (":");
	 		  	theRedirection.append (Integer.toString (aLocalPort));
	 		  	//responseStream.write (":".getBytes ());
	 		  	//responseStream.write (Integer.toString (aLocalPort).getBytes ());
	 		  }
	 		  theRedirection.append ("/");
	 		  //responseStream.write ("/".getBytes ());
	 	  }
	 	  theRedirection.append (redirection);
	 	  System.out.println ("Redirection to : " + theRedirection);
 		  responseStream.write (getBytesLocal (theRedirection.toString ()));//.getBytes ());
  		  if (pseudoEvents != null)
 		  {
 		  	responseStream.write (getBytesLocal (pseudoEvents));//.getBytes ());
 		  }
		  //responseStream.write ("\r\n".getBytes ());
		  //responseStream.write ("\r\n".getBytes ());
		  responseStream.write (CRLF);
		  responseStream.write (CRLF);
		}
		responseStream.flush ();
	  }
	  catch (IOException exception)
	  {
	    String theException = exception.toString ();
	    if (theException.indexOf ("Connection reset") == -1 &&
	    	theException.indexOf ("Broken pipe") == -1)
	    {
	        System.err.println("[HTTPResponse] - Problem while writing to responseStream.");
	        MauiRuntimeEngine.printException(exception);
	    }
	    else
	    {
	  		retVal = true;
	  	}
	  }
	  return retVal;
	}
	
	
	// ---------------------------------------------------------------------------
}


// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof