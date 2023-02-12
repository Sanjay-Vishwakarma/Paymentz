package com.payment.omnipay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.log4j.LogManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created by Admin on 2022-01-22.
 */
public class OmnipayPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE     = "omnipay";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.Omnipay");

    private static TransactionLogger transactionLogger  = new TransactionLogger(OmnipayPaymentGateway.class.getName());
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public OmnipayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("OmnipayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of OmnipayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        String merchantKey      = gatewayAccount.getMerchantId();
        String merchant_site    = gatewayAccount.getFRAUD_FTP_SITE();
        String account          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchant_secret  = gatewayAccount.getFRAUD_FTP_PASSWORD();

        boolean isTest = gatewayAccount.isTest();

        String NOTIFY_URL           = RB.getString("OMNIPAY_NOTIFY_URL") + trackingID;
        String REQUEST_URL          = "";
        String amount               = "";
        String currency             = "";
        String order_id             = "";
        String purpose              = "";
        String customer_first_name  = "";
        String customer_last_name   = "";
        String customer_address     = "";
        String customer_city        = "";
        String customer_zip_code    = "";
        String customer_phone       = "";
        String customer_email       = "";
        String customer_ip_address  = "";
        String success_url          = "";
        String fail_url             = "";
        String callback_url         = "";
        String status_url           = "";
        String card_holder          = "";
        String card_number          = "";
        String card_exp_month       = "";
        String card_exp_year        = "";
        String card_cvv             = "";
        String customer_country     = "";
        String browser_accept_header = "";
        String browser_java_enabled  = "";
        String browser_js_enabled    = "";
        String browser_color_depth   = "";
        String browser_screen_height = "";
        String browser_screen_width  = "";
        String browser_language      = "";
        String browser_time_zone     = "";
        String browser_user_agent    = "";
        String RETURN_URL            = "";
        String responseType          = "";
//        String Sign                  = "";
        String response              = "";
        String toType                =  "";
        try
        {
            if(functions.isValueNull(transactionDetailsVO.getTotype()))
            {
                toType                =  transactionDetailsVO.getTotype();
            }
            transactionLogger.error("toType >>>>>>>>>> "+trackingID+" "+toType);

            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                RETURN_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            else
                RETURN_URL = RB.getString("REDIRECT_URL") + trackingID;

            order_id            = trackingID;
            purpose             = trackingID;
            amount              = OmnipayPaymentGatewayUtils.getAmount(transactionDetailsVO.getAmount());
            currency            = transactionDetailsVO.getCurrency();

            if(functions.isValueNull(commAddressDetailsVO.getStreet()) && commAddressDetailsVO.getStreet().length() < 50)
            {
                customer_address = commAddressDetailsVO.getStreet();
            }
            else
            {
                customer_address = "1st Street";
            }

            if (functions.isValueNull(commAddressDetailsVO.getCity()) && commAddressDetailsVO.getCity().length() < 20)
            {
                customer_city = commAddressDetailsVO.getCity();
            }
            else
            {
                customer_city = "Denver";
            }

            if (functions.isValueNull(commAddressDetailsVO.getZipCode()) )
            {
                customer_zip_code   = commAddressDetailsVO.getZipCode();            }
            else
            {
                customer_zip_code = "121165";
            }

            if (functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()) )
            {
                customer_ip_address = commAddressDetailsVO.getCardHolderIpAddress();
            }
           else
            {
                customer_ip_address = "192.168.0.1";
            }

            if (functions.isValueNull(commAddressDetailsVO.getFirstname()) && commAddressDetailsVO.getFirstname().length() > 19)
            {
                customer_first_name = commAddressDetailsVO.getFirstname().substring(0,19);
            }
            else if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
            {
                customer_first_name = commAddressDetailsVO.getFirstname();
            }
            else
            {
                customer_first_name = "customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getLastname()) && commAddressDetailsVO.getLastname().length() > 19)
            {
                customer_last_name = commAddressDetailsVO.getLastname().substring(0,19);
            }
            else if (functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                customer_last_name = commAddressDetailsVO.getLastname();
            }
            else
            {
                customer_last_name = "customer";
            }

            if (functions.isValueNull(customer_first_name) && functions.isValueNull(customer_last_name))
            {
                card_holder = customer_first_name + " " + customer_last_name;
            }
            else
            {
                card_holder = "customer customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                customer_email = commAddressDetailsVO.getEmail();
            }
            else
            {
                customer_email = "customer@gmail.com";
            }

            if (functions.isValueNull(commAddressDetailsVO.getPhone()) )
            {
                customer_phone      = commAddressDetailsVO.getPhone();
            }
            else{
                customer_phone      = "9999999999";
            }

            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                customer_country = commAddressDetailsVO.getCountry();
            }
            else
            {
                customer_country = "US";
            }

            success_url         = RETURN_URL +"&success=success";
            fail_url            = RETURN_URL +"&fail=fail";
            callback_url        = NOTIFY_URL +"&callback=callback";
            status_url          = NOTIFY_URL +"&status=status";
            card_number         = commCardDetailsVO.getCardNum();
            card_exp_month      = commCardDetailsVO.getExpMonth();
            card_exp_year       = OmnipayPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            card_cvv            = commCardDetailsVO.getcVV();
            browser_js_enabled  = "TRUE";

            if (functions.isValueNull(commDeviceDetailsVO.getAcceptHeader())){
                browser_accept_header = commDeviceDetailsVO.getAcceptHeader();// if not work add else part here
            }else {
                browser_accept_header = "text/html,application/xhtml xml,application/xml;image/avif,image/webp,image/apng,*/*;application/signed-exchange;v=b3";
            }

            if (functions.isValueNull(commDeviceDetailsVO.getFingerprints()))
            {
                transactionLogger.error("Inside Fingerprints if condition----> ");
                HashMap fingerPrintMap = functions.getFingerPrintMap(commDeviceDetailsVO.getFingerprints());
                JSONArray screenResolution = new JSONArray();
                try
                {
                    if (fingerPrintMap.containsKey("userAgent"))
                        browser_user_agent = (String) fingerPrintMap.get("userAgent");
                    if (fingerPrintMap.containsKey("language"))
                        browser_language = (String) fingerPrintMap.get("language");
                    if (fingerPrintMap.containsKey("colorDepth"))
                        browser_color_depth = String.valueOf(fingerPrintMap.get("colorDepth"));
                    if (fingerPrintMap.containsKey("timezoneOffset"))
                        browser_time_zone = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                    if (fingerPrintMap.containsKey("screenResolution"))
                        screenResolution = (JSONArray) fingerPrintMap.get("screenResolution");
                    if (screenResolution.length() > 0)
                        browser_screen_height = String.valueOf(screenResolution.getString(0));
                    if (screenResolution.length() > 1)
                        browser_screen_width = String.valueOf(screenResolution.getString(1));
                    browser_java_enabled = "TRUE";
                    transactionLogger.error("screenResolution --------" + screenResolution);
                }
                catch (JSONException e)
                {
                    transactionLogger.error("JSONException---trackingId--" + trackingID + "--", e);
                }
            }
            else
            {
                transactionLogger.error("Inside Fingerprints else condition --------> ");
                if (functions.isValueNull(commDeviceDetailsVO.getUser_Agent()))
                    browser_user_agent = commDeviceDetailsVO.getUser_Agent();
                else
                    browser_user_agent = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                if (functions.isValueNull(commDeviceDetailsVO.getBrowserLanguage()))
                    browser_language = commDeviceDetailsVO.getBrowserLanguage();
                else
                    browser_language = "en-US";
                if (functions.isValueNull(commDeviceDetailsVO.getBrowserColorDepth()))
                    browser_color_depth = commDeviceDetailsVO.getBrowserColorDepth();
                else
                    browser_color_depth = "24";
                if (functions.isValueNull(commDeviceDetailsVO.getBrowserTimezoneOffset()))
                    browser_time_zone = commDeviceDetailsVO.getBrowserTimezoneOffset();
                else
                    browser_time_zone = "5";
                if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenHeight()))
                    browser_screen_height = commDeviceDetailsVO.getBrowserScreenHeight();
                else
                    browser_screen_height = "939";
                if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenWidth()))
                    browser_screen_width = commDeviceDetailsVO.getBrowserScreenWidth();
                else
                    browser_screen_width = "1255";
                if (functions.isValueNull(commDeviceDetailsVO.getBrowserJavaEnabled()))
                    browser_java_enabled = commDeviceDetailsVO.getBrowserJavaEnabled();
                else
                    browser_java_enabled = "TRUE";
            }


            if (isTest){
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }else {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }


            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.put("account", account);
            treeMap.put("amount", amount);
            treeMap.put("browser_accept_header", browser_accept_header);
            treeMap.put("browser_color_depth", browser_color_depth);
            treeMap.put("browser_java_enabled", browser_java_enabled.toLowerCase());
            treeMap.put("browser_js_enabled", browser_js_enabled.toLowerCase());
            treeMap.put("browser_language", browser_language);
            treeMap.put("browser_screen_height", browser_screen_height);
            treeMap.put("browser_screen_width", browser_screen_width);
            treeMap.put("browser_time_zone", browser_time_zone);
            treeMap.put("browser_user_agent", browser_user_agent);
            treeMap.put("callback_url", callback_url);
            treeMap.put("card_cvv", card_cvv);
            treeMap.put("card_exp_month", card_exp_month);
            treeMap.put("card_exp_year", card_exp_year);
            treeMap.put("card_holder", card_holder);
            treeMap.put("card_number", card_number);
            treeMap.put("currency", currency);
            treeMap.put("customer_address", customer_address);
            treeMap.put("customer_city", customer_city);
            treeMap.put("customer_country", customer_country);
            treeMap.put("customer_email", customer_email);
            treeMap.put("customer_first_name", customer_first_name);
            treeMap.put("customer_ip_address", customer_ip_address);
            treeMap.put("customer_last_name", customer_last_name);
            treeMap.put("customer_phone", customer_phone);
            treeMap.put("customer_zip_code", customer_zip_code);
            treeMap.put("fail_url", fail_url);
            treeMap.put("merchant_site", merchant_site);
            treeMap.put("order_id", order_id);
            treeMap.put("purpose", purpose);
            treeMap.put("status_url", status_url);
            treeMap.put("success_url", success_url);
            
            JSONObject parameters           = new JSONObject();
            StringBuilder sortedParameters  = new StringBuilder();
            for (Object keys : treeMap.keySet())
            {
                if (keys.equals(treeMap.lastKey()))
                {
                    sortedParameters.append(treeMap.get(keys));
                }
                else
                {
                    sortedParameters.append(treeMap.get(keys));
                    sortedParameters.append("|");
                }
                parameters.put((String) keys, treeMap.get(keys));
            }
