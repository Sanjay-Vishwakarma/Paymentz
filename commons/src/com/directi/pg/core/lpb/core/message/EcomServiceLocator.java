/**
 * EcomServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

public class EcomServiceLocator extends org.apache.axis.client.Service implements EcomService {

    public EcomServiceLocator() {
    }


    public EcomServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EcomServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EcomPort    http://demo.ipsp.lv/api/v2/soap
    private String EcomPort_address = "https://demo.ipsp.lv/api/v2/soap?wsdl";

    public String getEcomPortAddress() {
        return EcomPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String EcomPortWSDDServiceName = "EcomPort";

    public String getEcomPortWSDDServiceName() {
        return EcomPortWSDDServiceName;
    }

    public void setEcomPortWSDDServiceName(String name) {
        EcomPortWSDDServiceName = name;
    }

    public EcomPortType getEcomPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EcomPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEcomPort(endpoint);
    }

    public EcomPortType getEcomPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            EcomBindingStub _stub = new EcomBindingStub(portAddress, this);
            _stub.setPortName(getEcomPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEcomPortEndpointAddress(String address) {
        EcomPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (EcomPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                EcomBindingStub _stub = new EcomBindingStub(new java.net.URL(EcomPort_address), this);
                _stub.setPortName(getEcomPortWSDDServiceName());
                return _stub;
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
        if ("EcomPort".equals(inputPortName)) {
            return getEcomPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:Gateway", "EcomService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:Gateway", "EcomPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("EcomPort".equals(portName)) {
            setEcomPortEndpointAddress(address);
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
