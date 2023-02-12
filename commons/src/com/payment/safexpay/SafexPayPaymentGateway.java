package com.payment.safexpay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.paygate.ag.common.utils.PayGateCryptoUtils;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by jeet on 29/08/19.
 */
public class SafexPayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="safexpay";
    private final static SafexPayGatewayLogger transactionLogger=new SafexPayGatewayLogger(SafexPayPaymentGateway.class.getName());
    private final  static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.safexpay");
    public SafexPayPaymentGateway(String accountId){
        this.accountId=accountId;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("SafexPayPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO =  new Comm3DResponseVO();

         commResponseVO.setStatus("pending3DConfirmation");
        //commResponseVO.setUrlFor3DRedirect(payment_Url);
        transactionLogger.error("inside sale safexpay -------");

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        CommResponseVO commResponseVO =  new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        Functions functions= new Functions();
        String ag_id="Paygate";
        String me_id=gatewayAccount.getMerchantId();
        String merchant_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        String ag_ref=transactionDetailsVO.getPreviousTransactionId();
        String refund_amount=transactionDetailsVO.getAmount();
        String refund_reason=commRequestVO.getTransDetailsVO().getOrderDesc();
        PayGateCryptoUtils payGateCryptoUtils= new PayGateCryptoUtils();

        String Encrypted_AG_REF_Number = payGateCryptoUtils.encrypt(ag_ref, merchant_key);
        String Encrypted_Refund_Amount = payGateCryptoUtils.encrypt(refund_amount, merchant_key);
        String Encrypted_Refund_Reason = PayGateCryptoUtils.encrypt(refund_reason,merchant_key);

        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ag_id", ag_id);
            jsonObject.put("me_id", me_id);
            jsonObject.put("ag_ref", Encrypted_AG_REF_Number);
            jsonObject.put("refund_amount", Encrypted_Refund_Amount);
            jsonObject.put("refund_reason", Encrypted_Refund_Reason);

            transactionLogger.error("refundRequest-----" + jsonObject.toString());

            String refundResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside SafexPay Test Refund --->");
                refundResponse = SafexPayUtils.doPostHttpUrlConnection(RB.getString("REFUND_TEST_URL"), jsonObject.toString());
            }
            else
            {
                transactionLogger.error("inside SafexPay Live Refund --->");
                refundResponse = SafexPayUtils.doPostHttpUrlConnection(RB.getString("REFUND_LIVE_URL"), jsonObject.toString());
            }
            transactionLogger.error("refundResponse-----" + refundResponse);
            if (functions.isValueNull(refundResponse) && refundResponse.contains("{"))
            {
                JSONObject jsonObject1 = new JSONObject(refundResponse);
                if (jsonObject1 != null)
                {
                    String transactionId = "";
                    String txn_date = "";
                    String Response_refund_amount = "";
                    String currency = "";
                    String status = "";
                    String res_code = "";
                    String res_message = "";
                    String error_details = "";

                    if (jsonObject1.has("ag_ref"))
                    {
                        transactionId = jsonObject1.getString("ag_ref");
                    }
                    if (jsonObject1.has("txn_date"))
                    {
                        txn_date = jsonObject1.getString("txn_date");
                    }
                    if (jsonObject1.has("refund_amount"))
                    {
                        Response_refund_amount = jsonObject1.getString("refund_amount");
                    }
                    if (jsonObject1.has("currency"))
                    {
                        currency = jsonObject1.getString("currency");
                    }
                    if (jsonObject1.has("status"))
                    {
                        status = jsonObject1.getString("status");
                    }
                    if (jsonObject1.has("res_code"))
                    {
                        res_code = jsonObject1.getString("res_code");
                    }
                    if (jsonObject1.has("res_message"))
                    {
                        res_message = jsonObject1.getString("res_message");
                    }
                    if (jsonObject1.has("error_details"))
                    {
                        error_details = jsonObject1.getString("error_details");
                    }

                    if (res_code.equalsIgnoreCase("00000"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setDescription(status);
                        commResponseVO.setTransactionStatus("Successful");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(error_details);
                        commResponseVO.setTransactionStatus("Failed");
                    }
                    commResponseVO.setRemark(res_message);
                    commResponseVO.setBankTransactionDate(txn_date);
                    commResponseVO.setTransactionId(transactionId);
                    commResponseVO.setAmount(Response_refund_amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setErrorCode(res_code);
                    commResponseVO.setTransactionType(status);
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionStatus("Failed");
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SafexPayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        CommResponseVO commResponseVO =  new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        TransactionManager transactionManager =new TransactionManager();
        String transaction_status="";
        boolean isTest=gatewayAccount.isTest();
        Functions functions= new Functions();
        String ag_id="Paygate";
        String me_id=gatewayAccount.getMerchantId();
        String merchant_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        // String trackingId=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String order_no = commRequestVO.getTransDetailsVO().getOrderId();
        transactionLogger.error("commRequestVO.getTransDetailsVO().getPreviousTransactionId()---------------->"+commRequestVO.getTransDetailsVO().getOrderId());
        // String Encrypted_TrackingId_Number = PayGateCryptoUtils.encrypt(trackingId, merchant_key);
        transactionLogger.error("PayGateCryptoUtils.encrypt(ag_ref,merchant_key)----------------------------->"+PayGateCryptoUtils.encrypt(order_no,merchant_key));
        transactionLogger.error("order_no------------------>"+order_no);

        String Encrypted_Payment_Id_Number = PayGateCryptoUtils.encrypt(order_no,merchant_key);
        String ag_ref="";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject .put("ag_id",ag_id);
            jsonObject.put("me_id",me_id);
            // jsonObject.put("order_no",Encrypted_TrackingId_Number);
            jsonObject.put("order_no",Encrypted_Payment_Id_Number);
            jsonObject.put("ag_ref","");
            transactionLogger.error("inquiryRequest-----"+jsonObject.toString());
            String inquiryResponse="";

            if(isTest){
                inquiryResponse=SafexPayUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_TEST_URL"), jsonObject.toString());
            }else {
                inquiryResponse=SafexPayUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_LIVE_URL"),jsonObject.toString());
            }
            transactionLogger.error("inquiryResponse-----"+inquiryResponse);
            String decrypted_res = PayGateCryptoUtils.decrypt(inquiryResponse,merchant_key);
            transactionLogger.error("decrypted_res is -->"+decrypted_res);
            if(functions.isValueNull(decrypted_res)&&decrypted_res.contains("{"))
            {
                JSONObject jsonObject1 = new JSONObject(decrypted_res);
                if (jsonObject1 !=null)
                {
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("txn_response");
                    {
                       // String order_no="";
                        String amount="";
                        String country="";
                        String currency="";
                        String txn_date="";
                        String status="";
                        String res_code="";
                        String res_message="";
                        String ag_ref2="";
                        String res_message2="";

                        if (jsonObject2.has("me_id"))
                        {
                            me_id = String.valueOf(jsonObject2.get("me_id"));
                        }
                        if (jsonObject2.has("currency"))
                        {
                            currency = jsonObject2.getString("currency");
                        }
                        if (jsonObject2.has("res_code"))
                        {
                            res_code = jsonObject2.getString("res_code");
                        }
                        if (jsonObject2.has("ag_ref"))
                        {
                            ag_ref = jsonObject2.getString("ag_ref");
                        }
                        if (jsonObject2.has("status"))
                        {
                            status = jsonObject2.getString("status");
                        }
                        if (jsonObject2.has("amount"))
                        {
                            amount = jsonObject2.getString("amount");
                        }
                        if (jsonObject2.has("txn_date"))
                        {
                            txn_date = jsonObject2.getString("txn_date");
                        }
                        if (jsonObject2.has("res_message"))
                        {
                            res_message = jsonObject2.getString("res_message");
                        }

                        if("Successful".equalsIgnoreCase(status)){
                            commResponseVO.setTransactionStatus("success");
                            commResponseVO.setRemark(status);
                            commResponseVO.setBankTransactionDate(txn_date);
                            commResponseVO.setMerchantId(me_id);
                            commResponseVO.setTransactionId(ag_ref);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAuthCode(res_code);

                        }
                         else if("Pending".equalsIgnoreCase(status)){
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark(status);
                            commResponseVO.setBankTransactionDate(txn_date);
                            commResponseVO.setMerchantId(me_id);
                            commResponseVO.setTransactionId(ag_ref);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAuthCode(res_code);

                        }
                        else if("Failed".equalsIgnoreCase(status)||"Aborted".equalsIgnoreCase(status)){
                            commResponseVO.setTransactionStatus("fail");
                            commResponseVO.setRemark(status);
                            commResponseVO.setBankTransactionDate(txn_date);
                            commResponseVO.setMerchantId(me_id);
                            commResponseVO.setTransactionId(ag_ref);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAuthCode(res_code);

                        }
                        else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                        }
                            commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
            }

        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in Safexpay ---- ");
        String html = "";
        PaymentManager paymentManager = new PaymentManager();
        Comm3DResponseVO transRespDetails = null;
        SafexPayUtils safexPayUtils = new SafexPayUtils();

           String paymentMode = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO = safexPayUtils.getSafexPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionLogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("autoredirect flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = safexPayUtils.generateAutoSubmitForm(commonValidatorVO);
                transactionLogger.error("automatic redirect safexpay form -- >>"+html);
                //safexPayUtils.updateTransaction(commonValidatorVO.getTrackingid(),commRequestVO.getTransDetailsVO().getCustomerId());
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in SafexPaypaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside safexpay process payout-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        SafexPayPaymentProcess safexPayPaymentProcess=new SafexPayPaymentProcess();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        PayGateCryptoUtils payGateCryptoUtils=new PayGateCryptoUtils();
        //transactionLogger.error("key------------->"+merchant_key);
        boolean isTest = gatewayAccount.isTest();
        String sessionId="";
        String versionCheckKey="";
        String url="";
        if (isTest)
        {
            url=RB.getString("PAYOUT_TEST_URL");
            sessionId="AGGR0026548013";

        }
        else
        {
            url=RB.getString("PAYOUT_LIVE_URL");
            sessionId=gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        }
         String Amount2=commTransactionDetailsVO.getAmount();
         versionCheckKey=gatewayAccount.getFRAUD_FTP_PATH();
        if(!functions.isValueNull(versionCheckKey))
        {
            versionCheckKey = "9v6ZyFBzNYoP2Un8H5cZq5FeBwxL6itqNZsm7lisGBQ=";
        }
        String payoutKey="";
        String enreq="";
        String finalreq="";
        String versionCheckRequest= "{ \n" +
                "  \"header\": { \n" +
                "    \"operatingSystem\": \"ios\", \n" +
                "    \"sessionId\":\""+sessionId+"\", \n" +
                "    \"version\": \"1.0.0\" \n" +
                "  }, \n" +
                "  \"transaction\": { \n" +
                "    \"requestType\": \"ONBO\", \n" +
                "    \"requestSubType\": \"CHECK\", \n" +
                "    \"channel\": \"WEB\", \n" +
                "    \"tranCode\": 0, \n" +
                "    \"txnAmt\":\""+Amount2+"\" \n" +
                "  } \n" +
                "} ";

        enreq=payGateCryptoUtils.encrypt(versionCheckRequest,versionCheckKey);
        finalreq="{\"payload\": \""+enreq+"\", \"uId\": \"CHECK\"}";
        transactionLogger.error("versionCheckKey-->" + versionCheckKey);
        transactionLogger.error("versionCheckRequest-->" + versionCheckRequest);
        transactionLogger.error("finalreq-->" + finalreq);
        String enres=SafexPayUtils.doPostHttpUrlConnection(url,finalreq);
        transactionLogger.error("enresponse-->" + enres);


        String decryptedVersionCheckResponse="";
        String response="";
        try
        {
            JSONObject versionCheckResponse=new JSONObject(enres);

            if(versionCheckResponse.has("payload")){
                response=versionCheckResponse.getString("payload");
                decryptedVersionCheckResponse=payGateCryptoUtils.decrypt(response,versionCheckKey);
                transactionLogger.error(" decryptedVersionCheckResponse-->"+"trackingid"+trackingId+"------>"+ decryptedVersionCheckResponse);
            }
            JSONObject payloadResponse=new JSONObject(decryptedVersionCheckResponse);
            JSONObject responseKey=payloadResponse.getJSONObject("header");
            transactionLogger.error("responseKey-->" + responseKey);

            if (responseKey.has("key")){
                payoutKey=responseKey.getString("key");
             //   transactionLogger.error("payoutKey-->" + payoutKey);
            }
            else{
                if (isTest)
                {
                    payoutKey="/yQkzO9jeKgSyd0j0GFEikaJT5mz+6DzuoJAg7wimr4=";
                }
                else
                {   payoutKey=gatewayAccount.getFRAUD_FTP_PASSWORD();

                }

            }
// load money request
/*

            String enreq2="";
            String finalreq2="";
            String Amount3=commTransactionDetailsVO.getAmount();

            String LoadMoneyRequest="  {\n" +
                    "\"header\": {\n" +
                    "\"ipAddress\": \"192.168.1.147\",\n" +
                    "\"userAgent\": \"Mozilla/5.0 Chrome/80.0.3987.132 Safari/537.36\",\n" +
                    "\"operatingSystem\": \"WEB\",\n" +
                    "\"sessionId\": \""+sessionId+"\",\n" +
                    "\"version\": \"1.0.0\"\n" +
                    "},\n" +
                    "\"transaction\": {\n" +
                    "\"requestType\": \"WTW\",\n" +
                    "\"requestSubType\": \"LMREQ\", \n" +
                    "\"id\": \""+sessionId+"\"\n" +
                    "},\n" +
                    "\"loadMoneyModel\": {\n" +
                    "\"txnAmount\":\""+Amount3+"\",\n" +
                    "\"txnId\": \""+trackingId+"\",\n" +
                    "\"createdBy\": \""+sessionId+"\",\n" +
                    "\"ipAddress\": \"192.168.1.147\",\n" +
                    "\"userAgent\": \"Mozilla/5.o Chrome/80.0.3987.132 Safari/537.36\"\n" +
                    "}\n" +
                    "} ";

            enreq2=payGateCryptoUtils.encrypt(LoadMoneyRequest,payoutKey);
            finalreq2="{\"payload\": \""+enreq2+"\", \"uId\": \"DIST0013811079\"}";
            transactionLogger.error(" payoutfinalreq-->" + finalreq2);

            String enres3=SafexPayUtils.doPostHttpUrlConnection(url,finalreq);


*/


// final payout request
            String order_no=trackingId;
            String Amount=commTransactionDetailsVO.getAmount();
            String currency_code=commTransactionDetailsVO.getCurrency();
            String bene_name = "";
            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                bene_name=commTransactionDetailsVO.getCustomerBankAccountName();
                transactionLogger.error("bene_name is -- >"+bene_name);
            }
            String  bene_mobile_no="";
            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-")){
                    bene_mobile_no=commAddressDetailsVO.getPhone().split("-")[1];
                }
                else{
                    bene_mobile_no=commAddressDetailsVO.getPhone();
                }
                transactionLogger.error("bene_mobile_no is -->"+bene_mobile_no);
            }
            else{
                bene_mobile_no="9999999999";
            }
            String bene_account_no="";
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                bene_account_no=commTransactionDetailsVO.getBankAccountNo();
                transactionLogger.error("bene_account_no is -- >"+bene_account_no);
            }
            String bene_ifsc="";
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                bene_ifsc=commTransactionDetailsVO.getBankIfsc();
                transactionLogger.error("inside safexpay payout ifsc -->" + bene_ifsc);
            }
            String transfer_type ="";
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                transfer_type=commTransactionDetailsVO.getBankTransferType(); // additional fields added to check
                transactionLogger.error("transfer_type is -- >"+transfer_type);
            }  

           String payoutRequest="{\n" +
                    "   \"header\":{\n" +
                    "      \"operatingSystem\":\"ios\",\n" +
                    "      \"sessionId\":\""+sessionId+"\"\n" +
                    "   },\n" +
                    "   \"transaction\":{\n" +
                    "      \"requestType\":\"WTW\",\n" +
                    "      \"requestSubType\":\"PWTB\"\n" +
                    "   },\n" +
                    "   \"payOutBean\":{\n" +
                    "      \"mobileNo\":\""+bene_mobile_no+"\",\n" +
                    "      \"txnAmount\":\""+Amount+"\",\n" +
                    "      \"accountNo\":\""+bene_account_no+"\",\n" +
                    "      \"ifscCode\":\""+bene_ifsc+"\",\n" +
                    "      \"bankName\":\""+bene_name+"\",\n" +
                    "      \"accountHolderName\":\""+bene_name+"\",\n" +
                    "      \"txnType\":\""+transfer_type+"\",\n" +
                    "      \"accountType\":\"Saving\",\n" +
                    "      \"orderRefNo\":\""+trackingId+"\"\n" +
                    "   }\n" +
                    "}";

            enreq=payGateCryptoUtils.encrypt(payoutRequest,payoutKey);
            finalreq="{\"payload\": \""+enreq+"\", \"uId\": \""+sessionId+"\"}";
            transactionLogger.error(" payoutfinalreq-->" + finalreq);

            String enres2=SafexPayUtils.doPostHttpUrlConnection(url,finalreq);
            String CALL_EXECUTE_AFTER=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC=RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed=RB.getString("PAYOUT_THREAD_CALL");
            transactionLogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionLogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncSafexPayPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }
            String decryptedPayoutResponse="";
            String response2="";
            String description="";
            String code="";
            String payoutId="";
            String customerId="";
            String txnAmount="";
            String aggregatorId="";
            String txnStatus="";
            String bankStatus="";
            String statusCode="";
            String statusDesc="";
            String orderRefNo="";
            String spkRefNo="";
            String bankRefNo="";
            JSONObject payoutResponse=new JSONObject(enres2);

            if(payoutResponse.has("payload")){
                response=payoutResponse.getString("payload");
                decryptedPayoutResponse=payGateCryptoUtils.decrypt(response,payoutKey);
                transactionLogger.error(" decryptedPayoutResponse-->" + decryptedPayoutResponse);

            }
            JSONObject payloadResponse2=new JSONObject(decryptedPayoutResponse);
            JSONObject responseobj=payloadResponse2.getJSONObject("response");
            if (responseobj.has("code")&&responseobj.has("description")){
                description=responseobj.getString("description");
                code=responseobj.getString("code");
                transactionLogger.error(" description-->" + description+"    code---->"+code);
            }
            JSONObject payOutBean=payloadResponse2.getJSONObject("payOutBean");

            if(payOutBean.has("txnStatus")||payOutBean.has("bankStatus"))
            {
                if(payOutBean.has("spkRefNo")){
                    spkRefNo=payOutBean.getString("spkRefNo");
                }
                if(payOutBean.has("bankRefNo")){
                    bankRefNo=payOutBean.getString("bankRefNo");
                }
                if(payOutBean.has("aggregatorId")){
                    aggregatorId=payOutBean.getString("aggregatorId");

                }
                if(payOutBean.has("txnStatus")){
                    txnStatus=payOutBean.getString("txnStatus");

                }
                if(payOutBean.has("payoutId")){
                    payoutId=payOutBean.getString("payoutId");
                }

                if(payOutBean.has("statusCode")){
                statusCode=payOutBean.getString("statusCode");
                }

                if(payOutBean.has("statusDesc")){
                    statusDesc=payOutBean.getString("statusDesc");
                }
                // customerId=payOutBean.getString("customerId");
                //txnAmount=payOutBean.getString("txnAmount");
                //bankStatus=payOutBean.getString("bankStatus");
                //statusDesc=payOutBean.getString("statusDesc");
                //orderRefNo=payOutBean.getString("orderRefNo");
                transactionLogger.error(" payoutId-->" + payoutId+"    aggregatorId---->"+aggregatorId+" txnStatus-->" + txnStatus+"    statusCode---->"+statusCode+"   bankRefNo---->"+bankRefNo+"   spkRefNo----->"+spkRefNo);
            }
            transactionLogger.error(" payoutId-->" + payoutId+"    aggregatorId---->"+aggregatorId+" txnStatus-->" + txnStatus+"    statusCode---->"+statusCode);

            if (txnStatus.equalsIgnoreCase("SUCCESS"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setRemark(description+"-"+statusDesc);
                commResponseVO.setDescription(description);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(bene_ifsc);
                commResponseVO.setFullname(bene_name);
                commResponseVO.setBankaccount("");
                commResponseVO.setBankRefNo(bankRefNo);
                commResponseVO.setSpkRefNo(spkRefNo);
               // SafexPayUtils.updatePayoutTransaction(trackingId,payoutId,sessionId);
                transactionLogger.error("inside if payout success-->"+txnStatus);
            }

            else if(txnStatus.equalsIgnoreCase("QUEUED")||txnStatus.equalsIgnoreCase("PENDING")||txnStatus.equalsIgnoreCase("PROCESSING")){

                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                if(txnStatus.equalsIgnoreCase("QUEUED")){
                    commResponseVO.setRemark("Payout Final Status will be updated in 72 hours");
                    commResponseVO.setDescription("Payout Final Status will be updated in 72 hours ");
                }else{
                    commResponseVO.setRemark(description+"-"+statusDesc);
                    commResponseVO.setDescription(description);
                }
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(bene_ifsc);
                commResponseVO.setFullname(bene_name);
                commResponseVO.setBankaccount("");
                commResponseVO.setBankRefNo(bankRefNo);
                commResponseVO.setSpkRefNo(spkRefNo);
               // SafexPayUtils.updatePayoutTransaction(trackingId,payoutId,sessionId);
                transactionLogger.error("inside else if payout pending-->"+txnStatus);
            }

            else if((txnStatus.equalsIgnoreCase("FAILED")&&!code.equalsIgnoreCase("E0506"))||code.equalsIgnoreCase("E0092"))
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark(description+"-"+statusDesc);
                commResponseVO.setDescription(description);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(bene_ifsc);
                commResponseVO.setFullname(bene_name);
                commResponseVO.setBankaccount("");
                commResponseVO.setBankRefNo(bankRefNo);
                commResponseVO.setSpkRefNo(spkRefNo);
              //  SafexPayUtils.updatePayoutTransaction(trackingId,payoutId,sessionId);
                transactionLogger.error("inside else if payout failed-->"+txnStatus);
            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }

            commResponseVO.setTransactionId(payoutId);
            commResponseVO.setResponseHashInfo(aggregatorId);
            SafexPayUtils.updatePayoutTransaction(trackingId,payoutId,sessionId);
        }

        catch (JSONException e)
        {
            transactionLogger.error("JSONException----trackingid---->"+trackingId+"--->",e);
        }

        return commResponseVO;
    }
    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside safexpay process payout inquiry------->");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
      //  String merchant_key=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        PayGateCryptoUtils payGateCryptoUtils=new PayGateCryptoUtils();

        String sp_ref_no=commTransactionDetailsVO.getPreviousTransactionId();

        transactionLogger.error("sp_ref_no---------->"+sp_ref_no);

        String res="";
        String msg ="";
        String encryption_msg="";

        String sessionId=commTransactionDetailsVO.getAuthorization_code();
        String versionCheckKey="";
        String url="";
        String url2="";
        transactionLogger.error("payout inquiry sessionId---------->"+sessionId);
        if (isTest)
        {
            url=RB.getString("PAYOUT_TEST_URL");
            sessionId="AGGR0026548013";
            url2=RB.getString("PAYOUT_INQUIRY_LIVE_URL");
        }
        else
        {
            url=RB.getString("PAYOUT_LIVE_URL");
            url2=RB.getString("PAYOUT_INQUIRY_LIVE_URL");
         //   sessionId=gatewayAccount.getFRAUD_FILE_SHORT_NAME();

            /*if(!functions.isValueNull(sessionId))
            {
                sessionId = "DIST0011485884";
            }*/
        }
        String Amount2=commTransactionDetailsVO.getAmount();
        if(functions.isValueNull(Amount2)){
            Amount2="0.00";
        }
        versionCheckKey=gatewayAccount.getFRAUD_FTP_PATH();
        if(!functions.isValueNull(versionCheckKey))
        {
            versionCheckKey = "9v6ZyFBzNYoP2Un8H5cZq5FeBwxL6itqNZsm7lisGBQ=";
        }
        String payoutKey="";
        String enreq="";
        String finalreq="";
        String versionCheckRequest= "{ \n" +
                "  \"header\": { \n" +
                "    \"operatingSystem\": \"ios\", \n" +
                "    \"sessionId\":\""+sessionId+"\", \n" +
                "    \"version\": \"1.0.0\" \n" +
                "  }, \n" +
                "  \"transaction\": { \n" +
                "    \"requestType\": \"ONBO\", \n" +
                "    \"requestSubType\": \"CHECK\", \n" +
                "    \"channel\": \"WEB\", \n" +
                "    \"tranCode\": 0, \n" +
                "    \"txnAmt\":\""+Amount2+"\" \n" +
                "  } \n" +
                "} ";

        enreq=payGateCryptoUtils.encrypt(versionCheckRequest,versionCheckKey);
        finalreq="{\"payload\": \""+enreq+"\", \"uId\": \"CHECK\"}";
        transactionLogger.error("versionCheckKey-->" + versionCheckKey);
        transactionLogger.error("versionCheckRequest-->" + versionCheckRequest);
        transactionLogger.error("finalreq-->" + finalreq);
        String enres=SafexPayUtils.doPostHttpUrlConnection(url,finalreq);
        transactionLogger.error("enresponse-->" + enres);


        String decryptedVersionCheckResponse="";
        String response="";
        try
        {   if(functions.isValueNull(enres)){
            JSONObject versionCheckResponse = new JSONObject(enres);

            if (versionCheckResponse.has("payload"))
            {
                response = versionCheckResponse.getString("payload");
                decryptedVersionCheckResponse = payGateCryptoUtils.decrypt(response, versionCheckKey);
                transactionLogger.error(" decryptedVersionCheckResponse-->" + "trackingid" + trackingId + "------>" + decryptedVersionCheckResponse);
            }
            JSONObject payloadResponse = new JSONObject(decryptedVersionCheckResponse);
            JSONObject responseKey = payloadResponse.getJSONObject("header");
            transactionLogger.error("header-->" + responseKey);

            if (responseKey.has("key"))
            {
                payoutKey = responseKey.getString("key");
             //   transactionLogger.error("payoutKey-->" + payoutKey);
            }
            else{
                payoutKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
            }
        }
            else
            {
                if (isTest)
                {
                    payoutKey = "/yQkzO9jeKgSyd0j0GFEikaJT5mz+6DzuoJAg7wimr4=";
                }
                else
                {
                    payoutKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
                }

            }

            //payout inquiry
            String payoutinquiryrequest="";
            String payoutinquiryrequestfinalreq="";
            //berfore version check key
            // String key="9v6ZyFBzNYoP2Un8H5cZq5FeBwxL6itqNZsm7lisGBQ=";
            String payoutId=commTransactionDetailsVO.getPreviousTransactionId();

            payoutinquiryrequest= "{  \"header\": {\n" +
                    " \"operatingSystem\": \"ios\",\n" +
                    " \"sessionId\": \""+sessionId+"\"\n" +
                    " },\n" +
                    " \"transaction\": {\n" +
                    " \"requestType\": \"TMH\",\n" +
                    " \"requestSubType\": \"STCHK\"\n" +
                    " },\n" +
                    " \"payOutBean\": {\n" +
                    " \"payoutId\": \""+payoutId+"\"\n" +
                    " }\n" +
                    "}";




            String enreq2="";
            enreq2=payGateCryptoUtils.encrypt(payoutinquiryrequest,payoutKey);

         // payout inquiry request finalreq
            payoutinquiryrequestfinalreq="{\"payload\": \""+enreq2+"\", \"uId\": \""+sessionId+"\"}";
            transactionLogger.error(" payout inquiry request trackingid-->"+trackingId+"---"+ payoutinquiryrequest);
            transactionLogger.error(" payout inquiry requestfinalreq-->" + payoutinquiryrequestfinalreq);

            String enres2=SafexPayUtils.doPostHttpUrlConnection(url2,payoutinquiryrequestfinalreq);
            String decryptedPayoutResponse="";
            String response2="";
            String description="";
            String code="";
            String responsepayoutId="";
            String customerId="";
            String txnAmount="";
            String aggregatorId="";
            String txnStatus="";
            String bankStatus="";
            String statusCode="";
            String statusDesc="";
            String orderRefNo="";
            String spkRefNo="";
            String bankRefNo="";
            String accountNo="";
            String bankName="";
            String ifscCode="";
            JSONObject payoutResponse=new JSONObject(enres2);

            if(payoutResponse.has("payload")){
                response=payoutResponse.getString("payload");
                decryptedPayoutResponse=payGateCryptoUtils.decrypt(response, payoutKey);
                transactionLogger.error(" ecrypted Payout inquiry Response-->"+"trackingid---->"+trackingId+"----->" + response);
                transactionLogger.error(" decrypted Payout inquiry Response-->"+"trackingid---->"+trackingId+"----->"+ decryptedPayoutResponse);

            }
            JSONObject payloadResponse2=new JSONObject(decryptedPayoutResponse);
            JSONObject responseobj=payloadResponse2.getJSONObject("response");
            if (responseobj.has("code")&&responseobj.has("description")){
                description=responseobj.getString("description");
                code=responseobj.getString("code");
                transactionLogger.error(" description-->" + description+"    code---->"+code);
            }
            JSONObject payOutBean=payloadResponse2.getJSONObject("payOutBean");

            if(payOutBean.has("txnStatus")||payOutBean.has("bankStatus"))
            {
                if(payOutBean.has("spkRefNo")){
                    spkRefNo=payOutBean.getString("spkRefNo");
                }
                if(payOutBean.has("bankRefNo")){
                    bankRefNo=payOutBean.getString("bankRefNo");
                }
                if(payOutBean.has("aggregatorId")){
                    aggregatorId=payOutBean.getString("aggregatorId");

                }
                if(payOutBean.has("txnStatus")){
                    txnStatus=payOutBean.getString("txnStatus");

                }
                if(payOutBean.has("payoutId")){
                    responsepayoutId=payOutBean.getString("payoutId");
                }

                if(payOutBean.has("statusCode")){

                    statusCode=payOutBean.getString("statusCode");
                }
                if(payOutBean.has("bankStatus")){
                    bankStatus=payOutBean.getString("bankStatus");

                }

                if(payOutBean.has("accountNo")){
                    accountNo=payOutBean.getString("accountNo");

                }
                if(payOutBean.has("ifscCode")){
                    ifscCode=payOutBean.getString("ifscCode");

                }
                if(payOutBean.has("bankName")){
                    bankName=payOutBean.getString("bankName");

                }
                if(payOutBean.has("statusDesc")){
                    statusDesc=payOutBean.getString("statusDesc");
                }

                transactionLogger.error(" responsepayoutId-->" + responsepayoutId+"    aggregatorId---->"+aggregatorId+" txnStatus-->" + txnStatus+"    statusCode---->"+statusCode+"   bankRefNo---->"+bankRefNo+"   spkRefNo----->"+spkRefNo);
            }
            transactionLogger.error(" responsepayoutId-->" + responsepayoutId+"    aggregatorId---->"+aggregatorId+" txnStatus-->" + txnStatus+"    statusCode---->"+statusCode);
            transactionLogger.error(" description statusDesc-->" +description+"-"+statusDesc+"    accountNo--->"+accountNo+"   bankName---->"+bankName+"  ifscCode--->"+ifscCode);
            if (txnStatus.equalsIgnoreCase("SUCCESS"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setRemark(bankStatus+"-"+statusDesc);
                commResponseVO.setDescription(bankStatus);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(ifscCode);
                commResponseVO.setFullname(bankName);
                commResponseVO.setBankaccount("");
                commResponseVO.setBankRefNo(bankRefNo);
                commResponseVO.setSpkRefNo(spkRefNo);

            }
            else if(txnStatus.equalsIgnoreCase("QUEUED")||txnStatus.equalsIgnoreCase("PENDING")||txnStatus.equalsIgnoreCase("PROCESSING")){

                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                if(txnStatus.equalsIgnoreCase("QUEUED")){
                    commResponseVO.setRemark("Payout Final Status will be updated in 72 hours");
                    commResponseVO.setDescription("Payout Final Status will be updated in 72 hours ");
                }else{
                    commResponseVO.setRemark(description+"-"+statusDesc);
                    commResponseVO.setDescription(description);
                }
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(ifscCode);
                commResponseVO.setFullname(bankName);
                commResponseVO.setBankaccount("");
                commResponseVO.setBankRefNo(bankRefNo);
                commResponseVO.setSpkRefNo(spkRefNo);
            }

            else if ((txnStatus.equalsIgnoreCase("FAILED")||txnStatus.equalsIgnoreCase("UNKNOWN STATUS"))&&!code.equalsIgnoreCase("E0506"))
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark(bankStatus+"-"+statusDesc);
                commResponseVO.setDescription(bankStatus+"-"+statusDesc);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(ifscCode);
                commResponseVO.setFullname(bankName);
                commResponseVO.setBankaccount("");
                commResponseVO.setBankRefNo(bankRefNo);
                commResponseVO.setSpkRefNo(spkRefNo);
            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
            if(functions.isValueNull(payoutId))
            {
                commResponseVO.setTransactionId(payoutId);
            }

            commResponseVO.setResponseHashInfo(aggregatorId);

        }
        catch (JSONException e1)
        {
            transactionLogger.error(" JSONException-->" ,e1 );
        }


        return commResponseVO;

    }
}

