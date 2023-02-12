package com.payment.tojika;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 14-03-2019.
 */

public class TojikaPaymentGateway  extends AbstractPaymentGateway
{
    public static final  String GATEWAY_TYPE="tojika";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Tojika");
    private static  TransactionLogger transactionLogger = new TransactionLogger(TojikaPaymentGateway.class.getName());
    private static Logger log = new Logger(TojikaPaymentGateway.class.getName());
    public TojikaPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    /*public static void main(String[] args) throws UnsupportedEncodingException
    {
         String secretKey="TojikaPaymentGatewayIntegeration";
        String req =
                "vendor_id="+ URLEncoder.encode("ap.3c8bf4b5-0942-42e2-9", "UTF-8")+
                         "&message=" +URLEncoder.encode("","UTF-8")+
                        "&amount="+URLEncoder.encode("40.00","UTF-8")+
                        "&currency="+URLEncoder.encode("ZAR","UTF-8")+
                        "&txid="+URLEncoder.encode("16march201922","UTF-8")+
                        // "&appuser="+URLEncoder.encode("appuser","UTF-8")+
                        "&countrycode="+URLEncoder.encode("ZA","UTF-8")+
                        "&isTest=true"+//+URLEncoder.encode("true","UTF-8")+
                        "&fail="+URLEncoder.encode("http://localhost:8081/transaction/TojikaFrontEndServlet?trackingId=16march201922&status=fail","UTF-8")+
                       // "&fail="+URLEncoder.encode("https://<hostname>/transaction/CommonBackEndServlet","UTF-8")+
                        "&success="+URLEncoder.encode("http://localhost:8081/transaction/TojikaFrontEndServlet?trackingId=16march201922&status=success","UTF-8")+
                       // "&success="+URLEncoder.encode("https://<hostname>/transaction/CommonBackEndServlet","UTF-8")+
                        //"&firstname="+URLEncoder.encode("jeeten","UTF-8")+
                        //"&surname="+URLEncoder.encode("Gupta","UTF-8")+
                        "&address="+URLEncoder.encode("Malad","UTF-8")+
                        "&city="+URLEncoder.encode("Mumbai","UTF-8")+
                        "&postcode="+URLEncoder.encode("400064","UTF-8")+
                        "&email="+URLEncoder.encode("<emailaddress>","UTF-8")+
                        "&jwt="+TojikaUtils.getAuthTokenTojika("16march201922", "ap.3c8bf4b5-0942-42e2-9", "TojikaPaymentGatewayIntegeration");
        //"&jwt=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhcC4zYzhiZjRiNS0wOTQyLTQyZTItOSIsImp0aSI6IjIwMDAwIn0.i4HbEQXtYKGwj0Q9u5aVncKqQYxjuCOfWiMMr9Av-bs";
        // String  token =req+"&jwt=";
        //System.out.println("req is -------"+req);
        System.out.println("req url----"+"http://sandbox.trustpay.biz/TrustPayWebClient/Transact?"+req);
    }*/

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.debug("Inside Process Sale------");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String secretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String vendorId=gatewayAccount.getMerchantId();
        String amount=transactionDetailsVO.getAmount();
        String currency=transactionDetailsVO.getCurrency();
        String countryCode=commAddressDetailsVO.getCountry();
        boolean isTest=gatewayAccount.isTest();
        String firstName=commAddressDetailsVO.getFirstname();
        String surName=commAddressDetailsVO.getLastname();
        String address=commAddressDetailsVO.getStreet();
        String city=commAddressDetailsVO.getCity();
        String postCode=commAddressDetailsVO.getZipCode();
        String email=commAddressDetailsVO.getEmail();
        String orderMessage="";
        if(functions.isValueNull(transactionDetailsVO.getOrderDesc()))
        {
            orderMessage=transactionDetailsVO.getOrderDesc();
        }
        String jwt=TojikaUtils.getAuthTokenTojika(trackingID,vendorId,secretKey);

        String termUrl = "";
        transactionLogger.debug("---host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.debug("---from host url---" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.debug("---from Properties----" + termUrl);
        }
        String url="";
        if(isTest)
        {
            url=RB.getString("TEST_SALE_URL");
        }
        else
        {
            url=RB.getString("LIVE_SALE_URL");
        }
        transactionLogger.error("url-----" + url);

        try
        {
            Map<String, String> fields = new HashMap<String, String>();
            fields.put("vendor_id", vendorId);
            fields.put("message", orderMessage);
            fields.put("amount", amount);
            fields.put("currency", currency);
            fields.put("txid", trackingID);
            fields.put("countrycode", countryCode);
            fields.put("isTest", String.valueOf(isTest));
            fields.put("fail", termUrl+trackingID+"&status=fail");
            fields.put("success", termUrl+trackingID+"&status=success");
            fields.put("firstname", firstName);
            fields.put("surname", surName);
            fields.put("address", address);
            fields.put("city", city);
            fields.put("postcode", postCode);
            fields.put("email", email);
            fields.put("jwt", jwt);

            if (fields!=null)
            {
                for (Map.Entry<String, String> entry : fields.entrySet())
                {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    transactionLogger.debug("key line :::::" + key);
                    transactionLogger.debug("value line :::::" + value);
                }
            }
            transactionLogger.debug("after request -----");
            commResponseVO.setStatus("pending");
            commResponseVO.setRequestMap(fields);
            commResponseVO.setRedirectUrl(url);
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("processAutoRedirect in TojikaPaymentGateway ----");
        String html="";
        TojikaUtils tojikaUtils=new TojikaUtils();
        CommRequestVO commRequestVO = null;
        Comm3DResponseVO transRespDetails = null;
        commRequestVO = TojikaUtils.getTojikaHPPRequest(commonValidatorVO);

        try
        {
            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                html = tojikaUtils.getRedirectForm(commonValidatorVO.getTrackingid(),transRespDetails);
                transactionLogger.debug("Html in processAutoRedirect -------"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in TojikaPaymentGateway ---",e);
        }

        return html;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
