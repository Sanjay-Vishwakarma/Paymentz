package com.directi.pg;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;

public class NegativeBalance
{

    static Logger logger = new Logger(NegativeBalance.class.getName());

    public static void main(String[] arg)
    {
        NegativeBalance nr = new NegativeBalance();
        nr.sendBalance();
    }

    public void sendBalance()
    {
        logger.debug("Entering sendBalance ");

        StringBuffer mailbody = null;
        Connection conn = null;
        StringBuffer selectquery = null;
        int memberid = -1;
        String company_name = null;

        BigDecimal balance = null;
        TransactionEntry te = null;

        int toid = 0;
        int count = 0;


        try
        {
            conn = Database.getConnection();
            selectquery = new StringBuffer("select memberid,company_name from members order by company_name");
            mailbody = new StringBuffer("Following Merchants Balance is Negative.\r\n\r\n");
            mailbody.append("Memberid").append("\t").append("Company Name").append("\t\t\t").append("Balance").append("\r\n");
            mailbody.append("-----------").append("\t").append("----------").append("\t\t").append("-----------").append("\r\n");

            ResultSet rs = Database.executeQuery(selectquery.toString(), conn);
            while (rs.next())
            {
                memberid = rs.getInt("memberid");
                company_name = rs.getString("company_name");
                te = new TransactionEntry(memberid);
                balance = new BigDecimal(te.getBalance());

                if (balance.signum() == -1)
                {
                    mailbody.append(memberid).append("\t").append(company_name).append("\t").append(balance.toString()).append("\r\n");
                    count++;
                }
            }//while ends

            if (count >= 1)
            {
                logger.debug("calling SendMAil for balance ");
                Mail.sendNotificationMail(count + "- Merchant's Balance is Negative  ", mailbody.toString());
                logger.debug("called SendMAil for balance");
            }
        }
        catch (Exception ex)
        {
            logger.error("Exception : ",ex);
        }
        finally {
            Database.closeConnection(conn);
        }

        logger.debug("Leaving sendBalance ");
    }
}
