package com.payment.clearsettle;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 24-Jul-18.
 */
public class ClearSettleHPPGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "clrshpp";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.clearsettle");
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettleHPPGateway.class.getName());
    private static Functions functions = new Functions();

    public ClearSettleHPPGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new Comm3DResponseVO();
        ClearSettleUtills clearSettleUtills = new ClearSettleUtills();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        SimpleDateFormat inputDate = new SimpleDateFormat("yyyymmdd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        boolean isTest = gatewayAccount.isTest();
        String apiKey = gatewayAccount.getMerchantId();

        String status = "";
        String remark="";
        String descriptor = "";
        String response = "";
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

        try
        {
            String birthDate = formatter.format(inputDate.parse(addressDetailsVO.getBirthdate()));
            String requestData1="";
            requestData1="amount="+clearSettleUtills.getCentAmount(transDetailsVO.getAmount())+
                    "&apiKey="+apiKey+
                    "&currency="+transDetailsVO.getCurrency()+
                    "&email="+addressDetailsVO.getEmail()+
                    "&referenceNo="+trackingID +
                    "&returnUrl="+RB.getString("CLEAR_SETTLE_HPP_FRONTEND")+
                    "&billingFirstName="+addressDetailsVO.getFirstname()+
                    "&billingLastName="+addressDetailsVO.getLastname()+
                    "&billingAddress1="+addressDetailsVO.getStreet()+
                    "&billingCity="+addressDetailsVO.getCity()+
                    "&billingCountry="+addressDetailsVO.getCountry()+
                    "&billingPostcode="+addressDetailsVO.getZipCode()+
                    "&birthday="+birthDate+
                    "&paymentMethod=CREDITCARD";

            transactionLogger.error("-----sale request-----" + requestData1);
            if (isTest){
                response = clearSettleUtills.doPostHTTPSURLConnectionFromData(RB.getString("TEST_HPP_PURCHASE_URL"), requestData1);
            }
            else{
                response = clearSettleUtills.doPostHTTPSURLConnectionFromData(RB.getString("LIVE_HPP_PURCHASE_URL"), requestData1);
            }
            transactionLogger.error("-----sale response-----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClearSettleResponseVO clearSettleResponseVO = objectMapper.readValue(response, ClearSettleResponseVO.class);
            if(clearSettleResponseVO!=null){
                if("283".equals(clearSettleResponseVO.getCode()))
                {
                    if ("PENDING".equals(clearSettleResponseVO.getStatus())){
                        status = "pending3DConfirmation";
                        commResponseVO.setStatus(status);
                        transactionLogger.debug("purchase url :::::" + clearSettleResponseVO.getPurchaseUrl());
                        commResponseVO.setRedirectUrl(URLDecoder.decode(clearSettleResponseVO.getPurchaseUrl()));
                        if (functions.isValueNull(clearSettleResponseVO.getDescriptor())){
                            descriptor = clearSettleResponseVO.getDescriptor();
                        }
                        else{
                            descriptor=gatewayAccount.getDisplayName();
                        }
                    }else{
                        status = "failed";
                        remark=clearSettleResponseVO.getMessage();
                    }
                }
                else{
                    status = "failed";
                    remark=clearSettleResponseVO.getMessage();
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            commResponseVO.setResponseTime(clearSettleResponseVO.getDate());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleHPPGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleHPPGateway.class.getName(), "processSale()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleHPPGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettleHPPGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        String html = "";
        ClearSettleUtills clearSettleUtills=new ClearSettleUtills();
        html = clearSettleUtills.generateAutoSubmitForm(commonValidatorVO);
        return html;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

}
