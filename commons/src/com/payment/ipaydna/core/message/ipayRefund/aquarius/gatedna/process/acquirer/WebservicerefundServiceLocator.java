/**
 * WebservicerefundServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer;

public class WebservicerefundServiceLocator extends org.apache.axis.client.Service implements com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundService {

    public WebservicerefundServiceLocator() {
    }


    public WebservicerefundServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WebservicerefundServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WebservicerefundCfc
    private java.lang.String WebservicerefundCfc_address = "https://www.ipaydna.info/aquarius/gatedna/process/acquirer/webservicerefund.cfc";

    public java.lang.String getWebservicerefundCfcAddress() {
        return WebservicerefundCfc_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WebservicerefundCfcWSDDServiceName = "webservicerefund.cfc";

    public java.lang.String getWebservicerefundCfcWSDDServiceName() {
        return WebservicerefundCfcWSDDServiceName;
    }

    public void setWebservicerefundCfcWSDDServiceName(java.lang.String name) {
        WebservicerefundCfcWSDDServiceName = name;
    }

    public com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.Webservicerefund getWebservicerefundCfc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WebservicerefundCfc_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWebservicerefundCfc(endpoint);
    }

    public com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.Webservicerefund getWebservicerefundCfc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub _stub = new com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub(portAddress, this);
            _stub.setPortName(getWebservicerefundCfcWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWebservicerefundCfcEndpointAddress(java.lang.String address) {
        WebservicerefundCfc_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.Webservicerefund.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub _stub = new com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub(new java.net.URL(WebservicerefundCfc_address), this);
                _stub.setPortName(getWebservicerefundCfcWSDDServiceName());
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
        if ("webservicerefund.cfc".equals(inputPortName)) {
            return getWebservicerefundCfc();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://acquirer.process.gatedna.aquarius", "webservicerefundService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://acquirer.process.gatedna.aquarius", "webservicerefund.cfc"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WebservicerefundCfc".equals(portName)) {
            setWebservicerefundCfcEndpointAddress(address);
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
