/**
 * UGSInterfaceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.ugspay.message;

public class UGSInterfaceLocator extends org.apache.axis.client.Service implements UGSinterface
{

    public UGSInterfaceLocator() {
    }


    public UGSInterfaceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public UGSInterfaceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for InterfacePort
    private java.lang.String InterfacePort_address = "https://secure.ugspay.com:443/interface/SOAP/1.31/index.php";

    public java.lang.String getInterfacePortAddress() {
        return InterfacePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String InterfacePortWSDDServiceName = "InterfacePort";

    public java.lang.String getInterfacePortWSDDServiceName() {
        return InterfacePortWSDDServiceName;
    }

    public void setInterfacePortWSDDServiceName(java.lang.String name) {
        InterfacePortWSDDServiceName = name;
    }

    public com.directi.pg.core.ugspay.message.InterfacePortType getInterfacePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(InterfacePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getInterfacePort(endpoint);
    }

    public com.directi.pg.core.ugspay.message.InterfacePortType getInterfacePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.directi.pg.core.ugspay.message.InterfaceBindingStub _stub = new com.directi.pg.core.ugspay.message.InterfaceBindingStub(portAddress, this);
            _stub.setPortName(getInterfacePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setInterfacePortEndpointAddress(java.lang.String address) {
        InterfacePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.directi.pg.core.ugspay.message.InterfacePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.directi.pg.core.ugspay.message.InterfaceBindingStub _stub = new com.directi.pg.core.ugspay.message.InterfaceBindingStub(new java.net.URL(InterfacePort_address), this);
                _stub.setPortName(getInterfacePortWSDDServiceName());
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
        if ("InterfacePort".equals(inputPortName)) {
            return getInterfacePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:Interface", "Interface");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:Interface", "InterfacePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("InterfacePort".equals(portName)) {
            setInterfacePortEndpointAddress(address);
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
