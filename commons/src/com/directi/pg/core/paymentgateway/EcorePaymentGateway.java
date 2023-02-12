package com.directi.pg.core.paymentgateway;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.ecore.EcoreUtils;
import com.directi.pg.core.ecore.core.message.Response;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.EcoreRequestVO;
import com.directi.pg.core.valueObjects.EcoreResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.thoughtworks.xstream.XStream;
import org.apache.poi.util.SystemOutLogger;

import java.net.Inet4Address;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by IntelliJ IDEA.
 * User: Nishant
 * Date: Aug 21, 2012
 * Time: 9:12:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class EcorePaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(EcorePaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EcorePaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "Ecore";
    private final static String FIELD_TYPE = "type";
    //input fields

    private final static String FIELD_MD5KEY = "md5Key";
    //card details
    private final static String FIELD_CARD_NUMBER = "cardNum";
    private final static String FIELD_EXPIRY_MONTH = "month";
    private final static String FIELD_EXPIRY_YEAR = "year";
    private final static String FIELD_CVV = "cvv2";
    private final static String FIELD_IP = "cardHolderIp";
    private final static String FIELD_DATE_TIME = "dateTime";
    private final static String FIELD_BILLNO = "billNo";
    private final static String FIELD_AMT = "amount";
    private final static String FIELD_CURRENCYCODE = "currency";
    private final static String FIELD_LANGUAGE = "language";
    private final static String FIELD_MD5INFO = "md5Info";

    //customer detail
    private final static String FIELD_FIRST_NAME = "firstname";
    private final static String FIELD_LAST_NAME = "lastname";
    private final static String FIELD_EMAIL = "email";
    private final static String FIELD_ZIP = "zipCode";
    private final static String FIELD_PHONE = "phone";
    private final static String FIELD_ADDR = "address";
    private final static String FIELD_CITY = "city";
    private final static String FIELD_STATE = "state";
    private final static String FIELD_COUNTRY = "country";
    private final static String FIELD_PRODUCTS = "products";
    private final static String FIELD_REMARK = "remark";
    private final static String FIELD_BIRTHDAY = "birthDate";
    private final static String FIELD_SSN = "ssn";
    //retun fields

    private final static String FIELD_OPERATION = "operation";
    private final static String FIELD_PAYMENT_ORDER_NO = "paymentOrderNo";
    private final static String FIELD_RESULTCODE = "resultCode";
    private final static String FIELD_MERNO = "merNo";
    private final static String FIELD_AMOUNT = "amount";



    private final static String FIELD_REFUND_AMOUNT = "refundAmount";

    //Constants
    private final static String SALE = "1";
    private final static String AUTH = "2";
    private final static String CAPTURE = "3";
    private final static String VOID = "4";
    private final static String REFUND = "5";
    //Configuration
    private final static String PAYECORE_REFUND_URL = "https://gateway.ecorepay.cc/";
    private final static String PAYECORE_INQUIRY_URL = "https://gateway.ecorepay.cc/";
    private final static String PAYECORE_URL = "https://gateway.ecorepay.cc/";

    public String getMaxWaitDays()
    {
        return "3.5";
    }


    /**
     *
     * @param accountId
     * @throws com.directi.pg.SystemError
     */
    public EcorePaymentGateway(String accountId)
    {
        this.accountId = accountId;

    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        return super.processRefund(trackingID, requestVO);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processAuthentication");
        transactionLogger.debug("Entering processAuthentication");
        EcoreResponseVO ecoreResponse =null;
        String request = EcoreUtils.createTransactionRequestXML((EcoreRequestVO)requestVO, "Authorize");

            String response = EcoreUtils.doPostHTTPSURLConnection(PAYECORE_URL,request);
            ecoreResponse = EcoreUtils.getEcoreResponseVO(response);


        log.info("Leaving processAuthentication returning EcoreResponseVO " );
        transactionLogger.info("Leaving processAuthentication returning EcoreResponseVO " );
        return ecoreResponse;
    }
    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws com.directi.pg.SystemError
     */
        public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
        {
             log.debug("Entering processSale");
             transactionLogger.debug("Entering processSale");
             EcoreResponseVO ecoreResponse =null;
             String request = EcoreUtils.createTransactionRequestXML((EcoreRequestVO)requestVO, "AuthorizeCapture");
             transactionLogger.debug("request ecore-----"+request);

                String response = EcoreUtils.doPostHTTPSURLConnection(PAYECORE_URL,request);
            transactionLogger.debug("response ecore-----"+response);
                ecoreResponse = EcoreUtils.getEcoreResponseVO(response);


             /*catch(Exception e)
             {
                 log.info("There was an Error while posting data to bank. Please contact your merchant"+e);
                 throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
             }*/

            log.info("Leaving processSale returning EcoreResponseVO " );
            transactionLogger.info("Leaving processSale returning EcoreResponseVO " );
            return ecoreResponse;
        }

    public GenericResponseVO processCapture(String accountId, String accountAuth, String transactionId) throws PZTechnicalViolationException
    {
        log.debug("Entering processCapture");
        transactionLogger.debug("Entering processCapture");
        EcoreResponseVO ecoreResponse =null;
        String request = EcoreUtils.createTransUpdateRequestXML("Capture", accountId, accountAuth, transactionId, null);
        transactionLogger.debug("request ecore-----"+request);
            String response = EcoreUtils.doPostHTTPSURLConnection(PAYECORE_URL,request);
        transactionLogger.debug("response ecore-----"+response);
            ecoreResponse = EcoreUtils.getEcoreResponseVO(response);


        log.info("Leaving processCapture returning EcoreResponseVO " );
        transactionLogger.info("Leaving processCapture returning EcoreResponseVO " );
        return ecoreResponse;
    }

    public GenericResponseVO processInquiry(String accountId, String accountAuth, String transactionId, String referenceId) throws PZTechnicalViolationException
    {
        log.debug("Entering processInquiry");
        transactionLogger.debug("Entering processInquiry");
        EcoreResponseVO ecoreResponse =null;
        String request = EcoreUtils.createTransUpdateRequestXML("Lookup", accountId, accountAuth, transactionId, referenceId);
        transactionLogger.debug("request ecore-----"+request);
            String response = EcoreUtils.doPostHTTPSURLConnection(PAYECORE_URL,request);
        transactionLogger.debug("response ecore-----"+response);
            ecoreResponse = EcoreUtils.getEcoreResponseVO(response);


        log.info("Leaving processCapture returning EcoreResponseVO " );
        transactionLogger.info("Leaving processCapture returning EcoreResponseVO " );
        return ecoreResponse;
    }

    public GenericResponseVO processVoid(String accountId, String accountAuth, String transactionId) throws PZTechnicalViolationException
    {
        log.debug("Entering processVoid");
        transactionLogger.debug("Entering processVoid");
        EcoreResponseVO ecoreResponse =null;
        String request = EcoreUtils.createTransUpdateRequestXML("Void", accountId, accountAuth, transactionId, null);
        transactionLogger.debug("request ecore-----"+request);
            String response = EcoreUtils.doPostHTTPSURLConnection(PAYECORE_URL,request);
        transactionLogger.debug("response ecore-----"+response);
            ecoreResponse = EcoreUtils.getEcoreResponseVO(response);

        log.info("Leaving processCapture returning EcoreResponseVO " );
        transactionLogger.info("Leaving processCapture returning EcoreResponseVO " );
        return ecoreResponse;
    }

    public GenericResponseVO processRefund(String accountId, String accountAuth, String transactionId) throws PZTechnicalViolationException
    {
        log.debug("Entering processRefund");
        transactionLogger.debug("Entering processRefund");
        EcoreResponseVO ecoreResponse =null;
        String request = EcoreUtils.createTransUpdateRequestXML("Refund", accountId, accountAuth, transactionId, null);
        transactionLogger.debug("request ecore-----"+request);

            String response = EcoreUtils.doPostHTTPSURLConnection(PAYECORE_URL,request);
        transactionLogger.debug("response ecore-----"+response);
            ecoreResponse = EcoreUtils.getEcoreResponseVO(response);

        log.info("Leaving processCapture returning EcoreResponseVO " );
        transactionLogger.info("Leaving processCapture returning EcoreResponseVO " );
        return ecoreResponse;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("EcorePaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    private Response parseResponse(String response)
    {
        XStream xStream = new XStream();
        xStream.autodetectAnnotations(true);
        xStream.alias("response",Response.class);
        Response response1 = (Response) xStream.fromXML(response);

      return response1;
    }


}
