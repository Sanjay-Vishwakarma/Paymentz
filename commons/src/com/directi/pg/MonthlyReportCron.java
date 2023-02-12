package com.directi.pg;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Vector;

public class MonthlyReportCron
{

    static Logger logger = new Logger(MonthlyReportCron.class.getName());


    StringBuffer mailbody = null;
    Connection conn = null;
    StringBuffer selectquery1 = null;
    StringBuffer selectquery2 = null;
    String settleamount = "0.00";
    double totalsettleamount = 0.0;
    double totalavgamount = 0.0;
    double totalsettletransaction = 0;
    double totalchargebacktransaction = 0;
    String cbamount = "0.00";
    double totalcbamount = 0.0;
    String companyname = null;
    String memberid = "";
    int count = 0;


    public static void main(String[] arg)
    {
        MonthlyReportCron mr = new MonthlyReportCron();
        mr.sendReport(Integer.parseInt((String) arg[0]), Integer.parseInt((String) arg[1]));
    }

    public void sendReport(int recmonth, int recyear)
    {

        try
        {
            logger.debug("Entering MonthlyReportCron for # month");
            conn = Database.getConnection();

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
            // this query gives total settled transactions and chargeback transaction for the same month
            // Do not give chargeback done for the tranmsaction happened in previous month.
            //Also if transaction is chargeback it will not considered in settled transaction
            /*
               selectquery=new StringBuffer("select company_name,toid,sum(amount) as amount,status,count(*) from transaction_icicicredit,members where");
               selectquery.append(" (status='settled'  or status='chargeback')");
               selectquery.append(" and MONTH(from_unixtime(transaction_icicicredit.dtstamp))="+ premonth );
               selectquery.append(" and YEAR(from_unixtime(transaction_icicicredit.dtstamp))= "+ year );
               selectquery.append(" and transaction_icicicredit.toid=members.memberid");
               selectquery.append(" group by toid,status order by company_name ");
               */

            //this query actually work on transaction table so will return total transaction and chargeback amount for the same month.
            // two different query require for chargeback and transaction

            //total transaction query
            selectquery1 = new StringBuffer("select company_name,toid as memberid,sum(amount) as amount,type,count(*) from transactions,members where");
            selectquery1.append(" type='credit'");
            selectquery1.append(" and MONTH(from_unixtime(transactions.dtstamp))= ?");
            selectquery1.append(" and YEAR(from_unixtime(transactions.dtstamp))= ?");
            selectquery1.append(" and transactions.toid=members.memberid");
            selectquery1.append(" group by toid,type order by company_name");

            //total chargeback query

            selectquery2 = new StringBuffer("select company_name,fromid as memberid,sum(amount) as amount,type,count(*) from transactions,members where");
            selectquery2.append(" type='chargeback'");
            selectquery2.append(" and MONTH(from_unixtime(transactions.dtstamp))=?");
            selectquery2.append(" and YEAR(from_unixtime(transactions.dtstamp))= ?");
            selectquery2.append(" and transactions.fromid=members.memberid ");
            selectquery2.append(" group by fromid,type order by company_name");


            mailbody = new StringBuffer();
            mailbody.append("<html><head><style>\r\n.tr1{text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n\r\n.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:12px;color:#ffffff;font-family:arial,verdana,helvetica}\r\n</style></head><body text=\"#800000\"><p align=center><b>Sub-Merchantwise total transacted amount for the month <b>" + premonth + "/" + year + ".</b></p><hr>");
            mailbody.append("<table align=\"center\" width=\"90%\"><tr class=\"heading\" ><td>Memberid</td><td>Company Name</td><td>Settled Amount</td><td>No. of settled transactions</td><td>Average transaction amount</td><td>Chargebck</td><td>No. of chargebacks</td></tr>");
            mailbody.append("<tr><td colspan=\"4\">&nbsp;</td></tr>");



            PreparedStatement p2=conn.prepareStatement(selectquery2.toString());
            p2.setInt(1,premonth);
            p2.setInt(2, year);
            ResultSet rs2 = p2.executeQuery();
            Vector compname = new Vector();
            Vector member = new Vector();
            Vector ammount = new Vector();
            Vector countvector = new Vector();
            while (rs2.next())
            {
                compname.add(rs2.getString("company_name"));
                member.add(rs2.getString("memberid"));
                ammount.add(rs2.getString("amount"));
                countvector.add(rs2.getString(5));
            }

            logger.debug("Vec 1" + compname + " mem " + member + " amm " + ammount);

            //rssize=rs.getFetchSize();

            int toid = 0;
            boolean more = false;
            /*if(rs2.next())
                   more=true;*/

            PreparedStatement p1=conn.prepareStatement(selectquery1.toString());
            p1.setInt(1,premonth);
            p1.setInt(2, year);
            ResultSet rs1 = p1.executeQuery();
            while (rs1.next())
            {
                cbamount = "0.00";
                memberid = rs1.getString("memberid");
                settleamount = rs1.getString("amount");
                settleamount = rs1.getString(3);
                int settleCount = rs1.getInt(5);
                int chargebackCount = 0;
                totalsettleamount += Double.parseDouble(settleamount);
                double avrage = Double.parseDouble(settleamount) / settleCount;
                totalavgamount += avrage;
                totalsettletransaction += settleCount;
                companyname = rs1.getString("company_name");
                logger.debug("got company=" + companyname + " amount=" + settleamount);
                for (int i = 0; i < compname.size(); i++)
                {
                    if (((String) member.elementAt(i)).equals(memberid)) //if there are records for chargeback
                    {
                        cbamount = (String) ammount.get(i);
                        chargebackCount = Integer.parseInt((String) countvector.get(i));
                        totalcbamount += Double.parseDouble(cbamount);
                        totalchargebacktransaction += chargebackCount;
                        ammount.remove(i);
                        member.remove(i);
                        compname.remove(i);
                        countvector.remove(i);

                    }
                }

                mailbody.append("<tr class=\"tr1\" ><td>").append(memberid).append("</td><td>").append(companyname).append("</td><td>").append(settleamount).append("</td><td><center>").append(settleCount).append("</center></td><td>").append(new BigDecimal(avrage).setScale(2, BigDecimal.ROUND_HALF_UP)).append("</td><td>").append(cbamount).append("</td><td><center>").append(chargebackCount).append("</center></td></tr>");

                count++;

            }

            //if there are some charge back for the  company where no transaction were settled for that month.
            for (int i = 0; i < compname.size(); i++)
            {
                settleamount = "0.00";
                memberid = (String) member.get(i);
                cbamount = (String) ammount.get(i);
                totalcbamount += Double.parseDouble(cbamount);
                companyname = (String) compname.get(i);
                logger.debug("got company=" + companyname + " amount=" + cbamount);
                mailbody.append("<tr class=\"tr1\" ><td>").append(memberid).append("</td><td>").append(companyname).append("</td><td>").append(settleamount).append("</td><td>").append(cbamount).append("</td></tr>");
                count++;
            }
            mailbody.append("<tr><td colspan=\"7\"><hr></td></tr>");
            mailbody.append("<tr class=\"tr1\" ><td>").append("&nbsp;").append("</td><td>").append("&nbsp;").append("</td><td>").append(new BigDecimal(totalsettleamount).setScale(2, BigDecimal.ROUND_HALF_UP)).append("</td><td><center>" + new BigDecimal(totalsettletransaction).setScale(0, BigDecimal.ROUND_HALF_UP) + "</center></td><td>" + new BigDecimal(totalavgamount).setScale(2, BigDecimal.ROUND_HALF_UP) + "</td><td>").append(new BigDecimal(totalcbamount).setScale(2, BigDecimal.ROUND_HALF_UP)).append("</td><td><center>" + new BigDecimal(totalchargebacktransaction).setScale(0, BigDecimal.ROUND_HALF_UP) + "</center></td></tr>");
            mailbody.append("</table></body></html>");

            //   System.out.println("Mail body\r\n\r\n"+mailbody.toString()+"\r\n\r\n");

            if (count > 0)
            {
                logger.debug("calling SendMail - ");
                Mail.sendAdminMail("Sub-Merchantwise total for the month " + premonth + "/" + year, mailbody.toString());
                logger.debug("called SendMail ");
            }
            logger.debug("Leaving MonthlyReportCron ");

        }
        catch (Exception ex)
        {
            logger.error("Status Failed : ",ex);
        }
        finally {
            Database.closeConnection(conn);
        }

    }//end sendReport
}//end class
