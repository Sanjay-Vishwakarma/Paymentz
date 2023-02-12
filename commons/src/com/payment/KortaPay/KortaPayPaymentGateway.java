package com.payment.KortaPay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.google.gson.Gson;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.endeavourmpi.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 04-Jan-19.
 */
public class KortaPayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "KortaPay";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.KortaPay");
    private static TransactionLogger transactionLogger = new TransactionLogger(KortaPayPaymentGateway.class.getName());
    private static Logger log = new Logger(KortaPayPaymentGateway.class.getName());

    public KortaPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("---Entering into processSale---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Gson gson= new Gson();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String terminalNumber = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String mpiMid=GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();
        String is3DSupported=GatewayAccountService.getGatewayAccount(accountId).get_3DSupportAccount();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMddhhmmss");
        LocalDateTime now = LocalDateTime.now();
        dtf.format(now);

        boolean isTest = gatewayAccount.isTest();
        String termUrl="";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String hostUrl="";
        String ip="";
        transactionLogger.debug("host url---------"+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
        }
        else{
            termUrl = RB.getString("TERM_URL");
        }
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
            ip=addressDetailsVO.getCardHolderIpAddress();
        }else {
            ip=addressDetailsVO.getIp();
        }
        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String amount= KortaPayUtils.getCentAmount(transDetailsVO.getAmount());
        String status ="";

        try
        {
            if("Y".equals(is3DSupported) || "O".equals(is3DSupported))
            {
                transactionLogger.error("---inside is3DSupported---");
                EndeavourMPIGateway endeavourMPIGateway= new EndeavourMPIGateway();
                EnrollmentRequestVO enrollmentRequestVO= new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(),cardDetailsVO.getExpMonth()));
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(KortaPayUtils.getCentAmount(transDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(trackingID);
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);

                transactionLogger.error("---Enrollment Request---"+trackingID + "--" + gson.toJson(enrollmentRequestVO));

                EnrollmentResponseVO enrollmentResponseVO=endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                transactionLogger.error("---Enrollment Response---"+trackingID + "--" + gson.toJson(enrollmentResponseVO));

                if(enrollmentResponseVO!=null)
                {
                    if(enrollmentResponseVO.getResult().equalsIgnoreCase("Enrolled") && enrollmentResponseVO.getAvr().equalsIgnoreCase("Y"))
                    {
                        String acsUrl= java.net.URLDecoder.decode(enrollmentResponseVO.getAcsUrl(), "UTF-8");
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setPaReq(enrollmentResponseVO.getPAReq());
                        commResponseVO.setUrlFor3DRedirect(acsUrl);
                        commResponseVO.setTerURL(termUrl + trackingID);
                        commResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                    }
                    else if(enrollmentResponseVO.getAvr().equalsIgnoreCase("U") || enrollmentResponseVO.getAvr().equalsIgnoreCase("N"))
                    {
                        if ("O".equals(is3dSupported))
                        {
                            transactionLogger.error("rejecting 3d card as per configuration");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("SYS:Only 3D card Supported");
                            commResponseVO.setRemark("Only 3D card Supported");
                            return commResponseVO;
                        }
                        transactionLogger.error("inside 3D but non 3D Way");
                        String saleRequest = "" +
                                "user=" + userName +
                                "&pwd=" + password +
                                "&site=13" +
                                "&d41=" + terminalNumber +
                                "&d42=" + mId +
                              //  "&d3=000000" +
                                "&capture=True" +
                                "&d31=" + trackingID +
                                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +
                                "&d2=" + cardDetailsVO.getCardNum() +
                                "&d14=" +KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(),cardDetailsVO.getExpMonth()) +
                                "&d47=" + cardDetailsVO.getcVV() +
                                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +
                                "&d12=" + dtf.format(now) + //Date and local time for Transaction.
                                "&d4=" + amount +
                                "&de4=2" +
                                "&d49=" + currencyId;
                        String saleRequestlog = "" +
                                "user=" + userName +
                                "&pwd=" + password +
                                "&site=13" +
                                "&d41=" + terminalNumber +
                                "&d42=" + mId +
                                //  "&d3=000000" +
                                "&capture=True" +
                                "&d31=" + trackingID +
                                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +
                                "&d2=" + functions.maskingPan(cardDetailsVO.getCardNum()) +
                                "&d14=" +functions.maskingExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(),cardDetailsVO.getExpMonth())) +
                                "&d47=" + functions.maskingNumber(cardDetailsVO.getcVV()) +
                                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +
                                "&d12=" + dtf.format(now) + //Date and local time for Transaction.
                                "&d4=" + amount +
                                "&de4=2" +
                                "&d49=" + currencyId;

                        transactionLogger.error("-----sale request-----" +trackingID + "--" +  saleRequestlog);

                        String saleResponse = "";
                        if (isTest)
                        {
                            transactionLogger.error("inside isTest-----" + RB.getString("TEST_SALE_URL"));
                            saleResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_SALE_URL"), saleRequest, "BASIC", credentials);
                        }
                        else
                        {
                            transactionLogger.error("inside isLive-----" + RB.getString("LIVE_SALE_URL"));
                            saleResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_SALE_URL"), saleRequest, "BASIC", credentials);
                        }
                        transactionLogger.error("-----sale response-----" +trackingID + "--" +  saleResponse);
                        if (functions.isValueNull(saleResponse))
                        {
                            Map<String, String> responseMap = KortaPayUtils.getQueryMap(saleResponse);
                            if (responseMap != null)
                            {
                                String responseCode = responseMap.get("d39");
                                String user = responseMap.get("user");
                                String site = responseMap.get("site");
                                String processingCode = responseMap.get("d3");
                                String responseAmount = responseMap.get("d4");
                                String amountExponent = responseMap.get("de4");
                                String responseTime = responseMap.get("d12");
                                String responseOrderId = responseMap.get("d31");
                                String settlementRefNo = responseMap.get("d37");
                                String authorisationCode = responseMap.get("d38");
                                String uniqueResponseTransId = responseMap.get("d56");
                                String error = responseMap.get("error");
                                String errortext = responseMap.get("errortext");
                                String d39text = responseMap.get("d39text");

                                if (responseCode.equals("000"))
                                {
                                    status = "success";
                                    commResponseVO.setDescription(d39text);
                                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                }
                                else
                                {
                                    status = "fail";
                                    commResponseVO.setDescription("errorcode:" + error + ":" + errortext + ":" + d39text);
                                }
                                commResponseVO.setAmount(responseAmount);
                                commResponseVO.setAuthCode(authorisationCode);
                                commResponseVO.setBankTransactionDate(responseTime);
                               // commResponseVO.setResponseTime(responseTime);
                                commResponseVO.setCurrency(transDetailsVO.getCurrency());
                                commResponseVO.setIpaddress(ip);
                                commResponseVO.setRemark(d39text);
                                commResponseVO.setStatus(status);
                                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                                commResponseVO.setTransactionId(uniqueResponseTransId);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setCurrency(currency);
                            }
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                    }
                }
            }
            else
            {
                transactionLogger.error("inside non 3D sale");
                String saleRequest = "" +
                        "user=" + userName +
                        "&pwd=" + password +
                        "&site=13" +
                        "&d41=" + terminalNumber +  //Terminal number. Provided by KORTA.
                        "&d42=" + mId +           //Your agreement number. Provided by KORTA
                       // "&d3=000000" +
                        "&capture=True" +          // *True for sale or direct capture*   *False for Auth*
                        "&d31=" + trackingID +    //Your order number, provided by you. Currently max. 15
                        "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +           //Card Holder Name
                        "&d2=" + cardDetailsVO.getCardNum() +   //card number
                        "&d14=" +KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth()) +               //Expiry
                        "&d47=" + cardDetailsVO.getcVV() +                // cvc
                        "&cip=" + addressDetailsVO.getCardHolderIpAddress() +           // Ip
                        "&d12=" + dtf.format(now) + //Date and local time for Transaction.
                        "&d4=" + amount +             //amount
                        "&de4=2" +              //amount decimal  0 or 2
                        "&d49=" + currencyId;               //Currency Code.
                String saleRequestlog = "" +
                        "user=" + userName +
                        "&pwd=" + password +
                        "&site=13" +
                        "&d41=" + terminalNumber +  //Terminal number. Provided by KORTA.
                        "&d42=" + mId +           //Your agreement number. Provided by KORTA
                        // "&d3=000000" +
                        "&capture=True" +          // *True for sale or direct capture*   *False for Auth*
                        "&d31=" + trackingID +    //Your order number, provided by you. Currently max. 15
                        "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +           //Card Holder Name
                        "&d2=" + functions.maskingPan(cardDetailsVO.getCardNum()) +   //card number
                        "&d14=" +functions.maskingExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth())) +               //Expiry
                        "&d47=" + functions.maskingNumber(cardDetailsVO.getcVV()) +                // cvc
                        "&cip=" + addressDetailsVO.getCardHolderIpAddress() +           // Ip
                        "&d12=" + dtf.format(now) + //Date and local time for Transaction.
                        "&d4=" + amount +             //amount
                        "&de4=2" +              //amount decimal  0 or 2
                        "&d49=" + currencyId;               //Currency Code.

                transactionLogger.error("-----sale request-----" + trackingID + "--" +  saleRequestlog);

                String saleResponse = "";
                if (isTest)
                {
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_SALE_URL"));
                    saleResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_SALE_URL"), saleRequest, "BASIC", credentials);
                }
                else
                {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_SALE_URL"));
                    saleResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_SALE_URL"), saleRequest, "BASIC", credentials);
                }
                transactionLogger.error("-----sale response-----" + trackingID + "--" +  saleResponse);
                if (functions.isValueNull(saleResponse))
                {
                    Map<String, String> responseMap = KortaPayUtils.getQueryMap(saleResponse);
                    if (responseMap!=null)
                    {
                        String responseCode=responseMap.get("d39");
                        String user=responseMap.get("user");
                        String site=responseMap.get("site");
                        String processingCode=responseMap.get("d3");
                        String responseAmount=responseMap.get("d4");
                        String amountExponent=responseMap.get("de4");
                        String responseTime=responseMap.get("d12");
                        String responseOrderId = responseMap.get("d31");
                        String settlementRefNo=responseMap.get("d37");
                        String authorisationCode=responseMap.get("d38");
                        String uniqueResponseTransId=responseMap.get("d56");
                        String error=responseMap.get("error");
                        String errortext=responseMap.get("errortext");
                        String d39text=responseMap.get("d39text");

                        if (responseCode.equals("000"))
                        {
                            status="success";
                            commResponseVO.setDescription(d39text);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }
                        else
                        {
                            status="fail";
                            commResponseVO.setDescription("errorcode:" + error + ":" + errortext + ":" + d39text);
                        }
                        commResponseVO.setAmount(responseAmount);
                        commResponseVO.setAuthCode(authorisationCode);
                        commResponseVO.setBankTransactionDate(responseTime);
                       // commResponseVO.setResponseTime(responseTime);
                        commResponseVO.setCurrency(transDetailsVO.getCurrency());
                        commResponseVO.setIpaddress(ip);
                        commResponseVO.setRemark(d39text);
                        commResponseVO.setStatus(status);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                        commResponseVO.setTransactionId(uniqueResponseTransId);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setCurrency(currency);
                    }
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("---Entering into processAuthentication---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Gson gson= new Gson();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String terminalNumber = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String mpiMid=GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();
        String is3DSupported=GatewayAccountService.getGatewayAccount(accountId).get_3DSupportAccount();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        boolean isTest = gatewayAccount.isTest();
        String termUrl="";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String hostUrl="";
        String ip="";

        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
        }
        else{
            termUrl = RB.getString("TERM_URL");
        }
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
            ip=addressDetailsVO.getCardHolderIpAddress();
        }else {
            ip=addressDetailsVO.getIp();
        }
        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String amount= KortaPayUtils.getCentAmount(transDetailsVO.getAmount());
        String status ="";

        try
        {
            if("Y".equals(is3DSupported) || "O".equals(is3DSupported))
            {
                transactionLogger.error("---inside is3DSupported---");
                EndeavourMPIGateway endeavourMPIGateway= new EndeavourMPIGateway();
                EnrollmentRequestVO enrollmentRequestVO= new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(),cardDetailsVO.getExpMonth()));
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(KortaPayUtils.getCentAmount(transDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(trackingID);
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);

                transactionLogger.error("---Enrollment Request---"+trackingID + "--" + gson.toJson(enrollmentRequestVO));

                EnrollmentResponseVO enrollmentResponseVO=endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                transactionLogger.error("---Enrollment Response---"+trackingID + "--" + gson.toJson(enrollmentResponseVO));

                if(enrollmentResponseVO!=null)
                {
                    if(enrollmentResponseVO.getResult().equalsIgnoreCase("Enrolled") && enrollmentResponseVO.getAvr().equalsIgnoreCase("Y"))
                    {
                        String acsUrl= java.net.URLDecoder.decode(enrollmentResponseVO.getAcsUrl(), "UTF-8");
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setPaReq(enrollmentResponseVO.getPAReq());
                        commResponseVO.setUrlFor3DRedirect(acsUrl);
                        commResponseVO.setTerURL(termUrl + trackingID);
                        commResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                    }
                    else if(enrollmentResponseVO.getAvr().equalsIgnoreCase("U") || enrollmentResponseVO.getAvr().equalsIgnoreCase("N"))
                    {
                        if ("O".equals(is3dSupported))
                        {
                            transactionLogger.error("rejecting 3d card as per configuration");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("SYS:Only 3D card Supported");
                            commResponseVO.setRemark("Only 3D card Supported");
                            return commResponseVO;
                        }
                        transactionLogger.debug("---inside 3D but in Non 3D way");
                        String authRequest = "" +
                                "user=" + userName +
                                "&pwd=" + password +
                                "&site=13" +
                                "&d41=" + terminalNumber +
                                "&d42=" + mId +
                                "&capture=False" +
                               // "&d3=200000" +
                                "&d31=" + trackingID +
                                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +
                                "&d2=" + cardDetailsVO.getCardNum() +
                                "&d14=" +KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth()) +
                                "&d47=" + cardDetailsVO.getcVV() +
                                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +
                                "&d4=" + amount +
                                "&de4=2" +
                                "&d49=" + currencyId;
                        String authRequestlog = "" +
                                "user=" + userName +
                                "&pwd=" + password +
                                "&site=13" +
                                "&d41=" + terminalNumber +
                                "&d42=" + mId +
                                "&capture=False" +
                                // "&d3=200000" +
                                "&d31=" + trackingID +
                                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +
                                "&d2=" + functions.maskingPan(cardDetailsVO.getCardNum()) +
                                "&d14=" +functions.maskingExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth())) +
                                "&d47=" + functions.maskingNumber(cardDetailsVO.getcVV()) +
                                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +
                                "&d4=" + amount +
                                "&de4=2" +
                                "&d49=" + currencyId;
                        transactionLogger.error("-----auth request-----" + trackingID + "--" + authRequestlog);

                        String authResponse = "";
                        if (isTest)
                        {
                            transactionLogger.error("inside isTest-----" + RB.getString("TEST_SALE_URL"));
                            authResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_SALE_URL"), authRequest, "BASIC", credentials);
                        }
                        else
                        {
                            transactionLogger.error("inside isLive-----" + RB.getString("LIVE_SALE_URL"));
                            authResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_SALE_URL"), authRequest, "BASIC", credentials);
                        }
                        transactionLogger.error("-----auth response-----" +trackingID + "--" +  authResponse);
                        if (functions.isValueNull(authResponse))
                        {
                            Map<String, String> responseMap = KortaPayUtils.getQueryMap(authResponse);
                            if (responseMap != null)
                            {
                                String responseCode = responseMap.get("d39");
                                String user = responseMap.get("user");
                                String site = responseMap.get("site");
                                String processingCode = responseMap.get("d3");
                                String responseAmount = responseMap.get("d4");
                                String amountExponent = responseMap.get("de4");
                                String responseTime = responseMap.get("d12");
                                String settlementRefNo = responseMap.get("d37");
                                String responseOrderId = responseMap.get("d31");
                                String authorisationCode = responseMap.get("d38");
                                String uniqueResponseTransId = responseMap.get("d56");
                                String error = responseMap.get("error");
                                String errortext = responseMap.get("errortext");
                                String d39text = responseMap.get("d39text");

                                if (responseCode.equals("000"))
                                {
                                    status = "success";
                                    commResponseVO.setDescription(d39text);
                                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                }
                                else
                                {
                                    status = "fail";
                                    commResponseVO.setDescription("errorcode:" + error + ":" + errortext + ":" + d39text);
                                }
                                commResponseVO.setAmount(responseAmount);
                                commResponseVO.setAuthCode(authorisationCode);
                                commResponseVO.setBankTransactionDate(responseTime);
                              //  commResponseVO.setResponseTime(responseTime);
                                commResponseVO.setCurrency(transDetailsVO.getCurrency());
                                commResponseVO.setIpaddress(ip);
                                commResponseVO.setRemark(d39text);
                                commResponseVO.setStatus(status);
                                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                                commResponseVO.setTransactionId(uniqueResponseTransId);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setCurrency(currency);
                            }
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                    }
                }
            }
            else
            {
                String authRequest = "" +
                        "user=" + userName +
                        "&pwd=" + password +
                        "&site=13" +
                        "&d41=" + terminalNumber +  //Terminal number. Provided by KORTA.
                        "&d42=" + mId +           //Your agreement number. Provided by KORTA
                       // "&d3=000000" +
                        "&capture=False" +          // *True for sale or direct capture*   *False for Auth*
                        "&d31=" + trackingID +    //Your order number, provided by you. Currently max. 15
                        "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +           //Card Holder Name
                        "&d2=" + cardDetailsVO.getCardNum() +   //card number
                        "&d14=" +KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth()) +               //Expiry
                        "&d47=" + cardDetailsVO.getcVV() +                // cvc
                        "&cip=" + addressDetailsVO.getCardHolderIpAddress() +           // Ip
                        "&d4=" + amount +             //amount
                        "&de4=2" +              //amount decimal  0 or 2
                        "&d49=" + currencyId;               //Currency Code.
                String authRequestlog = "" +
                        "user=" + userName +
                        "&pwd=" + password +
                        "&site=13" +
                        "&d41=" + terminalNumber +  //Terminal number. Provided by KORTA.
                        "&d42=" + mId +           //Your agreement number. Provided by KORTA
                        // "&d3=000000" +
                        "&capture=False" +          // *True for sale or direct capture*   *False for Auth*
                        "&d31=" + trackingID +    //Your order number, provided by you. Currently max. 15
                        "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +           //Card Holder Name
                        "&d2=" + functions.maskingPan(cardDetailsVO.getCardNum()) +   //card number
                        "&d14=" +functions.maskingExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth())) +               //Expiry
                        "&d47=" + functions.maskingNumber(cardDetailsVO.getcVV()) +                // cvc
                        "&cip=" + addressDetailsVO.getCardHolderIpAddress() +           // Ip
                        "&d4=" + amount +             //amount
                        "&de4=2" +              //amount decimal  0 or 2
                        "&d49=" + currencyId;               //Currency Code.

                transactionLogger.error("-----auth request-----" +trackingID + "--" +  authRequestlog);

                String authResponse = "";
                if (isTest)
                {
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_SALE_URL"));
                    authResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_SALE_URL"), authRequest, "BASIC", credentials);
                }
                else
                {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_SALE_URL"));
                    authResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_SALE_URL"), authRequest, "BASIC", credentials);
                }
                transactionLogger.error("-----auth response-----" +trackingID + "--" +  authResponse);
                if (functions.isValueNull(authResponse))
                {
                    Map<String, String> responseMap = KortaPayUtils.getQueryMap(authResponse);
                    if (responseMap!=null)
                    {
                        String responseCode=responseMap.get("d39");
                        String user=responseMap.get("user");
                        String site=responseMap.get("site");
                        String processingCode=responseMap.get("d3");
                        String responseAmount=responseMap.get("d4");
                        String amountExponent=responseMap.get("de4");
                        String responseTime=responseMap.get("d12");
                        String settlementRefNo=responseMap.get("d37");
                        String responseOrderId = responseMap.get("d31");
                        String authorisationCode=responseMap.get("d38");
                        String uniqueResponseTransId=responseMap.get("d56");
                        String error=responseMap.get("error");
                        String errortext=responseMap.get("errortext");
                        String d39text=responseMap.get("d39text");

                        if (responseCode.equals("000"))
                        {
                            status="success";
                            commResponseVO.setDescription(d39text);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }
                        else
                        {
                            status="fail";
                            commResponseVO.setDescription("errorcode:"+error+":"+errortext+":"+d39text);
                        }
                        commResponseVO.setAmount(transDetailsVO.getAmount());
                        commResponseVO.setAuthCode(authorisationCode);
                        commResponseVO.setBankTransactionDate(responseTime);
                       // commResponseVO.setResponseTime(responseTime);
                        commResponseVO.setCurrency(transDetailsVO.getCurrency());
                        commResponseVO.setIpaddress(ip);
                        commResponseVO.setRemark(d39text);
                        commResponseVO.setStatus(status);
                        commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                        commResponseVO.setTransactionId(uniqueResponseTransId);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setCurrency(currency);
                    }
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("---Entering into processCapture---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String terminalNumber = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String termUrl="";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String hostUrl="";
        String ip="";

        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
        }
        else{
            termUrl = RB.getString("TERM_URL");
        }
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
            ip=addressDetailsVO.getCardHolderIpAddress();
        }else {
            ip=addressDetailsVO.getIp();
        }
        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String amount= KortaPayUtils.getCentAmount(transDetailsVO.getAmount());
        String status ="";

        try
        {
            String captureRequest = "" +
                    "user=" + userName +
                    "&pwd=" + password +
                    "&site=13" +
                    "&d4=" + amount +             //amount
                    "&de4=2" +              //amount decimal  0 or 2
                    "&d41=" + terminalNumber +           //Terminal number. Provided by KORTA.
                    "&d42=" + mId +        //Your agreement number. Provided by KORTA
                    "&d56="+transDetailsVO.getPreviousTransactionId();

            String userPassword = userName + ":" + password;
            String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            transactionLogger.error("-----capture request-----" + trackingID + "--" + captureRequest);

            String captureResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_CAPTURE_URL"));
                captureResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_CAPTURE_URL"), captureRequest, "BASIC", credentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_CAPTURE_URL"));
                captureResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_CAPTURE_URL"), captureRequest, "BASIC", credentials);
            }
            transactionLogger.error("-----capture response-----" + trackingID + "--" + captureResponse);
            if (functions.isValueNull(captureResponse))
            {
                Map<String, String> responseMap = KortaPayUtils.getQueryMap(captureResponse);
                if (responseMap!=null)
                {
                    String responseCode=responseMap.get("d39"); // this field decides fail or success.
                    String user=responseMap.get("user");
                    String site=responseMap.get("site");
                    String processingCode=responseMap.get("d3");
                    String responseAmount=responseMap.get("d4");
                    String amountExponent=responseMap.get("de4");
                    String responseTime=responseMap.get("d12");
                    String settlementRefNo=responseMap.get("d37");
                    String responseOrderId = responseMap.get("d31");
                    String uniqueResponseTransId=responseMap.get("d56");
                    String code1=responseMap.get("&o39"); // this field does not decides fail or success.
                    String error=responseMap.get("error");
                    String errortext=responseMap.get("errortext");
                    String d39text=responseMap.get("d39text");

                    if (responseCode.equals("000"))
                    {
                        status="success";
                        commResponseVO.setDescription(d39text);
                    }
                    else
                    {
                        status="fail";
                        commResponseVO.setDescription("errorcode:"+error+":"+errortext+":"+d39text);
                    }
                    commResponseVO.setAmount(responseAmount);
                    commResponseVO.setBankTransactionDate(responseTime);
                    commResponseVO.setCurrency(transDetailsVO.getCurrency());
                    commResponseVO.setRemark(d39text);
                    commResponseVO.setStatus(status);
                    commResponseVO.setIpaddress(ip);
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                    commResponseVO.setTransactionId(uniqueResponseTransId);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setCurrency(currency);
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;

    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("---Entering into processRefund---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String terminalNumber = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMddhhmmss");
        LocalDateTime now = LocalDateTime.now();
        dtf.format(now);
        String termUrl="";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String hostUrl="";
        String ip="";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
        }
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String status ="";
        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String amount= KortaPayUtils.getCentAmount(transDetailsVO.getAmount());
        String previousTransactionId=transDetailsVO.getPreviousTransactionId();
        String mainAmount=KortaPayUtils.getCentAmount(transDetailsVO.getPreviousTransactionAmount());
        String reversedAmount=transDetailsVO.getReversedAmount();
        String o4rfield="";
        if (reversedAmount.equals("0.0") || reversedAmount.equals("0.00"))
        {
            o4rfield="";
        }
        else
        {
            reversedAmount=KortaPayUtils.getCentAmount(reversedAmount);
            o4rfield="&o4r="+reversedAmount;
        }

        transactionLogger.debug("amount / previousTransactionId/ mainAmount"+amount+" "+previousTransactionId+" "+mainAmount);

        transactionLogger.debug("transactionTime-----------"+transDetailsVO.getResponsetime());
        KortaPayUtils kortaPayUtils=new KortaPayUtils();
        boolean result=kortaPayUtils.TimeDifference(transDetailsVO.getResponsetime());

        if (result || amount.equals(mainAmount))
        {
            try
            {
                String refundRequest = "" +
                        "user=" + userName +
                        "&pwd=" + password +
                       // "&site=13" +
                        "&d41=" + terminalNumber +
                        "&d42=" + mId +
                        "&d31=" +trackingID +       // unique order id
                        "&d49=" +currencyId+
                        "&d4=" + amount +           // amount to reverse this should not be more than main amount.
                        "&de4=2" +
                        "&o4="+ mainAmount+
                        o4rfield+
                        "&d56="+previousTransactionId;

                String userPassword = userName + ":" + password;
                String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
                transactionLogger.error("-----refund request-----" + trackingID + "--" + refundRequest);

                String refundResponse = "";
                if (isTest)
                {
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_REFUND_URL"));
                    refundResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_REFUND_URL"), refundRequest, "BASIC", credentials);
                }
                else
                {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_REFUND_URL"));
                    refundResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_REFUND_URL"), refundRequest, "BASIC", credentials);
                }
                transactionLogger.error("-----refund response-----" + trackingID + "--" + refundResponse);
                if (functions.isValueNull(refundResponse))
                {
                    Map<String, String> responseMap = KortaPayUtils.getQueryMap(refundResponse);
                    if (responseMap!=null)
                    {
                        String responseCode=responseMap.get("d39"); // this field decides fail or success.
                        String user=responseMap.get("user");
                        String site=responseMap.get("site");
                        String processingCode=responseMap.get("d3");
                        String responseAmount=responseMap.get("d4");
                        String amountExponent=responseMap.get("de4");
                        String responseTime=responseMap.get("d12");
                        String settlementRefNo=responseMap.get("d37");
                        String responseOrderId = responseMap.get("d31");
                        String uniqueResponseTransId=responseMap.get("d56");
                        String code1=responseMap.get("&o39"); // this field does not decides fail or success.
                        String error=responseMap.get("error");
                        String errortext=responseMap.get("errortext");
                        String d39text=responseMap.get("d39text");
                        String d31=responseMap.get("d31");

                        if (responseCode.equals("000") && error.equalsIgnoreCase("00000"))
                        {
                            status="success";
                            commResponseVO.setDescription(d39text);
                        }
                        else
                        {
                            status="fail";
                            commResponseVO.setDescription("errorcode:"+error+":"+errortext+":"+d39text);
                        }
                        transactionLogger.debug("uniqueResponseTransId ----- previousTransactionId "+uniqueResponseTransId+"-----"+previousTransactionId);

                        commResponseVO.setAmount(responseAmount);
                        commResponseVO.setCurrency(transDetailsVO.getCurrency());
                        commResponseVO.setRemark(d39text);
                        commResponseVO.setStatus(status);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setTransactionId(uniqueResponseTransId);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setCurrency(currency);
                    }
                }
            }
            catch (Exception e)
            {
                transactionLogger.error("Exception-----", e);
            }
        }
        else
        {
            commResponseVO.setStatus("waitforreversal");
            commResponseVO.setRemark("Transaction cannot be Reversed , Please wait for 24 Hours");
            commResponseVO.setDescription("Transaction cannot be Reversed , Please wait for 24 Hours");
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("---Entering into processCancel---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String terminalNumber = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMddhhmmss");
        LocalDateTime now = LocalDateTime.now();
        dtf.format(now);
        String status ="";
        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String amount= KortaPayUtils.getCentAmount(transDetailsVO.getAmount());
        String previousTransactionId=transDetailsVO.getPreviousTransactionId();

       // String mainAmount=KortaPayUtils.getCentAmount(transDetailsVO.getPreviousTransactionAmount());
        transactionLogger.debug("main amount----"+amount);
        transactionLogger.debug("currencyId/"+currencyId);
        transactionLogger.debug("previousTransactionId-----/"+previousTransactionId);

        try
        {
            String cancelRequest = "" +
                    "user=" + userName +
                    "&pwd=" + password +
                    "&site=13" +
                    "&d41=" + terminalNumber +
                    "&d42=" + mId +
                    "&d31=" +trackingID +
                    "&d49=" +currencyId+
                    "&d4=" + amount +
                    "&de4=2" +
                    "&o4="+ amount+
                    "&d56="+previousTransactionId;

            String userPassword = userName + ":" + password;
            String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            transactionLogger.error("-----cancel request-----" +trackingID + "--" +  cancelRequest);

            String cancelResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_REFUND_URL"));
                cancelResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_REFUND_URL"), cancelRequest, "BASIC", credentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_REFUND_URL"));
                cancelResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_REFUND_URL"), cancelRequest, "BASIC", credentials);
            }
            transactionLogger.error("-----cancel response-----" +trackingID + "--" +  cancelResponse);

            if (functions.isValueNull(cancelResponse))
            {
                Map<String, String> responseMap = KortaPayUtils.getQueryMap(cancelResponse);
                if (responseMap!=null)
                {
                    String responseCode=responseMap.get("d39"); // this field decides fail or success.
                    String user=responseMap.get("user");
                    String site=responseMap.get("site");
                    String processingCode=responseMap.get("d3");
                    String responseAmount=responseMap.get("d4");
                    String amountExponent=responseMap.get("de4");
                    String responseTime=responseMap.get("d12");
                    String settlementRefNo=responseMap.get("d37");
                    String responseOrderId = responseMap.get("d31");
                    String uniqueResponseTransId=responseMap.get("d56");
                    String code1=responseMap.get("&o39"); // this field does not decides fail or success.
                    String error=responseMap.get("error");
                    String errortext=responseMap.get("errortext");
                    String d39text=responseMap.get("d39text");
                    String d31=responseMap.get("d31");

                    if (responseCode.equals("000") && error.equalsIgnoreCase("00000"))
                    {
                        status="success";
                        commResponseVO.setDescription(d39text);
                    }
                    else
                    {
                        status="fail";
                        commResponseVO.setDescription("errorcode:"+error+":"+errortext+":"+d39text);
                    }

                    commResponseVO.setAmount(responseAmount);
                    commResponseVO.setBankTransactionDate(responseTime);
                    commResponseVO.setCurrency(transDetailsVO.getCurrency());
                    commResponseVO.setRemark(d39text);
                    commResponseVO.setStatus(status);
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                    commResponseVO.setTransactionId(uniqueResponseTransId);
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("---Entering process3DSaleConfirmation of KortaPay---");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String terminalNumber = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String amount= KortaPayUtils.getCentAmount(transDetailsVO.getAmount());
        String cvv= PzEncryptor.decryptCVV(commRequestVO.getMd());

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingId);
        paresDecodeRequestVO.setPares(commRequestVO.getPaRes());
        paresDecodeRequestVO.setTrackid(trackingId);

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        String XID="";
        String CAVV="";
        String VaRes="";
        String Sign="";
        String paRes="";
        String ECI="";

        if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getStatus())){
            VaRes=paresDecodeResponseVO.getStatus();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }

        transactionLogger.error("---processCommon3DSaleConfirmation 3D Sale Request---");
        String saleRequest = "" +
                "user=" + userName +
                "&pwd=" + password +
                "&site=13" +
                "&d41=" + terminalNumber +
                "&d42=" + mId +
                "&capture=True" +
                "&d31=" + trackingId +
                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +
                "&d2=" + cardDetailsVO.getCardNum() +
                "&d14=" +KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth())+      //Expiry
                "&d47="+cvv+
                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +
                "&d4=" + amount +
                "&de4=2" +
                "&d49=" + currencyId+
                "&TXcavv=" + CAVV+
                "&TransactionStatus=" + ECI+
                "&PurchaseXID=" + XID;

        String saleRequestlog = "" +
                "user=" + userName +
                "&pwd=" + password +
                "&site=13" +
                "&d41=" + terminalNumber +
                "&d42=" + mId +
                "&capture=True" +
                "&d31=" + trackingId +
                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +
                "&d2=" + functions.maskingPan(cardDetailsVO.getCardNum()) +
                "&d14=" +functions.maskingExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth()))+      //Expiry
                "&d47="+functions.maskingNumber(cvv)+
                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +
                "&d4=" + amount +
                "&de4=2" +
                "&d49=" + currencyId+
                "&TXcavv=" + CAVV+
                "&TransactionStatus=" + ECI+
                "&PurchaseXID=" + XID;

        transactionLogger.error("---3D sale Request---" +trackingId + "--" +  saleRequestlog);
        String userPassword = userName + ":" + password;
        String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        String saleResponse = "";
        if (isTest)
        {
            transactionLogger.error("inside isTest-----" + RB.getString("TEST_SALE_URL"));
            saleResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_SALE_URL"), saleRequest, "BASIC", credentials);
        }
        else
        {
            transactionLogger.error("inside isLive-----" + RB.getString("LIVE_SALE_URL"));
            saleResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_SALE_URL"), saleRequest, "BASIC", credentials);
        }
        transactionLogger.error("-----3D sale response-----" +trackingId + "--" +  saleResponse);
        if (functions.isValueNull(saleResponse))
        {
            Map<String, String> responseMap = KortaPayUtils.getQueryMap(saleResponse);
            if (responseMap != null)
            {
                String responseCode = responseMap.get("d39");
                String user = responseMap.get("user");
                String site = responseMap.get("site");
                String processingCode = responseMap.get("d3");
                String responseAmount = responseMap.get("d4");
                String amountExponent = responseMap.get("de4");
                String responseTime = responseMap.get("d12");
                String settlementRefNo = responseMap.get("d37");
                String responseOrderId = responseMap.get("d31");
                String authorisationCode = responseMap.get("d38");
                String uniqueResponseTransId = responseMap.get("d56");
                String error = responseMap.get("error");
                String errortext = responseMap.get("errortext");
                String d39text = responseMap.get("d39text");
                String status="";
                if (responseCode.equals("000"))
                {
                    status = "success";
                    commResponseVO.setDescription(d39text);
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                }
                else
                {
                    status = "fail";
                    commResponseVO.setDescription("errorcode:" + error + ":" + errortext + ":" + d39text);
                }

                commResponseVO.setAmount(responseAmount);
                commResponseVO.setAuthCode(authorisationCode);
                commResponseVO.setBankTransactionDate(responseTime);
               // commResponseVO.setResponseTime(responseTime);
                commResponseVO.setCurrency(transDetailsVO.getCurrency());
                commResponseVO.setRemark(d39text);
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                commResponseVO.setTransactionId(uniqueResponseTransId);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setCurrency(currency);
            }
        }
        return commResponseVO;
    }

    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException,PZGenericConstraintViolationException
    {
        transactionLogger.error("---Entering processCommon3DAuthConfirmation of KortaPay---");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String terminalNumber = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String amount= KortaPayUtils.getCentAmount(transDetailsVO.getAmount());
        String cvv= PzEncryptor.decryptCVV(commRequestVO.getMd());

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingID);
        paresDecodeRequestVO.setPares(commRequestVO.getPaRes());
        paresDecodeRequestVO.setTrackid(trackingID);

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        String XID="";
        String CAVV="";
        String VaRes="";
        String Sign="";
        String paRes="";
        String ECI="";

        if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getStatus())){
            VaRes=paresDecodeResponseVO.getStatus();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }

        transactionLogger.error("---processCommon3DSaleConfirmation 3D auth Request---");
        String authRequest = "" +
                "user=" + userName +
                "&pwd=" + password +
                "&site=13" +
                "&d41=" + terminalNumber +           //Terminal number. Provided by KORTA.
                "&d42=" + mId +           //Your agreement number. Provided by KORTA.
               // "&d3=000000" +
                "&capture=False" +           // *True for sale or direct capture*   *False for Auth*
                "&d31=" + trackingID +    //Your order number, provided by you. Currently max. 15
                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +           //Card Holder Name
                "&d2=" + cardDetailsVO.getCardNum() +   //card number
                "&d14=" +KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth()) +               //Expiry
                "&d47="+cvv+                // cvc
                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +           // Ip
                "&d4=" + amount +             //amount
                "&de4=2" +              //amount decimal  0 or 2
                "&d49=" + currencyId+              //Currency Code.
                "&TXcavv=" + CAVV+              //Currency Code.
                "&TransactionStatus=" + ECI+              //Currency Code.
                "&PurchaseXID=" + XID;             //Currency Code.

        String authRequestlog = "" +
                "user=" + userName +
                "&pwd=" + password +
                "&site=13" +
                "&d41=" + terminalNumber +           //Terminal number. Provided by KORTA.
                "&d42=" + mId +           //Your agreement number. Provided by KORTA.
                // "&d3=000000" +
                "&capture=False" +           // *True for sale or direct capture*   *False for Auth*
                "&d31=" + trackingID +    //Your order number, provided by you. Currently max. 15
                "&d2name=" + addressDetailsVO.getFirstname() + "" + addressDetailsVO.getLastname() +           //Card Holder Name
                "&d2=" + functions.maskingPan(cardDetailsVO.getCardNum()) +   //card number
                "&d14=" +functions.maskingExpiry(KortaPayUtils.getCardExpiry(cardDetailsVO.getExpYear(), cardDetailsVO.getExpMonth())) +               //Expiry
                "&d47="+functions.maskingNumber(cvv)+                // cvc
                "&cip=" + addressDetailsVO.getCardHolderIpAddress() +           // Ip
                "&d4=" + amount +             //amount
                "&de4=2" +              //amount decimal  0 or 2
                "&d49=" + currencyId+              //Currency Code.
                "&TXcavv=" + CAVV+              //Currency Code.
                "&TransactionStatus=" + ECI+              //Currency Code.
                "&PurchaseXID=" + XID;             //Currency Code.

        transactionLogger.error("---3D auth Request---" +trackingID + "--" +  authRequestlog);
        String userPassword = userName + ":" + password;
        String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        String authResponse = "";
        if (isTest)
        {
            transactionLogger.error("inside isTest-----" + RB.getString("TEST_SALE_URL"));
            authResponse = KortaPayUtils.doHttpPostConnection(RB.getString("TEST_SALE_URL"), authResponse, "BASIC", credentials);
        }
        else
        {
            transactionLogger.error("inside isLive-----" + RB.getString("LIVE_SALE_URL"));
            authResponse = KortaPayUtils.doHttpPostConnection(RB.getString("LIVE_SALE_URL"), authResponse, "BASIC", credentials);
        }
        transactionLogger.debug("-----3D auth Response-----" +trackingID + "--" +  authResponse);
        if (functions.isValueNull(authResponse))
        {
            Map<String, String> responseMap = KortaPayUtils.getQueryMap(authResponse);
            if (responseMap != null)
            {
                String responseCode = responseMap.get("d39");
                String user = responseMap.get("user");
                String site = responseMap.get("site");
                String processingCode = responseMap.get("d3");
                String responseAmount = responseMap.get("d4");
                String amountExponent = responseMap.get("de4");
                String responseTime = responseMap.get("d12");
                String settlementRefNo = responseMap.get("d37");
                String responseOrderId = responseMap.get("d31");
                String authorisationCode = responseMap.get("d38");
                String uniqueResponseTransId = responseMap.get("d56");
                String error = responseMap.get("error");
                String errortext = responseMap.get("errortext");
                String d39text = responseMap.get("d39text");
                String status="";
                if (responseCode.equals("000"))
                {
                    status = "success";
                    commResponseVO.setDescription(d39text);
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                }
                else
                {
                    status = "fail";
                    commResponseVO.setDescription("errorcode:" + error + ":" + errortext + ":" + d39text);
                }
                commResponseVO.setAmount(responseAmount);
                commResponseVO.setAuthCode(authorisationCode);
                commResponseVO.setBankTransactionDate(responseTime);
               // commResponseVO.setResponseTime(responseTime);
                commResponseVO.setCurrency(transDetailsVO.getCurrency());
                commResponseVO.setRemark(d39text);
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                commResponseVO.setTransactionId(uniqueResponseTransId);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setCurrency(currency);
            }
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
