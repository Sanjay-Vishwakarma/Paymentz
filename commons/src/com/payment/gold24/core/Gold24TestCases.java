package com.payment.gold24.core;

import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 11/3/12
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gold24TestCases
{
    private String username = "a-UTCC255";
    private String password_interface = "DSFGGdgfgh234";

//    private String terminal = "98754681";
//    private String password = "8ds7fds8fdsf7sd87fsfSDFSDFsdf4";

    private String terminal                 = "623545098";
    private String password                 = "f9nruYDvKfud9zTFIhjFq56Rr59gsHct";
    static TransactionLogger transactionLogger    = new TransactionLogger(Gold24TestCases.class.getName());


    @Test
    public void testSale_visa()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);

            String type = "VISA";
            CommCardDetailsVO cardDetailsVO = getFGol24TestCardDetails(type);


            CommAddressDetailsVO addressDetailsVO = getAddressDetails(type);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("10");
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();
            gold24RequestVO.setAddressDetailsVO(addressDetailsVO);
            gold24RequestVO.setCardDetailsVO(cardDetailsVO);
            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);

            CommResponseVO responseVO =null;
            //responseVO = (CommResponseVO) new Gold24PaymentGateway().processSale("123456", gold24RequestVO);
            /*System.out.println(((CommResponseVO)responseVO).getStatus());
            System.out.println(((CommResponseVO)responseVO).getErrorCode());

            System.out.println(((CommResponseVO)responseVO).getDescriptor());*/

        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception ::::::::",systemError);
            fail();
        }


    }


    @Test
    public void testSale_master()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);

            String type = "MASTER";
            CommCardDetailsVO cardDetailsVO = getFGol24TestCardDetails(type);


            CommAddressDetailsVO addressDetailsVO = getAddressDetails(type);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("100"); //Amount * 100 according to the docs
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();
            gold24RequestVO.setAddressDetailsVO(addressDetailsVO);
            gold24RequestVO.setCardDetailsVO(cardDetailsVO);
            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);
            CommResponseVO responseVO = null;
            //responseVO = (CommResponseVO)new Gold24PaymentGateway().processSale("123456", gold24RequestVO);
        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception :::::: ",systemError);
            fail();
        }


    }


    @Test
    public void testSale_master_enrolled()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);

            String type = "MASTER_ENROLLED";
            CommCardDetailsVO cardDetailsVO = getFGol24TestCardDetails(type);


            CommAddressDetailsVO addressDetailsVO = getAddressDetails(type);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("100"); //Amount * 100 according to the docs
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();
            gold24RequestVO.setAddressDetailsVO(addressDetailsVO);
            gold24RequestVO.setCardDetailsVO(cardDetailsVO);
            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);
            CommResponseVO responseVO =null;
            //responseVO = (CommResponseVO)new Gold24PaymentGateway().processSale("123456", gold24RequestVO);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::::::::",e);
            fail();
        }


    }

    @Test
    public void testSale_amex_enrolled()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);

            String type = "AMEX_ENROLLED";
            CommCardDetailsVO cardDetailsVO = getFGol24TestCardDetails(type);


            CommAddressDetailsVO addressDetailsVO = getAddressDetails(type);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("100"); //Amount * 100 according to the docs
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();
            gold24RequestVO.setAddressDetailsVO(addressDetailsVO);
            gold24RequestVO.setCardDetailsVO(cardDetailsVO);
            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);
            CommResponseVO responseVO = null;
            //responseVO = (CommResponseVO)new Gold24PaymentGateway().processSale("123456", gold24RequestVO);
        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception :::::::::",systemError);
            fail();
        }


    }


    @Test
    public void testAuthorization()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);

            String type = "VISA";
            CommCardDetailsVO cardDetailsVO = getFGol24TestCardDetails(type);


            CommAddressDetailsVO addressDetailsVO = getAddressDetails(type);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("1000"); //Amount * 100 according to the docs
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();
            gold24RequestVO.setAddressDetailsVO(addressDetailsVO);
            gold24RequestVO.setCardDetailsVO(cardDetailsVO);
            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);

            CommResponseVO responseVO = (CommResponseVO) new Gold24PaymentGateway().processAuthentication("123456", gold24RequestVO);

        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException :::::::::", e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }


    }

    @Test
    public void testCapture()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("1000"); //Amount * 100 according to the docs
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();
            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);

            CommResponseVO responseVO = (CommResponseVO) new Gold24PaymentGateway().processCapture("123456", gold24RequestVO);
        }

        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException :::::::::", e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }


    }

    @Test
    public void testRefund()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("1000"); //Amount * 100 according to the docs
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();

            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);

            CommResponseVO responseVO = (CommResponseVO) new Gold24PaymentGateway().processRefund("123456", gold24RequestVO);

        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception :::::::::", systemError);
            fail();
        }


    }


    @Test
    public void testVoid()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(terminal);
            merchantAccountVO.setPassword(password);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("1000"); //Amount * 100 according to the docs
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            CommRequestVO gold24RequestVO = new CommRequestVO();
            gold24RequestVO.setCommMerchantVO(merchantAccountVO);
            gold24RequestVO.setTransDetailsVO(transDetailsVO);

            CommResponseVO responseVO = (CommResponseVO)new Gold24PaymentGateway().processVoid("123456", gold24RequestVO);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException :::::::::", e); //To change body of catch statement use File | Settings | File Templates.
            fail();
        }


    }


    private CommAddressDetailsVO getAddressDetails()
    {
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        addressDetailsVO.setFirstname("Andy");
        addressDetailsVO.setLastname("Jagger");
        addressDetailsVO.setCity("London");
        addressDetailsVO.setCountry("UK");
        addressDetailsVO.setIp("2.2.2.2");
        addressDetailsVO.setPhone("9876543210");
        addressDetailsVO.setEmail("test@tester.com");
        addressDetailsVO.setState("London");
        addressDetailsVO.setStreet("Broadway 13");
        addressDetailsVO.setZipCode("BLM-129");

        return addressDetailsVO;

    }

    private CommAddressDetailsVO getAddressDetails(String type)
    {
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();


        if ("MASTER".equalsIgnoreCase(type))
        {
            addressDetailsVO.setFirstname("Michael");
            addressDetailsVO.setLastname("Zeppelin");
            addressDetailsVO.setCity("London");
            addressDetailsVO.setCountry("UK");
            addressDetailsVO.setIp("2.2.2.2");
            addressDetailsVO.setPhone("9876543210");
            addressDetailsVO.setEmail("test@tester.com");
            addressDetailsVO.setState("Falasha");
            addressDetailsVO.setStreet("Darkstategate 19");
            addressDetailsVO.setZipCode("DKT-123");

        }
        else if ("MASTER_ENROLLED".equalsIgnoreCase(type))
        {
            addressDetailsVO.setFirstname("Michael");
            addressDetailsVO.setLastname("Zeppelin");
            addressDetailsVO.setCity("London");
            addressDetailsVO.setCountry("UK");
            addressDetailsVO.setIp("2.2.2.2");
            addressDetailsVO.setPhone("9876543210");
            addressDetailsVO.setEmail("test@tester.com");
            addressDetailsVO.setState("Falasha");
            addressDetailsVO.setStreet("Darkstategate 19");
            addressDetailsVO.setZipCode("DKT-123");

        }
        else if ("AMEX_ENROLLED".equalsIgnoreCase(type))
        {
            addressDetailsVO.setFirstname("Brigitte ");
            addressDetailsVO.setLastname("Flow");
            addressDetailsVO.setCity("London");
            addressDetailsVO.setCountry("UK");
            addressDetailsVO.setIp("2.2.2.2");
            addressDetailsVO.setPhone("9876543210");
            addressDetailsVO.setEmail("test@tester.com");
            addressDetailsVO.setState("Dakota");
            addressDetailsVO.setStreet("Darkminister 19");
            addressDetailsVO.setZipCode("DRK-123");

        }
        else
        {


            addressDetailsVO.setFirstname("1234Jean");
            addressDetailsVO.setLastname("F133onda");
            addressDetailsVO.setCity("London");
            addressDetailsVO.setCountry("GB");
            addressDetailsVO.setIp("2.2.2.2");
            addressDetailsVO.setPhone("9876543210");
            addressDetailsVO.setEmail("test@tester.com");
            addressDetailsVO.setState("Bolmberg");
            addressDetailsVO.setStreet("Hollandsgade 12");
            addressDetailsVO.setZipCode("BLM-123");
        }

        return addressDetailsVO;

    }

