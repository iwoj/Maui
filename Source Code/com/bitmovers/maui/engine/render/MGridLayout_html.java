// ========================================================================
// com.bitmovers.maui.engine.render.MGridLayout_html
// ========================================================================

package com.bitmovers.maui.engine.render;

import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.utilities.StringParser;


// ========================================================================
// CLASS: MGridLayout_html                       (c) 2001 Bitmovers Systems
// ========================================================================

/** MGridLayout_html is the HTML renderer for the MGridLayout layout manager.
  * 
  */

public class MGridLayout_html extends DefaultHtmlLayoutRenderer implements I_RendererInitialize
{
	// ----------------------------------------------------------------------
	private int columns;


	// ----------------------------------------------------------------------
	// CONSTRUCTOR
	// ----------------------------------------------------------------------

	public MGridLayout_html()
	{
		super();
	}


	// ---------------------------------------------------------------------------
	// METHOD: render
	// ---------------------------------------------------------------------------
	
	public String render(I_Renderable aRenderable)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<!-- GRIDLAYOUT -->\n");
		
		try
		{
			MGridLayout gridLayout = (MGridLayout)aRenderable;
			MContainer parent = gridLayout.getParentContainer();
			MComponent[] components = parent.getComponents();
			int componentCounter = 0;
			
			int numberOfCells = gridLayout.getColumns() * gridLayout.getRows();
			
			// Output a warning if there are more components than there are spots
			if (components.length > numberOfCells)
			{
				System.err.println(new WarningString("The GridLayout has more components than its sized for. Extraneous components will be ignored."));
			}
			else if (components.length < numberOfCells)
			{
				System.err.println(new WarningString("The GridLayout has fewer components than its sized for. Blanks cells will result."));
			}
			
			// If we are rendering with grid divider lines, we need to do some
			// some fancy table work. If not, we won't bother.
			
			if (gridLayout.isGridDividerLines())
			{
			  buffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"");
			  if (gridLayout.isSpanMaximumWidth())
			  {
			  	buffer.append(" width=\"100%\"");
			  }
			  buffer.append("><tr><td bgcolor=\"");
			  buffer.append(MDesktop.getHexStringFromColor(gridLayout.getGridDividerLineColor()));
			  buffer.append("\">\n");
			}
			
			buffer.append("<table border=\"0\" cellpadding=\"");
			buffer.append(gridLayout.getCellPadding());
			buffer.append("\" cellspacing=\"1\"");
		  if (gridLayout.isSpanMaximumWidth())
		  {
		  	buffer.append(" width=\"100%\"");
		  }
			buffer.append(">\n");

			for (int i = 0; i < gridLayout.getRows(); i++)
			{
				buffer.append(" <tr bgcolor=\"");
				buffer.append(MDesktop.getHexStringFromColor(gridLayout.getCellColor()));
				buffer.append("\">\n");
				
				for (int j = 0; j < gridLayout.getColumns(); j++)
				{
					try
					{
						MComponent component = components[componentCounter];
						MLayout.Alignment alignment = (MLayout.Alignment)parent.getConstraints(component);
						
						if (alignment == null)
						{
							alignment = gridLayout.getDefaultAlignment();
						}
						
						buffer.append("  <td align=\"");
						buffer.append(alignment.toString());
						buffer.append("\">");
						buffer.append(doRenderComponent(component));
						buffer.append("</td>\n");
					}
					catch (Exception exception)
					{
						exception.printStackTrace(System.err);
						
						buffer.append("  <td>&nbsp;</td>\n");
					}
					
					componentCounter++;
				}
				
				buffer.append(" </tr>\n");
			}
			
			buffer.append("</table>\n");
		
