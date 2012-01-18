// ======================================================================
// com.bitmovers.maui.components.Paintable
// ======================================================================

package com.bitmovers.maui.components;

import java.awt.Graphics;


// ======================================================================
// INTERFACE: Paintable
// ======================================================================

/** The <code>Paintable</code> interface indicates that implementing components can
  * represent themselves graphically. Components which implement the
  * other XXXPaintable interfaces are required to implement this
  * interface (explicitly or not, as other <code>Paintable</code> interfaces extend
  * this interface), as they must provide a default way of rendering
  * themselves in the event of a requested state which the component
  * does not support. For components which implement the MouseXXPaintable
  * interfaces, the <code>paint()</code> method defined in this interface would
  * typically call the default state:
  *
  * Example:
  *
  * <pre>
  *  public void paint(Graphics graphics)
  *  {
  *    this.paintMouseOut(graphics);
  *  }
  * </pre>
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public interface Paintable
{
  // --------------------------------------------------------------------
  // METHOD: paint
  // --------------------------------------------------------------------

  public void paint(Graphics graphics);


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF