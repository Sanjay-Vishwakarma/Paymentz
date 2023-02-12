package com.payment.paysend;

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
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Rihen on 5/7/2019.
 */
public class PaySendPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySendPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.paysend");

    public static final String GATEWAY_TYPE = "paysend";

    public PaySendPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays() {   return null; }

    public static void main(String[] args)
    {
        String privateKey = "97c51ef6da884acfcdad508afcbac0e7";
        String publicKey  = "01470b9897ebefee7d9d9123f7b35547";

        try
        {
/*
            // USER
            String userUrl = "https://api2.paysend.com/v1/user/resolve";
            String identifier = "1";
            String ip = "192.168.0.1";
            String display_name = "Rihen";

            List uList=Arrays.asList(identifier,ip,publicKey,display_name);
            String userSignature = PaySendUtils.getSignature(uList, privateKey);
            System.out.println("userSignature ------"+userSignature);

            String userRequest = "project=" + publicKey +
                    "&identifier=" + identifier +
                    "&ip=" + ip +
                    "&display_name="+ display_name +
                    "&signature=" + userSignature;

            System.out.println("userRequest  -----"+ userRequest);
            String userResponse = PaySendUtils.doPostHTTPSURLConnectionClient(userUrl, userRequest);
            System.out.println("userResponse-----"+userResponse);

            JSONObject jsonUser =new JSONObject(userResponse);
            String userId = "";

            if(jsonUser.has("id")){
                userId = jsonUser.getString("id");
            }
            System.out.println("user id --------"+userId);

            //CARD
            String cardUrl = "https://api2.paysend.com/v1/card/getToken";

            String cardRequest ="?project="+ publicKey +
                    "&number=4012001037141112"+
                    "&expiration_month=4"+
                    "&expiration_year=2020"+
                    "&security_code=123";

            System.out.println("cardRequest  -----"+ cardRequest);
            String cardResponse = PaySendUtils.doGetHTTPSURLConnectionClient(cardUrl + cardRequest);
            System.out.println("cardResponse-----"+cardResponse);

            JSONObject jsonCard =new JSONObject(cardResponse);
            String cardToken = "";

            if(jsonCard.has("id")){
                cardToken = jsonCard.getString("id");
            }

            System.out.println("cardToken --------"+cardToken);

            // SALE
            String price = "1.00";
            String currency = "USD";
            String order_Id = "123458";
            String description = "Buying ice cream";
            String threeD = "1";
            String return_url = "http://localhost:8081/transaction/Common3DFrontEndServlet";

            List saleList=Arrays.asList(publicKey,userId,cardToken,price,currency,order_Id,description,threeD,return_url);
            String saleSignature = PaySendUtils.getSignature(saleList, privateKey);
            System.out.println("saleSignature ------"+saleSignature);


            String url = "https://api2.paysend.com/v1/card/process";
            String request= "project="+ publicKey +
                    "&user="+ userId +
                    "&card_token="+ cardToken +
                    "&price="+ price +
                    "&currency="+ currency +
                    "&order_id="+ order_Id +
                    "&description="+ description +
                    "&3ds="+threeD+
                    "&acs_return_url="+ return_url +
                    "&signature="+saleSignature;

            System.out.println("Request----"+request);
            String response=PaySendUtils.doPostHTTPSURLConnectionClient(url,request);
            System.out.println("Response-----"+response);
*/


            // 3D SALE returns pares which needs to be decoded, after form post pares and md are returned to FrontEndServlet also needs to be decoded which is then used
            // for the below request


/*

            String pares = "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";
            String md= "eyJ0cmFuc2FjdGlvbklkIjoiMTU1ODA5NjIxNjI4MjExNDU2NSIsImNhcmRUb2tlbiI6ImEzM2I4ZjM4ZjI3M2JmMGJhNTFmMjMyOWEyNDc2ZGRlZmY2ODIxZTRiOTY0NWRiMzk5OWZlZjZlMDYwYzhkNjciLCJyZW1lbWJlciI6ZmFsc2UsInJlY3VycmluZ1JlcXVlc3RlZCI6ZmFsc2V9";

            System.out.println("threeDSignature ------"+pares);

            List threeDList=Arrays.asList(publicKey,md,pares);
            String threeDSignature = PaySendUtils.getSignature(threeDList, privateKey);
            System.out.println("threeDSignature ------"+threeDSignature);

            String threeDUrl = "https://api2.paysend.com/v1/card/authenticate";
//            String threeDRequest = "project="+publicKey+
//                    "&PaRes="+pares+
//                    "&MD="+md+
//                    "&signature="+threeDSignature;

            String json = "{\n" +
                    "\"project\":\"01470b9897ebefee7d9d9123f7b35547\",\n" +
                    "\"PaRes\":\"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\",\n" +
                    "\"MD\":\"eyJ0cmFuc2FjdGlvbklkIjoiMTU1ODA5NjIxNjI4MjExNDU2NSIsImNhcmRUb2tlbiI6ImEzM2I4ZjM4ZjI3M2JmMGJhNTFmMjMyOWEyNDc2ZGRlZmY2ODIxZTRiOTY0NWRiMzk5OWZlZjZlMDYwYzhkNjciLCJyZW1lbWJlciI6ZmFsc2UsInJlY3VycmluZ1JlcXVlc3RlZCI6ZmFsc2V9\",\n" +
                    "\"signature\":\"599ca29b02f08ff9a598cca7777937c1adb73fe3913d47153b4c721c28fba820\"\n" +
                    "}";

            System.out.println("threeDRequest----"+threeDRequest);
            //PaySendUtils.verifySignature(threeDRequest,privateKey);

            String threeDResponse=PaySendUtils.doPostHTTPSURLConnectionClient(threeDUrl,json);
            System.out.println("threeDResponse-----"+threeDResponse);

*/

/*
            // REFUND

            String refundUrl = "https://api2.paysend.com/v1/transaction/reverse";

            String id= "1557840867357632375";
            String amount = "1.00";

            List refundList=Arrays.asList(publicKey,id,amount);
            String refundSignature = PaySendUtils.getSignature(refundList, privateKey);
            System.out.println("refundSignature ------"+refundSignature);

            String refundRequest= "project="+publicKey+
                    "&id="+id+
                    "&amount="+amount+
                    "&signature="+refundSignature;

            System.out.println("refundRequest----"+refundRequest);
            String refundResponse=PaySendUtils.doPostHTTPSURLConnectionClient(refundUrl,refundRequest);
            System.out.println("refundResponse-----"+refundResponse);
*/


/*            // INQUIRY

            String inquiryUrl = "https://api2.paysend.com/v1/transaction/status";

            String id= "1557737262232594311";

            List inquiryList=Arrays.asList(publicKey,id);
            String inquirySignature = PaySendUtils.getSignature(inquiryList, privateKey);
            System.out.println("inquirySignature ------"+inquirySignature);

            String inquiryRequest="?project="+publicKey+
                    "&id="+id+
                    "&signature="+inquirySignature;

            System.out.println("inquiryRequest  -----"+ inquiryRequest);
            String inquiryResponse = PaySendUtils.doGetHTTPSURLConnectionClient(inquiryUrl + inquiryRequest);
            System.out.println("inquiryResponse-----"+inquiryResponse);*/


            // Handling 3D response for PROCESSING status

           /* String response="--------------------------8fec95dfdf149f7aContent-Disposition:\n" +
                    " form-data; name=\"project\"42--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"id\"1567172580201799312--------------------------8fec95dfdf14\n" +
                    "9f7aContent-Disposition: form-data; name=\"reference_id\"--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"order_id\"105606------------------\n" +
                    "--------8fec95dfdf149f7aContent-Disposition: form-data; name=\"amount\"--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"price\"1.00---------\n" +
                    "-----------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"deposit\"0.0000--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"fee\n" +
                    "\"0.0000--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"earned\"1.0000--------------------------8fec95dfdf149f7aContent-Disposition: form-\n" +
                    "data; name=\"vat_rate\"0.00--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"vat_amount\"0.0000--------------------------8fec95dfdf149f7aCont\n" +
                    "ent-Disposition: form-data; name=\"currency\"EUR--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"type\"transaction--------------------------\n" +
                    "8fec95dfdf149f7aContent-Disposition: form-data; name=\"status\"completed--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"sandbox\"1---------\n" +
                    "-----------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"reason\"--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"user\"12415\n" +
                    "--------------------------8fec95dfdf149f7aContent-Disposition: form-data; name=\"signature\"28b0b851ba83a208ca8a570fc46031f21207fbc6f1b11fbc55f2621fc2ca8e4c--------------\n" +
                    "------------8fec95dfdf149f7a--";


            String data[]= response.split("--------------------------");

*/
            /*System.out.println("##"+data[0]);
            System.out.println("##"+data[1]);

            String v1=data[1];

            String splitV1[]=v1.split("=");

            System.out.println("@@@"+splitV1[0]);
            System.out.println("@@@"+splitV1[1]);

            String V2=splitV1[1];

            String splitV2[]=V2.split("\"");

            System.out.println("&&&"+splitV2[0]);
            System.out.println("&&&"+splitV2[1]);
            System.out.println("&&&"+splitV2[2]);*/


            /*String status="";
            String orderId="";
            String amount = "";
            String currency = "";
            Functions functions  =  new Functions();
            for (String d : data){
                if(functions.isValueNull(d)){
                    String s1[]=d.split("name=\"");
                    for (String d2 : s1){
                        if(d2.contains("status\"")){
                            System.out.println("d2---"+d2);
                            String a1[]=d2.split("\"");
                            status=a1[1];
                        }
                        if(d2.contains("order_id\"")){
                            System.out.println("d2---"+d2);
                            String a1[]=d2.split("\"");
                            orderId=a1[1];
                        }
                        if(d2.contains("currency\"")){
                            System.out.println("d2---"+d2);
                            String a1[]=d2.split("\"");
                            currency=a1[1];
                        }
                        if(d2.contains("price\"")){
                            System.out.println("d2---"+d2);
                            String a1[]=d2.split("\"");
                            amount=a1[1];
                        }
                    }
                }
            }

            System.out.println("status-----"+status);
            System.out.println("orderId-----"+orderId);
            System.out.println("currency-----"+currency);
            System.out.println("amount-----"+amount);*/
            /*List uList=Arrays.asList("567890",null,publicKey,"test test");
            String userSignature = PaySendUtils.getSignature(uList, privateKey);
            System.out.println("userSignature--->"+userSignature);*/
            String payoutUrl="https://client.paysend.com/api/clients/ae9339c1-63f3-4ae3-b9dd-d2c413f1171d/payout";
            String authenticateUrl="https://client.paysend.com/api/public/authenticate";
            String authenticateRequest="{" +
                    "  \"username\": \"\"," +
                    "  \"password\": \"\"" +
                    "}";
            String authToken="97c51ef6da884acfcdad508afcbac0e7";
            String payoutRequest="{" +
                    "  \"amount\": \"100.00\"," +
                    "  \"currency\": \"EUR\"," +
                    "  \"description\": \"To client #123\"," +
                    "  \"cardToken\": \"044a9a562b8f9ec27879774cd4f44660ef377210f0ce0d8e92921baab3e6d6ab\"," +
                    "  \"cardExpirationDate\": \"12/34\"," +
                    "  \"cardHolder\": \"John Smith\"," +
                    "  \"fullName\": \"Jane Doe\"," +
                    "  \"firstName\": \"Jane\"," +
                    "  \"lastName\": \"Doe\"," +
                    "  \"country\": \"GB\"," +
                    "  \"city\": \"Birmingham\"," +
                    "  \"address\": \"27 Colmore Row\"," +
                    "  \"postalCode\": \"B3 2EW\"," +
                    "  \"beneficiaryFirstName\": \"John\"," +
                    "  \"beneficiaryLastName\": \"Smith\"," +
                    "  \"receiverBankBic\": \"LOYDGB2L\"," +
                    "  \"phone\": \"1234567890\"," +
                    "  \"birthDate\": \"01.12.2034\"" +
                    "}";
            String userResponse = PaySendUtils.doPostHTTPSURLConnectionClient(payoutUrl, payoutRequest,authToken);
            System.out.println("userResponse--->"+userResponse);

        }
        catch (Exception e)
        {
            /*e.printStackTrace();
            System.out.println("Exception -----"+e);*/
        }

    }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        boolean isTest = gatewayAccount.isTest();

        String publicKey  = gatewayAccount.getMerchantId();
        String privateKey = gatewayAccount.getFRAUD_FTP_PASSWORD();

        transactionLogger.error("public key ---- "+publicKey);
        transactionLogger.error("private key ---- "+privateKey);

        try
        {
            // USER
            String identifier = "";

            if(functions.isValueNull(commAddressDetailsVO.getCustomerid()))
            {
                identifier = commAddressDetailsVO.getCustomerid();
            }
            else
            {
                identifier = commTransactionDetailsVO.getOrderId();
            }

            String ip = "";
            String display_name = commAddressDetailsVO.getFirstname() +" "+commAddressDetailsVO.getLastname();
            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                ip=commAddressDetailsVO.getCardHolderIpAddress();
            transactionLogger.error("identifier--->"+identifier);
            transactionLogger.error("ip--->"+ip);
            transactionLogger.error("display_name--->"+display_name);
            List uList=Arrays.asList(identifier,ip,publicKey,display_name);
            String userSignature = PaySendUtils.getSignature(uList, privateKey);

            JSONObject userRequest = new JSONObject();

            userRequest.put("project", publicKey);
            userRequest.put("identifier", identifier);
            userRequest.put("ip", ip);
            userRequest.put("display_name", display_name);
            userRequest.put("signature", userSignature);

/*
            String userRequest = "project=" + publicKey +
                    "&identifier="          + identifier +
                    "&ip="                  + ip +
                    "&display_name="        + display_name +
                    "&signature="           + userSignature;*/

            transactionLogger.error("userRequest  -----" + userRequest);

            String userResponse = "";
            if (isTest)
            {
                userResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("USER"), userRequest.toString());
            }
            else
            {
                userResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("USER"), userRequest.toString());
            }

            transactionLogger.error("userResponse-----" + userResponse);

            JSONObject jsonUser =new JSONObject(userResponse);
            String userId = "";
            if(jsonUser.has("error"))
            {
                commResponseVO.setStatus("fail");
                if(jsonUser.getJSONObject("error").has("message"))
                {
                    commResponseVO.setRemark(jsonUser.getJSONObject("error").getString("message"));
                    commResponseVO.setDescription(jsonUser.getJSONObject("error").getString("message"));
                }
                if(jsonUser.getJSONObject("error").has("code"))
                    commResponseVO.setErrorCode(jsonUser.getString("code"));

                return commResponseVO;
            }
            if(jsonUser.has("id")){
                userId = jsonUser.getString("id");
            }