//            transactionLogger.error("processSale() sortedParameters--" +trackingID + "--->" + sortedParameters);

            JSONObject parametersLOG = new JSONObject();
            parametersLOG.put("account", account);
            parametersLOG.put("amount", amount);
            parametersLOG.put("browser_accept_header", browser_accept_header);
            parametersLOG.put("browser_color_depth", browser_color_depth);
            parametersLOG.put("browser_java_enabled", browser_java_enabled.toLowerCase());
            parametersLOG.put("browser_js_enabled", browser_js_enabled.toLowerCase());
            parametersLOG.put("browser_language", browser_language);
            parametersLOG.put("browser_screen_height", browser_screen_height);
            parametersLOG.put("browser_screen_width", browser_screen_width);
            parametersLOG.put("browser_time_zone", browser_time_zone);
            parametersLOG.put("browser_user_agent", browser_user_agent);
            parametersLOG.put("callback_url", callback_url);
            parametersLOG.put("card_cvv", functions.maskingNumber(card_cvv));
            parametersLOG.put("card_exp_month", functions.maskingNumber(card_exp_month));
            parametersLOG.put("card_exp_year", functions.maskingNumber(card_exp_year));
            parametersLOG.put("card_holder", functions.getNameMasking(card_holder));
            parametersLOG.put("card_number", functions.maskingPan(card_number));
            parametersLOG.put("currency", currency);
            parametersLOG.put("customer_address", customer_address);
            parametersLOG.put("customer_city", customer_city);
            parametersLOG.put("customer_country", customer_country);
            parametersLOG.put("customer_email", functions.getEmailMasking(customer_email));
            parametersLOG.put("customer_first_name", functions.maskingFirstName(customer_first_name));
            parametersLOG.put("customer_ip_address", customer_ip_address);
            parametersLOG.put("customer_last_name", functions.maskingLastName(customer_last_name));
            parametersLOG.put("customer_phone", functions.getPhoneNumMasking(customer_phone));
            parametersLOG.put("customer_zip_code", customer_zip_code);
            parametersLOG.put("fail_url", fail_url);
            parametersLOG.put("merchant_site", merchant_site);
            parametersLOG.put("order_id", order_id);
            parametersLOG.put("purpose", purpose);
            parametersLOG.put("status_url", status_url);
            parametersLOG.put("success_url", success_url);

