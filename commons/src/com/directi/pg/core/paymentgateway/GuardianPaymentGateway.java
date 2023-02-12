package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.Guardian.*;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;

import javax.xml.rpc.ServiceException;
import java.io.*;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 7, 2012
 * Time: 9:30:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class GuardianPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(GuardianPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(GuardianPaymentGateway.class.getName());
    //Configuration
    public static final String GATEWAY_TYPE = "Guardian";

    public GuardianPaymentGateway(String accountId)
    {
         this.accountId = accountId;
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        log.info("Inside   GuardianPaymentGateway  ::::::::");
        log.info("Inside   processSale  ::::::::");

        validateForSale(trackingID, requestVO);

        CommResponseVO responseVO = new CommResponseVO();
        try {
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if(account.getMerchantId()==null || account.getMerchantId().equals(""))
            {
                log.info("MerchantId not configured");
                transactionLogger.info("MerchantId not configured");
                PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(),"processSale()",null,"common","Merchant Id not gonfigured for the accountId::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not gonfigured for the accountId::"+accountId,new Throwable("Merchant Id not gonfigured for the accountId::"+accountId));
            }

            String merchantId = account.getMerchantId();
            String merchantToken = getMerchantToken(accountId);

            CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
            GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
            GenericAddressDetailsVO genericAddressDetailsVO= comRequestVO.getAddressDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO= comRequestVO.getCardDetailsVO();

            CcgwwsdlBindingStub binding;

                binding = (CcgwwsdlBindingStub)new CcgwwsdlLocator().getccgwwsdlPort();


            // Time out after a minute
            binding.setTimeout(120000);

            // Test operation
            Response value = null;
            Request request = new Request();
            request.setMerchant_id(Integer.parseInt(merchantId));
            request.setMerchant_token(merchantToken);
            request.setHashkey(calculateHashkey(merchantId, merchantToken, trackingID));
            request.setTraceid(trackingID);
            request.setAmount(Float.parseFloat(genericTransDetailsVO.getAmount()));
            request.setCurrency(genericTransDetailsVO.getCurrency());
            request.setCustomer_firstname(genericAddressDetailsVO.getFirstname());
            request.setCustomer_lastname(genericAddressDetailsVO.getLastname());
            request.setCustomer_email(genericAddressDetailsVO.getEmail());
            request.setCustomer_phone(genericAddressDetailsVO.getPhone());
            request.setCustomer_ip(genericAddressDetailsVO.getIp());
            //request.setCustomer_ip("122.169.97.70");
            request.setBilling_street(genericAddressDetailsVO.getStreet());
            request.setBilling_postalcode(genericAddressDetailsVO.getZipCode());
            request.setBilling_city(genericAddressDetailsVO.getCity());
            request.setBilling_state(genericAddressDetailsVO.getState());
            request.setBilling_country(genericAddressDetailsVO.getCountry());
            request.setCc_number(genericCardDetailsVO.getCardNum());
            request.setCc_type("VISA");
            request.setCc_cardholder(genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
            request.setCc_ex_month(Integer.parseInt(genericCardDetailsVO.getExpMonth()));
            request.setCc_ex_year(Integer.parseInt(genericCardDetailsVO.getExpYear()));
            request.setCc_ccv(genericCardDetailsVO.getcVV());

            value = binding.newTransaction(request);

        if ("0".equals(value.getResult_code())) {
            responseVO.setStatus("success");
            responseVO.setDescription(value.getResult_text());
            responseVO.setTransactionId(value.getTransaction_id()+"");
            responseVO.setErrorCode(value.getResult_code());
        }
        else {
            responseVO.setStatus("fail");
        }

        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(), "validateForSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return responseVO;
    }

    /**
            *
            * @param trackingID
            * @param requestVO
            * @return
            * @throws com.directi.pg.SystemError
            */
           public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZDBViolationException, PZTechnicalViolationException
           {

               log.info("  Inside   GuardianPaymentGateway  ::::::::");
               log.info("  Inside   processRefund  ::::::::");

               validateForRefund(trackingID, requestVO);

               ResponseRefund value = null;

               CommResponseVO responseVO = new CommResponseVO();
               try {
                   GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                   if(account.getMerchantId()==null || account.getMerchantId().equals(""))
                   {
                       log.info("MerchantId not configured");
                       transactionLogger.info("MerchantId not configured");

                       PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(),"validateForRefund()",null,"common","Merchant Id not Configured while Refunding the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Mapping Id not Configured while Refunding the transaction",new Throwable("Mapping Id not Configured while Refunding the transaction"));
                   }

                   String merchantId = account.getMerchantId();
                   String merchantToken = getMerchantToken(accountId);

                   CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
                   CommTransactionDetailsVO transDetailsVO = comRequestVO.getTransDetailsVO();

                   CcgwwsdlBindingStub binding;

                       binding = (CcgwwsdlBindingStub)new CcgwwsdlLocator().getccgwwsdlPort();


                   // Time out after a minute
                   binding.setTimeout(120000);

                   RequestRefund request = new RequestRefund();
                   request.setMerchant_id(Integer.parseInt(merchantId));
                   request.setMerchant_token(merchantToken);
                   request.setTransaction_id(transDetailsVO.getPreviousTransactionId());
                   request.setHashkey(calculateHashkey(merchantId,merchantToken,transDetailsVO.getPreviousTransactionId()));
                   request.setComments("NA");
                   request.setAmount(Float.parseFloat(transDetailsVO.getAmount()));

                   value = binding.refund(request);

                   if ("0".equals(value.getResult_code())) {
                       responseVO.setStatus("success");
                       responseVO.setDescription(value.getResult_text());
                       responseVO.setTransactionId(value.getTransaction_id()+"");
                       responseVO.setErrorCode(value.getResult_code());
                       responseVO.setAmount(value.getAmount()+"");
                   }
                   else {
                       responseVO.setStatus("fail");
                   }

               }
               catch (ServiceException e)
               {
                   PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(),"processRefund()",null,"common","Technical Exception while Refunding transaction",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
               }
               catch (RemoteException e)
               {
                   PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(),"processRefund()",null,"common","Technical Exception while Refunding transaction",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
               }
               catch (NoSuchAlgorithmException e)
               {
                   PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(),"processRefund()",null,"common","Technical Exception while Refunding transaction",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
               }
               catch (UnsupportedEncodingException e)
               {
                   PZExceptionHandler.raiseTechnicalViolationException(GuardianPaymentGateway.class.getName(),"processRefund()",null,"common","Technical Exception while Refunding transaction",PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
               }

               return responseVO;

        }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("GuardianPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    private String calculateHashkey(String mid, String token, String traceId) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        String inputString = mid+token+traceId;
        byte[] bytesOfMessage = inputString.getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(bytesOfMessage);
        String hashKey = new String(getHexString(digest));
        return hashKey;
    }

    static String getHexString(byte[] raw) throws UnsupportedEncodingException {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "utf-8");
    }
    static final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2',	(byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7',	(byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c',	(byte) 'd', (byte) 'e', (byte) 'f' };


    /**
  * Check the input values (to be updated later for 3D Secure case )
  * @param trackingID
  * @param requestVO
  * @throws com.directi.pg.SystemError
  */
 private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
 {
     if(trackingID ==null || trackingID.equals(""))
     {
         PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracking Id not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Refunding the transaction",new Throwable("Tracking Id not provided while Refunding the transaction"));
     }

     if(requestVO ==null)
     {
         PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  not provided while Refunding the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Refunding the transaction",new Throwable("Request  not provided while Refunding the transaction"));
     }

     CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;



     CommTransactionDetailsVO commTransactionDetailsVO = comRequestVO.getTransDetailsVO();
     if(commTransactionDetailsVO ==null)
     {
         PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForRefund()",null,"common","TransactionDetails  not provided while Refunding the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while Refunding the transaction",new Throwable("TransactionDetails  not provided while Refunding the transaction"));
     }
     if(commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
     {
        PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Refunding the transaction",new Throwable("Amount not provided while Refunding the transaction"));
     }

     if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
     {
        PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transaction Id not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction ID not provided while Refunding the transaction",new Throwable("Previous Transaction ID not provided while Refunding the transaction"));
     }

 }

    /**
     * Check the input values (to be updated later for 3D Secure case )
     * @param trackingID
     * @param requestVO
     * @throws com.directi.pg.SystemError
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
           PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","TrackingId not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"TrackingId not provided while placing transaction",new Throwable("TrackingId not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }

        CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;



        GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= comRequestVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Last Name not provided while placing transaction",new Throwable("Last Name not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Email Id not provided while placing transaction",new Throwable("Email Id not provided while placing transaction"));
        }


        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Ip Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
        }


        //Address Details
       if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));
       }

       if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));
       }


       if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
       }

       if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"State not provided while placing transaction",new Throwable("State not provided while placing transaction"));
       }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Zip Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Zip Code not provided while placing transaction",new Throwable("Zip Code not provided while placing transaction"));
       }


        GenericCardDetailsVO genericCardDetailsVO= comRequestVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Card Number provided is invalid while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"TrackingId provided is Invalid while placing transaction",new Throwable("TrackingId provided is invalid while placing transaction"));
        }



        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }


        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }


        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(GuardianPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }

    }

    private String getMerchantToken (String accountId) throws PZDBViolationException
    {
        Connection con = null;
        String token = null;
        try
        {
            con = Database.getConnection();
            String sql = "select token from gateway_accounts_guardian where accountid =?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, accountId);
            token = null;
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                token = rs.getString("token");
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(GuardianPaymentGateway.class.getName(),"getMerchantToken()",null,"common","SQL exception while getting token from gateway_accounts_guardian table", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(GuardianPaymentGateway.class.getName(), "getMerchantToken()", null, "common", "System error while getting token from gateway_accounts_guardian table", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return token;
    }
}
