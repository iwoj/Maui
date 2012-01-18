// =============================================================================
// com.bitmovers.maui.engine.servlet.RemoteResponseObject
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import java.io.Serializable;

import com.bitmovers.maui.MauiCookie;

/** This object encapsulates a response to be sent back to the remote servlet
  * object.
  */
public class RemoteResponseObject implements Serializable
{
	private final byte [] content;
	private final String contentType;
	private final MauiCookie sessionCookie;
	private final MauiCookie [] applicationCookies;
	private final String redirection;
	private final boolean limitExceeded;
	private final int errorCode;
	
	protected RemoteResponseObject (int aErrorCode)
	{
		this (null, null, null, null, null, true, aErrorCode);
	}
	
	protected RemoteResponseObject (byte [] aContent,
									String aContentType,
									MauiCookie aSessionCookie,
									MauiCookie [] aApplicationCookies,
									String aRedirection,
									boolean aLimitExceeded)
	{
		this (aContent,
			  aContentType,
			  aSessionCookie,
			  aApplicationCookies,
			  aRedirection,
			  aLimitExceeded,
			  -1);
	}
	
	private RemoteResponseObject (byte [] aContent,
								  String aContentType,
								  MauiCookie aSessionCookie,
								  MauiCookie [] aApplicationCookies,
								  String aRedirection,
								  boolean aLimitExceeded,
								  int aErrorCode)
	{
		content = aContent;
		contentType = aContentType;
		sessionCookie = aSessionCookie;
		applicationCookies = aApplicationCookies;
		redirection = aRedirection;
		limitExceeded = aLimitExceeded;
		errorCode = aErrorCode;
	}
	
	protected byte [] getContent ()
	{
		return content;
	}
	
	protected boolean isLimitExceeded ()
	{
		return limitExceeded;
	}
	
	protected MauiCookie getSessionCookie ()
	{
		return sessionCookie;
	}
	
	protected MauiCookie [] getApplicationCookies ()
	{
		return applicationCookies;
	}
	
	public String getRedirection ()
	{
		return redirection;
	}
	
	public String getContentType ()
	{
		return contentType;
	}
	
	public int getErrorCode ()
	{
		return errorCode;
	}
}