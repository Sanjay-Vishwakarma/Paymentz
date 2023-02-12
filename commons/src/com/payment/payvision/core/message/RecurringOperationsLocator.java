/**
 * RecurringOperationsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payvision.core.message;

public class RecurringOperationsLocator extends org.apache.axis.client.Service implements com.payment.payvision.core.message.RecurringOperations {

    public RecurringOperationsLocator() {
    }


    public RecurringOperationsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public RecurringOperationsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for RecurringOperationsSoap
    private java.lang.String RecurringOperationsSoap_address = "https://testprocessor.payvisionservices.com/Gateway/RecurringOperations.asmx";

    public java.lang.String getRecurringOperationsSoapAddress() {
        return RecurringOperationsSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RecurringOperationsSoapWSDDServiceName = "RecurringOperationsSoap";

    public java.lang.String getRecurringOperationsSoapWSDDServiceName() {
        return RecurringOperationsSoapWSDDServiceName;
    }

    public void setRecurringOperationsSoapWSDDServiceName(java.lang.String name) {
        RecurringOperationsSoapWSDDServiceName = name;
    }

    public com.payment.payvision.core.message.RecurringOperationsSoap getRecurringOperationsSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RecurringOperationsSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRecurringOperationsSoap(endpoint);
    }

    public com.payment.payvision.core.message.RecurringOperationsSoap getRecurringOperationsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.payvision.core.message.RecurringOperationsSoapStub _stub = new com.payment.payvision.core.message.RecurringOperationsSoapStub(portAddress, this);
            _stub.setPortName(getRecurringOperationsSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRecurringOperationsSoapEndpointAddress(java.lang.String address) {
        RecurringOperationsSoap_address = address;
    }


    // Use to get a proxy class for RecurringOperationsSoap12
    private java.lang.String RecurringOperationsSoap12_address = "https://testprocessor.payvisionservices.com/Gateway/RecurringOperations.asmx";

    public java.lang.String getRecurringOperationsSoap12Address() {
        return RecurringOperationsSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RecurringOperationsSoap12WSDDServiceName = "RecurringOperationsSoap12";

    public java.lang.String getRecurringOperationsSoap12WSDDServiceName() {
        return RecurringOperationsSoap12WSDDServiceName;
    }

    public void setRecurringOperationsSoap12WSDDServiceName(java.lang.String name) {
        RecurringOperationsSoap12WSDDServiceName = name;
    }

    public com.payment.payvision.core.message.RecurringOperationsSoap getRecurringOperationsSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RecurringOperationsSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRecurringOperationsSoap12(endpoint);
    }

    public com.payment.payvision.core.message.RecurringOperationsSoap getRecurringOperationsSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.payvision.core.message.RecurringOperationsSoap12Stub _stub = new com.payment.payvision.core.message.RecurringOperationsSoap12Stub(portAddress, this);
            _stub.setPortName(getRecurringOperationsSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRecurringOperationsSoap12EndpointAddress(java.lang.String address) {
        RecurringOperationsSoap12_address = address;
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
            if (com.payment.payvision.core.message.RecurringOperationsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.payvision.core.message.RecurringOperationsSoapStub _stub = new com.payment.payvision.core.message.RecurringOperationsSoapStub(new java.net.URL(RecurringOperationsSoap_address), this);
                _stub.setPortName(getRecurringOperationsSoapWSDDServiceName());
                return _stub;
            }
            if (com.payment.payvision.core.message.RecurringOperationsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.payvision.core.message.RecurringOperationsSoap12Stub _stub = new com.payment.payvision.core.message.RecurringOperationsSoap12Stub(new java.net.URL(RecurringOperationsSoap12_address), this);
                _stub.setPortName(getRecurringOperationsSoap12WSDDServiceName());
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
        if ("RecurringOperationsSoap".equals(inputPortName)) {
            return getRecurringOperationsSoap();
        }
        else if ("RecurringOperationsSoap12".equals(inputPortName)) {
            return getRecurringOperationsSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://payvision.com/gateway/", "RecurringOperations");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://payvision.com/gateway/", "RecurringOperationsSoap"));
            ports.add(new javax.xml.namespace.QName("http://payvision.com/gateway/", "RecurringOperationsSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("RecurringOperationsSoap".equals(portName)) {
            setRecurringOperationsSoapEndpointAddress(address);
        }
        else 
if ("RecurringOperationsSoap12".equals(portName)) {
            setRecurringOperationsSoap12EndpointAddress(address);
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
