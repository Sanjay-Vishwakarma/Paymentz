/**
 * BasicOperationsTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payvision.core.message;

import com.directi.pg.Logger;

public class BasicOperationsTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(BasicOperationsTestCase.class.getName());
    public BasicOperationsTestCase(java.lang.String name) {
        super(name);
    }

    public void testBasicOperationsSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.payvision.core.message.BasicOperationsLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1BasicOperationsSoap12Authorize() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.authorize(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test2BasicOperationsSoap12Payment() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.payment(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test3BasicOperationsSoap12Credit() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.credit(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test4BasicOperationsSoap12Refund() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.refund(0, new java.lang.String(), 0, new java.lang.String(), new java.math.BigDecimal(0), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test5BasicOperationsSoap12Capture() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.capture(0, new java.lang.String(), 0, new java.lang.String(), new java.math.BigDecimal(0), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test6BasicOperationsSoap12_void() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding._void(0, new java.lang.String(), 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test7BasicOperationsSoap12ReferralApproval() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.referralApproval(0, new java.lang.String(), 0, new java.lang.String(), new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test8BasicOperationsSoap12CardFundTransfer() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.cardFundTransfer(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test9BasicOperationsSoap12RetrieveTransactionResult() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoap12Stub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.retrieveTransactionResult(0, new java.lang.String(), new java.lang.String(), java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void testBasicOperationsSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.payvision.core.message.BasicOperationsLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test10BasicOperationsSoapAuthorize() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.authorize(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test11BasicOperationsSoapPayment() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.payment(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test12BasicOperationsSoapCredit() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.credit(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test13BasicOperationsSoapRefund() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.refund(0, new java.lang.String(), 0, new java.lang.String(), new java.math.BigDecimal(0), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test14BasicOperationsSoapCapture() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.capture(0, new java.lang.String(), 0, new java.lang.String(), new java.math.BigDecimal(0), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test15BasicOperationsSoap_void() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding._void(0, new java.lang.String(), 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test16BasicOperationsSoapReferralApproval() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.referralApproval(0, new java.lang.String(), 0, new java.lang.String(), new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test17BasicOperationsSoapCardFundTransfer() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.cardFundTransfer(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test18BasicOperationsSoapRetrieveTransactionResult() throws Exception {
        com.payment.payvision.core.message.BasicOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.BasicOperationsSoapStub)
                          new com.payment.payvision.core.message.BasicOperationsLocator().getBasicOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.retrieveTransactionResult(0, new java.lang.String(), new java.lang.String(), java.util.Calendar.getInstance());
        // TBD - validate results
    }

}