//            Sign = OmnipayPaymentGatewayUtils.getHmac256Signature(sortedParameters.toString(), merchant_secret.getBytes());    todo not needed as per bank
//            transactionLogger.error("processSale() Request--"+trackingID+"---> MerchantKey:" + merchantKey + " Sign:" + Sign + " url:" + REQUEST_URL + " parameters:" + parametersLOG);
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Sale Request --> "+trackingID+" --->  URL:" + REQUEST_URL + " parameters:" + parametersLOG);
            }else{
                transactionLogger.error("processSale() Request--" + trackingID + "---> MerchantKey:" + merchantKey + " url:" + REQUEST_URL + " parameters:" + parametersLOG);
            }

            response = OmnipayPaymentGatewayUtils.doPostHTTPSURLConnection(merchantKey, merchant_secret, REQUEST_URL, parameters.toString(),trackingID);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Sale Response --> " + trackingID + "--->" + response);
            }else{
                transactionLogger.error("processSale() Response -->" + trackingID + "--->" + response);
            }

            if (functions.isValueNull(response) && OmnipayPaymentGatewayUtils.isJSONValid(response) )
            {
                HashMap<String,String> requestHM = new HashMap<>();
                String respStatus   = "";
                String respFormUrl  = "";
                String respPaReq    = "";
                String respMD       = "";
                String respTermUrl  = "";
                String respACSUrl   = "";
                String respError    = "";
                String respOrder_id             = "";
                String respCReq                 = "";
                String respDescription          = "";
                String respThreeDSMethodData    = "";
                String transMode                = "";

                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("order_id") && functions.isValueNull(responseJson.getString("order_id"))){
                    respOrder_id = responseJson.getString("order_id");

                }

                if (responseJson.has("description") && functions.isValueNull(responseJson.getString("description"))){
                    respDescription = responseJson.getString("description");
                }

                if (responseJson.has("threeDSMethodData") && functions.isValueNull(responseJson.getString("threeDSMethodData"))){
                    respThreeDSMethodData = responseJson.getString("threeDSMethodData");
                }

                if (responseJson.has("status") && functions.isValueNull(responseJson.getString("status"))){
                    respStatus = responseJson.getString("status");
                }

                if (responseJson.has("form_url") && functions.isValueNull(responseJson.getString("form_url"))){
                    respFormUrl = responseJson.getString("form_url");
                    requestHM.put("form_url",responseJson.getString("form_url"));
                }

                if (responseJson.has("CReq") && functions.isValueNull(responseJson.getString("CReq"))){
                    respCReq = responseJson.getString("CReq");
                    requestHM.put("CReq",responseJson.getString("CReq"));
                }

                if (responseJson.has("PaReq") && functions.isValueNull(responseJson.getString("PaReq"))){
                    respPaReq = responseJson.getString("PaReq");
                    requestHM.put("PaReq",responseJson.getString("PaReq"));
                }

                if (responseJson.has("MD") && functions.isValueNull(responseJson.getString("MD"))){
                    respMD = responseJson.getString("MD");
                    requestHM.put("MD",responseJson.getString("MD"));
                }

                if (responseJson.has("TermUrl") && functions.isValueNull(responseJson.getString("TermUrl"))){
                    respTermUrl = responseJson.getString("TermUrl");
                    requestHM.put("TermUrl",responseJson.getString("TermUrl"));
                }

                if (responseJson.has("ACSUrl") && functions.isValueNull(responseJson.getString("ACSUrl"))){
                    respACSUrl = responseJson.getString("ACSUrl");
                    requestHM.put("ACSUrl",responseJson.getString("ACSUrl"));
                }

                if (responseJson.has("responseType") && functions.isValueNull(responseJson.getString("responseType"))){
                    responseType = responseJson.getString("responseType");
                    requestHM.put("responseType",responseJson.getString("responseType"));
                }

                if (responseJson.has("err") && functions.isValueNull(responseJson.getString("err"))){
                    respError = responseJson.getString("err");
                }


                if (functions.isValueNull(respStatus) && "1".equals(respStatus) && functions.isValueNull(respACSUrl) && functions.isValueNull(respCReq))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(respACSUrl);
                    comm3DResponseVO.setTerURL(respTermUrl);
                    comm3DResponseVO.setMd(respMD);
                    comm3DResponseVO.setCreq(respCReq);
                    comm3DResponseVO.setRequestMap(requestHM);
                    comm3DResponseVO.setTransactionId(respOrder_id);
                    comm3DResponseVO.setThreeDVersion("3Dv2");
                    transMode="3Dv2";
                }
                else if(functions.isValueNull(respStatus) && "1".equals(respStatus) && functions.isValueNull(respFormUrl))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(respFormUrl);
                    comm3DResponseVO.setRedirectUrl(respACSUrl);
                    comm3DResponseVO.setTerURL(respTermUrl);
                    comm3DResponseVO.setPaReq(respPaReq);
                    comm3DResponseVO.setMd(respMD);
                    comm3DResponseVO.setRequestMap(requestHM);
                    comm3DResponseVO.setTransactionId(respOrder_id);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                    transMode="3Dv1";

                }
                else if(functions.isValueNull(respStatus) && "2".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark("Payment successful");
                    comm3DResponseVO.setDescription("Payment successful");
                    comm3DResponseVO.setTransactionId(respOrder_id);
                    comm3DResponseVO.setThreeDVersion("Non-3D");
                    transMode="Non-3D";
                }
                else if (functions.isValueNull(respStatus) && "-1".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark("failed");
                    comm3DResponseVO.setDescription("failed");
                    comm3DResponseVO.setTransactionId(respOrder_id);
                }
                else if (functions.isValueNull(respError))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(respError);
                    comm3DResponseVO.setDescription(respError);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
                    OmnipayPaymentGatewayUtils.updateMainTableEntry(respOrder_id,transMode,trackingID);
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
            transactionLogger.error("SSLHandshakeException ==> "+trackingID , e);
            PZExceptionHandler.raiseTechnicalViolationException(OmnipayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of OmnipayPaymentGateway......");
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        String merchantKey      = gatewayAccount.getMerchantId();
        String merchant_secret  = gatewayAccount.getFRAUD_FTP_PASSWORD();

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL          = "";
        String order_id             = "";
//        String Sign                 = "";
        String response              = "";
        String toType                =  "";

        try
        {

            order_id = trackingID;

            if(functions.isValueNull(transactionDetailsVO.getTotype())){
                toType                =  transactionDetailsVO.getTotype();
            }
            transactionLogger.error("ToType ----> "+ trackingID+" "+toType);

            if (isTest){
                REQUEST_URL = RB.getString("LIVE_INQUIRY_URL");
            }else {
                REQUEST_URL = RB.getString("LIVE_INQUIRY_URL");
            }

            JSONObject parameters           = new JSONObject();
            parameters.put("order_id", order_id);

//            Sign = OmnipayPaymentGatewayUtils.getHmac256Signature(order_id, merchant_secret.getBytes());      todo not needed as per bank
//            transactionLogger.error("processQuery() Request--"+trackingID+"---> MerchantKey:" + merchantKey + " Sign:" + Sign + " url:" + REQUEST_URL + " parameters:" + parameters);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Inquiry Request --> "+trackingID+" --->  REQUEST_URL:" + REQUEST_URL + " Parameters: " + parameters);
            }else{
                transactionLogger.error("processQuery() Request--"+trackingID+"---> MerchantKey:" + merchantKey + " url:" + REQUEST_URL + " parameters:" + parameters);
            }

            response = OmnipayPaymentGatewayUtils.doPostHTTPSURLConnection(merchantKey, merchant_secret, REQUEST_URL, parameters.toString(),trackingID);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Inquiry Response --> " + trackingID + "--->" + response);
            }else{
                transactionLogger.error("processQuery() Response--" + trackingID + "--->" + response);
            }

            if (functions.isValueNull(response) && OmnipayPaymentGatewayUtils.isJSONValid(response) )
            {
                String respStatus   = "";
                String respAmount   = "";
                String respCard     = "";
                String respCardNumber = "";
                String respExpMonth = "";
                String respExpYear = "";
                String respCardHolderName = "";
                String respHash = "";
                String respBin = "";
                String respError = "";
                String respErrorMessage    = "";
                String respTransactionDate = "";
                String respAuthorizeStatus = "";
                String respOrder_id = "";


                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("order_id") && functions.isValueNull(responseJson.getString("order_id"))){
                    respOrder_id = responseJson.getString("order_id");
                }

                if (responseJson.has("status") && functions.isValueNull(responseJson.getString("status"))){
                    respStatus = responseJson.getString("status");
                }

                if (responseJson.has("amount") && functions.isValueNull(responseJson.getString("amount"))){
                    respAmount = responseJson.getString("amount");
                    respAmount = String.format("%.2f", Double.parseDouble(respAmount)/100);
                    transactionLogger.error("Inquiry formated Response amount-- "+trackingID+" -->"+respAmount);
                }

