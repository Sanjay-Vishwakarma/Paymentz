package com.directi.pg;

import org.apache.log4j.Category;

import java.sql.*;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class FinanceDatabase
{
    static Connection dbConn = null;
    static Category cat = Category.getInstance(Database.class.getName());
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.financedatabase");
    private static final String DB_DRIVER = RB.getString("DB_DRIVER");
    private static final String DB_URL = RB.getString("DB_URL");
    private static String USER = RB.getString("USER");
    private static String PASSWORD = RB.getString("PASSWORD");


    public static Connection getConnection() throws SystemError
    {
        cat.debug("begin of get conn");
        try
        {
            Class.forName(DB_DRIVER);
            if (USER == null)
                USER = "";
            if (PASSWORD == null)
                PASSWORD = "";

            dbConn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            //  		dbConn.setAutoCommit(false);
        }
        catch (SQLException ex)
        {
            String error = "Error Message : " + ex.getMessage() + "SQL State : " + ex.getSQLState() + "Error Code : " + ex.getErrorCode();
            cat.error(error,ex);

            throw new SystemError("Database Problem");
        }
        catch (Exception ex)
        {
            String error = "Error Message : " + ex.getMessage();
            cat.error(error,ex);
            throw new SystemError(ex.toString());
        }
        cat.debug("end of getconnection");

        return dbConn;
    }

    public static ResultSet executeQuery(String query, Connection conn) throws SystemError
    {
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        }
        catch (SQLException ex)
        {
            cat.error("SQL Exception : " + ex.getMessage() + "SQL State : " + ex.getSQLState() + "Error Code : " + ex.getErrorCode(),ex);
            throw new SystemError(ex.getMessage());
        }
        return rs;
    }

    public static int executeUpdate(String query, Connection conn) throws SystemError
    {
        Statement stmt = null;
        int result = -999999;
        try
        {
            stmt = conn.createStatement();
            result = stmt.executeUpdate(query);
        }
        catch (SQLException ex)
        {
            cat.error("SQL Exception : " + ex.getMessage() + "SQL State : " + ex.getSQLState() + "Error Code : " + ex.getErrorCode(),ex);
            throw new SystemError(ex.getMessage());
        }
        return result;
    }


    public static Hashtable getHashFromResultSet(ResultSet rs) throws SQLException
    {
        int j = 0;
        if (rs == null)
            throw new SQLException("Empty ResultSet in getHashFromResultSet as parameter");

        Hashtable outerHash = new Hashtable();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int count = rsMetaData.getColumnCount();

        int i = 0;
        while (rs.next())
        {
            Hashtable innerHash = new Hashtable();
            for (i = 1; i <= count; i++)
            {
                if (rs.getString(i) != null)
                {
                    innerHash.put(rsMetaData.getColumnLabel(i), rs.getString(i));
                }
            }
            j++;
            outerHash.put("" + j, innerHash);
        }
        return outerHash;
    }

    public static void closeConnection(Connection connection)
    {
        try
        {
            if (connection != null)
            {
                connection.close();
                connection = null;
            }
        }
        catch (Exception ex)
        {
            cat.error("Exception in closeConnection : ",ex);
        }
    }

    public static void commit(Connection conn)
    {
        try
        {
            conn.commit();
        }
        catch (SQLException ex)
        {
            cat.error("Exception in commit : ",ex);
        }
    }

    public static void rollback(Connection conn)
    {
        try
        {
            conn.rollback();
        }
        catch (SQLException ex)
        {
            cat.error("Exception in rollback : ",ex);
        }
    }

}
