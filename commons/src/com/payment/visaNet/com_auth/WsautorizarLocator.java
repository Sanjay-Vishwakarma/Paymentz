/**
 * WsautorizarLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_auth;

public class WsautorizarLocator extends org.apache.axis.client.Service implements com.payment.visaNet.com_auth.Wsautorizar {

    public WsautorizarLocator() {
    }


    public WsautorizarLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WsautorizarLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for wsautorizarSoap12
    //private java.lang.String wsautorizarSoap12_address = "http://qas.multimerchantvisanet.com/wsautorizartransaccion/wsautorizar.asmx";
    //private java.lang.String wsautorizarSoap12_address = "https://www.multimerchantvisanet.com/wsautorizartransaccion/wsautorizar.asmx";
    private java.lang.String wsautorizarSoap12_address = "";

    public void setWsautorizarSoap12_address(String wsautorizarSoap12_address)
    {
        this.wsautorizarSoap12_address = wsautorizarSoap12_address;
    }

    public java.lang.String getwsautorizarSoap12Address() {
        return wsautorizarSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String wsautorizarSoap12WSDDServiceName = "wsautorizarSoap12";

    public java.lang.String getwsautorizarSoap12WSDDServiceName() {
        return wsautorizarSoap12WSDDServiceName;
    }

    public void setwsautorizarSoap12WSDDServiceName(java.lang.String name) {
        wsautorizarSoap12WSDDServiceName = name;
    }

    public com.payment.visaNet.com_auth.WsautorizarSoap getwsautorizarSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(wsautorizarSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getwsautorizarSoap12(endpoint);
    }

    public com.payment.visaNet.com_auth.WsautorizarSoap getwsautorizarSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.visaNet.com_auth.WsautorizarSoap12Stub _stub = new com.payment.visaNet.com_auth.WsautorizarSoap12Stub(portAddress, this);
            _stub.setPortName(getwsautorizarSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setwsautorizarSoap12EndpointAddress(java.lang.String address) {
        wsautorizarSoap12_address = address;
    }


    // Use to get a proxy class for wsautorizarSoap
    //private java.lang.String wsautorizarSoap_address = "http://qas.multimerchantvisanet.com/wsautorizartransaccion/wsautorizar.asmx";
    //private java.lang.String wsautorizarSoap_address = "https://www.multimerchantvisanet.com/wsautorizartransaccion/wsautorizar.asmx";
    private java.lang.String wsautorizarSoap_address = "";

    public void setWsautorizarSoap_address(String wsautorizarSoap_address)
    {
        this.wsautorizarSoap_address = wsautorizarSoap_address;
    }

    public java.lang.String getwsautorizarSoapAddress() {
        return wsautorizarSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String wsautorizarSoapWSDDServiceName = "wsautorizarSoap";

    public java.lang.String getwsautorizarSoapWSDDServiceName() {
        return wsautorizarSoapWSDDServiceName;
    }

    public void setwsautorizarSoapWSDDServiceName(java.lang.String name) {
        wsautorizarSoapWSDDServiceName = name;
    }

    public com.payment.visaNet.com_auth.WsautorizarSoap getwsautorizarSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(wsautorizarSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getwsautorizarSoap(endpoint);
    }

    public com.payment.visaNet.com_auth.WsautorizarSoap getwsautorizarSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.visaNet.com_auth.WsautorizarSoapStub _stub = new com.payment.visaNet.com_auth.WsautorizarSoapStub(portAddress, this);
            _stub.setPortName(getwsautorizarSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setwsautorizarSoapEndpointAddress(java.lang.String address) {
        wsautorizarSoap_address = address;
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
            if (com.payment.visaNet.com_auth.WsautorizarSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.visaNet.com_auth.WsautorizarSoap12Stub _stub = new com.payment.visaNet.com_auth.WsautorizarSoap12Stub(new java.net.URL(wsautorizarSoap12_address), this);
                _stub.setPortName(getwsautorizarSoap12WSDDServiceName());
                return _stub;
            }
            if (com.payment.visaNet.com_auth.WsautorizarSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.visaNet.com_auth.WsautorizarSoapStub _stub = new com.payment.visaNet.com_auth.WsautorizarSoapStub(new java.net.URL(wsautorizarSoap_address), this);
                _stub.setPortName(getwsautorizarSoapWSDDServiceName());
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
        if ("wsautorizarSoap12".equals(inputPortName)) {
            return getwsautorizarSoap12();
        }
        else if ("wsautorizarSoap".equals(inputPortName)) {
            return getwsautorizarSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/autorizartransaccion", "wsautorizar");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/autorizartransaccion", "wsautorizarSoap12"));
            ports.add(new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/autorizartransaccion", "wsautorizarSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("wsautorizarSoap12".equals(portName)) {
            setwsautorizarSoap12EndpointAddress(address);
        }
        else 
if ("wsautorizarSoap".equals(portName)) {
            setwsautorizarSoapEndpointAddress(address);
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
