package com.payment.paysec;

import com.directi.pg.LoadProperties;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created by Admin on 5/1/2016.
 */
public class PaysecPaymentProcess extends CommonPaymentProcess
{
    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.PaysecServlet");
    final static ResourceBundle RB2 = LoadProperties.getProperty("com.directi.pg.AsyncRedirectionUrl");

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {

        String url = "https://pay.paysec.com/GUX/GPost";

        AsyncParameterVO asyncParameterVO = null;
        JSONObject json = null;
        String v_callbackurl = rb.getString("CALLBACK_URL");
        StringBuffer stringParams = new StringBuffer();
        PaySecUtils paySecUtils = new PaySecUtils();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());

        String signaturekey = account.getPassword();
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        Float fAmount=Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
        try
        {
            stringParams.append("CID").append("=").append(URLEncoder.encode(account.getMerchantId(), "UTF-8")).append("&");
            stringParams.append("v_CartID").append("=").append(URLEncoder.encode(commonValidatorVO.getTrackingid(), "UTF-8")).append("&");
            stringParams.append("v_currency").append("=").append(URLEncoder.encode(account.getCurrency(), "UTF-8")).append("&");
            stringParams.append("v_amount").append("=").append(URLEncoder.encode(df.format(fAmount), "UTF-8")).append("&");
            stringParams.append("v_firstname").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getFirstname(), "UTF-8")).append("&");
            stringParams.append("v_lastname").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getLastname(), "UTF-8")).append("&");
            stringParams.append("v_billemail").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getEmail(), "UTF-8")).append("&");
            stringParams.append("v_billstreet").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getStreet(), "UTF-8")).append("&");
            stringParams.append("v_billcity").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getCity(), "UTF-8")).append("&");
            stringParams.append("v_billstate").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getState(), "UTF-8")).append("&");
            stringParams.append("v_billpost").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getZipCode(), "UTF-8")).append("&");
            stringParams.append("v_billcountry").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getCountry(), "UTF-8")).append("&");
            stringParams.append("v_billphone").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getPhone(), "UTF-8")).append("&");
            stringParams.append("v_productcode").append("=").append(URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getOrderId(), "UTF-8")).append("&");
            stringParams.append("v_callbackurl").append("=").append(URLEncoder.encode(v_callbackurl, "UTF-8")).append("&");

            String signature = paySecUtils.generateMD5ChecksumDirectKit(signaturekey.toUpperCase(), account.getMerchantId().toUpperCase(), commonValidatorVO.getTrackingid(), paySecUtils.getAmount(commonValidatorVO.getTransDetailsVO().getAmount()), account.getCurrency().toUpperCase());
            stringParams.append("signature=").append(URLEncoder.encode(signature, "UTF-8"));
            String urlParameters = stringParams.toString();

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            // Send post request
            System.setProperty("jsse.enableSNIExtension", "false");
            System.setProperty("https.protocols", "TLSv1.2");
            paySecUtils.doTrustToCertificates();
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // add request header
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            StringBuffer response;

            wr.write(postData);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            json = new JSONObject(response.toString());
            Iterator i = json.keys();
            String key = (String) i.next();

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("token");
            asyncParameterVO.setValue(json.get(key).toString());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            directKitResponseVO.setBankRedirectionUrl(RB2.getString("PAYSEC_URL"));
        }
        catch (Exception e)
        {
            //logger.error("Exception in PaySecUtils---",e);
        }

        return directKitResponseVO;
    }
}
