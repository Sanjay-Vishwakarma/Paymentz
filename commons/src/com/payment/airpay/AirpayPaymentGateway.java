package com.payment.airpay;

import com.directi.pg.AirPayTransactionLogger;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
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
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 10/17/2021.
 */

public class AirpayPaymentGateway extends AbstractPaymentGateway
{
    //private static TransactionLogger transactionlogger = new TransactionLogger(AirpayPaymentGateway.class.getName());
    private static AirPayTransactionLogger transactionlogger = new AirPayTransactionLogger(AirpayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE     = "airpay";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.airpay");
    private final static ResourceBundle RB_NB   = LoadProperties.getProperty("com.directi.pg.ARPBANKS");
    public AirpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("AirpayPaymentGateway  accountid------------------->" + accountId);
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AirpayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of AirpayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        AirpayUtils airpayUtils                         = new AirpayUtils();

        String sMerId    = gatewayAccount.getMerchantId();
        String sUserName = gatewayAccount.getFRAUD_FTP_USERNAME();;
        String sPassword = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String sSecret   = gatewayAccount.getFRAUD_FTP_PATH();

        String chmod       = "";
        String sMode       = "";
        String sCustVar    = "";
        String sEmail      = "";
        String sPhone      = "" ;
        String sFName      = "";
        String sLName      = "";
        String sAddress    = "";
        String sCity       = "";
        String sState      = "";
        String sPincode    = "";
        String customvar   = "";
        String sTxnSubType = "";//2

        String sAmount    = transactionDetailsVO.getAmount();
        String sCountry   = "";
        String sOrderId   = trackingID;
        String walletflg  = trackingID;
        String UID        = trackingID;

        String payment_Card = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String channel_mode  = airpayUtils.getPaymentType(payment_Card);

        boolean isTest  = gatewayAccount.isTest();
        DateFormat df   = new SimpleDateFormat("yyyy-MM-dd");
        String sCurDate = df.format(new Date());

        String currency      = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        String isoCurrency   = transactionDetailsVO.getCurrency();
        String payment_brand = transactionDetailsVO.getCardType();
        //UPI
        String payer_vpa    = commRequestVO.getCustomerId();
        String submode      = "vpa";
        String action       = "upi_vpa";
        String apiName      = "collectVPA";
        //Card
        String  CARD_NUMBER = commCardDetailsVO.getCardNum();
        String  ExpMonth    = commCardDetailsVO.getExpMonth();
        String  ExpYear     = commCardDetailsVO.getExpYear();
        String  CVV         = commCardDetailsVO.getcVV();

        String checksum     = "";
        String   sAllData   = "";
        String privateKey   = "";
        String REQUEST_URL  = "";
        Map<String, String> requestS2SMap = new HashMap<String, String>();

        try
        {

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
            {
                sFName = commAddressDetailsVO.getFirstname();
            }
            else{
                sFName = "Customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                sLName =  commAddressDetailsVO.getLastname();
            }
            else{
                sLName = "Customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                sEmail = commAddressDetailsVO.getEmail();
            }
            else{
                sEmail = "customer@gmail.com";
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    sPhone = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                if(sPhone.length()>10){

                    sPhone  = sPhone.substring(sPhone.length() - 10);
                }
                else{
                    sPhone = commAddressDetailsVO.getPhone();
                }
            }
            else{
                sPhone = "9999999999";
            }

            if(functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                sCountry = commAddressDetailsVO.getCountry();
            }
            else{
                sCountry = "India";
            }

            if(functions.isValueNull(commAddressDetailsVO.getStreet()))
            {
                sAddress = commAddressDetailsVO.getStreet();
            }
            else{
                sAddress = "Rajiv Chowk";
            }

            if(functions.isValueNull(commAddressDetailsVO.getCity()))
            {
                sCity = commAddressDetailsVO.getCity();
            }
            else{
                sCity = "Delhi";
            }
            if(functions.isValueNull(commAddressDetailsVO.getState()))
            {
                sState = commAddressDetailsVO.getState();
            }
            else{
                sState = "New Delhi";
            }

            if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
            {
                sPincode = commAddressDetailsVO.getZipCode();
            }
            else{
                sPincode = "110017";
            }

            if (isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            String sTemp = sSecret+"@"+sUserName+":|:"+sPassword;
            privateKey   = AirpayUtils.getPrivateKey(sTemp);
            sAllData     = sEmail + sFName + sLName + sAddress + sCity + sState + sCountry + sAmount + sOrderId +  sCurDate;
            sAllData     = sAllData + privateKey;
            checksum     = AirpayUtils.getChecksum(sAllData);

            requestS2SMap.put("privatekey", privateKey);
            requestS2SMap.put("checksum", checksum);
            requestS2SMap.put("mercid", sMerId);
            requestS2SMap.put("orderid", sOrderId);
            requestS2SMap.put("currency", currency);
            requestS2SMap.put("isocurrency", isoCurrency);
            requestS2SMap.put("buyerEmail", sEmail);
            requestS2SMap.put("buyerPhone", sPhone);
            requestS2SMap.put("buyerFirstName", sFName);
            requestS2SMap.put("buyerLastName", sLName);
            requestS2SMap.put("buyerAddress", sAddress);
            requestS2SMap.put("buyerCity", sCity);
            requestS2SMap.put("buyerState", sState);
            requestS2SMap.put("buyerCountry", sCountry);
            requestS2SMap.put("buyerPinCode", sPincode);
            requestS2SMap.put("amount", sAmount);
            requestS2SMap.put("chmod", channel_mode);
            requestS2SMap.put("customvar", customvar);
            requestS2SMap.put("txnsubtype", sTxnSubType);
            requestS2SMap.put("channel_mode", channel_mode);
            requestS2SMap.put("channel", channel_mode);
            requestS2SMap.put("UID", UID);
            requestS2SMap.put("walletflg", walletflg);
            requestS2SMap.put("transaction_id", sOrderId);

            if("pg".equalsIgnoreCase(channel_mode)){
                //card
                requestS2SMap.put("cnum", CARD_NUMBER);
                requestS2SMap.put("cexpiry_mm", ExpMonth);
                requestS2SMap.put("cexpiry_yy", ExpYear);
                requestS2SMap.put("cvv", CVV);
            }else if("nb".equalsIgnoreCase(channel_mode)){
                //netbanking
                requestS2SMap.put("bankCode", payment_brand);
            }else if ("upi".equalsIgnoreCase(channel_mode)){
               //UPI
                requestS2SMap.put("vpa", payer_vpa);
                requestS2SMap.put("submode", submode);
                requestS2SMap.put("action", action);
                requestS2SMap.put("apiName", apiName);
            }else{
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");
                return commResponseVO ;
            }

            transactionlogger .error("requestMap "+trackingID+" "+requestS2SMap +" "+requestS2SMap.size());
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
            commResponseVO.setRequestMap(requestS2SMap);

        }
       catch (Exception e)
        {
            transactionlogger.error("Exception-------------->",e);
        }

        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredirect in AirPay ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails           = null;
        AirpayUtils airpayUtils                     = new AirpayUtils();
        CommRequestVO commRequestVO                 = airpayUtils.getAirPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);
        AirpayPaymentProcess airpayPaymentProcess   = new AirpayPaymentProcess();
        boolean isTest                              = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = airpayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect AirPay form -- >>"+html);
            }
            transactionlogger.error("paymentid--------------------->"+transRespDetails.getTransactionId());


        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in AirpayPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of AirpayPayment---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        AirpayUtils airpayUtils             = new AirpayUtils();
        Map<String, String> hashMap         = new HashMap<String,String>();
        JSONObject parameters               = new JSONObject();


        String sMerId    = gatewayAccount.getMerchantId();
        String sUserName = gatewayAccount.getFRAUD_FTP_USERNAME();;
        String sPassword = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String sSecret   = gatewayAccount.getFRAUD_FTP_PATH();

        String REQUEST_URL      = "";
        String merchant_txnId   = trackingId;
        String privateKey       = "";

        String inquiry_res = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }
            String sTemp = sSecret+"@"+sUserName+":|:"+sPassword;
            privateKey   = AirpayUtils.getPrivateKey(sTemp);

            StringBuffer requestString      =  new StringBuffer();
            requestString.append("mercid=" + sMerId);
            requestString.append("&merchant_txnId=" + merchant_txnId);
            requestString.append("&privatekey=" + privateKey);

            transactionlogger.error("inquiry is -- >"+REQUEST_URL);
            transactionlogger.error("inquiry req is --> "+trackingId+" "+requestString.toString());

            inquiry_res = airpayUtils.doGetMehtodHTTPSURLConnectionClient(REQUEST_URL,requestString.toString());
            transactionlogger.error("inquiry res is -- > "+trackingId+" "+inquiry_res);


                String status         = "";
                String authCode       = "";
                String conversionRate = "";
                String transactionId  = "";
                String txnType        = "";
                String approvedAmount = "";
                String currency       = "";
                String dateTime       = "";
                String message        = "";
                String orderId        = "";
                String RRN        = "";

                HashMap<String,String> responseObject = (HashMap<String, String>) AirpayUtils.readXMLReponse(inquiry_res);

            transactionlogger.error("Inquiry HasMap -- > "+trackingId+" "+responseObject);

                if (responseObject != null)
                {
                    if (responseObject.containsKey("APTRANSACTIONID"))
                    {
                        transactionId = responseObject.get("APTRANSACTIONID");
                    }
                    if (responseObject.containsKey("TRANSACTIONPAYMENTSTATUS"))
                    {
                        status = responseObject.get("TRANSACTIONPAYMENTSTATUS");
                    }
                    if (responseObject.containsKey("AMOUNT"))
                    {
                        approvedAmount = responseObject.get("AMOUNT");
                    }
                    if (responseObject.containsKey("AUTHCODE"))
                    {
                        authCode = responseObject.get("AUTHCODE");
                    }
                    if (responseObject.containsKey("CONVERSIONRATE"))
                    {
                        conversionRate = responseObject.get("CONVERSIONRATE");
                    }
                    if (responseObject.containsKey("TRANSACTIONTIME"))
                    {
                        dateTime = responseObject.get("TRANSACTIONTIME");
                    }

                    if (responseObject.containsKey("MESSAGE")){

                        message = responseObject.get("MESSAGE");
                    }
                    if (responseObject.containsKey("RRN")){

                        RRN = responseObject.get("RRN");
                    }

                    if ("success".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setResponseTime(dateTime);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setRrn(RRN);
                    }
                    else if("fail".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setResponseTime(dateTime);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setRrn(RRN);
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }

                    commResponseVO.setMerchantId(sMerId);
                    commResponseVO.setTransactionType(txnType);
                    commResponseVO.setErrorCode(authCode);
                    commResponseVO.setAuthCode(authCode);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }



        }catch (JSONException e)
        {
            transactionlogger.error("AirpayPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("AirpayPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside AirPay process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        AirpayUtils airpayUtils                             = new AirpayUtils();

        String hostURL          = "";
        String bankName         = "";
        String Client_id        = trackingId;
        String beneficiaryName  = "";
        String accountNumber    = "";
        String type             = "";
        String remittanceAmount = "";
        String IFSCCode         = "";
        String MobileNumber     = "9999999999";
        String api_token        = gatewayAccount.getFRAUD_FTP_PATH();
        String  HASH            = "";
        String payoutKey        = gatewayAccount.getFRAUD_FTP_SITE();
        JSONObject jsonObject   = new JSONObject();

        try
        {
            if (isTest)
            {
                hostURL  = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                String tempbeneficiaryName = commTransactionDetailsVO.getCustomerBankAccountName();

                if(tempbeneficiaryName.contains("-")||tempbeneficiaryName.contains("_")||tempbeneficiaryName.contains(",")){
                    beneficiaryName = tempbeneficiaryName.replaceAll("-","").replaceAll("_","").replaceAll(",","");
                }
                else {
                    beneficiaryName = tempbeneficiaryName;
                }

            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                accountNumber = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                IFSCCode = commTransactionDetailsVO.getBankIfsc();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                remittanceAmount = commTransactionDetailsVO.getAmount();
                remittanceAmount = String.format("%.0f", Double.parseDouble(remittanceAmount));
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                type = commTransactionDetailsVO.getBankTransferType();
            }
            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-")){
                    MobileNumber  = commAddressDetailsVO.getPhone().split("-")[1];
                }
                else{
                    MobileNumber  = commAddressDetailsVO.getPhone();
                }
            }

            bankName = beneficiaryName.trim();

            String strForHash   = MobileNumber + IFSCCode + remittanceAmount + accountNumber + bankName + Client_id + type + payoutKey;


            jsonObject.put("hash",HASH);
            jsonObject.put("api_token",api_token);
            jsonObject.put("RemittanceAmount",remittanceAmount);
            jsonObject.put("Bank",bankName);
            jsonObject.put("MobileNumber",MobileNumber);
            jsonObject.put("IFSCCode",IFSCCode);
            jsonObject.put("AccountNumber",accountNumber);
            jsonObject.put("BeneficiaryName",bankName);
            jsonObject.put("client_id",Client_id);
            jsonObject.put("type",type);

            transactionlogger.error("imoneyPayFinalRequest payout1------> trackingid---->"+trackingId + jsonObject.toString());

            String responeString = airpayUtils.doGetHTTPSURLConnectionClient(hostURL, jsonObject.toString());

            transactionlogger.error("imoneyPayFinalResponse  payout1------> trackingid---->"+trackingId+ responeString);


            String MSG               = "";
            String message           = "";
            String RRN               = "";
            String beneficiaryNam    = "";
            String transactionId     = "";
            String txtStatus         = "";

            if(functions.isValueNull(responeString) && responeString.contains("{"))
            {
                JSONObject payoutResponse = new JSONObject(responeString);

                if (payoutResponse.has("MSG") && functions.isValueNull(payoutResponse.getString("MSG")))
                {
                    MSG = payoutResponse.getString("MSG");
                }
                if (payoutResponse.has("message") && functions.isValueNull(payoutResponse.getString("message")))
                {
                    message = payoutResponse.getString("message");
                }

                if (payoutResponse.has("STATUS") && functions.isValueNull(payoutResponse.getString("STATUS")))
                {
                    txtStatus = payoutResponse.getString("STATUS");
                }
                if (payoutResponse.has("RRN") && functions.isValueNull(payoutResponse.getString("RRN")))
                {
                    RRN = payoutResponse.getString("RRN");
                }
                if (payoutResponse.has("Name") && functions.isValueNull(payoutResponse.getString("Name")))
                {
                    beneficiaryNam = payoutResponse.getString("Name");
                }

                if (txtStatus.equalsIgnoreCase("SUCCESS"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(MSG+" "+message);
                    commResponseVO.setDescription(MSG+" "+message);
                    commResponseVO.setFullname(beneficiaryNam);

                    //airpayUtils.updateRRNMainTableEntry(RRN,trackingId);
                }
                else if (txtStatus.equalsIgnoreCase("PENDING"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(MSG+" "+message);
                    commResponseVO.setDescription(MSG+" "+message);

                }
                else if (txtStatus.equalsIgnoreCase("FAILED") || txtStatus.equalsIgnoreCase("REFUNDED") )
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    if(transactionId.contains("FAIL") || transactionId.contains("Invalid")){
                        commResponseVO.setRemark(transactionId);
                        commResponseVO.setDescription(transactionId);
                    }else{
                        commResponseVO.setRemark(MSG);
                        commResponseVO.setDescription(MSG);
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
                transactionlogger.error("---transactionId--->" + transactionId + "------" + "RRN---> " + RRN);
                commResponseVO.setTransactionId(RRN);
                commResponseVO.setResponseHashInfo(RRN);
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("processPayout ----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside AirPay processPayout inquiry------->");

        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        String api_token                  = "";
        boolean isTest                    = false;
        JSONObject jsonObject             = new JSONObject();
        AirpayUtils airpayUtils             = new AirpayUtils();
        String client_id                  = trackingId;
        String hostURL                    = "";
        Functions functions               = new Functions();
        try
        {
            api_token = gatewayAccount.getFRAUD_FTP_PATH();
            client_id = trackingId;
            isTest    = gatewayAccount.isTest();
            transactionlogger.error("isTest---------->" + isTest);
            if (isTest)
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            jsonObject.put("api_token",api_token);
            jsonObject.put("client_id",client_id);

            transactionlogger.error("payoutInquiryRequest------> "+ trackingId+" " +jsonObject.toString());
            String responeString    = airpayUtils.doGetHTTPSURLConnectionClient(hostURL, jsonObject.toString());

            transactionlogger.error("payoutInquiryResponse------> "+ trackingId +" "+ responeString);
            if(functions.isValueNull(responeString))
            {
                JSONObject payoutResponse   = new JSONObject(responeString);

                String MSG           = "";
                String txnStatus     = "";
                String transactionId = "";

                if(payoutResponse.has("STATUS") && functions.isValueNull(payoutResponse.getString("STATUS"))){
                    txnStatus    = payoutResponse.getString("STATUS");
                }
                if(payoutResponse.has("MSG") && functions.isValueNull(payoutResponse.getString("MSG"))){
                    MSG = payoutResponse.getString("MSG");
                }
                if(payoutResponse.has("opid") && functions.isValueNull(payoutResponse.getString("opid"))){
                    transactionId   = payoutResponse.getString("opid");
                }

                if (txnStatus.equalsIgnoreCase("SUCCESS"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(MSG);
                    commResponseVO.setDescription(MSG);
                }
                else if (txnStatus.equalsIgnoreCase("PENDING"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(MSG);
                    commResponseVO.setDescription(MSG);
                }
                else if (txnStatus.equalsIgnoreCase("FAILED") || txnStatus.equalsIgnoreCase("REFUNDED"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    if(transactionId.contains("FAIL") || transactionId.contains("Invalid")){
                        commResponseVO.setRemark(transactionId);
                        commResponseVO.setDescription(transactionId);
                    }else{
                        commResponseVO.setRemark(MSG);
                        commResponseVO.setDescription(MSG);
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setResponseHashInfo(transactionId);
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("processPayoutInquiry JSONException-->" ,e );
        }
        return commResponseVO;
    }
}


