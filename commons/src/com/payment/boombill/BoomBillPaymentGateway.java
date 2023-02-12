package com.payment.boombill;

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
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import com.payment.boombill.BoomBillAccount;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2022-01-22.
 */
public class BoomBillPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE     = "boombill";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.BoomBill");

    private static TransactionLogger transactionLogger = new TransactionLogger(BoomBillPaymentGateway.class.getName());


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public BoomBillPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BoomBillPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of BoomBillPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        BoomBillAccount boomBillAccount                 = new BoomBillAccount();
        Hashtable dataHash                              = boomBillAccount.getValuesFromDb(accountId);


        String KEY          =  "";
        String last_name    =  "";
        String first_name   =  "";
        String email        =  "";
        String phone        =  "";
        String address      =  "";
        String city         =  "";
        String state        =  "";
        String country      =  "";
        String zip_code     =  "";
        String amount       =  "";
        String currency     =  "";
        String pay_by       =  "";
        String card_number  =  "";
        String card_name    =  "";
        String expiry_month =  "";
        String expiry_year  =  "";
        String cvv_code     =  "";
        String orderid      =  "";
        String clientip     =  "";
        String REQUEST_URL  =  "";
        String REDIRECT_URL =  "";
        String HeaderToken  =  "";



        JSONObject requestJSON = new JSONObject();
        JSONObject requestLogJson;

        try
        {
            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                REDIRECT_URL = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingID;
            else
                REDIRECT_URL = RB.getString("REDIRECT_URL") + trackingID;

            amount       =   transactionDetailsVO.getAmount();
            currency     =   transactionDetailsVO.getCurrency();
            email        =   commAddressDetailsVO.getEmail();
            first_name   =   commAddressDetailsVO.getFirstname();
            last_name    =   commAddressDetailsVO.getLastname();
            country      =   commAddressDetailsVO.getCountry();
            state        =   commAddressDetailsVO.getState();
            city         =   commAddressDetailsVO.getCity();
            address      =   commAddressDetailsVO.getStreet();
            zip_code     =   commAddressDetailsVO.getZipCode();
            phone        =   commAddressDetailsVO.getPhone();
            pay_by       =   BoomBillUtils.getPaymentBrand(transactionDetailsVO.getCardType());
            clientip     =   commAddressDetailsVO.getIp();
            card_number  =   commCardDetailsVO.getCardNum();
            expiry_month =   commCardDetailsVO.getExpMonth();
            expiry_year  =   commCardDetailsVO.getExpYear();
            cvv_code     =   commCardDetailsVO.getcVV();
            card_name    =   commAddressDetailsVO.getFirstname()+ " "+ commAddressDetailsVO.getLastname();
            orderid      =   trackingID;
            REQUEST_URL  = RB.getString("LIVE_SALE_URL");


            if(gatewayAccount.get_3DSupportAccount().equalsIgnoreCase("Y"))
            {
                KEY          =  (String) dataHash.get("3D-Key");
                HeaderToken  =  (String) dataHash.get("3D-HeaderToken");
            }
            else
            {

                KEY          =  (String) dataHash.get("Non3D-Key");
                HeaderToken  =  (String) dataHash.get("Non3D-HeaderToken");
            }



            requestJSON.put("key",KEY);
            requestJSON.put("last_name",last_name);
            requestJSON.put("first_name",first_name);
            requestJSON.put("email",email);
            requestJSON.put("phone",phone);
            requestJSON.put("address",address);
            requestJSON.put("city",city);
            requestJSON.put("state",state);
            requestJSON.put("country",country);
            requestJSON.put("zip_code",zip_code);
            requestJSON.put("amount",amount);
            requestJSON.put("currency",currency);
            requestJSON.put("pay_by",pay_by);
            requestJSON.put("card_number",card_number);
            requestJSON.put("card_name",card_name);
            requestJSON.put("expiry_month",expiry_month);
            requestJSON.put("expiry_year",expiry_year);
            requestJSON.put("cvv_code",cvv_code);
            requestJSON.put("orderid",orderid);
            requestJSON.put("clientip",clientip);
            requestJSON.put("redirect_url",REDIRECT_URL);

            String requestLog= requestJSON.toString();

            requestLogJson = new JSONObject(requestLog);
            requestLogJson.put("last_name",functions.maskingLastName(last_name));
            requestLogJson.put("first_name",functions.maskingFirstName(first_name));
            requestLogJson.put("email",functions.getEmailMasking(email));
            requestLogJson.put("card_number",functions.maskingPan(card_number));
            requestLogJson.put("card_name",functions.maskingFirstName(commAddressDetailsVO.getFirstname())+ " "+ functions.maskingLastName(commAddressDetailsVO.getLastname()));
            requestLogJson.put("expiry_month",functions.maskingNumber(expiry_month));
            requestLogJson.put("expiry_year",functions.maskingNumber(expiry_year));
            requestLogJson.put("cvv_code",functions.maskingNumber(cvv_code));

            transactionLogger.error("Request Log Parameters: "+trackingID+" "+requestLogJson.toString());

            transactionLogger.error("processSale() Request ---->" + trackingID + " RequestURL:"+REQUEST_URL+ " HeaderTOKEN:"+HeaderToken + " RequestParameters" +requestLogJson.toString());
            String response= BoomBillUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), HeaderToken, trackingID);
            transactionLogger.error("processSale() Response--" + trackingID + "--->" + response);

            if(functions.isValueNull(response) && BoomBillUtils.isJSONValid(response))
            {
                String success          = "";
                String message          = "";
                String GatewayResponse  = "";
                String PaymentId        = "";
                String Orderid          = "";
                String RedirectUrl      = "";
                String GatewayStatus    = "";
                String Descriptor       = "";

                JSONObject responseJson = new JSONObject(response);
                JSONObject dataJSON;

                if(responseJson.has("success") && functions.isValueNull(responseJson.getString("success")))
                {
                    success = responseJson.getString("success");
                }
                if(responseJson.has("message") && functions.isValueNull(responseJson.getString("message")))
                {
                    message = responseJson.getString("message");
                }

                if(responseJson.has("data"))
                {

                    if(BoomBillUtils.isJSONValid(responseJson.getString("data")))
                    {
                        dataJSON = responseJson.getJSONObject("data");
                        if (dataJSON.has("descriptor") && functions.isValueNull(dataJSON.getString("descriptor")))
                        {
                            Descriptor =   dataJSON.getString("descriptor");
                        }
                        if (dataJSON.has("gatewayResponse"))
                        {
                            GatewayResponse =   dataJSON.getString("gatewayResponse");
                        }
                        if (dataJSON.has("paymentId") && functions.isValueNull(dataJSON.getString("paymentId")))
                        {
                            PaymentId =   dataJSON.getString("paymentId");
                        }
                        if (dataJSON.has("orderid") && functions.isValueNull(dataJSON.getString("orderid")))
                        {
                            Orderid =   dataJSON.getString("orderid");
                        }
                        if (dataJSON.has("gatewayStatus") && functions.isValueNull(dataJSON.getString("gatewayStatus")))
                        {
                            GatewayStatus =   dataJSON.getString("gatewayStatus");
                        }
                        if (dataJSON.has("redirectUrl") && functions.isValueNull(dataJSON.getString("redirectUrl")))
                        {
                            RedirectUrl =   dataJSON.getString("redirectUrl");
                        }
                    }
                    else if (BoomBillUtils.isJSONARRAYValid(responseJson.getString("data")))
                    {
                        JSONArray dataArray =   responseJson.getJSONArray("data");
                    }
                }


                if("True".equalsIgnoreCase(success) && "APPROVED".equalsIgnoreCase(GatewayStatus))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark(message);
                    if(functions.isValueNull(Descriptor))
                    {
                        comm3DResponseVO.setDescriptor(Descriptor);
                    }
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setTransactionId(PaymentId);
                    comm3DResponseVO.setThreeDVersion("Non-3D");

                }
                else if("INITIATED".equalsIgnoreCase(GatewayStatus) &&  functions.isValueNull(RedirectUrl))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(RedirectUrl);
                    comm3DResponseVO.setTransactionId(PaymentId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                }
                else if ("DECLINED".equalsIgnoreCase(GatewayStatus) || "ERROR".equalsIgnoreCase(GatewayStatus))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                }
                else if ("false".equalsIgnoreCase(success) && functions.isValueNull(message) && !functions.isValueNull(GatewayStatus))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
                comm3DResponseVO.setMerchantId(KEY);
                comm3DResponseVO.setTransactionId(PaymentId);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(BoomBillPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of boombillPaymentGateway......");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        BoomBillAccount boomBillAccount                     = new BoomBillAccount();
        Hashtable dataHash                                  = boomBillAccount.getValuesFromDb(accountId);



        String REQUEST_URL  = "";
        String KEY          = "";
        String PaymentId    = "";
        String OrderId      = "";
        String HeaderToken  = "";


        JSONObject requestJSON = new JSONObject();

        try
        {
            OrderId            =    trackingID;
            PaymentId          =    commTransactionDetailsVO.getPreviousTransactionId();
            REQUEST_URL        =   RB.getString("LIVE_INQUIRY_URL");

            if(gatewayAccount.get_3DSupportAccount().equalsIgnoreCase("Y"))
            {
                KEY          =  (String) dataHash.get("3D-Key");
                HeaderToken  =  (String) dataHash.get("3D-HeaderToken");
            }
            else
            {

                KEY          =  (String) dataHash.get("Non3D-Key");
                HeaderToken  =  (String) dataHash.get("Non3D-HeaderToken");
            }


            requestJSON.put("key", KEY);
            requestJSON.put("orderid", OrderId);
            requestJSON.put("paymentId", PaymentId);

            transactionLogger.error("processInquery() Request ---->" + trackingID + " RequestURL:"+REQUEST_URL+ " HeaderTOKEN:"+HeaderToken+ " RequestParameters" +requestJSON.toString());
            String response =   BoomBillUtils.doPostHttpUrlConnection(REQUEST_URL,requestJSON.toString(),HeaderToken,trackingID);
            transactionLogger.error("processQuery() Response--" + trackingID + " " + response);

            if (functions.isValueNull(response) && functions.isJSONValid(response) )
            {
                String success          =  "";
                String message          = "";
                String gatewayStatus    =   "";
                String gatewayResponse  =   "";
                String descriptor       =   "";
                String responsePaymentId=   "";

                JSONObject responseJson = new JSONObject(response);
                JSONObject dataJSON    = null;
                JSONObject data        = null;


                if(responseJson.has("success") && functions.isValueNull(responseJson.getString("success")))
                {
                    success = responseJson.getString("success");
                }

                if(responseJson.has("message") && functions.isValueNull(responseJson.getString("message")))
                {
                    message = responseJson.getString("message");
                }

                if (responseJson.has("data"))
                {
                    if (BoomBillUtils.isJSONValid(responseJson.getString("data")))
                    {
                        dataJSON = responseJson.getJSONObject("data");

                        if (dataJSON != null)
                        {
                            if (dataJSON.has("descriptor"))
                            {
                                descriptor = dataJSON.getString("descriptor");
                            }
                            if (dataJSON.has("gatewayStatus"))
                            {
                                gatewayStatus = dataJSON.getString("gatewayStatus");
                            }
                            if (dataJSON.has("paymentId") && functions.isValueNull(dataJSON.getString("paymentId")))
                            {
                                responsePaymentId = dataJSON.getString("paymentId");
                            }
                            if (dataJSON.has("gatewayResponse"))
                            {
                                gatewayResponse = dataJSON.getString("gatewayResponse");
                            }
                        }
                    }else if (BoomBillUtils.isJSONARRAYValid(responseJson.getString("data")))
                    {
                        JSONArray dataArray =   responseJson.getJSONArray("data");
                    }
                }

                if("APPROVED".equalsIgnoreCase(gatewayStatus)|| "REFUND".equalsIgnoreCase(gatewayStatus))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark(message);

                    if(functions.isValueNull(descriptor))
                    {
                        comm3DResponseVO.setDescriptor(descriptor);
                    }
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                }

                else if ("DECLINED".equalsIgnoreCase(gatewayStatus) || "ERROR".equalsIgnoreCase(gatewayStatus))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(gatewayResponse);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
                 comm3DResponseVO.setMerchantId(KEY);
                 comm3DResponseVO.setTransactionId(responsePaymentId);
                comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());

            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BoomBillPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processRefund(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processRefund() of BoomBillGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        BoomBillAccount boomBillAccount                 = new BoomBillAccount();
        Hashtable dataHash                              = boomBillAccount.getValuesFromDb(accountId);


        String is3DSupportAccount                       = gatewayAccount.get_3DSupportAccount();
        String REQUEST_URL  = "";
        String KEY          = "";
        String PaymentId    = "";
        String OrderId      = "";
        String HeaderToken  = "";


        JSONObject requestJSON = new JSONObject();

        try
        {
            OrderId            =    trackingId;
            PaymentId          =    commTransactionDetailsVO.getPreviousTransactionId();
            REQUEST_URL        =   RB.getString("LIVE_REFUND_URL");

            if(gatewayAccount.get_3DSupportAccount().equalsIgnoreCase("Y"))
            {
                KEY          =  (String) dataHash.get("3D-Key");
                HeaderToken  =  (String) dataHash.get("3D-HeaderToken");
            }
            else
            {

                KEY          =  (String) dataHash.get("Non3D-Key");
                HeaderToken  =  (String) dataHash.get("Non3D-HeaderToken");
            }



            requestJSON.put("key", KEY);
            requestJSON.put("orderid", OrderId);
            requestJSON.put("paymentId", PaymentId);

            transactionLogger.error("REQUEST_URL ---->" + trackingId + " "+REQUEST_URL);
            transactionLogger.error("is3DSupportAccount ---->" + trackingId + " "+is3DSupportAccount);
            transactionLogger.error("Refund Request ---->" + trackingId + " "+requestJSON.toString());

            String response =   BoomBillUtils.doPostHttpUrlConnection(REQUEST_URL,requestJSON.toString(),HeaderToken,trackingId);

            transactionLogger.error("Refund Response ---> " + trackingId + " " + response);

            if (functions.isValueNull(response) && functions.isJSONValid(response) )
            {
                String success              = "";
                String message              = "";
                String gatewayResponse      = "";
                String responsePaymentId    = "";
                String gatewayStatus        = "";
                JSONObject responseJson = new JSONObject(response);
                JSONObject dataJSON    = null;

                if(responseJson.has("success") && functions.isValueNull(responseJson.getString("success")))
                {
                    success = responseJson.getString("success");
                }
                if(responseJson.has("message") && functions.isValueNull(responseJson.getString("message")))
                {
                    message = responseJson.getString("message");
                }
                if (responseJson.has("data")) // todo we get empty data parameter
                {
                    if (BoomBillUtils.isJSONValid(responseJson.getString("data")))
                    {
                        dataJSON = responseJson.getJSONObject("data");

                        if (dataJSON != null)
                        {

                            if (dataJSON.has("gatewayStatus"))
                            {
                                gatewayStatus = dataJSON.getString("gatewayStatus");
                            }
                            if (dataJSON.has("paymentId") && functions.isValueNull(dataJSON.getString("paymentId")))
                            {
                                responsePaymentId = dataJSON.getString("paymentId");
                            }
                            if (dataJSON.has("gatewayResponse"))
                            {
                                gatewayResponse = dataJSON.getString("gatewayResponse");
                            }
                        }
                    }
                    else if (BoomBillUtils.isJSONARRAYValid(responseJson.getString("data")))
                    {
                        JSONArray dataArray =   responseJson.getJSONArray("data");
                    }

                }


                if("True".equalsIgnoreCase(success))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                }
                else if ("False".equalsIgnoreCase(success))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else if("False".equalsIgnoreCase(success) && ("Invalid Payment Id!").equalsIgnoreCase(message))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
                comm3DResponseVO.setMerchantId(KEY);
                comm3DResponseVO.setTransactionId(responsePaymentId);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BoomBillPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

}
