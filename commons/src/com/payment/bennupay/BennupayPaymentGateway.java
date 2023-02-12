package com.payment.bennupay;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.TransactionDetailsVO;
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
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Created by Admin on 4/21/2021.
 */
public class BennupayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "bennupay";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.bennupay");
    private static TransactionLogger transactionLogger = new TransactionLogger(BennupayPaymentGateway.class.getName());

    public BennupayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public String getMaxWaitDays()
    {
        return null;
    }

    public static void main(String[] args)
    {
        try
        {
            TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();
            BennupayUtils bennupayutils = new BennupayUtils();
            Functions functions = new Functions();
            String Merchantid="3034";
            String TerminalId = "30346";
            String transactionid = "345677778";
            System.out.println("transactionid" + transactionid);

            String APIPassword= "5npFxc3D";

            String PrivateKey= "kJ0exR6fBjvZP4GWCX5KF9oQ";
            String signature=bennupayutils.requestHashSignature(transactionid,PrivateKey);

            System.out.println("signature" +signature);
            String key=TerminalId + ":" + APIPassword;
            System.out.println(key);
            String AuthenticationKey = Base64.encode(key.getBytes());
            System.out.println("SecretKey" +AuthenticationKey);

            StringBuffer request = new StringBuffer();

           /* request.append("{"+
                    "\"Credentials\":{"+
           "\"MerchantId\":\""+Merchantid+"\","+
                    "\"Signature\":\""+signature+"\""+
        "},"+
            "\"CustomerDetails\":{"+
            "\"FirstName\": \"John\","+
                    "\"LastName\":\"Smith\","+
                    "\"CustomerIP\":\"127.0.0.1\","+
                    "\"Phone\":\"99894511\","+
                    "\"Email\":\"customer.email@email.com\","+
                    "\"Street\":\"Oxford\","+
                    "\"City\":\"London\","+
                    "\"Region\":\"\","+
                    "\"Country\":\"GB\","+
                    "\"Zip\":\"LND-032\""+
        "},"+
            "\"CardDetails\":{"+
            "\"CardHolderName\":\"John Smith\","+
                    "\"CardNumber\":\"4200000000000000\","+
                    "\"CardExpireMonth\":\"01\","+
                    "\"CardExpireYear\":\"22\","+
                    "\"CardSecurityCode\":\"123\""+
        "},"+
            "\"ProductDescription\":\"Tv Product\","+
                "\"TotalAmount\":\"1300\","+
                "\"CurrencyCode\":\"USD\","+
                "\"TransactionId\":\""+transactionid+"\","+
                "\"Custom\":\"\""+
            "}");*/

            request.append("{" +
                    "\"Credentials\":{" +
                    "\"MerchantId\": \"" + Merchantid + "\"," +
                    "\"Signature\": \"" + signature + "\"" +
                    "}," +
                    "\"TransactionId\": \""+transactionid+"\"," +
                    "\"ConfirmationNumber\":\"TBN20210503182047501\"," +
                    "\"TotalAmount\":\"2000\"," +
                    "\"RefundDescription\":\"Customer Reguest\""+
                    "}");


                System.out.println("refund response" +request + " " + RB.getString("TEST_REFUND_URL"));

                String Response = BennupayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REFUND_URL"), "Basic", AuthenticationKey,request.toString());


            System.out.println("refund response" +Response);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("BennupayPaymentGateway:: inside processmainSale()");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        BennupayUtils bennupayutils = new BennupayUtils();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        boolean isTest = gatewayAccount.isTest();
        String Merchantid=GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String TerminalId = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String PrivateKey= GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String APIPassword= GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String key=TerminalId + ":" + APIPassword;
        String AuthenticationKey = Base64.encode(key.getBytes());

        String Currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";

        if (functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            Currency = transactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }

        String amount="";
        if ("JPY".equalsIgnoreCase(transactionDetailsVO.getCurrency()))
            amount = bennupayutils.getJPYAmount(transactionDetailsVO.getAmount());
        else if ("KWD".equalsIgnoreCase(transactionDetailsVO.getCurrency()))
            amount = bennupayutils.getKWDSupportedAmount(transactionDetailsVO.getAmount());
        else
            amount = bennupayutils.getCentAmount(transactionDetailsVO.getAmount());
        String termUrl = "";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
        termUrl = RB.getString("TERM_URL")+trackingID;
        transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }

        /*termUrl = RB.getString("TERM_URL")+trackingID;*/
        transactionLogger.error("From RB TERM_URL ----" + termUrl);
        String notify_url=RB.getString("NOTIFY_URL")+trackingID;

        String street = "";
        String country = "";
        String state = "";
        String city = "";
        String zip = "";
        String phone = "";
        String Descriptor="";
        String ip="";


        if (functions.isValueNull(commAddressDetailsVO.getStreet()))
        {
            street = commAddressDetailsVO.getStreet();
        }

        if (functions.isValueNull(commAddressDetailsVO.getCountry()))
        {
            country = commAddressDetailsVO.getCountry();
            if(country.length()>2)
            {
                country=country.substring(0,2);
            }
        }

        if (functions.isValueNull(commAddressDetailsVO.getCity()))
        {
            city = commAddressDetailsVO.getCity();
        }

        if (functions.isValueNull(commAddressDetailsVO.getZipCode()))
        {
            zip = commAddressDetailsVO.getZipCode();
        }

        if (functions.isValueNull(commAddressDetailsVO.getPhone()))
        {
            phone = commAddressDetailsVO.getPhone();
            if(phone.contains("+"))
                phone=phone.replaceAll("\\+","");
        }

        if (functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
        {
            ip = commAddressDetailsVO.getCardHolderIpAddress();
        }else {
            ip=commAddressDetailsVO.getIp();
        }

        String exp_year = "";
        if (functions.isValueNull(commCardDetailsVO.getExpYear()))
        {
            String inputString = commCardDetailsVO.getExpYear();
            exp_year = inputString.substring(2);
        }


        try
        {
            String signature=bennupayutils.requestHashSignature(trackingID,PrivateKey);

            String first_name = new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);

            String last_name = new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);


            StringBuffer request = new StringBuffer();
            StringBuffer requestlogs = new StringBuffer();

            request.append("{" +
                    "\"Credentials\":{ " +
                    "\"MerchantId\":\""+Merchantid+"\"," +
                    "\"Signature\":\""+signature+"\"}," +
                    "\"CustomerDetails\": {" +
                    "\"FirstName\":\""+first_name+"\"," +
                    "\"LastName\":\""+last_name+"\"," +
                    "\"CustomerIP\":\""+ip+"\"," +
                    "\"Phone\":\""+phone+"\"," +
                    "\"Email\":\""+commAddressDetailsVO.getEmail()+"\"," +
                    "\"Street\":\""+street+"\"," +
                    "\"City\":\""+city+"\"," +
                    "\"Region\":\""+state+"\"," +
                    "\"Country\":\""+country+"\"," +
                    "\"Zip\":\""+zip+"\"}," +
                    "\"CardDetails\":{" +
                    "\"CardHolderName\":\""+first_name+" "+last_name+ "\"," +
                    "\"CardNumber\":\""+commCardDetailsVO.getCardNum()+"\"," +
                    "\"CardExpireMonth\":\""+commCardDetailsVO.getExpMonth()+"\"," +
                    "\"CardExpireYear\":\""+exp_year+"\"," +
                    "\"CardSecurityCode\":\""+commCardDetailsVO.getcVV()+"\"}," +
                    "\"ProductDescription\":\""+transactionDetailsVO.getOrderDesc()+"\"," +
                    "\"TotalAmount\":\""+amount+"\"," +
                    "\"CurrencyCode\":\""+Currency+"\"," +
                    "\"TransactionId\":\""+trackingID+"\"," +
                    "\"CallbackURL\":\""+notify_url+"\"," +
                    "\"ReturnUrl\":\""+termUrl+"\"," +
                    "\"Custom\":\"\"" +
                    "}");

            requestlogs.append("{" +
                    "\"Credentials\": { " +
                    "\"MerchantId\": \""+Merchantid+"\"," +
                    "\"Signature\": \""+signature+"\"}," +
                    "\"CustomerDetails\": {" +
                    "\"FirstName\": \""+first_name+"\"," +
                    "\"LastName\": \""+last_name+"\"," +
                    "\"CustomerIP\": \""+ip+"\"," +
                    "\"Phone\": \""+phone+"\"," +
                    "\"Email\": \""+commAddressDetailsVO.getEmail()+"\"," +
                    "\"Street\": \""+street+"\"," +
                    "\"City\": \""+city+"\"," +
                    "\"Region\": \""+state+"\"," +
                    "\"Country\": \""+country+"\"," +
                    "\"Zip\": \""+zip+"\"}," +
                    "\"CardDetails\": {" +
                    "\"CardHolderName\": \""+first_name+" "+last_name+ "\"," +
                    "\"CardNumber\": \""+functions.maskingPan(commCardDetailsVO.getCardNum())+"\"," +
                    "\"CardExpireMonth\": \""+functions.maskingPan(commCardDetailsVO.getExpMonth())+"\"," +
                    "\"CardExpireYear\": \""+functions.maskingPan(exp_year)+"\"," +
                    "\"CardSecurityCode\": \""+functions.maskingPan(commCardDetailsVO.getcVV())+"\"}," +
                    "\"ProductDescription\": \""+transactionDetailsVO.getOrderDesc()+"\"," +
                    "\"TotalAmount\": \""+amount+"\"," +
                    "\"CurrencyCode\": \""+Currency+"\"," +
                    "\"TransactionId\": \""+trackingID+"\"," +
                    "\"CallbackURL\":\""+notify_url+"\"," +
                    "\"ReturnUrl\":\""+termUrl+"\"," +
                    "\"Custom\": \"var="+trackingID+"\"" +
                    "}");

            String Response = "";
            transactionLogger.error("bennupay salerequest:::::::for--" + trackingID + "--" + requestlogs.toString());
            if (isTest)
            {
                transactionLogger.error(" bennupay TEST URL --for--" + trackingID + "--" + RB.getString("TEST_HOST_URL"));
                Response = bennupayutils.doPostHTTPSURLConnectionClient(RB.getString("TEST_HOST_URL"), "Basic",AuthenticationKey,request.toString());
            }
            else
            {
                transactionLogger.error(" bennupay LIVE URL --for--" + trackingID + "--" + RB.getString("LIVE_HOST_URL"));
                Response = bennupayutils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_HOST_URL"), "Basic",AuthenticationKey,request.toString());
            }
            transactionLogger.error("bennupay sale Response  --for--" + trackingID + "--" + Response);
            String ConfirmationNumber = "";
            String TransactionId ="";
            String Code ="";
            String Description ="";
            String TotalAmount ="";
            String TxnTime ="";
            String PaymentStatus ="";
            String CurrencyCode ="";
            String SecurePage="";
            if (functions.isValueNull(Response) && Response.contains("{"))
            {

                JSONObject jsonObject = new JSONObject(Response);


                if (jsonObject != null)
                {
                    if (jsonObject.has("ConfirmationNumber"))
                    {

                        ConfirmationNumber = jsonObject.getString("ConfirmationNumber");

                    }

                    if (jsonObject.has("TransactionId"))
                    {

                        TransactionId = jsonObject.getString("TransactionId");

                    }

                    if (jsonObject.has("PaymentStatus"))
                    {

                        PaymentStatus = jsonObject.getString("PaymentStatus");

                    }

                    if (jsonObject.has("Code"))
                    {

                        Code = jsonObject.getString("Code");

                    }

                    if (jsonObject.has("TotalAmount"))
                    {

                        TotalAmount = jsonObject.getString("TotalAmount");

                    }

                    if (jsonObject.has("Description"))
                    {

                        Description = jsonObject.getString("Description");

                    }

                    if (jsonObject.has("TxnTime"))
                    {

                        TxnTime = jsonObject.getString("TxnTime");

                    }

                    if (jsonObject.has("SecurePage"))
                    {

                        SecurePage = jsonObject.getString("SecurePage");

                    }

                    if (jsonObject.has("CurrencyCode"))
                    {

                        CurrencyCode = jsonObject.getString("CurrencyCode");

                    }

                    if ("APPROVED".equalsIgnoreCase(PaymentStatus))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionId(ConfirmationNumber);
                        commResponseVO.setErrorCode(Code);
                        commResponseVO.setCurrency(Currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                    }
                    else if ("DECLINED".equalsIgnoreCase(PaymentStatus))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionId(ConfirmationNumber);
                        commResponseVO.setErrorCode(Code);
                        //commResponseVO.setErrorName(status);
                        commResponseVO.setCurrency(Currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                    }
                    else if ("3D Authorization required".equalsIgnoreCase(PaymentStatus) || Code.equals("99") )
                    {
                        String DisplyAmount = amount + " " + getCurrency();
                        transactionLogger.error(":::::::::::::Inside 3D Auth version 1.0:::::::::::");
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(SecurePage);
                         //commResponseVO.setPaReq(new_pareq);
                        commResponseVO.setTerURL(termUrl);
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionId(ConfirmationNumber);
                        commResponseVO.setErrorCode(Code);
                        commResponseVO.setCurrency(Currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionId(ConfirmationNumber);
                        commResponseVO.setErrorCode(Code);
                        commResponseVO.setCurrency(Currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                    }
            }
            else
            {
                transactionLogger.error("Transaction Failed due to null json");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }else
        {
            transactionLogger.error("Transaction Failed due to null response");
            commResponseVO.setStatus("failed");
            commResponseVO.setRemark("Invalid Response");
            commResponseVO.setDescription("Invalid Response");
        }
        }
        catch (Exception e)
        {
            transactionLogger.error(" Bennupay processSale Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("Inside Bennupay 3D Process:::::::::::::");
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        try
        {
            commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
        }
        catch (Exception e)
        {
            transactionLogger.error(" bennupay 3D Confirmation Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;

    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Bennupay::: inside processInquiry()");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        BennupayUtils bennupayUtils = new BennupayUtils();

        boolean isTest = gatewayAccount.isTest();
        String Merchantid=GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String TerminalId = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String PrivateKey= GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String APIPassword= GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String key=TerminalId + ":" + APIPassword;
        String AuthenticationKey = Base64.encode(key.getBytes());

        String Paymentid = transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("Paymentid in inquiry::::::::::::::" + Paymentid);

        String Description = "";
        String Response = "";
        StringBuffer request = new StringBuffer();
        String UniqueId=bennupayUtils.generateAuthorizationIdentificationResponse();


        try
        {
            String signature=bennupayUtils.requestHashSignature(UniqueId,PrivateKey);
            request.append("{" +
                    "\"Credentials\":{"+
                    "\"MerchantId\": \""+Merchantid+"\","+
                    "\"Signature\": \""+signature+"\""+
                    "},"+
                    "\"TransactionId\": \""+UniqueId+"\","+
                    "\"ConfirmationNumber\":\""+Paymentid+"\""+
                    "}");
            transactionLogger.error("Bennupay INQUIRY Request --for--" + trackingID + "--" + request.toString());

            if (isTest)
            {
                transactionLogger.error("Bennupay INQUIRY TEST URL --for--" + trackingID + "--" + RB.getString("TEST_INQUIRY_URL"));
                Response = bennupayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_URL"), "Basic", AuthenticationKey,request.toString());
            }
            else
            {
                transactionLogger.error("Bennupay INQUIRY LIVE URL --for--" + trackingID + "--" + RB.getString("LIVE_INQUIRY_URL"));
                Response = bennupayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_INQUIRY_URL"), "Basic", AuthenticationKey,request.toString());
            }

            transactionLogger.error("Bennnupay INQUIRY Response --for--" + trackingID + "--" + Response);

            String ConfirmationNumber = "";
            String TransactionId ="";
            String Code ="";
            String TotalAmount ="";
            String TxnTime ="";
            String PaymentStatus ="";
            String CurrencyCode="";
            if (functions.isValueNull(Response) && Response.contains("{"))
            {

                JSONObject jsonObject = new JSONObject(Response);


                if (jsonObject != null)
                {

                    if (jsonObject.has("PaymentStatus"))
                    {

                        PaymentStatus = jsonObject.getString("PaymentStatus");

                    }

                    if (jsonObject.has("ConfirmationNumber"))
                    {

                        ConfirmationNumber = jsonObject.getString("ConfirmationNumber");

                    }


                    if (jsonObject.has("Code"))
                    {

                        Code = jsonObject.getString("Code");

                    }

                    if (jsonObject.has("TotalAmount"))
                    {

                        TotalAmount = jsonObject.getString("TotalAmount");

                    }

                    if (jsonObject.has("Description"))
                    {

                        Description = jsonObject.getString("Description");

                    }

                    if (jsonObject.has("TxnTime"))
                    {

                        TxnTime = jsonObject.getString("TxnTime");

                    }

                    if (jsonObject.has("CurrencyCode"))
                    {

                        CurrencyCode = jsonObject.getString("CurrencyCode");

                    }


                    transactionLogger.error(" bennupay Inquiry Status--for--" + PaymentStatus + "--");

                    if ("APPROVED".equalsIgnoreCase(PaymentStatus))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if ("DECLINED".equalsIgnoreCase(PaymentStatus))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);

                    }
                    else if ("PENDING".equalsIgnoreCase(PaymentStatus) || "99".equals(Code))
                    {
                        commResponseVO.setStatus("rejected");
                        commResponseVO.setTransactionStatus("rejected");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if (Description.contains("Refund"))
                    {
                        commResponseVO.setStatus("reversed");
                        commResponseVO.setTransactionStatus("reversed");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());

                    }
                    else{
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus(PaymentStatus);
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());

                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Transaction Failed");
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }

                commResponseVO.setBankTransactionDate(TxnTime);
                commResponseVO.setTransactionId(ConfirmationNumber);
                commResponseVO.setMerchantId(Merchantid);
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setAmount(transactionDetailsVO.getAmount());
                commResponseVO.setCurrency(CurrencyCode);
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error(" Bennupay Inquiry Exception for paymentid" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Bennupay:: inside processRefund");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        BennupayUtils bennupayUtils = new BennupayUtils();
        String Merchantid=GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String TerminalId = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String PrivateKey= GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String APIPassword= GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String key=TerminalId + ":" + APIPassword;
        String AuthenticationKey = Base64.encode(key.getBytes());

        String Paymentid = transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("Paymentid in inquiry::::::::::::::" + Paymentid);
        String amount="";
        if ("JPY".equalsIgnoreCase(transactionDetailsVO.getCurrency()))
            amount = bennupayUtils.getJPYAmount(transactionDetailsVO.getAmount());
        else if ("KWD".equalsIgnoreCase(transactionDetailsVO.getCurrency()))
            amount = bennupayUtils.getKWDSupportedAmount(transactionDetailsVO.getAmount());
        else
            amount = bennupayUtils.getCentAmount(transactionDetailsVO.getAmount());

        String Description = "";
        String Response = "";
        StringBuffer request = new StringBuffer();
        String UniqueId=bennupayUtils.UniqueforRefund();


        try
        {
            String signature=bennupayUtils.requestHashSignature(UniqueId,PrivateKey);

        request.append("{" +
                "\"Credentials\":{"+
                "\"MerchantId\": \""+Merchantid+"\","+
                "\"Signature\": \""+signature+"\""+
                "},"+
                  "\"TransactionId\": \""+UniqueId+"\","+
                  "\"ConfirmationNumber\":\""+Paymentid+"\","+
                  "\"TotalAmount\":\""+amount+"\","+
                  "\"RefundDescription\":\""+transactionDetailsVO.getOrderDesc()+"\""+
                  "}");

        transactionLogger.error(" bennupay Refund Request --for--" + trackingID + "--" + request.toString());

            if (isTest)
            {
                transactionLogger.error("bennupay REFUND TEST URL --for--" + trackingID + "--" + RB.getString("TEST_REFUND_URL"));
                Response = bennupayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REFUND_URL"), "Basic", AuthenticationKey,request.toString());
            }
            else
            {
                transactionLogger.error("bennupay REFUND LIVE URL --for--" + trackingID + "--" + RB.getString("LIVE_REFUND_URL"));
                Response = bennupayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_REFUND_URL"), "Basic", AuthenticationKey,request.toString());
            }

            transactionLogger.error(" bennupay Refund Response --for--" + trackingID + "--" + Response);
            String ConfirmationNumber = "";
            String TransactionId ="";
            String Code ="";
            String TotalAmount ="";
            String TxnTime ="";
            String PaymentStatus ="";


            if (functions.isValueNull(Response) && Response.contains("{"))
            {

                JSONObject jsonObject = new JSONObject(Response);


                if (jsonObject != null)
                {
                    if (jsonObject.has("ConfirmationNumber"))
                    {

                        ConfirmationNumber = jsonObject.getString("ConfirmationNumber");

                    }

                    if (jsonObject.has("TransactionId"))
                    {

                        TransactionId = jsonObject.getString("TransactionId");

                    }

                    if (jsonObject.has("PaymentStatus"))
                    {

                        PaymentStatus = jsonObject.getString("PaymentStatus");

                    }

                    if (jsonObject.has("Code"))
                    {

                        Code = jsonObject.getString("Code");

                    }

                    if (jsonObject.has("TotalAmount"))
                    {

                        TotalAmount = jsonObject.getString("TotalAmount");

                    }

                    if (jsonObject.has("Description"))
                    {

                        Description = jsonObject.getString("Description");

                    }

                    if (jsonObject.has("TxnTime"))
                    {

                        TxnTime = jsonObject.getString("TxnTime");

                    }

                    if ("APPROVED".equalsIgnoreCase(PaymentStatus))
                    {
                        transactionLogger.error("Refund successfull:::::::for" + trackingID + "--" + amount);
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setErrorCode(Code);
                        commResponseVO.setTransactionId(ConfirmationNumber);
                    }
                    else if ("DECLINED".equalsIgnoreCase(PaymentStatus))
                    {
                        transactionLogger.error("Refund Declined:::::::for" + trackingID + "--" + amount);
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setErrorCode(Code);
                        commResponseVO.setTransactionId(ConfirmationNumber);
                    }
                    else
                    {
                        transactionLogger.error("Refund failed:::::::for" + trackingID + "is" + PaymentStatus);
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(Description);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setErrorCode(Code);
                        commResponseVO.setTransactionId(ConfirmationNumber);

                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Transaction Failed");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Transaction Failed");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error(" bennupay Refund Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

}
