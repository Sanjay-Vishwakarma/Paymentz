/**
 * InterfacePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.ugspay.message;

public interface InterfacePortType extends java.rmi.Remote {

    /**
     * Process a sale transaction
     */
    public com.directi.pg.core.ugspay.message.AuthorizationResponse saleTransaction(com.directi.pg.core.ugspay.message.DirectAuthorizationRequest request) throws java.rmi.RemoteException;

    /**
     * Process an authorize transaction
     */
    public com.directi.pg.core.ugspay.message.AuthorizationResponse authorizeTransaction(com.directi.pg.core.ugspay.message.DirectAuthorizationRequest request) throws java.rmi.RemoteException;

    /**
     * Process a cancel transaction
     */
    public com.directi.pg.core.ugspay.message.Response cancelTransaction(com.directi.pg.core.ugspay.message.ReferralRequest request) throws java.rmi.RemoteException;

    /**
     * S3DCheck
     */
    public com.directi.pg.core.ugspay.message.S3DcheckResponse s3DCheckTransaction(com.directi.pg.core.ugspay.message.DirectRequest request) throws java.rmi.RemoteException;

    /**
     * Capture a transaction
     */
    public com.directi.pg.core.ugspay.message.Response captureTransaction(com.directi.pg.core.ugspay.message.ReferralDetailedRequest request) throws java.rmi.RemoteException;

    /**
     * Credit a transaction
     */
    public com.directi.pg.core.ugspay.message.Response creditTransaction(com.directi.pg.core.ugspay.message.ReferralDetailedRequest request) throws java.rmi.RemoteException;

    /**
     * Rebill a transaction
     */
    public com.directi.pg.core.ugspay.message.RebillResponse rebillTransaction(com.directi.pg.core.ugspay.message.ReferralDetailedRequest request) throws java.rmi.RemoteException;

    /**
     * Refund a transaction
     */
    public com.directi.pg.core.ugspay.message.Response refundTransaction(com.directi.pg.core.ugspay.message.ReferralDetailedRequest request) throws java.rmi.RemoteException;

    /**
     * OrderStatus
     */
    public com.directi.pg.core.ugspay.message.OrderStatusResponse orderStatus(com.directi.pg.core.ugspay.message.OrderStatusRequest request) throws java.rmi.RemoteException;
}
