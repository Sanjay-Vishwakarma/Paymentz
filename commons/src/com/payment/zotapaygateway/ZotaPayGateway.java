package com.payment.zotapaygateway;

        import com.directi.pg.Functions;
        import com.directi.pg.LoadProperties;
        import com.directi.pg.TransactionLogger;
        import com.directi.pg.core.GatewayAccount;
        import com.directi.pg.core.GatewayAccountService;
        import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
        import com.directi.pg.core.valueObjects.GenericRequestVO;
        import com.directi.pg.core.valueObjects.GenericResponseVO;
        import com.manager.PaymentManager;
        import com.manager.vo.ReserveField2VO;
        import com.payment.common.core.*;
        import com.payment.exceptionHandler.PZConstraintViolationException;
        import com.payment.exceptionHandler.PZDBViolationException;
        import com.payment.exceptionHandler.PZGenericConstraintViolationException;
        import com.payment.exceptionHandler.PZTechnicalViolationException;
        import com.payment.validators.vo.CommonValidatorVO;
        import org.codehaus.jettison.json.JSONException;
        import org.codehaus.jettison.json.JSONObject;

        import java.util.Date;
        import java.util.ResourceBundle;

/**
 * Created by Balaji on 23-Jan-20.
 */
public class ZotaPayGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ZotaPayGateway.class.getName());
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.zotaPayProperties");
    public static final String GATEWAY_TYPE = "zota";

    public ZotaPayGateway(String accountId)
    {
        this.accountId = accountId;
    }
    ZotapayUtils zotapayUtils = new ZotapayUtils();

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();



        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String endPoint = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String merchantID=gatewayAccount.getMerchantId();
        String merchantOrderDesc=commTransactionDetailsVO.getOrderDesc();
        String orderAmount=commTransactionDetailsVO.getAmount();
        String orderCurrency=commTransactionDetailsVO.getCurrency();
        String customerEmail=commAddressDetailsVO.getEmail();
        String customerFirstName=commAddressDetailsVO.getFirstname();
        String customerLastName=commAddressDetailsVO.getLastname();
        String customerAddress=commAddressDetailsVO.getStreet();
        String customerCountryCode=commAddressDetailsVO.getCountry();
        String customerCity=commAddressDetailsVO.getCity();
        String customerZipCode=commAddressDetailsVO.getZipCode();
        String customerPhone=commAddressDetailsVO.getPhone();
        String customerIP=commAddressDetailsVO.getIp();
        String redirectUrl="";
        String checkoutUrl="https://www.pz.com";
        String signature=zotapayUtils.getSignature(endPoint, trackingID, orderAmount, customerEmail,"", secretKey);

        String frontEndURL ="";
        String backEndUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            transactionLogger.error("inside if url ----" );
            frontEndURL = "https://" + commMerchantVO.getHostUrl() + RB.getString("TERM_URL")+trackingID;
            backEndUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
        }
        else
        {
            transactionLogger.error("inside else url ----" );
            frontEndURL = RB.getString("NOTIFICATION_TERM_URL")+trackingID;
            backEndUrl = RB.getString("NOTIFICATION_HOST_URL")+trackingID;
        }
        transactionLogger.error("From RB TERM_URL(frontEndURL) ----" + frontEndURL);
        transactionLogger.error("From RB notificationUrl(backEndUrl) ----" + backEndUrl);

        String request = "{" +
                "\"merchantOrderID\": \""+trackingID+"\"," +
                "\"merchantOrderDesc\": \""+merchantOrderDesc+"\"," +
                "\"orderAmount\": \""+orderAmount+"\"," +
                "\"orderCurrency\": \""+orderCurrency+"\"," +
                "\"customerEmail\": \""+customerEmail+"\"," +
                "\"customerFirstName\": \""+customerFirstName+"\"," +
                "\"customerLastName\": \""+customerLastName+"\"," +
                "\"customerAddress\": \""+customerAddress+"\"," +
                "\"customerCountryCode\": \""+customerCountryCode+"\"," +
                "\"customerCity\": \""+customerCity+"\"," +
                "\"customerZipCode\": \""+customerZipCode+"\"," +
                "\"customerPhone\": \""+customerPhone+"\"," +
                "\"customerIP\": \""+customerIP+"\"," +
                "\"redirectUrl\": \""+frontEndURL+"\"," +
                "\"callbackUrl\": \""+backEndUrl+"\"," +

                "\"checkoutUrl\": \""+checkoutUrl+"\"," +
                "\"signature\": \""+signature+"\"" +
                "}";

        transactionLogger.error("Zotapay sale request--for--" + trackingID + "--"+request);
        String url = "";
        if(isTest){
            url=RB.getString("TEST_SALE")+endPoint;
        }else{
            url=RB.getString("LIVE_SALE")+endPoint;
        }
        transactionLogger.error("url--------"+url);
        String response = zotapayUtils.doPostHTTPSURLConnectionClient(url,request);
        transactionLogger.error("Zotapay sale response---for--" + trackingID + "--"+response);

        String code = "";
        String depositUrl = "";
        String merchantorderID = "";
        String orderID = "";
        String message = "";

        if (response!=null){
            try{
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject!=null)
                    if(jsonObject.has("code")){
                        code = jsonObject.getString("code");
                        if(jsonObject.has("message")){
                            message = jsonObject.getString("message");
                        }
                        if(!jsonObject.isNull("data")){
                            JSONObject data = jsonObject.getJSONObject("data");
                            depositUrl=data.getString("depositUrl");
                            merchantorderID=data.getString("merchantOrderID");
                            orderID=data.getString("orderID");
                        }
                    }
            }
            catch (Exception e){
                transactionLogger.error("Exception -----",e);
            }
        }
        if("200".equals(code)){
//                successfull transaction
            commResponseVO.setStatus("pending");
            commResponseVO.setTransactionId(orderID);
            commResponseVO.setRedirectUrl(depositUrl);
            commResponseVO.setBankCode(code);
            commResponseVO.setErrorCode(code);
            commResponseVO.setRemark("SYS:Pending 3D Confirmation");
            commResponseVO.setDescription("SYS:Pending 3D Confirmation");
            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

        }
        else{
//                failed transaction
            commResponseVO.setStatus("failed");
            commResponseVO.setRemark("SYS:Transaction Declined");
            commResponseVO.setDescription(message);
            commResponseVO.setErrorCode(code);
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside zotapay processInquiry-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String uniqueId=commTransactionDetailsVO.getPreviousTransactionId();
        String trackingId = commTransactionDetailsVO.getOrderId();
        boolean isTest = gatewayAccount.isTest();

        try
        {

            String merchantId = gatewayAccount.getMerchantId();
            String endPoint = gatewayAccount.getFRAUD_FTP_USERNAME();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
//            merchantId = "PAYSTAGE";

            Date date= new Date();
            long time = (date.getTime()/1000);
            String timestamp = time+"";


            String signature = zotapayUtils.getSignature(merchantId, trackingId, uniqueId, timestamp, secretKey,"");
            String url = "";


            String request = "?"
                    +"merchantID="+merchantId
                    +"&merchantOrderID="+trackingId
                    +"&orderID="+uniqueId
                    +"&timestamp="+timestamp
                    +"&signature="+signature;

            if(isTest){
                url=RB.getString("TEST_INQUIRY");
            }else{
                url=RB.getString("LIVE_INQUIRY");
            }
            url=url+request;
            transactionLogger.error("request---" + url);

            String response = zotapayUtils.doGetHTTPSURLConnectionClient(url);
            transactionLogger.error("zotapay inquiry response---" + response);

            String code="";
            String type="";
            String status="";
            String errorMessage="";
            String endpointID="";
            String processorTransactionID="";
            String zotaUniqueId="";
            String merchantOrderID="";
            String amount="";
            String currency="";
            String customParam="";
            String extraData="";

            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("code"))
                code=jsonObject.getString("code");
            if(jsonObject.has("message"))
                errorMessage=jsonObject.getString("message");
            if(!jsonObject.isNull("data")){
                JSONObject data = jsonObject.getJSONObject("data");
                if(data.has("status"))
                    status=data.getString("status");
                if(data.has("amount"))
                    amount=data.getString("amount");
                if(data.has("currency"))
                    currency=data.getString("currency");
                if(data.has("orderID"))
                    uniqueId=data.getString("orderID");
                if(data.has("orderID"))
                    zotaUniqueId=data.getString("orderID");
                if(data.has("type"))
                    type=data.getString("type");
            }
            if(status.equals("CREATED") || status.equals("PROCESSING") || status.equals("PENDING")){
                //pending status
                transactionLogger.error("inside pending transaction condition");
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("Pending");
                commResponseVO.setRemark(status);
                commResponseVO.setDescription(status);
                commResponseVO.setTransactionType(type);
            }
            else if(status.equalsIgnoreCase("APPROVED")){
                //successful status
                transactionLogger.error("inside APPROVED transaction condition");

                commResponseVO.setStatus("success");
                commResponseVO.setTransactionStatus("Successful");
                commResponseVO.setRemark(status);
                commResponseVO.setDescription(status);
                commResponseVO.setTransactionType(type);

            }
            else if(status.equalsIgnoreCase("DECLINED")){
                //declined status
                transactionLogger.error("inside DECLINED transaction condition");

                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionStatus("Failed");
                commResponseVO.setRemark(status);
                commResponseVO.setDescription(status);
                commResponseVO.setTransactionType(type);

            }
            else{
                //failed staus
                transactionLogger.error("inside failed transaction condition");
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionStatus("Failed");
                commResponseVO.setRemark(errorMessage);
                commResponseVO.setDescription(errorMessage);
                commResponseVO.setTransactionType("Inquiry");

                transactionLogger.error("message from response ------"+errorMessage);
            }
            commResponseVO.setAuthCode(code);
            commResponseVO.setTransactionId(uniqueId);

//            commResponseVO.setBankTransactionDate(date_created);
            commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception--->",e);
        }

        return commResponseVO;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("inside processAutoRedirect in ZotaPayGateway");
        String html="";
        PaymentManager paymentManager = new PaymentManager();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = zotapayUtils.getCommRequestFromUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            transactionLogger.debug("process Sale finished");

            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                html = zotapayUtils.getRedirectForm(transRespDetails);
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                transactionLogger.debug("html---------------"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in ZotaPayGateway ---",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception--- ", e);
        }
        return html;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside process payout-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        ReserveField2VO reserveField2VO = ((CommRequestVO) requestVO).getReserveField2VO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String endPoint = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String merchantID="";
        String merchantOrderDesc="";
        String orderAmount="";
        String orderCurrency="";
        String customerEmail="";
        String customerLastName="";
        String customerPhone="";
        String customerIP="";
        String redirectUrl="";
        String customerBankAccountNumber="";
        String customerBankAccountName="";
        String customerBankCode="";
        String frontEndURL ="";
        String backEndUrl = "";

        if(functions.isValueNull(gatewayAccount.getMerchantId()))
             merchantID = gatewayAccount.getMerchantId();

        if(functions.isValueNull(gatewayAccount.getMerchantId()))
         merchantOrderDesc=commTransactionDetailsVO.getOrderDesc();

        if(functions.isValueNull(gatewayAccount.getMerchantId()))
         orderAmount=commTransactionDetailsVO.getAmount();

        if(functions.isValueNull(gatewayAccount.getMerchantId()))
         orderCurrency=commTransactionDetailsVO.getCurrency();

        if(functions.isValueNull(gatewayAccount.getMerchantId()))
         customerEmail=commAddressDetailsVO.getEmail();
        String customerFirstName=commAddressDetailsVO.getFirstname();

        if(functions.isValueNull(gatewayAccount.getMerchantId()))
         customerLastName=commAddressDetailsVO.getLastname();

