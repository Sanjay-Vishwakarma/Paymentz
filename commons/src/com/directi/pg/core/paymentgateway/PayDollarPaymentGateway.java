package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.payDollar.Elements;
import com.directi.pg.core.valueObjects.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.payDollar.PayDollarUtils;
import com.directi.pg.core.payDollar.PayDollarAccount;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import org.w3c.dom.Document;

import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.Hashtable;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 6, 2012
 * Time: 9:32:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarPaymentGateway extends AbstractPaymentGateway
{

    private static Logger log = new Logger(PayDollarPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayDollarPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "paydollar";
    private static Hashtable<String, PayDollarAccount> payDollarAccounts;


    // Auth Request fields
    private final static String AUTH_TRACKING_ID = "orderRef";             //Merchants Order Reference Number   (Trackin ID in our case)
    private final static String AUTH_AMOUNT = "amount";                    //[ Up to 2 decimal place ]
    private final static String AUTH_CURR_CODE = "currCode";
    private final static String AUTH_LANG = "lang";                        //(C,E,X, K, J)
    private final static String AUTH_MERCHANT_ID = "merchantId";           // Provided by PayDollar
    private final static String AUTH_P_METHOD = "pMethod";                 //The payment card type (VISA,Master,Diners,JCB,  AMEX)
    private final static String AUTH_EX_MONTH = "epMonth";                 //Credit card expiry month    Number(2)
    private final static String AUTH_EX_YEAR = "epYear";                   //Credit card expiry year      Number(4)
    private final static String AUTH_CARD_NO = "cardNo";
    private final static String AUTH_CARD_HOLDER = "cardHolder";
    private final static String AUTH_SECURITY_CODE = "securityCode";       //VISA: CVV2 (3-digit), MasterCard: CVC2 (3-digit), American Express: 4DBC (4-digit)
    private final static String AUTH_TYPE = "payType";                     //N  Normal Payment (Sales) H  Hold Payment (Authorize only)
    private final static String AUTH_REMARKS = "remark";                   // Optional , Order Description
    private final static String AUTH_SECURE_HASH = "secureHash";           // To be calculated here
    private final static String AUTH_FAIL_URL = "failUrl";                 // to redirect upon the transaction being rejected
    private final static String AUTH_SUCCESS_URL = "successUrl";           // to redirect upon the transaction being accepted
    private final static String AUTH_ERROR_URL = "errorUrl";               // to redirect when unexpected error occur
    private final static String CTOKEN = "ctoken";
    // Auth Request Fields for 3D Transaction
    private final static String AUTH_VBV_TRANS = "vbvTransaction";            // T  True: The transaction has been gone through the VE/PA process. F  False: Non-3D transaction without go through the VE/PA process
    private final static String AUTH_VBV_ECI = "vbvTransECI";                 //Mandatory field  For Details pls refer API doc
    // if  vbvTransaction = T
    private final static String AUTH_VBV_CH_RETURN_CODE = "vbvCHReturnCode";  // 0 or 1001 or -1
    private final static String AUTH_VBV_PA_RETURN_CODE = "vbvPAReturnCode";  // 0 or 1000 or 1003 or -1
    private final static String AUTH_VBV_TRANS_TIME = "vbvTransTime";         // (YYYYMMDD HH:MM:SS)
    private final static String AUTH_VBV_TRANS_AUTH = "vbvTransAuth";         // Cardholder Authentication Verification Value, CAVV, value in MPI PA Response Message [ Must be in Base64-Encoded format ]
    private final static String AUTH_VBV_CAVV_ALGO = "vbvCAVVAlgo";           // CAVV Algorithm, cavvAlgorithm, in MPI PA Response Message
    private final static String AUTH_VBV_XID = "vbvXID";                      // Transaction Identifier, xid, in MPI PA Response Message [ Must contain 20 characters ]
    private final static String AUTH_VBV_MERCHANT_ID = "vbvMerchantID";       // Acquirer-defined Merchant Identifier, merID, in MPI PA Response Message
    private final static String AUTH_VBV_AQUIRER_BIN = "vbvAcquirerBin";      // Acquirer BIN, acqBIN, in MPI PA Response Message
    private final static String AUTH_VBV_TRANS_STATUS = "vbvTransStatus";     // Transaction Status, status, in MPI PA Response Message - Set it to the value of status obtained from MPI PA Response Message- Set it to U if the status value is not available in the MPI PA Response Message



    // Response Fields
    private final static String FIELD_SRC = "src";                             // Return bank host status code
    private final static String FIELD_PRC = "prc";                             // Return bank host status code
    private final static String FIELD_ORD = "Ord";                             // Bank Reference  Order id
    private final static String FIELD_HOLDER = "Holder";                       // The Holder Name of the Payment Account
    private final static String FIELD_SUCCESS_CODE = "successcode";            // Transaction Status: -1 - Error 0 - Transaction succeeded 1 - Transaction Failure
    private final static String FIELD_REF = "Ref";                             // Merchants Order Reference Number
    private final static String FIELD_PAY_REF = "PayRef";                      // Payment Reference Number
    private final static String FIELD_AMT = "Amt";                             // Transaction Amount Number (15,5)
    private final static String FIELD_CUR = "Cur";                             // Transaction Currency i.e. 344 mean HKD   Number (3)
    private final static String FIELD_AUTH_ID = "AuthId";                      // Approval Code
    private final static String FIELD_TX_TIME = "TxTime";                      // Transaction Time (YYYY-MM-DD HH:MI:SS.0)
    private final static String FIELD_ERR_MSG = "errMsg";                      // Error Message

    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PayDrServlet");
    final static String PROXYSCHEME=RB.getString("PROXYSCHEME");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");
    final static String PAYDRFAILURL = RB.getString("PAYDRFAILURL");
    final static String PAYDRSUCCESSURL = RB.getString("PAYDRSUCCESSURL");
    final static String PAYDRERROR = RB.getString("PAYDRERROR");

    //Request Fields for API Calls
    private final static String FIELD_MERCHANT_ID = "merchantId";              //Merchant Id provided by PayDollar
    private final static String FIELD_LOGIN_ID = "loginId";                    // The loginId of merchant API
    private final static String FIELD_PASSWORD = "password";                   // The password of merchant API
    private final static String FIELD_ACTION_TYPE = "actionType";              // The action type
    private final static String FIELD_pay_Ref = "payRef";                      // Payment Reference Number
    private final static String FIELD_AMOUNT = "amount";                       // The amount to capture (must be less than or equal to the original amount)


    private final static String FIELD_RESULT_CODE = "resultCode";              // 0 - Request Successfully -1  Request Failed
    private final static String FIELD_ORDER_STATUS = "orderStatus";            // The new order status after successfully request
    private final static String FIELD_ref = "ref";                             // Merchants Order Reference Number
    private final static String FIELD_amt = "amt";                             // Transaction Amount Number (15,5)
    private final static String FIELD_cur = "cur";                             // Transaction Currency i.e. 344 mean HKD   Number (3)

   // Configuration
    private final static String LANG_C = "C";
    private final static String LANG_E = "E";
    private final static String LANG_X = "X";
    private final static String LANG_K = "K";
    private final static String LANG_J = "J";

    //private final static String TESTING_AUTH_URL = "https://test.paydollar.com/b2cDemo/eng/dPayment/payComp.jsp";
    //private final static String PRODUCTION_AUTH_URL = "https://www.paydollar.com/b2c2/eng/dPayment/payComp.jsp";

    //private final static String TESTING_API_URL = "https://test.paydollar.com/b2cDemo/eng/merchant/api/orderApi.jsp";
    //private final static String PRODUCTION_API_URL = "https://www.paydollar.com/b2c2/eng/merchant/api/orderApi.jsp";

    /*private final static String FAIL_URL = PROXYSCHEME+"://"+PROXYHOST+":"+PROXYPORT+"/icici/servlet/PayDRPostFail";
    private final static String SUCCESS_URL = PROXYSCHEME+"://"+PROXYHOST+":"+PROXYPORT+"/icici/servlet/PayDRPostSuccess";
    private final static String ERROR_URL = PROXYSCHEME+"://"+PROXYHOST+":"+PROXYPORT+"/icici/servlet/PayDRPostError";*/

    private final static String FAIL_URL = PROXYSCHEME+"://"+PROXYHOST+":"+PROXYPORT+PAYDRFAILURL;
    private final static String SUCCESS_URL = PROXYSCHEME+"://"+PROXYHOST+":"+PROXYPORT+PAYDRSUCCESSURL;
    private final static String ERROR_URL = PROXYSCHEME+"://"+PROXYHOST+":"+PROXYPORT+PAYDRERROR;
    //private final static String MERCHANT_ID = "102660";
    //private final static String LOGIN_ID="apitri";
    //private final static String PASSWORD="trip3894";


    //Constants
    private final static String CARD_TYPE_VISA = "VISA";
    private final static String CARD_TYPE_Master = "Master";
    private final static String CARD_TYPE_Diners = "Diners";
    private final static String CARD_TYPE_JCB = "JCB";
    private final static String CARD_TYPE_AMEX = "AMEX";
    private final static String PAYMENT_TYPE_SALES = "N";
    private final static String PAYMENT_TYPE_AUTH = "H";
    private final static String ACTION_TYPE_CAPTURE = "Capture";
    private final static String ACTION_TYPE_VOID = "RequestRefund";
    private final static String ACTION_TYPE_QUERY = "Query";
    private final static String ACTION_TYPE_REFUND = "Void";

    
    private final static String VBV_TRANS_TRUE = "T";
    private final static String VBV_TRANS_FALSE = "F";

     static
    {
        try
        {
            loadPayDollarAccounts();

        }
        catch (PZDBViolationException e)
        {
            log.error("Exception while loading PayDollar gateway account details",e);
            transactionLogger.error("Exception while loading PayDollar gateway account details",e);

            PZExceptionHandler.handleDBCVEException(e,"Pay Dollar", PZOperations.PAYDOLAR_LAOD_ACCOUNT);
        }

    }


    public static void loadPayDollarAccounts() throws PZDBViolationException
    {
        log.info("Loading PayDollar Accounts......");
        payDollarAccounts = new Hashtable<String, PayDollarAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_paydollar", conn);
            while (rs.next())
            {
                PayDollarAccount account = new PayDollarAccount(rs);
                payDollarAccounts.put(account.getAccountId() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PayDollarPaymentGateway.class.getName(),"loadPayDollarAccounts()",null,"common","Sql Exception while loading specific gateway account from gateway_accounts_paydollar", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(PayDollarPaymentGateway.class.getName(), "loadPayDollarAccounts()", null, "common", "System Error while loading specific gateway account from gateway_accounts_paydollar", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

     public static PayDollarAccount getPayDollarAccount(String accountId)
    {
        return payDollarAccounts.get(accountId);
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }


    /**
     *
     * @param accountId
     * @throws com.directi.pg.SystemError
     */
    public PayDollarPaymentGateway(String accountId)
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
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
                    if(trackingID ==null || trackingID.equals(""))
                    {
                         PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Tracking Id not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing the transaction",new Throwable("Tracking Id not provided while placing the transaction"));
                    }

                    if(requestVO ==null)
                    {
                         PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Request  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while placing the transaction",new Throwable("Request  not provided while placing the transaction"));
                    }

                    PayDollarRequestVO payDollarRequestVO  = (PayDollarRequestVO)requestVO;

                    if(payDollarRequestVO.getCardDetailsVO()==null)
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","CArdDetails  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,null,"CArdDetails  not provided while placing the transaction",new Throwable("CardDetails  not provided while placing the transaction"));
                    }

                    if(payDollarRequestVO.getTransDetailsVO()==null)
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","TransactionDetails  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing the transaction",new Throwable("TransactionDetails  not provided while placing the transaction"));
                    }
                    if(payDollarRequestVO.getAddressDetailsVO()==null)
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Address Details  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Address Details  not provided while placing the transaction",new Throwable("Address Details  not provided while placing the transaction"));
                    }

        /*if(payDollarRequestVO.getVbvDetails()==null)
        {
            log.info("VBV Details not provided");
             throw new SystemError("VBV Details not provided");
        }*/

                    CommCardDetailsVO commCardDetailsVO = payDollarRequestVO.getCardDetailsVO();
                    CommTransactionDetailsVO commTransactionDetailsVO = payDollarRequestVO.getTransDetailsVO();
                    CommAddressDetailsVO commAddressDetailsVO = payDollarRequestVO.getAddressDetailsVO();
                    //PayDollarVBVDetailsVO  payDollarVBVDetailsVO  = payDollarRequestVO.getVbvDetails();

                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    PayDollarAccount payDollarAccount = payDollarAccounts.get(accountId);

                    //Preparing map for authentication request
                    Map<String, String> authMap = new TreeMap<String, String>();

                    authMap.put(AUTH_TRACKING_ID,trackingID);
                    if(commTransactionDetailsVO.getAmount()==null || commTransactionDetailsVO.getAmount().equals(""))
                    {
                       PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Amount not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing the transaction",new Throwable("Amount not provided while placing the transaction"));
                    }
                    authMap.put(AUTH_AMOUNT,commTransactionDetailsVO.getAmount());
                    if(account.getCurrency()==null || account.getCurrency().equals(""))
                    {
                       PZExceptionHandler.raiseTechnicalViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Currency not confiured while placing the transaction,accountId::"+accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Currency not Configured while placing the transaction,accountId::"+accountId,new Throwable("Currency not Configured while placing the transaction,accountId::"+accountId));
                    }
                    authMap.put(AUTH_CURR_CODE,GatewayAccountService.getCurrencyCode(account.getCurrency()));
                    authMap.put(AUTH_LANG,LANG_E);
                    authMap.put(AUTH_MERCHANT_ID,payDollarAccount.getMerchantid());
                    if(commCardDetailsVO.getCardType().equals("VISA"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_VISA);
                    }
                    else if(commCardDetailsVO.getCardType().equals("MC"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_Master);
                    }
                    else if(commCardDetailsVO.getCardType().equals("DINER"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_Diners);
                    }
                    else if(commCardDetailsVO.getCardType().equals(CARD_TYPE_JCB))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_JCB);
                    }
                    else if(commCardDetailsVO.getCardType().equals("AMEX"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_AMEX);
                    }
                    else
                    {
                      PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Card Type is not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card Type is not provided while placing the transaction",new Throwable("Card Type is not provided while placing the transaction"));
                    }
                    if(commCardDetailsVO.getExpMonth()!=null && !commCardDetailsVO.getExpMonth().equals(""))
                    {
                       authMap.put(AUTH_EX_MONTH,commCardDetailsVO.getExpMonth());
                    }
                    else
                    {
                      PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Expiry Month not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month is not provided while placing the transaction",new Throwable("Expiry Month is not provided while placing the transaction"));
                    }

                    if(commCardDetailsVO.getExpYear()!=null && !commCardDetailsVO.getExpYear().equals(""))
                    {
                       authMap.put(AUTH_EX_YEAR,commCardDetailsVO.getExpYear());
                    }
                    else
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Expiry Year is not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year is not provided while placing the transaction",new Throwable("Expiry Year is not provided while placing the transaction"));
                    }
                    if(commCardDetailsVO.getCardNum()!=null && !commCardDetailsVO.getCardNum().equals(""))
                    {
                       authMap.put(AUTH_CARD_NO,commCardDetailsVO.getCardNum());
                    }
                    else
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Card NO is not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO is not provided while placing the transaction",new Throwable("Card NO is not provided while placing the transaction"));
                    }
                    if(commAddressDetailsVO.getFirstname()!=null && commAddressDetailsVO.getLastname()!=null && !commAddressDetailsVO.getLastname().equals("") && !commAddressDetailsVO.getFirstname().equals(""))
                    {
                       authMap.put(AUTH_CARD_HOLDER,commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());
                    }
                    else
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Card Holder Name is not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card Holder Name is not provided while placing the transaction",new Throwable("Card Holder Name is not provided while placing the transaction"));
                    }
                    if(commCardDetailsVO.getcVV()!=null && !commCardDetailsVO.getcVV().equals(""))
                    {
                       authMap.put(AUTH_SECURITY_CODE,commCardDetailsVO.getcVV());
                    }
                    else
                    {
                       PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","CVV is not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV is not provided while placing the transaction",new Throwable("CVV is not provided while placing the transaction"));
                    }


                    authMap.put(AUTH_TYPE,PAYMENT_TYPE_SALES);

                    String successUrl = SUCCESS_URL+"?ctoken="+payDollarRequestVO.getCtoken();
                    authMap.put(AUTH_SUCCESS_URL,successUrl);
                    String failUrl = FAIL_URL+"?ctoken="+payDollarRequestVO.getCtoken();
                    authMap.put(AUTH_FAIL_URL,failUrl);
                    String errorUrl = ERROR_URL+"?ctoken="+payDollarRequestVO.getCtoken();
                    authMap.put(AUTH_ERROR_URL,errorUrl);

                    authMap.put(AUTH_REMARKS,commTransactionDetailsVO.getOrderDesc());


                    //authMap = setVBVParameters(authMap,genericCardDetailsVO,payDollarTransDetailsVO,payDollarVBVDetailsVO);


                    //Preparing HTML form request


                    PayDollarUtils payDollarUtils = new PayDollarUtils();


                    String html = payDollarUtils.generateAutoSubmitForm(payDollarAccount.getAuthUrl(),authMap);
                    
                    PayDollarResponseVO  payDollarResponseVO = new PayDollarResponseVO();
                    payDollarResponseVO.setHtml(html);


                    /*String reqParameters = payDollarUtils.joinMapValue(authMap, '&');
                    System.out.println("Request string  from PayDollar ====  "+reqParameters);
                    String response =null;

                    try{

                     response = payDollarUtils.doPostURLConnection(TESTING_AUTH_URL,reqParameters);
                     log.debug("Response from PayDollar   ====  "+response);
                     System.out.println("Response from PayDollar ====  "+response);

                     }
                     catch(Exception e)
                     {
                        //log.error("calling SendMAil for  as Authentication Error " + Util.getStackTrace(e));
                        //Mail.sendAdminMail("Error while authentication for trackingID-" + trackingID, e.getMessage());
                        //log.info("called SendMAil for ");
                        log.info("There was an Error while posting data to bank. Please contact your merchant"+e);

                        throw new SystemError(" There was an Error while posting data to bank. Please contact your merchant.");

                     }


                    PayDollarResponseVO  payDollarResponseVO = new PayDollarResponseVO();

                    String[] arr =  payDollarUtils.getResArr(response);

                       for (int i = 0; i < arr.length; i++)

                       {

                            System.out.println(arr[i]);

                            String[] queryResultArr = arr[i].split("=");
                            // process  business logic
                            if (FIELD_SRC.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setSecondResponseCode(queryResultArr[1]);
                            }
                            if (FIELD_PRC.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setPrimaryResponseCode(queryResultArr[1]);
                            }
                            if (FIELD_ORD.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setBankOrderId(queryResultArr[1]);
                            }
                            if (FIELD_HOLDER.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setHolder(queryResultArr[1]);
                            }
                            if (FIELD_SUCCESS_CODE.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setSuccessCode(queryResultArr[1]);
                            }
                            if (FIELD_REF.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setOrderRef(queryResultArr[1]);
                            }
                            if (FIELD_PAY_REF.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setPayRef(queryResultArr[1]);
                            }
                            if (FIELD_AMT.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setAmount(queryResultArr[1]);
                            }
                            if (FIELD_CUR.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setCurrencyCode(queryResultArr[1]);
                            }
                            if (FIELD_AUTH_ID.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setAuthId(queryResultArr[1]);
                            }
                            if (FIELD_TX_TIME.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setTxTime(queryResultArr[1]);
                            }
                            if (FIELD_ERR_MSG.equals(queryResultArr[0]))
                            {
                                payDollarResponseVO.setErrMsg(queryResultArr[1]);
                            }

                        }
*/

                    return payDollarResponseVO;

    }

    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
     public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
     {
                    if(trackingID ==null || trackingID.equals(""))
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Tracking Id not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while authenticating transaction",new Throwable("Tracking Id not provided while authenticating transaction"));
                    }

                    if(requestVO ==null)
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Request  not provided while authenticating transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while authenticating transaction",new Throwable("REquest  not provided while authenticating transaction"));
                    }

                    PayDollarRequestVO payDollarRequestVO  = (PayDollarRequestVO)requestVO;

                    if(payDollarRequestVO.getCardDetailsVO()==null)
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","CardDetails  not provided while authenticating transaction",PZConstraintExceptionEnum.VO_MISSING,null,"CardDetails  not provided while authenticating transaction",new Throwable("CardDetails  not provided while authenticating transaction"));
                    }

                    if(payDollarRequestVO.getTransDetailsVO()==null)
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","TransactionDetails  not provided while authenticating transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while authenticating transaction",new Throwable("TransactionDetails  not provided while authenticating transaction"));
                    }

                    if(payDollarRequestVO.getAddressDetailsVO()==null)
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processSale()",null,"common","Address Details  not provided while authenticating  transaction", PZConstraintExceptionEnum.VO_MISSING,null,"Address Details  not provided while authenticating transaction",new Throwable("Address Details  not provided while authenticating transaction"));
                    }


                    CommCardDetailsVO commCardDetailsVO = payDollarRequestVO.getCardDetailsVO();
                    CommTransactionDetailsVO commTransactionDetailsVO = payDollarRequestVO.getTransDetailsVO();
                    CommAddressDetailsVO commAddressDetailsVO = payDollarRequestVO.getAddressDetailsVO();
                    //PayDollarVBVDetailsVO  payDollarVBVDetailsVO  = payDollarRequestVO.getVbvDetails();

                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    PayDollarAccount payDollarAccount = payDollarAccounts.get(accountId);
                    //Preparing map for authentication request
                    Map<String, String> authMap = new TreeMap<String, String>();

                    authMap.put(AUTH_TRACKING_ID,trackingID);
                    if(commTransactionDetailsVO.getAmount()==null || commTransactionDetailsVO.getAmount().equals(""))
                    {
                       PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Amount not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while authenticating transaction",new Throwable("Amount not provided while authenticating transaction"));
                    }
                    authMap.put(AUTH_AMOUNT,commTransactionDetailsVO.getAmount());
                    if(account.getCurrency()==null || account.getCurrency().equals(""))
                    {
                       PZExceptionHandler.raiseTechnicalViolationException(PayDollarPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Currency not Configured while authenticating transaction,accountId::" + accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Currency not Configured while authenticating transaction,accountId::", new Throwable("Currency not Configured while authenticating transaction,accountId::"));
                    }
                    authMap.put(AUTH_CURR_CODE,GatewayAccountService.getCurrencyCode(account.getCurrency()));
                    authMap.put(AUTH_LANG,LANG_E);
                    authMap.put(AUTH_MERCHANT_ID,payDollarAccount.getMerchantid());
                    if(commCardDetailsVO.getCardType().equals("VISA"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_VISA);
                    }
                    else if(commCardDetailsVO.getCardType().equals("MC"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_Master);
                    }
                    else if(commCardDetailsVO.getCardType().equals("DINER"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_Diners);
                    }
                    else if(commCardDetailsVO.getCardType().equals(CARD_TYPE_JCB))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_JCB);
                    }
                    else if(commCardDetailsVO.getCardType().equals("AMEX"))
                    {
                       authMap.put(AUTH_P_METHOD,CARD_TYPE_AMEX);
                    }
                    else
                    {
                      PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Card Type not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card Type not provided while authenticating transaction",new Throwable("Card Type not provided while authenticating transaction"));
                    }
                    if(commCardDetailsVO.getExpMonth()!=null && !commCardDetailsVO.getExpMonth().equals(""))
                    {
                       authMap.put(AUTH_EX_MONTH,commCardDetailsVO.getExpMonth());
                    }
                    else
                    {
                      PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Expiry Month not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while authenticating transaction",new Throwable("Expiry Month not provided while authenticating transaction"));
                    }

                    if(commCardDetailsVO.getExpYear()!=null && !commCardDetailsVO.getExpYear().equals(""))
                    {
                       authMap.put(AUTH_EX_YEAR,commCardDetailsVO.getExpYear());
                    }
                    else
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Expiry Year not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while authenticating transaction",new Throwable("Expiry Year not provided while authenticating transaction"));
                    }
                    if(commCardDetailsVO.getCardNum()!=null && !commCardDetailsVO.getCardNum().equals(""))
                    {
                       authMap.put(AUTH_CARD_NO,commCardDetailsVO.getCardNum());
                    }
                    else
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Card NO not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while authenticating transaction",new Throwable("Card NO not provided while authenticating transaction"));
                    }
                    if(commAddressDetailsVO.getFirstname()!=null && commAddressDetailsVO.getLastname()!=null && !commAddressDetailsVO.getLastname().equals("") && !commAddressDetailsVO.getFirstname().equals(""))
                    {
                       authMap.put(AUTH_CARD_HOLDER,commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());
                    }
                    else
                    {
                        PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","Card Holder Name not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card Holder Name not provided while authenticating transaction",new Throwable("Card Holder Name not provided while authenticating transaction"));
                    }
                    if(commCardDetailsVO.getcVV()!=null && !commCardDetailsVO.getcVV().equals(""))
                    {
                       authMap.put(AUTH_SECURITY_CODE,commCardDetailsVO.getcVV());
                    }
                    else
                    {
                       PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processAuthentication()",null,"common","CVV not provided while authenticating transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while authenticating transaction",new Throwable("CVV not provided while authenticating transaction"));
                    }


                    authMap.put(AUTH_TYPE,PAYMENT_TYPE_AUTH);
                    String successUrl = SUCCESS_URL+"?ctoken="+payDollarRequestVO.getCtoken();
                    authMap.put(AUTH_SUCCESS_URL,successUrl);
                    String failUrl = FAIL_URL+"?ctoken="+payDollarRequestVO.getCtoken();
                    authMap.put(AUTH_FAIL_URL,failUrl);
                    String errorUrl = ERROR_URL+"?ctoken="+payDollarRequestVO.getCtoken();
                    authMap.put(AUTH_ERROR_URL,errorUrl);

                    authMap.put(AUTH_REMARKS,commTransactionDetailsVO.getOrderDesc());


                    //authMap = setVBVParameters(authMap,genericCardDetailsVO,payDollarTransDetailsVO,payDollarVBVDetailsVO);


                    //Preparing HTML form request


                    PayDollarUtils payDollarUtils = new PayDollarUtils();


                    String html = payDollarUtils.generateAutoSubmitForm(payDollarAccount.getAuthUrl(),authMap);
                    
                    PayDollarResponseVO  payDollarResponseVO = new PayDollarResponseVO();
                    payDollarResponseVO.setHtml(html);



                    return payDollarResponseVO;

    }

    /**
     *
     * @param authMap
     * @param genericCardDetailsVO
     * @param payDollarTransDetailsVO
     * @param payDollarVBVDetailsVO
     * @return
     */
    private Map<String, String> setVBVParameters(Map<String, String> authMap, GenericCardDetailsVO genericCardDetailsVO, PayDollarTransDetailsVO payDollarTransDetailsVO, PayDollarVBVDetailsVO payDollarVBVDetailsVO) throws SystemError
    {

                            // VBV Parameters
                           if(payDollarVBVDetailsVO.isVbvTransaction())
                           {

                              authMap.put(AUTH_VBV_TRANS,VBV_TRANS_TRUE);

                              //setting  vbvCHReturnCode
                              if(payDollarVBVDetailsVO.getVbvCHReturnCode()==null || payDollarVBVDetailsVO.getVbvCHReturnCode().equals("") )
                              {
                                  authMap.put(AUTH_VBV_CH_RETURN_CODE,"-1");
                              }
                              else if(payDollarVBVDetailsVO.getVbvCHReturnCode().equals("Y"))
                               {
                                   authMap.put(AUTH_VBV_CH_RETURN_CODE,"0");
                               }
                               else if(payDollarVBVDetailsVO.getVbvCHReturnCode().equals("N"))
                               {
                                   authMap.put(AUTH_VBV_CH_RETURN_CODE,"1001");
                               }
                               else
                               {
                                   authMap.put(AUTH_VBV_CH_RETURN_CODE,"-1");
                               }

                              //setting vbvPAReturnCode

                              if(payDollarVBVDetailsVO.getVbvPAReturnCode()==null || payDollarVBVDetailsVO.getVbvPAReturnCode().equals("") )
                              {
                                  authMap.put(AUTH_VBV_PA_RETURN_CODE,"-1");
                              }
                              else if(payDollarVBVDetailsVO.getVbvCHReturnCode().equals("Y"))
                               {
                                   authMap.put(AUTH_VBV_PA_RETURN_CODE,"0");
                               }
                              else if(payDollarVBVDetailsVO.getVbvCHReturnCode().equals("A"))
                               {
                                   authMap.put(AUTH_VBV_PA_RETURN_CODE,"1000");
                               }
                               else if(payDollarVBVDetailsVO.getVbvCHReturnCode().equals("N"))
                               {
                                   authMap.put(AUTH_VBV_PA_RETURN_CODE,"1003");
                               }
                               else
                               {
                                   authMap.put(AUTH_VBV_PA_RETURN_CODE,"-1");
                               }

                               // setting vbvTransTime

                               if(payDollarVBVDetailsVO.getVbvTransTime()!=null && !payDollarVBVDetailsVO.getVbvTransTime().equals(""))
                               {
                                  //Todo  Format should be (YYYYMMDD HH:MM:SS)
                                  authMap.put(AUTH_VBV_TRANS_TIME,payDollarVBVDetailsVO.getVbvTransTime());
                               }
                               else
                               {
                                   log.info("vbvTransTime value not provided");
                                   throw new SystemError("vbvTransTime value not provided");
                               }


                               //setting vbvTransAuth

                               if(payDollarVBVDetailsVO.getVbvTransAuth()!=null && !payDollarVBVDetailsVO.getVbvTransAuth().equals(""))
                               {

                                   //Todo Base64Encoded
                                   authMap.put(AUTH_VBV_TRANS_AUTH,payDollarVBVDetailsVO.getVbvTransAuth());

                               }
                               else
                               {
                                   log.info("vbvTransAuth value not provided");
                                   throw new SystemError("vbvTransAuth value not provided");
                               }

                             //setting vbvCAVVAlgo

                               if(payDollarVBVDetailsVO.getVbvCAVVAlgo()!=null && !payDollarVBVDetailsVO.getVbvCAVVAlgo().equals(""))
                               {


                                   authMap.put(AUTH_VBV_CAVV_ALGO,payDollarVBVDetailsVO.getVbvCAVVAlgo());

                               }
                               else
                               {
                                   log.info("vbvCAVVAlgo value not provided");
                                   throw new SystemError("vbvCAVVAlgo value not provided");
                               }


                             //setting vbvXID
                               if(payDollarVBVDetailsVO.getVbvXID()!=null && !payDollarVBVDetailsVO.getVbvXID().equals(""))
                               {

                                   if(payDollarVBVDetailsVO.getVbvXID().length()!=20)
                                   {
                                   log.info("vbvXID value not correct length should be 20 characters");
                                   throw new SystemError("vbvXID value not correct length should be 20 characters");
                                   }
                                   authMap.put(AUTH_VBV_XID,payDollarVBVDetailsVO.getVbvXID());

                               }
                               else
                               {
                                   log.info("vbvXID value not provided");
                                   throw new SystemError("vbvXID value not provided");
                               }

                             // vbvMerchantID
                                if(payDollarVBVDetailsVO.getVbvMerchantID()!=null && !payDollarVBVDetailsVO.getVbvMerchantID().equals(""))
                               {


                                   authMap.put(AUTH_VBV_MERCHANT_ID,payDollarVBVDetailsVO.getVbvMerchantID());

                               }
                               else
                               {
                                   log.info("vbvMerchantID value not provided");
                                   throw new SystemError("vbvMerchantID value not provided");
                               }

                              //vbvAcquirerBin
                               if(payDollarVBVDetailsVO.getVbvAcquirerBin()!=null && !payDollarVBVDetailsVO.getVbvAcquirerBin().equals(""))
                               {


                                   authMap.put(AUTH_VBV_AQUIRER_BIN,payDollarVBVDetailsVO.getVbvAcquirerBin());

                               }
                               else
                               {
                                   log.info("vbvAcquirerBin value not provided");
                                   throw new SystemError("vbvAcquirerBin value not provided");
                               }

                               //vbvTransStatus
                               if(payDollarVBVDetailsVO.getVbvTransStatus()!=null && !payDollarVBVDetailsVO.getVbvTransStatus().equals(""))
                               {


                                   authMap.put(AUTH_VBV_TRANS_STATUS,payDollarVBVDetailsVO.getVbvTransStatus());

                               }
                               else
                               {
                                   authMap.put(AUTH_VBV_TRANS_STATUS,"U");
                               }



                           }
                           else
                           {
                               authMap.put(AUTH_VBV_TRANS,VBV_TRANS_FALSE);


                           }



                           // Setting  vbvTransECI
                           if(genericCardDetailsVO.getCardType().equals(CARD_TYPE_VISA) || genericCardDetailsVO.getCardType().equals(CARD_TYPE_JCB))
                           {
                               if(payDollarTransDetailsVO.getEnrolled3D()==null || payDollarTransDetailsVO.getEnrolled3D().equals("") )
                               {
                                    authMap.put(AUTH_VBV_ECI,"07");
                               }
                               else if(payDollarTransDetailsVO.getEnrolled3D().equals("Y"))
                               {
                                    if(payDollarVBVDetailsVO.getVbvTransECI()!=null && !payDollarVBVDetailsVO.getVbvTransECI().equals(""))
                                    {
                                        authMap.put(AUTH_VBV_ECI,payDollarVBVDetailsVO.getVbvTransECI());
                                    }
                                    else
                                    {
                                       authMap.put(AUTH_VBV_ECI,"07");
                                    }
                               }
                               else if(payDollarTransDetailsVO.getEnrolled3D().equals("N"))
                               {
                                   authMap.put(AUTH_VBV_ECI,"06");
                               }
                               else
                               {
                                   authMap.put(AUTH_VBV_ECI,"07");
                               }

                           }
                          else if(genericCardDetailsVO.getCardType().equals(CARD_TYPE_Master))
                          {
                              if(payDollarTransDetailsVO.getEnrolled3D()==null || payDollarTransDetailsVO.getEnrolled3D().equals("") )
                              {
                                   authMap.put(AUTH_VBV_ECI,"00");
                              }
                              else if(payDollarTransDetailsVO.getEnrolled3D().equals("Y"))
                              {
                                   if(payDollarVBVDetailsVO.getVbvTransECI()!=null && !payDollarVBVDetailsVO.getVbvTransECI().equals(""))
                                   {
                                       authMap.put(AUTH_VBV_ECI,payDollarVBVDetailsVO.getVbvTransECI());
                                   }
                                   else
                                   {
                                      authMap.put(AUTH_VBV_ECI,"00");
                                   }
                              }
                              else if(payDollarTransDetailsVO.getEnrolled3D().equals("N"))
                              {
                                  authMap.put(AUTH_VBV_ECI,"01");
                              }
                              else
                              {
                                  authMap.put(AUTH_VBV_ECI,"00");
                              }

                          }
                          else
                          {
                                 authMap.put(AUTH_VBV_ECI,"07");
                          }



            return authMap;
    }

  /**
   *
   * @param trackingID
   * @param requestVO
   * @return
   * @throws SystemError
   */
   public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
   {

        if(requestVO ==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processCapture()",null,"common","Request  not provided while capturing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while capturing the transaction",new Throwable("Request  not provided while capturing the transaction"));
        }

        PayDollarRequestVO payDollarRequestVO  = (PayDollarRequestVO)requestVO;


        if(payDollarRequestVO.getTransDetailsVO()==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processCapture()",null,"common","TransactionDetails  not provided while capturing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while capturing the transaction",new Throwable("TransactionDetails  not provided while capturing the transaction"));
        }


        CommTransactionDetailsVO payDollarTransDetailsVO = payDollarRequestVO.getTransDetailsVO();


        PayDollarAccount payDollarAccount = payDollarAccounts.get(accountId);

        if(payDollarRequestVO.getPayRef()==null || payDollarRequestVO.getPayRef().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processCapture()",null,"common","Payment Ref not provided while capturing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Payment Ref not provided while capturing the transaction",new Throwable("Payment Ref not provided while capturing the transaction"));
        }

        if(payDollarTransDetailsVO.getAmount()==null || payDollarTransDetailsVO.getAmount().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processCapture()",null,"common","Amount not provided while capturing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while capturing the transaction",new Throwable("Amount not provided while capturing the transaction"));
        }
        //Preparing map for capture request
        Map<String, String> captureMap = new TreeMap<String, String>();

        captureMap.put(FIELD_MERCHANT_ID,payDollarAccount.getMerchantid());
        captureMap.put(FIELD_LOGIN_ID,payDollarAccount.getLoginId());
        captureMap.put(FIELD_PASSWORD,payDollarAccount.getPassword());
        captureMap.put(FIELD_ACTION_TYPE,ACTION_TYPE_CAPTURE);
        captureMap.put(FIELD_pay_Ref,payDollarRequestVO.getPayRef());
        captureMap.put(FIELD_AMOUNT,payDollarTransDetailsVO.getAmount());

        PayDollarUtils payDollarUtils = new PayDollarUtils();
        String reqParameters = payDollarUtils.joinMapValue(captureMap, '&');
        //System.out.println("Request string  from PayDollar for Capture====  "+reqParameters);
        String response =null;


         response = payDollarUtils.doPostURLConnection(payDollarAccount.getApiUrl(),reqParameters);
         log.debug("Response from PayDollar for Capture  ====  "+response);
         transactionLogger.debug("Response from PayDollar for Capture  ====  "+response);





        PayDollarResponseVO  payDollarResponseVO = new PayDollarResponseVO();

        String[] arr =  payDollarUtils.getResArr(response);

           for (int i = 0; i < arr.length; i++)

           {

                log.debug(arr[i]);

                String[] queryResultArr = arr[i].split("=");
                // process  business logic
                if (FIELD_RESULT_CODE.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setResultCode(queryResultArr[1]);
                }
                if (FIELD_ORDER_STATUS.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setTransactionStatus(queryResultArr[1]);
                }
                if (FIELD_ref.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setTransactionId(queryResultArr[1]);
                }
                if (FIELD_pay_Ref.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setPayRef(queryResultArr[1]);
                }
                if (FIELD_amt.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setAmount(queryResultArr[1]);
                }
                if (FIELD_cur.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setCurrencyCode(queryResultArr[1]);
                }
                if (FIELD_ERR_MSG.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setErrMsg(queryResultArr[1]);
                }

           }
        payDollarResponseVO.setErrorCode(payDollarResponseVO.getResultCode());
        payDollarResponseVO.setDescription(payDollarResponseVO.getErrMsg());
        payDollarResponseVO.setTransactionId(payDollarResponseVO.getPayRef());
        log.debug("TRAN STATUS" + payDollarResponseVO.getTransactionStatus() + "-ERRMSG-" + payDollarResponseVO.getErrMsg());
        transactionLogger.debug("TRAN STATUS"+payDollarResponseVO.getTransactionStatus()+"-ERRMSG-"+payDollarResponseVO.getErrMsg());
        if(payDollarResponseVO.getResultCode().equals("0"))
            payDollarResponseVO.setStatus("success");
        else
            payDollarResponseVO.setStatus("failed");
        return payDollarResponseVO;
    }

    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {

         if(requestVO ==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processVoid()",null,"common","Tracking ID not provided while cancelling the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking ID not provided while cancelling the transaction",new Throwable("Tracking ID not provided while cancelling the transaction"));
        }

        if(((PayDollarResponseVO)processQuery(trackingID, requestVO)).getGatewayStatus().equalsIgnoreCase("Authorized"))
        {
            processCapture(trackingID, requestVO);
            return processRefund(trackingID ,requestVO);
        }
        else{
        PayDollarRequestVO payDollarRequestVO  = (PayDollarRequestVO)requestVO;




        PayDollarAccount payDollarAccount = payDollarAccounts.get(accountId);

        if(payDollarRequestVO.getPayRef()==null || payDollarRequestVO.getPayRef().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processVoid()",null,"common","Payment Ref not provided while cancelling the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Payment Ref not provided while cancelling the transaction",new Throwable("Payment Ref not provided while cancelling the transaction"));
        }


        //Preparing map for void request
        Map<String, String> captureMap = new TreeMap<String, String>();

        captureMap.put(FIELD_MERCHANT_ID,payDollarAccount.getMerchantid());
        captureMap.put(FIELD_LOGIN_ID,payDollarAccount.getLoginId());
        captureMap.put(FIELD_PASSWORD,payDollarAccount.getPassword());
        captureMap.put(FIELD_ACTION_TYPE,ACTION_TYPE_VOID);
        captureMap.put(FIELD_pay_Ref,payDollarRequestVO.getPayRef());
        captureMap.put(FIELD_AMOUNT, payDollarRequestVO.getTransDetailsVO().getAmount());

        PayDollarUtils payDollarUtils = new PayDollarUtils();
        String reqParameters = payDollarUtils.joinMapValue(captureMap, '&');
        //System.out.println("Request string  from PayDollar for Void====  "+reqParameters);
        String response =null;



         response = payDollarUtils.doPostURLConnection(payDollarAccount.getApiUrl(),reqParameters);
         log.debug("Response from PayDollar for Void  ====  "+response);
         transactionLogger.debug("Response from PayDollar for Void  ====  "+response);
         //System.out.println("Response from PayDollar for Void====  "+response);




        PayDollarResponseVO  payDollarResponseVO = new PayDollarResponseVO();

        String[] arr =  payDollarUtils.getResArr(response);

           for (int i = 0; i < arr.length; i++)

           {

                log.debug(arr[i]);

                String[] queryResultArr = arr[i].split("=");
                // process  business logic
                if (FIELD_RESULT_CODE.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setResultCode(queryResultArr[1]);
                }
                if (FIELD_ORDER_STATUS.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setTransactionStatus(queryResultArr[1]);
                }
                if (FIELD_ref.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setTransactionId(queryResultArr[1]);
                }
                if (FIELD_pay_Ref.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setPayRef(queryResultArr[1]);
                }
                if (FIELD_amt.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setAmount(queryResultArr[1]);
                }
                if (FIELD_cur.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setCurrencyCode(queryResultArr[1]);
                }
                if (FIELD_ERR_MSG.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setErrMsg(queryResultArr[1]);
                }

           }
        payDollarResponseVO.setErrorCode(payDollarResponseVO.getResultCode());
        payDollarResponseVO.setDescription(payDollarResponseVO.getErrMsg());
        payDollarResponseVO.setTransactionId(payDollarResponseVO.getPayRef());
        if(payDollarResponseVO.getResultCode().equals("0"))
            payDollarResponseVO.setStatus("success");
        else
            payDollarResponseVO.setStatus("failed");

        return payDollarResponseVO;

        }
    }
    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZDBViolationException, PZTechnicalViolationException
    {


        if(requestVO ==null)
        {
             PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processRefund()",null,"common","Request  not providded while Refunding via PAYDollar",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not providded while Refunding via PAYDollar",new Throwable("Request  not providded while Refunding via PAYDollar"));
        }

        PayDollarRequestVO payDollarRequestVO  = (PayDollarRequestVO)requestVO;


        if(payDollarRequestVO.getTransDetailsVO()==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processRefund()",null,"common","TransactionDetails  not providded while Refunding via PAYDollar",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not providded while Refunding via PAYDollar",new Throwable("TransactionDetails  not providded while Refunding via PAYDollar"));
        }

        if(((PayDollarResponseVO )processQuery(trackingID, requestVO)).getGatewayStatus().equalsIgnoreCase("Accepted")){

        CommTransactionDetailsVO payDollarTransDetailsVO = payDollarRequestVO.getTransDetailsVO();
            
            
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        PayDollarAccount payDollarAccount = payDollarAccounts.get(accountId);
        if(payDollarRequestVO.getPayRef()==null || payDollarRequestVO.getPayRef().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processRefund()",null,"common","Payment Ref not providded while Refunding via PAYDollar",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"PAyment Ref not providded while Refunding via PAYDollar",new Throwable("Payment Ref not providded while Refunding via PAYDollar"));
        }


            //checking database captured amount
            String query="select captureamount from transaction_common where trackingid="+trackingID;
            String captureamount="";
            Connection conn=null;
            try{
                
                conn=Database.getConnection();
                ResultSet rs=Database.executeQuery(query, conn);
                if(rs.next())
                {
                    captureamount=rs.getString("captureamount");
                    
                }
                
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException(PayDollarPaymentGateway.class.getName(),"processRefund()",null,"common","System error while getting captureAmount of the transaction",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
            }
            catch (SQLException se)
            {
                PZExceptionHandler.raiseDBViolationException(PayDollarPaymentGateway.class.getName(),"processRefund()",null,"common","Sql Exception while getting captureAmount of the transaction",PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause());
            }
            finally
            {
                Database.closeConnection(conn);
            }

            if(!captureamount.equals(payDollarTransDetailsVO.getAmount()))
            {
                    PZExceptionHandler.raiseTechnicalViolationException(PayDollarPaymentGateway.class.getName(), "processRefund()", null, "common", "Partial Refund is Not Supported For This Bank(PAY Dollar)", PZTechnicalExceptionEnum.NOT_ALLOWED_BY_GATEWAY,null, "Partial Refund is Not Supported For This Bank(PAY Dollar)", new Throwable("Partial Refund is Not Supported For This Bank(PAY Dollar)"));
            }
            


        //Preparing map for capture request
        Map<String, String> captureMap = new TreeMap<String, String>();

        captureMap.put(FIELD_MERCHANT_ID,payDollarAccount.getMerchantid());
        captureMap.put(FIELD_LOGIN_ID,payDollarAccount.getLoginId());
        captureMap.put(FIELD_PASSWORD,payDollarAccount.getPassword());
        captureMap.put(FIELD_ACTION_TYPE,ACTION_TYPE_REFUND);
        captureMap.put(FIELD_pay_Ref,payDollarRequestVO.getPayRef());
        

        PayDollarUtils payDollarUtils = new PayDollarUtils();
        String reqParameters = payDollarUtils.joinMapValue(captureMap, '&');
        //System.out.println("Request string  from PayDollar for Refund====  "+reqParameters);
        String response =null;



         response = payDollarUtils.doPostURLConnection(payDollarAccount.getApiUrl(),reqParameters);
         log.debug("Response from PayDollar for Refund  ====  "+response);
         //System.out.println("Response from PayDollar for Refund====  "+response);




        PayDollarResponseVO  payDollarResponseVO = new PayDollarResponseVO();

        String[] arr =  payDollarUtils.getResArr(response);

           for (int i = 0; i < arr.length; i++)

           {

                log.debug(arr[i]);

                String[] queryResultArr = arr[i].split("=");
                // process  business logic
                if (FIELD_RESULT_CODE.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setResultCode(queryResultArr[1]);
                }
                if (FIELD_ORDER_STATUS.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setTransactionStatus(queryResultArr[1]);
                }
                if (FIELD_ref.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setTransactionId(queryResultArr[1]);
                }
                if (FIELD_pay_Ref.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setPayRef(queryResultArr[1]);
                }
                if (FIELD_amt.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setAmount(queryResultArr[1]);
                }
                if (FIELD_cur.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setCurrencyCode(queryResultArr[1]);
                }
                if (FIELD_ERR_MSG.equals(queryResultArr[0]))
                {
                    payDollarResponseVO.setErrMsg(queryResultArr[1]);
                }

           }

        payDollarResponseVO.setErrorCode(payDollarResponseVO.getResultCode());
        payDollarResponseVO.setDescription(payDollarResponseVO.getErrMsg());
        payDollarResponseVO.setTransactionId(payDollarResponseVO.getPayRef());
        if(payDollarResponseVO.getResultCode().equals("0"))
            payDollarResponseVO.setStatus("success");
        else
            payDollarResponseVO.setStatus("failed");
           return payDollarResponseVO;
    }
        else {
            return processVoid(trackingID, requestVO);
        }
    
     
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        PayDollarResponseVO payDollarResponseVO = new PayDollarResponseVO();


        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processQuery()",null,"common","Request  not provided while Querying transction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Querying transction",new Throwable("Request  not provided while Querying transction"));
        }

        PayDollarRequestVO payDollarRequestVO  = (PayDollarRequestVO)requestVO;


        if(payDollarRequestVO.getTransDetailsVO()==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processQuery()",null,"common","TransactionDetails  not provided while Querying transction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while Querying transction",new Throwable("TransactionDetails  not provided while Querying transction"));
        }


        CommTransactionDetailsVO payDollarTransDetailsVO = payDollarRequestVO.getTransDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        PayDollarAccount payDollarAccount = payDollarAccounts.get(accountId);
        if(payDollarRequestVO.getPayRef()==null || payDollarRequestVO.getPayRef().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PayDollarPaymentGateway.class.getName(),"processQuery()",null,"common","Payment Ref not provided while Querying transction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Payment Ref not provided while Querying transction",new Throwable("Payment Ref not provided while Querying transction"));
        }

        //Preparing map for capture request
        Map<String, String> captureMap = new TreeMap<String, String>();

        captureMap.put(FIELD_MERCHANT_ID,payDollarAccount.getMerchantid());
        captureMap.put(FIELD_LOGIN_ID,payDollarAccount.getLoginId());
        captureMap.put(FIELD_PASSWORD,payDollarAccount.getPassword());
        captureMap.put(FIELD_ACTION_TYPE,ACTION_TYPE_QUERY);
        captureMap.put(FIELD_pay_Ref,payDollarRequestVO.getPayRef());
        captureMap.put(FIELD_AMOUNT,payDollarTransDetailsVO.getAmount());

        PayDollarUtils payDollarUtils = new PayDollarUtils();
        String reqParameters = payDollarUtils.joinMapValue(captureMap, '&');
        //System.out.println("Request string  from PayDollar for INQUIRY ====  "+reqParameters);
        String response =null;


            response = payDollarUtils.doPostURLConnection(payDollarAccount.getApiUrl(),reqParameters);
            /*log.debug("Response from PayDollar for INQUIRY====  "+response);
            System.out.println("Response from PayDollar for INQUIRY ====  "+response);
*/



        Map<String, String> responseMap = null;

        
        Document doc=PayDollarUtils.createDocumentFromString(response);
        
        
        if(doc !=null)
        {
            responseMap = PayDollarUtils.ReadXMLResponseForInquiry(doc);
        }



        

        payDollarResponseVO.setPrimaryResponseCode(responseMap.get(Elements.ELEM_PRC));
        payDollarResponseVO.setSecondResponseCode(responseMap.get(Elements.ELEM_SRC));
        payDollarResponseVO.setBankOrderId(responseMap.get(Elements.ELEM_ORD));
        payDollarResponseVO.setErrMsg(responseMap.get(Elements.ELEM_ERRMSG));
        payDollarResponseVO.setHolder(responseMap.get(Elements.ELEM_HOLDER));
        payDollarResponseVO.setPayRef(responseMap.get(Elements.ELEM_PAYREF));
        payDollarResponseVO.setTransactionId(responseMap.get(Elements.ELEM_PAYREF));
        payDollarResponseVO.setAmount(responseMap.get(Elements.ELEM_AMT));
        payDollarResponseVO.setGatewayStatus(responseMap.get(Elements.ELEM_ORDERSTATUS));
        payDollarResponseVO.setRef(responseMap.get(Elements.ELEM_REF));
        
        
        

        return payDollarResponseVO;
    }


    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


}