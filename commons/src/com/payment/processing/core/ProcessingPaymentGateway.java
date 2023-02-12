package com.payment.processing.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.RecurringManager;
import com.manager.vo.RecurringBillingVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.endeavourmpi.EnrollmentResponseVO;
import com.payment.endeavourmpi.ParesDecodeRequestVO;
import com.payment.endeavourmpi.ParesDecodeResponseVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.processingmpi.ProcessingMPIGateway;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Roshan
 * Date: 2/20/15
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessingPaymentGateway  extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "processing";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.processingdotcom");
    private final static String LIVE_INIT_URL = "";
    private static TransactionLogger transactionLogger =new TransactionLogger(ProcessingPaymentGateway.class.getName());
    private static Functions functions = new Functions();
    private static Map<String,String> cvv2_response = new HashMap();
    private static Map<String,String> responseCode = new HashMap();
    private static Map<String,String> subCode = new HashMap();
    Boolean isTest=false;
    private String url="";
    public ProcessingPaymentGateway(String accountId){
        this.accountId = accountId;
        isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        if(isTest){
            url=RB.getString("TEST_URL");
        }
        else{
            url=RB.getString("LIVE_URL");
        }
    }

    private static String Country(String countryCode){
        if(countryCode.length()>2){
            countryCode=countryCode.substring(0,2);
        }
        return countryCode;
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String indicatorForRecurring="1";
        StringBuffer requestUrl= new StringBuffer();
        StringBuffer requestUrlLog= new StringBuffer();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO) requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = ((CommRequestVO) requestVO).getAddressDetailsVO();
        CommMerchantVO commMerchantVO= ((CommRequestVO) requestVO).getCommMerchantVO();

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommResponseVO commResponseVO = new CommResponseVO();
        String bankApprovedID="";

        String merchantId=gatewayAccount.getMerchantId();
        String midQ=gatewayAccount.getFRAUD_FTP_PATH();
        String userName=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String cbPath = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String _3DSupportAccount=gatewayAccount.get_3DSupportAccount();
        String reject3DCard=((CommRequestVO) requestVO).getReject3DCard();
        String transactionType="Sale";

        try
        {
            if("Y".equals(_3DSupportAccount)){
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(merchantId);
                if (isTest)
                {
                    enrollmentRequestVO.setMid(cbPath);
                }

                enrollmentRequestVO.setAmount(transDetailsVO.getAmount());
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiryYear(cardDetailsVO.getExpYear());
                enrollmentRequestVO.setExpiryMonth(cardDetailsVO.getExpMonth());
                enrollmentRequestVO.setTestRequest(isTest);

                ProcessingMPIGateway processingMPIGateway = new ProcessingMPIGateway();
                EnrollmentResponseVO enrollmentResponseVO = processingMPIGateway.processEnrollment(enrollmentRequestVO);

                if ("Card enrolled".equals(enrollmentResponseVO.getResult()))
                {
                    if ("Y".equals(reject3DCard))
                    {
                        transactionLogger.error("rejecting 3d card as per configuration ");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionId(bankApprovedID);
                        commResponseVO.setDescription("3D Enrolled Card");
                        commResponseVO.setRemark("3D Enrolled Card");
                        return commResponseVO;
                    }
                    else
                    {
                        transactionLogger.error("3D:card enrolled flow");
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String MD = enrollmentResponseVO.getMD();
                        try{
                            acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");
                        }
                        catch (UnsupportedEncodingException e){
                            transactionLogger.error("UnsupportedEncodingException:::::" + e);
                            PZExceptionHandler.raiseTechnicalViolationException(ProcessingPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                        }

                        transactionLogger.error("paReq:::" + PAReq);
                        transactionLogger.error("url:::" + acsUrl);

                        String status = "pending3DConfirmation";

                        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setMd(MD);
                        comm3DResponseVO.setRedirectMethod("POST");
                        comm3DResponseVO.setTerURL(termUrl+trackingID);
                        comm3DResponseVO.setStatus(status);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setTransactionType(transactionType);

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        comm3DResponseVO.setCurrency(transDetailsVO.getCurrency());
                        comm3DResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                        comm3DResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                        return comm3DResponseVO;
                    }
                }
                else{
                    requestUrl.append("account_username=" + userName);
                    requestUrl.append("&account_password=" + password);
                    requestUrl.append("&type=purchase&amount=" + transDetailsVO.getAmount());
                    requestUrl.append("&mid=" + merchantId);
                    requestUrl.append("&mid_q=" + midQ);
                    requestUrl.append("&first_name=" + commAddressDetailsVO.getFirstname());
                    requestUrl.append("&last_name=" + commAddressDetailsVO.getLastname());
                    requestUrl.append("&card_number=" + cardDetailsVO.getCardNum());
                    requestUrl.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
                    requestUrl.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
                    requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
                    requestUrl.append("&ip_address=" + commAddressDetailsVO.getIp());
                    requestUrl.append("&city=" + commAddressDetailsVO.getCity());
                    requestUrl.append("&state=" + commAddressDetailsVO.getState());
                    requestUrl.append("&zip=" + commAddressDetailsVO.getZipCode());
                    requestUrl.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                    requestUrl.append("&phone_number=" + commAddressDetailsVO.getPhone());
                    requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
                    if("Y".equals(gatewayAccount.getIsRecurring())){
                        requestUrl.append("&recurring="+indicatorForRecurring);
                    }
                    requestUrlLog.append("account_username=" + userName);
                    requestUrlLog.append("&account_password=" + password);
                    requestUrlLog.append("&type=purchase&amount=" + transDetailsVO.getAmount());
                    requestUrlLog.append("&mid=" + merchantId);
                    requestUrlLog.append("&mid_q=" + midQ);
                    requestUrlLog.append("&first_name=" + commAddressDetailsVO.getFirstname());
                    requestUrlLog.append("&last_name=" + commAddressDetailsVO.getLastname());
                    requestUrlLog.append("&card_number=" + functions.maskingPan(cardDetailsVO.getCardNum()));
                    requestUrlLog.append("&card_expiry_month=" + functions.maskingNumber(cardDetailsVO.getExpMonth()));
                    requestUrlLog.append("&card_expiry_year=" + functions.maskingNumber(cardDetailsVO.getExpYear()));
                    requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
                    requestUrlLog.append("&ip_address=" + commAddressDetailsVO.getIp());
                    requestUrlLog.append("&city=" + commAddressDetailsVO.getCity());
                    requestUrlLog.append("&state=" + commAddressDetailsVO.getState());
                    requestUrlLog.append("&zip=" + commAddressDetailsVO.getZipCode());
                    requestUrlLog.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                    requestUrlLog.append("&phone_number=" + commAddressDetailsVO.getPhone());
                    requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
                    if("Y".equals(gatewayAccount.getIsRecurring())){
                        requestUrlLog.append("&recurring="+indicatorForRecurring);
                    }

                    transactionLogger.error("sale request url:::::" + url);
                    transactionLogger.error("sale request:::"+trackingID+"::" + requestUrlLog.toString());
                    String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
                    transactionLogger.error("sale response:::"+trackingID+"::" + response);

                    CommResponseVO responseVO=processResponse(response, PZProcessType.SALE.toString());
                    if(responseVO!=null){
                        if("Y".equalsIgnoreCase(gatewayAccount.getIsRecurring())){
                            String cardNumber = cardDetailsVO.getCardNum();
                            String first_six=cardNumber.substring(0,6);
                            String last_four=cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                            RecurringManager recurringManager = new RecurringManager();
                            if("success".equalsIgnoreCase(responseVO.getStatus())){
                                recurringManager.updateRbidForSuccessfullRebill(responseVO.getTransactionId(), first_six, last_four, trackingID);
                            }else{
                                recurringManager.deleteEntryForPFSRebill(trackingID);
                            }
                        }
                    }
                    responseVO.setCurrency(transDetailsVO.getCurrency());
                    responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                    return responseVO;
                }
            }
            else{
                requestUrl.append("account_username=" + userName);
                requestUrl.append("&account_password=" + password);
                requestUrl.append("&type=purchase&amount=" + transDetailsVO.getAmount());
                requestUrl.append("&mid=" + merchantId);
                requestUrl.append("&mid_q=" + midQ);
                requestUrl.append("&first_name=" + commAddressDetailsVO.getFirstname());
                requestUrl.append("&last_name=" + commAddressDetailsVO.getLastname());
                requestUrl.append("&card_number=" + cardDetailsVO.getCardNum());
                requestUrl.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
                requestUrl.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
                requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
                requestUrl.append("&ip_address=" + commAddressDetailsVO.getIp());
                requestUrl.append("&city=" + commAddressDetailsVO.getCity());
                requestUrl.append("&state=" + commAddressDetailsVO.getState());
                requestUrl.append("&zip=" + commAddressDetailsVO.getZipCode());
                requestUrl.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                requestUrl.append("&phone_number=" + commAddressDetailsVO.getPhone());
                requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
                if("Y".equals(gatewayAccount.getIsRecurring())){
                    requestUrl.append("&recurring="+indicatorForRecurring);
                }

                requestUrlLog.append("account_username=" + userName);
                requestUrlLog.append("&account_password=" + password);
                requestUrlLog.append("&type=purchase&amount=" + transDetailsVO.getAmount());
                requestUrlLog.append("&mid=" + merchantId);
                requestUrlLog.append("&mid_q=" + midQ);
                requestUrlLog.append("&first_name=" + commAddressDetailsVO.getFirstname());
                requestUrlLog.append("&last_name=" + commAddressDetailsVO.getLastname());
                requestUrlLog.append("&card_number=" + functions.maskingPan(cardDetailsVO.getCardNum()));
                requestUrlLog.append("&card_expiry_month=" + functions.maskingNumber(cardDetailsVO.getExpMonth()));
                requestUrlLog.append("&card_expiry_year=" + functions.maskingNumber(cardDetailsVO.getExpYear()));
                requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
                requestUrlLog.append("&ip_address=" + commAddressDetailsVO.getIp());
                requestUrlLog.append("&city=" + commAddressDetailsVO.getCity());
                requestUrlLog.append("&state=" + commAddressDetailsVO.getState());
                requestUrlLog.append("&zip=" + commAddressDetailsVO.getZipCode());
                requestUrlLog.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                requestUrlLog.append("&phone_number=" + commAddressDetailsVO.getPhone());
                requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
                if("Y".equals(gatewayAccount.getIsRecurring())){
                    requestUrlLog.append("&recurring="+indicatorForRecurring);
                }

                transactionLogger.error("sale request url:::::" + url);
                transactionLogger.error("sale request:::"+trackingID+"::" + requestUrlLog.toString());
                String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
                transactionLogger.error("sale response:::"+trackingID+"::" + response);

                CommResponseVO responseVO=processResponse(response, PZProcessType.SALE.toString());
                if(responseVO!=null){
                    if("Y".equalsIgnoreCase(gatewayAccount.getIsRecurring())){
                        String cardNumber = cardDetailsVO.getCardNum();
                        String first_six=cardNumber.substring(0,6);
                        String last_four=cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                        RecurringManager recurringManager = new RecurringManager();
                        if("success".equalsIgnoreCase(responseVO.getStatus())){
                            recurringManager.updateRbidForSuccessfullRebill(responseVO.getTransactionId(), first_six, last_four, trackingID);
                        }else{
                            recurringManager.deleteEntryForPFSRebill(trackingID);
                        }
                    }
                }
                responseVO.setCurrency(transDetailsVO.getCurrency());
                responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                return responseVO;
            }
        }
        catch(Exception e){
            PZExceptionHandler.raiseTechnicalViolationException(ProcessingPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            return null;
        }
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        StringBuffer requestUrl= new StringBuffer();
        StringBuffer requestUrlLog= new StringBuffer();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO) requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = ((CommRequestVO) requestVO).getAddressDetailsVO();
        CommMerchantVO commMerchantVO=((CommRequestVO) requestVO).getCommMerchantVO();

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommResponseVO commResponseVO = new CommResponseVO();
        String bankApprovedID="";

        String merchantId=gatewayAccount.getMerchantId();
        String midQ=gatewayAccount.getFRAUD_FTP_PATH();
        String userName=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String cbPath = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String _3DSupportAccount=gatewayAccount.get_3DSupportAccount();
        String reject3DCard=((CommRequestVO) requestVO).getReject3DCard();
        String transactionType="auth";
        try
        {
            if("Y".equals(_3DSupportAccount)){
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(merchantId);
                if (isTest)
                {
                    enrollmentRequestVO.setMid(cbPath);
                }
                enrollmentRequestVO.setAmount(transDetailsVO.getAmount());
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiryYear(cardDetailsVO.getExpYear());
                enrollmentRequestVO.setExpiryMonth(cardDetailsVO.getExpMonth());

                ProcessingMPIGateway processingMPIGateway = new ProcessingMPIGateway();
                EnrollmentResponseVO enrollmentResponseVO = processingMPIGateway.processEnrollment(enrollmentRequestVO);

                if ("Card enrolled".equals(enrollmentResponseVO.getResult()))
                {
                    if ("Y".equals(reject3DCard))
                    {
                        transactionLogger.error("rejecting 3d card as per configuration ");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionId(bankApprovedID);
                        commResponseVO.setDescription("3D Enrolled Card");
                        commResponseVO.setRemark("3D Enrolled Card");
                        return commResponseVO;
                    }
                    else
                    {
                        transactionLogger.error("3D:card enrolled flow");
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String MD = enrollmentResponseVO.getMD();
                        try{
                            acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");
                        }
                        catch (UnsupportedEncodingException e){
                            transactionLogger.error("UnsupportedEncodingException:::::" + e);
                            PZExceptionHandler.raiseTechnicalViolationException(ProcessingPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                        }

                        transactionLogger.error("paReq:::" + PAReq);
                        transactionLogger.error("url:::" + acsUrl);

                        String status = "pending3DConfirmation";

                        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setMd(MD);
                        comm3DResponseVO.setRedirectMethod("POST");
                        comm3DResponseVO.setTerURL(termUrl+ trackingID);
                        comm3DResponseVO.setStatus(status);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setTransactionType(transactionType);
                        comm3DResponseVO.setCurrency(transDetailsVO.getCurrency());
                        comm3DResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                        comm3DResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        return comm3DResponseVO;
                    }
                }
                else{
                    requestUrl.append("account_username=" + userName);
                    requestUrl.append("&account_password=" + password);
                    requestUrl.append("&type=authorization&amount=" + transDetailsVO.getAmount());
                    requestUrl.append("&mid=" + merchantId);
                    requestUrl.append("&mid_q=" + midQ);
                    requestUrl.append("&first_name=" + commAddressDetailsVO.getFirstname());
                    requestUrl.append("&last_name=" + commAddressDetailsVO.getLastname());
                    requestUrl.append("&card_number=" + cardDetailsVO.getCardNum());
                    requestUrl.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
                    requestUrl.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
                    requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
                    requestUrl.append("&ip_address=" + commAddressDetailsVO.getIp());
                    requestUrl.append("&city=" + commAddressDetailsVO.getCity());
                    requestUrl.append("&state=" + commAddressDetailsVO.getState());
                    requestUrl.append("&zip=" + commAddressDetailsVO.getZipCode());
                    requestUrl.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                    requestUrl.append("&phone_number=" + commAddressDetailsVO.getPhone());
                    requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());


                    requestUrlLog.append("account_username=" + userName);
                    requestUrlLog.append("&account_password=" + password);
                    requestUrlLog.append("&type=authorization&amount=" + transDetailsVO.getAmount());
                    requestUrlLog.append("&mid=" + merchantId);
                    requestUrlLog.append("&mid_q=" + midQ);
                    requestUrlLog.append("&first_name=" + commAddressDetailsVO.getFirstname());
                    requestUrlLog.append("&last_name=" + commAddressDetailsVO.getLastname());
                    requestUrlLog.append("&card_number=" + functions.maskingPan(cardDetailsVO.getCardNum()));
                    requestUrlLog.append("&card_expiry_month=" + functions.maskingNumber(cardDetailsVO.getExpMonth()));
                    requestUrlLog.append("&card_expiry_year=" + functions.maskingNumber(cardDetailsVO.getExpYear()));
                    requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
                    requestUrlLog.append("&ip_address=" + commAddressDetailsVO.getIp());
                    requestUrlLog.append("&city=" + commAddressDetailsVO.getCity());
                    requestUrlLog.append("&state=" + commAddressDetailsVO.getState());
                    requestUrlLog.append("&zip=" + commAddressDetailsVO.getZipCode());
                    requestUrlLog.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                    requestUrlLog.append("&phone_number=" + commAddressDetailsVO.getPhone());
                    requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());

                    transactionLogger.error("auth request url:::::" + url);
                    transactionLogger.error("auth request:::"+trackingID+"::" + requestUrlLog.toString());
                    String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
                    transactionLogger.error("auth response:::"+trackingID+"::" + response);
                    return processResponse(response, PZProcessType.AUTHORIZATION.toString());
                }
            }
            else{
                requestUrl.append("account_username=" + userName);
                requestUrl.append("&account_password=" + password);
                requestUrl.append("&type=authorization&amount=" + transDetailsVO.getAmount());
                requestUrl.append("&mid=" + merchantId);
                requestUrl.append("&mid_q=" + midQ);
                requestUrl.append("&first_name=" + commAddressDetailsVO.getFirstname());
                requestUrl.append("&last_name=" + commAddressDetailsVO.getLastname());
                requestUrl.append("&card_number=" + cardDetailsVO.getCardNum());
                requestUrl.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
                requestUrl.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
                requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
                requestUrl.append("&ip_address=" + commAddressDetailsVO.getIp());
                requestUrl.append("&city=" + commAddressDetailsVO.getCity());
                requestUrl.append("&state=" + commAddressDetailsVO.getState());
                requestUrl.append("&zip=" + commAddressDetailsVO.getZipCode());
                requestUrl.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                requestUrl.append("&phone_number=" + commAddressDetailsVO.getPhone());
                requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());

                requestUrlLog.append("account_username=" + userName);
                requestUrlLog.append("&account_password=" + password);
                requestUrlLog.append("&type=authorization&amount=" + transDetailsVO.getAmount());
                requestUrlLog.append("&mid=" + merchantId);
                requestUrlLog.append("&mid_q=" + midQ);
                requestUrlLog.append("&first_name=" + commAddressDetailsVO.getFirstname());
                requestUrlLog.append("&last_name=" + commAddressDetailsVO.getLastname());
                requestUrlLog.append("&card_number=" + functions.maskingPan(cardDetailsVO.getCardNum()));
                requestUrlLog.append("&card_expiry_month=" + functions.maskingNumber(cardDetailsVO.getExpMonth()));
                requestUrlLog.append("&card_expiry_year=" + functions.maskingNumber(cardDetailsVO.getExpYear()));
                requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
                requestUrlLog.append("&ip_address=" + commAddressDetailsVO.getIp());
                requestUrlLog.append("&city=" + commAddressDetailsVO.getCity());
                requestUrlLog.append("&state=" + commAddressDetailsVO.getState());
                requestUrlLog.append("&zip=" + commAddressDetailsVO.getZipCode());
                requestUrlLog.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
                requestUrlLog.append("&phone_number=" + commAddressDetailsVO.getPhone());
                requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());

                transactionLogger.error("auth request url:::::" + url);
                transactionLogger.error("auth request:::"+trackingID+"::" + requestUrlLog.toString());
                String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
                transactionLogger.error("auth response:::"+trackingID+"::" + response);
                return processResponse(response, PZProcessType.AUTHORIZATION.toString());
            }
        }
        catch(Exception e){
            PZExceptionHandler.raiseTechnicalViolationException(ProcessingPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            return null;
        }
    }

    public GenericResponseVO
    processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        StringBuffer requestUrl = new StringBuffer();
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();

        requestUrl.append("account_username=" + GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME());
        requestUrl.append("&account_password=" + GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD());
        requestUrl.append("&type=capture&amount=" + transDetailsVO.getAmount());
        requestUrl.append("&auth_code=" + transDetailsVO.getResponseHashInfo());
        requestUrl.append("&origin=" + transDetailsVO.getPreviousTransactionId());

        transactionLogger.error("capture request url::::::"+ url);
        transactionLogger.error("capture request:::::" + requestUrl.toString());

        String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.error("capture response:::::" + response);
        return processResponse(response,PZProcessType.CAPTURE.toString());
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        StringBuffer requestUrl= new StringBuffer();
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();

        requestUrl.append("account_username="+ GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME());
        requestUrl.append("&account_password="+GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD());
        requestUrl.append("&type=refund&amount="+transDetailsVO.getAmount());
        requestUrl.append("&origin="+transDetailsVO.getPreviousTransactionId());

        transactionLogger.error("refund request url:::::"+url);
        transactionLogger.error("refund request:::::" + requestUrl.toString());

        String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.error("refund response:::::" + response);
        return processResponse(response, PZProcessType.REFUND.toString());
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws  PZTechnicalViolationException
    {
        StringBuffer requestUrl= new StringBuffer();
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();

        requestUrl.append("account_username="+ GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME());
        requestUrl.append("&account_password="+GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD());
        requestUrl.append("&type=void");
        requestUrl.append("&origin="+transDetailsVO.getPreviousTransactionId());

        transactionLogger.error("void request url:::::"+url);
        transactionLogger.debug("void request:::::"+requestUrl.toString());

        String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.debug("void response:::::"+response);
        return processResponse(response, PZProcessType.CANCEL.toString());
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        StringBuffer requestUrl= new StringBuffer();
        CommResponseVO commResponseVO=null;
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        requestUrl.append("account_username=" + gatewayAccount.getFRAUD_FTP_USERNAME());
        requestUrl.append("&account_password="+gatewayAccount.getFRAUD_FTP_PASSWORD());
        requestUrl.append("&type=query");
        requestUrl.append("&origin=" + transDetailsVO.getPreviousTransactionId());
        requestUrl.append("&mid=" + gatewayAccount.getMerchantId());
        requestUrl.append("&mid_q="+gatewayAccount.getFRAUD_FTP_PATH());

        transactionLogger.error("enquiry request url:::::"+url);
        transactionLogger.error("enquiry request:::::" + requestUrl.toString());

        String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.error("enquiry response:::::" + response);

        commResponseVO=processResponseForInquiry(response);
        commResponseVO.setCurrency(gatewayAccount.getCurrency());
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String response = "";

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String userName=account.getFRAUD_FTP_USERNAME();
        String password=account.getFRAUD_FTP_PASSWORD();


        StringBuffer requestUrl= new StringBuffer();

        requestUrl.append("account_username=" + userName);
        requestUrl.append("&account_password=" + password);
        requestUrl.append("&type=recurring&amount=" + genericTransDetailsVO.getAmount());
        requestUrl.append("&origin=" + recurringBillingVO.getRbid());

        transactionLogger.error("rebilling request url:::::" + url);
        transactionLogger.error("rebilling request:::::" + requestUrl.toString());

        response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.error("rebilling response:::::" + response);
        return processResponse(response, PZProcessType.REBILLING.toString());
    }
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processPayout-----");

        String response = "";
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        String userName=account.getFRAUD_FTP_USERNAME();
        String password=account.getFRAUD_FTP_PASSWORD();


        StringBuffer requestUrl= new StringBuffer();

        requestUrl.append("account_username=" + userName);
        requestUrl.append("&account_password=" + password);
        requestUrl.append("&type=payout&amount="+transDetailsVO.getAmount());
        requestUrl.append("&mid=" + gatewayAccount.getMerchantId());
        requestUrl.append("&mid_q="+gatewayAccount.getFRAUD_FTP_PATH());
        requestUrl.append("&origin=" + transDetailsVO.getPreviousTransactionId());

        transactionLogger.error("payout request url:::::" + url);
        transactionLogger.error("payout request:::::" + requestUrl.toString());

        response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.error("payout response:::::" + response);
        return processResponse(response, PZProcessType.PAYOUT.toString());

    }

    private Comm3DResponseVO  processResponse(String response,String type) throws PZTechnicalViolationException
    {
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        String requestParameter[]=response.split("&");

        try{
            for(int i=0;i<requestParameter.length;i++)
            {
                if(functions.isValueNull(requestParameter[i]))
                {
                    String value="";
                    String requestValuePair[] = requestParameter[i].split("=");
                    if(requestValuePair.length==2)
                    {
                        if("transaction_id".equals(requestValuePair[0]))
                        {
                            commResponseVO.setTransactionId(requestValuePair[1]);
                        }
                        else if("customer_id".equals(requestValuePair[0]))
                        {
                            //blank
                        }
                        else if("mid_id".equals(requestValuePair[0]))
                        {
                            commResponseVO.setMerchantId(requestValuePair[1]);
                        }
                        else if("auth_code".equals(requestValuePair[0]))
                        {
                            commResponseVO.setResponseHashInfo(requestValuePair[1]);
                        }
                        else if("cvv2_response".equals(requestValuePair[0]))
                        {
                            if("S".equals(requestValuePair[1]))
                            {
                                commResponseVO.setStatus("success");
                            }
                            else
                            {
                                commResponseVO.setStatus("fail");
                            }
                        }
                        else if("amount".equals(requestValuePair[0]))
                        {
                            commResponseVO.setAmount(requestValuePair[1]);
                        }
                        else if("type".equals(requestValuePair[0]))
                        {
                            commResponseVO.setTransactionType(type);
                        }
                        else if("code".equals(requestValuePair[0]))
                        {
                            commResponseVO.setRemark(responseCode.get(requestValuePair[1]));
                            if (type.equals(PZProcessType.AUTHORIZATION.toString()) || type.equals(PZProcessType.SALE.toString()) || type.equals(PZProcessType.REBILLING.toString()) || type.equals(PZProcessType.PAYOUT.toString()) || type.equals(PZProcessType.REFUND.toString()) || type.equals(PZProcessType.CAPTURE.toString()) || type.equals(PZProcessType.CANCEL.toString()))
                            {
                                if("00".equals(requestValuePair[1]))
                                {
                                    commResponseVO.setStatus("success");
                                }
                                else
                                {
                                    commResponseVO.setStatus("fail");
                                }
                            }
                        }
                        else if("message".equals(requestValuePair[0]))
                        {
                            //blank
                        }
                        else if("sub_code".equals(requestValuePair[0]))
                        {
                            if(requestValuePair.length==2)
                            {
                                commResponseVO.setDescription(subCode.get(requestValuePair[1]));
                                commResponseVO.setDescription("Transaction Successful");
                            }
                            else
                            {
                                commResponseVO.setDescription("Transaction Failed ");
                            }
                        }
                        else if("sub_message".equals(requestValuePair[0]))
                        {
                            if(requestValuePair.length==2)
                            {
                                commResponseVO.setDescription(subCode.get(requestValuePair[1]));
                                commResponseVO.setDescription("Transaction Successful");
                            }
                            else
                            {
                                commResponseVO.setDescription("Transaction Failed ");
                            }
                        }
                    }
                }
            }

            commResponseVO.setStatus(functions.isValueNull(commResponseVO.getStatus())?commResponseVO.getStatus():"fail");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setDescriptor(GATEWAY_TYPE);
        }
        catch(Exception e){
            PZExceptionHandler.raiseTechnicalViolationException(ProcessingPaymentGateway.class.getName(),"processResponse()",null,"common","Null pointer exception while processing response", PZTechnicalExceptionEnum.NULL_POINTER_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return commResponseVO;
    }

    private CommResponseVO  processResponseForInquiry(String response) throws PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();

        String requestParameter[]=response.split("&");

        try{
            for(int i=0;i<requestParameter.length;i++)
            {
                if(functions.isValueNull(requestParameter[i]))
                {
                    String value="";
                    String requestValuePair[] = requestParameter[i].split("=");
                    if(requestValuePair.length==2)
                    {
                        if("transaction_id".equals(requestValuePair[0]))
                        {
                            commResponseVO.setTransactionId(requestValuePair[1]);
                        }
                        else if("customer_id".equals(requestValuePair[0]))
                        {
                            //blank
                        }
                        else if("mid_id".equals(requestValuePair[0]))
                        {
                            commResponseVO.setMerchantId(requestValuePair[1]);
                        }
                        else if("auth_code".equals(requestValuePair[0]))
                        {
                            commResponseVO.setResponseHashInfo(requestValuePair[1]);
                        }

                        else if("transaction_time".equals(requestValuePair[0]))
                        {
                            commResponseVO.setBankTransactionDate(requestValuePair[1]);
                        }
                        else if("amount".equals(requestValuePair[0]))
                        {
                            commResponseVO.setAmount(requestValuePair[1]);
                        }
                        else if("type".equals(requestValuePair[0]))
                        {
                            commResponseVO.setTransactionType(requestValuePair[1]);
                        }
                        else if("code".equals(requestValuePair[0]))
                        {
                            commResponseVO.setRemark(responseCode.get(requestValuePair[1]));
                            if("00".equals(requestValuePair[1]))
                            {
                                commResponseVO.setStatus("success");
                                commResponseVO.setDescription("Transaction Successful");
                            }
                            else
                            {
                                commResponseVO.setStatus("fail");
                                commResponseVO.setDescription("Transaction Failed ");
                            }

                        }
                        else if("message".equals(requestValuePair[0]))
                        {
                            commResponseVO.setTransactionStatus(requestValuePair[1]);
                        }
                        else if("sub_code".equals(requestValuePair[0]))
                        {
                            if(requestValuePair.length==2)
                            {
                                commResponseVO.setDescription(subCode.get(requestValuePair[1]));
                                //commResponseVO.setDescription("Transaction Successful");
                            }
                            else
                            {
                                commResponseVO.setDescription(subCode.get(requestValuePair[1]));
                            }
                        }
                        else if("sub_message".equals(requestValuePair[0]))
                        {
                            if(requestValuePair.length==2)
                            {
                                commResponseVO.setDescription(subCode.get(requestValuePair[1]));
                                //commResponseVO.setDescription("Transaction Successful");
                            }
                            else
                            {
                                commResponseVO.setDescription(subCode.get(requestValuePair[1]));
                            }
                        }
                    }
                }
            }
            commResponseVO.setStatus(functions.isValueNull(commResponseVO.getStatus())?commResponseVO.getStatus():"fail");
            commResponseVO.setDescriptor(GATEWAY_TYPE);
        }
        catch(Exception e){
            PZExceptionHandler.raiseTechnicalViolationException(ProcessingPaymentGateway.class.getName(),"processResponse()",null,"common","Null pointer exception while processing response", PZTechnicalExceptionEnum.NULL_POINTER_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingId, GenericRequestVO requestVO,String transactionType) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException,Exception
    {
        String sourceType="ecom";
        String type="purchase";
        if("auth".equals(transactionType)){
            type="authorization";
        }

        String indicatorForRecurring="1";
        StringBuffer requestUrl= new StringBuffer();
        StringBuffer requestUrlLog= new StringBuffer();
        ProcessingRequestVO processingRequestVO=(ProcessingRequestVO)requestVO;

        CommCardDetailsVO cardDetailsVO = processingRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO transDetailsVO = processingRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = processingRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        String merchantId=gatewayAccount.getMerchantId();
        String midQ=gatewayAccount.getFRAUD_FTP_PATH();
        String userName=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();

        String PaRes = processingRequestVO.getPARes();
        String MD = processingRequestVO.getMD();

        ProcessingMPIGateway processingMPIGateway = new ProcessingMPIGateway();
        ParesDecodeRequestVO paresDecodeRequestVO = new ParesDecodeRequestVO();

        paresDecodeRequestVO.setMassageID(MD);
        paresDecodeRequestVO.setPares(PaRes);
        paresDecodeRequestVO.setTestRequest(isTest);

        ParesDecodeResponseVO paresDecodeResponseVO = processingMPIGateway.processParesDecode(paresDecodeRequestVO);
        String eci=paresDecodeResponseVO.getEci();

        transactionLogger.error("isTest:::::::"+isTest);
        transactionLogger.error("cavv:::::::"+ paresDecodeResponseVO.getCavv());
        transactionLogger.error("eci::::::::"+ paresDecodeResponseVO.getEci());
        transactionLogger.error("xid::::::::"+ paresDecodeResponseVO.getXid());
        transactionLogger.error("secure_hash::"+paresDecodeResponseVO.getSignature());
        transactionLogger.error("source_type::"+sourceType);

        requestUrl.append("account_username=" + userName);
        requestUrl.append("&account_password=" + password);
        requestUrl.append("&type="+type+"&amount=" + transDetailsVO.getAmount());
        requestUrl.append("&mid=" + merchantId);
        requestUrl.append("&mid_q=" + midQ);
        requestUrl.append("&first_name=" + commAddressDetailsVO.getFirstname());
        requestUrl.append("&last_name=" + commAddressDetailsVO.getLastname());
        requestUrl.append("&card_number=" + cardDetailsVO.getCardNum());
        requestUrl.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
        requestUrl.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
        requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
        requestUrl.append("&ip_address=" + commAddressDetailsVO.getIp());
        requestUrl.append("&city=" + commAddressDetailsVO.getCity());
        requestUrl.append("&state=" + commAddressDetailsVO.getState());
        requestUrl.append("&zip=" + commAddressDetailsVO.getZipCode());
        requestUrl.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
        requestUrl.append("&phone_number=" + commAddressDetailsVO.getPhone());
        requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
        if("sale".equals(transactionType)  && "Y".equals(gatewayAccount.getIsRecurring())){
            requestUrl.append("&recurring="+indicatorForRecurring);
        }
        requestUrl.append("&cavv=" + paresDecodeResponseVO.getCavv());
        requestUrl.append("&xid=" + paresDecodeResponseVO.getXid());
        requestUrl.append("&eci=" + paresDecodeResponseVO.getEci());
        requestUrl.append("&secure_hash=" + paresDecodeResponseVO.getSignature());
        requestUrl.append("&source_type=" + sourceType);

        requestUrlLog.append("account_username=" + userName);
        requestUrlLog.append("&account_password=" + password);
        requestUrlLog.append("&type="+type+"&amount=" + transDetailsVO.getAmount());
        requestUrlLog.append("&mid=" + merchantId);
        requestUrlLog.append("&mid_q=" + midQ);
        requestUrlLog.append("&first_name=" + commAddressDetailsVO.getFirstname());
        requestUrlLog.append("&last_name=" + commAddressDetailsVO.getLastname());
        requestUrlLog.append("&card_number=" + functions.maskingPan(cardDetailsVO.getCardNum()));
        requestUrlLog.append("&card_expiry_month=" + functions.maskingNumber(cardDetailsVO.getExpMonth()));
        requestUrlLog.append("&card_expiry_year=" + functions.maskingNumber(cardDetailsVO.getExpYear()));
        requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
        requestUrlLog.append("&ip_address=" + commAddressDetailsVO.getIp());
        requestUrlLog.append("&city=" + commAddressDetailsVO.getCity());
        requestUrlLog.append("&state=" + commAddressDetailsVO.getState());
        requestUrlLog.append("&zip=" + commAddressDetailsVO.getZipCode());
        requestUrlLog.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
        requestUrlLog.append("&phone_number=" + commAddressDetailsVO.getPhone());
        requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
        if("sale".equals(transactionType)  && "Y".equals(gatewayAccount.getIsRecurring())){
            requestUrlLog.append("&recurring="+indicatorForRecurring);
        }
        requestUrlLog.append("&cavv=" + paresDecodeResponseVO.getCavv());
        requestUrlLog.append("&xid=" + paresDecodeResponseVO.getXid());
        requestUrlLog.append("&eci=" + paresDecodeResponseVO.getEci());
        requestUrlLog.append("&secure_hash=" + paresDecodeResponseVO.getSignature());
        requestUrlLog.append("&source_type=" + sourceType);

        transactionLogger.error(transactionType+" request url:::::"+url);
        transactionLogger.error(transactionType+" request:::"+trackingId+"::" + requestUrlLog.toString());
        String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.error(transactionType+" response:::"+trackingId+"::" + response);
        if("sale".equals(transactionType)){
            CommResponseVO responseVO=processResponse(response, PZProcessType.SALE.toString());
            responseVO.setEci(eci);
            if(responseVO!=null){
                if("Y".equalsIgnoreCase(gatewayAccount.getIsRecurring())){
                    String cardNumber = cardDetailsVO.getCardNum();
                    String first_six=cardNumber.substring(0,6);
                    String last_four=cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                    RecurringManager recurringManager = new RecurringManager();
                    if("success".equalsIgnoreCase(responseVO.getStatus())){
                        recurringManager.updateRbidForSuccessfullRebill(responseVO.getTransactionId(), first_six, last_four, trackingId);
                    }else{
                        recurringManager.deleteEntryForPFSRebill(trackingId);
                    }
                }
            }
            responseVO.setCurrency(transDetailsVO.getCurrency());
            responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
            return responseVO;
        }
        else{
            return processResponse(response, PZProcessType.AUTHORIZATION.toString());
        }
    }

    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
            String sourceType = "ecom";
            String type = "purchase";
            String transactionType = "sale";
            String indicatorForRecurring = "1";
            StringBuffer requestUrl = new StringBuffer();
            StringBuffer requestUrlLog = new StringBuffer();
            Comm3DRequestVO processingRequestVO = (Comm3DRequestVO) requestVO;

            CommCardDetailsVO cardDetailsVO = processingRequestVO.getCardDetailsVO();
            CommTransactionDetailsVO transDetailsVO = processingRequestVO.getTransDetailsVO();
            CommAddressDetailsVO commAddressDetailsVO = processingRequestVO.getAddressDetailsVO();

            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

            String merchantId = gatewayAccount.getMerchantId();
            String midQ = gatewayAccount.getFRAUD_FTP_PATH();
            String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
            String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

            String PaRes = processingRequestVO.getPaRes();
            String MD = processingRequestVO.getMd();

            ProcessingMPIGateway processingMPIGateway = new ProcessingMPIGateway();
            ParesDecodeRequestVO paresDecodeRequestVO = new ParesDecodeRequestVO();

            paresDecodeRequestVO.setMassageID(MD);
            paresDecodeRequestVO.setPares(PaRes);
            paresDecodeRequestVO.setTestRequest(isTest);

        ParesDecodeResponseVO paresDecodeResponseVO=null;
        try
        {
            paresDecodeResponseVO = processingMPIGateway.processParesDecode(paresDecodeRequestVO);
        }catch (Exception e){
            transactionLogger.error("Exception------",e);
        }
            String eci = paresDecodeResponseVO.getEci();

            transactionLogger.error("isTest:::::::" + isTest);
            transactionLogger.error("cavv:::::::" + paresDecodeResponseVO.getCavv());
            transactionLogger.error("eci::::::::" + paresDecodeResponseVO.getEci());
            transactionLogger.error("xid::::::::" + paresDecodeResponseVO.getXid());
            transactionLogger.error("secure_hash::" + paresDecodeResponseVO.getSignature());
            transactionLogger.error("source_type::" + sourceType);

            requestUrl.append("account_username=" + userName);
            requestUrl.append("&account_password=" + password);
            requestUrl.append("&type=" + type + "&amount=" + transDetailsVO.getAmount());
            requestUrl.append("&mid=" + merchantId);
            requestUrl.append("&mid_q=" + midQ);
            requestUrl.append("&first_name=" + commAddressDetailsVO.getFirstname());
            requestUrl.append("&last_name=" + commAddressDetailsVO.getLastname());
            requestUrl.append("&card_number=" + cardDetailsVO.getCardNum());
            requestUrl.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
            requestUrl.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
            requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
            requestUrl.append("&ip_address=" + commAddressDetailsVO.getCardHolderIpAddress());
            requestUrl.append("&city=" + commAddressDetailsVO.getCity());
            requestUrl.append("&state=" + commAddressDetailsVO.getState());
            requestUrl.append("&zip=" + commAddressDetailsVO.getZipCode());
            requestUrl.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
            requestUrl.append("&phone_number=" + commAddressDetailsVO.getPhone());
            requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
            if ("sale".equals(transactionType) && "Y".equals(gatewayAccount.getIsRecurring()))
            {
                requestUrl.append("&recurring=" + indicatorForRecurring);
            }
            requestUrl.append("&cavv=" + paresDecodeResponseVO.getCavv());
            requestUrl.append("&xid=" + paresDecodeResponseVO.getXid());
            requestUrl.append("&eci=" + paresDecodeResponseVO.getEci());
            requestUrl.append("&secure_hash=" + paresDecodeResponseVO.getSignature());
            requestUrl.append("&source_type=" + sourceType);

            requestUrlLog.append("account_username=" + userName);
            requestUrlLog.append("&account_password=" + password);
            requestUrlLog.append("&type=" + type + "&amount=" + transDetailsVO.getAmount());
            requestUrlLog.append("&mid=" + merchantId);
            requestUrlLog.append("&mid_q=" + midQ);
            requestUrlLog.append("&first_name=" + commAddressDetailsVO.getFirstname());
            requestUrlLog.append("&last_name=" + commAddressDetailsVO.getLastname());
            requestUrlLog.append("&card_number=" + cardDetailsVO.getCardNum());
            requestUrlLog.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
            requestUrlLog.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
            requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
            requestUrlLog.append("&ip_address=" + commAddressDetailsVO.getCardHolderIpAddress());
            requestUrlLog.append("&city=" + commAddressDetailsVO.getCity());
            requestUrlLog.append("&state=" + commAddressDetailsVO.getState());
            requestUrlLog.append("&zip=" + commAddressDetailsVO.getZipCode());
            requestUrlLog.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
            requestUrlLog.append("&phone_number=" + commAddressDetailsVO.getPhone());
            requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
            if ("sale".equals(transactionType) && "Y".equals(gatewayAccount.getIsRecurring()))
            {
                requestUrlLog.append("&recurring=" + indicatorForRecurring);
            }
            requestUrlLog.append("&cavv=" + paresDecodeResponseVO.getCavv());
            requestUrlLog.append("&xid=" + paresDecodeResponseVO.getXid());
            requestUrlLog.append("&eci=" + paresDecodeResponseVO.getEci());
            requestUrlLog.append("&secure_hash=" + paresDecodeResponseVO.getSignature());
            requestUrlLog.append("&source_type=" + sourceType);

            transactionLogger.error(transactionType + " request url:::::" + url);
            transactionLogger.error(transactionType + " request:::"+trackingId+"::" + requestUrlLog.toString());
            String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
            transactionLogger.error(transactionType + " response:::"+trackingId+"::" + response);
            if ("sale".equals(transactionType))
            {
                Comm3DResponseVO responseVO = processResponse(response, PZProcessType.SALE.toString());
                responseVO.setEci(eci);
                if (responseVO != null)
                {
                    if ("Y".equalsIgnoreCase(gatewayAccount.getIsRecurring()))
                    {
                        String cardNumber = cardDetailsVO.getCardNum();
                        String first_six = cardNumber.substring(0, 6);
                        String last_four = cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                        RecurringManager recurringManager = new RecurringManager();
                        if ("success".equalsIgnoreCase(responseVO.getStatus()))
                        {
                            recurringManager.updateRbidForSuccessfullRebill(responseVO.getTransactionId(), first_six, last_four, trackingId);
                        }
                        else
                        {
                            recurringManager.deleteEntryForPFSRebill(trackingId);
                        }
                    }
                }
                responseVO.setCurrency(transDetailsVO.getCurrency());
                responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                return responseVO;
            }
            else
            {
                return processResponse(response, PZProcessType.AUTHORIZATION.toString());
            }

    }

    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        String sourceType="ecom";
        String type="authorization";
        String transactionType="auth";
        String indicatorForRecurring="1";
        StringBuffer requestUrl= new StringBuffer();
        StringBuffer requestUrlLog= new StringBuffer();
        Comm3DRequestVO processingRequestVO=(Comm3DRequestVO)requestVO;

        CommCardDetailsVO cardDetailsVO = processingRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO transDetailsVO = processingRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = processingRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        String merchantId=gatewayAccount.getMerchantId();
        String midQ=gatewayAccount.getFRAUD_FTP_PATH();
        String userName=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();

        String PaRes = processingRequestVO.getPaRes();
        String MD = processingRequestVO.getMd();

        ProcessingMPIGateway processingMPIGateway = new ProcessingMPIGateway();
        ParesDecodeRequestVO paresDecodeRequestVO = new ParesDecodeRequestVO();

        paresDecodeRequestVO.setMassageID(MD);
        paresDecodeRequestVO.setPares(PaRes);
        paresDecodeRequestVO.setTestRequest(isTest);

        ParesDecodeResponseVO paresDecodeResponseVO=null;
        try{
            paresDecodeResponseVO = processingMPIGateway.processParesDecode(paresDecodeRequestVO);
        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }

        String eci=paresDecodeResponseVO.getEci();

        transactionLogger.error("isTest:::::::"+isTest);
        transactionLogger.error("cavv:::::::"+ paresDecodeResponseVO.getCavv());
        transactionLogger.error("eci::::::::"+ paresDecodeResponseVO.getEci());
        transactionLogger.error("xid::::::::"+ paresDecodeResponseVO.getXid());
        transactionLogger.error("secure_hash::"+paresDecodeResponseVO.getSignature());
        transactionLogger.error("source_type::"+sourceType);

        requestUrl.append("account_username=" + userName);
        requestUrl.append("&account_password=" + password);
        requestUrl.append("&type="+type+"&amount=" + transDetailsVO.getAmount());
        requestUrl.append("&mid=" + merchantId);
        requestUrl.append("&mid_q=" + midQ);
        requestUrl.append("&first_name=" + commAddressDetailsVO.getFirstname());
        requestUrl.append("&last_name=" + commAddressDetailsVO.getLastname());
        requestUrl.append("&card_number=" + cardDetailsVO.getCardNum());
        requestUrl.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
        requestUrl.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
        requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
        requestUrl.append("&ip_address=" + commAddressDetailsVO.getCardHolderIpAddress());
        requestUrl.append("&city=" + commAddressDetailsVO.getCity());
        requestUrl.append("&state=" + commAddressDetailsVO.getState());
        requestUrl.append("&zip=" + commAddressDetailsVO.getZipCode());
        requestUrl.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
        requestUrl.append("&phone_number=" + commAddressDetailsVO.getPhone());
        requestUrl.append("&email_address=" + commAddressDetailsVO.getEmail());
        if("sale".equals(transactionType)  && "Y".equals(gatewayAccount.getIsRecurring())){
            requestUrl.append("&recurring="+indicatorForRecurring);
        }
        requestUrl.append("&cavv=" + paresDecodeResponseVO.getCavv());
        requestUrl.append("&xid=" + paresDecodeResponseVO.getXid());
        requestUrl.append("&eci=" + paresDecodeResponseVO.getEci());
        requestUrl.append("&secure_hash=" + paresDecodeResponseVO.getSignature());
        requestUrl.append("&source_type=" + sourceType);

        requestUrlLog.append("account_username=" + userName);
        requestUrlLog.append("&account_password=" + password);
        requestUrlLog.append("&type="+type+"&amount=" + transDetailsVO.getAmount());
        requestUrlLog.append("&mid=" + merchantId);
        requestUrlLog.append("&mid_q=" + midQ);
        requestUrlLog.append("&first_name=" + commAddressDetailsVO.getFirstname());
        requestUrlLog.append("&last_name=" + commAddressDetailsVO.getLastname());
        requestUrlLog.append("&card_number=" + cardDetailsVO.getCardNum());
        requestUrlLog.append("&card_expiry_month=" + cardDetailsVO.getExpMonth());
        requestUrlLog.append("&card_expiry_year=" + cardDetailsVO.getExpYear());
        requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
        requestUrlLog.append("&ip_address=" + commAddressDetailsVO.getCardHolderIpAddress());
        requestUrlLog.append("&city=" + commAddressDetailsVO.getCity());
        requestUrlLog.append("&state=" + commAddressDetailsVO.getState());
        requestUrlLog.append("&zip=" + commAddressDetailsVO.getZipCode());
        requestUrlLog.append("&country=" + ProcessingPaymentGateway.Country(commAddressDetailsVO.getCountry()));
        requestUrlLog.append("&phone_number=" + commAddressDetailsVO.getPhone());
        requestUrlLog.append("&email_address=" + commAddressDetailsVO.getEmail());
        if("sale".equals(transactionType)  && "Y".equals(gatewayAccount.getIsRecurring())){
            requestUrlLog.append("&recurring="+indicatorForRecurring);
        }
        requestUrlLog.append("&cavv=" + paresDecodeResponseVO.getCavv());
        requestUrlLog.append("&xid=" + paresDecodeResponseVO.getXid());
        requestUrlLog.append("&eci=" + paresDecodeResponseVO.getEci());
        requestUrlLog.append("&secure_hash=" + paresDecodeResponseVO.getSignature());
        requestUrlLog.append("&source_type=" + sourceType);

        transactionLogger.error(transactionType+" request url:::::"+url);
        transactionLogger.error(transactionType+" request:::"+trackingId+"::" + requestUrlLog.toString());
        String response = ProcessingUtils.doPostHTTPSURLConnection(url, requestUrl.toString());
        transactionLogger.error(transactionType+" response:::"+trackingId+"::" + response);
        if("sale".equals(transactionType)){
            Comm3DResponseVO responseVO= processResponse(response, PZProcessType.SALE.toString());
            responseVO.setEci(eci);
            if(responseVO!=null){
                if("Y".equalsIgnoreCase(gatewayAccount.getIsRecurring())){
                    String cardNumber = cardDetailsVO.getCardNum();
                    String first_six=cardNumber.substring(0,6);
                    String last_four=cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                    RecurringManager recurringManager = new RecurringManager();
                    if("success".equalsIgnoreCase(responseVO.getStatus())){
                        recurringManager.updateRbidForSuccessfullRebill(responseVO.getTransactionId(), first_six, last_four, trackingId);
                    }else{
                        recurringManager.deleteEntryForPFSRebill(trackingId);
                    }
                }
            }
            responseVO.setCurrency(transDetailsVO.getCurrency());
            responseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            responseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
            return responseVO;
        }
        else{
            return processResponse(response, PZProcessType.AUTHORIZATION.toString());
        }
    }

    static
    {
        cvv2_response.put("S","Success");
        cvv2_response.put("D","Decline");
        cvv2_response.put("E","Error");
        cvv2_response.put("O","Omitted");

        responseCode.put("00","Transaction Successful");
        responseCode.put("01","Bad Account Information");
        responseCode.put("02","Bad MID Identifier");
        responseCode.put("03","Bad Account Information");
        responseCode.put("04","Bad Account Information");
        responseCode.put("11","Unrecognized Transaction Type");
        responseCode.put("12","Transaction Amount of Bounds");
        responseCode.put("21","Bad Value for Use Gateway ID");
        responseCode.put("22","Bad Value for Recurring Flag");
        responseCode.put("2X","There was a Stage 2 Communication Error");
        responseCode.put("31","Bad Original Transaction ID");
        responseCode.put("32","Invalid Authorization Code");
        responseCode.put("3X","There was a Stage 3 Communication Error");
        responseCode.put("41","Bad or Invalid Customer Information");
        responseCode.put("42","Bad or Invalid Customer Information");
        responseCode.put("43","Bad or Invalid Customer Phone Number");
        responseCode.put("44","Bad or Invalid Customer Address");
        responseCode.put("45","Bad or Invalid Customer Address");
        responseCode.put("46","Bad or Invalid Customer Address");
        responseCode.put("47","Bad or Invalid Customer Address");
        responseCode.put("48","Bad or Invalid Customer Address ");
        responseCode.put("49","Bad or Invalid Country Association");
        responseCode.put("4X","There was a Stage 4 Communication Error");
        responseCode.put("5X","Request Timeout");
        responseCode.put("51","Bad or Invalid Email Address value");
        responseCode.put("52","Bad or Invalid IP Address value");
        responseCode.put("61","Bad or Invalid CC Number Passed");
        responseCode.put("62","Bad, Invalid, or Expired CC Expire Info");
        responseCode.put("63","Bad, Invalid, or Expired CC Expire Info");
        responseCode.put("64","Bad or Invalid CVV2 Information");
        responseCode.put("65","Card Type Not Supported for this MID");
        responseCode.put("71","Passed Gateway Transaction ID Not Found");
        responseCode.put("72","Bad or Invalid Custom Field Value");
        responseCode.put("73","Bad or Invalid Custom Field Value");
        responseCode.put("74","Bad or Invalid Custom Field Value");
        responseCode.put("75","Bad or Invalid Custom Field Value");
        responseCode.put("99","Transaction Declined by Bank");
        responseCode.put("A1","Authorization Attempt Failure");
        responseCode.put("AE","Authorization Attempt Failure");
        responseCode.put("AX","Authorization Attempt Failure");
        responseCode.put("B1","Reversal Cannot Be Issued on Requested Transaction ID");
        responseCode.put("B2","Reversal Cannot Be Issued on Requested Transaction ID due to it’s status ");
        responseCode.put("B3","Reversal Cannot be Issued on Requested Transaction ID due to it’s type");
        responseCode.put("B4","Reversal Cannot be Issued on Requested Transaction ID due to previous actions");
        responseCode.put("BA","Transaction Cannot Complete Due to Blacklist");
        responseCode.put("BB","Transaction Cannot Complete Due to Blacklist");
        responseCode.put("BC","Transaction Cannot Complete Due to Blacklist");
        responseCode.put("BE","Transaction Cannot Complete Due to Blacklist");
        responseCode.put("BI","Transaction Cannot Complete Due to Blacklist");
        responseCode.put("BP","Transaction Cannot Complete Due to Blacklist");
        responseCode.put("C0","Capture Cannot be Issued on Authorization for Requested Amount");
        responseCode.put("CP","Capture cannot be issued on Authorization in Current S tate");
        responseCode.put("CX","Capture Cannot be Issued Due to Admin Blocks");
        responseCode.put("F1","Card Transaction Limit");
        responseCode.put("F2","Bad ticket range");
        responseCode.put("F3","Bad ticket amt");
        responseCode.put("F5","IP Velocity check");
        responseCode.put("F6","BIN overuse");
        responseCode.put("F7","Card Velocity check");
        responseCode.put("OF","Bad Transaction Request Process");
        responseCode.put("OI","Account Lookup Error");
        responseCode.put("R1","Refund Cannot be Issued as Requested");
        responseCode.put("R2","Refund Cannot be Issued as Requested");
        responseCode.put("R3","Refund Cannot be Issued as Requested");
        responseCode.put("TV","The transaction volume has exceeded the set caps.");
        responseCode.put("V1","Void Cannot Be Issued on requested Transaction ID due to previous actions");
        responseCode.put("V2","Void Cannot Be Issued on requested Transaction ID due to it’s status");
        responseCode.put("V3","Void Cannot Be Issued on Transaction of this type");
        responseCode.put("V4","Void Cannot Be Issued on Transaction ID due to previous actions");
        responseCode.put("XX","Malformed API request");

        subCode.put("00","SUCCESS");
        subCode.put("01","Refer to issuer");
        subCode.put("03","Error- Security Restrictions Security Restrictions-Call Help");
        subCode.put("04","Pick up card");
        subCode.put("05","Do not honor");
        subCode.put("06","Error");
        subCode.put("07","Pick up card (special)");
        subCode.put("10","Approved for partial amount");
        subCode.put("12","Invalid transaction");
        subCode.put("13","Invalid amount");
        subCode.put("14","Card number does not exist");
        subCode.put("30","Format error");
        subCode.put("31","Invalid Issuer");
        subCode.put("41","Pick up card (lost card)");
        subCode.put("43","Pick up card (stolen card)");
        subCode.put("51","Not sufficient funds");
        subCode.put("54","Expired card");
        subCode.put("55","Incorrect PIN");
        subCode.put("57","Transaction not permitted to card");
        subCode.put("58","Transaction not permitted to terminal");
        subCode.put("78","Previous message not found");
        subCode.put("79","Invalid CVV2");
        subCode.put("91","Issuer or switch inoperative");
        subCode.put("94","Duplicate transmission");
        subCode.put("95","Reconcile error");
        subCode.put("96","System malfunction");
    }
}