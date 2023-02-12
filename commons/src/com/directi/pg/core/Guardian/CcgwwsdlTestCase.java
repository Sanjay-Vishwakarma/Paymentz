/**
 * CcgwwsdlTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.Guardian;

import com.directi.pg.Logger;

public class CcgwwsdlTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(CcgwwsdlTestCase.class.getName());
    public CcgwwsdlTestCase(String name) {
        super(name);
    }

    public void testccgwwsdlPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new CcgwwsdlLocator().getccgwwsdlPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new CcgwwsdlLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1ccgwwsdlPortNewTransaction() throws Exception {
        CcgwwsdlBindingStub binding;
        try {
            binding = (CcgwwsdlBindingStub)
                          new CcgwwsdlLocator().getccgwwsdlPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                logger.error("IOException---->", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Response value = null;
        value = binding.newTransaction(new Request());
        // TBD - validate results
    }

    public void test2ccgwwsdlPortRefund() throws Exception {
        CcgwwsdlBindingStub binding;
        try {
            binding = (CcgwwsdlBindingStub)
                          new CcgwwsdlLocator().getccgwwsdlPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("IOException---->", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        ResponseRefund value = null;
        value = binding.refund(new RequestRefund());
        // TBD - validate results
    }

}
