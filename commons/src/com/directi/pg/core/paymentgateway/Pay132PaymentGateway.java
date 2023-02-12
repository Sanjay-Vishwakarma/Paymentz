package com.directi.pg.core.paymentgateway;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.pay132.ReadXMLResponse;
import com.directi.pg.core.pay132.RequestSender;
import com.directi.pg.core.pay132.WriteXMLRequest;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
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
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import org.w3c.dom.Document;

import java.util.Map;
import java.util.TreeMap;

import static com.directi.pg.core.pay132.Elements.*;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 7, 2012
 * Time: 9:30:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Pay132PaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(Pay132PaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(Pay132PaymentGateway.class.getName());
    //Configuration
    public static final String GATEWAY_TYPE = "Pay132";
    private final static String URL_TEST = "http://www.132211.com/response_xml";
    private final static String URL_LIVE = "https://www.132211.com/response_xml";
    private final static String MERCHANTID = "";


    public Pay132PaymentGateway(String accountId)
    {
         this.accountId = accountId;
    }

    public String getMaxWaitDays()
        {
            return "3.5";
        }




    /**
             *
             * @param trackingID
             * @param requestVO
             * @return
             * @throws SystemError
             */
            public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
            {
                log.info("  Inside   Pay132PaymentGateway  ::::::::");
               transactionLogger.info("  Inside   Pay132PaymentGateway  ::::::::");
                log.info("  Inside   Pay132PaymentGateway  ::::::::");
                transactionLogger.info("  Inside   Pay132PaymentGateway  ::::::::");
                 log.info("  Inside   processSale  ::::::::");
                 transactionLogger.info("  Inside   processSale  ::::::::");

                validateForSale(trackingID, requestVO);

                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);



                //Preparing map for authentication request
                Map<String, String> authMap = new TreeMap<String, String>();

                if(account.getMerchantId()==null || account.getMerchantId().equals(""))
                {
                    PZExceptionHandler.raiseTechnicalViolationException(Pay132PaymentGateway.class.getName(),"processSale()",null,"common","Merchant Id not Configured while placing transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not Configured while placing transaction,accountId::"+accountId,new Throwable("Merchant Id not Configured while placing transaction,accountId::"+accountId));
                }
                else
                {
                   authMap.put(ELEM_MERCHANT_ID,account.getMerchantId());
                }

                CommRequestVO pay132RequestVO  =    (CommRequestVO)requestVO;
                GenericTransDetailsVO genericTransDetailsVO = pay132RequestVO.getTransDetailsVO();
                GenericAddressDetailsVO genericAddressDetailsVO= pay132RequestVO.getAddressDetailsVO();
                GenericCardDetailsVO genericCardDetailsVO= pay132RequestVO.getCardDetailsVO();

                 //Transaction Details
                authMap.put(ELEM_ORDER_NUMBER,trackingID);
                authMap.put(ELEM_PRODUCT_NAME,genericTransDetailsVO.getOrderDesc());
                authMap.put(ELEM_TOTAL_AMOUNT,genericTransDetailsVO.getAmount());
                if(genericTransDetailsVO.getCurrency()!=null)
                {
                 authMap.put(ELEM_CURRENCY,genericTransDetailsVO.getCurrency());
                }
                else
                {
                   authMap.put(ELEM_CURRENCY,account.getCurrency());
                }

               //User Details
                authMap.put(ELEM_FIRST_NAME,genericAddressDetailsVO.getFirstname());
                authMap.put(ELEM_LAST_NAME,genericAddressDetailsVO.getLastname());
                authMap.put(ELEM_EMAIL,genericAddressDetailsVO.getEmail());
                authMap.put(ELEM_IP,genericAddressDetailsVO.getIp());
                if(genericAddressDetailsVO.getPhone()!=null)
                {
                authMap.put(ELEM_PHONE,genericAddressDetailsVO.getPhone());
                }
                else
                {
                  authMap.put(ELEM_PHONE,"");
                }

                //Address Details
               authMap.put(ELEM_ADDRESS1,genericAddressDetailsVO.getStreet());
               authMap.put(ELEM_ADDRESS2,"");
               authMap.put(ELEM_CITY,genericAddressDetailsVO.getCity());
               authMap.put(ELEM_COUNTRY,genericAddressDetailsVO.getCountry());
               authMap.put(ELEM_STATE,genericAddressDetailsVO.getState());
               authMap.put(ELEM_ZIP,genericAddressDetailsVO.getZipCode());


              //Card Details

                authMap.put(ELEM_CARD_NUM,genericCardDetailsVO.getCardNum());
                authMap.put(ELEM_CARD_TYPE,"");
                authMap.put(ELEM_CVV,genericCardDetailsVO.getcVV());
                authMap.put(ELEM_MONTH,genericCardDetailsVO.getExpMonth());
                String ExpYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length()-2);
                //System.out.println(ExpYear);
                authMap.put(ELEM_YEAR,ExpYear);

                Map<String, String> responseMap = null;

                //Document doc = WriteXMLRequest.createRedeemRequest(authMap);

                //Element root = doc.getDocumentElement();
                //String requestXML = root.toString();
                String requestXML = WriteXMLRequest.createAuthReq(authMap);


                //System.out.println(requestXML);

                Document response  = null;

                String result = null;



                   RequestSender requestSender = new RequestSender(URL_TEST);
                   result = requestSender.send(requestXML);
                   String responseXml =(result).replaceAll("AXOPResponse","Responses");
                   response = WriteXMLRequest.createDocumentFromString(responseXml);





                if(response !=null)
                {
                    responseMap = ReadXMLResponse.ReadXMLResponseForAuth(response);
                }

                // Testing
                 /*Set<String>  keys = responseMap.keySet();
                 log.debug("  ");
                 for(String key : keys)
                 {
                     log.debug("  "+key+"="+responseMap.get(key));

                 }*/


                CommResponseVO pay132ResponseVO = new CommResponseVO();
                if(responseMap.get(ELEM_RESPONSE)!=null && responseMap.get(ELEM_RESPONSE).equals("Approved"))
                {
                    pay132ResponseVO.setStatus("success");
                }else
                {
                    pay132ResponseVO.setStatus("fail");
                }
                pay132ResponseVO.setTransactionStatus(responseMap.get(ELEM_RESPONSE));
                pay132ResponseVO.setMerchantId(responseMap.get(ELEM_MERCHANT_ID));
                pay132ResponseVO.setMerchantOrderId(responseMap.get(ELEM_ORDER_NUMBER));
                pay132ResponseVO.setTransactionId(responseMap.get(ELEM_TRANSACTION_ID));
                pay132ResponseVO.setAmount(responseMap.get(ELEM_AMOUNT));
                pay132ResponseVO.setResponseTime(responseMap.get(ELEM_TRANSACTION_DATE_TIME));
                pay132ResponseVO.setErrorCode(responseMap.get(ELEM_ERROR_NO));
                pay132ResponseVO.setDescription(responseMap.get(ELEM_ERROR_DESCRIPTION));


                return pay132ResponseVO;




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

               log.info("  Inside   Pay132PaymentGateway  ::::::::");
                 log.info("  Inside   processRefund  ::::::::");

                validateForRefund(trackingID, requestVO);

                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);



                //Preparing map for authentication request
                Map<String, String> authMap = new TreeMap<String, String>();

                if(account.getMerchantId()==null || account.getMerchantId().equals(""))
                {
                    PZExceptionHandler.raiseTechnicalViolationException(Pay132PaymentGateway.class.getName(),"processRefund()",null,"common","Merchant Id not configured while Refunding the transation via PAY132,accountId:::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured while Refunding the transation via PAY132,accountId:::"+accountId,new Throwable("Merchant Id not configured while Refunding the transation via PAY132,accountId:::"+accountId));
                }
                else
                {
                   authMap.put(ELEM_MERCHANT_ID,account.getMerchantId());
                }

                CommRequestVO pay132RequestVO  =    (CommRequestVO)requestVO;
                CommTransactionDetailsVO commTransactionDetailsVO = pay132RequestVO.getTransDetailsVO();
                GenericAddressDetailsVO genericAddressDetailsVO= pay132RequestVO.getAddressDetailsVO();

               //User Details
                authMap.put(ELEM_IP,genericAddressDetailsVO.getIp());

                //Transaction Details
                authMap.put(ELEM_ORDER_NUMBER,trackingID);
                authMap.put(ELEM_ORDER_REF_NUMBER,commTransactionDetailsVO.getPreviousTransactionId());
                authMap.put(ELEM_TOTAL_AMOUNT,commTransactionDetailsVO.getAmount());

               Map<String, String> responseMap = null;

               //Document doc = WriteXMLRequest.createRedeemRequest(authMap);

               //Element root = doc.getDocumentElement();
               //String requestXML = root.toString();
               String requestXML = WriteXMLRequest.createRefundReq(authMap);


               //System.out.println(requestXML);

               Document response  = null;

               String result = null;


                  RequestSender requestSender = new RequestSender(URL_TEST);
                  result = requestSender.send(requestXML);
                  String responseXml =(result).replaceAll("AXOPResponse","Responses");

                  response = WriteXMLRequest.createDocumentFromString(responseXml);





                if(response !=null)
                {
                    responseMap = ReadXMLResponse.ReadXMLResponseForAuth(response);

                }

               /* // Testing
                 Set<String>  keys = responseMap.keySet();
                 log.debug("  ");
                 for(String key : keys)
                 {
                     log.debug("  "+key+"="+responseMap.get(key));

                 }*/


                CommResponseVO pay132ResponseVO = new CommResponseVO();
                if(responseMap.get(ELEM_RESPONSE)!=null && responseMap.get(ELEM_RESPONSE).equalsIgnoreCase("Approved"))
                {
                   pay132ResponseVO.setStatus("success");
                }
                else
                {
                   pay132ResponseVO.setStatus("fail");
                }

                pay132ResponseVO.setTransactionStatus(responseMap.get(ELEM_RESPONSE));
                pay132ResponseVO.setMerchantId(responseMap.get(ELEM_MERCHANT_ID));
                pay132ResponseVO.setMerchantOrderId(responseMap.get(ELEM_ORDER_NUMBER));
                pay132ResponseVO.setTransactionId(responseMap.get(ELEM_TRANSACTION_ID));
                pay132ResponseVO.setAmount(responseMap.get(ELEM_AMOUNT));
                pay132ResponseVO.setResponseTime(responseMap.get(ELEM_TRANSACTION_DATE_TIME));
                pay132ResponseVO.setErrorCode(responseMap.get(ELEM_ERROR_NO));
                pay132ResponseVO.setDescription(responseMap.get(ELEM_ERROR_DESCRIPTION));


                return pay132ResponseVO;

        }


    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }



    /**
  * Check the input values (to be updated later for 3D Secure case )
  * @param trackingID
  * @param requestVO
  * @throws SystemError
  */
 private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
 {
     if(trackingID ==null || trackingID.equals(""))
     {
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracking Id not provided while Refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Refunding transaction",new Throwable("Tracking Id not provided while Refunding transaction"));
     }

     if(requestVO ==null)
     {
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  not provided while Refunding transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Refunding transaction",new Throwable("Request  not provided while Refunding transaction"));
     }

     CommRequestVO pay132RequestVO  =    (CommRequestVO)requestVO;



     CommTransactionDetailsVO commTransactionDetailsVO = pay132RequestVO.getTransDetailsVO();
     if(commTransactionDetailsVO ==null)
     {
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForRefund()",null,"common","TransactionDetails  not provided while Refunding transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while Refunding transaction",new Throwable("TransactionDetails  not provided while Refunding transaction"));
     }
     if(commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
     {
        PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while Refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Refunding transaction",new Throwable("Amount not provided while Refunding transaction"));
     }

     if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
     {
        PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transaction Id not provided while Refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transactio Id not provided while Refunding transaction",new Throwable("Previous Transaction Id not provided while Refunding transaction"));
     }

     GenericAddressDetailsVO genericAddressDetailsVO= pay132RequestVO.getAddressDetailsVO();

     if(genericAddressDetailsVO ==null)
     {
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForRefund()",null,"common","AddressDetails  not provided while Refunding transaction",PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while Refunding transaction",new Throwable("AddressDetails  not provided while Refunding transaction"));
     }
     //User Details

     if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
     {
         PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForRefund()",null,"common","Ip Address not provided while Refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IP Address not provided while Refunding transaction",new Throwable("IP Address not provided while Refunding transaction"));
     }




 }


             /**
     * Check the input values (to be updated later for 3D Secure case )
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
             PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Request Vo not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }

        CommRequestVO pay132RequestVO  =    (CommRequestVO)requestVO;



        GenericTransDetailsVO genericTransDetailsVO = pay132RequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails Vo not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= pay132RequestVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","FirstName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"FirstName not provided while placing transaction",new Throwable("FirstName not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","LastName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"LastName not provided while placing transaction",new Throwable("LastName not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Email Id not provided while placing transaction",new Throwable("Email Id not provided while placing transaction"));
        }


        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
        }


        //Address Details
       if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));
       }

       if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));
       }


       if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
       }

       if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"State not provided while placing transaction",new Throwable("State not provided while placing transaction"));
       }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Zip code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Zip Code not provided while placing transaction",new Throwable("Zip Code not provided while placing transaction"));
       }


        GenericCardDetailsVO genericCardDetailsVO= pay132RequestVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO provided is Invalid while placing transaction", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,"Card NO provided is Invalid while placing transaction",new Throwable("Card NO providedis Invalid while placing transaction"));
        }



        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }


        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }


        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(Pay132PaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }




    }



}
