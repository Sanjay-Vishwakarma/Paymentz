package com.payment.zhixinfu;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Admin on 10/26/2020.
 */
public class ZhiXinfuUtils
{

    private static TransactionLogger transactionLogger=new TransactionLogger(ZhiXinfuUtils.class.getName());


    public static String doHttpPostConnection(String url, String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        transactionLogger.error(" URL --->" + url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            // post.addRequestHeader("Content-Type", "text/html");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException --->" + e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException --->" + e);
        }
        catch(Exception e)
        {

            transactionLogger.error("Exception --->" + e);
        }

        return result;
    }



    public static String convertmd5(TreeMap<String,String> treeMap,String secretKey) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;
        Functions functions=new Functions();
        if (null == treeMap) return null;
        String signMsgStr="";
        for (Map.Entry<String,String> entry:treeMap.entrySet())
        {
            String key=entry.getKey();
            String value=entry.getValue();
            if(!functions.isValueNull(signMsgStr))
                signMsgStr=key+"="+value;
            else
                signMsgStr+="&"+key+"="+value;
        }
        signMsgStr+=secretKey;

        try
        {

            transactionLogger.error("MD5 combination---" + signMsgStr);

            MessageDigest digest = MessageDigest.getInstance("MD5");

            md5 = getString(digest.digest(signMsgStr.getBytes()));

        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException---"+e);
        }


        return md5;
    }

    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }


    /*public static String hashSignature(String stringaMac) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] in = digest.digest(stringaMac.getBytes());

        String builder = "";
        builder=encoder.encode(in);
        return builder.toString();
    }*/

    public static String getSignature(List list, String privateKey)
    {
        Collections.sort(list);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++)
        {
            sb.append(list.get(i));
            if (i < list.size() - 1)
            {
                sb.append("|");
            }
        }

        String signature = HmacSHA512(sb.toString(), privateKey);
        transactionLogger.error("signature =" + signature);

        return signature;
    }

    public static String HmacSHA512(String message, String key)
    {
        MessageDigest md = null;
        try
        {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);


            byte raw[] = sha512_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));


            String hex = Base64.encode(raw);
            return hex; //step 6
        }
        catch (Exception e)
        {
            transactionLogger.error("exception " + e);
            return null;
        }

    }


    public static CommRequestVO getZhixinfuRequestVO(CommonValidatorVO commonValidatorVO)
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
        transactionLogger.error("CardType---"+commonValidatorVO.getCardType());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }



}
