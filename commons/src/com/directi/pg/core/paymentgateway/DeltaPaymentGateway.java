package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.payment.deltapay.core.DeltaPayAccount;
import static com.payment.deltapay.core.Elements.*;

import com.payment.deltapay.core.DeltaPayUtils;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 11/8/13
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeltaPaymentGateway extends AbstractPaymentGateway
{

    private static Logger log = new Logger(DeltaPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DeltaPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "Deltapay";

    private static Hashtable<String, DeltaPayAccount> deltaPayAccounts;
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CommServlet");
    final static String PROXYSCHEME=RB.getString("PROXYSCHEME");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");

    //Configuration

    private final static String DELTAPAY_URL = "https://fastecom.com/gateways/FAST/processFAST.php";

    private final static String REDIRECT_URL = PROXYSCHEME+"://"+PROXYHOST+":"+PROXYPORT+"/icici/servlet/PayDeltaRedirect";




    static
    {
        try
        {
            loadPayAccounts();
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while loading gateway accounts details:::",e);
            transactionLogger.error("PZDBViolationException while loading gateway accounts details:::",e);

            PZExceptionHandler.handleDBCVEException(e,null,"Exception wile loading accounts details for Delta Pay");
        }

    }

    public DeltaPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static DeltaPayAccount getDeltaPayAccount(String accountId)
    {
        return deltaPayAccounts.get(accountId);
    }
    public String getMaxWaitDays()
    {
        return null;
    }

    public static void loadPayAccounts() throws PZDBViolationException
    {
        log.info("Loading DeltaPay Accounts......");
        deltaPayAccounts = new Hashtable<String, DeltaPayAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_deltapay", conn);
            while (rs.next())
            {
                DeltaPayAccount account = new DeltaPayAccount(rs);
                deltaPayAccounts.put(account.getAccountid() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(DeltaPaymentGateway.class.getName(),"loadPayAccounts()",null,"common","SQL Exception while loading gateway Accounts details for Delta Pay", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(DeltaPaymentGateway.class.getName(), "loadPayAccounts()", null, "common", "System Error while loading gateway Accounts details for Delta Pay", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommRequestVO deltaPayVO = (CommRequestVO) requestVO;
        CommResponseVO deltaPayResVO=new CommResponseVO();
        validateForSale(trackingID,requestVO);

            DeltaPayAccount account= getDeltaPayAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();


            authMap.put(ELEM_AFFILIATE,account.getAffiliate());
            authMap.put(ELEM_PAYMETHOD,"Credit Card");
            authMap.put(ELEM_POSTMETHOD,"sync");
            authMap.put(ELEM_PROCESSING_MODE,"sale");
            authMap.put(ELEM_REDIRECT,REDIRECT_URL);
            authMap.put(ELEM_NOTIFICATION_URL,"");
            authMap.put(ELEM_LOCATION,"AFF");
            authMap.put(ELEM_ORDERID,trackingID);
            authMap.put(ELEM_ROOMNAME,account.getRoom_name());
            authMap.put(ELEM_AGENTNAME,account.getAgent_name());
            authMap.put(ELEM_CUSTOMER_ID,"");



            //Getting Address Details
            CommAddressDetailsVO genericAddressDetailsVO = deltaPayVO.getAddressDetailsVO();
            authMap.put(ELEM_FIRST_NAME,genericAddressDetailsVO.getFirstname());
            authMap.put(ELEM_LAST_NAME,genericAddressDetailsVO.getLastname());
            authMap.put(ELEM_ADDRESS1,genericAddressDetailsVO.getStreet());
            authMap.put(ELEM_ZIP,genericAddressDetailsVO.getZipCode());
            authMap.put(ELEM_CITY,genericAddressDetailsVO.getCity());
            authMap.put(ELEM_COUNTRY,genericAddressDetailsVO.getCountry());
            authMap.put(ELEM_STATE,genericAddressDetailsVO.getState());
            authMap.put(ELEM_EMAIL,genericAddressDetailsVO.getEmail());
            authMap.put(ELEM_TELEPHONE,genericAddressDetailsVO.getPhone());
            authMap.put(ELEM_IP,genericAddressDetailsVO.getIp());


            //Getting Transaction Detail
            CommTransactionDetailsVO genericTransactionDetailsVO = deltaPayVO.getTransDetailsVO();
            authMap.put(ELEM_PRODUCT_ID,genericTransactionDetailsVO.getOrderId());
            authMap.put(ELEM_PRODUCT_DESC,genericTransactionDetailsVO.getOrderDesc());
            //BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            //amount = amount.multiply(new BigDecimal(100)) ;
            authMap.put(ELEM_AMOUNT,genericTransactionDetailsVO.getAmount());
            authMap.put(ELEM_CURRENCY,genericTransactionDetailsVO.getCurrency());




            //Getting Card Details
            CommCardDetailsVO genericCardDetailsVO = deltaPayVO.getCardDetailsVO();
            authMap.put(ELEM_CARD_TYPE,genericCardDetailsVO.getCardType());
            authMap.put(ELEM_CC_NUM,genericCardDetailsVO.getCardNum());
            authMap.put(ELEM_CVV,genericCardDetailsVO.getcVV());
            authMap.put(ELEM_EXP_MONTH,genericCardDetailsVO.getExpMonth());
            authMap.put(ELEM_EXP_YEAR,genericCardDetailsVO.getExpYear());


            String cardParameters = DeltaPayUtils.joinMapValue(authMap, '&');
            //log.debug("init param:--- "+cardParameters);
            //log.error("init param:--- "+cardParameters);
            String response = DeltaPayUtils.doPostHTTPSURLConnection(DELTAPAY_URL,cardParameters);
            //log.error("Response===="+response);
            deltaPayResVO = DeltaPayUtils.getdeltaPayResponseVO(response);




        return deltaPayResVO;
    }


    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommRequestVO deltaPayVO = (CommRequestVO) requestVO;
        CommResponseVO deltaPayResVO=new CommResponseVO();
        validateForSale(trackingID,requestVO);


            DeltaPayAccount account= getDeltaPayAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();


            authMap.put(ELEM_AFFILIATE,account.getAffiliate());
            authMap.put(ELEM_PAYMETHOD,"Credit Card");
            authMap.put(ELEM_POSTMETHOD,"sync");
            authMap.put(ELEM_PROCESSING_MODE,"authorize");
            authMap.put(ELEM_REDIRECT,REDIRECT_URL);
            authMap.put(ELEM_NOTIFICATION_URL,"");
            authMap.put(ELEM_LOCATION,"AFF");
            authMap.put(ELEM_ORDERID,trackingID);
            authMap.put(ELEM_ROOMNAME,account.getRoom_name());
            authMap.put(ELEM_AGENTNAME,account.getAgent_name());
            authMap.put(ELEM_CUSTOMER_ID,"");



            //Getting Address Details
            CommAddressDetailsVO genericAddressDetailsVO = deltaPayVO.getAddressDetailsVO();
            authMap.put(ELEM_FIRST_NAME,genericAddressDetailsVO.getFirstname());
            authMap.put(ELEM_LAST_NAME,genericAddressDetailsVO.getLastname());
            authMap.put(ELEM_ADDRESS1,genericAddressDetailsVO.getStreet());
            authMap.put(ELEM_ZIP,genericAddressDetailsVO.getZipCode());
            authMap.put(ELEM_CITY,genericAddressDetailsVO.getCity());
            authMap.put(ELEM_COUNTRY,genericAddressDetailsVO.getCountry());
            authMap.put(ELEM_STATE,genericAddressDetailsVO.getState());
            authMap.put(ELEM_EMAIL,genericAddressDetailsVO.getEmail());
            authMap.put(ELEM_TELEPHONE,genericAddressDetailsVO.getPhone());
            authMap.put(ELEM_IP,genericAddressDetailsVO.getIp());


            //Getting Transaction Detail
            CommTransactionDetailsVO genericTransactionDetailsVO = deltaPayVO.getTransDetailsVO();
            authMap.put(ELEM_PRODUCT_ID,genericTransactionDetailsVO.getOrderId());
            authMap.put(ELEM_PRODUCT_DESC,genericTransactionDetailsVO.getOrderDesc());
            //BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            //amount = amount.multiply(new BigDecimal(100)) ;
            authMap.put(ELEM_AMOUNT,genericTransactionDetailsVO.getAmount());
            authMap.put(ELEM_CURRENCY,genericTransactionDetailsVO.getCurrency());




            //Getting Card Details
            CommCardDetailsVO genericCardDetailsVO = deltaPayVO.getCardDetailsVO();
            authMap.put(ELEM_CARD_TYPE,genericCardDetailsVO.getCardType());
            authMap.put(ELEM_CC_NUM,genericCardDetailsVO.getCardNum());
            authMap.put(ELEM_CVV,genericCardDetailsVO.getcVV());
            authMap.put(ELEM_EXP_MONTH,genericCardDetailsVO.getExpMonth());
            authMap.put(ELEM_EXP_YEAR,genericCardDetailsVO.getExpYear());


            String cardParameters = DeltaPayUtils.joinMapValue(authMap, '&');
            //log.debug("init param:--- "+cardParameters);
            //log.error("init param:--- "+cardParameters);
            String response = DeltaPayUtils.doPostHTTPSURLConnection(DELTAPAY_URL,cardParameters);
            //log.error("Response===="+response);
            deltaPayResVO = DeltaPayUtils.getdeltaPayResponseVO(response);



        return deltaPayResVO;
    }

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
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id not provided while placing the transaction",new Throwable("Tracking Id not provided while placing the transaction"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Request not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Request not provided while placing the transaction",new Throwable("Request not provided while placing the transaction"));
        }

        CommRequestVO deltaPayRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = deltaPayRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Transaction Details not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Transaction Details not provided while placing the transaction",new Throwable("Transaction Details not provided while placing the transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing the transaction",new Throwable("Amount not provided while placing the transaction"));
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Currency not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Currency not provided while placing the transaction",new Throwable("Currency not provided while placing the transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= deltaPayRequestVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"AddressDetails not provided while placing the transaction",new Throwable("AddressDetails not provided while placing the transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"First Name not provided while placing the transaction",new Throwable("First Name not provided while placing the transaction"));

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Last Name not provided while placing the transaction",new Throwable("Last Name not provided while placing the transaction"));

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Email Id not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Email ID not provided while placing the transaction",new Throwable("Email ID not provided while placing the transaction"));
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Ip Address not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Ip Address not provided while placing the transaction",new Throwable("Ip Address not provided while placing the transaction"));
        }

        //Address Details
        if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Street not provided while placing the transaction",new Throwable("Street not provided while placing the transaction"));
        }

        if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"City not provided while placing the transaction",new Throwable("City not provided while placing the transaction"));
        }
        if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Country not provided while placing the transaction",new Throwable("Country not provided while placing the transaction"));
        }

        if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","State not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"State not provided while placing the transaction",new Throwable("State not provided while placing the transaction"));
        }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Zip Code not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Zip Code not provided while placing the transaction",new Throwable("Zip Code not provided while placing the transaction"));
        }
        if(genericAddressDetailsVO.getPhone()==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Phone NO not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Phone NO not provided while placing the transaction",new Throwable("Phone NO not provided while placing the transaction"));
        }

        GenericCardDetailsVO genericCardDetailsVO= deltaPayRequestVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CardDetails not provided while placing the transaction",new Throwable("CardDetails not provided while placing the transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Card Number not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card Number not provided while placing the transaction",new Throwable("Card Number not provided while placing the transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Card Number provided is invalid while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card Number provided is invalid while placing the transaction",new Throwable("CardNumber provided is Invalid while placing the transaction"));
        }

        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CVV not provided while placing the transaction",new Throwable("CVV not provided while placing the transaction"));
        }
        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Month not provided while placing the transaction",new Throwable("Expiry Month not provided while placing the transaction"));
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year not provided while placing the transaction",new Throwable("Expiry Year not provided while placing the transaction"));
        }

        if(genericCardDetailsVO.getCardType()==null || genericCardDetailsVO.getCardType().equals(""))
        {
            
            String cardType =Functions.getCardType(genericCardDetailsVO.getCardNum());

            if(cardType ==null || cardType.equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CARD_TYPE);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForSale()",null,"common","Card Type not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card Type not provided while placing the transaction",new Throwable("Card Type not provided while placing the transaction"));
            }

            genericCardDetailsVO.setCardType(getCardTypeForDelta(cardType));

        }
    }
    
    private String getCardTypeForDelta(String cardType)
    {

        String newCardType="";

        if(cardType.equals("VISA"))
        {
            newCardType = "Visa";
                    }
        else  if(cardType.equals("MC"))
        {
            newCardType = "MasterCard";
        }
        else if(cardType.equals("AMEX"))
        {
            newCardType = "Amex";
        }
        else if (cardType.equals("DISC"))
        {
            newCardType = "Discover";
        }
        else if (cardType.equals("CUP"))
        {
            newCardType = "CUP";
        }
        else
        {
            newCardType = "";
        }

        return newCardType;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();
         //validateinquiry(trackingID,requestVO);
            if(trackingID ==null || trackingID.equals(""))
            {
                log.info("TrackingId not provided");
                transactionLogger.info("TrackingId not provided");
                PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"processQuery()",null,"common","Tracking Id not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing the transaction",new Throwable("Tracking Id not provided while placing the transaction"));
            }
            CommRequestVO deltaPayVO = (CommRequestVO) requestVO;
            DeltaPayAccount account= getDeltaPayAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELEM_AFFILIATE,account.getAffiliate());
            authMap.put(ELEM_PROCESSING_MODE,"transaction_status");
            authMap.put(ELEM_POSTMETHOD,"sync");
            authMap.put(ELEM_REDIRECT,REDIRECT_URL);
            authMap.put(ELEM_ORDERID,trackingID);

            CommTransactionDetailsVO CtransVo = deltaPayVO.getTransDetailsVO();
            //authMap.put(ELEM_REF_TRANS_NO,CtransVo.getPreviousTransactionId());


            String checkstatus = DeltaPayUtils.joinMapValue(authMap, '&');

            String response = DeltaPayUtils.doPostHTTPSURLConnection(DELTAPAY_URL,checkstatus);
            log.error("response"+response);
            commResponseVO = DeltaPayUtils.getdeltaPayResponseVO(response);


        return commResponseVO;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();

            //loadPayAccounts();
            validateForRefund(trackingID,requestVO);
            CommRequestVO deltaPayVO = (CommRequestVO) requestVO;
            DeltaPayAccount account= getDeltaPayAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELEM_AFFILIATE,account.getAffiliate());
            authMap.put(ELEM_PAYMETHOD,"Credit Card");
            authMap.put(ELEM_POSTMETHOD,"sync");
            authMap.put(ELEM_PROCESSING_MODE,"refund");
            authMap.put(ELEM_REDIRECT,REDIRECT_URL);
            authMap.put(ELEM_ROOMNAME,account.getRoom_name());
            authMap.put(ELEM_ORDERID,trackingID);

            CommTransactionDetailsVO CtransVo = deltaPayVO.getTransDetailsVO();
            authMap.put(ELEM_REF_TRANS_NO,CtransVo.getPreviousTransactionId());
            authMap.put(ELEM_CURRENCY,CtransVo.getCurrency());
            authMap.put(ELEM_AMOUNT,CtransVo.getAmount());

            String refundParameters = DeltaPayUtils.joinMapValue(authMap,'&');

            String response = DeltaPayUtils.doPostHTTPSURLConnection(DELTAPAY_URL,refundParameters);
            log.error("response==="+response);
            commResponseVO = DeltaPayUtils.getdeltaPayResponseVO(response);




        return commResponseVO;
    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForRefund()",null,"common","TrackingId not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"TrackingId not provided while placing transaction",new Throwable("TrackingId not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request Vo not provided while placing transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request not provided while placing transaction",new Throwable("Request not provided while placing transaction"));
        }

        CommRequestVO deltaPayRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = deltaPayRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForRefund()",null,"common","TransactionDetails not provided while placing transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails not provided while placing transaction",new Throwable("TransactionDetails not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(DeltaPaymentGateway.class.getName(),"validateForRefund()",null,"common","Currency not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
        }


    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();

            //loadPayAccounts();
            validateForRefund(trackingID,requestVO);
            CommRequestVO deltaPayVO = (CommRequestVO) requestVO;
            DeltaPayAccount account= getDeltaPayAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELEM_AFFILIATE,account.getAffiliate());
            authMap.put(ELEM_PAYMETHOD,"Credit Card");
            authMap.put(ELEM_POSTMETHOD,"sync");
            authMap.put(ELEM_PROCESSING_MODE,"void");
            authMap.put(ELEM_REDIRECT,REDIRECT_URL);
            authMap.put(ELEM_ROOMNAME,account.getRoom_name());
            authMap.put(ELEM_ORDERID,trackingID);

            CommTransactionDetailsVO CtransVo = deltaPayVO.getTransDetailsVO();
            authMap.put(ELEM_REF_TRANS_NO,CtransVo.getPreviousTransactionId());
            authMap.put(ELEM_CURRENCY,CtransVo.getCurrency());
            authMap.put(ELEM_AMOUNT,CtransVo.getAmount());
            String refundParameters = DeltaPayUtils.joinMapValue(authMap,'&');

            String response = DeltaPayUtils.doPostHTTPSURLConnection(DELTAPAY_URL,refundParameters);
            log.error("response"+response);
            commResponseVO = DeltaPayUtils.getdeltaPayResponseVO(response);



        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();

            //loadPayAccounts();
            validateForRefund(trackingID,requestVO);
            CommRequestVO deltaPayVO = (CommRequestVO) requestVO;
            DeltaPayAccount account= getDeltaPayAccount(accountId);
            Map<String, String> authMap = new TreeMap<String, String>();

            authMap.put(ELEM_AFFILIATE,account.getAffiliate());
            authMap.put(ELEM_PAYMETHOD,"Credit Card");
            authMap.put(ELEM_POSTMETHOD,"sync");
            authMap.put(ELEM_PROCESSING_MODE,"settlement");
            authMap.put(ELEM_REDIRECT,REDIRECT_URL);
            authMap.put(ELEM_ROOMNAME,account.getRoom_name());
            authMap.put(ELEM_ORDERID,trackingID);

            CommTransactionDetailsVO CtransVo = deltaPayVO.getTransDetailsVO();
            authMap.put(ELEM_REF_TRANS_NO,CtransVo.getPreviousTransactionId());
            authMap.put(ELEM_CURRENCY,CtransVo.getCurrency());
            authMap.put(ELEM_AMOUNT,CtransVo.getAmount());

            String refundParameters = DeltaPayUtils.joinMapValue(authMap,'&');

            String response = DeltaPayUtils.doPostHTTPSURLConnection(DELTAPAY_URL,refundParameters);
            log.error("response"+response);
            commResponseVO = DeltaPayUtils.getdeltaPayResponseVO(response);



        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("DeltaPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }



}
