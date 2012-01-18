// ======================================================================
// com.bitmovers.maui.applications.TestApplication
// ======================================================================

package com.bitmovers.maui.applications;

import java.util.*;
import com.bitmovers.utilities.*;
import java.awt.Dimension;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.constraints.MInputDescriptor;
import com.bitmovers.maui.engine.constraints.MConstraint;
import com.bitmovers.maui.engine.constraints.MDescriptor;
import com.bitmovers.maui.engine.constraints.MMatchAllDescriptor;
import com.bitmovers.maui.engine.resourcemanager.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.components.foundation.*;


// ======================================================================
// CLASS: TestApplication
// ======================================================================

public class TestApplication extends MauiApplication implements MActionListener
{
  // --------------------------------------------------------------------
	
  private MFrame window;
  
  private MTextArea eventLogTextArea;
  private MImage image;
  private MRadioButtonGroup radioButtonGroup;
  private MSelectList selectList;
  private MTable table;
  private MLayout currentLayout;
  private MContainer currentContainer;
	
  // --------------------------------------------------------------------
  // CONSTRUCTOR: TestApplication
  // --------------------------------------------------------------------

  public TestApplication(Object aInitializer) 
  {
    super(aInitializer, "TestApplication");
    
    //SelectListConstraint selectListConstraint = new SelectListConstraint({"dave@bitmovers.com","ian@bitmovers.com"}, );
		//TextFieldConstraint textFieldConstraint = new TextFieldConstraint(new MMatchAllDescriptor());
		
		MInputDescriptor hasKeyboard = new MInputDescriptor();
		hasKeyboard.setHasKeyboard(true);
		TextFieldConstraint textFieldConstraint = new TextFieldConstraint(hasKeyboard);
		
		MInputDescriptor hasNoKeyboard = new MInputDescriptor();
		hasNoKeyboard.setHasKeyboard(true);
		SelectListConstraint selectListConstraint = new SelectListConstraint(hasNoKeyboard);
		
		addConstraint(textFieldConstraint, "recipientField");
		addConstraint(selectListConstraint, "recipientField");
		
    initializeUI();
    addActionListener(this);
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: initializeUI
  // --------------------------------------------------------------------
  
  private void initializeUI()
  {
    window = new MFrame("Maui Test Application", 500);
		window.setLayout(new MBorderLayout());
		
		addMenus();
		
		addEventLog();
		
		window.add(new MDivider(), MBorderLayout.CENTER);
		
		initializeComponents();
		
		addComponents(getNewTabbedPane(), new MBoxLayout());
		
		add(window);
	}
  
  
  // --------------------------------------------------------------------
  // METHOD: getNewTabbedPane
  // --------------------------------------------------------------------
  
  private MTabbedPane getNewTabbedPane()
  {
		MTabbedPane tabbedPane = new MTabbedPane();
		tabbedPane.add(new MPanel(), "Test Components");
		tabbedPane.add(new MPanel(), "Left Intentionally Blank");
		
		return tabbedPane;
	}
	
	
  // --------------------------------------------------------------------
  // METHOD: addComponents
  // --------------------------------------------------------------------
  
  private void addComponents(MContainer container, MLayout newLayout)
  {
  	MPanel panel;
  	window.remove(2);
  	
  	if (container.getComponentCount() == 0)
  	{
  		panel = new MPanel();
  		container.add(panel);
  	}
  	else
  	{
	  	panel = (MPanel)container.getComponent(0);
  	}
  	
  	currentLayout = newLayout;
  	currentContainer = container;
  	
  	panel.setLayout(newLayout);
  	panel.add(image);
  	panel.add(radioButtonGroup.getRadioButtons()[0]);
  	panel.add(radioButtonGroup.getRadioButtons()[1]);
  	panel.add(selectList);
  	panel.add(table);
  	
//  	try
//  	{
	  	MComponent mysteryComponent = (MComponent)getSession().getObjectFactory().createObject(this,"recipientField");
	  	panel.add(mysteryComponent);
/*  	}
  	catch (ClassCastException e)
  	{
  		System.out.println("Couldn't create constraint-based component.");
  		// Do nothing.
  	}
*/  	
  	
  	window.add(container, MBorderLayout.SOUTH);
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: addEventLog
  // --------------------------------------------------------------------
  
  private void addEventLog()
  {
  	MPanel panel = new MPanel();
  	eventLogTextArea = new MTextArea(new Dimension(100, 10));
  	
  	MButton clearButton = new MButton("Clear");
  	clearButton.addActionListener(new MActionListener()
  	{
  		public void actionPerformed(MActionEvent event)
			{
	  		TestApplication.this.eventLogTextArea.setText("");
	  	}
  	});
  	
  	panel.add(new MLabel("Event Log:"));
  	panel.add(eventLogTextArea, MBoxLayout.CENTER);
  	panel.add(clearButton, MBoxLayout.RIGHT);
  	
  	window.add(panel, MBorderLayout.NORTH);
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: initializeComponents
  // --------------------------------------------------------------------
  
  private void initializeComponents()
  {
  	try
  	{
			image = new MImage("com/bitmovers/maui/components/foundation/MButton/characters/100.gif", 40, 40);
		}
		catch (ResourceNotFoundException e)
		{
			System.err.println("Image not found.");
		}
		
		radioButtonGroup = new MRadioButtonGroup("radioGroup");
		radioButtonGroup.addRadioButton("Selection One");
		radioButtonGroup.addRadioButton("Selection Two");
		
		String[] selectListItems = {"Selection One","Selection Two"};
		selectList = new MSelectList(selectListItems);
		
		initializeTable();
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: initializeTable
  // --------------------------------------------------------------------
  
  private void initializeTable()
  {
  	MCheckBox checkbox1 = new MCheckBox();
  	MCheckBox checkbox2 = new MCheckBox();
  	MCheckBox checkbox3 = new MCheckBox();
  	
		Object[][] tableData = {
			{checkbox1, "Ian", "i@woj.com"},
			{checkbox2, "Patrick", "email@patrickg.com"},
			{checkbox3, "Dave", "dave@bitmovers.com"}
		};
		
		Object[] columnNames = {"Select","Name","Email"};
		
		table = new MTable(tableData, columnNames);
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: addMenus
  // --------------------------------------------------------------------
  
  private void addMenus()
  {
		window.setMenuBar(new MMenuBar());
		window.getMenuBar().add(new MMenu("Options"));
		window.getMenuBar().getMenu(0).add(new MMenu("Containers"));
		window.getMenuBar().getMenu(0).getMenu(0).add(new MMenuItem("Expand Pane"));
		window.getMenuBar().getMenu(0).getMenu(0).getMenuItem(0).addActionListener(new MActionListener()
		{
  		public void actionPerformed(MActionEvent event)
			{
				TestApplication.this.addComponents(new MExpandPane("Expand Pane"), TestApplication.this.currentLayout);
			}
		});
		
		window.getMenuBar().getMenu(0).getMenu(0).add(new MMenuItem("Tabbed Pane"));
		window.getMenuBar().getMenu(0).getMenu(0).getMenuItem(1).addActionListener(new MActionListener()
		{
  		public void actionPerformed(MActionEvent event)
			{
				TestApplication.this.addComponents(getNewTabbedPane(), TestApplication.this.currentLayout);
			}
		});
		
		window.getMenuBar().getMenu(0).add(new MMenu("Layouts"));
		window.getMenuBar().getMenu(0).getMenu(1).add(new MMenuItem("Box Layout"));
		window.getMenuBar().getMenu(0).getMenu(1).getMenuItem(0).addActionListener(new MActionListener()
		{
  		public void actionPerformed(MActionEvent event)
			{
				TestApplication.this.addComponents(TestApplication.this.currentContainer, new MBoxLayout());
			}
		});
		
		window.getMenuBar().getMenu(0).getMenu(1).add(new MMenuItem("Flow Layout"));
		window.getMenuBar().getMenu(0).getMenu(1).getMenuItem(1).addActionListener(new MActionListener()
		{
  		public void actionPerformed(MActionEvent event)
			{
				TestApplication.this.addComponents(TestApplication.this.currentContainer, new MFlowLayout());
			}
		});
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: actionPerformed
  // --------------------------------------------------------------------
  
  public void actionPerformed(MActionEvent event)
	{
		try
		{
			// Disregard change events from the log window, so we 
			// don't go loopy.
			if (!event.getSource().equals(eventLogTextArea) &&
			    !Class.forName("com.bitmovers.maui.components.foundation.MMenuItem").isInstance(event.getSource()))
			{
				event.consume();
				recordEvent(event);
			}
		}
		catch (ClassNotFoundException e)
		{
			// No big deal. Do nothing.
		}
	}
  
  // --------------------------------------------------------------------
  // METHOD: recordEvent
  // --------------------------------------------------------------------
  
  private void recordEvent(MActionEvent event)
  {
		eventLogTextArea.append(event.getSource().getClass().getName() + ": " + event.getActionCommand() + "\n");
  }


	// =============================================================================
	// INNER CLASS: TextFieldConstraint
	// =============================================================================
	  
	public class TextFieldConstraint
	     extends MConstraint
	{
		// ---------------------------------------------------------------------------
		String defaultValue;
		
		public TextFieldConstraint(MDescriptor descriptor)
		{
			this("", descriptor);
		}
		
		public TextFieldConstraint(String defaultValue, MDescriptor descriptor)
		{
			super(descriptor);
			this.defaultValue = defaultValue;
		}
		
		public Object createObject()
		{
			return new MTextField(20, defaultValue);
		}
	}


	// =============================================================================
	// INNER CLASS: SelectListConstraint
	// =============================================================================
	  
	public class SelectListConstraint
	     extends MConstraint
	{
		// ---------------------------------------------------------------------------
		String[] options = {"One", "Two", "Three"};
		
		public SelectListConstraint(MDescriptor descriptor)
		{
			this(null, descriptor);
		}
		
		public SelectListConstraint(String[] options, MDescriptor descriptor)
		{
			super(descriptor);
			
			if (options != null)
			{
				this.options = options;
			}
		}
		
		public Object createObject()
		{
			return new MSelectList(options);
		}
		
		// ---------------------------------------------------------------------------
	}


  // --------------------------------------------------------------------
}


// ======================================================================
// Copyright © 2000 Bitmovers Software, Inc.                          eof