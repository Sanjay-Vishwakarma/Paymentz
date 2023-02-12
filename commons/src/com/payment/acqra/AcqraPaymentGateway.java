package com.payment.acqra;

import com.directi.pg.AbstractAuthenticator;
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
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.phoneix.PhoneixUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Admin on 12/19/2019.
 */
public class AcqraPaymentGateway extends AbstractPaymentGateway
{   public static final String GATEWAY_TYPE = "acqra";
    private TransactionLogger transactionLogger=new TransactionLogger(AcqraPaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.acqra");

    public AcqraPaymentGateway(String accountId){this.accountId=accountId;}

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO=commRequestVO.getCommDeviceDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String saleUrl="";
        boolean isTest = gatewayAccount.isTest();
        if(isTest)
            saleUrl=RB.getString("SALE_TEST_URL");
        else
            saleUrl=RB.getString("SALE_LIVE_URL");

        try
        {
            String mid = gatewayAccount.getMerchantId();
            String apikey = gatewayAccount.getFRAUD_FTP_PATH();
            String securitykey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String amount = transDetailsVO.getAmount();
            String currency = transDetailsVO.getCurrency();
            String card_number = cardDetailsVO.getCardNum();
            String exp_month = cardDetailsVO.getExpMonth();
            String exp_year = cardDetailsVO.getExpYear();
            String security_code = cardDetailsVO.getcVV();
            String buyer_ip = addressDetailsVO.getCardHolderIpAddress(); //customer IP
            String user_Agent = "";
            String accept_language = "en";
            String hash = AcqraUtils.hashSHA256(mid + "," + currency + "," + trackingID + "," + amount + "," + securitykey);
            String lang = "en";
            String card_holder_first_name = ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
            String card_holder_last_name = ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
            String bill_street_address = addressDetailsVO.getStreet();
            String bill_city = addressDetailsVO.getCity();
            String bill_state = addressDetailsVO.getState();
            String bill_zip = addressDetailsVO.getZipCode();
            String bill_country = addressDetailsVO.getCountry();
            String email = addressDetailsVO.getEmail();
            String phone = addressDetailsVO.getPhone();
            String website = gatewayAccount.getFRAUD_FTP_SITE();
            String referer = "";

            if(functions.isValueNull(commDeviceDetailsVO.getUser_Agent()))
                user_Agent=commDeviceDetailsVO.getUser_Agent();
            else
                user_Agent="rtd";


            StringBuffer request = new StringBuffer("");
            request.append("mid=" + mid + "&apikey=" + apikey + "&currency=" + currency + "&amount=" + amount + "&order_ref=" + trackingID + "&card_number=" + card_number + "&exp_month=" + exp_month + "&exp_year=" + exp_year
                    + "&security_code=" + security_code + "&buyer_ip=" + buyer_ip + "&lang=" + lang + "&card_holder_first_name=" + card_holder_first_name + "&card_holder_last_name=" + card_holder_last_name
                    + "&bill_street_address=" + bill_street_address + "&bill_city=" + bill_city + "&bill_state=" + bill_state + "&bill_zip=" + bill_zip + "&bill_country=" + bill_country + "&email=" + email + "&phone=" + phone
                    + "&user_agent=" + user_Agent + "&accept_language=" + accept_language + "&hash=" + hash + "&website=" + website);
            if (functions.isValueNull(referer))
                request.append("&referer=" + referer);

            StringBuffer requestLog = new StringBuffer("");
            requestLog.append("mid=" + mid + "&apikey=" + apikey + "&currency=" + currency + "&amount=" + amount + "&order_ref=" + trackingID + "&card_number=" + functions.maskingPan(card_number) + "&exp_month=" + functions.maskingNumber(exp_month) + "&exp_year=" + functions.maskingNumber(exp_year)
                    + "&security_code=" + functions.maskingNumber(security_code) + "&buyer_ip=" + buyer_ip + "&lang=" + lang + "&card_holder_first_name=" + card_holder_first_name + "&card_holder_last_name=" + card_holder_last_name
                    + "&bill_street_address=" + bill_street_address + "&bill_city=" + bill_city + "&bill_state=" + bill_state + "&bill_zip=" + bill_zip + "&bill_country=" + bill_country + "&email=" + email + "&phone=" + phone
                    + "&user_agent=" + user_Agent + "&accept_language=" + accept_language + "&hash=" + hash + "&website=" + website);
            if (functions.isValueNull(referer))
                requestLog.append("&referer=" + referer);

            transactionLogger.error("saleTest request---->" + requestLog);

            String response = AcqraUtils.doHttpPostConnection(saleUrl, request.toString());
            transactionLogger.error("response---->"+response);
            if (functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject responseJSON = new JSONObject(response);
                if(responseJSON != null)
                {
                    if(responseJSON.has("status_code") && "50000".equalsIgnoreCase(responseJSON.getString("status_code")))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setErrorCode(responseJSON.getString("status_code"));
                        commResponseVO.setRemark(responseJSON.getString("status_message"));
                        commResponseVO.setDescription(responseJSON.getString("status_message"));
                        if(!functions.isValueNull(commResponseVO.getRemark()))
                        {
                            commResponseVO.setRemark(AcqraUtils.getStatusDescription(responseJSON.getString("status_code")));
                            commResponseVO.setDescription(AcqraUtils.getStatusDescription(responseJSON.getString("status_code")));
                        }
                    }else{
                        commResponseVO.setStatus("failed");
                        commResponseVO.setErrorCode(responseJSON.getString("status_code"));
                        commResponseVO.setRemark(responseJSON.getString("status_message"));
                        commResponseVO.setDescription(responseJSON.getString("status_message"));
                        if(!functions.isValueNull(commResponseVO.getRemark()))
                        {
                            commResponseVO.setRemark(AcqraUtils.getStatusDescription(responseJSON.getString("status_code")));
                            commResponseVO.setDescription(AcqraUtils.getStatusDescription(responseJSON.getString("status_code")));
                        }
                    }

                    if(responseJSON.has("transaction_id"))
                        commResponseVO.setTransactionId(responseJSON.getString("transaction_id"));
                    if(responseJSON.has("transaction_time"))
                        commResponseVO.setBankTransactionDate(responseJSON.getString("transaction_time"));
                    if(responseJSON.has("settlement_ref"))
                        commResponseVO.setResponseHashInfo(responseJSON.getString("settlement_ref"));
                }

                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ----->",e);
        }
        catch (EncodingException e)
        {
            transactionLogger.error("JSONException ----->",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        String refundUrl="";

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        if(isTest)
            refundUrl=RB.getString("REFUND_TEST_URL");
        else
            refundUrl=RB.getString("REFUND_LIVE_URL");
        transactionLogger.error("refundUrl---->"+refundUrl);
        try
        {
            String mid = gatewayAccount.getMerchantId();
            String apikey = gatewayAccount.getFRAUD_FTP_PATH();
            String securitykey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String currency = transDetailsVO.getCurrency();
            String amount = transDetailsVO.getAmount();
            String transaction_id = transDetailsVO.getPreviousTransactionId();
            String refund_description = transDetailsVO.getOrderDesc();
            String hash = AcqraUtils.hashSHA256(mid + "," + currency + "," + transaction_id + "," + amount + "," + securitykey);


            StringBuffer request = new StringBuffer("");
            request.append("mid=" + mid + "&apikey=" + apikey + "&transaction_id=" + transaction_id + "&currency=" + currency + "&amount=" + amount +"&refund_description="+refund_description+"&hash=" + hash);
            transactionLogger.error("refund request---->" + request);

            String response = AcqraUtils.doHttpPostConnection(refundUrl, request.toString());
            transactionLogger.error("refund response---->"+response);
            if (functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject responseObject = new JSONObject(response);
                if(responseObject.has("status_code") && "60000".equalsIgnoreCase(responseObject.getString("status_code")))
                {
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setStatus("success");
                    commResponseVO.setErrorCode(responseObject.getString("status_code"));
                    commResponseVO.setRemark(responseObject.getString("status_message"));
                    commResponseVO.setDescription(responseObject.getString("status_message"));
                }
                else {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setErrorCode(responseObject.getString("status_code"));
                    commResponseVO.setRemark(responseObject.getString("status_message"));
                    commResponseVO.setDescription(responseObject.getString("status_message"));
                }
                if(!functions.isValueNull(commResponseVO.getRemark()))
                {
                    commResponseVO.setRemark(AcqraUtils.getStatusDescription(responseObject.getString("status_code")));
                    commResponseVO.setDescription(AcqraUtils.getStatusDescription(responseObject.getString("status_code")));
                }
                if(responseObject.has("settlement_ref"))
                    commResponseVO.setResponseHashInfo(responseObject.getString("settlement_ref"));
                if(responseObject.has("transaction_time"))
                    commResponseVO.setBankTransactionDate(responseObject.getString("transaction_time"));
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ----->",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processQuery:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd");
        SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
        Functions functions=new Functions();
        boolean isTest = gatewayAccount.isTest();
        String enquiryUrl="";
        if (isTest)
            enquiryUrl=RB.getString("ENQUIRY_TEST_URL");
        else
            enquiryUrl=RB.getString("ENQUIRY_LIVE_URL");
        try
        {
            String mid = gatewayAccount.getMerchantId();
            String apikey = gatewayAccount.getFRAUD_FTP_PATH();
            String securitykey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String order_ref = trackingID;
            String status = "";
            String transaction_date="";
            try
            {
                System.out.println("transactionDetailsVO.getResponsetime()--->"+transactionDetailsVO.getResponsetime());
                transaction_date= dateFormat.format(dateFormat2.parse(transactionDetailsVO.getResponsetime()));
            }
            catch (ParseException e)
            {
               transactionLogger.error("ParseException e -->",e);
            }
            transactionLogger.error("transaction_date ------>"+transaction_date);
            System.out.println("transaction_date---->" + transaction_date);
            String hash = AcqraUtils.hashSHA256(mid + "," + order_ref + "," + transaction_date + "," + securitykey);


            StringBuffer request = new StringBuffer("");
            request.append("mid=" + mid + "&apikey=" + apikey + "&order_ref=" + order_ref + "&transaction_date=" + transaction_date + "&hash=" + hash);
            transactionLogger.error("enquiryOrder_refTest request---->" + request);

            String response = AcqraUtils.doHttpPostConnection(enquiryUrl, request.toString());
            transactionLogger.error("enquiryOrder_refTest response---->"+response);
            if (functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject responseJSON = new JSONObject(response);
                if(responseJSON.has("status_code") && ("50000".equalsIgnoreCase(responseJSON.getString("status_code")) || "60000".equalsIgnoreCase(responseJSON.getString("status_code"))))
                    status="success";
                else
                    status="failed";

                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId(mid);
                if(responseJSON.has("order_ref"))
                    commResponseVO.setMerchantOrderId(responseJSON.getString("order_ref"));
                if(responseJSON.has("transaction_id"))
                    commResponseVO.setTransactionId(responseJSON.getString("transaction_id"));
                commResponseVO.setTransactionType("SALE");
                commResponseVO.setTransactionStatus(status);
                if(responseJSON.has("transaction_time"))
                {
                    commResponseVO.setResponseTime(responseJSON.getString("transaction_time"));
                    commResponseVO.setBankTransactionDate(responseJSON.getString("transaction_time"));
                }
                if(responseJSON.has("status_code"))
                {
                    commResponseVO.setErrorCode(responseJSON.getString("status_code"));
                    commResponseVO.setAuthCode(responseJSON.getString("status_code"));
                }
                if(responseJSON.has("status_message"))
                {
                    commResponseVO.setRemark(responseJSON.getString("status_message"));
                    commResponseVO.setDescription(responseJSON.getString("status_message"));
                }
                if(responseJSON.has("amount"))
                    commResponseVO.setAmount(responseJSON.getString("amount"));
                if(responseJSON.has("currency"))
                    commResponseVO.setCurrency(responseJSON.getString("currency"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException e----->",e);
        }
        return commResponseVO;
    }
    public String getMaxWaitDays()
    {
        return null;
    }
}
