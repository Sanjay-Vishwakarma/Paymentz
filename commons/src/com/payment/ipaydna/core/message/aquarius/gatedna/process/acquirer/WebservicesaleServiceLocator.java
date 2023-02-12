/**
 * WebservicesaleServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer;

public class WebservicesaleServiceLocator extends org.apache.axis.client.Service implements com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleService {

    public WebservicesaleServiceLocator() {
    }


    public WebservicesaleServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WebservicesaleServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WebservicesaleCfc
    private java.lang.String WebservicesaleCfc_address = "https://www.ipaydna.info/aquarius/gatedna/process/acquirer/webservicesale.cfc";

    public java.lang.String getWebservicesaleCfcAddress() {
        return WebservicesaleCfc_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WebservicesaleCfcWSDDServiceName = "webservicesale.cfc";

    public java.lang.String getWebservicesaleCfcWSDDServiceName() {
        return WebservicesaleCfcWSDDServiceName;
    }

    public void setWebservicesaleCfcWSDDServiceName(java.lang.String name) {
        WebservicesaleCfcWSDDServiceName = name;
    }

    public com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.Webservicesale getWebservicesaleCfc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WebservicesaleCfc_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWebservicesaleCfc(endpoint);
    }

    public com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.Webservicesale getWebservicesaleCfc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub _stub = new com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub(portAddress, this);
            _stub.setPortName(getWebservicesaleCfcWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWebservicesaleCfcEndpointAddress(java.lang.String address) {
        WebservicesaleCfc_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.Webservicesale.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub _stub = new com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub(new java.net.URL(WebservicesaleCfc_address), this);
                _stub.setPortName(getWebservicesaleCfcWSDDServiceName());
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
        if ("webservicesale.cfc".equals(inputPortName)) {
            return getWebservicesaleCfc();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://acquirer.process.gatedna.aquarius", "webservicesaleService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://acquirer.process.gatedna.aquarius", "webservicesale.cfc"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WebservicesaleCfc".equals(portName)) {
            setWebservicesaleCfcEndpointAddress(address);
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
