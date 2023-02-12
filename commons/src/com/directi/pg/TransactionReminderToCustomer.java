package com.directi.pg;

import com.directi.pg.core.GatewayAccountService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class TransactionReminderToCustomer
{

    static Logger logger = new Logger(TransactionReminderToCustomer.class.getName());

    public static void main(String[] arg) throws Exception
    {
        TransactionReminderToCustomer trc = new TransactionReminderToCustomer();

        StringBuffer strbuf = new StringBuffer();
        strbuf.append("<html><body style=\"font-size: 10pt; font-family: Arial,verdana,helvetica\" >");
        strbuf.append("Hello <#NAME#>,<br><br>You had recently placed an Order at <#BRANDNAME#> <#SITENAME#>, that you");
        strbuf.append(" successfully paid for, using the pz Payment Gateway:<br><br><U>Order");
        strbuf.append(" Details</U><br><br>Tracking Id: <#TRACKINGID#><br>Description: <#DESCRIPTION#><br>Order ");
        strbuf.append("Description: <#ORDERDESCRIPTION#><br>Transaction Amount: <#TRANSAMOUNT#> ( INR )<br>Captured ");
        strbuf.append(" Amount: <#CAPAMOUNT#> ( INR )<br>Date of Transaction: <#DATE#><br>Name of company:");
        strbuf.append(" <#COMPANYNAME#><br><br>We would like to take this opportunity to inform you, that the charge ");
        strbuf.append(" that will be appearing on your Credit Card Statement will display ");
        strbuf.append(" <b><#DISPLAYNAME#></b> This link will take you ");
        strbuf.append(" to the <b>Order Tracking System</b>, that allows you to trace all your ");
        strbuf.append(" transactions, using the Payment Gateway.<br><br>Regards,<br><br> ");
        strbuf.append(" Support Team.<br><b>Support:</b> <a href=\"http://support.pz.com\" ");
        strbuf.append(" >http://support.pz.com</a><br><b>Order Tracking:</b><a ");
        strbuf.append(" href=\"http://secure.pz.com/order\" > http://secure.pz.com/order</a>");
        strbuf.append("</body></html>");

        trc.sendReminder(strbuf.toString(), 8);
    }

    public void sendReminder(String custnotifymail, int mindays)
    {
        Connection conn = null;
        StringBuffer selectquery = null;
        Vector custMailVect = new Vector();
        Vector noMailCustEmailVect = new Vector();
        String mailbody = null;
        String emailaddr = null;
        String displayName = null;
        boolean custremindermail = false;

        try
        {
            logger.debug("Entering sendReminder ");

            conn = Database.getConnection();

            selectquery = new StringBuffer("select M.memberid,T.icicitransid,amount,captureamount,B.last_four,expdate,name,emailaddr,description,orderdescription,date_format(from_unixtime(T.dtstamp),'%d-%m-%Y') as date,company_name,brandname,sitename,custremindermail,currency,templatecurrency,templateamount,T.accountid from transaction_icicicredit as T,members as M, bin_details as B where");
            selectquery.append(" status='settled'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) =?");
            selectquery.append(" and M.memberid=T.toid and T.icicitransid=B.icicitransid ");
            selectquery.append(" order by T.timestamp asc");


            PreparedStatement pst=conn.prepareStatement(selectquery.toString());
            pst.setInt(1,mindays);
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                custremindermail = rs.getBoolean("custremindermail");
                emailaddr = rs.getString("emailaddr");
                String memberId = rs.getString("memberid");
                String accountId = rs.getString("accountid");
                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                String currency = rs.getString("currency");
                String templateCurrency = rs.getString("templatecurrency");
                String templateAmount = rs.getString("templateamount");
                Hashtable innerHash = new Hashtable();
                innerHash.put("NAME", rs.getString("name"));
                innerHash.put("CCNUM", rs.getString("lastfourofccnum"));
                innerHash.put("EXPDATE", PzEncryptor.decryptExpiryDate(rs.getString("expdate")));
                innerHash.put("EMAILADDR", emailaddr);
                innerHash.put("TRACKINGID", rs.getString("icicitransid"));
                innerHash.put("DESCRIPTION", rs.getString("description"));
                innerHash.put("ORDERDESCRIPTION", rs.getString("orderdescription"));
                innerHash.put("COMPANYNAME", rs.getString("company_name"));
                innerHash.put("BRANDNAME", rs.getString("brandname"));
                innerHash.put("SITENAME", rs.getString("sitename"));
                innerHash.put("TRANSAMOUNT", rs.getString("amount"));
                innerHash.put("CAPAMOUNT", rs.getString("captureamount"));
                innerHash.put("DATE", rs.getString("date"));
                innerHash.put("CURRENCY", currency);
                innerHash.put("DISPLAYNAME", displayName);

                if (! currency.equals(templateCurrency))
                    innerHash.put("TMPL_TRANSACTION", "(approximately " + templateCurrency + " " + templateAmount + " )");
                custMailVect.add(innerHash);
                if (!custremindermail)
                    noMailCustEmailVect.add(emailaddr);
              innerHash=null;
            }//while ends

            Enumeration custMailEnum = custMailVect.elements();
            while (custMailEnum.hasMoreElements())
            {
                Hashtable transData = (Hashtable) custMailEnum.nextElement();
                mailbody = Functions.replaceTag(custnotifymail, transData);
                emailaddr = (String) transData.get("EMAILADDR");
                String trackingid = (String) transData.get("TRACKINGID");
                if (! noMailCustEmailVect.contains(emailaddr))
                {
                    logger.debug("Sending mail to customer at " + emailaddr);
                    Mail.sendHtmlMail(emailaddr, "Do_Not_Reply@tc.com", null, null, "Last Reminder: Your Order at " + (String) transData.get("SITENAME"), mailbody);
                    logger.debug("Sent mail to customer");
                }
                else
                    logger.debug("Settlement notification mail is not sent to " + emailaddr + " for trackingID=" + trackingid + "as Merchant asked not to send mail.");
            }

        }
        catch (Exception ex)
        {
            logger.error("Status Failed : ",ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving sendReminder ");

    }
}
