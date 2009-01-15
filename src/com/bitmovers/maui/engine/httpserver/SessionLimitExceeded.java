// =============================================================================
// com.bitmovers.maui.engine.httpserver.SessionLimitExceeded
// =============================================================================

package com.bitmovers.maui.engine.httpserver;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.engine.AuthorizationManager;
import com.bitmovers.maui.components.foundation.MFrame;
import com.bitmovers.maui.components.foundation.MLabel;
import com.bitmovers.maui.components.foundation.MButton;
import com.bitmovers.maui.components.foundation.MDivider;
import com.bitmovers.maui.events.MActionEvent;
import com.bitmovers.maui.events.MActionListener;

/** This MauiApplication will be loaded if an attempt is made to access
  * Maui without the correct license level
  */
public class SessionLimitExceeded extends MauiApplication
{
	private MFrame frame = new MFrame ("Session Limit Exceeded");
	public SessionLimitExceeded (Object aInitializer)
	{
		super (aInitializer, "Session Limit Exceeded");
		AuthorizationManager theAuth = AuthorizationManager.getInstance ();
		MLabel theLabel =
			new MLabel ("License level does not allow more than " +
						theAuth.getAuthorizationValue (theAuth.AUTHORIZATION_SESSIONS).
							toString () + " sessions");
		frame.add (theLabel);
		MButton theButton = new MButton ("OK");
		theButton.setLink ("http://maui.bitmovers.com");
		theButton.addActionListener (new MActionListener ()
			{
				public void actionPerformed (MActionEvent aEvent)
				{
					exitAll ();
				}
			});
		frame.add (new MDivider ());
		frame.add (theButton);
		add (frame);
	}
}
		