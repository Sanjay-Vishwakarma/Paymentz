/**
 * WsanularTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_cancel;

import com.directi.pg.Logger;

public class WsanularTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(WsanularTestCase.class.getName());
    public WsanularTestCase(java.lang.String name) {
        super(name);
    }

    public void testwsanularSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.visaNet.com_cancel.WsanularLocator().getwsanularSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.visaNet.com_cancel.WsanularLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1wsanularSoap12AnularPedido() throws Exception {
        com.payment.visaNet.com_cancel.WsanularSoap12Stub binding;
        try {
            binding = (com.payment.visaNet.com_cancel.WsanularSoap12Stub)
                          new com.payment.visaNet.com_cancel.WsanularLocator().getwsanularSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anularPedido(new java.lang.String());
        // TBD - validate results
    }

    public void test2wsanularSoap12AnularEticket() throws Exception {
        com.payment.visaNet.com_cancel.WsanularSoap12Stub binding;
        try {
            binding = (com.payment.visaNet.com_cancel.WsanularSoap12Stub)
                          new com.payment.visaNet.com_cancel.WsanularLocator().getwsanularSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anularEticket(new java.lang.String());
        // TBD - validate results
    }

    public void testwsanularSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.visaNet.com_cancel.WsanularLocator().getwsanularSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.visaNet.com_cancel.WsanularLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test3wsanularSoapAnularPedido() throws Exception {
        com.payment.visaNet.com_cancel.WsanularSoapStub binding;
        try {
            binding = (com.payment.visaNet.com_cancel.WsanularSoapStub)
                          new com.payment.visaNet.com_cancel.WsanularLocator().getwsanularSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anularPedido(new java.lang.String());
        // TBD - validate results
    }

    public void test4wsanularSoapAnularEticket() throws Exception {
        com.payment.visaNet.com_cancel.WsanularSoapStub binding;
        try {
            binding = (com.payment.visaNet.com_cancel.WsanularSoapStub)
                          new com.payment.visaNet.com_cancel.WsanularLocator().getwsanularSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.anularEticket(new java.lang.String());
        // TBD - validate results
    }

}
