/**
 * APISoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public interface APISoap extends java.rmi.Remote {

    /**
     * Returns SessionID
     */
    public java.lang.String addSessionID(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns SessionID
     */
    //public com.payment.Oculus.com.MyCardStorage.AddSession_Response addSessionID_Soap(com.MyCardStorage.ServiceSecurity serviceSecurity) throws java.rmi.RemoteException;

    /**
     * New Booking Request
     */
    public java.lang.String bookingRequest(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * New Booking Request
     */
    //public com.MyCardStorage.Booking_Response bookingRequest_Soap(com.MyCardStorage.Booking booking) throws java.rmi.RemoteException;

    /**
     * Credit Sale
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditSale_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardSale) throws java.rmi.RemoteException;

    /**
     * Credit Auth
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditAuth_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardAuth) throws java.rmi.RemoteException;

    /**
     * Credit Capture
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditCapture_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardCapture) throws java.rmi.RemoteException;

    /**
     * Credit Sale
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult addTokenCreditSale_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardSale) throws java.rmi.RemoteException;

    /**
     * Credit Refund
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditCredit_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardCredit) throws java.rmi.RemoteException;

    /**
     * Credit Void
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditVoid_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardVoid) throws java.rmi.RemoteException;

    /**
     * Returns MCS Token
     */
    public java.lang.String addCOF(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns MCS Token
     */
    public com.payment.Oculus.com.MyCardStorage.AddToken_Response addCOF_Soap(com.payment.Oculus.com.MyCardStorage.AddToken addToken) throws java.rmi.RemoteException;

    /**
     * Returns MCS Token
     */
    public java.lang.String updateCOF(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns MCS Token
     */
    public com.payment.Oculus.com.MyCardStorage.AddToken_Response updateCOF_Soap(com.payment.Oculus.com.MyCardStorage.AddToken addToken) throws java.rmi.RemoteException;

    /**
     * Returns MCS Token
     */
    public java.lang.String removeToken(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Credit Sale MCS Token
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditSale_Token_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardSale) throws java.rmi.RemoteException;

    /**
     * Credit Credit MCS Token
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditCredit_Token_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardCredit) throws java.rmi.RemoteException;

    /**
     * Credit Void MCS Token
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditVoid_Token_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardVoid) throws java.rmi.RemoteException;

    /**
     * Returns MCS Token
     */
    public java.lang.String addToken(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns MCS Token
     */
    public com.payment.Oculus.com.MyCardStorage.AddToken_Response addToken_Soap(com.payment.Oculus.com.MyCardStorage.AddToken addToken) throws java.rmi.RemoteException;

    /**
     * Credit Sale 3rd Party Token
     */
    public java.lang.String creditSale_3RdPartyToken(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Credit Sale 3rd Party Token
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditSale_3RdPartyToken_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardSale) throws java.rmi.RemoteException;

    /**
     * Add 3rd Party Token and Run Credit Sale
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult addTokenCreditSale_3RdPartyToken_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardSale) throws java.rmi.RemoteException;

    /**
     * Credit Credit 3rd Party Token
     */
    public java.lang.String creditCredit_3RdPartyToken(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Credit Credit 3rd Party Token
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditCredit_3RdPartyToken_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardSale) throws java.rmi.RemoteException;

    /**
     * Credit Void 3rd Party Token
     */
    public java.lang.String creditVoid_3RdPartyToken(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Credit Void 3rd Party Token
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionResult creditVoid_3RdPartyToken_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardTransaction creditCardVoid) throws java.rmi.RemoteException;

    /**
     * Returns Open Sessions
     */
    public java.lang.String deviceIDLookUpTest(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns Open Sessions
     */
    public java.lang.String deviceIDLookUp(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns Open Sessions
     */
    public java.lang.String deviceIDLookUpWithTip(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns Open Sessions
     */
    public java.lang.String deviceIDLookUp_TAuth(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns SessionID
     */
    public java.lang.String addEMVSessionID(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns SessionID
     */
    public java.lang.String createTerminalSession(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns Token
     */
    public java.lang.String retrieveTerminalTokenData(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Secure Data from Terminal
     */
    public java.lang.String terminalDataTransfer(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Secure Data from Terminal
     */
    public java.lang.String terminalDataTransfer_TAuth(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Returns Token
     */
    public java.lang.String retrieveEMVTokenData(java.lang.String xml) throws java.rmi.RemoteException;

    /**
     * Upload Batch Credit Card File
     */
    //public com.payment.Oculus.com.MyCardStorage.CreditCardBatchFileUploadResult creditCardBatchFileUpload_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardBatchFile batchFile) throws java.rmi.RemoteException;

    /**
     * Upload Batch Credit Card File Get Results
     */
    //public com.payment.Oculus.com.MyCardStorage.CreditCardBatchFileResult creditCardBatchFileResult_Soap(com.payment.Oculus.com.MyCardStorage.CreditCardBatchFile batchFile) throws java.rmi.RemoteException;

    /**
     * Returns Chargebacks
     */
    //public com.payment.Oculus.com.MyCardStorage.ChargeBack_Response getChargebacks_Soap(com.MyCardStorage.ChargeBackRequest chargeBackRequest) throws java.rmi.RemoteException;

    /**
     * Returns Token
     */
    //public java.lang.String retrieveTokenData(java.lang.String xml) throws java.rmi.RemoteException;
}
