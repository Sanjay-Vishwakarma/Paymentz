package com.payment.safexpay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.manager.TransactionManager;
import com.manager.vo.*;
import com.paygate.ag.common.utils.PayGateCryptoUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by jeet on 03-09-2019.
 */
public class SafexPayPaymentProcess extends CommonPaymentProcess
{
    private static SafexPayGatewayLogger transactionLogger = new SafexPayGatewayLogger(SafexPayPaymentProcess.class.getName());
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.safexpay");
    private final static ResourceBundle RB1 = LoadProperties.getProperty("com.directi.pg.3CharCountryList");
    private final static ResourceBundle RB3 = LoadProperties.getProperty("com.directi.pg.SEPBANKS");
    private final static ResourceBundle RBWallets = LoadProperties.getProperty("com.directi.pg.SEPWALLETS");

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside get3DConfirmationForm");
         String html = "";
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
            SafexPayUtils safexPayUtils=new SafexPayUtils();
            CommRequestVO commRequestVO = safexPayUtils.getSafexPayRequestVO(commonValidatorVO);
            boolean isTest = gatewayAccount.isTest();
            Functions functions = new Functions();

            String accountID=commonValidatorVO.getMerchantDetailsVO().getAccountId();
            String merchant_key = gatewayAccount.getFRAUD_FTP_USERNAME();
            transactionLogger.error("merchant_key --- " + merchant_key);
            String payment_brand = commonValidatorVO.getPaymentBrand();
            transactionLogger.error("payment_brand is -->" + payment_brand);
            String processorName = commonValidatorVO.getProcessorName();
            transactionLogger.error("processorName--" + processorName);
            String card_Type = commonValidatorVO.getPaymentBrand();
            transactionLogger.error("card_type --- " + card_Type);
            String payment_Card = commonValidatorVO.getPaymentMode();
            transactionLogger.error("payment_Card --- " + payment_Card);
            //String payment_Type = GatewayAccountService.getPaymentMode(commonValidatorVO.getPaymentType());
            String autoRedirect      =commRequestVO.getAutoRedirectFlag();
            transactionLogger.error("safexpaypaymentprocess autoRedirect --- " + autoRedirect);

            String url = "";
            String pg_id = "";
            String paymode = "";
            String scheme = "";
            String card_name = "";
            String emi_months = "";
            String mobile_num = "";
            if(functions.isValueNull(genericAddressDetailsVO.getPhone())){
                mobile_num =genericAddressDetailsVO.getPhone().replace(" ","");
            }
            else {
                mobile_num="9999999999";
            }
            String mobile_number = "";
            String mobile_no = "";
            if (mobile_num.contains("-"))
            {
                String mobile_No[] = mobile_num.split("\\-");
                mobile_number = mobile_No[0];
                mobile_no = mobile_No[1];
            }
            if(mobile_num.length()>10){

                mobile_no=mobile_num.substring(mobile_num.length() - 10);
            }
            else
            {
                mobile_no = mobile_num;
            }
            String unique_id = "";
            String is_logged_in = "";
            String country = "";

            //Ship_details
            String ship_address = "";
            String ship_city = "";
            String ship_state = "";
            String ship_country = "";
            String ship_zip = "";
            String ship_days = "";
            String address_count = "";
            String currency="";

            // Item_details
            String item_count = "";
            String item_value = "";
            String item_category = "";

            // Other_details
            String udf_1 = "";
            String udf_2 = "";
            String udf_3 = "";
            String udf_4 = "";
            String udf_5 = "";
            String vpa_address="";

            if(functions.isValueNull(commonValidatorVO.getVpa_address())){
                vpa_address=commonValidatorVO.getVpa_address();
            }
            else {
                vpa_address="";
            }


            String pg_details = "";
            String txn_details = "";
            String card_details = "";
            String cust_details = "";
            String bill_details = "";
            String ship_details = "";
            String item_details = "";
            String other_details = "";
            String upi_details="";

            String card_no = "";
            if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
            {
                card_no = genericCardDetailsVO.getCardNum();
            }
            else
            {
                card_no = "";
            }
            transactionLogger.debug("card_no --- " + functions.maskingPan(card_no));
            String exp_month = "";
            if (functions.isValueNull(genericCardDetailsVO.getExpMonth()))
            {
                exp_month = genericCardDetailsVO.getExpMonth();
            }
            else
            {
                exp_month = "";
            }
            transactionLogger.debug("exp_month --- " +functions.maskingNumber(exp_month) );
            String exp_year = "";
            if (functions.isValueNull(genericCardDetailsVO.getExpYear()))
            {
                exp_year = genericCardDetailsVO.getExpYear();
            }
            else
            {
                exp_year = "";
            }
            transactionLogger.debug("exp_year --- " +functions.maskingNumber(exp_year));
            String cvv2 = "";
            if (functions.isValueNull(genericCardDetailsVO.getcVV()))
            {
                cvv2 = genericCardDetailsVO.getcVV();
            }
            else
            {
                cvv2 = "";
            }
            transactionLogger.debug("cvv2 --- " + functions.maskingNumber(cvv2));

