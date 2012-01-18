// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.ResourceFileFilter
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import java.io.*;


// ======================================================================
// CLASS: ResourceFileFilter
// ======================================================================

/** The ResourceFileFilter class ensures that only valid resources are
  * recognised and used. The list that this filter uses is as follows:
  *
  * <pre>
  *  --------------------------------------------------------------------
  *  EXTENSION          DESCRIPTION
  *  --------------------------------------------------------------------
  *  .html              HTML files
  *  .wml               WML files
  *  .css               Cascading Style Sheets
  *  .gif               Compuserve GIF files
  *  .jpg               JPEG graphic files
  *  --------------------------------------------------------------------
  * </pre>
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class ResourceFileFilter implements FilenameFilter
{
  // --------------------------------------------------------------------
  // METHOD: accept
  // --------------------------------------------------------------------

  public boolean accept(File folder, String name)
  {
    name = name.toLowerCase();
    
    if (name.endsWith(".html"))
    {
      return true;
    }
    else if (name.endsWith(".wml"))
    {
      return true;
    }
    else if (name.endsWith(".css"))
    {
      return true;
    }
    else if (name.endsWith(".gif"))
    {
      return true;
    }
    else if (name.endsWith(".jpg"))
    {
      return true;
    }
    else if (name.endsWith(".txt"))
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