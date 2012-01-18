// ======================================================================
// com.bitmovers.maui.components.MouseOverPaintable
// ======================================================================

package com.bitmovers.maui.components;

import java.awt.Graphics;


// ======================================================================
// INTERFACE: MouseOverPaintable
// ======================================================================

/** The <code>MouseOverPaintable</code> interface indicates that implementing
  * components can represent themselves graphically in a mouse-over state
  * (that is, the mouse is over the component).
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public interface MouseOverPaintable extends Paintable
{
  // --------------------------------------------------------------------
  public static final String STATE_NAME = "over";


  // --------------------------------------------------------------------
  // METHOD: paintMouseOver
  // --------------------------------------------------------------------

  public void paintMouseOver(Graphics graphics);


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF