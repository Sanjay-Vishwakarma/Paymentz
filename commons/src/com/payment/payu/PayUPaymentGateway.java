package com.payment.payu;

import com.directi.pg.Base64;
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

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 3/8/2021.
 */
public class PayUPaymentGateway  extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(PayUPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "payu";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.payupay");
    private final static ResourceBundle RB_NB           = LoadProperties.getProperty("com.directi.pg.PUBANKS");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public PayUPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of PayUPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        boolean isTest      = gatewayAccount.isTest();
        String REQUEST_URL  = "";
        Map<String, String> requestMap      = new HashMap<String, String>();
        Map<String, String> requestLogMap   = null;
        String SALT             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String KEY_ID           = gatewayAccount.getFRAUD_FTP_USERNAME();
        String  PAYMENT_TYPE    = PayUUtils.getPaymentType(payment_Card);//CARD/WL/NB
        String RETURN_URL       = RB.getString("RETURN_URL")+trackingID;

        String txnid        = trackingID;
        String amount       = transactionDetailsVO.getAmount();
        String productinfo  = "SALES";

        String firstname    = "";
        String email        = "";
        String phone        = "";
        String pg           = ""; //paymentMode CC/DC/NB
        String HASH         = "";
        String vpa          = commRequestVO.getCustomerId();
        //Card Details
        String ccnum        = commCardDetailsVO.getCardNum();
        String ccname       = "";
        String ccvv         = commCardDetailsVO.getcVV();
        String ccexpmon     = commCardDetailsVO.getExpMonth();
        String ccexpyr      = commCardDetailsVO.getExpYear();
        String txn_s2s_flow     = "4";
        String s2s_client_ip    = "4";

        try
        {
            if(isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
            {
                firstname   = commAddressDetailsVO.getFirstname().trim();
            }
            else {
                firstname  = "customer";
            }

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                ccname = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
                ccname = ccname.trim();
            }else {
                ccname  = "customer";
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
                email   = commAddressDetailsVO.getEmail().trim();
            else {
                email  = "customer@gmail.com";
            }

            if(functions.isValueNull(commAddressDetailsVO.getPhone())) {

                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    phone = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                if(phone.length()>10){

                    phone   = phone.substring(phone.length() - 10);
                }
                else{
                    phone = commAddressDetailsVO.getPhone();
                }

            }else {
                phone  = "9999999999";
            }

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
                s2s_client_ip =commAddressDetailsVO.getCardHolderIpAddress();
            }

            String strForHash       = KEY_ID+"|"+txnid+"|"+amount+"|"+productinfo+"|"+firstname+"|"+email+"|||||||||||"+SALT;

            HASH                    = PayUUtils.SHA512forSales(strForHash);

            requestMap.put("key", KEY_ID);
            requestMap.put("txnid", txnid);
            requestMap.put("amount", amount);
            requestMap.put("productinfo", productinfo);
            requestMap.put("firstname", firstname);
            requestMap.put("email", email);
            requestMap.put("phone", phone);
            requestMap.put("surl", RETURN_URL+"&sucess=sucess");
            requestMap.put("furl", RETURN_URL+"&failed=failed");
            requestMap.put("curl", RETURN_URL+"&cancel=cancel");
            requestMap.put("pg", PAYMENT_TYPE);
            requestMap.put("hash", HASH);
            //requestMap.put("txn_s2s_flow", txn_s2s_flow);
            //requestMap.put("s2s_client_ip", s2s_client_ip);

            if("CC".equalsIgnoreCase(PAYMENT_TYPE) || "DC".equalsIgnoreCase(PAYMENT_TYPE))
            {
                requestMap.put("ccnum", ccnum);
                requestMap.put("ccname", ccname);
                requestMap.put("ccvv", ccvv);
                requestMap.put("ccexpmon", ccexpmon);
                requestMap.put("ccexpyr", ccexpyr);

            }else if("UPI".equalsIgnoreCase(PAYMENT_TYPE)){
                requestMap.put("vpa", vpa);
                requestMap.put("bankcode", PAYMENT_TYPE);
            }
            else if("NB".equalsIgnoreCase(PAYMENT_TYPE))
            {
                requestMap.put("bankcode", payment_brand);
            }
            else{
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");

                return commResponseVO ;
           }

            requestLogMap = new HashMap<String, String>(requestMap);
            if(requestLogMap != null){
                if(requestMap.containsKey("ccnum")){
                    requestLogMap.put("ccnum",functions.maskingPan(requestMap.get("ccnum"))) ;
                }
                if(requestMap.containsKey("ccname")){
                    requestLogMap.put("ccname",functions.getNameMasking(requestMap.get("ccname"))) ;
                }
                if(requestMap.containsKey("ccvv")){
                    requestLogMap.put("ccvv",functions.maskingNumber(requestMap.get("ccvv")));
                }
                if(requestMap.containsKey("ccexpmon")){
                    requestLogMap.put("ccexpmon",functions.maskingNumber(requestMap.get("ccexpmon")));
                }
                if(requestMap.containsKey("ccexpyr")){
                    requestLogMap.put("ccexpyr",functions.maskingNumber(requestMap.get("ccexpyr")));
                }
            }


            //transactionlogger.error("resquestString "+trackingID+" "+requestMap.toString());
            transactionlogger.error("requestLogMap "+trackingID+" "+requestLogMap.toString());

            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
            commResponseVO.setRequestMap(requestMap);

        }catch (Exception e){
            transactionlogger.error("PayUPaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in PayU ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        PayUUtils payUUtils                     = new PayUUtils();
        PayUPaymentProcess payUPaymentProcess   = new PayUPaymentProcess();
        CommRequestVO commRequestVO             = payUUtils.getPayURequestVO(commonValidatorVO);
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
                html = payUPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect PayU form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in PayUPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of PayU---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                = gatewayAccount.isTest();
        Functions functions           = new Functions();
        PayUUtils payUUtils           = new PayUUtils();
        StringBuffer parameters       = new StringBuffer();

        String SALT          = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String KEY_ID        = gatewayAccount.getFRAUD_FTP_USERNAME();
        String command       = "check_payment";
        String REQUEST_URL   = "";
        String var1          = commTransactionDetailsVO.getPreviousTransactionId();
        String hash          = "";

        String inquiry_res = "";
        try
        {
            String strForHash           = KEY_ID+"|"+command+"|"+var1+"|"+SALT;
            String plainCredentials     = KEY_ID + ":" + SALT;
            String base64Credentials    = new String(Base64.encode(plainCredentials.getBytes()));

            hash                = PayUUtils.SHA512forSales(strForHash);

            parameters.append("key=" + KEY_ID);
            parameters.append("&var1=" + var1);
            parameters.append("&command=" + command);
            parameters.append("&hash=" + hash);

            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            transactionlogger.error("strForHash  --- >"+strForHash);
            transactionlogger.error("REQUEST_URL is -- >"+REQUEST_URL);
            transactionlogger.error("inquiry req is --> "+trackingId+" "+parameters.toString());

            inquiry_res = payUUtils.doGetHTTPSURLConnectionClient(REQUEST_URL,parameters.toString(),base64Credentials);
            transactionlogger.error("inquiry res is -- > "+trackingId+" "+inquiry_res);

            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String status           = "";
                String transactionId    = "";
                String approvedAmount   = "";
                String dateTime         = "";
                String bank_ref_num     = "";
                String unmappedstatus   = "";
                String mode             = "";
                String bankcode         = "";
                String field7           = "";

                JSONObject resJONObject = new JSONObject(inquiry_res);
                JSONObject jsonObject   = null;
                if(resJONObject.has("transaction_details"))
                {
                    jsonObject = new JSONObject();
                    jsonObject = resJONObject.getJSONObject("transaction_details");

                    if (jsonObject.has("mihpayid"))
                    {
                        transactionId = jsonObject.getString("mihpayid");
                    }
                    if (jsonObject.has("bank_ref_num"))
                    {
                        bank_ref_num = jsonObject.getString("bank_ref_num");
                    }

                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                    }
                    if (jsonObject.has("amount"))
                    {
                        approvedAmount = jsonObject.getString("amount");
                    }
                    if (jsonObject.has("unmappedstatus"))
                    {
                        unmappedstatus = jsonObject.getString("unmappedstatus");
                    }

                    if (jsonObject.has("addedon"))
                    {
                        dateTime = jsonObject.getString("addedon");
                    }
                    if (jsonObject.has("mode"))
                    {
                        mode = jsonObject.getString("mode");
                    }
                    if (jsonObject.has("bankcode"))
                    {
                        bankcode = jsonObject.getString("bankcode");
                    }
                    if (jsonObject.has("field7"))
                    {
                        field7 = jsonObject.getString("field7");

                        /*if(field7.contains("|")){
                            field7 = field7.split("|")[0];
                        }*/

                    }

                    if(functions.isValueNull(mode) && mode.equalsIgnoreCase("NB")){
                       // payUUtils.updateTransaction(trackingId,bankcode);
                    }

                    if (status.equalsIgnoreCase("success"))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(unmappedstatus);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setRrn(bank_ref_num);
                        commResponseVO.setTransactionType(mode);
                        commResponseVO.setMerchantId(KEY_ID);

                        payUUtils.updateRRNMainTableEntry(bank_ref_num,trackingId);
                    }
                    else if(status.equalsIgnoreCase("failure") || status.equalsIgnoreCase("failed"))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(unmappedstatus);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setTransactionType(mode);
                        commResponseVO.setMerchantId(KEY_ID);
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }

                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }
            // no response set pending
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

            commResponseVO.setTransactionType(PZProcessType.SALE.toString());

        }catch (JSONException e)
        {
            transactionlogger.error("PayUPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("PayUPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PayUPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
}