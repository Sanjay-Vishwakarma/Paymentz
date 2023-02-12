package com.directi.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Jan 10, 2013
 * Time: 8:17:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmailChecker
{

    static Logger logger = new Logger(EmailChecker.class.getName());

    public static boolean isEmailValidationRequired(String toid)
    {

        Connection dbConn = null;
        String isValidateEmail ="N";
        try
        {

           dbConn = Database.getConnection();
           String query1 = "select isValidateEmail from members where memberid= ? ";
           PreparedStatement pstmt = dbConn.prepareStatement(query1);
           pstmt.setString(1, toid);
           ResultSet resultSet = pstmt.executeQuery();
            if(resultSet.next())
           {
               isValidateEmail = resultSet.getString("isValidateEmail");
           }


        }
        catch (Exception e)
        {
            logger.error("Error while validating customer email ", e);
            //throw new SystemError(e.getMessage());

        }
        finally
        {
            Database.closeConnection(dbConn);
        }

        if(isValidateEmail.equals("Y"))
        {
           return true;
        }


        return false;
    }


    public static boolean isEmailValid(String emailid, String fistSixCCNum, String lastFourCcNum,String boiledname)
    {
        Connection dbConn = null;
        String ccnum = ""+fistSixCCNum+ lastFourCcNum;
        String dbccnum = null;
        String dbboiledname = null;
        try
        {

           dbConn = Database.getConnection();
           String query1 = "select icicitransid,accountid,boiledname,concat(first_six,last_four) as dbccnum from bin_details where emailaddr= ? ";
           PreparedStatement pstmt = dbConn.prepareStatement(query1);
           pstmt.setString(1, emailid);
           ResultSet resultSet = pstmt.executeQuery();
           //If No Records found hence new customer
           if(!resultSet.next())
           {
               return true;
           }
            while (resultSet.next())
            {
                dbccnum = resultSet.getString("dbccnum");
                //if boiled name matched means it is either used card or new card from same customer
                if(boiledname.equals(dbboiledname))
                {
                    return true;
                }
                //this case is added  to handle where boiled name is not present or boiled name in DB was not correctly entered previously.
                if(ccnum.equals(dbccnum))
                {
                    return true;
                }

            }


        }
        catch (Exception e)
        {
            logger.error("Error while validating customer email ", e);
            //throw new SystemError(e.getMessage());

        }
        finally
        {
            Database.closeConnection(dbConn);
        }




        return false;
    }


}
