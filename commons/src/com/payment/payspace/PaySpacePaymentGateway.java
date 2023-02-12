package com.payment.payspace;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
/**
 * Created by Uday on 7/11/17.
 */
public class PaySpacePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "payspace";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payspace");
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySpacePaymentGateway.class.getName());
    private static Functions functions = new Functions();
    public PaySpacePaymentGateway(String accountId) {this.accountId = accountId;}

    public static void main(String args[])
    {
        /*try
        {
            String card = "4111111111111111";
            String reversedFirstSix = "";
            String card_number = card.substring(0, 6);
            String card_number1 = card.substring(card.length() - 4);
            card_number = card_number + card_number1;
            System.out.println(card_number);
            char[] c = card_number.toCharArray();
            for (int i = c.length - 1; i >= 0; i--)
            {
                String d = String.valueOf(c[i]);
                //System.out.print(d);
                reversedFirstSix = reversedFirstSix + d;
            }
            String email = "<emailaddress>";
            char[] a = email.toCharArray();

            String custMail = "";
            for (int i = a.length - 1; i >= 0; i--)
            {

                String str = String.valueOf(a[i]);
                //System.out.print(str.toUpperCase());
                custMail = custMail + str.toUpperCase();
            }

            System.out.println("reversedFirstSix::::" + reversedFirstSix);
            System.out.println("custMail::::" + custMail);

            String md5String = custMail + "xs4K5YcKA2L45YwzxZZWuxyGvEWdEQy3".toUpperCase() + reversedFirstSix.trim();
            System.out.println("md5String:::" + md5String);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            String generatedCheckSum = getString(messageDigest.digest(md5String.getBytes()));
            System.out.println("generatedCheckSum::::" + generatedCheckSum);

            String saleRequest = "action=SALE" +
                    "&async=N" +
                    "&client_key=RMZSX7T88K" +
                    "&order_id=ORDER-533214521" +
                    "&order_amount=100.00" +
                    "&order_currency=USD" +
                    "&order_description=Productdgerheh" +
                    "&card_number=4111111111111111" +
                    "&card_exp_month=05" +
                    "&card_exp_year=2020" +
                    "&card_cvv2=000" +
                    "&payer_first_name=Uday" +
                    "&payer_last_name=Raj" +
                    "&payer_address=Malad" +
                    "&payer_country=IN" +
                    "&payer_state=MH" +
                    "&payer_city=Mumbai" +
                    "&payer_zip=400064" +
                    "&payer_email=<emailaddress>" +
                    "&payer_phone=+919870850511" +
                    "&payer_ip=45.64.195.218" +
                    "&term_url_3ds=https://staging.<hostname>.com/icici/admin/login.jsp" +
                    "&recurring_init=N" +
                    "&auth=Y" +
                    "&hash=" + generatedCheckSum + "";

            System.out.println("requestData::::" + saleRequest);
            String responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient("https://secure.payinspect.com/post/", saleRequest);
            System.out.println("responseData-->" + responseData);*/

           /* String responseData="{\"action\":\"SALE\",\"result\":\"REDIRECT\",\"status\":\"3DS\",\"order_id\":\"45534\",\"trans_id\":\"20132-71019-52461\",\"trans_date\":\"2017-07-29 11:18:21\",\"redirect_url\":\"https:\\/\\/secure.payment-digital.com\\/emulators\\/3ds\\/3ds_emulator_human.php\"," +
                    "\"redirect_params\":" +
                    "{\"PaReq\":\"VGVzdCBUcmFuc2FjdGlvbiBVREFZIFJBSiBtYWxhZCBtdW1iYWkgSU4=\"," +
                    "\"MD\":\"20132-71019-52461\"," +
                    "\"TermUrl\":\"https:\\/\\/secure.payinspect.com\\/3ds\\/6024b19\"" +
                    "}," +
                    "\"redirect_method\":\"POST\"}";

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PaySpaceResponseVO paySpaceResponseVO = objectMapper.readValue(responseData, PaySpaceResponseVO.class);

            //RedirectParams redirectParams=paySpaceResponseVO.getRedirect_params();

            JSONObject jsonObject=new JSONObject(responseData);

            String redirectData=jsonObject.getString("redirect_params");


            System.out.println("redirectData::::"+redirectData);

            JSONObject jsonObject1=new JSONObject(redirectData);

            String paReq=(String)jsonObject1.get("PaReq");
            String md=(String)jsonObject1.get("MD");
            String term_url=(String)jsonObject1.get("TermUrl");
            System.out.println("paReq::::"+paReq);
            System.out.println("MD::::"+md);
            System.out.println("Term_url::::"+term_url);


            RedirectParams redirectParams = objectMapper.readValue(redirectData, RedirectParams.class);

            System.out.println("pareq::::"+paySpaceResponseVO.getRedirect_method());
            System.out.println("redirectParams.pareq::::"+redirectParams.getPaReq());
            System.out.println("redirectParams.MD::::"+redirectParams.getMD());
            System.out.println("redirectParams.termurl::::"+redirectParams.getTermUrl());
*/          try{
        String s1="20174-14773-82791";
        String s2="45573";
        PaySpacePaymentProcess paySpacePaymentProcess= new PaySpacePaymentProcess();
        String result=paySpacePaymentProcess.updateBankTransactionId(s1,s2);

        //System.out.println("result:::::"+result);


    }
    catch (Exception e)
    {
        //e.printStackTrace();
        //System.out.println(e);
    }

    }

    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        PaySpaceUtils paySpaceUtils= new PaySpaceUtils();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status="";
        String remark="";
        String descriptor = "";
        String responseData = "";
        String hash="";
        try
        {
            hash=paySpaceUtils.getMD5Hash(addressDetailsVO.getEmail(),cardDetailsVO.getCardNum(),password);
            String requestData =
                    "action=SALE" +
                            "&client_key="+merchantId+"" +
                            "&order_id="+transDetailsVO.getOrderId()+"" +
                            "&order_amount="+transDetailsVO.getAmount()+"" +
                            "&order_currency="+transDetailsVO.getCurrency()+"" +
                            "&order_description="+transDetailsVO.getOrderDesc()+"" +
                            "&card_number="+cardDetailsVO.getCardNum()+"" +
                            "&card_exp_month="+cardDetailsVO.getExpMonth()+"" +
                            "&card_exp_year="+cardDetailsVO.getExpYear()+"" +
                            "&card_cvv2="+cardDetailsVO.getcVV()+"" +
                            "&payer_first_name="+addressDetailsVO.getFirstname()+"" +
                            "&payer_last_name="+addressDetailsVO.getLastname()+"" +
                            "&payer_address="+addressDetailsVO.getStreet()+"" +
                            "&payer_country="+addressDetailsVO.getCountry()+"" +
                            "&payer_state="+addressDetailsVO.getState()+"" +
                            "&payer_city="+addressDetailsVO.getCity()+"" +
                            "&payer_zip="+addressDetailsVO.getZipCode()+"" +
                            "&payer_email="+addressDetailsVO.getEmail()+"" +
                            "&payer_phone="+addressDetailsVO.getPhone()+"" +
                            "&payer_ip="+addressDetailsVO.getIp()+"" +
                            "&term_url_3ds="+RB.getString("TERM_URL_3DS")+"?order_id="+trackingID+""+
                            "&recurring_init=N" +
                            "&auth=N" +
                            "&hash="+hash+"";

            transactionLogger.error("-----sale request-----" + requestData);
            if(isTest){
                responseData = paySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }else{
                responseData = paySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            transactionLogger.error("----- sale response-----" + responseData);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PaySpaceResponseVO paySpaceResponseVO = objectMapper.readValue(responseData, PaySpaceResponseVO.class);

            if ("SUCCESS".equals(paySpaceResponseVO.getResult())){
                status = "success";
                remark="Transaction Successful";
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("ACCEPTED".equals(paySpaceResponseVO.getResult())){
                status = "pending";
                remark="Transaction Accepted By Acquirer(Processing)";

                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("REDIRECT".equals(paySpaceResponseVO.getResult())){
                status = "pending3DConfirmation";
                remark="Transaction Accepted(3d check pending)";

                JSONObject jsonObject=new JSONObject(responseData);
                String redirectData=jsonObject.getString("redirect_params");
                JSONObject jsonObject1=new JSONObject(redirectData);

                String paReq=(String)jsonObject1.get("PaReq");
                String md=(String)jsonObject1.get("MD");
                String term_url=(String)jsonObject1.get("TermUrl");

                commResponseVO.setRedirectMethod(paySpaceResponseVO.getRedirect_method());
                commResponseVO.setPaReq(paReq);
                commResponseVO.setMd(md);
                commResponseVO.setTerURL(term_url);
                commResponseVO.setStatus(status);
                commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(paySpaceResponseVO.getRedirect_url()));
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
                PaySpacePaymentProcess paySpacePaymentProcess= new PaySpacePaymentProcess();
                paySpacePaymentProcess.updateBankTransactionId(paySpaceResponseVO.getTrans_id(),paySpaceResponseVO.getOrder_id());
            }
            else if ("DECLINED".equals(paySpaceResponseVO.getResult()) || "ERROR".equals(paySpaceResponseVO.getResult())){
                status = "fail";
                remark=paySpaceResponseVO.getDecline_reason();
            }
            commResponseVO.setMerchantOrderId(paySpaceResponseVO.getOrder_id());
            commResponseVO.setTransactionId(paySpaceResponseVO.getTrans_id());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(paySpaceResponseVO.getTrans_date());
        }
        catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        PaySpaceUtils paySpaceUtils= new PaySpaceUtils();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status="";
        String remark="";
        String descriptor = "";
        String responseData = "";
        String hash="";
        try
        {
            hash=paySpaceUtils.getMD5Hash(addressDetailsVO.getEmail(),cardDetailsVO.getCardNum(),password);
            String requestData =
                    "action=SALE" +
                            "&client_key="+merchantId+"" +
                            "&order_id="+transDetailsVO.getOrderId()+"" +
                            "&order_amount="+transDetailsVO.getAmount()+"" +
                            "&order_currency="+transDetailsVO.getCurrency()+"" +
                            "&order_description="+transDetailsVO.getOrderDesc()+"" +
                            "&card_number="+cardDetailsVO.getCardNum()+"" +
                            "&card_exp_month="+cardDetailsVO.getExpMonth()+"" +
                            "&card_exp_year="+cardDetailsVO.getExpYear()+"" +
                            "&card_cvv2="+cardDetailsVO.getcVV()+"" +
                            "&payer_first_name="+addressDetailsVO.getFirstname()+"" +
                            "&payer_last_name="+addressDetailsVO.getLastname()+"" +
                            "&payer_address="+addressDetailsVO.getStreet()+"" +
                            "&payer_country="+addressDetailsVO.getCountry()+"" +
                            "&payer_state="+addressDetailsVO.getState()+"" +
                            "&payer_city="+addressDetailsVO.getCity()+"" +
                            "&payer_zip="+addressDetailsVO.getZipCode()+"" +
                            "&payer_email="+addressDetailsVO.getEmail()+"" +
                            "&payer_phone="+addressDetailsVO.getPhone()+"" +
                            "&payer_ip="+addressDetailsVO.getIp()+"" +
                            "&term_url_3ds="+RB.getString("TERM_URL_3DS")+"?order_id="+trackingID+""+
                            "&recurring_init=N" +
                            "&auth=Y" +
                            "&hash="+hash+"";

            transactionLogger.error("-----auth request-----" + requestData);
            if(isTest){
                responseData = paySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }else{
                responseData = paySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            transactionLogger.error("-----auth response-----" + responseData);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PaySpaceResponseVO paySpaceResponseVO = objectMapper.readValue(responseData, PaySpaceResponseVO.class);

            if ("SUCCESS".equals(paySpaceResponseVO.getResult())){
                status = "success";
                remark="Transaction Successful";
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("ACCEPTED".equals(paySpaceResponseVO.getResult())){
                status = "pending";
                remark="Transaction Accepted By Acquirer(Processing)";
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("REDIRECT".equals(paySpaceResponseVO.getResult())){
                status = "pending3DConfirmation";
                remark="Transaction Accepted(3d check pending)";
                JSONObject jsonObject=new JSONObject(responseData);
                String redirectData=jsonObject.getString("redirect_params");
                JSONObject jsonObject1=new JSONObject(redirectData);

                String paReq=(String)jsonObject1.get("PaReq");
                String md=(String)jsonObject1.get("MD");
                String term_url=(String)jsonObject1.get("TermUrl");

                commResponseVO.setRedirectMethod(paySpaceResponseVO.getRedirect_method());
                commResponseVO.setPaReq(paReq);
                commResponseVO.setMd(md);
                commResponseVO.setTerURL(term_url);
                commResponseVO.setStatus(status);
                commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(paySpaceResponseVO.getRedirect_url()));
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
                PaySpacePaymentProcess paySpacePaymentProcess= new PaySpacePaymentProcess();
                paySpacePaymentProcess.updateBankTransactionId(paySpaceResponseVO.getTrans_id(),paySpaceResponseVO.getOrder_id());

            }
            else if ("DECLINED".equals(paySpaceResponseVO.getResult()) || "ERROR".equals(paySpaceResponseVO.getResult())){
                status = "fail";
                remark=paySpaceResponseVO.getDecline_reason();
            }

            commResponseVO.setMerchantOrderId(paySpaceResponseVO.getOrder_id());
            commResponseVO.setTransactionId(paySpaceResponseVO.getTrans_id());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(paySpaceResponseVO.getTrans_date());
        }
        catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        PaySpaceResponseVO paySpaceResponseVO=null;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        PaySpaceUtils paySpaceUtils= new PaySpaceUtils();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status = "";
        String descriptor = "";
        String responseData = "";
        String remark="";
        String hash="";

        try{
            hash=paySpaceUtils.getMD5HashForOp(addressDetailsVO.getEmail(),transactionDetailsVO.getPreviousTransactionId(),cardDetailsVO.getCardNum(),password);
            String requestData ="action=CAPTURE"+
                    "&client_key="+merchantId+""+
                    "&trans_id="+transactionDetailsVO.getPreviousTransactionId()+""+
                    "&amount="+transactionDetailsVO.getAmount()+""+
                    "&hash="+hash+"";

            transactionLogger.error("-----capture request-----" + requestData);
            if(isTest){
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }else{
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            transactionLogger.error("-----capture response-----" + responseData);

            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            paySpaceResponseVO = objectMapper.readValue(responseData, PaySpaceResponseVO.class);

            if ("SUCCESS".equals(paySpaceResponseVO.getResult())){
                status = "success";
                remark="Capture Successful";
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("DECLINED".equals(paySpaceResponseVO.getResult())){
                status = "fail";
                remark="Capture Failed";
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("ERROR".equals(paySpaceResponseVO.getResult())){
                status = "pending";
                remark=paySpaceResponseVO.getDecline_reason();
            }
            if(!functions.isValueNull(paySpaceResponseVO.getTrans_id())) {
                paySpaceResponseVO.setTrans_id(transactionDetailsVO.getPreviousTransactionId());
            }
            commResponseVO.setMerchantOrderId(paySpaceResponseVO.getOrder_id());
            commResponseVO.setTransactionId(paySpaceResponseVO.getTrans_id());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(paySpaceResponseVO.getTrans_date());
        }
        catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        PaySpaceResponseVO paySpaceResponseVO= null;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        PaySpaceUtils paySpaceUtils= new PaySpaceUtils();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status="";
        String remark="";
        String descriptor = "";
        String responseData = "";
        String hash="";

        try
        {
            hash=paySpaceUtils.getMD5HashForOp(addressDetailsVO.getEmail(), commTransactionDetailsVO.getPreviousTransactionId(),cardDetailsVO.getCardNum(),password);
            String requestData ="action=CREDITVOID"+
                    "&client_key="+merchantId+""+
                    "&trans_id="+commTransactionDetailsVO.getPreviousTransactionId()+""+
                    "&amount="+commTransactionDetailsVO.getAmount()+""+
                    "&hash="+hash+"";

            transactionLogger.error("-----refund request-----" + requestData);
            if(isTest){
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }else{
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            transactionLogger.error("-----refund response-----" + responseData);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            paySpaceResponseVO = objectMapper.readValue(responseData, PaySpaceResponseVO.class);
            if ("ACCEPTED".equals(paySpaceResponseVO.getResult())){
                status = "success";
                remark="Refund Successful";
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("DECLINED".equals(paySpaceResponseVO.getResult()) || "ERROR".equals(paySpaceResponseVO.getResult())){
                status = "fail";
                remark=paySpaceResponseVO.getDecline_reason();
            }
            commResponseVO.setMerchantOrderId(paySpaceResponseVO.getOrder_id());
            commResponseVO.setTransactionId(paySpaceResponseVO.getTrans_id());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(paySpaceResponseVO.getTrans_date());
        }
        catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        PaySpaceResponseVO paySpaceResponseVO= null;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        PaySpaceUtils paySpaceUtils= new PaySpaceUtils();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status="";
        String remark="";
        String descriptor = "";
        String responseData = "";
        String hash="";

        try
        {
            hash=paySpaceUtils.getMD5HashForOp(addressDetailsVO.getEmail(), commTransactionDetailsVO.getPreviousTransactionId(),cardDetailsVO.getCardNum(),password);

            String requestData ="action=CREDITVOID"+
                    "&client_key="+merchantId+""+
                    "&trans_id="+commTransactionDetailsVO.getPreviousTransactionId()+""+
                    "&amount="+commTransactionDetailsVO.getAmount()+""+
                    "&hash="+hash+"";

            transactionLogger.error("-----cancel request-----" + requestData);
            if(isTest){
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }else{
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            transactionLogger.error("-----cancel response-----" + responseData);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            paySpaceResponseVO = objectMapper.readValue(responseData, PaySpaceResponseVO.class);
            if ("ACCEPTED".equals(paySpaceResponseVO.getResult())){
                status = "success";
                remark="Refund Transaction Successful";
                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("DECLINED".equals(paySpaceResponseVO.getResult()) || "ERROR".equals(paySpaceResponseVO.getResult())){
                status = "fail";
                remark=paySpaceResponseVO.getDecline_reason();
            }
            commResponseVO.setMerchantOrderId(paySpaceResponseVO.getOrder_id());
            commResponseVO.setTransactionId(paySpaceResponseVO.getTrans_id());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(paySpaceResponseVO.getTrans_date());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        PaySpaceResponseVO paySpaceResponseVO= null;
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Transactions transactions= new Transactions();
        PaySpaceUtils paySpaceUtils= new PaySpaceUtils();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String status="";
        String remark="";
        String descriptor = "";
        String responseData = "";
        String hash="";
        String dateTime="";
        try{
            hash=paySpaceUtils.getMD5HashForOp(addressDetailsVO.getEmail(), commTransactionDetailsVO.getPreviousTransactionId(),cardDetailsVO.getCardNum(),password);
            String requestData ="action=GET_TRANS_DETAILS"+
                    "&client_key="+merchantId+""+
                    "&trans_id="+commTransactionDetailsVO.getPreviousTransactionId()+""+
                    "&hash="+hash+"";

            transactionLogger.error("----inquiry request-----" + requestData);
            if(isTest){
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }else{
                responseData = PaySpaceUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            transactionLogger.error("-----inquiry response-----" + responseData);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            paySpaceResponseVO = objectMapper.readValue(responseData, PaySpaceResponseVO.class);

            if ("SUCCESS".equals(paySpaceResponseVO.getResult())){
                status = "success";
                remark="Operation Successful";

                JSONObject jsonObject=new JSONObject(responseData);
                JSONArray transactions1=jsonObject.getJSONArray("transactions");

                for (int i = 0; i < transactions1.length(); ++i) {
                    JSONObject jsonObject1 = transactions1.getJSONObject(i);
                    dateTime = jsonObject1.getString("date");
                    String type = jsonObject1.getString("type");
                    String status1 = jsonObject1.getString("status");
                    String amount = jsonObject1.getString("amount");
                    transactions.setDate(dateTime);
                    transactions.setType(type);
                    transactions.setStatus(status1);
                    transactions.setAmount(amount);
                }

                if (functions.isValueNull(paySpaceResponseVO.getDescriptor())){
                    descriptor = paySpaceResponseVO.getDescriptor();
                }
                else{
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("DECLINED".equals(paySpaceResponseVO.getResult()) || "ERROR".equals(paySpaceResponseVO.getResult())){
                status = "fail";
                remark=paySpaceResponseVO.getDecline_reason();
            }

            commResponseVO.setMerchantOrderId(paySpaceResponseVO.getOrder_id());
            commResponseVO.setTransactionId(paySpaceResponseVO.getTrans_id());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(paySpaceResponseVO.getTrans_date());
            commResponseVO.setMerchantId(merchantId);
            commResponseVO.setBankTransactionDate(dateTime);
            commResponseVO.setTransactionType(transactions.getType());
            commResponseVO.setAmount(paySpaceResponseVO.getAmount());
            commResponseVO.setCurrency(paySpaceResponseVO.getCurrency());
            commResponseVO.setTransactionStatus(paySpaceResponseVO.getResult());
        }
        catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PaySpacePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

}
