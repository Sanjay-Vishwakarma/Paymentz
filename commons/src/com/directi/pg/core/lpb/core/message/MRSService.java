/**
 * MRSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

public interface MRSService extends javax.xml.rpc.Service {
    public String getMRSPortAddress();

    public MRSPortType getMRSPort() throws javax.xml.rpc.ServiceException;

    public MRSPortType getMRSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
