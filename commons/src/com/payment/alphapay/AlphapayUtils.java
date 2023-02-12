package com.payment.alphapay;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.manager.vo.TerminalVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Admin on 05-Dec-22.
 */
public class AlphapayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AlphapayUtils.class.getName());

    private static Functions functions = new Functions();

    public CommRequestVO getAlphapayRequestVo(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        TerminalVO terminalVO = new TerminalVO();
        terminalVO = commonValidatorVO.getTerminalVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        commAddressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        commAddressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());

        commTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        commTransactionDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        commTransactionDetailsVO.setCustomerBankId(commonValidatorVO.getProcessorName());
        commTransactionDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());

        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        if (functions.isValueNull(commonValidatorVO.getTerminalVO().getAutoRedirectRequest()))
        {
            commRequestVO.setAutoRedirectFlag(commonValidatorVO.getTerminalVO().getAutoRedirectRequest());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setCardDetailsVO(commCardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }


    public static boolean isJSONValid(String jsonData)
    {
        try
        {
            new JSONObject(jsonData);
        }
        catch (JSONException ex)
        {
            return false;
        }
        return true;
    }

    public static boolean isJSONARRAYValid(String arrayData)
    {
        try
        {
            new JSONArray(arrayData);
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }


    public static void updateMainTableEntry(String transactionId, String remark, String trackingid)
    {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=?,remark=? WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateQuery1);
            preparedStatement.setString(1,transactionId);
            preparedStatement.setString(2,remark);
            preparedStatement.setString(3,trackingid);
            preparedStatement.executeUpdate();
        }

        catch (Exception s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
                Database.closePreparedStatement(preparedStatement);
            }
        }
    }

    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if ("203".equalsIgnoreCase(paymentMode))
            payBrand = "eft";

        if ("205".equalsIgnoreCase(paymentMode))
            payBrand = "vfd";

        return payBrand;


    }

    public static String doPostFormHTTPSURLConnectionClient(String base64Credentials,String request,String url, String trackingid) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {

            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("Authorization", "Basic "+base64Credentials);
            post.setRequestBody(request);
            httpClient.executeMethod(post);

            Header[] requestHeaders =  post.getRequestHeaders();
            transactionLogger.error(trackingid + " request Headers[]:");
            for(Header header1 : requestHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header1.getName() + " : " + header1.getValue());
            }

            Header[] responseHeaders =  post.getResponseHeaders();
            transactionLogger.error(trackingid + " response Headers[]:");
            for(Header header2 : responseHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header2.getName() + " : " + header2.getValue());
            }

            result = new String(post.getResponseBody());
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----" + he);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----" + io);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
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

    public static String doGetHTTPUrlConnection(String requestURL, String AUTHENTICATION, String trackingid)
    {
        String response       = "";
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod   = new GetMethod(requestURL);
        try
        {
            getMethod.addRequestHeader("Authorization", "Basic "+AUTHENTICATION);
            httpClient.executeMethod(getMethod);

            Header[] requestHeaders =  getMethod.getRequestHeaders();
            transactionLogger.error(trackingid + " request Headers[]:");
            for(Header header1 : requestHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header1.getName() + " : " + header1.getValue());
            }

            Header[] responseHeaders =  getMethod.getResponseHeaders();
            transactionLogger.error(trackingid + " response Headers[]:");
            for(Header header2 : responseHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header2.getName() + " : " + header2.getValue());
            }

            response = new String(getMethod.getResponseBody());
            transactionLogger.error(trackingid+ " response code: " + getMethod.getStatusCode());
        }
        catch (Exception he)
        {
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he);
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he.getMessage());
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he.getCause());
        }
        finally
        {
            getMethod.releaseConnection();
        }

        return response;
    }

    public static String doPutFormHTTPSURLConnectionClient(String base64Credentials,String request,String url, String trackingid) throws PZTechnicalViolationException
    {
        String result   = "";
        PutMethod post = new PutMethod(url);
        try
        {
            transactionLogger.error("Inside PUT method in AlphapayUtils -------->");
            transactionLogger.error("Inside PUT method in AlphapayUtils -------->"+base64Credentials);
            transactionLogger.error("Inside PUT method in AlphapayUtils -------->"+request);
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("Authorization", "Basic "+base64Credentials);
            post.setRequestBody(request);
            httpClient.executeMethod(post);

            Header[] requestHeaders =  post.getRequestHeaders();
            transactionLogger.error(trackingid + " request Headers[]:");
            for(Header header1 : requestHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header1.getName() + " : " + header1.getValue());
            }

            Header[] responseHeaders =  post.getResponseHeaders();
            transactionLogger.error(trackingid + " response Headers[]:");
            for(Header header2 : responseHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header2.getName() + " : " + header2.getValue());
            }

            result = new String(post.getResponseBody());
            transactionLogger.error(trackingid +"---> response code "+ post.getStatusCode());

        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----" + he);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----" + io);
            PZExceptionHandler.raiseTechnicalViolationException("TRAXXUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside AlphaUtils getCommRequestFromUtils");
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
        transDetailsVO.setCardType(commonValidatorVO.getCardType());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
}
