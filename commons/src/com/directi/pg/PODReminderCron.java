package com.directi.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PODReminderCron
{

    static Logger logger = new Logger(Status.class.getName());

    StringBuffer icicitransidsbuf = null;
    StringBuffer mailbody = null;
    Connection conn = null;
    StringBuffer selectquery = null;
    String amount = null;
    String icicitransid = null;
    String merchantid = null;
    String description = null;
    String authid = null;
    String notifyemail = null;
    String daysremaining = null;

    int toid = 0;
    int prevtoid = 0;
    int count = 0;
    int rssize = 0;


    public static void main(String[] arg)
    {
        PODReminderCron pr = new PODReminderCron();
        pr.sendReminder(11, 4);
    }

    public void sendReminder(int mindays, int daysover)
    {
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        try
        {
            logger.debug("Entering send capture Reminder ");
            icicitransidsbuf = new StringBuffer();
            count = 0;
            conn = Database.getConnection();

            selectquery = new StringBuffer("select icicitransid,description,amount,toid from transaction_icicicredit  where");
            selectquery.append(" status='authsuccessful'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >=? order by toid asc");

            mailbody = new StringBuffer("<html><body>Following Transaction have Not Yet Been Captured.<br><br>");
            mailbody.append("Capture these transaction else it will be Automatically cancelled after ").append(mindays).append(" days from the time of authsuccesful.<br><br>");
            mailbody.append("<table>");
            mailbody.append("<tr><td>trackingid</td>").append("<td>").append("amount</td>").append("<td>").append("description</td>").append("<br>");


            PreparedStatement p1=conn.prepareStatement(selectquery.toString());
            p1.setInt(1,daysover);
            ResultSet rs = p1.executeQuery();
            logger.debug("rssize " + rssize);

            while (rs.next())
            {
                icicitransid = rs.getString("icicitransid");
                description = rs.getString("description");
                amount = rs.getString("amount");
                toid = rs.getInt("toid");
                count++;

                if ((toid != prevtoid && prevtoid != 0))      //if it is not for first record then go inside
                {

                    icicitransidsbuf.deleteCharAt(icicitransidsbuf.length() - 1);
                    logger.debug("icicitransidsbuf " + icicitransidsbuf);
                    String s1="select notifyemail from members where memberid=?";
                    PreparedStatement p2=conn.prepareStatement(s1);
                    p2.setInt(1, prevtoid);
                    ResultSet rs1 = p2.executeQuery();
                    if (rs1.next())
                    {
                        notifyemail = rs1.getString("notifyemail");
                        logger.debug("calling SendMAil for Merchant - ");
                        mailbody.append("</table></body></html>");
                        //Mail.sendHtmlMail(notifyemail, fromAddress, null, null, "Capture Reminder - " + daysover + " days over", mailbody.toString());
                        logger.debug("called SendMAil for Merchant-capture");
                    }

                    //count=0;
                    icicitransidsbuf = new StringBuffer();

                    mailbody = new StringBuffer("<html><body>Following Transaction have Not Yet Been Captured.<br><br>");
                    mailbody.append("Capture these transaction else it will be Automatically cancelled after ").append(mindays).append(" days from the time of authsuccesful.<br><br>");
                    mailbody.append("<table>");
                    mailbody.append("<tr><td>trackingid</td>").append("<td>").append("amount</td>").append("<td>").append("description</td>");
                    mailbody.append("<tr><td>").append(icicitransid).append("</td><td>").append(amount).append("</td><td>").append(description).append("</td>");
                }
                else
                {
                    mailbody.append("<tr><td>").append(icicitransid).append("</td><td>").append(amount).append("</td><td>").append(description).append("</td>");
                }


                prevtoid = toid;
                icicitransidsbuf.append(icicitransid + ",");
                logger.debug("prevtoid = toid " + (prevtoid == toid));

            }//while ends

            if (count >= 1)         //for last record
            {   String s2="select notifyemail from members where memberid=?";
                PreparedStatement p2=conn.prepareStatement(s2);
                p2.setInt(1,prevtoid);
                ResultSet rs1 = p2.executeQuery();
                if (rs1.next())
                {
                    notifyemail = rs1.getString("notifyemail");
                    logger.debug("calling SendMAil for Merchant - ");
                    mailbody.append("</table></body></html>");
                    //Mail.sendHtmlMail(notifyemail, fromAddress, null, null, "Capture Reminder - " + daysover + " day(s) over", mailbody.toString());
                    logger.debug("called SendMAil for Merchant-capture");
                    logger.debug("Leaving sendReminder for " + daysover + " days");
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("Status Failed : ",ex);
        }
        finally {
            Database.closeConnection(conn);
        }
    }
}
