// =============================================================================
// com.bitmovers.maui.components.foundation.MContainer
// =============================================================================

package com.bitmovers.maui.components.foundation;

import com.bitmovers.utilities.*;
import com.bitmovers.maui.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.logmanager.*;
import com.bitmovers.maui.engine.wmlcompositor.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.components.foundation.*;
import com.bitmovers.maui.layouts.*;
import java.util.*;


// ========================================================================
// CLASS: MContainer                             (c) 2001 Bitmovers Systems
// ========================================================================

/** This class is a special Maui Component designed to hold other Components. 
  * As with the Component class, this class is not intended to be used directly,
  * but extended by actual component classes.
  * 
  */
  
public abstract class MContainer extends MComponent
{
	
	
	protected MLayout layoutManager;
	protected boolean dirty = false;

	private Vector components = new Vector();
	private Hashtable constraintsHash = new Hashtable();
	
	
	// ----------------------------------------------------------------------
	// METHOD: getComponentCount
	// ----------------------------------------------------------------------

  /** Returns the number of components in this panel.
    * 
    * @return The number of components in this panel.
    *
    */
	
	public int getComponentCount() 
	{
		return this.components.size();
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getComponent
	// ----------------------------------------------------------------------
	
  /** Returns the nth component in this container.
    * 
    * @param n The index of the component to get.
    * @return the n<sup>th</sup> component in this container.
    * @exception ArrayIndexOutOfBoundsException  
    *                 if the n<sup>th</sup> value does not exist.
    */

	public MComponent getComponent(int index)
  {
		try
		{
			return (MComponent)this.components.elementAt(index);
		}
		catch (Exception exception) // ArrayIndexOutOfBoundsException
		{
			throw new ArrayIndexOutOfBoundsException("MContainer.getComponent(int): No MComponent exists at the index specified (" + index + ").");
		}
  }
	

	// ----------------------------------------------------------------------
	// METHOD: getComponents
	// ----------------------------------------------------------------------

  /** Returns all the components in this container.
    * 
    * @return An array of all the components in this container.
    *
    */

	public MComponent[] getComponents() 
	{
		MComponent[] list = new MComponent[this.components.size()];
		
		for (int i = 0; i < this.components.size(); i++)
		{
			list[i] = (MComponent)this.components.elementAt(i);
		}
		
		return list;
  }
    

	// ----------------------------------------------------------------------
	// METHOD: getComponentIndex
	// ----------------------------------------------------------------------

  /** Returns an int representing the index position of the given component 
    * within this container. Returns -1 if the component does not exist within
    * this container.
    * 
    * @return A postive index position of the given component within this container. 
    *	        Returns -1 if the component does not exist within this container.
    *
    */

	public int getComponentIndex(MComponent component)
  {
		return this.components.indexOf(component);
	}


	// ----------------------------------------------------------------------
	// METHOD: setDirty
	// ----------------------------------------------------------------------

  /** Sets the dirty flag, which is used by <code>WML</code> renderers to detect changes in
	  * the component heirarchy.
	  *
	  * @param dirty  <code>true</code> to indicate changes in component hierarachy. <code>false</code> otherwise.
    * 
    * @invisible
    * 
    */
	
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}


	// ----------------------------------------------------------------------
	// METHOD: isDirty
	// ----------------------------------------------------------------------
	
  /** Checks to detect changes in the component heirarchy, which is used by 
  	* <code>WML</code> renderers .
	  *
	  * @return		<code>true</code> if renderers detected a change. <code>false</code> otherwise.
    * @invisible
    * 
    */
	
