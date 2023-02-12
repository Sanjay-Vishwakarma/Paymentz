/**
 * PayHOSTServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class PayHOSTServiceLocator extends org.apache.axis.client.Service implements com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTService {

    public PayHOSTServiceLocator() {
    }


    public PayHOSTServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PayHOSTServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PayHOSTSoap11
    private java.lang.String PayHOSTSoap11_address = "https://secure.paygate.co.za/PayHost/process.trans";

    public java.lang.String getPayHOSTSoap11Address() {
        return PayHOSTSoap11_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PayHOSTSoap11WSDDServiceName = "PayHOSTSoap11";

    public java.lang.String getPayHOSTSoap11WSDDServiceName() {
        return PayHOSTSoap11WSDDServiceName;
    }

    public void setPayHOSTSoap11WSDDServiceName(java.lang.String name) {
        PayHOSTSoap11WSDDServiceName = name;
    }

    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOST getPayHOSTSoap11() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PayHOSTSoap11_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPayHOSTSoap11(endpoint);
    }

    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOST getPayHOSTSoap11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub _stub = new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub(portAddress, this);
            _stub.setPortName(getPayHOSTSoap11WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPayHOSTSoap11EndpointAddress(java.lang.String address) {
        PayHOSTSoap11_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOST.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub _stub = new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub(new java.net.URL(PayHOSTSoap11_address), this);
                _stub.setPortName(getPayHOSTSoap11WSDDServiceName());
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
        if ("PayHOSTSoap11".equals(inputPortName)) {
            return getPayHOSTSoap11();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayHOSTService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayHOSTSoap11"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PayHOSTSoap11".equals(portName)) {
            setPayHOSTSoap11EndpointAddress(address);
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
