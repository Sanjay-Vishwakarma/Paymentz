/**
 * TransactionSOAPBindingImplServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService;

import com.directi.pg.Logger;

public class TransactionSOAPBindingImplServiceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(TransactionSOAPBindingImplServiceTestCase.class.getName());
    public TransactionSOAPBindingImplServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testTransactionServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1TransactionServiceProcessCCSale() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCCSale(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test2TransactionServiceProcessCCAuth() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCCAuth(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test3TransactionServiceProcessCCVoid() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCCVoid(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost());
        // TBD - validate results
    }

    public void test4TransactionServiceProcessCCPost() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCCPost(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost());
        // TBD - validate results
    }

    public void test5TransactionServiceProcessACHSale() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processACHSale(new com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo());
        // TBD - validate results
    }

    public void test6TransactionServiceProcessACHVerification() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processACHVerification(new com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo());
        // TBD - validate results
    }

    public void test7TransactionServiceProcessACHCheck21() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processACHCheck21(new com.directi.pg.core.swiffpay.MPTransProcess.ACHCheck21Info());
        // TBD - validate results
    }

    public void test8TransactionServiceProcessCCCredit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCCCredit(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test9TransactionServiceProcessACHCredit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processACHCredit(new com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo());
        // TBD - validate results
    }

    public void test10TransactionServiceProcessCredit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCredit(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost());
        // TBD - validate results
    }

    public void test11TransactionServiceProcessDebitSale() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processDebitSale(new com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo());
        // TBD - validate results
    }

    public void test12TransactionServiceProcessDebitReturn() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processDebitReturn(new com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn());
        // TBD - validate results
    }

    public void test13TransactionServiceProcessCCProfileAdd() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult value = null;
        value = binding.processCCProfileAdd(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test14TransactionServiceProcessCKProfileAdd() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult value = null;
        value = binding.processCKProfileAdd(new com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo());
        // TBD - validate results
    }

    public void test15TransactionServiceProcessProfileRetrieve() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult value = null;
        value = binding.processProfileRetrieve(new com.directi.pg.core.swiffpay.MPTransProcess.ProfileRetrieve());
        // TBD - validate results
    }

    public void test16TransactionServiceProcessProfileDelete() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult value = null;
        value = binding.processProfileDelete(new com.directi.pg.core.swiffpay.MPTransProcess.ProfileDelete());
        // TBD - validate results
    }

    public void test17TransactionServiceProcessProfileSale() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult value = null;
        value = binding.processProfileSale(new com.directi.pg.core.swiffpay.MPTransProcess.ProfileSale());
        // TBD - validate results
    }

    public void test18TransactionServiceProcessProfileUpdate() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult value = null;
        value = binding.processProfileUpdate(new com.directi.pg.core.swiffpay.MPTransProcess.ProfileUpdate());
        // TBD - validate results
    }

    public void test19TransactionServiceProcessProfileCredit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult value = null;
        value = binding.processProfileCredit(new com.directi.pg.core.swiffpay.MPTransProcess.ProfileCredit());
        // TBD - validate results
    }

    public void test20TransactionServiceProcessCheck21Void() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCheck21Void(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21());
        // TBD - validate results
    }

    public void test21TransactionServiceProcessCheck21Credit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCheck21Credit(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21());
        // TBD - validate results
    }

    public void test22TransactionServiceProcessExtACHVoid() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processExtACHVoid(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost());
        // TBD - validate results
    }

    public void test23TransactionServiceProcessExtACHCredit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processExtACHCredit(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost());
        // TBD - validate results
    }

    public void test24TransactionServiceProcessExtACHSale() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processExtACHSale(new com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo());
        // TBD - validate results
    }

    public void test25TransactionServiceProcessC21RCCVoid() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processC21RCCVoid(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost());
        // TBD - validate results
    }

    public void test26TransactionServiceProcessC21RCCCredit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processC21RCCCredit(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost());
        // TBD - validate results
    }

    public void test27TransactionServiceProcessC21RCCSale() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processC21RCCSale(new com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo());
        // TBD - validate results
    }

    public void test28TransactionServiceProcessExtACHConsumerDisbursement() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processExtACHConsumerDisbursement(new com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo());
        // TBD - validate results
    }

    public void test29TransactionServiceProcessTransRetrieve() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processTransRetrieve(new com.directi.pg.core.swiffpay.MPTransProcess.TransRetrieve());
        // TBD - validate results
    }

    public void test30TransactionServiceProcessRecurRetrieve() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult value = null;
        value = binding.processRecurRetrieve(new com.directi.pg.core.swiffpay.MPTransProcess.RecurRetrieve());
        // TBD - validate results
    }

    public void test31TransactionServiceProcessC21ICLSale() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processC21ICLSale(new com.directi.pg.core.swiffpay.MPTransProcess.C21ICLInfo());
        // TBD - validate results
    }

    public void test32TransactionServiceProcessC21ICLVoid() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processC21ICLVoid(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL());
        // TBD - validate results
    }

    public void test33TransactionServiceProcessC21ICLCredit() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processC21ICLCredit(new com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL());
        // TBD - validate results
    }

    public void test34TransactionServiceProcessCCAuthentication() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult value = null;
        value = binding.processCCAuthentication(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test35TransactionServiceProcessCCSales() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults value = null;
        value = binding.processCCSales(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test36TransactionServiceProcessCCAuths() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults value = null;
        value = binding.processCCAuths(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test37TransactionServiceProcessCCCredits() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults value = null;
        value = binding.processCCCredits(new com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo());
        // TBD - validate results
    }

    public void test38TransactionServiceProcessDebitSales() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults value = null;
        value = binding.processDebitSales(new com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo());
        // TBD - validate results
    }

    public void test39TransactionServiceProcessDebitReturns() throws Exception {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding;
        try {
            binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)
                          new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults value = null;
        value = binding.processDebitReturns(new com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn());
        // TBD - validate results
    }

}
