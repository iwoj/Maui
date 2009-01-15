// ======================================================================
// com.bitmovers.maui.applications.HelloMauiTwo
// ======================================================================

package com.bitmovers.maui.applications;

import java.awt.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.foundation.*;


// ======================================================================
// CLASS: HelloMauiTwo
// ======================================================================

/** HelloMauiTwo is a more complex Maui Application, and is mostly
  * intended to be a component demo.
  *
  */

public class HelloMauiTwo extends MauiApplication
{
  // --------------------------------------------------------------------
	private MFrame window;
	private MMenuBar menubar;
	private MMenu fileMenu;
	private MMenuItem newMenuItem;
	
	private MTabbedPane tabbedPane;
	
	private MLabel welcomeText;
	private MLabel nameLabel;
	private MTextField nameField;
	private MLabel activityLabel;
	private MTextArea activityArea;
	
	private MExpandPane additionalInfoPane;
	private MLabel useOsLabel;
	private MCheckBox macOsCheckBox;
	private MCheckBox unixCheckBox;
	private MCheckBox windowsCheckBox;
	
	private MLabel iPreferLabel;
	private MRadioButton macOsRadioButton;
	private MRadioButton unixRadioButton;
	private MRadioButton windowsRadioButton;
	
	private MLabel favouriteNumberLabel;
	private MSelectList favouriteNumberSelectList;
	
	private MLabel panelTwoText1;
	private MLabel panelTwoText2;

	private String name = null;
	

	// --------------------------------------------------------------------
	// CONSTRUCTOR: HelloMauiTwo
	// --------------------------------------------------------------------
	
	public HelloMauiTwo (Object aInitializer) 
	{
		// [1] Call the super's constructor with the title of the application.
		super(aInitializer, "Hello Maui (Part Two)");

		// [2] Call the method which sets up our application
		initializeWindow();    
	}


	// --------------------------------------------------------------------
	// METHOD: initializeWindow
	// --------------------------------------------------------------------

	private void initializeWindow()
	{
		// [1] Create a new MFrame to contain our view. The first argument
		//     is the title of the frame, and the second is the width in
		//     pixels (for platforms which support that (HTML being the only
		//     only currently).
		window = new MFrame("Hello Maui! (Part Two)", 300);
		
		// [2] Setup our MMenuBar
		menubar = new MMenuBar();
		fileMenu = new MMenu("File");
		newMenuItem = new MMenuItem("New");
		{
			fileMenu.add(newMenuItem);
			menubar.add(fileMenu);

			MMenuButton button = newMenuItem.getButton();
			button.addActionListener(new MActionListener()
			{
				public void actionPerformed(MActionEvent event)
				{
					HelloMauiTwo.this.initializeWindow();
				}
			});
		}
		
		// [3] Register the MMenuBar with the window
		window.setMenuBar(menubar);
		
		// [4] Create a tabbed pane
		tabbedPane = new MTabbedPane();
		{
			MPanel panelOne = createPanelOne();
			MPanel panelTwo = createPanelTwo();
			
			tabbedPane.add(panelOne, "Panel One");
			tabbedPane.add(panelTwo, "Panel Two");
		}
		
		// [5] Create an action listener for the tabbed pane button
		MTabbedPaneButton button = tabbedPane.getTabButton(1);
		button.addActionListener(new MActionListener()
		{
			public void actionPerformed(MActionEvent event)
			{
				String name = (String)HelloMauiTwo.this.nameField.getValue();
				
				// Check for an empty name.
				if (name.equals(""))
				{
					name = "yet-to-be-named user";
				}
				
				HelloMauiTwo.this.panelTwoText1.setText("Hello, " + name + ".");
			}
		});

		// Add the MTabbedPane to the window
		window.add(tabbedPane);


		// [x] Add the window to the MauiApplication
		add(window);
	}


	// --------------------------------------------------------------------
	// METHOD: createPanelOne
	// --------------------------------------------------------------------
	
	private MPanel createPanelOne()
	{
		MPanel panelOne = new MPanel();
		
		welcomeText = new MLabel("Welcome to the Hello Maui! Part Two demo. This application demonstrates some of the components which Maui provides.");
		
		nameLabel = new MLabel("Please enter your name:");
		nameField = new MTextField(20);
		activityLabel = new MLabel("Please describe your favourite activity:");
		activityArea = new MTextArea(new Dimension(25, 5));

		additionalInfoPane = new MExpandPane("Additional Information");
		{
			useOsLabel = new MLabel("I use the following OS's:");
			macOsCheckBox = new MCheckBox("Mac OS");
			unixCheckBox = new MCheckBox("Unix/Linux");
			windowsCheckBox = new MCheckBox("Windows");

			iPreferLabel = new MLabel("I prefer to use:");
			MRadioButtonGroup iPreferRadioGroup = new MRadioButtonGroup("prefer to use");
			macOsRadioButton = iPreferRadioGroup.addRadioButton("Mac OS");
			unixRadioButton = iPreferRadioGroup.addRadioButton("Unix/Linux");
			windowsRadioButton = iPreferRadioGroup.addRadioButton("Windows");

			favouriteNumberLabel = new MLabel("My favourite number is:");
			String[] numbers = { "Less than 42", "42", "More than 42" };
			favouriteNumberSelectList = new MSelectList(numbers);
			
			additionalInfoPane.add(useOsLabel);
			additionalInfoPane.add(macOsCheckBox);
			additionalInfoPane.add(unixCheckBox);
			additionalInfoPane.add(windowsCheckBox);
			additionalInfoPane.add(iPreferLabel);
			additionalInfoPane.add(macOsRadioButton);
			additionalInfoPane.add(unixRadioButton);
			additionalInfoPane.add(windowsRadioButton);
			additionalInfoPane.add(favouriteNumberLabel);
			additionalInfoPane.add(favouriteNumberSelectList);
		}

		panelOne.add(welcomeText);
		panelOne.add(nameLabel);
		panelOne.add(nameField);
		panelOne.add(activityLabel);
		panelOne.add(activityArea);
		panelOne.add(additionalInfoPane);
		
		return panelOne;
	}


	// --------------------------------------------------------------------
	// METHOD: createPanelTwo
	// --------------------------------------------------------------------
	
	private MPanel createPanelTwo()
	{
		MPanel panelTwo = new MPanel();
		
		panelTwoText1 = new MLabel("Hello, untitled user.");
		panelTwoText2 = new MLabel("By now, you would probably assume that all the information you entered on the first tab has disappeared. Well guess again! Click on the 'Panel One' tab and see for yourself!");

		panelTwo.add(panelTwoText1);
		panelTwo.add(panelTwoText2);
		
		return panelTwo;
	}


	// --------------------------------------------------------------------
}


// ======================================================================
// Copyright © 2000 Bitmovers Software, Inc.                          eof