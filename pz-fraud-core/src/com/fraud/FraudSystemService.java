package com.fraud;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.logicboxes.util.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/10/14
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudSystemService
{
    private static Logger logger = new Logger(FraudSystemService.class.getName());
    public static Hashtable<String, String> fraudSystem;
    static
    {
        try
        {
            loadFraudSystems();
        }
        catch (Exception e)
        {
            logger.error("Error while loading fraud System : " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    public static Hashtable<String, String> getFraudSystem()
    {
        return fraudSystem;
    }
    public static String getFSGateway(String fsid)
    {
        return fraudSystem.get(fsid);
    }
    public static boolean isMemberInTestMode(String memberId)
    {
        Connection conn = null;
        PreparedStatement psmt=null;
        ResultSet rs=null;
        String query="";
        boolean isTest=false;
        try
        {
            conn = Database.getConnection();
            query="select isTest from member_fraudsystem_mapping where memberid=?";
            psmt=conn.prepareStatement(query);
            psmt.setString(1,memberId);
            rs =psmt.executeQuery();
            if(rs.next())
            {
                if("Y".equals(rs.getString("isTest")))
                {
                    isTest=true;
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::"+e.getMessage());
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return isTest;
    }
    public static boolean isAPICallSupport(String fsId)
    {
        Connection conn = null;
        PreparedStatement psmt=null;
        ResultSet rs=null;
        String query="";
        boolean isAPI=false;
        try
        {
            conn = Database.getConnection();
            query="select * from fraudsystem_master where fsid=? and api='Y'";
            psmt=conn.prepareStatement(query);
            psmt.setString(1,fsId);
            rs =psmt.executeQuery();
            if(rs.next())
            {
                isAPI=true;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::"+e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isAPI;
    }
    public static void loadFraudSystems() throws Exception
    {
        logger.info("Loading Fraud System......");
        fraudSystem = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select fsid,fsname from fraudsystem_master",conn);
            while (rs.next())
            {
                fraudSystem.put(rs.getString("fsid"),rs.getString("fsname"));
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
}
