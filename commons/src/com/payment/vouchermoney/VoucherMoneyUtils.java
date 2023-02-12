package com.payment.vouchermoney;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * Created by nikita on 6/30/2017.
 */
public class VoucherMoneyUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(VoucherMoneyUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req,String signature,String merchantId) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {

            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Signature",signature);
            post.addRequestHeader("merchant-id", merchantId);
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException--",he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--",io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static CommRequestVO getVoucherMoneyRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merchantId = account.getMerchantId();

        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());

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
    public static String signature(String stringToSign, String key) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretkey = new SecretKeySpec(java.util.Base64.getDecoder()
                    .decode(key), "HmacSHA512");
            sha512_HMAC.init(secretkey);
            byte[] mac_data = sha512_HMAC.doFinal(stringToSign.getBytes("UTF-8"));
            return java.util.Base64.getEncoder()
                    .encodeToString(mac_data);
        } catch (Exception ex) {
            transactionLogger.error("Problem with signing: " + stringToSign);
            throw new RuntimeException("Problem with processing json", ex);
        }
    }
    public static  String writeValueAsString(Object obj){
        String valueAsString = "";
        ObjectMapper objectMapper= new ObjectMapper();
        try {
            valueAsString = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            //System.out.println("Problem with processing json from Object: " + obj.toString());
            throw new RuntimeException("Problem with processing json", e);
        }
        return  valueAsString;
    }
    public static String voucherExpDate(String expDateOffset ){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = "";
        Calendar c = Calendar.getInstance();
        c.getTime();
        c.add(Calendar.DATE, Integer.valueOf(expDateOffset));
        date = simpleDateFormat.format(c.getTime());
        return date.toString();
    }
    public static String generateAutoSubmitForm(String url)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"" + url+ "").append("\" method=\"post\">\n");
        html.append("</form>");
        return html.toString();
    }
    public String get2CharCountryCode(String str) {
        if(str!=null){
            return str.length() < 2 ? str : str.substring(0, 2);
        }
        return str;
    }


}
