// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.DuplicateResourceArchiveException
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import com.bitmovers.maui.*;


// ======================================================================
// CLASS: DuplicateResourceArchiveException
// ======================================================================

/** The DuplicateResourceFileException is thrown when a file is passed to
  * the addResource() method and the file has already been processed
  * before.
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class DuplicateResourceArchiveException extends MauiException
{
  // --------------------------------------------------------------------
  // CONSTRUCTOR: DuplicateResourceArchiveException
  // --------------------------------------------------------------------

  public DuplicateResourceArchiveException()
  {
    super("ResourceManager: Duplicate resource archive.");
  }


  // --------------------------------------------------------------------
  // CONSTRUCTOR: DuplicateResourceArchiveException
  // --------------------------------------------------------------------

  public DuplicateResourceArchiveException(String message)
  {
    super(message);
  }


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF