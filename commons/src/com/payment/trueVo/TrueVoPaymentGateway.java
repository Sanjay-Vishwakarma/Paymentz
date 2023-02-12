    package com.payment.trueVo;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.npayon.NPayOnResponseVO;
import com.payment.npayon.Parameter;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Balaji on 13-Nov-19.
 */


public class TrueVoPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "truevo";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.truevo");
    private static TransactionLogger transactionLogger = new TransactionLogger(TrueVoPaymentGateway.class.getName());

    public TrueVoPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("TrueVoPaymentGateway:: inside processSale()");
        String reqType = "DB";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        TrueVoUtils trueVoUtils= new TrueVoUtils();
        Functions functions = new Functions();

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        String termUrl = "";
        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from RB HOST_URL----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB TERM_URL----"+termUrl);
        }

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        transactionLogger.error("TrueVoPaymentGateway:: merchantId(entity id) ---"+merchantId);
        String authorizationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionLogger.debug("TrueVoPaymentGateway:: authorizationToken ---" + authorizationToken);

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String bankTransactionId = "";
        String time = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency=transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=addressDetailsVO.getTmpl_currency();
        }

        String transactionCurrency = transDetailsVO.getCurrency();
        String transactionAmount = transDetailsVO.getAmount();
        String cardType = trueVoUtils.getTrueVoCardTypeName(cardDetailsVO.getCardType());

        String country="";
        String customerCity="";
        String customerZipCode="";
        String customerStreet="";
        String hostIPAddress="";
        String customerEmail="";
        String lastName="";
        String firstName="";
        String customerCvv="";
        String customerCardExpiryYear="";
        String customerCardExpiryMonth="";
        String customerCardNumber="";
        String customerState ="";
        String WalletId ="";
        String customer_birthDate ="";
        //String customer_birthDate1="2020/02/20";

        if(functions.isValueNull(addressDetailsVO.getCountry())){
            country =addressDetailsVO.getCountry().substring(0,2).toUpperCase();
        }
        if(functions.isValueNull(addressDetailsVO.getCity())){
            customerCity =addressDetailsVO.getCity();
        }
        if(functions.isValueNull(addressDetailsVO.getZipCode())){
            customerZipCode =addressDetailsVO.getZipCode();
        }
        if(functions.isValueNull(addressDetailsVO.getState())){
            customerState =addressDetailsVO.getState();
        }
        if(functions.isValueNull(addressDetailsVO.getStreet())){
            customerStreet =addressDetailsVO.getStreet();
        }
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
          hostIPAddress =addressDetailsVO.getCardHolderIpAddress();
        }
        if(functions.isValueNull(addressDetailsVO.getEmail())){
            customerEmail =addressDetailsVO.getEmail();
        }
        if(functions.isValueNull(addressDetailsVO.getLastname())){
            lastName =addressDetailsVO.getLastname();
        }
        if(functions.isValueNull(addressDetailsVO.getFirstname())){
            firstName =addressDetailsVO.getFirstname();
        }
        if(functions.isValueNull(cardDetailsVO.getcVV())){
            customerCvv =cardDetailsVO.getcVV();
        }
        if(functions.isValueNull(cardDetailsVO.getExpYear())){
            customerCardExpiryYear =cardDetailsVO.getExpYear();
        }
        if(functions.isValueNull(cardDetailsVO.getExpMonth())){
            customerCardExpiryMonth =cardDetailsVO.getExpMonth();
        }
        if(functions.isValueNull(cardDetailsVO.getCardNum())){
            customerCardNumber =cardDetailsVO.getCardNum();
        }
      transactionLogger.error("addressDetailsVO.getBirthdate----------------------------->"+addressDetailsVO.getBirthdate());
        if(functions.isValueNull(addressDetailsVO.getBirthdate()))
        {
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                if (!addressDetailsVO.getBirthdate().contains("-"))
                {
                    customer_birthDate = dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));

                }
                else
                {
                    customer_birthDate=addressDetailsVO.getBirthdate();
                }
            }catch (ParseException e)
            {
                customer_birthDate=addressDetailsVO.getBirthdate();

                transactionLogger.error("Parse Execption-->",e);
            }
        }

        if(functions.isValueNull(addressDetailsVO.getCustomerid())){
            WalletId =addressDetailsVO.getCustomerid();
        }
        transactionLogger.error("customer_birthDate--------------------------------->"+customer_birthDate);
        transactionLogger.error("WalletId--------------------------------->"+WalletId);


        try
        {
            String saleRequest = ""
                    + "authentication.entityId=" + merchantId
                    + "&amount=" + transactionAmount
                    + "&currency=" + transactionCurrency
                    + "&paymentBrand=" + cardType
                    + "&paymentType=" + reqType
                    + "&merchantTransactionId=" + trackingID
                    + "&card.number=" + customerCardNumber
                    + "&card.holder=" + firstName+" "+lastName
                    + "&card.expiryMonth=" + customerCardExpiryMonth
                    + "&card.expiryYear=" + customerCardExpiryYear
                    + "&card.cvv=" + customerCvv
                    + "&customer.givenName=" + firstName
                    + "&customer.surname=" + lastName
                    + "&customer.email=" + customerEmail
                    + "&customer.ip=" + hostIPAddress
                    + "&billing.city=" + customerCity
                    + "&billing.street1=" + customerStreet
                    + "&billing.state=" + customerState
                    + "&billing.postcode=" + customerZipCode
                    + "&billing.country=" + country
                    + "&customer.birthDate=" + customer_birthDate
                    + "&customParameters[WalletId]=" + WalletId
                    + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID + "", "UTF-8");

            String saleRequestLog = ""
                    + "authentication.entityId=" + merchantId
                    + "&amount=" + transactionAmount
                    + "&currency=" + transactionCurrency
                    + "&paymentBrand=" + cardType
                    + "&paymentType=" + reqType
                    + "&merchantTransactionId=" + trackingID
                    + "&card.number=" + functions.maskingPan(customerCardNumber)
                    + "&card.holder=" + firstName+" "+lastName
                    + "&card.expiryMonth=" + functions.maskingNumber(customerCardExpiryMonth)
                    + "&card.expiryYear=" + functions.maskingNumber(customerCardExpiryYear)
                    + "&card.cvv=" + functions.maskingNumber(customerCvv)
                    + "&customer.givenName=" + firstName
                    + "&customer.surname=" + lastName
                    + "&customer.email=" + customerEmail
                    + "&customer.ip=" + hostIPAddress
                    + "&billing.city=" + customerCity
                    + "&billing.street1=" + customerStreet
                    + "&billing.state=" + customerState
                    + "&billing.postcode=" + customerZipCode
                    + "&billing.country=" + country
                    + "&customer.birthDate=" + customer_birthDate
                    + "&customParameters[WalletId]=" + WalletId
                    + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID + "", "UTF-8");

            transactionLogger.error("TrueVoPaymentGateway:: saleRequest --- for "+trackingID+ "----"+saleRequestLog);

            if (isTest)
            {
                transactionLogger.error("inside isTest-----for "+trackingID+ "----" + RB.getString("TEST_URL"));
                response = trueVoUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),authorizationToken,saleRequest);
            }
            else
            {
                transactionLogger.error("inside isLive-----for "+trackingID+ "----" + RB.getString("LIVE_URL"));
                response = trueVoUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),authorizationToken,saleRequest);
            }

            transactionLogger.error("TrueVoPaymentGateway:: sale response ---for "+trackingID+ "----"+response);

            NPayOnResponseVO responseVO = null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);

            if (responseVO != null)
            {
                transactionLogger.error("code:" + responseVO.getResult().getCode());
                if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    descriptor = gatewayAccount.getDisplayName();
                }
                else if ("000.200.000".equalsIgnoreCase(responseVO.getResult().getCode()))
                {
                    transactionLogger.error("inside 3d");
                    String MD = "";
                    String URL = "";
                    String PaReq = "";
                    String TermUrl = "";
                    String connector = "";

                    status = "pending3DConfirmation";

                    URL = responseVO.getRedirect().getUrl();
                    List<Parameter> parameters = responseVO.getRedirect().getParameters();
                    for (Parameter parameter : parameters)
                    {
                        String name = parameter.getName();
                        String value = parameter.getValue();

                        if ("PaReq".equals(name))
                        {
                            PaReq = value;
                        }
                        else if ("MD".equals(name))
                        {
                            MD = value;
                        }
                        else if ("TermUrl".equals(name))
                        {
                            TermUrl = value;
                        }
                        else if ("connector".equals(name))
                        {
                            connector = value;
                        }
                    }

                    transactionLogger.debug("TermUrl------"+TermUrl);
                    Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
                    comm3DResponseVO.setPaReq(PaReq);
                    comm3DResponseVO.setUrlFor3DRedirect(URL);
                    comm3DResponseVO.setMd(MD);
                    comm3DResponseVO.setConnector(connector);
                    comm3DResponseVO.setRedirectMethod("POST");
                    comm3DResponseVO.setTerURL(TermUrl);
                    comm3DResponseVO.setStatus(status);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    comm3DResponseVO.setTransactionType("Sale");
                    comm3DResponseVO.setTransactionId(responseVO.getId());
                    comm3DResponseVO.setDescription(responseVO.getResult().getDescription().replaceAll("'", " "));
                    comm3DResponseVO.setRemark(responseVO.getResult().getDescription().replaceAll("'", " "));
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    transactionLogger.error("redirecting for 3d");
                    return comm3DResponseVO;
                }
                else
                {
                    status = "failed";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                }
                bankTransactionId = responseVO.getId();
                time = responseVO.getTimestamp();
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setTransactionId(bankTransactionId);
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(time);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("Exception while connecting with bank for "+trackingID+ "----", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("TrueVoPaymentGateway:: processAuthentication ---This Functionality is not supported by processing gateway");
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint(TrueVoPaymentGateway.class.getName(), "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        String reqType = "RF";
        NPayOnResponseVO responseVO = null;
        TrueVoUtils trueVoUtils = new TrueVoUtils();

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions=new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String authorizationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String url="";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        String previousTransactionId=commTransactionDetailsVO.getPreviousTransactionId();
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=addressDetailsVO.getTmpl_currency();
        }
        try
        {
            String request =
                            "authentication.entityId=" + merchantId +
                            "&amount=" + commTransactionDetailsVO.getAmount() +
                            "&currency=" + commTransactionDetailsVO.getCurrency() +
                            "&paymentType=" + reqType;

            transactionLogger.error("TrueVoPaymentGateway :: refund request-----for "+trackingID+ "----" + request);

            if (isTest)
            {
                transactionLogger.error("TrueVoPaymentGateway :: inside isTest -----for "+trackingID+ "----" + RB.getString("TEST_URL")+"/"+  previousTransactionId);
                response = trueVoUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL")+"/"+  previousTransactionId, authorizationToken, request);
            }
            else
            {
                transactionLogger.error("TrueVoPaymentGateway :: inside isTest -----for "+trackingID+ "----" + RB.getString("LIVE_URL")+"/"+  previousTransactionId);
                response = trueVoUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL")+"/"+ previousTransactionId, authorizationToken, request);
            }
            transactionLogger.error("TrueVoPaymentGateway :: refund response-----for "+trackingID+ "----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
            if (responseVO != null)
            {
                if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    descriptor = gatewayAccount.getDisplayName();
                }
                else
                {
                    status = "failed";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                }
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {

        transactionLogger.error("TrueVoPaymentGateway :: inside processInquiry()");
        Functions functions=new Functions();
        TrueVoUtils trueVoUtils=new TrueVoUtils();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String authenticationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String transactionStatus = "";

        String previousTransactionId=commTransactionDetailsVO.getPreviousTransactionId();

        if (functions.isValueNull(commTransactionDetailsVO.getOrderId()))
        {
            HashMap<String,String> hashMapDetails=trueVoUtils.getTransactionStatus(commTransactionDetailsVO.getOrderId());
            transactionStatus= hashMapDetails.get("dbStatus");
           // transactionTime= hashMapDetails.get("transactionTime");
        }
        try
        {

            if (isTest)
            {
                String testUrl=RB.getString("TEST_URL") + "/" + previousTransactionId+ "?entityId=" + merchantId;
                transactionLogger.error("Test url----- "+testUrl);
                response = trueVoUtils.doHttpPostConnection(testUrl, authenticationToken);
            }
            else
            {
                String liveUrl=RB.getString("LIVE_URL") + "/" + previousTransactionId+ "?entityId=" + merchantId;
                transactionLogger.error("liveUrl----- "+liveUrl);
                response = trueVoUtils.doHttpPostConnection(liveUrl, authenticationToken);
            }
            transactionLogger.error("TrueVoPaymentGateway :: inquiry response-----" + response);
            NPayOnResponseVO nPayOnResponseVO = null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            nPayOnResponseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
            String eci="";
            if (nPayOnResponseVO != null)
            {
                if ("000.000.000".equals(nPayOnResponseVO.getResult().getCode()) || "000.000.100".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.110".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.111".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.112".equals(nPayOnResponseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                    descriptor = gatewayAccount.getDisplayName();
                }
                else
                {
                    status = "failed";
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                }
                if(nPayOnResponseVO.getThreeDSecure()!=null){
                    eci=nPayOnResponseVO.getThreeDSecure().getEci();
                }
            }
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setMerchantId(merchantId);
            commResponseVO.setMerchantOrderId(commTransactionDetailsVO.getOrderId());
            commResponseVO.setTransactionId(nPayOnResponseVO.getId());
            commResponseVO.setAuthCode("-");
            commResponseVO.setTransactionType(transactionStatus);
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setStatus(status);
            commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setEci(eci);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescriptor(descriptor);

        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TrueVoPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