            if (functions.isValueNull(genericAddressDetailsVO.getFirstname()))
            {
                if (functions.isValueNull(genericAddressDetailsVO.getLastname()))
                    card_name = genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname(); // Name on the Card ..
                else
                    card_name = genericAddressDetailsVO.getFirstname();
            }
            else
            {
                card_name = "";
            }

            String cust_name="";
            transactionLogger.debug("CardNme" + card_name);
            if(functions.isValueNull(payment_Card) && (payment_Card.equalsIgnoreCase("CC") || payment_Card.equalsIgnoreCase("DC"))){
                cust_name = card_name;
            }
            else{
                card_name="";
            }


            transactionLogger.debug("cust_name" + cust_name);
            String bill_address = "";
            String bill_city = "";
            String bill_state = "";
            String bill_country = "";
            String bill_zip = "";
            // Bill_details

            if (functions.isValueNull(genericAddressDetailsVO.getStreet()))
            {
                bill_address = genericAddressDetailsVO.getStreet();
            }
            else
            {
                bill_address = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getCity()))
            {
                bill_city = genericAddressDetailsVO.getCity();
            }
            else
            {
                bill_city = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getState()))
            {
                bill_state = genericAddressDetailsVO.getState();
            }
            else
            {
                bill_state = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getCountry()))
            {
                bill_country = genericAddressDetailsVO.getCountry();            }
            else
            {
                bill_country = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getZipCode()))
            {
                bill_zip = genericAddressDetailsVO.getZipCode();            }
            else
            {
                bill_zip = "";
            }




            String email_id = "";
            if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
            {
                email_id = genericAddressDetailsVO.getEmail();
                transactionLogger.debug("email_id ---" + email_id);
            }
            String ag_id = "paygate";
            String txn_type = "SALE";
            String channel = "WEB"; // WEB or MOBILE
            String me_id = gatewayAccount.getMerchantId();
            transactionLogger.error("me_id ---" + me_id);
            String order_no = commonValidatorVO.getTrackingid();
            transactionLogger.error("order_no--" + order_no);
            String amount = commonValidatorVO.getTransDetailsVO().getAmount();
           // transactionLogger.debug("country in  pp --333333------"+commonValidatorVO.getAddressDetailsVO().getCountry());
         /*   if (commonValidatorVO.getAddressDetailsVO().getCountry().length() == 2)
            {
                country = RB1.getString(genericAddressDetailsVO.getCountry());
            }
            else
            {
                country = "IND";
            }*/
            country = "IND";
             currency = commonValidatorVO.getTransDetailsVO().getCurrency();

            String termUrl = "";
            if (functions.isValueNull(merchantDetailsVO.getHostUrl()))
            {
                termUrl = "https://" + merchantDetailsVO.getHostUrl() + RB.getString("HOST_URL");
                transactionLogger.error("From HOST_URL notificationUrl ----" + termUrl);
            }
            else
            {
                termUrl = RB.getString("TERM_URL");
                transactionLogger.error("From RB notificationUrl ----" + termUrl);
            }

            String success_url = termUrl + order_no + "&status=success";
            String failure_url = termUrl + order_no + "&status=fail";
            transactionLogger.error("success_url  --" + success_url);
            transactionLogger.error("failure_url  --" + failure_url);

            // Step 1-
            if (functions.isValueNull(payment_Card) && (payment_Card.equalsIgnoreCase("CC") || payment_Card.equalsIgnoreCase("DC")))
            {
                transactionLogger.error("inside CC Or DC  --");
                paymode = payment_Card;

                if (isTest)
                {
                    transactionLogger.error("Inside isTest");
                    pg_id = "107";
                    emi_months = "6";
                    scheme = "2";
                    url = RB.getString("SALE_TEST_URL");
                }
                else
                {
                    transactionLogger.error("inside cc live-->");
                    if (currency.equals("INR"))
                    {
                        if(functions.isValueNull(gatewayAccount.getFromAccountId())){
                            transactionLogger.error("inside cc live-->"+pg_id);
                            pg_id = gatewayAccount.getFromAccountId();
                        }
                        else{
                            pg_id ="210";
                        }
                    }
                    if (currency.equals("USD"))
                    {
                        pg_id = "602";
                    }
                    emi_months = "";
                    url = RB.getString("SALE_LIVE_URL");

                    if (card_Type.equalsIgnoreCase("VISA"))
                        scheme = "1";
                    else if (card_Type.equalsIgnoreCase("MC"))
                        scheme = "2";
                    else if (card_Type.equalsIgnoreCase("MAESTRO"))
                        scheme = "5";
                    else if (card_Type.equalsIgnoreCase("RUPAY"))
                        scheme = "6";
                }
            }
            else if (functions.isValueNull(payment_Card) && payment_Card.equalsIgnoreCase("NBI"))
            {
                transactionLogger.error("payment_card  netbanking --- " + payment_Card);

                paymode = "NB";
                scheme = "7";
                if (isTest)
                {
                    transactionLogger.error("inside NBI test --");
                    pg_id = "63";
                    emi_months = "7";
                    url = RB.getString("SALE_TEST_URL");
                }
                else
                {
                    transactionLogger.error("inside NBI live --");
                    pg_id = processorName;
                    transactionLogger.error("pg_id  in live mode --->" + pg_id);
                    // pg_id=RB3.getString();
                    url = RB.getString("SALE_LIVE_URL");
                }

            }

            else if (functions.isValueNull(payment_Card) && payment_Card.equalsIgnoreCase("EWI"))
            {
                transactionLogger.error("payment_EWI wallet is -->" + payment_Card);
                if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE())&&"1352".equalsIgnoreCase(processorName))
                {
                    paymode = gatewayAccount.getFRAUD_FTP_SITE();
                }else{
                    paymode = "WA";
                }

               // paymode = "WA";
                scheme = "7";
                if (isTest)
                {
                    transactionLogger.error("inside EWI isTest -----");
                    pg_id = "60"; //60
                    emi_months = "7";
                    url = RB.getString("SALE_TEST_URL");

                }
                else if (RBWallets.containsKey("PAYTM") && processorName.contains(RBWallets.getString("PAYTM")))
                {
                    transactionLogger.error("inside paytm condition---->");
                    paymode="PT";
                    scheme="7";
                    pg_id =processorName;
                    url = RB.getString("SALE_LIVE_URL");

                 }
                  else
                    {
                        if("Y".equalsIgnoreCase(autoRedirect)&& RBWallets.containsKey(card_Type)){
                            transactionLogger.error(" cardType safexpayPaymentprocess......"+card_Type);

                            processorName= RBWallets.getString(card_Type);
                            transactionLogger.error("cardType safexpayPaymentprocess......"+processorName);

                        }
                        pg_id = processorName;
                        transactionLogger.error("pg_id  in live mode --->" + pg_id);
                        // pg_id=RB3.getString();
                        url = RB.getString("SALE_LIVE_URL");
                    }

                }

            //UPI
            else if (functions.isValueNull(payment_Card) && payment_Card.equalsIgnoreCase("UPI"))
            {
                transactionLogger.error("payment_card  UPI --- " + payment_Card);

                paymode = "UP";
                scheme = "7";
                if (isTest)
                {
                    transactionLogger.error("inside UPI test --");
                    pg_id = "63";
                    emi_months = "7";
                    url = RB.getString("SALE_TEST_URL");
                }
                else
                {
                    transactionLogger.error("inside NBI live --");
                    pg_id = gatewayAccount.getFromMid();
                    transactionLogger.error("pg_id  in live mode --->" + pg_id);
                    // pg_id=RB3.getString();
                    url = RB.getString("SALE_LIVE_URL");
                }

            }



            txn_details = ag_id.toString() + "|" + me_id.toString() + "|" + order_no.toString() + "|" + amount.toString() + "|" + country.toString() + "|" + currency.toString() + "|" + txn_type.toString() + "|" + success_url.toString() + "|" + failure_url.toString() + "|" + channel.toString();
            transactionLogger.error("Txn_Details-->" +"trackingid"+order_no+ txn_details);
            me_id = me_id.toString();
            pg_details = pg_id.toString() + "|" + paymode.toString() + "|" + scheme.toString() + "|" + emi_months.toString();
            transactionLogger.error("pg_details-->" +"trackingid"+order_no+ pg_details);
            card_details = card_no.toString() + "|" + exp_month.toString() + "|" + exp_year.toString() + "|" + cvv2.toString() + "|" + card_name.toString();
            cust_details = cust_name.toString() + "|" + email_id.toString() + "|" + mobile_no.toString() + "|" + unique_id.toString() + "|" + is_logged_in.toString();
            transactionLogger.error("cust_details-->" +"trackingid"+order_no+ cust_details);
            bill_details = bill_address.toString() + "|" + bill_city.toString() + "|" + bill_state.toString() + "|" + bill_country.toString() + "|" + bill_zip.toString();
            transactionLogger.error("Bill_details-->"+"trackingid"+order_no + bill_details);
            ship_details = ship_address + "|" + ship_city + "|" + ship_state + "|" + ship_country + "|" + ship_zip + "|" + ship_days + "|" + address_count;
            transactionLogger.error("Ship_details-->" +"trackingid"+order_no+ ship_details);
            item_details = item_count + "|" + item_value + "|" + item_category;
            transactionLogger.error("Item_details-->" +"trackingid"+order_no+ item_details);
            other_details = udf_1 + "|" + udf_2 + "|" + udf_3 + "|" + udf_4 + "|" + udf_5;
            transactionLogger.error("Other_details-->" +"trackingid"+order_no+ other_details);
            upi_details = vpa_address;
            transactionLogger.error("upi_details is -- >"+upi_details);

            String encrypted_txn_details = "";
            String encrypted_card_details = "";
            String encrypted_pg_details = "";
            String encrypted_cust_details = "";
            String encrypted_bill_details = "";
            String encrypted_ship_details = "";
            String encrypted_item_details = "";
            String encrypted_other_details = "";
            String encrypted_upi_details="";

            encrypted_txn_details = PayGateCryptoUtils.encrypt(txn_details, merchant_key.toString());
            encrypted_pg_details = PayGateCryptoUtils.encrypt(pg_details, merchant_key.toString());
            encrypted_cust_details = PayGateCryptoUtils.encrypt(cust_details, merchant_key.toString());
            encrypted_bill_details = PayGateCryptoUtils.encrypt(bill_details, merchant_key.toString());
            encrypted_ship_details = PayGateCryptoUtils.encrypt(ship_details, merchant_key.toString());
            encrypted_item_details = PayGateCryptoUtils.encrypt(item_details, merchant_key.toString());
            encrypted_other_details = PayGateCryptoUtils.encrypt(other_details, merchant_key.toString());
            encrypted_card_details = PayGateCryptoUtils.encrypt(card_details, merchant_key.toString());
            if(functions.isValueNull(upi_details))
                encrypted_upi_details = PayGateCryptoUtils.encrypt(upi_details,merchant_key.toString());

            transactionLogger.error("encrypted_Txn_Details is -->" + encrypted_txn_details);
            transactionLogger.error("encrypted_pg_details -->" + encrypted_pg_details);
            transactionLogger.error("encrypted_cust_details -->" + encrypted_cust_details);
            transactionLogger.error("encrypted_Bill_details -->" + encrypted_bill_details);
            transactionLogger.error("encrypted_Ship_details-->" + encrypted_ship_details);
            transactionLogger.error("encrypted_Item_details -->" + encrypted_item_details);
            transactionLogger.error("encrypted_Other_details-->" + encrypted_other_details);
            transactionLogger.error("encrypted_card_details-->" + encrypted_card_details);
            transactionLogger.error("encrypted_upi_details-->" + encrypted_upi_details);


            html = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" + url + "\">" +
                    "<input type=\"hidden\" name=\"me_id\" id=\"me_id\" value=\"" + me_id + "\">" +
                    "<input type=\"hidden\" name=\"txn_details\" id=\"txn_details\" value=\"" + encrypted_txn_details + "\">" +
                    "<input type=\"hidden\" name=\"pg_details\" id=\"pg_details\" value=\"" + encrypted_pg_details + "\">" +
                    "<input type=\"hidden\" name=\"card_details\" id=\"card_details\" value=\"" + encrypted_card_details + "\">" +
                    "<input type=\"hidden\" name=\"cust_details\" id=\"cust_details\" value=\"" + encrypted_cust_details + "\">" +
                    "<input type=\"hidden\" name=\"bill_details\" id=\"bill_details\" value=\"" + encrypted_bill_details + "\">" +
                    "<input type=\"hidden\" name=\"ship_details\" id=\"ship_details\" value=\"" + encrypted_ship_details + "\">" +
                    "<input type=\"hidden\" name=\"item_details\" id=\"item_details\" value=\"" + encrypted_item_details + "\">" +
                    "<input type=\"hidden\" name=\"other_details\" id=\"other_details\" value=\"" + encrypted_other_details + "\">" +
                    "<input type=\"hidden\" name=\"upi_details\" id=\"upi_details\" value=\"" + encrypted_upi_details + "\">" +
                    "</form>" +
                    "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

            transactionLogger.error("form---" + html);
            transactionLogger.error("Trackingid--------->" + commonValidatorVO.getTrackingid());
            transactionLogger.error("Authorization_code------>" + commRequestVO.getTransDetailsVO().getAuthorization_code());

          //  safexPayUtils.updateTransaction(commonValidatorVO.getTrackingid(),commRequestVO.getTransDetailsVO().getCustomerId());

            String CALL_EXECUTE_AFTER=RB.getString("CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL=RB.getString("CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC=RB.getString("MAX_EXECUTE_SEC");
            String isThreadAllowed=RB.getString("THREAD_CALL");
            transactionLogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionLogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncSafexPayQueryService(commonValidatorVO.getTrackingid(),accountID,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----", e);
        }
        return html.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside Safexpay Process --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.debug("inside getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside Rest flow SafexPayPaymentProcess Set 3DResponseVo-->");

        //String html = "";
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

            boolean isTest = gatewayAccount.isTest();
            Functions functions = new Functions();
            String merchant_key = gatewayAccount.getFRAUD_FTP_USERNAME();
            transactionLogger.error("merchant_key --- " + merchant_key);
            String payment_brand = commonValidatorVO.getPaymentBrand();
            transactionLogger.error("payment_brand is -->" + payment_brand);
            String processorName = commonValidatorVO.getProcessorName();
            transactionLogger.error("processorName--" + processorName);
            String card_Type = commonValidatorVO.getPaymentBrand();
            transactionLogger.error("card_type --- " + card_Type);
            String payment_Card = commonValidatorVO.getPaymentMode();
            transactionLogger.error("payment_Card --- " + payment_Card);
            //String payment_Type = GatewayAccountService.getPaymentMode(commonValidatorVO.getPaymentType());
            String url = "";
            String pg_id = "";
            String paymode = "";
            String scheme = "";
            String card_name = "";
            String emi_months = "";
            String mobile_num = "";
            if(functions.isValueNull(genericAddressDetailsVO.getPhone())){
                mobile_num =genericAddressDetailsVO.getPhone().replace(" ","");
            }
            else {
                mobile_num="9999999999";
            }
            String mobile_number = "";
            String mobile_no = "";
            if (mobile_num.contains("-"))
            {
                String mobile_No[] = mobile_num.split("\\-");
                mobile_number = mobile_No[0];
                mobile_no = mobile_No[1];
            }
            if(mobile_num.length()>10){

                mobile_no=mobile_num.substring(mobile_num.length() - 10);
            }
            else
            {
                mobile_no = mobile_num;
            }
            String unique_id = "";
            String is_logged_in = "";
            String country = "";

            //Ship_details
            String ship_address = "";
            String ship_city = "";
            String ship_state = "";
            String ship_country = "";
            String ship_zip = "";
            String ship_days = "";
            String address_count = "";

            // Item_details
            String item_count = "";
            String item_value = "";
            String item_category = "";
            String currency="";
            String vpa_address="";
            if(functions.isValueNull(commonValidatorVO.getVpa_address())){
                vpa_address=commonValidatorVO.getVpa_address();
            }
            else {
                vpa_address="";
            }

            // Other_details
            String udf_1 = "";
            String udf_2 = "";
            String udf_3 = "";
            String udf_4 = "";
            String udf_5 = "";


            String pg_details = "";
            String txn_details = "";
            String card_details = "";
            String cust_details = "";
            String bill_details = "";
            String ship_details = "";
            String item_details = "";
            String other_details = "";
            String upi_details="";

            String card_no = "";
            if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
            {
                card_no = genericCardDetailsVO.getCardNum();
            }
            else
            {
                card_no = "";
            }
            transactionLogger.debug("card_no --- " + functions.maskingPan(card_no));
            String exp_month = "";
            if (functions.isValueNull(genericCardDetailsVO.getExpMonth()))
            {
                exp_month = genericCardDetailsVO.getExpMonth();
            }
            else
            {
                exp_month = "";
            }
            transactionLogger.debug("exp_month --- " + functions.maskingNumber(exp_month));
            String exp_year = "";
            if (functions.isValueNull(genericCardDetailsVO.getExpYear()))
            {
                exp_year = genericCardDetailsVO.getExpYear();
            }
            else
            {
                exp_year = "";
            }
            transactionLogger.debug("exp_year --- " + functions.maskingNumber(exp_year));
            String cvv2 = "";
            if (functions.isValueNull(genericCardDetailsVO.getcVV()))
            {
                cvv2 = genericCardDetailsVO.getcVV();
            }
            else
            {
                cvv2 = "";
            }
            transactionLogger.debug("cvv2 --- " + functions.maskingNumber(cvv2));

            if (functions.isValueNull(genericAddressDetailsVO.getFirstname()))
            {
                if (functions.isValueNull(genericAddressDetailsVO.getLastname()))
                    card_name = genericAddressDetailsVO.getFirstname() + " " + genericAddressDetailsVO.getLastname(); // Name on the Card ..
                else
                    card_name = genericAddressDetailsVO.getFirstname();
            }
            else
            {
                card_name = "";
            }

            transactionLogger.debug("CardNme" + card_name);
            String cust_name = card_name;
            transactionLogger.debug("cust_name" + cust_name);
            String bill_address = "";
            String bill_city = "";
            String bill_state = "";
            String bill_country = "";
            String bill_zip = "";
            // Bill_details
            if (functions.isValueNull(genericAddressDetailsVO.getStreet()))
            {
                bill_address = genericAddressDetailsVO.getStreet();
            }
            else
            {
                bill_address = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getCity()))
            {
                bill_city = genericAddressDetailsVO.getCity();
            }
            else
            {
                bill_city = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getState()))
            {
                bill_state = genericAddressDetailsVO.getState();
            }
            else
            {
                bill_state = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getCountry()))
            {
                bill_country = genericAddressDetailsVO.getCountry();            }
            else
            {
                bill_country = "";
            }

            if (functions.isValueNull(genericAddressDetailsVO.getZipCode()))
            {
                bill_zip = genericAddressDetailsVO.getZipCode();            }
            else
            {
                bill_zip = "";
            }
            String email_id = "";
            if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
            {
                email_id = genericAddressDetailsVO.getEmail();
                transactionLogger.debug("email_id ---" + email_id);
            }
            String ag_id = "paygate";
            String txn_type = "SALE";
            String channel = "WEB"; // WEB or MOBILE
            String me_id = gatewayAccount.getMerchantId();
            transactionLogger.error("me_id ---" + me_id);
            String order_no = commonValidatorVO.getTrackingid();
            transactionLogger.error("order_no--" + order_no);
            String amount = commonValidatorVO.getTransDetailsVO().getAmount();
          /*  if (commonValidatorVO.getAddressDetailsVO().getCountry().length() == 2)
            {
                country = RB1.getString(genericAddressDetailsVO.getCountry());
            }
            else
            {
                country = "IND";
            }*/

            country = "IND";
             currency = commonValidatorVO.getTransDetailsVO().getCurrency();

            String termUrl = "";
            if (functions.isValueNull(merchantDetailsVO.getHostUrl()))
            {
                termUrl = "https://" + merchantDetailsVO.getHostUrl() + RB.getString("HOST_URL");
                transactionLogger.error("From HOST_URL notificationUrl ----" + termUrl);
            }
            else
            {
                termUrl = RB.getString("TERM_URL");
                transactionLogger.error("From RB notificationUrl ----" + termUrl);
            }

            String success_url = termUrl + order_no + "&status=success";
            String failure_url = termUrl + order_no + "&status=fail";
            transactionLogger.error("success_url  --" + success_url);
            transactionLogger.error("failure_url  --" + failure_url);

            // Step 1-
            if (functions.isValueNull(payment_Card) && (payment_Card.equalsIgnoreCase("CC") || payment_Card.equalsIgnoreCase("DC")))
            {
                transactionLogger.error("inside CC Or DC  --");
                paymode = payment_Card;

                if (isTest)
                {
                    transactionLogger.error("Inside isTest");
                    pg_id = "107";
                    emi_months = "6";
                    scheme = "2";
                    url = RB.getString("SALE_TEST_URL");
                }
                else
                {
                    transactionLogger.error("inside cc live-->");
                    if (currency.equals("INR"))
                    {
                        if(functions.isValueNull(gatewayAccount.getFromAccountId())){
                            transactionLogger.error("inside cc live-->"+pg_id);
                            pg_id = gatewayAccount.getFromAccountId();
                        }
                        else{
                            pg_id ="210";
                        }
                    }
                    if (currency.equals("USD"))
                    {
                        pg_id = "602";
                    }
                    emi_months = "";
                    url = RB.getString("SALE_LIVE_URL");

                    if (card_Type.equalsIgnoreCase("VISA"))
                        scheme = "1";
                    else if (card_Type.equalsIgnoreCase("MC"))
                        scheme = "2";
                    else if (card_Type.equalsIgnoreCase("MAESTRO"))
                        scheme = "5";
                    else if (card_Type.equalsIgnoreCase("RUPAY") || card_Type.equalsIgnoreCase("DISC"))
                        scheme = "6";
                }
            }
            else if (functions.isValueNull(payment_Card) && payment_Card.equalsIgnoreCase("NBI"))
            {
                transactionLogger.error("payment_card  netbanking --- " + payment_Card);

                paymode = "NB";
                scheme = "7";
                if (isTest)
                {
                    transactionLogger.error("inside NBI test --");
                    pg_id = "63";
                    emi_months = "7";
                    url = RB.getString("SALE_TEST_URL");
                }
                else
                {
                    transactionLogger.error("inside NBI live --");
                    pg_id = processorName; // todo
                    transactionLogger.error("pg_id  in live mode --->" + pg_id);
                    // pg_id=RB3.getString();
                    url = RB.getString("SALE_LIVE_URL");
                }

            }

            else if (functions.isValueNull(payment_Card) && payment_Card.equalsIgnoreCase("EWI"))
            {
                transactionLogger.error("paymentwallet of wallet is -->" + payment_Card+" processorName-->"+processorName);
                if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE())&&"1352".equalsIgnoreCase(processorName))
                {
                    paymode = gatewayAccount.getFRAUD_FTP_SITE();
                }else{
                    paymode = "WA";
                }

                scheme = "7";
                if (isTest)
                {
                    transactionLogger.error("inside EWI isTest -----");
                    pg_id = "60"; // 60
                    emi_months = "7";
                    url = RB.getString("SALE_TEST_URL");

                }
                else if (processorName.contains(RBWallets.getString("PAYTM")))
                {
                    transactionLogger.error("inside paytm condition---->");
                    paymode = "PT";
                    pg_id=processorName;
                    scheme="7";
                    url = RB.getString("SALE_LIVE_URL");

                }
                    else
                    {

                        pg_id = processorName; // todo
                        transactionLogger.error("pg_id  in live mode --->" + pg_id);
                        // pg_id=RB3.getString();
                        url = RB.getString("SALE_LIVE_URL");
                    }
            }

            //UPI
            else if (functions.isValueNull(payment_Card) && payment_Card.equalsIgnoreCase("UPI"))
            {
                transactionLogger.error("payment_card  UPI --- " + payment_Card);

                paymode = "UP";
                scheme = "7";
                if (isTest)
                {
                    transactionLogger.error("inside UPI test --");
                    pg_id = "63";
                    emi_months = "7";
                    url = RB.getString("SALE_TEST_URL");
                }
                else
                {
                    transactionLogger.error("inside NBI live --");
                    pg_id = gatewayAccount.getFromMid(); // todo
                    transactionLogger.error("pg_id  in live mode --->" + pg_id);
                    url = RB.getString("SALE_LIVE_URL");
                }
            }

            txn_details = ag_id.toString() + "|" + me_id.toString() + "|" + order_no.toString() + "|" + amount.toString() + "|" + country.toString() + "|" + currency.toString() + "|" + txn_type.toString() + "|" + success_url.toString() + "|" + failure_url.toString() + "|" + channel.toString();
            transactionLogger.error("Txn_Details-->"+"trackingid"+order_no + txn_details);
            me_id = me_id.toString();
            pg_details = pg_id.toString() + "|" + paymode.toString() + "|" + scheme.toString() + "|" + emi_months.toString();
            transactionLogger.error("pg_details-->"+"trackingid"+order_no + pg_details);
            card_details = card_no.toString() + "|" + exp_month.toString() + "|" + exp_year.toString() + "|" + cvv2.toString() + "|" + card_name.toString();

            cust_details = cust_name.toString() + "|" + email_id.toString() + "|" + mobile_no.toString() + "|" + unique_id.toString() + "|" + is_logged_in.toString();
            transactionLogger.error("cust_details-->"+"trackingid"+order_no + cust_details);
            bill_details = bill_address.toString() + "|" + bill_city.toString() + "|" + bill_state.toString() + "|" + bill_country.toString() + "|" + bill_zip.toString();
            transactionLogger.error("Bill_details-->" +"trackingid"+order_no+ bill_details);
            ship_details = ship_address + "|" + ship_city + "|" + ship_state + "|" + ship_country + "|" + ship_zip + "|" + ship_days + "|" + address_count;
            transactionLogger.error("Ship_details-->"+"trackingid"+order_no + ship_details);
            item_details = item_count + "|" + item_value + "|" + item_category;
            transactionLogger.error("Item_details-->"+"trackingid"+order_no + item_details);
            other_details = udf_1 + "|" + udf_2 + "|" + udf_3 + "|" + udf_4 + "|" + udf_5;
            transactionLogger.error("Other_details-->"+"trackingid"+order_no + other_details);
            upi_details = vpa_address;
            transactionLogger.error("upi_details-- >"+"trackingid"+order_no+upi_details);


            String encrypted_txn_details = "";
            String encrypted_card_details = "";
            String encrypted_pg_details = "";
            String encrypted_cust_details = "";
            String encrypted_bill_details = "";
            String encrypted_ship_details = "";
            String encrypted_item_details = "";
            String encrypted_other_details = "";
            String encrypted_upi_details="";

            encrypted_txn_details = PayGateCryptoUtils.encrypt(txn_details, merchant_key.toString());
            encrypted_pg_details = PayGateCryptoUtils.encrypt(pg_details, merchant_key.toString());
            encrypted_cust_details = PayGateCryptoUtils.encrypt(cust_details, merchant_key.toString());
            encrypted_bill_details = PayGateCryptoUtils.encrypt(bill_details, merchant_key.toString());
            encrypted_ship_details = PayGateCryptoUtils.encrypt(ship_details, merchant_key.toString());
            encrypted_item_details = PayGateCryptoUtils.encrypt(item_details, merchant_key.toString());
            encrypted_other_details = PayGateCryptoUtils.encrypt(other_details, merchant_key.toString());
            encrypted_card_details = PayGateCryptoUtils.encrypt(card_details, merchant_key.toString());
            if (functions.isValueNull(upi_details))
            {
                encrypted_upi_details = PayGateCryptoUtils.encrypt(upi_details,merchant_key.toString());
            }
            transactionLogger.error("encrypted_Txn_Details is -->" + encrypted_txn_details);
            transactionLogger.error("encrypted_pg_details -->" + encrypted_pg_details);
            transactionLogger.error("encrypted_cust_details -->" + encrypted_cust_details);
            transactionLogger.error("encrypted_Bill_details -->" + encrypted_bill_details);
            transactionLogger.error("encrypted_Ship_details-->" + encrypted_ship_details);
            transactionLogger.error("encrypted_Item_details -->" + encrypted_item_details);
            transactionLogger.error("encrypted_Other_details-->" + encrypted_other_details);
            transactionLogger.error("encrypted_card_details-->" + encrypted_card_details);
            transactionLogger.error("encrypted_upi_details-->" + encrypted_upi_details);


            String  html = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" + url + "\">" +
                    "<input type=\"hidden\" name=\"me_id\" id=\"me_id\" value=\"" + me_id + "\">" +
                    "<input type=\"hidden\" name=\"txn_details\" id=\"txn_details\" value=\"" + encrypted_txn_details + "\">" +
                    "<input type=\"hidden\" name=\"pg_details\" id=\"pg_details\" value=\"" + encrypted_pg_details + "\">" +
                    "<input type=\"hidden\" name=\"card_details\" id=\"card_details\" value=\"" + encrypted_card_details + "\">" +
                    "<input type=\"hidden\" name=\"cust_details\" id=\"cust_details\" value=\"" + encrypted_cust_details + "\">" +
                    "<input type=\"hidden\" name=\"bill_details\" id=\"bill_details\" value=\"" + encrypted_bill_details + "\">" +
                    "<input type=\"hidden\" name=\"ship_details\" id=\"ship_details\" value=\"" + encrypted_ship_details + "\">" +
                    "<input type=\"hidden\" name=\"item_details\" id=\"item_details\" value=\"" + encrypted_item_details + "\">" +
                    "<input type=\"hidden\" name=\"other_details\" id=\"other_details\" value=\"" + encrypted_other_details + "\">" +
                    "<input type=\"hidden\" name=\"upi_details\" id=\"upi_details\" value=\"" + encrypted_upi_details + "\">" +
                    "</form>" +
                    "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

            transactionLogger.error("rest form ---" + html);



            AsyncParameterVO asyncParameterVO = null;
            transactionLogger.error("inside SafexPayPaymentProcess payment process --->" + url);
            // asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
            directKitResponseVO.setBankRedirectionUrl(url);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("creditcard_checkout");
            asyncParameterVO.setValue(url);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("me_id");
            asyncParameterVO.setValue(me_id);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("txn_details");
            asyncParameterVO.setValue(encrypted_txn_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("pg_details");
            asyncParameterVO.setValue(encrypted_pg_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("card_details");
            asyncParameterVO.setValue(encrypted_card_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);


            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("cust_details");
            asyncParameterVO.setValue(encrypted_cust_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);


            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("bill_details");
            asyncParameterVO.setValue(encrypted_bill_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("ship_details");
            asyncParameterVO.setValue(encrypted_ship_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("item_details");
            asyncParameterVO.setValue(encrypted_item_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("other_details");
            asyncParameterVO.setValue(encrypted_other_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("upi_details");
            asyncParameterVO.setValue(encrypted_upi_details);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----", e);
        }
        //return html.toString();
    }

    // payout calling extension vo through commonpayment process page.

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error("inside payout vo params etension -- >");
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        //commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());


        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        //requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionLogger.debug("Entering ActionEntry for SafexPayPaymentGateway");
        System.out.println("entering");

        int results=0;
        Connection cn = null;

        try
        {
            String ifsc="";
            String bankaccount="";
            String fullname="";
            String bankRefNo="";
            String spkRefNo="";
            if(responseVO!=null){
                ifsc=responseVO.getIfsc();
                bankaccount=responseVO.getBankaccount();
                fullname=responseVO.getFullname();
                bankRefNo=responseVO.getBankRefNo();
                spkRefNo=responseVO.getSpkRefNo();
            }
            if(commRequestVO!=null){
            ifsc=commRequestVO.getTransDetailsVO().getBankIfsc();
            bankaccount=commRequestVO.getTransDetailsVO().getBankAccountNo();
            fullname=commRequestVO.getTransDetailsVO().getCustomerBankAccountName();

            }

            cn = Database.getConnection();
            String sql = "insert into transaction_safexpay_details(trackingId,fullname,bankaccount,ifsc,amount,status,bankRefNo,spkRefNo,dtstamp) values (?,?,?,?,?,?,?,?,unix_timestamp(now()))";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, fullname);
            pstmt.setString(3, bankaccount);
            pstmt.setString(4, ifsc);
            pstmt.setString(5,amount);
            pstmt.setString(6,status);
            pstmt.setString(7,bankRefNo);
            pstmt.setString(8,spkRefNo);
            results = pstmt.executeUpdate();
            transactionLogger.error("SqlQuery Safex-----" + pstmt);

        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }
   /* public static HashMap getPreviousTransactionDetails(String trackingid,String status)
    {
        String approval = "";
        Connection conn = null;
        PreparedStatement ps = null;
        HashMap detailHash = new HashMap();
        try
        {
            conn = Database.getConnection();
            String query = "select responsehashinfo,responsetransactionid from transaction_common_details where trackingid='" + trackingid + "' AND status IN("+status+") AND responsehashinfo IS NOT NULL AND responsehashinfo!='' LIMIT 1";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                detailHash.put("responsehashinfo",rs.getString("responsehashinfo"));
                detailHash.put("responsetransactionid",rs.getString("responsetransactionid"));
                transactionLogger.error("responsehashinfo or approval------" + detailHash);
            }
            transactionLogger.error("Sql Query-----" + ps);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError-----" + e);
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException-----" + se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return detailHash;
    }


    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        Functions functions= new Functions();
        transactionLogger.debug("Inside safexpayPaymentProcess setRefundVOParamsextension -----");
        int trackingid = refundRequest.getTrackingId();
        HashMap detailHash = getPreviousTransactionDetails(String.valueOf(trackingid),"'authsuccessful','capturesuccess'");
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        if(functions.isValueNull(detailHash.get("responsehashinfo").toString()))
            transactionDetailsVO.setResponseHashInfo((String)detailHash.get("responsehashinfo"));
        if(functions.isValueNull(detailHash.get("responsetransactionid").toString()))
            transactionDetailsVO.setPreviousTransactionId((String)detailHash.get("responsetransactionid"));
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }
*/
}
