/**
 * APITestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

import com.directi.pg.TransactionLogger;
import org.apache.axis.message.SOAPHeaderElement;

public class APITestCase extends junit.framework.TestCase {
    private static TransactionLogger transactionLogger = new TransactionLogger(APITestCase.class.getName());
    public APITestCase(java.lang.String name) {
        super(name);
    }

    public void testAPISoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.Oculus.com.MyCardStorage.APILocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1APISoapAddSessionID() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
               // jre.getLinkedCause().printStackTrace();
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addSessionID(new java.lang.String());
        // TBD - validate results
    }

    public void test2APISoapAddSessionID_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddSession_Response value = null;
        value = binding.addSessionID_Soap(new com.payment.Oculus.com.MyCardStorage.ServiceSecurity());
        // TBD - validate results
    }

    public void test3APISoapBookingRequest() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.bookingRequest(new java.lang.String());
        // TBD - validate results
    }

    public void test4APISoapBookingRequest_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.Booking_Response value = null;
        value = binding.bookingRequest_Soap(new com.payment.Oculus.com.MyCardStorage.Booking());
        // TBD - validate results
    }

    public void test5APISoapCreditSale_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditSale_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test6APISoapCreditAuth_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditAuth_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test7APISoapCreditCapture_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCapture_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test8APISoapAddTokenCreditSale_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.addTokenCreditSale_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test9APISoapCreditCredit_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCredit_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test10APISoapCreditVoid_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditVoid_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test11APISoapAddCOF() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addCOF(new java.lang.String());
        // TBD - validate results
    }

    public void test12APISoapAddCOF_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddToken_Response value = null;
        value = binding.addCOF_Soap(new com.payment.Oculus.com.MyCardStorage.AddToken());
        // TBD - validate results
    }

    public void test13APISoapUpdateCOF() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.updateCOF(new java.lang.String());
        // TBD - validate results
    }

    public void test14APISoapUpdateCOF_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddToken_Response value = null;
        value = binding.updateCOF_Soap(new com.payment.Oculus.com.MyCardStorage.AddToken());
        // TBD - validate results
    }

    public void test15APISoapRemoveToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.removeToken(new java.lang.String());
        // TBD - validate results
    }

    public void test16APISoapCreditSale_Token_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditSale_Token_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test17APISoapCreditCredit_Token_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCredit_Token_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test18APISoapCreditVoid_Token_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditVoid_Token_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test19APISoapAddToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addToken(new java.lang.String());
        // TBD - validate results
    }

    public void test20APISoapAddToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddToken_Response value = null;
        value = binding.addToken_Soap(new com.payment.Oculus.com.MyCardStorage.AddToken());
        // TBD - validate results
    }

    public void test21APISoapCreditSale_3RdPartyToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.creditSale_3RdPartyToken(new java.lang.String());
        // TBD - validate results
    }

    public void test22APISoapCreditSale_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditSale_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test23APISoapAddTokenCreditSale_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.addTokenCreditSale_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test24APISoapCreditCredit_3RdPartyToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.creditCredit_3RdPartyToken(new java.lang.String());
        // TBD - validate results
    }

    public void test25APISoapCreditCredit_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCredit_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test26APISoapCreditVoid_3RdPartyToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.creditVoid_3RdPartyToken(new java.lang.String());
        // TBD - validate results
    }

    public void test27APISoapCreditVoid_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditVoid_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test28APISoapDeviceIDLookUpTest() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUpTest(new java.lang.String());
        // TBD - validate results
    }

    public void test29APISoapDeviceIDLookUp() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUp(new java.lang.String());
        // TBD - validate results
    }

    public void test30APISoapDeviceIDLookUpWithTip() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUpWithTip(new java.lang.String());
        // TBD - validate results
    }

    public void test31APISoapDeviceIDLookUp_TAuth() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUp_TAuth(new java.lang.String());
        // TBD - validate results
    }

    public void test32APISoapAddEMVSessionID() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addEMVSessionID(new java.lang.String());
        // TBD - validate results
    }

    public void test33APISoapCreateTerminalSession() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.createTerminalSession(new java.lang.String());
        // TBD - validate results
    }

    public void test34APISoapRetrieveTerminalTokenData() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.retrieveTerminalTokenData(new java.lang.String());
        // TBD - validate results
    }

    public void test35APISoapTerminalDataTransfer() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.terminalDataTransfer(new java.lang.String());
        // TBD - validate results
    }

    public void test36APISoapTerminalDataTransfer_TAuth() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.terminalDataTransfer_TAuth(new java.lang.String());
        // TBD - validate results
    }

    public void test37APISoapRetrieveEMVTokenData() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.retrieveEMVTokenData(new java.lang.String());
        // TBD - validate results
    }

    public void test38APISoapCreditCardBatchFileUpload_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.CreditCardBatchFileUploadResult value = null;
        value = binding.creditCardBatchFileUpload_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardBatchFile());
        // TBD - validate results
    }

    public void test39APISoapCreditCardBatchFileResult_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.CreditCardBatchFileResult value = null;
        value = binding.creditCardBatchFileResult_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardBatchFile());
        // TBD - validate results
    }

    public void test40APISoapGetChargebacks_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.ChargeBack_Response value = null;
        value = binding.getChargebacks_Soap(new com.payment.Oculus.com.MyCardStorage.ChargeBackRequest());
        // TBD - validate results
    }

    public void test41APISoapRetrieveTokenData() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoapStub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoapStub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.retrieveTokenData(new java.lang.String());
        // TBD - validate results
    }

    public void testAPISoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.Oculus.com.MyCardStorage.APILocator().getServiceName());
        assertTrue(service != null);
    }

    public void test42APISoap12AddSessionID() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addSessionID(new java.lang.String());
        // TBD - validate results
    }

    public void test43APISoap12AddSessionID_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddSession_Response value = null;
        value = binding.addSessionID_Soap(new com.payment.Oculus.com.MyCardStorage.ServiceSecurity());
        // TBD - validate results
    }

    public void test44APISoap12BookingRequest() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.bookingRequest(new java.lang.String());
        // TBD - validate results
    }

    public void test45APISoap12BookingRequest_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.Booking_Response value = null;
        value = binding.bookingRequest_Soap(new com.payment.Oculus.com.MyCardStorage.Booking());
        // TBD - validate results
    }

    public void test46APISoap12CreditSale_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);
        SOAPHeaderElement authentication = new SOAPHeaderElement("https://MyCardStorage.com/","AuthHeader");
        SOAPHeaderElement user = new SOAPHeaderElement("https://MyCardStorage.com/","UserName", "TransactworldUser");
        SOAPHeaderElement password = new SOAPHeaderElement("https://MyCardStorage.com/","Password", "!uZQpQTnE$Q8Rr&T");
        authentication.addChild(user);
        authentication.addChild(password);
        binding.setHeader(authentication);


        //binding.setUsername("TransactworldUser");
        //binding.setPassword("!uZQpQTnE$Q8Rr&T");
        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditSale_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test47APISoap12CreditAuth_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditAuth_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test48APISoap12CreditCapture_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCapture_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test49APISoap12AddTokenCreditSale_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.addTokenCreditSale_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test50APISoap12CreditCredit_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCredit_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test51APISoap12CreditVoid_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditVoid_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test52APISoap12AddCOF() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addCOF(new java.lang.String());
        // TBD - validate results
    }

    public void test53APISoap12AddCOF_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddToken_Response value = null;
        value = binding.addCOF_Soap(new com.payment.Oculus.com.MyCardStorage.AddToken());
        // TBD - validate results
    }

    public void test54APISoap12UpdateCOF() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.updateCOF(new java.lang.String());
        // TBD - validate results
    }

    public void test55APISoap12UpdateCOF_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddToken_Response value = null;
        value = binding.updateCOF_Soap(new com.payment.Oculus.com.MyCardStorage.AddToken());
        // TBD - validate results
    }

    public void test56APISoap12RemoveToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.removeToken(new java.lang.String());
        // TBD - validate results
    }

    public void test57APISoap12CreditSale_Token_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditSale_Token_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test58APISoap12CreditCredit_Token_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCredit_Token_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test59APISoap12CreditVoid_Token_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditVoid_Token_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test60APISoap12AddToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addToken(new java.lang.String());
        // TBD - validate results
    }

    public void test61APISoap12AddToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.AddToken_Response value = null;
        value = binding.addToken_Soap(new com.payment.Oculus.com.MyCardStorage.AddToken());
        // TBD - validate results
    }

    public void test62APISoap12CreditSale_3RdPartyToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.creditSale_3RdPartyToken(new java.lang.String());
        // TBD - validate results
    }

    public void test63APISoap12CreditSale_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditSale_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test64APISoap12AddTokenCreditSale_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.addTokenCreditSale_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test65APISoap12CreditCredit_3RdPartyToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.creditCredit_3RdPartyToken(new java.lang.String());
        // TBD - validate results
    }

    public void test66APISoap12CreditCredit_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditCredit_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test67APISoap12CreditVoid_3RdPartyToken() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.creditVoid_3RdPartyToken(new java.lang.String());
        // TBD - validate results
    }

    public void test68APISoap12CreditVoid_3RdPartyToken_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.TransactionResult value = null;
        value = binding.creditVoid_3RdPartyToken_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardTransaction());
        // TBD - validate results
    }

    public void test69APISoap12DeviceIDLookUpTest() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUpTest(new java.lang.String());
        // TBD - validate results
    }

    public void test70APISoap12DeviceIDLookUp() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUp(new java.lang.String());
        // TBD - validate results
    }

    public void test71APISoap12DeviceIDLookUpWithTip() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUpWithTip(new java.lang.String());
        // TBD - validate results
    }

    public void test72APISoap12DeviceIDLookUp_TAuth() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.deviceIDLookUp_TAuth(new java.lang.String());
        // TBD - validate results
    }

    public void test73APISoap12AddEMVSessionID() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.addEMVSessionID(new java.lang.String());
        // TBD - validate results
    }

    public void test74APISoap12CreateTerminalSession() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.createTerminalSession(new java.lang.String());
        // TBD - validate results
    }

    public void test75APISoap12RetrieveTerminalTokenData() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.retrieveTerminalTokenData(new java.lang.String());
        // TBD - validate results
    }

    public void test76APISoap12TerminalDataTransfer() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.terminalDataTransfer(new java.lang.String());
        // TBD - validate results
    }

    public void test77APISoap12TerminalDataTransfer_TAuth() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.terminalDataTransfer_TAuth(new java.lang.String());
        // TBD - validate results
    }

    public void test78APISoap12RetrieveEMVTokenData() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.retrieveEMVTokenData(new java.lang.String());
        // TBD - validate results
    }

    public void test79APISoap12CreditCardBatchFileUpload_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.CreditCardBatchFileUploadResult value = null;
        value = binding.creditCardBatchFileUpload_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardBatchFile());
        // TBD - validate results
    }

    public void test80APISoap12CreditCardBatchFileResult_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.CreditCardBatchFileResult value = null;
        value = binding.creditCardBatchFileResult_Soap(new com.payment.Oculus.com.MyCardStorage.CreditCardBatchFile());
        // TBD - validate results
    }

    public void test81APISoap12GetChargebacks_Soap() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.Oculus.com.MyCardStorage.ChargeBack_Response value = null;
        value = binding.getChargebacks_Soap(new com.payment.Oculus.com.MyCardStorage.ChargeBackRequest());
        // TBD - validate results
    }

    public void test82APISoap12RetrieveTokenData() throws Exception {
        com.payment.Oculus.com.MyCardStorage.APISoap12Stub binding;
        try {
            binding = (com.payment.Oculus.com.MyCardStorage.APISoap12Stub)
                          new com.payment.Oculus.com.MyCardStorage.APILocator().getAPISoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException ::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.retrieveTokenData(new java.lang.String());
        // TBD - validate results
    }

}
