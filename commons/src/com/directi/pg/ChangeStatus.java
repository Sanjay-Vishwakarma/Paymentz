package com.directi.pg;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

public class ChangeStatus
{
    static Logger logger = new Logger(ChangeStatus.class.getName());


    public static void main(String[] args)
    {
        try
        {
            if (args.length < 3)
            {
                logger.debug("Usage....pass !");
                logger.debug("1st argument : icicitransid ");
                return;
            }

            ChangeStatus cs = new ChangeStatus();
            cs.change(args[0]);

        }
        catch (Exception e)
        {
            logger.error("Exception in ChangeStatus :::::::::",e);
        }
    }


    public void change(String icicitransid) throws Exception
    {
        logger.debug("Inside change()");
        String query = null;

        try
        {
            int size = 0;
            if (icicitransid != null)
            {
                Connection con=Database.getConnection();
                query = "select icicitransid from transaction_icicicredit where status='authstarted' and icicitransid=?";

                PreparedStatement pstmt=con.prepareStatement(query);
                pstmt.setString(1,icicitransid);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next())
                {
                    StringBuffer updatequery = new StringBuffer("update transaction_icicicredit set status='cancelled' where");
                    updatequery.append(" icicitransid=?");

                    PreparedStatement ps=con.prepareStatement(updatequery.toString());
                    ps.setString(1,icicitransid);
                    logger.debug("Number of rows Affected " );
                    logger.debug("leaving change");
                }
            }

            else
            {
                query = "update transaction_icicicredit set status='cancelled' where status='authstarted' and (unix_timestamp(now())- dtstamp ) > 3600  "; //if status is hang on authstarted even after one hour.

                logger.debug("Number of rows Affected " + Database.executeUpdate(query, Database.getConnection()));
                logger.debug("leaving change");

            }


        }
        catch (Exception e)
        {
            logger.error("Exception occure in change method",e);

        }

    }
}