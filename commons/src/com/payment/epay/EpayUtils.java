package com.payment.epay;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.ResourceBundle;

/**
 * Created by Admin on 10/31/17.
 */
public class EpayUtils
{
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.epay");
    TransactionLogger transactionLogger=new TransactionLogger(EpayUtils.class.getName());

   /* public static CommRequestVO getEpayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setCustomerid(commonValidatorVO.getAddressDetailsVO().getCustomerid());
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
    }*/

    public static String doGetHTTPSURLConnectionClient(String strURL) throws PZTechnicalViolationException
    {

        final String USER_AGENT = "Mozilla/5.0";
        TransactionLogger transactionLogger = new TransactionLogger(EpayUtils.class.getName());
        String result = "";
        URL obj;
        HttpURLConnection con=null;
        BufferedReader in=null;
        try
        {
            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
           /* System.out.println("\nSending 'GET' request to URL : " + strURL);
            System.out.println("Response Code : " + responseCode);*/

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }


            result = response.toString();
            //System.out.println(response.toString());

        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException--", he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--", io);
        }
        finally
        {
            if(in !=null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    transactionLogger.error("IOException--",  e);
                }
            }
            if(con !=null)
            {
                con.disconnect();
            }
        }
        return result;

    }

    public static String getEpayRequest(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZTechnicalViolationException
    {
        TransactionLogger transactionLogger=new TransactionLogger(EpayUtils.class.getName());

        String redirectHTML = "";
        try
        {
            String URL = "";
            String trackingid = commonValidatorVO.getTrackingid();
            String gatewayMid = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getMerchantId();
            String userName = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getFRAUD_FTP_USERNAME();
            String secretKey = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getFRAUD_FTP_PASSWORD();
            String amount = commonValidatorVO.getTransDetailsVO().getAmount();
            String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
            String customerId = commonValidatorVO.getAddressDetailsVO().getCustomerid();
            String cutomerEmail = commonValidatorVO.getAddressDetailsVO().getEmail();

            String testURL = RB.getString("TEST_SALE_URL");
            String livetURL = RB.getString("LIVE_SALE_URL");

            boolean isTest = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).isTest();

            if (isTest)
            {
                transactionLogger.error(":::::inside isTest:::::");
                URL = testURL;
            }
            else
            {
                transactionLogger.error(":::::inside Live:::::");
                URL = livetURL;
            }

            String saleRequest = "INVOICE=" + trackingid + "\nAMOUNT=" + amount + "\nCURRENCY=" + currency + "\nMIN=" + gatewayMid + "\nMEMAIL=" + userName + "\nCIN=" + customerId + "\nCEMAIL=" + cutomerEmail + "\nEXP_TIME="+EpayUtils.expDate(RB.getString("NO_OF_DAYS"))+"";
            transactionLogger.error("saleRequest:::::"+saleRequest);

            String encoded = new String(org.apache.commons.codec.binary.Base64.encodeBase64((saleRequest).getBytes()));
            transactionLogger.error("encoded:::::"+encoded);

            String checksum = calculateRFC2104HMAC(encoded, secretKey);
            transactionLogger.error("checksum:::::"+checksum);

            redirectHTML = generateAutoSubmitForm(URL,encoded, checksum,trackingid);
            transactionLogger.error("redirectHTML:::::"+redirectHTML);

        }
        catch (SignatureException e)
        {
            transactionLogger.error("SignatureException:::::"+e);
        }catch (NoSuchAlgorithmException ne){
            transactionLogger.error("NoSuchAlgorithmException:::::"+ne);
        }catch (InvalidKeyException ie){
            transactionLogger.error("InvalidKeyException:::::"+ie);
        }
        return redirectHTML;
    }

    public static String generateAutoSubmitForm(String url,String encoded,String checksum,String trackingid)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append("" +url).append("\" method=\"post\">\n");
        html.append("<input type=\"hidden\" name=\"PAGE\" id=\"PAGE\" value=\"paylogin\">\n");
        html.append("<input type=\"hidden\" name=\"ENCODED\" id=\"ENCODED\" value=\""+encoded+"\">\n");
        html.append("<input type=\"hidden\" name=\"CHECKSUM\" id=\"CHECKSUM\" value=\""+checksum+"\">\n");
        html.append("<input type=\"hidden\" name=\"URL_OK\" id=\"URL_OK\" value=\"" +RB.getString("URL_OK")+trackingid+ "\">\n");
        html.append("<input type=\"hidden\" name=\"URL_CANCEL\" id=\"URL_CANCEL\" value=\"" +RB.getString("URL_CANCEL")+trackingid+ "\">\n");
        html.append("</form>");
        return html.toString();
    }

    public static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    public static String expDate(String day){

        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd.MM.yyyy");
        String date="";
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,Integer.parseInt(day));
        date=simpleDateFormat.format(calendar.getTime());
       return date.toString();
    }

}
