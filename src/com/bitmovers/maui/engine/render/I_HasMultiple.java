package com.bitmovers.maui.engine.render;

/**
* I_HasMultiple INTERFACE <p>
* This interface indicates that a component may produce multiple MauiEvents
* from a single POST event.  Since this is a renderer level property (ie.
* it may be different between HTML and WML) it is handled at the renderer level.
*/
public interface I_HasMultiple
{
	/**
	* Get the component id's from the request string
	*
	* @param aComponentID The component id
	* @param aRequestValue The request value to parse
	*
	* @return The list of component ids
	*/
	public String [] getComponentIDs (String aComponentID, String aRequestValue);
	
	/**
	* Get the state data for a given component
	*
	* @param aComponentID	The component ID
	* @param aStateData		Any state data that can be gathered
	*
	* @return The state data to associate with the component
	*/
	public String getStateData (String aComponentID, String aStateData);
}