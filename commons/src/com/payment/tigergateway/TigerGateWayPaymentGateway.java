package com.payment.tigergateway;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.TigerGatePayRequestVO;
import com.directi.pg.core.valueObjects.TigerPayResponseVO;
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
import org.apache.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 3/8/2021.
 */
public class TigerGateWayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(TigerGateWayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "tigergate";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.tigergate");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public TigerGateWayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("TigerGateWayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of TigerGateWayPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        TigerPayResponseVO commResponseVO               = new TigerPayResponseVO();
        Functions functions                             = new Functions();

        TigerGateWayUtils tigerPayUtils                     = new TigerGateWayUtils();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String paymentMode                              = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String cardType                                 = transactionDetailsVO.getCardType();

        boolean isTest          = gatewayAccount.isTest();
        String merchantId       = gatewayAccount.getMerchantId();
        String token            = gatewayAccount.getFRAUD_FTP_PATH();
        String secretKey        = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String transaction_id   = trackingID;
        String name             = "";
        String phone            = "";
        String email            = "";
        String REQUEST_URL      = "";
        String amount           = transactionDetailsVO.getAmount();
        JSONObject requestObject =  new JSONObject();
        String toType                =  transactionDetailsVO.getTotype();
        try
        {
            transactionlogger.error("toType --->  " +trackingID+ " " + toType);

            if ("JPY".equalsIgnoreCase(transactionDetailsVO.getCurrency())){
                amount = TigerGateWayUtils.getJPYAmount(transactionDetailsVO.getAmount());
            }
            if(isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                name = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            }
            else {
                name  = "customer";
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                email = commAddressDetailsVO.getEmail();
            }else {
                email  = "customer@gmail.com";
            }

            if(functions.isValueNull(commAddressDetailsVO.getPhone())) {

                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    phone = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                if(phone.length()>10){

                    phone = phone.substring(phone.length() - 10);
                }
                else{
                    phone = commAddressDetailsVO.getPhone();
                }
            }else {
                phone  = "9999999999";
            }

            requestObject.put("transaction_id",transaction_id);
            requestObject.put("amount",amount);
            requestObject.put("name",name);
            requestObject.put("phone",phone);
            requestObject.put("email",email);

            transactionlogger.error("REQUEST_URL --->  " +trackingID+ " " + REQUEST_URL);
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerGateWay Sale Request --->  " +trackingID+ " " + requestObject.toString());
            }else{
                transactionlogger.error("Sale Request --->  " +trackingID+ " " + requestObject.toString());
            }


            String responseString = tigerPayUtils.doPostJsonHTTPSURLConnectionClient(REQUEST_URL, requestObject.toString(), token);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerGateWay Sale Response --->  " +trackingID+ " " + responseString.toString());
            }else{
                transactionlogger.error("Sale Response --->  " +trackingID+ " " + responseString.toString());
            }



            if(functions.isValueNull(responseString) && tigerPayUtils.isJSONValid(responseString) )
            {
                String resBank_name        = "";
                String resBranch_name      = "";
                String resBranch_code      = "";
                String resAccount_type     = "";
                String resDate_requested   = "";
                String resAccount_name     = "";
                String resDate_deposited   = "";
                String resAmount_requested = "";
                String resAccount_number   = "";
                String resAmount_deposited = "";
                String resReference_number = "";
                String resStatus           = "";
                String system_transaction_id = "";
                boolean resSuccess         = false;
                String resMessage         = "";

                JSONObject resJsonObject = new JSONObject(responseString);
                JSONObject resDataObject = null;
                JSONObject resBankObject = null;

                if(resJsonObject !=null){

                    if(resJsonObject.has("success")){
                        resSuccess = resJsonObject.getBoolean("success");
                    }
                    if(resJsonObject.has("message")){
                        resMessage = resJsonObject.getString("message");
                    }

                    if(resJsonObject.has("data") && resJsonObject.getJSONObject("data") != null){
                        resDataObject  =  resJsonObject.getJSONObject("data");
                    }

                    if(resDataObject != null ){
                        if(resDataObject.has("bank") && resDataObject.getJSONObject("bank") != null){
                            resBankObject  =  resDataObject.getJSONObject("bank");
                        }
                        if(resDataObject.has("reference_number")){
                            resReference_number  =  resDataObject.getString("reference_number");
                        }
                        if(resDataObject.has("system_transaction_id")){
                            system_transaction_id  =  resDataObject.getString("system_transaction_id");
                        }

                        if(resDataObject.has("status")){
                            resStatus = resDataObject.getString("status");
                        }

                        if(resBankObject != null){
                            if(resBankObject.has("bank_name")){
                                resBank_name = resBankObject.getString("bank_name");
                            }
                            if(resBankObject.has("branch_name")){
                                resBranch_name = resBankObject.getString("branch_name");
                            }
                            if(resBankObject.has("branch_code")){
                                resBranch_code = resBankObject.getString("branch_code");
                            }
                            if(resBankObject.has("account_type")){
                                resAccount_type = resBankObject.getString("account_type");
                            }
                            if(resBankObject.has("account_number")){
                                resAccount_number = resBankObject.getString("account_number");
                            }
                            if(resBankObject.has("account_name")){
                                resAccount_name = resBankObject.getString("account_name");
                            }
                        }

                        if(resStatus.equalsIgnoreCase("applying") && resSuccess){
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setDescription(resStatus);
                            commResponseVO.setRemark(resStatus);// do not change

                            commResponseVO.setBankName(resBank_name);
                            commResponseVO.setBranchName(resBranch_name);
                            commResponseVO.setBranchCode(resBranch_code);
                            commResponseVO.setAccountType(resAccount_type);
                            commResponseVO.setAccountNumber(resAccount_number);
                            commResponseVO.setAccountName(resAccount_name);
                            commResponseVO.setTransactionId(resReference_number);
                            commResponseVO.setAmount(amount);
                        }else if(!resSuccess){
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription(resMessage);  // set inquiry response not found
                            commResponseVO.setRemark(resMessage);  // set inquiry response not found
                            commResponseVO.setTransactionStatus(resMessage);
                        }
                        else{
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setTransactionStatus("pending");
                        }

                        if(functions.isValueNull(system_transaction_id) || functions.isValueNull(resReference_number)){
                            tigerPayUtils.updatePaymentIdTransaction(trackingID,system_transaction_id,resReference_number);
                        }
                        /*if(functions.isValueNull(resReference_number)){
                            tigerPayUtils.updateOrderid(resReference_number, trackingID);
                        }*/

                    }

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
        }catch (JSONException e)
        {
            transactionlogger.error("TigerGateWayPaymentGateway processQuery JSONException--->",e);
        }
        catch (Exception e)
        {
            transactionlogger.error("JSONException TigerGateWayPaymentGateway ---------------> ", e);
        }

        return commResponseVO;
    }


    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in TigerPay ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        TigerGateWayUtils tigerPayUtils                         = new TigerGateWayUtils();
        TigerGateWayPaymentProcess tigerPayPaymentProcess = new TigerGateWayPaymentProcess();
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
            transactionlogger.error("PZGenericConstraintViolationException in TigerGateWayPaymentGateway---", e);
        }
        return html;
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
        TigerGateWayUtils tigerGateWayUtils                     = new TigerGateWayUtils();

        String p_num       = gatewayAccount.getMerchantId();
        String user_name   = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String token       = gatewayAccount.getFRAUD_FTP_PATH();

        String REQUEST_URL          = "";
        String APPROVE_REQUEST_URL          = "";
        JSONArray jsonArray         = new JSONArray();

        String transaction_id   = trackingId;
        String amount           = tigerGateWayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String bank_name        = commRequestVO.getBank_Name();
        String branch_name      = commRequestVO.getBranch_Name();
        String account_type     = commRequestVO.getAccount_Type();
        String bank_code        = commRequestVO.getBank_Code();
        String branch_code      = commRequestVO.getBranch_Code();
        String account_number   = commTransactionDetailsVO.getBankAccountNo();
        String account_name     = commTransactionDetailsVO.getCustomerBankAccountName();


        String status                   = "approved";
        JSONObject jsonRequestObject    = new JSONObject();
        String toType                =  commTransactionDetailsVO.getTotype();

        try
        {
            transactionlogger.error("toType --->  " +trackingId+ " " + toType);
            if (isTest)
            {
                REQUEST_URL  = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (isTest)
            {
                APPROVE_REQUEST_URL  = RB.getString("PAYOUT_APPROVE_TEST_URL");
            }
            else
            {
                APPROVE_REQUEST_URL = RB.getString("PAYOUT_APPROVE_LIVE_URL");
            }

            if ( commTransactionDetailsVO.getCurrency() != null &&  "JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency())){
                amount = TigerGateWayUtils.getJPYAmount(amount);
            }

            jsonRequestObject.put("transaction_id", transaction_id);
            jsonRequestObject.put("bank_name", bank_name);
            jsonRequestObject.put("account_type", account_type);
            jsonRequestObject.put("account_name",account_name);
            jsonRequestObject.put("branch_name",branch_name);
            jsonRequestObject.put("branch_code",  branch_code);
            jsonRequestObject.put("account_number",account_number);
            jsonRequestObject.put("bank_code", bank_code);
            jsonRequestObject.put("amount", amount);

            transactionlogger.error("REQUEST_URL ------> "+trackingId + " "+REQUEST_URL);
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerGateWay PayoutRequest ------> "+trackingId + " " + jsonRequestObject.toString());
            }else{
                transactionlogger.error("PayoutRequest ------> "+trackingId + " " + jsonRequestObject.toString());
            }

            String responseString = tigerGateWayUtils.doPostJsonHTTPSURLConnectionClient(REQUEST_URL, jsonRequestObject.toString(), token);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TigerGateWay PayoutResponse ----> "+trackingId+ " "+responseString);
            }else{
                transactionlogger.error("PayoutResponse ----> "+trackingId+ " "+responseString);
            }

            if(functions.isValueNull(responseString) && tigerGateWayUtils.isJSONValid(responseString)){

                String resBank_name        = "";
                String resBranch_name      = "";
                String resBranch_code      = "";
                String resAccount_type     = "";
                String resDate_requested   = "";
                String resAccount_name     = "";
                String resDate_deposited   = "";
                String resAmount_requested = "";
                String resAccount_number   = "";
                String resAmount_deposited = "";
                String resReference_number = "";
                String resStatus           = "";
                boolean resSuccess         = true;
                String resMessage          = "";
                String system_transaction_id          = "";

                JSONObject resJsonObject = new JSONObject(responseString);
                JSONObject resDataObject = null;

                if(resJsonObject !=null){

                    if(resJsonObject.has("data") && resJsonObject.getJSONObject("data") != null){
                        resDataObject  =  resJsonObject.getJSONObject("data");

                        if(resJsonObject.has("success")){
                            resSuccess = resJsonObject.getBoolean("success");
                        }
                        if(resJsonObject.has("message")){
                            resMessage = resJsonObject.getString("message");
                        }
                    }

                    if(resDataObject != null ){

                        if(resDataObject.has("status")){
                            resStatus = resDataObject.getString("status");
                        }
                        if(resDataObject.has("system_transaction_id")){
                            system_transaction_id = resDataObject.getString("system_transaction_id");
                        }

                        /*if(resDataObject.has("bank_name")){
                            resBank_name = resDataObject.getString("bank_name");
                        }
                        if(resDataObject.has("branch_name")){
                            resBranch_name = resDataObject.getString("branch_name");
                        }
                        if(resDataObject.has("branch_code")){
                            resBranch_code = resDataObject.getString("branch_code");
                        }
                        if(resDataObject.has("account_type")){
                            resAccount_type = resDataObject.getString("account_type");
                        }
                        if(resDataObject.has("account_number")){
                            resAccount_number = resDataObject.getString("account_number");
                        }
                        if(resDataObject.has("account_name")){
                            resAccount_name = resDataObject.getString("account_name");
                        }
                        if(resDataObject.has("reference_number")){
                            resReference_number = resDataObject.getString("reference_number");
                        }*/
                    }

                    if(functions.isValueNull(system_transaction_id)){
                        tigerGateWayUtils.updateTranactionId(system_transaction_id,trackingId);
                    }

                        if(resStatus.equalsIgnoreCase("applying") && resSuccess){

                            jsonArray.put(trackingId);
                            JSONObject reqestJsonObject         =  new JSONObject();

                            reqestJsonObject.put("selected_ids", jsonArray);
                            reqestJsonObject.put("status", status);
                            if("Facilero".equalsIgnoreCase(toType)){
                                facileroLogger.error("TigerGate approveRequest ----> " + trackingId + " " + reqestJsonObject.toString());
                            }else{
                                transactionlogger.error("approveRequest ----> " + trackingId + " " + reqestJsonObject.toString());
                            }

                            String approveResponse = tigerGateWayUtils.doPostJsonHTTPSURLConnectionClient(APPROVE_REQUEST_URL, reqestJsonObject.toString(), token);

                            if("Facilero".equalsIgnoreCase(toType)){
                                facileroLogger.error("approveResponse ----> " + trackingId + " " + approveResponse);
                            }else{
                                transactionlogger.error("approveResponse ----> " + trackingId + " " + approveResponse);
                            }

                            if(functions.isValueNull(approveResponse) && tigerGateWayUtils.isJSONValid(approveResponse)){
                                commResponseVO.setStatus("pending");
                                commResponseVO.setTransactionStatus("pending");
                                commResponseVO.setDescription(resStatus);
                                commResponseVO.setRemark(resStatus);
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
}