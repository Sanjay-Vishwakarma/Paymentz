/**
 * IGold24Payment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.gold24.core.message;

public interface IGold24Payment extends java.rmi.Remote {

    /**
     * S3D Check
     */
    public com.payment.gold24.core.message.SalesResponse sales(com.payment.gold24.core.message.SalesRequest in) throws java.rmi.RemoteException;

    /**
     * Process a sale transaction
     */
    public com.payment.gold24.core.message.AuthorizeResponse authorize(com.payment.gold24.core.message.AuthorizeRequest in) throws java.rmi.RemoteException;

    /**
     * Continue 3D payment
     */
    public com.payment.gold24.core.message.S3DDataSubmitResponse s3DDataSubmit(com.payment.gold24.core.message.S3DDataSubmitRequest in) throws java.rmi.RemoteException;

    /**
     * Refund a transaction
     */
    public com.payment.gold24.core.message.RefundResponse refund(com.payment.gold24.core.message.RefundRequest in) throws java.rmi.RemoteException;

    /**
     * Rebill a transaction
     */
    public com.payment.gold24.core.message.RebillResponse rebill(com.payment.gold24.core.message.RebillRequest in) throws java.rmi.RemoteException;

    /**
     * Capture a transaction
     */
    public com.payment.gold24.core.message.CaptureResponse capture(com.payment.gold24.core.message.CaptureRequest request) throws java.rmi.RemoteException;

    /**
     * Cancel a transaction
     */
    public com.payment.gold24.core.message.CancelResponse cancel(com.payment.gold24.core.message.CancelRequest request) throws java.rmi.RemoteException;
}
