package com.payment.knox;

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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Created by Admin on 8/4/2020.
 */
public class KnoxPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "knox";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.knox");
    private static TransactionLogger transactionLogger = new TransactionLogger(KnoxPaymentGateway.class.getName());

    public KnoxPaymentGateway(String accountId)
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
            KnoxUtils KnoxUtils = new KnoxUtils();
            Functions functions = new Functions();
            String Key = "5f298299cdcfe";
            String SecretKey = "f37539958d7c5abd3750ff9d57eaa86e";

            StringBuffer request = new StringBuffer();

            request.append("{" +
                    "\"first_name\":\"Göran\"," +
                    "\"last_name\":\"rty\"," +
                    "\"email\":\"test@gmail.com\"," +
                    "\"billing_address1\":\"xyz\"," +
                    "\"billing_country\":\"IN\"," +
                    "\"billing_state\":\"mh\"," +
                    "\"billing_city\":\"Anaheim\"," +
                    "\"billing_zip\":\"400056\"," +
                    "\"mobile\":\"9845684125\"," +
                    "\"customer_ip\":\"192.168.1.1\"," +
                    "\"billing_address2\":\"\"," +
                    "\"card_cvv\":\"123\"," +
                    "\"card_no\":\"4111111111111111\"," +
                    "\"expiry_month\":\"08\"," +
                    "\"expiry_year\":\"2030\"," +
                    "\"order_description\":\"knox tx1\"," +
                    "\"amount\":\"100.00\"" +
                    "}");


            String Response = "";
            System.out.println(request);
            System.out.println(RB.getString("LIVE_CC_URL"));
            Response = KnoxUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_CC_URL"), Key, SecretKey, request.toString());
            System.out.println(Response);
        }
        catch (Exception e)
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

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("KnoxPaymentGatway:: inside processmainSale()");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        KnoxUtils KnoxUtils = new KnoxUtils();
        String Key = gatewayAccount.getMerchantId();
        String SecretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();

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
            if(street.contains("/"))
                street=street.replaceAll("/"," ");
            if(street.contains("\\"))
                street=street.replaceAll("\\\\"," ");
        }
        else{
            street="36 Congress St #1";
        }

        if (functions.isValueNull(commAddressDetailsVO.getCountry()))
        {
            country = commAddressDetailsVO.getCountry();
            if(country.length()>2)
            {
                country=country.substring(0,2);
            }
        }
        else{
            country="US";
        }


        if (functions.isValueNull(commAddressDetailsVO.getState()))
        {
            state = commAddressDetailsVO.getState();
        }
        else{
            state="NY";
        }

        if (functions.isValueNull(commAddressDetailsVO.getCity()))
        {
            city = commAddressDetailsVO.getCity();
        }
        else {
            city="cohoes";
        }

        if (functions.isValueNull(commAddressDetailsVO.getZipCode()))
        {
            zip = commAddressDetailsVO.getZipCode();
        }
        else{
            zip="12047";
        }

        if (functions.isValueNull(commAddressDetailsVO.getPhone()))
        {
            phone = commAddressDetailsVO.getPhone();
            if(phone.contains("+"))
                phone=phone.replaceAll("\\+","");
        }
        else{
            phone="5182373850";
        }

        if (functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
        {
            ip = commAddressDetailsVO.getCardHolderIpAddress();
        }
        else{
            ip="192.168.146.51";
        }


        try
        {
            String first_name = new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);

            String last_name = new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);


            StringBuffer request = new StringBuffer();
            StringBuffer requestlogs = new StringBuffer();


            request.append("{" +
                    "\"first_name\":\"" + first_name + "\"," +
                    "\"last_name\":\"" + last_name + "\"," +
                    "\"email\":\"" + commAddressDetailsVO.getEmail() + "\"," +
                    "\"billing_address1\":\"" + street + "\"," +
                    "\"billing_country\":\"" + country + "\"," +
                    "\"billing_state\":\"" + state + "\"," +
                    "\"billing_city\":\"" + city + "\"," +
                    "\"billing_zip\":\"" + zip + "\"," +
                    "\"mobile\":\"" + phone + "\"," +
                    "\"customer_ip\":\"" + ip + "\"," +
                    "\"billing_address2\":\"\"," +
                    "\"card_no\":\"" + commCardDetailsVO.getCardNum() + "\"," +
                    "\"expiry_month\":\"" + commCardDetailsVO.getExpMonth() + "\"," +
                    "\"expiry_year\":\"" + commCardDetailsVO.getExpYear() + "\"," +
                    "\"card_cvv\":\"" + commCardDetailsVO.getcVV() + "\"," +
                    "\"order_description\":\"" + transactionDetailsVO.getOrderId() + "\"," +
                    "\"amount\":\"" + transactionDetailsVO.getAmount() + "\"" +
                    "}");

            requestlogs.append("{" +
                    "\"first_name\":\"" + first_name + "\"," +
                    "\"last_name\":\"" + last_name + "\"," +
                    "\"email\":\"" + commAddressDetailsVO.getEmail() + "\"," +
                    "\"billing_address1\":\"" + street + "\"," +
                    "\"billing_country\":\"" + country + "\"," +
                    "\"billing_state\":\"" + state + "\"," +
                    "\"billing_city\":\"" + city + "\"," +
                    "\"billing_zip\":\"" + zip + "\"," +
                    "\"mobile\":\"" + phone + "\"," +
                    "\"customer_ip\":\"" + ip + "\"," +
                    "\"billing_address2\":\"\"," +
                    "\"card_no\":\"" + functions.maskingPan(commCardDetailsVO.getCardNum()) + "\"," +
                    "\"expiry_month\":\"" + functions.maskingNumber(commCardDetailsVO.getExpMonth()) + "\"," +
                    "\"expiry_year\":\"" + functions.maskingNumber(commCardDetailsVO.getExpYear()) + "\"," +
                    "\"card_cvv\":\"" + functions.maskingNumber(commCardDetailsVO.getcVV()) + "\"," +
                    "\"order_description\":\"" + transactionDetailsVO.getOrderId() + "\"," +
                    "\"amount\":\"" + transactionDetailsVO.getAmount() + "\"" +
                    "}");


            String Response = "";
            transactionLogger.error("Knox salerequest:::::::for--" + trackingID + "--" + requestlogs.toString());
            Response = KnoxUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_CC_URL"), Key, SecretKey, request.toString());

            transactionLogger.error("Knox saleResponse:::::::for--" + trackingID + "--" + Response);

            if (functions.isValueNull(Response) && Response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(Response);
                String Paymentid = "";
                String Status = "";
                String error = "";
                String message = "";
                String Description = "";
                String Remark = "";

                if (jsonObject != null)
                {
                    if (jsonObject.has("transaction_id"))
                    {
                        Paymentid = jsonObject.getString("transaction_id");
                    }
                    if (jsonObject.has("error"))
                    {
                        error = jsonObject.getString("error");
                    }

                    if (jsonObject.has("status"))
                    {
                        Status = jsonObject.getString("status");
                    }

                    if (jsonObject.has("descriptor"))
                    {
                        Descriptor = jsonObject.getString("descriptor");
                    }



                    if (jsonObject.has("errors"))
                    {
                        JSONObject questionMark = jsonObject.getJSONObject("errors");
                        Iterator keys = questionMark.keys();
                        while (keys.hasNext())
                        {
                            String currentDynamicKey = (String) keys.next();
                            JSONArray currentDynamicValue = questionMark.getJSONArray(currentDynamicKey);

                            for (int i = 0; i < currentDynamicValue.length(); i++)
                            {
                                if(functions.isValueNull(error))
                                    error += ","+currentDynamicValue.get(i);
                                else
                                    error += currentDynamicValue.get(i);
                            }

                        }
                    }

                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                    }

                    if ("success".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("success");
                        Description = "Transaction Successful";
                        Remark = "Transaction Successful";
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        if(functions.isValueNull(error))
                        {
                            Description = error;
                            Remark = error;
                        }else if(functions.isValueNull(message))
                        {
                            Description = message;
                            Remark = message;
                        }else
                        {
                            Description = "Transaction failed";
                            Remark = "Transaction failed";
                        }
                    }

                    commResponseVO.setTransactionId(Paymentid);
                    commResponseVO.setDescription(Description);
                    commResponseVO.setRemark(Remark);
                    //commResponseVO.setResponseTime(Responsedate);
                    commResponseVO.setCurrency(Currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setDescriptor(Descriptor);

                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error(" Knox processSale Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("knox:: inside processRefund()");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        KnoxUtils KnoxUtils = new KnoxUtils();
        String Key = gatewayAccount.getMerchantId();
        String SecretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();


        String paymentid = transactionDetailsVO.getPreviousTransactionId();
        String amount = transactionDetailsVO.getAmount();
        String message = "";
        String error = "";
        String status = "";
        transactionLogger.error("knox Refund amount for" + trackingID + "::::::::::" + amount);


        try
        {
            StringBuffer request = new StringBuffer();

            request.append("{" +
                    "\"trans_id\":\"" + paymentid + "\"," +
                    "\"amount\":\"" + amount + "\"" +
                    "}");
            transactionLogger.error("knox Refund request::::::::::" + trackingID + "--" + request.toString());

            transactionLogger.error("knox Refund URL::::::::::" + trackingID + "--" + RB.getString("LIVE_REFUND") + paymentid);


            String Response = "";
            Response = KnoxUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_REFUND"), Key, SecretKey, request.toString());
            transactionLogger.error("knox RefundResponse:::::::for" + trackingID + "--" + Response);

            JSONObject jsonObject = new JSONObject(Response);

            if (jsonObject != null)
            {
                if (jsonObject.has("message"))
                {
                    message = jsonObject.getString("message");
                }
                if (jsonObject.has("error"))
                {
                    error = jsonObject.getString("error");
                }
                if (jsonObject.has("status"))
                {
                    status = jsonObject.getString("status");
                }

                if ("success".equalsIgnoreCase(status))
                {
                    transactionLogger.error("Refund successfull:::::::for" + trackingID + "--" + amount);
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescription(message);
                    commResponseVO.setRemark(message);
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    transactionLogger.error("Refund failed:::::::for" + trackingID);
                    commResponseVO.setStatus("failed");
                    if(functions.isValueNull(error))
                    {
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                    }else if(functions.isValueNull(message))
                    {
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                    }else
                    {
                        commResponseVO.setDescription("Refund failed");
                        commResponseVO.setRemark("Refund failed");
                    }
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
            transactionLogger.error(" knox Refund Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("knox:: inside processInquiry()");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        KnoxUtils KnoxUtils = new KnoxUtils();
        String Key = gatewayAccount.getMerchantId();
        String SecretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String paymentid = transactionDetailsVO.getPreviousTransactionId();
        String amount = transactionDetailsVO.getAmount();

        transactionLogger.error("knox Key::::::::::for" + paymentid + "--" + Key + "knox SecretKey::::::::::" + SecretKey);


        String merchantid = gatewayAccount.getMerchantId();
        String currency = "";
        if (functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            currency = transactionDetailsVO.getCurrency();
        }


        try
        {
            StringBuffer request = new StringBuffer();

            request.append("{" +
                    "\"trans_id\":\"" + paymentid + "\"" +
                    "}");

            transactionLogger.error("knox InquiryRequest::::::::::for" + paymentid + "--" + request.toString());
            transactionLogger.error("knox Inquiry URL::::::::::for" + paymentid + "--" + RB.getString("LIVE_INQUIRY"));


            String Response = "";
            Response = KnoxUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_INQUIRY"), Key, SecretKey, request.toString());
            transactionLogger.error("knox Inquiry:::::::Response for" + paymentid + "--" + Response);
            if (functions.isValueNull(Response) && Response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(Response);
                String Paymentid = "";
                String Status = "";
                String Responsedate = "";
                String Description = "";

                if (jsonObject != null)
                {
                    if (jsonObject.has("result"))
                    {

                        Object o = jsonObject.get("result");
                        if (o instanceof JSONArray)
                        {
                            JSONArray data = jsonObject.getJSONArray("result");
                            if (data.length() > 0)
                            {
                                JSONObject json = (JSONObject) data.get(0);
                                if (paymentid.equals(json.getString("trans_id")))
                                {
                                    if (json.has("trans_id"))
                                    {
                                        Paymentid = json.getString("trans_id");
                                    }

                                    if (json.has("status"))
                                    {
                                        Status = json.getString("status");
                                    }

                                    if (json.has("date"))
                                    {
                                        Responsedate = json.getString("date");
                                    }

                                    if (json.has("amount"))
                                    {
                                        amount = json.getString("amount");
                                    }
                                }
                            }
                        }
                    }

                    transactionLogger.error(" knox Inquiry Status--for--" + Status + "--");


                    if ("success".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setDescription("Transaction Successful");
                    }
                    else
                    {
                        commResponseVO.setStatus(Status);
                        commResponseVO.setTransactionStatus(Status);
                        commResponseVO.setRemark(Status);
                        commResponseVO.setDescription(Status);
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
                commResponseVO.setTransactionId(Paymentid);
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
            transactionLogger.error(" Knox Inquiry Exception for paymentid" + paymentid + "--", e);
        }
        return commResponseVO;
    }


}