/*            JSONObject cardRequest = new JSONObject();

            cardRequest.put("project", publicKey);
            cardRequest.put("number", commCardDetailsVO.getCardNum());
            cardRequest.put("expiration_month", commCardDetailsVO.getExpMonth());
            cardRequest.put("expiration_year", commCardDetailsVO.getExpYear());
            cardRequest.put("security_code", commCardDetailsVO.getcVV());*/

            //CARD
            String cardRequest ="?project=" + publicKey +
                    "&number="              + commCardDetailsVO.getCardNum() +
                    "&expiration_month="    + commCardDetailsVO.getExpMonth()+
                    "&expiration_year="     + commCardDetailsVO.getExpYear() +
                    "&security_code="       + commCardDetailsVO.getcVV();

            String cardRequestLogs ="?project=" + publicKey +
                    "&number="              + functions.maskingPan(commCardDetailsVO.getCardNum()) +
                    "&expiration_month="    + functions.maskingNumber(commCardDetailsVO.getExpMonth())+
                    "&expiration_year="     + functions.maskingNumber(commCardDetailsVO.getExpYear()) +
                    "&security_code="       + functions.maskingNumber(commCardDetailsVO.getcVV());

            transactionLogger.error("cardRequest  -----for "+trackingID+ "----" + cardRequestLogs);

            String cardResponse ="";
            if (isTest)
            {
                cardResponse = PaySendUtils.doGetHTTPSURLConnectionClient(RB.getString("GETTOKEN") + cardRequest.toString());
            }
            else
            {
                cardResponse = PaySendUtils.doGetHTTPSURLConnectionClient(RB.getString("GETTOKEN") + cardRequest.toString());
            }
            transactionLogger.error("cardResponse-----for "+trackingID+ "----" +cardResponse);

            JSONObject jsonCard =new JSONObject(cardResponse);
            String cardToken = "";
            if(jsonCard.has("error"))
            {
                commResponseVO.setStatus("fail");
                if(jsonCard.getJSONObject("error").has("message"))
                {
                    commResponseVO.setRemark(jsonCard.getJSONObject("error").getString("message"));
                    commResponseVO.setDescription(jsonCard.getJSONObject("error").getString("message"));
                }
                if(jsonCard.getJSONObject("error").has("code"))
                    commResponseVO.setErrorCode(jsonCard.getString("code"));

                return commResponseVO;
            }
            if(jsonCard.has("id")){
                cardToken = jsonCard.getString("id");
            }

            // SALE
            String price =  commTransactionDetailsVO.getAmount();
            String currency = commTransactionDetailsVO.getCurrency();
            String order_Id = trackingID;
            String description = commTransactionDetailsVO.getOrderDesc();
            String return_url = RB.getString("REDIRECT_URL")+trackingID;
            String threeD = "1";

            String threeDParam = "";
            List saleList = null;
            if(is3dSupported.equalsIgnoreCase("Y"))
            {
                threeDParam = "&3ds="+threeD+""+"&acs_return_url="+ return_url+"";
                saleList=Arrays.asList(publicKey,userId,cardToken,price,currency,order_Id,description,threeD,return_url);
            }
            else
            {
                saleList=Arrays.asList(publicKey,userId,cardToken,price,currency,order_Id,description);
            }


            String saleSignature = PaySendUtils.getSignature(saleList, privateKey);


            JSONObject saleRequest = new JSONObject();

            saleRequest.put("project", publicKey);
            saleRequest.put("user", userId);
            saleRequest.put("card_token", cardToken);
            saleRequest.put("price", price);
            saleRequest.put("currency",currency);
            saleRequest.put("order_id", order_Id);
            saleRequest.put("description", description);
            if("Y".equalsIgnoreCase(is3dSupported))
            {
                saleRequest.put("3ds", threeD);
                saleRequest.put("acs_return_url", return_url);
            }

            saleRequest.put("signature", saleSignature);

