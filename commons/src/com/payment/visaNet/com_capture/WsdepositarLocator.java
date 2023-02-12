/**
 * WsdepositarLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_capture;

public class WsdepositarLocator extends org.apache.axis.client.Service implements com.payment.visaNet.com_capture.Wsdepositar {

    public WsdepositarLocator() {
    }


    public WsdepositarLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WsdepositarLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for wsdepositarSoap12
    //private java.lang.String wsdepositarSoap12_address = "http://qas.multimerchantvisanet.com/wsdepositartransaccion/wsdepositar.asmx";
    //private java.lang.String wsdepositarSoap12_address = "https://www.multimerchantvisanet.com/wsdepositartransaccion/wsdepositar.asmx";
    private java.lang.String wsdepositarSoap12_address = "";

    public void setWsdepositarSoap12_address(String wsdepositarSoap12_address)
    {
        this.wsdepositarSoap12_address = wsdepositarSoap12_address;
    }

    public java.lang.String getwsdepositarSoap12Address() {
        return wsdepositarSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String wsdepositarSoap12WSDDServiceName = "wsdepositarSoap12";

    public java.lang.String getwsdepositarSoap12WSDDServiceName() {
        return wsdepositarSoap12WSDDServiceName;
    }

    public void setwsdepositarSoap12WSDDServiceName(java.lang.String name) {
        wsdepositarSoap12WSDDServiceName = name;
    }

    public com.payment.visaNet.com_capture.WsdepositarSoap getwsdepositarSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(wsdepositarSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getwsdepositarSoap12(endpoint);
    }

    public com.payment.visaNet.com_capture.WsdepositarSoap getwsdepositarSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.visaNet.com_capture.WsdepositarSoap12Stub _stub = new com.payment.visaNet.com_capture.WsdepositarSoap12Stub(portAddress, this);
            _stub.setPortName(getwsdepositarSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setwsdepositarSoap12EndpointAddress(java.lang.String address) {
        wsdepositarSoap12_address = address;
    }


    // Use to get a proxy class for wsdepositarSoap
    //private java.lang.String wsdepositarSoap_address = "http://qas.multimerchantvisanet.com/wsdepositartransaccion/wsdepositar.asmx";
    //private java.lang.String wsdepositarSoap_address = "https://www.multimerchantvisanet.com/wsdepositartransaccion/wsdepositar.asmx";
    private java.lang.String wsdepositarSoap_address = "";

    public void setWsdepositarSoap_address(String wsdepositarSoap_address)
    {
        this.wsdepositarSoap_address = wsdepositarSoap_address;
    }

    public java.lang.String getwsdepositarSoapAddress() {
        return wsdepositarSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String wsdepositarSoapWSDDServiceName = "wsdepositarSoap";

    public java.lang.String getwsdepositarSoapWSDDServiceName() {
        return wsdepositarSoapWSDDServiceName;
    }

    public void setwsdepositarSoapWSDDServiceName(java.lang.String name) {
        wsdepositarSoapWSDDServiceName = name;
    }

    public com.payment.visaNet.com_capture.WsdepositarSoap getwsdepositarSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(wsdepositarSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getwsdepositarSoap(endpoint);
    }

    public com.payment.visaNet.com_capture.WsdepositarSoap getwsdepositarSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.visaNet.com_capture.WsdepositarSoapStub _stub = new com.payment.visaNet.com_capture.WsdepositarSoapStub(portAddress, this);
            _stub.setPortName(getwsdepositarSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setwsdepositarSoapEndpointAddress(java.lang.String address) {
        wsdepositarSoap_address = address;
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
            if (com.payment.visaNet.com_capture.WsdepositarSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.visaNet.com_capture.WsdepositarSoap12Stub _stub = new com.payment.visaNet.com_capture.WsdepositarSoap12Stub(new java.net.URL(wsdepositarSoap12_address), this);
                _stub.setPortName(getwsdepositarSoap12WSDDServiceName());
                return _stub;
            }
            if (com.payment.visaNet.com_capture.WsdepositarSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.visaNet.com_capture.WsdepositarSoapStub _stub = new com.payment.visaNet.com_capture.WsdepositarSoapStub(new java.net.URL(wsdepositarSoap_address), this);
                _stub.setPortName(getwsdepositarSoapWSDDServiceName());
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
        if ("wsdepositarSoap12".equals(inputPortName)) {
            return getwsdepositarSoap12();
        }
        else if ("wsdepositarSoap".equals(inputPortName)) {
            return getwsdepositarSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/depositartransaccion", "wsdepositar");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/depositartransaccion", "wsdepositarSoap12"));
            ports.add(new javax.xml.namespace.QName("https://www.multimerchantvisanet.com/depositartransaccion", "wsdepositarSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("wsdepositarSoap12".equals(portName)) {
            setwsdepositarSoap12EndpointAddress(address);
        }
        else 
if ("wsdepositarSoap".equals(portName)) {
            setwsdepositarSoapEndpointAddress(address);
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
