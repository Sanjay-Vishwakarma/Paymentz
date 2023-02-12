package com.payment.icici.com.sfa.java;

public class AirLineTransaction
{
	private String mstrBookingDate=null;
	private String mstrFlightDate=null;
	private String mstrFlightTime=null;
	private String mstrFlightNumber=null;
	private String mstrPassengerName=null;
	private String mstrNumberOfTickets=null;
	private String mstrIsCardNameNCustomerNameMatch=null;
	private String msrtPNR=null;
	private String mstrSectorFrom=null;
	private String mstrSectorTo=null;
	private String airLineInfoIsAvailable = null;

	public void setAirLineTransactionDetails(String astrBookingDate,String astrFlightDate,String astrFlightTime,
											 String astrFlightNumber,String astrPassengerName,String astrNumberOfTickets,
											 String astrIsCardNameNCustomerNameMatch,String asrtPNR,String astrSectorFrom,
											 String astrSectorTo)
	{
	  this.mstrBookingDate                  = astrBookingDate ;
	  this.mstrFlightDate                   = astrFlightDate;
	  this.mstrFlightTime                   = astrFlightTime;
	  this.mstrFlightNumber                 = astrFlightNumber;
	  this.mstrPassengerName                = astrPassengerName;
	  this.mstrNumberOfTickets              = astrNumberOfTickets;
	  this.mstrIsCardNameNCustomerNameMatch = astrIsCardNameNCustomerNameMatch;
	  this.msrtPNR                          = asrtPNR;
	  this.mstrSectorFrom                   = astrSectorFrom;
	  this.mstrSectorTo                     = astrSectorTo;
	  this.airLineInfoIsAvailable			= "YES";
	}

	public String getBookingDate()
	{
	  return this.mstrBookingDate ;
	}
	public String getAirLineFlag()
	{
	  return this.airLineInfoIsAvailable ;
	}
	public String getFlightDate()
	{
	  return this.mstrFlightDate;
	}
	public String getFlighttime()
	{
	  return  this.mstrFlightTime;
    }
    public String getFlightNumber()
    {
	  return this.mstrFlightNumber;
	}
	public String getPassengerName()
	{
		return   this.mstrPassengerName;
	}
	public String getNumberOfTickets()
	{
	  return   this.mstrNumberOfTickets;
	}
	public String getIsCardNameNCustomerNameMatch()
	{
		return this.mstrIsCardNameNCustomerNameMatch;
	}
	public String getPNR()
	{
		return  this.msrtPNR;
	}
	public String getSectorFrom()
	{
	  return  this.mstrSectorFrom;
	}
	public String getSecotrTo()
	{
		return this.mstrSectorTo;
	}
}