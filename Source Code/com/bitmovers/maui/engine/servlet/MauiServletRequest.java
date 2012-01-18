// =============================================================================
// com.bitmovers.maui.engine.servlet.MauiServletRequest
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import javax.servlet.http.HttpServletRequest;

import com.bitmovers.maui.engine.httpserver.HTTPRequest;
import com.bitmovers.maui.engine.httpserver.HTTPResponse;

/** This is the wrapper object for a servlet request.  It is intended to work
  * more or less transparently with the httpserver package.
  */
public class MauiServletRequest extends HTTPRequest
{
	private final HttpServletRequest request;
	protected MauiServletRequest (HttpServletRequest aRequest)
	{
		super (true);
		request = aRequest;
	}
	
	public HTTPResponse createResponseObject ()
	{
		return new MauiServletResponse (this);
	}
}