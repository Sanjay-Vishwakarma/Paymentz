
package com.payment.icici.com.sfa.java;

import java.sql.*;
//import .*;
import java.net.*;


/**
*
* Merchant class is used to set the details of the Merchant,Invoice and extra fields. The method setMerchantDetails is used for the purpose.
*
*/

public class ICICIMerchant
{
	//  Request message header description fields
   /**
    * Merchant ID corresponds to the ID given by the gateway to the merchant..A string value is passed to the gateway
    *
    *
    */
    private String       mstrMerchantID;
   /**
    *
    * This corresponds to the unique number for the order at the shopping cart of the merchant.
    *
    */

	private String      mstrOrderReferenceNo;

   /**
    *
    * This corresponds to the unique number for the transaction initiated by the merchant from his site.
    *
    */

	private String		mstrMerchantTxnID;



   /**
    *
    * In case the Merchant supports products from multiple vendors, a unique number identifying the vendor has to be given here..A string value is passed to the gateway
    *
    */
    private String       mstrVendor;

   /**
    *
    *Corresponds to a unique number of the Parner.A string value is passed to the gateway
    *
    */

	private String       mstrPartner;

   /**
    *
    * Denotes the URL to which the Payment Gateway redirects the result of the transaction.
    *
    */

	private String		mstrRespURL;

   /**
    *
    *Denotes the method used by the Payment Gateway to redirect the result of the transaction.
    *
    */

	private String		mstrRespMethod;

   /**
    *
    *Any other external field that the user wishes to supply along with the transaction.
    *
    */

	private String 		mstrExt1;

   /**
    *
    *Any other external field that the user wishes to supply along with the transaction.
    *
    */

	private String 		mstrExt2;

   /**
    *
    *Any other external field that the user wishes to supply along with the transaction.
    *
    */

	private String 		mstrExt3;

   /**
    *
    *Any other external field that the user wishes to supply along with the transaction.
    *
    */

	private String 		mstrExt4;

   /**
    *
    *Any other external field that the user wishes to supply along with the transaction.
    *
    */

	private String 		mstrExt5;

   /**
    *
    *The standard ISO currency code for the country has to specified.
    *
    */

	private String		mstrCurrCode;


   /**
    *
    * The message type(req.Preauthorization,req.Sale) that corresponds to the transaction that the merchant wishes to conduct.
    *
    */

	private String		mstrMessageType;

   /**
    *
    * The GMT Offset of the client
    *
    */

	private String		mstrGMTTimeOffset;

   /**
    *
    * This corresponding to a invoice number given by the merchant to the customer.
    *
    */

	private String      mstrInvoiceNo;

   /**
    *
    * The timestamp when the merchant has sent the transaction to the Gateway.
    *
    */

	private String		moInvoiceDate;

   /**
    *
    * The transaction amount for the invoice.
    *
    */

	private String		mstrAmount;

   /**
    *
    * The merchant captures the IPAddress in case he is a Moto Merchant and sends it in this parameter. This can be left blank in case of a SSL merchant as the payment gateway captures these details.
    *
    */

	private String mstrCustIPAddress;

    /**
	 *
	 * Transaction Reference Number of the orignal transaction
	 *
     */

    private String mstrRootTxnSysRefNum;

    /**
	 *
	 * RRN Number of the orignal transaction
	 *
     */
    private String mstrRootPNRefNum;

    /**
	 *
	 *  Auth id of the orignal transaction
	 *
     */
    private String mstrRootAuthCode;

    /**
	 *
	 *  Language code used by SFA
	 *
	 */
    private String mstrLanguageCode;

    /**
	 *
	 *  Start date for Searching Transaction through SFA
	 *
	 */
    private String mstrStartDate;

    /**
	 *
	 *  End date for Searching Transaction through SFA
	 *
	 */
    private String mstrEndDate;
//	Merchant()
//	{
//
//	}


    public void setMerchantDetails(	String astrMerchantID,String astrVendor ,String astrPartner,String astrCustIPAddress,String astrMerchantTxnID,
    											String astrOrderReferenceNo, String astrRespURL, String astrRespMethod, String astrCurrCode,
    											String astrInvoiceNo, String astrMessageType, String astrAmount,String astrGMTTimeOffset,
    											 String astrExt1, String astrExt2, String astrExt3, String astrExt4, String astrExt5)
    {
			mstrMerchantID = astrMerchantID;
			mstrVendor = astrVendor;
			mstrPartner = astrPartner;
			mstrCustIPAddress = astrCustIPAddress;
			mstrMerchantTxnID	=	astrMerchantTxnID;
			mstrOrderReferenceNo = astrOrderReferenceNo;
			mstrRespURL = astrRespURL;
			mstrRespMethod = astrRespMethod;
			mstrCurrCode = astrCurrCode;
			mstrInvoiceNo= astrInvoiceNo;
			mstrMessageType = astrMessageType;
			mstrAmount = astrAmount;
			mstrGMTTimeOffset=astrGMTTimeOffset;
			mstrExt1 	= astrExt1;
			mstrExt2 	= astrExt2;
			mstrExt3 	= astrExt3;
			mstrExt4 	= astrExt4;
			mstrExt5 	= astrExt5;

    }

