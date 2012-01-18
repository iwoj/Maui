package com.bitmovers.maui.engine.httpserver;

import java.util.*;

// Provides static type/format conversion methods.
public class Conversion
{
	private final static String[] hexDigits = {"0","1","2","3","4","5","6","7",
	   										   "8","9","a","b","c","d","e","f"};
   										 
	/**
	* Static method to convert a binary byte to a Hex String equivalent
	*
	* @param aByte The byte to convert
	*
	* @return Thre Hex String representation of the byte
	*/
	public static String byteToHexString (final byte aByte)
	{
		int n = ((int) aByte) & 0xff;
		int d1 = n / 16;
		int d2 = n % 16; 
		return hexDigits [d1] + hexDigits[d2];
	}
	
	/**
	* Convert a byte array to a hexadecimal String
	*
	* @param aByteArray The byte array to convert
	*
	* @return A hexadecimal String reresentation of the byte string
	*/
	public static String byteArrayToHexString (final byte [] aByteArray)
	{
		StringBuffer retVal = new StringBuffer (aByteArray.length * 2);
		for (int i = 0; i < aByteArray.length; i++)
		{
			retVal.append (byteToHexString (aByteArray [i]));
		} 
		return retVal.toString ();
	}
	
	/**
	* Convert a byte array to a base 64 string (see RFC 1421)
	*
	* @param aByteArray The byte array to convert
	* @param aLength The length of the byte array
	*
	* @return A base 64 representation of the byte array
	*/
	public static String byteArrayToBase64String (final byte [] aByteArray, int aLength)
	{
  		StringBuffer theInterim = new StringBuffer (aLength * 3);
  		
		// Organize into three byte groups and convert
		int n = aLength / 3;
		int m = aLength % 3;
		
		for (int i = 0; i < n; ++i)
		{
   			int j = i * 3;
   			theInterim.append (toBase64 (aByteArray [j],
	   									 aByteArray [j+1],
	   									 aByteArray [j+2]));
  		}
  		
  		if (m == 1)
  		{
  			theInterim.append (toBase64 (aByteArray [aLength - 1]));
  		}
  		else if (m == 2)
  		{
  			theInterim.append (toBase64 (aByteArray [aLength - 2],
  										 aByteArray [aLength - 1]));
  		}
  		
		// Insert a new line every 64 characters
  		StringBuffer retVal = new StringBuffer (aLength * 3);
  		int theInterimLength = theInterim.length();
  		n = theInterimLength / 64;
  		m = theInterimLength % 64;
  		
  		for (int i = 0; i < n; ++i)
  		{
   			retVal.append (theInterim.substring (i * 64, (i + 1) * 64));
   			retVal.append ("\n");
  		}
  		
  		if (m > 0)
  		{
  			retVal.append (theInterim.substring (n * 64, theInterimLength));
  			retVal.append ("\n");
  		}
		return retVal.toString ();
	}
	
	/**
	* Convert a byte array to a base 64 string (see RFC 1421)
	*
	* @param aByteArray The byte array to convert
	*
	* @return A base 64 representation of the byte array
	*/
	public static String byteArrayToBase64String (final byte [] aByteArray)
	{
  		return byteArrayToBase64String (aByteArray, aByteArray.length);
 	}
 	
 	/**
 	* Perform the base64 transformation
 	*
 	* @param aByte1 The first byte to convert
 	* @param aByte2 The second byte to convert
 	* @param aByte3 The third byte to convert
 	*
 	* @return The String representation of the base64 transformation
 	*/
 	private static String toBase64 (final byte aByte1,
 									final byte aByte2,
 									final byte aByte3)
 	{
  		int [] theDigit = new int [4];
		theDigit [0] = (aByte1 & 0xFC) >>> 2;
		theDigit [1] = (aByte1 & 0x03) << 4;
		theDigit [1] |= (aByte2 & 0xF0) >> 4;
		theDigit [2] = (aByte2 & 0x0F) << 2;
		theDigit [2] |= (aByte3 & 0xC0) >> 6;
		theDigit [3] = (aByte3 & 0x3F);
		
		StringBuffer retVal = new StringBuffer (theDigit.length);
		for (int i = 0; i < theDigit.length; i++)
		{ 
			retVal.append (base64Digit (theDigit [i]));
		}
		return retVal.toString ();
	}
	
	/**
	* Perform a padded base64 transformation
	*
	* @param aByte1 The first byte to convert
	* @param aByte2 The second byte to convert
	*
	* @return The padded String representation of the byte array
	*/
	private static String toBase64 (final byte aByte1,
									final byte aByte2)
	{
  		int [] theDigit = new int [3];
		theDigit [0] = (aByte1 & 0xFC) >>> 2;
		theDigit [1] = (aByte1 & 0x03) << 4;
		theDigit [1] |= (aByte2 & 0xF0) >> 4;
		theDigit [2] = (aByte2 & 0x0F) << 2;
		
		StringBuffer retVal = new StringBuffer (theDigit.length + 1);
		for (int i = 0; i < theDigit.length; i++)
		{ 
			retVal.append (base64Digit (theDigit [i]));
		}
		retVal.append ("=");
		return retVal.toString ();
	}
	
