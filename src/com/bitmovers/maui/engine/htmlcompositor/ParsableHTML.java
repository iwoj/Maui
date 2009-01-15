package com.bitmovers.maui.engine.htmlcompositor;

import java.io.*;

// =============================================================================
// CLASS: ParsableHTML
// =============================================================================

/** This interface defines two methods necessary for all classes that make use
  * of HTML template files.
  *	
  * @author Ian Wojtowicz (ian@bitmovers.com)
  * @version 1999.07.09-A
  */
  
public abstract interface ParsableHTML
{
	public abstract String getHTMLTemplate();
	public abstract String getHTMLParsed();
}