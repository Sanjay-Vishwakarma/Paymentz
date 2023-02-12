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
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.sofort.VO.SofortResponseVO;
import com.sofort.lib.DefaultSofortLibPayment;
import com.sofort.lib.DefaultSofortLibRefund;
import com.sofort.lib.SofortLibPayment;
import com.sofort.lib.SofortLibRefund;
import com.sofort.lib.internal.net.ConnectionException;
import com.sofort.lib.internal.net.http.HttpAuthorizationException;
import com.sofort.lib.internal.net.http.HttpConnectionException;
import com.sofort.lib.internal.transformer.RawResponse;
import com.sofort.lib.products.RefundBankAccount;
import com.sofort.lib.products.common.BankAccount;
import com.sofort.lib.products.request.PaymentRequest;
import com.sofort.lib.products.request.PaymentTransactionDetailsRequest;
import com.sofort.lib.products.request.RefundRequest;
import com.sofort.lib.products.request.parts.Notification;
import com.sofort.lib.products.response.PaymentResponse;
import com.sofort.lib.products.response.PaymentTransactionDetailsResponse;
import com.sofort.lib.products.response.RefundResponse;
import com.sofort.lib.products.response.SofortTransactionStatusNotification;
import com.sofort.lib.products.response.parts.FailureMessage;
import com.sofort.lib.products.response.parts.PaymentTransactionDetails;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 15/1/15
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SofortPaymentGateway extends AbstractPaymentGateway
{

    private static Logger log = new Logger(SofortPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SofortPaymentGateway.class.getName());
    private static Hashtable<String, SofortAccount> sofortAccounts;

    public static final String GATEWAY_TYPE = "sofort";
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
            PZExceptionHandler.handleDBCVEException(e,null,"loadSofortAccounts");
            throw new RuntimeException(e);
        }
    }




    public SofortPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {

        String orderId=null;
        String amount=null;
        String currency=null;
        String customerEmail=null;
        String customerPhone=null;
        String ipAddress=null;
        String shopId=null;
        String shopLabel=null;
        

        validateForAuth(trackingID, requestVO);


        SofortAccount sofortAccount =  getSofortAccount(accountId);

        int customerId = sofortAccount.getCustomerId();
        int projectId = sofortAccount.getProjectId();
        String apiKey = sofortAccount.getApiKey();

        /*int customerId = 4711;
        int projectId = 8150;
        String apiKey = "API-KEY";*/
        log.debug("===="+sofort.getString("PROXYSCHEME"));
        transactionLogger.debug("===="+sofort.getString("PROXYSCHEME"));
        log.debug("===="+sofort.getString("NotificationURL"));
        transactionLogger.debug("===="+sofort.getString("NotificationURL"));

        String statusNotificationUrl =sofort.getString("PROXYSCHEME")+"://"+sofort.getString("NotificationURL")+trackingID;
        String successUrl = sofort.getString("PROXYSCHEME")+"://"+sofort.getString("PROXYHOST")+":"+sofort.getString("PROXYPORT")+sofort.getString("SuccessURL")+trackingID;
        String abortUrl = sofort.getString("PROXYSCHEME")+"://"+sofort.getString("PROXYHOST")+":"+sofort.getString("PROXYPORT")+sofort.getString("AbortURL")+trackingID;
        String timeOutUrl = sofort.getString("PROXYSCHEME")+"://"+sofort.getString("PROXYHOST")+":"+sofort.getString("PROXYPORT")+sofort.getString("TimeOutURL")+trackingID;


        CommRequestVO commRequestVO  = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();



        orderId = genericTransDetailsVO.getOrderId();
        amount = genericTransDetailsVO.getAmount();
        Double amt = Double.valueOf(amount);

        currency = commRequestVO.getTransDetailsVO().getCurrency();

        customerEmail = genericAddressDetailsVO.getEmail();
        customerPhone= genericAddressDetailsVO.getPhone();

        shopId=commRequestVO.getCommMerchantVO().getDisplayName();
        shopLabel=commRequestVO.getCommMerchantVO().getAliasName();
        ipAddress=genericAddressDetailsVO.getCardHolderIpAddress();


        final SofortLibPayment sofortLibPayment = new DefaultSofortLibPayment(customerId, apiKey);

        PaymentRequest paymentRequest = new PaymentRequest(projectId, amt.doubleValue(), currency, Arrays.asList(trackingID), false);

        //paymentRequest.setInterfaceVersion("");
        paymentRequest.setEmailCustomer(customerEmail);
        paymentRequest.setPhoneCustomer(customerPhone);
        //paymentRequest.setLanguageCode("");
        paymentRequest.setUserVariables(Arrays.asList(orderId));
        
        paymentRequest.setNotificationUrls(Arrays.asList(new Notification(statusNotificationUrl)));
        paymentRequest.setSuccessUrl(successUrl);
        paymentRequest.setSuccessLinkRedirect(true);
        paymentRequest.setAbortUrl(abortUrl);
        paymentRequest.setTimeoutUrl(timeOutUrl);

        PaymentResponse paymentResponse = sofortLibPayment.sendPaymentRequest(paymentRequest);

        String errorMessage = null;
        String errorCode =null;
        System.out.println("paymentResponse.hasResponseErrors()---->"+paymentResponse.hasResponseErrors());
        if (paymentResponse.hasResponseErrors())
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
        }
        
           log.debug("===============1==================="+paymentResponse.getTransId());
           log.debug("===============2==================="+paymentResponse.getPaymentUrl());
           commResponseVO.setStatus("pending");
           commResponseVO.setTransactionId(paymentResponse.getTransId());
           commResponseVO.setRedirectUrl(paymentResponse.getPaymentUrl());
           commResponseVO.setMerchantId(trackingID);
           DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
           Date date = new Date();
           commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));



        return commResponseVO;   //To change body of overridden methods use File | Settings | File Templates.
    }


    public SofortTransactionStatusNotification parseNotification(String statusNotification)
    {
        SofortAccount sofortAccount =  getSofortAccount(accountId);

        int customerId = sofortAccount.getCustomerId();
        int projectId = sofortAccount.getProjectId();
        String apiKey = sofortAccount.getApiKey();


        /*int customerId = 4711;
        int projectId = 8150;
        String apiKey = "API-KEY";*/



        final SofortLibPayment sofortLibPayment = new DefaultSofortLibPayment(customerId, apiKey);
        /*
        * 2nd step -> parse the received transaction changes notification
        */

        SofortTransactionStatusNotification statusNotificationResponse = sofortLibPayment.parseStatusNotificationResponse(new RawResponse(RawResponse.Status.OK, statusNotification));
        String statusNotificationTransId = statusNotificationResponse.getTransId();

        // handle notification responses
        //System.out.println(statusNotificationTransId);


        return statusNotificationResponse;


    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {

        validateForInquiry(trackingID, requestVO);

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

        String paymentId = genericTransDetailsVO.getPreviousTransactionId();

        final SofortLibPayment sofortLibPayment = new DefaultSofortLibPayment(customerId, apiKey);


        PaymentTransactionDetailsRequest transactionRequest = new PaymentTransactionDetailsRequest().setTransIds(Arrays.asList(paymentId));
        PaymentTransactionDetailsResponse transactionDetailsResponse = sofortLibPayment.sendTransactionDetailsRequest(transactionRequest);

        if (transactionDetailsResponse== null )
        {
            commResponseVO.setStatus("failed");
            return commResponseVO;
        }
        PaymentTransactionDetails detailsPayment = transactionDetailsResponse.getTransactions().get(0);
        if (detailsPayment== null )
        {
            commResponseVO.setStatus("Order not exist");
            return commResponseVO;
        }

        // handle current status
        /*System.out.println(detailsPayment.getStatus() + " " + detailsPayment.getStatusReason());
        System.out.println(detailsPayment.getStatus().name() + " " + detailsPayment.getStatusReason().name());*/

        commResponseVO.setStatus("success");
        commResponseVO.setTransactionType("inquiry");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        commResponseVO.setMerchantId(detailsPayment.getReasons().get(0));
        commResponseVO.setTransactionId(detailsPayment.getTransId());
        commResponseVO.setDescription(detailsPayment.getStatusReason().toString());
        commResponseVO.setDescriptor(detailsPayment.getStatusReason().toString());
        commResponseVO.setTransactionStatus(detailsPayment.getStatus().toString());
        commResponseVO.setRemark(detailsPayment.getStatusReason().name());
        commResponseVO.setAmount(detailsPayment.getAmount()+"");
        commResponseVO.setAmountRefunded(detailsPayment.getAmountRefunded()+"");
        commResponseVO.setLanguageCode(detailsPayment.getLanguageCode());
        commResponseVO.setPaymentMethod(detailsPayment.getPaymentMethod());
        commResponseVO.setMerchantOrderId(detailsPayment.getUserVariables().get(0));
        commResponseVO.setSender(detailsPayment.getSender());
        commResponseVO.setRecipient(detailsPayment.getRecipient());




        return commResponseVO;


    }

    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException,PZConstraintViolationException
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
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public static void main(String[] args)
    {
        /*PaySafeCardPaymentGateway paySafeCardPaymentGateway=new PaySafeCardPaymentGateway(null);
        try
        {
            paySafeCardPaymentGateway.processSale(null, null);
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/
    }

    private void validateForAuth(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForAuth()",null,"common","Tracking Id not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while authenticating transaction",new Throwable("Tracking Id not provided while authenticating transaction"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForAuth()",null,"common","Request  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while authenticating transaction",new Throwable("Request  not provided while authenticating transaction"));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForAuth()",null,"common","TransactionDetails  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while authenticating transaction",new Throwable("TransactionDetails  not provided while authenticating transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForAuth()",null,"common","Amount not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while authenticating transaction",new Throwable("Amount not provided while authenticating transaction"));
        }
        if(genericTransDetailsVO.getOrderId() == null || genericTransDetailsVO.getOrderId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForAuth()",null,"common","Order Id not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order Id not provided while authenticating transaction",new Throwable("Order Id not provided while authenticating transaction"));
        }


    }


    private void validateForInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","Tracking Id not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while authenticating transaction",new Throwable("Tracking Id not provided while authenticating transaction"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","Request  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while authenticating transaction",new Throwable("Request  not provided while authenticating transaction"));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","TransactionDetails  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while authenticating transaction",new Throwable("TransactionDetails  not provided while authenticating transaction"));
        }

        if(genericTransDetailsVO.getPreviousTransactionId() == null || genericTransDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","PreviousTransaction Id not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"PreviousTransaction Id not provided while authenticating transaction",new Throwable("PreviousTransaction Id not provided while authenticating transaction"));
        }


    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","Tracking ID not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking ID not provided while authenticating transaction",new Throwable("Tracking ID not provided while authenticating transaction"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","Request  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while authenticating transaction",new Throwable("Request  not provided while authenticating transaction"));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","TransactionDetails  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while authenticating transaction",new Throwable("TransactionDetails  not provided while authenticating transaction"));
        }

        if(genericTransDetailsVO.getPreviousTransactionId() == null || genericTransDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","Previous Transaction ID not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction ID not provided while authenticating transaction",new Throwable("Previous Transaction ID not provided while authenticating transaction"));
        }

        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SofortPaymentGateway.class.getName(),"validateForInquiry()",null,"common","Amount not provided while authenticating transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while authenticating transaction",new Throwable("Amount not provided while authenticating transaction"));
        }

    }


    public static void loadSofortAccounts()throws PZDBViolationException
    {
        log.info("Loading Sofort Accounts......");
        transactionLogger.info("Loading Sofort Accounts......");
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
            PZExceptionHandler.raiseDBViolationException("SofortPaymentGateway.java", "loadSofortAccounts()", null, "Common/payment", "SQL Exception Thrown::::", PZDBExceptionEnum.SQL_EXCEPTION, null,e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public static SofortAccount getSofortAccount(String accountId)
    {
        log.debug("Inside getSofortAccount");
        transactionLogger.debug("Inside getSofortAccount");
        return sofortAccounts.get(accountId);
    }

}