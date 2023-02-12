/**
 * InterfacePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.twoGatePay.Interface_pkg;

public interface InterfacePortType extends java.rmi.Remote {

    /**
     * Sate transaction with s3d check
     */
    public com.payment.twoGatePay.Interface_pkg.SaleResponse sale(com.payment.twoGatePay.Interface_pkg.SaleRequest in) throws java.rmi.RemoteException;

    /**
     * Process an authorize transaction with s3d check
     */
    //public Interface_pkg.AuthResponse auth(Interface_pkg.AuthRequest in) throws java.rmi.RemoteException;

    /**
     * Finish S3D payment
     */
    //public Interface_pkg.S3DFinaliseResponse s3DFinalise(Interface_pkg.S3DFinaliseRequest in) throws java.rmi.RemoteException;

    /**
     * Reversal a previous transaction
     */
    public com.payment.twoGatePay.Interface_pkg.ReversalResponse reversal(com.payment.twoGatePay.Interface_pkg.ReversalRequest in) throws java.rmi.RemoteException;

    /**
     * Rebill a previous transaction
     */
    public com.payment.twoGatePay.Interface_pkg.RebillResponse rebill(com.payment.twoGatePay.Interface_pkg.RebillRequest in) throws java.rmi.RemoteException;

    /**
     * Capture a previous auth transaction
     */
    //public Interface_pkg.CaptureResponse capture(Interface_pkg.CaptureRequest request) throws java.rmi.RemoteException;

    /**
     * Cancel a previous auth transaction
     */
    //public Interface_pkg.CancelResponse cancel(Interface_pkg.CancelRequest request) throws java.rmi.RemoteException;
}
