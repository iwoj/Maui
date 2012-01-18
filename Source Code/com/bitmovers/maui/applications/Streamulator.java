// ========================================================================
// com.bitmovers.maui.applications.Streamulator
// ========================================================================

package com.bitmovers.maui.applications;


import java.util.*;
import com.bitmovers.utilities.*;

import com.bitmovers.maui.*;
import com.bitmovers.maui.events.*;
import com.bitmovers.maui.layouts.*;
import com.bitmovers.maui.components.foundation.*;


// ========================================================================
// CLASS: Streamulator
// ========================================================================

public class Streamulator extends MauiApplication
{
  
  
	private int numberOfUsers = 0;
	
  private MTextField streamSizeField = new MTextField();
	private MTextField durationField = new MTextField();
	private MLabel usersField = new MLabel(Integer.toString(numberOfUsers));
	private MLabel resultsField = new MLabel();
	
	
  // --------------------------------------------------------------------
  // CONSTRUCTOR: Streamulator
  // --------------------------------------------------------------------

  public Streamulator(Object aInitializer) 
  {
    super(aInitializer, "Streamulator");
    initializeUI();
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: initializeUI
  // --------------------------------------------------------------------
  
  private void initializeUI()
  {
    MFrame window = new MFrame("Streamulator", 250);
    add(window);
    
    window.add(new MLabel("Streamulator is a bandwidth usage calculator. Fill in the Stream Size and Duration fields and use the \"Stream Users\" tab to set the number of users."));
		
		MTabbedPane tabbedPane = new MTabbedPane();
		window.add(tabbedPane);
		
		
		 //------------\\
		// Calculations \\-----------------------------------------------------
		
		MPanel calculationPanel = new MPanel();
		tabbedPane.add(calculationPanel, "Calculations");
		
		// Stream Size
		MLabel streamSizeLabel = new MLabel("Stream Size (in kb/second):");
		calculationPanel.add(streamSizeLabel);
		calculationPanel.add(streamSizeField);
		
		// Duration
		MLabel durationLabel = new MLabel("Duration (in seconds):");
		calculationPanel.add(durationLabel);
		calculationPanel.add(durationField);
		
		// Users
		MLabel usersLabel = new MLabel("Number of Users:");
		calculationPanel.add(usersLabel);
		calculationPanel.add(usersField);
		
		// OK MButton
		MButton calculateButton = new MButton("Calculate");
    calculateButton.addActionListener(new MActionListener()
    {
			public void actionPerformed(MActionEvent event)
			{
				Streamulator.this.calculate();
			}
		});
    calculationPanel.add(calculateButton);
    
    // Results
		MLabel resultsLabel = new MLabel("Total Traffic:");
		calculationPanel.add(resultsLabel);
		resultsField.setBold(true);
		calculationPanel.add(resultsField);
		
		 //------------\\
		// Stream Users \\-----------------------------------------------------
		
		
		Object[] columnNames = {"Connected?","Name","Email"};
		
		Object[][] tableData = {
			{new MCheckBox(), "Dave", "dave@bitmovers.com"},
			{new MCheckBox(), "Ian", "i@woj.com"},
			{new MCheckBox(), "Mike", "email@michaelwood.com"},
			{new MCheckBox(), "Patrick", "email@patrickg.com"},
			{new MCheckBox(), "Salma", "salma@bitmovers.com"},
			{new MCheckBox(), "Sean", "sko@bitmovers.com"}
		};
		
		// Define checkbox listener
		MActionListener userCheckboxListener = new MActionListener()
		{
			public void actionPerformed(MActionEvent event)
			{
				if (event.getActionCommand().equals("checked"))
				{
					numberOfUsers++;
				}
				else if (event.getActionCommand().equals("unchecked"))
				{
					numberOfUsers--;
				}
				Streamulator.this.calculate();
			}
		};
		
		// Add checkbox listeners
		for (int i = 0; i < tableData.length; i++)
		{
			((MCheckBox)tableData[i][0]).addActionListener(userCheckboxListener);
		}
		
		MTable table = new MTable(tableData, columnNames);
		
		tabbedPane.add(table, "Stream Users");
  }
  
  
  // --------------------------------------------------------------------
  // METHOD: calculate
  // --------------------------------------------------------------------
  
  private void calculate()
  {
  	double streamSize = 0;
    try
    {
    	streamSize = Double.parseDouble((String)streamSizeField.getValue());
    }
    catch (NumberFormatException e)
    {
    	streamSizeField.setValue("0");
    }
    
    double duration = 0;
    try
    {
    	duration = Double.parseDouble((String)durationField.getValue());
    }
    catch (NumberFormatException e)
    {
    	durationField.setValue("0");
    }
    
    usersField.setText(Integer.toString(numberOfUsers));
    
		resultsField.setText(Double.toString((streamSize * 1024) * duration * numberOfUsers / 1024 / 1024 / 8) + " megabytes");
  }
	
	
}


// ======================================================================
// (c) 2001 Bitmovers Software, Inc.                                  eof