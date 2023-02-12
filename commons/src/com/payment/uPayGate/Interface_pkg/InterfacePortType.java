/**
 * InterfacePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.uPayGate.Interface_pkg;

public interface InterfacePortType extends java.rmi.Remote {

    /**
     * Process a sale transaction
     */
    public com.payment.uPayGate.Interface_pkg.AuthorizationResponse saleTransaction(com.payment.uPayGate.Interface_pkg.DirectAuthorizationRequest request) throws java.rmi.RemoteException;

    /**
     * Process an authorize transaction
     */
    public com.payment.uPayGate.Interface_pkg.AuthorizationResponse authorizeTransaction(com.payment.uPayGate.Interface_pkg.DirectAuthorizationRequest request) throws java.rmi.RemoteException;

    /**
     * Process a cancel transaction
     */
    public Response cancelTransaction(ReferralRequest request) throws java.rmi.RemoteException;

    /**
     * S3D Data Submit
     */
    public com.payment.uPayGate.Interface_pkg.S3DDataSubmitResponse s3DDataSubmit(com.payment.uPayGate.Interface_pkg.S3DDataSubmitRequest request) throws java.rmi.RemoteException;

    /**
     * Capture a transaction
     */
    public Response captureTransaction(com.payment.uPayGate.Interface_pkg.ReferralDetailedRequest request) throws java.rmi.RemoteException;

    /**
     * Process CFT (Credit fund transfer) a transaction
     */
    public com.payment.uPayGate.Interface_pkg.AuthorizationResponse creditTransaction(CreditRequest request) throws java.rmi.RemoteException;

    /**
     * Rebill a transaction
     */
    public RebillResponse rebillTransaction(com.payment.uPayGate.Interface_pkg.ReferralDetailedRequest request) throws java.rmi.RemoteException;

    /**
     * Refund a transaction
     */
    public Response refundTransaction(com.payment.uPayGate.Interface_pkg.ReferralDetailedRequest request) throws java.rmi.RemoteException;

    /**
     * Gets transaction information
     */
    public com.payment.uPayGate.Interface_pkg.OrderStatusResponse orderStatus(com.payment.uPayGate.Interface_pkg.OrderStatusRequest request) throws java.rmi.RemoteException;
}
