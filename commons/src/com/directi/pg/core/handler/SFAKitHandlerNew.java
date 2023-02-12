package com.directi.pg.core.handler;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Mail;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.Util;
import com.opus.epg.sfa.java.*;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Mar 2, 2007
 * Time: 12:07:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SFAKitHandlerNew extends AbstractKitHandler
{
    private static Logger log = new Logger(SFAKitHandlerNew.class.getName());
    private final static String AUTH = "req.Preauthorization";
    private final static String CAPTURE = "req.Authorization";
    private final static String REFUND = "req.Refund";
    private final static String GMT_OFFSET = "GMT+05:30";
    private final static String RESPONSE_METHOD = "POST";
    private final static String IP_ADDRESS = "";


    public SFAKitHandlerNew()
    {

    }

    public Hashtable processAuthentication(String trackingID, String transAmount, Hashtable cardDetailHash, Hashtable billingAddrHash, Hashtable shippingAddrHash, Hashtable MPIDataHash, String ipaddress, String accountID) throws SystemError
    {
        log.info("Inside processAuthentication");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        //In case of VBV we may have another merchant,in new version
        //there is no separate merchantid for vbv so I will mostly get as null

        //If we get icicimerchantID as a parameter then it will override merchantid for memberid passed in constructor
        String merchantId = account.getMerchantId();
        String currency = account.getCurrency();
        String ccNum, ccType, IP;
        if (Functions.parseData(ipaddress) != null)
            IP = ipaddress;
        else
            IP = IP_ADDRESS;

        //#################   set all SFA objects #########

        Merchant oMerchant = null;
        BillToAddress oBTA = null;
        ShipToAddress oSTA = null; //We are not accepting it
        MPIData oMPI = null;
        CardInfo oCI = null;

        oMerchant = new Merchant();

        oMerchant.setMerchantDetails(
                merchantId         //merchantid
                , null              //vendorid
                , null              //partnerid
                , IP
                , trackingID
                , null                  //Presently not sending orderid
                , null                 //For MOTO url is not response required
                , RESPONSE_METHOD
                , currency
                , null               //Presently not sending invoice number
                , AUTH      //message type(req.Preauthorization,req.Sale)
                , transAmount
                , GMT_OFFSET
                , null           //Not sending any extra parameters
                , null
                , null
                , null
                , null
        );


        if (cardDetailHash != null)
        {
            ccNum = (String) cardDetailHash.get("ccnum");
            ccType = Functions.getCardType(ccNum);
            String paymentMode = (String) cardDetailHash.get("mode");
            if (Functions.parseData(paymentMode) == null)
                paymentMode = "CREDI";

            oCI = new CardInfo();
            oCI.setCardDetails(
                    ccType
                    , ccNum
                    , (String) cardDetailHash.get("ccid")
                    , (String) cardDetailHash.get("year")
                    , (String) cardDetailHash.get("month")
                    , (String) cardDetailHash.get("name")
                    , paymentMode
            );
        }
        else
            throw new SystemError("#1234#Card details are Missing");

        if (billingAddrHash != null)
        {
            oBTA = new BillToAddress();
            oBTA.setAddressDetails(
                    null             //CustomerID not defined presently
                    , null            //Customer name not defined presently
                    , (String) billingAddrHash.get("address")
                    , null            //Address line 2
                    , null            //Address line 3
                    , (String) billingAddrHash.get("city")
                    , (String) billingAddrHash.get("state")
                    , (String) billingAddrHash.get("zip")
                    , (String) billingAddrHash.get("ccode")
                    , (String) billingAddrHash.get("emailaddr")
            );
        }
        if (shippingAddrHash != null)
        {
            oSTA = new ShipToAddress();
            oSTA.setAddressDetails(
                    (String) shippingAddrHash.get("address")
                    , null        //Address line 2
                    , null        //Address line 3
                    , (String) shippingAddrHash.get("city")
                    , (String) shippingAddrHash.get("state")
                    , (String) shippingAddrHash.get("zip")
                    , (String) shippingAddrHash.get("ccode")
                    , (String) shippingAddrHash.get("emailaddr")
            );
        }
        if (MPIDataHash != null)
        {
            oMPI = new MPIData();
            oMPI.setMPIResponseDetails(
                    (String) MPIDataHash.get("eci")
                    , (String) MPIDataHash.get("xid")
                    , (String) MPIDataHash.get("vbvstatus")
                    , (String) MPIDataHash.get("cavv")
                    , (String) MPIDataHash.get("shoppingcontext")
                    , (String) MPIDataHash.get("PurchaseAmount")
                    , (String) MPIDataHash.get("Currency")

            );
        }
        else    //MPI data not available
            oMPI = new MPIData();

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
                log.info("Inside authenticate");
                PGResponse oPgResp = null;

                try
                {
                    PostLib oPostLib = new PostLib();

                    try
                    {
                        oPgResp = oPostLib.postMOTO(oBTA, oSTA, oMerchant, oMPI, oCI);
                    }
                    catch (Exception e)
                    {
                        log.info("calling SendMAil for Authentication Error");
                        Mail.sendAdminMail("Error while authentication for trackingID-" + trackingID, e.getMessage());
                        log.info("called SendMAil");

                    }

                    if (trackingID.equals(oPgResp.getTxnId()))  //check whether we got result for same trackingid
                    {
                        authId = oPgResp.getEpgTxnId();
                        authCode = oPgResp.getAuthIdCode();

                        authReceiptNo = oPgResp.getRRN();
                        authQsiResponseCode = oPgResp.getRespCode();
                        authQsiResponseDesc = oPgResp.getRespMessage();

                        log.debug("AuthId=" + authId);
                        log.debug("AuthCode=" + authCode);
                        log.debug("authReceiptNo =" + authReceiptNo);
                        log.debug("authQsiResponseCode=" + authQsiResponseCode);
                        log.debug("authQsiResponseDesc=" + authQsiResponseDesc);


                    } // End of if
                    else
                    {
                        log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + oPgResp.getTxnId());
                        throw new SystemError("There was an Error while authentication. Please contact your System Administrator");
                    } // End of else.

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
        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashforAuth(authId, authCode, authReceiptNo, authQsiResponseCode, authQsiResponseDesc);
        log.info("Leaving processAuthentication returning Hash " + returnHash);
        ccNum=null;
        return returnHash;
    }


    public Hashtable processCapture(String trackingID, String captureAmount, String authId, String authCode, String authRRN, String accountID)
    {

        log.info("Inside newProcessCapture");

        //In case of VBV we may have another merchant,in new version
        //there is no separate merchantid for vbv so I will mostly get as null

        //If we get icicimerchantID as a parameter then it will override merchantid for memberid passed in constructor
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        Merchant oMerchant = null;
        BillToAddress oBTA = null;
        ShipToAddress oSTA = null; //We are not accepting it
        MPIData oMPI = null;
        CardInfo oCI = null;
        String merchantId = account.getMerchantId();
        String currency = account.getCurrency();
        oMerchant = new Merchant();
        oMerchant.setMerchantRelatedTxnDetails(
                merchantId         //merchantid
                , null              //vendorid
                , null              //partnerid
                , trackingID
                , authId
                , authRRN
                , authCode
                , null
                , RESPONSE_METHOD            //POST/GET
                , currency
                , CAPTURE               //req.Authorisation
                , captureAmount          //In Rs.
                , GMT_OFFSET
                , null           //Not sending any extra parameters
                , null
                , null
                , null
                , null
        );

        String captureId = null;
        String captureCode = null;
        String captureReceiptNo = null;

        String captureQsiResponseCode = null;
        String captureQsiResponseDesc = null;

        try
        {
            log.info("Inside newCapture");
            PGResponse oPgResp = null;

            try
            {
                PostLib oPostLib = new PostLib();

                try
                {
                    oPgResp = oPostLib.postRelatedTxn(oMerchant);
                }
                catch (Exception e)
                {
                    throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");

                }

                if (trackingID.equals(oPgResp.getTxnId()))  //check whether we got result for same trackingid
                {


                    captureId = oPgResp.getEpgTxnId();
                    captureCode = oPgResp.getAuthIdCode();
                    captureReceiptNo = oPgResp.getRRN();
                    captureQsiResponseCode = oPgResp.getRespCode();
                    captureQsiResponseDesc = oPgResp.getRespMessage();

                    log.debug("CaptureId=" + captureId);
                    log.debug("CaptureCode=" + captureCode);
                    log.debug("captureReceiptNo=" + captureReceiptNo);
                    log.debug("captureQsiResponseCode =" + captureQsiResponseCode);
                    log.debug("captureQsiResponseDesc =" + captureQsiResponseDesc);


                } // End of if
                else
                {
                    log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + oPgResp.getTxnId());
                    throw new SystemError("There was an Error while capture. Please contact your System Administrator");
                } // End of else.

            }
            catch (Exception e)
            {
                log.info("Error while posting data to Bank " + e.getMessage());
                throw new SystemError("#1234#" + e.getMessage());
            }

            if (captureQsiResponseCode == null || !captureQsiResponseCode.equals("0"))
            {
                throw new SystemError("#1234# There was an error while capture : " + captureQsiResponseDesc);
            }

            log.info("Leaving newCapture");
        }

        catch (SystemError se)
        {
            log.error("Exception while capture in newProcesscapture " , se);
            captureId = "";
        }

        //Fill the result in hashtable to return to client

        Hashtable returnHash = processResultHashforCapture(captureId, captureCode, captureReceiptNo, captureQsiResponseCode, captureQsiResponseDesc);
        log.info("Leaving newProcessCapture returning Hash " + returnHash);
        return returnHash;
    }


    public Hashtable processRefund(String trackingID, String refundAmount, String captureId, String captureCode, String captureRRN, String accountID) throws SystemError
    {
        log.info("Inside processRefund");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountID);
        //In case of VBV we may have another merchant,in new version
        //there is no separate merchantid for vbv so I will mostly get as null

        //If we get icicimerchantID as a parameter then it will override merchantid for memberid passed in constructor
        Merchant oMerchant = null;
        BillToAddress oBTA = null;
        ShipToAddress oSTA = null; //We are not accepting it
        MPIData oMPI = null;
        CardInfo oCI = null;
        String merchantId = account.getMerchantId();
        String currency = account.getCurrency();
        oMerchant = new Merchant();

        oMerchant.setMerchantRelatedTxnDetails(
                merchantId         //merchantid
                , null              //vendorid
                , null              //partnerid
                , trackingID
                , captureId
                , captureRRN
                , captureCode
                , null
                , RESPONSE_METHOD            //POST/GET
                , currency
                , REFUND               //req.Authorisation
                , refundAmount          //In Rs.
                , GMT_OFFSET
                , null           //Not sending any extra parameters
                , null
                , null
                , null
                , null
        );


        log.info("Inside refund");

        PGResponse oPgResp = null;
        String refundId = null;
        String refundCode = null;
        String refundReceiptNo = null;

        String refundQsiResponseCode = null;
        String refundQsiResponseDesc = null;


        try
        {
            PostLib oPostLib = new PostLib();

            try
            {
                oPgResp = oPostLib.postRelatedTxn(oMerchant);
            }
            catch (Exception e)
            {
                throw new SystemError("There was an Error while posting data to bank. Please contact your merchant.");

            }

            if (trackingID.equals(oPgResp.getTxnId()))  //check whether we got result for same trackingid
            {
                refundId = oPgResp.getEpgTxnId();
                refundCode = oPgResp.getAuthIdCode();
                refundReceiptNo = oPgResp.getRRN();
                refundQsiResponseCode = oPgResp.getRespCode();
                refundQsiResponseDesc = oPgResp.getRespMessage();

                log.debug("refundId=" + refundId);
                log.debug("refundCode=" + refundCode);
                log.debug("refundReceiptNo=" + refundReceiptNo);
                log.debug("refundQsiResponseCode =" + refundQsiResponseCode);
                log.debug("refundQsiResponseDesc =" + refundQsiResponseDesc);

            } // End of if
            else
            {
                log.info("Tracking ID we have=" + trackingID + " tracking id we got=" + oPgResp.getTxnId());
                throw new SystemError("There was an Error while refund. Please contact your System Administrator");
            } // End of else.


            if (refundQsiResponseCode == null || !refundQsiResponseCode.equals("0"))
            {
                throw new SystemError("#1234# There was an error while refund : " + refundQsiResponseDesc);
            }

        }
        catch (Exception e)
        {
            log.info("Error while posting data to Bank " + e.getMessage());
            throw new SystemError("#1234#" + e.getMessage());
        }

        log.info("Leaving refund");

        //Fill the result in hashtable to return to client
        Hashtable returnHash = processResultHashForRefund(refundId, refundCode, refundReceiptNo, refundQsiResponseCode, refundQsiResponseDesc);


        log.info("Leaving processRefund returning Hash " + returnHash);
        return returnHash;


    }

    public Hashtable getStatus(String trackingID, String accountId) throws SystemError
    {
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        Merchant merchant = new Merchant();
        merchant.setMerchantOnlineInquiry(merchantId, trackingID);
        PostLib postLib = null;
        PGSearchResponse oPgSearchResp = null;
        try
        {
            postLib = new PostLib();
            oPgSearchResp = postLib.postStatusInquiry(merchant);
        }
        catch (Exception e)
        {
            throw new SystemError("Get Status failed : " + Util.getStackTrace(e));
        }

        String id = "";
        String code = "";
        String receiptNo = "";
        String transType = "";
        String status = "";
        String qsiResponseCode = "";
        String qsiResponseDesc = "";

        Hashtable statusDetails;

        ArrayList oPgRespArr = oPgSearchResp.getPGResponseObjects();
        if (oPgRespArr != null)
        {
            //System.out.println("Size " + oPgRespArr.size());
            for (int i = 0; i < oPgRespArr.size(); i++)
            //First row will be always for preAuthorisation
            // we will always fetch the last status of the transaction to be returned
            {
               // System.out.println("Looping ");
                PGResponse oPgResp = (PGResponse) oPgRespArr.get(i);
                //System.out.println(" response object" + oPgResp);
                id = oPgResp.getEpgTxnId();
                code = oPgResp.getAuthIdCode();
                receiptNo = oPgResp.getRRN();
                transType = oPgResp.getTxnType();
                status = mapTxnTypeToStatus(transType);
                qsiResponseCode = oPgResp.getRespCode();
                qsiResponseDesc = oPgResp.getRespMessage();
                log.debug("transaction type=" + transType + " Response code=" + qsiResponseCode + " Response esc " + qsiResponseDesc);
            }
            statusDetails = processHashForGetDetails(trackingID, status, id, code, receiptNo, qsiResponseCode, qsiResponseDesc);
            log.debug("Stauts details " + statusDetails);
        }
        else
        {
            throw new SystemError("Error while getting status dats ");
        }

        return statusDetails;
    }

    private String mapTxnTypeToStatus(String transType)
    {
        if ("PreAuth".equalsIgnoreCase(transType))
        {
            return "approved";
        }
        else if ("Auth".equalsIgnoreCase(transType))
        {
            return "captured";
        }
        else
        {
            return transType;
        }
    }

    public Hashtable processVoidAuth(String trackingID, String accountId) throws SystemError
    {
        // No need to implemnt since all transactiosn if not capured are auto cancelled at end of 30 days
        // just return the cancellation details with 0 as qsiresponsedesc
        return processResultHashForCancellation("NONE", "NONE", "NONE", "0", "Auto Cancelled ");
    }

    public Hashtable processVoidCapture(String trackingID, String accountId) throws SystemError
    {
        return processResultHashForCancellation("NONE", "NONE", "NONE", "0", "Auto Cancelled ");
    }


    public static void main(String[] args) throws Exception
    {
        //System.out.println(" Result " + new SFAKitHandlerNew().getStatus("117407", "1"));
        log.debug(" Result " + new SFAKitHandlerNew().getStatus("117407", "1"));

    }

}

