/**
 * MerchantLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.alliedwalled.core.message.com._381808.service;

public class MerchantLocator extends org.apache.axis.client.Service implements com.payment.alliedwalled.core.message.com._381808.service.Merchant {

    public MerchantLocator() {
    }


    public MerchantLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MerchantLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MerchantSoap12
    private java.lang.String MerchantSoap12_address = "https://service.381808.com/Merchant.asmx";

    public java.lang.String getMerchantSoap12Address() {
        return MerchantSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MerchantSoap12WSDDServiceName = "MerchantSoap12";

    public java.lang.String getMerchantSoap12WSDDServiceName() {
        return MerchantSoap12WSDDServiceName;
    }

    public void setMerchantSoap12WSDDServiceName(java.lang.String name) {
        MerchantSoap12WSDDServiceName = name;
    }

    public com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap getMerchantSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MerchantSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMerchantSoap12(endpoint);
    }

    public com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap getMerchantSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub _stub = new com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub(portAddress, this);
            _stub.setPortName(getMerchantSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMerchantSoap12EndpointAddress(java.lang.String address) {
        MerchantSoap12_address = address;
    }


    // Use to get a proxy class for MerchantSoap
    private java.lang.String MerchantSoap_address = "https://service.381808.com/Merchant.asmx";

    public java.lang.String getMerchantSoapAddress() {
        return MerchantSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MerchantSoapWSDDServiceName = "MerchantSoap";

    public java.lang.String getMerchantSoapWSDDServiceName() {
        return MerchantSoapWSDDServiceName;
    }

    public void setMerchantSoapWSDDServiceName(java.lang.String name) {
        MerchantSoapWSDDServiceName = name;
    }

    public com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap getMerchantSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MerchantSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMerchantSoap(endpoint);
    }

    public com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap getMerchantSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub _stub = new com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub(portAddress, this);
            _stub.setPortName(getMerchantSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMerchantSoapEndpointAddress(java.lang.String address) {
        MerchantSoap_address = address;
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
            if (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub _stub = new com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub(new java.net.URL(MerchantSoap12_address), this);
                _stub.setPortName(getMerchantSoap12WSDDServiceName());
                return _stub;
            }
            if (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub _stub = new com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub(new java.net.URL(MerchantSoap_address), this);
                _stub.setPortName(getMerchantSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
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
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MerchantSoap12".equals(inputPortName)) {
            return getMerchantSoap12();
        }
        else if ("MerchantSoap".equals(inputPortName)) {
            return getMerchantSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.381808.com/", "Merchant");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.381808.com/", "MerchantSoap12"));
            ports.add(new javax.xml.namespace.QName("http://service.381808.com/", "MerchantSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MerchantSoap12".equals(portName)) {
            setMerchantSoap12EndpointAddress(address);
        }
        else 
if ("MerchantSoap".equals(portName)) {
            setMerchantSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
