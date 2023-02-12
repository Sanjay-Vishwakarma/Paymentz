/**
 * MerchantToolsSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.globalgate.biz.apsp.www;

public interface MerchantToolsSoap extends java.rmi.Remote {

    /**
     * Get Card Numbers used in a particular account
     */
    public String FPGetLastCardNumbers(String merchantCode, String clientAccount, String listType) throws java.rmi.RemoteException;

    /**
     * Get Transactions for specified date
     */
    public String getDailyTransactionSet(String MCHCode, String MCHPass, String sDate) throws java.rmi.RemoteException;

    /**
     * Get Transactions for specified Oref
     */
    public String getTransactionsByORef(String MCHCode, String MCHPass, String oref) throws java.rmi.RemoteException;

    /**
     * Get an XML Response for Transactions with given OREF
     */
    public GetTransactionStatusResponseGetTransactionStatusResult getTransactionStatus(String merchID, String merchPass, String ORef) throws java.rmi.RemoteException;

    /**
     * Get ARN Code For a Refund with given PSPID
     */
    public String retrieveARN(String merchID, String merchPass, String PSPID) throws java.rmi.RemoteException;

    /**
     * Get XML Hash For Fastpay Integration
     */
    public String computeHash(String merchID, String merchPass, String XML) throws java.rmi.RemoteException;

    /**
     * Build Token for Requested XML
     */
    public String buildXMLToken(String merchID, String merchPass, String XMLParam, javax.xml.rpc.holders.StringHolder errorMsg) throws java.rmi.RemoteException;

    /**
     * Get Envoy Payout Details for Different Countries
     */
    public String getBankPayoutDetails(String merchid, String merchpass, String username, String password, String country, String currency, String lang, boolean test, String sessionid) throws java.rmi.RemoteException;

    /**
     * Update User Defined Fields Value
     */
    public String updateUDF(String merchID, String merchPass, String PSPID, String UDF1, String UDF2, String UDF3) throws java.rmi.RemoteException;

    /**
     * Get Transaction Response XML
     */
    public String getTransactionXMLResponse(String merchID, String merchPass, String ORef) throws java.rmi.RemoteException;

    /**
     * Remove Card Number from Account
     */
    public String removeCardFromAccount(String MCHCode, String MCHPass, String PSPID) throws java.rmi.RemoteException;

    /**
     * Blacklisting Card by Transaction ID
     */
    public String blacklistCard(String MCHCode, String MCHPass, String PSPID, String userid) throws java.rmi.RemoteException;

    /**
     * Removing Blacklisted Card by Transaction ID
     */
    public String removeBlacklistCard(String MCHCode, String MCHPass, String PSPID) throws java.rmi.RemoteException;

    /**
     * Get All Cards with Different Statuses Used by a Client Account
     */
    public String getCardsUsedbyAccount(String mchCode, String mchPass, String clientAccount) throws java.rmi.RemoteException;
}
