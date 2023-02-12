package com.directi.pg.core.pay132;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.Pay132PaymentGateway;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 10, 2012
 * Time: 10:00:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPay132
{
    private static Logger log = new Logger(TestPay132.class.getName());


    public static void main(String[] args)
    {

        //System.out.println("Calling auth ====");
               processAuth();

    }

    public static void processAuth()
    {
     Pay132PaymentGateway pg = null;
     String accountid ="142";

     CommRequestVO pay132RequestVO = new CommRequestVO();
     CommResponseVO  pay132ResponseVO = new CommResponseVO();
     CommCardDetailsVO cardDetails = new CommCardDetailsVO();
     CommAddressDetailsVO billingAddr = new CommAddressDetailsVO();
     CommTransactionDetailsVO transDetails = new CommTransactionDetailsVO();


     try
        {
            log.debug("Getting Abstarct gateway===  account Id="+accountid);
            pg =(Pay132PaymentGateway) AbstractPaymentGateway.getGateway(accountid);

           // if(PayVTPaymentGateway.class.isIs)
        }
        catch (SystemError systemError)
        {   log.error("System Error:::::::::"+systemError);


        }


        InetAddress ownIP = null;
        try{
             ownIP=InetAddress.getLocalHost();
             log.debug("IP of my system is := "+ownIP.getHostAddress());
       }catch (Exception e){
       log.error("Exception caught ="+e.getMessage());
       }


        String  transAmount = "100";
        String trackingID="13211116";
        String  currency ="USD";
        String  ipaddress = ownIP.getHostAddress();
        String  orderDes = "Test Item123";
        String  firstname = "Joanne";
        String  lastname = "Farley";
        String  name = "Joanne Farley";
        String  address1 = "42 Lake Road";
        String  city = "Clemons";
        String  state = "NY";
        String  ccode = "US";
        String  zip = "12819";
        String  phone = "1234567890";
        String  email = "joannefarley@mail.com";
        String  cardnum = "4111111111111111";
        String cardtype ="VISA";
        String cvv ="243";
        String expMonth ="05";
        String expyear="22";



         billingAddr.setFirstname(firstname);
         billingAddr.setLastname(lastname);
         billingAddr.setCity(city);
         billingAddr.setState(state);
         billingAddr.setZipCode(zip);
         billingAddr.setCountry(ccode);
         billingAddr.setPhone(phone);
         billingAddr.setEmail(email);;
         billingAddr.setIp(ipaddress);
         billingAddr.setStreet(address1);


         transDetails.setAmount(transAmount);
         transDetails.setCurrency(currency);
         transDetails.setOrderDesc(orderDes);

         cardDetails.setCardNum(cardnum);
         cardDetails.setCardType(cardtype);
         cardDetails.setcVV(cvv);
         cardDetails.setExpMonth(expMonth);
         cardDetails.setExpYear(expyear);



         pay132RequestVO.setAddressDetailsVO(billingAddr);
         pay132RequestVO.setTransDetailsVO(transDetails);
         pay132RequestVO.setCardDetailsVO(cardDetails);

         try
         {
         pay132ResponseVO = (CommResponseVO)pg.processSale(trackingID,pay132RequestVO);
         }
         catch (PZTechnicalViolationException e)
         {
             log.debug("Exception Thrown "+e.getMessage());
             PZExceptionHandler.handleTechicalCVEException(e,"trackingId::::"+trackingID,"Process auth of pay132");
         }
         catch (PZConstraintViolationException e)
         {
             log.error("Exception Thrown "+e.getMessage());
             PZExceptionHandler.handleCVEException(e,"trackingId::::"+trackingID,"Process auth of pay132");
         }


      /*  System.out.println(pay132ResponseVO.getAmount());
        System.out.println(pay132ResponseVO.getStatus());
        System.out.println(pay132ResponseVO.getDescription());
        System.out.println(pay132ResponseVO.getDescriptor());
        System.out.println(pay132ResponseVO.getErrorCode());*/


    }

}
