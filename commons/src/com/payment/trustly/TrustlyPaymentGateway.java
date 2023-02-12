package com.payment.trustly;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.trustly.api.SignedAPI;
import com.payment.trustly.api.commons.Currency;
import com.payment.trustly.api.commons.Method;
import com.payment.trustly.api.data.request.Request;
import com.payment.trustly.api.data.request.RequestParameters;
import com.payment.trustly.api.data.request.requestdata.DepositData;
import com.payment.trustly.api.data.request.requestdata.RefundData;
import com.payment.trustly.api.data.response.Response;
import com.payment.trustly.api.requestbuilders.AccountPayout;
import org.codehaus.jettison.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 11/23/17.
 */
public class TrustlyPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="trustly";
    private static TransactionLogger transactionLogger= new TransactionLogger(TrustlyPaymentGateway.class.getName());
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.trustly");
    public TrustlyPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }



    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PerfectMoneyPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----enter's into processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String privateKeyPath = RB.getString("Test_Merchant_Private_Key");
        transactionLogger.debug("privateKeyPath------" + privateKeyPath);

        try
        {

            boolean isTest = gatewayAccount.isTest();
            String username = gatewayAccount.getMerchantId();
            String password = gatewayAccount.getPassword();
            String firstName = commAddressDetailsVO.getFirstname();
            String lastName = commAddressDetailsVO.getLastname();
            String amount = transDetailsVO.getAmount();
            String currency = transDetailsVO.getCurrency();

            String email = commAddressDetailsVO.getEmail();
            String successURL = RB.getString("successURL")+trackingID;
            String failURL = RB.getString("failURL")+trackingID;
            String endUserId = commAddressDetailsVO.getCustomerid();

            Map<String, Object> attributes = new TreeMap<>();
            attributes.put("Firstname", firstName);
            attributes.put("Currency", currency);
            attributes.put("Amount", amount);
            attributes.put("Lastname", lastName);
            attributes.put("Email", email);
            attributes.put("SuccessURL", successURL);
            attributes.put("FailURL", failURL);

            DepositData depositData = new DepositData();
            depositData.setEndUserID(endUserId);
            depositData.setMessageID(trackingID);
            depositData.setNotificationURL(RB.getString("notificationURL"));
            depositData.setUsername(username);
            depositData.setPassword(password);
            depositData.setAttributes(attributes);

            RequestParameters requestParameters = new RequestParameters();
            requestParameters.setData(depositData);
            requestParameters.setUUID(UUID.randomUUID().toString());

            Request request = new Request();
            request.setMethod(Method.DEPOSIT);
            request.setParams(requestParameters);
            request.setVersion(1.1);

            SignedAPI signedAPI = new SignedAPI();
            signedAPI.init(privateKeyPath, "", username, password, isTest);
            Response response = signedAPI.sendRequest(request);
            transactionLogger.error("-----sale Response-----" + response.getResJson());

            if (response != null)
            {
                if (response.getError() == null || response.getError().equals("null"))
                {

                    JSONObject jsonObject = new JSONObject(response.getResJson());

                    String url =jsonObject.getJSONObject("result").getJSONObject("data").getString("url");
                    String orderId=jsonObject.getJSONObject("result").getJSONObject("data").getString("orderid");

                    transactionLogger.debug("OrderID:::::" + orderId);
                    transactionLogger.debug("Url:::::" + url);

                    commResponseVO.setRedirectUrl(url);
                    commResponseVO.setTransactionId(orderId);
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Succesful");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType("sale");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction failed");
                    commResponseVO.setTransactionType("sale");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction failed");
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }catch (Exception e){
            transactionLogger.error("Exception--->",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----enter's into processRefund----");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String privateKeyPath = RB.getString("Test_Merchant_Private_Key");

        try
        {
            boolean isTest = gatewayAccount.isTest();
            String username = gatewayAccount.getMerchantId();
            String password = gatewayAccount.getPassword();
            String orderid = transactionDetailsVO.getPreviousTransactionId();
            String amount = transactionDetailsVO.getAmount();
            String currency = transactionDetailsVO.getCurrency();

            RefundData refundData=new RefundData();
            refundData.setUsername(username);
            refundData.setPassword(password);
            refundData.setAmount(amount);
            refundData.setCurrency(Currency.valueOf(currency));
            refundData.setOrderID(orderid);
            refundData.setAttributes(null);

            RequestParameters requestParameters = new RequestParameters();
            requestParameters.setData(refundData);
            requestParameters.setUUID(UUID.randomUUID().toString());

            Request request = new Request();
            request.setMethod(Method.REFUND);
            request.setParams(requestParameters);
            request.setVersion(1.1);

            SignedAPI signedAPI = new SignedAPI();
            signedAPI.init(privateKeyPath, "", username, password, isTest);
            Response response = signedAPI.sendRequest(request);

            transactionLogger.error("-----refund Response-----"+response.getResJson());

            if (response != null)
            {
                if (response.getError() == null || response.getError().equals("null"))
                {

                    JSONObject jsonObject = new JSONObject(response.getResJson());

                    String result =jsonObject.getJSONObject("result").getJSONObject("data").getString("result");
                    String orderId=jsonObject.getJSONObject("result").getJSONObject("data").getString("orderid");

                    transactionLogger.debug("OrderID:::::" + orderId);
                    transactionLogger.debug("result:::::" + result);

                    if ("1".equals(result))
                    {
                        commResponseVO.setTransactionId(orderId);
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Succesful");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionType("refund");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Transaction failed");
                        commResponseVO.setTransactionType("refund");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }

                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction failed");
                    commResponseVO.setTransactionType("refund");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction failed");
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }catch (Exception e){
            transactionLogger.error("Exception--->",e);
        }
        return commResponseVO;
    }

    public static void main(String[] args)
    {
        GenericRequestVO genericRequestVO = new GenericRequestVO();
        String accountId = "2768";
        try
        {

            TrustlyPaymentGateway trustlyPaymentGateway = new TrustlyPaymentGateway(accountId);

            trustlyPaymentGateway.processPayout("587228", null);

        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----enter's into processPayout----");

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String privateKeyPath = RB.getString("Test_Merchant_Private_Key");

        try
        {
            boolean isTest = gatewayAccount.isTest();
            String username = gatewayAccount.getMerchantId();
            String password = gatewayAccount.getPassword();
            Functions functions = new Functions();

            /*String username ="";
            String password ="0bd8e544-a045-4ffc-a76c-f18b129fa50b";
            boolean isTest = true;
            String endUserId ="12345ahah";
            String amount = "1.00";
            String currency ="EUR";
            String accountId="3721329101";
            String address="";
            String countryCode="";
            String dateOfBirth="";
            String firstName="";
            String lastName="";
            String partyType="PERSON";*/

            String endUserId = transactionDetailsVO.getCustomerId();
            String amount = transactionDetailsVO.getAmount();
            String currency = transactionDetailsVO.getCurrency();
            String accountId = "";
            String address = "";
            String countryCode = "";
            String dateOfBirth = "";
            String firstName = "";
            String lastName = "";
            String partyType = "PERSON";

            if (functions.isValueNull(transactionDetailsVO.getCustomerBankId()))
                accountId = transactionDetailsVO.getCustomerBankId();
            else
                accountId = commRequestVO.getCardDetailsVO().getAccountNumber();

            AccountPayout.Build build = new AccountPayout.Build(accountId,endUserId,trackingId,amount,RB.getString("notificationURL"),Currency.valueOf(currency));

            build.senderInformation(address,countryCode,dateOfBirth,firstName,lastName,partyType);
            AccountPayout accountPayout = new AccountPayout(build);

            Request request = accountPayout.getRequest();


            SignedAPI signedAPI = new SignedAPI();
            signedAPI.init(privateKeyPath, "", username, password, isTest);
            Response response = signedAPI.sendRequest(request);

            transactionLogger.error("-----account Payout-----"+response.getResJson());

            if (response != null)
            {
                if (response.getError() == null || response.getError().equals("null"))
                {

                    JSONObject jsonObject = new JSONObject(response.getResJson());

                    String result =jsonObject.getJSONObject("result").getJSONObject("data").getString("result");
                    String orderId=jsonObject.getJSONObject("result").getJSONObject("data").getString("orderid");

                    transactionLogger.debug("OrderID:::::" + orderId);
                    transactionLogger.debug("result:::::" + result);

                    if ("1".equals(result))
                    {
                        commResponseVO.setTransactionId(orderId);
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Succesful");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionType("payout");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("Transaction failed");
                        commResponseVO.setTransactionType("payout");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }

                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction failed");
                    commResponseVO.setTransactionType("payout");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction failed");
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("trustly withdraw error===",e);
        }
        return commResponseVO;
    }
}
