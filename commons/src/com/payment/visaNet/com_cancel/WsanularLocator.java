/**
 * WsanularLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_cancel;

public class WsanularLocator extends org.apache.axis.client.Service implements com.payment.visaNet.com_cancel.Wsanular {

    public WsanularLocator() {
    }


    public WsanularLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WsanularLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for wsanularSoap12
    //private java.lang.String wsanularSoap12_address = "http://qas.multimerchantvisanet.com/wsanulartransaccion/wsanular.asmx";
    //private java.lang.String wsanularSoap12_address = "https://www.multimerchantvisanet.com/wsanulartransaccion/wsanular.asmx";
    private java.lang.String wsanularSoap12_address = "";

    public void setWsanularSoap12_address(String wsanularSoap12_address)
    {
        this.wsanularSoap12_address = wsanularSoap12_address;
    }

    public java.lang.String getwsanularSoap12Address() {
        return wsanularSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String wsanularSoap12WSDDServiceName = "wsanularSoap12";

    public java.lang.String getwsanularSoap12WSDDServiceName() {
        return wsanularSoap12WSDDServiceName;
    }

    public void setwsanularSoap12WSDDServiceName(java.lang.String name) {
        wsanularSoap12WSDDServiceName = name;
    }

    public com.payment.visaNet.com_cancel.WsanularSoap getwsanularSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(wsanularSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getwsanularSoap12(endpoint);
    }

    public com.payment.visaNet.com_cancel.WsanularSoap getwsanularSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.visaNet.com_cancel.WsanularSoap12Stub _stub = new com.payment.visaNet.com_cancel.WsanularSoap12Stub(portAddress, this);
            _stub.setPortName(getwsanularSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setwsanularSoap12EndpointAddress(java.lang.String address) {
        wsanularSoap12_address = address;
    }


    // Use to get a proxy class for wsanularSoap
    //private java.lang.String wsanularSoap_address = "http://qas.multimerchantvisanet.com/wsanulartransaccion/wsanular.asmx";
    //private java.lang.String wsanularSoap_address = "https://www.multimerchantvisanet.com/wsanulartransaccion/wsanular.asmx";
    private java.lang.String wsanularSoap_address = "";

    public void setWsanularSoap_address(String wsanularSoap_address)
    {
        this.wsanularSoap_address = wsanularSoap_address;
    }

    public java.lang.String getwsanularSoapAddress() {
        return wsanularSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String wsanularSoapWSDDServiceName = "wsanularSoap";

    public java.lang.String getwsanularSoapWSDDServiceName() {
        return wsanularSoapWSDDServiceName;
    }

    public void setwsanularSoapWSDDServiceName(java.lang.String name) {
        wsanularSoapWSDDServiceName = name;
    }

    public com.payment.visaNet.com_cancel.WsanularSoap getwsanularSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(wsanularSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getwsanularSoap(endpoint);
    }

    public com.payment.visaNet.com_cancel.WsanularSoap getwsanularSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.visaNet.com_cancel.WsanularSoapStub _stub = new com.payment.visaNet.com_cancel.WsanularSoapStub(portAddress, this);
            _stub.setPortName(getwsanularSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setwsanularSoapEndpointAddress(java.lang.String address) {
        wsanularSoap_address = address;
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
            if (com.payment.visaNet.com_cancel.WsanularSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.visaNet.com_cancel.WsanularSoap12Stub _stub = new com.payment.visaNet.com_cancel.WsanularSoap12Stub(new java.net.URL(wsanularSoap12_address), this);
                _stub.setPortName(getwsanularSoap12WSDDServiceName());
                return _stub;
            }
            if (com.payment.visaNet.com_cancel.WsanularSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.visaNet.com_cancel.WsanularSoapStub _stub = new com.payment.visaNet.com_cancel.WsanularSoapStub(new java.net.URL(wsanularSoap_address), this);
                _stub.setPortName(getwsanularSoapWSDDServiceName());
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
        if ("wsanularSoap12".equals(inputPortName)) {
            return getwsanularSoap12();
        }
        else if ("wsanularSoap".equals(inputPortName)) {
            return getwsanularSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/anulartransaccion", "wsanular");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/anulartransaccion", "wsanularSoap12"));
            ports.add(new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/anulartransaccion", "wsanularSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("wsanularSoap12".equals(portName)) {
            setwsanularSoap12EndpointAddress(address);
        }
        else 
if ("wsanularSoap".equals(portName)) {
            setwsanularSoapEndpointAddress(address);
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