//                if (responseJson.has("card") && functions.isValueNull(responseJson.getString("card")))
//                {
//                    respCard = responseJson.getString("card");
//                    JSONObject cardInfo = new JSONObject(respCard);
//                    if (OmnipayPaymentGatewayUtils.isJSONValid(respCard))
//                    {
//                        if (cardInfo.has("number") && functions.isValueNull(cardInfo.getString("number"))){
//                            respCardNumber = cardInfo.getString("number");
//                        }
//
//                        if (cardInfo.has("expMonth") && functions.isValueNull(cardInfo.getString("expMonth"))){
//                            respExpMonth  = cardInfo.getString("expMonth");
//                        }
//
//                        if (cardInfo.has("expYear") && functions.isValueNull(cardInfo.getString("expYear"))){
//                            respExpYear = cardInfo.getString("expYear");
//                        }
//
//                        if (cardInfo.has("holder") && functions.isValueNull(cardInfo.getString("holder"))){
//                            respCardHolderName = cardInfo.getString("holder");
//                        }
//
//                        if (cardInfo.has("hash") && functions.isValueNull(cardInfo.getString("hash"))){
//                            respHash = cardInfo.getString("hash");
//                        }
//
//                        if (cardInfo.has("bin") && functions.isValueNull(cardInfo.getString("bin"))){
//                            respBin = cardInfo.getString("bin");
//                        }
//                    }
//                }

                if (responseJson.has("errorMessage") && functions.isValueNull(responseJson.getString("errorMessage"))){
                    respErrorMessage = responseJson.getString("errorMessage");
                }

                if (responseJson.has("err") && functions.isValueNull(responseJson.getString("err"))){
                    respError = responseJson.getString("err");
                }


                if (responseJson.has("endDate") && functions.isValueNull(responseJson.getString("endDate"))){
                    respTransactionDate = responseJson.getString("endDate");
                }

                if (responseJson.has("authorize_status") && functions.isValueNull(responseJson.getString("authorize_status"))){
                    respAuthorizeStatus = responseJson.getString("authorize_status");
                }


                if (functions.isValueNull(respStatus) && "2".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    if (functions.isValueNull(respErrorMessage)) {
                        comm3DResponseVO.setRemark(respErrorMessage);
                        comm3DResponseVO.setDescription(respErrorMessage);
                    }else {
                        comm3DResponseVO.setRemark("Payment successful");
                        comm3DResponseVO.setDescription("Payment successful");
                    }
                    comm3DResponseVO.setBankTransactionDate(respTransactionDate);
                    comm3DResponseVO.setAmount(respAmount);
                }
                else if (functions.isValueNull(respStatus) && "1".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("pending");  // set inquiry response not found
                    comm3DResponseVO.setRemark("pending");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setBankTransactionDate(respTransactionDate);
                    comm3DResponseVO.setAmount(respAmount);
                }
                else if (functions.isValueNull(respStatus) && "-1".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark("failed");
                    comm3DResponseVO.setDescription("failed");
                    comm3DResponseVO.setBankTransactionDate(respTransactionDate);
                    comm3DResponseVO.setAmount(respAmount);
                }
                else if (functions.isValueNull(respError))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(respError);
                    comm3DResponseVO.setDescription(respError);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

                comm3DResponseVO.setTransactionId(respOrder_id);
                comm3DResponseVO.setMerchantId(merchantKey);
                comm3DResponseVO.setAuthCode(respAuthorizeStatus);
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
            transactionLogger.error(trackingID +" Exception : "+e);
            PZExceptionHandler.raiseTechnicalViolationException(OmnipayPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside Omnipay processRefund() ---------->");

        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Functions functions                                 = new Functions();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        String merchantKey      = gatewayAccount.getMerchantId();
        String merchant_site    = gatewayAccount.getFRAUD_FTP_SITE();
        String account          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchant_secret  = gatewayAccount.getFRAUD_FTP_PASSWORD();

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL          = "";
        String order_id             = "";
        String amount               = "";
        String reason               = "";
//        String Sign                 = "";
        String response             = "";
        String toType               ="";

        try
        {
            order_id    = trackingID;
            amount      = OmnipayPaymentGatewayUtils.getAmount(commTransactionDetailsVO.getAmount());
            reason      = "Refund";

            if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
                toType = commTransactionDetailsVO.getTotype();
            }

            transactionLogger.error("Inside Omnipay processRefund() --> "+trackingID+" "+toType);

            if (isTest)
            {
                REQUEST_URL = RB.getString("LIVE_REFUND_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_REFUND_URL");
            }

            String callback_url           = RB.getString("OMNIPAY_NOTIFY_URL") + trackingID;

            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.put("account", account);
            treeMap.put("amount", amount);
            treeMap.put("order_id", order_id);
            treeMap.put("reason", reason);
            treeMap.put("callback_url", callback_url);

            JSONObject parameters           = new JSONObject();
            StringBuilder sortedParameters  = new StringBuilder();
            for (Map.Entry<String, String> map : treeMap.entrySet())
            {
                if (map.getKey() == treeMap.lastKey())
                {
                    sortedParameters.append(map.getValue());
                }
                else
                {
                    sortedParameters.append(map.getValue());
                    sortedParameters.append("|");
                }
                parameters.put(map.getKey(), map.getValue());
            }
//            transactionLogger.error("processPayout() sortedParameters--" + trackingID+ "--->" + sortedParameters);

//            Sign = OmnipayPaymentGatewayUtils.getHmac256Signature(sortedParameters.toString(), merchant_secret.getBytes());  todo not needed as per bank
//            transactionLogger.error("processRefund() Request--"+trackingID+"---> MerchantKey:" + merchantKey + " Sign:" + Sign + " url:" + REQUEST_URL + " parameters:" + parameters);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Refund Request --> "+trackingID+ " REQUEST_URL: " + REQUEST_URL + " Parameters: " + parameters);
            }else{
                transactionLogger.error("processRefund() Request--> "+trackingID+" ---> MerchantKey: " + merchantKey + " REQUEST_URL : " + REQUEST_URL + " Parameters: " + parameters);
            }

            response = OmnipayPaymentGatewayUtils.doPostHTTPSURLConnection(merchantKey, merchant_secret, REQUEST_URL, parameters.toString(),trackingID);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Refund Response --> " + trackingID + " --->" + response);
            }else{
                transactionLogger.error("processRefund() Response--" + trackingID + " --->" + response);
            }

            if (functions.isValueNull(response) && OmnipayPaymentGatewayUtils.isJSONValid(response))
            {
                String respStatus   = "";
                String respError    = "";
                String message      = "";
                String respOrder_id = "";
                String success      = "";

                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("status") && functions.isValueNull(responseJson.getString("status")))
                {
                    respStatus = responseJson.getString("status");
                }

                if (responseJson.has("message") && functions.isValueNull(responseJson.getString("message")))
                {
                    message = responseJson.getString("message");
                }

                if (responseJson.has("success") && functions.isValueNull(String.valueOf(responseJson.getBoolean("success"))))
                {
                    success = String.valueOf(responseJson.getBoolean("success"));
                }

                if (responseJson.has("err") && functions.isValueNull(responseJson.getString("err")))
                {
                    respError = responseJson.getString("err");
                }

                if (responseJson.has("refund_id") && functions.isValueNull(responseJson.getString("refund_id")))
                {
                    respOrder_id = responseJson.getString("refund_id");
                }
                else if (responseJson.has("order_id") && functions.isValueNull(responseJson.getString("order_id")))
                {
                    respOrder_id = responseJson.getString("order_id");
                }


                if ((functions.isValueNull(respStatus) && "2".equals(respStatus)) || (functions.isValueNull(success) && "true".equalsIgnoreCase(success)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    if (functions.isValueNull(message)) {
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                    }else{
                        comm3DResponseVO.setRemark("Refund successful");
                        comm3DResponseVO.setDescription("Refund successful");
                    }
                    comm3DResponseVO.setTransactionId(respOrder_id);
                }
                else if ((functions.isValueNull(respStatus) && "-1".equals(respStatus)) || (functions.isValueNull(success) && "false".equalsIgnoreCase(success)))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    if (functions.isValueNull(message)) {
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                    }else{
                        comm3DResponseVO.setRemark("failed");
                        comm3DResponseVO.setDescription("failed");
                    }
                    comm3DResponseVO.setTransactionId(respOrder_id);
                }
                else if (functions.isValueNull(respError))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(respError);
                    comm3DResponseVO.setDescription(respError);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
                OmnipayPaymentGatewayUtils.updateMainTableEntry(respOrder_id, "", trackingID);
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
            transactionLogger.error(trackingID + " Exception : "+e);
            PZExceptionHandler.raiseTechnicalViolationException(OmnipayPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processPayout() of OmnipayPaymentGateway......");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        String payoutAccount        = gatewayAccount.getFRAUD_FTP_PATH();
        String payoutMerchantKey    = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String payoutMerchantSecret = gatewayAccount.getCHARGEBACK_FTP_PATH();

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL          = "";
        String order_id             = "";
        String customer_card_number = "";
        String amount               = "";
        String customer_first_name  = "";
        String customer_last_name   = "";
//        String Sign                 = "";
        String response             = "";
        String respOrder_id         = "";
        String toType               ="";

        try
        {
            customer_first_name  = commAddressDetailsVO.getFirstname();
            customer_last_name   = commAddressDetailsVO.getLastname();
            amount               = OmnipayPaymentGatewayUtils.getAmount(commTransactionDetailsVO.getAmount());
            order_id             = trackingID;
            customer_card_number = commCardDetailsVO.getCardNum();

            if (isTest){
                REQUEST_URL = RB.getString("LIVE_PAYOUT_URL");
            }else {
                REQUEST_URL = RB.getString("LIVE_PAYOUT_URL");
            }

            if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
                toType = commTransactionDetailsVO.getTotype();
            }
            transactionLogger.error("ToType--" + trackingID+ "--->" + toType);


            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.put("account", payoutAccount);
            treeMap.put("amount", amount);
            treeMap.put("customer_card_number", customer_card_number);
            treeMap.put("customer_first_name", customer_first_name);
            treeMap.put("customer_last_name", customer_last_name);
            treeMap.put("order_id", order_id);

            JSONObject parameters           = new JSONObject();
            StringBuilder sortedParameters  = new StringBuilder();
            for (Map.Entry<String, String> map : treeMap.entrySet())
            {
                if (map.getKey() == treeMap.lastKey())
                {
                    sortedParameters.append(map.getValue());
                }
                else
                {
                    sortedParameters.append(map.getValue());
                    sortedParameters.append("|");
                }
                parameters.put(map.getKey(), map.getValue());
            }
//            transactionLogger.error("processPayout() sortedParameters--" + trackingID+ "--->" + sortedParameters);

            JSONObject parametersLOG = new JSONObject();
            parametersLOG.put("account", payoutAccount);
            parametersLOG.put("amount", amount);
            parametersLOG.put("customer_card_number", functions.maskingPan(customer_card_number));
            parametersLOG.put("customer_first_name", functions.maskingFirstName(customer_first_name));
            parametersLOG.put("customer_last_name", functions.maskingLastName(customer_last_name));
            parametersLOG.put("order_id", order_id);

//            Sign = OmnipayPaymentGatewayUtils.getHmac256Signature(sortedParameters.toString(), payoutMerchantSecret.getBytes());  todo not needed as per bank
//            transactionLogger.error("processPayout() Request--"+trackingID+"---> MerchantKey:" + payoutMerchantKey + " Sign:" + Sign + " url:" + REQUEST_URL + " parameters:" + parametersLOG);
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Payout Request --> "+trackingID+ " REQUEST_URL:" + REQUEST_URL + " parameters:" + parametersLOG);
            }else{
                transactionLogger.error("Payout Request--"+trackingID+"---> MerchantKey:" + payoutMerchantKey + " url:" + REQUEST_URL + " parameters:" + parametersLOG);
            }

            response = OmnipayPaymentGatewayUtils.doPostHTTPSURLConnection(payoutMerchantKey, payoutMerchantSecret, REQUEST_URL, parameters.toString(),trackingID);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Payout  Response --> " + trackingID + "--->" + response);
            }else{
                transactionLogger.error("processPayout() Response--" + trackingID + "--->" + response);
            }

            if (functions.isValueNull(response) && OmnipayPaymentGatewayUtils.isJSONValid(response) )
            {
                String respStatus   = "";
                String respError    = "";

                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("status") && functions.isValueNull(responseJson.getString("status"))){
                    respStatus = responseJson.getString("status");
                }
                if (responseJson.has("err") && functions.isValueNull(responseJson.getString("err"))){
                    respError = responseJson.getString("err");
                }

                if (responseJson.has("order_id") && functions.isValueNull(responseJson.getString("order_id"))){
                    respOrder_id = responseJson.getString("order_id");
                    OmnipayPaymentGatewayUtils.updateMainTableEntry(respOrder_id,"",trackingID);
                }


                if (functions.isValueNull(respStatus) && "2".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setRemark("Payout successful");
                    comm3DResponseVO.setDescription("Payout successful");
                    comm3DResponseVO.setTransactionId(respOrder_id);
                }
                else if (functions.isValueNull(respStatus) && "1".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("pending");  // set inquiry response not found
                    comm3DResponseVO.setRemark("pending");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setTransactionId(respOrder_id);
                }
                else if (functions.isValueNull(respStatus) && "-1".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark("failed");
                    comm3DResponseVO.setDescription("failed");
                    comm3DResponseVO.setTransactionId(respOrder_id);
                }
                else if (functions.isValueNull(respError))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(respError);
                    comm3DResponseVO.setDescription(respError);
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


            String CALL_EXECUTE_AFTER       = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL    = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC          = RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed          = RB.getString("THREAD_CALL");
            transactionLogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed))
            {
                transactionLogger.error("inside isThreadAllowed condition ------->"+isThreadAllowed);
                new AsyncOmnipayPayoutQueryService(trackingID,accountId,respOrder_id,CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error(trackingID + " Exception: "+e);
            PZExceptionHandler.raiseTechnicalViolationException(OmnipayPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processPayoutInquiry() of OmnipayPaymentGateway......");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        Functions functions                                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        String payoutAccount        = gatewayAccount.getFRAUD_FTP_PATH();
        String payoutMerchantKey    = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String payoutMerchantSecret = gatewayAccount.getCHARGEBACK_FTP_PATH();

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL          = "";
        String order_id             = "";
        String customer_card_number = "";
        String amount               = "";
//        String Sign                 = "";
        String response             = "";
        String toType               ="";

        try
        {
            amount               = OmnipayPaymentGatewayUtils.getAmount(commTransactionDetailsVO.getAmount());
            order_id             = trackingId;
            if (functions.isValueNull(commCardDetailsVO.getCardNum()))
            {
                customer_card_number = PzEncryptor.decryptPAN(commCardDetailsVO.getCardNum());
            }

            if (functions.isValueNull(commTransactionDetailsVO.getTotype()))
            {
                toType = commTransactionDetailsVO.getTotype();
            }

            transactionLogger.error("ToType--" + trackingId+ "--->" + toType);
            if (isTest){
                REQUEST_URL = RB.getString("LIVE_PAYOUT_INQUIRY");
            }else {
                REQUEST_URL = RB.getString("LIVE_PAYOUT_INQUIRY");
            }

            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.put("account", payoutAccount);
            treeMap.put("order_id", order_id);
            treeMap.put("amount", amount);
            treeMap.put("customer_card_number", customer_card_number);

            JSONObject parameters           = new JSONObject();
            StringBuilder sortedParameters  = new StringBuilder();
            for (Map.Entry<String, String> map : treeMap.entrySet())
            {
                if (map.getKey() == treeMap.lastKey())
                {
                    sortedParameters.append(map.getValue());
                }
                else
                {
                    sortedParameters.append(map.getValue());
                    sortedParameters.append("|");
                }
                parameters.put(map.getKey(), map.getValue());
            }
//            transactionLogger.error("processPayoutInquiry() sortedParameters--" + trackingId + "--->" + sortedParameters);

            JSONObject parametersLOG = new JSONObject();
            parametersLOG.put("account", payoutAccount);
            parametersLOG.put("amount", amount);
            parametersLOG.put("customer_card_number", functions.maskingPan(customer_card_number));
            parametersLOG.put("order_id", order_id);

//            Sign = OmnipayPaymentGatewayUtils.getHmac256Signature(sortedParameters.toString(), payoutMerchantSecret.getBytes()); todo not needed as per bank
//            transactionLogger.error("processPayoutInquiry() Request--"+trackingId+"---> MerchantKey:" + payoutMerchantKey + " Sign:" + Sign + " url:" + REQUEST_URL + " parameters:" + parametersLOG);
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Payout Inquiry Request --> "+trackingId+ " REQUEST_URL:" + REQUEST_URL + " parameters:" + parametersLOG);
            }else {
                transactionLogger.error("Payout Inquiry Request --> "+trackingId+" ---> MerchantKey:" + payoutMerchantKey + " url:" + REQUEST_URL + " parameters:" + parametersLOG);
            }

            response = OmnipayPaymentGatewayUtils.doPostHTTPSURLConnection(payoutMerchantKey, payoutMerchantSecret, REQUEST_URL, parameters.toString(),trackingId);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("OmniPay Payout Inquiry Response --> " + trackingId + " --->" + response);
            }else{
                transactionLogger.error("Payout Inquiry Response--> " + trackingId + " --->" + response);
            }


            if (functions.isValueNull(response) && OmnipayPaymentGatewayUtils.isJSONValid(response) )
            {
                String respStatus   = "";
                String respError    = "";
                String respOrder_id = "";
                String respReason   = "";

                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("order_id") && functions.isValueNull(responseJson.getString("order_id"))){
                    respOrder_id = responseJson.getString("order_id");
                }

                if (responseJson.has("status") && functions.isValueNull(responseJson.getString("status"))){
                    respStatus = responseJson.getString("status");
                }

                if (responseJson.has("reason") && functions.isValueNull(responseJson.getString("reason"))){
                    respReason = responseJson.getString("reason");
                }

                if (responseJson.has("err") && functions.isValueNull(responseJson.getString("err"))){
                    respError = responseJson.getString("err");
                }


                if (functions.isValueNull(respStatus) && "2".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    if (functions.isValueNull(respReason))
                    {
                        comm3DResponseVO.setRemark(respReason);
                        comm3DResponseVO.setDescription(respReason);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark("Payout successful");
                        comm3DResponseVO.setDescription("Payout successful");
                    }
                }
                else if (functions.isValueNull(respStatus) && "1".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    if (functions.isValueNull(respReason))
                    {
                        comm3DResponseVO.setRemark(respReason);
                        comm3DResponseVO.setDescription(respReason);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark("pending");  // set inquiry response not found
                        comm3DResponseVO.setDescription("pending");  // set inquiry response not found
                    }
                }
                else if (functions.isValueNull(respStatus) && "-1".equals(respStatus))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    if (functions.isValueNull(respReason))
                    {
                        comm3DResponseVO.setRemark(respReason);
                        comm3DResponseVO.setDescription(respReason);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark("failed");
                        comm3DResponseVO.setDescription("failed");
                    }
                }
                else if (functions.isValueNull(respError))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(respError);
                    comm3DResponseVO.setDescription(respError);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

                comm3DResponseVO.setTransactionId(respOrder_id);
                comm3DResponseVO.setMerchantId(payoutMerchantKey);
                comm3DResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
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
            transactionLogger.error(trackingId + " Exception: "+e);
            PZExceptionHandler.raiseTechnicalViolationException(OmnipayPaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- processAutoRedirect in OmniPay ----");
        OmnipayPaymentProcess omnipayPaymentProcess             = new OmnipayPaymentProcess();
        String html                                             = "";
        Comm3DResponseVO transRespDetails                       = null;
        OmnipayPaymentGatewayUtils omnipayPaymentGatewayUtils   = new OmnipayPaymentGatewayUtils();
        String paymentMode                                      = commonValidatorVO.getPaymentMode();
        CommRequestVO commRequestVO                             = omnipayPaymentGatewayUtils.getOmniPayPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();

        try
        {
            transactionLogger.error("  tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = omnipayPaymentProcess.get3DConfirmationForm(commonValidatorVO.getTrackingid(),"", transRespDetails);
                transactionLogger.error("Auto Redirect omnipay form -- >>" + html);
            }
            else{
                html    = transRespDetails.getStatus();
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in OmniPay---", e);
        }
        return html;
    }
}