//        String customerAddress=commAddressDetailsVO.getStreet();
//        String customerCountryCode=commAddressDetailsVO.getCountry();
//        String customerCity=commAddressDetailsVO.getCity();
//        String customerZipCode=commAddressDetailsVO.getZipCode();

        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
         customerPhone=commAddressDetailsVO.getPhone();

        if(functions.isValueNull(commAddressDetailsVO.getIp()))
         customerIP=commAddressDetailsVO.getIp();

        String checkoutUrl="https://www.pz.com";

        if(functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountNumber()))
         customerBankAccountNumber=commTransactionDetailsVO.getCustomerBankAccountNumber();

        if(functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
         customerBankAccountName=commTransactionDetailsVO.getCustomerBankAccountName();

        if(functions.isValueNull(commTransactionDetailsVO.getCustomerBankCode()))
         customerBankCode=commTransactionDetailsVO.getCustomerBankCode();


//        String customerBankBranch=reserveField2VO.getBankName();
//        String customerBankAddress=reserveField2VO.getBankAddress();
//        String customerBankZipCode=reserveField2VO.getBankZipcode();
//        String customerBankProvince=reserveField2VO.getBankState();
//        String customerBankArea=reserveField2VO.getBankCity();
//        String customerBankRoutingNumber=reserveField2VO.getRoutingNumber();


        String signature=zotapayUtils.getSignature(endPoint, trackingId, orderAmount, customerEmail, customerBankAccountNumber, secretKey);


        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            frontEndURL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingId;
            backEndUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL")+trackingId;
        }
        else
        {
            frontEndURL = RB.getString("TERM_URL")+trackingId;
            backEndUrl = RB.getString("NOTIFICATION_TERM_URL")+trackingId;
        }
        transactionLogger.error("From RB TERM_URL ----" + frontEndURL);
        transactionLogger.error("From RB notificationUrl ----" + backEndUrl);

        String payoutRequest = "{"
                +"\"merchantOrderID\": \""+trackingId+"\","
                +"\"merchantOrderDesc\": \""+merchantOrderDesc+"\","
                +"\"orderAmount\": \""+orderAmount+"\","
                +"\"orderCurrency\": \""+orderCurrency+"\","
                +"\"customerEmail\": \""+customerEmail+"\","
                +"\"customerFirstName\": \""+customerFirstName+"\","
                +"\"customerLastName\": \""+customerLastName+"\","
                +"\"customerPhone\": \""+customerPhone+"\","
                +"\"customerIP\": \""+customerIP+"\","
                +"\"callbackUrl\": \""+frontEndURL+"\","

                +"\"customerBankCode\": \""+customerBankCode+"\","                               //Conditional
                +"\"customerBankAccountNumber\": \""+customerBankAccountNumber+"\","             //Required
                +"\"customerBankAccountName\": \""+customerBankAccountName+"\","                 //Required
