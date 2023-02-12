package com.payment.clearsettle;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.google.gson.Gson;
import com.manager.PaymentManager;
import com.payment.Wirecardnew.WireCardNPaymentGateway;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.jeton.JetonUtils;
import com.payment.skrill.SkrillUtills;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Roshan on 9/7/2017.
 */
public class ClearSettleVoucherPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "csvoucher";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.clearsettle");
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettlePaymentGateway.class.getName());
    private static Functions functions = new Functions();
    private static ClearSettleUtills clearSettleUtills = new ClearSettleUtills();

    public ClearSettleVoucherPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String args[])
    {
        try
        {
            /*String requestData1=
                    "apiKey=108de0b176053216486c92c9e53f6dea" +
                    "&amount=200" +
                    "&currency=EUR" +
                    "&email= <emailaddress>" +
                    "&referenceNo=21414" +
                    "&returnUrl=http://localhost:8081/transaction/ClearSettleVoucherFrontEndServlet" +
                    "&billingFirstName=John" +
                    "&billingLastName=Wayne" +
                    "&billingAddress1=FakeStreet" +
                    "&billingCity=GothamCity" +
                    "&billingCountry=DE" +
                    "&billingPostcode=00007" +
                    "&birthday=1990-12-12" +
                    "&paymentMethod=VISA";

            System.out.println("requestData::::" + requestData1);
            //String responseData = clearSettleUtills.doPostHTTPSURLConnectionClient("https://testapi.theflyingmerchant.net/pw/v3/initializePayment", requestData1);
            String responseData = clearSettleUtills.doPostHTTPSURLConnectionFromData("https://testapi.gpsfsoft.com/pw/v3/initializePayment", requestData1);
            System.out.println("responseData--" + responseData);*/

            /*String apiKey="108de0b176053216486c92c9e53f6dea";
            String referenceId="21414";
            String requestData =
                    "{\n" +
                            "\"apiKey\":\"" + apiKey + "\" ,\n" +
                            "\"referenceNo\":\"" + referenceId + "\"}";

            System.out.println("-----voucher inquiry request-----" + requestData);

            String responseData="";
            responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_VOUCHER_STATUS_URL"), requestData);

            System.out.println("-----voucher inquiry response-----" + responseData);*/


            String apiKey="108de0b176053216486c92c9e53f6dea";
            String trackingID="83070";
            String amount="10.00";
            String currency="EUR";
            String transactionId="83296-1548420500-1381";

            String requestData =
                    "{"+
                            "\"apiKey\":\"" + apiKey + "\" ,\n" +
                            "\"referenceNo\":\"" + trackingID + "\" ,\n" +
                           /* "\"amount\":\"" + getCentAmount(amount) + "\" ,\n" +
                            "\"currency\":\"" + currency + "\" ,\n" +*/
                            "\"transactionId\" : \"" + transactionId + "\"}";

            System.out.println("-----voucher refund request-----" + requestData);

            String responseData="";

            responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_VOUCHER_REFUND_URL"), requestData);

            System.out.println("-----voucher refund response-----" + responseData);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ClearSettleVoucherPaymentGateway","processSale",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new Comm3DResponseVO();
        ClearSettleUtills clearSettleUtills = new ClearSettleUtills();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = gatewayAccount.getMerchantId();

        String countryCode="";
        if(functions.isValueNull(addressDetailsVO.getCountry())){
            countryCode=addressDetailsVO.getCountry();
            if(countryCode.length()>2){
                countryCode = countryCode.substring(0, 2);
            }
        }

        String status = "failed";
        String remark="";
        String descriptor = "";
        String responseData = "";

        try
        {
            String birthDate="";
            SimpleDateFormat inputDate = new SimpleDateFormat("yyyymmdd");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            if(functions.isValueNull(addressDetailsVO.getBirthdate())){
                birthDate = formatter.format(inputDate.parse(addressDetailsVO.getBirthdate()));
            }


            String requestData1="amount="+getCentAmount(transDetailsVO.getAmount())+
                    "&apiKey="+apiKey+
                    "&currency="+transDetailsVO.getCurrency()+
                    "&email="+addressDetailsVO.getEmail()+
                    "&referenceNo="+trackingID +
                    "&returnUrl="+RB.getString("CLEAR_SETTLE_VOUCHER_FRONTEND")+
                    "&billingFirstName="+addressDetailsVO.getFirstname()+
                    "&billingLastName="+addressDetailsVO.getLastname()+
                    "&billingAddress1="+addressDetailsVO.getStreet()+
                    "&billingCity="+addressDetailsVO.getCity()+
                    "&billingCountry="+countryCode+
                    "&billingPostcode="+addressDetailsVO.getZipCode()+
                    "&birthday="+birthDate+
                    "&paymentMethod=VISA";

            transactionLogger.error("-----voucher deposit request-----" + requestData1);
            if (isTest){
                responseData = clearSettleUtills.doPostHTTPSURLConnectionFromData(RB.getString("TEST_VOUCHER_PURCHASE_URL"), requestData1);
            }
            else{
                responseData = clearSettleUtills.doPostHTTPSURLConnectionFromData(RB.getString("LIVE_VOUCHER_PURCHASE_URL"), requestData1);
            }
            transactionLogger.error("-----voucher deposit response-----" + responseData);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClearSettleVoucherResponseVO clearSettleResponseVO = objectMapper.readValue(responseData, ClearSettleVoucherResponseVO.class);
            if(clearSettleResponseVO!=null){
                if("283".equals(clearSettleResponseVO.getCode()))
                {
                    if ("PENDING".equals(clearSettleResponseVO.getStatus())){
                        status = "pending";
                        commResponseVO.setRedirectUrl(URLDecoder.decode(clearSettleResponseVO.getPurchaseUrl()));
                        if (functions.isValueNull(clearSettleResponseVO.getDescriptor())){
                            descriptor = clearSettleResponseVO.getDescriptor();
                        }
                        else{
                            descriptor=gatewayAccount.getDisplayName();
                        }
                    }else{
                        status = "failed";
                        remark=clearSettleResponseVO.getMessage();
                    }
                }
                else{
                    status = "failed";
                    remark=clearSettleResponseVO.getMessage();
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(clearSettleResponseVO.getDate());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO responseVO = new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String responseData = "";
        try
        {
            String requestData =
                    "{\n" +
                            "\"apiKey\":\"" + apiKey + "\" ,\n" +
                            "\"referenceNo\":\"" + transactionDetailsVO.getOrderId() + "\"}";

            transactionLogger.error("-----voucher inquiry request-----" + requestData);
            if (isTest){
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_VOUCHER_STATUS_URL"), requestData);
            }
            else{
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_VOUCHER_STATUS_URL"), requestData);
            }

            transactionLogger.error("-----voucher inquiry response-----" + responseData);

            String status="";
            String transactionId="";

            if(functions.isValueNull(responseData) && responseData.contains("{")){
                JSONObject jsonObject= new JSONObject(responseData);
                if(jsonObject.has("status")){
                    status=jsonObject.getString("status");
                }
                if(jsonObject.has("transactionId")){
                    transactionId=jsonObject.getString("transactionId");
                }

            }
            if ("APPROVED".equalsIgnoreCase(status)){
                responseVO.setStatus("success");
                responseVO.setTransactionStatus("Transaction Successful");
                responseVO.setDescriptor(gatewayAccount.getDisplayName());
            }else {
                responseVO.setStatus("fail");
                responseVO.setTransactionStatus("Transaction Fail");
            }

            responseVO.setTransactionId(transactionId);
            responseVO.setDescription(status);
            responseVO.setRemark(status);
            responseVO.setMerchantId("-");
            responseVO.setBankTransactionDate("-");
            responseVO.setAmount("-");
            responseVO.setCurrency("-");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            responseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws  PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        ClearSettleResponseVO responseVO = new ClearSettleResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String status = "";
        String descriptor = "";
        String responseData = "";
        try
        {
            String requestData =
                    "{\n" +
                            "\"apiKey\":\"" + apiKey + "\",\n" +
                            "\"referenceNo\":\"" + trackingID + "\",\n" +
                            "\"transactionId\":\"" + transactionDetailsVO.getPreviousTransactionId() + "\"}";

            transactionLogger.error("-----voucher cancel request-----" + requestData);
            if (isTest){
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_CANCEL_URL"), requestData);
            }
            else{
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_CANCEL_URL"), requestData);
            }
            transactionLogger.error("-----voucher cancel response-----" + responseData);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(responseData, ClearSettleResponseVO.class);
            if ("00".equals(responseVO.getCode())){
                status = "success";
                if (functions.isValueNull(responseVO.getDescriptor())){
                    descriptor = responseVO.getDescriptor();
                }
            }
            responseVO.setStatus(status);
            responseVO.setDescriptor(descriptor);
            responseVO.setDescription(responseVO.getMessage());
        }
        catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e){
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        ClearSettleResponseVO responseVO = new ClearSettleResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String status = "failed";
        String descriptor = "";
        String responseData = "";
        try
        {
            String requestData =
                    "{"+
                            "\"apiKey\":\"" + apiKey + "\" ,\n" +
                            "\"referenceNo\":\"" + trackingID + "\" ,\n" +
                            "\"amount\":\"" + getCentAmount(transactionDetailsVO.getAmount()) + "\" ,\n" +
                            "\"currency\":\"" + transactionDetailsVO.getCurrency() + "\" ,\n" +
                            "\"transactionId\" : \"" + transactionDetailsVO.getPreviousTransactionId() + "\"}";

            transactionLogger.error("-----voucher refund request-----" + requestData);
            if (isTest){
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_VOUCHER_REFUND_URL"), requestData);
            }
            else{
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_VOUCHER_REFUND_URL"), requestData);
            }
            transactionLogger.error("-----voucher refund response-----" + responseData);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(responseData, ClearSettleResponseVO.class);
            if ("00".equals(responseVO.getCode()))
            {
                status = "success";
                if (functions.isValueNull(responseVO.getDescriptor()))
                {
                    descriptor = responseVO.getDescriptor();
                }else {
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            String message="";
            if(!functions.isValueNull(responseVO.getMessage())){
                message=responseVO.getStatus();
            }
            responseVO.setStatus(status);
            responseVO.setDescriptor(descriptor);
            responseVO.setDescription(message);
            responseVO.setRemark(message);
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleVoucherPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        String html = "";
        try
        {
            transactionLogger.error(":::::Entered into processAutoRedirect for ClearSettleVoucher:::::");
            CommRequestVO commRequestVO = null;
            Comm3DResponseVO transRespDetails = null;

            AuditTrailVO auditTrailVO= new AuditTrailVO();
            PaymentManager paymentManager = new PaymentManager();
            auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            auditTrailVO.setActionExecutorName("Customer");
            ClearSettleUtills clearSettleUtills1 = new ClearSettleUtills();
            commRequestVO = clearSettleUtills1.getClearSettleHPPRequest(commonValidatorVO);

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

            if(transRespDetails!=null){
                if(functions.isValueNull(transRespDetails.getRedirectUrl())){
                    html = clearSettleUtills1.generateAutoSubmitFormNew(transRespDetails);
                }else {
                    html="failed";
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                }
            }
            transactionLogger.error("html-----"+html);
        }catch (PZGenericConstraintViolationException e){
            transactionLogger.error("PZGenericConstraintViolationException-----",e);
        }
        return html;

    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException("ClearSettleVoucherPaymentGateway","processCapture()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }

    public static String  getCentAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }
}
