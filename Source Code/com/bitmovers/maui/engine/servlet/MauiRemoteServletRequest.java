// =============================================================================
// com.bitmovers.maui.engine.servlet.MauiRemoteServletRequest
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import com.bitmovers.maui.engine.httpserver.HTTPRequest;
import com.bitmovers.maui.engine.httpserver.HTTPResponse;

/** This is the wrapper object for a servlet request.  It is intended to work
  * more or less transparently with the httpserver package.
  */
public class MauiRemoteServletRequest extends HTTPRequest
{
	private final RemoteRequestObject request;
	private final String hostName;
	protected MauiRemoteServletRequest (RemoteRequestObject aRequest, String aHostName)
	{
		super (true);
		applicationName = fixupApplicationName (aRequest.getApplicationName ());
		servletURL = aRequest.getServletName ();
		if (!servletURL.endsWith ("/"))
		{
			servletURL = servletURL + "/";
		}
		request = aRequest;
		hostName = aHostName;
		byte [] theContent = aRequest.getContent ();
		setQueryPairs (request.getQueryValues ());
		setHeaderPairs (request.getHeaders ());
		completeParsing (true);
		setCookies (request.getCookies());
	}
	
	public HTTPResponse createResponseObject ()
	{
		return new MauiServletResponse (this);
	}
	
	/*public String getQueryValue (String aQueryValue)
	{
		return request.getQueryValue (aQueryValue);
	}
	
	public String getHeaderValue (String aHeaderValue)
	{
		return request.getHeaderValue (aHeaderValue);
	}*/
	
	/*public String getApplicationName ()
	{
		return applicationName;
	}*/
	
	public String getClientName ()
	{
		return hostName;
	}
}