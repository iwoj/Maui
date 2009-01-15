// =============================================================================
// com.bitmovers.maui.engine.render.I_ListGenerator
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_UsesListGenerator <p>
* This interface is used for renderers which can use I_ListGenerators for generating
* their lists.
*/
public interface I_UsesListGenerator
{
	/**
	* Generate a list of MComponents to render
	*
	* @param aRenderable The reference I_Renderable object
	* @param aListGenerator The I_ListGenerator to use in generating the list
	*
	* @return The rendered String
	*/
	public String render (I_Renderable aRenderable,
						  I_ListGenerator aListGenerator);
						  
	/**
	* Get the filter class name for the list generator to use
	*
	* @return The filter list class
	*/
	public Class getFilterClass ();
}
