package com.directi.pg.core.ecore;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Aug 24, 2012
 * Time: 8:53:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestEcore
{

    static Logger logger = new Logger(TestEcore.class.getName());


       public static void main(String[] aa)
           {


               //System.out.println("Calling auth ====");
              //EcoreInquiry();
               EcoreRefund();
              // processAuthenticationWithCapture();

           }


       public static void processAuthenticationWithCapture()
       {
          AbstractPaymentGateway pg = null;
          String accountid ="6";

           GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
           EcoreAddressDetailsVO AddressDetail= new EcoreAddressDetailsVO();
           EcoreTransDetailsVO TransDetail = new EcoreTransDetailsVO();
           EcoreRequestVO requestDetail=null;
           EcoreResponseVO  transRespDetails=null;
           try
          {
              //System.out.println("Getting Abstarct gateway===  account Id="+accountid);
              //System.out.println("Getting Abstarct gateway===  account Id="+AbstractPaymentGateway.getGateway(accountid));
              pg = AbstractPaymentGateway.getGateway(accountid);

             // if(PayVTPaymentGateway.class.isIs)
          }
          catch (SystemError systemError)
          {   logger.debug("System Error:::::::::"+systemError);

          }

          InetAddress ownIP = null;
          try{
               ownIP=InetAddress.getLocalHost();
               logger.debug("IP of my system is := "+ownIP.getHostAddress());
         }catch (Exception e){
         logger.error("Exception caught ="+e.getMessage());
         }




          String  trackingID = "143";
          String  currency = pg.getCurrency();

          String  orderDes = "Order5007";

           String amount="100.67";
           String  ccnum = "4543474002249996";
           String  ccid = "105";
           String  year = "17";
           String  month = "06";
           String tredtime="20121119052020";
           String  firstname = "Test";
           String  lastname = "User";
           String  address = "123 Test St";
           String  city = "Testville";
           String  state = "TT";
           String  ccode = "US";
           String  zip = "1234";
           String  phone = "1234567890";
           String  email = "test@test.com";
           String language="ENG";
           String returnurl="http://192.168.2.11:8081/support/paymentResponse.jsp";
           String MD5INFO="1be3f2894feff8820ae3538ff05e7aaa" ;
           String md5key="31F4EA0542E6F887F0BB8DF52A1EC899";
           String birthDay="19751211" ;
           String ssn="1234";
         try
         {  //cardDetailVO.setCardHolderName(name);
             cardDetail.setCardNum(ccnum);
             cardDetail.setcVV(ccid);
             cardDetail.setExpMonth(month);
             cardDetail.setExpYear(year);

             AddressDetail.setTime(tredtime);
             AddressDetail.setFirstname(firstname);
             AddressDetail.setLastname(lastname);
             AddressDetail.setLanguage(language);
             AddressDetail.setCity(city);
             AddressDetail.setCountry("IN");
             AddressDetail.setProducts(orderDes);
             AddressDetail.setState(state);
             AddressDetail.setZipCode(zip);
             AddressDetail.setStreet(address);
             AddressDetail.setPhone(phone);
             AddressDetail.setEmail(email);
             AddressDetail.setIp("127.0.0.1");
             AddressDetail.setMd5info(MD5INFO);
             AddressDetail.setBirthdate(birthDay);
             AddressDetail.setSsn(ssn);
             AddressDetail.setMd5key(md5key);
             TransDetail.setMerNo("88894");
             TransDetail.setAmount(amount);
             TransDetail.setCurrency(currency);
             TransDetail.setOrderId("123");
             TransDetail.setOrderDesc(orderDes);

          requestDetail = new EcoreRequestVO(cardDetail,AddressDetail, TransDetail);
          transRespDetails = (EcoreResponseVO) pg.processSale(trackingID, requestDetail);

             /*System.out.println(transRespDetails.getRemark());
             System.out.println(transRespDetails.getDateTime());
             System.out.println(transRespDetails.getBillingDescriptor());
             System.out.println(transRespDetails.getMd5Info());
             System.out.println(transRespDetails.getBillNo());
             System.out.println(transRespDetails.getMerNo());
             System.out.println(transRespDetails.getPaymentOrderNo());
             System.out.println(transRespDetails.getResultCode());*/

         }
       catch(Exception e)
       {
           logger.error("Excpetion Thrown ",e);
       }

    }

    public static void EcoreInquiry()
    {
        AbstractPaymentGateway pg = null;
        String accountid ="6";

        GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
        EcoreAddressDetailsVO AddressDetail= new EcoreAddressDetailsVO();
        EcoreTransDetailsVO TransDetail = new EcoreTransDetailsVO();
        EcoreRequestVO requestDetail=null;
        EcoreResponseVO  transRespDetails=null;
        try
        {
            logger.debug("Getting Abstarct gateway===  account Id="+accountid);
            //System.out.println("Getting Abstarct gateway===  account Id="+AbstractPaymentGateway.getGateway(accountid));
            pg = AbstractPaymentGateway.getGateway(accountid);
        }
        catch (SystemError systemError)
        {  logger.error("System Error:::::::::"+systemError);
        }
        //System.out.println("year------");
        String merno="88888";
        String  orderDes = "Order064";
        String key ="CCA4C06876088FCCDFA5D1DC1079BC50";
        String MD5INFO=null ;
        try
        {
            MD5INFO= Functions.convertmd5(merno+orderDes+key);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException---->",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("UnsupportedEncodingException---->", e);  //To change body of catch statement use File | Settings | File Templates.
        }


        try
        {

            AddressDetail.setMd5info(MD5INFO);

            TransDetail.setMerNo("88888");

            TransDetail.setOrderDesc(orderDes);

            requestDetail = new EcoreRequestVO(cardDetail,AddressDetail, TransDetail);
            transRespDetails = (EcoreResponseVO) pg.processInquiry(requestDetail);

            /*System.out.println("done"  +transRespDetails.getAmount() );
            System.out.println(transRespDetails.getRemark());
            System.out.println(transRespDetails.getDateTime());
            System.out.println(transRespDetails.getBillingDescriptor());
            System.out.println(transRespDetails.getMd5Info());
            System.out.println(transRespDetails.getBillNo());*/


        }
        catch(Exception e)
        {
            logger.error("Excpetion Thrown "+e.getMessage());
        }

    }

    public static void EcoreRefund()
    {
        AbstractPaymentGateway pg = null;
        String accountid ="6";

        GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
        EcoreAddressDetailsVO AddressDetail= new EcoreAddressDetailsVO();
        EcoreTransDetailsVO TransDetail = new EcoreTransDetailsVO();
        EcoreRequestVO requestDetail=null;
        EcoreResponseVO  transRespDetails=null;
        try
        {
            //System.out.println("Getting Abstarct gateway===  account Id="+accountid);
            //System.out.println("Getting Abstarct gateway===  account Id="+AbstractPaymentGateway.getGateway(accountid));
            pg = AbstractPaymentGateway.getGateway(accountid);
        }
        catch (SystemError systemError)
        {   logger.error("System Error:::::::::"+systemError);
        }
        String operation="02";
        String amount="100.67";
        String refundAmount="80.67";
        String tid="150" ;

        try
        {

            TransDetail.setOperation(operation);
            TransDetail.setPaymentOrderNo("888881499106");
            TransDetail.setBillNo("Order086");
            TransDetail.setAmount(amount);
            TransDetail.setRefundAmount(refundAmount);


            requestDetail = new EcoreRequestVO(cardDetail,AddressDetail, TransDetail);
            transRespDetails = (EcoreResponseVO) pg.processRefund(tid,requestDetail);

            /*System.out.println("done"  +transRespDetails.getResultCode() );
            System.out.println("done"  +transRespDetails.getPaymentOrderNo() );
            System.out.println("done"  +transRespDetails.getOperation() );
            System.out.println("done"  +transRespDetails.getBillNo() );
            System.out.println("done"  +transRespDetails.getRefundAmount() );*/




        }
        catch(Exception e)
        {
            logger.error("Excpetion Thrown "+e.getMessage());
        }

    }
}
