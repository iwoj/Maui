// ======================================================================
// com.bitmovers.maui.applications.TrivialMauiApplication
// ======================================================================

package com.bitmovers.maui.applications;

import java.util.*;
import com.bitmovers.utilities.*;

import com.bitmovers.maui.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.foundation.*;


// ======================================================================
// CLASS: TrivialMauiApplication
// ======================================================================

public class TrivialMauiApplication extends MauiApplication
{
  // --------------------------------------------------------------------
  
  
  private final String english = "English";
	private final String englishMessage = "Hello. Welcome to Trivial Maui App.";
	private final String french = "francais";
	private final String frenchMessage = "Bonjour. Bienvenue au TrivialMauiApp.";
	private final String spanish = "espanol";
	private final String spanishMessage = "Hola. Bienvenidos al TrivialMauiApp.";
	private final String polish = "polski";
	private final String polishMessage = "Czesc. Witamy cie do TrivialMauiApp.";
	private final String german = "deutsche";
	private final String germanMessage = "Hallo. Willkommen zu TrivialMauiApp.";
	
	
  private MFrame window;
  private MLabel messageText;
  private MSelectList messageSelector;
  private MButton okayButton;
  private MButton clearButton;
	
	
  // --------------------------------------------------------------------
  // CONSTRUCTOR: TrivialMauiApplication
  // --------------------------------------------------------------------
	
  public TrivialMauiApplication(Object aInitializer) 
  {
    super(aInitializer, "Trivial Maui App");
    
    window = new MFrame("Trivial Maui App", 250);
		
		messageText = new MLabel(englishMessage);
		window.add(messageText);
		
		String[] selectListItems = { english, french, spanish, polish, german };
		messageSelector = new MSelectList(selectListItems);
		window.add(messageSelector);
		
		// OK MButton
    okayButton = new MButton("Change the language");
    okayButton.addActionListener(new MActionListener() {
    		public void actionPerformed(MActionEvent event)
  			{
    			TrivialMauiApplication.this.updateMessage();
    		}
    	});
    window.add(okayButton);
    
    add(window);
  }


  // --------------------------------------------------------------------
  // METHOD: updateMessage
  // --------------------------------------------------------------------
  
  public void updateMessage()
  {
  	if (messageSelector.getValue().equals(english))
  	{
    	messageText.setText(englishMessage);
    	okayButton.setLabel("Change the language");
    }
    else if (messageSelector.getValue().equals(french))
  	{
    	messageText.setText(frenchMessage);
    	okayButton.setLabel("Change la langue");
    }
    else if (messageSelector.getValue().equals(spanish))
  	{
    	messageText.setText(spanishMessage);
    	okayButton.setLabel("Cambie el lenguaje");
    }
    else if (messageSelector.getValue().equals(polish))
  	{
    	messageText.setText(polishMessage);
    	okayButton.setLabel("Zmien jezyk");
    }
    else if (messageSelector.getValue().equals(german))
  	{
    	messageText.setText(germanMessage);
    	okayButton.setLabel("Andern Sie die Sprache"); // €ndern Sie die Sprache
    }
  }

  // --------------------------------------------------------------------
}


// ======================================================================
// Copyright © 2000 Bitmovers Software, Inc.                          eof