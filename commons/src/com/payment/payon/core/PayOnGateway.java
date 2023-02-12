package com.payment.payon.core;

import com.directi.pg.LoadProperties;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.payon.PayOnUtils;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
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
import com.payment.payon.core.message.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Roshan
 * Date: 25/11/17
 * Time: 11:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE_PAYON = "PayOn";
    public static final String GATEWAY_TYPE_CATELLA = "catella";
    public static final String PRE_AUTH = "PA";
    public static final String CAPTURE = "CP";
    public static final String DEBIT = "DB";
    public static final String CREDIT = "CD";
    public static final String REFUND = "RF";
    public static final String REVERSAL = "RV";
    //private final static String TEST_URL = "https://test.ppipe.net/connectors/gateway";
    //private final static String LIVE_URL = "https://ppipe.net/connectors/gateway";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payon");


    public PayOnGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        PayOnResponseVO payOnResponseVO = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String pwd = gatewayAccount.getPassword();
        PayOnUtils payOnUtils=new PayOnUtils();
        try
        {
            PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;
            PayOnMerchantAccountVO payOnMerchantAccountVO = payOnRequestVO.getPayOnMerchantAccountVO();
            String payOnConnector = payOnMerchantAccountVO.getConnector();
            Identification identification = payOnRequestVO.getIdentification();
            identification.setShortID(payOnMerchantAccountVO.getShortID());

            MerchantAccount merchantAccount = payOnUtils.populateMerchantAccount(payOnMerchantAccountVO);
            CreditCardAccount creditCardAccount = payOnUtils.populateCreditCardDetails(payOnRequestVO.getPayOnCardDetailsVO());
            Customer customer = payOnUtils.populateCustomerDetails(payOnRequestVO.getAddressDetailsVO());
            Authentication authentication = payOnUtils.populateAuthenticationDetails(payOnRequestVO.getPayOnVBVDetailsVO());

            Payment payment = new Payment();
            payment.setAmount(payOnRequestVO.getPayOnTransactionDetailsVO().getAmount());
            payment.setCurrency(payOnRequestVO.getPayOnTransactionDetailsVO().getCurrency());
            payment.setType(DEBIT);
            payment.setDescriptor(payOnRequestVO.getPayOnTransactionDetailsVO().getOrderDesc());

            Request request = new Request();
            RequestTransaction requestTransaction = new RequestTransaction();
            String recurrenceMode = payOnRequestVO.getPayOnTransactionDetailsVO().getRecurrenceMode();
            if (recurrenceMode != null){
                Recurrence recurrence = new Recurrence();
                recurrence.setMode(recurrenceMode);
                requestTransaction.setRecurrence(recurrence);
            }

            requestTransaction.setIdentification(identification);
            requestTransaction.setMerchantAccount(merchantAccount);
            requestTransaction.setPayment(payment);
            requestTransaction.setCreditCardAccount(creditCardAccount);
            requestTransaction.setCustomer(customer);
            if(payOnConnector.equalsIgnoreCase("ems") || payOnConnector.equalsIgnoreCase("catella")){
                requestTransaction.setAuthentication(authentication);
            }
            if(isTest){
                if(payOnConnector.equalsIgnoreCase("catella"))
                    requestTransaction.setMode("INTEGRATION");
                else
                    requestTransaction.setMode("TEST");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("TEST_URL"), userName, pwd);
                if(payOnResponseVO.getStatus().equals("success"))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
            else{
                requestTransaction.setMode("LIVE");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request,RB.getString("LIVE_URL"), userName, pwd);
                if(payOnResponseVO.getStatus().equals("success"))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayOnGateway.class.getName(), "processSale()", null, "common", "IOException while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        return payOnResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        PayOnResponseVO payOnResponseVO = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String pwd = gatewayAccount.getPassword();
        PayOnUtils payOnUtils=new PayOnUtils();

        try
        {
            PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;
            if (payOnRequestVO.getPayOnMerchantAccountVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processAuthentication()", null, "common", "Merchant Accounts  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null, "Merchant Accounts  not provided while authenticating transaction", new Throwable("Merchant Accounts  not provided while authenticating transaction"));
            }
            if (payOnRequestVO.getPayOnTransactionDetailsVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processAuthentication()", null, "common", "TransactionDetails  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null, "Transaction Details  not provided while authenticating transaction", new Throwable("TransactionDetails  not provided while authenticating transaction"));
            }
            if (payOnRequestVO.getPayOnCardDetailsVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processAuthentication()", null, "common", "CardDetails  not provided while authenticating transaction", PZConstraintExceptionEnum.VO_MISSING,null, "CardDetails  not provided while authenticating transaction", new Throwable("CardDetails  not provided while authenticating transaction"));
            }

            PayOnMerchantAccountVO payOnMerchantAccountVO = payOnRequestVO.getPayOnMerchantAccountVO();

            Identification identification = payOnRequestVO.getIdentification();
            identification.setShortID(payOnMerchantAccountVO.getShortID());

            String connector = payOnMerchantAccountVO.getConnector();

            MerchantAccount merchantAccount = payOnUtils.populateMerchantAccount(payOnMerchantAccountVO);
            Customer customer = payOnUtils.populateCustomerDetails(payOnRequestVO.getAddressDetailsVO());
            Authentication authentication = payOnUtils.populateAuthenticationDetails(payOnRequestVO.getPayOnVBVDetailsVO());

            Payment payment = new Payment();
            payment.setAmount(payOnRequestVO.getPayOnTransactionDetailsVO().getAmount());
            payment.setCurrency(payOnRequestVO.getPayOnTransactionDetailsVO().getCurrency());
            if(connector.equalsIgnoreCase("ems")) {
                payment.setDescriptor(payOnRequestVO.getPayOnTransactionDetailsVO().getOrderId());
            }
            payment.setType(PRE_AUTH);

            CreditCardAccount creditCardAccount = payOnUtils.populateCreditCardDetails(payOnRequestVO.getPayOnCardDetailsVO());

            Request request = new Request();
            RequestTransaction requestTransaction = new RequestTransaction();
            requestTransaction.setIdentification(identification);
            requestTransaction.setMerchantAccount(merchantAccount);
            requestTransaction.setPayment(payment);
            requestTransaction.setCreditCardAccount(creditCardAccount);
            if(connector.equalsIgnoreCase("concardis") || connector.equalsIgnoreCase("worldline"))
            {
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentSystemDate = dateFormater.format(new Date());
                requestTransaction.setRequestTimestamp(currentSystemDate);
            }
            if(connector.equalsIgnoreCase("ems") || connector.equalsIgnoreCase("catella"))
            {
                requestTransaction.setCustomer(customer);
                requestTransaction.setAuthentication(authentication);
            }
            if(isTest){
                if(connector.equalsIgnoreCase("catella"))
                    requestTransaction.setMode("INTEGRATION");
                else
                    requestTransaction.setMode("TEST");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("TEST_URL"), userName, pwd);
                if(payOnResponseVO.getStatus().equals("success") || "000.000.000".equals(payOnResponseVO.getReturnCode()))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
            else{
                requestTransaction.setMode("LIVE");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("LIVE_URL"), userName, pwd);
                if(payOnResponseVO.getStatus().equals("success"))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayOnGateway.class.getName(), "processAuthentication()", null, "common", "IOException while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        return payOnResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        PayOnResponseVO payOnResponseVO = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String pwd = gatewayAccount.getPassword();
        PayOnUtils payOnUtils=new PayOnUtils();

        try
        {
            PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;

            if (payOnRequestVO.getPayOnMerchantAccountVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processCapture()", null, "common", "Merchant Accounts  not provided while capturing transaction", PZConstraintExceptionEnum.VO_MISSING,null, "Merchant Accounts  not provided while capturing transaction", new Throwable("Merchant Accounts  not provided while capturing transaction"));
            }
            if (payOnRequestVO.getPayOnTransactionDetailsVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processCapture()", null, "common", "TransactionDetails  not provided while capturing transaction", PZConstraintExceptionEnum.VO_MISSING,null, "TransactionDetails  not provided while capturing transaction", new Throwable("TransactionDetails  not provided while capturing transaction"));
            }
            if (payOnRequestVO.getPayOnCardDetailsVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processCapture()", null, "common", "CardDetails  not provided while capturing transaction", PZConstraintExceptionEnum.VO_MISSING,null, "CardDetails  not provided while capturing transaction", new Throwable("CardDetails  not provided while capturing transaction"));
            }
            PayOnMerchantAccountVO payOnMerchantAccountVO = payOnRequestVO.getPayOnMerchantAccountVO();

            Identification identification = payOnRequestVO.getIdentification();
            identification.setShortID(payOnMerchantAccountVO.getShortID());

            String connector = payOnMerchantAccountVO.getConnector();
            MerchantAccount merchantAccount = payOnUtils.populateMerchantAccount(payOnMerchantAccountVO);

            CreditCardAccount creditCardAccount = payOnUtils.populateCreditCardDetails(payOnRequestVO.getPayOnCardDetailsVO());

            Payment payment = new Payment();
            payment.setAmount(String.format("%.2f", Double.parseDouble(payOnRequestVO.getPayOnTransactionDetailsVO().getAmount())));
            payment.setCurrency(payOnRequestVO.getPayOnTransactionDetailsVO().getCurrency());
            payment.setType(CAPTURE);
            payment.setDescriptor(payOnRequestVO.getPayOnTransactionDetailsVO().getOrderDesc());

            ReferencedTransaction referencedTransaction = new ReferencedTransaction();
            referencedTransaction.setType(payOnRequestVO.getPayOnTransactionDetailsVO().getReferenceTransactionType());

            ConnectorTxID connectorTxID1 = new ConnectorTxID();
            ConnectorTxID connectorTxID2 = new ConnectorTxID();
            ConnectorTxID connectorTxID3 = new ConnectorTxID();

            if(connector.equalsIgnoreCase("ems")) {
                connectorTxID2.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID2());
                referencedTransaction.setConnectorTxID2(connectorTxID2);
            }
            else if(connector.equalsIgnoreCase("worldline")/* || connector.equalsIgnoreCase("concardis")*/){
                connectorTxID3.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3());
                referencedTransaction.setConnectorTxID3(connectorTxID3);
            }
            else if(connector.equalsIgnoreCase("concardis") || connector.equalsIgnoreCase("catella"))
            {
                if(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID1() != null)
                {
                    connectorTxID1.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID1());
                    referencedTransaction.setConnectorTxID1(connectorTxID1);
                }
                if(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID2() != null)
                {
                    connectorTxID2.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID2());
                    referencedTransaction.setConnectorTxID2(connectorTxID2);
                }
                if(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3() != null)
                {
                    connectorTxID3.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3());
                    referencedTransaction.setConnectorTxID3(connectorTxID3);
                }
            }
            referencedTransaction.setRequestTimestamp(payOnRequestVO.getPayOnTransactionDetailsVO().getRequestTimestamp());
            referencedTransaction.setType("PA");

            Request request = new Request();
            RequestTransaction requestTransaction = new RequestTransaction();
            requestTransaction.setIdentification(identification);
            requestTransaction.setMerchantAccount(merchantAccount);
            requestTransaction.setCreditCardAccount(creditCardAccount);
            requestTransaction.setPayment(payment);
            requestTransaction.setReferencedTransaction(referencedTransaction);
            if(isTest){
                requestTransaction.setMode("TEST");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("TEST_URL"), userName, pwd);
                if("000.000.000".equals(payOnResponseVO.getReturnCode()))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
            else{
                requestTransaction.setMode("LIVE");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("LIVE_URL"), userName, pwd);
                if("000.000.000".equals(payOnResponseVO.getReturnCode())){
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayOnGateway.class.getName(), "processCapture()", null, "common", "IOException while capturing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        return payOnResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        PayOnResponseVO payOnResponseVO = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String pwd = gatewayAccount.getPassword();
        PayOnUtils payOnUtils=new PayOnUtils();
        try
        {
            PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;

            if (payOnRequestVO.getPayOnMerchantAccountVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processVoid()", null, "common", "Merchant Accounts  not provided while cancelling transaction", PZConstraintExceptionEnum.VO_MISSING,null, "Merchant Accounts  not provided while cancelling transaction", new Throwable("Merchant Accounts  not provided while cancelling transaction"));
            }
            if (payOnRequestVO.getPayOnTransactionDetailsVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processVoid()", null, "common", "TransactionDetails  not provided while cancelling transaction", PZConstraintExceptionEnum.VO_MISSING,null, "TransactionDetails  not provided while cancelling transaction", new Throwable("TransactionDetails  not provided while cancelling transaction"));
            }
            if (payOnRequestVO.getPayOnCardDetailsVO() == null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayOnGateway.class.getName(), "processVoid()", null, "common", "CardDetails  not provided while cancelling transaction", PZConstraintExceptionEnum.VO_MISSING,null, "CArdDetails  not provided while cancelling transaction", new Throwable("CarDEtails  not provided while cancelling transaction"));
            }
            PayOnMerchantAccountVO payOnMerchantAccountVO = payOnRequestVO.getPayOnMerchantAccountVO();
            String connector = payOnMerchantAccountVO.getConnector();

            Identification identification = payOnRequestVO.getIdentification();
            identification.setShortID(payOnMerchantAccountVO.getShortID());

            MerchantAccount merchantAccount = payOnUtils.populateMerchantAccount(payOnMerchantAccountVO);

            Payment payment = new Payment();
            payment.setAmount(payOnRequestVO.getPayOnTransactionDetailsVO().getAmount());
            payment.setCurrency(payOnRequestVO.getPayOnTransactionDetailsVO().getCurrency());
            payment.setType(REVERSAL);
            payment.setDescriptor(payOnRequestVO.getPayOnTransactionDetailsVO().getOrderDesc());

            CreditCardAccount creditCardAccount = payOnUtils.populateCreditCardDetails(payOnRequestVO.getPayOnCardDetailsVO());

            ReferencedTransaction referencedTransaction = new ReferencedTransaction();
            referencedTransaction.setType(payOnRequestVO.getPayOnTransactionDetailsVO().getReferenceTransactionType());
            ConnectorTxID connectorTxID1 = new ConnectorTxID();
            ConnectorTxID connectorTxID2 = new ConnectorTxID();
            ConnectorTxID connectorTxID3 = new ConnectorTxID();
            connectorTxID1.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID1().trim());
            connectorTxID2.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID2().trim());
            /*connectorTxID3.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3());*/

            if (connector.equalsIgnoreCase("worldline"))
            {
                referencedTransaction.setAmount(payOnRequestVO.getPayOnTransactionDetailsVO().getAmount());
                connectorTxID3.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3().trim());
            }
            else if(connector.equalsIgnoreCase("ems"))
            {
                connectorTxID3.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID4().trim());
                referencedTransaction.setConnectorTxID1(connectorTxID1);
                referencedTransaction.setConnectorTxID2(connectorTxID2);
            }
            else if(connector.equalsIgnoreCase("concardis") || connector.equalsIgnoreCase("catella"))
            {
                if(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID1() != null)
                {
                    connectorTxID1.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID1());
                    referencedTransaction.setConnectorTxID1(connectorTxID1);
                }
                if(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID2() != null)
                {
                    connectorTxID2.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID2());
                    referencedTransaction.setConnectorTxID2(connectorTxID2);
                }
                if(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3() != null)
                {
                    connectorTxID3.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3());
                    referencedTransaction.setConnectorTxID3(connectorTxID3);
                }
            }
            referencedTransaction.setConnectorTxID3(connectorTxID3);
            referencedTransaction.setRequestTimestamp(payOnRequestVO.getPayOnTransactionDetailsVO().getRequestTimestamp());
            referencedTransaction.setType("PA");

            Request request = new Request();
            RequestTransaction requestTransaction = new RequestTransaction();
            requestTransaction.setIdentification(identification);
            requestTransaction.setMerchantAccount(merchantAccount);
            requestTransaction.setCreditCardAccount(creditCardAccount);
            requestTransaction.setPayment(payment);
            requestTransaction.setReferencedTransaction(referencedTransaction);
            if(isTest){
                if(connector.equalsIgnoreCase("catella"))
                    requestTransaction.setMode("INTEGRATION");
                else
                    requestTransaction.setMode("TEST");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("TEST_URL"), userName, pwd);
                if("000.000.000".equals(payOnResponseVO.getReturnCode()))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
            else{
                requestTransaction.setMode("LIVE");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("LIVE_URL"), userName, pwd);
                if("000.000.000".equals(payOnResponseVO.getReturnCode()))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayOnGateway.class.getName(), "processVoid()", null, "common", "IOException while cancelling the transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        return payOnResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        PayOnResponseVO payOnResponseVO = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String pwd = gatewayAccount.getPassword();
        PayOnUtils payOnUtils=new PayOnUtils();
        try
        {
            PayOnRequestVO payOnRequestVO = (PayOnRequestVO) requestVO;

            PayOnMerchantAccountVO payOnMerchantAccountVO = payOnRequestVO.getPayOnMerchantAccountVO();
            String connector = payOnMerchantAccountVO.getConnector();

            Identification identification = payOnRequestVO.getIdentification();
            identification.setShortID(payOnMerchantAccountVO.getShortID());

            MerchantAccount merchantAccount = payOnUtils.populateMerchantAccount(payOnMerchantAccountVO);
            CreditCardAccount creditCardAccount = payOnUtils.populateCreditCardDetails(payOnRequestVO.getPayOnCardDetailsVO());

            Payment payment = new Payment();
            payment.setAmount(payOnRequestVO.getPayOnTransactionDetailsVO().getAmount());
            payment.setCurrency(payOnRequestVO.getPayOnTransactionDetailsVO().getCurrency());
            payment.setType(REFUND);

            ReferencedTransaction referencedTransaction = new ReferencedTransaction();
            referencedTransaction.setType(payOnRequestVO.getPayOnTransactionDetailsVO().getReferenceTransactionType());

            ConnectorTxID connectorTxID1 = new ConnectorTxID();
            ConnectorTxID connectorTxID2 = new ConnectorTxID();
            //ConnectorTxID connectorTxID3 = new ConnectorTxID();
            connectorTxID1.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID1());
            connectorTxID2.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID2());
            //connectorTxID3.setMessage(payOnRequestVO.getPayOnTransactionDetailsVO().getConnectorTxID3());
            referencedTransaction.setConnectorTxID1(connectorTxID1);
            referencedTransaction.setConnectorTxID2(connectorTxID2);
            // referencedTransaction.setConnectorTxID3(connectorTxID3);
            referencedTransaction.setRequestTimestamp(payOnRequestVO.getPayOnTransactionDetailsVO().getRequestTimestamp());
            referencedTransaction.setAmount(payOnRequestVO.getPayOnTransactionDetailsVO().getAmount());
            referencedTransaction.setType("DB");

            Request request = new Request();
            RequestTransaction requestTransaction = new RequestTransaction();
            requestTransaction.setIdentification(identification);
            requestTransaction.setMerchantAccount(merchantAccount);
            requestTransaction.setPayment(payment);
            requestTransaction.setReferencedTransaction(referencedTransaction);
            requestTransaction.setCreditCardAccount(creditCardAccount);
            if(isTest){
                if(connector.equalsIgnoreCase("catella"))
                    requestTransaction.setMode("INTEGRATION");
                else
                    requestTransaction.setMode("TEST");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("TEST_URL"), userName, pwd);
                if("000.000.000".equals(payOnResponseVO.getReturnCode()))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
            else{
                requestTransaction.setMode("LIVE");
                request.setTransaction(requestTransaction);
                payOnResponseVO = payOnUtils.processRequest(request, RB.getString("LIVE_URL"), userName, pwd);
                if("000.000.000".equals(payOnResponseVO.getReturnCode()))
                {
                    String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payOnResponseVO.setDescriptor(descriptor);
                }
            }
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayOnGateway.class.getName(), "processRefund()", null, "common", "IOException while refunding the transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        return payOnResponseVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PayOnGateway","processQuery()",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PayOnGateway","processRebilling()",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
    public String getMaxWaitDays()
    {
        return "5";
    }
}