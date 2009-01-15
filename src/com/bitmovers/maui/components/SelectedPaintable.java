// ======================================================================
// com.bitmovers.maui.components.SelectedPaintable
// ======================================================================

package com.bitmovers.maui.components;

import java.awt.Graphics;


// ======================================================================
// INTERFACE: SelectedPaintable
// ======================================================================

/** The <code>SelectedPaintable</code> interface indicates that implementing components can
  * represent themselves graphically in a selected state (that is, they
  * have no mouse states, but are not disabled).
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public interface SelectedPaintable extends Paintable
{
  // --------------------------------------------------------------------
  public static final String STATE_NAME = "selected";


  // --------------------------------------------------------------------
  // METHOD: paintSelected
  // --------------------------------------------------------------------

  public void paintSelected(Graphics graphics);


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF