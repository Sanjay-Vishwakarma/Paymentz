
package com.payment.icici.com.sfa.java;
import java.sql.Timestamp;
//import com.opus.epg.classes.*;


public class Address
{

	protected String mstrCountryAlphaCode=null;
	protected String mstrAddLine1=null ;
	protected String mstrAddLine2=null ;
	protected String mstrAddLine3=null ;
	protected String mstrCity=null;
	protected String mstrState=null;
	protected String mstrZip=null;
	protected String mstrEmail=null;

	public void setAddressDetails(String astrAddrLine1, String astrAddrLine2, String astrAddrLine3, String astrCity, String astrState, String astrZip, String astrCountryAlpha,String astrEmail)
	{
		this.mstrAddLine1 = astrAddrLine1 ;
		this.mstrAddLine2 = astrAddrLine2 ;
		this.mstrAddLine3 = astrAddrLine3 ;
		this.mstrCity=astrCity;
		this.mstrState=astrState;
		this.mstrZip = astrZip;
		this.mstrCountryAlphaCode = astrCountryAlpha;
		this.mstrEmail	=	astrEmail;
	}

	public String getAddrLine1()
	{
		return mstrAddLine1 ;
	}

	public String getAddrLine2()
	{
		return mstrAddLine2 ;
	}

	public String getAddrLine3()
	{
		return mstrAddLine3 ;
	}

	public String getCity()
	{
		return this.mstrCity;
	}

	public String getState()
	{
		return this.mstrState;
	}

	public String getZip()
	{
		return this.mstrZip;
	}

	public String getCountryAlphaCode()
	{
		return this.mstrCountryAlphaCode;
	}

	public String toString()
	{
		StringBuffer oStringBuffer = new StringBuffer(80);
		oStringBuffer.append(" \nCountry Alpha Code : " +  mstrCountryAlphaCode +
					" \nAddress Line 1 : " +  mstrAddLine1 +
					" \nAddress Line 2 : " + mstrAddLine2 +
					" \nAddress Line 3 : " + mstrAddLine3 +
					" \nCity : " + mstrCity +
					" \nState : " +  mstrState +
					" \nZip : " +  mstrZip
					);
		return(oStringBuffer.toString());
	}
}