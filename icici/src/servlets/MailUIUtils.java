package servlets;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: NISHANT
 * Date: 5/20/14
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class MailUIUtils
{
    private static Logger logger = new Logger(MailUIUtils.class.getName());
    Connection connection = null;

    public Hashtable getMailEvents()
    {
        Hashtable mailEvents=new Hashtable();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT * FROM mailevent order by mailEventId DESC ";
            pstmt=connection.prepareStatement(qry);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                mailEvents.put(rs.getString("mailEventId"),rs.getString("mailEventName"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return mailEvents;
    }

    public Hashtable getMailTemplates()
    {
        Hashtable mailTemplate=new Hashtable();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT * FROM mailtemplate";
            pstmt=connection.prepareStatement(qry);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                mailTemplate.put(rs.getString("templateId"), rs.getString("templateName"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return mailTemplate;
    }
    public Hashtable getSMSTemplates()
    {
        Hashtable smsTemplate = new Hashtable();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getRDBConnection();
            String qry = "SELECT * FROM sms_template";
            pstmt = connection.prepareStatement(qry);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                smsTemplate.put(rs.getString("sms_template_id"), rs.getString("sms_template_name"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while fetching sms templates", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SqlException while fetching data from sms templates", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return smsTemplate;
    }

    public Hashtable getMailEntity()
    {
        Hashtable mailEntity=new Hashtable();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT * FROM mailentity";
            pstmt=connection.prepareStatement(qry);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                mailEntity.put(rs.getString("mailEntityId"), rs.getString("mailEntityName"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return mailEntity;
    }

    public String getEventNameFromID(String eventId)
    {
        String eventName="";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT * FROM mailevent where mailEventId=?";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,eventId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                eventName= rs.getString("mailEventName");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return eventName;
    }

    public String getEntityName(String entityId)
    {
        String entityName="";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT * FROM mailentity where mailEntityId=?";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,entityId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                entityName= rs.getString("mailEntityName");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return entityName;
    }

    public String getTemplateName(String templateId)
    {
        String templateName="";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT * FROM mailtemplate where templateId=?";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,templateId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                templateName= rs.getString("templateName");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return templateName;
    }

    public String getSmsTemplateName(String smsTemplateId)
    {
        String smsTemplateName="";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT * FROM sms_template where sms_template_id=?";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,smsTemplateId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                smsTemplateName= rs.getString("sms_template_name");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching sms events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from sms events",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return smsTemplateName;
    }

    public String deleteSMSMapping(String mappingId)
    {
        String msg="";
        PreparedStatement pstmt = null;

        try
        {
            connection= Database.getConnection();
            String qry="DELETE FROM sms_template_event_entity_mapping  WHERE mapping_id=?";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,mappingId);
            int i=pstmt.executeUpdate();
            if(i>0)
            {
                msg= "Successfully Deleted SMS Mapping ID::"+mappingId;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching sms events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from sms events",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return msg;
    }

    public String deleteMailMapping(String mappingId)
    {
        String msg="";
        PreparedStatement pstmt = null;
        try
        {
            connection= Database.getConnection();
            String qry="DELETE FROM mappingmailtemplateevententity  WHERE mappingId =?";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,mappingId);
            int i=pstmt.executeUpdate();
            if(i>0)
            {
                msg= "Successfully Deleted Mail Mapping ID::"+mappingId;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return msg;
    }

    public String addMailMapping(String eventid, String fromentityid, String toEntityId, String templateId, String subject)
    {
        String msg="";
        PreparedStatement pstmt = null;
        try
        {
            connection= Database.getConnection();
            String qry="INSERT INTO mappingmailtemplateevententity (mailEventId,mailFromEntityId,mailToEntityId,mailTemplateId,mailSubject) VALUES (?,?,?,?,?)";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,eventid);
            pstmt.setString(2,fromentityid);
            pstmt.setString(3,toEntityId);
            pstmt.setString(4,templateId);
            pstmt.setString(5,subject);
            int i=pstmt.executeUpdate();
            if(i>0)
            {
                msg= "Mail Mapping added successfully.";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching mail events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from mail events",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return msg;
    }
    public String addSmsMapping(String templateId, String eventId, String fromEntityId, String toEntityId,String subject)
    {
        String msg="";
        PreparedStatement pstmt = null;
        try
        {
            connection= Database.getConnection();
            String qry="INSERT INTO sms_template_event_entity_mapping (template_id,event_id,from_entity_id,to_entity_id,subject) VALUES (?,?,?,?,?)";
            pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,templateId);
            pstmt.setString(2,eventId);
            pstmt.setString(3,fromEntityId);
            pstmt.setString(4,toEntityId);
            pstmt.setString(5,subject);
            int i=pstmt.executeUpdate();
            if(i>0)
            {
                msg= "SMS Mapping added successfully.";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while fetching sms events",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while fetching data from sms events",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return msg;
    }
}
