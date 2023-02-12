package com.payment.payvision.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/27/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVisionTestCases
{
    private static Logger logger=new Logger(PayVisionTestCases.class.getName());

    private String memberId_postbank = "142967";
    private String guId_postbank = "D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D";

    private String memberId_borgun = "142969";
    private String guId_borgun = "243825EF-0DDB-4257-ADA0-E94D32E4C8CE";

    private String memberId_privat = "142971";
    private String guId_privat = "A8BA7862-642B-4586-9C8F-185C74831A4F";


    @Test
    public void auth_capture_privat_1_2()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 1 and 2 Auth Capture Privat";
       /* System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "privat";
        int type = 1;
        String amount = "5.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Auth-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();


        try
        {
            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processAuthentication(transactionId, payVisionRequestVO);


            /*System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            payVisionRequestVO.setTrackingMemberCode("Capture - " + transactionId);
            CommTransactionDetailsVO capTransdetails = getTransDetails("5.00");
            capTransdetails.setPreviousTransactionId(payVisionResponseVO.getTransactionId());
            payVisionRequestVO.setTransDetailsVO(capTransdetails);
            payVisionRequestVO.setTransactionGuid(payVisionResponseVO.getTransactionGuid());

            payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processCapture(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);*/
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }

    @Test
    public void auth_void_privat_7_8()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 7&8 Auth and void Privat";
       /* System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "privat";
        int type = 4;
        String amount = "10.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Auth-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();

        try
        {


            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processAuthentication(transactionId, payVisionRequestVO);

            /*System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            payVisionRequestVO.setTrackingMemberCode("Void - " + transactionId);
            CommTransactionDetailsVO canTransdetails = getTransDetails(amount);
            canTransdetails.setPreviousTransactionId(payVisionResponseVO.getTransactionId());
            payVisionRequestVO.setTransDetailsVO(canTransdetails);
            payVisionRequestVO.setTransactionGuid(payVisionResponseVO.getTransactionGuid());

            payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processVoid(transactionId, payVisionRequestVO);

            String returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid :" + payVisionResponseVO.getTransactionGuid());*/
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->", e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }

    @Test
    public void sale_privat_9()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 9 Sale Privat";
        /*System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "privat";
        int type = 2;
        String amount = "20.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Sale-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();

        try
        {

            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processSale(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }


    @Test
    public void sale_privat_10()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 10 Sale Privat";
        /*System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "fail";
        String mid = "privat";
        int type = 1;
        String amount = "120.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Sale-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();

        try
        {

            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processSale(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }


    @Test
    public void sale_refunds_privat_11_12_13_14()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 11 12 13 14 Sale & Refunds Privat";
       /* System.out.println(testDescription);
        System.out.println();*/

        String mid = "privat";
        int type = 2;
        String amount = "15.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Sale-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();

        try
        {

            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processSale(transactionId, payVisionRequestVO);

            String expectedResult = "success";
            String returnResult = payVisionResponseVO.getStatus();
            /*System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);

            payVisionRequestVO.setTrackingMemberCode("Refund - " + transactionId);
            CommTransactionDetailsVO refTransdetails = getTransDetails("10.00");
            String saleTransactionId = payVisionResponseVO.getTransactionId();
            refTransdetails.setPreviousTransactionId(saleTransactionId);
            payVisionRequestVO.setTransDetailsVO(refTransdetails);
            payVisionRequestVO.setTransactionGuid(payVisionResponseVO.getTransactionGuid());
            payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processRefund(transactionId, payVisionRequestVO);
            expectedResult = "success";
            returnResult = payVisionResponseVO.getStatus();
            /*System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);


            payVisionRequestVO.setTrackingMemberCode("Refund - " + transactionId + "-2");
            refTransdetails = getTransDetails("5.00");
            refTransdetails.setPreviousTransactionId(saleTransactionId);
            payVisionRequestVO.setTransDetailsVO(refTransdetails);
            payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processRefund(transactionId, payVisionRequestVO);
            expectedResult = "success";
            returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);


            payVisionRequestVO.setTrackingMemberCode("Refund - " + transactionId + "-3");
            refTransdetails = getTransDetails("2.00");
            refTransdetails.setPreviousTransactionId(saleTransactionId);
            payVisionRequestVO.setTransDetailsVO(refTransdetails);
            payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processRefund(transactionId, payVisionRequestVO);
            expectedResult = "fail";
            returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());
*/
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }


    @Test
    public void sale_privat_15()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 15 Sale Privat";
       /* System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "privat";
        int type = 3;
        String amount = "1.45";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Sale-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();

        try
        {

            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processSale(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
          /*  System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }

    @Test
    public void auth_capture_borgun_1_2()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 1 and 2 Auth Capture Borgun";
       /* System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "borgun";
        int type = 1;
        String amount = "5.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Auth-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();


        try
        {
            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processAuthentication(transactionId, payVisionRequestVO);


            /*System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            payVisionRequestVO.setTrackingMemberCode("Capture - " + transactionId);
            CommTransactionDetailsVO capTransdetails = getTransDetails(amount);
            capTransdetails.setPreviousTransactionId(payVisionResponseVO.getTransactionId());
            payVisionRequestVO.setTransDetailsVO(capTransdetails);
            payVisionRequestVO.setTransactionGuid(payVisionResponseVO.getTransactionGuid());

            payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processCapture(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }


    @Test
    public void sale_borgun_9()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 9 Sale Borgun";
       /* System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "borgun";
        int type = 2;
        String amount = "20.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Sale-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();

        try
        {

            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processSale(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }


    @Test
    public void auth_capture_postbank_1_2()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 1 and 2 Auth Capture postbank";
        /*System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "postbank";
        int type = 1;
        String amount = "5.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Auth-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();


        try
        {
            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processAuthentication(transactionId, payVisionRequestVO);


           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            payVisionRequestVO.setTrackingMemberCode("Capture - " + transactionId);
            CommTransactionDetailsVO capTransdetails = getTransDetails(amount);
            capTransdetails.setPreviousTransactionId(payVisionResponseVO.getTransactionId());
            payVisionRequestVO.setTransDetailsVO(capTransdetails);
            payVisionRequestVO.setTransactionGuid(payVisionResponseVO.getTransactionGuid());

            payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processCapture(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
           /* System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/
            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }

    @Test
    public void sale_postbank_9()
    {
        PayVisionRequestVO payVisionRequestVO = new PayVisionRequestVO();

        String testDescription = "Test Case 9 Sale Postbank";
        /*System.out.println(testDescription);
        System.out.println();*/
        String expectedResult = "success";
        String mid = "postbank";
        int type = 2;
        String amount = "20.00";
        String transactionId = RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 5);
        payVisionRequestVO.setTrackingMemberCode("Sale-" + transactionId);
        payVisionRequestVO.setAddressDetailsVO(getAddress(type));
        payVisionRequestVO.setCardDetailsVO(getCardDetailsVo(type));
        payVisionRequestVO.setCommMerchantVO(getMerchant(mid));
        payVisionRequestVO.setTransDetailsVO(getTransDetails(amount));


        PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway();

        try
        {

            PayVisionResponseVO payVisionResponseVO = (PayVisionResponseVO) payVisionPaymentGateway.processSale(transactionId, payVisionRequestVO);
            String returnResult = payVisionResponseVO.getStatus();
            /*System.out.println();
            System.out.println("Return Code : " + payVisionResponseVO.getErrorCode());
            System.out.println("Return Status : " + payVisionResponseVO.getStatus());
            System.out.println("Return Transaction Id : " + payVisionResponseVO.getTransactionId());
            System.out.println("Return Tracking Member Code : " + payVisionResponseVO.getTrackingMemberCode());
            System.out.println("Return Transaction Guid : " + payVisionResponseVO.getTransactionGuid());*/

            assertEquals("Return Result does not match expected Result", expectedResult, returnResult);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException--->",e);  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }


    public CommMerchantVO getMerchant(String name)
    {
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        if ("privat".equals(name))
        {
            commMerchantVO.setMerchantUsername(guId_privat);
            commMerchantVO.setMerchantId(memberId_privat);
        }
        else if ("borgun".equalsIgnoreCase(name))
        {
            commMerchantVO.setMerchantUsername(guId_borgun);
            commMerchantVO.setMerchantId(memberId_borgun);
        }
        else
        {
            commMerchantVO.setMerchantUsername(guId_postbank);
            commMerchantVO.setMerchantId(memberId_postbank);
        }
        return commMerchantVO;

    }


    public CommTransactionDetailsVO getTransDetails(String amount)
    {
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        commTransactionDetailsVO.setAmount(amount);
        commTransactionDetailsVO.setCurrency("USD");
        return commTransactionDetailsVO;


    }


    public CommCardDetailsVO getCardDetailsVo(int type)
    {
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

        switch (type)
        {
            case 1:
                commCardDetailsVO.setCardNum("");
                commCardDetailsVO.setcVV("");
                break;
            case 2:
                commCardDetailsVO.setCardNum("");
                commCardDetailsVO.setcVV("");
                break;
            case 3:
                commCardDetailsVO.setCardNum("");
                commCardDetailsVO.setcVV("");
                break;
            case 4:
                commCardDetailsVO.setCardNum("");
                commCardDetailsVO.setcVV("");
                break;


        }
        commCardDetailsVO.setExpMonth("12");
        commCardDetailsVO.setExpYear("2013");
        return commCardDetailsVO;

    }

    public CommAddressDetailsVO getAddress(int type)
    {
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

        switch (type)
        {

            case 1:
                commAddressDetailsVO.setFirstname("John");
                commAddressDetailsVO.setLastname("Smith");
                break;
            case 2:
                commAddressDetailsVO.setFirstname("John");
                commAddressDetailsVO.setLastname("Doe");
                break;
            case 3:
                commAddressDetailsVO.setFirstname("Mike");
                commAddressDetailsVO.setLastname("Dough");
                break;
            case 4:
                commAddressDetailsVO.setFirstname("Jane");
                commAddressDetailsVO.setLastname("Doe");
                break;

        }
        commAddressDetailsVO.setCountry("IN");
        commAddressDetailsVO.setStreet("Link Road");
        commAddressDetailsVO.setZipCode("400064");
        return commAddressDetailsVO;
    }


}
