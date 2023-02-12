package com.payment.paspx;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 10/30/14
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaspxPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(PaspxPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaspxPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "paspx";
    private final static String TESTURL = "https://secure.paspx.com/cgi-bin/api.pl";

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PaspxPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("PaspxPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

        /*log.debug("Entering processAuth of PaspxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        PaspxAccount paspxAccount = new PaspxAccount();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        Hashtable usernamePassword = paspxAccount.getUserNamePassword(accountId);

        String userName = (String) usernamePassword.get("username");
        String password = (String) usernamePassword.get("password");
        String type = "auth";
        String amount = genericTransDetailsVO.getAmount();
        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(2);
        String expCard = expMonth+expYear;
        String cvv = genericCardDetailsVO.getcVV();
        String orderDesc = trackingID;
        String fName = addressDetailsVO.getFirstname();
        String lName = addressDetailsVO.getLastname();
        String country = addressDetailsVO.getCountry();
        String state = addressDetailsVO.getState();
        String add1 = addressDetailsVO.getStreet();
        String add2 = "";
        String city = addressDetailsVO.getCity();
        String zip = addressDetailsVO.getZipCode();
        String phone = addressDetailsVO.getPhone();
        String fax = "";
        String email = addressDetailsVO.getEmail();
        String sasaba = "1";
        String ipAddress = addressDetailsVO.getIp();


        String requestData = "username="+userName+"&password="+password+"&type="+type+"&totamt="+amount+"&currency="+currency+"&ccn="+cardNo+"&ccexp="+expCard+"&cvv="+cvv+
                "&orderid="+trackingID+"&orderdesc="+orderDesc+"&bfn="+fName+"&bln="+lName+"&bcountry="+country+"&bstate="+state+
                "&baddress1="+add1+"&baddress2="+add2+"&bcity="+city+"&bzip="+zip+"&bphone="+phone+"&bfax="+fax+"&bemail="+email+"&sasaba="+sasaba+"customer_ip="+ipAddress;

        String response = PaspxUtills.doPostHTTPSURLConnection(TESTURL, requestData);
        System.out.println("---request---"+requestData);
        System.out.println("Response---"+response);*/

        //return null;
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        log.debug("Entering processSale of PaspxPaymentGateway...");
        transactionLogger.debug("Entering processSale of PaspxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        PaspxAccount paspxAccount = new PaspxAccount();
        PaspxUtills paspxUtills = new PaspxUtills();

            GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

            Hashtable usernamePassword = paspxAccount.getUserNamePassword(accountId);

            String userName = (String) usernamePassword.get("mid");
            String password = (String) usernamePassword.get("password");
            String type = "sale";
            String amount = genericTransDetailsVO.getAmount();
            String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
            String cardNo = genericCardDetailsVO.getCardNum();
            String expMonth = genericCardDetailsVO.getExpMonth();
            String expYear = genericCardDetailsVO.getExpYear().substring(2);
            String expCard = expMonth+expYear;
            String cvv = genericCardDetailsVO.getcVV();
            String orderDesc = trackingID;
            String fName = addressDetailsVO.getFirstname();
            String lName = addressDetailsVO.getLastname();
            String country = addressDetailsVO.getCountry();
            String state = addressDetailsVO.getState();
            String add1 = addressDetailsVO.getStreet();
            String add2 = "";
            String city = addressDetailsVO.getCity();
            String zip = addressDetailsVO.getZipCode();
            String phone = addressDetailsVO.getPhone();
            String fax = "";
            String email = addressDetailsVO.getEmail();
            String sasaba = "1";
            String ipAddress = addressDetailsVO.getIp();


            String requestData = "username="+userName+"&password="+password+"&type="+type+"&totamt="+amount+"&currency="+currency+"&ccn="+cardNo+"&ccexp="+expCard+"&cvv="+cvv+
                    "&orderid="+trackingID+"&orderdesc="+orderDesc+"&bfn="+fName+"&bln="+lName+"&bcountry="+country+"&bstate="+state+
                    "&baddress1="+add1+"&baddress2="+add2+"&bcity="+city+"&bzip="+zip+"&bphone="+phone+"&bfax="+fax+"&bemail="+email+"&sasaba="+sasaba+"customer_ip="+ipAddress;

            String response = PaspxUtills.doPostHTTPSURLConnection(TESTURL, requestData);
            log.debug("----ResponseSale---"+response);
            log.error("---ResponseSale---"+response);

            transactionLogger.debug("----ResponseSale---"+response);
            transactionLogger.error("---ResponseSale---"+response);

            HashMap responseData = paspxUtills.readHtmlResponse(response);
            log.debug("---responseData---"+responseData);
            transactionLogger.debug("---responseData---"+responseData);

            if(!responseData.equals("") && responseData!=null)
            {
                String status = "fail";
                if(responseData.get("MSC response code").equals("100") && responseData.get("MSC response text").equals("SUCCESS"))
                {
                    status = "success";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setErrorCode((String) responseData.get("MSC response code"));
                commResponseVO.setTransactionStatus((String) responseData.get("Status"));
                commResponseVO.setRemark((String)responseData.get("Response text"));
                commResponseVO.setTransactionId((String) responseData.get("Transaction ID"));
                commResponseVO.setDescription((String) responseData.get("MSC response text"));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processRefund of PaspxPaymentGateway...");
        transactionLogger.debug("Entering processRefund of PaspxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

            PaspxAccount paspxAccount = new PaspxAccount();
            PaspxUtills paspxUtills = new PaspxUtills();

            Hashtable usernamePassword = paspxAccount.getUserNamePassword(accountId);

            String userName = (String) usernamePassword.get("mid");
            String password = (String) usernamePassword.get("password");
            String type = "refund";
            String refAmt = genericTransDetailsVO.getAmount();
            String tid = genericTransDetailsVO.getPreviousTransactionId();

            String refundRequest = "username="+userName+"&password="+password+"&type="+type+"&tid="+tid+"&tamnt="+refAmt;

            String response = PaspxUtills.doPostHTTPSURLConnection(TESTURL,refundRequest);
            log.debug("---refund response---"+response);
            log.error("---refund response---"+response);

            transactionLogger.debug("---refund response---"+response);
            transactionLogger.error("---refund response---"+response);

            HashMap responseData = paspxUtills.readHtmlResponse(response);
            log.debug("---responseData---"+responseData);
            transactionLogger.debug("---responseData---"+responseData);

            if(!responseData.equals("") && responseData!=null)
            {
                String status = "fail";
                if(responseData.get("MSC response code").equals("100") && responseData.get("MSC response text").equals("SUCCESS"))
                {
                    status = "success";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setErrorCode((String) responseData.get("MSC response code"));
                commResponseVO.setTransactionStatus((String) responseData.get("Status"));
                commResponseVO.setRemark((String) responseData.get("Response text"));
                commResponseVO.setTransactionId((String) responseData.get("Transaction ID"));
                commResponseVO.setDescription((String) responseData.get("MSC response text"));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                commResponseVO.setTransactionType(type);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO)
    {
        /*log.debug("Entering processVoid of PaspxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        PaspxAccount paspxAccount = new PaspxAccount();
        Hashtable usernamePassword = paspxAccount.getUserNamePassword(accountId);

        String userName = (String) usernamePassword.get("username");
        String password = (String) usernamePassword.get("password");
        String type = "void";
        String refAmt = genericTransDetailsVO.getAmount();
        String tid = genericTransDetailsVO.getPreviousTransactionId();

        String voidReq = "username="+userName+"&password="+password+"&type=void&tid="+tid;
        String response = PaspxUtills.doPostHTTPSURLConnection(TESTURL,voidReq);*/

        return null;

    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO)
    {
        /*log.debug("Entering processCapture of PaspxPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        PaspxAccount paspxAccount = new PaspxAccount();
        Hashtable usernamePassword = paspxAccount.getUserNamePassword(accountId);

        String userName = (String) usernamePassword.get("username");
        String password = (String) usernamePassword.get("password");
        String type = "capture";
        String tid = genericTransDetailsVO.getPreviousTransactionId();
        String captureAmount = "";

        String capture = "username="+userName+"&password="+password+"&type="+type+"&tid="+tid+"&tamnt="+captureAmount;*/

        return null;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PaspxPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
}
