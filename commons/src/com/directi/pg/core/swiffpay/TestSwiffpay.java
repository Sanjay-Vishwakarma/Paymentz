package com.directi.pg.core.swiffpay;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.SwiffpayRequestVO;
import com.directi.pg.core.valueObjects.SwiffpayResponseVO;

import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/2/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSwiffpay
{
    static Logger logger = new Logger(TestSwiffpay.class.getName());
    public static void main(String args[])
    {
        //processSale();
        processinquery();
        //System.out.println("TESTING ENDED SUCCESSFULLY");
    }

    public static void processinquery()
    {
        AbstractPaymentGateway pg = null;

        CommCardDetailsVO cardDetail= new CommCardDetailsVO();
        CommAddressDetailsVO AddressDetail= new CommAddressDetailsVO();
        CommTransactionDetailsVO TransDetail = new CommTransactionDetailsVO();
        SwiffpayRequestVO requestDetail=new SwiffpayRequestVO();
        SwiffpayResponseVO transRespDetails=new SwiffpayResponseVO();

        String trackingid="172";

        requestDetail.setMerchantordernumber("172");
        requestDetail.setIpaddress("127.0.0.1");
        requestDetail.setHistoryid("286485732");
        requestDetail=new SwiffpayRequestVO();
        requestDetail.setCardDetailsVO(cardDetail);
        requestDetail.setAddressDetailsVO(AddressDetail);
        requestDetail.setTransDetailsVO(TransDetail);

        try
        {   pg = AbstractPaymentGateway.getGateway("146");
            transRespDetails = (SwiffpayResponseVO) pg.processQuery(trackingid,requestDetail);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError---->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraintViolationException---->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (PZGenericConstraintViolationException e)
        {
            logger.error("PZGenericConstraintViolationException---->", e);  //To change body of catch statement use File | Settings | File Templates.
        }

       /* System.out.println(transRespDetails.getTransactionId());
        System.out.println(transRespDetails.getMerchantOrderId());
        System.out.println(transRespDetails.getStatus());
        System.out.println(transRespDetails.getErrorCode());
        System.out.println(transRespDetails.getDescription());*/
    }

    public static void processSale()
    {
        //System.out.println("TESTING");
        AbstractPaymentGateway pg = null;

        CommCardDetailsVO cardDetail= new CommCardDetailsVO();
        CommAddressDetailsVO AddressDetail= new CommAddressDetailsVO();
        CommTransactionDetailsVO TransDetail = new CommTransactionDetailsVO();
        SwiffpayRequestVO requestDetail=new SwiffpayRequestVO();
        SwiffpayResponseVO transRespDetails=new SwiffpayResponseVO();

        String transid="11353454";
        String orderid="7ddf32e1cbf2";
        String ip="127.0.0.1";
        String amount="25";
        String currency="USD";

        String cCnumber="5454545454545454";
        String address="1110 Main Ave";
        String zip="90266";
        String city="Los Angeles";
        String state="California";
        String ccode="US";
        String phone="NA";
        String emailaddr="john.doe@example.com";
        String cvv="956";
        String cMonth="05";
        String cYear="16";

        AddressDetail.setCity(city);
        AddressDetail.setCountry(ccode);
        AddressDetail.setEmail(emailaddr);
        AddressDetail.setIp(ip);
        AddressDetail.setPhone(phone);
        AddressDetail.setState(state);
        AddressDetail.setStreet(address);
        AddressDetail.setZipCode(zip);
        AddressDetail.setFirstname("JohnDoe");
        AddressDetail.setLastname("Soap");

        cardDetail.setExpMonth(cMonth);
        cardDetail.setExpYear(cYear);
        cardDetail.setCardNum(cCnumber);
        cardDetail.setcVV(cvv);


        TransDetail.setCurrency(currency);
        TransDetail.setAmount(amount);
        TransDetail.setOrderId(orderid);
        String accountid="146";


        requestDetail=new SwiffpayRequestVO();
        requestDetail.setCardDetailsVO(cardDetail);
        requestDetail.setAddressDetailsVO(AddressDetail);
        requestDetail.setTransDetailsVO(TransDetail);

        try
        {   pg = AbstractPaymentGateway.getGateway(accountid);

            //System.out.println(AbstractPaymentGateway.getGateway(accountid));
            transRespDetails = (SwiffpayResponseVO) pg.processAuthentication(transid,requestDetail);
        }
        catch (Exception e)
        {
            logger.error("Exception---->",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        /*System.out.println(transRespDetails.getTransactionId());
        System.out.println(transRespDetails.getMerchantOrderId());
        System.out.println(transRespDetails.getStatus());
        System.out.println(transRespDetails.getErrorCode());
        System.out.println(transRespDetails.getDescription());*/


    }
}
