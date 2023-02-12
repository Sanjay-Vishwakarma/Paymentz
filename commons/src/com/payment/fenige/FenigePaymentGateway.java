package com.payment.fenige;

import com.directi.pg.*;
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
import com.payment.trustly.api.security.SignatureHandler;
import org.codehaus.jettison.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ResourceBundle;

/**
 * Created by Admin on 8/28/2020.
 */
public class FenigePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "fenige";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.fenige");
    private static TransactionLogger transactionLogger = new TransactionLogger(FenigePaymentGateway.class.getName());

    public FenigePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public static void main(String[] args)
    {
        try
        {

            FenigeUtils FenigeUtils = new FenigeUtils();

            StringBuffer request = new StringBuffer();
            String key = "Transactworld:Fenige123@";
            String AuthenticationKey = Base64.encode(key.getBytes());
            System.out.println("basic" + Base64.decode(key));

            String uuid = SignatureHandler.generateNewUUID();
            double money = 100.1;
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String moneyString = formatter.format(money);
            System.out.println(moneyString);

           //get terminal info.......

          /* String Response = FenigeUtils.doGetHTTPSURLConnectionClient("https://ecom-staging.fenige.pl/merchants/b6ea9b33-85c7-4b5b-b60c-b1999ea6eda1/terminals", "Basic", AuthenticationKey);
            System.out.println("Terminal info" + Response);
*/
           /* StringBuffer threed = new StringBuffer();
            threed.append("{" +
                    "\"merchantUuid\":\"b6ea9b33-85c7-4b5b-b60c-b1999ea6eda1\"," +
                    "\"requestUuid\":\""+uuid+"\"," +
                    "\"terminalUuid\":\"1510af95-5c87-477e-94eb-2a16e71f01f4\"," +
                    "\"firstName\":\"user\"," +
                    "\"lastName\":\"testUser\"," +
                    "\"amount\":500.00," +
                    "\"currency\":\"USD\"," +
                    "\"cardNumber\":\"5453131785534417\"," +
                    "\"expiryDate\":\"11/22\"," +
                    "\"cvc2\":\"123\"," +
                    "\"email\":\"test@fenige.pl\"," +
                    "\"addressIp\":\"192.0.0.1\"," +
                    "\"autoClear\":true " +
                    "}");

            String Response = "";
            System.out.println(threed);
            Response = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_HOST_URL"), "Basic", AuthenticationKey, threed.toString());
            System.out.println("terminal:::::::::" + Response);*/

            /*StringBuffer reqinit =new StringBuffer();
            reqinit.append("{" +
                    "\"merchantName\":\"Transactworld\"," +
                    "\"cardNumber\":\"5453131785534417\"," +
                    "\"cardExpMonth\":\"11\"," +
                    "\"cardExpYear\":\"22\"," +
                    "\"transactionAmount\":\"500\"," +        // must be int
                    "\"transactionDescription\":500.00," +
                    "\"transactionDisplayAmount\":\"5,00 PLN\"," +
                    "\"currencyCode\":\"180\"," +
                    "\"currencyExponent\":\"2\"," +
                    "\"termUrl\":\"https://java-staging.fenige.pl:8181/fenige-mpi-mc\"," +
                    "\"md\":\"YTg0ZGRlOTYtMzJmYi00ZTY1LWFhZTEtMTRlNGVjYTNiODE\"" +
                    "}");

            String Response1 = "";
            System.out.println(reqinit);
            Response1 = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_3DINT_URL"), "Basic", AuthenticationKey, reqinit.toString());
            System.out.println("SALE" + Response1);*/


            /*StringBuffer reqinfinal =new StringBuffer();
            String key1="MWUyZGZkNDYtNDA5Zi00NTAxLWEyYzMtOTkwYmFlMTUwYTRi";    //init generated md
            String MID = Base64.encode(key1.getBytes());
            reqinfinal.append("{" +
                    "\"pares\":\"eJxlkltvwjAMhf8K4p3m0qRUlYnE4GHVxMQuT3uLWguK2lDSFsG/n8NlbFqkSD6Oe+p8DnxuPeLyA4vBo4EVdp3d4KgqZ2M5lWpsYD1/x4OBI/qu2jsjIh5JYHdJX/hia11vwBaHp/zVaCVEooDdJDTo86XhtCa0BbBrApxt0HToStUgsIuCYj+43p9NIhJgdwGDr82279suY2xnj3ZS4hFrEQ2trcqorbNUqZgVdYWuBxaqgT3aWg8h6sj9VJUmX8w3//Yyj1e7txmwUAGl7dFILlIeSz4SOlM60ymwSx5sE9oyi6+XUax5xDnd9JqCNvxpfhV0Fo5+p4AQe3TFmcxjut5dAZ7avaPeDXH9iYE9Gl88B7pFT9jiADIsRSxpQKlKtNRqmsiUkN2KgmNF5CTn+mIZBLBgw27jJECXSVP05wV8A1/aqWQ=\"," +
                    "\"md\":\"MWUyZGZkNDYtNDA5Zi00NTAxLWEyYzMtOTkwYmFlMTUwYTRi\"" +
                    "}");

            String Response = "";
            System.out.println(request);
            Response = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_3DFINAL_URL"), "Basic", AuthenticationKey, reqinfinal.toString());
            System.out.println("Final data" + Response);*/


           /* request.append("{"+
                    "\"name\" : \"Transactworld_NON3D\","+
                "\"isDefault\" : \"true\","+
                "\"is3dsEnable\" : \"false\","+
                "\"termUrl\" : \"https://ecom-staging.fenige.pl\","+
                "\"mcc\" : \"1234\","+
                "\"settlementCurrency\" : \"USD\","+
                "\"calculateCommissionType\" : \"STANDARD\","+
                "\"commission\" :{\"domestic\" : {\"percent\" : \"15\","+
                        "\"min\" : \"0\","+
                        "\"max\" : \"10\"}, \"crossborder\" : { \"percent\" : \"15\","+
                        "\"min\" : \"0\","+
                        "\"max\" : \"10\"}}"+
            "}");

            String Response = "";
            System.out.println(request);
            Response = FenigeUtils.doPostHTTPSURLConnectionClient("https://ecom-staging.fenige.pl/merchants/b6ea9b33-85c7-4b5b-b60c-b1999ea6eda1/terminals", "Basic", AuthenticationKey, request.toString());
            System.out.println("PREAUTH" + Response);
*/



            /*request.append("{" +
                    "\"cardNumber\":\"5430149160516930\"," +
                    "\"terminalUuid\":\"e1fd7f44-275e-4182-9175-60c4da62f2a9\"" +
                    "}");

            String Response = "";
            System.out.println(request);
            Response = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PREAUTH_URL"), "Basic", AuthenticationKey, request.toString());
            System.out.println("PREAUTH" + Response);*/


            /*request.append("{" +
                    "\"cardNumber\":\"5453131785534417\"" +
                    "}");

            String Response = "";
            System.out.println(request);
            Response = FenigeUtils.doPostHTTPSURLConnectionClient("https://ecom-staging.fenige.pl/client/3ds/verify", "Basic", AuthenticationKey, request.toString());
            System.out.println("PREAUTH" + Response);*/



     /*       request.append("{" +
                    "\"merchantUuid\":\"b6ea9b33-85c7-4b5b-b60c-b1999ea6eda1\"," +
                    "\"requestUuid\":\""+uuid+"\"," +
                   "\"terminalUuid\":\"1510af95-5c87-477e-94eb-2a16e71f01f4\"," +
                    "\"firstName\":\"user\"," +
                    "\"lastName\":\"testUser\"," +
                    "\"amount\":500.00," +
                    "\"currency\":\"EUR\"," +
                    "\"cardNumber\":\"5453010000095141\"," +
                    "\"expiryDate\":\"11/22\"," +
                    "\"cvc2\":\"123\"," +
                    "\"email\":\"test@fenige.pl\"," +
                    "\"addressIp\":\"127.0.0.1\"," +
                    "\"autoClear\":true, " +
                    "\"outside3ds\":{\"cavv\":\"jEu04WZns7pbARAApU4qgNdJTag=\"," +
                    "\"cavvAlgorithm\":\"3\"," +
                    "\"eci\":\"02\"," +
                    "\"authenticationStatus\":\"Y\"," +
                    "\"transactionXId\":\"1\"" +
                    "}}");

            String Response = "";
            System.out.println(request);
            Response = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_HOST_URL"), "Basic", AuthenticationKey, request.toString());
            System.out.println("terminal:::::::::" + Response);*/


            /*String key = "Transactworld:Fenige123@";
            String AuthenticationKey = Base64.encode(key.getBytes());
           */


         /* System.out.println(AuthenticationKey);
            String Response1 = "";
            Response1 = FenigeUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_HOST") + "8ba516e6-4fc2-44a2-9c1b-dd3f263af1f5", "Basic", AuthenticationKey);
            System.out.println("INQUIRY " + Response1);*/

            /*StringBuffer REQUESTYU = new StringBuffer();
            REQUESTYU.append("{" +
                    "\"merchantUuid\":\"b6ea9b33-85c7-4b5b-b60c-b1999ea6eda1\"," +
                    "\"requestUuid\":\"8bf40a6a-d5a1-4825-a7a5-a5ac40cb3d40\"," +
                    "\"amountToRefund\":10.00 " +
                    "}");

            String refund = "";
            System.out.println(REQUESTYU);
            refund = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REFUND_HOST"), "Basic", AuthenticationKey, REQUESTYU.toString());
            System.out.println("REFUND" + refund);*/
        }
        catch (
                Exception e
                )
        {
            System.out.println(e.getMessage());
        }

    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("Inside FENIGE Saleprocess::::::::::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommDeviceDetailsVO deviceDetailsVO = ((CommRequestVO)requestVO).getCommDeviceDetailsVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantUuid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String requestUuid = SignatureHandler.generateNewUUID();
        String terminalUuid = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();

        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String key = username + ":" + password;
        transactionLogger.error("Mid deatils for FENIGE " + trackingID + "is:::::" + key);
        String AuthenticationKey = Base64.encode(key.getBytes());

        boolean isTest = gatewayAccount.isTest();
        StringBuffer request = new StringBuffer();
        StringBuffer requestlog = new StringBuffer();


        String exp_year = "";
        if (functions.isValueNull(cardDetailsVO.getExpYear()))
        {
            String inputString = cardDetailsVO.getExpYear();
            exp_year = inputString.substring(2);
        }
        String exp_date= cardDetailsVO.getExpMonth() + "/"+ exp_year;

        String termUrl = "";
        String Currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";

        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            Currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }


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

        String amount="";
        if ("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount = FenigeUtils.getJPYAmount(transDetailsVO.getAmount());
        else if ("KWD".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount = FenigeUtils.getKWDSupportedAmount(transDetailsVO.getAmount());
        else
            amount = FenigeUtils.getCentAmount(transDetailsVO.getAmount());

        String AutoClear = "true";
        String status="";
        String transactionid="";
        String message="";
        String transactionStatus="";
        String responseCode="";
        String pareq="";
        String acsUrl="";
        String md="";
        String enrolledStatus="";
        try
        {


            String first_name = new String(addressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);

            String last_name = new String(addressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);


            request.append("{" +
                    "\"merchantUuid\":\"" + merchantUuid + "\"," +
                    "\"requestUuid\":\"" + requestUuid + "\"," +
                    "\"terminalUuid\":\"" + terminalUuid + "\"," +
                    "\"firstName\":\"" + first_name + "\"," +
                    "\"lastName\":\"" + last_name + "\"," +
                    "\"amount\":\"" + amount + "\"," +
                    "\"currency\":\"" + transDetailsVO.getCurrency() + "\"," +
                    "\"cardNumber\":\"" + cardDetailsVO.getCardNum() + "\"," +
                    "\"expiryDate\":\"" + exp_date + "\"," +
                    "\"cvc2\":\"" + cardDetailsVO.getcVV() + "\"," +
                    "\"email\":\"" + addressDetailsVO.getEmail() + "\"," +
                    "\"addressIp\":\"" + addressDetailsVO.getCardHolderIpAddress() + "\"," +
                    "\"autoClear\":\"" + AutoClear + "\"" +
                     "}");

            requestlog.append("{" +
                    "\"merchantUuid\":\"" + merchantUuid + "\"," +
                    "\"requestUuid\":\"" + requestUuid + "\"," +
                    "\"terminalUuid\":\"" + terminalUuid + "\"," +
                    "\"firstName\":\"" + first_name + "\"," +
                    "\"lastName\":\"" + last_name + "\"," +
                    "\"amount\":\"" + amount + "\"," +
                    "\"currency\":\"" + transDetailsVO.getCurrency() + "\"," +
                    "\"cardNumber\":\"" + functions.maskingPan(cardDetailsVO.getCardNum()) + "\"," +
                    "\"expiryDate\":\"" + functions.maskingExpiry(exp_date) + "\"," +
                    "\"cvc2\":\"" + functions.maskingNumber(cardDetailsVO.getcVV()) + "\"," +
                    "\"email\":\"" + addressDetailsVO.getEmail() + "\"," +
                    "\"addressIp\":\"" + addressDetailsVO.getCardHolderIpAddress() + "\"," +
                    "\"autoClear\":\"" + AutoClear + "\"" +
                    "}");

            transactionLogger.error(" FENIGE SaleRequest --for--" + trackingID + "--" + requestlog.toString());

            String saleResponse = "";
            if (isTest)
            {
                transactionLogger.error(" FENIGE TEST URL --for--" + trackingID + "--" + RB.getString("TEST_HOST_URL"));
                saleResponse = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_HOST_URL"), "Basic",AuthenticationKey,request.toString());
            }
            else
            {
                transactionLogger.error(" FENIGE LIVE URL --for--" + trackingID + "--" + RB.getString("TEST_HOST_URL"));
                saleResponse = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_HOST_URL"), "Basic",AuthenticationKey,request.toString());
            }

            transactionLogger.error(" FENIGE Salerespnose --for--" + trackingID + "--" + saleResponse);
            if (functions.isValueNull(saleResponse) && saleResponse.contains("{"))
            {

            JSONObject jsonObject = new JSONObject(saleResponse);

                if (jsonObject != null)
                {
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                    }

                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                    }

                    if (jsonObject.has("requestUuid"))
                    {
                        transactionid = jsonObject.getString("requestUuid");
                    }

                    if (jsonObject.has("transactionStatus"))
                    {
                        transactionStatus = jsonObject.getString("transactionStatus");
                    }

                    if (jsonObject.has("responseCode"))
                    {
                        responseCode = jsonObject.getString("responseCode");
                    }

                    if (jsonObject.has("pareq"))
                    {
                        pareq = jsonObject.getString("pareq");
                    }

                    if (jsonObject.has("acsUrl"))
                    {
                        acsUrl = jsonObject.getString("acsUrl");
                    }

                    if (jsonObject.has("md"))
                    {
                        md = jsonObject.getString("md");
                    }

                    if (jsonObject.has("enrolledStatus"))
                    {
                        enrolledStatus = jsonObject.getString("enrolledStatus");
                    }

                    transactionLogger.error(" transactionStatus --for--" + trackingID + "--" + transactionStatus);

                    if ("APPROVED".equalsIgnoreCase(transactionStatus))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription("Transaction complete successfully");
                        commResponseVO.setRemark(message);
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setErrorCode(responseCode);
                        //commResponseVO.setErrorName(status);
                        commResponseVO.setCurrency(Currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                    }
                    else if ("DECLINED".equalsIgnoreCase(transactionStatus))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("Transaction was declined by Mastercard");
                        commResponseVO.setRemark(message);
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setErrorCode(responseCode);
                        //commResponseVO.setErrorName(status);
                        commResponseVO.setCurrency(Currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else if ("WAITING_ON_3DS_CONFIRMATION".equalsIgnoreCase(transactionStatus))
                    {
                        String DisplyAmount = amount + " " + getCurrency();
                            transactionLogger.error(":::::::::::::Inside 3D Auth version 1.0:::::::::::");
                            StringBuffer Requestinit = new StringBuffer();
                            Requestinit.append("{" +
                                    "\"merchantName\":\"" + username + "\"," +
                                    "\"cardNumber\":\"" + cardDetailsVO.getCardNum() + "\"," +
                                    "\"cardExpMonth\":\"" + cardDetailsVO.getExpMonth() + "\"," +
                                    "\"cardExpYear\":\"" + exp_year + "\"," +
                                    "\"transactionAmount\":\"" + amount + "\"," +        // must be int
                                    "\"transactionDescription\":\"" + transDetailsVO.getOrderDesc() + "\"," +
                                    "\"transactionDisplayAmount\":\""+DisplyAmount+"\"," +
                                    "\"currencyCode\":\"" + CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()) + "\"," +
                                    "\"currencyExponent\":\"2\"," +
                                    "\"termUrl\":\"" + acsUrl + "\"," +
                                    "\"md\":\"" + md + "\"" +
                                    "}");

                        StringBuffer RequestinitLog = new StringBuffer();
                        RequestinitLog.append("{" +
                                "\"merchantName\":\"" + username + "\"," +
                                "\"cardNumber\":\"" + functions.maskingPan(cardDetailsVO.getCardNum()) + "\"," +
                                "\"cardExpMonth\":\"" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "\"," +
                                "\"cardExpYear\":\"" + functions.maskingNumber(exp_year) + "\"," +
                                "\"transactionAmount\":\"" + amount + "\"," +        // must be int
                                "\"transactionDescription\":\"" + transDetailsVO.getOrderDesc() + "\"," +
                                "\"transactionDisplayAmount\":\""+DisplyAmount+"\"," +
                                "\"currencyCode\":\"" + CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()) + "\"," +
                                "\"currencyExponent\":\"2\"," +
                                "\"termUrl\":\"" + acsUrl + "\"," +
                                "\"md\":\"" + md + "\"" +
                                "}");

                            transactionLogger.error(" FENIGE 3D Initialize Request --for--" + trackingID + "--" + RequestinitLog.toString());

                            String Responseinit = "";

                            if (isTest)
                            {
                                transactionLogger.error(" FENIGE TEST URL --for--" + trackingID + "--" + RB.getString("TEST_3DINT_URL"));
                                Responseinit = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_3DINT_URL"), "Basic", AuthenticationKey, Requestinit.toString());
                            }
                            else
                            {
                                transactionLogger.error(" FENIGE LIVE URL --for--" + trackingID + "--" + RB.getString("TEST_3DINT_URL"));
                                Responseinit = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_3DINT_URL"), "Basic", AuthenticationKey, Requestinit.toString());
                            }

                            transactionLogger.error(" FENIGE 3D Initialize Response-----" + trackingID + "--" + Responseinit);

                            String new_md = "";
                            String new_pareq = "";
                            if (functions.isValueNull(saleResponse) && saleResponse.contains("{"))
                            {

                                JSONObject jsonObject1 = new JSONObject(Responseinit);

                                if (jsonObject1 != null)
                                {
                                    if (jsonObject1.has("md"))
                                    {
                                        new_md = jsonObject1.getString("md");
                                    }

                                    if (jsonObject1.has("pareq"))
                                    {
                                        new_pareq = jsonObject1.getString("pareq");
                                    }
                                }
                            }
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(acsUrl);
                            commResponseVO.setMd(new_md);
                            commResponseVO.setPaReq(new_pareq);
                            commResponseVO.setTerURL(termUrl);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                            commResponseVO.setTransactionId(transactionid);
                            commResponseVO.setErrorCode(responseCode);
                            commResponseVO.setCurrency(Currency);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setErrorCode(responseCode);
                        //commResponseVO.setErrorName(status);
                        commResponseVO.setCurrency(Currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                    }

                }
                else
                {
                    transactionLogger.error("Transaction Failed due to null json");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                transactionLogger.error("Transaction Failed due to null response");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }
        }

        catch (Exception e)
        {
            transactionLogger.error(" Fenige processSale Exception--for--" + trackingID + "--", e);
        }

        return commResponseVO;

    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("Inside FENIGE 3D Process:::::::::::::");

        Comm3DRequestVO comm3DRequestVO=(Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String PaRes=comm3DRequestVO.getPaRes();
        String MD=comm3DRequestVO.getMd();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String key = username + ":" + password;
        transactionLogger.error("Mid deatils for FENIGE " + trackingID + "is:::::" + key);
        String AuthenticationKey = Base64.encode(key.getBytes());

        boolean isTest = gatewayAccount.isTest();


        String authenticationStatus="";
        String cavv="";
        String eci="";
        String cavvAlgorithm="";
        String authenticationTime="";

        try
        {


            StringBuffer reqinfinal = new StringBuffer();
            String MID = Base64.encode(MD.getBytes());
            reqinfinal.append("{" +
                    "\"pares\":\"" + PaRes + "\"," +
                    "\"md\":\"" + MID + "\"" +
                    "}");

            transactionLogger.error(" FENIGE 3D FINALIZE Request --for--" + trackingID + "--" + reqinfinal);
            String Response = "";

            if (isTest)
            {
                transactionLogger.error(" FENIGE TEST 3D FINALIZE URL --for--" + trackingID + "--" + RB.getString("TEST_3DFINAL_URL"));
                Response = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_3DFINAL_URL"), "Basic", AuthenticationKey, reqinfinal.toString());
            }
            else
            {
                transactionLogger.error(" FENIGE lIVE 3D FINALIZE --for--" + trackingID + "--" + RB.getString("TEST_3DFINAL_URL"));
                Response = FenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_3DFINAL_URL"), "Basic", AuthenticationKey, reqinfinal.toString());
            }

            transactionLogger.error(" FENIGE 3D FINALIZE Response-----" + trackingID + "--" + Response);


            if (functions.isValueNull(Response) && Response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(Response);

                if (jsonObject != null)
                {
                    if (jsonObject.has("authenticationStatus"))
                    {
                        authenticationStatus = jsonObject.getString("authenticationStatus");
                    }

                    if (jsonObject.has("cavv"))
                    {
                        cavv = jsonObject.getString("cavv");
                    }

                    if (jsonObject.has("eci"))
                    {
                        eci = jsonObject.getString("eci");
                    }

                    if (jsonObject.has("authenticationTime"))
                    {
                        authenticationTime = jsonObject.getString("authenticationTime");
                    }

                    if (jsonObject.has("cavvAlgorithm"))
                    {
                        cavvAlgorithm = jsonObject.getString("cavvAlgorithm");
                    }

                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                    commResponseVO.setEci(eci);
                }
                else
                {
                    transactionLogger.error("Transaction Failed due to null response");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Invalid Response");
                    commResponseVO.setDescription("Invalid Response");
                }
            }
        }

        catch (Exception e)
        {
            transactionLogger.error(" Fenige 3D Confirmation Exception--for--" + trackingID + "--", e);
        }


        return commResponseVO;

    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Fenige:: inside processInquiry()");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String merchantid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String terminalUuid = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();
        String Paymentid = transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("Paymentid in inquiry::::::::::::::" + Paymentid);
        FenigeUtils fenigeUtils = new FenigeUtils();
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String Responseid = "";
        String Status = "";
        String Responsedate = "";
        String Description = "";
        String amount="";
        String currency="";
        String transactionStatus="";


        String key = username + ":" + password;
        transactionLogger.error("Mid deatils for FENIGE " + trackingID + "is:::::" + key);
        String AuthenticationKey = Base64.encode(key.getBytes());
        String Response = "";

        try
        {

            if (isTest)
            {
                transactionLogger.error("FENIGE INQUIRY TEST URL --for--" + trackingID + "--" + RB.getString("TEST_INQUIRY_HOST") + Paymentid);
                Response = fenigeUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_HOST") + Paymentid, "Basic", AuthenticationKey);
            }
            else
            {
                transactionLogger.error("FENIGE INQUIRY LIVE URL --for--" + trackingID + "--" + RB.getString("TEST_INQUIRY_HOST") + Paymentid);
                Response = fenigeUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_HOST") + Paymentid, "Basic", AuthenticationKey);
            }

            transactionLogger.error("FENIGE INQUIRY LIVE Response --for--" + trackingID + "--" + Response);


            if (functions.isValueNull(Response) && Response.contains("{"))
            {

                JSONObject jsonObject = new JSONObject(Response);


                if (jsonObject != null)
                {
                    if (jsonObject.has("requestUuid"))
                    {
                        Responseid = jsonObject.getString("requestUuid");
                    }

                    if (jsonObject.has("createdDate"))
                    {
                        Responsedate = jsonObject.getString("createdDate");
                    }

                    if (jsonObject.has("amount"))
                    {
                        amount = jsonObject.getString("amount");
                    }

                    if (jsonObject.has("currency"))
                    {
                        currency = jsonObject.getString("currency");
                    }

                    if (jsonObject.has("message"))
                    {
                        Description = jsonObject.getString("message");
                    }

                    if (jsonObject.has("clearingStatus"))
                    {
                        Status = jsonObject.getString("clearingStatus");
                    }

                    if (jsonObject.has("transactionStatus"))
                    {
                        transactionStatus = jsonObject.getString("transactionStatus");
                    }


                    transactionLogger.error(" Fenige Inquiry Status--for--" + transactionStatus + "--");


                    if ("APPROVED".equalsIgnoreCase(transactionStatus))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setDescription("Transaction complete successfully");
                        commResponseVO.setRemark("Transaction complete successfully");

                    }
                    else if ("DECLINED".equalsIgnoreCase(transactionStatus))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setDescription("Transaction was declined by Mastercard");
                        commResponseVO.setRemark("Transaction was declined by Mastercard");

                    }
                    else if ("REJECTED".equalsIgnoreCase(transactionStatus))
                    {
                        commResponseVO.setStatus("rejected");
                        commResponseVO.setTransactionStatus("rejected");
                        commResponseVO.setDescription("Something went wrong and some validation error occurred");
                        commResponseVO.setRemark("Something went wrong and some validation error occurred");

                    }
                    else if ("REVERSED".equalsIgnoreCase(transactionStatus))
                    {
                        commResponseVO.setStatus("reversed");
                        commResponseVO.setTransactionStatus("reversed");
                        commResponseVO.setDescription("Transaction was reversed successfully");
                        commResponseVO.setRemark("Transaction was reversed successfully");

                    }else if ("WAITING_ON_3DS_CONFIRMATION".equalsIgnoreCase(transactionStatus))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setTransactionStatus("pending3DConfirmation");
                        commResponseVO.setDescription("pending3DConfirmation");
                        commResponseVO.setRemark("pending3DConfirmation");
                    }
                    else{
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setDescription(transactionStatus);
                        commResponseVO.setRemark("Transaction Failed");
                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Transaction Failed");
                }

                commResponseVO.setBankTransactionDate(Responsedate);
                commResponseVO.setTransactionId(Responseid);
                commResponseVO.setMerchantId(merchantid);
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setAmount(amount);
                commResponseVO.setCurrency(currency);
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
            transactionLogger.error(" Fenige Inquiry Exception for paymentid" + trackingID + "--", e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Fenige:: inside processRefund");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String merchantUuid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
          String paymentid = transactionDetailsVO.getPreviousTransactionId();

        transactionLogger.error("paymentid::::::::::" + paymentid);

        String amount="";
        if ("JPY".equalsIgnoreCase(transactionDetailsVO.getCurrency()))
            amount = FenigeUtils.getJPYAmount(transactionDetailsVO.getAmount());
        else if ("KWD".equalsIgnoreCase(transactionDetailsVO.getCurrency()))
            amount = FenigeUtils.getKWDSupportedAmount(transactionDetailsVO.getAmount());
        else
            amount = FenigeUtils.getCentAmount(transactionDetailsVO.getAmount());

        FenigeUtils fenigeUtils = new FenigeUtils();
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String key = username + ":" + password;
        transactionLogger.error("Mid deatils for FENIGE " + trackingID + "is:::::" + key);
        String AuthenticationKey = Base64.encode(key.getBytes());
        StringBuffer request = new StringBuffer();

        String message ="";
        String responseCode="";
        String status = "";
        String refundStatus="";

        request.append("{" +
                "\"merchantUuid\":\"" + merchantUuid + "\"," +
                "\"requestUuid\":\"" + paymentid + "\"," +
                "\"amountToRefund\":\"" + amount + "\"" +
                "}");

        transactionLogger.error(" FENIGE Refund Request --for--" + trackingID + "--" + request.toString());


        try
        {

            String Response = "";
            if (isTest)
            {
                transactionLogger.error("FENIGE REFUND TEST URL --for--" + trackingID + "--" + RB.getString("TEST_REFUND_HOST"));
                Response = fenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REFUND_HOST"), "Basic", AuthenticationKey,request.toString());
            }
            else
            {
                transactionLogger.error("FENIGE REFUND LIVE URL --for--" + trackingID + "--" + RB.getString("TEST_REFUND_HOST"));
                Response = fenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REFUND_HOST"), "Basic", AuthenticationKey,request.toString());
            }

            if (functions.isValueNull(Response) && Response.contains("{"))
            {

                JSONObject jsonObject = new JSONObject(Response);


                if (jsonObject != null)
                {
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                    }

                    if (jsonObject.has("refundStatus"))
                    {
                        refundStatus = jsonObject.getString("refundStatus");
                    }

                    if (jsonObject.has("responseCode"))
                    {
                        responseCode = jsonObject.getString("responseCode");
                    }

                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                    }

                    if ("APPROVED".equalsIgnoreCase(refundStatus))
                    {
                        transactionLogger.error("Refund successfull:::::::for" + trackingID + "--" + amount);
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setErrorCode(responseCode);
                    }
                    else if ("DECLINED".equalsIgnoreCase(refundStatus))
                    {
                        transactionLogger.error("Refund Declined:::::::for" + trackingID + "--" + amount);
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setErrorCode(responseCode);
                    }
                    else
                    {
                        transactionLogger.error("Refund failed:::::::for" + trackingID + "is" + refundStatus);
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setErrorCode(responseCode);

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
            transactionLogger.error(" Fenige Refund Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Fenige:: inside processVoid()to cancel the transaction");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String merchantUuid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String paymentid = transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("paymentid::::::::::" + paymentid);

        FenigeUtils fenigeUtils = new FenigeUtils();
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String key = username + ":" + password;
        transactionLogger.error("Mid deatils for FENIGE " + trackingID + "is:::::" + key);
        String AuthenticationKey = Base64.encode(key.getBytes());
        StringBuffer request = new StringBuffer();

        String message ="";
        String responseCode="";
        String status = "";
        String requestUuid="";

        request.append("{" +
                "\"merchantUuid\":\"" + merchantUuid + "\"," +
                "\"requestUuid\":\"" + paymentid + "\"," +
                "}");

        transactionLogger.error(" FENIGE REVERSAL Request --for--" + trackingID + "--" + request.toString());


        try
        {

            String Response = "";
            if (isTest)
            {
                transactionLogger.error("FENIGE REVERSAL TEST URL --for--" + trackingID + "--" + RB.getString("TEST_REVERSAL_URL"));
                Response = fenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REVERSAL_URL"), "Basic", AuthenticationKey,request.toString());
            }
            else
            {
                transactionLogger.error("FENIGE REVERSAL LIVE URL --for--" + trackingID + "--" + RB.getString("TEST_REVERSAL_URL"));
                Response = fenigeUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REVERSAL_URL"), "Basic", AuthenticationKey,request.toString());
            }

            if (functions.isValueNull(Response) && Response.contains("{"))
            {

                JSONObject jsonObject = new JSONObject(Response);


                if (jsonObject != null)
                {
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                    }

                    if (jsonObject.has("requestUuid"))
                    {
                        requestUuid = jsonObject.getString("requestUuid");
                    }

                    if (jsonObject.has("status"))
                    {
                        responseCode = jsonObject.getString("status");
                    }

                    if (jsonObject.has("reversalStatus"))
                    {
                        status = jsonObject.getString("reversalStatus");
                    }

                        if ("SUCCESS".equalsIgnoreCase(status))
                        {
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setErrorCode(responseCode);
                        }
                        commResponseVO.setTransactionId(requestUuid);
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);

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
            transactionLogger.error(" Fenige cancel Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }


}
