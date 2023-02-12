package com.payment.wealthpay;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.nestpay.NestPayPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Krishna on 24-Jul-21.
 */
public class WealthPayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(WealthPayUtils.class.getName());

    public static String hashMac(String sha)
    {
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            byte[] hash=messageDigest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

        }
        catch (NoSuchAlgorithmException e)
        {
           transactionLogger.error("NoSuchAlgorithmException============>"+e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException============>"+e);
        }
        return hexString.toString();
    }

    public static String doHttpPostConnection(String url,String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException============>" + e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException============>" + e);
        }
        return result;
    }

    public static String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("eBanking".equalsIgnoreCase(paymentMode))
            payMode = "1";
        else if("PromptpayQR".equalsIgnoreCase(paymentMode))
            payMode = "3";
        else if("MomoPay".equalsIgnoreCase(paymentMode))
            payMode = "10";
        else if("ZaloPay".equalsIgnoreCase(paymentMode))
            payMode = "11";
        else if("VNQRPay".equalsIgnoreCase(paymentMode))
            payMode = "12";
        else if("VAOffline".equalsIgnoreCase(paymentMode))
            payMode = "20";

        return payMode;
    }

    public CommRequestVO getWealthRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionLogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

}
