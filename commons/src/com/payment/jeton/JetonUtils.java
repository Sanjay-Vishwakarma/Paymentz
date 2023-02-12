package com.payment.jeton;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Uday on 8/1/17.
 */
public class JetonUtils
{
    static  TransactionLogger transactionLogger     = new TransactionLogger(JetonUtils.class.getName());
    public static String doPostHTTPSURLConnectionClient(String strURL, String apiKey,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("X-API-KEY",apiKey);
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException :::::::",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException ::::::::",io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnectionClient4(String url,String req,String requestId,String apiKey)throws PZTechnicalViolationException{
        transactionLogger.error("URL:::::"+url);
        String result="";
        PostMethod post= new PostMethod(url);
        try{
            HttpClient httpClient= new HttpClient();

            post.addRequestHeader("REQUEST_ID",requestId);
            post.addRequestHeader("API-KEY",apiKey);
            post.addRequestHeader("Content-Type","application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response= new String(post.getResponseBody());
            result=response;
        } catch (HttpException he){
            transactionLogger.error("HttpException :::::::", he);
        }
        catch (IOException io){
            transactionLogger.error("HttpException :::::::",io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String generateAutoSubmitForm(String url ,CommonValidatorVO commonValidatorVO){
        StringBuilder html=new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append("" + url + "").append("\" method=\"post\">\n");
        html.append("</form>");
        return html.toString();
    }

   public static String Currency_id(GenericRequestVO requestVO)
   {
       String currency_id = "";

       CommRequestVO commRequestVO = (CommRequestVO) requestVO;
       GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
       String currency=transDetailsVO.getCurrency();
       if (currency != null)
       {
           if ("EUR".equals(currency)){
               currency_id = "1";
           }
           else if ("USD".equals(currency)){
               currency_id = "2";
           }
           else if("GBP".equals(currency)){
               currency_id = "3";
           }
           else if("TRY".equals(currency)){
               currency_id = "4";
           }
           else if("NOK".equals(currency)){
               currency_id="5";
           }
           else if("CAD".equals(currency)){
               currency_id="6";
           }
       }
       return currency_id;
   }

    public CommRequestVO getJetonRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO= new CommCardDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        commCardDetailsVO.setVoucherNumber(commonValidatorVO.getCardDetailsVO().getVoucherNumber());
        commCardDetailsVO.setSecurity_Code(commonValidatorVO.getCardDetailsVO().getSecurity_Code());
        commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());

        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCardDetailsVO(commCardDetailsVO);

        return commRequestVO;
    }
}

