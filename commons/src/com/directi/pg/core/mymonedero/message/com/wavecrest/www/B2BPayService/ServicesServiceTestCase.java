/**
 * ServicesServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService;

import com.directi.pg.Logger;

public class ServicesServiceTestCase extends junit.framework.TestCase
{
    private static Logger logger=new Logger(ServicesServiceTestCase.class.getName());
    public ServicesServiceTestCase(java.lang.String name)
    {
        super(name);
    }

    public void testservicesPortWSDL() throws Exception
    {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesServiceLocator().getservicesPortAddress() + "/B2BPayIntegrationService.wsdl");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1servicesPortRedirectUrl() throws Exception
    {
        com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub binding;
        try
        {
            binding = (com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub)
                    new com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesServiceLocator().getservicesPort();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            logger.error("ServiceException---->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.RedirectUrlResponse value = null;
        value = binding.redirectUrl(new com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.RedirectUrlRequest());
        // TBD - validate results
    }

    public void test2servicesPortGetPaymentStatus() throws Exception
    {
        com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub binding;
        try
        {
            binding = (com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub)
                    new com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesServiceLocator().getservicesPort();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            logger.error("ServiceException--->",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.GetPaymentStatusResponse value = null;
        value = binding.getPaymentStatus(new com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.GetPaymentStatusRequest());
        // TBD - validate results
    }

}
