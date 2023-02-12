package com.payment.bnmquick;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
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

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class BnmQuickPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(BnmQuickPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "bnmquick";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.bnmquick");
    private final static ResourceBundle RB_WL           = LoadProperties.getProperty("com.directi.pg.PGWALLETS");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public BnmQuickPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of BnmQuickPAymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());

        boolean isTest          = gatewayAccount.isTest();
        String REQUEST_URL      = "";
        String requestString    = "";
        String PaymentType      = "";

        String merchent_id        = gatewayAccount.getMerchantId();
        String Token                = gatewayAccount.getFRAUD_FTP_USERNAME();

        String autoRedirect      =commRequestVO.getAutoRedirectFlag();

        String MobileNo     = "9999999999";
        //OrderAmountData
        String OrderAmount  = BnmQuickUtils.getDoubleAmount(transactionDetailsVO.getAmount());
        String OrderType    = "";
        String OrderId      = trackingID;
        String OrderStatus  = "Initiating";

        String IpAddress        = "192.168.0.1";
        String BankName         = "";
        String cardType         = transactionDetailsVO.getCardType();
        PaymentType     = BnmQuickUtils.getPaymentType(payment_Card);
        String Vpa      = commRequestVO.getCustomerId();
        String brandName        = GatewayAccountService.getCardType(cardType);
        String customer_email    = "";
        String call_back    = "";
        try
        {
            if(isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            call_back= RB.getString("CALLBACK_URL")+trackingID;

            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                customer_email = commAddressDetailsVO.getEmail();
            }

            transactionlogger.error("IpAddress ------> " + IpAddress);
            if(functions.isValueNull(commAddressDetailsVO.getPhone())) {

                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    MobileNo = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                else{
                    MobileNo = commAddressDetailsVO.getPhone();

                }
                if(MobileNo.length()>10){

                    MobileNo=MobileNo.substring(MobileNo.length() - 10);
                }
                else{
                    MobileNo = commAddressDetailsVO.getPhone();

                }

            }
            else{
                MobileNo= BnmQuickUtils.geDummyMobileNo();
            }


            transactionlogger.error("REQUEST_URL " + trackingID + " " + REQUEST_URL + " IpAddress::" + IpAddress + " PaymentType::"+PaymentType);


            JSONObject requestJson=new JSONObject();
            requestJson.put("merchent_id",merchent_id);
            requestJson.put("email_id",customer_email);
            requestJson.put("order_id",trackingID);
            requestJson.put("mobile_no",MobileNo);
            requestJson.put("amount",OrderAmount);
            requestJson.put("vpa",Vpa);
            requestJson.put("call_back", call_back);

            transactionlogger.error("bnmquick REQUEST ======>" + trackingID +" "+requestJson.toString());

            String responseString =  BnmQuickUtils.doPostHTTPSURLConnectionClient(REQUEST_URL,requestJson.toString(),Token);

            transactionlogger.error("bnmquick Response=======> " + trackingID+" "+  responseString.toString());

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject  jsonObject=new JSONObject(responseString);
                JSONObject  dataObject=null;

                String respStatus="";
                String message="";
                String paymentStatus="";
                String orderId="";
                String txnId="";

                if(jsonObject != null ){


                    if(jsonObject.has("data")){
                        dataObject=jsonObject.getJSONObject("data");
                    }

                    if(dataObject != null){

                        if(dataObject.has("paymentStatus")){
                            paymentStatus=dataObject.getString("paymentStatus");
                        }
                        if(dataObject.has("orderId")){
                            orderId=dataObject.getString("orderId");
                        }
                        if(dataObject.has("txnId")){
                            txnId=dataObject.getString("txnId");
                        }
                    }
                }

                if("Pending".equalsIgnoreCase(paymentStatus)){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(paymentStatus);
                    commResponseVO.setRemark(paymentStatus);
                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
                if(functions.isValueNull(txnId))
                {
                    BnmQuickUtils.updateOrderid(txnId,trackingID);
                }

            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }



        }catch (Exception e){
            transactionlogger.error("BnmQuickPaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in bnmquick ---- ");
        String html                                 = "";
        Comm3DResponseVO transRespDetails           = null;
        BnmQuickUtils payGUtils                         = new BnmQuickUtils();
        BnmQuickPaymentProcess payGPaymentProcess   = new BnmQuickPaymentProcess();
        CommRequestVO commRequestVO             = payGUtils.getBnmQuickPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(accountId);

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
                html = payGPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect PayG form -- >>"+commonValidatorVO.getTrackingid() +" " +html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in BnmQuickPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of BnmQuickPaymentGateway---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                = gatewayAccount.isTest();
        Functions functions           = new Functions();
        BnmQuickUtils bnmQuickUtils           = new BnmQuickUtils();
        StringBuffer parameters       = new StringBuffer();


        String MerchantKeyId        = gatewayAccount.getMerchantId();
        String Token     = gatewayAccount.getFRAUD_FTP_USERNAME();

        String orderKeyID           = commTransactionDetailsVO.getPreviousTransactionId();

        JSONObject reqjsonObject = new JSONObject();

        String responseString = "";
        String REQUEST_URL = "";
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

            reqjsonObject.put("orderid", trackingId);
            reqjsonObject.put("txnid", orderKeyID);

            transactionlogger.error("inquiry req is --> "+trackingId+" "+REQUEST_URL+ " ----> "+reqjsonObject.toString());

            responseString = BnmQuickUtils.doPostHTTPSURLConnectionClient(REQUEST_URL,reqjsonObject.toString(),Token);
            transactionlogger.error("inquiry res is -- > " + trackingId + " " + responseString);

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject  jsonObject=new JSONObject(responseString);
                JSONObject  dataObject=null;

                String amount="";
                String txnStatus="";
                String txnId="";


                if(jsonObject != null ){


                    if(jsonObject.has("data")){
                        dataObject=jsonObject.getJSONObject("data");
                    }

                    if(dataObject != null){

                        if(dataObject.has("amount")){
                            amount=dataObject.getString("amount");
                        }
                        if(dataObject.has("txnStatus")){
                            txnStatus=dataObject.getString("txnStatus");
                        }
                        if(dataObject.has("txnId")){
                            txnId=dataObject.getString("txnId");
                        }
                    }
                }

                if("Success".equalsIgnoreCase(txnStatus)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setAmount(amount);

                }
                else  if("Pending".equalsIgnoreCase(txnStatus)){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setAmount(amount);

                }
                else if("Failed".equalsIgnoreCase(txnStatus)){
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setAmount(amount);

                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }


            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (JSONException e)
        {
            transactionlogger.error("BnmQuickPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("BnmQuickPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BnmQuickPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside Bnmquick process payout-----");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();

        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest          = gatewayAccount.isTest();
        String REQUEST_URL      = "";
        String AccountNumber      = "";
        String MobileNo     = "";
        String merchent_id        = gatewayAccount.getMerchantId();
        String Token                = gatewayAccount.getFRAUD_FTP_USERNAME();
        //OrderAmountData
        String OrderAmount  = BnmQuickUtils.getDoubleAmount(commTransactionDetailsVO.getAmount());
        String IFSCCode     = "";

        String call_back    = "";
        try
        {
            if(isTest){
                REQUEST_URL = RB.getString("PAYOUT_TEST_URL");
            }else{
                REQUEST_URL = RB.getString("PAYOUT_LIVE_URL");
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())) {

                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    MobileNo = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                else{
                    MobileNo = commAddressDetailsVO.getPhone();

                }
                if(MobileNo.length()>10){

                    MobileNo=MobileNo.substring(MobileNo.length() - 10);
                }
                else{
                    MobileNo = commAddressDetailsVO.getPhone();

                }

            }
            else{
                MobileNo= BnmQuickUtils.geDummyMobileNo();
            }
            call_back= RB.getString("CALLBACK_URL")+trackingId;

            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                AccountNumber = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                IFSCCode = commTransactionDetailsVO.getBankIfsc();
            }
            JSONObject requestJson=new JSONObject();
            requestJson.put("merchent_id",merchent_id);
            requestJson.put("order_id",trackingId);
            requestJson.put("mobile_no",MobileNo);
            requestJson.put("amount",OrderAmount);
            requestJson.put("ac_no",AccountNumber);
            requestJson.put("ifsc", IFSCCode);

            transactionlogger.error("REQUEST ======>" + trackingId +" "+requestJson.toString());

            String responseString =  BnmQuickUtils.doPostHTTPSURLConnectionClient(REQUEST_URL,requestJson.toString(),Token);

            transactionlogger.error("Response=======> " + trackingId+" "+  responseString.toString());

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject  jsonObject=new JSONObject(responseString);
                JSONObject  dataObject=null;

                String respStatus="";
                String message="";
                String paymentStatus="";
                String orderId="";
                String txnId="";
                String rrn="";

                if(jsonObject != null ){


                    if(jsonObject.has("data")){
                        dataObject=jsonObject.getJSONObject("data");
                    }

                    if(dataObject != null){

                        if(dataObject.has("paymentStatus")){
                            paymentStatus=dataObject.getString("paymentStatus");
                        }
                        if(dataObject.has("orderId")){
                            orderId=dataObject.getString("orderId");
                        }
                        if(dataObject.has("txnId")){
                            txnId=dataObject.getString("txnId");
                        }
                        if(dataObject.has("rrn")){
                            rrn=dataObject.getString("rrn");
                        }
                    }
                }

                if("SUCCESS".equalsIgnoreCase(paymentStatus)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(paymentStatus);
                    commResponseVO.setRemark(paymentStatus);
                    commResponseVO.setRrn(rrn);

                }else if("Failed".equalsIgnoreCase(paymentStatus)){
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(paymentStatus);
                    commResponseVO.setRemark(paymentStatus);

                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
            String CALL_EXECUTE_AFTER       = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL    = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC          = RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed          = RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncBnmQuickPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }


        }catch (Exception e){
            transactionlogger.error("BnmQuickPaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside BnmQuick processPayoutInquiry inquiry------->");

        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                = gatewayAccount.isTest();
        Functions functions           = new Functions();
        BnmQuickUtils bnmQuickUtils           = new BnmQuickUtils();
        StringBuffer parameters       = new StringBuffer();


        String MerchantKeyId        = gatewayAccount.getMerchantId();
        String Token     = gatewayAccount.getFRAUD_FTP_USERNAME();

        String orderKeyID           = commTransactionDetailsVO.getPreviousTransactionId();

        JSONObject reqjsonObject = new JSONObject();

        String responseString = "";
        String REQUEST_URL = "";
        try
        {


            if (isTest)
            {
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            reqjsonObject.put("order_id", trackingId);
            reqjsonObject.put("merchent_id", MerchantKeyId);

            transactionlogger.error("inquiry req is --> "+trackingId+" "+REQUEST_URL+ " ----> "+reqjsonObject.toString());

            responseString = BnmQuickUtils.doPostHTTPSURLConnectionClient(REQUEST_URL,reqjsonObject.toString(),Token);
            transactionlogger.error("inquiry res is -- > " + trackingId + " " + responseString);

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject  jsonObject=new JSONObject(responseString);
                JSONObject  dataObject=null;

                String amount="";
                String txnStatus="";
                String txnId="";


                if(jsonObject != null ){


                    if(jsonObject.has("data")){
                        dataObject=jsonObject.getJSONObject("data");
                    }

                    if(dataObject != null){

                        if(dataObject.has("paymentAmount")){
                            amount=dataObject.getString("paymentAmount");
                            amount=String.format("%.2f", Double.parseDouble(amount));
                        }
                        if(dataObject.has("paymentStatus")){
                            txnStatus=dataObject.getString("paymentStatus");
                        }
                        if(dataObject.has("txnId")){
                            txnId=dataObject.getString("txnId");
                        }
                    }
                }

                if("Success".equalsIgnoreCase(txnStatus)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setAmount(amount);

                }
                else if(txnStatus.contains("pending")||txnStatus.contains("PENDING")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setAmount(amount);

                }
                else if("Failed".equalsIgnoreCase(txnStatus)){
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setTransactionId(txnId);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setAmount(amount);

                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }


            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (JSONException e)
        {
            transactionlogger.error("BnmQuickPaymentGateway processPayoutInquery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("BnmQuickPaymentGateway processPayoutInquery Exception--->",e);
        }
        return commResponseVO;

    }

}