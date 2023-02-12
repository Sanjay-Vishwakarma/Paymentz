package com.payment.Alweave;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Admin on 10/28/2020.
 */
public class AlweaveUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(AlweaveUtils.class.getName());
    public static CommRequestVO getAlweaveRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO=new CommDeviceDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCustomerid(commonValidatorVO.getAddressDetailsVO().getCustomerid());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commDeviceDetailsVO.setUser_Agent(commonValidatorVO.getAddressDetailsVO().getRequestedHeader());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setCommDeviceDetailsVO(commDeviceDetailsVO);
        return commRequestVO;
    }
    public static String doHttpPostConnection(String url, String request)
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
            transactionLogger.error("Triple000Utils HttpException --->" , e);
        }
        catch (IOException e)
        {
            transactionLogger.error("Triple000Utils IOException --->" , e);
        }
        catch(Exception e)
        {

            transactionLogger.error("Triple000Utils Exception --->" , e);
        }

        return result;
    }
    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));

        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }
    public static String getJPYAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKRWAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKWDSupportedAmount(String amount)
    {
        transactionLogger.debug("formatting amount for KWD Currency ---")
        ;
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }
}
