package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.ActivityTrackerVOs;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 * Created by Admin on 9/28/2020.
 */
public class ActivityTrackerDAO
{
    Logger logger=new Logger(AccountDAO.class.getName());
    Functions functions = new Functions();

   public String CreateActivity(ActivityTrackerVOs activityTrackerVOs){

       String  status                       ="fail";
       Connection con                       = null;
       PreparedStatement preparedStatement  = null;
       try
       {
           con                  = Database.getConnection();
           String Query         = "INSERT INTO activity_tracker(Interface,user_name,role,action,module_name,lable_values,description,ip,header,partner_id) VALUES(?,?,?,?,?,?,?,?,?,?)";
           preparedStatement    = con.prepareStatement(Query);
           preparedStatement.setString(1,activityTrackerVOs.getInterface());
           preparedStatement.setString(2, activityTrackerVOs.getUser_name());
           preparedStatement.setString(3, activityTrackerVOs.getRole());
           preparedStatement.setString(4,activityTrackerVOs.getAction());
           preparedStatement.setString(5,activityTrackerVOs.getModule_name());
           preparedStatement.setString(6,activityTrackerVOs.getLable_values());
           preparedStatement.setString(7,activityTrackerVOs.getDescription());
           preparedStatement.setString(8,activityTrackerVOs.getIp());
           preparedStatement.setString(9,activityTrackerVOs.getHeader());
           preparedStatement.setString(10,activityTrackerVOs.getPartnerId());

           int k    = preparedStatement.executeUpdate();
           if(k==1)
           {
               status="success";
           }

       }
       catch (SQLException se)
       {
           logger.error("SQLException ::::::: Leaving transactionEntry ", se);
       }
       catch(SystemError e)
       {
           logger.error("SystemError ::::::: Leaving transactionEntry ", e);
       }
       finally
       {
           Database.closeConnection(con);
       }
       return status;
   }

    public Hashtable getActivityList(ActivityTrackerVOs activityTrackerVOs,int records, int pageno,String tdtstamp, String fdtstamp)
    {
        logger.debug("Entering getActivityList ");
        MySQLCodec me       = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename    = "";
        String fields       = "";
        String pRefund      = "false";
        StringBuffer query  = new StringBuffer();
        Hashtable hash      = null;
        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end             = records;
        Connection conn = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getRDBConnection();

            query.append("select id,interface,user_name,role,action,module_name,lable_values,description,ip,header,timestamp from activity_tracker where id>0 ");

            if (functions.isValueNull(activityTrackerVOs.getRole()))
            {
                query.append(" and role='" + ESAPI.encoder().encodeForSQL(me, activityTrackerVOs.getRole()) + "'");
            }

            if (functions.isValueNull(activityTrackerVOs.getUser_name()))
            {
                query.append(" and user_name like '%" + ESAPI.encoder().encodeForSQL(me,activityTrackerVOs.getUser_name()) + "%'");
            }
            if (functions.isValueNull(activityTrackerVOs.getModule_name()))
            {
                query.append(" and module_name like '%" + ESAPI.encoder().encodeForSQL(me,activityTrackerVOs.getModule_name()) + "%'");
            }

            if (functions.isValueNull(activityTrackerVOs.getPartnerId()))// for partner interface only
            {
                query.append(" and partner_id IN(" + activityTrackerVOs.getPartnerId() + ")");
            }

            if ( functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and TIMESTAMP >= '" + startDate + "'");
            }

            if (functions.isValueNull(tdtstamp))
            {

                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and TIMESTAMP <= '" + endDate + "'");
            }


            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            query.append(" order by TIMESTAMP DESC");
            query.append(" limit " + start + "," + end);

            logger.debug("===count query===" + countquery);
            logger.debug("===query==="+query);
            System.out.println("query"+query);

            hash            = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs    = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public TreeMap<String, String> getModuleName()
    {
        TreeMap<String, String> GetModuleName = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select Distinct module_name from activity_tracker";
            pstmt = con.prepareStatement(qry);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                GetModuleName.put(rs.getString("module_name"),rs.getString("module_name"));
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
        return GetModuleName;
    }

    public TreeMap<String, String> getRolelist()
    {
        TreeMap<String, String> GetModuleName = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String Role="";
        try
        {
            con = Database.getRDBConnection();
            String qry = "select Distinct role from activity_tracker ORDER BY role";
            pstmt = con.prepareStatement(qry);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if(rs.getString("role").equalsIgnoreCase("Admin")){
                    Role="Admin";
                }
                else if(rs.getString("role").equalsIgnoreCase("agent")){
                    Role="Agent";
                }
                else if(rs.getString("role").equalsIgnoreCase("partner")){
                    Role="Partner";
                }
                else if(rs.getString("role").equalsIgnoreCase("submerchant")){
                    Role="Submerchant";
                }
                else if(rs.getString("role").equalsIgnoreCase("superpartner")){
                    Role="Superpartner";
                }
                else if(rs.getString("role").equalsIgnoreCase("childsuperpartner")){
                    Role="Childsuperpartner";
                }
                else if(rs.getString("role").equalsIgnoreCase("merchant")){
                    Role="Merchant";
                }
                else if(rs.getString("role").equalsIgnoreCase("subpartner")){
                    Role="Subpartner";
                }
                GetModuleName.put(rs.getString("role"),Role);
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
        return GetModuleName;
    }


    public TreeMap<String, String> getModuleNameByInterface(String interfaceName)
    {
        TreeMap<String, String> GetModuleName   = new TreeMap<String, String>();
        Connection con                          = null;
        PreparedStatement pstmt                 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select Distinct module_name from activity_tracker where interface IN('"+interfaceName+"')";
            pstmt = con.prepareStatement(qry);
            //pstmt.setString(1,interfaceName);
            rs = pstmt.executeQuery();
            logger.debug("getModuleNameByInterface >> "+pstmt);
            while (rs.next())
            {
                GetModuleName.put(rs.getString("module_name"),rs.getString("module_name"));
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
        return GetModuleName;
    }
}