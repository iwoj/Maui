// =============================================================================
// com.bitmovers.maui.engine.servlet.NoServletSupport
// =============================================================================

package com.bitmovers.maui.engine.servlet;
import com.bitmovers.maui.MauiApplication;
import com.bitmovers.maui.components.foundation.MFrame;
import com.bitmovers.maui.components.foundation.MLabel;

/** This MauiApplication will be loaded if an attempt is made to access
  * Maui without the correct license level
  */
public class NoServletSupport extends MauiApplication
	implements Runnable
{
	private MFrame frame = new MFrame ("No Servlet Support");
	public NoServletSupport (Object aInitializer)
	{
		super (aInitializer, "No Servlet Support");
		frame.add (new MLabel ("License level does not allow servlet support"));
		add (frame);
		
		new Thread (this).start ();
	}
	
	public void run ()
	{
		try
		{
			Thread.sleep (1000);
			exitAll ();
		}
		catch (InterruptedException e)
		{
		}
	}
}
		