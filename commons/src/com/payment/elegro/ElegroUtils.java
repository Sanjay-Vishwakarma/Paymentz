package com.payment.elegro;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Admin on 1/11/2019.
 */
public class ElegroUtils
{
    //static ElegroLogger transactionLogger= new ElegroLogger(ElegroUtils.class.getName());
    static TransactionLogger transactionLogger= new TransactionLogger(ElegroUtils.class.getName());
    public CommRequestVO getElegroRequest(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
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

        return commRequestVO;
    }

    public String getAutoSubmitForm(ElegroResponseVO elegroResponseVO ,boolean isTest)
    {
        String script="";
        if(isTest){
            script="<script src='https://widget-stage.elegro.io/checkout/widget.js'></script>";
        }else {
            script="<script src=https://widget.elegro.io/checkout/widget.js></script>";
        }

        String html="<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <style>\n" +
                "        @import url('https://fonts.googleapis.com/css?family=Quicksand');\n" +
                "        body {\n" +
                "            background: gray;\n" +
                "        }\n" +
                "        .details {\n" +
                "            background: #fffeea;\n" +
                "            border: 1px dotted #e8e6d0;\n" +
                "            color: #444;\n" +
                "            font-size: 16px;\n" +
                "            border-radius: 12px 0 12px 0;\n" +
                "            -webpack-border-radius: 12px;\n" +
                "        }\n" +
                "        .details p {\n" +
                "            text-indent: 15px;\n" +
                "        }\n" +
                "        h1,\n" +
                "        h2,\n" +
                "        h3 {\n" +
                "            font-family: Quicksand, sans-serif;\n" +
                "        }\n" +
                "        h1 {\n" +
                "            color: #fff;\n" +
                "            text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.4);\n" +
                "        }\n" +
                "    </style>\n" +
                "    \n" +
                "</head>\n" +
                "<body style=\"font-family: Raleway;\">\n" +
                "\n" +
                "<div class=\"container\">\n" +
                "    <div class=\"row\">\n" +
                "        <div class=\"col-sm-8\">\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"row\">\n" +
                "        <div class=\"col-sm-12\">\n" +
                "            <div class=\"elegro-widget\"></div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "<form method=\"post\" name=\"elegroForm\" action=\"\">\n" +
                "</form>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<script type = \"text/javascript\" src=\"/merchant/transactionCSS/js/jquery.min.js\"></script>\n" +
                ""+script+"" +
                "\n" +
                "<script>\n" +
                "\n" +
                "    var merchantPublicKey = \""+elegroResponseVO.getPublickey()+"\";\n" +
                "    var amount = \""+elegroResponseVO.getAmount()+"\";\n" +
                "    var currency=\""+elegroResponseVO.getCurrency()+"\";\n" +
                "    var trackingId=\""+elegroResponseVO.getMerchantOrderId()+"\";\n" +
                "    var redirectURL=\""+elegroResponseVO.getRedirectUrl()+"\";\n" +
                "    \n" +
                "    var elegro = ElegroWidget({\n" +
                "        amount: parseFloat(amount), currency: currency,\n" +
                "        orderId: trackingId,\n" +
                "        customData: {}\n" +
                "    }, 'elegro-widget')(merchantPublicKey); // merchant public key\n" +
                "    elegro.addListener('success', function (response) {\n" +
                "        console.log(\"response ---\" ,response);\n" +
                "        callFrontEnd(response,trackingId);\n" +
                "    });\n" +
                "    elegro.addListener('failed', function (error) {\n" +
                "        console.log(\"error ---\" ,error);\n" +
                "        let errorObj = {\n" +
                "            message:error['message'],\n" +
                "            error:error['error']['message']\n" +
                "        }\n" +
                "        callFrontEnd(errorObj,trackingId);\n" +
                "    });\n" +
                "    \n" +
                "    function callFrontEnd(data,trackingId){\n" +
                "        let dataObj = data;\n" +
                "        document.getElementsByName(\"elegroForm\")[0].setAttribute(\"action\",redirectURL);\n" +
                "        for(var prop in dataObj){\n" +
                "            var x = document.createElement(\"INPUT\");\n" +
                "            x.setAttribute(\"type\", \"hidden\");\n" +
                "            x.setAttribute(\"name\", prop);\n" +
                "            x.setAttribute(\"value\", dataObj[prop]);\n" +
                "            document.elegroForm.appendChild(x);\n" +
                "        }\n" +
                "        document.elegroForm.submit();\n" +
                "    }\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>\n";

        return html;
    }


    public static String doPostHTTPSURLConnectionClient(String strURL, String req, String key) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL-->" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("authorization", "Bearer" + " " + key);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Accept", "application/json");
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

}
