package com.payment.websecpay.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 8/3/13
 * Time: 3:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class WSecTestCases
{
    private static Logger logger=new Logger(WSecTestCases.class.getName());

    private String testAccount = "9360110";
//    private String testPassword = "Qzcewxdas#21";
//    private String testPassword = "qeczwxdas#21";
    private String testPassword = "d6855f6b4314be460c3c411fe5af7726";
    private String testKey = "test";
    private String testUrl = "https://www.pagamentsegur.com/prod/access.php";
//    private String testUrl = "https://www.pagamentsegur.com/prod/access.php";


    @Test
    public void testSale_visa()
    {

        try
        {
            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(testAccount);
            merchantAccountVO.setPassword(testPassword);

            WSecRequestVO wSecRequestVO = new WSecRequestVO();

            wSecRequestVO.setSiteUrl(testUrl);
            wSecRequestVO.setKey(testKey);

            String type = "VISA";
            CommCardDetailsVO cardDetailsVO = getWSecTestCardDetails(type);


            CommAddressDetailsVO addressDetailsVO = getAddressDetails(type);


            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount("10");
            transDetailsVO.setCurrency("USD");
            transDetailsVO.setOrderId("123456");
            transDetailsVO.setOrderDesc("TestOrder");

            wSecRequestVO.setAddressDetailsVO(addressDetailsVO);
            wSecRequestVO.setCardDetailsVO(cardDetailsVO);
            wSecRequestVO.setCommMerchantVO(merchantAccountVO);
            wSecRequestVO.setTransDetailsVO(transDetailsVO);

            WSecResponseVO responseVO = (WSecResponseVO) new WSecPaymentGateway().processSale("123456", wSecRequestVO);
            /*System.out.println(responseVO.getStatus());
            System.out.println(responseVO.getErrorCode());
            System.out.println(responseVO.getDescription());*/

        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);
            fail();
        }


    }


    private CommCardDetailsVO getWSecTestCardDetails(String type)
    {
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
//        CVC = 500:Insufficient funds.
//        ·CVC = 501:Authorization declined.
//        ·CVC = 502:Invalid transaction.
//        ·CVC = 503:Retain card -specialconditions.
//        ·CVC = 504:Payment refused by acquirer bank.
//        ·CVC = 505:Invalid merchant number.
//        ·CVC = 600:Stolen card.
//        ·CVC = 601:Card expired.
//        ·CVC = 602:Not in authorized db.
//        ·CVC = 603:Suspicion of  fraud.
//        ·CVC = 604:Pin entered  incorrectly too   often.
//        ·CVC = 605:Retain card.
//        ·CVC = 606:Lost card.
//        ·CVC = 607:Stolen card.
//        ·CVC = 700:Communication failure.
//        ·CVC = 701:A technical problem occurred, please contact helpdesk.

        if ("MASTER".equalsIgnoreCase(type))
        {
            cardDetailsVO.setCardHolderFirstName("Michael");
            cardDetailsVO.setCardHolderSurname("Zeppelin");
            cardDetailsVO.setCardNum("5431111111111111");
            cardDetailsVO.setCardType("MASTER");
            cardDetailsVO.setcVV("123");
            cardDetailsVO.setExpMonth("04");
            cardDetailsVO.setExpYear("2014");

        }

        else if ("AMEX".equalsIgnoreCase(type))
        {
            cardDetailsVO.setCardHolderFirstName("Brigitte");
            cardDetailsVO.setCardHolderSurname("Flow");
            cardDetailsVO.setCardNum("341111111111111");
            cardDetailsVO.setCardType("AMEX");
            cardDetailsVO.setcVV("000");
            cardDetailsVO.setExpMonth("04");
            cardDetailsVO.setExpYear("2014");

        }
        else if ("JCB".equalsIgnoreCase(type))
        {
            cardDetailsVO.setCardHolderFirstName("Brigitte");
            cardDetailsVO.setCardHolderSurname("Flow");
            cardDetailsVO.setCardNum("3088000000000017");
            cardDetailsVO.setCardType("AMEX");
            cardDetailsVO.setcVV("000");
            cardDetailsVO.setExpMonth("04");
            cardDetailsVO.setExpYear("2014");

        }
        else
        {

            cardDetailsVO.setCardHolderFirstName("Amoolya");
            cardDetailsVO.setCardHolderSurname("Vassa");
            cardDetailsVO.setCardNum("4546913000064979");
            cardDetailsVO.setCardType("VISA");
            cardDetailsVO.setcVV("851");
            cardDetailsVO.setExpMonth("08");
            cardDetailsVO.setExpYear("2017");

        }
        return cardDetailsVO;

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


            addressDetailsVO.setFirstname("Amoolya");
            addressDetailsVO.setLastname("Vassa");
            addressDetailsVO.setCity("Mumbai");
            addressDetailsVO.setCountry("IN");
            addressDetailsVO.setIp("113.193.41.128");
            addressDetailsVO.setPhone("9876543210");
            addressDetailsVO.setEmail("jignesh.r@pz.com");
            addressDetailsVO.setState("Maharashtra");
            addressDetailsVO.setStreet("Malad");
            addressDetailsVO.setZipCode("400067");
        }

        return addressDetailsVO;

    }


}
