/**
 * MerchantToolsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.apco.biz.apsp.www;

public class MerchantToolsLocator extends org.apache.axis.client.Service implements com.payment.apco.biz.apsp.www.MerchantTools {

    public MerchantToolsLocator() {
    }


    public MerchantToolsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MerchantToolsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MerchantToolsSoap12
    private java.lang.String MerchantToolsSoap12_address = "https://www.apsp.biz:9085/merchantTools.asmx";

    public java.lang.String getMerchantToolsSoap12Address() {
        return MerchantToolsSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MerchantToolsSoap12WSDDServiceName = "MerchantToolsSoap12";

    public java.lang.String getMerchantToolsSoap12WSDDServiceName() {
        return MerchantToolsSoap12WSDDServiceName;
    }

    public void setMerchantToolsSoap12WSDDServiceName(java.lang.String name) {
        MerchantToolsSoap12WSDDServiceName = name;
    }

    public com.payment.apco.biz.apsp.www.MerchantToolsSoap getMerchantToolsSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MerchantToolsSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return null;
    }

    public com.payment.apco.biz.apsp.www.MerchantToolsSoap getMerchantToolsSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.apco.biz.apsp.www.MerchantToolsSoap12Stub _stub = new com.payment.apco.biz.apsp.www.MerchantToolsSoap12Stub(portAddress, this);
            _stub.setPortName(getMerchantToolsSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMerchantToolsSoap12EndpointAddress(String address) {
        MerchantToolsSoap12_address = address;
    }


    // Use to get a proxy class for MerchantToolsSoap
    private String MerchantToolsSoap_address = "https://www.apsp.biz:9085/merchantTools.asmx";

    public String getMerchantToolsSoapAddress() {
        return MerchantToolsSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private String MerchantToolsSoapWSDDServiceName = "MerchantToolsSoap";

    public String getMerchantToolsSoapWSDDServiceName() {
        return MerchantToolsSoapWSDDServiceName;
    }

    public void setMerchantToolsSoapWSDDServiceName(String name) {
        MerchantToolsSoapWSDDServiceName = name;
    }

    public com.payment.apco.biz.apsp.www.MerchantToolsSoap getMerchantToolsSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MerchantToolsSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return null;
    }

    public com.payment.apco.biz.apsp.www.MerchantToolsSoap getMerchantToolsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.apco.biz.apsp.www.MerchantToolsSoapStub _stub = new com.payment.apco.biz.apsp.www.MerchantToolsSoapStub(portAddress, this);
            _stub.setPortName(getMerchantToolsSoapWSDDServiceName());
            return null;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMerchantToolsSoapEndpointAddress(String address) {
        MerchantToolsSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.payment.apco.biz.apsp.www.MerchantToolsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.apco.biz.apsp.www.MerchantToolsSoap12Stub _stub = new com.payment.apco.biz.apsp.www.MerchantToolsSoap12Stub(new java.net.URL(MerchantToolsSoap12_address), this);
                _stub.setPortName(getMerchantToolsSoap12WSDDServiceName());
                return _stub;
            }
            if (com.payment.apco.biz.apsp.www.MerchantToolsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.apco.biz.apsp.www.MerchantToolsSoapStub _stub = new com.payment.apco.biz.apsp.www.MerchantToolsSoapStub(new java.net.URL(MerchantToolsSoap_address), this);
                _stub.setPortName(getMerchantToolsSoapWSDDServiceName());
                return null;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("MerchantToolsSoap12".equals(inputPortName)) {
            return getMerchantToolsSoap12();
        }
        else if ("MerchantToolsSoap".equals(inputPortName)) {
            return null;
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://www.apsp.biz/", "MerchantTools");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://www.apsp.biz/", "MerchantToolsSoap12"));
            ports.add(new javax.xml.namespace.QName("https://www.apsp.biz/", "MerchantToolsSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("MerchantToolsSoap12".equals(portName)) {
            setMerchantToolsSoap12EndpointAddress(address);
        }
        else 
if ("MerchantToolsSoap".equals(portName)) {
            setMerchantToolsSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
