package com.directi.pg.core.airpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.VervePayGatewayLogger;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 10/17/2021.
 */


/*
public static void main(String[] args)
        {
        String sUserName ="3566071";
        String sPassword = "t3SRj9Hg";
        String sSecret = "UserName";
        String sMerId = "243551";
        String sHiddenMode ="UserName";
        String apikey ="2s5QS9M8ejq7kHmC";

        String sEmail = "dev@gmail.com";
        String sPhone = "9999999999";
        String sFName = "dev";
        String sLName = "pandey";
        String sAddress = "malad";
        String sAmount = "100.00";
        String sCity = "mum";
        String sState = "mh";
        String sPincode ="400059";
        String sCountry ="India";

        String txnsubtype ="2";
        String sOrderId = "fdbhtgsgh";
        String chmod = "upi";
        String channel_mode = "upi";
        String action = "upi";
        String apiName = "upi";
        String customvar =sOrderId ;
        try
        {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String sCurDate = df.format(new Date());
        String sAllData = sEmail+sFName+sLName+sAddress+sCity+sState+sCountry+sAmount+sOrderId+sCurDate+chmod+customvar+txnsubtype;

        String sTemp = sSecret+"@"+sUserName+":|:"+sPassword;
        MessageDigest md1 = MessageDigest.getInstance("SHA-256");
        md1.update(sTemp.getBytes());
        byte byteData[] = md1.digest();
        StringBuffer sb1 = new StringBuffer();
        for (int i = 0; i < byteData.length; i++)
        {
        sb1.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        String sPrivateKey = sb1.toString();


        sAllData = sAllData + sPrivateKey;
        String sChecksum = "";

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(sAllData.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i)
        {
        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
        sChecksum = sb.toString();


        String form="<form action=\"https://payments.airpay.co.in/pay/index.php\" method=\"post\">\n" +
        "                    <input type=\"hidden\" name=\"currency\" value=\"356\">\n" +
        "                    <input type=\"hidden\" name=\"isocurrency\" value=\"INR\">\n" +
        "                    <input type=\"hidden\" name=\"orderid\" value=\""+sOrderId+"\">\n" +
        "                    <input type=\"hidden\" name=\"privatekey\" value=\""+apikey+"\">\n" +
        "                    <input type=\"hidden\" name=\"checksum\" value=\""+sChecksum+"\">\n" +
        "                    <input type=\"hidden\" name=\"mercid\" value=\""+sMerId+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerEmail\" value=\""+sEmail+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerPhone\" value=\""+sPhone+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerFirstName\" value=\""+sFName+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerLastName\" value=\""+sLName+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerAddress\" value=\""+sAddress+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerCity\" value=\""+sCity+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerState\" value=\""+sState+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerCountry\" value=\""+sCountry+"\">\n" +
        "                    <input type=\"hidden\" name=\"buyerPinCode\" value=\""+sPincode+"\">\n" +
        "                    <input type=\"hidden\" name=\"amount\" value=\""+sAmount+"\">\n" +
        "                    <input type=\"hidden\" name=\"chmod\" value=\""+chmod+"\">\n" +
        "                    <input type=\"hidden\" name=\"customvar\" value=\""+sOrderId+"\">\n" +
        "                    <input type=\"hidden\" name=\"txnsubtype\" value=\""+txnsubtype+"\">\n" +
        "                </form>";

        System.out.println("from-->"+form);
        } catch (Exception e){ System.out.println("Exception-->"+e);}
        }
*/



public class AirpayPaymentGateway extends AbstractPaymentGateway
{
    private static VervePayGatewayLogger transactionlogger = new VervePayGatewayLogger(AirpayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "airpay";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.airpay");
    private final static ResourceBundle RB_NB = LoadProperties.getProperty("com.directi.pg.APBANKS");
    String redirecturl= "";
    public AirpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("AirpayPaymentGateway  accountid------------------->" + accountId);
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of AirpayPaymentGateway......");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        RubixpayUtils rubixpayutils =new RubixpayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        AirpayUtils airpayUtils=new AirpayUtils();
        // GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String payment_Card= GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String sUserName ="";
        String sPassword =gatewayAccount.getFRAUD_FTP_PASSWORD();
        String sSecret = gatewayAccount.getFRAUD_FTP_USERNAME();
        String sMerId = gatewayAccount.getMerchantId();
        String apikey =gatewayAccount.getFRAUD_FTP_PATH();
        String sEmail = "";
        String sPhone ="" ;
        String sFName = "";
        String sLName = "";
        String sAddress = "";
        String sAmount = transactionDetailsVO.getAmount();
        String sCity =   "";
        String sState =  "";
        String sPincode ="";
        String sCountry ="India";

        String txnsubtype ="2";
        String sOrderId = trackingID;
        String chmod = "upi";
        String channel_mode = "upi";
        String action = "upi";
        String apiName = "upi";
        String customvar =sOrderId ;
        String nbcode=transactionDetailsVO.getCardType();
        String vpa=commRequestVO.getCustomerId();
        String paymode=transactionDetailsVO.getPaymentType();
        String paymentmode=airpayUtils.getPaymentType(paymode);
        String payment_url="";
        boolean isTest = gatewayAccount.isTest();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String sCurDate = df.format(new Date());
        String sAllData = sEmail+sFName+sLName+sAddress+sCity+sState+sCountry+sAmount+sOrderId+sCurDate+chmod+customvar+txnsubtype;

        String client_ip ="";
        if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
        {
            client_ip =commAddressDetailsVO.getCardHolderIpAddress();
        }
        else{
            client_ip="193.168.1.1";
        }
        String domainname ="";
        if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
        {
            sFName = commAddressDetailsVO.getFirstname();
            sLName=  commAddressDetailsVO.getLastname();
        }
        else{
            sFName="Customer";
            sLName="Customer";
        }
        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
        {
            sEmail=commAddressDetailsVO.getEmail();
        }
        else{
            sEmail="customer@gmail.com";
        }
        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
        {
            if(commAddressDetailsVO.getPhone().contains("-"))
            {
                sPhone = commAddressDetailsVO.getPhone().split("\\-")[1];
            }
            if(sPhone.length()>10){

                sPhone=sPhone.substring(sPhone.length() - 10);
            }
            else{
                sPhone = commAddressDetailsVO.getPhone();
            }
        }
        else{
            sPhone="9999999999";
        }
        transactionlogger.error("phone is-------------->"+sPhone);
        String countrycode ="IND";
        String currencycode =transactionDetailsVO.getCurrency();


        if(functions.isValueNull(commAddressDetailsVO.getStreet()))
        {
            sAddress=commAddressDetailsVO.getStreet();
        }
        else{
            sAddress="Rajiv Chowk";
        }

        if(functions.isValueNull(commAddressDetailsVO.getCity()))
        {
            sCity=commAddressDetailsVO.getCity();
        }
        else{
            sCity="Delhi";
        }
        if(functions.isValueNull(commAddressDetailsVO.getState()))
        {
            sState=commAddressDetailsVO.getState();
        }
        else{
            sState="New Delhi";
        }

        if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
        {
            sPincode=commAddressDetailsVO.getZipCode();
        }
        else{
            sPincode="110017";
        }

        String response_url =RB.getString("RUBIXPAY_RU")+trackingID;
        transactionlogger.error("response_url is-------------->"+response_url);
        String cancel_url =RB.getString("RUBIXPAY_RU")+trackingID+"&status=cancel";;
        transactionlogger.error("cancel_url is-------------->"+cancel_url);

        String securePaymentGateway="";

        if (isTest){
            payment_url=RB.getString("TEST_SALE_URL");
            // transactionlogger.error("TEST_SALE_URL------------------------->"+payment_url);


        }
        else {
            payment_url=RB.getString("LIVE_SALE_URL");
            // transactionlogger.error("LIVE_SALE_URL------------------------->"+payment_url);
        }

        Map<String, String> requestS2SMap = new HashMap<String, String>();

        requestS2SMap.put("currency", "356");
        requestS2SMap.put("isocurrency", "INR");
        requestS2SMap.put("orderid", sOrderId);
        requestS2SMap.put("privatekey", apikey);
       // requestS2SMap.put("checksum", sChecksum);
        requestS2SMap.put("mercid", sMerId);
        requestS2SMap.put("buyerEmail", sEmail);
        requestS2SMap.put("buyerPhone", sPhone);
        requestS2SMap.put("buyerFirstName", sFName);
        requestS2SMap.put("buyerLastName", sLName);
        requestS2SMap.put("buyerAddress", sAddress);
        requestS2SMap.put("buyerCity", sCity);
        requestS2SMap.put("buyerState", sState);
        requestS2SMap.put("buyerCountry", sCountry);
        requestS2SMap.put("buyerPinCode", sPincode);
        requestS2SMap.put("amount", sAmount);
        requestS2SMap.put("chmod", chmod);
        requestS2SMap.put("customvar", sOrderId);
        requestS2SMap.put("txnsubtype", txnsubtype);

       /* if("CC".equalsIgnoreCase(PAYMENT_TYPE) || "DC".equalsIgnoreCase(PAYMENT_TYPE))
        {
            transactionlogger.error("inside CC & DC condition (map)-------------->"+PAYMENT_TYPE);
            requestS2SMap.put("CUST_NAME", CUST_NAME);
            requestS2SMap.put("CARD_NUMBER", CARD_NUMBER);
            requestS2SMap.put("CARD_EXP_DT", CARD_EXP_DT);
            requestS2SMap.put("CVV", CVV);

            MOP_TYPE= qikPayUtils.getPaymentBrand(payment_brand);

        }
        else if("NB".equalsIgnoreCase(PAYMENT_TYPE))
        {
            MOP_TYPE=payment_brand;

        }
        else if("UP".equalsIgnoreCase(PAYMENT_TYPE))
        {
            MOP_TYPE=PAYMENT_TYPE;
            requestS2SMap.put("UPI", UPI);
        }
        //  failed if incorrect paymentype
        else{
            commResponseVO.setStatus("fail");
            commResponseVO.setDescription("Incorrect request");
            commResponseVO.setRemark("Incorrect request");
            return commResponseVO ;
        }*/
        //  dotransaction
       /* request="mid="+mid+"&apikey="+apikey+"&fullname="+fullname+"&email="+email+"&phone="+phone+"&countrycode="+countrycode+"&orderid="+orderid+"&bill_address="+bill_address+"&bill_city="+bill_city+"&bill_state="+bill_state
                +"&bill_country="+bill_country+"&bill_postalcode="+bill_postalcode
                +"&currencycode="+currencycode+"&amount="+amount+"&response_url="+response_url+"&cancel_url="+cancel_url+"&domainname="+domainname+"&paymenttype="+paymenttype;
*/
        //transactionlogger.error("request--------------for----trackingid::::"+trackingID +"--"+request);

        String response="";// verveUtils.doHttpPostConnection(payment_url,request);
        transactionlogger.error("response--------------for--trackingid::::"+trackingID+"--"+response);
        String authcode= "";

        try
        {

            JSONObject jsonobj = new JSONObject(response);

            String responseMessgage = jsonobj.getString("message");
            transactionlogger.error("before pending3DConfirmation response status" + jsonobj.getString("status"));
            if (jsonobj.has("redirecturl") && !"error".equalsIgnoreCase(jsonobj.getString("status")))
            {

                transactionlogger.error("inside pending3DConfirmation response status" + jsonobj.getString("status"));
                commResponseVO.setStatus("pending3DConfirmation");
                authcode = jsonobj.getString("authcode");
                redirecturl = jsonobj.getString("redirecturl");
                commResponseVO.setTransactionId(authcode);
                transactionlogger.error("authcode---------------------------->" + authcode);
                commResponseVO.setUrlFor3DRedirect(redirecturl);
                transactionlogger.error("redirecturl---------------------------->" + redirecturl);
            }
            //  set failed if redirecturl not found in response
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription(responseMessgage);
                commResponseVO.setRemark(responseMessgage);
                return commResponseVO;
            }


            if ("UPI".equalsIgnoreCase(paymentmode))
            {
                transactionlogger.error(" insise UPI VPA update--->" + vpa);
             //   verveUtils.updateTransaction(trackingID, authcode, vpa);
            }

            else if ("netbanking".equalsIgnoreCase(paymentmode) && functions.isValueNull(nbcode))
            {
                transactionlogger.error(" insise netbanking bankName update--->" + paymentmode);
                HashMap<String, String> hashMap = new HashMap<>();
                Enumeration<String> banks = RB_NB.getKeys();
                String str11 = "";

                while (banks.hasMoreElements())
                {
                    String key = banks.nextElement();
                    str11 = RB_NB.getString(key);
                    hashMap.put(str11, key);
                }
                String bankName = hashMap.get(nbcode);
                String customerId = bankName;
              //  verveUtils.updateTransaction(trackingID, authcode, customerId);
            }
        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException------------------------->",e);
        }catch (Exception e)
        {
            transactionlogger.error("Exception-------------->",e);
        }

        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredirect in Vervepay ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;
        VerveUtils verveUtils =new VerveUtils();
        CommRequestVO commRequestVO = verveUtils.getVervePayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        VervePaymentProcess vervePaymentProcess =new VervePaymentProcess();
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
                html = vervePaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect vervepay form -- >>"+html);
            }
            transactionlogger.error("paymentid--------------------->"+transRespDetails.getTransactionId());


        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in VervePaymentGateway---", e);
        }
        return html;
    }
}


