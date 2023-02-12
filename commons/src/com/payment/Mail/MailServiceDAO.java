package com.payment.Mail;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 8/5/15
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class MailServiceDAO
{
    static Logger log = new Logger(MailServiceDAO.class.getName());
    public void loadMailEntiy(HashMap<Integer,String> hMapMailEntity)
    {
        Connection con=null;
        try
        {
            con= Database.getConnection();
            String qry= new String("select * from mailentity");
            PreparedStatement preparedStatement=con.prepareStatement(qry);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                hMapMailEntity.put(resultSet.getInt("mailEntityId"),resultSet.getString("placeHolderTagName"));
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "loadMailEntiy()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "loadMailEntiy()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public void loadMailTemplates(HashMap<Integer,String> hMapMailTemplate)
    {
        Connection con=null;
        try
        {
            con= Database.getConnection();
            String qry =new String("SELECT * FROM mailtemplate");
            PreparedStatement preparedStatement= con.prepareStatement(qry);
            ResultSet resultSet= preparedStatement.executeQuery();
            while(resultSet.next())
            {
                hMapMailTemplate.put(resultSet.getInt("templateId"), resultSet.getString("templateFileName"));
            }
        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "loadMailTemplates()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO","loadMailTemplates()",null,"Common","SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),null,null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public Hashtable getSuperAdminvalue()
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection con=null;
        try
        {
            con = Database.getConnection();
            String query3 =new String("select * from partners where partnerId=1 limit 1");
            partnerMailDetail =Database.getHashFromResultSet( Database.executeQuery(query3,con));

        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getSuperAdminvalue()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getSuperAdminvalue()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);

        }
        finally
        {
            Database.closeConnection(con);
        }
        return partnerMailDetail;
    }
    public Hashtable getPartnersMerchantDetails(String toid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String qry=new String("SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=?)");
            PreparedStatement preparedStatement= con.prepareStatement(qry);
            preparedStatement.setString(1,toid);
            partnerMailDetail =Database.getHashFromResultSet(preparedStatement.executeQuery());

            if(partnerMailDetail.isEmpty())
            {
                String qry1=new String("SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM agents WHERE agentId=?)");
                PreparedStatement preparedStatement1= con.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                partnerMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getPartnersMerchantDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getPartnersMerchantDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);

        }
        finally
        {
            Database.closeConnection(con);
        }
        log.debug(partnerMailDetail);
        return partnerMailDetail;
    }
    public Hashtable getPartnerDetails(String toid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String qry=new String("SELECT * FROM partners WHERE partnerId=?");
            PreparedStatement preparedStatement= con.prepareStatement(qry);
            preparedStatement.setString(1,toid);
            partnerMailDetail =Database.getHashFromResultSet(preparedStatement.executeQuery());

            if(partnerMailDetail.isEmpty())
            {
                String qry1=new String("SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=?)");
                PreparedStatement preparedStatement1= con.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                partnerMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
            if(partnerMailDetail.isEmpty())
            {
                String qry1=new String("SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM agents WHERE agentId=?)");
                PreparedStatement preparedStatement1= con.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                partnerMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getPartnerDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getPartnerDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);

        }
        finally
        {
            Database.closeConnection(con);
        }
        log.debug(partnerMailDetail);
        return partnerMailDetail;
    }

    public Hashtable getAgentDetails(String toid)
    {
        Hashtable agentMailDetail=new Hashtable();
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String qry=new String("SELECT * FROM agents WHERE agentId=?");
            PreparedStatement preparedStatement= con.prepareStatement(qry);
            preparedStatement.setString(1,toid);
            agentMailDetail =Database.getHashFromResultSet(preparedStatement.executeQuery());

            if(agentMailDetail.isEmpty())
            {
                String qry1=new String("SELECT * FROM agents WHERE agentId=(SELECT agentId FROM members WHERE memberid=?)");
                PreparedStatement preparedStatement1= con.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                agentMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getAgentDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getAgentDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);

        }
        finally
        {
            Database.closeConnection(con);
        }

        return agentMailDetail;
    }

    public Hashtable getMerchantDetails(String memberid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String qry=new String("select * from members where memberid=?");
            PreparedStatement preparedStatement= con.prepareStatement(qry);
            preparedStatement.setString(1,memberid);
            partnerMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());

        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getMerchantDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getMerchantDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);

        }
        finally
        {
            Database.closeConnection(con);
        }
        return partnerMailDetail;
    }

    public Hashtable getSMSMappingDetails(MailEventEnum mailEventEnum)
    {
        Hashtable smsMappingDetails=new Hashtable();
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String qry= new String("SELECT mappingId,smsEventId,smsFromEntityId, smsToEntityId, smsTemplateId, smsSubject FROM mappingsmstemplateevententity WHERE smsEventId IN (SELECT mailEventId FROM mailevent WHERE mailEventName=?)");
            PreparedStatement preparedStatement=con.prepareStatement(qry);
            preparedStatement.setString(1,mailEventEnum.name());
            smsMappingDetails = Database.getHashFromResultSet(preparedStatement.executeQuery());

        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getSMSMappingDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getSMSMappingDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);

        }
        finally
        {
            Database.closeConnection(con);
        }
        return smsMappingDetails;
    }

    public String getCustReminderSMSFlag(String memberid)
    {
        String flag =null;
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String qry=new String("SELECT custremindersms FROM members WHERE memberid=?");
            PreparedStatement p=con.prepareStatement(qry);
            p.setString(1,memberid);
            ResultSet rs=p.executeQuery();
            if(rs.next())
            {
                flag=rs.getString("custremindersms");


            }
        }
        catch (SQLException e)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getCustReminderSMSFlag()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {

            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getCustReminderSMSFlag()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);

        }
        finally
        {
            Database.closeConnection(con);
        }
        return flag;
    }

    public List<MailMappingDetailsVO> getMailMappingDetails(MailEventEnum mailEventEnum)
    {
        Connection conn = null;
        MailMappingDetailsVO mappingDetailsVO = null;
        List<MailMappingDetailsVO> mailMappingDetailsVOList = new ArrayList();
        try
        {
            conn = Database.getRDBConnection();
            String qry = "SELECT mappingId,mailEventId,mailFromEntityId, mailToEntityId, mailTemplateId, mailSubject FROM mappingmailtemplateevententity WHERE mailEventId IN (SELECT mailEventId FROM mailevent WHERE mailEventName=?)";
            PreparedStatement preparedStatement = conn.prepareStatement(qry);
            preparedStatement.setString(1, mailEventEnum.name());
            log.debug("preparedStatement" + preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            //how many mail tobe send
            while (rs.next())
            {
                mappingDetailsVO = new MailMappingDetailsVO();
                mappingDetailsVO.setMappingId(rs.getInt("mappingId"));
                mappingDetailsVO.setMailEventId(rs.getInt("mailEventId"));
                mappingDetailsVO.setMailFromEntityId(rs.getInt("mailFromEntityId"));
                mappingDetailsVO.setMailToEntityId(rs.getInt("mailToEntityId"));
                mappingDetailsVO.setMailTemplateId(rs.getInt("mailTemplateId"));
                mappingDetailsVO.setMailSubject(rs.getString("mailSubject"));
                mailMappingDetailsVOList.add(mappingDetailsVO);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getMailMappingDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseAndHandleDBViolationException("MailServiceDAO", "getMailMappingDetails()", null, "Common", "SQL Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), null, null);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mailMappingDetailsVOList;
    }
}
