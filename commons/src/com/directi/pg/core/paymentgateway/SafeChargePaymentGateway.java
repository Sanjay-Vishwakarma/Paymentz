package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.safecharge.SafeChargeUtils;
import com.directi.pg.core.valueObjects.*;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
/**
 * Created by IntelliJ IDEA.
 * User: uday
 * Date: 7/21/17
 * Time: 3.42:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafeChargePaymentGateway  extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "SafeCharge";
    public final static String ELE_TYPE = "sg_TransType";
    public final static String ELE_CURRENCY = "sg_Currency";
    public final static String ELE_AMOUNT = "sg_Amount";
    public final static String ELE_USERNAME = "sg_ClientLoginID";
    public final static String ELE_PASSWORD = "sg_ClientPassword";
    public final static String ELE_ORDERID= "sg_ClientUniqueID";
    public final static String ELE_FROMDATE= "sg_FromDate";
    public final static String ELE_RESPONSEFORMAT = "sg_ResponseFormat";
    public final static String ELE_FIRSTNAME = "sg_FirstName";
    public final static String ELE_LASTNAME = "sg_LastName";
    public final static String ELE_ADDRESS = "sg_Address";
    public final static String ELE_CITY = "sg_City";
    public final static String ELE_STATE = "sg_State";
    public final static String ELE_ZIP = "sg_Zip";
    public final static String ELE_COUNTRY = "sg_Country";
    public final static String ELE_PHONE = "sg_Phone";
    public final static String ELE_IPADDRESS = "sg_IPAddress";
    public final static String ELE_EMAIL = "sg_Email";
    public final static String ELE_NAMEonCARD = "sg_NameOnCard";
    public final static String ELE_CCNUMBER = "sg_CardNumber";
    public final static String ELE_EXPMONTH = "sg_ExpMonth";
    public final static String ELE_EXPYEAR = "sg_ExpYear";
    public final static String ELE_CVV = "sg_CVV2";
    public final static String ELE_TRANSACTIONID = "sg_TransactionID";
    public final static String ELE_AUTHCODE = "sg_AuthCode";
    public final static String ELE_WEbsite = "sg_WebSite";
    public final static String ELE_CREDITTYPE = "sg_CreditType";
    public final static String ELE_VERSION = "sg_Version";
    public final static String ELE_API_TYPE = "sg_APIType";
    public final static String ELE_PARes = "sg_PARes";
    public final static String SALE= "Sale";
    public final static String SALE_3D= "Sale3D";
    public final static String AUTH = "Auth";
    public final static String CAPTURE= "Settle";
    public final static String VOID = "Void";
    public final static String REFUND = "Credit";
    //config
    private final static String SafeCharge_URL_OLD = "https://process.safecharge.com/service.asmx/Process?";
    private final static String SafeCharge_URL = "https://process.sandbox.safecharge.com/service.asmx/Process?";
    private static TransactionLogger transactionLogger = new TransactionLogger(SafeChargePaymentGateway.class.getName());
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.safecharge");
    public SafeChargePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.safecharge");
       String query = "ClientUniqueID=52140&FromDate=2017-09-22 02:21:02&Password=NZk7BU1Qbw&Username=TPayQOD&Version=1.0.0";
        //System.out.println("query Request-----"+query);
        try
        {

            String response = SafeChargeUtils.doPostHTTPSURLConnectionForInquiry("https://test.safecharge.com/QODService/Service.asmx/GetSpecificTransactionByClientUniqueId", query);
            //System.out.println("response----"+response);

            response = response.replace("&lt;", "<")
                    .replace("&gt;", ">");

            //System.out.println("New REsponse"+response);

        }
        catch (Exception e)
        {
        }
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Entered in processSale()-----");
        SafeChargeResponseVO SafeChargeresVO=null;
        SafeChargeRequestVO SafeChargeVO = (SafeChargeRequestVO) requestVO;
        GenericTransDetailsVO genericTransactionDetailsVO =  SafeChargeVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = SafeChargeVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = SafeChargeVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=SafeChargeVO.getCommMerchantVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String _3DSupportedAccount=gatewayAccount.get_3DSupportAccount();
        boolean isTest=gatewayAccount.isTest();
        String addressValidation = gatewayAccount.getAddressValidation();
        Functions functions=new Functions();

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }


        validateForSale(trackingID, requestVO,addressValidation);
        Map<String, String> authMap = new TreeMap();
        Map<String, String> authMapLog = new TreeMap();
        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        authMap.put(ELE_NAMEonCARD,name);
        authMapLog.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMapLog.put(ELE_CCNUMBER,functions.maskingPan(genericCardDetailsVO.getCardNum()));
        authMap.put(ELE_EXPMONTH,genericCardDetailsVO.getExpMonth());
        authMapLog.put(ELE_EXPMONTH,functions.maskingNumber(genericCardDetailsVO.getExpMonth()));
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMapLog.put(ELE_EXPYEAR,functions.maskingNumber(genericCardDetailsVO.getExpYear().substring(2)));
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMapLog.put(ELE_CVV,functions.maskingNumber(genericCardDetailsVO.getcVV()));
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMapLog.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMapLog.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMapLog.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMapLog.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_ORDERID,trackingID);
        authMapLog.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMapLog.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMapLog.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMapLog.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMapLog.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMapLog.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMapLog.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMapLog.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMapLog.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMapLog.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMapLog.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMapLog.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMapLog.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
        authMapLog.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
        if("Y".equals(_3DSupportedAccount)){
            authMap.put(ELE_TYPE,SALE_3D);
            authMapLog.put(ELE_TYPE,SALE_3D);
            authMap.put(ELE_API_TYPE,"1");
            authMapLog.put(ELE_API_TYPE,"1");
        }else{
            authMap.put(ELE_TYPE,SALE);
            authMapLog.put(ELE_TYPE,SALE);
        }

        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');
        String cardParametersLogs = SafeChargeUtils.joinMapValue(authMapLog, '&');

        transactionLogger.error("-----sale request--"+trackingID+"---" + cardParametersLogs);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::" + RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), cardParameters);
        }else {
            transactionLogger.error("URL:::::" + RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), cardParameters);
        }
        transactionLogger.error("-----sale response--"+trackingID+"---" + response);

        SafeChargeresVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(SafeChargeresVO!=null && !SafeChargeresVO.equals(""))
        {
            transactionLogger.error("flow---"+SafeChargeresVO.getTransactionStatus());
            transactionLogger.error("flow 3D value---"+SafeChargeresVO.getThreeDFlow());
            transactionLogger.error("flow erorr code---"+SafeChargeresVO.getErrorCode());

            String status = "fail";
            if("APPROVED".equals(SafeChargeresVO.getTransactionStatus()) && "0".equals(SafeChargeresVO.getErrorCode()) && "1".equals(SafeChargeresVO.getThreeDFlow()) && functions.isValueNull(SafeChargeresVO.getACSUrl()) && functions.isValueNull(SafeChargeresVO.getPaReq()) && functions.isValueNull(SafeChargeresVO.getMerchantID()))
            {
                transactionLogger.error("3Ds flow");
                status = "pending3DConfirmation";
                SafeChargeresVO.setRemark("Transaction Successful");
                transactionLogger.debug("transactionId-----"+SafeChargeresVO.getTransactionId());
               /* PaymentManager paymentManager=new PaymentManager();
                paymentManager.updatePaymentIdForCommon(SafeChargeresVO, trackingID);*/
                SafeChargeResponseVO comm3DResponseVO=new SafeChargeResponseVO();
                comm3DResponseVO.setPaReq(SafeChargeresVO.getPaReq());
                comm3DResponseVO.setUrlFor3DRedirect(SafeChargeresVO.getACSUrl());
                //comm3DResponseVO.setUrlFor3DRedirect(RB.getString("TERM_URL")+trackingID+"@"+genericCardDetailsVO.getcVV());
                comm3DResponseVO.setMd(trackingID +"@"+genericCardDetailsVO.getcVV());
                comm3DResponseVO.setRedirectMethod("POST");
                comm3DResponseVO.setTerURL(termUrl+trackingID);
                comm3DResponseVO.setStatus(status);
                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                comm3DResponseVO.setTransactionType(SALE);
                comm3DResponseVO.setTransactionId(SafeChargeresVO.getTransactionId());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                comm3DResponseVO.setCurrency(genericTransactionDetailsVO.getCurrency());
                comm3DResponseVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
                comm3DResponseVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
                return comm3DResponseVO;
            }
            else if("APPROVED".equals(SafeChargeresVO.getTransactionStatus()) && "0".equals(SafeChargeresVO.getErrorCode()))
            {
                transactionLogger.error("Non 3Ds flow");
                status = "success";
                SafeChargeresVO.setStatus(status);
                SafeChargeresVO.setRemark("Transaction Successful");
                SafeChargeresVO.setDescription("Transaction Successful");
                SafeChargeresVO.setDescriptor(gatewayAccount.getDisplayName());
                SafeChargeresVO.setTransactionType(SALE);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                SafeChargeresVO.setCurrency(genericTransactionDetailsVO.getCurrency());
                SafeChargeresVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
                SafeChargeresVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
                return SafeChargeresVO;
            }
            /*else if("APPROVED".equals(SafeChargeresVO.getTransactionStatus()) && SafeChargeresVO.getErrorCode().equals("0") && "1".equals(SafeChargeresVO.getThreeDFlow()) && SafeChargeresVO.getACSUrl()!=null){*/

            else
            {
                transactionLogger.error("Non flow error");
                SafeChargeresVO.setStatus(status);
                SafeChargeresVO.setRemark("Transaction Failed");
                SafeChargeresVO.setDescription("Transaction Failed");
                SafeChargeresVO.setDescriptor(gatewayAccount.getDisplayName());
                SafeChargeresVO.setTransactionType(SALE);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                SafeChargeresVO.setCurrency(genericTransactionDetailsVO.getCurrency());
                SafeChargeresVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
                SafeChargeresVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
                return SafeChargeresVO;
            }
        }
        return null;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Entered in processAuth()-----");
        SafeChargeResponseVO SafeChargeresVO=null;
        SafeChargeRequestVO SafeChargeVO = (SafeChargeRequestVO) requestVO;
        Functions functions=new Functions();
        GenericTransDetailsVO genericTransactionDetailsVO =  SafeChargeVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = SafeChargeVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = SafeChargeVO.getCardDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String isAddressDetails = gatewayAccount.getAddressValidation();

        validateForSale(trackingID,requestVO,isAddressDetails);
        Map<String, String> authMap = new TreeMap();
        Map<String, String> authMapLog = new TreeMap();
        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        authMap.put(ELE_NAMEonCARD,name);
        authMapLog.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMapLog.put(ELE_CCNUMBER, functions.maskingPan(genericCardDetailsVO.getCardNum()));
        authMap.put(ELE_EXPMONTH, genericCardDetailsVO.getExpMonth());
        authMapLog.put(ELE_EXPMONTH,functions.maskingNumber(genericCardDetailsVO.getExpMonth()));
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMapLog.put(ELE_EXPYEAR,functions.maskingNumber(genericCardDetailsVO.getExpYear().substring(2)));
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMapLog.put(ELE_CVV,functions.maskingNumber(genericCardDetailsVO.getcVV()));
        authMap.put(ELE_TYPE,AUTH);
        authMapLog.put(ELE_TYPE,AUTH);
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMapLog.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMapLog.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMapLog.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMapLog.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_ORDERID,trackingID);
        authMapLog.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMapLog.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMapLog.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMapLog.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMapLog.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMapLog.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMapLog.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMapLog.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMapLog.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMapLog.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMapLog.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMapLog.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMapLog.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
        authMapLog.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());

        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');
        String cardParametersLog = SafeChargeUtils.joinMapValue(authMapLog, '&');
        transactionLogger.error("-----auth request---"+trackingID+"--" + cardParametersLog);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::" + RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), cardParameters);
        }else{
            transactionLogger.error("URL:::::" + RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), cardParameters);
        }
        transactionLogger.error("-----auth response---"+trackingID+"--" + response);

        SafeChargeresVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(SafeChargeresVO!=null && !SafeChargeresVO.equals("")){
            String status = "fail";
            String remark="Transaction Failed";
            if(SafeChargeresVO.getErrorCode().equals("0")){
                status = "success";
                remark="Transaction Successful";
            }
            SafeChargeresVO.setStatus(status);
            SafeChargeresVO.setRemark(remark);
            SafeChargeresVO.setDescription(remark);
            SafeChargeresVO.setDescriptor(GATEWAY_TYPE);
            SafeChargeresVO.setTransactionType(AUTH);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            SafeChargeresVO.setCurrency(genericTransactionDetailsVO.getCurrency());
            SafeChargeresVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
            SafeChargeresVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
        }
        return SafeChargeresVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Entered in processRefund()-----");
        SafeChargeResponseVO SafeChargeresVO=null;
        SafeChargeRequestVO SafeChargeVO = (SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO genericAddressDetailsVO = SafeChargeVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = SafeChargeVO.getCardDetailsVO();
        CommTransactionDetailsVO genericTransactionDetailsVO =  SafeChargeVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        Map<String, String> authMap = new TreeMap();
        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        authMap.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_EXPMONTH,genericCardDetailsVO.getExpMonth());
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMap.put(ELE_TYPE,REFUND);
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_AUTHCODE,SafeChargeVO.getAuthCode());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_TRANSACTIONID, genericTransactionDetailsVO.getPreviousTransactionId());
        authMap.put(ELE_CREDITTYPE,"2");
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());

        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');
        transactionLogger.error("-----refund request-----" + cardParameters);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::" + RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), cardParameters);
        }else {
            transactionLogger.error("URL:::::" + RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), cardParameters);
        }
        transactionLogger.error("-----refund response-----" + response);

        SafeChargeresVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(SafeChargeresVO!=null && !SafeChargeresVO.equals("")){
            String status = "fail";
            String remark="Transaction Failed";
            if(SafeChargeresVO.getErrorCode().equals("0")){
                status = "success";
                remark="Transaction Successful";
            }
            SafeChargeresVO.setStatus(status);
            SafeChargeresVO.setRemark(remark);
            SafeChargeresVO.setDescription(remark);
            SafeChargeresVO.setDescriptor(GATEWAY_TYPE);
            SafeChargeresVO.setTransactionType(REFUND);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        return SafeChargeresVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Entered in processPayout()-----");
        SafeChargeResponseVO SafeChargeresVO=null;
        SafeChargeRequestVO SafeChargeVO = (SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO genericAddressDetailsVO = SafeChargeVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = SafeChargeVO.getCardDetailsVO();
        CommTransactionDetailsVO genericTransactionDetailsVO =  SafeChargeVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        if (genericCardDetailsVO == null)
            genericCardDetailsVO = new CommCardDetailsVO();

        Map<String, String> authMap = new TreeMap();
        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        authMap.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_EXPMONTH,genericCardDetailsVO.getExpMonth());
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMap.put(ELE_TYPE,REFUND);
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_TRANSACTIONID, genericTransactionDetailsVO.getPreviousTransactionId());
        authMap.put(ELE_CREDITTYPE,"1");
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());

        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');
        transactionLogger.error("-----payout request-----" + cardParameters);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::" + RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), cardParameters);
        }else {
            transactionLogger.error("URL:::::" + RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), cardParameters);
        }
        transactionLogger.error("-----payout response-----" + response);

        SafeChargeresVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(SafeChargeresVO!=null && !SafeChargeresVO.equals(""))
        {
            String status = "fail";
            String description = "payout failed";
            if(SafeChargeresVO.getErrorCode().equals("0"))
            {
                status = "success";
                description = "payout successful";
            }
            SafeChargeresVO.setStatus(status);
            SafeChargeresVO.setRemark(description);
            SafeChargeresVO.setDescriptor(GATEWAY_TYPE);
            SafeChargeresVO.setTransactionType(REFUND);
            SafeChargeresVO.setDescription(description);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        return SafeChargeresVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Entered in processCapture()-----");
        SafeChargeResponseVO SafeChargeresVO=null;
        SafeChargeRequestVO SafeChargeVO = (SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO genericAddressDetailsVO = SafeChargeVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = SafeChargeVO.getCardDetailsVO();
        CommTransactionDetailsVO genericTransactionDetailsVO =  SafeChargeVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        // validateForSale(trackingID,requestVO);
        Map<String, String> authMap = new TreeMap();
        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        authMap.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_EXPMONTH,genericCardDetailsVO.getExpMonth());
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMap.put(ELE_TYPE,CAPTURE);
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_AUTHCODE,SafeChargeVO.getAuthCode());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_TRANSACTIONID, genericTransactionDetailsVO.getPreviousTransactionId());
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());

        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');
        transactionLogger.error("-----capture request-----" + cardParameters);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::" + RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"),cardParameters);
        }else {
            transactionLogger.error("URL:::::" + RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"),cardParameters);
        }
        transactionLogger.error("-----capture response-----" + response);

        SafeChargeresVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(SafeChargeresVO!=null && !SafeChargeresVO.equals("")){
            String status = "fail";
            String remark="Transaction Failed";
            if(SafeChargeresVO.getErrorCode().equals("0")){
                status = "success";
                 remark="Transaction Successful";
            }
            SafeChargeresVO.setStatus(status);
            SafeChargeresVO.setRemark(remark);
            SafeChargeresVO.setDescription(remark);
            SafeChargeresVO.setDescriptor(GATEWAY_TYPE);
            SafeChargeresVO.setTransactionType(CAPTURE);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        return SafeChargeresVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Entered in processVoid()-----");
        SafeChargeResponseVO SafeChargeresVO=null;
        SafeChargeRequestVO SafeChargeVO = (SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO genericAddressDetailsVO = SafeChargeVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = SafeChargeVO.getCardDetailsVO();
        CommTransactionDetailsVO genericTransactionDetailsVO =  SafeChargeVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        Map<String, String> authMap = new TreeMap();
        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        authMap.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_EXPMONTH,genericCardDetailsVO.getExpMonth());
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMap.put(ELE_TYPE,VOID);
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_AUTHCODE,SafeChargeVO.getAuthCode());
        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        authMap.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_TRANSACTIONID, genericTransactionDetailsVO.getPreviousTransactionId());
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');
        transactionLogger.error("-----void request-----" + cardParameters);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::"+RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), cardParameters);
        }else {
            transactionLogger.error("URL:::::"+RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), cardParameters);
        }
        transactionLogger.error("-----void response-----" + response);
        SafeChargeresVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(SafeChargeresVO!=null && !SafeChargeresVO.equals("")){
            String status = "fail";
            String remark="Transaction Cancelled";

            if(SafeChargeresVO.getErrorCode().equals("0")){
                status = "success";
                remark="Transaction Cancelled";
            }
            SafeChargeresVO.setStatus(status);
            SafeChargeresVO.setRemark(remark);
            SafeChargeresVO.setDescription(remark);
            SafeChargeresVO.setDescriptor(GATEWAY_TYPE);
            SafeChargeresVO.setTransactionType(VOID);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            SafeChargeresVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        return SafeChargeresVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("------inside Safecharge processInquiry----");
        SafeChargeResponseVO SafeChargeresVO = null;
        try
        {
            String USER = "Username";
            String PASS = "Password";
            String ORDERID = "ClientUniqueID";
            String FROMDATE = "FromDate";
            String VERSION = "Version";

            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            CommTransactionDetailsVO genericTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            String response = "";
            String requestParameters = "";

            if (isTest)
            {
                transactionLogger.error("---------inside Test----------");
                Map<String, String> authMap = new TreeMap();
                authMap.put(USER, RB.getString("TEST_QOD_Username"));
                authMap.put(PASS, RB.getString("TEST_QOD_Password"));
                authMap.put(ORDERID, trackingID);
                authMap.put(FROMDATE, genericTransactionDetailsVO.getResponsetime());
                authMap.put(VERSION, "1.0.0");

                requestParameters = SafeChargeUtils.joinMapValue(authMap, '&');

                transactionLogger.error("requestParameters----" + requestParameters);

                response = SafeChargeUtils.doPostHTTPSURLConnectionForInquiry(RB.getString("TEST_QUERY_URL"), requestParameters);
            }
            else
            {
                transactionLogger.error("---------inside Live--------------");
                Map<String, String> authMap = new TreeMap();
                authMap.put(USER, RB.getString("LIVE_QOD_Username"));
                authMap.put(PASS, RB.getString("LIVE_QOD_Password"));
                authMap.put(ORDERID, trackingID);
                authMap.put(FROMDATE, genericTransactionDetailsVO.getResponsetime());
                authMap.put(VERSION, "1.0.0");

                requestParameters = SafeChargeUtils.joinMapValue(authMap, '&');

                transactionLogger.error("queryRequestParameters----" + requestParameters);

                response = SafeChargeUtils.doPostHTTPSURLConnectionForInquiry(RB.getString("TEST_LIVE_URL"), requestParameters);
            }
            String response1 = response.replace("&lt;", "<").replace("&gt;", ">");
            String response2= response1.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");

            transactionLogger.error("queryResponse----" + response2);

            if (response != null && !response.equals(""))
            {
                SafeChargeresVO = SafeChargeUtils.getSafeChargeQueryResponseVO(response2);

                SafeChargeresVO.setMerchantId(commRequestVO.getCommMerchantVO().getMerchantId());
                SafeChargeresVO.setDescription(commRequestVO.getTransDetailsVO().getOrderDesc());

            }
        }catch (Exception e){
            transactionLogger.error("Exception--->",e);
        }
        return SafeChargeresVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    //validation_for_methods
    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals("")) {
            transactionLogger.error("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }

        if(requestVO ==null)
        {
            transactionLogger.error("Request input not provided");
            throw new SystemError("Request input not provided");
        }

        SafeChargeRequestVO SafeChargeVO  =    (SafeChargeRequestVO)requestVO;
        if(SafeChargeVO.getAuthCode()==null || SafeChargeVO.getAuthCode().equals(""))
        {
            transactionLogger.error("Authcode not provided");
            throw new SystemError("Authcode not provided") ;
        }

        GenericTransDetailsVO genericTransDetailsVO = SafeChargeVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            transactionLogger.error("transDetails input not provided");
            throw new SystemError("transDetails input not provided");
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            transactionLogger.error("amount not provided");
            throw new SystemError("amount not provided");
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            transactionLogger.error("currency not provided");
            throw new SystemError("currency not provided");
        }

        CommAddressDetailsVO genericAddressDetailsVO= SafeChargeVO.getAddressDetailsVO();
        if(genericAddressDetailsVO ==null)
        {
            transactionLogger.error("Address Details input not provided");
            throw new SystemError("Address Details input not provided");
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            transactionLogger.error("First Name not provided");
            throw new SystemError("First Name not provided");

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            transactionLogger.error("Last Name not provided");
            throw new SystemError("Last Name not provided");

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {

            transactionLogger.error("Customer Email not provided");
            throw new SystemError("Customer Email not provided");
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            transactionLogger.error("Customer IP not provided");
            throw new SystemError("Customer IP not provided");
        }

        //Address Details
        if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
        {
            transactionLogger.error("Customer Address not provided");
            throw new SystemError("Customer Address not provided");
        }

        if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
        {
            transactionLogger.error("City not provided");
            throw new SystemError("City not provided");
        }
        if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            transactionLogger.error("Country not provided");
            throw new SystemError("Country not provided");
        }

        if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            transactionLogger.error("State not provided");
            throw new SystemError("State not provided");
        }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            transactionLogger.error("ZIP Code not provided");
            throw new SystemError("Zip Code not provided");
        }
        if(genericAddressDetailsVO.getPhone()==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            transactionLogger.error("Phone number not provided");
            throw new SystemError("phone number not provided");
        }

        GenericCardDetailsVO genericCardDetailsVO= SafeChargeVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            transactionLogger.error("Card Details input not provided");
            throw new SystemError("Card Details input not provided");
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {

            transactionLogger.error("Card Number not provided");
            throw new SystemError("Card Number not provided");
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            transactionLogger.error("Card number is invalid.");
            throw new SystemError("Card number is invalid.");
        }


        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {

            transactionLogger.error("Exp Month not provided");
            throw new SystemError("Exp Month not provided");
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {

            transactionLogger.error("Exp Year not provided");
            throw new SystemError("Exp Year not provided");
        }
    }

    private void validateForCapture(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            transactionLogger.error("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }

        if(requestVO ==null)
        {
            transactionLogger.error("Request input not provided");
            throw new SystemError("Request input not provided");
        }



        SafeChargeRequestVO SafeChargeVO  =    (SafeChargeRequestVO)requestVO;

        if(SafeChargeVO.getAuthCode()==null || SafeChargeVO.getAuthCode().equals(""))
        {
            transactionLogger.error("Authcode not provided");
            throw new SystemError("Authcode not provided") ;
        }
        GenericTransDetailsVO genericTransDetailsVO = SafeChargeVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            transactionLogger.error("transDetails input not provided");
            throw new SystemError("transDetails input not provided");
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            transactionLogger.error("amount not provided");
            throw new SystemError("amount not provided");
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            transactionLogger.error("currency not provided");
            throw new SystemError("currency not provided");
        }

        GenericAddressDetailsVO genericAddressDetailsVO= SafeChargeVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            transactionLogger.error("Address Details input not provided");
            throw new SystemError("Address Details input not provided");
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            transactionLogger.error("First Name not provided");
            throw new SystemError("First Name not provided");

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            transactionLogger.error("Last Name not provided");
            throw new SystemError("Last Name not provided");

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {

            transactionLogger.error("Customer Email not provided");
            throw new SystemError("Customer Email not provided");
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            transactionLogger.error("Customer IP not provided");
            throw new SystemError("Customer IP not provided");
        }

        //Address Details
        if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
        {
            transactionLogger.error("Customer Address not provided");
            throw new SystemError("Customer Address not provided");
        }

        if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
        {
            transactionLogger.error("City not provided");
            throw new SystemError("City not provided");
        }
        if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            transactionLogger.error("Country not provided");
            throw new SystemError("Country not provided");
        }

        if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            transactionLogger.error("State not provided");
            throw new SystemError("State not provided");
        }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            transactionLogger.error("ZIP Code not provided");
            throw new SystemError("Zip Code not provided");
        }
        if(genericAddressDetailsVO.getPhone()==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            transactionLogger.error("Phone number not provided");
            throw new SystemError("phone number not provided");
        }

        GenericCardDetailsVO genericCardDetailsVO= SafeChargeVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            transactionLogger.error("Card Details input not provided");
            throw new SystemError("Card Details input not provided");
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {

            transactionLogger.error("Card Number not provided");
            throw new SystemError("Card Number not provided");
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            transactionLogger.error("Card number is invalid.");
            throw new SystemError("Card number is invalid.");
        }


        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {

            transactionLogger.error("Exp Month not provided");
            throw new SystemError("Exp Month not provided");
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {

            transactionLogger.error("Exp Year not provided");
            throw new SystemError("Exp Year not provided");
        }
        if(genericCardDetailsVO.getCardHolderName()==null || genericCardDetailsVO.getCardHolderName().equals(""))
        {


            transactionLogger.error("sitename not provided");
            throw new SystemError("sitename not provided");

        }
    }

    private void validateForVoid(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            transactionLogger.error("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }

        if(requestVO ==null)
        {
            transactionLogger.error("Request input not provided");
            throw new SystemError("Request input not provided");
        }



        SafeChargeRequestVO SafeChargeVO  =    (SafeChargeRequestVO)requestVO;

        if(SafeChargeVO.getAuthCode()==null || SafeChargeVO.getAuthCode().equals(""))
        {
            transactionLogger.error("Authcode not provided");
            throw new SystemError("Authcode not provided") ;
        }
        GenericTransDetailsVO genericTransDetailsVO = SafeChargeVO.getTransDetailsVO();
        if(genericTransDetailsVO == null)
        {
            transactionLogger.error("transDetails input not provided");
            throw new SystemError("transDetails input not provided");
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            transactionLogger.error("amount not provided");
            throw new SystemError("amount not provided");
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            transactionLogger.error("currency not provided");
            throw new SystemError("currency not provided");
        }

        GenericAddressDetailsVO genericAddressDetailsVO= SafeChargeVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            transactionLogger.error("Address Details input not provided");
            throw new SystemError("Address Details input not provided");
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            transactionLogger.error("First Name not provided");
            throw new SystemError("First Name not provided");

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            transactionLogger.error("Last Name not provided");
            throw new SystemError("Last Name not provided");

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {

            transactionLogger.error("Customer Email not provided");
            throw new SystemError("Customer Email not provided");
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            transactionLogger.error("Customer IP not provided");
            throw new SystemError("Customer IP not provided");
        }

        //Address Details
        if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
        {
            transactionLogger.error("Customer Address not provided");
            throw new SystemError("Customer Address not provided");
        }

        if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
        {
            transactionLogger.error("City not provided");
            throw new SystemError("City not provided");
        }
        if (genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
        {
            transactionLogger.error("Country not provided");
            throw new SystemError("Country not provided");
        }

        if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            transactionLogger.error("State not provided");
            throw new SystemError("State not provided");
        }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
        {
            transactionLogger.error("ZIP Code not provided");
            throw new SystemError("Zip Code not provided");
        }
        if(genericAddressDetailsVO.getPhone()==null || genericAddressDetailsVO.getPhone().equals(""))
        {
            transactionLogger.error("Phone number not provided");
            throw new SystemError("phone number not provided");
        }

        GenericCardDetailsVO genericCardDetailsVO= SafeChargeVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            transactionLogger.error("Card Details input not provided");
            throw new SystemError("Card Details input not provided");
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {

            transactionLogger.error("Card Number not provided");
            throw new SystemError("Card Number not provided");
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            transactionLogger.error("Card number is invalid.");
            throw new SystemError("Card number is invalid.");
        }


        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {

            transactionLogger.error("Exp Month not provided");
            throw new SystemError("Exp Month not provided");
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {

            transactionLogger.error("Exp Year not provided");
            throw new SystemError("Exp Year not provided");
        }
    }

    private void validateForSale(String trackingID, GenericRequestVO requestVO,String addressValidation) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Tracking Id not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Tracking Id not Provided while placing transaction", new Throwable("Tracking Id not Provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Request VO not Provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "Request VO not Provided while placing transaction", new Throwable("Request VO not Provided while placing transaction"));
        }



        SafeChargeRequestVO SafeChargeVO  =    (SafeChargeRequestVO)requestVO;

        GenericTransDetailsVO genericTransDetailsVO = SafeChargeVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails VO not Provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails VO not Provided while placing transaction",new Throwable("TransactionDetails VO not Provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Amount not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not Provided while placing transaction", new Throwable("Amount not Provided while placing transaction"));
        }
        if(genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Currency not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Currency not Provided while placing transaction", new Throwable("Currency not Provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= SafeChargeVO.getAddressDetailsVO();


        if(genericAddressDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails VO not Provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails VO not Provided while placing transaction",new Throwable("AddressDetails VO not Provided while placing transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not Provided while placing transaction",new Throwable("First Name not Provided while placing transaction"));

        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Last Name not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Last Name not Provided while placing transaction", new Throwable("Last Name not Provided while placing transaction"));

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {

            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Email Id not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Email Id not Provided while placing transaction", new Throwable("Email Id not Provided while placing transaction"));
        }

        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Ip Address not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Ip Address not Provided while placing transaction", new Throwable("Ip Address not Provided while placing transaction"));
        }

        //Address Details

        if (genericAddressDetailsVO.getCountry() == null || genericAddressDetailsVO.getCountry().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Country not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Country not Provided while placing transaction", new Throwable("Country not Provided while placing transaction"));
        }


        if("Y".equalsIgnoreCase(addressValidation))
        {
            if (genericAddressDetailsVO.getStreet() == null || genericAddressDetailsVO.getStreet().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Street not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Street not Provided while placing transaction", new Throwable("Street not Provided while placing transaction"));
            }

            if (genericAddressDetailsVO.getCity() == null || genericAddressDetailsVO.getCity().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "City not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "City not Provided while placing transaction", new Throwable("City not Provided while placing transaction"));
            }

        /*if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(),"validateForSale()",null,"common","State not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"State not Provided while placing transaction",new Throwable("State not Provided while placing transaction"));
        }*/

            if (genericAddressDetailsVO.getZipCode() == null || genericAddressDetailsVO.getZipCode().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Zip Code not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Zip Code not Provided while placing transaction", new Throwable("Zip Code not Provided while placing transaction"));
            }
            if (genericAddressDetailsVO.getPhone() == null || genericAddressDetailsVO.getPhone().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Phone NO not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Phone NO not Provided while placing transaction", new Throwable("Phone NO not Provided while placing transaction"));
            }
        }
        GenericCardDetailsVO genericCardDetailsVO= SafeChargeVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "CardDetails VO not Provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "CardDetails VO not Provided while placing transaction", new Throwable("CardDetails VO not Provided while placing transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {

            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Card NO not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Card NO not Provided while placing transaction", new Throwable("Card NO not Provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO  Provided is invalid while placing transaction", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,"Card NO not Provided is invalid while placing transaction",new Throwable("Card NO not Provided while placing transaction"));
        }

        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "CVV not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "CVV not Provided while placing transaction", new Throwable("CVV not Provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not Provided while placing transaction",new Throwable("Expiry Month not Provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(SafeChargePaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Year not Provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Expiry Year not Provided while placing transaction", new Throwable("Expiry Year not Provided while placing transaction"));
        }



    }

    private void validateForQuery(String trackingID, GenericRequestVO requestVO) throws SystemError
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            transactionLogger.error("TrackingId not provided");
            throw new SystemError("TrackingId not provided");
        }
        if(requestVO ==null)
        {
            transactionLogger.error("Request input not provided");
            throw new SystemError("Request input not provided");
        }

        CommRequestVO commRequestVO =  (CommRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            transactionLogger.error("TransactionID not provided");
            throw new SystemError("TransactionID not provided");
        }
    }

    public String getwebsiteid(String trackingid) throws PZDBViolationException
    {
        String website = "";
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String qry = "SELECT sitename FROM members AS m,transaction_common AS t WHERE t.toid=m.memberid AND t.trackingid=?";
            PreparedStatement pstmt = connection.prepareStatement(qry);
            pstmt.setString(1, trackingid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                website = rs.getString("sitename");
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SafeChargePaymentGateway.class.getName(),"getwebsiteid()",null,"common","Exception while getting sitename from members", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(SafeChargePaymentGateway.class.getName(),"getwebsiteid()",null,"common","Exception while getting sitename from members", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return website;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Entered in process3DSaleConfirmation()-----");
        SafeChargeResponseVO safeChargeResponseVO=null;
        SafeChargeRequestVO safeChargeVO = (SafeChargeRequestVO) requestVO;

        CommTransactionDetailsVO genericTransactionDetailsVO =  safeChargeVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = safeChargeVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = safeChargeVO.getCardDetailsVO();
        //CommMerchantVO commMerchantVO=safeChargeVO.getCommMerchantVO();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        //validateForSale(trackingID, requestVO);
        Map<String, String> authMap = new TreeMap();
        Functions functions = new Functions();

        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

        authMap.put(ELE_TYPE,SALE);
        authMap.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_EXPMONTH,genericCardDetailsVO.getExpMonth());
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getIp());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());

        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        if(functions.isValueNull(safeChargeVO.getPARes()))
            authMap.put(ELE_PARes,URLEncoder.encode(safeChargeVO.getPARes(),"UTF-8"));
        else
            authMap.put(ELE_PARes,"");
        authMap.put(ELE_TRANSACTIONID,genericTransactionDetailsVO.getPreviousTransactionId());
        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');

        transactionLogger.error("-----process3DSaleConfirmation request-----" + cardParameters);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::" + RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), cardParameters);
        }else {
            transactionLogger.error("URL:::::" + RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), cardParameters);
        }
        transactionLogger.error("-----process3DSaleConfirmation response-----" + response);

        safeChargeResponseVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(safeChargeResponseVO!=null && !safeChargeResponseVO.equals("")){
            String status = "fail";
            String remark="Transaction Failed";
            if("APPROVED".equals(safeChargeResponseVO.getTransactionStatus()) && "0".equals(safeChargeResponseVO.getErrorCode())){
                status = "success";
                remark="Transaction Successful";
                safeChargeResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            safeChargeResponseVO.setStatus(status);
            safeChargeResponseVO.setRemark(remark);
            safeChargeResponseVO.setTransactionType(SALE);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            safeChargeResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            safeChargeResponseVO.setCurrency(genericTransactionDetailsVO.getCurrency());
            safeChargeResponseVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
            safeChargeResponseVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
        }
        return safeChargeResponseVO;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Entered in processCommon3DSaleConfirmation()-----");
        SafeChargeResponseVO safeChargeResponseVO=null;
        Comm3DRequestVO comm3DRequestVO = (Comm3DRequestVO) requestVO;

        CommTransactionDetailsVO genericTransactionDetailsVO =  comm3DRequestVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = comm3DRequestVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = comm3DRequestVO.getCardDetailsVO();
        //CommMerchantVO commMerchantVO=safeChargeVO.getCommMerchantVO();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        transactionLogger.debug("paymentId-----"+comm3DRequestVO.getTransDetailsVO().getPreviousTransactionId());

        //validateForSale(trackingID, requestVO);
        Map<String, String> authMap = new TreeMap();
        Functions functions = new Functions();

        String cvv="";
        if(functions.isValueNull(comm3DRequestVO.getMd())){
            String data=comm3DRequestVO.getMd();
            String value[]=data.split("@");
             cvv=value[1];
        }

        String name=genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

        authMap.put(ELE_TYPE,SALE);
        authMap.put(ELE_NAMEonCARD,name);
        authMap.put(ELE_CCNUMBER,genericCardDetailsVO.getCardNum());
        authMap.put(ELE_EXPMONTH,genericCardDetailsVO.getExpMonth());
        authMap.put(ELE_EXPYEAR,genericCardDetailsVO.getExpYear().substring(2));
        authMap.put(ELE_CVV,cvv);
        authMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
        authMap.put(ELE_AMOUNT,genericTransactionDetailsVO.getAmount());
        authMap.put(ELE_ORDERID,trackingID);
        authMap.put(ELE_WEbsite,getwebsiteid(trackingID));
        authMap.put(ELE_RESPONSEFORMAT,"4");
        authMap.put(ELE_VERSION,"4.0.4");
        authMap.put(ELE_FIRSTNAME,genericAddressDetailsVO.getFirstname());
        authMap.put(ELE_LASTNAME,genericAddressDetailsVO.getLastname());
        authMap.put(ELE_ADDRESS,genericAddressDetailsVO.getStreet());
        authMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
        authMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
        authMap.put(ELE_STATE,genericAddressDetailsVO.getState());
        authMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
        authMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
        authMap.put(ELE_IPADDRESS,genericAddressDetailsVO.getCardHolderIpAddress());
        authMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());

        authMap.put(ELE_USERNAME,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        authMap.put(ELE_PASSWORD,GatewayAccountService.getGatewayAccount(accountId).getPassword());
        if(functions.isValueNull(comm3DRequestVO.getPaRes()))
        authMap.put(ELE_PARes,URLEncoder.encode(comm3DRequestVO.getPaRes(),"UTF-8"));
        else
            authMap.put(ELE_PARes,"");
        authMap.put(ELE_TRANSACTIONID,genericTransactionDetailsVO.getPreviousTransactionId());
        String cardParameters = SafeChargeUtils.joinMapValue(authMap, '&');

        transactionLogger.error("-----process3DSaleConfirmation request-----" + cardParameters);

        String response="";
        if(isTest){
            transactionLogger.error("URL:::::" + RB.getString("TEST_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("TEST_URL"), cardParameters);
        }else {
            transactionLogger.error("URL:::::" + RB.getString("LIVE_URL"));
            response = SafeChargeUtils.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), cardParameters);
        }
        transactionLogger.error("-----process3DSaleConfirmation response-----" + response);

        safeChargeResponseVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        if(safeChargeResponseVO!=null && !safeChargeResponseVO.equals("")){
            String status = "fail";
            String remark="Transaction Failed";
            if("APPROVED".equals(safeChargeResponseVO.getTransactionStatus()) && "0".equals(safeChargeResponseVO.getErrorCode())){
                status = "success";
                remark="Transaction Successful";
                safeChargeResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            safeChargeResponseVO.setStatus(status);
            safeChargeResponseVO.setRemark(remark);
            safeChargeResponseVO.setDescription(remark);
            safeChargeResponseVO.setTransactionType(SALE);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            safeChargeResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            safeChargeResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            safeChargeResponseVO.setCurrency(genericTransactionDetailsVO.getCurrency());
            safeChargeResponseVO.setTmpl_Amount(genericAddressDetailsVO.getTmpl_amount());
            safeChargeResponseVO.setTmpl_Currency(genericAddressDetailsVO.getTmpl_currency());
        }
        return safeChargeResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws UnsupportedEncodingException,PZGenericConstraintViolationException
    {
            ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
            ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
            errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
            PZGenericConstraint genConstraint = new PZGenericConstraint("SafeChargePaymentGateway", "process3DAuthConfirmation", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
            throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by Safecharge gateway. Please contact your Tech. support Team:::", null);
    }
}