/**
 * MRSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

public class MRSServiceLocator extends org.apache.axis.client.Service implements MRSService {

    public MRSServiceLocator() {
    }


    public MRSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MRSServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MRSPort
    private String MRSPort_address = "http://demo.e-merchants.eu/soap/soap.php?soap";

    public String getMRSPortAddress() {
        return MRSPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String MRSPortWSDDServiceName = "MRSPort";

    public String getMRSPortWSDDServiceName() {
        return MRSPortWSDDServiceName;
    }

    public void setMRSPortWSDDServiceName(String name) {
        MRSPortWSDDServiceName = name;
    }

    public MRSPortType getMRSPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MRSPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMRSPort(endpoint);
    }

    public MRSPortType getMRSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            MRSBindingStub _stub = new MRSBindingStub(portAddress, this);
            _stub.setPortName(getMRSPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMRSPortEndpointAddress(String address) {
        MRSPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (MRSPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                MRSBindingStub _stub = new MRSBindingStub(new java.net.URL(MRSPort_address), this);
                _stub.setPortName(getMRSPortWSDDServiceName());
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
        if ("MRSPort".equals(inputPortName)) {
            return getMRSPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:Gateway", "MRSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:Gateway", "MRSPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("MRSPort".equals(portName)) {
            setMRSPortEndpointAddress(address);
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
