package com.bitmovers.maui.engine;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import com.bitmovers.maui.engine.httpserver.HTTPSession;

// ======================================================================
// CLASS: AuthorizationManager                 (c) 2001 Bitmovers Systems
// ======================================================================

/** This is used to match up license levels with allowed activities.
  *
  */
public class AuthorizationManager
{
	public static final int AUTHORIZATION_MAJOR_VERSION = 0;
	public static final int AUTHORIZATION_MINOR_VERSION = 1;
	public static final int AUTHORIZATION_SERVLET = 2;
	public static final int AUTHORIZATION_SESSIONS = 3;
	public static final int AUTHORIZATION_LICENSE = 4;
	public static final int AUTHORIZATION_KEY = 5;
	public static final int AUTHORIZATION_CLIENTBRANDING = 6;
	public static final int AUTHORIZATION_BITMOVERSBRANDING = 7;
	
	public static final int VALUE_MAJOR_VERSION = 1;
	public static final int VALUE_MINOR_VERSION = 2;
	public static final boolean VALUE_SERVLET = false;
	public static final int VALUE_SESSIONS = 5;
	public static final boolean VALUE_CLIENTBRANDING = false;
	public static final boolean VALUE_BITMOVERSBRANDING = false;
	
	public static final boolean LICENSE_SERVLET = true;
	public static final int LICENSE_SESSIONS = -1;
	public static final boolean LICENSE_CLIENTBRANDING = true;
	public static final boolean LICENSE_BITMOVERSBRANDING = false;
	
	public static final byte TYPE_BOOLEAN = 0;
	public static final byte TYPE_INTEGER = 1;
	public static final byte TYPE_STRING = 2;
	
	private final int ARRAY_LENGTH = 8;
	
	private static final int [] mungeArray = new int [] {1651078253,
												  		 1870030194,
												  		 1931504993,
												  		 1969823776};
	
	private Object [] authorizationArray = new Object [ARRAY_LENGTH];
	private static AuthorizationManager authorizationManager = new AuthorizationManager ();
	
	private AuthorizationManager ()
	{
		//
		//	Load authorization defaults
		//
		authorizationArray [AUTHORIZATION_MAJOR_VERSION] = new Integer (VALUE_MAJOR_VERSION);
		authorizationArray [AUTHORIZATION_MINOR_VERSION] = new Integer (VALUE_MINOR_VERSION);
		authorizationArray [AUTHORIZATION_SERVLET] = new Boolean (VALUE_SERVLET);
		authorizationArray [AUTHORIZATION_SESSIONS] = new Integer (VALUE_SESSIONS);
		authorizationArray [AUTHORIZATION_LICENSE] = null;
		authorizationArray [AUTHORIZATION_KEY] = null;
		authorizationArray [AUTHORIZATION_CLIENTBRANDING] = new Boolean (VALUE_CLIENTBRANDING);
		authorizationArray [AUTHORIZATION_BITMOVERSBRANDING] = new Boolean (VALUE_BITMOVERSBRANDING);
	}
	
	public static AuthorizationManager getInstance ()
	{
		return authorizationManager;
	}
	
	private Object allocateObject (byte aDataType, DataInputStream aInput)
		throws IOException
	{
		Object retVal = null;
		
		switch (aDataType) 
		{
			case (TYPE_BOOLEAN) :
				byte theBoolean = aInput.readByte ();
				retVal = new Boolean ((theBoolean == 1 ? true : false));
				break;
				
			case (TYPE_INTEGER) :
				retVal = new Integer (aInput.readInt ());
				break;
				
			case (TYPE_STRING) :
				byte theLength = aInput.readByte ();
				byte [] theData = new byte [theLength];
				aInput.read (theData, 0, theLength);
				retVal = new String (theData, 0, theLength);
				break;
		}
		return retVal;
	}
	
	private Object allocateAuthorizationItem (byte aDataType, byte aArraySize, DataInputStream aInput)
		throws IOException
	{
		Object retVal = null;
		if (aArraySize == -1)
		{
			retVal = allocateObject (aDataType, aInput);
		}
		else
		{
			Object [] theArray = new Object [aArraySize];
			for (int i = 0; i < aArraySize; i++)
			{
				theArray [i] = allocateObject (aDataType, aInput);
			}
			retVal = theArray;
		}
		return retVal;
	}
	
