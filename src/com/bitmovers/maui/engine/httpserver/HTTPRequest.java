// =============================================================================
// com.bitmovers.maui.httpserver.HTTPRequest
// =============================================================================

// =============================================================================
// CHANGELOG:
//++ 181 MW 2001.08.09
// Modified the constructor to match application requests with their
// case-insentive counterparts. If the two equal each other (ignoring the case)
// then we will convert the case-insentive request to a case-sensitive one,
// which can be then used normally. The case sensitivity can be toggled on/off.
// =============================================================================


package com.bitmovers.maui.engine.httpserver;

import java.io.*;
import java.net.*;
import java.util.*;
import sun.io.CharToByteConverter;

import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;


// =============================================================================
// CLASS: HTTPRequest 
// =============================================================================

/** 
* HTTPRequest <p>
* @invisible
* This class describes a single HTTP Request.  It is responsible for parsing all of the HTTP header key/value
* pairs.  It also parses POST data and constructs the event queue.
*/

public class HTTPRequest
{
	// ---------------------------------------------------------------------------

  private String rawHeaders;	
  private Hashtable headerPairs; 
  private Hashtable queryPairs;
  private Hashtable cookies;
  private Vector pseudoEvents;  
  private HTTPSession session;
  private String url;
  protected String applicationName = null;
  private int uniqueReference = -1;
  private boolean emptyIsSignificant;
  private boolean retrievalDone = false;
  private String clientName;
  private boolean cookiesParsed = false;
  protected boolean servletBased = false;
  protected String servletURL = null;
	
	
	private final int getNumVal (final char aChar)
	{
		return (aChar >= '0' && aChar <= '9' ?
					aChar - 0x30 :
					(aChar >= 'A' && aChar <= 'F' ?
						aChar - 0x37 :
						aChar - 0x57));
	}
	
	/**
	* "Zap" the URL.  Sometimes WAP devices replace characters with their hex equivalents (eg. "/" becomes "%2f")
	* This method replaces all of these substitutions with the correct URL info
	*
	* @param aURL The original URL
	*
	* @return The zapped URL
	*/
	private final String zap (final String aURL)
	{
		char [] theURL = aURL.toCharArray ();
		StringBuffer retVal = new StringBuffer ();
		for (int i = 0; i < theURL.length; i++)
		{
			char theChar = theURL [i];
			if (theChar == '%')
			{
				int theNum = getNumVal (theURL [++i]);
				int theNum2 = getNumVal (theURL [++i]);
				theNum = (theNum << 4) + theNum2;
				theChar = (char) (theNum);
			}
			retVal.append (theChar);
		}
		return retVal.toString ();
	}
			
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: HTTPRequest
	// ------------------- --------------------------------------------------------
	
	/** Empty constructor for subclasses that don't all of the functionality
	*/
	public HTTPRequest ()
	{
	}
	
	public HTTPRequest (boolean aServletBased)
	{
		servletBased = aServletBased;
	}
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: HTTPRequest
	// ------------------- --------------------------------------------------------
	
	/**
	* Standard HTTPRequest constructor.  This performs some preprocessing on the arguments passed in.
	*
	* @param aClientName The name of the connecting client
	* @param aURL The URL portion of the HTTP request
	* @param rawHeaders The unparsed HTTP header key/value pairs
	* @param aHeaderPairs This hashtable contains some pre-parsed header key/value pairs.  It is used as a base for
	* 					  the header values which are parsed in HTTPRequest
	*/
	public HTTPRequest (String aClientName, String aURL, String rawHeaders, Hashtable aHeaderPairs)
	{
		clientName = aClientName;
		url = zap (aURL);
		if (url != null)
		{
			try
			{
				URL theURL = new URL (url);
				applicationName = theURL.getFile ();
			}
			catch (MalformedURLException e)
			{
				applicationName = url;
				if (!applicationName.startsWith ("/pseudo") &&
					applicationName.startsWith ("/"))
				{
					applicationName = applicationName.substring (1);
				}
			}
		}
		
		this.rawHeaders = rawHeaders;
		this.headerPairs = aHeaderPairs;
		this.queryPairs = new Hashtable();
		int theQuestion = applicationName.indexOf ("?");
		if (theQuestion != -1)
		{
			parseRawQueries (applicationName.substring (theQuestion + 1));
			applicationName = applicationName.substring (0, theQuestion);
		}

		//++ 181 MW 2001.08.09
		/*if (!Boolean.valueOf(ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_CASE_SENSITIVE)).booleanValue())
		{
			// get a list of names of the applications, and match them with
			// their case-insensitive counterparts.
			ApplicationManager appManager = ApplicationManager.getInstance();

			Vector appNames = appManager.getAllApplicationNames();

			for (int i = 0; i < appNames.size(); i++)
			{
				String appName = (String)appNames.elementAt(i);
				
				if (applicationName.equalsIgnoreCase(appName))
				{
					// switch the invalid case application Name to the valid case.
					applicationName = appName;
				}
			}
		}*/
		//-- 181
		
		parsePseudoEvents (applicationName);
		this.parseRawHeaders();
		this.parseRawQueries();
		
		completeParsing (false);
	}
	
