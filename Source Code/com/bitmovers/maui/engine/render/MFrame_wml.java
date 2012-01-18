// ========================================================================
// com.bitmovers.maui.engine.render.MFrame_wml
// ========================================================================

package com.bitmovers.maui.engine.render;
import java.util.Stack;
import java.util.Vector;
import java.util.StringTokenizer;

import com.bitmovers.utilities.StringParser;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.foundation.MContainer;
import com.bitmovers.maui.components.foundation.HasPostValue;
import com.bitmovers.maui.components.foundation.MMenuBar;
import com.bitmovers.maui.components.foundation.MFrame;
import com.bitmovers.maui.components.foundation.MSettable;
import com.bitmovers.maui.components.foundation.MRadioButton;
import com.bitmovers.maui.events.AlwaysNotify;
import com.bitmovers.maui.components.MComponent;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;
import com.bitmovers.maui.engine.httpserver.HTTPSession;

// ========================================================================
// CLASS: MFrame_wml
// ========================================================================

/** This is the wml renderer for MFrame objects.  It also handles "deep 
  * navigation", which is accomplished by listening to events published 
  * from objects which implement the "I_HasDepth" interface.  When such a 
  * component publishes an event this class sets the initial render point 
  * to be the component which published the event... a bit like zooming in 
  * on that component.
  * 
  * @invisible
  * 
  */

public class MFrame_wml extends A_Renderer
                     implements I_Generator,
                                I_RendererInitialize,
                                I_RendererListener,
                                I_FrameRenderer,
                                AlwaysNotify,
                                MActionListener
{
	private static final int PHASE_FORWARD = 0;
	private static final int PHASE_BACKWARD = 1;
	private static final int PHASE_POST = 2;
	
	private MauiApplication mauiApplication = null;
	private HTTPSession session = null;
	private HasPostValue [] valuePosters = null;
}
