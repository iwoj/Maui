// =============================================================================
// com.bitmovers.maui.engine.servlet.MauiServletConnection
// =============================================================================

package com.bitmovers.maui.engine.servlet;

import java.net.Socket;
import java.io.IOException;

import com.bitmovers.maui.engine.httpserver.HTTPConnection;

/** This is the wrapper object for a servlet connection.  It is intended to work
  * more or less transparently with the httpserver package.
  */
public class MauiServletConnection extends HTTPConnection
{
	protected MauiServletConnection ()
	{
		super ();
	}
}