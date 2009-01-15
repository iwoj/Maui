package com.bitmovers.maui;

import java.io.Serializable;

public class MauiCookie implements Serializable
{
	protected String key;
	protected String value;
	protected String path;
	protected String expires;
	protected String domain;
	protected String cookieString;
	
	public MauiCookie (String aKey,
					   String aValue,
					   String aExpires,
					   String aPath,
					   String aDomain)
	{
		key = aKey;
		value = aValue;
		path = aPath;
		expires = aExpires;
		domain = aDomain;
		
	}
	
	public String getValue ()
	{
		return value;
	}
	
	public String getKey ()
	{
		return key;
	}
	
	public String getPath ()
	{
		return path;
	}
	
	public String getExpires ()
	{
		return expires;
	}
	
	public String getDomain ()
	{
		return domain;
	}
	
	public String getCookieString ()
	{
		return cookieString;
	}
	
	public String generateCookie (String aDomain)
	{
		StringBuffer retVal = new StringBuffer ("Set-Cookie: ");
		retVal.append (key);
		retVal.append ("=");
		retVal.append (value);
		if (domain != null)
		{
			retVal.append (";domain=");
			retVal.append (domain);
		}
		
		if (expires != null)
		{
			retVal.append (";Expires=");
			retVal.append (expires);
		}
		
		retVal.append (";Path=");
		retVal.append ((path == null ? "/" : path));
		return retVal.toString ();
	}
}

