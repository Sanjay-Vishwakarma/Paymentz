package com.payment.tapmio;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.JsonObject;
import com.manager.PaymentManager;
import com.payment.bhartiPay.BhartiPayPaymentProcess;
import com.payment.bhartiPay.BhartiPayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.xpate.XPateUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 5/14/2020.
 */
public class TapMioPaymentGateway extends AbstractPaymentGateway
{

    private static TransactionLogger transactionlogger = new TransactionLogger(TapMioPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "tapmio";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Tapmio");

    CommonValidatorVO commonValidatorVO =new CommonValidatorVO();
    String paymentRequestId="";
    String TapmioAuthentication="";
    public TapMioPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("TapMioPaymentGateway  accountid------------------->"+accountId);
     }


    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("TapMioPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of TapMioPaymentGateway......");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        TapMioUtils tapmioutils = new TapMioUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand= transactionDetailsVO.getCardType();
        String payment_Card= GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String payment_url="";
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        boolean isTest = gatewayAccount.isTest();

        String CURRENCY= transactionDetailsVO.getCurrency();
        transactionlogger.error("CURRENCY is-------------->"+CURRENCY);
        String AMOUNT=transactionDetailsVO.getAmount();
        transactionlogger.error("AMOUNT is-------------->"+AMOUNT);
        String ORDER_ID=trackingID;
        transactionlogger.error("ORDER_ID is-------------->"+ORDER_ID);
        String ORDERDesc=transactionDetailsVO.getOrderDesc();
        transactionlogger.error("ORDERDesc is-------------->"+ORDERDesc);
        String APIKey=gatewayAccount.getFRAUD_FTP_USERNAME();
        transactionlogger.error("APIKey is-------------->"+APIKey);
        String APISecret=gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionlogger.error("APISecret is-------------->"+APISecret);
        String returnUrl= RB.getString("TAPMIO_RU")+trackingID;;
        transactionlogger.error("returnUrl is-------------->"+returnUrl);
         TapmioAuthentication= APIKey+":"+APISecret;
        transactionlogger.error("TapmioAuthentication is-------------->"+TapmioAuthentication);

        String securePaymentGateway="";

        if (isTest){
            payment_url=RB.getString("TEST_SALE_URL");
            transactionlogger.error("TEST_SALE_URL------------------------->"+payment_url);


        }
        else {
            payment_url=RB.getString("LIVE_SALE_URL");
            transactionlogger.error("LIVE_SALE_URL------------------------->"+payment_url);
        }

        String request=payment_url+"description="+ORDERDesc +"&paymentAmount="+AMOUNT+"&currency="+CURRENCY+"&returnUrl="+returnUrl;
        transactionlogger.error("request is---------for--"+trackingID+"--"+request);
       String response= tapmioutils.doHttpPostConnection(request, TapmioAuthentication);
        transactionlogger.error("response--------------for--"+trackingID+"--"+response);

        try
        {


            JSONObject jsonobj =new JSONObject(response);
            if (jsonobj.has("securePaymentGateway")){
                commResponseVO.setStatus("pending3DConfirmation");
            }
            else {
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("fail");
                //commResponseVO.setrem(jsonobj.getString("securePaymentGateway"));
            }
            paymentRequestId= jsonobj.getString("paymentRequestId");
            securePaymentGateway= jsonobj.getString("securePaymentGateway");
            commResponseVO.setTransactionId(paymentRequestId);
            transactionlogger.error("paymentRequestId---------------------------->"+paymentRequestId);
        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException------------------------->",e);
        }



        commResponseVO.setUrlFor3DRedirect(securePaymentGateway);

        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in TapMio ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;
        TapMioUtils tapmioutils = new TapMioUtils();
        TapMioPaymentProcess tapmioPaymentProcess = new TapMioPaymentProcess();
        CommRequestVO commRequestVO = tapmioutils.getTapMioRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                 html = tapmioPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                 transactionlogger.error("automatic redirect tapmio form -- >>"+html);
            }
            transactionlogger.error("paymentid--------------------->"+transRespDetails.getTransactionId());

            tapmioutils.updateTransaction(commonValidatorVO.getTrackingid(),transRespDetails.getTransactionId());
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in TapMioPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingID, GenericRequestVO requestVO)
    {
         transactionlogger.error("Entering processSale of TapMioPaymentGateway......");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        TapMioUtils tapmioutils = new TapMioUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        transactionlogger.error("CommTransactionDetailsVO------------------- ----->"+transactionDetailsVO);
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        transactionlogger.error("inside process3DAuthConfirmation ----- ");
        transactionlogger.error("accountId is-------------->"+accountId);

        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions = new Functions();
        String status="";
        String payment_url=RB.getString("FINAL_RESPONSE");
        String APIKey=gatewayAccount.getFRAUD_FTP_USERNAME();
        transactionlogger.error("APIKey is-------------->"+APIKey);
        String APISecret=gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionlogger.error("APISecret is-------------->"+APISecret);
        String returnUrl= RB.getString("TAPMIO_RU")+trackingID;;
        transactionlogger.error("returnUrl is-------------->"+returnUrl);
        TapmioAuthentication= APIKey+":"+APISecret;
        transactionlogger.error("TapmioAuthentication is-------------->"+TapmioAuthentication);


        try
        {


            String request=payment_url+"paymentId="+commRequestVO.getTransDetailsVO().getPreviousTransactionId();
            transactionlogger.error("process3DSaleConfirmation request ------------------------> " + request);
            String response= tapmioutils.doHttpPostConnection(request, TapmioAuthentication);
            transactionlogger.error("process3DSaleConfirmation response -------------------------> " + response);

            if (response != null)
            {
                commResponseVO.setTransactionId(commRequestVO.getTransDetailsVO().getPreviousTransactionId());
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("paymentStatus"))
                {
                    status=jsonObject.getString("paymentStatus");
                }

                    if (status.equalsIgnoreCase("success"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("transaction successful");
                        commResponseVO.setDescription("transaction successful");
                    }

                    else if(status.equalsIgnoreCase("pending")){
                        commResponseVO.setStatus("pending");
                        commResponseVO.setRemark("transaction is pending");
                        commResponseVO.setDescription("transaction is pending");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("fail");
                        commResponseVO.setDescription("fail");
                    }


            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Sys:Transaction Fail No Response");
                commResponseVO.setDescription("Sys:Transaction Fail No Response");
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception-----", e);
        }
        return commResponseVO;
    }



    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
