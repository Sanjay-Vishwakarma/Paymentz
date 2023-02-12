package com.payment.unicredit;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.ems.core.EMSUtils;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Admin on 12/28/2017.
 */
public class UnicreditPaymentGateway extends AbstractPaymentGateway
{
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.unicredit");

    public static String test = "https://gatet.borica.bg/boreps/";
    public static String live = "https://gate.borica.bg/boreps/";

    String TEST_URL = RB.getString("TEST_URL");
    String LIVE_URL = RB.getString("LIVE_URL");

    public static final String GATEWAY_TYPE = "unicredit";

    public UnicreditPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    private static TransactionLogger transactionlogger = new TransactionLogger(UnicreditPaymentGateway.class.getName());

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Entering processSale of UnicreditPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();

        String url = "";

        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String terminal = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        //System.out.println("accid---"+accountId+"-"+isTest);

        BOBS3DsecureImplementation implementation;
        implementation = new BOBS3DsecureImplementation();
        implementation.setTransactionCode(10);
        implementation.setTransactionAmount(commRequestVO.getTransDetailsVO().getAmount());
        implementation.setTerminalID("62161190");
        implementation.setOrderID(trackingID);
        implementation.setOrderDescr(commRequestVO.getTransDetailsVO().getOrderId());
        implementation.setLanguage("EN");
        implementation.setCurrency(currency);
        implementation.setProtocolVersion("1.1");
        if(isTest)
        {
            implementation.setBOR_URL(TEST_URL);
        }
        else
        {
            implementation.setBOR_URL(LIVE_URL);
        }

        implementation.generateBOReq(isTest,"sale");
        String req = implementation.getBOReq();
        transactionlogger.error("request---" + req);

        String[] rMessage = req.split("=");

        comm3DResponseVO.setUrlFor3DRedirect(req);
        /*comm3DResponseVO.setUrlFor3DRedirect(url+"registerTransaction");
        comm3DResponseVO.setPaReq(rMessage[1]);*/

        return comm3DResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = null;
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO = reqVO.getTransDetailsVO();

        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String terminal = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        UnicreditUtils unicreditUtils = new UnicreditUtils();

        BOBS3DsecureImplementation implementation;
        implementation = new BOBS3DsecureImplementation();
        implementation.setTransactionCode(Integer.parseInt(transactionDetailsVO.getTransactionType()));
        implementation.setTransactionAmount(transactionDetailsVO.getAmount());
        implementation.setTerminalID(terminal);
        implementation.setOrderID(transactionDetailsVO.getOrderId());
        implementation.setOrderDescr(transactionDetailsVO.getOrderDesc());
        implementation.setLanguage("EN");
        implementation.setCurrency(transactionDetailsVO.getCurrency());
        implementation.setProtocolVersion("1.1");

        if(isTest)
        {
            implementation.setBOR_URL(TEST_URL);
        }
        else
        {
            implementation.setBOR_URL(LIVE_URL);
        }

        implementation.generateBOReq(isTest,"inquiry");
        String req = implementation.getBOReq();
        transactionlogger.error("request---" + req);

        String response = unicreditUtils.doGetHTTPSURLConnectionClient(req);
        transactionlogger.error("Response---" + response);

        commResponseVO = unicreditUtils.decodeBOResp(response);
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = null;
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO = reqVO.getTransDetailsVO();

        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String terminal = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        UnicreditUtils unicreditUtils = new UnicreditUtils();

        BOBS3DsecureImplementation implementation;
        implementation = new BOBS3DsecureImplementation();
        implementation.setTransactionCode(40);
        implementation.setTransactionAmount(transactionDetailsVO.getAmount());
        implementation.setTerminalID(terminal);
        implementation.setOrderID(transactionDetailsVO.getOrderId());
        implementation.setOrderDescr(transactionDetailsVO.getOrderDesc());
        implementation.setLanguage("EN");
        implementation.setCurrency(currency);
        implementation.setProtocolVersion("1.1");

        if(isTest)
        {
            implementation.setBOR_URL(TEST_URL);
        }
        else
        {
            implementation.setBOR_URL(LIVE_URL);
        }

        implementation.generateBOReq(isTest,"refund");
        String req = implementation.getBOReq();
        transactionlogger.error("request---" + req);

        String response = unicreditUtils.doGetHTTPSURLConnectionClient(req);
        transactionlogger.error("Response---" + response);

