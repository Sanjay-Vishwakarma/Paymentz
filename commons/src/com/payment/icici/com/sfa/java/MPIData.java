
package com.payment.icici.com.sfa.java;


public class MPIData
{

   /**
    *
    * Unformatted purchase amount. It should not contain any special characters such as "$" etc.
    *
    */

	private String mstrPurchaseAmount=null ;
   /**
    *
    * Formatted purchase amount. This field should contain a currency symbol, with an thousands separator (s), decimal point and ISO minor units defined for the currency specified in the Purchase Currency field.
		Ex. $1,234.56
    *
    */

	private String mstrDisplayAmount=null ;
   /**
    *
    * Contains the Standard ISO currency value for the country. The value for India is 356. For details refer:
http://userpage.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
    *
    */

	private String mstrCurrencyVal=null;
   /**
    *
    * 	The number of decimal places used in the amount is specified. Used to distinguish the units of money like Rs and Ps.
Example: Amount 100000 with an exponent value of 2 becomes Rs.1,000

    *
    */

	private String mstrExponent=null;
   /**
    *
    * Brief description of items purchased, determined by the Merchant.
Example 2 shirts.

    *
    */

	private String mstrOrderDesc=null;
   /**
    *
    * This field is calculated based on installments and the Recur End and it denotes the frequency of payment. It is optional and must be included if other recurring variables are specified.
    *
    */

	private String mstrRecurFreq=null;
   /**
    *
    * This field indicates the end date of recurring value. It should be less than the card expiry date. It is also an optional field.
    *
    */

	private String mstrRecurEnd=null;
   /**
    *
    * MPI supports payment by installments. In order to enable this support to the customer, the following fields have to be set.This field indicates the number of times the payment is done to fulfill the transaction.
    *
    */

	private String mstrInstallment=null;
   /**
    *
    * This attribute indicates mode of transaction. For an internet based transaction the value to be set is "0".
    *
    */

	private String mstrDeviceCategory=null;
   /**
    * Denotes the browser version of the client. This field can be empty and is used by the MPI to denote the browser version.
    *
    *
    */

	private String mstrWhatIUse=null;
   /**
    *
    * The Accept request-header field can be used to specify certain media types which are acceptable for the response. Accept headers can be used to indicate that the request is specifically limited to a small set of desired types, as in the case of a request for an in-line image.
The server property request.getHeader("Accept") can be used for setting this value.

    *
    */

	private String mstrAcceptHdr=null;
   /**
    *
    * The User-Agent-header contains information about the user agent
   (typically a newsreader) generating the article, for statistical
   purposes and tracing of standards violations to specific software
   needing correction. Although not one of the mandatory headers,
   posting agents SHOULD normally include it.
The server property request.getHeader("User-Agent") can be used for setting this value.

    *
    */

	private String mstrAgentHdr=null;
	/**
	*	Any Echo back field value
	*/
	private String mstrShoppingContext=null;
   /**
    *
    * Should be set to the result of conducting an MPI transaction
    *
    */

	private String mstrVBVStatus	=null;
   /**
    * Should be set to the result of conducting an MPI transaction
    *
    */

	private String mstrCAVV=null;
   /**
    * Should be set to the result of conducting an MPI transaction
    *
    */

	private String mstrECI=null;
   /**
    * Should be set to the result of conducting an MPI transaction
    *
    */

	private String mstrXID=null;


	public String getECI()
	{
		return mstrECI;
	}

	public String getXID()
	{
		return mstrXID;
	}

	public String getVBVStatus()
	{
		return mstrVBVStatus;
	}

	public void setMPIRequestDetails(
										String astrPurchaseAmount, String astrDisplayAmount, String astrCurrencyVal,
										String astrExponent, String astrOrderDesc, String astrRecurFreq, String astrRecurEnd, String astrInstallment,
										String astrDeviceCategory, String astrWhatIUse, String astrAcceptHdr, String astrAgentHdr
									)
	{
        mstrPurchaseAmount = astrPurchaseAmount;
        mstrDisplayAmount = astrDisplayAmount;
        mstrCurrencyVal = astrCurrencyVal;
        mstrExponent = astrExponent;
        mstrOrderDesc = astrOrderDesc;
        mstrRecurFreq = astrRecurFreq;
        mstrRecurEnd = astrRecurEnd;
        mstrInstallment = astrInstallment;
        mstrDeviceCategory = astrDeviceCategory;
        mstrWhatIUse = astrWhatIUse;
        mstrAcceptHdr = astrAcceptHdr;
        mstrAgentHdr = astrAgentHdr;

    }

	public void setMPIResponseDetails(String astrECI,
										String astrXID, String astrVBVStatus,
										String astrCAVV,String astrShoppingContext,
										String astrPurchaseAmount,String astrCurrencyVal)
	{
        mstrECI = astrECI;
        mstrXID = astrXID;
        mstrVBVStatus = astrVBVStatus;
        mstrCAVV = astrCAVV;
        mstrShoppingContext	=	astrShoppingContext;
    	mstrPurchaseAmount = astrPurchaseAmount;
        mstrCurrencyVal = astrCurrencyVal;
    }


	public String getCAVV()
	{
		return mstrCAVV;
	}


	public String getPurchaseAmount()
	{
        return this.mstrPurchaseAmount;
    }

	public String getDisplayAmount()
	{
        return this.mstrDisplayAmount;
    }

	public String getCurrencyVal()
	{
        return this.mstrCurrencyVal;
    }

	public String getExponent()
	{
        return this.mstrExponent;
    }

	public String getOrderDesc()
	{
        return this.mstrOrderDesc;
    }

	public String getRecurFreq()
	{
        return this.mstrRecurFreq;
    }

	public String getRecurEnd()
	{
        return this.mstrRecurEnd;
    }

	public String getInstallment()
	{
        return this.mstrInstallment;
    }

    public String getDeviceCategory()
	{
        return this.mstrDeviceCategory;
    }

	public String getWhatIUse()
	{
        return this.mstrWhatIUse;
    }

	public String getAcceptHdr()
	{
        return this.mstrAcceptHdr;
    }

	public String getAgentHdr()
	{
        return this.mstrAgentHdr;
    }
	public String getShoppingContext()
	{
        return this.mstrShoppingContext;
    }


}