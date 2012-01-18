// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.SystemResourcesAlreadyDefinedException
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import com.bitmovers.maui.*;


// ======================================================================
// CLASS: SystemResourcesAlreadyDefinedException
// ======================================================================

/** The SystemResourcesAlreadyDefinedException is thrown when the
  * setSystemResources() is called more than once.
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class SystemResourcesAlreadyDefinedException extends MauiException
{
  // --------------------------------------------------------------------
  // CONSTRUCTOR: SystemResourcesAlreadyDefinedException
  // --------------------------------------------------------------------

  public SystemResourcesAlreadyDefinedException()
  {
    super("ResourceManager: setSystemResources() has already been called. This method can only be called once.");
  }


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF