package com.payment.payGateway.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.directi.pg.Logger;
import com.payment.payGateway.core.PaygatewayPaymentGateway;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh Dani
 * Date: 12/19/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalculateSHA256
{
    private static Logger logger=new Logger(CalculateSHA256.class.getName());

    public static String processTxSHA256()
    {

        String passphrese = "CcE98C8968B6F67C1aBbc26D7a9bac316";
        String trans_amt = "10.00";
        String accountid = "pz_paygateway";
        String email = "jinesh.d@pz.com";
        String cardno = "4444333322221111";
        String ip = "122.169.97.70";

        String sha = passphrese + trans_amt + accountid + email + cardno + ip;

        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
            md.update(sha.getBytes());
            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++)
            {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            //System.out.println("Hex format : " + sb.toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException--->",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        return sha;
    }

    public static String processRefundSHA256()
    {
        String passphrese = "CcE98C8968B6F67C1aBbc26D7a9bac316";
        String accountid = "pz_paygateway";
        String trasid = "34554";

        String sha = passphrese + accountid + trasid;
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
            md.update(sha.getBytes());
            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++)
            {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            //System.out.println("Hex format : " + sb.toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException--->",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        return sha;
    }

    public static String processAdjestSHA256()
    {
        String passphrese = "CcE98C8968B6F67C1aBbc26D7a9bac316";
        String accountid = "pz_paygateway";
        String trasid = "34554";

        String sha = passphrese + accountid + trasid;
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
            md.update(sha.getBytes());
            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++)
            {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            //System.out.println("Hex format : " + sb.toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("IllegalArgumentException--->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        return sha;
    }

    public static void main(String[] args)throws Exception
    {
        processTxSHA256();
        processRefundSHA256();
        processAdjestSHA256();
    }
}
