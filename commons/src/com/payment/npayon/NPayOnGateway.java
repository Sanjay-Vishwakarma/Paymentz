package com.payment.npayon;

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
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.rsp.RSPUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Sandip on 6/19/2018.
 */
public class NPayOnGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "npayon";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.npayon");
    private static TransactionLogger transactionLogger = new TransactionLogger(NPayOnGateway.class.getName());

    public NPayOnGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        try
        {
            String url = "https://test.oppwa.com/v1/payments/8a82944a6424257201642c3e008505ac" +
                    "?authentication.userId=8a8294184efaa37f014efbcd8a8300f1" +
                    "&authentication.password=2KTjJSZp" +
                    "&authentication.entityId=8a82941763da0a720163de4f11b1174f";

            String response = NPayOnUtils.doGetHTTPSURLConnectionClient(url);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ::::::::: ", e);
        }
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String reqType = "DB";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        NPayOnUtils nPayOnUtils = new NPayOnUtils();
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
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL_3DS");
            transactionLogger.error("from RB----"+termUrl);
        }

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String bankTransactionId = "";
        String time = "";
        String url="";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        String country="";
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
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry().substring(0,2);
        String transactionCurrency = transDetailsVO.getCurrency();
        String transactionAmount = transDetailsVO.getAmount();
        String cardType = nPayOnUtils.getNPayOnCardTypeName(cardDetailsVO.getCardType());

        try
        {
            String saleRequest = ""
                    + "authentication.userId=" + merchantId
                    + "&authentication.password=" + password
                    + "&authentication.entityId=" + userName
                    + "&amount=" + transactionAmount
                    + "&currency=" + transactionCurrency
                    + "&paymentBrand=" + cardType
                    + "&paymentType=" + reqType
                    + "&merchantTransactionId=" + trackingID
                    + "&card.number=" + cardDetailsVO.getCardNum()
                    + "&card.holder=" + addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()
                    + "&card.expiryMonth=" + cardDetailsVO.getExpMonth()
                    + "&card.expiryYear=" + cardDetailsVO.getExpYear()
                    + "&card.cvv=" + cardDetailsVO.getcVV()
                    + "&customer.givenName=" + addressDetailsVO.getFirstname()
                    + "&customer.surname=" + addressDetailsVO.getLastname()
                    + "&customer.email=" + addressDetailsVO.getEmail()
                    + "&customer.ip=" + addressDetailsVO.getCardHolderIpAddress()
                    + "&billing.city=" + addressDetailsVO.getCity()
                    + "&billing.street1=" + addressDetailsVO.getStreet()
                    + "&billing.state=" + addressDetailsVO.getState()
                    + "&billing.postcode=" + addressDetailsVO.getZipCode()
                    + "&billing.country=" + country
                    + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID + "", "UTF-8");

            transactionLogger.error("-----sale request-----" + saleRequest);
            if (isTest)
            {
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), saleRequest);
            }
            else
            {
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), saleRequest);
            }
            transactionLogger.error("----- sale response-----" + response);

            NPayOnResponseVO nPayOnResponseVO = null;
            Redirect nPayOnRedirect=null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            nPayOnResponseVO = objectMapper.readValue(response, NPayOnResponseVO.class);

            if (nPayOnResponseVO != null)
            {
                transactionLogger.error("code:" + nPayOnResponseVO.getResult().getCode());
                if ("000.000.000".equals(nPayOnResponseVO.getResult().getCode()) || "000.000.100".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.110".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.111".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.112".equals(nPayOnResponseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                    descriptor = gatewayAccount.getDisplayName();
                }
                else if ("000.200.000".equalsIgnoreCase(nPayOnResponseVO.getResult().getCode()))
                {
                    transactionLogger.error("inside 3d");
                    String MD = "";
                    String URL = "";
                    String PaReq = "";
                    String TermUrl = "";
                    String connector = "";

                    status = "pending3DConfirmation";

                    URL = nPayOnResponseVO.getRedirect().getUrl();
                    List<Parameter> parameters = nPayOnResponseVO.getRedirect().getParameters();
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
                    comm3DResponseVO.setTransactionId(nPayOnResponseVO.getId());
                    comm3DResponseVO.setDescription(nPayOnResponseVO.getResult().getDescription().replaceAll("'", " "));
                    comm3DResponseVO.setRemark(nPayOnResponseVO.getResult().getDescription().replaceAll("'", " "));
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
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                }
                bankTransactionId = nPayOnResponseVO.getId();
                time = nPayOnResponseVO.getTimestamp();
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
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String reqType = "PA";
        Functions functions=new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        NPayOnUtils nPayOnUtils = new NPayOnUtils();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        String termUrl = "";
        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL_3DS");
            transactionLogger.error("from RB----"+termUrl);
        }

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String bankTransactionId = "";
        String time = "";

        String transactionCurrency = transDetailsVO.getCurrency();
        String transactionAmount = transDetailsVO.getAmount();
        String cardType = nPayOnUtils.getNPayOnCardTypeName(cardDetailsVO.getCardType());
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        String country="";
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
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry().substring(0,2);
        try
        {
            String saleRequest = ""
                    + "authentication.userId=" + merchantId
                    + "&authentication.password=" + password
                    + "&authentication.entityId=" + userName
                    + "&amount=" + transactionAmount
                    + "&currency=" + transactionCurrency
                    + "&paymentBrand=" + cardType
                    + "&paymentType=" + reqType
                    + "&merchantTransactionId=" + trackingID
                    + "&card.number=" + cardDetailsVO.getCardNum()
                    + "&card.holder=" + addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()
                    + "&card.expiryMonth=" + cardDetailsVO.getExpMonth()
                    + "&card.expiryYear=" + cardDetailsVO.getExpYear()
                    + "&card.cvv=" + cardDetailsVO.getcVV()
                    + "&customer.givenName=" + addressDetailsVO.getFirstname()
                    + "&customer.surname=" + addressDetailsVO.getLastname()
                    + "&customer.email=" + addressDetailsVO.getEmail()
                    + "&customer.ip=" + addressDetailsVO.getCardHolderIpAddress()
                    + "&billing.city=" + addressDetailsVO.getCity()
                    + "&billing.street1=" + addressDetailsVO.getStreet()
                    + "&billing.state=" + addressDetailsVO.getState()
                    + "&billing.postcode=" + addressDetailsVO.getZipCode()
                    + "&billing.country=" + country
                    + "&shopperResultUrl=" + URLEncoder.encode(termUrl +trackingID + "", "UTF-8");

            transactionLogger.error("-----sale request-----" + saleRequest);
            if (isTest)
            {
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), saleRequest);
            }
            else
            {
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), saleRequest);
            }
            transactionLogger.error("----- sale response-----" + response);

            NPayOnResponseVO nPayOnResponseVO = null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            nPayOnResponseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
            if (nPayOnResponseVO != null)
            {
                transactionLogger.error("code:" + nPayOnResponseVO.getResult().getCode());
                if ("000.000.000".equals(nPayOnResponseVO.getResult().getCode()) || "000.000.100".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.110".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.111".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.112".equals(nPayOnResponseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                    descriptor =gatewayAccount.getDisplayName();
                }
                else if ("000.200.000".equalsIgnoreCase(nPayOnResponseVO.getResult().getCode()))
                {
                    transactionLogger.error("inside 3d");
                    String MD = "";
                    String URL = "";
                    String PaReq = "";
                    String TermUrl = "";
                    String connector = "";

                    status = "pending3DConfirmation";

                    URL = nPayOnResponseVO.getRedirect().getUrl();
                    List<Parameter> parameters = nPayOnResponseVO.getRedirect().getParameters();
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

                    transactionLogger.debug("TermUrl-----"+TermUrl);
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
                    comm3DResponseVO.setTransactionId(nPayOnResponseVO.getId());
                    comm3DResponseVO.setDescription(nPayOnResponseVO.getResult().getDescription().replaceAll("'", " "));
                    comm3DResponseVO.setRemark(nPayOnResponseVO.getResult().getDescription().replaceAll("'", " "));

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
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                }
                bankTransactionId = nPayOnResponseVO.getId();
                time = nPayOnResponseVO.getTimestamp();
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
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        String reqType = "CP";
        NPayOnUtils nPayOnUtils = new NPayOnUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions=new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
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
                    "authentication.userId=" + merchantId +
                            "&authentication.entityId=" + userName +
                            "&authentication.password=" + password +
                            "&amount=" + commTransactionDetailsVO.getAmount() +
                            "&currency=" + commTransactionDetailsVO.getCurrency() +
                            "&paymentType=" + reqType;

            transactionLogger.error("-----capture request-----" + request);
            if (isTest)
            {
                String url = RB.getString("TEST_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId();
                transactionLogger.error("url::::::" + url);
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(url, request);
            }
            else
            {
                String url = RB.getString("LIVE_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId();
                transactionLogger.error("url::::::" + url);
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(url, request);
            }
            transactionLogger.error("-----capture response-----" + response);

            NPayOnResponseVO nPayOnResponseVO = null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            nPayOnResponseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
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
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        String reqType = "RF";
        NPayOnResponseVO nPayOnResponseVO = null;

        NPayOnUtils nPayOnUtils = new NPayOnUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions=new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
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
                    "authentication.userId=" + merchantId +
                            "&authentication.entityId=" + userName +
                            "&authentication.password=" + password +
                            "&amount=" + commTransactionDetailsVO.getAmount() +
                            "&currency=" + commTransactionDetailsVO.getCurrency() +
                            "&paymentType=" + reqType;

            transactionLogger.error("-----refund request-----" + request);
            if (isTest)
            {
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId(), request);
            }
            else
            {
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId(), request);
            }

            transactionLogger.error("-----refund response-----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            nPayOnResponseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
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
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        String reqType = "RV";
        NPayOnUtils nPayOnUtils = new NPayOnUtils();
        Functions functions=new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        try
        {
            String request =
                    "authentication.userId=" + merchantId +
                            "&authentication.entityId=" + userName +
                            "&authentication.password=" + password +
                            "&amount=" + commTransactionDetailsVO.getAmount() +
                            "&currency=" + commTransactionDetailsVO.getCurrency() +
                            "&paymentType=" + reqType;

            transactionLogger.error("-----cancel request-----" + request);
            if (isTest)
            {
                String url = RB.getString("TEST_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId();
                transactionLogger.error("url::::::" + url);
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(url, request);
            }
            else
            {
                String url = RB.getString("LIVE_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId();
                transactionLogger.error("url::::::" + url);
                response = nPayOnUtils.doPostHTTPSURLConnectionClient(url, request);
            }
            transactionLogger.error("-----cancel response-----" + response);
            NPayOnResponseVO nPayOnResponseVO = null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            nPayOnResponseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
            if (nPayOnResponseVO != null)
            {
                if ("000.000.000".equals(nPayOnResponseVO.getResult().getCode()) || "000.000.100".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.110".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.111".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.112".equals(nPayOnResponseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                    descriptor =gatewayAccount.getDisplayName();
                }
                else
                {
                    status = "failed";
                    remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
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
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String reqType = "SEARCH";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        RSPUtils rspUtils = new RSPUtils();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String merchantId = gatewayAccount.getMerchantId();
        String reqAccountId = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();
        boolean isTest = gatewayAccount.isTest();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String requestTime = dateFormat.format(date);

        String status = "";
        String remark = "";
        String responseData = "";
        String hash = "";
        try
        {
            hash = rspUtils.getMD5HashForSale(reqType, merchantId, reqAccountId, userName, password, trackingID + "_" + requestTime, secretKey);
            String requestData =
                    "req_type=" + reqType +
                            "&req_mid=" + merchantId +
                            "&req_accountid=" + reqAccountId +
                            "&req_username=" + userName +
                            "&req_password=" + password +
                            "&req_trackid=" + trackingID + "_" + requestTime +
                            "&req_origtrackid=" + trackingID +
                            "&req_signature=" + hash;

            transactionLogger.error("----process query request-----" + requestData);
            if (isTest)
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }
            else
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            responseData = URLDecoder.decode(responseData);
            transactionLogger.error("-----process query response-----" + responseData);

            HashMap hashMap = rspUtils.readResponse(responseData);
            if ("0".equals(hashMap.get("res_code")) && "Approved".equals(hashMap.get("res_message")))
            {
                status = "success";
                remark = (String) hashMap.get("res_founddescription");
            }
            else
            {
                status = "fail";
                remark = (String) hashMap.get("res_founddescription");
            }

            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setMerchantId(merchantId);
            commResponseVO.setMerchantOrderId((String) hashMap.get("res_trackid"));
            commResponseVO.setTransactionId((String) hashMap.get("res_referenceid"));
            commResponseVO.setAuthCode("-");
            commResponseVO.setTransactionType((String) hashMap.get("res_foundtype"));
            commResponseVO.setTransactionStatus((String) hashMap.get("res_foundmessage"));
            commResponseVO.setStatus(status);
            commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setBankTransactionDate((String) hashMap.get("res_founddatetime"));
            commResponseVO.setErrorCode((String) hashMap.get("res_code"));
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while doing inquiry transaction ", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        NPayOnUtils nPayOnUtils = new NPayOnUtils();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";

        try
        {
           /* String request =
                    "authentication.userId=" + merchantId +
                    "&authentication.entityId=" + userName +
                    "&authentication.password=" + password;*/
            String url = "";

            if (isTest)
            {
                url = RB.getString("TEST_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId();
                transactionLogger.error("url::::::" + url);
                //response = nPayOnUtils.doPostHTTPSURLConnectionClient(url, request);
            }
            else
            {
                url = RB.getString("LIVE_URL") + "/" + commTransactionDetailsVO.getPreviousTransactionId();
                transactionLogger.error("url::::::" + url);
            }

            url = url +
                    "?authentication.userId=" + merchantId + "" +
                    "&authentication.password=" + password + "" +
                    "&authentication.entityId=" + userName + "";

            transactionLogger.error("-----inquiry request-----" + url);
            response = NPayOnUtils.doGetHTTPSURLConnectionClient(url);


            transactionLogger.error("-----inquiry response-----" + response);
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
            commResponseVO.setTransactionType("Inquiry");
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
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPayOnGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}