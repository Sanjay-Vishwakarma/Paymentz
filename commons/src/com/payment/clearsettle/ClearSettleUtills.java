package com.payment.clearsettle;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by Sneha on 1/16/2017.
 */
public class ClearSettleUtills
{
    public final static String charset = "UTF-8";
    private final  static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.clearsettle");
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettleUtills.class.getName());

    public static String generateAutoSubmitForm(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException
    {
        ClearSettleUtills clearSettleUtills = new ClearSettleUtills();

        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String apiKey = gatewayAccount.getMerchantId();

        GenericTransDetailsVO transDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        String countryCode = addressDetailsVO.getCountry();
        if (countryCode.length() > 2)
        {
            countryCode = countryCode.substring(0, 2);
        }

        SimpleDateFormat inputDate = new SimpleDateFormat("yyyymmdd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

        String birthDate = "";
        try
        {
            birthDate = formatter.format(inputDate.parse(addressDetailsVO.getBirthdate()));
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleUtills.class.getName(), "generateAutoSubmitForm()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        String amount = getCentAmount(transDetailsVO.getAmount());
        String currency = transDetailsVO.getCurrency();
        String email = addressDetailsVO.getEmail();
        String referenceNo =commonValidatorVO.getTrackingid() ;
        String returnUrl =RB.getString("CLEAR_SETTLE_HPP_FRONTEND");
        String billingFirstName = addressDetailsVO.getFirstname();
        String billingLastName = addressDetailsVO.getLastname();
        String billingAddress1 = addressDetailsVO.getStreet();
        String billingCity = addressDetailsVO.getCity();
        String billingCountry = countryCode;
        String billingPostcode = addressDetailsVO.getZipCode();

        StringBuilder html = new StringBuilder();

        try
        {
            String requestData1 = "" +
                    "amount=" + amount +
                    "&apiKey=" + apiKey +
                    "&currency=" + currency +
                    "&email=" + email +
                    "&referenceNo=" + referenceNo +
                    "&returnUrl=" + returnUrl +
                    "&billingFirstName=" + billingFirstName +
                    "&billingLastName=" + billingLastName +
                    "&billingAddress1=" + billingAddress1 +
                    "&billingCity=" + billingCity +
                    "&billingCountry=" + billingCountry +
                    "&billingPostcode=" + billingPostcode +
                    "&birthday=" + birthDate +
                    "&paymentMethod=CREDITCARD";

            transactionLogger.error("-----sale request-----" + requestData1);
            String response = "";
            if (isTest)
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionFromData(RB.getString("TEST_HPP_PURCHASE_URL"), requestData1);
            }
            else
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionFromData(RB.getString("LIVE_HPP_PURCHASE_URL"), requestData1);
            }

            transactionLogger.error("-----sale response-----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClearSettleResponseVO clearSettleResponseVO = objectMapper.readValue(response, ClearSettleResponseVO.class);
            if (clearSettleResponseVO != null)
            {
                String purchaseUrl = "";
                if ("283".equals(clearSettleResponseVO.getCode()))
                {
                    purchaseUrl = clearSettleResponseVO.getPurchaseUrl();
                    html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
                    html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"" + purchaseUrl + "").append("\" method=\"GET\">\n");
                    html.append("</form>");
                    transactionLogger.error("form---" + html);
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----", e);
        }
        return html.toString();
    }

    public static String getCentAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2 = dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    public static String getBirthday(String birthday)
    {
        String year = birthday.substring(0, 4);
        String month = birthday.substring(4, 6);
        String day = birthday.substring(6, 8);
        birthday = year + "-" + month + "-" + day;
        return birthday;
    }

    public static String generateAutoSubmitFormNew(Comm3DResponseVO commResponseVO)
    {
        transactionLogger.error("3d page displayed....." + commResponseVO.getRedirectUrl());
        String form = "<form name=\"launch3D\" method=\"GET\" action=\"" + commResponseVO.getRedirectUrl() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("ClearSettle Server URL:::" + strURL);
        StringBuffer result = new StringBuffer();
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
          //  System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
           // java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            URL obj = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type", "application/json");

            // Send post request
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            if (con.getResponseCode() != 500)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
            else
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ClearSettelUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ClearSettelUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        return result.toString();
    }

    public String doPostHTTPSURLConnectionFromData(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        try
        {
            URL obj = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            if (con.getResponseCode() != 500)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
            else
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ClearSettleUtills.java", "doPostHTTPSURLConnectionFromData()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ClearSettleUtills.java", "doPostHTTPSURLConnectionFromData()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        return result.toString();
    }

    public CommRequestVO getClearSettleHPPRequest(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        commAddressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());

        commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        return commRequestVO;
    }

    public String getVoucherPurchaseForm(CommResponseVO response)
    {
        String form = "<form name=\"launch3D\" method=\"GET\" action=\"" + response.getRedirectUrl() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }


}
