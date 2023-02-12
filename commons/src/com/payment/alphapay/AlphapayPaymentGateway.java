package com.payment.alphapay;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 05-Dec-22.
 */
public class AlphapayPaymentGateway extends AbstractPaymentGateway
{

    private static TransactionLogger transactionLogger = new TransactionLogger( AlphapayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "Alphapay";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.alphapay");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public AlphapayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.BANK_RESP_CONNECTION_TIME_OUT.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlphapayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale of AlphapayPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        StringBuffer reqParameter                       = new StringBuffer();

        boolean isTest = gatewayAccount.isTest();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String REQUEST_URL         = "";
        String REDIRECT_URL         = "";
        String amount              = "";
        String response            = "";
        String authorization       = "";
        String currency_code       = "";
        String token               = "";
        String merchant_reference  ="";
        String customer_reference  ="";
        String error_url           ="";
        String cancel_url          ="";
        String payment_brand    = transactionDetailsVO.getCardType();

        try
        {
            if (isTest)
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            else
                REQUEST_URL = RB.getString("LIVE_SALE_URL");

            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                REDIRECT_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            else
                REDIRECT_URL = RB.getString("REDIRECT_URL") + trackingID;

            authorization = Base64.encode((username + ":" + password).getBytes("utf-8"));

            if(functions.isValueNull(transactionDetailsVO.getAmount()))
                amount =transactionDetailsVO.getAmount();

            payment_brand = AlphapayUtils.getPaymentBrand(payment_brand);
            currency_code = transactionDetailsVO.getCurrency();

            reqParameter.append("amount="+amount);
            reqParameter.append("&merchant_reference="+trackingID);
            reqParameter.append("&customer_reference="+trackingID);
            reqParameter.append("&success_url="+REDIRECT_URL);
            reqParameter.append("&error_url="+REDIRECT_URL);
            reqParameter.append("&cancel_url="+REDIRECT_URL);
            reqParameter.append("&notify_url="+RB.getString("NOTIFY_URL"));
            reqParameter.append("&payment_type="+payment_brand);
            reqParameter.append("&currency_code="+currency_code);

            transactionLogger.error(trackingID + " ProcessSale() Request url: " + REQUEST_URL + " Request Body: " + reqParameter.toString());
            response = AlphapayUtils.doPostFormHTTPSURLConnectionClient(authorization, reqParameter.toString(), REQUEST_URL, trackingID);

            transactionLogger.error(trackingID + " ProcessSale() Response: " + response);

            String url = "";
            if(functions.isValueNull(response)&& AlphapayUtils.isJSONValid(response))
            {
                JSONObject jsonObject = new JSONObject(response);

                if(jsonObject.has("url"))
                    url = jsonObject.getString("url") ;

                if (functions.isValueNull(url))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(url);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("pending");
                    comm3DResponseVO.setDescription("pending");
                }
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setRemark("pending");
                comm3DResponseVO.setDescription("pending");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException( AlphapayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;

    }

    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        transactionLogger.error("Inside processInquiry of AlphapayPaymentGateway--");
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);


        boolean isTest = gatewayAccount.isTest();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String REQUEST_URL        = "";
        String trxn_id            = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("transaction_id in Gateway >>>>>>>>>>>>>"+trxn_id);
        String response           = "";
        String authorization      = "";

        try
        {
            if (isTest)
                REQUEST_URL = RB.getString("TEST_INQUIRY_URL")+trxn_id;
            else
                REQUEST_URL = RB.getString("LIVE_INQUIRY_URL")+trxn_id;

            authorization = Base64.encode((username + ":" + password).getBytes("utf-8"));

            transactionLogger.error(trackingId + " processInquiry() requestUrl: "+ REQUEST_URL);
            response = AlphapayUtils.doGetHTTPUrlConnection(REQUEST_URL,authorization,trackingId);
            transactionLogger.error(trackingId + " processInquiry() response: "+ response);

            if (functions.isValueNull(response) && AlphapayUtils.isJSONValid(response))
            {
                String id                               = "";
                String successful                       = "";
                String status                           = "";
                String amount                           = "";
                String reason                            = "";
                String currency                          = "";
                String gateway_reference                 = "";
                String payment_key                       = "";
                String created                           = "";

                JSONObject jsonObject                   = new JSONObject(response);

                if (jsonObject.has("id"))
                {
                    id = jsonObject.getString("id");
                }
                if (jsonObject.has("successful"))
                {
                    successful = jsonObject.getString("successful");
                }
                if (jsonObject.has("status"))
                {
                    status = jsonObject.getString("status");
                }
                if (jsonObject.has("amount"))
                {
                    amount = jsonObject.getString("amount");
                }
                if (jsonObject.has("reason"))
                {
                    reason = jsonObject.getString("reason");
                }
                if (jsonObject.has("currency"))
                {
                    currency = jsonObject.getString("currency");
                }
                if (jsonObject.has("gateway_reference"))
                {
                    gateway_reference = jsonObject.getString("gateway_reference");
                }
                if (jsonObject.has("payment_key"))
                {
                    payment_key = jsonObject.getString("payment_key");
                }
                if (jsonObject.has("created"))
                {
                    created = jsonObject.getString("created");
                }

                if ("complete".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setTransactionId(id);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setAuthCode(payment_key);
                    comm3DResponseVO.setResponseTime(created);
                }
                else if ("failed".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setTransactionId(id);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(reason);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setAuthCode(payment_key);
                    comm3DResponseVO.setResponseTime(created);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
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
        catch (Exception e)
        {
            transactionLogger.error(trackingId +" Exception in processQuery() ---->", e);
        }
        return comm3DResponseVO;
    }

    public  GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {

        transactionLogger.error("Entering processPayout() of  AlphapayPaymentGateway......");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest          = gatewayAccount.isTest();
        String username         = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String REQUEST_URL          = "";
        String VALIDATE_URL         = "";
        String bank                 = "";
        String branch               ="";
        String account_number        ="";
        String customer_name        ="";
        String amount               ="";
        String authentication       ="";
        String reference            = trackingID; //commTransactionDetailsVO.getPreviousTransactionId();
        String response            = "";
        StringBuffer requestParameter = new StringBuffer();
        StringBuffer requestPayout = new StringBuffer();
        try
        {
            if(isTest)
            {
                REQUEST_URL    =RB.getString("TEST_PAYOUT_URL");
                VALIDATE_URL  = RB.getString("TEST_VALIDATE_URL");
            }
            else
            {
                REQUEST_URL =RB.getString("LIVE_PAYOUT_URL");
                VALIDATE_URL=RB.getString("LIVE_VALIDATE_URL");
            }

            authentication = Base64.encode((username + ":" + password).getBytes("utf-8"));

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                customer_name   = commTransactionDetailsVO.getCustomerBankAccountName();
            }
            if(functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountNumber()))
            {
                account_number=commTransactionDetailsVO.getCustomerBankAccountNumber();
            }
            if(functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                amount=commTransactionDetailsVO.getAmount();
            }
            if(functions.isValueNull(commTransactionDetailsVO.getBankName()))
            {
                bank =commTransactionDetailsVO.getBankName();
            }
            if(functions.isValueNull(commTransactionDetailsVO.getBranchCode()))
            {
                branch =commTransactionDetailsVO.getBranchCode();
            }

            requestParameter.append("transaction[bank]="+bank);
            requestParameter.append("&transaction[branch]="+branch);
            requestParameter.append("&transaction[account]="+account_number);
            requestParameter.append("&transaction[customer_name]="+customer_name);

            transactionLogger.error(trackingID + " processPayout() VALIDATE_Request url: " + VALIDATE_URL + " Request Body: " + requestParameter.toString());
            response = AlphapayUtils.doPostFormHTTPSURLConnectionClient(authentication, requestParameter.toString(),VALIDATE_URL,trackingID);

            transactionLogger.error(trackingID + "  processPayout Response: " + response);

            if(functions.isValueNull(response) && AlphapayUtils.isJSONValid(response))
            {
                String success="";
                String message = "";
                String ERROR = "";

                JSONObject jsonObject=new JSONObject(response);

                if(jsonObject.has("success"))
                {
                    success=jsonObject.getString("success");
                }
                if(jsonObject.has("message"))
                {
                    message=jsonObject.getString("message");
                }

                if(success.equalsIgnoreCase("true"))
                {
                    requestPayout.append("transaction[bank]="+bank);
                    requestPayout.append("&transaction[amount]="+amount);
                    requestPayout.append("&transaction[branch]="+branch);
                    requestPayout.append("&transaction[account]="+account_number);
                    requestPayout.append("&transaction[customer_name]="+customer_name);
                    requestPayout.append("&transaction[reference]="+reference);

                    transactionLogger.error(trackingID + "  processPayout() Request url: " + REQUEST_URL + " Request Body: " + requestPayout.toString());
                    response = AlphapayUtils.doPostFormHTTPSURLConnectionClient(authentication, requestPayout.toString(),REQUEST_URL, trackingID);

                    transactionLogger.error(trackingID + " processPayout() Response: " + response);

                    if(functions.isValueNull(response) && AlphapayUtils.isJSONValid(response))
                    {
                        String total_amount="";
                        String id="";

                        JSONObject responseJson = new JSONObject(response);
                        JSONArray transactionJson = null;
                        JSONObject dataJSON  = new JSONObject();

                        if(responseJson.has("success"))
                        {
                            success=responseJson.getString("success");
                        }
                        transactionLogger.error("Sucess value is >>>>>>>>>>>>>>>>>>>>>>>>>"+success);
                        if(responseJson.has("message"))
                        {
                            message=responseJson.getString("message");
                        }
                        if(responseJson.has("total_amount"))
                        {
                            total_amount=responseJson.getString("total_amount");
                        }
                        if(responseJson.has("transactions"))
                        {
                            transactionJson=responseJson.getJSONArray("transactions");

                            if (transactionJson.length()>0)
                            {
                                dataJSON = (JSONObject)transactionJson.get(0);
                                if(dataJSON.has("id"))
                                {
                                    id=dataJSON.getString("id");
                                }
                                if(dataJSON.has("ERROR"))
                                {
                                    ERROR=dataJSON.getString("ERROR");
                                }
                            }
                        }

                        if ("true".equalsIgnoreCase(success))
                        {
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setTransactionStatus("pending");
                            comm3DResponseVO.setRemark(message);
                            comm3DResponseVO.setDescription(message);
                            //comm3DResponseVO.setDescription("Payout successful");
                            comm3DResponseVO.setTransactionId(id);
                            comm3DResponseVO.setAmount(total_amount);
                        }
                        else if("false".equalsIgnoreCase(success))
                        {
                            comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setTransactionStatus("failed");
                            comm3DResponseVO.setRemark(message);
                            comm3DResponseVO.setDescription(message);
                            comm3DResponseVO.setTransactionId(id);
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setTransactionStatus("pending");
                            comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        }

                        AlphapayUtils.updateMainTableEntry(id,message,trackingID);
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    }
                }
                else if (success.equalsIgnoreCase("false"))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
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
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(AlphapayPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        transactionLogger.error("Inside processInquiry of AlphapayPaymentGateway payout Inquiry---");
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);


        boolean isTest = gatewayAccount.isTest();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String REQUEST_URL        = "";
        String trxn_id            = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("transaction_id in Gateway >>>>>>>>>>>>>"+trxn_id);
        String response           = "";
        String authorization      = "";

        try
        {
            if (isTest)
                REQUEST_URL = RB.getString("TEST_PAYOUT_INQUIRY_URL")+trxn_id;
            else
                REQUEST_URL = RB.getString("LIVE_PAYOUT_INQUIRY_URL")+trxn_id;

            authorization = Base64.encode((username + ":" + password).getBytes("utf-8"));

            transactionLogger.error(trackingId + " PayoutInquiry() requestUrl: "+ REQUEST_URL);
            response = AlphapayUtils.doGetHTTPUrlConnection(REQUEST_URL,authorization,trackingId);
            transactionLogger.error(trackingId + " PayoutInquiry() response: "+ response);

            if (functions.isValueNull(response) && AlphapayUtils.isJSONValid(response))
            {
                String id                               = "";
                String successful                       = "";
                String status                           = "";
                String amount                           = "";
                String reason                            = "";
                String currency                          = "";
                String gateway_reference                 = "";
                String payment_key                       = "";
                String created                           = "";


                JSONObject jsonObject                   = new JSONObject(response);
                if (jsonObject.has("id"))
                {
                    id = jsonObject.getString("id");
                }
                if (jsonObject.has("successful"))
                {
                    successful = jsonObject.getString("successful");
                }
                if (jsonObject.has("status"))
                {
                    status = jsonObject.getString("status");
                }
                if (jsonObject.has("amount"))
                {
                    amount = jsonObject.getString("amount");
                }
                if (jsonObject.has("reason"))
                {
                    reason = jsonObject.getString("reason");
                }
                if (jsonObject.has("currency"))
                {
                    currency = jsonObject.getString("currency");
                }
                if (jsonObject.has("gateway_reference"))
                {
                    gateway_reference = jsonObject.getString("gateway_reference");
                }
                if (jsonObject.has("payment_key"))
                {
                    payment_key = jsonObject.getString("payment_key");
                }
                if (jsonObject.has("created"))
                {
                    created = jsonObject.getString("created");
                }

                if ("complete".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setTransactionId(id);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setAuthCode(payment_key);
                    comm3DResponseVO.setResponseTime(created);
                }
                else if ("failed".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setTransactionId(id);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(reason);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setAuthCode(payment_key);
                    comm3DResponseVO.setResponseTime(created);

                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
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
        catch (Exception e)
        {
            transactionLogger.error(trackingId +" Exception in processPayoutInquiry() ---->", e);
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside Alphapay processRefund() ---------->");

        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Functions functions                                 = new Functions();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest          = gatewayAccount.isTest();
        String username          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password          = gatewayAccount.getFRAUD_FTP_PASSWORD();


        String   REQUEST_URL          = "";
        String amount               ="";
        String transaction_id      ="";
        String response            ="";
        String authentication       ="";

        StringBuffer requestParameter = new StringBuffer();
        try
        {
            if (functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionId()))
            {
                transaction_id  = commTransactionDetailsVO.getPreviousTransactionId();
            }

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_REFUND_URL")+"/"+ transaction_id;
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_REFUND_URL")+"/"+ transaction_id;
            }

            authentication = Base64.encode((username + ":" + password).getBytes("utf-8"));

            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                amount=commTransactionDetailsVO.getAmount();
            }

            requestParameter.append(("amount="+ amount));

            transactionLogger.error(trackingID + " processRefund() Request url: " + REQUEST_URL  + " Request Body: " + requestParameter.toString());
            response=AlphapayUtils.doPutFormHTTPSURLConnectionClient(authentication, requestParameter.toString(), REQUEST_URL,trackingID);

            transactionLogger.error(trackingID + " processRefund() Response: " + response);

            if(functions.isValueNull(response)&& functions.isJSONValid(response))
            {
                boolean result = false;

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.has("result"))
                {
                    result = jsonObject.getBoolean("result");
                }

                if(result)
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                }
                else
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
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
        catch (Exception e)
        {
            transactionLogger.error(trackingID + "---> Exception in processRefund: " + e);
            PZExceptionHandler.raiseTechnicalViolationException(AlphapayPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in  AlphapayPaymentGateway---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        AlphapayPaymentProcess  alphapayPaymentProcess = new AlphapayPaymentProcess();
        AlphapayUtils alphapayUtils                  = new AlphapayUtils();
        CommRequestVO commRequestVO =alphapayUtils.getAlphapayRequestVo(commonValidatorVO);
        try
        {
            transactionLogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = alphapayPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                transactionLogger.error(commonValidatorVO.getTrackingid()+" automatic redirect  AlphapayPaymentGateway payin form -- >>" + commonValidatorVO.getTrackingid() + " " + html);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error( commonValidatorVO.getTrackingid() +" ----> Exception while AutoRedirecting: ", e);
        }
        return html;
    }

}
