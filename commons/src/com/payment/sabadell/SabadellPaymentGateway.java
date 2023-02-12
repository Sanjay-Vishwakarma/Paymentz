package com.payment.sabadell;

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
import com.payment.exceptionHandler.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 7/11/18.
 */
public class SabadellPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger= new TransactionLogger(SabadellPaymentGateway.class.getName());

    public static  final String GATEWAY_TYPE="sabadell";

    private static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.Sabadell");

    public SabadellPaymentGateway(String accountId){
        this.accountId=accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    /*public static void main(String[] args)
    {
        try
        {
            SabadellUtils sha256 = new SabadellUtils();


            sha256.setParameter("DS_MERCHANT_AMOUNT", "1234");
            sha256.setParameter("DS_MERCHANT_ORDER", "8747474");
            sha256.setParameter("DS_MERCHANT_MERCHANTCODE", "336866983");
            sha256.setParameter("DS_MERCHANT_CURRENCY", "978");
            sha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "0");
            sha256.setParameter("DS_MERCHANT_TERMINAL", "001");
            sha256.setParameter("DS_MERCHANT_MERCHANTURL", "https://staging.<hostname>.com/transaction/AWFrontEndServlet");
            sha256.setParameter("DS_MERCHANT_URLOK", "http://localhost:8081/transaction/NestPayFrontServlet?success");
            sha256.setParameter("DS_MERCHANT_URLKO", "http://localhost:8081/transaction/NestPayFrontServlet?fail");
            sha256.setParameter("DS_MERCHANT_PAN", "4548812049400004");
            sha256.setParameter("DS_MERCHANT_EXPIRYDATE", "1812");
            sha256.setParameter("DS_MERCHANT_CVV2", "123");
            sha256.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            String params = sha256.createMerchantParameters();

            System.out.println("params-----"+params);

            String key="sq7HjrUOBfKmC576ILgskD5srU870gJ7";

            String signature=sha256.createMerchantSignature(key);

            System.out.println("Signature-----"+signature);

         *//*String saleRequest = "{\n" +
                    "\"Ds_SignatureVersion\":\"HMAC_SHA256_V1\",\n" +
                    "\"Ds_MerchantParameters\": \""+params+"\",\n" +
                    "\"Ds_Signature\": \""+signature+"\",\n" +
                    "}";*//*


           *//* String saleRequest="Ds_SignatureVersion=HMAC_SHA256_V1" +
                    "&Ds_MerchantParameters="+params+
                    "&Ds_Signature="+signature.trim()+"";
            System.out.println("request-----"+saleRequest);
*//*

            *//*String response= SabadellUtils.doPostHTTPSURLConnection("https://sis-t.redsys.es:25443/sis/realizarPago", saleRequest);
            System.out.println("Resposne----"+response);


            //  String response="http://localhost:8081/transaction/NestPayFrontServlet?Ds_SignatureVersion=HMAC_SHA256_V1&Ds_MerchantParameters=eyJEc19EYXRlIjoiMTclMkYwNyUyRjIwMTgiLCJEc19Ib3VyIjoiMDglM0E0OCIsIkRzX1NlY3VyZVBheW1lbnQiOiIxIiwiRHNfQW1vdW50IjoiMTIzNCIsIkRzX0N1cnJlbmN5IjoiOTc4IiwiRHNfT3JkZXIiOiIzMjE0NSIsIkRzX01lcmNoYW50Q29kZSI6IjMyNzIzNDY4OCIsIkRzX1Rlcm1pbmFsIjoiMDAxIiwiRHNfUmVzcG9uc2UiOiIwMDAwIiwiRHNfVHJhbnNhY3Rpb25UeXBlIjoiMCIsIkRzX01lcmNoYW50RGF0YSI6IiIsIkRzX0F1dGhvcmlzYXRpb25Db2RlIjoiMjI3Mjc3IiwiRHNfQ29uc3VtZXJMYW5ndWFnZSI6IjIiLCJEc19DYXJkX0NvdW50cnkiOiI3MjQiLCJEc19DYXJkX0JyYW5kIjoiMSJ9&Ds_Signature=48qwB2APQRm1qco00kqK3NSdV9oKtF2WeuqrT23GkJM=";
            String resposneParams="eyJEc19EYXRlIjoiMjElMkYwNyUyRjIwMTgiLCJEc19Ib3VyIjoiMTElM0EyNCIsIkRzX1\n" +
                    "NlY3VyZVBheW1lbnQiOiIwIiwiRHNfQ2FyZF9Db3VudHJ5IjoiNzI0IiwiRHNfQW1vdW50IjoiMTIzNCIsIkRzX0N1cnJlbmN5IjoiOTc4IiwiRHNfT3JkZXIiOiIzMjExMiIsIkRzX01lcmNoYW50Q29kZSI6IjMyNzIzND\n" +
                    "Y4OCIsIkRzX1Rlcm1pbmFsIjoiMDAyIiwiRHNfUmVzcG9uc2UiOiIwMDAwIiwiRHNfTWVyY2hhbnREYXRhIjoiIiwiRHNfVHJhbnNhY3Rpb25UeXBlIjoiMCIsIkRzX0NvbnN1bWVyTGFuZ3VhZ2UiOiIyIiwiRHNfQXV0aG\n" +
                    "9yaXNhdGlvbkNvZGUiOiIxNDAwODgiLCJEc19DYXJkX0JyYW5kIjoiMSJ9";
            String decodedParams=sha256.decodeMerchantParameters(resposneParams);

            System.out.println("decodedParams-----"+decodedParams);*//*
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String DS_MERCHANT_TERMINAL="";
        if("Y".equalsIgnoreCase(is3dSupported)){
            DS_MERCHANT_TERMINAL="001";
        }else {
            DS_MERCHANT_TERMINAL="002";
        }
        boolean isTest=gatewayAccount.isTest();

        String Url="";
        if(isTest){
            Url=RB.getString("TEST_URL");
        }else {
            Url=RB.getString("LIVE_URL");
        }

        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("URL_OK");
            transactionLogger.error("from RB----"+termUrl);
        }
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        SabadellUtils sabadellUtils= new SabadellUtils();
        SabadellUtils sabadellUtilsLog= new SabadellUtils();
        try
        {
            sabadellUtilsLog.setParameter("DS_MERCHANT_AMOUNT", ""+sabadellUtilsLog.getAmount(commTransactionDetailsVO.getAmount())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_ORDER", ""+trackingID+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_MERCHANTCODE", ""+gatewayAccount.getMerchantId()+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_CURRENCY", ""+ CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "0"); //0 stands for sale
            sabadellUtilsLog.setParameter("DS_MERCHANT_TERMINAL", ""+DS_MERCHANT_TERMINAL+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_MERCHANTURL", ""+RB.getString("MERCHANT_URL")+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_URLOK", ""+termUrl+trackingID+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_URLKO", ""+termUrl+trackingID+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_PAN", ""+functions.maskingPan(commCardDetailsVO.getCardNum())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_EXPIRYDATE", ""+functions.maskingNumber(commCardDetailsVO.getExpYear().substring(2))+""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_CVV2", ""+functions.maskingNumber(commCardDetailsVO.getcVV())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            sabadellUtils.setParameter("DS_MERCHANT_AMOUNT", ""+sabadellUtils.getAmount(commTransactionDetailsVO.getAmount())+"");
            sabadellUtils.setParameter("DS_MERCHANT_ORDER", ""+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTCODE", ""+gatewayAccount.getMerchantId()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CURRENCY", ""+ CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"");
            sabadellUtils.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "0"); //0 stands for sale
            sabadellUtils.setParameter("DS_MERCHANT_TERMINAL", ""+DS_MERCHANT_TERMINAL+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTURL", ""+RB.getString("MERCHANT_URL")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLOK", ""+termUrl+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLKO", ""+termUrl+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_PAN", ""+commCardDetailsVO.getCardNum()+"");
            sabadellUtils.setParameter("DS_MERCHANT_EXPIRYDATE", ""+commCardDetailsVO.getExpYear().substring(2)+""+commCardDetailsVO.getExpMonth()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CVV2", ""+commCardDetailsVO.getcVV()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            String params = sabadellUtils.createMerchantParameters();
            String paramsLog = sabadellUtilsLog.createMerchantParameters();
            transactionLogger.error("params--"+trackingID+"---"+paramsLog);

            String key=gatewayAccount.getFRAUD_FTP_USERNAME();
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();
            String sig ="";
            String signature="";
            sig = sabadellUtils.createMerchantSignature(key);
            transactionLogger.error("sig------" + sig);
          //  signature=sig.replaceAll("\\+","%2B");
            signature=sig;

            transactionLogger.error("Signature-----"+signature);

            String request="Ds_SignatureVersion="+HMAC_SHA_Version+"" +
                    "&Ds_MerchantParameters="+params+
                    "&Ds_Signature="+signature+"";
            transactionLogger.error("request-----" + request);
            String response="";
            transactionLogger.error("----inside 3d----"+is3dSupported);
            if("Y".equalsIgnoreCase(is3dSupported)){
                transactionLogger.error("----inside 3d----"+is3dSupported);
                Map filed= new HashMap();
                filed.put("Url",""+Url+"");
                filed.put("Ds_SignatureVersion",""+HMAC_SHA_Version+"");
                filed.put("Ds_MerchantParameters",""+params+"");
                filed.put("Ds_Signature",""+signature+"");

                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setRemark("3D Authentication Pending");
                commResponseVO.setDescription("3D Authentication Pending");
                commResponseVO.setRequestMap(filed);

            }else {
                if(isTest){
                    response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), request);
                }else {
                    response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), request);
                }

                transactionLogger.debug("response--"+trackingID+"---"+response);
                Thread.sleep(Long.parseLong(RB.getString("THREAD_SLEEP_TIME")));
                HashMap hashMap=sabadellUtils.getPreviousTransactionDeatils(trackingID);

                String dbstatus="";
                String transaType="";
                String date="";
                String status="";
                String remark="";
                String description="";
                String billgDes="";


                if(hashMap!=null && hashMap.size()>0){
                    dbstatus= (String) hashMap.get("status");
                    transaType= (String) hashMap.get("transaType");
                    date=(String) hashMap.get("date");

                    transactionLogger.debug("status----"+dbstatus+"---transaType--"+transaType+"----date----"+date);
                }
                if("success".equalsIgnoreCase(dbstatus) && "0".equalsIgnoreCase(transaType)){
                    status=dbstatus;
                    remark="Transaction Successful";
                    description="Approved";
                    billgDes=gatewayAccount.getDisplayName();
                }else {
                    status="fail";
                    remark="Transaction Fail";
                    description="Failed";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(remark);
                commResponseVO.setDescription(description);
                commResponseVO.setDescriptor(billgDes);
                commResponseVO.setBankTransactionDate(URLDecoder.decode(date));
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        Functions functions = new Functions();
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String DS_MERCHANT_TERMINAL="";
        if("Y".equalsIgnoreCase(is3dSupported)){
            DS_MERCHANT_TERMINAL="001";
        }else {
            DS_MERCHANT_TERMINAL="002";
        }
        boolean isTest=gatewayAccount.isTest();

        String Url="";
        if(isTest){
            Url=RB.getString("TEST_URL");
        }else {
            Url=RB.getString("LIVE_URL");
        }

        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("URL_OK");
            transactionLogger.error("from RB----"+termUrl);
        }

        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        SabadellUtils sabadellUtils= new SabadellUtils();
        SabadellUtils sabadellUtilsLog= new SabadellUtils();
        try
        {
            sabadellUtilsLog.setParameter("DS_MERCHANT_AMOUNT", ""+sabadellUtilsLog.getAmount(commTransactionDetailsVO.getAmount())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_ORDER", ""+trackingID+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_MERCHANTCODE", ""+gatewayAccount.getMerchantId()+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_CURRENCY", ""+ CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "1"); //1 stands for auth
            sabadellUtilsLog.setParameter("DS_MERCHANT_TERMINAL", ""+DS_MERCHANT_TERMINAL+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_MERCHANTURL", ""+RB.getString("MERCHANT_URL")+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_URLOK", ""+termUrl+trackingID+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_URLKO", ""+termUrl+trackingID+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_PAN", ""+functions.maskingPan(commCardDetailsVO.getCardNum())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_EXPIRYDATE", ""+functions.maskingNumber(commCardDetailsVO.getExpYear().substring(2))+""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_CVV2", ""+functions.maskingNumber(commCardDetailsVO.getcVV())+"");
            sabadellUtilsLog.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            sabadellUtils.setParameter("DS_MERCHANT_AMOUNT", ""+sabadellUtils.getAmount(commTransactionDetailsVO.getAmount())+"");
            sabadellUtils.setParameter("DS_MERCHANT_ORDER", ""+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTCODE", ""+gatewayAccount.getMerchantId()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CURRENCY", ""+ CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"");
            sabadellUtils.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "1"); //1 stands for auth
            sabadellUtils.setParameter("DS_MERCHANT_TERMINAL", ""+DS_MERCHANT_TERMINAL+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTURL", ""+RB.getString("MERCHANT_URL")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLOK", ""+termUrl+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLKO", ""+termUrl+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_PAN", ""+commCardDetailsVO.getCardNum()+"");
            sabadellUtils.setParameter("DS_MERCHANT_EXPIRYDATE", ""+commCardDetailsVO.getExpYear().substring(2)+""+commCardDetailsVO.getExpMonth()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CVV2", ""+commCardDetailsVO.getcVV()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            String params = sabadellUtils.createMerchantParameters();
            String paramsLog = sabadellUtilsLog.createMerchantParameters();
            transactionLogger.error("params---"+trackingID+"--"+paramsLog);

            String key=gatewayAccount.getFRAUD_FTP_USERNAME();
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();
            String sig ="";
            String signature="";
            sig = sabadellUtils.createMerchantSignature(key);
            transactionLogger.error("sig------"+sig);
           // signature=sig.replaceAll("\\+","%2B");
            signature=sig;

            transactionLogger.error("Signature-----"+signature);

            String request="Ds_SignatureVersion="+HMAC_SHA_Version+"" +
                    "&Ds_MerchantParameters="+params+
                    "&Ds_Signature="+signature+"";
            transactionLogger.error("request-----" + request);
            String response="";

            if("Y".equalsIgnoreCase(is3dSupported)){
                Map filed= new HashMap();
                filed.put("Url",""+Url+"");
                filed.put("Ds_SignatureVersion",""+HMAC_SHA_Version+"");
                filed.put("Ds_MerchantParameters",""+params+"");
                filed.put("Ds_Signature",""+signature+"");

                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setRemark("3D Authentication Pending");
                commResponseVO.setDescription("3D Authentication Pending");
                commResponseVO.setRequestMap(filed);

            }else {
                if(isTest){
                    response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), request);
                }else {
                    response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), request);
                }
                transactionLogger.error("response---" + trackingID+"--"+response);
                Thread.sleep(Long.parseLong(RB.getString("THREAD_SLEEP_TIME")));
                HashMap hashMap=sabadellUtils.getPreviousTransactionDeatils(trackingID);

                String dbstatus="";
                String transaType="";
                String date="";
                String status="";
                String remark="";
                String description="";
                String billgDes="";


                if(hashMap!=null && hashMap.size()>0){
                    dbstatus= (String) hashMap.get("status");
                    transaType= (String) hashMap.get("transaType");
                    date=(String) hashMap.get("date");

                    transactionLogger.debug("status----"+dbstatus+"---transaType--"+transaType+"----date----"+date);
                }
                if("success".equalsIgnoreCase(dbstatus) && "1".equalsIgnoreCase(transaType)){
                    status=dbstatus;
                    remark="Transaction Successful";
                    description="Approved";
                    billgDes=gatewayAccount.getDisplayName();
                }else {
                    status="fail";
                    remark="Transaction Fail";
                    description="Failed";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(remark);
                commResponseVO.setDescription(description);
                commResponseVO.setDescriptor(billgDes);
                commResponseVO.setBankTransactionDate(URLDecoder.decode(date));
                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }catch (Exception e){
            transactionLogger.error("Exception------",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);

        String DS_MERCHANT_TERMINAL="";
        String currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        String terminal=commTransactionDetailsVO.getTransactionType();
        if(terminal.contains("3D")){
            DS_MERCHANT_TERMINAL="001";
        }else {
            DS_MERCHANT_TERMINAL="002";
        }

        boolean isTest=gatewayAccount.isTest();
        SabadellUtils sabadellUtils= new SabadellUtils();
        try
        {
            sabadellUtils.setParameter("DS_MERCHANT_AMOUNT", ""+sabadellUtils.getAmount(commTransactionDetailsVO.getAmount())+"");
            sabadellUtils.setParameter("DS_MERCHANT_ORDER", ""+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTCODE", ""+gatewayAccount.getMerchantId()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CURRENCY", ""+ CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"");
            sabadellUtils.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "2"); //2 stands for capture
            sabadellUtils.setParameter("DS_MERCHANT_TERMINAL", ""+DS_MERCHANT_TERMINAL+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTURL", ""+RB.getString("MERCHANT_URL")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLOK", ""+RB.getString("URL_OK")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLKO", ""+RB.getString("URL_OK")+"");
            sabadellUtils.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            String params = sabadellUtils.createMerchantParameters();
            transactionLogger.error("params-----"+params);

            String key=gatewayAccount.getFRAUD_FTP_USERNAME();
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();
            String sig ="";
            String signature="";
            sig = sabadellUtils.createMerchantSignature(key);
            transactionLogger.error("sig------"+sig);
          //  signature=sig.replaceAll("\\+","%2B");
            signature=sig;
            transactionLogger.error("Signature-----"+signature);

            String request="Ds_SignatureVersion="+HMAC_SHA_Version+"" +
                    "&Ds_MerchantParameters="+params+
                    "&Ds_Signature="+signature+"";
            transactionLogger.error("request-----" + request);

            String response="";
            if(isTest){
                response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), request);
            }else {
                response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), request);
            }
            transactionLogger.debug("response-----"+response);
            Thread.sleep(Long.parseLong(RB.getString("THREAD_SLEEP_TIME")));
            HashMap hashMap=sabadellUtils.getPreviousTransactionDeatils(trackingID);

            String dbstatus="";
            String transaType="";
            String date="";
            String status="";
            String remark="";
            String description="";
            String billgDes="";


            if(hashMap!=null && hashMap.size()>0){
                dbstatus= (String) hashMap.get("status");
                transaType= (String) hashMap.get("transaType");
                date=(String) hashMap.get("date");

                transactionLogger.debug("status----"+dbstatus+"---transaType--"+transaType+"----date----"+date);
            }
            if("success".equalsIgnoreCase(dbstatus) && "2".equalsIgnoreCase(transaType)){
                status=dbstatus;
                remark="Transaction Successful";
                description="Approved";
                billgDes=gatewayAccount.getDisplayName();
            }else {
                status="fail";
                remark="Transaction Fail";
                description="Failed";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescription(description);
            commResponseVO.setDescriptor(billgDes);
            commResponseVO.setBankTransactionDate(URLDecoder.decode(date));
            commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            commResponseVO.setCurrency(currency);


        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        Functions functions=new Functions();
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        String DS_MERCHANT_TERMINAL="";
        String terminal=commTransactionDetailsVO.getTransactionType();
        if(terminal.contains("3D")){
            DS_MERCHANT_TERMINAL="001";
        }else {
            DS_MERCHANT_TERMINAL="002";
        }
        boolean isTest=gatewayAccount.isTest();
        String currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }

        SabadellUtils sabadellUtils= new SabadellUtils();
        try
        {
            sabadellUtils.setParameter("DS_MERCHANT_AMOUNT", ""+sabadellUtils.getAmount(commTransactionDetailsVO.getAmount())+"");
            sabadellUtils.setParameter("DS_MERCHANT_ORDER", ""+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTCODE", ""+gatewayAccount.getMerchantId()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CURRENCY", ""+ CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"");
            sabadellUtils.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "3"); //3 stands for refund
            sabadellUtils.setParameter("DS_MERCHANT_TERMINAL", ""+DS_MERCHANT_TERMINAL+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTURL", ""+RB.getString("MERCHANT_URL")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLOK", ""+RB.getString("URL_OK")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLKO", ""+RB.getString("URL_OK")+"");
            sabadellUtils.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            String params = sabadellUtils.createMerchantParameters();
            transactionLogger.error("params-----"+params);

            String key=gatewayAccount.getFRAUD_FTP_USERNAME();
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();
            String sig ="";
            String signature="";
            sig = sabadellUtils.createMerchantSignature(key);
            transactionLogger.error("sig------"+sig);
            signature=sig;
            //signature=sig.replaceAll("\\+","%2B");
            transactionLogger.error("Signature-----"+signature);


            String request="Ds_SignatureVersion="+HMAC_SHA_Version+"" +
                    "&Ds_MerchantParameters="+params+
                    "&Ds_Signature="+signature+"";
            transactionLogger.error("request-----" + request);

            String response="";
            if(isTest){
                response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), request);
            }else {
                response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), request);
            }
            transactionLogger.debug("response-----"+response);
            Thread.sleep(Long.parseLong(RB.getString("THREAD_SLEEP_TIME")));
            HashMap hashMap=sabadellUtils.getPreviousTransactionDeatils(trackingID);

            String dbstatus="";
            String transaType="";
            String date="";
            String status="";
            String remark="";
            String description="";
            String billgDes="";


            if(hashMap!=null && hashMap.size()>0){
                dbstatus= (String) hashMap.get("status");
                transaType= (String) hashMap.get("transaType");
                date=(String) hashMap.get("date");

                transactionLogger.debug("status----"+dbstatus+"---transaType--"+transaType+"----date----"+date);
            }
            if("success".equalsIgnoreCase(dbstatus) && "3".equalsIgnoreCase(transaType)){
                status=dbstatus;
                remark="Transaction Successful";
                description="Approved";
                billgDes=gatewayAccount.getDisplayName();
            }else {
                status="fail";
                remark="Transaction Fail";
                description="Failed";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescription(description);
            commResponseVO.setDescriptor(billgDes);
            commResponseVO.setBankTransactionDate(URLDecoder.decode(date));
            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            commResponseVO.setCurrency(currency);

        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        Functions functions=new Functions();
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        String DS_MERCHANT_TERMINAL="";
        String terminal=commTransactionDetailsVO.getTransactionType();
        if(terminal.contains("3D")){
            DS_MERCHANT_TERMINAL="001";
        }else {
            DS_MERCHANT_TERMINAL="002";
        }
        boolean isTest=gatewayAccount.isTest();

        SabadellUtils sabadellUtils= new SabadellUtils();
        try
        {
            sabadellUtils.setParameter("DS_MERCHANT_AMOUNT", ""+sabadellUtils.getAmount(commTransactionDetailsVO.getAmount())+"");
            sabadellUtils.setParameter("DS_MERCHANT_ORDER", ""+trackingID+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTCODE", ""+gatewayAccount.getMerchantId()+"");
            sabadellUtils.setParameter("DS_MERCHANT_CURRENCY", ""+ CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"");
            sabadellUtils.setParameter("DS_MERCHANT_TRANSACTIONTYPE", "9"); // 9 stands for void
            sabadellUtils.setParameter("DS_MERCHANT_TERMINAL", ""+DS_MERCHANT_TERMINAL+"");
            sabadellUtils.setParameter("DS_MERCHANT_MERCHANTURL", ""+RB.getString("MERCHANT_URL")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLOK", ""+RB.getString("URL_OK")+"");
            sabadellUtils.setParameter("DS_MERCHANT_URLKO", ""+RB.getString("URL_OK")+"");
            sabadellUtils.setParameter("DS_MERCHANT_CONSUMERLANGUAGE", "002");

            String params = sabadellUtils.createMerchantParameters();
            transactionLogger.error("params-----"+params);

            String key=gatewayAccount.getFRAUD_FTP_USERNAME();
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();
            String sig ="";
            String signature="";
            sig = sabadellUtils.createMerchantSignature(key);
            transactionLogger.error("sig------"+sig);
           // signature=sig.replaceAll("\\+","%2B");
            signature=sig;
            transactionLogger.error("Signature-----"+signature);

            String request="Ds_SignatureVersion="+HMAC_SHA_Version+"" +
                    "&Ds_MerchantParameters="+params+
                    "&Ds_Signature="+signature+"";
            transactionLogger.error("request-----" + request);

            String response="";
            if(isTest){
                response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), request);
            }else {
                response= SabadellUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), request);
            }

            transactionLogger.debug("response-----"+response);
            Thread.sleep(Long.parseLong(RB.getString("THREAD_SLEEP_TIME")));
            HashMap hashMap=sabadellUtils.getPreviousTransactionDeatils(trackingID);

            String dbstatus="";
            String transaType="";
            String date="";
            String status="";
            String remark="";
            String description="";
            String billgDes="";


            if(hashMap!=null && hashMap.size()>0){
                dbstatus= (String) hashMap.get("status");
                transaType= (String) hashMap.get("transaType");
                date=(String) hashMap.get("date");

                transactionLogger.debug("status----"+dbstatus+"---transaType--"+transaType+"----date----"+date);
            }
            if("success".equalsIgnoreCase(dbstatus) && "9".equalsIgnoreCase(transaType)){
                status=dbstatus;
                remark="Transaction Successful";
                description="Approved";
                billgDes=gatewayAccount.getDisplayName();
            }else {
                status="fail";
                remark="Transaction Fail";
                description="Failed";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescription(description);
            commResponseVO.setDescriptor(billgDes);
            commResponseVO.setBankTransactionDate(URLDecoder.decode(date));
            commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());

        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

}