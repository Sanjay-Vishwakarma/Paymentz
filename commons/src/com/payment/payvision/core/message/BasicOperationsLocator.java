/**
 * BasicOperationsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payvision.core.message;

public class BasicOperationsLocator extends org.apache.axis.client.Service implements com.payment.payvision.core.message.BasicOperations {

    public BasicOperationsLocator() {
    }


    public BasicOperationsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public BasicOperationsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicOperationsSoap12
    private java.lang.String BasicOperationsSoap12_address = "https://testprocessor.payvisionservices.com/Gateway/BasicOperations.asmx";

    public java.lang.String getBasicOperationsSoap12Address() {
        return BasicOperationsSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicOperationsSoap12WSDDServiceName = "BasicOperationsSoap12";

    public java.lang.String getBasicOperationsSoap12WSDDServiceName() {
        return BasicOperationsSoap12WSDDServiceName;
    }

    public void setBasicOperationsSoap12WSDDServiceName(java.lang.String name) {
        BasicOperationsSoap12WSDDServiceName = name;
    }

    public com.payment.payvision.core.message.BasicOperationsSoap getBasicOperationsSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicOperationsSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicOperationsSoap12(endpoint);
    }

    public com.payment.payvision.core.message.BasicOperationsSoap getBasicOperationsSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.payvision.core.message.BasicOperationsSoap12Stub _stub = new com.payment.payvision.core.message.BasicOperationsSoap12Stub(portAddress, this);
            _stub.setPortName(getBasicOperationsSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicOperationsSoap12EndpointAddress(java.lang.String address) {
        BasicOperationsSoap12_address = address;
    }


    // Use to get a proxy class for BasicOperationsSoap
    private java.lang.String BasicOperationsSoap_address = "https://testprocessor.payvisionservices.com/Gateway/BasicOperations.asmx";

    public java.lang.String getBasicOperationsSoapAddress() {
        return BasicOperationsSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicOperationsSoapWSDDServiceName = "BasicOperationsSoap";

    public java.lang.String getBasicOperationsSoapWSDDServiceName() {
        return BasicOperationsSoapWSDDServiceName;
    }

    public void setBasicOperationsSoapWSDDServiceName(java.lang.String name) {
        BasicOperationsSoapWSDDServiceName = name;
    }

    public com.payment.payvision.core.message.BasicOperationsSoap getBasicOperationsSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicOperationsSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicOperationsSoap(endpoint);
    }

    public com.payment.payvision.core.message.BasicOperationsSoap getBasicOperationsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.payvision.core.message.BasicOperationsSoapStub _stub = new com.payment.payvision.core.message.BasicOperationsSoapStub(portAddress, this);
            _stub.setPortName(getBasicOperationsSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicOperationsSoapEndpointAddress(java.lang.String address) {
        BasicOperationsSoap_address = address;
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
            if (com.payment.payvision.core.message.BasicOperationsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.payvision.core.message.BasicOperationsSoap12Stub _stub = new com.payment.payvision.core.message.BasicOperationsSoap12Stub(new java.net.URL(BasicOperationsSoap12_address), this);
                _stub.setPortName(getBasicOperationsSoap12WSDDServiceName());
                return _stub;
            }
            if (com.payment.payvision.core.message.BasicOperationsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.payvision.core.message.BasicOperationsSoapStub _stub = new com.payment.payvision.core.message.BasicOperationsSoapStub(new java.net.URL(BasicOperationsSoap_address), this);
                _stub.setPortName(getBasicOperationsSoapWSDDServiceName());
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
        if ("BasicOperationsSoap12".equals(inputPortName)) {
            return getBasicOperationsSoap12();
        }
        else if ("BasicOperationsSoap".equals(inputPortName)) {
            return getBasicOperationsSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://payvision.com/gateway/", "BasicOperations");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://payvision.com/gateway/", "BasicOperationsSoap12"));
            ports.add(new javax.xml.namespace.QName("http://payvision.com/gateway/", "BasicOperationsSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicOperationsSoap12".equals(portName)) {
            setBasicOperationsSoap12EndpointAddress(address);
        }
        else 
if ("BasicOperationsSoap".equals(portName)) {
            setBasicOperationsSoapEndpointAddress(address);
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
