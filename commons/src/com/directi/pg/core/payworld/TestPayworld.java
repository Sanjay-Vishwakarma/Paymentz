package com.directi.pg.core.payworld;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.common.core.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 5/1/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPayworld
{
    static Logger logger = new Logger(TestPayworld.class.getName());

    public static void main(String[] aa)
    {
        processAuthenticationWithCapture();
        //System.out.println("Calling auth ====");
    }

    public static void processAuthenticationWithCapture()
    {
        AbstractPaymentGateway pg = null;
        String accountid ="145";

        CommRequestVO requestVO=new CommRequestVO();
        CommAddressDetailsVO addr=new CommAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommResponseVO responce=new CommResponseVO();
        String guid="";
        String pwd="";
        String rs="";

        String merchant_transaction_id="00056";
        String userip="127.0.0.1";
        String desc="order1234";

        String amount="2150";
        String currency="USD";
        String nameonCard="John";

        String street="dhgsajdhA";
        String zip="1234567";
        String city="mumbai";

        String country="india";
        String state="maharashtra";
        String email="nishant@pz.com";

        String phone="1234567890";
        String bin="4543474002249996";

        addr.setStreet(street);
        addr.setIp(userip);
        addr.setCity(city);
        addr.setCountry(country);
        addr.setEmail(email);
        addr.setPhone(phone);
        addr.setZipCode(zip);
        addr.setState(state);

        transactionDetailsVO.setAmount(amount);
        transactionDetailsVO.setCurrency(currency);
        transactionDetailsVO.setOrderId(merchant_transaction_id);
        transactionDetailsVO.setOrderDesc(desc);

        commCardDetailsVO.setCardNum(bin);
        commCardDetailsVO.setCardHolderName(nameonCard);

        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setAddressDetailsVO(addr);
        requestVO.setTransDetailsVO(transactionDetailsVO);

        try
        {   pg = AbstractPaymentGateway.getGateway(accountid);

            logger.debug(AbstractPaymentGateway.getGateway(accountid));
            //responce = (CommResponseVO) pg.processSale(merchant_transaction_id,requestVO);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError--->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        logger.error(responce);


    }

}
