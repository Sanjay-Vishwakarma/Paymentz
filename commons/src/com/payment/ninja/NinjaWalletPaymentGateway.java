package com.payment.ninja;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import java.util.*;

import java.util.ResourceBundle;

/**
 * Created by Admin on 5/28/2019.
 */
public class NinjaWalletPaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(NinjaWalletPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(NinjaWalletPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE="ninja";
    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.ninjawallet");
    Functions functions = new Functions();
    String url="https://sandbox.ninjawallet.com/v1/payment-request/credit-card";

    public NinjaWalletPaymentGateway(String accountId)
    {
        this.accountId = accountId;

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        if (isTest)
        {
            url = RB.getString("TEST_URL");
        }
        else
        {
            url = RB.getString("LIVE_URL");
        }
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        logger.error("Inside NinjaWallet Sale-----");
        transactionLogger.error("Inside NinjaWallet Sale-----");
        Comm3DResponseVO comm3DResponseVO1= new Comm3DResponseVO();
        GatewayAccount gatewayaccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayaccount.isTest();
        String is3DSupported = gatewayaccount.get_3DSupportAccount();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO)requestVO).getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = ((CommRequestVO)requestVO).getAddressDetailsVO();
        String ipn_url=RB.getString("IPN_URL")+trackingID;
        String token=gatewayaccount.getFRAUD_FTP_PATH();
        String merchantcode=gatewayaccount.getMerchantId();

        transactionLogger.error("MerchantCode----"+merchantcode);
        transactionLogger.error("token----"+token);
        transactionLogger.error("is3DSupported----"+is3DSupported);

        String url="";
        String mid = "";
        if (isTest) {
            transactionLogger.error("Inside 3D Test URL-----");
            url=RB.getString("TEST_URL")+"payment-request/credit-card";
        }
        else {
            transactionLogger.error("Inside 3d Live URL-----");
            url=RB.getString("LIVE_URL")+"payment-request/credit-card";
            mid = "\"merchant_code\": \""+merchantcode+"\",\n";
        }

        try
        {
            String cardrequest="{" +
                    "\"customer_email\": \""+addressDetailsVO.getEmail()+"\",\n" +
                    "\"total\": \""+transactionDetailsVO.getAmount()+"\",\n" +
                    "\"currency\": \""+transactionDetailsVO.getCurrency()+"\",\n" +
                    "\"reference\": \""+trackingID+"\",\n" +
                    "\"success_url\": \""+RB.getString("TERM_URL")+trackingID+"&status=success"+"\",\n" +
                    "\"cancel_url\": \""+RB.getString("TERM_URL")+trackingID+"&status=failed"+"\",\n" +
                    "\"ipn_url\": \""+ipn_url+"\",\n" +
                    mid+
                    "\"credit_card\": {" +
                    "\"name\": \""+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"\",\n" +
                    "\"number\": \""+cardDetailsVO.getCardNum()+"\",\n" +
                    "\"expiry_month\": \""+cardDetailsVO.getExpMonth()+"\",\n" +
                    "\"expiry_year\": \""+cardDetailsVO.getExpYear()+"\",\n" +
                    "\"cvv\": \""+cardDetailsVO.getcVV()+"\"\n" +
                    "},\n" +
                    "\"billing\": {" +
                    "\"address\": \""+addressDetailsVO.getStreet()+"\",\n" +
                    "\"country\": \""+addressDetailsVO.getCountry()+"\",\n" +
                    "\"zip\": \""+addressDetailsVO.getZipCode()+"\",\n" +
                    "\"city\": \""+addressDetailsVO.getCity()+"\",\n" +
                    "\"state\": \""+addressDetailsVO.getState()+"\"\n" +
                    "}\n" +
                    "}";

            transactionLogger.error("CardRequest for sale-----"+cardrequest);

            String cardresponse = NinjaWalletUtils.doPostHTTPSURLConnectionClient(url,token,cardrequest);

            transactionLogger.error("CardResponse for sale-----"+cardresponse);

            if(functions.isValueNull(cardresponse) && cardresponse.contains("{"))
            {
                String message="";
                String error="";
                JSONObject jsonObject = new JSONObject(cardresponse);
                if (jsonObject!=null){
                    String id="";
                    String total="";
                    String currency="";
                    String reference="";
                    String customer_email="";
                    String item_name="";
                    String item_description="";
                    String status="";
                    String reason="";
                    String descriptor="";
                    String redirect_url="";
                    String success_url1="";
                    String cancel_url1="";
                    String ipn_url1="";
                    String merchant_code="";
                    String created_at="";
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                        transactionLogger.error("Message----" + message);
                    }
                    if (jsonObject.has("errors"))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("errors");
                        Iterator i = jsonObject1.keys();

                        while(i.hasNext())
                        {
                            String key = (String) i.next();
                            transactionLogger.error(" key-----" + key + "Value-----" + jsonObject1.getJSONArray(key));
                            error=jsonObject1.getJSONArray(key).toString();
                        }
                    }
                    if (jsonObject.has("data"))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        if (jsonObject1.has("id"))
                        {
                            id=jsonObject1.getString("id");
                            transactionLogger.error("id-----"+id);
                        }
                        if (jsonObject1.has("total"))
                        {
                            total=jsonObject1.getString("total");
                            transactionLogger.error("total-----"+total);
                        }
                        if (jsonObject1.has("currency"))
                        {
                            currency=jsonObject1.getString("currency");
                            transactionLogger.error("currency-----"+currency);
                        }
                        if (jsonObject1.has("reference"))
                        {
                            reference=jsonObject1.getString("reference");
                            transactionLogger.error("reference-----"+reference);
                        }
                        if (jsonObject1.has("customer_email"))
                        {
                            customer_email=jsonObject1.getString("customer_email");
                            transactionLogger.error("customer_email-----"+customer_email);
                        }
                        if (jsonObject1.has("item_name"))
                        {
                            item_name= jsonObject1.getString("item_name");
                            transactionLogger.error("item_name-----"+item_name);
                        }
                        if (jsonObject1.has("item_description"))
                        {
                            item_description=jsonObject1.getString("item_description");
                            transactionLogger.error("item_description-----"+item_description);
                        }
                        if (jsonObject1.has("status"))
                        {
                            status=jsonObject1.getString("status");
                            transactionLogger.error("status-----"+status);
                        }
                        if(jsonObject1.has("reason"))
                        {
                            reason=jsonObject1.getString("reason");
                            transactionLogger.error("reason-----"+reason);
                        }
                        if(jsonObject1.has("descriptor"))
                        {
                            descriptor=jsonObject1.getString("descriptor");
                            transactionLogger.error("descriptor-----"+descriptor);
                        }
                        if (jsonObject1.has("redirect_url"))
                        {
                            redirect_url=jsonObject1.getString("redirect_url");
                            transactionLogger.error("redirect_url-----"+redirect_url);
                        }
                        if (jsonObject1.has("success_url1"))
                        {
                            success_url1=jsonObject1.getString("success_url1");
                            transactionLogger.error("success_url1-----"+success_url1);
                        }
                        if (jsonObject1.has("cancel_url1"))
                        {
                            cancel_url1=jsonObject1.getString("cancel_url1");
                            transactionLogger.error("cancel_url1-----"+cancel_url1);
                        }
                        if(jsonObject1.has("ipn_url1"))
                        {
                            ipn_url1=jsonObject1.getString("ipn_url1");
                            transactionLogger.error("ipn_url1-----"+ipn_url1);
                        }
                        if (jsonObject1.has("merchant_code"))
                        {
                            merchant_code=jsonObject1.getString("merchant_code");
                            transactionLogger.error("merchant_code-----"+merchant_code);
                        }
                        if (jsonObject1.has("created_at"))
                        {
                            created_at=jsonObject1.getString("created_at");
                            transactionLogger.error("created_at-----"+created_at);
                        }

                        if (status.equalsIgnoreCase("COMPLETE"))
                        {
                            transactionLogger.error("Inside complete----------");
                            comm3DResponseVO1.setStatus("success");
                            comm3DResponseVO1.setRemark(status);
                            comm3DResponseVO1.setDescription("SYS: The payment request has been paid successfully.");
                            comm3DResponseVO1.setDescriptor(descriptor);
                            comm3DResponseVO1.setTransactionId(id);
                            comm3DResponseVO1.setAmount(total);
                            comm3DResponseVO1.setResponseTime(created_at);
                            comm3DResponseVO1.setMerchantId(merchant_code);
                        }
                        else if(status.equalsIgnoreCase("PENDING") && functions.isValueNull(redirect_url))
                        {
                            transactionLogger.error("Inside pending----------");
                            transactionLogger.error("TERM URL----"+url + trackingID);
                            comm3DResponseVO1.setStatus("pending3DConfirmation");
                            comm3DResponseVO1.setUrlFor3DRedirect(redirect_url);
                            comm3DResponseVO1.setMethod("GET");
                            comm3DResponseVO1.setTerURL(url + trackingID);
                            comm3DResponseVO1.setRemark(status);
                            comm3DResponseVO1.setDescription("SYS: The payment request is pending 3rd party verification.");
                            comm3DResponseVO1.setDescriptor(descriptor);
                            comm3DResponseVO1.setTransactionId(id);
                            comm3DResponseVO1.setAmount(total);
                            comm3DResponseVO1.setResponseTime(created_at);
                            comm3DResponseVO1.setMerchantId(merchant_code);
                        }
                        else
                        {
                            transactionLogger.error("inside fail-------------");
                            comm3DResponseVO1.setStatus("fail");
                            comm3DResponseVO1.setRemark(status);
                            comm3DResponseVO1.setDescription(reason);
                            comm3DResponseVO1.setDescriptor(descriptor);
                            comm3DResponseVO1.setTransactionId(id);
                            comm3DResponseVO1.setAmount(total);
                            comm3DResponseVO1.setResponseTime(created_at);
                            comm3DResponseVO1.setMerchantId(merchant_code);
                        }
                    }
                    else
                    {
                        transactionLogger.error("inside fail1-------------");
                        comm3DResponseVO1.setStatus("fail");
                        comm3DResponseVO1.setRemark(message);
                        comm3DResponseVO1.setDescription(message+error);
                    }
                }
                else
                {
                    comm3DResponseVO1.setStatus("fail");
                    comm3DResponseVO1.setRemark("fail");
                    comm3DResponseVO1.setDescription("fail");
                }
            }
            else
            {
                comm3DResponseVO1.setStatus("fail");
                comm3DResponseVO1.setRemark("fail");
                comm3DResponseVO1.setDescription("fail");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return comm3DResponseVO1;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("NinjaWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. Support Team",null);
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.error("Inside NinjaWallet Refund-----");
        transactionLogger.error("Inside NinjaWallet Refund-----");
        GatewayAccount gatewayaccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayaccount.isTest();
        CommResponseVO commresponseVO=new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        String token=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();

        String url="";
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL-----");
            url=RB.getString("TEST_URL")+"refund";
        }
        else
        {
            transactionLogger.error("Inside LIVE URL-----");
            url=RB.getString("LIVE_URL")+"refund";
        }

        String captureamount=transactionDetailsVO.getPreviousTransactionAmount();
        transactionLogger.error("Capture Amount-----"+captureamount);

        String amount= transactionDetailsVO.getAmount();
        transactionLogger.error("Amount-----"+amount);

        if(!captureamount.equals(amount))
        {
            PZExceptionHandler.raiseTechnicalViolationException(NinjaWalletPaymentGateway.class.getName(), "processRefund()", null, "common", "Partial Refund is Not Supported For This Bank", PZTechnicalExceptionEnum.NOT_ALLOWED_BY_GATEWAY, null, "Partial Refund is Not Supported For This Bank.", new Throwable("Partial Refund is Not Supported For This Bank."));
        }

        try
        {
            String refundrequest="{\n" +
                    "\"total\": \""+transactionDetailsVO.getAmount()+"\",\n" +
                    "\"payment_request_id\": \""+transactionDetailsVO.getPreviousTransactionId()+"\",\n" +
                    "\"reason\": \"Item returned\"\n" +
                    "}";

            transactionLogger.error("RefundRequest for refund-----"+refundrequest);

            String refundresponse = NinjaWalletUtils.doPostHTTPSURLConnectionClient(url,token,refundrequest);

            transactionLogger.error("RefundResponse for refund-----"+refundresponse);

            if (functions.isValueNull(refundresponse) && refundresponse.contains("{"))
            {
                String message="";
                JSONObject jsonObject = new JSONObject(refundresponse);
                if (jsonObject!=null)
                {
                    String id="";
                    String total="";
                    String reason="";
                    String status="";
                    if (jsonObject.has("message"))
                    {
                        message=jsonObject.getString("message");
                        transactionLogger.error("Message----"+message);
                    }
                    if (jsonObject.has("data"))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        if (jsonObject1.has("id"))
                        {
                            id=jsonObject1.getString("id");
                            transactionLogger.error("id-----"+id);
                        }
                        if (jsonObject1.has("total"))
                        {
                            total=jsonObject1.getString("total");
                            transactionLogger.error("total-----"+total);
                        }
                        if (jsonObject1.has("reason"))
                        {
                            reason=jsonObject1.getString("reason");
                            transactionLogger.error("reason-----"+reason);
                        }
                        if (jsonObject1.has("status"))
                        {
                            status=jsonObject1.getString("status");
                            transactionLogger.error("status-----"+status);
                        }
                        if (status.equalsIgnoreCase("COMPLETED"))
                        {
                            transactionLogger.error("Inside Success-------");
                            commresponseVO.setStatus("success");
                            commresponseVO.setRemark(status);
                            commresponseVO.setDescription(reason);
                            commresponseVO.setTransactionId(id);
                            commresponseVO.setAmount(total);
                        }
                        else
                        {
                            transactionLogger.error("Inside fail-------");
                            commresponseVO.setStatus("fail");
                            commresponseVO.setRemark(status);
                            commresponseVO.setDescription(reason);
                            commresponseVO.setTransactionId(id);
                            commresponseVO.setAmount(total);
                        }
                    }
                    else
                    {
                        commresponseVO.setStatus("fail");
                        commresponseVO.setRemark(message);
                        commresponseVO.setDescription(message);
                    }
                }
                else
                {
                    commresponseVO.setStatus("fail");
                    commresponseVO.setRemark("fail");
                    commresponseVO.setDescription("fail");
                }
            }
            else
            {
                commresponseVO.setStatus("fail");
                commresponseVO.setRemark("fail");
                commresponseVO.setDescription("fail");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commresponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