			if (gridLayout.isGridDividerLines())
			{
			  buffer.append("</td></tr></table>\n");
			}
		}
		catch (Exception exception)
		{
		
		}

		buffer.append("<!-- end GRIDLAYOUT -->\n");
		
		return buffer.toString();
	}


	// ---------------------------------------------------------------------------
	// METHOD: renderSeparatedComponent
	// ---------------------------------------------------------------------------
	
	/** renderSeparatedComponent() overrides A_Renderer.renderSeparatedComponent()
	  * because in BoxLayout, the separator between each component may be
	  * different, as alignment will differ between each component.
	  *
	  */

	protected String renderSeparatedComponent(MComponent component, String separator)
	{
		StringBuffer rendered = new StringBuffer();
		MContainer container = component.getParent();
		
		try
		{
			Object constraints = container.getConstraints(component);
			
			// Handle improper contraint objects.
			if (!(constraints instanceof MLayout.Alignment))
			{
				if (constraints != null)
				{
					constraints = container.getLayout().getAlignment();
					System.out.println(new DebugString("MBoxLayout only supports only constraint objects of type Alignment. Use BoxLayout.LEFT, .CENTER, and .RIGHT for constraints with this LayoutManager. Defaulting to " + (MLayout.Alignment)constraints) + ".");
				}
				else
				{
					constraints = container.getLayout().getAlignment();
				}
			}

			MBoxLayout layout = (MBoxLayout)container.getLayout();

			final int lastComponent = (container.getComponentCount() - 1);
			final int componentIndex = container.getComponentIndex(component); 

			switch (layout.getAxis())
			{
				// X axis
				case MBoxLayout.X_AXIS:
				{
					switch (componentIndex)
					{
						// First component
						case 0:
						{
							// If there is only one component...
							if (componentIndex == lastComponent)
							{
								rendered.append(" <tr>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
								rendered.append("</td>\n </tr>");
							}
							else
							{
								rendered.append(" <tr>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
							}
							
							break;
						}
						
						// All others
						default:
						{
							// If this is the last component...
							if (componentIndex == lastComponent)
							{
								rendered.append("</td>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
								rendered.append("</td>\n </tr>");
							}
							else
							{
								rendered.append("</td>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
							}

							break;
						}
					}
					break;
				}
				
				// Y axis
				case MBoxLayout.Y_AXIS:
				{
					switch (componentIndex)
					{
						case 0:
						{
							// If there is only one component...
							if (componentIndex == lastComponent)
							{
								rendered.append(" <tr>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
								rendered.append("</td>\n </tr>");
							}
							else
							{
								rendered.append(" <tr>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
							}
							
							break;
						}

						default:
						{
							// If this is the last component...
							if (componentIndex == lastComponent)
							{
								rendered.append("</td>\n </tr>\n <tr>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
								rendered.append("</td>\n </tr>");
							}
							else
							{
								rendered.append("</td>\n </tr>\n <tr>\n  <td align=\"" + constraints + "\">");
								rendered.append(super.doRenderComponent(component));
							}

							break;
						}
					}
				}
			}
		}
		catch (Exception exception)
		{
	  	System.err.println(new WarningString("MGridLayout_html.renderSeparatedComponent(): " +
	  																			 "(Container: " + container + ")" +
	  																			 "(Component: " + component + ")" +
	  																			 " (" + exception.toString () + ")"));
	  	exception.printStackTrace ();
		}

		return rendered.toString();
	}


	// ----------------------------------------------------------------------
	// METHOD: renderComponents
	// ----------------------------------------------------------------------
	
	/** renderComponents() overrides A_Renderer.renderComponents() because we need
	  * to take a slightly different approach to rendering all the components.
	  * Since renderSeparatedComponent() prepends the separator instead of
	  * appending it, we want to call it for every component, not just some.
	  *
	  */

	protected String renderComponents(MContainer parent, String separator)
	{
		StringBuffer buffer = new StringBuffer();

		MComponent[] components = parent.getComponents();

  	if (components.length > 0) 
  	{
    	for (int i = 0; i < components.length; i++)
    	{
    		buffer.append(renderSeparatedComponent(components[i], separator));
    	}
  	}
  	
  	return buffer.toString();
	}
	
	
	// ----------------------------------------------------------------------
}


// ========================================================================
//                                               (c) 2001 Bitmovers Systems