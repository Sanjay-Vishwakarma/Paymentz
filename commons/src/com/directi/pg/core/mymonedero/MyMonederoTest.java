package com.directi.pg.core.mymonedero;

import com.directi.pg.Logger;
import com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesServiceTestCase;
import com.directi.pg.core.paymentgateway.MyMonederoPaymentGateway;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.MyMonederoRequestVO;
import com.directi.pg.core.valueObjects.MyMonederoResponseVO;

/**
 * Created by IntelliJ IDEA.
 * User: admin1
 * Date: 2/19/13
 * Time: 10:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoTest
{

    public static void main(String args[])
    {
        Logger logger = new Logger(MyMonederoTest.class.getName());
        ServicesServiceTestCase sstc=new ServicesServiceTestCase("TEST");

        try{

            logger.debug("TESTING 1 for redirecturl");
            sstc.test1servicesPortRedirectUrl();
        }
        catch(Exception e)
        {
            logger.error("Exception caught----",e);
        }

        try{

            logger.debug("TESTING 2 fro getPaymentStatus");
            sstc.test2servicesPortGetPaymentStatus();
        }
        catch(Exception e)
        {
            logger.error("Exception caught----",e);
        }


        try{

            logger.debug("Trying to test WSDL service using : servicesPort_address = \"https://demomonedero.wavecrest.gi/monedero/service\"");
            sstc.testservicesPortWSDL();
        }
        catch(Exception e)
        {
            logger.error("Exception caught::::::",e);
        }

        MyMonederoResponseVO ewalletResponseVO=new MyMonederoResponseVO();
        //System.out.println("Calling ");
        MyMonederoPaymentGateway pg= new MyMonederoPaymentGateway();


/*
        try{
            System.out.println("testing conn");
            pg.testPaymentStatus();
        }
        catch(Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
*/


        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        genericTransDetailsVO.setAmount("2");
        genericTransDetailsVO.setCurrency("GBP");
        genericTransDetailsVO.setOrderId("Testing");
        genericTransDetailsVO.setOrderDesc("This is a test order");

        MyMonederoRequestVO ewalletRequestVO= new MyMonederoRequestVO(genericTransDetailsVO,"10103");

        try{
            ewalletResponseVO =(MyMonederoResponseVO) pg.processSale("10030002", ewalletRequestVO);
            logger.debug("Received  response");
            //Print Response


        }
        catch(Exception e)
        {
            logger.error(e);
        }


       /* System.out.println("Error ---"+ewalletResponseVO.getError());
        System.out.println("Status---"+ewalletResponseVO.getStatus());
        System.out.println("Tracking ID--"+ewalletResponseVO.getTrackingid());
        System.out.println("WCTXNID--"+ewalletResponseVO.getWctxnid());
        System.out.println("redirecturl--"+ewalletResponseVO.getRedirecturl());*/





    }
    public static MyMonederoResponseVO test()
    {
        Logger log=new Logger(MyMonederoTest.class.getName());
        ServicesServiceTestCase sstc=new ServicesServiceTestCase("TEST");

        try{

            log.debug("TESTING 1 for redirecturl");
            sstc.test1servicesPortRedirectUrl();
        }
        catch(Exception e)
        {
            log.error("Exception caught----",e);
        }

        try{

            log.debug("TESTING 2 fro getPaymentStatus");
            sstc.test2servicesPortGetPaymentStatus();
        }
        catch(Exception e)
        {
            log.error("Exception caught----",e);
        }


        try{

            log.debug("Trying to test WSDL service using : servicesPort_address = \"https://demomonedero.wavecrest.gi/monedero/service\"");
            sstc.testservicesPortWSDL();
        }
        catch(Exception e)
        {
            log.error("Exception caught::::::",e);
        }




        MyMonederoResponseVO ewalletResponseVO=new MyMonederoResponseVO();
        //System.out.println("Calling ");
        MyMonederoPaymentGateway pg= new MyMonederoPaymentGateway();


/*
        try{
            System.out.println("testing conn");
            pg.testPaymentStatus();
        }
        catch(Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
*/


        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        genericTransDetailsVO.setAmount("2");
        genericTransDetailsVO.setCurrency("GBP");
        genericTransDetailsVO.setOrderId("Testing");
        genericTransDetailsVO.setOrderDesc("This is a test order");

        MyMonederoRequestVO ewalletRequestVO= new MyMonederoRequestVO(genericTransDetailsVO,"10103");

        try{
            ewalletResponseVO =(MyMonederoResponseVO) pg.processSale("10030002", ewalletRequestVO);
            log.debug("Received  response");
            //Print Response


        }
        catch(Exception e)
        {
            log.error(e);
        }


       /* System.out.println("Error ---"+ewalletResponseVO.getError());
        System.out.println("Status---"+ewalletResponseVO.getStatus());
        System.out.println("Tracking ID--"+ewalletResponseVO.getTrackingid());
        System.out.println("WCTXNID--"+ewalletResponseVO.getWctxnid());
        System.out.println("redirecturl--"+ewalletResponseVO.getRedirecturl());*/



      return ewalletResponseVO;

    }

    
    
}
