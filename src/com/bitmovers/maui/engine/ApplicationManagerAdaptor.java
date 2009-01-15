package com.bitmovers.maui.engine;

import java.util.EventListener;


// ======================================================================
// CLASS: ApplicationManagerAdaptor       (c) 2001 Bitmovers Systems
// ======================================================================

public abstract class ApplicationManagerAdaptor
	implements ApplicationManagerListener
{
	/**
	* The extensions folder has been loaded
	*
	* @param aApplicationMangaerEvent Object describing the event
	*/
	public void extensionsLoaded (ApplicationManagerEvent aEvent)
	{
	}
	
	/**
	* A Maui application suite has been loaded
	*
	* @param aApplicationManagerEvent Object describing the event
	*/
	public void applicationSuiteLoaded (ApplicationManagerEvent aEvent)
	{
	}
	
	/**
	* A Maui application suite has been unloaded
	*
	* @param aApplicationManagerEvent Object describing the event
	*/
	public void applicationSuiteUnloaded (ApplicationManagerEvent aEvent)
	{
	}
	
	/**
	* A Maui application suite has been reloaded
	*
	* @param aApplicationManagerEvent Object describing the event
	*/
	public void applicationSuiteReloaded (ApplicationManagerEvent aEvent)
	{
	}
}