	private Object [] getAuthorizationArray (DataInputStream aInput)
		throws IOException
	{
		int theArrayLength = aInput.readInt ();
		Object [] retVal = new Object [theArrayLength];
		for (int i = 0; i < retVal.length; i++)
		{
			byte theCode = aInput.readByte ();
			byte theDataType = aInput.readByte ();
			byte theArraySize = aInput.readByte ();
			retVal [(int) theCode] = allocateAuthorizationItem (theDataType, theArraySize, aInput);
		}
		return retVal;
	}
	
	private static Object [] getMungedKey (String aKey)
	{
		int [] theMungeKey = munge (aKey);
		Object [] retVal = new Integer [theMungeKey.length];
		for (int i = 0; i < theMungeKey.length; i++)
		{
			retVal [i] = new Integer (theMungeKey [i]);
		}
		return retVal;
	}
	
	public static boolean checkAuthorization (Object [] aLicense,
											  String aKey)
	{
		return checkAuthorization (aLicense, getMungedKey (aKey));
	}
	
	public static boolean checkAuthorization (Object [] aLicense,
											  Object [] aKey)
	{
		boolean retVal = true;
		int theMungeIndex = 0;
		
		for (int i = 0; i < aLicense.length && retVal; i++)
		{
			int theLicenseValue = ((Integer) aLicense [i]).intValue ();
			int theKeyValue = ((Integer) aKey [i]).intValue ();
			retVal = (mungeArray [theMungeIndex++] == (theLicenseValue ^ theKeyValue));
			if (theMungeIndex == mungeArray.length)
			{
				theMungeIndex = 0;
			}
		}
		return retVal;
	}
	
	private static int getMungeValue (char [] aChars, int aOffset)
	{
		int theOffset = aOffset * 4;
		int retVal = 0;
		for (int i = 0; i < 4; i++)
		{
			retVal = (retVal << 8) + (((byte) aChars [theOffset++]) & 0xff);
		}
		return retVal;
	}
		
	private static int [] munge (String aString)
	{
		int [] retVal = new int [(aString.length () + 3) / 4];
		char [] theChars = new char [retVal.length * 4];
		aString.getChars (0, aString.length (), theChars, 0);
		for (int i = 0; i < retVal.length; i++)
		{
			retVal [i] = getMungeValue (theChars, i);
		}
		return retVal;
	}
	
	public static Object [] getLicenseKey (String aEmailAddress)
	{
		int [] theMungedAddress = munge (aEmailAddress);
		Object [] retVal = new Object [theMungedAddress.length];
		int theMungeIndex = 0;
		
		for (int i = 0; i < theMungedAddress.length; i++)
		{
			retVal [i] = new Integer (mungeArray [theMungeIndex++] ^ theMungedAddress [i]);
			if (theMungeIndex == mungeArray.length)
			{
				theMungeIndex = 0;
			}
		}
		return retVal;
	}
	
