

package com.payment.icici.com.sfa.java;

import java.io.*;

public class BillToAddress extends Address
{

	private String mstrEmail=null;
	private String mstrCustomerId=null;
	private String mstrCustomerName=null;


	public void setAddressDetails(		String astrCustomerId, String astrCustomerName,String astrAddrLine1, String astrAddrLine2, String astrAddrLine3,
											String astrCity, String astrState, String astrZip, String astrCountryAlpha, String astrEmail)
	{
			this.mstrCustomerId=astrCustomerId;
			this.mstrCustomerName=astrCustomerName;
			this.mstrEmail = astrEmail;
			super.setAddressDetails(astrAddrLine1, astrAddrLine2, astrAddrLine3, astrCity, astrState, astrZip, astrCountryAlpha,astrEmail);
	}

	public String getEmail()
	{
		return this.mstrEmail;
	}

	public String getCustomerId()
	{
		return this.mstrCustomerId;
	}

	public String getName()
	{
		return this.mstrCustomerName;
	}


	public String toString()
	{
			return "The Bill to address is \n"+
					"CustomerId 	 "+mstrCustomerId+"\n"+
					"CustomerName	 "+mstrCustomerName+"\n"+
					"Street   	 "+mstrAddLine1 +"\n"+
					"        	 "+mstrAddLine2 +"\n"+
					"       	 "+mstrAddLine3 +"\n"+
					"City		 "+mstrCity+"\n"+
					"State	 	 "+mstrState+"\n"+
					"Zip		 "+mstrZip+"\n"+
					"CountryAlphaCode    	 "+mstrCountryAlphaCode+"\n"+
					"Email	 	 "+mstrEmail;
	}
}
