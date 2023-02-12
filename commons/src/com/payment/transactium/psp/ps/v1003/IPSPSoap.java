/**
 * IPSPSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003;

import com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.*;

public interface IPSPSoap extends java.rmi.Remote {
    public Response sale(Request request) throws java.rmi.RemoteException;
    public Response preAuthorisation(Request request) throws java.rmi.RemoteException;
    public Response completion(Request request) throws java.rmi.RemoteException;
    public Response refund(Request request) throws java.rmi.RemoteException;
    public Response CFT(Request request) throws java.rmi.RemoteException;
    public Response _void(Request request) throws java.rmi.RemoteException;
    public Response threeDSSale(Request request) throws java.rmi.RemoteException;
    public Response threeDSPreAuthorisation(Request request) throws java.rmi.RemoteException;
    public Response threeDSFinalisation(Request request) throws java.rmi.RemoteException;
    public Response process(RequestType requestType, Request request) throws java.rmi.RemoteException;
    public CloseBatchResponse closeBatch(RequestAuthenticationType auth, String accountNumber, String terminalNumber) throws java.rmi.RemoteException;
    public Response processMAP(RequestAuthenticationType auth, String txnId, String reason, Boolean active, Integer amount) throws java.rmi.RemoteException;
    public Response checkTransaction(RequestAuthenticationType auth, String merchantReference) throws java.rmi.RemoteException;
    public StoreTransactionResponse storeTransaction(StoreTransactionRequest request) throws java.rmi.RemoteException;
    public StoreTransactionResponse alterTransaction(String transactionId, StoreTransactionRequest request) throws java.rmi.RemoteException;
    public String getVersion() throws java.rmi.RemoteException;
    public MPIVEResponse MPIVerifyEnrollment(MPIVERequest request) throws java.rmi.RemoteException;
    public MPIPAResponse MPIPayerAuthentication(MPIPARequest request) throws java.rmi.RemoteException;
}
