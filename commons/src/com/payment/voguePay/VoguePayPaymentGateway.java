package com.payment.voguePay;

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
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONObject;
import java.net.URLEncoder;
import java.util.ResourceBundle;

/**
 * Created by jeet on 25-07-2019.
 */
public class VoguePayPaymentGateway extends AbstractPaymentGateway
{
    TransactionLogger transactionLogger = new TransactionLogger(VoguePayPaymentGateway.class.getName());
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.voguepay");
    public static final String GATEWAY_TYPE = "voguepay";


    public VoguePayPaymentGateway (String accountId)
    {
        this.accountId=accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("VoguePayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by VoguePay gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside VoguePay processSale ------>");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommResponseVO commResponseVO=new CommResponseVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String amount = transactionDetailsVO.getAmount();
        String currency = transactionDetailsVO.getCurrency();
        String orderDesc = transactionDetailsVO.getOrderDesc();
        String termUrl = "";
        String notificationUrl="";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }

        String response = "";

        try
        {
            String url ="";
            String data =
                            "&v_merchant_id=" +URLEncoder.encode(mid, "UTF-8") +
                            "&memo=" + URLEncoder.encode(orderDesc, "UTF-8") +
                            "&total=" + URLEncoder.encode(amount, "UTF-8") +
                            "&merchant_ref="+URLEncoder.encode(trackingID,"UTF-8")+
                            "&notify_url="+URLEncoder.encode(notificationUrl+trackingID,"UTF-8")+
                            "&success_url="+URLEncoder.encode(termUrl+trackingID+"&status=success","UTF-8")+
                            "&fail_url="+URLEncoder.encode(termUrl+trackingID+"&status=fail","UTF-8")+
                            "&cur="+ URLEncoder.encode(currency,"UTF-8");

            transactionLogger.error("data is --->"+data);
            String request = "";
            if (isTest)
            {
                transactionLogger.error("inside sale TestMode Voguepay ---->");
                url=RB.getString("SALE_TEST_URL");
                transactionLogger.error("SALE_TEST_URL --->"+url);
                request=url+data;
                transactionLogger.error("Sale Request --->"+request);
                response = VoguePayUtils.getHttpUrlConnection(request);
            }
            else
            {
                transactionLogger.error("inside sale LiveMode Voguepay ---->");
                url=RB.getString("SALE_LIVE_URL");
                transactionLogger.error("SALE_LIVE_URL --->"+url);
                request=url+data;
                transactionLogger.error("Sale Request --->"+request);
                response = VoguePayUtils.getHttpUrlConnection(request);
            }

            transactionLogger.error("sale Response ---->"+response);

            if (functions.isValueNull(response))
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setRedirectUrl(response);
                commResponseVO.setRemark("Sys:Transaction pending");
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Sys:Transaction fail");
                commResponseVO.setDescription("Response is null");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception During Process sale ----->",e);
        }
        return commResponseVO;
    }


    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("processAutoRedirect in VoguePay  ------>");
        String html="";
        VoguePayUtils voguePayUtils = new VoguePayUtils();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = voguePayUtils.getVoguePayUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                html = VoguePayUtils.getRedirectForm(commonValidatorVO.getTrackingid(), transRespDetails);
                transactionLogger.error("Html in processAutoRedirect -------" + html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in VoguePay ------->",e);
        }

        return html;
    }


    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside VoguePayInquiry ----->");

        Functions functions =new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String transaction_Id="";
        String request="";

        try
        {
            transaction_Id = transactionDetailsVO.getPreviousTransactionId();
            transactionLogger.error("transaction_Id ----->"+transaction_Id);

            String response="";
            if (isTest)
            {
                transactionLogger.error("inside Inquiry test mode ----->");
                request = RB.getString("INQUIRY_URL")+transaction_Id+"&type=json&demo=true";
                transactionLogger.error("Inquiry request ----->"+request);
            }
            else
            {
                transactionLogger.error("inside Inquiry Live mode ----->");
                request = RB.getString("INQUIRY_URL")+transaction_Id+"&type=json";
                transactionLogger.error("Inquiry request ----->"+request);
            }
            response = VoguePayUtils.getHttpUrlConnection(request);
            transactionLogger.error("Inquiry response ----->"+response);

            String transaction_id ="";
            String merchant_id="";
            String total_amount="";
            String merchant_ref="";
            String memo ="";
            String status="";
            String response_code="";
            String responseTime="";
            String response_message="";

            if (functions.isValueNull(response)&&response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject!= null)
                {
                    if (jsonObject.has("transaction_id"))
                    {
                        transaction_id = jsonObject.getString("transaction_id");
                    }
                    if (jsonObject.has("merchant_id"))
                    {
                        merchant_id = jsonObject.getString("merchant_id");
                    }
                    if (jsonObject.has("total_amount"))
                    {
                        total_amount = jsonObject.getString("total_amount");
                    }
                    if (jsonObject.has("merchant_ref"))
                    {
                        merchant_ref = jsonObject.getString("merchant_ref");
                    }
                    if (jsonObject.has("memo"))
                    {
                        memo=jsonObject.getString("memo");
                    }
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                    }
                    if (jsonObject.has("response_code"))
                    {
                        response_code = jsonObject.getString("response_code");
                    }
                    if (jsonObject.has("date"))
                    {
                        responseTime=jsonObject.getString("date");
                    }
                    if (jsonObject.has("response_message"))
                    {
                        response_message = jsonObject.getString("response_message");
                    }

                    transactionLogger.error("transaction_id="+transaction_id+","+"merchant_id="+merchant_id+","+"total_amount="+total_amount+","+"merchant_ref="+merchant_ref+","+"memo="+memo+","+"status="+status+","+"response_code="+response_code+","+"responseTime="+responseTime+","+"response_message="+response_message);

                    if (status.equalsIgnoreCase("Approved") && response_code.equals("00") )
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(memo);
                    commResponseVO.setAuthCode(response_code);
                    commResponseVO.setTransactionId(transaction_id);
                    commResponseVO.setCurrency(transactionDetailsVO.getCurrency());
                    commResponseVO.setAmount(total_amount);
                    commResponseVO.setBankTransactionDate(responseTime);
                    commResponseVO.setMerchantId(merchant_id);
                    commResponseVO.setDescription(response_message);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("JSON value is null");
                }
            }
            else
            {
                transactionLogger.error("Inside else");
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Sys:Transaction fail");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("inquiry method request error ---->",e);
        }
        return commResponseVO;
    }



    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
