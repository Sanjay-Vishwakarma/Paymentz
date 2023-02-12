package com.payment.smartfastpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.SmartFastPayRequestVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.apache.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2022-01-22.
 */
public class SmartFastPayPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE     = "smartftpay";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.smartfastpay");

    private static TransactionLogger transactionLogger  = new TransactionLogger(SmartFastPayPaymentGateway.class.getName());
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public SmartFastPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {

        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("SmartFastPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of SmartFastPayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest                      = gatewayAccount.isTest();

        String merchantId       = gatewayAccount.getMerchantId();
        String username         = gatewayAccount.getFRAUD_FTP_USERNAME();//client id
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();//CLIENT_SECRET

        String RETURN_URL            = "";

        JSONObject requestJSON       = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();
        JSONObject addressJSON       = new JSONObject();

        String REQUEST_URL          = "";
        String TOKEN_URL            = "";


        String customer_id      = "";
        String name             = "";
        String branch           = "";
        String email            = "";
        String document         = "";
        String amount           = "";
        String currency         = "";
        String access_token     = "";
        String errorMessage     = "";

        String address_1    = "";
        String number       = "5";
        String city         = "";
        String state        = "";
        String postal_code  = "";
        Map<String, String> requestMap   = new HashMap<String, String>();
        String toType                    =  transactionDetailsVO.getTotype();
        String payment_brand             = transactionDetailsVO.getCardType();
        try
        {
            transactionLogger.error("toType >>>> "+toType);
            payment_brand = SmartFastPayPaymentGatewayUtils.getPaymentBrand(payment_brand);

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
                TOKEN_URL   = RB.getString("TEST_TOKEN_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
                TOKEN_URL   = RB.getString("LIVE_TOKEN_URL");
            }

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                RETURN_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingId;
            }else{
                RETURN_URL = RB.getString("NOTIFY_URL") + trackingId;
            }

            transactionLogger.error("RequestString REQUEST_URL " + trackingId + " " + REQUEST_URL);

            String base64Credentials    = username+":"+password;
            base64Credentials           = new String(com.directi.pg.Base64.encode(base64Credentials.getBytes("utf-8")));

            String tokenResponse = SmartFastPayPaymentGatewayUtils.doPostTokenHTTPSURLConnection(TOKEN_URL, base64Credentials);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Token Response String >>>>>>>>>> "+trackingId+" " +tokenResponse);
            }else{
                transactionLogger.error("Token Response String "+trackingId+" " +tokenResponse);
            }

            if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                JSONObject tokenRespJSON            = new JSONObject(tokenResponse);

                JSONObject dataJSON       = null;
                JSONArray errorArrayJSON  = null;
                JSONObject  errorJSON     = null;

                if(tokenRespJSON.has("data")){
                    dataJSON = tokenRespJSON.getJSONObject("data");

                    if(dataJSON.has("access_token")){
                        access_token = dataJSON.getString("access_token");
                    }
                }
                if(tokenRespJSON.has("moreInformation")){
                    errorArrayJSON          = tokenRespJSON.getJSONArray("moreInformation");
                    if(errorArrayJSON != null){
                        errorJSON   = (JSONObject) errorArrayJSON.get(0);

                        if(errorJSON.has("code")){
                            errorMessage = errorJSON.getString("code");
                        }
                    }
                }
            }

            if(functions.isValueNull(access_token)){

                if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname())){
                    name = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
                }

                document        = commCardDetailsVO.getAccountNumber();
                currency        = transactionDetailsVO.getCurrency();
                amount          = transactionDetailsVO.getAmount();

                address_1       = commAddressDetailsVO.getStreet();
                city            = commAddressDetailsVO.getCity();
                postal_code     = commAddressDetailsVO.getZipCode();
                email           = commAddressDetailsVO.getEmail();
                state           = commAddressDetailsVO.getState();
                branch          = commMerchantVO.getMerchantOrganizationName();

                requestJSON.put("customer_id",trackingId);
                requestJSON.put("name",name);
                requestJSON.put("email",email);
                requestJSON.put("document",document);
                requestJSON.put("amount",amount);
                requestJSON.put("currency",currency);
                requestJSON.put("branch",branch);

                if("bank_transfer".equalsIgnoreCase(payment_brand) || "pix".equalsIgnoreCase(payment_brand)){
                    requestJSON.put("payment_method",payment_brand);
                }

                requestJSON.put("callback",RETURN_URL);

                transactionJSON.put("id",trackingId);

                requestJSON.put("transaction",transactionJSON);

                //boleto
                if("boleto".equalsIgnoreCase(payment_brand)){
                    addressJSON.put("address_1",address_1);
                    addressJSON.put("number",number);
                    addressJSON.put("neighborhood",city);
                    addressJSON.put("city",city);
                    addressJSON.put("state",state);
                    addressJSON.put("postal_code",postal_code);
                    addressJSON.put("address_2",city+" "+state);

                    requestJSON.put("address",addressJSON);

                    REQUEST_URL =REQUEST_URL+"/"+"boleto";

                }else if("picpay".equalsIgnoreCase(payment_brand)){

                    REQUEST_URL =REQUEST_URL+"/"+"picpay";

                }else if("bank_transfer".equalsIgnoreCase(payment_brand) || "pix".equalsIgnoreCase(payment_brand) ){

                    REQUEST_URL =REQUEST_URL+"/"+ "payment";
                }

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("Request String " + trackingId + " " + requestJSON.toString());
                }else {
                    transactionLogger.error("Request String " + trackingId + " " + requestJSON.toString());
                }

                String responseString = SmartFastPayPaymentGatewayUtils.doPostHTTPSURLConnection(REQUEST_URL, requestJSON.toString(), access_token);

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("Response From Bank "+trackingId+" "+payment_brand+" "+responseString);
                }else{
                    transactionLogger.error("Response From Bank "+trackingId+" "+payment_brand+" "+responseString);
                }

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON = new JSONObject(responseString);
                    JSONObject dataJSON     = null;
                    JSONObject paymentJSON  = null;
                    String transactionId    = "";
                    String status           = "";

                    //PIX
                    String qrcode           = "";
                    String reference        = "";
                    String beneficiary      = "";

                    String nameResp             = "";
                    String code                 = "";
                    String agency               = "";
                    String account              = "";
                    String account_operation    = "";
                    String documentResp         = "";

                    String url         = "";
                    String expired_at         = "";

                    JSONArray errorArrayJSON  = null;
                    JSONObject  errorJSON     = null;


                    if(responseJSON.has("data")){
                        dataJSON = responseJSON.getJSONObject("data");
                    }

                    if(responseJSON.has("moreInformation")){
                        errorArrayJSON          = responseJSON.getJSONArray("moreInformation");
                        if(errorArrayJSON != null){
                            errorJSON   = (JSONObject) errorArrayJSON.get(0);

                            if(errorJSON.has("code")){
                                errorMessage = errorJSON.getString("code");
                            }
                        }
                    }

                    if(dataJSON != null){
                        if(dataJSON.has("id")){
                            transactionId = dataJSON.getString("id");
                        }
                        if(dataJSON.has("status")){
                            status = dataJSON.getString("status");
                        }

                        if("bank_transfer".equalsIgnoreCase(payment_brand) && dataJSON.has("bank")){

                            paymentJSON = dataJSON.getJSONObject("bank");

                            if(paymentJSON.has("name")){
                                nameResp = paymentJSON.getString("name");
                            }
                            if(paymentJSON.has("code")){
                                code = paymentJSON.getString("code");
                            }
                            if(paymentJSON.has("agency")){
                                agency = paymentJSON.getString("agency");
                            }
                            if(paymentJSON.has("account")){
                                account = paymentJSON.getString("account");
                            }
                            if(paymentJSON.has("account_operation")){
                                account_operation = paymentJSON.getString("account_operation");
                            }
                            if(paymentJSON.has("document")){
                                documentResp = paymentJSON.getString("document");
                            }
                            if(paymentJSON.has("beneficiary")){
                                beneficiary = paymentJSON.getString("beneficiary");
                            }

                            requestMap.put("name",nameResp);
                            requestMap.put("code",code);
                            requestMap.put("agency",agency);
                            requestMap.put("account",account);
                            requestMap.put("account_operation",account_operation);
                            requestMap.put("document",documentResp);
                            requestMap.put("beneficiary",beneficiary);

                        } else if(("pix".equalsIgnoreCase(payment_brand) || "picpay".equalsIgnoreCase(payment_brand)) && dataJSON.has("pix"))
                        {
                            paymentJSON = dataJSON.getJSONObject("pix");

                            if(paymentJSON.has("qrcode")){
                                qrcode = paymentJSON.getString("qrcode");
                            }
                            if(paymentJSON.has("reference")){
                                reference = paymentJSON.getString("reference");
                            }
                            if(paymentJSON.has("beneficiary")){
                                beneficiary = paymentJSON.getString("beneficiary");
                            }

                            requestMap.put("qrcode",qrcode);
                            requestMap.put("reference",reference);
                            requestMap.put("beneficiary",beneficiary);


                        } else if("boleto".equalsIgnoreCase(payment_brand) && dataJSON.has("boleto"))
                        {
                            paymentJSON = dataJSON.getJSONObject("boleto");

                            if(paymentJSON.has("url")){
                                url = paymentJSON.getString("url");
                            }
                            if(paymentJSON.has("code")){
                                code = paymentJSON.getString("code");
                            }
                            if(paymentJSON.has("beneficiary")){
                                expired_at = paymentJSON.getString("beneficiary");
                            }
                        }
                    }

                    if(functions.isValueNull(url) && "pending".equalsIgnoreCase(status)){// card type =boleto
                        comm3DResponseVO.setUrlFor3DRedirect(url);
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amount);
                    }else if("pending".equalsIgnoreCase(status)){// card type =bank transfer/pix
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setRequestMap(requestMap);
                        comm3DResponseVO.setAmount(amount);
                    }else if("INVALID_REQUEST".equalsIgnoreCase(errorMessage)){// card type =bank transfer/pix
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        comm3DResponseVO.setRemark(errorMessage);
                        comm3DResponseVO.setDescription(errorMessage);
                    }else{
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setTransactionStatus("failed");
                comm3DResponseVO.setRemark(errorMessage);
                comm3DResponseVO.setDescription(errorMessage);
            }
            comm3DResponseVO.setThreeDVersion("Non-3D");
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(SmartFastPayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of SmartFastPayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId       = gatewayAccount.getMerchantId();
        String username         = gatewayAccount.getFRAUD_FTP_USERNAME();//client id
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();//CLIENT_SECRET

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL   = "";
        String TOKEN_URL     = "";
        String transaction   =  transactionDetailsVO.getPreviousTransactionId();
        String toType        =  transactionDetailsVO.getTotype();
        String access_token  =  "";
        try
        {

            if (isTest){
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL")+transaction;
                TOKEN_URL   = RB.getString("TEST_TOKEN_URL");
            }else {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL")+transaction;
                TOKEN_URL   = RB.getString("LIVE_TOKEN_URL");
            }

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Token Request URL --" + trackingId + "--->" + TOKEN_URL);
                facileroLogger.error("INQUIRY Request URL --" + trackingId + "--->" + REQUEST_URL);
            }else{
                transactionLogger.error("Token Request URL --" + trackingId + "--->" + TOKEN_URL);
                transactionLogger.error("INQUIRY Request URL --" + trackingId + "--->" + REQUEST_URL);
            }

            String base64Credentials    = username+":"+password;
            base64Credentials           = new String(com.directi.pg.Base64.encode(base64Credentials.getBytes("utf-8")));

            String tokenResponse = SmartFastPayPaymentGatewayUtils.doPostTokenHTTPSURLConnection(TOKEN_URL, base64Credentials);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Token Response String >>>>>>>>>> "+trackingId+" " +tokenResponse);
            }else{
                transactionLogger.error("Token Response String "+trackingId+" " +tokenResponse);
            }

            if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                JSONObject tokenRespJSON            = new JSONObject(tokenResponse);
                JSONObject dataJSON       = null;

                if(tokenRespJSON.has("data")){
                    dataJSON = tokenRespJSON.getJSONObject("data");

                    if(dataJSON.has("access_token")){
                        access_token = dataJSON.getString("access_token");
                    }
                }
            }

            if(functions.isValueNull(access_token)){
                String responseString = SmartFastPayPaymentGatewayUtils.doGetHTTPSURLConnectionClient(REQUEST_URL, access_token);

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("INQUIRY Response --> " + trackingId + " ---> " + responseString);
                }else{
                    transactionLogger.error("INQUIRY Response --> " + trackingId + " ---> " + responseString);
                }


                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON     =  new JSONObject(responseString);
                    JSONObject dataJSON         =  null;

                    String transactionId               = "";
                    String customer_id      = "";
                    String transaction_id   = "";
                    String amount           = "";
                    String currency         = "";
                    String status           = "";
                    String method           = "";
                    String created_at       = "";
                    String updated_at       = "";

                    if(responseJSON.has("data")){
                        dataJSON = responseJSON.getJSONObject("data");
                    }

                    if(dataJSON !=null)
                    {
                        if(dataJSON.has("id")){
                            transactionId = dataJSON.getString("id");
                        }
                        if(dataJSON.has("customer_id")){
                            customer_id = dataJSON.getString("customer_id");
                        }
                        if(dataJSON.has("transaction_id")){
                            transaction_id = dataJSON.getString("transaction_id");
                        }
                        if(dataJSON.has("amount")){
                            amount = dataJSON.getString("amount");
                        }
                        if(dataJSON.has("currency")){
                            currency = dataJSON.getString("currency");
                        }
                        if(dataJSON.has("status")){
                            status = dataJSON.getString("status");
                        }
                        if(dataJSON.has("method")){
                            method = dataJSON.getString("method");
                        }
                        if(dataJSON.has("updated_at")){
                            updated_at = dataJSON.getString("updated_at");
                        }
                    }

                    if("paid".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transaction_id);
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);
                    }else if("denied".equalsIgnoreCase(status) || "expired".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transaction_id);
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);
                    }else if("pending".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transaction_id);
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);

                    }else{
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }

                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
            comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SmartFastPayPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }



    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processPayout() of SmartFastPayPaymentGateway......");
        SmartFastPayRequestVO commRequestVO                     = (SmartFastPayRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest                      = gatewayAccount.isTest();
        String merchantId  = gatewayAccount.getMerchantId();
        String username    = gatewayAccount.getFRAUD_FTP_USERNAME();//client id
        String password    = gatewayAccount.getFRAUD_FTP_PASSWORD();//CLIENT_SECRET

        JSONObject requestJSON     = new JSONObject();
        JSONObject transactionJSON = new JSONObject();
        JSONObject paymentJSON     = new JSONObject();

        String REQUEST_URL          = "";
        String RETURN_URL          = "";
       // String customer_id      = transactionDetailsVO.getToId();
        String customer_id      = "";
        String name             = "";
        String branch             = "";
        String email            = "";
        String document         = "";
        String amount           = "";
        String currency         = "";
        String key              = "";
        String bank_Name              = "";

        String code                 = "";
        String agency               = "";
        String account              = "";
        String account_operation    = "";
        String toType       =  transactionDetailsVO.getTotype();

        String TOKEN_URL     = "";
        String access_token  =  "";
        String errorMessage  =  "";
        String transferType  =  "";
        String payment_brand             = transactionDetailsVO.getCardType();

        try
        {
            transactionLogger.error("payment_brand before >>>>> "+payment_brand);
            payment_brand = SmartFastPayPaymentGatewayUtils.getPaymentBrand(payment_brand);
            transactionLogger.error("payment_brand after >>>>> "+payment_brand);
            if(functions.isValueNull(transactionDetailsVO.getBankTransferType())){
                transferType    = transactionDetailsVO.getBankTransferType();
            }

            if (isTest)
            {
                REQUEST_URL = RB.getString("PAYOUT_TEST_URL");
                TOKEN_URL   = RB.getString("TEST_TOKEN_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("PAYOUT_LIVE_URL");
                TOKEN_URL   = RB.getString("LIVE_TOKEN_URL");
            }

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                RETURN_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingId;
            }else{
                RETURN_URL = RB.getString("NOTIFY_URL") + trackingId;
            }


            String base64Credentials    = username+":"+password;
            base64Credentials           = new String(com.directi.pg.Base64.encode(base64Credentials.getBytes("utf-8")));

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Token Request URL  "+trackingId+" " +TOKEN_URL);
            }else{
                transactionLogger.error("Token Request URL "+trackingId+" " +TOKEN_URL);
            }

            String tokenResponse = SmartFastPayPaymentGatewayUtils.doPostTokenHTTPSURLConnection(TOKEN_URL, base64Credentials);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Token Response String >>>>>>>>>> "+trackingId+" " +tokenResponse);
            }else{
                transactionLogger.error("Token Response String "+trackingId+" " +tokenResponse);
            }

            if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                JSONObject tokenRespJSON            = new JSONObject(tokenResponse);

                JSONObject dataJSON                 = null;
                JSONArray errorArrayJSON   = null;
                JSONObject  errorJSON = null;

                if(tokenRespJSON.has("data")){
                    dataJSON = tokenRespJSON.getJSONObject("data");

                    if(dataJSON.has("access_token")){
                        access_token = dataJSON.getString("access_token");
                    }
                }
                if(tokenRespJSON.has("moreInformation")){
                    errorArrayJSON          = tokenRespJSON.getJSONArray("moreInformation");
                    if(errorArrayJSON != null){
                        errorJSON   = (JSONObject) errorArrayJSON.get(0);

                        if(errorJSON.has("code")){
                            errorMessage = errorJSON.getString("code");
                        }
                    }
                }
            }

            if(functions.isValueNull(access_token)){


                if(functions.isValueNull(transactionDetailsVO.getCustomerBankAccountNumber())){
                    document        = transactionDetailsVO.getCustomerBankAccountNumber();
                }
                if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                    email        = commAddressDetailsVO.getEmail();
                }
                if(functions.isValueNull(commRequestVO.getBank_Name())){
                    bank_Name        = commRequestVO.getBank_Name();
                }
                if(functions.isValueNull(transactionDetailsVO.getCustomerBankAccountName()))
                {
                    name        = transactionDetailsVO.getCustomerBankAccountName();
                }
                branch          = commMerchantVO.getMerchantOrganizationName();

                if("BankTransfer".equalsIgnoreCase(transferType)){
                    if(functions.isValueNull(commRequestVO.getBank_Code())){
                        code        = commRequestVO.getBank_Code();
                    }
                    if(functions.isValueNull(commRequestVO.getBranch_Code())){
                        agency      = commRequestVO.getBranch_Code();
                    }
                    if(functions.isValueNull(transactionDetailsVO.getBankAccountNo())){
                        account     = transactionDetailsVO.getBankAccountNo();
                    }
                    if(functions.isValueNull(commRequestVO.getAccount_Type())){
                        account_operation = commRequestVO.getAccount_Type();
                    }
                }else{
                    if(functions.isValueNull(commRequestVO.getBank_Code())){
                        key        = commRequestVO.getBank_Code();
                    }
                }



                currency  = transactionDetailsVO.getCurrency();
                amount    = transactionDetailsVO.getAmount();

                requestJSON.put("customer_id",trackingId);
                requestJSON.put("name",name);
                requestJSON.put("email",email);
                requestJSON.put("document",document);
                requestJSON.put("amount",amount);
                requestJSON.put("currency",currency);
                requestJSON.put("callback",RETURN_URL);
                requestJSON.put("branch",branch);

                transactionJSON.put("id",trackingId);
                requestJSON.put("transaction",transactionJSON);

                if(("pix".equalsIgnoreCase(payment_brand) && "PIX".equalsIgnoreCase(transferType)) || ( "picpay".equalsIgnoreCase(payment_brand) && "PICPAY".equalsIgnoreCase(transferType)) ){
                    paymentJSON.put("key",key);
                    requestJSON.put("pix", paymentJSON);

                }else if("bank_transfer".equalsIgnoreCase(payment_brand) && "BankTransfer".equalsIgnoreCase(transferType)){
                    paymentJSON.put("name",bank_Name);
                    paymentJSON.put("code",code);
                    paymentJSON.put("agency",agency);
                    paymentJSON.put("account",account);
                    paymentJSON.put("account_operation",account_operation);

                    requestJSON.put("bank", paymentJSON);
                }

                transactionLogger.error("Payout Request REQUEST_URL "+trackingId +" "+REQUEST_URL);

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("Payout Request "+trackingId +" "+transferType+" "+requestJSON.toString());
                }else{
                    transactionLogger.error("Payout Request "+trackingId +" "+transferType+" "+requestJSON.toString());
                }

                String responseString = SmartFastPayPaymentGatewayUtils.doPostHTTPSURLConnection(REQUEST_URL, requestJSON.toString(),  access_token);

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("Payout Response "+trackingId +" "+responseString.toString());
                }else{
                    transactionLogger.error("Payout Response "+trackingId +" "+responseString.toString());
                }


                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON     =  new JSONObject(responseString);
                    JSONObject dataJSON         =  null;

                    String transactionId               = "";
                    String customer_idResp      = "";
                    String transaction_idResp   = "";
                    String amountResp           = "";
                    String currencyResp         = "";
                    String status           = "";
                    String method           = "";
                    String created_at       = "";
                    String updated_at       = "";

                    if(responseJSON.has("moreInformation")){
                        JSONArray errorArrayJSON   = null;
                        JSONObject  errorJSON      = null;
                        errorArrayJSON             = responseJSON.getJSONArray("moreInformation");
                        if(errorArrayJSON != null){
                            errorJSON   = (JSONObject) errorArrayJSON.get(0);

                            if(errorJSON.has("code")){
                                errorMessage = errorJSON.getString("code");
                            }
                        }
                    }

                    if(responseJSON.has("data")){
                        dataJSON = responseJSON.getJSONObject("data");
                    }

                    if(dataJSON !=null)
                    {
                        if(dataJSON.has("id")){
                            transactionId = dataJSON.getString("id");
                        }
                        if(dataJSON.has("customer_id")){
                            customer_idResp = dataJSON.getString("customer_id");
                        }
                        if(dataJSON.has("transaction_id")){
                            transaction_idResp = dataJSON.getString("transaction_id");
                        }
                        if(dataJSON.has("amount")){
                            amountResp = dataJSON.getString("amount");
                        }
                        if(dataJSON.has("currency")){
                            currencyResp = dataJSON.getString("currency");
                        }
                        if(dataJSON.has("status")){
                            status = dataJSON.getString("status");
                        }
                        if(dataJSON.has("method")){
                            method = dataJSON.getString("method");
                        }
                        if(dataJSON.has("updated_at")){
                            updated_at = dataJSON.getString("updated_at");
                        }
                    }
                    if(functions.isValueNull(transactionId)){
                        SmartFastPayPaymentGatewayUtils.updateMainTableEntry(trackingId,transactionId);
                    }


                    if("success".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amountResp);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transaction_idResp);
                        comm3DResponseVO.setCurrency(currencyResp);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);
                    }else if("failed".equalsIgnoreCase(status) || "INVALID_REQUEST".equalsIgnoreCase(errorMessage)){
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setTransactionStatus("failed");

                        if(functions.isValueNull(errorMessage)){
                            comm3DResponseVO.setRemark(errorMessage);
                            comm3DResponseVO.setDescription(errorMessage);
                        }else{
                            comm3DResponseVO.setRemark(status);
                            comm3DResponseVO.setDescription(status);
                        }

                        comm3DResponseVO.setAmount(amountResp);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transaction_idResp);
                        comm3DResponseVO.setCurrency(currencyResp);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);
                    }else if("pending".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Pending");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Pending");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }else{
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setTransactionStatus("failed");
                comm3DResponseVO.setRemark(errorMessage);
                comm3DResponseVO.setDescription(errorMessage);
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(SmartFastPayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processPayoutInquiry() of SmartFastPayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId       = gatewayAccount.getMerchantId();
        String username         = gatewayAccount.getFRAUD_FTP_USERNAME();//client id
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();//CLIENT_SECRET

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL = "";
        String TOKEN_URL   = "";
        String access_token   = "";
        String transaction =  transactionDetailsVO.getPreviousTransactionId();
        String toType      =  transactionDetailsVO.getTotype();
        try
        {

            if (isTest){
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_TEST_URL")+transaction;
                TOKEN_URL   = RB.getString("TEST_TOKEN_URL");
            }else {
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_LIVE_URL")+transaction;
                TOKEN_URL   = RB.getString("LIVE_TOKEN_URL");
            }

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Token Request URL --" + trackingId + "--->" + TOKEN_URL);
                facileroLogger.error("INQUIRY Request URL --" + trackingId + "--->" + REQUEST_URL);
            }else{
                transactionLogger.error("Token Request URL --" + trackingId + "--->" + TOKEN_URL);
                transactionLogger.error("INQUIRY Request URL --" + trackingId + "--->" + REQUEST_URL);
            }

            String base64Credentials    = username+":"+password;
            base64Credentials           = new String(com.directi.pg.Base64.encode(base64Credentials.getBytes("utf-8")));

            String tokenResponse = SmartFastPayPaymentGatewayUtils.doPostTokenHTTPSURLConnection(TOKEN_URL, base64Credentials);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Token Response String >>>>>>>>>> "+trackingId+" " +tokenResponse);
            }else{
                transactionLogger.error("Token Response String "+trackingId+" " +tokenResponse);
            }

            if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                JSONObject tokenRespJSON            = new JSONObject(tokenResponse);
                JSONObject dataJSON       = null;

                if(tokenRespJSON.has("data")){
                    dataJSON = tokenRespJSON.getJSONObject("data");

                    if(dataJSON.has("access_token")){
                        access_token = dataJSON.getString("access_token");
                    }
                }
            }

            if(functions.isValueNull(access_token)){

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("Payout Inquiry Request ---> " + trackingId + " ---> " + REQUEST_URL);
                }else{
                    transactionLogger.error("Payout Inquiry Request ---> " + trackingId + " ---> " + REQUEST_URL);
                }

                String responseString = SmartFastPayPaymentGatewayUtils.doGetHTTPSURLConnectionClient(REQUEST_URL, access_token);

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("processPayoutInquiry() Response ---> " + trackingId + "--->" + responseString);
                }else{
                    transactionLogger.error("processPayoutInquiry() Response ---> " + trackingId + "--->" + responseString);
                }

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON     =  new JSONObject(responseString);
                    JSONObject dataJSON         =  null;

                    String transactionId               = "";
                    String customer_id      = "";
                    String transaction_id   = "";
                    String amount           = "";
                    String currency         = "";
                    String status           = "";
                    String method           = "";
                    String created_at       = "";
                    String updated_at       = "";

                    if(responseJSON.has("data")){
                        dataJSON = responseJSON.getJSONObject("data");
                    }

                    if(dataJSON !=null)
                    {
                        if(dataJSON.has("id")){
                            transactionId = dataJSON.getString("id");
                        }
                        if(dataJSON.has("customer_id")){
                            customer_id = dataJSON.getString("customer_id");
                        }
                        if(dataJSON.has("transaction_id")){
                            transaction_id = dataJSON.getString("transaction_id");
                        }
                        if(dataJSON.has("amount")){
                            amount = dataJSON.getString("amount");
                        }
                        if(dataJSON.has("currency")){
                            currency = dataJSON.getString("currency");
                        }
                        if(dataJSON.has("status")){
                            status = dataJSON.getString("status");
                        }
                        if(dataJSON.has("method")){
                            method = dataJSON.getString("method");
                        }
                        if(dataJSON.has("updated_at")){
                            updated_at = dataJSON.getString("updated_at");
                        }
                    }

                    if("success".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transactionId);
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);
                    }else if("failed".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transactionId);
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);
                    }else if("pending".equalsIgnoreCase(status)){
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setRemark(status);
                        comm3DResponseVO.setDescription(status);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(transactionId);
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setResponseTime(updated_at);
                        comm3DResponseVO.setMerchantId(merchantId);
                    }else{
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }

                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
            comm3DResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());

        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SmartFastPayPaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

}
