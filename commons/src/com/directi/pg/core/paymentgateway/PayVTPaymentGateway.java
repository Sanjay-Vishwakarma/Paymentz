package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.payvt.PayVTUtils;
import com.directi.pg.core.valueObjects.*;

import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;


import java.util.TreeMap;
import java.util.Map;



/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Aug 21, 2012
 * Time: 9:12:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVTPaymentGateway  extends AbstractPaymentGateway
{
    private static Logger log = new Logger(PayVTPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVTPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "payvt";

    //input fields
    private final static String FIELD_SITE_ID = "site_id";
    private final static String FIELD_PASSWORD = "password";
    private final static String FIELD_TYPE = "type";

    private final static String FIELD_AMT = "amount";
    private final static String FIELD_CURRENCYCODE = "currency";
    private final static String FIELD_IP = "ip";
    private final static String FIELD_TRACKING_ID = "merchant_reference";
    private final static String FIELD_PRODUCT_DESC = "product";

    private final static String FIELD_FIRST_NAME = "firstname";
    private final static String FIELD_LAST_NAME = "lastname";
    private final static String FIELD_ADDR = "address";
    private final static String FIELD_CITY = "city";
    private final static String FIELD_STATE = "state";
    private final static String FIELD_COUNTRY = "country";
    private final static String FIELD_ZIP = "zipcode";
    private final static String FIELD_PHONE = "phone";
    private final static String FIELD_EMAIL = "email";


    private final static String FIELD_CARD_HOLDER = "card_holder";
    private final static String FIELD_CARD_NUM = "card_number";
    private final static String FIELD_EXPIRY_MONTH = "expiry_month";
    private final static String FIELD_EXPIRY_YEAR = "expiry_year";
    private final static String FIELD_CVV = "cvv";


    //retun fields
    private final static String FIELD_RESULT = "result";
    private final static String FIELD_TRANID = "transaction_id";
    private final static String FIELD_RESPONSECODE = "code";
    private final static String FIELD_MESSAGE = "message";




    //Constants
    private final static String SALE = "1";
    private final static String AUTH = "2";
    private final static String CAPTURE = "3";
    private final static String VOID = "4";
    private final static String REFUND = "5";

    //Configuration
    private final static String SITE_ID = "2000";
    private final static String PASSWORD = "testSitePass";
    private final static String PAYVT_URL = "https://admin.payvt.com/api/cc.php";



    public String getMaxWaitDays()
    {
        return "3.5";
    }


    /**
     *
     * @param accountId
     * @throws SystemError
     */
    public PayVTPaymentGateway(String accountId)
    {
        this.accountId = accountId;

    }





    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
                 log.debug("Entering processSale");

                 GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

                 PayVTRequestVO payVTRequestVO = (PayVTRequestVO)requestVO;



                 //Preparing map for authentication request
                 Map<String, String> authMap = new TreeMap<String, String>();

                 //getCurrencyCode(account.getCurrency())  //refer ACIUniversalKitHandler
                 authMap.put(FIELD_SITE_ID, SITE_ID);
                 authMap.put(FIELD_PASSWORD, PASSWORD);
                 authMap.put(FIELD_TYPE,SALE);
                 if(trackingID ==null || trackingID.equals(""))
                {
                     PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
                }
                else
                 {
                 authMap.put(FIELD_TRACKING_ID, trackingID);
                 }
                  if(account.getCurrency() ==null || account.getCurrency().equals(""))
                {
                     PZExceptionHandler.raiseTechnicalViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Currency not configured while placing transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Currency not configured while placing transaction,accountId::"+accountId,new Throwable("Currency not configured while placing transaction,accountId::"+accountId));
                }
                 else
                  {
                 authMap.put(FIELD_CURRENCYCODE, account.getCurrency());
                  }
                 if(payVTRequestVO.getTransDetails()!=null)
                 {
                     if(payVTRequestVO.getTransDetails().getAmount()==null || payVTRequestVO.getTransDetails().getAmount().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_AMT,payVTRequestVO.getTransDetails().getAmount());
                     }
                     if(payVTRequestVO.getTransDetails().getOrderDesc()==null || payVTRequestVO.getTransDetails().getOrderDesc().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Order Description not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order Desription not provided while placing transaction",new Throwable("Order Description not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_PRODUCT_DESC, payVTRequestVO.getTransDetails().getOrderDesc());
                     }

                 }
                 else
                 {
                  PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
                 }

                 if (payVTRequestVO.getBillingAddr() != null)
                {
                  if(payVTRequestVO.getBillingAddr().getIp()==null || payVTRequestVO.getBillingAddr().getIp().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_IP,payVTRequestVO.getBillingAddr().getIp());
                     }
                   if(payVTRequestVO.getBillingAddr().getFirstname()==null || payVTRequestVO.getBillingAddr().getFirstname().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_FIRST_NAME,payVTRequestVO.getBillingAddr().getFirstname());
                     }
                    if(payVTRequestVO.getBillingAddr().getLastname()==null || payVTRequestVO.getBillingAddr().getLastname().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","LastName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"LastName not provided while placing transaction",new Throwable("LastName not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_LAST_NAME, payVTRequestVO.getBillingAddr().getLastname());
                     }
                    if(payVTRequestVO.getBillingAddr().getStreet()==null || payVTRequestVO.getBillingAddr().getStreet().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_ADDR, payVTRequestVO.getBillingAddr().getStreet());
                     }
                    if(payVTRequestVO.getBillingAddr().getCity()==null || payVTRequestVO.getBillingAddr().getCity().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_CITY,payVTRequestVO.getBillingAddr().getCity());
                     }
                    if(payVTRequestVO.getBillingAddr().getZipCode()==null || payVTRequestVO.getBillingAddr().getZipCode().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","ZIP Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"ZIP Code not provided while placing transaction",new Throwable("ZIP Code not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_ZIP,payVTRequestVO.getBillingAddr().getZipCode());
                     }
                    if(payVTRequestVO.getBillingAddr().getState()==null || payVTRequestVO.getBillingAddr().getState().equals("") )
                     {
                          PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"State not provided while placing transaction",new Throwable("State not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_STATE, payVTRequestVO.getBillingAddr().getState());
                     }
                    if(payVTRequestVO.getBillingAddr().getCountry()==null || payVTRequestVO.getBillingAddr().getCountry().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_COUNTRY, payVTRequestVO.getBillingAddr().getCountry());
                     }
                    if(payVTRequestVO.getBillingAddr().getPhone()==null || payVTRequestVO.getBillingAddr().getPhone().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_PHONE,payVTRequestVO.getBillingAddr().getPhone());
                     }
                    if(payVTRequestVO.getBillingAddr().getEmail()==null || payVTRequestVO.getBillingAddr().getEmail().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Email ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email ID not provided while placing transaction",new Throwable("Email ID not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_EMAIL,payVTRequestVO.getBillingAddr().getEmail());
                     }

                }
               else
               {
                   PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","AddressDetails   not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails not provided while placing transaction",new Throwable("AddressDetails not provided while placing transaction"));
               }

                String ccNum = null;
                  if (payVTRequestVO.getCardDetails() != null)
                {
                  if(payVTRequestVO.getCardDetails().getCardNum()==null || payVTRequestVO.getCardDetails().getCardNum().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
                     }
                     else
                     {
                         ccNum =  payVTRequestVO.getCardDetails().getCardNum();
                         authMap.put(FIELD_CARD_NUM, ccNum);
                     }
                    if(payVTRequestVO.getCardDetails().getCardHolderName()==null || payVTRequestVO.getCardDetails().getCardHolderName().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","CardHolderName not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card Holder Name not provided while placing transaction",new Throwable("CArd Holder Name not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_CARD_HOLDER,payVTRequestVO.getCardDetails().getCardHolderName());
                     }
                    if(payVTRequestVO.getCardDetails().getExpMonth()==null || payVTRequestVO.getCardDetails().getExpMonth().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_EXPIRY_MONTH,payVTRequestVO.getCardDetails().getExpMonth());
                     }
                    if(payVTRequestVO.getCardDetails().getExpYear()==null || payVTRequestVO.getCardDetails().getExpYear().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
                     }
                     else
                     {
                          authMap.put(FIELD_EXPIRY_YEAR, payVTRequestVO.getCardDetails().getExpYear());
                     }
                   if(payVTRequestVO.getCardDetails().getcVV()==null || payVTRequestVO.getCardDetails().getcVV().equals("") )
                     {
                           PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
                     }
                     else
                     {
                         authMap.put(FIELD_CVV, payVTRequestVO.getCardDetails().getcVV());
                     }

                  }
                else
                 {
                     PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CardDetails not provided while placing transaction",new Throwable("CardDetails not provided while placing transaction"));
                 }

                String transaction_id = null;
                String code = null;
                String result = null;
                String message = null;

                boolean success = false;
                 if (Functions.isValid(ccNum))
                {

                 PayVTUtils payVTUtils = new PayVTUtils();
                 String reqParameters = payVTUtils.joinMapValue(authMap, '&');
                 String response = payVTUtils.doPostHTTPSURLConnection(PAYVT_URL,reqParameters);
                 //String response = payVTUtils.doPostURLConnection(PAYVT_URL,reqParameters);
                 log.debug("Response from PayVT   ====  "+response);
                 //System.out.println("Response from PayVT ====  "+response);

                 String[] arr =  payVTUtils.getResArr(response);

                 for (int i = 0; i < arr.length; i++) {

                 log.debug(arr[i]);

                 String[] queryResultArr = arr[i].split("=");
                    // process  business logic
                    if (FIELD_RESULT.equals(queryResultArr[0])) {
                        result = queryResultArr[1];

                    }
                    if (FIELD_RESPONSECODE.equals(queryResultArr[0])) {

                         code = queryResultArr[1];
                    }
                     if (FIELD_MESSAGE.equals(queryResultArr[0])) {

                         message = queryResultArr[1];
                    }
                      if (FIELD_TRANID.equals(queryResultArr[0])) {

                         transaction_id = queryResultArr[1];
                    }

            }


                }
                else
                {
                log.info("Card number is invalid.");
                transaction_id = null;
                code = null;
                result = null;
                message = "Invalid Card number";


                }

                log.debug("AuthId=" + transaction_id);
                log.debug("AuthCode=" + result);
                log.debug("authReceiptNo =" + transaction_id);
                log.debug("authQsiResponseCode=" + code);
                log.debug("authQsiResponseDesc=" + message);

                PayVTResponseVO payVTResponseVO= new   PayVTResponseVO(result,transaction_id,code,message);

                log.info("Leaving processSale returning PayVTResponseVO " );
                return payVTResponseVO;
   }

   /**
    *
    * @param trackingID
    * @param requestVO
    * @return
    * @throws SystemError
    */
   public GenericResponseVO processAuthentication(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
   {
                log.debug("Entering processAuthentication");

                 GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

                 PayVTRequestVO payVTRequestVO = (PayVTRequestVO)requestVO;



                 //Preparing map for authentication request
                 Map<String, String> authMap = new TreeMap<String, String>();

                 //getCurrencyCode(account.getCurrency())  //refer ACIUniversalKitHandler
                 authMap.put(FIELD_SITE_ID, SITE_ID);
                 authMap.put(FIELD_PASSWORD, PASSWORD);
                 authMap.put(FIELD_TYPE,AUTH);
                 authMap.put(FIELD_TRACKING_ID, trackingID);
                 authMap.put(FIELD_CURRENCYCODE, account.getCurrency());
                 if(payVTRequestVO.getTransDetails()!=null)
                 {
                 authMap.put(FIELD_AMT,payVTRequestVO.getTransDetails().getAmount());
                 authMap.put(FIELD_PRODUCT_DESC, payVTRequestVO.getTransDetails().getOrderDesc());
                 }
                 else
                 {
                    PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processAuthentication()",null,"common","TransactionDetails  not provided while Authenticating transaction via PayVT",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while Authenticating transaction via PayVT",new Throwable("TransactionDetails  not provided while Authenticating transaction via PayVT"));
                 }

                 if (payVTRequestVO.getBillingAddr() != null)
                {
                 authMap.put(FIELD_FIRST_NAME,payVTRequestVO.getBillingAddr().getFirstname());
                 authMap.put(FIELD_LAST_NAME, payVTRequestVO.getBillingAddr().getLastname());
                 authMap.put(FIELD_ADDR, payVTRequestVO.getBillingAddr().getStreet());
                 authMap.put(FIELD_CITY,payVTRequestVO.getBillingAddr().getCity());
                 authMap.put(FIELD_ZIP,payVTRequestVO.getBillingAddr().getZipCode());
                 authMap.put(FIELD_STATE, payVTRequestVO.getBillingAddr().getState());
                 authMap.put(FIELD_COUNTRY, payVTRequestVO.getBillingAddr().getCountry());
                 authMap.put(FIELD_PHONE,payVTRequestVO.getBillingAddr().getPhone());
                 authMap.put(FIELD_EMAIL,payVTRequestVO.getBillingAddr().getEmail());

                }
               else
               {
                   PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processAuthentication()",null,"common","AddressDetails  not provided while Authenticating transaction via PayVT",PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while Authenticating transaction via PayVT",new Throwable("AddressDetails  not provided while Authenticating transaction via PayVT"));
               }

                String ccNum = null;
                  if (payVTRequestVO.getCardDetails() != null)
                {

                 ccNum =  payVTRequestVO.getCardDetails().getCardNum();
                 authMap.put(FIELD_CARD_NUM, ccNum);
                 authMap.put(FIELD_CARD_HOLDER,payVTRequestVO.getCardDetails().getCardHolderName());
                 authMap.put(FIELD_EXPIRY_MONTH,payVTRequestVO.getCardDetails().getExpMonth());
                 authMap.put(FIELD_EXPIRY_YEAR, payVTRequestVO.getCardDetails().getExpYear());
                 authMap.put(FIELD_CVV, payVTRequestVO.getCardDetails().getcVV());
                  }
                else
                 {
                   PZExceptionHandler.raiseConstraintViolationException(PayVTPaymentGateway.class.getName(),"processAuthentication()",null,"common","CArdDetails  not provided while Authenticating transaction via PayVT",PZConstraintExceptionEnum.VO_MISSING,null,"Card Details  not provided while Authenticating transaction via PayVT",new Throwable("CardDetails  not provided while Authenticating transaction via PayVT"));
                 }



                String transaction_id = null;
                String code = null;
                String result = null;
                String message = null;

                boolean success = false;
                 if (Functions.isValid(ccNum))
                {

                 PayVTUtils payVTUtils = new PayVTUtils();
                 String reqParameters = payVTUtils.joinMapValue(authMap, '&');
                 String response = payVTUtils.doPostHTTPSURLConnection(PAYVT_URL,reqParameters);
                 //String response = payVTUtils.doPostURLConnection(PAYVT_URL,reqParameters);
                 log.debug("Response from PayVT   ====  "+response);
                 //System.out.println("Response from PayVT ====  "+response);

                 String[] arr =  payVTUtils.getResArr(response);

                 for (int i = 0; i < arr.length; i++) {

                 log.debug(arr[i]);

                 String[] queryResultArr = arr[i].split("=");
                    // process  business logic
                    if (FIELD_RESULT.equals(queryResultArr[0])) {
                        result = queryResultArr[1];

                    }
                    if (FIELD_RESPONSECODE.equals(queryResultArr[0])) {

                         code = queryResultArr[1];
                    }
                     if (FIELD_MESSAGE.equals(queryResultArr[0])) {

                         message = queryResultArr[1];
                    }
                      if (FIELD_TRANID.equals(queryResultArr[0])) {

                         transaction_id = queryResultArr[1];
                    }



            }



                }
                else
                {
                log.info("Card number is invalid.");
                transaction_id = null;
                code = null;
                result = null;
                message = "Invalid Card number";


                }

                log.debug("AuthId=" + transaction_id);
                log.debug("AuthCode=" + result);
                log.debug("authReceiptNo =" + transaction_id);
                log.debug("authQsiResponseCode=" + code);
                log.debug("authQsiResponseDesc=" + message);

                PayVTResponseVO payVTResponseVO= new   PayVTResponseVO(result,transaction_id,code,message);

                log.info("Leaving processAuthentication returning PayVTResponseVO " );
                return payVTResponseVO;
   }


    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }







}
