/**
 * WebservicesaleServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
   //com\payment\ipaydna\core\message\aquarius\gatedna\process\acquirer
package com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer;

import com.directi.pg.TransactionLogger;

public class WebservicesaleServiceTestCase extends junit.framework.TestCase {

    private static TransactionLogger transactionLogger = new TransactionLogger(WebservicesaleServiceTestCase.class.getName());

    public WebservicesaleServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testWebservicesaleCfcWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleServiceLocator().getWebservicesaleCfcAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1WebservicesaleCfcPayment() throws Exception {
        com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub binding;
        try {
            binding = (com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub)
                          new com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleServiceLocator().getWebservicesaleCfc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException  ::::::",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            java.lang.String value = null;
            value = binding.payment(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        }
        //com\payment\ipaydna\core\message\coldfusion\xml\rpc
        catch (com.payment.ipaydna.core.message.coldfusion.xml.rpc.CFCInvocationException e1) {
            throw new junit.framework.AssertionFailedError("CFCInvocationException Exception caught: " + e1);
        }
            // TBD - validate results
    }

}
