
package com.payment.icici.com.sfa.java;

import java.io.*;

/**
*
* CardInfo is used by the Moto Merchant to set the card details of the customer. The method setCardDetails is used for the purpose.
*
*/

public class CardInfo  {

   /**
    *
    * The merchant has to send the card type selected by the customer for the transaction. The valid values for this are
    * VISA,MC,AMEX.JCB
    */

	private String mstrCardType;
   /**
    * Card number given by the customer to process the transaction.
    *
    *
    */

	private String mstrCardNum;
   /**
    * Card Expiration year of the Card type selected should be specified. The format is yy
    *
    *
    */

	private String mstrExpDtYr;
   /**
    * Card Expiration month of the Card type selected should be specified. The format is mm
    *
    *
    */

	private String mstrExpDtMon;
   /**
    * Card Verification value on the card that the customer supplies in the payment page.
    *
    *
    */

	private String mstrCVVNum;
   /**
    * Name on card corresponds to the name given by the customer in the payment page.
    *
    *
    */

	private String mstrNameOnCard;
   /**
    * Credit or Debit card selected by the customer in the payment page.
    *
    *
    */

	private String mstrInstrType;


	public void setCardDetails(String astrCardType, String astrCardNum, String astrCVVNum, String astrExpDtYr, String astrExpDtMon, String astrNameOnCard, String astrInstrType)
	{
		this.mstrCardType = astrCardType;
		this.mstrCardNum = astrCardNum;
		this.mstrCVVNum = astrCVVNum;
		this.mstrExpDtYr = astrExpDtYr;
		this.mstrExpDtMon = astrExpDtMon;
		this.mstrNameOnCard = astrNameOnCard;
		this.mstrInstrType = astrInstrType;
	}

	public String getCardType()
	{
		return this.mstrCardType;
	}

	public String getCardNum()
	{
		return this.mstrCardNum;
	}

    public String getCVVNum()
    {
		return this.mstrCVVNum;
	}

	public String getExpDtYr()
	{
		return this.mstrExpDtYr ;
	}

	public String getExpDtMon()
	{
		return this.mstrExpDtMon ;
	}

	public String getNameOnCard()
	{
		return this.mstrNameOnCard;
	}

	public String getInstrType()
	{
		return mstrInstrType;
	}

	public String toString()
	{
		return "The Card Details are \n"+
				"CardType   	 "+mstrCardType +"\n"+
				"CardNumber 	 "+mstrCardNum+"\n"+
				"CV Num		 "+mstrCVVNum+"\n"+
				"Card Name	 "+mstrNameOnCard;
	}
}