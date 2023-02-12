/**
 * API.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public interface API extends javax.xml.rpc.Service {
    public java.lang.String getAPISoapAddress();

    public com.payment.Oculus.com.MyCardStorage.APISoap getAPISoap() throws javax.xml.rpc.ServiceException;

    public com.payment.Oculus.com.MyCardStorage.APISoap getAPISoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getAPISoap12Address();

    public com.payment.Oculus.com.MyCardStorage.APISoap getAPISoap12() throws javax.xml.rpc.ServiceException;

    public com.payment.Oculus.com.MyCardStorage.APISoap getAPISoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