	public boolean isDirty()
	{
		return this.dirty;
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: add
	// ----------------------------------------------------------------------
	
  /** Adds the specified component to the end of this container.
    * 
    * @param comp The component to be added.
    * @return The component argument.
    * 
    */

	public MComponent add(MComponent comp)
	{
		this.addImpl(comp, null, -1);

		return comp;
	}


	// ----------------------------------------------------------------------
	// METHOD: add
	// ----------------------------------------------------------------------

	/** Adds the specified component to this container at the given 
	  * position. 
	  * 
	  * @param comp The component to be added.
	  * @param index The position at which to insert the component, 
	  *              or <code>-1</code> to insert the component at the end.
	  * @return The component <code>comp</code>
	  *
	  */

	public MComponent add(MComponent component, int index)
	{
		this.addImpl(component, null, index);

		return component;
	}


	// ----------------------------------------------------------------------
	// METHOD: add
	// ----------------------------------------------------------------------

	/** Adds the specified component to the end of this container.
	  * Also notifies the layout manager to add the component to 
	  * this container's layout using the specified constraints object.<p>
	  * 
	  * Constraints objects are specific to each layout manager and govern 
	  * settings such as alignment.
	  * 
	  * @param comp The component to be added
	  * @param constraints An object expressing layout contraints for this component.
	  * 
	  */

	public void add(MComponent comp, Object constraints)
	{
		this.addImpl(comp, constraints, -1);
	}


	// ----------------------------------------------------------------------
	// METHOD: add
	// ----------------------------------------------------------------------

	/** Adds the specified component to this container with the specified
	  * constraints at the specified index. Also notifies the layout 
	  * manager to add the component to the this container's layout using 
	  * the specified constraints object.
	  * 
	  
	  * @param comp 				The component to be added
	  * @param constraints 	An object expressing layout contraints for this
	  * @param index 				The position in the container's list at which to insert 
	  *        							the component. -1 means insert at the end.
	  * 
	  * @see #remove
	  */

	public void add(MComponent comp, Object constraints, int index)
	{
		this.addImpl(comp, constraints, index);
	}


	// ----------------------------------------------------------------------
	// METHOD: addImpl
	// ----------------------------------------------------------------------

	/** Temporary leftovers of the old MContainer. This method is poorly named
	  * (bloody Sun programmers!) and I, quite frankly, don't like it. It's too
	  * hard to say what it is actually supposed to do.
	  *	
	  * @deprecated  Use addComponent() instead.
	  *
	  */

	protected void addImpl(MComponent component, Object constraints, int index) 
	{
		this.addComponent(component, constraints, index);
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: addComponent
	// ----------------------------------------------------------------------
	
	/** Adds the specified component to this container at the specified
	  * index. This method also notifies the layout manager to add 
	  * the component to this container's layout using the specified 
	  * constraints object.<p>
	  * 
	  * This is the method to override if a program needs to track 
	  * every add request to a container. An overriding method should 
	  * usually include a call to the superclass's version of the method:<p>
	  * 
	  * <blockquote>
	  *  <code>super.addComponent(comp, constraints, index)</code>
	  * </blockquote><p>
	  * 
	  * @param comp 				The component to be added.
	  * 
	  * @param constraints 	An object expressing layout contraints 
	  *                    for this component.
	  * 
	  * @param index 				The position in the container's list at which to
	  *                    insert the component, where <code>-1</code> 
	  *                    means insert at the end.
	  * 
	  */

	protected void addComponent(MComponent component, Object constraints, int index) 
	{
		// Check for correct arguments:  index in bounds,
		// comp cannot be one of this container's parents,
		// and comp cannot be a screen.
		if (index > this.components.size() || ((index < 0) && (index != -1)))
		{
			throw new IllegalArgumentException("MContainer.addComponent(): The index specified (" + index + ") of which to insert this component was out of range. It must be >= 0 and <= the total number of components.");
		}

		// Make sure the container's parent isn't being added to itself
		if (component instanceof MContainer)
		{
			for (MContainer container = this; container != null; container=container.parent)
			{
				if (container == component) 
				{
					throw new IllegalArgumentException("MContainer.addComponent(): Cannot add an MContainer's parent to itself.");
				}
			}
		}
		
		// Make sure a MauiApplication is not being added
		if (component instanceof MauiApplication) 
		{
			throw new IllegalArgumentException("MContainer.addComponent(): Cannot add a MauiApplication to an MContainer.");
		}
				
		// If the component had a previous parent, remove it, as this
		// is the new parent.
		if (component.getParent() != null)
		{
			component.getParent().remove(component);
		}

		// Add the component to this containers storage medium
		{
			if (index == -1)
			{
				this.components.addElement(component);
			}
			else
			{
				try
				{
					this.components.insertElementAt(component, index);
				}
				catch (ArrayIndexOutOfBoundsException exception)
				{
					System.err.println(new WarningString("MContainer.addComponent(): Could not add component at index '" + index + "' because it is greater than the number of components in this MContainer. Adding to the end instead..."));
					this.components.addElement(component);
				}
			}

			// Package the constraints as well (if there are any)
			if (constraints != null)
			{
				this.constraintsHash.put(component, constraints);
			}
		}
		
		// We are now the component's parent...
		component.setParent(this);
		this.setDirty(true);

		if (this.valid && this.layoutManager != null)
		{
			this.layoutManager.invalidate();
		}
	}
	
  
	// ----------------------------------------------------------------------
	// METHOD: remove
	// ----------------------------------------------------------------------
	
	/** Removes the component specified by <code>index</code>, 
    * from this container. 
    * 
    * @param index The index of the component to be removed.
    */
     
	public void remove(int index) 
	{
		try
		{
			MComponent component = (MComponent)this.components.elementAt(index);
			
			if (component != null)
			{
				component.resetRootParent();

				this.setDirty(true);

				if (valid)
				{
					this.invalidate();
				}
			}
			
			MComponent theComponent = (MComponent) components.elementAt (index);
			theComponent.resetServletURL ();
			this.components.removeElementAt(index);
			this.constraintsHash.remove(component);
	  }
	  catch (ArrayIndexOutOfBoundsException exception)
	  {
	  	System.out.println(new DebugString("MContainer.remove(int): Could not remove MComponent at index " + index + " because none exist at that location."));
	  }
	}
	

	// ----------------------------------------------------------------------
	// METHOD: remove
	// ----------------------------------------------------------------------

  /** Removes the specified component from this container.
    * 
    * @param comp The component to be removed.
    *
    */
		
	public void remove(MComponent component)
	{
		if (component.getParent() == this)
		{
			int index = this.components.indexOf(component);
			
			if (index != -1)
			{
				this.remove(index);
			}
			else
			{
		  	System.err.println(new InfoString("MContainer.remove(MComponent): Could not remove the passed MComponent because it does not exist in this MContainer."));
			}
		}
	}
    
	
	// ----------------------------------------------------------------------
	// METHOD: removeAll
	// ----------------------------------------------------------------------
	
  /** Removes all The components from this container.
    *
    */

  public void removeAll()
  {
  	Enumeration theComponents = components.elements ();
  	while (theComponents.hasMoreElements ())
  	{
  		((MComponent) theComponents.nextElement ()).resetServletURL ();
  	}
		this.components.removeAllElements();
		this.constraintsHash.clear();

		this.setDirty(true);

		if (valid)
		{
			this.invalidate();
		}
  }
    
	
	// ----------------------------------------------------------------------
	// METHOD: getLayout
	// ----------------------------------------------------------------------
	
	/** Returns the layout manager for this container. 
	  *
	  * @return The layout manager for this container.
	  */

	public MLayout getLayout()
	{
		return this.layoutManager;
	}
    
        
	// ----------------------------------------------------------------------
	// METHOD: setLayout
	// ----------------------------------------------------------------------

	/** Sets the layout manager for this container. 
	  *
	  * @param mgr The specified layout manager.
	  *
	  */
	
  public void setLayout(MLayout layoutManager)
  {
  	// If the given object is not the current layout manager.
  	//if (this.layoutManager != layoutManager)
  	{
	  	// If the given object is a different class than the current layout manager.
	  	if (this.layoutManager != null && !this.layoutManager.getClass().equals(layoutManager.getClass()))
	  	{
				this.constraintsHash.clear();
				System.out.println(new DebugString("MContainer.setLayout(): All layout constraints set to default."));
			}
			
			this.layoutManager = layoutManager;
			layoutManager.setParentContainer(this);
			
			if (valid)
			{
				this.invalidate();
			}
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: getConstraints
	// ----------------------------------------------------------------------
	
	/** Returns the constraints object for a given component. This
	  * method returns <code>null</code> value if the object has no constraints.
	  *
	  * <p>
	  * <pre>
	  * Example:
	  * <p>
	  *
	  * </pre>
	  *
	  *	@param component A component whos contraints is returned.
	  *
	  * @return constraints Object for </code>component</code> input.
	  *
	  * 
	  */
	
	public Object getConstraints(MComponent component)
	{
		return this.constraintsHash.get(component);
	}


	// ----------------------------------------------------------------------
	// METHOD: setConstraints
	// ----------------------------------------------------------------------
	
	/** Sets the constraints object for a given component. A
	  * <code>NoSuchComponentException</code> will be thrown if the component specified is not
	  * contained within this <code>MContainer</code>.
	  *
	  * @param component A component whos contraints is set.
	  *
	  * @param constraints Object constraints that will be set.
	  *
	  *	@exception NoSuchComponentException Specified component not found.
	  *
	  */
	
	public void setConstraints(MComponent component, Object constraints) throws NoSuchComponentException
	{
		try
		{
			if (this.components.contains(component))
			{
				this.constraintsHash.put(component, constraints);
			}
			else
			{
				throw new NoSuchComponentException("MContainer.setConstraints(): The MComponent specified does not exist in this MContainer.");
			}
		}
		catch (NullPointerException exception)
		{
			System.err.println(new WarningString("MContainer.setConstraints(MComponent, Object): Received a null constraints object."));
		}
	}


	// ----------------------------------------------------------------------
	// METHOD: invalidate
	// ----------------------------------------------------------------------
    
	/** Invalidates the container.  The container and all parents
	  * above it are marked as needing to be laid out.  This method can
	  * be called often, so it needs to execute quickly.
	  *
	  * @invisible
	  * 
	  */

	public void invalidate()
	{
		super.invalidate();

		if (this.layoutManager != null)
		{
			layoutManager.invalidate();
		}
	}
	
	
	// ----------------------------------------------------------------------
	// METHOD: validate
	// ----------------------------------------------------------------------
	
	/** Validates this container and all of its subcomponents.
	  * 
	  * @invisible
	  * 
	  */
	/*
	public void validate()
	{
		if (!valid)
		{
			this.validateTree();
			valid = true;
		}
	}
	*/
	
	// ----------------------------------------------------------------------
	// METHOD: validateTree
	// ----------------------------------------------------------------------
	
	/** Recursively descends the container tree and recomputes the
	  * layout for any subtrees marked as needing it (those marked as
	  * invalid).  Synchronization should be provided by the method
	  * that calls this one:  <code>validate</code>.
	  *
	  * @invisible
	  * 
	  */
	/*
	protected void validateTree()
	{
		if (!super.valid)
		{
			super.doLayout();

			for (int i = 0 ; i < this.components.size() ; i++)
			{
				MComponent component = (MComponent)this.components.elementAt(i);

				if ((component instanceof MContainer) && !component.isValid())
				{
					((MContainer)component).validateTree();
				}
				else
				{
					component.validate();
				}
			}
		}

		super.valid = true;
	}
	*/
	
	
	// ----------------------------------------------------------------------
	// METHOD: exiting
	// ----------------------------------------------------------------------
	
	/** Notifys contained <code>MComponent</code>s that it is exiting.
	  * 
	  * @invisible
	  * 
	  */
	
	public final void exiting ()
	{
		super.exiting ();
		MComponent [] theComponents = getComponents ();
		for (int i = 0; i < theComponents.length; i++)
		{
			theComponents [i].exiting ();
		}
	}
  
	
	// ----------------------------------------------------------------------
	// METHOD: setVisible
	// ----------------------------------------------------------------------
	
	/** Overrides <code>MComponent</code>'s <code>setVisible()</code> to 
	  * propagate visibility changes down through all child components.
 	  *
	  * @param b If <code>true</code>, shows this container and all children; 
	  *           otherwise, hides this container and all children.
	  *
	  */
      
	public void setVisible(boolean b) 
	{
		super.setVisible(b);
		setChildrenVisible(b);
	}
  
	
	// ----------------------------------------------------------------------
	// METHOD: setChildrenVisible
	// ----------------------------------------------------------------------
	  
	/** Sets the visibility of all child components to the given value.
	  * 
	  * @param b <code>true</code> to set child components as visible, <code>false</code> otherwise.
	  */
  
	public void setChildrenVisible(boolean b) 
	{
		MComponent[] theComponents = getComponents();
		for (int i = 0; i < theComponents.length; i++)
		{
			theComponents[i].setVisible(b);
		}
	}
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF