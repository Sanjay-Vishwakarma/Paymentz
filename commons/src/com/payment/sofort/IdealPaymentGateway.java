package com.payment.sofort;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.logicboxes.util.Util;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.sofort.VO.SofortResponseVO;
import com.sofort.lib.*;
import com.sofort.lib.internal.net.ConnectionException;
import com.sofort.lib.internal.net.http.HttpAuthorizationException;
import com.sofort.lib.internal.utils.HashAlgorithm;
import com.sofort.lib.products.RefundBankAccount;
import com.sofort.lib.products.common.BankAccount;
import com.sofort.lib.products.request.IDealRequest;
import com.sofort.lib.products.request.RefundRequest;
import com.sofort.lib.products.response.IDealBanksResponse;
import com.sofort.lib.products.response.IDealNotificationResponse;
import com.sofort.lib.products.response.RefundResponse;
import com.sofort.lib.products.response.parts.FailureMessage;

import com.sofort.lib.internal.net.http.HttpConnectionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 22/2/15
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdealPaymentGateway extends AbstractPaymentGateway
{

    private static Logger log = new Logger(IdealPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(IdealPaymentGateway.class.getName());
    private static Hashtable<String, SofortAccount> sofortAccounts;

    public static final String GATEWAY_TYPE = "iDeal";
    final static ResourceBundle sofort = LoadProperties.getProperty("com.directi.pg.SofortServlet");

    static
    {
        try
        {
            loadSofortAccounts();

        }
        catch (PZDBViolationException e)
        {
            log.error("Error while loading gateway accounts for Sofort: " + Util.getStackTrace(e));
            transactionLogger.error("Error while loading gateway accounts for Sofort: " + Util.getStackTrace(e));
            PZExceptionHandler.handleDBCVEException(e, null, "loadSofortAccounts");
        }
    }

    public IdealPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public  IDealBanksResponse getIDealBankDetails() throws PZTechnicalViolationException
    {
        SofortAccount sofortAccount =  getSofortAccount(accountId);

        int customerId = sofortAccount.getCustomerId();
        int projectId = sofortAccount.getProjectId();
        String apiKey = sofortAccount.getApiKey();
        final String projectPassword = sofortAccount.getProjectPass();
        final String notificationPassword = sofortAccount.getNotificationPass();
        transactionLogger.debug("customerId----"+customerId);
        transactionLogger.debug("apiKey----"+apiKey);

        final SofortLibIDeal sofortLibIDeal = new DefaultSofortLibIDeal(customerId, apiKey, HashAlgorithm.SHA1);

        IDealBanksResponse banksResponse = null;
        try
        {
            banksResponse = sofortLibIDeal.sendIDealBanksRequest();
        }

        catch (HttpConnectionException e) {

            if (e.getResponseCode() == 401) {

                System.err.println("The authorization with the given apiKey has been failed.");
            }

            PZExceptionHandler.raiseTechnicalViolationException("IdealPaymentGateway.java", "getBankDetails()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        if (banksResponse!=null && banksResponse.hasErrors()) {
            // handle errors
            for (FailureMessage error : banksResponse.getErrors()) {
                // handle refund errors
                System.err.println(error.getCode() + " " + error.getMessage());
            }
        }

        return banksResponse;

    }


    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {

        String orderId=null;
        String amount=null;
        String currency=null;
        String ipAddress=null;
        String shopId=null;
        String shopLabel=null;
        String senderBankCode=null;

        validateForAuth(trackingID, requestVO);


        SofortAccount sofortAccount =  getSofortAccount(accountId);

        int customerId = sofortAccount.getCustomerId();
        int projectId = sofortAccount.getProjectId();
        String apiKey = sofortAccount.getApiKey();
        final String projectPassword = sofortAccount.getProjectPass();
        final String notificationPassword = sofortAccount.getNotificationPass();


        CommRequestVO commRequestVO  = (CommRequestVO)requestVO;
        SofortResponseVO commResponseVO = new SofortResponseVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();


        orderId = genericTransDetailsVO.getOrderId();
        amount = genericTransDetailsVO.getAmount();
        Double amt = Double.valueOf(amount);

        currency = commRequestVO.getTransDetailsVO().getCurrency();

        

        senderBankCode = (String)commRequestVO.getAdditioanlParams().get("senderBankCode");

        final SofortLibIDeal sofortLibIDeal = new DefaultSofortLibIDeal(customerId, apiKey, HashAlgorithm.SHA1);

        /*
           * 2nd step, variant 'A' -> generate an iDEAL redirection URL (GET
           * redirection method)
           */
        IDealRequest request = new IDealRequest()
                .setUserId(customerId)
                .setProjectId(projectId)
                .setAmount(amt.doubleValue())
                .setReason1(trackingID)
                .setReason2(orderId)
                .setSenderCountryId("NL")
                .setSenderBankCode(senderBankCode);

        // let the SofortLib generate the proper redirection URL. Hash value
        // will be set automatically!
        String redirectionUrl = sofortLibIDeal.getPaymentUrl(request, projectPassword);


        String errorMessage = null;

        String errorCode =null;

       /* if (paymentResponse.hasResponseErrors())
        {
            // check and handle the response errors and warnings

            while(paymentResponse.getResponsePaymentErrors().iterator().hasNext())
            {
                FailureMessage failureMessage = paymentResponse.getResponsePaymentErrors().iterator().next();
                errorMessage = failureMessage.getMessage()+" , "+errorMessage;
                errorCode = failureMessage.getCode()+" , "+ errorCode;

            }


            commResponseVO.setStatus("failed");
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setRemark(errorMessage);
            commResponseVO.setAmount(amount);
            return commResponseVO;
        }

        if (paymentResponse.hasNewPaymentWarnings())
        {
            // check and handle the response errors and warnings

            while(paymentResponse.getNewPaymentWarnings().iterator().hasNext())
            {
                FailureMessage failureMessage = paymentResponse.getNewPaymentWarnings().iterator().next();
                errorMessage = failureMessage.getMessage()+" , "+errorMessage;
                errorCode = failureMessage.getCode()+" , "+ errorCode;

            }

            commResponseVO.setStatus("failed");
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setRemark(errorMessage);
            commResponseVO.setAmount(amount);
            return commResponseVO;
        }*/


        commResponseVO.setStatus("success");
        commResponseVO.setTransactionId(null);
        commResponseVO.setPaymentURL(redirectionUrl);
        commResponseVO.setMerchantId(trackingID);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));



        return commResponseVO;   //To change body of overridden methods use File | Settings | File Templates.
    }

    public String calculateHash(IDealNotificationResponse notificationResponse)
    {

        SofortAccount sofortAccount =  getSofortAccount(accountId);

        int customerId = sofortAccount.getCustomerId();
        int projectId = sofortAccount.getProjectId();
        String apiKey = sofortAccount.getApiKey();
        final String projectPassword = sofortAccount.getProjectPass();
        final String notificationPassword = sofortAccount.getNotificationPass();



        final SofortLibIDeal sofortLibIDeal = new DefaultSofortLibIDeal(customerId, apiKey, HashAlgorithm.SHA1);

        // generate the hash value with your notification password
        String notificationHash = sofortLibIDeal.calculateHash(notificationResponse, notificationPassword);


        return notificationHash;   //To change body of overridden methods use File | Settings | File Templates.
    }



    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {

        validateForRefund(trackingID, requestVO);

        SofortAccount sofortAccount =  getSofortAccount(accountId);

        int customerId = sofortAccount.getCustomerId();
        int projectId = sofortAccount.getProjectId();
        String apiKey = sofortAccount.getApiKey();


        /*int customerId = 4711;
        int projectId = 8150;
        String apiKey = "API-KEY";*/

        CommRequestVO commRequestVO  = (CommRequestVO)requestVO;

        SofortResponseVO commResponseVO = new SofortResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        String transIdToRefund = genericTransDetailsVO.getPreviousTransactionId();
        String orderId = genericTransDetailsVO.getOrderId();
        String amount = genericTransDetailsVO.getAmount();
        Double amt = Double.valueOf(amount);


        final SofortLibRefund sofortLibRefund = new DefaultSofortLibRefund(customerId, apiKey);

        /* refund a sofort payment */

        RefundRequest request = new RefundRequest(
                Arrays.asList(
                        new com.sofort.lib.products.request.parts.Refund(transIdToRefund, amt)
                                .setComment("Refund for"+orderId).setReason1(trackingID).setReason2(orderId)));

        RefundBankAccount sender = new RefundBankAccount();
        sender.setHolder(sofortAccount.getAccountHolderName());
        sender.setBankName(sofortAccount.getBankName());
        sender.setIban(sofortAccount.getIban());
        sender.setBic(sofortAccount.getBic());

        request.setSender(sender);

        RefundResponse response =null;
        try {
            response = sofortLibRefund.sendRefundRequest(request);
        } catch (HttpAuthorizationException e) {
            //System.err.println("The authorization with the given apiKey has been failed.");
            String msg = "The authorization with the given apiKey has been failed.";
            PZExceptionHandler.raiseTechnicalViolationException("SofortPaymentGateway","processrefund",null,"common",msg, PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,e.getMessage(),e.getCause());


        } catch (HttpConnectionException e) {
            //System.err.println("The HTTP communication has been failed. Response/status code: " + e.getResponseCode());
            String msg = "The HTTP communication has been failed. Response/status code: " + e.getResponseCode();
            PZExceptionHandler.raiseTechnicalViolationException("SofortPaymentGateway","processrefund",null,"common",msg, PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,e.getMessage(),e.getCause());

        } catch (ConnectionException e) {
            //System.err.println("The communication has been failed.");
            String msg = "The communication has been failed.";
            PZExceptionHandler.raiseTechnicalViolationException("SofortPaymentGateway","processrefund",null,"common",msg, PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,e.getMessage(),e.getCause());

        }

        // check for response error first
        if (response.hasErrors()) {
            for (FailureMessage error : response.getErrors()) {
                // handle response errors
                //System.err.println(error.getCode() + " " + error.getMessage());
                commResponseVO.setStatus("failed");
                commResponseVO.setErrorCode(error.getCode());
                commResponseVO.setRemark(error.getMessage());
                commResponseVO.setAmount(amount);
                commResponseVO.setMerchantId(trackingID);
                commResponseVO.setTransactionId(transIdToRefund);
                return commResponseVO;
            }
        }

        for (com.sofort.lib.products.response.parts.Refund refund : response.getRefunds()) {

            // check for refund error first
            if (refund.hasErrors()) {
                for (FailureMessage error : refund.getErrors()) {
                    // handle refund errors
                    //System.err.println(error.getCode() + " " + error.getMessage());
                    commResponseVO.setStatus("failed");
                    commResponseVO.setErrorCode(error.getCode());
                    commResponseVO.setRemark(error.getMessage());
                    commResponseVO.setAmount(amount);
                    commResponseVO.setMerchantId(trackingID);
                    commResponseVO.setTransactionId(transIdToRefund);
                    return commResponseVO;
                }
            }

            // handle refund
            //System.out.println(refund.getStatus());
            //System.out.println(refund.getComment());

            commResponseVO.setStatus(PZTransactionStatus.ACCEPTED_FOR_REVERSAL.toString());
            commResponseVO.setTransactionType("inquiry");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setMerchantId(trackingID);
            commResponseVO.setTransactionId(refund.getTransId());
            commResponseVO.setTransactionStatus(refund.getStatus());
            commResponseVO.setRemark(refund.getComment());
            commResponseVO.setAmount(refund.getAmount()+"");
            commResponseVO.setAmountRefunded(refund.getAmount()+"");
            RefundBankAccount refundBankAccount = refund.getRecipient();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBankName(refundBankAccount.getBankName());
            bankAccount.setBic(refundBankAccount.getBic());
            bankAccount.setHolder(refundBankAccount.getHolder());
            bankAccount.setIban(refundBankAccount.getIban());
            commResponseVO.setRecipient(bankAccount);
            commResponseVO.setPartialRefundId(refund.getPartialRefundId());
            return commResponseVO;


        }


        commResponseVO.setStatus("failed");
        commResponseVO.setAmount(amount);
        commResponseVO.setMerchantId(trackingID);
        commResponseVO.setTransactionId(transIdToRefund);
        return commResponseVO;

    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("IdealPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracking Id not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while authenticating",new Throwable("Tracking Id not provided while authenticating"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  not provided while authenticating", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while authenticating",new Throwable("Request  not provided while authenticating"));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForRefund()",null,"common","TransactionDetails  not provided while authenticating", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while authenticating",new Throwable("TransactionDetails  not provided while authenticating"));
        }

        if(genericTransDetailsVO.getPreviousTransactionId() == null || genericTransDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transaction Id not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while authenticating",new Throwable("Previous Transaction Id not provided while authenticating"));
        }

        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while authenticating",new Throwable("Amount not provided while authenticating"));
        }

    }




    private void validateForAuth(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForAuth()",null,"common","Tracking Id not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while authenticating",new Throwable("Tracking Id not provided while authenticating"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForAuth()",null,"common","Request  not provided while authenticating", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while authenticating",new Throwable("Request  not provided while authenticating"));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForAuth()",null,"common","TransactionDetails  not provided while authenticating", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while authenticating",new Throwable("TransactionDetails  not provided while authenticating"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForAuth()",null,"common","Amount not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while authenticating",new Throwable("Amount not provided while authenticating"));
        }
        if(genericTransDetailsVO.getOrderId() == null || genericTransDetailsVO.getOrderId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForAuth()",null,"common","Order Id not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order Id not provided while authenticating",new Throwable("Order Id not provided while authenticating"));
        }

        CommRequestVO iDealRequestVO  =    (CommRequestVO)requestVO;
        if(iDealRequestVO.getAdditioanlParams()==null || iDealRequestVO.getAdditioanlParams().isEmpty())
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForAuth()",null,"common","Additional Parameter not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Additional Parameter not provided while authenticating",new Throwable("Additional Parameter not provided while authenticating"));
        }
        if(iDealRequestVO.getAdditioanlParams().get("senderBankCode") == null || iDealRequestVO.getAdditioanlParams().get("senderBankCode").equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IdealPaymentGateway.class.getName(),"validateForAuth()",null,"common","Send Bank Code of Additional Parameter  not provided while authenticating", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Send Bank Code of Additional Parameter  not provided while authenticating",new Throwable("Send Bank Code of Additional Parameter  not provided while authenticating"));
        }

    }

































    public static void loadSofortAccounts()throws PZDBViolationException
    {
        log.info("Loading Sofort Accounts......");
        sofortAccounts = new Hashtable<String, SofortAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_sofort", conn);
            while (rs.next())
            {
                SofortAccount account = new SofortAccount(rs);
                //System.out.println(" Sofort Accounts......"+account.getAccountId());
                sofortAccounts.put(account.getAccountId() + "", account);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SofortPaymentGateway.java","loadSofortAccounts()",null,"Common/payment","SQL Exception Thrown::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SofortPaymentGateway.java", "loadSofortAccounts()", null, "Common/payment", "SQL Exception Thrown::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public static SofortAccount getSofortAccount(String accountId)
    {
        log.debug("Inside getSofortAccount");
        return sofortAccounts.get(accountId);
    }





}
