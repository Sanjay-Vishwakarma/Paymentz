/**
 * SrvprocesoLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.procesosmc.org.tempuri;

public class SrvprocesoLocator extends org.apache.axis.client.Service implements Srvproceso {

    public SrvprocesoLocator() {
    }


    public SrvprocesoLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SrvprocesoLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_Isrvproceso
    private String BasicHttpBinding_Isrvproceso_address ="";// "https://testws.punto-web.com/wcfadproc/srvproceso.svc";

    public void setBasicHttpBinding_Isrvproceso_address(String url)
    {
        BasicHttpBinding_Isrvproceso_address=url;
    }

    public String getBasicHttpBinding_IsrvprocesoAddress() {
        return BasicHttpBinding_Isrvproceso_address;
    }

    // The WSDD service name defaults to the port name.
    private String BasicHttpBinding_IsrvprocesoWSDDServiceName = "BasicHttpBinding_Isrvproceso";

    public String getBasicHttpBinding_IsrvprocesoWSDDServiceName() {
        return BasicHttpBinding_IsrvprocesoWSDDServiceName;
    }

    public void setBasicHttpBinding_IsrvprocesoWSDDServiceName(String name) {
        BasicHttpBinding_IsrvprocesoWSDDServiceName = name;
    }

    public Isrvproceso getBasicHttpBinding_Isrvproceso() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_Isrvproceso_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_Isrvproceso(endpoint);
    }

    public Isrvproceso getBasicHttpBinding_Isrvproceso(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            BasicHttpBinding_IsrvprocesoStub _stub = new BasicHttpBinding_IsrvprocesoStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_IsrvprocesoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_IsrvprocesoEndpointAddress(String address) {
        BasicHttpBinding_Isrvproceso_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Isrvproceso.class.isAssignableFrom(serviceEndpointInterface)) {
                BasicHttpBinding_IsrvprocesoStub _stub = new BasicHttpBinding_IsrvprocesoStub(new java.net.URL(BasicHttpBinding_Isrvproceso_address), this);
                _stub.setPortName(getBasicHttpBinding_IsrvprocesoWSDDServiceName());
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
        if ("BasicHttpBinding_Isrvproceso".equals(inputPortName)) {
            return getBasicHttpBinding_Isrvproceso();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "srvproceso");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_Isrvproceso"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_Isrvproceso".equals(portName)) {
            setBasicHttpBinding_IsrvprocesoEndpointAddress(address);
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
