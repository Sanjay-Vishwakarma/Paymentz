package com.directi.pg.core.handler;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.ICICIPaymentGateway;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Mar 1, 2007
 * Time: 3:24:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractKitHandler
{

    private static Logger log = new Logger(AbstractKitHandler.class.getName());

    public abstract Hashtable processAuthentication(String trackingID, String transAmount, Hashtable cardDetailHash,
                                                    Hashtable billingAddrHash, Hashtable shippingAddrHash, Hashtable MPIDataHash,
                                                    String ipaddress, String accountId) throws SystemError;


    public abstract Hashtable processCapture(String trackingID, String captureAmount, String authId, String authCode,
                                             String authRRN, String accountId) throws SystemError;


    public abstract Hashtable processRefund(String trackingID, String refundAmount, String captureId, String captureCode,
                                            String captureRRN, String accountId) throws SystemError;

    public abstract Hashtable getStatus(String id, String accountId) throws SystemError;

    public abstract Hashtable processVoidAuth(String trackingID, String accountId) throws SystemError;

    public abstract Hashtable processVoidCapture(String trackingID, String accountId) throws SystemError;

    public Hashtable processVerifyEnrollment(String trackingID, String transAmount, Hashtable cardDetailHash, Hashtable billingAddrHash, String accountID) throws SystemError
    {
        throw new SystemError("This gateway can't process VEReq");
    }

    public Hashtable processPayerAuthentication(String trackingID, String PARes, String paymentId,String accountID) throws SystemError
    {
        throw new SystemError("This gateway can't process PAReq");
    }

    public static AbstractKitHandler getHandler(String gatewayName) throws SystemError
    {
        if (SBMPaymentGateway.GATEWAY_TYPE.equals(gatewayName))
        {
            //return new ACIKitHandler();
            return new ACIUniversalKitHandler();

        }
        
        else if (ICICIPaymentGateway.GATEWAY_TYPE_NEW.equals(gatewayName))
        {
            return new SFAKitHandlerNew();
        }

        else
        {
            throw new SystemError("Couldn't find a Valid Gateway Handler");
        }

    }

    public Hashtable processResultHashforAuth(String authId, String authCode, String authReceiptNo, String authQsiResponseCode, String authQsiResponseDesc)
    {
        Hashtable returnHash = new Hashtable();
        if (Functions.parseData(authId) != null)
            returnHash.put("authid", authId);
        else
            returnHash.put("authid", "");

        if (Functions.parseData(authCode) != null)
            returnHash.put("authcode", authCode);
        else
            returnHash.put("authcode", "Could not retrive");

        if (Functions.parseData(authReceiptNo) != null)
            returnHash.put("authreceiptno", authReceiptNo);
        else
            returnHash.put("authreceiptno", "Could not retrive");

        if (Functions.parseData(authQsiResponseCode) != null)
            returnHash.put("authqsiresponsecode", authQsiResponseCode);
        else
            returnHash.put("authqsiresponsecode", "Could not retrive");

        if (Functions.parseData(authQsiResponseDesc) != null)
            returnHash.put("authqsiresponsedesc", authQsiResponseDesc);
        else
            returnHash.put("authqsiresponsedesc", "Could not retrive");
        return returnHash;
    }

    public Hashtable processResultHashforCapture(String captureId, String captureCode, String captureReceiptNo, String captureQsiResponseCode, String captureQsiResponseDesc)
    {
        Hashtable returnHash = new Hashtable();
        returnHash.put("captureid", captureId);//Not checking for null as it must be there else let the error be thrown
        if (Functions.parseData(captureCode) != null)
            returnHash.put("capturecode", captureCode);
        else
            returnHash.put("capturecode", "Could not retrive");

        if (Functions.parseData(captureReceiptNo) != null)
            returnHash.put("capturereceiptno", captureReceiptNo);
        else
            returnHash.put("capturereceiptno", "Could not retrive");

        if (Functions.parseData(captureQsiResponseCode) != null)
            returnHash.put("captureqsiresponsecode", captureQsiResponseCode);
        else
            returnHash.put("captureqsiresponsecode", "Could not retrive");

        if (Functions.parseData(captureQsiResponseDesc) != null)
            returnHash.put("captureqsiresponsedesc", captureQsiResponseDesc);
        else
            returnHash.put("captureqsiresponsedesc", "Could not retrive");
        return returnHash;
    }

    public Hashtable processResultHashForRefund(String refundId, String refundCode, String refundReceiptNo, String refundQsiResponseCode, String refundQsiResponseDesc)
    {
        Hashtable returnHash = new Hashtable();

        returnHash.put("refundid", refundId);//Not checking for null as it must be there else let the error be thrown

        if (Functions.parseData(refundCode) != null)
            returnHash.put("refundcode", refundCode);
        else
            returnHash.put("refundcode", "Could not retrive");

        if (Functions.parseData(refundReceiptNo) != null)
            returnHash.put("refundreceiptno", refundReceiptNo);
        else
            returnHash.put("refundreceiptno", "Could not retrive");

        if (Functions.parseData(refundQsiResponseCode) != null)
            returnHash.put("refundqsiresponsecode", refundQsiResponseCode);
        else
            returnHash.put("refundqsiresponsecode", "Could not retrive");

        if (Functions.parseData(refundQsiResponseDesc) != null)
            returnHash.put("refundqsiresponsedesc", refundQsiResponseDesc);
        else
            returnHash.put("refundqsiresponsedesc", "Could not retrive");
        return returnHash;
    }


    public Hashtable processHashForGetDetails(String trackingId, String status, String id, String code, String receiptNo, String qsiResponseCode, String qsiResponseDesc)
    {
        Hashtable returnHash = new Hashtable();

        returnHash.put("id", id);//Not checking for null as it must be there else let the error be thrown
        returnHash.put("status", status);
        returnHash.put("trackingid", trackingId);

        if (Functions.parseData(code) != null)
            returnHash.put("code", code);
        else
            returnHash.put("code", "Could not retrive");

        if (Functions.parseData(receiptNo) != null)
            returnHash.put("receiptno", receiptNo);
        else
            returnHash.put("receiptno", "Could not retrive");

        if (Functions.parseData(qsiResponseCode) != null)
            returnHash.put("qsiresponsecode", qsiResponseCode);
        else
            returnHash.put("qsiresponsecode", "Could not retrive");

        if (Functions.parseData(qsiResponseDesc) != null)
            returnHash.put("qsiresponsedesc", qsiResponseDesc);
        else
            returnHash.put("qsiresponsedesc", "Could not retrive");
        return returnHash;
    }

    public Hashtable processResultHashForCancellation(String cancellationId, String cancellationCode, String cancellationReceiptNo, String cancellationQsiResponseCode, String cancellationQsiResponseDesc)
    {
        Hashtable returnHash = new Hashtable();

        returnHash.put("cancellationid", cancellationId);//Not checking for null as it must be there else let the error be thrown

        if (Functions.parseData(cancellationCode) != null)
            returnHash.put("cancellationcode", cancellationCode);
        else
            returnHash.put("cancellationcode", "Could not retrive");

        if (Functions.parseData(cancellationReceiptNo) != null)
            returnHash.put("cancellationreceiptno", cancellationReceiptNo);
        else
            returnHash.put("cancellationreceiptno", "Could not retrive");

        if (Functions.parseData(cancellationQsiResponseCode) != null)
            returnHash.put("cancellationqsiresponsecode", cancellationQsiResponseCode);
        else
            returnHash.put("cancellationqsiresponsecode", "Could not retrive");

        if (Functions.parseData(cancellationQsiResponseDesc) != null)
            returnHash.put("cancellationqsiresponsedesc", cancellationQsiResponseDesc);
        else
            returnHash.put("cancellationqsiresponsedesc", "Could not retrive");
        return returnHash;
    }


}
