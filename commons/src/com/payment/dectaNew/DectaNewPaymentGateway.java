package com.payment.dectaNew;

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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by Rihen on 5/29/2019.
 */
public class DectaNewPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DectaNewPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.decta_new");

    public static final String GATEWAY_TYPE = "decta";

    public DectaNewPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public static void main(String[] args)
    {
        String secretKey = "dc4e2da19787608dd6a8d1a98948c03b1d190da67b0691adcf8a949bba7417bd";

        try
        {

            //Order Request
/*

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("referrer", "1102");
            orderRequest.put("number", "1102");
            orderRequest.put("success_redirect", "http://localhost:8081/transaction/DectaNewFrontEndServlet?trackingId=1102&status=success");
            orderRequest.put("failure_redirect", "http://localhost:8081/transaction/DectaNewFrontEndServlet?trackingId=1102&status=fail");
            orderRequest.put("currency", "EUR");
            orderRequest.put("skip_capture", "false");

            JSONArray jsonArray= new JSONArray();

            JSONObject orderProduct = new JSONObject();
            orderProduct.put("title","Apple Iphone 7 plus");
            orderProduct.put("price","1.00");

            jsonArray.put(orderProduct);

            JSONObject orderClient = new JSONObject();
            orderClient.put("email","<emiladdress>");
            orderClient.put("phone","9876543210");

            orderRequest.put("client",orderClient);
            orderRequest.put("products",jsonArray);

            System.out.println("orderRequest----" + orderRequest);
            String orderResponse = "";


            orderResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS"), orderRequest.toString(),secretKey);

            System.out.println("orderResponse -----" + orderResponse );
*/


            // Card Request FORM in sale
/*

            String cardRequest ="cardholder_name = Rihen Dedhia"+
                    "&card_number=4242424242424242"+
                    "&exp_month=2"+
                    "&exp_year=20"+
                    "&csc=111";

            System.out.println("cardRequest----" + cardRequest);

            String cardResponse = "";

            // Card Request has to be sent on the url from the order response
            cardResponse = DectaNewUtils.doPostFormHTTPSURLConnectionClient("https://transactions.decta.com/p/6d1751b0-5e4e-41ed-81f1-6bf25dbbe1e4/", cardRequest.toString());

            System.out.println("cardResponse-----" + cardResponse);

*/


            // Capture
/*

            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", "1.00");


            System.out.println("captureRequest----" + captureRequest);
            String captureResponse = "";


            captureResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS")+"688f40bd-a480-491b-9643-547f564e3a70"+"/capture/", captureRequest.toString(),secretKey);

            System.out.println("captureResponse-----" + captureResponse);

*/


            // REFUND
/*
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("total", "1.00");


            System.out.println("refundRequest----" + refundRequest);
            String refundResponse = "";

            refundResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS")+"621be2eb-dad4-40d7-9125-ca41bf045ef4"+"/refund/", refundRequest.toString(),secretKey);

            System.out.println("url ---- "+RB.getString("OPERATIONS")+"621be2eb-dad4-40d7-9125-ca41bf045ef4"+"/refund/");
            System.out.println("refundResponse-----" + refundResponse);
  */


            // Cancel
/*

            String cancelResponse = "";
            cancelResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS")+"bee54b25-6272-487f-b136-39ffa047a5e7"+"/cancel/", "",secretKey);

            System.out.println("cancelResponse ---- "+cancelResponse);
*/


        }
        catch (Exception e)
        {
            System.out.println("Exception ----" + e);
        }


    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        boolean isTest = gatewayAccount.isTest();

        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();

        try
        {
            //Order Request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("referrer", trackingID);
            orderRequest.put("number", trackingID);
            orderRequest.put("success_redirect", RB.getString("REDIRECT_URL") + trackingID + "&status=success");
            orderRequest.put("failure_redirect", RB.getString("REDIRECT_URL") + trackingID + "&status=fail");
            orderRequest.put("currency", commTransactionDetailsVO.getCurrency());
            orderRequest.put("skip_capture", "false");

            JSONArray jsonArray = new JSONArray();

            JSONObject orderProduct = new JSONObject();
            orderProduct.put("title", commTransactionDetailsVO.getOrderId());
            orderProduct.put("price", commTransactionDetailsVO.getAmount());
            orderProduct.put("description", commTransactionDetailsVO.getOrderDesc());

            jsonArray.put(orderProduct);

            JSONObject orderClient = new JSONObject();
            orderClient.put("email", commAddressDetailsVO.getEmail());
            orderClient.put("phone", commAddressDetailsVO.getPhone());

            orderRequest.put("client", orderClient);
            orderRequest.put("products", jsonArray);

            transactionLogger.error("sale orderRequest----for "+trackingID+ "is" + orderRequest);

            String orderResponse = "";
            if (isTest)
            {
                orderResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS"), orderRequest.toString(), secretKey);
            }
            else
            {
                orderResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS"), orderRequest.toString(), secretKey);
            }

            transactionLogger.error("sale orderResponse -----for "+trackingID+ "is" + orderResponse);

            String transactionId = "";
            String direct_post = "";
            String status = "";
            String arn = "";

            if (functions.isValueNull(orderResponse))
            {
                JSONObject jsonOrderResponse = new JSONObject(orderResponse);

                if (jsonOrderResponse != null)
                {
                    if (jsonOrderResponse.has("id"))
                    {
                        if (functions.isValueNull(jsonOrderResponse.getString("id")))
                        {
                            transactionId = jsonOrderResponse.getString("id");
                        }
                    }

                    if (jsonOrderResponse.has("direct_post"))
                    {
                        if (functions.isValueNull(jsonOrderResponse.getString("direct_post")))
                        {
                            direct_post = jsonOrderResponse.getString("direct_post");
                        }
                    }

                    if (jsonOrderResponse.has("transaction_details"))
                    {
                        JSONObject jsonTransactionDetail = jsonOrderResponse.getJSONObject("transaction_details");

                        if (jsonTransactionDetail != null)
                        {
                            if (functions.isValueNull(jsonTransactionDetail.getString("arn")))
                            {
                                arn = jsonTransactionDetail.getString("arn");
                            }
                        }
                    }

                    if (jsonOrderResponse.has("status"))
                    {
                        status = jsonOrderResponse.getString("status");
                        commResponseVO.setStatus("pending");
                        commResponseVO.setRemark("Transaction Pending");
                        commResponseVO.setDescription("Transaction Pending (" + status + ")");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }

                    if (jsonOrderResponse.has("message"))
                    {
                        if (functions.isValueNull(jsonOrderResponse.getString("message")))
                        {
                            commResponseVO.setErrorName(jsonOrderResponse.getString("message"));
                        }

                        if (functions.isValueNull(jsonOrderResponse.getString("code")))
                        {
                            commResponseVO.setErrorCode(jsonOrderResponse.getString("code"));
                        }

                        commResponseVO.setStatus("Failed");
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setTransactionStatus("failed");
                    }
                }
            }

            transactionLogger.error("transaction id ===" + transactionId);

            commResponseVO.setRedirectUrl(direct_post);
            commResponseVO.setArn(arn);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setTransactionType(PZProcessType.SALE.toString());

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----", e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        boolean isTest = gatewayAccount.isTest();

        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();

        try
        {
            //Order Request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("referrer", trackingID);
            orderRequest.put("number", trackingID);
            orderRequest.put("success_redirect", RB.getString("REDIRECT_URL") + trackingID + "&status=success");
            orderRequest.put("failure_redirect", RB.getString("REDIRECT_URL") + trackingID + "&status=fail");
            orderRequest.put("currency", commTransactionDetailsVO.getCurrency());
            orderRequest.put("skip_capture", "true");

            JSONArray jsonArray = new JSONArray();

            JSONObject orderProduct = new JSONObject();
            orderProduct.put("title", commTransactionDetailsVO.getOrderId());
            orderProduct.put("price", commTransactionDetailsVO.getAmount());
            orderProduct.put("description", commTransactionDetailsVO.getOrderDesc());

            jsonArray.put(orderProduct);

            JSONObject orderClient = new JSONObject();
            orderClient.put("email", commAddressDetailsVO.getEmail());
            orderClient.put("phone", commAddressDetailsVO.getPhone());

            orderRequest.put("client", orderClient);
            orderRequest.put("products", jsonArray);

            transactionLogger.error("auth orderRequest----for "+trackingID+ "is" + orderRequest);

            String orderResponse = "";
            if (isTest)
            {
                orderResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS"), orderRequest.toString(), secretKey);
            }
            else
            {
                orderResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS"), orderRequest.toString(), secretKey);
            }

            transactionLogger.error("auth orderResponse -----for "+trackingID+ "is" + orderResponse);


            String transactionId = "";
            String direct_post = "";
            String status = "";
            String arn = "";

            if (functions.isValueNull(orderResponse))
            {
                JSONObject jsonOrderResponse = new JSONObject(orderResponse);

                if (jsonOrderResponse != null)
                {
                    if (jsonOrderResponse.has("id"))
                    {
                        if (functions.isValueNull(jsonOrderResponse.getString("id")))
                        {
                            transactionId = jsonOrderResponse.getString("id");
                        }
                    }

                    if (jsonOrderResponse.has("direct_post"))
                    {
                        if (functions.isValueNull(jsonOrderResponse.getString("direct_post")))
                        {
                            direct_post = jsonOrderResponse.getString("direct_post");
                        }
                    }

                    if (jsonOrderResponse.has("transaction_details"))
                    {
                        JSONObject jsonTransactionDetail = jsonOrderResponse.getJSONObject("transaction_details");
                        if (jsonTransactionDetail != null)
                        {
                            if (functions.isValueNull(jsonTransactionDetail.getString("arn")))
                            {
                                arn = jsonTransactionDetail.getString("arn");
                            }
                        }
                    }

                    if (jsonOrderResponse.has("status"))
                    {
                        status = jsonOrderResponse.getString("status");
                        commResponseVO.setStatus("Pending");
                        commResponseVO.setRemark("Transaction Pending");
                        commResponseVO.setDescription("Transaction Pending (" + status + ")");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }

                    if (jsonOrderResponse.has("message"))
                    {
                        if (functions.isValueNull(jsonOrderResponse.getString("message")))
                        {
                            commResponseVO.setErrorName(jsonOrderResponse.getString("message"));
                        }

                        if (functions.isValueNull(jsonOrderResponse.getString("code")))
                        {
                            commResponseVO.setErrorCode(jsonOrderResponse.getString("code"));
                        }

                        commResponseVO.setStatus("Failed");
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setTransactionStatus("failed");
                    }
                }
            }

            transactionLogger.error("transactionId ===" + transactionId);

            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
            commResponseVO.setRedirectUrl(direct_post);
            commResponseVO.setArn(arn);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----for "+trackingID+ "is", e);
        }


        return commResponseVO;
    }


    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("----- inside processCapture -----");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();

        try
        {
            String amount = commTransactionDetailsVO.getAmount();
            String orderID = commTransactionDetailsVO.getPreviousTransactionId();

            JSONObject captureRequest = new JSONObject();
            captureRequest.put("total", amount);


            transactionLogger.error("captureRequest----for "+trackingID+ "is" + captureRequest);
            String captureResponse = "";

            if (isTest)
            {
                captureResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderID + "/capture/", captureRequest.toString(), secretKey);
            }
            else
            {
                captureResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderID + "/capture/", captureRequest.toString(), secretKey);
            }

            transactionLogger.error("captureResponse-----for "+trackingID+ "is" + captureResponse);

            String transactionId = "";
            String currency = "";
            String status = "";
            String arn = "";

            if (functions.isValueNull(captureResponse))
            {
                JSONObject jsonCaptureResponse = new JSONObject(captureResponse);
                if (jsonCaptureResponse != null)
                {
                    if (jsonCaptureResponse.has("id"))
                    {
                        if (functions.isValueNull(jsonCaptureResponse.getString("id")))
                        {
                            transactionId = jsonCaptureResponse.getString("id");
                        }
                    }

                    if (jsonCaptureResponse.has("transaction_details"))
                    {
                        JSONObject jsonTransactionDetail = jsonCaptureResponse.getJSONObject("transaction_details");
                        if (jsonTransactionDetail != null)
                        {
                            if (functions.isValueNull(jsonTransactionDetail.getString("arn")))
                            {
                                arn = jsonTransactionDetail.getString("arn");
                            }
                        }
                    }

                    if (jsonCaptureResponse.has("currency"))
                    {
                        currency = jsonCaptureResponse.getString("currency");
                    }

                    if (jsonCaptureResponse.has("status"))
                    {
                        status = jsonCaptureResponse.getString("status");
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setTransactionStatus("failed");
                    }

                }
            }

            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            commResponseVO.setArn(arn);
            commResponseVO.setAmount(amount);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Currency(currency);
            commResponseVO.setTmpl_Amount(amount);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----for "+trackingID+ "is", e);
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("----- inside processVoid -----");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();

        try
        {

            String orderID = commTransactionDetailsVO.getPreviousTransactionId();
            String cancelResponse = "";

            if (isTest)
            {
                cancelResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderID + "/cancel/", "", secretKey);
            }
            else
            {
                cancelResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderID + "/cancel/", "", secretKey);
            }

            transactionLogger.error("cancelResponse-----for "+trackingID+ "is" + cancelResponse);

            String transactionId = "";
            String currency = "";
            String amount = "";
            String status = "";
            String arn = "";

            if (functions.isValueNull(cancelResponse))
            {
                JSONObject jsonCancelResponse = new JSONObject(cancelResponse);

                if (jsonCancelResponse != null)
                {
                    if (jsonCancelResponse.has("id"))
                    {
                        if (functions.isValueNull(jsonCancelResponse.getString("id")))
                        {
                            transactionId = jsonCancelResponse.getString("id");
                        }
                    }

                    if (jsonCancelResponse.has("currency"))
                    {
                        currency = jsonCancelResponse.getString("currency");
                    }

                    if (jsonCancelResponse.has("total"))
                    {
                        amount = jsonCancelResponse.getString("total");
                    }

                    if (jsonCancelResponse.has("transaction_details"))
                    {
                        JSONObject jsonTransactionDetail = jsonCancelResponse.getJSONObject("transaction_details");

                        if (jsonTransactionDetail != null)
                        {
                            if (functions.isValueNull(jsonTransactionDetail.getString("arn")))
                            {
                                arn = jsonTransactionDetail.getString("arn");
                            }
                        }
                    }

                    if (jsonCancelResponse.has("status"))
                    {
                        status = jsonCancelResponse.getString("status");
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setTransactionStatus("failed");
                    }

                }
            }

            commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setArn(arn);
            commResponseVO.setAmount(amount);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Currency(currency);
            commResponseVO.setTmpl_Amount(amount);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----for "+trackingID+ "is" + e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("----- inside processRefund -----");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();

        try
        {
            String amount = commTransactionDetailsVO.getAmount();
            String orderID = commTransactionDetailsVO.getPreviousTransactionId();

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount);


            transactionLogger.error("refundRequest----for "+trackingID+ "is" + refundRequest);
            String refundResponse = "";

            if (isTest)
            {
                refundResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderID + "/refund/", refundRequest.toString(), secretKey);
            }
            else
            {
                refundResponse = DectaNewUtils.doPostHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderID + "/refund/", refundRequest.toString(), secretKey);
            }

            transactionLogger.error("refundResponse-----for "+trackingID+ "is" + refundResponse);

            String transactionId = "";
            String currency = "";
            String status = "";
            String arn = "";

            if (functions.isValueNull(refundResponse))
            {
                JSONObject jsonRefundResponse = new JSONObject(refundResponse);

                if (jsonRefundResponse != null)
                {
                    if (jsonRefundResponse.has("id"))
                    {
                        if (functions.isValueNull(jsonRefundResponse.getString("id")))
                        {
                            transactionId = jsonRefundResponse.getString("id");
                        }
                    }

                    if (jsonRefundResponse.has("transaction_details"))
                    {
                        JSONObject jsonTransactionDetail = jsonRefundResponse.getJSONObject("transaction_details");
                        if (jsonTransactionDetail != null)
                        {
                            if (functions.isValueNull(jsonTransactionDetail.getString("arn")))
                            {
                                arn = jsonTransactionDetail.getString("arn");
                            }
                        }
                    }

                    if (jsonRefundResponse.has("currency"))
                    {
                        currency = jsonRefundResponse.getString("currency");
                    }

                    if (jsonRefundResponse.has("status"))
                    {
                        status = jsonRefundResponse.getString("status");
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setTransactionStatus("failed");
                    }

                }
            }

            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setArn(arn);
            commResponseVO.setAmount(amount);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Currency(currency);
            commResponseVO.setTmpl_Amount(amount);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----for "+trackingID+ "is", e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("----- inside processInquiry -----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String trackingId = commTransactionDetailsVO.getOrderId();
        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();
        String publicKey = gatewayAccount.getMerchantId();
        transactionLogger.error("Decta InquiryMid Details For " + trackingId + "::::::::::secretKey::::::" + secretKey + " Public Key ::::::::" + publicKey);


        try
        {
            String orderId = commTransactionDetailsVO.getPreviousTransactionId();

            String inquiryResponse = "";

            if (isTest)
            {
                transactionLogger.error("Decta Inquiry Test URL for  " + trackingId + "-----" + RB.getString("OPERATIONS") + orderId + "/");
                inquiryResponse = DectaNewUtils.doGetHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderId + "/", secretKey);
            }
            else
            {
                transactionLogger.error("Decta Inquiry Live URL" + trackingId + "-----" + RB.getString("OPERATIONS") + orderId + "/");
                inquiryResponse = DectaNewUtils.doGetHTTPSURLConnectionClient(RB.getString("OPERATIONS") + orderId + "/", secretKey);
            }

            transactionLogger.error("inquiryResponse for " + trackingId + " -----" + inquiryResponse);

            String transactionId = "";
            String currency = "";
            String amount = "";
            String status = "";
            String arn = "";
            String status_3D = "";
            String description = "";
            String remark = "";
            String error_code = "";
            String mpi_code = "";

            if (functions.isValueNull(inquiryResponse))
            {
                JSONObject jsonInquiryResponse = new JSONObject(inquiryResponse);

                if (jsonInquiryResponse != null)
                {
                    if (jsonInquiryResponse.has("id"))
                    {
                        if (functions.isValueNull(jsonInquiryResponse.getString("id")))
                        {
                            transactionId = jsonInquiryResponse.getString("id");
                        }
                    }

                    if (jsonInquiryResponse.has("transaction_details"))
                    {
                        JSONObject jsonTransactionDetail = jsonInquiryResponse.getJSONObject("transaction_details");
                        if (jsonTransactionDetail != null)
                        {
                            if (functions.isValueNull(jsonTransactionDetail.getString("arn")))
                            {
                                arn = jsonTransactionDetail.getString("arn");
                            }

                            if (jsonTransactionDetail.has("status_3d_secure"))
                            {
                                if (functions.isValueNull(jsonTransactionDetail.getString("status_3d_secure")))
                                {
                                    status_3D = jsonTransactionDetail.getString("status_3d_secure");
                                    commResponseVO.setAuthCode(status_3D);
                                }
                            }

                            if (jsonTransactionDetail.has("three_d_secure_status"))
                            {
                                JSONObject jsonThreeDDetail = jsonTransactionDetail.getJSONObject("three_d_secure_status");
                                if (functions.isValueNull(jsonThreeDDetail.getString("description")))
                                {
                                    description = (jsonThreeDDetail.getString("description"));
                                }
                                if (functions.isValueNull(jsonThreeDDetail.getString("mpi_status_code")))
                                {
                                    commResponseVO.setResponseHashInfo(jsonThreeDDetail.getString("mpi_status_code"));
                                }
                            }

                            if (jsonTransactionDetail.has("processing_status"))
                            {
                                JSONObject jsonProcessingStatus = jsonTransactionDetail.getJSONObject("processing_status");
                                if (functions.isValueNull(jsonProcessingStatus.getString("description")))
                                {
                                    remark = (jsonProcessingStatus.getString("description"));
                                }

                                if (functions.isValueNull(jsonProcessingStatus.getString("code")))
                                {
                                    error_code = (jsonProcessingStatus.getString("code"));
                                    commResponseVO.setErrorCode(error_code);
                                }
                            }

                        }
                    }

                    if (jsonInquiryResponse.has("currency"))
                    {
                        currency = jsonInquiryResponse.getString("currency");
                    }

                    if (jsonInquiryResponse.has("total"))
                    {
                        amount = jsonInquiryResponse.getString("total");
                    }

                    if (jsonInquiryResponse.has("status"))
                    {
                        status = jsonInquiryResponse.getString("status");
                        transactionLogger.error("Inquiry Status-----" + status);
                        if ("failed".equalsIgnoreCase(status))
                        {
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("Transaction Failed");
                            if (functions.isValueNull(remark))
                            {
                                commResponseVO.setRemark(remark);
                            }
                            else if (functions.isValueNull(description))
                            {
                                commResponseVO.setRemark(description);
                            }
                            else
                            {
                                commResponseVO.setRemark("Transaction Failed");
                            }
                            commResponseVO.setTransactionStatus(status);
                        }
                        else if ("paid".equalsIgnoreCase(status) || "hold".equalsIgnoreCase(status))
                        {
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescription("Transaction Successful");
                            if (functions.isValueNull(description))
                            {
                                commResponseVO.setRemark(description);
                            }
                            else
                            {
                                commResponseVO.setRemark("Transaction Successful");
                            }
                            commResponseVO.setTransactionStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else
                        {
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("Transaction Failed");
                            if (functions.isValueNull(remark))
                            {
                                commResponseVO.setRemark(remark);
                            }
                            else if (functions.isValueNull(description))
                            {
                                commResponseVO.setRemark(description);
                            }
                            else
                            {
                                commResponseVO.setRemark("Transaction Failed (" + status + ")");
                            }
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                    }
                }
            }

            commResponseVO.setTransactionType(DectaNewUtils.getTransactionType(status));
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setArn(arn);
            commResponseVO.setAmount(amount);
            commResponseVO.setCurrency(currency);
            commResponseVO.setMerchantId(publicKey);

            transactionLogger.error("Description = " + commResponseVO.getRemark());
            transactionLogger.error("Status 3D =" + commResponseVO.getAuthCode());
            transactionLogger.error("MPI Code = " + commResponseVO.getResponseHashInfo());

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----for "+trackingId+ "is", e);
        }

        return commResponseVO;
    }

}