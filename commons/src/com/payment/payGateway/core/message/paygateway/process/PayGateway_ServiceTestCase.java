/**
 * PayGateway_ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payGateway.core.message.paygateway.process;

import com.directi.pg.Logger;

public class PayGateway_ServiceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(PayGateway_ServiceTestCase.class.getName());
    public PayGateway_ServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testPayGateway_ServiceSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1PayGateway_ServiceSoap12ProcessTx() throws Exception {
        com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub binding;
        try {
            binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub)
                          new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payGateway.core.message.paygateway.process.TTransacResult value = null;
        value = binding.processTx(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test2PayGateway_ServiceSoap12ProcessAdjust() throws Exception {
        com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub binding;
        try {
            binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub)
                          new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payGateway.core.message.paygateway.process.TTransacResult value = null;
        value = binding.processAdjust(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test3PayGateway_ServiceSoap12ProcessRefund() throws Exception {
        com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub binding;
        try {
            binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub)
                          new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payGateway.core.message.paygateway.process.TTransacResult value = null;
        value = binding.processRefund(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void testPayGateway_ServiceSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test4PayGateway_ServiceSoapProcessTx() throws Exception {
        com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub binding;
        try {
            binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub)
                          new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payGateway.core.message.paygateway.process.TTransacResult value = null;
        value = binding.processTx(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test5PayGateway_ServiceSoapProcessAdjust() throws Exception {
        com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub binding;
        try {
            binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub)
                          new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payGateway.core.message.paygateway.process.TTransacResult value = null;
        value = binding.processAdjust(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test6PayGateway_ServiceSoapProcessRefund() throws Exception {
        com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub binding;
        try {
            binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub)
                          new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payGateway.core.message.paygateway.process.TTransacResult value = null;
        value = binding.processRefund(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

}
