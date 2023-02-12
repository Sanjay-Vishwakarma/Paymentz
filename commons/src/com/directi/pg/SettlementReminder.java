package com.directi.pg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class SettlementReminder
{

    static Logger logger = new Logger(SettlementReminder.class.getName());

    public static void main(String[] arg)
    {
        SettlementReminder sr = new SettlementReminder();
        sr.sendReminder(7);
    }

    public void sendReminder(int mindays)
    {
        StringBuffer mailbody = null;
        Connection conn = null;
        StringBuffer selectquery = null;
        String amount = null;
        String icicitransid = null;
        String merchantid = null;
        String captureid = null;
        String captureRRN = null;
        String podbatch = null;
        String company_name = null;

        int count = 0;

        try
        {
            logger.debug("Entering sendReminder ");
            count = 0;
            conn = Database.getConnection();

            selectquery = new StringBuffer("select T.icicitransid,T.captureid,T.amount,T.icicimerchantid,T.capturereceiptno,date_format(from_unixtime(T.podbatch),'%d-%m-%Y') as podbatch,M.company_name from transaction_icicicredit as T,members as M where");
            selectquery.append(" status in ('podsent','capturesucess','capturestarted')");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >=?");
            selectquery.append(" and M.memberid=T.toid");
            selectquery.append(" order by T.podbatch asc");

            mailbody = new StringBuffer("Following Transaction has Not yet  Settled.\r\n\r\n");
            mailbody.append("SessionID").append("\t").append("FT").append("\t").append("Amount").append("  \t").append("MerchantID").append("\t").append("captureRRN").append("\t").append("Capture Date").append("\t").append("Company Name").append("\r\n");
            mailbody.append("---------").append("\t").append("---").append("\t").append("------").append("  \t").append("----------").append("\t").append("-----------").append("\t").append("------------").append("\t").append("------------").append("\r\n");


            PreparedStatement p1=conn.prepareStatement(selectquery.toString());
            p1.setInt(1,mindays);
            ResultSet rs = p1.executeQuery();


            while (rs.next())
            {
                icicitransid = rs.getString("icicitransid");
                captureid = rs.getString("captureid");
                amount = rs.getString("amount");
                merchantid = rs.getString("icicimerchantid");
                captureRRN = rs.getString("capturereceiptno");
                podbatch = rs.getString("podbatch");
                company_name = rs.getString("company_name");
                count++;

                mailbody.append(icicitransid).append("\t\t").append(captureid).append("\t").append(amount).append("  \t").append(merchantid).append("\t").append(captureRRN).append("\t").append(podbatch).append("\t").append(company_name).append("\r\n");

            }//while ends

            if (count >= 1)
            {
                logger.debug("calling SendMAil for settle ");
                Mail.sendNotificationMail("Settlement Reminder -" + mindays + " days", mailbody.toString());
                logger.debug("called SendMAil for settle");
                logger.debug("Leaving sendReminder ");
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
