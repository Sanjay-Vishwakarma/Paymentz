/**
 * _interfaceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.uPayGate.Interface_pkg;

import com.directi.pg.Logger;

public class _interfaceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(_interfaceTestCase.class.getName());
    public _interfaceTestCase(String name) {
        super(name);
    }

    public void testInterfacePortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new InterfaceLocator().getInterfacePortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new InterfaceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1InterfacePortSaleTransaction() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.uPayGate.Interface_pkg.AuthorizationResponse value = null;
        value = binding.saleTransaction(new com.payment.uPayGate.Interface_pkg.DirectAuthorizationRequest());
        // TBD - validate results
    }

    public void test2InterfacePortAuthorizeTransaction() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.uPayGate.Interface_pkg.AuthorizationResponse value = null;
        value = binding.authorizeTransaction(new com.payment.uPayGate.Interface_pkg.DirectAuthorizationRequest());
        // TBD - validate results
    }

    public void test3InterfacePortCancelTransaction() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.cancelTransaction(new ReferralRequest());
        // TBD - validate results
    }

    public void test4InterfacePortS3DDataSubmit() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.uPayGate.Interface_pkg.S3DDataSubmitResponse value = null;
        value = binding.s3DDataSubmit(new com.payment.uPayGate.Interface_pkg.S3DDataSubmitRequest());
        // TBD - validate results
    }

    public void test5InterfacePortCaptureTransaction() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.captureTransaction(new com.payment.uPayGate.Interface_pkg.ReferralDetailedRequest());
        // TBD - validate results
    }

    public void test6InterfacePortCreditTransaction() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.uPayGate.Interface_pkg.AuthorizationResponse value = null;
        value = binding.creditTransaction(new CreditRequest());
        // TBD - validate results
    }

    public void test7InterfacePortRebillTransaction() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        RebillResponse value = null;
        value = binding.rebillTransaction(new com.payment.uPayGate.Interface_pkg.ReferralDetailedRequest());
        // TBD - validate results
    }

    public void test8InterfacePortRefundTransaction() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.refundTransaction(new com.payment.uPayGate.Interface_pkg.ReferralDetailedRequest());
        // TBD - validate results
    }

    public void test9InterfacePortOrderStatus() throws Exception {
        com.payment.uPayGate.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.uPayGate.Interface_pkg.InterfaceBindingStub)
                          new InterfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.uPayGate.Interface_pkg.OrderStatusResponse value = null;
        value = binding.orderStatus(new com.payment.uPayGate.Interface_pkg.OrderStatusRequest());
        // TBD - validate results
    }

}
