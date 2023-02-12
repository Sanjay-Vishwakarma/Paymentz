/**
 * TransactionQuery.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer;

public interface TransactionQuery extends java.rmi.Remote {
    public java.lang.String getRecord(java.lang.String customerPaymentPageText, java.lang.String orderReference, java.lang.String orderDescription) throws java.rmi.RemoteException, com.payment.ipaydna.core.message.ipayInquiry.coldfusion.xml.rpc.CFCInvocationException;
}
