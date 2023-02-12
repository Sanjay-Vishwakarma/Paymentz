/**
 * SrvprocesoTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.procesosmc.org.tempuri;

import com.directi.pg.Logger;
import com.payment.procesosmc.org.tempuri.depositcancel.SrvprocesoLocator;

public class SrvprocesoTestCase extends junit.framework.TestCase {
    private static Logger logger=new Logger(SrvprocesoTestCase.class.getName());
    public SrvprocesoTestCase(String name) {
        super(name);
    }

    public void testBasicHttpBinding_IsrvprocesoWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new SrvprocesoLocator().getBasicHttpBinding_IsrvprocesoAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new SrvprocesoLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1BasicHttpBinding_IsrvprocesoDeposito() throws Exception {
        BasicHttpBinding_IsrvprocesoStub binding;
        try {
            binding = (BasicHttpBinding_IsrvprocesoStub)
                          new SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String value = null;
        value = binding.deposito(new String());
        // TBD - validate results
    }

    public void test2BasicHttpBinding_IsrvprocesoAnulacion() throws Exception {
        BasicHttpBinding_IsrvprocesoStub binding;
        try {
            binding = (BasicHttpBinding_IsrvprocesoStub)
                          new SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String value = null;
        value = binding.anulacion(new String());
        // TBD - validate results
    }

    public void test3BasicHttpBinding_IsrvprocesoConfirma() throws Exception {
        BasicHttpBinding_IsrvprocesoStub binding;
        try {
            binding = (BasicHttpBinding_IsrvprocesoStub)
                          new SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String value = null;
        value = binding.confirma(new String());
        // TBD - validate results
    }

    public void test4BasicHttpBinding_IsrvprocesoConsulta() throws Exception {
        BasicHttpBinding_IsrvprocesoStub binding;
        try {
            binding = (BasicHttpBinding_IsrvprocesoStub)
                          new SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String value = null;
        value = binding.consulta(new String());
        // TBD - validate results
    }

    public void test5BasicHttpBinding_IsrvprocesoTest() throws Exception {
        BasicHttpBinding_IsrvprocesoStub binding;
        try {
            binding = (BasicHttpBinding_IsrvprocesoStub)
                          new SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String value = null;
        value = binding.test(new String());
        // TBD - validate results
    }

    public void test6BasicHttpBinding_IsrvprocesoAutoriza() throws Exception {
        BasicHttpBinding_IsrvprocesoStub binding;
        try {
            binding = (BasicHttpBinding_IsrvprocesoStub)
                          new SrvprocesoLocator().getBasicHttpBinding_Isrvproceso();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String value = null;
        value = binding.autoriza(new String());
        // TBD - validate results
    }

}
