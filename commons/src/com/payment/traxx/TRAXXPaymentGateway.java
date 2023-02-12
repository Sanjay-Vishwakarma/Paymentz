package com.payment.traxx;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by admin on 9/13/2016.
 */

public class TRAXXPaymentGateway extends AbstractPaymentGateway
{
    //private static Logger log                             = new Logger(TRAXXPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger      = new TransactionLogger(TRAXXPaymentGateway.class.getName());
    private final static ResourceBundle RB                  = LoadProperties.getProperty("com.directi.pg.traxx");


    public static final String GATEWAY_TYPE = "traxx";

    private final static String TESTURL = "https://secure.tra-xx.com/Payments/WebServer";

    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public  TRAXXPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {
        String request = "request_type=AUTHORIZE\n"  +
                "&username=caibodigitaltest\n" +
                "&password=c41b0d1g1t4lt35t0822\n" +
                "&merchant_id=13242b0e76\n" +
                "&trxn_type=CAPTURE\n" +
                "&track_id=512140807\n" +
                "&currency_code=USD\n" +
                "&amount=200.50\n" +
                "&payment_method=2\n" +
                "&card_account=5464000000000000\n" +
                "&card_expiry=20/12\n" +
                "&cvv=476\n" +
                "&ip_address=203.208.22.44\n" +
                "&site_url=www.mydomain.com\n" +
                "&first_name=khushali\n" +
                "&last_name=rathod\n" +
                "&address1=ggon\n" +
                "&city=mumbai\n" +
                "&state=MH\n" +
                "&country_code=IN\n" +
                "&zip_code=400062\n" +
                "&email=khushali.rathod@transactworld.com" +
                "&phone no=1234567890";

        System.out.println("request---"+request);
        String response= TRAXXUtils.doPostHTTPSURLConnection(TESTURL, request);
        System.out.println("response---"+response);
    }

    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of TRAXXPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest                      = gatewayAccount.isTest();
        String is3dSupported                = gatewayAccount.get_3DSupportAccount();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String site_url      = gatewayAccount.getFRAUD_FTP_PATH();
        String request_type    = "AUTHORIZE";
        String trxn_type       = "CAPTURE";

        String first_name  = "";
        String last_name   = "";
        String address1    = "";
        String city        = "";
        String zip_code    = "";
        String phone_no    = "";
        String email       = "";
        String country_code    = "";
        String state           = "";
        String card_account    = "";
        String cardExpiryMonth = "";
        String cardExpiryYear  = "";
        String card_expiry    = "";
        String cvv            = "";
        String ip_address     = "";
        String amount         = "";
        String currency_code  =  "";
        String track_id       = "";
        String payment_method = "";
        String REQUEST_URL    = "";
        String NOTIFY_URL     = "";
        String RETURN_URL     = "";

        String payment_brand             = transactionDetailsVO.getCardType();
        StringBuffer requestParameter    = new StringBuffer();
        StringBuffer requestLogParameter = new StringBuffer();

        try
        {

            if (isTest)
            {
                if("Y".equalsIgnoreCase(is3dSupported)){
                    REQUEST_URL = RB.getString("TEST_3D_SALE_URL");
                }else{
                    REQUEST_URL = RB.getString("TEST_SALE_URL");
                }

            }
            else
            {
                if("Y".equalsIgnoreCase(is3dSupported)){
                    REQUEST_URL = RB.getString("LIVE_3D_SALE_URL");
                }else{
                    REQUEST_URL = RB.getString("LIVE_SALE_URL");
                }

            }
            transactionLogger.error("Sale Request URL ===> " +track_id+" is3dSupported "+is3dSupported+" "+REQUEST_URL);

            track_id        = trackingId;

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                RETURN_URL = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingId;
                NOTIFY_URL = "https://"+commMerchantVO.getHostUrl()+RB.getString("NOTIFY_HOST_URL");
            }
            else
            {
                RETURN_URL = RB.getString("RETURN_URL")+trackingId;
                NOTIFY_URL = RB.getString("NOTIFY_URL")+trackingId;
            }
            transactionLogger.error("RETURN_URL URL ----"+RETURN_URL);
            transactionLogger.error("NOTIFY_URL URL ----"+NOTIFY_URL);

            if(functions.isValueNull(transactionDetailsVO.getCurrency())){
                currency_code = transactionDetailsVO.getCurrency();
            }

