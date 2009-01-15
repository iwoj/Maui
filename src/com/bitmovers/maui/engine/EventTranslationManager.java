package com.bitmovers.maui.engine;

import java.io.*;
import java.net.*;
import java.util.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.httpserver.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.utilities.*;


// =============================================================================
// <<SINGLETON>> CLASS: EventTranslationManager
// =============================================================================

public class EventTranslationManager
{
	// ---------------------------------------------------------------------------

  private static EventTranslationManager theInstance = new EventTranslationManager();  
  
  private Hashtable processHashtable;

	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR: EventTranslationManager
	// ---------------------------------------------------------------------------
	
	private EventTranslationManager()
	{
		this.processHashtable = new Hashtable();

		System.err.println(new DebugString("[EventTranslationManager] - Started."));
	}


	// ---------------------------------------------------------------------------
	// CLASS METHOD: getInstance
	// ---------------------------------------------------------------------------
	
	public static EventTranslationManager getInstance()
	{
	  return theInstance;
	}
		

	// ---------------------------------------------------------------------------
	// METHOD: runEventTranslationManager
	// ---------------------------------------------------------------------------


	// ---------------------------------------------------------------------------

}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof