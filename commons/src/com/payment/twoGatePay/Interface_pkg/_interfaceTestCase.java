/**
 * _interfaceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.twoGatePay.Interface_pkg;

import com.directi.pg.Logger;

public class _interfaceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(_interfaceTestCase.class.getName());
    public _interfaceTestCase(java.lang.String name) {
        super(name);
    }

    public void testInterfacePortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getInterfacePortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1InterfacePortSale() throws Exception {
        com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub)
                          new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.twoGatePay.Interface_pkg.SaleResponse value = null;
        value = binding.sale(new com.payment.twoGatePay.Interface_pkg.SaleRequest());
        // TBD - validate results
    }

    /*public void test2InterfacePortAuth() throws Exception {
        com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub)
                          new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Interface_pkg.AuthResponse value = null;
        value = binding.auth(new Interface_pkg.AuthRequest());
        // TBD - validate results
    }

    public void test3InterfacePortS3DFinalise() throws Exception {
        Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (Interface_pkg.InterfaceBindingStub)
                          new Interface_pkg._interfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Interface_pkg.S3DFinaliseResponse value = null;
        value = binding.s3DFinalise(new Interface_pkg.S3DFinaliseRequest());
        // TBD - validate results
    }*/

    public void test4InterfacePortReversal() throws Exception {
        com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub)
                          new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.twoGatePay.Interface_pkg.ReversalResponse value = null;
        value = binding.reversal(new com.payment.twoGatePay.Interface_pkg.ReversalRequest());
        // TBD - validate results
    }

    public void test5InterfacePortRebill() throws Exception {
        com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub)
                          new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.twoGatePay.Interface_pkg.RebillResponse value = null;
        value = binding.rebill(new com.payment.twoGatePay.Interface_pkg.RebillRequest());
        // TBD - validate results
    }

    /*public void test6InterfacePortCapture() throws Exception {
        Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (Interface_pkg.InterfaceBindingStub)
                          new Interface_pkg._interfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Interface_pkg.CaptureResponse value = null;
        value = binding.capture(new Interface_pkg.CaptureRequest());
        // TBD - validate results
    }

    public void test7InterfacePortCancel() throws Exception {
        Interface_pkg.InterfaceBindingStub binding;
        try {
            binding = (Interface_pkg.InterfaceBindingStub)
                          new Interface_pkg._interfaceLocator().getInterfacePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException---->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Interface_pkg.CancelResponse value = null;
        value = binding.cancel(new Interface_pkg.CancelRequest());
        // TBD - validate results
    }*/

}
