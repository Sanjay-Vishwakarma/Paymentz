/**
 * CcgwwsdlPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.Guardian;

public interface CcgwwsdlPortType extends java.rmi.Remote {

    /**
     * Return Transaction Response
     */
    public Response newTransaction(Request data) throws java.rmi.RemoteException;

    /**
     * Return Refund Response
     */
    public ResponseRefund refund(RequestRefund data) throws java.rmi.RemoteException;
}
