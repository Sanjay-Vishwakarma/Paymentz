/**
 * UGSinterface.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.ugspay.message;

public interface UGSinterface extends javax.xml.rpc.Service {
    public java.lang.String getInterfacePortAddress();

    public com.directi.pg.core.ugspay.message.InterfacePortType getInterfacePort() throws javax.xml.rpc.ServiceException;

    public com.directi.pg.core.ugspay.message.InterfacePortType getInterfacePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
