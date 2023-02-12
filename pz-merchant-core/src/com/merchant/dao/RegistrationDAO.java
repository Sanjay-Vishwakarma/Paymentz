package com.merchant.dao;

import com.directi.pg.*;
import com.manager.helper.TransactionHelper;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 6/6/2017.
 */
public class RegistrationDAO
{
    private static Logger log = new Logger(RegistrationDAO.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RegistrationDAO.class.getName());
    private static Functions functions = new Functions();
    public boolean isEmailAddressBlocked(String emailAddress) throws PZDBViolationException
    {
        boolean isEmailBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_email WHERE emailAddress=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, emailAddress);

            rs = p.executeQuery();
            if (rs.next())
            {
                isEmailBlocked = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isEmailAddressBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isEmailAddressBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isEmailBlocked;
    }

    public boolean isCountryBlocked(String countryCode, String telnocc) throws PZDBViolationException
    {
        boolean isCountryBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_country WHERE country_code=? OR telnocc=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, countryCode);
            p.setString(2, telnocc);

            rs = p.executeQuery();
            if (rs.next())
            {
                isCountryBlocked = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCountryBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCountryBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isCountryBlocked;
    }

    public boolean isNameBlocked(String name) throws PZDBViolationException
    {
        boolean isNameBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_name WHERE name=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, name);
            rs = p.executeQuery();
            if (rs.next())
            {
                isNameBlocked = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isNameBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isNameBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isNameBlocked;
    }
}
