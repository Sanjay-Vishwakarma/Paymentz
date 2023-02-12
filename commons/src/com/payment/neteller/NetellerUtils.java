package com.payment.neteller;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.neteller.response.NetellerResponse;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;

/**
 * Created by Sneha on 2/11/2017.
 */
public class NetellerUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(NetellerUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req, String authType, String encodedCredentials) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL-->" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException--" + he.getMessage());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--"+io.getMessage());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public  static String doGetHTTPSURLConnectionClient(String url, String authType, String encodedCredentials) throws PZTechnicalViolationException
    {
        transactionLogger.debug("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/json");
            method.setRequestHeader("Authorization", authType+ " " +encodedCredentials);
            method.setRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            transactionLogger.debug("Response-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            transactionLogger.debug("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String generateAutoSubmitForm(String url)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"" + url+ "").append("\" method=\"post\">\n");
        html.append("</form>");
        return html.toString();
    }

    public static void main(String[] args)
    {
        String jSn = "{\n" +
                "  \"customer\": {\n" +
                "    \"customerId\": \"CUS_64DE83ED-CA21-4672-B636-E85854A41185\",\n" +
                "    \"accountProfile\": {\n" +
                "      \"email\": \"netellertest_bgn@neteller.com\"\n" +
                "    },\n" +
                "    \"verificationLevel\": \"10\"\n" +
                "  },\n" +
                "  \"billingDetail\": {\n" +
                "    \"email\": \"netellertest_bgn@neteller.com\",\n" +
                "    \"firstName\": \"BGNFirstname\",\n" +
                "    \"lastName\": \"BGNLastname\",\n" +
                "    \"address1\": \"address\",\n" +
                "    \"city\": \"city\",\n" +
                "    \"countrySubdivisionCode\": \"ABE\",\n" +
                "    \"country\": \"GB\",\n" +
                "    \"postCode\": \"pcode\"\n" +
                "  },\n" +
                "  \"transaction\": {\n" +
                "    \"merchantRefId\": \"56357\",\n" +
                "    \"amount\": 300,\n" +
                "    \"currency\": \"BGN\",\n" +
                "    \"id\": \"199510154563024\",\n" +
                "    \"transactionType\": \"Member to Merchant Transfer (Order)\",\n" +
                "    \"description\": \"netellertest_bgn@neteller.com to Test pz\",\n" +
                "    \"createDate\": \"2017-11-08T15:22:44Z\",\n" +
                "    \"updateDate\": \"2017-11-08T15:22:44Z\",\n" +
                "    \"status\": \"accepted\",\n" +
                "    \"fees\": \n" +
                "    [\n" +
                "      {\n" +
                "        \"feeType\": \"service_fee\",\n" +
                "        \"feeAmount\": 183,\n" +
                "        \"feeCurrency\": \"BGN\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"links\": \n" +
                "  [\n" +
                "    {\n" +
                "      \"url\": \"https://test.api.neteller.com/v1/payments/199510154563024\",\n" +
                "      \"rel\": \"self\",\n" +
                "      \"method\": \"GET\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try
        {
            JSONObject jsonObject = new JSONObject(jSn);

            //System.out.println(jsonObject.getJSONObject("customer").getJSONObject("accountProfile").getString("email"));

            /*JSONArray lineItems = jsonObject.getJSONArray("links");

            System.out.println("length---"+lineItems.length());
            for (int i = 0; i < lineItems.length(); i++)
            {
                JSONObject rec = lineItems.getJSONObject(i);
                System.out.println("rel---"+rec.getString("rel"));
                System.out.println("url---"+rec.getString("url"));

                if(rec.getString("rel").equals("invoice"))
                {
                    System.out.println("url---"+rec.getString("url"));
                    break;
                }


            }*/

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception----" ,e);
        }

    }

    public NetellerResponse readInquiryJson(String jsonString)
    {
        NetellerResponse netellerResponse = new NetellerResponse();
        try
        {
            JSONObject jsonObject = new JSONObject(jsonString);
            String merchantRefId = jsonObject.getString("merchantRefId");
            String status = jsonObject.getString("status");
            String orderId = jsonObject.getString("orderId");
            String iUrl = "";
            String pStatus = "";



            JSONArray lineItems = jsonObject.getJSONArray("links");
            for (int i = 0; i < lineItems.length(); i++)
            {
                JSONObject rec = lineItems.getJSONObject(i);

                if(rec.getString("rel").equals("payment"))
                {
                    iUrl = rec.getString("url");
                  //  System.out.println("url payment---"+rec.getString("url"));
                    break;
                }
            }
            if("paid".equalsIgnoreCase(status))
            {
                pStatus = "capturesuccess";
            }
            else
            {
                pStatus = "failed";
            }
            netellerResponse.setTransactionId(orderId);
            netellerResponse.setRedirectUrl(iUrl);
            netellerResponse.setStatus(pStatus);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException in readInquiryJson in Neteller---", e);
        }

        return netellerResponse;
    }

    public CommRequestVO getNetellerRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        String paymentType = GatewayAccountService.getPaymentTypes(commonValidatorVO.getPaymentType());
        String cardType = GatewayAccountService.getCardType(commonValidatorVO.getCardType());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setPaymentType(paymentType.toLowerCase()); //value parameter in paymentMethods
        transDetailsVO.setCardType(cardType.toLowerCase()); //type parameter in paymentMethods
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        return commRequestVO;
    }

    public CommRequestVO getNetellerRequestVOForInquiry(String paymentId, String accountId)
    {
        CommRequestVO commRequestVO = null;
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        transDetailsVO.setPreviousTransactionId(paymentId);
        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accountId));
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

    public String getTransactionStatus (String eventType)
    {
        String status = "";

        if("payment_cancelled".equals(eventType))
            status = "reversed";
        else if("payment_declined".equals(eventType))
            status = "cancelled";
        else if("payment_pending".equals(eventType))
            status = "pending";
        else if("payment_succeeded".equals(eventType))
            status = "capturesuccess";

        return status;
    }
}
