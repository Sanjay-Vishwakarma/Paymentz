package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Calendar;

public class Balance
{

    static Logger logger = new Logger(Balance.class.getName());

    public static void main(String[] arg)
    {
        Balance nr = new Balance();
        nr.sendBalance(7, 2007);
    }

    public void sendBalance(int recmonth, int recyear)
    {
        logger.info("Entering sendBalance ");

        Calendar rightNow = Calendar.getInstance();
        int curmonth = rightNow.get(rightNow.MONTH);
        int curyear = rightNow.get(rightNow.YEAR);
        logger.debug("curmonth=" + curmonth + "curyear=" + curyear);
        int premonth, year;

        if (curmonth == 0)
        {
            premonth = 12;
            year = curyear - 1;
        }
        else
        {
            premonth = curmonth;
            year = curyear;
        }
        if (recmonth != 0)
            premonth = recmonth;
        if (recyear != 0)
            year = recyear;
        logger.debug("premonth=" + premonth + "year=" + year);

        java.util.GregorianCalendar encal = new java.util.GregorianCalendar(year, premonth, 1, 0, 0, 0);
        Long ldt = new Long(encal.getTime().getTime() / 1000);
        //subtact by 1 so that the balance is of prev month
        int date = ldt.intValue() - 1;
        StringBuffer mailbody = null;
        Connection conn = null;
        StringBuffer selectquery = null;
        int memberid = -1;
        String company_name = null;

        BigDecimal balance = null;
        TransactionEntry te = null;
        int count = 0;

        try
        {

            conn = Database.getConnection();
            selectquery = new StringBuffer("select memberid,company_name from members order by company_name,memberid");
            mailbody = new StringBuffer();


            mailbody.append("<html><head><style>\r\n.tr1{text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n\r\n.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:12px;color:#ffffff;font-family:arial,verdana,helvetica}\r\n</style></head><body text=\"#800000\"><p align=center><b>Merchant Monthly Balance Report - ?/?</b></p><hr>");
            mailbody.append("<table width=\"100%\"><tr class=\"heading\" ><td>Memberid</td><td>Company Name</td><td>Balance</td>");


            PreparedStatement pstmt=conn.prepareStatement(selectquery.toString());
            pstmt.setInt(1,premonth);
            pstmt.setInt(2,year);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberid = rs.getInt("memberid");
                company_name = rs.getString("company_name");
                te = new TransactionEntry(memberid);
                balance = new BigDecimal(te.getbalance(0, date));
                if (!balance.toString().equals("0"))
                {
                    mailbody.append("<tr class=\"tr1\"><td>").append(memberid).append("</td><td>").append(company_name).append("</td><td>").append(balance.toString()).append("</td></tr>");
                    count++;
                }
            }//while ends

            if (count >= 1)
            {
                mailbody.append("</table></body></html>");
                String toBilling = ApplicationProperties.getProperty("COMPANY_BILLING_EMAIL");
                String bccAdmin = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
                //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
                logger.debug("calling SendMAil");
                //Mail.sendHtmlMail(toBilling, fromAddress, null, bccAdmin, "Merchant Monthly Balance Report - " + premonth + "/" + year, mailbody.toString());
                logger.debug("called SendMAil");
            }
        }
        catch (Exception ex)
        {   logger.error("Exception :",ex);
            //System.out.println("Exception : " + ex.toString());
        }

        logger.debug("Leaving sendBalance ");
    }
}
