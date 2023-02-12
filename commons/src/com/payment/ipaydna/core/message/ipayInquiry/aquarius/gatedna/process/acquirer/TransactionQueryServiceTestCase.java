/**
 * TransactionQueryServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer;

import com.directi.pg.TransactionLogger;

public class TransactionQueryServiceTestCase extends junit.framework.TestCase {

    private static TransactionLogger transactionLogger = new TransactionLogger(TransactionQueryServiceTestCase.class.getName());

    public TransactionQueryServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testTransactionQueryCfcWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryServiceLocator().getTransactionQueryCfcAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1TransactionQueryCfcGetRecord() throws Exception {
        com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub binding;
        try {
            binding = (com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub)
                          new com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryServiceLocator().getTransactionQueryCfc();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException TransactionQueryServiceTestCase :::::::::",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        try {
            java.lang.String value = null;
            value = binding.getRecord(new java.lang.String(), new java.lang.String(), new java.lang.String());
        }
        catch (com.payment.ipaydna.core.message.ipayRefund.coldfusion.xml.rpc.CFCInvocationException e1) {
            throw new junit.framework.AssertionFailedError("CFCInvocationException Exception caught: " + e1);
        }
            // TBD - validate results
    }

}
