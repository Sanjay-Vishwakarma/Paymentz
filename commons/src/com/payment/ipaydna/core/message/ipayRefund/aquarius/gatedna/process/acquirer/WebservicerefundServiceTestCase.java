/**
 * WebservicerefundServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer;

import com.directi.pg.TransactionLogger;

public class WebservicerefundServiceTestCase extends junit.framework.TestCase {

    private static TransactionLogger transactionLogger = new TransactionLogger(WebservicerefundServiceTestCase.class.getName());

    public WebservicerefundServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testWebservicerefundCfcWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundServiceLocator().getWebservicerefundCfcAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1WebservicerefundCfcRefund() throws Exception {
        com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub binding;
        try {
            binding = (com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub)
                          new com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundServiceLocator().getWebservicerefundCfc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::::::::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            java.lang.String value = null;
            value = binding.refund(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        }
        catch (com.payment.ipaydna.core.message.ipayRefund.coldfusion.xml.rpc.CFCInvocationException e1) {
            throw new junit.framework.AssertionFailedError("CFCInvocationException Exception caught: " + e1);
        }
            // TBD - validate results
    }

}