//                +"\"customerBankBranch\": \""+customerBankBranch+"\","                           //Conditional
//                +"\"customerBankAddress\": \""+customerBankAddress+"\","                         //Conditional
//                +"\"customerBankZipCode\": \""+customerBankZipCode+"\","                         //Conditional
//                +"\"customerBankProvince\": \""+customerBankProvince+"\","                       //Optional
//                +"\"customerBankArea\": \""+customerBankArea+"\","                               //Optional
                +"\"customerBankRoutingNumber\": \""+customerBankAccountName+"\","             //Conditional

//                +"\"customParam\": \"{\\\"UserId\\\": \\\"e139b447\\\"}\","
                +"\"checkoutUrl\": \""+checkoutUrl+"\","
                +"\"signature\": \""+signature+"\""
                +"}";

        transactionLogger.error("zotapay payoutRequest----for--" + trackingId + "--"+payoutRequest);
        String url="";
        if(isTest){
            url=RB.getString("TEST_PAYOUT")+endPoint;
        }else{
            url=RB.getString("LIVE_PAYOUT")+endPoint;
        }
        transactionLogger.error("url-----"+url);
        String payoutResponse = zotapayUtils.doPostHTTPSURLConnectionClient(url, payoutRequest);
        transactionLogger.error("zotapay payoutResponse-----for--" + trackingId + "--" + payoutResponse);

        if(functions.isValueNull(payoutRequest)){
            String code="";
            String message="";
            String merchantOrderID="";
            String orderID="";
            try
            {
                JSONObject jsonObject = new JSONObject(payoutResponse);
                if(jsonObject.has("code")){
                    code=jsonObject.getString("code");
                }
                if(jsonObject.has("message")){
                    message=jsonObject.getString("message");
                }

                if(!jsonObject.isNull("data")){
                    JSONObject data = jsonObject.getJSONObject("data");
                    if(data.has("merchantOrderID")){
                        merchantOrderID=data.getString("merchantOrderID");
                    }
                    if(data.has("orderID")){
                        orderID=data.getString("orderID");
                    }
                }
            }
            catch (JSONException e)
            {
                transactionLogger.error("JSONException --",e);
            }
            transactionLogger.error("bank code-----"+code);
            if(code.equalsIgnoreCase("200")){
                //successfull transaction
                transactionLogger.error("inside success if :: bankCode-----"+code);
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(orderID);
                commResponseVO.setDescription("SYS: Payout Successful");
                commResponseVO.setRemark("SYS: Payout Successful");
            }
            else{
                //failed transaction
                transactionLogger.error("inside fail else :: bankCode-----"+code);
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS: Payout Failed!");
                commResponseVO.setDescription(message);
            }
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