    public void setMerchantRelatedTxnDetails(	String astrMerchantID,String astrVendor ,
    											String astrPartner,
    											String astrMerchantTxnID,String astrRootTxnSysRefNum,
    											String astrRootPNRef, String astrRootAuthCode,
	    										String astrRespURL, String astrRespMethod, String astrCurrCode,
	    										String astrMessageType, String astrAmount,String astrGMTTimeOffset,
	    										String astrExt1, String astrExt2, String astrExt3, String astrExt4, String astrExt5)
	{
				mstrMerchantID = astrMerchantID;
				mstrPartner = astrPartner;
				mstrMerchantTxnID	=	astrMerchantTxnID;
				mstrRootTxnSysRefNum=   astrRootTxnSysRefNum;
				mstrRootPNRefNum	=   astrRootPNRef;
				mstrRootAuthCode	=   astrRootAuthCode;
				mstrRespURL = astrRespURL;
				mstrRespMethod = astrRespMethod;
				mstrCurrCode = astrCurrCode;
				mstrMessageType = astrMessageType;
				mstrAmount = astrAmount;
				mstrGMTTimeOffset=astrGMTTimeOffset;
				mstrExt1 	= astrExt1;
				mstrExt2 	= astrExt2;
				mstrExt3 	= astrExt3;
				mstrExt4 	= astrExt4;
				mstrExt5 	= astrExt5;
				mstrVendor = astrVendor;


    }

    public void setMerchantOnlineInquiry(String astrMerchantID,String astrMerchantTxnID){
				mstrMerchantID = astrMerchantID;
				mstrMerchantTxnID	=	astrMerchantTxnID;

	}

	public void setMerchantTxnSearch(String astrMerchantID,String astrStartDate,String astrEndDate){
					mstrMerchantID = astrMerchantID;
					mstrStartDate = astrStartDate;
					mstrEndDate = astrEndDate;
	}

    public String getLanguageCode(){
	       return this.mstrLanguageCode;
	}

	public void setLanguageCode(String astrLanguageCode){
	        this.mstrLanguageCode= astrLanguageCode;
	}
	public String getStartDate(){
	       return this.mstrStartDate;
	}

	public void setStartDate(String astrStartDate){
	        this.mstrStartDate= astrStartDate;
	}

	public String getEndDate(){
	       return this.mstrEndDate;
	}

	public void setEndDate(String astrEndDate){
	        this.mstrEndDate= astrEndDate;
	}
    public String getMerchantID() {
        return this.mstrMerchantID;
    }

	public String getMrtIPAddress(){
		try{
		return InetAddress.getLocalHost().getHostAddress();
	}catch(Exception netExc){
			return null;
		}
	}

	public String getCustIPAddress(){
		return mstrCustIPAddress;
	}

    public String getVendor()
    {
        return this.mstrVendor;
    }

    public String getPartner()
    {
        return this.mstrPartner;
    }

   	public String getOrderReferenceNo()
   	{
        return this.mstrOrderReferenceNo;
    }

    public String getRespURL()
    {
        return this.mstrRespURL;
    }

    public String getRespMethod()
    {
        return this.mstrRespMethod;
    }

	public String getCurrCode()
	{
		return this.mstrCurrCode;
	}

   	public String getInvoiceNo()
   	{
        return this.mstrInvoiceNo;
    }

    public String getInvoiceDate()
    {
		return String.valueOf(System.currentTimeMillis());
    }

    public String getMerchantTxnID()
    {
        return mstrMerchantTxnID;
    }

    public String getMessageType()
    {
        return this.mstrMessageType;
    }

	public String getAmount()
	{
		return this.mstrAmount;
	}

    public String getGMTTimeOffset()
    {
     	return this.mstrGMTTimeOffset;
    }

	public String getExt1()
	{
		return mstrExt1;
	}

	public String getExt2()
	{
		return mstrExt2;
	}

	public String getExt3()
	{
		return mstrExt3;
	}

	public String getExt4()
	{
		return mstrExt4;
	}

	public String getExt5()
	{
		return mstrExt5;
	}

	public String getRootTxnSysRefNum (){

		return mstrRootTxnSysRefNum;
	}

	public String getRootPNRefNum(){

		return mstrRootPNRefNum;
	}

	public String getRootAuthCode(){

		return mstrRootAuthCode ;
	}
//	Merchant()
//	{
//
//	}

}