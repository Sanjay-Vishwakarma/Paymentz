
package com.payment.icici.com.sfa.java;

public class ShipToAddress extends Address
{
	public String toString()
	{
		return "The Ship  address is \n"+
				"Street   	 "+mstrAddLine1 +"\n"+
				"        	 "+mstrAddLine2 +"\n"+
				"       	 "+mstrAddLine3 +"\n"+
			"City		 "+mstrCity+"\n"+
			"State	 	 "+mstrState+"\n"+
			"Zip		 "+mstrZip+"\n"+
			"CountryAlphaCode    	 "+mstrCountryAlphaCode;
	}
}
