// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.InvalidResourceArchiveException
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import com.bitmovers.maui.*;


// ======================================================================
// CLASS: InvalidResourceArchiveException
// ======================================================================

/** The InvalidResourceArchiveException is thrown when a file is passed to
  * the addResource() method and the file is invalid for some reason
  * (ie. is not a ZIP file).
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class InvalidResourceArchiveException extends MauiException
{
  // --------------------------------------------------------------------
  // CONSTRUCTOR: InvalidResourceArchiveException
  // --------------------------------------------------------------------

  public InvalidResourceArchiveException()
  {
    super("ResourceManager: Resource files must be in the ZIP format (ie. .jar or .zip).");
  }


  // --------------------------------------------------------------------
  // CONSTRUCTOR: InvalidResourceArchiveException
  // --------------------------------------------------------------------

  public InvalidResourceArchiveException(String message)
  {
    super(message);
  }


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF