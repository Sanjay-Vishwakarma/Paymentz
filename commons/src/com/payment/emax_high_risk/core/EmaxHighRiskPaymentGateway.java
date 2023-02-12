package com.payment.emax_high_risk.core;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 1/28/15
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmaxHighRiskPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(EmaxHighRiskPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EmaxHighRiskPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "emax";
    private final static String SALEURL = "https://paymentgateway.emexhighrisk.com/transactions/payments";
    private final static String REFUNDURL = "https://paymentgateway.emexhighrisk.com/transactions/refunds";
    private final static String AUTHURL= "https://paymentgateway.emexhighrisk.com/transactions/authorizations";
    private final static String CAPTUREURL="https://paymentgateway.emexhighrisk.com/transactions/captures";
    private final static String VOIDURL = "https://paymentgateway.emexhighrisk.com/transactions/voids";

    @Override
    public String getMaxWaitDays()
    {
        return "3.5";  //To change body of implemented methods use File | Settings | File Templates.
    }
    public  EmaxHighRiskPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        log.debug("Entering processAuth of EmaxPaymentGateway...");
        transactionLogger.debug("Entering processAuth of EmaxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EmaxResponseVO emaxResponseVO = new EmaxResponseVO();
        Functions functions =new Functions();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        validateForAuthAndSale(trackingID,requestVO);

        String secretKey= GatewayAccountService.getGatewayAccount(accountId).getPassword();//3ecd77a8185edb2825286be42131f635563110a7932f9dbc82948d9d7a7b8035
        String shopId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//332
        log.debug("shopid---"+shopId);
        log.debug("shopkey---"+secretKey);

        try
        {
            String authRequest =
                "{\"request\":" +
                    "{\n" +
                    "\"amount\":"+getAmount(genericTransDetailsVO.getAmount())+",\n" +
                    "\"currency\":\""+genericTransDetailsVO.getCurrency()+"\",\n" +
                    "\"description\":\""+genericTransDetailsVO.getOrderDesc()+"\",\n" +
                    "\"tracking_id\":\""+trackingID+"\",\n" +
                    "\"credit_card\":{\n" +
                    "\"number\":\""+genericCardDetailsVO.getCardNum()+"\",\n" +
                    "\"verification_value\":\""+genericCardDetailsVO.getcVV()+"\",\n" +
                    "\"holder\":\""+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"\",\n" +
                    "\"exp_month\":\""+genericCardDetailsVO.getExpMonth()+"\",\n" +
                    "\"exp_year\":\""+genericCardDetailsVO.getExpYear()+"\"\n" +
                    "},\n" +
                    "\"customer\":{\n" +
                    "\"ip\":\""+addressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                    "\"email\":\""+addressDetailsVO.getEmail().trim()+"\"\n" +
                    "},\n" +
                    "\"billing_address\":{\n" +
                    "\"first_name\":\""+addressDetailsVO.getFirstname()+"\",\n" +
                    "\"last_name\":\""+addressDetailsVO.getLastname()+"\",\n" +
                    "\"country\":\""+addressDetailsVO.getCountry()+"\",\n" +
                    "\"city\":\""+addressDetailsVO.getCity()+"\",\n" +
                    "\"state\":\""+addressDetailsVO.getState()+"\",\n" +
                    "\"zip\":\""+addressDetailsVO.getZipCode()+"\",\n" +
                    "\"address\":\""+addressDetailsVO.getStreet()+"\",\n" +
                    "\"phone\":\""+addressDetailsVO.getPhone()+"\"\n" +
                    "}\n" +
                    "}\n" +
                    "}";
            String authRequestlog =
                    "{\"request\":" +
                            "{\n" +
                            "\"amount\":"+getAmount(genericTransDetailsVO.getAmount())+",\n" +
                            "\"currency\":\""+genericTransDetailsVO.getCurrency()+"\",\n" +
                            "\"description\":\""+genericTransDetailsVO.getOrderDesc()+"\",\n" +
                            "\"tracking_id\":\""+trackingID+"\",\n" +
                            "\"credit_card\":{\n" +
                            "\"number\":\""+functions.maskingPan(genericCardDetailsVO.getCardNum())+"\",\n" +
                            "\"verification_value\":\""+functions.maskingNumber(genericCardDetailsVO.getcVV())+"\",\n" +
                            "\"holder\":\""+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"\",\n" +
                            "\"exp_month\":\""+functions.maskingNumber(genericCardDetailsVO.getExpMonth())+"\",\n" +
                            "\"exp_year\":\""+functions.maskingNumber(genericCardDetailsVO.getExpYear())+"\"\n" +
                            "},\n" +
                            "\"customer\":{\n" +
                            "\"ip\":\""+addressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                            "\"email\":\""+addressDetailsVO.getEmail().trim()+"\"\n" +
                            "},\n" +
                            "\"billing_address\":{\n" +
                            "\"first_name\":\""+addressDetailsVO.getFirstname()+"\",\n" +
                            "\"last_name\":\""+addressDetailsVO.getLastname()+"\",\n" +
                            "\"country\":\""+addressDetailsVO.getCountry()+"\",\n" +
                            "\"city\":\""+addressDetailsVO.getCity()+"\",\n" +
                            "\"state\":\""+addressDetailsVO.getState()+"\",\n" +
                            "\"zip\":\""+addressDetailsVO.getZipCode()+"\",\n" +
                            "\"address\":\""+addressDetailsVO.getStreet()+"\",\n" +
                            "\"phone\":\""+addressDetailsVO.getPhone()+"\"\n" +
                            "}\n" +
                            "}\n" +
                            "}";


            transactionLogger.error("Request data -------------" + trackingID + "--" + authRequestlog);

            String userPassword = shopId + ":" + secretKey;
            String encodedCredentials = new String(Base64.encodeBase64(userPassword.getBytes()));
            String response = EMAXUtills.doPostHTTPSURLConnectionClient(AUTHURL, authRequest,encodedCredentials);

            transactionLogger.error("Response Data -----------" + trackingID + "--" + response);
            String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            RequestData requestData= new RequestData();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            requestData = objectMapper.readValue(response,RequestData.class);

            log.debug("requestData::::::"+requestData.getResponse());

            if(requestData!=null && !requestData.equals(""))
            {
                String status="fail";
                if(requestData.getTransaction()!=null)
                {
                    if("successful".equalsIgnoreCase(requestData.getTransaction().getStatus()))
                    {
                        status="success";
                    }


                emaxResponseVO.setTransactionId(requestData.getTransaction().getUid());
                emaxResponseVO.setRemark(requestData.getTransaction().getAuthorization().getMessage());
                emaxResponseVO.setTransactionType(requestData.getTransaction().getType());
                emaxResponseVO.setDescription(requestData.getTransaction().getMessage());
                emaxResponseVO.setTransactionStatus(requestData.getTransaction().getAuthorization().getStatus());

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                emaxResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                emaxResponseVO.setToken(requestData.getTransaction().getCreditCard().getToken());
                emaxResponseVO.setStamp(requestData.getTransaction().getCreditCard().getStamp());
                String address = requestData.getTransaction().getBillingAddress().getAddress()+","+requestData.getTransaction().getBillingAddress().getCity()+","+requestData.getTransaction().getBillingAddress().getState()+","+requestData.getTransaction().getBillingAddress().getCountry()+","+requestData.getTransaction().getBillingAddress().getZip();
                emaxResponseVO.setAddress_details(address);
                emaxResponseVO.setCard_holder(requestData.getTransaction().getCreditCard().getHolder());
            }
                else
                {
                    emaxResponseVO.setDescription(requestData.getResponse().getMessage());
                }

                emaxResponseVO.setStatus(status);
                emaxResponseVO.setDescriptor(descriptor);
            }
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processAuthentication()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processAuthentication()",null,"common","Technical Parsing while placing transaction",PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processAuthentication()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return emaxResponseVO;
    }
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        log.debug("Entering processCapture of EmaxPaymentGateway...");
        transactionLogger.debug("Entering processCapture of EmaxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EmaxResponseVO emaxResponseVO = new EmaxResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        String secretKey= GatewayAccountService.getGatewayAccount(accountId).getPassword();//3ecd77a8185edb2825286be42131f635563110a7932f9dbc82948d9d7a7b8035
        String shopId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//332
        log.debug("shopid---"+shopId);
        log.debug("shopkey---"+secretKey);

        validationForCaptureVoidRefund(trackingID,commRequestVO);

        try
        {
            String captureReq =
                    "{\"request\":"+
                            "{\n"+
                            "\"parent_uid\":\""+genericTransDetailsVO.getPreviousTransactionId()+"\",\n" +
                            "\"amount\":"+getAmount(genericTransDetailsVO.getAmount())+"\n"+
                            "}\n" +
                            "}";

            log.debug("Request data------------"+captureReq);
            String userPassword = shopId+ ":" +secretKey ;
            String encodedCredentials = new String(Base64.encodeBase64(userPassword.getBytes()));
            String response = EMAXUtills.doPostHTTPSURLConnectionClient(CAPTUREURL,captureReq,encodedCredentials);

            log.debug("Response Data -----------"+response);
            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            RequestData requestData = new RequestData();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            requestData = objectMapper.readValue(response,RequestData.class);

            if(requestData!=null && !requestData.equals(""))
            {
                String status = "fail";
                if(requestData.getTransaction()!=null)
                {
                    if("successful".equalsIgnoreCase(requestData.getTransaction().getStatus()))
                    {
                        status = "success";
                    }

                emaxResponseVO.setDescription(requestData.getTransaction().getMessage());
                emaxResponseVO.setTransactionId(requestData.getTransaction().getUid());
                emaxResponseVO.setTransactionStatus(requestData.getTransaction().getStatus());

                emaxResponseVO.setTransactionType(requestData.getTransaction().getType());
                //commResponseVO.setRemark(requestData.getTransaction().getCapture().getMessage());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                emaxResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                 }
                else
                {
                    emaxResponseVO.setDescription(requestData.getResponse().getMessage());
                }
                emaxResponseVO.setStatus(status);
                emaxResponseVO.setDescriptor(descriptor);
            }
        }


        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processCapture()",null,"common","Io Exception while capturing the transaction",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return emaxResponseVO;
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        log.debug("Entering processSale of EmaxPaymentGateway...");
        transactionLogger.debug("Entering processSale of EmaxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EmaxResponseVO emaxResponseVO = new EmaxResponseVO();
        Functions functions = new Functions();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        validateForAuthAndSale(trackingID,requestVO);

        String secretKey= GatewayAccountService.getGatewayAccount(accountId).getPassword();//3ecd77a8185edb2825286be42131f635563110a7932f9dbc82948d9d7a7b8035
        String shopId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//332
        log.debug("shopid---"+shopId);
        log.debug("shopkey---"+secretKey);
        try
        {
            String saleRequest =
                "{\"request\":" +
                    "{\n" +
                    "\"amount\":"+getAmount(genericTransDetailsVO.getAmount())+",\n" +
                    "\"currency\":\""+genericTransDetailsVO.getCurrency()+"\",\n" +
                    "\"description\":\""+genericTransDetailsVO.getOrderDesc()+"\",\n" +
                    "\"tracking_id\":\""+trackingID+"\",\n" +
                    "\"credit_card\":{\n" +
                    "\"number\":\""+genericCardDetailsVO.getCardNum()+"\",\n" +
                    "\"verification_value\":\""+genericCardDetailsVO.getcVV()+"\",\n" +
                    "\"holder\":\""+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"\",\n" +
                    "\"exp_month\":\""+genericCardDetailsVO.getExpMonth()+"\",\n" +
                    "\"exp_year\":\""+genericCardDetailsVO.getExpYear()+"\"\n" +
                    "},\n" +
                    "\"customer\":{\n" +
                    "\"ip\":\""+addressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                    "\"email\":\""+addressDetailsVO.getEmail().trim()+"\"\n" +
                    "},\n" +
                    "\"billing_address\":{\n" +
                    "\"first_name\":\""+addressDetailsVO.getFirstname()+"\",\n" +
                    "\"last_name\":\""+addressDetailsVO.getLastname()+"\",\n" +
                    "\"country\":\""+addressDetailsVO.getCountry()+"\",\n" +
                    "\"city\":\""+addressDetailsVO.getCity()+"\",\n" +
                    "\"state\":\""+addressDetailsVO.getState()+"\",\n" +
                    "\"zip\":\""+addressDetailsVO.getZipCode()+"\",\n" +
                    "\"address\":\""+addressDetailsVO.getStreet()+"\",\n" +
                    "\"phone\":\""+addressDetailsVO.getPhone()+"\"\n" +
                    "}\n" +
                    "}\n" +
                    "}";
            String saleRequestlog =
                    "{\"request\":" +
                            "{\n" +
                            "\"amount\":"+getAmount(genericTransDetailsVO.getAmount())+",\n" +
                            "\"currency\":\""+genericTransDetailsVO.getCurrency()+"\",\n" +
                            "\"description\":\""+genericTransDetailsVO.getOrderDesc()+"\",\n" +
                            "\"tracking_id\":\""+trackingID+"\",\n" +
                            "\"credit_card\":{\n" +
                            "\"number\":\""+functions.maskingNumber(genericCardDetailsVO.getCardNum())+"\",\n" +
                            "\"verification_value\":\""+functions.maskingNumber(genericCardDetailsVO.getcVV())+"\",\n" +
                            "\"holder\":\""+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"\",\n" +
                            "\"exp_month\":\""+functions.maskingNumber(genericCardDetailsVO.getExpMonth())+"\",\n" +
                            "\"exp_year\":\""+functions.maskingNumber(genericCardDetailsVO.getExpYear())+"\"\n" +
                            "},\n" +
                            "\"customer\":{\n" +
                            "\"ip\":\""+addressDetailsVO.getCardHolderIpAddress()+"\",\n" +
                            "\"email\":\""+addressDetailsVO.getEmail().trim()+"\"\n" +
                            "},\n" +
                            "\"billing_address\":{\n" +
                            "\"first_name\":\""+addressDetailsVO.getFirstname()+"\",\n" +
                            "\"last_name\":\""+addressDetailsVO.getLastname()+"\",\n" +
                            "\"country\":\""+addressDetailsVO.getCountry()+"\",\n" +
                            "\"city\":\""+addressDetailsVO.getCity()+"\",\n" +
                            "\"state\":\""+addressDetailsVO.getState()+"\",\n" +
                            "\"zip\":\""+addressDetailsVO.getZipCode()+"\",\n" +
                            "\"address\":\""+addressDetailsVO.getStreet()+"\",\n" +
                            "\"phone\":\""+addressDetailsVO.getPhone()+"\"\n" +
                            "}\n" +
                            "}\n" +
                            "}";

            transactionLogger.error("request data---"+trackingID + "--"+saleRequestlog);

            String userPassword = shopId + ":" + secretKey;
            String encodedCredentials = new String(Base64.encodeBase64(userPassword.getBytes()));
            String response = EMAXUtills.doPostHTTPSURLConnectionClient(SALEURL, saleRequest,encodedCredentials);

            transactionLogger.error("---response---"+trackingID + "--"+response);
            String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            RequestData requestData= new RequestData();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            requestData = objectMapper.readValue(response,RequestData.class);

            if(requestData!=null && !requestData.equals(""))
            {
                String status = "fail";
                //System.out.println("outside message---"+requestData.getResponse().getMessage());
                if(requestData.getTransaction()!=null)
                {
                    if("successful".equalsIgnoreCase(requestData.getTransaction().getStatus()))
                    {
                        status = "success";
                    }

                emaxResponseVO.setDescription(requestData.getTransaction().getPayment().getMessage());
                emaxResponseVO.setTransactionId(requestData.getTransaction().getUid());
                emaxResponseVO.setRemark(requestData.getTransaction().getMessage());
                emaxResponseVO.setTransactionStatus(requestData.getTransaction().getPayment().getStatus());

                emaxResponseVO.setTransactionType(requestData.getTransaction().getType());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                emaxResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                emaxResponseVO.setToken(requestData.getTransaction().getCreditCard().getToken());
                emaxResponseVO.setStamp(requestData.getTransaction().getCreditCard().getStamp());
                String address = requestData.getTransaction().getBillingAddress().getAddress()+","+requestData.getTransaction().getBillingAddress().getCity()+","+requestData.getTransaction().getBillingAddress().getState()+","+requestData.getTransaction().getBillingAddress().getCountry()+","+requestData.getTransaction().getBillingAddress().getZip();
                emaxResponseVO.setAddress_details(address);
                emaxResponseVO.setCard_holder(requestData.getTransaction().getCreditCard().getHolder());
                }
                else
                {
                    emaxResponseVO.setDescription(requestData.getMessage());
                }
                emaxResponseVO.setStatus(status);
                emaxResponseVO.setDescriptor(descriptor);
            }
        }
        catch (JsonParseException je)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION,null,je.getMessage(),je.getCause());
        }
        catch (JsonMappingException jm)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION,null,jm.getMessage(),jm.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION,null,ie.getMessage(),ie.getCause());
        }
        return emaxResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        log.debug("Entering processRefund of EmaxPaymentGateway...");
        transactionLogger.debug("Entering processRefund of EmaxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EmaxResponseVO emaxResponseVO = new EmaxResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        String secretKey= GatewayAccountService.getGatewayAccount(accountId).getPassword();//3ecd77a8185edb2825286be42131f635563110a7932f9dbc82948d9d7a7b8035
        String shopId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//332

        validationForCaptureVoidRefund(trackingID,commRequestVO);

        try
        {
            String refundReq = "{\n" +
                "\"request\":{\n" +
                "\"parent_uid\":\""+genericTransDetailsVO.getPreviousTransactionId()+"\",\n" +
                "\"amount\":"+getAmount(genericTransDetailsVO.getAmount())+",\n" +
                "\"reason\":\""+genericTransDetailsVO.getOrderDesc()+"\"\n" +
                "}\n" +
                "}";

            String userPassword = shopId + ":" + secretKey;
            String encodedCredentials = new String(Base64.encodeBase64(userPassword.getBytes()));
            String response = EMAXUtills.doPostHTTPSURLConnectionClient(REFUNDURL, refundReq,encodedCredentials);

            log.error("---response---"+response);
            String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            RequestData requestData = new RequestData();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            requestData = objectMapper.readValue(response,RequestData.class);

            if(requestData!=null && !requestData.equals(""))
            {
                String status = "fail";
                if(requestData.getTransaction()!=null)
                {
                     if("successful".equalsIgnoreCase(requestData.getTransaction().getStatus()))
                    {
                        status = "success";
                    }

                emaxResponseVO.setDescription(requestData.getTransaction().getMessage());
                emaxResponseVO.setTransactionId(requestData.getTransaction().getUid());
                emaxResponseVO.setRemark(requestData.getTransaction().getReason());
                emaxResponseVO.setTransactionStatus(requestData.getTransaction().getStatus());

                emaxResponseVO.setTransactionType(requestData.getTransaction().getType());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                emaxResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
                else
                {
                  emaxResponseVO.setDescription(requestData.getResponse().getMessage());
                }
                emaxResponseVO.setStatus(status);
                emaxResponseVO.setDescriptor(descriptor);
            }
        }
        catch (JsonParseException je)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION,null,je.getMessage(),je.getCause());
        }
        catch (JsonMappingException jm)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION,null,jm.getMessage(),jm.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION,null,ie.getMessage(),ie.getCause());
        }
        return emaxResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        EmaxResponseVO emaxResponseVO = new EmaxResponseVO();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        String secretKey= GatewayAccountService.getGatewayAccount(accountId).getPassword();//3ecd77a8185edb2825286be42131f635563110a7932f9dbc82948d9d7a7b8035
        String shopId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//332
        log.debug("shopid---"+shopId);
        log.debug("shopkey---"+secretKey);
        validationForCaptureVoidRefund(trackingID,commRequestVO);
        try{
            String voidRequest =
                "{\"request\":"+
                    "{\n"+
                    "\"parent_uid\":\""+commTransactionDetailsVO.getPreviousTransactionId()+"\",\n" +
                    "\"amount\":"+getAmount(commTransactionDetailsVO.getAmount())+"\n"+
                    "}\n" +
                    "}";

            log.debug("Request data------------"+voidRequest);
            String userPassword = shopId+ ":" +secretKey ;
            String encodedCredentials = new String(Base64.encodeBase64(userPassword.getBytes()));
            String response = EMAXUtills.doPostHTTPSURLConnectionClient(VOIDURL,voidRequest,encodedCredentials);

            log.debug("Response Data -----------"+response);
            String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            RequestData requestData= new RequestData();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            requestData = objectMapper.readValue(response,RequestData.class);

            if(requestData!=null && !requestData.equals(""))
            {
                String status="fail";
                if(requestData.getTransaction()!=null)
                {
                    if("successful".equalsIgnoreCase(requestData.getTransaction().getStatus()))
                    {
                        status = "success";
                    }


                emaxResponseVO.setTransactionId(requestData.getTransaction().getUid());
                emaxResponseVO.setTransactionType(requestData.getTransaction().getType());
                emaxResponseVO.setTransactionStatus(requestData.getTransaction().getStatus());
                emaxResponseVO.setDescription(requestData.getTransaction().getMessage());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                emaxResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
                else
                {
                    emaxResponseVO.setDescription(requestData.getResponse().getMessage());
                }
                emaxResponseVO.setStatus(status);
                emaxResponseVO.setDescriptor(descriptor);
            }

        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processVoid()",null,"common","Technical Exception while placing the transaction",PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processVoid()",null,"common","Technical Exception while placing the transaction",PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EmaxHighRiskPaymentGateway.class.getName(),"processVoid()",null,"common","Technical Exception while placing the transaction",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        return emaxResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        EmaxResponseVO emaxResponseVO = new EmaxResponseVO();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        String emaxResponseId = commTransactionDetailsVO.getPreviousTransactionId();

        String qURL = "https://paymentgateway.emexhighrisk.com/transactions/"+emaxResponseId;

        String secretKey= GatewayAccountService.getGatewayAccount(accountId).getPassword();//3ecd77a8185edb2825286be42131f635563110a7932f9dbc82948d9d7a7b8035
        String shopId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//332
        log.debug("shopid---"+shopId);
        transactionLogger.debug("shopid---"+shopId);
        log.debug("shopkey---"+secretKey);
        transactionLogger.debug("shopkey---"+secretKey);

        String userPassword = shopId+ ":" +secretKey ;
        String encodedCredentials = new String(Base64.encodeBase64(userPassword.getBytes()));

        String response = EMAXUtills.doGetHTTPSURLConnectionClient(qURL,encodedCredentials);

        log.debug("response query---"+response);
        transactionLogger.debug("response query---"+response);


        return emaxResponseVO;
    }
    public String getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    private void validateForAuthAndSale(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;

        GenericCardDetailsVO genericCardDetailsVO=commRequestVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();

        if(genericCardDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Card Details  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Card Details  not provided while placing transaction",new Throwable("Card Details  not provided while placing transaction"));
        }
        if(genericTransDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Transaction Details  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Transaction Details  not provided while placing transaction",new Throwable("Transaction Details  not provided while placing transaction"));
        }
        if(addressDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Address Details  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Address Details  not provided while placing transaction",new Throwable("Address Details  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount()==null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getCurrency()==null || genericTransDetailsVO.getCurrency().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Currency not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getOrderDesc()==null || genericTransDetailsVO.getOrderDesc().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Order Description not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Order Description not provided while placing transaction",new Throwable("Order Description not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }
        if(addressDetailsVO.getFirstname()==null || addressDetailsVO.getFirstname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","First Name not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));
        }
        if(addressDetailsVO.getLastname()==null || addressDetailsVO.getLastname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Last Name not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Last Name not provided while placing transaction",new Throwable("Last Name not provided while placing transaction"));
        }
        if(addressDetailsVO.getCountry()==null || addressDetailsVO.getCountry().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
        }
        if(addressDetailsVO.getCity()==null || addressDetailsVO.getCity().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));
        }
        if(addressDetailsVO.getState()==null || addressDetailsVO.getState().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","State not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"State not provided while placing transaction",new Throwable("State not provided while placing transaction"));
        }
        if(addressDetailsVO.getZipCode()==null || addressDetailsVO.getZipCode().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Zip Code not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Zip Code not provided while placing transaction",new Throwable("Zip Code not provided while placing transaction"));
        }
        if(addressDetailsVO.getStreet()==null || addressDetailsVO.getStreet().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));
        }
        if(addressDetailsVO.getPhone()==null || addressDetailsVO.getPhone().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Phone NO not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
        }
        if(addressDetailsVO.getIp()==null || addressDetailsVO.getIp().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","IP Address not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"IP Address  not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
        }
        if(addressDetailsVO.getEmail()==null || addressDetailsVO.getEmail().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validateForAuthAndSale()",null,"common","Email ID not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Email ID not provided while placing transaction",new Throwable("Email Id not provided while placing transaction"));
        }

    }

    private void validationForCaptureVoidRefund(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        if(genericTransDetailsVO == null)
        {

            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validationForCaptureVoidRefund()",null,"common","Transaction Details  not provided while placing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Transaction Details  not provided while placing the transaction",new Throwable("Transaction Details  not provided while placing the transaction"));
        }
        if(commTransactionDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validationForCaptureVoidRefund()",null,"common","CommTransaction Details  not provided while placing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"CommTransaction Details  not provided while placing the transaction",new Throwable("CommTransaction Details  not provided while placing the transaction"));
        }
        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validationForCaptureVoidRefund()",null,"common","Previous TransactionId not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous TransactionId not provided while placing the transaction",new Throwable("Previous TransactionId not provided while placing the transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(EmaxHighRiskPaymentGateway.class.getName(),"validationForCaptureVoidRefund()",null,"common","Amount not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing the transaction",new Throwable("Amount not provided while placing the transaction"));
        }

    }

    public GenericResponseVO processCaptureVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        EmaxResponseVO emaxResponseVO = new EmaxResponseVO();

        emaxResponseVO.setStatus("fail");
        emaxResponseVO.setDescription("Captured transaction can not be cancel through provided terminalID.");
        return null;
    }
}