package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.ESAPI;

/**
 * Created by IntelliJ IDEA.
 * User: alpesh.s
 * Date: Aug 27, 2005
 * Time: 1:35:14 PM
 */

public class AutoReverse
{
    private static Logger log = new Logger(AutoReverse.class.getName());
    static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Servlet");

    public static void main(String[] arg)
    {
        try
        {
            String icicitransid = null;
            if (arg.length > 0)
                icicitransid = arg[0];

            reverse(icicitransid);
        }
        catch (Exception e)
        {
            log.error("Error in Main " ,e);
        }

    }

    public static void reverse(String icicitransId)
    {
        Connection conn = null;
        //Added for reversal mail
        HashMap<String,Collection> membersMap = new HashMap<String,Collection>();

        Collection<Hashtable> listOfRefunds = null;
        Transaction transaction = new Transaction();
        Hashtable refundDetails = null;
        try
        {
            conn = Database.getConnection();
             Codec ME = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            //select all transactions who are on capturestarted since last 10 minutes
            StringBuffer selectquery = new StringBuffer("select toid,icicitransid,captureid,capturecode,capturereceiptno,refundamount,icicimerchantid,accountid,captureamount,description,name from transaction_icicicredit  where");
            selectquery.append(" status='markedforreversal'");
            selectquery.append(" and unix_timestamp(now()) - unix_timestamp(timestamp) >600");
            if (icicitransId != null && !icicitransId.equals(""))
                selectquery.append(" and icicitransid= " +ESAPI.encoder().encodeForSQL(ME,icicitransId));
            selectquery.append(" order by timestamp asc");


            ResultSet rs = Database.executeQuery(selectquery.toString(), conn);

            while (rs.next())
            {
                String icicitransid = rs.getString("icicitransid");
                String captureid = rs.getString("captureid");
                String captureCode = rs.getString("capturecode");
                String captureRRN = rs.getString("capturereceiptno");
                String refundamount = rs.getString("refundamount");
                String icicimerchantid = rs.getString("icicimerchantid");
                String toid = rs.getString("toid");
                String accountId = rs.getString("accountid");
                //Added for reversal mail to merchant
                String captureamount=rs.getString("captureamount");
                String rsdescription = rs.getString("description");
                String cardholdername = rs.getString("name");
                log.debug("Reversing for merchantid=" + toid + " for icicitransid=" + icicitransid);
                if (!(icicimerchantid.equals(RB.getString("MID1")) || icicimerchantid.equals(RB.getString(RB.getString("MID1") + "_MID2")))) //for old kit don't refund using api
                {
                    //Now actually reverse transaction

                    AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                    log.debug("callng processRefund");
                    Hashtable hash = paymentGateway.processRefund(icicitransid, refundamount, captureid, captureCode, captureRRN);
                    log.debug("called processRefund");

                    if (hash != null && ((String) hash.get("refundqsiresponsecode")).equals("0"))
                    {
                        StringBuffer sb = new StringBuffer();
                        sb.append("update transaction_icicicredit set status='reversed'");

                        Enumeration e = hash.keys();
                        while (e.hasMoreElements())
                        {
                            String key = (String) e.nextElement();
                            sb.append(" , " + key + " = '" + (String) hash.get(key) + "' ");
                        }

                        sb.append(" where icicitransid=" + icicitransid + " and status='markedforreversal'");

                        int rows = Database.executeUpdate(sb.toString(), conn);
                        log.debug("No of Rows updated : " + rows);
                       // preparing collections of refunds as per merchant
                            refundDetails = new Hashtable();
                            refundDetails.put("icicitransid",icicitransid);
                            refundDetails.put("captureamount",captureamount);
                            refundDetails.put("refundamount",refundamount);
                            refundDetails.put("description",rsdescription);
                            refundDetails.put("accountid",accountId);
                            refundDetails.put("cardholdername",Functions.decryptString(cardholdername));

                            if(membersMap.get(toid)==null)
                            {

                            listOfRefunds = new ArrayList<Hashtable>();
                            listOfRefunds.add(refundDetails);
                            membersMap.put(toid,listOfRefunds);


                            }
                             else
                            {
                             listOfRefunds = membersMap.get(toid);
                             listOfRefunds.add(refundDetails);
                             membersMap.put(toid,listOfRefunds);
                            }
                    }
                }
            }
        }
        catch (Exception e)
        {   log.error("Status Failed : " , e);
            //System.out.println("Status Failed : " + e.toString());


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
                log.error(" Exception occured ",e);
            }
        }

         //sending mail to merchant for transaction reversal
        Set members = membersMap.keySet();
                for(Object memberid : members )
                {
                    log.info("calling sendReverseTransactionMail");
                    try
                    {
                    transaction.sendReverseTransactionMail((ArrayList)membersMap.get(memberid),(String)memberid);
                    }
                    catch(SystemError se)
                    {
                       log.error("Error while sending reversal mail:",se);
                    }
                    log.info("called sendReverseTransactionMail");
                }
        
    }

}
