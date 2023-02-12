package com.payment.beekash;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.Enum.PZProcessType;
import com.payment.beekash.vos.Refund;
import com.payment.beekash.vos.RefundResponse;
import com.payment.beekash.vos.Request;
import com.payment.beekash.vos.Response;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by NIKET on 12/10/2015.
 */
public class BeekashUtils
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(BeekashUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BeekashUtils.class.getName());


    public static String doPostHttpUrlConnection(String url,String req)
{
    transactionLogger.error("url-----" + url);
    PostMethod postMethod = new PostMethod(url);

    String result="";
    try
    {
        System.setProperty("jsse.enableSNIExtension", "false");
        System.setProperty("https.protocols", "TLSv1.2");
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();
       // System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
       // java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        HttpClient httpClient = new HttpClient();
        postMethod.setRequestHeader("Content-Type", "application/json");
        postMethod.setRequestBody(req);
        httpClient.executeMethod(postMethod);
        result= new String(postMethod.getResponseBody());
    }
    catch (Exception e)
    {
        transactionLogger.error("Exception-----",e);
    }
    finally
    {
        postMethod.releaseConnection();
    }
    return result;
}

    public static Response doPostHTTPSURLConnection(String strURL, Request request) throws PZTechnicalViolationException
    {
        Response response =null;
        try
        {
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
            Client client = Client.create(config);
            WebResource service = client.resource(getBaseURI(strURL));
             response=service.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Response.class, request);

        }
        catch(Exception e)
        {
              PZExceptionHandler.raiseTechnicalViolationException(BeekashUtils.class.getName(),"doPostHTTPSURLConnection()",null,"Common","Exception while creating connection with bank",PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        return response;
    }

    public static RefundResponse doPostHTTPSURLConnectionForRefund(String strURL, Refund refund) throws PZTechnicalViolationException
    {
        RefundResponse response =null;
        try
        {
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
            Client client = Client.create(config);
            WebResource service = client.resource(getBaseURI(strURL));

             response=service.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(RefundResponse.class, refund);

        }
        catch(Exception e)
        {
              log.error("Exception:::",e);
              PZExceptionHandler.raiseTechnicalViolationException(BeekashUtils.class.getName(),"doPostHTTPSURLConnection()",null,"Common","Exception while creating connection with bank",PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        return response;
    }

    /**
     * Converting response to System Related response
     * @param response
     * @return
     */
    public static CommResponseVO convertRequestToSystemResponse(Response response,PZProcessType processType)
    {
        CommResponseVO commResponseVO =new CommResponseVO();
        String status = "success";
        if(response!=null)
        {
            if("0".equals(response.getResultcode()))
            {
                status="success";
            }
            else
            {
                status="fail";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setAmount(response.getAmount());
            commResponseVO.setTransactionId(response.getPaymentordernumber());
            commResponseVO.setRemark(response.getRemark());
            commResponseVO.setErrorCode(response.getResultcode());
            commResponseVO.setDescription(response.getRemark());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(dateFormat.format(date));
            commResponseVO.setDescriptor(response.getBillingDescriptor());
            commResponseVO.setTransactionType(processType.toString());
            //commResponseVO.setIpaddress(response.getIp());
        }
        return commResponseVO;
    }


    public static CommResponseVO convertRequestToSystemRefundResponse(RefundResponse response,PZProcessType processType)
    {
        CommResponseVO commResponseVO =new CommResponseVO();
        String status = "success";
        if(response!=null)
        {
            if("0".equals(response.getResultcode()))
            {
                status="success";
            }
            else
            {
                status="fail";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setAmount(response.getRefundamount());
            commResponseVO.setTransactionId(response.getPaymentorderno());
            commResponseVO.setRemark(response.getRemarks());
            commResponseVO.setErrorCode(response.getResultcode());
            commResponseVO.setDescription(response.getRemarks());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(dateFormat.format(date));
            //commResponseVO.setDescriptor(response.getBillingDescriptor());
            commResponseVO.setTransactionType(processType.toString());
            //commResponseVO.setIpaddress(response.getIp());
        }
        return commResponseVO;
    }
    public static URI getBaseURI(String url)
    {

        return UriBuilder.fromUri(url).build();

    }
}
