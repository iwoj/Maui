// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.ResourceNotFoundException
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import com.bitmovers.maui.*;


// ======================================================================
// CLASS: ResourceNotFoundException
// ======================================================================

/** The ResourceNotFoundException is thrown when the getResourceXXX()
  * methods of the ResourceManager couldn't locate the requested file.
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class ResourceNotFoundException extends MauiException
{
  // --------------------------------------------------------------------
  // CONSTRUCTOR: ResourceNotFoundException
  // --------------------------------------------------------------------

  public ResourceNotFoundException()
  {
    super("The ResourceManager could not locate the requested file.");
  }


  // --------------------------------------------------------------------
  // CONSTRUCTOR: ResourceNotFoundException
  // --------------------------------------------------------------------

  public ResourceNotFoundException(String message)
  {
    super(message);
  }


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF