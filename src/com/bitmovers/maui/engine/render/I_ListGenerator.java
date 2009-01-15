// =============================================================================
// com.bitmovers.maui.engine.render.I_ListGenerator
// =============================================================================

package com.bitmovers.maui.engine.render;

/**
* I_ListGenerator <p>
* This interface is used for renderers which are capable of generating lists (or sublists)
* of MComponents.  This is useful for such things as creating MCheckBox details in tables,
* etc.
*/
public interface I_ListGenerator
{
	/**
	* Generate a list of MComponents to render
	*
	* @param aRenderable The reference I_Renderable object
	* @param aListClass The Class to use as a filter for generating the list
	*
	* @return An array of I_Renderables to render
	*/
	public I_Renderable [] generateList (I_Renderable aRenderable,
										 Class aListClass);
}
