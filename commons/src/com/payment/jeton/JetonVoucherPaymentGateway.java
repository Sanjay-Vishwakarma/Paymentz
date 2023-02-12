package com.payment.jeton;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Uday on 8/26/17.
 */
public class JetonVoucherPaymentGateway extends AbstractPaymentGateway
{
    public static  final String GATEWAY_TYPE="jetonvouch";
    final static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.jeton");
    TransactionLogger transactionLogger=new TransactionLogger(JetonVoucherPaymentGateway.class.getName());
    public JetonVoucherPaymentGateway(String accountId){this.accountId=accountId;}

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("JetonVoucherPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into processSale:::::");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        JetonResponseVO jetonResponseVO=new JetonResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String apiKey = gatewayAccount.getMerchantId();
        boolean isTest=gatewayAccount.isTest();
        Date date = new Date();
        String datetime = date.getTime() + "";
        String status="";
        String remark="";
        String descriptor="";
        String saleResponse="";

        try{
            String saleRequest = "{\n" +
                    "\"voucherNumber\":\""+cardDetailsVO.getVoucherNumber()+"\",\n" +
                    "\"expiryMonth\": \""+cardDetailsVO.getExpMonth()+"\",\n" +
                    "\"expiryYear\": \""+cardDetailsVO.getExpYear()+"\",\n" +
                    "\"securityCode\": \""+cardDetailsVO.getSecurity_Code()+"\",\n" +
                    "\"customerIpAddress\" : \""+addressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                    "\"currencyId\" : \""+JetonUtils.Currency_id(requestVO)+"\",\n" +
                    "\"amount\" : \""+transDetailsVO.getAmount()+"\"\n" +
                    "}";
            String saleRequestlog = "{\n" +
                    "\"voucherNumber\":\""+functions.maskingPan(cardDetailsVO.getVoucherNumber())+"\",\n" +
                    "\"expiryMonth\": \""+functions.maskingNumber(cardDetailsVO.getExpMonth())+"\",\n" +
                    "\"expiryYear\": \""+functions.maskingNumber(cardDetailsVO.getExpYear())+"\",\n" +
                    "\"securityCode\": \""+functions.maskingNumber(cardDetailsVO.getSecurity_Code())+"\",\n" +
                    "\"customerIpAddress\" : \""+addressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                    "\"currencyId\" : \""+JetonUtils.Currency_id(requestVO)+"\",\n" +
                    "\"amount\" : \""+transDetailsVO.getAmount()+"\"\n" +
                    "}";

            transactionLogger.error("-----sale request-----" + trackingID + "--" + saleRequestlog);
            if(isTest){
                saleResponse = JetonUtils.doPostHTTPSURLConnectionClient4(RB.getString("VOUCHER_REDEEM_URL_TEST"), saleRequest, trackingID, apiKey);
            }else{
                saleResponse = JetonUtils.doPostHTTPSURLConnectionClient4(RB.getString("VOUCHER_REDEEM_URL_LIVE"), saleRequest, trackingID, apiKey);
            }
            transactionLogger.error("-----sale response-----"+trackingID + "--" + saleResponse);
            if(saleResponse!=null){
                JSONObject jsonObject = new JSONObject(saleResponse);
                JSONObject jsonObject1 = jsonObject.getJSONObject("header");
                String statusCode = jsonObject1.getString("statusCode");

                if("200".equals(statusCode)){
                    status="success";
                    remark="Transaction Successful";
                    descriptor=gatewayAccount.getDisplayName();

                }else{
                    JSONArray jsonArray=jsonObject1.getJSONArray("errors");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject2= jsonArray.getJSONObject(i);
                        String errorMessage= jsonObject2.getString("errorMessage");
                        status="failed("+errorMessage+")";
                        remark ="Transaction Failed";
                    }
                }
                jetonResponseVO.setStatus(status);
                jetonResponseVO.setRemark(remark);
                jetonResponseVO.setDescriptor(descriptor);
                jetonResponseVO.setDescription(remark);
                jetonResponseVO.setPaymentId(trackingID);
                jetonResponseVO.setResponseTime(datetime);
                jetonResponseVO.setTransactionId(trackingID);
                jetonResponseVO.setTransactionStatus(status);
                jetonResponseVO.setTransactionType("sale");
                jetonResponseVO.setAmount(transDetailsVO.getAmount());
                jetonResponseVO.setIpaddress(addressDetailsVO.getCardHolderIpAddress());
            }
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonVoucherPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return jetonResponseVO;
    }
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonVoucherPaymentGateway.class.getName(),"processCapture()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonVoucherPaymentGateway.class.getName(),"processRefund()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }
    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonVoucherPaymentGateway.class.getName(),"processInquiry()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonVoucherPaymentGateway.class.getName(), "processVoid()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonVoucherPaymentGateway.class.getName(), "processPayout()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
