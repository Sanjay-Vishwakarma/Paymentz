package com.directi.pg.core.ugspay;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.UGSPayRequestVO;
import com.directi.pg.core.valueObjects.UGSPayResponseVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommTransactionDetailsVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 12, 2013
 * Time: 2:30:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestUGSPay
{
    static Logger logger = new Logger(TestUGSPay.class.getName());
    public static void main(String args[])
    {
        processSale();
        //System.out.println("TESTING ENDED SUCCESSFULLY");
    }

    public static void processSale()
    {
        //System.out.println("TESTING");
        AbstractPaymentGateway pg = null;

        CommCardDetailsVO cardDetail= new CommCardDetailsVO();
        CommAddressDetailsVO AddressDetail= new CommAddressDetailsVO();
        CommTransactionDetailsVO TransDetail = new CommTransactionDetailsVO();
        UGSPayRequestVO requestDetail=new UGSPayRequestVO();
        UGSPayResponseVO transRespDetails=new UGSPayResponseVO();
        String transid="11";
        String webid="105982";
        String password="3bf4e07ca2dbe2752fec12d87b7cb0caae25f4365c7b55c208ba47373035f684";
        String orderid="7ddf32e17a6ac5ce04a8ecbf2";
        String ip="127.0.0.1";
        String amount="25";
        String currency="USD";
        String cName="John Doe";
        String cCnumber="4111111111111111";
        String address="406 Parkway House";
        String zip="SW14 8LS";
        String city="London";
        String state="N/A";
        String ccode="UK";
        String phone="NA";
        String emailaddr="john.doe@example.com";
        String cvv="000";
        String cMonth="10";
        String cYear="2014";

        AddressDetail.setCity(city);
        AddressDetail.setCountry(ccode);
        AddressDetail.setEmail(emailaddr);
        AddressDetail.setIp(ip);
        AddressDetail.setPhone(phone);
        AddressDetail.setState(state);
        AddressDetail.setStreet(address);
        AddressDetail.setZipCode(zip);

        cardDetail.setExpMonth(cMonth);
        cardDetail.setExpYear(cYear);
        cardDetail.setCardNum(cCnumber);
        cardDetail.setcVV(cvv);
        cardDetail.setCardHolderName(cName);

        TransDetail.setCurrency(currency);
        TransDetail.setAmount(amount);
        TransDetail.setOrderId(orderid);
        String accountid="141";
        requestDetail=new UGSPayRequestVO();
        requestDetail.setCardDetailsVO(cardDetail);
        requestDetail.setAddressDetailsVO(AddressDetail);
        requestDetail.setTransDetailsVO(TransDetail);
        requestDetail.setUGSTransId(Integer.parseInt(transid));
        requestDetail.setAmount(amount);
        try
        {   pg = AbstractPaymentGateway.getGateway(accountid);

            //System.out.println(AbstractPaymentGateway.getGateway(accountid));
            //transRespDetails = (UGSPayResponseVO) pg.processSale(transid,requestDetail);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError----->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        /*System.out.println(transRespDetails.getTransactionId());
        System.out.println(transRespDetails.getMerchantOrderId());
        System.out.println(transRespDetails.getStatus());
        System.out.println(transRespDetails.getErrorCode());
        System.out.println(transRespDetails.getDescription());
*/

    }
}