	/**
	* Perform a padded base64 transformation
	*
	* @param aByte1 The byte to convert
	*
	* @return The padded String representation of the byte array
	*/
	private static String toBase64 (byte aByte1)
	{
		int [] theDigit = new int [2];
		theDigit [0] = (aByte1 & 0xFC) >>> 2;
		theDigit [1] = (aByte1 & 0x03) << 4;
		
		StringBuffer retVal = new StringBuffer (theDigit.length + 2);
		for (int i = 0; i < theDigit.length; i++)
		{ 
			retVal.append (base64Digit (theDigit [i]));
		}
		retVal.append ("==");
		return retVal.toString ();
	}
	
	/**
	* Get the base64 digit for a binary value
	*
	* @param aValue The value to convert
	*
	* @return A char representing the base64 digit
	*/
	private static char base64Digit (final int aValue)
	{
		char retVal = '/';
		
		if (aValue < 26)
		{
			retVal =  (char) ('A' + aValue);
		}
		else if (aValue < 52)
		{
			retVal =  (char) ('a' + (aValue - 26));
		}
		else if (aValue < 62)
		{
			retVal = (char) ('0' + (aValue - 52));
		}
		else if (aValue == 62)
		{
			retVal = '+';
		}
		return retVal;
 	}
 	
 	/**
 	* Convert a base 64 string to a byte array (see RFC 1421)
 	*
 	* @param aBase64String The String to convert to a byte array
 	*
 	* @param The decoded binary byte array
 	*/
 	public static byte[] base64StringToByteArray (String aBase64String)
   		throws NumberFormatException
   	{
   		byte [] retVal;
  		StringBuffer theInterimString = new StringBuffer (aBase64String.length ());
		for (int i = 0; i < aBase64String.length(); i++)
		{
   			char c = aBase64String.charAt (i);
   			if (c == '\n')
   			{
   				continue;
   			}
   			else if ((c >= 'A' && c <= 'Z') ||
   					 (c >= 'a' && c <= 'z') || 
     				 (c >= '0' && c <= '9') ||
     				 c=='+' ||
     				 c=='/')
     		{
     			theInterimString.append (c);
     		}
			else if (c == '=')
			{
				break;
			}
			else
			{
				throw new NumberFormatException();
			}
		}

		int theLength = theInterimString.length();
		int n = 3 * (theLength / 4);
		switch (theLength % 4)
		{
			case 1:
				throw new NumberFormatException();
			
			case 2:
				theLength += 2;
				n += 1;
				theInterimString.append ("==");
				break;

			case 3:
				theLength++;
				n += 2;
				theInterimString.append ("=");
				break;
  		}
  		
  		retVal = new byte [n];
		for(int i = 0; i < theLength / 4; i++)
		{
   			byte [] theTempArray = base64ToBytes (theInterimString.substring (4 * i,
   																			  4 * (i + 1)));
   			for (int j = 0; j < theTempArray.length;++j)
   			{
    			retVal [3 * i + j] = theTempArray [j];
			}
		}
		return retVal;
	}

	/**
	* Convert a base64 string to bytes
	*
	* @param aBase64String The Base 64 String to convert
	*
	* @return The base 64 array converted to bytes
	*/
	private static byte[] base64ToBytes (final String aBase64String)
	{
		byte [] retVal;
  		int theLength = 0;
  		
  		for (theLength = 0; theLength < aBase64String.length (); theLength++)
  		{
  			if (aBase64String.charAt (theLength) == '=')
  			{
  				break;
  			}
  		}
  		
		int [] theDigit = new int [theLength];
		for (int i = 0; i < theLength;i++)
		{
			char theChar = aBase64String.charAt (i);
			if (theChar >= 'A' && theChar <= 'Z') 
			{
				theDigit [i] = theChar - 'A';
			}
			else if (theChar >= 'a' && theChar <='z')
			{
				theDigit[i] = theChar - 'a' + 26;
			}
			else if (theChar >= '0' && theChar <= '9')
			{
				theDigit [i] = theChar - '0' + 52;
			}
			else if (theChar == '+')
			{
				theDigit [i] = 62;
			}
			else if (theChar == '/')
			{
				theDigit [i] = 63;
			}
  		}
  		
  		retVal = new byte [theLength - 1];
		switch (theLength)
		{
		   case 4:
				retVal [2] = (byte) ((((theDigit [2]) & 0x03) << 6) | theDigit [3]);
		    
		   case 3:
				retVal [1] = (byte) ((((theDigit [1]) & 0x0F) << 4) | ((theDigit[2] & 0x3C) >>> 2));
				
		   case 2:
		    	retVal [0] = (byte) ((theDigit[0] << 2) | ((theDigit[1] & 0x30) >>> 4));
  		}
  		return retVal;
	}
}
