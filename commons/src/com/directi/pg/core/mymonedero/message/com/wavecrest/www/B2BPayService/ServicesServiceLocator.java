/**
 * ServicesServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService;

public class ServicesServiceLocator extends org.apache.axis.client.Service implements com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesService
{

    public ServicesServiceLocator()
    {
    }


    public ServicesServiceLocator(org.apache.axis.EngineConfiguration config)
    {
        super(config);
    }

    public ServicesServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException
    {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for servicesPort
    private java.lang.String servicesPort_address = "https://demomonedero.wavecrest.gi/monedero/service";

    public java.lang.String getservicesPortAddress()
    {
        return servicesPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String servicesPortWSDDServiceName = "servicesPort";

    public java.lang.String getservicesPortWSDDServiceName()
    {
        return servicesPortWSDDServiceName;
    }

    public void setservicesPortWSDDServiceName(java.lang.String name)
    {
        servicesPortWSDDServiceName = name;
    }

    public com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.Services getservicesPort() throws javax.xml.rpc.ServiceException
    {
        java.net.URL endpoint;
        try
        {
            endpoint = new java.net.URL(servicesPort_address);
        }
        catch (java.net.MalformedURLException e)
        {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getservicesPort(endpoint);
    }

    public com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.Services getservicesPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException
    {
        try
        {
            com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub _stub = new com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub(portAddress, this);
            _stub.setPortName(getservicesPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e)
        {
            return null;
        }
    }

    public void setservicesPortEndpointAddress(java.lang.String address)
    {
        servicesPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException
    {
        try
        {
            if (com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.Services.class.isAssignableFrom(serviceEndpointInterface))
            {
                com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub _stub = new com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub(new java.net.URL(servicesPort_address), this);
                _stub.setPortName(getservicesPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t)
        {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException
    {
        if (portName == null)
        {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("servicesPort".equals(inputPortName))
        {
            return getservicesPort();
        }
        else
        {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName()
    {
        return new javax.xml.namespace.QName(getservicesPortAddress() + "/B2BPayIntegrationService.wsdl", "servicesService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts()
    {
        if (ports == null)
        {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName(getservicesPortAddress() + "/B2BPayIntegrationService.wsdl", "servicesPort"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException
    {

        if ("servicesPort".equals(portName))
        {
            setservicesPortEndpointAddress(address);
        }
        else
        { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException
    {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
