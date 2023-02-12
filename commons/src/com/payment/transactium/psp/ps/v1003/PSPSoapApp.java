/**
 * PSPSoapApp.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003;

public interface PSPSoapApp extends javax.xml.rpc.Service {
    public String getBasicHttpBinding_IPSPSoapAddress();

    public com.payment.transactium.psp.ps.v1003.IPSPSoap getBasicHttpBinding_IPSPSoap() throws javax.xml.rpc.ServiceException;

    public com.payment.transactium.psp.ps.v1003.IPSPSoap getBasicHttpBinding_IPSPSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
