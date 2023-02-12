package com.payment.ippopay;

import com.directi.pg.*;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 10/17/2020.
 */
public class IppoPayUtils
{

    //private static TransactionLogger transactionlogger = new TransactionLogger(IppoPayUtils.class.getName());
    private static IppoPayTransactionLogger transactionlogger = new IppoPayTransactionLogger(IppoPayUtils.class.getName());

    public String doPostHttpUrlConnection(String url ,String request ,String publickey,String secretkey )
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {

            String userPassword = publickey + ":" + secretkey;
            String encodedCredentials = Base64.encode(userPassword.getBytes());

            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization","Basic" + " " + encodedCredentials);
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
            transactionlogger.error("response-->" + result);
            transactionlogger.error("publickey-->" + publickey);
            transactionlogger.error("secretkey-->" + secretkey);
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" , e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->" , e);
        }finally
        {
            post.releaseConnection();
        }


        return result;
    }

    public CommRequestVO getIppoPayRequestVO(CommonValidatorVO commonValidatorVO)
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
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());


        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }

    public Boolean updateTransaction (String trackingid, String paymentid){

        transactionlogger.error("in side  updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set paymentid = ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,paymentid);
            psUpdateTransaction.setString(2,trackingid);
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
            transactionlogger.error("SystemError :::::::----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }

    public String doGetHttpUrlConnection(String url ,String request ,String publickey,String secretkey )
    {
        String result = "";
        GetMethod getMethod = new GetMethod(url);
        try
        {

            String userPassword = publickey + ":" + secretkey;
            String encodedCredentials = Base64.encode(userPassword.getBytes());

            HttpClient httpClient = new HttpClient();
            getMethod.addRequestHeader("Content-Type", "application/json");
            getMethod.addRequestHeader("Authorization","Basic" + " " + encodedCredentials);
           // getMethod.setRequestBody(request);
            httpClient.executeMethod(getMethod);
            String response = new String(getMethod.getResponseBody());
            result = response;
            transactionlogger.error("response-->" + result);
            transactionlogger.error("publickey-->" + publickey);
            transactionlogger.error("secretkey-->" + secretkey);
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" , e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->" , e);
        }finally
        {
            getMethod.releaseConnection();
        }


        return result;
    }

    public String doGetHttpUrlConnection2(String url ,String request ,String publickey,String secretkey )
    {
        String result = "";
        PostMethod post = new PostMethod(url);;
        try
        {

            String userPassword = publickey + ":" + secretkey;
            String encodedCredentials = Base64.encode(userPassword.getBytes());

            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization","Basic" + " " + encodedCredentials);
            // getMethod.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
            transactionlogger.error("response-->" + result);
            transactionlogger.error("publickey-->" + publickey);
            transactionlogger.error("secretkey-->" + secretkey);
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" , e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->" , e);
        }


        return result;
    }

    public String getPaymentid (String trackingid){

        transactionlogger.error("in side  updateTransaction----------->"+trackingid);
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        String paymentid="";
        try
        {
            ResultSet rs = null;
            con= Database.getConnection();
            String select = "SELECT paymentid from transaction_common where trackingid=?";
            psUpdateTransaction = con.prepareStatement(select.toString());
            psUpdateTransaction.setString(1,trackingid);
            transactionlogger.error("transaction common query----"+psUpdateTransaction);
            rs=psUpdateTransaction.executeQuery();

            if (rs.next())
            {
                paymentid= rs.getString("paymentid");
                transactionlogger.error("paymentid----"+paymentid);

            }
        }

        catch (Exception e)
        {
            transactionlogger.error("SQLException----",e);

        }

        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return paymentid;

    }
}
