package com.payment.payon.core;


import com.payment.common.core.CommAddressDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/15/12
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */

public class PayOnTest
{


    private static final String username = "68176b4415224fc1af4962fe794cc84f";
    private static final String password = "yoUEPlMfyoEh";
    private static final String url = "https://test.ppipe.net/connectors/gateway";
    private static final String transactionMode = "TEST";

    private PayOnGateway payOnGateway;

    @Before
    public void setUp()
    {

            payOnGateway = new PayOnGateway("1");


    }


    private PayOnCardDetailsVO getTestCardDetails(String type)
    {
        PayOnCardDetailsVO payOnCardDetailsVO = new PayOnCardDetailsVO();


        payOnCardDetailsVO.setCardHolderName("Jigy Dude");
        if ("VISA".equalsIgnoreCase(type))
        {
            payOnCardDetailsVO.setCardNum("4012888888881881");
            payOnCardDetailsVO.setCardType("VISA");
            payOnCardDetailsVO.setcVV("456");
            payOnCardDetailsVO.setExpMonth("01");
            payOnCardDetailsVO.setExpYear("2015");
        }
        else if ("MASTER".equalsIgnoreCase(type))
        {
            payOnCardDetailsVO.setCardNum("5105105105105100");
            payOnCardDetailsVO.setCardType("MASTER");
            payOnCardDetailsVO.setcVV("789");
            payOnCardDetailsVO.setExpMonth("01");
            payOnCardDetailsVO.setExpYear("2016");

        }
        else if ("VISA_ENROLLED".equalsIgnoreCase(type))
        {
            payOnCardDetailsVO.setCardNum("4711100000000000");
            payOnCardDetailsVO.setCardType("VISA");
            payOnCardDetailsVO.setcVV("456");
            payOnCardDetailsVO.setExpMonth("02");
            payOnCardDetailsVO.setExpYear("2015");
        }
        else if ("VISA_FAILED_AUTHORIZATION".equalsIgnoreCase(type))
        {
            payOnCardDetailsVO.setCardNum("4012001037461114");
            payOnCardDetailsVO.setCardType("VISA");
            payOnCardDetailsVO.setcVV("456");
            payOnCardDetailsVO.setExpMonth("03");
            payOnCardDetailsVO.setExpYear("2015");
        }
        else if ("MASTER_ENROLLED".equalsIgnoreCase(type))
        {
            payOnCardDetailsVO.setCardNum("5453010000059543");
            payOnCardDetailsVO.setCardType("MASTER");
            payOnCardDetailsVO.setcVV("789");
            payOnCardDetailsVO.setExpMonth("02");
            payOnCardDetailsVO.setExpYear("2016");
        }
        else
        {
            payOnCardDetailsVO.setCardNum("4111111111111111");
            payOnCardDetailsVO.setCardType("VISa");
            payOnCardDetailsVO.setcVV("123");
            payOnCardDetailsVO.setExpMonth("01");
            payOnCardDetailsVO.setExpYear("2018");
        }


        return payOnCardDetailsVO;
    }

    private PayOnMerchantAccountVO getTestMerchantDetails()
    {
        PayOnMerchantAccountVO payOnMerchantAccountVO = new PayOnMerchantAccountVO();
        payOnMerchantAccountVO.setType("CERTIFICATION");
        payOnMerchantAccountVO.setMerchantId("1234567890");
        payOnMerchantAccountVO.setAliasName("Jigy Dude");
        payOnMerchantAccountVO.setPassword("mypassword123");
        payOnMerchantAccountVO.setCountry("DE");
        return payOnMerchantAccountVO;
    }

    private PayOnTransactionDetailsVO getTestTransactionDetails(String amount, String currency, String description)
    {
        PayOnTransactionDetailsVO payOnTransactionDetailsVO = new PayOnTransactionDetailsVO();
        payOnTransactionDetailsVO.setAmount(amount);
        payOnTransactionDetailsVO.setCurrency(currency);
        payOnTransactionDetailsVO.setOrderDesc(description);
        return payOnTransactionDetailsVO;
    }

