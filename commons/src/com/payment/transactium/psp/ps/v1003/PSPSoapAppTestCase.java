/**
 * PSPSoapAppTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003;

import com.directi.pg.Logger;
import com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.*;

public class PSPSoapAppTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(PSPSoapAppTestCase.class.getName());
    public PSPSoapAppTestCase(String name) {
        super(name);
    }

    public void testBasicHttpBinding_IPSPSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1BasicHttpBinding_IPSPSoapSale() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.sale(new Request());
        // TBD - validate results
    }

    public void test2BasicHttpBinding_IPSPSoapPreAuthorisation() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.preAuthorisation(new Request());
        // TBD - validate results
    }

    public void test3BasicHttpBinding_IPSPSoapCompletion() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.completion(new Request());
        // TBD - validate results
    }

    public void test4BasicHttpBinding_IPSPSoapRefund() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.refund(new Request());
        // TBD - validate results
    }

    public void test5BasicHttpBinding_IPSPSoapCFT() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.CFT(new Request());
        // TBD - validate results
    }

    public void test6BasicHttpBinding_IPSPSoap_void() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding._void(new Request());
        // TBD - validate results
    }

    public void test7BasicHttpBinding_IPSPSoapThreeDSSale() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.threeDSSale(new Request());
        // TBD - validate results
    }

    public void test8BasicHttpBinding_IPSPSoapThreeDSPreAuthorisation() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.threeDSPreAuthorisation(new Request());
        // TBD - validate results
    }

    public void test9BasicHttpBinding_IPSPSoapThreeDSFinalisation() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.threeDSFinalisation(new Request());
        // TBD - validate results
    }

    public void test10BasicHttpBinding_IPSPSoapProcess() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.process(RequestType.Sale, new Request());
        // TBD - validate results
    }

    public void test11BasicHttpBinding_IPSPSoapCloseBatch() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        CloseBatchResponse value = null;
        value = binding.closeBatch(new RequestAuthenticationType(), new String(), new String());
        // TBD - validate results
    }

    public void test12BasicHttpBinding_IPSPSoapProcessMAP() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.processMAP(new RequestAuthenticationType(), new String(), new String(), new Boolean(false), new Integer(0));
        // TBD - validate results
    }

    public void test13BasicHttpBinding_IPSPSoapCheckTransaction() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.checkTransaction(new RequestAuthenticationType(), new String());
        // TBD - validate results
    }

    public void test14BasicHttpBinding_IPSPSoapStoreTransaction() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        StoreTransactionResponse value = null;
        value = binding.storeTransaction(new StoreTransactionRequest());
        // TBD - validate results
    }

    public void test15BasicHttpBinding_IPSPSoapAlterTransaction() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        StoreTransactionResponse value = null;
        value = binding.alterTransaction(new String(), new StoreTransactionRequest());
        // TBD - validate results
    }

    public void test16BasicHttpBinding_IPSPSoapGetVersion() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String value = null;
        value = binding.getVersion();
        // TBD - validate results
    }

    public void test17BasicHttpBinding_IPSPSoapMPIVerifyEnrollment() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MPIVEResponse value = null;
        value = binding.MPIVerifyEnrollment(new MPIVERequest());
        // TBD - validate results
    }

    public void test18BasicHttpBinding_IPSPSoapMPIPayerAuthentication() throws Exception {
        com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub binding;
        try {
            binding = (com.payment.transactium.psp.ps.v1003.BasicHttpBinding_IPSPSoapStub)
                          new com.payment.transactium.psp.ps.v1003.PSPSoapAppLocator().getBasicHttpBinding_IPSPSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MPIPAResponse value = null;
        value = binding.MPIPayerAuthentication(new MPIPARequest());
        // TBD - validate results
    }

}
