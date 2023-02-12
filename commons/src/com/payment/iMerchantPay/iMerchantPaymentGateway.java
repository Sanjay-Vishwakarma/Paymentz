package com.payment.iMerchantPay;

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
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Uday on 6/26/18.
 */
public class iMerchantPaymentGateway extends AbstractPaymentGateway
{
    private static final  TransactionLogger transactionLogger= new TransactionLogger(iMerchantPaymentGateway.class.getName());

    final static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.iMerchantPay");

    public static final String GATEWAY_TYPE="iMerchant";

    public iMerchantPaymentGateway(String accountId){this.accountId=accountId;}

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        StringBuffer requestData = new StringBuffer();
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String attemptThreeD = "";
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String reject3DCard=commRequestVO.getReject3DCard();
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }
        String birthday = "";
        if (functions.isValueNull(commAddressDetailsVO.getBirthdate()))
        {
            birthday = iMerchantPayUtils.getBirthday(commAddressDetailsVO.getBirthdate());
        }
        transactionLogger.debug("customerDOB----"+birthday);
        try
        {
            String passCode = gatewayAccount.getFRAUD_FTP_PASSWORD(); //your pass Code
            String merchantID = gatewayAccount.getMerchantId(); //your merchant ID
            String amount = commTransactionDetailsVO.getAmount();
            String currency = commTransactionDetailsVO.getCurrency(); //your assign currency
            String orderID = trackingID; // unique order ID
            String customerIP = commAddressDetailsVO.getCardHolderIpAddress();
            String customerEmail = commAddressDetailsVO.getEmail();
            String customerPhone = commAddressDetailsVO.getPhone();
            String customerFirstName = commAddressDetailsVO.getFirstname();
            String customerLastName = commAddressDetailsVO.getLastname();
            String customerAddress1 = commAddressDetailsVO.getStreet();
            String customerCity = commAddressDetailsVO.getCity();
            String customerZipCode = commAddressDetailsVO.getZipCode();
            String customerStateProvince = commAddressDetailsVO.getState();
            String customerCountry = commAddressDetailsVO.getCountry();
            String cardNumber = commCardDetailsVO.getCardNum();
            String cardCVV2 = commCardDetailsVO.getcVV();
            String cardExpiryDate = commCardDetailsVO.getExpMonth() + commCardDetailsVO.getExpYear().substring(2);
            String cardHolderName = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            String pSign = "";
            String and = "&";

            //Note- 3D is  pending on bank side.
            if ("Y".equalsIgnoreCase(is3dSupported) && !("Direct".equalsIgnoreCase(attemptThreeD)))
            {
                pSign = iMerchantPayUtils.generatePSign(passCode + merchantID + amount + currency + orderID + cardNumber + cardExpiryDate + cardHolderName);
                requestData.append("merchantID=" + merchantID);
                requestData.append(and + "amount=" + amount);
                requestData.append(and + "currency=" + currency);
                requestData.append(and + "orderID=" + orderID);
                requestData.append(and + "cardNumber=" + cardNumber);
                requestData.append(and + "cardExpiryDate=" + cardExpiryDate);
                requestData.append(and + "cardHolderName=" + cardHolderName);
                requestData.append(and + "pSign=" + pSign);

                transactionLogger.error("checkEnrollment request-----" + requestData.toString());
                String enrollmentResponse = "";
                if (isTest)
                {
                    enrollmentResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_3DS_MPI_URL"));
                }
                else
                {
                    enrollmentResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("LIVE_3DS_MPI_URL"));
                }
                transactionLogger.error("checkEnrollment response-----" + enrollmentResponse);
            }
            else
            {
                pSign = iMerchantPayUtils.generatePSign(passCode+ merchantID+ amount+ currency+orderID+customerIP+customerEmail+customerPhone+customerFirstName+customerLastName+customerAddress1+customerCity+customerZipCode+customerStateProvince+customerCountry+birthday+cardNumber+cardCVV2+cardExpiryDate+cardHolderName);
                requestData.append("merchantID=" + merchantID);
                requestData.append(and + "amount=" + amount);
                requestData.append(and + "currency=" + currency);
                requestData.append(and + "orderID=" + orderID);
                requestData.append(and + "customerIP=" + customerIP);
                requestData.append(and + "customerEmail=" + customerEmail);
                requestData.append(and + "customerPhone=" + customerPhone);
                requestData.append(and + "customerFirstName=" + customerFirstName);
                requestData.append(and + "customerLastName=" + customerLastName);
                requestData.append(and + "customerAddress1=" + customerAddress1);
                requestData.append(and + "customerCity=" + customerCity);
                requestData.append(and + "customerZipCode=" + customerZipCode);
                requestData.append(and + "customerStateProvince=" + customerStateProvince);
                requestData.append(and + "customerCountry=" + customerCountry);
                requestData.append(and + "customerDob=" + birthday);
                requestData.append(and + "cardNumber=" + cardNumber);
                requestData.append(and + "cardCVV2=" + cardCVV2);
                requestData.append(and + "cardExpiryDate=" + cardExpiryDate);
                requestData.append(and + "cardHolderName=" + cardHolderName);
                requestData.append(and + "pSign=" + pSign);

                /*System.out.println("isTest-----" + isTest  );
                System.out.println("-------TesT_URL----"+RB.getString("TEST_SALE_URL"));*/

                transactionLogger.error("sale request-----" + requestData.toString());

                String saleResponse = "";
                if (isTest)
                {
                    saleResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_SALE_URL"));
                }
                else
                {
                    saleResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("LIVE_SALE_URL"));
                }
                transactionLogger.error("sale response-----" + saleResponse);

                if(functions.isValueNull(saleResponse) && saleResponse.contains("{")){
                    JSONObject jsonObject= new JSONObject(saleResponse);
                    String responseCode="";
                    String reasonCode="";
                    String transactionID="";
                    String responseAmount="";
                    String responseCurrency="";
                    String executed="";
                    String errorInfo="";
                    if(jsonObject.has("responseCode")){
                        responseCode=jsonObject.getString("responseCode");
                    }
                    if(jsonObject.has("reasonCode")){
                        reasonCode=jsonObject.getString("reasonCode");
                    }
                    if(jsonObject.has("errorInfo")){
                        errorInfo=jsonObject.getString("errorInfo");
                    }
                    if(jsonObject.has("transaction")){
                        if(jsonObject.getJSONObject("transaction").has("transactionID")){
                            transactionID=jsonObject.getJSONObject("transaction").getString("transactionID");
                        }
                        if(jsonObject.getJSONObject("transaction").has("amount")){
                            responseAmount=jsonObject.getJSONObject("transaction").getString("amount");
                        }
                        if(jsonObject.getJSONObject("transaction").has("currency")){
                            responseCurrency=jsonObject.getJSONObject("transaction").getString("currency");
                        }
                        if(jsonObject.getJSONObject("transaction").has("executed")){
                            executed=jsonObject.getJSONObject("transaction").getString("executed");
                        }
                    }


                    String remark="";
                    String description="";
                    if("1".equals(responseCode) && "1".equals(reasonCode)){
                        remark= "Transaction Successful";
                        description="Successful";
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription(description);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setAmount(responseAmount);
                        commResponseVO.setCurrency(responseCurrency);
                    }else {
                        commResponseVO.setStatus("fail");
                        if("2".equals(responseCode)){
                            if(!functions.isValueNull(errorInfo)){
                                remark= "Transaction Declined";
                                description="Declined";
                            }else {
                                remark= errorInfo;
                                description=errorInfo;
                            }
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription(description);
                            commResponseVO.setAmount(responseAmount);
                            commResponseVO.setCurrency(responseCurrency);
                        }else if("3".equals(responseCode)){
                            if(!functions.isValueNull(errorInfo)){
                                remark= "Error";
                                description="Error";
                            }else {
                                remark= errorInfo;
                                description=errorInfo;
                            }
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription(description);
                        }else {
                            if(!functions.isValueNull(errorInfo)){
                                remark= "Transaction Fail";
                                description="Failed";
                            }else {
                                remark= errorInfo;
                                description=errorInfo;
                            }
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription(description);
                        }
                    }
                    commResponseVO.setTransactionId(transactionID);
                    commResponseVO.setErrorCode(reasonCode);
                    commResponseVO.setBankTransactionDate(executed);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }else {
                    commResponseVO.setRemark("Internal Error");
                    commResponseVO.setDescription("Internal Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
            }
        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(iMerchantPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        StringBuffer requestData = new StringBuffer();
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String attemptThreeD = "";
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String reject3DCard=commRequestVO.getReject3DCard();
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }
        String birthday = "";
        if (functions.isValueNull(commAddressDetailsVO.getBirthdate()))
        {
            birthday = iMerchantPayUtils.getBirthday(commAddressDetailsVO.getBirthdate());
        }
        transactionLogger.debug("customerDOB----"+birthday);

        try
        {
            String passCode = gatewayAccount.getFRAUD_FTP_PASSWORD(); //your pass Code
            String merchantID = gatewayAccount.getMerchantId(); //your merchant ID
            String amount = commTransactionDetailsVO.getAmount();
            String currency = commTransactionDetailsVO.getCurrency(); //your assign currency
            String orderID = trackingID; // unique order ID
            String customerIP = commAddressDetailsVO.getCardHolderIpAddress();
            String customerEmail = commAddressDetailsVO.getEmail();
            String customerPhone = commAddressDetailsVO.getPhone();
            String customerFirstName = commAddressDetailsVO.getFirstname();
            String customerLastName = commAddressDetailsVO.getLastname();
            String customerAddress1 = commAddressDetailsVO.getStreet();
            String customerCity = commAddressDetailsVO.getCity();
            String customerZipCode = commAddressDetailsVO.getZipCode();
            String customerStateProvince = commAddressDetailsVO.getState();
            String customerCountry = commAddressDetailsVO.getCountry();
            String cardNumber = commCardDetailsVO.getCardNum();
            String cardCVV2 = commCardDetailsVO.getcVV();
            String cardExpiryDate = commCardDetailsVO.getExpMonth() + commCardDetailsVO.getExpYear().substring(2);
            String cardHolderName = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            String pSign = "";
            String and = "&";


            if ("Y".equalsIgnoreCase(is3dSupported) && !("Direct".equalsIgnoreCase(attemptThreeD)))
            {
                pSign = iMerchantPayUtils.generatePSign(passCode + merchantID + amount + currency + orderID + cardNumber + cardExpiryDate + cardHolderName);
                requestData.append("merchantID=" + merchantID);
                requestData.append(and + "amount=" + amount);
                requestData.append(and + "currency=" + currency);
                requestData.append(and + "orderID=" + orderID);
                requestData.append(and + "cardNumber=" + cardNumber);
                requestData.append(and + "cardExpiryDate=" + cardExpiryDate);
                requestData.append(and + "cardHolderName=" + cardHolderName);
                requestData.append(and + "pSign=" + pSign);

                transactionLogger.error("checkEnrollment request-----" + requestData.toString());
                String enrollmentResponse = "";
                if (isTest)
                {
                    enrollmentResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_3DS_MPI_URL"));
                }
                else
                {
                    enrollmentResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("LIVE_3DS_MPI_URL"));
                }
                transactionLogger.error("checkEnrollment response-----" + enrollmentResponse);
            }
            else
            {
                pSign = iMerchantPayUtils.generatePSign(passCode+ merchantID+ amount+ currency+orderID+customerIP+customerEmail+customerPhone+customerFirstName+customerLastName+customerAddress1+customerCity+customerZipCode+customerStateProvince+customerCountry+birthday+cardNumber+cardCVV2+cardExpiryDate+cardHolderName);
                requestData.append("merchantID=" + merchantID);
                requestData.append(and + "amount=" + amount);
                requestData.append(and + "currency=" + currency);
                requestData.append(and + "orderID=" + orderID);
                requestData.append(and + "customerIP=" + customerIP);
                requestData.append(and + "customerEmail=" + customerEmail);
                requestData.append(and + "customerPhone=" + customerPhone);
                requestData.append(and + "customerFirstName=" + customerFirstName);
                requestData.append(and + "customerLastName=" + customerLastName);
                requestData.append(and + "customerAddress1=" + customerAddress1);
                requestData.append(and + "customerCity=" + customerCity);
                requestData.append(and + "customerZipCode=" + customerZipCode);
                requestData.append(and + "customerStateProvince=" + customerStateProvince);
                requestData.append(and + "customerCountry=" + customerCountry);
                requestData.append(and + "customerDob=" + birthday);
                requestData.append(and + "cardNumber=" + cardNumber);
                requestData.append(and + "cardCVV2=" + cardCVV2);
                requestData.append(and + "cardExpiryDate=" + cardExpiryDate);
                requestData.append(and + "cardHolderName=" + cardHolderName);
                requestData.append(and + "pSign=" + pSign);

                transactionLogger.error("auth request-----" + requestData.toString());
                String authResponse = "";
                if (isTest)
                {
                    authResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_AUTH_URL"));
                }
                else
                {
                    authResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("LIVE_AUTH_URL"));
                }
                transactionLogger.error("auth response-----" + authResponse);

                if(functions.isValueNull(authResponse) && authResponse.contains("{")){
                    JSONObject jsonObject= new JSONObject(authResponse);
                    String responseCode="";
                    String reasonCode="";
                    String transactionID="";
                    String responseAmount="";
                    String responseCurrency="";
                    String executed="";
                    String errorInfo="";
                    if(jsonObject.has("responseCode")){
                        responseCode=jsonObject.getString("responseCode");
                    }
                    if(jsonObject.has("reasonCode")){
                        reasonCode=jsonObject.getString("reasonCode");
                    }
                    if(jsonObject.has("errorInfo")){
                        errorInfo=jsonObject.getString("errorInfo");
                    }
                    if(jsonObject.has("transaction")){
                        if(jsonObject.getJSONObject("transaction").has("transactionID")){
                            transactionID=jsonObject.getJSONObject("transaction").getString("transactionID");
                        }
                        if(jsonObject.getJSONObject("transaction").has("amount")){
                            responseAmount=jsonObject.getJSONObject("transaction").getString("amount");
                        }
                        if(jsonObject.getJSONObject("transaction").has("currency")){
                            responseCurrency=jsonObject.getJSONObject("transaction").getString("currency");
                        }
                        if(jsonObject.getJSONObject("transaction").has("executed")){
                            executed=jsonObject.getJSONObject("transaction").getString("executed");
                        }

                    }

                    String remark="";
                    String description="";
                    if("10".equals(responseCode) && "10".equals(reasonCode)){
                        remark= "Transaction Successful";
                        description="Successful";
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription(description);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setAmount(responseAmount);
                        commResponseVO.setCurrency(responseCurrency);
                    }else {
                        commResponseVO.setStatus("fail");
                        if("2".equals(responseCode)){
                            remark= "Transaction Declined";
                            description="Declined";
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription(description);
                            commResponseVO.setAmount(responseAmount);
                            commResponseVO.setCurrency(responseCurrency);
                        }else if("3".equals(responseCode)){
                            remark= "Error";
                            description="Error";
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription(description);
                        }else {
                            commResponseVO.setRemark("Transaction Fail");
                            commResponseVO.setDescription("Failed");
                        }
                        if(!functions.isValueNull(remark) && !functions.isValueNull(description)){
                            commResponseVO.setRemark(errorInfo);
                            commResponseVO.setDescription(errorInfo);
                        }
                    }
                    commResponseVO.setTransactionId(transactionID);
                    commResponseVO.setErrorCode(reasonCode);
                    commResponseVO.setBankTransactionDate(executed);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }else {
                    commResponseVO.setRemark("Internal Error");
                    commResponseVO.setDescription("Internal Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }
            }
        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(iMerchantPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        StringBuffer requestData = new StringBuffer();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        boolean isTest=gatewayAccount.isTest();
        try{
            String passCode = gatewayAccount.getFRAUD_FTP_PASSWORD(); //your pass Code
            String merchantID = gatewayAccount.getMerchantId(); //your merchant ID
            String amount = commTransactionDetailsVO.getAmount();
            String transactionID=commTransactionDetailsVO.getPreviousTransactionId();
            String pSign = "";
            String and = "&";

            pSign = iMerchantPayUtils.generatePSign(passCode + merchantID + amount +transactionID);
            requestData.append("merchantID=" + merchantID);
            requestData.append(and + "amount=" + amount);
            requestData.append(and + "transactionID=" + transactionID);
            requestData.append(and + "pSign=" + pSign);

            transactionLogger.error("capture request-----" + requestData.toString());
            String captureResponse = "";
            if (isTest)
            {
                captureResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_CAPTURE_URL"));
            }
            else
            {
                captureResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("LIVE_CAPTURE_URL"));
            }
            transactionLogger.error("capture response-----" + captureResponse);

            if(functions.isValueNull(captureResponse) && captureResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(captureResponse);
                String responseCode = "";
                String reasonCode = "";
                String paymentId = "";
                String responseAmount = "";
                String responseCurrency = "";
                String executed = "";

                if (jsonObject.has("responseCode"))
                {
                    responseCode = jsonObject.getString("responseCode");
                }
                if (jsonObject.has("reasonCode"))
                {
                    reasonCode = jsonObject.getString("reasonCode");
                }

                if(jsonObject.has("transaction")){
                    if (jsonObject.getJSONObject("transaction").has("transactionID"))
                    {
                        paymentId = jsonObject.getJSONObject("transaction").getString("transactionID");
                    }
                    if (jsonObject.getJSONObject("transaction").has("amount"))
                    {
                        responseAmount = jsonObject.getJSONObject("transaction").getString("amount");
                    }
                    if (jsonObject.getJSONObject("transaction").has("currency"))
                    {
                        responseCurrency = jsonObject.getJSONObject("transaction").getString("currency");
                    }
                    if (jsonObject.getJSONObject("transaction").has("executed"))
                    {
                        executed = jsonObject.getJSONObject("transaction").getString("executed");
                    }

                }

                if("1".equals(responseCode) && "1".equals(reasonCode)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescription("Successful");
                    commResponseVO.setAmount(responseAmount);
                    commResponseVO.setCurrency(responseCurrency);
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Fail");
                    commResponseVO.setDescription("Failed");
                }
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setTransactionId(paymentId);
                commResponseVO.setBankTransactionDate(executed);
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }else {
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Internal Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(iMerchantPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        StringBuffer requestData = new StringBuffer();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        boolean isTest=gatewayAccount.isTest();
        try{
            String passCode = gatewayAccount.getFRAUD_FTP_PASSWORD(); //your pass Code
            String merchantID = gatewayAccount.getMerchantId(); //your merchant ID
            String refundAmount  = commTransactionDetailsVO.getAmount();
            String transactionID=commTransactionDetailsVO.getPreviousTransactionId();
            String pSign = "";
            String and = "&";

            pSign = iMerchantPayUtils.generatePSign(passCode + merchantID + refundAmount  +transactionID);
            requestData.append("merchantID=" + merchantID);
            requestData.append(and + "refundAmount=" + refundAmount );
            requestData.append(and + "transactionID=" + transactionID);
            requestData.append(and + "pSign=" + pSign);

            transactionLogger.error("refund request-----" + requestData.toString());
            String refundResponse = "";
            if (isTest)
            {
                refundResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_REFUND_URL"));
            }
            else
            {
                refundResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("LIVE_REFUND_URL"));
            }
            transactionLogger.error("refund response-----" + refundResponse);

            if(functions.isValueNull(refundResponse) && refundResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(refundResponse);
                String responseCode = "";
                String reasonCode = "";
                String paymentId = "";
                String responseAmount = "";
                String responseCurrency = "";
                String executed = "";
                String errorInfo="";

                if (jsonObject.has("responseCode"))
                {
                    responseCode = jsonObject.getString("responseCode");
                }
                if (jsonObject.has("reasonCode"))
                {
                    reasonCode = jsonObject.getString("reasonCode");
                }
                if(jsonObject.has("errorInfo")){
                    errorInfo=jsonObject.getString("errorInfo");
                }

                if(jsonObject.has("transaction")){
                    if (jsonObject.getJSONObject("transaction").has("transactionID"))
                    {
                        paymentId = jsonObject.getJSONObject("transaction").getString("transactionID");
                    }
                    if (jsonObject.getJSONObject("transaction").has("amount"))
                    {
                        responseAmount = jsonObject.getJSONObject("transaction").getString("amount");
                    }
                    if (jsonObject.getJSONObject("transaction").has("currency"))
                    {
                        responseCurrency = jsonObject.getJSONObject("transaction").getString("currency");
                    }
                    if (jsonObject.getJSONObject("transaction").has("executed"))
                    {
                        executed = jsonObject.getJSONObject("transaction").getString("executed");
                    }
                }


                if("1".equals(responseCode) && "1".equals(reasonCode)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescription("Successful");
                    commResponseVO.setAmount(responseAmount);
                    commResponseVO.setCurrency(responseCurrency);
                }else {
                    if(!functions.isValueNull(errorInfo)){
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("Transaction Fail");
                        commResponseVO.setDescription("Failed");
                    }else {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark(errorInfo);
                        commResponseVO.setDescription(errorInfo);
                    }

                }
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setTransactionId(paymentId);
                commResponseVO.setBankTransactionDate(executed);
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            }else {
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Internal Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            }
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(iMerchantPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        StringBuffer requestData = new StringBuffer();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        boolean isTest=gatewayAccount.isTest();
        try{
            String passCode = gatewayAccount.getFRAUD_FTP_PASSWORD(); //your pass Code
            String merchantID = gatewayAccount.getMerchantId(); //your merchant ID
            String transactionID=commTransactionDetailsVO.getPreviousTransactionId();
            String pSign = "";
            String and = "&";

            pSign = iMerchantPayUtils.generatePSign(passCode + merchantID +transactionID);
            requestData.append("merchantID=" + merchantID);
            requestData.append(and + "transactionID=" + transactionID);
            requestData.append(and + "pSign=" + pSign);

            transactionLogger.error("void request-----" + requestData.toString());
            String voidResponse = "";
            if (isTest)
            {
                voidResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_VOID_URL"));
            }
            else
            {
                voidResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("LIVE_VOID_URL"));
            }
            transactionLogger.error("void response-----" + voidResponse);

            if(functions.isValueNull(voidResponse) && voidResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(voidResponse);
                String responseCode = "";
                String reasonCode = "";

                if (jsonObject.has("responseCode"))
                {
                    responseCode = jsonObject.getString("responseCode");
                }
                if (jsonObject.has("reasonCode"))
                {
                    reasonCode = jsonObject.getString("reasonCode");
                }

                if("1".equals(responseCode) && "1".equals(reasonCode)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescription("Successful");

                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Fail");
                    commResponseVO.setDescription("Failed");
                }
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }else {
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Internal Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(iMerchantPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        StringBuffer requestData = new StringBuffer();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        boolean isTest=gatewayAccount.isTest();
        try{
            String passCode = gatewayAccount.getFRAUD_FTP_PASSWORD(); //your pass Code
            String merchantID = gatewayAccount.getMerchantId(); //your merchant ID
            String orderID =commTransactionDetailsVO.getOrderId();
            String pSign = "";
            String and = "&";

            pSign = iMerchantPayUtils.generatePSign(passCode + merchantID +orderID);
            requestData.append("merchantID=" + merchantID);
            requestData.append(and + "orderID=" + orderID);
            requestData.append(and + "pSign=" + pSign);

            transactionLogger.error("inquiry request-----" + requestData.toString());
            String inquiryResponse = "";
            if (isTest)
            {
                inquiryResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_INQUIRY_URL"));
            }
            else
            {
                inquiryResponse = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), RB.getString("TEST_INQUIRY_URL"));
            }
            transactionLogger.error("inquiry response-----" + inquiryResponse);

            if(functions.isValueNull(inquiryResponse) && inquiryResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(inquiryResponse);
                String responseCode = "";
                String reasonCode = "";
                String transactionID="";
                String responseAmount="";
                String responseCurrency="";
                String executed="";
                String relatedTransaction="";
                String status="";
                String amount="";

                if (jsonObject.has("responseCode"))
                {
                    responseCode = jsonObject.getString("responseCode");
                }
                if (jsonObject.has("reasonCode"))
                {
                    reasonCode = jsonObject.getString("reasonCode");
                }

                if(jsonObject.has("transaction")){
                    if(jsonObject.getJSONObject("transaction").has("transactionID")){
                        transactionID=jsonObject.getJSONObject("transaction").getString("transactionID");
                    }
                    if(jsonObject.getJSONObject("transaction").has("amount")){
                        responseAmount=jsonObject.getJSONObject("transaction").getString("amount");
                    }
                    if(jsonObject.getJSONObject("transaction").has("currency")){
                        responseCurrency=jsonObject.getJSONObject("transaction").getString("currency");
                    }
                    if(jsonObject.getJSONObject("transaction").has("executed")){
                        executed=jsonObject.getJSONObject("transaction").getString("executed");
                    }

                    if(jsonObject.getJSONObject("transaction").has("relatedTransactions")){
                        JSONArray jsonArray=jsonObject.getJSONObject("transaction").getJSONArray("relatedTransactions");
                        for(int i=0 ; i<jsonArray.length(); i++){

                            JSONObject object=jsonArray.getJSONObject(i);
                            relatedTransaction=object.getString("relatedTransaction");
                            status=object.getString("status");
                            amount=object.getString("amount");
                        }
                    }
                }


                if("1".equals(responseCode) && "1".equals(reasonCode)){
                    if(functions.isValueNull(status)){
                         if("40".equals(status)){
                            commResponseVO.setTransactionStatus("refund");
                            commResponseVO.setRemark("Transaction is refunded");
                            commResponseVO.setDescription("Successful");
                            commResponseVO.setAmount(amount);
                            commResponseVO.setTransactionId(relatedTransaction);
                            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        }else  if("24".equals(status)){
                            commResponseVO.setTransactionStatus("voided");
                            commResponseVO.setRemark("Transaction is voided");
                            commResponseVO.setDescription("Successful");
                            commResponseVO.setAmount(amount);
                             commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                            commResponseVO.setTransactionId(relatedTransaction);
                        }else if("80".equals(status)){
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("Transaction is pending processing");
                            commResponseVO.setDescription("pending");
                            commResponseVO.setAmount(amount);
                            commResponseVO.setTransactionId(relatedTransaction);
                        }
                    }else {
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark("Transaction is approved");
                        commResponseVO.setDescription("Successful");
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                }else if("24".equals(responseCode) && "24".equals(reasonCode)){
                    commResponseVO.setTransactionStatus("voided");
                    commResponseVO.setRemark("Transaction is voided");
                    commResponseVO.setDescription("Successful");
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                }
                else if("10".equals(responseCode) && "10".equals(reasonCode)){
                    commResponseVO.setTransactionStatus("authorized");
                    commResponseVO.setRemark("Transaction is authorized");
                    commResponseVO.setDescription("Successful");
                    commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }
                else if("2".equals(responseCode) && "2".equals(reasonCode)) {
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setRemark("Transaction is declined");
                    commResponseVO.setDescription("Declined");
                }else if("5".equals(responseCode) && "5".equals(reasonCode)){
                    commResponseVO.setTransactionStatus("Cancelled");
                    commResponseVO.setRemark("Transaction Cancelled");
                    commResponseVO.setDescription("Customer canceled transaction");
                }else if("7".equals(responseCode) && "7".equals(reasonCode)){
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Transaction pending");
                    commResponseVO.setDescription("pending");
                }else {
                    commResponseVO.setTransactionStatus("Fail");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Failed");
                }
                commResponseVO.setAmount(responseAmount);
                commResponseVO.setCurrency(responseCurrency);
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setTransactionId(transactionID);
                commResponseVO.setBankTransactionDate(executed);
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setMerchantId(merchantID);
                if(!functions.isValueNull(commResponseVO.getTransactionType())){
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                }
            }else {
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Internal Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            }
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(iMerchantPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("iMerchantPaymentGateway","processPayout",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

    }
/*
     public static void main(String[] args)
    {
        try
        {
             String endPointUrl = "https://payment.imerchantpayments.com/transaction/execute";
           // String endPointUrl = "https://payment.imerchantpayments.com/transaction/authorize";
            //String endPointUrl = "https://payment.imerchantpayments.com/transaction/mpi";
            String passCode = "2#2Um5DE8krJ"; //your pass Code
            String merchantID = "4"; //your merchant ID
            String amount = "2.11";
            String currency = "USD"; //your assign currency
            String orderID = "21214"; // unique order ID
            //String returnURL = "";
            //  String notifyURL = "";
            String customerIP = "45.64.195.218";
            //   String customerForwardedIP = "";
            //   String customerUserAgent = "";
            //   String customerAcceptLanguage = "";
            String customerEmail = "";
            String customerPhone = "9870850511";
            String customerFirstName = "Uday";
            String customerLastName = "Raj";
            String customerAddress1 = "Malad";
            // String customerAddress2 = "Mindspace";
            String customerCity = "Mumbai";
            String customerZipCode = "400067";
            String customerStateProvince = "MH";
            String customerCountry = "IN";
            String customerDob = "1989-02-02";
            String cardNumber = "4773654827386427";
            String cardCVV2 = "777";
            String cardExpiryDate = "1020";
            String cardHolderName = "Uday Raj";
            // String routingString = "";
            // String saveCard = "";
            // String description = "";
            //String csid = "";
            //String threeDSecureInfo = "";

            */
/*String pSign = iMerchantPayUtils.generatePSign(passCode + merchantID + amount + currency + orderID + returnURL +
                    notifyURL + customerIP + customerForwardedIP + customerUserAgent + customerAcceptLanguage +
                    customerEmail + customerPhone + customerFirstName + customerLastName + customerAddress1 +
                    customerAddress2 + customerCity + customerZipCode + customerStateProvince + customerCountry +
                    customerDob + cardNumber + cardCVV2 + cardExpiryDate + cardHolderName +
                    routingString + saveCard + description + csid + threeDSecureInfo);
*//*

            String pSign = iMerchantPayUtils.generatePSign(passCode+ merchantID+ amount+ currency+orderID+customerIP+customerEmail+customerPhone+customerFirstName+customerLastName+customerAddress1+customerCity+customerZipCode+customerStateProvince+customerCountry+customerDob+cardNumber+cardCVV2+cardExpiryDate+cardHolderName);
            // String pSign = iMerchantPayUtils.generatePSign(passCode+ merchantID+ amount+ currency+orderID+cardNumber+cardExpiryDate+cardHolderName);

            System.out.println("pSign-----"+pSign);
            String and = "&";
            StringBuffer requestData = new StringBuffer();

            requestData.append("merchantID=" + merchantID);
            requestData.append(and + "amount=" + amount);
            requestData.append(and + "currency=" + currency);
            requestData.append(and + "orderID=" + orderID);
            // requestData.append(and + "returnURL=" + URLEncoder.encode(returnURL, "UTF-8"));
            //  requestData.append(and + "notifyURL=" + URLEncoder.encode(notifyURL, "UTF-8"));
            requestData.append(and + "customerIP=" + customerIP);
            // requestData.append(and + "customerForwardedIP=" + customerForwardedIP);
            //   requestData.append(and + "customerUserAgent=" + customerUserAgent);
            //   requestData.append(and + "customerAcceptLanguage=" + customerAcceptLanguage);
            requestData.append(and + "customerEmail=" + customerEmail);
            requestData.append(and + "customerPhone=" + customerPhone);
            requestData.append(and + "customerFirstName=" + customerFirstName);
            requestData.append(and + "customerLastName=" + customerLastName);
            requestData.append(and + "customerAddress1=" + customerAddress1);
            //   requestData.append(and + "customerAddress2=" + customerAddress2);
            requestData.append(and + "customerCity=" + customerCity);
            requestData.append(and + "customerZipCode=" + customerZipCode);
            requestData.append(and + "customerStateProvince=" + customerStateProvince);
            requestData.append(and + "customerCountry=" + customerCountry);
            requestData.append(and + "customerDob=" + customerDob);
            requestData.append(and + "cardNumber=" + cardNumber);
            requestData.append(and + "cardCVV2=" + cardCVV2);
            requestData.append(and + "cardExpiryDate=" + cardExpiryDate);
            requestData.append(and + "cardHolderName=" + cardHolderName);
            //   requestData.append(and + "routingString=" + routingString);
            //   requestData.append(and + "saveCard=" + saveCard);
            //   requestData.append(and + "description=" + description);
            //   requestData.append(and + "csid=" + csid);
            //   requestData.append(and + "threeDSecureInfo=" + URLEncoder.encode(threeDSecureInfo, "UTF-8"));
            requestData.append(and + "pSign=" + pSign);

            System.out.println("requestData-----"+requestData.toString());

            String result = iMerchantPayUtils.doPostHTTPSURLConnectionClient(requestData.toString(), endPointUrl);

            System.out.println("result-----"+result);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
*/
}
