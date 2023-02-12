package com.payment.payneteasy;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Ecommpay.EcommpayUtils;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by Krishna on 28-Apr-21.
 */
public class PayneteasyGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "payneteasy";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payneteasy");

    private static TransactionLogger paynetEasyLogger = new TransactionLogger(PayneteasyGateway.class.getName());

    public PayneteasyGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        try
        {
            /*TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();

            System.out.println("test url ======= " + (RB.getString("TEST_URL")));

            String endpointid = "5179";
            String trackingId = "HYHTKJ7894654";
            String email = "test@gmail.com";
            String controlKey = "61E152FA-EC05-4CDC-91AF-C451D3A8C678";
            String amount = "100.00";
            String currency = "USD";
            String amountCent = "";

            if ("JPY".equalsIgnoreCase(currency))
                amountCent = PayneteasyUtils.getJPYAmount(amount);

            else if ("KWD".equalsIgnoreCase(currency))
                amountCent = PayneteasyUtils.getKWDSupportedAmount(amount);

            else
                amountCent = PayneteasyUtils.getCentAmount(amount);

            String control = PayneteasyUtils.hashMac(endpointid + trackingId + amountCent + email + controlKey);
            System.out.println("control ======= " + control);

            StringBuffer request = new StringBuffer("client_orderid=" + trackingId
                    + "&order_desc=" + "Test Order"
                    + "&card_printed_name=" + "Krishna Desai"
                    + "&first_name=" + "Krishna"
                    + "&last_name=" + "Desai"
                    + "&email=" + email
                    + "&address1=" + "11Jalan Lurah 6 Kg. Kempas Baru"
                    + "&country=" + "US"
                    + "&phone=" + "96841265"
                    + "&city=" + "Anaheim"
                    + "&state=" + "NC"
                    + "&zip_code=" + "12315"
                    + "&phone=" + "9845684125"
                    + "&ipaddress=" + "192.168.1.10"
                    + "&control=" + control
                    + "&redirect_url=" + "http://doc.payneteasy.com/doc/dummy.htm"
                    + "&amount=" + amount
                    + "&currency=" + currency
                    + "&credit_card_number=" + "3555555555555552"
                    + "&expire_month=" + "12"
                    + "&expire_year=" + "2021"
                    + "&cvv2=" + "321"
            );

            String response = "";
            System.out.println("\n request = " + request);*/
            Functions functions = new Functions();
            String errorMessage = "[92]";
            if (functions.isValueNull(errorMessage) && errorMessage.startsWith("[") && errorMessage.endsWith("]"))
            {
                errorMessage = errorMessage.substring(1, errorMessage.length() - 1);
                errorMessage = PayneteasyUtils.getErrorCodeHash(errorMessage);
            }

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String endpointid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String saleUrl = "";

        paynetEasyLogger.error("endpointid in processSale ===== " + endpointid);
        paynetEasyLogger.error("username in processSale  ===== " + username);
        paynetEasyLogger.error("password in processSale  ===== " + password);
        paynetEasyLogger.error("isTest in processSale  ===== " + isTest);


        String amount = transDetailsVO.getAmount();
        String currency = transDetailsVO.getCurrency();
        String amountCent = "";
        String control = "";
        String first_name = "";
        String last_name = "";
        String card_printed_name = "";
        String city = "";
        String address1 = "";
        String email = addressDetailsVO.getEmail();
        String phone = addressDetailsVO.getPhone();
        String ipAddress = addressDetailsVO.getCardHolderIpAddress();
        String cardNumber = cardDetailsVO.getCardNum();
        String expiryMonth = cardDetailsVO.getExpMonth();
        String expiryYear = cardDetailsVO.getExpYear();
        String cvv = cardDetailsVO.getcVV();
        String zipcode = addressDetailsVO.getZipCode();
        String country = addressDetailsVO.getCountry();
        String state = addressDetailsVO.getState();
        String notifyURL = "";
        String termUrl = "";

        paynetEasyLogger.error("country before in processSale  ===== " + country);
        paynetEasyLogger.error("state before in processSale  ===== " + state);

        if(functions.isValueNull(country) && country.length() == 3)
        {
            country = PayneteasyUtils.getCountryCodeHash(country);
        }

        if("AU".equals(country) || "CA".equals(country) || "US".equals(country))
        {
            state = PayneteasyUtils.getStateCodeHash(state.toUpperCase());
        }

        // state len2,3 then paas the requests
        if (!functions.isValueNull(state))
        {
            paynetEasyLogger.error("state not found in map in processSale  ===== ");
            state = addressDetailsVO.getState();
        }

        paynetEasyLogger.error("amount in processSale  ===== " + amount);
        paynetEasyLogger.error("currency in processSale  ===== " + currency);
        paynetEasyLogger.error("email in processSale  ===== " + email);
        paynetEasyLogger.error("country after in processSale  ===== " + country);
        paynetEasyLogger.error("state after in processSale  ===== " + state);
        paynetEasyLogger.error("phone in processSale  ===== " + phone);
        paynetEasyLogger.error("ipAddress in processSale  ===== " + ipAddress);
        paynetEasyLogger.error("cardNumber in processSale  ===== " + cardNumber);
        paynetEasyLogger.error("expiryMonth in processSale  ===== " + expiryMonth);
        paynetEasyLogger.error("expiryYear in processSale  ===== " + expiryYear);
        paynetEasyLogger.error("cvv in processSale  ===== " + cvv);
        paynetEasyLogger.error("zipcode in processSale  ===== " + zipcode);

        if (isTest)
        {
            saleUrl = RB.getString("TEST_SALE_URL") + endpointid;
        }
        else
        {
            saleUrl = RB.getString("LIVE_SALE_URL") + endpointid;
        }

        paynetEasyLogger.error("saleUrl in processSale  ==== " + saleUrl);

        termUrl = RB.getString("TERM_URL") + trackingID;
        paynetEasyLogger.error("TERM_URL in processSale  ----" + termUrl);

        notifyURL = RB.getString("NOTIFY_URL") + trackingID;
        paynetEasyLogger.error("notifyURL in processSale  ----" + notifyURL);

        amountCent=PayneteasyUtils.getCentAmount(amount);

        try
        {
            control = PayneteasyUtils.hashMac(endpointid + trackingID + amountCent + addressDetailsVO.getEmail() + password);
            if(functions.isValueNull(addressDetailsVO.getFirstname()))
                first_name = new String(addressDetailsVO.getFirstname().getBytes(),"UTF-8");
            if(functions.isValueNull(addressDetailsVO.getLastname()))
                last_name = new String(addressDetailsVO.getLastname().getBytes(),"UTF-8");
            card_printed_name = first_name + " " + last_name;
            if(functions.isValueNull(addressDetailsVO.getCity()))
                city = new String(addressDetailsVO.getCity().getBytes(),"UTF-8");
            if(functions.isValueNull(addressDetailsVO.getStreet()))
                address1 = new String(addressDetailsVO.getStreet().getBytes(),"UTF-8");
        }
        catch (Exception e)
        {
            paynetEasyLogger.error("Exception in encoding name ===== ", e);
        }

        if(!functions.isValueNull(country))
            country="JP";
        if(!functions.isValueNull(state))
            state="MH";
        if(!functions.isValueNull(city))
            city="Okayama";
        if(!functions.isValueNull(address1))
            address1="Maniwa";
        if(!functions.isValueNull(zipcode))
            zipcode="716-1401";
        if(!functions.isValueNull(phone))
            phone="9999999999";
        paynetEasyLogger.error("control in processSale ----" + control);
        paynetEasyLogger.error("first_name in processSale ----" + first_name);
        paynetEasyLogger.error("last_name in processSale ----" + last_name);
        paynetEasyLogger.error("card_printed_name in processSale ----" + card_printed_name);
        paynetEasyLogger.error("city in processSale ----" + city);
        paynetEasyLogger.error("address1 in processSale ----" + address1);
        paynetEasyLogger.error("phone----" + phone);



        StringBuffer request = new StringBuffer("client_orderid=" + trackingID
                +"&order_desc=" + trackingID
                +"&card_printed_name=" + card_printed_name
                +"&first_name=" + first_name
                +"&last_name=" + last_name
                +"&email=" + email
                +"&address1=" + address1
                +"&country=" + country
                +"&phone=" + addressDetailsVO.getPhone()
                +"&city=" + city
                +"&zip_code=" + zipcode
                +"&phone=" + phone
                +"&ipaddress=" + ipAddress
                +"&control=" + control
                +"&redirect_url=" + termUrl
                +"&amount=" + amount
                +"&currency=" + currency
                +"&credit_card_number=" + cardNumber
                +"&expire_month=" + expiryMonth
                +"&expire_year=" + expiryYear
                +"&cvv2=" + cvv
                +"&server_callback_url=" + notifyURL
                +"&state=" + state
        );

        StringBuffer requestLog = new StringBuffer("client_orderid=" + trackingID
                +"&order_desc=" + trackingID
                +"&card_printed_name=" + card_printed_name
                +"&first_name=" + first_name
                +"&last_name=" + last_name
                +"&email=" + email
                +"&address1=" + address1
                +"&country=" + country
                +"&phone=" + addressDetailsVO.getPhone()
                +"&city=" + city
                +"&zip_code=" + zipcode
                +"&phone=" + phone
                +"&ipaddress=" + ipAddress
                +"&control=" + control
                +"&redirect_url=" + termUrl
                +"&amount=" + amount
                +"&currency=" + currency
                +"&credit_card_number=" + functions.maskingPan(cardNumber)
                +"&expire_month=" + functions.maskingExpiry(expiryMonth)
                +"&expire_year=" + functions.maskingExpiry(expiryYear)
                +"&cvv2=" + functions.maskingNumber(cvv)
                +"&server_callback_url=" + notifyURL
                +"&state=" + state
        );

        paynetEasyLogger.error("PaynetEasy Sale Request ===== " + trackingID+ "----" + requestLog);

        String response = PayneteasyUtils.doPostHTTPSURLConnectionClient(saleUrl, request.toString());
        paynetEasyLogger.error("PaynetEasy Sale Response ===== " + trackingID+ "----" + response);

        Map<String, String> resultMap = PayneteasyUtils.getResponseMap(response);
        paynetEasyLogger.error("PaynetEasy Sale Response resultMap ===== " + trackingID+ "----"+  resultMap);

        String responseType = resultMap.get("type");
        String orderId = resultMap.get("paynet-order-id");
        String errorCode = resultMap.get("error-code");
        String errorMessage = resultMap.get("error-message");

        if(functions.isValueNull(errorMessage))
        {
            try
            {
                errorMessage= ESAPI.encoder().decodeFromURL(errorMessage);
            }
            catch (EncodingException e)
            {
                paynetEasyLogger.error("Exception in error message" , e);
            }
        }

        paynetEasyLogger.error("responseType in processSale +++++ " + responseType);
        paynetEasyLogger.error("orderId in processSale +++++ " + orderId);
        paynetEasyLogger.error("errorCode in processSale +++++ " + errorCode);
        paynetEasyLogger.error("errorMessage in processSale +++++ " + errorMessage);

        commRequestVO.getTransDetailsVO().setPreviousTransactionId(orderId);

        if(functions.isValueNull(responseType) && responseType.trim().equalsIgnoreCase("async-response"))
        {
            try
            {
                commResponseVO = (Comm3DResponseVO) processQuery(trackingID, commRequestVO);
                if("pending".equalsIgnoreCase(commResponseVO.getTransactionStatus()))
                {
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, commRequestVO);
                }
                if("pending".equalsIgnoreCase(commResponseVO.getTransactionStatus()))
                {
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, commRequestVO);
                }
            }
            catch (PZGenericConstraintViolationException e)
            {
                paynetEasyLogger.error("exception in processSale ===== " , e);
            }
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setRemark(errorMessage);
            commResponseVO.setDescription(errorMessage);
            commResponseVO.setErrorCode(errorCode);
        }

        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        paynetEasyLogger.error("Inside processQuery ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String mid = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String amount = commTransactionDetailsVO.getAmount();
        String currency = commTransactionDetailsVO.getCurrency();
        String endpointid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String control = "";
        String inquiryUrl = "";
        String orderId = commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String isDynamicDescriptor = "N";
        String billingDesc = "";

        paynetEasyLogger.error("mid in processQuery ===== " + mid);
        paynetEasyLogger.error("userName in processQuery ===== " + userName);
        paynetEasyLogger.error("password in processQuery ===== " + password);
        paynetEasyLogger.error("isTest in processQuery ===== " + isTest);
        paynetEasyLogger.error("amount in processQuery ===== " + amount);
        paynetEasyLogger.error("currency in processQuery ===== " + currency);
        paynetEasyLogger.error("endpointid in processQuery ===== " + endpointid);
        paynetEasyLogger.error("orderId in processQuery ===== " + orderId);

        try
        {
            control = PayneteasyUtils.hashMac(userName + trackingID + orderId + password);
        }
        catch (Exception e)
        {
            paynetEasyLogger.error("Error in creating control in processQuery ===== ", e);
        }

        if (isTest)
        {
            inquiryUrl = RB.getString("TEST_INQUIRY_URL") + endpointid;
        }
        else
        {
            inquiryUrl = RB.getString("LIVE_INQUIRY_URL") + endpointid;
        }

        paynetEasyLogger.error("control in processQuery ===== " + control);
        paynetEasyLogger.error("inquiryUrl in processQuery ===== " + inquiryUrl);

        StringBuffer request = new StringBuffer("login=" + userName
                + "&client_orderid=" + trackingID
                + "&orderid=" + orderId
                + "&control=" + control
                + "&amount=" + amount
        );

        paynetEasyLogger.error("PaynetEasy Inquiry Request ===== " + trackingID + "----" + request);

        String response = PayneteasyUtils.doPostHTTPSURLConnectionClient(inquiryUrl, request.toString());
        paynetEasyLogger.error("PaynetEasy Inquiry Response ===== " + trackingID + "----" + response);

        Map<String, String> resultMap = PayneteasyUtils.getResponseMap(response);
        paynetEasyLogger.error("PaynetEasy Inquiry Response ResultMap ===== " + trackingID + "----" + resultMap);

        String statusResponse = "";
        if (functions.isValueNull(resultMap.get("status")))
        {
            statusResponse = resultMap.get("status").trim();
        }
        paynetEasyLogger.error("statusResponse in processQuery ======= " + statusResponse);

        String errorMessage = resultMap.get("error-message");

        paynetEasyLogger.error("before encoding error ==== " + errorMessage);
        if (functions.isValueNull(errorMessage))
        {
            try
            {
                errorMessage = ESAPI.encoder().decodeFromURL(errorMessage);
                errorMessage = errorMessage.trim();
            }
            catch (EncodingException e)
            {
                paynetEasyLogger.error("Exception in error message", e);
            }
        }

        paynetEasyLogger.error("errorMessage in processQuery ======= " + errorMessage);

        String errorCode = resultMap.get("error-code");
        paynetEasyLogger.error("errorCode in processQuery ======= " + errorCode);

        String descriptor = resultMap.get("descriptor");
        paynetEasyLogger.error("descriptor in processQuery ======= " + descriptor);

        String transaction_Type = resultMap.get("transaction-type");
        paynetEasyLogger.error("transaction_Type in processQuery ======= " + transaction_Type);

        isDynamicDescriptor = gatewayAccount.getIsDynamicDescriptor();
        billingDesc = gatewayAccount.getDisplayName();

        paynetEasyLogger.error("isDynamicDescriptor in processQuery ======= " + isDynamicDescriptor);
        paynetEasyLogger.error("billingDesc in processQuery ======= " + billingDesc);

        String rrn = resultMap.get("processor-rrn");
        String approvalCode = resultMap.get("approval-code");

        paynetEasyLogger.error("rrn in processQuery ======= " + rrn);
        paynetEasyLogger.error("approvalCode in processQuery ======= " + approvalCode);


        String amount1 = resultMap.get("amount");
        String time = resultMap.get("paynet-processing-date");
        String currency1 = resultMap.get("currency");
        String merId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        if (functions.isValueNull(time))
        {
            try
            {
                time = ESAPI.encoder().decodeFromURL(time);
            }
            catch (EncodingException e)
            {
                paynetEasyLogger.error("Error in decoding time ===== ", e);
            }
        }


        paynetEasyLogger.error("Amount  ======= " + amount1);
        paynetEasyLogger.error("Time  ======= " + time);
        paynetEasyLogger.error("Currency1  ======= " + currency1);

        commResponseVO.setTransactionId(orderId);
        commResponseVO.setRrn(rrn);
        commResponseVO.setAuthCode(approvalCode);
        commResponseVO.setMerchantId(merId);
        commResponseVO.setAmount(amount1);
        commResponseVO.setBankTransactionDate(time);
        commResponseVO.setCurrency(currency1);

        if (functions.isValueNull(statusResponse))
        {


            if ("processing".equalsIgnoreCase(statusResponse))
            {
                paynetEasyLogger.error("inside Transaction processing ===== ");

                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Transaction is pending");
                commResponseVO.setDescription("Transaction is pending");
                commResponseVO.setErrorCode(errorCode);

                String verify3D = resultMap.get("verified-3d-status");
                String html = resultMap.get("html");
                String redirectTo = resultMap.get("redirect-to");

                paynetEasyLogger.error("verify3D in processQuery ===== " + verify3D);
                paynetEasyLogger.error("html in processQuery ===== " + html);
                paynetEasyLogger.error("redirectTo in processQuery ===== " + redirectTo);

                if (functions.isValueNull(verify3D) && verify3D.equalsIgnoreCase("AUTHENTICATED"))
                {
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setUrlFor3DRedirect(html);
                    commResponseVO.setRedirectUrl(redirectTo);
                }
            }


        else if("approved".equalsIgnoreCase(statusResponse) && "sale".equalsIgnoreCase(transaction_Type))
        {
            paynetEasyLogger.error("inside Transaction approved for sale in processQuery ===== ");

            commResponseVO.setStatus("Success");
            commResponseVO.setTransactionStatus("Success");
            commResponseVO.setRemark("Transaction Successful");
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setErrorCode(errorCode);

            if(functions.isValueNull(descriptor) && "Y".equalsIgnoreCase(isDynamicDescriptor))
            {
                commResponseVO.setDescriptor(descriptor);
            }
            else
            {
                commResponseVO.setDescriptor(billingDesc);
            }
            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
        }

        else if("approved".equalsIgnoreCase(statusResponse) && "reversal".equalsIgnoreCase(transaction_Type))
        {
            paynetEasyLogger.error("inside Transaction approved for reversal in processQuery ===== ");

            commResponseVO.setStatus("Success");
            commResponseVO.setTransactionStatus("Success");
            commResponseVO.setRemark("Transaction Successful");
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            if(functions.isValueNull(resultMap.get("total-reversal-amount")))
                commResponseVO.setAmount(String.format("%.2f", Double.parseDouble(resultMap.get("total-reversal-amount"))));
        }

        else if("approved".equalsIgnoreCase(statusResponse) && "preauth".equalsIgnoreCase(transaction_Type))
        {
            paynetEasyLogger.error("inside Transaction approved for preauth in processQuery ===== ");

            commResponseVO.setStatus("Success");
            commResponseVO.setTransactionStatus("Success");
            commResponseVO.setRemark("Transaction Successful");
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setDescriptor(resultMap.get("descriptor"));
            commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
        }

        else if("declined".equalsIgnoreCase(statusResponse) || "error".equalsIgnoreCase(statusResponse) || "filtered".equalsIgnoreCase(statusResponse))
        {
            paynetEasyLogger.error("inside else if of declined in processQuery ***** ");
            if(functions.isValueNull(errorMessage) && errorMessage.startsWith("[") && errorMessage.endsWith("]"))
            {
                String code=errorMessage.substring(1,errorMessage.length()-1);
                String message=PayneteasyUtils.getErrorCodeHash(code);
                if(functions.isValueNull(message))
                    errorMessage=message;
            }
            commResponseVO.setStatus("failed");
            commResponseVO.setTransactionStatus("failed");
            commResponseVO.setRemark(errorMessage);
            commResponseVO.setDescription(errorMessage);
            commResponseVO.setErrorCode(errorCode);
        }

            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Transaction is pending");
                commResponseVO.setDescription("Transaction is pending");
                commResponseVO.setErrorCode(errorCode);
            }

        }

        else
        {
            commResponseVO.setStatus("pending");
            commResponseVO.setTransactionStatus("pending");
            commResponseVO.setRemark("Transaction is pending");
            commResponseVO.setDescription(errorMessage);
            commResponseVO.setErrorCode(errorCode);
        }
        return commResponseVO;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        paynetEasyLogger.error("Inside processRefund =====");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        int mId = Integer.parseInt(gatewayAccount.getMerchantId());
        boolean isTest = gatewayAccount.isTest();
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String endpointid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String orderId = commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String refundURL = "";
        String amount = commTransactionDetailsVO.getAmount();
        String currency = commTransactionDetailsVO.getCurrency();
        String amountCent = "";
        String control = "";

        paynetEasyLogger.error("mId in processRefund ===== " + mId);
        paynetEasyLogger.error("isTest in processRefund ===== " + isTest);
        paynetEasyLogger.error("endpointid in processRefund ===== " + endpointid);
        paynetEasyLogger.error("username in processRefund ===== " + username);
        paynetEasyLogger.error("password in processRefund ===== " + password);
        paynetEasyLogger.error("orderId in processRefund ===== " + orderId);
        paynetEasyLogger.error("amount in processRefund ===== " + amount);
        paynetEasyLogger.error("currency in processRefund ===== " + currency);

        paynetEasyLogger.error("string required for control ===== " + username + trackingID + orderId + amountCent + currency + password);

        if (isTest)
        {
            refundURL = RB.getString("TEST_REFUND_URL") + endpointid;
        }
        else
        {
            refundURL = RB.getString("LIVE_REFUND_URL") + endpointid;
        }

        paynetEasyLogger.error("refundURL in processRefund ===== " + refundURL);

        if("JPY".equalsIgnoreCase(currency))
            amountCent=PayneteasyUtils.getJPYAmount(amount);

        else if("KWD".equalsIgnoreCase(currency))
            amountCent=PayneteasyUtils.getKWDSupportedAmount(amount);

        else
            amountCent=PayneteasyUtils.getCentAmount(amount);

        try
        {
            control = PayneteasyUtils.hashMac(username + trackingID + orderId + amountCent + currency + password);
        }
        catch (Exception e)
        {
            paynetEasyLogger.error("Exception in encoding name in processRefund ===== " , e);
        }

        paynetEasyLogger.error("control ===== " + control);
        paynetEasyLogger.error("comment ===== " + commTransactionDetailsVO.getOrderDesc());

        String comment = functions.isValueNull(commTransactionDetailsVO.getOrderDesc()) ? commTransactionDetailsVO.getOrderDesc() : "Refund transaction";

        StringBuffer request = new StringBuffer("login=" + username
                +"&orderid=" + orderId
                +"&client_orderid=" + trackingID
                +"&amount=" + amount
                +"&control=" + control
                +"&currency=" + currency
                +"&comment=" + comment
        );

        paynetEasyLogger.error("PaynetEasy Refund Request ===== " + trackingID+ "----" + request);

        String response = PayneteasyUtils.doPostHTTPSURLConnectionClient(refundURL, request.toString());
        paynetEasyLogger.error("PaynetEasy Refund Response ===== " + trackingID+ "----" + response);

        Map<String, String> resultMap = PayneteasyUtils.getResponseMap(response);
        paynetEasyLogger.error("PaynetEasy Refund ResultMap ===== " + trackingID+ "----" + resultMap);

        String responseType = resultMap.get("type");
        paynetEasyLogger.error("PaynetEasy Refund ResponseType +++++ " + responseType);

        String refundStatus = resultMap.get("status");
        paynetEasyLogger.error("PaynetEasy RefundStatus +++++ " + refundStatus);

        commRequestVO.getTransDetailsVO().setPreviousTransactionId(orderId);

        if(functions.isValueNull(responseType) && responseType.equalsIgnoreCase("async-response"))
        {
            try
            {
                commResponseVO = (Comm3DResponseVO) processQuery(trackingID, commRequestVO);
                if(!PZProcessType.REFUND.toString().equalsIgnoreCase(commResponseVO.getTransactionType())){
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Refund Declined");
                }
            }
            catch (PZGenericConstraintViolationException e)
            {
                paynetEasyLogger.error("exception  in processRefunds ===== " , e);
            }
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setRemark("Refund Declined");
        }

        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("Payneteasy","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

