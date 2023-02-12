/**
 * CcgwwsdlLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.Guardian;

public class CcgwwsdlLocator extends org.apache.axis.client.Service implements Ccgwwsdl {

    public CcgwwsdlLocator() {
    }


    public CcgwwsdlLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CcgwwsdlLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ccgwwsdlPort
    private String ccgwwsdlPort_address = "https://gwsys1.globalsecureprocessing.com:443/webservice/server.php";

    public String getccgwwsdlPortAddress() {
        return ccgwwsdlPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String ccgwwsdlPortWSDDServiceName = "ccgwwsdlPort";

    public String getccgwwsdlPortWSDDServiceName() {
        return ccgwwsdlPortWSDDServiceName;
    }

    public void setccgwwsdlPortWSDDServiceName(String name) {
        ccgwwsdlPortWSDDServiceName = name;
    }

    public CcgwwsdlPortType getccgwwsdlPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ccgwwsdlPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getccgwwsdlPort(endpoint);
    }

    public CcgwwsdlPortType getccgwwsdlPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            CcgwwsdlBindingStub _stub = new CcgwwsdlBindingStub(portAddress, this);
            _stub.setPortName(getccgwwsdlPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setccgwwsdlPortEndpointAddress(String address) {
        ccgwwsdlPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (CcgwwsdlPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                CcgwwsdlBindingStub _stub = new CcgwwsdlBindingStub(new java.net.URL(ccgwwsdlPort_address), this);
                _stub.setPortName(getccgwwsdlPortWSDDServiceName());
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
        if ("ccgwwsdlPort".equals(inputPortName)) {
            return getccgwwsdlPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:ccgwwsdl", "ccgwwsdl");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:ccgwwsdl", "ccgwwsdlPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("ccgwwsdlPort".equals(portName)) {
            setccgwwsdlPortEndpointAddress(address);
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
