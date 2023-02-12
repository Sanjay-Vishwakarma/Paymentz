/**
 * Webservicerefund.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer;

public interface Webservicerefund extends java.rmi.Remote {
    public java.lang.String refund(java.lang.String customerpaymentpagetext, java.lang.String orderdescription, java.lang.String referralOrderReference, java.lang.String comment1, java.lang.String cardHolderIP) throws java.rmi.RemoteException, com.payment.ipaydna.core.message.ipayRefund.coldfusion.xml.rpc.CFCInvocationException;
}
