// ======================================================================
// com.bitmovers.maui.applications.HelloMauiOne
// ======================================================================

package com.bitmovers.maui.applications;

import com.bitmovers.maui.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.foundation.*;


// ======================================================================
// CLASS: HelloMauiOne
// ======================================================================

/** HelloMauiOne is the simplest of Maui Applications, and is based upon
  * the infamous "Hello World!" application that all programmers write
  * when they try a new environment or language.
  *
  * All Maui applications extend the MauiApplication class. Every view,
  * or "window" is an MFrame. When an MFrame is added to a
  * MauiApplication, it becomes the forefront window.
  *
  * MauiApplications are accessed through the Maui Engine via their
  * class name. For example, to access this application, you would go to:
  *
  * http://myhost.com:port/HelloMauiOne
  *
  * where 'myhost.com' is the host on which the Maui Engine is running
  * and 'port' is the value of the ‘maui.port’ property as defined in
  * 'maui.properties'.
  *
  */

public class HelloMauiOne extends MauiApplication
{
  // --------------------------------------------------------------------
  // CONSTRUCTOR: HelloMauiOne
  // --------------------------------------------------------------------
	
  public HelloMauiOne (Object aInitializer) 
  {
  	// [1] Call the super's constructor with the title of the application.
    super(aInitializer, "Hello Maui (Part One)");
    
    // [2] Create a new MFrame to contain our view. The first argument
    //     is the title of the frame, and the second is the width in
    //     pixels (for platforms which support that (HTML being the only
    //     only currently).
		MFrame window = new MFrame("Hello Maui! (Part One)", 250);
		
		// [3] Create some text to add to the frame.
		MLabel text = new MLabel("Hello Maui!");
		
		// [4] Add the text to the frame
		window.add(text);
		
		// [5] Add the frame to the MauiApplication
		add(window);
  }

  // --------------------------------------------------------------------
}


// ======================================================================
// Copyright © 2000 Bitmovers Software, Inc.                          eof