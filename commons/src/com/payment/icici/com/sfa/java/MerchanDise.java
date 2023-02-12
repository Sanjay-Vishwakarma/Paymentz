package com.payment.icici.com.sfa.java;
public class MerchanDise
{
	private String mstrItemPurchased=null;
	private String mstrBuyersName=null;
	private String mstrModelNumber=null;
	private String mstrBrand=null;
	private String mstrQuantity=null;
	private String mstrIsCardNameNBuyerNameMatch=null;
	private String mstrDiseIsAvailable = null;


	public void setMerchanDiseDetails(String astrItemPurchased,String astrQuantity,String astrBrand,
											 String astrModelNumber,String astrBuyersName,
											 String astrIsCardNameNBuyerNameMatch)
	{
	  this.mstrItemPurchased       	      = astrItemPurchased ;
	  this.mstrQuantity                   = astrQuantity;
	  this.mstrBrand                   	  = astrBrand;
	  this.mstrModelNumber                = astrModelNumber;
	  this.mstrBuyersName                 = astrBuyersName;
	  this.mstrIsCardNameNBuyerNameMatch  = astrIsCardNameNBuyerNameMatch;
	  this.mstrDiseIsAvailable			  = "YES";
	}

	public String getMerchantFlag()
	{
	  return this.mstrDiseIsAvailable ;
	}
	public String getItemPurchased()
	{
	  return this.mstrItemPurchased ;
	}
	public String getQuantity()
	{
	  return this.mstrQuantity;
	}
	public String getBrand()
	{
	  return  this.mstrBrand;
    }
    public String getModelNumber()
    {
	  return this.mstrModelNumber;
	}

	public String getBuyersName()
	{
	  return   this.mstrBuyersName;
	}
	public String getIsCardNameNBuyerNameMatch()
	{
		return this.mstrIsCardNameNBuyerNameMatch;
	}

}