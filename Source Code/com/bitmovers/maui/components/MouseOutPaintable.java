// ======================================================================
// com.bitmovers.maui.components.MouseOutPaintable
// ======================================================================

package com.bitmovers.maui.components;

import java.awt.Graphics;


// ======================================================================
// INTERFACE: MouseOutPaintable
// ======================================================================

/** The <code>MouseOutPaintable</code> interface indicates that implementing components can
  * represent themselves graphically in a mouse-out state (that is, the
  * mouse is not over component).
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public interface MouseOutPaintable extends Paintable
{
  // --------------------------------------------------------------------
  public static final String STATE_NAME = "out";


  // --------------------------------------------------------------------
  // METHOD: paintMouseOut
  // --------------------------------------------------------------------

  public void paintMouseOut(Graphics graphics);


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF