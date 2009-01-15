// =============================================================================
// com.bitmovers.maui.httpserver.SessionMaximumException
// =============================================================================

package com.bitmovers.maui.engine.httpserver;

public class SessionMaximumException extends Exception
{
	private final int limit;
	
	public SessionMaximumException (int aLimit)
	{
		limit = aLimit;
	}

	public int getLimit ()
	{
		return limit;
	}
}