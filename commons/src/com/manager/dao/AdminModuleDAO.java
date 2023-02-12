package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.AdminModuleVO;
import com.manager.vo.AdminModulesMappingVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/9/15
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminModuleDAO
{
    private static Logger logger = new Logger(AdminModuleDAO.class.getName());

    public boolean isUniqueName(AdminModuleVO adminModuleVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean status=true;
        String query=null;

        try
        {
            conn = Database.getConnection();
            query = "select moduleid from admin_modules_master where modulename=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,adminModuleVO.getModuleName());
            resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                status=false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "isUniqueName()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "isUniqueName()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {

            Database.closeConnection(conn);
            logger.debug("Connection Closed");
        }
        return status;
    }
    public String addNewAdminModule(AdminModuleVO adminModuleVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String status="";
        String query=null;
        try
        {
            conn = Database.getConnection();
            query = "insert into admin_modules_master(moduleId,moduleName,modulecreationtime)values(null,?,unix_timestamp(now()))";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,adminModuleVO.getModuleName());
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "addNewAdminModule()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "addNewAdminModule()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }
    public Set<String> getAdminAccessModuleSet(String adminId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> set=new HashSet<String>();
        try
        {
            conn = Database.getConnection();
            query = "SELECT dmm.modulename FROM admin_modules_mapping AS amm JOIN admin_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE adminid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,adminId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("modulename"));
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "getAdminAccessModuleSet()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "getAdminAccessModuleSet()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return set;
    }
    public String addNewModuleMapping(AdminModulesMappingVO adminModulesMappingVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        String status="";
        try
        {
            conn = Database.getConnection();
            query = "insert into admin_modules_mapping(mappingid,adminid,moduleid)values(null,?,?)";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,adminModulesMappingVO.getAdminId());
            pstmt.setString(2,adminModulesMappingVO.getModuleId());
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "addNewModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "addNewModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }
    public boolean isMappingAvailable(AdminModulesMappingVO adminModulesMappingVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean status=false;
        String query=null;

        try
        {
            conn = Database.getConnection();
            query = "select moduleid from admin_modules_mapping where adminid=? and moduleid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1, adminModulesMappingVO.getAdminId());
            pstmt.setString(2, adminModulesMappingVO.getModuleId());
            resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }
    public boolean removeAdminModuleMapping(String mappingId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        try
        {
            conn = Database.getConnection();
            query = "delete from admin_modules_mapping where mappingid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1, mappingId);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "removeAdminModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "removeAdminModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }

    public static String getAdminName(String adminid)
    {
        String adminname="";
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT login FROM admin WHERE adminid=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, adminid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                adminname = rs.getString("login");
            }
        }
        catch (Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return adminname;
    }

    public static String getModuleName(String moduleid)
    {
        String modulename="";
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT modulename FROM admin_modules_master WHERE moduleid=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, moduleid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                modulename = rs.getString("modulename");
            }
        }
        catch (Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return modulename;
    }

    public boolean isCronMappingAvailable(String adminid)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean status=false;
        String query=null;

        try
        {
            conn = Database.getConnection();
            query = "select moduleid from admin_modules_mapping where moduleid=(SELECT moduleid FROM `admin_modules_master` WHERE modulename='CRON') AND adminId=?";

            pstmt= conn.prepareStatement(query);
            pstmt.setString(1, adminid);
            resultSet= pstmt.executeQuery();
            if(resultSet.next())
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }
}
