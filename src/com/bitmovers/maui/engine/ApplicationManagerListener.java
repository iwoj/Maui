package com.bitmovers.maui.engine;

import java.util.EventListener;


// ======================================================================
// INTERFACE: ApplicationManagerListener       (c) 2001 Bitmovers Systems
// ======================================================================

/** This is a listener interface for events issued by the
  * ApplicationManager
  */
  
public interface ApplicationManagerListener extends EventListener
{
	public static final int LOADED = 1;
	public static final int UNLOADED = 2;
	public static final int RELOADED = 3;
	public static final int EXTENSIONS = 4;
	
	/**
	* The extensions folder has been loaded
	*
	* @param aApplicationMangaerEvent Object describing the event
	*/
	public void extensionsLoaded (ApplicationManagerEvent aEvent);
	
	/**
	* A Maui application suite has been loaded
	*
	* @param aApplicationManagerEvent Object describing the event
	*/
	public void applicationSuiteLoaded (ApplicationManagerEvent aEvent);
	
	/**
	* A Maui application suite has been unloaded
	*
	* @param aApplicationManagerEvent Object describing the event
	*/
	public void applicationSuiteUnloaded (ApplicationManagerEvent aEvent);
	
	/**
	* A Maui application suite has been reloaded
	*
	* @param aApplicationManagerEvent Object describing the event
	*/
	public void applicationSuiteReloaded (ApplicationManagerEvent aEvent);
}
