/**
 * UGSInterfaceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.ugspay.message;

import com.directi.pg.Logger;

public class UGSInterfaceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(UGSInterfaceTestCase.class.getName());
    public UGSInterfaceTestCase(java.lang.String name) {
        super(name);
    }

    public void testInterfacePortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new UGSInterfaceLocator().getInterfacePortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new UGSInterfaceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1InterfacePortSaleTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.AuthorizationResponse value = null;
        value = binding.saleTransaction(new com.directi.pg.core.ugspay.message.DirectAuthorizationRequest());
        // TBD - validate results
    }

    public void test2InterfacePortAuthorizeTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.AuthorizationResponse value = null;
        value = binding.authorizeTransaction(new com.directi.pg.core.ugspay.message.DirectAuthorizationRequest());
        // TBD - validate results
    }

    public void test3InterfacePortCancelTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.Response value = null;
        value = binding.cancelTransaction(new com.directi.pg.core.ugspay.message.ReferralRequest());
        // TBD - validate results
    }

    public void test4InterfacePortS3DCheckTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.S3DcheckResponse value = null;
        value = binding.s3DCheckTransaction(new com.directi.pg.core.ugspay.message.DirectRequest());
        // TBD - validate results
    }

    public void test5InterfacePortCaptureTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.Response value = null;
        value = binding.captureTransaction(new com.directi.pg.core.ugspay.message.ReferralDetailedRequest());
        // TBD - validate results
    }

    public void test6InterfacePortCreditTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.Response value = null;
        value = binding.creditTransaction(new com.directi.pg.core.ugspay.message.ReferralDetailedRequest());
        // TBD - validate results
    }

    public void test7InterfacePortRebillTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.RebillResponse value = null;
        value = binding.rebillTransaction(new com.directi.pg.core.ugspay.message.ReferralDetailedRequest());
        // TBD - validate results
    }

    public void test8InterfacePortRefundTransaction() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.Response value = null;
        value = binding.refundTransaction(new com.directi.pg.core.ugspay.message.ReferralDetailedRequest());
        // TBD - validate results
    }

    public void test9InterfacePortOrderStatus() throws Exception {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding;
        try {
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)
                          new UGSInterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.ugspay.message.OrderStatusResponse value = null;
        value = binding.orderStatus(new com.directi.pg.core.ugspay.message.OrderStatusRequest());
        // TBD - validate results
    }

}
