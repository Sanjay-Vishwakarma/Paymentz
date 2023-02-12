
package com.payment.icici.com.sfa.java;

import java.io.*;

/**
*
* PGReserveData is used by the Moto/SSL Merchant to set the Reserve Field details of the customer. The method setReserveObj is used for the purpose.
*
*/

public class PGReserveData  {


	private String mstrReserveField1;
    private String mstrReserveField2;
    private String mstrReserveField3;
    private String mstrReserveField4;
    private String mstrReserveField5;
    private String mstrReserveField6;
    private String mstrReserveField7;
    private String mstrReserveField8;
    private String mstrReserveField9;
    private String mstrReserveField10;

	public void setReserveObj(String astrReserveField1, String astrReserveField2, String astrReserveField3, String astrReserveField4, String astrReserveField5, String astrReserveField6, String astrReserveField7,String astrReserveField8,String astrReserveField9,String astrReserveField10)
	{
		mstrReserveField1 = astrReserveField1;
		mstrReserveField2 = astrReserveField2;
		mstrReserveField3 = astrReserveField3;
		mstrReserveField4 = astrReserveField4;
		mstrReserveField5 = astrReserveField5;
		mstrReserveField6 = astrReserveField6;
		mstrReserveField7 = astrReserveField7;
		mstrReserveField8 = astrReserveField8;
		mstrReserveField9 = astrReserveField9;
		mstrReserveField10 = astrReserveField10;

	}

	public String getReserveField1()
	{
		return this.mstrReserveField1;
	}

	public String getReserveField2()
	{
		return this.mstrReserveField2;
	}


	public String getReserveField3()
	{
		return this.mstrReserveField3;
	}
	public String getReserveField4()
	{
		return this.mstrReserveField4;
	}


	public String getReserveField5()
	{
		return this.mstrReserveField5;
	}


	public String getReserveField6()
	{
		return this.mstrReserveField6;
	}


	public String getReserveField7()
	{
		return this.mstrReserveField7;
	}


	public String getReserveField8()
	{
		return this.mstrReserveField8;
	}


	public String getReserveField9()
	{
		return this.mstrReserveField9;
	}


	public String getReserveField10()
	{
		return this.mstrReserveField10;
	}


	public String toString()
	{
		return "The Reserved Object Details are \n"+
				"Reserve Field 1   	 :"+mstrReserveField1 +"\n"+
				"Reserve Field 2 	 :"+mstrReserveField2+"\n"+
				"Reserve Field 3 	 :"+mstrReserveField3+"\n"+
				"Reserve Field 4 	 :"+mstrReserveField4+"\n"+
				"Reserve Field 5 	 :"+mstrReserveField5+"\n"+
				"Reserve Field 6 	 :"+mstrReserveField6+"\n"+
				"Reserve Field 7 	 :"+mstrReserveField7+"\n"+
				"Reserve Field 8 	 :"+mstrReserveField8+"\n"+
				"Reserve Field 9 	 :"+mstrReserveField9+"\n"+
				"Reserve Field 10 	 :"+mstrReserveField10;

	}
}