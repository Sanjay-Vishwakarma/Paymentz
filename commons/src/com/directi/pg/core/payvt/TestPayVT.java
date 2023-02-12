package com.directi.pg.core.payvt;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;

import java.net.InetAddress;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Aug 24, 2012
 * Time: 8:53:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPayVT
{

    static Logger logger = new Logger(TestPayVT.class.getName());
    //final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Servlet");
    //final static ResourceBundle RB1 = LoadProperties.getProperty("com.directi.pg.ICICI");

    public static void main(String[] aa)
    {


        //System.out.println("Calling auth ====");
        processAuthenticationWithCapture();

    }


    public static void processAuthenticationWithCapture()
    {
        AbstractPaymentGateway pg = null;
        String accountid ="3";

        GenericCardDetailsVO cardDetailVO= new GenericCardDetailsVO();
        GenericAddressDetailsVO addressDetailVO= new GenericAddressDetailsVO();
        GenericTransDetailsVO transDetailVO = new GenericTransDetailsVO(null,null,null,null,null);
        PayVTRequestVO requestDetail=null;
        PayVTResponseVO  transRespDetails=null;
        try
        {
            logger.debug("Getting Abstarct gateway===  account Id="+accountid);
            pg = AbstractPaymentGateway.getGateway(accountid);

            // if(PayVTPaymentGateway.class.isIs)
        }
        catch (SystemError systemError)
        {   logger.error("System Error:::::::::"+systemError);


        }



        InetAddress ownIP = null;
        try{
            ownIP=InetAddress.getLocalHost();
            logger.debug("IP of my system is := "+ownIP.getHostAddress());
        }catch (Exception e){
            logger.error("Exception caught ="+e.getMessage());
        }



        String  transAmount = "6.00";
        String  trackingID = "Testing.1001";
        String  currency = pg.getCurrency();
        String  ipaddress = ownIP.getHostAddress();
        String  orderDes = "Test Item";


        String  ccnum = "4111111111111111";
        String  ccid = "123";
        String  year = "2015";
        String  month = "04";
        String  name = "Test Card";

        String  firstname = "Test";
        String  lastname = "User";
        String  address = "123 Test St";
        String  city = "Testville";
        String  state = "TT";
        String  ccode = "US";
        String  zip = "1234";
        String  phone = "1234567890";
        String  email = "test@test.com";





        try
        {  cardDetailVO.setCardHolderName(name);
            cardDetailVO.setCardNum(ccnum);
            cardDetailVO.setcVV(ccid);
            cardDetailVO.setExpMonth(month);
            cardDetailVO.setExpYear(year);

            addressDetailVO.setFirstname(firstname);
            addressDetailVO.setLastname(lastname);

            addressDetailVO.setCity(city);
            addressDetailVO.setCountry(ccode);

            addressDetailVO.setState(state);
            addressDetailVO.setZipCode(zip);
            addressDetailVO.setStreet(address);
            addressDetailVO.setPhone(phone);
            addressDetailVO.setEmail(email);
            addressDetailVO.setIp(ipaddress);
            transDetailVO.setAmount(transAmount);
            transDetailVO.setCurrency(currency);
            transDetailVO.setOrderId(trackingID);
            transDetailVO.setOrderDesc(orderDes);


            requestDetail = new PayVTRequestVO(cardDetailVO,addressDetailVO, transDetailVO);
            transRespDetails = (PayVTResponseVO) pg.processSale(trackingID, requestDetail);

            logger.debug(transRespDetails);

        }
        catch(Exception e)
        {
            logger.error("Excpetion Thrown "+e.getMessage());
        }





    }


}
