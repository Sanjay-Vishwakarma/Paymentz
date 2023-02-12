package com.payment.paygsmile;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* Created by Admin on 2022-01-22.
*/
public class PayGSmilePaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE     = "paygsmile";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.paygsmile");

    private static TransactionLogger transactionLogger  = new TransactionLogger(PayGSmilePaymentGateway.class.getName());


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public PayGSmilePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PayGSmilePaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of PayGSmilePaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        Date date                                       = new Date();
        SimpleDateFormat formatter                      = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        transactionLogger.error("payment_Card >>>>>>>"+payment_Card);

        String app_id       = gatewayAccount.getMerchantId();
        String secret_key   = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest      = gatewayAccount.isTest();

        String REQUEST_URL      = "";
        String out_trade_no     = trackingID;

        String bank             = "";
        String order_amount     = transactionDetailsVO.getAmount();
        String order_currency   = transactionDetailsVO.getCurrency();
        String subject          = trackingID;
        String content          = trackingID;
        String notify_url       = "";
        String return_url       = "";
        String buyer_id         = "";
        String timestamp        = formatter.format(date);
        String method           = transactionDetailsVO.getCardType();
        String channel          = transactionDetailsVO.getCardType();
        String verification_code= "";
//        String account_number   = commRequestVO.
        String account_verification_code  = commCardDetailsVO.getSecurity_Code();

        String type             = "";
        String number           = "";
        String name             = "";
        String email            = "";
        String phone            = "";
        String zip_code         = "";
        String accountnumber    = "";
        JSONObject requestJson  = new JSONObject();
        JSONObject customer     = new JSONObject();
        JSONObject identify     = new JSONObject();
        JSONObject address      = new JSONObject();



        try
        {

            notify_url       = RB.getString("NOTIFY_URL") + trackingID;
            return_url       = RB.getString("REDIRECT_URL") + trackingID;

            if("EW".equalsIgnoreCase(payment_Card))
            {
                method = "Wallet";
            }
            else if(functions.isValueNull(method))
            {
                method  = PayGSmileGatewayUtils.getPaymentBrand(method);
            }

            if(functions.isValueNull(channel))
            {
                channel  = PayGSmileGatewayUtils.getPaymentBrand(channel);
            }

            transactionLogger.error("method >>> "+trackingID+" "+method);
            transactionLogger.error("channel >>> "+trackingID+" "+channel);


            if(functions.isValueNull(transactionDetailsVO.getCustomerBankAccountName()))
            {
                bank    = transactionDetailsVO.getCustomerBankAccountName();
            }
            if("BRL".equalsIgnoreCase(order_currency) && ("Pix".equalsIgnoreCase(method) || "Lottery".equalsIgnoreCase(method)|| "Boleto".equalsIgnoreCase(method)|| "Deposit Express".equalsIgnoreCase(method) || "Wallet".equalsIgnoreCase(method)))
            {
                type    =   "CPF";
            }
            else if ("MXN".equalsIgnoreCase(order_currency) && ("SPEI".equalsIgnoreCase(method) || "CoDi".equalsIgnoreCase(method)|| "OXXO".equalsIgnoreCase(method)|| "OXXOPay".equalsIgnoreCase(method) || "Cash".equalsIgnoreCase(method)|| "Wallet".equalsIgnoreCase(method)))
            {
                type    = "RFC";
            }
            else if ("COP".equalsIgnoreCase(order_currency) && ("PSE".equalsIgnoreCase(method) || "Efecty".equalsIgnoreCase(method)|| "SuRed".equalsIgnoreCase(method)|| "Gana".equalsIgnoreCase(method) || "Baloto".equalsIgnoreCase(method)|| "BankTransfer".equalsIgnoreCase(method) || "Wallet".equalsIgnoreCase(method)))
            {
                type    = "NIT";
            }
            else if ("CLP".equalsIgnoreCase(order_currency) && ("Khipu".equalsIgnoreCase(method) || "BankTransfer".equalsIgnoreCase(method)|| "Wallet".equalsIgnoreCase(method) || "Cash".equalsIgnoreCase(method) || "Pago46".equalsIgnoreCase(method)))
            {
                type    = "RUT";
            }
            else if ("PEN".equalsIgnoreCase(order_currency) && ("Pagpeffectivo".equalsIgnoreCase(method) || "PagoEfectivo".equalsIgnoreCase(method) || "BankTransfer".equalsIgnoreCase(method)|| "Cash".equalsIgnoreCase(method)))
            {
                type    = "RUC";
            }
            else if ("USD".equalsIgnoreCase(order_currency) && ("BankTransfer".equalsIgnoreCase(method)|| "Cash".equalsIgnoreCase(method)))
            {
                type    = "CI";
            }
            else if ("GTQ".equalsIgnoreCase(order_currency) && ("BankTransfer".equalsIgnoreCase(method)|| "Cash".equalsIgnoreCase(method)))
            {
                type    = "DPI";
            }
            else if ("CRC".equalsIgnoreCase(order_currency) && ("BankTransfer".equalsIgnoreCase(method)|| "Cash".equalsIgnoreCase(method)))
            {
                type    = "ID";
            }
            else if ("CRC".equalsIgnoreCase(order_currency) && ("Cash".equalsIgnoreCase(method)))
            {
                type    = "ID";
            }else if ("ARS".equalsIgnoreCase(order_currency) && ("Khipu".equalsIgnoreCase(method) || "Rapipago".equalsIgnoreCase(method) || "PagoFacil".equalsIgnoreCase(method)))
            {
                type    = "DNI";
            }
            else if ("USD".equalsIgnoreCase(order_currency) && ("Cash".equalsIgnoreCase(method)))
            {
                type    = "DUI";
            }

            if (functions.isValueNull(transactionDetailsVO.getBankAccountNo()))
            {
                number  = transactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(transactionDetailsVO.getCustomerBankCode()))
            {
                verification_code   = transactionDetailsVO.getCustomerBankCode();
            }
            if (functions.isValueNull(order_amount))
            {
                double amt  = Double.parseDouble(order_amount);
                order_amount      = String.format("%.2f", amt);
            }
           /* if (functions.isValueNull(commMerchantVO.getMerchantId()))
            {
                buyer_id    =   commMerchantVO.getMerchantId();
            }*/
            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
            {
                name    =   commAddressDetailsVO.getFirstname();
            }
            if (functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                email   =   commAddressDetailsVO.getEmail();
            }
            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                phone   =   commAddressDetailsVO.getPhone();
            }
            if (functions.isValueNull(commAddressDetailsVO.getZipCode()))
            {
                zip_code    =   commAddressDetailsVO.getZipCode();
            }
            if (functions.isValueNull(commCardDetailsVO.getVoucherNumber()))
            {
                accountnumber   = commCardDetailsVO.getVoucherNumber();
            }

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            requestJson.put("app_id",app_id);
            requestJson.put("out_trade_no",out_trade_no);
            requestJson.put("method",method);

            if ("Wallet".equalsIgnoreCase(method)){
                requestJson.put("channel",channel);
            }

            if("DepositExpress".equalsIgnoreCase(method)){
                 requestJson.put("bank",bank);
            }/*else if("USD".equalsIgnoreCase(order_currency) && "BankTransfer".equalsIgnoreCase(method)){
                requestJson.put("bank",bank);
            }*/

            requestJson.put("order_amount",order_amount);
            requestJson.put("order_currency",order_currency);
            requestJson.put("subject",subject);
            requestJson.put("content",content);
            requestJson.put("notify_url",notify_url);
            requestJson.put("return_url",return_url);
            requestJson.put("buyer_id",trackingID);
            requestJson.put("timestamp",timestamp);
            requestJson.put("timeout_express","1c");

            if ("Todito".equalsIgnoreCase(channel))
            {
                requestJson.put("account_number", accountnumber);
                requestJson.put("account_verification_code", verification_code);
            }

            identify.put("number",number);
            identify.put("type",type);

            customer.put("identify",identify);
            customer.put("name",name);
            customer.put("email",email);
            customer.put("phone",phone);

            requestJson.put("customer",customer);
            address.put("zip_code",zip_code);
            requestJson.put("address",address);

            String keySecret            = app_id + ":" + secret_key;
            String encodedCredentials   = new String(com.directi.pg.Base64.encode(keySecret.getBytes("utf-8")));

            transactionLogger.debug("encodedCredentials: " + trackingID + " " + encodedCredentials);
            transactionLogger.error("RequestURL : " + trackingID + " " + REQUEST_URL);
            transactionLogger.error("Request Log Parameters: " + trackingID + " " + requestJson.toString());

            String response = PayGSmileGatewayUtils.doPostHTTPSURLConnection(REQUEST_URL, requestJson.toString(),encodedCredentials );

            transactionLogger.error("processSale() Response--" + trackingID + "--->" + response);

            if (functions.isValueNull(response) && functions.isJSONValid(response))
            {
                HashMap<String,String> requestHM = new HashMap<>();
                JSONObject responseJson = new JSONObject(response);

                String code         = "";
                String resmsg          = "";
                String trade_no     = "";
                String trade_status = "";
                String wallet_url   = "";
                String pay_url      = "";
                String sub_msg      = "";
                String app_link_url      = "";
                String bank_code      = "";

                String sub_code     = "";
                String prepay_id    = "";
                String web_url      = "";
                String barcode      = "";

                String resqr_code      = "";
                String bank_no      = "";



                if (responseJson.has("code")){
                    code = responseJson.getString("code");
                }
                if (responseJson.has("msg")){
                    resmsg  = responseJson.getString("msg");
                }
                if (responseJson.has("sub_code")){
                    sub_code = responseJson.getString("sub_code");
                }
                if (responseJson.has("sub_msg")){
                    sub_msg = responseJson.getString("sub_msg");
                }
                if (responseJson.has("prepay_id")){
                    prepay_id = responseJson.getString("prepay_id");
                }
                if (responseJson.has("trade_no")){
                    trade_no = responseJson.getString("trade_no");
                }
                if (responseJson.has("out_trade_no")){
                    out_trade_no = responseJson.getString("out_trade_no");
                }
                if (responseJson.has("web_url")){
                    web_url = responseJson.getString("web_url");
                }
                if (responseJson.has("trade_status")){
                    trade_status = responseJson.getString("trade_status");
                }
                if (responseJson.has("pay_url")){
                    pay_url = responseJson.getString("pay_url");
                }
                if (responseJson.has("wallet_url")){
                    wallet_url = responseJson.getString("wallet_url");
                }
                if (responseJson.has("app_link_url") && ("Wallet".equalsIgnoreCase(method) && "Vita".equalsIgnoreCase(channel) )){
                    wallet_url = responseJson.getString("app_link_url");
                }

                if(responseJson.has("form_date") && ("PSE".equalsIgnoreCase(method) || "Efecty".equalsIgnoreCase(method)|| "SuRed".equalsIgnoreCase(method)|| "Gana".equalsIgnoreCase(method)))
                {
                    JSONObject form_dateJOSN = responseJson.getJSONObject("form_date");
                    Iterator<String> keys    = form_dateJOSN.keys();

                    while(keys.hasNext()) {
                        String key = keys.next();
                        requestHM.put(key,form_dateJOSN.getString(key));
                    }
                }

                if("DepositExpress".equalsIgnoreCase(method) && "BRL".equalsIgnoreCase(order_currency))
                {
                    String provider_agency          = "";
                    String provider_number          = "";
                    String bank_name                = "";
                    String provider_owner_document  = "";

                    if (responseJson.has("provider_agency"))
                    {
                        provider_agency = responseJson.getString("provider_agency");
                    }
                    if (responseJson.has("provider_number"))
                    {
                        provider_number = responseJson.getString("provider_number");
                    }
                    if (responseJson.has("bank_name"))
                    {
                        bank_name = responseJson.getString("bank_name");
                    }

                    if (responseJson.has("provider_owner_document"))
                    {
                        provider_owner_document =responseJson.getString("provider_owner_document");
                    }

                    requestHM.put("provider_agency",provider_agency);
                    requestHM.put("provider_number",provider_number);
                    requestHM.put("bank_name",bank_name);
                    requestHM.put("provider_owner_document",provider_owner_document);
                }

                if (responseJson.has("barcode")){
                    barcode = responseJson.getString("barcode");
                }
                if (responseJson.has("bank_no")){
                    bank_no = responseJson.getString("bank_no");
                }
                if (responseJson.has("bank_code")){
                    bank_code = responseJson.getString("bank_code");
                }

                if ("10000".equalsIgnoreCase(code) && "PROCESSING".equalsIgnoreCase(trade_status) )
                {
                    if(method.equalsIgnoreCase("Mach") || ("MXN".equalsIgnoreCase(order_currency) && method.equalsIgnoreCase("Cash")))
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                    }else if(functions.isValueNull(wallet_url) || functions.isValueNull(pay_url))
                    {
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setTransactionStatus("pending3DConfirmation");

                        if ("Wallet".equalsIgnoreCase(method)){
                            comm3DResponseVO.setUrlFor3DRedirect(wallet_url);
                        }else{
                            comm3DResponseVO.setUrlFor3DRedirect(pay_url);
                        }
                        comm3DResponseVO.setRequestMap(requestHM);
                    }else{
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                    }

                    comm3DResponseVO.setTransactionId(trade_no);
                    comm3DResponseVO.setRemark(resmsg);
                    comm3DResponseVO.setDescription(resmsg);
                }
                else if (("20000".equalsIgnoreCase(code) || "40001".equalsIgnoreCase(code) || "40004".equalsIgnoreCase(code) || "40005".equalsIgnoreCase(code) || "40002".equalsIgnoreCase(code)))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(sub_msg);
                    comm3DResponseVO.setDescription(sub_msg);
                    comm3DResponseVO.setTransactionId(trade_no);
                }
                else {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setTransactionId(trade_no);
                }


            }
            else {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
            comm3DResponseVO.setThreeDVersion("3Dv1");



        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(PayGSmilePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }
    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processInquiry of Paygsmile---");
        Comm3DResponseVO commResponseVO                           = new Comm3DResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                = gatewayAccount.isTest();
        Functions functions           = new Functions();
        String app_id                 = gatewayAccount.getMerchantId();
        String secret_key             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Date date                   = new Date();
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String timestamp            = formatter.format(date);
        String out_trade_no         = trackingId;
        String trade_no             = commTransactionDetailsVO.getPreviousTransactionId();


        String REQUEST_URL = "";
        JSONObject requestJOSN = new JSONObject();
        try
        {

            String base64Credentials = app_id + ":" + secret_key;
            String encodedCredentials = new String(com.directi.pg.Base64.encode(base64Credentials.getBytes("utf-8")));

            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            requestJOSN.put("app_id", app_id);
            requestJOSN.put("timestamp", timestamp);
            requestJOSN.put("out_trade_no", out_trade_no);
            requestJOSN.put("trade_no", trade_no);

            transactionLogger.error("REQUEST_URL --> " + trackingId + " " + REQUEST_URL);
            transactionLogger.error("Inquiry Request is --> " + trackingId + " " + requestJOSN.toString());

            String response = PayGSmileGatewayUtils.doPostHTTPSURLConnection(REQUEST_URL, requestJOSN.toString(), encodedCredentials);
            transactionLogger.error("Inquiry Response is -- > " + trackingId + " " + response);

            if (functions.isValueNull(response) && functions.isJSONValid(response))
            {
                String code             = "";
                String msg              = "";
                String resTrade_no      = "";
                String resOut_trade_no  = "";
                String trade_status     = "";
                String order_currency   = "";
                String order_amount     = "";
                String refuse_detail    = "";
                String create_time      = "";
                String update_time      = "";
                String sub_code         = "";
                String sub_msg          = "";

                JSONObject responseJSON = new JSONObject(response);


                if(responseJSON.has("code")){
                    code =  responseJSON.getString("code");
                }
                if(responseJSON.has("msg")){
                    msg =  responseJSON.getString("msg");
                }
                if(responseJSON.has("trade_no")){
                    resTrade_no =  responseJSON.getString("trade_no");
                }
                if(responseJSON.has("trade_status")){
                    trade_status =  responseJSON.getString("trade_status");
                }
                if(responseJSON.has("order_currency")){
                    order_currency =  responseJSON.getString("order_currency");
                }
                if(responseJSON.has("resOut_trade_no")){
                    resOut_trade_no =  responseJSON.getString("resOut_trade_no");
                }
                if(responseJSON.has("refuse_detail")){
                    resOut_trade_no =  responseJSON.getString("refuse_detail");
                }
                if(responseJSON.has("create_time")){
                    create_time =  responseJSON.getString("create_time");
                }
                if(responseJSON.has("order_amount")){
                    order_amount =  responseJSON.getString("order_amount");
                }
                if(responseJSON.has("update_time")){
                    update_time =  responseJSON.getString("update_time");
                }
                if(responseJSON.has("sub_code")){
                    sub_code =  responseJSON.getString("sub_code");
                }
                if(responseJSON.has("sub_msg")){
                    sub_msg =  responseJSON.getString("sub_msg");
                }


                if ("10000".equalsIgnoreCase(code) && "SUCCESS".equalsIgnoreCase(trade_status))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setDescription(msg);
                    commResponseVO.setRemark(trade_status);
                    commResponseVO.setAmount(order_amount);
                    commResponseVO.setTransactionId(resTrade_no);
                    commResponseVO.setBankTransactionDate(update_time);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setAuthCode(code);
                    commResponseVO.setResponseHashInfo(resTrade_no);
                    commResponseVO.setCurrency(order_currency);
                }else if ("PROCESSING".equalsIgnoreCase(trade_status) || "INITIAL".equalsIgnoreCase(trade_status) || "INITIAL".equalsIgnoreCase(trade_status))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescription(msg);
                    commResponseVO.setRemark(trade_status);
                    commResponseVO.setAmount(order_amount);
                    commResponseVO.setTransactionId(resTrade_no);
                    commResponseVO.setBankTransactionDate(update_time);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setAuthCode(code);
                    commResponseVO.setResponseHashInfo(resTrade_no);
                    commResponseVO.setCurrency(order_currency);
                }
                else if ("CANCEL".equalsIgnoreCase(trade_status) || "DISPUTE".equalsIgnoreCase(trade_status)  || "REFUSED".equalsIgnoreCase(trade_status) ||
                        "40002".equalsIgnoreCase(code) || "20000".equalsIgnoreCase(code) || "40001".equalsIgnoreCase(code) || "40004".equalsIgnoreCase(code) || "40005".equalsIgnoreCase(code))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("Failed");

                    if("40002".equalsIgnoreCase(code)){
                        commResponseVO.setDescription(sub_code);
                        commResponseVO.setRemark(sub_msg);
                    }else {
                        commResponseVO.setDescription(msg);
                        commResponseVO.setRemark(trade_status);
                    }
                    commResponseVO.setAmount(order_amount);
                    commResponseVO.setTransactionId(resTrade_no);
                    commResponseVO.setBankTransactionDate(update_time);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setAuthCode(code);
                    commResponseVO.setResponseHashInfo(resTrade_no);
                    commResponseVO.setCurrency(order_currency);

                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                commResponseVO.setMerchantId(app_id);
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
            commResponseVO.setThreeDVersion("3Dv1");


        }catch (Exception e){
            transactionLogger.error("PayGSmilePaymentGateway processQuery Exception ---> "+trackingId,e);
        }
        return commResponseVO;
    }


    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- processAutoRedirect in PayGSmile ----");
        PayGSmilePaymentProcess omnipayPaymentProcess             = new PayGSmilePaymentProcess();
        String html                                             = "";
        Comm3DResponseVO transRespDetails                       = null;
        PayGSmileGatewayUtils PayGSmilePaymentGatewayUtils      = new PayGSmileGatewayUtils();
        String paymentMode                                      = commonValidatorVO.getPaymentMode();
        CommRequestVO commRequestVO                             = PayGSmilePaymentGatewayUtils.getOmniPayPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest              = gatewayAccount.isTest();


        try
        {
            transactionLogger.error("  tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = omnipayPaymentProcess.get3DConfirmationForm(commonValidatorVO.getTrackingid(),"", transRespDetails);
                transactionLogger.error("Auto Redirect PayGSmile form -- >>" + html);
            }
            else{
                html    = transRespDetails.getStatus();
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in PayGSmile---", e);
        }
        return html;
    }


    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processPayout() of SmartFastPayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest     = gatewayAccount.isTest();
        String merchantId  = gatewayAccount.getMerchantId();
        String app_key     = gatewayAccount.getFRAUD_FTP_USERNAME();
        String AppId       = gatewayAccount.getFRAUD_FTP_PATH();
        String fee_bear    = "merchant";//*

        String NOTIFY_URL               = "";

        JSONObject requestJSON          = new JSONObject();
        String payment_brand            = transactionDetailsVO.getCardType();
        Map<String ,String> parameter   = new HashMap<>();

        String REQUEST_URL      = "";
        String name             = "";// *
        String phone            = "";
        String email            = "";
        String amount           = ""; //*
        String arrival_currency = gatewayAccount.getCurrency(); // fixed value: BRL *
        String country          = ""; // fixed value: BRA **
        String method           = "";   // fixed value: PIX *


        String custom_code       = trackingId; //*
        String notify_url        = ""; //*
        String additional_remark = trackingId; //*
        String source_currency   = ""; //*


        String account       = ""; //*
        String account_type  = "";  // * should be one of CPF CNPJ PHONE EMAIL EVP
        String document_id   = "";//*
        String document_type = ""; //*

        String bank_code     = "";//BankTransfer
        String account_digit = "";//BankTransfer
        String branch        = "";//BankTransfer
        String region        = "";//BankTransfer

        try
        {
            transactionLogger.error("payment_brand before >>>>> "+payment_brand);

            payment_brand = PayGSmileGatewayUtils.getPaymentBrand(payment_brand);

            source_currency     = transactionDetailsVO.getCurrency();
            amount              = transactionDetailsVO.getAmount();

            if(arrival_currency.equalsIgnoreCase("BRL"))
            {
                country     = "BRA";
            }else if(arrival_currency.equalsIgnoreCase("MXN"))
            {
                country     = "MEX";
            }else if(arrival_currency.equalsIgnoreCase("CLP"))
            {
                country     = "CHL";
                amount      = PayGSmileGatewayUtils.getAmount(amount);
            }else if(arrival_currency.equalsIgnoreCase("COP"))
            {
                country     = "COL";
                amount      = PayGSmileGatewayUtils.getAmount(amount);
            }else if(arrival_currency.equalsIgnoreCase("PEN"))
            {
                country     = "PER";
                amount      = PayGSmileGatewayUtils.getAmount(amount);
            }else if(arrival_currency.equalsIgnoreCase("COP"))
            {
                country     = "COL";
                amount      = PayGSmileGatewayUtils.getAmount(amount);
            }else if(arrival_currency.equalsIgnoreCase("USD"))
            {
                country     = "ECU";
                amount      = PayGSmileGatewayUtils.getAmount(amount);
            }

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_PAYOUT_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_PAYOUT_URL");
            }

            if(functions.isValueNull(transactionDetailsVO.getBankTransferType())){
                method    = transactionDetailsVO.getBankTransferType();
            }

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                notify_url = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingId;
            }else{
                notify_url = RB.getString("NOTIFY_URL") + trackingId;
            }

            if(functions.isValueNull(transactionDetailsVO.getCustomerBankAccountNumber())){
                account = transactionDetailsVO.getCustomerBankAccountNumber();
            }
            if (functions.isValueNull(transactionDetailsVO.getBankAccountNo()))
            {
                document_id = transactionDetailsVO.getBankAccountNo();
            }
            if(functions.isValueNull(transactionDetailsVO.getAccountType())){
                account_type        = transactionDetailsVO.getAccountType();
            }
            if(functions.isValueNull(transactionDetailsVO.getBankIfsc())){
                document_type        = transactionDetailsVO.getBankIfsc();
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                email        = commAddressDetailsVO.getEmail();
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                phone        = commAddressDetailsVO.getPhone();
            }
            if(functions.isValueNull(transactionDetailsVO.getCustomerBankAccountName()))
            {
                name        = transactionDetailsVO.getCustomerBankAccountName();
            }

            if(functions.isValueNull(transactionDetailsVO.getBranchCode()))
            {
                branch        = transactionDetailsVO.getBranchCode();
            }
            if(functions.isValueNull(transactionDetailsVO.getBankCode()))
            {
                bank_code        = transactionDetailsVO.getBankCode();
            }
            if(functions.isValueNull(transactionDetailsVO.getBranchName()))
            {
                account_digit        = transactionDetailsVO.getBranchName();
            }

            transactionLogger.error("payment_brand after >>>>> "+trackingId +" method -----> "+method +" payment_brand "+payment_brand+" ");

            parameter.put("method",method);
            parameter.put("name",name);
            parameter.put("phone",phone);
            parameter.put("email",email);
            parameter.put("amount",amount);
            parameter.put("arrival_currency",arrival_currency);
            parameter.put("custom_code",custom_code);
            parameter.put("document_id",document_id);
            parameter.put("document_type",document_type);
            parameter.put("fee_bear",fee_bear);
            parameter.put("notify_url",notify_url);
            parameter.put("account",account);
            parameter.put("source_currency",source_currency);
            parameter.put("additional_remark",additional_remark);
            parameter.put("country",country);

            if("PIX".equalsIgnoreCase(method) && "PIX".equalsIgnoreCase(payment_brand))
            {
                parameter.put("account",account);
                parameter.put("account_type",account_type);
                parameter.put("document_id",document_id);
                parameter.put("document_type",document_type);

            }else if(method.equalsIgnoreCase("BANKTRANSFER") &&  PayGSmileGatewayUtils.isCartTypeForBankTransfer(method,payment_brand))
            {
                parameter.put("account",account);
                parameter.put("account_type",account_type);
                parameter.put("document_id",document_id);
                parameter.put("document_type",document_type);
                parameter.put("bank_code",bank_code);

                if("DepositExpress".equalsIgnoreCase(payment_brand)){
                    parameter.put("branch",branch);
                    parameter.put("account_digit",account_digit);
                }

                if("PEN".equalsIgnoreCase(arrival_currency)){
                    parameter.put("region",account_digit);
                }

            }else  if("SPEI".equalsIgnoreCase(method) && "SPEI".equalsIgnoreCase(payment_brand))
            {
                parameter.put("account",account);
                parameter.put("account_type",account_type);
                parameter.put("document_id",document_id);
                parameter.put("document_type",document_type);
                parameter.put("bank_code",bank_code);
            }

            String authorizationKey =  PayGSmileGatewayUtils.getSign(parameter, app_key);

            transactionLogger.debug("AppId >>>>>>> " + AppId);
            transactionLogger.debug("Authorization >>>>>>> " + authorizationKey);

            for (String key : parameter.keySet()) {
                requestJSON.put(key,parameter.get(key));
            }
            transactionLogger.error("Payout Request -------->  " + trackingId + " " + requestJSON);

            String responseString = PayGSmileGatewayUtils.doPayoutPostHTTPSURLConnection(AppId,authorizationKey,REQUEST_URL,requestJSON.toString());

            transactionLogger.error("Payout Response -------->  " + trackingId + " " + responseString);


            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON     =  new JSONObject(responseString);
                JSONObject dataJSON         =  null;

                String id                   = "";
                String resCustom_code       = "";
                String arrival_amount       = "";
                String resArrival_currency  = "";
                String source_amount        = "";
                String resSource_currency  = "";
                String status               = "";
                String code                 = "";
                String msg                  = "";
                String err                  = "";
                String time                 = "";

                if(responseJSON.has("data")){
                    dataJSON = responseJSON.getJSONObject("data");
                }

                if(responseJSON.has("code")){
                    code = responseJSON.getString("code");
                }
                if(responseJSON.has("msg")){
                    msg = responseJSON.getString("msg");
                }
                if(responseJSON.has("time")){
                    time = responseJSON.getString("time");
                }

                if(dataJSON != null)
                {
                    if(dataJSON.has("status")){
                        status = dataJSON.getString("status");
                    }
                    if(dataJSON.has("id")){
                        id = dataJSON.getString("id");
                    }if(dataJSON.has("err")){
                        err = dataJSON.getString("err");
                    }
                    if(dataJSON.has("arrival_amount")){
                        arrival_amount = dataJSON.getString("arrival_amount");
                        arrival_amount =  String.format("%.2f", Double.parseDouble(arrival_amount));
                    }
                    if(dataJSON.has("arrival_currency")){
                        resArrival_currency = dataJSON.getString("arrival_currency");
                    }
                    if(dataJSON.has("source_amount")){
                        source_amount = dataJSON.getString("source_amount");
                    }
                    if(dataJSON.has("source_currency")){
                        resSource_currency = dataJSON.getString("source_currency");
                    }
                }

                if(functions.isValueNull(id)){
                    PayGSmileGatewayUtils.updatePaymentId(id,trackingId);
                }


                if("PAID".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setAmount(arrival_amount);
                    comm3DResponseVO.setTransactionId(id);
                    comm3DResponseVO.setResponseHashInfo(id);
                    comm3DResponseVO.setCurrency(resArrival_currency);
                    comm3DResponseVO.setResponseTime(time);
                    comm3DResponseVO.setMerchantId(merchantId);
                }else if("REJECTED".equalsIgnoreCase(status) || PayGSmileGatewayUtils.isFailed(code))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");

                    if(functions.isValueNull(msg)){
                        comm3DResponseVO.setRemark(msg +" "+err);
                        comm3DResponseVO.setDescription(msg+" "+err);
                    }else{
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                    }

                    comm3DResponseVO.setAmount(arrival_amount);
                    comm3DResponseVO.setTransactionId(id);
                    comm3DResponseVO.setResponseHashInfo(id);
                    comm3DResponseVO.setCurrency(resArrival_currency);
                    comm3DResponseVO.setResponseTime(time);
                    comm3DResponseVO.setMerchantId(merchantId);

                }else if("IN_PROCESSING".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status)){
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Pending");  // set inquiry response not found
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setAmount(arrival_amount);
                    comm3DResponseVO.setTransactionId(id);
                    comm3DResponseVO.setResponseHashInfo(id);
                    comm3DResponseVO.setCurrency(resArrival_currency);
                    comm3DResponseVO.setResponseTime(time);
                    comm3DResponseVO.setMerchantId(merchantId);

                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==> "+trackingId , e);
            PZExceptionHandler.raiseTechnicalViolationException(PayGSmilePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processPayoutInquiry() of PayGSmilePaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest     = gatewayAccount.isTest();
        String merchantId  = gatewayAccount.getMerchantId();
        String app_key     = gatewayAccount.getFRAUD_FTP_USERNAME();
        String AppId       = gatewayAccount.getFRAUD_FTP_PATH();

        String REQUEST_URL = "";
        String payout_id    =  transactionDetailsVO.getPreviousTransactionId();
        String custom_code  =  trackingId;
        String toType      =  transactionDetailsVO.getTotype();

        JSONObject requestJSON          = new JSONObject();
        Map<String ,String> parameter   = new HashMap<>();
        try
        {

            if (isTest){
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }else {
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            transactionLogger.error("INQUIRY Request URL --" + trackingId + "--->" + REQUEST_URL);


            parameter.put("custom_code",custom_code);
            parameter.put("payout_id",payout_id);

            for (String key : parameter.keySet()) {
                requestJSON.put(key,parameter.get(key));
            }

            String authorizationKey =  PayGSmileGatewayUtils.getSign(parameter, app_key);

            transactionLogger.debug("AppId >>>>>>> " + AppId);
            transactionLogger.debug("Authorization >>>>>>> " + authorizationKey);

            transactionLogger.error("Payout Inquiry Request -------->  " + trackingId + " " + requestJSON.toString());

            String responseString = PayGSmileGatewayUtils.doPayoutPostHTTPSURLConnection(AppId,authorizationKey,REQUEST_URL,requestJSON.toString());

            transactionLogger.error("Payout Inquiry Response -------->  " + trackingId + " " + responseString);

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString))
            {
                JSONObject responseJSON     =  new JSONObject(responseString);
                JSONObject dataJSON         =  null;

                String transactionId               = "";
                String amount           = "";
                String currency         = "";
                String status           = "";

                String code                 = "";
                String msg                  = "";
                String err                  = "";
                String time                 = "";

                if(responseJSON.has("code")){
                    code = responseJSON.getString("code");
                }
                if(responseJSON.has("msg")){
                    msg = responseJSON.getString("msg");
                }
                if(responseJSON.has("time")){
                    time = responseJSON.getString("time");
                    long milliSeconds = Long.parseLong(time + "000");
                    Calendar calendar           = Calendar.getInstance();
                    calendar.setTimeInMillis(milliSeconds);
                    DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    time        = formatter1.format(calendar.getTime());
                }

                if(responseJSON.has("data")){
                    dataJSON = responseJSON.getJSONObject("data");
                }

                if(dataJSON !=null)
                {
                    if(dataJSON.has("id")){
                        transactionId = dataJSON.getString("id");
                    }
                    if(dataJSON.has("arrival_amount")){
                        amount = dataJSON.getString("arrival_amount");
                        amount =  String.format("%.2f", Double.parseDouble(amount));
                    }
                    if(dataJSON.has("arrival_currency")){
                        currency = dataJSON.getString("arrival_currency");
                    }
                    if(dataJSON.has("status")){
                        status = dataJSON.getString("status");
                    }
                }

                if("PAID".equalsIgnoreCase(status)){
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setTransactionId(transactionId);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                     comm3DResponseVO.setResponseTime(time);
                    comm3DResponseVO.setMerchantId(merchantId);
                }else if("REJECTED".equalsIgnoreCase(status) || PayGSmileGatewayUtils.isFailed(code)){
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setTransactionId(transactionId);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setResponseTime(time);
                    comm3DResponseVO.setMerchantId(merchantId);
                }else if("PROCESSING".equalsIgnoreCase(status) || "IN_PROCESSING".equalsIgnoreCase(status)){
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    if(functions.isValueNull(msg)){
                        comm3DResponseVO.setRemark(msg +" "+err);
                        comm3DResponseVO.setDescription(msg+" "+err);
                    }else{
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                    }
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setTransactionId(transactionId);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setResponseTime(time);
                    comm3DResponseVO.setMerchantId(merchantId);
                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
            comm3DResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());

        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayGSmilePaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }
}
