package com.bitmovers.maui.engine.wmlcompositor;


// =============================================================================
// CLASS: ParsableWML
// =============================================================================

/** This interface defines two methods necessary for all classes that make use
  * of WML template files.
  */
  
public abstract interface ParsableWML
{
	/**
	* Retrieve a template which is used by the StringParser to construct WML code
	*
	* @return A String containing the template
	*/
	public abstract String getWMLTemplate();
	
	/**
	* Get the completely constructed WML rendering of the component
	*
	* @return The String containing the completely parsed WML code
	*/
	public abstract String getWMLParsed();
}