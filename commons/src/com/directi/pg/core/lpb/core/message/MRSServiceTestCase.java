/**
 * MRSServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

import com.directi.pg.Logger;

public class MRSServiceTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(MRSServiceTestCase.class.getName());
    public MRSServiceTestCase(String name) {
        super(name);
    }

    public void testMRSPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new MRSServiceLocator().getMRSPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new MRSServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1MRSPortMrsREQUEST() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsREQUEST(new MrsDATA());
        // TBD - validate results
    }

    public void test2MRSPortMrsDECLINE() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsDECLINE(new MrsDATA());
        // TBD - validate results
    }

    public void test3MRSPortMrsUNAPPROVE() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsUNAPPROVE(new MrsDATA());
        // TBD - validate results
    }

    public void test4MRSPortMrsAPPROVE() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsAPPROVE(new MrsDATA());
        // TBD - validate results
    }

    public void test5MRSPortMrsDEPOSIT() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsDEPOSIT(new MrsDATA());
        // TBD - validate results
    }

    public void test6MRSPortMrsREVERSE() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsREVERSE(new MrsDATA());
        // TBD - validate results
    }

    public void test7MRSPortMrsPROCESS() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsPROCESS(new MrsDATA());
        // TBD - validate results
    }

    public void test8MRSPortMrsRETURN() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsRETURN(new MrsDATA());
        // TBD - validate results
    }

    public void test9MRSPortMrsCLOSE() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsCLOSE(new MrsDATA());
        // TBD - validate results
    }

    public void test10MRSPortMrsARCHIVE() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsARCHIVE(new MrsDATA());
        // TBD - validate results
    }

    public void test11MRSPortMrsSTATUS() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsSTATUS(new MrsDATA());
        // TBD - validate results
    }

    public void test12MRSPortMrsTOKENACQ() throws Exception {
        MRSBindingStub binding;
        try {
            binding = (MRSBindingStub)
                          new MRSServiceLocator().getMRSPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        MrsDATA value = null;
        value = binding.mrsTOKENACQ(new MrsDATA());
        // TBD - validate results
    }

}
