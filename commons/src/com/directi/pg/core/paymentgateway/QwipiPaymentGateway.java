package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.qwipi.QwipiUtils;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.QwipiRequestVO;
import com.directi.pg.core.valueObjects.QwipiResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Nishant
 * Date: Aug 21, 2012
 * Time: 9:12:16 AM
 * To change this template use File | Settings | File Templates.
 */

public class QwipiPaymentGateway  extends AbstractPaymentGateway
{
   // private static Logger log = new Logger(QwipiPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(QwipiPaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.QwipiServlet");
    public static final String GATEWAY_TYPE = "Qwipi";
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
    private final static String FIELD_resType= "resType";

    //customer detail
    private final static String FIELD_FIRST_NAME = "firstName";
    private final static String FIELD_LAST_NAME = "lastName";
    private final static String FIELD_EMAIL = "email";
    private final static String FIELD_ZIP = "zipCode";
    private final static String FIELD_PHONE = "phone";
    private final static String FIELD_ADDR = "address";
    private final static String FIELD_CITY = "city";
    private final static String FIELD_STATE = "state";
    private final static String FIELD_COUNTRY = "country";
    private final static String FIELD_PRODUCTS = "products";
    private final static String FIELD_REMARK = "remark";
    private final static String FIELD_BIRTHDAY = "dob";
    private final static String FIELD_SSN = "ssn";
    //private final static String FIELD_MIDDLENAME="middleName";

    //return fields
    private final static String FIELD_OPERATION = "operation";
    private final static String FIELD_PAYMENT_ORDER_NO = "orderId";
    private final static String FIELD_MERNO = "merNo";
    private final static String FIELD_AMOUNT = "amount";
    private final static String FIELD_RF_RETURN_URL = "returnUrl";
    private final static String FIELD_RF_MD5INFO = "md5Info";
    private final static String FIELD_REFUND_AMOUNT = "amountRefund";

    //Constants
    private final static String SALE = "1";
    private final static String AUTH = "2";
    private final static String CAPTURE = "3";
    private final static String VOID = "4";
    private final static String REFUND = "5";

    //Configuration
    private final static String PAYQWIPI_REFUND_URL = "https://secure.qwipi.com/universalS2S/refund";
    private final static String PAYQWIPI_INQUIRY_ARS_URL = "https://secure.qwipi.com/qr/query.jsp";
    private final static String PAYQWIPI_URL = "https://secure.qwipi.com/universalS2S/payment";
    private final static String PAYQWIPI_REBILL_URL = "https://secure.qwipi.com/universalS2S/rebill";

    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public QwipiPaymentGateway(String accountId)
    {
        this.accountId = accountId;

    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processSale");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        QwipiRequestVO qwipiRequestVO = (QwipiRequestVO)requestVO;

        //Preparing map for authentication request
        Map<String, String> authMap = new TreeMap<String, String>();
        QwipiResponseVO qwipiResponseVO= null;
        QwipiUtils qwipiUtils = new QwipiUtils();
        String response=null;

        if(account.getCurrency() ==null || account.getCurrency().equals(""))
        {
            PZExceptionHandler.raiseTechnicalViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Currency not configured while placing transaction,AccountID:::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Currency not configured while placing transaction,AccountID:::"+accountId,new Throwable("Currency not configured while placing transaction,AccountID:::"+accountId));
        }
        else
        {
            authMap.put(FIELD_CURRENCYCODE, account.getCurrency());
        }

        if(qwipiRequestVO.getTransDetails()!=null)
        {
            if(qwipiRequestVO.getTransDetails().getAmount()==null || qwipiRequestVO.getTransDetails().getAmount().equals("") )
            {
               PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_AMT,qwipiRequestVO.getTransDetails().getAmount());
            }
            if(qwipiRequestVO.getTransDetails().getOrderDesc()==null || qwipiRequestVO.getTransDetails().getOrderDesc().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Order Description not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order Description not provided while placing transaction",new Throwable("Order Description not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_BILLNO, qwipiRequestVO.getTransDetails().getOrderDesc());
            }
            if(qwipiRequestVO.getTransDetails().getMerNo()==null || qwipiRequestVO.getTransDetails().getMerNo().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Merchant NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Merchant NO not provided while placing transaction",new Throwable("Merchant NO not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_MERNO, qwipiRequestVO.getTransDetails().getMerNo());
            }

        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }

        if (qwipiRequestVO.getBillingAddr() != null)
        {
            if(qwipiRequestVO.getBillingAddr().getBirthdate()==null || qwipiRequestVO.getBillingAddr().getBirthdate().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Birth Date not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Birth Date not provided while placing transaction",new Throwable("Birth Date not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_BIRTHDAY,qwipiRequestVO.getBillingAddr().getBirthdate());
            }
            if(qwipiRequestVO.getBillingAddr().getSsn()==null || qwipiRequestVO.getBillingAddr().getSsn().equals("") )
            {
                transactionLogger.info("ssn not provided");
                //throw new SystemError("ssn not provided");
            }
            else
            {
                authMap.put(FIELD_SSN,qwipiRequestVO.getBillingAddr().getSsn());
            }

            if(qwipiRequestVO.getBillingAddr().getFirstname()==null || qwipiRequestVO.getBillingAddr().getFirstname().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_FIRST_NAME,qwipiRequestVO.getBillingAddr().getFirstname());
            }

            if(qwipiRequestVO.getBillingAddr().getTime()==null || qwipiRequestVO.getBillingAddr().getTime().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Time not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Time not provided while placing transaction",new Throwable("Time not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_DATE_TIME,qwipiRequestVO.getBillingAddr().getTime());
            }
            if(qwipiRequestVO.getBillingAddr().getLastname()==null || qwipiRequestVO.getBillingAddr().getLastname().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Last Name not provided while placing transaction",new Throwable("Last Name not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_LAST_NAME, qwipiRequestVO.getBillingAddr().getLastname());
            }
            //GatewayAccount gatewayAccount = new GatewayAccount();
           // GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String addressValidation = account.getAddressValidation();
            if (addressValidation.equalsIgnoreCase("Y"))
            {


                if (qwipiRequestVO.getBillingAddr().getStreet() == null || qwipiRequestVO.getBillingAddr().getStreet().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processSale()", null, "common", "Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Street not provided while placing transaction", new Throwable("Street not provided while placing transaction"));
                }
                else
                {
                    authMap.put(FIELD_ADDR, qwipiRequestVO.getBillingAddr().getStreet());
                }
                if (qwipiRequestVO.getBillingAddr().getCity() == null || qwipiRequestVO.getBillingAddr().getCity().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processSale()", null, "common", "City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "City not provided while placing transaction", new Throwable("City not provided while placing transaction"));
                }
                else
                {
                    authMap.put(FIELD_CITY, qwipiRequestVO.getBillingAddr().getCity());
                }
                if (qwipiRequestVO.getBillingAddr().getZipCode() == null || qwipiRequestVO.getBillingAddr().getZipCode().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processSale()", null, "common", "Zip Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Zip Code not provided while placing transaction", new Throwable("Zip Code not provided while placing transaction"));
                }
                else
                {
                    authMap.put(FIELD_ZIP, qwipiRequestVO.getBillingAddr().getZipCode());
                }
                if (qwipiRequestVO.getBillingAddr().getState() == null || qwipiRequestVO.getBillingAddr().getState().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processSale()", null, "common", "State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "State not provided while placing transaction", new Throwable("State not provided while placing transaction"));
                }
                else
                {
                    authMap.put(FIELD_STATE, qwipiRequestVO.getBillingAddr().getState());
                }
                if (qwipiRequestVO.getBillingAddr().getCountry() == null || qwipiRequestVO.getBillingAddr().getCountry().equals(""))
                {
                    authMap.put(FIELD_COUNTRY, "INDIA");
                }
                else
                {
                    authMap.put(FIELD_COUNTRY, qwipiRequestVO.getBillingAddr().getCountry());
                }
                if (qwipiRequestVO.getBillingAddr().getPhone() == null || qwipiRequestVO.getBillingAddr().getPhone().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processSale()", null, "common", "Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Phone NO not provided while placing transaction", new Throwable("Phone NO not provided while placing transaction"));
                }
                else
                {
                    authMap.put(FIELD_PHONE, qwipiRequestVO.getBillingAddr().getPhone());
                }
            }
            if(qwipiRequestVO.getBillingAddr().getEmail()==null || qwipiRequestVO.getBillingAddr().getEmail().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email Id not provided while placing transaction",new Throwable("Email Id not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_EMAIL,qwipiRequestVO.getBillingAddr().getEmail());
            }
            if(qwipiRequestVO.getBillingAddr().getLanguage()==null || qwipiRequestVO.getBillingAddr().getLanguage().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Language not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Language not provided while placing transaction",new Throwable("Language not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_LANGUAGE,qwipiRequestVO.getBillingAddr().getLanguage());
            }
            if(qwipiRequestVO.getBillingAddr().getMd5info()==null || qwipiRequestVO.getBillingAddr().getMd5info().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","MD5 Info not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"MD5 Info not provided while placing transaction",new Throwable("MD5 Info not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_MD5INFO,qwipiRequestVO.getBillingAddr().getMd5info());
            }
            if(qwipiRequestVO.getBillingAddr().getIp()==null || qwipiRequestVO.getBillingAddr().getIp().equals("") )
            {
                transactionLogger.info("IP not provided");
                authMap.put(FIELD_IP,"192.127.0.0");
            }
            else
            {
                authMap.put(FIELD_IP,qwipiRequestVO.getBillingAddr().getIp());
            }

            authMap.put(FIELD_PRODUCTS,qwipiRequestVO.getBillingAddr().getProducts());

            authMap.put(FIELD_REMARK,qwipiRequestVO.getBillingAddr().getRemark());

        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Addressdetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Addressdetails  not provided while placing transaction",new Throwable("Addressdetails  not provided while placing transaction"));
        }

        String ccNum=null;
        if (qwipiRequestVO.getCardDetails() != null)
        {
            if(qwipiRequestVO.getCardDetails().getCardNum()==null || qwipiRequestVO.getCardDetails().getCardNum().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
            }
            else
            {
                ccNum =  qwipiRequestVO.getCardDetails().getCardNum();
                authMap.put(FIELD_CARD_NUMBER, ccNum);
            }

            if(qwipiRequestVO.getCardDetails().getExpMonth()==null || qwipiRequestVO.getCardDetails().getExpMonth().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_EXPIRY_MONTH,qwipiRequestVO.getCardDetails().getExpMonth());
            }
            if(qwipiRequestVO.getCardDetails().getExpYear()==null || qwipiRequestVO.getCardDetails().getExpYear().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_EXPIRY_YEAR, qwipiRequestVO.getCardDetails().getExpYear());
            }
            if(qwipiRequestVO.getCardDetails().getcVV()==null || qwipiRequestVO.getCardDetails().getcVV().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
            }
            else
            {
                authMap.put(FIELD_CVV, qwipiRequestVO.getCardDetails().getcVV());
            }

        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
            authMap.put(FIELD_resType,"XML");
            String reqParameters = qwipiUtils.joinMapValue(authMap, '&');
        //todo gatewayCall
        //Date date104=new Date();
        //transactionLogger.debug("QwipiPaymentGateway gatewayCall start time 104########"+date104.getTime());
        transactionLogger.error("-------sale request------"+reqParameters);
        response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_URL,reqParameters);
        //transactionLogger.debug("QwipiPaymentGateway gatewayCall end time 104########" + new Date().getTime());
        //transactionLogger.debug("QwipiPaymentGateway gatewayCall diff time 104########"+(new Date().getTime()-date104.getTime()));

        transactionLogger.error("-------sale response------"+response);
        qwipiResponseVO=qwipiUtils.getQWIPIResponseVOI(response);
        //transactionLogger.info("Leaving processSale returning QwipiResponseVO " );
        return qwipiResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processInquiry");
        QwipiRequestVO qwipiRequestVO = (QwipiRequestVO)requestVO;
        //Preparing map for INQUIRY request

        Map<String, String> authMap = new TreeMap<String, String>();
        QwipiResponseVO qwipiResponseVO= new QwipiResponseVO();

        if(qwipiRequestVO.getTransDetails()!=null)
        {
            if(qwipiRequestVO.getTransDetails().getOrderDesc()==null || qwipiRequestVO.getTransDetails().getOrderDesc().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processInquiry()",null,"common","Order Description not provided while inquirying transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order Description not provided while inquirying transaction",new Throwable("Order Description not provided while inquirying transaction"));
            }
            else
            {
                authMap.put(FIELD_BILLNO, qwipiRequestVO.getTransDetails().getOrderDesc());
            }
            if(qwipiRequestVO.getTransDetails().getMerNo()==null || qwipiRequestVO.getTransDetails().getMerNo().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processInquiry()",null,"common","Merchant NO not provided while inquirying transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Merchant NO not provided while inquirying transaction",new Throwable("Merchant NO not provided while inquirying transaction"));
            }
            else
            {
                authMap.put(FIELD_MERNO, qwipiRequestVO.getTransDetails().getMerNo());
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processInquiry()",null,"common","TransactionDetails  not provided while inquirying transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while inquirying transaction",new Throwable("TransactionDetails  not provided while inquirying transaction"));
        }
        if (qwipiRequestVO.getBillingAddr() != null)
        {
            if(qwipiRequestVO.getBillingAddr().getMd5info()==null || qwipiRequestVO.getBillingAddr().getMd5info().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processInquiry()",null,"common","TraMD Info not provided while inquirying transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"TraMD Info not provided while inquirying transaction",new Throwable("TraMD Info not provided while inquirying transaction"));
            }
            else
            {
                authMap.put(FIELD_MD5INFO,qwipiRequestVO.getBillingAddr().getMd5info());
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processInquiry()",null,"common","AddressDetails  not provided while inquirying transaction",PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while inquirying transaction",new Throwable("AddressDetails  not provided while inquirying transaction"));
        }
        //calling qwipi inquiry

        QwipiUtils qwipiUtils = new QwipiUtils();
        String reqParameters = qwipiUtils.joinMapValue(authMap, '&');
        String response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_INQUIRY_ARS_URL,reqParameters);

        transactionLogger.debug("qwipi inquiry request---"+reqParameters);
        transactionLogger.debug("qwipi inquiry response---"+response);
        qwipiResponseVO = qwipiUtils.getQWIPIResponseVOInquiry(response);

        return qwipiResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processRefund");
        QwipiRequestVO qwipiRequestVO = (QwipiRequestVO)requestVO;

        //Preparing map for INQUIRY request
        Map<String, String> authMap = new TreeMap<String, String>();
        QwipiResponseVO qwipiResponseVO= null;
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","Tracking Id not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while refunding transaction",new Throwable("Tracking Id not provided while refunding transaction"));
        }
        if(qwipiRequestVO.getTransDetails()!=null)
        {
            if(qwipiRequestVO.getTransDetails().getPaymentOrderNo()==null || qwipiRequestVO.getTransDetails().getPaymentOrderNo().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","PauymentOrder NO not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"PauymentOrder NO not provided while refunding transaction",new Throwable("PauymentOrder NO not provided while refunding transaction"));
            }
            else
            {
                authMap.put(FIELD_PAYMENT_ORDER_NO, qwipiRequestVO.getTransDetails().getPaymentOrderNo());
            }
            if(qwipiRequestVO.getTransDetails().getBillNo()==null || qwipiRequestVO.getTransDetails().getBillNo().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","Bill NO not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Bill NO not provided while refunding transaction",new Throwable("Bill NO not provided while refunding transaction"));
            }
            else
            {
                authMap.put(FIELD_BILLNO, qwipiRequestVO.getTransDetails().getBillNo());
            }
            if(qwipiRequestVO.getTransDetails().getAmount()==null || qwipiRequestVO.getTransDetails().getAmount().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","Amount not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while refunding transaction",new Throwable("Amount not provided while refunding transaction"));
            }
            else
            {
                authMap.put(FIELD_AMOUNT, qwipiRequestVO.getTransDetails().getAmount());
            }
            if(qwipiRequestVO.getTransDetails().getRefundAmount()==null || qwipiRequestVO.getTransDetails().getRefundAmount().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","Refund Amount not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Refund Amount not provided while refunding transaction",new Throwable("Refund Amount not provided while refunding transaction"));
            }
            else
            {
                authMap.put(FIELD_REFUND_AMOUNT, qwipiRequestVO.getTransDetails().getRefundAmount());
            }

        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","TransactionDetails  not provided while refunding transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while refunding transaction",new Throwable("TransactionDetails  not provided while refunding transaction"));
        }

        if(qwipiRequestVO.getBillingAddr()!=null)
        {
            if(qwipiRequestVO.getBillingAddr().getMd5info()==null || qwipiRequestVO.getBillingAddr().getMd5info().equals("") )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","MD Info not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"MD Info not provided while refunding transaction",new Throwable("MD Info not provided while refunding transaction"));
            }
            else
            {
                authMap.put(FIELD_RF_MD5INFO, qwipiRequestVO.getBillingAddr().getMd5info());
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRefund()",null,"common","AddressDetails  not provided while refunding transaction",PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while refunding transaction",new Throwable("AddressDetails  not provided while refunding transaction"));
        }
        //calling qwipi inquiry
        authMap.put(FIELD_OPERATION, "02");
        authMap.put(FIELD_resType,"XML");
        authMap.put(FIELD_RF_RETURN_URL, RB.getString("REFUND_RETURN_URL"));
        transactionLogger.debug("refund request"+authMap);
        QwipiUtils qwipiUtils = new QwipiUtils();

            String response =null;

            String reqParameters = qwipiUtils.joinMapValue(authMap, '&');
            transactionLogger.error("------refund request------" + reqParameters);
            response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_REFUND_URL,reqParameters);
            transactionLogger.error("-------- refund response-------"+response);
            qwipiResponseVO = qwipiUtils.getQWIPIResponseVOI(response);
            if(qwipiResponseVO!=null)
            {
                if(qwipiResponseVO.getResultCode()!=null && !qwipiResponseVO.getResultCode().equals(""))
                {
                    if(qwipiResponseVO.getResultCode().trim().equals("0") || qwipiResponseVO.getResultCode().trim().equals("O"))
                    {
                        transactionLogger.error("Refund ResultCode= "+qwipiResponseVO.getResultCode());
                        qwipiResponseVO.setResultCode("0");
                    }
                }
            }
        return qwipiResponseVO;
    }
    public GenericResponseVO processRebilling(String trackingid,GenericRequestVO requestVO) throws PZTechnicalViolationException,PZConstraintViolationException
    {
        Functions functions = new Functions();
        transactionLogger.debug("Entering processRebill");
        QwipiRequestVO qwipiRequestVO = (QwipiRequestVO)requestVO;

        Map<String, String> authMap = new TreeMap<String, String>();
        QwipiResponseVO qwipiResponseVO= null;

        if(qwipiRequestVO.getTransDetails()!=null)
        {
            if (!functions.isValueNull(qwipiRequestVO.getTransDetails().getOrderId()) )
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processRebilling()", null, "common", "OrderId not provided while rebilling transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "OrderId not provided while rebilling transaction", new Throwable("OrderId not provided while rebilling transaction"));
            }
            else
            {
                authMap.put(FIELD_PAYMENT_ORDER_NO, qwipiRequestVO.getTransDetails().getOrderId());//QwipiPaymentOrderNo
            }
            if (!functions.isValueNull(qwipiRequestVO.getTransDetails().getBillNo()))
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processRebilling()", null, "common", "BillNo not provided while rebilling transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "BillNo is not provided while rebilling transaction", new Throwable("BillNo not provided while rebilling transaction"));
            }
            else
            {
                authMap.put(FIELD_BILLNO, qwipiRequestVO.getTransDetails().getBillNo());//Unique OrderId
            }
            if (!functions.isValueNull(qwipiRequestVO.getTransDetails().getAmount()))
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(), "processRebilling()", null, "common", "Amount not provided while rebilling transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while rebilling transaction", new Throwable("Amount not provided while rebilling transaction"));
            }
            else
            {
                authMap.put(FIELD_AMOUNT, qwipiRequestVO.getTransDetails().getAmount());
            }
        }
        else
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRebilling()",null,"common","TransactionDetails not provided while rebilling transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails not provided while rebilling transaction",new Throwable("TransactionDetails not provided while rebilling transaction"));
            }
        if(qwipiRequestVO.getBillingAddr()!=null)
        {
            if(!functions.isValueNull(qwipiRequestVO.getBillingAddr().getMd5info()))
            {
                PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processRebilling()",null,"common","MD Info not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"MD Info not provided while refunding transaction",new Throwable("MD Info not provided while refunding transaction"));
            }
            else
            {
                authMap.put(FIELD_RF_MD5INFO, qwipiRequestVO.getBillingAddr().getMd5info());
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QwipiPaymentGateway.class.getName(),"processReBilling()",null,"common","AddressDetails  not provided while refunding transaction",PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while refunding transaction",new Throwable("AddressDetails  not provided while refunding transaction"));
        }

        //authMap.put(FIELD_OPERATION, "02");
        authMap.put(FIELD_resType,"XML");

        transactionLogger.error("request map"+authMap);

        QwipiUtils qwipiUtils = new QwipiUtils();

        String response =null;

        String reqParameters = qwipiUtils.joinMapValue(authMap, '&');

        transactionLogger.error("------rebill request-------"+reqParameters);
        response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_REBILL_URL,reqParameters);
        transactionLogger.error("-------rebill response--------------"+response);

        qwipiResponseVO = qwipiUtils.getQWIPIResponseVOI(response);

        if(qwipiResponseVO!=null)
        {
            if(qwipiResponseVO.getResultCode()!=null && !qwipiResponseVO.getResultCode().equals(""))
            {
                if(qwipiResponseVO.getResultCode().trim().equals("0") || qwipiResponseVO.getResultCode().trim().equals("0"))
                {
                    transactionLogger.error("Refund ResultCode= "+qwipiResponseVO.getResultCode());
                    qwipiResponseVO.setResultCode("0");
                }
            }
        }
        return qwipiResponseVO;
    }
}