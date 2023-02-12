package com.payment.paysend;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.Enum.PZProcessType;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by Rihen on 5/8/2019.
 */
public class PaySendUtils
{
    private static TransactionLogger transactionLogger= new TransactionLogger(PaySendUtils.class.getName());
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.paysend");

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException---->",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException---->", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String authorizationToken) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", authorizationToken);
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException -->",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException -->", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public  static String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
        //transactionLogger.error("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.error("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            transactionLogger.error("Response PaySend Utils-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }



    public static String getSignature(List list,String privateKey)
    {
        Collections.sort(list);

        StringBuffer sb= new StringBuffer();
        for(int i=0;i<list.size();i++)
        {
            sb.append(list.get(i));
            if(i<list.size()-1)
            {
                sb.append("|");
            }
        }

        transactionLogger.error("sb ===" + sb.toString());
        String signature = HmacSHA256(sb.toString(), privateKey);
        transactionLogger.error("signature ="+signature);

        return signature;
    }

    public static void verifySignature(String request, String privateKey)
    {
        StringBuffer sb = new StringBuffer();
        String[] words=request.split("&");
        List list = new ArrayList<String>();
        String request_sig = null;
        for(int i=0;i<words.length;i++)
        {
            String[] values = null;
            if(words[i].contains("project"))
            {
                values=words[i].split("project=");
                System.out.println("values 1 ="+values[1]);
                list.add(values[1]);

            }
            else if(words[i].contains("PaRes"))
            {
                values=words[i].split("PaRes=");
                System.out.println("values 2="+values[1]);
                list.add(values[1]);
            }
            else if(words[i].contains("MD"))
            {
                values=words[i].split("MD=");
                System.out.println("values3 ="+values[1]);
                list.add(values[1]);
            }
            else if(words[i].contains("signature"))
            {
                values=words[i].split("signature=");
                System.out.println("values4 ="+values[1]);
                request_sig = values[1];
            }


        }
        Collections.sort(list);
        for(int i=0;i<list.size();i++)
        {
            sb.append(list.get(i));
            if(i<list.size()-1)
            {
                sb.append("|");
            }
        }

        transactionLogger.error("sb ="+sb.toString());
        String signature = HmacSHA256(sb.toString(), privateKey);
        transactionLogger.error("veirfy signature =" + signature);
        transactionLogger.error("request signature=" + request_sig);


        transactionLogger.error(request_sig.equals(signature));


    }

    public static String HmacSHA256(String message,String secret)
    {
        MessageDigest md = null;
        try
        {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);


            byte raw[] = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));

            /*StringBuffer ls_sb=new StringBuffer();
            for(int i=0;i<raw.length;i++)
                ls_sb.append(char2hex(raw[i]));
            System.out.println("=====hex String=="+ls_sb.toString());*/

            String hex = bytesToHex(raw);
            System.out.println("=====hex2 String=="+hex);
            return hex; //step 6
        }
        catch(Exception e)
        {
            transactionLogger.error("exception");
            return null;
        }

    }


    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }

    public static String char2hex(byte x)
    {
        char arr[]={
                '0','1','2','3',
                '4','5','6','7',
                '8','9','A','B',
                'C','D','E','F'
        };

        char c[] = {arr[(x & 0xF0)>>4],arr[x & 0x0F]};
        return (new String(c));
    }

    public static String getTransactionType(String transType){

        if("payment".equalsIgnoreCase(transType))
        {
            transType= PZProcessType.SALE.toString();
        }
        else if("refund".equalsIgnoreCase(transType))
        {
            transType=PZProcessType.REFUND.toString();
        }
        return transType;
    }


    public static CommRequestVO getPaySendRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merchantId = account.getMerchantId();

        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());


        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merchantId);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }



    public static String getPaySendWalletForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO transRespDetails,String projectID)
    {
        transactionLogger.error("3d page displayed....." + transRespDetails.getRedirectUrl());

        String form="<form name=\"launch3D\" method=\"GET\" action=\""+transRespDetails.getRedirectUrl()+"\">"+
                "<input type=\"hidden\" name=\"project\" value=\""+projectID+"\">"+
                "<input type=\"hidden\" name=\"amount\" value=\""+commonValidatorVO.getTransDetailsVO().getAmount()+"\">"+
                "<input type=\"hidden\" name=\"currency\" value=\""+commonValidatorVO.getTransDetailsVO().getCurrency()+"\">"+
                "<input type=\"hidden\" name=\"success_url\" value=\""+RB.getString("SUCCESS_URL")+"\">"+
                "<input type=\"hidden\" name=\"fail_url\" value=\""+RB.getString("FAIL_URL")+ "\">"+
                "<input type=\"hidden\" name=\"first_name\" value=\""+commonValidatorVO.getAddressDetailsVO().getFirstname()+ "\">"+
                "<input type=\"hidden\" name=\"last_name\" value=\""+commonValidatorVO.getAddressDetailsVO().getLastname()+ "\">"+
                "<input type=\"hidden\" name=\"birth_date_full\" value=\""+commonValidatorVO.getAddressDetailsVO().getBirthdate()+ "\">"+
                "<input type=\"hidden\" name=\"address\" value=\""+commonValidatorVO.getAddressDetailsVO().getStreet()+"\">"+
                "<input type=\"hidden\" name=\"email\" value=\""+commonValidatorVO.getAddressDetailsVO().getEmail()+"\">"+
                "<input type=\"hidden\" name=\"description\" value=\""+commonValidatorVO.getTransDetailsVO().getOrderDesc()+"\">"+
                "<input type=\"hidden\" name=\"identifier\" value=\""+commonValidatorVO.getTransDetailsVO().getOrderId()+"\">"+
                "<input type=\"hidden\" name=\"order_id\" value=\""+commonValidatorVO.getTrackingid()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }
}