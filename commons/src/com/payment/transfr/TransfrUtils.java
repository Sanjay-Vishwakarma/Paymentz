package com.payment.transfr;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by admin on 27-Apr-22.
 */
public class TransfrUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(TransfrUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String request, String authToken) throws PZTechnicalViolationException
    {

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
//            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Content-Type", "application/json");
            post.setRequestHeader("Authorization", "Bearer " + authToken);
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException httpException)
        {
            transactionLogger.error("BoltPayUtils HttpException----- ", httpException);

        }
        catch (IOException ioException)
        {
            transactionLogger.error("BoltPayUtils IOException----- ", ioException);
        }
        catch (Exception exception)
        {
            transactionLogger.error("BoltPayUtils Exception----- ", exception);
        }

        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static Boolean updatePaymentId (String trackingid, String paymentid)
    {

        transactionLogger.error("in side updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set paymentid=? where trackingid=?" ;
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,paymentid);
            psUpdateTransaction.setString(2,trackingid);
            transactionLogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException---- ", e);

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError---- ", systemError);
        }
        catch (Exception ex)
        {
            transactionLogger.error("Exception ---- ", ex);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }

    public static String doGetHTTPSURLConnectionClient(String url, String authToken) throws PZTechnicalViolationException
    {
        //transactionlogger.error("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client   = new HttpClient();
        GetMethod method    = new GetMethod(url);
        String result       = "";
        try
        {
            method.setRequestHeader("Authorization", "Bearer " + authToken);
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                //transactionlogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            //transactionlogger.error("Response-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            //transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("MtnUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            //transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("MtnUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            method.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public CommRequestVO getTransfrRequestVO(CommonValidatorVO commonValidatorVO)
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

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
}
