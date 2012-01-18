// =============================================================================
// com.bitmovers.maui.engine.Compositor
// =============================================================================

package com.bitmovers.maui.engine;

import java.util.*;


// =============================================================================
// <<INTERFACE>> Compositor
// =============================================================================

public interface Compositor
{
	// ---------------------------------------------------------------------------

  public void doComposition(Hashtable parameters);
  
  /**
  * Get the base content type for this compositor
  *
  * @return A String description of the base content type
  */
  public String getBaseContentType ();
  
  /**
  * Generate an exception message
  *
  * @param aException The Exception object
  *
  * @return A representation of the exception which is appropriate for the client
  * 			  type
  */
  public String generateExceptionMessage (Exception aException);
	

	// ---------------------------------------------------------------------------

}

// =============================================================================
// Copyright © 2000 Bitmovers Software Inc.                                  eof