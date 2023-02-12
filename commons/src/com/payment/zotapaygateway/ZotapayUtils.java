package com.payment.zotapaygateway;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Balaji on 23-Jan-20.
 */
public class ZotapayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ZotaPayGateway.class.getName());
    public String getSignature(String endpointID, String merchantOrderID, String orderAmount, String customerEmail,String accountNo, String MerchantSecretKey)
    {

        String input = endpointID + merchantOrderID + orderAmount + customerEmail + accountNo + MerchantSecretKey;
        transactionLogger.error("signature appended string----" + input);
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("Exception -----",e);
        }
//        return md.digest(input.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, md.digest(input.getBytes(StandardCharsets.UTF_8)));
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }
        transactionLogger.error("signature-------"+hexString.toString());
        return hexString.toString();
    }

    public String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
//            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("Exception -----", he);
        }
        catch (IOException io)
        {
            transactionLogger.error("Exception -----", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside PayBoutiqueUtils getCommRequestFromUtils");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
//        addressDetailsVO.setEmail(commonValidatorVO.getCustEmail());
        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());

        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
    public String getRedirectForm(CommResponseVO response3D)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\">\n");
        html.append("</form>");
        return html.toString();
    }
    public static String doGetHTTPSURLConnectionClient(String strURL) throws PZTechnicalViolationException
    {
        String result = "";
        GetMethod get = new GetMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            httpClient.executeMethod(get);
            String response = new String(get.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException-->", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException-->", io);
        }
        finally
        {
            get.releaseConnection();
        }
        return result;
    }
}
