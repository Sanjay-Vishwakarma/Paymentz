package com.payment.romcard;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.ResourceBundle;
import org.apache.commons.codec.binary.Hex;

/**
 * Created by Rihen on 12/12/2018.
 */
public class RomCardPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(RomCardPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.romcard");

    public static final String GATEWAY_TYPE = "romcard";

    public RomCardPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays() {   return null;   }


    public static void main(String[] args)
    {

     /*   try{
            String url = "https://www.activare3dsecure.ro/teste3d/cgi-bin/";
            String encryptionKey="2B026FF3B2FB0B532D935165ACD54A6F";

            String trackingid = "3214987";

            String amount="1.00";
            String currency="RON";
            String order=trackingid;
            String desc="Test order";
            String merch_name="ALTEX ROMANIA SRL";
            String merch_url="https://www.altex.ro";
            String terminal="10627001";
            String merchant="0000000"+terminal;
            String email="alexandru.termegan@altex.ro";
            String trtype="0";
            String country="";
            String merch_gmt="";
            String timpstamp =  RomCardUtils.currentTimeToGMT();
            String nonce= Functions.convertmd5(new Long (new Date().getTime()).toString());
            String backref="http://localhost:8081/transaction/Common3DFrontEndServlet";


            String hmac_request =amount.length()+amount
                    +currency.length()+currency
                    +order.length()+order
                    +desc.length()+desc
                    +merch_name.length()+merch_name
                    +merch_url.length()+merch_url
                    +merchant.length()+merchant
                    +terminal.length()+terminal
                    +email.length()+email
                    +trtype.length()+trtype+"--"
                    +timpstamp.length()+timpstamp
                    +nonce.length()+nonce
                    +backref.length()+backref;

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            String p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String request = "" +
                    "AMOUNT="+amount+
                    "&CURRENCY="+currency+
                    "&ORDER="+order+
                    "&DESC="+desc+
                    "&MERCH_NAME="+merch_name+
                    "&MERCH_URL="+merch_url+
                    "&MERCHANT="+merchant+
                    "&TERMINAL="+terminal+
                    "&EMAIL="+email+
                    "&TRTYPE="+trtype+
                    "&COUNTRY="+country+
                    "&MERCH_GMT="+merch_gmt+
                    "&TIMESTAMP="+timpstamp+
                    "&NONCE="+nonce+
                    "&BACKREF="+backref+
                    "&P_SIGN="+p_sign;

            RomCardUtils.makeHmacRequest(request);

            System.out.println("request-------"+request);

           // String response = RomCardUtils.doPostHTTPSURLConnectionClient(url,request);

           // System.out.println("response ----------"+response);

        }
        catch (Exception e)
        {
            System.out.println(" Exception ---------- " +e);
        }*/



        // CAPTURE
        /*String encryptionKey="2B026FF3B2FB0B532D935165ACD54A6F";
        String order = "00061852";
        String amount = "1.00";
        String currency = "USD";
        String rrn = "400630392116";
        String int_ref = "999E8049AC594B91";
        String trtype = "21";
        String terminal= "10627001";
        String timpstamp =  RomCardUtils.currentTimeToGMT();
        String nonce = null;
        try
        {
            nonce = Functions.convertmd5(new Long(new Date().getTime()).toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in RomCardGateway---", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException in RomCardGateway---", e);
        }
        String backref = RB.getString("RETURN_URL");
        String p_sign = "";

        try
        {
            String hmac_request = order.length()     +order+
                    amount.length()    +amount+
                    currency.length()  +currency+
                    rrn.length()       +rrn+
                    int_ref.length()   +int_ref+
                    trtype.length()    +trtype+
                    terminal.length()  +terminal+
                    timpstamp.length() +timpstamp+
                    nonce.length()     +nonce+
                    backref.length()   +backref;

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String captureRequest =
                            "ORDER="     + order +
                            "&AMOUNT="     + amount    +
                            "&CURRENCY="  + currency  +
                            "&RRN="       + rrn       +
                            "&INT_REF="   + int_ref   +
                            "&TRTYPE="    + trtype    +
                            "&TERMINAL="  + terminal  +
                            "&TIMESTAMP=" + timpstamp +
                            "&NONCE="     + nonce     +
                            "&BACKREF="   + backref   +
                            "&P_SIGN="    + p_sign;

            System.out.println("captureRequest-------" + captureRequest);

            String captureResponse = "";
            captureResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , captureRequest);


            System.out.println("captureResponse ----------"+captureResponse);
        }
        catch (Exception e)
        {
            System.out.println(" Exception ---------- " +e);
        }*/



        //REFUND
        String encryptionKey="12D4C80FE6D844A437CD0BD0F1145C80";
        String order = "00095112";
        String amount = "1.00";
        String currency = "RON";
        String rrn = "905385962721";
        String int_ref = "A21F54ACD84DF34E";
        String trtype = "24";
        String terminal= "10627002";
        String timpstamp =  RomCardUtils.currentTimeToGMT();
        String nonce = null;
        try
        {
            nonce = Functions.convertmd5(new Long(new Date().getTime()).toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in RomCardGateway---", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException in RomCardGateway---", e);
        }
        String backref = RB.getString("RETURN_URL");
        String p_sign = "";

        try
        {
            String hmac_request = order.length()     +order+
                    amount.length()    +amount+
                    currency.length()  +currency+
                    rrn.length()       +rrn+
                    int_ref.length()   +int_ref+
                    trtype.length()    +trtype+
                    terminal.length()  +terminal+
                    timpstamp.length() +timpstamp+
                    nonce.length()     +nonce+
                    backref.length()   +backref;

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String captureRequest =
                    "ORDER="     + order +
                            "&AMOUNT="     + amount    +
                            "&CURRENCY="  + currency  +
                            "&RRN="       + rrn       +
                            "&INT_REF="   + int_ref   +
                            "&TRTYPE="    + trtype    +
                            "&TERMINAL="  + terminal  +
                            "&TIMESTAMP=" + timpstamp +
                            "&NONCE="     + nonce     +
                            "&BACKREF="   + backref   +
                            "&P_SIGN="    + p_sign;

            System.out.println("captureRequest-------" + captureRequest);

            String captureResponse = "";
            captureResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , captureRequest);


            System.out.println("captureResponse ----------"+captureResponse);
        }
        catch (Exception e)
        {
            System.out.println(" Exception ---------- " +e);
        }



    }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String orderDesc = commTransactionDetailsVO.getOrderId();
        if (orderDesc.equals(trackingID))
        {
            orderDesc = commTransactionDetailsVO.getMerchantOrderId();
        }
        if (!functions.isValueNull(orderDesc))
        {
            orderDesc = commTransactionDetailsVO.getOrderDesc();
        }


        String encryptionKey = gatewayAccount.getFRAUD_FTP_PATH();

        if(trackingID.length()<6)
        {
            trackingID = "000"+trackingID;
        }

        String amount = commTransactionDetailsVO.getAmount().trim();
        String currency = commTransactionDetailsVO.getCurrency().trim();
        String order = trackingID;
        String desc = orderDesc;
        String merch_name = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merch_url = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String terminal= gatewayAccount.getMerchantId();
        String merchant="0000000"+terminal;
        String email = commAddressDetailsVO.getEmail();
        String installment ="";
        if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
        {
            transactionLogger.error("Installment value not null= " + commTransactionDetailsVO.getEmiCount());
            installment = commTransactionDetailsVO.getEmiCount();
        }
        String trtype = "0";
        String country="";
        String merch_gmt="";
        String timpstamp =  RomCardUtils.currentTimeToGMT();
        String nonce = null;
        try
        {
            nonce = RomCardUtils.convertmd5(new Long(new Date().getTime()).toString()).trim();
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in RomCardGateway---", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException in RomCardGateway---", e);
        }
        String backref = RB.getString("RETURN_URL");
        String p_sign = "";

        try
        {
            String hmac_request =amount.length()+amount
                    +currency.length()+currency
                    +order.length()+order
                    +desc.length()+desc
                    +merch_name.length()+merch_name
                    +merch_url.length()+merch_url
                    +merchant.length()+merchant
                    +terminal.length()+terminal
                    +email.length()+email
                    +trtype.length()+trtype+"--"
                    +timpstamp.length()+timpstamp
                    +nonce.length()+nonce
                    +backref.length()+backref;

            String addInstallment ="";
            if(functions.isValueNull(installment)){
                hmac_request +=+installment.length() + installment;
                addInstallment = "&RAMBURSARE=" + installment;
            }

            transactionLogger.error(" ------- hmac request ----- " + hmac_request);

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String saleRequest = "AMOUNT="+ amount +
                    "&CURRENCY="   + currency  +
                    "&ORDER="      + order +
                    "&DESC="       + desc  +
                    "&MERCH_NAME=" + merch_name +
                    "&MERCH_URL="  + merch_url +
                    "&MERCHANT="   + merchant +
                    "&TERMINAL="   + terminal +
                    "&EMAIL="      + email +
                    "&TRTYPE="     + trtype +
                    "&COUNTRY="    +
                    "&MERCH_GMT="  +
                    "&TIMESTAMP="  + timpstamp +
                    "&NONCE="      + nonce +
                    "&BACKREF="    + backref +
                        addInstallment +
                    "&P_SIGN="     + p_sign;

            transactionLogger.error("saleRequest-------" + saleRequest);

            String saleresponse = "";
            if (isTest)
            {
                transactionLogger.error(" test url ----------" + RB.getString("TEST_URL"));
                saleresponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , saleRequest);
            }
            else
            {
                transactionLogger.error("live url ----------" + RB.getString("LIVE_URL"));
                saleresponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , saleRequest);
            }


            transactionLogger.error("saleresponse ----------" + saleresponse);

            if(functions.isValueNull(saleresponse))
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setUrlFor3DRedirect(saleresponse);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException ,PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String orderDesc = commTransactionDetailsVO.getOrderId();
        if (orderDesc.equals(trackingID))
        {
            orderDesc = commTransactionDetailsVO.getMerchantOrderId();
        }
        if (!functions.isValueNull(orderDesc))
        {
            orderDesc = commTransactionDetailsVO.getOrderDesc();
        }


        String encryptionKey = gatewayAccount.getFRAUD_FTP_PATH();

        if(trackingID.length()<6)
        {
            trackingID = "000"+trackingID;
        }

        String amount = commTransactionDetailsVO.getAmount().trim();
        String currency = commTransactionDetailsVO.getCurrency().trim();
        String order = trackingID;
        String desc = orderDesc;
        String merch_name = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merch_url = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String terminal= gatewayAccount.getMerchantId();
        String merchant="0000000"+terminal;
        String email = commAddressDetailsVO.getEmail();
        String installment = "";
        if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
        {
            transactionLogger.error("Installment value not null= " + commTransactionDetailsVO.getEmiCount());
            installment = commTransactionDetailsVO.getEmiCount();
        }
        String trtype = "0";
        String country="";
        String merch_gmt="";
        String timpstamp =  RomCardUtils.currentTimeToGMT();
        String nonce = null;
        try
        {
            nonce = RomCardUtils.convertmd5(new Long(new Date().getTime()).toString()).trim();
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in RomCardGateway---", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException in RomCardGateway---", e);
        }
        String backref = RB.getString("RETURN_URL");
        String p_sign = "";

        try
        {
            String hmac_request =amount.length()+amount
                    +currency.length()+currency
                    +order.length()+order
                    +desc.length()+desc
                    +merch_name.length()+merch_name
                    +merch_url.length()+merch_url
                    +merchant.length()+merchant
                    +terminal.length()+terminal
                    +email.length()+email
                    +trtype.length()+trtype+"--"
                    +timpstamp.length()+timpstamp
                    +nonce.length()+nonce
                    +backref.length()+backref;

            String addInstallment ="";
            if(functions.isValueNull(installment)){
                hmac_request +=+installment.length() + installment;
                addInstallment = "&RAMBURSARE=" + installment;
            }

            transactionLogger.error(" ------- hmac request ----- " + hmac_request);

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String authRequest = "AMOUNT="+ amount +
                    "&CURRENCY="   + currency  +
                    "&ORDER="      + order +
                    "&DESC="       + desc  +
                    "&MERCH_NAME=" + merch_name +
                    "&MERCH_URL="  + merch_url +
                    "&MERCHANT="   + merchant +
                    "&TERMINAL="   + terminal +
                    "&EMAIL="      + email +
                    "&TRTYPE="     + trtype +
                    "&COUNTRY="    +
                    "&MERCH_GMT="  +
                    "&TIMESTAMP="  + timpstamp +
                    "&NONCE="      + nonce +
                    "&BACKREF="    + backref +
                        addInstallment +
                    "&P_SIGN="     + p_sign;

            transactionLogger.error("authRequest-------" + authRequest);

            String authresponse = "";
            if (isTest)
            {
                transactionLogger.error(" test url ----------" + RB.getString("TEST_URL"));
                authresponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , authRequest);
            }
            else
            {
                transactionLogger.error("live url ----------" + RB.getString("LIVE_URL"));
                authresponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , authRequest);
            }
            transactionLogger.error("authresponse ----------" + authresponse);

            if(functions.isValueNull(authresponse))
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setUrlFor3DRedirect(authresponse);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String encryptionKey = gatewayAccount.getFRAUD_FTP_PATH();

        if(trackingID.length()<6)
        {
            trackingID = "000"+trackingID;
        }

        String order = trackingID;
        String amount = commTransactionDetailsVO.getAmount();
        String currency = commTransactionDetailsVO.getCurrency();
        String rrn = commTransactionDetailsVO.getResponseHashInfo();
        String int_ref = commTransactionDetailsVO.getPreviousTransactionId();
        String trtype = "21";
        String terminal= gatewayAccount.getMerchantId();
        String timpstamp =  RomCardUtils.currentTimeToGMT();
        String nonce = null;

        transactionLogger.error("order Id ------" + order);
        transactionLogger.error("amount------" + amount);
        transactionLogger.error("currency ------" + currency);
        transactionLogger.error("rrn ------" + rrn);
        transactionLogger.error("int_ref ------" + int_ref);

        try
        {
            nonce = Functions.convertmd5(new Long(new Date().getTime()).toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in RomCardGateway---", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException in RomCardGateway---", e);
        }
        String backref = RB.getString("RETURN_URL");
        String p_sign = "";

        try
        {
            String hmac_request =
                    order.length()     +order+
                            amount.length()    +amount+
                            currency.length()  +currency+
                            rrn.length()       +rrn+
                            int_ref.length()   +int_ref+
                            trtype.length()    +trtype+
                            terminal.length()  +terminal+
                            timpstamp.length() +timpstamp+
                            nonce.length()     +nonce+
                            backref.length()   +backref;

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String captureRequest =
                    "ORDER="     + order     +
                            "&AMOUNT="     + amount    +
                            "&CURRENCY="  + currency  +
                            "&RRN="       + rrn       +
                            "&INT_REF="   + int_ref   +
                            "&TRTYPE="    + trtype    +
                            "&TERMINAL="  + terminal  +
                            "&TIMESTAMP=" + timpstamp +
                            "&NONCE="     + nonce     +
                            "&BACKREF="   + backref   +
                            "&P_SIGN="    + p_sign;

            transactionLogger.error("captureRequest-------" + captureRequest);

            String captureResponse = "";
            if (isTest)
            {
                captureResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , captureRequest);
            }
            else
            {
                captureResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , captureRequest);
            }

            transactionLogger.error("captureResponse ----------" + captureResponse);

            Document html = Jsoup.parse(captureResponse);
            String resp_action = html.body().getElementsByAttributeValue("name","ACTION").val();
            String resp_message = html.body().getElementsByAttributeValue("name","MESSAGE").val();
            String resp_amount = html.body().getElementsByAttributeValue("name","AMOUNT").val();
            String resp_currency = html.body().getElementsByAttributeValue("name","CURRENCY").val();
            String resp_order = html.body().getElementsByAttributeValue("name","ORDER").val();
            String resp_rrn = html.body().getElementsByAttributeValue("name","RRN").val();
            String resp_int_ref = html.body().getElementsByAttributeValue("name","INT_REF").val();


            if(!functions.isValueNull(resp_action))
                resp_action = html.body().getElementsByAttributeValue("name","b").val();
            if(!functions.isValueNull(resp_message))
                resp_message = html.body().getElementsByAttributeValue("name","d").val();
            if(!functions.isValueNull(resp_amount))
                resp_amount = html.body().getElementsByAttributeValue("name","f").val();
            if(!functions.isValueNull(resp_currency))
                resp_currency = html.body().getElementsByAttributeValue("name","g").val();
            if(!functions.isValueNull(resp_order))
                resp_order = html.body().getElementsByAttributeValue("name","h").val();
            if(!functions.isValueNull(resp_rrn))
                resp_rrn = html.body().getElementsByAttributeValue("name","i").val();
            if(!functions.isValueNull(resp_int_ref))
                resp_int_ref = html.body().getElementsByAttributeValue("name","j").val();

            transactionLogger.error("--- action --- " + resp_action);
            transactionLogger.error("--- message --- " + resp_message);
            transactionLogger.error("--- amount --- " + resp_amount);
            transactionLogger.error("--- currency --- " + resp_currency);
            transactionLogger.error("--- order --- " + resp_order);
            transactionLogger.error("--- rrn --- " + resp_rrn);
            transactionLogger.error("--- int_ref --- " + resp_int_ref);

            if(resp_message.equalsIgnoreCase("Approved") && "0".equals(resp_action))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            else
            {
                commResponseVO.setStatus("failed");
            }

            commResponseVO.setCurrency(resp_currency);
            commResponseVO.setAmount(resp_amount);
            commResponseVO.setResponseHashInfo(resp_rrn);
            commResponseVO.setTransactionId(resp_int_ref);
            commResponseVO.setDescription(resp_message);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();

        String encryptionKey = gatewayAccount.getFRAUD_FTP_PATH();

        if(trackingID.length()<6)
        {
            trackingID = "000"+trackingID;
        }

        String order = trackingID;
        String amount = commTransactionDetailsVO.getAmount();
        String currency = commTransactionDetailsVO.getCurrency();
        String rrn = commTransactionDetailsVO.getResponseHashInfo();
        String int_ref = commTransactionDetailsVO.getPreviousTransactionId();
        String trtype = "24";
        String terminal= gatewayAccount.getMerchantId();
        String timpstamp =  RomCardUtils.currentTimeToGMT();
        String nonce = null;

        transactionLogger.error("order Id ------" + order);
        transactionLogger.error("amount------" + amount);
        transactionLogger.error("currency ------" + currency);
        transactionLogger.error("rrn ------" + rrn);
        transactionLogger.error("int_ref ------" + int_ref);

        try
        {
            nonce = Functions.convertmd5(new Long(new Date().getTime()).toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in RomCardGateway---", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException in RomCardGateway---", e);
        }
        String backref = RB.getString("RETURN_URL");
        String p_sign = "";

        try
        {
            String hmac_request =
                    order.length()     +order+
                            amount.length()    +amount+
                            currency.length()  +currency+
                            rrn.length()       +rrn+
                            int_ref.length()   +int_ref+
                            trtype.length()    +trtype+
                            terminal.length()  +terminal+
                            timpstamp.length() +timpstamp+
                            nonce.length()     +nonce+
                            backref.length()   +backref;

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String voidRequest =
                    "ORDER="     + order     +
                            "&AMOUNT="     + amount    +
                            "&CURRENCY="  + currency  +
                            "&RRN="       + rrn       +
                            "&INT_REF="   + int_ref   +
                            "&TRTYPE="    + trtype    +
                            "&TERMINAL="  + terminal  +
                            "&TIMESTAMP=" + timpstamp +
                            "&NONCE="     + nonce     +
                            "&BACKREF="   + backref   +
                            "&P_SIGN="    + p_sign;

            transactionLogger.error("voidRequest-------" + voidRequest);

            String voidResponse = "";
            if (isTest)
            {
                voidResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , voidRequest);
            }
            else
            {
                voidResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , voidRequest);
            }

            transactionLogger.error("voidResponse ----------" + voidResponse);

            Document html = Jsoup.parse(voidResponse);
            String resp_action = html.body().getElementsByAttributeValue("name","ACTION").val();
            String resp_message = html.body().getElementsByAttributeValue("name","MESSAGE").val();
            String resp_amount = html.body().getElementsByAttributeValue("name","AMOUNT").val();
            String resp_currency = html.body().getElementsByAttributeValue("name","CURRENCY").val();
            String resp_order = html.body().getElementsByAttributeValue("name","ORDER").val();
            String resp_rrn = html.body().getElementsByAttributeValue("name","RRN").val();
            String resp_int_ref = html.body().getElementsByAttributeValue("name","INT_REF").val();


            if(!functions.isValueNull(resp_action))
                resp_action = html.body().getElementsByAttributeValue("name","b").val();
            if(!functions.isValueNull(resp_message))
                resp_message = html.body().getElementsByAttributeValue("name","d").val();
            if(!functions.isValueNull(resp_amount))
                resp_amount = html.body().getElementsByAttributeValue("name","f").val();
            if(!functions.isValueNull(resp_currency))
                resp_currency = html.body().getElementsByAttributeValue("name","g").val();
            if(!functions.isValueNull(resp_order))
                resp_order = html.body().getElementsByAttributeValue("name","h").val();
            if(!functions.isValueNull(resp_rrn))
                resp_rrn = html.body().getElementsByAttributeValue("name","i").val();
            if(!functions.isValueNull(resp_int_ref))
                resp_int_ref = html.body().getElementsByAttributeValue("name","j").val();

            transactionLogger.error("--- action --- " + resp_action);
            transactionLogger.error("--- message --- " + resp_message);
            transactionLogger.error("--- amount --- " + resp_amount);
            transactionLogger.error("--- currency --- " + resp_currency);
            transactionLogger.error("--- order --- " + resp_order);
            transactionLogger.error("--- rrn --- " + resp_rrn);
            transactionLogger.error("--- int_ref --- " + resp_int_ref);

            if(resp_message.equalsIgnoreCase("Approved") && "0".equals(resp_action))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            else
            {
                commResponseVO.setStatus("failed");
            }

            commResponseVO.setCurrency(resp_currency);
            commResponseVO.setAmount(resp_amount);
            commResponseVO.setResponseHashInfo(resp_rrn);
            commResponseVO.setTransactionId(resp_int_ref);
            commResponseVO.setDescription(resp_message);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions=new Functions();
        boolean isTest = gatewayAccount.isTest();

        String encryptionKey = gatewayAccount.getFRAUD_FTP_PATH();
        String refund_trtype = "";

        if(trackingID.length()<6)
        {
            trackingID = "000"+trackingID;
        }

        transactionLogger.error("PREVIOUS TRANSACTION AMOUNT --"+commTransactionDetailsVO.getPreviousTransactionAmount());

        if(functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionAmount()) && functions.isValueNull(commTransactionDetailsVO.getAmount()))
        {
            if(Float.parseFloat(commTransactionDetailsVO.getAmount()) < Float.parseFloat(commTransactionDetailsVO.getPreviousTransactionAmount()))
            {
                transactionLogger.error("Amount less than transaction amount ---- "+commTransactionDetailsVO.getAmount());
                refund_trtype = "25";
            }
            else
            {
                transactionLogger.error("Amount equals to transaction amount ---- "+commTransactionDetailsVO.getAmount());
                refund_trtype = "24";
            }
        }
        else
        {
            refund_trtype = "24";
        }

        String order = trackingID;
        String amount = commTransactionDetailsVO.getAmount();
        String currency = commTransactionDetailsVO.getCurrency();
        String rrn = commTransactionDetailsVO.getResponseHashInfo();
        String int_ref = commTransactionDetailsVO.getPreviousTransactionId();
        String trtype = refund_trtype;
        String terminal= gatewayAccount.getMerchantId();
        String timpstamp =  RomCardUtils.currentTimeToGMT();
        String nonce = null;

        transactionLogger.error("order Id ------" + order);
        transactionLogger.error("amount------" + amount);
        transactionLogger.error("currency ------" + currency);
        transactionLogger.error("rrn ------" + rrn);
        transactionLogger.error("int_ref ------" + int_ref);

        try
        {
            nonce = Functions.convertmd5(new Long(new Date().getTime()).toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in RomCardGateway---", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException in RomCardGateway---", e);
        }
        String backref = RB.getString("RETURN_URL");
        String p_sign = "";

        try
        {
            String hmac_request =
                    order.length()     +order+
                            amount.length()    +amount+
                            currency.length()  +currency+
                            rrn.length()       +rrn+
                            int_ref.length()   +int_ref+
                            trtype.length()    +trtype+
                            terminal.length()  +terminal+
                            timpstamp.length() +timpstamp+
                            nonce.length()     +nonce+
                            backref.length()   +backref;

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String refundRequest =
                    "ORDER="     + order     +
                            "&AMOUNT="     + amount    +
                            "&CURRENCY="  + currency  +
                            "&RRN="       + rrn       +
                            "&INT_REF="   + int_ref   +
                            "&TRTYPE="    + trtype    +
                            "&TERMINAL="  + terminal  +
                            "&TIMESTAMP=" + timpstamp +
                            "&NONCE="     + nonce     +
                            "&BACKREF="   + backref   +
                            "&P_SIGN="    + p_sign;

            transactionLogger.error("refundRequest-------" + refundRequest);

            String refundResponse = "";
            if (isTest)
            {
                refundResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , refundRequest);
            }
            else
            {
                refundResponse = RomCardUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , refundRequest);
            }

            transactionLogger.error("refundResponse ----------" + refundResponse);

            Document html = Jsoup.parse(refundResponse);
            String resp_action = html.body().getElementsByAttributeValue("name","ACTION").val();
            String resp_message = html.body().getElementsByAttributeValue("name","MESSAGE").val();
            String resp_amount = html.body().getElementsByAttributeValue("name","AMOUNT").val();
            String resp_currency = html.body().getElementsByAttributeValue("name","CURRENCY").val();
            String resp_order = html.body().getElementsByAttributeValue("name","ORDER").val();
            String resp_rrn = html.body().getElementsByAttributeValue("name","RRN").val();
            String resp_int_ref = html.body().getElementsByAttributeValue("name","INT_REF").val();

            if(!functions.isValueNull(resp_action))
                resp_action = html.body().getElementsByAttributeValue("name","b").val();
            if(!functions.isValueNull(resp_message))
                resp_message = html.body().getElementsByAttributeValue("name","d").val();
            if(!functions.isValueNull(resp_amount))
                resp_amount = html.body().getElementsByAttributeValue("name","f").val();
            if(!functions.isValueNull(resp_currency))
                resp_currency = html.body().getElementsByAttributeValue("name","g").val();
            if(!functions.isValueNull(resp_order))
                resp_order = html.body().getElementsByAttributeValue("name","h").val();
            if(!functions.isValueNull(resp_rrn))
                resp_rrn = html.body().getElementsByAttributeValue("name","i").val();
            if(!functions.isValueNull(resp_int_ref))
                resp_int_ref = html.body().getElementsByAttributeValue("name","j").val();

            transactionLogger.error("--- action --- " + resp_action);
            transactionLogger.error("--- message --- " + resp_message);
            transactionLogger.error("--- amount --- " + resp_amount);
            transactionLogger.error("--- currency --- " + resp_currency);
            transactionLogger.error("--- order --- " + resp_order);
            transactionLogger.error("--- rrn --- " + resp_rrn);
            transactionLogger.error("--- int_ref --- " + resp_int_ref);

            if(resp_message.equalsIgnoreCase("Approved") && "0".equals(resp_action))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            else
            {
                commResponseVO.setStatus("failed");
            }

            commResponseVO.setCurrency(resp_currency);
            commResponseVO.setAmount(resp_amount);
            commResponseVO.setResponseHashInfo(resp_rrn);
            commResponseVO.setTransactionId(resp_int_ref);
            commResponseVO.setDescription(resp_message);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }


    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in RomCard ---- ");
        CommRequestVO commRequestVO = null;
        String html = "";
        PaymentManager paymentManager=new PaymentManager();
        Comm3DResponseVO transRespDetails = null;
        commRequestVO = RomCardUtils.getRomCardRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String url ="";

        if (isTest)
        {
            url = RB.getString("TEST_URL");
        }
        else
        {
            url = RB.getString("LIVE_URL");
        }

        try
        {
            transactionLogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("EMI Count Flag -----" + commRequestVO.getTransDetailsVO().getEmiCount());
            if(commonValidatorVO.getMerchantDetailsVO().getIsService().equals("N"))
            {
                transRespDetails = (Comm3DResponseVO) this.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);
            }
            else
            {
                transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in RomCardGateway---",e);
        }

        if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
        {
            paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
            html = transRespDetails.getUrlFor3DRedirect();
            //html = RomCardUtils.AlterHtml(html , url);
        }

        return html;
    }

}