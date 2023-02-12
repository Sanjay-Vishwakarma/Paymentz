package com.payment.payhost;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
//import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.payhost.core.za.co.paygate.www.PayHOST.*;
import org.apache.axis.AxisFault;
import org.apache.axis.types.Token;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Admin on 16/9/2015.
 */
public class PayHostPayGatePaymentGateway extends AbstractPaymentGateway
{

    private static Logger log = new Logger(PayHostPayGatePaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayHostPayGatePaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "PayHost";
    private final static String URL = "https://secure.paygate.co.za/payhost/process.trans?wsdl";

    @Override
    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public PayHostPayGatePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        log.debug("Entering processSale of PayHost PayGate...");
        transactionLogger.debug("Entering processSale of PayHost PayGate...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        try
        {
            Token PAYGATEID = new Token(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            Token PAYGATEPASSWORD = new Token(GatewayAccountService.getGatewayAccount(accountId).getPassword());

            Token FIRSTNAME = new Token(genericAddressDetailsVO.getFirstname());
            Token LASTNAME = new Token(genericAddressDetailsVO.getLastname());
            Token PHONE = new Token(genericAddressDetailsVO.getPhone());
            Token EMAIL = new Token(genericAddressDetailsVO.getEmail());

            Token ADDRESS1 = new Token(genericAddressDetailsVO.getStreet());
            Token CITY = new Token(genericAddressDetailsVO.getCity());
            Token STATE = new Token(genericAddressDetailsVO.getState()+" "+genericAddressDetailsVO.getCountry());
            Token ZIP = new Token(genericAddressDetailsVO.getZipCode());

            Token CARDNUMBER = new Token(genericCardDetailsVO.getCardNum());
            Token CARDEXPDATE = new Token(genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear());
            Token CVV = new Token(genericCardDetailsVO.getcVV());
            Token BUDGETPERIOD = new Token("0");
            Token CARDISSUE = new Token("012015");
            //Token VAULTID = new Token("6eb998d9-b4e8-46b8-9772-90ecb644ab54");

            Token MERCHANTORDERID = new Token(trackingID);
            Token CURRENCY = new Token(GatewayAccountService.getGatewayAccount(accountId).getCurrency());
            //Token AMOUNT = new Token(genericTransDetailsVO.getAmount());

            log.debug("request data..."+PAYGATEID+"-password-"+PAYGATEPASSWORD+"-first name-"+FIRSTNAME+"-Last name-"+LASTNAME+"-Phone-"+PHONE+"-Email-"+EMAIL+"-Address1-"+ADDRESS1+"-City-"+CITY+"-State-"+STATE+"-Zip-"+ZIP+"-Card Number-"+CARDNUMBER+"-Card Exp Date-"+CARDEXPDATE+"-Cvv-"+CVV+"-Budget Period-"+BUDGETPERIOD+"-Merchant OrderId-"+MERCHANTORDERID+"");
            transactionLogger.debug("request data...");

            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

            SinglePaymentRequest singlePaymentRequest = new SinglePaymentRequest();
            CardPaymentRequestType cardPaymentRequestType = new CardPaymentRequestType();
            PayGateAccountType payGateAccountType = new PayGateAccountType();
            PersonType personType = new PersonType();
            AddressType addressType = new AddressType();
            OrderType orderType = new OrderType();

            payGateAccountType.setPayGateId(PAYGATEID);
            payGateAccountType.setPassword(PAYGATEPASSWORD);

            personType.setFirstName(FIRSTNAME);
            personType.setLastName(LASTNAME);
            /*Token[] mobileList = new Token[1];
            mobileList[0]= PHONE;
            personType.setMobile(mobileList);*/
            Token[] emailList = new Token[1];
            emailList[0]= EMAIL;
            personType.setEmail(emailList);

            Token[] addressList = new Token[1];
            addressList[0]= EMAIL;
            addressType.setAddressLine(addressList);
            addressType.setCity(CITY);
            addressType.setState(STATE);
            addressType.setZip(ZIP);

            orderType.setMerchantOrderId(MERCHANTORDERID);
            orderType.setAmount(getAmount(genericTransDetailsVO.getAmount()));
            orderType.setCurrency(CurrencyType.ZAR);

            cardPaymentRequestType.setCardNumber(CARDNUMBER);
            cardPaymentRequestType.setCardExpiryDate(CARDEXPDATE);
            cardPaymentRequestType.setCVV(CVV);
            //cardPaymentRequestType.setBudgetPeriod(BUDGETPERIOD);
            //cardPaymentRequestType.setVault(false);
            //cardPaymentRequestType.setCardIssueDate(CARDISSUE);
            //cardPaymentRequestType.setVaultId(VAULTID);

            //personType.setAddress(addressType);
            cardPaymentRequestType.setOrder(orderType);
            cardPaymentRequestType.setCustomer(personType);
            cardPaymentRequestType.setAccount(payGateAccountType);
            singlePaymentRequest.setCardPaymentRequest(cardPaymentRequestType);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub payHOSTSoap11Stub;
            payHOSTSoap11Stub = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub) new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();

            assertNotNull("binding is null", payHOSTSoap11Stub);
            payHOSTSoap11Stub.setTimeout(60000);

            transactionLogger.debug("response called-----");

            com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse response = null;

            response = payHOSTSoap11Stub.singlePayment(singlePaymentRequest);

            transactionLogger.debug("response----"+response);
            transactionLogger.debug("response transaction code---" + response.getCardPaymentResponse().getStatus().getTransactionStatusCode());
            transactionLogger.debug("response transaction status---" + response.getCardPaymentResponse().getStatus().getTransactionStatusDescription());

            String status = "fail";
            if(response!=null && !(response.equals("")))
            {
                log.debug("response transaction code---" + response.getCardPaymentResponse().getStatus().getTransactionStatusCode());
                log.debug("response transaction status---" + response.getCardPaymentResponse().getStatus().getTransactionStatusDescription());
                transactionLogger.debug("response transaction code---" + response.getCardPaymentResponse().getStatus().getTransactionStatusCode());
                transactionLogger.debug("response transaction status---" + response.getCardPaymentResponse().getStatus().getTransactionStatusDescription());
                if (response.getCardPaymentResponse().getStatus().getTransactionStatusCode().equals("1") && response.getCardPaymentResponse().getStatus().getTransactionStatusDescription().equals("Approved"))
                {
                    status = "success";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantOrderId(response.getCardPaymentResponse().getStatus().getReference());
                commResponseVO.setDescription(response.getCardPaymentResponse().getStatus().getResultDescription().toString());
                commResponseVO.setTransactionId(response.getCardPaymentResponse().getStatus().getTransactionId());
                commResponseVO.setErrorCode(response.getCardPaymentResponse().getStatus().getResultCode().toString());
                commResponseVO.setRemark(response.getCardPaymentResponse().getStatus().getStatusName().toString());
                commResponseVO.setTransactionStatus(response.getCardPaymentResponse().getStatus().getTransactionStatusDescription().toString());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch(AxisFault e)
        {
            log.error("Axis---exception---", e);
            transactionLogger.error("Axis---exception---", e);
            //PZExceptionHandler.raiseTechnicalViolationException("PayHostPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch(ServiceException e)
        {
            log.error("Service---exception---",e);
            transactionLogger.error("Service---exception---", e);
            //PZExceptionHandler.raiseTechnicalViolationException("PayHostPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch(RemoteException e)
        {
            log.error("Remote---exception---",e);
            transactionLogger.error("Remote---exception---",e);
            //PZExceptionHandler.raiseTechnicalViolationException("PayHostPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        log.debug("Entering processSale of PayHost PayGate...");
        transactionLogger.debug("Entering processSale of PayHost PayGate...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        try
        {
            Token PAYGATEID = new Token(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            Token PAYGATEPASSWORD = new Token(GatewayAccountService.getGatewayAccount(accountId).getPassword());

            Token FIRSTNAME = new Token(genericAddressDetailsVO.getFirstname());
            Token LASTNAME = new Token(genericAddressDetailsVO.getLastname());
            Token PHONE = new Token(genericAddressDetailsVO.getPhone());
            Token EMAIL = new Token(genericAddressDetailsVO.getEmail());

            Token ADDRESS1 = new Token(genericAddressDetailsVO.getStreet());
            Token CITY = new Token(genericAddressDetailsVO.getCity());
            Token STATE = new Token(genericAddressDetailsVO.getState()+" "+genericAddressDetailsVO.getCountry());
            Token ZIP = new Token(genericAddressDetailsVO.getZipCode());

            Token CARDNUMBER = new Token(genericCardDetailsVO.getCardNum());
            Token CARDEXPDATE = new Token(genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear());
            Token CVV = new Token(genericCardDetailsVO.getcVV());
            Token BUDGETPERIOD = new Token("0");
            Token CARDISSUE = new Token("012015");
            //Token VAULTID = new Token("6eb998d9-b4e8-46b8-9772-90ecb644ab54");

            Token MERCHANTORDERID = new Token(trackingID);
            Token CURRENCY = new Token(GatewayAccountService.getGatewayAccount(accountId).getCurrency());
            //Token AMOUNT = new Token(genericTransDetailsVO.getAmount());

            log.debug("request data..."+PAYGATEID+"-password-"+PAYGATEPASSWORD+"-first name-"+FIRSTNAME+"-Last name-"+LASTNAME+"-Phone-"+PHONE+"-Email-"+EMAIL+"-Address1-"+ADDRESS1+"-City-"+CITY+"-State-"+STATE+"-Zip-"+ZIP+"-Card Number-"+CARDNUMBER+"-Card Exp Date-"+CARDEXPDATE+"-Cvv-"+CVV+"-Budget Period-"+BUDGETPERIOD+"-Merchant OrderId-"+MERCHANTORDERID+"");
            transactionLogger.debug("request data...");

            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

            SinglePaymentRequest singlePaymentRequest = new SinglePaymentRequest();
            SettleRequestType settleRequestType = new SettleRequestType();
            SingleFollowUpRequest singleFollowUpRequest = new SingleFollowUpRequest();


            CardPaymentRequestType cardPaymentRequestType = new CardPaymentRequestType();
            PayGateAccountType payGateAccountType = new PayGateAccountType();
            PersonType personType = new PersonType();
            AddressType addressType = new AddressType();
            OrderType orderType = new OrderType();

            payGateAccountType.setPayGateId(PAYGATEID);
            payGateAccountType.setPassword(PAYGATEPASSWORD);

            personType.setFirstName(FIRSTNAME);
            personType.setLastName(LASTNAME);
            /*Token[] mobileList = new Token[1];
            mobileList[0]= PHONE;
            personType.setMobile(mobileList);*/
            Token[] emailList = new Token[1];
            emailList[0]= EMAIL;
            personType.setEmail(emailList);

            Token[] addressList = new Token[1];
            addressList[0]= EMAIL;
            addressType.setAddressLine(addressList);
            addressType.setCity(CITY);
            addressType.setState(STATE);
            addressType.setZip(ZIP);

            orderType.setMerchantOrderId(MERCHANTORDERID);
            orderType.setAmount(getAmount(genericTransDetailsVO.getAmount()));
            orderType.setCurrency(CurrencyType.ZAR);

            cardPaymentRequestType.setCardNumber(CARDNUMBER);
            cardPaymentRequestType.setCardExpiryDate(CARDEXPDATE);
            cardPaymentRequestType.setCVV(CVV);
            //cardPaymentRequestType.setBudgetPeriod(BUDGETPERIOD);
            //cardPaymentRequestType.setVault(false);
            //cardPaymentRequestType.setCardIssueDate(CARDISSUE);
            //cardPaymentRequestType.setVaultId(VAULTID);

            //personType.setAddress(addressType);
            cardPaymentRequestType.setOrder(orderType);
            cardPaymentRequestType.setCustomer(personType);
            cardPaymentRequestType.setAccount(payGateAccountType);
            singlePaymentRequest.setCardPaymentRequest(cardPaymentRequestType);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub payHOSTSoap11Stub;
            payHOSTSoap11Stub = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub) new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();

            assertNotNull("binding is null", payHOSTSoap11Stub);
            //payHOSTSoap11Stub.setTimeout(60000);

            transactionLogger.debug("response called-----" );

            com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse response = null;
            com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse responseSale = null;

            response = payHOSTSoap11Stub.singlePayment(singlePaymentRequest);
            transactionLogger.debug("response-----"+response);

            //settleRequestType.setMerchantOrderId(MERCHANTORDERID);
            if(response!=null && !(response.equals("")) && response.getCardPaymentResponse()!=null && response.getCardPaymentResponse().getStatus()!=null && response.getCardPaymentResponse().getStatus().getTransactionStatusCode().equals("1") && response.getCardPaymentResponse().getStatus().getTransactionStatusDescription().equals("Approved"))
            {
                settleRequestType.setAccount(payGateAccountType);
                settleRequestType.setTransactionId(response.getCardPaymentResponse().getStatus().getTransactionId());
                singleFollowUpRequest.setSettlementRequest(settleRequestType);
                responseSale = payHOSTSoap11Stub.singleFollowUp(singleFollowUpRequest);
            }

            String status = "fail";
            if(response!=null && !(response.equals("")))
            {
                log.debug("response transaction code---" + response.getCardPaymentResponse().getStatus().getTransactionStatusCode());
                log.debug("response transaction status---" + response.getCardPaymentResponse().getStatus().getTransactionStatusDescription());
                transactionLogger.debug("response transaction code---" + response.getCardPaymentResponse().getStatus().getTransactionStatusCode());
                transactionLogger.debug("response transaction status---" + response.getCardPaymentResponse().getStatus().getTransactionStatusDescription());
                if (responseSale!=null && responseSale.getSettlementResponse()!=null && responseSale.getSettlementResponse().getStatus()!=null && responseSale.getSettlementResponse().getStatus().getTransactionStatusCode()!=null &&  responseSale.getSettlementResponse().getStatus().getTransactionStatusCode().equals("5"))
                {
                    status = "success";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantOrderId(response.getCardPaymentResponse().getStatus().getReference());
                commResponseVO.setDescription(response.getCardPaymentResponse().getStatus().getResultDescription().toString());
                commResponseVO.setTransactionId(response.getCardPaymentResponse().getStatus().getTransactionId());
                commResponseVO.setErrorCode(response.getCardPaymentResponse().getStatus().getResultCode().toString());
                commResponseVO.setRemark(response.getCardPaymentResponse().getStatus().getStatusName().toString());
                commResponseVO.setTransactionStatus(response.getCardPaymentResponse().getStatus().getTransactionStatusDescription().toString());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch(AxisFault e)
        {
            log.error("Axis---exception---", e);
            transactionLogger.error("Axis---exception---", e);
            //PZExceptionHandler.raiseTechnicalViolationException("PayHostPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch(ServiceException e)
        {
            log.error("Service---exception---",e);
            transactionLogger.error("Service---exception---", e);
            //PZExceptionHandler.raiseTechnicalViolationException("PayHostPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch(RemoteException e)
        {
            log.error("Remote---exception---",e);
            transactionLogger.error("Remote---exception---",e);
            //PZExceptionHandler.raiseTechnicalViolationException("PayHostPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processRefund of PayHost PayGate...");
        transactionLogger.debug("Entering processRefund of PayHost PayGate...");

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        //GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        //String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        //String transactionId = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            Token PAYGATEID = new Token(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            Token PAYGATEPASSWORD = new Token(GatewayAccountService.getGatewayAccount(accountId).getPassword());


            Token TRANSACTIONID = new Token();
            Token AMOUNT = new Token();


            SingleFollowUpRequest singleFollowUpRequest = new SingleFollowUpRequest();
            //CardPaymentRequestType cardPaymentRequestType = new CardPaymentRequestType();
            PayGateAccountType payGateAccountType = new PayGateAccountType();

            RefundRequestType refundRequestType = new RefundRequestType();

            payGateAccountType.setPayGateId(PAYGATEID);
            payGateAccountType.setPassword(PAYGATEPASSWORD);

            refundRequestType.setAccount(payGateAccountType);
            //refundRequestType.setMerchantOrderId(MERCHANTORDERID);
            refundRequestType.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
            refundRequestType.setAmount(getAmount(commTransactionDetailsVO.getAmount()));
            singleFollowUpRequest.setRefundRequest(refundRequestType);


            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub payHOSTSoap11Stub;
            payHOSTSoap11Stub = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub) new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();

            assertNotNull("binding is null", payHOSTSoap11Stub);
            payHOSTSoap11Stub.setTimeout(60000);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse response = null;

            response = payHOSTSoap11Stub.singleFollowUp(singleFollowUpRequest);
            transactionLogger.debug("response----"+response);

            String status = "fail";
            if(response!=null && !(response.equals("")))
            {
                if(response.getRefundResponse().getStatus().getTransactionStatusCode().equals("5"))
                {
                    status="success";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionId(response.getRefundResponse().getStatus().getTransactionId());
                commResponseVO.setMerchantOrderId(response.getRefundResponse().getStatus().getReference());
                commResponseVO.setRemark(response.getRefundResponse().getStatus().getStatusName().toString());
                commResponseVO.setTransactionStatus(response.getRefundResponse().getStatus().getTransactionStatusDescription().toString());
                commResponseVO.setErrorCode(response.getRefundResponse().getStatus().getResultCode().toString());
                commResponseVO.setDescription(response.getRefundResponse().getStatus().getResultDescription().toString());
            }
            log.debug("description---"+response.getRefundResponse().getStatus().getResultDescription());
            log.debug("error_code---"+response.getRefundResponse().getStatus().getTransactionStatusCode());
            log.debug("transaction-description---" + response.getRefundResponse().getStatus().getTransactionStatusDescription());


        }
        catch (RemoteException e)
        {
            log.error("Exception in refund---", e);
        }
        catch(ServiceException e)
        {
            log.error("Exception in refund---", e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processQuery of PayHost PayGate...");
        transactionLogger.debug("Entering processQuery of PayHost PayGate...");

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        //GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        //String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        //String transactionId = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            Token PAYGATEID = new Token(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            Token PAYGATEPASSWORD = new Token(GatewayAccountService.getGatewayAccount(accountId).getPassword());

            //Token TRANSACTIONID = new Token();
            Token PAYREQUESTID = new Token();
            Token MERCHANTORDERID = new Token(trackingID);

            SingleFollowUpRequest singleFollowUpRequest = new SingleFollowUpRequest();
            //CardPaymentRequestType cardPaymentRequestType = new CardPaymentRequestType();
            PayGateAccountType payGateAccountType = new PayGateAccountType();

            QueryRequestType queryRequestType = new QueryRequestType();

            payGateAccountType.setPayGateId(PAYGATEID);
            payGateAccountType.setPassword(PAYGATEPASSWORD);

            queryRequestType.setAccount(payGateAccountType);
            queryRequestType.setMerchantOrderId(MERCHANTORDERID);
            queryRequestType.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
            singleFollowUpRequest.setQueryRequest(queryRequestType);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub payHOSTSoap11Stub;
            payHOSTSoap11Stub = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub) new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();

            assertNotNull("binding is null", payHOSTSoap11Stub);
            payHOSTSoap11Stub.setTimeout(60000);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse response = null;

            response = payHOSTSoap11Stub.singleFollowUp(singleFollowUpRequest);
            transactionLogger.debug("resposne----"+response);

        }
        catch (ServiceException e)
        {

        }
        catch (RemoteException e)
        {

        }
        return commResponseVO;

    }
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processVoid of PayHost PayGate...");
        transactionLogger.debug("Entering processVoid of PayHost PayGate...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        //GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        //String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        //String transactionId = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            Token PAYGATEID = new Token(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            Token PAYGATEPASSWORD = new Token(GatewayAccountService.getGatewayAccount(accountId).getPassword());

            SingleFollowUpRequest singleFollowUpRequest = new SingleFollowUpRequest();
            //CardPaymentRequestType cardPaymentRequestType = new CardPaymentRequestType();
            PayGateAccountType payGateAccountType = new PayGateAccountType();

            VoidRequestType voidRequestType = new VoidRequestType();

            payGateAccountType.setPayGateId(PAYGATEID);
            payGateAccountType.setPassword(PAYGATEPASSWORD);

            voidRequestType.setAccount(payGateAccountType);
            voidRequestType.setTransactionType(TransactionType.Authorisation);
            voidRequestType.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
            singleFollowUpRequest.setVoidRequest(voidRequestType);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub payHOSTSoap11Stub;
            payHOSTSoap11Stub = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub) new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();

            assertNotNull("binding is null", payHOSTSoap11Stub);
            payHOSTSoap11Stub.setTimeout(60000);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse response = null;

            response = payHOSTSoap11Stub.singleFollowUp(singleFollowUpRequest);
            transactionLogger.debug("request----"+singleFollowUpRequest);
            transactionLogger.debug("response-----"+response);

            String status = "fail";
            if(response!=null && !(response.equals("")))
            {
                if(response.getVoidResponse().getStatus().getTransactionStatusCode().equals("9"))
                {
                    status="success";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionId(response.getVoidResponse().getStatus().getTransactionStatusCode().toString());
                commResponseVO.setRemark(response.getVoidResponse().getStatus().getStatusName().toString());
                commResponseVO.setTransactionStatus(response.getVoidResponse().getStatus().getTransactionStatusDescription().toString());
                commResponseVO.setErrorCode(response.getVoidResponse().getStatus().getResultCode().toString());
                commResponseVO.setDescription(response.getVoidResponse().getStatus().getResultDescription().toString());

            }
            log.debug("description---" + response.getVoidResponse().getStatus().getResultDescription());
            log.debug("error_code---" + response.getVoidResponse().getStatus().getTransactionStatusCode());
            log.debug("transaction-description---" + response.getVoidResponse().getStatus().getTransactionStatusDescription());


        }
        catch (ServiceException e)
        {
            log.error("Exception in cancel---", e);
        }
        catch (RemoteException e)
        {
            log.error("Exception in cancel---", e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processCapture of PayHost PayGate...");
        transactionLogger.debug("Entering processCapture of PayHost PayGate...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        //GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        //String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        //String transactionId = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            Token PAYGATEID = new Token(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            Token PAYGATEPASSWORD = new Token(GatewayAccountService.getGatewayAccount(accountId).getPassword());

            Token TRANSACTIONID = new Token();

            SingleFollowUpRequest singleFollowUpRequest = new SingleFollowUpRequest();
            //CardPaymentRequestType cardPaymentRequestType = new CardPaymentRequestType();
            PayGateAccountType payGateAccountType = new PayGateAccountType();

            SettleRequestType settleRequestType = new SettleRequestType();

            payGateAccountType.setPayGateId(PAYGATEID);
            payGateAccountType.setPassword(PAYGATEPASSWORD);

            settleRequestType.setAccount(payGateAccountType);
            settleRequestType.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
            singleFollowUpRequest.setSettlementRequest(settleRequestType);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub payHOSTSoap11Stub;
            payHOSTSoap11Stub = (com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTSoap11Stub) new com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOSTServiceLocator().getPayHOSTSoap11();

            assertNotNull("binding is null", payHOSTSoap11Stub);
            payHOSTSoap11Stub.setTimeout(60000);

            com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse response = null;

            response = payHOSTSoap11Stub.singleFollowUp(singleFollowUpRequest);

            transactionLogger.debug("response----"+response);

            String status = "fail";
            if(response!=null && !(response.equals("")))
            {
                if(response.getSettlementResponse().getStatus().getTransactionStatusCode().equals("5"))
                {
                    status="success";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionId(response.getSettlementResponse().getStatus().getTransactionId());
                commResponseVO.setMerchantOrderId(response.getSettlementResponse().getStatus().getReference());
                commResponseVO.setRemark(response.getSettlementResponse().getStatus().getStatusName().toString());
                commResponseVO.setTransactionStatus(response.getSettlementResponse().getStatus().getTransactionStatusDescription().toString());
                commResponseVO.setErrorCode(response.getSettlementResponse().getStatus().getResultCode().toString());
                commResponseVO.setDescription(response.getSettlementResponse().getStatus().getResultDescription().toString());

            }
            log.debug("description---" + response.getSettlementResponse().getStatus().getResultDescription());
            log.debug("error_code---" + response.getSettlementResponse().getStatus().getTransactionStatusCode());
            log.debug("transaction-description---" + response.getSettlementResponse().getStatus().getTransactionStatusDescription());

        }
        catch (ServiceException e)
        {
            log.error("Exception in capture---", e);
        }
        catch (RemoteException e)
        {
            log.error("Exception in capture---",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public int getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount;
    }
}