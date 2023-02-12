package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;


public class Status
{
// no of days > 11
// status =authsuccess
// set to status =failed
// description = Auth not captured within the specified days
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

    int toid = 0;
    int prevtoid = 0;
    int count = 0;
    int rssize = 0;


    public static void main(String[] arg)
    {
        Status s = new Status();
        s.setStatus(11);
    }

    public void setStatus(int mindays)
    {
        String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        try
        {
            logger.debug("Entering setStatus");
            icicitransidsbuf = new StringBuffer();
            count = 0;

            conn = Database.getConnection();

            selectquery = new StringBuffer("select * from transaction_icicicredit  where");
            selectquery.append(" status='authsuccessful'");
            selectquery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >=? order by toid asc");

            mailbody = new StringBuffer("Following Transaction have been Cancelled as these were not been captured at the End of " + mindays + " days\r\n\r\n");
            mailbody.append("trackingid").append("\t").append("amount").append("\t\t").append("description").append("\r\n");
            mailbody.append("-----------").append("\t").append("-------").append("\t\t").append("-----------").append("\r\n");


            PreparedStatement p1=conn.prepareStatement(selectquery.toString());
            p1.setInt(1,mindays);
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
                count++;

                if ((toid != prevtoid && prevtoid != 0))
                {
                    //			if(rssize==1)
                    //			{
                    //				icicitransidsbuf.append(icicitransid+",");
                    //				mailbody.append(icicitransid).append("\t").append(description).append("\t").append(amount);
                    //			}

                    icicitransidsbuf.deleteCharAt(icicitransidsbuf.length() - 1);
                    logger.debug("icicitransidsbuf " + icicitransidsbuf);
                    StringBuffer updatequery = new StringBuffer("update transaction_icicicredit set status='authcancelled' where");
                    updatequery.append(" icicitransid IN (?)");
                    //updatequery.append(" status='authsuccess'");
                    //updatequery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >" + mindays);


                    PreparedStatement p2=conn.prepareStatement(updatequery.toString());
                    p2.setString(1,icicitransidsbuf.toString());
                    logger.debug("Number of rows Affected " + p2.executeUpdate());
                    logger.debug("leaving setStatus");
                    String s1="select notifyemail from members where memberid=?";
                    PreparedStatement p3=conn.prepareStatement(s1);
                    p3.setInt(1, prevtoid);
                    ResultSet rs1 = p3.executeQuery();
                    notifyemail = rs1.getString("notifyemail");

                    logger.debug("calling SendMAil for Merchant - ");
                    //Mail.sendmail(notifyemail, fromAddress, null, adminEmail, "Transaction Cancelled", mailbody.toString());
                    logger.debug("called SendMAil for Merchant-capture");

                    //count=0;
                    icicitransidsbuf = new StringBuffer();
                    mailbody = new StringBuffer("Following Transaction have been Cancelled as these were not been captured at the End of " + mindays + " days\r\n\r\n");
                    mailbody.append("trackingid").append("\t").append("amount").append("\t\t").append("description").append("\r\n");
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
            {
                icicitransidsbuf.deleteCharAt(icicitransidsbuf.length() - 1);
                logger.debug("icicitransidsbuf " + icicitransidsbuf);
                StringBuffer updatequery = new StringBuffer("update transaction_icicicredit set status='authcancelled' where");
                updatequery.append(" icicitransid IN (?)");
                //updatequery.append(" status='authsuccess'");
                //updatequery.append(" and (TO_DAYS(now()) - TO_DAYS(timestamp)) >" + mindays);



                PreparedStatement p4=conn.prepareStatement(updatequery.toString());
                p4.setString(1,icicitransidsbuf.toString());
                logger.debug("Number of rows Affected " + p4.executeUpdate());
                logger.debug("leaving setStatus");
                String s="select notifyemail from members where memberid=?";
                PreparedStatement p5=conn.prepareStatement(s);
                p5.setInt(1,prevtoid);
                ResultSet rs1 = p5.executeQuery();
                notifyemail = rs1.getString("notifyemail");

                logger.debug("calling SendMAil for Merchant - ");
                //Mail.sendmail(notifyemail, fromAddress, null, adminEmail, "Transaction Cancelled", mailbody.toString());
                logger.debug("called SendMAil for Merchant-capture");

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
