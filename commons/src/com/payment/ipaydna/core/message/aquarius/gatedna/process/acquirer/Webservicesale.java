/**
 * Webservicesale.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer;

public interface Webservicesale extends java.rmi.Remote {
    public java.lang.String payment(java.lang.String customerpaymentpagetext, java.lang.String orderdescription, java.lang.String orderDetail, java.lang.String currencytext, java.lang.String purchaseamount, java.lang.String taxamount, java.lang.String shippingamount, java.lang.String dutyamount, java.lang.String cardholdername, java.lang.String cardno, java.lang.String cardtypetext, java.lang.String securitycode, java.lang.String cardexpiremonth, java.lang.String cardexpireyear, java.lang.String cardissuemonth, java.lang.String cardissueyear, java.lang.String issuername, java.lang.String firstname, java.lang.String lastname, java.lang.String company, java.lang.String address, java.lang.String city, java.lang.String state, java.lang.String zip, java.lang.String country, java.lang.String email, java.lang.String phone, java.lang.String shipfirstname, java.lang.String shiplastname, java.lang.String shipaddress, java.lang.String shipcity, java.lang.String shipstate, java.lang.String shipzip, java.lang.String shipcountry, java.lang.String cardHolderIP) throws java.rmi.RemoteException, com.payment.ipaydna.core.message.coldfusion.xml.rpc.CFCInvocationException;
}
