// =============================================================================
// com.bitmovers.maui.engine.render.MRadioButton_wml
// =============================================================================

package com.bitmovers.maui.engine.render;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.MRadioButton;
import com.bitmovers.maui.components.foundation.MRadioButtonGroup;

public class MRadioButton_wml extends MSelectList_wml
	//implements I_HasEpilogue
	//		   I_HasForwardPrologue
{
	public MRadioButton_wml ()
	{
		super ();
	}
	
	public String render (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ();
		MRadioButton theRadioButton = (MRadioButton) aRenderable;
		renderable = aRenderable;
		representativeRenderable = theRadioButton.getRadioButtonGroup ().getRadioButton (0);
		if (renderable == representativeRenderable)
		{
			MRadioButtonGroup theRadioButtonGroup = theRadioButton.getRadioButtonGroup ();
			//onPick = generateComponentID ((MComponent) aRenderable) + "_RadioCard";
			//allowMultiples = true;
			/*retVal.append (super.generateSelectList (this,
													 generateComponentID (theRadioButton),
													 ((HasSelectList) aRenderable).getSelectListOptions (),
													 getValue (theRadioButtonGroup),
													 " ",
													 null,
													 false,
													 new SimpleOnPickGenerator ()));*/
			retVal.append (super.render (theRadioButtonGroup));
		}
		return retVal.toString ();
	}
	
	public String generateComponentID (MComponent aComponent)
	{
		return super.generateComponentID ((aComponent instanceof MRadioButtonGroup ? (MComponent)renderable : 
		                                                                             aComponent));
	}
	
	/**
	* Get the MauiApplication object from the renderable
	*
	* @param aRenderable The I_Renderable object
	*
	* @return The MauiApplication object
	*/
	/*protected MauiApplication getMauiApplication (I_Renderable aRenderable)
	{
		MComponent theComponent = (MComponent) aRenderable;
		if (theComponent instanceof MRadioButtonGroup)
		{
			theComponent = ((MRadioButtonGroup) theComponent).getRadioButtons () [0];
		}
		return (MauiApplication) theComponent.getRootParent ();
	}
	
	public String generateEpilogue (I_Renderable aRenderable)
	{
		StringBuffer retVal = new StringBuffer ("");
		MRadioButton theRadioButton = (MRadioButton) aRenderable;
		
		if (theRadioButton.getButtonIndex () == 0)
		{
			retVal.append ("\n");
			MRadioButtonGroup theRadioButtonGroup = theRadioButton.getRadioButtonGroup ();		
			retVal.append (generateGoCard (onPick,
										   theRadioButtonGroup,
										   "$(" +
										   	generateComponentID (theRadioButtonGroup) + ")"));//":e)"));
		}
		return retVal.toString ();
	}*/
	
	public String getValue (I_Renderable aRenderable)
	{
		MRadioButtonGroup theButtonGroup = (MRadioButtonGroup) aRenderable;
		return theButtonGroup.getSelectedButton ().getLabel ();
	}	
}