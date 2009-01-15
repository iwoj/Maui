package com.bitmovers.maui.engine.logmanager;

import java.util.Date;


// ========================================================================
// CLASS: LogManagerDate                         (c) 2001 Bitmovers Systems
// ========================================================================

/** The LogManagerDate class overrides the java.util.Date class to
  * provide an alternate output using the toString() method which is
  * suitable for the Maui LogManager. It outputs a String like so:<p>
  *
  * [YYYY.MM.DD HH:MM:SS]
  *
  * @invisible
  *
  */

public class LogManagerDate extends Date
{
  // ----------------------------------------------------------------------
  // CONSTRUCTOR: LogManagerDate
  // ----------------------------------------------------------------------

  public LogManagerDate()
  {
    super();
  }


  // ----------------------------------------------------------------------
  // METHOD: toString
  // ----------------------------------------------------------------------

  /** toString() overrides the Date.toString() to display a format which
    * is preferred for timestamps in the Maui LogManger.
    *
    */
  
  public String toString()
  {
    String year = this.padWithZero(super.getYear() + 1900);
    String month = this.padWithZero(super.getMonth() + 1);
    String day = this.padWithZero(super.getDate());
    String hour = this.padWithZero(super.getHours());
    String minute = this.padWithZero(super.getMinutes());
    String second = this.padWithZero(super.getSeconds());
    
    return "[" + year + "." + month + "." + day + " " + hour + ":" + minute + ":" + second + "]";
  }


  // ----------------------------------------------------------------------
  // METHOD: padWithZero
  // ----------------------------------------------------------------------
  
  /** padWithZero() prepends a zero to a number if it is less than 10 so
    * that all numbers are at least 2 digits long.
    *
    */
  
  private String padWithZero(int number)
  {
    if (number < 10)
    {
      return "0" + number;
    }
    else
    {
      return Integer.toString(number);
    }
  }
	
	
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF