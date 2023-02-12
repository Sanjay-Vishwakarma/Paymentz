package com.payment.tigerpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZConstraint;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class TigerPayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(TigerPayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "tigerpay";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.tigerpay");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public TigerPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("TigerPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of TigerPayPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        TigerPayResponseVO commResponseVO               = new TigerPayResponseVO();
        Functions functions                             = new Functions();

        TigerPayUtils tigerPayUtils                     = new TigerPayUtils();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String paymentMode                              = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String cardType                                 = transactionDetailsVO.getCardType();

        boolean isTest      = gatewayAccount.isTest();
        String p_num        = gatewayAccount.getMerchantId();
        String user_name    = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password     = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String REQUEST_URL  = "";
        String RETURN_URL   = "";
        String NOTIFY_URL   = "";
        String currency         = "";
        String signature        = "";
        String apiName          = "";

        String trans_id         = trackingID;
        String amount           = transactionDetailsVO.getAmount();
        String message          = trackingID;

        LinkedHashMap<String,String> signatureHM    = new LinkedHashMap<>();

        String title        = cardType;
        String success_url  = "";
        String fail_url     = "";
        String return_url   = "";

        HashMap<String,String> requestHM  = new HashMap<>();
        String toType                =  transactionDetailsVO.getTotype();
        try
        {
            transactionlogger.error("paymentMode ---> "+trackingID +" "+paymentMode);
            transactionlogger.error("cardType ---> "+trackingID +" "+cardType);
            transactionlogger.error("toType ---> "+trackingID +" "+toType);
            if ("JPY".equalsIgnoreCase(transactionDetailsVO.getCurrency())){
                amount = TigerPayUtils.getJPYAmount(transactionDetailsVO.getAmount());
            }
            
            if(isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
                trans_id= "TRACK"+trans_id;
            }else{
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            RETURN_URL  = RB.getString("RETURN_URL");
            NOTIFY_URL  = RB.getString("NOTIFY_URL");
            success_url = RETURN_URL+trackingID+"&status=success";
            fail_url    = RETURN_URL+trackingID+"&status=fail";
            return_url  = NOTIFY_URL+trackingID;
            //trans_id    = "TRACK"+trans_id;

            currency         = transactionDetailsVO.getCurrency();

            requestHM.put("p_num",p_num);
            requestHM.put("trans_id",trans_id);
            requestHM.put("currency",currency);
            requestHM.put("amount",amount);
            requestHM.put("title",title);
            requestHM.put("success_url",success_url);
            requestHM.put("fail_url",fail_url);
            requestHM.put("return_url",return_url);

            signatureHM.put("user_name",user_name);
            signatureHM.put("password",password);
            signatureHM.put("p_num",p_num);

            signature           = tigerPayUtils.generateCheckSum(signatureHM);
            requestHM.put("signature",signature);
            transactionlogger.error("Sale signature --->  " +trackingID+ " " + signature);

            if("Facilero".equalsIgnoreCase(toType)){
               // facileroLogger.error("TigerPay Sale Request --->  " +trackingID+ " " + signatureHM);
                facileroLogger.error("TigerPay Sale Request --->  " +trackingID+ " " + requestHM);
            }else{
                //transactionlogger.error("Sale Request --->  " +trackingID+ " " + signatureHM);
                transactionlogger.error("Sale Request --->  " +trackingID+ " " + requestHM);
            }

            commResponseVO.setRequestMap(requestHM);
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);

        }
        catch (Exception e)
        {
            transactionlogger.error("JSONException TigerPayPaymentGateway ---------------> ", e);
        }

        return commResponseVO;
    }


    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in TigerPay ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        TigerPayUtils tigerPayUtils                         = new TigerPayUtils();
        TigerPayPaymentProcess tigerPayPaymentProcess = new TigerPayPaymentProcess();
        CommRequestVO commRequestVO     = tigerPayUtils.getTigerPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);

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
                html = tigerPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect tigerpay form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in TigerPayPaymentGateway---", e);
        }
        return html;
    }


    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of TigerPay---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        TigerPayUtils tigerPayUtils                             = new TigerPayUtils();

        String p_num       = gatewayAccount.getMerchantId();
        String user_name   = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password    = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String REQUEST_URL = "";
        String signature   = "";
        String trans_id    = trackingId;
        String api_name    = "settlement_result";

        LinkedHashMap<String,String> signatureHM    = new LinkedHashMap<>();
        StringBuffer reqStringBuffer = new StringBuffer();
        CommTransactionDetailsVO transactionDetailsVO   = reqVO.getTransDetailsVO();
        String toType                =  transactionDetailsVO.getTotype();
        try
        {
            transactionlogger.error("toType ---> "+trackingId +" " + toType);

            if(isTest){
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
                trans_id= "TRACK"+trans_id;

            }else{
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            reqStringBuffer.append("p_num=" + p_num);
            reqStringBuffer.append("&txs=" + trans_id);

            String combine  = api_name + trans_id ;
            signature       = tigerPayUtils.getHmacSHA512(combine, password);
            reqStringBuffer.append("&signature=" + signature);

            transactionlogger.error("trans_id ---> " + trackingId +" " + trans_id);
            transactionlogger.error("REQUEST_URL ---> " + trackingId +" " + REQUEST_URL);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerPay Sale Inquiry Request ---> "+trackingId +" " + reqStringBuffer.toString());
            }else{
                transactionlogger.error("Request String ---> "+trackingId +" " + reqStringBuffer.toString());
            }


            String responseString = tigerPayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, reqStringBuffer.toString());
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Inquiry Response ---> "+trackingId +" " +responseString);
            }else{
                transactionlogger.error("responseString ---> "+trackingId +" " +responseString);
            }


            JSONObject txsJson          = null;
            JSONObject transctionJson   = null;
            String result               = "";
            String responsResult        = "";
            String responsStatus        = "";
            String transactionId        = "";
            String responsCurrency      = "";
            String responAmount         = "";

            transactionlogger.error("responseJson ---> "+trackingId +" " +tigerPayUtils.isJSONValid(responseString));
            if(functions.isValueNull(responseString) && tigerPayUtils.isJSONValid(responseString)){

                JSONObject responseJson     = new JSONObject(responseString);
                transactionlogger.error("responseJson ---> "+trackingId +" " +responseJson);
                if(responseJson != null){

                    if(responseJson.has("txs")){
                        txsJson = responseJson.getJSONObject("txs");
                    }

                    if(responseJson.has("status"))
                    {
                        responsStatus = responseJson.getString("status");
                    }
                    if(responseJson.has("result")){
                        responsResult = responseJson.getString("result");
                    }

                    if(txsJson != null){
                        if(txsJson.has(trans_id)){
                            transctionJson = txsJson.getJSONObject(trans_id);
                        }

                        if(transctionJson != null){

                            if(transctionJson.has("transaction_number")){
                                transactionId = transctionJson.getString("transaction_number");
                            }
                            if(transctionJson.has("currency")){
                                responsCurrency = transctionJson.getString("currency");
                            }
                            if(transctionJson.has("amount")){
                                responAmount = transctionJson.getString("amount");
                            }
                        }
                    }
                    transactionlogger.error("responseJson ---> "+trackingId +" " +responsStatus + " "+responsResult);

                    if (responsStatus.equalsIgnoreCase("OK") && responsResult.equalsIgnoreCase("00") ){

                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setDescription(responsStatus);
                        commResponseVO.setRemark(responsStatus);
                        commResponseVO.setAmount(responAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setAuthCode(responsResult);
                        commResponseVO.setErrorCode(responsResult);
                    }//fail
                    else if(responsResult.equalsIgnoreCase("01") ||  responsResult.equalsIgnoreCase("02") || responsResult.equalsIgnoreCase("03")
                            || responsResult.equalsIgnoreCase("99") || responsResult.equalsIgnoreCase("90") || responsResult.equalsIgnoreCase("92")){

                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(responsStatus);
                        commResponseVO.setDescription(responsStatus);
                        commResponseVO.setAmount(responAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setAuthCode(responsResult);
                        commResponseVO.setErrorCode(responsResult);
                    }else{
                        //pending
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Transaction is Pending.");  // set inquiry response not found
                        commResponseVO.setRemark("Transaction is Pending.");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Transaction is Pending");  // set inquiry response not found
                    commResponseVO.setRemark("Transaction is Pending");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Transaction is Pending");  // set inquiry response not found
                commResponseVO.setRemark("Transaction is Pending");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            commResponseVO.setMerchantId(p_num);
            commResponseVO.setCurrency(responsCurrency);

        }catch (JSONException e)
        {
            transactionlogger.error("TigerPayPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("TigerPayPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside Tiger process payout-----");
        TigerGatePayRequestVO commRequestVO                         = (TigerGatePayRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        TigerPayUtils tigerPayUtils                         = new TigerPayUtils();

        String p_num       = gatewayAccount.getMerchantId();
        String user_name   = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String from_account  = gatewayAccount.getFRAUD_FTP_PATH();

        String REQUEST_URL          = "";


        String trans_id         = trackingId;
        String amount           = commTransactionDetailsVO.getAmount();
        String to_account       = "";
        String debit_currency   = "";
        String currency         = "";
        String signature        = "";
        LinkedHashMap<String,String> signatureHM    = new LinkedHashMap<>();
        StringBuffer reqStringBuffer = new StringBuffer();
        String toType               ="";
        try
        {
            if (isTest)
            {
                REQUEST_URL  = RB.getString("PAYOUT_TEST_URL");
                trans_id= "TRACK"+trans_id;
            }
            else
            {
                REQUEST_URL = RB.getString("PAYOUT_LIVE_URL");

            }

            if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
                toType = commTransactionDetailsVO.getTotype();
            }
            transactionlogger.error("toType ---> "+trackingId +" " + toType);

            if (commTransactionDetailsVO.getCurrency() != null && "JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency())){
                amount = TigerPayUtils.getJPYAmount(amount);
            }

           // from_account     = commTransactionDetailsVO.getCustomerBankAccountNumber();
            to_account       = commTransactionDetailsVO.getBankAccountNo();
            currency         = commTransactionDetailsVO.getCurrency();
            debit_currency   = commTransactionDetailsVO.getCurrency();



            reqStringBuffer.append("p_num=" + p_num);
            reqStringBuffer.append("&from_account=" + from_account);
            reqStringBuffer.append("&to_account=" + to_account);
            reqStringBuffer.append("&currency=" + currency);
            reqStringBuffer.append("&amount=" + amount);
            reqStringBuffer.append("&message=" + trans_id);
            reqStringBuffer.append("&debit_currency=" + debit_currency);
            reqStringBuffer.append("&trans_id=" + trans_id);

            signatureHM.put("from_account",from_account);
            signatureHM.put("password",password);
            signatureHM.put("p_num",p_num);
            signatureHM.put("amount",amount);

            signature           = tigerPayUtils.generateCheckSum(signatureHM);
            reqStringBuffer.append("&signature=" + signature);

            transactionlogger.error("REQUEST_URL ------> "+trackingId + " "+REQUEST_URL);
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerPay Payout Request ------> "+trackingId + " " + reqStringBuffer.toString());
            }else{
                transactionlogger.error("PayoutRequest ------> "+trackingId + " " + reqStringBuffer.toString());
            }


            String responseString =  tigerPayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, reqStringBuffer.toString());

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerPay Payout Response ---->"+trackingId+ " "+responseString);
            }else{
                transactionlogger.error("Payout Response ---->"+trackingId+ " "+responseString);
            }


            if(functions.isValueNull(responseString) && tigerPayUtils.isJSONValid(responseString)){

                JSONObject responsJSON = new JSONObject(responseString);

                String responseResult   ="";
                String responseStatus   ="";
                String transactionId   ="";
                String responsCurrency ="";
                String responAmount    ="";
                String fee             ="";

                if(responsJSON != null){

                    if(responsJSON.has("result")){
                        responseResult = responsJSON.getString("result");
                    }
                    if(responsJSON.has("status")){
                        responseStatus = responsJSON.getString("status");
                    }
                    if(responsJSON.has("transaction_number")){
                        transactionId = responsJSON.getString("transaction_number");
                    }
                    if(responsJSON.has("currency")){
                        responsCurrency = responsJSON.getString("currency");
                    }
                    if(responsJSON.has("amount")){
                        responAmount = responsJSON.getString("amount");
                    }
                    if(responsJSON.has("fee")){
                        fee = responsJSON.getString("fee");
                    }

                    if(responseStatus.equalsIgnoreCase("OK") && responseResult.equalsIgnoreCase("00") ){

                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setRemark(responseStatus);
                        commResponseVO.setAmount(responAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                    }//fail
                    else if(responseResult.equalsIgnoreCase("01") ||  responseResult.equalsIgnoreCase("02") || responseResult.equalsIgnoreCase("03")
                            || responseResult.equalsIgnoreCase("99") || responseResult.equalsIgnoreCase("90") || responseResult.equalsIgnoreCase("92")){

                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(responseStatus);
                        commResponseVO.setDescription("Failed");
                    }else{
                        //pending
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }

                    commResponseVO.setErrorCode(responseResult);
                    commResponseVO.setAuthCode(responseStatus);

                }else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("processPayout ----trackingId---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----Inside Tiger Pay Payout inquiry-------> "+trackingId);

        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        TigerPayUtils tigerPayUtils       = new TigerPayUtils();
        Functions functions               = new Functions();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                    = gatewayAccount.isTest();
        String p_num       = gatewayAccount.getMerchantId();
        String user_name   = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String token        = gatewayAccount.getFRAUD_FTP_PATH();
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();

        String REQUEST_URL              = "";

        String signature   = "";
        String trans_id    = trackingId;
        String api_name    = "MoneyRequest_result";
        String toType      = "";

        StringBuffer reqStringBuffer = new StringBuffer();

        try
        {
            if (functions.isValueNull(commTransactionDetailsVO.getTotype()))
            {
                toType = commTransactionDetailsVO.getTotype();
            }
            transactionlogger.error("toType ---> "+trackingId +" " + toType);


            if(isTest){
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
                trans_id= "TRACK"+trans_id;
            }else{
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            reqStringBuffer.append("p_num=" + p_num);
            reqStringBuffer.append("&txs=" + trans_id);

            String combine  = api_name + trans_id ;
            signature       = tigerPayUtils.getHmacSHA512(combine, password);
            reqStringBuffer.append("&signature=" + signature);

            transactionlogger.error("REQUEST_URL ---> " + trackingId + " " + REQUEST_URL);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerPay Payout Inquiry Request ---> "+trackingId +" " + reqStringBuffer.toString());
            }else{
                transactionlogger.error("Payout Inquiry Request ---> "+trackingId +" " + reqStringBuffer.toString());
            }


            String responseString = tigerPayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, reqStringBuffer.toString());
            if("Facilero".equalsIgnoreCase(toType)){
                transactionlogger.error("TigerPay Payout Inquiry Response ---> "+trackingId +" " +responseString);
            }else{
                transactionlogger.error(" Payout Inquiry ---> "+trackingId +" " +responseString);
            }


            JSONObject txsJson          = null;
            JSONObject transctionJson   = null;
            String result               = "";
            String responsResult        = "";
            String responsStatus        = "";
            String transactionId        = "";
            String responsCurrency      = "";
            String responAmount         = "";

            if(functions.isValueNull(responseString) && tigerPayUtils.isJSONValid(responseString)){

                JSONObject responseJson     = new JSONObject(responseString);

                if(responseJson != null){

                    if(responseJson.has("txs")){
                        txsJson = responseJson.getJSONObject("txs");
                    }else{
                        if(responseJson.has("result")){
                            responsResult = responseJson.getString("result");
                        }
                        if(responseJson.has("status")){
                            responsStatus = responseJson.getString("status");
                        }
                    }

                    if(txsJson != null){
                        if(txsJson.has(trans_id)){
                            transctionJson = txsJson.getJSONObject(trans_id);
                        }

                        if(transctionJson != null){

                            if(responseJson.has("status")){
                                responsStatus = responseJson.getString("status");
                            }
                            if(responseJson.has("result")){
                                responsResult = responseJson.getString("result");
                            }

                            if(transctionJson.has("transaction_number")){
                                transactionId = transctionJson.getString("transaction_number");
                            }
                            if(transctionJson.has("currency")){
                                responsCurrency = transctionJson.getString("currency");
                            }
                            if(transctionJson.has("amount")){
                                responAmount = transctionJson.getString("amount");
                            }
                        }
                    }

                    if(responsStatus.equalsIgnoreCase("OK") && responsResult.equalsIgnoreCase("00") ){

                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setDescription(responsStatus);
                        commResponseVO.setRemark(responsStatus);
                        commResponseVO.setAmount(responAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setAuthCode(result);
                        commResponseVO.setErrorCode(result);
                    }//fail
                    else if(responsResult.equalsIgnoreCase("01") ||  responsResult.equalsIgnoreCase("02") || responsResult.equalsIgnoreCase("03")
                            || responsResult.equalsIgnoreCase("99") || responsResult.equalsIgnoreCase("90") || responsResult.equalsIgnoreCase("92")){

                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(responsStatus);
                        commResponseVO.setDescription(responsStatus);
                    }else{
                        //pending
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Transaction is Pending.");  // set inquiry response not found
                        commResponseVO.setRemark("Transaction is Pending.");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Transaction is Pending");  // set inquiry response not found
                    commResponseVO.setRemark("Transaction is Pending");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Transaction is Pending");  // set inquiry response not found
                commResponseVO.setRemark("Transaction is Pending");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            commResponseVO.setMerchantId(p_num);
            commResponseVO.setCurrency(responsCurrency);

        }
        catch (Exception e)
        {
            transactionlogger.error("processPayoutInquiry JSONException-->" ,e );
        }

        return commResponseVO;

    }
}