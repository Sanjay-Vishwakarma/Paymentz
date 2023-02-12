package com.directi.pg.core.paymentgateway;

import static com.directi.pg.core.paylineVoucher.Elements.*;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.Database;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.PayVTPaymentGateway;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.directi.pg.core.paylineVoucher.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.Util;
import com.payment.common.core.CommTransactionDetailsVO;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.sql.Connection;
import java.sql.ResultSet;

import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.owasp.esapi.ESAPI;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Aug 31, 2012
 * Time: 7:26:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayLineVoucherGateway   extends AbstractPaymentGateway
{

    private static Logger log = new Logger(PayLineVoucherGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayLineVoucherGateway.class.getName());
    private static Hashtable<String, PayVoucherAccount> payVoucherAccounts;



    //Configuration
     public static final String GATEWAY_TYPE = "payVoucher";
     private final static String PAYLINE_URL_TEST = "https://test.ctpe.net/payment/ctpe";
     private final static String PAYLINE_URL_LIVE = "https://ctpe.net/payment/ctpe";
     private final static String PAYLINE_QUERY_TEST = "https://test.ctpe.net/payment/query";
     private final static String PAYLINE_QUERY_LIVE = "https://ctpe.net/payment/query";
     public static final String MODE_LIVE = "LIVE";
     public static final String MODE_TEST = "INTEGRATOR_TEST";
     public static final String PAYMENTCODE_VA_DB = "VA.DB";                    //Fixed for Voucher
     public static final String PAYMENTCODE_VA_RV = "VA.RV";                    //Payment code for reversal/void/cancel
     public static final String PAYMENTCODE_VA_RF = "VA.RF";                    //Payment code for refund
    // public static final String BRAND = "PASTEANDPAY_V";                        //Fixed & can be white labelled for /


     static
    {
        try
        {
            loadPayVoucherAccounts();

        }
        catch (PZDBViolationException e)
        {
            log.error("Exception while loading gatewya details",e);
            transactionLogger.error("Exception while loading gatewya details",e);

            PZExceptionHandler.handleDBCVEException(e,"PayLineVoucher", PZOperations.PAYLINEVOUCHER_LAOD_ACCOUNT);
        }

    }

     /**
     *
     * @param accountId
     * @throws SystemError
     */
    public PayLineVoucherGateway(String accountId)
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
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {


            log.info("  Inside   PayLineVoucherGateway  ::::::::");

            validateSale(trackingID,requestVO);



            PayLineVoucherRequestVO payLineVoucherRequestVO  =    (PayLineVoucherRequestVO)requestVO;

            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

            PayVoucherAccount payVoucherAccount  = getPayVoucherAccount(accountId);

            //Preparing map for authentication request
            Map<String, String> authMap = new TreeMap<String, String>();



            authMap.put(ATTR_VERSION,payVoucherAccount.getVersion());
            authMap.put(ATTR_SENDER,payVoucherAccount.getSender());
            authMap.put(ATTR_TOKEN,payVoucherAccount.getToken());
            if(payVoucherAccount.isLive())
            {
            authMap.put(ATTR_MODE,MODE_LIVE);
            }
            else
            {
             authMap.put(ATTR_MODE,MODE_TEST);
            }
            authMap.put(ATTR_CHANNEL,payVoucherAccount.getChannel());
            authMap.put(ELEM_TRANSACTIONID,trackingID);
            authMap.put(ATTR_LOGIN,payVoucherAccount.getLogin());
            authMap.put(ATTR_PWD,payVoucherAccount.getPwd());
            authMap.put(ELEM_BRAND,payVoucherAccount.getBrand());
            authMap.put(ATTR_PAYMENTCODE,PAYMENTCODE_VA_DB);
            if(payLineVoucherRequestVO.getTransDetailsVO()!=null)
            {
             authMap.put(ELEM_AMOUNT,payLineVoucherRequestVO.getTransDetailsVO().getAmount());
            }
            else
            {
               authMap.put(ELEM_AMOUNT,"");
            }

            if(payLineVoucherRequestVO.getTransDetailsVO()!=null && payLineVoucherRequestVO.getTransDetailsVO().getCurrency()!=null)
            {
             authMap.put(ELEM_CURRENCY,payLineVoucherRequestVO.getTransDetailsVO().getCurrency());
            }
            else
            {
               authMap.put(ELEM_CURRENCY,account.getCurrency());
            }

            if(payLineVoucherRequestVO.getId()!=null)
            {
             authMap.put(ELEM_ID,payLineVoucherRequestVO.getId());
            }
            else
            {
               authMap.put(ELEM_ID,payLineVoucherRequestVO.getId());
            }


           if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getLastname()!=null)
            {
            authMap.put(ELEM_FAMILY,payLineVoucherRequestVO.getAddressDetailsVO().getLastname());
            }
            else
            {
              authMap.put(ELEM_FAMILY,"");
            }

            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getFirstname()!=null)
            {
            authMap.put(ELEM_GIVEN,payLineVoucherRequestVO.getAddressDetailsVO().getFirstname());
            }
            else
            {
              authMap.put(ELEM_GIVEN,"");
            }

            if(payLineVoucherRequestVO.getCompany()!=null )
            {
            authMap.put(ELEM_COMPANY,payLineVoucherRequestVO.getCompany());
            }
            else
            {
              authMap.put(ELEM_COMPANY,"");
            }

            if(payLineVoucherRequestVO.getSalutation()!=null)
            {
            authMap.put(ELEM_SALUTATION,payLineVoucherRequestVO.getSalutation());
            }
            else
            {
              authMap.put(ELEM_SALUTATION,"");
            }

            if(payLineVoucherRequestVO.getTitle()!=null)
            {
            authMap.put(ELEM_TITLE,payLineVoucherRequestVO.getTitle());
            }
            else
            {
              authMap.put(ELEM_TITLE,"");
            }

            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getEmail()!=null)
            {
            authMap.put(ELEM_EMAIL,payLineVoucherRequestVO.getAddressDetailsVO().getEmail());
            }
            else
            {
              authMap.put(ELEM_EMAIL,"");
            }

            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getIp()!=null)
            {
            authMap.put(ELEM_IP,payLineVoucherRequestVO.getAddressDetailsVO().getIp());
            }
            else
            {
              authMap.put(ELEM_IP,"96.236.7.223");
            }

            if(payLineVoucherRequestVO.getMobile()!=null)
            {
            authMap.put(ELEM_MOBILE,payLineVoucherRequestVO.getMobile());
            }
            else
            {
              authMap.put(ELEM_MOBILE,"");
            }

            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getCity()!=null)
            {
            authMap.put(ELEM_CITY,payLineVoucherRequestVO.getAddressDetailsVO().getCity());
            }
            else
            {
              authMap.put(ELEM_CITY,"");
            }

            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getCountry()!=null)
            {
            authMap.put(ELEM_COUNTRY,payLineVoucherRequestVO.getAddressDetailsVO().getCountry());
            }
            else
            {
              authMap.put(ELEM_COUNTRY,"");
            }

            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getState()!=null)
            {
            authMap.put(ELEM_STATE,payLineVoucherRequestVO.getAddressDetailsVO().getState());
            }
            else
            {
              authMap.put(ELEM_STATE,"");
            }

            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getStreet()!=null)
            {
            authMap.put(ELEM_STREET,payLineVoucherRequestVO.getAddressDetailsVO().getStreet());
            }
            else
            {
              authMap.put(ELEM_STREET,"");
            }
            if(payLineVoucherRequestVO.getAddressDetailsVO()!=null || payLineVoucherRequestVO.getAddressDetailsVO().getZipCode()!=null)
            {
            authMap.put(ELEM_ZIP,payLineVoucherRequestVO.getAddressDetailsVO().getZipCode());
            }
            else
            {
              authMap.put(ELEM_ZIP,"");
            }



            Map<String, String> responseMap = null;

            //Document doc = WriteXMLRequest.createRedeemRequest(authMap);

            //Element root = doc.getDocumentElement();
            //String requestXML = root.toString();
            String requestXML = WriteXMLRequest.createRedeemReq(authMap);


            //System.out.println(requestXML);

            Document response  = null;

            String result = null;


               //PayLineUtils PayLineUtils = new PayLineUtils();
              //response = PayLineUtils.doPostURLConnection(PAYLINE_URL_TEST,doc);
              //response = PayLineUtils.doPostHTTPSURLConnection(PAYLINE_URL_TEST,doc);

               RequestSender requestSender = null;
                if(payVoucherAccount.isLive())
                {
                requestSender = new RequestSender(PAYLINE_URL_LIVE);
                }
                else
                {
                requestSender = new RequestSender(PAYLINE_URL_TEST);
                }
               result = requestSender.send(requestXML);
               response = WriteXMLRequest.createDocumentFromString(result);



            if(response !=null)
            {
                responseMap = ReadXMLResponse.ReadXMLResponseForAuth(response);
            }

            // Testing
             Set<String>  keys = responseMap.keySet();
             /*log.debug("  ");
             for(String key : keys)
             {
                 log.debug("  "+key+"="+responseMap.get(key));
                 
             }
*/

            PayLineVoucherResponseVO payLineVoucherResponseVO = new PayLineVoucherResponseVO();

            payLineVoucherResponseVO.setMode(responseMap.get(ATTR_MODE));
            payLineVoucherResponseVO.setChannel(responseMap.get(ATTR_CHANNEL));
            payLineVoucherResponseVO.setClearingAmount(responseMap.get(ELEM_AMOUNT));
            payLineVoucherResponseVO.setCurrency(responseMap.get(ELEM_CURRENCY));
            payLineVoucherResponseVO.setPaymentCode(responseMap.get(ATTR_PAYMENTCODE));
            payLineVoucherResponseVO.setDescription(responseMap.get(ELEM_REASON));
            payLineVoucherResponseVO.setErrorCode(responseMap.get(ATTR_REASONCODE));
            payLineVoucherResponseVO.setResult(responseMap.get(ELEM_RESULT));
            payLineVoucherResponseVO.setReturnCode(responseMap.get(ELEM_RETURN));
            payLineVoucherResponseVO.setReturnMessage(responseMap.get(ATTR_RETURNCODE));
            payLineVoucherResponseVO.setShortId(responseMap.get(ELEM_SHORTID));
            payLineVoucherResponseVO.setStatus(responseMap.get(ELEM_STATUS));
            payLineVoucherResponseVO.setStatusCode(responseMap.get(ATTR_STATUSCODE));
            payLineVoucherResponseVO.setTransactionId(responseMap.get(ELEM_UNIQUEID));
            payLineVoucherResponseVO.setReferenceId(responseMap.get(ELEM_UNIQUEID));
            payLineVoucherResponseVO.setTimeStamp(responseMap.get(ELEM_TIMESTAMP));
            payLineVoucherResponseVO.setUniqueId(responseMap.get(ELEM_UNIQUEID));
            payLineVoucherResponseVO.setDescriptor(responseMap.get(ELEM_DESCRIPTOR));
            payLineVoucherResponseVO.setFxRate(responseMap.get(ELEM_FX_RATE));
            payLineVoucherResponseVO.setFxSource(responseMap.get(ELEM_FX_SOURCE));
            payLineVoucherResponseVO.setFxDate(responseMap.get(ELEM_FX_DATE));
            payLineVoucherResponseVO.setRisk(responseMap.get(ELEM_RISK));
            payLineVoucherResponseVO.setRiskScore(responseMap.get(ATTR_SCORE));

            return payLineVoucherResponseVO;
        }

    private void validateSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
           log.info("  Inside   validateSale  ::::::::");

            if(trackingID ==null || trackingID.equals(""))
            {
                 PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"validateSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
            }


            if(requestVO ==null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"validateSale()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
            }

            PayLineVoucherRequestVO payLineVoucherRequestVO  =    (PayLineVoucherRequestVO)requestVO;

            if(payLineVoucherRequestVO.getId()==null||payLineVoucherRequestVO.getId().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"validateSale()",null,"common","PayLineVoucher Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"PayLineVoucher Id not provided while placing transaction",new Throwable("PayLineVoucher Id not provided while placing transaction"));
            }


    }

    /**
         * The Reversal voids a previous Debit (DB) or Preauthorization (PA) transaction.
         * Same as cancel or void
         * @param trackingID
         * @param requestVO
         * @return
         * @throws SystemError
         */
        public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
        {
            if(trackingID ==null || trackingID.equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"processVoid()",null,"common","TrackingId not provided while cancelling the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"TrackingId not provided while cancelling the transaction",new Throwable("TrackingId not provided while cancelling the transaction"));
            }


            if(requestVO ==null)
            {
                PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"processVoid()",null,"common","Request  not provided while cancelling the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while cancelling the transaction",new Throwable("Request  not provided while cancelling the transaction"));
            }


            PayLineVoucherRequestVO payLineVoucherRequestVO  =    (PayLineVoucherRequestVO)requestVO;


            if(payLineVoucherRequestVO.getReferenceId()==null || payLineVoucherRequestVO.getReferenceId().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"processVoid()",null,"common","Refference Id not provided while cancelling the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Refference Id not provided while cancelling the transaction",new Throwable("Reffernce Id not provided while cancelling the transaction"));
            }

            PayVoucherAccount payVoucherAccount  = getPayVoucherAccount(accountId);

            //Preparing map for reversal/void/cancel request
            Map<String, String> reversalMap = new TreeMap<String, String>();


            reversalMap.put(ATTR_VERSION,payVoucherAccount.getVersion());
            reversalMap.put(ATTR_SENDER,payVoucherAccount.getSender());
            reversalMap.put(ATTR_TOKEN,payVoucherAccount.getToken());
            reversalMap.put(ATTR_MODE,MODE_TEST);
            reversalMap.put(ATTR_CHANNEL,payVoucherAccount.getChannel());
            reversalMap.put(ELEM_TRANSACTIONID,trackingID);
            reversalMap.put(ELEM_REFERENCEID,payLineVoucherRequestVO.getReferenceId());
            reversalMap.put(ATTR_LOGIN,payVoucherAccount.getLogin());
            reversalMap.put(ATTR_PWD,payVoucherAccount.getPwd());
            reversalMap.put(ATTR_PAYMENTCODE,PAYMENTCODE_VA_RV);


            Map<String, String> responseMap = null;

            String requestXML = WriteXMLRequest.createReversalRequest(reversalMap);

            //System.out.println(requestXML);

            Document response  = null;

            String result = null;



               RequestSender requestSender = null;
                if(payVoucherAccount.isLive())
                {
                requestSender = new RequestSender(PAYLINE_URL_LIVE);
                }
                else
                {
                requestSender = new RequestSender(PAYLINE_URL_TEST);
                }
               result = requestSender.send(requestXML);
               response = WriteXMLRequest.createDocumentFromString(result);




             if(response !=null)
            {
                responseMap = ReadXMLResponse.ReadXMLResponseForAuth(response);
            }

            // Testing
             Set<String>  keys = responseMap.keySet();
             /*System.out.println("  ");
             for(String key : keys)
             {
                 System.out.println("  "+key+"="+responseMap.get(key));
             }*/




            PayLineVoucherResponseVO payLineVoucherResponseVO = new PayLineVoucherResponseVO();

            payLineVoucherResponseVO.setMode(responseMap.get(ATTR_MODE));
            payLineVoucherResponseVO.setChannel(responseMap.get(ATTR_CHANNEL));
            payLineVoucherResponseVO.setClearingAmount(responseMap.get(ELEM_AMOUNT));
            payLineVoucherResponseVO.setCurrency(responseMap.get(ELEM_CURRENCY));
            payLineVoucherResponseVO.setPaymentCode(responseMap.get(ATTR_PAYMENTCODE));
            payLineVoucherResponseVO.setDescription(responseMap.get(ELEM_REASON));
            payLineVoucherResponseVO.setErrorCode(responseMap.get(ATTR_REASONCODE));
            payLineVoucherResponseVO.setResult(responseMap.get(ELEM_RESULT));
            payLineVoucherResponseVO.setReturnCode(responseMap.get(ELEM_RETURN));
            payLineVoucherResponseVO.setReturnMessage(responseMap.get(ATTR_RETURNCODE));
            payLineVoucherResponseVO.setShortId(responseMap.get(ELEM_SHORTID));
            payLineVoucherResponseVO.setStatus(responseMap.get(ELEM_STATUS));
            payLineVoucherResponseVO.setStatusCode(responseMap.get(ATTR_STATUSCODE));
            payLineVoucherResponseVO.setMerchantId(responseMap.get(ELEM_TRANSACTIONID));
            payLineVoucherResponseVO.setTransactionId(responseMap.get(ELEM_UNIQUEID));
            payLineVoucherResponseVO.setReferenceId(responseMap.get(ELEM_REFERENCEID));
            payLineVoucherResponseVO.setTimeStamp(responseMap.get(ELEM_TIMESTAMP));
            payLineVoucherResponseVO.setUniqueId(responseMap.get(ELEM_UNIQUEID));
            payLineVoucherResponseVO.setDescriptor(responseMap.get(ELEM_DESCRIPTOR));
            payLineVoucherResponseVO.setFxRate(responseMap.get(ELEM_FX_RATE));
            payLineVoucherResponseVO.setFxSource(responseMap.get(ELEM_FX_SOURCE));
            payLineVoucherResponseVO.setFxDate(responseMap.get(ELEM_FX_DATE));
            payLineVoucherResponseVO.setRisk(responseMap.get(ELEM_RISK));
            payLineVoucherResponseVO.setRiskScore(responseMap.get(ATTR_SCORE));

            if(payLineVoucherResponseVO.getErrorCode().equals("00"))
                payLineVoucherResponseVO.setStatus("success");

            return payLineVoucherResponseVO;
        }



            /**
             *
             * @param trackingID
             * @param requestVO
             * @return
             * @throws SystemError
             */
             public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
             {
                if(trackingID ==null || trackingID.equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"processRefund()",null,"common","TrackingId not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"TrackingId not provided while Refunding the transaction",new Throwable("TrackingId not provided while Refunding the transaction"));
                }


                if(requestVO ==null)
                {
                    PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"processRefund()",null,"common","Request  not provided while Refunding the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Refunding the transaction",new Throwable("Request  not provided while Refunding the transaction"));
                }


                PayLineVoucherRequestVO payLineVoucherRequestVO  =    (PayLineVoucherRequestVO)requestVO;



                CommTransactionDetailsVO  payLineVoucherTransDetailsVO  = (CommTransactionDetailsVO)payLineVoucherRequestVO.getTransDetailsVO();

                if(payLineVoucherRequestVO.getReferenceId()==null || payLineVoucherRequestVO.getReferenceId().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"processRefund()",null,"common","Reffernce ID not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Reffernce Id not provided while Refunding the transaction",new Throwable("Reffernce Id not provided while Refunding the transaction"));
                }

                if(payLineVoucherTransDetailsVO.getAmount()==null || payLineVoucherTransDetailsVO.getAmount().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(PayLineVoucherGateway.class.getName(),"processRefund()",null,"common","Amount not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Refunding the transaction",new Throwable("Amount not provided while Refunding the transaction"));
                }

                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

                PayVoucherAccount payVoucherAccount  = getPayVoucherAccount(accountId);

                //Preparing map for reversal/void/cancel request
                Map<String, String> reversalMap = new TreeMap<String, String>();


                reversalMap.put(ATTR_VERSION,payVoucherAccount.getVersion());
                reversalMap.put(ATTR_SENDER,payVoucherAccount.getSender());
                reversalMap.put(ATTR_TOKEN,payVoucherAccount.getToken());
                reversalMap.put(ATTR_MODE,MODE_TEST);
                reversalMap.put(ATTR_CHANNEL,payVoucherAccount.getChannel());
                reversalMap.put(ELEM_TRANSACTIONID,trackingID);
                reversalMap.put(ELEM_REFERENCEID,payLineVoucherRequestVO.getReferenceId());
                reversalMap.put(ATTR_LOGIN,payVoucherAccount.getLogin());
                reversalMap.put(ATTR_PWD,payVoucherAccount.getPwd());
                reversalMap.put(ATTR_PAYMENTCODE,PAYMENTCODE_VA_RF);
                reversalMap.put(ELEM_AMOUNT,payLineVoucherTransDetailsVO.getAmount());


                if(payLineVoucherTransDetailsVO.getCurrency()!=null)
                {
                 reversalMap.put(ELEM_CURRENCY,payLineVoucherTransDetailsVO.getCurrency());
                }
                else
                {
                   reversalMap.put(ELEM_CURRENCY,account.getCurrency());
                }


                Map<String, String> responseMap = null;

                String requestXML = WriteXMLRequest.createRefundRequest(reversalMap);

                //System.out.println(requestXML);

                Document response  = null;

                String result = null;



                   RequestSender requestSender = null;
                    if(payVoucherAccount.isLive())
                    {
                    requestSender = new RequestSender(PAYLINE_URL_LIVE);
                    }
                    else
                    {
                    requestSender = new RequestSender(PAYLINE_URL_TEST);
                    }
                   result = requestSender.send(requestXML);
                   //System.out.println(result);
                   response = WriteXMLRequest.createDocumentFromString(result);





                 if(response !=null)
                {
                    responseMap = ReadXMLResponse.ReadXMLResponseForAuth(response);
                }

                // Testing
                 Set<String>  keys = responseMap.keySet();
                 /*System.out.println("  ");
                 for(String key : keys)
                 {
                     System.out.println("  "+key+"="+responseMap.get(key));
                 }*/




                PayLineVoucherResponseVO payLineVoucherResponseVO = new PayLineVoucherResponseVO();

                payLineVoucherResponseVO.setMode(responseMap.get(ATTR_MODE));
                payLineVoucherResponseVO.setChannel(responseMap.get(ATTR_CHANNEL));
                payLineVoucherResponseVO.setClearingAmount(responseMap.get(ELEM_AMOUNT));
                payLineVoucherResponseVO.setCurrency(responseMap.get(ELEM_CURRENCY));
                payLineVoucherResponseVO.setPaymentCode(responseMap.get(ATTR_PAYMENTCODE));
                payLineVoucherResponseVO.setDescription(responseMap.get(ELEM_REASON));
                payLineVoucherResponseVO.setErrorCode(responseMap.get(ATTR_REASONCODE));
                payLineVoucherResponseVO.setResult(responseMap.get(ELEM_RESULT));
                payLineVoucherResponseVO.setReturnCode(responseMap.get(ELEM_RETURN));
                payLineVoucherResponseVO.setReturnMessage(responseMap.get(ATTR_RETURNCODE));
                payLineVoucherResponseVO.setShortId(responseMap.get(ELEM_SHORTID));
                payLineVoucherResponseVO.setStatus(responseMap.get(ELEM_STATUS));
                payLineVoucherResponseVO.setStatusCode(responseMap.get(ATTR_STATUSCODE));
                payLineVoucherResponseVO.setMerchantId(responseMap.get(ELEM_TRANSACTIONID));
                payLineVoucherResponseVO.setTransactionId(responseMap.get(ELEM_REFERENCEID));
                payLineVoucherResponseVO.setTimeStamp(responseMap.get(ELEM_TIMESTAMP));
                payLineVoucherResponseVO.setUniqueId(responseMap.get(ELEM_UNIQUEID));
                payLineVoucherResponseVO.setDescriptor(responseMap.get(ELEM_DESCRIPTOR));
                payLineVoucherResponseVO.setFxRate(responseMap.get(ELEM_FX_RATE));
                payLineVoucherResponseVO.setFxSource(responseMap.get(ELEM_FX_SOURCE));
                payLineVoucherResponseVO.setFxDate(responseMap.get(ELEM_FX_DATE));
                payLineVoucherResponseVO.setRisk(responseMap.get(ELEM_RISK));
                payLineVoucherResponseVO.setRiskScore(responseMap.get(ATTR_SCORE));

                if(payLineVoucherResponseVO.getErrorCode().equals("00"))
                    payLineVoucherResponseVO.setStatus("success");
                return payLineVoucherResponseVO;
            }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    public static void loadPayVoucherAccounts() throws PZDBViolationException
    {
        log.info("Loading Payvoucher Accounts......");
        payVoucherAccounts = new Hashtable<String, PayVoucherAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_payvoucher", conn);
            while (rs.next())
            {
                PayVoucherAccount account = new PayVoucherAccount(rs);
                payVoucherAccounts.put(account.getAccountId() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PayLineVoucherGateway.class.getName(),"loadPayVoucherAccounts()",null,"common","SQL Exception while getting Details from gateway payvoucher", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(PayLineVoucherGateway.class.getName(), "loadPayVoucherAccounts()", null, "common", "System Error while getting Details from gateway payvoucher", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

     public static PayVoucherAccount getPayVoucherAccount(String accountId)
    {
        return payVoucherAccounts.get(accountId);
    }


}
