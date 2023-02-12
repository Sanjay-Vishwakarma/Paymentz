package com.payment.euroeximbank;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.QikPayGatewayLogger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.paygate.ag.common.utils.PayGateCryptoUtils;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.onepay.OnepayUtils;
import com.payment.qikpay.QikPayUtils;
import com.payment.safexpay.SafexPayUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 12/28/2021.
 */
public class EuroEximBankPaymentGateway extends AbstractPaymentGateway{

        private static TransactionLogger transactionLogger  = new TransactionLogger(EuroEximBankPaymentGateway.class.getName());
        public static final String GATEWAY_TYPE             = "eebpay";
        public static final String CARD                     = "CARD";
        private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.euroeximbank");
        private final static ResourceBundle RB_NB           = LoadProperties.getProperty("com.directi.pg.EEBANKS");
        private final static ResourceBundle RBWallets = LoadProperties.getProperty("com.directi.pg.EEWALLET");


        @Override
        public String getMaxWaitDays()
        {
            return null;
        }

        public EuroEximBankPaymentGateway(String accountId)
        {
            this.accountId = accountId;
        }

        public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
        {
            transactionLogger.error("Entering processSale of EuroEximBankPaymentGateway......");

            CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
            Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
            Functions functions                             = new Functions();

            CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
            CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
            CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
            GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
            String payment_brand                            = transactionDetailsVO.getCardType();
            String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
            String html = "";
            try
            {
                boolean isTest = gatewayAccount.isTest();
                String merchant_key = gatewayAccount.getFRAUD_FTP_USERNAME();
                transactionLogger.error("merchant_key --- " + merchant_key);
                String processorName = transactionDetailsVO.getCustomerBankId();
                transactionLogger.error("processorName--" + processorName);
                String url = "";
                String pg_id = "";
                String paymode = "";
                String scheme = "";
                String card_name = "";
                String emi_months = "";

                String mobile_num = "";

                if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                    mobile_num =commAddressDetailsVO.getPhone().replace(" ","");
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
                String vpa_address=commRequestVO.getCustomerId();


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
                if (functions.isValueNull(commCardDetailsVO.getCardNum()))
                {
                    card_no = commCardDetailsVO.getCardNum();
                }
                else
                {
                    card_no = "";
                }
                transactionLogger.debug("card_no --- " + functions.maskingPan(card_no));
                String exp_month = "";
                if (functions.isValueNull(commCardDetailsVO.getExpMonth()))
                {
                    exp_month = commCardDetailsVO.getExpMonth();
                }
                else
                {
                    exp_month = "";
                }
                transactionLogger.debug("exp_month --- " +functions.maskingNumber( exp_month));
                String exp_year = "";
                if (functions.isValueNull(commCardDetailsVO.getExpYear()))
                {
                    exp_year = commCardDetailsVO.getExpYear();
                }
                else
                {
                    exp_year = "";
                }
                transactionLogger.debug("exp_year --- " + functions.maskingNumber(exp_year));
                String cvv2 = "";
                if (functions.isValueNull(commCardDetailsVO.getcVV()))
                {
                    cvv2 = commCardDetailsVO.getcVV();
                }
                else
                {
                    cvv2 = "";
                }
                transactionLogger.debug("cvv2 --- " + functions.maskingNumber(cvv2));

                if(!"UPI".equalsIgnoreCase(payment_brand))
                {
                    if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                    {
                        if (functions.isValueNull(commAddressDetailsVO.getLastname()))
                            card_name = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname(); // Name on the Card ..
                        else
                            card_name = commAddressDetailsVO.getFirstname();
                    }
                    else
                    {
                        card_name = "";
                    }
                }
                transactionLogger.debug("CardNme" + card_name);
                String cust_name="";
                transactionLogger.debug("CardNme" + card_name);
                if(functions.isValueNull(payment_Card) && (payment_Card.equalsIgnoreCase("CC") || payment_Card.equalsIgnoreCase("DC"))){
                    cust_name = card_name;
                }
                else{
                    card_name="";
                }
                String bill_address = "";
                String bill_city = "";
                String bill_state = "";
                String bill_country = "";
                String bill_zip = "";
                // Bill_details
                if (functions.isValueNull(commAddressDetailsVO.getStreet()))
                {
                    bill_address = commAddressDetailsVO.getStreet();
                }
                else
                {
                    bill_address = "";
                }

                if (functions.isValueNull(commAddressDetailsVO.getCity()))
                {
                    bill_city = commAddressDetailsVO.getCity();
                }
                else
                {
                    bill_city = "";
                }

                if (functions.isValueNull(commAddressDetailsVO.getState()))
                {
                    bill_state = commAddressDetailsVO.getState();
                }
                else
                {
                    bill_state = "";
                }

                if (functions.isValueNull(commAddressDetailsVO.getCountry()))
                {
                    bill_country = commAddressDetailsVO.getCountry();            }
                else
                {
                    bill_country = "";
                }

                if (functions.isValueNull(commAddressDetailsVO.getZipCode()))
                {
                    bill_zip = commAddressDetailsVO.getZipCode();            }
                else
                {
                    bill_zip = "";
                }

                String email_id = "";
                if (functions.isValueNull(commAddressDetailsVO.getEmail()))
                {
                    email_id = commAddressDetailsVO.getEmail();
                    transactionLogger.debug("email_id ---" + email_id);
                }
                String ag_id = "paygate";
                String txn_type = "SALE";
                String channel = "WEB"; // WEB or MOBILE
                String me_id = gatewayAccount.getMerchantId();
                transactionLogger.error("me_id ---" + me_id);
                String order_no =trackingID;
                transactionLogger.error("order_no--" + order_no);
                String amount = transactionDetailsVO.getAmount();

                country = "IND";
                currency = transactionDetailsVO.getCurrency();

                String termUrl = "";
                if (functions.isValueNull(commMerchantVO.getHostUrl()))
                {
                    termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
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
                            pg_id = "210";
                        }
                        if (currency.equals("USD"))
                        {
                            pg_id = "602";
                        }
                        emi_months = "";
                        url = RB.getString("SALE_LIVE_URL");

                        if (payment_brand.equalsIgnoreCase("VISA"))
                            scheme = "1";
                        else if (payment_brand.equalsIgnoreCase("MC"))
                            scheme = "2";
                        else if (payment_brand.equalsIgnoreCase("MAESTRO"))
                            scheme = "5";
                        else if (payment_brand.equalsIgnoreCase("RUPAY") || payment_brand.equalsIgnoreCase("DISC"))
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
                    transactionLogger.error("payment_card of wallet is -->" + payment_Card);
                    paymode = "WA";
                    scheme = "7";
                    if (isTest)
                    {
                        transactionLogger.error("inside EWI isTest -----");
                        pg_id = "60"; //60
                        emi_months = "7";
                        url = RB.getString("SALE_TEST_URL");

                    }
                    else if (processorName.contains(RBWallets.getString("Paytm")))
                    {
                        transactionLogger.error("inside paytm condition---->");
                        paymode="PT";
                        scheme="7";
                        pg_id =processorName;
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
                        transactionLogger.error("pg_id  in live mode UPI --->" + pg_id);
                        // pg_id=RB3.getString();
                        url = RB.getString("SALE_LIVE_URL");
                    }
                }
                HashMap<String, String> requestMap = new HashMap();
                txn_details = ag_id.toString() + "|" + me_id.toString() + "|" + order_no.toString() + "|" + amount.toString() + "|" + country.toString() + "|" + currency.toString() + "|" + txn_type.toString() + "|" + success_url.toString() + "|" + failure_url.toString() + "|" + channel.toString();
                transactionLogger.error("Txn_Details-->"+"trackingid"+order_no + txn_details);
                me_id = me_id.toString();
                pg_details = pg_id.toString() + "|" + paymode.toString() + "|" + scheme.toString() + "|" + emi_months.toString();
                transactionLogger.error("pg_details-->" +"trackingid"+order_no+ pg_details);
                card_details = card_no.toString() + "|" + exp_month.toString() + "|" + exp_year.toString() + "|" + cvv2.toString() + "|" + card_name.toString();
                cust_details = cust_name.toString() + "|" + email_id.toString() + "|" + mobile_no.toString() + "|" + unique_id.toString() + "|" + is_logged_in.toString();
                transactionLogger.error("cust_details-->"+"trackingid"+order_no + cust_details);
                bill_details = bill_address.toString() + "|" + bill_city.toString() + "|" + bill_state.toString() + "|" + bill_country.toString() + "|" + bill_zip.toString();
                transactionLogger.error("Bill_details-->"+"trackingid"+order_no + bill_details);
                ship_details = ship_address + "|" + ship_city + "|" + ship_state + "|" + ship_country + "|" + ship_zip + "|" + ship_days + "|" + address_count;
                transactionLogger.error("Ship_details-->"+"trackingid"+order_no + ship_details);
                item_details = item_count + "|" + item_value + "|" + item_category;
                transactionLogger.error("Item_details-->"+"trackingid"+order_no + item_details);
                other_details = udf_1 + "|" + udf_2 + "|" + udf_3 + "|" + udf_4 + "|" + udf_5;
                transactionLogger.error("Other_details-->"+"trackingid"+order_no + other_details);
                upi_details = vpa_address;
                transactionLogger.error("upi_details is -- >"+"trackingid"+order_no+upi_details);


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
                {
                    encrypted_upi_details = PayGateCryptoUtils.encrypt(upi_details, merchant_key.toString());
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
                requestMap.put("me_id", me_id);
                requestMap.put("txn_details", encrypted_txn_details);
                requestMap.put("pg_details", encrypted_pg_details);
                requestMap.put("card_details", encrypted_card_details);
                requestMap.put("cust_details", encrypted_cust_details);
                requestMap.put("bill_details", encrypted_bill_details);
                requestMap.put("ship_details", encrypted_bill_details);
                requestMap.put("item_details", encrypted_item_details);
                requestMap.put("other_details", other_details);
                requestMap.put("upi_details", encrypted_upi_details);

                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setUrlFor3DRedirect(url);
                commResponseVO.setRequestMap(requestMap);
            }
            catch (Exception e)
            {
                transactionLogger.error("Exception -----", e);
            }
      return commResponseVO;
        }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in Safexpay ---- ");
        String html = "";
        PaymentManager paymentManager = new PaymentManager();
        Comm3DResponseVO transRespDetails = null;
        EuroEximBankUtils euroEximBankUtils=new EuroEximBankUtils();
        EuroEximBankPaymentProcess euroEximBankPaymentProcess=new EuroEximBankPaymentProcess();
        CommRequestVO commRequestVO = euroEximBankUtils.getPayaidPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        try
        {
            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = euroEximBankPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails);
                transactionLogger.error("automatic redirect safexpay form -- >>"+html);

            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in SafexPaypaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        CommResponseVO commResponseVO =  new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        String transaction_status="";
        boolean isTest=gatewayAccount.isTest();
        Functions functions= new Functions();
        String ag_id="Paygate";
        String me_id=gatewayAccount.getMerchantId();
        String merchant_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        // String trackingId=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String order_no = commRequestVO.getTransDetailsVO().getOrderId();
        transactionLogger.error("commRequestVO.getTransDetailsVO().getPreviousTransactionId()---------------->"+commRequestVO.getTransDetailsVO().getOrderId());
        // String Encrypted_TrackingId_Number = PayGateCryptoUtils.encrypt(trackingId, merchant_key);
        transactionLogger.error("PayGateCryptoUtils.encrypt(ag_ref,merchant_key)----------------------------->"+PayGateCryptoUtils.encrypt(order_no,merchant_key));
        transactionLogger.error("order_no------------------>"+order_no);

