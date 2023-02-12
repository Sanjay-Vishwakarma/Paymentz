package com.payment.alliedwalled.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 5/14/14
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlliedWalletPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE = "AlliedWall";
    final static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.alliedWallet");
    private final static String URL = "https://service.381808.com/Merchant.asmx?WSDL";
    private static TransactionLogger transactionLogger = new TransactionLogger(AlliedWalletPaymentGateway.class.getName());

    public  AlliedWalletPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        try
        {
            String token="AAEAAKbtFqxbIVS22o7reQYyaEhZZmzCmukwjtnvtUfkUJDTRbRJKcUw4jBs7IYIwFT3k9z9DO42LJWxwGAxwwNXDf2leYSbzy1ADr_5QE7bD4bDgIJP0dvy7Y01iW7IUK08Mm0DPeUMvs3oMghtpgYYgzm4SvIlZS_pMMH55Mb__jAAnOkQiGB97xZQZPxDLoAqy2UUOlF4iK2buDJlTHtFbACJm7AQjaNDy-3vfVGfbohUDaYDdEK1Ao77VBGnMRxhahTPQVQNm2SnQzXQp0LdvLeCieDJXRaxa3QUou30GCqOXLz7f4U2EcObPxYb0P0v2oEe4oifnosE9UBhFhByqXOkAQAAAAEAAAI4CdVJd-e-b5WGRvbNvdIu_AfU-HkTohRtlI9mBf17pCVI65v3Le_uLmMV4iJsa9sU5gZ-BiodIXsgeDb9_w3i2CXuxPM7RqT721fru8AO6xP0AmDT-ZK_qp1REOq62WoE2s3sJB4YceSL_0mnagg3R62ZsFitiCGFUt15LgPHzfLNT-vOZRoGx_-gADEar1WkB-M38hMWI5dGULu-bN1cOj63iqHsP4NtsDA4Q78W4JvqQ0KoSzBa-fM1sZimMdRMp0ZVGcOL9jCx-O2ftLQFuut9CD5jrzU54Ok6jK6jE2hR2pkm1tMSk0HrYMdgaiRFvZNvDLHnbNo2RLRH_AHdCnOwTW4Q2TAbrM7NwY2x6D1VV_f5VmAKGPFEO3_bqGE0OTdMGWANXXEaKgwjDCIwfNTIfJvylOZvnn3yvO0thMsKojj-ocWFrE1jAZ7wJlaNc_De0GJ_MtErWJnBIFwpuQZ39fbh3j6yrAnRyhD7UfSLp4-rKJ33nAjRsgCvm8b3ObpLlYoKCscPL0KYi1AgeULGvlcK8t5UaCqtLBQF";
            String request = "{\"siteId\":\"56319\",\n" +
                    "\"amount\":\"50.00\",\n" +
                    "\"currency\":\"USD\",\n" +
                    "\"firstName\":\"Uday\",\n" +
                    "\"lastName\":\"Raj\",\n" +
                    "\"phone\":\"9870850511\",\n" +
                    "\"addressLine1\":\"Malad\",\n" +
                    "\"city\":\"Mumbai\",\n" +
                    "\"state\":\"MH\",\n" +
                    "\"countryId\":\"IN\",\n" +
                    "\"postalCode\":\"400067\",\n" +
                    "\"email\":\"udaybhan.rajbhar@pz.com\",\n" +
                    "\"cardNumber\":\"4242424242424242\",\n" +                       //4485914041913136
                    "\"nameOnCard\":\"SYSTEMERROR\",\n" +
                    "\"expirationMonth\":\"12\",\n" +
                    "\"expirationYear\":\"2019\",\n" +
                    "\"cVVCode\":\"123\",\n" +
                    "\"iPAddress\":\"127.0.0.0\",\n" +
                    "\"trackingID\" : \"688767\",\n" +
                    "\"isInitialForRecurring\":false }";

            String captureRequest="{\n" +
                    "\"authorizeTransactionId\": \"7843491\",\n" +
                    "\"amount\": 50.00\n" +
                    "}\n";

            String refundRequest="{\n" +
                    "\"referenceTransactionId\": \"7843505\",\n" +
                    "\"amount\": 50.00\n" +
                    "}\n";

            String voidRequest="{\n" +
                    "\"authorizeTransactionId\": \"7685503\"\n" +
                    "}\n";

            String finalRequest="{\"VerifyTransactionId\":\"7942661\",\n" +
                    "\"PaRes\":\"\"}";

            //System.out.println("request----" + finalRequest);

            // String saleResponse = AlliedWalletUtils.doPostHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/saletransactions", token, request);
            //String sale3dResponse = AlliedWalletUtils.doPostHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/verifytransactions", token, request);
            // String authresponse = AlliedWalletUtils.doPostHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/authorizetransactions", token, request);
            // String captureResponse = AlliedWalletUtils.doPostHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/capturetransactions", token, captureRequest);
            // String refundResponse = AlliedWalletUtils.doPostHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/refundtransactions", token, refundRequest);
            // String voidResponse = AlliedWalletUtils.doPostHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/voidtransactions", token, voidRequest);
            //String inquiryResponse = AlliedWalletUtils.GETHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/transactions/12313", token);
            String finalResponse = AlliedWalletUtils.doPostHTTPSURLConnectionClient("https://api.alliedwallet.com/merchants/32434/ThreeDSaleTransactions", token, finalRequest);

            //System.out.println("Resposne-----"+finalResponse);

        }catch (Exception e){
            transactionLogger.error("Exception AlliedWalletPaymentGateway-----",e);
        }
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processSale-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String merchantId=gatewayAccount.getMerchantId();
        String siteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String attemptThreeD = "";
        String reject3DCard=commRequestVO.getReject3DCard();
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }
        String token=AlliedWalletUtils.getToken(accountId);

        transactionLogger.error("attemptThreeD----"+attemptThreeD);
        transactionLogger.error("reject3DCard----"+reject3DCard);
        try{
            if("Y".equals(is3dSupported) && !("Direct".equalsIgnoreCase(attemptThreeD))){
                transactionLogger.error("-----inside checkEnrollment-----");
                String checkEnrollmentRequest = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+commCardDetailsVO.getCardNum()+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+commCardDetailsVO.getExpMonth()+"\",\n" +
                        "\"expirationYear\":\""+commCardDetailsVO.getExpYear()+"\",\n" +
                        "\"cVVCode\":\""+commCardDetailsVO.getcVV()+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                String checkEnrollmentRequestLog = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+functions.maskingPan(commCardDetailsVO.getCardNum())+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"\",\n" +
                        "\"expirationYear\":\""+functions.maskingNumber(commCardDetailsVO.getExpYear())+"\",\n" +
                        "\"cVVCode\":\""+functions.maskingNumber(commCardDetailsVO.getcVV())+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                transactionLogger.error("checkEnrollmentRequest-----"+checkEnrollmentRequestLog);

                String checkEnrollmentResponse="";
                if(isTest){
                    transactionLogger.debug("inside Test----");
                    checkEnrollmentResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/verifytransactions"),token,checkEnrollmentRequest);
                }else {

                    transactionLogger.debug("inside Live----");
                    checkEnrollmentResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/verifytransactions"),token,checkEnrollmentRequest);
                }
                transactionLogger.error("checkEnrollmentResponse-----"+checkEnrollmentResponse);
                if(functions.isValueNull(checkEnrollmentResponse) && checkEnrollmentResponse.contains("{")){
                    String state="";
                    String status="";
                    String paymentId="";
                    String trackingId="";
                    String message="";
                    String PaReq="";
                    String AcsUrl="";
                    String EnrollmentStatus="";
                    HashMap map= new HashMap();
                    JSONObject jsonObject= new JSONObject(checkEnrollmentResponse);
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("id")){
                        paymentId=jsonObject.getString("id");
                    }
                    if(jsonObject.has("trackingId")){
                        trackingId=jsonObject.getString("trackingId");
                    }
                    if(jsonObject.has("message")){
                        message=jsonObject.getString("message");
                    }
                    if(jsonObject.has("result")){
                        JSONArray jsonArray= jsonObject.getJSONArray("result");
                        //System.out.println("Length-----"+jsonArray.length());
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            map.put(jsonObject1.getString("responseKey"),jsonObject1.getString("responseValue"));
                        }
                        PaReq= String.valueOf(map.get("PaReq"));
                        AcsUrl= String.valueOf(map.get("AcsUrl"));
                        EnrollmentStatus= String.valueOf(map.get("EnrollmentStatus"));
                    }
                    if("Successful".equalsIgnoreCase(status) && "Y".equals(EnrollmentStatus)){
                        if("Y".equals(reject3DCard)){
                            transactionLogger.error("rejecting 3d card as per configuration ");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            commResponseVO.setTransactionId(paymentId);
                            commResponseVO.setDescription("3D Enrolled Card");
                            commResponseVO.setRemark("3D Enrolled Card");
                            return commResponseVO;
                        }else {
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(AcsUrl);
                            commResponseVO.setPaReq(PaReq);
                            commResponseVO.setMd(trackingId + "@" + paymentId);
                            commResponseVO.setTerURL(RB.getString("TERM_URL"));
                            commResponseVO.setRemark("Pending for 3D Authentication");
                            commResponseVO.setDescription("Pending for 3D Authentication");
                            return commResponseVO;
                        }
                    }
                    else if ("Only3D".equalsIgnoreCase(attemptThreeD))
                    {
                        transactionLogger.error("Only 3D Card Required");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setDescription("Only 3D Card Required");
                        commResponseVO.setRemark("Only 3D Card Required");
                        return commResponseVO;
                    }
                    else if("N".equals(EnrollmentStatus) || "U".equals(EnrollmentStatus) || "E".equals(EnrollmentStatus)){

                        String finalRequest="{\"VerifyTransactionId\":\""+paymentId+"\",\n" +
                                "\"PaRes\":\""+""+"\"}";

                        transactionLogger.error("finalRequest-----"+finalRequest);

                        String finalResponse="";
                        if (isTest){
                            //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                            finalResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/ThreeDSaleTransactions"),token,finalRequest);
                        }else {
                            //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                            finalResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/ThreeDSaleTransactions"),token,finalRequest);
                        }
                        transactionLogger.error("finalResponse-----"+finalResponse);
                        String state1="";
                        String status1="";
                        String meassage="";
                        String trackingid="";
                        String newpaymentId="";
                        if(functions.isValueNull(finalResponse) && finalResponse.contains("{")){
                             jsonObject= new JSONObject(finalResponse);
                            if(jsonObject!=null){
                                if(jsonObject.has("state")){
                                    state1=jsonObject.getString("state");
                                }
                                if(jsonObject.has("status")){
                                    status1=jsonObject.getString("status");
                                }
                                if(jsonObject.has("message")){
                                    meassage=jsonObject.getString("message");
                                }
                                if(jsonObject.has("trackingId")){
                                    trackingid=jsonObject.getString("trackingId");
                                }
                                if(jsonObject.has("id")){
                                    newpaymentId=jsonObject.getString("id");
                                }

                                if("Successful".equalsIgnoreCase(status1)){
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                }else {
                                    commResponseVO.setStatus("fail");
                                }
                                commResponseVO.setRemark(meassage);
                                commResponseVO.setDescription(status1);
                                commResponseVO.setTransactionId(newpaymentId);
                                commResponseVO.setTransactionStatus(state1);
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                            }else {
                                commResponseVO.setRemark("Transaction Failed");
                                commResponseVO.setDescription("Technical Error");
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                            }
                        }else {
                            commResponseVO.setRemark("Transaction Failed");
                            commResponseVO.setDescription("Technical Error");
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        if(functions.isValueNull(message)){
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                        }
                        else {
                            commResponseVO.setRemark("Card enrollment failed");
                            commResponseVO.setDescription("Card enrollment failed");
                        }
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Technical Error");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
            }
            else
            {
                String saleRequest = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+commCardDetailsVO.getCardNum()+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+commCardDetailsVO.getExpMonth()+"\",\n" +
                        "\"expirationYear\":\""+commCardDetailsVO.getExpYear()+"\",\n" +
                        "\"cVVCode\":\""+commCardDetailsVO.getcVV()+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                String saleRequestLog = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+functions.maskingPan(commCardDetailsVO.getCardNum())+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"\",\n" +
                        "\"expirationYear\":\""+functions.maskingNumber(commCardDetailsVO.getExpYear())+"\",\n" +
                        "\"cVVCode\":\""+functions.maskingNumber(commCardDetailsVO.getcVV())+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                transactionLogger.error("saleRequest-----"+saleRequestLog);

                String saleResponse="";
                if(isTest){
                    //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                    transactionLogger.debug("inside Test----");
                    saleResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/saletransactions"),token,saleRequest);
                }else {
                    //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                    transactionLogger.debug("inside Live----");
                    saleResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/saletransactions"),token,saleRequest);
                }
                transactionLogger.error("saleResponse-----"+saleResponse);

                String state="";
                String status="";
                String meassage="";
                String trackingid="";
                String paymentId="";
                if(functions.isValueNull(saleResponse) && saleResponse.contains("{")){
                    JSONObject jsonObject= new JSONObject(saleResponse);
                    if(jsonObject!=null){
                        if(jsonObject.has("state")){
                            state=jsonObject.getString("state");
                        }
                        if(jsonObject.has("status")){
                            status=jsonObject.getString("status");
                        }
                        if(jsonObject.has("message")){
                            meassage=jsonObject.getString("message");
                        }
                        if(jsonObject.has("trackingId")){
                            trackingid=jsonObject.getString("trackingId");
                        }
                        if(jsonObject.has("id")){
                            paymentId=jsonObject.getString("id");
                        }

                        if("Successful".equalsIgnoreCase(status)){
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }else {
                            commResponseVO.setStatus("fail");
                        }
                        commResponseVO.setRemark(meassage);
                        commResponseVO.setDescription(status);
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setTransactionStatus(state);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }else {
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Technical Error");
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
            }
        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;

    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processAuthentication-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String merchantId=gatewayAccount.getMerchantId();
        String siteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String attemptThreeD = "";
        String reject3DCard=commRequestVO.getReject3DCard();
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }

        String token=AlliedWalletUtils.getToken(accountId);

        transactionLogger.error("attemptThreeD----"+attemptThreeD);
        transactionLogger.error("reject3DCard----"+reject3DCard);
        try{
            if("Y".equals(is3dSupported) && !("Direct".equalsIgnoreCase(attemptThreeD))){
                transactionLogger.error("-----inside checkEnrollment-----");
                String checkEnrollmentRequest = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+commCardDetailsVO.getCardNum()+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+commCardDetailsVO.getExpMonth()+"\",\n" +
                        "\"expirationYear\":\""+commCardDetailsVO.getExpYear()+"\",\n" +
                        "\"cVVCode\":\""+commCardDetailsVO.getcVV()+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                String checkEnrollmentRequestLog = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+functions.maskingPan(commCardDetailsVO.getCardNum())+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"\",\n" +
                        "\"expirationYear\":\""+functions.maskingNumber(commCardDetailsVO.getExpYear())+"\",\n" +
                        "\"cVVCode\":\""+functions.maskingNumber(commCardDetailsVO.getcVV())+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                transactionLogger.error("checkEnrollmentRequest-----"+checkEnrollmentRequestLog);
                String checkEnrollmentResponse="";
                if(isTest){
                    //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                    transactionLogger.debug("inside Test----");
                    checkEnrollmentResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/verifytransactions"),token,checkEnrollmentRequest);
                }else {
                    //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                    transactionLogger.debug("inside Live----");
                    checkEnrollmentResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/verifytransactions"),token,checkEnrollmentRequest);
                }
                transactionLogger.error("checkEnrollmentResponse-----"+checkEnrollmentResponse);
                if(functions.isValueNull(checkEnrollmentResponse) && checkEnrollmentResponse.contains("{")){
                    String state="";
                    String status="";
                    String paymentId="";
                    String trackingId="";
                    String message="";
                    String PaReq="";
                    String AcsUrl="";
                    String EnrollmentStatus="";
                    HashMap map= new HashMap();
                    JSONObject jsonObject= new JSONObject(checkEnrollmentResponse);
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("id")){
                        paymentId=jsonObject.getString("id");
                    }
                    if(jsonObject.has("trackingId")){
                        trackingId=jsonObject.getString("trackingId");
                    }
                    if(jsonObject.has("message")){
                        message=jsonObject.getString("message");
                    }
                    if(jsonObject.has("result")){
                        JSONArray jsonArray= jsonObject.getJSONArray("result");
                        //System.out.println("Length-----"+jsonArray.length());
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            map.put(jsonObject1.getString("responseKey"),jsonObject1.getString("responseValue"));
                        }
                        PaReq= String.valueOf(map.get("PaReq"));
                        AcsUrl= String.valueOf(map.get("AcsUrl"));
                        EnrollmentStatus= String.valueOf(map.get("EnrollmentStatus"));
                    }
                    if("Successful".equalsIgnoreCase(status) && "Y".equals(EnrollmentStatus)){
                        if("Y".equals(reject3DCard)){
                            transactionLogger.error("rejecting 3d card as per configuration ");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            commResponseVO.setTransactionId(paymentId);
                            commResponseVO.setDescription("3D Enrolled Card");
                            commResponseVO.setRemark("3D Enrolled Card");
                            return commResponseVO;
                        }else {
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(AcsUrl);
                            commResponseVO.setPaReq(PaReq);
                            commResponseVO.setMd(trackingId + "@" + paymentId);
                            commResponseVO.setTerURL(RB.getString("TERM_URL"));
                            commResponseVO.setRemark("Pending for 3D Authentication");
                            commResponseVO.setDescription("Pending for 3D Authentication");
                            return commResponseVO;
                        }
                    } else if ("Only3D".equalsIgnoreCase(attemptThreeD))
                    {
                        transactionLogger.error("Only 3D Card Required");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setDescription("Only 3D Card Required");
                        commResponseVO.setRemark("Only 3D Card Required");
                        return commResponseVO;
                    }
                    else if("N".equals(EnrollmentStatus) || "U".equals(EnrollmentStatus) ||"E".equals(EnrollmentStatus)){

                        String finalRequest="{\"VerifyTransactionId\":\""+paymentId+"\",\n" +
                                "\"PaRes\":\""+""+"\"}";

                        transactionLogger.error("finalRequest-----"+finalRequest);

                        String finalResponse="";
                        if (isTest){
                            //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                            finalResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/ThreeDAuthorizeTransactions"),token,finalRequest);
                        }else {
                            //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                            finalResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/ThreeDAuthorizeTransactions"),token,finalRequest);
                        }
                        transactionLogger.error("finalResponse-----"+finalResponse);
                        String state1="";
                        String status1="";
                        String meassage="";
                        String trackingid="";
                        String newpaymentId="";
                        if(functions.isValueNull(finalResponse) && finalResponse.contains("{")){
                            jsonObject= new JSONObject(finalResponse);
                            if(jsonObject!=null){
                                if(jsonObject.has("state")){
                                    state1=jsonObject.getString("state");
                                }
                                if(jsonObject.has("status")){
                                    status1=jsonObject.getString("status");
                                }
                                if(jsonObject.has("message")){
                                    meassage=jsonObject.getString("message");
                                }
                                if(jsonObject.has("trackingId")){
                                    trackingid=jsonObject.getString("trackingId");
                                }
                                if(jsonObject.has("id")){
                                    newpaymentId=jsonObject.getString("id");
                                }

                                if("Successful".equalsIgnoreCase(status1)){
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                }else {
                                    commResponseVO.setStatus("fail");
                                }
                                commResponseVO.setRemark(meassage);
                                commResponseVO.setDescription(status1);
                                commResponseVO.setTransactionId(newpaymentId);
                                commResponseVO.setTransactionStatus(state1);
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                            }else {
                                commResponseVO.setRemark("Transaction Failed");
                                commResponseVO.setDescription("Technical Error");
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                            }
                        }else {
                            commResponseVO.setRemark("Transaction Failed");
                            commResponseVO.setDescription("Technical Error");
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        if(functions.isValueNull(message)){
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                        }
                        else {
                            commResponseVO.setRemark("Card enrollment failed");
                            commResponseVO.setDescription("Card enrollment failed");
                        }
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Technical Error");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
            }else {
                String authRequest = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+commCardDetailsVO.getCardNum()+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+commCardDetailsVO.getExpMonth()+"\",\n" +
                        "\"expirationYear\":\""+commCardDetailsVO.getExpYear()+"\",\n" +
                        "\"cVVCode\":\""+commCardDetailsVO.getcVV()+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                String authRequestLog = "{\"siteId\":\""+siteId+"\",\n" +
                        "\"amount\":\""+transactionDetailsVO.getAmount()+"\",\n" +
                        "\"currency\":\""+transactionDetailsVO.getCurrency()+"\",\n" +
                        "\"firstName\":\""+commAddressDetailsVO.getFirstname()+"\",\n" +
                        "\"lastName\":\""+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"phone\":\""+commAddressDetailsVO.getPhone()+"\",\n" +
                        "\"addressLine1\":\""+commAddressDetailsVO.getStreet()+"\",\n" +
                        "\"city\":\""+commAddressDetailsVO.getCity()+"\",\n" +
                        "\"state\":\""+commAddressDetailsVO.getState()+"\",\n" +
                        "\"countryId\":\""+commAddressDetailsVO.getCountry()+"\",\n" +
                        "\"postalCode\":\""+commAddressDetailsVO.getZipCode()+"\",\n" +
                        "\"email\":\""+commAddressDetailsVO.getEmail()+"\",\n" +
                        "\"cardNumber\":\""+functions.maskingPan(commCardDetailsVO.getCardNum())+"\",\n" +
                        "\"nameOnCard\":\""+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"\",\n" +
                        "\"expirationMonth\":\""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"\",\n" +
                        "\"expirationYear\":\""+functions.maskingNumber(commCardDetailsVO.getExpYear())+"\",\n" +
                        "\"cVVCode\":\""+functions.maskingNumber(commCardDetailsVO.getcVV())+"\",\n" +
                        "\"iPAddress\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                        "\"trackingID\" : \""+trackingID+"\",\n" +
                        "\"isInitialForRecurring\":false }";

                transactionLogger.error("authRequest-----"+authRequestLog);

                String authResponse="";
                if(isTest){
                    transactionLogger.debug("inside Test----");
                    //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                    authResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/authorizetransactions"),token,authRequest);
                }else {
                    transactionLogger.debug("inside Live----");
                    //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                    authResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/authorizetransactions"),token,authRequest);
                }
                transactionLogger.error("authResponse-----"+authResponse);

                String state="";
                String status="";
                String meassage="";
                String trackingid="";
                String paymentId="";
                if(functions.isValueNull(authResponse) && authResponse.contains("{")){
                    JSONObject jsonObject= new JSONObject(authResponse);
                    if(jsonObject!=null){
                        if(jsonObject.has("state")){
                            state=jsonObject.getString("state");
                        }
                        if(jsonObject.has("status")){
                            status=jsonObject.getString("status");
                        }
                        if(jsonObject.has("message")){
                            meassage=jsonObject.getString("message");
                        }
                        if(jsonObject.has("trackingId")){
                            trackingid=jsonObject.getString("trackingId");
                        }
                        if(jsonObject.has("id")){
                            paymentId=jsonObject.getString("id");
                        }

                        if("Successful".equalsIgnoreCase(status)){
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }else {
                            commResponseVO.setStatus("fail");
                        }
                        commResponseVO.setRemark(meassage);
                        commResponseVO.setDescription(status);
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setTransactionStatus(state);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                    }else {
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Technical Error");
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                    }
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }
            }
        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "processAuth()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("Inside processCapture-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();

        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try{
            String captureRequest = "{\n" +
                    "\"authorizeTransactionId\": \""+transactionDetailsVO.getPreviousTransactionId()+"\",\n" +
                    "\"amount\": "+transactionDetailsVO.getAmount()+"\n" +
                    "}\n";

            transactionLogger.error("captureRequest-----"+captureRequest);

            String token=AlliedWalletUtils.getToken(accountId);
            String captureResponse="";
            if(isTest){
                //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                transactionLogger.debug("inside Test----");
                captureResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/capturetransactions"),token,captureRequest);
            }else {
                //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                transactionLogger.debug("inside Live----");
                captureResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/capturetransactions"),token,captureRequest);
            }
            transactionLogger.error("captureResponse-----"+captureResponse);

            String state="";
            String status="";
            String meassage="";
            String paymentId="";
            if(functions.isValueNull(captureResponse) && captureResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(captureResponse);
                if(jsonObject!=null){
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("message")){
                        meassage=jsonObject.getString("message");
                    }
                    if(jsonObject.has("id")){
                        paymentId=jsonObject.getString("id");
                    }

                    if("Successful".equalsIgnoreCase(status)){
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setRemark(meassage);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setTransactionStatus(state);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                }
            }else {
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }

        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processRefund-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();

        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try{
            String refundRequest = "{\n" +
                    "\"referenceTransactionId\": \""+transactionDetailsVO.getPreviousTransactionId()+"\",\n" +
                    "\"amount\": "+transactionDetailsVO.getAmount()+"\n" +
                    "}\n";

            transactionLogger.error("refundRequest-----"+refundRequest);

            String token=AlliedWalletUtils.getToken(accountId);
            String refundResponse="";
            if(isTest){
                transactionLogger.debug("inside Test----");
                //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                refundResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/refundtransactions"),token,refundRequest);
            }else {
                transactionLogger.debug("inside Live----");
                //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                refundResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/refundtransactions"),token,refundRequest);
            }
            transactionLogger.error("refundResponse-----"+refundResponse);

            String state="";
            String status="";
            String meassage="";
            String paymentId="";
            if(functions.isValueNull(refundResponse) && refundResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(refundResponse);
                if(jsonObject!=null){
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("message")){
                        meassage=jsonObject.getString("message");
                    }
                    if(jsonObject.has("id")){
                        paymentId=jsonObject.getString("id");
                    }

                    if("Successful".equalsIgnoreCase(status)){
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setRemark(meassage);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setTransactionStatus(state);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                }
            }else {
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            }

        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("Inside processVoid-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();

        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try{
            String voidRequest = "{\n" +
                    "\"authorizeTransactionId\": \""+transactionDetailsVO.getPreviousTransactionId()+"\"\n" +
                    "}\n";

            transactionLogger.error("voidRequest-----"+voidRequest);

            String token=AlliedWalletUtils.getToken(accountId);
            String voidResponse="";
            if(isTest){
                transactionLogger.debug("inside Test----");
                //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                voidResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/voidtransactions"),token,voidRequest);
            }else {
                transactionLogger.debug("inside Live----");
                //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                voidResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/voidtransactions"),token,voidRequest);
            }
            transactionLogger.error("voidResponse-----"+voidResponse);

            String state="";
            String status="";
            String meassage="";
            String paymentId="";
            if(functions.isValueNull(voidResponse) && voidResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(voidResponse);
                if(jsonObject!=null){
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("message")){
                        meassage=jsonObject.getString("message");
                    }
                    if(jsonObject.has("id")){
                        paymentId=jsonObject.getString("id");
                    }

                    if("Successful".equalsIgnoreCase(status)){
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setRemark(meassage);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setTransactionStatus(state);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                }
            }else {
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }

        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processInquiry-----");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        Functions functions= new Functions();
        boolean isTest=gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();

        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String transactionId=transactionDetailsVO.getPreviousTransactionId();

        transactionLogger.debug("TransactionId-----"+transactionId);

        try{
            String inquiryRequest = "{\n" +
                    "\"authorizeTransactionId\": \""+transactionId+"\"\n" +
                    "}\n";

            transactionLogger.error("inquiryRequest-----"+inquiryRequest);
            String token=AlliedWalletUtils.getToken(accountId);
            String inquiryResponse="";
            if(isTest){
                transactionLogger.debug("inside Test----");
                //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                inquiryResponse =AlliedWalletUtils.GETHTTPSURLConnectionClient((RB.getString("TEST_URL") + merchantId + "/transactions/" + transactionId), token);
            }else {
                transactionLogger.debug("inside Live----");
                //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                inquiryResponse =AlliedWalletUtils.GETHTTPSURLConnectionClient((RB.getString("LIVE_URL") + merchantId + "/transactions/"+transactionId), token);
            }
            transactionLogger.error("inquiryResponse-----"+inquiryResponse);

            String state="";
            String status="";
            String amount="";
            String currency="";
            String paymentDate="";
            String merchantid="";
            String newdate="";
            String paymentId="";
            String transType="";
            String message="";
            if(functions.isValueNull(inquiryResponse) && inquiryResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(inquiryResponse);
                if(jsonObject!=null){
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                        transType=AlliedWalletUtils.transType(state);
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("message")){
                        message=jsonObject.getString("message");
                    }
                    if(jsonObject.has("amount")){
                        amount=jsonObject.getString("amount");
                    }
                    if(jsonObject.has("currency")){
                        currency=jsonObject.getString("currency");
                    }
                    if(jsonObject.has("paymentDate")){
                        paymentDate=jsonObject.getString("paymentDate");
                    }
                    if(jsonObject.has("merchantId")){
                        merchantid=jsonObject.getString("merchantId");
                    }
                    if(jsonObject.has("date")){
                        newdate=jsonObject.getString("date");
                    }
                    if(jsonObject.has("id")){
                        paymentId=jsonObject.getString("id");
                    }
                    commResponseVO.setStatus(status);
                    if(functions.isValueNull(message)){
                        commResponseVO.setRemark(message);
                    }else {
                        commResponseVO.setRemark(status);
                    }
                    commResponseVO.setDescription(status);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setBankTransactionDate(paymentDate);
                    commResponseVO.setMerchantId(merchantid);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setTransactionStatus(state + " successful");
                    commResponseVO.setResponseTime(newdate);
                    commResponseVO.setTransactionType(transType);
                    commResponseVO.setAuthCode("-");
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAuthCode("-");
                }
            }else {
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                commResponseVO.setAuthCode("-");
            }

        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processPayout",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

    }

    public GenericResponseVO process3DSaleConfirmation(GenericRequestVO requestVO,String PaRes) throws PZTechnicalViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DSaleConfirmation-----");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        Functions functions= new Functions();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();

        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        String paymentId=commTransactionDetailsVO.getPreviousTransactionId();

        transactionLogger.debug("Previous trasactionid-----"+paymentId);
        try{
            String final3DRequest="{\"VerifyTransactionId\":\""+paymentId+"\",\n" +
                    "\"PaRes\":\""+PaRes+"\"}";

            transactionLogger.error("final3DSaleRequest-----"+final3DRequest);

            String token=AlliedWalletUtils.getToken(accountId);
            String final3DSaleResponse="";
            if (isTest){
                //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                final3DSaleResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/ThreeDSaleTransactions"),token,final3DRequest);
            }else {
                //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                final3DSaleResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/ThreeDSaleTransactions"),token,final3DRequest);
            }
            transactionLogger.error("final3DSaleResponse-----"+final3DSaleResponse);
            String state="";
            String status="";
            String meassage="";
            String trackingid="";
            String newpaymentId="";
            if(functions.isValueNull(final3DSaleResponse) && final3DSaleResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(final3DSaleResponse);
                if(jsonObject!=null){
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("message")){
                        meassage=jsonObject.getString("message");
                    }
                    if(jsonObject.has("trackingId")){
                        trackingid=jsonObject.getString("trackingId");
                    }
                    if(jsonObject.has("id")){
                        newpaymentId=jsonObject.getString("id");
                    }

                    if("Successful".equalsIgnoreCase(status)){
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setRemark(meassage);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(newpaymentId);
                    commResponseVO.setTransactionStatus(state);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
            }else {
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            }
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;

    }

    public GenericResponseVO process3DAuthConfirmation( GenericRequestVO requestVO,String PaRes) throws PZTechnicalViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DAuthConfirmation-----");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        Functions functions= new Functions();
        Date date= new Date();
        DateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();

        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        String paymentId=commTransactionDetailsVO.getPreviousTransactionId();

        transactionLogger.debug("Previous trasactionid-----"+paymentId);
        try{
            String final3DAuthRequest="{\"VerifyTransactionId\":\""+paymentId+"\",\n" +
                    "\"PaRes\":\""+PaRes+"\"}";

            transactionLogger.error("final3DAuthRequest-----"+final3DAuthRequest);
            String token=AlliedWalletUtils.getToken(accountId);
            String final3DAuthResponse="";
            if (isTest){
                //String token=RB.getString("TEST_AUTHORIZATION_TOKEN");
                final3DAuthResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("TEST_URL")+merchantId+"/ThreeDAuthorizeTransactions"),token,final3DAuthRequest);
            }else {
                //String token=RB.getString("LIVE_AUTHORIZATION_TOKEN");
                final3DAuthResponse =AlliedWalletUtils.doPostHTTPSURLConnectionClient((RB.getString("LIVE_URL")+merchantId+"/ThreeDAuthorizeTransactions"),token,final3DAuthRequest);
            }
            transactionLogger.error("final3DAuthResponse-----"+final3DAuthResponse);
            String state="";
            String status="";
            String meassage="";
            String trackingid="";
            String newpaymentId="";
            if(functions.isValueNull(final3DAuthResponse) && final3DAuthResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(final3DAuthResponse);
                if(jsonObject!=null){
                    if(jsonObject.has("state")){
                        state=jsonObject.getString("state");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("message")){
                        meassage=jsonObject.getString("message");
                    }
                    if(jsonObject.has("trackingId")){
                        trackingid=jsonObject.getString("trackingId");
                    }
                    if(jsonObject.has("id")){
                        newpaymentId=jsonObject.getString("id");
                    }

                    if("Successful".equalsIgnoreCase(status)){
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setRemark(meassage);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(newpaymentId);
                    commResponseVO.setTransactionStatus(state);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                }else {
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Technical Error");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                }
            }else {
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
            }
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(AlliedWalletPaymentGateway.class.getName(), "process3DAuthConfirmation()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;

    }
}
