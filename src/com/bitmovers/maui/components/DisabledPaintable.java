// ======================================================================
// com.bitmovers.maui.components.DisabledPaintable
// ======================================================================

package com.bitmovers.maui.components;

import java.awt.Graphics;


// ======================================================================
// INTERFACE: DisabledPaintable
// ======================================================================

/** The <code>DisabledPaintable</code> interface indicates that implementing
  * components represent themselves graphically in a disabled state (that
  * is, the component is not clickable and should be greyed out).
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public interface DisabledPaintable extends Paintable
{
  // --------------------------------------------------------------------
  public static final String STATE_NAME = "off";


  // --------------------------------------------------------------------
  // METHOD: paintDisabled
  // --------------------------------------------------------------------

  public void paintDisabled(Graphics graphics);


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF