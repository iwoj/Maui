package com.bitmovers.maui.engine;

import java.util.EventObject;


// ======================================================================
// CLASS: ApplicationManagerEvent       (c) 2001 Bitmovers Systems
// ======================================================================

/** This is an event object that describes an ApplicationManager event.
  */
  
public class ApplicationManagerEvent extends EventObject
{
	private String name;
	
	public ApplicationManagerEvent (Object aSource,
									String aName)
	{
		super (aSource);
		name = aName;
	}
	
	public String getName ()
	{
		return name;
	}
}
