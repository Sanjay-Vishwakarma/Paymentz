package com.directi.pg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class ProofReminder
{

    static Logger logger = new Logger(ProofReminder.class.getName());

    StringBuffer icicitransidsbuf = null;
    StringBuffer mailbody = null;
    Connection conn = null;
    StringBuffer selectquery = null;
    String amount = null;
    String icicitransid = null;
    String merchantid = null;
    String description = null;
    String authid = null;
    String contact_emails = null;
    String daysremaining = null;

    int toid = 0;
    int prevtoid = 0;
    int count = 0;
    int rssize = 0;


    public static void main(String[] arg)
    {
        ProofReminder pr = new ProofReminder();
        pr.sendReminder(11, 5);
    }

    public void sendReminder(int mindays, int daysover)
    {

        try
        {
            logger.debug("Entering sendReminder for # days");
            icicitransidsbuf = new StringBuffer();
            count = 0;

            conn = Database.getConnection();

            selectquery = new StringBuffer("select icicitransid,description,amount,toid from transaction_icicicredit  where");
            selectquery.append(" status='proofrequired'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) =? order by toid asc");

            mailbody = new StringBuffer("We have not received Proof Document for the following High Value Transaction - " + daysover + " days over.\r\n\r\n");
            mailbody.append("Arrange to send the documents ASAP ").append("\r\n\r\n");
            mailbody.append("trackingid").append("\t").append("amount").append("\t\t").append("description").append("\r\n");
            mailbody.append("-----------").append("\t").append("-------").append("\t\t").append("-----------").append("\r\n");


            PreparedStatement p1=conn.prepareStatement(selectquery.toString());
            p1.setInt(1,daysover);
            ResultSet rs = p1.executeQuery();
            //rssize=rs.getFetchSize();
            logger.debug("rssize " + rssize);

            while (rs.next())
            {
                //rssize++;
                icicitransid = rs.getString("icicitransid");
                description = rs.getString("description");
                amount = rs.getString("amount");
                toid = rs.getInt("toid");
                //daysremaining=rs.getString("daysremaining");

                count++;

                if ((toid != prevtoid && prevtoid != 0))
                {

                    icicitransidsbuf.deleteCharAt(icicitransidsbuf.length() - 1);
                    logger.debug("icicitransidsbuf " + icicitransidsbuf);
                    String s1="select contact_emails from members where memberid=?" ;
                    PreparedStatement p2=conn.prepareStatement(s1);
                    p2.setInt(1,prevtoid);
                    ResultSet rs1 = p2.executeQuery();
                    String contact_emails = rs1.getString("contact_emails");

                    logger.debug("calling SendMAil for Merchant - ");
                    Mail.sendmail(contact_emails, "Do_Not_Reply@tc.com", "Proof Required Reminder - " + daysover + " days over", mailbody.toString());
                    logger.debug("called SendMAil for Merchant-capture");

                    //count=0;
                    icicitransidsbuf = new StringBuffer();
                    mailbody = new StringBuffer("We have not received Proof Document for the following High Value Transaction - " + daysover + " days over.\r\n\r\n");

                    mailbody.append("Arrange to send the documents ASAP ").append("\r\n\r\n");
                    mailbody.append("trackingid").append("\t").append("amount").append("\t\t\t").append("description").append("\r\n");
                    mailbody.append("-----------").append("\t").append("-------").append("\t\t").append("-----------").append("\r\n");
                    mailbody.append(icicitransid).append("\t").append(amount).append("\t\t\t").append(description).append("\r\n");
                }
                else
                {
                    mailbody.append(icicitransid).append("\t").append(amount).append("\t\t\t").append(description).append("\r\n");
                }


                prevtoid = toid;
                icicitransidsbuf.append(icicitransid + ",");
                logger.debug("prevtoid = toid " + (prevtoid == toid));

            }//while ends

            if (count >= 1)
            {   String s2="select contact_emails from members where memberid=?" ;
                PreparedStatement p3=conn.prepareStatement(s2);
                p3.setInt(1,prevtoid);
                ResultSet rs1 = p3.executeQuery();
                String contact_emails = rs1.getString("contact_emails");

                logger.debug("calling SendMAil for Merchant - ");
                Mail.sendmail(contact_emails, "Do_Not_Reply@tc.com", "", mailbody.toString());
                logger.debug("called SendMAil for Merchant-capture");

                logger.debug("Entering sendReminder for # days");
            }
        }
        catch (Exception ex)
        {
            logger.error("Status Failed : ",ex);
        }

    }
}
