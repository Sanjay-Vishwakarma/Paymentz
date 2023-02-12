package com.directi.pg.core.paymentgateway;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.ugspay.UGSPayAccount;
import com.directi.pg.core.ugspay.message.*;
import com.directi.pg.core.valueObjects.*;

import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.gold24.core.message.Gold24BindingStub;
import com.payment.gold24.core.message.Gold24ServiceLocator;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 9, 2013
 * Time: 10:35:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class UGSPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log=new Logger(UGSPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(UGSPaymentGateway.class.getName());

    private static Hashtable<String, UGSPayAccount> ugspayAccounts;


    public static final String GATEWAY_TYPE_UGS = "UGSPay";
    public static final String GATEWAY_TYPE_FORT = "Fort7Pay";
    public static final String GATEWAY_URL_UGS = "https://secure.ugspay.com:443/interface/SOAP/1.31/index.php";
    public static final String GATEWAY_URL_FORT = "https://secure.fort7pay.com:443/interface/SOAP/1.31/index.php";


     static
    {
        try
        {
            loadUGSPayAccounts();

        }
        catch (PZDBViolationException e)
        {
            log.error("Exceptin while loading gateway accounts details of UGS",e);
            transactionLogger.error("Exceptin while loading gateway accounts details of UGS",e);

            PZExceptionHandler.handleDBCVEException(e,"UGS", PZOperations.UGS_LAOD_ACCOUNT);
        }

    }

         /**
         *
         * @param accountId
         * @throws com.directi.pg.SystemError
         */
        public UGSPaymentGateway(String accountId)
        {
            this.accountId = accountId;
        }

        /**
         *
         */
        public UGSPaymentGateway()
        {

        }

        public String getMaxWaitDays()
       {
           return null;  //To change body of implemented methods use File | Settings | File Templates.
       }

        /**
         *
         * @param trackingID
         * @param requestVO
         * @return
         * @throws SystemError
         */
        public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
        {
             log.info("  Inside   processSale  ::::::::");
             transactionLogger.info("  Inside   processSale  ::::::::");


            validateForSale(trackingID, requestVO);    //validating manadatory parameters


            UGSPayRequestVO  ugsPayRequestVO = (UGSPayRequestVO)requestVO;
            UGSPayResponseVO ugsPayResponseVO = null;
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            UGSPayAccount ugsPayAccount  = getUGSPayAccount(accountId);

            DirectAuthorizationRequest directAuthorizationRequest  = new DirectAuthorizationRequest();

            directAuthorizationRequest.setWebsiteID(ugsPayAccount.getWebsiteid());
            directAuthorizationRequest.setPassword(ugsPayAccount.getPassword());
            directAuthorizationRequest.setOrderID(trackingID);

            //Getting Transaction Details
            GenericTransDetailsVO genericTransDetailsVO = ugsPayRequestVO.getTransDetailsVO();

           //Amount in minor unit, e.g. cents; 100 dollar cent equals to 1 dollar
            BigDecimal amount = new BigDecimal(genericTransDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;

            directAuthorizationRequest.setAmount(amount.intValue());

            if(gatewayAccount.getCurrency() ==null || gatewayAccount.getCurrency().equals(""))
            {
                PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"processSale()",null,"common","Currency not configured while placing the transaction,AccountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Currency not configured while placing the transaction,AccountId::"+accountId,new Throwable("Currency not configured while placing the transaction,AccountId::"+accountId));
            }
            directAuthorizationRequest.setCurrency(gatewayAccount.getCurrency());

            //Getting Address Details
            GenericAddressDetailsVO genericAddressDetailsVO = ugsPayRequestVO.getAddressDetailsVO();

            if(genericAddressDetailsVO.getIp() ==null || genericAddressDetailsVO.getIp().equals(""))
            {
                 directAuthorizationRequest.setCustomerIP("127.0.0.1");         //if no ip available
            }
            else
            {
             directAuthorizationRequest.setCustomerIP(genericAddressDetailsVO.getIp());
            }

            directAuthorizationRequest.setCardHolderAddress(genericAddressDetailsVO.getStreet());
            directAuthorizationRequest.setCardHolderZipcode(genericAddressDetailsVO.getZipCode());
            directAuthorizationRequest.setCardHolderCity(genericAddressDetailsVO.getCity());
            directAuthorizationRequest.setCardHolderState(genericAddressDetailsVO.getState());
            directAuthorizationRequest.setCardHolderCountryCode(genericAddressDetailsVO.getCountry());
            directAuthorizationRequest.setCardHolderPhone(genericAddressDetailsVO.getPhone());
            directAuthorizationRequest.setCardHolderEmail(genericAddressDetailsVO.getEmail());

            //Getting Card Details
            GenericCardDetailsVO genericCardDetailsVO = ugsPayRequestVO.getCardDetailsVO();

            directAuthorizationRequest.setCardHolderName(genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
            directAuthorizationRequest.setCardNumber(genericCardDetailsVO.getCardNum());
            directAuthorizationRequest.setCardSecurityCode(genericCardDetailsVO.getcVV());
            directAuthorizationRequest.setCardExpireMonth(genericCardDetailsVO.getExpMonth());
            directAuthorizationRequest.setCardExpireYear(genericCardDetailsVO.getExpYear());


            directAuthorizationRequest.setAVSPolicy("");       //Depricated
            directAuthorizationRequest.setFSPolicy("");        //Not to be used as of now

            //directAuthorizationRequest.setSecure3DAcsMessage(); //To be used later in case of 3d Secure
            directAuthorizationRequest.setSecure3DCheckTransactionID(0);

            //get binding stub based on end point URL (for UGSpay and Fort7Pay)
            InterfaceBindingStub UGSBindingStub = getInterfaceBindingStub(ugsPayAccount.getWebsiteurl());

            AuthorizationResponse response = null;
            try
            {
                response = UGSBindingStub.saleTransaction(directAuthorizationRequest);
            }
            catch(RemoteException re)
            {
                 PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception while connecting to UGS",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
            }

            if(response!=null)
            {
            ugsPayResponseVO = new UGSPayResponseVO();
            ugsPayResponseVO.setTransactionId(new String(response.getTransactionID()+""));
            ugsPayResponseVO.setMerchantOrderId(response.getOrderID());

            ugsPayResponseVO.setTransactionStatus(response.getStatus());
                if("000".equals(response.getErrorcode()))
                {
                    ugsPayResponseVO.setStatus("success");
                }
                else
                {
                    ugsPayResponseVO.setStatus("fail");
                }
            ugsPayResponseVO.setErrorCode(response.getErrorcode());
            ugsPayResponseVO.setDescription(response.getErrormessage());
            ugsPayResponseVO.setDescriptor(response.getStatementDescriptor());
            ugsPayResponseVO.setFSResult(response.getFSResult());
            ugsPayResponseVO.setFSStatus(response.getFSStatus());
            }

            return ugsPayResponseVO;
        }



    /**
     * Check the input values (to be updated later for 3D Secure case )
     * @param trackingID
     * @param requestVO
     * @throws SystemError
     */
    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
             PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }


        if(requestVO ==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }

        UGSPayRequestVO  ugsPayRequestVO = (UGSPayRequestVO)requestVO;

        GenericTransDetailsVO genericTransDetailsVO = ugsPayRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO = ugsPayRequestVO.getAddressDetailsVO();
        if(genericAddressDetailsVO ==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getCountry() ==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
        }
         if(genericAddressDetailsVO.getPhone() ==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
        }
         if(genericAddressDetailsVO.getEmail() ==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Email ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email ID not provided while placing transaction",new Throwable("Email ID not provided while placing transaction"));
        }

         if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Last Name not provided while placing transaction",new Throwable("Last Name not provided while placing transaction"));

        }

        GenericCardDetailsVO genericCardDetailsVO = ugsPayRequestVO.getCardDetailsVO();
        if(genericCardDetailsVO==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
       /* if(genericCardDetailsVO.getCardHolderName() ==null || genericCardDetailsVO.getCardHolderName().equals(""))
        {
               log.info("card holder name input not provided");
              throw new SystemError("Card Holder name input not provided");
        }*/
        if(genericCardDetailsVO.getCardNum() ==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpMonth() ==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpYear() ==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }

        if(genericCardDetailsVO.getcVV() ==null || genericCardDetailsVO.getcVV().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }




    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO)
        {
            return null;
        }



    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
        public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
        {


             log.info("  Inside   processAuthentication  ::::::::");
             transactionLogger.info("  Inside   processAuthentication  ::::::::");


            validateForSale(trackingID, requestVO);    //validating manadatory parameters


            UGSPayRequestVO  ugsPayRequestVO = (UGSPayRequestVO)requestVO;
            UGSPayResponseVO ugsPayResponseVO = null;
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            UGSPayAccount ugsPayAccount  = getUGSPayAccount(accountId);

            DirectAuthorizationRequest directAuthorizationRequest  = new DirectAuthorizationRequest();

            directAuthorizationRequest.setWebsiteID(ugsPayAccount.getWebsiteid());
            directAuthorizationRequest.setPassword(ugsPayAccount.getPassword());
            directAuthorizationRequest.setOrderID(trackingID);

            //Getting Transaction Details
            GenericTransDetailsVO genericTransDetailsVO = ugsPayRequestVO.getTransDetailsVO();

           //Amount in minor unit, e.g. cents; 100 dollar cent equals to 1 dollar
            BigDecimal amount = new BigDecimal(genericTransDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;

            directAuthorizationRequest.setAmount(amount.intValue());

            if(gatewayAccount.getCurrency() ==null || gatewayAccount.getCurrency().equals(""))
            {
                 PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"processAuthentication()",null,"common","Currency not configured while Authenticating the transaction,AccountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Currency not configured while placing the transaction,AccountId::"+accountId,new Throwable("Currency not configured while placing the transaction,AccountId::"+accountId));
            }
            directAuthorizationRequest.setCurrency(gatewayAccount.getCurrency());

            //Getting Address Details
            GenericAddressDetailsVO genericAddressDetailsVO = ugsPayRequestVO.getAddressDetailsVO();

            if(genericAddressDetailsVO.getIp() ==null || genericAddressDetailsVO.getIp().equals(""))
            {
                 directAuthorizationRequest.setCustomerIP("127.0.0.1");         //if no ip available
            }
            else
            {
             directAuthorizationRequest.setCustomerIP(genericAddressDetailsVO.getIp());
            }

            directAuthorizationRequest.setCardHolderAddress(genericAddressDetailsVO.getStreet());
            directAuthorizationRequest.setCardHolderZipcode(genericAddressDetailsVO.getZipCode());
            directAuthorizationRequest.setCardHolderCity(genericAddressDetailsVO.getCity());
            directAuthorizationRequest.setCardHolderState(genericAddressDetailsVO.getState());
            directAuthorizationRequest.setCardHolderCountryCode(genericAddressDetailsVO.getCountry());
            directAuthorizationRequest.setCardHolderPhone(genericAddressDetailsVO.getPhone());
            directAuthorizationRequest.setCardHolderEmail(genericAddressDetailsVO.getEmail());

            //Getting Card Details
            GenericCardDetailsVO genericCardDetailsVO = ugsPayRequestVO.getCardDetailsVO();

            directAuthorizationRequest.setCardHolderName(genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
            directAuthorizationRequest.setCardNumber(genericCardDetailsVO.getCardNum());
            directAuthorizationRequest.setCardSecurityCode(genericCardDetailsVO.getcVV());
            directAuthorizationRequest.setCardExpireMonth(genericCardDetailsVO.getExpMonth());
            directAuthorizationRequest.setCardExpireYear(genericCardDetailsVO.getExpYear());


            //directAuthorizationRequest.setAVSPolicy();       //Depricated
            //directAuthorizationRequest.setFSPolicy();        //Not to be used as of now

            //directAuthorizationRequest.setSecure3DAcsMessage(); //To be used later in case of 3d Secure
            directAuthorizationRequest.setSecure3DCheckTransactionID(0);

            //get binding stub based on end point URL (for UGSpay and Fort7Pay)
            InterfaceBindingStub UGSBindingStub = getInterfaceBindingStub(ugsPayAccount.getWebsiteurl());

            AuthorizationResponse response = null;
            try
            {
                response = UGSBindingStub.authorizeTransaction(directAuthorizationRequest);
            }
            catch(RemoteException re)
            {
                 PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"processAuthentication()",null,"common","Technical Exception while connecting to UGS",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
            }

            if(response!=null)
            {
            ugsPayResponseVO = new UGSPayResponseVO();
            ugsPayResponseVO.setTransactionId(new String(response.getTransactionID()+""));
            ugsPayResponseVO.setMerchantOrderId(response.getOrderID());

            ugsPayResponseVO.setTransactionStatus(response.getStatus());
            ugsPayResponseVO.setErrorCode(response.getErrorcode());
                if("000".equals(response.getErrorcode()))
                {
                    ugsPayResponseVO.setStatus("success");
                }
                else
                {
                    ugsPayResponseVO.setStatus("fail");
                }
            ugsPayResponseVO.setDescription(response.getErrormessage());
            ugsPayResponseVO.setDescriptor(response.getStatementDescriptor());
            ugsPayResponseVO.setFSResult(response.getFSResult());
            ugsPayResponseVO.setFSStatus(response.getFSStatus());
            }

            return ugsPayResponseVO;
        }

        public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO)
        {
            return null;
        }


        /**
         *
         * @param trackingID
         * @param requestVO
         * @return
         * @throws SystemError
         */
        public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
        {

            log.info("  Inside   processRefund  ::::::::");
            transactionLogger.info("  Inside   processRefund  ::::::::");

            validateForRefund(trackingID, requestVO);    //validating manadatory parameters


            UGSPayRequestVO  ugsPayRequestVO = (UGSPayRequestVO)requestVO;
            UGSPayResponseVO ugsPayResponseVO = null;
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            UGSPayAccount ugsPayAccount  = getUGSPayAccount(accountId);

            ReferralDetailedRequest referralDetailedRequest  = new ReferralDetailedRequest();

            referralDetailedRequest.setWebsiteID(ugsPayAccount.getWebsiteid());
            referralDetailedRequest.setPassword(ugsPayAccount.getPassword());
            referralDetailedRequest.setTransactionID(ugsPayRequestVO.getUGSTransId());

            //Amount in minor unit, e.g. cents; 100 dollar cent equals to 1 dollar
            BigDecimal amount = new BigDecimal(ugsPayRequestVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            referralDetailedRequest.setAmount(amount.intValue());

            //get binding stub based on end point URL (for UGSpay and Fort7Pay)
            InterfaceBindingStub UGSBindingStub = getInterfaceBindingStub(ugsPayAccount.getWebsiteurl());

            Response response = null;
            try
            {
                response = UGSBindingStub.refundTransaction(referralDetailedRequest);
            }
            catch(RemoteException re)
            {
                 PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"processRefund()",null,"common","Technical Exception while connecting to UGS",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
            }

            if(response!=null)
            {
            ugsPayResponseVO = new UGSPayResponseVO();
            ugsPayResponseVO.setTransactionId(new String (response.getTransactionID()+""));

            ugsPayResponseVO.setTransactionStatus(response.getStatus());
                if("000".equals(response.getErrorcode()))
                {
                    ugsPayResponseVO.setStatus("success");
                }
                else
                {
                    ugsPayResponseVO.setStatus("fail");
                }
            ugsPayResponseVO.setErrorCode(response.getErrorcode());
            ugsPayResponseVO.setDescription(response.getErrormessage());
            }

            return ugsPayResponseVO;

        }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {

        if(requestVO ==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }

        UGSPayRequestVO  ugsPayRequestVO = (UGSPayRequestVO)requestVO;

        if(ugsPayRequestVO.getAmount() == null || ugsPayRequestVO.getAmount().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        if(ugsPayRequestVO.getUGSTransId()==0)
        {
            PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"validateForRefund()",null,"common","UGS TransactionID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"UGS TransactionID not provided while placing transaction",new Throwable("UGS TransactionID not provided while placing transaction"));
        }


    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
            log.info("  Inside   processCapture  ::::::::");
            transactionLogger.info("  Inside   processCapture  ::::::::");

            validateForRefund(trackingID, requestVO);    //validating manadatory parameters


            UGSPayRequestVO  ugsPayRequestVO = (UGSPayRequestVO)requestVO;
            UGSPayResponseVO ugsPayResponseVO = null;
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            UGSPayAccount ugsPayAccount  = getUGSPayAccount(accountId);

            ReferralDetailedRequest referralDetailedRequest  = new ReferralDetailedRequest();

            referralDetailedRequest.setWebsiteID(ugsPayAccount.getWebsiteid());
            referralDetailedRequest.setPassword(ugsPayAccount.getPassword());
            referralDetailedRequest.setTransactionID(ugsPayRequestVO.getUGSTransId());

            //Amount in minor unit, e.g. cents; 100 dollar cent equals to 1 dollar
            BigDecimal amount = new BigDecimal(ugsPayRequestVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            referralDetailedRequest.setAmount(amount.intValue());

            //get binding stub based on end point URL (for UGSpay and Fort7Pay)
            InterfaceBindingStub UGSBindingStub = getInterfaceBindingStub(ugsPayAccount.getWebsiteurl());

            Response response = null;
            try
            {
                response = UGSBindingStub.captureTransaction(referralDetailedRequest);
            }
            catch(RemoteException re)
            {
                 PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"processCapture()",null,"common","Technical Exception while Capturing",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
            }

            if(response!=null)
            {
            ugsPayResponseVO = new UGSPayResponseVO();
            ugsPayResponseVO.setTransactionId(new String (response.getTransactionID()+""));

            ugsPayResponseVO.setTransactionStatus(response.getStatus());
            ugsPayResponseVO.setErrorCode(response.getErrorcode());
                if("000".equals(response.getErrorcode()))
                {
                    ugsPayResponseVO.setStatus("success");
                }
                else
                {
                    ugsPayResponseVO.setStatus("fail");
                }
            ugsPayResponseVO.setDescription(response.getErrormessage());
            }

            return ugsPayResponseVO;
        }

        public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
        {
             log.info("  Inside   processQuery  ::::::::");
             transactionLogger.info("  Inside   processQuery  ::::::::");

            if(trackingID ==null || trackingID.equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UGSPaymentGateway.class.getName(),"processQuery()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
            }


            UGSPayRequestVO  ugsPayRequestVO = (UGSPayRequestVO)requestVO;
            UGSPayResponseVO ugsPayResponseVO = null;
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            UGSPayAccount ugsPayAccount  = getUGSPayAccount(accountId);

            OrderStatusRequest orderStatusRequest  = new OrderStatusRequest();

            orderStatusRequest.setWebsiteID(new String (ugsPayAccount.getWebsiteid()+""));
            orderStatusRequest.setPassword(ugsPayAccount.getPassword());
            orderStatusRequest.setOrderID(trackingID);


            //get binding stub based on end point URL (for UGSpay and Fort7Pay)
            InterfaceBindingStub UGSBindingStub = getInterfaceBindingStub(ugsPayAccount.getWebsiteurl());

            OrderStatusResponse response = null;
            try
            {
                response = UGSBindingStub.orderStatus(orderStatusRequest);
            }
            catch(RemoteException re)
            {
                 PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"processQuery()",null,"common","Technical Exception while Querying to UGS",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
            }

            if(response!=null)
            {
            ugsPayResponseVO = new UGSPayResponseVO();
            ugsPayResponseVO.setResult(response.getResult());
            ugsPayResponseVO.setErrorCode(response.getErrorcode());
            ugsPayResponseVO.setDescription(response.getErrormessage());
            ugsPayResponseVO.setTransactionId(response.getTransactionID());
            ugsPayResponseVO.setTransactionErrorcode(response.getTransactionErrorcode());
            ugsPayResponseVO.setTransactionErrormessage(response.getTransactionErrormessage());
            ugsPayResponseVO.setResponseTime(response.getTransactionDate());
            }

            return ugsPayResponseVO;
        }

        public GenericResponseVO process3DSecureCheck(String trackingID, GenericRequestVO requestVO)
        {
            return null;
        }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }




    private InterfaceBindingStub getInterfaceBindingStub(String endPointAddress) throws PZTechnicalViolationException
    {
        com.directi.pg.core.ugspay.message.InterfaceBindingStub binding = null;
        try {

            UGSInterfaceLocator ugsInterfaceLocator  = new UGSInterfaceLocator();
            ugsInterfaceLocator.setInterfacePortEndpointAddress(endPointAddress);
            binding = (com.directi.pg.core.ugspay.message.InterfaceBindingStub)ugsInterfaceLocator.getInterfacePort();
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"getInterfaceBindingStub()",null,"common","Service Exception while connecting to UGS",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return binding;
    }

    public static void loadUGSPayAccounts() throws PZDBViolationException
    {
        log.info("Loading UGSPay Accounts......");
        ugspayAccounts = new Hashtable<String, UGSPayAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_ugspay", conn);
            while (rs.next())
            {
                UGSPayAccount account = new UGSPayAccount(rs);
                ugspayAccounts.put(account.getAccountId() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(UGSPaymentGateway.class.getName(),"loadUGSPayAccounts()",null,"common","Sql Exception while loading gateway accounts details of UGS", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(UGSPaymentGateway.class.getName(), "loadUGSPayAccounts()", null, "common", "System Error while loading gateway accounts details of UGS", PZDBExceptionEnum.SQL_EXCEPTION,null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

     public static UGSPayAccount getUGSPayAccount(String accountId)
    {
        return ugspayAccounts.get(accountId);
    }
    private Gold24BindingStub getCommBindingStub() throws PZTechnicalViolationException
    {
        Gold24BindingStub gold24BindingStub = null;

        try
        {
            gold24BindingStub = (Gold24BindingStub) new Gold24ServiceLocator().getInterfacePort();

        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(UGSPaymentGateway.class.getName(),"getCommBindingStub()",null,"common","Service Exception while getting InterfacePort of UGS ",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return gold24BindingStub;
    }

}
