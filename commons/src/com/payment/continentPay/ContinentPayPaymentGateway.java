package com.payment.continentPay;

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
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;


import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2022-04-20.
 */
public class ContinentPayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE  = "continent";
    private final static ResourceBundle RB   = LoadProperties.getProperty("com.directi.pg.ContinentPay");

    private static TransactionLogger transactionLogger = new TransactionLogger(ContinentPayPaymentGateway.class.getName());

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public ContinentPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ContinentPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of ContinentPayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        String ApiToken         = gatewayAccount.getMerchantId();
        String NOTIFY_URL       = "";
        String REQUEST_URL      = "";
        String REDIRECT_URL     = "";
        String bic              = "";
        String amount           = "";
        String currency         = "";
        String success_url      = "";
        String fail_url         = "";
        String callback_url     = "";
        String email            = "";
        String first_name       = "";
        String last_name        = "";
        String country          = "";
        String state            = "";
        String city             = "";
        String address          = "";
        String zip_code         = "";
        String phone            = "";
        String cc               = "";
        String terminal         = "";
        String card_number      = "";
        String card_expire_date = "";
        String card_cvv_code    = "";
        String card_first_name  = "";
        String card_last_name   = "";
        String ip_address       = "";
        String user_agent       = "";
        String response         = "";
        String EncryptedData    = "";


        try
        {
            REQUEST_URL = RB.getString("LIVE_SALE_URL");
            NOTIFY_URL  = RB.getString("NOTIFY_URL") + trackingID;

            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                REDIRECT_URL ="https://"+commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            else
                REDIRECT_URL = RB.getString("REDIRECT_URL") + trackingID;

            success_url         = REDIRECT_URL +"&success=success";
            fail_url            = REDIRECT_URL +"&fail=fail";
            callback_url        = NOTIFY_URL   +"&callback=callback";

            bic              = trackingID;                                                                  // Business Identifier Codes
            amount           = transactionDetailsVO.getAmount();
            currency         = transactionDetailsVO.getCurrency();
            email            = commAddressDetailsVO.getEmail();
            first_name       = commAddressDetailsVO.getFirstname();
            last_name        = commAddressDetailsVO.getLastname();
            country          = commAddressDetailsVO.getCountry();
            state            = commAddressDetailsVO.getState();
            city             = commAddressDetailsVO.getCity();
            address          = commAddressDetailsVO.getStreet();
            zip_code         = commAddressDetailsVO.getZipCode();

            if (functions.isValueNull(commAddressDetailsVO.getTelnocc()))
            {
                cc = commAddressDetailsVO.getTelnocc();
            }
            else
            {
                cc = "091";
            }
            transactionLogger.error("commAddressDetailsVO.getTelnocc(): "+commAddressDetailsVO.getTelnocc());
            transactionLogger.error("cc: " + cc);
            if (cc.length() == 3 && !cc.contains("+"))
            {
                cc = "0"+cc;
            }
            transactionLogger.error("cc: "+cc);
            phone            = cc + commAddressDetailsVO.getPhone();
            terminal         = "card";                                                                      // ["card","apmgw_Sofort","apmgw_NeoSurf_Direct_Integration_","apmgw_Giropay"]
            card_number      = commCardDetailsVO.getCardNum();
            card_expire_date = commCardDetailsVO.getExpMonth()+ "/" + ContinentPayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            card_cvv_code    = commCardDetailsVO.getcVV();
            card_first_name  = commAddressDetailsVO.getFirstname();
            card_last_name   = commAddressDetailsVO.getLastname();

            if (functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                ip_address = commAddressDetailsVO.getCardHolderIpAddress();
            else
                ip_address = "192.168.1.1";


            if (commDeviceDetailsVO != null)
            {
                if (functions.isValueNull(commDeviceDetailsVO.getFingerprints()))
                {
                    transactionLogger.error("Inside Fingerprints if condition----> ");
                    HashMap fingerPrintMap = functions.getFingerPrintMap(commDeviceDetailsVO.getFingerprints());
                    if (fingerPrintMap.containsKey("userAgent"))
                        user_agent = (String) fingerPrintMap.getOrDefault("userAgent", "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0");
                }
                else
                {
                    transactionLogger.error("Inside Fingerprints else condition --------> ");
                    if (functions.isValueNull(commDeviceDetailsVO.getUser_Agent()))
                        user_agent = commDeviceDetailsVO.getUser_Agent();
                    else
                        user_agent = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                }
            }
            else
            {
                user_agent = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
            }


            JSONObject requestParameters = new JSONObject();
            JSONObject device            = new JSONObject();
            JSONObject user              = new JSONObject();
            JSONObject payment_details   = new JSONObject();

            //device json
            device.put("ip_address",ip_address);
            device.put("user_agent",user_agent);

            //user json
            user.put("email",email);
            user.put("first_name",first_name);
            user.put("last_name",last_name);
            user.put("country",country);
            user.put("state",state);
            user.put("city",city);
            user.put("address",address);
            user.put("zip_code",zip_code);
            user.put("phone",phone);

            //payment details
            payment_details.put("card_number",card_number);
            payment_details.put("card_expire_date",card_expire_date);
            payment_details.put("card_cvv_code",card_cvv_code);
            payment_details.put("card_first_name",card_first_name);
            payment_details.put("card_last_name",card_last_name);
            payment_details.put("bic",bic);

            // request json
            requestParameters.put("amount",amount);
            requestParameters.put("currency",currency);
            requestParameters.put("success_url",success_url);
            requestParameters.put("fail_url",fail_url);
            requestParameters.put("callback_url",callback_url);
            requestParameters.put("user",user);
            requestParameters.put("terminal",terminal);
            requestParameters.put("device",device);
            requestParameters.put("payment_details",payment_details);


            JSONObject requestParametersLOG = new JSONObject();
            JSONObject deviceLOG            = new JSONObject();
            JSONObject userLOG              = new JSONObject();
            JSONObject payment_detailsLOG   = new JSONObject();

            //device json
            deviceLOG.put("ip_address",ip_address);
            deviceLOG.put("user_agent",user_agent);

            //user json
            userLOG.put("email",functions.getEmailMasking(email));
            userLOG.put("first_name",functions.maskingFirstName(first_name));
            userLOG.put("last_name",functions.maskingLastName(last_name));
            userLOG.put("country",country);
            userLOG.put("state",state);
            userLOG.put("city",city);
            userLOG.put("address",address);
            userLOG.put("zip_code",zip_code);
            userLOG.put("phone",functions.getPhoneNumMasking(phone));

            //payment details
            payment_detailsLOG.put("card_number",functions.maskingPan(card_number));
            payment_detailsLOG.put("card_expire_date",functions.maskingExpiry(card_expire_date));
            payment_detailsLOG.put("card_cvv_code",functions.maskingNumber(card_cvv_code));
            payment_detailsLOG.put("card_first_name",functions.maskingFirstName(card_first_name));
            payment_detailsLOG.put("card_last_name",functions.maskingLastName(card_last_name));
            payment_detailsLOG.put("bic",bic);

            // request json
            requestParametersLOG.put("amount",amount);
            requestParametersLOG.put("currency",currency);
            requestParametersLOG.put("success_url",success_url);
            requestParametersLOG.put("fail_url",fail_url);
            requestParametersLOG.put("callback_url",callback_url);
            requestParametersLOG.put("user",userLOG);
            requestParametersLOG.put("terminal",terminal);
            requestParametersLOG.put("device",deviceLOG);
            requestParametersLOG.put("payment_details",payment_detailsLOG);

            transactionLogger.error("processSale() >>>>> "+trackingID+ " ApiToken: "+ApiToken +" RequestURL: "+REQUEST_URL +" requestParameters--->"+requestParametersLOG);

            EncryptedData = ContinentPayUtils.getHmac256Signature(requestParameters.toString(), ApiToken.getBytes());
            transactionLogger.error("EncryptedData-----> "+ trackingID +" >>>>> "+ EncryptedData);

            response = ContinentPayUtils.doPostHTTPUrlConnection(REQUEST_URL, ApiToken, requestParameters.toString());
            transactionLogger.error("processSale() response----> "+ trackingID +" >>>>> " + response);


            if (functions.isValueNull(response) && ContinentPayUtils.isJSONValid(response))
            {
                String respId           = "";
                String respStatus       = "";
                String respRedirect_url = "";
                String respAmount       = "";
                String respCurrency     = "";
                String respErrors       = "";

                JSONObject responseJSON = new JSONObject(response);
                HashMap<String,String> requestHM = new HashMap<>();

                if (responseJSON.has("id") && functions.isValueNull(responseJSON.getString("id")))
                {
                    respId = responseJSON.getString("id");
                    ContinentPayUtils.updateMainTableEntry(respId,trackingID);
                }

                if (responseJSON.has("status") && functions.isValueNull(responseJSON.getString("status")))
                {
                    respStatus = responseJSON.getString("status");
                }

                if (responseJSON.has("redirect_url") && functions.isValueNull(responseJSON.getString("redirect_url")))
                {
                    respRedirect_url = responseJSON.getString("redirect_url");
                    requestHM.put("redirect_url",responseJSON.getString("redirect_url"));
                }

                if (responseJSON.has("amount") && functions.isValueNull(responseJSON.getString("amount")))
                {
                    respAmount = responseJSON.getString("amount");
                }

                if (responseJSON.has("currency") && functions.isValueNull(responseJSON.getString("currency")))
                {
                    respCurrency = responseJSON.getString("currency");
                }

                if (responseJSON.has("errors") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("errors"))))
                {
                    JSONArray errorArray = responseJSON.getJSONArray("errors");
                    for (int i = 0; i < errorArray.length(); i++)
                    {
                        respErrors += errorArray.getString(i);
                    }
                }


                /*if (respStatus.equalsIgnoreCase("purchase_complete"))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark("Payment successful");
                    comm3DResponseVO.setDescription("Payment successful");
                    comm3DResponseVO.setTransactionId(respId);
                    comm3DResponseVO.setThreeDVersion("Non-3D");
                }
                else*/
                if ((respStatus.equalsIgnoreCase("redirect") || "3d_checking".equalsIgnoreCase(respStatus) || "new".equalsIgnoreCase(respStatus)) && functions.isValueNull(respRedirect_url))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(respRedirect_url);
                    comm3DResponseVO.setRequestMap(requestHM);
                    comm3DResponseVO.setTransactionId(respId);
                    comm3DResponseVO.setAmount(respAmount);
                    comm3DResponseVO.setCurrency(respCurrency);
                }
                else if (respStatus.equalsIgnoreCase("purchase_declined") || respStatus.equalsIgnoreCase("error") || functions.isValueNull(respErrors))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    if (functions.isValueNull(respErrors))
                    {
                        comm3DResponseVO.setRemark(respErrors);
                        comm3DResponseVO.setDescription(respErrors);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark("failed");
                        comm3DResponseVO.setDescription("failed");
                    }
                    comm3DResponseVO.setTransactionId(respId);
                    comm3DResponseVO.setAmount(respAmount);
                    comm3DResponseVO.setCurrency(respCurrency);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
                comm3DResponseVO.setThreeDVersion("3Dv2");
                comm3DResponseVO.setTransactionId(respId);
                comm3DResponseVO.setMerchantId(ApiToken);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("processSale() Exception  ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(ContinentPayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of ContinentPayPaymentGateway......");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        String ApiToken                 = gatewayAccount.getMerchantId();
        String LIVE_INQUIRY_REQUEST_URL = "";
        String id                       = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            LIVE_INQUIRY_REQUEST_URL = RB.getString("LIVE_INQUIRY_URL");

            transactionLogger.error("processInquiry GET request----> "+ trackingID +">>>>>>>> "+ LIVE_INQUIRY_REQUEST_URL+id);

            String response = ContinentPayUtils.doGetHTTPUrlConnection(LIVE_INQUIRY_REQUEST_URL+id, ApiToken);
            transactionLogger.error("processInquiry() response----> "+ trackingID +" >>>>> " + response);

            if (functions.isValueNull(response) && ContinentPayUtils.isJSONValid(response))
            {
                String respId           = "";
                String respStatus       = "";
                String respRedirect_url = "";
                String respAmount       = "";
                String respCurrency     = "";
                String respErrors       = "";

                JSONObject responseJSON = new JSONObject(response);
                HashMap<String,String> requestHM = new HashMap<>();

                if (responseJSON.has("id") && functions.isValueNull(responseJSON.getString("id")))
                {
                    respId = responseJSON.getString("id");
                    ContinentPayUtils.updateMainTableEntry(respId,trackingID);
                }

                if (responseJSON.has("status") && functions.isValueNull(responseJSON.getString("status")))
                {
                    respStatus = responseJSON.getString("status");
                }

                if (responseJSON.has("redirect_url") && functions.isValueNull(responseJSON.getString("redirect_url")))
                {
                    respRedirect_url = responseJSON.getString("redirect_url");
                    requestHM.put("redirect_url",responseJSON.getString("redirect_url"));
                }

                if (responseJSON.has("amount") && functions.isValueNull(responseJSON.getString("amount")))
                {
                    respAmount = responseJSON.getString("amount");
                }

                if (responseJSON.has("currency") && functions.isValueNull(responseJSON.getString("currency")))
                {
                    respCurrency = responseJSON.getString("currency");
                }

                if (responseJSON.has("errors") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("errors"))))
                {
                    JSONArray errorArray = responseJSON.getJSONArray("errors");
                    for (int i = 0; i < errorArray.length(); i++)
                    {
                        respErrors += errorArray.getString(i);
                    }
                }


                if (respStatus.equalsIgnoreCase("purchase_complete"))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark(respStatus);
                    comm3DResponseVO.setDescription(respStatus);
                    comm3DResponseVO.setTransactionId(respId);
                    comm3DResponseVO.setAmount(respAmount);
                    comm3DResponseVO.setCurrency(respCurrency);
                    comm3DResponseVO.setMerchantId(ApiToken);
                }
                else if (respStatus.equalsIgnoreCase("purchase_declined") || respStatus.equalsIgnoreCase("error") || functions.isValueNull(respErrors))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    if (functions.isValueNull(respErrors))
                    {
                        comm3DResponseVO.setRemark(respErrors);
                        comm3DResponseVO.setDescription(respErrors);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark("failed");
                        comm3DResponseVO.setDescription("failed");
                    }
                    comm3DResponseVO.setTransactionId(respId);
                    comm3DResponseVO.setAmount(respAmount);
                    comm3DResponseVO.setCurrency(respCurrency);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
                comm3DResponseVO.setThreeDVersion("3Dv2");
                comm3DResponseVO.setTransactionId(respId);
                comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
                comm3DResponseVO.setMerchantId(ApiToken);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ContinentPayPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

}
