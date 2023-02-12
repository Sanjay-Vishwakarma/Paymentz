package com.directi.pg.core.handler;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.aciworldwide.commerce.gateway.plugins.UniversalPlugin;

import java.util.Hashtable;
import java.util.Set;

/**
 * This handler is extension to existing ACI handler which uses e24transpipe.
 * The MPIVerifyEnrollment and MPIPayerAuthentication Universal Plug-in transactions are used as a pair to provide
 * enrollment verification and consumer authorization with a 3-D Secure Access Control Server (ACS) through
 * the ACI Commerce Gateway.
 * <p/>
 * The MPIVerifyEnrollment transaction requests the ACS to verify the enrollment status of a submitted card number.
 * <p/>
 * If the card is not enrolled, by default, the ACI Commerce Gateway processes the transaction as a credit card transaction,
 * and the returned fields will be the same as those returned by the PaymentTran Universal Plug-in transaction.
 * <p/>
 * If the card is enrolled, the consumer must authenticate themselves to the ACS before the transaction can continue
 * to be processed as a 3-D Secure transaction.
 * <p/>
 * User: Alpesh.s
 * Date: Mar 1, 2007
 * Time: 3:25:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ACIUniversalKitHandler extends ACIKitHandler
{
    private static Logger log = new Logger(ACIUniversalKitHandler.class.getName());

    private static String RESOURCE_TEMP_PATH = ApplicationProperties.getProperty("RESOURCE_TEMP_PATH");

    private final static String TRANS_TYPE_MPIVerifyEnrollment = "MPIVerifyEnrollment";
    private final static String TRANS_TYPE_MPIPayerAuthentication = "MPIPayerAuthentication";
    private final static String TRANS_TYPE_TranPortal = "TranPortal";
    private final static String TRANS_TYPE_PaymentTran = "PaymentTran";

    //input fields
    private final static String FIELD_ACTION = "action";
    private final static String FIELD_CARD = "card";
    private final static String FIELD_EXPIRY_YEAR = "expyear";
    private final static String FIELD_EXPIRY_MONTH = "expmonth";
    private final static String FIELD_EXPIRY_DAY = "expday";
    private final static String FIELD_MEMBER = "member";
    private final static String FIELD_CVV2 = "cvv2";
    private final static String FIELD_TYPE = "type";
    private final static String FIELD_ADDR = "addr";
    private final static String FIELD_ZIP = "zip";
    private final static String FIELD_AMT = "amt";
    private final static String FIELD_CURRENCYCODE = "currencycode";
    private final static String FIELD_TRACKID = "trackid";
    private final static String FIELD_TRANSID = "transid";
    private final static String FIELD_VERSION = "1";
    private final static String FIELD_PARES = "pares";
    private final static String FIELD_PAYMENTID = "paymentid";
    private final static String FIELD_TYPE_VALE = "CC";

    //retun fields
    private final static String FIELD_TRANID = "tranid";
    private final static String FIELD_AUTH = "auth";
    private final static String FIELD_REF = "ref";
    private final static String FIELD_RESPONSECODE = "responsecode";
    private final static String FIELD_RESULT = "result";

    private final static String STATUS_ENROLLED = "ENROLLED";

    /**
     * This transaction makes available communication to an Access Control Server (ACS) via the ACI Commerce Gateway for
     * performing the 3-D Secure �VEReq/VERes� transactions as well as generating the 3-D Secure �PAReq� request
     * necessary to start consumers with their authentication process with the ACS.
     *
     * @param trackingID
     * @param transAmount
     * @param cardDetailHash
     * @param billingAddrHash
     * @param accountID
     * @return
     * @throws SystemError
     */

    public Hashtable processVerifyEnrollment(String trackingID, String transAmount, Hashtable cardDetailHash, Hashtable billingAddrHash, String accountID) throws SystemError
    {
        log.debug("Entering processVerifyEnrollment");

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOff();

        //
        // Populate the Plugin to perform the MPIVerifyEnrollment version 1 transaction
        //
        CGPipe.setTransactionType(TRANS_TYPE_MPIVerifyEnrollment);
        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());
        CGPipe.setTempDirectory(getTempResourcePath(account.getAliasName()));

        
        //
        // Populate the MPIVerifyEnrollment Transaction details
        //

        CGPipe.set(FIELD_ACTION, AUTH);    // 1 - Purchase, 4 - Authorization

        CGPipe.set(FIELD_AMT, transAmount);
        CGPipe.set(FIELD_CURRENCYCODE, getCurrencyCode(account.getCurrency()));
        CGPipe.set(FIELD_TRACKID, trackingID);

        String ccNum;
        if (cardDetailHash != null)
        {
            ccNum = (String) cardDetailHash.get("ccnum");
            CGPipe.set(FIELD_CARD, ccNum);

            CGPipe.set(FIELD_EXPIRY_YEAR, (String) cardDetailHash.get("year"));
            CGPipe.set(FIELD_EXPIRY_MONTH, (String) cardDetailHash.get("month"));
            CGPipe.set(FIELD_EXPIRY_DAY, "");
            CGPipe.set(FIELD_MEMBER, (String) cardDetailHash.get("name"));

            if(account.isCvvRequired())
                CGPipe.set(FIELD_CVV2, (String) cardDetailHash.get("ccid"));

            String type = (String) cardDetailHash.get("type");
            if (Functions.parseData(type) == null)
            { //type = "VPAS";
                type = FIELD_TYPE_VALE;
            }
            CGPipe.set(FIELD_TYPE, type);

        }
        else
            throw new SystemError("#1234#Card details are Missing");
        ccNum=null;
        if (billingAddrHash != null)
        {
            String address = (String) billingAddrHash.get("address") + " " +
                    (String) billingAddrHash.get("city") + " " +
                    (String) billingAddrHash.get("state") + " " +
                    (String) billingAddrHash.get("ccode");

            CGPipe.set(FIELD_ADDR, address);
            CGPipe.set(FIELD_ZIP, (String) billingAddrHash.get("zip"));

        }
        else
            throw new SystemError("#1234#Billing address details are Missing");


        String authId = null;
        String authCode = null;
        String authReceiptNo = null;
        String authQsiResponseCode = null;
        String authQsiResponseDesc = null;
        boolean success = false;

        Hashtable returnHash = new Hashtable();

        /*if (Functions.isValid(ccNum))
        {*/

            try
            {
                CGPipe.setTraceOn();
                success = CGPipe.performTransaction();
                CGPipe.setTraceOff();
                String error = CGPipe.get("error_code_tag");
                String errortext = CGPipe.get("error_text");

                // Call was not successfull
                if (!success)
                {

                    authId = "";
                    authCode = "";
                    authReceiptNo = "";
                    authQsiResponseCode = "-1";
                    authQsiResponseDesc = CGPipe.getErrorText();

                }
                //call was successful but got error during MPIVerifyEnrollMent
                else if (Functions.checkStringNull(error) != null)
                {
                    authId = "";
                    authCode = "";
                    authReceiptNo = "";
                    authQsiResponseCode = error;
                    authQsiResponseDesc = errortext;
                }
                else
                {
                    //MPIVerifyEnrollMent is successfullly performed without any error
                    // If the user is ENROLLED then this page redirects the consumer browser into the
                    // Commerce Gateway via the returned url which will allow the consumer to authenticate with the ACS.
                    //
                    // If the user is NOT ENROLLED then by default the Commerce Gateway will have performed
                    // the requested transaction as a Credit Card and returned the status of the purchase (aka the flow is done).

                    if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                    {
                        String result = CGPipe.get(FIELD_RESULT);
                        String aVResult = null;
                        String paymentId = null;   //Unique ID generated by and used within the ACI Commerce Gateway to identify a payment.
                        String cvv2Response = null;

                        String acsURL = null;
                        String PAReq = null;

                        if (!STATUS_ENROLLED.equals(result))
                        {
                            // The user is not enrolled.  By Default the Commerce Gateway will automatically process the payment as a
                            // Credit Card transaction so read the reply data

                            authId = CGPipe.get(FIELD_TRANID);
                            authCode = CGPipe.get(FIELD_AUTH);

                            authReceiptNo = CGPipe.get(FIELD_REF);
                            authQsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);

                            paymentId = CGPipe.get(FIELD_PAYMENTID);

                            aVResult = CGPipe.get("avr");
                            cvv2Response = CGPipe.get("cvv2response");

                            if ("00".equals(authQsiResponseCode))// converting the response 00 to 0
                            {
                                authQsiResponseCode = "0";
                            }
                            authQsiResponseDesc = result;//"ENROLLED or NOT ENROLLED in case of VEReq";//

                            log.debug("AuthId=" + authId);
                            log.debug("AuthCode=" + authCode);
                            log.debug("paymentId=" + paymentId);
                            log.debug("authReceiptNo =" + authReceiptNo);
                            log.debug("authQsiResponseCode=" + authQsiResponseCode);
                            log.debug("authQsiResponseDesc=" + authQsiResponseDesc);

                        } // End of if
                        else
                        {
                            //If the Consumer was ENROLLED, Then we have a PAReq.
                            //If we have a PAReq, we will redirect the user to the Commerce Gateway to Authenticate with the ACS.

                            authQsiResponseCode = "E"; //temporary code, it will get chaged after MPIPA transaction
                            authQsiResponseDesc = result;

                            paymentId = CGPipe.get("paymentid"); //Unique ID generated by and used within the ACI Commerce Gateway to identify a payment.

                            acsURL = CGPipe.get("url");
                            PAReq = CGPipe.get("PAReq");

                            log.debug("paymentId=" + paymentId);
                            log.debug("authQsiResponseCode=" + authQsiResponseCode);
                            log.debug("authQsiResponseDesc=" + authQsiResponseDesc);

                        }
                        log.debug(processResultHashforVEReq(paymentId, aVResult, cvv2Response, acsURL, PAReq));
                        returnHash.putAll(processResultHashforVEReq(paymentId, aVResult, cvv2Response, acsURL, PAReq));

                    }
                    else
                    {
                        log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                        throw new SystemError("There was an Error while authentication. Please contact your System Administrator");
                    } // End of else.

                }
            }
            catch (Exception e)
            {
                log.error("Error while verifying enrollment " + e.getMessage());
                throw new SystemError("#1234#" + e.getMessage());
            }
            finally
            {
                writeToFile(CGPipe.getTraceMessages(), request_log_path);
            }

        /*}
        else
        {
            log.info("Card number is invalid.");
            authId = "";
            authCode = "";
            authReceiptNo = "";
            authQsiResponseCode = "";
            authQsiResponseDesc = "Invalid Card number";

        }*/

        //Fill the result in hashtable to return to client
        returnHash.putAll(processResultHashforAuth(authId, authCode, authReceiptNo, authQsiResponseCode, authQsiResponseDesc));

        log.debug("Leaving processVerifyEnrollment returning Hash " + returnHash);
        return returnHash;

    }

    /**
     * Objective of this method is to pass the PARes that the ACS returned back into the Commerce Gateway
     * so that the transaction can continue.
     * <p/>
     * This transaction makes available communication to an Access Control Server (ACS) via the ACI Commerce Gateway for
     * continuing the 3-D Secure transactions started by MPIVerifyEnrollment by submitting the 3-D Secure �PARes� message
     * returned from the ACS to the ACI Commerce Gateway along with the payment ID that was returned after the MPIVerifyEnrollment.
     *
     * @param trackingID
     * @param PARes
     * @param paymentId
     * @param accountID
     * @return
     * @throws SystemError
     */

    public Hashtable processPayerAuthentication(String trackingID, String PARes, String paymentId, String accountID) throws SystemError
    {
        log.debug("Entering processPayerAuthentication");

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOff();

        //
        // Populate the Plugin to perform the MPIVerifyEnrollment version 1 transaction
        //
        CGPipe.setTransactionType(TRANS_TYPE_MPIPayerAuthentication);
        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());

        //
        //  Get and Set the MD (paymentid) and PARes for the MPIPayerAuthentication Transaction
        //

        CGPipe.set(FIELD_PARES, PARes);
        CGPipe.set(FIELD_PAYMENTID, paymentId);

        String authId = null;
        String authCode = null;
        String authReceiptNo = null;
        String authQsiResponseCode = null;
        String authQsiResponseDesc = null;
        boolean success = false;

        Hashtable returnHash = new Hashtable();

        try
        {
            CGPipe.setTraceOn();
            success = CGPipe.performTransaction();
            CGPipe.setTraceOff();
            String error = CGPipe.get("error_code_tag");
            String errortext = CGPipe.get("error_text");

            java.util.HashMap fileds = CGPipe.getResponseFields();
            Set<String> keys= fileds.keySet();

            // Call was not successfull
            if (!success)
            {

                authId = "";
                authCode = "";
                authReceiptNo = "";
                authQsiResponseCode = "-1";
                authQsiResponseDesc = CGPipe.getErrorText();

            }
            //call was successful but got error during MPIPayerAuthentication
            else if (Functions.checkStringNull(error) != null)
            {
                authId = "";
                authCode = "";
                authReceiptNo = "";
                authQsiResponseCode = error;
                authQsiResponseDesc = errortext;
            }
            else
            {
                //MPIPayerAuthentication is successfullly performed without any error
                //Transaction is also performed this time so
                if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                {
                    String result = CGPipe.get(FIELD_RESULT);
                    String aVResult = null;
                    String paymentID = null;   //Unique ID generated by and used within the ACI Commerce Gateway to identify a payment.
                    String cvv2Response = null;

                    //collect other transactions details as per normal transactions

                    authId = CGPipe.get(FIELD_TRANID);
                    authCode = CGPipe.get(FIELD_AUTH);

                    authReceiptNo = CGPipe.get(FIELD_REF);
                    authQsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);

                    paymentID = CGPipe.get("paymentid"); //this value shoul b same as passed

                    aVResult = CGPipe.get("avr");
                    cvv2Response = CGPipe.get("cvv2response");

                    if ("00".equals(authQsiResponseCode))// converting the response 00 to 0
                    {
                        authQsiResponseCode = "0";
                    }
                    authQsiResponseDesc = result;//"ENROLLED or NOT ENROLLED in case of VEReq";//

                    log.debug("AuthId=" + authId);
                    log.debug("AuthCode=" + authCode);
                    log.debug("paymentId=" + paymentId);
                    log.debug("authReceiptNo =" + authReceiptNo);
                    log.debug("authQsiResponseCode=" + authQsiResponseCode);
                    log.debug("authQsiResponseDesc=" + authQsiResponseDesc);

                    returnHash.putAll(processResultHashforPAReq(paymentId, aVResult, cvv2Response, PARes));
                }
                else
                {
                    log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                    throw new SystemError("There was an Error while authentication. Please contact your System Administrator");
                } // End of else.

            }
        }
        catch (Exception e)
        {
            log.info("Error while procesing PAResponse " + e.getMessage());
            throw new SystemError("#1234#" + e.getMessage());
        }
        finally
        {
            writeToFile(CGPipe.getTraceMessages(), request_log_path);
        }

        //Fill the result in hashtable to return to client
        returnHash.putAll(processResultHashforAuth(authId, authCode, authReceiptNo, authQsiResponseCode, authQsiResponseDesc));

        log.debug("Leaving processPayerAuthentication returning Hash " + returnHash);
        return returnHash;

    }

    public Hashtable processAuthentication(String trackingID, String transAmount, Hashtable cardDetailHash, Hashtable billingAddrHash, Hashtable shippingAddrHash, Hashtable MPIDataHash, String ipaddress, String accountID) throws SystemError
    {
        log.debug("Entering processAuthentication");

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOff();

        CGPipe.setTransactionType(TRANS_TYPE_TranPortal);
        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());
        CGPipe.setTempDirectory(getTempResourcePath(account.getAliasName()));

        CGPipe.set(FIELD_ACTION, AUTH);    // 1 - Purchase, 4 - Authorization

        CGPipe.set(FIELD_AMT, transAmount);
        CGPipe.set(FIELD_CURRENCYCODE, getCurrencyCode(account.getCurrency()));
        CGPipe.set(FIELD_TRACKID, trackingID);

        String ccNum;
        if (cardDetailHash != null)
        {
            ccNum = (String) cardDetailHash.get("ccnum");
            CGPipe.set(FIELD_CARD, ccNum);

            CGPipe.set(FIELD_EXPIRY_YEAR, (String) cardDetailHash.get("year"));
            CGPipe.set(FIELD_EXPIRY_MONTH, (String) cardDetailHash.get("month"));
            CGPipe.set(FIELD_EXPIRY_DAY, "");
            CGPipe.set(FIELD_MEMBER, (String) cardDetailHash.get("name"));
            if(account.isCvvRequired())
                CGPipe.set(FIELD_CVV2, (String) cardDetailHash.get("ccid"));

            String type = (String) cardDetailHash.get("type");
            if (Functions.parseData(type) == null)
            { //type = "VPAS";
                type = FIELD_TYPE_VALE;
            }
            CGPipe.set(FIELD_TYPE, type);

        }
        else
            throw new SystemError("#1234#Card details are Missing");


        if (billingAddrHash != null)
        {
            String address = (String) billingAddrHash.get("address") + " " +
                    (String) billingAddrHash.get("city") + " " +
                    (String) billingAddrHash.get("state") + " " +
                    (String) billingAddrHash.get("ccode");

            CGPipe.set(FIELD_ADDR, address);
            CGPipe.set(FIELD_ZIP, (String) billingAddrHash.get("zip"));
        }
        else
        {
            log.debug("billing address "+billingAddrHash);
            //throw new SystemError("#1234#Billing address details are Missing");
        }

        String authId = null;
        String authCode = null;
        String authReceiptNo = null;
        String authQsiResponseCode = null;
        String authQsiResponseDesc = null;
        boolean success = false;

        if (Functions.isValid(ccNum))
        {
            try
            {
                CGPipe.setTraceOn();
                success = CGPipe.performTransaction();
                CGPipe.setTraceOff();
                String error = CGPipe.get("error_code_tag");
                String errortext = CGPipe.get("error_text");
                // Call was not successfull
                if (!success)
                {

                    authId = "";
                    authCode = "";
                    authReceiptNo = "";
                    authQsiResponseCode = "-1";
                    authQsiResponseDesc = CGPipe.getErrorText();

                }
                //call was successful but got error during MPIVerifyEnrollMent
                else if (Functions.checkStringNull(error) != null)
                {
                    authId = "";
                    authCode = "";
                    authReceiptNo = "";
                    authQsiResponseCode = error;
                    authQsiResponseDesc = errortext;
                }
                else
                {
                    if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                    {

                        String aVResult = null;
                        String paymentId = null;   //Unique ID generated by and used within the ACI Commerce Gateway to identify a payment.
                        String cvv2Response = null;

                        authId = CGPipe.get(FIELD_TRANID);
                        authCode = CGPipe.get(FIELD_AUTH);

                        authReceiptNo = CGPipe.get(FIELD_REF);
                        authQsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);

                        paymentId = CGPipe.get("paymentid");


                        aVResult = CGPipe.get("avr");
                        cvv2Response = CGPipe.get("cvv2response");

                        if ("00".equals(authQsiResponseCode))// converting the response 00 to 0
                        {
                            authQsiResponseCode = "0";
                        }
                        authQsiResponseDesc = CGPipe.get(FIELD_RESULT);

                        log.debug("AuthId=" + authId);
                        log.debug("AuthCode=" + authCode);
                        log.debug("paymentId=" + paymentId);
                        log.debug("authReceiptNo =" + authReceiptNo);
                        log.debug("authQsiResponseCode=" + authQsiResponseCode);
                        log.debug("authQsiResponseDesc=" + authQsiResponseDesc);

                    }
                    else
                    {
                        log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                        throw new SystemError("There was an Error while authentication. Please contact your System Administrator");
                    } // End of else.

                }
            }
            catch (Exception e)
            {
                log.error("calling SendMAil for Authentication Error " + Util.getStackTrace(e));
                Mail.sendAdminMail("Error while authentication for trackingID-" + trackingID, e.getMessage());
                log.info("called SendMAil");
                throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
            }
            finally
            {
                writeToFile(CGPipe.getTraceMessages(), request_log_path);
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

        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashforAuth(authId, authCode, authReceiptNo, authQsiResponseCode, authQsiResponseDesc);

        log.info("Leaving processAuthentication returning Hash " + returnHash);
        return returnHash;
    }


    public Hashtable processCapture(String trackingID, String captureAmount, String authId, String authCode, String authRRN, String accountID) throws SystemError
    {
        log.info("Inside processCapture");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        String name = "",paymentId=null;

        Hashtable<String, String> transDetails = getTransactionDetails(trackingID);
        if (transDetails != null)
        {
            name = transDetails.get("name");
            paymentId = transDetails.get("paymentid");
        }
        else
        {
            throw new SystemError("Transaction Not found");
        }


        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOn();

        //
        // Populate the Plugin to perform the TranPortal version 1 transaction
        //

        // for VBV transction, we get paymentid and in that case we need to use Paymenttrans method.
        if(Functions.checkStringNull(paymentId)!=null)
            CGPipe.setTransactionType(TRANS_TYPE_PaymentTran);
        else  //For non VBV transaction transportal is used.
            CGPipe.setTransactionType(TRANS_TYPE_TranPortal);

        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());
        CGPipe.setTempDirectory(getTempResourcePath(account.getAliasName()));


        CGPipe.set(FIELD_ACTION, CAPTURE);
        CGPipe.set(FIELD_MEMBER, name);
        CGPipe.set(FIELD_TYPE, FIELD_TYPE_VALE);

        CGPipe.set(FIELD_AMT, captureAmount);
        CGPipe.set(FIELD_CURRENCYCODE, getCurrencyCode(account.getCurrency()));
        CGPipe.set(FIELD_TRACKID, trackingID);
        CGPipe.set(FIELD_TRANSID, authId);
        CGPipe.set(FIELD_PAYMENTID, paymentId);


        String captureId = null;
        String captureCode = null;
        String captureReceiptNo = null;
        String captureQsiResponseCode = null;
        String captureQsiResponseDesc = null;
        boolean success = false;

        try
        {
            success = CGPipe.performTransaction();
            CGPipe.setTraceOff();
            String error = CGPipe.get("error_code_tag");
            String errortext = CGPipe.get("error_text");

/*

            java.util.HashMap fileds = CGPipe.getResponseFields();
            Set<String> keys= fileds.keySet();

            for(String key: keys)
            {
                 log.debug("key="+key+",value="+fileds.get(key));
            }
*/

            // Call was not successfull
            if (!success)
            {
                captureId = "";
                captureCode = "";
                captureReceiptNo = "";
                captureQsiResponseCode = "-1";
                captureQsiResponseDesc = CGPipe.getErrorText();
            }
            //call was successful but got error during capture
            else if (Functions.checkStringNull(error) != null)
            {
                captureId = "";
                captureCode = "";
                captureReceiptNo = "";
                captureQsiResponseCode = error;
                captureQsiResponseDesc = errortext;
            }
            else
            {
                /*if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                {*/
                    captureId = CGPipe.get(FIELD_TRANID);
                    captureCode = CGPipe.get(FIELD_AUTH);
                    captureReceiptNo = CGPipe.get(FIELD_REF);
                    captureQsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);
                    if ("00".equals(captureQsiResponseCode))
                    {
                        captureQsiResponseCode = "0";
                    }
                    captureQsiResponseDesc = CGPipe.get(FIELD_RESULT);

                    log.debug("CaptureId=" + captureId);
                    log.debug("CaptureCode=" + captureCode);
                    log.debug("captureReceiptNo=" + captureReceiptNo);
                    log.debug("captureQsiResponseCode =" + captureQsiResponseCode);
                    log.debug("captureQsiResponseDesc =" + captureQsiResponseDesc);
                /*}
                else
                {
                    log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                    throw new SystemError("There was an Error while authentication. Please contact your System Administrator");
                } // End of else.*/

            }


        }
        catch (Exception e)
        {
            log.error("Error while posting data to Bank " + Util.getStackTrace(e));
            log.info("calling SendMAil for  Capture Error");
            Mail.sendAdminMail("Error while capture for trackingID-" + trackingID, e.getMessage());
            log.info("called SendMAil for ");

            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }
        finally
        {
            writeToFile("\r\n Logging request for "+paymentId+"\r\n"+CGPipe.getTraceMessages(), request_log_path);
        }
        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashforCapture(captureId, captureCode, captureReceiptNo, captureQsiResponseCode, captureQsiResponseDesc);

        log.info("Leaving processCapture returning Hash " + returnHash);
        return returnHash;

    }

    public Hashtable processRefund(String trackingID, String refundAmount, String captureId, String captureCode, String captureRRN, String accountID) throws SystemError
    {
        log.info("Inside processRefund");

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        String name = "",paymentId=null;

        Hashtable<String, String> transDetails = getTransactionDetails(trackingID);
        if (transDetails != null)
        {
            name = transDetails.get("name");
            paymentId = transDetails.get("paymentid");
        }
        else
        {
            throw new SystemError("Transaction Not found");
        }

        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOff();

        //
        // Populate the Plugin to perform the TranPortal version 1 transaction
        //
        CGPipe.setTransactionType(TRANS_TYPE_TranPortal);
        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());
        CGPipe.setTempDirectory(getTempResourcePath(account.getAliasName()));


        CGPipe.set(FIELD_ACTION, REFUND);
        CGPipe.set(FIELD_MEMBER, name);
        CGPipe.set(FIELD_TYPE, FIELD_TYPE_VALE);

        CGPipe.set(FIELD_AMT, refundAmount);
        CGPipe.set(FIELD_CURRENCYCODE, getCurrencyCode(account.getCurrency()));
        CGPipe.set(FIELD_TRACKID, trackingID);
        CGPipe.set(FIELD_TRANSID, captureId);
        CGPipe.set(FIELD_PAYMENTID, paymentId);

        String refundId = null;
        String refundCode = null;
        String refundReceiptNo = null;
        String refundQsiResponseCode = null;
        String refundQsiResponseDesc = null;
        boolean success = false;

        try
        {
            CGPipe.setTraceOn();
            success = CGPipe.performTransaction();
            CGPipe.setTraceOff();
            String error = CGPipe.get("error_code_tag");
            String errortext = CGPipe.get("error_text");

            /*java.util.HashMap fileds = CGPipe.getResponseFields();
            Set<String> keys= fileds.keySet();

            for(String key: keys)
            {
                 log.debug("key="+key+",value="+fileds.get(key));
            }*/

            // Call was not successfull
            if (!success)
            {
                refundId = "";
                refundCode = "";
                refundReceiptNo = "";
                refundQsiResponseCode = "-1";
                refundQsiResponseDesc = CGPipe.getErrorText();
            }
            //call was successful but got error during capture
            else if (Functions.checkStringNull(error) != null)
            {
                refundId = "";
                refundCode = "";
                refundReceiptNo = "";
                refundQsiResponseCode = error;
                refundQsiResponseDesc = errortext;
            }
            else
            {
                if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                {
                    refundId = CGPipe.get(FIELD_TRANID);
                    refundCode = CGPipe.get(FIELD_AUTH);
                    refundReceiptNo = CGPipe.get(FIELD_REF);
                    refundQsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);
                    if ("00".equals(refundQsiResponseCode))
                    {
                        refundQsiResponseCode = "0";
                    }
                    refundQsiResponseDesc = CGPipe.get(FIELD_RESULT);

                    log.debug("refundId=" + refundId);
                    log.debug("refundCode=" + refundCode);
                    log.debug("refundReceiptNo=" + refundReceiptNo);
                    log.debug("refundQsiResponseCode =" + refundQsiResponseCode);
                    log.debug("refundQsiResponseDesc =" + refundQsiResponseDesc);
                }
                else
                {
                    log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                    throw new SystemError("There was an Error while refund. Please contact your System Administrator");
                } // End of else.

            }

        }
        catch (Exception e)
        {
            log.error("Error while posting data to Bank " + Util.getStackTrace(e));
            log.info("calling SendMAil for  Reversal Error");
            Mail.sendAdminMail("Error while Refund for trackingID-" + trackingID, e.getMessage());

            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }
        finally
        {
            writeToFile("\n Logging request for "+paymentId+"\n"+CGPipe.getTraceMessages(), request_log_path);
        }

        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashForRefund(refundId, refundCode, refundReceiptNo, refundQsiResponseCode, refundQsiResponseDesc);
        log.info("Leaving processRefund returning Hash " + returnHash);
        return returnHash;
    }


    public Hashtable getStatus(String trackingID, String accountId) throws SystemError
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOff();

        //
        // Populate the Plugin to perform the TranPortal version 1 transaction
        //
        CGPipe.setTransactionType(TRANS_TYPE_TranPortal);
        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());
        CGPipe.setTempDirectory(getTempResourcePath(account.getAliasName()));

        CGPipe.set(FIELD_ACTION, INQUIRY);

        CGPipe.set(FIELD_CURRENCYCODE, getCurrencyCode(account.getCurrency()));
        CGPipe.set(FIELD_TRACKID, trackingID);

        String id = null;
        String code = null;
        String receiptNo = null;
        String qsiResponseCode = null;
        String qsiResponseDesc = null;
        String status = null;
        boolean success = false;

        try
        {
            CGPipe.setTraceOn();
            success = CGPipe.performTransaction();
            CGPipe.setTraceOff();
            String error = CGPipe.get("error_code_tag");
            String errortext = CGPipe.get("error_text");

            if (!success)
            {
                id = "";
                code = "";
                receiptNo = "";
                qsiResponseCode = "-1";
                qsiResponseDesc = CGPipe.getErrorText();
                status = "Error";
            }
            //call was successful but got error during capture
            else if (Functions.checkStringNull(error) != null)
            {
                id = "";
                code = "";
                receiptNo = "";
                qsiResponseCode = error;
                qsiResponseDesc = errortext;
                status = "Error";
            }
            else
            {
                if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                {
                    id = CGPipe.get(FIELD_TRANID);
                    code = CGPipe.get(FIELD_AUTH);
                    receiptNo = CGPipe.get(FIELD_REF);
                    qsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);

                    if ("00".equals(qsiResponseCode))
                    {
                        qsiResponseCode = "0";
                    }
                    qsiResponseDesc = CGPipe.get(FIELD_RESULT);
                    status = qsiResponseDesc;

                    log.debug("id=" + id);
                    log.debug("code=" + code);
                    log.debug("receiptNo=" + receiptNo);
                    log.debug("qsiResponseDesc =" + qsiResponseDesc);
                    log.debug("qsiResponseCode =" + qsiResponseCode);

                }
                else
                {
                    log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                    throw new SystemError("There was an Error while gettinfg Details. Please contact your System Administrator");
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error while posting data to Bank " + Util.getStackTrace(e));
            log.info("calling SendMAil for Inquiry Error");
            Mail.sendAdminMail("Error while inquiry for trackingID-" + trackingID, e.getMessage());
            log.info("called SendMAil ");

            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }
        finally
        {
            writeToFile(CGPipe.getTraceMessages(), request_log_path);
        }

        /*log.debug("Amount : " + CGPipe.get(FIELD_AMT));
        log.debug("Type : " + CGPipe.get(FIELD_TYPE));
        log.debug("Result : " + CGPipe.get(FIELD_RESULT));
        log.debug("Member : " + CGPipe.get(FIELD_MEMBER));
        log.debug("Trans id. : " + CGPipe.get(FIELD_TRANID));
        log.debug("Auth : " + CGPipe.get(FIELD_AUTH));
        log.debug("Ref no. : " + CGPipe.get(FIELD_REF));

        log.debug("UDF. : " + CGPipe.get("udf1") + CGPipe.get("udf2"));
        log.debug("Debug : " + CGPipe.getTraceMessages());*/
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

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOff();

        CGPipe.setTransactionType(TRANS_TYPE_TranPortal);
        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());
        CGPipe.setTempDirectory(getTempResourcePath(account.getAliasName()));


        CGPipe.set(FIELD_ACTION, VOID_AUTH);

        CGPipe.set(FIELD_MEMBER, name);
        CGPipe.set(FIELD_TYPE, FIELD_TYPE_VALE);

        CGPipe.set(FIELD_AMT, amount);
        CGPipe.set(FIELD_CURRENCYCODE, getCurrencyCode(account.getCurrency()));
        CGPipe.set(FIELD_TRACKID, trackingID);
        CGPipe.set(FIELD_TRANSID, authId);


        String cancellationId = null;
        String cancellationCode = null;
        String cancellationReceiptNo = null;
        String cancellationQsiResponseCode = null;
        String cancellationQsiResponseDesc = null;
        boolean success = false;

        try
        {
            CGPipe.setTraceOn();
            success = CGPipe.performTransaction();
            CGPipe.setTraceOff();
            String error = CGPipe.get("error_code_tag");
            String errortext = CGPipe.get("error_text");

            // Call was not successfull
            if (!success)
            {
                cancellationId = "";
                cancellationCode = "";
                cancellationReceiptNo = "";
                cancellationQsiResponseCode = "-1";
                cancellationQsiResponseDesc = CGPipe.getErrorText();
            }
            //call was successful but got error during capture
            else if (Functions.checkStringNull(error) != null)
            {
                cancellationId = "";
                cancellationCode = "";
                cancellationReceiptNo = "";
                cancellationQsiResponseCode = error;
                cancellationQsiResponseDesc = errortext;
            }
            else
            {
                if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                {
                    cancellationId = CGPipe.get(FIELD_TRANID);
                    cancellationCode = CGPipe.get(FIELD_AUTH);
                    cancellationReceiptNo = CGPipe.get(FIELD_REF);
                    cancellationQsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);
                    if ("00".equals(cancellationQsiResponseCode))
                    {
                        cancellationQsiResponseCode = "0";
                    }
                    cancellationQsiResponseDesc = CGPipe.get(FIELD_RESULT);

                    log.debug("refundId=" + cancellationId);
                    log.debug("refundCode=" + cancellationCode);
                    log.debug("refundReceiptNo=" + cancellationReceiptNo);
                    log.debug("refundQsiResponseCode =" + cancellationQsiResponseCode);
                    log.debug("refundQsiResponseDesc =" + cancellationQsiResponseDesc);
                }
                else
                {
                    log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                    throw new SystemError("There was an Error while void Auth. Please contact your System Administrator");
                } // End of else.

            }
        }
        catch (Exception e)
        {
            log.error("Error while posting data to Bank " + Util.getStackTrace(e));
            Mail.sendAdminMail("Error while Refund for trackingID-" + trackingID, e.getMessage());

            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }
        finally
        {
            writeToFile(CGPipe.getTraceMessages(), request_log_path);
        }

        Hashtable returnHash = processResultHashForCancellation(cancellationId, cancellationCode, cancellationReceiptNo, cancellationQsiResponseCode, cancellationQsiResponseDesc);
        log.info("Leaving process VoidAuth returning Hash " + returnHash);
        return returnHash;
    }

    public Hashtable processVoidCapture(String trackingID, String accountId) throws SystemError
    {
        log.info("Entering process VoidCapture");
        String name = "", amount = "", captureId = "";//, ccNum = "", expMonth = "", expYear = "", ccid = "";
        Hashtable<String, String> transDetails = getTransactionDetails(trackingID);
        if (transDetails != null)
        {
            name = transDetails.get("name");
            captureId = transDetails.get("captureid");
            log.debug("Capture Id " + captureId);
            amount = transDetails.get("amount");

          /*  ccNum = Functions.decryptString(transDetails.get("ccnum"));
            String expDate = transDetails.get("expdate").trim();//e.g 05/2010
            int index = expDate.indexOf("/");
            expMonth = expDate.substring(0, index);
            expYear = expDate.substring(index + 1, expDate.length());
            ccid = transDetails.get("ccid");*/
        }
        else
        {
            throw new SystemError("Transaction Not found");
        }

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        UniversalPlugin CGPipe = new UniversalPlugin();
        CGPipe.setTraceOff();

        CGPipe.setTransactionType(TRANS_TYPE_TranPortal);
        CGPipe.setVersion(FIELD_VERSION);
        CGPipe.setResourcePath(getResourcePath(account.getAliasName()));
        CGPipe.setTerminalAlias(account.getAliasName());
        CGPipe.setTempDirectory(getTempResourcePath(account.getAliasName()));


        CGPipe.set(FIELD_ACTION, VOID_CAPTURE);

        CGPipe.set(FIELD_MEMBER, name);
        CGPipe.set(FIELD_TYPE, FIELD_TYPE_VALE);

        CGPipe.set(FIELD_AMT, amount);
        CGPipe.set(FIELD_CURRENCYCODE, getCurrencyCode(account.getCurrency()));
        CGPipe.set(FIELD_TRACKID, trackingID);
        CGPipe.set(FIELD_TRANSID, captureId);

        String cancellationId = null;
        String cancellationCode = null;
        String cancellationReceiptNo = null;
        String cancellationQsiResponseCode = null;
        String cancellationQsiResponseDesc = null;
        boolean success = false;

        try
        {
            CGPipe.setTraceOn();
            success = CGPipe.performTransaction();
            CGPipe.setTraceOff();
            String error = CGPipe.get("error_code_tag");
            String errortext = CGPipe.get("error_text");

            // Call was not successfull
            if (!success)
            {
                cancellationId = "";
                cancellationCode = "";
                cancellationReceiptNo = "";
                cancellationQsiResponseCode = "-1";
                cancellationQsiResponseDesc = CGPipe.getErrorText();
            }
            //call was successful but got error during capture
            else if (Functions.checkStringNull(error) != null)
            {
                cancellationId = "";
                cancellationCode = "";
                cancellationReceiptNo = "";
                cancellationQsiResponseCode = error;
                cancellationQsiResponseDesc = errortext;
            }
            else
            {
                if (trackingID.equals(CGPipe.get("trackid")))  //check whether we got result for same trackingid
                {
                    cancellationId = CGPipe.get(FIELD_TRANID);
                    cancellationCode = CGPipe.get(FIELD_AUTH);
                    cancellationReceiptNo = CGPipe.get(FIELD_REF);
                    cancellationQsiResponseCode = CGPipe.get(FIELD_RESPONSECODE);
                    if ("00".equals(cancellationQsiResponseCode))
                    {
                        cancellationQsiResponseCode = "0";
                    }
                    cancellationQsiResponseDesc = CGPipe.get(FIELD_RESULT);

                    log.debug("refundId=" + cancellationId);
                    log.debug("refundCode=" + cancellationCode);
                    log.debug("refundReceiptNo=" + cancellationReceiptNo);
                    log.debug("refundQsiResponseCode =" + cancellationQsiResponseCode);
                    log.debug("refundQsiResponseDesc =" + cancellationQsiResponseDesc);
                }
                else
                {
                    log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + CGPipe.get("trackid"));
                    throw new SystemError("There was an Error while void Auth. Please contact your System Administrator");
                } // End of else.

            }
        }
        catch (Exception e)
        {
            log.error("Error while posting data to Bank " + Util.getStackTrace(e));
            Mail.sendAdminMail("Error while Refund for trackingID-" + trackingID, e.getMessage());

            throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");
        }
        finally
        {
            writeToFile(CGPipe.getTraceMessages(), request_log_path);
        }

        Hashtable returnHash = processResultHashForCancellation(cancellationId, cancellationCode, cancellationReceiptNo, cancellationQsiResponseCode, cancellationQsiResponseDesc);
        log.info("Leaving processVoidCapture returning Hash " + returnHash);
        return returnHash;
    }

    public Hashtable processResultHashforVEReq(String paymentId, String avr, String cvv2Response, String acsURL, String PAReq)
    {

        Hashtable returnHash = new Hashtable();
        if (Functions.parseData(paymentId) != null)
            returnHash.put("paymentid", paymentId);
        else
            returnHash.put("paymentid", "Could not retrive");

        if (Functions.parseData(avr) != null)
            returnHash.put("avr", avr);
        else
            returnHash.put("avr", "Could not retrive");

        if (Functions.parseData(cvv2Response) != null)
            returnHash.put("cvv2response", cvv2Response);
        else
            returnHash.put("cvv2response", "Could not retrive");

        if (Functions.parseData(acsURL) != null)
            returnHash.put("acsurl", acsURL);
        if (Functions.parseData(PAReq) != null)
            returnHash.put("PAReq", PAReq);

        return returnHash;

    }

    public Hashtable processResultHashforPAReq(String paymentId, String avr, String cvv2Response, String PARes)
    {

        Hashtable returnHash = new Hashtable();
        if (Functions.parseData(paymentId) != null)
            returnHash.put("paymentid", paymentId);
        else
            returnHash.put("paymentid", "Could not retrive");

        if (Functions.parseData(avr) != null)
            returnHash.put("avr", avr);
        else
            returnHash.put("avr", "Could not retrive");

        if (Functions.parseData(cvv2Response) != null)
            returnHash.put("cvv2response", cvv2Response);
        else
            returnHash.put("cvv2response", "Could not retrive");

        /*if (Functions.parseData(PARes) != null)
            returnHash.put("PARes", PARes);
*/
        return returnHash;

    }

    public String getTempResourcePath(String aliasName)
        {
            if (FILE_SEPARATOR != null && !FILE_SEPARATOR.equals((RESOURCE_TEMP_PATH.substring(RESOURCE_TEMP_PATH.length() - 1, RESOURCE_TEMP_PATH.length()))))
                RESOURCE_TEMP_PATH = RESOURCE_TEMP_PATH + FILE_SEPARATOR;

            log.info("Returning resourse path as "+RESOURCE_TEMP_PATH + aliasName + FILE_SEPARATOR);
            return RESOURCE_TEMP_PATH + aliasName + FILE_SEPARATOR;

            //return RESOURCE_TEMP_PATH+ FILE_SEPARATOR;
        }

    public static void main(String[] args) throws Exception
    {
        //System.out.println("Status got " + new ACIUniversalKitHandler().processVoidAuth("31279", "2"));
        log.debug("Status got " + new ACIUniversalKitHandler().processVoidAuth("31279", "2"));
    }
}



