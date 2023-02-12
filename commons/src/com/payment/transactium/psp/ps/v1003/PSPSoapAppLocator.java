/**
 * PSPSoapAppLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003;

public class PSPSoapAppLocator extends org.apache.axis.client.Service implements com.payment.transactium.psp.ps.v1003.PSPSoapApp {

    // Use to get a proxy class for BasicHttpBinding_IPSPSoap
    private String BasicHttpBinding_IPSPSoap_address = "";
    // The WSDD service name defaults to the port name.
    private String BasicHttpBinding_IPSPSoapWSDDServiceName = "BasicHttpBinding_IPSPSoap";
    private java.util.HashSet ports = null;

    public PSPSoapAppLocator() {
    }

    public PSPSoapAppLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PSPSoapAppLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    public void setBasicHttpBinding_IPSPSoapEndpointAddress(String address) {
        this.BasicHttpBinding_IPSPSoap_address = address;
    }

    public String getBasicHttpBinding_IPSPSoapAddress() {
        return BasicHttpBinding_IPSPSoap_address;
    }

    public String getBasicHttpBinding_IPSPSoapWSDDServiceName() {
        return BasicHttpBinding_IPSPSoapWSDDServiceName;
    }

    public void setBasicHttpBinding_IPSPSoapWSDDServiceName(String name) {
        BasicHttpBinding_IPSPSoapWSDDServiceName = name;
    }

    public com.payment.transactium.psp.ps.v1003.IPSPSoap getBasicHttpBinding_IPSPSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_IPSPSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_IPSPSoap(endpoint);
    }

    public com.payment.transactium.psp.ps.v1003.IPSPSoap getBasicHttpBinding_IPSPSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub _stub = new com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_IPSPSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.payment.transactium.psp.ps.v1003.IPSPSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub _stub = new com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub(new java.net.URL(BasicHttpBinding_IPSPSoap_address), this);
                _stub.setPortName(getBasicHttpBinding_IPSPSoapWSDDServiceName());
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
        if ("BasicHttpBinding_IPSPSoap".equals(inputPortName)) {
            return getBasicHttpBinding_IPSPSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://psp.transactium.com/ps/v1003", "PSPSoapApp");
    }

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://psp.transactium.com/ps/v1003", "BasicHttpBinding_IPSPSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_IPSPSoap".equals(portName)) {
            setBasicHttpBinding_IPSPSoapEndpointAddress(address);
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
