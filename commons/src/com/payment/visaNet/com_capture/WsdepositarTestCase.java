/**
 * WsdepositarTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_capture;

import com.directi.pg.Logger;

public class WsdepositarTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(WsdepositarTestCase.class.getName());
    public WsdepositarTestCase(java.lang.String name) {
        super(name);
    }

    public void testwsdepositarSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.visaNet.com_capture.WsdepositarLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1wsdepositarSoap12DepositarEticket() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoap12Stub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoap12Stub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.depositarEticket(new java.lang.String());
        // TBD - validate results
    }

    public void test2wsdepositarSoap12DepositarPedido() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoap12Stub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoap12Stub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.depositarPedido(new java.lang.String());
        // TBD - validate results
    }

    public void test3wsdepositarSoap12AnulaDepositoPedido() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoap12Stub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoap12Stub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anulaDepositoPedido(new java.lang.String());
        // TBD - validate results
    }

    public void test4wsdepositarSoap12AnulaDepositoEticket() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoap12Stub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoap12Stub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anulaDepositoEticket(new java.lang.String());
        // TBD - validate results
    }

    public void testwsdepositarSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.visaNet.com_capture.WsdepositarLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test5wsdepositarSoapDepositarEticket() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoapStub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoapStub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.depositarEticket(new java.lang.String());
        // TBD - validate results
    }

    public void test6wsdepositarSoapDepositarPedido() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoapStub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoapStub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.depositarPedido(new java.lang.String());
        // TBD - validate results
    }

    public void test7wsdepositarSoapAnulaDepositoPedido() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoapStub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoapStub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anulaDepositoPedido(new java.lang.String());
        // TBD - validate results
    }

    public void test8wsdepositarSoapAnulaDepositoEticket() throws Exception {
        com.payment.visaNet.com_capture.WsdepositarSoapStub binding;
        try {
            binding = (com.payment.visaNet.com_capture.WsdepositarSoapStub)
                          new com.payment.visaNet.com_capture.WsdepositarLocator().getwsdepositarSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anulaDepositoEticket(new java.lang.String());
        // TBD - validate results
    }

}