	public void initialize ()
	{
		DataInputStream theInput = null;
		//
		//	Look for the license file
		//
		try
		{
			theInput = new DataInputStream (new FileInputStream ("license.dat"));
			//
			//	Read the license file.
			//
			Object [] theLicenseArray = getAuthorizationArray (theInput);
			if (checkAuthorization ((Object []) theLicenseArray [AUTHORIZATION_LICENSE],
									(Object []) theLicenseArray [AUTHORIZATION_KEY]))
			{
				//
				//	Okay
				//
				//	Replace the authorization array with the loaded one
				//
				int theLimit = (authorizationArray.length > theLicenseArray.length ?
									theLicenseArray.length :
									authorizationArray.length);
				for (int i = 0; i < theLimit; i++)
				{
					authorizationArray [i] = theLicenseArray [i];
				}
			}
			else
			{
				//
				//	Not Okay
				//
				//	Leave the authorization the way it is and send out a nasty message
				//
				System.err.println ("[AuthorizationManager] Invalid license.  Reverting to limited version");
			}
			
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (theInput != null)
			{
				try
				{
					theInput.close ();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
	
	private byte getDataType (int aCode)
	{
		byte retVal = 0;
		switch (aCode)
		{
			case (AUTHORIZATION_MAJOR_VERSION) :
			case (AUTHORIZATION_MINOR_VERSION) :
			case (AUTHORIZATION_SESSIONS) :
			case (AUTHORIZATION_LICENSE) :
			case (AUTHORIZATION_KEY) :
				retVal = TYPE_INTEGER;
				break;
				
			case (AUTHORIZATION_SERVLET) :
			case (AUTHORIZATION_CLIENTBRANDING) :
			case (AUTHORIZATION_BITMOVERSBRANDING) :
				retVal = TYPE_BOOLEAN;
				break;
				
		}
		return retVal;
	}
	
	private byte getArraySize (int aCode)
	{
		byte retVal = -1;
		
		if (aCode == AUTHORIZATION_LICENSE ||
			aCode == AUTHORIZATION_KEY)
		{
			retVal = (byte) ((Object []) authorizationArray [aCode]).length;
		}
		return retVal;
	}
	
	private void writeValue (DataOutputStream aOutput, Object aValue, byte aDataType)
		throws IOException
	{
		switch (aDataType)
		{
			case (TYPE_BOOLEAN) :
				aOutput.writeByte (((Boolean) aValue).booleanValue () ? (byte) 1 : (byte) 0);
				break;
				
			case (TYPE_INTEGER) :
				aOutput.writeInt (((Integer) aValue).intValue ());
				break;
		}
	}
	
	private void writeValues (DataOutputStream aOutput, Object aValue, byte aDataType, byte aArrayLength)
		throws IOException
	{
		if (aArrayLength == -1)
		{
			writeValue (aOutput, aValue, aDataType);
		}
		else
		{
			for (int i = 0; i < aArrayLength; i++)
			{
				writeValue (aOutput, ((Object []) aValue) [i], aDataType);
			}
		}
	}
	
	public void setAuthorization (Object [] aLicense, String aKey)
	{
		Object [] theKeyArray = getMungedKey (aKey);
		if (checkAuthorization (aLicense, theKeyArray))
		{
			authorizationArray [AUTHORIZATION_LICENSE] = aLicense;
			authorizationArray [AUTHORIZATION_KEY] = theKeyArray;
			authorizationArray [AUTHORIZATION_SERVLET] = new Boolean (LICENSE_SERVLET);
			authorizationArray [AUTHORIZATION_SESSIONS] = new Integer (LICENSE_SESSIONS);
			authorizationArray [AUTHORIZATION_CLIENTBRANDING] = new Boolean (LICENSE_CLIENTBRANDING);
			authorizationArray [AUTHORIZATION_BITMOVERSBRANDING] = new Boolean (LICENSE_BITMOVERSBRANDING);
		}
	}
			
	
	public void saveLicense (Object [] aLicense, String aKey)
		throws IOException
	{
		saveLicense(aLicense, aKey, new FileOutputStream("license.dat"));
	}

	
	public void saveLicense (Object [] aLicense, String aKey, OutputStream aStream)
		throws IOException
	{
		Object [] theKeyArray = getMungedKey (aKey);
		
		if (checkAuthorization (aLicense, theKeyArray))
		{
			setAuthorization (aLicense, aKey);
			DataOutputStream theOutput = new DataOutputStream (aStream);
			try
			{
				theOutput.writeInt (ARRAY_LENGTH);
				
				for (int i = 0; i < ARRAY_LENGTH; i++)
				{
					theOutput.writeByte ((byte) i);
					byte theDataType = getDataType (i);
					theOutput.writeByte (theDataType);
					byte theArraySize = getArraySize (i);
					theOutput.writeByte (theArraySize);
					writeValues (theOutput, authorizationArray [i], theDataType, theArraySize);
				}
			}
			catch (IOException e)
			{
				System.err.println (e.toString ());
			}
			finally
			{
				theOutput.close ();
			}
		}
	}

	public boolean isAuthorized (int aCode)
	{
		return isAuthorized (null, aCode);
	}
	
	public boolean isAuthorized (Object aComparator, int aCode)
	{
		boolean retVal = false;
		switch (aCode)
		{
			case (AUTHORIZATION_SERVLET) :
				retVal = ((Boolean) authorizationArray [aCode]).booleanValue ();
				break;
				
			case (AUTHORIZATION_SESSIONS) :
				int theValue = ((Integer) authorizationArray [aCode]).intValue ();
				int theSessionCount = HTTPSession.getSessionCount ();
				retVal = (theValue == -1 || theSessionCount <= theValue);
				break;
				
			case (AUTHORIZATION_CLIENTBRANDING) :
				retVal = ((Boolean) authorizationArray [aCode]).booleanValue ();
				break;
				
			case (AUTHORIZATION_BITMOVERSBRANDING) :
				retVal = ((Boolean) authorizationArray [aCode]).booleanValue ();
				break;
		}
				
		return retVal;
	}
	
	public Object getAuthorizationValue (int aCode)
	{
		return authorizationArray [aCode];
	}
}
