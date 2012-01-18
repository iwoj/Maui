// =============================================================================
// CHANGELOG:
//++ 249 MW 2001.08.03
// Modified the Layout Manager for the Log Window to be able to resize. Added
// weights to the components, and re-organized the creation of the window.
// =============================================================================



package com.bitmovers.maui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;


// ---------------------------------------------------------------------------
// SINGLETON CLASS: MauiRuntimeWindow
// ---------------------------------------------------------------------------

/** <code>MauiRuntimeWindow</code> opens up a new Window on platforms running graphical
  * clients. It will allow users to shutdown the engine, as well provide some
  * information about where to go to get more information.
  *
  */

public class MauiRuntimeWindow implements LogListener
{
	
	
	private static MauiRuntimeWindow theInstance = null;
	public Frame window = null;
	private Panel panel = null;
	private MenuBar menubar = null;
	private Menu file = null;
	private MenuItem quit = null;
	private TextArea logArea = null;
	private Button shutdownButton = null;
	private String hostName = null;
	private String port = null;
	private boolean hostNameSet = false;
	private Label hostNameLabel = null;

	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------
	
	private MauiRuntimeWindow()
	{
		//++ 249 MW 2001.08.03
	
		// [1] Create the window
		window = new Frame("Maui Console");
		window.move(10,10);
		Insets insets = window.getInsets();
		insets.top = 5;
		insets.bottom = 5;
		insets.left = 5;
		insets.right = 5;
		window.addWindowListener(new WindowAdapter()
		{
			// Exit when the window is closed
			public void windowClosing(WindowEvent event)
			{
				System.exit(0);
			}
		});

		// [2] Create Menubar
		menubar = new MenuBar();
		file = new Menu("File");
		quit = new MenuItem("Quit", new MenuShortcut((int)'q'));
		quit.addActionListener(new ActionListener()
		{
			// Exit when the Quit item is selected
      public void actionPerformed(ActionEvent event)
      {
        System.exit(0);
      }
		});

		file.add(quit);
		menubar.add(file);
		window.setMenuBar(menubar);		


		// [3] Logging Text Area
		logArea = new TextArea(6, 74);
		logArea.setEditable(false);


		// [4] Shutdown button
		shutdownButton = new Button("Shutdown Maui Engine");
		shutdownButton.addActionListener(new ActionListener()
		{
			// Exit when the Shutdown button is pressed
      public void actionPerformed(ActionEvent event)
      {
        System.exit(0);
      }
    });


		// [5] Get the hostname of the local computer
		port     = ServerConfigurationManager.getInstance().getProperty(ServerConfigurationManager.MAUI_PORT);
		try
		{
			hostName = InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException exception)
		{
			hostName = "127.0.0.1";
		}


		// [6.0] Create the Panel and set Layout.
		panel = new Panel();
		GridBagLayout panelGridBag = new GridBagLayout();
		panel.setLayout(panelGridBag);
		
		GridBagConstraints panelConstraints = new GridBagConstraints();
		panelConstraints.gridwidth = GridBagConstraints.REMAINDER;
		

		// [7] Add the components to the panel
		panel.add(Box.createVerticalStrut(5), createGridBagConstraints(1, 1));
		hostNameLabel = new Label(generateLabel ());
		panel.add(hostNameLabel, createGridBagConstraints(1, 2));
		panel.add(Box.createVerticalStrut(5), createGridBagConstraints(1, 3));
		
		GridBagConstraints constraints = createGridBagConstraints(1, 4);
		constraints.fill = GridBagConstraints.BOTH;
		// set the weight of the log area to one.
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		panel.add(logArea, constraints);
		
		panel.add(Box.createVerticalStrut(5), createGridBagConstraints(1, 5));
		panel.add(shutdownButton, createGridBagConstraints(1, 6, GridBagConstraints.EAST));
		panel.add(Box.createVerticalStrut(5), createGridBagConstraints(1, 7));

		
		// [8] Add Panel to the window, and display.
		window.add(panel);
		window.pack();
		window.show();
		
		//-- 249

	}
	

	//-----------------------------------------------------------------------
	// METHOD: generateLabel
	//-----------------------------------------------------------------------
	
	private String generateLabel ()
	{
		return "Welcome to Maui. Your Maui engine is located at: http://" + hostName + ":" + port + "/";
	}
	
	
	//-----------------------------------------------------------------------
	// METHOD: getInstance
	//-----------------------------------------------------------------------
	
	/** Returns the instance of the <code>MauiRuntimeWindow</code>. If it hasn't been created 
	  * yet, then it will create it.
		*
		* @return The instance of <code>MauiRuntimeWindow</code>.
		* 
		*/
		
	public static MauiRuntimeWindow getInstance()
	{
		if (MauiRuntimeWindow.theInstance == null)
		{
			MauiRuntimeWindow.theInstance = new MauiRuntimeWindow();
		}
		
		return MauiRuntimeWindow.theInstance;
	}


	//-----------------------------------------------------------------------
	// METHOD: createGridBagConstraints
	//-----------------------------------------------------------------------

	private GridBagConstraints createGridBagConstraints(int x, int y)
	{
	  return createGridBagConstraints(x, y, GridBagConstraints.CENTER);
	}

	
	//-----------------------------------------------------------------------
	// METHOD: createGridBagConstraints
	//-----------------------------------------------------------------------

	private GridBagConstraints createGridBagConstraints(int x, int y, int anchor)
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		
		//++ 249 MW 2001.08.03
		// set default weight of components to zero.
		gridBagConstraints.weightx = 0.0;
		gridBagConstraints.weighty = 0.0;
		//-- 249
		
		gridBagConstraints.anchor = anchor;
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);
		
		return gridBagConstraints;
	}
	

	//-----------------------------------------------------------------------
	// METHOD: setHostName
	//-----------------------------------------------------------------------
	
	/** Sets the host name (as it is defined by a client accessing the 
	  * server environment).
		*
		* @param aHostName the host name to use.
		* 
		*/
		
	public void setHostName (String aHostName)
	{
		if (!hostNameSet)
		{
			hostNameSet = true;
			if (!aHostName.equals ("localhost"))
			{
				hostName = aHostName;
				hostNameLabel.setText (generateLabel ());
				window.pack ();
			}
		}
	}


	//-----------------------------------------------------------------------
	// METHOD: processLogMessage
	//-----------------------------------------------------------------------
	
	/** Writes a log message to the window.  This will only work until the 
	  * <code>TextArea</code> has filled up.
		*
		* @param logMessage the message to log to the window.
		* 
		*/
		
	public void processLogMessage(String logMessage)
	{
		logArea.append(logMessage);
	}

	
}


// ========================================================================
//                                               (c) 2001 Bitmovers Systems