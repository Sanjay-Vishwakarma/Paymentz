package com.directi.pg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class CaptureReminder
{

    static Logger logger = new Logger(CaptureReminder.class.getName());

    public static void main(String[] arg)
    {
        CaptureReminder cr = new CaptureReminder();
        cr.sendReminder(3);
    }

    public void sendReminder(int mindays)
    {
        StringBuffer mailbody = null;
        Connection conn = null;
        StringBuffer selectquery = null;
        String amount = null;
        String icicitransid = null;
        String merchantid = null;
        String authid = null;

        int count = 0;

        try
        {
           logger.debug("Entering sendReminder ");

            count = 0;

            conn = Database.getConnection();

            selectquery = new StringBuffer("select icicitransid,authid,captureamount,icicimerchantid from transaction_icicicredit  where");
            selectquery.append(" status='capturestarted'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >=?" );
            selectquery.append(" order by timestamp asc");

            mailbody = new StringBuffer("Following Transaction has Not yet  Captured.<br><br>");

            mailbody.append("TrackingId").append("&nbsp;&nbsp;&nbsp;").append("AuthId").append("&nbsp;&nbsp;&nbsp;").append("Amount to Capture").append("  &nbsp;&nbsp;&nbsp;").append("ICICI MerchantID").append("&nbsp;&nbsp;&nbsp;").append("<br>");
            mailbody.append("-----------").append("&nbsp;&nbsp;&nbsp;").append("-------").append("&nbsp;&nbsp;&nbsp;").append("----------------").append("  &nbsp;&nbsp;&nbsp;").append("----------------").append("&nbsp;&nbsp;&nbsp;").append("<br>");


            PreparedStatement pstmt=conn.prepareStatement(selectquery.toString());
            pstmt.setInt(1,mindays);
            ResultSet rs = pstmt.executeQuery();


            while (rs.next())
            {
                //rssize++;
                icicitransid = rs.getString("icicitransid");
                authid = rs.getString("authid");
                amount = rs.getString("captureamount");
                merchantid = rs.getString("icicimerchantid");

                count++;

                mailbody.append(icicitransid).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(authid).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(amount).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(merchantid).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append("<br>");

            }//while ends

            if (count >= 1)
            {
                logger.debug("calling SendMAil");
                Mail.sendAdminMail("Capture Reminder", mailbody.toString());
                logger.debug("called SendMAil");
                logger.debug("Leaving sendReminder ");
            }
        }
        catch (Exception ex)
        {   logger.error("Status Failed :",ex);
            //System.out.println("Status Failed : " + ex.toString());
        }

    }
}
