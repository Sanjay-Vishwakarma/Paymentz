package com.payment.curo;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Admin on 1/6/2020.
 */
public class CuroUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(CuroUtils.class.getName());
    public static String encodeBase64(String value)
    {
        return new String(Base64.encodeBase64(value.getBytes()));
    }
    static Hashtable<String,String> errorTable=new Hashtable();
    static {
        errorTable.put("0","Transaction in progress");
        errorTable.put("100","Authorization successful");
        errorTable.put("150","	3D secure status 'Y' (yes), waiting for 3D secure authentication");
        errorTable.put("152","3D secure status 'N' (no)");
        errorTable.put("154","3D secure status 'U' (unknown)");
        errorTable.put("156","3D secure status 'E' (error)");
        errorTable.put("200","Transaction successful");
        errorTable.put("210","Recurring transaction successful");
        errorTable.put("300","Transaction failed");
        errorTable.put("301","Transaction failed due to anti fraud system");
        errorTable.put("302","Transaction rejected");
        errorTable.put("308","Transaction was expired");
        errorTable.put("309","Transaction was cancelled");
        errorTable.put("310","Recurring transaction failed");
        errorTable.put("330","Authorization failed");
        errorTable.put("350","Transaction failed, time out for 3D secure authentication");
        errorTable.put("351","Transaction failed, non-3DS transactions are not allowed");
        errorTable.put("352","Transaction failed 3DS verification");
        errorTable.put("370","Wait time expired");
        errorTable.put("399","Problem at remote payment processor");
        errorTable.put("400","Refund to consumer");
        errorTable.put("404","Reversal by system (transaction was never received)");
        errorTable.put("410","Chargeback by consumer");
        errorTable.put("420","Chargeback (2nd attempt)");
        errorTable.put("450","Authorization cancelled");
        errorTable.put("601","Fraud notification received from bank");
        errorTable.put("604","Retrieval notification received from bank");
        errorTable.put("700","Transaction is waiting for user action");
        errorTable.put("701","Waiting for capture");
        errorTable.put("710","Waiting for confirmation recurring");
        errorTable.put("750","Waiting for confirmation (from external party like a bank)");
    }
    public static String doHttpPostConnection(String url,String request,String hash)
    {
        String result="";
        PostMethod post=new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic " + hash);
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response=new String(post.getResponseBody());
            result=response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("CuroUtils HttpException e---->",e);
        }
        catch (IOException e)
        {
            transactionLogger.error("CuroUtils IOException e---->",e);
        }
        return result;
    }
    public static String getPaymentMethodEndPoint(String cardType)
    {
        String paymentMethod="";
        if("Sofort".equalsIgnoreCase(cardType))
            paymentMethod="sofortbanking/";
        else if("Ideal".equalsIgnoreCase(cardType))
            paymentMethod="ideal/issuers/";
        else if("PAYSAFECARD".equalsIgnoreCase(cardType))
            paymentMethod="paysafecard";
        else if("BCPAYGATE".equalsIgnoreCase(cardType))
            paymentMethod="bitcoin";

        return paymentMethod;
    }
    public static String getRedirectForm(String trackingId, CommResponseVO response3D)
    {
        transactionLogger.error("redirect url---" + response3D.getRedirectUrl());
        transactionLogger.error("trackingid inside Curo utils-------"+trackingId);
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\">\n");
        //  html.append("<input type=\"hidden\" name=\"transactionID\" value=\"" + response3D.getTransactionId() + "\">");
        html.append("</form>");

        transactionLogger.error("form----"+html.toString());
        return html.toString();
    }
    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside CuroUtils -----");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommAddressDetailsVO addressDetailsVO=new CommAddressDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setSex(commonValidatorVO.getAddressDetailsVO().getSex());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setCardType(commonValidatorVO.getPaymentBrand());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);

        return commRequestVO;
    }
    public static String getErrorMessage(String errorCode)
    {
        return errorTable.get(errorCode);
    }
}