            amount         = transactionDetailsVO.getAmount();
            payment_method = TRAXXUtils.getPaymentBrand(payment_brand);
            card_account    = commCardDetailsVO.getCardNum();
            cardExpiryMonth = commCardDetailsVO.getExpMonth();
            cardExpiryYear  = TRAXXUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            card_expiry     = cardExpiryMonth+"/"+cardExpiryYear;
            cvv             = commCardDetailsVO.getcVV();

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
                ip_address  = commAddressDetailsVO.getCardHolderIpAddress();
            }else {
                ip_address  = commAddressDetailsVO.getIp();
            }

            transactionLogger.error("Return URL ----"+site_url);

            if(functions.isValueNull(commAddressDetailsVO.getFirstname())){
                first_name = commAddressDetailsVO.getFirstname();
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname())){
                last_name  = commAddressDetailsVO.getLastname();
            }


            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city       = commAddressDetailsVO.getCity();
            }
            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                address1    = commAddressDetailsVO.getStreet() + "," + city;
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip_code   = commAddressDetailsVO.getZipCode();
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                phone_no      = commAddressDetailsVO.getPhone();
            }

            if(functions.isValueNull(commAddressDetailsVO.getState())){

                state      = commAddressDetailsVO.getState();

                if(state.length() !=2){
                    state = TRAXXUtils.getStateCode(state);
                }
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                email      = commAddressDetailsVO.getEmail();
            }
            transactionLogger.error("commAddressDetailsVO.getCountry() >>>>>>>>> "+commAddressDetailsVO.getCountry());
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country_code    = commAddressDetailsVO.getCountry();
                if(country_code.length() !=2){
                    country_code =  TRAXXUtils.getCountryCode(country_code);
                }
            }

            requestParameter.append("merchant_id="+merchantId);
            requestParameter.append("&username="+merchantName);
            requestParameter.append("&password="+password);
            requestParameter.append("&request_type="+request_type);
            requestParameter.append("&trxn_type="+trxn_type);
            requestParameter.append("&track_id="+track_id);
            requestParameter.append("&currency_code="+currency_code);
            requestParameter.append("&amount="+amount);
            requestParameter.append("&payment_method="+payment_method);
            requestParameter.append("&ip_address="+ip_address);
            requestParameter.append("&site_url="+site_url);
            requestParameter.append("&first_name="+first_name);
            requestParameter.append("&last_name="+last_name);
            requestParameter.append("&address1="+address1);
            requestParameter.append("&city="+city);
            requestParameter.append("&state="+state);
            requestParameter.append("&country_code="+country_code);
            requestParameter.append("&zip_code="+zip_code);
            requestParameter.append("&email="+email);
            requestParameter.append("&phone_no="+phone_no);
           requestParameter.append("&reserve1="+RETURN_URL);
           requestParameter.append("&reserve2="+NOTIFY_URL);

            requestLogParameter = new StringBuffer(requestParameter.toString());

            requestParameter.append("&card_account="+card_account);
            requestParameter.append("&card_expiry="+card_expiry);
            requestParameter.append("&cvv="+cvv);

            requestLogParameter.append("&card_account="+functions.maskingPan(card_account));
            requestLogParameter.append("&card_expiry="+functions.maskingNumber(card_expiry));
            requestLogParameter.append("&cvv="+functions.maskingNumber(cvv));

            //transactionLogger.error("Sale Request Parameter ===> "+is3dSupported+"  " +trackingId+" "+requestParameter);
            transactionLogger.error("Sale Request Log Parameter ===> " +trackingId+" "+requestLogParameter);

            String response = TRAXXUtils.doPostFormHTTPSURLConnectionClient(REQUEST_URL,requestParameter.toString(),request_type,merchantName,password,merchantId);

            transactionLogger.error("Sale Response  ===> " +trackingId+" "+response);

            if(functions.isValueNull(response))
            {

                HashMap<String, String> responseHM = (HashMap<String, String>) TRAXXUtils.readSaleXMLReponse(response);

                transactionLogger.error("Sale responseHM  ===> " + trackingId + " " + responseHM);

                if (functions.isValueNull(response) && responseHM != null && !responseHM.isEmpty())
                {
                    String resp_code = "";
                    String resp_desc = "";
                    String resp_time = "";
                    String request_Type = "";
                    String merchant_id = "";
                    String rrn = "";
                    String rebill_id = "";
                    String currencyCode = "";
                    String trxn_amount = "";

                    if (responseHM.containsKey("resp_code"))
                    {
                        resp_code = responseHM.getOrDefault("resp_code", "");
                    }
                    if (responseHM.containsKey("resp_desc"))
                    {
                        resp_desc = responseHM.getOrDefault("resp_desc", "");
                    }
                    if (responseHM.containsKey("request_type"))
                    {
                        request_Type = responseHM.getOrDefault("request_type", "");
                    }
                    if (responseHM.containsKey("merchant_id"))
                    {
                        merchant_id = responseHM.getOrDefault("merchant_id", "");
                    }
                    if (responseHM.containsKey("track_id"))
                    {
                        track_id = responseHM.getOrDefault("track_id", "");
                    }
                    if (responseHM.containsKey("rrn"))
                    {
                        rrn = responseHM.getOrDefault("rrn", "");
                    }
                    if (responseHM.containsKey("rebill_id"))
                    {
                        rebill_id = responseHM.getOrDefault("rebill_id", "");
                    }
                    if (responseHM.containsKey("resp_time"))
                    {
                        resp_time = responseHM.getOrDefault("resp_time", "");
                    }
                    if (responseHM.containsKey("currency_code"))
                    {
                        currencyCode = responseHM.getOrDefault("currency_code", "");
                    }
                    if (responseHM.containsKey("trxn_amount"))
                    {
                        trxn_amount = responseHM.getOrDefault("trxn_amount", "");
                    }

                    if ("0".equalsIgnoreCase(resp_code) && "Payment Success".equalsIgnoreCase(resp_desc))
                    {
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(resp_desc);
                        comm3DResponseVO.setDescription(resp_desc);
                        comm3DResponseVO.setAmount(trxn_amount);
                        comm3DResponseVO.setTransactionId(rrn);
                        comm3DResponseVO.setResponseHashInfo(rrn);
                        comm3DResponseVO.setRrn(rrn);
                        comm3DResponseVO.setCurrency(currencyCode);
                        comm3DResponseVO.setErrorCode(resp_code);
                        comm3DResponseVO.setAuthCode(resp_code);
                        comm3DResponseVO.setResponseTime(resp_time);
                        comm3DResponseVO.setMerchantId(merchant_id);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else if ("TR402".equalsIgnoreCase(resp_code) || "2".equalsIgnoreCase(resp_code) || "TR22".equalsIgnoreCase(resp_code) || "TR701".equalsIgnoreCase(resp_code) || "TR01".equalsIgnoreCase(resp_code) || "TR4023".equalsIgnoreCase(resp_code) || "TR4024".equalsIgnoreCase(resp_code)
                            || "TR4025".equalsIgnoreCase(resp_code) || "TR4026".equalsIgnoreCase(resp_code) || "TR4027".equalsIgnoreCase(resp_code))
                    {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setTransactionStatus("failed");
                        comm3DResponseVO.setRemark(resp_desc);
                        comm3DResponseVO.setDescription(resp_desc);
                        comm3DResponseVO.setAmount(trxn_amount);
                        comm3DResponseVO.setTransactionId(rrn);
                        comm3DResponseVO.setResponseHashInfo(rrn);
                        comm3DResponseVO.setRrn(rrn);
                        comm3DResponseVO.setCurrency(currencyCode);
                        comm3DResponseVO.setErrorCode(resp_code);
                        comm3DResponseVO.setAuthCode(resp_code);
                        comm3DResponseVO.setResponseTime(resp_time);
                        comm3DResponseVO.setMerchantId(merchant_id);

                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                    if("Y".equalsIgnoreCase(is3dSupported)){
                        comm3DResponseVO.setThreeDVersion("Non-3D");
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

            }else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }


        }
        catch (Exception e)
        {
            transactionLogger.error("Exception In TRAXXPaymentGateway processSale ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(TRAXXPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processAuthentication() of TRAXXPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest          = gatewayAccount.isTest();
        String is3dSupported    = gatewayAccount.get_3DSupportAccount();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String site_url      = gatewayAccount.getFRAUD_FTP_SITE();
        String request_type  = "AUTHORIZE";
        String trxn_type     = "PREAUTH";

        String first_name   = "";
        String last_name    = "";
        String address1     = "";
        String city         = "";
        String zip_code     = "";
        String phone_no     = "";
        String email        = "";
        String country_code = "";
        String state        = "";
        String card_account    = "";
        String cardExpiryMonth = "";
        String cardExpiryYear  = "";
        String card_expiry     = "";
        String cvv             = "";
        String ip_address     = "";
        String amount         = "";
        String currency_code  =  "";
        String track_id        = "";
        String payment_method  = "";
        String REQUEST_URL          = "";
        String RETURN_URL          = "";
        String NOTIFY_URL          = "";

        StringBuffer requestParameter       = new StringBuffer();
        StringBuffer requestLogParameter    = new StringBuffer();

        String payment_brand = transactionDetailsVO.getCardType();

        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }
            transactionLogger.error("REQUEST_URL URL ----"+REQUEST_URL);

            track_id        = trackingId;

            if(functions.isValueNull(transactionDetailsVO.getCurrency())){
                currency_code = transactionDetailsVO.getCurrency();
            }

            amount         = transactionDetailsVO.getAmount();
            payment_method = TRAXXUtils.getPaymentBrand(payment_brand);
            card_account    = commCardDetailsVO.getCardNum();
            cardExpiryMonth = commCardDetailsVO.getExpMonth();
            cardExpiryYear  = TRAXXUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            card_expiry     = cardExpiryMonth+"/"+cardExpiryYear;
            cvv             = commCardDetailsVO.getcVV();

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
                ip_address  = commAddressDetailsVO.getCardHolderIpAddress();
            }else {
                ip_address  = commAddressDetailsVO.getIp();
            }

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                RETURN_URL = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingId;
                NOTIFY_URL = "https://"+commMerchantVO.getHostUrl()+RB.getString("NOTIFY_HOST_URL");
            }
            else
            {
                RETURN_URL = RB.getString("RETURN_URL")+trackingId;
                NOTIFY_URL = RB.getString("NOTIFY_URL")+trackingId;
            }
            transactionLogger.error("Return URL ----"+RETURN_URL);

            if(functions.isValueNull(commAddressDetailsVO.getFirstname())){
                first_name = commAddressDetailsVO.getFirstname();
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname())){
                last_name  = commAddressDetailsVO.getLastname();
            }


            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city       = commAddressDetailsVO.getCity();
            }
            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                address1    = commAddressDetailsVO.getStreet() + "," + city;
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip_code   = commAddressDetailsVO.getZipCode();
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                phone_no      = commAddressDetailsVO.getPhone();
            }

            if(functions.isValueNull(commAddressDetailsVO.getState())){
                state      = commAddressDetailsVO.getState();

                if(state.length() != 2){
                    state = TRAXXUtils.getStateCode(state);
                }
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                email      = commAddressDetailsVO.getEmail();
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country_code    = commAddressDetailsVO.getCountry();
                if(country_code.length() !=2){
                    country_code =  TRAXXUtils.getCountryCode(country_code);
                }
            }

            requestParameter.append("merchant_id="+merchantId);
            requestParameter.append("&username="+merchantName);
            requestParameter.append("&password="+password);
            requestParameter.append("&request_type="+request_type);
            requestParameter.append("&trxn_type="+trxn_type);
            requestParameter.append("&site_url="+site_url);
            requestParameter.append("&track_id="+track_id);
            requestParameter.append("&currency_code="+currency_code);
            requestParameter.append("&amount="+amount);
            requestParameter.append("&payment_method="+payment_method);
            requestParameter.append("&ip_address="+ip_address);
            requestParameter.append("&site_url="+site_url);
            requestParameter.append("&first_name="+first_name);
            requestParameter.append("&last_name="+last_name);
            requestParameter.append("&address1="+address1);
            requestParameter.append("&city="+city);
            requestParameter.append("&state="+state);
            requestParameter.append("&country_code="+country_code);
            requestParameter.append("&zip_code="+zip_code);
            requestParameter.append("&email="+email);
            requestParameter.append("&phone_no="+phone_no);
            requestParameter.append("&reserve1="+RETURN_URL);
            requestParameter.append("&reserve2="+NOTIFY_URL);

            requestLogParameter = new StringBuffer(requestParameter.toString());

            requestParameter.append("&card_account="+card_account);
            requestParameter.append("&card_expiry="+card_expiry);
            requestParameter.append("&cvv="+cvv);

            requestLogParameter.append("&card_account="+functions.maskingPan(card_account));
            requestLogParameter.append("&card_expiry="+functions.maskingNumber(card_expiry));
            requestLogParameter.append("&cvv="+functions.maskingNumber(cvv));

            //transactionLogger.error("Auth Request Parameter ===> " +track_id+" "+requestParameter);
            transactionLogger.error("Auth Request Log Parameter ===> " +track_id+" "+requestLogParameter);


            String response = TRAXXUtils.doPostFormHTTPSURLConnectionClient(REQUEST_URL,requestParameter.toString(),request_type,merchantName,password,merchantId);

            transactionLogger.error("Sale Auth  ===> " +track_id+" "+response);

            HashMap<String,String> responseHM = (HashMap<String, String>) TRAXXUtils.readSaleXMLReponse(response);

            transactionLogger.error("Sale responseHM  ===> " +trackingId+" "+responseHM);

            if(functions.isValueNull(response) && !responseHM.isEmpty())
            {
                String resp_code    = "";
                String resp_desc    = "";
                String resp_time    = "";
                String request_Type = "";
                String merchant_id  = "";
                String trackId      = "";
                String rrn          = "";
                String rebill_id    = "";
                String paymentMethod    = "";
                String currencyCode     = "";
                String trxn_amount      = "";

                if(responseHM.containsKey("resp_code")){
                    resp_code = responseHM.getOrDefault("resp_code","");
                }
                if(responseHM.containsKey("resp_desc")){
                    resp_desc = responseHM.getOrDefault("resp_desc","");
                }
                if(responseHM.containsKey("request_type")){
                    request_Type = responseHM.getOrDefault("request_type","");
                }
                if(responseHM.containsKey("merchant_id")){
                    merchant_id = responseHM.getOrDefault("merchant_id","");
                }
                if(responseHM.containsKey("track_id")){
                    track_id = responseHM.getOrDefault("track_id","");
                }
                if(responseHM.containsKey("rrn")){
                    rrn = responseHM.getOrDefault("rrn","");
                }
                if(responseHM.containsKey("rebill_id")){
                    rebill_id = responseHM.getOrDefault("rebill_id","");
                }
                if(responseHM.containsKey("resp_time")){
                    resp_time = responseHM.getOrDefault("resp_time","");
                }
                if(responseHM.containsKey("currency_code")){
                    currencyCode = responseHM.getOrDefault("currency_code","");
                }
                if(responseHM.containsKey("trxn_amount")){
                    trxn_amount = responseHM.getOrDefault("trxn_amount","");
                }


                if("0".equalsIgnoreCase(resp_code) && "Payment Success".equalsIgnoreCase(resp_desc))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currencyCode);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(resp_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if("TR701".equalsIgnoreCase(resp_code) || "TR01".equalsIgnoreCase(resp_code)  || "TR4023".equalsIgnoreCase(resp_code) || "TR4024".equalsIgnoreCase(resp_code)
                        || "TR4025".equalsIgnoreCase(resp_code) || "TR4026".equalsIgnoreCase(resp_code) || "TR4027".equalsIgnoreCase(resp_code) )
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currencyCode);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(resp_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);

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
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception In TRAXXPaymentGateway processAuthentication ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(TRAXXPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processCapture(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processCapture() of TRAXXPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String site_url      = gatewayAccount.getFRAUD_FTP_SITE();
        String request_type    = "AUTHORIZE";
        String trxn_type       = "CAPTURE";

        String first_name  = "";
        String last_name    = "";
        String address1     = "";
        String city         = "";
        String zip_code     = "";
        String phone_no     = "";
        String email        = "";
        String country_code = "";
        String state        = "";

        String card_account    = "";
        String cardExpiryMonth = "";
        String cardExpiryYear  = "";
        String card_expiry     = "";
        String cvv             = "";

        String ip_address     = "";
        String amount         = "";
        String currency_code  =  "";

        String track_id        = "";
        String payment_method  = "";

        String REQUEST_URL          = "";
        String payment_brand = transactionDetailsVO.getCardType();

        StringBuffer requestParameter   = new StringBuffer();
        StringBuffer requestLogParameter = new StringBuffer();
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }
            transactionLogger.error("REQUEST_URL URL ----"+REQUEST_URL);

            track_id        = trackingId;

            if(functions.isValueNull(transactionDetailsVO.getCurrency())){
                currency_code = transactionDetailsVO.getCurrency();
            }

            amount         = transactionDetailsVO.getAmount();
            payment_method = TRAXXUtils.getPaymentBrand(payment_brand);
            card_account    = commCardDetailsVO.getCardNum();
            cardExpiryMonth = commCardDetailsVO.getExpMonth();
            cardExpiryYear  = TRAXXUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            card_expiry     = cardExpiryMonth+"/"+cardExpiryYear;
            cvv             = commCardDetailsVO.getcVV();

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
                ip_address  = commAddressDetailsVO.getCardHolderIpAddress();
            }else {
                ip_address  = commAddressDetailsVO.getIp();
            }

            transactionLogger.error("Return URL ----"+site_url);

            if(functions.isValueNull(commAddressDetailsVO.getFirstname())){
                first_name = commAddressDetailsVO.getFirstname();
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname())){
                last_name  = commAddressDetailsVO.getLastname();
            }


            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city       = commAddressDetailsVO.getCity();
            }
            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                address1    = commAddressDetailsVO.getStreet() + "," + city;
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip_code   = commAddressDetailsVO.getZipCode();
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                phone_no      = commAddressDetailsVO.getPhone();
            }

            if(functions.isValueNull(commAddressDetailsVO.getState())){

                state      = commAddressDetailsVO.getState();

                if(state.length() !=2){
                    state = TRAXXUtils.getStateCode(state);
                }
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                email      = commAddressDetailsVO.getEmail();
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country_code    = commAddressDetailsVO.getCountry();
                if(country_code.length() !=2){
                    country_code =  TRAXXUtils.getCountryCode(country_code);
                }
            }

            requestParameter.append("merchant_id="+merchantId);
            requestParameter.append("&username="+merchantName);
            requestParameter.append("&password="+password);
            requestParameter.append("&request_type="+request_type);
            requestParameter.append("&trxn_type="+trxn_type);
            requestParameter.append("&track_id="+track_id);
            requestParameter.append("&currency_code="+currency_code);
            requestParameter.append("&amount="+amount);
            requestParameter.append("&payment_method="+payment_method);
            requestParameter.append("&ip_address="+ip_address);
            requestParameter.append("&site_url="+site_url);
            requestParameter.append("&first_name="+first_name);
            requestParameter.append("&last_name="+last_name);
            requestParameter.append("&address1="+address1);
            requestParameter.append("&city="+city);
            requestParameter.append("&state="+state);
            requestParameter.append("&country_code="+country_code);
            requestParameter.append("&zip_code="+zip_code);
            requestParameter.append("&email="+email);
            requestParameter.append("&phone_no="+phone_no);
            requestParameter.append("&email="+email);

            requestLogParameter =  new StringBuffer(requestParameter.toString());

            requestParameter.append("&card_account="+card_account);
            requestParameter.append("&card_expiry="+card_expiry);
            requestParameter.append("&cvv="+cvv);


            requestLogParameter.append("&card_account="+functions.maskingPan(card_account));
            requestLogParameter.append("&card_expiry="+functions.maskingNumber(card_expiry));
            requestLogParameter.append("&cvv="+functions.maskingNumber(cvv));

            //transactionLogger.error("Capture Request ===> " +track_id+" "+requestParameter);
            transactionLogger.error("Capture Request Log ===> " +track_id+" "+requestLogParameter);

            String response = TRAXXUtils.doPostFormHTTPSURLConnectionClient(REQUEST_URL,requestParameter.toString(),request_type,merchantName,password,merchantId);

            transactionLogger.error("Sale Response  ===> " +trackingId+" "+response);

            HashMap<String,String> responseHM = (HashMap<String, String>) TRAXXUtils.readSaleXMLReponse(response);

            transactionLogger.error("Sale responseHM  ===> " +trackingId+" "+responseHM);
            transactionLogger.error("Sale Auth  ===> " +track_id+" "+response);

            if(functions.isValueNull(response) && responseHM != null  && !responseHM.isEmpty()){
                String resp_code    = "";
                String resp_desc    = "";
                String resp_time    = "";
                String request_Type = "";
                String merchant_id  = "";
                String trackId      = "";
                String rrn          = "";
                String rebill_id    = "";
                String paymentMethod    = "";
                String currencyCode     = "";
                String trxn_amount      = "";

                if(responseHM.containsKey("resp_code")){
                    resp_code = responseHM.getOrDefault("resp_code","");
                }
                if(responseHM.containsKey("resp_desc")){
                    resp_desc = responseHM.getOrDefault("resp_desc","");
                }
                if(responseHM.containsKey("request_type")){
                    request_Type = responseHM.getOrDefault("request_type","");
                }
                if(responseHM.containsKey("merchant_id")){
                    merchant_id = responseHM.getOrDefault("merchant_id","");
                }
                if(responseHM.containsKey("track_id")){
                    track_id = responseHM.getOrDefault("track_id","");
                }
                if(responseHM.containsKey("rrn")){
                    rrn = responseHM.getOrDefault("rrn","");
                }
                if(responseHM.containsKey("resp_time")){
                    resp_time = responseHM.getOrDefault("resp_time","");
                }
                if(responseHM.containsKey("currency_code")){
                    currencyCode = responseHM.getOrDefault("currency_code","");
                }
                if(responseHM.containsKey("trxn_amount")){
                    trxn_amount = responseHM.getOrDefault("trxn_amount","");
                }

                if("0".equalsIgnoreCase(resp_code) && "Payment Success".equalsIgnoreCase(resp_desc))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currencyCode);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(resp_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if("TR701".equalsIgnoreCase(resp_code) || "TR01".equalsIgnoreCase(resp_code)  || "TR4023".equalsIgnoreCase(resp_code) || "TR4024".equalsIgnoreCase(resp_code)
                        || "TR4025".equalsIgnoreCase(resp_code) || "TR4026".equalsIgnoreCase(resp_code) || "TR4027".equalsIgnoreCase(resp_code) )
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currencyCode);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(resp_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);

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

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception In TRAXXPaymentGateway processCapture() ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(TRAXXPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of TRAXXPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId       = gatewayAccount.getMerchantId();
        String username         = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest          = gatewayAccount.isTest();

        String request_type             = "TRACKAUTH";
        String REQUEST_URL              = "";
        StringBuffer requestParameter   = new StringBuffer();

        try
        {
            if (isTest){
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }else {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            requestParameter.append("request_type="+request_type);
            requestParameter.append("&merchant_id="+merchantId);
            requestParameter.append("&username="+username);
            requestParameter.append("&password="+password);
            requestParameter.append("&track_id="+trackingId);
            requestParameter.append("&rrn="+transactionDetailsVO.getPreviousTransactionId());

            transactionLogger.error("REQUEST_URL >>>>> "+trackingId+" "+REQUEST_URL);
            transactionLogger.error("Inquiry Request >>>>>> "+trackingId +" "+requestParameter.toString());

            String response = TRAXXUtils.doPostFormHTTPSURLConnectionClient(REQUEST_URL,requestParameter.toString(),request_type,username,password,merchantId);

            transactionLogger.error("Inquiry Response >>>>>> "+trackingId +" "+response.toString());

            HashMap<String,String> responseParam = (HashMap<String, String>) TRAXXUtils.readSaleXMLReponse(response);

            transactionLogger.error("Response HM >>>>>> "+trackingId +" "+responseParam);

            if(responseParam != null && !responseParam.isEmpty()){

                String resp_code            = "";
                String resp_desc      = "";
                String resp_time      = "";
                String trxn_type      = "";
                String merchant_id    = "";
                String rrn            = "";
                String currency_code  = "";
                String trxn_amount    = "";

                if(responseParam.containsKey("resp_code")){
                    resp_code = responseParam.getOrDefault("resp_code","");
                }
                if(responseParam.containsKey("resp_desc")){
                    resp_desc = responseParam.getOrDefault("resp_desc","");
                }
                if(responseParam.containsKey("resp_time")){
                    resp_time = responseParam.getOrDefault("resp_time","");
                }
                if(responseParam.containsKey("trxn_type")){
                    trxn_type = responseParam.getOrDefault("trxn_type","");
                }
                if(responseParam.containsKey("merchant_id")){
                    merchant_id = responseParam.getOrDefault("merchant_id","");
                }
                if(responseParam.containsKey("rrn")){
                    rrn = responseParam.getOrDefault("rrn","");
                }
                if(responseParam.containsKey("currency_code")){
                    currency_code = responseParam.getOrDefault("currency_code","");
                }
                if(responseParam.containsKey("trxn_amount")){
                    trxn_amount = responseParam.getOrDefault("trxn_amount","");
                }

                if("0".equalsIgnoreCase(resp_code) && "Payment Success".equalsIgnoreCase(resp_desc))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currency_code);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(resp_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if("TR402".equalsIgnoreCase(resp_code) || "2".equalsIgnoreCase(resp_code) || "TR22".equalsIgnoreCase(resp_code) || "TR701".equalsIgnoreCase(resp_code) || "TR01".equalsIgnoreCase(resp_code) /*|| "TR16".equalsIgnoreCase(resp_code)*/ || "TR4023".equalsIgnoreCase(resp_code) || "TR4024".equalsIgnoreCase(resp_code)
                        || "TR4025".equalsIgnoreCase(resp_code) || "TR4026".equalsIgnoreCase(resp_code) || "TR4027".equalsIgnoreCase(resp_code) )
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currency_code);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(resp_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);

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
            transactionLogger.error("Exception In TRAXXPaymentGateway processQuery() ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(TRAXXPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }



    public GenericResponseVO processRefund(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processRefund() of TRAXXPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        boolean isTest                      = gatewayAccount.isTest();
        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String request_type    = "AUTHORIZE";
        String trxn_type       = "REFUND";

        String REQUEST_URL = "";
        String track_id    = "";
        String amount      = "";
        String ip_address  = "";
        String ref_id      = "";
        String phone_no    = "";
        StringBuffer requestParameter = new StringBuffer();

        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("REFUND_TEST_URL");
            }else
            {
                REQUEST_URL = RB.getString("REFUND_LIVE_URL");
            }

            track_id = trackingId;
            amount   = transactionDetailsVO.getAmount();

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
                ip_address  = commAddressDetailsVO.getCardHolderIpAddress();
            }else {
                ip_address  = commAddressDetailsVO.getIp();
            }

            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                phone_no      = commAddressDetailsVO.getPhone();
            }
            if(functions.isValueNull(transactionDetailsVO.getPreviousTransactionId())){
                ref_id      = transactionDetailsVO.getPreviousTransactionId();
            }

            requestParameter.append("merchant_id="+merchantId);
            requestParameter.append("&username="+merchantName);
            requestParameter.append("&password="+password);
            requestParameter.append("&request_type="+request_type);
            requestParameter.append("&trxn_type="+trxn_type);
            requestParameter.append("&track_id="+track_id);
            requestParameter.append("&amount="+amount);
            requestParameter.append("&ip_address="+ip_address);
            requestParameter.append("&phone_no="+phone_no);
            requestParameter.append("&ref_id="+ref_id);

            transactionLogger.error("REQUEST_URL  >>>>>>> "+trackingId +" "+REQUEST_URL);
            transactionLogger.error("Refund Request Parameter >>>>>>> "+trackingId +" "+requestParameter.toString());

            String response = TRAXXUtils.doPostFormHTTPSURLConnectionClient(REQUEST_URL,requestParameter.toString(),request_type,merchantName,password,merchantId);

            transactionLogger.error("Refund Response ===> " +trackingId+" "+response);

            HashMap<String,String> responseParam = (HashMap<String, String>) TRAXXUtils.readSaleXMLReponse(response);
            transactionLogger.error("response Param HM >>>>>> "+trackingId +" "+responseParam);

            if(responseParam != null && !responseParam.isEmpty()){

                String resp_code      = "";
                String resp_desc      = "";
                String resp_time      = "";
                String trxn_typeResp  = "";
                String merchant_id    = "";
                String rrn            = "";
                String payment_method = "";
                String currency_code  = "";
                String auth_code      = "";
                String descriptor     = "";
                String trxn_amount    = "";

                if(responseParam.containsKey("resp_code")){
                    resp_code = responseParam.getOrDefault("resp_code","");
                }
                if(responseParam.containsKey("resp_desc")){
                    resp_desc = responseParam.getOrDefault("resp_desc","");
                }
                if(responseParam.containsKey("resp_time")){
                    resp_time = responseParam.getOrDefault("resp_time","");
                }
                if(responseParam.containsKey("trxn_type")){
                    trxn_typeResp = responseParam.getOrDefault("trxn_type","");
                }
                if(responseParam.containsKey("merchant_id")){
                    merchant_id = responseParam.getOrDefault("merchant_id","");
                }
                if(responseParam.containsKey("track_id")){
                    track_id = responseParam.getOrDefault("track_id","");
                }
                if(responseParam.containsKey("rrn")){
                    rrn = responseParam.getOrDefault("rrn","");
                }
                if(responseParam.containsKey("currency_code")){
                    currency_code = responseParam.getOrDefault("currency_code","");
                }
                if(responseParam.containsKey("auth_code")){
                    auth_code = responseParam.getOrDefault("auth_code","");
                }
                if(responseParam.containsKey("descriptor")){
                    descriptor = responseParam.getOrDefault("descriptor","");
                }
                if(responseParam.containsKey("trxn_amount")){
                    trxn_amount = responseParam.getOrDefault("trxn_amount","");
                }

                if("0".equalsIgnoreCase(resp_code) && "Processing Completed".equalsIgnoreCase(resp_desc))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currency_code);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(auth_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if("TR701".equalsIgnoreCase(resp_code) || "TR01".equalsIgnoreCase(resp_code) || "TR16".equalsIgnoreCase(resp_code) || "TR4023".equalsIgnoreCase(resp_code) || "TR4024".equalsIgnoreCase(resp_code)
                        || "TR4025".equalsIgnoreCase(resp_code) || "TR4026".equalsIgnoreCase(resp_code) || "TR4027".equalsIgnoreCase(resp_code) )
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(resp_desc);
                    comm3DResponseVO.setDescription(resp_desc);
                    comm3DResponseVO.setAmount(trxn_amount);
                    comm3DResponseVO.setTransactionId(rrn);
                    comm3DResponseVO.setResponseHashInfo(rrn);
                    comm3DResponseVO.setRrn(rrn);
                    comm3DResponseVO.setCurrency(currency_code);
                    comm3DResponseVO.setErrorCode(resp_code);
                    comm3DResponseVO.setAuthCode(resp_code);
                    comm3DResponseVO.setResponseTime(resp_time);
                    comm3DResponseVO.setMerchantId(merchant_id);

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
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception In TRAXXPaymentGateway processRefund() ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(TRAXXPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processVoid(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(".......... Entering processVoid() of TRAXXPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest      = gatewayAccount.isTest();
        String merchantId   = gatewayAccount.getMerchantId();
        String merchantName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password     = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String request_type = "AUTHORIZE";
        String trxn_type    = "VOID";

        String track_id        = "";
        String amount          = "";
        String ip_address      = "";
        String ref_id          = "";
        String REQUEST_URL     = "";

        StringBuffer requestParameter = new StringBuffer();

        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }
            transactionLogger.error("REQUEST_URL URL ----"+REQUEST_URL);

            track_id        = trackingId;
            amount         = transactionDetailsVO.getAmount();

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
                ip_address  = commAddressDetailsVO.getCardHolderIpAddress();
            }else {
                ip_address  = commAddressDetailsVO.getIp();
            }

            requestParameter.append("merchant_id="+merchantId);
            requestParameter.append("&username="+merchantName);
            requestParameter.append("&password="+password);
            requestParameter.append("&request_type="+request_type);
            requestParameter.append("&trxn_type="+trxn_type);
            requestParameter.append("&track_id="+track_id);
            requestParameter.append("&amount="+amount);
            requestParameter.append("&ip_address="+ip_address);
            requestParameter.append("&ref_id="+ref_id);

            transactionLogger.error("Void Request Parameter ===> " +track_id+" "+requestParameter);


            String response = TRAXXUtils.doPostFormHTTPSURLConnectionClient(REQUEST_URL,requestParameter.toString(),request_type,merchantName,password,merchantId);

            transactionLogger.error("Void Response ===> " +track_id+" "+response);

            if(functions.isValueNull(response)){

            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception In TRAXXPaymentGateway processVoid() ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(TRAXXPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processPayout() of TRAXXPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String site_url      = gatewayAccount.getFRAUD_FTP_SITE();
        String request_type    = "AUTHORIZE";
        String trxn_type       = "PAYOUT";

        String first_name  = "";
        String last_name   = "";
        String address1     = "";
        String city        = "";
        String zip_code    = "";
        String phone_no       = "";
        String email       = "";
        String country_code     = "";
        String state                 = "";

        String card_account         = "";
        String cardExpiryMonth      = "";
        String cardExpiryYear       = "";
        String card_expiry       = "";
        String cvv                   = "";

        String ip_address           = "";
        String amount               = "";
        String currency_code        =  "";

        String track_id         = "";
        String REQUEST_URL      = "";
        String payment_brand    = transactionDetailsVO.getCardType();

        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }
            transactionLogger.error("REQUEST_URL URL ----"+REQUEST_URL);

            track_id        = trackingId;

            if(functions.isValueNull(transactionDetailsVO.getCurrency())){
                currency_code = transactionDetailsVO.getCurrency();
            }

            amount         = transactionDetailsVO.getAmount();
            card_account    = commCardDetailsVO.getCardNum();
            cardExpiryMonth = commCardDetailsVO.getExpMonth();
            cardExpiryYear  = TRAXXUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            card_expiry     = cardExpiryMonth+"/"+cardExpiryYear;
            cvv             = commCardDetailsVO.getcVV();

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
                ip_address  = commAddressDetailsVO.getCardHolderIpAddress();
            }else {
                ip_address  = commAddressDetailsVO.getIp();
            }

            if(functions.isValueNull(commAddressDetailsVO.getFirstname())){
                first_name = commAddressDetailsVO.getFirstname();
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname())){
                last_name  = commAddressDetailsVO.getLastname();
            }


            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city       = commAddressDetailsVO.getCity();
            }
            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                address1    = commAddressDetailsVO.getStreet() + "," + city;
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip_code   = commAddressDetailsVO.getZipCode();
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                phone_no      = commAddressDetailsVO.getPhone();
            }

            if(functions.isValueNull(commAddressDetailsVO.getState())){

                state      = commAddressDetailsVO.getState();

                if(state.length() !=2){
                    state = TRAXXUtils.getStateCode(state);
                }
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                email      = commAddressDetailsVO.getEmail();
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country_code    = commAddressDetailsVO.getCountry();
                if(country_code.length() !=2){
                    country_code =  TRAXXUtils.getCountryCode(country_code);
                }
            }

            StringBuffer requestParameter = new StringBuffer();

            /*requestParameter.append("merchantId="+merchantId);
            requestParameter.append("&merchantName="+merchantName);
            requestParameter.append("&password="+password);
            requestParameter.append("&request_type="+request_type);
            requestParameter.append("&trxn_type="+trxn_type);
            requestParameter.append("&track_id="+track_id);
            requestParameter.append("&currency_code="+currency_code);
            requestParameter.append("&amount="+amount);
            requestParameter.append("&card_account="+card_account);
            requestParameter.append("&card_expiry="+card_expiry);
            requestParameter.append("&cvv="+cvv);
            requestParameter.append("&ip_address="+ip_address);
            requestParameter.append("&site_url="+site_url);
            requestParameter.append("&first_name="+first_name);
            requestParameter.append("&last_name="+last_name);
            requestParameter.append("&address1="+address1);
            requestParameter.append("&city="+city);
            requestParameter.append("&state="+state);
            requestParameter.append("&country_code="+country_code);
            requestParameter.append("&zip_code="+zip_code);
            requestParameter.append("&email="+email);
            requestParameter.append("&phone_no="+phone_no);
            requestParameter.append("&email="+email);*/



            requestParameter.append("merchant_id="+merchantId);
            requestParameter.append("&username="+merchantName);
            requestParameter.append("&password="+password);
            requestParameter.append("&trxn_type="+trxn_type);
            requestParameter.append("request_type="+request_type);
            requestParameter.append("&track_id="+track_id);
            requestParameter.append("&currency_code="+currency_code);
            requestParameter.append("&amount="+amount);
            requestParameter.append("&ip_address="+ip_address);
            requestParameter.append("&first_name="+first_name);
            requestParameter.append("&last_name="+last_name);
            requestParameter.append("&address1="+address1);
            requestParameter.append("&city="+city);
            requestParameter.append("&state="+state);
            requestParameter.append("&country_code="+country_code);
            requestParameter.append("&zip_code="+zip_code);
            requestParameter.append("&email="+email);
            requestParameter.append("&phone_no="+phone_no);
            requestParameter.append("&site_url="+site_url);

            transactionLogger.error("Sale Request Parameter ===> " +track_id+" "+requestParameter);


        }
        catch (Exception e)
        {

            transactionLogger.error("Exception In TRAXXPaymentGateway processVoid() ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(TRAXXPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        /*comm3DResponseVO.setStatus("pending");
        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
        comm3DResponseVO.setTransactionStatus("pending");*/
        return comm3DResponseVO;
    }



}