	protected void fixupApplicationName ()
	{
		applicationName = fixupApplicationName (applicationName);
	}
	
	protected String fixupApplicationName (String aApplicationName)
	{
		String retVal = null;
		String theApplicationName = aApplicationName;
		if (((String) ServerConfigurationManager.getInstance ().getProperty (ServerConfigurationManager.MAUI_CASE_SENSITIVE)).equals ("false"))
		{
			if (theApplicationName.startsWith ("/"))
			{
				theApplicationName = theApplicationName.substring (1);
			}
			Hashtable theNames = ApplicationManager.getInstance ().getAllApplicationNames ();
			retVal = (String) theNames.get (theApplicationName.toLowerCase ());
		}
		return (retVal == null ? theApplicationName : retVal);
	}
	
	protected void completeParsing (boolean aLowerCase)
	{
		parsePseudoEvents (getApplicationName ());
		
		String theUniqueReference = getQueryValue ("uniqueRef");
		uniqueReference = (theUniqueReference == null ? -1 : Integer.parseInt (theUniqueReference));
		
		String theSignificance = getQueryValue ("emptyIsSignificant");
		emptyIsSignificant =  (theSignificance != null && theSignificance.equals ("true"));
		cookies = new Hashtable (5);
		applicationName = fixupApplicationName (applicationName);
		
		if (aLowerCase)
		{
			Enumeration theKeys = headerPairs.keys ();
			String theKey;
			while (theKeys.hasMoreElements ())
			{
				theKey = (String) theKeys.nextElement();
				headerPairs.put (theKey.toLowerCase (), headerPairs.get (theKey));
			}
		}
	}	


	// ---------------------------------------------------------------------------
	// METHOD: setSession
	// ---------------------------------------------------------------------------
	
	/**
	* Set the HTTP session object for this HTTPRequest
	*
	* @param session The HTTPSession object
	*/
	protected void setSession(HTTPSession session)
	{
	  this.session = session;
	}
	
	/** Set a cookie value
	  *
	  * @param aKey The key for the cookie
	  * @param aValue The value for the cookie
	  */
	protected void setCookie (String aKey, String aValue)
	{
		cookies.put (aKey, aValue);
	}
	
	/** Get the cookies
	  * 
	  * @return Hashtable of cookies
	  */
	public Hashtable getCookies ()
	{
		return cookies;
	}
	
	/** Get a cookie
	  *
	  * @param aKey The key of retrieval
	  *
	  * @return The cookie value, or null if not found
	  */
	public String getCookie (String aKey)
	{
		return (String) cookies.get (aKey);
	}
	
	/** Have the cookies already been parsed ?
	*/
	public boolean isCookiesParsed ()
	{
		return cookiesParsed;
	}


	// ---------------------------------------------------------------------------
	// METHOD: getSession
	// ---------------------------------------------------------------------------
	
	/**
	* Get the HTTPSession object
	*
	* @return The HTTPSesison object
	*/
	public HTTPSession getSession()
		throws SessionMaximumException
	{
		return retrieveSession ();
	}
	
	public void setCookies (Hashtable aCookies)
	{
		cookiesParsed = true;
		cookies = aCookies;
	}
	
	public void setCookies (MauiCookie [] aCookies)
	{
		cookiesParsed = true;
		if (cookies == null)
		{
			cookies = new Hashtable ();
		}
		else
		{
			cookies.clear ();
		}
		
		for (int i = 0; i < aCookies.length; i++)
		{
			cookies.put (aCookies [i].getKey (),
						 aCookies [i].getValue ());
		}
	}
	
	/**
	* Retrieve the HTTPSession object
	*
	* @return The HTTPSession object
	*/
	public HTTPSession retrieveSession ()
		throws SessionMaximumException
	{
		if (!retrievalDone &&
			session == null)
		{
			retrievalDone = true;
			session = HTTPSession.retrieveSession (this);
			if (session != null)
			{
				session.setUniqueReference (uniqueReference);
			}
		}
		return session;
	}
	
	public String getClientName ()
	{
		return clientName;
	}

