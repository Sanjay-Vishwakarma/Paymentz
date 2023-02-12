package com.directi.pg.core.paymentgateway;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.fluzznetwork.FluzznetworkUtils;

import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 10/25/13
 * Time: 7:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class FluzznetworkPaymentGateway  extends AbstractPaymentGateway
{
    private static Logger log = new Logger(FluzznetworkPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(FluzznetworkPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "Fluzz";


    public final static String ELE_TYPE = "type";
    public final static String ELE_USERNAME = "username";
    public final static String ELE_PASSWORD = "password";
    public final static String ELE_CCNUMBER = "ccnumber";
    public final static String ELE_CCEXP = "ccexp";
    public final static String ELE_CVV = "cvv";
    public final static String ELE_AMOUNT = "amount";
    public final static String ELE_CURRENCY = "currency";
    public final static String ELE_ORDERDESCRIPTION= "orderdescription";
    public final static String ELE_ORDERID = "orderid";
    public final static String ELE_IPADDRESS= "ipaddress";
    public final static String ELE_FIRSTNAME= "firstname";
    public final static String ELE_LASTNAME = "lastname";
    public final static String ELE_ADDRESS1 = "address1";
    public final static String ELE_CITY = "city";
    public final static String ELE_STATE = "state";
    public final static String ELE_ZIP = "zip";
    public final static String ELE_COUNTRY = "country";
    public final static String ELE_PHONE= "phone";
    public final static String ELE_EMAIL = "email";
    public final static String ELE_TRANSACTIONID= "transactionid";




    public final static String SALE= "sale";
    public final static String AUTH = "auth";
    public final static String CAPTURE= "capture";
    public final static String VOID = "void";
    public final static String REFUND = "refund";



    //configuration
    private final static String Fn_URL = "https://secure.wspgateway.com/api/transact.php";
    private final static String Fn_STATUS="https://secure.wspgateway.com/api/query.php";


    public FluzznetworkPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        Functions functions = new Functions();
        CommRequestVO fluzznetworkVO = (CommRequestVO) requestVO;
        CommResponseVO fluzznetworkresVO=new CommResponseVO();

        //validateForSale(trackingID,requestVO);
        Map<String, String> authMap = new TreeMap<String, String>();
        authMap.put(ELE_TYPE,SALE);
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_ORDERID,trackingID);

        Map<String, String> authMaplog = new TreeMap<String, String>();
        authMaplog.put(ELE_TYPE,SALE);
        authMaplog.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMaplog.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMaplog.put(ELE_ORDERID,trackingID);

        //Getting Transaction Detail
        CommTransactionDetailsVO genericTransactionDetailsVO = fluzznetworkVO.getTransDetailsVO();
        authMap.put(ELE_ORDERDESCRIPTION,genericTransactionDetailsVO.getOrderDesc());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());

        CommTransactionDetailsVO genericTransactionDetailsVOlog = fluzznetworkVO.getTransDetailsVO();
        authMaplog.put(ELE_ORDERDESCRIPTION,genericTransactionDetailsVOlog.getOrderDesc());
        authMaplog.put(ELE_AMOUNT,genericTransactionDetailsVOlog.getAmount());
        authMaplog.put(ELE_CURRENCY,genericTransactionDetailsVOlog.getCurrency());

        //Getting Address Details
        CommAddressDetailsVO genericAddressDetailsVO = fluzznetworkVO.getAddressDetailsVO();
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS1,genericAddressDetailsVO.getStreet());

        CommAddressDetailsVO genericAddressDetailsVOlog = fluzznetworkVO.getAddressDetailsVO();
        authMaplog.put(ELE_IPADDRESS,genericAddressDetailsVOlog.getIp());
        authMaplog.put(ELE_ZIP,genericAddressDetailsVOlog.getZipCode());
        authMaplog.put(ELE_CITY,genericAddressDetailsVOlog.getCity());
        authMaplog.put(ELE_COUNTRY,genericAddressDetailsVOlog.getCountry());
        authMaplog.put(ELE_STATE,genericAddressDetailsVOlog.getState());
        authMaplog.put(ELE_EMAIL,genericAddressDetailsVOlog.getEmail());
        authMaplog.put(ELE_PHONE,genericAddressDetailsVOlog.getPhone());
        authMaplog.put(ELE_FIRSTNAME,genericAddressDetailsVOlog.getFirstname());
        authMaplog.put(ELE_LASTNAME,genericAddressDetailsVOlog.getLastname());
        authMaplog.put(ELE_ADDRESS1,genericAddressDetailsVOlog.getStreet());

        //String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

        //Getting Card Details
        CommCardDetailsVO genericCardDetailsVO = fluzznetworkVO.getCardDetailsVO();
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMap.put(ELE_CCEXP,genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear().substring(2));

        CommCardDetailsVO genericCardDetailsVOlog = fluzznetworkVO.getCardDetailsVO();
        authMaplog.put(ELE_CCNUMBER,functions.maskingPan(genericCardDetailsVOlog.getCardNum()));
        authMaplog.put(ELE_CVV,functions.maskingNumber(genericCardDetailsVOlog.getcVV()));
        authMaplog.put(ELE_CCEXP,functions.maskingExpiry(genericCardDetailsVOlog.getExpMonth()+genericCardDetailsVOlog.getExpYear().substring(2)));

        String cardParameters = FluzznetworkUtils.joinMapValue(authMap, '&');
        String cardParameterslog = FluzznetworkUtils.joinMapValue(authMaplog, '&');
        log.debug("fuzz sale request----"+trackingID + "--" + cardParameterslog);
        transactionLogger.debug("fuzz sale request----"+trackingID + "--" + cardParameterslog);
        String response = FluzznetworkUtils.doPostHTTPSURLConnection(Fn_URL,cardParameters);
        log.debug("Fluzz sale response----"+trackingID + "--" + response);
        transactionLogger.debug("Fluzz sale response----"+trackingID + "--" + response);

        Map<String,String> status = FluzznetworkUtils.getResponseString(response);
        if(status!=null && !status.equals(""))
        {
            fluzznetworkresVO.setTransactionId(status.get("transactionid"));

            if(status.get("response").equalsIgnoreCase("1"))
            {

                fluzznetworkresVO.setStatus("success");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            else
            {
                fluzznetworkresVO.setStatus("fail");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            fluzznetworkresVO.setErrorCode(status.get("response_code"));
            fluzznetworkresVO.setTransactionType(status.get("type"));


        }
        else
        {
            fluzznetworkresVO.setStatus("fail");
            fluzznetworkresVO.setDescription(status.get("responsetext"));
        }

        return fluzznetworkresVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        Functions functions = new Functions();
        CommRequestVO fluzznetworkVO = (CommRequestVO) requestVO;
        CommResponseVO fluzznetworkresVO=new CommResponseVO();

        //validateForSale(trackingID,requestVO);
        Map<String, String> authMap = new TreeMap<String, String>();
        authMap.put(ELE_TYPE,AUTH);
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_ORDERID,trackingID);

        Map<String, String> authMaplog = new TreeMap<String, String>();
        authMaplog.put(ELE_TYPE,AUTH);
        authMaplog.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMaplog.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMaplog.put(ELE_ORDERID,trackingID);


        //Getting Transaction Detail
        CommTransactionDetailsVO genericTransactionDetailsVO = fluzznetworkVO.getTransDetailsVO();
        authMap.put(ELE_ORDERDESCRIPTION,genericTransactionDetailsVO.getOrderDesc());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());

        CommTransactionDetailsVO genericTransactionDetailsVOlog = fluzznetworkVO.getTransDetailsVO();
        authMaplog.put(ELE_ORDERDESCRIPTION,genericTransactionDetailsVOlog.getOrderDesc());
        authMaplog.put(ELE_AMOUNT,genericTransactionDetailsVOlog.getAmount());
        authMaplog.put(ELE_CURRENCY,genericTransactionDetailsVOlog.getCurrency());

        //Getting Address Details
        CommAddressDetailsVO genericAddressDetailsVO = fluzznetworkVO.getAddressDetailsVO();
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS1,genericAddressDetailsVO.getStreet());

        CommAddressDetailsVO genericAddressDetailsVOlog = fluzznetworkVO.getAddressDetailsVO();
        authMaplog.put(ELE_IPADDRESS,genericAddressDetailsVOlog.getIp());
        authMaplog.put(ELE_ZIP,genericAddressDetailsVOlog.getZipCode());
        authMaplog.put(ELE_CITY,genericAddressDetailsVOlog.getCity());
        authMaplog.put(ELE_COUNTRY,genericAddressDetailsVOlog.getCountry());
        authMaplog.put(ELE_STATE,genericAddressDetailsVOlog.getState());
        authMaplog.put(ELE_EMAIL,genericAddressDetailsVOlog.getEmail());
        authMaplog.put(ELE_PHONE,genericAddressDetailsVOlog.getPhone());
        authMaplog.put(ELE_FIRSTNAME,genericAddressDetailsVOlog.getFirstname());
        authMaplog.put(ELE_LASTNAME,genericAddressDetailsVOlog.getLastname());
        authMaplog.put(ELE_ADDRESS1,genericAddressDetailsVOlog.getStreet());
        //String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

        //Getting Card Details
        CommCardDetailsVO genericCardDetailsVO = fluzznetworkVO.getCardDetailsVO();
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMap.put(ELE_CCEXP,genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear().substring(2));

        CommCardDetailsVO genericCardDetailsVOlog = fluzznetworkVO.getCardDetailsVO();
        authMaplog.put(ELE_CCNUMBER,functions.maskingPan(genericCardDetailsVOlog.getCardNum()));
        authMaplog.put(ELE_CVV,functions.maskingNumber(genericCardDetailsVOlog.getcVV()));
        authMaplog.put(ELE_CCEXP,functions.maskingExpiry(genericCardDetailsVOlog.getExpMonth()+genericCardDetailsVO.getExpYear().substring(2)));

        String cardParameters = FluzznetworkUtils.joinMapValue(authMap, '&');
        String cardParameterslog = FluzznetworkUtils.joinMapValue(authMaplog, '&');
        log.debug("Fluzz Auth Request----"+trackingID + "--" + cardParameterslog);
        transactionLogger.debug("Fluzz Auth Request----"+trackingID + "--" + cardParameterslog);

        String response = FluzznetworkUtils.doPostHTTPSURLConnection(Fn_URL,cardParameters);
        log.debug("Fluzz Auth Response----"+trackingID + "--" + response);
        transactionLogger.debug("Fluzz Auth Response----"+trackingID + "--" + response);
        Map<String,String> status = FluzznetworkUtils.getResponseString(response);
        if(status.get("response").equalsIgnoreCase("1"))
        {
            fluzznetworkresVO.setStatus("success");
            fluzznetworkresVO.setDescription(status.get("responsetext"));
        }
        else
        {
            fluzznetworkresVO.setStatus("fail");
            fluzznetworkresVO.setDescription(status.get("responsetext"));
        }
        fluzznetworkresVO.setErrorCode(status.get("response_code"));
        fluzznetworkresVO.setTransactionType(status.get("type"));

        return fluzznetworkresVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO fluzznetworkVO = (CommRequestVO) requestVO;
        CommResponseVO fluzznetworkresVO=new CommResponseVO();

        // validateForVoid(trackingID,requestVO);
        Map<String, String> authMap = new TreeMap<String, String>();
        authMap.put(ELE_TYPE,VOID);
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_ORDERID,trackingID);

        CommTransactionDetailsVO CtransVo = fluzznetworkVO.getTransDetailsVO();
        authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());

        String cardParameters = FluzznetworkUtils.joinMapValue(authMap, '&');
        log.debug("Fluzz Cancel Request----"+trackingID + "--" + cardParameters);
        transactionLogger.debug("Fluzz Cancel Request----"+trackingID + "--" + cardParameters);

        String response = FluzznetworkUtils.doPostHTTPSURLConnection(Fn_URL,cardParameters);
        log.debug("Fluzz cancel Response----"+trackingID + "--" + response);
        transactionLogger.debug("Fluzz cancel Response----"+trackingID + "--" + response);
        Map<String,String> status = FluzznetworkUtils.getResponseString(response);
        if(status!=null && !status.equals(""))
        {
            fluzznetworkresVO.setTransactionId(status.get("transactionid"));

            if(status.get("response").equalsIgnoreCase("1"))
            {

                fluzznetworkresVO.setStatus("success");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            else
            {
                fluzznetworkresVO.setStatus("fail");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            fluzznetworkresVO.setErrorCode(status.get("response_code"));
            fluzznetworkresVO.setTransactionType(status.get("type"));

        }
        else
        {
            fluzznetworkresVO.setStatus("fail");
            fluzznetworkresVO.setDescription(status.get("responsetext"));
        }

        return fluzznetworkresVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO fluzznetworkVO = (CommRequestVO) requestVO;
        CommResponseVO fluzznetworkresVO=new CommResponseVO();


        // validateForVoid(trackingID,requestVO);
        Map<String, String> authMap = new TreeMap<String, String>();
        authMap.put(ELE_TYPE,REFUND);
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_ORDERID,trackingID);

        CommTransactionDetailsVO CtransVo = fluzznetworkVO.getTransDetailsVO();

        authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());
        authMap.put(ELE_AMOUNT,CtransVo.getAmount());

        String cardParameters = FluzznetworkUtils.joinMapValue(authMap, '&');
        log.debug("Fluzz Refund Request----"+trackingID + "--" + authMap);
        log.debug("Fluzz Refund Request1----"+trackingID + "--" + cardParameters);
        transactionLogger.debug("Fluzz Refund Request1----"+cardParameters);

        String response = FluzznetworkUtils.doPostHTTPSURLConnection(Fn_URL,cardParameters);
        log.debug("Fluzz Refund Response----"+trackingID + "--" + response);
        transactionLogger.debug("Fluzz Refund Response----"+trackingID + "--" + response);
        Map<String,String> status = FluzznetworkUtils.getResponseString(response);
        if(status!=null && !status.equals(""))
        {
            fluzznetworkresVO.setTransactionId(status.get("transactionid"));

            if(status.get("response").equalsIgnoreCase("1"))
            {
                fluzznetworkresVO.setStatus("success");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            else
            {
                fluzznetworkresVO.setStatus("fail");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            fluzznetworkresVO.setErrorCode(status.get("response_code"));
            fluzznetworkresVO.setTransactionType(status.get("type"));

        }
        else
        {
            fluzznetworkresVO.setStatus("fail");
            fluzznetworkresVO.setDescription(status.get("responsetext"));
        }

        return fluzznetworkresVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO fluzznetworkVO = (CommRequestVO) requestVO;
        CommResponseVO fluzznetworkresVO=new CommResponseVO();

        // validateForVoid(trackingID,requestVO);
        Map<String, String> authMap = new TreeMap<String, String>();
        authMap.put(ELE_TYPE,CAPTURE);
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_ORDERID,trackingID);

        CommTransactionDetailsVO CtransVo = fluzznetworkVO.getTransDetailsVO();
        authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());
        authMap.put(ELE_AMOUNT,CtransVo.getAmount());

        String cardParameters = FluzznetworkUtils.joinMapValue(authMap, '&');
        log.debug("Fluzz Capture Request----"+trackingID + "--" + cardParameters);
        transactionLogger.debug("Fluzz Capture Request----"+trackingID + "--" + cardParameters);

        String response = FluzznetworkUtils.doPostHTTPSURLConnection(Fn_URL,cardParameters);
        log.debug("Fluzz Capture Response----"+trackingID + "--" + response);
        transactionLogger.debug("Fluzz Capture Response----"+trackingID + "--" + response);
        Map<String,String> status = FluzznetworkUtils.getResponseString(response);
        if(status!=null && !status.equals(""))
        {
            fluzznetworkresVO.setTransactionId(status.get("transactionid"));

            if(status.get("response").equalsIgnoreCase("1"))
            {
                fluzznetworkresVO.setStatus("success");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            else
            {
                fluzznetworkresVO.setStatus("fail");
                fluzznetworkresVO.setDescription(status.get("responsetext"));
            }
            fluzznetworkresVO.setErrorCode(status.get("response_code"));
            fluzznetworkresVO.setTransactionType(status.get("type"));

        }
        else
        {
            fluzznetworkresVO.setStatus("fail");
            fluzznetworkresVO.setDescription(status.get("responsetext"));
        }

        return fluzznetworkresVO;
    }


    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO fluzznetworkVO = (CommRequestVO) requestVO;
        CommResponseVO fluzznetworkresVO=new CommResponseVO();


        //validateForQuery(trackingID, requestVO);

        Map<String, String> authMap = new TreeMap<String, String>();

        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        CommTransactionDetailsVO CtransVo = fluzznetworkVO.getTransDetailsVO();
        authMap.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_TRANSACTIONID,CtransVo.getPreviousTransactionId());

        String cardParameters = FluzznetworkUtils.joinMapValue(authMap, '&');

        String res = FluzznetworkUtils.doPostHTTPSURLConnection(Fn_STATUS, cardParameters);

        fluzznetworkresVO =FluzznetworkUtils.getFluzznetworkResponseVOInquiry(res);

        return fluzznetworkresVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("FluzznetworkPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            log.info("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }

        if(requestVO ==null)
        {
            log.info("Request input not provided");
            throw new SystemError("Request input not provided");
        }

        CommRequestVO fluzznetworkRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = fluzznetworkRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            log.info("transDetails input not provided");
            throw new SystemError("transDetails input not provided");
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            log.info("amount not provided");
            throw new SystemError("amount not provided");
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            log.info("currency not provided");
            throw new SystemError("currency not provided");
        }

        GenericAddressDetailsVO genericAddressDetailsVO= fluzznetworkRequestVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            log.info("Address Details input not provided");
            throw new SystemError("Address Details input not provided");
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            log.info("First Name not provided");
            throw new SystemError("First Name not provided");

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            log.info("Last Name not provided");
            throw new SystemError("Last Name not provided");

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {

            log.info("Customer Email not provided");
            throw new SystemError("Customer Email not provided");
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            log.info("Customer IP not provided");
            throw new SystemError("Customer IP not provided");
        }

        //Address Details
        if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
        {
            log.info("Customer Address not provided");
            throw new SystemError("Customer Address not provided");
        }

        if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
        {
            log.info("City not provided");
            throw new SystemError("City not provided");
        }
        if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            log.info("Country not provided");
            throw new SystemError("Country not provided");
        }

        if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            log.info("State not provided");
            throw new SystemError("State not provided");
        }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            log.info("ZIP Code not provided");
            throw new SystemError("Zip Code not provided");
        }
        if(genericAddressDetailsVO.getPhone()==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            log.info("Phone number not provided");
            throw new SystemError("phone number not provided");
        }

        GenericCardDetailsVO genericCardDetailsVO= fluzznetworkRequestVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            log.info("Card Details input not provided");
            throw new SystemError("Card Details input not provided");
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {

            log.info("Card Number not provided");
            throw new SystemError("Card Number not provided");
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            log.info("Card number is invalid.");
            throw new SystemError("Card number is invalid.");
        }

        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {

            log.info("CVV not provided");
            throw new SystemError("CVV not provided");
        }
        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {

            log.info("Exp Month not provided");
            throw new SystemError("Exp Month not provided");
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {

            log.info("Exp Year not provided");
            throw new SystemError("Exp Year not provided");
        }


    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            log.info("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }

        if(requestVO ==null)
        {
            log.info("Request input not provided");
            throw new SystemError("Request input not provided");
        }

        CommRequestVO fluzznetworkRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = fluzznetworkRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            log.info("transDetails input not provided");
            throw new SystemError("transDetails input not provided");
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            log.info("amount not provided");
            throw new SystemError("amount not provided");
        }

        if(GatewayAccountService.getGatewayAccount(accountId).getPassword() == null || GatewayAccountService.getGatewayAccount(accountId).getPassword().equals(""))
        {
            log.info("Currency not provided");
            throw new SystemError("Currency not provided");
        }
    }
    private void validateForQuery(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            log.info("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }
        if(requestVO ==null)
        {
            log.info("Request input not provided");
            throw new SystemError("Request input not provided");
        }

        CommRequestVO fluzznetworkVO =  (CommRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = fluzznetworkVO.getTransDetailsVO();

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            log.info("TransactionID not provided");
            throw new SystemError("TransactionID not provided");
        }
    }



    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


