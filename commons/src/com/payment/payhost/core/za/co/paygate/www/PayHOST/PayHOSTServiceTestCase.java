/**
 * PayHOSTServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

import com.directi.pg.Logger;

public class PayHOSTServiceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(PayHOSTServiceTestCase.class.getName());
    public PayHOSTServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testPayHOSTSoap11WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1PayHOSTSoap11SinglePayout() throws Exception {
        com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub binding;
        try {
            binding = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub)
                          new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutResponse value = null;
        value = binding.singlePayout(new com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutRequest());
        // TBD - validate results
    }

    public void test2PayHOSTSoap11SinglePayment() throws Exception {
        com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub binding;
        try {
            binding = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub)
                          new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse value = null;
        value = binding.singlePayment(new com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentRequest());
        // TBD - validate results
    }

    public void test3PayHOSTSoap11SingleFollowUp() throws Exception {
        com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub binding;
        try {
            binding = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub)
                          new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse value = null;
        value = binding.singleFollowUp(new com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpRequest());
        // TBD - validate results
    }

    public void test4PayHOSTSoap11SingleVault() throws Exception {
        com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub binding;
        try {
            binding = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub)
                          new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        //com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultResponse value = null;
        //value = binding.singleVault(new com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultRequest());
        // TBD - validate results
    }

    public void test5PayHOSTSoap11Ping() throws Exception {
        com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub binding;
        try {
            binding = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub)
                          new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        //com.payment.payhost.core.za.co.paygate.www.PayHOST.PingResponse value = null;
        //value = binding.ping(new com.payment.payhost.core.za.co.paygate.www.PayHOST.PingRequest());
        // TBD - validate results
    }

}
