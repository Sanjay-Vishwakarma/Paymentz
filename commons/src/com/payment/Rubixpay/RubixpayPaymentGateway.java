package com.payment.Rubixpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.paygate.ag.common.utils.PayGateCryptoUtils;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.safexpay.SafexPayUtils;
import com.payment.tapmio.TapMioPaymentProcess;
import com.payment.tapmio.TapMioUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by Admin on 5/26/2020.
 */
public class RubixpayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger = new TransactionLogger(RubixpayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "rubixpay";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.rubixpay");
    String redirecturl= "";
    public RubixpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("RubixpayPaymentGateway  accountid------------------->" + accountId);
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of RubixpayPaymentGateway......");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        RubixpayUtils rubixpayutils =new RubixpayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
       // GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String payment_Card= GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String bill_address="";
        String bill_city="";
        String bill_state="";
        String bill_country="";
        String bill_postalcode="";
        String payment_url="";
        String phone="";
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        boolean isTest = gatewayAccount.isTest();

        String mid=gatewayAccount.getMerchantId();
        transactionlogger.error("mid is-------------->"+mid);
        String apikey =gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionlogger.error("apikey is-------------->"+apikey);
        /*String serverkey ="3A6CCEEA-3036-4C24-A63C-DADFA5355276";
        transactionlogger.error("returnUrl is-------------->");*/
        String client_ip =commAddressDetailsVO.getCardHolderIpAddress();
        transactionlogger.error("client_ip is-------------->"+client_ip);
        String domainname =gatewayAccount.getFRAUD_FTP_PATH();
        transactionlogger.error("domainname is-------------->"+domainname);
        String fullname =commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        transactionlogger.error("fullname is-------------->"+fullname);
        String email=commAddressDetailsVO.getEmail();
        transactionlogger.error("email is-------------->"+email);
        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
        {
             phone=commAddressDetailsVO.getPhone();
        }
        else{
            phone="9999999999";
        }
        transactionlogger.error("phone is-------------->"+phone);
        String orderid =trackingID;
        transactionlogger.error("orderid is-------------->"+orderid);
        String countrycode ="IND";
        transactionlogger.error("countrycode is-------------->"+countrycode);
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
             bill_address="moscow";
           }
        transactionlogger.error("bill_address is-------------->"+bill_address);
        if(functions.isValueNull(commAddressDetailsVO.getCity()))
        {
            bill_city=commAddressDetailsVO.getCity();
        }
        else{
            bill_city="newyork";
        }
        transactionlogger.error("bill_city is-------------->"+bill_city);
        if(functions.isValueNull(commAddressDetailsVO.getState()))
        {
            bill_state=commAddressDetailsVO.getState();
        }
        else{
             bill_state="MH";
        }
        transactionlogger.error("bill_state is-------------->"+bill_state);
        if(functions.isValueNull(commAddressDetailsVO.getCountry()))
        {
            bill_country="IND";
        }
        else{
            bill_country="IND";
        }

        transactionlogger.error("bill_country is-------------->"+bill_country);

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
            transactionlogger.error("TEST_SALE_URL------------------------->"+payment_url);


        }
        else {
            payment_url=RB.getString("LIVE_SALE_URL");
            transactionlogger.error("LIVE_SALE_URL------------------------->"+payment_url);
        }

        String request="mid="+mid+"&apikey="+apikey+"&fullname="+fullname+"&email="+email+"&phone="+phone+"&countrycode="+countrycode+"&orderid="+orderid+"&bill_address="+bill_address+"&bill_city="+bill_city+"&bill_state="+bill_state
                +"&bill_country="+bill_country+"&bill_postalcode="+bill_postalcode
                +"&currencycode="+currencycode+"&amount="+amount+"&response_url="+response_url+"&cancel_url="+cancel_url+"&domainname="+domainname+"&paymenttype="+paymenttype
                ;

        transactionlogger.error("request--------------for----" + request);

        String response= rubixpayutils.doHttpPostConnection(payment_url,request);
        transactionlogger.error("response--------------for--"+trackingID+"--"+response);

        try
        {

            String authcode= "";

            JSONObject jsonobj =new JSONObject(response);
            if (jsonobj.has("redirecturl")){
                commResponseVO.setStatus("pending3DConfirmation");
            }
            else {
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("fail");
                //commResponseVO.setrem(jsonobj.getString("securePaymentGateway"));
            }
            authcode= jsonobj.getString("authcode");
            redirecturl= jsonobj.getString("redirecturl");
            commResponseVO.setTransactionId(authcode);
            transactionlogger.error("authcode---------------------------->"+authcode);
        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException------------------------->",e);
        }



        commResponseVO.setUrlFor3DRedirect(redirecturl);
        transactionlogger.error("redirecturl---------------------------->"+redirecturl);
        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in Rubixpay ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;
        RubixpayUtils rubixpayutils =new RubixpayUtils();
        CommRequestVO commRequestVO = rubixpayutils.getRubixPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        RubixpayPaymentProcess  rubixpayPaymentProcess =new RubixpayPaymentProcess();
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
                html = rubixpayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect rubixpay form -- >>"+html);
            }
            transactionlogger.error("paymentid--------------------->"+transRespDetails.getTransactionId());

           rubixpayutils.updateTransaction(commonValidatorVO.getTrackingid(),transRespDetails.getTransactionId());
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in TapMioPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingID, GenericRequestVO requestVO)
    {
        transactionlogger.error("Entering process3DSaleConfirmation of RubixPayPaymentGateway......");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        RubixpayUtils rubixpayutils =new RubixpayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        transactionlogger.error("CommTransactionDetailsVO------------------------>"+transactionDetailsVO);
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        transactionlogger.error("inside process3DAuthConfirmation ----- ");
        transactionlogger.error("accountId is-------------->"+accountId);

        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions = new Functions();
        String status="";
        String payment_url=RB.getString("GET_TRANSACTION");
        String mid=gatewayAccount.getMerchantId();
        transactionlogger.error("mid is-------------->"+mid);
        String serverkey=gatewayAccount.getFRAUD_FTP_USERNAME();
        transactionlogger.error("serverkey is-------------->"+serverkey);
        String transactionid=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        transactionlogger.error("transactionid(authcode) is-------------->"+transactionid);


        try
        {


            String request="mid="+mid+"&serverkey="+serverkey+"&transactionid="+transactionid;;
            transactionlogger.error("process3DSaleConfirmation request ------------------------> " + request);
            String response= rubixpayutils.doHttpPostConnection(payment_url,request );
            transactionlogger.error("process3DSaleConfirmation response -------------------------> " + response);

            if (response != null)
            {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("txn_status"))
                {
                    status=jsonObject.getString("txn_status");
                }

                if (status.equalsIgnoreCase("TXN_COMPLETE"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }

                else if(status.equalsIgnoreCase("TXN_UPI_REDIRECT")||status.equalsIgnoreCase("TXN_NB_REDIRECT")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }
                else if(status.equalsIgnoreCase("error")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }

                commResponseVO.setTransactionId(jsonObject.getString("transactionid"));
                commResponseVO.setResponseHashInfo(jsonObject.getString("paymentmode"));
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Sys:Transaction Fail No Response");
                commResponseVO.setDescription("Sys:Transaction Fail No Response");
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
        RubixpayUtils rubixpayutils =new RubixpayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        transactionlogger.error("CommTransactionDetailsVO------------------------>"+transactionDetailsVO);
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        transactionlogger.error("inside processInquiry ----- ");
        transactionlogger.error("accountId is-------------->"+accountId);

        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions = new Functions();
        String status="";
        String payment_url=RB.getString("GET_TRANSACTION");
        String mid=gatewayAccount.getMerchantId();
        transactionlogger.error("mid is-------------->"+mid);
        String serverkey=gatewayAccount.getFRAUD_FTP_USERNAME();
        transactionlogger.error("serverkey is-------------->"+serverkey);
        String transactionid=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        transactionlogger.error("transactionid(authcode) is-------------->"+transactionid);


        try
        {


            String request="mid="+mid+"&serverkey="+serverkey+"&transactionid="+transactionid;;
            transactionlogger.error("process3DSaleConfirmation request ------------------------> " + request);
            String response= rubixpayutils.doHttpPostConnection(payment_url,request );
            transactionlogger.error("process3DSaleConfirmation response -------------------------> " + response);

            if (response != null)
            {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("txn_status"))
                {
                    status=jsonObject.getString("txn_status");
                }

                if (status.equalsIgnoreCase("TXN_COMPLETE"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }

                else if(status.equalsIgnoreCase("TXN_UPI_REDIRECT")||status.equalsIgnoreCase("TXN_NB_REDIRECT")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }
                else if(status.equalsIgnoreCase("error")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setRemark(jsonObject.getString("txn_message"));
                    commResponseVO.setDescription(jsonObject.getString("txn_message"));
                }

                commResponseVO.setTransactionId(jsonObject.getString("transactionid"));
                commResponseVO.setResponseHashInfo(jsonObject.getString("paymentmode"));
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark("Sys:Transaction Fail No Response");
                commResponseVO.setDescription("Sys:Transaction Fail No Response");
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception-----", e);
        }



        return commResponseVO;
    }

}
