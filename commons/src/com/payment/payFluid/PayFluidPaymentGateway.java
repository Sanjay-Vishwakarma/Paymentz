package com.payment.payFluid;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Rubixpay.RubixpayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.verve.VervePaymentProcess;
import com.payment.verve.VerveUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Created by Admin on 12/19/2020.
 */
public class PayFluidPaymentGateway  extends AbstractPaymentGateway
{

    private static TransactionLogger transactionlogger = new TransactionLogger(PayFluidPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "payfluid";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payfluid");
    String redirecturl= "";
    public PayFluidPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("PayFluidPaymentGateway  accountid------------------->" + accountId);
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {


        transactionlogger.error("Entering processSale of PayFluidPaymentGateway......");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        RubixpayUtils rubixpayutils =new RubixpayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        VerveUtils verveUtils=new VerveUtils();
        // GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();


        try
        {
        String payment_Card= GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String bill_address="";
        String bill_city="";
        String bill_state="";
        String bill_country="";
        String bill_postalcode="";
        String payment_url="";
        String clientMobileNumber="";
        String request="";
        String nbcode="HDFC-BNK";
        String vpa="NA";
        String paymode=transactionDetailsVO.getPaymentType();
        transactionlogger.error("from system paymode is-------------->"+paymode);
        String paymentmode=verveUtils.getPaymentType(paymode);
        transactionlogger.error("request paymode is-------------->"+paymentmode);
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        boolean isTest = gatewayAccount.isTest();

        String id=gatewayAccount.getMerchantId();
        transactionlogger.error("id is-------------->"+id);

        String id2="";
        String lang="en";
        String otherInfo = "test222";
        id2 = Base64.getEncoder().encodeToString(id.getBytes());
        transactionlogger.error("Base64.getEncoder id2 is-------------->"+id2);
        String loginParam =gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionlogger.error("loginParam is-------------->"+loginParam);
        String RSAPublicKey =gatewayAccount.getFRAUD_FTP_PATH();
        transactionlogger.error("RSAPublicKey is-------------->"+RSAPublicKey);
        /*String serverkey ="3A6CCEEA-3036-4C24-A63C-DADFA5355276";
        transactionlogger.error("returnUrl is-------------->");*/


        String descr =trackingID;
        transactionlogger.error("descr is-------------->"+trackingID);
        String datetTime2=PayFluidUtils.fetchCurDate("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
        transactionlogger.error("datetTime2 is-------------->"+datetTime2);
        String clientName =commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        transactionlogger.error("clientName is-------------->"+clientName);

        String email="";
        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
        {
            email=commAddressDetailsVO.getEmail();
        }
        else{
            email="transaction@support.com";
        }
        transactionlogger.error("email is-------------->"+email);
        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
        {
            clientMobileNumber=commAddressDetailsVO.getPhone();
        }
        else{
            clientMobileNumber="9999999999";
        }
        transactionlogger.error("phone is-------------->"+clientMobileNumber);
        String transRef =trackingID;
        transactionlogger.error("transRef is-------------->"+transRef);
        String currency =transactionDetailsVO.getCurrency();
        transactionlogger.error("currencycode is-------------->"+currency);
        double amount= Double.parseDouble(transactionDetailsVO.getAmount());
        transactionlogger.error("amount is-------------->"+amount);
        String response_url =RB.getString("RUBIXPAY_RU")+trackingID;
        transactionlogger.error("response_url is-------------->"+response_url);
        String cancel_url =RB.getString("RUBIXPAY_RU")+trackingID+"&status=cancel";;
        transactionlogger.error("cancel_url is-------------->"+cancel_url);
        String redirectURL=RB.getString("RUBIXPAY_RU")+trackingID;;
        String sessionID="";
        String callbackURL=RB.getString("RUBIXPAY_RU")+trackingID;;
//******************secureZone API**********************
        PayFluidUtils payFluidUtils=new PayFluidUtils();

        String curDate=PayFluidUtils.fetchCurDate("yyyyMMddHHmmssSSS");
        String strToEncrypt = loginParam + "." + curDate;
        String apikey = Base64.getEncoder().encodeToString(PayFluidUtils.encrypt(strToEncrypt, RSAPublicKey));
        // String apikey="Hydz8SjBwe2xbKAPjRbh7UF5BWQXIYfmiiMzW26qqYhsngrn8MU88l2fKP3GDYOYKSIMthtLAgl0wddsYDl88r3/LiqkWHMMRE9qHzNv86/ZhyAbqiZFYl2VJx5v7PuR/WtQI572X156QWG+hRjOV939mV89faVj+4cEyS1Sc00=";
        String returnUrl="https://test.dsgintl.net/transaction/CommINFrontEndServlet";
       // String url="https://sixty40.net/paymentz/secureCredentials/";

            request="{\"id\":\""+id2+"\",\"apikey\":\""+apikey+"\",\"dateTM\":\""+curDate+"\"}";


        if (isTest){
            payment_url=RB.getString("secureCredentials");
            transactionlogger.error("TEST_SALE_URL------------------------->"+payment_url);


        }
        else {
            payment_url=RB.getString("secureCredentials");
            transactionlogger.error("LIVE_SALE_URL------------------------->"+payment_url);
        }

            String  response= payFluidUtils.doPostHttpUrlConnection(payment_url, request);

            transactionlogger.error("response--------------for--"+trackingID+"--"+response);



            JSONObject jsonResponse = new JSONObject(response);

            String approvalCode= jsonResponse.getString("approvalCode");
            String cmd= jsonResponse.getString("cmd");
            String datetime2= jsonResponse.getString("datetime");
            String kekExpiry= jsonResponse.getString("kekExpiry");
            String macExpiry= jsonResponse.getString("macExpiry");
            String mobile= jsonResponse.getString("mobile");
            String resultCode= jsonResponse.getString("resultCode");
            String resultMessage= jsonResponse.getString("resultMessage");
            String session= jsonResponse.getString("session");
            String kek= jsonResponse.getString("kek");
            // String kek= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj+2qxYVilNw682FY12zt7qGNo5eqASmtIyJ31a9rrbqnhyYIa0AV0Taa9Cl0AcjAoDRwPqxRkEqGADufMtsybCluECJ9Img+wPJ/3ewutWj6tV+B60U+Gi6zuy8KKKZf3Pexgn32KI/x5OzEymBzPZ3+1y2c3+qdnwhmuFE96RHftn9SlWfkMXIitTjjlcSrqqB2LqS3FV3j63nC0wmvhsOrv8WBtkOVTxY7KNDfCWoO3wwBqAf737yhjEsk3nei9YmddWcTcmPAmOm7dgFR+Lboa6NhFkF3x6VDkWAn1CYrO0bsfTaU4D31oB6XmR2Bw+PBX2nOjK6O+LkDw23wawIDAQAB.4e4e01c8c1041ec8dbcffbf27c5fc8fc";

            String[] keySalt = kek.split("\\.");
            String RSAKey = keySalt[0];
            String salt = keySalt[1];
            transactionlogger.error("RSAKey-->" + RSAKey);
            transactionlogger.error("salt-->" + salt);
            transactionlogger.error("response  approvalCode---->" + approvalCode);
            transactionlogger.error("cmd---->" + cmd);
            transactionlogger.error("datetime2---->" + datetime2);
            transactionlogger.error("kekExpiry---->" + kekExpiry);
            transactionlogger.error("macExpiry---->" + macExpiry);
            transactionlogger.error("mobile---->" + mobile);
            transactionlogger.error("resultCode---->" + resultCode);
            transactionlogger.error("resultMessage---->" + resultMessage);
            transactionlogger.error("session---->" + session);
            transactionlogger.error("kek---->"+kek);

 //*****************getpayLink******************

            sessionID=session;

            String trnxJson = "{"+
                    "\"amount\":"+amount+","+
                    "\"currency\":\""+currency+"\","+
                    "\"datetime\":\""+datetTime2+"\","+
                    "\"descr\":\""+descr+"\","+
                    "\"email\":\""+email+"\","+
                    "\"lang\":\""+lang+"\","+
                    "\"mobile\":\""+clientMobileNumber+"\","+
                    "\"name\":\""+clientName+"\","+
                    "\"otherInfo\":\""+otherInfo+"\","+
                    "\"reference\":\""+transRef+"\","+
                    "\"responseRedirectURL\":\""+redirectURL+"\","+
                    "\"session\":\""+sessionID+"\","+
                    "\"trxStatusCallbackURL\":\""+callbackURL+"\""+
                    "}";
            transactionlogger.error("getpaymentlink request--->" +trnxJson);

            String shortedRequest=PayFluidUtils.prepAmount(amount)+currency+datetTime2+descr+email+lang+clientMobileNumber+clientName+
                    otherInfo+transRef+redirectURL+session+callbackURL;

            transactionlogger.error("shortedRequest --->" + shortedRequest);
            String hashedStr = PayFluidUtils.hmacDigestSimple(shortedRequest, salt);
            transactionlogger.error("enRequest --->" + hashedStr);

            String signature = Base64.getEncoder().encodeToString(PayFluidUtils.encrypt(hashedStr, RSAKey));
            transactionlogger.error("signature --->" + signature);

            String trnxJson2 = "{"+
                    "\"amount\":"+amount+","+
                    "\"currency\":\""+currency+"\","+
                    "\"datetime\":\""+datetTime2+"\","+
                    "\"descr\":\""+descr+"\","+
                    "\"email\":\""+email+"\","+
                    "\"lang\":\""+lang+"\","+
                    "\"mobile\":\""+clientMobileNumber+"\","+
                    "\"name\":\""+clientName+"\","+
                    "\"otherInfo\":\""+otherInfo+"\","+
                    "\"reference\":\""+transRef+"\","+
                    "\"responseRedirectURL\":\""+redirectURL+"\","+
                    "\"session\":\""+sessionID+"\","+
                    "\"trxStatusCallbackURL\":\""+callbackURL+"\","+
                    "\"signature\":\""+signature+"\""+
                    "}";

           String  getPaylinkURL="";
            if (isTest){
                getPaylinkURL=RB.getString("getPayLink");
                transactionlogger.error("TEST_SALE_URL------------------------->"+"trackingid--->"+trackingID+":::"+payment_url);


            }
            else {
                getPaylinkURL=RB.getString("getPayLink");
                transactionlogger.error("LIVE_SALE_URL------------------------->"+"trackingid--->"+trackingID+":::"+payment_url);

            }

            transactionlogger.error("getPaylinkURL --->" + getPaylinkURL);
            transactionlogger.error(" executing getPaylink:::");
            String  getPaylinkResponse= payFluidUtils.doPostHttpUrlConnection(getPaylinkURL, trnxJson2);

            transactionlogger.error("getPaylinkResponse --->" + getPaylinkResponse);
            String[] finalresponsee = getPaylinkResponse.split("\\{");
            String http = finalresponsee[0];
            String getpayResponse = finalresponsee[1];
            transactionlogger.error(" getpayResponse:::" + "{" + getpayResponse);
            String jsonConverted = "{"+getpayResponse;

            transactionlogger.error(" jsonConverted:::" + "{" + jsonConverted);
            String resultJson =jsonConverted.replaceAll("\\\\","");
            transactionlogger.error(" resultJason:::"+"trackingid--->"+trackingID+":::"+resultJson);

            JSONObject getpaylinkJsonRes = new JSONObject(resultJson);
            String approvalCodeGetpaylink= getpaylinkJsonRes.getString("approvalCode");
            String result_message= getpaylinkJsonRes.getString("result_message");
            String webURL= getpaylinkJsonRes.getString("webURL");
            String sessionRes= getpaylinkJsonRes.getString("session");
            String result_code= getpaylinkJsonRes.getString("result_code");
            transactionlogger.error("approvalCodeGetpaylink --->" + approvalCodeGetpaylink);
            transactionlogger.error("result_message --->" + result_message);
            transactionlogger.error("webURL --->" + webURL);
            transactionlogger.error("sessionRes --->" + sessionRes);
            transactionlogger.error("result_code --->" + result_code);
            transactionlogger.error("finalurl --->" + webURL);



            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(webURL);
            transactionlogger.error("redirecturl---------------------------->"+webURL);
        }


        catch (Exception e)
        {
            transactionlogger.error("Exception------------------------->",e);
        }




        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in payfluid ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;
       PayFluidUtils payFluidUtils=new PayFluidUtils();
        CommRequestVO commRequestVO = payFluidUtils.getVervePayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
       PayFluidPaymentProcess payFluidPaymentProcess=new PayFluidPaymentProcess();
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
                html = payFluidPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect PayFluid form -- >>"+html);
            }
            transactionlogger.error("paymentid--------------------->"+transRespDetails.getTransactionId());

            payFluidUtils.updateTransaction(commonValidatorVO.getTrackingid(),transRespDetails.getTransactionId());
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in PayFluidPaymentGateway---", e);
        }
        return html;
    }



}
