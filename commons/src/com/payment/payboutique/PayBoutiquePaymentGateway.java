package com.payment.payboutique;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Created by Balaji on 15-Oct-19.
 */
public class PayBoutiquePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "boutique";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payBoutique");
    private static TransactionLogger transactionLogger = new TransactionLogger(PayBoutiquePaymentGateway.class.getName());
    private static Logger log = new Logger(PayBoutiquePaymentGateway.class.getName());
    PayBoutiqueUtils payBoutiqueUtils = new PayBoutiqueUtils();
    Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

    public PayBoutiquePaymentGateway(String accountId){this.accountId = accountId;}

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::Entering into processSale:::::");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String userId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();//"o7cNyVE@8@_3123";
        String merchantId = gatewayAccount.getMerchantId();//"636";
        String paymentMethod = "CreditCard";
        String type = "GetInvoice";
        String buyerCurrency = transDetailsVO.getCurrency();
        transactionLogger.error("buyerCurrency---------------"+buyerCurrency);

        String termURL ="";
        String notificationUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termURL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termURL);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termURL = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termURL);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }
        String Url=RB.getString("URL");
        transactionLogger.error("test url ----" +  Url);
        String isLive = "false";
        String amountMerchantCurrency = transDetailsVO.getAmount();
        String merchantCurrency = buyerCurrency;
        String siteAddress = "www.pz.com";
        String accountId="";
        if(isTest)
        {
//            accountId = "9151245661";  //always returns success
//          accountId = "9151245661r"; //always returns  fail
            accountId = "512";
//          accountId = "9151245662"; //always returns invoice generation error
            isLive = "false";
        }
        else
        {
            accountId = "512";
            isLive = "true";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'UTC'HHmmss+0000");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = simpleDateFormat.format(new Date());
        String expirationTime = simpleDateFormat.format(new Date().getTime() + 1 * 60 * 1000);

        String orderId = trackingID;


       // String successURL = termURL+ trackingID + "?status=Success";

        String successURL = termURL+ trackingID + "&amp;status=Success";
        String failureURL = termURL+ trackingID + "&amp;status=fail";
        notificationUrl = notificationUrl+ trackingID;

        try
        {
            String signature = payBoutiqueUtils.getSignature(userId, password, time);

            String saleRequest ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
                    "<Message version=\"0.5\">" +
                            "<Header>" +
                            "<Identity>" +
                                "<UserID>" + userId + "</UserID>" +
                            "<Signature>" + signature + "</Signature>" +
                            "</Identity>" +
                            "<Time>" + time + "</Time>" +
                            "</Header>" +
                            "<Body type=\"" + type + "\" live=\"" + isLive + "\">" +
                            "<Order paymentMethod=\"" + paymentMethod + "\" buyerCurrency=\"" + buyerCurrency + "\">" +
                            "<MerchantID>" + merchantId + "</MerchantID>" +
                            "<OrderID>" + orderId + "</OrderID>" +
                            "<AmountMerchantCurrency>" + amountMerchantCurrency + "</AmountMerchantCurrency>" +
                            "<MerchantCurrency>" + merchantCurrency + "</MerchantCurrency>" +
//                            "<ExpirationTime>" + expirationTime + "</ExpirationTime>" +
                            "<SiteAddress>" + siteAddress + "</SiteAddress>" +
                            "<SuccessURL>" + successURL + "</SuccessURL>" +
                            "<FailureURL>" + failureURL + "</FailureURL>" +
                            "<PostbackURL>" + notificationUrl + "</PostbackURL>" +
                            "<Buyer>" +
//                            "<FirstName>" + firstName + "</FirstName>" +
//                            "<LastName>" + lastName + "</LastName>" +
                            "<AccountID>" + accountId + "</AccountID>" +
                            "</Buyer>" +
                            "</Order>" +
                            "</Body>" +
                            "</Message>";

            transactionLogger.error("saleRequest----- "+ saleRequest);
            String saleResponse = payBoutiqueUtils.doPostHTTPSURLConnectionClient(Url, saleRequest, "Basic", signature);
            transactionLogger.error("saleResponse-----"+ saleResponse);

            Map<String, String> responseMap = payBoutiqueUtils.readSoapResponse(saleResponse,"sale");

            if (responseMap != null)
            {
                String transactionType_fromResponse = responseMap.get("transaction_type");
                String userID_response = responseMap.get("UserID");
                String merchantID_response = responseMap.get("MerchantID");
                String orderID_response = responseMap.get("OrderID");
                String amountMerchantCurrency_response = responseMap.get("AmountMerchantCurrency");
                String merchantCurrency_response = responseMap.get("MerchantCurrency");
                String expirationTime_response = responseMap.get("ExpirationTime");
                String label_response = responseMap.get("Label");
                String amountBuyerCurrency_response = responseMap.get("AmountBuyerCurrency");
                String buyerCurrency_response = responseMap.get("BuyerCurrency");
                String redirectURL_response = responseMap.get("RedirectURL");
                String iframeURL_response = responseMap.get("IframeURL");
                String status_response = responseMap.get("Status");
                String referenceID_response = responseMap.get("ReferenceID");
                String description_response = responseMap.get("Description");
                String invoiceSplitNumber_response = responseMap.get("InvoiceSplitNumber");

                String errorId = responseMap.get("ErrorID");
                String errorMessage = responseMap.get("ErrorMessage");
                String orderId_error = responseMap.get("OrderId_error");

                transactionLogger.error("iframeURL_response----"+ESAPI.encoder().decodeFromURL(iframeURL_response).trim());


                if(status_response.equals("created"))
                {
                    transactionLogger.error("inside status_response created");
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionId(referenceID_response);
                    commResponseVO.setDescription(description_response);
                    transactionLogger.error("redirectURL_response---------------"+redirectURL_response);
                    commResponseVO.setRedirectUrl(ESAPI.encoder().decodeFromURL(redirectURL_response));
                    transactionLogger.error("redirectURL_response url decoded----"+ESAPI.encoder().decodeFromURL(redirectURL_response));
                }
                else
                {
                    transactionLogger.error("inside failed");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(errorMessage);
                    commResponseVO.setDescription("SYS:3D Authentication Pending " + errorMessage);
                    commResponseVO.setErrorCode(errorId);
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS:Transaction Declined");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return  commResponseVO;
    }

    public GenericResponseVO processInquiry( GenericRequestVO requestVO)
    {
        transactionLogger.error(":::::Entering into processQuery:::::");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'UTC'HHmmss+0000");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String time = simpleDateFormat.format(new Date());
        String signature = payBoutiqueUtils.getSignature(userId, password, time);
        String type = "getStatus";
        String orderId = commTransactionDetailsVO.getPreviousTransactionId();
        String trackingId = commTransactionDetailsVO.getOrderId();
//        String testURL = "https://merchant.payb.lv/xml_service";
        transactionLogger.error("TransactionId --- " + orderId);
        transactionLogger.error("trackingId --- " + trackingId);
        String URL = RB.getString("URL");
        try
        {
            String requestInquiry =
                    "<Message version=\"0.5\">" +
                            "<Header>" +
                            "<Identity>" +
                            "<UserID>" + userId + "</UserID>" +
                            "<Signature>" + signature + "</Signature>" +
                            "</Identity>" +
                            "<Time>" + time + "</Time>" +
                            "</Header>" +
                            "<Body type=\"" + type + "\">" +
                            "<Order>" +
                            "<OrderID>" + trackingId + "</OrderID>" +
                            "</Order>" +
                            "</Body>" +
                            "</Message>";

            transactionLogger.error("requestInquiry --- " + requestInquiry);
            String response = payBoutiqueUtils.doPostHTTPSURLConnectionClient(URL, requestInquiry, "Basic",signature);
            transactionLogger.error("responseInquiry --- " + response);
            if(functions.isValueNull(response))
            {
                Map<String, String> responseMap = payBoutiqueUtils.readSoapResponse(response, "inquiry");
                if (responseMap != null)
                {
                    transactionLogger.error("response map --- " + responseMap);
                    String merchantID = responseMap.get("MerchantID");
                    String orderID = responseMap.get("OrderID");
                    String merchantReference = responseMap.get("MerchantReference");
                    String paymentMethod = responseMap.get("PaymentMethod");
                    String amountMerchantCurrency = responseMap.get("AmountMerchantCurrency");
                    String totalOrderAmountMerchantCurrency = responseMap.get("TotalOrderAmountMerchantCurrency");
                    String merchantCurrency = responseMap.get("MerchantCurrency");
                    String label = responseMap.get("Label");
                    String amountBuyerCurrency = responseMap.get("AmountBuyerCurrency");
                    String totalOrderAmountBuyerCurrency = responseMap.get("TotalOrderAmountBuyerCurrency");
                    String buyerCurrency = responseMap.get("BuyerCurrency");
                    String status = responseMap.get("Status");
                    String referenceID = responseMap.get("ReferenceID");
                    String lastUpdate = responseMap.get("LastUpdate");
                    String creationDate = responseMap.get("CreationDate");
                    String captureDate = responseMap.get("CaptureDate");
                    String settlementDate = responseMap.get("SettlementDate");
                    String settlementAmount = responseMap.get("SettlementAmount");
                    String totalOrderSettlementAmount = responseMap.get("TotalOrderSettlementAmount");
                    String grossSettlementAmount = responseMap.get("GrossSettlementAmount");
                    String totalOrderGrossSettlementAmount = responseMap.get("TotalOrderGrossSettlementAmount");
                    String settlementCurrency = responseMap.get("SettlementCurrency");
                    String siteAddress = responseMap.get("SiteAddress");
                    String live = responseMap.get("Live");
                    String description = responseMap.get("Description");
                    String cardMask = responseMap.get("CardMask");



                    commResponseVO.setAmount(amountMerchantCurrency);

                    commResponseVO.setStatus(status);
                  //  commResponseVO.setResponseTime(timestamp);
                    commResponseVO.setMerchantId(merchantID);
                    commResponseVO.setTransactionId(referenceID);
                    commResponseVO.setAuthCode("-");
                    commResponseVO.setBankTransactionDate(captureDate);
                    commResponseVO.setCurrency(merchantCurrency);
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    if(status.equalsIgnoreCase("created"))
                    {
                        commResponseVO.setRemark(status+" Transaction");
                        commResponseVO.setDescription("SYS:created");
                        commResponseVO.setTransactionStatus("fail");
                    }
                    if(status.equalsIgnoreCase("captured"))
                    {
                        commResponseVO.setRemark(status+" Transaction");
                        commResponseVO.setDescription("SYS:captured");
                        commResponseVO.setTransactionStatus("success");
                    }

                }
                else
                {
                    //no response
                    //fail block
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS:Transaction Declined");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return  commResponseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("inside processAutoRedirect in PayBoutiquePaymentGateway");
        String html="";
        PaymentManager paymentManager = new PaymentManager();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = payBoutiqueUtils.getCommRequestFromUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            transactionLogger.debug("process Sale finished");

            transactionLogger.debug("status---------------"+transRespDetails.getStatus());

            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                html = payBoutiqueUtils.getRedirectForm(commonValidatorVO.getTrackingid(),transRespDetails);
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                transactionLogger.debug("html---------------"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in OneRoadPaymentGateway ---",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("error", e);
        }
        return html;
    }
}
