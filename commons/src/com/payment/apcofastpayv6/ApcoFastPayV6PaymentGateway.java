package com.payment.apcofastpayv6;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;

import com.manager.dao.MerchantDAO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.apcoFastpay.ApcoFastpayPaymentGateway;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.imoneypay.IMoneyPayPaymentProcess;
import com.payment.imoneypay.IMoneypPayUtils;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.axis.AxisFault;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.impl.client.HttpClients;


import javax.xml.rpc.ServiceException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by Jyoti on 9/16/2021.
 */
public class ApcoFastPayV6PaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="fastpayv6";
    private static Logger log=new Logger(ApcoFastPayV6PaymentGateway.class.getName());
    private static TransactionLogger transactionLogger= new TransactionLogger(ApcoFastPayV6PaymentGateway.class.getName());
    ResourceBundle rb= LoadProperties.getProperty("com.directi.pg.ApcoFastPayV6");

    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.3CharCountryList");
    public  ApcoFastPayV6PaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        log.error("Entering into processAuthentication of ApcoFastPayV6PaymentGateway :::::");
        transactionLogger.error("Entering into processAuthentication of  ApcoFastPayV6PaymentGateway :::::");
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        ApcoFastPayV6Utils apcoFastPayV6Utils=new ApcoFastPayV6Utils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        //Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String URL=rb.getString("URL");
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String ProfileId=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String transactionId=commTransactionDetailsVO.getOrderId();
        boolean isTest = gatewayAccount.isTest();
        String Email=addressDetailsVO.getEmail();
        String Amount=commTransactionDetailsVO.getAmount();
        String Address="";

        String UDF2="";
        String UDF3="";
        String redirectionUrl="";
        String statusUrl="";
        String failedUrl="";
        String Country="";
        if(functions.isValueNull(addressDetailsVO.getCountry())){
            Country=RBTemplate.getString(addressDetailsVO.getCountry());
        }
        if(functions.isValueNull(addressDetailsVO.getStreet()))
        {
            Address=addressDetailsVO.getStreet();
        }

        String UDF1=commTransactionDetailsVO.getOrderId();
        if( UDF1 != null && UDF1.equals(trackingID)){
            UDF1=commTransactionDetailsVO.getMerchantOrderId();
        }
        if(functions.isValueNull(commMerchantVO.getHostUrl())){

            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_auth";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_auth";
        }
        else
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_auth";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_auth";

        }
        String transactionMode="";

        String XMLParamRequest ="<Transaction hash=\""+secretWord+"\">" +
                "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                "<ProfileID>"+ProfileId+"</ProfileID>" +
                "<ForceBank>PTEST</ForceBank>"+
                "<ActionType>4</ActionType>"+
                "<Value>"+Amount+"</Value>" +
                "<Curr>"+currencyCode+"</Curr>" +
                "<Lang>en</Lang>" +
                "<ORef>"+trackingID+"</ORef>" +
                "<Email>"+Email+"</Email>"+
                "<Address>"+Address+"</Address>"+
                "<RegCountry>"+ Country +"</RegCountry>"+
                "<UDF1>"+UDF1+"</UDF1>" +
                "<UDF2></UDF2>" +
                "<UDF3></UDF3>" +
                "<TEST />"+
                "</Transaction>";


      System.out.println("AUTH XML Request---------------->"+XMLParamRequest);

       transactionLogger.error("Auth XML Request-----------for--" + trackingID + "-->"+XMLParamRequest);

        try{
            JSONObject jsonObject=new JSONObject();

            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass", MerchPass);
            jsonObject.put("XMLParam", URLEncoder.encode(XMLParamRequest,"UTF-8"));

            transactionLogger.error("Auth JSON Request---------------->"+jsonObject.toString());


            String response = apcoFastPayV6Utils.doPostHTTPSURLConnectionClient(URL, jsonObject.toString());


            transactionLogger.error("Response---------------->"+response);


            if (functions.isValueNull(response) && response.contains("{"))
            {
                String result="";
                String baseUrl="";
                String token="";
                String errorMsg="";
                JSONObject jsonReader=new JSONObject(response);

                if(jsonReader != null)
                {
                    if(jsonReader.has("Result") && functions.isValueNull(jsonReader.getString("Result")))
                    {
                        result=jsonReader.getString("Result");
                    }
                    if (jsonReader.has("ErrorMsg") && functions.isValueNull(jsonReader.getString("ErrorMsg"))){
                        errorMsg = jsonReader.getString("ErrorMsg");
                    }
                    if(jsonReader.has("BaseURL") && functions.isValueNull(jsonReader.getString("BaseURL")))
                    {
                        baseUrl=jsonReader.getString("BaseURL");
                    }
                    if(jsonReader.has("Token") && functions.isValueNull(jsonReader.getString("Token")))
                    {
                        token=jsonReader.getString("Token");
                    }
                    if (result.equalsIgnoreCase("OK"))
                    {
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setDescription(errorMsg);
                        comm3DResponseVO.setUrlFor3DRedirect(baseUrl + token);
                        transactionLogger.error("URLFOR3DREdirect fastpay processAuthentication =====>"+comm3DResponseVO.getUrlFor3DRedirect());
                        return comm3DResponseVO;
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setDescription("Transaction Failed");
                        comm3DResponseVO.setRemark(jsonReader.getString("ErrorMsg"));
                    }
                }
                else {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            } else {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processSale ApcoFastPayV6PaymentGateway-----   ");
        ApcoFastPayV6Utils apcoFastPayV6Utils=new ApcoFastPayV6Utils();
        Functions functions = new Functions();
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        transactionLogger.error("commTransactionDetailsVO.getCurrency()==>"+commTransactionDetailsVO.getCurrency());
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        transactionLogger.error("Currency============>" + currency);
        Integer currencyCode = Integer.parseInt(currency);
        String URL=rb.getString("URL");
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String ProfileId=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String transactionId=commTransactionDetailsVO.getOrderId();
        boolean isTest = gatewayAccount.isTest();
        String Email=addressDetailsVO.getEmail();
        String Amount=commTransactionDetailsVO.getAmount();
        String Address="";
        String UDF2="";
        String UDF3="";
        String UDF1=commTransactionDetailsVO.getOrderId();
        if( UDF1 != null && UDF1.equals(trackingID)){
            UDF1=commTransactionDetailsVO.getMerchantOrderId();
        }
        if(functions.isValueNull(addressDetailsVO.getStreet()))
        {
            Address=addressDetailsVO.getStreet();
        }
        String Country="";
        if(functions.isValueNull(addressDetailsVO.getCountry())){
            Country=RBTemplate.getString(addressDetailsVO.getCountry());
        }
        String redirectionUrl="";
        String statusUrl="";
        String failedUrl="";
        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_sale";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_sale";
        }
        else
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_sale";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_sale";

        }
        String XMLParamRequest ="<Transaction hash=\""+secretWord+"\">" +
                "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                // "<FailedRedirectionURL>"+failedUrl+"</FailedRedirectionURL>"+
                "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                "<ProfileID>"+ProfileId+"</ProfileID>" +
                "<ForceBank>PTEST</ForceBank>"+
                "<ActionType>1</ActionType>"+
                "<Value>"+Amount+"</Value>" +
                "<Curr>"+currencyCode+"</Curr>" +
                "<Lang>en</Lang>" +
                "<ORef>"+trackingID+"</ORef>" +
                "<Email>"+Email+"</Email>"+
                "<Address>"+Address+"</Address>"+
                "<RegCountry>"+ Country +"</RegCountry>"+
                "<UDF1>" +UDF1+ "</UDF1>" +
                "<UDF2></UDF2>" +
                "<UDF3></UDF3>" +
                "<TEST />"+
                "</Transaction>";

        System.out.println("Request---------- for ------>"  + trackingID + "--" + XMLParamRequest);
        transactionLogger.error("Sale XML  Request------- for --------->"  + trackingID + "--"+XMLParamRequest);

        try{
            JSONObject jsonObject=new JSONObject();

            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass", MerchPass);
            jsonObject.put("XMLParam", URLEncoder.encode(XMLParamRequest,"UTF-8"));

            transactionLogger.error("Sale Encoded JSON Request------- for --------->"  + trackingID + "--"+jsonObject.toString());

            String response = apcoFastPayV6Utils.doPostHTTPSURLConnectionClient(URL, jsonObject.toString());

            transactionLogger.error("Sale Response----------- for ----->"  + trackingID + "--" +response);


            if (functions.isValueNull(response) && response.contains("{"))
            {
                String result="";
                String baseUrl="";
                String token="";
                String errorMsg="";
                JSONObject jsonReader=new JSONObject(response);

                if(jsonReader != null)
                {
                    if(jsonReader.has("Result") && functions.isValueNull(jsonReader.getString("Result")))
                    {
                        result=jsonReader.getString("Result");
                    }
                    if (jsonReader.has("ErrorMsg") && functions.isValueNull(jsonReader.getString("ErrorMsg"))){
                        errorMsg = jsonReader.getString("ErrorMsg");
                    }
                    if(jsonReader.has("BaseURL") && functions.isValueNull(jsonReader.getString("BaseURL")))
                    {
                        baseUrl=jsonReader.getString("BaseURL");
                    }
                    if(jsonReader.has("Token") && functions.isValueNull(jsonReader.getString("Token")))
                    {
                        token=jsonReader.getString("Token");
                    }
                    if (result.equalsIgnoreCase("OK")){
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setDescription(errorMsg);
                        comm3DResponseVO.setUrlFor3DRedirect(baseUrl+token);
                        transactionLogger.error("URLFOR3DREdirect fastpay processSale =====>"+comm3DResponseVO.getUrlFor3DRedirect());

                        return comm3DResponseVO;

                    }
                    else
                    {
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setDescription("Transaction Failed");
                        comm3DResponseVO.setRemark(jsonReader.getString("ErrorMsg"));
                    }
                }
                else {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }
            else {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");

            }

        }
        catch(Exception e)
        {
            transactionLogger.error("ApcoFatsPayV6PaymentGateway  processSale exception ---"+e);
        }

        return comm3DResponseVO;
    }


    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in FastpayV6 ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        MerchantDetailsVO merchantDetailsVO = null;
        ApcoFastPayV6Utils apcoFastPayV6Utils                         = new ApcoFastPayV6Utils();
        ApcoFastPayV6PaymentProcess apcoFastPayV6PaymentProcess = new ApcoFastPayV6PaymentProcess();
        CommRequestVO commRequestVO     = apcoFastPayV6Utils.getIMoneyPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        MerchantDAO merchantDAO             = new MerchantDAO();
        merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        try
        {
            transactionLogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                transRespDetails = (Comm3DResponseVO) this.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);
            }
            else {
                transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            }

            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = apcoFastPayV6PaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionLogger.error("automatic redirect FastPayv6 form -- >>"+html);
            }
            else if (transRespDetails.getStatus().equalsIgnoreCase("Failed"))
            {
                html="Failed";
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in ApcoFastPayV6PaymentProcess---", e);
        }
        return html;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        log.debug("Entering into processCapture of ApcoFastPayV6PaymentGateway:::::");
        transactionLogger.debug("Entering into processCapture of ApcoFastPayV6PaymentGateway:::::");
        ApcoFastPayV6Utils apcoFastPayV6Utils=new ApcoFastPayV6Utils();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO =  new CommResponseVO();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String URL=rb.getString("URL");
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String ProfileId=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String Country="";
        if(functions.isValueNull(addressDetailsVO.getCountry())){
            Country=RBTemplate.getString(addressDetailsVO.getCountry());
        }
        String transactionId=commTransactionDetailsVO.getOrderId();
        boolean isTest = gatewayAccount.isTest();
        String Email="";
        if(functions.isValueNull(addressDetailsVO.getEmail()))
        {
            Email=addressDetailsVO.getEmail();
        }
        String Amount=commTransactionDetailsVO.getAmount();
        String Address="";
        if(functions.isValueNull(addressDetailsVO.getStreet()))
        {
            Address=addressDetailsVO.getStreet();
        }
        String TransID = commTransactionDetailsVO.getPreviousTransactionId();
        String UDF1=commTransactionDetailsVO.getOrderId();
        if( UDF1 != null && UDF1.equals(trackingID)){
            UDF1=commTransactionDetailsVO.getMerchantOrderId();
        }
        String UDF2="";
        String UDF3="";
        String redirectionUrl="";
        String statusUrl="";
        String failedUrl="";
        if(functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_capture";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_capture";
        }
        else
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_capture";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_capture";

        }
        try
        {
            String XMLParamRequest = "<Transaction hash=\"" + secretWord + "\">" +
                    "<RedirectionURL>" + redirectionUrl + "</RedirectionURL>" +
                    "<status_url urlEncode=\"true\">" + statusUrl + "</status_url>" +
                    // "<FailedRedirectionURL>"+failedUrl+"</FailedRedirectionURL>"+
                    "<FailedRedirectionURL>" + redirectionUrl + "</FailedRedirectionURL>" +
                    "<ProfileID>" + ProfileId + "</ProfileID>" +
                    "<ForceBank>PTEST</ForceBank>"+
                    "<PspID>" + TransID + "</PspID>" +
                    "<ActionType>5</ActionType>" +
                    "<Value>" + Amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>en</Lang>" +
                    "<ORef>" + trackingID + "</ORef>" +
                    "<Email>" + Email + "</Email>" +
                    "<Address>" + Address + "</Address>" +
                    "<RegCountry>"+ Country +"</RegCountry>"+
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2>"+UDF2+"</UDF2>" +
                    "<UDF3></UDF3>" +
                    "<TEST />" +
                    "</Transaction>";


            System.out.println("Capture Request---------- for ------>"  + trackingID + "--" + XMLParamRequest);

            transactionLogger.error("Capture Request-------- for -------->" + trackingID + "--" +  XMLParamRequest);

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("MerchID", MerchID);
            jsonObject.put("MerchPass", MerchPass);
            jsonObject.put("XMLParam", URLEncoder.encode(XMLParamRequest,"UTF-8"));
            System.out.println("json---" + jsonObject.toString());

            transactionLogger.error("Capture Encoded JSON Request------- for --------->"  + trackingID + "--"+jsonObject.toString());

            String response = ApcoFastPayV6Utils.doPostHTTPSURLConnectionClient(URL, jsonObject.toString());
            transactionLogger.error("-----Capture response--for--" + trackingID + "--" + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String result = "";
                String baseUrl = "";
                String token = "";
                String errorMsg = "";
                JSONObject jsonReader = new JSONObject(response);
                String status="";

                if (jsonReader != null)
                {
                    if (jsonReader.has("Result") && functions.isValueNull(jsonReader.getString("Result")))
                    {
                        result = jsonReader.getString("Result");
                    }
                    if (jsonReader.has("ErrorMsg") && functions.isValueNull(jsonReader.getString("ErrorMsg"))){
                        errorMsg = jsonReader.getString("ErrorMsg");
                    }
                    if (jsonReader.has("BaseURL") && functions.isValueNull(jsonReader.getString("BaseURL")))
                    {
                        baseUrl = jsonReader.getString("BaseURL");
                    }
                    if (jsonReader.has("Token") && functions.isValueNull(jsonReader.getString("Token")))
                    {
                        token = jsonReader.getString("Token");
                    }
                    if (result.equalsIgnoreCase("OK"))
                    {
                        TransactionManager transactionManager = new TransactionManager();
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(baseUrl+token);

                        httpClient.executeMethod(postMethod);
                        transactionLogger.error("Response capture code---"+postMethod.getStatusCode());

                        transactionLogger.error("inside if ");
                        TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingID);
                        if("capturesuccess".equalsIgnoreCase(updatedTransactionDetailsVO.getStatus())){
                            transactionLogger.error("inside  if capture condition ");
                            commResponseVO.setStatus("success");
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setTransactionId(updatedTransactionDetailsVO.getPaymentId());
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setCurrency(updatedTransactionDetailsVO.getCurrency());
                            commResponseVO.setRemark(updatedTransactionDetailsVO.getRemark());
                            return commResponseVO;

                        }
                        else{
                            commResponseVO.setStatus("pending");
                        }
                }
                else {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                }
            }
            else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
            }
        }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        log.debug("Entering into processRefund of ApcoFastPayV6PaymentGateway:::::");
        transactionLogger.debug("Entering into processRefund of ApcoFastPayV6PaymentGateway:::::");
        CommResponseVO commResponseVO =  new CommResponseVO();
        ApcoFastPayV6Utils apcoFastPayV6Utils=new ApcoFastPayV6Utils();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String Country="";
        if(functions.isValueNull(addressDetailsVO.getCountry())){
            Country=RBTemplate.getString(addressDetailsVO.getCountry());
        }
        String transactionId=commTransactionDetailsVO.getMerchantOrderId();
        String Email="";
        if(functions.isValueNull(addressDetailsVO.getEmail()))
        {
            Email=addressDetailsVO.getEmail();
        }
        String Amount=commTransactionDetailsVO.getAmount();

        String Address="";

        if(functions.isValueNull(addressDetailsVO.getStreet()))
        {
            Address=addressDetailsVO.getStreet();
        }
        String UDF1=commTransactionDetailsVO.getOrderId();
        if( UDF1 != null && UDF1.equals(trackingID)){
            UDF1=commTransactionDetailsVO.getMerchantOrderId();
        }
        String UDF2="";
        String UDF3="";
        String redirectionUrl="";
        String statusUrl="";
        String failedUrl="";


        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String ProfileId = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String secretWord = gatewayAccount.getFRAUD_FTP_PATH();
        String orderId = commTransactionDetailsVO.getOrderId();
        boolean isTest = gatewayAccount.isTest();
        String testString = "";
        String URL = "";
        if (isTest)
        {
            testString = "<TEST />";
            URL = rb.getString("URL");
        }
        else
        {
            URL = rb.getString("URL");
        }
        
        String amount = transactionDetailsVO.getAmount();
        String setPreviousTransactionId = transactionDetailsVO.getPreviousTransactionId();
        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_refund";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_refund";
        }
        else {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_refund";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_refund";
        }

        String XMLParamRequest ="<Transaction hash=\""+secretWord+"\">" +
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+ProfileId+"</ProfileID>" +
                    "<ForceBank>PTEST</ForceBank>"+
                    "<ActionType>12</ActionType>"+
                    "<PspID>" + setPreviousTransactionId + "</PspID>" +
                    "<Value>"+Amount+"</Value>" +
                    "<Curr>"+currencyCode+"</Curr>" +
                    "<Lang>en</Lang>" +
                    "<ORef>"+orderId+"</ORef>" +
                    "<Email>"+Email+"</Email>"+
                    "<Address>"+Address+"</Address>"+
                    "<RegCountry>"+ Country +"</RegCountry>"+
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2></UDF2>" +
                    "<UDF3></UDF3>" +
                    "<TEST />"+
                    "</Transaction>";
        transactionLogger.error("-----refund1 XML  request--for--" + trackingID + "--" + XMLParamRequest);

        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MerchID", MerchID);
            jsonObject.put("MerchPass", MerchPass);
            jsonObject.put("XMLParam", URLEncoder.encode(XMLParamRequest,"UTF-8"));



            transactionLogger.error("-----refund1 JSON  request--for--" + trackingID + "--" +jsonObject.toString());

            String response = ApcoFastPayV6Utils.doPostHTTPSURLConnectionClient(URL, jsonObject.toString());

            transactionLogger.error("-----refund1 response--for--" + trackingID + "--" + response);


            if (functions.isValueNull(response) && response.contains("{"))
            {
                String result = "";
                String baseUrl = "";
                String token = "";
                String errorMsg = "";
                JSONObject jsonReader = new JSONObject(response);

                if (jsonReader != null)
                {
                    if (jsonReader.has("Result") && functions.isValueNull(jsonReader.getString("Result")))
                    {
                        result = jsonReader.getString("Result");
                    }
                    if (jsonReader.has("ErrorMsg") && functions.isValueNull(jsonReader.getString("ErrorMsg"))){
                        errorMsg = jsonReader.getString("ErrorMsg");
                    }
                    if (jsonReader.has("BaseURL") && functions.isValueNull(jsonReader.getString("BaseURL")))
                    {
                        baseUrl = jsonReader.getString("BaseURL");
                    }
                    if (jsonReader.has("Token") && functions.isValueNull(jsonReader.getString("Token")))
                    {
                        token = jsonReader.getString("Token");
                    }
                    if (result.equalsIgnoreCase("OK")){
                       // transactionLogger.error("UrlFor3DRedirect fastpay processRefund ===>" + comm3DResponseVO.getUrlFor3DRedirect());
                        TransactionManager transactionManager = new TransactionManager();
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(baseUrl+token);

                        httpClient.executeMethod(postMethod);
                        transactionLogger.error("Response Refund code---"+postMethod.getStatusCode());

                            transactionLogger.error("inside if ");
                            TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingID);
                            if("Reversed".equalsIgnoreCase(updatedTransactionDetailsVO.getStatus())){
                                transactionLogger.error("inside  if reversed condition ");
                                commResponseVO.setStatus("success");
                                commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                                commResponseVO.setTransactionId(updatedTransactionDetailsVO.getPaymentId());
                                commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                                commResponseVO.setCurrency(updatedTransactionDetailsVO.getCurrency());
                                commResponseVO.setRemark(updatedTransactionDetailsVO.getRemark());
                                return commResponseVO;

                            }
                            else{
                                commResponseVO.setStatus("pending");
                            }
                    }
                    else
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setRemark(jsonReader.getString("ErrorMsg"));
                    }
                }
                else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }
            else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");

            }


        }
        catch (Exception e)
        {
            transactionLogger.error("FastPay Refund Exception--for--" + trackingID + "--", e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        log.debug("Entering into processPayout of ApcoFastPayV6PaymentGateway:::::");
        transactionLogger.debug("Entering into processPayout of ApcoFastPayV6PaymentGateway:::::");
        ApcoFastPayV6Utils apcoFastPayV6Utils=new ApcoFastPayV6Utils();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String ProfileId=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String Country=RBTemplate.getString(addressDetailsVO.getCountry());
        String orderId = transactionDetailsVO.getOrderId();

        String setPreviousTransactionId = transactionDetailsVO.getPaymentId();

        boolean isTest = gatewayAccount.isTest();
        String Email="";
        if(functions.isValueNull(addressDetailsVO.getEmail()))
        {
            Email=addressDetailsVO.getEmail();
        }
        String Amount=transactionDetailsVO.getAmount();
        String Address="";
        if(functions.isValueNull(addressDetailsVO.getStreet()))
        {
            Address=addressDetailsVO.getStreet();
        }



        String UDF1=transactionDetailsVO.getOrderId();
        if( UDF1 != null && UDF1.equals(trackingID)){
            UDF1=transactionDetailsVO.getMerchantOrderId();
        }
        String UDF2="";
        String UDF3="";
        String redirectionUrl="";
        String statusUrl="";
        String failedUrl="";

        if(functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_payout";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_payout";
        }
        else
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_payout";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_payout";

        }
        String testString = "";
        String URL="";
        if (isTest){
            testString = "<TEST />";
            URL=rb.getString("URL");

        }else{
            URL=rb.getString("URL");
        }

        String XMLParamRequest ="<Transaction hash=\""+secretWord+"\">" +
                "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                "<ProfileID>"+ProfileId+"</ProfileID>" +
                "<ForceBank>PTEST</ForceBank>"+
                "<ActionType>13</ActionType>"+
                "<PspID>" + setPreviousTransactionId + "</PspID>" +
                "<Value>"+Amount+"</Value>" +
                "<Curr>"+currencyCode+"</Curr>" +
                "<Lang>en</Lang>" +
                "<ORef>"+trackingID+"</ORef>" +
                "<Email>"+Email+"</Email>"+
                "<Address>"+Address+"</Address>"+
                "<RegCountry>"+ Country +"</RegCountry>"+
                "<UDF1>"+UDF1+"</UDF1>" +
                "<UDF2></UDF2>" +
                "<UDF3></UDF3>" +
                "<TEST />"+
                "</Transaction>";


        transactionLogger.error("Payout XML Request------- for --------->" + trackingID + "--" +XMLParamRequest);
        transactionLogger.error("PSPID Previous Transaction Id ====================="+setPreviousTransactionId);

        try{
            JSONObject jsonObject=new JSONObject();

            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass", MerchPass);
            jsonObject.put("XMLParam", URLEncoder.encode(XMLParamRequest,"UTF-8"));

            transactionLogger.error("Payout JSON Request--------- for ------->" + trackingID + "--"+jsonObject.toString());


            String response = apcoFastPayV6Utils.doPostHTTPSURLConnectionClient(URL, jsonObject.toString());


            transactionLogger.error("Payout  Response------- for --------->" + trackingID + "--"+response);


            if (functions.isValueNull(response) && response.contains("{"))
            {
                String result="";
                String baseUrl="";
                String token="";
                String errorMsg="";
                JSONObject jsonReader=new JSONObject(response);

                if(jsonReader != null)
                {
                    if(jsonReader.has("Result") && functions.isValueNull(jsonReader.getString("Result")))
                    {
                        result=jsonReader.getString("Result");
                    }
                    if (jsonReader.has("ErrorMsg") && functions.isValueNull(jsonReader.getString("ErrorMsg"))){
                        errorMsg = jsonReader.getString("ErrorMsg");
                    }
                    if(jsonReader.has("BaseURL") && functions.isValueNull(jsonReader.getString("BaseURL")))
                    {
                        baseUrl=jsonReader.getString("BaseURL");
                    }
                    if(jsonReader.has("Token") && functions.isValueNull(jsonReader.getString("Token")))
                    {
                        token=jsonReader.getString("Token");
                    }
                    if (result.equalsIgnoreCase("OK"))
                    {

                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription(errorMsg);
                        comm3DResponseVO.setUrlFor3DRedirect(baseUrl+token);
                        transactionLogger.error("UrlFor3DRedirect fastpay processPayout ===>"+comm3DResponseVO.getUrlFor3DRedirect());
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(comm3DResponseVO.getUrlFor3DRedirect());

                        httpClient.executeMethod(postMethod);

                        transactionLogger.error("Response Payout code---"+postMethod.getStatusCode());

                        return comm3DResponseVO;

                    }
                    else
                    {

                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setDescription("Transaction Failed");
                        comm3DResponseVO.setRemark("Payout Failed");
                    }
                }
            }
            else {
                comm3DResponseVO.setStatus("Failed");
                comm3DResponseVO.setDescription("Payout Failed");
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {

        log.error("Entering into processVoid of ApcoFastPayV6PaymentGateway :::::");
        transactionLogger.error("Entering into processVoid of  ApcoFastPayV6PaymentGateway :::::");

        ApcoFastPayV6Utils apcoFastPayV6Utils=new ApcoFastPayV6Utils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO =new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String URL=rb.getString("URL");
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String ProfileId=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String Country="";
        if(functions.isValueNull(addressDetailsVO.getCountry())){
            Country=RBTemplate.getString(addressDetailsVO.getCountry());
        }
        String transactionId=commTransactionDetailsVO.getOrderId();
        String TransID = commTransactionDetailsVO.getPreviousTransactionId();
        boolean isTest = gatewayAccount.isTest();
        String Email="";
        if(functions.isValueNull(addressDetailsVO.getEmail()))
        {
            Email=addressDetailsVO.getEmail();
        }
        String Amount=commTransactionDetailsVO.getAmount();
        String Address="";

        if(functions.isValueNull(addressDetailsVO.getStreet()))
        {
            Address=addressDetailsVO.getStreet();
        }
        String UDF1=commTransactionDetailsVO.getOrderId();
        if( UDF1 != null && UDF1.equals(trackingID)){
            UDF1=commTransactionDetailsVO.getMerchantOrderId();
        }
        String UDF2="";
        String UDF3="";
        String redirectionUrl="";
        String statusUrl="";
        String failedUrl="";

        if(functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_cancel";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_cancel";
        }
        else
        {
            redirectionUrl=rb.getString("FastPayV6_FRONTEND")+trackingID+"_cancel";
            statusUrl=rb.getString("FastPayV6_BACKEND")+trackingID+"_cancel";

        }
        try
        {

            String XMLParamRequest = "<Transaction hash=\"" + secretWord + "\">" +
                    "<RedirectionURL>" + redirectionUrl + "</RedirectionURL>" +
                    "<status_url urlEncode=\"true\">" + statusUrl + "</status_url>" +
                    "<FailedRedirectionURL>" + redirectionUrl + "</FailedRedirectionURL>" +
                    "<ProfileID>" + ProfileId + "</ProfileID>" +
                    "<ForceBank>PTEST</ForceBank>"+
                    "<PspID>" + TransID + "</PspID>" +
                    "<ActionType>9</ActionType>" +
                    "<Value>" + Amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>en</Lang>" +
                    "<ORef>" + trackingID + "</ORef>" +
                    "<Email>" + Email + "</Email>" +
                    "<Address>" + Address + "</Address>" +
                    "<RegCountry>"+ Country +"</RegCountry>"+
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2></UDF2>" +
                    "<UDF3></UDF3>" +
                    "<TEST />" +
                    "</Transaction>";

            transactionLogger.error("Cancel xml Request--------- for ------->"  + trackingID + "--" + XMLParamRequest);


            transactionLogger.debug("Cancel request--for--" + trackingID + "--" + XMLParamRequest);
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("MerchID", MerchID);
            jsonObject.put("MerchPass", MerchPass);
            jsonObject.put("XMLParam", URLEncoder.encode(XMLParamRequest,"UTF-8"));
            transactionLogger.error("cancel json request ---" + jsonObject.toString());

            String response = ApcoFastPayV6Utils.doPostHTTPSURLConnectionClient(URL, jsonObject.toString());
            transactionLogger.error("-----Cancel response--for--" + trackingID + "--" + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String result = "";
                String baseUrl = "";
                String token = "";
                String errorMsg = "";
                JSONObject jsonReader = new JSONObject(response);
                String status="";

                if (jsonReader != null)
                {
                    if (jsonReader.has("Result") && functions.isValueNull(jsonReader.getString("Result")))
                    {
                        result = jsonReader.getString("Result");
                    }
                    if (jsonReader.has("ErrorMsg") && functions.isValueNull(jsonReader.getString("ErrorMsg"))){
                        errorMsg = jsonReader.getString("ErrorMsg");
                    }
                    if (jsonReader.has("BaseURL") && functions.isValueNull(jsonReader.getString("BaseURL")))
                    {
                        baseUrl = jsonReader.getString("BaseURL");
                    }
                    if (jsonReader.has("Token") && functions.isValueNull(jsonReader.getString("Token")))
                    {
                        token = jsonReader.getString("Token");
                    }
                    if (result.equalsIgnoreCase("OK")){
                        TransactionManager transactionManager = new TransactionManager();
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(baseUrl+token);

                        httpClient.executeMethod(postMethod);
                        transactionLogger.error("Response void code---"+postMethod.getStatusCode());

                        transactionLogger.error("inside if ");
                        TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingID);
                        if("authcancelled".equalsIgnoreCase(updatedTransactionDetailsVO.getStatus())){
                            transactionLogger.error("inside  if void condition ");
                            commResponseVO.setStatus("success");
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setTransactionId(updatedTransactionDetailsVO.getPaymentId());
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setCurrency(updatedTransactionDetailsVO.getCurrency());
                            commResponseVO.setRemark(updatedTransactionDetailsVO.getRemark());
                            return commResponseVO;

                        }
                        else{
                            commResponseVO.setStatus("pending");
                        }
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
            }

        }
        }
        catch (org.json.JSONException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        log.debug("Entering into processInquiry of ApcoFastpayv6PaymentGatewaay:::::");
        transactionLogger.debug("Entering into processInquiry of ApcoFastpayv6PaymentGatewaay:::::");
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String orderId = commTransactionDetailsVO.getOrderId();

        try
        {
            String inquiryRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <getTransactionStatusExtended xmlns=\"https://www.apsp.biz/\">\n" +
                    "      <MerchID>" + MerchID + "</MerchID>\n" +
                    "      <MerchPass>" + MerchPass + "</MerchPass>\n" +
                    "      <ORef>" + orderId + "</ORef>\n" +
                    "    </getTransactionStatusExtended>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";

            transactionLogger.error("Inquiry Request--for--" + commTransactionDetailsVO.getOrderId() + "--" + inquiryRequest);
            transactionLogger.error("Inquery  URL===================>"+rb.getString("inquiry_url"));

            String response = ApcoFastPayV6Utils.doPostHTTPSURLConnection(rb.getString("inquiry_url"), inquiryRequest);

            transactionLogger.error("Inquiry Response--for--" + commTransactionDetailsVO.getOrderId() + "--" + response);

            String data = "";
            String result = "";
            String paymentid = "";
            String status = "";
            String remark = "";
            if (functions.isValueNull(response))
            {
                HashMap<String, String> map = (HashMap) ApcoFastPayV6Utils.readFastpayV6InquiryXMLResponse(response);
                if (map != null || map.size() != 0)
                {
                    result = map.get("Result");
                    transactionLogger.error("result--->" + result);
                    if (functions.isValueNull(result))
                    {
                        if (result.equals("OK") || result.equals("CAPTURED") || result.equals("APPROVED"))
                        {
                            status = "success";
                            comm3DResponseVO.setStatus(status);
                            comm3DResponseVO.setDescription("Transaction Successful");
                            comm3DResponseVO.setRemark(result);
                            comm3DResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }
                        else if (result.equalsIgnoreCase("NOTOK"))
                        {
                            status = "fail";
                            comm3DResponseVO.setStatus(status);
                            comm3DResponseVO.setRemark(result);
                            comm3DResponseVO.setDescription(result);
                        }
                        else if (result.equals("ENROLLED"))
                        {
                            status = "pending";
                            comm3DResponseVO.setStatus(status);
                        }
                        else
                        {
                            status = "pending";
                            comm3DResponseVO.setStatus(status);
                            comm3DResponseVO.setRemark("Transaction Pending");
                            comm3DResponseVO.setDescription("Transaction Pending");
                            comm3DResponseVO.setTransactionId(paymentid);
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    java.util.Date date = new java.util.Date();
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setMerchantId(MerchID);
                    comm3DResponseVO.setMerchantOrderId(map.get("ORef"));
                    comm3DResponseVO.setTransactionId(map.get("pspid"));
                    comm3DResponseVO.setAuthCode(map.get("AuthCode"));
                    comm3DResponseVO.setTransactionStatus(status);
                    comm3DResponseVO.setTransactionType("inquiry");
                    comm3DResponseVO.setAmount(map.get("Value"));
                    comm3DResponseVO.setCurrency(map.get("Currency"));
                    comm3DResponseVO.setBankTransactionDate(comm3DResponseVO.getResponseTime());
                    comm3DResponseVO.setDescription(comm3DResponseVO.getDescription());
                    comm3DResponseVO.setRemark(map.get("Result"));
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
            comm3DResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date date = new java.util.Date();
            comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (Exception e)
        {
            log.error("Inquiry Exception--for--" + commTransactionDetailsVO.getOrderId() + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApcoFastPayV6PaymentGateway.class.getName(), "processQuery()", null, "common", "Remote Exception while refunding transaction via Fastpayv6", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside ApcoFastPayV6Gateway processPayout inquiry------->");
        log.debug("Entering into processPayoutInquiry of ApcoFastpayv6PaymentGatewaay:::::");
        transactionLogger.debug("Entering into processPayoutInquiry of ApcoFastpayv6PaymentGatewaay:::::");
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String MerchPass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String orderId = commTransactionDetailsVO.getOrderId();

        try
        {
            String inquiryRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <getTransactionStatusExtended xmlns=\"https://www.apsp.biz/\">\n" +
                    "      <MerchID>" + MerchID + "</MerchID>\n" +
                    "      <MerchPass>" + MerchPass + "</MerchPass>\n" +
                    "      <ORef>" + orderId + "</ORef>\n" +
                    "    </getTransactionStatusExtended>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";

            transactionLogger.error("Payout Inquiry Request--for--" + commTransactionDetailsVO.getOrderId() + "--" + inquiryRequest);
            transactionLogger.error("Payout Inquery  URL===================>"+rb.getString("inquiry_url"));

            String response = ApcoFastPayV6Utils.doPostHTTPSURLConnection(rb.getString("inquiry_url"), inquiryRequest);

            transactionLogger.error(" Payout Inquiry Response--for--" + commTransactionDetailsVO.getOrderId() + "--" + response);

            String data = "";
            String result = "";
            String paymentid = "";
            String status = "";
            String remark = "";
            if (functions.isValueNull(response))
            {

                HashMap<String, String> map = (HashMap) ApcoFastPayV6Utils.readFastpayV6InquiryXMLResponse(response);
                if (map != null || map.size() != 0)
                {
                    result = map.get("Result");
                    transactionLogger.error("result--->" + result);
                    if (functions.isValueNull(result))
                    {
                        if (result.equals("OK") || result.equals("CAPTURED") || result.equals("APPROVED") || result.equals("PROCESSED"))
                        {
                            status = "success";
                            comm3DResponseVO.setStatus(status);
                            comm3DResponseVO.setDescription("Transaction Successful");
                            comm3DResponseVO.setRemark(result);
                            comm3DResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }
                        else if (result.equalsIgnoreCase("NOTOK"))
                        {
                            status = "fail";
                            comm3DResponseVO.setStatus(status);
                            comm3DResponseVO.setRemark(result);
                            comm3DResponseVO.setDescription(result);
                        }
                        else if (result.equals("ENROLLED"))
                        {
                            status = "pending";
                            comm3DResponseVO.setStatus(status);
                        }
                        else
                        {
                            status = "pending";
                            comm3DResponseVO.setStatus(status);
                            comm3DResponseVO.setRemark("Transaction Pending");
                            comm3DResponseVO.setDescription("Transaction Pending");
                            comm3DResponseVO.setTransactionId(paymentid);
                        }

                    }
                    else
                    {
                        status = "pending";
                        comm3DResponseVO.setStatus(status);
                        comm3DResponseVO.setRemark("Transaction Pending");
                        comm3DResponseVO.setDescription("Transaction Pending");
                        comm3DResponseVO.setTransactionId(paymentid);
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    java.util.Date date = new java.util.Date();
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setMerchantId(MerchID);
                    comm3DResponseVO.setMerchantOrderId(map.get("ORef"));
                    comm3DResponseVO.setTransactionId(map.get("pspid"));
                    comm3DResponseVO.setAuthCode(map.get("AuthCode"));
                    comm3DResponseVO.setTransactionStatus(status);
                    comm3DResponseVO.setTransactionType("inquiry");
                    comm3DResponseVO.setAmount(map.get("Value"));
                    comm3DResponseVO.setCurrency(map.get("Currency"));
                    comm3DResponseVO.setBankTransactionDate(comm3DResponseVO.getResponseTime());
                    comm3DResponseVO.setDescription(comm3DResponseVO.getDescription());
                    comm3DResponseVO.setRemark(map.get("Result"));
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
            comm3DResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date date = new java.util.Date();
            comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (Exception e)
        {
            log.error("Payout Inquiry Exception--for--" + commTransactionDetailsVO.getOrderId() + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApcoFastPayV6PaymentGateway.class.getName(), "processQuery()", null, "common", "Remote Exception while refunding transaction via Fastpayv6", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }
}
