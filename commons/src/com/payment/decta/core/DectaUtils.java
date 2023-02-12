package com.payment.decta.core;

import com.directi.pg.Logger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Trupti on 5/19/2017.
 */
public class DectaUtils
{
    private static Logger log = new Logger(DectaUtils.class.getName());

    public static String doPostHTTPSURLConnection(String strURL, Map<String,String> map) throws PZTechnicalViolationException
    {
        String result = "";
        BufferedReader in=null;
        BufferedOutputStream out = null;
        NameValuePair[] data = null;

        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();

            data = getNamedValuePairArray(map);
            log.error("Request Data decta---"+data+"---POST Url---"+strURL);
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.setRequestBody(data);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            log.error("Response from decta---" + response);
            result= response;

        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("DectaUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());

        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("DectaUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static NameValuePair[] getNamedValuePairArray(Map<String,String> mapInternal)
    {
        NameValuePair result[] = new NameValuePair[mapInternal.size()];
        int counter = 0;
        for (Iterator iterator = mapInternal.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            result[counter++] = new NameValuePair(key, (String) mapInternal.get(key));
        }
        return result;
    }

    public String getResultDescriptionFromCode(String errorCode)
    {
        HashMap hashMap = new HashMap();
        String resDescription = "";

        hashMap.put("000","Approved");
        hashMap.put("001","Approved, honour with identification");
        hashMap.put("002","Approved for partial amount");
        hashMap.put("003","Approved for VIP");
        hashMap.put("004","Approved, update track 3");
        hashMap.put("005","Approved, account type specified by card issuer");
        hashMap.put("006","Approved for partial amount, account type specified by card issuer");
        hashMap.put("007","Approved, update ICC");
        hashMap.put("100","Decline (general, no comments)");
        hashMap.put("101","Decline, expired card");
        hashMap.put("102","Decline, suspected fraud");
        hashMap.put("103","Decline, card acceptor contact acquirer");
        hashMap.put("104","Decline, restricted card");
        hashMap.put("105","Decline, card acceptor call acquirer's security department");
        hashMap.put("106","Decline, allowable PIN tries exceeded");
        hashMap.put("107","Decline, refer to card issuer");
        hashMap.put("108","Decline, refer to card issuer's special conditions");
        hashMap.put("109","Decline, invalid merchant");
        hashMap.put("110","Decline, invalid amount");
        hashMap.put("111","Decline, invalid card number");
        hashMap.put("112","Decline, PIN data required");
        hashMap.put("113","Decline, unacceptable fee");
        hashMap.put("114","Decline, no account of type requested");
        hashMap.put("115","Decline, requested function not supported");
        hashMap.put("116","Decline, not sufficient funds");
        hashMap.put("117","Decline, incorrect PIN");
        hashMap.put("118","Decline, no card record");
        hashMap.put("119","Decline, transaction not permitted to cardholder");
        hashMap.put("120","Decline, transaction not permitted to terminal");
        hashMap.put("121","Decline, exceeds withdrawal amount limit");
        hashMap.put("122","Decline, security violation");
        hashMap.put("123","Decline, exceeds withdrawal frequency limit");
        hashMap.put("124","Decline, violation of law");
        hashMap.put("125","Decline, card not effective");
        hashMap.put("126","Decline, invalid PIN block");
        hashMap.put("127","Decline, PIN length error");
        hashMap.put("128","Decline, PIN kay synch error");
        hashMap.put("129","Decline, suspected counterfeit card");
        hashMap.put("180","Decline, by cardholders wish");
        hashMap.put("200","Pick-up (general, no comments)");
        hashMap.put("201","Pick-up, expired card");
        hashMap.put("202","Pick-up, suspected fraud");
        hashMap.put("203","Pick-up, card acceptor contact card acquirer");
        hashMap.put("204","Pick-up, restricted card");
        hashMap.put("205","Pick-up, card acceptor call acquirer's security department");
        hashMap.put("206","Pick-up, allowable PIN tries exceeded");
        hashMap.put("207","Pick-up, special conditions");
        hashMap.put("208","Pick-up, lost card");
        hashMap.put("209","Pick-up, stolen card");
        hashMap.put("210","Pick-up, suspected counterfeit card");
        hashMap.put("300","Status message: file action successful");
        hashMap.put("301","Status message: file action not supported by receiver");
        hashMap.put("302","Status message: unable to locate record on file");
        hashMap.put("303","Status message: duplicate record, old record replaced");
        hashMap.put("304","Status message: file record field edit error");
        hashMap.put("305","Status message: file locked out");
        hashMap.put("306","Status message: file action not successful");
        hashMap.put("307","Status message: file data format error");
        hashMap.put("308","Status message: duplicate record, new record rejected");
        hashMap.put("309","Status message: unknown file");
        hashMap.put("400","Accepted (for reversal)");
        hashMap.put("499","Approved, no original message data");
        hashMap.put("500","Status message: reconciled, in balance");
        hashMap.put("501","Status message: reconciled, out of balance");
        hashMap.put("502","Status message: amount not reconciled, totals provided");
        hashMap.put("503","Status message: totals for reconciliation not available");
        hashMap.put("504","Status message: not reconciled, totals provided");
        hashMap.put("600","Accepted (for administrative info)");
        hashMap.put("601","Status message: impossible to trace back original transaction");
        hashMap.put("602","Status message: invalid transaction reference number");
        hashMap.put("603","Status message: reference number/PAN incompatible");
        hashMap.put("604","Status message: POS photograph is not available");
        hashMap.put("605","Status message: requested item supplied");
        hashMap.put("606","Status message: request cannot be fulfilled - required documentation is not availabe");
        hashMap.put("680","List ready");
        hashMap.put("681","List not ready");
        hashMap.put("700","Accepted (for fee collection)");
        hashMap.put("800","Accepted (for network management)");
        hashMap.put("900","Advice acknowledged, no financial liability accepted");
        hashMap.put("901","Advice acknowledged, financial liability accepted");
        hashMap.put("902","Decline reason message: invalid transaction");
        hashMap.put("903","Status message: re-enter transaction");
        hashMap.put("904","Decline reason message: format error");
        hashMap.put("905","Decline reason message: acqiurer not supported by switch");
        hashMap.put("906","Decline reason message: cutover in process");
        hashMap.put("907","Decline reason message: card issuer or switch inoperative");
        hashMap.put("908","Decline reason message: transaction destination cannot be found for routing");
        hashMap.put("909","Decline reason message: system malfunction");
        hashMap.put("910","Decline reason message: card issuer signed off");
        hashMap.put("911","Decline reason message: card issuer timed out");
        hashMap.put("912","Decline reason message: card issuer unavailable");
        hashMap.put("913","Decline reason message: duplicate transmission");
        hashMap.put("914","Decline reason message: not able to trace back to original transaction");
        hashMap.put("915","Decline reason message: reconciliation cutover or checkpoint error");
        hashMap.put("916","Decline reason message: MAC incorrect");
        hashMap.put("917","Decline reason message: MAC key sync error");
        hashMap.put("918","Decline reason message: no communication keys available for use");
        hashMap.put("919","Decline reason message: encryption key sync error");
        hashMap.put("920","Decline reason message: security software/hardware error - try again");
        hashMap.put("921","Decline reason message: security software/hardware error - no action");
        hashMap.put("922","Decline reason message: message number out of sequence");
        hashMap.put("923","Status message: request in progress");
        hashMap.put("950","Decline reason message: violation of business arrangement");

        resDescription = (String)hashMap.get(errorCode);
        return resDescription;
    }

    public static String convertSha1(String password) throws NoSuchAlgorithmException
    {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }


}
