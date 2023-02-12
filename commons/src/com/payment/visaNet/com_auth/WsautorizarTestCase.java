/**
 * WsautorizarTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_auth;

import com.directi.pg.Logger;

public class WsautorizarTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(WsautorizarTestCase.class.getName());
    public WsautorizarTestCase(java.lang.String name) {
        super(name);
    }

    public void testwsautorizarSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.visaNet.com_auth.WsautorizarLocator().getwsautorizarSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.visaNet.com_auth.WsautorizarLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1wsautorizarSoap12AutorizarTransaccion() throws Exception {
        com.payment.visaNet.com_auth.WsautorizarSoap12Stub binding;
        try {
            binding = (com.payment.visaNet.com_auth.WsautorizarSoap12Stub)
                          new com.payment.visaNet.com_auth.WsautorizarLocator().getwsautorizarSoap12();
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
        value = binding.autorizarTransaccion(new java.lang.String());
        // TBD - validate results
    }

    public void testwsautorizarSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.visaNet.com_auth.WsautorizarLocator().getwsautorizarSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.visaNet.com_auth.WsautorizarLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test2wsautorizarSoapAutorizarTransaccion() throws Exception {
        com.payment.visaNet.com_auth.WsautorizarSoapStub binding;
        try {
            binding = (com.payment.visaNet.com_auth.WsautorizarSoapStub)
                          new com.payment.visaNet.com_auth.WsautorizarLocator().getwsautorizarSoap();
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
        value = binding.autorizarTransaccion(new java.lang.String());
        // TBD - validate results
    }

}
