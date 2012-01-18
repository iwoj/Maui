// ======================================================================
// com.bitmovers.maui.applications.MauiAdministrator
// ======================================================================

package com.bitmovers.maui.applications;

import java.util.*;
import java.awt.*;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.resourcemanager.*;


// ======================================================================
// CLASS: MauiAdministrator
// ======================================================================

public class MauiAdministrator extends MauiApplication
{
  // --------------------------------------------------------------------
  private MFrame theWindow;
  private MFrame theConfirmWindow;
	

  // --------------------------------------------------------------------
  // CONSTRUCTOR: MauiAdministrator
  // --------------------------------------------------------------------
	
  public MauiAdministrator(Object aInitializer) 
  {
    super(aInitializer, "Maui Administrator");
    
    final MLabel theErrorLabel;
    final MTextField thePasswordField;
    final MPanel thePasswordPanel;
    final MButton theShutdownButton;
    
    theWindow = new MFrame("Maui Administrator", 300);
		
		theErrorLabel = new MLabel("");
		{
      theErrorLabel.setBold(true);
      theErrorLabel.setColor(Color.red);
    }
    theWindow.add(theErrorLabel);
        
    theWindow.add(new MLabel("Welcome to the Maui Administrator. Currently the only administrative function one can perform is to shutdown the Maui Runtime Engine.\n\nEnter the administrator password and click on the Shutdown button to shutdown.\n\n"), MBoxLayout.LEFT);
    
    thePasswordField = new MTextField(35, true);
    
    thePasswordPanel = new MPanel();
    {
      thePasswordPanel.setLayout(new MBoxLayout(thePasswordPanel, MBoxLayout.X_AXIS));
      thePasswordPanel.add(new MLabel("Password:"), MLayout.LEFT);
      thePasswordPanel.add(thePasswordField, MLayout.LEFT);
    }
    theWindow.add(thePasswordPanel, MLayout.LEFT);
    
		// Shutdown MButton
    theShutdownButton = new MButton("Shutdown");
    {
      theShutdownButton.addActionListener(new MActionListener()
	    {
	  		public void actionPerformed(MActionEvent event)
				{
				  String thePassword;
				  final ServerConfigurationManager scm = ServerConfigurationManager.getInstance();
				  
				  //get the password from maui.properties and compare with the entered password
				  //if no property is found, the password is set to blank

				  if ((thePassword = (String)scm.getProperty(scm.MAUI_ADMIN_PASSWORD)) == null)
				  {
				    System.err.println(new InfoString("The administrator password property was not found... returning the default"));
				  } 
				  
				  if (((String)thePasswordField.getValue()).equals(thePassword))
				  {
				    theErrorLabel.setText("");
				    System.err.println(new InfoString("MauiAdministrator is shutting Maui down..."));
	  			  System.exit(0);
	  			  //MauiAdministrator.this.confirm();
	  			}
	  			else
	  			{
	  			  //display an 'invalid password' error message
	  			  theErrorLabel.setText("You have specified an invalid password.\n\n");
	  			}
	  		}
	  	});
	  }
    theWindow.add(theShutdownButton, MLayout.RIGHT);

    add(theWindow);
  }


  // --------------------------------------------------------------------
  // METHOD: confirm
  // --------------------------------------------------------------------
  
  private void confirm()
  {
    final MButton theOkayButton;
    final MButton theCancelButton;
    
  	theConfirmWindow = new MFrame("Confirm Shutdown", 250);
  	theConfirmWindow.add(new MLabel("Do you really want to shutdown? (If you answer 'Okay', you will not get a response, as the Maui Runtime Engine will not be running.)"));

  	theCancelButton = new MButton("Cancel");
  	{
  		theCancelButton.addActionListener(new MActionListener()
  		{
  			public void actionPerformed(MActionEvent event)
  			{
					// Go back to the main screen
  				MauiAdministrator.this.add(MauiAdministrator.this.theWindow);
  			}
  		});
  	}
  	theConfirmWindow.add(theCancelButton);

  	theOkayButton = new MButton("Okay");
  	{
  		theOkayButton.addActionListener(new MActionListener()
  		{
  			public void actionPerformed(MActionEvent event)
  			{
  				System.exit(0);
  			}
  		});
  	}
  	theConfirmWindow.add(theOkayButton);

  	add(theConfirmWindow);
  }


  // --------------------------------------------------------------------
}


// ======================================================================
// Copyright © 2000 Bitmovers Software, Inc.                          eof