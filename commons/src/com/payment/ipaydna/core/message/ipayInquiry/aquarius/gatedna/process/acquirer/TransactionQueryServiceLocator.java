/**
 * TransactionQueryServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer;

public class TransactionQueryServiceLocator extends org.apache.axis.client.Service implements com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryService {

    public TransactionQueryServiceLocator() {
    }


    public TransactionQueryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TransactionQueryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TransactionQueryCfc
    private java.lang.String TransactionQueryCfc_address = "https://www.ipaydna.info/aquarius/gatedna/process/acquirer/transactionQuery.cfc";

    public java.lang.String getTransactionQueryCfcAddress() {
        return TransactionQueryCfc_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TransactionQueryCfcWSDDServiceName = "transactionQuery.cfc";

    public java.lang.String getTransactionQueryCfcWSDDServiceName() {
        return TransactionQueryCfcWSDDServiceName;
    }

    public void setTransactionQueryCfcWSDDServiceName(java.lang.String name) {
        TransactionQueryCfcWSDDServiceName = name;
    }

    public com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQuery getTransactionQueryCfc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TransactionQueryCfc_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTransactionQueryCfc(endpoint);
    }

    public com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQuery getTransactionQueryCfc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub _stub = new com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub(portAddress, this);
            _stub.setPortName(getTransactionQueryCfcWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTransactionQueryCfcEndpointAddress(java.lang.String address) {
        TransactionQueryCfc_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQuery.class.isAssignableFrom(serviceEndpointInterface)) {
                com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub _stub = new com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub(new java.net.URL(TransactionQueryCfc_address), this);
                _stub.setPortName(getTransactionQueryCfcWSDDServiceName());
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
        if ("transactionQuery.cfc".equals(inputPortName)) {
            return getTransactionQueryCfc();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://acquirer.process.gatedna.aquarius", "transactionQueryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://acquirer.process.gatedna.aquarius", "transactionQuery.cfc"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TransactionQueryCfc".equals(portName)) {
            setTransactionQueryCfcEndpointAddress(address);
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
