/**
 * RecurringOperationsTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payvision.core.message;

import com.directi.pg.Logger;

public class RecurringOperationsTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(RecurringOperationsTestCase.class.getName());
    public RecurringOperationsTestCase(java.lang.String name) {
        super(name);
    }

    public void testRecurringOperationsSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.payvision.core.message.RecurringOperationsLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1RecurringOperationsSoapAuthorize() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoapStub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.authorize(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test2RecurringOperationsSoapPayment() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoapStub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.payment(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test3RecurringOperationsSoapCredit() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoapStub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.credit(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test4RecurringOperationsSoapCardFundTransfer() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoapStub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.cardFundTransfer(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test5RecurringOperationsSoapRegisterCard() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoapStub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoapStub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.RegisterCardResult value = null;
        value = binding.registerCard(0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String());
        // TBD - validate results
    }

    public void testRecurringOperationsSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.payvision.core.message.RecurringOperationsLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test6RecurringOperationsSoap12Authorize() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoap12Stub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.authorize(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test7RecurringOperationsSoap12Payment() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoap12Stub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.payment(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test8RecurringOperationsSoap12Credit() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoap12Stub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.credit(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test9RecurringOperationsSoap12CardFundTransfer() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoap12Stub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.TransactionResult value = null;
        value = binding.cardFundTransfer(0, new java.lang.String(), 0, new java.math.BigDecimal(0), 0, new java.lang.String(), 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test10RecurringOperationsSoap12RegisterCard() throws Exception {
        com.payment.payvision.core.message.RecurringOperationsSoap12Stub binding;
        try {
            binding = (com.payment.payvision.core.message.RecurringOperationsSoap12Stub)
                          new com.payment.payvision.core.message.RecurringOperationsLocator().getRecurringOperationsSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payvision.core.message.RegisterCardResult value = null;
        value = binding.registerCard(0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new org.apache.axis.types.UnsignedByte(0), (short)0, new java.lang.String());
        // TBD - validate results
    }

}
