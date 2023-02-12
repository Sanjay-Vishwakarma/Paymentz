/**
 * EcomServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

import com.directi.pg.Logger;

public class EcomServiceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(EcomServiceTestCase.class.getName());
    public EcomServiceTestCase(String name) {
        super(name);
    }

    public void testEcomPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new EcomServiceLocator().getEcomPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new EcomServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1EcomPortPayment() throws Exception {
        EcomBindingStub binding;
        try {
            binding = (EcomBindingStub)
                          new EcomServiceLocator().getEcomPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        EcomPaymentType value = null;
        value = binding.payment(new EcomPaymentType());
        // TBD - validate results
    }

    public void test2EcomPortAuthenticate() throws Exception {
        EcomBindingStub binding;
        try {
            binding = (EcomBindingStub)
                          new EcomServiceLocator().getEcomPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        EcomPaymentType value = null;
        value = binding.authenticate(new EcomPaymentType());
        // TBD - validate results
    }

    public void test3EcomPortDeposit() throws Exception {
        EcomBindingStub binding;
        try {
            binding = (EcomBindingStub)
                          new EcomServiceLocator().getEcomPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        EcomPaymentType value = null;
        value = binding.deposit(new EcomPaymentType());
        // TBD - validate results
    }

    public void test4EcomPortReverse() throws Exception {
        EcomBindingStub binding;
        try {
            binding = (EcomBindingStub)
                          new EcomServiceLocator().getEcomPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        EcomPaymentType value = null;
        value = binding.reverse(new EcomPaymentType());
        // TBD - validate results
    }

    public void test5EcomPortClose() throws Exception {
        EcomBindingStub binding;
        try {
            binding = (EcomBindingStub)
                          new EcomServiceLocator().getEcomPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        EcomPaymentType value = null;
        value = binding.close(new EcomPaymentType());
        // TBD - validate results
    }

    public void test6EcomPortGetPayment() throws Exception {
        EcomBindingStub binding;
        try {
            binding = (EcomBindingStub)
                          new EcomServiceLocator().getEcomPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        EcomPaymentType value = null;
        value = binding.getPayment(new EcomPaymentType());
        // TBD - validate results
    }

    public void test7EcomPortRegisterToken() throws Exception {
        EcomBindingStub binding;
        try {
            binding = (EcomBindingStub)
                          new EcomServiceLocator().getEcomPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        EcomPaymentType value = null;
        value = binding.registerToken(new EcomPaymentType());
        // TBD - validate results
    }

}
