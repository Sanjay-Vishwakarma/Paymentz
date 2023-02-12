/**
 * PscServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

import com.directi.pg.Logger;

public class PscServiceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(PscServiceTestCase.class.getName());
    public PscServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testPscWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.paySafeCard.pscservice.PscServiceLocator().getPscAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.paySafeCard.pscservice.PscServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1PscCreateDisposition() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.CreateDispositionReturn value = null;
        value = binding.createDisposition(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new com.payment.paySafeCard.pscservice.DispositionRestriction[0], new com.payment.paySafeCard.pscservice.MerchantClientData(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], new java.lang.Boolean(false));
        // TBD - validate results
    }

    public void test2PscAssignCardToDisposition() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.AssignCardToDispositionReturn value = null;
        value = binding.assignCardToDisposition(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], 0, new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test3PscAssignCardsToDisposition() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.AssignCardsToDispositionReturn value = null;
        value = binding.assignCardsToDisposition(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], 0, new java.lang.String(), new java.lang.String(), 0, new com.payment.paySafeCard.pscservice.AssignCardsInformation[0], new java.lang.String());
        // TBD - validate results
    }

    public void test4PscModifyDispositionValue() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ModifyDispositionValueReturn value = null;
        value = binding.modifyDispositionValue(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], 0, new java.lang.String());
        // TBD - validate results
    }

    public void test5PscGetSerialNumbers() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetSerialNumbersReturn value = null;
        value = binding.getSerialNumbers(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], new java.lang.String(), new java.lang.String[0]);
        // TBD - validate results
    }

    public void test6PscGetDispositionRawState() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetDispositionRawStateReturn value = null;
        value = binding.getDispositionRawState(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], new java.lang.String());
        // TBD - validate results
    }

    public void test7PscExecuteDebit() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ExecuteDebitReturn value = null;
        value = binding.executeDebit(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], 0, new java.lang.String(), 0, new java.lang.String());
        // TBD - validate results
    }

    public void test8PscExecuteDirectDebit() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ExecuteDirectDebitReturn value = null;
        value = binding.executeDirectDebit(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new com.payment.paySafeCard.pscservice.DispositionRestriction[0], new java.lang.String(), new java.lang.String(), new java.lang.String[0], new java.lang.Boolean(false), com.payment.paySafeCard.pscservice.CustomerIdType.CUSTOMERID, new java.lang.String());
        // TBD - validate results
    }

    public void test9PscGetCardInfo() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetCardInfoReturn value = null;
        value = binding.getCardInfo(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test10PscGetCardInfoActDate() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetCardInfoActDateReturn value = null;
        value = binding.getCardInfoActDate(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test11PscGetBalance() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetBalanceReturn value = null;
        value = binding.getBalance(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test12PscGetConvertedDispositionAmount() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetConvertedDispositionAmountReturn value = null;
        value = binding.getConvertedDispositionAmount(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String[0], 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test13PscActivateCard() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ActivateCardReturn value = null;
        value = binding.activateCard(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test14PscCancelCard() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.CancelCardReturn value = null;
        value = binding.cancelCard(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test15PscDeactivateCard() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.DeactivateCardReturn value = null;
        value = binding.deactivateCard(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test16PscExecutePayment() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ExecutePaymentReturn value = null;
        value = binding.executePayment(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test17PscExecutePaymentActDate() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ExecutePaymentActDateReturn value = null;
        value = binding.executePaymentActDate(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test18PscExecutePaymentACK() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ExecutePaymentACKReturn value = null;
        value = binding.executePaymentACK(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test19PscCancelPayment() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.CancelPaymentReturn value = null;
        value = binding.cancelPayment(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test20PscGetMid() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetMidReturn value = null;
        value = binding.getMid(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test21PscOpenLoopGiftcardActivation() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.OpenLoopGiftcardActivationReturn value = null;
        value = binding.openLoopGiftcardActivation(new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test22PscOpenLoopGiftcardReversal() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.OpenLoopGiftcardReversalReturn value = null;
        value = binding.openLoopGiftcardReversal(new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test23PscLoadWallet() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.LoadWalletReturn value = null;
        value = binding.loadWallet(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), com.payment.paySafeCard.pscservice.CustomerIdentificationType.phoneNumber, 0, new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test24PscCancelLoadWallet() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.CancelLoadWalletReturn value = null;
        value = binding.cancelLoadWallet(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test25PscExecutePayout() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.ExecutePayoutReturn value = null;
        value = binding.executePayout(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.Long(0), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test26PscRefund() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.RefundRequestReturnType value = null;
        value = binding.refund(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), com.payment.paySafeCard.pscservice.CustomerIdType.CUSTOMERID, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.Boolean(false), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test27PscPayout() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.PayoutReturn value = null;
        value = binding.payout(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), com.payment.paySafeCard.pscservice.CustomerIdType.CUSTOMERID, new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.Boolean(false), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new com.payment.paySafeCard.pscservice.CustomerDetailsBasic());
        // TBD - validate results
    }

    public void test28PscGetPayoutState() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.GetPayoutStateReturn value = null;
        value = binding.getPayoutState(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test29PscPrecheckPayout() throws Exception {
        com.payment.paySafeCard.pscservice.PscSoapBindingStub binding;
        try {
            binding = (com.payment.paySafeCard.pscservice.PscSoapBindingStub)
                          new com.payment.paySafeCard.pscservice.PscServiceLocator().getPsc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.paySafeCard.pscservice.PrecheckPayoutReturn value = null;
        value = binding.precheckPayout(new java.lang.String(), new java.lang.String(), new java.lang.String(), 0, new java.lang.String(), new java.lang.Long(0), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

}
