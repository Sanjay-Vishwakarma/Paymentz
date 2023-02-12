package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.payworld.PayWorldAccount;
import com.directi.pg.core.payworld.PayWorldUtils;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/24/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayWorldPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(PayWorldPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayWorldPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "Payworld";
    public static final String GATEWAY_TYPE_RITUMU = "RitumuBank";
    public final static String ELE_GUID = "guid";
    public final static String ELE_PASSWORD = "pwd";
    public final static String ELE_RS = "rs";
    public final static String ELE_UNIQUE_ID = "merchant_transaction_id";
    public final static String ELE_IP = "user_ip";
    public final static String ELE_DESCRIPTION = "description";
    public final static String ELE_AMOUNT = "amount";
    public final static String ELE_CURRENCY = "currency";
    public final static String ELE_CARD_NAME = "name_on_card";
    public final static String ELE_STREET = "street";
    public final static String ELE_CITY = "city";
    public final static String ELE_COUNTRY = "country";
    public final static String ELE_STATE = "state";
    public final static String ELE_ZIP = "zip";
    public final static String ELE_PHONE = "phone";
    public final static String ELE_EMAIL = "email";
    public final static String ELE_RETURN_URL = "merchant_site_url";
    public final static String ELE_BIN_NUMBER = "card_bin";
    public final static String ELE_CARD_NUM = "cc";
    public final static String ELE_CVV = "cvv";
    public final static String ELE_EXPIRE_CARD = "expire";
    public final static String ELE_INIT_ID = "init_transaction_id";
    public final static String ELE_EXTANDED_ID = "f_extended";
    public final static String ELE_ACCOUNT_GUID = "account_guid";
    public final static String ELE_AMOUNT_REFUND = "amount_to_refund";
    public final static String ELE_TRANS_TYPE="request_type";

    //Configuration

    /*private final static String PAYWORLD_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=init";
    private final static String Payworld_charge_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=charge";
    private final static String Payworld_Refund = "https://www2.1stpayments.net/gwprocessor2.php?a=refund";
    private final static String Payworld_Inquiry = "https://www2.1stpayments.net/gwprocessor2.php?a=status_request";*/

    private final static String PAYWORLD_URL = "https://87.110.182.18:8443/gw2test/gwprocessor2.php?a=init";
    private final static String Payworld_charge_URL = "https://87.110.182.18:8443/gw2test/gwprocessor2.php?a=charge";
    private final static String Payworld_Refund = "https://87.110.182.18:8443/gw2test/gwprocessor2.php?a=refund";
    private final static String Payworld_Inquiry = "https://87.110.182.18:8443/gw2test/gwprocessor2.php?a=status_request";
    private static Hashtable<String, PayWorldAccount> PayWorldAccounts;

    static
    {
        try
        {
            loadPayAccounts();
        }
        catch (PZDBViolationException dbe)
        {
            log.error("Error while loading gateway accounts : " + Util.getStackTrace(dbe));
            transactionLogger.error("Error while loading gateway accounts : " + Util.getStackTrace(dbe));
            PZExceptionHandler.handleDBCVEException(dbe,null,null);
            //throw new RuntimeException(e);
        }
    }

    public PayWorldPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static PayWorldAccount getPayWorldAccount(String accountId)
    {
        return PayWorldAccounts.get(accountId);
    }
    public String getMaxWaitDays()
    {
        return null;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("PayWorldPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO payWorldVO = (CommRequestVO) requestVO;
        CommResponseVO payWorldresVO=new CommResponseVO();
        Functions functions=new Functions();
        validateForSale(trackingID,requestVO);


            loadPayAccounts();
            PayWorldAccount account= getPayWorldAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();
            Map<String, String> chargemap = new TreeMap<String, String>();
            Map<String, String> chargemapLog = new TreeMap<String, String>();

            authMap.put(ELE_GUID,account.getGuid());
            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_RS,account.getRS());
            authMap.put(ELE_UNIQUE_ID,trackingID);

            //Getting Transaction Detail
            CommTransactionDetailsVO genericTransactionDetailsVO = payWorldVO.getTransDetailsVO();
            authMap.put(ELE_DESCRIPTION,genericTransactionDetailsVO.getOrderDesc());
            BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            authMap.put(ELE_AMOUNT,String.valueOf(amount.intValue()));
            authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());


            //Getting Address Details
            CommAddressDetailsVO genericAddressDetailsVO = payWorldVO.getAddressDetailsVO();
            authMap.put(ELE_IP,genericAddressDetailsVO.getIp());
            authMap.put(ELE_STREET,genericAddressDetailsVO.getStreet());
            authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
            authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
            authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
            authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
            authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
            authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
            String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

            //Getting Card Details
            CommCardDetailsVO genericCardDetailsVO = payWorldVO.getCardDetailsVO();
            String bin=genericCardDetailsVO.getCardNum();
            String number=bin.substring(0,6);
            authMap.put(ELE_CARD_NAME,name);
            authMap.put(ELE_BIN_NUMBER,number);
            chargemap.put(ELE_CARD_NUM,genericCardDetailsVO.getCardNum());
            chargemap.put(ELE_CVV,genericCardDetailsVO.getcVV());
            chargemap.put(ELE_EXPIRE_CARD,genericCardDetailsVO.getExpMonth()+"/"+genericCardDetailsVO.getExpYear().substring(2));

            chargemapLog.put(ELE_CARD_NUM,functions.maskingPan(genericCardDetailsVO.getCardNum()));
            chargemapLog.put(ELE_CVV,functions.maskingNumber(genericCardDetailsVO.getcVV()));
            chargemapLog.put(ELE_EXPIRE_CARD,functions.maskingNumber(genericCardDetailsVO.getExpMonth())+"/"+functions.maskingNumber(genericCardDetailsVO.getExpYear().substring(2)));
            String cardParameters = PayWorldUtils.joinMapValue(authMap,'&');
            //log.debug("init param:--- "+cardParameters);
            transactionLogger.error("init param:--"+trackingID+"- "+cardParameters);
            String response = PayWorldUtils.doPostHTTPSURLConnection(PAYWORLD_URL,authMap);
            transactionLogger.error("response--"+trackingID+"--"+response);
            if(response!=null || !response.equals("") )
            {
                String res[]=response.split(":");
                String initid=res[1];
                String text=res[0];
                if(text!=null || !text.equals("") && initid!=null || !initid.equals(""))
                {
                    if(text.equals("OK"))
                    {
                        chargemap.put(ELE_EXTANDED_ID,"4");
                        chargemap.put(ELE_INIT_ID,initid);

                        chargemapLog.put(ELE_EXTANDED_ID,"4");
                        chargemapLog.put(ELE_INIT_ID,initid);
                        String card = PayWorldUtils.joinMapValue(chargemap, '&');
                        String cardLog = PayWorldUtils.joinMapValue(chargemap, '&');

                        String response2 = PayWorldUtils.doPostHTTPSURLConnection(Payworld_charge_URL,chargemap);
                        transactionLogger.error("response2    ---"+trackingID+"--  "+response2);
                        if(response2.indexOf("ERROR")==0)
                        {
                            String error="";
                            String id="";
                            String res1[]=response2.split(":");
                            error=res1[2];
                            id=res1[5];
                            payWorldresVO.setTransactionId(initid);
                            payWorldresVO.setDescription(error);
                            payWorldresVO.setStatus("fail");
                        }
                        else
                        {
                            payWorldresVO= PayWorldUtils.getpayWorldResponseVO(response2);
                            if(payWorldresVO.getErrorCode()!=null || !payWorldresVO.getErrorCode().equalsIgnoreCase(""))
                            {
                                payWorldresVO.setDescription(loadreason(payWorldresVO.getErrorCode()));
                            }
                            else
                            {
                                payWorldresVO.setDescription(payWorldresVO.getStatus());
                            }
                        }
                    }
                    else
                    {
                        payWorldresVO.setStatus("fail");
                        payWorldresVO.setDescription("fail");
                        payWorldresVO.setTransactionId(initid);
                    }
                }
                else
                {
                    payWorldresVO.setStatus("fail");
                    payWorldresVO.setDescription("fail");
                    payWorldresVO.setTransactionStatus("fail");
                }
            }
            else
            {
                payWorldresVO.setStatus("fail");
                payWorldresVO.setDescription("fail");
                payWorldresVO.setTransactionStatus("fail");
            }


        return payWorldresVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommResponseVO ResponseVO = new CommResponseVO();
         //validateinquiry(trackingID,requestVO);
            CommRequestVO payWorldVO = (CommRequestVO) requestVO;
            PayWorldAccount account= getPayWorldAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELE_GUID,account.getGuid());
            authMap.put(ELE_PASSWORD,account.getPassword());
            authMap.put(ELE_TRANS_TYPE,"transaction_status");
            CommTransactionDetailsVO CtransVo = payWorldVO.getTransDetailsVO();
            authMap.put(ELE_INIT_ID,CtransVo.getPreviousTransactionId());
            authMap.put(ELE_EXTANDED_ID,"4");

            String checkstatus = PayWorldUtils.joinMapValue(authMap, '&');

            String response2 = PayWorldUtils.doPostHTTPSURLConnection(Payworld_Inquiry,authMap);

            String res[]=response2.split("~");
            Map<String,String> responseString= new TreeMap<String, String>();
            for(int i=0;i<res.length;i++)
            {
                String field=res[i];
                String temp[]=field.split(":");
                String response[]=new String[2];

                for(int x=0;x<temp.length;x++)
                {
                    response[x]=temp[x];
                }
                if(response[1]!=null && !response[1].equalsIgnoreCase(""))
                {
                    responseString.put(response[0],response[1]);
                }
                else
                {
                    responseString.put(response[0],"");
                }
            }
            if(responseString!=null)
            {
                ResponseVO.setStatus((String)responseString.get("Status"));
                ResponseVO.setTransactionId((String)responseString.get("ID"));
                ResponseVO.setMerchantId((String)responseString.get("MerchantID"));
                ResponseVO.setErrorCode((String)responseString.get("ApprovalCode"));

            }
            else
            {
                ResponseVO.setStatus("fail");
                ResponseVO.setDescription("fail");
                ResponseVO.setTransactionStatus("fail");
            }


        return ResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        CommResponseVO ResponseVO = new CommResponseVO();

            loadPayAccounts();
            validateForrefund(trackingID,requestVO);
            CommRequestVO payWorldVO = (CommRequestVO) requestVO;
            PayWorldAccount account= getPayWorldAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELE_ACCOUNT_GUID,account.getGuid());
            authMap.put(ELE_PASSWORD,account.getPassword());

            CommTransactionDetailsVO CtransVo = payWorldVO.getTransDetailsVO();
            authMap.put(ELE_INIT_ID,CtransVo.getPreviousTransactionId());
            BigDecimal amount = new BigDecimal(CtransVo.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            authMap.put(ELE_AMOUNT_REFUND,String.valueOf(amount.intValue()));

            String refundParameters = PayWorldUtils.joinMapValue(authMap,'&');

            String response = PayWorldUtils.doPostHTTPSURLConnection(Payworld_Refund,authMap);
            //System.out.println(response);
            /*if(response!=null)
            {
                if(response.equalsIgnoreCase("Refund Success"))
                {
                    ResponseVO.setStatus("success");
                    ResponseVO.setTransactionStatus(response);
                }
                else
                {
                    ResponseVO.setStatus("fail");
                    ResponseVO.setTransactionStatus(response);
                }

            } */

        return ResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    private void validateinquiry(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            log.info("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }

        PayWorldAccount account= getPayWorldAccount(accountId);
        if(account.getPassword() ==null)
        {
            log.info("account password not provided");
            throw new SystemError("account password not provided");
        }
        if(account.getGuid() ==null)
        {
            log.info("account Guid not provided");
            throw new SystemError("account Guid not provided");
        }
        CommRequestVO payWorldRequestVO  =    (CommRequestVO)requestVO;
        CommTransactionDetailsVO CtransVo = payWorldRequestVO.getTransDetailsVO();
        if(CtransVo ==null)
        {
            log.info("transDetails input not provided");
            throw new SystemError("transDetails input not provided");
        }
        if(CtransVo.getPreviousTransactionId() == null || CtransVo.getPreviousTransactionId().equals(""))
        {
            log.info("detailID not provided");
            throw new SystemError("detailID not provided");
        }

    }

    private void validateForrefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForrefund()",null,"common","Tracking Id not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Refunding the transaction",new Throwable("Tracking Id not provided while Refunding the transaction"));
        }

        PayWorldAccount account= getPayWorldAccount(accountId);
        if(account.getPassword() ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayWorldPaymentGateway.class.getName(), "validateForrefund()", null, "common", "Password not Configured while Refunding the transaction", PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Password not configured while Refunding the transaction", new Throwable("Password not configured while Refunding the transaction"));
        }
        if(account.getGuid() ==null)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayWorldPaymentGateway.class.getName(),"validateForrefund()",null,"common","GUID not Configured while Refunding the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"GUID not configured while Refunding the transaction",new Throwable("GUID not configured while Refunding the transaction"));
        }
        CommRequestVO payWorldRequestVO  =    (CommRequestVO)requestVO;
        CommTransactionDetailsVO CtransVo = payWorldRequestVO.getTransDetailsVO();
        if(CtransVo ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForrefund()",null,"common","TransactonDetails  not provided while Refunding the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while Refunding the transaction",new Throwable("TransactionDetails  not provided while Refunding the transaction"));
        }
        if(CtransVo.getAmount() == null || CtransVo.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForrefund()",null,"common","Amount not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Refunding the transaction",new Throwable("Amount not provided while Refunding the transaction"));
        }
        if(CtransVo.getPreviousTransactionId() == null || CtransVo.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForrefund()",null,"common","Previous Transaction Id not provided while Refunding the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while Refunding the transaction",new Throwable("Previous Transaction Id not provided while Refunding the transaction"));
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
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }
        PayWorldAccount account= getPayWorldAccount(accountId);
        if(account ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseTechnicalViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Account not not configured while placing transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,errorCodeListVO,"Account not not configured while placing transaction,accountId::"+accountId,new Throwable("Account not not configured while placing transaction,accountId::"+accountId));
        }
        if(account.getPassword() ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PASSWORD);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseTechnicalViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Password not not configured while placing transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,errorCodeListVO,"Password not not configured while placing transaction,accountId::"+accountId,new Throwable("Password not not configured while placing transaction,accountId::"+accountId));
        }
        if(account.getGuid() ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseTechnicalViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","GUID not not configured while placing transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,errorCodeListVO,"GUID not not configured while placing transaction,accountId::"+accountId,new Throwable("GUID not not configured while placing transaction,accountId::"+accountId));
        }
        if(account.getRS() ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseTechnicalViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","RSS String not not configured while placing transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,errorCodeListVO,"RSS String not configured while placing transaction,accountId::"+accountId,new Throwable("RSS String  not configured while placing transaction,accountId::"+accountId));
        }

        CommRequestVO payWorldRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = payWorldRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= payWorldRequestVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","FirstName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"FirstName not provided while placing transaction",new Throwable("FirstName not provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","LastName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"LastName not provided while placing transaction",new Throwable("LastName not provided while placing transaction"));

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Email Id not provided while placing transaction",new Throwable("Email Id not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
        }

        //Address Details
        if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","State  not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"State not provided while placing transaction",new Throwable("State not provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","ZIP Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Zip Code not provided while placing transaction",new Throwable("Zip Code not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getPhone()==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
        }

        GenericCardDetailsVO genericCardDetailsVO= payWorldRequestVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"CArdDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO  provided is invalid while placing transaction", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,"Card NO  provided is invalid while placing transaction",new Throwable("Card NO provided is Invalid while placing transaction"));
        }

        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PayWorldPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }
    }

    public String loadreason(String rs) throws PZDBViolationException
    {
        String failreason="";
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String pgtypeid= account.getGateway();
        try
        {
            Connection connection= Database.getConnection();
            String qry="select * from rs_codes where gateway='"+pgtypeid+"' and code='"+rs+"'";
            PreparedStatement p=connection.prepareStatement(qry);
            ResultSet result=p.executeQuery();
            if(result.next())
            {
                failreason=result.getString("reason");
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(PayWorldPaymentGateway.class.getName(), "loadreason()", null, "common", "System Error while loading reason from the gateway", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PayWorldPaymentGateway.class.getName(), "loadreason()", null, "common", "System Error while loading reason from the gateway", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, e.getMessage(), e.getCause());
        }
        return failreason;
    }

    public static void loadPayAccounts() throws PZDBViolationException
    {
        log.info("Loading PayWorld Accounts......");
        PayWorldAccounts = new Hashtable<String, PayWorldAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_payworld", conn);
            while (rs.next())
            {
                PayWorldAccount account = new PayWorldAccount(rs);
                PayWorldAccounts.put(account.getAccountid() + "", account);
            }
        }
        catch (SQLException q)
        {
            PZExceptionHandler.raiseDBViolationException("PaygatewayPaymentGateway.java","loadPayAccounts()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,q.getMessage(),q.getCause());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaygatewayPaymentGateway.java","loadPayAccounts()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
}