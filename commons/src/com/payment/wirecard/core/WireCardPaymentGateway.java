 package com.payment.wirecard.core;

import com.bea.xml.stream.samples.Parse;
import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZInquiryRequest;
import com.thoughtworks.xstream.XStream;
import org.jaxen.function.FloorFunction;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

 /**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class WireCardPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE = "WireCard";
    private static Logger log = new Logger(WireCardPaymentGateway.class.getName());
    private String username = "56500";
    private String password = "TestXAPTER";
    private String signature = "5650";
    private String contentType ="text/xml";
    private String url = "https://c3-test.wirecard.com/secure/ssl-gateway";
    private final int TIMEOUT = 30000;
    private String xmlSchema = "http://www.w3.org/1999/XMLSchema-instance";
    private String xsdValue = "wirecard.xsd";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.WireCard");
    //String webPage = "https://c3-test.wirecard.com/secure/ssl-gateway";
    String webPage = RB.getString("wirecardurl");
    String mode = RB.getString("mode");
    //private String mode = "demo";




    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }


    public WireCardPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public  WireCardPaymentGateway()
    {

    }

    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String errMsg,type,no,guWid,advice,result,timeStamp,authCode,info;

        WireCardRequestVO wireCardRequestVO = (WireCardRequestVO)requestVO;
        username = wireCardRequestVO.getCommMerchantVO().getMerchantUsername();
        password = wireCardRequestVO.getCommMerchantVO().getPassword();

        CommResponseVO commResponseVO = new CommResponseVO();

            CREDIT_CARD_DATA credit_card_data = new CREDIT_CARD_DATA();
            credit_card_data.setCreditCardNumber(wireCardRequestVO.getCardDetailsVO().getCardNum());
            credit_card_data.setCardHolderName(wireCardRequestVO.getCardDetailsVO().getCardHolderName());
            credit_card_data.setExpireMonth(wireCardRequestVO.getCardDetailsVO().getExpMonth());
            credit_card_data.setExpireYear(wireCardRequestVO.getCardDetailsVO().getExpYear());
            credit_card_data.setCvc2(wireCardRequestVO.getCardDetailsVO().getcVV());

            CONTACT_DATA contact_data = new CONTACT_DATA();
            contact_data.setIPAddress(wireCardRequestVO.getAddressDetailsVO().getIp());

            RECURRING_TRANSACTION recurring_transaction = new RECURRING_TRANSACTION();
            recurring_transaction.setType("Initial");

            CC_TRANSACTION cc_transaction = new CC_TRANSACTION();

            cc_transaction.setMode(getMode());
            cc_transaction.setTransactionID(trackingID);

            String amount = wireCardRequestVO.getTransDetailsVO().getAmount();

            Double dObj2 = Double.valueOf(amount);
            dObj2= dObj2 * 100;
            Integer newAmount = dObj2.intValue();

            cc_transaction.setAmount(newAmount.toString());
            cc_transaction.setCurrency(wireCardRequestVO.getTransDetailsVO().getCurrency());
            cc_transaction.setCountryCode(wireCardRequestVO.getAddressDetailsVO().getCountry());

            cc_transaction.setCredit_card_data(credit_card_data);
            cc_transaction.setContact_data(contact_data);
            cc_transaction.setRecurring_transaction(recurring_transaction);

            Address address = new Address();
            address.setFirstName(wireCardRequestVO.getAddressDetailsVO().getFirstname());
            address.setLastName(wireCardRequestVO.getAddressDetailsVO().getLastname());
            address.setAddress1(wireCardRequestVO.getAddressDetailsVO().getStreet());
            address.setCity(wireCardRequestVO.getAddressDetailsVO().getCity());
            address.setCountry(wireCardRequestVO.getAddressDetailsVO().getCountry());
            address.setPhone(wireCardRequestVO.getAddressDetailsVO().getPhone());
            address.setZip(wireCardRequestVO.getAddressDetailsVO().getZipCode());
            address.setEmail(wireCardRequestVO.getAddressDetailsVO().getEmail());

            CORPTRUSTCENTER_DATA conCorptrustcenter_data = new CORPTRUSTCENTER_DATA();
            conCorptrustcenter_data.setAddress(address);

            cc_transaction.setCorptrustcenter_data(conCorptrustcenter_data);


            FNC_CC_PURCHASE fnc_cc_purchase = new FNC_CC_PURCHASE();
            fnc_cc_purchase.setCc_transaction(cc_transaction);
            fnc_cc_purchase.setFunctionID(trackingID);

            W_JOB w_job = new W_JOB();
            w_job.setBusinessCaseSignature(wireCardRequestVO.getCommMerchantVO().getMerchantId()); // need to be added
            w_job.setFnc_cc_purchase(fnc_cc_purchase);
            w_job.setJobID(trackingID);

            W_REQUEST w_request = new W_REQUEST();
            w_request.setW_job(w_job);

            WIRECARD_BXML wirecard_bxml = new WIRECARD_BXML();
            wirecard_bxml.setW_request(w_request);

            String response  = make_request(wirecard_bxml, username, password);

            W_JOB wJobResponse = getW_job(response);

            ERROR wJobError = wJobResponse.getError();

            if(wJobError != null)
            {
                errMsg = wJobError.getMessage();
                no = wJobError.getNumber();
                type = wJobError.getType();

                commResponseVO.setDescription(errMsg);
                commResponseVO.setErrorCode(no);
                commResponseVO.setStatus("fail");
            } else
            {

                FNC_CC_PURCHASE  purchase_response = wJobResponse .getFnc_cc_purchase();
                CC_TRANSACTION cc_transaction_response = purchase_response.getCc_transaction();

                String TransactionID = cc_transaction_response.getTransactionID().toString();

                PROCESSING_STATUS processing_status = cc_transaction_response.getProcessing_status();
                guWid = processing_status.getGuWID();
                authCode = processing_status.getAuthorizationCode();
                result = processing_status.getFunctionResult();
                info = processing_status.getInfo();
                timeStamp = processing_status.getTimeStamp();

                ERROR error = processing_status.getError();

                if(error != null)
                {
                    errMsg = error.getMessage();
                    no = error.getNumber();
                    type = error.getType();
                    advice = error.getAdvice();

                    commResponseVO.setDescription(errMsg);
                    commResponseVO.setDescriptor(advice);
                    commResponseVO.setErrorCode(no);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionId(guWid);
                }else
                {
                     commResponseVO.setStatus("success");
                     commResponseVO.setTransactionId(guWid);
                     commResponseVO.setDescription(info);
                     commResponseVO.setDescriptor(info);
                }
            }


        return  commResponseVO;
    }



    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String errMsg,type,no,guWid,advice,result,timeStamp,authCode,info;
        CommResponseVO commResponseVO = new CommResponseVO();


            WireCardRequestVO wireCardRequestVO = (WireCardRequestVO)requestVO;
            username = wireCardRequestVO.getCommMerchantVO().getMerchantUsername();
            password = wireCardRequestVO.getCommMerchantVO().getPassword();

            CREDIT_CARD_DATA credit_card_data = new CREDIT_CARD_DATA();
            credit_card_data.setCardHolderName(wireCardRequestVO.getCardDetailsVO().getCardHolderName());
            credit_card_data.setCreditCardNumber(wireCardRequestVO.getCardDetailsVO().getCardNum());
            credit_card_data.setExpireMonth(wireCardRequestVO.getCardDetailsVO().getExpMonth());
            credit_card_data.setExpireYear(wireCardRequestVO.getCardDetailsVO().getExpYear());
            credit_card_data.setCvc2(wireCardRequestVO.getCardDetailsVO().getcVV());

            CONTACT_DATA contact_data = new CONTACT_DATA();
            contact_data.setIPAddress(wireCardRequestVO.getAddressDetailsVO().getIp());

            RECURRING_TRANSACTION recurring_transaction = new RECURRING_TRANSACTION();
            recurring_transaction.setType("Initial");

            CC_TRANSACTION cc_transaction = new CC_TRANSACTION();
            cc_transaction.setMode(getMode());
            cc_transaction.setTransactionID(trackingID);
            String amount = wireCardRequestVO.getTransDetailsVO().getAmount();

            //String newAmount = amount.replace(".","");
            Double dObj2 = Double.valueOf(amount);
            dObj2= dObj2 * 100;
            Integer newAmount = dObj2.intValue();

            cc_transaction.setAmount(newAmount.toString());

            cc_transaction.setCurrency(wireCardRequestVO.getTransDetailsVO().getCurrency());
            cc_transaction.setCountryCode(wireCardRequestVO.getAddressDetailsVO().getCountry());

            cc_transaction.setCredit_card_data(credit_card_data);
            cc_transaction.setContact_data(contact_data);
            cc_transaction.setRecurring_transaction(recurring_transaction);

            FNC_CC_PREAUTHORIZATION fnc_cc_preauthorization = new FNC_CC_PREAUTHORIZATION();
            fnc_cc_preauthorization.setFunctionID("authorization " + trackingID);
            fnc_cc_preauthorization.setCc_transaction(cc_transaction);

            Address address = new Address();
            address.setFirstName(wireCardRequestVO.getAddressDetailsVO().getFirstname());
            address.setLastName(wireCardRequestVO.getAddressDetailsVO().getLastname());
            address.setAddress1(wireCardRequestVO.getAddressDetailsVO().getStreet());
            address.setCity(wireCardRequestVO.getAddressDetailsVO().getCity());
            address.setCountry(wireCardRequestVO.getAddressDetailsVO().getCountry());
            address.setPhone(wireCardRequestVO.getAddressDetailsVO().getPhone());
            address.setZip(wireCardRequestVO.getAddressDetailsVO().getZipCode());
            address.setEmail(wireCardRequestVO.getAddressDetailsVO().getEmail());

            CORPTRUSTCENTER_DATA conCorptrustcenter_data = new CORPTRUSTCENTER_DATA();
            conCorptrustcenter_data.setAddress(address);

            cc_transaction.setCorptrustcenter_data(conCorptrustcenter_data);

            W_JOB w_job = new W_JOB();
            w_job.setBusinessCaseSignature(wireCardRequestVO.getCommMerchantVO().getMerchantId()); // need to be added
            w_job.setFnc_cc_preauthorization(fnc_cc_preauthorization);
            w_job.setJobID("Auth Job " + trackingID);

            W_REQUEST w_request = new W_REQUEST();
            w_request.setW_job(w_job);

            WIRECARD_BXML wirecard_bxml = new WIRECARD_BXML();
            wirecard_bxml.setXmlns(xmlSchema);
            wirecard_bxml.setXsi(xsdValue);
            wirecard_bxml.setW_request(w_request);

            String response =  make_request(wirecard_bxml, username, password);

            W_JOB wJobResponse = getW_job(response);

            ERROR wJobError = wJobResponse.getError();

            if(wJobError != null)
            {
                errMsg = wJobError.getMessage();
                no = wJobError.getNumber();
                type = wJobError.getType();

                commResponseVO.setDescription(errMsg);
                commResponseVO.setErrorCode(no);
                commResponseVO.setStatus("fail");
                commResponseVO.setDescriptor(type);

            }
            else
            {

                FNC_CC_PREAUTHORIZATION  preauth_response = wJobResponse .getFnc_cc_preauthorization();
                CC_TRANSACTION cc_transaction_response = preauth_response.getCc_transaction();

                String TransactionID = cc_transaction_response.getTransactionID().toString();

                PROCESSING_STATUS processing_status = cc_transaction_response.getProcessing_status();

                guWid = processing_status.getGuWID();
                authCode = processing_status.getAuthorizationCode();
                result = processing_status.getFunctionResult();
                info = processing_status.getInfo();
                timeStamp = processing_status.getTimeStamp();

                ERROR error = processing_status.getError();

                if(error != null)
                {

                    errMsg = error.getMessage();
                    no = error.getNumber();
                    type = error.getType();
                    advice = error.getAdvice();
                    commResponseVO.setDescription(errMsg);
                    commResponseVO.setErrorCode(no);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescriptor(advice);
                    commResponseVO.setTransactionId(guWid);

                    return  commResponseVO;
                }
                else
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(guWid);
                    commResponseVO.setDescription(info);
                    commResponseVO.setResponseTime(timeStamp);
                    commResponseVO.setDescriptor(info);
                }
            }

        return  commResponseVO;
    }



    @Override
    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String errMsg,type,no,guWid,advice,result,timeStamp,authCode,info;

        CommResponseVO commResponseVO = new CommResponseVO();


            WireCardRequestVO wireCardRequestVO = (WireCardRequestVO)requestVO;
            username = wireCardRequestVO.getCommMerchantVO().getMerchantUsername();
            password = wireCardRequestVO.getCommMerchantVO().getPassword();

            CC_TRANSACTION cc_transaction = new CC_TRANSACTION();
            cc_transaction.setMode(mode);
            cc_transaction.setTransactionID(trackingID);
            cc_transaction.setGuWID(wireCardRequestVO.getTransDetailsVO().getPreviousTransactionId());
            String amount = wireCardRequestVO.getTransDetailsVO().getAmount();
            //String newAmount = amount.replace(".","");

            Double dObj2 = Double.valueOf(amount);
            dObj2 = dObj2 * 100;
            Integer newAmount = dObj2.intValue();

            cc_transaction.setAmount(newAmount.toString());

            cc_transaction.setUsage("Bookback " + trackingID);

            FNC_CC_BOOKBACK fnc_cc_bookback = new FNC_CC_BOOKBACK();
            fnc_cc_bookback.setCc_transaction(cc_transaction);
            fnc_cc_bookback.setFunctionID("Bookback " + trackingID);

            W_JOB w_job = new W_JOB();
            w_job.setBusinessCaseSignature(wireCardRequestVO.getCommMerchantVO().getMerchantKey());
            w_job.setFnc_cc_bookback(fnc_cc_bookback);

            W_REQUEST w_request = new W_REQUEST();

            w_request.setW_job(w_job);

            WIRECARD_BXML wirecard_bxml = new WIRECARD_BXML();
            wirecard_bxml.setW_request(w_request);

            String response = make_request(wirecard_bxml, username, password);
            W_JOB wJobResponse = getW_job(response);

            ERROR wJobError = wJobResponse.getError();

            if(wJobError != null)
            {
                errMsg = wJobError.getMessage();
                no = wJobError.getNumber();
                type = wJobError.getType();

                commResponseVO.setDescription(errMsg);
                commResponseVO.setErrorCode(no);
                commResponseVO.setStatus("fail");
            }else
            {

                FNC_CC_BOOKBACK bookback_response = wJobResponse.getFnc_cc_bookback();
                CC_TRANSACTION cc_transaction_response = bookback_response.getCc_transaction();

                String TransactionID = cc_transaction_response.getTransactionID().toString();

                PROCESSING_STATUS processing_status = cc_transaction_response.getProcessing_status();
                guWid = processing_status.getGuWID();
                result = processing_status.getFunctionResult();
                info = processing_status.getInfo();


                ERROR error = processing_status.getError();
                timeStamp = processing_status.getTimeStamp();

                if(error != null)
                {
                    errMsg = error.getMessage();
                    no = error.getNumber();
                    type = error.getType();
                    advice = error.getAdvice();

                    commResponseVO.setDescription(errMsg);
                    commResponseVO.setErrorCode(no);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescriptor(advice);
                    commResponseVO.setTransactionId(guWid);
                    commResponseVO.setTransactionId(guWid);
                }
                else
                {
                    timeStamp = processing_status.getTimeStamp();
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescription(result);
                    commResponseVO.setTransactionId(guWid);
                    commResponseVO.setResponseTime(timeStamp);
                    commResponseVO.setDescriptor(info);
                }
            }


        return  commResponseVO;

    }



    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String errMsg,type,no,guWid,advice,result,timeStamp,authCode,info;

        CommResponseVO commResponseVO = new CommResponseVO();


            WireCardRequestVO wireCardRequestVO = (WireCardRequestVO)requestVO;
            username = wireCardRequestVO.getCommMerchantVO().getMerchantUsername();
            password = wireCardRequestVO.getCommMerchantVO().getPassword();

            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            guWid = commRequestVO.getTransDetailsVO().getPreviousTransactionId();

            CC_TRANSACTION cc_transaction = new CC_TRANSACTION();
            cc_transaction.setMode(mode);
            cc_transaction.setTransactionID(trackingID);
            cc_transaction.setGuWID(guWid);
            cc_transaction.setUsage("Order no " + trackingID );
            String amount = wireCardRequestVO.getTransDetailsVO().getAmount();
            //String newAmount = amount.replace(".","");

            Double dObj2 = Double.valueOf(amount);
            dObj2 = dObj2 * 100;
            Integer newAmount = dObj2.intValue();

            cc_transaction.setAmount(newAmount.toString());

            cc_transaction.setCurrency(wireCardRequestVO.getTransDetailsVO().getCurrency());
            cc_transaction.setCountryCode(wireCardRequestVO.getAddressDetailsVO().getCountry());


            FNC_CC_CAPTURE fnc_cc_capture = new FNC_CC_CAPTURE();
            fnc_cc_capture.setCc_transaction(cc_transaction);
            fnc_cc_capture.setFunctionID("Capture " + trackingID);

            W_JOB w_job = new W_JOB();
            w_job.setJobID("Job " + trackingID);
            w_job.setBusinessCaseSignature(wireCardRequestVO.getCommMerchantVO().getMerchantId());
            w_job.setFnc_cc_capture(fnc_cc_capture);

            W_REQUEST w_request = new W_REQUEST();
            w_request.setW_job(w_job);

            WIRECARD_BXML wirecard_bxml = new WIRECARD_BXML();
           // wirecard_bxml.setXmlns(url);
            //wirecard_bxml.setXsi("wirecard.xsd");
            wirecard_bxml.setW_request(w_request);

            String response = make_request(wirecard_bxml, username, password);

            W_JOB wJobResponse = getW_job(response);

            ERROR wJobError = wJobResponse.getError();

            if(wJobError != null)
            {
                errMsg = wJobError.getMessage();
                no = wJobError.getNumber();
                type = wJobError.getType();

                commResponseVO.setDescription(errMsg);
                commResponseVO.setDescriptor(errMsg);
                commResponseVO.setErrorCode(no);
                commResponseVO.setStatus("fail");

            }else
            {

                FNC_CC_CAPTURE capture_response = wJobResponse.getFnc_cc_capture();
                CC_TRANSACTION cc_transaction_response = capture_response.getCc_transaction();

                String TransactionID = cc_transaction_response.getTransactionID().toString();

                PROCESSING_STATUS processing_status = cc_transaction_response.getProcessing_status();
                guWid = processing_status.getGuWID();
                result = processing_status.getFunctionResult();
                timeStamp = processing_status.getTimeStamp();
                type = processing_status.getStatusType();
                info = processing_status.getInfo();

                ERROR error = processing_status.getError();

                if(error != null)
                {
                    errMsg = error.getMessage();
                    no = error.getNumber();
                    type = error.getType();
                    advice = error.getAdvice();

                    commResponseVO.setDescription(errMsg);
                    commResponseVO.setDescriptor(advice);
                    commResponseVO.setErrorCode(no);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setResponseTime(timeStamp);
                    commResponseVO.setTransactionId(guWid);
                }
                else
                {
                    commResponseVO.setDescription(info);
                    commResponseVO.setStatus("success");
                    commResponseVO.setResponseTime(timeStamp);
                    commResponseVO.setTransactionId(guWid);
                    commResponseVO.setDescriptor(info);
                }

            }


        return  commResponseVO;
    }


    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String errMsg,type,no,advice,timeStamp,guWid,result,info;
        CommResponseVO commResponseVO = new CommResponseVO();


            WireCardRequestVO wireCardRequestVO = (WireCardRequestVO)requestVO;
            username = wireCardRequestVO.getCommMerchantVO().getMerchantUsername();
            password = wireCardRequestVO.getCommMerchantVO().getPassword();

            CC_TRANSACTION cc_transaction = new CC_TRANSACTION();
            cc_transaction.setTransactionID(trackingID);
            cc_transaction.setGuWID(wireCardRequestVO.getTransDetailsVO().getPreviousTransactionId());

            FNC_CC_REVERSAL fnc_cc_reversal = new FNC_CC_REVERSAL();
            fnc_cc_reversal.setCc_transaction(cc_transaction);
            fnc_cc_reversal.setFunctionID(trackingID);

             W_JOB w_job = new W_JOB();
             w_job.jobID = trackingID;
             w_job.setBusinessCaseSignature(wireCardRequestVO.getCommMerchantVO().getMerchantId());
             w_job.setFnc_cc_reversal(fnc_cc_reversal);

            W_REQUEST w_request = new W_REQUEST();
            w_request.setW_job(w_job);

            WIRECARD_BXML wirecard_bxml = new WIRECARD_BXML();
            wirecard_bxml.setW_request(w_request);

            String response = make_request(wirecard_bxml, username, password);
            W_JOB wJobResponse = getW_job(response);

            ERROR wJobError = wJobResponse.getError();

            if(wJobError != null)
            {
                errMsg = wJobError.getMessage();
                no = wJobError.getNumber();
                type = wJobError.getType();

                commResponseVO.setDescription(errMsg);
                commResponseVO.setDescriptor(errMsg);
                commResponseVO.setErrorCode(no);
                commResponseVO.setStatus("fail");
            }else
            {

                FNC_CC_REVERSAL reversal_response = wJobResponse.getFnc_cc_reversal();
                CC_TRANSACTION cc_transaction_response = reversal_response.getCc_transaction();

                String TransactionID = cc_transaction_response.getTransactionID().toString();

                PROCESSING_STATUS processing_status = cc_transaction_response.getProcessing_status();
                guWid = processing_status.getGuWID();
                result = processing_status.getFunctionResult();
                timeStamp = processing_status.getTimeStamp();
                info = processing_status.getInfo();

                ERROR error = processing_status.getError();

                if(error != null)
                {
                    errMsg = error.getMessage();
                    no = error.getNumber();
                    type = error.getType();
                    advice = error.getAdvice();

                    commResponseVO.setDescription(errMsg);
                    commResponseVO.setDescriptor(advice);
                    commResponseVO.setErrorCode(no);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setResponseTime(timeStamp);
                    commResponseVO.setTransactionId(guWid);
                }
                else
                {
                    commResponseVO.setDescription(result);
                    commResponseVO.setStatus("success");
                    commResponseVO.setResponseTime(timeStamp);
                    commResponseVO.setTransactionId(guWid);
                    commResponseVO.setDescriptor(info);
                }
            }


        return  commResponseVO;
    }

    //public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws SystemError

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZDBViolationException, PZTechnicalViolationException
    {
        String errMsg,type,no,advice,timeStamp,guWid,result,info;
        CommResponseVO commResponseVO = new CommResponseVO();



            WireCardRequestVO wireCardRequestVO = (WireCardRequestVO)requestVO;
            username = wireCardRequestVO.getCommMerchantVO().getMerchantUsername();
            password = wireCardRequestVO.getCommMerchantVO().getPassword();


        CC_TRANSACTION cc_transaction = new CC_TRANSACTION();
        String trasationId = wireCardRequestVO.getTransDetailsVO().getOrderId();

        guWid = get_guid(trasationId);

        //cc_transaction.setTransactionID(wireCardRequestVO.getTransDetailsVO().getOrderId());
        cc_transaction.setGuWID(guWid);

        FNC_CC_QUERY fnc_cc_query = new FNC_CC_QUERY();
        fnc_cc_query.setCc_transaction(cc_transaction);

        W_JOB w_job = new W_JOB();
        w_job.setBusinessCaseSignature(wireCardRequestVO.getCommMerchantVO().getMerchantUsername());
        w_job.setFnc_cc_query(fnc_cc_query);

        W_REQUEST w_request = new W_REQUEST();
        w_request.setW_job(w_job);

        WIRECARD_BXML wirecard_bxml = new WIRECARD_BXML();
        wirecard_bxml.setW_request(w_request);

            String response = make_request(wirecard_bxml, username, password);

             W_JOB wJobResponse = getW_job(response);

            ERROR wJobError = wJobResponse.getError();
            if(wJobError != null)
            {
                errMsg = wJobError.getMessage();
                no = wJobError.getNumber();
                type = wJobError.getType();
            }
            else
            {
                FNC_CC_QUERY query_response = wJobResponse.getFnc_cc_query();
                CC_TRANSACTION cc_transaction_response = query_response.getCc_transaction();

                String TransactionID = cc_transaction_response.getTransactionID().toString();

                PROCESSING_STATUS processing_status = cc_transaction_response.getProcessing_status();
                guWid = processing_status.getGuWID();
                result = processing_status.getFunctionResult();
                timeStamp = processing_status.getTimeStamp();
                info = processing_status.getInfo();

                ERROR error = processing_status.getError();

                if(error != null)
                {
                    errMsg = error.getMessage();
                    no = error.getNumber();
                    type = error.getType();
                    advice = error.getAdvice();
                }
                else
                {
                    commResponseVO.setDescription(info);
                    commResponseVO.setStatus("success");
                    commResponseVO.setResponseTime(timeStamp);
                    commResponseVO.setTransactionId(guWid);
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(info);
                }
            }


       // w_request = get_api_result();
        return  commResponseVO;
    }


    public String make_request(WIRECARD_BXML wirecard_bxml, String username, String password) throws PZTechnicalViolationException
    {
        URL url;
        HttpURLConnection connection = null;

        wirecard_bxml.setXmlns("http://www.w3.org/1999/XMLSchema-instance");
        wirecard_bxml.setXsi("wirecard.xsd");

        StringBuffer response = new StringBuffer();

        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("","com.payment.wirecard.core");

        try
        {
        	//String username = "56501";
			//String password = "TestXAPTER";

			String authString = username + ":" + password;
            byte[] ate = authString.getBytes();

			String authStringEnc = Base64.encode(ate);

            String xmldata = "";
            StringBuilder sb = new StringBuilder("<?xml  version=\"1.0\" encoding=\"UTF-8\"?>");

            StringBuilder xmlOut = sb.append(System.getProperty("line.separator")).append(xstream.toXML(wirecard_bxml));

            String str = xmlOut.toString();
            xmldata = str.replaceAll("__","_");

			url = new URL(webPage);
            try
            {
                connection = (HttpURLConnection)url.openConnection();
                connection.setConnectTimeout(120000);
                connection.setReadTimeout(120000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("WireCardPaymentGateway.java", "make_request()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestProperty("Content-Type","application/xml");
            connection.setRequestProperty("Content-Length",Integer.toString(xmldata.length()));

            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
            connection.getOutputStream ());
            wr.writeBytes (xmldata);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;

            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();

		}
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WireCardPaymentGateway.java","make_request()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WireCardPaymentGateway.java","make_request()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WireCardPaymentGateway.java","make_request()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WireCardPaymentGateway.java", "make_request()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
        }
        finally
        {
            if(connection != null)
            {
                connection.disconnect();
            }
        }
        return  response.toString();

  }

  public W_JOB getW_job(String response)
  {
       W_JOB wJobResponse;

        XStream responseXstream = new XStream();
        responseXstream.autodetectAnnotations(true);
        responseXstream.aliasPackage("","com.payment.wirecard.core");
        responseXstream.alias("",WIRECARD_BXML.class);
        WIRECARD_BXML wirecard_response = (WIRECARD_BXML)responseXstream.fromXML(response);

        W_RESPONSE wResponse = wirecard_response.getW_response();
        wJobResponse = wResponse.getW_job();

        return  wJobResponse;
  }

    private String get_guid(String trackingId)throws PZDBViolationException
    {
        String transactionGuid = "";
        Connection conn = null;
        try
        {

            conn = Database.getConnection();
            String auth_details = "select paymentid from transaction_common where trackingid = " + trackingId + "";
            PreparedStatement authstmt = conn.prepareStatement(auth_details);
            ResultSet rsAuthDetails = authstmt.executeQuery();
            if (rsAuthDetails.next())
            {
                transactionGuid = rsAuthDetails.getString("paymentid");
            }
            else
                transactionGuid = "0";
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("WireCardPaymentGateway.java","get_guid()",null,"common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("WireCardPaymentGateway.java", "get_guid()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null,se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionGuid;
    }

}