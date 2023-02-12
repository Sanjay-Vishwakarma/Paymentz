package com.payment.Triple000;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 10/14/2020.
 */
public class Triple000PaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "triple000";
    private TransactionLogger transactionLogger = new TransactionLogger(Triple000PaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.triple000");
    private static Triple000Utils triple000Utils = new Triple000Utils();

    public Triple000PaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        try
        {
            String termUrl = "";
            StringBuffer html = new StringBuffer();
            String site_url="https://"+commMerchantVO.getHostUrl();
            transactionLogger.error("site_url---"+site_url);
            transactionLogger.error("Triple000 host url----" + commMerchantVO.getHostUrl());
  /* if (functions.isValueNull(commMerchantVO.getHostUrl()))
       {

           termUrl = ESAPI.encoder().encodeForURL("https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")) + trackingID;
           transactionLogger.error("Triple000 from RB HOST_URL----"+termUrl);
       }
       else
     {*/
         termUrl = ESAPI.encoder().encodeForURL(RB.getString("TERM_URL")) + trackingID;
         transactionLogger.error("Triple000 from RB TERM_URL----" + termUrl);
    //}



            String saleUrl = "";
            boolean isTest = gatewayAccount.isTest();
            if (isTest)
                saleUrl = RB.getString("TEST_URL");
            else
                saleUrl = RB.getString("LIVE_URL");



            String threeD_flag = gatewayAccount.get_3DSupportAccount();
            String id = gatewayAccount.getFRAUD_FTP_USERNAME();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String operator_id = gatewayAccount.getMerchantId();
            String ext_id = trackingID;
            String tran_amount = "";
            String currency = "";
            String cc_number = "";
            String cc_expiry = "";
            String cvv = "";
            String ip_address = "";
            String cust_fname = "";
            String cust_lname = "";
            String birthdate = "";
            String ssn = "";
            String address1 = "";
            String address2 = "";
            String card_type = "";
            String city = "";
            String state = "";
            String zip_code = "";
            String country_code = "";
            String email = "";
            String phone_no = "";
            String cardType1=GatewayAccountService.getCardType(transDetailsVO.getCardType());
            String notifyUrl = ESAPI.encoder().encodeForURL(RB.getString("NOTIFY_URL"));
            if (functions.isValueNull(addressDetailsVO.getBirthdate()))
            {
                //SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    if (!addressDetailsVO.getBirthdate().contains("-"))
                    {
                        birthdate = dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));

                    }
                    else
                    {
                        birthdate = addressDetailsVO.getBirthdate();
                    }
                }
                catch (ParseException e)
                {
                    birthdate = addressDetailsVO.getBirthdate();

                    transactionLogger.error("Parse Exception Triple000-->", e);
                }
            }

            if (functions.isValueNull(transDetailsVO.getAmount()))
            {
                tran_amount = transDetailsVO.getAmount();
            }

            if (functions.isValueNull(ESAPI.encoder().encodeForURL(transDetailsVO.getCurrency())))
            {
                currency = ESAPI.encoder().encodeForURL(transDetailsVO.getCurrency());
            }
            String month = "";
            String year = "";

            if (functions.isValueNull(cardDetailsVO.getExpMonth())&& functions.isValueNull(cardDetailsVO.getExpYear()))
            {
                month = cardDetailsVO.getExpMonth();
                year = cardDetailsVO.getExpYear();
                cc_expiry = month + "/" + year.substring(2, 4);
            }
            if (functions.isValueNull(cardDetailsVO.getcVV()))
            {
                cvv = cardDetailsVO.getcVV();

            }
            if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
            {
                ip_address = addressDetailsVO.getCardHolderIpAddress();
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname())))
            {
                cust_fname = ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname())))
            {
                cust_lname = ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
            }

            if (functions.isValueNull(addressDetailsVO.getSsn()))
            {
                ssn = addressDetailsVO.getSsn();

            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet())))
            {
                address1 = ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());

            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet())))
            {
                address2 = ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());

            }
            if (functions.isValueNull(cardType1))
            {
                card_type = Triple000Utils.getCardTypeCode(cardType1);
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCity())))
            {
                city = ESAPI.encoder().encodeForURL(addressDetailsVO.getCity());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getState())))
            {
                state = ESAPI.encoder().encodeForURL(addressDetailsVO.getState());

            }
            if (functions.isValueNull(cardDetailsVO.getCardNum()))
            {
                cc_number = cardDetailsVO.getCardNum();
            }
            if (functions.isValueNull(addressDetailsVO.getZipCode()))
            {
                zip_code = addressDetailsVO.getZipCode();


            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry())))
            {
                country_code = ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry());

            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getEmail())))
            {
                email = ESAPI.encoder().encodeForURL(addressDetailsVO.getEmail());

            }
            if (functions.isValueNull(addressDetailsVO.getPhone()))
            {
                phone_no = addressDetailsVO.getPhone();
            }

            if (threeD_flag.equalsIgnoreCase("O") || threeD_flag.equalsIgnoreCase("Y") || threeD_flag.equalsIgnoreCase("N"))
            {
                threeD_flag = "Payment";
            }
            StringBuffer request = new StringBuffer();
            request.append("type=" + threeD_flag + "&id=" + id + "&key=" + key + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&currency=" + currency +
                    "&tran_amount=" + tran_amount + "&card_type=" + card_type + "&cc_number=" + cc_number + "&cc_expiry=" + cc_expiry + "&cvv=" + cvv +
                    "&ip_address=" + ip_address + "&cust_fname=" + cust_fname + "&cust_lname=" + cust_lname + "&birthdate=" + birthdate + "&ssn=" + ssn +
                    "&address1=" + address1 + "&address2=" + address2 + "&city=" + city + "&state=" + state + "&country_code=" + country_code +
                    "&zip_code=" + zip_code + "&email=" + email + "&phone_no=" + phone_no + "&server_url=" + notifyUrl + "&browser_url=" + termUrl+"&site_url="+site_url);



            StringBuffer requestlog = new StringBuffer();
            requestlog.append("type=" + threeD_flag + "&id=" + id + "&key=" + key + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&currency=" + currency +
                    "&tran_amount=" + tran_amount + "&card_type=" + card_type + "&cc_number=" + functions.maskingPan(cc_number) + "&cc_expiry=" + functions.maskingExpiry(cc_expiry) + "&cvv=" + functions.maskingNumber(cvv) +
                    "&ip_address=" + ip_address + "&cust_fname=" + cust_fname + "&cust_lname=" + cust_lname + "&birthdate=" + birthdate + "&ssn=" + ssn +
                    "&address1=" + address1 + "&address2=" + address2 + "&city=" + city + "&state=" + state + "&country_code=" + country_code +
                    "&zip_code=" + zip_code + "&email=" + email + "&phone_no=" + phone_no + "&server_url=" + notifyUrl + "&browser_url=" + termUrl+"&site_url="+site_url);



            transactionLogger.error("Triple000 Sale Request ---->" + trackingID + "---" + requestlog);

            String response = Triple000Utils.doHttpPostConnection(saleUrl, request.toString());

            transactionLogger.error("Triple000 Sale response---->" + trackingID + "---" + response);



            if (functions.isValueNull(response))
            {
                Map<String, String> responseMap = triple000Utils.readTriple000XMLResponse(response, trackingID, threeD_flag);
                transactionLogger.error("tran_id---->" + responseMap.get("tran_id"));
                transactionLogger.error("error_code---->" + responseMap.get("error_code"));
                transactionLogger.error("response---->" + responseMap.get("type"));
                if ("0".equalsIgnoreCase(responseMap.get("error_code")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(responseMap.get("tran_id"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("error_code"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                    commResponseVO.setUrlFor3DRedirect(responseMap.get("html_data"));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else if ("3D".equalsIgnoreCase(responseMap.get("error_code")))
                {
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setTransactionId(responseMap.get("tran_id"));
                    // commResponseVO.setUrlFor3DRedirect(responseMap.get("threeDSecureAcsUrl"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("error_code"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                    commResponseVO.setUrlFor3DRedirect(responseMap.get("html_data"));
                }

                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("error_code"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                }


            }
        }
        catch (EncodingException e)
        {
            transactionLogger.error("Triple000 Sale Exception ---->" + trackingID + "---", e);


        }
        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO)  throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processQuery ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String id = gatewayAccount.getFRAUD_FTP_USERNAME();
        String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String operator_id = gatewayAccount.getMerchantId();
        String ext_id = trackingID;
        String type = "CheckStatus";
        String tran_id = commRequestVO.getTransDetailsVO().getPreviousTransactionId();

        transactionLogger.error("inside triple000 processQuery transactionid -------------->"+trackingID + "---" + tran_id);
        boolean isTest = gatewayAccount.isTest();
        String amount = commTransactionDetailsVO.getAmount();
        String currency = commTransactionDetailsVO.getCurrency();
        String date = commResponseVO.getBankTransactionDate();
        String inquiryUrl = "";

        if (isTest)
        {
            inquiryUrl = RB.getString("TEST_URL");
            transactionLogger.error("InquiryRequest TESTURL::::::::" + inquiryUrl);

        }
        else
        {
            inquiryUrl = RB.getString("LIVE_URL");

            transactionLogger.error("InquiryRequest LIVE_URL::::::::" + inquiryUrl);
        }

        try
        {

            StringBuffer request = new StringBuffer();
            request.append("type=" + type + "&id=" + id + "&key=" + key + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&tran_id=" + tran_id);


            transactionLogger.error("Request processQuery Triple000 -->" +trackingID + "---" +  request);
            String response = Triple000Utils.doHttpPostConnection(inquiryUrl, request.toString());
            transactionLogger.error("Response processQuery Triple000-->" + trackingID + "---" + response);
            if (functions.isValueNull(response))
            {
                Map<String, String> responseMap = triple000Utils.readTriple000XMLResponse(response, trackingID, type);
                transactionLogger.error("tran_id---->" + responseMap.get("tran_id"));
                transactionLogger.error("error_code---->" + responseMap.get("error_code"));
                transactionLogger.error("response---->" + responseMap.get("type"));
                if ("0".equalsIgnoreCase(responseMap.get("error_code")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setTransactionStatus(responseMap.get("code"));
                    commResponseVO.setTransactionId(responseMap.get("tran_id"));
                    // commResponseVO.setBankTransactionDate(responseMap.get("timestamp"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                    commResponseVO.setAuthCode(responseMap.get("error_code"));
                    commResponseVO.setBankTransactionDate(responseMap.get("resp_time"));

                    commResponseVO.setAmount(commTransactionDetailsVO.getAmount());

                    commResponseVO.setTransactionStatus("success");

                    commResponseVO.setResponseHashInfo(id);

                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Declined");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("Triple000PaymentGateway :: Inside processPayout ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        try
        {


            String payoutUrl="";
            boolean isTest = gatewayAccount.isTest();
            if(isTest)
                payoutUrl=RB.getString("TEST_URL");
            else
                payoutUrl=RB.getString("LIVE_URL");

            String type = "Payout";
            String payout_type="1";

            String threeD_flag = gatewayAccount.get_3DSupportAccount();
            String id = gatewayAccount.getFRAUD_FTP_USERNAME();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String operator_id = gatewayAccount.getMerchantId();
            String ext_id = trackingID;
            String tran_amount = "";
            String currency = "";
            String cc_number = "";
            String cc_expiry = "";
            String cvv ="";
            // String ip_address =addressDetailsVO.getCardHolderIpAddress();
            String ip_address ="";
            String cust_fname ="";
            String cust_lname = "";
            String birthdate = "";
            String ssn = "";
            String address1 = "";
            String address2 =  "";
            String card_type = "";
            String city = "";
            String state = "";
            String zip_code ="";
            String country_code = "";
            String email = "";
            String phone_no ="";
            String notifyUrl = ESAPI.encoder().encodeForURL(RB.getString("NOTIFY_URL"));

            if(functions.isValueNull(transDetailsVO.getAmount()))
            {
                tran_amount=transDetailsVO.getAmount();
            }

            if(functions.isValueNull(ESAPI.encoder().encodeForURL(transDetailsVO.getCurrency())))
            {
                currency=ESAPI.encoder().encodeForURL(transDetailsVO.getCurrency());
            }
            String month="";
            String year="";

            if(functions.isValueNull(cardDetailsVO.getExpMonth() + "/"+cardDetailsVO.getExpYear()))
            {
                month=cardDetailsVO.getExpMonth() ;
                year=cardDetailsVO.getExpYear();
                cc_expiry=month+"/"+year.substring(2,4);
            }
            if(functions.isValueNull(cardDetailsVO.getcVV()))
            {
                cvv  =cardDetailsVO.getcVV();

            }
            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
            {
                ip_address=addressDetailsVO.getCardHolderIpAddress();
            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname())))
            {
                cust_fname = ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname())))
            {
                cust_lname = ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
            }

            if(functions.isValueNull(addressDetailsVO.getSsn()))
            {
                ssn = addressDetailsVO.getSsn();

            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet())))
            {
                address1 = ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());

            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet())))
            {
                address2 =  ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());

            }
            if( functions.isValueNull(Triple000Utils.getCardTypeCode(cardDetailsVO.getCardType())))
            {
                card_type = Triple000Utils.getCardTypeCode(cardDetailsVO.getCardType());
            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCity())))
            {
                city = ESAPI.encoder().encodeForURL(addressDetailsVO.getCity());
            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getState())))
            {
                state = ESAPI.encoder().encodeForURL(addressDetailsVO.getState());

            }
            if(functions.isValueNull(cardDetailsVO.getCardNum()))
            {
                cc_number = cardDetailsVO.getCardNum();
            }
            if(functions.isValueNull(addressDetailsVO.getZipCode()))
            {
                zip_code = addressDetailsVO.getZipCode();
            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry())))
            {
                country_code =ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry());

            }
            if(functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getEmail())))
            {
                email = ESAPI.encoder().encodeForURL(addressDetailsVO.getEmail());

            }
            if(functions.isValueNull(addressDetailsVO.getPhone()))
            {
                phone_no = addressDetailsVO.getPhone();
            }




            StringBuffer request = new StringBuffer();
            request.append("type=" + type + "&id=" + id + "&key=" + key + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&currency=" + currency +
                    "&tran_amount=" + tran_amount + "&card_type=" + card_type + "&cc_number=" + cc_number + "&cc_expiry=" + cc_expiry + "&cvv=" + cvv +
                    "&ip_address=" + ip_address + "&cust_fname=" + cust_fname + "&cust_lname=" + cust_lname + "&birthdate=" + birthdate +"&ssn=" + ssn +
                    "&address1=" + address1 + "&address2=" + address2 + "&city=" + city + "&state=" + state + "&country_code=" + country_code + "&payout_type=" +payout_type+
                    "&zip_code=" + zip_code + "&email=" + email + "&phone_no=" + phone_no );

            StringBuffer requestlog = new StringBuffer();
            requestlog.append("type=" + type + "&id=" + id + "&key=" + key + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&currency=" + currency +
                    "&tran_amount=" + tran_amount + "&card_type=" + card_type + "&cc_number=" + functions.maskingPan(cc_number) + "&cc_expiry=" + functions.maskingExpiry(cc_expiry) + "&cvv=" + functions.maskingNumber(cvv) +
                    "&ip_address=" + ip_address + "&cust_fname=" + cust_fname + "&cust_lname=" + cust_lname + "&birthdate=" + birthdate +"&ssn=" + ssn +
                    "&address1=" + address1 + "&address2=" + address2 + "&city=" + city + "&state=" + state + "&country_code=" + country_code + "&payout_type=" +payout_type+
                    "&zip_code=" + zip_code + "&email=" + email + "&phone_no=" + phone_no );
            transactionLogger.error("Triple000 Payout Request---->"+trackingID+"---"+requestlog);

            String response = Triple000Utils.doHttpPostConnection(payoutUrl, request.toString());

            transactionLogger.error("Triple000 Payout response---->" +trackingID+"---"+ response);



            if (functions.isValueNull(response))
            {
                Map<String, String> responseMap = triple000Utils.readTriple000XMLResponse(response, trackingID, type);
                transactionLogger.error("tran_id---->" +responseMap.get("tran_id") );
                transactionLogger.error("error_code---->" + responseMap.get("error_code"));
                transactionLogger.error("response---->" + responseMap.get("type"));
                transactionLogger.error("responseMap---->" + responseMap);
                if ("0".equalsIgnoreCase(responseMap.get("error_code")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(responseMap.get("tran_id"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("error_code"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                    commResponseVO.setUrlFor3DRedirect(responseMap.get("html_data"));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else if("3D".equalsIgnoreCase(responseMap.get("error_code")))
                {
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setTransactionId(responseMap.get("tran_id"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("error_code"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                    commResponseVO.setUrlFor3DRedirect(responseMap.get("html_data"));
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("error_code"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                }


            }}
        catch (EncodingException e)
        {
            transactionLogger.error("Triple000 Exception ---->" + trackingID+"---",e);

        }
        return commResponseVO;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("Triple000PaymentGateway :: Inside processRefund ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String id = gatewayAccount.getFRAUD_FTP_USERNAME();
        String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String operator_id = gatewayAccount.getMerchantId();
        String ext_id = trackingID;
        String type = "Refund";
        String tran_id = commRequestVO.getTransDetailsVO().getPreviousTransactionId();
// String tran_id = commTransactionDetailsVO.getPreviousTransactionId();
        String refundurl = "";
        String amount = commTransactionDetailsVO.getAmount();
        String currency = commTransactionDetailsVO.getCurrency();
        String date = commResponseVO.getBankTransactionDate();
        String notifyUrl = RB.getString("NOTIFY_URL");
        transactionLogger.error("inside triple000 processRefund transactionid -------------->" + tran_id);
        boolean isTest = gatewayAccount.isTest();

        if (isTest)
        {
            refundurl = RB.getString("TEST_URL");
            transactionLogger.error("RefundRequest TESTURL::::::::" + refundurl);

        }
        else
        {
            refundurl = RB.getString("LIVE_URL");

            transactionLogger.error("RefundRequest LIVE_URL::::::::" + refundurl);
        }
        try{

            StringBuffer request = new StringBuffer();
            request.append("type=" + type + "&id=" + id + "&key=" + key + "&operator_id=" + operator_id + "&tran_id=" + tran_id + "&ext_id=" + ext_id);

            transactionLogger.error("Request processRefund Triple000 -->" +trackingID + "---" +  request);
            String response = Triple000Utils.doHttpPostConnection(refundurl, request.toString());
            transactionLogger.error("Response processRefund Triple000-->" +trackingID + "---" +  response);

            if (functions.isValueNull(response))
            {
                Map<String, String> responseMap = triple000Utils.readTriple000XMLResponse(response, trackingID, type);
                transactionLogger.error("tran_id---->" + responseMap.get("tran_id"));
                transactionLogger.error("error_code---->" + responseMap.get("error_code"));
                transactionLogger.error("response---->" + responseMap.get("type"));
                if ("0".equalsIgnoreCase(responseMap.get("error_code")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setTransactionStatus(responseMap.get("code"));
                    commResponseVO.setTransactionId(responseMap.get("tran_id"));
// commResponseVO.setBankTransactionDate(responseMap.get("timestamp"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("resp_time"));
                    commResponseVO.setAuthCode(responseMap.get("error_code"));
                    commResponseVO.setBankTransactionDate(date);

                    commResponseVO.setAmount(commTransactionDetailsVO.getAmount());

                    commResponseVO.setTransactionStatus("success");

                    commResponseVO.setResponseHashInfo(id);

                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Declined");
                }

            }
        }

        catch (Exception e)
        {
            transactionLogger.error("Triple000 Refund Exception ---->" + trackingID + "---", e);


        }
        return commResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("Triple000PaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {

        transactionLogger.error(":::::Entered into processAutoRedirect for Triple000:::::");
        CommRequestVO commRequestVO = null;
        Functions functions = new Functions();
        String html = "";
        commRequestVO = Triple000Utils.getTriple000RequestVO(commonValidatorVO);
        Comm3DResponseVO comm3DResponseVO = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
        html = comm3DResponseVO.getUrlFor3DRedirect();
        return html;

    }

    public String getMaxWaitDays()
    {
        return null;
    }
}
