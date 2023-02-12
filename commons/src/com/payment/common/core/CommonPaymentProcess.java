package com.payment.common.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.logicboxes.util.ApplicationProperties;
import com.manager.*;
import com.manager.dao.FraudServiceDAO;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TransactionDAO;
import com.manager.enums.BlacklistEnum;
import com.manager.enums.ResponseLength;
import com.manager.utils.TransactionUtil;
import com.manager.vo.*;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailService;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.checkers.PaymentChecker;
import com.payment.easypay.EasyPaymentGateway;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.errors.TransactionError;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.flexepin.FlexepinPaymentGateway;
import com.payment.omnipay.OmnipayPaymentGateway;
import com.payment.request.*;
import com.payment.response.*;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.tigerpay.TigerPayPaymentGateway;
import com.payment.trustPay.VO.responseVO.ResponseVO;
import com.payment.uba_mc.UBAMCPaymentGateway;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.vouchermoney.VoucherMoneyPaymentGateway;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/*import com.fraud.FraudChecker;
import com.fraud.utils.ReadFraudServiceRequest;*/

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 3/30/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommonPaymentProcess extends AbstractPaymentProcess
{
    private final static ResourceBundle RB                  = LoadProperties.getProperty("com.directi.pg.CommServlet");
    final static String ERRORSERVLET                        = RB.getString("ERRORSERVLET");
    private final static String POSTURL                     = RB.getString("POSTURL");
    private final static String COMMSERVLET                 = RB.getString("COMMSERVLET");
    private final static String ICICIEMAIL                  = RB.getString("NOPROOFREQUIRED_EMAIL");
    private final static String MANAGEMENT_NOTIFY_EMAIL     = RB.getString("MANAGEMENT_NOTIFY_EMAIL");
    private final static String PROXYHOST                   = RB.getString("PROXYHOST");
    private final static String PROXYPORT                   = RB.getString("PROXYPORT");
    private final static String PROXYSCHEME                 = RB.getString("PROXYSCHEME");
    ResourceBundle rb                                       = LoadProperties.getProperty("com.directi.pg.chargebackFraud");
    private static Logger log                               = new Logger(CommonPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger      = new TransactionLogger(CommonPaymentProcess.class.getName());
    private static Vector unboilchar                        = new Vector();
    private static Vector blockedemail, blockeddomain, blockedcountry;
    private static String defaultchargepercent          = "700";
    private static String INR_defaulttaxpercent         = "1236";
    private static String USD_defaulttaxpercent         = "1236";
    boolean isLogEnabled                            = Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));
    PaymentManager paymentManager                   = new PaymentManager();
    TransactionManager transactionManager           = new TransactionManager();
    TransactionDAO transactionDAO                   = new TransactionDAO();
    TransactionError transactionError               = new TransactionError();


    static
    {

        unboilchar.add("a");
        unboilchar.add("b");
        unboilchar.add("c");
        unboilchar.add("d");
        unboilchar.add("e");
        unboilchar.add("f");
        unboilchar.add("g");
        unboilchar.add("h");
        unboilchar.add("i");
        unboilchar.add("j");
        unboilchar.add("k");
        unboilchar.add("l");
        unboilchar.add("m");
        unboilchar.add("n");
        unboilchar.add("o");
        unboilchar.add("p");
        unboilchar.add("q");
        unboilchar.add("r");
        unboilchar.add("s");
        unboilchar.add("t");
        unboilchar.add("u");
        unboilchar.add("v");
        unboilchar.add("w");
        unboilchar.add("x");
        unboilchar.add("y");
        unboilchar.add("x");
        unboilchar.add("0");
        unboilchar.add("1");
        unboilchar.add("2");
        unboilchar.add("3");
        unboilchar.add("4");
        unboilchar.add("5");
        unboilchar.add("6");
        unboilchar.add("7");
        unboilchar.add("8");
        unboilchar.add("9");
    }
    private TransactionUtil transactionUtil = new TransactionUtil();
    private Functions functions             = new Functions();

    @Override
    public int insertTransactionDetails(Hashtable parameters) throws SystemError
    {
        return insertTransDetails(parameters, PZTransactionStatus.BEGUN);
    }


    public int insertTransDetails(Hashtable parameters, PZTransactionStatus initialTransStatus) throws SystemError
    {

        Connection connection = Database.getConnection();
        int trackingId = 0;
        try
        {

            String query        = "insert into transaction_common(accountid,paymodeid,cardtypeid,toid,totype,fromid,fromtype,description,orderdescription,amount,currency,redirecturl,status,dtstamp,httpheader,ipaddress) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, String.valueOf(parameters.get("ACCOUNTID")));
            p.setString(2, String.valueOf(parameters.get("PAYMODEID")));
            p.setString(3, String.valueOf(parameters.get("CARDTYPEID")));
            p.setString(4, String.valueOf(parameters.get("TOID")));
            p.setString(5, String.valueOf(parameters.get("TOTYPE")));
            p.setString(6, String.valueOf(parameters.get("FROMID")));
            p.setString(7, String.valueOf(parameters.get("FROMTYPE")));
            p.setString(8, String.valueOf(parameters.get("DESCRIPTION")));
            p.setString(9, String.valueOf(parameters.get("ORDER_DESCRIPTION")));
            p.setString(10, String.valueOf(parameters.get("TXN_AMT")));
            p.setString(11, String.valueOf(parameters.get("CURRENCY")));
            p.setString(12, String.valueOf(parameters.get("REDIRECTURL")));
            p.setString(13, String.valueOf(initialTransStatus));
            p.setString(14, String.valueOf(parameters.get("HEADDER")));
            p.setString(15, String.valueOf(parameters.get("REMOTEADDR")));
            int num = p.executeUpdate();
            ResultSet rs = p.getGeneratedKeys();
            if (num == 1 && rs != null)
            {
                rs.next();
                trackingId = rs.getInt(1);
            }
            else
            {
                throw new SystemError("Error while inserting transaction data number of rows" + num + " resultset" + rs);
            }

        }
        catch (SQLException e)
        {
            throw new SystemError("Error while inserting transaction data" + e.getMessage());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return trackingId;
    }

    @Override
    public String getRedirectPage(Hashtable parameters) throws SystemError
    {

        StringBuffer responseBuffer = new StringBuffer();
        Hashtable error             = new Hashtable();
        Hashtable otherdetails      = new Hashtable();
        String template             = null;
        String vbv                  = null;
        String autoredirect         = null;
        String key                  = "";
        String val                  = "";
        String ctoken               = String.valueOf(parameters.get("ctoken"));

        Connection connection       = null;
        try
        {
            connection          = Database.getConnection();
            String sql          = "select autoredirect,template,vbv from members where memberid =?";
            PreparedStatement p = connection.prepareStatement(sql);
            p.setString(1, String.valueOf(parameters.get("TOID")));
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                autoredirect    = rs.getString("autoredirect");
                template        = rs.getString("template");
                vbv             = rs.getString("vbv");

            }

        }
        catch (Exception e)
        {
            error.put("Error", "Error occured while inserting data");

        }
        finally
        {
            Database.closeConnection(connection);
        }

        StringBuffer sbuf       = new StringBuffer();

        if (error.size() > 0)
        {
            sbuf.append("Following Parameters are Invalid");
            Enumeration enu     = error.keys();

            sbuf.append("<center><table border=1>");
            sbuf.append("<tr bgcolor=\"blue\" >");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Field");
            sbuf.append("</font></td>");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Error");
            sbuf.append("</font></td>");
            sbuf.append("</tr>");
            while (enu.hasMoreElements())
            {
                String field = (String) enu.nextElement();
                sbuf.append("<tr>");
                sbuf.append("<td>");
                sbuf.append(field);
                sbuf.append("</td>");
                sbuf.append("<td>");
                sbuf.append((String) error.get(field));
                sbuf.append("</td>");
                sbuf.append("</tr>");
            }
            sbuf.append("</table>");

            otherdetails.put("MESSAGE", sbuf.toString());

            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");


            try
            {
                String toid     = String.valueOf(parameters.get("TOID"));
                responseBuffer.append(Template.getError(toid, otherdetails));


            }
            catch (SystemError se)
            {
                log.error("Excpetion in WaitServlet", se);
                transactionLogger.error("Excpetion in WaitServlet", se);

            }
            return responseBuffer.toString();
        }

        parameters.put("autoredirect", autoredirect);
        parameters.put("TEMPLATE", template);
        parameters.put("VBV", vbv);


        responseBuffer.append("<HTML>");
        responseBuffer.append("<HEAD> <script language=\"javascript\">" +
                "function Load(){" +
                "document.form.submit();" +
                "}" +
                " </script>");
        responseBuffer.append("</HEAD>");
        responseBuffer.append("<BODY onload=Load()>");
        responseBuffer.append("<form name=\"form\" action=\""+COMMSERVLET+"?ctoken"+ctoken+"\" method=\"post\" >");
        Enumeration enu = parameters.keys();

        while (enu.hasMoreElements())
        {
            key = (String) enu.nextElement();
            val = parameters.get(key).toString();
            responseBuffer.append("<input type=hidden name=\"" + key.toString() + "\" value=\"" + val.toString() + "\" >");
            //log.debug("<input type=hidden name=\"" + key.toString() + "\" value=\"" + val.toString() + "\"");
        }

        responseBuffer.append("</form>");
        responseBuffer.append("</BODY>");
        responseBuffer.append("</HTML>");

        return responseBuffer.toString();
    }

   /* @Override
    public Hashtable transaction(Map<String, String> transactionRequestParameters, Hashtable<String, Object> transactionAttributes)

    {
        Hashtable<String, Object> responseHash = new Hashtable<String, Object>();
        Hashtable<String, Object> attributeHash = new Hashtable<String, Object>();
        responseHash.put("attributes", attributeHash);
        Hashtable error = new Hashtable();

        TransactionError transactionError = new TransactionError();
        RiskCheckers riskCheckers = new RiskCheckers();
        PaymentChecker paymentChecker = new PaymentChecker();
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();
        LimitChecker limitChecker = new LimitChecker();

        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        log.debug("Entering doService of Servlet");
        transactionLogger.debug("Entering doService of Servlet");

        Hashtable otherdetails = new Hashtable();

        blockedemail = (Vector) transactionAttributes.get("BLOCKEDEMAIL");
        blockeddomain = (Vector) transactionAttributes.get("BLOCKEDDOMAIN");
        blockedcountry = (Vector) transactionAttributes.get("BLOCKEDCOUNTRY");

        Hashtable paramHash = new Hashtable();           //Hashtable
        String ctoken = String.valueOf(transactionAttributes.get("ctoken"));
        for (Map.Entry stringEntry : transactionRequestParameters.entrySet())
        {

            String name = (String) stringEntry.getKey();
            String value = null;
            if (stringEntry.getValue() != null)
            {
                value = ESAPI.encoder().encodeForHTML(String.valueOf(transactionRequestParameters.get(name)));
            }
            if (value == null)
                value = "";

            paramHash.put(name, value);

            if (name.startsWith("TMPL_"))
                otherdetails.put(name, value);
        }

        otherdetails.put("TEMPLATE", transactionRequestParameters.get("TEMPLATE"));

        // MemberId
        int memberId = -9999;
        String merchantId = null;
        String displayName = null;
        String chargepercent = null;
        String dbTaxPercent = null;
        String fixamount = null;
        BigDecimal charge = null;
        BigDecimal chargeamount = null;
        BigDecimal taxPercentage = null;
        String templateCurrency = null;
        String templateAmount = null;

        log.debug("Getting all details of Customer");
        transactionLogger.debug("Getting all details of Customer");
        String checksumVal = String.valueOf(transactionRequestParameters.get("checksum"));
        String HRCode = "";
        String countryinrec = "";
        String hrAlertProof = "";
        String datamismatchproof = "";
        boolean diffCountry = false;
        boolean repeatEmail = false;
        boolean repeatmachine = false;
        String encoded_TRACKING_ID = ESAPI.encoder().encodeForHTML(transactionRequestParameters.get("TRACKING_ID"));
        String encoded_DESCRIPTION = ESAPI.encoder().encodeForHTML(transactionRequestParameters.get("DESCRIPTION"));
        String encoded_ORDER_DESCRIPTION = ESAPI.encoder().encodeForHTML(transactionRequestParameters.get("ORDER_DESCRIPTION"));

        // Start: ValidationMandatory Parameter
        error = validateMandatoryParameter(transactionRequestParameters);

        if(error.size()>0)
        {
            transactionError.displayErrorsForCommon(error,otherdetails,transactionRequestParameters.get("TOID"),responseHash,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION);
            return responseHash;
        }

        String toid = transactionRequestParameters.get("TOID");
        String country = transactionRequestParameters.get("country");
        String firstname = transactionRequestParameters.get("firstname");
        String lastname = transactionRequestParameters.get("lastname");
        String cardholder = firstname + " " + lastname;
        String emailaddr = transactionRequestParameters.get("emailaddr");
        String ccpan = transactionRequestParameters.get("PAN");
        String ccid = transactionRequestParameters.get("ccid");
        String street = transactionRequestParameters.get("street");
        String city = transactionRequestParameters.get("city");
        String state = transactionRequestParameters.get("state");
        String zip = transactionRequestParameters.get("zip");
        String telno = transactionRequestParameters.get("telno");
        String telnocc = transactionRequestParameters.get("telnocc");
        String month = transactionRequestParameters.get("EXPIRE_MONTH");
        String year = transactionRequestParameters.get("EXPIRE_YEAR");
        String namecp = transactionRequestParameters.get("NAMECP");
        String cccp = transactionRequestParameters.get("CCCP");
        String addrcp = transactionRequestParameters.get("ADDRCP");
        String cardtype = transactionRequestParameters.get("CARDTYPE");
        String bday = transactionRequestParameters.get("DAY");
        String bmonth = transactionRequestParameters.get("MONTH");
        String byear = transactionRequestParameters.get("YEAR");
        String merchantIpAddress = transactionRequestParameters.get("MERCHANTIPADDRESS");
        String birthDay=byear+bmonth+bday;
        if (!cardtype.equals("ANY") && !Functions.getCardType(ccpan).equals(cardtype))
        {
            log.debug("Invalid CARDTYPE");
            transactionLogger.debug("Invalid CARDTYPE");
            error.put("CARDTYPE", "Invalid Paymode or Card Type Selected");
        }
        if (transactionRequestParameters.get("ccid").length() < 3)
        {
            log.debug("Invalid CVV Number");
            transactionLogger.debug("Invalid CVV Number");
            error.put("CVV", "Invalid Card Verification Number");
        }
        String firstSix = Functions.getFirstSix(ccpan);
        String lastfour = Functions.getLastFour(ccpan);

        if (firstSix.equals("") || lastfour.equals(""))
        {
            error.put("Invalid Card", "Invalid card number.");
        }
        if (!Functions.isValid(ccpan))
        {
            log.debug("Invalid Card Number");
            transactionLogger.debug("Invalid Card Number");
            error.put("Card Number", "Invalid Card Number");
        }


        if (namecp != null && cccp != null && addrcp != null)
        {
            if (cccp.equalsIgnoreCase("Y") && namecp.equalsIgnoreCase("Y") && addrcp.equalsIgnoreCase("Y"))
                HRCode = HRCode + "-HRCP";

        }


        String fullname = null;
        fullname = firstname + "" + lastname;
        StringBuffer sbuf = new StringBuffer();

        if (error.size() > 0)
        {
            transactionError.displayErrorsForCommon(error,otherdetails,toid,responseHash,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION);
            return responseHash;
        }


        int aptprompt = 0;
        int memberaptprompt = 0;


        int count = -9999;
        PrintWriter pw = null;
        String query = null;

        CommResponseVO transRespDetails = null;

        String type = "";

        StringWriter sw = new StringWriter();
        boolean authexception = false;
        boolean captureexception = false;
        boolean proofrequired = false;
        String authMessage = "";

        //for proofrequired
        String brandname = "";
        String sitename = "";


        boolean transactionfailed = true;
        int ipcode = -9999;
        long tempcode = -9999;
        String machineid = null;
        int emailfraudcount = 0;
        int machinefraudcount = 0;
        CommRequestVO commRequestVO = null;

        String txnamount = transactionRequestParameters.get("TXN_AMT");
        BigDecimal amount = new BigDecimal(txnamount);
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN); //as amount value in database was round down by mysql while inserting in php page
        txnamount = amount.toString();
        String amountInDb = "";

        String trackingId = null;
        String version = null;
        try
        {
            trackingId = ESAPI.validator().getValidInput("TRACKING_ID", transactionRequestParameters.get("TRACKING_ID"), "Numbers", 10, false);
            version = ESAPI.validator().getValidInput("TMPL_VERSION", transactionRequestParameters.get("TMPL_VERSION"), "SafeString", 50, true);
        }
        catch (ValidationException e)
        {
            log.error("Invalid TrackingID or Temlate Version::::", e);
            transactionLogger.error("Invalid TrackingID or Temlate Version::::", e);
        }


        String transactionDescription = "";
        String transactionOrderDescription = "";
        String transactionMessage = "Connection Failure";
        String transactionStatus = "N";
        String mailtransactionStatus = "Failed";
        String captureStatus = null;
        String clkey = "";
        boolean card_transaction_limit = false;
        boolean card_check_limit = false;
        String checksumAlgo = null;

        if (version == null)
            version = "";
        String notifyEmails = "";

        String sql = "";
        String company_name = null;
        String currency = null;
        boolean vbv = false;
        boolean custremindermail = false;
        boolean hrParameterised = false;

        StringBuffer subject = new StringBuffer();
        //StringBuffer body = new StringBuffer();
        StringBuffer body = new StringBuffer();
        StringBuffer merchantbody = new StringBuffer();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        String tredtime = null;
        String ipaddress = "N/A";
        String boilname = getBoiledName(cardholder);
        String billingDescriptor = null;

        AbstractPaymentGateway pg = null;

        String accountId = "";
        String fromid = null;

        try
        {
            count = Integer.parseInt(String.valueOf(transactionAttributes.get("noOfClients") == null ? "0" : String.valueOf(transactionAttributes.get("noOfClients"))));
            count++;
            attributeHash.put("noOfClients", String.valueOf(count));
            log.debug("Execute select query for gateway Account Service");
            transactionLogger.debug("Execute select query for gateway Account Service");

            Hashtable transHash = transaction.getTransactionDetailsForCommon(trackingId);

            if (!transHash.isEmpty())*//*(rs.next())*//*
            {
                memberId = Integer.parseInt((String)transHash.get("toid"));
                amountInDb = (String) transHash.get("amount");
                accountId = (String) transHash.get("accountid");
                fromid = (String) transHash.get("fromid");
                transactionRequestParameters.put("accountid", accountId);



                try
                {
                    aptprompt = GatewayAccountService.getGatewayAccount(accountId).getHighRiskAmount(); // get default HR amount for this gateway
                    pg = AbstractPaymentGateway.getGateway(accountId);

                }
                catch (SystemError systemError)
                {
                    log.error("System Error:::::::::", systemError);
                    transactionLogger.error("System Error:::::::::", systemError);
                    transactionError.displayErrorPageForCommon(otherdetails,"System Error",toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,responseHash);
                    return responseHash;

                }

                transactionDescription = (String) transHash.get("description");
                transactionOrderDescription = (String) transHash.get("orderdescription");

                if (transactionOrderDescription == null)
                    transactionOrderDescription = "";

                String dtstamp = (String) transHash.get("dtstamp");
                ipaddress = (String) transHash.get("ipaddress");
                templateCurrency = (String) transHash.get("currency");
                templateAmount = (String) transHash.get("amount");


                if (!transHash.get("status").equals("begun"))
                {
                    log.debug("inside if for status!=begun");
                    transactionLogger.debug("inside if for status!=begun");
                    String table = "ERROR!!! Your Transaction is already being processed. This can occur if you clicked on the back button and tried to submit this Transaction again. The transaction may succeed or fail, however the status of the Transaction will have to be set manually. Please contact the Merchant to verify the status of the transaction with the following reference numbers and inform him of this message. PLEASE DO NOT TRY to execute this transaction once more from the beginning, or you may end up charging your card twice.<br><br> Please visit at" + ApplicationProperties.getProperty("COMPANY_SUPPORT_URL") + " to know more about the reason for this error.";
                    transactionError.displayErrorPageForCommon(otherdetails,table,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,responseHash);
                    return responseHash;

                }
                else if (!paymentChecker.isWhitelistedCardnumber(toid, accountId, ccpan))
                {
                    String message = "ERROR!!! Your card number is not whitelisted .<BR><BR>";
                    otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
                    otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
                    otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
                    otherdetails.put("MESSAGE", message);
                    responseHash.put("status", "error");
                    try
                    {

                        auditTrailVO.setActionExecutorId(toid);
                        auditTrailVO.setActionExecutorName("Customer");
                        int actionEntry = actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_VALIDATION_FAILED, ActionEntry.ACTION_VALIDATION_FAILED, transRespDetails, commRequestVO,auditTrailVO);
                        paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(),String.valueOf(amount),machineid,null,null,null,String.valueOf(trackingId));
                        StringBuilder errorbuilder = new StringBuilder();
                        errorbuilder.append("<form name=\"error\" action=\""+ERRORSERVLET+"?ctoken"+ctoken+"\" method=\"post\" >");
                        errorbuilder.append("<input type=\"hidden\" name=\"ERROR\" value=\"" + URLEncoder.encode(Template.getErrorPage("" + memberId, otherdetails)) + "\">");
                        errorbuilder.append("</form>");
                        errorbuilder.append("<script language=\"javascript\">");
                        errorbuilder.append("document.error.submit();");
                        errorbuilder.append("</script>");
                        errorbuilder.append("</body>");
                        errorbuilder.append("</html>");

                        responseHash.put("status", "error");
                        responseHash.put("redirect", errorbuilder.toString());
                    }
                    catch (PZDBViolationException e)
                    {
                        log.error("PZDBViolationException while placing transaction via common process::::",e);
                        transactionLogger.error("PZDBViolationException while placing transaction via common process::::",e);
                        PZExceptionHandler.handleDBCVEException(e,toid,"Sale");
                    }
                    return responseHash;
                }
                else
                {

                    String expdate = transactionRequestParameters.get("EXPIRE_MONTH") + "/" + transactionRequestParameters.get("EXPIRE_YEAR");
                    Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                    StringBuffer sb = new StringBuffer("update transaction_common set");
                    sb.append(" ccnum='" + pzEncryptor.encryptPAN(ccpan) + "'");
                    sb.append(" , firstname='" + ESAPI.encoder().encodeForSQL(me, firstname) + "'");
                    sb.append(" , lastname='" + ESAPI.encoder().encodeForSQL(me, lastname) + "'");
                    sb.append(", name='" + ESAPI.encoder().encodeForSQL(me, fullname) + "'");
                    sb.append(" , street='" + ESAPI.encoder().encodeForSQL(me, street) + "'");
                    sb.append(" , country ='" + ESAPI.encoder().encodeForSQL(me, country) + "'");
                    sb.append(" , city ='" + ESAPI.encoder().encodeForSQL(me, city) + "'");
                    sb.append(" , state ='" + ESAPI.encoder().encodeForSQL(me, state) + "'");
                    sb.append(" , zip ='" + ESAPI.encoder().encodeForSQL(me, zip) + "'");
                    sb.append(" , telno ='" + ESAPI.encoder().encodeForSQL(me, telno) + "'");
                    sb.append(" , cccp ='" + ESAPI.encoder().encodeForSQL(me, cccp) + "'");
                    sb.append(" , namecp ='" + ESAPI.encoder().encodeForSQL(me, namecp) + "'");
                    sb.append(" , addrcp ='" + ESAPI.encoder().encodeForSQL(me, addrcp) + "'");
                    sb.append(" , telnocc ='" + ESAPI.encoder().encodeForSQL(me, telnocc) + "'");

                    sb.append(" , expdate='" + pzEncryptor.encryptExpiryDate(expdate) + "'");
                    sb.append(" ,cardtype='" + ESAPI.encoder().encodeForSQL(me, Functions.getCardType(ccpan)) + "'");
                    if (emailaddr != null)
                        sb.append(" , emailaddr='" + emailaddr + "'");
                    merchantId = pg.getMerchantId();
                    displayName = pg.getDisplayName();
                    sb.append(", status='authstarted'  where trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingId));
                    //Database.executeUpdate(sb.toString(), con);
                    transaction.executeUpdate(sb.toString());
                    //Database.commit(con);

                    transaction.updateBinDetails(trackingId,ccpan,accountId,emailaddr,boilname);

                    int actionEntry = actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, transRespDetails, commRequestVO,auditTrailVO);

                    // End : Added for Action and Status Entry in Action History table


                    /////*//*******************  risk checking is started *********************************************//*/

                    //get ip code
                    tempcode = riskCheckers.getIPCode(ipaddress);

                    //check whether this is blocked email
                    riskCheckers.checkBlockedEmail(proofrequired,emailaddr,HRCode);


                    //check whether this is blocked Domain
                    riskCheckers.checkBlockedDomain(proofrequired,emailaddr,HRCode);


                    //check whether this is blocked IP
                    riskCheckers.checkBlockedIP(proofrequired,ipaddress,tempcode,HRCode);


                    //country ckeck start
                    //check for different country
                    riskCheckers.checkDifferentCountry(proofrequired,country,HRCode,tempcode,diffCountry);

                    //country check ends
                    //check whether country is blocked
                    riskCheckers.checkBlockedCountry(proofrequired,country,HRCode);


                    //ipcheck starts
                    riskCheckers.checkEmail(proofrequired, machineid, HRCode, ccpan, boilname, repeatEmail, "transaction_common");

                    //Email check ends
                    //Machine check starts

                    //machineid = getCookie(transactionRequestParameters, res);

                    if (machineid != null)
                    {
                        riskCheckers.checkMachine(proofrequired,machineid,HRCode,ccpan,boilname,repeatmachine,"transaction_common");

                    }
                    else
                    {  //if cookie is still not set for this machine
                        //generate random number and assign it to machine id
                        Random rand = new Random();
                        rand.setSeed((new java.util.Date()).getTime());//set seed to current time.
                        Integer i = new Integer(rand.nextInt());
                        machineid = i.toString();

                        Cookie c = new Cookie("mid", machineid);
                        c.setMaxAge(2678400);
                        //res.addCookie(c);
                    }
                    //Machine check ends
                    /////*//*******************  risk checking is done *********************************************//*/
                }
            }
            else
            {
                String message = "ERROR!!! We have encountered an internal error while processing your transactionRequestParameters. Please visit at" + ApplicationProperties.getProperty("COMPANY_SUPPORT_URL") + " and create Support Request to know the status of this transaction.<BR><BR>";
                transactionError.displayErrorPageForCommon(otherdetails,message,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,responseHash);
                return responseHash;

            }
        }
        *//*catch (SQLException sqle)
        {
            log.error("SQLException", sqle);
            transactionLogger.error("SQLException", sqle);
            //sqle.printStackTrace();

        }*//*
        catch (SystemError s)
        {
            log.error("System Error::::::::::::", s);
            transactionLogger.error("System Error::::::::::::", s);
            //s.printStackTrace();

        }//try catch ends
        catch (PZDBViolationException e)
        {
            log.error("System Error::::::::::::", e);
            transactionLogger.error("System Error::::::::::::", e);
        }

        StringBuffer queryString = new StringBuffer("");

        for (Map.Entry<String, String> stringEntry : transactionRequestParameters.entrySet())
        {
            String name = stringEntry.getKey();
            String value = null;
            if (stringEntry.getValue() != null)
            {
                value = ESAPI.encoder().encodeForHTML(stringEntry.getValue());
            }
            try
            {
                value = ESAPI.encoder().encodeForURL(value);
            }
            catch (EncodingException e)
            {

            }


            paramHash.put(name, value);

            if (name.startsWith("TMPL_"))
                queryString.append("&").append(name).append("=").append(value);
        }


        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        CommMerchantVO commMerchantAccountVO = new CommMerchantVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        tredtime = dateFormat.format(calendar.getTime());


        // Quering Members Table to find out whether the member is a service based
        // products based member.

        try
        {

            Hashtable memberDetails = merchants.getMemberDetailsForTransaction(toid);
            String isService = "";

            if (!memberDetails.isEmpty())*//*(rs.next())*//*
            {
                memberaptprompt = Integer.parseInt((String) memberDetails.get("aptprompt"));
                notifyEmails = (String) memberDetails.get("notifyemail");
                company_name = (String) memberDetails.get("company_name");
                isService = (String) memberDetails.get("isservice");
                hrAlertProof = (String) memberDetails.get("hralertproof");
                datamismatchproof = (String) memberDetails.get("datamismatchproof");
                brandname = (String) memberDetails.get("brandname");
                sitename = (String) memberDetails.get("sitename");
                chargepercent = (String) memberDetails.get("chargeper");
                dbTaxPercent = (String) memberDetails.get("taxper");
                fixamount = (String) memberDetails.get("fixamount");
                clkey = (String) memberDetails.get("clkey");
                card_transaction_limit = (Boolean)memberDetails.get("card_transaction_limit");
                card_check_limit = (Boolean)memberDetails.get("card_check_limit");
                vbv = (Boolean)memberDetails.get("vbv");
                custremindermail = (Boolean)memberDetails.get("custremindermail");
                hrParameterised = (Boolean)memberDetails.get("hrparameterised");
                checksumAlgo = (String) memberDetails.get("checksumalgo");
                currency = pg.getCurrency();


                if (clkey == null || clkey.trim().equals(""))
                    throw new SystemError("Could not load Key");

                //autoredirect=rs.getString("autoredirect");

                if (brandname.trim().equals(""))
                    brandname = company_name;

                if (sitename.trim().equals(""))
                    sitename = company_name;


                if (chargepercent == null)
                    charge = new BigDecimal(defaultchargepercent);
                else
                    charge = new BigDecimal(chargepercent);

                if (dbTaxPercent == null)
                    if ("USD".equals(currency))
                        taxPercentage = new BigDecimal(USD_defaulttaxpercent);
                    else
                        taxPercentage = new BigDecimal(INR_defaulttaxpercent);
                else
                    taxPercentage = new BigDecimal(dbTaxPercent);


            }
            else
            {
                throw new SystemError("Members Details not found" );
            }

            log.debug("verify" + Checksum.verifyChecksumV2(memberId + "", transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumVal, checksumAlgo));
            log.debug("verify+Checksum.verifyChecksumV2" + memberId + "==" + transactionDescription + "=======" + new BigDecimal(amountInDb).doubleValue() + "========" + clkey + "=====" + checksumVal + checksumAlgo);

            transactionLogger.debug("verify" + Checksum.verifyChecksumV2(memberId + "", transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumVal, checksumAlgo));
            transactionLogger.debug("verify+Checksum.verifyChecksumV2" + memberId + "==" + transactionDescription + "=======" + new BigDecimal(amountInDb).doubleValue() + "========" + clkey + "=====" + checksumVal + checksumAlgo);
            if (!Checksum.verifyChecksumV2(memberId + "", transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumVal, checksumAlgo))
            {

                String message = "ERROR!!! checksum mismatched. Please visit at" + ApplicationProperties.getProperty("COMPANY_SUPPORT_URL") + " and create Support Request to know the status of this transaction.<BR><BR>";
                transactionError.displayErrorPageForCommon(otherdetails,message,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,responseHash);
                return responseHash;


            }
            txnamount = amountInDb;
            boolean cardLimitsExceeded = false;
            String cardLimitErrorCode = "";

            if (card_transaction_limit)
            {
                boolean cardTransactionLimit = LimitChecker.checkCardLimit(String.valueOf(memberId), accountId, firstSix, lastfour);
                if (cardTransactionLimit)
                {
                    cardLimitsExceeded = true;
                    cardLimitErrorCode = "C1";
                }
            }

            if (card_check_limit)
            {
                boolean cardAmountLimit = limitChecker.checkCardAmountLimit(String.valueOf(memberId), accountId, amountInDb, firstSix, lastfour);
                if (cardAmountLimit)
                {
                    cardLimitsExceeded = true;
                    cardLimitErrorCode = "C2";
                }

            }

            String cardLimitMessage = "Limits for the card have exceeded " + cardLimitErrorCode + ".";


            try
            {
                //set mail body before doing auth
                *//*body.append("Member ID:" + memberId + "\r\n");
                body.append("Company Name:" + company_name + "\r\n\r\n");
                body.append("Description:" + transactionDescription + "\r\n");
                body.append("Order Description:" + transactionOrderDescription + "\r\n");
                if (templateCurrency != null && !currency.equals(templateCurrency))
                    body.append("Amount:" + currency + "" + txnamount + " ( approximately" + templateCurrency + "" + templateAmount + " )" + "\r\n");
                else
                    body.append("Amount:" + currency + "" + txnamount + "\r\n");*//*

                StringBuffer sb = new StringBuffer();
                if (!cardLimitsExceeded)
                {
                    if (isService.equals("Y"))
                        type = "sale";
                    else
                        type = "auth";
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    String merctId = account.getMerchantId();
                    String alias = account.getAliasName();
                    String username = account.getFRAUD_FTP_USERNAME();
                    String password = account.getFRAUD_FTP_PASSWORD();
                    String displayname = account.getDisplayName();

                    cardDetailsVO.setCardNum(ccpan);
                    cardDetailsVO.setcVV(ccid);
                    cardDetailsVO.setExpMonth(month);
                    cardDetailsVO.setExpYear(year);
                    cardDetailsVO.setCardType(Functions.getCardType(ccpan));

                    addressDetailsVO.setFirstname(firstname);
                    addressDetailsVO.setLastname(lastname);
                    addressDetailsVO.setCity(city);
                    addressDetailsVO.setCountry(country);
                    addressDetailsVO.setIp(merchantIpAddress);
                    addressDetailsVO.setPhone(telno);
                    addressDetailsVO.setEmail(emailaddr);
                    addressDetailsVO.setState(state);
                    addressDetailsVO.setStreet(street);
                    addressDetailsVO.setZipCode(zip);
                    addressDetailsVO.setBirthdate(birthDay);

                    transDetailsVO.setAmount(String.valueOf(txnamount)); //Amount * 100 according to the docs
                    transDetailsVO.setCurrency(currency);
                    transDetailsVO.setOrderId(trackingId);
                    transDetailsVO.setOrderDesc(transactionDescription);

                    CommMerchantVO merchantAccountVO = new CommMerchantVO();
                    merchantAccountVO.setMerchantId(merchantId);
                    merchantAccountVO.setPassword(password);
                    merchantAccountVO.setMerchantUsername(username);
                    merchantAccountVO.setDisplayName(displayname);
                    merchantAccountVO.setAliasName(alias);


                    merchantAccountVO.setMerchantId(merctId);
                    merchantAccountVO.setPassword(password);
                    merchantAccountVO.setMerchantUsername(username);
                    merchantAccountVO.setDisplayName(displayname);
                    merchantAccountVO.setAliasName(alias);

                    commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accountId));

                    commRequestVO.setAddressDetailsVO(addressDetailsVO);
                    commRequestVO.setCardDetailsVO(cardDetailsVO);
                    commRequestVO.setCommMerchantVO(merchantAccountVO);
                    commRequestVO.setTransDetailsVO(transDetailsVO);

                    setTransactionVOParamsExtension(commRequestVO, transactionRequestParameters);

                    String status = "";
                    Connection connection = Database.getConnection();
                    try
                    {
                        if (type.equals("auth"))
                        {

                            actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, null, commRequestVO,auditTrailVO);
                            transRespDetails = (CommResponseVO) pg.processAuthentication(trackingId, commRequestVO);
                            status = "authsuccessful";
                        }
                        else if (type.equals("sale"))
                        {

                            actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.STATUS_CAPTURE_STARTED, null, commRequestVO,auditTrailVO);
                            transRespDetails = (CommResponseVO) pg.processSale(trackingId, commRequestVO);
                            status = "capturesuccess";
                            log.debug("in process sale...1");
                            transactionLogger.debug("in process sale...1");
                        }
                        log.debug("in process sale after else if...1");
                        transactionLogger.debug("in process sale after else if...1");
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success")){
                            String binUpdateQuery = "update bin_details set isSuccessful='Y' where icicitransid=?";
                            PreparedStatement pPStmt = connection.prepareStatement(binUpdateQuery);
                            pPStmt.setString(1,trackingId);
                            pPStmt.executeUpdate();
                        }

                    }
                    catch (Exception e)
                    {
                        log.debug("in process sale...in catch....");
                        transactionLogger.debug("in process sale...in catch....");
                        String message = "ERROR!!! We have encountered an internal error while processing your transactionRequestParameters. Please visit at" + ApplicationProperties.getProperty("COMPANY_SUPPORT_URL") + " and create Support Request to know the status of this transaction.<BR><BR>";
                        transactionError.displayErrorPageForCommon(otherdetails,message,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,responseHash);
                        transaction.updateTransactionStatusCommon("authfailed",trackingId,"Bank Connectivity issue");
                        // Start : Added for Action and Status Entry in Action History table
                        int actionEntry = actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO);
                        // End : Added for Action and Status Entry in Action History table
                        return responseHash;

                    }
                    finally {
                        connection.close();
                    }

                    Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                    sb.append("update transaction_common set");
                    String respDesc = ESAPI.encoder().encodeForSQL(me,transRespDetails.getDescription());
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        sb.append(" status = '" + status + "' ,remark = '" + respDesc + "'");
                        transactionStatus = "Y";
                        if(functions.isValueNull(transRespDetails.getDescriptor()))
                        {
                            billingDescriptor=transRespDetails.getDescriptor();
                        }
                        else
                        {
                            billingDescriptor=pg.getDisplayName();
                        }

                    }
                    else
                    {
                        sb.append(" status = 'authfailed',remark = '" + respDesc + "'");
                        transactionStatus = "N";
                    }
                    if (type.equals("sale"))
                    {
                        sb.append(", captureamount=" + txnamount);
                    }

                    if (transactionStatus.equals("Y"))
                    {
                        // Start : Added for Action and Status Entry in Action History table

                        if (status.equals("capturesuccess"))
                        {
                            int actionEntryNew = actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                        }
                        else if (status.equals("authsuccessful"))
                        {
                            int actionEntryNew = actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                        }

                        // End : Added for Action and Status Entry in Action History table
                    }
                    else
                    {
                        // Start : Added for Action and Status Entry in Action History table
                        int actionEntryNew = actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO,null);
                        // End : Added for Action and Status Entry in Action History table
                    }

                    if ((transRespDetails.getStatus().trim()).equals("fail"))
                    {
                        authMessage = "Failed";
                    }
                    if ((transRespDetails.getStatus().trim()).equals("success"))
                    {
                        authMessage = "Approved";
                    }
                }
                else
                {
                    transRespDetails = new CommResponseVO();
                    transRespDetails.setStatus("fail");
                    transRespDetails.setDescription(cardLimitMessage);
                    sb.append("update transaction_common set status = 'failed',remark = '" + cardLimitMessage + "'");
                    transactionStatus = "N";
                    authMessage = "Failed";
                    actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, commRequestVO,auditTrailVO,null);
                }


                if (Functions.parseData(authMessage) != null)
                    transactionMessage = authMessage;



                sendFailMails(transactionStatus,transactionMessage,transRespDetails,trackingId,transactionAttributes,attributeHash,transactionDescription);


                if (!HRCode.equals(""))
                    HRCode = HRCode.substring(1);

                //also update charge value as we decided to charge merchant with the percentage that existed at the time of authorisation
                //not at the time of settlement.
                sb.append(",hrcode='" + HRCode + "' ,paymentid='" + transRespDetails.getTransactionId() + "' ");
                sb.append(" where trackingid =" + trackingId);

                int result = transaction.executeUpdate(sb.toString());*//*Database.executeUpdate(sb.toString(), con);*//*

                if (result != 1)
                {
                    //Database.rollback(con);
                    log.debug("Leaving do service with error in result, result is" + result + " and not 1");
                    transactionLogger.debug("Leaving do service with error in result, result is" + result + " and not 1");

                    Mail.sendAdminMail("Exception while Updating status", "\r\n\r\nException has occured while updating status for tracking id=" + trackingId + "\r\nAuth code=" + transRespDetails.getStatus() + "\r\nAuth message=" + transactionMessage);
                    throw new SystemError("Error in Query, result not 1 result is" + result + "  Query :" + sb.toString());
                }


                //mail body to Merchant	and pz

                if (transactionStatus.equals("Y"))
                {
                    transactionfailed = false;
                    mailtransactionStatus = "Successful";
                }
                else
                {
                    mailtransactionStatus = "failed";
                    transactionfailed = true;
                }

            }
            catch (Exception nex)
            {
                pw = new PrintWriter(sw);
                nex.printStackTrace(pw);

                authexception = true;
                subject.append("Exception Occured while Auth");
                body.append("Exception Occured while Auth" + sw.toString() + "\r\n\r\n");

                String exp = nex.getMessage();

                int ind = -1;

                if (exp != null)
                    ind = exp.indexOf("#1234#");

                if (exp != null && ind != -1)
                {
                    transaction.updateTransactionStatusCommon("authfailed",trackingId);
                    exp = exp.substring(ind + 5);

                    // Start : Added for Action and Status Entry in Action History table
                    int actionEntry = actionEntry(trackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO,null);
                    // End : Added for Action and Status Entry in Action History table
                }

                log.debug("Error while communicating to bank for trackingId =" + trackingId + ", error is :" + nex.toString());
                log.error("error", nex);

                transactionLogger.debug("Error while communicating to bank for trackingId =" + trackingId + ", error is :" + nex.toString());
                transactionLogger.error("error", nex);

            }//try catch ends

            SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
            sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION,trackingId,mailtransactionStatus,null,billingDescriptor);

            chargeamount = amount.multiply(charge.multiply(new BigDecimal(1 / 10000.00)));
            chargeamount = chargeamount.add(new BigDecimal(fixamount));
            chargeamount = chargeamount.setScale(2, BigDecimal.ROUND_HALF_UP);
            String chargeamt = chargeamount.toString();
            String reason = "";


            if (transRespDetails.getStatus() != null && !(transRespDetails.getStatus()).equalsIgnoreCase("success"))
                transactionMessage = "Failed";

            String checksum;
            if (version.equals("2"))
                checksum = Checksum.generateChecksumV3(String.valueOf(memberId), transactionStatus, transactionMessage, transactionDescription, "" + txnamount, chargeamt, clkey, checksumAlgo);
            else
                checksum = Checksum.generateChecksumV1(String.valueOf(memberId), transactionStatus, transactionMessage, transactionDescription, "" + txnamount, clkey, checksumAlgo);

            log.debug("checksum         " + checksum);
            transactionLogger.debug("checksum         " + checksum);
            String message = "";
            if (!reason.equals(""))
            {
                message = transactionMessage + " (" + reason + " )";
            }


            else
            {
                message = transactionMessage + " (" + transRespDetails.getDescription() + " )";
            }

            StringBuffer postPayStrBuf = new StringBuffer();
            //postPayStrBuf.append("<form name=\"postpay\" action=\"" + PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "" + POSTURL + "\" method=\"post\" >");
            postPayStrBuf.append("<form name=\"postpay\" action=\""+ POSTURL+"?ctoken="+ctoken + "\" method=\"post\" >");
            postPayStrBuf.append("<input type=\"hidden\" name=\"status\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(transactionStatus) + "\">");
            postPayStrBuf.append("<input type=\"hidden\" name=\"message\" value=\"" + message + "\">");

            postPayStrBuf.append("<input type=\"hidden\" name=\"amount\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(txnamount) + "\">");
            postPayStrBuf.append("<input type=\"hidden\" name=\"chargeamt\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(chargeamt) + "\">");
            postPayStrBuf.append("<input type=\"hidden\" name=\"checksum\" value=\"" + checksum + "\">");
            postPayStrBuf.append("<input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\">");


            for (Map.Entry<String, String> stringEntry : transactionRequestParameters.entrySet())
            {
                String name = stringEntry.getKey();
                String value = ESAPI.encoder().encodeForHTMLAttribute(stringEntry.getValue());
                if(name.equals("HEADER"))
                {
                    postPayStrBuf.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" +value+ "\">");
                }
                else
                {
                    postPayStrBuf.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(value) + "\">");
                }
            }
            postPayStrBuf.append("</form>");
            postPayStrBuf.append("<script language=\"javascript\">");
            postPayStrBuf.append("document.postpay.submit();");
            postPayStrBuf.append("</script>");
            postPayStrBuf.append("</body>");
            postPayStrBuf.append("</html>");
            responseHash.put("status", "success");
            responseHash.put("redirect", postPayStrBuf.toString());

            if (authexception || captureexception)
            {

                log.debug("calling SendMAil for ");
                transactionLogger.debug("calling SendMAil for ");
                //Mail.sendAdminMail(subject.toString(), body.toString());
                log.debug("called SendMAil for ");
                transactionLogger.debug("called SendMAil for ");
            }

            if (authexception)
            {
                log.debug("calling SendMAil for Merchant");
                transactionLogger.debug("calling SendMAil for Merchant");
                //Mail.sendmail(notifyEmails, fromAddress, "No Response From Bank.", "It seems that the Transaction Authorization has failed due to some error.\r\n\r\n" + merchantbody.toString());
                log.debug("called SendMAil for Merchant");
                transactionLogger.debug("called SendMAil for Merchant");
            }
            ccpan = null;
            month = null;
            year = null;


            if (!transactionfailed && custremindermail)
            {
                // Mail.sendHtmlMail(emailaddr, fromAddress, null, null, "Your transaction at" + sitename, CUSTOMERNOTIFICATION);

            }
        }
        catch (SystemError sye)
        {
            if (count >= 0)
            {
                count = Integer.parseInt((String) transactionAttributes.get("noOfClients"));
                count--;
                attributeHash.put("noOfClients", String.valueOf(count));
            }

            sye.printStackTrace();
            log.error("System Error:::::", sye);
            transactionLogger.error("System Error:::::", sye);
            //throw new ServletException(sye.toString());
        }
        catch (Exception nex)
        {
            log.error("Internal System Error:", nex);
            transactionLogger.error("Internal System Error:", nex);
            pw = new PrintWriter(sw);
            nex.printStackTrace(pw);

            subject.append(" / Exception in");
            body.append("\r\nFollowing Exception Occured" + sw.toString() + "\r\n\r\n");

            try
            {
                log.debug("calling SendMAil for ");
                transactionLogger.debug("calling SendMAil for ");
                Mail.sendAdminMail(subject.toString(), body.toString());

            }
            catch (SystemError sye)
            {
                log.error("System Error", sye);
                transactionLogger.error("System Error", sye);
                if (count >= 0)
                {
                    count = Integer.parseInt((String) transactionAttributes.get("noOfClients"));
                    count--;
                    attributeHash.put("noOfClients", String.valueOf(count));
                }

                sye.printStackTrace();
                log.error("SystemError Occur::", sye);
                transactionLogger.error("SystemError Occur::", sye);
                //throw new ServletException(sye.toString());

            }
            finally
            {
                String message = "";

                if (!authexception)
                    message = "<b>ERROR!!!</b> We have encountered an internal error while processing your transactionRequestParameters. Please visit at" + ApplicationProperties.getProperty("COMPANY_SUPPORT_URL") + " and create Support Request to know the status of this transaction.<BR>";
                else
                    message = "<b>ERROR!!!</b> We have encountered an internal error while processing your transactionRequestParameters. This happened because of a connectivity issue with the Credit Card Processor. Please retry this transaction afresh. If this continues to happen, Please visit at" + ApplicationProperties.getProperty("COMPANY_SUPPORT_URL") + " and create Support Request.<BR>";

                otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
                otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
                otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
                otherdetails.put("MESSAGE", message);
                //otherdetails.put("HEADER",transactionAttributes.get("HEADER"));
                try
                {
                    StringBuffer errorPage = new StringBuffer();

                    //RequestDispatcher rd=transactionRequestParameters.getRequestDispatcher("/icici/servlet/Error.jsp");
                    //rd.forward(transactionRequestParameters,res);
                    errorPage.append("<form name=\"error\" action=\""+ERRORSERVLET+"?ctoken"+ctoken+"\" method=\"post\" >");
                    errorPage.append("<input type=\"hidden\" name=\"ERROR\" value=\"" + URLEncoder.encode(Template.getErrorPage("" + memberId, otherdetails)) + "\">");
                    errorPage.append("</form>");
                    errorPage.append("<script language=\"javascript\">");
                    errorPage.append("document.error.submit();");
                    errorPage.append("</script>");
                    errorPage.append("</body>");
                    errorPage.append("</html>");

                    responseHash.put("status", "error");
                    responseHash.put("redirect", errorPage.toString());
                }
                catch (Exception e)
                {
                    log.error("Exception occur", e);
                    transactionLogger.error("Exception occur", e);
                }
            }
        }
        finally
        {
            if (count >= 0)
            {
                count = Integer.parseInt((String) transactionAttributes.get("noOfClients"));
                count--;
                attributeHash.put("noOfClients", String.valueOf(count));
            }
            //Database.closeConnection(con);
        }
        return responseHash;
    }*/

public DirectCommResponseVO transactionAPI(CommonValidatorVO commonValidatorVO)
{
    CommResponseVO commResponse = null;
    AuditTrailVO auditTrailVO = new AuditTrailVO();
    StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
    PaymentChecker paymentChecker = new PaymentChecker();
    Transaction transaction = new Transaction();
    ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
    FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
    CommRequestVO commRequestVO = null;
    AbstractPaymentGateway pg = null;
    SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
    DirectCommResponseVO directCommResponseVO = new DirectCommResponseVO();

    String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
    String cardHolderIpAddress = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
    String header = commonValidatorVO.getTransDetailsVO().getHeader();
    String status = "";
    String apiStatus = "";
    String type = "";
    String transactionStatus = "";
    String statusMsg = "";
    String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
    String amount = commonValidatorVO.getTransDetailsVO().getAmount();
    String ccnum = commonValidatorVO.getCardDetailsVO().getCardNum();
    String cvv = commonValidatorVO.getCardDetailsVO().getcVV();
    String expmonth = commonValidatorVO.getCardDetailsVO().getExpMonth();
    String expyear = commonValidatorVO.getCardDetailsVO().getExpYear();

    String firstname = commonValidatorVO.getAddressDetailsVO().getFirstname();
    String lastname = commonValidatorVO.getAddressDetailsVO().getLastname();
    String street = commonValidatorVO.getAddressDetailsVO().getStreet();
    String country = commonValidatorVO.getAddressDetailsVO().getCountry();
    String city = commonValidatorVO.getAddressDetailsVO().getCity();
    String state = commonValidatorVO.getAddressDetailsVO().getState();
    String zip = commonValidatorVO.getAddressDetailsVO().getZipCode();
    String telno = commonValidatorVO.getAddressDetailsVO().getPhone();
    String telnocc = commonValidatorVO.getAddressDetailsVO().getTelnocc();
    String email = commonValidatorVO.getAddressDetailsVO().getEmail();
    String boiledname = commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname();
    String ipaddress = commonValidatorVO.getAddressDetailsVO().getIp();
    String currency = commonValidatorVO.getMerchantDetailsVO().getCurrency();
    String description = commonValidatorVO.getTransDetailsVO().getOrderId();
    String displayName = "";
    String birthdate = commonValidatorVO.getAddressDetailsVO().getBirthdate();
    String language = commonValidatorVO.getAddressDetailsVO().getLanguage();
    String paymentType = commonValidatorVO.getPaymentType();
    String isRecurring = "";
    String recInterval = "";
    String recFrequency = "";
    String recRunDate = "";
    String isAutomaticRecurring = commonValidatorVO.getTerminalVO().getIsRecurring();
    String isManualRecurring = commonValidatorVO.getTerminalVO().getIsManualRecurring();
    String recurringType = "";
    String terminalid = commonValidatorVO.getTerminalId();
    String responseLength = "";
    String token = "";
    String mailtransactionStatus = "";
    String billingDescriptor = null;
    String generatedBy = commonValidatorVO.getMerchantDetailsVO().getLogin();

    String iban = commonValidatorVO.getCardDetailsVO().getIBAN();
    String bic = commonValidatorVO.getCardDetailsVO().getBIC();
    String mandateId = commonValidatorVO.getCardDetailsVO().getMandateId();

    if (commonValidatorVO.getResponseLength() != null)
    {
        responseLength = commonValidatorVO.getResponseLength();
    }

    CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

    if (commonValidatorVO.getRecurringBillingVO() != null)
    {
        isRecurring = commonValidatorVO.getRecurringBillingVO().getRecurring();
        recInterval = commonValidatorVO.getRecurringBillingVO().getInterval();
        recFrequency = commonValidatorVO.getRecurringBillingVO().getFrequency();
        recRunDate = commonValidatorVO.getRecurringBillingVO().getRunDate();
    }

    if (header != null)
    {
        if (header.contains("VirtualSingleCall"))
        {
            auditTrailVO.setActionExecutorName("Merchant VT");
        }
        else
        {
            auditTrailVO.setActionExecutorName("Merchant DK");
        }

    }
    auditTrailVO.setActionExecutorId(toid);

    int trackingId = 0;
    int detailId = 0;
    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
    directCommResponseVO.setDescriptor(account.getDisplayName());
    try
    {
        CommRequestVO commRequestVO1 = new CommRequestVO();
        CommAddressDetailsVO genericAddressDetailsVO1 = new CommAddressDetailsVO();
        //commonValidatorVO.getAddressDetailsVO().setIp(cardHolderIpAddress);
        commRequestVO1.setAddressDetailsVO(genericAddressDetailsVO1);

        if ("PFS".equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getFromtype()))
        {
            transDetailsVO.setResponseHashInfo(UUID.randomUUID().toString());//UUID set for PFS
        }
        commRequestVO1.setTransDetailsVO(transDetailsVO);
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        commCardDetailsVO.setIBAN(commonValidatorVO.getCardDetailsVO().getIBAN());
        commCardDetailsVO.setBIC(commonValidatorVO.getCardDetailsVO().getBIC());
        commCardDetailsVO.setMandateId(commonValidatorVO.getCardDetailsVO().getMandateId());

/*
            if (commonValidatorVO.getMerchantDetailsVO().getBinService().equalsIgnoreCase("Y"))
            {
                String firstSix = "";
                if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
                {
                    firstSix = functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum());
                }

                BinResponseVO binResponseVO = new BinResponseVO();
                binResponseVO = functions.getBinDetails(firstSix,commonValidatorVO.getMerchantDetailsVO().getCountry());
                commonValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
                commonValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
                commonValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
                commonValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
                commonValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
                commonValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
                commonValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
                commonValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());
                commonValidatorVO.getCardDetailsVO().setCountryName(binResponseVO.getCountryname());
                commonValidatorVO.getCardDetailsVO().setBankName(binResponseVO.getBank());

            }
*/
        commRequestVO1.setCardDetailsVO(commCardDetailsVO);
        commRequestVO1.getAddressDetailsVO().setCardHolderIpAddress(cardHolderIpAddress);
        trackingId = paymentManager.insertAuthStartedForCommon(commonValidatorVO, ActionEntry.STATUS_AUTHORISTION_STARTED);

        actionEntry(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponse, commRequestVO1, auditTrailVO, null);


        directCommResponseVO.setTrackingid(String.valueOf(trackingId));

        //commRequestVO1=null;
        transactionLogger.debug("online fraud check---" + commonValidatorVO.getMerchantDetailsVO().getOnlineFraudCheck());
        if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getOnlineFraudCheck()))
        {
            //Make Online Fraud Checking Using Payment Fraud Processor

            FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);

            if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
            {

                commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                commonValidatorVO.setTime(Functions.convertDateDBFormat(new Date()));
                commonValidatorVO.getMerchantDetailsVO().setAccountId(accountId);
                    /*FraudChecker onlineFraudChecker = new FraudChecker();
                    onlineFraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO, account);*/
                directCommResponseVO.setFraudScore(commonValidatorVO.getFraudScore());
                directCommResponseVO.setRulesTriggered(commonValidatorVO.getRuleTriggered());
                if (commonValidatorVO.isFraud())
                {
                    //paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(),amount,machineId,null,"Fraud Transaction",null,String.valueOf(trackingId));
                    paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), PZTransactionStatus.FAILED.toString(), amount, "Fraud Transaction", null,null);

                    actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, null, commRequestVO1, auditTrailVO, null);


                    apiStatus = "N";
                    statusMsg = "Fraud Validation Failed";
                    directCommResponseVO.setCommStatus(apiStatus);
                    directCommResponseVO.setCommStatusMessage(statusMsg);
                    //transactionUtil.setSystemResponseAndErrorCodeListVO(null,errorCodeListVO,ErrorName.SYS_FRAUD_TRANS,null,null,null,null);

                    mailtransactionStatus = "failed";
                    String remark = "High Risk Transaction Detected";

                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, remark, null);
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, remark, null);
                    return directCommResponseVO;
                }
            }
        }

        displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
    }
    catch (PZDBViolationException dbe)
    {
        log.error("PZDBViolationException in CommonPaymentProcess---", dbe);
        transactionLogger.error("PZDBViolationException in CommonPaymentProcess---", dbe);
        PZExceptionHandler.handleDBCVEException(dbe, toid, "transactionAPI");
        apiStatus = "N";
        statusMsg = "Internal Error Occured, Please contact Customer Support";
        directCommResponseVO.setCommStatus(apiStatus);
        //commResponse.set("errorCodeListVO", dbe.getPzdbConstraint().getErrorCodeListVO());
        directCommResponseVO.setCommStatusMessage(statusMsg);
        return directCommResponseVO;
    }

    try
    {

        pg = AbstractPaymentGateway.getGateway(accountId);
        String merchantId = account.getMerchantId();
        String alias = account.getAliasName();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        String displayname = account.getDisplayName();


        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        cardDetailsVO.setCardNum(ccnum);
        cardDetailsVO.setcVV(cvv);
        cardDetailsVO.setExpMonth(expmonth);
        cardDetailsVO.setExpYear(expyear);
        cardDetailsVO.setCardType(Functions.getCardType(ccnum));

        cardDetailsVO.setIBAN(iban);
        cardDetailsVO.setBIC(bic);
        cardDetailsVO.setMandateId(mandateId);

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        addressDetailsVO.setFirstname(firstname);
        addressDetailsVO.setLastname(lastname);
        addressDetailsVO.setCity(city);
        addressDetailsVO.setCountry(country);
        addressDetailsVO.setIp(ipaddress);
        addressDetailsVO.setCardHolderIpAddress(cardHolderIpAddress);
        addressDetailsVO.setTelnocc(telnocc);
        addressDetailsVO.setPhone(telno);
        addressDetailsVO.setEmail(email);
        addressDetailsVO.setState(state);
        addressDetailsVO.setStreet(street);
        addressDetailsVO.setZipCode(zip);
        addressDetailsVO.setBirthdate(birthdate);
        addressDetailsVO.setLanguage(language);
        addressDetailsVO.setCardHolderIpAddress(cardHolderIpAddress);

        BigDecimal txnAmount = new BigDecimal(amount);


        transDetailsVO.setAmount(String.valueOf(txnAmount));
        transDetailsVO.setCurrency(currency);
        transDetailsVO.setOrderId(description);
        transDetailsVO.setOrderDesc(description);
        transDetailsVO.setPaymentType(paymentType);
        transDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(account.getMerchantId());
        merchantAccountVO.setPassword(account.getFRAUD_FTP_PASSWORD());

        merchantAccountVO.setMerchantId(merchantId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(displayname);
        merchantAccountVO.setAliasName(alias);
        String merchantTelno = commonValidatorVO.getMerchantDetailsVO().getTelNo();
        String mTelNo = "";

        if (functions.isValueNull(merchantTelno) && merchantTelno.contains("-"))
        {
            String[] phone = merchantTelno.split("-");
            mTelNo = phone[1];
        }
        else
        {
            mTelNo = merchantTelno;
        }

        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
            merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        if (functions.isValueNull(mTelNo))
            merchantAccountVO.setPartnerSupportContactNumber(mTelNo);


        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accountId));


        //RecurringBillingVO set in CommRequestVO
        RecurringBillingVO recurringBillingVO = null;
        RecurringManager recurringManager = new RecurringManager();

        if ((isRecurring != null && !isRecurring.equals("") && "Y".equalsIgnoreCase(isRecurring)) || ("Y".equalsIgnoreCase(account.getIsRecurring())))
        {
            recurringBillingVO = new RecurringBillingVO();
            recurringBillingVO.setRecurring(isRecurring);
            recurringBillingVO.setInterval(recInterval);
            recurringBillingVO.setFrequency(recFrequency);
            recurringBillingVO.setRunDate(recRunDate);


            //insert for recurring subscription table
            recurringBillingVO.setOriginTrackingId(String.valueOf(trackingId));
            recurringBillingVO.setAmount(amount);
            recurringBillingVO.setCardHolderName(firstname + " " + lastname);
            recurringBillingVO.setMemberId(toid);
            recurringBillingVO.setTerminalid(terminalid);
            recurringBillingVO.setIsAutomaticRecurring(isAutomaticRecurring);
            recurringBillingVO.setIsManualRecurring(isManualRecurring);
            if ("Y".equalsIgnoreCase(isManualRecurring))
            {
                recurringType = "Manual";
            }
            else if ("Y".equalsIgnoreCase(isAutomaticRecurring))
            {
                recurringType = "Automatic";
            }
            recurringBillingVO.setRecurringType(recurringType);


            recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);

            commRequestVO.setRecurringBillingVO(recurringBillingVO);
        }

        ReserveField2VO reserveField2VO = null;


        if (commonValidatorVO.getReserveField2VO() != null)
        {
            reserveField2VO = new ReserveField2VO();
            reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
            reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
        }

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setReserveField2VO(reserveField2VO);
        commRequestVO.setRecurringBillingVO(recurringBillingVO);
        commRequestVO.setIsPSTProcessingRequest(commonValidatorVO.getIsPSTProcessingRequest());
        commRequestVO.setReject3DCard(commonValidatorVO.getReject3DCard());
        commRequestVO.setCurrencyConversion(commonValidatorVO.getCurrencyConversion());
        commRequestVO.setConversionCurrency(commonValidatorVO.getConversionCurrency());

        setTransactionVOParamsExtension(commRequestVO, commonValidatorVO);
        StringBuffer sb         = new StringBuffer();
        String isSuccessful     = "N";
        if ("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getService()))
        {
            commResponse = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingId), commRequestVO);
            if ("Y".equals(commonValidatorVO.getCurrencyConversion()) && commResponse != null && functions.isValueNull(commResponse.getAmount()))
            {
                amount = commResponse.getAmount();
            }
            if ("success".equalsIgnoreCase(commResponse.getStatus().trim()))
            {
                isSuccessful            = "Y";
                status                  = ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL;
                int actionEntry         = actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());
                statusMsg               = "Approved";
                mailtransactionStatus   = "successful";

                if (functions.isValueNull(commResponse.getDescriptor()))
                {
                    directCommResponseVO.setDescriptor(commResponse.getDescriptor());
                }
            }
            else if ("pending".equalsIgnoreCase(commResponse.getStatus().trim()))
            {
                isSuccessful            = "Y";
                mailtransactionStatus   = "pending";
            }
            else
            {

                int actionEntry         = actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());

                statusMsg               = "Failed";
                status                  = ActionEntry.STATUS_AUTHORISTION_FAILED;
                mailtransactionStatus   = "failed";
            }
            type = "auth";

        }
        else /*if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getService()))*/
        {

            commResponse = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);
            if ("Y".equals(commonValidatorVO.getCurrencyConversion()) && commResponse != null && functions.isValueNull(commResponse.getAmount()))
            {
                amount = commResponse.getAmount();
            }
            if ("success".equalsIgnoreCase(commResponse.getStatus().trim()))
            {
                isSuccessful            = "Y";
                status                  = ActionEntry.STATUS_CAPTURE_SUCCESSFUL;
                int actionEntry         = actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());
                statusMsg               = "Approved";
                mailtransactionStatus   = "successful";

                if (functions.isValueNull(commResponse.getDescriptor()))
                {
                    directCommResponseVO.setDescriptor(commResponse.getDescriptor());
                }
            }
            else if ("pending".equalsIgnoreCase(commResponse.getStatus().trim()))
            {
                isSuccessful            = "Y";
                mailtransactionStatus   = "pending";
                status                  = "pending";
            }
            else
            {
                log.debug("status for fail----");
                int actionEntry         = actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());

                statusMsg               = "Failed";
                mailtransactionStatus   = "failed";
                status                  = ActionEntry.STATUS_CAPTURE_FAILED;
            }
            type = "sale";

        }
        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), status, amount, commResponse.getDescription(), commResponse.getTransactionId(),"Non-3D");

        if ("Y".equals(isSuccessful) && "Y".equals(commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()))
        {
            TerminalManager terminalManager = new TerminalManager();
            if (terminalManager.isTokenizationActiveOnTerminal(toid, commonValidatorVO.getTerminalId()))
            {
                TokenManager tokenManager   = new TokenManager();
                Functions functions         = new Functions();
                String strToken             = tokenManager.isCardAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum());
                if (functions.isValueNull(strToken))
                {
                    token = strToken;
                }
                else
                {
                    TokenRequestVO tokenRequestVO       = new TokenRequestVO();
                    TokenResponseVO tokenResponseVO     = null;
                    CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                    commCardDetailsVO.setCardNum(ccnum);
                    commCardDetailsVO.setExpMonth(expmonth);
                    commCardDetailsVO.setExpYear(expyear);
                    commCardDetailsVO.setcVV(cvv);

                    tokenRequestVO.setMemberId(toid);
                    tokenRequestVO.setTrackingId(String.valueOf(trackingId));
                    tokenRequestVO.setDescription(description);

                    tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                    tokenRequestVO.setAddressDetailsVO(addressDetailsVO);

                    tokenRequestVO.setGeneratedBy(generatedBy);

                    tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                    if ("success".equals(tokenResponseVO.getStatus()))
                    {
                        token = tokenResponseVO.getToken();
                    }
                }
            }

        }//todo to set token in vo
        directCommResponseVO.setToken(token);

        if (ResponseLength.FULL.toString().equals(responseLength))
        {
            setTransactionResponseVOParamsExtension(commResponse, directCommResponseVO);
        }
        if ("Y".equals(isSuccessful))
        {
            billingDescriptor = directCommResponseVO.getDescriptor();
        }
        //todo - send transaction maill start
            /*if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getEmailSent()))
            {*/
        Date date72 = new Date();
        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null, billingDescriptor);


        AsynchronousSmsService smsService = new AsynchronousSmsService();
        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null, billingDescriptor);

           /* }*/
            /*if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getEmailSent()))
            {
                //todo - send transaction maill start
                Date date72 = new Date();
                transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
                transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
            }*/

            /*BigDecimal chargeamount = null;
            chargeamount = new BigDecimal(amount).multiply(charge.multiply(new BigDecimal(1 / 10000.00)));
            chargeamount = chargeamount.add(new BigDecimal(fixamount));
            chargeamount = chargeamount.setScale(2, BigDecimal.ROUND_HALF_UP);
            String chargeamt = chargeamount.toString();*/

        //getting responce code for fail reason
            /*String str = commResponse.getDescription();

            String numbers[] = str.split("");
            String errorCode = numbers[0];
            String spliErrorCodes[] = errorCode.split(":");
            String errorNumber = "0";
            if (spliErrorCodes.length == 1)
            {
                errorNumber = spliErrorCodes[0];
            }
            else
            {
                if (spliErrorCodes[0].equals("0"))
                    errorNumber = spliErrorCodes[1];
                else
                    errorNumber = spliErrorCodes[0];

            }*/
        if ("success".equalsIgnoreCase(commResponse.getStatus()))
        {
            apiStatus = "Y";
            statusMsg = statusMsg + "---Your Transaction is successful";
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
        else if ("pending".equalsIgnoreCase(commResponse.getStatus()))
        {

            apiStatus = "P";
            statusMsg = statusMsg + "--(" + commResponse.getDescription().replace(":", " ") + " )";
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
        else if ("pending3DConfirmation".equalsIgnoreCase(commResponse.getStatus()))
        {
            ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
            errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
            PZGenericConstraint genConstraint = new PZGenericConstraint("CommonPaymentProcess", "transactionApi()", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
            throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
        }
        else
        {
            apiStatus = "N";
            statusMsg = statusMsg + "--(" + commResponse.getDescription().replace(":", " ") + " )";
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
    }
    catch (PZTechnicalViolationException tve)
    {
        log.error("PZTechnicalViolationException in CommonPaymentProcess---", tve);
        transactionLogger.error("PZTechnicalViolationException in CommonPaymentProcess---", tve);
        String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();
        if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SERVICE_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.AXISFAULT.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.HTTP_EXCEPTION.toString()))
        {
            try
            {
                paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.AUTH_FAILED.toString(), amount, "", null, PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE.toString(), null, String.valueOf(trackingId));

                actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponse, commRequestVO, auditTrailVO, PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString());

            }
            catch (PZDBViolationException dbe)
            {
                log.error("DBViolation Exception in CommonPaymentProcess---", dbe);
                transactionLogger.error("DBViolation Exception in CommonPaymentProcess---", dbe);
                PZExceptionHandler.handleDBCVEException(dbe, toid, "transactionAPI");
                apiStatus = "N";
                statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
                directCommResponseVO.setCommStatus(apiStatus);
                directCommResponseVO.setCommStatusMessage(statusMsg);
                return directCommResponseVO;
            }
            statusMsg = "Bank Connectivity Issue while processing your request";
        }
        else if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
        {
            statusMsg = "Your transaction is Timed out.Please check with support team.";
        }
        else
        {
            statusMsg = "We have encountered technical error while processing your request";
        }
        PZExceptionHandler.handleTechicalCVEException(tve, toid, PZOperations.DIRECTKIT_SALE);
        apiStatus = "N";
        if (tve.getPzTechnicalConstraint().getErrorCodeListVO() != null)
            directCommResponseVO.setCommErrorCode(tve.getPzTechnicalConstraint().getErrorCodeListVO().toString());
        directCommResponseVO.setCommStatus(apiStatus);
        directCommResponseVO.setCommStatusMessage(statusMsg);
        return directCommResponseVO;
    }
    catch (PZDBViolationException dbe)
    {
        log.error("DBViolation Exception in CommonPaymentProcess---", dbe);
        transactionLogger.error("DBViolation Exception in CommonPaymentProcess---", dbe);
        PZExceptionHandler.handleDBCVEException(dbe, toid, "transactionAPI");
        apiStatus = "N";
        statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
        if (dbe.getPzdbConstraint().getErrorCodeListVO() != null)
            directCommResponseVO.setCommErrorCode(dbe.getPzdbConstraint().getErrorCodeListVO().toString());
        directCommResponseVO.setCommStatus(apiStatus);
        directCommResponseVO.setCommStatusMessage(statusMsg);
        return directCommResponseVO;
    }
    catch (PZConstraintViolationException cve)
    {
        log.error("Constraint Violation Exception in CommonPaymentProcess---", cve);
        transactionLogger.error("Constraint Violation Exception in CommonPaymentProcess---", cve);
        PZExceptionHandler.handleCVEException(cve, toid, "transactionAPI");
        apiStatus = "N";
        statusMsg = cve.getPzConstraint().getMessage();
        if (cve.getPzConstraint().getErrorCodeListVO() != null)
            directCommResponseVO.setCommErrorCode(cve.getPzConstraint().getErrorCodeListVO().toString());
        directCommResponseVO.setCommStatus(apiStatus);
        directCommResponseVO.setCommStatusMessage(statusMsg);
        return directCommResponseVO;
    }
    catch (PZGenericConstraintViolationException gce)
    {
        log.error("PZGenericConstraintViolationException in CommonPaymentProcess---", gce);
        transactionLogger.error("PZGenericConstraintViolationException in CommonPaymentProcess---", gce);
        statusMsg = gce.getMessage();

        try
        {
            paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.AUTH_FAILED.toString(), amount, null, null, null, null, String.valueOf(trackingId));
            actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, null, commRequestVO, auditTrailVO, null);

        }
        catch (PZDBViolationException dbe)
        {
            log.error("DBViolation Exception in CommonPaymentProcess---", dbe);
            transactionLogger.error("DBViolation Exception in CommonPaymentProcess---", dbe);
            PZExceptionHandler.handleDBCVEException(dbe, toid, "transactionAPI");
            apiStatus = "N";
            statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }

        PZExceptionHandler.handleGenericCVEException(gce, toid, "Merchant DK");
        apiStatus = "N";
        if (gce.getPzGenericConstraint().getErrorCodeListVO() != null)
            directCommResponseVO.setCommErrorCode(gce.getPzGenericConstraint().getErrorCodeListVO().toString());
        directCommResponseVO.setCommStatus(apiStatus);
        directCommResponseVO.setCommStatusMessage(statusMsg);
        return directCommResponseVO;
    }
    catch (SystemError e)
    {
        log.error("systemerror", e);
        transactionLogger.error("systemerror", e);
        PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(), "transactionAPI()", null, "common", "Exception::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause(), toid, PZOperations.DIRECTKIT_SALE);
        apiStatus = "N";
        statusMsg = "Your transaction is fail ::-(" + commResponse.getDescription() + ")";
        directCommResponseVO.setCommStatus(apiStatus);
        directCommResponseVO.setCommStatusMessage(statusMsg);
        return directCommResponseVO;
    }
}

    public String getBoiledName(String name)
    {

        log.debug("Inside getBoiledName");
        transactionLogger.debug("Inside getBoiledName");
        if (Functions.parseData(name) == null)
            return name;

        int index = 0;
        String temp = "", boiledName = "";
        for (int i = 1; i <= name.length(); i++)
        {
            temp = name.substring(index, i).toLowerCase();
            if (unboilchar.contains(temp))
                boiledName = boiledName + temp;
            index = i;
        }

        log.debug("leaving getBoiledName");
        return boiledName;
    }


    @Override
    public PZTransactionResponse transaction(PZTransactionRequest transactionRequest)
    {
        return null;

    }

    @Override
    public PZRefundResponse refund(PZRefundRequest refundRequest)
    {
        Connection conn                     = null;
        TransactionEntry transactionEntry   = new TransactionEntry();
        PZRefundResponse refundResponse     = new PZRefundResponse();
        MarketPlaceVO marketPlaceVO         = null;
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        CommRequestVO commRequestVO         = null;
        AuditTrailVO auditTrailVO           = null;
        String toid             = "";
        String pzTransId        = "";
        String sRefundAmount    = "";
        String captureAmount    = "";
        String reversedAmount   = "";
        String transStatus      = "";
        String notificationURL  = "";
        MerchantDetailsVO merchantDetailsVO     = null;
        MerchantDAO merchantDAO                 = new MerchantDAO();
        double refAmount        = 0.00;
        try
        {
            Integer trackingId  = refundRequest.getTrackingId();
            Integer accntId     = refundRequest.getAccountId();
            sRefundAmount       = refundRequest.getRefundAmount();
            captureAmount       = refundRequest.getCaptureAmount();
            String refundReason = refundRequest.getRefundReason();
            transStatus         = refundRequest.getTransactionStatus();
            boolean isFraud     = refundRequest.isFraud();
            boolean isAdmin     = refundRequest.isAdmin();
            String ipaddress    = refundRequest.getIpAddress();
            String pzRequestedTransId   = "";
            String childCaptureAmount   = "";
            String childReversedAmount  = "";
            double childRefAmount       = 0.00;
            String sChildRefundAmount   = "";
            String childtrackingId      = "";
            Hashtable childTransDetails = null;
            //transactionStatus = refundRequest.getTransactionStatus();
            reversedAmount                          = refundRequest.getReversedAmount();
            auditTrailVO                            = refundRequest.getAuditTrailVO();
            List<MarketPlaceVO> childDetails        = refundRequest.getChildDetailsList();
            List<MarketPlaceVO> childDetailsList    = null;
            AbstractPaymentGateway pg               = AbstractPaymentGateway.getGateway(String.valueOf(accntId));
            String currency = "";

            if(functions.isValueNull(refundRequest.getCurrency()))
                currency = refundRequest.getCurrency();
            else
                currency = pg.getCurrency();

            if(functions.isValueNull(refundRequest.getNotificationURL())){
                notificationURL = refundRequest.getNotificationURL();
            }

            GatewayAccount account  = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
            String merchantId       = account.getMerchantId();
            String alias            = account.getAliasName();
            String username         = account.getFRAUD_FTP_USERNAME();
            String password         = account.getFRAUD_FTP_PASSWORD();
            String displayname      = account.getDisplayName();
            String marketPlaceFlag  = refundRequest.getMarketPlaceFlag() == null ? "N" : refundRequest.getMarketPlaceFlag();
            Integer requestedTrackingid = refundRequest.getRequestedTrackingid() == null? 0 : refundRequest.getRequestedTrackingid();
            if(functions.isValueNull(refundRequest.getRequestedReversedAmount()))
                childReversedAmount     = refundRequest.getRequestedReversedAmount();
            if(functions.isValueNull(refundRequest.getRequestedCaptureAmount()))
                childCaptureAmount      = refundRequest.getRequestedCaptureAmount();

            CommResponseVO transRespDetails = null;

            if(Functions.checkAccuracy(sRefundAmount, 1))
            {
                sRefundAmount = sRefundAmount + "0";
            }
            if(!Functions.checkAccuracy(sRefundAmount, 2) )
            {
                refundResponse.setStatus(PZResponseStatus.ERROR);
                refundResponse.setResponseDesceiption("Refund Amount should be 2 decimal places accurate");
                return refundResponse;
            }

            transactionLogger.debug("sRefundAmount="+Double.parseDouble(sRefundAmount));
            transactionLogger.debug("captureAmount="+Double.parseDouble(captureAmount));
            transactionLogger.debug("reversedAmount="+Double.parseDouble(reversedAmount));
            String remainingAmt     = String.format("%.2f",Double.parseDouble(captureAmount)-Double.parseDouble(reversedAmount));
            transactionLogger.debug("Balance Amount="+Double.parseDouble(remainingAmt));
            transactionLogger.error("account.isPartialRefund()--->" + account.isPartialRefund());
            transactionLogger.error("captureAmount--->"+captureAmount);
            transactionLogger.error("sRefundAmount--->"+sRefundAmount);
            if(!account.isPartialRefund() && Double.parseDouble(captureAmount) != Double.parseDouble(sRefundAmount))
            {
                String error                    = "Partial Refund not allowed.";
                ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
                errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_PARTIAL_REFUND_NOT_ALLOWED));
                PZExceptionHandler.raiseConstraintViolationException(CommonPaymentProcess.class.getName(),"processRefund()",null,"common",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }
            if("N".equalsIgnoreCase(account.getIsMultipleRefund()) && transStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString()))
            {
                String error                    = "Multiple Refund not allowed.";
                ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
                errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MULTIPLE_REFUND_NOT_ALLOWED));
                PZExceptionHandler.raiseConstraintViolationException(CommonPaymentProcess.class.getName(),"processRefund()",null,"common",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
            }
            if(Double.parseDouble(sRefundAmount) <= Double.parseDouble(remainingAmt)){
                refAmount   = Double.parseDouble(sRefundAmount) + Double.parseDouble(reversedAmount);

            }else {
                String error = "Refund Amount should be less than or equal to capture amount:" + captureAmount;
                PZExceptionHandler.raiseConstraintViolationException("TransactionEntry.java","newGenericRefundTransaction()",null,"common",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);

            }
            if(childDetails == null && requestedTrackingid != 0 && !"N".equalsIgnoreCase(marketPlaceFlag))
            {
                childDetails    = new ArrayList<>();
                marketPlaceVO   = new MarketPlaceVO();
                marketPlaceVO.setTrackingid(String.valueOf(requestedTrackingid));
                marketPlaceVO.setRefundAmount(sRefundAmount);
                marketPlaceVO.setCapturedAmount(childCaptureAmount);
                marketPlaceVO.setReversedAmount(childReversedAmount);
                childDetails.add(marketPlaceVO);
            }
            if(childDetails != null && !"N".equalsIgnoreCase(marketPlaceFlag))
            {
                childDetailsList    = new ArrayList<>();
                for (int i = 0; i < childDetails.size(); i++)
                {
                    marketPlaceVO       = (MarketPlaceVO) childDetails.get(i);
                    childtrackingId     = marketPlaceVO.getTrackingid();
                    sChildRefundAmount  = marketPlaceVO.getRefundAmount();
                    childCaptureAmount  = marketPlaceVO.getCapturedAmount();
                    childReversedAmount = marketPlaceVO.getReversedAmount();
                    transactionLogger.debug("Child sRefundAmount="+Double.parseDouble(sChildRefundAmount));
                    transactionLogger.debug("Child captureAmount="+Double.parseDouble(childCaptureAmount));
                    transactionLogger.debug("Child reversedAmount="+Double.parseDouble(childReversedAmount));
                    remainingAmt    = String.format("%.2f",Double.parseDouble(childCaptureAmount)-Double.parseDouble(childReversedAmount));
                    transactionLogger.debug("Child Balance Amount="+Double.parseDouble(remainingAmt));
                    if(Double.parseDouble(sChildRefundAmount) <= Double.parseDouble(remainingAmt)){
                        childRefAmount  = Double.parseDouble(sChildRefundAmount)+Double.parseDouble(childReversedAmount);

                    }else {
                        String error = "Cannot Refund Transaction as Refund Amount is greater than Available Amount for Tracking Id "+childtrackingId;
                        PZExceptionHandler.raiseConstraintViolationException("TransactionEntry.java","newGenericRefundTransaction()",null,"common",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);

                    }
                    marketPlaceVO.setAmount(String.format("%.2f", childRefAmount));
                    childDetailsList.add(marketPlaceVO);
                }
            }

            conn = Database.getConnection();

            toid                                    = String.valueOf(refundRequest.getMemberId());
            String transaction_details              = "select detailid,responsetransactionid,responsetime,amount,transtype from transaction_common_details where trackingid=? and status='capturesuccess'";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, trackingId);
            ResultSet rsTransDetails                = transDetailsprepstmnt.executeQuery();
            if (isLogEnabled)
                log.debug("transDetailsprepstmnt from refund in CommonPaymentProcess----"+transDetailsprepstmnt);
            if (rsTransDetails.next())
            {

                pzTransId           = String.valueOf(trackingId);
                String accountId    = String.valueOf(accntId);
                String detailId     = rsTransDetails.getString("detailid");
                String gateway_transactionid    = rsTransDetails.getString("responsetransactionid");
                String responsetime             = rsTransDetails.getString("responsetime");
                String transactionAmount        = rsTransDetails.getString("amount");
                String transtype                = rsTransDetails.getString("transtype");
                if (isLogEnabled)
                    log.debug("gateway_transactionid from CommonPaymentProcess----"+gateway_transactionid);

                String description;
                String binUpdateQuery       = "update bin_details set isFraud=?, isRefund=? where icicitransid=?";
                PreparedStatement pPStmt    = conn.prepareStatement(binUpdateQuery);
                if (isLogEnabled)
                    log.debug("pPStmt from CommonPaymentProcess----"+pPStmt);
                if (isFraud)
                {
                    if (isAdmin)
                    {
                        description = "Refund of " + trackingId + "  (Fraudulent Transaction)";
                    }
                    else
                    {
                        description = "Refund of " + trackingId + "  (Fraudulent Transaction)";
                    }
                    pPStmt.setString(1,"Y");
                }
                else
                {
                    if (isAdmin)
                    {
                        description = "Refund of " + trackingId + "  ";
                    }
                    else
                    {
                        description = "Refund of " + trackingId + "  ";
                    }
                    pPStmt.setString(1,"N");
                }
                pPStmt.setString(2,"Y");
                pPStmt.setString(3,pzTransId);
                pPStmt.executeUpdate();
                transactionEntry.newGenericRefundTransactionforCommon(pzTransId, new BigDecimal(sRefundAmount), accountId, description, transRespDetails,auditTrailVO);
                transactionEntry.closeConnection();
                if(childDetailsList != null && !"N".equalsIgnoreCase(marketPlaceFlag))
                {
                    for (int i = 0 ; i < childDetailsList.size() ; i++)
                    {
                        marketPlaceVO           = (MarketPlaceVO)childDetailsList.get(i);
                        sChildRefundAmount      = marketPlaceVO.getRefundAmount();
                        pzRequestedTransId      = marketPlaceVO.getTrackingid();
                        boolean isFraudChild    = marketPlaceVO.isFraud();
                        if (isLogEnabled)
                            log.debug("gateway_transactionid from CommonPaymentProcess----"+gateway_transactionid);

                        binUpdateQuery  = "update bin_details set isFraud=?, isRefund=? where icicitransid=?";
                        pPStmt          = conn.prepareStatement(binUpdateQuery);
                        if (isLogEnabled)
                            log.debug("pPStmt from CommonPaymentProcess----"+pPStmt);
                        if (isFraudChild)
                        {
                            if (isAdmin)
                            {
                                description = "Refund of " + pzRequestedTransId + "  (Fraudulent Transaction)";
                            }
                            else
                            {
                                description = "Refund of " + pzRequestedTransId + "  (Fraudulent Transaction)";
                            }
                            pPStmt.setString(1,"Y");
                        }
                        else
                        {
                            if (isAdmin)
                            {
                                description = "Refund of " + pzRequestedTransId + "  ";
                            }
                            else
                            {
                                description = "Refund of " + pzRequestedTransId + "  ";
                            }
                            pPStmt.setString(1,"N");
                        }
                        pPStmt.setString(2,"Y");
                        pPStmt.setString(3,pzRequestedTransId);
                        pPStmt.executeUpdate();
                        transactionEntry.newGenericRefundTransactionforCommon(pzRequestedTransId, new BigDecimal(sChildRefundAmount), accountId, description, transRespDetails,auditTrailVO);
                        transactionEntry.closeConnection();
                    }
                }

                CommMerchantVO merchantAccountVO = new CommMerchantVO();

                merchantDetailsVO = merchantDAO.getMemberDetails(refundRequest.getMemberId()+"");
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);

                CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
                addressDetailsVO.setIp(ipaddress);

                CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
                transDetailsVO.setAmount(sRefundAmount);
                transDetailsVO.setReversedAmount(reversedAmount);
                transDetailsVO.setRemainingAmount(remainingAmt);
                transDetailsVO.setCurrency(currency);
                transDetailsVO.setOrderId(String.valueOf(trackingId));
                transDetailsVO.setOrderDesc(refundReason);
                transDetailsVO.setDetailId(detailId);
                transDetailsVO.setPreviousTransactionId(gateway_transactionid);
                transDetailsVO.setResponsetime(responsetime);
                transDetailsVO.setPreviousTransactionAmount(transactionAmount);
                transDetailsVO.setTransactionType(transtype);


                commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accountId));
                commRequestVO.setCount(getRefundCount(String.valueOf(trackingId)));
                commRequestVO.setAddressDetailsVO(addressDetailsVO);
                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(transDetailsVO);

                setRefundVOParamsextension(commRequestVO, refundRequest);
                AbstractPaymentGateway paymentGateway   = AbstractPaymentGateway.getGateway(accountId);
                transRespDetails                        = (CommResponseVO) paymentGateway.processRefund(String.valueOf(trackingId), commRequestVO);
                if (transRespDetails != null && ("success").equalsIgnoreCase(transRespDetails.getStatus()))
                {
                    String status               = "reversed";
                    String dbStatus             = "";
                    String actionEntryAction    = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                    String actionEntryStatus    = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                    String transactionStatus    = PZResponseStatus.SUCCESS.toString();
                    String rrn=transRespDetails.getRrn();
                    if (Double.parseDouble(captureAmount) > Double.parseDouble(String.valueOf(refAmount)))
                    {
                        status              = "reversed";
                        dbStatus            = "";
                        actionEntryAction   = ActionEntry.ACTION_PARTIAL_REFUND;
                        actionEntryStatus   = ActionEntry.STATUS_PARTIAL_REFUND;
                        transactionStatus   = PZResponseStatus.PARTIALREFUND.toString();
                    }

                    if (captureAmount.equals(String.format("%.2f",refAmount)))
                    {
                        status              = "reversed";
                        actionEntryAction   = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                        actionEntryStatus   = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                        transactionStatus   = PZResponseStatus.SUCCESS.toString();
                    }
                    Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                    StringBuffer sb = new StringBuffer();
                    sb.append("update transaction_common set status='" + status + "'");
                    sb.append(",refundinfo='" + ESAPI.encoder().encodeForSQL(MY,refundReason) +"'");
                    sb.append(",refundamount='" + ESAPI.encoder().encodeForSQL(MY, String.valueOf(refAmount)) + "'");
                    sb.append(",refundtimestamp='" + functions.getTimestamp() + "'");
                    if(functions.isValueNull(rrn))
                        sb.append(",rrn='" + ESAPI.encoder().encodeForSQL(MY, rrn) + "'");
                    sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, pzTransId) + " and status='markedforreversal'");
                    int rows = Database.executeUpdate(sb.toString(), conn);
                    if (isLogEnabled)
                        log.debug("sb from CommonPaymentProcess----"+sb);
                    if (isLogEnabled)
                        log.debug("No of Rows updated :" + rows + "<br>");
                    transRespDetails.setIpaddress(ipaddress);

                    if (rows == 1)
                    {
                        actionEntry(pzTransId, String.valueOf(sRefundAmount), actionEntryAction, actionEntryStatus, transRespDetails, commRequestVO,auditTrailVO,refundReason);
                        refundResponse.setStatus(PZResponseStatus.SUCCESS);
                        refundResponse.setResponseDesceiption("Transaction has been successfully reversed");
                    }
                    else if(rows == 0)
                    {
                        TransactionDAO transactionDAO = new TransactionDAO();
                        TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(trackingId.toString());
                        if(transactionDetailsVO.getStatus().equalsIgnoreCase(ActionEntry.STATUS_REVERSAL_SUCCESSFUL) || transactionDetailsVO.getStatus().equalsIgnoreCase(ActionEntry.STATUS_PARTIAL_REFUND))
                        {
                            refundResponse.setStatus(PZResponseStatus.SUCCESS);
                            refundResponse.setResponseDesceiption("Transaction has been successfully reversed");
                        }
                    }
                    StringBuffer invoiceUpdate  = new StringBuffer("update invoice set status='"+actionEntryStatus+"' where trackingid="+ESAPI.encoder().encodeForSQL(MY, pzTransId));
                    Database.executeUpdate(invoiceUpdate.toString(), conn);
                    if(childDetailsList != null && !"N".equalsIgnoreCase(marketPlaceFlag))
                    {
                        for (int i=0;i<childDetailsList.size();i++)
                        {
                            marketPlaceVO           = childDetailsList.get(i);
                            String cRefAmount       = marketPlaceVO.getAmount();
                            String cCaptureAmount   = marketPlaceVO.getCapturedAmount();
                            String reason           = "";
                            if(functions.isValueNull(marketPlaceVO.getRefundReason()))
                                reason  = marketPlaceVO.getRefundReason();
                            else
                                reason  = refundReason;
                            sChildRefundAmount  = marketPlaceVO.getRefundAmount();
                            childtrackingId     = marketPlaceVO.getTrackingid();
                            actionEntryAction   = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                            actionEntryStatus   = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                            transactionStatus   = PZResponseStatus.SUCCESS.toString();
                            if (Double.parseDouble(cCaptureAmount) > Double.parseDouble(String.valueOf(cRefAmount)))
                            {
                                status            = "reversed";
                                dbStatus          = "";
                                actionEntryAction = ActionEntry.ACTION_PARTIAL_REFUND;
                                actionEntryStatus = ActionEntry.STATUS_PARTIAL_REFUND;
                                transactionStatus = PZResponseStatus.PARTIALREFUND.toString();
                            }
                            if (cCaptureAmount.equals(cRefAmount))
                            {
                                status              = "reversed";
                                actionEntryAction   = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                actionEntryStatus   = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                transactionStatus   = PZResponseStatus.SUCCESS.toString();
                            }
                            sb = new StringBuffer();
                            sb.append("update transaction_common set status='"+ status +"'");
                            sb.append(",refundinfo='" + ESAPI.encoder().encodeForSQL(MY,reason) +"'");
                            sb.append(",refundamount='" + ESAPI.encoder().encodeForSQL(MY,  cRefAmount)+ "'");
                            sb.append(",refundtimestamp='" + functions.getTimestamp() + "'");
                            sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, childtrackingId) + " and status='markedforreversal'");
                            transactionLogger.error("Query------>"+sb.toString());
                            rows = Database.executeUpdate(sb.toString(), conn);
                            if (isLogEnabled)
                                log.debug("sb from CommonPaymentProcess----"+sb);
                            if (isLogEnabled)
                                log.debug("No of Rows updated :" + rows + "<br>");
                            transRespDetails.setIpaddress(ipaddress);

                            if (rows == 1)
                            {
                                actionEntry(childtrackingId, String.valueOf(sChildRefundAmount), actionEntryAction, actionEntryStatus, transRespDetails, commRequestVO,auditTrailVO,reason);
                                refundResponse.setStatus(PZResponseStatus.SUCCESS);
                                refundResponse.setResponseDesceiption("Transaction has been successfully reversed");
                            }
                            else if(rows == 0)
                            {
                                TransactionDAO transactionDAO = new TransactionDAO();
                                TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(childtrackingId);
                                if(transactionDetailsVO.getStatus().equalsIgnoreCase(ActionEntry.STATUS_REVERSAL_SUCCESSFUL) || transactionDetailsVO.getStatus().equalsIgnoreCase(ActionEntry.STATUS_PARTIAL_REFUND))
                                {
                                    refundResponse.setStatus(PZResponseStatus.SUCCESS);
                                    refundResponse.setResponseDesceiption("Transaction has been successfully reversed");
                                }
                            }
                        }
                    }
                }
                else if (transRespDetails != null && ("pending").equalsIgnoreCase(transRespDetails.getStatus()))
                {
                    refundResponse.setStatus(PZResponseStatus.PENDING);
                    refundResponse.setResponseDesceiption(transRespDetails.getDescription());
                    refundResponse.setPaymentURL(transRespDetails.getPaymentURL());
                }
                else if (transRespDetails != null && (PZTransactionStatus.ACCEPTED_FOR_REVERSAL.toString()).equalsIgnoreCase(transRespDetails.getStatus()))
                {
                    Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                    StringBuffer sb = new StringBuffer();
                    sb.append("update transaction_common set status='acceptedforreversal'");
                    sb.append(",refundamount='" + ESAPI.encoder().encodeForSQL(MY, String.valueOf(refAmount)) + "'");
                    sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, pzTransId) + " and status='markedforreversal'");
                    int rows = Database.executeUpdate(sb.toString(), conn);
                    if (isLogEnabled)
                        log.debug("No of Rows updated :" + rows + "<br>");
                    transRespDetails.setIpaddress(ipaddress);

                    if (rows == 1)
                    {
                        actionEntry(pzTransId, String.valueOf(sRefundAmount), ActionEntry.ACTION_REVERSAL_REQUEST_ACCEPTED, ActionEntry.STATUS_REVERSAL_REQUEST_ACCEPTED, transRespDetails, commRequestVO,auditTrailVO,null);
                        refundResponse.setStatus(PZResponseStatus.ACCEPTED);
                        refundResponse.setResponseDesceiption("Transaction has been accepted for reversal");
                    }
                    else if(rows == 0)
                    {
                        TransactionDAO transactionDAO = new TransactionDAO();
                        TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(trackingId.toString());
                        if(transactionDetailsVO.getStatus().equalsIgnoreCase(ActionEntry.STATUS_REVERSAL_REQUEST_ACCEPTED))
                        {
                            refundResponse.setStatus(PZResponseStatus.ACCEPTED);
                            refundResponse.setResponseDesceiption("Transaction has been accepted for reversal");
                        }
                    }
                    if(childDetailsList != null && !"N".equalsIgnoreCase(marketPlaceFlag))
                    {
                        for (int i = 0; i < childDetailsList.size(); i++)
                        {
                            marketPlaceVO = childDetailsList.get(i);
                            String cRefAmount = marketPlaceVO.getAmount();
                            sChildRefundAmount=marketPlaceVO.getRefundAmount();
                            childtrackingId=marketPlaceVO.getTrackingid();
                            sb = new StringBuffer();
                            sb.append("update transaction_common set status='acceptedforreversal'");
                            sb.append(",refundamount='" + ESAPI.encoder().encodeForSQL(MY, String.valueOf(cRefAmount)) + "'");
                            sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, childtrackingId) + " and status='markedforreversal'");
                            rows = Database.executeUpdate(sb.toString(), conn);
                            if (isLogEnabled)
                                log.debug("No of Rows updated :" + rows + "<br>");
                            transRespDetails.setIpaddress(ipaddress);

                            if (rows == 1)
                            {
                                actionEntry(childtrackingId, String.valueOf(sChildRefundAmount), ActionEntry.ACTION_REVERSAL_REQUEST_ACCEPTED, ActionEntry.STATUS_REVERSAL_REQUEST_ACCEPTED, transRespDetails, commRequestVO,auditTrailVO,null);
                                refundResponse.setStatus(PZResponseStatus.ACCEPTED);
                                refundResponse.setResponseDesceiption("Transaction has been accepted for reversal");
                            }
                            else if(rows == 0)
                            {
                                TransactionDAO transactionDAO = new TransactionDAO();
                                TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(childtrackingId);
                                if(transactionDetailsVO.getStatus().equalsIgnoreCase(ActionEntry.STATUS_REVERSAL_REQUEST_ACCEPTED))
                                {
                                    refundResponse.setStatus(PZResponseStatus.ACCEPTED);
                                    refundResponse.setResponseDesceiption("Transaction has been accepted for reversal");
                                }
                            }
                        }
                    }
                }
                else if(transRespDetails != null && (PZTransactionStatus.WAIT_FOR_REVERSAL.toString()).equalsIgnoreCase(transRespDetails.getStatus()))
                {
                    // Change the status back to Capture Success
                    Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                    StringBuffer sb = new StringBuffer();
                    sb.append("update transaction_common set status='capturesuccess'");
                    sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, pzTransId) + " and status='markedforreversal'");
                    int rows = Database.executeUpdate(sb.toString(), conn);
                    if (isLogEnabled)
                        log.debug("No of Rows updated :" + rows + "<br>");
                    if(rows==1)
                    {
                        actionEntry(pzTransId, String.valueOf(sRefundAmount), ActionEntry.ACTION_REVERSAL_REQUEST_DECLINED, ActionEntry.STATUS_REVERSAL_REQUEST_DECLINED, transRespDetails, commRequestVO, auditTrailVO, null);
                    }
                    refundResponse.setStatus(PZResponseStatus.FAILED);
                    refundResponse.setResponseDesceiption(transRespDetails.getDescription());
                    if(childDetailsList != null && !"N".equalsIgnoreCase(marketPlaceFlag))
                    {
                        for (int i = 0; i < childDetailsList.size(); i++)
                        {
                            marketPlaceVO = childDetailsList.get(i);
                            String cRefAmount = marketPlaceVO.getAmount();
                            sChildRefundAmount = marketPlaceVO.getRefundAmount();
                            childtrackingId = marketPlaceVO.getTrackingid();
                            sb = new StringBuffer();
                            sb.append("update transaction_common set status='capturesuccess'");
                            sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, childtrackingId) + " and status='markedforreversal'");
                            rows = Database.executeUpdate(sb.toString(), conn);
                            if (isLogEnabled)
                                log.debug("No of Rows updated :" + rows + "<br>");
                            if(rows==1)
                            {
                                actionEntry(childtrackingId, String.valueOf(sChildRefundAmount), ActionEntry.ACTION_REVERSAL_REQUEST_DECLINED, ActionEntry.STATUS_REVERSAL_REQUEST_DECLINED, transRespDetails, commRequestVO, auditTrailVO, null);
                            }
                        }
                    }
                }
                else
                {
                    refundResponse.setStatus(PZResponseStatus.FAILED);
                    refundResponse.setResponseDesceiption("Transaction cannot be Reversed.");
                }
            }
            else
            {
                refundResponse.setStatus(PZResponseStatus.ERROR);
                refundResponse.setResponseDesceiption("Error retrieving captured transaction details");
            }

            if(!functions.isValueNull(notificationURL) && functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
            {
                notificationURL = merchantDetailsVO.getNotificationUrl();
            }

            transactionLogger.error("Sending Refund Notification ---------->  "+trackingId+" "+notificationURL);

            if(functions.isValueNull(notificationURL) && (!PZResponseStatus.PENDING.equals(refundResponse.getStatus()) || !PZResponseStatus.ERROR.equals(refundResponse.getStatus())))
            {

                TransactionManager transactionManager               = new  TransactionManager();
                TransactionDetailsVO transactionDetailsVO1          = transactionManager.getTransDetailFromCommon(trackingId+"");
                transactionDetailsVO1.setSecretKey(merchantDetailsVO.getKey());
                if(functions.isValueNull(transactionDetailsVO1.getCcnum()))
                {
                    String cardStr = PzEncryptor.decryptPAN(transactionDetailsVO1.getCcnum());
                    String Expdate = PzEncryptor.decryptExpiryDate(transactionDetailsVO1.getExpdate());
                    transactionDetailsVO1.setCcnum(cardStr);
                    transactionDetailsVO1.setExpdate(Expdate);
                }else{
                    transactionDetailsVO1.setCcnum("");
                    transactionDetailsVO1.setExpdate("");
                }

                if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                    transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                }else{
                    transactionDetailsVO1.setMerchantNotificationUrl("");
                }

                AsyncNotificationService asyncNotificationService 	= AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId+"", transactionDetailsVO1.getStatus(), transactionDetailsVO1.getRemark());
            }

        }
        catch ( PZTechnicalViolationException tve)
        {
            log.error("PZTechnicalViolationException", tve);
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction" + tve.getMessage());
            PZExceptionHandler.handleTechicalCVEException(tve,toid,"refund");
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException", cve);
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption(cve.getPzConstraint().getMessage());
            PZExceptionHandler.handleCVEException(cve, toid, "refund");
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException", dbe);
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction" + dbe.getMessage());
            PZExceptionHandler.handleDBCVEException(dbe, toid, "refund");
        }

        catch (SystemError systemError)
        {
            log.error("System Error while refunding transaction",systemError);
            PZExceptionHandler.raiseAndHandleConstraintViolationException(CommonPaymentProcess.class.getName(), "refund()", null, "common", "System Error:::", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, systemError.getMessage(), systemError.getCause(), toid, "Refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Internal error occured while Refunding transaction");
        }
        catch (SQLException e)
        {
            log.error("SQL Exception while refunding transaction",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"refund()",null,"common","System Error:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),toid,"Refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Internal error occured while Refunding transaction");
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return refundResponse;
    }

    public String getRefundCount(String trackingId) throws PZDBViolationException
    {
        transactionLogger.error("Inside getRefundCount---");
        String count="0";
        Connection conn=null;
        PreparedStatement ps=null;
        try
        {
            conn=Database.getConnection();
            String sqlQuery="Select count(trackingid) as count from transaction_common_details where status IN ('markedforreversal','partialrefund','reversed') AND trackingid=?";
            ps=conn.prepareStatement(sqlQuery);
            ps.setString(1, trackingId);
            transactionLogger.error("getRefundCount---"+ps);
            ResultSet rs=ps.executeQuery();
            if (rs.next())
                count=rs.getString("count");
        }
        catch (SystemError se)
        {
            transactionLogger.error("System error---",se);
            PZExceptionHandler.raiseDBViolationException(CommonPaymentProcess.class.getName(), "getRefundCount()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException error---",e);
            PZExceptionHandler.raiseDBViolationException(CommonPaymentProcess.class.getName(),"getRefundCount()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return count;
    }

    @Override
    public PZCaptureResponse capture(PZCaptureRequest captureRequest)
    {
        Connection conn = null;
        String toid = "";
        String pzTransId="";
        Double captureAmount                    = null;
        PZCaptureResponse captureResponse       = new PZCaptureResponse();
        CommRequestVO commRequestVO             = null;
        AuditTrailVO auditTrailVO               = new AuditTrailVO();
        MerchantDetailsVO merchantDetailsVO     = null;
        MerchantDAO merchantDAO                 = new MerchantDAO();
        String notificationURL = "";

        try
        {
            Integer trackingId  = captureRequest.getTrackingId();
            Integer accntId     = captureRequest.getAccountId();
            captureAmount       = captureRequest.getAmount();
            String capturePod   = captureRequest.getPod();
            String ip           = captureRequest.getIpAddress();

            if(functions.isValueNull(captureRequest.getNotificationUrl())){
                notificationURL     = captureRequest.getNotificationUrl();
            }

            AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(String.valueOf(accntId));

            String currency = "";
            if(functions.isValueNull(captureRequest.getCurrency()))
                currency = captureRequest.getCurrency();
            else
                currency = pg.getCurrency();

            GatewayAccount account  = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
            String merchantId       = account.getMerchantId();
            String alias            = account.getAliasName();
            String username         = account.getFRAUD_FTP_USERNAME();
            String password         = account.getFRAUD_FTP_PASSWORD();
            String displayname      = account.getDisplayName();


            CommResponseVO transRespDetails = null;

            conn = Database.getConnection();


            /*String transaction = "select toid from transaction_common where trackingid=? and status IN('authsuccessful')";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));
            ResultSet rstransaction = transPreparedStatement.executeQuery();

            if (rstransaction.next())
            {*/
            toid = String.valueOf(captureRequest.getMemberId());
            BigDecimal captureamount = null;
            try
            {
                captureamount = new BigDecimal(captureAmount);
                if (!Functions.checkAccuracy(String.valueOf(captureAmount), 2))
                {
                    captureResponse.setStatus(PZResponseStatus.FAILED);
                    captureResponse.setResponseDesceiption("Capture Amount should be 2 decimal places accurate");
                }
            }
            catch (NumberFormatException e)
            {
                log.debug("Invalid Capture amount" + captureAmount + "-" + e.getMessage());
                PZExceptionHandler.raiseAndHandleTechnicalViolationException(CommonPaymentProcess.class.getName(),"capture()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.NUMBER_FORMAT_EXCEPTION,null,"Number format exception while converting",new Throwable("Number format exception while converting"),toid,"Capture");
                captureResponse.setStatus(PZResponseStatus.FAILED);
                captureResponse.setResponseDesceiption("Invalid Capture Amount");
            }

            String transaction_details              = "SELECT td.detailid, td.responsetransactionid,td.responsehashinfo,t.terminalid FROM transaction_common_details AS td JOIN transaction_common AS t ON td.trackingid=t.trackingid  WHERE td.trackingid=? AND td.status='authsuccessful'";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, trackingId);
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {
                String query = "update transaction_common set pod=?,podbatch=?, captureamount=?, status = 'capturestarted' where pod is null and podbatch is null and toid= ? and status='authsuccessful' and trackingid=?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,(captureRequest).getPod());
                pstmt.setString(2,(captureRequest).getPodBatch());
                pstmt.setString(3,captureamount.toString());
                pstmt.setString(4,String.valueOf((captureRequest).getMemberId()));
                pstmt.setString(5,trackingId.toString());
                int result = pstmt.executeUpdate();

                if (result == 1)
                {
                    pzTransId                       = String.valueOf(trackingId);
                    String detailId                 = rsTransDetails.getString("detailid");
                    String gateway_transactionid    = rsTransDetails.getString("responsetransactionid");
                    String terminalId               = rsTransDetails.getString("terminalid");
                    String responsehashinfo         = rsTransDetails.getString("responsehashinfo");

                    TerminalManager terminalManager = new TerminalManager();
                    TerminalVO terminalVO           = terminalManager.getMemberTerminalfromMemberAndTerminal(toid,terminalId,"");
                    String isPSTTerminal            = terminalVO.getIsPSTTerminal();

                    CommMerchantVO merchantAccountVO = new CommMerchantVO();
                    merchantAccountVO.setMerchantId(merchantId);
                    merchantAccountVO.setPassword(password);
                    merchantAccountVO.setMerchantUsername(username);
                    merchantAccountVO.setDisplayName(displayname);
                    merchantAccountVO.setAliasName(alias);
                    merchantAccountVO.setAccountId(String.valueOf(accntId));

                    CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
                    transDetailsVO.setAmount(String.format("%.2f",captureAmount));
                    transDetailsVO.setCurrency(currency);
                    transDetailsVO.setOrderId(String.valueOf(trackingId));
                    transDetailsVO.setOrderDesc(capturePod);
                    transDetailsVO.setDetailId(detailId);
                    transDetailsVO.setPreviousTransactionId(gateway_transactionid);
                    if(functions.isValueNull(responsehashinfo))
                        transDetailsVO.setResponseHashInfo(responsehashinfo);

                    CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
                    addressDetailsVO.setIp(ip);

                    commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(accntId);

                    commRequestVO.setCommMerchantVO(merchantAccountVO);
                    commRequestVO.setTransDetailsVO(transDetailsVO);
                    commRequestVO.setAddressDetailsVO(addressDetailsVO);
                    commRequestVO.setIsPSTProcessingRequest(isPSTTerminal);

                    setCaptureVOParamsExtension(commRequestVO, captureRequest);

                    //newly added

                    //auditTrailVO=new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(captureRequest.getAuditTrailVO().getActionExecutorId());
                    auditTrailVO.setActionExecutorName(captureRequest.getAuditTrailVO().getActionExecutorName());
                    actionEntry(String.valueOf(trackingId), String.valueOf(captureAmount), ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.STATUS_CAPTURE_STARTED, null, commRequestVO,auditTrailVO,null);

                    merchantDetailsVO = merchantDAO.getMemberDetails(captureRequest.getMemberId()+"");
                    //Now Capture transaction on the gateway

                    transRespDetails = (CommResponseVO) pg.processCapture(String.valueOf(trackingId), commRequestVO);

                    if (transRespDetails != null && (transRespDetails.getStatus()).equalsIgnoreCase("success"))
                    {
                        Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                        StringBuffer sb = new StringBuffer();
                        sb.append("update transaction_common set status='capturesuccess'");
                        sb.append(",captureamount='" + ESAPI.encoder().encodeForSQL(MY, String.valueOf(captureAmount)) + "'");
                        sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, pzTransId) + " and status='capturestarted'");
                        int rows = Database.executeUpdate(sb.toString(), conn);

                        if (rows == 1)
                        {

                            // Start : Added for Action and Status Entry in Action History table

                            actionEntry(pzTransId, String.valueOf(captureAmount), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO,null);

                            // End : Added for Action and Status Entry in Action History table

                            captureResponse.setStatus(PZResponseStatus.SUCCESS);
                            captureResponse.setResponseDesceiption("Transaction has been successfully captured");
                        } if (pg.getGateway().equalsIgnoreCase("fastpayv6")&& transRespDetails.getStatus().equalsIgnoreCase("success"))
                    {

                        // Start : Added for Action and Status Entry in Action History table

                        // actionEntry(pzTransId, String.valueOf(captureAmount), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO,null);

                        // End : Added for Action and Status Entry in Action History table

                        captureResponse.setStatus(PZResponseStatus.SUCCESS);
                        captureResponse.setResponseDesceiption("Transaction has been successfully captured");
                    }
                    }
                    else if (transRespDetails != null && (transRespDetails.getStatus()).equalsIgnoreCase("pending"))
                    {
                        captureResponse.setStatus(PZResponseStatus.PENDING);
                        captureResponse.setResponseDesceiption(transRespDetails.getDescription());
                    }
                    else
                    {
                        captureResponse.setStatus(PZResponseStatus.FAILED);
                        captureResponse.setResponseDesceiption("Transaction cannot be Captured.");
                    }
                    log.debug("status in CommonPayProcess after condition---"+captureResponse.getStatus());
                }
                else
                {
                    captureResponse.setStatus(PZResponseStatus.ERROR);
                    captureResponse.setResponseDesceiption("Error initiating capture for transaction");
                }
            }
            else
            {
                captureResponse.setStatus(PZResponseStatus.ERROR);
                captureResponse.setResponseDesceiption("Error retrieving auth transaction details");
            }

            /*}
            else
            {
                captureResponse.setStatus(PZResponseStatus.FAILED);
                captureResponse.setResponseDesceiption("Transaction not found");
            }*/
            setCaptureResponseParams(captureResponse,transRespDetails);

            if(!functions.isValueNull(notificationURL) && merchantDetailsVO != null ){
                notificationURL = merchantDetailsVO.getNotificationUrl();
            }

            transactionLogger.error("Capture notificationURL -----> "+trackingId+" "+notificationURL);
            if(functions.isValueNull(notificationURL) && !PZResponseStatus.PENDING.equals(captureResponse.getStatus()))
            {

                TransactionManager transactionManager               = new  TransactionManager();
                TransactionDetailsVO transactionDetailsVO1          = transactionManager.getTransDetailFromCommon(trackingId+"");
                if(functions.isValueNull(transactionDetailsVO1.getCcnum()))
                {
                    String cardStr = PzEncryptor.decryptPAN(transactionDetailsVO1.getCcnum());
                    String Expdate = PzEncryptor.decryptExpiryDate(transactionDetailsVO1.getExpdate());
                    transactionDetailsVO1.setCcnum(cardStr);
                    transactionDetailsVO1.setExpdate(Expdate);
                }else{
                    transactionDetailsVO1.setCcnum("");
                    transactionDetailsVO1.setExpdate("");
                }
                if(transactionDetailsVO1.getStatus().equalsIgnoreCase("capturesuccess")){
                    transactionDetailsVO1.setBillingDesc(account.getDisplayName());
                }

                transactionDetailsVO1.setSecretKey(merchantDetailsVO.getKey());

                if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                    transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                }else{
                    transactionDetailsVO1.setMerchantNotificationUrl("");
                }

                AsyncNotificationService asyncNotificationService 	= AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId+"", transactionDetailsVO1.getStatus(), transactionDetailsVO1.getRemark());
            }
        }
        catch (PZTechnicalViolationException tce)
        {
            log.debug("PZTechnicalViolationException in CommonPaymentProcess---"+tce);
            transactionLogger.debug("PZTechnicalViolationException in CommonPaymentProcess---"+tce);

            PZExceptionHandler.handleTechicalCVEException(tce,toid,"capture");
            captureResponse.setStatus(PZResponseStatus.ERROR);
            captureResponse.setResponseDesceiption("Error during capture of transaction" + tce.getMessage());
        }
        catch (PZConstraintViolationException cve)
        {
            log.debug("system error in CommonPaymentProcess---"+cve);
            transactionLogger.debug("system error in CommonPaymentProcess---"+cve);

            PZExceptionHandler.handleCVEException(cve,toid,"capture");
            captureResponse.setStatus(PZResponseStatus.ERROR);
            captureResponse.setResponseDesceiption("Error during capture of transaction" + cve.getMessage());
        }
        catch (PZDBViolationException dbe)
        {
            log.debug("PZDBViolationException in CommonPaymentProcess---"+dbe);
            transactionLogger.debug("PZDBViolationException in CommonPaymentProcess---"+dbe);

            PZExceptionHandler.handleDBCVEException(dbe,toid,"capture");
            captureResponse.setStatus(PZResponseStatus.ERROR);
            captureResponse.setResponseDesceiption("Error during capture of transaction" + dbe.getMessage());
        }
        catch (SystemError e)
        {
            log.debug("system error in CommonPaymentProcess---"+e);
            transactionLogger.debug("system error in CommonPaymentProcess---"+e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"capture()",null,"common","System Error",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause(),toid,"Capture");
            captureResponse.setStatus(PZResponseStatus.ERROR);
            captureResponse.setResponseDesceiption("Error during capture of transaction" + e.getMessage());
        }
        catch (SQLException e)
        {
            log.debug("SQLException in CommonPaymentProcess---"+e);
            transactionLogger.debug("SQLException in CommonPaymentProcess---"+e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"capture()",null,"common","Sql Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),toid,"Capture");
            captureResponse.setStatus(PZResponseStatus.ERROR);
            captureResponse.setResponseDesceiption("Error during capture of transaction" + e.getMessage());
        }

        finally
        {
            Database.closeConnection(conn);
        }

        return captureResponse;


    }

    @Override
    public PZCancelResponse cancel(PZCancelRequest cancelRequest)
    {

        Connection conn = null;

        CommRequestVO commRequestVO     = null;
        PZCancelResponse cancelResponse = new PZCancelResponse();
        AuditTrailVO auditTrailVO   = new AuditTrailVO();
        String toid                 = "";
        String icicitransid         = "";
        String notificationURL      = "";
        MerchantDetailsVO merchantDetailsVO     = null;
        MerchantDAO merchantDAO                 = new MerchantDAO();
        try
        {
            Integer trackingId      = cancelRequest.getTrackingId();
            Integer accntId         = cancelRequest.getAccountId();
            Integer memberId        = cancelRequest.getMemberId();
            String ip               = cancelRequest.getIpAddress();
            String cancelReason     = cancelRequest.getCancelReason();

            if(functions.isValueNull(cancelRequest.getNotificationURL()))
            {
                notificationURL     = cancelRequest.getNotificationURL();
            }

            AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(String.valueOf(accntId));
            //String currency = pg.getCurrency();
            GatewayAccount account  = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
            String merchantId       = account.getMerchantId();
            String alias            = account.getAliasName();
            String username         = account.getFRAUD_FTP_USERNAME();
            String password         = account.getFRAUD_FTP_PASSWORD();
            String displayname      = account.getDisplayName();

            String currency = "";
            if(functions.isValueNull(cancelRequest.getCurrency()))
                currency = cancelRequest.getCurrency();
            else
                currency = pg.getCurrency();

            CommResponseVO transRespDetails = null;

            conn = Database.getConnection();


            /*String transaction = "select T.*,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge from transaction_common as T,members as M where T.trackingid=? and T.accountid = ? and T.toid = ? and T.toid=M.memberid and status IN('authsuccessful')";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setInt(1, trackingId);
            transPreparedStatement.setInt(2, accntId);
            transPreparedStatement.setInt(3, memberId);
            //ResultSet rstransaction = transPreparedStatement.executeQuery();

            if (rstransaction.next())
            {*/
            toid                        = String.valueOf(cancelRequest.getMemberId());
            String amount               = cancelRequest.getAmount();
            String transaction_details  = "SELECT detailid,responsetransactionid,timestamp FROM transaction_common_details WHERE trackingid=? AND status='authsuccessful'";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, trackingId);
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {
                String cancelStartedQuery = "update transaction_common set status = 'cancelstarted' where  status='authsuccessful' and trackingid = ?";
                PreparedStatement pstmt = conn.prepareStatement(cancelStartedQuery);
                pstmt.setInt(1, trackingId);
                int result = pstmt.executeUpdate();
                if (result == 1)
                {

                    icicitransid                    = String.valueOf(trackingId);
                    String detailId                 = rsTransDetails.getString("detailid");
                    String gateway_transactionid    = rsTransDetails.getString("responsetransactionid");
                    String terminalId               = cancelRequest.getTerminalId();
                    String responseTime             = rsTransDetails.getString("timestamp");

                    TerminalManager terminalManager = new TerminalManager();
                    TerminalVO terminalVO           = terminalManager.getMemberTerminalfromMemberAndTerminal(toid,terminalId,"");
                    String isPSTTerminal            = terminalVO.getIsPSTTerminal();

                    CommMerchantVO merchantAccountVO = new CommMerchantVO();
                    merchantDetailsVO                = merchantDAO.getMemberDetails(memberId+"");
                    merchantAccountVO.setMerchantId(merchantId);
                    merchantAccountVO.setPassword(password);
                    merchantAccountVO.setMerchantUsername(username);
                    merchantAccountVO.setDisplayName(displayname);
                    merchantAccountVO.setAliasName(alias);
                    merchantAccountVO.setAccountId(String.valueOf(accntId));


                    CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
                    transDetailsVO.setAmount(String.valueOf(amount));
                    transDetailsVO.setCurrency(currency);
                    transDetailsVO.setOrderId(String.valueOf(trackingId));
                    transDetailsVO.setOrderDesc(cancelReason);
                    transDetailsVO.setDetailId(detailId);
                    transDetailsVO.setPreviousTransactionId(gateway_transactionid);

                    CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
                    addressDetailsVO.setIp(ip);
                    addressDetailsVO.setTime(responseTime);

                    commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(accntId);

                    commRequestVO.setCommMerchantVO(merchantAccountVO);
                    commRequestVO.setTransDetailsVO(transDetailsVO);
                    commRequestVO.setAddressDetailsVO(addressDetailsVO);
                    commRequestVO.setIsPSTProcessingRequest(isPSTTerminal);

                    setCancelVOParamsExtension(commRequestVO, cancelRequest);
                    auditTrailVO=new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(cancelRequest.getAuditTrailVO().getActionExecutorId());
                    auditTrailVO.setActionExecutorName(cancelRequest.getAuditTrailVO().getActionExecutorName());
                    int actionEntry = actionEntry(String.valueOf(trackingId), String.valueOf(amount), ActionEntry.ACTION_CANCEL_STARTED, ActionEntry.STATUS_CANCEL_STARTED, null, commRequestVO,auditTrailVO,null);

                    //Now Capture transaction on the gateway
                    transRespDetails = (CommResponseVO) pg.processVoid(String.valueOf(trackingId), commRequestVO);

                    if (transRespDetails != null && (transRespDetails.getStatus()).equalsIgnoreCase("success"))
                    {
                        Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                        StringBuffer sb = new StringBuffer();
                        sb.append("update transaction_common set status='authcancelled'");
                        sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, icicitransid) + " and status='cancelstarted'");
                        int rows = Database.executeUpdate(sb.toString(), conn);

                        if (rows == 1)
                        {
                            // Start : Added for Action and Status Entry in Action History table

                            actionEntry(icicitransid, String.valueOf(amount), ActionEntry.ACTION_CANCEL_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_CANCLLED, transRespDetails, commRequestVO,auditTrailVO,null);

                            // End : Added for Action and Status Entry in Action History table

                            cancelResponse.setStatus(PZResponseStatus.SUCCESS);
                            cancelResponse.setResponseDesceiption("Transaction has been successfully cancelled");
                        }
                        if (pg.getGateway().equalsIgnoreCase("fastpayv6")&& transRespDetails.getStatus().equalsIgnoreCase("success"))
                        {
                            transactionLogger.error("<==================Inside FASTPAYV6PAYMENT GATEWAY CANCEL COMONPAYMENT PROCESS==================>");

                            // Start : Added for Action and Status Entry in Action History table

                            // actionEntry(pzTransId, String.valueOf(captureAmount), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO,null);

                            // End : Added for Action and Status Entry in Action History table

                            cancelResponse.setStatus(PZResponseStatus.SUCCESS);
                            cancelResponse.setResponseDesceiption("Transaction has been successfully cancelled");
                        }
                    }
                    else if (transRespDetails != null && (transRespDetails.getStatus()).equalsIgnoreCase("pending"))
                    {
                        cancelResponse.setStatus(PZResponseStatus.PENDING);
                        cancelResponse.setResponseDesceiption(transRespDetails.getDescription());
                    }
                    else
                    {
                        cancelResponse.setStatus(PZResponseStatus.FAILED);
                        cancelResponse.setResponseDesceiption("Transaction cannot be Captured.");
                    }

                }
                else
                {
                    cancelResponse.setStatus(PZResponseStatus.ERROR);
                    cancelResponse.setResponseDesceiption("Error initiating cancellation for transaction");
                }
            }
            else
            {
                cancelResponse.setStatus(PZResponseStatus.ERROR);
                cancelResponse.setResponseDesceiption("Error retrieving auth transaction details");
            }

            setCancelResponseParams(cancelResponse,transRespDetails);

            if(!functions.isValueNull(notificationURL) && functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
            {
                notificationURL = merchantDetailsVO.getNotificationUrl();
            }
            transactionLogger.error("Cancel notificationURL ----> "+trackingId +" "+notificationURL);

            if(functions.isValueNull(notificationURL) && (!PZResponseStatus.PENDING.equals(cancelResponse.getStatus()) || !PZResponseStatus.ERROR.equals(cancelResponse.getStatus()))){

                TransactionManager transactionManager               = new  TransactionManager();
                TransactionDetailsVO transactionDetailsVO1          = transactionManager.getTransDetailFromCommon(trackingId+"");
                if(functions.isValueNull(transactionDetailsVO1.getCcnum()))
                {
                    String cardStr = PzEncryptor.decryptPAN(transactionDetailsVO1.getCcnum());
                    String Expdate = PzEncryptor.decryptExpiryDate(transactionDetailsVO1.getExpdate());
                    transactionDetailsVO1.setCcnum(cardStr);
                    transactionDetailsVO1.setExpdate(Expdate);
                }else{
                    transactionDetailsVO1.setCcnum("");
                    transactionDetailsVO1.setExpdate("");
                }

                transactionDetailsVO1.setSecretKey(merchantDetailsVO.getKey());

                if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                    transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                }else{
                    transactionDetailsVO1.setMerchantNotificationUrl("");
                }

                AsyncNotificationService asyncNotificationService 	= AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId+"", transactionDetailsVO1.getStatus(), transactionDetailsVO1.getRemark());
            }

        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException while cancelling transaction",e);

            PZExceptionHandler.handleTechicalCVEException(e,toid,"cancel");
            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + e.getMessage());
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while cancelling transaction",e);

            PZExceptionHandler.handleCVEException(e,toid,"cancel");
            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + e.getMessage());
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while cancelling transaction",e);

            PZExceptionHandler.handleDBCVEException(e,toid,"cancel");
            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQL Exception while cancelling transaction",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"cancel()",null,"common","System Error:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),toid,"Cancel");
            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while cancelling transaction",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"cancel()",null,"common","System Error:::",PZDBExceptionEnum.SQL_EXCEPTION,null,systemError.getMessage(),systemError.getCause(),toid,"Cancel");
            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + systemError.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return cancelResponse;
    }

    public PZChargebackResponse doChargeback(PZChargebackRequest pzChargebackRequest)
    {
        Connection conn = null;
        Transaction trans = new Transaction();
        PZChargebackResponse chargebackResponse = new PZChargebackResponse();
        try
        {
            Integer trackingId = pzChargebackRequest.getTrackingId();
            Integer accountId = pzChargebackRequest.getAccountId();
            Integer memberId = pzChargebackRequest.getMemberId();
            String cbamount = String.format("%.2f", Double.valueOf(pzChargebackRequest.getCbAmount()));
            String cbreason = pzChargebackRequest.getCbReason();

            boolean isAdmin = pzChargebackRequest.isAdmin();

            if (cbreason == null || cbreason.equals(""))
            {
                cbreason = "N/A";
            }

            try
            {
                if (!Functions.checkAccuracy(cbamount, 2))
                {
                    chargebackResponse.setStatus(PZResponseStatus.FAILED);
                    chargebackResponse.setResponseDesceiption("ChargeBack Amount should be 2 decimal places accurate");
                    return chargebackResponse;
                }
            }
            catch (NumberFormatException e)
            {
                log.debug("Invalid ChargeBack amount" + cbamount + "-" + e.getMessage());
                transactionLogger.debug("Invalid ChargeBack amount" + cbamount + "-" + e.getMessage());
                chargebackResponse.setStatus(PZResponseStatus.FAILED);
                chargebackResponse.setResponseDesceiption("Invalid ChargeBack Amount");
                return chargebackResponse;
            }

            conn = Database.getConnection();
            String transaction = "select trackingid,toid,fromid,description,amount,captureamount-refundamount as cbamount,refundamount,transid,transaction_common.accountid,status,timestamp,name,paymentid,members.contact_emails,members.company_name from transaction_common,members where transaction_common.toid=members.memberid and trackingid=? and transaction_common.accountid = ? and transaction_common.toid = ?";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));
            transPreparedStatement.setString(2, String.valueOf(accountId));
            transPreparedStatement.setString(3, String.valueOf(memberId));
            ResultSet rstransaction = transPreparedStatement.executeQuery();
            AuditTrailVO auditTrailVO = pzChargebackRequest.getAuditTrailVO();
            if (rstransaction.next())
            {
                String trackingIdDB = rstransaction.getString("trackingid");
                String amount = rstransaction.getString("amount");
                String transStatus = rstransaction.getString("status");

                String remark = "";
                if (isAdmin)
                {
                    remark = "ChargeBack BY ADMIN ";
                }

                chargebackResponse.setTrackingId(trackingIdDB);
                chargebackResponse.setDbStatus(transStatus);

                if (transStatus.equals("capturesuccess") || transStatus.equals("markedforreversal") || transStatus.equals("settled") ||
                        (transStatus.equals("reversed") && (Double.valueOf(cbamount) <= rstransaction.getDouble("cbamount"))) ||
                        (transStatus.equals("reversed") && (Double.parseDouble(cbamount) > rstransaction.getDouble("cbamount"))))
                {
                    trans.executeChargeback(String.valueOf(accountId), transStatus, cbreason, amount, cbamount, String.valueOf(trackingId), null, auditTrailVO);
                    chargebackResponse.setStatus(PZResponseStatus.SUCCESS);
                    chargebackResponse.setResponseDesceiption("Transaction successfuly chargebacked");
                }
                else if (transStatus.equals("authstarted") || transStatus.equals("authfailed") || transStatus.equals("begun"))
                {
                    chargebackResponse.setStatus(PZResponseStatus.FAILED);
                    chargebackResponse.setResponseDesceiption("Transaction can not be chargebacked");
                }
                else
                {
                    chargebackResponse.setStatus(PZResponseStatus.FAILED);
                    chargebackResponse.setResponseDesceiption("Transaction not found");
                }

                if (transStatus.equals("chargeback"))
                {
                    chargebackResponse.setStatus(PZResponseStatus.FAILED);
                    chargebackResponse.setResponseDesceiption("Transaction already chargebacked");
                }
            }
            else
            {
                chargebackResponse.setStatus(PZResponseStatus.FAILED);
                chargebackResponse.setResponseDesceiption("Transaction not found");
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error while chargeback transaction via Common Process",systemError);
            transactionLogger.error("System Error while chargeback transaction via Common Process",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"doChargeback()",null,"common","Excpetion while chargeback transaction",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"ChargeBack");
            chargebackResponse.setStatus(PZResponseStatus.ERROR);
            chargebackResponse.setResponseDesceiption("Error during charge back of transaction " + systemError.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQL Exception while chargeback transaction via Common Process",e);
            transactionLogger.error("SQL Exception while chargeback transaction via Common Process",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"doChargeback()",null,"common","Excpetion while chargeback transaction",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"ChargeBack");
            chargebackResponse.setStatus(PZResponseStatus.ERROR);
            chargebackResponse.setResponseDesceiption("Error during charge back of transaction " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return chargebackResponse;
    }
    @Override
    public PZChargebackResponse chargeback(PZChargebackRequest pzChargebackRequest)
    {
        Connection conn = null;
        TransactionEntry transactionEntry = null;
        Transaction trans = new Transaction();
        PZChargebackResponse chargebackResponse = new PZChargebackResponse();
        try
        {
            Integer trackingId = pzChargebackRequest.getTrackingId();
            Integer accountId = pzChargebackRequest.getAccountId();
            Integer memberId = pzChargebackRequest.getMemberId();
            String cbamount = pzChargebackRequest.getCbAmount();
            String cbreason = pzChargebackRequest.getCbReason();
            String captureamount=pzChargebackRequest.getCaptureAmount();
            String bankChargebackDate=pzChargebackRequest.getChargebackDate();
            //String refundAmount=pzChargebackRequest.getRefundAmount();
            boolean isAdmin = pzChargebackRequest.isAdmin();
            conn = Database.getConnection();
            transactionEntry = new TransactionEntry();

            double chargebackAmount=Double.parseDouble(cbamount);
            double captureAmount=Double.parseDouble(captureamount);
            //double refAmount=Double.parseDouble(refundAmount);
            //System.out.println("chargebackAmount::"+chargebackAmount);
            //System.out.println("captureAmount::"+captureAmount);

            if (cbreason == null || cbreason.equals(""))
            {
                cbreason = "N/A";
            }

            if(chargebackAmount>captureAmount){
                chargebackResponse.setStatus(PZResponseStatus.ERROR);
                chargebackResponse.setResponseDesceiption("ChargeBack Amount should Not be greater than Capture Amount");
                return chargebackResponse;
            }

            try
            {
                if (!Functions.checkAccuracy(cbamount, 2))
                {
                    chargebackResponse.setStatus(PZResponseStatus.FAILED);
                    chargebackResponse.setResponseDesceiption("ChargeBack Amount should be 2 decimal places accurate");
                    return chargebackResponse;
                }
            }
            catch (NumberFormatException e)
            {
                log.debug("Invalid ChargeBack amount" + cbamount + "-" + e.getMessage());
                transactionLogger.debug("Invalid ChargeBack amount" + cbamount + "-" + e.getMessage());
                chargebackResponse.setStatus(PZResponseStatus.FAILED);
                chargebackResponse.setResponseDesceiption("Invalid ChargeBack Amount");
                return chargebackResponse;
            }

            String transaction = "select trackingid,toid,fromid,description,amount,captureamount,refundamount,chargebackamount,transid,transaction_common.accountid,status,timestamp,name,paymentid,members.contact_emails,members.company_name from transaction_common,members where transaction_common.toid=members.memberid and trackingid=? and transaction_common.accountid = ? and transaction_common.toid = ?";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));
            transPreparedStatement.setString(2, String.valueOf(accountId));
            transPreparedStatement.setString(3, String.valueOf(memberId));
            ResultSet rstransaction = transPreparedStatement.executeQuery();
            AuditTrailVO auditTrailVO=pzChargebackRequest.getAuditTrailVO();
            log.debug("ActionExecutor Name:"+auditTrailVO.getActionExecutorName());
            if (rstransaction.next())
            {
                String trackingIdDB = rstransaction.getString("trackingid");
                String transid = rstransaction.getString("transid");
                String accountIdDB = rstransaction.getString("accountid");
                String rsdescription = rstransaction.getString("description");
                String amount = rstransaction.getString("amount");
                String transstatus = rstransaction.getString("status");
                String captureAmt=rstransaction.getString("captureamount");
                String refundamount=rstransaction.getString("refundamount");
                String chargebackamount=rstransaction.getString("chargebackamount");

                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountIdDB);
                String remark = "";

                if (isAdmin)
                {
                    remark = "ChargeBack BY ADMIN "; //set in VO object

                }
                /*if (transstatus.equals("chargebackreversed"))
                {*/
                double captureAmountdb=Double.parseDouble(captureAmt);
                double chargebackAmountdb=Double.parseDouble(chargebackamount);

                double minusValue= Double.parseDouble(String.format("%.2f",captureAmountdb-chargebackAmountdb));
                log.error("minusvalue:::"+minusValue);

                if(chargebackAmount>minusValue){
                    chargebackResponse.setStatus(PZResponseStatus.ERROR);
                    chargebackResponse.setResponseDesceiption("ChargeBack Amount should Not be greater than max amount considerable for Chargeback");
                    return chargebackResponse;
                }
               /* }*/

                if (transstatus.equals("capturesuccess"))
                {
                    //int paymentTransId = 0;
                    //paymentTransId = transactionEntry.newGenericCreditTransaction(trackingIdDB, new BigDecimal(amount), accountIdDB, null,auditTrailVO);
                    String query = "update transaction_common set status='settled' where trackingid=?";
                    PreparedStatement p1 = conn.prepareStatement(query);
                    p1.setString(1, trackingIdDB);
                    int rows = p1.executeUpdate();
                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.genericActionEntry(trackingIdDB, amount, ActionEntry.ACTION_CREDIT, ActionEntry.STATUS_CREDIT, null, account.getGateway(), null, auditTrailVO);
                }
                if (transstatus.equals("markedforreversal"))
                {

                    String query = "update transaction_common set status='reversed', refundtimestamp = '" + functions.getTimestamp() + "' where status='markedforreversal' and trackingid=?";
                    PreparedStatement p1 = conn.prepareStatement(query);
                    p1.setString(1, trackingIdDB);
                    int rows = p1.executeUpdate();
                }
                //TODO-Gener
                //trans.genericProcessChargeback(trackingIdDB, null, null,cbamount, cbreason, null,null, account,auditTrailVO);
                ResponseVO responseVO=new ResponseVO();
                responseVO.setResponseTime(bankChargebackDate);
                trans.genericProcessChargebackNew(trackingIdDB, rsdescription, null, cbamount, cbreason, captureAmt,refundamount,chargebackamount,transstatus,responseVO, account, auditTrailVO);
                log.debug("Added charges and changed the status to chargeback");
                transactionLogger.debug("Added charges and changed the status to chargeback");
                chargebackResponse.setStatus(PZResponseStatus.SUCCESS);
                chargebackResponse.setResponseDesceiption(rsdescription);
                chargebackResponse.setTrackingId(trackingIdDB);
            }
            else
            {
                chargebackResponse.setStatus(PZResponseStatus.FAILED);
                chargebackResponse.setResponseDesceiption("Transaction not found");
            }
        }
        catch (SQLException e)
        {
            log.error("SQL Exception while chargeback transaction via Common Process",e);
            transactionLogger.error("SQL Exception while chargeback transaction via Common Process",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"chargeback()",null,"common","Excpetion while chargeback transaction",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"ChargeBack");
            chargebackResponse.setStatus(PZResponseStatus.ERROR);
            chargebackResponse.setResponseDesceiption("Error during charge back of transaction " + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("System Error while chargeback transaction via Common Process",systemError);
            transactionLogger.error("System Error while chargeback transaction via Common Process",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"chargeback()",null,"common","Excpetion while chargeback transaction",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"ChargeBack");
            chargebackResponse.setStatus(PZResponseStatus.ERROR);
            chargebackResponse.setResponseDesceiption("Error during charge back of transaction " + systemError.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
            if(transactionEntry!=null)
            {
                transactionEntry.closeConnection();
            }
        }
        return chargebackResponse;
    }

    @Override
    public PZStatusResponse status(PZStatusRequest pzStatusRequest)
    {
        return null;
    }

    @Override
    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<PZChargebackRecord> readChargebackFile(PZFileVO fileName) throws SystemError
    {
        //throw new SystemError("Functionality is not allowed for selected Account");
        List<PZChargebackRecord> pzChargebackRecordList = new ArrayList();
        try
        {
            //Create a new workbook instance.
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            //Get first sheet from the workbook.
            HSSFSheet sheet = workbook.getSheetAt(0);
            //Iterator through each row one by one.
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            //Skip header rows.
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;


                if(cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                PZChargebackRecord loadTransactions = new PZChargebackRecord();
                try
                {
                    loadTransactions.setTrackingid(row.getCell((short) 0).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setTrackingid(row.getCell((short) 0).getStringCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setTrackingid("");
                }
                try
                {
                    loadTransactions.setPaymentid(row.getCell((short) 1).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setPaymentid(row.getCell((short) 1).getStringCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setPaymentid("");
                }
                try
                {
                    loadTransactions.setCurrency(row.getCell((short) 2).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setCurrency(row.getCell((short) 2).getNumericCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setCurrency("");
                }
                try
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 3).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 3).getStringCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setChargebackAmount("");
                }
                try
                {
                    loadTransactions.setChargebackReason(row.getCell((short) 4).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setChargebackReason(String.valueOf(row.getCell((short) 4).getNumericCellValue()));
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setChargebackReason("");
                }
                try
                {
                    loadTransactions.setDate(row.getCell((short) 5).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setDate(row.getCell((short) 5).getStringCellValue());
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setDate("");
                }
                pzChargebackRecordList.add(loadTransactions);
            }
        }
        catch (Exception e)
        {
            log.error("IOException:::::", e);
        }

        return pzChargebackRecordList;
    }

    @Override
    public List<PZChargebackRecord> readChargebackFileXlsx(PZFileVO fileName) throws SystemError
    {
        //throw new SystemError("Functionality is not allowed for selected Account");
        List<PZChargebackRecord> pzChargebackRecordList = new ArrayList();
        try
        {
            //Create a new workbook instance.
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            //Get first sheet from the workbook.
            XSSFSheet sheet = workbook.getSheetAt(0);
            //Iterator through each row one by one.
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            //Skip header rows.
            while (rows.hasNext())
            {
                XSSFRow row = (XSSFRow) rows.next();
                cntRowToLeaveFromTop++;


                if(cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                XSSFRow row = (XSSFRow) rows.next();
                PZChargebackRecord loadTransactions = new PZChargebackRecord();
                try
                {
                    loadTransactions.setTrackingid(row.getCell((short) 0).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setTrackingid(row.getCell((short) 0).getStringCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setTrackingid("");
                }
                try
                {
                    loadTransactions.setPaymentid(row.getCell((short) 1).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setPaymentid(row.getCell((short) 1).getStringCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setPaymentid("");
                }
                try
                {
                    loadTransactions.setCurrency(row.getCell((short) 2).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setCurrency(row.getCell((short) 2).getNumericCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setCurrency("");
                }
                try
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 3).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 3).getStringCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setChargebackAmount("");
                }
                try
                {
                    loadTransactions.setChargebackReason(row.getCell((short) 4).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setChargebackReason(String.valueOf(row.getCell((short) 4).getNumericCellValue()));
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setChargebackReason("");
                }
                try
                {
                    loadTransactions.setDate(row.getCell((short) 5).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setDate(row.getCell((short) 5).getStringCellValue());
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setDate("");
                }
                pzChargebackRecordList.add(loadTransactions);
            }
        }
        catch (Exception e)
        {
            log.error("IOException:::::", e);
        }

        return pzChargebackRecordList;
    }

    public PZInquiryResponse inquiry(PZInquiryRequest inquiryRequest)
    {
        Connection conn                     = null;
        PZInquiryResponse inquiryResponse   = new PZInquiryResponse();

        Integer trackingId = 0;
        try
        {

            trackingId          = inquiryRequest.getTrackingId();
            Integer accntId     = inquiryRequest.getAccountId();
            Integer memberId    = inquiryRequest.getMemberId();
            String ip           = inquiryRequest.getIpAddress();

            AbstractPaymentGateway pg   = AbstractPaymentGateway.getGateway(String.valueOf(accntId));
            String currency             = pg.getCurrency();
            GatewayAccount account      = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
            String merchantId           = account.getMerchantId();
            String alias                = account.getAliasName();
            String username             = account.getFRAUD_FTP_USERNAME();
            String password             = account.getFRAUD_FTP_PASSWORD();
            String displayname          = account.getDisplayName();


            conn = Database.getConnection();

            CommResponseVO commResponseVO = null;

            String transaction                          = "select * from transaction_common where trackingid=? and toid = ? and accountid = ?";
            PreparedStatement transPreparedStatement    = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));
            transPreparedStatement.setString(2, String.valueOf(memberId));
            transPreparedStatement.setString(3, String.valueOf(accntId));
            ResultSet rstransaction = transPreparedStatement.executeQuery();
            if (rstransaction.next())
            {


                String trackingIdDB = rstransaction.getString("trackingid");
                String ccnum        = rstransaction.getString("ccnum");
                String accountIdDB  = rstransaction.getString("accountid");
                String paymentId    = rstransaction.getString("paymentid");
                String timestamp    = rstransaction.getString("timestamp");
                String amount       = rstransaction.getString("amount");
                String description  = rstransaction.getString("description");
                String dbStatus     = rstransaction.getString("status");
                String sessionId            = rstransaction.getString("podbatch");
                String sessionCode          = rstransaction.getString("boiledname");
                String transactionMode      = rstransaction.getString("transaction_mode");
                String Authorization_code   = rstransaction.getString("Authorization_code");
                String cardtypeid           =  rstransaction.getString("cardtypeid");
                String country           =  rstransaction.getString("country");
                String telno           =  rstransaction.getString("telno");
                String emailaddr           =  rstransaction.getString("emailaddr");
                String totype               =  rstransaction.getString("totype");

                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);
                merchantAccountVO.setAccountId(String.valueOf(accntId));

                CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
                if (functions.isValueNull(ccnum))
                {
                    commCardDetailsVO.setCardNum(ccnum);
                }

                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setOrderId(trackingIdDB);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setResponsetime(timestamp);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setOrderDesc(description);
                commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                commTransactionDetailsVO.setAuthorization_code(Authorization_code);
                commTransactionDetailsVO.setSessionCode(sessionCode);
                commTransactionDetailsVO.setSessionId(sessionId);
                commTransactionDetailsVO.setTransactionmode(transactionMode);
                commTransactionDetailsVO.setCardType(cardtypeid);
                commTransactionDetailsVO.setTotype(totype);

                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                commAddressDetailsVO.setIp(ip);
                commAddressDetailsVO.setCountry(country);
                commAddressDetailsVO.setPhone(telno);
                commAddressDetailsVO.setEmail(emailaddr);


                CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(accntId);

                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setCardDetailsVO(commCardDetailsVO);

                setInquiryVOParamsExtension(commRequestVO, inquiryRequest);
                transactionLogger.error("before if commonpaymentprocess dbStatus-->"+dbStatus);
                if(dbStatus.equalsIgnoreCase("payoutfailed")||dbStatus.equalsIgnoreCase("payoutstarted")||dbStatus.equalsIgnoreCase("payoutsuccessful")){
                    transactionLogger.error("inside  if commonpaymentprocess dbStatus-->"+dbStatus);
                    commResponseVO = (CommResponseVO) pg.processPayoutInquiry(trackingIdDB, commRequestVO);
                }
                else{
                    commResponseVO = (CommResponseVO) pg.processInquiry(commRequestVO);
                    if(commResponseVO == null){
                        commResponseVO = (CommResponseVO) pg.processQuery(trackingIdDB, commRequestVO);
                    }
                }


                if (commResponseVO != null)
                {
                    inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                    inquiryResponse.setTrackingId(trackingIdDB);
                    inquiryResponse.setResponseTransactionId(commResponseVO.getTransactionId());
                    inquiryResponse.setAuthCode(commResponseVO.getAuthCode());
                    inquiryResponse.setResponseTransactionStatus(commResponseVO.getTransactionStatus());
                    inquiryResponse.setTransactionType(commResponseVO.getTransactionType());
                    inquiryResponse.setResponseAmount(commResponseVO.getAmount());
                    inquiryResponse.setResponseCurrency(commResponseVO.getCurrency());
                    inquiryResponse.setResponseTransactionTime(commResponseVO.getBankTransactionDate());
                    inquiryResponse.setMid(commResponseVO.getMerchantId());
                    inquiryResponse.setResponseDescription(commResponseVO.getDescription());
                    inquiryResponse.setResponseTime(commResponseVO.getResponseTime());
                }
                else
                {
                    inquiryResponse.setStatus(PZResponseStatus.PENDING);
                    inquiryResponse.setResponseDesceiption("Operation Not Completed Please Retry In Some Time . TrackingId:" + trackingId);
                    return inquiryResponse;
                }
            }
            else
            {
                inquiryResponse.setStatus(PZResponseStatus.PENDING);
                inquiryResponse.setResponseDesceiption("Operation Not Completed Please Retry In Some Time . TrackingId:" + trackingId);
            }

        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException while inquiring via Common process",e);
            transactionLogger.error("PZTechnicalViolationException while inquiring via Common process",e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.PENDING);
            inquiryResponse.setResponseDesceiption("Operation Not Completed Please Retry In Some Time ");
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while inquiring via Common process",e);
            transactionLogger.error("PZConstraintViolationException while inquiring via Common process",e);

            PZExceptionHandler.handleCVEException(e, null, "Inquiry");

            inquiryResponse.setStatus(PZResponseStatus.PENDING);
            inquiryResponse.setResponseDesceiption("Operation Not Completed Please Retry In Some Time ");
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while inquiring via Common process",e);
            transactionLogger.error("PZDBViolationException while inquiring via Common process",e);

            PZExceptionHandler.handleDBCVEException(e,null,"Inquiry");
            inquiryResponse.setStatus(PZResponseStatus.PENDING);
            inquiryResponse.setResponseDesceiption("Operation Not Completed Please Retry In Some Time ");
        }
        catch (SQLException e)
        {
            log.error("Sql Exception while Iquirying Transaction via Common Process",e);
            transactionLogger.error("Sql Exception while Iquirying Transaction via Common Process",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"inquiry()",null,"common","Exception while Inquiry",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while inquiring via Common process",systemError);
            transactionLogger.error("SystemError while inquiring via Common process",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(), "inquiry", null, "common", "System Error while Inquiring via Common Process", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.PENDING);
            inquiryResponse.setResponseDesceiption("Operation Not Completed Please Retry In Some Time ");
        }
        catch (PZGenericConstraintViolationException e)
        {
            log.error("SystemError while inquiring via Common process",e);
            transactionLogger.error("SystemError while inquiring via Common process",e);

            PZExceptionHandler.handleGenericCVEException(e, null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.PENDING);
            inquiryResponse.setResponseDesceiption("Operation Not Completed Please Retry In Some Time ");
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return inquiryResponse;


    }

    @Override
    public List<PZReconcilationResponce> reconcilationTransaction(List<PZReconcilationRequest> pzReconcilationRequests) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public int actionEntry(String trackingId, String
            amount, String
                                   action, String
                                   status, CommResponseVO
                                   responseVO, CommRequestVO
                                   requestVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {

        String remark = "";

        Connection cn = null;
        if (responseVO != null)
        {
            remark = responseVO.getRemark();
        }
        //log.error("action -----> "+requestVO.getAddressDetailsVO().getCardHolderIpAddress());
        int newDetailId = 0;

        newDetailId = paymentManager.actionEntryForCommon( trackingId,  amount,  action,  status,  responseVO,  requestVO, auditTrailVO, remark);
        actionEntryExtension(newDetailId, trackingId, amount, action, status, responseVO, requestVO);

        return 1;

    }

    public int actionEntry(String trackingId, String
            amount, String
                                   action, String
                                   status, CommResponseVO
                                   responseVO, CommRequestVO
                                   requestVO,AuditTrailVO auditTrailVO,String remark) throws  PZDBViolationException
    {
        int newDetailId = 0;

        newDetailId = paymentManager.actionEntryForCommon( trackingId,  amount,  action,  status,  responseVO,  requestVO, auditTrailVO, remark);
        actionEntryExtension(newDetailId, trackingId, amount, action, status, responseVO, requestVO);

        return 1;



    }

    private String getCookie
            (HttpServletRequest
                     request, HttpServletResponse
                     res) throws SystemError
    {

        log.debug("Inside getCookie");
        transactionLogger.debug("Inside getCookie");

        Cookie cook[] = request.getCookies();
        String name = null;
        String value = null;
        if (cook != null)
        {
            for (int i = 0; i < cook.length; i++)
            {
                name = cook[i].getName();
                if (name.equals("mid"))
                {
                    value = cook[i].getValue();
                    break;
                }
            }
        }
        log.debug("leaving  getCookie" + value);
        transactionLogger.debug("leaving  getCookie" + value);
        return value;
    }

    public void setTransactionVOParamsExtension
            (CommRequestVO
                     requestVO, CommonValidatorVO commonValidatorVO) throws  PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTransactionVOParamsExtension
            (CommRequestVO
                     requestVO, Map
                     transactionRequestPArams) throws  PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setTransactionResponseVOParamsExtension
            (CommResponseVO
                     responseVO,Map
                     transactionResponseParams) throws  PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTransactionResponseVOParamsExtension
            (CommResponseVO
                     responseVO,DirectCommResponseVO directCommResponseVO) throws  PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setPayoutTransactionResponseVOParamsExtension
            (CommResponseVO
                     responseVO,PZPayoutResponse payoutResponse) throws  PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setTransactionResponseVOParamsExtension
            (CommResponseVO
                     responseVO,VTResponseVO vtResponseVO)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setCaptureVOParamsExtension
            (CommRequestVO
                     requestVO, PZCaptureRequest
                     captureRequest) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setCaptureResponseParams
            (PZCaptureResponse captureResponseVO,CommResponseVO commResponseVO) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setCancelResponseParams
            (PZCancelResponse cancelResponseVO,CommResponseVO commResponseVO) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setRefundVOParamsextension
            (CommRequestVO
                     requestVO, PZRefundRequest
                     refundRequest) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setPayoutVOParamsextension
            (CommRequestVO
                     requestVO, PZPayoutRequest
                     payoutRequest) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public void setInquiryVOParamsExtension
            (CommRequestVO
                     requestVO, PZInquiryRequest
                     pzInquiryRequest) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEnrollmentRequestVOExtention(EnrollmentRequestVO enrollmentRequestVO, TransactionDetailsVO transactionDetailsVO) throws PZDBViolationException
    {

    }

    public void setCancelVOParamsExtension
            (CommRequestVO
                     requestVO, PZCancelRequest
                     cancelRequest) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int actionEntryExtension
            (
                    int newDetailId, String
                    trackingId, String
                            amount, String
                            action, String
                            status, CommResponseVO
                            responseVO, CommRequestVO
                            commRequestVO) throws PZDBViolationException
    {
        return 0;
    }

    public StringBuffer processFraudulentTransactions(int accountid, List<PZTC40Record> vTransactions, String toAddress, String isRefund)
    {
        StringBuffer errString = new StringBuffer();
        StringBuffer eString = new StringBuffer();
        errString.append("<br><b>  Tracking ID  |   Merchant ID  |   Account ID  |   Order ID   |   Remark   </b>");

        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        Connection conn = null;
        PZRefundRequest refundRequest = new PZRefundRequest();
        PZRefundResponse response = new PZRefundResponse();
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        for(PZTC40Record pztc40Record : vTransactions)
        {
            log.debug("Display error message-----");
            String paymentId = "";
            if (functions.isValueNull(pztc40Record.getOrderno()))
            {
                paymentId = pztc40Record.getOrderno();
            }
            else
            {
                errString.append("<br>  -  |   -  |   -  |   -   |   Invalid/Missing Payment Order Number   ");
                continue;
            }

            try
            {
                conn = Database.getConnection();
                String query = "select toid,description,captureamount,trackingid,status from transaction_common where paymentid=? and accountid=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, paymentId);
                pstmt.setString(2, String.valueOf(accountid));
                log.debug("Get details from transaction_common---" + pstmt);
                ResultSet res = pstmt.executeQuery();
                if (res.next())
                {
                    continue;
                }
                else
                {
                    eString.append("Selected Bank Account does not match with the provided TC40 File transaction records");
                    //break;
                    return eString;
                }
            }
            catch (Exception e)
            {
                log.error("Exception---",e);
            }
            finally
            {
                Database.closeConnection(conn);
            }
            return eString;
        }

        try
        {
            conn = Database.getConnection();

            auditTrailVO.setActionExecutorId("0");
            auditTrailVO.setActionExecutorName("Upload Bulk Fraud");
            for (PZTC40Record pztc40Record : vTransactions)
            {

                String paymentId = "";
                if (functions.isValueNull(pztc40Record.getOrderno()))
                {
                    paymentId = pztc40Record.getOrderno();
                }
                else
                {
                    errString.append("<br>  -  |   -  |   -  |   -   |   Invalid/Missing Payment Order Number   ");
                    continue;
                }

                String toid = "";
                String description = "";
                String captureAmount = "";
                String trackingid = "";
                String status = "";

                String query = "select toid,description,captureamount,trackingid,status from transaction_common where paymentid=? and accountid=?";
                PreparedStatement p1 = conn.prepareStatement(query);
                p1.setString(1, paymentId);
                p1.setString(2, String.valueOf(accountid));
                ResultSet rs = p1.executeQuery();

                if(rs.next())
                {
                    toid = rs.getString("toid");
                    description = rs.getString("description");
                    captureAmount = rs.getString("captureamount");
                    trackingid = rs.getString("trackingid");
                    status = rs.getString("status");
                }
                else
                {
                    errString.append("<br> - |    -   |  " + accountid + "  |  " + pztc40Record.getOrderno() + "  |  Transaction Not Found  \r\n");
                    continue;
                }

                if("Y".equalsIgnoreCase(isRefund))
                {
                    refundRequest.setMemberId(Integer.valueOf(toid));
                    refundRequest.setAccountId(Integer.valueOf(accountid));
                    refundRequest.setTrackingId(Integer.parseInt(trackingid));
                    refundRequest.setRefundAmount(captureAmount);
                    refundRequest.setRefundReason("Fraudulent Transaction");
                    refundRequest.setAdmin(true);
                    refundRequest.setIpAddress("");
                    refundRequest.setFraud(true);
                    refundRequest.setAuditTrailVO(auditTrailVO);

                    response = refund(refundRequest);

                    PZResponseStatus refundStatus = response.getStatus();
                    String mailStatus = "";
                    if (response != null && PZResponseStatus.SUCCESS.equals(refundStatus))
                    {
                        errString.append("<br>  " + trackingid + "  |   " + toid + "  |   " + accountid + "  |   " + description + "   |   "+response.getResponseDesceiption()+"   ");
                        //update ststus
                        statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingid,"fraud",conn);
                        mailStatus = "successful";
                    }
                    else
                    {
                        mailStatus = "failed";
                        errString.append("<br>  " + trackingid + "  |   " + toid + "  |   " + accountid + "  |   " + description + "   |   "+response.getResponseDesceiption()+"   ");
                        continue;
                    }

                    SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION,trackingid,mailStatus,"Refund Fraudulent Transaction",null);
                }
                else
                {
                    statusSyncDAO.updateAllFraudulantTransactionFlowFlag(trackingid, "fraud", conn);
                    errString.append("<br>|  " + trackingid + "  |   " + toid + "  |   " + accountid + "  |   " + description + "   |   Transaction marked as Fraud   |");

                    SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.FRAUD_TRANSACTION_MARKED,trackingid,"","Fraudulent Transaction Marked",null);
                }

            }
        }
        catch (SystemError se)
        {
            log.error("Exception in processFradulantTransactions---",se);
        }
        catch (SQLException se)
        {
            log.error("Exception in processFradulantTransactions---",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return errString;
    }


    public String processSettlement_Old(int accountid, List<PZSettlementRecord> vTransactions, String toAddress)
    {
        StringBuffer errString = new StringBuffer();
        errString.append("<br>|  Tracking ID  |   Merchant ID  |   Account ID  |        Order ID       |  Amount  |  Current Status  |      Treatment Given     |" + "\r\n");

        TransactionEntry transactionEntry = null;
        Transaction transaction = null;
        Connection conn = null;

        transactionEntry = new TransactionEntry();
        transaction = new Transaction();
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        List vErrRecords = new ArrayList();

        for (PZSettlementRecord vSettleTransVO : vTransactions)
        {
            int trackingId = 0;
            try
            {
                conn = Database.getConnection();
                String vPaymentOrderId = vSettleTransVO.getPaymentid();
                String vTrackingId = vSettleTransVO.getMerchantTXNid();
                if (functions.isValueNull(vTrackingId))
                {
                    vTrackingId.trim();
                }
                String vAmount = vSettleTransVO.getAmount();

                //To be used to updated detail table
                CommResponseVO commResponseVO = new CommResponseVO();
                commResponseVO.setErrorCode(vPaymentOrderId);
                commResponseVO.setDescription("Updated via gateway Settlement cron");
                commResponseVO.setTransactionId(vPaymentOrderId);

                String query = "select toid,fromid,description,captureamount,amount,trackingid,accountid,status from transaction_common where trackingid=?";
                PreparedStatement p1 = conn.prepareStatement(query);
                p1.setString(1, vTrackingId);
                ResultSet rs = p1.executeQuery();
                boolean isRecordFound = false;
                isRecordFound = rs.next();
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                if (isRecordFound)
                {
                    int toid = rs.getInt("toid");
                    String fromid = rs.getString("fromid");
                    String description = rs.getString("description");
                    trackingId = rs.getInt("trackingid");
                    int accountId = rs.getInt("accountid");

                    auditTrailVO.setActionExecutorId(String.valueOf(toid));
                    auditTrailVO.setActionExecutorName("Admin");
                    //Processing Failed Transactions
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.AUTH_FAILED))
                            || vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CAPTURE_FAILED)))
                    {
                        //Variable Definition
                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_FAILED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_FAILED)))
                        {
                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            //transactionEntry.newGenericFailedTransaction(String.valueOf(trackingId), String.valueOf(accountId), null);
                            statusSyncDAO.updateSettlementTransactionCronFlagNew(String.valueOf(trackingId), "authfailed");
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");
                        }
                        else if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {
                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            //update the status, PaymentOrderNumber and remark column for this transaction
                            if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                query = "update transaction_common set paymentid=?, status='authfailed', remark='Bank Connectivity Issue' where trackingid=? ";
                            else
                                query = "update transaction_common set paymentid=?, status='capturefailed', remark='Bank Connectivity Issue' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, vTrackingId);
                            int result = ps.executeUpdate();
                            if (result == 1)
                            {
                                //insert the status change action entry for authfailed to details table
                                statusSyncDAO.updateSettlementTransactionCronFlagNew(String.valueOf(trackingId), "authfailed");
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, null, auditTrailVO);
                                if (num == 1)
                                {
                                    //now as it is in normal authfailed state, you may apply failure charges
                                    //transactionEntry.newGenericFailedTransaction(String.valueOf(trackingId), String.valueOf(accountId), null);
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Settled" + "\r\n");
                                }
                            }
                        }
                        else
                        {
                            vErrRecords.add(trackingId);
                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction will not be Processed for " + vSettleTransVO.getPaymentid() + "\r\n");

                            //errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + +"Transaction NOT FOUND" + "\r\n");
                        }
                    }
                    else if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.SETTLED)) ////Processing Successful Transactions which are not Refunded or not Chargebacked
                            || vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS)))
                    {
                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS)))
                        {


                            BigDecimal amount = new BigDecimal(rs.getString("captureamount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);

                            query = "update transaction_common set status='settled' where trackingid=? ";

                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vTrackingId);
                            int result = ps.executeUpdate();
                            if (result == 1)
                            {
                                //insert the status change action entry for capturesuccess to details table
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_CREDIT, ActionEntry.STATUS_CREDIT, commResponseVO, null, auditTrailVO);
                                //GatewayAccount vGatewayAccount = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                                //transactionEntry.newGenericCreditTransaction(String.valueOf(trackingId), amount, String.valueOf(accountId), null, auditTrailVO);
                                statusSyncDAO.updateSettlementTransactionCronFlagNew(String.valueOf(trackingId), "capturesuccess");
                                errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");
                            }
                        }
                        else if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {
                            BigDecimal amount = new BigDecimal(vAmount);
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);

                            int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, null, auditTrailVO, null);

                            //update the status,captureamount, PaymentOrderNumber and remark column for this transaction
                            query = "update transaction_common set paymentid=?, status='settled',captureamount=?, remark='Settled via gateway report' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, amount.toString());
                            ps.setString(3, vTrackingId);
                            int result = ps.executeUpdate();
                            auditTrailVO.setActionExecutorId(vSettleTransVO.getMerchantid());
                            auditTrailVO.setActionExecutorName("Admin");
                            if (result == 1)
                            {
                                //insert the status change action entry for capture success to details table
                                int num2 = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_CREDIT, ActionEntry.STATUS_CREDIT, commResponseVO, null, auditTrailVO);

                                if (num2 == 1)
                                {
                                    //now as it is in normal capture sucess state, you may apply settle charges
                                    //GatewayAccount vGatewayAccount = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));


                                    //transactionEntry.newGenericCreditTransaction(String.valueOf(trackingId), amount, String.valueOf(accountId), null, auditTrailVO);

                                    statusSyncDAO.updateSettlementTransactionCronFlagNew(String.valueOf(trackingId), "capturesuccess");
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");
                                }
                            }
                        }
                        else
                        {

                            vErrRecords.add(trackingId);
                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction will not be Processed for " + vSettleTransVO.getPaymentid() + "\r\n");

                            //vErrRecords.add(trackingId);
                            //errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                        }
                    }
                    ////Processing Chargeback Transactions
                    else if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CHARGEBACK)))
                    {
                        statusSyncDAO.updateSettlementTransactionCronFlagNew(String.valueOf(trackingId), "chargeback");
                        errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction Must be in Chargeback Status" + "\r\n");
                    }
                    //Processing Refund
                    else if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.REVERSED)))
                    {
                        statusSyncDAO.updateSettlementTransactionCronFlagNew(String.valueOf(trackingId), "reversed");
                        if (!rs.getString("status").equals("reversed"))
                        {
                            errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction Must be in Reverse Status" + "\r\n");
                        }
                    }
                }
                else
                {


                    vErrRecords.add(trackingId);
                    errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException while processing Settlement via Common Process", e);
                transactionLogger.error("PZDBViolationException while processing Settlement via Common Process", e);
                PZExceptionHandler.handleDBCVEException(e, null, "processSettlement");
                vErrRecords.add(trackingId);
            }
            catch (SQLException e)
            {
                log.error("SQLException while processing Settlement via Common Process", e);
                transactionLogger.error("SQLException while processing Settlement via Common Process", e);
                PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(), "processSettlement()", null, "common", "Exception while processing Settlement", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, "processSettlement");
                vErrRecords.add(trackingId);
            }
            catch (SystemError systemError)
            {
                log.error("SystemError while processing Settlement via Common Process", systemError);
                transactionLogger.error("SystemError while processing Settlement via Common Process", systemError);
                PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(), "processSettlement()", null, "common", "Exception while processing Settlement", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "processSettlement");
                vErrRecords.add(trackingId);
            }
            finally
            {
                Database.closeConnection(conn);
            }

        }
        /*}*/
       /* finally
        {
            Database.closeConnection(conn);
        }*/
        String vMailSend = "";
        try
        {
            transaction.sendSettlementCron(errString, toAddress);
        }
        catch (SystemError systemError)
        {
            log.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");
            transactionLogger.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"processSettlement()",null,"common","Exception sending mail",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"processSettlement");
            vMailSend = "Mail Sending to admin is failed.";
        }
        return "Settlement File uploaded successfully. " + vMailSend + "\r\n" + errString.toString();
    }

    public String processSettlement(int accountid, List<PZSettlementRecord> vTransactions, String toAddress, AuditTrailVO auditTrailVO,String tablename)
    {
        log.error("Inside processSettlement::::::");
        StringBuffer errString = new StringBuffer();
        TransactionManager transactionManager = new TransactionManager();
        SettlementManager settlementManager = new SettlementManager();

        errString.append("<br> |  Tracking ID  |   Merchant ID  |   Account ID  |        Order ID       |  Amount  |  Current Status  |      Treatment Given     |" + "\r\n");
        log.error("total transactions in excel::::" + vTransactions.size());
        for (PZSettlementRecord vSettleTransVO : vTransactions)
        {
            try
            {
                String vTrackingId = vSettleTransVO.getMerchantTXNid();
                String vPaymentOrderId = vSettleTransVO.getPaymentid();
                String vAmount = vSettleTransVO.getAmount();
                String excelStatus = vSettleTransVO.getStatusDetail();

                log.error("Before trim from excel:" + vTrackingId + ":" + vPaymentOrderId + ":" + vAmount + ":" + excelStatus);

                if (functions.isValueNull(vTrackingId))
                {
                    vTrackingId = vTrackingId.trim();
                }
                if (functions.isValueNull(vPaymentOrderId))
                {
                    vPaymentOrderId = vPaymentOrderId.trim();
                }
                if (functions.isValueNull(excelStatus))
                {
                    excelStatus = excelStatus.trim();
                }
                log.error("After trim from excel:" + vTrackingId + ":" + vPaymentOrderId + ":" + vAmount + ":" + excelStatus);

                //To be used to updated detail table
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransactionDetailsToProcessCommonSettlement(vTrackingId,tablename);
                if (transactionDetailsVO != null)
                {
                    log.error(vTrackingId + " Transaction found in table");
                    int toId = Integer.parseInt(transactionDetailsVO.getToid());
                    int trackingId = Integer.parseInt(transactionDetailsVO.getTrackingid());
                    int accountId = Integer.parseInt(transactionDetailsVO.getAccountId());

                    String description = transactionDetailsVO.getDescription();
                    String tblTransStatus = transactionDetailsVO.getStatus();
                    String tblAmount = transactionDetailsVO.getAmount();

                    log.error("before trim and data from table:" + toId + ":" + trackingId + ":" + trackingId + ":" + accountId + ":" + description + ":" + tblTransStatus);

                    if (functions.isValueNull(description))
                    {
                        description = description.trim();
                    }
                    if (functions.isValueNull(tblTransStatus))
                    {
                        tblTransStatus = tblTransStatus.trim();
                    }

                    log.error("after trim and data from table:" + toId + ":" + trackingId + ":" + trackingId + ":" + accountId + ":" + description + ":" + tblTransStatus);


                    if (PZTransactionStatus.SETTLED.toString().equalsIgnoreCase(excelStatus) || PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(excelStatus))
                    {
                        log.error(trackingId + " inside settled and capturesuccess from excel ");
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(tblTransStatus))
                        {
                            log.error(trackingId + " inside capturesuccess from table -" +tablename);
                            String opStatus = settlementManager.settleTheTransactionBasedOnTable(vTrackingId,tablename);
                            log.error(trackingId + " has settled");
                            if ("success".equalsIgnoreCase(opStatus))
                            {
                                //insert the status change action entry for capturesuccess to details table
                                actionEntry(vTrackingId, tblAmount, ActionEntry.ACTION_CREDIT, ActionEntry.STATUS_CREDIT, null, null, auditTrailVO);
                                errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  " + "Transaction has settled" + "\r\n");
                            }
                        }
                        else if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(tblTransStatus) || PZTransactionStatus.CAPTURE_STARTED.toString().equalsIgnoreCase(tblTransStatus))
                        {
                            log.error(trackingId + " inside authstarted and capturestarted from table");
                            //update the status,captureamount, PaymentOrderNumber and remark column for this transaction
                            String opStatus = settlementManager.captureTheTransaction(vTrackingId, vPaymentOrderId, vAmount);
                            if ("success".equalsIgnoreCase(opStatus))
                            {
                                actionEntry(vTrackingId, tblAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, null, null, auditTrailVO, null);
                                //settle the transaction
                                String settledResult = settlementManager.settleTheTransaction(vTrackingId);
                                if ("success".equalsIgnoreCase(settledResult))
                                {
                                    //insert the status change action entry for capture success to details table
                                    actionEntry(vTrackingId, tblAmount.toString(), ActionEntry.ACTION_CREDIT, ActionEntry.STATUS_CREDIT, null, null, auditTrailVO);
                                    errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  " + "Status correction done.Transaction Settled" + "\r\n");
                                }
                            }
                        }
                        else
                        {
                            log.error(trackingId + " not found in (capturesuccess,authstarted,capturestarted)");
                            errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  " + "1. Transaction cannot be Processed for " + vSettleTransVO.getPaymentid() + "\r\n");
                        }
                    }
                    //Processing Failed Transactions
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(excelStatus) || PZTransactionStatus.CAPTURE_FAILED.toString().equalsIgnoreCase(excelStatus))
                    {
                        log.error(trackingId + " inside authfailed and capturefailed from excel");
                        //Variable Definition
                        if (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(tblTransStatus) || PZTransactionStatus.CAPTURE_FAILED.toString().equalsIgnoreCase(tblTransStatus))
                        {
                            log.error(trackingId + " inside authfailed and capturefailed from table");
                            errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  " + "Declined Transaction" + "\r\n");
                        }
                        else if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(tblTransStatus) || PZTransactionStatus.CAPTURE_STARTED.toString().equalsIgnoreCase(tblTransStatus))
                        {
                            log.error(trackingId + " inside authstarted and capturestarted from excel");
                            //update the status, PaymentOrderNumber and remark column for this transaction
                            String opResult = settlementManager.declineTheTransaction(vTrackingId, vPaymentOrderId);
                            log.error(trackingId + " transaction has declined");
                            if ("success".equalsIgnoreCase(opResult))
                            {
                                //insert the status change action entry for authfailed to details table
                                actionEntry(vTrackingId, tblAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, null, null, auditTrailVO);
                                //now as it is in normal authfailed state, you may apply failure charges
                                errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  " + "Status correction done. Transaction Declined" + "\r\n");
                            }
                        }
                        else
                        {
                            log.error(trackingId + " not found in (authfailed,capturefailed,authstarted,capturestarted)");
                            errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  " + "2. Transaction cannot be processed for " + vSettleTransVO.getPaymentid() + "\r\n");
                        }
                    }
                    ////Processing Chargeback Transactions
                    else if (PZTransactionStatus.CHARGEBACK.toString().equalsIgnoreCase(excelStatus))
                    {
                        log.error(trackingId + " inside chargeback from excel");
                        errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  Transaction need to be in chargeback status" + "\r\n");
                        //errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction must be in chargeback status" + "\r\n");
                    }
                    //Processing Refund
                    else if (PZTransactionStatus.REVERSED.toString().equalsIgnoreCase(excelStatus))
                    {
                        log.error(trackingId + " inside reversed from excel");
                        errString.append("<br>" + trackingId + "  |  " + toId + "  |  " + accountId + "  |  " + description + "  |  " + tblAmount + "  |  " + tblTransStatus + "  |  Transaction need to be in reversed status" + "\r\n");
                    }
                }
                else
                {
                    errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException while processing Settlement via Common Process", e);
                transactionLogger.error("PZDBViolationException while processing Settlement via Common Process", e);
                PZExceptionHandler.handleDBCVEException(e, null, "processSettlement");
            }
        }
        String vMailSend = "";
        try
        {
            MailService mailService = new MailService();
            mailService.sendSettlementCron(errString, toAddress);
        }
        catch (SystemError systemError)
        {
            log.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");
            transactionLogger.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");
            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(), "processSettlement()", null, "common", "Exception sending mail", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "processSettlement");
            vMailSend = "Mail Sending to admin is failed.";
        }
        return "Settlement File uploaded successfully. " + vMailSend + "\r\n" + errString.toString();
    }

    public StringBuffer processChargeback(List<PZChargebackRecord> vTransactions, String gateway, AuditTrailVO auditTrailVO)
    {
        CommonPaymentProcess commProcess            = new CommonPaymentProcess();
        TransactionManager transactionManager       = new TransactionManager();
        StringBuffer returnMsg                      = new StringBuffer();
        MerchantDetailsVO merchantDetailsVO         = null;
        MerchantDAO merchantDAO                     = new MerchantDAO();
        String mailStatus       = "";

        String trackingId       = "";
        String cbAmount         = "";
        String previousStatus   = "";
        String toId             = "";
        String accountId        = "";
        String amount           = "";
        String fromtype         = "";
        String currency         = "";
        String refundamout      = "";

        returnMsg.append("<tr class=\"tdstyle texthead\"><td>  Tracking Id  </td><td> Merchant Id  </td><td>   Account Id  </td><td>   Payment Id  </td><td>  Amount  </td><td>  CB Amount  </td><td>    Status   </td><td>   Status Description    </td><td>   Previous Status   </td><td>   Current Status   </td></tr>");

        for(PZChargebackRecord pzChargebackRecord : vTransactions)
        {
            try
            {
                if (functions.isValueNull(pzChargebackRecord.getTrackingid()))
                {
                    trackingId = pzChargebackRecord.getTrackingid();
                }
                else
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    TrackingId is  missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }

                if (functions.isValueNull(pzChargebackRecord.getChargebackAmount()))
                {
                    cbAmount = pzChargebackRecord.getChargebackAmount();
                }
                else
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback amount is missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if(!functions.isValueNull(pzChargebackRecord.getCurrency()))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback currency is missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if(!functions.isValueNull(pzChargebackRecord.getChargebackReason()))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback reason is missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }

                if (!ESAPI.validator().isValidInput("cbamount", pzChargebackRecord.getChargebackAmount(), "Amount", 12, false))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Invalid Chargeback amount   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if (!ESAPI.validator().isValidInput("currency", pzChargebackRecord.getCurrency(), "StrictString", 3, false))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Invalid Chargeback currency   </td><td> - </td><td> - </td></tr>");
                    continue;
                }


                TransactionDetailsVO transactionDetailsVO = transactionManager.getCommonTransactionDetailsForChargeBack(trackingId);
                if (transactionDetailsVO != null)
                {
                    toId = transactionDetailsVO.getToid();
                    previousStatus = transactionDetailsVO.getStatus();
                    accountId = transactionDetailsVO.getAccountId();
                    amount = transactionDetailsVO.getAmount();
                    fromtype = transactionDetailsVO.getFromtype();
                    currency = transactionDetailsVO.getCurrency();
                    refundamout= transactionDetailsVO.getRefundAmount();

                }
                else
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Record not found   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if(functions.isValueNull(gateway) && !gateway.equalsIgnoreCase(fromtype))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + " </td><td>     -   </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + " </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + " </td><td>  " + "   failed  </td><td>    Record not found for "+gateway+" </td><td> -</td><td> - </td></tr>");
                    continue;
                }
                if(functions.isValueNull(currency) && !currency.equalsIgnoreCase(pzChargebackRecord.getCurrency()))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + " </td><td>     -   </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -   </td><td>  " + pzChargebackRecord.getChargebackAmount() + " </td><td>  " + "   failed  </td><td>    Currency mismatch  </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if(Double.parseDouble(cbAmount)>(Double.parseDouble(amount)-Double.parseDouble(refundamout))){
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + " </td><td>     -   </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -   </td><td>  " + pzChargebackRecord.getChargebackAmount() + " </td><td>  " + "   failed  </td><td>    ChargeBack Amount should Not be greater then Capture Amount  </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                PZChargebackRequest request = new PZChargebackRequest();
                request.setTrackingId(Integer.parseInt(trackingId));
                request.setAccountId(Integer.parseInt(accountId));
                request.setMemberId(Integer.parseInt(toId));
                request.setCbAmount(cbAmount);
                request.setCbReason(pzChargebackRecord.getChargebackReason());
                request.setAdmin(true);
                request.setAuditTrailVO(auditTrailVO);
                PZChargebackResponse pzChargebackResponse = commProcess.doChargeback(request);

                if (pzChargebackResponse != null && (pzChargebackResponse.getStatus()).equals(PZResponseStatus.SUCCESS))
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + toId + "  </td><td>  " + accountId + "  </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>  " + String.format("%.2f", Double.valueOf(amount)) + "  </td><td>  " + String.format("%.2f", Double.valueOf(pzChargebackRecord.getChargebackAmount())) + "  </td><td>  " + "   success   </td><td>   " + pzChargebackResponse.getResponseDesceiption() + "  </td><td>  " + previousStatus + "  </td><td>  " + "chargeback </td></tr>");
                    mailStatus = "success";
                }
                else
                {
                    if (pzChargebackResponse.getDbStatus() == null)
                    {
                        returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + toId + "  </td><td>  " + accountId + "  </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>  " + String.format("%.2f", Double.valueOf(amount)) + "  </td><td>  " + String.format("%.2f", Double.valueOf(pzChargebackRecord.getChargebackAmount())) + "  </td><td>  " + "   failed   </td><td>   " + pzChargebackResponse.getResponseDesceiption() + "  </td><td>  " + previousStatus + "  </td><td>  " + pzChargebackResponse.getDbStatus() + " </td></tr>");
                    }
                    else
                    {
                        returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + toId + "  </td><td>  " + accountId + "  </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>  " + String.format("%.2f", Double.valueOf(amount)) + "  </td><td>  " + String.format("%.2f", Double.valueOf(pzChargebackRecord.getChargebackAmount())) + "  </td><td>  " + "   failed   </td><td>   " + pzChargebackResponse.getResponseDesceiption() + "  </td><td>  " + pzChargebackResponse.getDbStatus() + "  </td><td>  " + pzChargebackResponse.getDbStatus() + "</td></tr>");
                    }
                    continue;
                }
                try
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    String notificationUrl = transactionDetailsVO.getNotificationUrl();
                    String notification_status ="chargeback";
                    log.error("Notification Sending to---" + notificationUrl + "---" + trackingId + " status " + notification_status + pzChargebackResponse.getResponseDesceiption());

                    log.error("ChargebackNotification flag for ---" + toId + "---" + merchantDetailsVO.getChargebackNotification());
                    if (functions.isValueNull(notificationUrl) && "Y".equals(merchantDetailsVO.getChargebackNotification()))
                    {
                        log.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, notification_status, pzChargebackResponse.getResponseDesceiption());
                    }
                }catch(Exception e){
                    log.error("PZDBViolationException--->", e);
                }
                if ("Y".equalsIgnoreCase(merchantDetailsVO.getChargebackMailsend()))
                {
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.CHARGEBACK_TRANSACTION, trackingId, mailStatus, request.getCbReason(), null);
                }
                String reason = rb.getString("reason");
                String reason1[] = reason.split("\\|");
                TransactionDAO transactionDAO = new TransactionDAO();
                if (ArrayUtils.contains(reason1, pzChargebackRecord.getChargebackReason()))
                {
                    boolean status = transactionDAO.getUploadBulkFraudFile(trackingId);
                    BlacklistManager blacklistManager = new BlacklistManager();
                    WhiteListManager whiteListManager = new WhiteListManager();
                    String blacklistReason = (pzChargebackRecord.getChargebackReason() +" (Chargeback by Admin) "+"(" + trackingId + ")");
                    try
                    {
                        blacklistManager.blacklistEntities(trackingId, blacklistReason,BlacklistEnum.Chargeback_Received.toString(),auditTrailVO);
                        whiteListManager.whiteListEntities(trackingId);
                        if("UPI".equalsIgnoreCase(transactionDetailsVO.getCardtype()) && functions.isValueNull(transactionDetailsVO.getCustomerId()))
                        {
                            //vpa Address store in customerId field while transaction
                            blacklistManager.addCustomerVPAAddressBatch(transactionDetailsVO.getCustomerId(),pzChargebackRecord.getChargebackReason(),auditTrailVO);
                        }
                        Set<String> cardNum = new HashSet<>();
                        Set<String> emailAddr = new HashSet<>();
                        Set<String> phone=new HashSet<>();
                        /*if(functions.isValueNull(transactionDetailsVO.getName()))
                            transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr,phone);*/
                        log.error("emailAddr size---->"+emailAddr.size());
                        log.error("cardNum size---->"+cardNum.size());
                        try
                        {
                            if(emailAddr.size()>0)
                                blacklistManager.addCustomerEmailBatch(emailAddr, pzChargebackRecord.getChargebackReason(),auditTrailVO);
                        }catch (PZDBViolationException e){
                            log.error("Duplicate Entry:::" , e);
                        }
                       /* try
                        {
                            if(phone.size()>0)
                                blacklistManager.addCustomerPhoneBatch(phone, pzChargebackRecord.getChargebackReason(),auditTrailVO);
                        }catch (PZDBViolationException e){
                            log.error("Duplicate Entry:::" , e);
                        }*/
                        try{
                            if(cardNum.size()>0)
                                blacklistManager.addCustomerCardBatch(cardNum, pzChargebackRecord.getChargebackReason(),BlacklistEnum.Chargeback_Received.toString(),auditTrailVO);
                        }
                        catch (PZDBViolationException e){
                            log.error("Duplicate Entry:::" , e);
                        }
                        try{
                            //whiteListManager.removeCardEmailEntry(emailAddr, cardNum);
                            whiteListManager.updateCardEmailEntry(emailAddr, cardNum);
                        }
                        catch (PZDBViolationException e){
                            log.error("Duplicate Entry:::" , e);
                        }
                    }
                    catch (PZDBViolationException e)
                    {
                        log.error("Duplicate Entry:::::" , e);
                    }
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException::::", e);
            }
        }
        return returnMsg;
    }

    /**
     * Return Admin Email Address to be used to send the settlement report.
     * This to be overwrite by implementing PaymentProcess else it will return default value
     *
     * @return
     */
    public String getAdminEmailAddress()
    {
        return ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
    }

    @Override
    public String getSpecificCreditPageTemplate()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Hashtable specificValidationAPI(Hashtable<String, String> transactionParameters)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String specificValidationAPI(HttpServletRequest request)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Hashtable specificValidation(Map<String, String> transactionRequestParameters)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public String getSpecificVirtualTerminalJSP()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTransactionVOExtension(CommRequestVO commRequestVO,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Hashtable validateMandatoryParameter(Map<String, String> otherDetail)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();
        hashTable.put(InputFields.TOID_CAPS, otherDetail.get("TOID"));
        hashTable.put(InputFields.COUNTRY, otherDetail.get("country"));
        hashTable.put(InputFields.FIRSTNAME, otherDetail.get("firstname"));
        hashTable.put(InputFields.LASTNAME, otherDetail.get("lastname"));
        hashTable.put(InputFields.EMAILADDR, otherDetail.get("emailaddr"));
        hashTable.put(InputFields.PAN, otherDetail.get("PAN"));
        hashTable.put(InputFields.CVV, otherDetail.get("ccid"));
        hashTable.put(InputFields.STREET, otherDetail.get("street"));
        hashTable.put(InputFields.CITY, otherDetail.get("city"));
        hashTable.put(InputFields.TELNO, otherDetail.get("telno"));
        hashTable.put(InputFields.TELCC, otherDetail.get("telnocc"));
        hashTable.put(InputFields.STATE, otherDetail.get("state"));
        hashTable.put(InputFields.NAME, otherDetail.get("NAMECP"));
        hashTable.put(InputFields.CARDTYPE_CAPS, otherDetail.get("CARDTYPE"));
        hashTable.put(InputFields.ADDRCP, otherDetail.get("ADDRCP"));
        hashTable.put(InputFields.EXPIRE_MONTH, otherDetail.get("EXPIRE_MONTH"));
        hashTable.put(InputFields.EXPIRE_YEAR, otherDetail.get("EXPIRE_YEAR"));
        hashTable.put(InputFields.CCCP, otherDetail.get("CCCP"));
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
    private void sendFailMails(String transactionStatus, String transactionMessage,CommResponseVO transRespDetails,String trackingId, Hashtable transactionAttributes,Hashtable attributeHash,String transactionDescription) throws SystemError
    {
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        String ids = TimeZone.getTimeZone("GMT+5:30").getID();
        SimpleTimeZone tz = new SimpleTimeZone(+0 * 00 * 60 * 1000, ids);
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar(tz);
        cal.setTime(new java.util.Date());


    }


    public PZCancelResponse cancelCapture(PZCancelRequest cancelCaptureRequest)
    {
        //manager initialization
        GatewayManager gatewayManager = new GatewayManager();
        //DAO initialization
        TransactionDAO transactionDAO = new TransactionDAO();
        //VO initialization
        PZCancelResponse cancelResponse = new PZCancelResponse();
        TransactionVO transactionVO = new TransactionVO();
        TerminalVO terminalVO = new TerminalVO();
        AuditTrailVO auditTrailVO=null;
        //common variable
        boolean isValid= false;
        try
        {
            transactionVO.setTrackingId(String.valueOf(cancelCaptureRequest.getTrackingId()));
            transactionVO.setStatus(PZTransactionStatus.CAPTURE_SUCCESS.toString());
            transactionVO.setUpdateStatusTo(PZTransactionStatus.CANCEL_STARTED.toString());

            terminalVO.setAccountId(String.valueOf(cancelCaptureRequest.getAccountId()));
            terminalVO.setMemberId(String.valueOf(cancelCaptureRequest.getMemberId()));
            //getting PaymentGateway from accountId
            AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(String.valueOf(cancelCaptureRequest.getAccountId()));
            //getting gatewayAccountVO using accountId
            GatewayAccountVO gatewayAccountVO =gatewayManager.getGatewayAccountForAccountId(terminalVO.getAccountId());
            //defining CommResponseVO
            CommResponseVO commResponseVO = null;
            //getting transaction from common table using accountId,memberId,trackingId,status
            isValid=transactionDAO.setSingleTransactionAccordingToTheStatus(terminalVO,transactionVO);
            if (isValid)
            {
                //updating common table using
                isValid=transactionDAO.updateTransactionStatusfromCommon(transactionVO);
                if (isValid)
                {
                    CommMerchantVO merchantAccountVO = new CommMerchantVO();
                    CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
                    CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
                    //getting instance of the Common request VO according to the accountId
                    CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(cancelCaptureRequest.getAccountId());
                    //setting necessity info to CommMerchantDetails VO
                    merchantAccountVO.setMerchantId(gatewayAccountVO.getGatewayAccount().getMerchantId());
                    merchantAccountVO.setPassword(gatewayAccountVO.getGatewayAccount().getFRAUD_FTP_PASSWORD());
                    merchantAccountVO.setMerchantUsername(gatewayAccountVO.getGatewayAccount().getFRAUD_FTP_USERNAME());
                    merchantAccountVO.setDisplayName(gatewayAccountVO.getGatewayAccount().getDisplayName());
                    merchantAccountVO.setAliasName(gatewayAccountVO.getGatewayAccount().getAliasName());
                    //setting necessity info to CommTransactionDetails VO
                    transDetailsVO.setAmount(transactionVO.getAmount());
                    transDetailsVO.setCurrency(gatewayAccountVO.getGatewayAccount().getCurrency());
                    transDetailsVO.setOrderId(transactionVO.getTrackingId());
                    transDetailsVO.setOrderDesc(cancelCaptureRequest.getCancelReason());
                    transDetailsVO.setDetailId(transactionVO.getDetailId());
                    transDetailsVO.setPreviousTransactionId(transactionVO.getResponseTransactionId());
                    //setting necessity info to CommAddressDetails VO
                    addressDetailsVO.setIp(cancelCaptureRequest.getIpAddress());
                    //setting common Request vo parameters
                    commRequestVO.setCommMerchantVO(merchantAccountVO);
                    commRequestVO.setTransDetailsVO(transDetailsVO);
                    commRequestVO.setAddressDetailsVO(addressDetailsVO);
                    //todo want to ask to sir for params extension if needed
                    //setCancelVOParamsExtension(commRequestVO, cancelCaptureRequest);  //adding new params if allowed
                    auditTrailVO=new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(merchantAccountVO.getMerchantId());
                    auditTrailVO.setActionExecutorName("Customer");
                    //adding new row to transaction_common_details table
                    int actionEntry = actionEntry(transactionVO.getTrackingId(),transactionVO.getAmount(), ActionEntry.ACTION_CANCEL_STARTED, ActionEntry.STATUS_CANCEL_STARTED, null, commRequestVO,auditTrailVO,null);

                    log.debug("callng processCapture");
                    transactionLogger.debug("callng processCapture");
                    //calling payment gateway
                    commResponseVO = (CommResponseVO) pg.processCaptureVoid(transactionVO.getTrackingId(),commRequestVO);
                    log.debug("called processCaptureVoid" + commResponseVO.getErrorCode() + commResponseVO.getDescription() + commResponseVO.getTransactionId());
                    transactionLogger.debug("called processCaptureVoid" + commResponseVO.getErrorCode() + commResponseVO.getDescription() + commResponseVO.getTransactionId());



                    if (commResponseVO != null && PZResponseStatus.SUCCESS.name().toLowerCase().equals(commResponseVO.getStatus()))//commResponseVO.getStatus()).equalsIgnoreCase("success")
                    {
                        //search criteria changed for updating
                        transactionVO.setStatus(transactionVO.getUpdateStatusTo());
                        transactionVO.setUpdateStatusTo(PZTransactionStatus.AUTH_CANCELLED.toString());
                        //updating the status of the
                        isValid = transactionDAO.updateTransactionStatusfromCommon(transactionVO);

                        auditTrailVO.setActionExecutorId(merchantAccountVO.getMerchantId());
                        auditTrailVO.setActionExecutorName("Customer");
                        if (isValid)
                        {
                            //adding new row in transaction_common_details table
                            actionEntry(transactionVO.getTrackingId(),transactionVO.getAmount(), ActionEntry.ACTION_CANCEL_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_CANCLLED, commResponseVO, commRequestVO,auditTrailVO);

                            cancelResponse.setStatus(PZResponseStatus.SUCCESS);
                            cancelResponse.setResponseDesceiption("Transaction has been successfully cancelled");
                        }
                    }
                    else
                    {
                        cancelResponse.setStatus(PZResponseStatus.FAILED);
                        cancelResponse.setResponseDesceiption("Transaction cannot be Canceled.");
                    }
                }
                else
                {
                    cancelResponse.setStatus(PZResponseStatus.ERROR);
                    cancelResponse.setResponseDesceiption("Error initiating cancellation for transaction");
                }
            }
            else
            {
                cancelResponse.setStatus(PZResponseStatus.FAILED);
                cancelResponse.setResponseDesceiption("Transaction not found");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException while cancelling capture transaction via Common Process",e);
            transactionLogger.error("PZTechnicalViolationException while cancelling capture transaction via Common Process",e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"cancelCapture");

            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + e.getMessage());
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while cancelling capture transaction via Common Process",e);
            transactionLogger.error("PZDBViolationException while cancelling capture transaction via Common Process",e);

            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while cancelling capture transaction via Common Process",systemError);
            transactionLogger.error("SystemError while cancelling capture transaction via Common Process",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"cancelCapture",null,"common","Exception while cancelling transaction",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"CancelCapture");
            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error during cancellation of transaction" + systemError.getMessage());
        }
        return cancelResponse;
    }

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D){
        return "";
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){
        return "";
    }

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        return "";
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        log.debug("inside common process set3DResponseVO");

    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        log.debug("inside common process set3DResponseVO");

    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO,CommonValidatorVO commonValidatorVO)
    {
        log.debug("inside common process");
        return null;
    }

    @Override
    public List<PZTC40Record> readTC40file(PZFileVO fileName)throws SystemError
    {
        throw new SystemError("Functionality is not allowed for selected Account");
    }

    public Hashtable process3DConfirmation(String trackingId, String paRes){
        return null;
    }

    public void getIntegrationSpecificTransactionDetails(TransactionDetailsVO transactionDetailsVO,String trackingId,String toid){}

    public CommInquiryResponseVO getCommonInquiryResponseVO(TransactionDetailsVO transactionDetailsVO)
    {
        CommInquiryResponseVO  commInquiryResponseVO=new CommInquiryResponseVO();
        commInquiryResponseVO.setOrderId(transactionDetailsVO.getDescription());
        commInquiryResponseVO.setTrackingId(transactionDetailsVO.getTrackingid());
        commInquiryResponseVO.setStatus(transactionDetailsVO.getStatus());
        commInquiryResponseVO.setAuthAmount(transactionDetailsVO.getAmount());
        commInquiryResponseVO.setCaptureAmount(transactionDetailsVO.getCaptureAmount());
        commInquiryResponseVO.setRefundAmount(transactionDetailsVO.getRefundAmount());
        setAddtionalResponseParameters(commInquiryResponseVO,transactionDetailsVO);
        return commInquiryResponseVO;
    }
    public void setAddtionalResponseParameters(CommInquiryResponseVO commInquiryResponseVO,TransactionDetailsVO transactionDetailsVO)
    {

    }


    public DirectKitResponseVO setHostedPaymentResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("------Inside CommonPaymentProcess---setHostedPaymentResponseVO--");
        try
        {
            Functions functions                 = new Functions();
            String checksum                     = functions.generateMD5ChecksumVV(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getRedirectUrl(), commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getMerchantDetailsVO().getChecksumAlgo());
            AsyncParameterVO asyncParameterVO   = null;
            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

            GenericCardDetailsVO genericCardDetailsVO=commonValidatorVO.getCardDetailsVO();

            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("toid");
                asyncParameterVO.setValue(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(checksum)){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("checksum");
                asyncParameterVO.setValue(checksum);
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getTotype())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("totype");
                asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getTotype());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("description");
                asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getOrderId());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("amount");
                asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getAmount());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_AMOUNT");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderDesc())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("orderdescription");
                asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getOrderDesc());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRedirectUrl())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("redirecturl");
                asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCountry())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_COUNTRY");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getCountry());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCity())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_city");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getCity());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getState())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_state");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getState());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getZipCode())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_zip");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getZipCode());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getStreet())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_street");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getStreet());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTelnocc())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_telnocc");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getTelnocc());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_telno");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getPhone());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_emailaddr");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getEmail());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("ipaddr");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("currency");
                asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getCurrency());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("TMPL_CURRENCY");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("firstName");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getFirstname());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("lastName");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getLastname());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("dateOfBirth");
                asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getBirthdate());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }

            if(merchantDetailsVO.getIsIgnorePaymode().equalsIgnoreCase("Y")){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("terminalid");
                asyncParameterVO.setValue("");
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("paymentMode");
                asyncParameterVO.setValue("");
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("paymentBrand");
                asyncParameterVO.setValue("");
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }else{
                if(functions.isValueNull(commonValidatorVO.getTerminalId()))
                {
                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("terminalid");
                    asyncParameterVO.setValue(commonValidatorVO.getTerminalId());
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                }
                if(functions.isValueNull(commonValidatorVO.getPaymentMode())){
                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("paymentMode");
                    asyncParameterVO.setValue(commonValidatorVO.getPaymentMode());
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                }
                if(functions.isValueNull(commonValidatorVO.getPaymentBrand())){
                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("paymentBrand");
                    asyncParameterVO.setValue(commonValidatorVO.getPaymentBrand());
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                }
            }

            if(functions.isValueNull(commonValidatorVO.getCustomerId())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("customerId");
                asyncParameterVO.setValue(commonValidatorVO.getCustomerId());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl())){
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("notificationUrl");
                asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            if(commonValidatorVO.getMarketPlaceVOList()!=null){
                for(MarketPlaceVO marketPlaceVO:commonValidatorVO.getMarketPlaceVOList())
                {
                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("MP_Memberid[]");
                    asyncParameterVO.setValue(marketPlaceVO.getMemberid());
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("MP_Amount[]");
                    asyncParameterVO.setValue(marketPlaceVO.getAmount());
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("MP_Orderid[]");
                    asyncParameterVO.setValue(marketPlaceVO.getOrderid());
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("MP_Order_Description[]");
                    asyncParameterVO.setValue(marketPlaceVO.getOrderDesc());
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                    // card details

                }
            }
            if(genericCardDetailsVO!=null)
            {
                transactionLogger.error("------Inside if genericCardDetailsVO card condition---setHostedPaymentResponseVO--");

                if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
                {
                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("cardNumber");
                    asyncParameterVO.setValue(functions.encryptString(genericCardDetailsVO.getCardNum()));
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                }

                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName("expire");
                if (functions.isValueNull(genericCardDetailsVO.getExpMonth()) && functions.isValueNull(genericCardDetailsVO.getExpYear()))
                {
                    asyncParameterVO.setValue(functions.encryptString(genericCardDetailsVO.getExpMonth() + "-" + genericCardDetailsVO.getExpYear()));
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                }
                else
                {
                    asyncParameterVO.setValue(" ");
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                }

                if (functions.isValueNull(genericCardDetailsVO.getcVV()))
                {
                    asyncParameterVO = new AsyncParameterVO();
                    asyncParameterVO.setName("cvv");
                    asyncParameterVO.setValue(functions.encryptString(genericCardDetailsVO.getcVV()));
                    directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
                }

            }
            if(functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getHostUrl()))
            {
                directKitResponseVO.setBankRedirectionUrl("https://"+commonValidatorVO.getPartnerDetailsVO().getHostUrl() + "/transaction/Checkout");
            }else{
                directKitResponseVO.setBankRedirectionUrl("http://localhost:8081/transaction/Checkout");
            }


        }catch (NoSuchAlgorithmException e )
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
        }
        return directKitResponseVO;
    }

    public PZPayoutResponse payout(PZPayoutRequest payoutRequest)
    {
        PZPayoutResponse payoutResponse = new PZPayoutResponse();

        AuditTrailVO auditTrailVO   =null;
        AbstractPaymentGateway pg   = null;

        String  orderId             = "";
        String  orderDescription    = "";
        String  accountId           = "";
        String  payoutAmount        = "";
        String  toId                = "";
        String  terminalId          = "";
        String  ipAddress           = "";
        String  header              = "";
        String  customerEmail       = "";
        String  customerAccount     = "";
        Integer previousTrackingId  = 0;
        String expDateOffset        = "";
        String payoutCurrency       = "";
        String tmpl_amount          = "0.00";
        String tmpl_currency        = "";
        String customerId           = "";
        String customerBankId       = "";
        Integer trackingId          = 0;
        String payoutType           = "";
        String notificationUrl      = "";
        String walletId             = "";
        String walletAmount         = "";
        String walletCurrency       = "";

        String customerBankCode             = "";
        String customerBankAccountNumber    = "";
        String customerBankAccountName      = "";
        int actionEntry                     = 0;

        if(functions.isValueNull(payoutRequest.getOrderId())){
            orderId = payoutRequest.getOrderId();
        }
        if(functions.isValueNull(payoutRequest.getOrderDescription())){
            orderDescription    = payoutRequest.getOrderDescription();
        }
        if(functions.isValueNull(String.valueOf(payoutRequest.getAccountId()))){
            accountId   = String.valueOf(payoutRequest.getAccountId());
        }
        if(functions.isValueNull(payoutRequest.getPayoutAmount())){
            payoutAmount    = payoutRequest.getPayoutAmount();
        }
        if(functions.isValueNull(String.valueOf(payoutRequest.getMemberId()))){
            toId    = String.valueOf(payoutRequest.getMemberId());
        }
        if(functions.isValueNull(payoutRequest.getIpAddress())){
            ipAddress   = payoutRequest.getIpAddress();
        }
        if(functions.isValueNull(payoutRequest.getTerminalId())){
            terminalId  = payoutRequest.getTerminalId();
        }
        if(functions.isValueNull(payoutRequest.getHeader())){
            header  = payoutRequest.getHeader();
        }
        if(functions.isValueNull(payoutRequest.getCustomerEmail())){
            customerEmail   = payoutRequest.getCustomerEmail();
        }
        if(payoutRequest.getAuditTrailVO()!=null){
            auditTrailVO    = payoutRequest.getAuditTrailVO();
        }
        if(functions.isValueNull(payoutRequest.getCustomerAccount())){
            customerAccount = payoutRequest.getCustomerAccount();
        }
        if(payoutRequest.getTrackingId()!=null){
            previousTrackingId  = payoutRequest.getTrackingId();
        }

        if (functions.isValueNull(payoutRequest.getExpDateOffset()))
            expDateOffset = payoutRequest.getExpDateOffset();

        if (functions.isValueNull(payoutRequest.getPayoutCurrency()))
            payoutCurrency = payoutRequest.getPayoutCurrency();

        if (functions.isValueNull(payoutRequest.getTmpl_currency()))
            tmpl_currency = payoutRequest.getTmpl_currency();

        if (functions.isValueNull(payoutRequest.getTmpl_amount()))
            tmpl_amount = payoutRequest.getTmpl_amount();

        if (functions.isValueNull(payoutRequest.getCustomerBankId()))
            customerBankId = payoutRequest.getCustomerBankId();

        if (functions.isValueNull(payoutRequest.getCustomerId()))
            customerId = payoutRequest.getCustomerId();

        if (functions.isValueNull(payoutRequest.getPayoutType()))
            payoutType = payoutRequest.getPayoutType();

        if(functions.isValueNull(payoutRequest.getNotificationUrl()))
            notificationUrl = payoutRequest.getNotificationUrl();

        if(functions.isValueNull(payoutRequest.getWalletId()))
            walletId    = payoutRequest.getWalletId();

        if(functions.isValueNull(payoutRequest.getWalletAmount()))
            walletAmount    = payoutRequest.getWalletAmount();

        if(functions.isValueNull(payoutRequest.getWalletCurrency()))
            walletCurrency  = payoutRequest.getWalletCurrency();

        if(functions.isValueNull(payoutRequest.getCustomerBankAccountNumber())){
            customerBankAccountNumber   = payoutRequest.getCustomerBankAccountNumber();
        }
        if(functions.isValueNull(payoutRequest.getCustomerBankAccountName())){
            customerBankAccountName = payoutRequest.getCustomerBankAccountName();
        }
        if(functions.isValueNull(payoutRequest.getCustomerBankCode())){
            customerBankCode    = payoutRequest.getCustomerBankCode();
        }
        //System.out.println("custAccount in common---"+customerAccount);

        MerchantDetailsVO merchantDetailsVO=null;
        String status                   = "";
        String toType                   ="";
        String redirectUrl              ="";
        String cardType                 ="";
        String paymentType              ="";
        String mailTransactionStatus    ="";

        try{

            TransactionDetailsVO transactionDetailsVO       = transactionManager.getTransDetailFromCommon(String.valueOf(previousTrackingId));
            BinDetailsVO binDetails                         = transactionDAO.getBinDetails(String.valueOf(previousTrackingId));

            MerchantDAO merchantDAO                         = new MerchantDAO();
            merchantDetailsVO                               = merchantDAO.getMemberDetails(toId);
            if(merchantDetailsVO != null){
                toType  = merchantDetailsVO.getPartnerName();
            }

            TerminalManager terminalManager = new TerminalManager();
            TerminalVO terminalVO           = terminalManager.getTerminalByTerminalId(terminalId);

            if(terminalVO!=null){
                paymentType = terminalVO.getPaymodeId();
                cardType    = terminalVO.getCardTypeId();
            }

            //Gateway account details/Payer account details
            GatewayAccount account  = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
            String merchantId       = account.getMerchantId();
            String alias            = account.getAliasName();
            String username         = account.getFRAUD_FTP_USERNAME();
            String password         = account.getFRAUD_FTP_PASSWORD();

            String currency         = "";
            if (functions.isValueNull(payoutCurrency))
                currency    = payoutCurrency;
            else
                currency    = account.getCurrency();   //This line should be removed after making currency mandatory
            String fromId   = account.getMerchantId();
            String fromType = account.getGateway();

            String name = "";
            if( functions.isValueNull(transactionDetailsVO.getName())){
                name    = transactionDetailsVO.getName();
            }
            String ccnum    = "";
            if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                ccnum   = transactionDetailsVO.getCcnum();
            }
            String firstname    = "";
            if(functions.isValueNull(transactionDetailsVO.getFirstName())){
                firstname   = transactionDetailsVO.getFirstName();
            }
            String lastname = "";
            if(functions.isValueNull(transactionDetailsVO.getLastName())){
                lastname    = transactionDetailsVO.getLastName();
            }
            String expdate  = "";
            if(functions.isValueNull(transactionDetailsVO.getExpdate())){
                expdate=transactionDetailsVO.getExpdate();
            }
            String street   = "";
            if(functions.isValueNull(transactionDetailsVO.getStreet())){
                street  = transactionDetailsVO.getStreet();
            }
            String country  = "";
            if(functions.isValueNull(transactionDetailsVO.getCountry())){
                country = transactionDetailsVO.getCountry();
            }
            String city = "";
            if(functions.isValueNull(transactionDetailsVO.getCity())){
                city    = transactionDetailsVO.getCity();
            }
            String state    = "";
            if(functions.isValueNull(transactionDetailsVO.getState())){
                state   = transactionDetailsVO.getState();
            }
            String zip  = "";
            if(functions.isValueNull(transactionDetailsVO.getZip())){
                zip = transactionDetailsVO.getZip();
            }
            String telno    = "";
            if(functions.isValueNull(payoutRequest.getPhone())){
                telno = payoutRequest.getPhone();
            }
            else if(functions.isValueNull(transactionDetailsVO.getTelno())){
                    telno = transactionDetailsVO.getTelno();
            }
            String telnocc  = "";
            if(functions.isValueNull(payoutRequest.getPhoneCountryCode())){
                telnocc = payoutRequest.getPhoneCountryCode();
            }
            else if(functions.isValueNull(transactionDetailsVO.getTelcc()))
            {
                telnocc = transactionDetailsVO.getTelcc();
            }
            String email    = "";
            if(functions.isValueNull(payoutRequest.getCustomerEmail())){
                email = payoutRequest.getCustomerEmail();
            }else if(functions.isValueNull(transactionDetailsVO.getEmailaddr())){
                email   = transactionDetailsVO.getEmailaddr();
            }
            String paymentId    = "";
            if(functions.isValueNull(transactionDetailsVO.getPaymentId())){
                paymentId   = transactionDetailsVO.getPaymentId();
            }
            if (!functions.isValueNull(payoutRequest.getTmpl_currency()) && functions.isValueNull(transactionDetailsVO.getTemplatecurrency()))
            {
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
            }

            if (!functions.isValueNull(payoutRequest.getTmpl_amount()) && functions.isValueNull(transactionDetailsVO.getTemplateamount()))
            {
                tmpl_amount = transactionDetailsVO.getTemplateamount();
            }
            String cardTypeName = "";
            if (functions.isValueNull(transactionDetailsVO.getCardtype()))
            {
                cardTypeName = transactionDetailsVO.getCardtype();
            }
            else{
                cardTypeName    = GatewayAccountService.getCardType(cardType);
            }

            String authorization_code = "";
            if (functions.isValueNull(transactionDetailsVO.getAuthorization_code()))
            {
                authorization_code = transactionDetailsVO.getAuthorization_code();
            }

            String firstSix = "";
            if(functions.isValueNull(binDetails.getFirst_six())){
                firstSix    = binDetails.getFirst_six();
            }
            String lastFour = "";
            if(functions.isValueNull(binDetails.getLast_four())){
                lastFour    = binDetails.getLast_four();
            }
            String bin_Brand    = "";
            if(functions.isValueNull(binDetails.getBin_brand())){
                bin_Brand   = binDetails.getBin_brand();
            }
            String bin_Transaction_Type = "";
            if(functions.isValueNull(binDetails.getBin_transaction_type())){
                bin_Transaction_Type    = binDetails.getBin_transaction_type();
            }
            String bin_CardType = "";
            if(functions.isValueNull(binDetails.getBin_card_type())){
                bin_CardType    = binDetails.getBin_card_type();
            }
            String bin_Card_Category    = "";
            if(functions.isValueNull(binDetails.getBin_card_category())){
                bin_Card_Category   = binDetails.getBin_card_category();
            }
            String bin_Usage_Type   ="";
            if(functions.isValueNull(binDetails.getBin_usage_type())){
                bin_Usage_Type  = binDetails.getBin_usage_type();
            }

            transactionLogger.debug("customerid----"+customerId+"----accountNumber---"+customerAccount);

            CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
            if(payoutType.equalsIgnoreCase("approval"))
            {
                commCardDetailsVO.setAccountNumber(customerId);
            }
            else
            {
                commCardDetailsVO.setAccountNumber(customerAccount);
            }

            if(functions.isValueNull(expdate))
            {
                String eDate        = PzEncryptor.decryptExpiryDate(expdate);
                String expMonth     = eDate.split("\\/")[0];
                String expYear      = eDate.split("\\/")[1];
                commCardDetailsVO.setExpMonth(expMonth);
                commCardDetailsVO.setExpYear(expYear);
                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(ccnum));

            }
            CommRequestVO commRequestVO         = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accountId));
            commRequestVO.setCardDetailsVO(commCardDetailsVO);
            CommMerchantVO merchantAccountVO    = new CommMerchantVO();
            merchantAccountVO.setMerchantId(merchantId);
            merchantAccountVO.setPassword(password);
            merchantAccountVO.setMerchantUsername(username);
            merchantAccountVO.setAliasName(alias);
            merchantAccountVO.setAccountId(String.valueOf(accountId));
            if(merchantDetailsVO.getCompany_name() != null){
                merchantAccountVO.setMerchantOrganizationName(merchantDetailsVO.getCompany_name());
            }


            //Preparing transaction details vo for gateway call.
            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
            transDetailsVO.setAmount(payoutAmount);
            transDetailsVO.setCurrency(currency);
            transDetailsVO.setOrderId(String.valueOf(trackingId));
            transDetailsVO.setOrderDesc(orderId);
            transDetailsVO.setPreviousTransactionId(previousTrackingId.toString());
            transDetailsVO.setCustomerId(customerId);
            transDetailsVO.setCustomerBankId(customerBankId);
            transDetailsVO.setPaymentId(paymentId);
            transDetailsVO.setPayoutType(payoutType);
            transDetailsVO.setWalletId(walletId);
            transDetailsVO.setWalletAmount(walletAmount);
            transDetailsVO.setWalletCurrency(walletCurrency);
            transDetailsVO.setAuthorization_code(authorization_code);
            transDetailsVO.setCardType(cardType);

            transDetailsVO.setCustomerBankAccountNumber(customerBankAccountNumber);
            transDetailsVO.setCustomerBankAccountName(customerBankAccountName);
            transDetailsVO.setCustomerBankCode(customerBankCode);
            transDetailsVO.setTotype(transactionDetailsVO.getTotype());

            transDetailsVO.setToId(toId);
            transactionLogger.debug("customer id/bankid" + customerBankId + "---" + customerId);
            transactionLogger.debug("customerBankAccountNumber----->" + customerBankAccountNumber);
            transactionLogger.debug("customerBankAccountName----->" + customerBankAccountName);
            transactionLogger.debug("customerBankCode----->" + customerBankCode);

            CommAddressDetailsVO commAddressDetailsVO1  = new CommAddressDetailsVO();
            commAddressDetailsVO1.setEmail(customerEmail);
            if (functions.isValueNull(expDateOffset))
                commAddressDetailsVO1.setBirthdate(expDateOffset);
            else
                commAddressDetailsVO1.setBirthdate(merchantDetailsVO.getExpDateOffset());
                commAddressDetailsVO1.setCountry(transactionDetailsVO.getCountry());

            if("0.00".equals(tmpl_amount))
                commAddressDetailsVO1.setTmpl_amount(payoutAmount);
            else
                commAddressDetailsVO1.setTmpl_amount(tmpl_amount);

            if (functions.isValueNull(tmpl_currency))
                commAddressDetailsVO1.setTmpl_currency(tmpl_currency);
            else
                commAddressDetailsVO1.setTmpl_currency(currency);

            if (functions.isValueNull(firstname))
            {
                commAddressDetailsVO1.setFirstname(firstname);
            }
            if (functions.isValueNull(lastname))
            {
                commAddressDetailsVO1.setLastname(lastname);
            }
            commAddressDetailsVO1.setPhone(telno);
            commRequestVO.setCommMerchantVO(merchantAccountVO);
            commRequestVO.setTransDetailsVO(transDetailsVO);
            commRequestVO.setAddressDetailsVO(commAddressDetailsVO1);

            setPayoutVOParamsextension(commRequestVO, payoutRequest);

            transactionLogger.error("PayoutType-----" + payoutType);

            if(functions.isValueNull(payoutType) && "cancel".equalsIgnoreCase(payoutType))
            {
                trackingId  = previousTrackingId;
                transactionLogger.debug("trackingId in cancel:::::::"+trackingId);
                //System.out.println("cancel payout called 1 started---");
                actionEntry = actionEntry(String.valueOf(trackingId), payoutAmount.toString(), ActionEntry.ACTION_PAYOUT_CANCEL_STARTED, ActionEntry.STATUS_PAYOUT_CANCEL_STARTED, null, commRequestVO,auditTrailVO,"");
            }
            else//for approval(by default)
            {
                trackingId  = paymentManager.insertTransactionCommonForPayout(toId, toType, fromId, fromType, orderId, orderDescription, payoutAmount, redirectUrl, accountId, Integer.parseInt(paymentType), Integer.parseInt(cardType), currency, header,ipAddress,ActionEntry.STATUS_PAYOUT_STARTED, terminalId,name,ccnum, firstname, lastname, street, country, city, state, zip, telno, telnocc, expdate,email, tmpl_amount, tmpl_currency,customerId,notificationUrl,walletId,cardTypeName);
                transactionLogger.debug("trackingId in approval:::::::"+trackingId);
                if (FlexepinPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                {
                    paymentManager.insertTransactionEntryForFlexepinVoucher(String.valueOf(trackingId),payoutAmount,currency,ActionEntry.STATUS_PAYOUT_STARTED);
                }
                CommResponseVO commResponseVO = null;
                /*commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);*/
                actionEntry = actionEntry(String.valueOf(trackingId), payoutAmount.toString(), ActionEntry.ACTION_PAYOUT_STARTED, ActionEntry.STATUS_PAYOUT_STARTED, commResponseVO, commRequestVO,auditTrailVO,"");
                paymentManager.addBinDetailsEntry(String.valueOf(trackingId),accountId,firstSix,lastFour,bin_Brand,bin_Transaction_Type,bin_CardType,bin_Card_Category,bin_Usage_Type);
            }
            //pa.updateBinDetails(commonValidatorVO)

            //Preparing merchant account vo for gateway call

            transactionLogger.debug("CommonPaymentProcess email---"+email);
            transactionLogger.debug("CommonPaymentProcess customerEmail---"+customerEmail);
            //making payout request
            log.debug("calling processPayout");
            transactionLogger.debug("Calling processPayout");
            pg                              = AbstractPaymentGateway.getGateway(accountId);
            CommResponseVO transRespDetails = (CommResponseVO) pg.processPayout(String.valueOf(trackingId),commRequestVO);
            log.error("transRespDetails.status:::::"+transRespDetails.getStatus());
            payoutResponse.setTrackingId(String.valueOf(trackingId));
            payoutResponse.setPayoutAmount(payoutAmount);

            payoutResponse.setTmpl_amount(tmpl_amount);
            payoutResponse.setTmpl_currency(tmpl_currency);
            //  boolean archive = false;

            transRespDetails.setTmpl_Amount(tmpl_amount);
            transRespDetails.setTmpl_Currency(tmpl_currency);

            if (functions.isValueNull(transRespDetails.getStatus()))
            {
                if ((transRespDetails.getStatus()).equalsIgnoreCase("success"))
                {
                    if (VoucherMoneyPaymentGateway.GATEWAY_TYPE.equals(fromType))
                    {
                        payoutAmount = transRespDetails.getAmount();
                    }

                    if (FlexepinPaymentGateway.GATEWAY_TYPE.equals(fromType) && functions.isValueNull(String.valueOf(Double.valueOf(transRespDetails.getAmount()))))
                    {
                        payoutAmount = String.valueOf(Double.valueOf(transRespDetails.getAmount()));
                    }
                    payoutResponse.setPayoutAmount(payoutAmount);
                    payoutResponse.setStatus(PZResponseStatus.SUCCESS);
                    payoutResponse.setResponseDesceiption(transRespDetails.getDescription());
                    if (functions.isValueNull(transRespDetails.getRemark()))
                        payoutResponse.setRemark(transRespDetails.getRemark());
                    else
                        payoutResponse.setRemark(transRespDetails.getDescription());

                    if(functions.isValueNull(payoutType) && "cancel".equalsIgnoreCase(payoutType))
                    {
                        actionEntry = actionEntry(String.valueOf(trackingId), payoutAmount.toString(), ActionEntry.ACTION_PAYOUT_CANCEL_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_CANCEL_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO, transRespDetails.getRemark());
                        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_CANCEL_SUCCESSFUL, payoutAmount, ActionEntry.ACTION_PAYOUT_CANCEL_SUCCESSFUL, transRespDetails.getTransactionId(),null);
                    }
                    else
                    {
                        actionEntry = actionEntry(String.valueOf(trackingId), payoutAmount.toString(), ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO, transRespDetails.getRemark());
                        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_SUCCESSFUL, payoutAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, transRespDetails.getTransactionId(),null);
                    }

                    transactionLogger.debug("inside commonPaymentProcess payout success---->"+accountId);
                    LimitChecker limitChecker=new LimitChecker();
                    limitChecker.updatePayoutAmountOnAccountid(accountId, payoutAmount);

                }
                else if((transRespDetails.getStatus()).equalsIgnoreCase("failed"))
                {
                    payoutResponse.setStatus(PZResponseStatus.FAILED);
                    payoutResponse.setResponseDesceiption(transRespDetails.getDescription());
                    if (functions.isValueNull(transRespDetails.getRemark()))
                        payoutResponse.setRemark(transRespDetails.getRemark());
                    else
                        payoutResponse.setRemark(transRespDetails.getDescription());
                    if(functions.isValueNull(payoutType) && "cancel".equalsIgnoreCase(payoutType))
                    {
                        transactionLogger.debug("inside cancel----"+payoutType+"-----");
                        actionEntry = actionEntry(String.valueOf(trackingId), payoutAmount.toString(), ActionEntry.ACTION_PAYOUT_CANCEL_FAILED, ActionEntry.STATUS_PAYOUT_CANCEL_FAILED, transRespDetails, commRequestVO, auditTrailVO, transRespDetails.getRemark());
                        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_CANCEL_FAILED, payoutAmount, transRespDetails.getDescription(), transRespDetails.getTransactionId(),null);
                    }
                    else//approve by default
                    {
                        actionEntry = actionEntry(String.valueOf(trackingId), payoutAmount.toString(), ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, transRespDetails, commRequestVO, auditTrailVO, transRespDetails.getRemark());
                        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_FAILED, payoutAmount, transRespDetails.getDescription(), transRespDetails.getTransactionId(),null);
                    }

                }
                else
                {
                    payoutResponse.setPayoutAmount(payoutAmount);
                    payoutResponse.setStatus(PZResponseStatus.PENDING);
                    payoutResponse.setResponseDesceiption(transRespDetails.getDescription());
                    if (functions.isValueNull(transRespDetails.getRemark()))
                        payoutResponse.setRemark(transRespDetails.getRemark());
                    else
                        payoutResponse.setRemark(transRespDetails.getDescription());
                }

            }
            else
            {  payoutResponse.setPayoutAmount(payoutAmount);
                payoutResponse.setStatus(PZResponseStatus.PENDING);
                payoutResponse.setResponseDesceiption(transRespDetails.getDescription());
                if (functions.isValueNull(transRespDetails.getRemark()))
                    payoutResponse.setRemark(transRespDetails.getRemark());
                else
                    payoutResponse.setRemark(transRespDetails.getDescription());

                /*payoutResponse.setStatus(PZResponseStatus.PENDING);
                payoutResponse.setResponseDesceiption(transRespDetails.getDescription());
                if (functions.isValueNull(transRespDetails.getRemark()))
                    payoutResponse.setRemark(transRespDetails.getRemark());comm
                else
                    payoutResponse.setRemark(transRespDetails.getDescription());
                //actionEntry = actionEntry(String.valueOf(trackingId), payoutAmount.toString(), ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, transRespDetails, commRequestVO, auditTrailVO, transRespDetails.getRemark());
*/
            }
            setPayoutTransactionResponseVOParamsExtension(transRespDetails,payoutResponse);
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PAYOUT_TRANSACTION, String.valueOf(trackingId), payoutResponse.getStatus().toString(),payoutResponse.getRemark(), null);

            if(EasyPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || OmnipayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || TigerPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || UBAMCPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || FlexepinPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
            {
                if(payoutResponse.getStatus().toString().equalsIgnoreCase(PZResponseStatus.SUCCESS.toString()))
                {
                    payoutResponse.setStatus(PZResponseStatus.PAYOUTSUCCESSFUL);
                }
                else if(payoutResponse.getStatus().toString().equalsIgnoreCase(PZResponseStatus.FAILED.toString()))
                {
                    payoutResponse.setStatus(PZResponseStatus.PAYOUTFAILED);
                }
                else if(payoutResponse.getStatus().toString().equalsIgnoreCase(PZResponseStatus.PENDING.toString()))
                {
                    payoutResponse.setStatus(PZResponseStatus.PENDING);
                }
            }

            if ((functions.isValueNull(notificationUrl) && merchantDetailsVO.getPayoutNotification().equals("Y")) && !transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                transactionLogger.error("inside sending notification from ProcessPayoutUpload---" + notificationUrl);
                TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(String.valueOf(trackingId));
                if (functions.isValueNull(transactionDetailsVO1.getCcnum()))
                {
                    String accno = PzEncryptor.decryptPAN(transactionDetailsVO1.getCcnum());
                    transactionDetailsVO1.setCcnum(accno);
                }

                if (functions.isValueNull(transactionDetailsVO1.getExpdate()))
                {
                    String expdateDecrypt = PzEncryptor.decryptExpiryDate(transactionDetailsVO1.getExpdate());
                    transactionDetailsVO1.setExpdate(functions.maskingExpiry(expdateDecrypt));
                }
                if (functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
                {
                    transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                }else{
                    transactionDetailsVO1.setMerchantNotificationUrl("");
                }

                if (FlexepinPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) && functions.isValueNull(String.valueOf(Double.valueOf(payoutAmount))))
                {
                    transactionDetailsVO1.setAmount(String.valueOf(Double.valueOf(payoutAmount)));
                }

                transactionDetailsVO1.setSecretKey(merchantDetailsVO.getKey());
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, String.valueOf(trackingId),payoutResponse.getStatus().toString(), transRespDetails.getRemark());
            }
            //AsynchronousSmsService smsService=new AsynchronousSmsService();
            //smsService.sendSMS(MailEventEnum.PAYOUT_TRANSACTION,String.valueOf(trackingId), payoutResponse.getStatus().toString(),payoutResponse.getRemark(),null);

        }
        catch (SystemError systemError){
            log.error("SystemError while payout transaction",systemError);
            transactionLogger.error("SystemError while payout transaction",systemError);
            PZExceptionHandler.raiseAndHandleConstraintViolationException(CommonPaymentProcess.class.getName(), "payout()", null, "common", "SystemError:::", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, systemError.getMessage(), systemError.getCause(), toId, "Payout");
            payoutResponse.setStatus(PZResponseStatus.PENDING);
            payoutResponse.setResponseDesceiption("Internal error occurred while payout transaction");
        }
        catch (PZTechnicalViolationException tve){
            log.error("PZTechnicalViolationException", tve);
            transactionLogger.error("PZTechnicalViolationException", tve);
            payoutResponse.setStatus(PZResponseStatus.PENDING);
            payoutResponse.setResponseDesceiption("Error during payout of transaction" + tve.getMessage());
            PZExceptionHandler.handleTechicalCVEException(tve, toId, "payout");
        }
        catch (PZConstraintViolationException cve){
            log.error("PZConstraintViolationException", cve);
            transactionLogger.error("PZConstraintViolationException", cve);
            payoutResponse.setStatus(PZResponseStatus.PENDING);
            //refundResponse.setResponseDesceiption(functions.isValueNull(cve.getPzConstraint().getMessage())?cve.getPzConstraint().getMessage():"Error during reversal of transaction: " + cve.getMessage());
            log.debug("PZConstraintViolationException message---" + cve.getPzConstraint().getMessage());
            payoutResponse.setResponseDesceiption("Error during payout of transaction: " + cve.getPzConstraint().getMessage());
            PZExceptionHandler.handleCVEException(cve, toId, "payout");
        }
        catch (PZGenericConstraintViolationException cve){
            log.error("PZConstraintViolationException", cve);
            transactionLogger.error("PZConstraintViolationException", cve);
            payoutResponse.setStatus(PZResponseStatus.PENDING);
            payoutResponse.setTrackingId(String.valueOf(trackingId));
            payoutResponse.setPayoutAmount(payoutAmount);
            payoutResponse.setResponseDesceiption("Payout Functionality is not supported ");
        }
        catch (Exception e){
            log.error("Exception", e);
            transactionLogger.error("Exception", e);
            payoutResponse.setStatus(PZResponseStatus.PENDING);
            payoutResponse.setTrackingId(String.valueOf(trackingId));
            payoutResponse.setPayoutAmount(payoutAmount);
            payoutResponse.setResponseDesceiption("Payout Functionality is not supported ");
        }
        return payoutResponse;
    }



    public CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId)throws PZDBViolationException
    {
        return null;
    }

    @Override
    public CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO)
    {
        return commonValidatorVO;
    }

    @Override
    public Hashtable readBulkPayoutFile(PZFileVO fileName) throws SystemError
    {
        List<PZPayoutRequest> pzPayoutRequestsList  =  new ArrayList<PZPayoutRequest>();
        HashMap<String,String> errorHashMap         =  new HashMap<>();
        Hashtable hashtable                         = new Hashtable();
        InputStream inputStream                     = null;
        CommonBulkPayOut commonBulkPayOut           = null;
        String EOL                                  = "<BR>";

        try
        {
            //Create a new workbook instance.
            //HSSFWorkbook workbook           = new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            inputStream         = new BufferedInputStream(new FileInputStream(fileName.getFilepath()));
            POIFSFileSystem fs  = new POIFSFileSystem(inputStream);
            HSSFWorkbook workbook           = new HSSFWorkbook(fs);
            //Get first sheet from the workbook.
            HSSFSheet sheet                 = workbook.getSheetAt(0);
            if(inputStream == null)
            {
                throw new SystemError("File not found");
            }
            //Iterator through each row one by one.
            Iterator rows                       = sheet.rowIterator();
            int cntRowToLeaveFromTop            = 0;
            boolean isFileProceeding            = false;
            ArrayList<String> columnNameList    = new ArrayList<>();
            commonBulkPayOut                    = new CommonBulkPayOut();
            //Skip header rows.
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;

                short lastcolumnused = row.getLastCellNum();
                if(lastcolumnused > 6){
                    errorHashMap.put("extaColumn","Extra Column Added,Please Check Excel Sheet"+EOL);
                }
                if(cntRowToLeaveFromTop == 1)
                {
                    log.info("lastcolumnused--"+lastcolumnused);
                    for (int i = 0; i <= lastcolumnused; i++) {
                        log.info("----index -----"+i);
                        /*if(row.getCell((short) i).getStringCellValue() == null){
                            errorHashMap.put("emptyColumn","Invalid file content,Please Check Excel Sheet");
                        }
                        if(row.getCell((short) i).getStringCellValue() != null){
                            String colunmName = row.getCell((short) i).getStringCellValue();
                            columnNameList.add(colunmName.toLowerCase());
                        }*/
                        if(row.getCell((short) i) != null && row.getCell((short) i).getStringCellValue() != null){
                            String colunmName = row.getCell((short) i).getStringCellValue();
                            columnNameList.add(colunmName.toLowerCase());
                        }
                    }
                    StringBuffer colunmMissingName  = new StringBuffer();
                    for(String column : commonBulkPayOut.getBulkColumnNameList()){
                        if(!columnNameList.contains(column)){
                            colunmMissingName.append(column+",");
                        }
                    }
                    if(colunmMissingName.length() > 0){
                        errorHashMap.put("columnMiss","Missing  Column are: "+colunmMissingName.deleteCharAt(colunmMissingName.length()-1).toString().toUpperCase()+" Please Check Excel Sheet.  "+EOL);
                    }

                    if(!errorHashMap.isEmpty()){
                        hashtable.put("errorHashMap", errorHashMap);
                        return hashtable;
                    }

                    isFileProceeding = true;
                    break;
                }
            }
            log.info("columnNameList--->"+columnNameList);
            if(!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row                       = (HSSFRow) rows.next();
                PZPayoutRequest pzPayoutRequest   =  new PZPayoutRequest();

                try
                {
                    pzPayoutRequest.setBankAccountNo(row.getCell((short) 0).getStringCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    pzPayoutRequest.setBankAccountNo(row.getCell((short) 0).getNumericCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    errorHashMap.put("BankAccountNo","Invalid BankAccountNo Column Data "+EOL);
                    pzPayoutRequest.setBankAccountNo("");
                }

                try
                {
                    pzPayoutRequest.setCustomerBankAccountName(row.getCell((short) 1).getStringCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    pzPayoutRequest.setCustomerBankAccountName(row.getCell((short) 1).getNumericCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    errorHashMap.put("BankAccountName","Invalid BankAccountName Column Data "+EOL);
                    pzPayoutRequest.setCustomerBankAccountName("");
                }

                try
                {
                    pzPayoutRequest.setBankIfsc(row.getCell((short) 2).getStringCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    pzPayoutRequest.setBankIfsc(row.getCell((short) 2).getNumericCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    errorHashMap.put("BankIfsc","Invalid BankIfsc Column Data "+EOL);
                    pzPayoutRequest.setBankIfsc("");
                }

                try
                {
                    pzPayoutRequest.setPayoutAmount(row.getCell((short) 3).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    pzPayoutRequest.setPayoutAmount(row.getCell((short) 3).getStringCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    errorHashMap.put("PayoutAmount","Invalid Column or Payout Amount "+EOL);
                    pzPayoutRequest.setPayoutAmount("");
                }

                try
                {
                    pzPayoutRequest.setBankTransferType(row.getCell((short) 4).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    pzPayoutRequest.setBankTransferType(row.getCell((short) 4).getStringCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    errorHashMap.put("BankTransferType","Invalid BankTransfer Type Column Data "+EOL);
                    pzPayoutRequest.setBankTransferType("");
                }

                try
                {
                    pzPayoutRequest.setOrderId(row.getCell((short) 5).getNumericCellValue() + "");
                    DecimalFormat d = new DecimalFormat("0");
                    Double dObj2    = Double.valueOf(pzPayoutRequest.getOrderId());

                    pzPayoutRequest.setOrderId(d.format(dObj2));

                    if(pzPayoutRequest.getOrderId().equalsIgnoreCase("0.0") || pzPayoutRequest.getOrderId().equalsIgnoreCase("0")){
                        pzPayoutRequest.setOrderId("");
                    }
                }
                catch (NumberFormatException nfe)
                {
                    pzPayoutRequest.setOrderId(row.getCell((short) 5).getStringCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    pzPayoutRequest.setOrderId("");
                }


                /*try
                {
                    pzPayoutRequest.setTerminalId(row.getCell((short) 5).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    pzPayoutRequest.setTerminalId(row.getCell((short) 5).getStringCellValue() + "");

                }
                catch (NullPointerException ne)
                {
                    errorHashMap.put("TerminalId","Invalid TerminalId Column Data "+EOL);
                    //errorHashMap.put("column","Column is Missing,Please Check Excel Sheet "+EOL);
                    pzPayoutRequest.setTerminalId("0");
                }*/

                if(functions.isEmptyOrNull(pzPayoutRequest.getBankAccountNo())){
                    errorHashMap.put("BankAccountNo","Please Check Bank Account Number Column Data"+EOL);
                }
                /*if(functions.isEmptyOrNull(pzPayoutRequest.getTerminalId())){
                    errorHashMap.put("TerminalId","TerminalId not Provided "+EOL);
                }*/
                if(functions.isEmptyOrNull(pzPayoutRequest.getCustomerBankAccountName())){
                    errorHashMap.put("BankAccountName","Please Check Bank Name Column Data"+EOL);
                }
                if(functions.isEmptyOrNull(pzPayoutRequest.getBankIfsc())){
                    errorHashMap.put("BankIfsc","Please Check BankIfsc Column Data"+EOL);
                }
                if(functions.isEmptyOrNull(pzPayoutRequest.getPayoutAmount())){
                    errorHashMap.put("PayoutAmount","Please Check Payout Amount Column "+EOL);
                }
                if(functions.isEmptyOrNull(pzPayoutRequest.getBankTransferType())){
                    errorHashMap.put("BankTransferType","Please Check BankTransfer Type Column Data"+EOL);
                }


                pzPayoutRequestsList.add(pzPayoutRequest);
            }
            log.error("errorHashMap---->"+errorHashMap.values().toString());
            if(pzPayoutRequestsList.size()== 0){
                errorHashMap.put("NoDataFund","Please Provided At Least One Record For  Payout"+EOL);
                commonBulkPayOut.deleteErrorExcelFile(fileName.getFilepath());
            }


            log.error("final hastable :::::"+hashtable);

        }
        catch (Exception e)
        {
            errorHashMap.put("exception","Error: Invalid file content"+EOL);
            commonBulkPayOut.deleteErrorExcelFile(fileName.getFilepath());
            log.error("IOException:::::", e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }

        }
        hashtable.put("errorHashMap", errorHashMap);
        hashtable.put("pzPayoutRequestsList",pzPayoutRequestsList);

        return hashtable;
    }
}