/*            String saleRequest="project="   + publicKey +
                    "&user="                + userId +
                    "&card_token="          + cardToken +
                    "&price="               + price +
                    "&currency="            + currency +
                    "&order_id="            + order_Id +
                    "&description="         + description +
                    threeDParam +
                    "&signature="           + saleSignature;*/

            transactionLogger.error("saleRequest---- for "+trackingID+ "----" + saleRequest);

            String saleResponse = "";
            if (isTest)
            {
                saleResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE"), saleRequest.toString());
            }
            else
            {
                saleResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE"), saleRequest.toString());
            }

            transactionLogger.error("saleResponse----- " + saleResponse);


            if(functions.isValueNull(saleResponse))
            {
                JSONObject jsonSaleResponse =new JSONObject(saleResponse);

                String tmpl_amount = "";
                String tmpl_currency = "";
                String status = "";

                if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount())) {
                    tmpl_amount=commAddressDetailsVO.getTmpl_amount();
                }
                else {
                    tmpl_amount = price;
                }

                if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency())) {
                    tmpl_currency=commAddressDetailsVO.getTmpl_currency();
                }
                else {
                    tmpl_currency = currency;
                }

                if(jsonSaleResponse.has("id")){
                    transactionLogger.error("id ---- "+jsonSaleResponse.getString("id"));
                    commResponseVO.setTransactionId(jsonSaleResponse.getString("id"));
                    transactionLogger.error("transaction id = "+commResponseVO.getTransactionId());
                }

                if(jsonSaleResponse.has("status")){
                    transactionLogger.error("status ---- "+jsonSaleResponse.getString("status"));
                    status = jsonSaleResponse.getString("status");
                    transactionLogger.error("status = "+status);
                }

                if(jsonSaleResponse.has("acs") && status.equalsIgnoreCase("authenticating"))
                {
                    transactionLogger.error("inside----3dConfimation-----");
                    JSONObject threeDObject =jsonSaleResponse.getJSONObject("acs");
                    String url3d = threeDObject.getString("url").replace("\\/", "/");

                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setUrlFor3DRedirect(url3d);
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription("Pending 3D Authenication");

                    if(threeDObject.has("parameters"))
                    {
                        JSONObject parameters =threeDObject.getJSONObject("parameters");
                        String PaReq = parameters.getString("PaReq").replaceAll("\\n", "").replaceAll("\\/", "/");
                        String termUrl = parameters.getString("TermUrl").replace("\\/", "/");

                        commResponseVO.setPaReq(PaReq);
                        commResponseVO.setMd(parameters.getString("MD"));
                        commResponseVO.setTerURL(termUrl);
                    }
                }
                else if(status.equalsIgnoreCase("completed"))
                {
                    transactionLogger.error("in approved-------" + status);
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescription(status);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    transactionLogger.error("in fail-------" + status);
                    commResponseVO.setStatus("fail");
                    if(jsonSaleResponse.has("reason"))
                    {
                        if(functions.isValueNull(jsonSaleResponse.getString("reason")))
                        {
                            commResponseVO.setRemark(jsonSaleResponse.getString("reason"));
                            commResponseVO.setDescription(jsonSaleResponse.getString("reason"));
                        }
                        else
                        {
                            commResponseVO.setRemark("Transaction Failed");
                            commResponseVO.setDescription("Transaction Failed");
                        }
                    }
                    else
                    {
                        commResponseVO.setRemark("Transaction Failed");
                        commResponseVO.setDescription("Transaction Failed");
                    }
                }

                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----for "+trackingID+ "----", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PaySendPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by PaySend gateway. Please contact your Tech. support Team:::",null);
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {

        transactionLogger.error("----- inside processRefund -----");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String publicKey  = gatewayAccount.getMerchantId();
        String privateKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status = "";

        try
        {
            // REFUND
            String id = commTransactionDetailsVO.getPreviousTransactionId();
            String amount = commTransactionDetailsVO.getAmount();

            List refundList = Arrays.asList(publicKey, id, amount);
            String refundSignature = PaySendUtils.getSignature(refundList, privateKey);
            transactionLogger.error("refundSignature ------for "+trackingID+ "----" + refundSignature);

            JSONObject refundRequest = new JSONObject();

            refundRequest.put("project", publicKey);
            refundRequest.put("id", id);
            refundRequest.put("amount", amount);
            refundRequest.put("signature", refundSignature);

/*            String refundRequest = "project="   + publicKey +
                    "&id="                      + id +
                    "&amount="                  + amount +
                    "&signature="               + refundSignature;*/

            transactionLogger.error("refundRequest----for "+trackingID+ "----" + refundRequest);
            String refundResponse = "";

            if(isTest)
            {
                refundResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("REFUND"), refundRequest.toString());
            }
            else
            {
                refundResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("REFUND"), refundRequest.toString());
            }

            transactionLogger.error("refundResponse-----for "+trackingID+ "----" + refundResponse);

            if(functions.isValueNull(refundResponse))
            {
                JSONObject jsonRefundResponse = new JSONObject(refundResponse);

                if(jsonRefundResponse.has("id"))
                {
                    commResponseVO.setTransactionId(jsonRefundResponse.getString("id"));
                }

                if(jsonRefundResponse.has("status"))
                {
                    status = jsonRefundResponse.getString("status");
                }

                if(jsonRefundResponse.has("currency"))
                {
                    commResponseVO.setCurrency(jsonRefundResponse.getString("currency"));
                }


                if(("completed").equalsIgnoreCase(status))
                {
                    transactionLogger.error(" completed -------" + status);
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescription("Transaction Successful");
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Transaction Failed");
                    commResponseVO.setTransactionStatus(status);
                }

            }

            commResponseVO.setAmount(amount);
            commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ---- for "+trackingID+ "----",e);
        }


        return commResponseVO;
    }



    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("----- inside processInquiry -----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String publicKey  = gatewayAccount.getMerchantId();
        String privateKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status = "";
        String transaction_status = "";
        String transaction_type = "";
        try
        {
            // INQUIRY

            transactionLogger.error("Order id ----- "+commTransactionDetailsVO.getOrderId());
            transactionLogger.error("previous transaciton id  ----- "+commTransactionDetailsVO.getPreviousTransactionId());

            String id = commTransactionDetailsVO.getOrderId();

            List inquiryList = Arrays.asList(publicKey, id);
            String inquirySignature = PaySendUtils.getSignature(inquiryList, privateKey);
            transactionLogger.error("inquirySignature ------" + inquirySignature);

/*            JSONObject inquiryRequest = new JSONObject();

            inquiryRequest.put("project", publicKey);
            inquiryRequest.put("order_id", id);
            inquiryRequest.put("signature", inquirySignature);*/

            String inquiryRequest = "?project=" + publicKey +
                    "&order_id=" + id +
                    "&signature=" + inquirySignature;

            transactionLogger.error("inquiryRequest  -----" + inquiryRequest);
            String inquiryResponse = "";

            if(isTest)
            {
                inquiryResponse = PaySendUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY") + inquiryRequest.toString());
            }
            else
            {
                inquiryResponse = PaySendUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY") + inquiryRequest.toString());
            }

            transactionLogger.error("inquiryResponse-----" + inquiryResponse);

            if(functions.isValueNull(inquiryResponse))
            {
                JSONObject jsonInquiryResponse = new JSONObject(inquiryResponse);

                if(jsonInquiryResponse.has("id"))
                {
                    commResponseVO.setTransactionId(jsonInquiryResponse.getString("id"));
                    transactionLogger.error("transaction id = " + commResponseVO.getTransactionId());
                }

                if (jsonInquiryResponse.has("reason"))
                {
                    commResponseVO.setRemark(jsonInquiryResponse.getString("reason"));
                    transactionLogger.error("remark = "+commResponseVO.getRemark());
                }

                if(jsonInquiryResponse.has("price"))
                {
                    commResponseVO.setAmount(jsonInquiryResponse.getString("price"));
                    transactionLogger.error("amount = "+commResponseVO.getAmount());
                }

                if(jsonInquiryResponse.has("currency"))
                {
                    commResponseVO.setCurrency(jsonInquiryResponse.getString("currency"));
                    transactionLogger.error("currency = "+commResponseVO.getCurrency());
                }

                if(jsonInquiryResponse.has("type"))
                {
                    transaction_type = jsonInquiryResponse.getString("type");
                }

                if(jsonInquiryResponse.has("status"))
                {
                    status = jsonInquiryResponse.getString("status");
                }

                if(jsonInquiryResponse.has("originalStatus"))
                {
                    transaction_status = jsonInquiryResponse.getString("originalStatus");
                }

                transactionLogger.error("status = " + status);
                transactionLogger.error("transaction_status = " + transaction_status);
                transactionLogger.error("transaction_type = " + transaction_type);


                if(transaction_status.equalsIgnoreCase("completed"))
                {
                    transactionLogger.error("in success ----");
                    commResponseVO.setStatus("Success");
                    commResponseVO.setDescription("Success");
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else if(transaction_status.equalsIgnoreCase("authenticating"))
                {
                    transactionLogger.error("in processing-----");
                    commResponseVO.setStatus("Pending");
                    commResponseVO.setRemark("Pending 3D Confirmation");
                    commResponseVO.setDescription("Pending 3D Confirmation");
                    commResponseVO.setTransactionStatus(status);
                }
                else if(transaction_status.equalsIgnoreCase("failed"))
                {
                    transactionLogger.error("in fail ----");
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setDescription("Failed");
                    commResponseVO.setTransactionStatus("Failed");
                }

            }

            commResponseVO.setMerchantId(publicKey);
            commResponseVO.setTransactionType(PaySendUtils.getTransactionType(transaction_type));;
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ---- ",e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("----- inside processCommon3DSaleConfirmation -----");
        Comm3DRequestVO comm3DRequestVO=(Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        String PaRes=comm3DRequestVO.getPaRes();
        String MD=comm3DRequestVO.getMd();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String publicKey = gatewayAccount.getMerchantId();
        String privateKey = gatewayAccount.getFRAUD_FTP_PASSWORD();

        try
        {

            List threeDList=Arrays.asList(publicKey,PaRes,MD);
            String threeDSignature = PaySendUtils.getSignature(threeDList, privateKey);
            transactionLogger.error("threeDSignature ------" + threeDSignature);

            JSONObject threeDRequest = new JSONObject();

            threeDRequest.put("project", publicKey);
            threeDRequest.put("PaRes", PaRes);
            threeDRequest.put("MD", MD);
            threeDRequest.put("signature", threeDSignature);

            transactionLogger.error("threeDRequest----for "+trackingID+ "----" + threeDRequest);

            String threeDResponse = "";
            if(isTest)
            {
                threeDResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("AUTHENTICATE"), threeDRequest.toString());
            }
            else
            {
                threeDResponse = PaySendUtils.doPostHTTPSURLConnectionClient(RB.getString("AUTHENTICATE"), threeDRequest.toString());
            }

            transactionLogger.error("threeDResponse-----for "+trackingID+ "----" + threeDResponse);
            String status = "";

            if(functions.isValueNull(threeDResponse))
            {
                JSONObject json3DResponse = new JSONObject(threeDResponse);

                if(json3DResponse.has("error"))
                {
                    JSONObject jsonErrorResponse = new JSONObject(threeDResponse);
                    if(jsonErrorResponse.has("code"))
                    {
                        commResponseVO.setErrorCode(jsonErrorResponse.getString("code"));
                    }

                    if(jsonErrorResponse.has("message"))
                    {
                        commResponseVO.setRemark(jsonErrorResponse.getString("message"));
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(" Error");
                    }
                }

                if(json3DResponse.has("status"))
                {
                    status = json3DResponse.getString("status");
                    if("completed".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setDescription("Transaction Successful");
                    }
                    else if ("processing".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("pending");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setRemark(status);
                }

                if(json3DResponse.has("reason"))
                {
                    commResponseVO.setDescription(json3DResponse.getString("reason"));
                }
                if(json3DResponse.has("id"))
                {
                    commResponseVO.setTransactionId(json3DResponse.getString("id"));
                }
            }


            transactionLogger.error("threeDResponse Status ---- for "+trackingID+ "----" +status);

            commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            commResponseVO.setTmpl_Amount(comm3DRequestVO.getAddressDetailsVO().getTmpl_amount());
            commResponseVO.setTmpl_Currency(comm3DRequestVO.getAddressDetailsVO().getTmpl_currency());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----for "+trackingID+ "----", e);
        }

        return commResponseVO;
    }
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("------- Inside PaySend payout --------");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = ((CommRequestVO) requestVO).getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO) requestVO).getCardDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String publicKey  = gatewayAccount.getMerchantId();
        String privateKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String apiKey = gatewayAccount.getFRAUD_FTP_USERNAME();
        String clientId = gatewayAccount.getFRAUD_FTP_PATH();
        String success="";
        String status="";
        String transactionId="";
        String cvv="";
        if(functions.isValueNull(cardDetailsVO.getcVV()))
            cvv=cardDetailsVO.getcVV();
        else
            cvv="000";
        try
        {
        //CARD
        String cardRequest ="?project=" + publicKey +
                "&number="              + cardDetailsVO.getCardNum() +
                "&expiration_month="    + cardDetailsVO.getExpMonth()+
                "&expiration_year="     + cardDetailsVO.getExpYear() +
                "&security_code="       + cvv;

            String cardRequestLog ="?project=" + publicKey +
                    "&number="              + functions.maskingPan(cardDetailsVO.getCardNum()) +
                    "&expiration_month="    + functions.maskingNumber(cardDetailsVO.getExpMonth())+
                    "&expiration_year="     + functions.maskingNumber(cardDetailsVO.getExpYear()) +
                    "&security_code="       + functions.maskingNumber(cvv);

        transactionLogger.error("cardRequest  -----for "+trackingID+ "----"+ cardRequestLog);
            String cardResponse = "";
            if (isTest)
            {
                cardResponse = PaySendUtils.doGetHTTPSURLConnectionClient(RB.getString("GETTOKEN") + cardRequest.toString());
            }
            else
            {
                cardResponse = PaySendUtils.doGetHTTPSURLConnectionClient(RB.getString("GETTOKEN") + cardRequest.toString());
            }
            transactionLogger.error("cardResponse-----for "+trackingID+ "----" + cardResponse);

            JSONObject jsonCard = new JSONObject(cardResponse);
            String cardToken = "";
            if (jsonCard.has("error"))
            {
                commResponseVO.setStatus("fail");
                if (jsonCard.getJSONObject("error").has("message"))
                {
                    commResponseVO.setRemark(jsonCard.getJSONObject("error").getString("message"));
                    commResponseVO.setDescription(jsonCard.getJSONObject("error").getString("message"));
                }
                if (jsonCard.getJSONObject("error").has("code"))
                    commResponseVO.setErrorCode(jsonCard.getString("code"));

                return commResponseVO;
            }
            if (jsonCard.has("id"))
            {
                cardToken = jsonCard.getString("id");
            }
            String payoutUrl=RB.getString("PAYOUT_URL")+clientId+"/payout";
            String payoutRequest="{" +
                    "  \"amount\": \""+transactionDetailsVO.getAmount()+"\"," +
                    "  \"currency\": \""+transactionDetailsVO.getCurrency()+"\"," +
                    "  \"description\": \""+trackingID+"\"," +
                    "  \"cardToken\": \""+cardToken+"\"," +
                    "  \"beneficiaryFirstName\": \""+addressDetailsVO.getFirstname()+"\"," +
                    "  \"beneficiaryLastName\": \""+addressDetailsVO.getLastname()+"\"" +
                    "}";
            transactionLogger.error("payoutRequest------->for "+trackingID+ "----"+payoutRequest);
            String payoutResponse = PaySendUtils.doPostHTTPSURLConnectionClient(payoutUrl, payoutRequest,apiKey);
            transactionLogger.error("payoutResponse------->for "+trackingID+ "----"+payoutResponse);
            if(functions.isValueNull(payoutResponse))
            {
                JSONObject responseJSON=new JSONObject(payoutResponse);
                if(responseJSON.has("success"))
                    success=responseJSON.getString("success");
                if(responseJSON.has("status"))
                    status=responseJSON.getString("status");
                if(responseJSON.has("id"))
                    transactionId=responseJSON.getString("id");
                if("true".equalsIgnoreCase(success))
                {
                    commResponseVO.setTransactionId(transactionId);
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                }
                else if (responseJSON.has("error"))
                {
                    commResponseVO.setStatus("fail");
                    if (responseJSON.getJSONObject("error").has("message"))
                    {
                        commResponseVO.setRemark(responseJSON.getJSONObject("error").getString("message"));
                        commResponseVO.setDescription(responseJSON.getJSONObject("error").getString("message"));
                    }
                    if (responseJSON.getJSONObject("error").has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException------->for "+trackingID+ "----",e);
        }
        return commResponseVO;
    }

}