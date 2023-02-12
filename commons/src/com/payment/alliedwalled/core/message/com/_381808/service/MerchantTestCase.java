/**
 * MerchantTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.alliedwalled.core.message.com._381808.service;

import com.directi.pg.TransactionLogger;

public class MerchantTestCase extends junit.framework.TestCase {
    private static TransactionLogger transactionLogger = new TransactionLogger(MerchantTestCase.class.getName());

    public MerchantTestCase(java.lang.String name) {
        super(name);
    }

    public void testMerchantSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1MerchantSoap12BatchShipping() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.FailedTransaction[] value = null;
        value = binding.batchShipping(new com.payment.alliedwalled.core.message.com._381808.service.ShippingDetails[0]);
        // TBD - validate results
    }

    public void test2MerchantSoap12RepeatTransaction() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.repeatTransaction(new java.lang.String(), new java.lang.String(), new java.math.BigDecimal(0), new java.lang.String());
        // TBD - validate results
    }

    public void test3MerchantSoap12Refund() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.refund(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test4MerchantSoap12PartialRefund() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.partialRefund(new java.lang.String(), new java.lang.String(), new java.math.BigDecimal(0));
        // TBD - validate results
    }

    public void test5MerchantSoap12_void() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding._void(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test6MerchantSoap12Capture() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.capture(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test7MerchantSoap12PreauthorizeCreditCard() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeCreditCard(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String());
        // TBD - validate results
    }

    public void test8MerchantSoap12PreauthorizeCreditCard2() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeCreditCard2(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test9MerchantSoap12PreauthorizeAVS() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeAVS(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test10MerchantSoap12PreauthorizeInitialRecur() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeInitialRecur(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test11MerchantSoap12PreauthorizeThreeDMPI() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeThreeDMPI(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test12MerchantSoap12ExecuteCreditCard() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeCreditCard(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String());
        // TBD - validate results
    }

    public void test13MerchantSoap12ExecuteAVS() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeAVS(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test14MerchantSoap12ExecuteInitialRecur() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeInitialRecur(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test15MerchantSoap12ExecuteThreeDMPI() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeThreeDMPI(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test16MerchantSoap12ExecuteCreditCard2() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeCreditCard2(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test17MerchantSoap12CheckTransaction() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.checkTransaction(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test18MerchantSoap12VerifyThreeDEnrollment() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ThreeDVerifyResponse value = null;
        value = binding.verifyThreeDEnrollment(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test19MerchantSoap12ExecuteThreeD() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeThreeD(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void testMerchantSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test20MerchantSoapBatchShipping() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.FailedTransaction[] value = null;
        value = binding.batchShipping(new com.payment.alliedwalled.core.message.com._381808.service.ShippingDetails[0]);
        // TBD - validate results
    }

    public void test21MerchantSoapRepeatTransaction() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.repeatTransaction(new java.lang.String(), new java.lang.String(), new java.math.BigDecimal(0), new java.lang.String());
        // TBD - validate results
    }

    public void test22MerchantSoapRefund() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.refund(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test23MerchantSoapPartialRefund() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.partialRefund(new java.lang.String(), new java.lang.String(), new java.math.BigDecimal(0));
        // TBD - validate results
    }

    public void test24MerchantSoap_void() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding._void(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test25MerchantSoapCapture() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.capture(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test26MerchantSoapPreauthorizeCreditCard() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeCreditCard(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String());
        // TBD - validate results
    }

    public void test27MerchantSoapPreauthorizeCreditCard2() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeCreditCard2(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test28MerchantSoapPreauthorizeAVS() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeAVS(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test29MerchantSoapPreauthorizeInitialRecur() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeInitialRecur(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test30MerchantSoapPreauthorizeThreeDMPI() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.preauthorizeThreeDMPI(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test31MerchantSoapExecuteCreditCard() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeCreditCard(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String());
        // TBD - validate results
    }

    public void test32MerchantSoapExecuteAVS() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeAVS(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test33MerchantSoapExecuteInitialRecur() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeInitialRecur(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test34MerchantSoapExecuteThreeDMPI() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeThreeDMPI(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test35MerchantSoapExecuteCreditCard2() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeCreditCard2(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test36MerchantSoapCheckTransaction() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.checkTransaction(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test37MerchantSoapVerifyThreeDEnrollment() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ThreeDVerifyResponse value = null;
        value = binding.verifyThreeDEnrollment(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test38MerchantSoapExecuteThreeD() throws Exception {
        com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub binding;
        try {
            binding = (com.payment.alliedwalled.core.message.com._381808.service.MerchantSoapStub)
                          new com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator().getMerchantSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse value = null;
        value = binding.executeThreeD(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

}
