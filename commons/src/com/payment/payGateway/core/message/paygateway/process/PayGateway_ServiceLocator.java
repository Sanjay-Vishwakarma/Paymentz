/**
 * PayGateway_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payGateway.core.message.paygateway.process;

public class PayGateway_ServiceLocator extends org.apache.axis.client.Service implements com.payment.payGateway.core.message.paygateway.process.PayGateway_Service {

    public PayGateway_ServiceLocator() {
    }


    public PayGateway_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PayGateway_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PayGateway_ServiceSoap12
    private java.lang.String PayGateway_ServiceSoap12_address = "https://paygateway.net/api/processtx.asmx";

    public java.lang.String getPayGateway_ServiceSoap12Address() {
        return PayGateway_ServiceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PayGateway_ServiceSoap12WSDDServiceName = "PayGateway_ServiceSoap12";

    public java.lang.String getPayGateway_ServiceSoap12WSDDServiceName() {
        return PayGateway_ServiceSoap12WSDDServiceName;
    }

    public void setPayGateway_ServiceSoap12WSDDServiceName(java.lang.String name) {
        PayGateway_ServiceSoap12WSDDServiceName = name;
    }

    public com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap getPayGateway_ServiceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PayGateway_ServiceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPayGateway_ServiceSoap12(endpoint);
    }

    public com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap getPayGateway_ServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub _stub = new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub(portAddress, this);
            _stub.setPortName(getPayGateway_ServiceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPayGateway_ServiceSoap12EndpointAddress(java.lang.String address) {
        PayGateway_ServiceSoap12_address = address;
    }


    // Use to get a proxy class for PayGateway_ServiceSoap
    private java.lang.String PayGateway_ServiceSoap_address = "https://paygateway.net/api/processtx.asmx";

    public java.lang.String getPayGateway_ServiceSoapAddress() {
        return PayGateway_ServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PayGateway_ServiceSoapWSDDServiceName = "PayGateway_ServiceSoap";

    public java.lang.String getPayGateway_ServiceSoapWSDDServiceName() {
        return PayGateway_ServiceSoapWSDDServiceName;
    }

    public void setPayGateway_ServiceSoapWSDDServiceName(java.lang.String name) {
        PayGateway_ServiceSoapWSDDServiceName = name;
    }

    public com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap getPayGateway_ServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PayGateway_ServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPayGateway_ServiceSoap(endpoint);
    }

    public com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap getPayGateway_ServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub _stub = new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub(portAddress, this);
            _stub.setPortName(getPayGateway_ServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPayGateway_ServiceSoapEndpointAddress(java.lang.String address) {
        PayGateway_ServiceSoap_address = address;
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
            if (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub _stub = new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub(new java.net.URL(PayGateway_ServiceSoap12_address), this);
                _stub.setPortName(getPayGateway_ServiceSoap12WSDDServiceName());
                return _stub;
            }
            if (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub _stub = new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub(new java.net.URL(PayGateway_ServiceSoap_address), this);
                _stub.setPortName(getPayGateway_ServiceSoapWSDDServiceName());
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
        if ("PayGateway_ServiceSoap12".equals(inputPortName)) {
            return getPayGateway_ServiceSoap12();
        }
        else if ("PayGateway_ServiceSoap".equals(inputPortName)) {
            return getPayGateway_ServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://paygateway.net/", "PayGateway_Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://paygateway.net/", "PayGateway_ServiceSoap12"));
            ports.add(new javax.xml.namespace.QName("http://paygateway.net/", "PayGateway_ServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PayGateway_ServiceSoap12".equals(portName)) {
            setPayGateway_ServiceSoap12EndpointAddress(address);
        }
        else 
if ("PayGateway_ServiceSoap".equals(portName)) {
            setPayGateway_ServiceSoapEndpointAddress(address);
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
