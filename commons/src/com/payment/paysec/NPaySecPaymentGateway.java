package com.payment.paysec;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

/**
 * Created by Admin on 7/24/2018.
 */
public class NPaySecPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "npaysec";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PaysecServlet");
    private static TransactionLogger transactionLogger = new TransactionLogger(NPaySecPaymentGateway.class.getName());

    public NPaySecPaymentGateway(String accountid)
    {
        this.accountId = accountid;
    }

    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String merchantId = account.getMerchantId();
        String secretKey = account.getFRAUD_FTP_PASSWORD();
        boolean isTest = account.isTest();

        PaySecUtils paySecUtils = new PaySecUtils();

        String version = "3.0";
        String channelCode = "BANK_TRANSFER";
        String orderTime = "1509979555000";

        try
        {
            String hashStr = "" + trackingId + ";" + genericTransDetailsVO.getAmount() + ";" + genericTransDetailsVO.getCurrency() + ";" + merchantId + ";" + version + "";
            String hash256 = paySecUtils.hash256(hashStr);
            String signature = paySecUtils.generateHash(hash256, secretKey);

            String request = "{" +
                    "\"header\" : {" +
                    "\"version\" : \"" + version + "\"," +
                    "\"merchantCode\" : \"" + merchantId + "\",\n" +
                    "\"signature\" : \"" + signature + "\"\n" +
                    "}," +
                    "\"body\" : {" +
                    "\"channelCode\" : \"" + channelCode + "\"," +
                    "\"notifyURL\" : \"" + RB.getString("NOTIFY_URL") + "\"," +
                    "\"returnURL\" : \"" + RB.getString("RETURN_URL") + "\"," +
                    "\"orderAmount\" : \"" + genericTransDetailsVO.getAmount() + "\"," +
                    "\"orderTime\" : \"" + orderTime + "\"," +
                    "\"cartId\" : \"" + trackingId + "\"," +
                    "\"currency\" : \"" + genericTransDetailsVO.getCurrency() + "\"," +
                    "\"firstName\" : \"" + genericAddressDetailsVO.getFirstname() + "\"," +
                    "\"lastName\" : \"" + genericAddressDetailsVO.getLastname() + "\"," +
                    "\"email\" : \"" + genericAddressDetailsVO.getEmail() + "\"," +
                    "\"phone\" : \"" + genericAddressDetailsVO.getPhone() + "\"," +
                    "\"street\" : \"" + genericAddressDetailsVO.getStreet() + "\"," +
                    "\"city\" : \"" + genericAddressDetailsVO.getCity() + "\"," +
                    "\"state\" : \"" + genericAddressDetailsVO.getState() + "\"," +
                    "\"zip\" : \"" + genericAddressDetailsVO.getZipCode() + "\"" +
                    "}" +
                    "}";

            transactionLogger.error(":::::token response:::::" + request);
            String response = "";
            if (isTest)
            {
                response = paySecUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_URL"), request);
            }
            else
            {
                response = paySecUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_URL"), request);
            }
            transactionLogger.error(":::::token response:::::" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PaySecDepositResponse paySecDepositResponse = objectMapper.readValue(response, PaySecDepositResponse.class);

            if (paySecDepositResponse != null)
            {

                Header header = paySecDepositResponse.getHeader();
                Body body = paySecDepositResponse.getBody();
                if (functions.isValueNull(header.getStatus()) && "SUCCESS".equalsIgnoreCase(header.getStatus()))
                {
                    if (isTest)
                    {
                        commResponseVO.setUrlFor3DRedirect(RB.getString("TEST_SEND_TOKEN_URL"));
                    }
                    else
                    {
                        commResponseVO.setUrlFor3DRedirect(RB.getString("LIVE_SEND_TOKEN_URL"));
                    }
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark("Transaction Pending");
                    commResponseVO.setDescription("Pending");
                    commResponseVO.setResponseHashInfo(body.getToken());
                }
                else
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Failed");
                }
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Failed");
            }
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (PZTechnicalViolationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId, GenericRequestVO genericRequestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) genericRequestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        String merchantId = commMerchantVO.getMerchantId();
        String secretKey = commMerchantVO.getPassword();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = account.isTest();

        String amount = transactionDetailsVO.getAmount();
        String currency = transactionDetailsVO.getCurrency();

        String status = "";
        String remark = "";

        String bankCode = "CIMB_MYR";
        String bankName = "CIMB Bank";
        String bankBranch = "Malad";
        String customerName = "Shipra";
        String bankAccountName = "pz";
        String bankAccountNumber = "6217852000016118245";
        String province = "MH";
        String city = "mumbai";
        String version = "3.0";

        PaySecUtils paySecUtils = new PaySecUtils();

        try
        {
            String hashStr = "" + trackingId + ";" + amount + ";" + currency + ";" + merchantId + ";" + version + "";
            String hash256 = paySecUtils.hash256(hashStr);
            String signature = paySecUtils.generateHash(hash256, secretKey);

            String request = "{  " +
                    "\"merchantCode\" : \"" + merchantId + "\",\n" +
                    "\"amount\" : \"" + amount + "\",\n" +
                    "\"currency\" : \"" + currency + "\",\n " +
                    "\"bankCode\" : \"" + bankCode + "\",\n " +
                    "\"bankName\" : \"" + bankName + "\",\n" +
                    "\"bankBranch\"  : \"" + bankBranch + "\",\n  " +
                    "\"customerName\" : \"" + customerName + "\",\n " +
                    "\"bankAccountName\" : \"" + bankAccountName + "\",\n " +
                    "\"bankAccountNumber\" : \"" + bankAccountNumber + "\",\n " +
                    "\"cartId\" : \"" + trackingId + "\",\n " +
                    "\"province\" : \"" + province + "\",\n  " +
                    "\"city\" : \"" + city + "\",\n " +
                    "\"signature\" : \"" + signature + "\",\n " +
                    "\"notifyURL\" : \"" + RB.getString("NOTIFY_URL") + "\",\n " +
                    "\"version\" : \"3.0\" } ";

            transactionLogger.error(":::::payout request:::::" + request);
            String response = "";
            if (isTest)
            {
                response = paySecUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PAYOUT_URL"), request);
            }
            else
            {
                response = paySecUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PAYOUT_URL"), request);
            }
            transactionLogger.error(":::::payout response:::::" + response);

            Functions functions = new Functions();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PaySecPayoutResponse payoutResponse = objectMapper.readValue(response, PaySecPayoutResponse.class);

            if (payoutResponse == null)
            {
                if (functions.isValueNull(payoutResponse.getStatus()) && "PENDING".equalsIgnoreCase(payoutResponse.getStatus()))
                {
                    status = "success";
                    remark = "Payout Successful";
                }
                else if (functions.isValueNull(payoutResponse.getStatus()) && "FAILURE".equalsIgnoreCase(payoutResponse.getStatus()))
                {
                    status = "failed";
                    remark = "Payout Failed";
                }
            }
            else
            {
                status = "failed";
                remark = "Payout Failed";
            }
            commResponseVO.setMerchantOrderId(trackingId);
            commResponseVO.setStatus(status);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime("");

        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NPaySecPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (PZTechnicalViolationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PZTechnicalViolationException.class.getName(), "processPayout()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingId, GenericRequestVO genericRequestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint(NPaySecPaymentGateway.class.getName(), "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(NPaySecPaymentGateway.class.getName(), "processRefund()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(NPaySecPaymentGateway.class.getName(), "processRefund()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(NPaySecPaymentGateway.class.getName(), "processCapture()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(NPaySecPaymentGateway.class.getName(), "processVoid()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}