package com.payment.aamarpay;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Admin on 10/17/2021.
 */
public class AamarPayUtils
{
    private static TransactionLogger transactionlogger      = new TransactionLogger(AamarPayUtils.class.getName());

    public String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CARD".equalsIgnoreCase(paymentMode))
            payMode = "CARD";
        if("Mobile Banking".equalsIgnoreCase(paymentMode))
            payMode = "MFS";

        return payMode;
    }
   /* public String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("NAGAD".equalsIgnoreCase(paymentMode))
            payMode = "nagad";
        if("UPAY".equalsIgnoreCase(paymentMode))
            payMode = "UPAY";

        return payMode;
    }*/


    public static String getChecksum(String input) throws NoSuchAlgorithmException {
        String response     = null;
        MessageDigest md    = MessageDigest.getInstance("MD5");
        byte[] array        = md.digest(input.getBytes());

        StringBuffer sb     = new StringBuffer();

        for (int i = 0; i < array.length; ++i)
        {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
        response = sb.toString();

        return response;
    }

    public CommRequestVO getAamarPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        /*addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());*/
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getPaymentBrand());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionlogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public Boolean updateTransaction (String trackingid, String  customerId ){

        transactionlogger.error("in side  updateTransaction----------->"+customerId);
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_common set customerId= ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, customerId);
            psUpdateTransaction.setString(2, trackingid);
            transactionlogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionlogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            if(con!=null){
                Database.closeConnection(con);
            }
        }
        return isUpdate;

    }

    public static String doPostHTTPSURLConnectionClient(String url,String request) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("AamarPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("AamarPayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result.trim();
    }

    public static String doPostQueryParamHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Referer", url);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----" + he);
            PZExceptionHandler.raiseTechnicalViolationException("TigerPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----" + io);
            PZExceptionHandler.raiseTechnicalViolationException("TigerPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }



    public static String doPostQueryHTTPSURLConnectionClient(String url,String token) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Authorization", "Token "+token);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----" + he);
            PZExceptionHandler.raiseTechnicalViolationException("TigerPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----" + io);
            PZExceptionHandler.raiseTechnicalViolationException("TigerPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }


    public HashMap<String,String> getTransactionBankDetails (String trackingid){

        transactionlogger.error("in side  updateTransaction----------->");
        Connection con = null;
        PreparedStatement preparedStatement = null;
        boolean isUpdate=false;
        ResultSet resultSet                                    = null;
        HashMap<String,String> hashMap =  new HashMap<>();
        try
        {

            con                 = Database.getConnection();
            String update       = "Select pod ,podbatch from  transaction_common  where trackingid=?";
            preparedStatement = con.prepareStatement(update.toString());
            preparedStatement.setString(1, trackingid);
            resultSet       = preparedStatement.executeQuery();
            while(resultSet.next()){
                hashMap.put("sysbatchid", resultSet.getString("pod"));
                hashMap.put("bankbatchid", resultSet.getString("podbatch"));
            }
        }

        catch (SQLException e)
        {
            transactionlogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            if(con!=null){
                Database.closeConnection(con);
            }
        }
        return hashMap;

    }



}
