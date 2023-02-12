package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesBindingStub;
import com.directi.pg.core.mymonedero.message.com.wavecrest.www.B2BPayService.ServicesServiceLocator;
import com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.*;
import com.directi.pg.core.mymonedero.MyMonederoAccount;
import com.directi.pg.core.valueObjects.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.Util;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Hashtable;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Feb 16, 2013
 * Time: 7:09:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoPaymentGateway extends AbstractPaymentGateway
{

     private static Hashtable<String, MyMonederoAccount> myMonederoAccounts;
     private static Logger log=new Logger(MyMonederoPaymentGateway.class.getName());
     private static TransactionLogger transactionLogger=new TransactionLogger(MyMonederoPaymentGateway.class.getName());

     public static final String GATEWAY_TYPE = "MyMonedero";



     static
    {
        try
        {
            loadMyMonederoAccounts();

        }
        catch (PZDBViolationException e)
        {
            log.error("Exception while loading gateway accounts from MyMonedero Gateway Accounts",e);
            transactionLogger.error("Exception while loading gateway accounts from MyMonedero Gateway Accounts",e);

            PZExceptionHandler.handleDBCVEException(e,"MyMoneDero", PZOperations.MYMONEDERO_LAOD_ACCOUNT);
        }

    }



    /**
     *
     * @param accountId
     * @throws SystemError
     */
    public MyMonederoPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }


    public MyMonederoPaymentGateway()
    {

    }

    /**
     *
     * @throws SystemError
     * @throws RemoteException
     */
    public void testPaymentStatus() throws PZTechnicalViolationException
    {

        ServicesBindingStub MyMonederoBindingStub = getMyMonederoBindingStub(null);
        GetPaymentStatusRequest getPaymentStatusRequest = new GetPaymentStatusRequest();
        //setParams

        try
        {
            GetPaymentStatusResponse paymentStatusResponse = MyMonederoBindingStub.getPaymentStatus(getPaymentStatusRequest);
        }
        catch (RemoteException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"testPaymentStatus()",null,"common","Remote Exception while placing transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

    }

   /**
    *
    * @throws SystemError
    * @throws RemoteException
    */

    public void testRedirectUrl() throws PZTechnicalViolationException
    {

        ServicesBindingStub MyMonederoBindingStub = getMyMonederoBindingStub(null);
        RedirectUrlRequest redirectUrlRequest = new RedirectUrlRequest();
        //setParams

        try
        {
            RedirectUrlResponse redirectUrlResponse = MyMonederoBindingStub.redirectUrl(redirectUrlRequest);
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"testRedirectUrl()",null,"common","Remote Exception while placing transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

    }


    private ServicesBindingStub getMyMonederoBindingStub(String endpoint) throws PZTechnicalViolationException
    {
        ServicesBindingStub MyMonederoBindingStub = null;

        try

        {   ServicesServiceLocator  servicesServiceLocator  = new ServicesServiceLocator();
            if(endpoint!=null)
            {
            servicesServiceLocator.setservicesPortEndpointAddress(endpoint);
            }
            MyMonederoBindingStub = (ServicesBindingStub) servicesServiceLocator.getservicesPort();

        }
        catch (ServiceException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"getMyMonederoBindingStub()",null,"common","Service Exception while placing transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return MyMonederoBindingStub;

    }

    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZDBViolationException, PZTechnicalViolationException
    {
        log.debug("Inside processSale");
        transactionLogger.debug("Inside processSale");



        MyMonederoResponseVO MyMonederoResponseVO=new MyMonederoResponseVO();

        //validating mandatory parameter
        validateForSale(trackingID,requestVO);

        try
        {
            loadMyMonederoAccounts();

            MyMonederoRequestVO MyMonederoRequestVO=(MyMonederoRequestVO) requestVO;
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            MyMonederoAccount myMonederoAccount=getMyMonederoAccount(accountId);
            // set up merchant and sub merchant details
            MerchantCredentials merchantCredentials=new MerchantCredentials(myMonederoAccount.getMerchantid(),myMonederoAccount.getMerchantpass(),myMonederoAccount.getAccesskey());
            SubMerchantCredentials subMerchantCredentials=new SubMerchantCredentials(myMonederoAccount.getSubmerchantid(),myMonederoAccount.getSubmerchantpass());
            
            
            
            //create request
            RedirectUrlRequest redirectUrlRequest=new RedirectUrlRequest();
           //setting merchant and sub merchant details 
            redirectUrlRequest.setMerchant(merchantCredentials);
            redirectUrlRequest.setSubMerchant(subMerchantCredentials);
            //setting details
            redirectUrlRequest.setAffiliateId(MyMonederoRequestVO.getMemberid());


            redirectUrlRequest.setTransactionAmount(MyMonederoRequestVO.getGenericTransDetailsVO().getAmount());
            if(gatewayAccount.getCurrency() ==null || gatewayAccount.getCurrency().equals(""))
            {
                 PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"processSale()",null,"common","Currecy not configured while placing transaction,AccountID:::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Currecy not configured while placing transaction,AccountID:::"+accountId,new Throwable("Currecy not configured while placing transaction,AccountID:::"+accountId));
            }
            redirectUrlRequest.setTransactionCurrency(gatewayAccount.getCurrency());
            redirectUrlRequest.setTransactionDescription(MyMonederoRequestVO.getGenericTransDetailsVO().getOrderId());
            redirectUrlRequest.setMerchantTnxId(trackingID);
            
            redirectUrlRequest.setSecondaryAuthorization("2");
            redirectUrlRequest.setTransactionType("RECEIVE");
            redirectUrlRequest.setLocalDate(new Date());
            redirectUrlRequest.setCancelUrl(MyMonederoRequestVO.getUrl());
            redirectUrlRequest.setReturnlUrl(MyMonederoRequestVO.getUrl());


            //Print Request
            /*log.debug("Merchant Username  :"+merchantCredentials.getId());
            log.debug("Merchant Password  :"+merchantCredentials.getPassword());
            log.debug("Merchant Key       :"+merchantCredentials.getAccessKey());
            log.debug("Sub Merchant       :"+subMerchantCredentials.getId());
            log.debug("Sub Merchant Password:"+subMerchantCredentials.getPassword());
            log.debug("Affiliate ID :"+redirectUrlRequest.getAffiliateId());
            log.debug("Trans Amount   :"+redirectUrlRequest.getTransactionAmount());
            log.debug("Trans Currency :"+redirectUrlRequest.getTransactionCurrency());
            log.debug("Trans Description :"+redirectUrlRequest.getTransactionDescription());
            log.debug("MerchantTnxId :"+redirectUrlRequest.getMerchantTnxId());
            log.debug("Secondary Authorization :"+redirectUrlRequest.getSecondaryAuthorization());
            log.debug("Transaction Type :"+redirectUrlRequest.getTransactionType());
            log.debug("Cancel URL :"+redirectUrlRequest.getCancelUrl());
            log.debug("Return URL :"+redirectUrlRequest.getReturnlUrl());
            log.debug("Calling the service Redirect URL ");*/

            //get binding stub
            ServicesBindingStub MyMonederoBindingStub = getMyMonederoBindingStub(myMonederoAccount.getEndpoint());
            //get response
            RedirectUrlResponse redirectUrlResponse = MyMonederoBindingStub.redirectUrl(redirectUrlRequest);

            //set in response vo

            log.debug("Error ---"+redirectUrlResponse.getErrorCode());
            transactionLogger.debug("Error ---"+redirectUrlResponse.getErrorCode());
            log.debug("Status---"+redirectUrlResponse.getTransactionStatus());
            transactionLogger.debug("Status---"+redirectUrlResponse.getTransactionStatus());
            log.debug("Tracking ID--"+redirectUrlResponse.getMerchantTnxId());
            transactionLogger.debug("Tracking ID--"+redirectUrlResponse.getMerchantTnxId());
            log.debug("WCTXNID--"+redirectUrlResponse.getWcTxnId());
            transactionLogger.debug("WCTXNID--"+redirectUrlResponse.getWcTxnId());
            log.debug("redirecturl--"+redirectUrlResponse.getRedirectUrl());
            transactionLogger.debug("redirecturl--"+redirectUrlResponse.getRedirectUrl());

            MyMonederoResponseVO.setError(redirectUrlResponse.getErrorCode());
            MyMonederoResponseVO.setWctxnid(redirectUrlResponse.getWcTxnId());
            MyMonederoResponseVO.setStatus(redirectUrlResponse.getTransactionStatus());
            MyMonederoResponseVO.setTrackingid(redirectUrlResponse.getMerchantTnxId());
            MyMonederoResponseVO.setRedirecturl(""+redirectUrlResponse.getRedirectUrl());
            

            
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"processSale()",null,"common","Remote Exception while placing transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }


        return MyMonederoResponseVO;
    }



    /**
     * Check the input values
     * @param trackingID
     * @param requestVO
     * @throws SystemError
     */
    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        if(trackingID ==null || trackingID.equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }


        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"Request  not provided while placing transaction",new Throwable("Request Vo not provided while placing transaction"));
        }

        MyMonederoRequestVO  myMonederoRequestVO = (MyMonederoRequestVO)requestVO;

        GenericTransDetailsVO genericTransDetailsVO = myMonederoRequestVO.getGenericTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        if(genericTransDetailsVO.getOrderId() == null || genericTransDetailsVO.getOrderId().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForSale()",null,"common","Order Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Order Id not provided while placing transaction",new Throwable("Order Id not provided while placing transaction"));
        }

         if(myMonederoRequestVO.getUrl() == null || myMonederoRequestVO.getUrl().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REDIRECT_URL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForSale()",null,"common","URL not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"URL not provided while placing transaction",new Throwable("URL not provided while placing transaction"));
        }


    }


    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
     public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
     {
        log.debug("Inside processRefund");
        transactionLogger.debug("Inside processRefund");

        MyMonederoResponseVO MyMonederoResponseVO=new MyMonederoResponseVO();

        //validating mandatory parameter
        validateForRefund(trackingID,requestVO);

        try
        {
            MyMonederoRequestVO MyMonederoRequestVO=(MyMonederoRequestVO) requestVO;
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            MyMonederoAccount myMonederoAccount=getMyMonederoAccount(accountId);
            // set up merchant and sub merchant details
            MerchantCredentials merchantCredentials=new MerchantCredentials(myMonederoAccount.getMerchantid(),myMonederoAccount.getMerchantpass(),myMonederoAccount.getAccesskey());
            SubMerchantCredentials subMerchantCredentials=new SubMerchantCredentials(myMonederoAccount.getSubmerchantid(),myMonederoAccount.getSubmerchantpass());



            //create request
            RedirectUrlRequest redirectUrlRequest=new RedirectUrlRequest();
           //setting merchant and sub merchant details
            redirectUrlRequest.setMerchant(merchantCredentials);
            redirectUrlRequest.setSubMerchant(subMerchantCredentials);
            //setting details
            redirectUrlRequest.setAffiliateId(MyMonederoRequestVO.getMemberid());


            redirectUrlRequest.setTransactionAmount(MyMonederoRequestVO.getGenericTransDetailsVO().getAmount());
            if(gatewayAccount.getCurrency() ==null || gatewayAccount.getCurrency().equals(""))
            {
                 PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"processRefund()",null,"common","Currency not configured while Refunding transaction,accountID::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Currency not configured while Refunding transaction,accountID::"+accountId,new Throwable("Currency not configured while Refunding transaction,accountID::"+accountId));
            }
            redirectUrlRequest.setTransactionCurrency(gatewayAccount.getCurrency());
            redirectUrlRequest.setTransactionDescription(MyMonederoRequestVO.getGenericTransDetailsVO().getOrderId());
            redirectUrlRequest.setMerchantTnxId(trackingID);

            redirectUrlRequest.setSecondaryAuthorization("0");
            redirectUrlRequest.setTransactionType("SEND");
            redirectUrlRequest.setLocalDate(new Date());

            redirectUrlRequest.setReceiverUserid(MyMonederoRequestVO.getUserId());

            //Print Request
            /*log.debug("Merchant Username  :"+merchantCredentials.getId());
            log.debug("Merchant Password  :"+merchantCredentials.getPassword());
            log.debug("Merchant Key       :"+merchantCredentials.getAccessKey());
            log.debug("Sub Merchant       :"+subMerchantCredentials.getId());
            log.debug("Sub Merchant Password:"+subMerchantCredentials.getPassword());
            log.debug("Affiliate ID :"+redirectUrlRequest.getAffiliateId());
            log.debug("Trans Amount   :"+redirectUrlRequest.getTransactionAmount());
            log.debug("Trans Currency :"+redirectUrlRequest.getTransactionCurrency());
            log.debug("Trans Description :"+redirectUrlRequest.getTransactionDescription());
            log.debug("MerchantTnxId :"+redirectUrlRequest.getMerchantTnxId());
            log.debug("Secondary Authorization :"+redirectUrlRequest.getSecondaryAuthorization());
            log.debug("Transaction Type :"+redirectUrlRequest.getTransactionType());
            log.debug("Calling the service Redirect URL ");*/

            //get binding stub
            ServicesBindingStub MyMonederoBindingStub = getMyMonederoBindingStub(myMonederoAccount.getEndpoint());
            //get response
            RedirectUrlResponse redirectUrlResponse = MyMonederoBindingStub.redirectUrl(redirectUrlRequest);

            //set in response vo

            log.debug("Error ---"+redirectUrlResponse.getErrorCode());
            transactionLogger.debug("Error ---"+redirectUrlResponse.getErrorCode());
            log.debug("Status---"+redirectUrlResponse.getTransactionStatus());
            transactionLogger.debug("Status---"+redirectUrlResponse.getTransactionStatus());
            log.debug("Tracking ID--"+redirectUrlResponse.getMerchantTnxId());
            transactionLogger.debug("Tracking ID--"+redirectUrlResponse.getMerchantTnxId());
            log.debug("WCTXNID--"+redirectUrlResponse.getWcTxnId());
            transactionLogger.debug("WCTXNID--"+redirectUrlResponse.getWcTxnId());
            log.debug("redirecturl--"+redirectUrlResponse.getRedirectUrl());
            transactionLogger.debug("redirecturl--"+redirectUrlResponse.getRedirectUrl());

            MyMonederoResponseVO.setError(redirectUrlResponse.getErrorCode());
            MyMonederoResponseVO.setWctxnid(redirectUrlResponse.getWcTxnId());
            MyMonederoResponseVO.setStatus(redirectUrlResponse.getTransactionStatus());
            MyMonederoResponseVO.setTrackingid(redirectUrlResponse.getMerchantTnxId());
            MyMonederoResponseVO.setRedirecturl(""+redirectUrlResponse.getRedirectUrl());



        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"processRefund()",null,"common","Remote Exception while refunding transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }


         return MyMonederoResponseVO;
    }

    /**
        * Check the input values
        * @param trackingID
        * @param requestVO
        * @throws SystemError
        */
       private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
       {
           if(trackingID ==null || trackingID.equals(""))
           {
                PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Tracking Id not provided while Refunding transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null, "Tracking id not provided while Refunding transaction", new Throwable("Tracking id not provided while Refunding transaction"));
           }


           if(requestVO ==null)
           {
                PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Request  not provided while Refunding transaction", PZConstraintExceptionEnum.VO_MISSING,null, "Request  not provided while Refunding transaction", new Throwable("Request  not provided while Refunding transaction"));
           }

           MyMonederoRequestVO  myMonederoRequestVO = (MyMonederoRequestVO)requestVO;

           GenericTransDetailsVO genericTransDetailsVO = myMonederoRequestVO.getGenericTransDetailsVO();
           if(genericTransDetailsVO ==null)
           {
                PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(), "validateForRefund()", null, "common", "TransactionDetails  not provided while Refunding transaction", PZConstraintExceptionEnum.VO_MISSING,null, "TransactionDetails  not provided while Refunding transaction", new Throwable("TransactionDetails  not provided while Refunding transaction"));
           }
           if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
           {
              PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Amount not provided while Refunding transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null, "Amount not provided while Refunding transaction", new Throwable("Amount not provided while Refunding transaction"));
           }

           if(genericTransDetailsVO.getOrderId() == null || genericTransDetailsVO.getOrderId().equals(""))
           {
              PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Order Id not provided while Refunding transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null, "Order id not provided while Refunding transaction", new Throwable("Order id not provided while Refunding transaction"));
           }

            if(myMonederoRequestVO.getUserId() == null || myMonederoRequestVO.getUserId().equals(""))
           {
              PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(), "validateForRefund()", null, "common", "USER ID not provided while Refunding transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null, "USER ID not provided while Refunding transaction", new Throwable("USER ID not provided while Refunding transaction"));
           }


       }


    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {

        log.debug("Inside processQuery");
        transactionLogger.debug("Inside processQuery");

        MyMonederoResponseVO responseVO=new MyMonederoResponseVO();

        //validating mandatory parameter
        validateForQuery(trackingID,requestVO);
        try{
    
             MyMonederoRequestVO MyMonederoRequestVO=(MyMonederoRequestVO) requestVO;

            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            MyMonederoAccount myMonederoAccount=getMyMonederoAccount(accountId);
            // set up merchant and sub merchant details
            MerchantCredentials merchantCredentials=new MerchantCredentials(myMonederoAccount.getMerchantid(),myMonederoAccount.getMerchantpass(),myMonederoAccount.getAccesskey());


    
            //create request
            GetPaymentStatusRequest gpstreq =new GetPaymentStatusRequest();
            gpstreq.setMerchant(merchantCredentials);
            gpstreq.setMerchantTnxId(trackingID);
            gpstreq.setWcTxnId(MyMonederoRequestVO.getWctxnid());
            gpstreq.setTransactionAmount(MyMonederoRequestVO.getGenericTransDetailsVO().getAmount());
            if(gatewayAccount.getCurrency() ==null || gatewayAccount.getCurrency().equals(""))
            {
                 PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(), "processQuery()", null, "Common", "Cuurecy not configured wile Querying transaction,accountId" + accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Cuurecy not configured wile Querying transaction,accountId" + accountId, new Throwable("Cuurecy not configured wile Querying transaction,accountId" + accountId));
            }

            gpstreq.setTransactionCurrency(gatewayAccount.getCurrency());
            gpstreq.setLocalDate(new Date());

            //get binding stub
            ServicesBindingStub MyMonederoBindingStub = getMyMonederoBindingStub(myMonederoAccount.getEndpoint());
    
            GetPaymentStatusResponse gpstresp=MyMonederoBindingStub.getPaymentStatus(gpstreq);
            
            responseVO.setError(gpstresp.getErrorCode());
            responseVO.setDestID(gpstresp.getDestinationAccount());
            responseVO.setSourceID(gpstresp.getSourceAccount());
            responseVO.setStatus(gpstresp.getTransactionStatus());
            responseVO.setTransactionDate(gpstresp.getTxnDate()+"");
            responseVO.setWctxnid(MyMonederoRequestVO.getWctxnid());
            responseVO.setTrackingid(trackingID);
        
            
            
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(MyMonederoPaymentGateway.class.getName(),"processQuery()",null,"common","Remote Exception while Querying transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }


        return responseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("MyMonederoPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    /**
            * Check the input values
            * @param trackingID
            * @param requestVO
            * @throws SystemError
            */
           private void validateForQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
           {

               if(requestVO ==null)
               {
                   PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForQuery()",null,"common","Request Vo not provided while Quering transction",PZConstraintExceptionEnum.VO_MISSING,null,"Request Vo not provided while Quering transction",new Throwable("Request Vo not provided while Quering transction"));
               }

               MyMonederoRequestVO  myMonederoRequestVO = (MyMonederoRequestVO)requestVO;

               GenericTransDetailsVO genericTransDetailsVO = myMonederoRequestVO.getGenericTransDetailsVO();
               if(genericTransDetailsVO ==null)
               {
                   PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForQuery()",null,"common","TransactionDetails  not provided while Quering transction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails Vo not provided while Quering transction",new Throwable("TransactionDetails Vo not provided while Quering transction"));
               }
               if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
               {
                  PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(),"validateForQuery()",null,"common","Amount not provided while Quering transction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Quering transction",new Throwable("Amount not provided while Quering transction"));
               }


                if(myMonederoRequestVO.getWctxnid() == null || myMonederoRequestVO.getWctxnid().equals(""))
               {
                  PZExceptionHandler.raiseConstraintViolationException(MyMonederoPaymentGateway.class.getName(), "validateForQuery()", null, "common", "WctXnID not provided while Quering transction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null, "WctXnID not provided while Quering transction", new Throwable("WctXnID not provided while Quering transction"));
               }


           }




    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void loadMyMonederoAccounts() throws PZDBViolationException
    {
        log.info("Loading MyMonedero Accounts......");
        transactionLogger.info("Loading MyMonedero Accounts......");
        myMonederoAccounts = new Hashtable<String, MyMonederoAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_mymonedero", conn);
            while (rs.next())
            {
                MyMonederoAccount account = new MyMonederoAccount(rs);
                myMonederoAccounts.put(account.getAccountId() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(MyMonederoPaymentGateway.class.getName(),"loadMyMonederoAccounts()",null,"common","SQLException while loading gateway accounts details of MyMonedero  from gateway_accounts_mymonedero", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(MyMonederoPaymentGateway.class.getName(), "loadMyMonederoAccounts()", null, "common", "System Error while loading gateway accounts details of MyMonedero  from gateway_accounts_mymonedero", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

     public static MyMonederoAccount getMyMonederoAccount(String accountId)
    {
        log.debug("Inside getMyMonederoAccount");
        return myMonederoAccounts.get(accountId);
    }



}