        String Encrypted_Payment_Id_Number = PayGateCryptoUtils.encrypt(order_no,merchant_key);
        String ag_ref="";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject .put("ag_id",ag_id);
            jsonObject.put("me_id",me_id);
            // jsonObject.put("order_no",Encrypted_TrackingId_Number);
            jsonObject.put("order_no",Encrypted_Payment_Id_Number);
            jsonObject.put("ag_ref","");
            transactionLogger.error("inquiryRequest-----"+jsonObject.toString());
            String inquiryResponse="";

            if(isTest){
                inquiryResponse= EuroEximBankUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_TEST_URL"), jsonObject.toString());
            }else {
                inquiryResponse=EuroEximBankUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_LIVE_URL"),jsonObject.toString());
            }
            transactionLogger.error("inquiryResponse-----"+inquiryResponse);
            String decrypted_res = PayGateCryptoUtils.decrypt(inquiryResponse,merchant_key);
            transactionLogger.error("decrypted_res is -->"+decrypted_res);
            if(functions.isValueNull(decrypted_res)&&decrypted_res.contains("{"))
            {
                JSONObject jsonObject1 = new JSONObject(decrypted_res);
                if (jsonObject1 !=null)
                {
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("txn_response");
                    {
                        // String order_no="";
                        String amount="";
                        String country="";
                        String currency="";
                        String txn_date="";
                        String status="";
                        String res_code="";
                        String res_message="";
                        String ag_ref2="";
                        String res_message2="";

                        if (jsonObject2.has("me_id"))
                        {
                            me_id = String.valueOf(jsonObject2.get("me_id"));
                        }
                        if (jsonObject2.has("currency"))
                        {
                            currency = jsonObject2.getString("currency");
                        }
                        if (jsonObject2.has("res_code"))
                        {
                            res_code = jsonObject2.getString("res_code");
                        }
                        if (jsonObject2.has("ag_ref"))
                        {
                            ag_ref = jsonObject2.getString("ag_ref");
                        }
                        if (jsonObject2.has("status"))
                        {
                            status = jsonObject2.getString("status");
                        }
                        if (jsonObject2.has("amount"))
                        {
                            amount = jsonObject2.getString("amount");
                        }
                        if (jsonObject2.has("txn_date"))
                        {
                            txn_date = jsonObject2.getString("txn_date");
                        }
                        if (jsonObject2.has("res_message"))
                        {
                            res_message = jsonObject2.getString("res_message");
                        }

                        if("Successful".equalsIgnoreCase(status)){
                            commResponseVO.setTransactionStatus("success");
                            commResponseVO.setRemark(status);
                            commResponseVO.setBankTransactionDate(txn_date);
                            commResponseVO.setMerchantId(me_id);
                            commResponseVO.setTransactionId(ag_ref);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAuthCode(res_code);

                        }
                        else if("Pending".equalsIgnoreCase(status)){
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark(status);
                            commResponseVO.setBankTransactionDate(txn_date);
                            commResponseVO.setMerchantId(me_id);
                            commResponseVO.setTransactionId(ag_ref);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAuthCode(res_code);

                        }
                        else if("Failed".equalsIgnoreCase(status)||"Aborted".equalsIgnoreCase(status)){
                            commResponseVO.setTransactionStatus("fail");
                            commResponseVO.setRemark(status);
                            commResponseVO.setBankTransactionDate(txn_date);
                            commResponseVO.setMerchantId(me_id);
                            commResponseVO.setTransactionId(ag_ref);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAuthCode(res_code);

                        }
                        else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                        }
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
            }

        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }

        return commResponseVO;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside EuroEximBankUtils processRefund-----");
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        CommResponseVO commResponseVO =  new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        Functions functions= new Functions();
        String ag_id="Paygate";
        String me_id=gatewayAccount.getMerchantId();
        String merchant_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        String ag_ref=transactionDetailsVO.getPreviousTransactionId();
        String refund_amount=transactionDetailsVO.getAmount();
        String refund_reason=commRequestVO.getTransDetailsVO().getOrderDesc();
        PayGateCryptoUtils payGateCryptoUtils= new PayGateCryptoUtils();

        String Encrypted_AG_REF_Number = payGateCryptoUtils.encrypt(ag_ref, merchant_key);
        String Encrypted_Refund_Amount = payGateCryptoUtils.encrypt(refund_amount, merchant_key);
        String Encrypted_Refund_Reason = PayGateCryptoUtils.encrypt(refund_reason,merchant_key);

        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ag_id", ag_id);
            jsonObject.put("me_id", me_id);
            jsonObject.put("ag_ref", Encrypted_AG_REF_Number);
            jsonObject.put("refund_amount", Encrypted_Refund_Amount);
            jsonObject.put("refund_reason", Encrypted_Refund_Reason);

            transactionLogger.error("refundRequest-----" + jsonObject.toString());

