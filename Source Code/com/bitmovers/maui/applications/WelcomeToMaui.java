// ======================================================================
// com.bitmovers.maui.applications.WelcomeToMaui
// ======================================================================


package com.bitmovers.maui.applications;

import java.util.*;
import java.net.*;

import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.utilities.*;


// ======================================================================
// CLASS: WelcomeToMaui
// ======================================================================

public class WelcomeToMaui extends MauiApplication
{
  // --------------------------------------------------------------------
  
  
  private MFrame window;
	private MTabbedPane tabbedPane;
	
	
  // --------------------------------------------------------------------
  // CONSTRUCTOR: WelcomeToMaui
  // --------------------------------------------------------------------
	
  public WelcomeToMaui(Object aInitializer)
  {
    super(aInitializer, "Welcome to Bitmovers Maui");
    
    window = new MFrame("Welcome to Bitmovers Maui", 350);
    
    // Logo image
    try
    {
    	window.add(new MImage("/com/bitmovers/maui/applications/WelcomeToMaui/logo.gif", 160, 64), MBoxLayout.CENTER);
    }
    catch (ResourceNotFoundException e)
    {
    	// Do nothing
    }
    
    // Version
    if (MauiRuntimeEngine.getInstance().isLicensed())
    {
	    window.add(new MLabel("Version " + MauiRuntimeEngine.getVersion() + " Pro"), MBoxLayout.CENTER);
    }
    else
    {
	    window.add(new MLabel("Version " + MauiRuntimeEngine.getVersion() + " Lite"), MBoxLayout.CENTER);
	    MLabel buyNow = new MLabel("UPGRADE NOW");
	    buyNow.setLink("http://maui.bitmovers.com/purchase");
	    buyNow.setBold(true);
	    window.add(buyNow, MBoxLayout.CENTER);
    }
    
		// Tabbed pane
		tabbedPane = new MTabbedPane();
		{
			MPanel usingMauiPanel = createUsingMauiPanel();
			MPanel writingMauiAppsPanel = createWritingMauiAppsPanel();
			
			tabbedPane.add(usingMauiPanel, "Using Maui");
			tabbedPane.add(writingMauiAppsPanel, "Writing Maui Applications");
		}
		
		window.add(tabbedPane);
		
		add(window);
  }
	
	
  // --------------------------------------------------------------------
  // METHOD: createUsingMauiPanel
  // --------------------------------------------------------------------
  
