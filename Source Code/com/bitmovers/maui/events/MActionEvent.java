package com.bitmovers.maui.events;

import java.io.*;
import java.net.*;
import java.util.*;


// ========================================================================
// CLASS: MActionEvent                           (c) 2001 Bitmovers Systems
// ========================================================================

/** This is the only event class currently defined in Maui. When a user
  * performs an action upon a component which may require attention, that 
  * component publishes a new <code>MActionEvent</code> to any 
  * listener objects. (Listeners register themselves using the
  * <code>MComponent.addActionListener()</code> method.) 
  * 
  * @see <code>MActionListener</code>, 
  *      <code>MComponent.addActionListener()</code>
  * 
  */

public class MActionEvent extends MauiEvent
{
	
	
	/** This is the action command used by <code>MSettable</code>s. 
	  *
	  */
	  
	public static final String VALUE_CHANGED = "value_changed";
	
	
	/** This is an action command used by <code>MCheckbox</code>es. 
	  *
	  */
	  
	public static final String ACTION_CHECKED = "checked";
	
	
	/** This is an action command used by <code>MCheckbox</code>es. 
	  *
	  */
	  
	public static final String ACTION_UNCHECKED = "unchecked";
	
	
	/** This is an action command used by <code>MSelectList</code>s. 
	  *
	  */
	
	public static final String ACTION_SELECTED = "selected";
	
	
	/** This is an action command used by <code>MTabbedPane</code>s. 
	  *
	  */
	  
	public static final String ACTION_TABSELECTED = "tabSelected";
	
	
	/** This is an action command used by <code>MExpandPane</code>s. 
	  *
	  */
	  
	public static final String ACTION_OPENED = "opened";
	
	
	/** This is an action command used by <code>MExpandPane</code>s. 
	  *
	  */
	  
	public static final String ACTION_CLOSED = "closed";
	
	
	/** This is an action command used by <code>MButton</code>s. 
	  * and <code>MMenuItem</code>s.
	  *
	  */
	
	public static final String ACTION_CLICKED = "clicked";
	
	
	/** Currently unused.
	  *
	  */
	
	public static final String ACTION_BACKOUT = "backout";
	
	
	/** This is an action command used by <code>MFrame</code> in its
	  * WML context. 
	  *
	  * @invisible
	  *
	  */
	  
	public static final String ACTION_POP = "pop";
	public static final String ACTION_PUSH = "push";
	
	
	private String command;
	
	
	// ----------------------------------------------------------------------
	// CONSTRUCTOR: MActionEvent
	// ----------------------------------------------------------------------
	
	/** Constructs a new <code>MActionEvent</code> object.
	  * 
	  * @param  source   a reference to the component which generated the 
	  *                  event. Useful for callbacks.
	  * 
	  * @param  command  provides additional details regarding the event. All
	  *                  possible values are defined as static constants in 
	  *                  this class.
	  *
	  */
	  
	public MActionEvent(Object source, String command)
	{
	  super(source);
	  this.command = command;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getActionCommand
	// ----------------------------------------------------------------------

	/** @return  the command string associated with this action.
	  *
	  */
	  
	public String getActionCommand()
	{
	  return this.command;
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF