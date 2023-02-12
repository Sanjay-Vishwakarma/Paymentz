package com.payment.payg;

import com.directi.pg.*;
import com.directi.pg.Base64;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.manager.vo.ProductDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.MailPlaceHolder;
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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class PayGPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(PayGPaymentGateway.class.getName());
    //private static PayGTransactionLogger transactionlogger  = new PayGTransactionLogger(PayGPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "payg";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.paygpay");
    private final static ResourceBundle RB_WL           = LoadProperties.getProperty("com.directi.pg.PGWALLETS");
    private  static ResourceBundle rbAmount             =null;
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public PayGPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of PayGPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        PayGUtils payGUtils=new PayGUtils();
        boolean isTest          = gatewayAccount.isTest();
        String REQUEST_URL      = "";
        String requestString    = "";
        String PaymentType      = "";

        String RETURN_URL           = RB.getString("RETURN_URL")+trackingID;
        String MerchantKeyId        = gatewayAccount.getMerchantId();
        String AuthenticationKey    = gatewayAccount.getFRAUD_FTP_USERNAME();
        String AuthenticationToken  = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String aesKey  = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String websiteName  = gatewayAccount.getFRAUD_FTP_PATH();
        String UniqueRequestId      = trackingID;
        String autoRedirect      =commRequestVO.getAutoRedirectFlag();


        //CustomerData
        String MobileNo     = "9999999999";
        /*String FirstName    = "";
        String LastName     = "";
        String Email        = "";
        String BillingAddress   = "";
        String BillingCity      = "";
        String BillingState     = "";
        String BillingZipCode   = "";
        boolean EmailReceipt    = false;*/

        //OrderAmountData
        String OrderAmount  = transactionDetailsVO.getAmount();
        String OrderType    = "";
        String OrderId      = trackingID;
        String OrderStatus  = "Initiating";

        //IntegrationData
        String UserName         = "";
        String Source           = "";
        String IntegrationType  = "1";
        String HashData         = "";
        String PlatformId       = "";
        String IpAddress        = "192.168.0.1";
        String BankName         = "";
        String cardType         = transactionDetailsVO.getCardType();

        //Product Data
        String PaymentReason = gatewayAccount.getFRAUD_FTP_SITE();


        Map mapData         = new HashMap();
        Map tranData        = new HashMap();
        Map EncryptionData  = new HashMap();
        Map<String, String> bankdetailsHM       = null;
        Map<String, String> requestMap  = new HashMap<String, String>();
        SimpleDateFormat dt             = new SimpleDateFormat("MMddyyyy");

        PaymentType     = PayGUtils.getPaymentType(payment_Card);
        String Vpa      = commRequestVO.getCustomerId();

        String brandName        = GatewayAccountService.getCardType(cardType);
        BankName        = transactionDetailsVO.getCustomerBankId();
        Gson gson       = null;
        transactionlogger.error(" cardType PayGPaymentGateway......"+trackingID+"::"+brandName);

        String  CardNo          = "";
        String  ExpiryDate      = "";
        String  Cvv             = commCardDetailsVO.getcVV();
        String  ExpYear         = "";
        String NameOnCard = "";
        String EncryptionType   = "";
        String CUST_EMAIL    = "";
        HashMap productAmountMap=new HashMap();
        try
        {
            ExpYear             = PayGUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            ExpiryDate          = commCardDetailsVO.getExpMonth()+""+ExpYear;
            String SaltKey          = String.format("%04d", new Random().nextInt(10000));
            List<Integer> priceList = new ArrayList<Integer>();
            List <ProductDetailsVO>productList  = new ArrayList<>();
            rbAmount= LoadProperties.getProperty("com.directi.pg."+websiteName);
        /*    Enumeration<String> keys            = rbAmount.getKeys();
            while(keys.hasMoreElements()){
                int keyAmount = Integer.parseInt(keys.nextElement());
                priceList.add(keyAmount);
            }*/

            if(commCardDetailsVO.getCardNum() != null){
                CardNo = PayGUtils.getCardNumber(commCardDetailsVO.getCardNum(),0,6)+"|"+MerchantKeyId+"|"+PayGUtils.getCardNumber(commCardDetailsVO.getCardNum(),6,12)+"|"+aesKey+"|"+PayGUtils.getCardNumber(commCardDetailsVO.getCardNum(),12,16)+"|"+SaltKey;
                CardNo = Base64.encode(CardNo.getBytes("utf-8"));

            }

            if(isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
            {
                IpAddress =commAddressDetailsVO.getCardHolderIpAddress();

            }
            transactionlogger.error("IpAddress ------> " + IpAddress);

            if(functions.isValueNull(commAddressDetailsVO.getPhone())) {

                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    MobileNo = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                else{
                    MobileNo = commAddressDetailsVO.getPhone();

                }
                if(MobileNo.length()>10){

                    MobileNo=MobileNo.substring(MobileNo.length() - 10);
                }
                else{
                    MobileNo = commAddressDetailsVO.getPhone();

                }

            }
            else{
                MobileNo=PayGUtils.geDummyMobileNo();
            }
            if(functions.isValueNull(transactionDetailsVO.getToId())&&"10036".equalsIgnoreCase(transactionDetailsVO.getToId())){
                MobileNo=PayGUtils.geDummyMobileNo();

            }

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                NameOnCard  = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            else {
                NameOnCard  = "customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
                CUST_EMAIL   = commAddressDetailsVO.getEmail();
            else {
                CUST_EMAIL  = "customer@gmail.com";
            }

            transactionlogger.error("REQUEST_URL " + trackingID + " " + REQUEST_URL + " IpAddress::" + IpAddress + " PaymentType::"+PaymentType);
            // product mail
        /*    String amount  = payGUtils.getAmount(OrderAmount);
            productList         = payGUtils.getProductAndPricelist((ArrayList<Integer>) priceList, Integer.parseInt(amount));
            transactionlogger.error("productList -->"+productList.size());

            for(ProductDetailsVO productDetailsVO  : productList){
                transactionlogger.error("ProductAmount-->  "+trackingID+" "+productDetailsVO.getProductAmount());
                transactionlogger.error("ProductName-->  " +trackingID+" "+ productDetailsVO.getProductName());
                transactionlogger.error("ProductUnit-->  " +trackingID+" "+ productDetailsVO.getProductUnit());
                transactionlogger.error("ProductSKU--> " +trackingID+" "+ productDetailsVO.getSKU());

                if(!functions.isValueNull(productDetailsVO.getShippingAmount())){
                    productDetailsVO.setShippingAmount("0.00");
                }
                transactionlogger.error("ShippingAmount-->"+productDetailsVO.getShippingAmount());
            }

            boolean flag    = payGUtils.insertProductDetails(productList,OrderId);
            transactionlogger.error("after insert flag-->"+flag);
            transactionlogger.error("transactionDetailsVO.getToId() -->"+transactionDetailsVO.getToId());
            if(flag){

                Hashtable values = new Hashtable();
                Date d           = new Date();

                values.put("memberid",transactionDetailsVO.getToId());
                values.put("custname",NameOnCard);
                values.put("TIME",d.toString());
                values.put("invoiceno",OrderId);
                values.put("orderid",OrderId);
                values.put("orderdesc",OrderId);
                values.put("amount",OrderAmount);
                values.put("currency","INR");
                values.put("custname",NameOnCard);
                values.put("companyname","gatewayName");
                values.put("sitename","puma.com");
                values.put("lateFee", "0.00");
                values.put("custemail", CUST_EMAIL);
                values.put("ctoken","ctokenctokenctokenctokenctokenctokenctokenctokenctokenctoken");
                values.put("TERMINALID", "1234");
                values.put("contact_emails", CUST_EMAIL);
                values.put("TERMINALID", "1234");
                values.put("paymentterms","paymentterms");
                values.put("duedate",d.toString());
                values.put("langForInvoice","EN");
                values.put("listOfProducts",productList);
                values.put("islatefee","N");

                PayGUtils.sendHTMLMail(values,"","new","",OrderId);
            }*/

            mapData.put("OrderKeyId", null);
            mapData.put("MerchantKeyId", MerchantKeyId);
            mapData.put("UniqueRequestId", UniqueRequestId);
            mapData.put("OrderAmount", OrderAmount);
            mapData.put("OrderType", OrderType);
            mapData.put("OrderId", OrderId);
            mapData.put("OrderStatus", OrderStatus);
            mapData.put("OrderAmountData", null);
            mapData.put("NextStepFlowData", null);
            mapData.put("RecurringBillingData", null);
            mapData.put("CouponData", null);
            mapData.put("ShipmentData", "");
            mapData.put("RequestDateTime", dt.format(new Date()));
            mapData.put("RedirectUrl", RETURN_URL);
            mapData.put("Source", Source);

            //TransactionData
            tranData.put("PaymentType", PaymentType);
            tranData.put("StopDeclinedRetryFlag",true);
            if("UPI".equalsIgnoreCase(PaymentType))
            {
                bankdetailsHM =  new HashMap<String, String>();
                bankdetailsHM.put("Vpa",Vpa);
                tranData.put("Upi",bankdetailsHM);
            }
            else if("Netbanking".equalsIgnoreCase(PaymentType)){
                bankdetailsHM =  new HashMap<String, String>();
                bankdetailsHM.put("BankName",BankName);
                tranData.put("Netbanking",bankdetailsHM);
            }
            else if("Wallet".equalsIgnoreCase(PaymentType)){
                bankdetailsHM =  new HashMap<String, String>();
                transactionlogger.error("  PayGPaymentGateway autoRedirect......"+trackingID+"::"+autoRedirect);
                transactionlogger.error("  PayGPaymentGateway brandName......"+trackingID+"::"+brandName);

                if("Y".equalsIgnoreCase(autoRedirect)&& RB_WL.containsKey(brandName)){
                    transactionlogger.error(" cardType PayGPaymentGateway......"+brandName);

                    BankName= RB_WL.getString(brandName);
                    transactionlogger.error("BankName PayGPaymentGateway......"+BankName);

                }
                bankdetailsHM.put("WalletType",BankName);
                tranData.put("Wallet",bankdetailsHM);
            }
           else if("DebitCard".equalsIgnoreCase(PaymentType))
            {
                bankdetailsHM =  new HashMap<String, String>();
                bankdetailsHM.put("CardNo",CardNo);
                bankdetailsHM.put("NameOnCard",NameOnCard);
                bankdetailsHM.put("ExpiryDate",ExpiryDate);
                bankdetailsHM.put("CVV",Cvv);

                tranData.put("Card",bankdetailsHM);

                //EncryptionData.put("EncryptionType","AES256");
                EncryptionData.put("EncryptionType","CUSTOM");
                EncryptionData.put("EncryptionKey",aesKey);
                EncryptionData.put("SaltKey",SaltKey);
                EncryptionData.put("DeviceType","WEB");



               // mapData.put("Encryption", bankdetailsHM);

            }
            else{
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");

                return commResponseVO ;
            }

            //UserDefinedData
            Map userDefinedData   = new HashMap();
            userDefinedData.put("UserDefined1", "");

            //IntegrationData
            Map<String, String> intgData = new HashMap<String, String>();
            intgData.put("UserName", UserName);
            intgData.put("Source", Source);
            intgData.put("IntegrationType", IntegrationType);
            intgData.put("HashData", HashData);
            intgData.put("PlatformId", PlatformId);
            intgData.put("IpAddress", IpAddress);


            //Product Data
            Map<String, String> ProductData = new HashMap<String, String>();
            ProductData.put("WEBSITEURL", PaymentReason);

            //custData Data
            Map<String, String> custData = new HashMap<String, String>();
            custData.put("MobileNo",MobileNo);

            gson = new Gson();
            String productData = gson.toJson(ProductData).toString().replaceAll("\"", "'");

            mapData.put("IntegrationData", intgData);
            mapData.put("OrderAmount", OrderAmount);
            mapData.put("UserDefinedData", userDefinedData);
            mapData.put("TransactionData", tranData);
            mapData.put("ProductData", productData);
            mapData.put("CustomerData", custData);

            if(EncryptionData != null && !EncryptionData.isEmpty()){
                mapData.put("Encryption", EncryptionData);
            }


            gson            = new Gson();
            requestString   = gson.toJson(mapData);

            String plainCredentials = AuthenticationKey + ":" + AuthenticationToken + ":M:" + MerchantKeyId;
            String authentication = new String(Base64.encode(plainCredentials.getBytes("utf-8")));

            transactionlogger.error("resquestString "+trackingID+" "+requestString.toString());

            String responseString = PayGUtils.doGetHTTPSURLConnectionClient(REQUEST_URL, requestString.toString(), authentication);

            transactionlogger.error("responseString "+trackingID+" "+responseString);

            String PaymentProcessUrl    = "";
            String PaymentStatus        = "";
            String OrderKeyId           = "";
            String PaymentResponseCode  = "";
            String UpiLink              = "";
            String PaymentTransactionId = "";
            String PaymentResponseText  = "";
            String ResponseCode     = "";
            String Code             = "";
            String MessageRes             = "";

            String status = "";
            if(functions.isValueNull(responseString) && responseString.contains("["))
            {
                JSONArray jsonArray = new JSONArray(responseString);
                for (int i = 0 ; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String Message          =   obj.getString("Message");
                    String DeveloperMessage =   obj.getString("DeveloperMessage");

                    if(Message.contains("Invalid")){
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(Message + "\n" + DeveloperMessage);  // set inquiry response not found
                        commResponseVO.setRemark(Message + "\n" + DeveloperMessage);  // set inquiry response not found
                        commResponseVO.setTransactionStatus("fail");
                        return  commResponseVO;
                    }
                }
            }

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString))
            {
                JSONObject jsonObject       = new JSONObject(responseString);
                transactionlogger.error("jsonObject >>>>>>>>>> "+jsonObject);
                if(functions.isValueNull(jsonObject.toString())){
                    if(jsonObject.has("PaymentProcessUrl")){
                        PaymentProcessUrl = jsonObject.getString("PaymentProcessUrl");
                    }

                    if(jsonObject.has("PaymentStatus")){
                        PaymentStatus = jsonObject.getString("PaymentStatus");
                    }
                    if(jsonObject.has("OrderKeyId")){
                        OrderKeyId = jsonObject.getString("OrderKeyId");
                    }
                    if(jsonObject.has("PaymentResponseCode")){
                        PaymentResponseCode = jsonObject.getString("PaymentResponseCode");
                    }
                    if(jsonObject.has("PaymentResponseText")){
                        PaymentResponseText = jsonObject.getString("PaymentResponseText");
                    }
                    if(jsonObject.has("PaymentTransactionId")){
                        PaymentTransactionId = jsonObject.getString("PaymentTransactionId");
                    }

                    if(jsonObject.has("ResponseCode")){
                        ResponseCode = jsonObject.getString("ResponseCode");
                    }
                    if(jsonObject.has("Code")){
                        Code = jsonObject.getString("Code");
                    }
                    if(jsonObject.has("Message")){
                        MessageRes = jsonObject.getString("Message");
                    }

                    transactionlogger.error("PaymentType "+PaymentType);
                     if("Netbanking".equalsIgnoreCase(PaymentType)||"Wallet".equalsIgnoreCase(PaymentType) ){
                         transactionlogger.error("inside if PaymentType "+PaymentType);
                         if(functions.isValueNull(PaymentProcessUrl)){
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(PaymentProcessUrl);
                         }else{
                            commResponseVO.setStatus("fail");
                            commResponseVO.setDescription(PaymentResponseText);  // set inquiry response not found
                            commResponseVO.setRemark(PaymentResponseText);  // set inquiry response not found
                            commResponseVO.setTransactionStatus("fail");
                         }
                     }else if("UPI".equalsIgnoreCase(PaymentType))
                     {
                         if("Declined".equalsIgnoreCase(PaymentResponseText) || "Failed".equalsIgnoreCase(PaymentResponseText)){
                             commResponseVO.setStatus("failed");
                             commResponseVO.setTransactionStatus("failed");
                             commResponseVO.setTransactionId(OrderKeyId);
                             commResponseVO.setDescription(PaymentResponseText);
                             commResponseVO.setRemark(PaymentResponseText);
                         }else if("Pending".equalsIgnoreCase(PaymentResponseText)){
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setTransactionId(OrderKeyId);
                            commResponseVO.setDescription(PaymentResponseText);
                            commResponseVO.setRemark(PaymentResponseText);
                        }else{
                             commResponseVO.setStatus("pending");
                             commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                             commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                             commResponseVO.setTransactionStatus("pending");
                         }
                     }else if("DebitCard".equalsIgnoreCase(PaymentType)){

                         if(functions.isValueNull(PaymentProcessUrl)){
                             commResponseVO.setStatus("pending3DConfirmation");
                             commResponseVO.setUrlFor3DRedirect(PaymentProcessUrl);
                             commResponseVO.setTransactionId(OrderKeyId);

                         }
                         else if("3".equalsIgnoreCase(ResponseCode) && "0401".equalsIgnoreCase(Code)){
                             commResponseVO.setStatus("failed");
                             commResponseVO.setTransactionStatus("Failed");
                             commResponseVO.setDescription(MessageRes);
                             commResponseVO.setRemark(MessageRes);
                             commResponseVO.setTransactionId(OrderKeyId);

                         }else {
                             commResponseVO.setStatus("pending");
                             commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                             commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                             commResponseVO.setTransactionStatus("pending");
                         }

                     }
                }else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }


            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

            if(functions.isValueNull(OrderKeyId))
            {

                PayGUtils.updateOrderid(OrderKeyId,trackingID);
            }

        }catch (Exception e){
            transactionlogger.error("PayGPaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in PayG ---- ");
        String html                                 = "";
        Comm3DResponseVO transRespDetails           = null;
        PayGUtils payGUtils                         = new PayGUtils();
        PayGPaymentProcess payGPaymentProcess   = new PayGPaymentProcess();
        CommRequestVO commRequestVO             = payGUtils.getPayGRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(accountId);

        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = payGPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect PayG form -- >>"+commonValidatorVO.getTrackingid() +" " +html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in PayGPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of PayG---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                = gatewayAccount.isTest();
        Functions functions           = new Functions();
        PayGUtils payUUtils           = new PayGUtils();
        StringBuffer parameters       = new StringBuffer();


        String MerchantKeyId        = gatewayAccount.getMerchantId();
        String AuthenticationKey    = gatewayAccount.getFRAUD_FTP_USERNAME();
        String AuthenticationToken  = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String orderKeyID           = commTransactionDetailsVO.getPreviousTransactionId();

        Map mapData = new HashMap();

        String inquiry_res = "";
        String REQUEST_URL = "";
        try
        {
            mapData.put("Merchantkeyid", MerchantKeyId);
            mapData.put("OrderKeyID", orderKeyID);

            String base64Credentials = AuthenticationKey+":"+AuthenticationToken+":M:"+MerchantKeyId;
            String authentication = new String(Base64.encode(base64Credentials.getBytes("utf-8")));
            Gson gson               = new Gson();
            String requestString    = gson.toJson(mapData);

            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            transactionlogger.error("inquiry req is --> "+trackingId+" "+REQUEST_URL+ " ----> "+requestString);

            inquiry_res = payUUtils.doGetHTTPSURLConnectionClient(REQUEST_URL,requestString,authentication);
            transactionlogger.error("inquiry res is -- > "+trackingId+" "+inquiry_res);

            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String PaymentStatus    = "";
                String transactionId    = "";
                String OrderAmount      = "";
                String dateTime         = "";
                String PaymentResponseCode  = "";
                String bankcode             = "";
                String PaymentResponseText  = "";

                JSONObject jsonObject = new JSONObject(inquiry_res);

                if(jsonObject != null){

                    if (jsonObject.has("OrderKeyId"))
                    {
                        transactionId = jsonObject.getString("OrderKeyId");
                    }

                    if (jsonObject.has("OrderAmount"))
                    {
                        OrderAmount = jsonObject.getString("OrderAmount");
                    }

                    if (jsonObject.has("PaymentResponseCode"))
                    {
                        PaymentResponseCode = jsonObject.getString("PaymentResponseCode");
                    }
                    if (jsonObject.has("UpdatedDateTime"))
                    {
                        dateTime = jsonObject.getString("UpdatedDateTime");
                    }

                    if (jsonObject.has("PaymentResponseText"))
                    {
                        PaymentResponseText = jsonObject.getString("PaymentResponseText");
                    }


                    if ("Approved".equalsIgnoreCase(PaymentResponseText))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setDescription(PaymentResponseText);
                        commResponseVO.setRemark(PaymentResponseText);
                        commResponseVO.setAmount(OrderAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setErrorCode(PaymentResponseCode);
                        commResponseVO.setAuthCode(PaymentResponseCode);
                        commResponseVO.setResponseHashInfo(transactionId);
                    }
                    else if("Declined".equalsIgnoreCase(PaymentResponseText) || "Failed".equalsIgnoreCase(PaymentResponseText))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(PaymentResponseText);
                        commResponseVO.setRemark(PaymentResponseText);
                        commResponseVO.setAmount(OrderAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setErrorCode(PaymentResponseCode);
                        commResponseVO.setAuthCode(PaymentResponseCode);
                        commResponseVO.setResponseHashInfo(transactionId);

                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    commResponseVO.setMerchantId(MerchantKeyId);
                }else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }
            // no response set pending
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (JSONException e)
        {
            transactionlogger.error("PayGPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("PayGPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PayGPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside paygpay process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        PayGUtils payGUtils = new PayGUtils();

        String hostURL          = "";
        String MerchantKeyId        = gatewayAccount.getMerchantId();
        String AuthenticationKey    = gatewayAccount.getFRAUD_FTP_USERNAME();
        String AuthenticationToken  = gatewayAccount.getFRAUD_FTP_PASSWORD();
        //String PayoutCustomerkeyId  = gatewayAccount.getFRAUD_FTP_PATH();
        String PayoutCustomerkeyId  = "";

        String UniqueRequestId        = trackingId;
        String PayOutType             = "Immediate";
        String PayOutDate             = "";
        String PaymentType            = "";
        String Amount       = "0.00";
        String BankName     = "";
        String IFSCCode     = "";
        String AccountNumber = "";
        String PaymentReason = "https://live.dsgintl.net";
        String MobileNo     = PayGUtils.geDummyMobileNo();

        JSONObject jsonObject   = new JSONObject();
        Map ProductData = new HashMap();
        try
        {
            if (isTest)
            {
                hostURL         = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                BankName = commTransactionDetailsVO.getCustomerBankAccountName();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                AccountNumber = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                IFSCCode = commTransactionDetailsVO.getBankIfsc();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                Amount = commTransactionDetailsVO.getAmount();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                PaymentType = commTransactionDetailsVO.getBankTransferType();
            }

            ProductData.put("WEBSITEURL",PaymentReason);

            Gson gson = new Gson();
            String productData = gson.toJson(ProductData).toString().replaceAll("\"", "'");

            jsonObject.put("MerchantKeyId", MerchantKeyId);
            jsonObject.put("UniqueRequestId", UniqueRequestId);
            jsonObject.put("PayoutCustomerkeyId", PayoutCustomerkeyId);
            jsonObject.put("PayOutType", PayOutType);
            jsonObject.put("PayOutDate", PayOutDate);
            jsonObject.put("PaymentType", PaymentType);
            jsonObject.put("Amount", Amount);
            jsonObject.put("BeneficiaryName", BankName);
            jsonObject.put("BankName", BankName);
            jsonObject.put("BranchName", BankName);
            jsonObject.put("BankCode", IFSCCode);
            jsonObject.put("AccountNumber", AccountNumber);
            jsonObject.put("ProductData", productData);
            jsonObject.put("MobileNo", MobileNo);

            transactionlogger.error("payGPayouyRequest------> " +trackingId +" "+ jsonObject.toString());

            String base64Credentials = AuthenticationKey+":"+AuthenticationToken+":M:"+MerchantKeyId;
            String authentication   = new String(Base64.encode(base64Credentials.getBytes("utf-8")));

            String  responeString  =  PayGUtils.doGetHTTPSURLConnectionClient(hostURL,jsonObject.toString(),authentication);

            transactionlogger.error("payGPayoutRespone------> " +trackingId +" "+responeString);

            String PayOutKeyId = "";
            String ResponseText = "";
            String ResponseCode = "";
            String TransactionId = "";
            String UTRNo = "";
            if(functions.isValueNull(responeString) && responeString.contains("["))
            {
                JSONArray jsonArray = new JSONArray(responeString);
                for (int i = 0 ; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);
                    String code             = obj.getString("Code");
                    String FieldName        = obj.getString("FieldName");
                    String DeveloperMessage = obj.getString("DeveloperMessage");
                    StringBuffer remarkMSG  = new StringBuffer();
                    if(functions.isValueNull(FieldName)){
                        remarkMSG.append(FieldName+" ");
                    }
                    if(functions.isValueNull(DeveloperMessage)){
                        remarkMSG.append(DeveloperMessage);
                    }
                    if("6095".equalsIgnoreCase(code)){
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(remarkMSG.toString());
                        commResponseVO.setDescription(remarkMSG.toString());
                        return commResponseVO;
                    }

                }
            }

            if(responeString != null && responeString.contains("{")){
                JSONObject payoutResponse   = new JSONObject(responeString);

                if (payoutResponse.has("TransactionId") && functions.isValueNull(payoutResponse.getString("TransactionId")))
                {
                    TransactionId = payoutResponse.getString("TransactionId");
                }
                if (payoutResponse.has("UTRNo") && functions.isValueNull(payoutResponse.getString("UTRNo")))
                {
                    UTRNo = payoutResponse.getString("UTRNo");
                }
                if (payoutResponse.has("ResponseText") && functions.isValueNull(payoutResponse.getString("ResponseText")))
                {
                    ResponseText = payoutResponse.getString("ResponseText");
                }
                if (payoutResponse.has("ResponseCode") && functions.isValueNull(payoutResponse.getString("ResponseCode")))
                {
                    ResponseCode = payoutResponse.getString("ResponseCode");
                }
                if (payoutResponse.has("PayOutKeyId") && functions.isValueNull(payoutResponse.getString("PayOutKeyId")))
                {
                    PayOutKeyId = payoutResponse.getString("PayOutKeyId");
                }

                if (ResponseText.equalsIgnoreCase("Approved"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(ResponseText);
                    commResponseVO.setDescription(ResponseText);
                    commResponseVO.setErrorCode(ResponseCode);
                }
                else if (ResponseText.equalsIgnoreCase("Pending") || ResponseText.equalsIgnoreCase("Pending from bank")
                        || ResponseText.equalsIgnoreCase("Initiated"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(ResponseText);
                    commResponseVO.setDescription(ResponseText);
                    commResponseVO.setErrorCode(ResponseCode);
                }
                else if ("2".equalsIgnoreCase(ResponseCode)|| ResponseText.equalsIgnoreCase("Declined") || ResponseText.equalsIgnoreCase("Failed")|| ResponseText.contains("Failures"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(ResponseText);
                    commResponseVO.setDescription(ResponseText);
                    commResponseVO.setErrorCode(ResponseCode);
                }else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
                if(functions.isValueNull(TransactionId) && functions.isValueNull(PayOutKeyId) ){
                    payGUtils.updatePayGTransctionId(TransactionId,PayOutKeyId,trackingId);
                }


                commResponseVO.setTransactionId(PayOutKeyId);
                commResponseVO.setResponseHashInfo(PayOutKeyId);
                commResponseVO.setIfsc(IFSCCode);
                commResponseVO.setFullname(BankName);
                commResponseVO.setBankaccount(AccountNumber);
            } else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");

            }

            String CALL_EXECUTE_AFTER       = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL    = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC          = RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed          = RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncPayGPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }
        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException----trackingid---->"+trackingId+"--->",e);
        }catch (Exception e){
            transactionlogger.error("Exception----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside Payg processPayout inquiry------->");

        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        CommTransactionDetailsVO commTransactionDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        boolean isTest                    = false;
        String hostURL                    = "";
        Functions functions               = new Functions();
        PayGUtils payGUtils = new PayGUtils();

        String MerchantKeyId        = gatewayAccount.getMerchantId();
        String AuthenticationKey    = gatewayAccount.getFRAUD_FTP_USERNAME();
        String AuthenticationToken  = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String TransactionId    = commTransactionDetailsVO.getAuthorization_code();
        String PayOutKeyId      = commTransactionDetailsVO.getPreviousTransactionId();
        JSONObject jsonObject   = new JSONObject();

        try
        {
            isTest    = gatewayAccount.isTest();
            transactionlogger.error("isTest---------->" + isTest);
            if (isTest)
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            jsonObject.put("TransactionId",TransactionId);
            if(functions.isValueNull(PayOutKeyId)){
                jsonObject.put("PayOutKeyId",PayOutKeyId);
            }
            else {
                jsonObject.put("RequestUniqueId",trackingId);

            }
            jsonObject.put("MerchantKeyId",MerchantKeyId);

            String base64Credentials = AuthenticationKey+":"+AuthenticationToken+":M:"+MerchantKeyId;
            String authentication   = new String(Base64.encode(base64Credentials.getBytes("utf-8")));

            transactionlogger.error("payoutInquiryRequest------> "+ trackingId+" " +jsonObject.toString());
            String responeString    = PayGUtils.doGetHTTPSURLConnectionClient(hostURL,jsonObject.toString(),authentication);

            transactionlogger.error("payoutInquiryResponse------> "+ trackingId +" "+ responeString);
            String ResponseText = "";
            String ResponseCode = "";
            String UTRNo = "";

/*

            if(functions.isValueNull(responeString) && responeString.contains("["))
            {
                JSONArray jsonArray = new JSONArray(responeString);
                for (int i = 0 ; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);
                    String code             = obj.getString("Code");
                    String FieldName        = obj.getString("FieldName");
                    String DeveloperMessage = obj.getString("DeveloperMessage");
                    StringBuffer remarkMSG = new StringBuffer();
                    if(functions.isValueNull(FieldName)){
                        remarkMSG.append(FieldName+" ");
                    }
                    if(functions.isValueNull(DeveloperMessage)){
                        remarkMSG.append(DeveloperMessage);
                    }
                    if("6095".equalsIgnoreCase(code)){
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(remarkMSG.toString());
                        commResponseVO.setDescription(remarkMSG.toString());
                        return commResponseVO;
                    }

                }
            }
*/

            if(functions.isValueNull(responeString) && responeString.contains("{"))
            {

                JSONObject payoutResponse   = new JSONObject(responeString);

                if (payoutResponse.has("TransactionId") && functions.isValueNull(payoutResponse.getString("TransactionId")))
                {
                    TransactionId = payoutResponse.getString("TransactionId");
                }
                if (payoutResponse.has("UTRNo") && functions.isValueNull(payoutResponse.getString("UTRNo")))
                {
                    UTRNo = payoutResponse.getString("UTRNo");
                }
                if (payoutResponse.has("ResponseText") && functions.isValueNull(payoutResponse.getString("ResponseText")))
                {
                    ResponseText = payoutResponse.getString("ResponseText");
                }
                if (payoutResponse.has("ResponseCode") && functions.isValueNull(payoutResponse.getString("ResponseCode")))
                {
                    ResponseCode = payoutResponse.getString("ResponseCode");
                }
                if (payoutResponse.has("PayOutKeyId") && functions.isValueNull(payoutResponse.getString("PayOutKeyId")))
                {
                    PayOutKeyId = payoutResponse.getString("PayOutKeyId");
                }


                if (ResponseText.equalsIgnoreCase("Approved")&&"1".equalsIgnoreCase(ResponseCode))
                {
                    transactionlogger.error("inside if success payoutinquiry payg  -->" +ResponseText );
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(ResponseText);
                    commResponseVO.setDescription(ResponseText);
                }
                else if ("5".equalsIgnoreCase(ResponseCode)||ResponseText.equalsIgnoreCase("Pending") || ResponseText.equalsIgnoreCase("Pending from bank")
                        || ResponseText.equalsIgnoreCase("Initiated"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(ResponseText);
                    commResponseVO.setDescription(ResponseText);
                }
                else if ("2".equalsIgnoreCase(ResponseCode)|| ResponseText.equalsIgnoreCase("Declined") || ResponseText.equalsIgnoreCase("Failed")|| ResponseText.contains("Failures"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    if("Bank API Connectivity is down".equalsIgnoreCase(ResponseText))
                        ResponseText="Beneficiary Bank down";
                    commResponseVO.setRemark(ResponseText);
                    commResponseVO.setDescription(ResponseText);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }

               /* if(functions.isValueNull(UTRNo)){
                    payGUtils.updateRRNMainTableEntry(UTRNo, trackingId);
                }*/
                commResponseVO.setTransactionId(PayOutKeyId);
                commResponseVO.setResponseHashInfo(PayOutKeyId);
                commResponseVO.setErrorCode(ResponseCode);
                commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                commResponseVO.setMerchantId(MerchantKeyId);
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("processPayoutInquiry JSONException-->" ,e );
        }

        return commResponseVO;

    }

}