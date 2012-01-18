// =============================================================================
// com.bitmovers.maui.httpserver.HTTPServer
// =============================================================================

package com.bitmovers.maui.engine.httpserver;

import java.net.InetAddress;
import javax.net.*;
import javax.net.ssl.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Box;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Random;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.sun.net.ssl.*;

import com.bitmovers.maui.engine.ServerConfigurationManager;
import com.bitmovers.maui.MauiRuntimeWindow;
import com.bitmovers.maui.MauiRuntimeEngine;
import com.bitmovers.maui.engine.logmanager.LogManager;
import com.bitmovers.maui.engine.logmanager.InfoString;

// =============================================================================
// <<SINGLETON>> CLASS: HTTPSecureServer 
// =============================================================================

/** 
  * HTTPSecureServer SINGLETON <p>
  * This is the object which creates the SSL ServerSocket, and establishes connections with
  * clients via HTTPS.  It is designed to perform only the SSL Socket connection activity so that
  * it can easily be replaced without affecting any other part of Maui
  *
  * @invisible
  */

public class HTTPSecureServer extends HTTPServer
{
	private static HTTPSecureServer instance = new HTTPSecureServer ();
	
	private HTTPSecureServer ()
	{
		portPropertyName = ServerConfigurationManager.MAUI_SECURE_PORT;
		realClassName = "HTTPSecureServer";
	}
	
	protected String promptForPassPhrase ()
	{
		String retVal = null;
		if (MauiRuntimeEngine.windowingEnvironmentAvailable)
		{
			MauiRuntimeWindow theWindow = MauiRuntimeWindow.getInstance ();
			final Dialog theDialog = new Dialog (theWindow.window, "Passphrase", true);
			theDialog.setSize (200,30);
			theDialog.setLocation (30, 30);
			Insets theInsets = theDialog.getInsets ();
			theInsets.top = 5;
			theInsets.bottom = 5;
			theInsets.left = 5;
			theInsets.right = 5;
			theDialog.setResizable (false);
			TextField thePassphrase = new TextField (30);
			thePassphrase.setSize (200, 30);
			thePassphrase.setEchoChar ('*');
			theDialog.add (thePassphrase);
			thePassphrase.addActionListener (new ActionListener ()
				{
					public void actionPerformed (ActionEvent aEvent)
					{
						theDialog.setVisible (false);
					}
				});
			theDialog.pack ();
			theDialog.show ();
			retVal = thePassphrase.getText ();
			theDialog.dispose ();
		}
		else
		{
			LogManager.getOriginalOut ().print ("Passphrase: ");
			StringBuffer theInput = new StringBuffer ();
			int theChar;
			try
			{
				while ((theChar = System.in.read ()) != '\n' &&
						theChar != '\r' &&
						theChar != -1)
				{
					theInput.append (theChar);
				}
			}
			catch (IOException e)
			{
			}
			retVal = theInput.toString ();
		}
		return retVal;
	}
	
	private InputStream getCertificateStream (String aCertFile)
		throws IOException
	{
		InputStream retVal = new FileInputStream (aCertFile);
		BufferedReader theInput = new BufferedReader (new InputStreamReader (retVal));
		if (theInput.readLine ().equalsIgnoreCase ("-----BEGIN CERTIFICATE-----"))
		{
			//
			//	The certificate is base64 encoded
			//
			StringBuffer theBase64 = new StringBuffer (theInput.readLine ());
			String theLine;
			while ( !((theLine = theInput.readLine ()).equalsIgnoreCase ("-----END CERTIFICATE-----")))
			{
				theBase64.append (theLine);
			}
			
			byte [] theByteArray = Conversion.base64StringToByteArray (theBase64.toString ());
			retVal.close ();
			retVal = new ByteArrayInputStream (theByteArray);
		}
		else
		{
			retVal.close ();
			retVal = new FileInputStream (aCertFile);
		}
		return retVal;
	}
			
	protected ServerSocket createServerSocket (int aServerPort, InetAddress aInetAddress)
		throws IOException
	{
		secure = true;
		
		ServerConfigurationManager theSCM = ServerConfigurationManager.getInstance ();
		String thePassphraseProperty = theSCM.getProperty (theSCM.MAUI_PASS_PHRASE);
		if (thePassphraseProperty == null)
		{
			thePassphraseProperty = promptForPassPhrase ();
		}
		char[] thePassphrase = thePassphraseProperty.toCharArray();
		SSLServerSocketFactory theFactory = null;
		try
		{
			String theCertFile = theSCM.getProperty (theSCM.MAUI_CERTIFICATE_FILE);
			javax.net.ssl.SSLContext theContext = javax.net.ssl.SSLContext.getInstance ("TLS");
			javax.net.ssl.KeyManagerFactory theKMF = javax.net.ssl.KeyManagerFactory.getInstance ("SunX509");
			KeyStore theKS = KeyStore.getInstance (KeyStore.getDefaultType ());
			theKS.load (getCertificateStream (theCertFile), null);
			theKMF.init (theKS, thePassphrase);
			
			SecureRandom theSecureRandom = null;
			String theFastRandom = theSCM.getProperty (theSCM.MAUI_FAST_RANDOM);
			if (theFastRandom != null && theFastRandom.equalsIgnoreCase ("true"))
			{
				Random theRandom = new Random ();
				byte [] theRandomBytes = new byte [20];
				theRandom.nextBytes (theRandomBytes);
				theSecureRandom = new SecureRandom (theRandomBytes);
			}
			else
			{
				System.out.println (new InfoString ("[HTTPSecureServer] - Generating random seed.  This may take a short while."));
			}
			theContext.init (theKMF.getKeyManagers (), null, theSecureRandom);
			
			theFactory = (SSLServerSocketFactory) theContext.getServerSocketFactory ();
		}
		catch (Exception e)
		{
			if (! (e instanceof IOException))
			{
				throw new IOException (e.toString ());
			}
			else
			{
				throw (IOException) e;
			}
		}
		ServerSocket retVal = (aInetAddress == null ? theFactory.createServerSocket (aServerPort) :
													 theFactory.createServerSocket (aServerPort,
													 								100,
													 								aInetAddress));
		/*String [] theCiphers = ((SSLServerSocket) retVal).getEnabledCipherSuites ();
		for (int i = 0; i < theCiphers.length; i++)
		{
			System.out.println ("Cipher suite " + i + " = " + theCiphers [i]);
		}*/
		return retVal;//theFactory.createServerSocket (aServerPort);
	}
	
	public static HTTPServer getInstance ()
	{
		return instance;
	}
}
		