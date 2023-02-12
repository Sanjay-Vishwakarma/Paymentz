package com.payment.voguePay;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.jasper.tagplugins.jstl.core.Url;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jeet on 25-07-2019.
 */
public class VoguePayUtils
{

    private static final TransactionLogger transactionLogger = new TransactionLogger(VoguePayUtils.class.getName());

    public static String getRedirectForm(String trackingId, CommResponseVO response3D)
    {
        transactionLogger.error("redirect url-->" + response3D.getRedirectUrl());
        transactionLogger.error("trackingid inside VoguePayUtils ----->"+trackingId);

        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\">\n");
        html.append("<input type=\"hidden\" name=\"encoding\" value=\"utf-8\">");
        html.append("</form>\n");

        transactionLogger.error("form----"+html.toString());
        return html.toString();
    }

    public static String getHttpUrlConnection(String reqUrl)
    {
        transactionLogger.error("Inside getHttpUrlConnection --->");
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn = null;
        try
        {
            URL url = new URL(reqUrl);
            System.setProperty("https.protocols", "TLSv1.2");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            //conn.addRequestProperty("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)");
            BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";

            while ((result = bf.readLine()) != null)
            {
                sb.append(result);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Connection Issue While placing transaction", e);
        }
        finally
        {
            conn.disconnect();
        }
        return sb.toString();
    }

    public CommRequestVO getVoguePayUtils(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return  commRequestVO;

    }

}
