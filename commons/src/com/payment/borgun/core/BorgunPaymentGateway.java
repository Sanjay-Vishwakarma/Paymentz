package com.payment.borgun.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.RecurringManager;
import com.manager.vo.RecurringBillingVO;
import com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator;
import com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringUtils;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 9/7/13
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BorgunPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "Borgun";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Borgun");
    //private static Logger log = new Logger(BorgunPaymentGateway.class.getName());
    private static Logger logger = new Logger(BorgunPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BorgunPaymentGateway.class.getName());
    private final int TIMEOUT = 30000;
    int processor = Integer.parseInt(RB.getString("processorid"));
    private String username = "pz";
    //private String password = "dM.439";
    private String password = "HiOp.333";
    private String url = "https://gatewaytest.borgun.is/ws/Heimir.pub.ws:Authorization";
    private String version = "1000";
    //  private int processor = 108;
    private int terminalId = 1;
    private int saleTransType = 1;
    private int authTransType = 5;
    private int refundTransType = 3;
    private int captureTransType = 1;
    private int voidTransType = 5;
    private String rrnSuffix = "PBG";
    private int recurringTerminalId = 3;



    public BorgunPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        InquiryAuthorization inquiryAuthorization = new InquiryAuthorization();
        InquiryAuthorizationReply inquiryAuthorizationReply=new InquiryAuthorizationReply();
        BorgunPaymentGateway b = new BorgunPaymentGateway("");

       /* Hashtable<String,String> data = new Hashtable<String,String>();
        data = b.getData("19731", "inquiry");*/

        inquiryAuthorization.setVersion("1000");
        inquiryAuthorization.setProcessor("108");
        inquiryAuthorization.setMerchantID("108");
        inquiryAuthorization.setTerminalID(1);
        inquiryAuthorization.setBatchNumber("");
        inquiryAuthorization.setFromDate("");
        inquiryAuthorization.setToDate("");
        inquiryAuthorization.setRrn("PBG000042468");
        //inquiryAuthorization.setRrn("PBG000042468");

        try
        {
            String response = b.make_inquiry_request(inquiryAuthorization);

            inquiryAuthorizationReply = b.get_GetInquiryReply(String.valueOf(response));
            String actionCode = inquiryAuthorizationReply.getActionCode();

            //Map<String,String> inquiry = BorgunUtills.ReadInquiryResponseNew(response);
            //System.out.println("inquiry res---"+response);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::",e);
        }
    }

    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String response,authCode,actionCode,date,transaction,cardAccountId,storeTerminal,cardType;
        int batchId = 0;
        Functions functions = new Functions();

        GetAuthorizationReply getAuthorizationReply;
        CommResponseVO commResponseVO = new CommResponseVO();
        BorgunUtills borgunUtills = new BorgunUtills();

        BorgunRequestVO borgunRequestVO = (BorgunRequestVO) requestVO;
        GetAuthorization getAuthorization = new GetAuthorization();

        getAuthorization.setVersion(version);
        getAuthorization.setProcessor(processor);
        getAuthorization.setMerchantID(borgunRequestVO.getCommMerchantVO().getMerchantUsername());
        getAuthorization.setTerminalID(terminalId);
        getAuthorization.setTransType(authTransType);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String displayName = account.getDisplayName();


        Hashtable hash = borgunUtills.getBorgunAcoountDetails(accountId);

        String merchantHome = (String) hash.get("MerchantHome");
        String merchantCity = (String) hash.get("MerchantCity");
        String merchantZip = (String) hash.get("MerchantZipCode");
        String merchantCountry = (String) hash.get("MerchantCountry");
        String eCommerce = (String) hash.get("Ecommerce");
        String eCommercePhone = (String) hash.get("EcommercePhone");
        if (account.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
        {
            displayName += borgunRequestVO.getTransDetailsVO().getOrderId();
            if (!displayName.equals("") && displayName.length()!=0 && displayName.length() > 21)
            {
                displayName = displayName.substring(0,21);
            }
            getAuthorization.setMerchantName(displayName);
            if (!merchantHome.equals("") && merchantHome.length()!=0 && merchantHome.length() > 19)
            {
                merchantHome = merchantHome.substring(0,19);
            }
            getAuthorization.setMerchantHome(merchantHome);

            if (!merchantCity.equals("") && merchantCity.length()!=0 && merchantCity.length() > 12)
            {
                merchantCity = merchantCity.substring(0,12);
            }
            getAuthorization.setMerchantCity(merchantCity);

            if (!merchantZip.equals("") && merchantZip.length()!=0 && merchantZip.length() > 9)
            {
                merchantZip = merchantZip.substring(0,9);
            }
            getAuthorization.setMerchantZipCode(merchantZip);

            if (!merchantCountry.equals("") && merchantCountry.length()!=0 && merchantCountry.length() > 2)
            {
                merchantCountry = merchantCountry.substring(0,2);
            }
            getAuthorization.setMerchantCountry(merchantCountry);

            if (!eCommerce.equals("") && eCommerce.length()!=0 && eCommerce.length() > 1)
            {
                eCommerce = eCommerce.substring(0,1);
            }
            getAuthorization.setEcommerce(eCommerce);

            if (!eCommercePhone.equals("") && eCommercePhone.length()!=0 && eCommercePhone.length() > 12)
            {
                eCommercePhone = eCommercePhone.substring(0,12);
            }
            getAuthorization.setEcommercePhone(eCommercePhone);
        }

        String amount =  borgunRequestVO.getTransDetailsVO().getAmount();
        getAuthorization.setTrAmount(getAmount(amount,borgunRequestVO.getTransDetailsVO().getCurrency()));


        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(borgunRequestVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        getAuthorization.setTrCurrency(currencyCode);

        getAuthorization.setDateAndTime(borgunRequestVO.getAddressDetailsVO().getTime());
        date = getDate();
        getAuthorization.setDateAndTime(date);
        getAuthorization.setPan(borgunRequestVO.getCardDetailsVO().getCardNum());
        String year = borgunRequestVO.getCardDetailsVO().getExpYear();

        String onlyYear = year.substring(2);
        getAuthorization.setExpDate(onlyYear + borgunRequestVO.getCardDetailsVO().getExpMonth());
        getAuthorization.setCvc2(borgunRequestVO.getCardDetailsVO().getcVV());

        String rrn = getRRN(trackingID);
        getAuthorization.setRrn(rrn);

        response = make_request(getAuthorization);

        transactionLogger.error("-------process authentication response-----"+response);


        if(!response.equalsIgnoreCase("ConnectionError"))
        {
            getAuthorizationReply = get_GetAuthorizationReply(response);
            actionCode = getAuthorizationReply.getActionCode();
            commResponseVO.setDescription(BorgunErrorCodeClass.getErrorStringFromCode(actionCode));
            if(actionCode.equalsIgnoreCase("000"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(getAuthorizationReply.getAuthCode());
                commResponseVO.setDescription("Transaction Successful");
                commResponseVO.setDescriptor(displayName);
                commResponseVO.setResponseTime(getAuthorizationReply.getDateAndTime());
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setErrorCode(getAuthorizationReply.getAuthCode());
                commResponseVO.setDescription(BorgunErrorCodeClass.getErrorStringFromCode(actionCode));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                commResponseVO.setResponseTime(getAuthorizationReply.getDateAndTime());
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }

            int res = insertData(getAuthorizationReply,trackingID,"auth",5);

        }else
        {
            commResponseVO.setDescription("ConnectionError");
            commResponseVO.setErrorCode("0");
            commResponseVO.setStatus("fail");

        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        String response, authCode = "", actionCode = "", date, transaction, cardAccountId, rbId = "";
        Functions functions = new Functions();
        int batchId =0;

        GetAuthorizationReply getAuthorizationReply;
        CommResponseVO commResponseVO = new CommResponseVO();

        BorgunRequestVO borgunRequestVO = (BorgunRequestVO) requestVO;
        RecurringBillingVO recurringBillingVO = borgunRequestVO.getRecurringBillingVO();
        String isManualRecurring = "";

        if (recurringBillingVO != null)
        {
            isManualRecurring = recurringBillingVO.getIsManualRecurring();
        }

        String cardNumber = borgunRequestVO.getCardDetailsVO().getCardNum();
        String first_six = cardNumber.substring(0, 6);
        String last_four = cardNumber.substring((cardNumber.length() - 4), cardNumber.length());

        GetAuthorization getAuthorization = new GetAuthorization();

        getAuthorization.setVersion(version);
        getAuthorization.setProcessor(processor);
        getAuthorization.setMerchantID(borgunRequestVO.getCommMerchantVO().getMerchantUsername());
        getAuthorization.setTerminalID(terminalId);
        getAuthorization.setTransType(saleTransType);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String amount = borgunRequestVO.getTransDetailsVO().getAmount();
        String newAmount = getAmount(amount, borgunRequestVO.getTransDetailsVO().getCurrency());
        getAuthorization.setTrAmount(newAmount);

        //        String currency = borgunRequestVO.getTransDetailsVO().getCurrency();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(borgunRequestVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);

        BorgunUtills borgunUtills = new BorgunUtills();
        Hashtable hash = borgunUtills.getBorgunAcoountDetails(accountId);
        String displayName = account.getDisplayName();

        transactionLogger.error("Descriptor Flag---"+account.getIsDynamicDescriptor());
        String merchantHome = (String) hash.get("MerchantHome");
        String merchantCity = (String) hash.get("MerchantCity");
        String merchantZip = (String) hash.get("MerchantZipCode");
        String merchantCountry = (String) hash.get("MerchantCountry");
        String eCommerce = (String) hash.get("Ecommerce");
        String eCommercePhone = (String) hash.get("EcommercePhone");
        if (account.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
        {
            displayName += borgunRequestVO.getTransDetailsVO().getOrderId();
            if (!displayName.equals("") && displayName.length()!=0 && displayName.length() > 21)
            {
                displayName = displayName.substring(0,21);
            }
            getAuthorization.setMerchantName(displayName);
            if (!merchantHome.equals("") && merchantHome.length()!=0 && merchantHome.length() > 19)
            {
                merchantHome = merchantHome.substring(0,19);
            }
            getAuthorization.setMerchantHome(merchantHome);

            if (!merchantCity.equals("") && merchantCity.length()!=0 && merchantCity.length() > 12)
            {
                merchantCity = merchantCity.substring(0,12);
            }
            getAuthorization.setMerchantCity(merchantCity);

            if (!merchantZip.equals("") && merchantZip.length()!=0 && merchantZip.length() > 9)
            {
                merchantZip = merchantZip.substring(0,9);
            }
            getAuthorization.setMerchantZipCode(merchantZip);

            if (!merchantCountry.equals("") && merchantCountry.length()!=0 && merchantCountry.length() > 2)
            {
                merchantCountry = merchantCountry.substring(0,2);
            }
            getAuthorization.setMerchantCountry(merchantCountry);

            if (!eCommerce.equals("") && eCommerce.length()!=0 && eCommerce.length() > 1)
            {
                eCommerce = eCommerce.substring(0,1);
            }
            getAuthorization.setEcommerce(eCommerce);

            if (!eCommercePhone.equals("") && eCommercePhone.length()!=0 && eCommercePhone.length() > 12)
            {
                eCommercePhone = eCommercePhone.substring(0,12);
            }
            getAuthorization.setEcommercePhone(eCommercePhone);
        }
        //getAuthorization.setTrCurrency(840);
        getAuthorization.setTrCurrency(currencyCode);

        getAuthorization.setCurrency(currencyCode.toString());
        date = getDate();

        getAuthorization.setDateAndTime(date);
        getAuthorization.setPan(borgunRequestVO.getCardDetailsVO().getCardNum());
        String year = borgunRequestVO.getCardDetailsVO().getExpYear();
        String onlyYear = year.substring(2);
        getAuthorization.setExpDate(onlyYear + borgunRequestVO.getCardDetailsVO().getExpMonth());
        getAuthorization.setCvc2(borgunRequestVO.getCardDetailsVO().getcVV());

        String rrn = getRRN(trackingID);

        getAuthorization.setRrn(rrn);

        if ("Y".equalsIgnoreCase(isManualRecurring))
        {
            getAuthorization.setReturnRecurrentTicket("true");
        }

        //todo gatewayCall
        //Date date104=new Date();
        /*transactionLogger.debug("BorgunPaymentGateway gatewayCall start time 104########"+date104.getTime());
                response = make_request(getAuthorization);
        transactionLogger.debug("BorgunPaymentGateway gatewayCall end time 104########" + new Date().getTime());
        transactionLogger.debug("BorgunPaymentGateway gatewayCall diff time 104########"+(new Date().getTime()-date104.getTime()));
*/
        response = make_request(getAuthorization);
        transactionLogger.error("response---" + response);
        if (!response.equalsIgnoreCase("ConnectionError"))
        {
            getAuthorizationReply = get_GetAuthorizationReply(response);
            actionCode = getAuthorizationReply.getActionCode();
            if (actionCode.equalsIgnoreCase("000"))
            {
                authCode = getAuthorizationReply.getAuthCode();
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(authCode);
                commResponseVO.setDescription("Transaction Successful");
                commResponseVO.setDescriptor(displayName);
                commResponseVO.setResponseTime(getAuthorizationReply.getDateAndTime());
                rbId = getAuthorizationReply.getRecurrentTicket();
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionId(authCode);
                commResponseVO.setErrorCode(actionCode);
                commResponseVO.setDescription(BorgunErrorCodeClass.getErrorStringFromCode(actionCode));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }
            int res = insertData(getAuthorizationReply, trackingID, "sale", 1);
            if ("Y".equalsIgnoreCase(isManualRecurring))
            {
                RecurringManager recurringManager = new RecurringManager();
                recurringManager.updateSubscriptionAfterBankCall(rbId, first_six, last_four, trackingID, authCode);
            }
        }
        else
        {
            //log.error("Borgun: exception : process sale : ",e) ;
            commResponseVO.setDescription("ConnectionError");
            commResponseVO.setErrorCode("0");
            commResponseVO.setStatus("fail");
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside Borgun processRefund---");
        String response, date, rrn;
        GetAuthorizationReply getAuthorizationReply;
        CommResponseVO commResponseVO = new CommResponseVO();
        BorgunUtills borgunUtills = new BorgunUtills();
        Functions functions = new Functions();

        BorgunRequestVO borgunRequestVO = (BorgunRequestVO) requestVO;
        GetAuthorization getAuthorization = new GetAuthorization();

        getAuthorization.setVersion(version);
        getAuthorization.setProcessor(processor);
        getAuthorization.setMerchantID(borgunRequestVO.getCommMerchantVO().getMerchantId());
        getAuthorization.setTerminalID(terminalId);
        getAuthorization.setTransType(refundTransType);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String amount = borgunRequestVO.getTransDetailsVO().getAmount();
        getAuthorization.setTrAmount(getAmount(amount, borgunRequestVO.getTransDetailsVO().getCurrency()));
        Hashtable hash = borgunUtills.getBorgunAcoountDetails(accountId);
        String merchantHome = (String) hash.get("MerchantHome");
        String merchantCity = (String) hash.get("MerchantCity");
        String merchantZip = (String) hash.get("MerchantZipCode");
        String merchantCountry = (String) hash.get("MerchantCountry");
        String eCommerce = (String) hash.get("Ecommerce");
        String eCommercePhone = (String) hash.get("EcommercePhone");
        String displayName = account.getDisplayName();
        if (account.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
        {
            displayName += borgunRequestVO.getTransDetailsVO().getOrderId();
            if (!displayName.equals("") && displayName.length()!=0 && displayName.length() > 21)
            {
                displayName = displayName.substring(0,21);
            }
            getAuthorization.setMerchantName(displayName);
            if (!merchantHome.equals("") && merchantHome.length()!=0 && merchantHome.length() > 19)
            {
                merchantHome = merchantHome.substring(0,19);
            }
            getAuthorization.setMerchantHome(merchantHome);

            if (!merchantCity.equals("") && merchantCity.length()!=0 && merchantCity.length() > 12)
            {
                merchantCity = merchantCity.substring(0,12);
            }
            getAuthorization.setMerchantCity(merchantCity);

            if (!merchantZip.equals("") && merchantZip.length()!=0 && merchantZip.length() > 9)
            {
                merchantZip = merchantZip.substring(0,9);
            }
            getAuthorization.setMerchantZipCode(merchantZip);

            if (!merchantCountry.equals("") && merchantCountry.length()!=0 && merchantCountry.length() > 2)
            {
                merchantCountry = merchantCountry.substring(0,2);
            }
            getAuthorization.setMerchantCountry(merchantCountry);

            if (!eCommerce.equals("") && eCommerce.length()!=0 && eCommerce.length() > 1)
            {
                eCommerce = eCommerce.substring(0,1);
            }
            getAuthorization.setEcommerce(eCommerce);

            if (!eCommercePhone.equals("") && eCommercePhone.length()!=0 && eCommercePhone.length() > 12)
            {
                eCommercePhone = eCommercePhone.substring(0,12);
            }
            getAuthorization.setEcommercePhone(eCommercePhone);
        }

        //getAuthorization.setCurrency(borgunRequestVO.getTransDetailsVO().getCurrency());
        //String currency = borgunRequestVO.getTransDetailsVO().getCurrency();
        //Integer currencyCode = get_currency_code(currency);
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(borgunRequestVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);

        getAuthorization.setTrCurrency(currencyCode);
        //after verifying Document only currency code is not required.
        //getAuthorization.setCurrency(currencyCode.toString());

        Hashtable<String, String> data = new Hashtable<String, String>();
        data = getData(trackingID, "refund");
        date = data.get("dateAndTime");
        rrn = data.get("rrn");
        String transaction = data.get("transaction");
        String batchId = data.get("batchId");
        transactionLogger.debug("data---"+data);

        getAuthorization.setDateAndTime(date);
        getAuthorization.setAuthCode(borgunRequestVO.getTransDetailsVO().getPreviousTransactionId());
        getAuthorization.setRrn(rrn);
        //after verifying Document only batchId and transaction are not required.
        //getAuthorization.setBatch(batchId);
        //getAuthorization.setTransaction(transaction);

        response = make_request(getAuthorization);
        transactionLogger.error("response---" + response);

        if (!response.equalsIgnoreCase("ConnectionError"))
        {
            getAuthorizationReply = get_GetAuthorizationReply(response);
            String actionCode = getAuthorizationReply.getActionCode().toString();

            if (getAuthorizationReply.getActionCode().equalsIgnoreCase("000"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(getAuthorizationReply.getAuthCode());
                commResponseVO.setDescription("Success");
                commResponseVO.setDescriptor(account.getDisplayName());
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionId(getAuthorizationReply.getAuthCode());
                commResponseVO.setErrorCode(getAuthorizationReply.getActionCode());
                commResponseVO.setDescription(BorgunErrorCodeClass.getErrorStringFromCode(actionCode));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }

            int res = insertData(getAuthorizationReply,trackingID,"refund",1);
        }
        else
        {
            commResponseVO.setDescription("ConnectionError");
            commResponseVO.setErrorCode("0");
            commResponseVO.setStatus("fail");
            //commResponseVO.setStatus("");
        }

        return commResponseVO;

    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String response, date, rrn, authCode, transaction;
        String batchId;
        GetAuthorizationReply getAuthorizationReply;

        CommResponseVO commResponseVO = new CommResponseVO();
        BorgunUtills borgunUtills = new BorgunUtills();
        Functions functions = new Functions();

        BorgunRequestVO borgunRequestVO = (BorgunRequestVO) requestVO;
        GetAuthorization getAuthorization = new GetAuthorization();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        getAuthorization.setVersion(version);
        getAuthorization.setProcessor(processor);
        getAuthorization.setMerchantID(borgunRequestVO.getCommMerchantVO().getMerchantId());
        getAuthorization.setTerminalID(terminalId);
        getAuthorization.setTransType(captureTransType);
        //getAuthorization.setTrCurrency(840);
        Hashtable hash = borgunUtills.getBorgunAcoountDetails(accountId);
        String merchantHome = (String) hash.get("MerchantHome");
        String merchantCity = (String) hash.get("MerchantCity");
        String merchantZip = (String) hash.get("MerchantZipCode");
        String merchantCountry = (String) hash.get("MerchantCountry");
        String eCommerce = (String) hash.get("Ecommerce");
        String eCommercePhone = (String) hash.get("EcommercePhone");
        String displayName = account.getDisplayName();
        if (account.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
        {
            displayName += borgunRequestVO.getTransDetailsVO().getOrderId();
            if (!displayName.equals("") && displayName.length()!=0 && displayName.length() > 21)
            {
                displayName = displayName.substring(0,21);
            }
            getAuthorization.setMerchantName(displayName);
            if (!merchantHome.equals("") && merchantHome.length()!=0 && merchantHome.length() > 19)
            {
                merchantHome = merchantHome.substring(0,19);
            }
            getAuthorization.setMerchantHome(merchantHome);

            if (!merchantCity.equals("") && merchantCity.length()!=0 && merchantCity.length() > 12)
            {
                merchantCity = merchantCity.substring(0,12);
            }
            getAuthorization.setMerchantCity(merchantCity);

            if (!merchantZip.equals("") && merchantZip.length()!=0 && merchantZip.length() > 9)
            {
                merchantZip = merchantZip.substring(0,9);
            }
            getAuthorization.setMerchantZipCode(merchantZip);

            if (!merchantCountry.equals("") && merchantCountry.length()!=0 && merchantCountry.length() > 2)
            {
                merchantCountry = merchantCountry.substring(0,2);
            }
            getAuthorization.setMerchantCountry(merchantCountry);

            if (!eCommerce.equals("") && eCommerce.length()!=0 && eCommerce.length() > 1)
            {
                eCommerce = eCommerce.substring(0,1);
            }
            getAuthorization.setEcommerce(eCommerce);

            if (!eCommercePhone.equals("") && eCommercePhone.length()!=0 && eCommercePhone.length() > 12)
            {
                eCommercePhone = eCommercePhone.substring(0,12);
            }
            getAuthorization.setEcommercePhone(eCommercePhone);
        }
        String amount = borgunRequestVO.getTransDetailsVO().getAmount();
        getAuthorization.setTrAmount(getAmount(amount, borgunRequestVO.getTransDetailsVO().getCurrency()));

        //         String currency = borgunRequestVO.getTransDetailsVO().getCurrency();
        //       Integer currencyCode = get_currency_code(currency);

        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(borgunRequestVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);

        getAuthorization.setTrCurrency(currencyCode);
        getAuthorization.setCurrency(currencyCode.toString());

        //            getAuthorization.setCurrency("840");

        getAuthorization.setAuthCode(borgunRequestVO.getTransDetailsVO().getPreviousTransactionId());

        Hashtable<String, String> data = new Hashtable<String, String>();
        data = getData(trackingID, "auth");

        date = data.get("dateAndTime");
        rrn = data.get("rrn");
        authCode = data.get("authCode");
        transaction = data.get("transaction");
        batchId = data.get("batchId");


        getAuthorization.setDateAndTime(date);
        getAuthorization.setRrn(rrn);
        getAuthorization.setBatch(batchId);
        getAuthorization.setTransaction(transaction);

        response = make_request(getAuthorization);
        transactionLogger.error("response---" + response);

        if (!response.equalsIgnoreCase("ConnectionError"))
        {

            getAuthorizationReply = get_GetAuthorizationReply(response);
            String actionCode = getAuthorizationReply.getActionCode();

            if (getAuthorizationReply.getActionCode().equalsIgnoreCase("000"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(getAuthorizationReply.getAuthCode());
                commResponseVO.setDescription("Success");
                commResponseVO.setDescriptor(account.getDisplayName());
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionId(getAuthorizationReply.getAuthCode());
                commResponseVO.setErrorCode(getAuthorizationReply.getActionCode());
                commResponseVO.setDescription(BorgunErrorCodeClass.getErrorStringFromCode(actionCode));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }

            int res = insertData(getAuthorizationReply, trackingID, "capture", 1);
        }
        else
        {
            commResponseVO.setDescription("ConnectionError");
            commResponseVO.setErrorCode("0");
            //commResponseVO.setStatus("fail");
            //log.error("Borgun: exception : process capture : ",e) ;
        }


        return commResponseVO;

    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        BorgunResponseVO commResponseVO = new BorgunResponseVO();
        InquiryAuthorization inquiryAuthorization = new InquiryAuthorization();
        Functions functions = new Functions();

        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        String trackingID=commTransactionDetailsVO.getOrderId();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        inquiryAuthorization.setVersion(version);
        inquiryAuthorization.setProcessor(String.valueOf(processor));
        inquiryAuthorization.setMerchantID(account.getMerchantId());
        inquiryAuthorization.setTerminalID(terminalId);
        inquiryAuthorization.setBatchNumber("");
        inquiryAuthorization.setFromDate("");
        inquiryAuthorization.setToDate("");
        inquiryAuthorization.setRrn(getRRN(trackingID));

        String response = make_inquiry_request(inquiryAuthorization);
        transactionLogger.error("Inquiry Response----" + response);

        String merchantOrderId="";
        String transactionId="";
        String authCode="";
        String transactionStatus="";
        String transactionType="";
        String amount="";
        String currency="";
        String remark="";
        String transactionDate="";
        String status="";

        if(response==null)
        {
            status="fail";
            remark="Transaction not found";
        }
        else
        {
            Map<String, InquiryTransaction> readResponse = BorgunUtills.ReadInquiryResponseNew(StringUtils.trim(response));
            if (readResponse != null && readResponse.size() > 0)
            {
                status="success";
                commResponseVO.setListMap(readResponse);
                if(commResponseVO.getListMap()!=null){
                    if(commResponseVO.getListMap().get("auth")!=null){
                        InquiryTransaction inquiryTransaction=commResponseVO.getListMap().get("auth");
                        transactionType="Auth";
                        if(commResponseVO.getListMap().get("auth").getActionCode().equalsIgnoreCase("000")){
                            transactionStatus="Successful";
                            remark="Transaction Successful";
                        }
                        else{
                            transactionStatus="Failed";
                            remark="Transaction Failed("+BorgunErrorCodeClass.getErrorStringFromCode(inquiryTransaction.getActionCode())+")";
                        }

                        merchantOrderId=inquiryTransaction.getRrn();
                        transactionId=inquiryTransaction.getTransactionNumber();
                        authCode=inquiryTransaction.getAuthorizationCode();
                        amount=inquiryTransaction.getTrAmount();
                        currency=CurrencyCodeISO4217.getAlphaCurrencyCode(inquiryTransaction.getTrCurrency());
                        transactionDate=inquiryTransaction.getTransactionDate();

                    }
                    if(commResponseVO.getListMap().get("sale")!=null){
                        InquiryTransaction inquiryTransaction=commResponseVO.getListMap().get("sale");
                        transactionType="Sale";
                        if(commResponseVO.getListMap().get("sale").getActionCode().equalsIgnoreCase("000")){
                            transactionStatus="Successful";
                            remark="Transaction Successful";
                        }
                        else{
                            transactionStatus="Failed";
                            remark="Transaction Failed("+BorgunErrorCodeClass.getErrorStringFromCode(inquiryTransaction.getActionCode())+")";
                        }

                        merchantOrderId=inquiryTransaction.getRrn();
                        transactionId=inquiryTransaction.getTransactionNumber();
                        authCode=inquiryTransaction.getAuthorizationCode();
                        amount=inquiryTransaction.getTrAmount();
                        currency=CurrencyCodeISO4217.getAlphaCurrencyCode(inquiryTransaction.getTrCurrency());
                        transactionDate=inquiryTransaction.getTransactionDate();
                    }
                }
                if(commResponseVO.getListMap().get("refund")!=null){
                    transactionType="Refund";
                }
            }
            else{
                status="fail";
                remark="Transaction not found";
                transactionStatus="N/A";
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        commResponseVO.setMerchantId(account.getMerchantId());
        commResponseVO.setMerchantOrderId(merchantOrderId);
        commResponseVO.setTransactionId(transactionId);
        commResponseVO.setAuthCode(authCode);
        commResponseVO.setTransactionStatus(transactionStatus);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setAmount(amount);
        commResponseVO.setCurrency(currency);
        commResponseVO.setBankTransactionDate(transactionDate);
        commResponseVO.setDescription(remark);
        commResponseVO.setRemark(remark);
        return commResponseVO;
    }
    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        String response,date,rrn,transaction,batch;
        CommResponseVO commResponseVO = new CommResponseVO();
        CancelAuthorizationReply cancelAuthorizationReply = new CancelAuthorizationReply();


        BorgunRequestVO borgunRequestVO = (BorgunRequestVO) requestVO;
        Functions functions = new Functions();

        CancelAuthorization cancelAuthorization = new CancelAuthorization();
        cancelAuthorization.setVersion(version);
        cancelAuthorization.setProcessor(processor);
        cancelAuthorization.setMerchantID(borgunRequestVO.getCommMerchantVO().getMerchantId());
        cancelAuthorization.setTerminalID(terminalId);
        cancelAuthorization.setTransType(voidTransType);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String amount =  borgunRequestVO.getTransDetailsVO().getAmount();
        cancelAuthorization.setTrAmount(getAmount(amount, borgunRequestVO.getTransDetailsVO().getCurrency()));

        //        String currency = borgunRequestVO.getTransDetailsVO().getCurrency();
        //        Integer currencyCode = get_currency_code(currency);

        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(borgunRequestVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);

        cancelAuthorization.setTrCurrency(currencyCode);

        Hashtable<String,String> data = new Hashtable<String,String>();
        data = getData(trackingID,"auth");

        date = data.get("dateAndTime");

        date =  data.get("dateAndTime");
        rrn = data.get("rrn");
        transaction = data.get("transaction");
        batch = data.get("batchId");


        cancelAuthorization.setDateAndTime(date);
        cancelAuthorization.setAuthCode(borgunRequestVO.getTransDetailsVO().getPreviousTransactionId());
        cancelAuthorization.setRrn(rrn);
        cancelAuthorization.setTransType(voidTransType);
        cancelAuthorization.setBatch(batch);
        cancelAuthorization.setTransaction(transaction);

        response = make_void_request(cancelAuthorization);
        transactionLogger.error("response---" + response);

        if(!response.equalsIgnoreCase("ConnectionError"))
        {

            cancelAuthorizationReply = get_CancelAuthorizationReply(response);
            String actionCode = cancelAuthorizationReply.getActionCode();

            if(cancelAuthorizationReply.getActionCode().equalsIgnoreCase("000"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(cancelAuthorizationReply.getAuthCode());
                commResponseVO.setDescription("Success");
                commResponseVO.setDescriptor(account.getDisplayName());
                if (functions.isValueNull(cancelAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(cancelAuthorizationReply.getMessage());

            }else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionId(cancelAuthorizationReply.getAuthCode());
                commResponseVO.setErrorCode(cancelAuthorizationReply.getActionCode());
                commResponseVO.setDescription(BorgunErrorCodeClass.getErrorStringFromCode(actionCode));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                if (functions.isValueNull(cancelAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(cancelAuthorizationReply.getMessage());
            }
            int res = insertVoidData(cancelAuthorizationReply,cancelAuthorization,trackingID,"void","1");
        }else
        {
            commResponseVO.setDescription("ConnectionError");
            commResponseVO.setErrorCode("0");
            //     commResponseVO.setStatus("fail");
        }


        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String response, actionCode, date, authCode = "";
        Functions functions = new Functions();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();

        GetAuthorizationReply getAuthorizationReply;
        CommResponseVO commResponseVO = new CommResponseVO();

        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String amount = genericTransDetailsVO.getAmount();
        String newAmount = getAmount(amount, account.getCurrency());

        String cardNum = PzEncryptor.decryptPAN(commCardDetailsVO.getCardNum());
        String EXP = PzEncryptor.decryptExpiryDate(commCardDetailsVO.getExpMonth());
        String dateArr[] = EXP.split("/");
        String expMonth = dateArr[0];
        String year = dateArr[1].substring(0, 4);
        String onlyYear = year.substring(2);
        date = getDate();

        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(account.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);


        GetAuthorization getAuthorization = new GetAuthorization();

        getAuthorization.setVersion(version);
        getAuthorization.setProcessor(processor);
        getAuthorization.setMerchantID(account.getMerchantId());
        getAuthorization.setTerminalID(recurringTerminalId);
        getAuthorization.setTransType(saleTransType);

        getAuthorization.setTrCurrency(currencyCode);
        getAuthorization.setCurrency(currencyCode.toString());
        getAuthorization.setTrAmount(newAmount);
        getAuthorization.setDateAndTime(date);

        getAuthorization.setPan(cardNum);
        getAuthorization.setExpDate(onlyYear + expMonth);
        String rrn = getRRN(trackingID);
        getAuthorization.setRrn(rrn);
        getAuthorization.setRecurrentTicket(recurringBillingVO.getRbid());

        response = make_request(getAuthorization);

        transactionLogger.error("-----rebilling response---" + response);
        if (!response.equalsIgnoreCase("ConnectionError"))
        {
            getAuthorizationReply = get_GetAuthorizationReply(response);
            actionCode = getAuthorizationReply.getActionCode();
            if (actionCode.equalsIgnoreCase("000"))
            {
                authCode = getAuthorizationReply.getAuthCode();
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(authCode);
                commResponseVO.setDescription("Transaction Successful");
                commResponseVO.setDescriptor(account.getDisplayName());
                commResponseVO.setResponseTime(getAuthorizationReply.getDateAndTime());
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionId(authCode);
                commResponseVO.setErrorCode(getAuthorizationReply.getAuthCode());
                commResponseVO.setDescription(BorgunErrorCodeClass.getErrorStringFromCode(actionCode));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                commResponseVO.setResponseTime(getAuthorizationReply.getDateAndTime());
                if (functions.isValueNull(getAuthorizationReply.getMessage()))
                    commResponseVO.setRemark(getAuthorizationReply.getMessage());
            }
            transactionLogger.error("authCode::::" + authCode);
            int res = insertData(getAuthorizationReply, trackingID, "sale", 1);
        }
        else
        {
            commResponseVO.setDescription("ConnectionError");
            commResponseVO.setErrorCode("0");
            commResponseVO.setStatus("fail");
        }
        return commResponseVO;
    }
    public String make_request(GetAuthorization getAuthorization) throws PZTechnicalViolationException
    {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("","com.payment.borgun.core");

        String xmldata = "";
        String response ="";

        StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding=\"utf-8\"?>");
        StringBuilder xmlOut = sb.append(System.getProperty("line.separator")).append(xstream.toXML(getAuthorization));
        xmldata = xmlOut.toString();
        transactionLogger.error("===borgun request===" + xmldata);

        try {

            Heimir_pub_ws_Authorization_BinderStub binding;
            binding = (Heimir_pub_ws_Authorization_BinderStub) new AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
            binding.setTimeout(60000);
            java.lang.String value = null;
            response = binding.getAuthorization(xmldata);
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BorgunPaymentGateway.class.getName(),"make_request()",null,"Common","SERVICE EXCEPTION while placing transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BorgunPaymentGateway.class.getName(),"make_request()",null,"Common","Remote EXCEPTION while placing transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return response;
    }

    public String make_void_request(CancelAuthorization cancelAuthorization) throws PZTechnicalViolationException
    {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("","com.payment.borgun.core");

        String xmldata = "";
        String response ="";

        StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding=\"utf-8\"?>");
        StringBuilder xmlOut  = new StringBuilder();
        xmldata = sb.append(System.getProperty("line.separator")).append(xstream.toXML(cancelAuthorization)).toString();

        try {
            Heimir_pub_ws_Authorization_BinderStub binding;
            binding = (Heimir_pub_ws_Authorization_BinderStub) new AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
            java.lang.String value = null;
            response = binding.cancelAuthorization(xmldata);
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BorgunPaymentGateway.class.getName(),"make_void_request()",null,"common","Service Exception while placing transaction",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BorgunPaymentGateway.class.getName(), "make_void_request()", null, "common", "Remote Exception while placing transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return response;
    }

    public String make_inquiry_request(InquiryAuthorization inquiryAuthorization) throws PZTechnicalViolationException
    {
        logger.debug("inside make_inquiry_request----");
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("","com.payment.borgun.core");

        String xmldata = "";
        String response ="";

        StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding=\"utf-8\"?>");
        StringBuilder xmlOut  = new StringBuilder();
        xmldata = sb.append(System.getProperty("line.separator")).append(xstream.toXML(inquiryAuthorization)).toString();
        transactionLogger.debug("borgun xml---"+xmldata);

        try {
            Heimir_pub_ws_Authorization_BinderStub binding;
            binding = (Heimir_pub_ws_Authorization_BinderStub) new AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
            java.lang.String value = null;
            response = binding.getTransactionList(xmldata);
            logger.debug("borgun inquiry response---" + response);
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BorgunPaymentGateway.class.getName(),"make_inquiry_request()",null,"common","Service Exception while placing transaction",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(BorgunPaymentGateway.class.getName(), "make_inquiry_request()", null, "common", "Remote Exception while placing transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return response;
    }

    public String getAmount(String amount, String sChCurrencyCode)
    {
        Double dObj2 = Double.valueOf(amount);
        if(sChCurrencyCode.equalsIgnoreCase("JPY"))
        {
            //do nothing
        }
        else
        {
            dObj2= dObj2 * 100;
        }

        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    public String getDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String dt = dateFormat.format(date);
        return dt;
    }

    public String getRRN(String trackingId)
    {
        String temp = "0000000";
        String rrn ;
        int defaultLength = 12;
        int length = trackingId.length();
        int totalLength = rrnSuffix.length() + length;
        int newLength = defaultLength - totalLength;

        if(newLength > 0)
            temp = temp.substring(0,newLength);
        else
            temp = "";

        String rrnSuffix = "PBG";
        rrn = rrnSuffix + temp.toString() + trackingId;

        return  rrn;
    }

    public int insertData(GetAuthorizationReply getAuthorizationReply, String trackingId, String method, int transType)
    {
        String rrn = "";
        String date = "";
        String authCode = "";
        String actionCode = "";

        String batchId ="0";
        String cardAccId ="fail";
        String transaction = "fail";
        String transAmount = "0";
        String storeTerminal = "0";
        String message = "NA";
        Functions functions = new Functions();

        actionCode = getAuthorizationReply.getActionCode();
        if(actionCode.equalsIgnoreCase("000"))
        {

            if (functions.isValueNull(getAuthorizationReply.getBatch()))
            batchId = getAuthorizationReply.getBatch();

            if (functions.isValueNull(getAuthorizationReply.getAuthCode()))
            authCode = getAuthorizationReply.getAuthCode();

            if (functions.isValueNull(getAuthorizationReply.getCardAccId()))
            cardAccId = getAuthorizationReply.getCardAccId();

            if (functions.isValueNull(getAuthorizationReply.getTransaction()))
            transaction = getAuthorizationReply.getTransaction();

            if (functions.isValueNull(getAuthorizationReply.getStoreTerminal()))
            storeTerminal = getAuthorizationReply.getStoreTerminal();

            if (functions.isValueNull(getAuthorizationReply.getTrAmount()))
            transAmount = getAuthorizationReply.getTrAmount();

            if (functions.isValueNull(getAuthorizationReply.getMessage()))
                message = getAuthorizationReply.getMessage();
        }
        else
        {
            authCode = "fail";

            if(getAuthorizationReply.getMessage() != null)
                message = getAuthorizationReply.getMessage();

            if(getAuthorizationReply.getBatch() != null)
                batchId  = getAuthorizationReply.getBatch();

            if(getAuthorizationReply.getTransaction() != null)
                transaction = getAuthorizationReply.getTransaction();

            if(getAuthorizationReply.getStoreTerminal() != null)
                storeTerminal = getAuthorizationReply.getStoreTerminal();

            if(getAuthorizationReply.getCardAccId() != null)
                cardAccId = getAuthorizationReply.getCardAccId();
        }

        date = getAuthorizationReply.getDateAndTime();
        transAmount = getAuthorizationReply.getTrAmount();
        rrn = getAuthorizationReply.getRrn();

        Connection conn = null;
        int result;
        try
        {
            String query = "insert into transaction_borgun_details (trackingId,batchId,authCode,transaction,rrn,actionCode,dateAndTime,method,cardAccId,transType,amount,storeTerminal,message) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            conn = Database.getConnection();
            PreparedStatement authstmt = conn.prepareStatement(query);
            authstmt.setString(1,trackingId);
            authstmt.setString(2,batchId);
            authstmt.setString(3,authCode);
            authstmt.setString(4,transaction);
            authstmt.setString(5,rrn);
            authstmt.setString(6,actionCode);
            authstmt.setString(7,date);
            authstmt.setString(8,method);
            authstmt.setString(9,cardAccId);
            authstmt.setInt(10,transType);
            authstmt.setString(11,transAmount);
            authstmt.setString(12,storeTerminal);
            authstmt.setString(13,message);
            result = authstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            transactionLogger.error("Error in insertData of BorgunPaymentGateway : ",e);
            result = 0;
            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunPaymentGateway.class.getName(),"insertData()",null,"common","Exception while inserting data into transaction_borgun_details", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),trackingId, PZOperations.TRANSACTION_BORGUN_DETAILS);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Error in insertData of BorgunPaymentGateway : ",systemError);
            result = 0;
            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunPaymentGateway.class.getName(),"insertData()",null,"common","Exception while inserting data into transaction_borgun_details", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),trackingId, PZOperations.TRANSACTION_BORGUN_DETAILS);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return result;
    }


    public Hashtable<String,String> getData(String trackingId,String method)
    {
        Hashtable<String,String> ht = new Hashtable<String,String>();
        Connection conn = null;
        String dateAndTime,rrn,query,transaction;
        String batchId;
        int result;
        try
        {
            conn = Database.getConnection();

            if(method.equalsIgnoreCase("refund"))
                query ="select * from transaction_borgun_details where trackingId='" + trackingId + "' and method in ('sale','capture') and actionCode='000'";
            else
                query ="select * from transaction_borgun_details where trackingId='" + trackingId + "' and method in ('auth') and actionCode='000'";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            transactionLogger.debug("RRN from BorgunDetails---"+stmt);
            transactionLogger.debug("RRN from BorgunDetails---"+query);
            if (rs.next())
            {
                dateAndTime = rs.getString("dateAndTime");
                rrn = rs.getString("rrn");
                batchId = rs.getString("batchId");
                transaction = rs.getString("transaction");
                ht.put("dateAndTime",dateAndTime);
                ht.put("rrn",rrn);
                ht.put("batchId",batchId.toString());
                ht.put("transaction",transaction);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in BorgunPaymentGateway : ",e );
            result = 0;

            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunPaymentGateway.class.getName(), "getData()", null, "common", "Exception while getting data from transaction_borgun_details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), trackingId, PZOperations.TRANSACTION_BORGUN_DETAILS);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return ht;
    }

    public Hashtable<String,String> getData(String trackingId)
    {
        logger.debug("Inside getData method......");

        Hashtable<String,String> ht = new Hashtable<String,String>();
        Connection conn = null;
        String dateAndTime,rrn,query,transaction;
        String batchId;
        int result;
        try
        {
            conn = Database.getConnection();

            query ="select * from transaction_borgun_details where trackingId='" + trackingId + "'";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            logger.debug("getData select query---"+stmt);
            if (rs.next())
            {
                dateAndTime = rs.getString("dateAndTime");
                rrn = rs.getString("rrn");
                batchId = rs.getString("batchId");
                transaction = rs.getString("transaction");
                ht.put("dateAndTime",dateAndTime);
                ht.put("rrn",rrn);
                ht.put("batchId",batchId.toString());
                ht.put("transaction",transaction);
            }
            else
            {
                ht.put("dateAndTime","0");
                ht.put("rrn","0");
                ht.put("batchId","0");
                ht.put("transaction","0");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in BorgunPaymentGateway : ",e );
            result = 0;
            logger.error("exception---",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunPaymentGateway.class.getName(), "getData()", null, "common", "Exception while getting data from transaction_borgun_details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), trackingId, PZOperations.TRANSACTION_BORGUN_DETAILS);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return ht;
    }
    public int insertVoidData(CancelAuthorizationReply cancelAuthorizationReply,CancelAuthorization cancelAuthorization,String trackingId,String method,String transType)
    {
        String rrn,date,authCode,transaction,actionCode,cardAccId,transAmount,storeTerminal,message;
        String batchId;
        batchId ="fail";
        cardAccId ="fail";
        transaction = "fail";
        transAmount = "0";
        storeTerminal = "0";
        authCode="fail";
        rrn ="fail";
        date ="fail";
        message = "fail";

        actionCode = cancelAuthorizationReply.getActionCode();
        if(actionCode.equalsIgnoreCase("000"))
        {
            message = "success";
            batchId = cancelAuthorizationReply.getBatch();
            authCode = cancelAuthorizationReply.getAuthCode();
            date = cancelAuthorizationReply.getDateAndTime();
            transAmount = cancelAuthorizationReply.getTrAmount();
            rrn = cancelAuthorizationReply.getRrn();
            cardAccId = "notinauth";
            if(cancelAuthorizationReply.getStoreTerminal() != null)
                storeTerminal = cancelAuthorizationReply.getStoreTerminal();

            transaction = cancelAuthorizationReply.getTransaction();
        }
        else
        {
            if(cancelAuthorizationReply.getMessage() != null)
                message = cancelAuthorizationReply.getMessage();

            if(cancelAuthorization.getDateAndTime() != null)
                date = cancelAuthorizationReply.getDateAndTime();

            if(cancelAuthorizationReply.getRrn() != null)
                rrn = cancelAuthorization.getRrn();

            if(cancelAuthorizationReply.getBatch() != null)
                batchId = cancelAuthorization.getBatch();

            if(cancelAuthorizationReply.getTransaction() != null)
                transaction = cancelAuthorization.getTransaction();

            if(cancelAuthorizationReply.getTrAmount() != null)
                transAmount = cancelAuthorization.getTrAmount();

        }

        Connection conn = null;
        int result;
        try
        {
            String query = "insert into transaction_borgun_details (trackingId,batchId,authCode,transaction,rrn,actionCode,dateAndTime,method,cardAccId,transType,amount,storeTerminal,message) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            conn = Database.getConnection();
            PreparedStatement authstmt = conn.prepareStatement(query);
            authstmt.setString(1,trackingId);
            authstmt.setString(2,batchId);
            authstmt.setString(3,authCode);
            authstmt.setString(4,transaction);
            authstmt.setString(5,rrn);
            authstmt.setString(6,actionCode);
            authstmt.setString(7,date);
            authstmt.setString(8,method);
            authstmt.setString(9,cardAccId);
            authstmt.setString(10,transType);
            authstmt.setString(11,transAmount);
            authstmt.setString(12,storeTerminal);
            authstmt.setString(13,message);
            result = authstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            transactionLogger.error("Error in insertVoidData of BorgunPaymentGateway : ",e);
            result = 0;

            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunPaymentGateway.class.getName(),"insertVoidData()",null,"common","Exception while getting data from transaction_borgun_details", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),trackingId, PZOperations.TRANSACTION_BORGUN_DETAILS);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Error in insertVoidData of BorgunPaymentGateway : ",systemError);
            result = 0;

            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunPaymentGateway.class.getName(),"insertVoidData()",null,"common","Exception while getting data from transaction_borgun_details", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),trackingId, PZOperations.TRANSACTION_BORGUN_DETAILS);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return  result;
    }



    public GetAuthorizationReply get_GetAuthorizationReply(String response)
    {
        GetAuthorizationReply getAuthorizationReply;
        try
        {
            XStream responseXstream = new XStream();
            responseXstream.autodetectAnnotations(true);
            responseXstream.aliasPackage("getAuthorizationReply","com.payment.borgun.core");
            responseXstream.alias("getAuthorizationReply", GetAuthorizationReply.class);
            getAuthorizationReply = (GetAuthorizationReply)responseXstream.fromXML(response);
        }catch (Exception e)
        {
            transactionLogger.error("Error in get_GetAuthorizationReply of BorgunPaymentGateway : ",e);
            getAuthorizationReply = new GetAuthorizationReply();
        }
        return  getAuthorizationReply;
    }


    public InquiryAuthorizationReply get_GetInquiryReply(String response)
    {
        InquiryAuthorizationReply getInquiryReply;
        try
        {
            XStream responseXstream = new XStream();
            responseXstream.autodetectAnnotations(true);
            responseXstream.aliasPackage("TransactionList","com.payment.borgun.core");
            responseXstream.alias("TransactionList", InquiryAuthorizationReply.class);
            getInquiryReply = (InquiryAuthorizationReply)responseXstream.fromXML(response);

        }catch (Exception e)
        {
            transactionLogger.error("Error in get_GetAuthorizationReply of BorgunPaymentGateway : ",e);
            getInquiryReply = new InquiryAuthorizationReply();
        }
        return  getInquiryReply;
    }

    public CancelAuthorizationReply get_CancelAuthorizationReply(String response)
    {
        CancelAuthorizationReply cancelAuthorizationReply;
        try
        {
            XStream responseXstream = new XStream();
            responseXstream.autodetectAnnotations(true);
            responseXstream.aliasPackage("cancelAuthorizationReply","com.payment.borgun.core");
            responseXstream.alias("cancelAuthorizationReply", CancelAuthorizationReply.class);
            cancelAuthorizationReply = (CancelAuthorizationReply)responseXstream.fromXML(response);

        }catch (Exception e)
        {
            transactionLogger.error("Error in get_CancelAuthorizationReply of BorgunPaymentGateway : ",e);
            cancelAuthorizationReply = new CancelAuthorizationReply();
        }

        return  cancelAuthorizationReply;

    }

}