//    private CommCardDetailsVO getFGol24TestCardDetails(String type)
//    {
//        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
//        cardDetailsVO.setcVV("111");
//        cardDetailsVO.setExpMonth("10");
//        cardDetailsVO.setExpYear("2016");
//
//        if ("VISA_ENROLLED".equalsIgnoreCase(type))
//        {
//            cardDetailsVO.setCardHolderFirstName("Andy");
//            cardDetailsVO.setCardHolderSurname("Jagger");
//            cardDetailsVO.setCardNum("4111111111111111");
//            cardDetailsVO.setCardType("VISA");
//        }
//
//        else if ("MASTER".equalsIgnoreCase(type))
//        {
//            cardDetailsVO.setCardHolderFirstName("Nikolas");
//            cardDetailsVO.setCardHolderSurname("Tesla");
//            cardDetailsVO.setCardNum("545454545454545n");
//            cardDetailsVO.setCardType("MASTER");
//        }
//        else if ("MASTER_ENROLLED".equalsIgnoreCase(type))
//        {
//
//            cardDetailsVO.setCardHolderFirstName("Leonard");
//            cardDetailsVO.setCardHolderSurname("Cohens");
//            cardDetailsVO.setCardNum("5121212121212124");
//            cardDetailsVO.setCardType("MASTER");
//
//        }
//        else if ("AMEX".equalsIgnoreCase(type))
//        {
//            cardDetailsVO.setCardHolderFirstName("Paula");
//            cardDetailsVO.setCardHolderSurname("Dean");
//            cardDetailsVO.setCardNum("311111111111117");
//            cardDetailsVO.setCardType("AMEX");
//
//        }
//        else
//        {
//
//            cardDetailsVO.setCardHolderFirstName("George");
//            cardDetailsVO.setCardHolderSurname("Washington");
//            cardDetailsVO.setCardNum("4444333322221111");
//            cardDetailsVO.setCardType("VISA");
//
//        }
//        return cardDetailsVO;
//
//    }


    private CommCardDetailsVO getFGol24TestCardDetails(String type)
    {
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();


        if ("MASTER".equalsIgnoreCase(type))
        {
            cardDetailsVO.setCardHolderFirstName("Michael");
            cardDetailsVO.setCardHolderSurname("Zeppelin");
            cardDetailsVO.setCardNum("5112112112112113");
            cardDetailsVO.setCardType("MASTER");
            cardDetailsVO.setcVV("123");
            cardDetailsVO.setExpMonth("04");
            cardDetailsVO.setExpYear("2014");

        }
        else if ("MASTER_ENROLLED".equalsIgnoreCase(type))
        {

            cardDetailsVO.setCardHolderFirstName("Michael");
            cardDetailsVO.setCardHolderSurname("Zeppelin");
            cardDetailsVO.setCardNum("5111111111111118");
            cardDetailsVO.setCardType("MASTER");
            cardDetailsVO.setcVV("118");
            cardDetailsVO.setExpMonth("04");
            cardDetailsVO.setExpYear("2014");

        }
        else if ("AMEX_ENROLLED".equalsIgnoreCase(type))
        {
            cardDetailsVO.setCardHolderFirstName("Brigitte");
            cardDetailsVO.setCardHolderSurname("Flow");
            cardDetailsVO.setCardNum("3411111111111118");
            cardDetailsVO.setCardType("AMEX");
            cardDetailsVO.setcVV("000");
            cardDetailsVO.setExpMonth("04");
            cardDetailsVO.setExpYear("2014");

        }
        else
        {

            cardDetailsVO.setCardHolderFirstName("Jean");
            cardDetailsVO.setCardHolderSurname("Fonda");
            cardDetailsVO.setCardNum("4515151515151515");
            cardDetailsVO.setCardType("VISA");
            cardDetailsVO.setcVV("515");
            cardDetailsVO.setExpMonth("04");
            cardDetailsVO.setExpYear("2014");

        }
        return cardDetailsVO;

    }

    public static void main(String[] args) throws Exception
    {


    }


}
