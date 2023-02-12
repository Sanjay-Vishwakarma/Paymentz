package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.NMI.NMIAccount;
import com.directi.pg.core.NMI.NMIUtils;

import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import com.payment.common.core.*;
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


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Saurabh.B
 * Date: 8/6/13
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class NMIPaymentGateway  extends AbstractPaymentGateway
{
    private static Logger log = new Logger(NMIPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(NMIPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "NMIPayment";
    private static Hashtable<String, NMIAccount> NMIAccounts;
    public final static String ELE_TYPE = "type";
    public final static String ELE_USEERNAME = "username";
    public final static String ELE_PASSWORD = "password";
    public final static String ELE_CCNUMBER = "ccnumber";
    public final static String ELE_CCEXP = "ccexp";
    public final static String ELE_CVV = "cvv";
    public final static String ELE_AMOUNT = "amount";
    public final static String ELE_CURRENCY = "currency";
    public final static String ELE_ORDERDESCRIPTION= "orderdescription";
    public final static String ELE_ORDERID = "orderid";
    public final static String ELE_ORDER_ID = "order_id";
    public final static String ELE_IPADDRESS= "ipaddress";
    public final static String ELE_FIRSTNAME= "firstname";
    public final static String ELE_LASTNAME = "lastname";
    public final static String ELE_ADDRESS1 = "address1";
    public final static String ELE_ADDRESS2 = "address2";
    public final static String ELE_CITY = "city";
    public final static String ELE_STATE = "state";
    public final static String ELE_ZIP = "zip";
    public final static String ELE_COUNTRY = "country";
    public final static String ELE_PHONE= "phone";
    public final static String ELE_EXPIRE_EMAIL = "email";
    public final static String ELE_TRANSACTIONID= "transactionid";
    public final static String ELE_Processor_id="processor_id";
    public final static String SALE= "sale";
    public final static String AUTH = "auth";
    public final static String CAPTURE= "capture";
    public final static String VOID = "void";
    public final static String REFUND = "refund";






    //configuration
    private final static String NMI_URL = "https://secure.nmi.com/api/transact.php";
    private final static String NMI_STATUS ="https://secure.nmi.com/api/query.php";

    static
    {
        try
        {
            loadPayAccounts();
        }
        catch (PZDBViolationException e)
        {
            log.error("Exception while loading Specific gateway account from the database",e);
            transactionLogger.error("Exception while loading Specific gateway account from the database",e);

            PZExceptionHandler.handleDBCVEException(e,"NMI Gateway", PZOperations.NMI_LAOD_ACCOUNT);
        }

    }

    public NMIPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static NMIAccount getNMIAccount(String accountId)
    {
        return NMIAccounts.get(accountId);
    }




    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZDBViolationException, PZTechnicalViolationException, PZConstraintViolationException
    {

        CommRequestVO NMIVO = (CommRequestVO) requestVO;
        CommResponseVO NMIresVO=new CommResponseVO();



            loadPayAccounts();
            validateForSale(trackingID,requestVO);
            NMIAccount account= getNMIAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();


            authMap.put(ELE_TYPE,SALE);
            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_USEERNAME,account.getUsername());
            authMap.put(ELE_ORDERID,trackingID);
            authMap.put(ELE_Processor_id, GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            //Getting Transaction Detail
            CommTransactionDetailsVO genericTransactionDetailsVO = NMIVO.getTransDetailsVO();
            authMap.put(ELE_ORDERDESCRIPTION,genericTransactionDetailsVO.getOrderDesc());

            authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
            authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());


            //Getting Address Details
            CommAddressDetailsVO genericAddressDetailsVO = NMIVO.getAddressDetailsVO();
            authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
            authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
            authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
            authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
            authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
            authMap.put(ELE_EXPIRE_EMAIL,genericAddressDetailsVO.getEmail());
            authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
            authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
            authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
            authMap.put(ELE_ADDRESS1,genericAddressDetailsVO.getStreet());
            authMap.put(ELE_ADDRESS2,genericAddressDetailsVO.getStreet());

            //String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

            //Getting Card Details
            CommCardDetailsVO genericCardDetailsVO = NMIVO.getCardDetailsVO();



            authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
            authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
            authMap.put(ELE_CCEXP,genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear().substring(2));

            String cardParameters = NMIUtils.joinMapValue(authMap, '&');
            String response = NMIUtils.doPostHTTPSURLConnection(NMI_URL,cardParameters);

            Map<String,String> status = NMIUtils.getResponseString(response);
            if(status!=null && !status.equals(""))
            {
                NMIresVO.setTransactionId(status.get("transactionid"));

                if(status.get("response").equalsIgnoreCase("1"))
                {

                    NMIresVO.setStatus("success");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                else
                {
                    NMIresVO.setStatus("fail");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                NMIresVO.setErrorCode(status.get("response_code"));
                NMIresVO.setTransactionType("type");
                NMIresVO.setMerchantOrderId("orderid");

            }
            else
            {
                NMIresVO.setStatus("fail");
                NMIresVO.setDescription(status.get("responsetext"));
            }


        return NMIresVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO NMIVO = (CommRequestVO) requestVO;
        CommResponseVO NMIresVO=new CommResponseVO();


            loadPayAccounts();
            validateForSale(trackingID, requestVO);
            NMIAccount account= getNMIAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();
            Map<String, String> chargemap = new TreeMap<String, String>();

            authMap.put(ELE_TYPE,AUTH);
            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_USEERNAME,account.getUsername());
            authMap.put(ELE_ORDERID,trackingID);
            authMap.put(ELE_Processor_id, GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            //Getting Transaction Detail
            CommTransactionDetailsVO genericTransactionDetailsVO = NMIVO.getTransDetailsVO();
            authMap.put(ELE_ORDERDESCRIPTION,genericTransactionDetailsVO.getOrderDesc());

            authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
            authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());


            //Getting Address Details
            CommAddressDetailsVO genericAddressDetailsVO = NMIVO.getAddressDetailsVO();
            authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
            authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
            authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
            authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
            authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
            authMap.put(ELE_EXPIRE_EMAIL,genericAddressDetailsVO.getEmail());
            authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
            authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
            authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
            authMap.put(ELE_ADDRESS1,genericAddressDetailsVO.getStreet());
            authMap.put(ELE_ADDRESS2,genericAddressDetailsVO.getStreet());

            String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

            //Getting Card Details
            CommCardDetailsVO genericCardDetailsVO = NMIVO.getCardDetailsVO();



            authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
            authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
            authMap.put(ELE_CCEXP,genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear().substring(2));

            String cardParameters = NMIUtils.joinMapValue(authMap, '&');
            String response = NMIUtils.doPostHTTPSURLConnection(NMI_URL,cardParameters);
            Map<String,String> status = NMIUtils.getResponseString(response);
            if(status!=null && !status.equals(""))
            {
                NMIresVO.setTransactionId(status.get("transactionid"));

                if(status.get("response").equalsIgnoreCase("1"))
                {

                    NMIresVO.setStatus("success");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                else
                {
                    NMIresVO.setStatus("fail");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                NMIresVO.setErrorCode(status.get("response_code"));
                NMIresVO.setTransactionType("type");
                NMIresVO.setMerchantOrderId("orderid");

            }
            else
            {
                NMIresVO.setStatus("fail");
                NMIresVO.setDescription(status.get("responsetext"));
            }
        return NMIresVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZDBViolationException, PZTechnicalViolationException, PZConstraintViolationException
    {
        CommRequestVO NMIVO = (CommRequestVO) requestVO;
        CommResponseVO NMIresVO=new CommResponseVO();
        // validateForSale(trackingID,requestVO);


            loadPayAccounts();
            validateForVoid(trackingID,requestVO);
            NMIAccount account= getNMIAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELE_TYPE,VOID);
            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_USEERNAME,account.getUsername());
            authMap.put(ELE_ORDERID,trackingID);
            CommTransactionDetailsVO CtransVo = NMIVO.getTransDetailsVO();

            authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());



            String cardParameters = NMIUtils.joinMapValue(authMap, '&');
            String response = NMIUtils.doPostHTTPSURLConnection(NMI_URL,cardParameters);
            Map<String,String> status = NMIUtils.getResponseString(response);
            if(status!=null && !status.equals(""))
            {
                NMIresVO.setTransactionId(status.get("transactionid"));

                if(status.get("response").equalsIgnoreCase("1"))
                {

                    NMIresVO.setStatus("success");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                else
                {
                    NMIresVO.setStatus("fail");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                NMIresVO.setErrorCode(status.get("response_code"));
                NMIresVO.setTransactionType("type");
                NMIresVO.setMerchantOrderId("orderid");

            }
            else
            {
                NMIresVO.setStatus("fail");
                NMIresVO.setDescription(status.get("responsetext"));
            }

        return NMIresVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZDBViolationException, PZTechnicalViolationException, PZConstraintViolationException
    {
        CommRequestVO NMIVO = (CommRequestVO) requestVO;
        CommResponseVO NMIresVO=new CommResponseVO();
        // validateForSale(trackingID,requestVO);


            loadPayAccounts();
            validateForRefund(trackingID,requestVO);
            NMIAccount account= getNMIAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELE_TYPE,REFUND);
            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_USEERNAME,account.getUsername());
            CommTransactionDetailsVO CtransVo = NMIVO.getTransDetailsVO();

            authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());
            authMap.put(ELE_AMOUNT,CtransVo.getAmount());


            String cardParameters = NMIUtils.joinMapValue(authMap, '&');
            String response = NMIUtils.doPostHTTPSURLConnection(NMI_URL,cardParameters);

            Map<String,String> status = NMIUtils.getResponseString(response);
            if(status!=null && !status.equals(""))
            {
                NMIresVO.setTransactionId(status.get("transactionid"));

                if(status.get("response").equalsIgnoreCase("1"))
                {

                    NMIresVO.setStatus("success");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                else
                {
                    NMIresVO.setStatus("fail");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                NMIresVO.setErrorCode(status.get("response_code"));
                NMIresVO.setTransactionType("type");
                NMIresVO.setMerchantOrderId("orderid");

            }
            else
            {
                NMIresVO.setStatus("fail");
                NMIresVO.setDescription(status.get("responsetext"));
            }

        return NMIresVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZDBViolationException, PZTechnicalViolationException, PZConstraintViolationException
    {   CommRequestVO NMIVO = (CommRequestVO) requestVO;
        CommResponseVO NMIresVO=new CommResponseVO();
        // validateForSale(trackingID,requestVO);


            loadPayAccounts();
            validateForCapture(trackingID,requestVO);
            NMIAccount account= getNMIAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELE_TYPE,CAPTURE);
            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_USEERNAME,account.getUsername());
            CommTransactionDetailsVO CtransVo = NMIVO.getTransDetailsVO();

            authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());
            authMap.put(ELE_AMOUNT,CtransVo.getAmount());
            authMap.put(ELE_ORDERID,CtransVo.getOrderId());

            String cardParameters = NMIUtils.joinMapValue(authMap, '&');
            String response = NMIUtils.doPostHTTPSURLConnection(NMI_URL,cardParameters);
            Map<String,String> status = NMIUtils.getResponseString(response);
            if(status!=null && !status.equals(""))
            {
                NMIresVO.setTransactionId(status.get("transactionid"));

                if(status.get("response").equalsIgnoreCase("1"))
                {

                    NMIresVO.setStatus("success");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                else
                {
                    NMIresVO.setStatus("fail");
                    NMIresVO.setDescription(status.get("responsetext"));
                }
                NMIresVO.setErrorCode(status.get("response_code"));
                NMIresVO.setTransactionType("type");
                NMIresVO.setMerchantOrderId("orderid");

            }
            else
            {
                NMIresVO.setStatus("fail");
                NMIresVO.setDescription(status.get("responsetext"));
            }
        return NMIresVO;
    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracking Id not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Refunding the transaction",new Throwable("Tracking Id not provided while Refunding the transaction"));
        }
        if(requestVO ==null)
        {
           PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  not provided while Refunding the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Refunding the transaction",new Throwable("Request  not provided while Refunding the transaction"));
        }
        NMIAccount account= getNMIAccount(accountId);
        if(account ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForRefund()",null,"common","GatewayAccounts not configured while refunding transaction,accountID::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"GatewayAccounts not configured while refunding transaction,accountID::"+accountId,new Throwable("GatewayAccounts not configured while refunding transaction,accountID::"+accountId));
        }
        if(account.getPassword() ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForRefund()",null,"common","Pasword not configured while refunding transaction,accountID::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Password not configured while refunding transaction,accountID::"+accountId,new Throwable("Password not configured while refunding transaction,accountID::"+accountId));
        }
        if(account.getUsername()==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForRefund()",null,"common","UserName not configured while refunding transaction,accountID::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"UserName not configured while refunding transaction,accountID::"+accountId,new Throwable("UserName not configured while refunding transaction,accountID::"+accountId));
        }

        CommRequestVO commRequestVO =  (CommRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transactin Id not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while Refunding the transaction",new Throwable("Previous Transaction Id not provided while Refunding the transaction"));
        }

    }

    private void validateForCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForCapture()",null,"common","Tracking Id not provided while Capturing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Capturing the transaction",new Throwable("Tracking Id not provided while Capturing the transaction"));
        }
        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForCapture()",null,"common","Request  not provided while Capturing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Capturing the transaction",new Throwable("Request  not provided while Capturing the transaction"));
        }
        NMIAccount account= getNMIAccount(accountId);
        if(account ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(), "validateForCapture()", null, "common", "Account not configured while Capturing the transaction", PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Account not configured while Capturing the transaction", new Throwable("Account not configured while Capturing the transaction"));
        }
        if(account.getPassword() ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForCapture()",null,"common","Password not configured while Capturing the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Password not configured while Capturing the transaction",new Throwable("Password not configured while Capturing the transaction"));
        }
        if(account.getUsername()==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForCapture()",null,"common","UserName not configured while Capturing the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"UserName not configured while Capturing the transaction",new Throwable("UserName not configured while Capturing the transaction"));
        }
        CommRequestVO commRequestVO =  (CommRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForCapture()",null,"common","Previous Transaction Id not provided while Capturing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while Capturing the transaction",new Throwable("Previous Transacton Id not provided while Capturing the transaction"));
        }
        if(commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForCapture()",null,"common","Amount not provided while Capturing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Capturing the transaction",new Throwable("Amount not provided while Capturing the transaction"));
        }
    }
    private void validateForVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForVoid()",null,"common","Tracking Id not provided while cancelling the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while cancelling the transaction",new Throwable("Tracking Id not provided while cancelling the transaction"));
        }
        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForVoid()",null,"common","Request  not provided while cancelling the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while cancelling the transaction",new Throwable("Request  not provided while cancelling the transaction"));
        }
        NMIAccount account= getNMIAccount(accountId);
        if(account ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(), "validateForVoid()", null, "common", "Account not configured while cancelling the transaction", PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Account not Configured while cancelling the transaction", new Throwable("Account not configured while cancelling the transaction"));
        }
        if(account.getPassword() ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForVoid()",null,"common","Password not configured while cancelling the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Password not Configured while cancelling the transaction",new Throwable("Password not configured while cancelling the transaction"));
        }
        if(account.getUsername()==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForVoid()",null,"common","UserName not configured while cancelling the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"UserName not Configured while cancelling the transaction",new Throwable("UserName not configured while cancelling the transaction"));
        }
        CommRequestVO commRequestVO =  (CommRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForVoid()",null,"common","Previous Transaction Id not provided while cancelling the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while cancelling the transaction",new Throwable("Previous Transaction Id not provided while cancelling the transaction"));
        }
    }

    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(trackingID ==null || trackingID.equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"RequestVO not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }
        NMIAccount account= getNMIAccount(accountId);
        if(account ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Account not configured while placing transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,errorCodeListVO,"Account not configured while placing transaction,accountId::"+accountId,new Throwable("Account not configured while placing transaction,accountId::"+accountId));
        }
        if(account.getPassword() ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PASSWORD);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Password not configured while placing transaction,accountID::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,errorCodeListVO,"Password not configured while placing transaction,accountId::"+accountId,new Throwable("Password not configured while placing transaction,accountId::"+accountId));
        }
        if(account.getUsername()==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_USERNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","UserName not configured while placing transaction,accountID::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,errorCodeListVO,"UserName not configured while placing transaction,accountId::"+accountId,new Throwable("UserName not configured while placing transaction,accountId::"+accountId));
        }


        CommRequestVO NMIVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = NMIVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= NMIVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","FirstName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"FirstName not provided while placing transaction",new Throwable("FirstName not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","LastName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"LastName not provided while placing transaction",new Throwable("LastName not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Email ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Email ID not provided while placing transaction",new Throwable("Email ID not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
        }

        //Address Details
        if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"State not provided while placing transaction",new Throwable("State not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","ZIP Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"ZIP Code not provided while placing transaction",new Throwable("Zip Code not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getPhone()==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
        }

        GenericCardDetailsVO genericCardDetailsVO= NMIVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO  provided is invalid while placing transaction", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,"Card NO provided is Invalid while placing transaction",new Throwable("Card NO provided is invalid while placing transaction"));
        }

        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZDBViolationException, PZTechnicalViolationException, PZConstraintViolationException
    {
        CommRequestVO NMIVO = (CommRequestVO) requestVO;
        CommResponseVO NMIresVO=new CommResponseVO();
            loadPayAccounts();
            validateForQuery(trackingID, requestVO);
            NMIAccount account= getNMIAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_USEERNAME,account.getUsername());
            CommTransactionDetailsVO CtransVo = NMIVO.getTransDetailsVO();
            authMap.put(ELE_ORDER_ID,trackingID);
            authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());

            String cardParameters = NMIUtils.joinMapValue(authMap, '&');

            String res = NMIUtils.doPostHTTPSURLConnection(NMI_STATUS, cardParameters);

            NMIresVO = NMIUtils.getNMIResponseVOInquiry(res) ;


        return NMIresVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("NMIPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    private void validateForQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForQuery()",null,"common","Tracking Id not provided while Querying the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Querying the transaction",new Throwable("Tracking Id not provided while Querying the transaction"));
        }
        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForQuery()",null,"common","Request  not provided while Querying the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Querying the transaction",new Throwable("Request  not provided while Querying the transaction"));
        }
        NMIAccount account= getNMIAccount(accountId);
        if(account ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(), "validateForQuery()", null, "common", "Account not configured while Querying the transaction,accountId::" + accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Account not configured while Querying the transaction,accountId::" + accountId, new Throwable("Account not configured while Querying the transaction,accountId::" + accountId));
        }
        if(account.getPassword() ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForQuery()",null,"common","Password not configured while Querying the transaction,accountId::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Password not configured while Querying the transaction,accountId::"+accountId,new Throwable("Password not configured while Querying the transaction,accountId::"+accountId));
        }
        if(account.getUsername()==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NMIPaymentGateway.class.getName(),"validateForQuery()",null,"common","UserName not configured while Querying the transaction,accountId::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"UserName not configured while Querying the transaction,accountId::"+accountId,new Throwable("UserName not configured while Querying the transaction,accountId::"+accountId));
        }
        CommRequestVO commRequestVO =  (CommRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(NMIPaymentGateway.class.getName(),"validateForQuery()",null,"common","Previous Transaction Id not provided while Querying the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while Querying the transaction",new Throwable("Previous Transaction Id not provided while Querying the transaction"));
        }
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void loadPayAccounts() throws PZDBViolationException
    {
        log.info("Loading PayWorld Accounts......");
        NMIAccounts = new Hashtable<String, NMIAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_nmi", conn);
            while (rs.next())
            {
                NMIAccount account = new NMIAccount(rs);
                NMIAccounts.put(account.getAccountid() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(NMIPaymentGateway.class.getName(),"loadPayAccounts()",null,"common","Sql Exception while loading Gateway Accounts details For NMI from gateway_accounts_nmi", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(NMIPaymentGateway.class.getName(),"loadPayAccounts()",null,"common","System Error while loading Gateway Accounts details For NMI from gateway_accounts_nmi", PZDBExceptionEnum.SQL_EXCEPTION,null,systemError.getMessage(),systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);


        }
    }

}
