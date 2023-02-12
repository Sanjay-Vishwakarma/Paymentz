package com.payment.mtn;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
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
 * Created by admin on 08-Mar-22.
 */
public class MtnUgandaUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(MtnUgandaUtils.class.getName());

    public static String doPostHTTPSURLConnectionAuthToken(String url, String authorization, String key) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(url);
        try
        {
//            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Content-Type", "application/json");
            post.setRequestHeader("Authorization", authorization);
            post.setRequestHeader("Ocp-Apim-Subscription-Key", key);
//            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException httpException)
        {
            transactionLogger.error("MtnUtils HttpException----- ", httpException);
        }
        catch (IOException ioException)
        {
            transactionLogger.error("MtnUtils IOException----- ", ioException);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String request, String authorization, String referenceId, String targetEnvironment, String key, String callbackURL) throws PZTechnicalViolationException
    {

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
//            transactionLogger.error("Authorization ===== " + authorization);
//            transactionLogger.error("X-Reference-Id ===== " + referenceId);
//            transactionLogger.error("X-Target-Environment ===== " + targetEnvironment);
//            transactionLogger.error("Ocp-Apim-Subscription-Key ===== " + key);

//            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Content-Type", "application/json");
            post.setRequestHeader("Authorization", "Bearer " + authorization);
            post.setRequestHeader("X-Reference-Id", referenceId);
//            post.setRequestHeader("X-Callback-Url", callbackURL);
            post.setRequestHeader("X-Target-Environment", targetEnvironment);
            post.setRequestHeader("Ocp-Apim-Subscription-Key", key);
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException httpException)
        {
            transactionLogger.error("AirtelMoneyUtils HttpException----- ", httpException);

        }
        catch (IOException ioException)
        {
            transactionLogger.error("AirtelMoneyUtils IOException----- ", ioException);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doGetHTTPSURLConnectionClient(String url, String targetEnvironment, String key, String authtoken) throws PZTechnicalViolationException
    {
        //transactionlogger.error("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client   = new HttpClient();
        GetMethod method    = new GetMethod(url);
        String result       = "";
        try
        {
            method.setRequestHeader("Content-Type", "application/json");
            method.setRequestHeader("X-Target-Environment", targetEnvironment);
            method.setRequestHeader("Ocp-Apim-Subscription-Key", key);
            method.setRequestHeader("Authorization", "Bearer " + authtoken);

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

    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside MtnUgandaUtils getCommRequestFromUtils");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setTelnocc(genericAddressDetailsVO.getTelnocc());
        addressDetailsVO.setState(genericAddressDetailsVO.getState());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());

        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static Boolean updatePayoutTransaction (String trackingid, String telnocc, String telno, String referenceId){

        transactionLogger.error("in side updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set telnocc = ?, telno = ?, podbatch = ? where trackingid=?" ;
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,telnocc);
            psUpdateTransaction.setString(2,telno);
            psUpdateTransaction.setString(3,referenceId);
            psUpdateTransaction.setString(4,trackingid);
            transactionLogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }
}