	private MPanel createUsingMauiPanel()
	{
		MPanel panel = new MPanel();
		
		// Link to User's Guide.
		try
		{
			panel.add(new MLabel("For detailed information on using Maui, please refer to our online documentation at:"));
			MLabel link1 = new MLabel("http://maui.bitmovers.com/documents");
			link1.setLink (new URL("http://maui.bitmovers.com/documents").toString());
			panel.add(link1, MBoxLayout.LEFT);
			
			panel.add(new MDivider());
		}
		catch (MalformedURLException e)
		{
			System.err.println("WelcomeToMaui: MalformedURLException for link to documentation.");
		}
		
		// Application Directory
		panel.add(new MLabel("The following Maui application lists a directory of all Maui applications available on this server:"));
		MButton directoryButton = new MButton("Application Directory");
		addChainApplicationSource (directoryButton, "ApplicationDirectory");
		panel.add(directoryButton, MBoxLayout.CENTER);
		
		// Maui Administrator
		panel.add(new MLabel("The following Maui application allows you to perform basic administration tasks on this server:"));
		MButton adminButton = new MButton("Maui Administrator");
		addChainApplicationSource (adminButton, "MauiAdministrator");
		panel.add(adminButton, MBoxLayout.CENTER);
		
		panel.add(new MDivider());
		panel.add(new MLabel("Maui currently supports 25 client environments:"));
		
		
		//
		// Mac OS 9
		//
		
		MExpandPane mac9HTMLBrowsersPane = new MExpandPane("Mac OS 9 (6)");
		
		addBrowserLink(mac9HTMLBrowsersPane,
		               "Microsoft Internet Explorer 5.0", 
		               "http://microsoft.com/mac/download/ie/ie50.asp");
		
		addBrowserLink(mac9HTMLBrowsersPane,
		               "Mozilla 0.9.3", 
		               "ftp://ftp.mozilla.org/pub/mozilla/releases/mozilla0.9.3/mozilla-mac-0.9.3.sea.bin");
		
		addBrowserLink(mac9HTMLBrowsersPane,
		               "Netscape 6.1", 
		               "http://home.netscape.com/computing/download/index.html?cp=hophb2");
		
		addBrowserLink(mac9HTMLBrowsersPane,
		               "Netscape Communicator 4.78", 
		               "http://home.netscape.com/download/0730101/10000-en-macppc-4.78-complete-128_qual.html");
		
		addBrowserLink(mac9HTMLBrowsersPane,
		               "Netscape Navigator 4.08", 
		               "http://home.netscape.com/download/0730101/10002-en-macppc-4.08-base-128_qual.html");
		
		addBrowserLink(mac9HTMLBrowsersPane,
		               "Opera 5.0 (Classic) Beta 2", 
		               "http://www.opera.com/download/?platform=mac");
		
		panel.add(mac9HTMLBrowsersPane);
		
		// Mac OS X
		
		MExpandPane macXHTMLBrowsersPane = new MExpandPane("Mac OS X (4)");
		
		macXHTMLBrowsersPane.add(new MLabel("Microsoft Internet Explorer 5.1"));
		
		addBrowserLink(macXHTMLBrowsersPane,
		               "Mozilla 0.9.2", 
		               "ftp://ftp.mozilla.org/pub/mozilla/releases/mozilla0.9.2/");
		
		addBrowserLink(macXHTMLBrowsersPane,
		               "Netscape 6.1 Preview Release", 
		               "ftp://ftp.netscape.com/pub/netscape6/english/6.1/mac/macosx/sea/Netscape6-macosX.sit.bin");
		
		addBrowserLink(macXHTMLBrowsersPane,
		               "Opera 5.0 (Carbon) Beta 1", 
		               "http://www.opera.com/download/?platform=mac");
		
		panel.add(macXHTMLBrowsersPane);
		
		//
		// Supported WAP clients
		//
		MExpandPane supportedWAPBrowsersPane = new MExpandPane("Mobile Phones (4)");
		
		addBrowserLink(supportedWAPBrowsersPane,
		               "Nokia WAP Toolkit 2.1", 
		               "http://forum.nokia.com/");
		
		addBrowserLink(supportedWAPBrowsersPane,
		               "OpenWave UP.Simulator 4.1", 
		               "http://developer.openwave.com/download/license_41.html");
		
		addBrowserLink(supportedWAPBrowsersPane,
		               "OpenWave UP.Simulator 3.2", 
		               "http://developer.openwave.com/download/license_32.html");
		
		addBrowserLink(supportedWAPBrowsersPane,
		               "Yospace SmartPhone 2.0", 
		               "http://www.yospace.com/login.html");
		
		panel.add(supportedWAPBrowsersPane);
		
		//
		// Supported Palm clients
		//
		MExpandPane supportedPalmClientsPane = new MExpandPane("Palm OS (1)");
		
		addBrowserLink(supportedPalmClientsPane,
		               "Web Clipping Applications", 
		               "http://www.palmos.com/dev/tech/webclipping/");
		
		/*               
		addBrowserLink(supportedPalmClientsPane,
		               "Go.Web 6.0", 
		               "http://www.palmos.com/dev/tech/webclipping/");
		*/
		
		panel.add(supportedPalmClientsPane);
		
		/*
		//
		// Supported PocketPC clients
		//
		MExpandPane supportedPocketPCClientsPane = new MExpandPane("PocketPC (1)");
		
		addBrowserLink(supportedPocketPCClientsPane,
		               "Go.Web 5.01", 
		               "http://www.goamerica.com/downloads/");
		
		panel.add(supportedPocketPCClientsPane);
		
		*/
		
		//
		// Supported BlackBerry clients
		//
		MExpandPane supportedBlackBerryClientsPane = new MExpandPane("BlackBerry (1)");
		
		addBrowserLink(supportedBlackBerryClientsPane,
		               "Go.Web 6.0", 
		               "http://www.goamerica.com/downloads/");
		
		/*               
		addBrowserLink(supportedPalmClientsPane,
		               "Go.Web 6.0", 
		               "http://www.palmos.com/dev/tech/webclipping/");
		*/
		
		panel.add(supportedBlackBerryClientsPane);
		
		//
		// Supported Windows Clients
		//
		
		MExpandPane windowsHTMLBrowsersPane = new MExpandPane("Windows (9)");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Espial Escape 4.8", 
		               "http://www.espial.com/main/page?view=p-escp_main");
		