        commResponseVO = unicreditUtils.decodeBOResp(response);
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = null;
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO = reqVO.getTransDetailsVO();

        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String terminal = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        UnicreditUtils unicreditUtils = new UnicreditUtils();

        BOBS3DsecureImplementation implementation;
        implementation = new BOBS3DsecureImplementation();
        implementation.setTransactionCode(11);
        implementation.setTransactionAmount(transactionDetailsVO.getAmount());
        implementation.setTerminalID(terminal);
        implementation.setOrderID(transactionDetailsVO.getPreviousTransactionId());
        implementation.setOrderDescr(transactionDetailsVO.getOrderId());
        implementation.setLanguage("EN");
        implementation.setCurrency(currency);
        implementation.setProtocolVersion("1.1");

        if(isTest)
        {
            implementation.setBOR_URL(TEST_URL);
        }
        else
        {
            implementation.setBOR_URL(LIVE_URL);
        }

        implementation.generateBOReq(isTest,"refund");
        String req = implementation.getBOReq();
        transactionlogger.error("request---" + req);

        String response = unicreditUtils.doGetHTTPSURLConnectionClient(req);
        transactionlogger.error("Response---" + response);

        commResponseVO = unicreditUtils.decodeBOResp(response);
        return commResponseVO;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(":::::Entered into processAutoRedirect for UniCredit:::::");
        CommRequestVO commRequestVO = null;
        String html = "";
        CommResponseVO transRespDetails = null;

        UnicreditUtils unicreditUtils = new UnicreditUtils();

        commRequestVO = unicreditUtils.getUnicreditRequestVO(commonValidatorVO);
        transRespDetails = (CommResponseVO) this.processSale(String.valueOf(commonValidatorVO.getTrackingid()), commRequestVO);

        Comm3DResponseVO response3D = null;

        if (transRespDetails != null)
        {
            response3D = (Comm3DResponseVO) transRespDetails;
            transactionlogger.error("URL Unicredit---" + response3D.getUrlFor3DRedirect());
            transactionlogger.error("Messgae Unicredit---" + response3D.getPaReq());
            html = unicreditUtils.generateAutoSubmitForm(response3D);
            transactionlogger.error("HTML Unicredit---" + html);
        }
        return html;
    }

    public static void main(String[] args)
    {
        try
        {
            UnicreditUtils unicreditUtils = new UnicreditUtils();
            BOBS3DsecureImplementation implementation;
            implementation = new BOBS3DsecureImplementation();
            implementation.setTransactionCode(40);
            implementation.setTransactionAmount("50.00");
            implementation.setTerminalID("62161190");
            implementation.setOrderID("54169");
            implementation.setOrderDescr("TEST");
            implementation.setLanguage("EN");
            implementation.setCurrency("EUR");
            implementation.setProtocolVersion("1.1");
            implementation.setBOR_URL(test);

            implementation.generateBOReq(true,"inquiry");
            //String req = implementation.getBOReq();
            String req = "https://gatet.borica.bg/boreps/manageTransaction?eBorica=NDAyMDE4MDIyMTE2NTYwNDAwMDAwMDAwMDEwMDYyMTYxMTkwNjQ1NDAgICAgICAgICAgQWxsIFJldmVyc2UgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBFTjEuMUVVUhAeP93aeJlgnY8iSSs9VE5awGlTdLOsFNN96Y69HArJ0ZCRMYUnbX%2FTJkj0IkSpEm2BFgC5xvD9m86z0wlW%2Bw9N5P9c7xQs%2Ba5OSEIiDhF1buEDs0nE9r58yqjuoqzdMzlEWgz1VacxXw0Cv7dCjQaN%2BKOurFmeOMLRW9GyKmvu";
            //System.out.println("request---" + req);

            /*SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = date.format(new Date());
            FileOutputStream fout= new FileOutputStream("BOReq_"+ time + ".txt");
            fout.write(req.getBytes());
            fout.flush();
            fout.close();*/

            String response = unicreditUtils.doGetHTTPSURLConnectionClient(req);
            //System.out.println("Response---"+response);
            BOBS3DsecureImplementation bobs3DsecureImplementation = new BOBS3DsecureImplementation();
            bobs3DsecureImplementation.verifyBOResp(response,"EuroFootball_eBorica_LiveKey");

            //implementation.verifyBOResp(BORespMessage, "APGWpublicKey");
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception :::::::",e);
        }
    }


}
