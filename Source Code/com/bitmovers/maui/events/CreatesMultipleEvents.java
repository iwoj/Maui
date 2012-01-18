package com.bitmovers.maui.events;


// ========================================================================
// INTERFACE: CreatesMultipleEvents
// ========================================================================

/** This interface is for MComponents which are capable of generating
  * multiple MauiEvents as a result of a single event<p>
  * 
  * @invisible
  *
  */
  
public interface CreatesMultipleEvents
{
	/**
	  * Create the MauiEvent array
	  *
	  * @param aStateData The state data
	  *
	  * @return The MaueEvent array
	  */
	  public MauiEvent [] createEvents (String aStateData);
}


// ========================================================================
// (c) 2001 Bitmovers Systems                                           EOF