package com.payment.doku;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.PaymentManager;
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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Created by Rihen on 5/25/2021.
 */
public class DokuPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DokuPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.doku");

    public static final String GATEWAY_TYPE = "doku";

    public DokuPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public static void main(String[] args) {
        try
        {
            Functions functions = new Functions();
            String virtualAccountObj = "{'virtual_account_number': '9876543210'}";
            JSONObject jsonObject = new JSONObject(virtualAccountObj.toString());
            System.out.println("json obj = "+jsonObject);
            String maskedVirtualAccount = functions.maskingNumber(jsonObject.getString("virtual_account_number"));
            jsonObject.put("virtual_account_number", maskedVirtualAccount);
            System.out.println("json ======" +jsonObject);
        }
        catch(Exception e){
            System.out.println("e ="+e);
        }
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {

        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();

        Functions functions             = new Functions();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);

        String clientId     = gatewayAccount.getMerchantId();
        String secretKey    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest      = gatewayAccount.isTest();

        String orderId      = commTransactionDetailsVO.getOrderId();
        String orderDesc    = "";

        if(functions.isValueNull(commTransactionDetailsVO.getOrderDesc())){
            orderDesc   = commTransactionDetailsVO.getOrderDesc();
        }
        else {
            orderDesc = orderId;
        }

        String CUST_PHONE           = "";

        try
        {

            transactionLogger.error("phoneNummber  "+trackingID+ "-is-" + commAddressDetailsVO.getPhone());
            if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                try{

                    if (commAddressDetailsVO.getPhone().contains("-"))
                    {
                        CUST_PHONE = commAddressDetailsVO.getPhone().split("\\-")[1];
                    }
                    else
                    {
                        CUST_PHONE = commAddressDetailsVO.getPhone();
                    }
                }catch (Exception e){
                    CUST_PHONE  = "";
                }

            }
            String amount = DokuUtils.getAmount(commTransactionDetailsVO.getAmount());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("invoice_number", trackingID);
            orderRequest.put("currency", commTransactionDetailsVO.getCurrency());
            orderRequest.put("callback_url", RB.getString("CALLBACK_URL")+trackingID);

            JSONArray lineItemsArray    = new JSONArray();
            JSONObject lineItemsObject  = new JSONObject();
            lineItemsObject.put("name", orderDesc);
            lineItemsObject.put("price", amount);
            lineItemsObject.put("quantity","1");
            lineItemsArray.put(lineItemsObject);

            orderRequest.put("line_items", lineItemsArray);


            JSONObject paymentRequest = new JSONObject();

            JSONArray paymentMethodTypes = new JSONArray();
            paymentMethodTypes.put("VIRTUAL_ACCOUNT_BCA");
            paymentMethodTypes.put("VIRTUAL_ACCOUNT_BANK_MANDIRI");
            paymentMethodTypes.put("VIRTUAL_ACCOUNT_BANK_SYARIAH_MANDIRI");
            paymentMethodTypes.put("VIRTUAL_ACCOUNT_DOKU");
            paymentMethodTypes.put("ONLINE_TO_OFFLINE_ALFA");
            paymentMethodTypes.put("CREDIT_CARD");
            paymentMethodTypes.put("DIRECT_DEBIT_BRI");

            paymentRequest.put("payment_due_date", "30");
//            paymentRequest.put("payment_method_types", paymentMethodTypes);


            JSONObject customerRequest = new JSONObject();
            customerRequest.put("id", commTransactionDetailsVO.getCustomerId());
            customerRequest.put("email", commAddressDetailsVO.getEmail());

            if(functions.isValueNull(CUST_PHONE)){
                customerRequest.put("phone", CUST_PHONE);
            }
            customerRequest.put("address", commAddressDetailsVO.getStreet() + "," + commAddressDetailsVO.getCity()+","+commAddressDetailsVO.getState());
            customerRequest.put("country", commAddressDetailsVO.getCountry());

            JSONObject saleRequest = new JSONObject();
            saleRequest.put("order",orderRequest);
            saleRequest.put("payment",paymentRequest);
            saleRequest.put("customer",customerRequest);

            transactionLogger.error("sale request -----for "+trackingID+ "-is-" + saleRequest);


            String saleResponse = "";

            if (isTest ) {
                saleResponse = DokuUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), saleRequest.toString(), clientId, trackingID, secretKey);
            }
            else {
                saleResponse = DokuUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), saleRequest.toString(), clientId, trackingID, secretKey);
            }

            transactionLogger.error("sale response  -----for "+trackingID+ "is" + saleResponse);


            String status = "";
            String transactionId = "";
            String url = "";
            String description = "";
            String error_code = "";

            if (functions.isValueNull(saleResponse)) {
                JSONObject jsonResponse = new JSONObject(saleResponse);

                if (jsonResponse != null)
                {
                    if (jsonResponse.has("message")) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("message");
                        if (jsonArray != null) {
                            status = jsonArray.getString(0);
                        }
                    }

                    if (jsonResponse.has("response")) {
                        JSONObject jsonTransactionDetail = jsonResponse.getJSONObject("response");
                        if (jsonTransactionDetail != null) {
                            if (functions.isValueNull(jsonTransactionDetail.getString("uuid"))) {
                                transactionId = jsonTransactionDetail.getString("uuid");
                            }
                        }

                        if (jsonTransactionDetail.has("payment")) {
                            JSONObject jsonPaymentDetail = jsonTransactionDetail.getJSONObject("payment");
                            if (jsonPaymentDetail != null) {
                                if (functions.isValueNull(jsonPaymentDetail.getString("url"))) {
                                    url = jsonPaymentDetail.getString("url");
                                }
                            }
                        }
                    }

                    if(jsonResponse.has("error")) {
                        JSONObject errorObj = jsonResponse.getJSONObject("error");
                        if(functions.isValueNull(errorObj.getString("message"))){
                            description =  errorObj.getString("message");
                        }
                        if(functions.isValueNull(errorObj.getString("code"))){
                            error_code =  errorObj.getString("code");
                        }
                        status="FAILED";
                    }

                    transactionLogger.error("Status = " + status);
                    transactionLogger.error("url = " + url);
                    transactionLogger.error("transactionId = " + transactionId);

                    if("SUCCESS".equalsIgnoreCase(status)){
                        commResponseVO.setStatus("pending");
                        description="Transaction is Pending";
                        commResponseVO.setDescription("Transaction Pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else if ("FAILED".equalsIgnoreCase(status) || "EXPIRED".equalsIgnoreCase(status) || status.contains("Invalid"))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        if(!functions.isValueNull(description))
                            description=status;
                    } else if(!functions.isValueNull(url))
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription("fail");
                        commResponseVO.setRedirectUrl("");
                        commResponseVO.setRemark(status);
                        return commResponseVO;
                    }
                    else {
                        description = "Transaction Pending";
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    description="No response";
                }
            }


            commResponseVO.setRedirectUrl(url);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            commResponseVO.setDescription(description);
            commResponseVO.setRemark(description);
            commResponseVO.setErrorCode(error_code);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----", e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("DokuPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("inside processAutoRedirect in DokuPaymentGateway");
        String html="";
        DokuUtils dokuUtils = new DokuUtils();
        PaymentManager paymentManager = new PaymentManager();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("CustomerCheckout");
        commRequestVO = dokuUtils.getCommRequestFromUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            transactionLogger.debug("process Sale finished");
            transactionLogger.error("isTest in DOKU autoredirect ---"+isTest);
            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                html = DokuPaymentProcess.getPaymentForm(transRespDetails.getRedirectUrl(),isTest);
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                transactionLogger.debug("html---------------"+html);
            }else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
            {
                paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                html="failed";
            }
        }

        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in ZotaPayGateway ---",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception--- ", e);
        }
        return html;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("----- inside Doku processInquiry -----");;
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();

        String trackingId   = commTransactionDetailsVO.getOrderId();
        String clientId     = gatewayAccount.getMerchantId();
        String secretKey    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest      = gatewayAccount.isTest();


        transactionLogger.error("Doku Inquiry Details For " + trackingId + "::::::::::secretKey::::::" + secretKey + " clientId::::::::" + clientId);


        try
        {
            String inquiryResponse = "";

            transactionLogger.error("tracking ID = "+trackingId);


            if (isTest)
            {
                inquiryResponse = DokuUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_TEST") + trackingId , clientId, trackingId, secretKey);
            }
            else
            {
                inquiryResponse = DokuUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_LIVE") + trackingId, clientId, trackingId, secretKey);
            }

            transactionLogger.error("inquiryResponse for " + trackingId + " -----" + inquiryResponse);

            String transactionId    = "";
            String amount           = "";
            String status           = "";
            String error_code       = "";
            String description      = "";
            String paymentMode      = "";
            String rrn              = "";
            String responseHashInfo = "";
            String bankTransDate    = "";

            if (functions.isValueNull(inquiryResponse)) {
                JSONObject jsonInquiryResponse = new JSONObject(inquiryResponse);
                if (jsonInquiryResponse != null) {

                    if (jsonInquiryResponse.has("transaction")) {
                        JSONObject transObj = jsonInquiryResponse.getJSONObject("transaction");
                        if (functions.isValueNull(transObj.getString("status"))) {
                            status = transObj.getString("status");
                            transactionLogger.error("Inquiry Status-----" + status);
                            if ("success".equalsIgnoreCase(status)) {
                                description = "Transaction Successful";
                                commResponseVO.setStatus("success");
                                commResponseVO.setTransactionStatus("success");
                                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else if ("failed".equalsIgnoreCase(status) || "EXPIRED".equalsIgnoreCase(status)) {
                                description = "Transaction Failed";
                                commResponseVO.setStatus("failed");
                                commResponseVO.setTransactionStatus("failed");
                            }
                            else {
                                description = "Transaction Pending";
                                commResponseVO.setStatus("pending");
                                commResponseVO.setTransactionStatus("pending");
                            }
                        }
                        else {
                            description = "Transaction Pending";
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                        }

                        if(transObj.has("date"))
                        {
                            if (functions.isValueNull(transObj.getString("date")))
                            {
                                bankTransDate = transObj.getString("date");
                            }
                        }
                    }

                    if(jsonInquiryResponse.has("order")) {
                        JSONObject orderObj = jsonInquiryResponse.getJSONObject("order");
                        if (functions.isValueNull(orderObj.getString("amount"))) {
                            amount = orderObj.getString("amount");
                        }
                    }

                    if(jsonInquiryResponse.has("channel")) {
                        JSONObject channelObj = jsonInquiryResponse.getJSONObject("channel");
                        if (functions.isValueNull(channelObj.getString("id"))) {
                            paymentMode = channelObj.getString("id");
                        }
                    }

                    // in case of CREDIT CARD only
                    if (jsonInquiryResponse.has("card_payment")) {
                        JSONObject cardObject = jsonInquiryResponse.getJSONObject("card_payment");
                        if (functions.isValueNull(cardObject.getString("response_code"))) {
                            error_code = cardObject.getString("response_code");
                        }
                        if (functions.isValueNull(cardObject.getString("response_message"))) {
                            description = cardObject.getString("response_message");
                        }
                        if (functions.isValueNull(cardObject.getString("masked_card_number"))) {
                            responseHashInfo = cardObject.getString("masked_card_number");
                        }
                    }

                    // in case of VIRTUAL ACCOUNT only
                    if (jsonInquiryResponse.has("virtual_account_info")) {
                        JSONObject virtualAccountObj = jsonInquiryResponse.getJSONObject("virtual_account_info");
                        if (functions.isValueNull(virtualAccountObj.getString("virtual_account_number"))) {
                            responseHashInfo = virtualAccountObj.getString("virtual_account_number");
                            String maskedVirtualAccount = functions.maskingNumber(virtualAccountObj.getString("virtual_account_number"));
                            virtualAccountObj.put("virtual_account_number", maskedVirtualAccount);
//                            transactionLogger.error("Virtual account obj = "+ virtualAccountObj);
                            jsonInquiryResponse.put("virtual_account_info", virtualAccountObj);
                        }
                    }


                    if (jsonInquiryResponse.has("virtual_account_payment")) {
                        JSONObject virtualAccountObj = jsonInquiryResponse.getJSONObject("virtual_account_payment");
                        if (virtualAccountObj != null) {
                            if (functions.isValueNull(virtualAccountObj.getString("reference_number"))) {
                                rrn = virtualAccountObj.getString("reference_number");
                            }

                            JSONArray identifierArray =virtualAccountObj.getJSONArray("identifier");
                            for (int i = 0; i < identifierArray.length(); i++) {
                                JSONObject identifierDetails = identifierArray.getJSONObject(i);
                                String name = identifierDetails.getString("name");
                                if("TRANSACTION_ID".equalsIgnoreCase(name)){
                                    transactionId = identifierDetails.getString("value");
                                }
                            }
                        }
                    }

                    if(jsonInquiryResponse.has("error")) {
                        JSONObject errorObj = jsonInquiryResponse.getJSONObject("error");
                        if(functions.isValueNull(errorObj.getString("message"))){
                            description =  errorObj.getString("message");
                        }
                        if(functions.isValueNull(errorObj.getString("code"))){
                            error_code =  errorObj.getString("code");
                        }
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");

                        if("idempotent_request".equalsIgnoreCase(error_code) || "request_time_out_of_range".equalsIgnoreCase(error_code))
                        {
                            description = "Transaction pending, please check transaction status after sometime";
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");

                        }
                    }
                }

                transactionLogger.error(" new inquiryResponse for " + trackingId + " -----" + jsonInquiryResponse);
            }
            else {
                description = "Transaction Pending";
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
            }




            if(functions.isValueNull(amount)){
                amount = String.format("%.2f", Double.parseDouble(amount));
                commResponseVO.setAmount(amount);
            }

            commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setMerchantId(clientId);
            commResponseVO.setErrorCode(error_code);
            commResponseVO.setDescription(description);
            commResponseVO.setRemark(description);
            commResponseVO.setArn(paymentMode);
            commResponseVO.setRrn(rrn);
            commResponseVO.setResponseHashInfo(responseHashInfo);
            commResponseVO.setBankTransactionDate(bankTransDate);
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----for "+trackingId+ "is", e);
        }

        return commResponseVO;
    }
}