	// ---------------------------------------------------------------------------
	// METHOD: getURL
	// ---------------------------------------------------------------------------
	
	/**
	* Get the URL portion of the HTTP request
	*
	* @return The URL
	*/
	public String getURL ()
	{
	  return url;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: getApplicationName
	// ---------------------------------------------------------------------------
	
	/**
	* Get the application name (as specified in the HTTPRequest... it's possible that's it's not valid)
	*
	* @return The application name
	*/
	public String getApplicationName ()
	{
	  return applicationName;
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: getHeaders
	// ---------------------------------------------------------------------------
	
	/**
	* Get a hashtable of the HTTP header key/value pairs
	*
	* @return The HTTP key/value pairs
	*/
	public Hashtable getHeaders()
	{
    return (Hashtable)this.headerPairs.clone();
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: getQueries
	// ---------------------------------------------------------------------------
	/*
	* Get the query key/value pairs from the HTTPRequest
	*
	* @return Hashtable of the key/value pairs
	*/
	public Hashtable getQueries()
	{
		return (Hashtable)this.queryPairs.clone();
	}
  
  	/**
  	* Get the pseudo event hashtable
  	*
  	* @return The pseudo event array
  	*/
  	public String [] getPseudoEvents ()
  	{
  		String [] retVal = null;
  		
  		if (pseudoEvents != null)
  		{
  			Object [] theEvents = pseudoEvents.toArray ();
  			retVal = new String [theEvents.length];
  			for (int i = 0; i < theEvents.length; i++)
  			{
  				retVal [i] = (String) theEvents [i];
  			}
  		}
  		return (retVal == null ? new String [0] : retVal);
  	}

	
	// ---------------------------------------------------------------------------
	// METHOD: getHeaderValue
	// ---------------------------------------------------------------------------
	
	/**
	* Get a value from the HTTP header key/value pairs
	*
	* @param headerName The key of the key/value pair
	*
	* @return The value associated with the headerName, or null if it wasn't found
	*/
	public String getHeaderValue(String headerName)
	{
	  if (this.headerPairs.containsKey(headerName))
	  {
	    return this.headerPairs.get(headerName).toString();
	  }
	  else
	  {
	    return null;
	  }
	}

	
	// ---------------------------------------------------------------------------
	// METHOD: getQueryValue
	// ---------------------------------------------------------------------------
	
	/**
	* Get a component value posted from the browser.  This is the value used in constructing an event
	* for the associated component
	*
	* @param queryName The key of access (typically a component id)
	*
	* @return The query value, or null if not found
	*/
	public String getQueryValue(String queryName)
	{
	  if (this.queryPairs.containsKey(queryName))
	  {
	    return this.queryPairs.get(queryName).toString();
	  }
	  else
	  {
	    return null;
	  }
	}
	
	/**
	* Get the unique ref for the request.
	*
	* @return The unique ref
	*/
	public int getUniqueReference ()
	{
		return uniqueReference;
	}
	
	/**
	* Is an empty name/value pair significant?
	*
	* @return The boolean indicating so
	*/
	public boolean isEmptySignificant ()
	{
		return emptyIsSignificant;
	}
	
	/** Is this a servlet based request?
	  *
	  */
	public boolean isServletBased ()
	{
		return servletBased;
	}
	
	private final int intNibble (char [] aBytes, int aOffset)
	{
		int retVal = 0;
		char theChar = aBytes [aOffset];
		
		if (theChar >= '0' && theChar <= '9')
		{
			retVal = (int) theChar - '0';
		}
		else if (retVal >= 'a')
		{
			retVal = (int) theChar - 'a' + 10;
		}
		else
		{
			retVal = (int) theChar - 'A' + 10;
		}
		return retVal;
	}
		
	private final int intValue (char [] aBytes, int aOffset)
	{
		return ((intNibble (aBytes, aOffset) << 4) & 0xff) |
				intNibble (aBytes, aOffset + 1);
	}
	
	protected char [] getStringBytes (String aString)
	{
		char [] retVal = new char [aString.length ()];
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = aString.charAt (i);
		}
		return retVal;
	}
	
	protected String decode (char [] aName, int aLength)
	{
		int theOffset = 0;
		int theCharValue;
		for (int i = 0; i < aLength; i++)
		{
			if (aName [i] == '+')
			{
				aName [theOffset++] = ' ';
			}
			else if (aName [i] == '%')
			{
				theCharValue = intValue (aName, i + 1);
				aName [theOffset++] = (char) theCharValue;
				i += 2;
			}
			else
			{
				aName [theOffset++] = aName [i];
			}
		}
		return new String (aName, 0, theOffset);
	}
	
	protected String decode (char [] aName)
	{
		return decode (aName, aName.length);
	}
		
	protected String decode (String aName)
	{
		return decode (getStringBytes (aName));
	}
	
	private int isMatch (String aString, int aOffset, int aLength, char aMatch)
	{
		int retVal = 0;
		
		if (aString.charAt (aOffset) == aMatch)
		{
			while (retVal + aOffset + 1 < aLength &&
				   aString.charAt (aOffset + retVal + 1) == ' ')
			{
				retVal++;
			}
		}
		else
		{
			retVal = -1;
		}			   
		
		return retVal;
	}
	
	private void parseValues (Hashtable aTarget, String aString, int aLength, char aSeparator, char aTerminator)
	{
		if (aString != null)
		{
		  char [] theName = new char [aLength];
		  char [] theValue = new char [aLength];
		  
		  boolean theEqualsFound = false;
		  int theNameLength = 0;
		  int theValueLength = 0;
		  char theChar = 0;
		  int theAdjustment = 0;
		  for (int i = 0; i < aLength; i++)
		  {
		  	 theChar = aString.charAt (i);
		  	 //if (theChar != ' ')
		  	 //{
			  	 if ((theAdjustment = isMatch (aString, i, aLength, aSeparator)) != -1)
			  	 {
			  	 	theEqualsFound = true;
			  	 	i += theAdjustment;
			  	 }
			  	 else if ((theAdjustment = isMatch (aString, i, aLength, aTerminator)) == -1)
			  	 {
			  	 	if (theEqualsFound)
			  	 	{
				  	 	theValue [theValueLength++] = theChar;
				  	}
				  	else
				  	{
			  	 		theName [theNameLength++] = theChar;
			  	 	}
			  	 }
			  	 
			  	 if ((theAdjustment = isMatch (aString, i, aLength, aTerminator)) != -1 ||
			  	 	 i == aLength - 1)
			  	 {
			  	 	//
			  	 	//	Terminator found... Add it to the key/value pair array
			  	 	//
			  	 	aTarget.put (decode (theName, theNameLength), decode (theValue, theValueLength));
			  	 	theEqualsFound = false;
			  	 	theNameLength = 0;
			  	 	theValueLength = 0;
			  	 	if (theAdjustment != -1)
			  	 	{
			  	 		i += theAdjustment;
			  	 	}
			  	 }
			  //}
		   }
		}
	}
	
	// ---------------------------------------------------------------------------
	// METHOD: parseRawHeaders
	// ---------------------------------------------------------------------------
	/**
	* Parse the HTTP header key/value pairs
	*/
	private void parseRawHeaders()
	{
		parseValues (headerPairs, rawHeaders, rawHeaders.length (), ':', '\n');
	}
	  /*try
	  {
  	  StringTokenizer pairs = new StringTokenizer(this.rawHeaders, "\n");
  	  
  	  while (pairs.hasMoreTokens())
  	  {
  	    String pair = pairs.nextToken().toString();
  	    int dividerPosition = pair.indexOf(": ");
  	    
  	    if (dividerPosition != -1)
  	    {
    	    String name = pair.substring(0, dividerPosition);
    	    String value = pair.substring((dividerPosition + 2), pair.length());
  	    
  	    	name = decode (name);
  	    	value = decode (value);
  	      //try { name = URLDecoder.decode(name);   } catch (Exception exception) { }
  	      //try { value = URLDecoder.decode(value); } catch (Exception exception) { }
  	    
          this.headerPairs.put(name, value);
        }
  	  }
  	}
  	catch (NullPointerException exception) { }
	}*/


	// ---------------------------------------------------------------------------
	// METHOD: parseRawQueries
	// ---------------------------------------------------------------------------
	
	/**
	* Parse the query values from the x-queries header value
	*/
	private void parseRawQueries ()
	{
		parseRawQueries (getHeaderValue ("x-queries"));
	}
	
	/**
	* Parse the query values.  These are the values to use for creating events for components
	*/
	private void parseRawQueries (String aQueryString)
	{
	  if (aQueryString != null)
	  {
		  
		  // This fixes the problem of Netscape appending carriage returns to the end of 
		  // query strings. This code should be reevaluated in case a better solution is
		  // possible.
		  int theLength = aQueryString.length ();
		  if (aQueryString.endsWith("\r"))
		  {
		  	--theLength;
		  }
		  parseValues (queryPairs, aQueryString, theLength, '=', '&');
	  }
	}
		  /*char [] theName = new char [theLength];
		  char [] theValue = new char [theLength];
		  
		  boolean theEqualsFound = false;
		  int theNameLength = 0;
		  int theValueLength = 0;
		  char theChar = 0;
		  for (int i = 0; i < theLength; i++)
		  {
		  	 theChar = aQueryString.charAt (i);
		  	 if (theChar == '&' ||
		  	 	 i == theLength - 1)
		  	 {
		  	 	//
		  	 	//	Terminator found... Add it to the key/value pair array
		  	 	//
		  	 	queryPairs.put (decode (theName, theNameLength), decode (theValue, theValueLength));
		  	 }
		  	 else if (theChar == '=')
		  	 {
		  	 	theEqualsFound = true;
		  	 }
		  	 else if (theEqualsFound)
		  	 {
		  	 	theValue [theValueLength++] = theChar;
		  	 }
		  	 else
		  	 {
		  	 	theName [theNameLength++] = theChar;
		  	 }
		  }
		  	 
		  /*  pairs = new StringTokenizer(aQueryString.substring(0, aQueryString.length() - 1), "&");
		  }
		  else
		  {
			pairs = new StringTokenizer(aQueryString, "&");
		  }
	  	  
	  	  while (pairs.hasMoreTokens())
	  	  {
	  	    String pair = pairs.nextToken().toString();
	  	    int dividerPosition = pair.indexOf("=");
	  	    
	  	    if (dividerPosition != -1 && dividerPosition + 1 != pair.length ())
	  	    {
		  	    String name = pair.substring(0, dividerPosition);
		  	    String value = pair.substring((dividerPosition + 1), pair.length());

				name = decode (name);
				value = decode (value);
	  	      	//try { name = URLDecoder.decode(name);   } catch (Exception exception) { }
	  	      	//try { value = URLDecoder.decode(value); } catch (Exception exception) { }
	  	    
		        this.queryPairs.put(name, value);
		    }
	  	  }
	   }
  	}*/

	/**
	*	Because of some odd ways some of the WML browsers handle caching (specifically the Nokia browser)
	*	it's necessary to embed some card navigation information in the URL, and create "pseudo events"
	*	from them.  These events are delivered to MFrame_wml, and are used to select the correct deck to render.
	*
	* @param aString The String to parse
	*/
	private void parsePseudoEvents (String aString)
	{
		int theFirstPseudoEvent = aString.indexOf ("/pseudo");
		if (theFirstPseudoEvent != -1)
		{
			String theString = aString.substring (theFirstPseudoEvent + 8);
			applicationName = aString.substring (0, theFirstPseudoEvent);
			StringTokenizer theTokenizer = new StringTokenizer (theString, "/");
			
			String theToken;
			String theKey;
			String theValue;
			while (theTokenizer.hasMoreTokens ())
			{
				theToken = theTokenizer.nextToken ();
				if (theToken.startsWith ("IDz"))
				{
					theToken = theToken.substring (3);
				}
				int theUnderscore = theToken.indexOf ("_");
				if (theUnderscore != -1)
				{
					theKey = theToken.substring (0, theUnderscore);
					theValue = theToken.substring (theUnderscore + 1);
					if (pseudoEvents == null)
					{
						pseudoEvents = new Vector ();
					}
					
					if (!pseudoEvents.contains (theKey))
					{
						pseudoEvents.add (theKey);
						queryPairs.put (theKey, theValue);
					}
				}
			}
		}
	}
	
	public HTTPResponse createResponseObject ()
	{
		return new HTTPResponse ();
	}
	
	protected void setQueryPairs (Hashtable aQueryPairs)
	{
		queryPairs = aQueryPairs;
	}
	
	protected void setHeaderPairs (Hashtable aHeaderPairs)
	{
		headerPairs = aHeaderPairs;
	}
	
	protected String cookieMonster ()
	{
	  String retVal = null;
	  String cookieString = getHeaderValue("cookie");
	  if (cookieString != null)
	  {
		  cookies = new Hashtable(5);
		  parseValues (cookies, cookieString, cookieString.length (), '=', ';');
		  String theCookie = (String) cookies.get ("Maui.HTTPSession");
		  if (theCookie != null)
		  {
		  	 if (!theCookie.equals ("exit"))
		  	 {
		  		retVal = theCookie;
		  	 }
		  	 else
		  	 {
		  	 	retVal = (String) cookies.get ("Maui.HTTPOldSession");
		  	 }
		  }
	  }
	  else
	  {
		retVal = getCookie ("Maui.HTTPSession");
	  }
	  return (retVal == null || retVal.equals ("exit") ? null : retVal);
	}
	
	public String getServletURL ()
	{
		return (servletURL == null ? "/" : servletURL);
	}
	
	
	// ---------------------------------------------------------------------------
}


// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof