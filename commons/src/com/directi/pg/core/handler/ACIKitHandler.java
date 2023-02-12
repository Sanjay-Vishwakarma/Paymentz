package com.directi.pg.core.handler;

import com.aciworldwide.commerce.gateway.plugins.NotEnoughDataException;
import com.aciworldwide.commerce.gateway.plugins.e24TranPipe;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Calendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.io.IOException;
import java.io.FileWriter;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Mar 1, 2007
 * Time: 3:25:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ACIKitHandler extends AbstractKitHandler
{
    private static Logger log = new Logger(ACIKitHandler.class.getName());
    private static String RESOURCE_PATH = ApplicationProperties.getProperty("RESOURCE_PATH");
    protected final static String AUTH = "4";
    protected final static String CAPTURE = "5";
    protected final static String REFUND = "2";
    protected final static String INQUIRY = "8";
    protected final static String VOID_AUTH = "9";
    protected final static String VOID_CAPTURE = "7";
    private static Hashtable<String, String> currency_code = new Hashtable<String, String>();

    protected static final String FILE_SEPARATOR = System.getProperty("file.separator");
    protected static String request_log_path ;
    protected static boolean log_request=false;

    static
    {

        try
        {
            ResourceBundle rb = ResourceBundle.getBundle("SBM" );
            //InputStream inputstream = e24TranPipe.class.getResourceAsStream( "SBM.properties" );
            //Properties properties = new Properties();
            //properties.load( inputstream );

            //request_log_path = properties.getProperty( "request_log_path" );
            request_log_path = rb.getString("request_log_path" );
            if ( request_log_path == null || "".equals( request_log_path ) )
                log.error( "Error in the properties file. Value for request_log_path is not mentioned or is invalid" );

            //String str = properties.getProperty( "log_request" );
            String str = rb.getString( "log_request" );
            if ( str != null && str.trim().equals( "true" ) )
                log_request = true;

            log.info("Got request_log_path "+request_log_path+ " log_request "+log_request);

        }
        catch ( Exception exception )
        {
             log.error("An exception occured while loading the Properties file. "+exception );
        }

    }
    static
    {

        Connection conn = null;
        String curencyCode = "";
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from currency_code ", conn);
            while (rs.next())
            {
                String currency = rs.getString("currency");
                String curr_code = rs.getString("currencycode");
                currency_code.put(currency, curr_code);
            }
        }
        catch (Exception e)
        {
            log.error(" exception " + Functions.getStackTrace(e));
            throw new RuntimeException(" exception " + Functions.getStackTrace(e));
        }
        finally
        {
            try
            {
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                log.error(" exception " + Functions.getStackTrace(e));
                throw new RuntimeException(" exception " + Functions.getStackTrace(e));
            }
        }
    }

    public Hashtable processAuthentication(String trackingID, String transAmount, Hashtable cardDetailHash, Hashtable billingAddrHash, Hashtable shippingAddrHash, Hashtable MPIDataHash, String ipaddress, String accountID) throws SystemError
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        e24TranPipe e24tp = new e24TranPipe();
        e24tp.setAlias(account.getAliasName());
        e24tp.setResourcePath(getResourcePath(account.getAliasName()));
        e24tp.setAction(AUTH);
        e24tp.setAmt(transAmount);
        e24tp.setTrackId(trackingID);
        e24tp.setCurrencyCode(getCurrencyCode(account.getCurrency()));

        if (MPIDataHash != null)
        {
            e24tp.setEci((String) MPIDataHash.get("eci"));
            e24tp.setXid((String) MPIDataHash.get("xid"));
            e24tp.setCavv((String) MPIDataHash.get("cavv"));
        }

        String ccNum;
        if (cardDetailHash != null)
        {
            ccNum = (String) cardDetailHash.get("ccnum");
            e24tp.setCard(ccNum);
            e24tp.setExpMonth((String) cardDetailHash.get("month"));
            e24tp.setExpYear((String) cardDetailHash.get("year"));
            e24tp.setMember((String) cardDetailHash.get("name"));
            e24tp.setCvv2((String) cardDetailHash.get("ccid"));

            String type = (String) cardDetailHash.get("type");
            if (Functions.parseData(type) == null)
                type = "CC";

            e24tp.setType(type);

        }
        else
            throw new SystemError("#1234#Card details are Missing");

        if (billingAddrHash != null)
        {
            String address = (String) billingAddrHash.get("address") + " " +
                    (String) billingAddrHash.get("city") + " " +
                    (String) billingAddrHash.get("state") + " " +
                    (String) billingAddrHash.get("ccode");

            e24tp.setAddress(address);
            e24tp.setZip((String) billingAddrHash.get("zip"));
        }

        String authId = null;
        String authCode = null;
        String authReceiptNo = null;
        String authQsiResponseCode = null;
        String authQsiResponseDesc = null;
        String authAVSResult = null;

        if (Functions.isValid(ccNum))
        {
            try
            {
                int result;

                try
                {
                    try
                    {
                        e24tp.setDebug(log_request);
                        result = e24tp.performTransaction();

                    }
                    catch (NotEnoughDataException e)
                    {
                        log.info("calling SendMAil for Authentication Error");
                        Mail.sendAdminMail("Error while authentication for trackingID-" + trackingID, e.getMessage());
                        log.info("called SendMAil for ");
                        throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
                    }

                    writeToFile(e24tp.getDebugMsg(),request_log_path);

                    // Print out results
                    if (result == e24TranPipe.FAILURE)
                    {

                        authId = "";
                        authCode = "";
                        authReceiptNo = "";
                        authQsiResponseCode = e24tp.getError();
                        authQsiResponseDesc = e24tp.getErrorText();

                    }
                    else if (result == e24TranPipe.SUCCESS)
                    {

                        if (trackingID.equals(e24tp.getTrackId()))  //check whether we got result for same trackingid
                        {
                            authId = e24tp.getTransId();
                            authCode = e24tp.getAuth();

                            authReceiptNo = e24tp.getRef();
                            authQsiResponseCode = e24tp.getResponseCode();
                            if ("00".equals(authQsiResponseCode))// converting the response 00 to 0
                            {
                                authQsiResponseCode = "0";
                            }
                            authQsiResponseDesc = e24tp.getResult();//"Transaction Successful";//

                            log.debug("AuthId=" + authId);
                            log.debug("AuthCode=" + authCode);
                            log.debug("authReceiptNo =" + authReceiptNo);
                            log.debug("authQsiResponseCode=" + authQsiResponseCode);
                            log.debug("authQsiResponseDesc=" + authQsiResponseDesc + " " + e24tp.getResult());

                        } // End of if
                        else
                        {
                            log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + e24tp.getTrackId());
                            throw new SystemError("There was an Error while authentication. Please contact your System Administrator");
                        } // End of else.
                    }
                }
                catch (Exception e)
                {
                    log.info("Error while posting data to Bank " + e.getMessage());
                    throw new SystemError("#1234#" + e.getMessage());
                }

            }
            catch (SystemError systemError)
            {
                authId = "";
            }
        }
        else
        {
            log.info("Card number is invalid.");
            authId = "";
            authCode = "";
            authReceiptNo = "";
            authQsiResponseCode = "";
            authQsiResponseDesc = "Invalid Card number";

        }
         ccNum=null;
        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashforAuth(authId, authCode, authReceiptNo, authQsiResponseCode, authQsiResponseDesc);

        log.info("Leaving processAuthentication returning Hash " + returnHash);
        return returnHash;
    }


    public Hashtable processCapture(String trackingID, String captureAmount, String authId, String authCode, String authRRN, String accountID) throws SystemError
    {

        log.info("Inside processCapture");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        String name = "";
        String ccNum = "";
        String expMonth = "";
        String expYear = "";
        String ccid = "";


        Hashtable<String, String> transDetails = getTransactionDetails(trackingID);
        if (transDetails != null)
        {
            name = transDetails.get("name");
            ccNum = Functions.decryptString(transDetails.get("ccnum"));
            String expDate = transDetails.get("expdate").trim();//e.g 05/2010
            int index = expDate.indexOf("/");
            expMonth = expDate.substring(0, index);
            expYear = expDate.substring(index + 1, expDate.length());
            ccid = transDetails.get("ccid");

        }
        else
        {
            throw new SystemError("Transaction Not found");
        }


        e24TranPipe e24tp = new e24TranPipe();
        e24tp.setAlias(account.getAliasName());
        e24tp.setResourcePath(getResourcePath(account.getAliasName()));
        e24tp.setAction(CAPTURE);
        e24tp.setAmt(captureAmount);
        e24tp.setTrackId(trackingID);
        e24tp.setTransId(authId);
        e24tp.setMember(name);
        e24tp.setCard(ccNum);
        e24tp.setCvv2(ccid);
        e24tp.setExpMonth(expMonth);
        e24tp.setExpYear(expYear);

        e24tp.setCurrencyCode(getCurrencyCode(account.getCurrency()));
        String captureId = null;
        String captureCode = null;
        String captureReceiptNo = null;
        String captureQsiResponseCode = null;
        String captureQsiResponseDesc = null;
        String captureAVSResult = null;

        try
        {


            int result;

            try
            {

                try
                {
                    e24tp.setDebug(log_request);
                    result = e24tp.performTransaction();
                }
                catch (NotEnoughDataException e)
                {
                    log.info("calling SendMAil for Authentication Error");
                    Mail.sendAdminMail("Error while capture for trackingID-" + trackingID, e.getMessage());
                    log.info("called SendMAil for ");

                    throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
                }


                writeToFile(e24tp.getDebugMsg(),request_log_path);

                // Print out results
                if (result == e24TranPipe.FAILURE)
                {

                    captureId = "";
                    captureCode = "";
                    captureReceiptNo = "";
                    captureQsiResponseCode = e24tp.getError();
                    captureQsiResponseDesc = e24tp.getErrorText();

                }
                else if (result == e24TranPipe.SUCCESS)
                {

                    //commenting validation as we are not getting tracking ID back
                   /* if (trackingID.equals(e24tp.getTrackId()))  //check whether we got result for same trackingid
                    {*/

                        captureId = e24tp.getTransId();
                        captureCode = e24tp.getAuth();
                        captureReceiptNo = e24tp.getRef();
                        captureQsiResponseCode = e24tp.getResponseCode();
                        if ("00".equals(captureQsiResponseCode))
                        {
                            captureQsiResponseCode = "0";
                        }
                        captureQsiResponseDesc = e24tp.getResult();//"Transaction Successful";//

                        log.debug("CaptureId=" + captureId);
                        log.debug("CaptureCode=" + captureCode);
                        log.debug("captureReceiptNo=" + captureReceiptNo);
                        log.debug("captureQsiResponseCode =" + captureQsiResponseCode);
                        log.debug("captureQsiResponseDesc =" + captureQsiResponseDesc + " " + e24tp.getResult());


                   /* }
                    else
                    {*/
                        log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + e24tp.getTrackId());
                   /*     throw new SystemError("There was an Error while capture. Please contact your System Administrator");
                    }*/
                }
            }
            catch (Exception e)
            {
                writeToFile(e24tp.getDebugMsg(),request_log_path);
                log.error("Error while posting data to Bank " , e);
                throw new SystemError("#1234#" + e.getMessage());
            }

            //@ToDo check if this validation required.
            /*
              if ( captureQsiResponseCode == null || !captureQsiResponseCode.equals( "0" ) )
            {
                throw new SystemError( "#1234# There was an error while capture : " + captureQsiResponseDesc );
            }
            */

        }
        catch (SystemError se)
        {
            log.error("Exception while capture in newProcesscapture " , se);
            captureId = "";
        }
        ccNum=null;
        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashforCapture(captureId, captureCode, captureReceiptNo, captureQsiResponseCode, captureQsiResponseDesc);

        log.info("Leaving processCapture returning Hash " + returnHash);
        return returnHash;

    }

    public Hashtable processRefund(String trackingID, String refundAmount, String captureId, String captureCode, String captureRRN, String accountID) throws SystemError
    {
        log.info("Inside processRefund");

        String ccNum = "", expYear = "", expMonth = "", name = "", ccid = "";
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from transaction_icicicredit where icicitransid=" + trackingID, conn);
            while (rs.next())
            {
                ccNum = Functions.decryptString(rs.getString("ccnum"));
                String expDate = rs.getString("expdate").trim();//e.g 05/2010
                int index = expDate.indexOf("/");
                expMonth = expDate.substring(0, index);
                expYear = expDate.substring(index + 1, expDate.length());
                name = rs.getString("name");
                ccid = rs.getString("ccid");
            }
        }
        catch (SQLException e)
        {
            log.error(" exception " + Functions.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        e24TranPipe e24tp = new e24TranPipe();
        e24tp.setAlias(account.getAliasName());
        e24tp.setResourcePath(getResourcePath(account.getAliasName()));
        e24tp.setAction(REFUND);
        e24tp.setAmt(refundAmount);
        e24tp.setTrackId(trackingID);
        e24tp.setTransId(captureId);
        e24tp.setMember(name);
        e24tp.setCard(ccNum);
        e24tp.setExpMonth(expMonth);
        e24tp.setExpYear(expYear);
        e24tp.setCvv2(ccid);

        String currency = account.getCurrency();
        e24tp.setCurrencyCode(getCurrencyCode(currency));

        String refundId = null;
        String refundCode = null;
        String refundReceiptNo = null;
        String refundQsiResponseCode = null;
        String refundQsiResponseDesc = null;

        try
        {

            int result;

            try
            {
                try
                {
                    e24tp.setDebug(log_request);
                    result = e24tp.performTransaction();
                }
                catch (NotEnoughDataException e)
                {
                    log.info("calling SendMAil for Authentication Error");
                    Mail.sendAdminMail("Error while capture for trackingID-" + trackingID, e.getMessage());
                    log.info("called SendMAil");
                    throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
                }

                writeToFile(e24tp.getDebugMsg(),request_log_path);
                // Print out results
                if (result == e24TranPipe.FAILURE)
                {

                    refundId = "";
                    refundCode = "";
                    refundReceiptNo = "";
                    refundQsiResponseCode = e24tp.getError();
                    refundQsiResponseDesc = e24tp.getErrorText();

                }
                else if (result == e24TranPipe.SUCCESS)
                {

                    if (trackingID.equals(e24tp.getTrackId()))  //check whether we got result for same trackingid
                    {

                        captureId =
                                refundId = e24tp.getTransId();
                        refundCode = e24tp.getAuth();
                        refundReceiptNo = e24tp.getRef();
                        refundQsiResponseCode = e24tp.getResponseCode();
                        if ("00".equals(refundQsiResponseCode))
                        {
                            refundQsiResponseCode = "0";
                        }
                        refundQsiResponseDesc = e24tp.getResult();//"Transaction Successful";//

                        log.debug("refundId=" + refundId);
                        log.debug("refundCode=" + refundCode);
                        log.debug("refundReceiptNo=" + refundReceiptNo);
                        log.debug("refundQsiResponseCode =" + refundQsiResponseCode);
                        log.debug("refundQsiResponseDesc =" + refundQsiResponseDesc);

                    } // End of if
                    else
                    {
                        log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + e24tp.getTrackId());
                        throw new SystemError("There was an Error while refund. Please contact your System Administrator");
                    } // End of else.
                }

            }
            catch (Exception e)
            {
                log.info("Error while posting data to Bank " + e.getMessage());
                throw new SystemError("#1234#" + e.getMessage());
            }

            //@ToDo check if this validation required.

            /* if ( refundQsiResponseCode == null || !refundQsiResponseCode.equals( "0" ) )
            {
                throw new SystemError( "#1234# There was an error while refund : " + refundQsiResponseDesc );
            }*/




        }
        catch (SystemError se)
        {
            log.error("Exception while capture in newProcesscapture " , se);
            refundId = "";
        }
         ccNum=null;
        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashForRefund(refundId, refundCode, refundReceiptNo, refundQsiResponseCode, refundQsiResponseDesc);
        log.info("Leaving processRefund returning Hash " + returnHash);
        return returnHash;
    }


    public Hashtable getStatus(String trackingID, String accountId) throws SystemError
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        e24TranPipe e24tp = new e24TranPipe();
        e24tp.setAlias(account.getAliasName());
        e24tp.setResourcePath(getResourcePath(account.getAliasName()));
        e24tp.setAction(INQUIRY);
        e24tp.setTrackId(trackingID);
        e24tp.setCurrencyCode(getCurrencyCode(account.getCurrency()));


        int result;

        String id = null;
        String code = null;
        String receiptNo = null;
        String qsiResponseCode = null;
        String qsiResponseDesc = null;
        String status = null;


        try
        {
            e24tp.setDebug(log_request);
            result = e24tp.performTransaction();
        }
        catch (NotEnoughDataException e)
        {
            log.info("calling SendMAil for Authentication Error");
            Mail.sendAdminMail("Error while authentication for trackingID-" + trackingID, e.getMessage());
            log.info("called SendMAil ");
            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }

       writeToFile(e24tp.getDebugMsg(),request_log_path);

        if (result == e24TranPipe.FAILURE)
        {
            id = "";
            code = "";
            receiptNo = "";
            qsiResponseCode = e24tp.getError();
            qsiResponseDesc = e24tp.getErrorText();
            status = "Error";
        }
        else if (result == e24TranPipe.SUCCESS)
        {
            id = e24tp.getTransId();
            code = e24tp.getAuth();
            receiptNo = e24tp.getRef();
            qsiResponseDesc = e24tp.getResult();
            qsiResponseCode = e24tp.getResponseCode();
            status = qsiResponseDesc;
            if ("00".equals(qsiResponseCode))
            {
                qsiResponseCode = "0";
            }

            log.debug("id=" + id);
            log.debug("code=" + code);
            log.debug("receiptNo=" + receiptNo);
            log.debug("qsiResponseDesc =" + qsiResponseDesc);
            log.debug("qsiResponseCode =" + qsiResponseCode);

        }
        else
        {
            log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + e24tp.getTrackId());
            throw new SystemError("There was an Error while gettinfg Details. Please contact your System Administrator");
        }

/*        System.out.println("Amount : " + e24tp.getAmt());
        System.out.println("Card : " + e24tp.getCard());
        System.out.println("Type : " + e24tp.getType());
        System.out.println("Exp day : " + e24tp.getExpDay());
        System.out.println("Exp Month : " + e24tp.getExpMonth());
        System.out.println("Exp Year : 1" + e24tp.getExpYear());
        System.out.println("Result : " + e24tp.getResult());
        System.out.println("Member : " + e24tp.getMember());

        System.out.println("Trans id. : " + e24tp.getTransId());
        System.out.println("Auth : " + e24tp.getAuth());
        System.out.println("Ref no. : " + e24tp.getRef());

        System.out.println("UDF. : " + e24tp.getUdf1() + e24tp.getUdf2());
        System.out.println("Debug : " + e24tp.getDebugMsg());*/
        return processHashForGetDetails(trackingID, status, id, code, receiptNo, qsiResponseCode, qsiResponseDesc);
    }

    public Hashtable processVoidAuth(String trackingID, String accountId) throws SystemError
    {
        log.info("Entering process Void Auth");
        String name = "", authId = "", amount = "";
        Hashtable<String, String> transDetails = getTransactionDetails(trackingID);
        if (transDetails != null)
        {
            name = transDetails.get("name");
            authId = transDetails.get("authid");
            amount = transDetails.get("amount");
        }
        else
        {
            throw new SystemError("Transaction Not found");
        }


        log.info("Entering proces Cancel");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        e24TranPipe e24tp = new e24TranPipe();
        e24tp.setAlias(account.getAliasName());
        e24tp.setResourcePath(getResourcePath(account.getAliasName()));
        e24tp.setAction(VOID_AUTH);
        e24tp.setTrackId(trackingID);
        e24tp.setTransId(authId);
        e24tp.setAmt(amount);
        e24tp.setMember(name);
        e24tp.setCurrencyCode(getCurrencyCode(account.getCurrency()));

        int result;

        String cancellationId = null;
        String cancellationCode = null;
        String cancellationReceiptNo = null;
        String cancellationQsiResponseCode = null;
        String cancellationQsiResponseDesc = null;

        try
        {
            e24tp.setDebug(log_request);
            result = e24tp.performTransaction();
        }
        catch (NotEnoughDataException e)
        {
            log.info("calling SendMAil for Authentication Error");
            Mail.sendAdminMail("Error while authentication for trackingID-" + trackingID, e.getMessage());
            log.info("called SendMAil for ");
            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }

        writeToFile(e24tp.getDebugMsg(),request_log_path);

        if (result == e24TranPipe.FAILURE)
        {
            cancellationId = "";
            cancellationCode = "";
            cancellationReceiptNo = "";
            cancellationQsiResponseCode = e24tp.getError();
            cancellationQsiResponseDesc = e24tp.getErrorText();

        }
        else if (result == e24TranPipe.SUCCESS)
        {
            cancellationId = e24tp.getTransId();
            cancellationCode = e24tp.getAuth();
            cancellationReceiptNo = e24tp.getRef();
            cancellationQsiResponseDesc = e24tp.getResult();
            cancellationQsiResponseCode = e24tp.getResponseCode();
            if ("00".equals(cancellationQsiResponseCode))
            {
                cancellationQsiResponseCode = "0";
            }

            log.debug("cancellationId=" + cancellationId);
            log.debug("cancellationCode=" + cancellationCode);
            log.debug("cancellationReceiptNo=" + cancellationReceiptNo);
            log.debug("cancellationQsiResponseDesc =" + cancellationQsiResponseDesc);
            log.debug("cancellationQsiResponseCode =" + cancellationQsiResponseCode);

        }
        else
        {
            log.info("Tracking ID we have=" + trackingID + " tracking cancellationId we got=" + e24tp.getTrackId());
            throw new SystemError("There was an Error while gettinfg Details. Please contact your System Administrator");
        }

       /* System.out.println("Amount : " + e24tp.getAmt());
        System.out.println("Card : " + e24tp.getCard());
        System.out.println("Type : " + e24tp.getType());
        System.out.println("Exp day : " + e24tp.getExpDay());
        System.out.println("Exp Month : " + e24tp.getExpMonth());
        System.out.println("Exp Year : 1" + e24tp.getExpYear());
        System.out.println("Result : " + e24tp.getResult());
        System.out.println("Member : " + e24tp.getMember());

        System.out.println("Trans cancellationId. : " + e24tp.getTransId());
        System.out.println("Auth : " + e24tp.getAuth());
        System.out.println("Ref no. : " + e24tp.getRef());

        System.out.println("UDF. : " + e24tp.getUdf1() + e24tp.getUdf2());

        System.out.println(" Id : " + cancellationId);
        System.out.println(" Result : " + result);
        System.out.println(" Code : " + cancellationCode);
        System.out.println(" cancellationReceiptNo : " + cancellationReceiptNo);
        System.out.println(" qsiresponsedesc : " + cancellationQsiResponseDesc);
        System.out.println(" qsiresponsecode : " + cancellationQsiResponseCode);*/

        Hashtable returnHash = processResultHashForCancellation(cancellationId, cancellationCode, cancellationReceiptNo, cancellationQsiResponseCode, cancellationQsiResponseDesc);
        log.info("Leaving process VoidAuth returning Hash " + returnHash);

        return returnHash;
    }

    public Hashtable processVoidCapture(String trackingID, String accountId) throws SystemError
    {
        log.info("Entering process VoidCapture");
        String name = "", amount = "", captureId = "", ccNum = "", expMonth = "", expYear = "", ccid = "";
        Hashtable<String, String> transDetails = getTransactionDetails(trackingID);
        if (transDetails != null)
        {
            name = transDetails.get("name");
            captureId = transDetails.get("captureid");
            log.debug("Capture Id " + captureId);
            amount = transDetails.get("amount");

            ccNum = Functions.decryptString(transDetails.get("ccnum"));
            String expDate = transDetails.get("expdate").trim();//e.g 05/2010
            int index = expDate.indexOf("/");
            expMonth = expDate.substring(0, index);
            expYear = expDate.substring(index + 1, expDate.length());
            ccid = transDetails.get("ccid");
        }
        else
        {
            throw new SystemError("Transaction Not found");
        }


        log.info("Entering proces Void Capture");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        e24TranPipe e24tp = new e24TranPipe();
        e24tp.setAlias(account.getAliasName());
        e24tp.setResourcePath(getResourcePath(account.getAliasName()));
        e24tp.setAction(VOID_CAPTURE);

        e24tp.setTrackId(trackingID);
        e24tp.setTransId(captureId);
        e24tp.setAmt(amount);
        e24tp.setMember(name);

        e24tp.setCard(ccNum);
        e24tp.setCvv2(ccid);
        e24tp.setExpMonth(expMonth);
        e24tp.setExpYear(expYear);

        e24tp.setCurrencyCode(getCurrencyCode(account.getCurrency()));

        int result;

        String cancellationId = null;
        String cancellationCode = null;
        String cancellationReceiptNo = null;
        String cancellationQsiResponseCode = null;
        String cancellationQsiResponseDesc = null;

        try
        {
            e24tp.setDebug(log_request);
            result = e24tp.performTransaction();
        }
        catch (NotEnoughDataException e)
        {
            log.info("calling SendMAil for Authentication Error");
            Mail.sendAdminMail("Error while authentication for trackingID-" + trackingID, e.getMessage());
            log.info("called SendMAil ");
            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }

        writeToFile(e24tp.getDebugMsg(),request_log_path);

        if (result == e24TranPipe.FAILURE)
        {
            cancellationId = "";
            cancellationCode = "";
            cancellationReceiptNo = "";
            cancellationQsiResponseCode = e24tp.getError();
            cancellationQsiResponseDesc = e24tp.getErrorText();

        }
        else if (result == e24TranPipe.SUCCESS)
        {
            cancellationId = e24tp.getTransId();
            cancellationCode = e24tp.getAuth();
            cancellationReceiptNo = e24tp.getRef();
            cancellationQsiResponseDesc = e24tp.getResult();
            cancellationQsiResponseCode = e24tp.getResponseCode();
            if ("00".equals(cancellationQsiResponseCode))
            {
                cancellationQsiResponseCode = "0";
            }

            log.debug("cancellationId=" + cancellationId);
            log.debug("cancellationCode=" + cancellationCode);
            log.debug("cancellationReceiptNo=" + cancellationReceiptNo);
            log.debug("cancellationQsiResponseDesc =" + cancellationQsiResponseDesc);
            log.debug("cancellationQsiResponseCode =" + cancellationQsiResponseCode);

        }
        else
        {
            log.info("Tracking ID we have=" + trackingID + " tracking cancellationId we got=" + e24tp.getTrackId());
            throw new SystemError("There was an Error while gettinfg Details. Please contact your System Administrator");
        }

       /* System.out.println("Amount : " + e24tp.getAmt());
        System.out.println("Card : " + e24tp.getCard());
        System.out.println("Type : " + e24tp.getType());
        System.out.println("Exp day : " + e24tp.getExpDay());
        System.out.println("Exp Month : " + e24tp.getExpMonth());
        System.out.println("Exp Year : 1" + e24tp.getExpYear());
        System.out.println("Result : " + e24tp.getResult());
        System.out.println("Member : " + e24tp.getMember());

        System.out.println("Trans cancellationId. : " + e24tp.getTransId());
        System.out.println("Auth : " + e24tp.getAuth());
        System.out.println("Ref no. : " + e24tp.getRef());

        System.out.println("UDF. : " + e24tp.getUdf1() + e24tp.getUdf2());

        System.out.println(" Id : " + cancellationId);
        System.out.println(" Result : " + result);
        System.out.println(" Code : " + cancellationCode);
        System.out.println(" cancellationReceiptNo : " + cancellationReceiptNo);
        System.out.println(" qsiresponsedesc : " + cancellationQsiResponseDesc);
        System.out.println(" qsiresponsecode : " + cancellationQsiResponseCode);*/

        Hashtable returnHash = processResultHashForCancellation(cancellationId, cancellationCode, cancellationReceiptNo, cancellationQsiResponseCode, cancellationQsiResponseDesc);
        log.info("Leaving processVoidCapture returning Hash " + returnHash);
        ccNum=null;
        return returnHash;
    }

    protected Hashtable<String, String> getTransactionDetails(String trackingID)
            throws SystemError
    {
        Connection conn = null;
        Hashtable details = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from transaction_icicicredit where icicitransid=" + trackingID, conn);
            details = Util.getHashFromResultSet(rs);
        }
        catch (Exception e)
        {
            log.error(" exception " + Functions.getStackTrace(e));
            throw new SystemError(Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }
        // since we'll have only 1 row for that tracking Id
        return (Hashtable<String, String>) details.get("1");
    }

    public static String getCurrencyCode(String currency) throws SystemError
    {
        log.info("Entering inside getCurrencyCode ");
        String currCode = currency_code.get(currency);
        log.info("Leaving getCurrencyCode ");
        return currCode;
    }

    public String getResourcePath(String aliasName)
    {
        if (FILE_SEPARATOR != null && !FILE_SEPARATOR.equals((RESOURCE_PATH.substring(RESOURCE_PATH.length() - 1, RESOURCE_PATH.length()))))
            RESOURCE_PATH = RESOURCE_PATH + FILE_SEPARATOR;

        log.info("Returning resourse path as "+RESOURCE_PATH + aliasName + FILE_SEPARATOR);
        return RESOURCE_PATH + aliasName + FILE_SEPARATOR;

        //return RESOURCE_PATH+ FILE_SEPARATOR;
    }

   protected void writeToFile(String line, String filePath)
   {
       try
       {
           if (log_request && request_log_path != null && line!=null && line.length() > 0)
           {
               FileWriter fw = new FileWriter(filePath, true);
               fw.write( Calendar.getInstance().getTime() + "\n");
               fw.write(line + "\n");
               fw.write("###############################################\n");
               fw.close();
           }
       }
       catch (IOException e)
       {
           log.error("Below line couldn't write in File \n" + line +" because "+e.getMessage());
       }

   }
    public static void main(String[] args) throws Exception
    {
        //System.out.println("Status got " + new ACIKitHandler().getStatus("117401", "4"));
         log.debug("Status got " + new ACIKitHandler().getStatus("117401", "4"));
    }
}