            String refundResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside EuroEximBankUtils Test Refund --->");
                refundResponse = EuroEximBankUtils.doPostHttpUrlConnection(RB.getString("REFUND_TEST_URL"), jsonObject.toString());
            }
            else
            {
                transactionLogger.error("inside EuroEximBankUtils Live Refund --->");
                refundResponse = EuroEximBankUtils.doPostHttpUrlConnection(RB.getString("REFUND_LIVE_URL"), jsonObject.toString());
            }
            transactionLogger.error("refundResponse-----" + refundResponse);
            if (functions.isValueNull(refundResponse) && refundResponse.contains("{"))
            {
                JSONObject jsonObject1 = new JSONObject(refundResponse);
                if (jsonObject1 != null)
                {
                    String transactionId = "";
                    String txn_date = "";
                    String Response_refund_amount = "";
                    String currency = "";
                    String status = "";
                    String res_code = "";
                    String res_message = "";
                    String error_details = "";

                    if (jsonObject1.has("ag_ref"))
                    {
                        transactionId = jsonObject1.getString("ag_ref");
                    }
                    if (jsonObject1.has("txn_date"))
                    {
                        txn_date = jsonObject1.getString("txn_date");
                    }
                    if (jsonObject1.has("refund_amount"))
                    {
                        Response_refund_amount = jsonObject1.getString("refund_amount");
                    }
                    if (jsonObject1.has("currency"))
                    {
                        currency = jsonObject1.getString("currency");
                    }
                    if (jsonObject1.has("status"))
                    {
                        status = jsonObject1.getString("status");
                    }
                    if (jsonObject1.has("res_code"))
                    {
                        res_code = jsonObject1.getString("res_code");
                    }
                    if (jsonObject1.has("res_message"))
                    {
                        res_message = jsonObject1.getString("res_message");
                    }
                    if (jsonObject1.has("error_details"))
                    {
                        error_details = jsonObject1.getString("error_details");
                    }

                    if (res_code.equalsIgnoreCase("00000"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setDescription(status);
                        commResponseVO.setTransactionStatus("Successful");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(error_details);
                        commResponseVO.setTransactionStatus("Failed");
                    }
                    commResponseVO.setRemark(res_message);
                    commResponseVO.setBankTransactionDate(txn_date);
                    commResponseVO.setTransactionId(transactionId);
                    commResponseVO.setAmount(Response_refund_amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setErrorCode(res_code);
                    commResponseVO.setTransactionType(status);
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionStatus("Failed");
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EuroEximBankUtils.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }


}
