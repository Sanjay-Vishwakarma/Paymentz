/**
 * PscServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class PscServiceLocator extends org.apache.axis.client.Service implements com.payment.paySafeCard.pscservice.PscService {

    public PscServiceLocator() {
    }


    public PscServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PscServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Psc
    private java.lang.String Psc_address = "https://soatest.paysafecard.com/psc/services/PscService";

    public java.lang.String getPscAddress() {
        return Psc_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PscWSDDServiceName = "Psc";

    public java.lang.String getPscWSDDServiceName() {
        return PscWSDDServiceName;
    }

    public void setPscWSDDServiceName(java.lang.String name) {
        PscWSDDServiceName = name;
    }

    public com.payment.paySafeCard.pscservice.Psc getPsc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Psc_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPsc(endpoint);
    }

    public com.payment.paySafeCard.pscservice.Psc getPsc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.paySafeCard.pscservice.PscSoapBindingStub _stub = new com.payment.paySafeCard.pscservice.PscSoapBindingStub(portAddress, this);
            _stub.setPortName(getPscWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPscEndpointAddress(java.lang.String address) {
        Psc_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.payment.paySafeCard.pscservice.Psc.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.paySafeCard.pscservice.PscSoapBindingStub _stub = new com.payment.paySafeCard.pscservice.PscSoapBindingStub(new java.net.URL(Psc_address), this);
                _stub.setPortName(getPscWSDDServiceName());
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
        if ("Psc".equals(inputPortName)) {
            return getPsc();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:pscservice", "PscService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:pscservice", "Psc"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Psc".equals(portName)) {
            setPscEndpointAddress(address);
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
