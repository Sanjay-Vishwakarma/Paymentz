/**
 * Gold24Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.gold24.core.message;

public interface Gold24Service extends javax.xml.rpc.Service {
    public java.lang.String getInterfacePortAddress();

    public IGold24Payment getInterfacePort() throws javax.xml.rpc.ServiceException;

    public IGold24Payment getInterfacePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
