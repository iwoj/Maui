// =============================================================================
// com.bitmovers.maui.engine.servlet.RemoteRequestObject
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import java.io.Serializable;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.ServletInputStream;

import com.bitmovers.maui.MauiCookie;

/** This object encapsulates a response to be sent back to the remote servlet
  * object.
  */
public class RemoteRequestObject implements Serializable
{
	private Hashtable headers = new Hashtable ();
	private String pathInfo;
	private String applicationName;
	private String servletName;
	private Hashtable queryValues = new Hashtable ();
	private MauiCookie [] cookies;
	byte [] content;
	private String queries = null;
	
	protected RemoteRequestObject (HttpServletRequest aRequest)
		throws IOException
	{
		Enumeration theKeys = aRequest.getHeaderNames ();
		while (theKeys.hasMoreElements ())
		{
			String theKey = (String) theKeys.nextElement ();
			headers.put (theKey, aRequest.getHeader (theKey));
		}
		
		int theContentLength = aRequest.getContentLength ();
		if (theContentLength > -1)
		{
			content = new byte [theContentLength];
			aRequest.getInputStream ().read (content, 0, theContentLength);
			parseLine (content);
		}
		else
		{
			content = new byte [0];
		}
		
		applicationName = aRequest.getPathInfo ();
		servletName = aRequest.getContextPath ();
		servletName += aRequest.getServletPath ();
		queries = aRequest.getQueryString ();
		StringBuffer theKey = new StringBuffer ();
		StringBuffer theValue = new StringBuffer ();
		if (queries != null)
		{
			parseLine (queries.getBytes ());
		}
		
		
		Cookie [] theCookies = aRequest.getCookies ();
		if (theCookies != null)
		{
			cookies = new MauiCookie [theCookies.length];
			for (int i = 0; i < cookies.length; i++)
			{
				cookies [i] = new MauiCookie (theCookies [i].getName (),
											  theCookies [i].getValue (),
											  null,//theCookies [i].getMaxAge (),
											  theCookies [i].getPath (),
											  theCookies [i].getDomain ());
			}
		}
		else
		{
			cookies = new MauiCookie [0];
		}
	}
	
	private String decode (String aToDecode)
	{
		String retVal = null;
		try
		{
			retVal = URLDecoder.decode (aToDecode);
		}
		catch (Exception e)
		{
			retVal = aToDecode;
		}
		return retVal;
	}
	
	private void parseLine (byte [] aLine)
	{
		int theStart = 0;
		boolean theIsKey = true;
		StringBuffer theKey = new StringBuffer ();
		StringBuffer theValue = new StringBuffer ();
		for (int i = 0; i < aLine.length; i++)
		{
			if ((char) aLine [i] == '&')
			{
				String theKeyString = decode (theKey.toString ());
				String theValueString = decode (theValue.toString ());
				queryValues.put (theKeyString, theValueString);
				theKey.setLength (0);
				theValue.setLength (0);
				theIsKey = true;
				theStart = i + 1;
			}
			else if ((char) aLine [i] == '=')
			{
				theIsKey = false;
			}
			else if (theIsKey)
			{
				theKey.append ((char) aLine [i]);
			}
			else
			{
				theValue.append ((char) aLine [i]);
			}
		}
		
		if (theValue.length () > 0)
		{
			String theKeyString = decode (theKey.toString ());
			String theValueString = decode (theValue.toString ());
			queryValues.put (theKeyString, theValueString);
		}
	}
		
	
	protected Hashtable getHeaders ()
	{
		return headers;
	}
	
	protected Hashtable getQueryValues ()
	{
		return queryValues;
	}
	
	protected String getQueryValue ()
	{
		return queries;
	}
	
	protected String getHeaderValue (String aHeaderValue)
	{
		return (String) headers.get (aHeaderValue);
	}
	
	protected Enumeration getHeaderNames ()
	{
		return headers.keys ();
	}
	
	protected MauiCookie [] getCookies ()
	{
		return cookies;
	}
	
	protected String getQueryValue (String aKey)
	{
		return (String) queryValues.get (aKey);
	}
	
	protected byte [] getContent ()
	{
		return content;
	}
	
	protected String getApplicationName ()
	{
		return (applicationName == null ? "" : applicationName);
	}
	
	protected String getServletName ()
	{
		return (servletName == null ? "" : servletName);
	}
}