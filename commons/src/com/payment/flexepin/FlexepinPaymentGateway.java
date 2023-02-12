package com.payment.flexepin;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.apache.log4j.LogManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2022-06-30.
 */
public class FlexepinPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "Flexepin";
    private final static ResourceBundle RB  = LoadProperties.getProperty("com.directi.pg.Flexepin");

    private static TransactionLogger transactionLogger = new TransactionLogger(FlexepinPaymentGateway.class.getName());
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public FlexepinPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("FlexepinPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering in processSale() for FlexepinPaymentGateway------>");
        Comm3DRequestVO commRequestVO                       = (Comm3DRequestVO) requestVO;
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String api_key                   = gatewayAccount.getMerchantId();
        String api_secret                = gatewayAccount.getPassword();
        boolean isTest                   = gatewayAccount.isTest();
        String nounce                    = trackingID;
        String Request_Method            = "";
        String Request_URL               = "";
        String pin                       = "";
        String terminal_id               = "";
        String customer_ip               = "";
        String signatureData             = "";
        String AUTHENTICATION            = "";
        String encryptedSignature        = "";
        String Voucher_Validate_Response = "";
        String Voucher_Redeem_Response   = "";
        String trans_mode                = "Non-3D";
        HashMap<String,String> hashMap   = null;
        String toType                    =  commTransactionDetailsVO.getTotype();

        transactionLogger.error("toType >>>>>>>>>> "+trackingID+" "+toType);

        try
        {
            transactionLogger.error("commTransactionDetailsVO.getAmount(): " + commTransactionDetailsVO.getAmount());
            if (isTest)
                Request_URL = RB.getString("TEST_SALE_Voucher_Validate_URL");
            else
                Request_URL = RB.getString("LIVE_SALE_Voucher_Validate_URL");

            Request_Method = "GET";
            pin            = commCardDetailsVO.getVoucherNumber();
            terminal_id    = trackingID;   //"WEB_"+trackingID;   //FACILEROWEB

            String reqUrl[] = Request_URL.split("/");
            signatureData += Request_Method + "\n"+"/"+reqUrl[3]+"/"+reqUrl[4]+"/"+pin+"/"+terminal_id+"/"+nounce + "\n"+nounce + "\n";
            String signatureDatalog = Request_Method + "\n"+"/"+reqUrl[3]+"/"+reqUrl[4]+"/"+functions.maskingPan(pin)+"/"+terminal_id+"/"+nounce + "\n"+nounce + "\n";
            transactionLogger.error(trackingID + " Voucher Validate Signature data: " + signatureDatalog);

            encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
            String encryptedSignatureLOG = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
            transactionLogger.error(trackingID + " Voucher Validate Encrypted Signature: " + encryptedSignatureLOG);

            AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;
            String Request_URLLOG = Request_URL+ "/"+functions.maskingPan(pin)+"/"+terminal_id+"/"+nounce+"\n";
            Request_URL += "/"+pin+"/"+terminal_id+"/"+nounce+"\n";

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin Sale Request "+ trackingID + " Voucher Validate  URL: " + Request_URLLOG);
            else
                transactionLogger.error("Flexepin Sale Request "+ trackingID + " Voucher Validate  URL: " + Request_URLLOG);

            Voucher_Validate_Response = FlexepinUtils.doGetHTTPUrlConnection(Request_URL, AUTHENTICATION,trackingID);

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin Sale Response " + trackingID + " Voucher Validate Response: " + Voucher_Validate_Response.trim());
            else
                transactionLogger.error("Flexepin Sale Response " + trackingID + " Voucher Validate Response: " + Voucher_Validate_Response.trim());

            if (functions.isValueNull(Voucher_Validate_Response.trim()) && FlexepinUtils.isJSONValid(Voucher_Validate_Response.trim()))
            {
                String VVtransaction_id = "";
                String VVtrans_no       = "";
                String VVresult         = "";
                String VVserial         = "";
                String VVvalue          = "";
                String VVcost           = "";
                String VVcurrency       = "";
                String VVstatus         = "";
                String VVerror          = "";
                String VVdescription    = "";
                String VVresult_description = "";

                JSONObject VVresponseJson = new JSONObject(Voucher_Validate_Response.trim());

                if (VVresponseJson.has("transaction_id") && functions.isValueNull(VVresponseJson.getString("transaction_id")))
                    VVtransaction_id = VVresponseJson.getString("transaction_id");

                if (VVresponseJson.has("trans_no") && functions.isValueNull(VVresponseJson.getString("trans_no")))
                    VVtrans_no = VVresponseJson.getString("trans_no");

                if (VVresponseJson.has("result") && functions.isValueNull(VVresponseJson.getString("result")))
                    VVresult = VVresponseJson.getString("result");

                if (VVresponseJson.has("result_description") && functions.isValueNull(VVresponseJson.getString("result_description")))
                    VVresult_description = VVresponseJson.getString("result_description");

                if (VVresponseJson.has("serial") && functions.isValueNull(VVresponseJson.getString("serial")))
                    VVserial = VVresponseJson.getString("serial");

                if (VVresponseJson.has("value") && functions.isValueNull(VVresponseJson.getString("value")))
                    VVvalue = VVresponseJson.getString("value");

                if (VVresponseJson.has("cost") && functions.isValueNull(VVresponseJson.getString("cost")))
                    VVcost = VVresponseJson.getString("cost");

                if (VVresponseJson.has("currency") && functions.isValueNull(VVresponseJson.getString("currency")))
                    VVcurrency = VVresponseJson.getString("currency");

                if (VVresponseJson.has("status") && functions.isValueNull(VVresponseJson.getString("status")))
                    VVstatus = VVresponseJson.getString("status");

                if (VVresponseJson.has("description") && functions.isValueNull(VVresponseJson.getString("description")))
                    VVdescription = VVresponseJson.getString("description");

                if (VVresponseJson.has("error") && functions.isValueNull(VVresponseJson.getString("error")))
                    VVerror = VVresponseJson.getString("error");

                hashMap = new HashMap<>();
                hashMap.put("transaction_id",VVtransaction_id);
                hashMap.put("trans_no",VVtrans_no);
                hashMap.put("result",VVresult);
                hashMap.put("description",VVdescription);
                hashMap.put("result_description",VVresult_description);
                hashMap.put("serial",VVserial);
                hashMap.put("value",VVvalue);
                hashMap.put("cost",VVcost);
                hashMap.put("currency",VVcurrency);
                hashMap.put("status",VVstatus);
                hashMap.put("voucherNumber",pin);

                if ("0".equals(VVresult) && "ACTIVE".equalsIgnoreCase(VVstatus))
                {
                    FlexepinUtils.updateMainTableEntry(VVtrans_no, trans_mode,"", trackingID);

                    if (isTest)
                        Request_URL = RB.getString("TEST_SALE_Voucher_Redeem_URL");
                    else
                        Request_URL = RB.getString("LIVE_SALE_Voucher_Redeem_URL");

                    Request_Method = "PUT";
                    pin            = commCardDetailsVO.getVoucherNumber();
                    terminal_id    =  trackingID;   //"WEB_"+trackingID;

                    if (functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                        customer_ip = commAddressDetailsVO.getCardHolderIpAddress();
                    else
                        customer_ip = "192.168.1.1";

                    reqUrl = Request_URL.split("/");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("customer_id",nounce);
                    jsonObject.put("customer_ip",customer_ip);

                    signatureData = Request_Method + "\n"+"/"+reqUrl[3]+"/"+reqUrl[4]+"/"+pin+"/"+terminal_id+"/"+nounce + "\n"+nounce + "\n"+jsonObject;
                    signatureDatalog = Request_Method + "\n"+"/"+reqUrl[3]+"/"+reqUrl[4]+"/"+functions.maskingPan(pin)+"/"+terminal_id+"/"+nounce + "\n"+nounce + "\n"+jsonObject;
                    transactionLogger.error(trackingID + " Voucher Redeem Signature data: " + signatureDatalog);

                    encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
                    transactionLogger.error(trackingID + " Voucher Redeem Encrypted Signature: " + encryptedSignature);

                    AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;
                    Request_URLLOG = Request_URL + "/"+functions.maskingPan(pin)+"/"+terminal_id+"/"+nounce;
                    Request_URL += "/"+pin+"/"+terminal_id+"/"+nounce;

                    if("Facilero".equalsIgnoreCase(toType))
                        facileroLogger.error("Flexepin Sale Request " +trackingID + " Voucher Redeem Request ------> URL: " + Request_URLLOG + " " + "RequestBody: " + jsonObject);
                    else
                        transactionLogger.error("Flexepin Sale Request " +trackingID + " Voucher Redeem Request ------> URL: " + Request_URLLOG + " " + "RequestBody: " + jsonObject);

                    Voucher_Redeem_Response = FlexepinUtils.doPutHTTPUrlConnection(Request_URL, AUTHENTICATION, jsonObject.toString(), trackingID);

                    if("Facilero".equalsIgnoreCase(toType))
                        facileroLogger.error("Flexepin Sale Response "+ trackingID + " Voucher Redeem Response: " + Voucher_Redeem_Response.trim());
                    else
                        transactionLogger.error("Flexepin Sale Response "+ trackingID + " Voucher Redeem Response: " + Voucher_Redeem_Response.trim());

                    if (functions.isValueNull(Voucher_Redeem_Response.trim()) && FlexepinUtils.isJSONValid(Voucher_Redeem_Response.trim()))
                    {
                        String VRtransaction_id = "";
                        String VRtrans_no       = "";
                        String VRresult         = "";
                        String VRean            = "";
                        String VRserial         = "";
                        String VRvalue          = "";
                        String VRcost           = "";
                        String VRcurrency       = "";
                        String VRstatus         = "";
                        String VRresidual_value = "";
                        String VRerror          = "";
                        String VRdescription    = "";
                        String VRresult_description = "";

                        JSONObject VRresponseJson = new JSONObject(Voucher_Redeem_Response.trim());

                        if (VRresponseJson.has("transaction_id") && functions.isValueNull(VRresponseJson.getString("transaction_id")))
                            VRtransaction_id = VRresponseJson.getString("transaction_id");

                        if (VRresponseJson.has("trans_no") && functions.isValueNull(VRresponseJson.getString("trans_no")))
                            VRtrans_no = VRresponseJson.getString("trans_no");

                        if (VRresponseJson.has("result") && functions.isValueNull(VRresponseJson.getString("result")))
                            VRresult = VRresponseJson.getString("result");

                        if (VRresponseJson.has("result_description") && functions.isValueNull(VRresponseJson.getString("result_description")))
                            VRresult_description = VRresponseJson.getString("result_description");

                        if (VRresponseJson.has("serial") && functions.isValueNull(VRresponseJson.getString("serial")))
                            VRserial = VRresponseJson.getString("serial");

                        if (VRresponseJson.has("value") && functions.isValueNull(VRresponseJson.getString("value")))
                            VRvalue = VRresponseJson.getString("value");

                        if (VRresponseJson.has("cost") && functions.isValueNull(VRresponseJson.getString("cost")))
                            VRcost = VRresponseJson.getString("cost");

                        if (VRresponseJson.has("currency") && functions.isValueNull(VRresponseJson.getString("currency")))
                            VRcurrency = VRresponseJson.getString("currency");

                        if (VRresponseJson.has("status") && functions.isValueNull(VRresponseJson.getString("status")))
                            VRstatus = VRresponseJson.getString("status");

                        if (VRresponseJson.has("description") && functions.isValueNull(VRresponseJson.getString("description")))
                            VRdescription = VRresponseJson.getString("description");

                        if (VRresponseJson.has("residual_value") && functions.isValueNull(VRresponseJson.getString("residual_value")))
                            VRresidual_value = VRresponseJson.getString("residual_value");

                        if (VRresponseJson.has("error") && functions.isValueNull(VRresponseJson.getString("error")))
                            VRerror = VRresponseJson.getString("error");

                        hashMap.put("transaction_id",VRtransaction_id);
                        hashMap.put("trans_no",VRtrans_no);
                        hashMap.put("result",VRresult);
                        hashMap.put("description",VRdescription);
                        hashMap.put("result_description",VRresult_description);
                        hashMap.put("serial",VRserial);
                        hashMap.put("value",VRvalue);
                        hashMap.put("cost",VRcost);
                        hashMap.put("currency",VRcurrency);
                        hashMap.put("status",VRstatus);
                        hashMap.put("residual_value",VRresidual_value);
                        hashMap.put("voucherNumber",pin);

                        if ("0".equals(VRresult) && "USED".equalsIgnoreCase(VRstatus))
                        {
                            comm3DResponseVO.setStatus("success");
                            comm3DResponseVO.setTransactionStatus("success");
                            comm3DResponseVO.setRemark(VRresult_description);
                            comm3DResponseVO.setDescription(VRresult_description);
                            comm3DResponseVO.setErrorCode(VRresult);
                            comm3DResponseVO.setCurrency(VRcurrency);
                            comm3DResponseVO.setBankRefNo(VRtrans_no);
                            comm3DResponseVO.setAmount(VRvalue);
                            comm3DResponseVO.setAuthCode(VRean);
                            comm3DResponseVO.setRequestMap(hashMap);
                            comm3DResponseVO.setResponseHashInfo(VRtransaction_id);
                            comm3DResponseVO.setTransactionId(VRtrans_no);
                            comm3DResponseVO.setMerchantId(api_key);
                        }
                        else if ("401".equals(VRresult) || "503".equals(VRresult) || "4019".equals(VRresult) || "4071".equals(VRresult) || "4072".equals(VRresult) || "4073".equals(VRresult) || "4074".equals(VRresult) || "4075".equals(VRresult) || "4080".equals(VRresult) || "5000".equals(VRresult) || "4099".equals(VRresult))
                        {
                            comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setTransactionStatus("failed");
                            comm3DResponseVO.setTransactionId(VRtrans_no);
                            comm3DResponseVO.setDescription(VRresult_description);
                            comm3DResponseVO.setErrorCode(VRresult);
                            comm3DResponseVO.setRequestMap(hashMap);
                            if (functions.isValueNull(VRresult_description))
                            {
                                comm3DResponseVO.setRemark(VRresult_description);
                                comm3DResponseVO.setDescription(VRresult_description);
                            }
                            else
                            {
                                comm3DResponseVO.setRemark(VRdescription);
                                comm3DResponseVO.setDescription(VRdescription);
                            }
                        }
                        else if ("401".equals(VRerror) || "403".equals(VRerror) || "503".equals(VRerror) || "4019".equals(VRerror) || "4071".equals(VRerror) || "4072".equals(VRerror) || "4073".equals(VRerror) || "4074".equals(VRerror) || "4075".equals(VRerror) || "4080".equals(VRerror) || "5000".equals(VRerror) || "4099".equals(VRerror))
                        {
                            comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setTransactionStatus("failed");
                            comm3DResponseVO.setTransactionId(VRtrans_no);
                            comm3DResponseVO.setDescription(VRresult_description);
                            comm3DResponseVO.setErrorCode(VRresult);
                            comm3DResponseVO.setRequestMap(hashMap);
                            if (functions.isValueNull(VRresult_description))
                            {
                                comm3DResponseVO.setRemark(VRresult_description);
                                comm3DResponseVO.setDescription(VRresult_description);
                            }
                            else
                            {
                                comm3DResponseVO.setRemark(VRdescription);
                                comm3DResponseVO.setDescription(VRdescription);
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setTransactionStatus("pending");
                            comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        }
                        FlexepinUtils.updateMainTableEntry(VRtrans_no, trans_mode ,VRean, trackingID);
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    }
                }
                else if ("401".equals(VVresult) || "503".equals(VVresult) || "4019".equals(VVresult) || "4071".equals(VVresult) || "4072".equals(VVresult) || "4073".equals(VVresult) || "4074".equals(VVresult) || "4075".equals(VVresult) || "4080".equals(VVresult) || "5000".equals(VVresult) || "4099".equals(VVresult))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setTransactionId(VVtrans_no);
                    comm3DResponseVO.setRequestMap(hashMap);
                    comm3DResponseVO.setDescription(VVresult_description);
                    comm3DResponseVO.setErrorCode(VVresult);
                    if (functions.isValueNull(VVresult_description))
                    {
                        comm3DResponseVO.setRemark(VVresult_description);
                        comm3DResponseVO.setDescription(VVresult_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(VVdescription);
                        comm3DResponseVO.setDescription(VVdescription);
                    }
                }
                else if ("401".equals(VVerror) || "403".equals(VVerror) || "503".equals(VVerror) || "4019".equals(VVerror) || "4071".equals(VVerror) || "4072".equals(VVerror) || "4073".equals(VVerror) || "4074".equals(VVerror) || "4075".equals(VVerror) || "4080".equals(VVerror) || "5000".equals(VVerror) || "4099".equals(VVerror))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setTransactionId(VVtrans_no);
                    comm3DResponseVO.setRequestMap(hashMap);
                    comm3DResponseVO.setDescription(VVresult_description);
                    comm3DResponseVO.setErrorCode(VVresult);
                    if (functions.isValueNull(VVresult_description))
                    {
                        comm3DResponseVO.setRemark(VVresult_description);
                        comm3DResponseVO.setDescription(VVresult_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(VVdescription);
                        comm3DResponseVO.setDescription(VVdescription);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
                FlexepinUtils.updateMainTableEntry(VVtrans_no, trans_mode,"", trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
            comm3DResponseVO.setThreeDVersion("Non-3D");
            if (!functions.isValueNull(comm3DResponseVO.getAmount()))
                comm3DResponseVO.setAmount(commTransactionDetailsVO.getAmount());
        }
        catch (Exception e)
        {
            transactionLogger.error(trackingID +"----> Exception while processSale():" +e);
            PZExceptionHandler.raiseTechnicalViolationException(FlexepinPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering in processQuery() for FlexepinPaymentGateway------>");
        CommRequestVO comm3DRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = comm3DRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String api_key              = gatewayAccount.getMerchantId();
        String api_secret           = gatewayAccount.getPassword();
        boolean isTest              = gatewayAccount.isTest();
        String nounce               = trackingID;
        String Request_Method       = "";
        String Request_URL          = "";
        String terminal_id          = "";
        String signatureData        = "";
        String AUTHENTICATION       = "";
        String encryptedSignature   = "";
        String response             = "";
        String trans_mode           = "Non-3D";
        String toType               =  commTransactionDetailsVO.getTotype();

        transactionLogger.error("toType >>>>>>>>>> "+trackingID+" "+toType);

        try
        {
            if (isTest)
                Request_URL = RB.getString("TEST_SALE_Transaction_Query_URL");
            else
                Request_URL = RB.getString("LIVE_SALE_Transaction_Query_URL");

            Request_Method = "GET";
            terminal_id    =  trackingID;  //"WEB_"+trackingID;   //FACILEROWEB

            String reqUrl[] = Request_URL.split("/");
            signatureData = Request_Method+"\n"+"/"+ reqUrl[3] +"/" + reqUrl[4] + "/"+nounce+"/"+terminal_id+"/"+nounce+"\n"+nounce+"\n";
            transactionLogger.error(trackingID + " Inquiry Signature data: " + signatureData);

            encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
            transactionLogger.error(trackingID + " Inquiry Encrypted Signature: " + encryptedSignature);

            AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;
            Request_URL += "/"+nounce+"/"+terminal_id+"/"+nounce+"\n";

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin Inquiry Request " + trackingID + " Request Url ----> " + Request_URL);
            else
                transactionLogger.error("Flexepin Inquiry Request " + trackingID + " Request Url ----> " + Request_URL);

            response = FlexepinUtils.doGetHTTPUrlConnection(Request_URL, AUTHENTICATION,trackingID);

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin Inquiry Response "+ trackingID + " Response: " + response.trim());
            else
                transactionLogger.error("Flexepin Inquiry Response "+ trackingID + " Response: " + response.trim());

            if (functions.isValueNull(response.trim()) && FlexepinUtils.isJSONValid(response.trim()))
            {
                String trans_no             = "";
                String transaction_id       = "";
                String timestamp            = "";
                String result               = "";
                String result_description   = "";
                String serial               = "";
                String value                = "";
                String currency             = "";
                String status               = "";
                String description          = "";
                String error                = "";

                JSONObject responseJson = new JSONObject(response.trim());

                if (responseJson.has("trans_no") && functions.isValueNull(responseJson.getString("trans_no")))
                    trans_no = responseJson.getString("trans_no");
                if (responseJson.has("transaction_id") && functions.isValueNull(responseJson.getString("transaction_id")))
                    transaction_id = responseJson.getString("transaction_id");
                if (responseJson.has("timestamp") && functions.isValueNull(responseJson.getString("timestamp")))
                    timestamp = responseJson.getString("timestamp");
                if (responseJson.has("result") && functions.isValueNull(responseJson.getString("result")))
                    result = responseJson.getString("result");
                if (responseJson.has("result_description") && functions.isValueNull(responseJson.getString("result_description")))
                    result_description = responseJson.getString("result_description");

                if (responseJson.has("error") && functions.isValueNull(responseJson.getString("error")))
                    error = responseJson.getString("error");

                if (responseJson.has("transaction") && functions.isValueNull(responseJson.getString("transaction")))
                {
                    JSONObject transactionJson = new JSONObject(responseJson.getString("transaction"));

                    if (transactionJson.has("trans_no") && functions.isValueNull(transactionJson.getString("trans_no")))
                        trans_no = transactionJson.getString("trans_no");
                    if (transactionJson.has("timestamp") && functions.isValueNull(transactionJson.getString("timestamp")))
                        timestamp = transactionJson.getString("timestamp");
                    if (transactionJson.has("result") && functions.isValueNull(transactionJson.getString("result")))
                        result = transactionJson.getString("result");
                    if (transactionJson.has("result_description") && functions.isValueNull(transactionJson.getString("result_description")))
                        result_description = transactionJson.getString("result_description");
                    if (transactionJson.has("serial") && functions.isValueNull(transactionJson.getString("serial")))
                        serial = transactionJson.getString("serial");
                    if (transactionJson.has("value") && functions.isValueNull(transactionJson.getString("value")))
                        value = transactionJson.getString("value");
                    if (transactionJson.has("currency") && functions.isValueNull(transactionJson.getString("currency")))
                        currency = transactionJson.getString("currency");
                    if (transactionJson.has("status") && functions.isValueNull(transactionJson.getString("status")))
                        status = transactionJson.getString("status");
                    if (transactionJson.has("description") && functions.isValueNull(transactionJson.getString("description")))
                        description = transactionJson.getString("description");
                    if (transactionJson.has("transaction_id") && functions.isValueNull(transactionJson.getString("transaction_id")))
                        transaction_id = transactionJson.getString("transaction_id");
                }

                if ("0".equals(result))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark(result_description);
                    comm3DResponseVO.setDescription(result_description);
                    comm3DResponseVO.setResponseTime(timestamp);
                    comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setAmount(value);
                    comm3DResponseVO.setErrorCode(result);
                    comm3DResponseVO.setBankRefNo(trans_no);
                    comm3DResponseVO.setResponseHashInfo(transaction_id);
                    comm3DResponseVO.setTransactionId(trans_no);
                    comm3DResponseVO.setMerchantId(api_key);
                }
                else if ("401".equals(result) || "503".equals(result) || "4019".equals(result) || "4071".equals(result) || "4072".equals(result) || "4073".equals(result) || "4074".equals(result) || "4075".equals(result) || "4080".equals(result) || "5000".equals(result) || "4099".equals(result))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setErrorCode(result);
                    if (functions.isValueNull(result_description))
                    {
                        comm3DResponseVO.setRemark(result_description);
                        comm3DResponseVO.setDescription(result_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(description);
                        comm3DResponseVO.setDescription(description);
                    }
                }
                else if ("401".equals(error) || "403".equals(error) || "503".equals(error) || "4019".equals(error) || "4071".equals(error) || "4072".equals(error) || "4073".equals(error) || "4074".equals(error) || "4075".equals(error) || "4080".equals(error) || "5000".equals(error) || "4099".equals(error))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setErrorCode(result);
                    if (functions.isValueNull(result_description))
                    {
                        comm3DResponseVO.setRemark(result_description);
                        comm3DResponseVO.setDescription(result_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(description);
                        comm3DResponseVO.setDescription(description);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
                FlexepinUtils.updateMainTableEntry(trans_no, trans_mode ,"", trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error(trackingID + "---> Exception in processQuery: " + e);
            PZExceptionHandler.raiseTechnicalViolationException(FlexepinPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering in processPayout() for FlexepinPaymentGateway------>");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(accountId);
        String api_key                          = gatewayAccount.getFRAUD_FTP_PATH();
        String api_secret                       = gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest                          = gatewayAccount.isTest();
        String nounce                           = trackingID;
        String Request_Method                   = "";
        String Request_URL                      = "";
        String qty                              = "1";        //Quantity of vouchers to be created, normally set to 1
        String terminal_id                      = "";
        String customer_ip                      = "";
        String signatureData                    = "";
        String AUTHENTICATION                   = "";
        String encryptedSignature               = "";
        String Voucher_Types_Payout_Response    = "";
        String Voucher_Payout_Response          = "";
        String Voucher_Validate_Payout_Response = "";
        String currency                         = "";
        String trans_mode                       = "Non-3D";
        String   s                              = "payoutstarted";
        String toType                           =  commTransactionDetailsVO.getTotype();
        HashMap hashMap                         = null;

        transactionLogger.error("toType >>>>>>>>>> "+trackingID+" "+toType);

        try
        {
            if (isTest)
                Request_URL = RB.getString("TEST_Voucher_Types_URL");
            else
                Request_URL = RB.getString("LIVE_Voucher_Types_URL");

            Request_Method = "GET";

            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
                currency = commTransactionDetailsVO.getCurrency();

            transactionLogger.error("currency in payout : "+currency);

            if (currency.equalsIgnoreCase("All") || !functions.isValueNull(currency))
                currency = "CAD";

            transactionLogger.error("currency in payout : "+currency);

            String reqUrl[] = Request_URL.split("/");
            signatureData +=  Request_Method+ "\n" +"/"+ reqUrl[3] +"/" + reqUrl[4] + "\n"+ nounce+"\n";
            transactionLogger.error(trackingID + " Voucher Types Signature data: " + signatureData);

            encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
            transactionLogger.error(trackingID + " Voucher Types Encrypted Signature: " + encryptedSignature);

            AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin Payout Request " +trackingID + " Voucher Types Request ----> " + Request_URL);
            else
                transactionLogger.error("Flexepin Payout Request " +trackingID + " Voucher Types Request ----> " + Request_URL);

            Voucher_Types_Payout_Response = FlexepinUtils.doGetHTTPUrlConnection(Request_URL, AUTHENTICATION, trackingID);

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin Payout Response "+trackingID + " Voucher Types Response: " + Voucher_Types_Payout_Response.trim());
            else
                transactionLogger.error("Flexepin Payout Response "+trackingID + " Voucher Types Response: " + Voucher_Types_Payout_Response.trim());

            if (functions.isValueNull(Voucher_Types_Payout_Response.trim()) && FlexepinUtils.isJSONValid(Voucher_Types_Payout_Response.trim()))
            {
                String VTPtransaction_id   = "";
                String VTPtrans_no         = "";
                String VTPresult           = "";
                String VTPcode             = "";
                String VTPdescription      = "";
                String VTPean              = "";
                String VTPcurrency         = "";
                String VTPvalue            = "";
                String VTPdistributor_cost = "";
                String VTPmerchant_cost    = "";
                String VTPerror            = "";
                String VTPextensions       = "";
                String VTPresult_description = "";

                String VTPEXTERNAL_PRODUCT_CODE         = "";
                String VTPFLEXEPIN_VARIABLE_VOUCHER     = "";
                String VTPLAST_TIME_SEND_STOCK_ALERT    = "";
                String VTPVOUCHER_TOPUP_ALLOWED         = "";


                JSONObject VTPresponseJson = new JSONObject(Voucher_Types_Payout_Response);

                if (VTPresponseJson.has("transaction_id") && functions.isValueNull(VTPresponseJson.getString("transaction_id")))
                    VTPtransaction_id = VTPresponseJson.getString("transaction_id");
                if (VTPresponseJson.has("trans_no") && functions.isValueNull(VTPresponseJson.getString("trans_no")))
                    VTPtrans_no = VTPresponseJson.getString("trans_no");
                if (VTPresponseJson.has("result") && functions.isValueNull(VTPresponseJson.getString("result")))
                    VTPresult = VTPresponseJson.getString("result");
                if (VTPresponseJson.has("result_description") && functions.isValueNull(VTPresponseJson.getString("result_description")))
                    VTPresult_description = VTPresponseJson.getString("result_description");

                if (VTPresponseJson.has("types") && functions.isValueNull(VTPresponseJson.getString("types")))
                {
                    if (FlexepinUtils.isJSONValid(VTPresponseJson.getString("types")))
                    {
                        JSONObject typesJsonResponse = new JSONObject(VTPresponseJson.getString("types"));

                        if (typesJsonResponse.has("code") && functions.isValueNull(typesJsonResponse.getString("code")))
                            VTPcode = typesJsonResponse.getString("code");
                        if (typesJsonResponse.has("description") && functions.isValueNull(typesJsonResponse.getString("description")))
                            VTPdescription = typesJsonResponse.getString("description");
                        if (typesJsonResponse.has("ean") && functions.isValueNull(typesJsonResponse.getString("ean")))
                            VTPean = typesJsonResponse.getString("ean");
                        if (typesJsonResponse.has("currency") && functions.isValueNull(typesJsonResponse.getString("currency")))
                            VTPcurrency = typesJsonResponse.getString("currency");
                        if (typesJsonResponse.has("value") && functions.isValueNull(typesJsonResponse.getString("value")))
                            VTPvalue = typesJsonResponse.getString("value");
                        if (typesJsonResponse.has("distributor_cost") && functions.isValueNull(typesJsonResponse.getString("distributor_cost")))
                            VTPdistributor_cost = typesJsonResponse.getString("distributor_cost");
                        if (typesJsonResponse.has("merchant_cost") && functions.isValueNull(typesJsonResponse.getString("merchant_cost")))
                            VTPmerchant_cost = typesJsonResponse.getString("merchant_cost");
                    }
                    else if (FlexepinUtils.isJSONARRAYValid(VTPresponseJson.getString("types")))
                    {
                        JSONArray typesJsonResponse = new JSONArray(VTPresponseJson.getString("types"));

                        for (int i = 0; i<typesJsonResponse.length(); i++)
                        {
                            if (typesJsonResponse.getJSONObject(i) != null && FlexepinUtils.isJSONValid(typesJsonResponse.getString(i)))
                            {
                                JSONObject typesJson = new JSONObject(typesJsonResponse.getString(i));

                                if (typesJson.has("code") && functions.isValueNull(typesJson.getString("code")))
                                    VTPcode = typesJson.getString("code");
                                if (typesJson.has("description") && functions.isValueNull(typesJson.getString("description")))
                                    VTPdescription = typesJson.getString("description");
                                if (typesJson.has("currency") && functions.isValueNull(typesJson.getString("currency")))
                                    VTPcurrency = typesJson.getString("currency");
                                if (typesJson.has("value") && functions.isValueNull(typesJson.getString("value")))
                                    VTPvalue = typesJson.getString("value");
                                if (typesJson.has("ean") && functions.isValueNull(typesJson.getString("ean")))
                                    VTPean = typesJson.getString("ean");
                                if (typesJson.has("distributor_cost") && functions.isValueNull(typesJson.getString("distributor_cost")))
                                    VTPdistributor_cost = typesJson.getString("distributor_cost");
                                if (typesJson.has("merchant_cost") && functions.isValueNull(typesJson.getString("merchant_cost")))
                                    VTPmerchant_cost = typesJson.getString("merchant_cost");
                                if (typesJson.has("extensions") && functions.isValueNull(typesJson.getString("extensions")))
                                    VTPextensions = typesJson.getString("extensions");      // todo This extentions jsonArray not usefull as per bank. dont use as per bank

                                transactionLogger.error("VTPcurrency: "+ VTPcurrency + " currency: "+ currency+ " VTPvalue: "+ VTPvalue+ " VTPcode: "+ VTPcode);
                                if (currency.equalsIgnoreCase(VTPcurrency) && functions.isValueNull(VTPvalue) && functions.isValueNull(VTPcode))
                                    break;
                                else if (currency.equalsIgnoreCase(VTPcurrency))
                                    break;
                            }
                        }
                    }
                }

                hashMap = new HashMap();
                hashMap.put("trans_no",VTPtrans_no);
                hashMap.put("result",VTPresult);
                hashMap.put("result_description",VTPresult_description);
                hashMap.put("code",VTPcode);
                hashMap.put("description",VTPdescription);
                hashMap.put("currency",VTPcurrency);
                hashMap.put("value",VTPvalue);
                hashMap.put("ean",VTPean);
                hashMap.put("distributor_cost",VTPdistributor_cost);
                hashMap.put("merchant_cost",VTPmerchant_cost);

                if ("0".equals(VTPresult))
                {
                    if (isTest)
                        Request_URL = RB.getString("TEST_Voucher_PAYOUT_URL");
                    else
                        Request_URL = RB.getString("LIVE_Voucher_PAYOUT_URL");

                    Request_Method = "PUT";
                    terminal_id    = trackingID;   //"WEB_"+trackingID;

                    if (functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                        customer_ip = commAddressDetailsVO.getCardHolderIpAddress();
                    else
                        customer_ip = "192.168.1.1";

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("customer_ip",customer_ip);

                    reqUrl = Request_URL.split("/");
                    signatureData = Request_Method+"\n" + "/"+reqUrl[3]+"/"+reqUrl[4]+"/"+VTPcode+"/"+VTPvalue+"/"+qty+"/"+terminal_id+"/"+ nounce + "\n"+nounce+ "\n"+jsonObject;
                    transactionLogger.error(trackingID + " Voucher Pay out Signature data: " + signatureData);

                    encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
                    transactionLogger.error(trackingID + " Voucher Pay out Encrypted Signature: " + encryptedSignature);

                    AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;
                    Request_URL += "/"+VTPcode+"/"+VTPvalue+"/"+qty+"/"+terminal_id+"/"+ nounce;

                    if("Facilero".equalsIgnoreCase(toType))
                        facileroLogger.error("Flexepin Payout Request " + trackingID + " Voucher Pay out Request Url ----> " + Request_URL +" " + "RequestBody: "+jsonObject);
                    else
                        transactionLogger.error("Flexepin Payout Request " + trackingID + " Voucher Pay out Request Url ----> " + Request_URL +" " + "RequestBody: "+jsonObject);

                    Voucher_Payout_Response = FlexepinUtils.doPutHTTPUrlConnection(Request_URL, AUTHENTICATION, String.valueOf(jsonObject), trackingID);

                    if("Facilero".equalsIgnoreCase(toType))
                        facileroLogger.error("Flexepin Payout Response "+  trackingID + " Voucher Pay out Response: "+Voucher_Payout_Response.trim());
                    else
                        transactionLogger.error("Flexepin Payout Response "+  trackingID + " Voucher Pay out Response: "+Voucher_Payout_Response.trim());

                    if (functions.isValueNull(Voucher_Payout_Response.trim()) && FlexepinUtils.isJSONValid(Voucher_Payout_Response.trim()))
                    {
                        String VPtransaction_id   = "";
                        String VPtrans_no         = "";
                        String VPresult           = "";
                        String VPpin              = "";
                        String VPserial           = "";
                        String VPexpiry           = "";
                        String VPvalue            = "";
                        String VPcost             = "";
                        String VPcurrency         = "";
                        String VPstatus           = "";
                        String VPdescription      = "";
                        String VPean              = "";
                        String VPerror            = "";
                        String VPresult_description = "";

                        JSONObject VPresponseJson = new JSONObject(Voucher_Payout_Response.trim());

                        if (VPresponseJson.has("transaction_id") && functions.isValueNull(VPresponseJson.getString("transaction_id")))
                            VPtransaction_id = VPresponseJson.getString("transaction_id");
                        if (VPresponseJson.has("trans_no") && functions.isValueNull(VPresponseJson.getString("trans_no")))
                            VPtrans_no = VPresponseJson.getString("trans_no");
                        if (VPresponseJson.has("result") && functions.isValueNull(VPresponseJson.getString("result")))
                            VPresult = VPresponseJson.getString("result");
                        if (VPresponseJson.has("result_description") && functions.isValueNull(VPresponseJson.getString("result_description")))
                            VPresult_description = VPresponseJson.getString("result_description");

                        if (VPresponseJson.has("vouchers") && functions.isValueNull(VPresponseJson.getString("vouchers")))
                        {
                            if (FlexepinUtils.isJSONValid(VPresponseJson.getString("vouchers")))
                            {
                                JSONObject vouchersJson = new JSONObject(VPresponseJson.getString("vouchers"));

                                if (vouchersJson.has("pin") && functions.isValueNull(vouchersJson.getString("pin")))
                                    VPpin = vouchersJson.getString("pin");
                                if (vouchersJson.has("serial") && functions.isValueNull(vouchersJson.getString("serial")))
                                    VPserial = vouchersJson.getString("serial");
                                if (vouchersJson.has("expiry") && functions.isValueNull(vouchersJson.getString("expiry")))
                                    VPexpiry = vouchersJson.getString("expiry");
                                if (vouchersJson.has("value") && functions.isValueNull(vouchersJson.getString("value")))
                                    VPvalue = vouchersJson.getString("value");
                                if (vouchersJson.has("cost") && functions.isValueNull(vouchersJson.getString("cost")))
                                    VPcost = vouchersJson.getString("cost");
                                if (vouchersJson.has("currency") && functions.isValueNull(vouchersJson.getString("currency")))
                                    VPcurrency = vouchersJson.getString("currency");
                                if (vouchersJson.has("status") && functions.isValueNull(vouchersJson.getString("status")))
                                    VPstatus = vouchersJson.getString("status");
                                if (vouchersJson.has("description") && functions.isValueNull(vouchersJson.getString("description")))
                                    VPdescription = vouchersJson.getString("description");
                                if (vouchersJson.has("ean") && functions.isValueNull(vouchersJson.getString("ean")))
                                    VPean = vouchersJson.getString("ean");
                            }
                            else if (FlexepinUtils.isJSONARRAYValid(VPresponseJson.getString("vouchers")))
                            {
                                JSONArray vouchersJson = new JSONArray(VPresponseJson.getString("vouchers"));
                                for (int k=0; k < vouchersJson.length(); k++)
                                {
                                    JSONObject vouchers1json = new JSONObject(String.valueOf(vouchersJson.get(k)));

                                    if (vouchers1json.has("pin") && functions.isValueNull(vouchers1json.getString("pin")))
                                        VPpin = vouchers1json.getString("pin");
                                    if (vouchers1json.has("serial") && functions.isValueNull(vouchers1json.getString("serial")))
                                        VPserial = vouchers1json.getString("serial");
                                    if (vouchers1json.has("expiry") && functions.isValueNull(vouchers1json.getString("expiry")))
                                        VPexpiry = vouchers1json.getString("expiry");
                                    if (vouchers1json.has("value") && functions.isValueNull(vouchers1json.getString("value")))
                                        VPvalue = vouchers1json.getString("value");
                                    if (vouchers1json.has("cost") && functions.isValueNull(vouchers1json.getString("cost")))
                                        VPcost = vouchers1json.getString("cost");
                                    if (vouchers1json.has("currency") && functions.isValueNull(vouchers1json.getString("currency")))
                                        VPcurrency = vouchers1json.getString("currency");
                                    if (vouchers1json.has("status") && functions.isValueNull(vouchers1json.getString("status")))
                                        VPstatus = vouchers1json.getString("status");
                                    if (vouchers1json.has("description") && functions.isValueNull(vouchers1json.getString("description")))
                                        VPdescription = vouchers1json.getString("description");
                                    if (vouchers1json.has("ean") && functions.isValueNull(vouchers1json.getString("ean")))
                                        VPean = vouchers1json.getString("ean");
                                }
                            }
                        }

                        hashMap.put("trans_no",VPtrans_no);
                        hashMap.put("result",VPresult);
                        hashMap.put("result_description",VPresult_description);
                        hashMap.put("code",VTPcode);
                        hashMap.put("description",VPdescription);
                        hashMap.put("currency",VPcurrency);
                        hashMap.put("value",VPvalue);
                        hashMap.put("ean",VPean);
                        hashMap.put("distributor_cost",VTPdistributor_cost);
                        hashMap.put("merchant_cost",VTPmerchant_cost);
                        hashMap.put("pin",VPpin);
                        hashMap.put("serial",VPserial);
                        hashMap.put("expiry",VPexpiry);
                        hashMap.put("cost",VPcost);
                        hashMap.put("voucherStatus",VPstatus);

                        if ("0".equals(VPresult) && "SOLD".equalsIgnoreCase(VPstatus))
                        {
                            if (isTest)
                                Request_URL = RB.getString("TEST_PAYOUT_Voucher_Validate_URL");
                            else
                                Request_URL = RB.getString("LIVE_PAYOUT_Voucher_Validate_URL");

                            Request_Method = "GET";
                            terminal_id    = trackingID;    //"WEB_"+trackingID;

                            reqUrl = Request_URL.split("/");
                            signatureData = Request_Method+"\n" + "/"+reqUrl[3]+"/"+reqUrl[4]+ "/"+VPpin+"/"+terminal_id+"/"+nounce + "\n"+nounce+ "\n";
                            String signatureDataLog = Request_Method+"\n" + "/"+reqUrl[3]+"/"+reqUrl[4]+ "/"+functions.maskingPan(VPpin)+"/"+terminal_id+"/"+nounce + "\n"+nounce+ "\n";
                            transactionLogger.error(trackingID + " Payout Voucher Validate Signature data: " + signatureDataLog);

                            encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
                            transactionLogger.error(trackingID + " Payout Voucher Validate Encrypted Signature: " + encryptedSignature);

                            AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;
                            String Request_URLLog = Request_URL +"/"+functions.maskingPan(VPpin)+"/"+terminal_id+"/"+nounce;
                            Request_URL += "/"+VPpin+"/"+terminal_id+"/"+nounce;

                            if("Facilero".equalsIgnoreCase(toType))
                                facileroLogger.error("Flexepin Payout Request "+ trackingID + " Payout Voucher Validate Request Url ----> " + Request_URLLog);
                            else
                                transactionLogger.error("Flexepin Payout Request "+ trackingID + " Payout Voucher Validate Request Url ----> " + Request_URLLog);

                            Voucher_Validate_Payout_Response = FlexepinUtils.doGetHTTPUrlConnection(Request_URL, AUTHENTICATION,trackingID);

                            if("Facilero".equalsIgnoreCase(toType))
                                facileroLogger.error("Flexepin Payout Response " +trackingID + " Payout Voucher Validate Response: "+ Voucher_Validate_Payout_Response.trim());
                            else
                                transactionLogger.error("Flexepin Payout Response " +trackingID + " Payout Voucher Validate Response: "+ Voucher_Validate_Payout_Response.trim());

                            if (functions.isValueNull(Voucher_Validate_Payout_Response.trim()) && FlexepinUtils.isJSONValid(Voucher_Validate_Payout_Response.trim()))
                            {
                                String VVPtransaction_id   = "";
                                String VVPtrans_no         = "";
                                String VVPresult           = "";
                                String VVPpin              = "";
                                String VVPserial           = "";
                                String VVPexpiry           = "";
                                String VVPvalue            = "";
                                String VVPcost             = "";
                                String VVPcurrency         = "";
                                String VVPstatus           = "";
                                String VVPdescription      = "";
                                String VVPean              = "";
                                String VVPerror            = "";
                                String VVPresult_description = "";

                                JSONObject VVPresponseJson = new JSONObject(Voucher_Validate_Payout_Response.trim());

                                if (VVPresponseJson.has("transaction_id") && functions.isValueNull(VVPresponseJson.getString("transaction_id")))
                                    VVPtransaction_id = VVPresponseJson.getString("transaction_id");
                                if (VVPresponseJson.has("trans_no") && functions.isValueNull(VVPresponseJson.getString("trans_no")))
                                    VVPtrans_no = VVPresponseJson.getString("trans_no");
                                if (VVPresponseJson.has("result") && functions.isValueNull(VVPresponseJson.getString("result")))
                                    VVPresult = VVPresponseJson.getString("result");
                                if (VVPresponseJson.has("result_description") && functions.isValueNull(VVPresponseJson.getString("result_description")))
                                    VVPresult_description = VVPresponseJson.getString("result_description");
                                if (VVPresponseJson.has("serial") && functions.isValueNull(VVPresponseJson.getString("serial")))
                                    VVPserial = VVPresponseJson.getString("serial");
                                if (VVPresponseJson.has("value") && functions.isValueNull(VVPresponseJson.getString("value")))
                                    VVPvalue = VVPresponseJson.getString("value");
                                if (VVPresponseJson.has("cost") && functions.isValueNull(VVPresponseJson.getString("cost")))
                                    VVPcost = VVPresponseJson.getString("cost");
                                if (VVPresponseJson.has("currency") && functions.isValueNull(VVPresponseJson.getString("currency")))
                                    VVPcurrency = VVPresponseJson.getString("currency");
                                if (VVPresponseJson.has("status") && functions.isValueNull(VVPresponseJson.getString("status")))
                                    VVPstatus = VVPresponseJson.getString("status");
                                if (VVPresponseJson.has("description") && functions.isValueNull(VVPresponseJson.getString("description")))
                                    VVPdescription = VVPresponseJson.getString("description");

                                hashMap.put("trans_no",VVPtrans_no);
                                hashMap.put("result",VVPresult);
                                hashMap.put("result_description",VVPresult_description);
                                hashMap.put("code",VTPcode);
                                hashMap.put("description",VVPdescription);
                                hashMap.put("currency",VVPcurrency);
                                hashMap.put("value",VVPvalue);
                                hashMap.put("ean",VPean);
                                hashMap.put("distributor_cost",VTPdistributor_cost);
                                hashMap.put("merchant_cost",VTPmerchant_cost);
                                hashMap.put("pin",VPpin);
                                hashMap.put("serial",VVPserial);
                                hashMap.put("expiry",VPexpiry);
                                hashMap.put("cost",VVPcost);
                                hashMap.put("voucherStatus",VVPstatus);

                                if ("0".equals(VVPresult) && "ACTIVE".equalsIgnoreCase(VVPstatus))
                                {
                                    s= "payoutsuccessful";
                                    comm3DResponseVO.setStatus("success");
                                    comm3DResponseVO.setTransactionStatus("success");
                                    comm3DResponseVO.setRemark(VVPresult_description);
                                    comm3DResponseVO.setDescription(VVPresult_description);
                                    comm3DResponseVO.setCurrency(VVPcurrency);
                                    comm3DResponseVO.setBankRefNo(VVPtrans_no);
                                    comm3DResponseVO.setAmount(VVPvalue);
                                    comm3DResponseVO.setAuthCode(VVPean);
                                    comm3DResponseVO.setErrorCode(VVPresult);
                                    comm3DResponseVO.setResponseHashInfo(VVPtransaction_id);
                                    comm3DResponseVO.setTransactionId(VVPtrans_no);
                                    comm3DResponseVO.setRequestMap(hashMap);
                                    comm3DResponseVO.setMerchantId(api_key);
                                }
                                else if ("503".equals(VVPresult) || "4019".equals(VVPresult) || "4071".equals(VVPresult) || "4072".equals(VVPresult) || "4073".equals(VVPresult) || "4074".equals(VVPresult) || "4075".equals(VVPresult) || "4080".equals(VVPresult) || "5000".equals(VVPresult) || "4099".equals(VVPresult))
                                {
                                    s= "payoutfailed";
                                    comm3DResponseVO.setStatus("failed");
                                    comm3DResponseVO.setTransactionStatus("failed");
                                    comm3DResponseVO.setErrorCode(VVPresult);
                                    comm3DResponseVO.setTransactionId(VVPtrans_no);
                                    if (functions.isValueNull(VVPresult_description))
                                    {
                                        comm3DResponseVO.setRemark(VVPresult_description);
                                        comm3DResponseVO.setDescription(VVPresult_description);
                                    }
                                    else
                                    {
                                        comm3DResponseVO.setRemark(VVPdescription);
                                        comm3DResponseVO.setDescription(VVPdescription);
                                    }
                                }
                                else if ("403".equals(VVPerror) || "503".equals(VVPerror) || "4019".equals(VPerror) || "4071".equals(VVPerror) || "4072".equals(VVPerror) || "4073".equals(VVPerror) || "4074".equals(VVPerror) || "4075".equals(VVPerror) || "4080".equals(VVPerror) || "5000".equals(VVPerror) || "4099".equals(VVPerror))
                                {
                                    s= "payoutfailed";
                                    comm3DResponseVO.setStatus("failed");
                                    comm3DResponseVO.setTransactionStatus("failed");
                                    comm3DResponseVO.setErrorCode(VVPresult);
                                    comm3DResponseVO.setTransactionId(VVPtrans_no);
                                    if (functions.isValueNull(VVPresult_description))
                                    {
                                        comm3DResponseVO.setRemark(VVPresult_description);
                                        comm3DResponseVO.setDescription(VVPresult_description);
                                    }
                                    else
                                    {
                                        comm3DResponseVO.setRemark(VVPdescription);
                                        comm3DResponseVO.setDescription(VVPdescription);
                                    }
                                }
                                else
                                {
                                    s= "payoutstarted";
                                    comm3DResponseVO.setStatus("pending");
                                    comm3DResponseVO.setTransactionStatus("pending");
                                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                }
                                FlexepinUtils.updateMainTableEntry(VVPtrans_no, trans_mode, "", trackingID);
                            }
                            else
                            {
                                comm3DResponseVO.setStatus("pending");
                                comm3DResponseVO.setTransactionStatus("pending");
                                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            }
                        }
                        else if ("503".equals(VPresult) || "4019".equals(VPresult) || "4071".equals(VPresult) || "4072".equals(VPresult) || "4073".equals(VPresult) || "4074".equals(VPresult) || "4075".equals(VPresult) || "4080".equals(VPresult) || "5000".equals(VPresult) || "4099".equals(VPresult))
                        {
                            comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setTransactionStatus("failed");
                            comm3DResponseVO.setErrorCode(VPresult);
                            comm3DResponseVO.setTransactionId(VPtrans_no);
                            if (functions.isValueNull(VPresult_description))
                            {
                                comm3DResponseVO.setRemark(VPresult_description);
                                comm3DResponseVO.setDescription(VPresult_description);
                            }
                            else
                            {
                                comm3DResponseVO.setRemark(VPdescription);
                                comm3DResponseVO.setDescription(VPdescription);
                            }
                            FlexepinUtils.updateMainTableEntry(VPtrans_no, trans_mode, "", trackingID);
                        }
                        else if ("403".equals(VPerror) || "503".equals(VPerror) || "4019".equals(VPerror) || "4071".equals(VPerror) || "4072".equals(VPerror) || "4073".equals(VPerror) || "4074".equals(VPerror) || "4075".equals(VPerror) || "4080".equals(VPerror) || "5000".equals(VPerror) || "4099".equals(VPerror))
                        {
                            comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setTransactionStatus("failed");
                            comm3DResponseVO.setErrorCode(VPresult);
                            comm3DResponseVO.setTransactionId(VPtrans_no);
                            if (functions.isValueNull(VPresult_description))
                            {
                                comm3DResponseVO.setRemark(VPresult_description);
                                comm3DResponseVO.setDescription(VPresult_description);
                            }
                            else
                            {
                                comm3DResponseVO.setRemark(VPdescription);
                                comm3DResponseVO.setDescription(VPdescription);
                            }
                            FlexepinUtils.updateMainTableEntry(VPtrans_no, trans_mode, "", trackingID);
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setTransactionStatus("pending");
                            comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            FlexepinUtils.updateMainTableEntry(VPtrans_no, trans_mode, "", trackingID);
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    }
                }
                else if ("503".equals(VTPresult) || "4019".equals(VTPresult) || "4071".equals(VTPresult) || "4072".equals(VTPresult) || "4073".equals(VTPresult) || "4074".equals(VTPresult) || "4075".equals(VTPresult) || "4080".equals(VTPresult) || "5000".equals(VTPresult) || "4099".equals(VTPresult))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setErrorCode(VTPresult);
                    comm3DResponseVO.setTransactionId(VTPtrans_no);
                    if (functions.isValueNull(VTPresult_description))
                    {
                        comm3DResponseVO.setRemark(VTPresult_description);
                        comm3DResponseVO.setDescription(VTPresult_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(VTPdescription);
                        comm3DResponseVO.setDescription(VTPdescription);
                    }
                    FlexepinUtils.updateMainTableEntry(VTPtrans_no, trans_mode, "", trackingID);
                }
                else if ("403".equals(VTPerror) || "503".equals(VTPerror) || "4019".equals(VTPerror) || "4071".equals(VTPerror) || "4072".equals(VTPerror) || "4073".equals(VTPerror) || "4074".equals(VTPerror) || "4075".equals(VTPerror) || "4080".equals(VTPerror) || "5000".equals(VTPerror) || "4099".equals(VTPerror))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setErrorCode(VTPresult);
                    comm3DResponseVO.setTransactionId(VTPtrans_no);
                    if (functions.isValueNull(VTPresult_description))
                    {
                        comm3DResponseVO.setRemark(VTPresult_description);
                        comm3DResponseVO.setDescription(VTPresult_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(VTPdescription);
                        comm3DResponseVO.setDescription(VTPdescription);
                    }
                    FlexepinUtils.updateMainTableEntry(VTPtrans_no, trans_mode, "", trackingID);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    FlexepinUtils.updateMainTableEntry(VTPtrans_no, trans_mode, "", trackingID);
                }

                if (!functions.isValueNull(comm3DResponseVO.getAmount()))
                    comm3DResponseVO.setAmount(commTransactionDetailsVO.getAmount());

                FlexepinUtils.updateCaptureEntryforPayout(hashMap, s, comm3DResponseVO.getAmount() ,trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionLogger.error(trackingID +"----> Exception IN processPayout():" +e);
            PZExceptionHandler.raiseTechnicalViolationException(FlexepinPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    //todo as per bank we can also use sale inquiry for payout transactions, so we use sale inquiry java code in processPayoutInquiry
    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering in processPayoutInquiry() for FlexepinPaymentGateway------>");
        CommRequestVO comm3DRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = comm3DRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String api_key              = gatewayAccount.getFRAUD_FTP_PATH();
        String api_secret           = gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest              = gatewayAccount.isTest();
        String nounce               = trackingID;
        String Request_Method       = "";
        String Request_URL          = "";
        String terminal_id          = "";
        String signatureData        = "";
        String AUTHENTICATION       = "";
        String encryptedSignature   = "";
        String response             = "";
        String trans_mode           = "Non-3D";
        String toType               =  commTransactionDetailsVO.getTotype();

        transactionLogger.error("toType >>>>>>>>>> "+trackingID+" "+toType);

        try
        {
            if (isTest)
                Request_URL = RB.getString("TEST_SALE_Transaction_Query_URL");
            else
                Request_URL = RB.getString("LIVE_SALE_Transaction_Query_URL");

            Request_Method = "GET";
            terminal_id    =  trackingID;   //    "WEB_"+trackingID;   //FACILEROWEB

            String reqUrl[] = Request_URL.split("/");
            signatureData = Request_Method+"\n"+"/"+ reqUrl[3] +"/" + reqUrl[4] + "/"+nounce+"/"+terminal_id+"/"+nounce+"\n"+nounce+"\n";
            transactionLogger.error(trackingID + " PayoutInquiry Signature data: " + signatureData);

            encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
            transactionLogger.error(trackingID + " PayoutInquiry Encrypted Signature: " + encryptedSignature);

            AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;
            Request_URL += "/"+nounce+"/"+terminal_id+"/"+nounce+"\n";

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin PayoutInquiry Request " +trackingID + " PayoutInquiry Request ----> " + Request_URL);
            else
                transactionLogger.error("Flexepin PayoutInquiry Request " +trackingID + " PayoutInquiry Request : " + Request_URL);

            response = FlexepinUtils.doGetHTTPUrlConnection(Request_URL, AUTHENTICATION,trackingID);

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin PayoutInquiry " +trackingID + " PayoutInquiry Response ----> " + response.trim());
            else
                transactionLogger.error("Flexepin PayoutInquiry "+trackingID + " PayoutInquiry Response: "+response.trim());

            if (functions.isValueNull(response.trim()) && FlexepinUtils.isJSONValid(response.trim()))
            {
                String trans_no             = "";
                String transaction_id       = "";
                String timestamp            = "";
                String result               = "";
                String result_description   = "";
                String serial               = "";
                String value                = "";
                String currency             = "";
                String status               = "";
                String description          = "";
                String error                = "";

                JSONObject responseJson = new JSONObject(response.trim());

                if (responseJson.has("trans_no") && functions.isValueNull(responseJson.getString("trans_no")))
                    trans_no = responseJson.getString("trans_no");
                if (responseJson.has("transaction_id") && functions.isValueNull(responseJson.getString("transaction_id")))
                    transaction_id = responseJson.getString("transaction_id");
                if (responseJson.has("timestamp") && functions.isValueNull(responseJson.getString("timestamp")))
                    timestamp = responseJson.getString("timestamp");
                if (responseJson.has("result") && functions.isValueNull(responseJson.getString("result")))
                    result = responseJson.getString("result");
                if (responseJson.has("result_description") && functions.isValueNull(responseJson.getString("result_description")))
                    result_description = responseJson.getString("result_description");

                if (responseJson.has("error") && functions.isValueNull(responseJson.getString("error")))
                    error = responseJson.getString("error");

                if (responseJson.has("transaction") && functions.isValueNull(responseJson.getString("transaction")))
                {
                    JSONObject transactionJson = new JSONObject(responseJson.getString("transaction"));

                    if (transactionJson.has("trans_no") && functions.isValueNull(transactionJson.getString("trans_no")))
                        trans_no = transactionJson.getString("trans_no");
                    if (transactionJson.has("timestamp") && functions.isValueNull(transactionJson.getString("timestamp")))
                        timestamp = transactionJson.getString("timestamp");
                    if (transactionJson.has("result") && functions.isValueNull(transactionJson.getString("result")))
                        result = transactionJson.getString("result");
                    if (transactionJson.has("result_description") && functions.isValueNull(transactionJson.getString("result_description")))
                        result_description = transactionJson.getString("result_description");
                    if (transactionJson.has("serial") && functions.isValueNull(transactionJson.getString("serial")))
                        serial = transactionJson.getString("serial");
                    if (transactionJson.has("value") && functions.isValueNull(transactionJson.getString("value")))
                        value = transactionJson.getString("value");
                    if (transactionJson.has("currency") && functions.isValueNull(transactionJson.getString("currency")))
                        currency = transactionJson.getString("currency");
                    if (transactionJson.has("status") && functions.isValueNull(transactionJson.getString("status")))
                        status = transactionJson.getString("status");
                    if (transactionJson.has("description") && functions.isValueNull(transactionJson.getString("description")))
                        description = transactionJson.getString("description");
                    if (transactionJson.has("transaction_id") && functions.isValueNull(transactionJson.getString("transaction_id")))
                        transaction_id = transactionJson.getString("transaction_id");
                }

                if ("0".equals(result))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark(result_description);
                    comm3DResponseVO.setResponseTime(timestamp);
                    comm3DResponseVO.setDescription(result_description);
                    comm3DResponseVO.setErrorCode(result);
                    comm3DResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setAmount(value);
                    comm3DResponseVO.setBankRefNo(trans_no);
                    comm3DResponseVO.setResponseHashInfo(transaction_id);
                    comm3DResponseVO.setTransactionId(trans_no);
                    comm3DResponseVO.setMerchantId(api_key);
                }
                else if ("401".equals(result) || "503".equals(result) || "4019".equals(result) || "4071".equals(result) || "4072".equals(result) || "4073".equals(result) || "4074".equals(result) || "4075".equals(result) || "4080".equals(result) || "5000".equals(result) || "4099".equals(result))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setErrorCode(result);
                    comm3DResponseVO.setTransactionId(trans_no);
                    if (functions.isValueNull(result_description))
                    {
                        comm3DResponseVO.setRemark(result_description);
                        comm3DResponseVO.setDescription(result_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(description);
                        comm3DResponseVO.setDescription(description);
                    }
                }
                else if ("401".equals(error) || "403".equals(error) || "503".equals(error) || "4019".equals(error) || "4071".equals(error) || "4072".equals(error) || "4073".equals(error) || "4074".equals(error) || "4075".equals(error) || "4080".equals(error) || "5000".equals(error)|| "4099".equals(error))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setErrorCode(result);
                    comm3DResponseVO.setTransactionId(trans_no);
                    if (functions.isValueNull(result_description))
                    {
                        comm3DResponseVO.setRemark(result_description);
                        comm3DResponseVO.setDescription(result_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(description);
                        comm3DResponseVO.setDescription(description);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
                FlexepinUtils.updateMainTableEntry(trans_no, trans_mode ,"", trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionLogger.error(trackingID + "---> Exception in processPayoutInquiry: " + e);
            PZExceptionHandler.raiseTechnicalViolationException(FlexepinPaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    //todo original payout inquiry
    public GenericResponseVO processPayoutInquiry1(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering in processPayoutInquiry() for FlexepinPaymentGateway------>");
        CommRequestVO comm3DRequestVO                       = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = comm3DRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String api_key        = gatewayAccount.getMerchantId();
        String api_secret     = gatewayAccount.getPassword();
        boolean isTest        = gatewayAccount.isTest();
        String nounce         = trackingID;
        String Request_Method = "";
        String Request_URL    = "";
        String terminal_id    = "";
        String from_date      = "";
        String to_date        = "";
        String signatureData  = "";
        String AUTHENTICATION = "";
        String encryptedSignature = "";
        String response           = "";
        String trans_mode = "Non-3D";
        LocalDate today;
        String toType             =  commTransactionDetailsVO.getTotype();
        transactionLogger.error("toType >>>>>>>>>> "+trackingID+" "+toType);
        try
        {
            if (isTest)
                Request_URL = RB.getString("TEST_PAYOUT_Transaction_Query_URL");
            else
                Request_URL = RB.getString("LIVE_PAYOUT_Transaction_Query_URL");

            Request_Method = "GET";
            terminal_id    = trackingID;     //"WEB_"+trackingID;   //FACILEROWEB

            today     = LocalDate.now();
            to_date   = String.valueOf(today);
            HashMap dateMap = FlexepinUtils.getTimeRange(trackingID);
            if (functions.isValueNull(String.valueOf(dateMap.get("timestamp"))))
                from_date = String.valueOf(dateMap.getOrDefault("timestamp",today));

            if (from_date.contains(" ") && from_date.split(" ").length > 0)
                from_date = from_date.split(" ")[0];

            transactionLogger.error("to_date: "+to_date);
            transactionLogger.error("from_date: "+from_date);

            String reqUrl[] = Request_URL.split("/");
            signatureData = Request_Method+"\n"+"/"+ reqUrl[3] +"/" + reqUrl[4] + "/"+from_date+"/"+to_date+"/"+terminal_id+"/"+nounce+"\n"+nounce+"\n";
            transactionLogger.error(trackingID + " PayoutInquiry Signature data: " + signatureData);

            encryptedSignature = FlexepinUtils.getHmac256Signature(api_secret.getBytes(), signatureData);
            transactionLogger.error(trackingID + " PayoutInquiry Encrypted Signature: " + encryptedSignature);

            AUTHENTICATION = api_key + ":" + encryptedSignature + ":" + nounce;
            Request_URL += "/"+from_date+"/"+to_date+"/"+terminal_id+"/"+nounce;

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin PayoutInquiry Request " +trackingID + " PayoutInquiry Request ----> " + Request_URL);
            else
                transactionLogger.error("Flexepin PayoutInquiry Request " +trackingID + " PayoutInquiry Request : " + Request_URL);

            response = FlexepinUtils.doGetHTTPUrlConnection(Request_URL, AUTHENTICATION,trackingID);

            if("Facilero".equalsIgnoreCase(toType))
                facileroLogger.error("Flexepin PayoutInquiry " +trackingID + " PayoutInquiry Response ----> " + response.trim());
            else
                transactionLogger.error("Flexepin PayoutInquiry "+trackingID + " PayoutInquiry Response: "+response.trim());

            if (functions.isValueNull(response.trim()) && FlexepinUtils.isJSONValid(response.trim()))
            {
                String trans_no             = "";
                String timestamp            = "";
                String result               = "";
                String result_description   = "";
                String serial               = "";
                String value                = "";
                String transaction_id       = "";
                String currency             = "";
                String status               = "";
                String description          = "";
                String error                = "";

                JSONObject responseJson = new JSONObject(response.trim());

                if (responseJson.has("trans_no") && functions.isValueNull(responseJson.getString("trans_no")))
                    trans_no = responseJson.getString("trans_no");
                if (responseJson.has("timestamp") && functions.isValueNull(responseJson.getString("timestamp")))
                    timestamp = responseJson.getString("timestamp");
                if (responseJson.has("result") && functions.isValueNull(responseJson.getString("result")))
                    result = responseJson.getString("result");
                if (responseJson.has("result_description") && functions.isValueNull(responseJson.getString("result_description")))
                    result_description = responseJson.getString("result_description");
                if (responseJson.has("transaction_id") && functions.isValueNull(responseJson.getString("transaction_id")))
                    transaction_id = responseJson.getString("transaction_id");

                if (responseJson.has("error") && functions.isValueNull(responseJson.getString("error")))
                    error = responseJson.getString("error");

                if (responseJson.has("transactions") && functions.isValueNull(responseJson.getString("transactions")))
                {
                    if (FlexepinUtils.isJSONValid(responseJson.getString("transactions")))
                    {
                        JSONObject transactionJson = new JSONObject(responseJson.getString("transactions"));

                        if (transactionJson.has("trans_no") && functions.isValueNull(transactionJson.getString("trans_no")))
                            trans_no = transactionJson.getString("trans_no");
                        if (transactionJson.has("timestamp") && functions.isValueNull(transactionJson.getString("timestamp")))
                            timestamp = transactionJson.getString("timestamp");
                        if (transactionJson.has("result") && functions.isValueNull(transactionJson.getString("result")))
                            result = transactionJson.getString("result");
                        if (transactionJson.has("result_description") && functions.isValueNull(transactionJson.getString("result_description")))
                            result_description = transactionJson.getString("result_description");
                        if (transactionJson.has("serial") && functions.isValueNull(transactionJson.getString("serial")))
                            serial = transactionJson.getString("serial");
                        if (transactionJson.has("value") && functions.isValueNull(transactionJson.getString("value")))
                            value = transactionJson.getString("value");
                        if (transactionJson.has("currency") && functions.isValueNull(transactionJson.getString("currency")))
                            currency = transactionJson.getString("currency");
                        if (transactionJson.has("status") && functions.isValueNull(transactionJson.getString("status")))
                            status = transactionJson.getString("status");
                        if (transactionJson.has("description") && functions.isValueNull(transactionJson.getString("description")))
                            description = transactionJson.getString("description");
                        if (transactionJson.has("transaction_id") && functions.isValueNull(transactionJson.getString("transaction_id")))
                            transaction_id = transactionJson.getString("transaction_id");
                    }
                    else if (FlexepinUtils.isJSONARRAYValid(responseJson.getString("transactions")))
                    {
                        JSONArray transactionArray = new JSONArray(responseJson.getString("transactions"));
                        for (int i=0; i <  transactionArray.length(); i++)
                        {
                            JSONObject transactionJson = new JSONObject(transactionArray.getString(i));
                            if (transactionJson.has("trans_no") && functions.isValueNull(transactionJson.getString("trans_no")))
                                trans_no = transactionJson.getString("trans_no");
                            if (transactionJson.has("timestamp") && functions.isValueNull(transactionJson.getString("timestamp")))
                                timestamp = transactionJson.getString("timestamp");
                            if (transactionJson.has("result") && functions.isValueNull(transactionJson.getString("result")))
                                result = transactionJson.getString("result");
                            if (transactionJson.has("result_description") && functions.isValueNull(transactionJson.getString("result_description")))
                                result_description = transactionJson.getString("result_description");
                            if (transactionJson.has("serial") && functions.isValueNull(transactionJson.getString("serial")))
                                serial = transactionJson.getString("serial");
                            if (transactionJson.has("currency") && functions.isValueNull(transactionJson.getString("currency")))
                                currency = transactionJson.getString("currency");
                            if (transactionJson.has("description") && functions.isValueNull(transactionJson.getString("description")))
                                description = transactionJson.getString("description");
                            if (transactionJson.has("value") && functions.isValueNull(transactionJson.getString("value")))
                                value = transactionJson.getString("value");
                            if (transactionJson.has("status") && functions.isValueNull(transactionJson.getString("status")))
                                status = transactionJson.getString("status");
                            if (transactionJson.has("transaction_id") && functions.isValueNull(transactionJson.getString("transaction_id")))
                                transaction_id = transactionJson.getString("transaction_id");

                            if(trackingID.equalsIgnoreCase(transaction_id))
                                break;
                        }
                    }
                }

                if ("0".equals(result) /*&& "ACTIVE".equalsIgnoreCase(status)*/)
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark(result_description);
                    comm3DResponseVO.setDescription(description);
                    comm3DResponseVO.setResponseTime(timestamp);
                    comm3DResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setBankRefNo(trans_no);
                    comm3DResponseVO.setAmount(value);
//                    comm3DResponseVO.setResponseHashInfo(serial);
                    comm3DResponseVO.setTransactionId(trans_no);
                    comm3DResponseVO.setMerchantId(api_key);
                }
                else if ("503".equals(result) || "4019".equals(result) || "4071".equals(result) || "4072".equals(result) || "4073".equals(result) || "4074".equals(result) || "4075".equals(result) || "4080".equals(result) || "5000".equals(result) || "4099".equals(result))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setTransactionId(trans_no);
                    if (functions.isValueNull(result_description))
                    {
                        comm3DResponseVO.setRemark(result_description);
                        comm3DResponseVO.setDescription(result_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(description);
                        comm3DResponseVO.setDescription(description);
                    }
                }
                else if ("403".equals(error) || "503".equals(error) || "4019".equals(error) || "4071".equals(error) || "4072".equals(error) || "4073".equals(error) || "4074".equals(error) || "4075".equals(error) || "4080".equals(error) || "5000".equals(error) || "4099".equals(error))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setTransactionId(trans_no);
                    if (functions.isValueNull(result_description))
                    {
                        comm3DResponseVO.setRemark(result_description);
                        comm3DResponseVO.setDescription(result_description);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(description);
                        comm3DResponseVO.setDescription(description);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
                FlexepinUtils.updateMainTableEntry(trans_no, trans_mode ,"", trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error(trackingID + "---> Exception in processPayoutInquiry: " + e);
            PZExceptionHandler.raiseTechnicalViolationException(FlexepinPaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

}
