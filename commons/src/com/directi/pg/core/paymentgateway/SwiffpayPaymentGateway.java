package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.swiffpay.MPTransProcess.*;
import com.directi.pg.core.swiffpay.SwiffpayAccount;
import com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 6/13/13
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwiffpayPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log=new Logger(SwiffpayPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(SwiffpayPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "Swiffpay";
    private static Hashtable<String, SwiffpayAccount> SwiffpayAccounts;
    public static final String GATEWAY_URL_SWIFF = "https://plgtrans.swiffpay.com/MPWeb/services/TransactionService";

    static
    {
        try
        {
            loadPayAccounts();
        }
        catch (PZDBViolationException e)
        {
            log.error("exception while loading gateway account detail",e);
            transactionLogger.error("exception while loading gateway account detail",e);

            PZExceptionHandler.handleDBCVEException(e,"Swiff Pay", PZOperations.SWIFFPAY_LAOD_ACCOUNT);
        }

    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SwiffpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static SwiffpayAccount getSwiffpayAccount(String accountId)
    {
        return SwiffpayAccounts.get(accountId);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        //validation for request parameter
        validateForSale(trackingID,requestVO);
        SwiffpayAccount account=getSwiffpayAccount(accountId);

        //defind swiffpay request and response variable
        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        SwiffpayResponseVO swiffPayResponseVO = new SwiffpayResponseVO();

        ProcessResult responce=new ProcessResult();
        CreditCardInfo ccInfodata=new CreditCardInfo();
        Address billingaddress=new Address();
        Recur recur=new Recur();

        //hard code value for transaction
        ccInfodata.setAcctid(String.valueOf(account.getMid()));
        ccInfodata.setMerchantpin(account.getMerchantpin());
        ccInfodata.setCardpresent(1);
        recur.setBillingcycle(0);
        ccInfodata.setRecurring(recur);

        //transaction details
        GenericTransDetailsVO genericTransDetailsVO = swiffPayRequestVO.getTransDetailsVO();
        ccInfodata.setAmount(Float.parseFloat(genericTransDetailsVO.getAmount()));
        ccInfodata.setMerchantordernumber(trackingID);

        ccInfodata.setCurrencycode(genericTransDetailsVO.getCurrency());

        //address details
        GenericAddressDetailsVO genericAddressDetailsVO = swiffPayRequestVO.getAddressDetailsVO();
        billingaddress.setAddr1(genericAddressDetailsVO.getStreet());
        billingaddress.setCity(genericAddressDetailsVO.getCity());
        billingaddress.setCountry(genericAddressDetailsVO.getCountry());
        billingaddress.setZip(genericAddressDetailsVO.getZipCode());
        billingaddress.setState(genericAddressDetailsVO.getState());
        ccInfodata.setBilladdress(billingaddress);
        ccInfodata.setShipaddress(billingaddress);
        ccInfodata.setEmail(genericAddressDetailsVO.getEmail());
        ccInfodata.setPhone(genericAddressDetailsVO.getPhone());
        ccInfodata.setIpaddress(genericAddressDetailsVO.getIp());
        String nameOnCard= genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        //card details
        GenericCardDetailsVO genericCardDetailsVO = swiffPayRequestVO.getCardDetailsVO();
        ccInfodata.setCcnum(genericCardDetailsVO.getCardNum());
        ccInfodata.setExpyear(Integer.parseInt(genericCardDetailsVO.getExpYear()));
        ccInfodata.setExpmon(Integer.parseInt(genericCardDetailsVO.getExpMonth()));
        ccInfodata.setCcname(nameOnCard);
        ccInfodata.setCvv2(Integer.parseInt(genericCardDetailsVO.getcVV()));
        ccInfodata.setCctype(Functions.getCardType(genericCardDetailsVO.getCardNum()));

        TransactionServiceSoapBindingStub swiffpay = getInterfaceBindingStub(GATEWAY_URL_SWIFF);


        try
        {
            responce =  swiffpay.processCCSale(ccInfodata);

        }
        catch(RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SwiffpayPaymentGateway.class.getName(),"processSale()",null,"common","Remote Exception while placing transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }


        if(responce!=null)
        {
            if(responce.getStatus().equalsIgnoreCase("Approved"))
            {
                swiffPayResponseVO.setAuthorizationCode(responce.getAuthcode());
                swiffPayResponseVO.setReferenceNumber(responce.getRefcode());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
                swiffPayResponseVO.setMerchantOrderId(responce.getMerchantordernumber());
                swiffPayResponseVO.setTransactionId(responce.getOrderid());
                swiffPayResponseVO.setDescription(responce.getStatus());
                if(("Approved").equalsIgnoreCase(responce.getStatus()))
                {
                    swiffPayResponseVO.setStatus("success");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                else
                {
                    swiffPayResponseVO.setStatus("fail");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                swiffPayResponseVO.setTransactionType(responce.getPaytype());

            }
            else
            {
                swiffPayResponseVO.setStatus("fail");
                swiffPayResponseVO.setDescription(responce.getStatus());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
            }
        }

        return swiffPayResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        //validation for request parameter
        validateForSale(trackingID,requestVO);
        SwiffpayAccount account=getSwiffpayAccount(accountId);

        //defind swiffpay request and response variable
        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        SwiffpayResponseVO swiffPayResponseVO = new SwiffpayResponseVO();

        ProcessResult responce=new ProcessResult();
        CreditCardInfo ccInfodata=new CreditCardInfo();
        Address billingaddress=new Address();
        Recur recur=new Recur();

        //hard code value for transaction
        ccInfodata.setAcctid(String.valueOf(account.getMid()));
        ccInfodata.setMerchantpin(account.getMerchantpin());
        ccInfodata.setCardpresent(1);
        recur.setBillingcycle(0);
        ccInfodata.setRecurring(recur);

        //transaction details
        GenericTransDetailsVO genericTransDetailsVO = swiffPayRequestVO.getTransDetailsVO();
        ccInfodata.setAmount(Float.parseFloat(genericTransDetailsVO.getAmount()));
        ccInfodata.setMerchantordernumber(trackingID);
        ccInfodata.setCurrencycode(genericTransDetailsVO.getCurrency());

        //address details
        GenericAddressDetailsVO genericAddressDetailsVO = swiffPayRequestVO.getAddressDetailsVO();
        billingaddress.setAddr1(genericAddressDetailsVO.getStreet());
        billingaddress.setCity(genericAddressDetailsVO.getCity());
        billingaddress.setCountry(genericAddressDetailsVO.getCountry());
        billingaddress.setZip(genericAddressDetailsVO.getZipCode());
        billingaddress.setState(genericAddressDetailsVO.getState());
        ccInfodata.setBilladdress(billingaddress);
        ccInfodata.setShipaddress(billingaddress);
        ccInfodata.setEmail(genericAddressDetailsVO.getEmail());
        ccInfodata.setPhone(genericAddressDetailsVO.getPhone());
        ccInfodata.setIpaddress(genericAddressDetailsVO.getIp());
        String nameOnCard= genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

        //card details
        GenericCardDetailsVO genericCardDetailsVO = swiffPayRequestVO.getCardDetailsVO();
        ccInfodata.setCcnum(genericCardDetailsVO.getCardNum());
        ccInfodata.setExpyear(Integer.parseInt(genericCardDetailsVO.getExpYear()));
        ccInfodata.setExpmon(Integer.parseInt(genericCardDetailsVO.getExpMonth()));
        ccInfodata.setCcname(nameOnCard);
        ccInfodata.setCvv2(Integer.parseInt(genericCardDetailsVO.getcVV()));
        ccInfodata.setCctype(genericCardDetailsVO.getCardType());

        TransactionServiceSoapBindingStub swiffpay = getInterfaceBindingStub(GATEWAY_URL_SWIFF);

        try
        {
            responce =  swiffpay.processCCAuth(ccInfodata);

        }
        catch(RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SwiffpayPaymentGateway.class.getName(),"processAuthentication()",null,"common","Remote Exception while authenticating transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        if(responce!=null)
        {
            if(responce.getStatus().equalsIgnoreCase("Approved"))
            {
                swiffPayResponseVO.setAuthorizationCode(responce.getAuthcode());
                swiffPayResponseVO.setReferenceNumber(responce.getRefcode());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
                swiffPayResponseVO.setMerchantOrderId(responce.getMerchantordernumber());
                swiffPayResponseVO.setTransactionId(responce.getOrderid());
                swiffPayResponseVO.setDescription(responce.getStatus());
                if(("Approved").equalsIgnoreCase(responce.getStatus()))
                {
                    swiffPayResponseVO.setStatus("success");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                else
                {
                    swiffPayResponseVO.setStatus("fail");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                swiffPayResponseVO.setTransactionType(responce.getPaytype());

            }
            else
            {
                swiffPayResponseVO.setStatus("fail");
                swiffPayResponseVO.setDescription(responce.getStatus());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
            }
        }
        return swiffPayResponseVO;
    }

    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        //validate trackingID
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }
        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }
        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;

        //validate transaction details
        GenericTransDetailsVO genericTransDetailsVO = swiffPayRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
        }

        //validate card details
        GenericCardDetailsVO genericCardDetailsVO= swiffPayRequestVO.getCardDetailsVO();
        if(genericCardDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getCardNum() ==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpMonth() ==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpYear() ==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }

        if(genericCardDetailsVO.getcVV() ==null || genericCardDetailsVO.getcVV().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }

        //validate address details
        GenericAddressDetailsVO genericAddressDetailsVO=swiffPayRequestVO.getAddressDetailsVO();
        if(genericAddressDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getCountry() ==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getPhone() ==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getEmail() ==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email Id not provided while placing transaction",new Throwable("Email Id not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Last Name not provided while placing transaction",new Throwable("Last Name not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getIp() ==null || genericAddressDetailsVO.getIp().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Ip Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Ip Address not provided while placing transaction",new Throwable("Ip Address not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getZipCode()==null|| genericAddressDetailsVO.getZipCode().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Zip Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Zip Code not provided while placing transaction",new Throwable("Zip Code not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getStreet()==null|| genericAddressDetailsVO.getStreet().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getCity()==null|| genericAddressDetailsVO.getCity().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));

        }
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        validateForRefund(trackingID,requestVO);
        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        SwiffpayResponseVO swiffPayResponseVO = new SwiffpayResponseVO();
        SwiffpayAccount account=getSwiffpayAccount(accountId);
        ProcessResult responce=new ProcessResult();
        VoidCreditPost refunddetail=new VoidCreditPost();

        refunddetail.setAcctid(account.getMid());
        refunddetail.setAmount(Float.parseFloat(swiffPayRequestVO.getTransDetailsVO().getAmount()));
        refunddetail.setHistoryid(swiffPayRequestVO.getHistoryid());
        refunddetail.setIpaddress(swiffPayRequestVO.getAddressDetailsVO().getIp());
        refunddetail.setOrderid(swiffPayRequestVO.getTransDetailsVO().getPreviousTransactionId());
        refunddetail.setMerchantpin(account.getMerchantpin());

        TransactionServiceSoapBindingStub swiffpay = getInterfaceBindingStub(GATEWAY_URL_SWIFF);

        try
        {
            responce =  swiffpay.processCredit(refunddetail);

        }
        catch(RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SwiffpayPaymentGateway.class.getName(),"processRefund()",null,"common","Remote Exception while Refunding transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        if(responce!=null)
        {
            if(responce.getStatus().equalsIgnoreCase("Approved"))
            {
                swiffPayResponseVO.setAuthorizationCode(responce.getAuthcode());
                swiffPayResponseVO.setReferenceNumber(responce.getRefcode());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
                swiffPayResponseVO.setMerchantOrderId(responce.getMerchantordernumber());
                swiffPayResponseVO.setTransactionId(responce.getOrderid());
                swiffPayResponseVO.setDescription(responce.getStatus());
                if(("Approved").equalsIgnoreCase(responce.getStatus()))
                {
                    swiffPayResponseVO.setStatus("success");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                else
                {
                    swiffPayResponseVO.setStatus("fail");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                swiffPayResponseVO.setTransactionType(responce.getPaytype());

            }
            else
            {
                swiffPayResponseVO.setStatus("fail");
                swiffPayResponseVO.setDescription(responce.getStatus());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
            }
        }

        return swiffPayResponseVO;
    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }

        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO=swiffPayRequestVO.getTransDetailsVO();
        if(swiffPayRequestVO.getTransDetailsVO().getPreviousTransactionId()==null|| swiffPayRequestVO.getTransDetailsVO().getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Previous Transaction Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while placing transaction",new Throwable("Previous Transaction Id not provided while placing transaction"));

        }
        if(swiffPayRequestVO.getHistoryid()==null|| swiffPayRequestVO.getHistoryid().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","History Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"History Id not provided while placing transaction",new Throwable("History Id not provided while placing transaction"));

        }

        if(swiffPayRequestVO.getTransDetailsVO().getAmount()==null|| swiffPayRequestVO.getTransDetailsVO().getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));

        }
        if(swiffPayRequestVO.getAddressDetailsVO().getIp()==null|| swiffPayRequestVO.getAddressDetailsVO().getIp().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Ip Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Ip Address not provided while placing transaction",new Throwable("Ip Address not provided while placing transaction"));

        }
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        validateForInquiry(trackingID,requestVO);
        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        SwiffpayResponseVO swiffPayResponseVO = new SwiffpayResponseVO();

        GenericTransDetailsVO genericTransDetailsVO= swiffPayRequestVO.getTransDetailsVO();
        ProcessResult responce=new ProcessResult();
        SwiffpayAccount account=getSwiffpayAccount(accountId);
        TransRetrieve inquirydetails=new TransRetrieve();

        inquirydetails.setAcctid(account.getMid());
        inquirydetails.setMerchantordernumber(swiffPayRequestVO.getMerchantordernumber());
        inquirydetails.setMerchantpin(account.getMerchantpin());

        inquirydetails.setIpaddress(swiffPayRequestVO.getIpaddress());

        TransactionServiceSoapBindingStub swiffpay = getInterfaceBindingStub(GATEWAY_URL_SWIFF);

        try
        {
            responce =  swiffpay.processTransRetrieve(inquirydetails);

        }
        catch(RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SwiffpayPaymentGateway.class.getName(),"processQuery()",null,"common","Remote Exception while Querying transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        if(responce!=null)
        {




                swiffPayResponseVO.setAuthorizationCode(responce.getAuthcode());
                swiffPayResponseVO.setReferenceNumber(responce.getRefcode());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
                swiffPayResponseVO.setMerchantOrderId(responce.getMerchantordernumber());
                swiffPayResponseVO.setTransactionId(responce.getOrderid());
                swiffPayResponseVO.setDescription(responce.getStatus());
                if(("Approved").equalsIgnoreCase(responce.getStatus()))
                {
                    swiffPayResponseVO.setStatus("success");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                else
                {
                    swiffPayResponseVO.setStatus("fail");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                swiffPayResponseVO.setTransactionType(responce.getPaytype());

        }
        return swiffPayResponseVO;
    }

    private void validateForInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }

        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;


        if(swiffPayRequestVO.getMerchantordernumber()==null|| swiffPayRequestVO.getMerchantordernumber().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Merchant Order NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Merchant Order NO not provided while placing transaction",new Throwable("Merchant Order NO not provided while placing transaction"));
        }

        if(swiffPayRequestVO.getIpaddress()==null|| swiffPayRequestVO.getIpaddress().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
        }
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        validateForCapture(trackingID,requestVO);
        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        SwiffpayResponseVO swiffPayResponseVO = new SwiffpayResponseVO();
        SwiffpayAccount account=getSwiffpayAccount(accountId);
        VoidCreditPost capturedata=new VoidCreditPost();
        ProcessResult responce=new ProcessResult();
        GenericTransDetailsVO genericTransDetailsVO=swiffPayRequestVO.getTransDetailsVO();

        capturedata.setAcctid(account.getMid());

        capturedata.setAmount(Float.parseFloat(genericTransDetailsVO.getAmount()));
        capturedata.setOrderid(genericTransDetailsVO.getOrderId());
        capturedata.setHistoryid(swiffPayRequestVO.getHistoryid());
        capturedata.setMerchantordernumber(swiffPayRequestVO.getMerchantordernumber());
        capturedata.setMerchantpin(account.getMerchantpin());

        TransactionServiceSoapBindingStub swiffpay = getInterfaceBindingStub(GATEWAY_URL_SWIFF);

        try
        {
            responce =  swiffpay.processCCPost(capturedata);

        }
        catch(RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SwiffpayPaymentGateway.class.getName(),"processCapture()",null,"common","Remote Exception while Capturing transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        if(responce!=null)
        {
            if(responce.getStatus().equalsIgnoreCase("Approved"))
            {
                swiffPayResponseVO.setAuthorizationCode(responce.getAuthcode());
                swiffPayResponseVO.setReferenceNumber(responce.getRefcode());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
                swiffPayResponseVO.setMerchantOrderId(responce.getMerchantordernumber());
                swiffPayResponseVO.setTransactionId(responce.getOrderid());
                swiffPayResponseVO.setDescription(responce.getStatus());
                if(("Approved").equalsIgnoreCase(responce.getStatus()))
                {
                    swiffPayResponseVO.setStatus("success");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                else
                {
                    swiffPayResponseVO.setStatus("fail");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                swiffPayResponseVO.setTransactionType(responce.getPaytype());

            }
            else
            {
                swiffPayResponseVO.setStatus("fail");
                swiffPayResponseVO.setDescription(responce.getStatus());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
            }
        }

        return swiffPayResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    private void validateForCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }

        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO=swiffPayRequestVO.getTransDetailsVO();


        if(swiffPayRequestVO.getMerchantordernumber()==null|| swiffPayRequestVO.getMerchantordernumber().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Merchant Order NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Merchant Order NO not provided while placing transaction",new Throwable("Merchant Order NO not provided while placing transaction"));

        }

        if(genericTransDetailsVO.getOrderId()==null|| genericTransDetailsVO.getOrderId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Order Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order Id not provided while placing transaction",new Throwable("Order Id not provided while placing transaction"));

        }
        if(swiffPayRequestVO.getHistoryid()==null|| swiffPayRequestVO.getHistoryid().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","History Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"History Id not provided while placing transaction",new Throwable("History Id not provided while placing transaction"));

        }
        if(genericTransDetailsVO.getAmount()==null|| genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SwiffpayPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));

        }
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        SwiffpayRequestVO swiffPayRequestVO = (SwiffpayRequestVO)requestVO;
        SwiffpayResponseVO swiffPayResponseVO = new SwiffpayResponseVO();
        SwiffpayAccount account=getSwiffpayAccount(accountId);
        VoidCreditPost cancledata=new VoidCreditPost();
        ProcessResult responce=new ProcessResult();
        GenericTransDetailsVO genericTransDetailsVO=swiffPayRequestVO.getTransDetailsVO();

        cancledata.setAcctid(account.getMid());

        cancledata.setAmount(Float.parseFloat(genericTransDetailsVO.getAmount()));
        cancledata.setOrderid(genericTransDetailsVO.getOrderId());
        cancledata.setHistoryid(swiffPayRequestVO.getHistoryid());

        TransactionServiceSoapBindingStub swiffpay = getInterfaceBindingStub(GATEWAY_URL_SWIFF);

        try
        {
            responce =  swiffpay.processCCVoid(cancledata);

        }
        catch(RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SwiffpayPaymentGateway.class.getName(),"processVoid()",null,"common","Remote Exception while Cancelling transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        if(responce!=null)
        {
            if(responce.getStatus().equalsIgnoreCase("Approved"))
            {
                swiffPayResponseVO.setAuthorizationCode(responce.getAuthcode());
                swiffPayResponseVO.setReferenceNumber(responce.getRefcode());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
                swiffPayResponseVO.setMerchantOrderId(responce.getMerchantordernumber());
                swiffPayResponseVO.setTransactionId(responce.getOrderid());
                swiffPayResponseVO.setDescription(responce.getStatus());
                if(("Approved").equalsIgnoreCase(responce.getStatus()))
                {
                    swiffPayResponseVO.setStatus("success");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                else
                {
                    swiffPayResponseVO.setStatus("fail");
                    swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                }
                swiffPayResponseVO.setTransactionStatus(responce.getStatus());
                swiffPayResponseVO.setTransactionType(responce.getPaytype());

            }
            else
            {
                swiffPayResponseVO.setStatus("fail");
                swiffPayResponseVO.setDescription(responce.getStatus());
                swiffPayResponseVO.setHistoryid(responce.getHistoryid());
            }
        }

        return swiffPayResponseVO;
    }

    public static void loadPayAccounts() throws PZDBViolationException
    {
        log.info("Loading Swiffpay Accounts Details");
        transactionLogger.info("Loading Swiffpay Accounts Details");
        SwiffpayAccounts = new Hashtable<String, SwiffpayAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_swiffpay", conn);
            while (rs.next())
            {
                SwiffpayAccount account = new SwiffpayAccount(rs);
                SwiffpayAccounts.put(account.getAccountid() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(SwiffpayPaymentGateway.class.getName(),"loadPayAccounts()",null,"common","Sql Exception while loading swiff Pay gate way account details", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SwiffpayPaymentGateway.class.getName(), "loadPayAccounts()", null, "common", "System Error while loading swiff Pay gate way account details", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    private TransactionServiceSoapBindingStub getInterfaceBindingStub(String endPointAddress) throws PZTechnicalViolationException
    {
        com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub binding=null;
        try
        {
                binding = (com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionServiceSoapBindingStub)new com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImplServiceLocator().getTransactionService();               
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SwiffpayPaymentGateway.class.getName(),"getInterfaceBindingStub()",null,"common","Service Exception while making transaction with SWIFF PAY", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }


        return binding;
    }
}
