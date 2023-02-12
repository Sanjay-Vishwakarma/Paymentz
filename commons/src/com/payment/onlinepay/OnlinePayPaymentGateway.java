package com.payment.onlinepay;

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
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class OnlinePayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OnlinePayPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.onlinepay");

    public static final String GATEWAY_TYPE = "onlinepay";

    public OnlinePayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays() { return null; }

    public static void main(String[] args){

        String url ="https://sandbox.cardpay.com/api/payments";

        String authUrl = "https://sandbox.cardpay.com/api/auth/token";

        String refundUrl = "https://sandbox.cardpay.com/api/refunds/";


        try
        {
           /* String authRequest="" +
                    "grant_type=password"+
                    "&terminal_code=19425"+
                    "&password=cQ352G6mvfSC";

            String authResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient2(authUrl,authRequest);

            System.out.println("auth request = "+authRequest);
            System.out.println("auth response === "+authResponse);

            JSONObject jsonAuthResponse = new JSONObject(authResponse);

            String authToken = jsonAuthResponse.getString("access_token");

            Date date = new Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sd.setTimeZone(TimeZone.getTimeZone("UTC"));
            String req_time = sd.format(date);

            JSONObject sale = new JSONObject();

            JSONObject request = new JSONObject();
            request.put("id", "18");
            request.put("time",req_time);

            JSONObject merchant_order = new JSONObject();
            merchant_order.put("id","1018");
            merchant_order.put("description","18 buy");

            JSONObject customer_order = new JSONObject();
            customer_order.put("locale","ja");

            JSONObject redirect = new JSONObject();
            redirect.put("success_url","https://staging.<hostname>.com/transaction/OnlinePayFrontEndServlet?status=success&trackingId=");
            redirect.put("decline_url","https://staging.<hostname>.com/transaction/OnlinePayFrontEndServlet?status=fail&trackingId=");
            redirect.put("cancel_url","https://staging.<hostname>.com/transaction/OnlinePayFrontEndServlet?status=cancel&trackingId=");

            JSONObject payment_data = new JSONObject();
            payment_data.put("currency","JPY");
            payment_data.put("amount",1);

            sale.put("request",request);
            sale.put("merchant_order",merchant_order);
            sale.put("payment_method","ONLINEPAY");
            sale.put("payment_data",payment_data);
            sale.put("customer",customer_order);
            sale.put("return_urls",redirect);


            System.out.println("sale request = "+sale);*/


            /*String saleResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient(url,sale.toString(),authToken);

            System.out.println("sale response ====="+saleResponse);*/


            /*********************** INQUIRY *****************************/

//            String response = OnlinePayUtils.doGetHTTPSURLConnectionClient("https://sandbox.cardpay.com/api/payments/2143431",authToken);
//
//            System.out.println("inquiry resposne = "+response);

        }
        catch(Exception e){
            System.out.println("exception ---"+e);
        }


    }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String merchantID = gatewayAccount.getMerchantId();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        try {

            Date date = new Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sd.setTimeZone(TimeZone.getTimeZone("UTC"));

            String authRequest="" +
                    "grant_type=password"+
                    "&terminal_code="+merchantID+
                    "&password="+password;

            transactionLogger.error("auth token request = "+authRequest);

            String authResponse = "";

            if(isTest)
            {
                authResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient2(RB.getString("AUTH_TEST"), authRequest);
            }
            else
            {
                authResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient2(RB.getString("AUTH_LIVE"), authRequest);
            }

            transactionLogger.error("auth token response === "+authResponse);

            String authToken = "";
            if(functions.isValueNull(authResponse))
            {
                JSONObject jsonAuthResponse = new JSONObject(authResponse);

                if (jsonAuthResponse != null)
                {
                    if(jsonAuthResponse.has("access_token"))
                    {
                        authToken = jsonAuthResponse.getString("access_token");
                    }
                }
            }

            String req_time = sd.format(date);

            JSONObject sale = new JSONObject();

            JSONObject request = new JSONObject();
            request.put("id", trackingID);
            request.put("time",req_time);

            JSONObject merchant_order = new JSONObject();
            merchant_order.put("id",trackingID);
            merchant_order.put("description",commTransactionDetailsVO.getOrderId());

            JSONObject customer_order = new JSONObject();
            customer_order.put("locale","ja");

            JSONObject redirect = new JSONObject();
            redirect.put("success_url",RB.getString("REDIRECT_URL")+"?status=success&trackingId="+trackingID);
            redirect.put("decline_url",RB.getString("REDIRECT_URL")+"?status=fail&trackingId="+trackingID);
            redirect.put("cancel_url",RB.getString("REDIRECT_URL")+"?status=cancel&trackingId="+trackingID);
            redirect.put("inprocess_url",RB.getString("REDIRECT_URL")+"?status=pending&trackingId="+trackingID);

            JSONObject payment_data = new JSONObject();
            payment_data.put("currency",commTransactionDetailsVO.getCurrency());
            payment_data.put("amount",commTransactionDetailsVO.getAmount());

            sale.put("request",request);
            sale.put("merchant_order",merchant_order);
            sale.put("payment_method","ONLINEPAY");
            sale.put("payment_data",payment_data);
            sale.put("customer",customer_order);
            sale.put("return_urls",redirect);

            transactionLogger.error("sale request = "+sale);

            String saleResponse = "";

            if(isTest)
            {
                saleResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYMENT_TEST"), sale.toString(), authToken);
            }
            else
            {
                saleResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYMENT_LIVE"), sale.toString(), authToken);
            }

            transactionLogger.error("sale response ====="+saleResponse);

            String redirectUrl = "";
            String errorName = "";
            String errorCode = "";

            if(functions.isValueNull(saleResponse))
            {
                JSONObject jsonSaleResponse = new JSONObject(saleResponse);

                if (jsonSaleResponse.has("redirect_url"))
                {
                    redirectUrl = jsonSaleResponse.getString("redirect_url");

                    commResponseVO.setRedirectUrl(redirectUrl);
                    commResponseVO.setStatus("Pending");
                }

                if (jsonSaleResponse.has("message"))
                {
                    errorCode = jsonSaleResponse.getString("message");
                    errorName = jsonSaleResponse.getString("name");

                    commResponseVO.setStatus("Failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Transaction Failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setErrorName(errorName);
                    commResponseVO.setErrorCode(errorCode);
                }
            }


            commResponseVO.setTransactionType(PZProcessType.SALE.toString());

        }
        catch (Exception e) {
            transactionLogger.error("Exception ----",e);
        }


        return commResponseVO;

    }


    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String merchantID = gatewayAccount.getMerchantId();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        try {

            Date date = new Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sd.setTimeZone(TimeZone.getTimeZone("UTC"));

            String authRequest="" +
                    "grant_type=password"+
                    "&terminal_code="+merchantID+
                    "&password="+password;

            transactionLogger.error("auth token request = "+authRequest);

            String authResponse = "";

            if(isTest)
            {
                authResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient2(RB.getString("AUTH_TEST"), authRequest);
            }
            else
            {
                authResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient2(RB.getString("AUTH_LIVE"), authRequest);
            }

            transactionLogger.error("auth token response === "+authResponse);

            String authToken = "";
            if(functions.isValueNull(authResponse))
            {
                JSONObject jsonAuthResponse = new JSONObject(authResponse);

                if (jsonAuthResponse != null)
                {
                    if(jsonAuthResponse.has("access_token"))
                    {
                        authToken = jsonAuthResponse.getString("access_token");
                    }
                }
            }

            String req_time = sd.format(date);

            JSONObject inquiry = new JSONObject();

            JSONObject request = new JSONObject();
            request.put("id", "");
            request.put("time",req_time);

            JSONObject merchant_order = new JSONObject();
            merchant_order.put("id",commTransactionDetailsVO.getOrderId());
            merchant_order.put("description",commTransactionDetailsVO.getOrderDesc());


            JSONObject payment_data = new JSONObject();
            payment_data.put("currency",commTransactionDetailsVO.getCurrency());
            payment_data.put("amount",commTransactionDetailsVO.getAmount());

            inquiry.put("request",request);
            inquiry.put("merchant_order",merchant_order);
            inquiry.put("payment_method","ONLINEPAY");
            inquiry.put("payment_data",payment_data);


            transactionLogger.error("inquiry request = "+inquiry);

            String inquiryResponse = "";

            if(isTest)
            {
                inquiryResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYMENT_TEST"), inquiry.toString(), authToken);
            }
            else
            {
                inquiryResponse = OnlinePayUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYMENT_LIVE"), inquiry.toString(), authToken);
            }

            transactionLogger.error("inquiry response ====="+inquiryResponse);

            String redirectUrl = "";
            String errorName = "";
            String errorCode = "";

            if(functions.isValueNull(inquiryResponse))
            {
                JSONObject jsonSaleResponse = new JSONObject(inquiryResponse);

                if (jsonSaleResponse.has("redirect_url"))
                {
                    redirectUrl = jsonSaleResponse.getString("redirect_url");

                    commResponseVO.setRedirectUrl(redirectUrl);
                    commResponseVO.setStatus("Pending");
                    commResponseVO.setRemark("Transaction Pending");
                    commResponseVO.setDescription("Transaction Pending");
                    commResponseVO.setTransactionStatus("pending");
                }

                if (jsonSaleResponse.has("message"))
                {
                    errorCode = jsonSaleResponse.getString("message");
                    errorName = jsonSaleResponse.getString("name");

                    commResponseVO.setStatus("Failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Transaction Failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setErrorName(errorName);
                    commResponseVO.setErrorCode(errorCode);
                }
            }


            commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());

        }
        catch (Exception e) {
            transactionLogger.error("Exception ----",e);
        }


        return commResponseVO;

    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processAutoRedirect-----");
        String html="";
        PaymentManager paymentManager = new PaymentManager();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = OnlinePayUtils.getCommRequestFromUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            transactionLogger.error("status === "+transRespDetails.getStatus());
            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                html = OnlinePayUtils.getRedirectForm(transRespDetails);
                transactionLogger.error("Html in processAutoRedirect -------" + html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in OnlinePayPaymentGateway ---",e);
        }
        return html;
    }



}
