// =============================================================================
// com.bitmovers.maui.engine.render.I_Generator
// =============================================================================

package com.bitmovers.maui.engine.render;

/*
* I_OnPickGenerator INTERFACE <p>
* This is used to allow a renderer to create the "onpick" statement for a select list
* item.  It is a callback method
*/
public interface I_OnPickGenerator
{
	/**
	* Generate the onpick statement
	*
	* @param aComponentID The componentId associated with this onpick
	* @param aOnPickValue The associated onpick value
	*
	* @return The generated on pick code
	*/
	public String generateOnPick (String aComponentID,
								  String aOnPickValue);
}