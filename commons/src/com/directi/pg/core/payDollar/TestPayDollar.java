package com.directi.pg.core.payDollar;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;

import com.directi.pg.core.paymentgateway.PayDollarPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 24, 2012
 * Time: 7:29:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPayDollar
{
    static Logger logger = new Logger(TestPayDollar.class.getName());

    public static void main(String[] aa)
   {


       //System.out.println("Calling  AUTH SUCCESS Inquiry ====");


       CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
       transDetailsVO.setAmount(String.valueOf("4.4"));
       transDetailsVO.setCurrency("USD");
       transDetailsVO.setOrderId(String.valueOf(10139));
       transDetailsVO.setOrderDesc("test cancel");

       

       PayDollarRequestVO commRequestVO = new PayDollarRequestVO();
       commRequestVO.setPayRef("1179418");

       
       commRequestVO.setTransDetailsVO(transDetailsVO);

       PayDollarPaymentGateway pg=null;
       try{

           pg=new PayDollarPaymentGateway("8");
       
           pg.processQuery("10147",commRequestVO);
       
       
       }
       catch(Exception e)
       {
           //System.out.println("EXCEPTION OCCURED");
       }


       //System.out.println("CALLLING CAPTURESUCCESS INQUIRY");

       transDetailsVO.setAmount(String.valueOf("12"));
       transDetailsVO.setCurrency("USD");
       transDetailsVO.setOrderId(String.valueOf(10139));




       commRequestVO = new PayDollarRequestVO();
       commRequestVO.setPayRef("1175873");


       commRequestVO.setTransDetailsVO(transDetailsVO);

       try{

           pg=new PayDollarPaymentGateway("8");

           pg.processQuery("10139",commRequestVO);


       }
       catch(Exception e)
       {
           //System.out.println("EXCEPTION OCCURED");
       }


       //System.out.println("Calling INQUIRY FOR REVERSED (VOIDED) transaction");

       transDetailsVO.setAmount(String.valueOf("6.1"));
       transDetailsVO.setCurrency("USD");
       transDetailsVO.setOrderId(String.valueOf(10134));




       commRequestVO = new PayDollarRequestVO();
       commRequestVO.setPayRef("1179404");


       commRequestVO.setTransDetailsVO(transDetailsVO);

       try{

           pg=new PayDollarPaymentGateway("8");

           pg.processQuery("10134",commRequestVO);


       }
       catch(Exception e)
       {
           //System.out.println("EXCEPTION OCCURED");
       }


   }

}
