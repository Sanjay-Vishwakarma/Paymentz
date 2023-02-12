package com.payment.smartcode;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.manager.vo.ProductDetailsVO;
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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class SmartCodePayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(SmartCodePayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "smartcode";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.smartcodepay");
    private final static ResourceBundle RB_WL           = LoadProperties.getProperty("com.directi.pg.PGWALLETS");
    private final  static ResourceBundle rbAmount       = LoadProperties.getProperty("com.directi.pg.puma");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public SmartCodePayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of SmartCodePayPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        SmartCodePayUtils payGUtils                     = new SmartCodePayUtils();
        boolean isTest                                  = gatewayAccount.isTest();
        JSONObject requestJSON                          = new JSONObject();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();


        String REQUEST_URL      = "";
        String Merchant_Id      = "";
        String Order_Id         = "";
        String Amount           = "";
        String CUST_NAME        = "";
        String CUST_EMAIL       = "";
        String CUST_MOBILE      = "";
        String PAY_MODE         = "UPI";
        String UPI_CHANNEL      = "INTENT";
        String VPA              = commRequestVO.getCustomerId();
        String RETURN_URL       = "";
        String HASH             = "";
        String API_KEY          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String str              = "";
        String response         = "";
        String request          = "";


        try{
            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                RETURN_URL = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingID;
            else
                RETURN_URL = RB.getString("REDIRECT_URL") + trackingID;

            if(isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else{
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }

            Merchant_Id = gatewayAccount.getMerchantId();
            Order_Id    = trackingID;
            Amount      = transactionDetailsVO.getAmount();

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                CUST_NAME  = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            else {
                CUST_NAME  = "customer";
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
                CUST_EMAIL   = commAddressDetailsVO.getEmail();
            else {
                CUST_EMAIL  = "customer@gmail.com";
            }

            if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                CUST_MOBILE =   commAddressDetailsVO.getPhone();
            }
            else
            {
                CUST_MOBILE =   "9999999999";
            }

             str = "AMOUNT="+Amount+"|CUST_EMAIL="+CUST_EMAIL+"|CUST_MOBILE="+CUST_MOBILE+"|CUST_NAME="+CUST_NAME+"|MERCHANT_ID="+Merchant_Id+"|ORDER_ID="+Order_Id+/*"|PAY_MODE="+PAY_MODE+*/"|RETURN_URL="+RETURN_URL/*+"|UPI_CHANNEL="+UPI_CHANNEL+"|VPA="+VPA*/;

            HASH = SmartCodePayUtils.getHmac256Signature(str,API_KEY.getBytes());

            request = "MERCHANT_ID=" + Merchant_Id + "&ORDER_ID=" + Order_Id + "&AMOUNT=" + Amount + "&CUST_NAME=" + CUST_NAME + "&CUST_EMAIL=" + CUST_EMAIL + "&CUST_MOBILE=" + CUST_MOBILE + "&PAY_MODE=" + PAY_MODE + "&UPI_CHANNEL=" + UPI_CHANNEL + "&VPA=" + VPA
                    + "&RETURN_URL=" + RETURN_URL + "&HASH=" + HASH;

            transactionlogger.error("processSale() Request ---->" + trackingID + " RequestURL:"+REQUEST_URL+ " RequestParameters" +request.toString());
            response = SmartCodePayUtils.doPostHttpUrlConnection(REQUEST_URL,request,trackingID);
            transactionlogger.error("processSale() Response--" + trackingID + "--->" + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String respCode = "";
                String respStatus = "";
                String ORDER_ID = "";
                String StatusCode = "";
                String GatewayTID = "";
                String IntentURL = "";
                String respAmount = "";
                String Message = "";

                JSONObject jsonObject = new JSONObject(response);
                JSONObject data = new JSONObject();

                if (jsonObject != null)
                {

                    if (jsonObject.has("respCode"))
                    {
                        respCode = jsonObject.getString("respCode");
                    }

                    if (jsonObject.has("respStatus"))
                    {
                        respStatus = jsonObject.getString("respStatus");
                    }

                    if (jsonObject.has("data"))
                    {
                        data = jsonObject.getJSONObject("data");

                        if (data.has("ORDER_ID"))
                        {
                            ORDER_ID = data.getString("ORDER_ID");
                        }
                        if (data.has("GatewayTID"))
                        {
                            GatewayTID = data.getString("GatewayTID");
                        }
                        if (data.has("IntentURL"))
                        {
                            IntentURL = data.getString("IntentURL");
                        }
                        if (data.has("Amount"))
                        {
                            respAmount = data.getString("Amount");
                        }
                        if (data.has("Message"))
                        {
                            Message = data.getString("Message");
                        }

                        if("SUCCESS".equalsIgnoreCase(respStatus)/*&&"Transaction Initiated".equalsIgnoreCase(Message)*/)
                        {
                            comm3DResponseVO.setStatus("pending3DConfirmation");
                            comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                            comm3DResponseVO.setUrlFor3DRedirect(IntentURL);
                            comm3DResponseVO.setTransactionId(GatewayTID);
                            comm3DResponseVO.setRemark(Message);
                            comm3DResponseVO.setDescription(Message);
                        }
                        else if("FAILED".equalsIgnoreCase(respStatus))
                        {
                            comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setTransactionStatus("failed");
                            comm3DResponseVO.setRemark(Message);
                            comm3DResponseVO.setDescription(Message);
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setTransactionStatus("pending");
                        }


                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }

                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }

        }catch (Exception e){
            transactionlogger.error("SmartCodePaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in SmartCodePay ---- ");
        String html                                 = "";
        Comm3DResponseVO transRespDetails           = null;
        SmartCodePayUtils smartCodePayUtils                         = new SmartCodePayUtils();
        SmartCodePayPaymentProcess payGPaymentProcess   = new SmartCodePayPaymentProcess();
        CommRequestVO commRequestVO             = smartCodePayUtils.getSmartCodeRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(accountId);

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
                html = payGPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect SmartCodePay form -- >>"+commonValidatorVO.getTrackingid() +" " +html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in SmartCodePayPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of SmartCode---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        SmartCodePayUtils smartCodePayUtils                     = new SmartCodePayUtils();
        StringBuffer parameters                                 = new StringBuffer();
        JSONObject requestJSON                                  = new JSONObject();


        String Merchant_Id = gatewayAccount.getMerchantId();
        String API_KEY     = gatewayAccount.getFRAUD_FTP_USERNAME();
        String orderKeyID  = commTransactionDetailsVO.getPreviousTransactionId();
        String REQUEST_URL = "";
        String inquiry_res = "";


        try
        {
            String AuthenticationToken  = Merchant_Id+":"+API_KEY;
            String authentication = new String(Base64.encode(AuthenticationToken.getBytes("utf-8")));

            requestJSON.put("ORDER_ID",orderKeyID);


            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_STATUS_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("TEST_STATUS_URL");
            }

            transactionlogger.error("inquiry req is --> "+trackingId+" "+REQUEST_URL+ " ----> "+requestJSON.toString());

            inquiry_res = smartCodePayUtils.doGetHTTPSURLConnectionClient(REQUEST_URL,requestJSON.toString(),authentication);
            transactionlogger.error("inquiry res is -- > "+trackingId+" "+inquiry_res);

            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String respCode   = "";
                String respStatus = "";
                String ORDER_ID   = "";
                String StatusCode = "";
                String GatewayTID = "";
                String TxnStatus  = "";
                String Amount     = "";
                String BankRefNum = "";
                String Message    = "";

                JSONObject jsonObject = new JSONObject(inquiry_res);
                JSONObject data       = new JSONObject();

                if(jsonObject != null){

                    if (jsonObject.has("respCode"))
                    {
                        respCode = jsonObject.getString("respCode");
                    }

                    if (jsonObject.has("respStatus"))
                    {
                        respStatus = jsonObject.getString("respStatus");
                    }

                    if (jsonObject.has("data"))
                    {
                        data = jsonObject.getJSONObject("data");

                        if(data.has("ORDER_ID"))
                        {
                            ORDER_ID = data.getString("ORDER_ID");
                        }
                        if(data.has("GatewayTID"))
                        {
                            GatewayTID = data.getString("GatewayTID");
                        }
                        if(data.has("StatusCode"))
                        {
                            StatusCode = data.getString("StatusCode");
                        }
                        if(data.has("TxnStatus"))
                        {
                            TxnStatus = data.getString("TxnStatus");
                        }
                        if(data.has("Amount"))
                        {
                            Amount = data.getString("Amount");
                        }
                        if(data.has("Message"))
                        {
                            Message = data.getString("Message");
                        }
                        if(data.has("BankRefNum"))
                        {
                            BankRefNum = data.getString("BankRefNum");
                        }

                        if ("0".equalsIgnoreCase(StatusCode) && "SUCCESS".equalsIgnoreCase(TxnStatus))
                        {
                            commResponseVO.setStatus("SUCCESS");
                            commResponseVO.setTransactionStatus("SUCCESS");
                            commResponseVO.setTransactionId(GatewayTID);
                            commResponseVO.setAmount(Amount);
                            commResponseVO.setResponseHashInfo(BankRefNum);
                            commResponseVO.setRemark(Message);
                            commResponseVO.setDescription(Message);
                        }
                        else if("1".equalsIgnoreCase(StatusCode) && "FAILED".equalsIgnoreCase(TxnStatus))
                        {
                            commResponseVO.setStatus("FAILED");
                            commResponseVO.setTransactionStatus("FAILED");
                            commResponseVO.setTransactionId(GatewayTID);
                            commResponseVO.setAmount(Amount);
                            commResponseVO.setResponseHashInfo(BankRefNum);
                            commResponseVO.setRemark(Message);
                            commResponseVO.setDescription(Message);
                        }
                        else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setTransactionStatus("pending");
                        }

                    }
                }else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }
            // no response set pending
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (JSONException e)
        {
            transactionlogger.error("PayGPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("PayGPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }


}