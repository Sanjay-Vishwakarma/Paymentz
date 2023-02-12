package com.directi.pg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.SortedMap;

public class DailyTransactionReport
{
    static Logger Log = new Logger(DailyTransactionReport.class.getName());

    public static void main(String[] args) throws Exception
    {
        Merchants.refresh();  //for Merchants.getcompanyName();
        new DailyTransactionReport().sendMail(new Date());
    }

    public static void sendMail(Date dt) throws Exception
    {

        Log.debug("Entering sendMail method of DailyTransactionReport");
        Merchants merchants = new Merchants();
        String startdt = "";
        String enddt = "";
        String date = "";

        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        java.util.GregorianCalendar stcal = new java.util.GregorianCalendar();
        java.util.GregorianCalendar encal = new java.util.GregorianCalendar();

        try
        {
            cal.setTime(dt);
            stcal.setTime(dt);
            encal.setTime(dt);

            stcal.add(Calendar.DATE, -2);
            cal.add(Calendar.DATE, -1);

            startdt = "" + stcal.get(Calendar.YEAR) + "-" + Integer.toString(stcal.get(Calendar.MONTH) + 1) + "-" + (stcal.get(Calendar.DATE)) + " 23:59:59";
            enddt = "" + encal.get(Calendar.YEAR) + "-" + Integer.toString(encal.get(Calendar.MONTH) + 1) + "-" + (encal.get(Calendar.DATE)) + " 00:00:00";
            date = " " + cal.get(Calendar.YEAR) + "-" + Integer.toString(cal.get(Calendar.MONTH) + 1) + "-" + (cal.get(Calendar.DATE));

        }
        catch (NumberFormatException nfe)
        {
            startdt = "-";
            enddt = "-";
        }
        StringBuffer mail = new StringBuffer();
        StringBuffer row = new StringBuffer();
        StringBuffer statussb = new StringBuffer();
        Connection con = null;
        try
        {
            con = Database.getConnection();
            TransactionEntry transactionEntry = new TransactionEntry(0);

            SortedMap statusHash = transactionEntry.getSortedMap();

            String st = "select count(*) as \"count\",toid,status, authqsiresponsedesc from transaction_icicicredit where dtstamp > unix_timestamp(?)  and dtstamp < unix_timestamp(?) group by toid,status,authqsiresponsedesc order by toid,status,authqsiresponsedesc";
            PreparedStatement ps=con.prepareStatement(st);
            ps.setString(1,startdt);
            ps.setString(2,enddt);
            ResultSet rs = ps.executeQuery();



            int count = 0;
            String status = null;
            String style = "tr2";
            boolean found = false;
            int toid = 0;
            int prevtoid = 0;

            while (rs.next())
            {
                status = rs.getString("status");
                toid = rs.getInt("toid");
                String msg = "";
                if (status.equals("authfailed"))
                    msg = rs.getString("authqsiresponsedesc");
                else
                    msg = (String) statusHash.get(status);

                if (prevtoid != 0 && prevtoid != toid)
                {
                    if ("tr1".equals(style))
                        style = "tr2";
                    else
                        style = "tr1";
                }

                row.append("<tr class=\"" + style + "\">");
                row.append("<td>" + merchants.getCompany("" + toid) + " (" + toid + ")</td>");
                row.append("<td>" + rs.getInt("count") + "</td>");
                row.append("<td>" + msg + "</td>");
                row.append("</tr>");


                count += rs.getInt("count");
                found = true;
                prevtoid = toid;
            }

            st = "select count(*) as \"count\",status, authqsiresponsedesc from transaction_icicicredit where dtstamp > unix_timestamp(?)  and dtstamp < unix_timestamp(?) group by status, authqsiresponsedesc order by status, authqsiresponsedesc";

            PreparedStatement pstmt=con.prepareStatement(st);
            pstmt.setString(1,startdt);
            pstmt.setString(2,enddt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                status = rs.getString("status");
                String msg = "";
                if (status.equals("authfailed"))
                    msg = rs.getString("authqsiresponsedesc");
                else
                    msg = (String) statusHash.get(status);
                statussb.append("<tr valign=left cellspacing=\"0\">");
                statussb.append("<td align=\"left\" ><font face=\"verdana,arial,helvetica\"  size=\"1\">Total " + msg + " : <b>" + rs.getInt("count") + "</b></font></td>");
                statussb.append("</tr>");

            }

            mail.append("<html><head><style>.tr1{background:#FaEEE0;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:12px;color:#ffffff;fonts-family:arial,verdana,helvetica}.tr2{background:#fffeec;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}</style></head><body><center><h3>Daily Transaction Report - " + date + "</h3></center>");
            mail.append("<table align=center border=0 cellPadding=0 cellSpacing=0 width=\"95%\" valign=\"top\">");

            mail.append("<tr valign=\"left\" cellspacing=\"0\">");
            mail.append("<td align=\"left\" ><b><font face=\"verdana,arial,helvetica\"  size=\"1\">Total Transactions : " + count + " </font></td>");
            mail.append("</tr>");
            mail.append("<tr><td colspan=\"3\">&nbsp;</td></tr>");
            mail.append("<tr><td colspan=\"3\">&nbsp;</td></tr>");
            mail.append(statussb.toString());
            mail.append("<tr><td colspan=\"3\">&nbsp;</td></tr>");
            mail.append("<tr><td colspan=\"3\">&nbsp;</td></tr>");

            mail.append("<tr valign=center cellspacing=\"0\">");
            mail.append("<td align=\"left\" bgcolor=\"#2379A5\"><b><font face=\"verdana,arial,helvetica\" color=\"#FFFFFF\" size=\"1\">Company</font></td>");
            mail.append("<td align=\"left\" bgcolor=\"#2379A5\"><b><font face=\"verdana,arial,helvetica\" color=\"#FFFFFF\" size=\"1\">No of Transactions</font></td>");
            mail.append("<td align=\"left\" bgcolor=\"#2379A5\"><b><font face=\"verdana,arial,helvetica\" color=\"#FFFFFF\" size=\"1\">Status</font></td>");
            mail.append("</tr>");

            mail.append(row.toString());
            mail.append("</table></body></html>");
            if (found)
            {
                Mail.sendNotificationMail("Daily Transaction Report -" + date, mail.toString());
            }

            Log.debug("Mail sent " + found);

        }
        catch (Exception ex)
        {   Log.error("Exception ", ex);

        }
        finally {
            Database.closeConnection(con);
        }
    }
}


