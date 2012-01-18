package com.bitmovers.maui.applications;

import java.util.*;
import java.net.*;
import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.foundation.*;


// ========================================================================
// CLASS: ApplicationDirectory                   (c) 2001 Bitmovers Systems
// ========================================================================

/** This Maui application provides a detailed listing of all Maui 
  * applications available on the same server, and allows users to easily
  * launch them. This app is the the default Maui application in the 
  * standard Maui installation, meaning that this app will be launched
  * if no application is specified in the Maui URL, or if the URL refers
  * to a non-existent application.
  * 
  */
  
public class ApplicationDirectory extends MauiApplication
{
  
  private ApplicationManager appManager;
  private MPanel directoryPanel;
  private MLabel totalSessions;
  
  private Thread sessionWatcherThread;

	
  // ----------------------------------------------------------------------
  // CONSTRUCTOR: ApplicationDirectory
  // ----------------------------------------------------------------------
	
	/** Constructs a new <code>ApplicationDirectory</code>.
	  *
	  */
	  
  public ApplicationDirectory (Object aInitializer) 
  {
    super (aInitializer, "Maui Application Directory");
    
		appManager = ApplicationManager.getInstance();
		
    MFrame window = new MFrame("Maui Application Directory", 400);
		window.add(new MLabel("Welcome to the Maui Application Directory. Click on an expand pane for more information about available Maui applications. Click on a launch button to start using that application."));
		
		totalSessions = new MLabel();
  	totalSessions.setBold(true);
		window.add(totalSessions);
		
		directoryPanel = new MPanel();
		window.add(directoryPanel);
		add(window);
		readApplications();
		
	  //
	  // Session watcher thread.
	  //
		sessionWatcherThread = new Thread(new Runnable()
	  {
	  	public void run()
	  	{
	  		while (true)
	  		{
			  	readTotalSessions();
		  		try
		  		{
			  		Thread.sleep (15 * 1000); // 15 seconds of glorious sleep.
			  	}
			  	catch (InterruptedException e) {}
		  	}
	  	}
	  }, "ApplicationManager - session watcher thread");
	  sessionWatcherThread.start();
  }
	
	
  // ----------------------------------------------------------------------
  // METHOD: readTotalSessions
  // ----------------------------------------------------------------------
	
  private synchronized void readTotalSessions() 
  {
  	totalSessions.setText("Total sessions: " + appManager.getSessionCount());
  }
	
	
  // ----------------------------------------------------------------------
  // METHOD: readApplications
  // ----------------------------------------------------------------------
	
  private void readApplications() 
  {
  	directoryPanel.removeAll();
  	
 		final String[] folderNames = appManager.getApplicationFolders();
 		
		for (int k = 0; k < folderNames.length; k++)
		{
			Vector appDescriptions = new Vector();
			String[] appSuites = appManager.getApplicationSuiteNames(folderNames[k]);
			final String thisClass = getClass().getName();
			boolean thisFolderHasApplications = false;
			
			// Initialize folder name
			String theFolderName = folderNames[k];
			if (theFolderName.length() > 0)
			{
				theFolderName = theFolderName + "/";
			}
			
			// Iterate through JAR files in this folder
			for(int i = 0; i < appSuites.length; i++)
			{
				try
				{
					final String [] appNames = (appManager.getApplicationNames(folderNames[k], appSuites[i]));
					for (int j = 0; j < appNames.length; j++)
					{
						final String appClass = appManager.getMauiApplicationClassName(folderNames[k], appNames[j]);
						final String appShortName = appManager.getSimpleShortName(folderNames[k], appClass);
						final Object[] tableData = new Object[2];
						
						// Application Info Column
						final MExpandPane expandPane = new MExpandPane(appShortName);
/*
						expandPane.addActionListener (new MActionListener ()
							{
								public void actionPerformed (MActionEvent event)
								{
									if (!expandPane.isOpen ())
									{
										readApplications();
									}
								}
							});
*/
						expandPane.add(new MLabel("Location: " + appSuites[i] + ".jar"), MLayout.LEFT);
						expandPane.add(new MLabel("Class: " + appClass), MLayout.LEFT);
						tableData[0] = expandPane;
						
						// Don't create a launch link for Application Directory
						if (!thisClass.equals(appClass))
						{
							MButton launchButton = new MButton("Launch");
							final String theChain = theFolderName + appClass;
							launchButton.addActionListener(new MActionListener()
					    {
					  		public void actionPerformed(MActionEvent event)
								{
					  			ApplicationDirectory.this.setChainApplicationName(theChain);
					  			event.consume();
					  		}
					  	});
							tableData[1] = launchButton;
						}
						appDescriptions.addElement(tableData);
						
						thisFolderHasApplications = true;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					System.out.println(new WarningString("ApplicationDirectory.readApplications(): Skipped application suite."));
				}
			}
			
			if (thisFolderHasApplications)
			{
				Object[] appDescriptionsArray = appDescriptions.toArray();
				Object[][] tableData = new Object[appDescriptionsArray.length][2];
				
				for (int i = 0; i < appDescriptionsArray.length; i++)
				{
					tableData[i] = (Object[]) appDescriptionsArray[i];
				}
				
				MTable appTable = new MTable(tableData);
				appTable.setDefaultAlignment(MLayout.LEFT);
				
				final MExpandPane folderExpandPane = new MExpandPane("/" + ServerConfigurationManager.MAUI_APPLICATION_FOLDER_VALUE + "/" + theFolderName);
/*
				folderExpandPane.addActionListener (new MActionListener ()
				{
					public void actionPerformed (MActionEvent event)
					{
						if (!folderExpandPane.isOpen ())
						{
							readApplications ();
						}
					}
				});
*/
				folderExpandPane.add(appTable);
				folderExpandPane.open();
				
				directoryPanel.add(folderExpandPane);
			}
		}
		
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF