// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.ResourceArchiveFilter
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import java.io.*;


// ======================================================================
// CLASS: ResourceArchiveFilter
// ======================================================================

/** The ResourceArchiveFilter class ensures that only valid resource
  * archives recognised and used. The list that this filter uses is as
  * follows:
  *
  * <pre>
  *  --------------------------------------------------------------------
  *  EXTENSION          DESCRIPTION
  *  --------------------------------------------------------------------
  *  .zip               ZIP file
  *  .jar               Java ARchive (ZIP format)
  *  --------------------------------------------------------------------
  * </pre>
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class ResourceArchiveFilter implements FilenameFilter
{
  // --------------------------------------------------------------------
  // METHOD: accept
  // --------------------------------------------------------------------

  public boolean accept(File folder, String name)
  {
    name = name.toLowerCase();
    
    if (name.endsWith(".zip"))
    {
      return true;
    }
    else if (name.endsWith(".jar"))
    {
      return true;
    }
    else if (new File (folder, name).isDirectory ())
    {
    	return true;
    }
    else
    {
      return false;
    }
  }


  // --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF