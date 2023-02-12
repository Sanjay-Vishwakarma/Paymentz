package com.payment.verve;


import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.VervePayGatewayLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.paygate.ag.common.utils.PayGateCryptoUtils;
import com.payment.Rubixpay.RubixpayPaymentProcess;
import com.payment.Rubixpay.RubixpayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.safexpay.AsyncSafexPayPayoutQueryService;
import com.payment.safexpay.SafexPayPaymentProcess;
import com.payment.safexpay.SafexPayUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 10/6/2020.
 */
public class VervePaymentGateway extends AbstractPaymentGateway
{
    private static VervePayGatewayLogger transactionlogger = new VervePayGatewayLogger(VervePaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "vervepay";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.vervepay");
    private final static ResourceBundle RB_NB = LoadProperties.getProperty("com.directi.pg.VPBANKS");
    String redirecturl= "";
    public VervePaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("VervePaymentGateway  accountid------------------->" + accountId);
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of VervePaymentGateway......");
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
        String payment_Card= GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String bill_address="";
        String bill_city="";
        String bill_state="";
        String bill_country="";
        String bill_postalcode="";
        String payment_url="";
        String phone="";
        String request="";
        String finalNbcode="";

        String nbcode=transactionDetailsVO.getCardType();
        if(functions.isValueNull(nbcode)){

            finalNbcode=nbcode.replace('_','-');
        }
        transactionlogger.error("vervepay finalNbcode is-------------->"+finalNbcode);
        String vpa=commRequestVO.getCustomerId();
        transactionlogger.error("vervepay gateway vpa is-------------->"+vpa);
        String paymode=transactionDetailsVO.getPaymentType();
        transactionlogger.error("from system paymode is-------------->"+paymode);
        String paymentmode=verveUtils.getPaymentType(paymode);
        transactionlogger.error("request paymode is-------------->"+paymentmode);
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        boolean isTest = gatewayAccount.isTest();

        String mid=gatewayAccount.getMerchantId();
        transactionlogger.error("mid is-------------->"+mid);
        String apikey =gatewayAccount.getFRAUD_FTP_PASSWORD();
     //   transactionlogger.error("apikey is-------------->"+apikey);
        String serverkey =gatewayAccount.getFromMid();
     //   transactionlogger.error("serverkey is-------------->"+serverkey);


        String client_ip ="";
        if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
        {
             client_ip =commAddressDetailsVO.getCardHolderIpAddress();

        }
        else{
            client_ip="193.168.1.1";
        }
        transactionlogger.error("client_ip is-------------->"+client_ip);
        String domainname =gatewayAccount.getFRAUD_FTP_PATH();
        transactionlogger.error("domainname is-------------->"+domainname);
        String fullname ="";
        if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
        {
            fullname = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();

        }
        else{
            fullname="Customer";
        }
        transactionlogger.error("fullname is-------------->"+fullname);
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
            if(commAddressDetailsVO.getPhone().contains("-"))
            {
                phone = commAddressDetailsVO.getPhone().split("\\-")[1];
            }
            if(phone.length()>10){

                phone=phone.substring(phone.length() - 10);
            }
            else{
                phone = commAddressDetailsVO.getPhone();
            }
        }
        else{
            phone="9999999999";
        }
       // phone="9999999999";
        transactionlogger.error("phone is-------------->"+phone);
        String orderid =trackingID;
        transactionlogger.error("orderid is-------------->"+orderid);
        String countrycode ="IND";
        transactionlogger.error("countrycode is-------------->"+countrycode);
        bill_country="IND";
        transactionlogger.error("bill_country is-------------->"+bill_country);
        String currencycode =transactionDetailsVO.getCurrency();
        transactionlogger.error("currencycode is-------------->"+currencycode);
        String amount=transactionDetailsVO.getAmount();
        transactionlogger.error("amount is-------------->"+amount);
        String paymenttype="sale";
        transactionlogger.error("paymenttype is-------------->"+paymenttype);
        if(functions.isValueNull(commAddressDetailsVO.getStreet()))
        {
            bill_address=commAddressDetailsVO.getStreet();
        }
        else{
            bill_address="Rajiv Chowk";
        }
        transactionlogger.error("bill_address is-------------->"+bill_address);
        if(functions.isValueNull(commAddressDetailsVO.getCity()))
        {
            bill_city=commAddressDetailsVO.getCity();
        }
        else{
            bill_city="Delhi";
        }
        transactionlogger.error("bill_city is-------------->"+bill_city);
        if(functions.isValueNull(commAddressDetailsVO.getState()))
        {
            bill_state=commAddressDetailsVO.getState();
        }
        else{
            bill_state="New Delhi";
        }
        transactionlogger.error("bill_state is-------------->"+bill_state);


        if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
        {
            bill_postalcode=commAddressDetailsVO.getZipCode();
        }
        else{
            bill_postalcode="110017";
        }
        transactionlogger.error("bill_postalcode is-------------->"+bill_postalcode);

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

        if("netbanking".equalsIgnoreCase(paymentmode))
        {
            payment_url=RB.getString("dotransactionnetbanking");

            //netbanking
            request = "mid=" + mid + "&serverkey=" + serverkey + "&client_ip=" + client_ip + "&domainname=" + domainname + "&fullname=" + fullname + "&email=" + email + "&phone=" + phone + "&orderid=" + orderid + "&countrycode=" + countrycode
                    + "&currencycode=" + currencycode + "&amount=" + amount + "&paymentmode=" + paymentmode + "&paymenttype=" + paymenttype + "&bill_address=" + bill_address + "&bill_city=" + bill_city + "&bill_state=" + bill_state
                    + "&bill_country=" + bill_country + "&bill_postalcode=" + bill_postalcode + "&nbcode=" + finalNbcode+"&response_url="+response_url;
        }

        else  if("upi".equalsIgnoreCase(paymentmode)){
             payment_url=RB.getString("dotransactionupi");

            //UPI
            request="mid="+mid+"&serverkey="+serverkey+"&client_ip="+client_ip+"&domainname="+domainname+"&fullname="+fullname+"&email="+email+"&phone="+phone+"&orderid="+orderid+"&countrycode="+countrycode
                    +"&currencycode="+currencycode+"&amount="+amount+"&paymentmode="+paymentmode+"&paymenttype="+paymenttype+"&bill_address="+bill_address+"&bill_city="+bill_city+"&bill_state="+bill_state
                    +"&bill_country="+bill_country+"&bill_postalcode="+bill_postalcode+"&vpa="+vpa+"&response_url="+response_url;

        }
        //  failed if incorrect paymentype
        else{
            commResponseVO.setStatus("fail");
            commResponseVO.setDescription("Incorrect request");
            commResponseVO.setRemark("Incorrect request");
            return commResponseVO ;
        }
          //  dotransaction
       /* request="mid="+mid+"&apikey="+apikey+"&fullname="+fullname+"&email="+email+"&phone="+phone+"&countrycode="+countrycode+"&orderid="+orderid+"&bill_address="+bill_address+"&bill_city="+bill_city+"&bill_state="+bill_state
                +"&bill_country="+bill_country+"&bill_postalcode="+bill_postalcode
                +"&currencycode="+currencycode+"&amount="+amount+"&response_url="+response_url+"&cancel_url="+cancel_url+"&domainname="+domainname+"&paymenttype="+paymenttype;
*/
            transactionlogger.error("request--------------for----trackingid::::"+trackingID +"--"+request);

        String response= verveUtils.doHttpPostConnection(payment_url,request);
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
                verveUtils.updateTransaction(trackingID, authcode, vpa);
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
                verveUtils.updateTransaction(trackingID, authcode, customerId);
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

    public GenericResponseVO process3DSaleConfirmation(String trackingID, GenericRequestVO requestVO)
    {
        transactionlogger.error("Entering process3DSaleConfirmation of VervePaymentGateway......");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        VerveUtils verveUtils =new VerveUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        transactionlogger.error("CommTransactionDetailsVO------------------------>"+transactionDetailsVO);
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        transactionlogger.error("inside process3DAuthConfirmation ----- ");
        transactionlogger.error("accountId is-------------->"+accountId);
        String payment_url="";
        boolean isTest = gatewayAccount.isTest();
        if (isTest){
            payment_url=RB.getString("GET_TRANSACTION_TEST");
            transactionlogger.error("GET_TRANSACTION_TEST------------------------->"+payment_url);


        }
        else {
            payment_url=RB.getString("GET_TRANSACTION_LIVE");
            transactionlogger.error("GET_TRANSACTION_LIVE------------------------->"+payment_url);
        }


        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions = new Functions();
        String status="";
        String cardtype="";
        String approvedAmount="";
        String resRemark="";
        String mid=gatewayAccount.getMerchantId();
        transactionlogger.error("mid is-------------->"+mid);
        String serverkey=gatewayAccount.getFromMid();
        //transactionlogger.error("serverkey is-------------->"+serverkey);
        String transactionid=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        transactionlogger.error("transactionid(authcode) is-------------->"+transactionid);


        try
        {


            String request="mid="+mid+"&serverkey="+serverkey+"&transactionid="+transactionid;
            transactionlogger.error("process3DSaleConfirmation request ------------------------> " + request);
            String response= verveUtils.doHttpPostConnection(payment_url,request );
            transactionlogger.error("process3DSaleConfirmation response -------------------------> " + response);

            if (response != null)
            {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("txn_status"))
                {
                    status=jsonObject.getString("txn_status");
                }

                if (jsonObject.has("cardtype"))
                {
                    cardtype=jsonObject.getString("cardtype");
                }
                 if (jsonObject.has("txn_message"))
                {
                    resRemark=jsonObject.getString("txn_message");
                }

                if (status.equalsIgnoreCase("TXN_COMPLETE"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                    commResponseVO.setBankCode(cardtype);
                }
                else if(status.equalsIgnoreCase("TXN_PENDING")||status.equalsIgnoreCase("TXN_UPI_REDIRECT")||status.equalsIgnoreCase("TXN_NB_REDIRECT")||status.contains("pending")||status.contains("PENDING")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }
                else if(status.equalsIgnoreCase("error")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }
                else if(status.equalsIgnoreCase("TXN_FAILED")||status.contains("FAIL")||status.contains("fail"))
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setBankCode(cardtype);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }

                commResponseVO.setTransactionId(jsonObject.getString("transactionid"));
                commResponseVO.setResponseHashInfo(jsonObject.getString("authcode"));
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception-----", e);
        }
        return commResponseVO;
    }
    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionlogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        VerveUtils verveUtils =new VerveUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        transactionlogger.error("CommTransactionDetailsVO------------------------>"+transactionDetailsVO);
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        transactionlogger.error("inside processInquiry ----- ");
        transactionlogger.error("accountId is-------------->"+accountId);

        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions = new Functions();
        String status="";
        String payment_url="";
        boolean isTest = gatewayAccount.isTest();
        if (isTest){
            payment_url=RB.getString("GET_TRANSACTION_TEST");
            transactionlogger.error("GET_TRANSACTION_TEST------------------------->"+payment_url);


        }
        else {
            payment_url=RB.getString("GET_TRANSACTION_LIVE");
            transactionlogger.error("GET_TRANSACTION_LIVE;------------------------->"+payment_url);
        }


        String mid=gatewayAccount.getMerchantId();
        transactionlogger.error("mid is-------------->"+mid);
        String serverkey=gatewayAccount.getFromMid();
        transactionlogger.error("serverkey is-------------->"+serverkey);
        String transactionid=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        transactionlogger.error("transactionid(authcode) is-------------->"+transactionid);


        try
        {


            String request="mid="+mid+"&serverkey="+serverkey+"&transactionid="+transactionid;;
            transactionlogger.error("process3DSaleConfirmation request ------------------------> " + request);
            String response= verveUtils.doHttpPostConnection(payment_url,request );
            transactionlogger.error("process3DSaleConfirmation response -------------------------> " + response);
            String resRemark="";
            String resNotFoundRemark="";
            String cardtype="";
            if (response != null)
            {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("txn_status"))
                {
                    status=jsonObject.getString("txn_status");
                }
                if (jsonObject.has("cardtype"))
                {
                    cardtype=jsonObject.getString("cardtype");
                }
                if (jsonObject.has("txn_message"))
                {
                    resRemark=jsonObject.getString("txn_message");
                }

                if (jsonObject.has("message"))
                {
                    resNotFoundRemark=jsonObject.getString("message");
                }

                if (status.equalsIgnoreCase("TXN_COMPLETE"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                    commResponseVO.setBankCode(cardtype);
                }
                else if(status.equalsIgnoreCase("TXN_PENDING")||status.equalsIgnoreCase("TXN_UPI_REDIRECT")||status.equalsIgnoreCase("TXN_NB_REDIRECT")||status.contains("pending")||status.contains("PENDING")||status.equalsIgnoreCase("error")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }

                else if(status.equalsIgnoreCase("TXN_FAILED")||status.contains("FAIL")||status.contains("fail"))
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setBankCode(cardtype);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }
                else if(resNotFoundRemark.equalsIgnoreCase("Invalid transaction id")&& status.equalsIgnoreCase("error"))
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setRemark(resNotFoundRemark);
                    commResponseVO.setDescription(resNotFoundRemark);
                  //  commResponseVO.setBankCode(cardtype);
                   // commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(jsonObject.getString("txnamount"));
                }

                commResponseVO.setTransactionId(jsonObject.getString("transactionid"));
                commResponseVO.setResponseHashInfo(jsonObject.getString("authcode"));
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception-----", e);
        }



        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside vervepay process payout-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        SafexPayPaymentProcess safexPayPaymentProcess=new SafexPayPaymentProcess();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        PayGateCryptoUtils payGateCryptoUtils=new PayGateCryptoUtils();
        VerveUtils verveUtils=new VerveUtils();
        //transactionLogger.error("key------------->"+merchant_key);
        boolean isTest = gatewayAccount.isTest();
        String sessionId="";
        String versionCheckKey="";
        String url="";
        String mid=gatewayAccount.getMerchantId();//"10011064";
        String apikey =gatewayAccount.getFRAUD_FTP_USERNAME();//"F019F374-9688-4A21-B49D-0D50B56CEEC2";//payout API Key
        String client_ip ="103.79.170.134";
        String domainname ="3dsy";
        // String domainname ="cashfree";
        String fullname ="";
        String email="customer@pay.com";
        String phone ="99999999999";
        String orderid =trackingId;
        String countrycode ="IND";
        String amount=commTransactionDetailsVO.getAmount();
        String accountnumber ="";
        String ifsccode ="";
        String accounttype ="savings";
        String beneficiaryvpa ="test@upi";
        String remark =trackingId;
        String request ="";
        String bankName ="";



        transactionlogger.error("vervepay accounttype is -- >"+accounttype);
        transactionlogger.error("vervepay remark is -- >"+remark);
        transactionlogger.error("vervepay beneficiaryvpa is -- >"+beneficiaryvpa);
        transactionlogger.error("vervepay amount is -- >"+amount);
        transactionlogger.error("vervepay phone is -- >"+phone);
        transactionlogger.error("vervepay countrycode is -- >"+countrycode);
        transactionlogger.error("vervepay email is -- >"+email);
        transactionlogger.error("vervepay fullname is -- >"+fullname);
        transactionlogger.error("vervepay accountno is -- >"+commTransactionDetailsVO.getBankAccountNo());

        if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
        {
            bankName=commTransactionDetailsVO.getCustomerBankAccountName();
            if(bankName.contains("_")){

                String[] splitBankname=  bankName.split("\\_");
                fullname=splitBankname[0];
            }
            else
            {
                fullname=commTransactionDetailsVO.getCustomerBankAccountName();

            }


            transactionlogger.error("vervepay fullname is -- >"+fullname);
        }
        if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
        {
            accountnumber=commTransactionDetailsVO.getBankAccountNo();
            transactionlogger.error("vervepay bene_account_no is -- >"+accountnumber);
        }

        if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
        {
            ifsccode=commTransactionDetailsVO.getBankIfsc();
            transactionlogger.error("vervepay payout ifsc -->" + ifsccode);
        }
        String transfer_type ="";
        if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
        {
            //accounttype=commTransactionDetailsVO.getBankTransferType(); // additional fields added to check
        }
        accounttype ="savings";
        transactionlogger.error("vervepay accounttype is -- >"+accounttype);


        if (isTest)
        {
            url=RB.getString("PAYOUT_TEST_URL");

        }
        else
        {
            url=RB.getString("PAYOUT_LIVE_URL");


        }

        request="mid="+mid+"&payoutkey="+apikey+"&email="+email+"&phone="+phone+"&orderid="+orderid+"&amount="+amount+"&accountholder="+fullname+
                "&countrycode="+countrycode+"&accountnumber="+accountnumber+"&ifsccode="+ifsccode+"&accounttype="+accounttype+"&beneficiaryvpa="+beneficiaryvpa+"&remark="+remark;

        transactionlogger.error(" vervepay payout request-->" +trackingId+"--->"+ request);
        String response= verveUtils.doHttpPostConnection(url,request);
        transactionlogger.error("response--------------for--"+trackingId+"--"+response);

            String status="";
            String description="";
            String code="";
            String message="";
            String authcode="";
            String transactionid="";
            String bankreferenceid="";
            String responseOrderid="";

        try{

            if(functions.isValueNull(response))
            {
                JSONObject payoutResponse=new JSONObject(response);

                if (payoutResponse.has("status"))
                {
                    status = payoutResponse.getString("status");
                    transactionlogger.error("status-->" + status);
                }
                if (payoutResponse.has("code"))
                {
                    code = payoutResponse.getString("code");
                    transactionlogger.error("code-->" + code);
                }
                if (payoutResponse.has("message"))
                {
                    message = payoutResponse.getString("message");
                    transactionlogger.error("message-->" + message);
                }
                if (payoutResponse.has("authcode"))
                {
                    authcode = payoutResponse.getString("authcode");
                    transactionlogger.error("authcode-->" + authcode);
                }
                if (payoutResponse.has("transactionid"))
                {
                    transactionid = payoutResponse.getString("transactionid");
                    transactionlogger.error("transactionid-->" + transactionid);
                }
                if (payoutResponse.has("bankreferenceid"))
                {
                    bankreferenceid = payoutResponse.getString("bankreferenceid");
                    transactionlogger.error("bankreferenceid-->" + bankreferenceid);
                }
                if (payoutResponse.has("responseOrderid"))
                {
                    responseOrderid = payoutResponse.getString("responseOrderid");
                    transactionlogger.error("responseOrderid-->" + responseOrderid);
                }

                transactionlogger.error("status-->" + status);
                transactionlogger.error("code-->" + code);
                transactionlogger.error("message-->" + message);
                transactionlogger.error("authcode-->" + authcode);
                transactionlogger.error("transactionid-->" + transactionid);
                transactionlogger.error("bankreferenceid-->" + bankreferenceid);
                transactionlogger.error("responseOrderid-->" + responseOrderid);


                if (status.equalsIgnoreCase("SUCCESS"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setIfsc(ifsccode);
                    commResponseVO.setFullname(fullname);
                    commResponseVO.setBankaccount(accountnumber);
                    commResponseVO.setBankRefNo(bankreferenceid);


                }

                else if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("PROCESSING") || status.equalsIgnoreCase("TXN_PENDING") || status.contains("pending") || status.contains("PENDING"))
                {

                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setIfsc(ifsccode);
                    commResponseVO.setFullname(fullname);
                    commResponseVO.setBankaccount(accountnumber);
                    commResponseVO.setBankRefNo(bankreferenceid);
                    VerveUtils.updatePayoutTransaction(trackingId, transactionid);
                }
                // error status check
                else if (status.equalsIgnoreCase("TXN_FAILED"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setIfsc(ifsccode);
                    commResponseVO.setFullname(fullname);
                    commResponseVO.setBankaccount(accountnumber);
                    commResponseVO.setBankRefNo(bankreferenceid);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setIfsc(ifsccode);
                    commResponseVO.setFullname(fullname);
                    commResponseVO.setBankaccount(accountnumber);
                    commResponseVO.setBankRefNo(bankreferenceid);
                    VerveUtils.updatePayoutTransaction(trackingId, transactionid);
                }

                commResponseVO.setTransactionId(transactionid);
                commResponseVO.setResponseHashInfo(authcode);
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark(message);
                commResponseVO.setDescription(message);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(ifsccode);
                commResponseVO.setFullname(fullname);
                commResponseVO.setBankaccount(accountnumber);
                commResponseVO.setBankRefNo(bankreferenceid);
                VerveUtils.updatePayoutTransaction(trackingId, transactionid);
            }
            String CALL_EXECUTE_AFTER=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC=RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed=RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncVervePayPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }

        }

        catch (JSONException e)
        {
            transactionlogger.error("JSONException----trackingid---->"+trackingId+"--->",e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside vervepay process payout inquiry------->");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        VerveUtils verveUtils=new VerveUtils();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchant_key=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();


        String url="";
        String mid=gatewayAccount.getMerchantId();//"10011064";
        String apikey =gatewayAccount.getFRAUD_FTP_USERNAME();//"F019F374-9688-4A21-B49D-0D50B56CEEC2";//payout API Key
        String response="";
        String msg ="";
        String inquiryRequest ="";
        String transactionid=commTransactionDetailsVO.getPreviousTransactionId();

        if (isTest)
        {
            url=RB.getString("PAYOUT_INQUIRY_TEST_URL");
        }
        else
        {
            url=RB.getString("PAYOUT_INQUIRY_LIVE_URL");
        }



        inquiryRequest="mid="+mid+"&payoutkey="+apikey+"&transactionid="+transactionid;
        transactionlogger.error("vervepay payout inquiry Request---->" +trackingId+"---->"+inquiryRequest );
        response=verveUtils.doHttpPostConnection(url,inquiryRequest);
        transactionlogger.error("vervepay payout inquiry  Response---->" +trackingId+"---->"+response );
        try
        {

            String status="";
            String description="";
            String code="";
            String message="";
            String authcode="";
            String responseTransactionid="";
            String bankreferenceid="";
            String responseOrderid="";
            String id_merchant="";
            String txn_status="";
            String txn_message="";

    if(functions.isValueNull(response))
    {
      JSONObject payoutResponse=new JSONObject(response);


    if (payoutResponse.has("status"))
    {
        status = payoutResponse.getString("status");
        transactionlogger.error("status-->" + status);
    }
    if (payoutResponse.has("code"))
    {
        code = payoutResponse.getString("code");
        transactionlogger.error("code-->" + code);
    }
    if (payoutResponse.has("id_merchant"))
    {
        id_merchant = payoutResponse.getString("id_merchant");
        transactionlogger.error("id_merchant-->" + id_merchant);
    }
    if (payoutResponse.has("message"))
    {
        message = payoutResponse.getString("message");
        transactionlogger.error("message-->" + message);
    }
    if (payoutResponse.has("authcode"))
    {
        authcode = payoutResponse.getString("authcode");
        transactionlogger.error("authcode-->" + authcode);
    }
    if (payoutResponse.has("transactionid"))
    {
        responseTransactionid = payoutResponse.getString("transactionid");
        transactionlogger.error("responseTransactionid-->" + responseTransactionid);
    }
    if (payoutResponse.has("bankreferenceid"))
    {
        bankreferenceid = payoutResponse.getString("bankreferenceid");
        transactionlogger.error("bankreferenceid-->" + bankreferenceid);
    }
    if (payoutResponse.has("responseOrderid"))
    {
        responseOrderid = payoutResponse.getString("responseOrderid");
        transactionlogger.error("responseOrderid-->" + responseOrderid);
    }
    if (payoutResponse.has("txn_status"))
    {
        txn_status = payoutResponse.getString("txn_status");
        transactionlogger.error("txn_status-->" + txn_status);
    }
    if (payoutResponse.has("txn_message"))
    {
        txn_message = payoutResponse.getString("txn_message");
        transactionlogger.error("txn_message-->" + txn_message);
    }

    transactionlogger.error("status-->" + status);
    transactionlogger.error("code-->" + code);
    transactionlogger.error("message-->" + message);
    transactionlogger.error("authcode-->" + authcode);
    transactionlogger.error("transactionid-->" + transactionid);
    transactionlogger.error("bankreferenceid-->" + bankreferenceid);
    transactionlogger.error("responseOrderid-->" + responseOrderid);
    transactionlogger.error("txn_status-->" + txn_status);


    if (txn_status.equalsIgnoreCase("TXN_COMPLETE"))
    {
        commResponseVO.setStatus("success");
        commResponseVO.setTransactionStatus("success");
        commResponseVO.setRemark(message);
        commResponseVO.setDescription(message);
        commResponseVO.setErrorCode(code);
        commResponseVO.setBankRefNo(bankreferenceid);

    }
    else if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("PROCESSING") || status.equalsIgnoreCase("TXN_PENDING") || status.contains("pending") || status.contains("PENDING"))
    {

        commResponseVO.setStatus("pending");
        commResponseVO.setTransactionStatus("pending");
        commResponseVO.setRemark(message);
        commResponseVO.setDescription(message);
        commResponseVO.setErrorCode(code);
        commResponseVO.setBankRefNo(bankreferenceid);

    }

    else if (txn_status.equalsIgnoreCase("TXN_FAILED"))
    {

        commResponseVO.setStatus("failed");
        commResponseVO.setTransactionStatus("failed");
        commResponseVO.setRemark(txn_message);
        commResponseVO.setDescription(txn_message);
        commResponseVO.setErrorCode(code);
        commResponseVO.setBankRefNo(bankreferenceid);

    }

    else
    {
        commResponseVO.setStatus("pending");
        commResponseVO.setTransactionStatus("pending");
        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
        commResponseVO.setErrorCode(code);
        commResponseVO.setBankRefNo(bankreferenceid);

    }
        if(functions.isValueNull(responseTransactionid))
        {
            commResponseVO.setTransactionId(responseTransactionid);
        }
        commResponseVO.setResponseHashInfo(authcode);
    }
    else{
        commResponseVO.setStatus("pending");
        commResponseVO.setTransactionStatus("pending");
        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
        commResponseVO.setErrorCode(code);
        commResponseVO.setBankRefNo(bankreferenceid);
        }

        }
        catch (JSONException e1)
        {
            transactionlogger.error(" JSONException-->" ,e1 );
        }catch (Exception e1)
        {
            transactionlogger.error(" Exception-->" ,e1 );
        }

        return commResponseVO;

    }

}
