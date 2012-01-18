// =============================================================================
// com.bitmovers.maui.engine.render.MSelectList_wml
// =============================================================================

package com.bitmovers.maui.engine.render;
import java.util.Enumeration;
import java.util.Vector;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.HasSelectList;
import com.bitmovers.maui.components.foundation.Settable;

public class MSelectList_wml extends DefaultWmlRenderer
{
	protected String onPick = null;
	protected boolean allowMultiples = false;
	
	protected static String generateSelectList (A_Renderer aRenderer,
												String aComponentID,
												Vector aList,
												String aSelectedValue,
												String aIndentation)
	{
		return generateSelectList (aRenderer, aComponentID, aList, aSelectedValue, aIndentation, null, false, null);
	}
	
	protected static String generateSelectList (A_Renderer aRenderer,
												String aComponentID,
												Vector aList,
												String aSelectedValue,
												String aIndentation,
												Object aOnPickRef)
	{
		return generateSelectList (aRenderer, aComponentID, aList, aSelectedValue, aIndentation, aOnPickRef, false, null);
	}
	
	protected static String generateSelectList (A_Renderer aRenderer,
												String aComponentID,
												Vector aList,
												String aSelectedValue,
												String aIndentation,
												Object aOnPickRef,
												boolean aAllowMultiples,
												I_OnPickGenerator aOnPickGenerator)
	{
		StringBuffer retVal =
			new StringBuffer (aIndentation);
		I_OnPickGenerator theOnPickGenerator = (aOnPickGenerator == null ?
													new DefaultOnPickGenerator (aRenderer) :
													aOnPickGenerator);
		retVal.append ("<select name=\"");
		retVal.append (aComponentID);
		retVal.append ("\" value=\"");
		retVal.append (aSelectedValue);
		retVal.append ("\" ");
		if (aAllowMultiples)
		{
			retVal.append ("multiple=\"true\" ");
		}
		retVal.append (">\n");
		
		Enumeration theEnumeration = aList.elements ();
		String theValue;
		String theLabel;
		boolean theUseOnPickRef = (aOnPickRef != null);
		boolean theUseArray = true;
		String theOnPickRef = null;
		
		if (theUseOnPickRef && aOnPickRef instanceof String)
		{
			theOnPickRef = theOnPickGenerator.generateOnPick (aComponentID, (String) aOnPickRef);
			theUseArray = false;
		}
		
		int i = 0;
		while (theEnumeration.hasMoreElements())
		{
			Object theRawElement = theEnumeration.nextElement ();
			if (theRawElement instanceof String)
			{
				theValue = theLabel = (String) theRawElement;
			}
			else
			{
				theValue = ((String []) theRawElement) [0];
				theLabel = ((String []) theRawElement) [1];
			}
			
			retVal.append (aIndentation);
			retVal.append (" <option title=\"Pick\" value=\"");
			retVal.append (theValue);
			
			if (theUseOnPickRef)
			{
				if (theUseArray)
				{
					theOnPickRef = theOnPickGenerator.generateOnPick (aComponentID, ((String []) aOnPickRef) [i]);
					if (i + 1 < ((String []) aOnPickRef).length)
					{
						i++;
					}
				}
				retVal.append (theOnPickRef);
			}
			else
			{
			    retVal.append ("\">");
			}
			retVal.append (theLabel);
			retVal.append ("</option>\n");
		}
		
		retVal.append (aIndentation  + "</select>");
		return retVal.toString ();
	}
	
	public String render (I_Renderable aRenderable)
	{
		return render (aRenderable, null);
	}
	
	protected String render (I_Renderable aRenderable,
							 I_OnPickGenerator aOnPickGenerator)
	{
		String retVal = new String ();
		HasSelectList theSelectList = (HasSelectList) aRenderable;
		return generateSelectList (this,
		                           generateComponentID((MComponent)aRenderable),
		                           theSelectList.getSelectListOptions(),
		                           getValue(aRenderable),
		                           "  ",
		                           onPick,
		                           allowMultiples,
		                           aOnPickGenerator);
	}
}

class DefaultOnPickGenerator implements I_OnPickGenerator
{
	A_Renderer renderer;
	protected DefaultOnPickGenerator (A_Renderer aRenderer)
	{
		renderer = aRenderer;
	}
	
	/**
	* Generate the onpick statement
	*
	* @param aComponentID The componentId associated with this onpick
	* @param aOnPickValue The associated onpick value
	*
	* @return The generated on pick code
	*/
	public String generateOnPick (String aComponentID,
								  String aOnPickValue)
	{
		StringBuffer retVal = new StringBuffer ("\">\n");
		retVal.append ("<onevent type=\"onpick\">\n");
		retVal.append (renderer.generatePostHeader ());
		retVal.append (">\n<setvar name=\"");
		retVal.append (aComponentID);
		retVal.append ("\" value=\"");
		retVal.append (aOnPickValue);
		retVal.append ("\"/>\n</go>\n</onevent>");
		return retVal.toString ();
	}
	
}

class SimpleOnPickGenerator implements I_OnPickGenerator
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
								  String aOnPickValue)
	{
		return "\" onpick=\"#" + aOnPickValue + "\">";
	}
}
