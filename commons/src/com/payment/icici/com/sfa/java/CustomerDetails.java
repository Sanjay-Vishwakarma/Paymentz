package com.payment.icici.com.sfa.java;
public class CustomerDetails extends Address
{
   private Address mstrOfficeAddress=null;
   private Address mstrHomeAddress=null;
   private String mstrMobileNo=null;
   private String mstrFirstName=null;
   private String mstrLastName=null;
   private String mstrRegDate = null;
   private String mstrIsBillNShipAddrMatch= null;
   private String custIsAvailable = null;

   public void setCustomerDetails(String astrFirstName,String astrLastName,Address astrOfficeAddress, Address astrHomeAddress, String astrMobileNo, String astrRegDate,String astrIsBillNShipAddrMatch)
   {
     this.mstrOfficeAddress=astrOfficeAddress;
     this.mstrHomeAddress=astrHomeAddress;
     this.mstrMobileNo=astrMobileNo;
     this.mstrFirstName=astrFirstName;
     this.mstrLastName=astrLastName;
     this.mstrRegDate=astrRegDate;
     this.mstrIsBillNShipAddrMatch=astrIsBillNShipAddrMatch;
     this.custIsAvailable = "YES";
   }
   public String getFirstName()
   {
	   return this.mstrFirstName;
   }
   public String getCustAvailFlag()
   {
	   return this.custIsAvailable;
   }
   public String getLastName()
   {
	   return this.mstrLastName;
   }
   public String getMobileNo()
   {
	   return this.mstrMobileNo;
   }
   public String getRegDate()
   {
	   return this.mstrRegDate;
   }
   public String getBillNShipAddrMatch()
   {
     return this.mstrIsBillNShipAddrMatch;
   }

  public Address getOfficeAddress()
  {
    return this.mstrOfficeAddress;
  }
  public Address getHomeAddress()
  {
    return this.mstrHomeAddress;
  }



}