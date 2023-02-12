package com.payment.bitclear;

import com.directi.pg.*;
import com.manager.vo.TerminalVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Objects;
/**
 * Created by Admin on 3/8/2021.
 */
public class BitClearUtils
{

    private static TransactionLogger transactionlogger      = new TransactionLogger(BitClearUtils.class.getName());
    Functions functions                                     = new Functions();
    private final static String HMAC_SHA1                   = "HmacSHA1";


    public static void updateOrderid(String paymentid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, paymentid);
            ps2.setString(2, trackingid);
            transactionlogger.error("payg updateOrderid--->"+ps2);

            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }


    public CommRequestVO getBitClearGRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();
        TerminalVO terminalVO                   = new TerminalVO();
        terminalVO                             = commonValidatorVO.getTerminalVO();
        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCustomerBankId(commonValidatorVO.getProcessorName());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        if(functions.isValueNull(commonValidatorVO.getTerminalVO().getAutoRedirectRequest())){
            commRequestVO.setAutoRedirectFlag(commonValidatorVO.getTerminalVO().getAutoRedirectRequest());
        }

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String doPostHTTPSURLConnectionClient(String url,String request,String base64Credentials) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic "+base64Credentials);

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String doGetHTTPSURLConnectionClient(String url,String base64Credentials) throws PZTechnicalViolationException
    {
        String result   = "";
        GetMethod post = new GetMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic "+base64Credentials);

            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("doGetHTTPSURLConnectionClient HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("doGetHTTPSURLConnectionClient IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }


    public static String doGetHttpConnection(String url,String key)throws PZTechnicalViolationException
    {
        String response         = "";
        HttpClient httpClient   = new HttpClient();
        GetMethod getMethod     = new GetMethod(url);
        try
        {
            getMethod.addRequestHeader("Authorization","Basic " +key);
            httpClient.executeMethod(getMethod);
            response    = new String (getMethod.getResponseBody());
        }
        catch (HttpException he)
        {
            transactionlogger.error("doGetHttpConnection HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java", "doGetHttpConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("doGetHttpConnection IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java","doGetHttpConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            getMethod.releaseConnection();
        }

        if (response == null)
            return "";
        else
            return response;
    }


    private static String generateSignature(String message, String key) throws Exception {
        SecretKeySpec signingKey    = new SecretKeySpec(key.getBytes(), HMAC_SHA1);
        Mac mac                     = Mac.getInstance(HMAC_SHA1);
        mac.init(signingKey);
        byte[] signatureBytes       = mac.doFinal(message.getBytes());
        return toHexStringLowercased(signatureBytes);
    }

    private static boolean verify(String message, String key, String signature) throws Exception {
        return Objects.equals(generateSignature(message, key), signature);
    }

    private static String toHexStringLowercased(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

    public static void updateResponseHashInfo(CommResponseVO commResponseVO, String trackingid) throws PZDBViolationException
    {
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection          = Database.getConnection();
            String updateRecord = "UPDATE transaction_common_details SET responsehashinfo=? WHERE trackingid=?";
            preparedStatement   = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1, commResponseVO.getResponseHashInfo());
            preparedStatement.setString(2, trackingid);
            int i = preparedStatement.executeUpdate();

            transactionlogger.error("updated -------" + i);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("BitClearUtils.java", "updateResponseHashInfo()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("BitClearUtils.java", "updateResponseHashInfo()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public HashMap getResponsehashinfo(String trackingId)
    {
        HashMap hashMap = new HashMap();
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            connection  = Database.getConnection();
            String query    = "SELECT responsehashinfo FROM transaction_common_details WHERE trackingid=? and status='authstarted'";
            p               = connection.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("responsehashinfo")))
                {
                    hashMap.put("responsehashinfo", rs.getString("responsehashinfo"));
                }

            }
            transactionlogger.error("getResponsehashinfo---" + p);
        }
        catch (SystemError systemError)
        {
            transactionlogger.error("System error", systemError);
        }
        catch (SQLException e)
        {
            transactionlogger.error("SQLException", e);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return hashMap;
    }

    public static String doPostHTTPSURLConnectionClient(String url,String base64Credentials) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic "+base64Credentials);

            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("BitClearUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static void updatePaymentId(String paymentid ,String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, paymentid);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection != null){
                Database.closeConnection(connection);
            }
        }
    }
}