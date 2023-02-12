package com.payment.appletree;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.ReserveField2VO;
import com.manager.vo.TerminalVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 7/15/2021.
 */
public class AppleTreeCellulantPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(AppleTreeCellulantPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "Appletree";
    private static ResourceBundle RB                    = LoadProperties.getProperty("com.directi.pg.appletree");

    public AppleTreeCellulantPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    //new flow comment
   /* @Override
    public GenericResponseVO getOptionalCodeList(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        ReserveField2VO reserveField2VO = commRequestVO.getReserveField2VO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        boolean isTest = gatewayAccount.isTest();
        String Mid = gatewayAccount.getMerchantId();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        UUID uuid1 = UUID.randomUUID();
        String RequestId = uuid1.toString();
        String encodedPassword = "";
        if (functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());

        String countryCode = "";
        String paymentoptionsURL = "";
        String privatePath = "";
        String message ="";
        String Status ="";
        if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            countryCode = commAddressDetailsVO.getCountry();
        if (countryCode.length() == 3)
            countryCode = AppleTreeCellulantUtils.getCountryTwoDigit(countryCode);

        if (isTest)
        {
            paymentoptionsURL = RB.getString("PR_TEST_URL");
            privatePath = RB.getString("TEST_PRIVATE_KEY");
        }
        else
        {
            paymentoptionsURL = RB.getString("PR_LIVE_URL");
            privatePath = RB.getString("LIVE_PRIVATE_KEY");
        }


        String PaymentOptionsSignature = AppleTreeCellulantUtils.getSignature(Mid + RequestId, privatePath);

        try
        {
            StringBuffer PaymentOptionsRequest = new StringBuffer();

            PaymentOptionsRequest.append("{" +
                    "\"PaymentRequestId\":\"" + RequestId + "\"," +
                    "\"OrderCurrency\":\"" + commTransactionDetailsVO.getCurrency() + "\"," +
                    "\"Country\":\"" + countryCode + "\"" +
                    "}");

            transactionLogger.error("PaymentOptionsRequest ----For " + trackingID + " " + PaymentOptionsRequest.toString());
            String PaymentOptionsResponse = null;
            PaymentOptionsResponse = AppleTreeCellulantUtils.doPostHTTPSURLConnectionClient(paymentoptionsURL, PaymentOptionsRequest.toString(), PaymentOptionsSignature, Mid, encodedPassword);
            transactionLogger.error("PaymentOptionsResponse ----For " + trackingID + " " + PaymentOptionsResponse.toString());

            if (functions.isValueNull(PaymentOptionsResponse) && PaymentOptionsResponse.substring(0, 1).equals("{"))
            {
                JSONObject initResponseJSON = new JSONObject(PaymentOptionsResponse);
                if (initResponseJSON.has("PaymentOptions") && functions.isValueNull(initResponseJSON.getString("PaymentOptions")))
                {
                    JSONArray jsonArray = initResponseJSON.getJSONArray("PaymentOptions");
                    int length = jsonArray.length();
                    JsonArray data_json = new JsonArray();
                    JsonObject json_response = new JsonObject();
                    for (int i = 0; i < length; i++)
                    {
                        JsonObject json = new JsonObject();
                        json.addProperty("Code", jsonArray.getJSONObject(i).getString("Code"));
                        json.addProperty("FinancialServiceName", jsonArray.getJSONObject(i).getString("FinancialServiceName"));
                        json.addProperty("Country", jsonArray.getJSONObject(i).getString("Country"));
                        json.addProperty("Currency", jsonArray.getJSONObject(i).getString("Currency"));
                        json.addProperty("TransactionLimit", jsonArray.getJSONObject(i).getString("TransactionLimit"));
                        json.addProperty("PaymentMethodName", jsonArray.getJSONObject(i).getString("PaymentMethodName"));
                        json.addProperty("IntegrationMethod", jsonArray.getJSONObject(i).getString("IntegrationMethod"));
                        data_json.add(json);

                    }
                    json_response.add("aaData", data_json);
                    comm3DResponseVO.setOptionalCode(String.valueOf(json_response));
                }
                else
                {
                    if (initResponseJSON.has("ResultDetails") && functions.isValueNull(initResponseJSON.getString("ResultDetails")))
                    {
                        if (functions.isValueNull(initResponseJSON.getJSONObject("ResultDetails").getString("Status")))
                        {
                            Status = initResponseJSON.getJSONObject("ResultDetails").getString("Status");
                        }
                        if (functions.isValueNull(initResponseJSON.getJSONObject("ResultDetails").getString("ResultMessage")))
                        {
                            message = initResponseJSON.getJSONObject("ResultDetails").getString("ResultMessage");
                        }
                    }
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(Status + " Invalid Response");
                }
            }
            else
            {
                comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setRemark("Invalid Response");
                comm3DResponseVO.setDescription("Invalid Response");
            }
        }
        catch(JSONException e )
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }
        return comm3DResponseVO;
    }*/

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws
            PZGenericConstraintViolationException
    {
        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions                 = new Functions();
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        CommRequestVO commRequestVO             = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        ReserveField2VO reserveField2VO                     = commRequestVO.getReserveField2VO();
        CommMerchantVO commMerchantVO                       = commRequestVO.getCommMerchantVO();

        boolean isTest          = gatewayAccount.isTest();
        String Mid              = gatewayAccount.getMerchantId();
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String MerchantId       = gatewayAccount.getFRAUD_FTP_SITE();
        String StoreId          = gatewayAccount.getFRAUD_FTP_PATH();
        String TerminalId       = gatewayAccount.getFRAUD_FTP_USERNAME();
        UUID uuid1              = UUID.randomUUID();
        String RequestId        = uuid1.toString();
        String encodedPassword  = "";
        if (functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());

        String Amount =commTransactionDetailsVO.getAmount();

        String Paymentid        = "";
        String MobileNumber     = "";
        String EmailAddress     = "";
        String StreetAddress    = "";
        String zip          = "";
        String countryCode  = "";
        String Locality     = "";
        String state        = "";
        String Status       = "";
        String transactionStatus = "";
        String message          = "";
        String referenceNumber  = "";
        String paymentoptionsURL    = "";
        String SaleURL              = "";
        String privatePath  = "";
        String OptionalCode = "";
        String Description  = "";
        String Remark       = "";
        String XID      = null;
        String MD       = null;
        String TermUrl  = null;
        String auth     = null;
        String ACSURL   = null;
        String PAReq    = null;
        String PARes    = null;
        String BackgroundColour = "Red";
        String EmbededUrl       = null;
        String cardno           = "";
        String ExpMonth         = "";
        String ExpYear          = "";
        String CVV              = "";
        String PaymentCompletionURL             = "";
        String PaymentCompletionInstructions    = "";
        long expirationMin  = 5;
        String ExpiredTime                  = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        SimpleDateFormat simpleDateFormat   = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String FinancialServiceName         = "";

        Hashtable additioanlParams = null;
        String paymentOtionCode = null;
        try
        {
            if (functions.isValueNull(ExpiredTime))
                expirationMin = (long) Double.parseDouble(ExpiredTime.trim());
        }
        catch (NumberFormatException e)
        {
            transactionLogger.error("NumberFormatException--" + trackingID + "-->", e);
        }
        Date date                   = new Date();
        String CreationDateTime     = simpleDateFormat.format(date);
        long threadSleepTime        = expirationMin * 60 * 1000;
        long time   = date.getTime() + (expirationMin * 60 * 1000);
        date        = new Date(time);
        String ExpiryDateTime = simpleDateFormat.format(date);

        if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            MobileNumber = commAddressDetailsVO.getPhone();
        if (functions.isValueNull(commAddressDetailsVO.getEmail()))
            EmailAddress = commAddressDetailsVO.getEmail();
        if (functions.isValueNull(commAddressDetailsVO.getStreet()))
            StreetAddress = commAddressDetailsVO.getStreet();
        if (functions.isValueNull(commAddressDetailsVO.getState()))
            state = commAddressDetailsVO.getState();
        if (functions.isValueNull(commAddressDetailsVO.getZipCode()))
            zip = commAddressDetailsVO.getZipCode();
        if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            countryCode = commAddressDetailsVO.getCountry();
        if (countryCode.length() == 3)
            countryCode = AppleTreeCellulantUtils.getCountryTwoDigit(countryCode);
        if (functions.isValueNull(commAddressDetailsVO.getCity()))
            Locality = commAddressDetailsVO.getCity();
        if (commCardDetailsVO != null && functions.isValueNull(commCardDetailsVO.getCardNum()))
        {
            cardno = commCardDetailsVO.getCardNum();
        }
        else
        {
            cardno = reserveField2VO.getAccountNumber();
        }

        if (commCardDetailsVO != null && functions.isValueNull(commCardDetailsVO.getExpMonth()))
        {
            ExpMonth = commCardDetailsVO.getExpMonth();
        }
        else
        {
            ExpMonth = null;
        }
        if (commCardDetailsVO != null && functions.isValueNull(commCardDetailsVO.getExpYear()))
        {
            ExpYear = commCardDetailsVO.getExpYear();
        }
        else
        {
            ExpYear = null;
        }
        if (commCardDetailsVO != null && functions.isValueNull(commCardDetailsVO.getcVV()))
        {
            CVV = commCardDetailsVO.getcVV();
        }
        else
        {
            CVV = null;
        }


        String PaymentType      = GatewayAccountService.getPaymentTypes(commTransactionDetailsVO.getPaymentType());
        String FirstName        = new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        String LastName         = new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);

        if (isTest)
        {
            paymentoptionsURL   = RB.getString("PR_TEST_URL");
            SaleURL             = RB.getString("INIT_TEST_URL");
            privatePath         = RB.getString("TEST_PRIVATE_KEY");
        }
        else
        {
            paymentoptionsURL   = RB.getString("PR_LIVE_URL");
            SaleURL             = RB.getString("INIT_LIVE_URL");
            privatePath         = RB.getString("LIVE_PRIVATE_KEY");
        }

        String ReturnURL = "";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            ReturnURL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            transactionLogger.error("From HOST_URL----" + ReturnURL);
        }
        else
        {
            ReturnURL = RB.getString("TERM_URL") + trackingID;
            transactionLogger.error("From RB TERM_URL ----" + ReturnURL);
        }

        //String REDIRECT_URL = RB.getString("REDIRECT_URL") + trackingID;


        String PaymentOptionsSignature = AppleTreeCellulantUtils.getSignature(Mid + RequestId, privatePath);

        FinancialServiceName = commAddressDetailsVO.getCustomerid();
        String ServiceCode      = GatewayAccountService.getPaymentBrand(commTransactionDetailsVO.getCardType());

        if (ServiceCode.equals("MC"))
        {
            FinancialServiceName = "MasterCard";
        }else if(ServiceCode.equalsIgnoreCase("VISA")){
            FinancialServiceName = "VISA";
        }
        if(commRequestVO.getAdditioanlParams() != null){
            additioanlParams = (Hashtable) commRequestVO.getAdditioanlParams();
            OptionalCode    = (String)additioanlParams.get("paymentOtionCode");
            if(OptionalCode.contains(",")){
                OptionalCode = AppleTreeCellulantUtils.getOptionalCode(OptionalCode,FinancialServiceName);
            }
        }

        transactionLogger.error("PaymentDetails :::::::::: "+trackingID+" " + PaymentType);
        transactionLogger.error("getPaymentType :::::::::: "+trackingID+" " + commTransactionDetailsVO.getPaymentType());
        transactionLogger.error("RequestId:::::::::: "+trackingID+" " + RequestId);
        transactionLogger.error("ServiceCode:::::::::: "+trackingID+" " + FinancialServiceName);
        transactionLogger.error("OptionalCode ----> "+ trackingID + " " + OptionalCode);
        transactionLogger.error("FinancialServiceName ----> " + trackingID + " " + FinancialServiceName);

        try
        {
            StringBuffer PaymentOptionsRequest = new StringBuffer();

            /*PaymentOptionsRequest.append("{" +
                    "\"PaymentRequestId\":\"" + RequestId + "\"," +
                    "\"OrderCurrency\":\"" + commTransactionDetailsVO.getCurrency() + "\"," +
                    "\"Country\":\"" + countryCode + "\"" +
                    "}");

            transactionLogger.error("paymentoptionsURL ----For " + trackingID + " " + paymentoptionsURL);
            transactionLogger.error("PaymentOptionsRequest ----For " + trackingID + " " + PaymentOptionsRequest.toString());
            String PaymentOptionsResponse   = null;
            PaymentOptionsResponse          = AppleTreeCellulantUtils.doPostHTTPSURLConnectionClient(paymentoptionsURL, PaymentOptionsRequest.toString(), PaymentOptionsSignature, Mid, encodedPassword);
            transactionLogger.error("PaymentOptionsResponse ----For " + trackingID + " " + PaymentOptionsResponse.toString());*/
            if (functions.isValueNull(OptionalCode))
            {
                String InitialSignature                 = AppleTreeCellulantUtils.getSignature(Mid + RequestId + commTransactionDetailsVO.getCurrency() + commTransactionDetailsVO.getAmount(), privatePath);
                AppletreeRequestVo appletreeRequestVo   = new AppletreeRequestVo();
                appletreeRequestVo.setACSURL(ACSURL);
                appletreeRequestVo.setAmount(commTransactionDetailsVO.getAmount());
                appletreeRequestVo.setAuthCode(auth);
                appletreeRequestVo.setBackgroundColour(BackgroundColour);
                appletreeRequestVo.setCardNo(cardno);
                appletreeRequestVo.setCountryCode(countryCode);
                appletreeRequestVo.setCreationDateTime(CreationDateTime);
                appletreeRequestVo.setCurrency(commTransactionDetailsVO.getCurrency());
                appletreeRequestVo.setCVV(CVV);
                appletreeRequestVo.setEmailAddress(EmailAddress);
                appletreeRequestVo.setExpiryDateTime(ExpiryDateTime);
                appletreeRequestVo.setExpMonth(ExpMonth);
                appletreeRequestVo.setExpYear(ExpYear);
                appletreeRequestVo.setFirstName(FirstName);
                appletreeRequestVo.setLastName(LastName);
                appletreeRequestVo.setLocality(Locality);
                appletreeRequestVo.setMobileNumber(MobileNumber);
                appletreeRequestVo.setOptionalCode(OptionalCode);
                //appletreeRequestVo.setOrderid(commTransactionDetailsVO.getOrderId());
                appletreeRequestVo.setOrderid(commTransactionDetailsVO.getOrderDesc());
                appletreeRequestVo.setPAReq(PAReq);
                appletreeRequestVo.setRequestId(RequestId);
                appletreeRequestVo.setReturnURL(ReturnURL+"_"+OptionalCode);
                //appletreeRequestVo.setReturnURL(ReturnURL);
                appletreeRequestVo.setPARes(PARes);
                appletreeRequestVo.setServiceCode(FinancialServiceName);
                appletreeRequestVo.setStreetAddress(StreetAddress);
                appletreeRequestVo.setPostalcode(zip);
                appletreeRequestVo.setState(state);
                appletreeRequestVo.setXID(XID);
                /*if(FinancialServiceName.equalsIgnoreCase("REDBOXX")){*/
                if(ServiceCode.equalsIgnoreCase("GiftCardAT")){
                    appletreeRequestVo.setProcessingStage("1");
                    appletreeRequestVo.setMerchantId(MerchantId);
                    appletreeRequestVo.setStoreId(StoreId);
                    appletreeRequestVo.setTerminalId(TerminalId);
                }else{
                    appletreeRequestVo.setProcessingStage("1");
                }

                StringBuffer InitialRequestLog  = AppleTreeCellulantUtils.InitialRequestLog(appletreeRequestVo);
                StringBuffer InitialRequest     = AppleTreeCellulantUtils.InitialRequest(appletreeRequestVo);

                transactionLogger.error("SaleURL ----For " + trackingID + " " + SaleURL);
                transactionLogger.error("InitialRequestLog ----For " + trackingID + " " + InitialRequestLog.toString());

                String InitialResponse  = null;
                InitialResponse         = AppleTreeCellulantUtils.doPostHTTPSURLConnectionClient(SaleURL, InitialRequest.toString(), InitialSignature, Mid, encodedPassword);
                transactionLogger.error("InitialResponse ----For " + trackingID + " " + InitialResponse.toString());

                if (functions.isValueNull(InitialResponse) && AppleTreeCellulantUtils.isJSONValid(InitialResponse))
                {
                    JSONObject jsonObject       = new JSONObject(InitialResponse);
                    JSONObject RequestDetails   = null;
                    JSONObject ResultDetails   = null;
                    JSONObject PayerAuthenticationDetails   = null;
                    if (jsonObject.has("RequestDetails") && functions.isValueNull(jsonObject.getString("RequestDetails")))
                    {
                        RequestDetails = jsonObject.getJSONObject("RequestDetails");

                        if(RequestDetails != null){
                            if (RequestDetails.has("Id")  && functions.isValueNull(RequestDetails.getString("Id")))
                            {
                                Paymentid = RequestDetails.getString("Id");
                            }
                        }
                    }


                    if (jsonObject.has("ResultDetails") && functions.isValueNull(jsonObject.getString("ResultDetails")))
                    {
                        ResultDetails = jsonObject.getJSONObject("ResultDetails");

                        if(RequestDetails != null){
                            if (RequestDetails.has("Id")  && functions.isValueNull(RequestDetails.getString("Id")))
                            {
                                Paymentid = RequestDetails.getString("Id");
                            }

                            if (ResultDetails.has("Status") && functions.isValueNull(ResultDetails.getString("Status")))
                            {
                                Status = ResultDetails.getString("Status");
                            }
                            if (ResultDetails.has("ResultMessage") && functions.isValueNull(ResultDetails.getString("ResultMessage")))
                            {
                                message = ResultDetails.getString("ResultMessage");
                            }
                            if (ResultDetails.has("ReferenceNumber") && functions.isValueNull(ResultDetails.getString("ReferenceNumber")))
                            {
                                referenceNumber = jsonObject.getJSONObject("ResultDetails").getString("ReferenceNumber");
                            }
                            if (ResultDetails.has("PaymentCompletionEmbedCode") && functions.isValueNull(ResultDetails.getString("PaymentCompletionEmbedCode")))
                            {
                                EmbededUrl = ResultDetails.getString("PaymentCompletionEmbedCode");
                            }
                            if (ResultDetails.has("PaymentCompletionURL") && functions.isValueNull(ResultDetails.getString("PaymentCompletionURL")))
                            {
                                PaymentCompletionURL = ResultDetails.getString("PaymentCompletionURL");
                            }
                            if (ResultDetails.has("PaymentCompletionInstructions") && functions.isValueNull(ResultDetails.getString("PaymentCompletionInstructions")))
                            {
                                PaymentCompletionInstructions = ResultDetails.getString("PaymentCompletionInstructions");
                            }
                        }

                    }

                    if (jsonObject.has("PayerAuthenticationDetails") && functions.isValueNull(jsonObject.getString("PayerAuthenticationDetails")))
                    {
                        PayerAuthenticationDetails = jsonObject.getJSONObject("PayerAuthenticationDetails");
                        transactionLogger.error("Payer JSON ---> "+PayerAuthenticationDetails);
                        if(PayerAuthenticationDetails != null)
                        {
                            if (PayerAuthenticationDetails.has("AuthenticationId") && functions.isValueNull(PayerAuthenticationDetails.getString("AuthenticationId")))
                            {
                                auth = PayerAuthenticationDetails.getString("AuthenticationId");
                            }
                            if ( PayerAuthenticationDetails.has("XID") && functions.isValueNull(PayerAuthenticationDetails.getString("XID")))
                            {
                                XID = PayerAuthenticationDetails.getString("XID");
                            }
                            if (PayerAuthenticationDetails.has("ACSURL") &&  functions.isValueNull(PayerAuthenticationDetails.getString("ACSURL")))
                            {
                                ACSURL = PayerAuthenticationDetails.getString("ACSURL");
                            }
                            if (PayerAuthenticationDetails.has("PAReq") && functions.isValueNull(PayerAuthenticationDetails.getString("PAReq")))
                            {
                                PAReq = PayerAuthenticationDetails.getString("PAReq");
                            }
                            if (PayerAuthenticationDetails.has("PAReq") && functions.isValueNull(PayerAuthenticationDetails.getString("PARes")))
                            {
                                PARes = PayerAuthenticationDetails.getString("PARes");
                            }
                            if (PayerAuthenticationDetails.has("MD") && functions.isValueNull(PayerAuthenticationDetails.getString("MD")))
                            {
                                MD = PayerAuthenticationDetails.getString("MD");
                            }
                            if (PayerAuthenticationDetails.has("TermUrl") && functions.isValueNull(PayerAuthenticationDetails.getString("TermUrl")))
                            {
                                TermUrl = PayerAuthenticationDetails.getString("TermUrl");
                            }
                        }
                    }


                    if (Status.equalsIgnoreCase("PAID"))
                    {
                        transactionStatus   = "success";
                        Description         = message;
                        Remark              = Status;
                    }
                    else if (Status.equalsIgnoreCase("INITIATED"))
                    {
                        transactionStatus   = "pending";
                        comm3DResponseVO.setRedirectUrl(EmbededUrl);
                        Description         = message;

                        if (functions.isValueNull(EmbededUrl))
                        {
                            Remark = message;
                        }
                        else
                        {
                            //if (functions.isValueNull(PaymentCompletionInstructions) &&
                            if (PaymentType.equalsIgnoreCase("BankTransferAfrica") ||
                                     PaymentType.equalsIgnoreCase("MobileMoneyAfrica") || PaymentType.equalsIgnoreCase("WalletAfrica"))
                            {
                                comm3DResponseVO.setUrlFor3DRedirect(ReturnURL);
                                if(functions.isValueNull(PaymentCompletionInstructions)){
                                    Remark = PaymentCompletionInstructions;
                                }

                            }else if(functions.isValueNull(PaymentCompletionInstructions)){
                                String msg          = PaymentCompletionInstructions.substring(3);
                                String[] sentences  = msg.split("<b>");
                                Remark              = Status + ": " + sentences[0];
                            }
                            else
                            {
                                Remark = message;
                            }
                        }
                        comm3DResponseVO.setDescription(Description);
                        comm3DResponseVO.setRemark(Remark);
                    }
                    else if (Status.equalsIgnoreCase("PENDINGAUTHENTICATION"))
                    {
                        transactionStatus = "pending3DConfirmation";
                        comm3DResponseVO.setUrlFor3DRedirect(ACSURL);
                        //comm3DResponseVO.setMd(XID);
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setAuthCode(auth);
                        String enData = PzEncryptor.encryptCVV(commCardDetailsVO.getcVV());

                        if(functions.isValueNull(TermUrl)){
                            comm3DResponseVO.setTerURL(TermUrl);
                        }else{
                            //comm3DResponseVO.setTerURL(ReturnURL + "&AuthCode=" + auth + "&OPcode=" + OptionalCode + "&data=" + ESAPI.encoder().encodeForURL(enData) + "&pareq=" + PAReq);
                            comm3DResponseVO.setTerURL(ReturnURL + "_" + OptionalCode);
                        }

                        if(functions.isValueNull(MD)){
                            comm3DResponseVO.setMd(MD);
                        }else{
                            comm3DResponseVO.setMd(RequestId);
                        }

                        comm3DResponseVO.setOptionalCode(OptionalCode);
                    }
                    else if (Status.equalsIgnoreCase("FAILED") || Status.equalsIgnoreCase("FAILEDREPEATABLE"))
                    {
                        transactionStatus = "failed";
                        if (functions.isValueNull(message))
                        {
                            Remark = message;
                        }
                        else
                        {
                            Remark = PaymentCompletionURL;
                        }
                        Description = transactionStatus + " Transaction Declined";
                    }
                    else
                    {
                        transactionStatus   = "failed";
                        Description         = message;
                        Remark              = message;
                        Description = transactionStatus + " Transaction Declined";
                    }
                    transactionLogger.error("Remark ------------------> "+Remark);
                    transactionLogger.error("Description ------------------> "+Description);
                    transactionLogger.error("transactionStatus ------------------> "+transactionStatus);
                    comm3DResponseVO.setStatus(transactionStatus);
                    comm3DResponseVO.setTransactionId(Paymentid);
                    comm3DResponseVO.setDescription(Description);
                    comm3DResponseVO.setRemark(Remark);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    comm3DResponseVO.setRrn(referenceNumber);
                    comm3DResponseVO.setAmount(Amount);
                }
                else
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setRemark("Invalid Response");
                    comm3DResponseVO.setDescription("Invalid Response");

                }
            }
            else
            {
                comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setRemark("Invalid Service Code");
                comm3DResponseVO.setDescription("Invalid Service Code");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("EncodingException---" + trackingID + "-->", e);
        }

        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws
            PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AppleTreeCellulantPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions             = new Functions();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                  = gatewayAccount.isTest();
        String mid                  = gatewayAccount.getMerchantId();
        String password             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String encodedPassword      = "";
        String transactionStatus    = "";
        String message              = "";
        String Paymentid            = "";
        String Status               = "";
        String referenceNumber      = "";
        String Description          = "";
        String Remark               = "";
        if (functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
        String privatePath  = "";
        String inquiryUrl   = "";
        if (isTest)
        {
            inquiryUrl  = RB.getString("INQUIRY_TEST_URL");
            privatePath = RB.getString("TEST_PRIVATE_KEY");
        }
        else
        {
            inquiryUrl  = RB.getString("INQUIRY_LIVE_URL");
            privatePath = RB.getString("LIVE_PRIVATE_KEY");
        }
        try
        {
            transactionLogger.error("trackingID ----" + trackingID + "-->" + commTransactionDetailsVO.getPreviousTransactionId());
            /*if (functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionId()))
            {*/
                String authenticationHash = AppleTreeCellulantUtils.getSignature(mid + commTransactionDetailsVO.getPreviousTransactionId(), privatePath);
                String request = "{" +
                        " \"PaymentRequestId\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"" +
                        "}";

                transactionLogger.error("processQuery Request ----" + trackingID + "-->" + request);
                String response = AppleTreeCellulantUtils.doPostHTTPSURLConnectionClient(inquiryUrl, request, authenticationHash, mid, encodedPassword);
                transactionLogger.error("processQuery Response ----" + trackingID + "-->" + response);
                if (functions.isValueNull(response) && AppleTreeCellulantUtils.isJSONValid(response))
                {
                    JSONObject jsonObject       = new JSONObject(response);
                    JSONObject RequestDetails   = null;
                    JSONObject ResultDetails   = null;

                    if (jsonObject.has("RequestDetails") && functions.isValueNull(jsonObject.getString("RequestDetails")))
                    {
                        RequestDetails = jsonObject.getJSONObject("RequestDetails");
                        if(RequestDetails != null){
                            if (RequestDetails.has("Id") && functions.isValueNull(RequestDetails.getString("Id")))
                            {
                                Paymentid = RequestDetails.getString("Id");
                            }
                        }

                    }

                    if (jsonObject.has("ResultDetails") && functions.isValueNull(jsonObject.getString("ResultDetails")))
                    {
                        ResultDetails = jsonObject.getJSONObject("ResultDetails");
                        if(ResultDetails != null){
                            if (ResultDetails.has("Status") &&  functions.isValueNull(ResultDetails.getString("Status")))
                            {
                                Status = ResultDetails.getString("Status");
                            }
                            if (ResultDetails.has("ResultMessage") && functions.isValueNull(ResultDetails.getString("ResultMessage")))
                            {
                                message = ResultDetails.getString("ResultMessage");
                            }
                            if (ResultDetails.has("ReferenceNumber") && functions.isValueNull(ResultDetails.getString("ReferenceNumber")))
                            {
                                referenceNumber = ResultDetails.getString("ReferenceNumber");
                            }
                        }

                    }
                    transactionLogger.error("Status::::::::::::::" + Status);
                    if (Status.equals("PAID"))
                    {
                        transactionStatus   = "success";
                        Description         = message;
                        Remark              = message;
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if (Status.equals("INITIATED"))
                    {
                        transactionStatus   = "pending";
                        Description         = message;
                        Remark              = message;
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if (Status.equals("PENDINGAUTHENTICATION"))
                    {
                        transactionStatus   = "pending";
                        Description         = message;
                        Remark              = message;
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if (Status.equals("REVERSED"))
                    {
                        transactionStatus   = "reversed";
                        Description         = message;
                        Remark              = message;
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                    }
                    else
                    {
                        transactionStatus   = "failed";
                        transactionStatus   = "failed";
                        Description         = message;
                        Remark              = message;
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    commResponseVO.setStatus(transactionStatus);
                    commResponseVO.setTransactionStatus(transactionStatus);
                    commResponseVO.setTransactionId(Paymentid);
                    commResponseVO.setDescription(Description);
                    commResponseVO.setRemark(Remark);
                    commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setRrn(referenceNumber);
                }
                else
                {
                    /*commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Invalid Response");
                    commResponseVO.setDescription("Invalid Response");*/
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");

                }
                commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            /*}*/
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO                       = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions             = new Functions();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        String mid       = gatewayAccount.getMerchantId();
        String password  = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest   = gatewayAccount.isTest();
        String refundUrl            = "";
        String transactionStatus    = "";
        String message              = "";
        String referenceNumber      = "";
        String privatePath          = "";
        String Status               = "";
        String Description          = "";
        String Remark       = "";
        String Paymentid    = "";

        try
        {
            if (isTest)
            {
                refundUrl   = RB.getString("REFUND_TEST_URL");
                privatePath = RB.getString("TEST_PRIVATE_KEY");
            }
            else
            {
                refundUrl   = RB.getString("REFUND_LIVE_URL");
                privatePath = RB.getString("LIVE_PRIVATE_KEY");
            }
            String encodedPassword = "";
            if (functions.isValueNull(password))
                encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());

            String authenticationHash = AppleTreeCellulantUtils.getSignature(mid + commTransactionDetailsVO.getPreviousTransactionId(), privatePath);
            String refundRequest = "{" +
                    " \"PaymentRequestId\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"" +
                    "}";
            transactionLogger.error("Refund request---" + trackingID + "--->" + refundRequest);
            String response = AppleTreeCellulantUtils.doPostHTTPSURLConnectionClient(refundUrl, refundRequest, authenticationHash, mid, encodedPassword);
            transactionLogger.error("Refund response--" + trackingID + "--->" + response);
            if (functions.isValueNull(response) && AppleTreeCellulantUtils.isJSONValid(response))
            {
                JSONObject jsonObject       = new JSONObject(response);
                JSONObject RequestDetails   = null;
                JSONObject ResultDetails   = null;

                if (jsonObject.has("RequestDetails") && functions.isValueNull(jsonObject.getString("RequestDetails")))
                {
                    RequestDetails = jsonObject.getJSONObject("RequestDetails");
                    if (RequestDetails.has("Id") && functions.isValueNull(RequestDetails.getString("Id")))
                    {
                        Paymentid = RequestDetails.getString("Id");
                    }
                }
                if (jsonObject.has("ResultDetails") && functions.isValueNull(jsonObject.getString("ResultDetails")))
                {
                    ResultDetails = jsonObject.getJSONObject("ResultDetails");

                    if(ResultDetails != null){
                        if (ResultDetails.has("Status") && functions.isValueNull(ResultDetails.getString("Status")))
                        {
                            Status = ResultDetails.getString("Status");
                        }
                        if (ResultDetails.has("ResultMessage") && functions.isValueNull(ResultDetails.getString("ResultMessage")))
                        {
                            message = ResultDetails.getString("ResultMessage");
                        }
                        if (ResultDetails.has("ReferenceNumber") &&  functions.isValueNull(ResultDetails.getString("ReferenceNumber")))
                        {
                            referenceNumber = ResultDetails.getString("ReferenceNumber");
                        }
                    }
                }
                transactionLogger.error("Status::::::::::::::" + Status);
                if (Status.equals("REVERSED"))
                {
                    transactionStatus = "success";
                    Description = Status;
                    Remark = message;
                }
                else
                {
                    transactionStatus = "failed";
                    Description = Status;
                    Remark = message;
                }
                commResponseVO.setStatus(transactionStatus);
                commResponseVO.setTransactionStatus(Status);
                commResponseVO.setTransactionId(Paymentid);
                commResponseVO.setDescription(Description);
                commResponseVO.setRemark(Remark);
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setRrn(referenceNumber);
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");

            }
            commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }


        return commResponseVO;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DSaleConfirmation-----");
        Comm3DRequestVO comm3DRequestVO                     = (Comm3DRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = comm3DRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = comm3DRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = comm3DRequestVO.getCardDetailsVO();
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        Functions functions                                 = new Functions();
        CommMerchantVO commMerchantVO = comm3DRequestVO.getCommMerchantVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String Mid              = gatewayAccount.getMerchantId();
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String encodedPassword  = "";
        if (functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
        boolean isTest = gatewayAccount.isTest();

        long expirationMin  = 5;
        String ExpiredTime  = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try
        {
            if (functions.isValueNull(ExpiredTime))
                expirationMin = (long) Double.parseDouble(ExpiredTime.trim());
        }
        catch (NumberFormatException e)
        {
            transactionLogger.error("NumberFormatException--" + trackingID + "-->", e);
        }
        Date date               = new Date();
        String CreationDateTime     = simpleDateFormat.format(date);
        long threadSleepTime        = expirationMin * 60 * 1000;
        long time                   = date.getTime() + (expirationMin * 60 * 1000);
        date    = new Date(time);
        String ExpiryDateTime = simpleDateFormat.format(date);

        String privatePath = "";
        String MobileNumber = "";
        String EmailAddress = "";
        String StreetAddress = "";
        String countryCode = "";
        String Locality = "";
        String transactionStatus = "";
        String message = "";
        String referenceNumber = "";
        String Description = "";
        String Remark = "";
        String Status = "";
        String state = "";
        String zip = "";
        String paymentRequestId = commTransactionDetailsVO.getPreviousTransactionId();

        if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            MobileNumber = commAddressDetailsVO.getPhone();
        if (functions.isValueNull(commAddressDetailsVO.getEmail()))
            EmailAddress = commAddressDetailsVO.getEmail();
        if (functions.isValueNull(commAddressDetailsVO.getStreet()))
            StreetAddress = commAddressDetailsVO.getStreet();
        if (functions.isValueNull(commAddressDetailsVO.getState()))
            state = commAddressDetailsVO.getState();
        if (functions.isValueNull(commAddressDetailsVO.getZipCode()))
            zip = commAddressDetailsVO.getZipCode();
        if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            countryCode = commAddressDetailsVO.getCountry();
        if (countryCode.length() == 3)
            countryCode = AppleTreeCellulantUtils.getCountryTwoDigit(countryCode);
        if (functions.isValueNull(commAddressDetailsVO.getCity()))
            Locality = commAddressDetailsVO.getCity();

        String ServiceCode = commCardDetailsVO.getCardType();
        if (ServiceCode.equals("MC"))
        {
            ServiceCode = "MasterCard";
        }else if (ServiceCode.equals("Visa")){
            ServiceCode = "VISA";
        }
        String FirstName = new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        String LastName = new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);

        String PaRes            = comm3DRequestVO.getPaRes();
        String XID              = comm3DRequestVO.getMd();
        String Authcode         = comm3DRequestVO.getAuthCode();
        String Optionalcode     = comm3DRequestVO.getOptionalCode();
        String rrn          = "";
        String ThreeDUrl    = "";
        String PaReq        = comm3DRequestVO.getPaReq();


        if (isTest)
        {
            ThreeDUrl = RB.getString("INIT_TEST_URL");
            privatePath = RB.getString("TEST_PRIVATE_KEY");
        }
        else
        {
            ThreeDUrl = RB.getString("INIT_LIVE_URL");
            privatePath = RB.getString("LIVE_PRIVATE_KEY");
        }

        String ReturnURL = "";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            ReturnURL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            transactionLogger.error("From HOST_URL----" + ReturnURL);
        }
        else
        {
            ReturnURL = RB.getString("TERM_URL") + trackingID;
            transactionLogger.error("From RB TERM_URL ----" + ReturnURL);
        }

        try
        {
            AppletreeRequestVo appletreeRequestVo = new AppletreeRequestVo();
            appletreeRequestVo.setACSURL(null);
            appletreeRequestVo.setAmount(commTransactionDetailsVO.getAmount());
            appletreeRequestVo.setAuthCode(Authcode);
            appletreeRequestVo.setBackgroundColour("");
            appletreeRequestVo.setCardNo(commCardDetailsVO.getCardNum());
            appletreeRequestVo.setCountryCode(countryCode);
            appletreeRequestVo.setCreationDateTime(CreationDateTime);
            appletreeRequestVo.setCurrency(commTransactionDetailsVO.getCurrency());
            appletreeRequestVo.setCVV(commCardDetailsVO.getcVV());
            appletreeRequestVo.setEmailAddress(EmailAddress);
            appletreeRequestVo.setExpiryDateTime(ExpiryDateTime);
            appletreeRequestVo.setExpMonth(commCardDetailsVO.getExpMonth());
            appletreeRequestVo.setExpYear(commCardDetailsVO.getExpYear());
            appletreeRequestVo.setFirstName(FirstName);
            appletreeRequestVo.setLastName(LastName);
            appletreeRequestVo.setLocality(Locality);
            appletreeRequestVo.setMobileNumber(MobileNumber);
            appletreeRequestVo.setOptionalCode(comm3DRequestVO.getOptionalCode());
            appletreeRequestVo.setOrderid(commTransactionDetailsVO.getOrderDesc());
            //appletreeRequestVo.setOrderid(trackingID);
            appletreeRequestVo.setPAReq(PaReq);
            appletreeRequestVo.setRequestId(paymentRequestId);
            appletreeRequestVo.setReturnURL(ReturnURL);
            appletreeRequestVo.setPARes(PaRes);
            appletreeRequestVo.setServiceCode(ServiceCode);
            appletreeRequestVo.setStreetAddress(StreetAddress);
            appletreeRequestVo.setPostalcode(zip);
            appletreeRequestVo.setState(state);
            appletreeRequestVo.setXID(XID);
            //appletreeRequestVo.setProcessingStage("3");
            appletreeRequestVo.setProcessingStage("2");

            String InitialSignature             = AppleTreeCellulantUtils.getSignature(Mid + paymentRequestId + commTransactionDetailsVO.getCurrency() + commTransactionDetailsVO.getAmount(), privatePath);
            StringBuffer InitialRequestLog      = AppleTreeCellulantUtils.Initial3dRequestLog(appletreeRequestVo);
            StringBuffer InitialRequest         = AppleTreeCellulantUtils.Initial3dRequest(appletreeRequestVo);
            //transactionLogger.error("Initial3DRequestLog ----For " + trackingID + " " + InitialRequestLog.toString());
            transactionLogger.error("Initial3DRequestLog ----For " + trackingID + " " + InitialRequestLog.toString());
            String process3DResponse = null;
            process3DResponse = AppleTreeCellulantUtils.doPostHTTPSURLConnectionClient(ThreeDUrl, InitialRequest.toString(), InitialSignature, Mid, encodedPassword);
            transactionLogger.error("Initial3DResponse ----For " + trackingID + " " + process3DResponse.toString());

            if (functions.isValueNull(process3DResponse) && AppleTreeCellulantUtils.isJSONValid(process3DResponse))
            {
                JSONObject jsonObject = new JSONObject(process3DResponse);

                if (jsonObject.has("RequestDetails") && functions.isValueNull(jsonObject.getString("RequestDetails")))
                {
                    if (functions.isValueNull(jsonObject.getJSONObject("RequestDetails").getString("Id")))
                    {
                        paymentRequestId = jsonObject.getJSONObject("RequestDetails").getString("Id");
                    }
                }
                if (jsonObject.has("ResultDetails") && functions.isValueNull(jsonObject.getString("ResultDetails")))
                {
                    JSONObject resultDetailsJONS = jsonObject.getJSONObject("ResultDetails");

                    if (resultDetailsJONS.has("Status"))
                    {
                        Status = resultDetailsJONS.getString("Status");
                    }
                    if (resultDetailsJONS.has("ResultMessage"))
                    {
                        message = resultDetailsJONS.getString("ResultMessage");
                    }
                    if (resultDetailsJONS.has("ReferenceNumber"))
                    {
                        referenceNumber = resultDetailsJONS.getString("ReferenceNumber");
                    }
                }
                transactionLogger.error("Status::::::::::::::" + Status);
                if (Status.equals("PAID"))
                {
                    transactionStatus = "success";
                    Description = message;
                    Remark = Status;
                }
                else
                {
                    transactionStatus = "failed";
                    Description = message;
                    Remark = Status;
                }
                transactionLogger.error("Status::::::::::::::" + transactionStatus);

                commResponseVO.setStatus(transactionStatus);
                commResponseVO.setTransactionId(paymentRequestId);
                commResponseVO.setDescription(Description);
                commResponseVO.setRemark(Remark);
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setRrn(referenceNumber);
                commResponseVO.setAuthCode(Authcode);
            }
            else
            {
               /* commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");*/
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");

            }
            commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }
        return commResponseVO;
    }


    public HashMap<String,ArrayList<TerminalVO>> getPaymentType(String currency,String country)
    {
        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions                 = new Functions();
        boolean isTest          = gatewayAccount.isTest();
        String Mid              = gatewayAccount.getMerchantId();
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        UUID uuid1              = UUID.randomUUID();
        String RequestId        = uuid1.toString();
        String encodedPassword  = "";
        if (functions.isValueNull(password)){
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
        }
        String countryCode              = "";
        String paymentoptionsURL        = "";
        String privatePath              = "";
        HashMap<String,ArrayList<TerminalVO>> arrayListHashMap = new HashMap<>();
        try
        {
            if (isTest)
            {
                paymentoptionsURL   = RB.getString("PR_TEST_URL");
                privatePath         = RB.getString("TEST_PRIVATE_KEY");
            }
            else
            {
                paymentoptionsURL   = RB.getString("PR_LIVE_URL");
                privatePath         = RB.getString("LIVE_PRIVATE_KEY");
            }

            String PaymentOptionsSignature  = AppleTreeCellulantUtils.getSignature(Mid + RequestId, privatePath);

            if (functions.isValueNull(country))
                countryCode = country;
            if (country.length() == 3)
                countryCode = AppleTreeCellulantUtils.getCountryTwoDigit(country);

            StringBuffer PaymentOptionsRequest = new StringBuffer();

            PaymentOptionsRequest.append("{" +
                    "\"PaymentRequestId\":\"" + RequestId + "\"," +
                    "\"OrderCurrency\":\"" + currency + "\"," +
                    "\"Country\":\"" + countryCode + "\"" +
                    "}");

            transactionLogger.error("paymentoptionsURL ---->" + paymentoptionsURL);
            transactionLogger.error("PaymentOptionsRequest ---->" + PaymentOptionsRequest.toString());
            String PaymentOptionsResponse   = null;
            PaymentOptionsResponse          = AppleTreeCellulantUtils.doPostHTTPSURLConnectionClient(paymentoptionsURL, PaymentOptionsRequest.toString(), PaymentOptionsSignature, Mid, encodedPassword);
            transactionLogger.error("PaymentOptionsResponse ----> " + PaymentOptionsResponse.toString());

            if (functions.isValueNull(PaymentOptionsResponse) && PaymentOptionsResponse.substring(0, 1).equals("{"))
            {
                JSONObject initResponseJSON = new JSONObject(PaymentOptionsResponse);
                if (initResponseJSON.has("PaymentOptions") && functions.isValueNull(initResponseJSON.getString("PaymentOptions")))
                {
                    JSONArray jsonArray = initResponseJSON.getJSONArray("PaymentOptions");
                    int length          = jsonArray.length();

                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject       = jsonArray.getJSONObject(i);

                        String paymentMethod           = jsonObject.getString("PaymentMethod");
                        String financialServiceName    = jsonObject.getString("FinancialServiceName");
                        String paymentOtionCode        = jsonObject.getString("Code");
                        String FinancialServiceLogoURL = jsonObject.getString("FinancialServiceLogoURL");
                        String Instructions             = jsonObject.getString("Instructions");
                        String paymentType             = AppleTreeCellulantUtils.getPaymentMode(paymentMethod);

                        if(!arrayListHashMap.containsKey(paymentType)){
                            ArrayList<TerminalVO> serviceNameList = new ArrayList<>();

                            TerminalVO terminalVO = new TerminalVO();

                            terminalVO.setPaymentTypeName(paymentType);
                            terminalVO.setCardType(financialServiceName);
                            terminalVO.setCardTypeId(paymentOtionCode);
                            terminalVO.setDisplayName(FinancialServiceLogoURL);
                            terminalVO.setAddressDetails(Instructions);

                            serviceNameList.add(terminalVO);

                            arrayListHashMap.put(paymentType,serviceNameList);
                        }else{
                            ArrayList<TerminalVO> serviceNameList = arrayListHashMap.get(paymentType);
                            TerminalVO terminalVO               = new TerminalVO();
                            transactionLogger.error("paymentType "+paymentType);

                            terminalVO.setPaymentTypeName(paymentType);
                            terminalVO.setCardType(financialServiceName);
                            terminalVO.setCardTypeId(paymentOtionCode);
                            terminalVO.setDisplayName(FinancialServiceLogoURL);
                            terminalVO.setAddressDetails(Instructions);

                            serviceNameList.add(terminalVO);

                            arrayListHashMap.put(paymentType,serviceNameList);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException----->", e);
        }

        return arrayListHashMap;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
