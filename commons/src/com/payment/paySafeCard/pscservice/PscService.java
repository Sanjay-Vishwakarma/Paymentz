/**
 * PscService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public interface PscService extends javax.xml.rpc.Service {
    public java.lang.String getPscAddress();

    public com.payment.paySafeCard.pscservice.Psc getPsc() throws javax.xml.rpc.ServiceException;

    public com.payment.paySafeCard.pscservice.Psc getPsc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
