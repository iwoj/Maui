// =============================================================================
// com.bitmovers.maui.engine.servlet.MauiServletResponse
// =============================================================================

package com.bitmovers.maui.engine.servlet;
import java.io.OutputStream;

import com.bitmovers.maui.engine.httpserver.HTTPResponse;
import com.bitmovers.maui.engine.httpserver.HTTPRequest;
import com.bitmovers.maui.engine.httpserver.HTTPSession;
import com.bitmovers.maui.engine.httpserver.SessionMaximumException;
import com.bitmovers.maui.MauiCookie;
import com.bitmovers.maui.MauiApplication;

/** This is the wrapper object for a servlet Response.  It is intended to work
  * more or less transparently with the httpserver package.
  */
public class MauiServletResponse extends HTTPResponse
{
	private final HTTPRequest request;
	private MauiCookie sessionCookie;
	private MauiCookie [] applicationCookies;
	
	protected MauiServletResponse (HTTPRequest aRequest)
	{
		super ();
		request = aRequest;
	}
	
	protected void prepareResponse ()
	{
		try
		{
			//
			//	Collect all of the cookies from the session and the application
			//
			HTTPSession theSession = request.getSession ();
			sessionCookie = theSession.getSessionCookie ();
			MauiApplication theApplication = theSession.getApplication ();
			applicationCookies = (theApplication == null ?
										new MauiCookie [0] :
										theApplication.getAndClearCookies (true));
		}
		catch (SessionMaximumException e)
		{
		}
	}
	
	protected MauiCookie getSessionCookie ()
	{
		return sessionCookie;
	}
	
	protected MauiCookie [] getApplicationCookies ()
	{
		return applicationCookies;
	}
	
	protected void setErrorCode (OutputStream aOutputStream, int aErrorCode)
	{
		errorCode = aErrorCode;
	}
}