		/*
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Go.Web 5.5", 
		               "http://www.goamerica.com/downloads/");
		*/
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Microsoft Internet Explorer 5.01 SP2", 
		               "http://www.microsoft.com/windows/ie/downloads/recommended/ie501sp2");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Microsoft Internet Explorer 5.5 SP1", 
		               "http://www.microsoft.com/windows/ie/downloads/recommended/ie501sp2");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Microsoft Internet Explorer 6.0", 
		               "http://www.microsoft.com/windows/ie/downloads/ie6");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Mozilla 0.9.3", 
		               "http://ftp.mozilla.org/pub/mozilla/releases/mozilla0.9.3");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Netscape 6.1", 
		               "http://home.netscape.com/computing/download/index.html?cp=hophb2");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Netscape Communicator 4.78", 
		               "http://home.netscape.com/download/0730101/10000-en-win32-4.78-complete-128_qual.html");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Netscape Navigator 4.08", 
		               "http://home.netscape.com/download/0730101/10002-en-win32-4.08-base-128_qual.html");
		
		addBrowserLink(windowsHTMLBrowsersPane,
		               "Opera 5.12", 
		               "http://www.opera.com/download/?platform=win");
		
		panel.add(windowsHTMLBrowsersPane);
		
		/*
		//
		// Supported WindowsCE clients
		//
		MExpandPane supportedWindowsCEClientsPane = new MExpandPane("Windows CE (1)");
		
		addBrowserLink(supportedWindowsCEClientsPane,
		               "Go.Web 5.0", 
		               "http://www.goamerica.com/downloads/");
		
		panel.add(supportedWindowsCEClientsPane);
		*/
		
		
		return panel;
	}
	
	
  // --------------------------------------------------------------------
  // METHOD: createWritingMauiAppsPanel
  // --------------------------------------------------------------------
  
	private MPanel createWritingMauiAppsPanel()
	{
		MPanel panel = new MPanel(new MBoxLayout(MBoxLayout.Y_AXIS, MBoxLayout.LEFT));
		
		//http://java.sun.com/docs/books/tutorial/uiswing/mini
		
		try
		{
			MLabel step1 = new MLabel("Step 1:");
			step1.setBold(true);
			panel.add(step1);
			panel.add(new MLabel("In order to start creating your own Maui applications, you should have a basic understanding of Java programming. Java is easy to learn, and the following tutorial should provide a good starting point:"));
			MLabel link1 = new MLabel("http://java.sun.com");
			link1.setLink(new URL("http://java.sun.com/docs/books/tutorial/java/index.html").toString());
			panel.add(link1);
			
			panel.add(new MDivider());
		}
		catch (MalformedURLException e)
		{
			// Do nothing.
		}
		
		try
		{
			MLabel step2 = new MLabel("Step 2:");
			step2.setBold(true);
			panel.add(step2);
			panel.add(new MLabel("Once you are familiar with Java programming, have a look at some sample Maui source code:"));
			MLabel link1 = new MLabel("http://maui.bitmovers.com/samplecode");
			link1.setLink(new URL("http://maui.bitmovers.com/samplecode").toString());
			panel.add(link1);
			
			panel.add(new MDivider());
		}
		catch (MalformedURLException e)
		{
			// Do nothing.
		}
		
		try
		{
			MLabel step3 = new MLabel("Step 3:");
			step3.setBold(true);
			panel.add(step3);
			panel.add(new MLabel("For more in-depth information, please read through our API documentation:"));
			MLabel link1 = new MLabel("http://maui.bitmovers.com/api");
			link1.setLink(new URL("http://maui.bitmovers.com/api").toString());
			panel.add(link1);
		}
		catch (MalformedURLException e)
		{
			// Do nothing.
		}
		
		return panel;
	}
	
	
  // --------------------------------------------------------------------
  // METHOD: addBrowserLink
  // --------------------------------------------------------------------
  
	private void addBrowserLink(MExpandPane expandPane, String label, String url)
	{
		try
		{
			MLabel browserLink = new MLabel(label);
			browserLink.setLink(new URL(url).toString());
			expandPane.add(browserLink);
		}
		catch (MalformedURLException e) {}
	}
}


// ======================================================================
// Copyright © 2000 Bitmovers Software, Inc.                          eof