    private CommAddressDetailsVO getTestAddressDetails()
    {
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        commAddressDetailsVO.setFirstname("Jigy");
        commAddressDetailsVO.setLastname("Tester");
        commAddressDetailsVO.setCity("Mumbai");
        commAddressDetailsVO.setStreet("Link Street");
        commAddressDetailsVO.setState("MH");
        commAddressDetailsVO.setZipCode("400062");
        commAddressDetailsVO.setCountry("IN");
        commAddressDetailsVO.setEmail("test@tester.com");
        commAddressDetailsVO.setIp("2.2.2.2");
        return commAddressDetailsVO;
    }


    @Test
    public void testCaseA1_VISA()
    {
        String testCaseDescription = "Test Case A.1 for VISA";
        String expectedReturnCode = "000.000.000";
        try
        {


           /* System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("10.00", "EUR", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);

//            System.out.println("Debit Response Code : " + payOnDebitResponseVO.getReturnCode());
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            PayOnRequestVO payOnReversalRequestVO = new PayOnRequestVO();
            payOnReversalRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());

            PayOnTransactionDetailsVO reversalTransaction = getTestTransactionDetails("10.00", "EUR", testCaseDescription);
            reversalTransaction.setReferenceTransactionType(PayOnGateway.DEBIT);
            reversalTransaction.setConnectorTxID1(payOnDebitResponseVO.getConnectorTxID1());
            reversalTransaction.setConnectorTxID2(payOnDebitResponseVO.getConnectorTxID2());
            reversalTransaction.setConnectorTxID3(payOnDebitResponseVO.getConnectorTxID3());

            payOnReversalRequestVO.setPayOnTransactionDetailsVO(reversalTransaction);
            payOnReversalRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            PayOnResponseVO payOnReversalResponseVO = (PayOnResponseVO) payOnGateway.processVoid("123456", payOnReversalRequestVO);

            String returnCode = payOnReversalResponseVO.getReturnCode();
//            System.out.println("Reversal Response Code : " + returnCode);
//            System.out.println("Reversal Response Message : " + payOnReversalResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }

    @Test
    public void testCaseA1_MASTER()
    {

        String testCaseDescription = "Test Case A.1 for MASTER";
        String expectedReturnCode = "000.000.000";
        try
        {
            /*System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("10.00", "EUR", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("MASTER"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);

//            System.out.println("Debit Response Code : " + payOnDebitResponseVO.getReturnCode());
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            PayOnRequestVO payOnReversalRequestVO = new PayOnRequestVO();
            payOnReversalRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());

            PayOnTransactionDetailsVO reversalTransaction = getTestTransactionDetails("10.00", "EUR", testCaseDescription);
            reversalTransaction.setReferenceTransactionType(PayOnGateway.DEBIT);
            reversalTransaction.setConnectorTxID1(payOnDebitResponseVO.getConnectorTxID1());
            reversalTransaction.setConnectorTxID2(payOnDebitResponseVO.getConnectorTxID2());
            reversalTransaction.setConnectorTxID3(payOnDebitResponseVO.getConnectorTxID3());

            payOnReversalRequestVO.setPayOnTransactionDetailsVO(reversalTransaction);
            payOnReversalRequestVO.setPayOnCardDetailsVO(getTestCardDetails("MASTER"));
            PayOnResponseVO payOnReversalResponseVO = (PayOnResponseVO) payOnGateway.processVoid("123456", payOnReversalRequestVO);

            String returnCode = payOnReversalResponseVO.getReturnCode();
//            System.out.println("Reversal Response Code : " + returnCode);
//            System.out.println("Reversal Response Message : " + payOnReversalResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }

    @Test
    public void testCaseA2()
    {

        String testCaseDescription = "Test Case A.2 for VISA";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnPreAuthRequestVO = new PayOnRequestVO();

            PayOnCardDetailsVO visa = getTestCardDetails("VISA");
            PayOnMerchantAccountVO testMerchantDetails = getTestMerchantDetails();

            payOnPreAuthRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnPreAuthRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("19.90", "EUR", testCaseDescription));

            payOnPreAuthRequestVO.setPayOnCardDetailsVO(visa);
            payOnPreAuthRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnPreAuthResponseVO = (PayOnResponseVO) payOnGateway.processAuthentication("123456", payOnPreAuthRequestVO);

//            System.out.println("Pre Auth Response Code : " + payOnPreAuthResponseVO.getReturnCode());
//            System.out.println("Pre Auth Response Message : " + payOnPreAuthResponseVO.getReturnMesaage());

            PayOnRequestVO payOnCaptureRequestVO = new PayOnRequestVO();
            payOnCaptureRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnCaptureRequestVO.setPayOnCardDetailsVO(visa);

            PayOnTransactionDetailsVO captureTransaction = getTestTransactionDetails("14.90", "EUR", testCaseDescription);
            captureTransaction.setReferenceTransactionType(PayOnGateway.PRE_AUTH);
            captureTransaction.setConnectorTxID1(payOnPreAuthResponseVO.getConnectorTxID1());
            captureTransaction.setConnectorTxID2(payOnPreAuthResponseVO.getConnectorTxID2());
            captureTransaction.setConnectorTxID3(payOnPreAuthResponseVO.getConnectorTxID3());

            payOnCaptureRequestVO.setPayOnTransactionDetailsVO(captureTransaction);
            PayOnResponseVO payOnCaptureResponseVO = (PayOnResponseVO) payOnGateway.processCapture("123456", payOnCaptureRequestVO);

//            System.out.println("Capture Response Code : " + payOnCaptureResponseVO.getReturnCode());
//            System.out.println("Capture Response Message : " + payOnCaptureResponseVO.getReturnMesaage());


            PayOnRequestVO payOnRefundRequestVO = new PayOnRequestVO();
            payOnRefundRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnRefundRequestVO.setPayOnCardDetailsVO(visa);

            PayOnTransactionDetailsVO refundTransaction = getTestTransactionDetails("4.90", "EUR", testCaseDescription);
            refundTransaction.setReferenceTransactionType(PayOnGateway.CAPTURE);
            refundTransaction.setConnectorTxID1(payOnCaptureResponseVO.getConnectorTxID1());
            refundTransaction.setConnectorTxID2(payOnCaptureResponseVO.getConnectorTxID2());
            refundTransaction.setConnectorTxID3(payOnCaptureResponseVO.getConnectorTxID3());

            payOnRefundRequestVO.setPayOnTransactionDetailsVO(refundTransaction);
            PayOnResponseVO payOnRefundResponseVO = (PayOnResponseVO) payOnGateway.processRefund("123456", payOnRefundRequestVO);

            String returnCode = payOnRefundResponseVO.getReturnCode();
//            System.out.println("Refund Response Code : " + returnCode);
//            System.out.println("Refund Response Message : " + payOnRefundResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }


    @Test
    public void testCaseA3()
    {

        String testCaseDescription = "Test Case A.3 for VISA";
        String expectedReturnCode = "800.100.170";
        try
        {
           /* System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnPreAuthRequestVO = new PayOnRequestVO();

            PayOnCardDetailsVO visa = getTestCardDetails("VISA");
            PayOnMerchantAccountVO testMerchantDetails = getTestMerchantDetails();

            payOnPreAuthRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnPreAuthRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("24.90", "EUR", testCaseDescription));

            payOnPreAuthRequestVO.setPayOnCardDetailsVO(visa);
            payOnPreAuthRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnPreAuthResponseVO = (PayOnResponseVO) payOnGateway.processAuthentication("123456", payOnPreAuthRequestVO);

//            System.out.println("Pre Auth Response Code : " + payOnPreAuthResponseVO.getReturnCode());
//            System.out.println("Pre Auth Response Message : " + payOnPreAuthResponseVO.getReturnMesaage());


            PayOnRequestVO payOnRefundRequestVO = new PayOnRequestVO();
            payOnRefundRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnRefundRequestVO.setPayOnCardDetailsVO(visa);

            PayOnTransactionDetailsVO refundTransaction = getTestTransactionDetails("17.90", "EUR", testCaseDescription);
            refundTransaction.setReferenceTransactionType(PayOnGateway.PRE_AUTH);
            refundTransaction.setConnectorTxID1(payOnPreAuthResponseVO.getConnectorTxID1());
            refundTransaction.setConnectorTxID2(payOnPreAuthResponseVO.getConnectorTxID2());
            refundTransaction.setConnectorTxID3(payOnPreAuthResponseVO.getConnectorTxID3());

            payOnRefundRequestVO.setPayOnTransactionDetailsVO(refundTransaction);
            PayOnResponseVO payOnRefundResponseVO = (PayOnResponseVO) payOnGateway.processRefund("123456", payOnRefundRequestVO);

            String returnCode = payOnRefundResponseVO.getReturnCode();
//            System.out.println("Refund Response Code : " + returnCode);
//            System.out.println("Refund Response Message : " + payOnRefundResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }


    @Test
    public void testCaseA4()
    {

        String testCaseDescription = "Test Case A.4 for VISA";
        String expectedReturnCode = "700.100.300";
        try
        {
            /*System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnPreAuthRequestVO = new PayOnRequestVO();

            PayOnCardDetailsVO visa = getTestCardDetails("VISA");
            PayOnMerchantAccountVO testMerchantDetails = getTestMerchantDetails();

            payOnPreAuthRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnPreAuthRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("19.90", "EUR", testCaseDescription));

            payOnPreAuthRequestVO.setPayOnCardDetailsVO(visa);
            payOnPreAuthRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnPreAuthResponseVO = (PayOnResponseVO) payOnGateway.processAuthentication("123456", payOnPreAuthRequestVO);

//            System.out.println("Pre Auth Response Code : " + payOnPreAuthResponseVO.getReturnCode());
//            System.out.println("Pre Auth Response Message : " + payOnPreAuthResponseVO.getReturnMesaage());

            PayOnRequestVO payOnCaptureRequestVO = new PayOnRequestVO();
            payOnCaptureRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnCaptureRequestVO.setPayOnCardDetailsVO(visa);

            PayOnTransactionDetailsVO captureTransaction = getTestTransactionDetails("29.90", "EUR", testCaseDescription);
            captureTransaction.setReferenceTransactionType(PayOnGateway.PRE_AUTH);
            captureTransaction.setConnectorTxID1(payOnPreAuthResponseVO.getConnectorTxID1());
            captureTransaction.setConnectorTxID2(payOnPreAuthResponseVO.getConnectorTxID2());
            captureTransaction.setConnectorTxID3(payOnPreAuthResponseVO.getConnectorTxID3());

            payOnCaptureRequestVO.setPayOnTransactionDetailsVO(captureTransaction);
            PayOnResponseVO payOnCaptureResponseVO = (PayOnResponseVO) payOnGateway.processCapture("123456", payOnCaptureRequestVO);

            String returnCode = payOnCaptureResponseVO.getReturnCode();
//            System.out.println("Capture Response Code : " + returnCode);
//            System.out.println("Capture Response Message : " + payOnCaptureResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }


    @Test
    public void testCaseA5()
    {

        String testCaseDescription = "Test Case A.5 for VISA";
        String expectedReturnCode = "800.100.152";
        try
        {
            /*System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("2.51", "USD", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);

            String returnCode = payOnDebitResponseVO.getReturnCode();
//            System.out.println("Debit Response Code : " + returnCode);
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }

    @Test
    public void testCaseA6()
    {

        String testCaseDescription = "Test Case A.6 for VISA";
        String expectedReturnCode = "800.100.151";
        try
        {
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("2.52", "GBP", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);

            String returnCode = payOnDebitResponseVO.getReturnCode();
//            System.out.println("Debit Response Code : " + returnCode);
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }

    @Test
    public void testCaseA7()
    {

        String testCaseDescription = "Test Case A.7 for VISA";
        String expectedReturnCode = "900.100.400";
        try
        {
            /*System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("2.53", "CHF", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);

            String returnCode = payOnDebitResponseVO.getReturnCode();
//            System.out.println("Debit Response Code : " + returnCode);
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }

    @Test
    public void testCaseA8()
    {

        String testCaseDescription = "Test Case A.8 for VISA";
        String expectedReturnCode = "900.100.300";
        try
        {

           /* System.out.println(testCaseDescription);
            System.out.println();
            System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("2.54", "EUR", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);

            String returnCode = payOnDebitResponseVO.getReturnCode();
//            System.out.println("Debit Response Code : " + returnCode);
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }


    @Test
    public void testCaseB1_USD()
    {
        String testCaseDescription = "Test Case B.1 for USD";
        String expectedReturnCode = "000.000.000";
        try
        {
           /* System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("10.01", "USD", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);


//            System.out.println("Debit Response Code : " + payOnDebitResponseVO.getReturnCode());
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            PayOnRequestVO payOnReversalRequestVO = new PayOnRequestVO();
            payOnReversalRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());

            PayOnTransactionDetailsVO reversalTransaction = getTestTransactionDetails("10.01", "USD", testCaseDescription);
            reversalTransaction.setReferenceTransactionType(PayOnGateway.DEBIT);
            reversalTransaction.setConnectorTxID1(payOnDebitResponseVO.getConnectorTxID1());
            reversalTransaction.setConnectorTxID2(payOnDebitResponseVO.getConnectorTxID2());
            reversalTransaction.setConnectorTxID3(payOnDebitResponseVO.getConnectorTxID3());

            payOnReversalRequestVO.setPayOnTransactionDetailsVO(reversalTransaction);
            payOnReversalRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            PayOnResponseVO payOnReversalResponseVO = (PayOnResponseVO) payOnGateway.processVoid("123456", payOnReversalRequestVO);

            String returnCode = payOnReversalResponseVO.getReturnCode();
//            System.out.println("Reversal Response Code : " + returnCode);
//            System.out.println("Reversal Response Message : " + payOnReversalResponseVO.getReturnMesaage());


            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseB1_EUR()
    {
        String testCaseDescription = "Test Case B.1 for EUR";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("10.01", "EUR", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);


//            System.out.println("Debit Response Code : " + payOnDebitResponseVO.getReturnCode());
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            PayOnRequestVO payOnReversalRequestVO = new PayOnRequestVO();
            payOnReversalRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());

            PayOnTransactionDetailsVO reversalTransaction = getTestTransactionDetails("10.01", "EUR", testCaseDescription);
            reversalTransaction.setReferenceTransactionType(PayOnGateway.DEBIT);
            reversalTransaction.setConnectorTxID1(payOnDebitResponseVO.getConnectorTxID1());
            reversalTransaction.setConnectorTxID2(payOnDebitResponseVO.getConnectorTxID2());
            reversalTransaction.setConnectorTxID3(payOnDebitResponseVO.getConnectorTxID3());

            payOnReversalRequestVO.setPayOnTransactionDetailsVO(reversalTransaction);
            payOnReversalRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            PayOnResponseVO payOnReversalResponseVO = (PayOnResponseVO) payOnGateway.processVoid("123456", payOnReversalRequestVO);

            String returnCode = payOnReversalResponseVO.getReturnCode();
//            System.out.println("Reversal Response Code : " + returnCode);
//            System.out.println("Reversal Response Message : " + payOnReversalResponseVO.getReturnMesaage());


            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }

        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }


    }


    @Test
    public void testCaseB1_GBP()
    {
        String testCaseDescription = "Test Case B.1 for GBP";
        String expectedReturnCode = "000.000.000";
        try
        {
            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("10.01", "GBP", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);


//            System.out.println("Debit Response Code : " + payOnDebitResponseVO.getReturnCode());
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            PayOnRequestVO payOnReversalRequestVO = new PayOnRequestVO();
            payOnReversalRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());

            PayOnTransactionDetailsVO reversalTransaction = getTestTransactionDetails("10.01", "GBP", testCaseDescription);
            reversalTransaction.setReferenceTransactionType(PayOnGateway.DEBIT);
            reversalTransaction.setConnectorTxID1(payOnDebitResponseVO.getConnectorTxID1());
            reversalTransaction.setConnectorTxID2(payOnDebitResponseVO.getConnectorTxID2());
            reversalTransaction.setConnectorTxID3(payOnDebitResponseVO.getConnectorTxID3());

            payOnReversalRequestVO.setPayOnTransactionDetailsVO(reversalTransaction);
            payOnReversalRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            PayOnResponseVO payOnReversalResponseVO = (PayOnResponseVO) payOnGateway.processVoid("123456", payOnReversalRequestVO);

            String returnCode = payOnReversalResponseVO.getReturnCode();
//            System.out.println("Reversal Response Code : " + returnCode);
//            System.out.println("Reversal Response Message : " + payOnReversalResponseVO.getReturnMesaage());


            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseB1_CHF()
    {
        String testCaseDescription = "Test Case B.1 for CHF";
        String expectedReturnCode = "000.000.000";
        try
        {
           /* System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("10.01", "CHF", testCaseDescription));
            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);


//            System.out.println("Debit Response Code : " + payOnDebitResponseVO.getReturnCode());
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            PayOnRequestVO payOnReversalRequestVO = new PayOnRequestVO();
            payOnReversalRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());

            PayOnTransactionDetailsVO reversalTransaction = getTestTransactionDetails("10.01", "CHF", testCaseDescription);
            reversalTransaction.setReferenceTransactionType(PayOnGateway.DEBIT);
            reversalTransaction.setConnectorTxID1(payOnDebitResponseVO.getConnectorTxID1());
            reversalTransaction.setConnectorTxID2(payOnDebitResponseVO.getConnectorTxID2());
            reversalTransaction.setConnectorTxID3(payOnDebitResponseVO.getConnectorTxID3());

            payOnReversalRequestVO.setPayOnTransactionDetailsVO(reversalTransaction);
            payOnReversalRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            PayOnResponseVO payOnReversalResponseVO = (PayOnResponseVO) payOnGateway.processVoid("123456", payOnReversalRequestVO);

            String returnCode = payOnReversalResponseVO.getReturnCode();
//            System.out.println("Reversal Response Code : " + returnCode);
//            System.out.println("Reversal Response Message : " + payOnReversalResponseVO.getReturnMesaage());


            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseC1_INITIAL()
    {
        String testCaseDescription = "Test Case C.1 with INITIAL";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());


            PayOnTransactionDetailsVO testTransactionDetails = getTestTransactionDetails("20.00", "EUR", testCaseDescription);
            testTransactionDetails.setRecurrenceMode("INITIAL");
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(testTransactionDetails);

            payOnDebitRequestVO.setPayOnCardDetailsVO(getTestCardDetails("VISA"));
            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);


            String returnCode = payOnDebitResponseVO.getReturnCode();
//            System.out.println("Debit Response Code : " + returnCode);
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseC2_REPEATED()
    {
        String testCaseDescription = "Test Case C.2 with REPEATED";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnDebitRequestVO = new PayOnRequestVO();
            payOnDebitRequestVO.setPayOnMerchantAccountVO(getTestMerchantDetails());
            PayOnTransactionDetailsVO testTransactionDetails = getTestTransactionDetails("21.00", "EUR", testCaseDescription);
            testTransactionDetails.setRecurrenceMode("REPEATED");
            payOnDebitRequestVO.setPayOnTransactionDetailsVO(testTransactionDetails);


            PayOnCardDetailsVO payOnCardDetailsVO = new PayOnCardDetailsVO();
            payOnCardDetailsVO.setCardNum("4012888888881881");
            payOnCardDetailsVO.setCardType("VISA");
            payOnCardDetailsVO.setExpMonth("01");
            payOnCardDetailsVO.setExpYear("2015");


            payOnDebitRequestVO.setPayOnCardDetailsVO(payOnCardDetailsVO);

            payOnDebitRequestVO.setAddressDetailsVO(getTestAddressDetails());
            PayOnResponseVO payOnDebitResponseVO = (PayOnResponseVO) payOnGateway.processSale("123456", payOnDebitRequestVO);


            String returnCode = payOnDebitResponseVO.getReturnCode();
//            System.out.println("Debit Response Code : " + returnCode);
//            System.out.println("Debit Response Message : " + payOnDebitResponseVO.getReturnMesaage());

            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseD1()
    {

        String testCaseDescription = "Test Case D.1 for VISA";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnPreAuthRequestVO = new PayOnRequestVO();

            PayOnCardDetailsVO visa = getTestCardDetails("VISA");
            PayOnMerchantAccountVO testMerchantDetails = getTestMerchantDetails();

            payOnPreAuthRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnPreAuthRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("25.00", "EUR", testCaseDescription));
            payOnPreAuthRequestVO.setPayOnCardDetailsVO(visa);
            payOnPreAuthRequestVO.setAddressDetailsVO(getTestAddressDetails());


            PayOnVBVDetailsVO payOnVBVDetailsVO = new PayOnVBVDetailsVO();
            payOnVBVDetailsVO.setEci("05");
            payOnVBVDetailsVO.setVerification("AAABAhGEcSWFUSWFVIRwAAAAAAA=");
            payOnVBVDetailsVO.setXid("MDAwMDAwMDAwMDAwNjUwOTk4ODE=");

            payOnPreAuthRequestVO.setPayOnVBVDetailsVO(payOnVBVDetailsVO);

            PayOnResponseVO payOnPreAuthResponseVO = (PayOnResponseVO) payOnGateway.processAuthentication("123456", payOnPreAuthRequestVO);

            String returnCode = payOnPreAuthResponseVO.getReturnCode();

//            System.out.println("Pre Auth Response Code : " + returnCode);
//            System.out.println("Pre Auth Response Message : " + payOnPreAuthResponseVO.getReturnMesaage());
            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseD2()
    {

        String testCaseDescription = "Test Case D.2 for MASTER";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnPreAuthRequestVO = new PayOnRequestVO();

            PayOnCardDetailsVO visa = getTestCardDetails("MASTER");
            PayOnMerchantAccountVO testMerchantDetails = getTestMerchantDetails();

            payOnPreAuthRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnPreAuthRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("26.01", "USD", testCaseDescription));
            payOnPreAuthRequestVO.setPayOnCardDetailsVO(visa);
            payOnPreAuthRequestVO.setAddressDetailsVO(getTestAddressDetails());


            PayOnVBVDetailsVO payOnVBVDetailsVO = new PayOnVBVDetailsVO();
            payOnVBVDetailsVO.setEci("01");
            payOnVBVDetailsVO.setVerification("AAABAhGEcSWFUSWFVIRwAAAAAAA=");
            payOnVBVDetailsVO.setXid("MDAwMDAwMDAwMDAwNjUwOTk4ODE=");

            payOnPreAuthRequestVO.setPayOnVBVDetailsVO(payOnVBVDetailsVO);

            PayOnResponseVO payOnPreAuthResponseVO = (PayOnResponseVO) payOnGateway.processAuthentication("123456", payOnPreAuthRequestVO);

            String returnCode = payOnPreAuthResponseVO.getReturnCode();

//            System.out.println("Pre Auth Response Code : " + returnCode);
//            System.out.println("Pre Auth Response Message : " + payOnPreAuthResponseVO.getReturnMesaage());
            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseD3()
    {

        String testCaseDescription = "Test Case D.3 for MASTER";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnPreAuthRequestVO = new PayOnRequestVO();

            PayOnCardDetailsVO visa = getTestCardDetails("MASTER");
            PayOnMerchantAccountVO testMerchantDetails = getTestMerchantDetails();

            payOnPreAuthRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnPreAuthRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("26.02", "USD", testCaseDescription));
            payOnPreAuthRequestVO.setPayOnCardDetailsVO(visa);
            payOnPreAuthRequestVO.setAddressDetailsVO(getTestAddressDetails());


            PayOnVBVDetailsVO payOnVBVDetailsVO = new PayOnVBVDetailsVO();
            payOnVBVDetailsVO.setEci("02");
            payOnVBVDetailsVO.setVerification("AAABAhGEcSWFUSWFVIRwAAAAAAA=");
            payOnVBVDetailsVO.setXid("MDAwMDAwMDAwMDAwNjUwOTk4ODE=");

            payOnPreAuthRequestVO.setPayOnVBVDetailsVO(payOnVBVDetailsVO);

            PayOnResponseVO payOnPreAuthResponseVO = (PayOnResponseVO) payOnGateway.processAuthentication("123456", payOnPreAuthRequestVO);

            String returnCode = payOnPreAuthResponseVO.getReturnCode();

//            System.out.println("Pre Auth Response Code : " + returnCode);
//            System.out.println("Pre Auth Response Message : " + payOnPreAuthResponseVO.getReturnMesaage());
            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


    @Test
    public void testCaseD4()
    {

        String testCaseDescription = "Test Case D.4 for VISA";
        String expectedReturnCode = "000.000.000";
        try
        {

            /*System.out.println(testCaseDescription);
                        System.out.println();
                        System.out.println();*/
            PayOnRequestVO payOnPreAuthRequestVO = new PayOnRequestVO();

            PayOnCardDetailsVO visa = getTestCardDetails("VISA");
            PayOnMerchantAccountVO testMerchantDetails = getTestMerchantDetails();

            payOnPreAuthRequestVO.setPayOnMerchantAccountVO(testMerchantDetails);
            payOnPreAuthRequestVO.setPayOnTransactionDetailsVO(getTestTransactionDetails("26.03", "EUR", testCaseDescription));
            payOnPreAuthRequestVO.setPayOnCardDetailsVO(visa);
            payOnPreAuthRequestVO.setAddressDetailsVO(getTestAddressDetails());


            PayOnVBVDetailsVO payOnVBVDetailsVO = new PayOnVBVDetailsVO();
            payOnVBVDetailsVO.setEci("06");
            payOnVBVDetailsVO.setVerification("AAABAhGEcSWFUSWFVIRwAAAAAAA=");
            payOnVBVDetailsVO.setXid("MDAwMDAwMDAwMDAwNjUwOTk4ODE=");

            payOnPreAuthRequestVO.setPayOnVBVDetailsVO(payOnVBVDetailsVO);

            PayOnResponseVO payOnPreAuthResponseVO = (PayOnResponseVO) payOnGateway.processAuthentication("123456", payOnPreAuthRequestVO);

            String returnCode = payOnPreAuthResponseVO.getReturnCode();

//            System.out.println("Pre Auth Response Code : " + returnCode);
//            System.out.println("Pre Auth Response Message : " + payOnPreAuthResponseVO.getReturnMesaage());
            assertEquals("ReturnCode does not match expected Value", expectedReturnCode, returnCode);


        }
        catch (PZConstraintViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
        catch (PZTechnicalViolationException e)
        {
            //System.out.println("Error executing " + testCaseDescription);
            fail();
        }
    }


}
